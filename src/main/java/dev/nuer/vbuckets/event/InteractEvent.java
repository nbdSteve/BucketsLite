package dev.nuer.vbuckets.event;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import dev.nuer.vbuckets.methods.BlockGenerators;
import dev.nuer.vbuckets.methods.BlockWorldBorderCheck;
import dev.nuer.vbuckets.methods.CheckPlayerDirection;
import dev.nuer.vbuckets.support.Factions;
import dev.nuer.vbuckets.support.WorldGuard;
import dev.nuer.vbuckets.vBuckets;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Interact event class
 */
public class InteractEvent implements Listener {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();
    //Get the server economy
    private Economy econ = vBuckets.getEconomy();
    //Hashmap for players placing buckets
    private HashMap<UUID, Long> playersUsingBuckets = new HashMap<>();
    //Store the amount that the player is going to pay
    private double totalBucketCost;
    //Store the players active message task
    private BukkitTask playerMessageTask;

    /**
     * All code for the interact event is handled in this method
     *
     * @param e the event, cannot be null
     */
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        //Get the player
        Player p = e.getPlayer();
        //Get the action
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //Check that the player has a vBucket in their hand
            if (p.getItemInHand().hasItemMeta()) {
                if (p.getItemInHand().getItemMeta().hasLore()) {
                    //Store the information about the vBucket
                    ItemMeta bucketMeta = p.getItemInHand().getItemMeta();
                    List<String> bucketLore = bucketMeta.getLore();
                    String bucketType = null;
                    for (int x = 1; x <= 54; x++) {
                        String bucket = "bucket-" + x;
                        try {
                            lpf.getBuckets().getString(bucket + ".unique");
                            if (bucketLore.contains(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getBuckets().getString(bucket + ".unique")))) {
                                bucketType = bucket;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this bucket just doesn't exist.
                        }
                    }
                    if (bucketType == null) {
                        return;
                    }
                    e.setCancelled(true);
                    boolean wg = false;
                    boolean fac = false;
                    //Figure out which plugins are being used and what to support
                    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                        wg = true;
                    }
                    if (Bukkit.getPluginManager().getPlugin("MassiveCore") != null) {
                        fac = true;
                    } else if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null) {
                        fac = true;
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
                        if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                "vertical") || lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase("scaffold")) {
                            //Start the generation from this height
                            int y = e.getClickedBlock().getY() + 1;
                            //While the block being replaced is air, add these blocks to be changed
                            while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR) && y < lpf.getBuckets().getInt(bucketType + ".generation-finish-height")) {
                                if (wg && !WorldGuard.allowsBreak(p.getWorld().getBlockAt(x, y, z).getLocation())) {
                                    //Do nothing
                                } else if (fac && !Factions.canBreakBlock(p, p.getWorld().getBlockAt(x, y,
                                        z))) {
                                    //Do nothing
                                } else if (BlockWorldBorderCheck.isInsideBorder(p.getWorld().getBlockAt(x, y,
                                        z), e, p)) {
                                    //Do nothing
                                } else {
                                    blocks.add(p.getWorld().getBlockAt(x, y, z));
                                }
                                y++;
                            }
                        } else if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                "horizontal")) {
                            //Store the coordinates for the starting block
                            int y = e.getClickedBlock().getY();
                            //Get the players current direction, for the direction of the gen bucket
                            double facing = p.getLocation().getYaw();
                            //Spigot uses -180 - 180 degrees, mod this so its 0 - 360
                            facing = (facing %= 360) >= 0 ?
                                    p.getLocation().getYaw() : (p.getLocation().getYaw() + 360);
                            //Call check position method and instantiate it
                            CheckPlayerDirection cpd = new CheckPlayerDirection(facing, lpf,
                                    bucketType, x, z);
                            //Set the x, z value relative to the gen direction
                            x = cpd.getBlockX();
                            z = cpd.getBlockZ();
                            //Get the generation start point
                            int startPoint = cpd.getStartPoint();
                            //Check that the block being replaced is air
                            while (p.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)) {
                                //Moved the loop condition inside to allow for all directions
                                if ((cpd.negativeX() || cpd.negativeZ())
                                        && (startPoint < cpd.getEndPoint())) {
                                    break;
                                } else if ((cpd.positiveX() || cpd.positiveZ())
                                        && (startPoint > cpd.getEndPoint())) {
                                    break;
                                }
                                if (wg && !WorldGuard.allowsBreak(p.getWorld().getBlockAt(x, y, z).getLocation())) {
                                    //Do nothing
                                } else if (fac && !Factions.canBreakBlock(p,
                                        p.getWorld().getBlockAt(x, y,
                                        z))) {
                                    //Do nothing
                                } else if (BlockWorldBorderCheck.isInsideBorder(p.getWorld().getBlockAt(x,
                                        y, z), e, p)) {
                                    //Do nothing
                                } else {
                                    //Get the current location in the gen buckets generation and set that
                                    // block
                                    // to the desired type
                                    blocks.add(p.getWorld().getBlockAt(x, y, z));
                                }
                                //Conditions for incrementing values - bit clunky but it works
                                if (cpd.positiveX()) x++;
                                if (cpd.positiveZ()) z++;
                                if (cpd.negativeX()) x--;
                                if (cpd.negativeZ()) z--;
                                //Condition for end of generation
                                if (cpd.positiveZ() || cpd.positiveX()) {
                                    startPoint++;
                                } else {
                                    startPoint--;
                                }
                            }
                        }
                        //Code for generating the actual blocks, this references the methods from the
                        // BlockGenerators class
                        //Figure out what kind of bucket it is
                        if (lpf.getBuckets().getBoolean(bucketType + ".generates-gravity-blocks")) {
                            if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                    "scaffold")) {
                                //Generate blocks relevant to that gen style
                                BlockGenerators.scaffoldGen(bucketType, blocks, pl, lpf);
                            } else {
                                BlockGenerators.gravityGen(bucketType, blocks, pl, lpf);
                            }
                        } else if (lpf.getBuckets().getString(bucketType + ".type").equalsIgnoreCase("mixed")) {
                            if (lpf.getBuckets().getString(bucketType + ".direction").equalsIgnoreCase(
                                    "scaffold")) {
                                BlockGenerators.scaffoldMixedGen(bucketType, blocks, pl, lpf);
                            } else {
                                BlockGenerators.mixedGen(bucketType, blocks, pl, lpf);
                            }
                        } else {
                            BlockGenerators.genericGen(bucketType, blocks, pl, lpf);
                        }
                        //Withdraw the money from the players account
                        if (blocks.size() > 0) {
                            updateCooldown(p, price);
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("insufficient-funds")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%bal%"
                                    , String.valueOf(econ.getBalance(p))).replace("%cost%",
                                    String.valueOf(price)));
                        }
                    }
                }
            }
        }
    }

    /**
     * Void method to add the player to the generating hashmap and send a delayed message
     *
     * @param player   the player generating
     * @param genPrice the price for each gen bucket
     */
    private void updateCooldown(Player player, double genPrice) {
        //Withdraw the money from the players account
        econ.withdrawPlayer(player, genPrice);
        //Increment the total withdrawal amount
        totalBucketCost += genPrice;
        //If the hashmap contains the player, remove them and update it.
        if (playersUsingBuckets.containsKey(player.getUniqueId())) {
            playersUsingBuckets.remove(player.getUniqueId());
            //Cancel the existing message task
            playerMessageTask.cancel();
            //Set the variable to null so it can be used again
            playerMessageTask = null;
        } else {
            /*
            If they are not in the map then they have just started generating, send them this
            message to alert.
             */
            for (String line : lpf.getMessages().getStringList("start-generation")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        line).replace("%price%", String.valueOf(genPrice)));
            }
        }
        //Add the player to the hashmap with the delay cooldown
        playersUsingBuckets.put(player.getUniqueId(),
                System.currentTimeMillis() + (lpf.getConfig().getInt("message-delay") * 10));
        //Send the delayed message call
        delayedMessage(player, genPrice);
    }

    /**
     * Send the price message to a player after the specified delay
     *
     * @param player the player to send the message to
     * @param price  the price of each bucket
     */
    private void delayedMessage(Player player, double price) {
        //Set our class variable to this
        playerMessageTask = new BukkitRunnable() {
            @Override
            public void run() {
                //Check to make sure the player is in the map
                if (playersUsingBuckets.containsKey(player.getUniqueId())) {
                    /*
                    See if they have stopped genning by checking cooldown, if they are still
                    going then their cooldown will be > 0.
                     */
                    if (playersUsingBuckets.get(player.getUniqueId()) - System.currentTimeMillis() <= 0) {
                        //Send the message
                        for (String line : lpf.getMessages().getStringList("bucket-use")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)
                                    .replace("%price%", String.valueOf(totalBucketCost))
                                    .replace("%bucketsUsed%", String.valueOf((int) (totalBucketCost / price))));
                        }
                        //Reset the class variable for later use
                        resetTotalBucketCost();
                        //Remove them from the hashmap
                        playersUsingBuckets.remove(player.getUniqueId());
                    }
                }
            }
        }.runTaskLater(pl, lpf.getConfig().getInt("message-delay"));
    }

    /**
     * Can't reference the variable in a runnable, therefore make a method to reset it.
     */
    private void resetTotalBucketCost() {
        this.totalBucketCost = 0;
    }
}