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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor.OnCommand;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;

public class Commands {

	@OnCommand(value="shop.new", clickCommand=true)
	public void newShop(Player player, Location location) {
		if (!OakShops.tracker.isShopHere(location)) {
			if (Utils.disallowedBlockTypes.contains(location.getBlock().getType()) ||
					Utils.disallowedBlockTypes.contains(location.clone().add(new Vector(0,1,0)).getBlock().getType())) {
				player.sendMessage(ChatColor.RED + "You can not build a shop on this block type.");
				return;
			}
			if (location.clone().add(new Vector(0,1,0)).getBlock().getType() != Material.AIR) {
				player.sendMessage(ChatColor.RED + "You can not build a shop inside a wall.");
				return;
			}
			if (!OakCoreLib.canBuild(player, location)) {
				player.sendMessage(ChatColor.RED + "You do not have build rights for this spot.");
				return;
			}
			DBCode.createShop(player, location);
			player.sendMessage(ChatColor.DARK_AQUA + "Shop created.");
		} else {
			player.sendMessage(ChatColor.RED + "There is already a shop here.");
		}
	}
	
	@OnCommand(value="shop.set.type.sell", clickCommand=true)
	public void setTypeSell(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		shop.setType(ShopType.Sell);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop type set to Sell.");
	}
	
	@OnCommand(value="shop.set.type.buy", clickCommand=true)
	public void setTypeBuy(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		shop.setType(ShopType.Buy);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop type set to Buy.");
	}
	
	@OnCommand(value="shop.set.item.inhand", clickCommand=true)
	public void setItemThis(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		if (shop.getInventory() > 0) {
			player.sendMessage(ChatColor.RED + "You must first remove all items from shop inventory.");
			return;
		}
		ItemStack inHand = player.getItemInHand();
		if (inHand.getType() == Material.AIR) {
			player.sendMessage(ChatColor.RED + "You must first select an item.");
			return;
		}
		shop.setItem(player.getItemInHand());
		player.sendMessage(ChatColor.DARK_AQUA + "Shop item type registered.");
	}

	@OnCommand(value="shop.set.item.name", clickCommand=true, labels="ItemName")
	public void setItemName(Player player, Location location, String itemName) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		itemName = itemName.toUpperCase();
		if (Material.getMaterial(itemName) == null) {
			player.sendMessage(ChatColor.RED + "An item with that name could not be found.");
			return;
		}
		ItemStack item = new ItemStack(Material.getMaterial(itemName));
		if (shop.getInventory() > 0) {
			player.sendMessage(ChatColor.RED + "You must first remove all items from shop inventory.");
			return;
		}
		shop.setItem(item);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop item type set to " + item.getType().toString().replace('_', ' ') + ".");
	}
	
	@OnCommand(value="shop.set.item.id", clickCommand=true, labels="ItemID, itemData")
	public void setItemID(Player player, Location location, int itemID, byte itemData) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		if (Material.getMaterial(itemID) == null) {
			player.sendMessage(ChatColor.RED + "That is not a valid minecraft item ID.");
			return;
		}
		ItemStack item = new ItemStack(itemID, 1, (short) 0, itemData);
		if (shop.getInventory() > 0) {
			player.sendMessage(ChatColor.RED + "You must first remove all items from shop inventory.");
			return;
		}
		shop.setItem(item);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop item type set to " + item.getType().toString().replace('_', ' ') + ".");
	}
	
	@OnCommand(value="shop.set.price", clickCommand=true)
	public void setPrice(Player player, Location location, double newPrice) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		if (newPrice < 0) {
			player.sendMessage(ChatColor.RED + "Value can not be negative");
			return;
		}
		shop.setPrice(newPrice);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop price set to " + StringFormats.toCurrency(newPrice) + " " + OakCoreLib.getEconomy().currencyNamePlural() + " per unit.");
	}
	
	@OnCommand(value="shop.set.maxbuy", clickCommand=true)
	public void setPrice(Player player, Location location, int newCount) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		if (newCount < 0) {
			player.sendMessage(ChatColor.RED + "Value can not be negative");
			return;
		}
		shop.setMaxInventory(newCount);
		player.sendMessage(ChatColor.DARK_AQUA + "Set maximum purchase inventory to " + newCount + " items.");
	}
	
	@OnCommand(value="shop.stock.add.all", clickCommand=true)
	public void addStock(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		ItemStack shopItem = shop.getItem();
		if (shopItem == null) {
			player.sendMessage(ChatColor.RED + "Please first set the item type of this shop.");
			return;
		}
		int totalAdded = Utils.takeItemsFromPlayer(player, shopItem, Integer.MAX_VALUE);
		if (totalAdded == 0) {
			player.sendMessage(ChatColor.RED + "You do not have any undamaged items of the correct type.");
			return;
		}
		shop.setInventory(shop.getInventory() + totalAdded);
		player.sendMessage(ChatColor.DARK_AQUA + "Added " + totalAdded + " item(s) to shop inventory.");
	}
	
	@OnCommand(value="shop.stock.add", clickCommand=true)
	public void addStock(Player player, Location location, int count) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		ItemStack shopItem = shop.getItem();
		if (shopItem == null) {
			player.sendMessage(ChatColor.RED + "Please first set the item type of this shop.");
			return;
		}
		if (count == 0) {
			player.sendMessage(ChatColor.RED + "You can not add \"0\" items...");
			return;
		}
		int totalAdded = Utils.takeItemsFromPlayer(player, shopItem, count);
		if (totalAdded == 0) {
			player.sendMessage(ChatColor.RED + "You do not have any undamaged items of the correct type.");
			return;
		}
		shop.setInventory(shop.getInventory() + totalAdded);
		player.sendMessage(ChatColor.DARK_AQUA + "Added " + totalAdded + " item(s) to shop inventory.");
	}
	
	@OnCommand(value="shop.stock.remove.all", clickCommand=true)
	public void removeStockAll(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		ItemStack shopItem = shop.getItem();
		if (shopItem == null) {
			player.sendMessage(ChatColor.DARK_AQUA + "Please first set the item type of this shop.");
			return;
		}
		int initalInventory = shop.getInventory();
		if (initalInventory == 0) {
			player.sendMessage(ChatColor.DARK_AQUA + "The shop has no inventory remaining.");
			return;
		}
		int notRemoved = Utils.giveItemsToPlayer(player, shopItem, initalInventory);
		shop.setInventory(notRemoved);
		if (notRemoved > 0) {
			player.sendMessage(ChatColor.DARK_AQUA + "Removed " +  (initalInventory - notRemoved) + " item(s). Not enough inventory space to remove all.");
		} else {
			player.sendMessage(ChatColor.DARK_AQUA + "Removed all (" +  (initalInventory - notRemoved) + ") item(s).");
		}
	}
	
	@OnCommand(value="shop.stock.remove", clickCommand=true)
	public void removeStock(Player player, Location location, int count) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		ItemStack shopItem = shop.getItem();
		if (shopItem == null) {
			player.sendMessage(ChatColor.DARK_AQUA + "Please first set the item type of this shop.");
			return;
		}
		int initalInventory = shop.getInventory();
		if (initalInventory == 0) {
			player.sendMessage(ChatColor.DARK_AQUA + "The shop has no inventory remaining.");
			return;
		} else if (count > initalInventory && count > 1) {
			player.sendMessage(ChatColor.DARK_AQUA + "The shop does not have that many items in inventory.");
			return;
		}
		int notRemoved = Utils.giveItemsToPlayer(player, shopItem, count);
		shop.setInventory(initalInventory - count + notRemoved);
		if (notRemoved > 0) {
			player.sendMessage(ChatColor.DARK_AQUA + "Removed " +  (count - notRemoved) + " item(s). Not enough inventory space to remove rest.");
		} else {
			player.sendMessage(ChatColor.DARK_AQUA + "Removed requested (" +  count + ") item(s).");
		}
	}
	
	@OnCommand(value="shop.stats.day", clickCommand=true)
	public void getStatisticsA(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		DBCode.printTransactionData(shop, player, 1);
	}
	
	@OnCommand(value="shop.stats.week", clickCommand=true)
	public void getStatisticsB(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		DBCode.printTransactionData(shop, player, 7);
	}
	
	@OnCommand(value="shop.stats.month", clickCommand=true)
	public void getStatisticsC(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		DBCode.printTransactionData(shop, player, 30);
	}
	
	@OnCommand(value="shop.stats.year", clickCommand=true)
	public void getStatisticsD(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		DBCode.printTransactionData(shop, player, 365);
	}
	
	@OnCommand(value="shop.delete", clickCommand=true)
	public void deleteShop(Player player, Location location) {
		Shop shop = Utils.validate(player, location);
		if (shop == null) return;
		if (!OakCoreLib.canBuild(player, location)) {
			player.sendMessage(ChatColor.RED + "Only an admin can delete a shop in a restricted zone.");
			return;
		}
		if (shop.getInventory() > 0) {
			player.sendMessage(ChatColor.DARK_RED + "You must first remove all items from shop inventory.");
			return;
		}
		OakShops.tracker.untrackShop(shop);
		DBCode.deleteShop(shop, player);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop deleted.");
	}
	
	@OnCommand(value="shop.admin.owner", labels="OwnerName", clickCommand=true)
	public void adminOwner(Player player, Location location, String ownerName) {
		Shop shop = Utils.validateAdmin(player, location);
		String formatedName = Utils.getActualPlayerName(ownerName);
		if (formatedName != null) {
			shop.setOwner(formatedName);
			player.sendMessage(ChatColor.DARK_AQUA + "Shop ownership given to " + formatedName  + ". [ADMIN]");
		} else {
			player.sendMessage(ChatColor.DARK_RED + "A player by this name does not exist.");
		}
	}

	@OnCommand(value="shop.admin.delete", clickCommand=true)
	public void adminDelete(Player player, Location location) {
		Shop shop = Utils.validateAdmin(player, location);
		OakShops.tracker.untrackShop(shop);
		DBCode.deleteShop(shop, player);
		player.sendMessage(ChatColor.DARK_AQUA + "Shop deleted. [ADMIN]");
	}
	
	@OnCommand(value="shop.admin.flag.devshop", clickCommand=true)
	public void adminFlag_DevShop(Player player, Location location) {
		Shop shop = Utils.validateAdmin(player, location);
		shop.setFlag_DevShop(!shop.getFlag_DevShop());
		player.sendMessage(ChatColor.DARK_AQUA + "Shop flag (DEVSHOP:" + shop.getFlag_DevShop() + ") [ADMIN]");
	}
	
}
