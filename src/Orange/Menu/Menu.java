package Orange.Menu;

import java.util.*;
import java.lang.Exception;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.math.*;
import cn.nukkit.utils.*;
import cn.nukkit.inventory.*;
import cn.nukkit.command.*;
import money.Money;

public class Menu {

	public static Map<Player, Block> LookMenu = new HashMap();

	public static Map<Player, Map> MenuType = new HashMap();

	public static Map<Player, String> Setting = new HashMap<>();

	public static Map<Map, Map> returnMenu = new HashMap<>();

	public static Map<String, Map<Integer, Item>> AllMenu = new HashMap<>();

	public static void initMenu() {
		try {
			AllMenu.clear();
			Set<String> all = ((LinkedHashMap) Main.getThis().menus.getAll()).keySet();
			for (String id : all) {
				ConfigSection conf = Main.getThis().menus.getSections(id);
				Set<String> set = conf.getSections("data").getKeys(false);
				Map<Integer, Item> menu = new HashMap();
				for (String key : set) {
					LinkedHashMap itemData = (LinkedHashMap) conf.getSections("data").getAll().get(key);
					Item item = Item.get((int) itemData.get("item"), 0, 1, (byte[]) Binary.hexStringToBytes((String) itemData.get("nbt")));
					menu.put(new Integer(key), item);
				}
				AllMenu.put(id, menu);
			}
			for (String MenuId : all) {
				if (AllMenu.containsKey(Main.getThis().menus.getSections(MenuId))) {
					returnMenu.put(AllMenu.get(Main.getThis().menus.getSections(MenuId)), AllMenu.get(Main.getThis().menus.getSections(MenuId).getString("父菜单")));
				}
			}
		} catch (Exception e) {}
	}

	public static void use(Player player, Item item, int slot) {
		CompoundTag tag = item.getCustomBlockData();
		if (tag != null && tag.contains("Menu")) {
			CompoundTag nbt = item.getCustomBlockData().getCompound("Menu");

			if (nbt.contains("del")) {
				Map tags = nbt.getCompound("del").getTags();
				Set<String> set = tags.keySet();
				for (String key : set) {
					StringTag DelTag = (StringTag) tags.get(key);
					String[] itemData = DelTag.data.split("\\:");
					if (player.getInventory().contains(Item.get(Integer.parseInt(itemData[0]), itemData.length == 2 ? Integer.parseInt(itemData[1]) : 0))) {
						player.getInventory().remove(Item.get(Integer.parseInt(itemData[0]), itemData.length == 2 ? Integer.parseInt(itemData[1]) : 0));
					} else {
						return;
					}
				}
			}

			if (nbt.contains("remove")) {
				if (Money.getMoney(player) > (float) nbt.getInt("remove")) {
					Money.reduceMoney(player, (float) nbt.getInt("remove"));
				} else {
					return;
				}
			}

			if (nbt.contains("give")) {
				Map tags = nbt.getCompound("give").getTags();
				Set<String> set = tags.keySet();
				for (String key : set) {
					StringTag giveTag = (StringTag) tags.get(key);
					String[] itemData = giveTag.data.split("\\:");
					player.getInventory().addItem(Item.get(Integer.parseInt(itemData[0]), itemData.length == 2 ? Integer.parseInt(itemData[1]) : 0));
				}
			}

			if (nbt.contains("add")) {
				Money.addMoney(player, (float) nbt.getInt("add"));
			}

			if (nbt.contains("cmd")) {
				Map tags = nbt.getCompound("cmd").getTags();
				Set<String> set = tags.keySet();
				for (String key : set) {
					StringTag CmdTag = (StringTag) tags.get(key);
					String cmdTag = CmdTag.data.replace(".", " ");
					if (cmdTag.contains("{op}")) {
						cmdTag = cmdTag.replace("{op}", "");
						cmdTag = cmdTag.replace("{p}", player.getName());
						Main.getThis().getServer().addOp(player.getName());
						Main.getThis().getServer().dispatchCommand(player, cmdTag);
						Main.getThis().getServer().removeOp(player.getName());
						Main.getThis().getServer().getOps().remove(player.getName());
						Main.getThis().getServer().getOps().save(true);
					} else {
						cmdTag = cmdTag.replace("{p}", player.getName());
						Main.getThis().getServer().dispatchCommand(player, cmdTag);

					}
				}
			}

			if (nbt.contains("close") && nbt.getBoolean("close")) {
				Menu.closeMenu(player);
			}

		}
	}

	public static boolean sendMenu(Player player, String config) {
		if (!AllMenu.containsKey(config))
			return false;
		Map menu = AllMenu.get(config);
		Menu.sendMenu(player, menu);
		return true;
	}

	public static void sendMenu(Player player, Map<Integer, Item> menu) {

		Menu.MenuType.put(player, menu);
		if (Menu.LookMenu.containsKey(player)) {
			closeMenu(player);
		}
		Vector3 v3 = new Vector3((int) player.x, (int) player.y - 5, (int) player.z);
		Menu.LookMenu.put(player, player.level.getBlock(v3));

		player.level.setBlock(v3, Block.get(54));
		player.level.sendBlocks(new Player[]{player}, new Vector3[]{player.level.getBlock(v3)});

		CompoundTag nbt = new CompoundTag()
			.putList(new ListTag("Items"))
			.putString("id", "Chest")
			.putInt("x", (int) v3.x)
			.putInt("y", (int) v3.y)
			.putInt("z", (int) v3.z);
		BlockEntityChest chest = new BlockEntityChest(player.chunk, nbt);
		chest.getInventory().setContents(menu);
		player.addWindow(chest.getInventory());
	}

	public static boolean closeMenu(Player player) {
		return closeMenu(player, null);
	}

	public static boolean closeMenu(Player player, ChestInventory inv) {
		if (!LookMenu.containsKey(player))
			return false;
		try {
			if (Setting.containsKey(player) && inv != null) {
				Set<Integer> set = inv.getContents().keySet();
				LinkedHashMap c = Main.getThis().menus.getSections(Setting.get(player));
				((LinkedHashMap) c.get("data")).clear();
				for (Integer slot : set) {
					Item item = inv.getContents().get(slot);
					LinkedHashMap map = new LinkedHashMap();
					map.put("item", item.getId());
					map.put("nbt", "" + Binary.bytesToHexString((byte[]) item.getCompoundTag()));
					((LinkedHashMap) c.get("data")).put(slot.toString(), map);
				}
				Main.getThis().menus.set(Setting.get(player), new ConfigSection(c));
				Main.getThis().menus.save();
				Setting.remove(player);
				player.sendMessage("§a保存成功");
				initMenu();
			}
		} catch (Exception e) {
		}
		player.level.setBlock(Menu.LookMenu.get(player), LookMenu.get(player));
		LookMenu.remove(player);
		return true;
	}


}
