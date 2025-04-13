package com.johnymuffin.beta.discordchatbridge;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import com.johnymuffin.jperms.beta.JohnyPerms;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordListener extends ListenerAdapter {
    private DiscordChatBridge plugin;

    public DiscordListener(DiscordChatBridge plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }
        if (event.getMessage().getContentRaw().isEmpty()) {
            return;
        }
        String content = event.getMessage().getContentRaw();
        if (plugin.getConfig().getConfigBoolean("server-shell.enabled") &&
        	    event.getChannel().getId().equals(plugin.getConfig().getConfigString("server-shell.shell-channel-id"))) {
        	    List<?> rawList = (List<?>) plugin.getConfig().getConfigOption("server-shell.allowed-users");
        	    List<String> allowedUsers = new ArrayList<String>();
        	    for (Object obj : rawList) {
        	        allowedUsers.add(obj.toString());
        	    }
        	    if (allowedUsers.contains(event.getAuthor().getId())) {
        	        String command = event.getMessage().getContentRaw();
                    BlacklistManager blacklist = plugin.getBlacklistManager();
                    if (blacklist.isCommandBlacklisted(content)) {
                    	if (plugin.getConfig().getConfigBoolean("blacklist")) {
                        event.getChannel().sendMessage(":no_entry_sign: This command is blacklisted and cannot be executed.").queue();
                        return;
                    	}
                    }
                    Bukkit.getLogger().info("[DiscordShell] executing command: " + command);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        ServerShellSender sender = new ServerShellSender();
                        boolean result = Bukkit.dispatchCommand(sender, content);
                        List<String> output = sender.getOutput();
                        if (!result || output.isEmpty()) {
                            event.getChannel().sendMessage(":x: Unknown or failed command: `" + content + "`").queue();
                            return;
                        }
                        StringBuilder builder = new StringBuilder(":white_check_mark: **Executed:** `" + content + "`\n```\n");
                        for (int i = 0; i < Math.min(output.size(), 10); i++) {
                            builder.append(output.get(i)).append("\n");
                        }
                        if (output.size() > 10) {
                            builder.append("... (truncated)\n");
                        }
                        builder.append("```");
                        event.getChannel().sendMessage(builder.toString()).queue();
                    });
        	        return;
        	    } else {
        	        event.getChannel().sendMessage(":no_entry_sign: You are not authorized to use the server-shell.").queue();
        	        return;
        	    }
        	}
    
        String gameBridgeChannelID = plugin.getConfig().getConfigString("channel-id");
        String[] messageCMD = event.getMessage().getContentRaw().split(" ");
        if (messageCMD[0].equalsIgnoreCase("!online") && plugin.getConfig().getConfigBoolean("online-command-enabled")) {
            if (plugin.getConfig().getConfigBoolean("bot-command-channel-enabled")) {
                if (Objects.equals(plugin.getConfig().getConfigString("bot-command-channel-id"), event.getChannel().getId())) {
                    String onlineMessage = "**The online players are:** ";
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        onlineMessage += p.getName() + ", ";
                    }
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(plugin.getConfig().getConfigString("server-name") + " Online Players", null);
                    if (Bukkit.getServer().getOnlinePlayers().length > 0) {
                        int rnd = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().length);
                        Player player = Bukkit.getServer().getOnlinePlayers()[rnd];
                        eb.setThumbnail("http://minotar.net/helm/" + player.getName() + "/100.png");
                    }
                    eb.setColor(Color.red);
                    eb.setDescription("There are currently **" + Bukkit.getServer().getOnlinePlayers().length + "** players online\n" + onlineMessage);
                    eb.setFooter("https://github.com/RhysB/Discord-Bot-Chatbridge", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
                    event.getChannel().sendMessage(eb.build()).queue();
                    return;
                }
                if (plugin.getConfig().getConfigString("bot-command-channel-id").isEmpty() || Objects.equals(plugin.getConfig().getConfigString("bot-command-channel-id"), "id")) {
                    Bukkit.getLogger().warning("You appear to have forgotten to add a channel ID. go to the config and add an ID or disable the bot command channel limiter");
                    Bukkit.getLogger().info("Will proceed like the feature is disabled.");
                    String onlineMessage = "**The online players are:** ";
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        onlineMessage += p.getName() + ", ";
                    }
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(plugin.getConfig().getConfigString("server-name") + " Online Players", null);
                    if (Bukkit.getServer().getOnlinePlayers().length > 0) {
                        int rnd = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().length);
                        Player player = Bukkit.getServer().getOnlinePlayers()[rnd];
                        eb.setThumbnail("http://minotar.net/helm/" + player.getName() + "/100.png");
                    }
                    eb.setColor(Color.red);
                    eb.setDescription("There are currently **" + Bukkit.getServer().getOnlinePlayers().length
                            + "** players online\n" + onlineMessage);
                    eb.setFooter("https://github.com/RhysB/Discord-Bot-Chatbridge",
                            "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
                    event.getChannel().sendMessage(eb.build()).queue();
                    return;
                }
            }
            if (!plugin.getConfig().getConfigBoolean("bot-command-channel-enabled")) {
                String onlineMessage = "**The online players are:** ";
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    onlineMessage += p.getName() + ", ";
                }
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(plugin.getConfig().getConfigString("server-name") + " Online Players", null);
                if (Bukkit.getServer().getOnlinePlayers().length > 0) {
                    int rnd = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().length);
                    Player player = Bukkit.getServer().getOnlinePlayers()[rnd];
                    eb.setThumbnail("http://minotar.net/helm/" + player.getName() + "/100.png");
                }
                eb.setColor(Color.red);
                eb.setDescription("There are currently **" + Bukkit.getServer().getOnlinePlayers().length
                        + "** players online\n" + onlineMessage);
                eb.setFooter("https://github.com/RhysB/Discord-Bot-Chatbridge",
                        "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }
        }

        if (event.getChannel().getId().equalsIgnoreCase(gameBridgeChannelID)) {
            String displayName = null;
            String prefix = null;
            UUID playerUUID = null;

            if (plugin.getConfig().getConfigBoolean("authentication.enabled")) {
                DiscordAuthentication authPlugin = (DiscordAuthentication) Bukkit.getServer().getPluginManager().getPlugin("DiscordAuthentication");
                if (authPlugin.getData().isDiscordIDAlreadyLinked(event.getAuthor().getId())) {
                    playerUUID = UUID.fromString(authPlugin.getData().getUUIDFromDiscordID(event.getAuthor().getId()));
                }
                if (plugin.getConfig().getConfigBoolean("authentication.discord.only-allow-linked-users")) {
                    if (!authPlugin.getData().isDiscordIDAlreadyLinked(event.getAuthor().getId())) {
                        event.getChannel().sendMessage(plugin.getConfig().getString("message.require-link")).queue();
                        return;
                    }
                }
                if (plugin.getConfig().getConfigBoolean("authentication.discord.use-in-game-names-if-available")) {
                    displayName = authPlugin.getData().getLastUsernameFromDiscordID(event.getAuthor().getId());
                }
            }

            if (this.plugin.getConfig().getConfigBoolean("johnyperms-prefix-support.enabled")) {
                if (playerUUID != null) {
                    if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
                        JohnyPerms jperms = (JohnyPerms) Bukkit.getServer().getPluginManager().getPlugin("JPerms");
                        prefix = jperms.getUser(playerUUID).getPrefix();
                        if (prefix == null) {
                            prefix = jperms.getUser(playerUUID).getGroup().getPrefix();
                        }
                    } else {
                        this.plugin.logger(Level.WARNING, "JohnyPerms prefix support is enabled but the plugin is not installed or enabled.");
                    }
                } else {
                    this.plugin.logger(Level.WARNING, "JohnyPerms prefix support is enabled but the player UUID is null. This is likely due to the DiscordAuthentication plugin not being installed or enabled.");
                }
            }

            String dmsg = event.getMessage().getContentDisplay();
            dmsg = dmsg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
            if (!plugin.getConfig().getConfigBoolean("message.allow-chat-colors")) {
                dmsg = ChatColor.stripColor(dmsg);
            }

            if (displayName == null) {
                if (event.getMember().getNickname() != null) {
                    displayName = event.getMember().getNickname();
                } else {
                    displayName = event.getAuthor().getName();
                }
            }

            if (prefix == null) {
                prefix = "";
            }
            prefix = prefix.replaceAll("(&([a-f0-9]))", "\u00A7$2");

            String chatMessage = plugin.getConfig().getConfigString("message.discord-chat-message");
            chatMessage = chatMessage.replace("%messageAuthor%", displayName);
            chatMessage = chatMessage.replace("%message%", dmsg);
            chatMessage = chatMessage.replaceAll("(&([a-f0-9]))", "\u00A7$2");
            chatMessage = chatMessage.replace("%prefix%", prefix);
            Bukkit.getServer().broadcastMessage(chatMessage);
            return;
        }
    }
}
