package dev.nuer.bl.bucket;

import dev.nuer.bl.generation.CalculateBlocksToGenerate;
import dev.nuer.bl.generation.DelayedBlockGeneration;
import dev.nuer.bl.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class GenBucket {
    //Store a static list of the buckets pending generation
    public static ArrayList<GenBucket> bucketsPendingGeneration = new ArrayList<>();
    //Store a static list of the buckets that are actively generating
    public static ArrayList<GenBucket> bucketsActivelyGenerating = new ArrayList<>();

    //Store if the generation is paused
    public boolean generationPaused = false;
    //Store if the generation is stopped
    public boolean generationStopped = false;
    //Store the instance of the gen bucket
    public GenBucket bucketInstance;

    //Store the player who placed the gen bucket
    private Player owner;
    //Store the type of gen bucket
    private String type;
    //Store the type of material being generated
    private Material generationMaterial;
    //Store the starting block
    private Block startingBlock;
    //Store the delay between blocks being placed
    private int delay;
    //Store the max generation length of the bucket
    private int generationLength;
    //Store the price per block generated
    private double price;
    //Store if the gen bucket is a pseudo bucket or not
    private boolean pseudo;
    //Store the generation task
    private BukkitTask generationTask;
    //Store the task ID
    private UUID generationTaskID;
    //Store the block locations that the bucket will generate to
    private ArrayList<Block> blocksToGenerate;

    public GenBucket(Player player, NBTItem item, Block block) {
        //Set all of the bucket attributes
        this.bucketInstance = this;
        this.owner = player;
        this.type = item.getString("buckets-lite.type");
        this.generationMaterial = Material.getMaterial(item.getString("buckets-lite.material").toUpperCase());
        this.startingBlock = block;
        this.delay = item.getInteger("buckets-lite.delay");
        this.generationLength = item.getInteger("buckets-lite.generation-length");
        this.price = item.getDouble("buckets-lite.price");
        this.pseudo = item.getBoolean("buckets-lite.pseudo");
        this.generationTaskID = UUID.randomUUID();
        //Calculate the blocks to generate and store it
        this.blocksToGenerate = CalculateBlocksToGenerate.getBlocks(this);
        //Add it to the list of buckets that are pending generation
        bucketsPendingGeneration.add(this);
    }

    public void doGeneration() {
        //Make sure that the bucket is not already generating
        if (!bucketsPendingGeneration.contains(this) || bucketsActivelyGenerating.contains(this)) return;
        //Remove it from the pending buckets list
        bucketsPendingGeneration.remove(this);
        //Add it to the actively generating list
        bucketsActivelyGenerating.add(this);
        //Run the code to generate blocks
        DelayedBlockGeneration.generate(this);
    }

    public void pauseGeneration() {
        generationPaused = !generationPaused;
    }

    public void stopGeneration() {
        generationStopped = true;
    }

    public boolean isGenerationPaused() {
        return generationPaused;
    }

    public boolean isGenerationStopped() {
        return generationStopped;
    }

    public GenBucket getBucketInstance() {
        return bucketInstance;
    }

    public Player getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public Material getGenerationMaterial() {
        return generationMaterial;
    }

    public Block getStartingBlock() {
        return startingBlock;
    }

    public int getDelay() {
        return delay;
    }

    public int getGenerationLength() {
        return generationLength;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPseudo() {
        return pseudo;
    }

    public BukkitTask getGenerationTask() {
        return generationTask;
    }

    public void setGenerationTask(BukkitTask generationTask) {
        this.generationTask = generationTask;
    }

    public UUID getGenerationTaskID() {
        return generationTaskID;
    }

    public ArrayList<Block> getBlocksToGenerate() {
        if (blocksToGenerate.size() > 0) return blocksToGenerate;
        return null;
    }
}