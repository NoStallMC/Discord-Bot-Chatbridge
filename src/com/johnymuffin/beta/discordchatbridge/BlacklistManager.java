package com.johnymuffin.beta.discordchatbridge;

import org.bukkit.util.config.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BlacklistManager {
	@SuppressWarnings("unused")
	private DiscordChatBridge plugin;
    private Configuration blacklistConfig;
    private File blacklistFile;

    public BlacklistManager(DiscordChatBridge plugin) {
        this.plugin = plugin;
        File pdir = new File(plugin.getDataFolder().getParentFile(), "DiscordShell");
        blacklistFile = new File(pdir, "blacklist.yml");
        if (!blacklistFile.exists()) {
            createDefaultBlacklistFile();
        }
        blacklistConfig = new Configuration(blacklistFile);
        loadBlacklistConfig();
    }
    private void createDefaultBlacklistFile() {
        try {
            if (blacklistFile.createNewFile()) {
                System.out.println("Created default blacklist.yml file.");
                blacklistConfig = new Configuration(blacklistFile);
                saveDefaults();
            }
        } catch (IOException e) {
            System.out.println("Failed to create blacklist.yml: " + e.getMessage());
        }
    }
    private void saveDefaults() {
        blacklistConfig.setProperty("blacklisted-commands", Arrays.asList("command1", "command2"));
        blacklistConfig.save();
    }
    private void loadBlacklistConfig() {
        if (blacklistConfig != null) {
            blacklistConfig.load();
        }
        // Debug output to list all blacklisted commands
        List<String> blacklistedCommands = getBlacklistedCommands();
        if (blacklistedCommands.isEmpty()) {
            System.out.println("No blacklisted commands found.");
        } else {
            System.out.println("Loaded blacklisted commands:");
            for (String command : blacklistedCommands) {
                System.out.println(" - " + command);
            }
        }
    }
    @SuppressWarnings("unchecked")
	public List<String> getBlacklistedCommands() {
        Object property = blacklistConfig.getProperty("blacklisted-commands");
        if (property instanceof List) {
            return (List<String>) property;
        }
        return Arrays.asList();
    }
    public boolean isCommandBlacklisted(String command) {
        List<String> blacklistedCommands = getBlacklistedCommands();
        return blacklistedCommands.contains(command);
    }
}
