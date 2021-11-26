package com.cavetale.bingo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.bukkit.Material;

@Data
public final class PlayerTag {
    protected boolean completed;
    protected List<Material> materialList = new ArrayList<>();
    protected List<Boolean> completeList = new ArrayList<>();
}
