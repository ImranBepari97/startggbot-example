package com.imranbepari.startggbot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "com.imranbepari.app")
public class AppConfiguration {

    //How many members to get the top standings for, 8 would return the top 8 players
    private int maxStandings;

    //How often to poll StartGG for standings updates in ms
    private int pollTime;
}