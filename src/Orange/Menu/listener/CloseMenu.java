package Orange.Menu.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.inventory.*;
import cn.nukkit.inventory.*;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.math.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.item.Item;

import Orange.Menu.Main;
import Orange.Menu.Menu;

public class CloseMenu implements Listener {

	public Main plugin = Main.getThis();

	public CloseMenu() {}

	@EventHandler
	public void onCloseMenu(InventoryCloseEvent event) {
		if (Menu.LookMenu.containsKey(event.getPlayer())) {
			Menu.closeMenu(event.getPlayer(), (ChestInventory) event.getInventory());
		}
	}

}
