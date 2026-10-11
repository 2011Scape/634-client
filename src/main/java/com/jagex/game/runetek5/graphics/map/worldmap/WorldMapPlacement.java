package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!jg")
public final class WorldMapPlacement extends Node {

	@OriginalMember(owner = "client!jg", name = "t", descriptor = "I")
	public final int displayMinZ;

	@OriginalMember(owner = "client!jg", name = "D", descriptor = "I")
	public final int displayZ1;

	@OriginalMember(owner = "client!jg", name = "E", descriptor = "I")
	private final int startX;

	@OriginalMember(owner = "client!jg", name = "v", descriptor = "I")
	private final int startY;

	@OriginalMember(owner = "client!jg", name = "z", descriptor = "I")
	private final int plane;

	@OriginalMember(owner = "client!jg", name = "w", descriptor = "I")
	public final int displayMaxZ;

	@OriginalMember(owner = "client!jg", name = "q", descriptor = "I")
	private final int endX;

	@OriginalMember(owner = "client!jg", name = "u", descriptor = "I")
	private final int endY;

	@OriginalMember(owner = "client!jg", name = "x", descriptor = "I")
	public final int displayZ2;

	@OriginalMember(owner = "client!jg", name = "<init>", descriptor = "(IIIIIIIII)V")
	public WorldMapPlacement(@OriginalArg(0) int plane, @OriginalArg(1) int startX, @OriginalArg(2) int startY, @OriginalArg(3) int endX, @OriginalArg(4) int endY, @OriginalArg(5) int mapMinX, @OriginalArg(6) int mapMinY, @OriginalArg(7) int mapMaxX, @OriginalArg(8) int mapMaxY) {
		this.displayMinZ = mapMinX;
		this.displayZ1 = mapMaxX;
		this.startX = startX;
		this.startY = startY;
		this.plane = plane;
		this.displayMaxZ = mapMinY;
		this.endX = endX;
		this.endY = endY;
		this.displayZ2 = mapMaxY;
	}

	@OriginalMember(owner = "client!jg", name = "b", descriptor = "(III)Z")
	public boolean withinBounds(@OriginalArg(1) int x, @OriginalArg(2) int y) {
		return this.startX <= x && this.endX >= x && y >= this.startY && this.endY >= y;
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(IIIZ)Z")
	public boolean withinPlaneBounds(@OriginalArg(0) int y, @OriginalArg(1) int x, @OriginalArg(2) int plane) {
		return plane == this.plane && this.startX <= x && this.endX >= x && this.startY <= y && this.endY >= y;
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(III[I)V")
	public void mapToGameCoords(@OriginalArg(0) int mapX, @OriginalArg(1) int mapY, @OriginalArg(3) int[] position) {
		position[0] = this.plane;
		position[1] = this.startX + mapX - this.displayMinZ;
		position[2] = mapY + this.startY - this.displayMaxZ;
	}

	@OriginalMember(owner = "client!jg", name = "c", descriptor = "(III)Z")
	public boolean displayContains(@OriginalArg(0) int z, @OriginalArg(2) int mapY) {
		return this.displayMinZ <= z && z <= this.displayZ1 && this.displayMaxZ <= mapY && mapY <= this.displayZ2;
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(IZ[II)V")
	public void gameToMapCoords(@OriginalArg(0) int y, @OriginalArg(2) int[] position, @OriginalArg(3) int x) {
		position[0] = 0;
		position[2] = y + this.displayMaxZ - this.startY;
		position[1] = x + this.displayMinZ - this.startX;
	}
}
