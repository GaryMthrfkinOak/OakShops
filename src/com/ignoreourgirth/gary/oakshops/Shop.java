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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakcorelib.DisplayItems;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;

public class Shop {

	private Integer displayID;
	private Location center;
	
	protected Shop(int ID, String ownerName, Location location) {
		displayID = null;
		shopID = ID;
		type = ShopType.Sell;
		base = location.clone();
		center = base.clone().add(.5, 1.2, .5);
		owner = ownerName;
		inventory = 0;
		maxInventory = 64;
		flag_DevShop = false;
		base.getBlock().setType(Material.IRON_BLOCK);
	}
	
	protected Shop(int ID, ShopType shopType, String ownerName, Location location, ItemStack baseItem, int itemCount, int itemsMax, double itemPrice, boolean flags_DevShop) {
		displayID = null;
		shopID = ID;
		type = shopType;
		base = location.clone();
		center = base.clone().add(.5, 1.2, .5);
		owner = ownerName;
		item = baseItem;
		inventory = itemCount;
		maxInventory = itemsMax;
		price = itemPrice;
		flag_DevShop = flags_DevShop;
 
		switch (type) {
			case Sell: base.getBlock().setType(Material.IRON_BLOCK); break;
			case Buy: base.getBlock().setType(Material.GOLD_BLOCK); break;
		}	
	}
	
	private int shopID;
	public int getShopID() { return shopID; }
	
	private String owner;
	public String getOwner() { return owner; }
	public void setOwner(String newOwner) { 
		owner = newOwner;
		DBCode.setField(shopID, "owner", owner);
	}
	public boolean isOwner(Player player) {
		return (player.getName().equals(owner));
	}
	
	private Location base;
	public Location getBaseLocation() { return base.clone(); }
	
	private ShopType type;
	public ShopType getType() { return type; }
	public void setType(ShopType newType) { 
		type = newType;
		DBCode.setField(shopID, "type", type.getID());
		if (type  == ShopType.Sell) {
			base.getBlock().setType(Material.IRON_BLOCK);
		} else if (type  == ShopType.Buy) {
			base.getBlock().setType(Material.GOLD_BLOCK);
		}
		showDisplay(true);
	}
	
	private ItemStack item;
	protected ItemStack getItem() { 
		if (item != null) {
			return item.clone();
		} else {
			return null;
		}	 
	}
	
	protected void setItem(ItemStack newItem) {
		newItem = newItem.clone();
		newItem.setAmount(1);
		item = newItem;
		setInventory(0);
		DBCode.setItem(shopID, newItem);
		showDisplay(false);
	}

	private int inventory;
	public int getInventory() { return inventory; }
	public void setInventory(int newCount) { 
		inventory = newCount;
		DBCode.setField(shopID, "inventory", inventory);
		showDisplay(true);
	}
	
	private int maxInventory;
	public double getMaxInventory() { return maxInventory; }
	public void setMaxInventory(int newCount) {
		maxInventory = newCount;
		DBCode.setField(shopID, "maxInventory", newCount);
		if (type == ShopType.Buy) showDisplay(true);
	}
	
	private double price;
	public double getPrice() { return price; }
	public void setPrice(double newPrice) { 
		price = newPrice;
		DBCode.setField(shopID, "price", price);
	}
	
	private boolean flag_DevShop;
	public boolean getFlag_DevShop() {return flag_DevShop; }
	public void setFlag_DevShop(boolean newValue) {
		flag_DevShop = newValue;
		DBCode.setField(shopID, "flag_DevShop", flag_DevShop);
	}
	
	public void showDisplay(boolean relaxed) {
		if ((inventory == 0 && type == ShopType.Sell) ||
				inventory >= maxInventory && type == ShopType.Buy) {
			hideDisplay();
			return;
		} else if (!relaxed || displayID == null) {
			if (center.getChunk().isLoaded()) {
				hideDisplay();
				displayID = DisplayItems.newItem(item, center, OakShops.plugin);
			}
		}
	}
	
	public void hideDisplay() {
		if (displayID != null) {
			DisplayItems.removeItem(displayID);
			displayID = null;
		}
	}
	
	public void leftClick(Player player) {
		if (item == null) return;
		ChatColor outline = ChatColor.GRAY;
		String readableMaterial = item.getType().toString().replace('_', ' ');
		String inventoryText = "" + ChatColor.WHITE + ChatColor.BOLD + "Stock: " 
				+ ChatColor.RESET + ChatColor.DARK_AQUA + String.valueOf(inventory);
		String magicText = "";
		String magicDescription = null;
		String priceText = "" + ChatColor.WHITE + ChatColor.BOLD + "  ";
		if (type == ShopType.Sell) {
			priceText += "Price: ";
		} else if (type == ShopType.Buy) {
			priceText += "Value: ";
			inventoryText += ChatColor.WHITE + "/" + ChatColor.DARK_AQUA + String.valueOf(maxInventory);
		}
		priceText += "" + ChatColor.RESET + ChatColor.DARK_AQUA;
		if (price > 0) {
			priceText += StringFormats.toCurrency(price, false); 
		} else {
			if (type == ShopType.Sell) {
				priceText += "Free"; 
			} else if (type == ShopType.Buy) {
				priceText += "Donation"; 
			}
		}
		if (flag_DevShop) inventoryText = "";
		int spellID = -1;
		if (item.getType() == Material.MAP) {
			Map<Enchantment, Integer> enchantments = item.getEnchantments();
			if (enchantments.get(Enchantment.ARROW_DAMAGE) != null) {
				spellID = enchantments.get(Enchantment.ARROW_DAMAGE);
				boolean isLearnable = (enchantments.get(Enchantment.ARROW_KNOCKBACK) != null);
				if (isLearnable) {
					readableMaterial = "Spell";
				} else {
					readableMaterial = "ONE TIME USE";
				}
				
				String level = StringFormats.toRomanNumeral(enchantments.get(Enchantment.DIG_SPEED));
				String[] info = DBCode.getSpellInfo(spellID);
				magicText = ("    " + 
				        ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "  Magic: " + ChatColor.RESET + ChatColor.DARK_PURPLE + info[0] + " " + level); 
				magicDescription = info[1];
			}
		}
		if (type == ShopType.Buy) outline = ChatColor.GOLD;
		player.sendMessage("" + outline + ChatColor.BOLD + "---------------------------------------------");
		player.sendMessage("" + 
				priceText + "    " +
				inventoryText + "    " + 
				ChatColor.WHITE + ChatColor.BOLD + "Item: " + ChatColor.RESET  + ChatColor.DARK_AQUA + readableMaterial +
				magicText);
		
		if (spellID == -1) {
			Map<Enchantment, Integer> enchantments = item.getEnchantments();
			Iterator<Entry<Enchantment, Integer>> iteratorA = enchantments.entrySet().iterator();
			while (iteratorA.hasNext()) {
				Entry<Enchantment, Integer> entry = iteratorA.next();
				player.sendMessage("    :: " + 
						ChatColor.LIGHT_PURPLE + Utils.friendlyEnchantmentNames.get(entry.getKey()) 
						+ " " + StringFormats.toRomanNumeral(entry.getValue()));
			}
		} else {
			player.sendMessage("" + 
					ChatColor.RED + ChatColor.BOLD + " *** " + ChatColor.WHITE + magicDescription); 
		}

		player.sendMessage("" + outline + ChatColor.BOLD + "---------------------------------------------");
	}
	
	public void rightClick(Player player) {
		if (item == null) return;
		boolean shift = (player.isSneaking());
		int maxStack = item.getMaxStackSize();
		if (!player.getName().equals(owner)) {
			if (type == ShopType.Sell) {
				int itemsToGive = 1;
				if (shift) itemsToGive = maxStack;
				if (inventory >= itemsToGive) {
					if (player.getInventory().firstEmpty() > - 1) {
						double balance = OakCoreLib.getEconomy().getBalance(player.getName());
						double bundlePrice = price * itemsToGive;
						if (balance >= bundlePrice) {
							OakCoreLib.getEconomy().bankWithdraw(player.getName(), bundlePrice);
							if (!flag_DevShop) OakCoreLib.getEconomy().bankDeposit(OakCoreLib.bankPrefix + owner, bundlePrice);
							Utils.giveItemsToPlayer(player, item, itemsToGive);
							if (!flag_DevShop) setInventory(inventory - itemsToGive);
							player.sendMessage(ChatColor.GREEN + "Purchased " + itemsToGive + " item(s) for " + bundlePrice + " " + OakCoreLib.getEconomy().currencyNamePlural() + ".");
							DBCode.logTransaction(this, player, itemsToGive);
						} else {
							player.sendMessage(ChatColor.RED + "You do not have enough money.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Not enough space in your inventory.");
					}
				} else {
					if (shift) {
						player.sendMessage(ChatColor.RED + "There is less than a full stack in the shop inventory.");
					} else {
						player.sendMessage(ChatColor.RED + "Out of stock.");
					}
				}
			} else if (type == ShopType.Buy) {
				int maxItemsToTake = 1;
				if (shift) maxItemsToTake = maxStack;
				if (maxItemsToTake + inventory > maxInventory) {
					if (shift) {
						player.sendMessage(ChatColor.RED + "This shop does not have room for another full stack.");
					} else {
						player.sendMessage(ChatColor.RED + "This shop does not have room for any more items.");
					}
					return;
				}
				double ownerBalance = OakCoreLib.getEconomy().getBalance(OakCoreLib.bankPrefix + owner);
				if (flag_DevShop) ownerBalance = Integer.MAX_VALUE;
				if (ownerBalance >= price * maxItemsToTake) {
					int taken = Utils.takeItemsFromPlayer(player, item, maxItemsToTake);
					if (taken > 0) {
						if (!flag_DevShop) OakCoreLib.getEconomy().bankWithdraw(OakCoreLib.bankPrefix + owner, price * taken);
						OakCoreLib.getEconomy().bankDeposit(player.getName(), price * taken);
						if (!flag_DevShop) setInventory(inventory + taken);
						player.sendMessage(ChatColor.GREEN + "Sold " + taken + " item(s) for " + price * taken + " " + OakCoreLib.getEconomy().currencyNamePlural() + ".");
						DBCode.logTransaction(this, player, taken);
					} else {
						player.sendMessage(ChatColor.RED + "You do not have any undamaged items of the correct type.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Sorry, the owner of this shop does not have enough credit.");
				}
			}
		}
	}
	
}
