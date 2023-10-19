package com.imranbepari.startggbot.discord;

import com.imranbepari.startggbot.app.BotChannelRegistry;
import com.imranbepari.startggbot.discord.listeners.RegisterTournamentMessageListener;
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
public class RegisterTournamentMessageListenerTest {

    @Mock
    private BotChannelRegistry botChannelRegistry;

    @Mock
    private Message message;

    @Mock
    private MessageChannel messageChannel;

    private RegisterTournamentMessageListener registerTournamentMessageListener;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        registerTournamentMessageListener = new RegisterTournamentMessageListener(botChannelRegistry);
    }

    @Test
    public void testValidateMessage() {
        when(message.getContent()).thenReturn("!subscribe tournament1 event1");
        Assertions.assertTrue(registerTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!subscribe tournament1 event_1");
        Assertions.assertFalse(registerTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!subscribe tournament-1 event-1");
        Assertions.assertTrue(registerTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!subscribe tournament! event@1");
        Assertions.assertFalse(registerTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!subscribe tournament1");
        Assertions.assertFalse(registerTournamentMessageListener.validateMessage(message));
        when(message.getContent()).thenReturn("!subscrib tournament1 event1");
        Assertions.assertFalse(registerTournamentMessageListener.validateMessage(message));
    }

    @Test
    public void testDoOnMessage() {
        when(message.getContent()).thenReturn("!subscribe tournament1 event1");
        when(message.getChannel()).thenReturn(Mono.just(messageChannel));

        when(messageChannel.createMessage(anyString())).thenReturn(null);

        when(botChannelRegistry.subscribeChannel("tournament1", "event1", messageChannel)).thenReturn(false);
        registerTournamentMessageListener.doOnMessage(message);
        verify(messageChannel, times(1)).createMessage("That isn't a valid tournament/event!");

        when(message.getContent()).thenReturn("!subscribe tournament2 event2");

        when(botChannelRegistry.subscribeChannel("tournament2", "event2", messageChannel)).thenReturn(true);
        registerTournamentMessageListener.doOnMessage(message);
        verify(messageChannel, times(1)).createMessage("Subscribed to event successfully!");
    }
}