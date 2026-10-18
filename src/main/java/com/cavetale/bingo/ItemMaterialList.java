package com.cavetale.bingo;

import com.destroystokyo.paper.MaterialTags;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Tag;

@Getter
public final class ItemMaterialList {
    private final List<Material> materialList;

    public ItemMaterialList() {
        final Material[] mats = new Material[] {
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
            Material.TURTLE_SCUTE,
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
            // May 2023
            Material.COD,
            Material.SALMON,
            Material.PUFFERFISH,
            Material.TROPICAL_FISH,
            Material.TADPOLE_BUCKET,
            Material.GOAT_HORN,
            Material.ECHO_SHARD,
            Material.RECOVERY_COMPASS,
            // 1.20.6, July 2024
            Material.ARMADILLO_SCUTE,
            // 1.21
            Material.COPPER_BULB,
            Material.CRAFTER,
            // 1.21.2
            Material.BUNDLE,
            // 1.21.4
            Material.CREAKING_HEART,
            Material.PALE_MOSS_BLOCK,
            Material.RESIN_BRICK,
            // 1.21.5
            Material.FIREFLY_BUSH,
            Material.CACTUS_FLOWER,
            Material.LEAF_LITTER,
            // 1.21.9 1.21.10
            Material.COPPER_CHEST,
            Material.COPPER_BARS,
            Material.COPPER_LANTERN,
            Material.COPPER_TORCH,
            Material.OAK_SHELF,
            Material.SPRUCE_SHELF,
            Material.BIRCH_SHELF,
            Material.JUNGLE_SHELF,
            Material.ACACIA_SHELF,
            Material.DARK_OAK_SHELF,
            Material.MANGROVE_SHELF,
            Material.CHERRY_SHELF,
            Material.PALE_OAK_SHELF,
            Material.BAMBOO_SHELF,
            Material.CRIMSON_SHELF,
            Material.WARPED_SHELF,
            // 26.1
            Material.GOLDEN_DANDELION,
            // 26.2
            Material.CINNABAR,
            Material.SULFUR,
            Material.SULFUR_CUBE_BUCKET,
            Material.SULFUR_SPIKE,
        };
        final Set<Material> set = new HashSet<>();
        for (Material mat : mats) set.add(mat);
        set.addAll(Tag.FLOWERS.getValues());
        set.addAll(Tag.SAPLINGS.getValues());
        set.addAll(Tag.LEAVES.getValues());
        set.addAll(MaterialTags.MUSHROOMS.getValues());
        set.addAll(MaterialTags.MUSHROOM_BLOCKS.getValues());
        set.addAll(Tag.ITEMS_CHEST_BOATS.getValues());
        set.addAll(Tag.ITEMS_HANGING_SIGNS.getValues());
        for (Material mat : set) {
            if (!mat.isItem()) {
                BingoPlugin.getInstance().getLogger().severe("[ItemMaterialList] " + mat + ": Not an item!");
            }
        }
        set.removeIf(m -> !m.isItem());
        materialList = List.copyOf(set);
    }
}
