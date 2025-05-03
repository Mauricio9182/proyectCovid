package org.parcial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.parcial.db.dao")
@EntityScan(basePackages = "org.parcial.db.model")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
