package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object TextureTypeList {
	@OriginalMember(owner = "client!ufa", name = "c", descriptor = "Lclient!in;")
	@JvmField
	var modelResource: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!kv", name = "n", descriptor = "D")
	@JvmField
	var currentBrightness: Double = -1.0

	@OriginalMember(owner = "client!eh", name = "b", descriptor = "Lclient!in;")
	@JvmField
	var textureSpriteResource: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!kda", name = "fc", descriptor = "[I")
	@JvmField
	val BRIGHTNESS_TABLE: IntArray = IntArray(256)

	@OriginalMember(owner = "client!gn", name = "e", descriptor = "Lclient!d;")
	@JvmField
	var textureSource: MaterialProvider? = null

	@OriginalMember(owner = "client!sa", name = "a", descriptor = "(ILclient!cea;)Lclient!tf;")
	@JvmStatic
	fun decodeTextureOperation(@OriginalArg(1) packet: Packet?): TextureOperation {
		packet!!.readUByte()
		@Pc(13) val implementationType: Int = packet.readUByte()
		@Pc(17) val textureOp: TextureOperation? = TextureOperation.implementationForType(implementationType)
		textureOp!!.imageCacheCapacity = packet.readUByte()
		@Pc(26) val opcodeCount: Int = packet.readUByte()
		@Pc(33) var i: Int = 0
		while (i < opcodeCount) {
			@Pc(39) val opcode: Int = packet.readUByte()
			textureOp.decode(packet, opcode)
			i++
		}
		textureOp.postDecode()
		return textureOp
	}
}
