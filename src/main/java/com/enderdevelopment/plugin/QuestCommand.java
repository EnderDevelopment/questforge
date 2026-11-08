package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class QuestCommand implements CommandExecutor, TabCompleter {
    private final QuestManager manager;

    public QuestCommand(QuestManager manager) { this.manager = manager; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use quests.");
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reroll")) {
            manager.reroll(player.getUniqueId());
            player.sendMessage("Your quests have been rerolled.");
            return true;
        }
        player.sendMessage("Your active quests:");
        List<Quest> quests = manager.getQuests(player.getUniqueId());
        for (int index = 0; index < quests.size(); index++) {
            Quest quest = quests.get(index);
            player.sendMessage((index + 1) + ". " + quest.description() + " [" + quest.progress() + "/" + quest.target() + "]");
        }
        player.sendMessage("Use /quests reroll to replace all quests.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return new ArrayList<>(List.of("list", "reroll"));
        return List.of();
    }
}
