package dev.nuer.bl.gui.player;

import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.gui.AbstractGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ColorUtil;
import dev.nuer.bl.utils.ItemUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GenerationPageGui extends AbstractGui {

    public GenerationPageGui(Player player, int startingSlot, int endSlot, HashMap<GenBucket, Integer> buckets, int page) {
        super(FileManager.get("config").getInt("player-gui.size"),
                ColorUtil.colorize(FileManager.get("config").getString("player-gui.name")
                        .replace("{page}", String.valueOf(page))));

        int slot = 0;
        for (GenBucket bucket : buckets.keySet()) {
            if (startingSlot > buckets.get(bucket)) continue;
            if (endSlot <= buckets.get(bucket)) break;
            setItemInSlot(slot, ItemUtil.createPlayerGuiItem(
                    FileManager.get("config").getString("player-gui.buckets.material"),
                    "{slot}", String.valueOf(startingSlot + slot + 1),
                    "{type}", bucket.getType(),
                    "{gen-id}", bucket.getGenerationTaskID().toString(),
                    "{material}", bucket.getGenerationMaterial().toString().toLowerCase(),
                    "{gen-paused}", String.valueOf(bucket.isGenerationPaused()))
                    , player1 -> {
                        bucket.pauseGeneration();
                        player.closeInventory();
                        new GenerationPageGui(player, startingSlot, endSlot, buckets, page).open(player);
                    });
            slot++;
        }
        if (endSlot > 45) {
            pageItems(player, startingSlot, endSlot, buckets, page, false);
        } else {
            pageItems(player, startingSlot, endSlot, buckets, page, true);
        }
    }
}
