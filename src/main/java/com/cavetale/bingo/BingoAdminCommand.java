package com.cavetale.bingo;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.playercache.PlayerCache;
import com.cavetale.fam.trophy.Highscore;
import com.cavetale.mytems.item.trophy.TrophyCategory;
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
        rootNode.addChild("addscore").arguments("<player> <score>")
            .description("Add score")
            .completers(CommandArgCompleter.PLAYER_CACHE,
                        CommandArgCompleter.INTEGER)
            .senderCaller(this::addScore);
        rootNode.addChild("event").arguments("[true|false]")
            .description("Toggle event mode")
            .completers(CommandArgCompleter.BOOLEAN)
            .senderCaller(this::event);
        rootNode.addChild("pause").arguments("[true|false]")
            .description("Toggle pause")
            .completers(CommandArgCompleter.BOOLEAN)
            .senderCaller(this::pause);
        rootNode.addChild("start").arguments("<minutes>")
            .description("Start timed game")
            .completers(CommandArgCompleter.integer(i -> i > 0))
            .senderCaller(this::start);
    }

    private void give(Player player) {
        for (Material mat : plugin.getPlayerTag(player).getMaterialList()) {
            player.getInventory().addItem(new ItemStack(mat));
        }
        player.sendMessage(text("Items given!", AQUA));
    }

    private void reset(CommandSender sender) {
        plugin.resetProgress();
        sender.sendMessage(text("Player progress was reset", AQUA));
    }

    private void reward(CommandSender sender) {
        final int count = Highscore.reward(
            plugin.getSaveTag().getScores(),
            "bingo_event",
            TrophyCategory.MEDAL,
            plugin.BINGO,
            hi -> "You completed " + hi.score + " Bingo card" + (hi.score == 1 ? "" : "s")
        );
        sender.sendMessage(text(count + " highscores rewarded", AQUA));
        Highscore.rewardMoneyWithFeedback(sender, plugin, plugin.getSaveTag().getScores(), "Bingo!");
    }

    private boolean addScore(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        PlayerCache target = PlayerCache.require(args[0]);
        int score = CommandArgCompleter.requireInt(args[1], i -> i != 0);
        plugin.saveTag.addScore(target.uuid, score);
        plugin.computeHighscore();
        sender.sendMessage(text("Score of " + target.name + " added: " + score, AQUA));
        return true;
    }

    private boolean event(CommandSender sender, String[] args) {
        if (args.length > 1) return false;
        if (args.length >= 1) {
            boolean value = CommandArgCompleter.requireBoolean(args[0]);
            plugin.saveTag.setEvent(value);
            plugin.saveSaveTag();
        }
        if (plugin.saveTag.isEvent()) {
            sender.sendMessage(text("Event mode enabled", AQUA));
        } else {
            sender.sendMessage(text("Event mode disabled", RED));
        }
        return true;
    }

    private boolean pause(CommandSender sender, String[] args) {
        if (args.length > 1) return false;
        if (args.length >= 1) {
            boolean value = CommandArgCompleter.requireBoolean(args[0]);
            plugin.saveTag.setPause(value);
            plugin.saveTag.setEndTime(0L);
            plugin.saveSaveTag();
        }
        if (plugin.saveTag.isPause()) {
            sender.sendMessage(text("Pause mode enabled, end time reset", AQUA));
        } else {
            sender.sendMessage(text("Pause mode disabled, end time reset", RED));
        }
        return true;
    }

    private boolean start(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        final int minutes = CommandArgCompleter.requireInt(args[0], i -> i > 0);
        final long then = System.currentTimeMillis() + ((long) minutes * 1000L * 60L);
        plugin.getSaveTag().setEndTime(then);
        plugin.getSaveTag().setPause(false);
        plugin.saveSaveTag();
        sender.sendMessage(text("Timed game started", YELLOW));
        return true;
    }
}
