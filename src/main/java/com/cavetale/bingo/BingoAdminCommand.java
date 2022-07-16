package com.cavetale.bingo;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class BingoAdminCommand extends AbstractCommand<BingoPlugin> {
    protected BingoAdminCommand(final BingoPlugin plugin) {
        super(plugin, "bingoadmin");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("give").denyTabCompletion()
            .description("Give yourself all bingo items")
            .playerCaller(this::give);
        rootNode.addChild("reset").denyTabCompletion()
            .description("Reset player data")
            .senderCaller(this::reset);
        rootNode.addChild("reward").denyTabCompletion()
            .description("Reward players")
            .senderCaller(this::reward);
    }

    private void give(Player player) {
        for (Material mat : plugin.getPlayerTag(player).getMaterialList()) {
            player.getInventory().addItem(new ItemStack(mat));
        }
        player.sendMessage(text("Items given!", AQUA));
    }

    private void reset(CommandSender sender) {
        plugin.resetProgress();
        plugin.computeHighscore();
        sender.sendMessage(text("Player progress was reset", AQUA));
    }

    private void reward(CommandSender sender) {
        int count = plugin.rewardHighscore();
        sender.sendMessage(text(count + " highscores rewarded", AQUA));
    }
}
