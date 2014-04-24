package com.ste.AnvilRepairs.tasks;

import org.bukkit.Effect;
import org.bukkit.block.Block;

import com.ste.AnvilRepairs.AnvilRepairs;

public class AnvilRepairParticles implements Runnable {
	
	private AnvilRepairs plugin;
	private Block anvil;

	public AnvilRepairParticles(AnvilRepairs plugin, Block anvil) {
		this.plugin = plugin;
		this.anvil = anvil;
	}

	@Override
	public void run() {
		String effectName = plugin.getConfig().getString("repair-effect");
		int effectModifier = 0;
		Effect effect = null;
		if (effectName.equals(null) || effectName.equals("") || effectName.equals("none"))
			return;
		try {
			effect = Effect.valueOf(effectName.toUpperCase());
		} catch (Exception e) {
			effect = Effect.POTION_BREAK;
		}
		try {
			effectModifier = plugin.getConfig().getInt("repair-effect-modifier");
		} catch (Exception e) {
			effectModifier = 0;
		}
		anvil.getWorld().playEffect(anvil.getLocation().add(0.5, 1, 0.5), effect, effectModifier); // The .add() only adds integers, the 0.5 is ignored as 0.
	}
}
