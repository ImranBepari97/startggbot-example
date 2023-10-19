package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.startgg.model.Entrant;
import com.imranbepari.startggbot.startgg.model.StartggStandingsResponse;
import com.imranbepari.startggbot.startgg.service.StartggService;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class BotChannelRegistry {

    private ConcurrentHashMap<EventInfo, List<MessageChannel>> scheduledMessages;
    private StartggService startggService;

    public BotChannelRegistry(StartggService startggService) {
        this.scheduledMessages = new ConcurrentHashMap<>();
        this.startggService = startggService;
    }


    /**
     * Add a channel to the registry.
     * @param tournamentSlug the tournament slug
     * @param eventSlug the event slug
     * @param channel the channel to subscribe to
     * @return whether subscribe was successful
     */
    public boolean subscribeChannel(String tournamentSlug, String eventSlug, MessageChannel channel) {
        EventInfo info = new EventInfo(tournamentSlug, eventSlug);

        Optional<StartggStandingsResponse> startggInitialResponse = startggService.getStandings(tournamentSlug, eventSlug, 8);

        if(!startggInitialResponse.isPresent()) {
            return false;
        }

        if(scheduledMessages.containsKey(info)) {
            scheduledMessages.get(info).add(channel);
            return true;
        } else {
            ArrayList<MessageChannel> channelList = new ArrayList<>();
            channelList.add(channel);
            scheduledMessages.put(info, channelList);
        }

        return true;
    }

    /**
     * Remove a channel from the registry. If there are no channels left subbed to an event, then remove the whole event
     * from the registry
     * @param tournamentSlug the tournament slug
     * @param eventSlug the event slug
     * @param channel the channel to unsubscribe from
     * @return whether unsubscribe was successful
     */
    public boolean unsubscribeChannel(String tournamentSlug, String eventSlug, MessageChannel channel) {

        EventInfo info = new EventInfo(tournamentSlug, eventSlug);

        if(!scheduledMessages.containsKey(info)) {
            return false;
        }

        List<MessageChannel> channelList = scheduledMessages.get(info);

        //Remove the channel from the subscribed list
        channelList.remove(channel);

        //If there's no more channels that are subscribed to, then remove it entirely
        if(channelList.isEmpty()) scheduledMessages.remove(info);

        return true;
    }
}
