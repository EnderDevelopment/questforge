package com.enderdevelopment.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class QuestManager {
    private static final int QUEST_COUNT = 3;
    private final Main plugin;
    private final File file;
    private final YamlConfiguration data;
    private final Map<UUID, List<Quest>> playerQuests = new HashMap<>();
    private final Random random = new Random();
    private final Map<QuestType, Material[]> materials = new EnumMap<>(QuestType.class);

    public QuestManager(Main plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.data = YamlConfiguration.loadConfiguration(file);
        materials.put(QuestType.BREAK_BLOCKS, new Material[] {Material.OAK_LOG, Material.COBBLESTONE, Material.DIRT, Material.SAND});
        materials.put(QuestType.MINE_ITEMS, new Material[] {Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE});
        materials.put(QuestType.CRAFT_ITEMS, new Material[] {Material.BREAD, Material.TORCH, Material.CHEST, Material.IRON_PICKAXE, Material.ARROW});
        materials.put(QuestType.KILL_MOBS, new Material[] {Material.ROTTEN_FLESH, Material.BONE, Material.STRING, Material.GUNPOWDER, Material.SLIME_BALL});
        materials.put(QuestType.FISH_ITEMS, new Material[] {Material.COD, Material.SALMON});
    }

    public synchronized void load(UUID uuid) {
        if (playerQuests.containsKey(uuid)) return;
        List<Quest> quests = new ArrayList<>();
        ConfigurationSection section = data.getConfigurationSection("players." + uuid);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String path = section.getCurrentPath() + "." + key;
                QuestType type = parseType(data.getString(path + ".type"));
                Material material = parseMaterial(data.getString(path + ".material"));
                int target = data.getInt(path + ".target", 1);
                int progress = data.getInt(path + ".progress", 0);
                if (type != null && material != null && target > 0) quests.add(new Quest(type, material, target, progress));
            }
        }
        while (quests.size() < QUEST_COUNT) quests.add(generateQuest());
        playerQuests.put(uuid, quests);
        savePlayer(uuid);
    }

    public synchronized List<Quest> getQuests(UUID uuid) {
        load(uuid);
        return List.copyOf(playerQuests.get(uuid));
    }

    public synchronized void reroll(UUID uuid) {
        load(uuid);
        List<Quest> quests = playerQuests.get(uuid);
        quests.clear();
        for (int i = 0; i < QUEST_COUNT; i++) quests.add(generateQuest());
        savePlayer(uuid);
    }

    public synchronized void addProgress(Player player, QuestType type, Material material, int amount) {
        load(player.getUniqueId());
        List<Quest> quests = playerQuests.get(player.getUniqueId());
        for (int index = 0; index < quests.size(); index++) {
            Quest quest = quests.get(index);
            if (quest.type() == type && quest.material() == material && !quest.isComplete()) {
                quest.addProgress(amount);
                if (quest.isComplete()) {
                    int reward = quest.target() * 2;
                    int experience = quest.target() * 3;
                    player.giveExp(experience);
                    player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.EMERALD, reward));
                    player.sendMessage(Component.text("Quest complete: " + quest.description() + "! Reward: " + reward + " emeralds and " + experience + " XP.", NamedTextColor.GREEN));
                    quests.set(index, generateQuest());
                }
                savePlayer(player.getUniqueId());
                return;
            }
        }
    }

    private Quest generateQuest() {
        QuestType type = QuestType.values()[random.nextInt(QuestType.values().length)];
        Material material = materials.get(type)[random.nextInt(materials.get(type).length)];
        int target = switch (type) {
            case BREAK_BLOCKS, MINE_ITEMS -> 8 + random.nextInt(13);
            case CRAFT_ITEMS, FISH_ITEMS -> 3 + random.nextInt(8);
            case KILL_MOBS -> 4 + random.nextInt(9);
        };
        return new Quest(type, material, target, 0);
    }

    public synchronized void save() {
        for (UUID uuid : playerQuests.keySet()) savePlayer(uuid);
        try { data.save(file); } catch (IOException exception) { plugin.getLogger().warning("Could not save quests: " + exception.getMessage()); }
    }

    private void savePlayer(UUID uuid) {
        List<Quest> quests = playerQuests.get(uuid);
        String base = "players." + uuid;
        data.set(base, null);
        for (int index = 0; index < quests.size(); index++) {
            Quest quest = quests.get(index);
            String path = base + "." + index;
            data.set(path + ".type", quest.type().name());
            data.set(path + ".material", quest.material().name());
            data.set(path + ".target", quest.target());
            data.set(path + ".progress", quest.progress());
        }
        try { data.save(file); } catch (IOException exception) { plugin.getLogger().warning("Could not save quests: " + exception.getMessage()); }
    }

    private QuestType parseType(String value) { try { return value == null ? null : QuestType.valueOf(value); } catch (IllegalArgumentException exception) { return null; } }
    private Material parseMaterial(String value) { try { return value == null ? null : Material.valueOf(value); } catch (IllegalArgumentException exception) { return null; } }
}
