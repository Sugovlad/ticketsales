package com.sugo.ticketsales;

import config.CustomConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import repository.OrderRepository;


@EntityScan(basePackages = "entity")
@Slf4j
@ConfigurationPropertiesScan(basePackageClasses = CustomConfig.class)
@ComponentScan(basePackages = {"controllers", "service"})
@EnableJpaRepositories(basePackageClasses = OrderRepository.class)
@SpringBootApplication
public class TicketSalesApplication {
    public static void main(String... args) {
        SpringApplication.run(TicketSalesApplication.class, args);
    }
}
