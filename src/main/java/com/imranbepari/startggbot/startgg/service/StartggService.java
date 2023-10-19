package com.imranbepari.startggbot.startgg.service;

import com.imranbepari.startggbot.startgg.model.StartggStandingsResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class StartggService {

    WebClient client;

    @PostConstruct
    void init() {
        client = WebClient.create("https://api.smash.gg");
    }

    /**
     * Get the current standings for a tournament and a particular event, up to a certain limit
     * @param tournamentSlug String formatted like `the-big-house-6`
     * @param eventSlug String formatted like `the-big-house-6`
     * @param standingLimit integer for how many players to get in the top standing ie 8 will return top 8 players
     * @return optional of the response, return null if 404 or other error, return response if successful
     */
    public Optional<StartggStandingsResponse> getStandings(String tournamentSlug, String eventSlug, int standingLimit) {

        return this.client.get()
                .uri("/tournament/{tournamentSlug}/event/{eventSlug}/standings?entityType=event&expand[]=entrants&page=1&per_page={standingLimit}", tournamentSlug, eventSlug, standingLimit)
                .retrieve()
                .bodyToMono(StartggStandingsResponse.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
                .blockOptional();
    }

}
