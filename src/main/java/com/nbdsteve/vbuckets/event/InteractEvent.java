package com.nbdsteve.vbuckets.event;

import com.nbdsteve.vbuckets.file.LoadProvidedFiles;
import com.nbdsteve.vbuckets.vBuckets;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class InteractEvent implements Listener {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();
    //Get the server economy
    private Economy econ = vBuckets.getEconomy();

    private int taskid;

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
                    //Store the price for the gen bucket
                    double price = lpf.getBuckets().getDouble(bucketType + ".price-per-use");
                    if (econ.getBalance(p) >= price) {
                        //Store the blocks that are going to be changed by the gen bucket
                        ArrayList<Block> blocks = new ArrayList<>();
                        //Store the bucket type as a final variable
                        final String bt = bucketType;
                        //Store the initial blocks coordinates
                        int x = e.getClickedBlock().getX();
                        int z = e.getClickedBlock().getZ();
                        //Code for the vertical bucket
                        if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase("vertical")) {
                            //Start the generation from this height
                            int y = lpf.getBuckets().getInt(bucketType + ".generation-start-height");
                            //While the block being replaced is air, add these blocks to be changed
                            while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) && y > 0) {
                                blocks.add(p.getWorld().getBlockAt(x, y, z));
                                y--;
                            }
                        } else if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase("horizontal")) {
                            //Store the coordinates for the starting block
                            int y = e.getClickedBlock().getY();
                            int end = x + (lpf.getBuckets().getInt(bucketType + ".horizontal-gen-length"));
                            int start = x + 1;
                            //Check that the block being replaced is air
                            while (p.getWorld().getBlockAt(start, y, z).getType().equals(Material.AIR) && start < end) {
                                //Get the current location in the gen buckets generation and set that block to the desired type
                                blocks.add(p.getWorld().getBlockAt(start, y, z));
                                start++;
                            }
                        }
                        //Runnable to do the block generation
                        this.taskid = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
                            int index = 0;
                            @Override
                            public void run() {
                                if (index < blocks.size()) {
                                    //If the block isn't air then stop generating, do this check twice
                                    if (!blocks.get(index).getType().equals(Material.AIR)) {
                                        stopTask();
                                    }
                                    blocks.get(index).setType(Material.valueOf(lpf.getBuckets().getString(bt + ".type").toUpperCase()));
                                    index++;
                                } else {
                                    stopTask();
                                }
                            }
                        }, 0L, lpf.getConfig().getInt("delay"));
                        //Withdraw the money from the players account
                        if (blocks.size() <= 0) {
                            for (String line : lpf.getMessages().getStringList("bucket-use")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%price%", String.valueOf(price)));
                            }
                            econ.withdrawPlayer(p, price);
                        }
                    }
                }
            }
        }
    }

    private void stopTask() {
        pl.getServer().getScheduler().cancelTask(taskid);
    }
}
