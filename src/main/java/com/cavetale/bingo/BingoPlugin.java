package com.cavetale.bingo;

import com.cavetale.bingo.util.Gui;
import com.cavetale.core.font.GuiOverlay;
import com.cavetale.core.util.Json;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.MytemsTag;
import com.cavetale.mytems.util.Items;
import com.winthier.title.TitlePlugin;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class BingoPlugin extends JavaPlugin {
    protected BingoCommand bingoCommand = new BingoCommand(this);
    protected BingoAdminCommand bingoAdminCommand = new BingoAdminCommand(this);
    protected EventListener eventListener = new EventListener(this);
    protected Random random = new Random();
    protected final Map<UUID, PlayerTag> playerTagMap = new HashMap<>();
    protected File playersFolder;
    private final List<Material> materialList = new ArrayList<>(EnumSet.of(Material.CAKE, new Material[] {
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
                Material.COMPASS,
                Material.CROSSBOW,
                Material.BLAZE_ROD,
                Material.SWEET_BERRIES,
                Material.QUARTZ,
                Material.FLINT_AND_STEEL,
                Material.MAGMA_CREAM,
                Material.POWERED_RAIL,
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
            }));
    private static final List<String> TITLES = List.of("Bingo",
                                                       "Battleship",
                                                       "AxolotlBucket");
    private static final List<ItemStack> STARTER_KIT = List.of(new ItemStack[] {
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.WOODEN_SHOVEL),
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.BREAD, 16),
        });
    protected Component bingoComponent;

    @Override
    public void onEnable() {
        bingoCommand.enable();
        bingoAdminCommand.enable();
        eventListener.enable();
        bingoComponent = Component.text()
            .decorate(TextDecoration.BOLD)
            .append(Component.text("B", TextColor.color(0xff4500)))
            .append(Component.text("i", TextColor.color(0xffa500)))
            .append(Component.text("n", TextColor.color(0x00ff00)))
            .append(Component.text("g", TextColor.color(0x8080ff)))
            .append(Component.text("o", TextColor.color(0xcd5c5c)))
            .append(Component.text("!", TextColor.color(0xffd700)))
            .build();
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
        playerTagMap.clear();
        Gui.disable(this);
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

    public void resetPlayerTags() {
        playerTagMap.clear();
        for (File file : playersFolder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private void rollPlayerTag(PlayerTag playerTag) {
        Collections.shuffle(materialList, random);
        playerTag.getMaterialList().clear();
        for (int i = 0; i < 25; i += 1) {
            playerTag.getMaterialList().add(materialList.get(i));
        }
    }

    public Component getSubtitle(PlayerTag playerTag) {
        return playerTag.getCompletionCount() == 0
            ? Component.text("Collect 5 in a row!", NamedTextColor.WHITE)
            : Component.text("Run #" + ((playerTag.isCompleted() ? 0 : 1) + playerTag.getCompletionCount()), NamedTextColor.WHITE);
    }

    public void openGui(Player player) {
        PlayerTag playerTag = getPlayerTag(player);
        if (playerTag == null) throw new IllegalStateException(player.getName() + " playerTag = null");
        player.sendMessage(Component.join(JoinConfiguration.noSeparators(),
                                          bingoComponent,
                                          Component.space(),
                                          Component.text("Collect 5 in a row, column, or diagonal!", NamedTextColor.WHITE)));
        final int size = 5 * 9;
        Component guiTitle = Component.join(JoinConfiguration.noSeparators(), new Component[] {
                bingoComponent,
                Component.space(),
                getSubtitle(playerTag),
            });
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size).title(guiTitle)
            .layer(GuiOverlay.BLANK, TextColor.color(0x802080));
        if (!playerTag.isCompleted()) {
            buildCompleteList(player, playerTag);
        }
        for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                int guiIndex = 2 + column + row * 9;
                if (playerTag.getMaterialList().isEmpty()) {
                    gui.setItem(guiIndex, Items.text(Mytems.QUESTION_MARK.createItemStack(),
                                                     List.of(Component.text("?", NamedTextColor.GREEN))));
                } else {
                    Material material = playerTag.getMaterialList().get(column + row * 5);
                    gui.setItem(guiIndex, new ItemStack(material));
                    if (playerTag.getCompleteList().get(column + row * 5)) {
                        builder.highlightSlot(guiIndex, NamedTextColor.GREEN);
                    }
                }
            }
        }
        gui.title(builder.build());
        if (playerTag.isCompleted() || playerTag.getMaterialList().isEmpty()) {
            gui.setItem(18, Items.text(Mytems.DICE.createItemStack(),
                                       List.of(Component.text("Create Bingo Sheet", NamedTextColor.GREEN))),
                        click -> {
                            if (!click.isLeftClick()) return;
                            playerTag.setCompleted(false);
                            playerTag.getCompleteList().clear();
                            rollPlayerTag(playerTag);
                            savePlayerTag(player.getUniqueId(), playerTag);
                            startRollAnimation(player, playerTag);
                            for (ItemStack item : STARTER_KIT) {
                                player.getInventory().addItem(item.clone());
                            }
                        });
        }
        if (!playerTag.isCompleted() && isBingo(playerTag)) {
            playerTag.setCompleted(true);
            playerTag.setCompletionCount(playerTag.getCompletionCount() + 1);
            savePlayerTag(player.getUniqueId(), playerTag);
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getInventory().clear();
                player.getEnderChest().clear();
            }
            gui.onClose(evt -> onPlayerHasBingo(player, playerTag));
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            gui.setEditable(true);
        }
        gui.open(player);
    }

    protected void startRollAnimation(Player player, PlayerTag playerTag) {
        Component guiTitle = Component.join(JoinConfiguration.noSeparators(), new Component[] {
                bingoComponent,
                Component.space(),
                getSubtitle(playerTag),
            });
        final int size = 5 * 9;
        final Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size).title(guiTitle)
            .layer(GuiOverlay.BLANK, TextColor.color(0x802080));
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

    protected void buildCompleteList(Player player, PlayerTag playerTag) {
        playerTag.getCompleteList().clear();
        for (Material mat : playerTag.getMaterialList()) {
            playerTag.getCompleteList().add(player.getInventory().contains(mat)
                                            || player.getEnderChest().contains(mat));
        }
    }

    protected boolean isBingo(PlayerTag playerTag) {
        List<Boolean> completeList = playerTag.getCompleteList();
        if (completeList.isEmpty()) return false;
        COLUMNS: for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                if (!completeList.get(column + row * 5)) {
                    continue COLUMNS;
                }
            }
            return true;
        }
        ROWS: for (int row = 0; row < 5; row += 1) {
            for (int column = 0; column < 5; column += 1) {
                if (!completeList.get(column + row * 5)) {
                    continue ROWS;
                }
            }
            return true;
        }
        DIAG: do {
            for (int i = 0; i < 5; i += 1) {
                if (!completeList.get(i + i * 5)) {
                    break DIAG;
                }
            }
            return true;
        } while (false);
        DIAG: do {
            for (int i = 0; i < 5; i += 1) {
                if (!completeList.get(i + (4 - i) * 5)) {
                    break DIAG;
                }
            }
            return true;
        } while (false);
        return false;
    }

    protected void onPlayerHasBingo(Player player, PlayerTag playerTag) {
        // Announce
        final int completionCount = playerTag.getCompletionCount();
        getLogger().info(player.getName() + " has Bingo #" + completionCount);
        for (Player target : Bukkit.getOnlinePlayers()) {
            Component message = Component.join(JoinConfiguration.noSeparators(), new Component[] {
                    player.displayName(),
                    Component.text(" has ", NamedTextColor.GREEN),
                    bingoComponent,
                    (completionCount == 0
                     ? Component.empty()
                     : Component.text(" #" + completionCount, NamedTextColor.GRAY)),
                });
            target.sendActionBar(message);
            target.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), new Component[] {
                        Component.empty(),
                        message,
                        Component.empty(),
                    }));
        }
        player.showTitle(Title.title(bingoComponent,
                                     Component.text("Congratulations!", NamedTextColor.GREEN),
                                     Title.Times.of(Duration.ofSeconds(1),
                                                    Duration.ofSeconds(1),
                                                    Duration.ofSeconds(1))));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0f, 0.85f);
        // MemberList and Title
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ml add " + player.getName());
        List<String> titles = List.copyOf(TITLES.subList(0, Math.min(TITLES.size(), completionCount)));
        String cmd = "titles unlockset " + player.getName() + " " + String.join(" ", titles);
        getLogger().info("Dispatching command: " + cmd);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        // Random reward
        List<Mytems> list = MytemsTag.MUSIC.toList();
        if (!list.isEmpty()) {
            player.getInventory().addItem(list.get(random.nextInt(list.size())).createItemStack());
        }
    }
}
