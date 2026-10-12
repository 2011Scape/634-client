package com.jagex.game.runetek5.network

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.network.world.WorldDescriptor
import com.jagex.game.runetek5.WorldConnectionInfo
import com.jagex.game.runetek5.WorldListSorter
import com.jagex.game.runetek5.client.menu.MiniMenu
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.client.PingWorker
import com.jagex.game.runetek5.network.world.WorldMeta
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.utils.Utils

object WorldConnection {
	@OriginalMember(owner = "client!np", name = "f", descriptor = "Z")
	@JvmField
	var requestingWorldList: Boolean = false

	@OriginalMember(owner = "client!ta", name = "l", descriptor = "J")
	@JvmField
	var worldListReceivedTime: Long = 0L

	@OriginalMember(owner = "client!kb", name = "i", descriptor = "[Lclient!mf;")
	@JvmField
	var WORLD_DESCRIPTORS_BYID: Array<WorldDescriptor?> = arrayOfNulls<WorldDescriptor>(0)
	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(Z)Lclient!mf;")
	@JvmStatic
	fun getNextWorldDescriptor(): WorldDescriptor? {
		return if (WorldConnection.nextWorldDescriptorIndex < WorldConnection.WORLD_DESCRIPTORS_BYID.size) WorldConnection.WORLD_DESCRIPTORS_BYID[WorldConnection.nextWorldDescriptorIndex++] else null
	}

	@OriginalMember(owner = "client!rq", name = "w", descriptor = "Lclient!ss;")
	@JvmField
	var aClass116_2: Connection? = null
	@OriginalMember(owner = "client!kaa", name = "q", descriptor = "Lclient!pc;")
	@JvmField
	var aClass255_3: WorldConnectionInfo? = null
	@OriginalMember(owner = "client!dda", name = "M", descriptor = "Lclient!pc;")
	@JvmField
	var aClass255_1: WorldConnectionInfo? = null
	@OriginalMember(owner = "client!bca", name = "h", descriptor = "(I)V")
	@JvmStatic
	fun pingWorlds() {
		if (!MiniMenu.debugOps) {
			return
		}
		while (true) {
			while (WorldConnection.WORLD_DESCRIPTORS_BYID.size > WorldListSorter.pingWorldIndex) {
				@Pc(25) val gameWorld: WorldDescriptor? = WorldConnection.WORLD_DESCRIPTORS_BYID[WorldListSorter.pingWorldIndex]
				if (gameWorld != null && gameWorld.ping == -1) {
					if (WorldListSorter.aClass2_Sub36_2 == null) {
						WorldListSorter.aClass2_Sub36_2 = PingWorker.aClass262_1!!.requestPing(gameWorld.activity)
					}
					@Pc(50) val ping: Int = WorldListSorter.aClass2_Sub36_2!!.ping
					if (ping == -1) {
						return
					}
					gameWorld.ping = ping
					WorldListSorter.pingWorldIndex++
					WorldListSorter.aClass2_Sub36_2 = null
				} else {
					WorldListSorter.pingWorldIndex++
				}
			}
			return
		}
	}
	@OriginalMember(owner = "client!lp", name = "i", descriptor = "Lclient!pc;")
	@JvmField
	var aClass255_4: WorldConnectionInfo? = null

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "(Z[BB)V")
	@JvmStatic
	fun decodeWorldList(@OriginalArg(0) refresh: Boolean, @OriginalArg(1) buffer: ByteArray?) {
		if (WorldConnection.aClass2_Sub11_5 == null) {
			WorldConnection.aClass2_Sub11_5 = Packet(20000)
		}
		WorldConnection.aClass2_Sub11_5!!.writeBytes(buffer!!.size, 0, buffer)
		if (!refresh) {
			return
		}
		WorldConnection.decodeWorldListResponse(WorldConnection.aClass2_Sub11_5!!.buffer)
		WorldConnection.WORLD_DESCRIPTORS_BYID = arrayOfNulls<WorldDescriptor>(OrthoMode.worldListSize)
		@Pc(31) var count: Int = 0
		@Pc(33) var i: Int = WorldDescriptor.WORLD_LIST_START
		while (i <= WorldMeta.worldListEnd) {
			@Pc(39) val gameWorld: WorldDescriptor? = WorldMeta.getWorldDescriptor(i)
			if (gameWorld != null) {
				WorldConnection.WORLD_DESCRIPTORS_BYID[count++] = gameWorld
			}
			i++
		}
		WorldConnection.requestingWorldList = false
		WorldConnection.worldListReceivedTime = Utils.currentTimeMillis()
		WorldConnection.aClass2_Sub11_5 = null
	}
	@OriginalMember(owner = "client!qda", name = "h", descriptor = "Lclient!cea;")
	@JvmField
	var aClass2_Sub11_5: Packet? = null

	@OriginalMember(owner = "client!rk", name = "r", descriptor = "I")
	@JvmField
	var nextWorldDescriptorIndex: Int = 999999

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(B[B)Z")
	@JvmStatic
	fun decodeWorldListResponse(@OriginalArg(1) src: ByteArray?): Boolean {
		@Pc(8) val packet: Packet = Packet(src)
		@Pc(19) val version: Int = packet.readUByte()
		if (version != 2) {
			return false
		}
		@Pc(35) val hasWorldList: Boolean = packet.readUByte() == 1
		if (hasWorldList) {
			WorldMeta.decodeWorldList(packet)
		}
		WorldMeta.decodeWorldPopulations(packet)
		return true
	}
}
