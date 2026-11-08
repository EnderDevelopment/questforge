package com.enderdevelopment.plugin;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class QuestListener implements Listener {
    private final QuestManager manager;

    public QuestListener(QuestManager manager) { this.manager = manager; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) { manager.load(event.getPlayer().getUniqueId()); }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        QuestType type = isOre(material) ? QuestType.MINE_ITEMS : QuestType.BREAK_BLOCKS;
        manager.addProgress(event.getPlayer(), type, material, 1);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player player && event.getRecipe() != null) {
            manager.addProgress(player, QuestType.CRAFT_ITEMS, event.getRecipe().getResult().getType(), event.getRecipe().getResult().getAmount());
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Material marker = mobMaterial(event.getEntityType());
            if (marker != null) manager.addProgress(event.getEntity().getKiller(), QuestType.KILL_MOBS, marker, 1);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof org.bukkit.entity.Item item) {
            manager.addProgress(event.getPlayer(), QuestType.FISH_ITEMS, item.getItemStack().getType(), 1);
        }
    }

    private boolean isOre(Material material) { return material.name().endsWith("_ORE") || material == Material.ANCIENT_DEBRIS; }

    private Material mobMaterial(EntityType type) {
        return switch (type) {
            case ZOMBIE -> Material.ROTTEN_FLESH;
            case SKELETON -> Material.BONE;
            case SPIDER -> Material.STRING;
            case CREEPER -> Material.GUNPOWDER;
            case SLIME -> Material.SLIME_BALL;
            default -> null;
        };
    }
}
