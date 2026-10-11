package com.jagex.game.runetek5;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.scene.CollisionMap;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import com.jagex.game.runetek5.config.flu.FluType;
import com.jagex.game.runetek5.config.flu.FluTypeList;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.scene.MapLoader;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.config.flo.FloType;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.math.ColourUtils;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!sr")
public class Terrain {

	@OriginalMember(owner = "client!sr", name = "c", descriptor = "[[[B")
	public byte[][][] cameraHeights;

	@OriginalMember(owner = "client!sr", name = "A", descriptor = "[I")
	private final int[] tileSizeDeltasZ = new int[] { 0, 0, 0, 256, 512, 512, 512, 256, 256, 384, 128, 128, 256 };

	@OriginalMember(owner = "client!sr", name = "w", descriptor = "[I")
	private final int[] tileSizeDeltasX = new int[] { 0, 256, 512, 512, 512, 256, 0, 0, 128, 256, 128, 384, 256 };

	@OriginalMember(owner = "client!sr", name = "h", descriptor = "Z")
	public final boolean underwater;

	@OriginalMember(owner = "client!sr", name = "r", descriptor = "Lclient!cb;")
	private final FloTypeList floorOverlayTypeList;

	@OriginalMember(owner = "client!sr", name = "s", descriptor = "Lclient!qe;")
	private final FluTypeList underlayTypeList;

	@OriginalMember(owner = "client!sr", name = "p", descriptor = "I")
	public final int regionSizeX;

	@OriginalMember(owner = "client!sr", name = "C", descriptor = "I")
	public final int levels;

	@OriginalMember(owner = "client!sr", name = "m", descriptor = "I")
	public final int regionSizeZ;

	@OriginalMember(owner = "client!sr", name = "u", descriptor = "[[[I")
	public final int[][][] tileHeights;

	@OriginalMember(owner = "client!sr", name = "g", descriptor = "[[[B")
	public byte[][][] occluderFlags;

	@OriginalMember(owner = "client!sr", name = "l", descriptor = "[[[B")
	private final byte[][][] underlayIds;

	@OriginalMember(owner = "client!sr", name = "B", descriptor = "[[[B")
	private final byte[][][] overlayRotations;

	@OriginalMember(owner = "client!sr", name = "y", descriptor = "[[[B")
	private final byte[][][] tileShapes;

	@OriginalMember(owner = "client!sr", name = "v", descriptor = "[[[B")
	private final byte[][][] overlayIds;

	@OriginalMember(owner = "client!sr", name = "<init>", descriptor = "(IIIZLclient!cb;Lclient!qe;)V")
	public Terrain(@OriginalArg(0) int levels, @OriginalArg(1) int regionSizeX, @OriginalArg(2) int regionSizeZ, @OriginalArg(3) boolean underwater, @OriginalArg(4) FloTypeList floorOverlayTypeList, @OriginalArg(5) FluTypeList floorUnderlayTypeList) {
		this.underwater = underwater;
		this.floorOverlayTypeList = floorOverlayTypeList;
		this.underlayTypeList = floorUnderlayTypeList;
		this.regionSizeX = regionSizeX;
		this.levels = levels;
		this.regionSizeZ = regionSizeZ;
		this.tileHeights = new int[this.levels][this.regionSizeX + 1][this.regionSizeZ + 1];
		this.occluderFlags = new byte[this.levels][this.regionSizeX + 1][this.regionSizeZ + 1];
		this.underlayIds = new byte[this.levels][this.regionSizeX][this.regionSizeZ];
		this.overlayRotations = new byte[this.levels][this.regionSizeX][this.regionSizeZ];
		this.tileShapes = new byte[this.levels][this.regionSizeX][this.regionSizeZ];
		this.overlayIds = new byte[this.levels][this.regionSizeX][this.regionSizeZ];
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II[[BI[[BILclient!vd;Lclient!hk;[ZI[[BIILclient!ha;)V")
	private void calculateOverlayDisplay(@OriginalArg(0) int x, @OriginalArg(1) int sizeZ, @OriginalArg(2) byte[][] levelShapes, @OriginalArg(3) int z, @OriginalArg(4) byte[][] levelRotations, @OriginalArg(6) FloType spotAnimType, @OriginalArg(7) FluType floorUnderlayType, @OriginalArg(8) boolean[] hasFacesOn, @OriginalArg(9) int tileRotation, @OriginalArg(10) byte[][] levelOverlayIds, @OriginalArg(11) int sizeX, @OriginalArg(12) int pathShape, @OriginalArg(13) Toolkit toolkit) {
		@Pc(18) boolean[] blendingAttributes = spotAnimType != null && spotAnimType.blendsWithUnderlay ? Terrain.BLENDING_SHAPE_EDGE_FACES[pathShape] : Terrain.BLENDED_SHAPE_EDGE_FACES[pathShape];
		@Pc(37) int local37;
		@Pc(47) FloType spotAnimType2;
		@Pc(65) byte shape;
		@Pc(81) int rotation;
		@Pc(86) int local86;
		if (z > 0) {
			if (x > 0) {
				int neighbourOverlayId = levelOverlayIds[x - 1][z - 1] & 0xFF;
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
					if (spotAnimType2.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes[x - 1][z - 1];
						rotation = levelRotations[x - 1][z - 1] * 2 + 4 & 0x7;
						int neighbourColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][rotation]) {
							Terrain.overlayTileColours[0] = spotAnimType2.tileColorHsl;
							Terrain.overlayRenderColours[0] = neighbourColour;
							Terrain.overlayTextureIds[0] = spotAnimType2.texture;
							Terrain.overlayTextureScales[0] = spotAnimType2.textureScale;
							Terrain.overlaySlots[0] = spotAnimType2.slot;
							Terrain.overlayNeighbourMasks[0] = 256;
						}
					}
				}
			}
			if (sizeX - 1 > x) {
				int neighbourOverlayId = levelOverlayIds[x + 1][z - 1] & 0xFF;
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
					if (spotAnimType2.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes[x + 1][z - 1];
						rotation = levelRotations[x + 1][z - 1] * 2 + 6 & 0x7;
						int neighbourColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][rotation]) {
							Terrain.overlayTileColours[2] = spotAnimType2.tileColorHsl;
							Terrain.overlayRenderColours[2] = neighbourColour;
							Terrain.overlayTextureIds[2] = spotAnimType2.texture;
							Terrain.overlayTextureScales[2] = spotAnimType2.textureScale;
							Terrain.overlaySlots[2] = spotAnimType2.slot;
							Terrain.overlayNeighbourMasks[2] = 512;
						}
					}
				}
			}
		}
		if (z < sizeZ - 1) {
			if (x > 0) {
				int neighbourOverlayId = levelOverlayIds[x - 1][z + 1] & 0xFF;
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
					if (spotAnimType2.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes[x - 1][z + 1];
						rotation = levelRotations[x - 1][z + 1] * 2 + 2 & 0x7;
						int neighbourColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][rotation]) {
							Terrain.overlayTileColours[6] = spotAnimType2.tileColorHsl;
							Terrain.overlayRenderColours[6] = neighbourColour;
							Terrain.overlayTextureIds[6] = spotAnimType2.texture;
							Terrain.overlayTextureScales[6] = spotAnimType2.textureScale;
							Terrain.overlaySlots[6] = spotAnimType2.slot;
							Terrain.overlayNeighbourMasks[6] = 64;
						}
					}
				}
			}
			if (x < sizeX - 1) {
				int neighbourOverlayId = levelOverlayIds[x + 1][z + 1] & 0xFF;
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
					if (spotAnimType2.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes[x + 1][z + 1];
						rotation = levelRotations[x + 1][z + 1] * 2 & 0x7;
						int neighbourColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][rotation]) {
							Terrain.overlayTileColours[4] = spotAnimType2.tileColorHsl;
							Terrain.overlayRenderColours[4] = neighbourColour;
							Terrain.overlayTextureIds[4] = spotAnimType2.texture;
							Terrain.overlayTextureScales[4] = spotAnimType2.textureScale;
							Terrain.overlaySlots[4] = spotAnimType2.slot;
							Terrain.overlayNeighbourMasks[4] = 128;
						}
					}
				}
			}
		}
		@Pc(492) int neighbourVertex;
		@Pc(497) int renderColour;
		@Pc(499) int step;
		@Pc(481) byte neighbourRotation;
		if (z > 0) {
			int neighbourOverlayId = levelOverlayIds[x][z - 1] & 0xFF;
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
				if (spotAnimType2.tileColorHsl != -1) {
					shape = levelShapes[x][z - 1];
					neighbourRotation = levelRotations[x][z - 1];
					if (spotAnimType2.blendsWithUnderlay) {
						int overlayVertex = 2;
						neighbourVertex = neighbourRotation * 2 + 4;
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						for (step = 0; step < 3; step++) {
							overlayVertex &= 0x7;
							neighbourVertex &= 0x7;
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][neighbourVertex] && spotAnimType2.slot >= Terrain.overlaySlots[overlayVertex]) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl;
								Terrain.overlayRenderColours[overlayVertex] = renderColour;
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture;
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale;
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] |= 0x20;
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 32;
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot;
							}
							neighbourVertex++;
							overlayVertex--;
						}
						if (!blendingAttributes[tileRotation & 0x3]) {
							hasFacesOn[0] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape][neighbourRotation + 2 & 0x3];
						}
					} else if (!blendingAttributes[tileRotation & 0x3]) {
						hasFacesOn[0] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape][neighbourRotation + 2 & 0x3];
					}
				}
			}
		}
		if (sizeZ - 1 > z) {
			int neighbourOverlayId = levelOverlayIds[x][z + 1] & 0xFF;
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
				if (spotAnimType2.tileColorHsl != -1) {
					shape = levelShapes[x][z + 1];
					neighbourRotation = levelRotations[x][z + 1];
					if (spotAnimType2.blendsWithUnderlay) {
						int overlayVertex = 4;
						neighbourVertex = neighbourRotation * 2 + 2;
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						for (step = 0; step < 3; step++) {
							overlayVertex &= 0x7;
							neighbourVertex &= 0x7;
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][neighbourVertex] && Terrain.overlaySlots[overlayVertex] <= spotAnimType2.slot) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl;
								Terrain.overlayRenderColours[overlayVertex] = renderColour;
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture;
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale;
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] |= 0x10;
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 16;
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot;
							}
							neighbourVertex--;
							overlayVertex++;
						}
						if (!blendingAttributes[tileRotation + 2 & 0x3]) {
							hasFacesOn[2] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape][--neighbourRotation & 0x3];
						}
					} else if (!blendingAttributes[tileRotation + 2 & 0x3]) {
						hasFacesOn[2] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape][neighbourRotation & 0x3];
					}
				}
			}
		}
		if (x > 0) {
			int neighbourOverlayId = levelOverlayIds[x - 1][z] & 0xFF;
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
				if (spotAnimType2.tileColorHsl != -1) {
					shape = levelShapes[x - 1][z];
					neighbourRotation = levelRotations[x - 1][z];
					if (spotAnimType2.blendsWithUnderlay) {
						int overlayVertex = 6;
						neighbourVertex = neighbourRotation * 2 + 4;
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						for (step = 0; step < 3; step++) {
							neighbourVertex &= 0x7;
							overlayVertex &= 0x7;
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][neighbourVertex] && Terrain.overlaySlots[overlayVertex] <= spotAnimType2.slot) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl;
								Terrain.overlayRenderColours[overlayVertex] = renderColour;
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture;
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale;
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] |= 0x8;
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 8;
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot;
							}
							overlayVertex++;
							neighbourVertex--;
						}
						if (!blendingAttributes[tileRotation + 3 & 0x3]) {
							hasFacesOn[3] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape][neighbourRotation + 1 & 0x3];
						}
					} else if (!blendingAttributes[tileRotation + 3 & 0x3]) {
						hasFacesOn[3] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape][neighbourRotation + 1 & 0x3];
					}
				}
			}
		}
		if (x < sizeX - 1) {
			int neighbourOverlayId = levelOverlayIds[x + 1][z] & 0xFF;
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList.list(neighbourOverlayId - 1);
				if (spotAnimType2.tileColorHsl != -1) {
					shape = levelShapes[x + 1][z];
					neighbourRotation = levelRotations[x + 1][z];
					if (spotAnimType2.blendsWithUnderlay) {
						int overlayVertex = 4;
						neighbourVertex = neighbourRotation * 2 + 6;
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2);
						for (step = 0; step < 3; step++) {
							overlayVertex &= 0x7;
							neighbourVertex &= 0x7;
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape][neighbourVertex] && spotAnimType2.slot >= Terrain.overlaySlots[overlayVertex]) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl;
								Terrain.overlayRenderColours[overlayVertex] = renderColour;
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture;
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale;
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] |= 0x4;
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 4;
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot;
							}
							neighbourVertex++;
							overlayVertex--;
						}
						if (!blendingAttributes[tileRotation + 1 & 0x3]) {
							hasFacesOn[1] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape][neighbourRotation + 3 & 0x3];
						}
					} else if (!blendingAttributes[tileRotation + 1 & 0x3]) {
						hasFacesOn[1] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape][neighbourRotation + 3 & 0x3];
					}
				}
			}
		}
		if (spotAnimType == null) {
			return;
		}
		int overlayColour = FloType.getMinimapColorHsl(toolkit, spotAnimType);
		if (!spotAnimType.blendsWithUnderlay) {
			return;
		}
		for (@Pc(1198) int i = 0; i < 8; i++) {
			@Pc(1209) int signedRotation = i - tileRotation * 2 & 0x7;
			if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][i] && spotAnimType.slot >= Terrain.overlaySlots[signedRotation]) {
				Terrain.overlayTileColours[signedRotation] = spotAnimType.tileColorHsl;
				Terrain.overlayRenderColours[signedRotation] = overlayColour;
				Terrain.overlayTextureIds[signedRotation] = spotAnimType.texture;
				Terrain.overlayTextureScales[signedRotation] = spotAnimType.textureScale;
				if (Terrain.overlaySlots[signedRotation] == spotAnimType.slot) {
					Terrain.overlayNeighbourMasks[signedRotation] |= 0x2;
				} else {
					Terrain.overlayNeighbourMasks[signedRotation] = 2;
				}
				Terrain.overlaySlots[signedRotation] = spotAnimType.slot;
			}
		}
		return;
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!s;Lclient!s;ILclient!s;[[ILclient!ha;I)V")
	private void addBlendedTiles(@OriginalArg(0) Ground ground, @OriginalArg(1) Ground ground2, @OriginalArg(3) Ground ground3, @OriginalArg(4) int[][] floorColours, @OriginalArg(5) Toolkit toolkit, @OriginalArg(6) int level) {
		@Pc(10) byte[][] levelShapes = this.tileShapes[level];
		@Pc(21) byte[][] levelRotations = this.overlayRotations[level];
		@Pc(26) byte[][] levelUnderlayIds = this.underlayIds[level];
		@Pc(31) byte[][] levelOverlayIds = this.overlayIds[level];
		for (@Pc(33) int i = 0; i < this.regionSizeX; i++) {
			@Pc(47) int nextX = i >= this.regionSizeX - 1 ? i : i + 1;
			for (@Pc(49) int j = 0; j < this.regionSizeZ; j++) {
				@Pc(67) int nextZ = this.regionSizeZ - 1 > j ? j + 1 : j;
				if (Minimap.visibleFromLevel == -1 || SettingsBits.isVisibleFromLevel(j, i, Minimap.visibleFromLevel, level)) {
					@Pc(82) boolean hasShadows = false;
					@Pc(84) boolean overlaySupportsBlending = false;
					@Pc(87) boolean[] hasFacesOn = new boolean[4];
					@Pc(93) int pathShape = levelShapes[i][j];
					@Pc(99) int tileRotation = levelRotations[i][j];
					@Pc(107) int overlayId = levelOverlayIds[i][j] & 0xFF;
					@Pc(115) int underlayId = levelUnderlayIds[i][j] & 0xFF;
					@Pc(123) int underlayIdB = levelUnderlayIds[i][nextZ] & 0xFF;
					@Pc(131) int underlayIdC = levelUnderlayIds[nextX][nextZ] & 0xFF;
					@Pc(139) int underlayIdD = levelUnderlayIds[nextX][j] & 0xFF;
					if (overlayId != 0 || underlayId != 0) {
						@Pc(161) FloType spotAnimType = overlayId == 0 ? null : this.floorOverlayTypeList.list(overlayId - 1);
						@Pc(176) FluType floorUnderlayType = underlayId == 0 ? null : this.underlayTypeList.list(underlayId - 1);
						if (pathShape == 0 && spotAnimType == null) {
							pathShape = 12;
						}
						@Pc(184) FloType spotAnimType2 = spotAnimType;
						if (spotAnimType != null) {
							if (spotAnimType.tileColorHsl == -1 && spotAnimType.minimapColorHsl == -1) {
								spotAnimType2 = spotAnimType;
								spotAnimType = null;
							} else if (floorUnderlayType != null && pathShape != 0) {
								overlaySupportsBlending = spotAnimType.blendsWithUnderlay;
							}
						}
						@Pc(292) int local292;
						@Pc(322) int local322;
						@Pc(372) int local372;
						@Pc(385) int local385;
						if ((pathShape == 0 || pathShape == 12) && i > 0 && j > 0 && i < this.regionSizeX && j < this.regionSizeZ) {
							@Pc(258) int underlayNorthEast = levelUnderlayIds[nextX][nextZ] == underlayId ? 1 : -1;
							@Pc(273) int underlaySouthEast = levelUnderlayIds[nextX][j - 1] == underlayId ? 1 : -1;
							int underlaySouthWest = underlayId == levelUnderlayIds[i - 1][j - 1] ? 1 : -1;
							if (levelUnderlayIds[i][j - 1] == underlayId) {
								underlaySouthWest++;
								underlaySouthEast++;
							} else {
								underlaySouthEast--;
								underlaySouthWest--;
							}
							int underlayNorthWest = levelUnderlayIds[i - 1][nextZ] == underlayId ? 1 : -1;
							if (underlayId == levelUnderlayIds[nextX][j]) {
								underlaySouthEast++;
								underlayNorthEast++;
							} else {
								underlaySouthEast--;
								underlayNorthEast--;
							}
							if (levelUnderlayIds[i][nextZ] == underlayId) {
								underlayNorthEast++;
								underlayNorthWest++;
							} else {
								underlayNorthWest--;
								underlayNorthEast--;
							}
							if (underlayId == levelUnderlayIds[i - 1][j]) {
								underlayNorthWest++;
								underlaySouthWest++;
							} else {
								underlayNorthWest--;
								underlaySouthWest--;
							}
							int southWestNorthEastDiff = underlaySouthWest - underlayNorthEast;
							if (southWestNorthEastDiff < 0) {
								southWestNorthEastDiff = -southWestNorthEastDiff;
							}
							int southEastNorthWestDiff = underlaySouthEast - underlayNorthWest;
							if (southEastNorthWestDiff < 0) {
								southEastNorthWestDiff = -southEastNorthWestDiff;
							}
							if (southWestNorthEastDiff == southEastNorthWestDiff) {
								southWestNorthEastDiff = ground2.getHeight(j, i) - ground2.getHeight(nextZ, nextX);
								if (southWestNorthEastDiff < 0) {
									southWestNorthEastDiff = -southWestNorthEastDiff;
								}
								southEastNorthWestDiff = ground2.getHeight(j, nextX) - ground2.getHeight(nextZ, i);
								if (southEastNorthWestDiff < 0) {
									southEastNorthWestDiff = -southEastNorthWestDiff;
								}
							}
							tileRotation = southWestNorthEastDiff >= southEastNorthWestDiff ? 0 : 1;
						}
						for (int overlayVertex = 0; overlayVertex < 13; overlayVertex++) {
							Terrain.overlaySlots[overlayVertex] = -1;
							Terrain.overlayNeighbourMasks[overlayVertex] = 1;
						}
						@Pc(475) boolean[] blendingAttributes = spotAnimType != null && spotAnimType.blendsWithUnderlay ? Terrain.BLENDING_SHAPE_EDGE_FACES[pathShape] : Terrain.BLENDED_SHAPE_EDGE_FACES[pathShape];
						this.calculateOverlayDisplay(i, this.regionSizeZ, levelShapes, j, levelRotations, spotAnimType, floorUnderlayType, hasFacesOn, tileRotation, levelOverlayIds, this.regionSizeX, pathShape, toolkit);
						@Pc(508) boolean overlayHasMultiColour = spotAnimType != null && spotAnimType.minimapColorHsl != spotAnimType.tileColorHsl;
						if (!overlayHasMultiColour) {
							for (int overlayVertex = 0; overlayVertex < 8; overlayVertex++) {
								if (Terrain.overlaySlots[overlayVertex] >= 0 && Terrain.overlayRenderColours[overlayVertex] != Terrain.overlayTileColours[overlayVertex]) {
									overlayHasMultiColour = true;
									break;
								}
							}
						}
						if (!blendingAttributes[tileRotation + 1 & 0x3]) {
							hasFacesOn[1] = Terrain.booleanOr(hasFacesOn[1], (Terrain.overlayNeighbourMasks[2] & Terrain.overlayNeighbourMasks[4]) == 0);
						}
						if (!blendingAttributes[tileRotation + 3 & 0x3]) {
							hasFacesOn[3] = Terrain.booleanOr(hasFacesOn[3], (Terrain.overlayNeighbourMasks[0] & Terrain.overlayNeighbourMasks[6]) == 0);
						}
						if (!blendingAttributes[tileRotation & 0x3]) {
							hasFacesOn[0] = Terrain.booleanOr(hasFacesOn[0], (Terrain.overlayNeighbourMasks[0] & Terrain.overlayNeighbourMasks[2]) == 0);
						}
						if (!blendingAttributes[tileRotation + 2 & 0x3]) {
							hasFacesOn[2] = Terrain.booleanOr(hasFacesOn[2], (Terrain.overlayNeighbourMasks[4] & Terrain.overlayNeighbourMasks[6]) == 0);
						}
						if (!overlaySupportsBlending && (pathShape == 0 || pathShape == 12)) {
							if (hasFacesOn[0] && !hasFacesOn[1] && !hasFacesOn[2] && hasFacesOn[3]) {
								hasFacesOn[0] = hasFacesOn[3] = false;
								tileRotation = 0;
								pathShape = pathShape == 0 ? 13 : 14;
							} else if (hasFacesOn[0] && hasFacesOn[1] && !hasFacesOn[2] && !hasFacesOn[3]) {
								pathShape = pathShape == 0 ? 13 : 14;
								hasFacesOn[0] = hasFacesOn[1] = false;
								tileRotation = 3;
							} else if (!hasFacesOn[0] && hasFacesOn[1] && hasFacesOn[2] && !hasFacesOn[3]) {
								tileRotation = 2;
								pathShape = pathShape == 0 ? 13 : 14;
								hasFacesOn[1] = hasFacesOn[2] = false;
							} else if (!hasFacesOn[0] && !hasFacesOn[1] && hasFacesOn[2] && hasFacesOn[3]) {
								pathShape = pathShape == 0 ? 13 : 14;
								hasFacesOn[2] = hasFacesOn[3] = false;
								tileRotation = 1;
							}
						}
						@Pc(819) boolean unblendable = !overlaySupportsBlending && !hasFacesOn[0] && !hasFacesOn[2] && !hasFacesOn[1] && !hasFacesOn[3];
						@Pc(821) int[] overlayFaceCounts = null;
						@Pc(837) int[] shapeVertexA;
						@Pc(845) int[] shapeVertexB;
						@Pc(849) int[] shapeVertexC;
						int underlayFaceCount;
						int overlayFaceCount;
						if (unblendable) {
							shapeVertexA = MapLoader.SHAPE_VERTEX_A[pathShape];
							shapeVertexB = MapLoader.SHAPE_VERTEX_B[pathShape];
							underlayFaceCount = floorUnderlayType == null ? 0 : MapLoader.UNDERLAY_FACE_COUNT[pathShape];
							shapeVertexC = MapLoader.SHAPE_VERTEX_C[pathShape];
							overlayFaceCount = spotAnimType == null ? 0 : Terrain.faceVertices[pathShape];
						} else if (overlaySupportsBlending) {
							overlayFaceCount = spotAnimType == null ? 0 : Terrain.OVERLAY_FACE_COUNT_BLENDED[pathShape];
							shapeVertexA = Terrain.BLENDING_SHAPE_VERTEX_A[pathShape];
							overlayFaceCounts = Terrain.BLENDING_FACE_MAPPING[pathShape];
							shapeVertexB = MapLoader.BLENDING_SHAPE_VERTEX_B[pathShape];
							shapeVertexC = Terrain.BLENDING_SHAPE_VERTEX_C[pathShape];
							underlayFaceCount = floorUnderlayType == null ? 0 : Terrain.UNDERLAY_FACE_COUNT_BLENDED[pathShape];
						} else {
							underlayFaceCount = floorUnderlayType == null ? 0 : Terrain.UNDERLAY_FACE_COUNTS[pathShape];
							overlayFaceCount = spotAnimType == null ? 0 : Terrain.OVERLAY_FACE_COUNTS[pathShape];
							shapeVertexB = MapLoader.BLENDED_SHAPE_VERTEX_B[pathShape];
							shapeVertexC = Terrain.BLENDED_SHAPE_VERTEX_C[pathShape];
							shapeVertexA = MapLoader.BLENDED_SHAPE_VERTEX_A[pathShape];
							overlayFaceCounts = MapLoader.BLENDED_FACE_MAPPING[pathShape];
						}
						@Pc(923) int floorFaceCount = overlayFaceCount + underlayFaceCount;
						if (floorFaceCount <= 0) {
							Scene.loadTiles(level, i, j);
						} else {
							if (hasFacesOn[0]) {
								floorFaceCount++;
							}
							if (hasFacesOn[2]) {
								floorFaceCount++;
							}
							if (hasFacesOn[1]) {
								floorFaceCount++;
							}
							if (hasFacesOn[3]) {
								floorFaceCount++;
							}
							@Pc(953) int overlayTotalFaces = 0;
							@Pc(955) int vertexIndex = 0;
							@Pc(959) int vertexCount = floorFaceCount * 3;
							@Pc(966) int[] blendingColours = overlayHasMultiColour ? new int[vertexCount] : null;
							@Pc(969) int[] offsetsX = new int[vertexCount];
							@Pc(972) int[] offsetsZ = new int[vertexCount];
							@Pc(975) int[] mainColours = new int[vertexCount];
							@Pc(978) int[] textures = new int[vertexCount];
							@Pc(981) int[] textureScales = new int[vertexCount];
							@Pc(988) int[] nextLevelHeightDiffs = ground == null ? null : new int[vertexCount];
							@Pc(997) int[] localLevelHeightDiffs = ground == null && ground3 == null ? null : new int[vertexCount];
							@Pc(999) int overlayHslColour = -1;
							@Pc(1001) int overlayTextureId = -1;
							@Pc(1003) int overlayTextureScale = 256;
							@Pc(1109) byte faceVertexCount;
							@Pc(1021) int local1021;
							@Pc(1023) int local1023;
							@Pc(1255) int local1255;
							@Pc(1261) int local1261;
							@Pc(1270) int local1270;
							@Pc(1275) int local1275;
							@Pc(1292) int local1292;
							@Pc(1280) int local1280;
							@Pc(1297) int local1297;
							@Pc(1348) int local1348;
							@Pc(1354) int local1354;
							if (spotAnimType != null) {
								overlayTextureId = spotAnimType.texture;
								overlayTextureScale = spotAnimType.textureScale;
								overlayHslColour = spotAnimType.tileColorHsl;
								int overlayBlendColour = FloType.getMinimapColorHsl(toolkit, spotAnimType);
								for (int overlayFace = 0; overlayFace < overlayFaceCount; overlayFace++) {
									if (hasFacesOn[-tileRotation & 0x3] && overlayFaceCounts[0] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 1;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 1;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
										faceVertexCount = 6;
									} else if (hasFacesOn[2 - tileRotation & 0x3] && overlayFaceCounts[2] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 5;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 5;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 6;
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
									} else if (hasFacesOn[1 - tileRotation & 0x3] && overlayTotalFaces == overlayFaceCounts[1]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 3;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 3;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 6;
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
									} else if (hasFacesOn[3 - tileRotation & 0x3] && overlayFaceCounts[3] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 7;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 7;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 6;
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
									} else {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = shapeVertexB[overlayTotalFaces];
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										faceVertexCount = 3;
									}
									for (int faceVertex = 0; faceVertex < faceVertexCount; faceVertex++) {
										int tileVertex = Terrain.overlayFaceVertices[faceVertex];
										int rotatedVertex = tileVertex - tileRotation * 2 & 0x7;
										int deltaX = this.tileSizeDeltasX[tileVertex];
										int deltaZ = this.tileSizeDeltasZ[tileVertex];
										int vertexOffsetX;
										int vertexOffsetZ;
										if (tileRotation == 1) {
											vertexOffsetZ = 512 - deltaX;
											vertexOffsetX = deltaZ;
										} else if (tileRotation == 2) {
											vertexOffsetX = 512 - deltaX;
											vertexOffsetZ = 512 - deltaZ;
										} else if (tileRotation == 3) {
											vertexOffsetZ = deltaX;
											vertexOffsetX = 512 - deltaZ;
										} else {
											vertexOffsetZ = deltaZ;
											vertexOffsetX = deltaX;
										}
										offsetsX[vertexIndex] = vertexOffsetX;
										offsetsZ[vertexIndex] = vertexOffsetZ;
										if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
											int worldX = vertexOffsetX + (i << 9);
											int worldZ = vertexOffsetZ + (j << 9);
											nextLevelHeightDiffs[vertexIndex] = ground.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
										}
										if (localLevelHeightDiffs != null) {
											if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												int worldX = (i << 9) + vertexOffsetX;
												int worldZ = vertexOffsetZ + (j << 9);
												localLevelHeightDiffs[vertexIndex] = ground2.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ);
											} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												int worldX = (i << 9) + vertexOffsetX;
												int worldZ = (j << 9) + vertexOffsetZ;
												localLevelHeightDiffs[vertexIndex] = ground3.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
											}
										}
										if (tileVertex < 8 && Terrain.overlaySlots[rotatedVertex] > spotAnimType.slot) {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = Terrain.overlayRenderColours[rotatedVertex];
											}
											textureScales[vertexIndex] = Terrain.overlayTextureScales[rotatedVertex];
											textures[vertexIndex] = Terrain.overlayTextureIds[rotatedVertex];
											mainColours[vertexIndex] = Terrain.overlayTileColours[rotatedVertex];
										} else {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = overlayBlendColour;
											}
											textures[vertexIndex] = spotAnimType.texture;
											textureScales[vertexIndex] = spotAnimType.textureScale;
											mainColours[vertexIndex] = overlayHslColour;
										}
										vertexIndex++;
									}
									overlayTotalFaces++;
								}
								if (!this.underwater && level == 0) {
									Terrain.addWater(i, j, spotAnimType.waterColor, spotAnimType.waterFogDepth * 8, spotAnimType.waterIntensity);
								}
								if (pathShape != 12 && spotAnimType.tileColorHsl != -1 && spotAnimType.shadowed) {
									hasShadows = true;
								}
							} else if (unblendable) {
								overlayTotalFaces = Terrain.faceVertices[pathShape];
							} else if (overlaySupportsBlending) {
								overlayTotalFaces = Terrain.OVERLAY_FACE_COUNT_BLENDED[pathShape];
							} else {
								overlayTotalFaces = Terrain.OVERLAY_FACE_COUNTS[pathShape];
							}
							if (floorUnderlayType != null) {
								if (underlayIdD == 0) {
									underlayIdD = underlayId;
								}
								if (underlayIdC == 0) {
									underlayIdC = underlayId;
								}
								if (underlayIdB == 0) {
									underlayIdB = underlayId;
								}
								@Pc(1600) FluType floorUnderlayType2 = this.underlayTypeList.list(underlayId - 1);
								@Pc(1608) FluType floorUnderlayType3 = this.underlayTypeList.list(underlayIdB - 1);
								@Pc(1616) FluType floorUnderlayType4 = this.underlayTypeList.list(underlayIdC - 1);
								@Pc(1624) FluType floorUnderlayType5 = this.underlayTypeList.list(underlayIdD - 1);
								for (int underlayFace = 0; underlayFace < underlayFaceCount; underlayFace++) {
									if (hasFacesOn[-tileRotation & 0x3] && overlayTotalFaces == overlayFaceCounts[0]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 1;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 1;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 6;
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
									} else if (hasFacesOn[2 - tileRotation & 0x3] && overlayTotalFaces == overlayFaceCounts[2]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 5;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 5;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 6;
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
									} else if (hasFacesOn[1 - tileRotation & 0x3] && overlayTotalFaces == overlayFaceCounts[1]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 3;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 3;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
										faceVertexCount = 6;
									} else if (hasFacesOn[3 - tileRotation & 0x3] && overlayTotalFaces == overlayFaceCounts[3]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = 7;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
										Terrain.overlayFaceVertices[3] = 7;
										Terrain.overlayFaceVertices[4] = shapeVertexB[overlayTotalFaces];
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces];
										faceVertexCount = 6;
									} else {
										Terrain.overlayFaceVertices[0] = shapeVertexA[overlayTotalFaces];
										Terrain.overlayFaceVertices[1] = shapeVertexB[overlayTotalFaces];
										faceVertexCount = 3;
										Terrain.overlayFaceVertices[2] = shapeVertexC[overlayTotalFaces];
									}
									overlayTotalFaces++;
									for (int faceVertex = 0; faceVertex < faceVertexCount; faceVertex++) {
										int tileVertex = Terrain.overlayFaceVertices[faceVertex];
										int rotatedVertex = tileVertex - tileRotation * 2 & 0x7;
										int deltaX = this.tileSizeDeltasX[tileVertex];
										int deltaZ = this.tileSizeDeltasZ[tileVertex];
										@Pc(1892) int vertexOffsetZ;
										int vertexOffsetX;
										if (tileRotation == 1) {
											vertexOffsetX = deltaZ;
											vertexOffsetZ = 512 - deltaX;
										} else if (tileRotation == 2) {
											vertexOffsetZ = 512 - deltaZ;
											vertexOffsetX = 512 - deltaX;
										} else if (tileRotation == 3) {
											vertexOffsetZ = deltaX;
											vertexOffsetX = 512 - deltaZ;
										} else {
											vertexOffsetZ = deltaZ;
											vertexOffsetX = deltaX;
										}
										offsetsX[vertexIndex] = vertexOffsetX;
										offsetsZ[vertexIndex] = vertexOffsetZ;
										@Pc(1947) int local1947;
										@Pc(1954) int local1954;
										if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
											int worldX = vertexOffsetX + (i << 9);
											int worldZ = (j << 9) + vertexOffsetZ;
											nextLevelHeightDiffs[vertexIndex] = ground.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
										}
										if (localLevelHeightDiffs != null) {
											if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												int worldX = (i << 9) + vertexOffsetX;
												int worldZ = vertexOffsetZ + (j << 9);
												localLevelHeightDiffs[vertexIndex] = ground2.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ);
											} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												int worldX = (i << 9) + vertexOffsetX;
												int worldZ = vertexOffsetZ + (j << 9);
												localLevelHeightDiffs[vertexIndex] = ground3.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
											}
										}
										if (tileVertex < 8 && Terrain.overlaySlots[rotatedVertex] >= 0) {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = Terrain.overlayRenderColours[rotatedVertex];
											}
											textureScales[vertexIndex] = Terrain.overlayTextureScales[rotatedVertex];
											textures[vertexIndex] = Terrain.overlayTextureIds[rotatedVertex];
											mainColours[vertexIndex] = Terrain.overlayTileColours[rotatedVertex];
										} else {
											if (overlaySupportsBlending && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												textures[vertexIndex] = overlayTextureId;
												textureScales[vertexIndex] = overlayTextureScale;
												mainColours[vertexIndex] = overlayHslColour;
											} else if (vertexOffsetX == 0 && vertexOffsetZ == 0) {
												mainColours[vertexIndex] = floorColours[i][j];
												textures[vertexIndex] = floorUnderlayType2.texture;
												textureScales[vertexIndex] = floorUnderlayType2.scale;
											} else if (vertexOffsetX == 0 && vertexOffsetZ == 512) {
												mainColours[vertexIndex] = floorColours[i][nextZ];
												textures[vertexIndex] = floorUnderlayType3.texture;
												textureScales[vertexIndex] = floorUnderlayType3.scale;
											} else if (vertexOffsetX == 512 && vertexOffsetZ == 512) {
												mainColours[vertexIndex] = floorColours[nextX][nextZ];
												textures[vertexIndex] = floorUnderlayType4.texture;
												textureScales[vertexIndex] = floorUnderlayType4.scale;
											} else if (vertexOffsetX == 512 && vertexOffsetZ == 0) {
												mainColours[vertexIndex] = floorColours[nextX][j];
												textures[vertexIndex] = floorUnderlayType5.texture;
												textureScales[vertexIndex] = floorUnderlayType5.scale;
											} else {
												if (vertexOffsetX >= 256) {
													if (vertexOffsetZ >= 256) {
														textures[vertexIndex] = floorUnderlayType4.texture;
														textureScales[vertexIndex] = floorUnderlayType4.scale;
													} else {
														textures[vertexIndex] = floorUnderlayType5.texture;
														textureScales[vertexIndex] = floorUnderlayType5.scale;
													}
												} else if (vertexOffsetZ < 256) {
													textures[vertexIndex] = floorUnderlayType2.texture;
													textureScales[vertexIndex] = floorUnderlayType2.scale;
												} else {
													textures[vertexIndex] = floorUnderlayType3.texture;
													textureScales[vertexIndex] = floorUnderlayType3.scale;
												}
												int southColour = ColorUtil.blendHsl(floorColours[i][j], floorColours[nextX][j], vertexOffsetX << 7 >> 9);
												int northColour = ColorUtil.blendHsl(floorColours[i][nextZ], floorColours[nextX][nextZ], vertexOffsetX << 7 >> 9);
												mainColours[vertexIndex] = ColorUtil.blendHsl(southColour, northColour, vertexOffsetZ << 7 >> 9);
											}
											if (blendingColours != null) {
												blendingColours[vertexIndex] = mainColours[vertexIndex];
											}
										}
										vertexIndex++;
									}
								}
								if (pathShape != 0 && floorUnderlayType.shadowed) {
									hasShadows = true;
								}
							}
							int southWestHeight = ground2.getHeight(j, i);
							int southEastHeight = ground2.getHeight(j, nextX);
							int northEastHeight = ground2.getHeight(nextZ, nextX);
							int northWestHeight = ground2.getHeight(nextZ, i);
							@Pc(2381) boolean bridge = SettingsBits.isBridge(j, i);
							if (bridge && level > 1 || !bridge && level > 0) {
								@Pc(2397) boolean occludes = true;
								if (floorUnderlayType != null && !floorUnderlayType.occlude) {
									occludes = false;
								} else if (underlayId == 0 && pathShape != 0) {
									occludes = false;
								} else if (overlayId > 0 && spotAnimType2 != null && !spotAnimType2.occlude) {
									occludes = false;
								}
								if (occludes && southWestHeight == southEastHeight && northEastHeight == southWestHeight && southWestHeight == northWestHeight) {
									this.occluderFlags[level][i][j] = (byte) (this.occluderFlags[level][i][j] | 0x4);
								}
							}
							int waterColour = 0;
							int waterScale = 0;
							int waterIntensity = 0;
							if (this.underwater) {
								waterColour = Scene.getWaterColor(i, j);
								waterScale = Scene.getWaterScale(i, j);
								waterIntensity = Scene.getWaterIntensity(i, j);
							}
							ground2.U(i, j, offsetsX, nextLevelHeightDiffs, offsetsZ, localLevelHeightDiffs, mainColours, blendingColours, textures, textureScales, waterColour, waterScale, waterIntensity, hasShadows);
							Scene.loadTiles(level, i, j);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIIIII)V")
	public final void setTileHeights(@OriginalArg(0) int x, @OriginalArg(1) int length, @OriginalArg(2) int z, @OriginalArg(4) int width, @OriginalArg(5) int level) {
		@Pc(13) int local13;
		for (@Pc(9) int i = z; i < length + z; i++) {
			for (int tileX = x; tileX < width + x; tileX++) {
				if (tileX >= 0 && tileX < this.regionSizeX && i >= 0 && this.regionSizeZ > i) {
					this.tileHeights[level][tileX][i] = level <= 0 ? 0 : this.tileHeights[level - 1][tileX][i] - 960;
				}
			}
		}
		if (x > 0 && x < this.regionSizeX) {
			for (int tileZ = z + 1; tileZ < z + length; tileZ++) {
				if (tileZ >= 0 && tileZ < this.regionSizeZ) {
					this.tileHeights[level][x][tileZ] = this.tileHeights[level][x - 1][tileZ];
				}
			}
		}
		if (z > 0 && z < this.regionSizeZ) {
			for (int tileX = x + 1; tileX < width + x; tileX++) {
				if (tileX >= 0 && tileX < this.regionSizeX) {
					this.tileHeights[level][tileX][z] = this.tileHeights[level][tileX][z - 1];
				}
			}
		}
		if (x < 0 || z < 0 || this.regionSizeX <= x || z >= this.regionSizeZ) {
			return;
		}
		if (level != 0) {
			if (x > 0 && this.tileHeights[level - 1][x - 1][z] != this.tileHeights[level][x - 1][z]) {
				this.tileHeights[level][x][z] = this.tileHeights[level][x - 1][z];
				return;
			}
			if (z > 0 && this.tileHeights[level][x][z - 1] != this.tileHeights[level - 1][x][z - 1]) {
				this.tileHeights[level][x][z] = this.tileHeights[level][x][z - 1];
				return;
			}
			if (x > 0 && z > 0 && this.tileHeights[level][x - 1][z - 1] != this.tileHeights[level - 1][x - 1][z - 1]) {
				this.tileHeights[level][x][z] = this.tileHeights[level][x - 1][z - 1];
				return;
			}
			return;
		}
		if (x > 0 && this.tileHeights[level][x - 1][z] != 0) {
			this.tileHeights[level][x][z] = this.tileHeights[level][x - 1][z];
			return;
		}
		if (z > 0 && this.tileHeights[level][x][z - 1] != 0) {
			this.tileHeights[level][x][z] = this.tileHeights[level][x][z - 1];
			return;
		}
		if (x > 0 && z > 0 && this.tileHeights[level][x - 1][z - 1] != 0) {
			this.tileHeights[level][x][z] = this.tileHeights[level][x - 1][z - 1];
			return;
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II[[I)V")
	public final void addHeightOffsets(@OriginalArg(2) int[][] heightOffsets) {
		@Pc(10) int[][] heights = this.tileHeights[0];
		for (@Pc(12) int x = 0; x < this.regionSizeX + 1; x++) {
			for (@Pc(16) int z = 0; z < this.regionSizeZ + 1; z++) {
				heights[x][z] += heightOffsets[x][z];
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!s;BLclient!s;Lclient!ha;)V")
	public final void calculateUnderlayPalette(@OriginalArg(0) Ground ground, @OriginalArg(2) Ground ground2, @OriginalArg(3) Toolkit toolkit) {
		@Pc(11) int[][] floorColours = new int[this.regionSizeX][this.regionSizeZ];
		if (Terrain.underlayHues == null || this.regionSizeZ != Terrain.underlayHues.length) {
			Terrain.underlayCounts = new int[this.regionSizeZ];
			PlayerList.underlaySaturation = new int[this.regionSizeZ];
			Terrain.underlayDividers = new int[this.regionSizeZ];
			Terrain.underlayHues = new int[this.regionSizeZ];
			Terrain.underlayLightness = new int[this.regionSizeZ];
		}
		@Pc(48) int local48;
		for (@Pc(44) int i = 0; i < this.levels; i++) {
			for (int z = 0; z < this.regionSizeZ; z++) {
				Terrain.underlayHues[z] = 0;
				PlayerList.underlaySaturation[z] = 0;
				Terrain.underlayLightness[z] = 0;
				Terrain.underlayDividers[z] = 0;
				Terrain.underlayCounts[z] = 0;
			}
			for (@Pc(77) int j = -5; j < this.regionSizeX; j++) {
				@Pc(87) int local87;
				@Pc(106) int local106;
				@Pc(175) int local175;
				for (@Pc(81) int k = 0; k < this.regionSizeZ; k++) {
					int forwardX = j + 5;
					@Pc(155) int local155;
					if (forwardX < this.regionSizeX) {
						int addedUnderlayId = this.underlayIds[i][forwardX][k] & 0xFF;
						if (addedUnderlayId > 0) {
							@Pc(119) FluType floorUnderlayType = this.underlayTypeList.list(addedUnderlayId - 1);
							Terrain.underlayHues[k] += floorUnderlayType.hue;
							PlayerList.underlaySaturation[k] += floorUnderlayType.saturation;
							Terrain.underlayLightness[k] += floorUnderlayType.lightness;
							Terrain.underlayDividers[k] += floorUnderlayType.hueMultiplier;
							int previousCount = Terrain.underlayCounts[k]++;
						}
					}
					int backwardX = j - 5;
					if (backwardX >= 0) {
						int removedUnderlayId = this.underlayIds[i][backwardX][k] & 0xFF;
						if (removedUnderlayId > 0) {
							@Pc(185) FluType floorUnderlayType2 = this.underlayTypeList.list(removedUnderlayId - 1);
							Terrain.underlayHues[k] -= floorUnderlayType2.hue;
							PlayerList.underlaySaturation[k] -= floorUnderlayType2.saturation;
							Terrain.underlayLightness[k] -= floorUnderlayType2.lightness;
							Terrain.underlayDividers[k] -= floorUnderlayType2.hueMultiplier;
							int previousCount = Terrain.underlayCounts[k]--;
						}
					}
				}
				if (j >= 0) {
					int hueSum = 0;
					int saturationSum = 0;
					int lightnessSum = 0;
					@Pc(243) int dividerSum = 0;
					@Pc(245) int count = 0;
					for (@Pc(247) int m = -5; m < this.regionSizeZ; m++) {
						@Pc(253) int forwardZ = m + 5;
						if (this.regionSizeZ > forwardZ) {
							lightnessSum += Terrain.underlayLightness[forwardZ];
							saturationSum += PlayerList.underlaySaturation[forwardZ];
							hueSum += Terrain.underlayHues[forwardZ];
							count += Terrain.underlayCounts[forwardZ];
							dividerSum += Terrain.underlayDividers[forwardZ];
						}
						@Pc(295) int backwardZ = m - 5;
						if (backwardZ >= 0) {
							dividerSum -= Terrain.underlayDividers[backwardZ];
							hueSum -= Terrain.underlayHues[backwardZ];
							lightnessSum -= Terrain.underlayLightness[backwardZ];
							count -= Terrain.underlayCounts[backwardZ];
							saturationSum -= PlayerList.underlaySaturation[backwardZ];
						}
						if (m >= 0 && dividerSum > 0 && count > 0) {
							floorColours[j][m] = ColourUtils.packHsl(lightnessSum / count, saturationSum / count, hueSum * 256 / dividerSum);
						}
					}
				}
			}
			if (Terrain.blendFloor) {
				this.addBlendedTiles(i == 0 ? ground : null, Scene.aClass65Array1[i], i == 0 ? ground2 : null, floorColours, toolkit, i);
			} else {
				this.addRegularTile(toolkit, i == 0 ? ground : null, i, floorColours, Scene.aClass65Array1[i], i == 0 ? ground2 : null);
			}
			this.underlayIds[i] = null;
			this.overlayIds[i] = null;
			this.tileShapes[i] = null;
			this.overlayRotations[i] = null;
		}
		if (!this.underwater) {
			if (MapLoader.hdShadows != 0) {
				Ground.addHdShadow();
			}
			if (Terrain.hdLight) {
				Scene.addHdLight();
			}
		}
		for (int level = 0; level < this.levels; level++) {
			Scene.aClass65Array1[level].YA();
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!cea;I[Lclient!fe;IIIB)V")
	public final void decodeTilesClient(@OriginalArg(0) Packet packet, @OriginalArg(1) int chunkX, @OriginalArg(2) CollisionMap[] collisionMaps, @OriginalArg(3) int baseZ, @OriginalArg(4) int baseX, @OriginalArg(5) int chunkZ) {
		@Pc(10) int local10;
		@Pc(22) int local22;
		@Pc(28) int local28;
		@Pc(32) int local32;
		if (!this.underwater) {
			for (int level = 0; level < 4; level++) {
				@Pc(16) CollisionMap collisionMap = collisionMaps[level];
				for (@Pc(18) int i = 0; i < 64; i++) {
					for (int localZ = 0; localZ < 64; localZ++) {
						int tileX = i + chunkX;
						int tileZ = chunkZ + localZ;
						if (tileX >= 0 && this.regionSizeX > tileX && tileZ >= 0 && tileZ < this.regionSizeZ) {
							collisionMap.unflagBlocked(tileZ, tileX);
						}
					}
				}
			}
		}
		int absoluteX = chunkX + baseX;
		@Pc(91) int absoluteZ = baseZ + chunkZ;
		for (int level = 0; level < this.levels; level++) {
			for (int localX = 0; localX < 64; localX++) {
				for (int localZ = 0; localZ < 64; localZ++) {
					this.decodeTile(false, 0, localZ + chunkZ, 0, packet, 0, chunkX + localX, absoluteX + localX, absoluteZ + localZ, level);
				}
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(ZIIILclient!cea;IIIIII)V")
	private void decodeTile(@OriginalArg(0) boolean heightOnly, @OriginalArg(1) int offsetZ, @OriginalArg(2) int z, @OriginalArg(3) int rotation, @OriginalArg(4) Packet packet, @OriginalArg(5) int offsetX, @OriginalArg(7) int x, @OriginalArg(8) int absoluteX, @OriginalArg(9) int absoluteZ, @OriginalArg(10) int level) {
		if (rotation == 1) {
			offsetZ = 1;
		} else if (rotation == 2) {
			offsetZ = 1;
			offsetX = 1;
		} else if (rotation == 3) {
			offsetX = 1;
		}
		@Pc(53) int opcode;
		if (x < 0 || this.regionSizeX <= x || z < 0 || z >= this.regionSizeZ) {
			while (true) {
				opcode = packet.readUByte();
				if (opcode == 0) {
					break;
				}
				if (opcode == 1) {
					packet.readUByte();
					break;
				}
				if (opcode <= 49) {
					packet.readUByte();
				}
			}
			return;
		}
		if (!this.underwater && !heightOnly) {
			SettingsBits.tileFunctionMasks[level][x][z] = 0;
		}
		while (true) {
			opcode = packet.readUByte();
			if (opcode == 0) {
				if (this.underwater) {
					this.tileHeights[0][offsetX + x][offsetZ + z] = 0;
				} else if (level == 0) {
					this.tileHeights[0][offsetX + x][z + offsetZ] = -Terrain.calculateTileHeight(absoluteX + 932731, absoluteZ + 556238) * 8 << 2;
				} else {
					this.tileHeights[level][offsetX + x][offsetZ + z] = this.tileHeights[level - 1][offsetX + x][offsetZ + z] - 960;
				}
				break;
			}
			if (opcode == 1) {
				@Pc(196) int height = packet.readUByte();
				if (this.underwater) {
					this.tileHeights[0][x + offsetX][z + offsetZ] = height * 8 << 2;
				} else {
					if (height == 1) {
						height = 0;
					}
					if (level == 0) {
						this.tileHeights[0][x + offsetX][z + offsetZ] = -height * 8 << 2;
					} else {
						this.tileHeights[level][offsetX + x][offsetZ + z] = this.tileHeights[level - 1][offsetX + x][z + offsetZ] - (height * 8 << 2);
					}
				}
				break;
			}
			if (opcode <= 49) {
				if (heightOnly) {
					packet.readUByte();
				} else {
					this.overlayIds[level][x][z] = packet.readByte();
					this.tileShapes[level][x][z] = (byte) ((opcode - 2) / 4);
					this.overlayRotations[level][x][z] = (byte) (opcode + rotation - 2 & 0x3);
				}
			} else if (opcode <= 81) {
				if (!this.underwater && !heightOnly) {
					SettingsBits.tileFunctionMasks[level][x][z] = (byte) (opcode - 49);
				}
			} else if (!heightOnly) {
				this.underlayIds[level][x][z] = (byte) (opcode - 81);
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!ha;Lclient!s;IB[[ILclient!s;Lclient!s;)V")
	private void addRegularTile(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Ground ground, @OriginalArg(2) int level, @OriginalArg(4) int[][] floorColours, @OriginalArg(5) Ground ground2, @OriginalArg(6) Ground ground3) {
		for (@Pc(7) int i = 0; i < this.regionSizeX; i++) {
			for (@Pc(11) int j = 0; j < this.regionSizeZ; j++) {
				if (Minimap.visibleFromLevel == -1 || SettingsBits.isVisibleFromLevel(j, i, Minimap.visibleFromLevel, level)) {
					@Pc(35) byte overlayShape = this.tileShapes[level][i][j];
					@Pc(44) byte overlayRotation = this.overlayRotations[level][i][j];
					@Pc(55) int overlayId = this.overlayIds[level][i][j] & 0xFF;
					@Pc(66) int underlayId = this.underlayIds[level][i][j] & 0xFF;
					@Pc(81) FloType spotAnimType = overlayId == 0 ? null : this.floorOverlayTypeList.list(overlayId - 1);
					if (overlayShape == 0 && spotAnimType == null) {
						overlayShape = 12;
					}
					@Pc(105) FluType floorUnderlayType = underlayId == 0 ? null : this.underlayTypeList.list(underlayId - 1);
					@Pc(107) int underlayFaceCount = 0;
					@Pc(109) int overlayFaceCount = 0;
					if (overlayShape != 0) {
						overlayFaceCount = spotAnimType == null ? 0 : Terrain.faceVertices[overlayShape];
						underlayFaceCount = floorUnderlayType == null ? 0 : MapLoader.UNDERLAY_FACE_COUNT[overlayShape];
					} else if (spotAnimType != null) {
						overlayFaceCount = Terrain.faceVertices[overlayShape];
					} else if (floorUnderlayType != null) {
						underlayFaceCount = Terrain.faceVertices[overlayShape];
					}
					@Pc(147) int faceCount = overlayFaceCount + underlayFaceCount;
					@Pc(149) int faceIndex = 0;
					if (faceCount != 0) {
						@Pc(161) int overlayTextureId = spotAnimType == null ? -1 : spotAnimType.texture;
						@Pc(168) int underlayTextureId = floorUnderlayType == null ? -1 : floorUnderlayType.texture;
						@Pc(171) int[] colours = new int[faceCount];
						@Pc(174) int[] minimapColours = new int[faceCount];
						@Pc(177) int[] textures = new int[faceCount];
						@Pc(180) int[] textureScales = new int[faceCount];
						@Pc(182) boolean hasMinimapColour = false;
						@Pc(184) boolean noOverlay = false;
						@Pc(202) int length;
						if (spotAnimType == null || spotAnimType.tileColorHsl == -1 && spotAnimType.minimapColorHsl == -1 && overlayTextureId == -1) {
							noOverlay = true;
							for (length = 0; length < overlayFaceCount; length++) {
								colours[faceIndex] = -1;
								faceIndex++;
							}
						} else {
							for (length = 0; length < overlayFaceCount; length++) {
								textures[faceIndex] = overlayTextureId;
								textureScales[faceIndex] = spotAnimType.textureScale;
								if (spotAnimType.tileColorHsl == -1) {
									colours[faceIndex] = -1;
								} else {
									colours[faceIndex] = spotAnimType.tileColorHsl;
								}
								if (spotAnimType.minimapColorHsl == -1) {
									minimapColours[faceIndex] = -1;
								} else {
									hasMinimapColour = true;
									minimapColours[faceIndex] = spotAnimType.minimapColorHsl;
								}
								faceIndex++;
							}
							if (!this.underwater && level == 0) {
								Terrain.addWater(i, j, spotAnimType.waterColor, spotAnimType.waterFogDepth * 8, spotAnimType.waterIntensity);
							}
						}
						if (!hasMinimapColour) {
							minimapColours = null;
						}
						if (floorUnderlayType == null) {
							if (noOverlay) {
								continue;
							}
							for (length = 0; length < underlayFaceCount; length++) {
								colours[faceIndex] = -1;
								faceIndex++;
							}
						} else {
							for (length = 0; length < underlayFaceCount; length++) {
								textures[faceIndex] = underlayTextureId;
								textureScales[faceIndex] = floorUnderlayType.scale;
								colours[faceIndex] = floorColours[i][j];
								if (minimapColours != null) {
									minimapColours[faceIndex] = -1;
								}
								faceIndex++;
							}
						}
						length = this.tileSizeDeltasX.length;
						@Pc(356) int[] sizesX = new int[length];
						@Pc(359) int[] sizesZ = new int[length];
						@Pc(366) int[] nextLevelHeightDiffs = ground == null ? null : new int[length];
						@Pc(375) int[] localLevelHeightDiffs = ground == null && ground3 == null ? null : new int[length];
						@Pc(384) int local384;
						@Pc(389) int local389;
						@Pc(474) int local474;
						@Pc(483) int local483;
						for (@Pc(377) int k = 0; k < length; k++) {
							int deltaX = this.tileSizeDeltasX[k];
							int deltaZ = this.tileSizeDeltasZ[k];
							if (overlayRotation == 0) {
								sizesX[k] = deltaX;
								sizesZ[k] = deltaZ;
							} else if (overlayRotation == 1) {
								sizesX[k] = deltaZ;
								sizesZ[k] = 512 - deltaX;
							} else if (overlayRotation == 2) {
								sizesX[k] = 512 - deltaX;
								sizesZ[k] = 512 - deltaZ;
							} else if (overlayRotation == 3) {
								sizesX[k] = 512 - deltaZ;
								sizesZ[k] = deltaX;
							}
							if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape][k]) {
								int worldX = sizesX[k] + (i << 9);
								int worldZ = (j << 9) + sizesZ[k];
								nextLevelHeightDiffs[k] = ground.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
							}
							if (localLevelHeightDiffs != null) {
								if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape][k]) {
									int worldX = sizesX[k] + (i << 9);
									int worldZ = (j << 9) + sizesZ[k];
									localLevelHeightDiffs[k] = ground2.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ);
								} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape][k]) {
									int worldX = sizesX[k] + (i << 9);
									int worldZ = sizesZ[k] + (j << 9);
									localLevelHeightDiffs[k] = ground3.averageHeight(worldX, worldZ) - ground2.averageHeight(worldX, worldZ);
								}
							}
						}
						int southWestHeight = ground2.getHeight(j, i);
						int southEastHeight = ground2.getHeight(j, i + 1);
						int northEastHeight = ground2.getHeight(j + 1, i - -1);
						int northWestHeight = ground2.getHeight(j + 1, i);
						@Pc(620) boolean bridge = SettingsBits.isBridge(j, i);
						if (bridge && level > 1 || !bridge && level > 0) {
							@Pc(631) boolean occludes = true;
							if (floorUnderlayType != null && !floorUnderlayType.occlude) {
								occludes = false;
							} else if (underlayId == 0 && overlayShape != 0) {
								occludes = false;
							} else if (overlayId > 0 && spotAnimType != null && !spotAnimType.occlude) {
								occludes = false;
							}
							if (occludes && southWestHeight == southEastHeight && northEastHeight == southWestHeight && northWestHeight == southWestHeight) {
								this.occluderFlags[level][i][j] = (byte) (this.occluderFlags[level][i][j] | 0x4);
							}
						}
						@Pc(704) int waterColour = 0;
						@Pc(706) int waterScale = 0;
						@Pc(708) int waterIntensity = 0;
						if (this.underwater) {
							waterColour = Scene.getWaterColor(i, j);
							waterScale = Scene.getWaterScale(i, j);
							waterIntensity = Scene.getWaterIntensity(i, j);
						}
						ground2.addTile(i, j, sizesX, nextLevelHeightDiffs, sizesZ, localLevelHeightDiffs, MapLoader.SHAPE_VERTEX_A[overlayShape], MapLoader.SHAPE_VERTEX_B[overlayShape], MapLoader.SHAPE_VERTEX_C[overlayShape], colours, minimapColours, textures, textureScales, waterColour, waterScale, waterIntensity);
						Scene.loadTiles(level, i, j);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIILclient!cea;III[Lclient!fe;II)V")
	public final void decodeTilesServer(@OriginalArg(0) int chunkStartX, @OriginalArg(1) int level, @OriginalArg(3) Packet packet, @OriginalArg(4) int sourceChunkZ, @OriginalArg(5) int chunkStartZ, @OriginalArg(6) int sourceChunkX, @OriginalArg(7) CollisionMap[] collisionMaps, @OriginalArg(8) int rotation, @OriginalArg(9) int sourceLevel) {
		@Pc(15) int sourceTileX = (sourceChunkX & 0x7) * 8;
		@Pc(21) int sourceTileZ = (sourceChunkZ & 0x7) * 8;
		@Pc(30) int local30;
		@Pc(60) int local60;
		if (!this.underwater) {
			@Pc(28) CollisionMap collisionMap = collisionMaps[level];
			for (int localX = 0; localX < 8; localX++) {
				for (@Pc(34) int i = 0; i < 8; i++) {
					@Pc(48) int clipX = chunkStartX + Terrain.rotateTerrainBlockX(i & 0x7, rotation, localX & 0x7);
					int clipZ = TerrainBlockRotationY.rotateTerrainBlockY(localX & 0x7, i & 0x7, rotation) + chunkStartZ;
					if (clipX > 0 && clipX < this.regionSizeX - 1 && clipZ > 0 && this.regionSizeZ - 1 > clipZ) {
						collisionMap.unflagBlocked(clipZ, clipX);
					}
				}
			}
		}
		@Pc(98) int regionBaseX = (sourceChunkX & 0xFFFFFFF8) << 3;
		int regionBaseZ = (sourceChunkZ & 0xFFFFFFF8) << 3;
		@Pc(106) byte offsetX = 0;
		@Pc(108) byte offsetZ = 0;
		if (rotation == 1) {
			offsetZ = 1;
		} else if (rotation == 2) {
			offsetZ = 1;
			offsetX = 1;
		} else if (rotation == 3) {
			offsetX = 1;
		}
		for (int currentLevel = 0; currentLevel < this.levels; currentLevel++) {
			for (@Pc(139) int j = 0; j < 64; j++) {
				for (@Pc(143) int k = 0; k < 64; k++) {
					if (sourceLevel == currentLevel && j >= sourceTileX && sourceTileX + 8 >= j && k >= sourceTileZ && k <= sourceTileZ + 8) {
						@Pc(217) int destX;
						@Pc(229) int destZ;
						if (j == sourceTileX + 8 || sourceTileZ + 8 == k) {
							if (rotation == 0) {
								destZ = k + chunkStartZ - sourceTileZ;
								destX = j + chunkStartX - sourceTileX;
							} else if (rotation == 1) {
								destZ = chunkStartZ + sourceTileX + 8 - j;
								destX = k + chunkStartX - sourceTileZ;
							} else if (rotation == 2) {
								destZ = chunkStartZ + sourceTileZ + 8 - k;
								destX = sourceTileX + chunkStartX + 8 - j;
							} else {
								destX = sourceTileZ + chunkStartX + 8 - k;
								destZ = chunkStartZ + j - sourceTileX;
							}
							this.decodeTile(true, 0, destZ, 0, packet, 0, destX, j + regionBaseX, k + regionBaseZ, level);
						} else {
							destX = Terrain.rotateTerrainBlockX(k & 0x7, rotation, j & 0x7) + chunkStartX;
							destZ = chunkStartZ + TerrainBlockRotationY.rotateTerrainBlockY(j & 0x7, k & 0x7, rotation);
							this.decodeTile(false, offsetZ, destZ, rotation, packet, offsetX, destX, regionBaseX + j, k + regionBaseZ, level);
						}
						if (j == 63 || k == 63) {
							@Pc(355) byte edgeCount = 1;
							if (j == 63 && k == 63) {
								edgeCount = 3;
							}
							for (@Pc(369) int m = 0; m < edgeCount; m++) {
								@Pc(373) int edgeTileX = j;
								@Pc(375) int edgeTileZ = k;
								if (m == 0) {
									edgeTileZ = k == 63 ? 64 : k;
									edgeTileX = j == 63 ? 64 : j;
								} else if (m == 1) {
									edgeTileX = 64;
								} else if (m == 2) {
									edgeTileZ = 64;
								}
								@Pc(426) int edgeDestX;
								@Pc(419) int edgeDestZ;
								if (rotation == 0) {
									edgeDestZ = edgeTileZ + chunkStartZ - sourceTileZ;
									edgeDestX = edgeTileX + chunkStartX - sourceTileX;
								} else if (rotation == 1) {
									edgeDestZ = sourceTileX + chunkStartZ + 8 - edgeTileX;
									edgeDestX = chunkStartX + edgeTileZ - sourceTileZ;
								} else if (rotation == 2) {
									edgeDestZ = chunkStartZ + sourceTileZ + 8 - edgeTileZ;
									edgeDestX = sourceTileX + chunkStartX + 8 - edgeTileX;
								} else {
									edgeDestX = sourceTileZ + chunkStartX + 8 - edgeTileZ;
									edgeDestZ = edgeTileX + chunkStartZ - sourceTileX;
								}
								if (edgeDestX >= 0 && this.regionSizeX > edgeDestX && edgeDestZ >= 0 && edgeDestZ < this.regionSizeZ) {
									this.tileHeights[level][edgeDestX][edgeDestZ] = this.tileHeights[level][offsetX + destX][offsetZ + destZ];
								}
							}
						}
					} else {
						this.decodeTile(false, 0, -1, 0, packet, 0, -1, 0, 0, 0);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!ha;[[[II[Lclient!fe;)V")
	public final void initializeFloor(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int[][][] underwaterHeights, @OriginalArg(3) CollisionMap[] collisionMaps) {
		@Pc(19) int level;
		@Pc(23) int local23;
		@Pc(27) int local27;
		if (!this.underwater) {
			for (level = 0; level < 4; level++) {
				for (int tileX = 0; tileX < this.regionSizeX; tileX++) {
					for (int tileZ = 0; tileZ < this.regionSizeZ; tileZ++) {
						if ((SettingsBits.tileFunctionMasks[level][tileX][tileZ] & 0x1) != 0) {
							@Pc(44) int clipLevel = level;
							if ((SettingsBits.tileFunctionMasks[1][tileX][tileZ] & 0x2) != 0) {
								clipLevel = level - 1;
							}
							if (clipLevel >= 0) {
								collisionMaps[clipLevel].flagBlocked(tileZ, tileX);
							}
						}
					}
				}
			}
		}
		for (level = 0; level < this.levels; level++) {
			int groundFlags = 0;
			int featureFlags = 0;
			if (!this.underwater) {
				if (Terrain.hdLight) {
					groundFlags = 2;
				}
				if (Terrain.hdWater) {
					featureFlags = 8;
				}
				if (MapLoader.hdShadows != 0) {
					if (level == 0 | Terrain.highLightDetail) {
						featureFlags |= 0x10;
					}
					groundFlags |= 0x1;
				}
			}
			if (Terrain.hdLight) {
				featureFlags |= 0x7;
			}
			if (!Terrain.enableTextures) {
				featureFlags |= 0x20;
			}
			@Pc(163) int[][] nextHeights = underwaterHeights == null || level >= underwaterHeights.length ? this.tileHeights[level] : underwaterHeights[level];
			Scene.setLocalGround(level, toolkit.createGround(this.regionSizeX, this.regionSizeZ, this.tileHeights[level], nextHeights, groundFlags, featureFlags));
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIIII)V")
	public final void setMapSquareHeights(@OriginalArg(0) int startZ, @OriginalArg(4) int z) {
		for (@Pc(7) int level = 0; level < this.levels; level++) {
			this.setTileHeights(z, 64, startZ, 64, level);
		}
	}

	@OriginalMember(owner = "client!qha", name = "N", descriptor = "[I")
	public static final int[] faceVertices = new int[] { 2, 1, 1, 1, 2, 2, 2, 1, 3, 3, 3, 2, 0, 4, 0 };
	@OriginalMember(owner = "client!fi", name = "U", descriptor = "[I")
	public static final int[] overlayRenderColours = new int[13];

	@OriginalMember(owner = "client!dk", name = "b", descriptor = "[I")
	public static final int[] OVERLAY_FACE_COUNTS = new int[] { 4, 2, 1, 1, 2, 2, 3, 1, 3, 3, 3, 2, 0 };

	@OriginalMember(owner = "client!gb", name = "v", descriptor = "[[Z")
	public static final boolean[][] BLENDING_SHAPE_EDGE_FACES = new boolean[][] { new boolean[4], { false, true, true, false }, { true, false, true, false }, { true, false, true, false }, { false, false, true, false }, { false, false, true, false }, { true, false, true, false }, { true, false, false, true }, { true, false, false, true }, { true, true, false, false }, new boolean[4], { false, true, false, true }, new boolean[4] };

	@OriginalMember(owner = "client!iq", name = "n", descriptor = "[I")
	public static final int[] OVERLAY_FACE_COUNT_BLENDED = new int[] { 4, 2, 1, 1, 2, 2, 3, 1, 3, 3, 3, 2, 0 };

	@OriginalMember(owner = "client!il", name = "A", descriptor = "[[I")
	public static final int[][] BLENDING_SHAPE_VERTEX_C = new int[][] { { 12, 12, 12, 12 }, { 12, 12, 12, 12, 12, 5 }, { 5, 5, 1, 1 }, { 5, 1, 1, 5 }, { 5, 5, 5 }, { 5, 5, 5 }, { 12, 12, 12, 12, 12, 12 }, { 1, 12, 12, 12, 12, 12 }, { 1, 1, 7, 1 }, { 8, 9, 9, 8, 8, 3, 1, 9 }, { 8, 8, 9, 8, 9, 9 }, { 10, 10, 11, 11, 11, 7, 3, 7 }, { 12, 12, 12, 12 } };

	@OriginalMember(owner = "client!il", name = "v", descriptor = "[I")
	public static final int[] overlayTileColours = new int[13];
	@OriginalMember(owner = "client!uk", name = "g", descriptor = "[[Z")
	public static final boolean[][] BLENDED_SHAPE_EDGE_FACES = new boolean[][] { new boolean[4], new boolean[4], { false, false, true, false }, { false, false, true, false }, { false, false, true, false }, { false, false, true, false }, { true, false, true, false }, { true, false, false, true }, { true, false, false, true }, new boolean[4], new boolean[4], new boolean[4], new boolean[4] };

	@OriginalMember(owner = "client!mia", name = "h", descriptor = "[[I")
	public static final int[][] BLENDING_FACE_MAPPING = new int[][] { { 0, 1, 2, 3 }, { 1, -1, -1, 0 }, { -1, 2, -1, 0 }, { -1, 0, -1, 2 }, { 0, 1, -1, 2 }, { 1, 2, -1, 0 }, { -1, 4, -1, 1 }, { -1, 3, 4, -1 }, { -1, 0, 2, -1 }, { -1, -1, 2, 0 }, { 0, 2, 5, 3 }, { 0, -1, 6, -1 }, { 0, 1, 2, 3 } };

	@OriginalMember(owner = "client!wc", name = "a", descriptor = "(IIIB)I")
	public static int rotateTerrainBlockX(@OriginalArg(0) int localZ, @OriginalArg(1) int rotation, @OriginalArg(2) int localX) {
		@Pc(12) int rotationMasked = rotation & 0x3;
		if (rotationMasked == 0) {
			return localX;
		} else if (rotationMasked == 1) {
			return localZ;
		} else if (rotationMasked == 2) {
			return 7 - localX;
		} else {
			return 7 - localZ;
		}
	}

	@OriginalMember(owner = "client!uk", name = "f", descriptor = "[I")
	public static int[] underlayDividers;

	@OriginalMember(owner = "client!gia", name = "p", descriptor = "[I")
	public static int[] underlayLightness;

	@OriginalMember(owner = "client!ws", name = "b", descriptor = "[I")
	public static final int[] overlaySlots = new int[13];
	@OriginalMember(owner = "client!gv", name = "a", descriptor = "Z")
	public static boolean blendFloor = false;

	@OriginalMember(owner = "client!eh", name = "d", descriptor = "[I")
	public static final int[] overlayFaceVertices = new int[6];

	@OriginalMember(owner = "client!ad", name = "g", descriptor = "[I")
	public static final int[] overlayTextureIds = new int[13];

	@OriginalMember(owner = "client!ls", name = "v", descriptor = "[I")
	public static int[] underlayHues;

	@OriginalMember(owner = "client!aga", name = "a", descriptor = "(IIIII)V")
	public static void addWater(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int colour, @OriginalArg(3) int fogDepth, @OriginalArg(4) int intensity) {
		if (Scene.waterColors != null) {
			Scene.waterColors[x][z] = colour | 0xFF000000;
		}
		if (Scene.waterScales != null) {
			Scene.waterScales[x][z] = (short) fogDepth;
		}
		if (Scene.waterIntensities != null) {
			Scene.waterIntensities[x][z] = (byte) intensity;
		}
	}

	@OriginalMember(owner = "client!qg", name = "c", descriptor = "Z")
	public static boolean hdLight = false;

	@OriginalMember(owner = "client!fn", name = "n", descriptor = "Z")
	public static boolean hdWater = false;

	@OriginalMember(owner = "client!wda", name = "c", descriptor = "Z")
	public static boolean highLightDetail = false;

	@OriginalMember(owner = "client!ev", name = "o", descriptor = "[I")
	public static int[] underlayCounts;

	@OriginalMember(owner = "client!vca", name = "u", descriptor = "[[I")
	public static final int[][] BLENDED_SHAPE_VERTEX_C = new int[][] { { 12, 12, 12, 12 }, { 12, 12, 12, 12 }, { 5, 5, 5 }, { 5, 5, 5 }, { 5, 5, 5 }, { 5, 5, 5 }, { 12, 12, 12, 12, 12, 12 }, { 1, 1, 1, 7 }, { 1, 1, 7, 1 }, { 8, 9, 9, 8, 8, 9 }, { 8, 8, 9, 8, 9, 9 }, { 10, 10, 11, 11, 11, 10 }, { 12, 12, 12, 12 } };

	@OriginalMember(owner = "client!vh", name = "d", descriptor = "[I")
	public static final int[] UNDERLAY_FACE_COUNT_BLENDED = new int[] { 0, 4, 3, 3, 1, 1, 3, 5, 1, 5, 3, 6, 4 };

	@OriginalMember(owner = "client!gba", name = "p", descriptor = "[I")
	public static final int[] overlayTextureScales = new int[13];

	@OriginalMember(owner = "client!ca", name = "a", descriptor = "[I")
	public static final int[] UNDERLAY_FACE_COUNTS = new int[] { 0, 2, 2, 2, 1, 1, 3, 3, 1, 3, 3, 4, 4 };
	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(III)I")
	public static int calculateTileHeight(@OriginalArg(1) int x, @OriginalArg(2) int y) {
		@Pc(39) int height = Terrain.perlinNoise(4, y + 91923, x + 45365) + (Terrain.perlinNoise(2, y + 37821, x + 10294) - 128 >> 1) + (Terrain.perlinNoise(1, y, x) + -128 >> 2) - 128;
		height = (int) ((double) height * 0.3D) + 35;
		if (height < 10) {
			height = 10;
		} else if (height > 60) {
			height = 60;
		}
		return height;
	}

	@OriginalMember(owner = "client!mda", name = "c", descriptor = "[[I")
	public static final int[][] BLENDING_SHAPE_VERTEX_A = new int[][] { { 0, 2, 4, 6 }, { 6, 0, 2, 3, 5, 3 }, { 6, 0, 2, 4 }, { 2, 5, 6, 1 }, { 0, 2, 6 }, { 6, 0, 2 }, { 5, 6, 0, 1, 2, 4 }, { 7, 7, 1, 2, 4, 6 }, { 2, 4, 4, 7 }, { 6, 6, 4, 0, 1, 1, 3, 3 }, { 0, 2, 2, 6, 6, 4 }, { 0, 2, 2, 3, 7, 0, 4, 3 }, { 0, 2, 4, 6 } };

	@OriginalMember(owner = "client!qaa", name = "a", descriptor = "[I")
	public static final int[] overlayNeighbourMasks = new int[13];

	@OriginalMember(owner = "client!io", name = "c", descriptor = "Z")
	public static boolean enableTextures = false;
	@OriginalMember(owner = "client!cv", name = "b", descriptor = "(I)V")
	public static void releaseUnderlayData() {
		Terrain.underlayHues = null;
		Terrain.underlayCounts = null;
		PlayerList.underlaySaturation = null;
		MapRegion.lowDetailShadows = false;
		Terrain.underlayDividers = null;
		Terrain.underlayLightness = null;
	}

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(ZZ)Z")
	public static boolean booleanOr(@OriginalArg(0) boolean lhs, @OriginalArg(1) boolean rhs) {
		return lhs | rhs;
	}

	@OriginalMember(owner = "client!ik", name = "a", descriptor = "(III)I")
	public static int randomNoiseWeightedSum(@OriginalArg(0) int x, @OriginalArg(2) int y) {
		@Pc(41) int corners = Terrain.randomNoise(x - 1, y - 1) + Terrain.randomNoise(x + 1, y + -1) + Terrain.randomNoise(x - 1, y + 1) + Terrain.randomNoise(x - -1, y - -1);
		@Pc(71) int sides = Terrain.randomNoise(x - 1, y) + Terrain.randomNoise(x + 1, y) + Terrain.randomNoise(x, y - 1) + Terrain.randomNoise(x, y + 1);
		@Pc(76) int center = Terrain.randomNoise(x, y);
		return center / 4 + sides / 8 + corners / 16;
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IBIII)I")
	public static int cosineInterpolate(@OriginalArg(0) int b, @OriginalArg(2) int frequencyReciprocal, @OriginalArg(3) int angle, @OriginalArg(4) int a) {
		@Pc(16) int cosine = 65536 - TextureCombineSource.COSINE[angle * 8192 / frequencyReciprocal] >> 1;
		return (cosine * b >> 16) + (a * (65536 - cosine) >> 16);
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(IIB)I")
	public static int randomNoise(@OriginalArg(0) int x, @OriginalArg(1) int y) {
		@Pc(10) int seed = x + y * 57;
		@Pc(16) int n = seed ^ seed << 13;
		@Pc(38) int value = n * (n * 15731 * n + 789221) + 1376312589 & Integer.MAX_VALUE;
		return value >> 19 & 0xFF;
	}

	@OriginalMember(owner = "client!cda", name = "a", descriptor = "(IIBI)I")
	public static int perlinNoise(@OriginalArg(0) int frequencyReciprocal, @OriginalArg(1) int y, @OriginalArg(3) int x) {
		@Pc(15) int adjX = x / frequencyReciprocal;
		@Pc(21) int angleX = frequencyReciprocal - 1 & x;
		@Pc(25) int adjY = y / frequencyReciprocal;
		@Pc(31) int angleY = y & frequencyReciprocal - 1;
		@Pc(36) int noiseBase = Terrain.randomNoiseWeightedSum(adjX, adjY);
		@Pc(43) int noiseEast = Terrain.randomNoiseWeightedSum(adjX + 1, adjY);
		@Pc(50) int noiseSouth = Terrain.randomNoiseWeightedSum(adjX, adjY + 1);
		@Pc(59) int noiseSouthEast = Terrain.randomNoiseWeightedSum(adjX + 1, adjY + 1);
		@Pc(66) int interpolatedNoiseNorth = Terrain.cosineInterpolate(noiseEast, frequencyReciprocal, angleX, noiseBase);
		@Pc(73) int interpolatedNoiseSouth = Terrain.cosineInterpolate(noiseSouthEast, frequencyReciprocal, angleX, noiseSouth);
		return Terrain.cosineInterpolate(interpolatedNoiseSouth, frequencyReciprocal, angleY, interpolatedNoiseNorth);
	}
}
