package com.cavetale.bingo;

import com.cavetale.dungeons.DungeonLootEvent;
import com.cavetale.sidebar.PlayerSidebarEvent;
import com.cavetale.sidebar.Priority;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    void onPlayerSidebar(PlayerSidebarEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("bingo.bingo")) return;
        PlayerTag playerTag = plugin.getPlayerTag(player);
        List<Component> list = new ArrayList<>();
        list.add(Component.text("/", NamedTextColor.YELLOW)
                 .append(plugin.bingoComponent));
        list.add(plugin.getSubtitle(playerTag));
        event.add(plugin, Priority.HIGHEST, list);
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
        List<Material> materialList = plugin.getPlayerTag(player).materialList;
        Material material = materialList.get(plugin.random.nextInt(materialList.size()));
        ItemStack item = new ItemStack(material);
        if (!event.addItem(item)) return;
        plugin.getLogger().info("Spawned " + item.getI18NDisplayName() + " for " + player.getName()
                                + " in dungeon at " + event.getDungeon().getLo());
    }
}
