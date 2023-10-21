package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.startgg.model.Entrant;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class BotEventCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotEventCache.class);
    private ConcurrentHashMap<EventInfo, List<Entrant>> cachedStandings;

    public BotEventCache() {
        this.cachedStandings = new ConcurrentHashMap<>();
    }

    /**
     * Return the cached entrants value for an event, if there is a cached list available.
     * @param info the event to try and get the info for.
     * @return optional of the cached entrants list.
     */
    public Optional<List<Entrant>> getCachedStanding(EventInfo info) {
        if(!this.cachedStandings.containsKey(info)) return Optional.empty();

        List<Entrant> result = this.cachedStandings.get(info);

        LOGGER.trace("Retrieved event info: {}/{}, standings: {} from BotEventCache", info.getTournamentSlug(), info.getEventSlug(), result);

        return Optional.of(result);
    }

    /**
     * Store the current standings for a tournament/event for later.
     * @param info Info to store
     * @param standings Current standings for given info, as List<Entrant>
     */
    public void putCachedStanding(EventInfo info, List<Entrant> standings) {
        LOGGER.trace("Putting new event info standings: {}/{}, standings: {} into BotEventCache", info.getTournamentSlug(), info.getEventSlug(), standings);
        this.cachedStandings.put(info, standings);
    }
}
