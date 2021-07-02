package com.cavetale.bingo;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.cavetale.core.command.CommandNode;

@RequiredArgsConstructor
public final class BingoAdminCommand implements TabExecutor {
    private final BingoPlugin plugin;
    private CommandNode rootNode;

    public void enable() {
        rootNode = new CommandNode("bingoadmin");
        rootNode.addChild("give").denyTabCompletion()
            .description("Give yourself all bingo items")
            .playerCaller(this::give);
        rootNode.addChild("reset").denyTabCompletion()
            .description("Reset player data")
            .senderCaller(this::reset);
        plugin.getCommand("bingoadmin").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return rootNode.call(sender, command, alias, args);
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return rootNode.complete(sender, command, alias, args);
    }

    boolean give(Player player, String[] args) {
        if (args.length != 0) return false;
        for (Material mat : plugin.getPlayerTag(player).materialList) {
            player.getInventory().addItem(new ItemStack(mat));
        }
        player.sendMessage("Items given!");
        return true;
    }

    boolean reset(CommandSender sender, String[] args) {
        if (args.length != 0) return false;
        plugin.tag = new Tag();
        plugin.save();
        sender.sendMessage("Player progress was reset");
        return true;
    }
}
