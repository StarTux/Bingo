package com.cavetale.bingo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;

public final class Tag {
    Map<UUID, String> completed = new HashMap<>();
    Map<UUID, Player> players = new HashMap<>();

    public static final class Player {
        List<Material> materialList = new ArrayList<>();
    }
}
