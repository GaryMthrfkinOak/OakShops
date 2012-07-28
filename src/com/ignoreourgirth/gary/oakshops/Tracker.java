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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.google.common.collect.HashMultimap;

public class Tracker implements Listener {
	
	private HashSet<Shop> shops;
	private HashSet<Location> locations;
	private HashMap<Location, Shop> shopsByLocation;
	private HashMultimap<Location, Shop> shopChunkBases;
	private int repeatingTaskID;
	private static long repeatTicks = 2400L; //2400L;
	
	public Tracker() {
		shops = new HashSet<Shop>();
		locations = new HashSet<Location>();
		shopsByLocation = new HashMap<Location, Shop>();
		shopChunkBases = HashMultimap.create();
		Iterator<Shop> iterator = DBCode.loadAllShops().iterator();
		while (iterator.hasNext()) {
			trackShop(iterator.next());
		}
		
		repeatingTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(OakShops.plugin, new Runnable() {
    		public void run() {
    			Iterator<Shop> shopIterator = shops.iterator();
    			while (shopIterator.hasNext())
    			{
    				shopIterator.next().refreshDisplay(false);
    			}
    		}
    	}, repeatTicks, repeatTicks);
	}
	
	public void unload() {
		HandlerList.unregisterAll(this);
		Bukkit.getServer().getScheduler().cancelTask(repeatingTaskID);
		Iterator<Shop> shopIterator = shops.iterator();
		while (shopIterator.hasNext())
		{
			Shop shop = shopIterator.next();
			shop.clearDisplay();
			shop.getBaseLocation().getBlock().setType(Material.AIR);
		}
		shops.clear();
		locations.clear();
		shopsByLocation.clear();
	}
	
	public void trackShop(Shop shop) {
		Location base = shop.getBaseLocation();
		Location top = base.clone().add(0,1,0);
		
		if (!shopChunkBases.containsKey(base.getChunk().getBlock(0, 0, 0).getLocation())) {
			Entity[] possibleDupes = base.getChunk().getEntities();
			for (int i=0; i < possibleDupes.length; i++) {
				Entity next = possibleDupes[i];
				if (next instanceof Item) {
					next.remove();
				}
			}
		}
		
		locations.add(base);
		locations.add(top);
		shopChunkBases.put(base.getChunk().getBlock(0, 0, 0).getLocation(), shop);
		shops.add(shop);
		shopsByLocation.put(base, shop);
		shopsByLocation.put(top, shop);
		shop.refreshDisplay(false);
	}
	
	public void untrackShop(Shop shop) {
		Location base = shop.getBaseLocation();
		Location top = base.clone().add(0,1,0);
		locations.remove(base);
		locations.remove(top);
		shopChunkBases.removeAll(base.getChunk().getBlock(0, 0, 0).getLocation());
		shops.remove(shop);
		shopsByLocation.remove(base);
		shopsByLocation.remove(top);
		shop.clearDisplay();
		DBCode.revertShopBlock(shop.getShopID(), base);
	}
	
	public Shop getShop(Location location) {
		return shopsByLocation.get(location);
	}
	
	public boolean isShopHere(Location location) {
		return locations.contains(location);
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
	
	@EventHandler (priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {
		Location location =  event.getChunk().getBlock(0, 0, 0).getLocation();
		if (shopChunkBases.containsKey(location)) {
			Iterator<Shop> iterator = shopChunkBases.get(location).iterator();
			while (iterator.hasNext()) {
				iterator.next().refreshDisplay(false);
			}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		Location location =  event.getChunk().getBlock(0, 0, 0).getLocation();
		if (shopChunkBases.containsKey(location)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (locations.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (locations.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		Iterator<Block> blocks = event.getBlocks().iterator();
		while (blocks.hasNext()) {
			if (locations.contains(blocks.next().getLocation())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		if (locations.contains(event.getRetractLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPistonChangeBlock(EntityChangeBlockEvent event) {
		if (locations.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		ArrayList<Block> removalList = new ArrayList<Block>();
		List<Block> blockList = event.blockList();
		Iterator<Block> blocks = blockList.iterator();
		while (blocks.hasNext()) {
			Block nextBlock = blocks.next();
			Location location = nextBlock.getLocation();
			if (locations.contains(location)) {
				removalList.add(nextBlock);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OakShops.plugin, 
						new RefreshTask(shopsByLocation.get(location), false));
			}
		}
		Iterator<Block> removal = removalList.iterator();
		while (removal.hasNext()) {
			blockList.remove(removal.next());
			
		}
	}	
}
