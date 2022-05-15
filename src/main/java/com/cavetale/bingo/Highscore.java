package com.cavetale.bingo;

import com.winthier.playercache.PlayerCache;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@RequiredArgsConstructor
public final class Highscore {
    public static final Highscore ZERO = new Highscore(new UUID(0L, 0L), 0);
    public final UUID uuid;
    public final int score;
    protected int placement;

    public Component name() {
        if (this == ZERO) return text("???", GRAY);
        Player player = Bukkit.getPlayer(uuid);
        return player != null
            ? player.displayName()
            : text(PlayerCache.nameForUuid(uuid), WHITE);
    }
}
