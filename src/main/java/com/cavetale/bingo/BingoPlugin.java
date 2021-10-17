package com.cavetale.bingo;

import com.cavetale.core.font.DefaultFont;
import com.cavetale.mytems.Mytems;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin {
    protected BingoCommand bingoCommand = new BingoCommand(this);
    protected BingoAdminCommand bingoAdminCommand = new BingoAdminCommand(this);
    protected EventListener eventListener = new EventListener(this);
    protected Random random = new Random();
    protected Tag tag;
    /** All possible materials. May contain duplicates! */
    private final Material[] materialArray = {
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
    };
    /**
     * Initialized in onEnable(). Shuffled all the time to produce new
     * player tags.
     */
    private List<Material> materialList;
    private Component bingoComponent;

    @Override
    public void onEnable() {
        Set<Material> materialSet = EnumSet.of(materialArray[0], materialArray);
        materialList = new ArrayList<>(materialSet);
        bingoCommand.enable();
        bingoAdminCommand.enable();
        eventListener.enable();
        load();
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
    }

    @Override
    public void onDisable() {
        save();
        Gui.disable(this);
    }

    void load() {
        tag = Json.load(new File(getDataFolder(), "save.json"), Tag.class, Tag::new);
    }

    void save() {
        getDataFolder().mkdirs();
        Json.save(new File(getDataFolder(), "save.json"), tag, true);
    }

    public Tag.Player getPlayerTag(Player player) {
        if (!player.hasPermission("bingo.bingo")) return null;
        return tag.players.computeIfAbsent(player.getUniqueId(), this::makePlayerTag);
    }

    private Tag.Player makePlayerTag(UUID uuid) {
        Tag.Player result = new Tag.Player();
        Collections.shuffle(materialList, random);
        for (int i = 0; i < 25; i += 1) {
            result.materialList.add(materialList.get(i));
        }
        return result;
    }

    public void openGui(Player player) {
        Tag.Player playerTag = getPlayerTag(player);
        if (playerTag == null) throw new IllegalStateException(player.getName() + " playerTag = null");
        Set<Material> has = EnumSet.noneOf(Material.class);
        for (Material mat : playerTag.materialList) {
            if (player.getInventory().contains(mat) || player.getEnderChest().contains(mat)) {
                has.add(mat);
            }
        }
        player.sendMessage(Component.join(JoinConfiguration.noSeparators(),
                                          bingoComponent,
                                          Component.space(),
                                          Component.text("Collect 5 in a row, column, or diagonal!", NamedTextColor.WHITE)));
        final int size = 5 * 9;
        Component guiTitle = Component.join(JoinConfiguration.noSeparators(), new Component[] {
                bingoComponent,
                Component.space(),
                Component.text("Collect 5 in a row!", NamedTextColor.WHITE),
            });
        Gui gui = new Gui(this)
            .size(size)
            .title(DefaultFont.guiBlankOverlay(size, TextColor.color(0xFF00FF), guiTitle));
        for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                Material material = playerTag.materialList.get(column + row * 5);
                int guiIndex = 2 + column + row * 9;
                ItemStack icon;
                if (has.contains(material)) {
                    icon = Mytems.CROSSED_CHECKBOX.createItemStack();
                    icon.editMeta(meta -> {
                            Component displayName = Component.text(new ItemStack(material).getI18NDisplayName(),
                                                                   NamedTextColor.BLUE);
                            meta.displayName(displayName);
                        });
                } else {
                    icon = new ItemStack(material);
                }
                gui.setItem(guiIndex, icon);
            }
        }
        gui.open(player);
        boolean bingo = false;
        COLUMNS: for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                Material material = playerTag.materialList.get(column + row * 5);
                if (!has.contains(material)) continue COLUMNS;
            }
            bingo = true;
        }
        ROWS: for (int row = 0; row < 5; row += 1) {
            for (int column = 0; column < 5; column += 1) {
                Material material = playerTag.materialList.get(column + row * 5);
                if (!has.contains(material)) continue ROWS;
            }
            bingo = true;
        }
        DIAG: do {
            for (int i = 0; i < 5; i += 1) {
                Material material = playerTag.materialList.get(i + i * 5);
                if (!has.contains(material)) break DIAG;
            }
            bingo = true;
        } while (false);
        DIAG: do {
            for (int i = 0; i < 5; i += 1) {
                Material material = playerTag.materialList.get(i + (4 - i) * 5);
                if (!has.contains(material)) break DIAG;
            }
            bingo = true;
        } while (false);
        if (bingo) {
            playerHasBingo(player);
            gui.onClose(ce -> {
                    player.showTitle(Title.title(Component.text("Bingo!", NamedTextColor.GREEN, TextDecoration.BOLD),
                                                 Component.empty(),
                                                 Title.Times.of(Duration.ofSeconds(1),
                                                                Duration.ofSeconds(1),
                                                                Duration.ofSeconds(1))));
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "titles unlockset " + player.getName() + " Bingo");
                        }, 60L);
                });
        }
    }

    public void playerHasBingo(Player player) {
        tag.completed.put(player.getUniqueId(), player.getName());
        save();
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(ChatColor.GREEN + player.getName() + " has a Bingo!");
        }
        getLogger().info(player.getName() + " has a Bingo!");
        player.getInventory().clear();
        player.getEnderChest().clear();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ml add " + player.getName());
        List<Mytems> list = List.of(Mytems.PAN_FLUTE,
                                    Mytems.TRIANGLE,
                                    Mytems.WOODEN_LUTE,
                                    Mytems.WOODEN_OCARINA,
                                    Mytems.BANJO,
                                    Mytems.GUITAR,
                                    Mytems.MUSICAL_BELL,
                                    Mytems.COW_BELL,
                                    Mytems.POCKET_PIANO,
                                    Mytems.ELECTRIC_PIANO,
                                    Mytems.IRON_XYLOPHONE);
        player.getInventory().addItem(list.get(random.nextInt(list.size())).createItemStack(player));
    }
}
