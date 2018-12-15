package com.nbdsteve.vbuckets;

import com.nbdsteve.vbuckets.command.BucketCommand;
import com.nbdsteve.vbuckets.event.InteractEvent;
import com.nbdsteve.vbuckets.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class vBuckets extends JavaPlugin {
    //Economy variable for the plugin
    private static Economy econ;
    //New LoadProvidedFiles instance
    private LoadProvidedFiles lpf;

    /**
     * Method called when the plugin starts, register all events and commands in this method
     */
    @Override
    public void onEnable() {
        getLogger().info("Thanks for using vBuckets - nbdSteve");
        if (!setupEconomy()) {
            getLogger().severe("Vault.jar not found, disabling economy features.");
        }
        //Generate all of the provided files for the plugin
        this.lpf = new LoadProvidedFiles();
        getCommand("vb").setExecutor(new BucketCommand(this));
        getCommand("gen").setExecutor(new BucketCommand(this));
        getServer().getPluginManager().registerEvents(new InteractEvent(), this);
    }

    /**
     * Method called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        getLogger().info("Thanks for using vBuckets - nbdSteve");
    }

    //Set up the economy for the plugin
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    /**
     * Get the LoadProvidedFiles instance that has been created
     *
     * @return LoadProvidedFiles instance
     */
    public LoadProvidedFiles getFiles() {
        return lpf;
    }

    /**
     * Get the servers economy
     *
     * @return econ
     */
    public static Economy getEconomy() {
        return econ;
    }
}
