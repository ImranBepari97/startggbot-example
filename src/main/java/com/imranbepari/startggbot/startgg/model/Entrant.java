package com.imranbepari.startggbot.startgg.model;

import java.util.ArrayList;
import java.util.Objects;

public record Entrant(Integer id, Integer eventId, ArrayList<Integer> participantIds, Integer participant1Id, Integer participant2Id,
                      String name, Integer finalPlacement, Integer defaultSkill, Integer skill, Integer skillOrder, boolean unverified,
                      boolean isDisqualified, boolean isPlaceholder) {

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Entrant)) {
            return false;
        }
        Entrant entrant = (Entrant) other;
        if (entrant.id == this.id ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

}
