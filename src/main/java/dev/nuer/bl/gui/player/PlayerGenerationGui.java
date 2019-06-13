package dev.nuer.bl.gui.player;

import dev.nuer.bl.bucket.GenBucket;
import dev.nuer.bl.gui.AbstractGui;
import dev.nuer.bl.managers.FileManager;
import dev.nuer.bl.utils.ColorUtil;
import dev.nuer.bl.utils.ItemUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerGenerationGui extends AbstractGui {

    private HashMap<GenBucket, Integer> playerBuckets = new HashMap<>();

    public PlayerGenerationGui(Player player) {
        super(FileManager.get("config").getInt("player-gui.size"),
                ColorUtil.colorize(FileManager.get("config").getString("player-gui.name")
                        .replace("{page}", "1")));
        int index = 0;
        for (GenBucket bucket : GenBucket.bucketsActivelyGenerating) {
            if (bucket.getOwner().getUniqueId().equals(player.getUniqueId())) {
                playerBuckets.put(bucket, index);
                index++;
            }
        }

        int slot = 0;
        for (GenBucket bucket : playerBuckets.keySet()) {
            if (slot > 44) break;
            if (bucket.getOwner().getUniqueId().equals(player.getUniqueId())) {
                setItemInSlot(slot, ItemUtil.createPlayerGuiItem(
                        FileManager.get("config").getString("player-gui.buckets.material"),
                        "{slot}", String.valueOf(slot + 1),
                        "{type}", bucket.getType(),
                        "{gen-id}", bucket.getGenerationTaskID().toString(),
                        "{material}", bucket.getGenerationMaterial().toString().toLowerCase(),
                        "{gen-paused}", String.valueOf(bucket.isGenerationPaused()))
                        , player1 -> {
                            bucket.pauseGeneration();
                            player.closeInventory();
                            new PlayerGenerationGui(player).open(player);
                        });
                slot++;
            }
        }

        pageItems(player, 0, 45, playerBuckets, 1, true);
    }
}