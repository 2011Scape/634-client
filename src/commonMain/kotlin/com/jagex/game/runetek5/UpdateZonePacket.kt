package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.core.constants.AreaMode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!kn")
class UpdateZonePacket @OriginalMember(owner = "client!kn", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) id: Int, @OriginalArg(1) size: Int) {

	@OriginalMember(owner = "client!kn", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!fi", name = "P", descriptor = "Lclient!kn;")
		@JvmField
		val SPOT_ANIM: UpdateZonePacket = UpdateZonePacket(12, 7)

		@OriginalMember(owner = "client!ifa", name = "b", descriptor = "Lclient!kn;")
		@JvmField
		val GROUND_ITEM_COUNT: UpdateZonePacket = UpdateZonePacket(7, 7)
		@OriginalMember(owner = "client!uk", name = "c", descriptor = "Lclient!kn;")
		@JvmField
		val OBJ_ANIM: UpdateZonePacket = UpdateZonePacket(2, 4)

		@OriginalMember(owner = "client!de", name = "i", descriptor = "Lclient!kn;")
		@JvmField
		val GROUND_ITEM_REVEAL: UpdateZonePacket = UpdateZonePacket(4, 7)
		@OriginalMember(owner = "client!li", name = "a", descriptor = "Lclient!kn;")
		@JvmField
		val SOUND_AREA: UpdateZonePacket = UpdateZonePacket(10, 8)

		@OriginalMember(owner = "client!bea", name = "A", descriptor = "Lclient!kn;")
		@JvmField
		val MIDI_SONG_LOCATION: UpdateZonePacket = UpdateZonePacket(11, 8)

		@OriginalMember(owner = "client!gca", name = "b", descriptor = "Lclient!kn;")
		@JvmField
		val DESTROY_OBJECT: UpdateZonePacket = UpdateZonePacket(3, 2)
		@OriginalMember(owner = "client!pd", name = "f", descriptor = "Lclient!kn;")
		@JvmField
		val MAP_PROJANIM: UpdateZonePacket = UpdateZonePacket(5, 16)

		@OriginalMember(owner = "client!ri", name = "q", descriptor = "Lclient!kn;")
		@JvmField
		val OBJECT_PREFETCH: UpdateZonePacket = UpdateZonePacket(8, 3)

		@OriginalMember(owner = "client!ub", name = "a", descriptor = "(B)[Lclient!kn;")
		@JvmStatic
		fun updateZonePacketValues(): Array<UpdateZonePacket?> {
			return arrayOf(UpdateZonePacket.REMOVE_GROUND_ITEM, UpdateZonePacket.CREATE_OBJECT, UpdateZonePacket.OBJ_ANIM, UpdateZonePacket.DESTROY_OBJECT, UpdateZonePacket.GROUND_ITEM_REVEAL, UpdateZonePacket.MAP_PROJANIM, UpdateZonePacket.CREATE_GROUND_ITEM, UpdateZonePacket.GROUND_ITEM_COUNT, UpdateZonePacket.OBJECT_PREFETCH, UpdateZonePacket.MAP_PROJANIM_HALFSQ, UpdateZonePacket.SOUND_AREA, UpdateZonePacket.MIDI_SONG_LOCATION, UpdateZonePacket.SPOT_ANIM, UpdateZonePacket.TILE_MESSAGE)
		}

		@OriginalMember(owner = "client!sv", name = "R", descriptor = "Lclient!kn;")
		@JvmField
		val CREATE_GROUND_ITEM: UpdateZonePacket = UpdateZonePacket(6, 5)

		@OriginalMember(owner = "client!vda", name = "f", descriptor = "I")
		@JvmField
		var updateZoneX: Int = 0

		/**
		 * Not part of the original client: added to the zone offsets a packet carries. The server sends
		 * them from the base of its own scene (MapRegion.hdServerMapSize); the client's may be bigger
		 * (StaticRegionInit.hdApplySceneSize), with its base further south-west.
		 */
		@JvmStatic
		fun hdServerZoneShift(): Int {
			if (MapRegion.areaMode != AreaMode.STATIC_AREA) {
				return 0
			}
			return ClientFeatures.serverZoneShift(MapRegion.sceneSizeX, MapRegion.MAP_SIZES[MapRegion.hdServerMapSize])
		}

		@OriginalMember(owner = "client!an", name = "w", descriptor = "Lclient!kn;")
		@JvmField
		val MAP_PROJANIM_HALFSQ: UpdateZonePacket = UpdateZonePacket(9, 19)

		@OriginalMember(owner = "client!pf", name = "Q", descriptor = "Lclient!kn;")
		@JvmField
		val REMOVE_GROUND_ITEM: UpdateZonePacket = UpdateZonePacket(0, 3)
		@OriginalMember(owner = "client!qg", name = "b", descriptor = "Lclient!kn;")
		@JvmField
		val TILE_MESSAGE: UpdateZonePacket = UpdateZonePacket(13, -1)

		@OriginalMember(owner = "client!mj", name = "u", descriptor = "Lclient!kn;")
		@JvmField
		val CREATE_OBJECT: UpdateZonePacket = UpdateZonePacket(1, 4)
		@OriginalMember(owner = "client!kk", name = "o", descriptor = "I")
		@JvmField
		var updateZoneY: Int = 0
	}
}
