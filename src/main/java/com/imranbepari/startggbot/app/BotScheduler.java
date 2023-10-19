package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.configuration.AppConfiguration;
import com.imranbepari.startggbot.startgg.model.Entrant;
import com.imranbepari.startggbot.startgg.model.StartggStandingsResponse;
import com.imranbepari.startggbot.startgg.service.StartggService;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;

@EnableScheduling
@Service
public class BotScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotScheduler.class);

    private TaskScheduler taskScheduler;
    private StartggService startggService;
    private BotChannelRegistry botChannelRegistry;
    private BotEventCache botEventCache;
    private AppConfiguration appConfiguration;

    @Autowired
    public BotScheduler(StartggService startggService, BotChannelRegistry botChannelRegistry, BotEventCache botEventCache, AppConfiguration appConfiguration) {
        this.taskScheduler = getTaskScheduler();
        this.startggService = startggService;
        this.botChannelRegistry = botChannelRegistry;
        this.appConfiguration = appConfiguration;
        this.botEventCache = botEventCache;

        this.scheduleMessageUpdates();
    }

    public TaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("MessageUpdateThread");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    private void scheduleMessageUpdates() {
        taskScheduler.scheduleAtFixedRate(() -> updateChannels(), appConfiguration.getPollTime());
    }

    /**
     * Iterates through all the tournaments/events, and send an update message to each
     * Discord channel that is subscribed to it. This will also remove any tournaments/events from the map if
     * they are complete.
     */
    private void updateChannels() {
        Iterator<Map.Entry<EventInfo, List<MessageChannel>>> iterator = botChannelRegistry.getScheduledMessages().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<EventInfo, List<MessageChannel>> kvp = iterator.next();

            EventInfo currentInfo = kvp.getKey();

            StartggStandingsResponse newStandingResponse = null;

            try {
                getStandingsFor(currentInfo).orElseThrow();

                //Turns out entrants list from the response is already an ordered list by standing, so we can
                //just set that as our standing list
                List<Entrant> newStanding = newStandingResponse.getItems().getEntities().getEntrants();

                List<Entrant> oldStanding = botEventCache.getCachedStanding(currentInfo).orElse(Collections.emptyList());

                if (oldStanding.equals(newStanding)) {
                    //The standings are the same, we don't need to update any channels
                    return;
                }

                botEventCache.putCachedStanding(currentInfo, newStanding);

                //Otherwise let's post an update to each channel subscribed to this!
                kvp.getValue().parallelStream().forEach(channel -> {
                    sendStandingsUpdate(channel, currentInfo);
                });

                //If all the final placements have been set, then we can remove this from the scheduling map
                if (newStanding.parallelStream().allMatch(entrant -> entrant.finalPlacement() != null)) {
                    kvp.getValue().parallelStream().forEach(channel -> {
                        channel.createMessage(String.format("Tournament %s event % has completed! You will no longer receive updates!",
                                currentInfo.getTournamentSlug(), currentInfo.getEventSlug()));
                    });
                    iterator.remove();
                    return;
                }

            } catch (NoSuchElementException e) {
                LOGGER.error("Scheduled update tried to GET a tournament/event that does not / no longer exists, removing from scheduler.");
                iterator.remove();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Little wrapper for the service as this class usually operates using EventInfo's,
     * but the API accepts the components of it separately.
     *
     * @param info the event to get the standings for
     * @return the response containing the current standings, or null if the response is invalid
     */
    public Optional<StartggStandingsResponse> getStandingsFor(EventInfo info) {
        return startggService.getStandings(info.getTournamentSlug(), info.getEventSlug(), appConfiguration.getMaxStandings());
    }


    /**
     * Send a message to a channel containing the standings for a particular event.
     *
     * @param channel Discord text channel to send message to.
     * @param info The event to send the standings of.
     */
    public void sendStandingsUpdate(MessageChannel channel, EventInfo info) {
        channel.getRestChannel().createMessage(generateUpdateMessageFrom(info)).subscribe();
    }

    /**
     * Generates an update message to post to Discord.
     * <p>
     * Will emit the format of
     * <p>
     * ```
     * Current Standings for eventSlug at tournamentSlug is:
     * 1. Player1
     * 2. Player2.
     * ...
     * n. PlayerN
     * ```
     * <p>
     * This method expects the value to be cached, but will grab the info via a blocking request if necessary.
     *
     * @param info the event info to generate a message from
     * @return formatted message to be sent directly to discord
     */
    public String generateUpdateMessageFrom(EventInfo info) {
        List<Entrant> standings;

        //Try and get the current entrant info from cache, and manually get it
        //from API if not present
        if (botEventCache.getCachedStanding(info).isPresent()) {
            standings = botEventCache.getCachedStanding(info).get();
        } else {
            standings = getStandingsFor(info).orElseThrow().getItems().getEntities().getEntrants();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Current Standings for %s at %s is: \n", info.getEventSlug(), info.getTournamentSlug()));

        for (int i = 0; i < standings.size(); i++) {
            builder.append((i + 1) + ". " + standings.get(i).name() + "\n");
        }
        return builder.toString();
    }
}
