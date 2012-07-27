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

public enum ShopType {
	Sell(1),
	Buy(2);
	
	ShopType(int typeID) {
		ID = typeID;
	}
	
	int ID;
	public int getID() {return ID;}
	public static ShopType getByID(int ID) {
		if (ID == 1) return Sell;
		if (ID == 2) return Buy;
		return null;
	}
}
