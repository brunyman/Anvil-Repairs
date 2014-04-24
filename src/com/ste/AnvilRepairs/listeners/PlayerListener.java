package com.ste.AnvilRepairs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.ste.AnvilRepairs.AnvilRepairs;
import com.ste.AnvilRepairs.economy.Econ;
import com.ste.AnvilRepairs.lang.Lang;
import com.ste.AnvilRepairs.tasks.AnvilRepairParticles;
import com.ste.AnvilRepairs.tasks.AnvilRepairSound;
import com.ste.AnvilRepairs.tasks.RepairItem;

public class PlayerListener implements Listener{
	
	private AnvilRepairs plugin;

	public PlayerListener(AnvilRepairs plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onClickAnvil(PlayerInteractEvent event) {
		
		Block anvil = event.getClickedBlock();
		
		if (!anvil.getType().equals(Material.ANVIL))
			return;
		
		if (!plugin.getConfig().getStringList("enabled-worlds").contains(anvil.getWorld().getName()))
			return;
		
		boolean cancel = false;
		
		if (plugin.getConfig().getBoolean("disable-normal-anvil", false) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			event.setCancelled(cancel = true);
		
		String trigger = plugin.getConfig().getString("repair-action");
		Action action = null;
		
		if (trigger.equalsIgnoreCase("LEFT_CLICK"))
			action = Action.LEFT_CLICK_BLOCK;
		
		if (trigger.equalsIgnoreCase("RIGHT_CLICK"))
			action = Action.RIGHT_CLICK_BLOCK;
		
		Player player = event.getPlayer();
		
		if (action == null || !event.getAction().equals(action)) {
			if (cancel)
				player.sendMessage(Lang.REGULAR_ANVIL_DISABLED.toString());
			return;
		}
		
		if (plugin.getUserOfAnvil(anvil.getLocation()) != null) {
			player.sendMessage(Lang.ANVIL_IN_USE.toString());
			if (action.equals(Action.RIGHT_CLICK_BLOCK))
				event.setCancelled(cancel = true);
			return;
		}
		
		ItemStack item = player.getItemInHand();
		if (item == null || item.equals(Material.AIR)) {
			if (cancel)
				player.sendMessage(Lang.REGULAR_ANVIL_DISABLED.toString());
			return;
		}
		
		boolean repairable = false;
		for (String str : plugin.getConfig().getStringList("repairable-items")) {
			Material material = Material.valueOf(str.toUpperCase());
			if (material == null) {
				plugin.debug(Lang.LOG_INVALID_MATERIAL.toString().replaceAll("(?i)\\{material\\}", str.toUpperCase()));
				continue;
			}
			if (item.getType().equals(material)) {
				repairable = true;
				break;
			}
		}
		
		if (!repairable) {
			plugin.debug(Lang.LOG_ITEM_NOT_REPAIRABLE.toString().replaceAll("(?i)\\{item\\}", item.getType().toString().toUpperCase()));
			return;
		}
		
		if (item.getDurability() == 0) {
			player.sendMessage(Lang.FULL_DURABILITY.toString());
			return;
		}
		
		Integer cost = null;
		
		try {
			cost = plugin.getConfig().getInt("repair-cost");
		} catch (Exception e) {
			cost = null;
		}
		
		if (cost == null) {
			player.sendMessage(Lang.ERROR.toString());
			plugin.log(Lang.LOG_INVALID_REPAIR_COST.toString());
			return;
		}
		
		if (plugin.getConfig().getBoolean("use-economy", false)) {
			if (Econ.getEconomy().getBalance(player.getName()) < cost && !player.hasPermission("anvilrepairs.bypass.cost")) {
				player.sendMessage(Lang.NOT_ENOUGH_FUNDS.toString());
				return;
			}
			
		} 
		else {
			if (player.getLevel() < cost && !player.hasPermission("anvilrepairs.bypass.cost")) {
				player.sendMessage(Lang.NOT_ENOUGH_FUNDS.toString());
				return;
			}
		}
		
		plugin.debug(Lang.LOG_REPAIRING_ITEM.toString().replaceAll("(?i)\\{item\\}", item.getType().toString().toUpperCase()));
		
		player.sendMessage(Lang.REPAIRING_ITEM.toString());
		
		if (!player.hasPermission("anvilrepairs.bypass.cost")) {
			if (plugin.getConfig().getBoolean("use-economy", false)) {
				Econ.getEconomy().withdrawPlayer(player.getName(), cost);
			} else {
				player.setLevel(player.getLevel() - cost);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerExpChangeEvent(player, 0));
			}
		}
		
		Item droppedItem = anvil.getWorld().dropItem(anvil.getLocation().add(0.5, 1.5, 0.5), item);
		
		droppedItem.setVelocity(new Vector(0, 0, 0));
		
		player.setItemInHand(new ItemStack(Material.AIR, 1));
		
		plugin.addAnvil(anvil.getLocation(), player);
		
		plugin.addClaimedItem(item, player);
		
		event.setCancelled(true);
		
		if (plugin.getConfig().getBoolean("play-warp-sounds", true))
			anvil.getWorld().playSound(anvil.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
		
		Bukkit.getScheduler().runTaskLater(plugin, new RepairItem
				(plugin, anvil.getLocation(), item, droppedItem), plugin.getConfig().getInt("seconds-to-showcase", 3) * 20);
		
		if (plugin.getConfig().getBoolean("play-repair-sounds", true)) {
			final BukkitTask soundTask = Bukkit.getScheduler().runTaskTimer(plugin, new AnvilRepairSound(anvil), 5, 10);
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					soundTask.cancel();
				}
			}, plugin.getConfig().getInt("seconds-to-showcase", 3) * 20);
		}
		
		final BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(plugin, new AnvilRepairParticles(plugin, anvil), 5, 10);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				particleTask.cancel();
			}
		}, plugin.getConfig().getInt("seconds-to-showcase", 3) * 20);
			
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		if (plugin.getPlayerOfClaimedItem(event.getItem().getItemStack()) != null)
			event.setCancelled(true);
	}

}
