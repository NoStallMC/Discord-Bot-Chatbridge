package com.johnymuffin.beta.discordchatbridge;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.Arrays;

public class Config extends Configuration {


    public Config(Plugin plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
        this.reload();
    }

    private void write() {
        //Main
        generateConfigOption("config-version", 1);
        //Setting
        generateConfigOption("server-name", "BetaServer");
        generateConfigOption("channel-id", "id");
        generateConfigOption("bot-command-channel-info", "This feature limits all Discord commands so that they can only be used in a specific channel.");
        generateConfigOption("bot-command-channel-id", "id");
        generateConfigOption("bot-command-channel-enabled", false);
        generateConfigOption("presence-player-count", true);
        generateConfigOption("presence-message", "{servername} With {onlineCount} Players");
        generateConfigOption("online-command-enabled", true);
        generateConfigOption("message.discord-chat-message", "&b[D] &f%prefix%%messageAuthor%: %message%");
        generateConfigOption("message.game-chat-message", "**%messageAuthor%**: %message%");
        generateConfigOption("message.join-message", "**%username%** Has Joined The Game [%onlineCount%/%maxCount%]");
        generateConfigOption("message.quit-message", "**%username%** Has Quit The Game [%onlineCount%/%maxCount%]");
        generateConfigOption("message.require-link", "Sorry, in order to chat from Discord, you must link your in-game account to Discord. You can begin this process by running !link in the bot commands channel.");
        generateConfigOption("message.allow-chat-colors", true);
        generateConfigOption("message.use-displayname", true);
        //Shutdown & Starting messages
        generateConfigOption("system.shutdown-message.enable", true);
        generateConfigOption("system.shutdown-message.message", ":no_entry_sign: {servername} is shutting down.");
        generateConfigOption("system.starting-message.enable", true);
        generateConfigOption("system.starting-message.message", ":white_check_mark: {servername} is starting.");
        //Discord-Authentication
        generateConfigOption("authentication.enabled", false);
        generateConfigOption("authentication.discord.only-allow-linked-users", true);
        generateConfigOption("authentication.discord.use-in-game-names-if-available", true);
        //JohnyPerms Prefix Support
        generateConfigOption("johnyperms-prefix-support.enabled", false);
        generateConfigOption("johnyperms-prefix-support.info", "This option when enabled will display a users prefix from JohnyPerms in front of their name in chat when they send a message from Discord.");
        //Webhook
        generateConfigOption("webhook.use-webhook", false);
        generateConfigOption("webhook.url", "url");
        generateConfigOption("webhook.info", "This option when configured with webhook and enabled allows for messages posted by the bot to use a players avatar and username.");
        //Server Shell
        generateConfigOption("server-shell.enabled", false);
        generateConfigOption("server-shell.shell-channel-id", "id");
        generateConfigOption("server-shell.allowed-users", Arrays.asList("id1", "id2"));
        generateConfigOption("server-shell.info", "If enabled, allows execution of server commands from Discord. Be VERY careful with this.");
        //Blacklist
        generateConfigOption("blacklist", false);
    }

    private void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End


    private void reload() {
        this.load();
        this.write();
        this.save();
    }
}
