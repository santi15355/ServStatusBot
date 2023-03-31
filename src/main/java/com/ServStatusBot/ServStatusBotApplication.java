package com.ServStatusBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServStatusBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServStatusBotApplication.class, args);
    }

}
