package com.cavetale.bingo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Data
public final class PlayerTag {
    protected boolean completed;
    protected int completionCount;
    protected List<Material> materialList = new ArrayList<>();
    protected List<Boolean> completeList = new ArrayList<>();

    public List<Material> findBingo() {
        if (completeList.isEmpty()) return null;
        COLUMNS:
        for (int column = 0; column < 5; column += 1) {
            for (int row = 0; row < 5; row += 1) {
                if (!completeList.get(column + row * 5)) {
                    continue COLUMNS;
                }
            }
            return List.of(materialList.get(column + 0 * 5),
                           materialList.get(column + 1 * 5),
                           materialList.get(column + 2 * 5),
                           materialList.get(column + 3 * 5),
                           materialList.get(column + 4 * 5));
        }
        ROWS:
        for (int row = 0; row < 5; row += 1) {
            for (int column = 0; column < 5; column += 1) {
                if (!completeList.get(column + row * 5)) {
                    continue ROWS;
                }
            }
            return List.of(materialList.get(0 + row * 5),
                           materialList.get(1 + row * 5),
                           materialList.get(2 + row * 5),
                           materialList.get(3 + row * 5),
                           materialList.get(4 + row * 5));
        }
        DIAG:
        do {
            for (int i = 0; i < 5; i += 1) {
                if (!completeList.get(i + i * 5)) {
                    break DIAG;
                }
            }
            return List.of(materialList.get(0 + 0 * 5),
                           materialList.get(1 + 1 * 5),
                           materialList.get(2 + 2 * 5),
                           materialList.get(3 + 3 * 5),
                           materialList.get(4 + 4 * 5));
        } while (false);
        DIAG:
        do {
            for (int i = 0; i < 5; i += 1) {
                if (!completeList.get(i + (4 - i) * 5)) {
                    break DIAG;
                }
            }
            return List.of(materialList.get(0 + 4 * 5),
                           materialList.get(1 + 3 * 5),
                           materialList.get(2 + 2 * 5),
                           materialList.get(3 + 1 * 5),
                           materialList.get(4 + 0 * 5));
        } while (false);
        return null;
    }

    public boolean isBingo() {
        return findBingo() != null;
    }

    public void buildCompleteList(Player player) {
        completeList.clear();
        for (Material mat : materialList) {
            completeList.add(player.getInventory().contains(mat) || player.getEnderChest().contains(mat));
        }
    }

    public void roll(List<Material> allMaterials, Random random) {
        Collections.shuffle(allMaterials, random);
        materialList.clear();
        for (int i = 0; i < 25; i += 1) {
            materialList.add(allMaterials.get(i));
        }
    }
}
