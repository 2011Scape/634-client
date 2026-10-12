package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!jg")
class WorldMapPlacement : Node {

	@OriginalMember(owner = "client!jg", name = "t", descriptor = "I")
	@JvmField
	val displayMinZ: Int

	@OriginalMember(owner = "client!jg", name = "D", descriptor = "I")
	@JvmField
	val displayZ1: Int

	@OriginalMember(owner = "client!jg", name = "E", descriptor = "I")
	private val startX: Int

	@OriginalMember(owner = "client!jg", name = "v", descriptor = "I")
	private val startY: Int

	@OriginalMember(owner = "client!jg", name = "z", descriptor = "I")
	private val plane: Int

	@OriginalMember(owner = "client!jg", name = "w", descriptor = "I")
	@JvmField
	val displayMaxZ: Int

	@OriginalMember(owner = "client!jg", name = "q", descriptor = "I")
	private val endX: Int

	@OriginalMember(owner = "client!jg", name = "u", descriptor = "I")
	private val endY: Int

	@OriginalMember(owner = "client!jg", name = "x", descriptor = "I")
	@JvmField
	val displayZ2: Int

	@OriginalMember(owner = "client!jg", name = "<init>", descriptor = "(IIIIIIIII)V")
	constructor(@OriginalArg(0) plane: Int, @OriginalArg(1) startX: Int, @OriginalArg(2) startY: Int, @OriginalArg(3) endX: Int, @OriginalArg(4) endY: Int, @OriginalArg(5) mapMinX: Int, @OriginalArg(6) mapMinY: Int, @OriginalArg(7) mapMaxX: Int, @OriginalArg(8) mapMaxY: Int) : super() {
		this.displayMinZ = mapMinX
		this.displayZ1 = mapMaxX
		this.startX = startX
		this.startY = startY
		this.plane = plane
		this.displayMaxZ = mapMinY
		this.endX = endX
		this.endY = endY
		this.displayZ2 = mapMaxY
	}

	@OriginalMember(owner = "client!jg", name = "b", descriptor = "(III)Z")
	fun withinBounds(@OriginalArg(1) x: Int, @OriginalArg(2) y: Int): Boolean {
		return this.startX <= x && this.endX >= x && y >= this.startY && this.endY >= y
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(IIIZ)Z")
	fun withinPlaneBounds(@OriginalArg(0) y: Int, @OriginalArg(1) x: Int, @OriginalArg(2) plane: Int): Boolean {
		return plane == this.plane && this.startX <= x && this.endX >= x && this.startY <= y && this.endY >= y
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(III[I)V")
	fun mapToGameCoords(@OriginalArg(0) mapX: Int, @OriginalArg(1) mapY: Int, @OriginalArg(3) position: IntArray?) {
		position!![0] = this.plane
		position[1] = this.startX + mapX - this.displayMinZ
		position[2] = mapY + this.startY - this.displayMaxZ
	}

	@OriginalMember(owner = "client!jg", name = "c", descriptor = "(III)Z")
	fun displayContains(@OriginalArg(0) z: Int, @OriginalArg(2) mapY: Int): Boolean {
		return this.displayMinZ <= z && z <= this.displayZ1 && this.displayMaxZ <= mapY && mapY <= this.displayZ2
	}

	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(IZ[II)V")
	fun gameToMapCoords(@OriginalArg(0) y: Int, @OriginalArg(2) position: IntArray?, @OriginalArg(3) x: Int) {
		position!![0] = 0
		position[2] = y + this.displayMaxZ - this.startY
		position[1] = x + this.displayMinZ - this.startX
	}
}
