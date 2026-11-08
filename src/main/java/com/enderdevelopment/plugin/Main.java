package com.enderdevelopment.plugin;

import java.io.File;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private QuestManager questManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().warning("Could not create the plugin data folder.");
        }
        questManager = new QuestManager(this, new File(getDataFolder(), "quests.yml"));
        QuestListener listener = new QuestListener(questManager);
        Bukkit.getPluginManager().registerEvents(listener, this);
        Objects.requireNonNull(getCommand("quests")).setExecutor(new QuestCommand(questManager));
        Objects.requireNonNull(getCommand("quests")).setTabCompleter(new QuestCommand(questManager));
    }

    @Override
    public void onDisable() {
        if (questManager != null) {
            questManager.save();
        }
    }
}
