package com.cavetale.bingo;

import com.destroystokyo.paper.MaterialTags;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Tag;

@Getter
public final class BlockMaterialList {
    private final List<Material> materialList;

    public BlockMaterialList() {
        final Material[] mats = new Material[] {
            // Overworld
            Material.GRASS_BLOCK,
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.STONE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,
            Material.DEEPSLATE,
            Material.GRAVEL,
            // Desert
            Material.SAND,
            Material.SANDSTONE,
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.CACTUS_FLOWER,
            Material.SHORT_DRY_GRASS,
            Material.DEAD_BUSH,
            // The End
            Material.END_STONE,
            Material.OBSIDIAN,
            // Nether
            Material.NETHERRACK,
            Material.SOUL_SAND,
            Material.NETHER_BRICKS,
            // Soul Sand Valley
            Material.SOUL_SOIL,
            Material.BONE_BLOCK,
            // Crimson Forest
            Material.CRIMSON_NYLIUM,
            Material.SHROOMLIGHT,
            Material.NETHER_WART_BLOCK,
            Material.CRIMSON_FUNGUS,
            // Warped Forest
            Material.WARPED_NYLIUM,
            Material.WARPED_FUNGUS,
            Material.WARPED_WART_BLOCK,
            Material.TWISTING_VINES,
            // Basalt Delta
            Material.BASALT,
            Material.BLACKSTONE,
            Material.MAGMA_BLOCK,
            // Ocean
            Material.SEAGRASS,
            Material.KELP,
            // Ocean Ruins / Monuments
            Material.DARK_PRISMARINE,
            Material.PRISMARINE,
            Material.PRISMARINE_BRICKS,
            Material.SEA_LANTERN,
            Material.GOLD_BLOCK,
            // Frozen Ocean
            Material.ICE,
            Material.PACKED_ICE,
            Material.SNOW_BLOCK,
            // Mushroom Fields
            Material.MYCELIUM,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM,
            // Taiga
            Material.PODZOL,
            Material.FERN,
            Material.SWEET_BERRY_BUSH,
            // Pale Garden
            Material.PALE_MOSS_BLOCK,
            Material.PALE_MOSS_CARPET,
            Material.PALE_HANGING_MOSS,
            // Jungle
            Material.FERN,
            Material.VINE,
            Material.MELON,
            Material.BAMBOO,
            // Lush Caves
            Material.MOSS_BLOCK,
            Material.MOSS_CARPET,
            Material.AZALEA,
            Material.FLOWERING_AZALEA,
            Material.SMALL_DRIPLEAF,
            Material.ROOTED_DIRT,
            // Mossy
            Material.MOSSY_COBBLESTONE,
            Material.MOSSY_STONE_BRICKS,
            // Swamp
            Material.LILY_PAD,
            Material.FIREFLY_BUSH,
            // Badlands
            Material.RED_SAND,
            Material.RED_SANDSTONE,
            Material.TERRACOTTA,
            Material.ORANGE_TERRACOTTA,
            Material.RED_TERRACOTTA,
            Material.YELLOW_TERRACOTTA,
            Material.WHITE_TERRACOTTA,
            Material.BROWN_TERRACOTTA,
            Material.LIGHT_GRAY_TERRACOTTA,
            // Deep Dark
            Material.SCULK,
            Material.SCULK_VEIN,
            Material.SCULK_SENSOR,
            Material.SCULK_SHRIEKER,
            // Dropstone Caves
            Material.DRIPSTONE_BLOCK,
            Material.POINTED_DRIPSTONE,
            // Sulfur Caves
            Material.CINNABAR,
            Material.SULFUR,
            Material.SULFUR_SPIKE,
            Material.POTENT_SULFUR,
            // Geode
            Material.AMETHYST_BLOCK,
            Material.AMETHYST_CLUSTER,
            Material.BUDDING_AMETHYST,
            Material.CALCITE,
            Material.SMOOTH_BASALT,
            // Misc
            Material.BRICKS, // Ocean ruins, plains village, trail ruins
            Material.BONE_BLOCK,
            Material.CLAY,
        };
        final Set<Material> set = new HashSet<>();
        for (Material mat : mats) set.add(mat);
        set.addAll(Tag.FLOWERS.getValues());
        set.addAll(Tag.LEAVES.getValues());
        set.addAll(MaterialTags.MUSHROOMS.getValues());
        set.addAll(MaterialTags.MUSHROOM_BLOCKS.getValues());
        set.addAll(Tag.CORAL_BLOCKS.getValues());
        set.addAll(MaterialTags.ORES.getValues());
        set.remove(Material.GOLDEN_DANDELION);
        for (Material mat : set) {
            if (!mat.isItem()) {
                BingoPlugin.getInstance().getLogger().severe("[ItemMaterialList] " + mat + ": Not an item!");
            }
            if (!mat.isBlock()) {
                BingoPlugin.getInstance().getLogger().severe("[ItemMaterialList] " + mat + ": Not a block!");
            }
        }
        set.removeIf(m -> !m.isItem() || !m.isBlock());
        materialList = List.copyOf(set);
    }
}
