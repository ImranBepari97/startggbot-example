package com.imranbepari.startggbot.startgg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
public record Standing(int id, int groupId, String entityType, int entityId, int standing, boolean isTied,
                       boolean isFinal, int createdAt, int updatedAt, ArrayList<Object> metadata, boolean isShim,
                       String containerType, int containerId, int points, int highestPoints,
                       ArrayList<Integer> pointIds, Object pointContributions) {
}