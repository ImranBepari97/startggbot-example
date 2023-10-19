package com.imranbepari.startggbot.discord.configuration;

import com.imranbepari.startggbot.discord.listeners.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "com.imranbepari.discord")
public class DiscordConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfiguration.class);

    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
//        LOGGER.info("Starting bot with token: {}", token);
        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        long applicationId = client.getRestClient().getApplicationId().block();

        //Register any listeners that we create
        eventListeners.forEach(listener -> {
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        });

        client.onDisconnect().block();

        return client;
    }
}
