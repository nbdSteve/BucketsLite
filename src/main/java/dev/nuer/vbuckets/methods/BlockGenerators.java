package dev.nuer.vbuckets.methods;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Class containing all of the methods to generate blocks for the gen buckets
 */
public class BlockGenerators {

    /**
     * Method for generic block generation, this is for vertical and horizontal buckets
     *
     * @param bucketType     string the bucket number from buckets.yml
     * @param blocksToChange the array list of blocks to gen
     * @param pl             providing plugin
     * @param lpf            the files for the plugin
     */
    public static void genericGen(String bucketType, ArrayList<Block> blocksToChange, Plugin pl,
                                  LoadProvidedFiles lpf) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index <= blocksToChange.size()) {
                    //If the block isn't air then stop generating, do this check twice
                    if (blocksToChange.get(index).getType().equals(Material.AIR)) {
                        blocksToChange.get(index).setType(Material.valueOf(lpf.getBuckets().getString(bucketType + ".type").toUpperCase()));
                        index++;
                    } else {
                        //If the block isn't air cancel the task
                        this.cancel();
                    }
                } else {
                    //When the generation is scheduled to finish, cancel the task
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, lpf.getBuckets().getInt(bucketType + ".delay"));
    }

    /**
     * Method for gravity block generation, this is for sand and gravel vertical buckets
     *
     * @param bucketType     string the bucket number from buckets.yml
     * @param blocksToChange the array list of blocks to gen
     * @param pl             providing plugin
     * @param lpf            the files for the plugin
     */
    public static void gravityGen(String bucketType, ArrayList<Block> blocksToChange, Plugin pl,
                                  LoadProvidedFiles lpf) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < blocksToChange.size()) {
                    //For gravity blocks you have to place the block at the top and
                    // wait for it to fall
                    //then place the next block until you reach the top of the stack
                    if (blocksToChange.get(0).getType().equals(Material.AIR)) {
                        blocksToChange.get(0).setType(Material.valueOf(lpf.getBuckets().getString(bucketType + ".type").toUpperCase()));
                        index++;
                    } else {
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, lpf.getBuckets().getInt(bucketType + ".delay"));
    }

    /**
     * Method for scaffold block generation, this is for the scaffold buckets
     *
     * @param bucketType     string the bucket number from buckets.yml
     * @param blocksToChange the array list of blocks to gen
     * @param pl             providing plugin
     * @param lpf            the files for the plugin
     */
    public static void scaffoldGen(String bucketType, ArrayList<Block> blocksToChange, Plugin pl,
                                   LoadProvidedFiles lpf) {
        new BukkitRunnable() {
            int index = blocksToChange.size() - 1;

            @Override
            public void run() {
                if (index >= 0) {
                    //If the block isn't air then stop generating, do this check twice
                    if (blocksToChange.get(index).getType().equals(Material.AIR)) {
                        blocksToChange.get(index).setType(Material.valueOf(lpf.getBuckets().getString(bucketType + ".type").toUpperCase()));
                        index--;
                    } else {
                        //If the block isn't air cancel the task
                        this.cancel();
                    }
                } else {
                    //When the generation is scheduled to finish, cancel the task
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, lpf.getBuckets().getInt(bucketType + ".delay"));
    }

    /**
     * Method for the mixed scaffold buckets, this needs to be a seperate method because of the random
     * number generation to create the pattern
     *
     * @param bucketType     string the bucket number from buckets.yml
     * @param blocksToChange the array list of blocks to gen
     * @param pl             providing plugin
     * @param lpf            the files for the plugin
     */
    public static void scaffoldMixedGen(String bucketType, ArrayList<Block> blocksToChange, Plugin pl,
                                        LoadProvidedFiles lpf) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < blocksToChange.size()) {
                    int random = (int) (Math.random() * 2 + 1);
                    //If the block isn't air then stop generating, do this check twice
                    if (blocksToChange.get(index).getType().equals(Material.AIR)) {
                        if (random == 1) {
                            blocksToChange.get(index).setType(Material.SAND);
                        } else if (random == 2) {
                            blocksToChange.get(index).setType(Material.GRAVEL);
                        }
                        index++;
                    } else {
                        //If the block isn't air cancel the task
                        this.cancel();
                    }
                } else {
                    //When the generation is scheduled to finish, cancel the task
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, lpf.getBuckets().getInt(bucketType + ".delay"));
    }

    /**
     * Method for mixed block generation, this is for vertical mixed buckets
     *
     * @param bucketType     string the bucket number from buckets.yml
     * @param blocksToChange the array list of blocks to gen
     * @param pl             providing plugin
     * @param lpf            the files for the plugin
     */
    public static void mixedGen(String bucketType, ArrayList<Block> blocksToChange, Plugin pl,
                                LoadProvidedFiles lpf) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                int random = (int) (Math.random() * 2 + 1);
                if (index < blocksToChange.size()) {
                    if (blocksToChange.get(0).getType().equals(Material.AIR)) {
                        if (random == 1) {
                            blocksToChange.get(0).setType(Material.SAND);
                        } else if (random == 2) {
                            blocksToChange.get(0).setType(Material.GRAVEL);
                        }
                        index++;
                    } else {
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(pl, 0L, lpf.getBuckets().getInt(bucketType + ".delay"));
    }
}