package com.cavetale.bingo;

import com.cavetale.bingo.save.PlayerTag;
import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import com.cavetale.core.exploits.PlayerPlacedBlocks;
import com.cavetale.core.item.ItemKinds;
import com.cavetale.dungeons.DungeonLootEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

@RequiredArgsConstructor
public final class EventListener implements Listener {
    private final BingoPlugin plugin;

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerTagMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerHud(PlayerHudEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("bingo.bingo")) return;
        PlayerTag playerTag = plugin.getPlayerTag(player);
        List<Component> lines = new ArrayList<>();
        lines.add(textOfChildren(text("/", YELLOW), plugin.BINGO));
        if (plugin.getSaveTag().getEndTime() > 0L) {
            lines.add(plugin.getTimeLeftComponent());
        }
        if (plugin.getSaveTag().isPause()) {
            lines.add(text("Paused", GRAY, ITALIC));
        } else {
            lines.add(plugin.getSubtitle(playerTag));
        }
        if (plugin.getSaveTag().isEvent()) {
            lines.addAll(plugin.getHighscoreLines());
        }
        if (lines.isEmpty()) return;
        event.sidebar(PlayerHudPriority.HIGHEST, lines);
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        if (plugin.getSaveTag().isPause()) return;
        if (!plugin.getSaveTag().isUseItems()) return;
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
        if (plugin.getSaveTag().isPause()) return;
        if (!plugin.getSaveTag().isUseItems()) return;
        Player player = event.getPlayer();
        List<Material> materialList = plugin.getPlayerTag(player).getMaterialList();
        Material material = materialList.get(plugin.getRandom().nextInt(materialList.size()));
        ItemStack item = new ItemStack(material);
        if (!event.addItem(item)) return;
        plugin.getLogger().info(
            "Spawned " + ItemKinds.name(item) + " for " + player.getName()
            + " in dungeon at " + event.getBoundingBox()
        );
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getSaveTag().isPause()) return;
        if (!plugin.getSaveTag().isUseBlocks()) return;
        final Block block = event.getBlock();
        if (PlayerPlacedBlocks.isPlayerPlaced(block)) return;
        final Player player = event.getPlayer();
        final PlayerTag tag = plugin.getPlayerTagMap().get(player.getUniqueId());
        if (tag == null || tag.isCompleted() || tag.getMaterialList().isEmpty()) return;
        final boolean success = tag.setMaterial(block.getType(), true);
        if (success) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 0.5f, 2f);
            player.sendMessage(textOfChildren(plugin.BINGO, text(" You collected " + block.getType().getKey().getKey().replace("_", " ") + "!", GREEN)));
        }
    }
}
