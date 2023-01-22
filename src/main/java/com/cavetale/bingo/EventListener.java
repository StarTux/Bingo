package com.cavetale.bingo;

import com.cavetale.bingo.save.PlayerTag;
import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import com.cavetale.core.item.ItemKinds;
import com.cavetale.dungeons.DungeonLootEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;

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
        plugin.playerTagMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onPlayerHud(PlayerHudEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("bingo.bingo")) return;
        PlayerTag playerTag = plugin.getPlayerTag(player);
        List<Component> lines = new ArrayList<>();
        lines.add(join(noSeparators(), text("/", YELLOW), plugin.BINGO));
        lines.add(plugin.getSubtitle(playerTag));
        if (plugin.saveTag.isEvent()) {
            lines.addAll(plugin.highscoreLines);
        }
        if (lines.isEmpty()) return;
        event.sidebar(PlayerHudPriority.HIGHEST, lines);
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        switch (event.getEntityType()) {
        case ENDERMAN:
            if (event.getDrops().isEmpty()) {
                event.getDrops().add(new ItemStack(Material.ENDER_PEARL));
            }
            break;
        case MAGMA_CUBE:
            if (event.getDrops().isEmpty()) {
                event.getDrops().add(new ItemStack(Material.MAGMA_CREAM));
            }
            break;
        case BLAZE:
            if (event.getDrops().isEmpty()) {
                event.getDrops().add(new ItemStack(Material.BLAZE_ROD));
            }
            break;
        default: break;
        }
    }

    @EventHandler
    private void onDungeonLoot(DungeonLootEvent event) {
        Player player = event.getPlayer();
        List<Material> materialList = plugin.getPlayerTag(player).getMaterialList();
        Material material = materialList.get(plugin.random.nextInt(materialList.size()));
        ItemStack item = new ItemStack(material);
        if (!event.addItem(item)) return;
        plugin.getLogger().info("Spawned " + ItemKinds.name(item) + " for " + player.getName()
                                + " in dungeon at " + event.getBoundingBox());
    }
}
