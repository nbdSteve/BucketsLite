package dev.nuer.bl;

import dev.nuer.bl.cmd.BlCmd;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.managers.SetupManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BucketsLite extends JavaPlugin {
    //Store the main plugin instance
    public static BucketsLite instance;
    //Make the plugins logger easily accessible from any class
    public static Logger LOGGER;
    //Store the servers economy
    public static Economy economy;

    @Override
    public void onEnable() {
        //Register instance
        instance = this;
        //Store the plugins logger (with prefix)
        LOGGER = instance.getLogger();
        //Load files
        SetupManager.setupFiles(new FileManager(this));
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        } else {
            LOGGER.info("Unable to find economy instance, disabling economy features.");
            economy = null;
        }
        //Register commands
        registerCommands();
        //Register events
        SetupManager.registerEvents(this);
        LOGGER.info("Thank you for using BucketsLite, if you find any problems please PM nbdSteve#0583");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Thank you for using BucketsLite, if you find any problems please PM nbdSteve#0583");
    }

    private void registerCommands() {
        getCommand("buckets-lite").setExecutor(new BlCmd());
    }
}
