package com.example.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReaderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReaderServiceApplication.class, args);
    }
}
