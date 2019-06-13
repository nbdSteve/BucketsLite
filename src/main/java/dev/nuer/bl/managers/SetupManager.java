package dev.nuer.bl.managers;

import dev.nuer.bl.listeners.CustomEventListener;
import dev.nuer.bl.listeners.GuiClickListener;
import dev.nuer.bl.listeners.PlayerClickListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

public class SetupManager {

    public static void setupFiles(FileManager fileManager) {
        fileManager.add("config", "buckets-lite.yml");
        fileManager.add("messages", "messages.yml");
        fileManager.add("vertical", "buckets" + File.separator + "vertical.yml");
        fileManager.add("horizontal", "buckets" + File.separator + "horizontal.yml");
    }

    public static void registerEvents(Plugin instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(new CustomEventListener(), instance);
        pm.registerEvents(new PlayerClickListener(), instance);
        pm.registerEvents(new GuiClickListener(), instance);
    }
}
