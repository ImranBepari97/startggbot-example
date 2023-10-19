package com.imranbepari.startggbot.discord.listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

/**
 * Abstract MessageListener class.
 *
 * This provides an easy template, where any developer could implement commands by providing:
 *  - conditions to execute command via `validateMessage()`
 *  - command instructions  via `doOnMessage()`
 */
public abstract class MessageListener implements EventListener<MessageCreateEvent> {

    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> doesRespondToBot() ? true : message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(this::validateMessage)
                .doOnNext(this::doOnMessage)
                .then();
    }

    /**
     * Whether this MessageListener will receive events from other bots. This can be overridden if necessary to respond to certain bots or
     * certain conditions.
     *
     * Defaults to false.
     *
     * @return whether this MessageListener will receive events from other bots
     */
    public boolean doesRespondToBot() {
        return false;
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }

    /**
     * Method used to validate whether the main function of this MessageListener should happen.
     * Examples of this include checking message contents for a prefix for a command, or that it conforms to a value.
     *
     * @param message
     * @return
     */
    public boolean validateMessage(Message message) {
        return true;
    }

    /**
     * The code block to execute if the message successfully meets the listener conditions.
     * @param message
     */
    public abstract void doOnMessage(Message message);
}