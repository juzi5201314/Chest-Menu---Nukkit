package Orange.Menu;

import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

import cn.nukkit.plugin.*;
import cn.nukkit.event.*;
import cn.nukkit.event.player.*;
import cn.nukkit.command.*;
import cn.nukkit.*;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.potion.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.entity.*;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.*;
import cn.nukkit.math.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.item.*;
import cn.nukkit.inventory.BigShapedRecipe;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.*;

import Orange.Menu.listener.*;

public class Main extends PluginBase implements Listener {

	public static Main obj = null;

	public Config menus;
	public Config configs;

	public File png;
	public ItemMap MenuMap;

	public static Main getThis() {
		return obj;
	}

	public void onLoad() {
		obj = this;
		getDataFolder().mkdirs();
		saveResource("Menu.png");
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new UseMenu(), this);
		getServer().getPluginManager().registerEvents(new CloseMenu(), this);
		getServer().getPluginManager().registerEvents(new OpenMenu(), this);
		getLogger().info("橘子么么哒");
		menus = new Config(new File(this.getDataFolder(), "Menus.yml"), Config.YAML);
		png = new File(this.getDataFolder(), "/Menu.png");
		ConfigSection c = new ConfigSection("默认菜单", "null");
		c.set("潜行打开菜单", "true");
		c.set("物品点击打开菜单", "2333:0");
		configs = new Config(new File(this.getDataFolder(), "Config.yml"), Config.YAML, c);
		Menu.initMenu();
		try {
			MenuMap = new ItemMap();
			MenuMap.setCustomName("§d菜单");
			MenuMap.setImage(ImageIO.read(png));
		} catch (Exception e) {}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		try {
			switch (command.getName()) {
				case "menu":

					if (args.length == 1 && args[0].equals("reload")) {
						Menu.initMenu();
						menus.reload();
						configs.reload();
						player.sendMessage("§3成功重载插件数据");
					}

					if (args.length == 4) {
						if (args[0].equals("item") && args[1].equals("set")) {
							Item item = player.getInventory().getItemInHand();
							if (item.getCustomBlockData() == null && !item.getCustomBlockData().contains("Menu")) {
								player.sendMessage("§c这个物品不是一个菜单物品");
								return true;
							} else {
								CompoundTag tag = item.getCustomBlockData().getCompound("Menu");
								switch (args[2]) {
									case "return":
										tag.putBoolean("return", (args[3].equals("true")) ? true : false);
										break;
									case "close":
										tag.putBoolean("close", (args[3].equals("true")) ? true : false);
										break;
									case "give":
										tag.getCompound("give").putString("" + tag.getCompound("give").getTags().size(), args[3]);
										break;
									case "del":
										tag.getCompound("del").putString("" + tag.getCompound("del").getTags().size(), args[3]);
										break;
									case "cmd":
										tag.getCompound("cmd").putString("" + tag.getCompound("cmd").getTags().size(), args[3]);
										break;
									case "add":
										tag.putInt("add", Integer.parseInt(args[3]));
										break;
									case "remove":
										tag.putInt("remove", Integer.parseInt(args[3]));
										break;
								}
								CompoundTag BlockData = item.getCustomBlockData();
								BlockData.putCompound("Menu", tag);
								item.setCustomBlockData(BlockData);
								player.getInventory().setItemInHand(item);
								player.sendMessage("§6成功设置手持物品属性");
							}
						}
					}

					if (args.length == 3) {
						if (args[0].equals("item") && args[1].equals("new")) {
							Item item = player.getInventory().getItemInHand();
							CompoundTag tag = new CompoundTag();
							CompoundTag nbt = new CompoundTag("Menu");
							nbt.putCompound("cmd", new CompoundTag("cmd"));
							tag.putCompound("Menu", nbt);
							item.setCustomBlockData(tag).setCustomName(args[2]);
							player.getInventory().setItemInHand(item);
							player.sendMessage("§6成功将手持物品设置成菜单物品");
						}
					}

					if (args.length == 2) {
						if (args[0].equals("add")) {
							if (!menus.exists(args[1])) {
								ConfigSection config = new ConfigSection();
								config.set("data", new ConfigSection());
								config.set("父菜单", null);
								menus.set(args[1], config);
								menus.save();
								player.sendMessage("§3成功创建一个菜单");
								return true;
							} else {
								player.sendMessage("§c已有这个菜单id了");
								return true;
							}
						}
						if (args[0].equals("send")) {
							if (menus.exists(args[1])) {
								Menu.sendMenu(player, args[1]);
								return true;
							} else {
								player.sendMessage("§c没有这个菜单");
								return true;
							}
						}
						if (args[0].equals("set")) {
							if (menus.exists(args[1])) {
								Menu.Setting.put(player, args[1]);
								Menu.sendMenu(player, args[1]);
								return true;
							} else {
								player.sendMessage("§c没有这个菜单");
								return true;
							}
						}

						return true;
					}

					return true;
			}
		} catch (Exception e) {
		}
		return true;

	}


}
