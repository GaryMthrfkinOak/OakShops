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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Workaround for huge bukkit inventory bug.
public class InventoryModificationTask implements Runnable {

	private ItemStack taskStack;
	private Player taskPlayer;
	private Integer taskSlotIndex;
	
	InventoryModificationTask(Player player, Integer slotIndex, ItemStack stack) {
		taskStack = stack;
		taskPlayer = player;
		taskSlotIndex = slotIndex;
	}

	
	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		if (taskStack != null) {
			taskStack = new ItemStack(taskStack);
			if (taskSlotIndex != null) {
				taskPlayer.getInventory().setItem(taskSlotIndex, taskStack);
			} else {
				taskPlayer.getInventory().addItem(taskStack);
			}
		} else {
			if (taskSlotIndex != null) taskPlayer.getInventory().clear(taskSlotIndex);
		}
		
		// Okay... sure... let's not use the only method that fixes the client inventory bug.
		// Lets just use the correct method. Oh wait, that doesn't exist. 11/10 best mod 200% good job Bukkit.
		taskPlayer.updateInventory();
	}
	
}
