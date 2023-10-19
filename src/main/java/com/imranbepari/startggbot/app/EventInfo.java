package com.imranbepari.startggbot.app;

import com.imranbepari.startggbot.startgg.model.Entrant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class EventInfo {

    private String tournamentSlug;
    private String eventSlug;

    public EventInfo(String tournamentSlug, String eventSlug) {
        this.tournamentSlug = tournamentSlug;
        this.eventSlug = eventSlug;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof EventInfo)) {
            return false;
        }
        EventInfo eventInfo = (EventInfo) other;
        if (eventInfo.tournamentSlug.equals(this.tournamentSlug) && eventInfo.eventSlug.equals(this.eventSlug)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournamentSlug, eventSlug);
    }

}
