package com.WRutt1.mystalStreams;

import org.bukkit.plugin.java.JavaPlugin;
import com.WRutt1.mystalStreams.listener.PlayerQuitListener;

public class MystalStreams extends JavaPlugin {
    private Database database;
    private StreamManager streamManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        database = new Database(this);
        database.initialize();

        streamManager = new StreamManager(this, database);

        getCommand("stream").setExecutor(new StreamCommand(this, streamManager));
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(streamManager), this);
    }

    @Override
    public void onDisable() {
        database.closeConnection();
    }
}