package um.edu.uy.jdftech.entities;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import um.edu.uy.jdftech.repositories.TestIntelliJRepository;

import um.edu.uy.jdftech.entities.TestIntelliJ;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "um.edu.uy.jdftech.repositories")
@EntityScan(basePackages = "um.edu.uy.jdftech.entities")
public class YourApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }

    @Bean
    CommandLineRunner testConnection(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✅ Database connected successfully!");
                System.out.println("Database: " + connection.getMetaData().getDatabaseProductName());
            }
        };
    }

    @Bean
    CommandLineRunner testDatabase(TestIntelliJRepository testRepository) {
        return args -> {
            System.out.println("🚀 Testing database connection...");

            // Create and save a test user
            TestIntelliJ testUser = new TestIntelliJ("John Doe", "john@example.com", 25);
            testRepository.save(testUser);
            System.out.println("✅ User saved: " + testUser);

            // Retrieve all users
            System.out.println("\n📋 All users in database:");
            testRepository.findAll().forEach(System.out::println);

            System.out.println("\n✅ Database test completed successfully!");
        };
    }
}
