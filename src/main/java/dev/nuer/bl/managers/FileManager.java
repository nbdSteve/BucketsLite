package dev.nuer.bl.managers;

import dev.nuer.bl.utils.YamlFileUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class FileManager {
    private static HashMap<String, YamlFileUtil> files;
    private Plugin instance;

    public FileManager(Plugin instance) {
        this.instance = instance;
        files = new HashMap<>();
    }

    public void add(String name, String path) {
        files.put(name, new YamlFileUtil(path, instance));
    }

    public static YamlConfiguration get(String name) {
        if (files.containsKey(name)) return files.get(name).get();
        return null;
    }

    public static void save(String name) {
        if (files.containsKey(name)) files.get(name).save();
    }

    public static void reload() {
        for (YamlFileUtil file : files.values()) file.reload();
    }
}
