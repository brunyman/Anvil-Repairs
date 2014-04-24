package com.ste.AnvilRepairs.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
	
	NO_CONSOLE("no-console", "&4This command is only available to players!"),
	NO_PERM("no-perm", "&4You don''t have permission for that!"),
	ANVIL_IN_USE("anvil-in-use", "&4That anvil is in use!"),
	REGULAR_ANVIL_DISABLED("regular-anvil-disable", "&4Anvil regular use has been disabled!"),
	FULL_DURABILITY("full-durability", "&4Item already at full durability!"),
	NOT_ENOUGH_FUNDS("not-enough-funds", "&4You can't afford to repair this item!"),
	REPAIRING_ITEM("repairing-item", "&6Repairing item."),
	ITEM_REPAIRED("item-repaired", "&aItem has been repaired!"),
	ERROR("error", "&4Something went wrong!"),
	LOG_INVALID_REPAIR_COST("log-invalid-repair-cost", "&4[ERROR] Repair cost in config is not valid!"),
	LOG_ACCESS_DENIED("log-access-denied", "&4{player} was denied access to that command!"),
	LOG_INVALID_MATERIAL("log-invalid-material", "Invalid material in config: {material}"),
	LOG_ITEM_NOT_REPAIRABLE("log-item-not-repairable", "Item not repairable: {item}"),
	LOG_REPAIRING_ITEM("log-repairing-item", "&dRepairing item: {item}");

	private String name;
	private String def;
	private static YamlConfiguration LANG;

	Lang(String name, String def) {
		this.name = name;
		this.def = colorize(def);
	}

	public String toString() {
		return colorize(LANG.getString(name, def));
	}

	public static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void setFile(YamlConfiguration config) {
		LANG = config;
	}

	public String getDefault() {
		return def;
	}

	public String getPath() {
		return name;
	}

}
