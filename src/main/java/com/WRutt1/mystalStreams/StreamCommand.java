package com.WRutt1.mystalStreams;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class StreamCommand implements CommandExecutor, TabCompleter {
    private final MystalStreams plugin;
    private final StreamManager streamManager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public StreamCommand(MystalStreams plugin, StreamManager streamManager) {
        this.plugin = plugin;
        this.streamManager = streamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mystalstreams.use")) {
            player.sendMessage(streamManager.getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Используйте: /stream <url> или /stream off");
            return true;
        }

        if ("off".equalsIgnoreCase(args[0])) {
            streamManager.stopStream(player);
            return true;
        }

        if (!streamManager.hasCooldownBypass(player)) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                long secondsLeft = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                if (secondsLeft > 0) {
                    player.sendMessage(streamManager.getMessage("cooldown")
                            .replace("{time}", String.valueOf(secondsLeft)));
                    return true;
                }
            }
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 3600000);
        }

        if (!URLValidator.isValidStreamUrl(args[0])) {
            player.sendMessage(streamManager.getMessage("invalid_url"));
            return true;
        }

        streamManager.startStream(player, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("off"), new ArrayList<>());
        }
        return Collections.emptyList();
    }
}