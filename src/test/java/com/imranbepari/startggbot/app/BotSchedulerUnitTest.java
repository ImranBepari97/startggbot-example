package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.app.BotChannelRegistry;
import com.imranbepari.startggbot.app.BotEventCache;
import com.imranbepari.startggbot.app.BotScheduler;
import com.imranbepari.startggbot.app.EventInfo;
import com.imranbepari.startggbot.configuration.AppConfiguration;
import com.imranbepari.startggbot.startgg.model.Entrant;
import com.imranbepari.startggbot.startgg.service.StartggService;
import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.imranbepari.startggbot.TestUtils.createEntrant;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class BotSchedulerUnitTest {
    @Mock
    private StartggService startggService;

    @Mock
    private BotChannelRegistry botChannelRegistry;

    @Mock
    private BotEventCache botEventCache;

    private AppConfiguration appConfiguration;

    private BotScheduler botScheduler;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);

        appConfiguration = new AppConfiguration();
        appConfiguration.setMaxStandings(8);
        appConfiguration.setPollTime(5000);

        botScheduler = new BotScheduler(startggService, botChannelRegistry, botEventCache, appConfiguration);
    }

    @Test
    public void testGetStandingsFor() {
        EventInfo eventInfo = new EventInfo("tournament1", "event1");
        when(startggService.getStandings("tournament1", "event1", 8)).thenReturn(Optional.empty());
        Assertions.assertEquals(Optional.empty(), botScheduler.getStandingsFor(eventInfo));
    }

    @Test
    public void testGenerateUpdateMessageFrom() {
        EventInfo eventInfo = new EventInfo("tournament1", "event1");
        List<Entrant> entrants = List.of(createEntrant(1, "Player1"), createEntrant(2, "Player2"), createEntrant(3, "Player3"));
        when(botEventCache.getCachedStanding(eventInfo)).thenReturn(Optional.of(entrants));
        String expectedMessage = "Current Standings for event1 at tournament1 is: \n1. Player1\n2. Player2\n3. Player3\n";
        Assertions.assertEquals(expectedMessage, botScheduler.generateUpdateMessageFrom(eventInfo));
    }
}
