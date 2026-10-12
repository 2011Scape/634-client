package com.jagex.game.runetek5.scene.environment

import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.preference.GamePreferences
import kotlin.jvm.JvmField

@OriginalClass("client!bp")
class Environment {

	@OriginalMember(owner = "client!bp", name = "h", descriptor = "F")
	@JvmField
	var brightpass: Float = 0.25f

	@OriginalMember(owner = "client!bp", name = "k", descriptor = "F")
	@JvmField
	var bloom: Float = 1.0f

	@OriginalMember(owner = "client!bp", name = "q", descriptor = "F")
	@JvmField
	var whitePoint: Float = 1.0f

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "I")
	@JvmField
	val sunPositionZ: Int

	@OriginalMember(owner = "client!bp", name = "b", descriptor = "F")
	@JvmField
	val sunBacklight: Float

	@OriginalMember(owner = "client!bp", name = "g", descriptor = "Lclient!wh;")
	@JvmField
	val sunCubeTexture: EnvironmentSampler?

	@OriginalMember(owner = "client!bp", name = "l", descriptor = "I")
	@JvmField
	val fogDepth: Int

	@OriginalMember(owner = "client!bp", name = "o", descriptor = "I")
	@JvmField
	val fogColour: Int

	@OriginalMember(owner = "client!bp", name = "n", descriptor = "I")
	@JvmField
	val sunPositionY: Int

	@OriginalMember(owner = "client!bp", name = "d", descriptor = "I")
	@JvmField
	val sunPositionX: Int

	@OriginalMember(owner = "client!bp", name = "j", descriptor = "F")
	@JvmField
	val sunLight: Float

	@OriginalMember(owner = "client!bp", name = "f", descriptor = "I")
	@JvmField
	val sunColour: Int

	@OriginalMember(owner = "client!bp", name = "m", descriptor = "F")
	@JvmField
	val sunAmbient: Float

	@OriginalMember(owner = "client!bp", name = "<init>", descriptor = "()V")
	constructor() {
		this.sunPositionZ = -50
		this.sunBacklight = 1.2f
		this.sunCubeTexture = EnvironmentManager.aClass78_4
		this.fogDepth = 0
		this.fogColour = EnvironmentManager.defaultFogColour
		this.sunPositionY = -60
		this.sunPositionX = -50
		this.sunLight = 0.69921875f
		this.sunColour = EnvironmentManager.defaultSunColour
		this.sunAmbient = 1.1523438f
	}

	@OriginalMember(owner = "client!bp", name = "<init>", descriptor = "(Lclient!cea;)V")
	constructor(@OriginalArg(0) packet: Packet) {
		@Pc(16) val flags: Int = packet.readUByte()
		if (GamePreferences.uid!!.lightDetail!!.getValue() == 1 && EnvironmentManager.toolkit!!.getMaxLights() > 0) {
			if ((flags and 0x1) == 0) {
				this.sunColour = EnvironmentManager.defaultSunColour
			} else {
				this.sunColour = packet.readInt()
			}
			if ((flags and 0x2) == 0) {
				this.sunAmbient = 1.1523438f
			} else {
				this.sunAmbient = packet.readUShort().toFloat() / 256.0f
			}
			if ((flags and 0x4) == 0) {
				this.sunLight = 0.69921875f
			} else {
				this.sunLight = packet.readUShort().toFloat() / 256.0f
			}
			if ((flags and 0x8) == 0) {
				this.sunBacklight = 1.2f
			} else {
				this.sunBacklight = packet.readUShort().toFloat() / 256.0f
			}
		} else {
			if ((flags and 0x1) != 0) {
				packet.readInt()
			}
			if ((flags and 0x2) != 0) {
				packet.readUShort()
			}
			if ((flags and 0x4) != 0) {
				packet.readUShort()
			}
			if ((flags and 0x8) != 0) {
				packet.readUShort()
			}
			this.sunColour = EnvironmentManager.defaultSunColour
			this.sunAmbient = 1.1523438f
			this.sunLight = 0.69921875f
			this.sunBacklight = 1.2f
		}
		if ((flags and 0x10) == 0) {
			this.sunPositionY = -60
			this.sunPositionZ = -50
			this.sunPositionX = -50
		} else {
			this.sunPositionX = packet.readShort()
			this.sunPositionY = packet.readShort()
			this.sunPositionZ = packet.readShort()
		}
		if ((flags and 0x20) == 0) {
			this.fogColour = EnvironmentManager.defaultFogColour
		} else {
			this.fogColour = packet.readInt()
		}
		if ((flags and 0x40) == 0) {
			this.fogDepth = 0
		} else {
			this.fogDepth = packet.readUShort()
		}
		if ((flags and 0x80) == 0) {
			this.sunCubeTexture = EnvironmentManager.aClass78_4
		} else {
			@Pc(231) val materialAtPositiveX: Int = packet.readUShort()
			@Pc(235) val materialAtNegativeX: Int = packet.readUShort()
			@Pc(239) val materialAtPositiveY: Int = packet.readUShort()
			@Pc(243) val materialAtNegativeY: Int = packet.readUShort()
			@Pc(247) val materialAtPositiveZ: Int = packet.readUShort()
			@Pc(251) val materialAtNegativeZ: Int = packet.readUShort()
			this.sunCubeTexture = EnvironmentManager.getCubeTexture(materialAtPositiveZ, materialAtNegativeY, materialAtPositiveX, materialAtNegativeZ, materialAtNegativeX, materialAtPositiveY)
		}
	}

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(ILclient!cea;)V")
	fun decodeHdr(@OriginalArg(1) packet: Packet) {
		this.bloom = (packet.readUByte() * 8).toFloat() / 255.0f
		this.brightpass = (packet.readUByte() * 8).toFloat() / 255.0f
		this.whitePoint = (packet.readUByte() * 8).toFloat() / 255.0f
	}

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(Lclient!bp;B)Z")
	fun isEqual(@OriginalArg(0) environment: Environment?): Boolean {
		return environment!!.sunColour == this.sunColour && this.sunAmbient == environment.sunAmbient && environment.sunLight == this.sunLight && this.sunBacklight == environment.sunBacklight && environment.brightpass == this.brightpass && environment.bloom == this.bloom && environment.whitePoint == this.whitePoint && this.fogColour == environment.fogColour && this.fogDepth == environment.fogDepth && environment.sunCubeTexture === this.sunCubeTexture
	}
}
