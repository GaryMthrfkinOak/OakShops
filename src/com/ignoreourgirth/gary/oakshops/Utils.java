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
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

	private static final String noShopThereText = ChatColor.RED + "There is no shop here.";
	private static final String notOwnerText = ChatColor.RED + "Access Denied. You are not the owner of this shop.";
	protected static HashMap<Enchantment, String> friendlyEnchantmentNames;
	protected static HashMap<Integer, String> friendlyDyeNames;
	protected static HashSet<Material> disallowedBlockTypes;
	protected static HashSet<Material> damageableItems;
	
	static {
		friendlyEnchantmentNames = new HashMap<Enchantment, String>();
		friendlyEnchantmentNames.put(Enchantment.ARROW_DAMAGE, "Power");
		friendlyEnchantmentNames.put(Enchantment.ARROW_FIRE, "Flame");
		friendlyEnchantmentNames.put(Enchantment.ARROW_INFINITE, "Infinity");
		friendlyEnchantmentNames.put(Enchantment.ARROW_KNOCKBACK, "Punch");
		friendlyEnchantmentNames.put(Enchantment.DAMAGE_ALL, "Sharpness");
		friendlyEnchantmentNames.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
		friendlyEnchantmentNames.put(Enchantment.DAMAGE_UNDEAD, "Smite");
		friendlyEnchantmentNames.put(Enchantment.DIG_SPEED, "Efficiency");
		friendlyEnchantmentNames.put(Enchantment.DURABILITY, "Unbreaking");
		friendlyEnchantmentNames.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
		friendlyEnchantmentNames.put(Enchantment.KNOCKBACK, "Knockback");
		friendlyEnchantmentNames.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
		friendlyEnchantmentNames.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
		friendlyEnchantmentNames.put(Enchantment.OXYGEN, "Respiration");
		friendlyEnchantmentNames.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
		friendlyEnchantmentNames.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
		friendlyEnchantmentNames.put(Enchantment.PROTECTION_FALL, "Feather Falling");
		friendlyEnchantmentNames.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
		friendlyEnchantmentNames.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
		friendlyEnchantmentNames.put(Enchantment.SILK_TOUCH, "Silk Touch");
		friendlyEnchantmentNames.put(Enchantment.WATER_WORKER, "Aqua Affinity");
		disallowedBlockTypes = new HashSet<Material>();
		disallowedBlockTypes.add(Material.BEDROCK);
		disallowedBlockTypes.add(Material.BED);
		disallowedBlockTypes.add(Material.BED_BLOCK);
		disallowedBlockTypes.add(Material.BREWING_STAND);
		disallowedBlockTypes.add(Material.BURNING_FURNACE);
		disallowedBlockTypes.add(Material.CAKE);
		disallowedBlockTypes.add(Material.CHEST);
		disallowedBlockTypes.add(Material.DETECTOR_RAIL);
		disallowedBlockTypes.add(Material.DIAMOND_BLOCK);
		disallowedBlockTypes.add(Material.DISPENSER);
		disallowedBlockTypes.add(Material.DIODE);
		disallowedBlockTypes.add(Material.DIODE_BLOCK_OFF);
		disallowedBlockTypes.add(Material.DIODE_BLOCK_ON);
		disallowedBlockTypes.add(Material.DRAGON_EGG);
		disallowedBlockTypes.add(Material.ENCHANTMENT_TABLE);
		disallowedBlockTypes.add(Material.ENDER_PORTAL);
		disallowedBlockTypes.add(Material.ENDER_PORTAL_FRAME);
		disallowedBlockTypes.add(Material.FIRE);
		disallowedBlockTypes.add(Material.FURNACE);
		disallowedBlockTypes.add(Material.GOLD_BLOCK);
		disallowedBlockTypes.add(Material.IRON_BLOCK);
		disallowedBlockTypes.add(Material.JUKEBOX);
		disallowedBlockTypes.add(Material.LADDER);
		disallowedBlockTypes.add(Material.LAPIS_BLOCK);
		disallowedBlockTypes.add(Material.LAVA);
		disallowedBlockTypes.add(Material.LEVER);
		disallowedBlockTypes.add(Material.MOB_SPAWNER);
		disallowedBlockTypes.add(Material.NOTE_BLOCK);
		disallowedBlockTypes.add(Material.PISTON_BASE);
		disallowedBlockTypes.add(Material.PISTON_EXTENSION);
		disallowedBlockTypes.add(Material.PISTON_MOVING_PIECE);
		disallowedBlockTypes.add(Material.PISTON_STICKY_BASE);
		disallowedBlockTypes.add(Material.PORTAL);
		disallowedBlockTypes.add(Material.POWERED_MINECART);
		disallowedBlockTypes.add(Material.POWERED_RAIL);
		disallowedBlockTypes.add(Material.RAILS);
		disallowedBlockTypes.add(Material.REDSTONE);
		disallowedBlockTypes.add(Material.REDSTONE_LAMP_OFF);
		disallowedBlockTypes.add(Material.REDSTONE_LAMP_ON);
		disallowedBlockTypes.add(Material.REDSTONE_TORCH_OFF);
		disallowedBlockTypes.add(Material.REDSTONE_TORCH_ON);
		disallowedBlockTypes.add(Material.REDSTONE_WIRE);
		disallowedBlockTypes.add(Material.SIGN);
		disallowedBlockTypes.add(Material.SIGN_POST);
		disallowedBlockTypes.add(Material.SPONGE);
		disallowedBlockTypes.add(Material.STATIONARY_LAVA);
		disallowedBlockTypes.add(Material.STATIONARY_WATER);
		disallowedBlockTypes.add(Material.STONE_BUTTON);
		disallowedBlockTypes.add(Material.STONE_PLATE);
		disallowedBlockTypes.add(Material.TNT);
		disallowedBlockTypes.add(Material.TRAP_DOOR);
		disallowedBlockTypes.add(Material.WALL_SIGN);
		disallowedBlockTypes.add(Material.WATER);
		disallowedBlockTypes.add(Material.WOODEN_DOOR);
		disallowedBlockTypes.add(Material.WOOD_PLATE);
		disallowedBlockTypes.add(Material.WORKBENCH);
		damageableItems = new HashSet<Material>();
		damageableItems.add(Material.WOOD_AXE);
		damageableItems.add(Material.WOOD_HOE);
		damageableItems.add(Material.WOOD_PICKAXE);
		damageableItems.add(Material.WOOD_SPADE);
		damageableItems.add(Material.WOOD_SWORD);
		damageableItems.add(Material.STONE_AXE);
		damageableItems.add(Material.STONE_HOE);
		damageableItems.add(Material.STONE_PICKAXE);
		damageableItems.add(Material.STONE_SPADE);
		damageableItems.add(Material.STONE_SWORD);
		damageableItems.add(Material.IRON_AXE);
		damageableItems.add(Material.IRON_HOE);
		damageableItems.add(Material.IRON_PICKAXE);
		damageableItems.add(Material.IRON_SPADE);
		damageableItems.add(Material.IRON_SWORD);
		damageableItems.add(Material.GOLD_AXE);
		damageableItems.add(Material.GOLD_HOE);
		damageableItems.add(Material.GOLD_PICKAXE);
		damageableItems.add(Material.GOLD_SPADE);
		damageableItems.add(Material.GOLD_SWORD);
		damageableItems.add(Material.DIAMOND_AXE);
		damageableItems.add(Material.DIAMOND_HOE);
		damageableItems.add(Material.DIAMOND_PICKAXE);
		damageableItems.add(Material.DIAMOND_SPADE);
		damageableItems.add(Material.DIAMOND_SWORD);
		damageableItems.add(Material.SHEARS);
		damageableItems.add(Material.FISHING_ROD);
		damageableItems.add(Material.BOW);
		damageableItems.add(Material.LEATHER_BOOTS);
		damageableItems.add(Material.LEATHER_CHESTPLATE);
		damageableItems.add(Material.LEATHER_HELMET);
		damageableItems.add(Material.LEATHER_LEGGINGS);
		damageableItems.add(Material.IRON_BOOTS);
		damageableItems.add(Material.IRON_CHESTPLATE);
		damageableItems.add(Material.IRON_HELMET);
		damageableItems.add(Material.IRON_LEGGINGS);
		damageableItems.add(Material.GOLD_BOOTS);
		damageableItems.add(Material.GOLD_CHESTPLATE);
		damageableItems.add(Material.GOLD_HELMET);
		damageableItems.add(Material.GOLD_LEGGINGS);
		damageableItems.add(Material.DIAMOND_BOOTS);
		damageableItems.add(Material.DIAMOND_CHESTPLATE);
		damageableItems.add(Material.DIAMOND_HELMET);
		damageableItems.add(Material.DIAMOND_LEGGINGS);
		friendlyDyeNames = new HashMap<Integer, String>();
		friendlyDyeNames.put(0, "Ink Sack");
		friendlyDyeNames.put(2, "Cactus Green Dye");
		friendlyDyeNames.put(3, "Coca Bean");
		friendlyDyeNames.put(4, "Lapis Lazuli");
		friendlyDyeNames.put(5, "Purple Dye");
		friendlyDyeNames.put(6, "Cyan Dye");
		friendlyDyeNames.put(7, "Light Gray Dye");
		friendlyDyeNames.put(8, "Gray Dye");
		friendlyDyeNames.put(9, "Pink Dye");
		friendlyDyeNames.put(10, "Lime Dye");
		friendlyDyeNames.put(11, "Dandelion Yellow Dye");
		friendlyDyeNames.put(12, "Light Blue Dye");
		friendlyDyeNames.put(13, "Magenta Dye");
		friendlyDyeNames.put(14, "Orange Dye");
		friendlyDyeNames.put(15, "Bone Meal"); 
	}
	
	public static Shop validate(Player player, Location location) {
		Shop shop = OakShops.tracker.getShop(location);
		if (shop != null) {
			if (shop.isOwner(player)) {
				return shop;
			} else {
				player.sendMessage(notOwnerText);
			}
		} else {
			player.sendMessage(noShopThereText);
		}
		return null;
	}
	
	public static Shop validateAdmin(Player player, Location location) {
		Shop shop = OakShops.tracker.getShop(location);
		if (shop != null) {
			return shop;
		} else {
			player.sendMessage(noShopThereText);
		}
		return null;
	}
	
	public static boolean sameItem(ItemStack a, ItemStack b) {
		if (a.getType() != b.getType()) return false;
		if (a.getData().getData() != b.getData().getData()) return false;
		Map<Enchantment, Integer> enchantsA = a.getEnchantments();
		Map<Enchantment, Integer> enchantsB = b.getEnchantments();
		if (enchantsA.size() != enchantsB.size()) return false;
		Iterator<Entry<Enchantment, Integer>> iteratorA = enchantsA.entrySet().iterator();
		while (iteratorA.hasNext()) {
			Entry<Enchantment, Integer> entry = iteratorA.next();
			Integer valueA = entry.getValue();
			Integer valueB = enchantsB.get(entry.getKey());
			if (valueA != valueB) return false;
		}
		return true;
	}
	
	public static int takeItemsFromPlayer(Player player, ItemStack baseStack, int remainingToTake) {
		Inventory inventory = player.getInventory();
		int totalTaken = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack nextStack = inventory.getItem(i);
			if (nextStack != null) {
				if (Utils.sameItem(nextStack, baseStack)) {
					if (! (damageableItems.contains(nextStack.getType()) 
							&& nextStack.getDurability() > 0)) {
						int nextCount = nextStack.getAmount();
						if (nextCount - remainingToTake > 0) {
							totalTaken += remainingToTake;
							nextStack.setAmount(nextStack.getAmount() - remainingToTake);
							remainingToTake = 0;
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OakShops.plugin, 
									new InventoryModificationTask(player, i, nextStack));
						} else {
							remainingToTake -= nextCount;
							totalTaken += nextCount;
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OakShops.plugin, 
									new InventoryModificationTask(player, i, null));
						}
						if (remainingToTake == 0) break;
					}
				}
			}
		}
		return totalTaken;
	}
	
	public static int giveItemsToPlayer(Player player, ItemStack baseStack, int remainingToGive) {
		Inventory inventory = player.getInventory();
		int maxStack = baseStack.getMaxStackSize();
		while (inventory.firstEmpty() > -1 && remainingToGive >= maxStack) {
			ItemStack fullStack = baseStack.clone();
			if (maxStack > 1) fullStack.setAmount(maxStack);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OakShops.plugin, 
					new InventoryModificationTask(player, null, fullStack));
			remainingToGive -= maxStack;
		}
		if (remainingToGive > 0 && inventory.firstEmpty() > -1) {
			ItemStack partialStack = baseStack.clone();
			partialStack.setAmount(remainingToGive);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OakShops.plugin, 
					new InventoryModificationTask(player, null, partialStack));
			remainingToGive = 0;
		}
		return remainingToGive;
	}
	
	public static String getActualPlayerName(String name) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) return player.getName();
		}
		for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) return player.getName();
		}
		return null;
	}
	
	public static String formatItemName(ItemStack stack) {
		String initalName = stack.getType().toString();
		
		if (stack.getType() == Material.MONSTER_EGG) {
			EntityType entityType = EntityType.fromId(stack.getData().getData());
			initalName = entityType.getName() + "_Egg";
		} else if (stack.getType() == Material.WOOL) { 
			DyeColor color = DyeColor.getByData(stack.getData().getData());
			initalName = color.toString() + "_Wool";
		} else if (stack.getType() == Material.INK_SACK) { 
			return friendlyDyeNames.get(((int)stack.getData().getData()));
		}
		
		StringBuilder result = new StringBuilder(initalName.length());
		String[] words = initalName.split("\\_");
		for(int i=0; i < words.length; i++) {
		  if(i>0) result.append(" ");      
		  result.append(Character.toUpperCase(words[i].charAt(0)));
		  result.append(words[i].substring(1).toLowerCase());
		}
		return result.toString();
	}
	
}
