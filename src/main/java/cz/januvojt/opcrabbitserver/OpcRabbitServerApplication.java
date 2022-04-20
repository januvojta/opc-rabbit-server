package cz.januvojt.opcrabbitserver;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpcRabbitServerApplication implements ApplicationRunner {

    private final NavServerServer server;

    public OpcRabbitServerApplication(NavServerServer server) {
        this.server = server;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpcRabbitServerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        server.startup().get();
    }
}
