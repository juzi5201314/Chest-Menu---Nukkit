package Orange.Menu.listener;

import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.*;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.*;
import cn.nukkit.event.player.*;
import cn.nukkit.level.*;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.Transaction;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.item.Item;
import cn.nukkit.utils.*;

import java.util.*;

import Orange.Menu.Main;
import Orange.Menu.Menu;

public class UseMenu implements Listener {

	public Main plugin = Main.getThis();

	public Map<Player, Item> t = new HashMap();

	public UseMenu() {}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.getInventory().setItem(0, plugin.MenuMap);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getItem().deepEquals(plugin.MenuMap))
			event.setCancelled();
	}

	@EventHandler
	public void onTransaction(InventoryTransactionEvent event) {
		Set<Transaction> transactions = event.getTransaction().getTransactions();
		Player player = null;
		ChestInventory chestInventory = null;
		Transaction tran = null;
		for (Transaction transaction : transactions) {
			if (!(transaction.getInventory() instanceof ChestInventory)) {
				continue;
			}
			for (Player p : transaction.getInventory().getViewers()) {
				if (p != null) {
					player = p;
					chestInventory = (ChestInventory) transaction.getInventory();
					tran = transaction;
					break;
				}
			}
		}
		if (player == null || chestInventory == null || tran == null) {
			return;
		}
		Item sitem = tran.getSourceItem();
		if (sitem.deepEquals(plugin.MenuMap))
			event.setCancelled();
		if (Menu.LookMenu.containsKey(player) && !Menu.Setting.containsKey(player)) {
			if (this.t.containsKey(player) && sitem.deepEquals(this.t.get(player))) {
				event.setCancelled();
				this.t.remove(player);
				Menu.use(player, sitem, tran.getSlot());
			} else {
				event.setCancelled();
				this.t.put(player, sitem);
			}
		}
	}

}
