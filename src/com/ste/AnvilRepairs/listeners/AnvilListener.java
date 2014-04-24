package com.ste.AnvilRepairs.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.ste.AnvilRepairs.AnvilRepairs;
import com.ste.AnvilRepairs.lang.Lang;

public class AnvilListener implements Listener{
	
	private AnvilRepairs plugin;

	public AnvilListener(AnvilRepairs plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAnvilBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		
		if (!block.getType().equals(Material.ANVIL))
			return;
		
		if (plugin.getUserOfAnvil(block.getLocation()) == null)
			return;
		
		event.getPlayer().sendMessage(Lang.ANVIL_IN_USE.toString());
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAnvilPistonExtend(BlockPistonExtendEvent event) {
		
		for (Block block : event.getBlocks()) {
			if (plugin.getUserOfAnvil(block.getLocation()) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAnvilPistonExtend(BlockPistonRetractEvent event) {
		
		Block piston = event.getBlock();
		
		if (!piston.getType().equals(Material.PISTON_STICKY_BASE))
			return;
		
		Block block = piston.getRelative(event.getDirection());
		if (plugin.getUserOfAnvil(block.getRelative(event.getDirection()).getLocation()) == null)
			return;
		
		event.setCancelled(true);
	}

}
