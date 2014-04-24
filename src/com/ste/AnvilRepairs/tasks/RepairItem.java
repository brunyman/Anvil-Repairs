package com.ste.AnvilRepairs.tasks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ste.AnvilRepairs.AnvilRepairs;
import com.ste.AnvilRepairs.lang.Lang;

public class RepairItem implements Runnable {
	
	private AnvilRepairs plugin;
	private Location anvilLoc;
	private ItemStack item;
	private Item droppedItem;

	public RepairItem(AnvilRepairs plugin, Location anvilLoc, ItemStack item, Item droppedItem) {
		this.plugin = plugin;
		this.anvilLoc = anvilLoc;
		this.item = item;
		this.droppedItem = droppedItem;
	}

	@Override
	public void run() {
		Player player = plugin.getUserOfAnvil(anvilLoc);
		item.setDurability((short) 0);
		if (plugin.getConfig().getBoolean("play-warp-sounds", true))
			anvilLoc.getWorld().playSound(anvilLoc, Sound.ENDERMAN_TELEPORT, 1f, 1f);
		droppedItem.remove();
		plugin.removeAnvil(anvilLoc);
		plugin.removeClaimedItem(item);
		if (player == null)
			return;
		if (plugin.getConfig().getBoolean("stop-multi-world-exploiters") && !anvilLoc.getWorld().equals(player.getWorld()))
			return;
		player.getInventory().addItem(item);
		player.sendMessage(Lang.ITEM_REPAIRED.toString());
	}
}
