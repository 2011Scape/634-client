package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!dga")
class SsePointLight : PointLight {

	@OriginalMember(owner = "client!dga", name = "<init>", descriptor = "(IIIIIF)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) intensity: Float) : super(x, y, z, radius, colour, intensity)

	@OriginalMember(owner = "client!dga", name = "a", descriptor = "(IBII)V")
	override fun setPosition(@OriginalArg(0) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) y: Int) {
		this.z = z
		this.y = y
		this.x = x
	}

	@OriginalMember(owner = "client!dga", name = "a", descriptor = "(IF)V")
	override fun setIntensity(@OriginalArg(1) intensity: Float) {
		this.intensity = intensity
	}
}
