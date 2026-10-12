package com.jagex.game.runetek5.config.defaults

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.graphics.gl.HitsplatSlotXOffsets
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState

object EntityDefaults {
	@OriginalMember(owner = "client!vi", name = "c", descriptor = "I")
	@JvmField
	var loginInterfaceId: Int = 0

	@OriginalMember(owner = "client!pi", name = "y", descriptor = "I")
	@JvmField
	var profilingMesh: Int = -1

	@OriginalMember(owner = "client!jea", name = "e", descriptor = "I")
	@JvmField
	var lobbyWindow: Int = 0

	@OriginalMember(owner = "client!kg", name = "a", descriptor = "([BB)V")
	@JvmStatic
	fun decode(@OriginalArg(0) src: ByteArray?) {
		@Pc(10) val packet: Packet = Packet(src)
		@Pc(20) var offsetsDecoded: Boolean = false
		while (true) {
			while (true) {
				@Pc(24) var local24: Int = packet.readUByte()
				if (local24 == 0) {
					if (offsetsDecoded) {
						return
					}
					if (HitsplatSlotXOffsets.hitsplatOffsetsX == null) {
						HitsplatSlotXOffsets.hitsplatOffsetsX = IntArray(4)
						EntityDefaults.hitsplatOffsetsY = IntArray(4)
						PathingEntity.maximumHits = 4
					}
					local24 = 0
					while (local24 < HitsplatSlotXOffsets.hitsplatOffsetsX!!.size) {
						HitsplatSlotXOffsets.hitsplatOffsetsX!![local24] = 0
						EntityDefaults.hitsplatOffsetsY!![local24] = local24 * 20
						local24++
					}
					return
				}
				if (local24 == 1) {
					if (HitsplatSlotXOffsets.hitsplatOffsetsX == null) {
						HitsplatSlotXOffsets.hitsplatOffsetsX = IntArray(4)
						PathingEntity.maximumHits = 4
						EntityDefaults.hitsplatOffsetsY = IntArray(4)
					}
					@Pc(44) var i: Int = 0
					while (i < HitsplatSlotXOffsets.hitsplatOffsetsX!!.size) {
						HitsplatSlotXOffsets.hitsplatOffsetsX!![i] = packet.readShort()
						EntityDefaults.hitsplatOffsetsY!![i] = packet.readShort()
						i++
					}
					offsetsDecoded = true
				} else if (local24 == 2) {
					EntityDefaults.profilingMesh = packet.readUShort()
				} else if (local24 == 3) {
					PathingEntity.maximumHits = packet.readUByte()
					HitsplatSlotXOffsets.hitsplatOffsetsX = IntArray(PathingEntity.maximumHits)
					EntityDefaults.hitsplatOffsetsY = IntArray(PathingEntity.maximumHits)
				}
			}
		}
	}

	@OriginalMember(owner = "client!mia", name = "f", descriptor = "[I")
	@JvmField
	var hitsplatOffsetsY: IntArray? = null
}
