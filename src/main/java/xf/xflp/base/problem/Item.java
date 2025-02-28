package xf.xflp.base.problem;


import util.Copyable;
import util.collection.Indexable;
import xf.xflp.report.PackageEventType;

import java.util.Set;

/** 
 * Copyright (c) 2012-present Holger Schneider
 * All rights reserved.
 *
 * This source code is licensed under the MIT License (MIT) found in the
 * LICENSE file in the root directory of this source tree.
 *
 *
 * A Item object wraps a Package object with planning informations
 * 
 * @author Hogo
 *
 */
public class Item implements Copyable<Item>, Cloneable, Indexable {

	public int size, volume, h;
	public int x, y, z, xw, yl, zh, w, l;
	
	public boolean spinable, stackable;
	public int loadingLoc, unLoadingLoc;
	
	// Binary representation, where only one bit can be active
	public int stackingGroup;
	// Allowed container types (cooled, dangerous goods, etc.)
	public Set<Integer> allowedContainerSet;
	// Allowed items that can be stacked on top (binary representation)
	public int allowedStackingGroups;

	public float weight;
	public float stackingWeightLimit;
	
	/* Unique index of this item object*/
	public int externalIndex;
	/* Type of item: loading or unloading */
	public PackageEventType loadingType;
	/* External externalIndex of this order. There can be two items
	 * with the same order externalIndex (up- and unloading) */
	public int orderIndex = -1;
	/* Idx in data structure of its holding container */
	/* -1 if its unpacked */
	public int index = -1;
	/* Idx of the container, where the item is packed in. */
	/* -1 if its unpacked */
	public int containerIndex = -1;
	
	// Defines if this item is loaded (true) or unloaded (false)
	public boolean isLoading = false;
	// Defines if this item was rotated (true) or not rotated (false)
	public boolean isRotated = false;

	public Item() {
		this.x = this.y = this.z = this.xw = this.yl = this.zh = -1;
		this.stackable = true;
	}

	public void postInit() {
		this.size = w * l;
		this.volume = h * w * l;
		this.loadingType = (isLoading) ? PackageEventType.LOAD : PackageEventType.UNLOAD;
	}
	
	/**
	 * 
	 */
	public void rotate() {
		int tmp = w;
		w = l;
		l = tmp;
		
		isRotated = !isRotated;
	}
	
	/**
	 * 
	 * @param pos
	 */
	public void setPosition(Position pos) {
		x = pos.x;
		y = pos.y;
		z = pos.z;
		xw = x + w;
		yl = y + l;
		zh = z + h;
	}
	
	/**
	 * 
	 */
	public void clearPosition() {
		this.x = this.y = this.z = this.xw = this.yl = this.zh = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see da.util.Copyable#copy()
	 */
	@Override
	public Item copy() {
		try {
			return (Item)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Item "+this.externalIndex+" "+loadingLoc+" "+unLoadingLoc+" ("+w+","+l+","+h+") ["+x+", "+y+", "+z+" "+(this.isRotated?"R":"")+"]";
	}
	
	/**
	 * 
	 */
	public void reset() {
		clearPosition();
		this.index = -1;
		this.containerIndex = -1;
		if(this.isRotated)
			rotate();
		this.isLoading = false;
	}

	/*
	 * (non-Javadoc)
	 * @see util.collection.Indexable#getIdx()
	 */
	@Override
	public int getIdx() {
		return index;
	}
	
	/*
	 * (non-Javadoc)
	 * @see util.collection.Indexable#setIdx(int)
	 */
	@Override
	public void setIdx(int idx) {
		this.index = idx;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getXw() {
		return xw;
	}

	public void setXw(int xw) {
		this.xw = xw;
	}

	public int getYl() {
		return yl;
	}

	public void setYl(int yl) {
		this.yl = yl;
	}

	public int getZh() {
		return zh;
	}

	public void setZh(int zh) {
		this.zh = zh;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public boolean isSpinable() {
		return spinable;
	}

	public void setSpinable(boolean spinable) {
		this.spinable = spinable;
	}

	public boolean isStackable() {
		return stackable;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public int getLoadingLoc() {
		return loadingLoc;
	}

	public void setLoadingLoc(int loadingLoc) {
		this.loadingLoc = loadingLoc;
	}

	public int getUnLoadingLoc() {
		return unLoadingLoc;
	}

	public void setUnLoadingLoc(int unLoadingLoc) {
		this.unLoadingLoc = unLoadingLoc;
	}

	public int getStackingGroup() {
		return stackingGroup;
	}

	public void setStackingGroup(int stackingGroup) {
		this.stackingGroup = stackingGroup;
	}

	public Set<Integer> getAllowedContainerSet() {
		return allowedContainerSet;
	}

	public void setAllowedContainerSet(Set<Integer> allowedContainerSet) {
		this.allowedContainerSet = allowedContainerSet;
	}

	public int getAllowedStackingGroups() {
		return allowedStackingGroups;
	}

	public void setAllowedStackingGroups(int allowedStackingGroups) {
		this.allowedStackingGroups = allowedStackingGroups;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getStackingWeightLimit() {
		return stackingWeightLimit;
	}

	public void setStackingWeightLimit(float stackingWeightLimit) {
		this.stackingWeightLimit = stackingWeightLimit;
	}

	public int getExternalIndex() {
		return externalIndex;
	}

	public void setExternalIndex(int externalIndex) {
		this.externalIndex = externalIndex;
	}

	public PackageEventType getLoadingType() {
		return loadingType;
	}

	public void setLoadingType(PackageEventType loadingType) {
		this.loadingType = loadingType;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getContainerIndex() {
		return containerIndex;
	}

	public void setContainerIndex(int containerIndex) {
		this.containerIndex = containerIndex;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean loading) {
		isLoading = loading;
	}

	public boolean isRotated() {
		return isRotated;
	}

	public void setRotated(boolean rotated) {
		isRotated = rotated;
	}
}
