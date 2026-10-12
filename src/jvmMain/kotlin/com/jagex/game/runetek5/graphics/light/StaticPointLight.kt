package com.jagex.game.runetek5.graphics.light

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise

@OriginalClass("client!ro")
class StaticPointLight {

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "I")
	@JvmField
	var level: Int = 0

	@OriginalMember(owner = "client!ro", name = "d", descriptor = "I")
	@JvmField
	var type: Int = 0

	@OriginalMember(owner = "client!ro", name = "e", descriptor = "I")
	private var duration: Int = 0

	@OriginalMember(owner = "client!ro", name = "g", descriptor = "Z")
	@JvmField
	var growsUpwards: Boolean = false

	@OriginalMember(owner = "client!ro", name = "h", descriptor = "I")
	private var surrounding: Int = 0

	@OriginalMember(owner = "client!ro", name = "i", descriptor = "I")
	private var rotationOffset: Int = 0

	@OriginalMember(owner = "client!ro", name = "j", descriptor = "I")
	private var effect: Int = 0

	@OriginalMember(owner = "client!ro", name = "l", descriptor = "[S")
	@JvmField
	var ranges: ShortArray? = null

	@OriginalMember(owner = "client!ro", name = "m", descriptor = "I")
	private var ticker: Int = 0

	@OriginalMember(owner = "client!ro", name = "o", descriptor = "Lclient!ab;")
	@JvmField
	var pointLight: PointLight? = null

	@OriginalMember(owner = "client!ro", name = "s", descriptor = "Z")
	@JvmField
	var growsDownwards: Boolean = false

	@OriginalMember(owner = "client!ro", name = "<init>", descriptor = "()V")
	private constructor() {
		if (StaticPointLight.flickerNoise == null) {
			StaticPointLight.initFlickerNoise()
		}
		this.updateConstants()
	}

	@OriginalMember(owner = "client!ro", name = "<init>", descriptor = "(Lclient!ha;Lclient!cea;I)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) packet: Packet?, @OriginalArg(2) sizeShift: Int) {
		if (StaticPointLight.flickerNoise == null) {
			StaticPointLight.initFlickerNoise()
		}
		this.level = packet!!.readUByte()
		this.growsUpwards = (this.level and 0x8) != 0
		this.growsDownwards = (this.level and 0x10) != 0
		this.level = this.level and 0x7
		@Pc(47) val x: Int = packet.readUShort() shl sizeShift
		@Pc(53) val z: Int = packet.readUShort() shl sizeShift
		@Pc(59) val y: Int = packet.readUShort() shl sizeShift
		@Pc(63) var radius: Int = packet.readUByte()
		@Pc(69) val rangeCount: Int = radius * 2 + 1
		this.ranges = ShortArray(rangeCount)
		@Pc(86) var local86: Int
		@Pc(75) var i: Int = 0
		while (i < this.ranges!!.size) {
			@Pc(82) val packedRange: Short = packet.readUShort().toShort()
			var rangeStart: Int = packedRange.toInt() ushr 8
			if (rangeStart >= rangeCount) {
				rangeStart = rangeCount - 1
			}
			@Pc(101) var rangeLength: Int = packedRange.toInt() and 0xFF
			if (rangeCount - rangeStart < rangeLength) {
				rangeLength = rangeCount - rangeStart
			}
			this.ranges!![i] = (rangeLength or (rangeStart shl 8)).toShort()
			i++
		}
		radius = (radius shl Scene.tileUnits) + Scene.halfTileOffset
		@Pc(155) val colour: Int = if (ColorUtil.hslToRgb == null) ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(packet.readUShort()).toInt() and 0xFFFF] else ColorUtil.hslToRgb!![packet.readUShort()]
		val packedType: Int = packet.readUByte()
		this.rotationOffset = (packedType and 0xE0) shl 3
		this.type = packedType and 0x1F
		if (this.type != 31) {
			this.updateConstants()
		}
		this.generateLight(y, toolkit, z, x, colour, radius)
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IBIII)V")
	fun setType(@OriginalArg(0) surrounding: Int, @OriginalArg(2) duration: Int, @OriginalArg(3) effect: Int, @OriginalArg(4) ticker: Int) {
		this.duration = duration
		this.surrounding = surrounding
		this.ticker = ticker
		this.effect = effect
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(ILclient!ha;IIIII)V")
	private fun generateLight(@OriginalArg(0) y: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int, @OriginalArg(5) colour: Int, @OriginalArg(6) radius: Int) {
		this.pointLight = toolkit!!.createPointLight(x, y, z, radius, colour, 1.0f)
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(I)V")
	private fun updateConstants() {
		@Pc(4) val type: Int = this.type
		if (type == 2) {
			this.ticker = 2048
			this.surrounding = 0
			this.duration = 2048
			this.effect = 1
		} else if (type == 3) {
			this.surrounding = 0
			this.ticker = 2048
			this.effect = 1
			this.duration = 4096
		} else if (type == 4) {
			this.surrounding = 0
			this.effect = 4
			this.ticker = 2048
			this.duration = 2048
		} else if (type == 5) {
			this.duration = 8192
			this.effect = 4
			this.ticker = 2048
			this.surrounding = 0
		} else if (type == 12) {
			this.duration = 2048
			this.effect = 2
			this.surrounding = 0
			this.ticker = 2048
		} else if (type == 13) {
			this.ticker = 2048
			this.surrounding = 0
			this.effect = 2
			this.duration = 8192
		} else if (type == 10) {
			this.ticker = 512
			this.effect = 3
			this.surrounding = 1536
			this.duration = 2048
		} else if (type == 11) {
			this.effect = 3
			this.ticker = 512
			this.surrounding = 1536
			this.duration = 4096
		} else if (type == 6) {
			this.surrounding = 1280
			this.effect = 3
			this.ticker = 768
			this.duration = 2048
		} else if (type == 7) {
			this.surrounding = 1280
			this.effect = 3
			this.duration = 4096
			this.ticker = 768
		} else if (type == 8) {
			this.surrounding = 1024
			this.duration = 2048
			this.ticker = 1024
			this.effect = 3
		} else if (type == 9) {
			this.ticker = 1024
			this.duration = 4096
			this.effect = 3
			this.surrounding = 1024
		} else if (type == 14) {
			this.effect = 1
			this.surrounding = 1280
			this.duration = 2048
			this.ticker = 768
		} else if (type == 15) {
			this.duration = 4096
			this.ticker = 512
			this.effect = 1
			this.surrounding = 1536
		} else if (type == 16) {
			this.duration = 8192
			this.effect = 1
			this.ticker = 256
			this.surrounding = 1792
		} else {
			this.duration = 2048
			this.ticker = 2048
			this.surrounding = 0
			this.effect = 0
		}
	}

	@OriginalMember(owner = "client!ro", name = "a", descriptor = "(IZI)V")
	fun applyEffect(@OriginalArg(1) disableFlickering: Boolean, @OriginalArg(2) cycle: Int) {
		@Pc(17) val flicker: Int
		if (disableFlickering) {
			flicker = 2048
		} else {
			@Pc(31) val phase: Int = (this.duration * cycle / 50 + this.rotationOffset) and 0x7FF
			@Pc(34) val effectType: Int = this.effect
			if (effectType == 1) {
				flicker = (TextureCombineSource.SINE[phase shl 3] shr 4) + 1024
			} else if (effectType == 3) {
				flicker = StaticPointLight.flickerNoise!![phase] shr 1
			} else if (effectType == 4) {
				flicker = (phase shr 10) shl 11
			} else if (effectType == 2) {
				flicker = phase
			} else if (effectType == 5) {
				flicker = (if (phase >= 1024) 2048 - phase else phase) shl 1
			} else {
				flicker = 2048
			}
		}
		this.pointLight!!.setIntensity((((this.ticker * flicker) shr 11) + this.surrounding).toFloat() / 2048.0f)
	}

	companion object {
		@OriginalMember(owner = "client!vw", name = "h", descriptor = "[I")
		@JvmField
		var flickerNoise: IntArray? = null
		@OriginalMember(owner = "client!oda", name = "c", descriptor = "(B)V")
		@JvmStatic
		fun initFlickerNoise() {
			StaticPointLight.flickerNoise = TextureOpPerlinNoise.generateNoiseRow(0.4f)
		}
	}
}
