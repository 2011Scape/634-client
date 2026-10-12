package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!dfa")
class WorldMapElement : CacheableNode {

	@OriginalMember(owner = "client!dfa", name = "I", descriptor = "I")
	@JvmField
	var color: Int = -1

	@OriginalMember(owner = "client!dfa", name = "N", descriptor = "Z")
	@JvmField
	var shouldRender: Boolean = true

	@OriginalMember(owner = "client!dfa", name = "B", descriptor = "I")
	@JvmField
	var displayZ2: Int = 0

	@OriginalMember(owner = "client!dfa", name = "F", descriptor = "I")
	@JvmField
	var defaultZoom: Int = -1

	@OriginalMember(owner = "client!dfa", name = "S", descriptor = "I")
	@JvmField
	var boundsMinX: Int = 12800

	@OriginalMember(owner = "client!dfa", name = "J", descriptor = "I")
	@JvmField
	var displayMaxX: Int = 0

	@OriginalMember(owner = "client!dfa", name = "T", descriptor = "I")
	@JvmField
	var displayMinZ: Int = 12800

	@OriginalMember(owner = "client!dfa", name = "O", descriptor = "I")
	@JvmField
	val bitpackedPlacement: Int

	@OriginalMember(owner = "client!dfa", name = "z", descriptor = "Ljava/lang/String;")
	@JvmField
	val filenamePrefix: String?

	@OriginalMember(owner = "client!dfa", name = "G", descriptor = "Ljava/lang/String;")
	@JvmField
	val displayName: String?

	@OriginalMember(owner = "client!dfa", name = "C", descriptor = "I")
	@JvmField
	val elementId: Int

	@OriginalMember(owner = "client!dfa", name = "D", descriptor = "Lclient!uh;")
	@JvmField
	val worldMapPlacements: Deque

	@OriginalMember(owner = "client!dfa", name = "<init>", descriptor = "(ILjava/lang/String;Ljava/lang/String;IIZII)V")
	constructor(@OriginalArg(0) elementId: Int, @OriginalArg(1) string: String?, @OriginalArg(2) string2: String?, @OriginalArg(3) bitpackedPlacement: Int, @OriginalArg(4) color: Int, @OriginalArg(5) shouldRender: Boolean, @OriginalArg(6) defaultZoomLevel: Int, @OriginalArg(7) mapsizeId: Int) : super() {
		this.defaultZoom = defaultZoomLevel
		this.bitpackedPlacement = bitpackedPlacement
		this.filenamePrefix = string
		this.displayName = string2
		this.color = color
		this.elementId = elementId
		this.shouldRender = shouldRender
		if (this.defaultZoom == 255) {
			this.defaultZoom = 0
		}
		this.worldMapPlacements = Deque()
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "([IIIIB)Z")
	fun convertSourceToDisplay(@OriginalArg(0) position: IntArray?, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int, @OriginalArg(3) plane: Int): Boolean {
		@Pc(11) var worldMapChunk: WorldMapPlacement? = this.worldMapPlacements.first() as WorldMapPlacement?
		while (worldMapChunk != null) {
			if (worldMapChunk.withinPlaneBounds(z, x, plane)) {
				worldMapChunk.gameToMapCoords(z, position, x)
				return true
			}
			worldMapChunk = this.worldMapPlacements.next() as WorldMapPlacement?
		}
		return false
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(BII)Z")
	fun sourceContains(@OriginalArg(1) x: Int, @OriginalArg(2) z: Int): Boolean {
		@Pc(19) var chunk: WorldMapPlacement? = this.worldMapPlacements.first() as WorldMapPlacement?
		while (chunk != null) {
			if (chunk.withinBounds(x, z)) {
				return true
			}
			chunk = this.worldMapPlacements.next() as WorldMapPlacement?
		}
		return false
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(ZII[I)Z")
	fun mapToGameCoords(@OriginalArg(1) mapY: Int, @OriginalArg(2) mapX: Int, @OriginalArg(3) position: IntArray?): Boolean {
		@Pc(20) var worldMapChunk: WorldMapPlacement? = this.worldMapPlacements.first() as WorldMapPlacement?
		while (worldMapChunk != null) {
			if (worldMapChunk.displayContains(mapX, mapY)) {
				worldMapChunk.mapToGameCoords(mapX, mapY, position)
				return true
			}
			worldMapChunk = this.worldMapPlacements.next() as WorldMapPlacement?
		}
		return false
	}

	@OriginalMember(owner = "client!dfa", name = "f", descriptor = "(B)V")
	fun recalculateBounds() {
		this.boundsMinX = 12800
		this.displayMinZ = 12800
		this.displayZ2 = 0
		this.displayMaxX = 0
		@Pc(32) var worldMapChunk: WorldMapPlacement? = this.worldMapPlacements.first() as WorldMapPlacement?
		while (worldMapChunk != null) {
			if (worldMapChunk.displayMaxZ < this.boundsMinX) {
				this.boundsMinX = worldMapChunk.displayMaxZ
			}
			if (this.displayZ2 < worldMapChunk.displayZ2) {
				this.displayZ2 = worldMapChunk.displayZ2
			}
			if (this.displayMaxX < worldMapChunk.displayZ1) {
				this.displayMaxX = worldMapChunk.displayZ1
			}
			if (this.displayMinZ > worldMapChunk.displayMinZ) {
				this.displayMinZ = worldMapChunk.displayMinZ
			}
			worldMapChunk = this.worldMapPlacements.next() as WorldMapPlacement?
		}
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "([IIII)Z")
	fun convertDisplayToSource(@OriginalArg(0) position: IntArray?, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int): Boolean {
		@Pc(11) var worldMapChunk: WorldMapPlacement? = this.worldMapPlacements.first() as WorldMapPlacement?
		while (worldMapChunk != null) {
			if (worldMapChunk.withinBounds(x, z)) {
				worldMapChunk.gameToMapCoords(z, position, x)
				return true
			}
			worldMapChunk = this.worldMapPlacements.next() as WorldMapPlacement?
		}
		return false
	}

	companion object {
		@OriginalMember(owner = "client!oha", name = "a", descriptor = "(IZLclient!in;I)Lclient!dfa;")
		@JvmStatic
		fun list(@OriginalArg(0) fileId: Int, @OriginalArg(2) resource: Js5ResourceProvider?, @OriginalArg(3) groupId: Int): WorldMapElement {
			@Pc(14) val packet: Packet = Packet(resource!!.getFile(groupId, fileId))
			@Pc(52) val worldMapArea: WorldMapElement = WorldMapElement(fileId, packet.readString(), packet.readString(), packet.readInt(), packet.readInt(), packet.readUByte() == 1, packet.readUByte(), packet.readUByte())
			@Pc(56) val placementCount: Int = packet.readUByte()
			@Pc(58) var i: Int = 0
			while (i < placementCount) {
				worldMapArea.worldMapPlacements.addLast(WorldMapPlacement(packet.readUByte(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort()))
				i++
			}
			worldMapArea.recalculateBounds()
			return worldMapArea
		}
	}
}
