package dev.nuer.bl.listeners;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.events.GenBucketPlaceBlockEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

    @EventHandler
    public void blockGenerate(GenBucketPlaceBlockEvent event) {
        //Check that the events is not cancelled
        if (event.isCancelled()) return;
        //Generate the block
        if (BucketsLite.economy != null) {
            if (BucketsLite.economy.getBalance(event.getPlayer()) < event.getGenBucket().getPrice()) {
                event.getGenBucket().stopGeneration();
                return;
            }
            BucketsLite.economy.withdrawPlayer(event.getPlayer(), event.getGenBucket().getPrice());
        }
        event.getGenerationBlock().setType(event.getGenBucket().getGenerationMaterial());
    }
}
