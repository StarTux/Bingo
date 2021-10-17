package com.cavetale.bingo;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BingoCommand implements TabExecutor {
    private final BingoPlugin plugin;

    public void enable() {
        plugin.getCommand("bingo").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[Bingo:bingo] Player expected!");
            return true;
        }
        if (args.length != 0) return false;
        Player player = (Player) sender;
        if (plugin.tag.completed.containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text("You already had bingo!", NamedTextColor.RED));
            return true;
        }
        plugin.openGui(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return Collections.emptyList();
    }
}
