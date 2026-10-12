package com.jagex.game.runetek5.network.world

import com.jagex.game.runetek5.Country
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object WorldMeta {
	@OriginalMember(owner = "client!sc", name = "f", descriptor = "[Lclient!mf;")
	@JvmField
	var WORLD_LIST_DESCRIPTORS: Array<WorldDescriptor?>? = null

	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(BLclient!cea;)V")
	@JvmStatic
	fun decodeWorldList(@OriginalArg(1) packet: Packet?) {
		@Pc(9) val countryCount: Int = packet!!.readUSmart()
		Country.aClass359Array1 = arrayOfNulls<Country>(countryCount)
		@Pc(14) var i: Int = 0
		while (i < countryCount) {
			Country.aClass359Array1[i] = Country()
			Country.aClass359Array1[i].flag = packet.readUSmart()
			Country.aClass359Array1[i].name = packet.readJagString()
			i++
		}
		WorldDescriptor.WORLD_LIST_START = packet.readUSmart()
		WorldMeta.worldListEnd = packet.readUSmart()
		OrthoMode.worldListSize = packet.readUSmart()
		WorldMeta.WORLD_LIST_DESCRIPTORS = arrayOfNulls<WorldDescriptor>(WorldMeta.worldListEnd + 1 - WorldDescriptor.WORLD_LIST_START)
		@Pc(66) var j: Int = 0
		while (j < OrthoMode.worldListSize) {
			@Pc(72) val id: Int = packet.readUSmart()
			@Pc(80) val gameWorld: WorldDescriptor = WorldDescriptor()
			WorldMeta.WORLD_LIST_DESCRIPTORS!![id] = gameWorld
			gameWorld.country = packet.readUByte()
			gameWorld.flags = packet.readInt()
			gameWorld.worldNumber = WorldDescriptor.WORLD_LIST_START + id
			gameWorld.address = packet.readJagString()
			gameWorld.activity = packet.readJagString()
			j++
		}
		WorldDescriptor.port = packet.readInt()
		WorldMeta.worldsLoaded = true
	}

	@OriginalMember(owner = "client!qt", name = "k", descriptor = "Z")
	@JvmField
	var worldsLoaded: Boolean = false

	@OriginalMember(owner = "client!kfa", name = "b", descriptor = "(ZI)Lclient!mf;")
	@JvmStatic
	fun getWorldDescriptor(@OriginalArg(1) worldId: Int): WorldDescriptor? {
		return if (WorldMeta.worldsLoaded && WorldDescriptor.WORLD_LIST_START <= worldId && worldId <= WorldMeta.worldListEnd) WorldMeta.WORLD_LIST_DESCRIPTORS!![worldId - WorldDescriptor.WORLD_LIST_START] else null
	}

	@OriginalMember(owner = "client!nk", name = "a", descriptor = "(BLclient!cea;)V")
	@JvmStatic
	fun decodeWorldPopulations(@OriginalArg(1) packet: Packet?) {
		@Pc(7) var i: Int = 0
		while (i < OrthoMode.worldListSize) {
			@Pc(13) val worldId: Int = packet!!.readUSmart()
			@Pc(17) var population: Int = packet.readUShort()
			if (population == 65535) {
				population = -1
			}
			if (WorldMeta.WORLD_LIST_DESCRIPTORS!![worldId] != null) {
				WorldMeta.WORLD_LIST_DESCRIPTORS!![worldId]!!.population = population
			}
			i++
		}
	}

	@OriginalMember(owner = "client!fv", name = "v", descriptor = "I")
	@JvmField
	var worldListEnd: Int = 0
}
