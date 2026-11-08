# QuestForge Minecraft Quest Plugin

QuestForge is a Minecraft plugin that automatically generates persistent player quests for Paper servers, tracking block breaking, ore mining, item crafting, mob defeats, and fishing with automatic emerald and experience rewards.

## Install

1. Build the plugin with Java 21 and Maven:
   ```bash
   mvn package
   ```
2. Copy the generated `target/plugin.jar` file into your server's `plugins` directory.
3. Start or restart a Paper Minecraft 1.21.11 server.
4. Confirm that the plugin loads successfully in the server console.

## Use

1. Join the server. Each player receives three automatically generated quests.
2. Run `/quests` or `/quests list` to view active quests and progress.
3. Complete quests by performing the tracked activities:
   - Break oak logs, cobblestone, dirt, or sand.
   - Mine coal, iron, copper, redstone, or diamond ore.
   - Craft bread, torches, chests, iron pickaxes, or arrows.
   - Defeat zombies, skeletons, spiders, creepers, or slimes.
   - Catch cod or salmon.
4. Completed quests award emeralds and experience, then are immediately replaced with new generated quests.
5. Run `/quests reroll` to replace all active quests.
6. Quest progress is stored in the plugin data folder in `quests.yml`.

The `autoquests.use` permission controls access to the `/quests` command and is enabled for players by default. The command also has the `/quest` alias.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/43325d33-8c3d-4c49-ba77-196196d1a34f?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=43325d33-8c3d-4c49-ba77-196196d1a34f
