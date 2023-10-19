package com.imranbepari.startggbot.discord;

import com.imranbepari.startggbot.app.BotChannelRegistry;
import com.imranbepari.startggbot.discord.listeners.UnregisterTournamentMessageListener;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UnregisterTournamentMessageListenerTest {

    @Mock
    private BotChannelRegistry botChannelRegistry;

    @Mock
    private Message message;

    @Mock
    private MessageChannel messageChannel;

    private UnregisterTournamentMessageListener unregisterTournamentMessageListener;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        unregisterTournamentMessageListener = new UnregisterTournamentMessageListener(botChannelRegistry);
    }

    @Test
    public void testValidateMessage() {
        when(message.getContent()).thenReturn("!unsubscribe tournament1 event1");
        Assertions.assertTrue(unregisterTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!unsubscribe tournament1 event_1");
        Assertions.assertFalse(unregisterTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!unsubscribe tournament-1 event-1");
        Assertions.assertTrue(unregisterTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!unsubscribe tournament! event@1");
        Assertions.assertFalse(unregisterTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!unsubscribe tournament1");
        Assertions.assertFalse(unregisterTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!usubscrib tournament1 event1");
        Assertions.assertFalse(unregisterTournamentMessageListener.validateMessage(message));
    }

    @Test
    public void testDoOnMessage() {
        when(message.getContent()).thenReturn("!unsubscribe tournament1 event1");
        when(message.getChannel()).thenReturn(Mono.just(messageChannel));

        when(messageChannel.createMessage(anyString())).thenReturn(null);

        when(botChannelRegistry.unsubscribeChannel("tournament1", "event1", messageChannel)).thenReturn(true);
        unregisterTournamentMessageListener.doOnMessage(message);
        verify(messageChannel, times(1)).createMessage("Unsubscribed from event successfully!");

        when(message.getContent()).thenReturn("!unsubscribe tournament2 event2");

        when(botChannelRegistry.unsubscribeChannel("tournament2", "event2", messageChannel)).thenReturn(false);
        unregisterTournamentMessageListener.doOnMessage(message);
        verify(messageChannel, times(1)).createMessage("That isn't a valid tournament/event, or you aren't subscribed to that anyway!");
    }
}