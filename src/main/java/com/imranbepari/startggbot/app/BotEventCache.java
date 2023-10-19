package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.startgg.model.Entrant;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class BotEventCache {
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

        return Optional.of(this.cachedStandings.get(info));
    }

    /**
     * Store the current standings for a tournament/event for later.
     * @param info Info to store
     * @param standings Current standings for given info, as List<Entrant>
     */
    public void putCachedStanding(EventInfo info, List<Entrant> standings) {
        this.cachedStandings.put(info, standings);
    }
}
