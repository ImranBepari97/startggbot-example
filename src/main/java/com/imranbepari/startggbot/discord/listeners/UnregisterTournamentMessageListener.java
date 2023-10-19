package com.imranbepari.startggbot.discord.listeners;

import com.imranbepari.startggbot.app.BotChannelRegistry;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnregisterTournamentMessageListener extends MessageListener {

    private BotChannelRegistry botChannelRegistry;

    @Autowired
    public UnregisterTournamentMessageListener(BotChannelRegistry botChannelRegistry) {
        this.botChannelRegistry = botChannelRegistry;
    }

    /**
     * Ensure that the message we get conforms to "!unregister tournament/anyString/event/anyString"!
     * @param message The message to validate
     * @return Whether the message conforms to the unregister tournament conditions
     */
    @Override
    public boolean validateMessage(Message message) {
        LOGGER.debug("Unregister validating message: {}", message.getContent());
        String[] split = message.getContent().split(" ");

        if(split.length != 3) return false;

        if(!split[0].equals("!unsubscribe")) return false;

        if(!split[1].matches("([a-zA-Z0-9]*-*)*")) return false;

        if(!split[2].matches("([a-zA-Z0-9]*-*)*")) return false;

        return true;
    }

    @Override
    public void doOnMessage(Message message) {
        String[] split = message.getContent().split(" ");

        message.getChannel()
                .doOnNext(channel -> {
                    if(botChannelRegistry.unsubscribeChannel(split[1], split[2], channel)) {
                        channel.createMessage("Unsubscribed from event successfully!");
                    } else {
                        channel.createMessage("That isn't a valid tournament/event, or you aren't subscribed to that anyway!");
                    }
                })
                .subscribe();
    }
}