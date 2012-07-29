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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ignoreourgirth.gary.oakcorelib.ProtectedLocations;

public class Tracker implements Listener {
	
	private HashSet<Shop> shops;
	private HashSet<Location> locations;
	private HashMap<Location, Shop> shopsByLocation;
	
	public Tracker() {
		shops = new HashSet<Shop>();
		locations = new HashSet<Location>();
		shopsByLocation = new HashMap<Location, Shop>();
		Iterator<Shop> iterator = DBCode.loadAllShops().iterator();
		while (iterator.hasNext()) {
			trackShop(iterator.next());
		}
	}
	
	public void unload() {
		Iterator<Shop> shopIterator = new HashSet<Shop>(shops).iterator();
		while (shopIterator.hasNext())
		{
			untrackShop(shopIterator.next());
		}
		shops.clear();
		locations.clear();
		shopsByLocation.clear();
	}
	
	public void trackShop(Shop shop) {
		Location base = shop.getBaseLocation();
		Location top = base.clone().add(0,1,0);
		locations.add(base);
		locations.add(top);
		shops.add(shop);
		shopsByLocation.put(base, shop);
		shopsByLocation.put(top, shop);
		shop.showDisplay(false);
		ProtectedLocations.add(base);
		ProtectedLocations.add(top);
	}
	
	public void untrackShop(Shop shop) {
		Location base = shop.getBaseLocation();
		Location top = base.clone().add(0,1,0);
		locations.remove(base);
		locations.remove(top);
		shops.remove(shop);
		shopsByLocation.remove(base);
		shopsByLocation.remove(top);
		shop.hideDisplay();
		DBCode.revertShopBlock(shop.getShopID(), base);
		ProtectedLocations.remove(base);
		ProtectedLocations.remove(top);
	}
	
	@EventHandler (priority=EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		boolean leftClick = event.getAction() == Action.LEFT_CLICK_BLOCK;
		boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
		if (leftClick || rightClick) {
			Location location = event.getClickedBlock().getLocation();
			if (locations.contains(location)) {
				Player player = event.getPlayer();
				if (leftClick) shopsByLocation.get(location).leftClick(player);
				if (rightClick) shopsByLocation.get(location).rightClick(player);
				event.setCancelled(true);
			}
		}

	}
	
	public Shop getShop(Location location) {
		return shopsByLocation.get(location);
	}
	
	public boolean isShopHere(Location location) {
		return locations.contains(location);
	}
	
}
