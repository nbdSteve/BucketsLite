package dev.nuer.vbuckets.command;

import dev.nuer.vbuckets.file.LoadProvidedFiles;
import dev.nuer.vbuckets.gui.BucketGui;
import dev.nuer.vbuckets.vBuckets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * /vb, /gen command class
 */
public class BucketCommand implements CommandExecutor {
    //Register the main class
    private Plugin pl = vBuckets.getPlugin(vBuckets.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((vBuckets) pl).getFiles();

    //Constructor to make the command work
    public BucketCommand(vBuckets pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (c.getName().equalsIgnoreCase("gen") || c.getName().equalsIgnoreCase("vb")) {
            if (args.length == 0) {
                if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    if (s instanceof Player) {
                        if (s.hasPermission("vbucket.gui")) {
                            BucketGui menu = new BucketGui();
                            menu.gui((Player) s);
                            for (String line : lpf.getMessages().getStringList("open-gui")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    }
                } else {
                    if (s instanceof Player) {
                        if (s.hasPermission("vbucket.help")) {
                            for (String line : lpf.getMessages().getStringList("help")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("The help message can only be seen using game chat.");
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                    if (s instanceof Player) {
                        if (s.hasPermission("vbucket.help")) {
                            for (String line : lpf.getMessages().getStringList("help")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("The help message can only be seen using game chat.");
                    }
                } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                    if (s instanceof Player) {
                        if (s.hasPermission("vbucket.reload")) {
                            lpf.reload();
                            for (String line : lpf.getMessages().getStringList("reload")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        lpf.reload();
                        pl.getLogger().info("You have successfully reloaded all files.");
                    }
                } else {
                    if (s instanceof Player) {
                        for (String line : lpf.getMessages().getStringList("invalid-command")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    } else {
                        pl.getLogger().info("The command you entered is invalid.");
                    }
                }
            } else if (args.length == 3 || args.length == 4) {
                if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g")) {
                    if (s instanceof Player) {
                        if (!s.hasPermission("vbucket.give")) {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            return true;
                        }
                    }
                    // Initializing variables for the tool
                    Player target = null;
                    int bucketType = 0;
                    int amount = 1;
                    int x = 0;
                    String bucketNumber = "bucket-" + args[2];
                    try {
                        target = pl.getServer().getPlayer(args[1]);
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-player")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The command you entered is invalid");
                        }
                    }
                    try {
                        bucketType = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-bucket")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The bucket you entered doesn't exist.");
                        }
                    }
                    try {
                        new ItemStack(Material.valueOf(lpf.getBuckets().getString(bucketNumber + ".item").toUpperCase()));
                    } catch (Exception e) {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-bucket")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The bucket you entered doesn't exist.");
                        }
                    }
                    if (args.length == 4) {
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (Exception e) {
                            if (s instanceof Player) {
                                for (String line : lpf.getMessages().getStringList("invalid-amount")) {
                                    s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                                }
                            } else {
                                pl.getLogger().info("The amount you entered is invalid.");
                            }
                        }
                    }
                    if (bucketType != 0) {
                        while (x < amount) {
                            ItemStack bucket =
                                    new ItemStack(Material.valueOf(lpf.getBuckets().getString(bucketNumber + ".item").toUpperCase()));
                            ItemMeta bucketMeta = bucket.getItemMeta();
                            List<String> bucketLore = new ArrayList<>();
                            bucketMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    lpf.getBuckets().getString(bucketNumber + ".name")));
                            for (String lore : lpf.getBuckets().getStringList(bucketNumber + ".lore")) {
                                bucketLore.add(ChatColor.translateAlternateColorCodes('&', lore));
                            }
                            if (lpf.getBuckets().getBoolean(bucketNumber + ".glowing")) {
                                bucketMeta.addEnchant(Enchantment.LURE, 1, true);
                                bucketMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }
                            bucketMeta.setLore(bucketLore);
                            bucket.setItemMeta(bucketMeta);
                            target.getInventory().addItem(bucket);
                            x++;
                        }
                    } else {
                        if (s instanceof Player) {
                            for (String line : lpf.getMessages().getStringList("invalid-level")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            pl.getLogger().info("The bucket you entered doesn't exist.");
                        }
                    }
                } else {
                    if (s instanceof Player) {
                        if (s.hasPermission("vbucket.give")) {
                            for (String line : lpf.getMessages().getStringList("invalid-command")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("no-permission")) {
                                s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                        }
                    } else {
                        pl.getLogger().info("the command you entered is invalid.");
                    }
                }
            } else {
                if (s instanceof Player) {
                    if (s.hasPermission("vbucket.give") || s.hasPermission("vbucket.reload")
                            || s.hasPermission("vbucket.help")) {
                        for (String line : lpf.getMessages().getStringList("invalid-command")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("no-permission")) {
                            s.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    }
                } else {
                    pl.getLogger().info("the command you entered is invalid.");
                }
            }
        }
        return true;
    }
}