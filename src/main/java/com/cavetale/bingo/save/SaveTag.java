package com.cavetale.bingo.save;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public final class SaveTag {
    private Map<UUID, Integer> scores = new HashMap<>();
    private boolean event;
    private boolean pause;
    private long endTime;
    private boolean useItems = false;
    private boolean useBlocks = true;

    public void addScore(UUID uuid, int value) {
        int old = scores.getOrDefault(uuid, 0);
        scores.put(uuid, Math.max(0, old + value));
    }

    public int getScore(UUID uuid) {
        return scores.getOrDefault(uuid, 0);
    }
}
