package com.enderdevelopment.plugin;

import org.bukkit.Material;

public final class Quest {
    private final QuestType type;
    private final Material material;
    private final int target;
    private int progress;

    public Quest(QuestType type, Material material, int target, int progress) {
        this.type = type;
        this.material = material;
        this.target = target;
        this.progress = Math.min(progress, target);
    }

    public QuestType type() { return type; }
    public Material material() { return material; }
    public int target() { return target; }
    public int progress() { return progress; }
    public boolean isComplete() { return progress >= target; }

    public int addProgress(int amount) {
        int oldProgress = progress;
        progress = Math.min(target, progress + amount);
        return progress - oldProgress;
    }

    public String description() {
        return switch (type) {
            case BREAK_BLOCKS -> "Break " + target + " " + displayMaterial() + " blocks";
            case MINE_ITEMS -> "Mine " + target + " " + displayMaterial() + "";
            case CRAFT_ITEMS -> "Craft " + target + " " + displayMaterial() + "";
            case KILL_MOBS -> "Defeat " + target + " " + displayMaterial() + "";
            case FISH_ITEMS -> "Catch " + target + " fish";
        };
    }

    private String displayMaterial() {
        return material.name().toLowerCase().replace('_', ' ');
    }
}
