package com.cavetale.bingo;

import com.cavetale.bingo.save.PlayerTag;
import com.cavetale.bingo.save.SaveTag;
import com.cavetale.bingo.util.Gui;
import com.cavetale.core.font.GuiOverlay;
import com.cavetale.core.util.Json;
import com.cavetale.fam.trophy.Highscore;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.MytemsCategory;
import com.cavetale.mytems.MytemsTag;
import com.cavetale.mytems.item.trophy.TrophyCategory;
import com.cavetale.mytems.util.Items;
import com.destroystokyo.paper.MaterialTags;
import com.winthier.title.TitlePlugin;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.title.Title.Times.times;
import static net.kyori.adventure.title.Title.title;

public final class BingoPlugin extends JavaPlugin {
    protected BingoCommand bingoCommand = new BingoCommand(this);
    protected BingoAdminCommand bingoAdminCommand = new BingoAdminCommand(this);
    protected EventListener eventListener = new EventListener(this);
    protected Random random = ThreadLocalRandom.current();
    protected SaveTag saveTag;
    protected final Map<UUID, PlayerTag> playerTagMap = new HashMap<>();
    protected List<Highscore> highscore = List.of();
    protected List<Component> highscoreLines = List.of();
    protected File playersFolder;
    private final List<Material> materialList = new ArrayList<>();
    private static final List<String> TITLES = List.of("Bingo",
                                                       "Battleship",
                                                       "AxolotlBucket",
                                                       "TropicalFishBucket");
    private static final List<ItemStack> STARTER_KIT = List.of(new ItemStack[] {
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.WOODEN_SHOVEL),
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.BREAD, 16),
        });
    protected static final Component BINGO = join(noSeparators(),
                                                  text("B", color(0xff4500)),
                                                  text("i", color(0xffa500)),
                                                  text("n", color(0x00ff00)),
                                                  text("g", color(0x8080ff)),
                                                  text("o", color(0xcd5c5c)),
                                                  text("!", color(0xffd700)))
        .decorate(TextDecoration.BOLD);

    private void buildMaterialList() {
        materialList.clear();
        Set<Material> set = new HashSet<>(Set.of(new Material[] {
                    // Original
                    Material.MAGMA_CREAM,
                    Material.SPIDER_EYE,
                    Material.ENDER_PEARL,
                    Material.POWERED_RAIL,
                    Material.REPEATER,
                    Material.COMPASS,
                    Material.BLUE_CARPET,
                    Material.ENCHANTING_TABLE,
                    Material.GOLDEN_APPLE,
                    // 2nd run
                    Material.CROSSBOW,
                    Material.BLAZE_ROD,
                    Material.SWEET_BERRIES,
                    Material.QUARTZ,
                    Material.FLINT_AND_STEEL,
                    Material.TNT,
                    // Random
                    Material.SEA_LANTERN,
                    Material.LANTERN,
                    Material.GLOWSTONE,
                    Material.ANCIENT_DEBRIS,
                    Material.IRON_BLOCK,
                    Material.GOLD_BLOCK,
                    Material.REDSTONE_BLOCK,
                    // 1.17
                    Material.AMETHYST_SHARD,
                    Material.AXOLOTL_BUCKET,
                    Material.COPPER_BLOCK,
                    Material.GLOW_BERRIES,
                    Material.GLOW_INK_SAC,
                    Material.SPYGLASS,
                    Material.CALCITE,
                    Material.CANDLE,
                    Material.POLISHED_DEEPSLATE,
                    Material.DRIPSTONE_BLOCK,
                    Material.GLOW_ITEM_FRAME,
                    Material.LIGHTNING_ROD,
                    Material.POINTED_DRIPSTONE,
                    Material.TINTED_GLASS,
                    Material.TUFF,
                    // August 2021
                    Material.OBSERVER,
                    Material.DAYLIGHT_DETECTOR,
                    Material.LEAD,
                    Material.JACK_O_LANTERN,
                    Material.FIRE_CHARGE,
                    Material.CAKE,
                    // November 2021
                    Material.HONEYCOMB,
                    Material.VINE,
                    Material.SMALL_DRIPLEAF,
                    Material.BIG_DRIPLEAF,
                    Material.GLOW_LICHEN,
                    Material.ENDER_CHEST,
                    Material.FERMENTED_SPIDER_EYE,
                    Material.MOSS_BLOCK,
                    Material.NETHERITE_PICKAXE,
                    Material.SCUTE,
                    Material.SHROOMLIGHT,
                    Material.AZALEA,
                    Material.FLOWERING_AZALEA,
                    // January 2022
                    Material.COOKIE,
                    Material.PUMPKIN_PIE,
                    Material.SHIELD,
                    Material.POWDER_SNOW_BUCKET,
                    Material.STONECUTTER,
                    Material.CARTOGRAPHY_TABLE,
                    Material.BREWING_STAND,
                    Material.COMPOSTER,
                    Material.BARREL,
                    Material.LECTERN,
                    Material.CAULDRON,
                    Material.LOOM,
                    Material.GRINDSTONE,
                    Material.SMITHING_TABLE,
                    // May 2022
                    Material.ANVIL,
                    Material.FEATHER,
                    Material.DRIED_KELP_BLOCK,
                    Material.SEA_PICKLE,
                    Material.LILY_PAD,
                    Material.COBWEB,
                    Material.RABBIT_STEW,
                    Material.MUSHROOM_STEW,
                    Material.SUSPICIOUS_STEW,
                }));
        set.addAll(Tag.FLOWERS.getValues());
        set.addAll(Tag.SAPLINGS.getValues());
        set.addAll(Tag.LEAVES.getValues());
        set.addAll(MaterialTags.MUSHROOMS.getValues());
        set.addAll(MaterialTags.MUSHROOM_BLOCKS.getValues());
        set.removeIf(m -> !m.isItem());
        materialList.addAll(set);
    }

    @Override
    public void onEnable() {
        buildMaterialList();
        loadSaveTag();
        bingoCommand.enable();
        bingoAdminCommand.enable();
        eventListener.enable();
        Gui.enable(this);
        playersFolder = new File(getDataFolder(), "players");
        playersFolder.mkdirs();
        for (String titleName : TITLES) {
            if (TitlePlugin.getInstance().getTitle(titleName) == null) {
                getLogger().warning("Title not found: " + titleName);
            }
        }
    }

    @Override
    public void onDisable() {
        saveSaveTag();
        playerTagMap.clear();
        Gui.disable(this);
    }

    protected void loadSaveTag() {
        saveTag = Json.load(new File(getDataFolder(), "save.json"), SaveTag.class, SaveTag::new);
        computeHighscore();
    }

    protected void saveSaveTag() {
        if (saveTag == null) return;
        Json.save(new File(getDataFolder(), "save.json"), saveTag);
    }

    protected PlayerTag loadPlayerTag(UUID uuid) {
        return Json.load(new File(playersFolder, uuid + ".json"), PlayerTag.class, PlayerTag::new);
    }

    protected void savePlayerTag(UUID uuid, PlayerTag playerTag) {
        Json.save(new File(playersFolder, uuid + ".json"), playerTag, true);
    }

    protected void savePlayerTag(UUID uuid) {
        PlayerTag playerTag = Objects.requireNonNull(playerTagMap.get(uuid));
        savePlayerTag(uuid, playerTag);
    }

    public PlayerTag getPlayerTag(Player player) {
        if (!player.hasPermission("bingo.bingo")) return null;
        return playerTagMap.computeIfAbsent(player.getUniqueId(), this::loadPlayerTag);
    }

    public void resetProgress() {
        playerTagMap.clear();
        for (File file : playersFolder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
        saveTag.getScores().clear();
        saveSaveTag();
        computeHighscore();
    }

    public Component getSubtitle(PlayerTag playerTag) {
        return playerTag.getCompletionCount() == 0
            ? text("Collect 5 in a row!", WHITE)
            : text("Run #" + ((playerTag.isCompleted() ? 0 : 1) + playerTag.getCompletionCount()), WHITE);
    }

    public void openGui(Player player) {
        PlayerTag playerTag = getPlayerTag(player);
        if (playerTag == null) throw new IllegalStateException(player.getName() + " playerTag = null");
        final UUID uuid = player.getUniqueId();
        if (!playerTag.isMemberListed()) {
            playerTag.setMemberListed(true);
            savePlayerTag(uuid, playerTag);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ml add " + player.getName());
        }
        player.sendMessage(join(noSeparators(),
                                          BINGO,
                                          space(),
                                          text("Collect 5 in a row, column, or diagonal!", WHITE)));
        final int size = 5 * 9;
        Component guiTitle = join(noSeparators(), new Component[] {
                BINGO,
                space(),
                getSubtitle(playerTag),
            });
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size).title(guiTitle)
            .layer(GuiOverlay.BLANK, color(0x802080));
        if (!playerTag.isCompleted()) {
            playerTag.buildCompleteList(player);
        }
        for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                int guiIndex = 2 + column + row * 9;
                if (playerTag.getMaterialList().isEmpty()) {
                    gui.setItem(guiIndex, Items.text(Mytems.QUESTION_MARK.createItemStack(),
                                                     List.of(text("?", GREEN))));
                } else {
                    Material material = playerTag.getMaterialList().get(column + row * 5);
                    gui.setItem(guiIndex, new ItemStack(material));
                    if (playerTag.getCompleteList().get(column + row * 5)) {
                        builder.highlightSlot(guiIndex, GREEN);
                    }
                }
            }
        }
        gui.title(builder.build());
        if (playerTag.isCompleted() || playerTag.getMaterialList().isEmpty()) {
            gui.setItem(18, Items.text(Mytems.DICE.createItemStack(),
                                       List.of(text("Create Bingo Sheet", GREEN))),
                        click -> {
                            if (!click.isLeftClick()) return;
                            playerTag.setCompleted(false);
                            playerTag.getCompleteList().clear();
                            playerTag.roll(materialList, random);
                            savePlayerTag(uuid, playerTag);
                            startRollAnimation(player, playerTag);
                            if (playerTag.getCompletionCount() == 0) {
                                for (ItemStack item : STARTER_KIT) {
                                    player.getInventory().addItem(item.clone());
                                }
                            }
                        });
        }
        List<Material> bingo = playerTag.findBingo();
        if (!playerTag.isCompleted() && bingo != null && !bingo.isEmpty()) {
            for (Material material : bingo) {
                int first;
                first = player.getInventory().first(material);
                if (first >= 0) {
                    player.getInventory().getItem(first).subtract(1);
                    continue;
                }
                first = player.getEnderChest().first(material);
                if (first >= 0) {
                    player.getEnderChest().getItem(first).subtract(1);
                    continue;
                }
            }
            playerTag.setCompleted(true);
            playerTag.setCompletionCount(playerTag.getCompletionCount() + 1);
            savePlayerTag(uuid, playerTag);
            saveTag.getScores().put(uuid, playerTag.getCompletionCount());
            computeHighscore();
            saveSaveTag();
            gui.onClose(evt -> onPlayerHasBingo(player, playerTag));
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            gui.setEditable(true);
        }
        gui.open(player);
    }

    protected void startRollAnimation(Player player, PlayerTag playerTag) {
        Component guiTitle = join(noSeparators(), new Component[] {
                BINGO,
                space(),
                getSubtitle(playerTag),
            });
        final int size = 5 * 9;
        final Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size).title(guiTitle)
            .layer(GuiOverlay.BLANK, color(0x802080));
        gui.title(builder.build());
        gui.open(player);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                ticks += 1;
                if (gui.isClosed()) {
                    cancel();
                    return;
                }
                if (ticks > 120) {
                    if ((ticks % 8) != 0) return;
                    if (ticks > 140) {
                        cancel();
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE,
                                         SoundCategory.MASTER, 1.0f, 2.0f);
                        openGui(player);
                        return;
                    }
                } else if (ticks > 100) {
                    if ((ticks % 4) != 0) return;
                } else if (ticks > 80) {
                    if ((ticks % 3) != 0) return;
                } else if (ticks > 60) {
                    if ((ticks % 2) == 0) return;
                } else if (ticks > 40) {
                    if ((ticks % 3) == 0) return;
                } else if (ticks > 20) {
                    if ((ticks % 4) == 0) return;
                }
                for (int column = 0; column < 5; column += 1) {
                    for (int row = 0; row < 5; row += 1) {
                        Material material = materialList.get(random.nextInt(materialList.size()));
                        gui.setItem(2 + column + row * 9, new ItemStack(material));
                    }
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,
                                 SoundCategory.MASTER, 0.25f, 2.0f);
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    protected void onPlayerHasBingo(Player player, PlayerTag playerTag) {
        // Announce
        final int completionCount = playerTag.getCompletionCount();
        getLogger().info(player.getName() + " has Bingo #" + completionCount);
        for (Player target : Bukkit.getOnlinePlayers()) {
            Component message = join(noSeparators(), new Component[] {
                    player.displayName(),
                    text(" has ", GREEN),
                    BINGO,
                    (completionCount == 0
                     ? empty()
                     : text(" #" + completionCount, GRAY)),
                });
            target.sendActionBar(message);
            target.sendMessage(join(separator(newline()), new Component[] {
                        empty(),
                        message,
                        empty(),
                    }));
        }
        player.showTitle(title(BINGO,
                               text("Congratulations!", GREEN),
                               times(Duration.ofSeconds(1),
                                     Duration.ofSeconds(1),
                                     Duration.ofSeconds(1))));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0f, 0.85f);
        // MemberList and Title
        List<String> titles = List.copyOf(TITLES.subList(0, Math.min(TITLES.size(), completionCount)));
        String cmd = "titles unlockset " + player.getName() + " " + String.join(" ", titles);
        getLogger().info("Dispatching command: " + cmd);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        // Random reward
        List<Mytems> list = MytemsTag.of(MytemsCategory.MUSIC).getMytems();
        if (!list.isEmpty()) {
            player.getInventory().addItem(list.get(random.nextInt(list.size())).createItemStack());
        }
    }

    protected void computeHighscore() {
        highscore = Highscore.of(saveTag.getScores());
        highscoreLines = Highscore.sidebar(highscore, TrophyCategory.MEDAL);
    }

    protected int rewardHighscore() {
        return Highscore.reward(saveTag.getScores(),
                                "bingo_event",
                                TrophyCategory.MEDAL,
                                BINGO,
                                hi -> "You completed " + hi.score + " Bingo card" + (hi.score == 1 ? "" : "s"));
    }
}
