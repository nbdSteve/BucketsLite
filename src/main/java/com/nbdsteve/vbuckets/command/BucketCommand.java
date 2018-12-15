package com.nbdsteve.vbuckets.command;

import com.nbdsteve.vbuckets.file.LoadProvidedFiles;
import com.nbdsteve.vbuckets.vBuckets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class BucketCommand implements CommandExecutor {
    public BucketCommand(vBuckets pl) {
        this.pl = pl;
    }

    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (c.getName().equalsIgnoreCase("gen") || c.getName().equalsIgnoreCase("vb")) {
            if (args.length == 0) {
                ItemStack bucket = new ItemStack(Material.valueOf(lpf.getBuckets().getString("bucket-1.item").toUpperCase()));
                ItemMeta bucketMeta = bucket.getItemMeta();
                List<String> bucketLore = new ArrayList<>();
                bucketMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', lpf.getBuckets().getString("bucket-1.name")));
                for (String lore : lpf.getBuckets().getStringList("bucket-1.lore")) {
                    bucketLore.add(ChatColor.translateAlternateColorCodes('&', lore));
                }
                bucketMeta.setLore(bucketLore);
                bucket.setItemMeta(bucketMeta);
                if (s instanceof Player) {
                    ((Player) s).getInventory().addItem(bucket);
                }
            }
        }
        return true;
    }
}
