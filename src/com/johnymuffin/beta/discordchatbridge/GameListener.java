package com.johnymuffin.beta.discordchatbridge;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class GameListener extends PlayerListener {
    private DiscordChatBridge plugin;

    public GameListener(DiscordChatBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        String chatMessage = plugin.getConfig().getConfigString("message.join-message");
        if (plugin.getConfig().getConfigBoolean("message.use-displayname")) {
            chatMessage = chatMessage.replace("%username%", sanitizeDisplayName(event.getPlayer().getDisplayName()));
        }
        else {
            chatMessage = chatMessage.replace("%username%", event.getPlayer().getName());
        }
        chatMessage = chatMessage.replace("%onlineCount%", String.valueOf(Bukkit.getServer().getOnlinePlayers().length));
        chatMessage = chatMessage.replace("%maxCount%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
        plugin.getDiscordCore().getDiscordBot().discordSendToChannel(plugin.getConfig().getConfigString("channel-id"), chatMessage);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        String chatMessage = plugin.getConfig().getConfigString("message.quit-message");
        if (plugin.getConfig().getConfigBoolean("message.use-displayname")) {
            chatMessage = chatMessage.replace("%username%", sanitizeDisplayName(event.getPlayer().getDisplayName()));
        }
        else {
            chatMessage = chatMessage.replace("%username%", event.getPlayer().getName());
        }
        chatMessage = chatMessage.replace("%onlineCount%", String.valueOf(Bukkit.getServer().getOnlinePlayers().length - 1));
        chatMessage = chatMessage.replace("%maxCount%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
        plugin.getDiscordCore().getDiscordBot().discordSendToChannel(plugin.getConfig().getConfigString("channel-id"), chatMessage);
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (plugin.getConfig().getConfigBoolean("webhook.use-webhook")) {
            final DiscordWebhook webhookMessage = new DiscordWebhook(plugin.getConfig().getConfigString("webhook.url"));
            if (plugin.getConfig().getConfigBoolean("message.use-displayname")) {
                webhookMessage.setUsername(sanitizeDisplayName(event.getPlayer().getDisplayName()));
            }
            else {
                webhookMessage.setUsername(event.getPlayer().getName());
            }
            webhookMessage.setContent(sanitizeMessage(event.getMessage()));
            webhookMessage.setAvatarUrl("http://minotar.net/helm/" + event.getPlayer().getName() + "/100.png");
            webhookMessage.setTts(false);
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
                try {
                    webhookMessage.execute();
                } catch (IOException exception) {
                    plugin.logger(Level.INFO, "Failed to send message through webhook to Discord chat channel: " + exception + " : " + exception.getMessage());
                }
            }, 0L);
        } else {
            String chatMessage = plugin.getConfig().getConfigString("message.game-chat-message");
            if (plugin.getConfig().getConfigBoolean("message.use-displayname")) {
                chatMessage = chatMessage.replace("%messageAuthor%", sanitizeDisplayName(event.getPlayer().getDisplayName()));
            }
            else {
                chatMessage = chatMessage.replace("%messageAuthor%", event.getPlayer().getName());
            }
            chatMessage = chatMessage.replace("%message%", sanitizeMessage(event.getMessage()));
            plugin.getDiscordCore().getDiscordBot().discordSendToChannel(plugin.getConfig().getConfigString("channel-id"), chatMessage);
        }
    }

    // Method to sanitize player display name, removing Minecraft color codes and formatting
    public String sanitizeDisplayName(String displayName) {
        if (displayName == null) return "";
        return displayName.replaceAll("§[0-9a-fA-Fklmnor]", "").trim();    // Remove color codes and formatting codes (e.g., §c, §f, §l, §r, etc.)
    }

    // Sanitizing method to clean up the message (removing @everyone, @here, and Minecraft color codes)
    public String sanitizeMessage(String chatMessage) {
        chatMessage = chatMessage.replaceAll("§[0-9a-fA-Fklmnor]", "");
        chatMessage = chatMessage.replaceAll(Pattern.quote("@everyone"), " ");
        chatMessage = chatMessage.replaceAll(Pattern.quote("@here"), " ");
        chatMessage = chatMessage.replaceAll(Pattern.quote("@"), " ");
        return chatMessage;
    }
}
