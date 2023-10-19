package com.imranbepari.startggbot.discord.listeners;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * The key interface for listening to Discord events. These range from receiving messages, to simply going online.
 * @param <T>
 */
public interface EventListener<T extends Event> {

    Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    Class<T> getEventType();
    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        LOGGER.error("Unable to process " + getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}