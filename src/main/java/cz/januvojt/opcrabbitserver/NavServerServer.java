package cz.januvojt.opcrabbitserver;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class NavServerServer {
    private final OpcUaServer server;
    private final ManagedNamespaceWithLifecycle namespace;

    public NavServerServer( OpcUaServer server, ManagedNamespaceWithLifecycle namespace) {
        this.namespace = namespace;
        this.server = server;
    }

    public OpcUaServer getServer() {
        return server;
    }

    public ManagedNamespaceWithLifecycle getNamespace() {
        return namespace;
    }

    public CompletableFuture<OpcUaServer> startup(){
        namespace.startup();
        return server.startup();
    }

    public CompletableFuture<OpcUaServer> shutdown(){
        namespace.shutdown();
        return server.shutdown();
    }
}
