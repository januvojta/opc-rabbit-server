package cz.januvojt.opcrabbitserver.configs;

import cz.januvojt.opcrabbitserver.KeyStoreLoader;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfigBuilder;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Set;

@Configuration
public class OpcUaConfig {

    @Value("${opcua.server.address}")
    private String opcUaServerAddress;
    @Value("${opcua.server.port}")
    private int opcUaServerPort;
    @Value("${opcua.application.name}")
    private String appName;
    @Value("${opcua.application.uri}")
    private String appUri;

    @Bean
    public OpcUaServerConfig opcUaServerConfig() throws Exception {
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "server", "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security temp dir: " + securityTempDir);
        }

        File pkiDir = securityTempDir.resolve("pki").toFile();

        KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);


        DefaultCertificateManager certificateManager = new DefaultCertificateManager(
                loader.getServerKeyPair(),
                loader.getServerCertificateChain()
        );

        DefaultTrustListManager trustListManager = new DefaultTrustListManager(pkiDir);

        DefaultServerCertificateValidator certificateValidator =
                new DefaultServerCertificateValidator(trustListManager);

        X509Certificate certificate = certificateManager.getCertificates()
                .stream()
                .findFirst()
                .orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError, "no certificate found"));

        Set<EndpointConfiguration> endpointsConfiguration = Set.of(new EndpointConfiguration.Builder()
                .addTokenPolicy(OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME)
                .setSecurityPolicy(SecurityPolicy.None)
                .setBindAddress(opcUaServerAddress)
                .setBindPort(opcUaServerPort)
                .setCertificate(certificate)
                .build());

        return new OpcUaServerConfigBuilder()
                .setEndpoints(endpointsConfiguration)
                .setApplicationName(LocalizedText.english(appName))
                .setApplicationUri(appUri)
                .setIdentityValidator(new UsernameIdentityValidator(false, credentials ->
                        credentials.getUsername().equals("admin") && credentials.getPassword().equals("Montrac2020")))
                .setCertificateManager(certificateManager)
                .setCertificateValidator(certificateValidator)
                .build();
    }

    @Bean
    public OpcUaServer opcUaServer(OpcUaServerConfig serverConfig) {
        return new OpcUaServer(serverConfig);
    }
}
