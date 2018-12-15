package com.nbdsteve.vbuckets.event;

import com.nbdsteve.vbuckets.file.LoadProvidedFiles;
import com.nbdsteve.vbuckets.vBuckets;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class InteractEvent implements Listener {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();
    //Get the server economy
    private Economy econ = vBuckets.getEconomy();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        //Get the player
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (p.getItemInHand().hasItemMeta()) {
                if (p.getItemInHand().getItemMeta().hasLore()) {
                    ItemMeta bucketMeta = p.getItemInHand().getItemMeta();
                    List<String> bucketLore = bucketMeta.getLore();
                    String bucketType = null;
                    for (int x = 1; x < 27; x++) {
                        String bucket = "bucket-" + String.valueOf(x);
                        try {
                            lpf.getBuckets().getString(bucket + ".unique");
                            if (bucketLore.contains(ChatColor.translateAlternateColorCodes('&', lpf.getBuckets().getString(bucket + ".unique")))) {
                                bucketType = bucket;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this bucket just doesn't exist.
                        }
                    }
                    if (bucketType == null) {
                        return;
                    }
                    double price = lpf.getBuckets().getDouble(bucketType + ".price-per-use");
                    if (econ.getBalance(p) >= price) {
                        int x = e.getClickedBlock().getX();
                        int z = e.getClickedBlock().getZ();
                        int y = 255;
                        while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) && y > 0) {
                            p.getWorld().getBlockAt(x, y, z).setType(Material.valueOf(lpf.getBuckets().getString(bucketType + ".type").toUpperCase()));
                            y--;
                            pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 20L);
                        }
                    }
                }
            }
        }
    }
}
