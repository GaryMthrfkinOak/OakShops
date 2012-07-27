/*******************************************************************************
 * Copyright (c) 2012 GaryMthrfkinOak (Jesse Caple).
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.ignoreourgirth.gary.oakshops;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor;

public class OakShops extends JavaPlugin {
	
	public static Plugin plugin;
	public static Logger log;
	public static Tracker tracker;
	
	public void onEnable() {
		plugin = this;
		log = this.getLogger();
		tracker = new Tracker();
        getServer().getPluginManager().registerEvents(tracker, this);
        CommandPreprocessor.addPermission("shop.admin", "oakshops.admin");
        CommandPreprocessor.addExecutor(new Commands());
	}
	
	public void onDisable() {
		tracker.unload();
	}
	
}
