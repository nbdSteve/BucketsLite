package dev.nuer.bl.events;

import dev.nuer.bl.bucket.GenBucket;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GenBucketPlaceBlockEvent extends Event implements Cancellable {
    //Static events handlers
    private static final HandlerList handlers = new HandlerList();
    //Store if events is cancelled or not
    private boolean cancel;
    //Store the gen bucket used, can call methods on this bucket
    private GenBucket genBucket;
    //Store the block being changed
    private Block generationBlock;

    public GenBucketPlaceBlockEvent(GenBucket genBucket, Block generationBlock) {
        this.genBucket = genBucket;
        this.generationBlock = generationBlock;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GenBucket getGenBucket() {
        return genBucket;
    }

    public Block getGenerationBlock() {
        return generationBlock;
    }

    public Player getPlayer() {
        return genBucket.getOwner();
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
