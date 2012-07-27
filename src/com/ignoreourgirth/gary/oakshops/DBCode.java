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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;

public class DBCode {

	public static String[] getSpellInfo(int spellID) {
		String[] returnValue = new String[2];
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT " +
					"SpellName, Description " +
					"FROM oakmagic_spells WHERE SpellID=?;");
			statement.setInt(1, spellID);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				returnValue[0] = result.getString(1);
				returnValue[1] = result.getString(2);
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
		return returnValue;
	}
	
	public static List<Shop> loadAllShops() {
		List<Shop> returnValue = new ArrayList<Shop>();
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT " +
					"shopID, type, owner, world, x, y, z, price, inventory, maxInventory, " + 
					"materialID, materialData, enchantmentIDs, enchantmentLevels, flag_DevShop " +
					"FROM oakshops_active;");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				int shopID = result.getInt(1);
				ShopType shopType = ShopType.getByID(result.getInt(2));
				String owner = result.getString(3);
				World world = Bukkit.getServer().getWorld(result.getString(4));
				Location shopLocation = new Location(
						world, result.getInt(5), result.getInt(6), result.getInt(7));
				double price = result.getDouble(8);
				int inventory = result.getInt(9);
				int maxInventory = result.getInt(10);
				ItemStack item = new ItemStack(result.getInt(11), 1, (short) 0, result.getByte(12));
				String enchantmentIDList = result.getString(13);
				if (enchantmentIDList != null) {
					String[] enchantIDs = enchantmentIDList.split(",\\s*");
					String[] enchantLevels = result.getString(14).split(",\\s*");
					for (int i = 0; i < enchantIDs.length; i++) {
						int nextID = Integer.parseInt(enchantIDs[i]);
						int nextLevel = Integer.parseInt(enchantLevels[i]);
						item.addUnsafeEnchantment(Enchantment.getById(nextID), nextLevel);
					}
				}
				if (item.getType() ==  Material.AIR) item = null;
				boolean flag_DevShop = result.getBoolean(15);
				Shop nextShop = new Shop(shopID, shopType, owner, shopLocation,
						item, inventory, maxInventory, price, flag_DevShop);
				returnValue.add(nextShop);
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
		return returnValue;
	}
	
	public static Shop createShop(Player owner, Location location) {
		try {
			Block block = location.getBlock();
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"INSERT INTO oakshops_active " +
					"(originalBlockID, originalBlockData, type, owner, " +
					"world, x, y, z, price, inventory, maxInventory) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?,?);"
					, Statement.RETURN_GENERATED_KEYS);
			String ownerName = owner.getName();
			statement.setInt(1, block.getType().getId());
			statement.setByte(2, block.getData());
			statement.setInt(3, ShopType.Sell.getID());
			statement.setString(4, ownerName);
			statement.setString(5, location.getWorld().getName());
			statement.setInt(6, location.getBlockX());
			statement.setInt(7, location.getBlockY());
			statement.setInt(8, location.getBlockZ());
			statement.setInt(9, 0);
			statement.setInt(10, 0);
			statement.setInt(11, 64);
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			int ID = result.getInt(1);
			result.close();
			statement.close();
			Shop newShop = new Shop(ID, owner.getName(), location);
			OakShops.tracker.trackShop(newShop);
			return newShop;
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}
	
	public static void revertShopBlock(int shopID, Location shopBase) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"SELECT originalBlockID, originalBlockData " +
					"FROM oakshops_active WHERE shopID=?;");
			statement.setInt(1, shopID);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				Block block = shopBase.getBlock();
				block.setType(Material.getMaterial(result.getInt(1)));
				Byte data = result.getByte(2);
				if (data != null) {
					block.setData(data);
				}
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void deleteShop(Shop shop, Player deleter) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"DELETE FROM oakshops_active " +
					"WHERE shopID=?;");
			statement.setInt(1, shop.getShopID());
			statement.executeUpdate();
			statement.close();
			statement = OakCoreLib.getDB().prepareStatement("" +
					"INSERT INTO minecraft.oakshops_deletionlog " + 
					"(shopID, timestamp, owner, deleter) VALUES(?,?,?,?);");
			statement.setInt(1, shop.getShopID());
			statement.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
			statement.setString(3, shop.getOwner());
			statement.setString(4, deleter.getName());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void logTransaction(Shop shop, Player player, int itemsTraded) {
		try {
			ItemStack item = shop.getItem();
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"INSERT INTO oakshops_tradelog " +
					"(timestamp, shopID, tradeType , player, money, itemCount, " +
					"materialID, materialData, enchantmentIDs, enchantmentLevels) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?);");
			statement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
			statement.setInt(2, shop.getShopID());
			statement.setInt(3, shop.getType().getID());
			statement.setString(4, player.getName());
			statement.setDouble(5, shop.getPrice() * itemsTraded);
			statement.setInt(6, itemsTraded);
			statement.setInt(7, item.getType().getId());
			statement.setByte(8, item.getData().getData());
			StringBuilder enchantmentIDs = new StringBuilder();
			StringBuilder enchantmentLevels = new StringBuilder();
			Iterator<Entry<Enchantment, Integer>> iterator = item.getEnchantments().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Enchantment,Integer> entry = iterator.next();
				enchantmentIDs.append(entry.getKey().getId());
				enchantmentLevels.append(entry.getValue());
				if (iterator.hasNext()) {
					enchantmentIDs.append(',');
					enchantmentLevels.append(',');
				}
			}
			if (enchantmentIDs.length() > 0) {
				statement.setString(9, enchantmentIDs.toString());
				statement.setString(10, enchantmentLevels.toString());
			} else {
				statement.setString(9, null);
				statement.setString(10, null);
			}
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void printTransactionData(Shop shop, Player player, int daysToSearch) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, daysToSearch * -1);
			Timestamp timestamp = new Timestamp(cal.getTime().getTime());
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"SELECT tradeType, money " +
					"FROM oakshops_tradelog WHERE shopID=? AND timestamp > ?;");
			statement.setInt(1, shop.getShopID());
			statement.setTimestamp(2, timestamp);
			ResultSet result = statement.executeQuery();
			int transactions = 0;
			double moneySpent = 0;
			double moneyEarned = 0;
			while (result.next()) {
				ShopType type = ShopType.getByID(result.getInt(1));
				if (type == ShopType.Sell) {
					moneyEarned += result.getDouble(2);
				} else if (type == ShopType.Buy) {
					moneySpent += result.getDouble(2);
				}
				transactions++;
			}
			result.close();
			statement.close();
			String header = "" + ChatColor.BLUE;
			if (daysToSearch > 1) {
				header += "Statistics for the last " + daysToSearch + " days...";
			} else {
				header += "Statistics for the last 24 hours...";
			}
			player.sendMessage(header);
			player.sendMessage("" +
					ChatColor.WHITE + ChatColor.BOLD + "Trades: " + 
					 ChatColor.RESET + ChatColor.DARK_AQUA + transactions + "   " + 
					ChatColor.WHITE + ChatColor.BOLD + "Spent: " + 
					 ChatColor.RESET + ChatColor.DARK_AQUA  + StringFormats.toCurrency(moneySpent, false) + "   " +
					ChatColor.WHITE + ChatColor.BOLD + "Earned: " + 
					 ChatColor.RESET + ChatColor.DARK_AQUA  + StringFormats.toCurrency(moneyEarned, false));
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void setItem(int ID, ItemStack item) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"UPDATE oakshops_active SET " +
					"materialID=?, materialData=?, enchantmentIDs=?, enchantmentLevels=? " +
					"WHERE shopID=?");
			statement.setInt(1, item.getType().getId());
			statement.setByte(2, item.getData().getData());
			StringBuilder enchantmentIDs = new StringBuilder();
			StringBuilder enchantmentLevels = new StringBuilder();
			Iterator<Entry<Enchantment, Integer>> iterator = item.getEnchantments().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Enchantment,Integer> entry = iterator.next();
				enchantmentIDs.append(entry.getKey().getId());
				enchantmentLevels.append(entry.getValue());
				if (iterator.hasNext()) {
					enchantmentIDs.append(',');
					enchantmentLevels.append(',');
				}
			}
			if (enchantmentIDs.length() > 0) {
				statement.setString(3, enchantmentIDs.toString());
				statement.setString(4, enchantmentLevels.toString());
			} else {
				statement.setString(3, null);
				statement.setString(4, null);
			}
			statement.setInt(5, ID);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}

	public static void setField(int ID, String field, String value) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"UPDATE oakshops_active SET " + field + "=? WHERE shopID=?");
			statement.setString(1, value);
			statement.setInt(2, ID);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void setField(int ID, String field, int value) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"UPDATE oakshops_active SET " + field + "=? WHERE shopID=?");
			statement.setInt(1, value);
			statement.setInt(2, ID);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void setField(int ID, String field, double value) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"UPDATE oakshops_active SET " + field + "=? WHERE shopID=?");
			statement.setDouble(1, value);
			statement.setInt(2, ID);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static void setField(int ID, String field, boolean value) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"UPDATE oakshops_active SET " + field + "=? WHERE shopID=?");
			statement.setBoolean(1, value);
			statement.setInt(2, ID);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakShops.log.log(Level.SEVERE, e.getMessage());
		}
	}


}
