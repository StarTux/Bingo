package com.cavetale.bingo.save;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import static com.cavetale.bingo.BingoPlugin.ROWS;

@Data
public final class PlayerTag {
    protected boolean completed;
    protected boolean memberListed;
    protected int completionCount;
    protected List<Material> materialList = new ArrayList<>();
    protected List<Boolean> completeList = new ArrayList<>();

    public List<Material> findBingo() {
        if (completeList.isEmpty()) return null;
        List<Material> result = new ArrayList<>();
        COLUMNS:
        for (int column = 0; column < ROWS; column += 1) {
            for (int row = 0; row < ROWS; row += 1) {
                if (!completeList.get(column + row * ROWS)) {
                    continue COLUMNS;
                }
            }
            for (int i = 0; i < ROWS; i += 1) {
                result.add(materialList.get(column + i * ROWS));
            }
            return result;
        }
        ROWS:
        for (int row = 0; row < ROWS; row += 1) {
            for (int column = 0; column < ROWS; column += 1) {
                if (!completeList.get(column + row * ROWS)) {
                    continue ROWS;
                }
            }
            for (int i = 0; i < ROWS; i += 1) {
                result.add(materialList.get(i + row * ROWS));
            }
            return result;
        }
        DIAG:
        do {
            for (int i = 0; i < ROWS; i += 1) {
                if (!completeList.get(i + i * ROWS)) {
                    break DIAG;
                }
            }
            for (int i = 0; i < ROWS; i += 1) {
                result.add(materialList.get(i + i * ROWS));
            }
            return result;
        } while (false);
        DIAG:
        do {
            for (int i = 0; i < ROWS; i += 1) {
                if (!completeList.get(i + (ROWS - i - 1) * ROWS)) {
                    break DIAG;
                }
            }
            for (int i = 0; i < ROWS; i += 1) {
                result.add(materialList.get(i + (ROWS - i - 1) * ROWS));
            }
            return result;
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
        for (int i = 0; i < ROWS * ROWS; i += 1) {
            materialList.add(allMaterials.get(i));
        }
    }
}
