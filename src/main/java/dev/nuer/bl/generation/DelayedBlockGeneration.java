package dev.nuer.bl.generation;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.events.GenBucketPlaceBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Class that handles generating blocks from an Array
 */
public class DelayedBlockGeneration {

    /**
     * Generates the blocks for the specified gen bucket
     *
     * @param bucket GenBucket, the bucket to generate
     */
    public static void generate(GenBucket bucket) {
        //Create a new Bukkit task to generate the blocks and assign it to the gen bucket
        bucket.setGenerationTask(new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                //Verify that there are still blocks to generate
                if (bucket.getBlocksToGenerate() == null
                        || bucket.isGenerationStopped()
                        || index >= bucket.getBlocksToGenerate().size() - 1) {
                    //Remove it from the list of buckets actively generating once the generation has
                    // finished
                    GenBucket.bucketsActivelyGenerating.remove(bucket.getBucketInstance());
                    //Cancel the task
                    cancel();
                }
                if (!bucket.isGenerationPaused()) {
                    //Store the next block from the array
                    //Check that the next block is AIR, if it isn't check if the bucket is pseudo
                    if (!bucket.getOwner().getWorld().getBlockAt(bucket.getBlocksToGenerate().get(index).getLocation()).getType().equals(Material.AIR)) {
                        //If the bucket is not sudo cancel the task
                        if (!bucket.isPseudo()) {
                            GenBucket.bucketsActivelyGenerating.remove(bucket.getBucketInstance());
                            cancel();
                        }
                        index++;
                    } else {
                        //Have to run this code block sync because it includes methods that aren't thread safe
                        Bukkit.getScheduler().runTask(BucketsLite.instance, () -> {
                            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(bucket.getBlocksToGenerate().get(index), bucket.getOwner());
                            Bukkit.getPluginManager().callEvent(blockBreakEvent);
                            if (!blockBreakEvent.isCancelled()) {
                                blockBreakEvent.setCancelled(true);
                                //Call custom events to handle block generation
                                Bukkit.getPluginManager().callEvent(new GenBucketPlaceBlockEvent(bucket.getBucketInstance(), bucket.getBlocksToGenerate().get(index)));
                            } else {
                                if (!bucket.isPseudo()) {
                                    GenBucket.bucketsActivelyGenerating.remove(bucket.getBucketInstance());
                                    cancel();
                                }
                            }
                            index++;
                        });
                    }
                }
            }
            //Run the task with the buckets delay
        }.runTaskTimerAsynchronously(BucketsLite.instance, 0L, bucket.getDelay()));
    }
}