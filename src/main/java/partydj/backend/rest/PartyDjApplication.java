package partydj.backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import partydj.backend.rest.service.TestDataGenerator;

@SpringBootApplication
public class PartyDjApplication {

    @Autowired
    private TestDataGenerator testDataGenerator;

    public static void main(String[] args) {
        SpringApplication.run(PartyDjApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            testDataGenerator.createTestData();
        };
    }

}
