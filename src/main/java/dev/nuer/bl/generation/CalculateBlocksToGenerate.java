package dev.nuer.bl.generation;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.PlayerDirectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

/**
 * Class that handles calculating the blocks that need to be generated
 */
public class CalculateBlocksToGenerate {

    /**
     * Decodes which calculation method to use based on the buckets attributes and type
     *
     * @param bucket GenBucket, the gen bucket to generate
     * @return ArrayList<Block>, the blocks to generate
     */
    public static ArrayList<Block> getBlocks(GenBucket bucket) {
        if (bucket.getType().equalsIgnoreCase("vertical")) {
            if (bucket.getStartingBlock().getY() > 240) {
                return calculateBlocks(BlockFace.DOWN, bucket);
            }
            return calculateBlocks(BlockFace.UP, bucket);
        }
        if (bucket.getType().equalsIgnoreCase("horizontal"))
            return calculateBlocks(PlayerDirectionUtil.checkDirection(bucket.getOwner()), bucket);
        return null;
    }

    /**
     * Calculates the blocks to generate based on the block face to increment and the gen buckets attributes
     *
     * @param face   BlockFace, the face to increment
     * @param bucket GenBucket, the gen bucket to generate
     * @return ArrayList<Block>, the blocks to generate
     */
    private static ArrayList<Block> calculateBlocks(BlockFace face, GenBucket bucket) {
        ArrayList<Block> blocks = new ArrayList<>();
        //Store the maximum generation height as defined in buckets-lite.yml
        int maxHeight = FileManager.get("config").getInt("max-generation-height");
        //Run this calculation task async since it is quite a large math operation
        Bukkit.getScheduler().runTaskAsynchronously(BucketsLite.instance, () -> {
            while (blocks.size() < bucket.getGenerationLength()) {
                Block currentBlock = bucket.getStartingBlock();
                if (blocks.size() > 0) currentBlock = blocks.get(blocks.size() - 1);
                currentBlock = currentBlock.getRelative(face);
                if (currentBlock.getY() >= maxHeight) break;
                if (!currentBlock.getType().equals(Material.AIR)) {
                    if (!bucket.isPseudo()) break;
                    blocks.add(currentBlock);
                } else {
                    blocks.add(currentBlock);
                }
            }
            if (blocks.size() == 0) return;
            bucket.doGeneration();
        });
        return blocks;
    }
}