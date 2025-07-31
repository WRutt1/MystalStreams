package com.WRutt1.mystalStreams.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;
import com.WRutt1.mystalStreams.StreamManager;

public class PlayerQuitListener implements Listener {
    private final StreamManager streamManager;

    public PlayerQuitListener(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (streamManager.isStreaming(uuid)) {
            streamManager.stopStream(event.getPlayer());
        }
    }
}