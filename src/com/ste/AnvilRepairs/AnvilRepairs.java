package com.ste.AnvilRepairs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.ste.AnvilRepairs.economy.Econ;
import com.ste.AnvilRepairs.lang.Lang;
import com.ste.AnvilRepairs.listeners.AnvilListener;
import com.ste.AnvilRepairs.listeners.PlayerListener;

public class AnvilRepairs extends JavaPlugin {
	
	public static YamlConfiguration LANG;
	public static File LANG_FILE;
	private HashMap<Location, Player> anvilsInUse = new HashMap<Location, Player>();
	private HashMap<ItemStack, Player> claimedItems = new HashMap<ItemStack, Player>();

	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		loadLang();

		if (getConfig().getBoolean("use-economy", false) && (!Bukkit.getPluginManager().isPluginEnabled("Vault") || !Econ.setupEconomy())) {
			log("&c[ERROR] You have chosen to use economy support,");
			log("&c[ERROR] But no economy plugin(s) with Vault support were found!");
			log("&c[ERROR] &a" + getName() + " &cis disabling itself!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);

		Bukkit.getServer().getLogger().info("[AnvilRepairs] Enabled v" + this.getDescription().getVersion());
	}

	public void log(Object obj) {
		
		if (getConfig().getBoolean("color-logs", true)) {
			getServer().getConsoleSender().sendMessage(Lang.colorize("&8[&e" + getName() + "&7]&r " + obj));
		} 
		else {
			Bukkit.getLogger().log(Level.INFO, "[" + getName() + "] " + ((String) obj).replaceAll("(?)\u00a7([a-f0-9k-or])", ""));
		}
	}

	public void debug(Object obj) {
		
		if (getConfig().getBoolean("debug-mode", false))
			log(obj);
	}

	public YamlConfiguration getLang() {
		return LANG;
	}

	public File getLangFile() {
		return LANG_FILE;
	}

	public void loadLang() {
		
		File lang = new File(getDataFolder(), "lang.yml");
		
		if (!lang.exists()) {
			
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				InputStream defConfigStream = getResource("lang.yml");
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					defConfig.save(lang);
					Lang.setFile(defConfig);
					return;
				}
			} 
			catch (IOException e) {
				e.printStackTrace(); // So they notice
				log("&4Couldn't create language file.");
				log("&4This is a fatal error. Now disabling.");
				setEnabled(false); // Without it loaded, we can't send them messages
			}
		}
		
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null)
				conf.set(item.getPath(), item.getDefault());
		}
		
		Lang.setFile(conf);
		
		LANG = conf;
		LANG_FILE = lang;
		
		try {
			conf.save(getLangFile());
		} 
		catch (IOException e) {
			log("&4Failed to save lang.yml.");
			e.printStackTrace();
		}
	}

	public void addAnvil(Location loc, Player player) {
		if (anvilsInUse.containsKey(loc))
			return;
		
		anvilsInUse.put(loc, player);
	}

	public void removeAnvil(Location loc) {
		if (!anvilsInUse.containsKey(loc))
			return;
		
		anvilsInUse.remove(loc);
	}

	public Player getUserOfAnvil(Location loc) {
		if (!anvilsInUse.containsKey(loc))
			return null;
		
		return anvilsInUse.get(loc);
	}

	public boolean isPlayerRepairing(Player player) {
		if (anvilsInUse.containsValue(player))
			return true;
		
		return false;
	}

	public void addClaimedItem(ItemStack item, Player player) {
		if (claimedItems.containsKey(item))
			return;
		
		claimedItems.put(item, player);
	}

	public void removeClaimedItem(ItemStack item) {
		if (!claimedItems.containsKey(item))
			return;
		
		claimedItems.remove(item);
	}

	public Player getPlayerOfClaimedItem(ItemStack item) {
		if (!claimedItems.containsKey(item))
			return null;
		
		return claimedItems.get(item);
	}

}
