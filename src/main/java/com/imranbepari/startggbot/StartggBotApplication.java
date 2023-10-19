package com.imranbepari.startggbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("!test")
@SpringBootApplication
@EnableScheduling
public class StartggBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartggBotApplication.class, args);
    }

}
