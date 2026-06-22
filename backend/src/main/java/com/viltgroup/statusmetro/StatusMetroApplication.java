package com.viltgroup.statusmetro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Status Metrô backend — a REST API that serves the current
 * São Paulo Metrô and CPTM line status, polled and normalized from the public CCR source.
 */
@SpringBootApplication
@EnableScheduling
public class StatusMetroApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatusMetroApplication.class, args);
    }
}
