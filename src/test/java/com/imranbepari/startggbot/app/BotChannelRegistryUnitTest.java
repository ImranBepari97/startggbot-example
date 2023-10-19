package com.imranbepari.startggbot.app;


import com.imranbepari.startggbot.app.BotChannelRegistry;
import com.imranbepari.startggbot.app.BotEventCache;
import com.imranbepari.startggbot.app.EventInfo;
import com.imranbepari.startggbot.startgg.model.Entrant;
import com.imranbepari.startggbot.startgg.model.StartggStandingsResponse;
import com.imranbepari.startggbot.startgg.service.StartggService;
import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class BotChannelRegistryUnitTest {
    @Mock
    private StartggService startggService;

    @Mock
    private MessageChannel messageChannel;

    private BotChannelRegistry botChannelRegistry;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        botChannelRegistry = new BotChannelRegistry(startggService);
    }

    @Test
    public void testSubscribeChannel() {
        String tournamentSlug = "tournament1";
        String eventSlug = "event1";

        when(startggService.getStandings(tournamentSlug, eventSlug, 8)).thenReturn(Optional.empty());
        Assertions.assertFalse(botChannelRegistry.subscribeChannel(tournamentSlug, eventSlug, messageChannel));

        when(startggService.getStandings(tournamentSlug, eventSlug, 8)).thenReturn(Optional.empty());
        Assertions.assertFalse(botChannelRegistry.subscribeChannel(tournamentSlug, eventSlug, messageChannel));

        when(startggService.getStandings(tournamentSlug, eventSlug, 8)).thenReturn(Optional.of(new StartggStandingsResponse()));
        Assertions.assertTrue(botChannelRegistry.subscribeChannel(tournamentSlug, eventSlug, messageChannel));
    }

    @Test
    public void testUnsubscribeChannel() {
        String tournamentSlug = "tournament1";
        String eventSlug = "event1";

        when(startggService.getStandings(tournamentSlug, eventSlug, 8)).thenReturn(Optional.of(new StartggStandingsResponse()));
        Assertions.assertTrue(botChannelRegistry.subscribeChannel(tournamentSlug, eventSlug, messageChannel));

        Assertions.assertTrue(botChannelRegistry.unsubscribeChannel(tournamentSlug, eventSlug, messageChannel));
        Assertions.assertFalse(botChannelRegistry.unsubscribeChannel("nonexistentTournament", "nonexistentEvent", messageChannel));
    }

}
