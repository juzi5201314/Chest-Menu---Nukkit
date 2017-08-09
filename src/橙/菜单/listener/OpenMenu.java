package Orange.Menu.listener;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.*;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.entity.Entity;

import Orange.Menu.Main;
import Orange.Menu.Menu;

import java.util.*;

public class OpenMenu implements Listener {

	public Main plugin = Main.getThis();

	@EventHandler
	public void onInteract(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		if (Main.getThis().configs.getString("物品点击打开菜单").equals(item.getId() + ":" + item.getDamage())) {
			event.setCancelled();
			if (Menu.AllMenu.containsKey(Main.getThis().configs.getString("默认菜单")))
				Menu.sendMenu(player, Menu.AllMenu.get(Main.getThis().configs.getString("默认菜单")));
		}
	}

	@EventHandler
	public void onSendPk(DataPacketReceiveEvent event) {
		try {
			Player player = event.getPlayer();
			Item item = player.getInventory().getItemInHand();
			if (event.getPacket() instanceof PlayerActionPacket) {
				PlayerActionPacket pk = (PlayerActionPacket) event.getPacket();
				if (pk.action == 11 && item.deepEquals(plugin.MenuMap)) {
					if (Menu.AllMenu.containsKey(Main.getThis().configs.getString("默认菜单"))) {
						Menu.sendMenu(player, Menu.AllMenu.get(Main.getThis().configs.getString("默认菜单")));
						player.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SNEAKING, false);
					}
				}
			}
		} catch (Exception e) {}
	}

}
