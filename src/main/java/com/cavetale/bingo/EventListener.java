package com.cavetale.bingo;

import com.cavetale.core.font.VanillaItems;
import com.cavetale.sidebar.PlayerSidebarEvent;
import com.cavetale.sidebar.Priority;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class EventListener implements Listener {
    private final BingoPlugin plugin;

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onPlayerQuit(PlayerQuitEvent event) {
    }

    @EventHandler
    void onPlayerSidebar(PlayerSidebarEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("bingo.bingo")) return;
        if (plugin.tag.completed.containsKey(player.getUniqueId())) return;
        List<Component> list = new ArrayList<>();
        list.add(Component.text("/bingo", NamedTextColor.BLUE));
        // Tag.Player playerTag = plugin.getPlayerTag(player);
        // for (int row = 0; row < 5; row += 1) {
        //     TextComponent.Builder line = Component.text();
        //     for (int col = 0; col < 5; col += 1) {
        //         Material material = playerTag.materialList.get(row * 5 + col);
        //         line.append(VanillaItems.componentOf(material));
        //     }
        //     list.add(line.build());
        // }
        event.add(plugin, Priority.HIGH, list);
    }
}
