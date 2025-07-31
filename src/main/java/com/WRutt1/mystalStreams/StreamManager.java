package com.WRutt1.mystalStreams;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class StreamManager {
    private final MystalStreams plugin;
    private final Database database;
    private final FileConfiguration config;

    public StreamManager(MystalStreams plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        this.config = plugin.getConfig();
    }

    public boolean isStreaming(UUID uuid) {
        return database.isStreaming(uuid);
    }

    public void startStream(Player player, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        if (!URLValidator.isValidStreamUrl(url)) {
            player.sendMessage(getMessage("invalid_url"));
            return;
        }

        UUID uuid = player.getUniqueId();
        database.addActiveStream(uuid, url);

        sendAnnounceMessage(player, url);

        String command = config.getString("commands.on_start", "say Стрим начат: {player}")
                .replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public void stopStream(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isStreaming(uuid)) {
            player.sendMessage(getMessage("not_streaming"));
            return;
        }

        database.removeActiveStream(uuid);

        String command = config.getString("commands.on_stop", "say Стрим завершен: {player}")
                .replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        player.sendMessage(getMessage("stream_off"));
    }

    private void sendAnnounceMessage(Player player, String url) {
        String rawMessage = config.getString("messages.announce", "&eИгрок {player} начал стрим!")
                .replace("{player}", player.getName());

        TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', rawMessage));

        TextComponent link = new TextComponent(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "[ПЕРЕЙТИ]");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.GRAY + "Нажмите, чтобы перейти на стрим")));

        message.addExtra(" ");
        message.addExtra(link);

        Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(message));
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages." + key, ""));
    }

    public boolean hasCooldownBypass(Player player) {
        return player.hasPermission("mystalstreams.nocooldown");
    }
}