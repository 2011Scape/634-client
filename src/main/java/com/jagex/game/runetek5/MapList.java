package com.jagex.game.runetek5;

import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.config.staticelements.StaticElements;
import com.jagex.game.runetek5.cache.node.MultiLocEntry;
import com.jagex.game.runetek5.entity.WorldMapTile;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import com.jagex.game.runetek5.config.flu.FluTypeList;
import com.jagex.game.runetek5.config.loctype.LocTypeList;
import com.jagex.game.runetek5.config.mec.MECTypeList;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;

public final class MapList {

	@OriginalMember(owner = "client!kp", name = "G", descriptor = "[[[Lclient!tca;")
	public static EntityNodeList[][][] tiles;

	@OriginalMember(owner = "client!kp", name = "I", descriptor = "Lclient!eq;")
	public static IterableHashTable multiLocs;

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(II)Lclient!dfa;")
	public static WorldMapElement getMapDef(@OriginalArg(0) int coordX, @OriginalArg(1) int coordY) {
		for (@Pc(4) WorldMapElement area = (WorldMapElement) WORLDMAP_DEFS.first(); area != null; area = (WorldMapElement) WORLDMAP_DEFS.next()) {
			if (area.shouldRender && area.sourceContains(coordX, coordY)) {
				return area;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;)V")
	public static void drawTiles(@OriginalArg(0) Toolkit toolkit) {
		@Pc(3) int viewWidth = WorldMap.tileViewEndX - WorldMap.tileViewStartX;
		@Pc(7) int viewHeight = WorldMap.tileViewEndY - WorldMap.tileViewStartY;
		@Pc(15) int scaleX = (WorldMap.screenX2 - WorldMap.pixelViewStartX << 16) / viewWidth;
		@Pc(23) int scaleZ = (WorldMap.pixelViewEndY - WorldMap.screenY1 << 16) / viewHeight;
		WorldMapRenderer.drawTiles(toolkit, scaleX, scaleZ);
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;II)V")
	public static void renderMap(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int pixelsPerTileX, @OriginalArg(2) int pixelsPerTileY) {
		@Pc(9) Packet packet = new Packet(MAP_AREA_RESOURCE.getFile(WorldMap.area.filenamePrefix, "area"));
		@Pc(13) int underlayCount = packet.readUByte();
		@Pc(16) int[] underlayIdTable = new int[underlayCount];
		for (@Pc(18) int i = 0; i < underlayCount; i++) {
			underlayIdTable[i] = packet.readUByte();
		}
		@Pc(33) int overlayCount = packet.readUByte();
		@Pc(36) int[] overlayIdTable = new int[overlayCount];
		for (@Pc(38) int j = 0; j < overlayCount; j++) {
			overlayIdTable[j] = packet.readUByte();
		}
		while (true) {
			@Pc(58) int local58;
			@Pc(64) int local64;
			@Pc(67) int local67;
			@Pc(76) int local76;
			@Pc(148) int local148;
			while (packet.currentPosition < packet.buffer.length) {
				@Pc(62) int mapSquareY;
				@Pc(84) int local84;
				if (packet.readUByte() == 0) {
					int mapSquareX = packet.readUByte();
					mapSquareY = packet.readUByte();
					for (int localX = 0; localX < 64; localX++) {
						for (int localZ = 0; localZ < 64; localZ++) {
							int tileX = mapSquareX * 64 + localX - WorldMap.mapSquareAbsX;
							int tileZ = mapSquareY * 64 + localZ - WorldMap.mapSquareAbsY;
							decodeTile(toolkit, packet, mapSquareX, mapSquareY, tileX, tileZ, underlayIdTable, overlayIdTable);
						}
					}
				} else {
					int mapSquareX = packet.readUByte();
					mapSquareY = packet.readUByte();
					int chunkX = packet.readUByte();
					int chunkZ = packet.readUByte();
					for (int offsetX = 0; offsetX < 8; offsetX++) {
						for (int offsetZ = 0; offsetZ < 8; offsetZ++) {
							@Pc(136) int x = mapSquareX * 64 + chunkX * 8 + offsetX - WorldMap.mapSquareAbsX;
							int z = mapSquareY * 64 + chunkZ * 8 + offsetZ - WorldMap.mapSquareAbsY;
							decodeTile(toolkit, packet, mapSquareX, mapSquareY, x, z, underlayIdTable, overlayIdTable);
						}
					}
				}
			}
			WorldMap.underlayColoursHigh = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
			WorldMap.underlayColoursLow = new short[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
			for (int level = 0; level < 3; level++) {
				@Pc(191) byte[] underlays = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
				for (int squareX = 0; squareX < tiles[level].length; squareX++) {
					for (int squareZ = 0; squareZ < tiles[level][0].length; squareZ++) {
						@Pc(205) EntityNodeList linkedList = tiles[level][squareX][squareZ];
						if (linkedList != null) {
							for (@Pc(212) WorldMapTile worldMapTile = (WorldMapTile) linkedList.getFirst(); worldMapTile != null; worldMapTile = (WorldMapTile) linkedList.getNext()) {
								underlays[squareX * 64 + worldMapTile.x + (squareZ * 64 + worldMapTile.z) * WorldMapRenderer.mapSizeX] = (byte) worldMapTile.underlayIdOrColour;
							}
						}
					}
				}
				WorldMap.decodeUnderlayColors(underlays, WorldMap.underlayColoursHigh, WorldMap.underlayColoursLow, pixelsPerTileX, pixelsPerTileY);
				for (int squareX = 0; squareX < tiles[level].length; squareX++) {
					for (int squareZ = 0; squareZ < tiles[level][0].length; squareZ++) {
						@Pc(276) EntityNodeList linkedList2 = tiles[level][squareX][squareZ];
						if (linkedList2 != null) {
							for (@Pc(283) WorldMapTile worldMapTile2 = (WorldMapTile) linkedList2.getFirst(); worldMapTile2 != null; worldMapTile2 = (WorldMapTile) linkedList2.getNext()) {
								int tileIndex = squareX * 64 + worldMapTile2.x + (squareZ * 64 + worldMapTile2.z) * WorldMapRenderer.mapSizeX;
								worldMapTile2.underlayIdOrColour = (WorldMap.underlayColoursHigh[tileIndex] & 0xFF) << 16 | WorldMap.underlayColoursLow[tileIndex] & 0xFFFF;
								if (worldMapTile2.underlayIdOrColour != 0) {
									worldMapTile2.underlayIdOrColour |= 0xFF000000;
								}
							}
						}
					}
				}
			}
			WorldMap.decodeUnderlayColors(WorldMap.underlayIds, WorldMap.underlayColoursHigh, WorldMap.underlayColoursLow, pixelsPerTileX, pixelsPerTileY);
			WorldMap.underlayIds = null;
			WorldMap.buildMapCategories();
			return;
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIII[S[B)V")
	public static void drawMsiMultiple(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int drawX, @OriginalArg(2) int drawY, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) short[] locIds, @OriginalArg(6) byte[] rotations) {
		if (locIds == null) {
			return;
		}
		for (@Pc(4) int i = 0; i < locIds.length; i++) {
			@Pc(14) LocType locType = LOCTYPE_LIST.list(locIds[i] & 0xFFFF);
			@Pc(17) int msi = locType.mapIconId;
			if (msi != -1) {
				@Pc(25) MapSpriteType msiType = MAP_SPRITE_TYPE_LIST.list(msi);
				@Pc(49) NativeSprite sprite = msiType.createRotatedNativeSprite(locType.mapIconRotates ? rotations[i] >> 6 & 0x3 : 0, locType.mapIconFlipped ? locType.inverted : false, toolkit);
				if (sprite != null) {
					@Pc(58) int spriteWidth = width * sprite.scaleWidth() >> 2;
					@Pc(65) int spriteHeight = height * sprite.scaleHeight() >> 2;
					if (msiType.requiresUpscaling) {
						@Pc(71) int locWidth = locType.sizeX;
						@Pc(74) int locLength = locType.sizeY;
						if ((rotations[i] >> 6 & 0x1) == 1) {
							@Pc(85) int temp = locWidth;
							locWidth = locLength;
							locLength = temp;
						}
						spriteWidth = locWidth * width;
						spriteHeight = locLength * height;
					}
					if (spriteWidth != 0 && spriteHeight != 0) {
						if (msiType.backgroundColor == 0) {
							sprite.drawTintedScaled(drawX, drawY + height - spriteHeight, spriteWidth, spriteHeight);
						} else {
							sprite.drawCircle(drawX, drawY + height - spriteHeight, spriteWidth, spriteHeight, 0, msiType.backgroundColor | 0xFF000000, 1);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!cea;IIII[I[I)V")
	public static void decodeTile(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Packet packet, @OriginalArg(2) int chunkX, @OriginalArg(3) int chunkZ, @OriginalArg(4) int x, @OriginalArg(5) int z, @OriginalArg(6) int[] underlayPalette, @OriginalArg(7) int[] overlayPalette) {
		@Pc(3) int flags = packet.readUByte();
		if ((flags & 0x1) == 0) {
			@Pc(15) boolean underlay = (flags & 0x2) == 0;
			@Pc(21) int id = flags >> 2 & 0x3F;
			if (id != 62) {
				if (id == 63) {
					id = packet.readUByte();
				} else if (underlay) {
					id = underlayPalette[id];
				} else {
					id = overlayPalette[id];
				}
				if (underlay) {
					WorldMap.underlayIds[x + z * WorldMapRenderer.mapSizeX] = (byte) id;
					WorldMap.overlayIds[x + z * WorldMapRenderer.mapSizeX] = 0;
				} else {
					WorldMap.overlayIds[x + z * WorldMapRenderer.mapSizeX] = (byte) id;
					WorldMap.overlayShapes[x + z * WorldMapRenderer.mapSizeX] = 0;
					WorldMap.underlayIds[x + z * WorldMapRenderer.mapSizeX] = packet.readByte();
				}
			}
			return;
		}
		@Pc(100) int levels = (flags >> 1 & 0x3) + 1;
		@Pc(108) boolean hasOverlay = (flags & 0x8) != 0;
		@Pc(116) boolean hasLocs = (flags & 0x10) != 0;
		for (@Pc(118) int level = 0; level < levels; level++) {
			@Pc(123) int underlayId = packet.readUByte();
			@Pc(125) int overlayId = 0;
			@Pc(127) int shape = 0;
			if (hasOverlay) {
				overlayId = packet.readUByte();
				shape = packet.readUByte();
			}
			@Pc(139) int locCount = 0;
			if (hasLocs) {
				locCount = packet.readUByte();
			}
			@Pc(215) short[] levelLocIds;
			@Pc(218) byte[] levelLocShapes;
			@Pc(220) int i;
			if (level == 0) {
				WorldMap.underlayIds[x + z * WorldMapRenderer.mapSizeX] = (byte) underlayId;
				WorldMap.overlayIds[x + z * WorldMapRenderer.mapSizeX] = (byte) overlayId;
				WorldMap.overlayShapes[x + z * WorldMapRenderer.mapSizeX] = (byte) shape;
				if (locCount == 1) {
					WorldMap.tileLocIds[x + z * WorldMapRenderer.mapSizeX] = (short) (packet.readUShort() + 1);
					WorldMap.tileLocShapes[x + z * WorldMapRenderer.mapSizeX] = packet.readByte();
				} else if (locCount > 1) {
					WorldMap.tileLocIds[x + z * WorldMapRenderer.mapSizeX] = -1;
					levelLocIds = new short[locCount];
					levelLocShapes = new byte[locCount];
					for (i = 0; i < locCount; i++) {
						levelLocIds[i] = (short) packet.readUShort();
						levelLocShapes[i] = packet.readByte();
					}
					multiLocs.put((long) (x << 16 | z), new MultiLocEntry(levelLocIds, levelLocShapes));
				}
			} else {
				levelLocIds = null;
				levelLocShapes = null;
				if (locCount > 0) {
					levelLocIds = new short[locCount];
					levelLocShapes = new byte[locCount];
					for (i = 0; i < locCount; i++) {
						levelLocIds[i] = (short) packet.readUShort();
						levelLocShapes[i] = packet.readByte();
					}
				}
				if (tiles[level - 1][chunkX - (WorldMap.mapSquareAbsX >> 6)][chunkZ - (WorldMap.mapSquareAbsY >> 6)] == null) {
					tiles[level - 1][chunkX - (WorldMap.mapSquareAbsX >> 6)][chunkZ - (WorldMap.mapSquareAbsY >> 6)] = new EntityNodeList();
				}
				@Pc(338) WorldMapTile tile = new WorldMapTile(x & 0x3F, z & 0x3F, underlayId, overlayId, shape, levelLocIds, levelLocShapes);
				tiles[level - 1][chunkX - (WorldMap.mapSquareAbsX >> 6)][chunkZ - (WorldMap.mapSquareAbsY >> 6)].offer(tile);
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "Lclient!in;")
	public static Js5ResourceProvider MAP_AREA_RESOURCE;

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(I)Lclient!dfa;")
	public static WorldMapElement getArea(@OriginalArg(0) int id) {
		return (WorldMapElement) WORLDMAP_DEFS.get((long) id);
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(II)Lclient!mn;")
	public static Queue getRenderedElements(@OriginalArg(0) int coordX, @OriginalArg(1) int coordY) {
		@Pc(3) Queue queue = new Queue();
		for (@Pc(8) WorldMapElement area = (WorldMapElement) WORLDMAP_DEFS.first(); area != null; area = (WorldMapElement) WORLDMAP_DEFS.next()) {
			if (area.shouldRender && area.sourceContains(coordX, coordY)) {
				queue.insert(area);
			}
		}
		return queue;
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(Lclient!ha;)Lclient!uh;")
	public static Deque positionElements(@OriginalArg(0) Toolkit toolkit) {
		@Pc(3) int viewWidth = WorldMap.tileViewEndX - WorldMap.tileViewStartX;
		@Pc(7) int viewHeight = WorldMap.tileViewEndY - WorldMap.tileViewStartY;
		@Pc(15) int scaleX = (WorldMap.screenX2 - WorldMap.pixelViewStartX << 16) / viewWidth;
		@Pc(23) int scaleZ = (WorldMap.pixelViewEndY - WorldMap.screenY1 << 16) / viewHeight;
		return WorldMapRenderer.positionElements(toolkit, scaleX, scaleZ);
	}

	@OriginalMember(owner = "client!kp", name = "c", descriptor = "Lclient!cb;")
	public static FloTypeList FLOTYPE_LIST;

	@OriginalMember(owner = "client!kp", name = "d", descriptor = "Lclient!qe;")
	public static FluTypeList FLUTYPE_LIST;

	@OriginalMember(owner = "client!kp", name = "e", descriptor = "()V")
	public static void loadWorldMapCategories() {
		@Pc(2) int[] displayCoords = new int[3];
		for (@Pc(4) int i = 0; i < staticElements.elementCount; i++) {
			@Pc(32) boolean converted = WorldMap.area.convertSourceToDisplay(displayCoords, staticElements.bitpackedPlacement[i] >> 14 & 0x3FFF, staticElements.bitpackedPlacement[i] & 0x3FFF, staticElements.bitpackedPlacement[i] >> 28 & 0x3);
			if (converted) {
				@Pc(42) WorldMapCategory mapElementListEntry = new WorldMapCategory(staticElements.mapCategoryIds[i]);
				mapElementListEntry.x = displayCoords[1] - WorldMap.mapSquareAbsX;
				mapElementListEntry.z = displayCoords[2] - WorldMap.mapSquareAbsY;
				worldMapCategories.addLast(mapElementListEntry);
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "e", descriptor = "Lclient!eq;")
	public static final IterableHashTable WORLDMAP_DEFS = new IterableHashTable(16);

	@OriginalMember(owner = "client!kp", name = "f", descriptor = "Lclient!oda;")
	public static MapSpriteTypeList MAP_SPRITE_TYPE_LIST;

	@OriginalMember(owner = "client!kp", name = "h", descriptor = "Lclient!dd;")
	public static VarDomain PLAYER_VAR_PROVIDER;

	@OriginalMember(owner = "client!kp", name = "i", descriptor = "Lclient!uha;")
	public static LocTypeList LOCTYPE_LIST;

	@OriginalMember(owner = "client!kp", name = "j", descriptor = "Lclient!tu;")
	public static StaticElements staticElements;

	@OriginalMember(owner = "client!kp", name = "r", descriptor = "Lclient!uh;")
	public static final Deque worldMapCategories = new Deque();
}
