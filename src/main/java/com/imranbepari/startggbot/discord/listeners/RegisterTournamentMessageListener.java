package com.imranbepari.startggbot.discord.listeners;

import com.imranbepari.startggbot.app.BotChannelRegistry;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterTournamentMessageListener extends MessageListener {

    private BotChannelRegistry botChannelRegistry;

    @Autowired
    public RegisterTournamentMessageListener(BotChannelRegistry botChannelRegistry) {
        this.botChannelRegistry = botChannelRegistry;
    }

    /**
     * Ensure that the message we get conforms to "!register tournament/anyString/event/anyString"!
     * @param message The message to validate
     * @return Whether the message conforms to the register tournament conditions
     */
    @Override
    public boolean validateMessage(Message message) {
        LOGGER.debug("Register validating message: {}", message.getContent());
        String[] split = message.getContent().split(" ");

        if(split.length != 3) return false;

        if(!split[0].equals("!subscribe")) return false;

        if(!split[1].matches("([a-zA-Z0-9]*-*)*")) return false;

        if(!split[2].matches("([a-zA-Z0-9]*-*)*")) return false;

        return true;
    }

    @Override
    public void doOnMessage(Message message) {
        String[] split = message.getContent().split(" ");

        //Register the channel and event with the StartGGPoller
        message.getChannel()
                .doOnNext(channel -> {
                    if(botChannelRegistry.subscribeChannel(split[1], split[2], channel)) {
                        channel.createMessage("Subscribed to event successfully!");
                    } else {
                        channel.createMessage("That isn't a valid tournament/event!");
                    }
                })
                .subscribe();
    }
}