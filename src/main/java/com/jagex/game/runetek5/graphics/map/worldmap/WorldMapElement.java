package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

@OriginalClass("client!dfa")
public final class WorldMapElement extends CacheableNode {

	@OriginalMember(owner = "client!dfa", name = "I", descriptor = "I")
	public int color = -1;

	@OriginalMember(owner = "client!dfa", name = "N", descriptor = "Z")
	public boolean shouldRender = true;

	@OriginalMember(owner = "client!dfa", name = "B", descriptor = "I")
	public int displayZ2 = 0;

	@OriginalMember(owner = "client!dfa", name = "F", descriptor = "I")
	public int defaultZoom = -1;

	@OriginalMember(owner = "client!dfa", name = "S", descriptor = "I")
	public int boundsMinX = 12800;

	@OriginalMember(owner = "client!dfa", name = "J", descriptor = "I")
	public int displayMaxX = 0;

	@OriginalMember(owner = "client!dfa", name = "T", descriptor = "I")
	public int displayMinZ = 12800;

	@OriginalMember(owner = "client!dfa", name = "O", descriptor = "I")
	public final int bitpackedPlacement;

	@OriginalMember(owner = "client!dfa", name = "z", descriptor = "Ljava/lang/String;")
	public final String filenamePrefix;

	@OriginalMember(owner = "client!dfa", name = "G", descriptor = "Ljava/lang/String;")
	public final String displayName;

	@OriginalMember(owner = "client!dfa", name = "C", descriptor = "I")
	public final int elementId;

	@OriginalMember(owner = "client!dfa", name = "D", descriptor = "Lclient!uh;")
	public final Deque worldMapPlacements;

	@OriginalMember(owner = "client!dfa", name = "<init>", descriptor = "(ILjava/lang/String;Ljava/lang/String;IIZII)V")
	public WorldMapElement(@OriginalArg(0) int elementId, @OriginalArg(1) String string, @OriginalArg(2) String string2, @OriginalArg(3) int bitpackedPlacement, @OriginalArg(4) int color, @OriginalArg(5) boolean shouldRender, @OriginalArg(6) int defaultZoomLevel, @OriginalArg(7) int mapsizeId) {
		this.defaultZoom = defaultZoomLevel;
		this.bitpackedPlacement = bitpackedPlacement;
		this.filenamePrefix = string;
		this.displayName = string2;
		this.color = color;
		this.elementId = elementId;
		this.shouldRender = shouldRender;
		if (this.defaultZoom == 255) {
			this.defaultZoom = 0;
		}
		this.worldMapPlacements = new Deque();
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "([IIIIB)Z")
	public boolean convertSourceToDisplay(@OriginalArg(0) int[] position, @OriginalArg(1) int x, @OriginalArg(2) int z, @OriginalArg(3) int plane) {
		for (@Pc(11) WorldMapPlacement worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.first(); worldMapChunk != null; worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.next()) {
			if (worldMapChunk.withinPlaneBounds(z, x, plane)) {
				worldMapChunk.gameToMapCoords(z, position, x);
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(BII)Z")
	public boolean sourceContains(@OriginalArg(1) int x, @OriginalArg(2) int z) {
		for (@Pc(19) WorldMapPlacement chunk = (WorldMapPlacement) this.worldMapPlacements.first(); chunk != null; chunk = (WorldMapPlacement) this.worldMapPlacements.next()) {
			if (chunk.withinBounds(x, z)) {
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(ZII[I)Z")
	public boolean mapToGameCoords(@OriginalArg(1) int mapY, @OriginalArg(2) int mapX, @OriginalArg(3) int[] position) {
		for (@Pc(20) WorldMapPlacement worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.first(); worldMapChunk != null; worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.next()) {
			if (worldMapChunk.displayContains(mapX, mapY)) {
				worldMapChunk.mapToGameCoords(mapX, mapY, position);
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!dfa", name = "f", descriptor = "(B)V")
	public void recalculateBounds() {
		this.boundsMinX = 12800;
		this.displayMinZ = 12800;
		this.displayZ2 = 0;
		this.displayMaxX = 0;
		for (@Pc(32) WorldMapPlacement worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.first(); worldMapChunk != null; worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.next()) {
			if (worldMapChunk.displayMaxZ < this.boundsMinX) {
				this.boundsMinX = worldMapChunk.displayMaxZ;
			}
			if (this.displayZ2 < worldMapChunk.displayZ2) {
				this.displayZ2 = worldMapChunk.displayZ2;
			}
			if (this.displayMaxX < worldMapChunk.displayZ1) {
				this.displayMaxX = worldMapChunk.displayZ1;
			}
			if (this.displayMinZ > worldMapChunk.displayMinZ) {
				this.displayMinZ = worldMapChunk.displayMinZ;
			}
		}
	}

	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "([IIII)Z")
	public boolean convertDisplayToSource(@OriginalArg(0) int[] position, @OriginalArg(2) int z, @OriginalArg(3) int x) {
		for (@Pc(11) WorldMapPlacement worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.first(); worldMapChunk != null; worldMapChunk = (WorldMapPlacement) this.worldMapPlacements.next()) {
			if (worldMapChunk.withinBounds(x, z)) {
				worldMapChunk.gameToMapCoords(z, position, x);
				return true;
			}
		}
		return false;
	}
	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(IZLclient!in;I)Lclient!dfa;")
	public static WorldMapElement list(@OriginalArg(0) int fileId, @OriginalArg(2) Js5ResourceProvider resource, @OriginalArg(3) int groupId) {
		@Pc(14) Packet packet = new Packet(resource.getFile(groupId, fileId));
		@Pc(52) WorldMapElement worldMapArea = new WorldMapElement(fileId, packet.readString(), packet.readString(), packet.readInt(), packet.readInt(), packet.readUByte() == 1, packet.readUByte(), packet.readUByte());
		@Pc(56) int placementCount = packet.readUByte();
		for (@Pc(58) int i = 0; i < placementCount; i++) {
			worldMapArea.worldMapPlacements.addLast(new WorldMapPlacement(packet.readUByte(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort(), packet.readUShort()));
		}
		worldMapArea.recalculateBounds();
		return worldMapArea;
	}
}
