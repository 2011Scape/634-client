package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ab")
abstract class PointLight : Node {

	@OriginalMember(owner = "client!ab", name = "r", descriptor = "I")
	@JvmField
	var y: Int

	@OriginalMember(owner = "client!ab", name = "w", descriptor = "I")
	@JvmField
	var z: Int

	@OriginalMember(owner = "client!ab", name = "x", descriptor = "I")
	private val radius: Int

	@OriginalMember(owner = "client!ab", name = "t", descriptor = "F")
	@JvmField
	var intensity: Float

	@OriginalMember(owner = "client!ab", name = "o", descriptor = "I")
	private val colour: Int

	@OriginalMember(owner = "client!ab", name = "F", descriptor = "I")
	@JvmField
	var x: Int

	@OriginalMember(owner = "client!ab", name = "<init>", descriptor = "(IIIIIF)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) intensity: Float) {
		this.y = y
		this.z = z
		this.radius = radius
		this.intensity = intensity
		this.colour = colour
		this.x = x
	}

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(IF)V")
	abstract fun setIntensity(@OriginalArg(1) intensity: Float)

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(B)I")
	fun getZ(): Int {
		return this.z
	}

	@OriginalMember(owner = "client!ab", name = "d", descriptor = "(I)I")
	fun getColour(): Int {
		return this.colour
	}

	@OriginalMember(owner = "client!ab", name = "e", descriptor = "(I)F")
	fun getIntensity(): Float {
		return this.intensity
	}

	@OriginalMember(owner = "client!ab", name = "f", descriptor = "(I)I")
	fun getY(): Int {
		return this.y
	}

	@OriginalMember(owner = "client!ab", name = "g", descriptor = "(I)I")
	fun getRadius(): Int {
		return this.radius
	}

	@OriginalMember(owner = "client!ab", name = "h", descriptor = "(I)I")
	fun getX(): Int {
		return this.x
	}

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(IBII)V")
	abstract fun setPosition(@OriginalArg(0) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) y: Int)
}
