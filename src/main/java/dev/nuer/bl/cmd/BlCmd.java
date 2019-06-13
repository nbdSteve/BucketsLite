package dev.nuer.bl.cmd;

import dev.nuer.bl.BucketsLite;
import dev.nuer.bl.cmd.sub.GiveCmd;
import dev.nuer.bl.cmd.sub.HelpCmd;
import dev.nuer.bl.cmd.sub.ReloadCmd;
import dev.nuer.bl.gui.player.PlayerGenerationGui;
import dev.nuer.bl.gui.purchase.MenuGui;
import dev.nuer.bl.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Main class that handles the /b+ command
 */
public class BlCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                new MenuGui().open((Player) sender);
            } else {
                BucketsLite.LOGGER.info("The gui can only be viewed by players.");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("help")) {
                HelpCmd.onCmd(sender);
            }
            if (args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("reload")) {
                ReloadCmd.onCmd(sender);
            }
            if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("active")) {
                if (sender instanceof Player) {
                    new PlayerGenerationGui((Player) sender).open((Player) sender);
                } else {
                    BucketsLite.LOGGER.info("The gui can only be viewed by players.");
                }
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("g") || args[0].equalsIgnoreCase("give")) {
                GiveCmd.onCmd(sender, args);
            }
        } else {
            if (sender instanceof Player) {
                MessageUtil.message("messages", "invalid-command", (Player) sender, "{reason}",
                        "An error occurred. Please check your command syntax, then your configuration (stack trace console)");
            } else {
                BucketsLite.LOGGER.info("Invalid command, check the in game help command for command help.");
            }
        }
        return true;
    }
}