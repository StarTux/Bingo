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
import org.bukkit.inventory.Inventory;
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
        if (event.getDungeon().isRaided()) return;
        Player player = event.getPlayer();
        List<Material> materialList = plugin.getPlayerTag(player).materialList;
        Material material = materialList.get(plugin.random.nextInt(materialList.size()));
        Inventory inv = event.getInventory();
        int index = -1;
        int chance = 1;
        for (int i = 0; i < inv.getSize(); i += 1) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                if (plugin.random.nextInt(chance++) == 0) {
                    index = i;
                }
            }
        }
        if (index < 0) return;
        ItemStack item = new ItemStack(material);
        inv.setItem(index, item);
        plugin.getLogger().info("Spawned " + item.getI18NDisplayName() + " for " + player.getName()
                                + " in dungeon at " + event.getDungeon().getLo());
    }
}
