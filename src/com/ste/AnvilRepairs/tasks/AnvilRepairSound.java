package com.ste.AnvilRepairs.tasks;

import org.bukkit.Sound;
import org.bukkit.block.Block;

public class AnvilRepairSound implements Runnable {
	
	private Block anvil;

	public AnvilRepairSound(Block anvil) {
		this.anvil = anvil;
	}

	@Override
	public void run() {
		anvil.getWorld().playSound(anvil.getLocation(), Sound.ANVIL_LAND, 1f, 1f);
	}
}
