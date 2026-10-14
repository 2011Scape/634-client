package com.jagex.game.runetek5

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.config.flo.FloTypeList
import com.jagex.game.runetek5.config.flu.FluType
import com.jagex.game.runetek5.config.flu.FluTypeList
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.scene.MapLoader
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.config.flo.FloType
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.math.ColourUtils
import com.jagex.game.runetek5.utils.ColorUtil
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!sr")
open class Terrain @OriginalMember(owner = "client!sr", name = "<init>", descriptor = "(IIIZLclient!cb;Lclient!qe;)V") constructor(@OriginalArg(0) levels: Int, @OriginalArg(1) regionSizeX: Int, @OriginalArg(2) regionSizeZ: Int, @OriginalArg(3) underwater: Boolean, @OriginalArg(4) floorOverlayTypeList: FloTypeList?, @OriginalArg(5) floorUnderlayTypeList: FluTypeList?) {

	@OriginalMember(owner = "client!sr", name = "c", descriptor = "[[[B")
	@JvmField
	var cameraHeights: Array<Array<ByteArray?>?>? = null

	@OriginalMember(owner = "client!sr", name = "A", descriptor = "[I")
	private val tileSizeDeltasZ: IntArray = intArrayOf(0, 0, 0, 256, 512, 512, 512, 256, 256, 384, 128, 128, 256)

	@OriginalMember(owner = "client!sr", name = "w", descriptor = "[I")
	private val tileSizeDeltasX: IntArray = intArrayOf(0, 256, 512, 512, 512, 256, 0, 0, 128, 256, 128, 384, 256)

	@OriginalMember(owner = "client!sr", name = "h", descriptor = "Z")
	@JvmField
	val underwater: Boolean

	@OriginalMember(owner = "client!sr", name = "r", descriptor = "Lclient!cb;")
	private val floorOverlayTypeList: FloTypeList?

	@OriginalMember(owner = "client!sr", name = "s", descriptor = "Lclient!qe;")
	private val underlayTypeList: FluTypeList?

	@OriginalMember(owner = "client!sr", name = "p", descriptor = "I")
	@JvmField
	val regionSizeX: Int

	@OriginalMember(owner = "client!sr", name = "C", descriptor = "I")
	@JvmField
	val levels: Int

	@OriginalMember(owner = "client!sr", name = "m", descriptor = "I")
	@JvmField
	val regionSizeZ: Int

	@OriginalMember(owner = "client!sr", name = "u", descriptor = "[[[I")
	@JvmField
	val tileHeights: Array<Array<IntArray?>?>

	@OriginalMember(owner = "client!sr", name = "g", descriptor = "[[[B")
	@JvmField
	var occluderFlags: Array<Array<ByteArray?>?>?

	@OriginalMember(owner = "client!sr", name = "l", descriptor = "[[[B")
	private val underlayIds: Array<Array<ByteArray?>?>

	@OriginalMember(owner = "client!sr", name = "B", descriptor = "[[[B")
	private val overlayRotations: Array<Array<ByteArray?>?>

	@OriginalMember(owner = "client!sr", name = "y", descriptor = "[[[B")
	private val tileShapes: Array<Array<ByteArray?>?>

	@OriginalMember(owner = "client!sr", name = "v", descriptor = "[[[B")
	private val overlayIds: Array<Array<ByteArray?>?>

	// Not part of the original client: the floor ids, kept after the build frees its own (floorIds)
	private val keptUnderlayIds: Array<Array<ByteArray?>?> = arrayOfNulls(4)
	private val keptOverlayIds: Array<Array<ByteArray?>?> = arrayOfNulls(4)

	/**
	 * Not part of the original client: tile ([x], [z]) of [level]'s floor underlay id (high byte) and
	 * overlay id (low byte), 0 for none or off the map; for 634HD's authoring tools and tile overrides.
	 */
	fun floorIds(level: Int, x: Int, z: Int): Int {
		val underlays: Array<ByteArray?> = this.underlayIds.getOrNull(level) ?: this.keptUnderlayIds.getOrNull(level) ?: return 0
		val overlays: Array<ByteArray?> = this.overlayIds.getOrNull(level) ?: this.keptOverlayIds.getOrNull(level) ?: return 0
		val underlayRow: ByteArray = underlays.getOrNull(x) ?: return 0
		val overlayRow: ByteArray = overlays.getOrNull(x) ?: return 0
		if (z < 0 || z >= underlayRow.size || z >= overlayRow.size) {
			return 0
		}
		return ((underlayRow[z].toInt() and 0xFF) shl 8) or (overlayRow[z].toInt() and 0xFF)
	}

	init {
		this.underwater = underwater
		this.floorOverlayTypeList = floorOverlayTypeList
		this.underlayTypeList = floorUnderlayTypeList
		this.regionSizeX = regionSizeX
		this.levels = levels
		this.regionSizeZ = regionSizeZ
		this.tileHeights = Array<Array<IntArray?>?>(this.levels) { Array<IntArray?>(this.regionSizeX + 1) { IntArray(this.regionSizeZ + 1) } }
		this.occluderFlags = Array<Array<ByteArray?>?>(this.levels) { Array<ByteArray?>(this.regionSizeX + 1) { ByteArray(this.regionSizeZ + 1) } }
		this.underlayIds = Array<Array<ByteArray?>?>(this.levels) { Array<ByteArray?>(this.regionSizeX) { ByteArray(this.regionSizeZ) } }
		this.overlayRotations = Array<Array<ByteArray?>?>(this.levels) { Array<ByteArray?>(this.regionSizeX) { ByteArray(this.regionSizeZ) } }
		this.tileShapes = Array<Array<ByteArray?>?>(this.levels) { Array<ByteArray?>(this.regionSizeX) { ByteArray(this.regionSizeZ) } }
		this.overlayIds = Array<Array<ByteArray?>?>(this.levels) { Array<ByteArray?>(this.regionSizeX) { ByteArray(this.regionSizeZ) } }
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II[[BI[[BILclient!vd;Lclient!hk;[ZI[[BIILclient!ha;)V")
	private fun calculateOverlayDisplay(@OriginalArg(0) x: Int, @OriginalArg(1) sizeZ: Int, @OriginalArg(2) levelShapes: Array<ByteArray?>?, @OriginalArg(3) z: Int, @OriginalArg(4) levelRotations: Array<ByteArray?>?, @OriginalArg(6) spotAnimType: FloType?, @OriginalArg(7) floorUnderlayType: FluType?, @OriginalArg(8) hasFacesOn: BooleanArray?, @OriginalArg(9) tileRotation: Int, @OriginalArg(10) levelOverlayIds: Array<ByteArray?>?, @OriginalArg(11) sizeX: Int, @OriginalArg(12) pathShape: Int, @OriginalArg(13) toolkit: Toolkit?) {
		@Pc(18) val blendingAttributes: BooleanArray? = if (spotAnimType != null && spotAnimType.blendsWithUnderlay) Terrain.BLENDING_SHAPE_EDGE_FACES[pathShape] else Terrain.BLENDED_SHAPE_EDGE_FACES[pathShape]
		@Pc(37) var local37: Int
		@Pc(47) var spotAnimType2: FloType?
		@Pc(65) var shape: Byte
		@Pc(81) var rotation: Int
		@Pc(86) var local86: Int
		if (z > 0) {
			if (x > 0) {
				val neighbourOverlayId: Int = levelOverlayIds!![x - 1]!![z - 1].toInt() and 0xFF
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
					if (spotAnimType2!!.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes!![x - 1]!![z - 1]
						rotation = (levelRotations!![x - 1]!![z - 1].toInt() * 2 + 4) and 0x7
						val neighbourColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][rotation]) {
							Terrain.overlayTileColours[0] = spotAnimType2.tileColorHsl
							Terrain.overlayRenderColours[0] = neighbourColour
							Terrain.overlayTextureIds[0] = spotAnimType2.texture
							Terrain.overlayTextureScales[0] = spotAnimType2.textureScale
							Terrain.overlaySlots[0] = spotAnimType2.slot
							Terrain.overlayNeighbourMasks[0] = 256
						}
					}
				}
			}
			if (sizeX - 1 > x) {
				val neighbourOverlayId: Int = levelOverlayIds!![x + 1]!![z - 1].toInt() and 0xFF
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
					if (spotAnimType2!!.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes!![x + 1]!![z - 1]
						rotation = (levelRotations!![x + 1]!![z - 1].toInt() * 2 + 6) and 0x7
						val neighbourColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][rotation]) {
							Terrain.overlayTileColours[2] = spotAnimType2.tileColorHsl
							Terrain.overlayRenderColours[2] = neighbourColour
							Terrain.overlayTextureIds[2] = spotAnimType2.texture
							Terrain.overlayTextureScales[2] = spotAnimType2.textureScale
							Terrain.overlaySlots[2] = spotAnimType2.slot
							Terrain.overlayNeighbourMasks[2] = 512
						}
					}
				}
			}
		}
		if (z < sizeZ - 1) {
			if (x > 0) {
				val neighbourOverlayId: Int = levelOverlayIds!![x - 1]!![z + 1].toInt() and 0xFF
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
					if (spotAnimType2!!.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes!![x - 1]!![z + 1]
						rotation = (levelRotations!![x - 1]!![z + 1].toInt() * 2 + 2) and 0x7
						val neighbourColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][rotation]) {
							Terrain.overlayTileColours[6] = spotAnimType2.tileColorHsl
							Terrain.overlayRenderColours[6] = neighbourColour
							Terrain.overlayTextureIds[6] = spotAnimType2.texture
							Terrain.overlayTextureScales[6] = spotAnimType2.textureScale
							Terrain.overlaySlots[6] = spotAnimType2.slot
							Terrain.overlayNeighbourMasks[6] = 64
						}
					}
				}
			}
			if (x < sizeX - 1) {
				val neighbourOverlayId: Int = levelOverlayIds!![x + 1]!![z + 1].toInt() and 0xFF
				if (neighbourOverlayId > 0) {
					spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
					if (spotAnimType2!!.tileColorHsl != -1 && spotAnimType2.blendsWithUnderlay) {
						shape = levelShapes!![x + 1]!![z + 1]
						rotation = (levelRotations!![x + 1]!![z + 1].toInt() * 2) and 0x7
						val neighbourColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][rotation]) {
							Terrain.overlayTileColours[4] = spotAnimType2.tileColorHsl
							Terrain.overlayRenderColours[4] = neighbourColour
							Terrain.overlayTextureIds[4] = spotAnimType2.texture
							Terrain.overlayTextureScales[4] = spotAnimType2.textureScale
							Terrain.overlaySlots[4] = spotAnimType2.slot
							Terrain.overlayNeighbourMasks[4] = 128
						}
					}
				}
			}
		}
		@Pc(492) var neighbourVertex: Int
		@Pc(497) var renderColour: Int
		@Pc(499) var step: Int
		@Pc(481) var neighbourRotation: Byte
		if (z > 0) {
			val neighbourOverlayId: Int = levelOverlayIds!![x]!![z - 1].toInt() and 0xFF
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
				if (spotAnimType2!!.tileColorHsl != -1) {
					shape = levelShapes!![x]!![z - 1]
					neighbourRotation = levelRotations!![x]!![z - 1]
					if (spotAnimType2.blendsWithUnderlay) {
						var overlayVertex: Int = 2
						neighbourVertex = neighbourRotation.toInt() * 2 + 4
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						step = 0
						while (step < 3) {
							overlayVertex = overlayVertex and 0x7
							neighbourVertex = neighbourVertex and 0x7
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][neighbourVertex] && spotAnimType2.slot >= Terrain.overlaySlots[overlayVertex]) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl
								Terrain.overlayRenderColours[overlayVertex] = renderColour
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] = Terrain.overlayNeighbourMasks[overlayVertex] or 0x20
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 32
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot
							}
							neighbourVertex++
							overlayVertex--
							step++
						}
						if (!blendingAttributes!![tileRotation and 0x3]) {
							hasFacesOn!![0] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 2) and 0x3]
						}
					} else if (!blendingAttributes!![tileRotation and 0x3]) {
						hasFacesOn!![0] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 2) and 0x3]
					}
				}
			}
		}
		if (sizeZ - 1 > z) {
			val neighbourOverlayId: Int = levelOverlayIds!![x]!![z + 1].toInt() and 0xFF
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
				if (spotAnimType2!!.tileColorHsl != -1) {
					shape = levelShapes!![x]!![z + 1]
					neighbourRotation = levelRotations!![x]!![z + 1]
					if (spotAnimType2.blendsWithUnderlay) {
						var overlayVertex: Int = 4
						neighbourVertex = neighbourRotation.toInt() * 2 + 2
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						step = 0
						while (step < 3) {
							overlayVertex = overlayVertex and 0x7
							neighbourVertex = neighbourVertex and 0x7
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][neighbourVertex] && Terrain.overlaySlots[overlayVertex] <= spotAnimType2.slot) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl
								Terrain.overlayRenderColours[overlayVertex] = renderColour
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] = Terrain.overlayNeighbourMasks[overlayVertex] or 0x10
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 16
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot
							}
							neighbourVertex--
							overlayVertex++
							step++
						}
						if (!blendingAttributes!![(tileRotation + 2) and 0x3]) {
							neighbourRotation--
							hasFacesOn!![2] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape.toInt()]!![neighbourRotation.toInt() and 0x3]
						}
					} else if (!blendingAttributes!![(tileRotation + 2) and 0x3]) {
						hasFacesOn!![2] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape.toInt()]!![neighbourRotation.toInt() and 0x3]
					}
				}
			}
		}
		if (x > 0) {
			val neighbourOverlayId: Int = levelOverlayIds!![x - 1]!![z].toInt() and 0xFF
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
				if (spotAnimType2!!.tileColorHsl != -1) {
					shape = levelShapes!![x - 1]!![z]
					neighbourRotation = levelRotations!![x - 1]!![z]
					if (spotAnimType2.blendsWithUnderlay) {
						var overlayVertex: Int = 6
						neighbourVertex = neighbourRotation.toInt() * 2 + 4
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						step = 0
						while (step < 3) {
							neighbourVertex = neighbourVertex and 0x7
							overlayVertex = overlayVertex and 0x7
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][neighbourVertex] && Terrain.overlaySlots[overlayVertex] <= spotAnimType2.slot) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl
								Terrain.overlayRenderColours[overlayVertex] = renderColour
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] = Terrain.overlayNeighbourMasks[overlayVertex] or 0x8
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 8
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot
							}
							overlayVertex++
							neighbourVertex--
							step++
						}
						if (!blendingAttributes!![(tileRotation + 3) and 0x3]) {
							hasFacesOn!![3] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 1) and 0x3]
						}
					} else if (!blendingAttributes!![(tileRotation + 3) and 0x3]) {
						hasFacesOn!![3] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 1) and 0x3]
					}
				}
			}
		}
		if (x < sizeX - 1) {
			val neighbourOverlayId: Int = levelOverlayIds!![x + 1]!![z].toInt() and 0xFF
			if (neighbourOverlayId > 0) {
				spotAnimType2 = this.floorOverlayTypeList!!.list(neighbourOverlayId - 1)
				if (spotAnimType2!!.tileColorHsl != -1) {
					shape = levelShapes!![x + 1]!![z]
					neighbourRotation = levelRotations!![x + 1]!![z]
					if (spotAnimType2.blendsWithUnderlay) {
						var overlayVertex: Int = 4
						neighbourVertex = neighbourRotation.toInt() * 2 + 6
						renderColour = FloType.getMinimapColorHsl(toolkit, spotAnimType2)
						step = 0
						while (step < 3) {
							overlayVertex = overlayVertex and 0x7
							neighbourVertex = neighbourVertex and 0x7
							if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[shape.toInt()][neighbourVertex] && spotAnimType2.slot >= Terrain.overlaySlots[overlayVertex]) {
								Terrain.overlayTileColours[overlayVertex] = spotAnimType2.tileColorHsl
								Terrain.overlayRenderColours[overlayVertex] = renderColour
								Terrain.overlayTextureIds[overlayVertex] = spotAnimType2.texture
								Terrain.overlayTextureScales[overlayVertex] = spotAnimType2.textureScale
								if (spotAnimType2.slot == Terrain.overlaySlots[overlayVertex]) {
									Terrain.overlayNeighbourMasks[overlayVertex] = Terrain.overlayNeighbourMasks[overlayVertex] or 0x4
								} else {
									Terrain.overlayNeighbourMasks[overlayVertex] = 4
								}
								Terrain.overlaySlots[overlayVertex] = spotAnimType2.slot
							}
							neighbourVertex++
							overlayVertex--
							step++
						}
						if (!blendingAttributes!![(tileRotation + 1) and 0x3]) {
							hasFacesOn!![1] = Terrain.BLENDING_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 3) and 0x3]
						}
					} else if (!blendingAttributes!![(tileRotation + 1) and 0x3]) {
						hasFacesOn!![1] = Terrain.BLENDED_SHAPE_EDGE_FACES[shape.toInt()]!![(neighbourRotation.toInt() + 3) and 0x3]
					}
				}
			}
		}
		if (spotAnimType == null) {
			return
		}
		val overlayColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType)
		if (!spotAnimType.blendsWithUnderlay) {
			return
		}
		@Pc(1198) var i: Int = 0
		while (i < 8) {
			@Pc(1209) val signedRotation: Int = (i - tileRotation * 2) and 0x7
			if (MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][i] && spotAnimType.slot >= Terrain.overlaySlots[signedRotation]) {
				Terrain.overlayTileColours[signedRotation] = spotAnimType.tileColorHsl
				Terrain.overlayRenderColours[signedRotation] = overlayColour
				Terrain.overlayTextureIds[signedRotation] = spotAnimType.texture
				Terrain.overlayTextureScales[signedRotation] = spotAnimType.textureScale
				if (Terrain.overlaySlots[signedRotation] == spotAnimType.slot) {
					Terrain.overlayNeighbourMasks[signedRotation] = Terrain.overlayNeighbourMasks[signedRotation] or 0x2
				} else {
					Terrain.overlayNeighbourMasks[signedRotation] = 2
				}
				Terrain.overlaySlots[signedRotation] = spotAnimType.slot
			}
			i++
		}
		return
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!s;Lclient!s;ILclient!s;[[ILclient!ha;I)V")
	private fun addBlendedTiles(@OriginalArg(0) ground: Ground?, @OriginalArg(1) ground2: Ground?, @OriginalArg(3) ground3: Ground?, @OriginalArg(4) floorColours: Array<IntArray?>?, @OriginalArg(5) toolkit: Toolkit?, @OriginalArg(6) level: Int) {
		@Pc(10) val levelShapes: Array<ByteArray?>? = this.tileShapes[level]
		@Pc(21) val levelRotations: Array<ByteArray?>? = this.overlayRotations[level]
		@Pc(26) val levelUnderlayIds: Array<ByteArray?>? = this.underlayIds[level]
		@Pc(31) val levelOverlayIds: Array<ByteArray?>? = this.overlayIds[level]
		@Pc(33) var i: Int = 0
		while (i < this.regionSizeX) {
			@Pc(47) val nextX: Int = if (i >= this.regionSizeX - 1) i else i + 1
			@Pc(49) var j: Int = 0
			while (j < this.regionSizeZ) {
				@Pc(67) val nextZ: Int = if (this.regionSizeZ - 1 > j) j + 1 else j
				if (Minimap.visibleFromLevel == -1 || SettingsBits.isVisibleFromLevel(j, i, Minimap.visibleFromLevel, level)) {
					@Pc(82) var hasShadows: Boolean = false
					@Pc(84) var overlaySupportsBlending: Boolean = false
					@Pc(87) val hasFacesOn: BooleanArray = BooleanArray(4)
					@Pc(93) var pathShape: Int = levelShapes!![i]!![j].toInt()
					@Pc(99) var tileRotation: Int = levelRotations!![i]!![j].toInt()
					@Pc(107) val overlayId: Int = levelOverlayIds!![i]!![j].toInt() and 0xFF
					@Pc(115) val underlayId: Int = levelUnderlayIds!![i]!![j].toInt() and 0xFF
					@Pc(123) var underlayIdB: Int = levelUnderlayIds[i]!![nextZ].toInt() and 0xFF
					@Pc(131) var underlayIdC: Int = levelUnderlayIds[nextX]!![nextZ].toInt() and 0xFF
					@Pc(139) var underlayIdD: Int = levelUnderlayIds[nextX]!![j].toInt() and 0xFF
					if (overlayId != 0 || underlayId != 0) {
						@Pc(161) var spotAnimType: FloType? = if (overlayId == 0) null else this.floorOverlayTypeList!!.list(overlayId - 1)
						@Pc(176) val floorUnderlayType: FluType? = if (underlayId == 0) null else this.underlayTypeList!!.list(underlayId - 1)
						if (pathShape == 0 && spotAnimType == null) {
							pathShape = 12
						}
						@Pc(184) var spotAnimType2: FloType? = spotAnimType
						if (spotAnimType != null) {
							if (spotAnimType.tileColorHsl == -1 && spotAnimType.minimapColorHsl == -1) {
								spotAnimType2 = spotAnimType
								spotAnimType = null
							} else if (floorUnderlayType != null && pathShape != 0) {
								overlaySupportsBlending = spotAnimType.blendsWithUnderlay
							}
						}
						@Pc(292) var local292: Int
						@Pc(322) var local322: Int
						@Pc(372) var local372: Int
						@Pc(385) var local385: Int
						if ((pathShape == 0 || pathShape == 12) && i > 0 && j > 0 && i < this.regionSizeX && j < this.regionSizeZ) {
							@Pc(258) var underlayNorthEast: Int = if (levelUnderlayIds[nextX]!![nextZ].toInt() == underlayId) 1 else -1
							@Pc(273) var underlaySouthEast: Int = if (levelUnderlayIds[nextX]!![j - 1].toInt() == underlayId) 1 else -1
							var underlaySouthWest: Int = if (underlayId == levelUnderlayIds[i - 1]!![j - 1].toInt()) 1 else -1
							if (levelUnderlayIds[i]!![j - 1].toInt() == underlayId) {
								underlaySouthWest++
								underlaySouthEast++
							} else {
								underlaySouthEast--
								underlaySouthWest--
							}
							var underlayNorthWest: Int = if (levelUnderlayIds[i - 1]!![nextZ].toInt() == underlayId) 1 else -1
							if (underlayId == levelUnderlayIds[nextX]!![j].toInt()) {
								underlaySouthEast++
								underlayNorthEast++
							} else {
								underlaySouthEast--
								underlayNorthEast--
							}
							if (levelUnderlayIds[i]!![nextZ].toInt() == underlayId) {
								underlayNorthEast++
								underlayNorthWest++
							} else {
								underlayNorthWest--
								underlayNorthEast--
							}
							if (underlayId == levelUnderlayIds[i - 1]!![j].toInt()) {
								underlayNorthWest++
								underlaySouthWest++
							} else {
								underlayNorthWest--
								underlaySouthWest--
							}
							var southWestNorthEastDiff: Int = underlaySouthWest - underlayNorthEast
							if (southWestNorthEastDiff < 0) {
								southWestNorthEastDiff = -southWestNorthEastDiff
							}
							var southEastNorthWestDiff: Int = underlaySouthEast - underlayNorthWest
							if (southEastNorthWestDiff < 0) {
								southEastNorthWestDiff = -southEastNorthWestDiff
							}
							if (southWestNorthEastDiff == southEastNorthWestDiff) {
								southWestNorthEastDiff = ground2!!.getHeight(j, i) - ground2.getHeight(nextZ, nextX)
								if (southWestNorthEastDiff < 0) {
									southWestNorthEastDiff = -southWestNorthEastDiff
								}
								southEastNorthWestDiff = ground2.getHeight(j, nextX) - ground2.getHeight(nextZ, i)
								if (southEastNorthWestDiff < 0) {
									southEastNorthWestDiff = -southEastNorthWestDiff
								}
							}
							tileRotation = if (southWestNorthEastDiff >= southEastNorthWestDiff) 0 else 1
						}
						var overlayVertex: Int = 0
						while (overlayVertex < 13) {
							Terrain.overlaySlots[overlayVertex] = -1
							Terrain.overlayNeighbourMasks[overlayVertex] = 1
							overlayVertex++
						}
						@Pc(475) val blendingAttributes: BooleanArray? = if (spotAnimType != null && spotAnimType.blendsWithUnderlay) Terrain.BLENDING_SHAPE_EDGE_FACES[pathShape] else Terrain.BLENDED_SHAPE_EDGE_FACES[pathShape]
						this.calculateOverlayDisplay(i, this.regionSizeZ, levelShapes, j, levelRotations, spotAnimType, floorUnderlayType, hasFacesOn, tileRotation, levelOverlayIds, this.regionSizeX, pathShape, toolkit)
						@Pc(508) var overlayHasMultiColour: Boolean = spotAnimType != null && spotAnimType.minimapColorHsl != spotAnimType.tileColorHsl
						if (!overlayHasMultiColour) {
							var overlayVertex2: Int = 0
							while (overlayVertex2 < 8) {
								if (Terrain.overlaySlots[overlayVertex2] >= 0 && Terrain.overlayRenderColours[overlayVertex2] != Terrain.overlayTileColours[overlayVertex2]) {
									overlayHasMultiColour = true
									break
								}
								overlayVertex2++
							}
						}
						if (!blendingAttributes!![(tileRotation + 1) and 0x3]) {
							hasFacesOn[1] = Terrain.booleanOr(hasFacesOn[1], (Terrain.overlayNeighbourMasks[2] and Terrain.overlayNeighbourMasks[4]) == 0)
						}
						if (!blendingAttributes[(tileRotation + 3) and 0x3]) {
							hasFacesOn[3] = Terrain.booleanOr(hasFacesOn[3], (Terrain.overlayNeighbourMasks[0] and Terrain.overlayNeighbourMasks[6]) == 0)
						}
						if (!blendingAttributes[tileRotation and 0x3]) {
							hasFacesOn[0] = Terrain.booleanOr(hasFacesOn[0], (Terrain.overlayNeighbourMasks[0] and Terrain.overlayNeighbourMasks[2]) == 0)
						}
						if (!blendingAttributes[(tileRotation + 2) and 0x3]) {
							hasFacesOn[2] = Terrain.booleanOr(hasFacesOn[2], (Terrain.overlayNeighbourMasks[4] and Terrain.overlayNeighbourMasks[6]) == 0)
						}
						if (!overlaySupportsBlending && (pathShape == 0 || pathShape == 12)) {
							if (hasFacesOn[0] && !hasFacesOn[1] && !hasFacesOn[2] && hasFacesOn[3]) {
								hasFacesOn[3] = false
								hasFacesOn[0] = false
								tileRotation = 0
								pathShape = if (pathShape == 0) 13 else 14
							} else if (hasFacesOn[0] && hasFacesOn[1] && !hasFacesOn[2] && !hasFacesOn[3]) {
								pathShape = if (pathShape == 0) 13 else 14
								hasFacesOn[1] = false
								hasFacesOn[0] = false
								tileRotation = 3
							} else if (!hasFacesOn[0] && hasFacesOn[1] && hasFacesOn[2] && !hasFacesOn[3]) {
								tileRotation = 2
								pathShape = if (pathShape == 0) 13 else 14
								hasFacesOn[2] = false
								hasFacesOn[1] = false
							} else if (!hasFacesOn[0] && !hasFacesOn[1] && hasFacesOn[2] && hasFacesOn[3]) {
								pathShape = if (pathShape == 0) 13 else 14
								hasFacesOn[3] = false
								hasFacesOn[2] = false
								tileRotation = 1
							}
						}
						@Pc(819) val unblendable: Boolean = !overlaySupportsBlending && !hasFacesOn[0] && !hasFacesOn[2] && !hasFacesOn[1] && !hasFacesOn[3]
						@Pc(821) var overlayFaceCounts: IntArray? = null
						@Pc(837) val shapeVertexA: IntArray?
						@Pc(845) val shapeVertexB: IntArray?
						@Pc(849) val shapeVertexC: IntArray?
						val underlayFaceCount: Int
						val overlayFaceCount: Int
						if (unblendable) {
							shapeVertexA = MapLoader.SHAPE_VERTEX_A[pathShape]
							shapeVertexB = MapLoader.SHAPE_VERTEX_B[pathShape]
							underlayFaceCount = if (floorUnderlayType == null) 0 else MapLoader.UNDERLAY_FACE_COUNT[pathShape]
							shapeVertexC = MapLoader.SHAPE_VERTEX_C[pathShape]
							overlayFaceCount = if (spotAnimType == null) 0 else Terrain.faceVertices[pathShape]
						} else if (overlaySupportsBlending) {
							overlayFaceCount = if (spotAnimType == null) 0 else Terrain.OVERLAY_FACE_COUNT_BLENDED[pathShape]
							shapeVertexA = Terrain.BLENDING_SHAPE_VERTEX_A[pathShape]
							overlayFaceCounts = Terrain.BLENDING_FACE_MAPPING[pathShape]
							shapeVertexB = MapLoader.BLENDING_SHAPE_VERTEX_B[pathShape]
							shapeVertexC = Terrain.BLENDING_SHAPE_VERTEX_C[pathShape]
							underlayFaceCount = if (floorUnderlayType == null) 0 else Terrain.UNDERLAY_FACE_COUNT_BLENDED[pathShape]
						} else {
							underlayFaceCount = if (floorUnderlayType == null) 0 else Terrain.UNDERLAY_FACE_COUNTS[pathShape]
							overlayFaceCount = if (spotAnimType == null) 0 else Terrain.OVERLAY_FACE_COUNTS[pathShape]
							shapeVertexB = MapLoader.BLENDED_SHAPE_VERTEX_B[pathShape]
							shapeVertexC = Terrain.BLENDED_SHAPE_VERTEX_C[pathShape]
							shapeVertexA = MapLoader.BLENDED_SHAPE_VERTEX_A[pathShape]
							overlayFaceCounts = MapLoader.BLENDED_FACE_MAPPING[pathShape]
						}
						@Pc(923) var floorFaceCount: Int = overlayFaceCount + underlayFaceCount
						if (floorFaceCount <= 0) {
							Scene.loadTiles(level, i, j)
						} else {
							if (hasFacesOn[0]) {
								floorFaceCount++
							}
							if (hasFacesOn[2]) {
								floorFaceCount++
							}
							if (hasFacesOn[1]) {
								floorFaceCount++
							}
							if (hasFacesOn[3]) {
								floorFaceCount++
							}
							@Pc(953) var overlayTotalFaces: Int = 0
							@Pc(955) var vertexIndex: Int = 0
							@Pc(959) val vertexCount: Int = floorFaceCount * 3
							@Pc(966) val blendingColours: IntArray? = if (overlayHasMultiColour) IntArray(vertexCount) else null
							@Pc(969) val offsetsX: IntArray = IntArray(vertexCount)
							@Pc(972) val offsetsZ: IntArray = IntArray(vertexCount)
							@Pc(975) val mainColours: IntArray = IntArray(vertexCount)
							@Pc(978) val textures: IntArray = IntArray(vertexCount)
							@Pc(981) val textureScales: IntArray = IntArray(vertexCount)
							@Pc(988) val nextLevelHeightDiffs: IntArray? = if (ground == null) null else IntArray(vertexCount)
							@Pc(997) val localLevelHeightDiffs: IntArray? = if (ground == null && ground3 == null) null else IntArray(vertexCount)
							@Pc(999) var overlayHslColour: Int = -1
							@Pc(1001) var overlayTextureId: Int = -1
							@Pc(1003) var overlayTextureScale: Int = 256
							@Pc(1109) var faceVertexCount: Byte
							@Pc(1021) var local1021: Int
							@Pc(1023) var local1023: Int
							@Pc(1255) var local1255: Int
							@Pc(1261) var local1261: Int
							@Pc(1270) var local1270: Int
							@Pc(1275) var local1275: Int
							@Pc(1292) var local1292: Int
							@Pc(1280) var local1280: Int
							@Pc(1297) var local1297: Int
							@Pc(1348) var local1348: Int
							@Pc(1354) var local1354: Int
							if (spotAnimType != null) {
								overlayTextureId = spotAnimType.texture
								overlayTextureScale = spotAnimType.textureScale
								overlayHslColour = spotAnimType.tileColorHsl
								val overlayBlendColour: Int = FloType.getMinimapColorHsl(toolkit, spotAnimType)
								var overlayFace: Int = 0
								while (overlayFace < overlayFaceCount) {
									if (hasFacesOn[(-tileRotation) and 0x3] && overlayFaceCounts!![0] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 1
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 1
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
										faceVertexCount = 6
									} else if (hasFacesOn[(2 - tileRotation) and 0x3] && overlayFaceCounts!![2] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 5
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 5
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 6
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
									} else if (hasFacesOn[(1 - tileRotation) and 0x3] && overlayTotalFaces == overlayFaceCounts!![1]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 3
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 3
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 6
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
									} else if (hasFacesOn[(3 - tileRotation) and 0x3] && overlayFaceCounts!![3] == overlayTotalFaces) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 7
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 7
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 6
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
									} else {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = shapeVertexB!![overlayTotalFaces]
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										faceVertexCount = 3
									}
									var faceVertex: Int = 0
									while (faceVertex < faceVertexCount) {
										val tileVertex: Int = Terrain.overlayFaceVertices[faceVertex]
										val rotatedVertex: Int = (tileVertex - tileRotation * 2) and 0x7
										val deltaX: Int = this.tileSizeDeltasX[tileVertex]
										val deltaZ: Int = this.tileSizeDeltasZ[tileVertex]
										val vertexOffsetX: Int
										val vertexOffsetZ: Int
										if (tileRotation == 1) {
											vertexOffsetZ = 512 - deltaX
											vertexOffsetX = deltaZ
										} else if (tileRotation == 2) {
											vertexOffsetX = 512 - deltaX
											vertexOffsetZ = 512 - deltaZ
										} else if (tileRotation == 3) {
											vertexOffsetZ = deltaX
											vertexOffsetX = 512 - deltaZ
										} else {
											vertexOffsetZ = deltaZ
											vertexOffsetX = deltaX
										}
										offsetsX[vertexIndex] = vertexOffsetX
										offsetsZ[vertexIndex] = vertexOffsetZ
										if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
											val worldX: Int = vertexOffsetX + (i shl 9)
											val worldZ: Int = vertexOffsetZ + (j shl 9)
											nextLevelHeightDiffs[vertexIndex] = ground!!.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
										}
										if (localLevelHeightDiffs != null) {
											if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												val worldX: Int = (i shl 9) + vertexOffsetX
												val worldZ: Int = vertexOffsetZ + (j shl 9)
												localLevelHeightDiffs[vertexIndex] = ground2!!.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ)
											} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												val worldX: Int = (i shl 9) + vertexOffsetX
												val worldZ: Int = (j shl 9) + vertexOffsetZ
												localLevelHeightDiffs[vertexIndex] = ground3.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
											}
										}
										if (tileVertex < 8 && Terrain.overlaySlots[rotatedVertex] > spotAnimType.slot) {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = Terrain.overlayRenderColours[rotatedVertex]
											}
											textureScales[vertexIndex] = Terrain.overlayTextureScales[rotatedVertex]
											textures[vertexIndex] = Terrain.overlayTextureIds[rotatedVertex]
											mainColours[vertexIndex] = Terrain.overlayTileColours[rotatedVertex]
										} else {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = overlayBlendColour
											}
											textures[vertexIndex] = spotAnimType.texture
											textureScales[vertexIndex] = spotAnimType.textureScale
											mainColours[vertexIndex] = overlayHslColour
										}
										vertexIndex++
										faceVertex++
									}
									overlayTotalFaces++
									overlayFace++
								}
								if (!this.underwater && level == 0) {
									Terrain.addWater(i, j, spotAnimType.waterColor, spotAnimType.waterFogDepth * 8, spotAnimType.waterIntensity)
								}
								if (pathShape != 12 && spotAnimType.tileColorHsl != -1 && spotAnimType.shadowed) {
									hasShadows = true
								}
							} else if (unblendable) {
								overlayTotalFaces = Terrain.faceVertices[pathShape]
							} else if (overlaySupportsBlending) {
								overlayTotalFaces = Terrain.OVERLAY_FACE_COUNT_BLENDED[pathShape]
							} else {
								overlayTotalFaces = Terrain.OVERLAY_FACE_COUNTS[pathShape]
							}
							if (floorUnderlayType != null) {
								if (underlayIdD == 0) {
									underlayIdD = underlayId
								}
								if (underlayIdC == 0) {
									underlayIdC = underlayId
								}
								if (underlayIdB == 0) {
									underlayIdB = underlayId
								}
								@Pc(1600) val floorUnderlayType2: FluType? = this.underlayTypeList!!.list(underlayId - 1)
								@Pc(1608) val floorUnderlayType3: FluType? = this.underlayTypeList.list(underlayIdB - 1)
								@Pc(1616) val floorUnderlayType4: FluType? = this.underlayTypeList.list(underlayIdC - 1)
								@Pc(1624) val floorUnderlayType5: FluType? = this.underlayTypeList.list(underlayIdD - 1)
								var underlayFace: Int = 0
								while (underlayFace < underlayFaceCount) {
									if (hasFacesOn[(-tileRotation) and 0x3] && overlayTotalFaces == overlayFaceCounts!![0]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 1
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 1
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 6
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
									} else if (hasFacesOn[(2 - tileRotation) and 0x3] && overlayTotalFaces == overlayFaceCounts!![2]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 5
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 5
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 6
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
									} else if (hasFacesOn[(1 - tileRotation) and 0x3] && overlayTotalFaces == overlayFaceCounts!![1]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 3
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 3
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
										faceVertexCount = 6
									} else if (hasFacesOn[(3 - tileRotation) and 0x3] && overlayTotalFaces == overlayFaceCounts!![3]) {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = 7
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
										Terrain.overlayFaceVertices[3] = 7
										Terrain.overlayFaceVertices[4] = shapeVertexB!![overlayTotalFaces]
										Terrain.overlayFaceVertices[5] = shapeVertexC[overlayTotalFaces]
										faceVertexCount = 6
									} else {
										Terrain.overlayFaceVertices[0] = shapeVertexA!![overlayTotalFaces]
										Terrain.overlayFaceVertices[1] = shapeVertexB!![overlayTotalFaces]
										faceVertexCount = 3
										Terrain.overlayFaceVertices[2] = shapeVertexC!![overlayTotalFaces]
									}
									overlayTotalFaces++
									var faceVertex: Int = 0
									while (faceVertex < faceVertexCount) {
										val tileVertex: Int = Terrain.overlayFaceVertices[faceVertex]
										val rotatedVertex: Int = (tileVertex - tileRotation * 2) and 0x7
										val deltaX: Int = this.tileSizeDeltasX[tileVertex]
										val deltaZ: Int = this.tileSizeDeltasZ[tileVertex]
										@Pc(1892) val vertexOffsetZ: Int
										val vertexOffsetX: Int
										if (tileRotation == 1) {
											vertexOffsetX = deltaZ
											vertexOffsetZ = 512 - deltaX
										} else if (tileRotation == 2) {
											vertexOffsetZ = 512 - deltaZ
											vertexOffsetX = 512 - deltaX
										} else if (tileRotation == 3) {
											vertexOffsetZ = deltaX
											vertexOffsetX = 512 - deltaZ
										} else {
											vertexOffsetZ = deltaZ
											vertexOffsetX = deltaX
										}
										offsetsX[vertexIndex] = vertexOffsetX
										offsetsZ[vertexIndex] = vertexOffsetZ
										@Pc(1947) var local1947: Int
										@Pc(1954) var local1954: Int
										if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
											val worldX: Int = vertexOffsetX + (i shl 9)
											val worldZ: Int = (j shl 9) + vertexOffsetZ
											nextLevelHeightDiffs[vertexIndex] = ground!!.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
										}
										if (localLevelHeightDiffs != null) {
											if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												val worldX: Int = (i shl 9) + vertexOffsetX
												val worldZ: Int = vertexOffsetZ + (j shl 9)
												localLevelHeightDiffs[vertexIndex] = ground2!!.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ)
											} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												val worldX: Int = (i shl 9) + vertexOffsetX
												val worldZ: Int = vertexOffsetZ + (j shl 9)
												localLevelHeightDiffs[vertexIndex] = ground3.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
											}
										}
										if (tileVertex < 8 && Terrain.overlaySlots[rotatedVertex] >= 0) {
											if (blendingColours != null) {
												blendingColours[vertexIndex] = Terrain.overlayRenderColours[rotatedVertex]
											}
											textureScales[vertexIndex] = Terrain.overlayTextureScales[rotatedVertex]
											textures[vertexIndex] = Terrain.overlayTextureIds[rotatedVertex]
											mainColours[vertexIndex] = Terrain.overlayTileColours[rotatedVertex]
										} else {
											if (overlaySupportsBlending && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[pathShape][tileVertex]) {
												textures[vertexIndex] = overlayTextureId
												textureScales[vertexIndex] = overlayTextureScale
												mainColours[vertexIndex] = overlayHslColour
											} else if (vertexOffsetX == 0 && vertexOffsetZ == 0) {
												mainColours[vertexIndex] = floorColours!![i]!![j]
												textures[vertexIndex] = floorUnderlayType2!!.texture
												textureScales[vertexIndex] = floorUnderlayType2.scale
											} else if (vertexOffsetX == 0 && vertexOffsetZ == 512) {
												mainColours[vertexIndex] = floorColours!![i]!![nextZ]
												textures[vertexIndex] = floorUnderlayType3!!.texture
												textureScales[vertexIndex] = floorUnderlayType3.scale
											} else if (vertexOffsetX == 512 && vertexOffsetZ == 512) {
												mainColours[vertexIndex] = floorColours!![nextX]!![nextZ]
												textures[vertexIndex] = floorUnderlayType4!!.texture
												textureScales[vertexIndex] = floorUnderlayType4.scale
											} else if (vertexOffsetX == 512 && vertexOffsetZ == 0) {
												mainColours[vertexIndex] = floorColours!![nextX]!![j]
												textures[vertexIndex] = floorUnderlayType5!!.texture
												textureScales[vertexIndex] = floorUnderlayType5.scale
											} else {
												if (vertexOffsetX >= 256) {
													if (vertexOffsetZ >= 256) {
														textures[vertexIndex] = floorUnderlayType4!!.texture
														textureScales[vertexIndex] = floorUnderlayType4.scale
													} else {
														textures[vertexIndex] = floorUnderlayType5!!.texture
														textureScales[vertexIndex] = floorUnderlayType5.scale
													}
												} else if (vertexOffsetZ < 256) {
													textures[vertexIndex] = floorUnderlayType2!!.texture
													textureScales[vertexIndex] = floorUnderlayType2.scale
												} else {
													textures[vertexIndex] = floorUnderlayType3!!.texture
													textureScales[vertexIndex] = floorUnderlayType3.scale
												}
												val southColour: Int = ColorUtil.blendHsl(floorColours!![i]!![j], floorColours[nextX]!![j], (vertexOffsetX shl 7) shr 9)
												val northColour: Int = ColorUtil.blendHsl(floorColours[i]!![nextZ], floorColours[nextX]!![nextZ], (vertexOffsetX shl 7) shr 9)
												mainColours[vertexIndex] = ColorUtil.blendHsl(southColour, northColour, (vertexOffsetZ shl 7) shr 9)
											}
											if (blendingColours != null) {
												blendingColours[vertexIndex] = mainColours[vertexIndex]
											}
										}
										vertexIndex++
										faceVertex++
									}
									underlayFace++
								}
								if (pathShape != 0 && floorUnderlayType.shadowed) {
									hasShadows = true
								}
							}
							val southWestHeight: Int = ground2!!.getHeight(j, i)
							val southEastHeight: Int = ground2.getHeight(j, nextX)
							val northEastHeight: Int = ground2.getHeight(nextZ, nextX)
							val northWestHeight: Int = ground2.getHeight(nextZ, i)
							@Pc(2381) val bridge: Boolean = SettingsBits.isBridge(j, i)
							if (bridge && level > 1 || !bridge && level > 0) {
								@Pc(2397) var occludes: Boolean = true
								if (floorUnderlayType != null && !floorUnderlayType.occlude) {
									occludes = false
								} else if (underlayId == 0 && pathShape != 0) {
									occludes = false
								} else if (overlayId > 0 && spotAnimType2 != null && !spotAnimType2.occlude) {
									occludes = false
								}
								if (occludes && southWestHeight == southEastHeight && northEastHeight == southWestHeight && southWestHeight == northWestHeight) {
									this.occluderFlags!![level]!![i]!![j] = (this.occluderFlags!![level]!![i]!![j].toInt() or 0x4).toByte()
								}
							}
							var waterColour: Int = 0
							var waterScale: Int = 0
							var waterIntensity: Int = 0
							if (this.underwater) {
								waterColour = Scene.getWaterColor(i, j)
								waterScale = Scene.getWaterScale(i, j)
								waterIntensity = Scene.getWaterIntensity(i, j)
							}
							ground2.U(i, j, offsetsX, nextLevelHeightDiffs, offsetsZ, localLevelHeightDiffs, mainColours, blendingColours, textures, textureScales, waterColour, waterScale, waterIntensity, hasShadows)
							Scene.loadTiles(level, i, j)
						}
					}
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIIIII)V")
	fun setTileHeights(@OriginalArg(0) x: Int, @OriginalArg(1) length: Int, @OriginalArg(2) z: Int, @OriginalArg(4) width: Int, @OriginalArg(5) level: Int) {
		@Pc(13) var local13: Int
		@Pc(9) var i: Int = z
		while (i < length + z) {
			var tileX: Int = x
			while (tileX < width + x) {
				if (tileX >= 0 && tileX < this.regionSizeX && i >= 0 && this.regionSizeZ > i) {
					this.tileHeights[level]!![tileX]!![i] = if (level <= 0) 0 else this.tileHeights[level - 1]!![tileX]!![i] - 960
				}
				tileX++
			}
			i++
		}
		if (x > 0 && x < this.regionSizeX) {
			var tileZ: Int = z + 1
			while (tileZ < z + length) {
				if (tileZ >= 0 && tileZ < this.regionSizeZ) {
					this.tileHeights[level]!![x]!![tileZ] = this.tileHeights[level]!![x - 1]!![tileZ]
				}
				tileZ++
			}
		}
		if (z > 0 && z < this.regionSizeZ) {
			var tileX: Int = x + 1
			while (tileX < width + x) {
				if (tileX >= 0 && tileX < this.regionSizeX) {
					this.tileHeights[level]!![tileX]!![z] = this.tileHeights[level]!![tileX]!![z - 1]
				}
				tileX++
			}
		}
		if (x < 0 || z < 0 || this.regionSizeX <= x || z >= this.regionSizeZ) {
			return
		}
		if (level != 0) {
			if (x > 0 && this.tileHeights[level - 1]!![x - 1]!![z] != this.tileHeights[level]!![x - 1]!![z]) {
				this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x - 1]!![z]
				return
			}
			if (z > 0 && this.tileHeights[level]!![x]!![z - 1] != this.tileHeights[level - 1]!![x]!![z - 1]) {
				this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x]!![z - 1]
				return
			}
			if (x > 0 && z > 0 && this.tileHeights[level]!![x - 1]!![z - 1] != this.tileHeights[level - 1]!![x - 1]!![z - 1]) {
				this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x - 1]!![z - 1]
				return
			}
			return
		}
		if (x > 0 && this.tileHeights[level]!![x - 1]!![z] != 0) {
			this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x - 1]!![z]
			return
		}
		if (z > 0 && this.tileHeights[level]!![x]!![z - 1] != 0) {
			this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x]!![z - 1]
			return
		}
		if (x > 0 && z > 0 && this.tileHeights[level]!![x - 1]!![z - 1] != 0) {
			this.tileHeights[level]!![x]!![z] = this.tileHeights[level]!![x - 1]!![z - 1]
			return
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II[[I)V")
	fun addHeightOffsets(@OriginalArg(2) heightOffsets: Array<IntArray?>?) {
		@Pc(10) val heights: Array<IntArray?>? = this.tileHeights[0]
		@Pc(12) var x: Int = 0
		while (x < this.regionSizeX + 1) {
			@Pc(16) var z: Int = 0
			while (z < this.regionSizeZ + 1) {
				heights!![x]!![z] += heightOffsets!![x]!![z]
				z++
			}
			x++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!s;BLclient!s;Lclient!ha;)V")
	fun calculateUnderlayPalette(@OriginalArg(0) ground: Ground?, @OriginalArg(2) ground2: Ground?, @OriginalArg(3) toolkit: Toolkit?) {
		@Pc(11) val floorColours: Array<IntArray?> = Array<IntArray?>(this.regionSizeX) { IntArray(this.regionSizeZ) }
		if (Terrain.underlayHues == null || this.regionSizeZ != Terrain.underlayHues!!.size) {
			Terrain.underlayCounts = IntArray(this.regionSizeZ)
			PlayerList.underlaySaturation = IntArray(this.regionSizeZ)
			Terrain.underlayDividers = IntArray(this.regionSizeZ)
			Terrain.underlayHues = IntArray(this.regionSizeZ)
			Terrain.underlayLightness = IntArray(this.regionSizeZ)
		}
		@Pc(48) var local48: Int
		@Pc(44) var i: Int = 0
		while (i < this.levels) {
			var z: Int = 0
			while (z < this.regionSizeZ) {
				Terrain.underlayHues!![z] = 0
				PlayerList.underlaySaturation!![z] = 0
				Terrain.underlayLightness!![z] = 0
				Terrain.underlayDividers!![z] = 0
				Terrain.underlayCounts!![z] = 0
				z++
			}
			@Pc(77) var j: Int = -5
			while (j < this.regionSizeX) {
				@Pc(87) var local87: Int
				@Pc(106) var local106: Int
				@Pc(175) var local175: Int
				@Pc(81) var k: Int = 0
				while (k < this.regionSizeZ) {
					val forwardX: Int = j + 5
					@Pc(155) var local155: Int
					if (forwardX < this.regionSizeX) {
						val addedUnderlayId: Int = this.underlayIds[i]!![forwardX]!![k].toInt() and 0xFF
						if (addedUnderlayId > 0) {
							@Pc(119) val floorUnderlayType: FluType? = this.underlayTypeList!!.list(addedUnderlayId - 1)
							Terrain.underlayHues!![k] += floorUnderlayType!!.hue
							PlayerList.underlaySaturation!![k] += floorUnderlayType.saturation
							Terrain.underlayLightness!![k] += floorUnderlayType.lightness
							Terrain.underlayDividers!![k] += floorUnderlayType.hueMultiplier
							val previousCount: Int = Terrain.underlayCounts!![k]++
						}
					}
					val backwardX: Int = j - 5
					if (backwardX >= 0) {
						val removedUnderlayId: Int = this.underlayIds[i]!![backwardX]!![k].toInt() and 0xFF
						if (removedUnderlayId > 0) {
							@Pc(185) val floorUnderlayType2: FluType? = this.underlayTypeList!!.list(removedUnderlayId - 1)
							Terrain.underlayHues!![k] -= floorUnderlayType2!!.hue
							PlayerList.underlaySaturation!![k] -= floorUnderlayType2.saturation
							Terrain.underlayLightness!![k] -= floorUnderlayType2.lightness
							Terrain.underlayDividers!![k] -= floorUnderlayType2.hueMultiplier
							val previousCount: Int = Terrain.underlayCounts!![k]--
						}
					}
					k++
				}
				if (j >= 0) {
					var hueSum: Int = 0
					var saturationSum: Int = 0
					var lightnessSum: Int = 0
					@Pc(243) var dividerSum: Int = 0
					@Pc(245) var count: Int = 0
					@Pc(247) var m: Int = -5
					while (m < this.regionSizeZ) {
						@Pc(253) val forwardZ: Int = m + 5
						if (this.regionSizeZ > forwardZ) {
							lightnessSum += Terrain.underlayLightness!![forwardZ]
							saturationSum += PlayerList.underlaySaturation!![forwardZ]
							hueSum += Terrain.underlayHues!![forwardZ]
							count += Terrain.underlayCounts!![forwardZ]
							dividerSum += Terrain.underlayDividers!![forwardZ]
						}
						@Pc(295) val backwardZ: Int = m - 5
						if (backwardZ >= 0) {
							dividerSum -= Terrain.underlayDividers!![backwardZ]
							hueSum -= Terrain.underlayHues!![backwardZ]
							lightnessSum -= Terrain.underlayLightness!![backwardZ]
							count -= Terrain.underlayCounts!![backwardZ]
							saturationSum -= PlayerList.underlaySaturation!![backwardZ]
						}
						if (m >= 0 && dividerSum > 0 && count > 0) {
							floorColours[j]!![m] = ColourUtils.packHsl(lightnessSum / count, saturationSum / count, hueSum * 256 / dividerSum)
						}
						m++
					}
				}
				j++
			}
			if (Terrain.blendFloor) {
				this.addBlendedTiles(if (i == 0) ground else null, Scene.aClass65Array1!![i], if (i == 0) ground2 else null, floorColours, toolkit, i)
			} else {
				this.addRegularTile(toolkit, if (i == 0) ground else null, i, floorColours, Scene.aClass65Array1!![i], if (i == 0) ground2 else null)
			}
			// Not part of the original client: the floor ids kept for floorIds (634HD's authoring tools)
			this.keptUnderlayIds[i] = this.underlayIds[i]
			this.keptOverlayIds[i] = this.overlayIds[i]
			this.underlayIds[i] = null
			this.overlayIds[i] = null
			this.tileShapes[i] = null
			this.overlayRotations[i] = null
			i++
		}
		if (!this.underwater) {
			if (MapLoader.hdShadows != 0) {
				Ground.addHdShadow()
			}
			if (Terrain.hdLight) {
				Scene.addHdLight()
			}
		}
		var level: Int = 0
		while (level < this.levels) {
			Scene.aClass65Array1!![level]!!.YA()
			level++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!cea;I[Lclient!fe;IIIB)V")
	fun decodeTilesClient(@OriginalArg(0) packet: Packet?, @OriginalArg(1) chunkX: Int, @OriginalArg(2) collisionMaps: Array<CollisionMap?>?, @OriginalArg(3) baseZ: Int, @OriginalArg(4) baseX: Int, @OriginalArg(5) chunkZ: Int) {
		@Pc(10) var local10: Int
		@Pc(22) var local22: Int
		@Pc(28) var local28: Int
		@Pc(32) var local32: Int
		if (!this.underwater) {
			var level: Int = 0
			while (level < 4) {
				@Pc(16) val collisionMap: CollisionMap? = collisionMaps!![level]
				@Pc(18) var i: Int = 0
				while (i < 64) {
					var localZ: Int = 0
					while (localZ < 64) {
						val tileX: Int = i + chunkX
						val tileZ: Int = chunkZ + localZ
						if (tileX >= 0 && this.regionSizeX > tileX && tileZ >= 0 && tileZ < this.regionSizeZ) {
							collisionMap!!.unflagBlocked(tileZ, tileX)
						}
						localZ++
					}
					i++
				}
				level++
			}
		}
		val absoluteX: Int = chunkX + baseX
		@Pc(91) val absoluteZ: Int = baseZ + chunkZ
		var level: Int = 0
		while (level < this.levels) {
			var localX: Int = 0
			while (localX < 64) {
				var localZ: Int = 0
				while (localZ < 64) {
					this.decodeTile(false, 0, localZ + chunkZ, 0, packet, 0, chunkX + localX, absoluteX + localX, absoluteZ + localZ, level)
					localZ++
				}
				localX++
			}
			level++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(ZIIILclient!cea;IIIIII)V")
	private fun decodeTile(@OriginalArg(0) heightOnly: Boolean, @OriginalArg(1) offsetZ: Int, @OriginalArg(2) z: Int, @OriginalArg(3) rotation: Int, @OriginalArg(4) packet: Packet?, @OriginalArg(5) offsetX: Int, @OriginalArg(7) x: Int, @OriginalArg(8) absoluteX: Int, @OriginalArg(9) absoluteZ: Int, @OriginalArg(10) level: Int) {
		var offsetZ: Int = offsetZ
		var offsetX: Int = offsetX
		if (rotation == 1) {
			offsetZ = 1
		} else if (rotation == 2) {
			offsetZ = 1
			offsetX = 1
		} else if (rotation == 3) {
			offsetX = 1
		}
		@Pc(53) var opcode: Int
		if (x < 0 || this.regionSizeX <= x || z < 0 || z >= this.regionSizeZ) {
			while (true) {
				opcode = packet!!.readUByte()
				if (opcode == 0) {
					break
				}
				if (opcode == 1) {
					packet.readUByte()
					break
				}
				if (opcode <= 49) {
					packet.readUByte()
				}
			}
			return
		}
		if (!this.underwater && !heightOnly) {
			SettingsBits.tileFunctionMasks!![level]!![x]!![z] = 0
		}
		while (true) {
			opcode = packet!!.readUByte()
			if (opcode == 0) {
				if (this.underwater) {
					this.tileHeights[0]!![offsetX + x]!![offsetZ + z] = 0
				} else if (level == 0) {
					this.tileHeights[0]!![offsetX + x]!![z + offsetZ] = (-Terrain.calculateTileHeight(absoluteX + 932731, absoluteZ + 556238) * 8) shl 2
				} else {
					this.tileHeights[level]!![offsetX + x]!![offsetZ + z] = this.tileHeights[level - 1]!![offsetX + x]!![offsetZ + z] - 960
				}
				break
			}
			if (opcode == 1) {
				@Pc(196) var height: Int = packet.readUByte()
				if (this.underwater) {
					this.tileHeights[0]!![x + offsetX]!![z + offsetZ] = (height * 8) shl 2
				} else {
					if (height == 1) {
						height = 0
					}
					if (level == 0) {
						this.tileHeights[0]!![x + offsetX]!![z + offsetZ] = (-height * 8) shl 2
					} else {
						this.tileHeights[level]!![offsetX + x]!![offsetZ + z] = this.tileHeights[level - 1]!![offsetX + x]!![z + offsetZ] - ((height * 8) shl 2)
					}
				}
				break
			}
			if (opcode <= 49) {
				if (heightOnly) {
					packet.readUByte()
				} else {
					this.overlayIds[level]!![x]!![z] = packet.readByte()
					this.tileShapes[level]!![x]!![z] = ((opcode - 2) / 4).toByte()
					this.overlayRotations[level]!![x]!![z] = ((opcode + rotation - 2) and 0x3).toByte()
				}
			} else if (opcode <= 81) {
				if (!this.underwater && !heightOnly) {
					SettingsBits.tileFunctionMasks!![level]!![x]!![z] = (opcode - 49).toByte()
				}
			} else if (!heightOnly) {
				this.underlayIds[level]!![x]!![z] = (opcode - 81).toByte()
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!ha;Lclient!s;IB[[ILclient!s;Lclient!s;)V")
	private fun addRegularTile(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) ground: Ground?, @OriginalArg(2) level: Int, @OriginalArg(4) floorColours: Array<IntArray?>?, @OriginalArg(5) ground2: Ground?, @OriginalArg(6) ground3: Ground?) {
		for (@Pc(7) i in 0 until this.regionSizeX) {
			for (@Pc(11) j in 0 until this.regionSizeZ) {
				if (Minimap.visibleFromLevel == -1 || SettingsBits.isVisibleFromLevel(j, i, Minimap.visibleFromLevel, level)) {
					@Pc(35) var overlayShape: Byte = this.tileShapes[level]!![i]!![j]
					@Pc(44) val overlayRotation: Byte = this.overlayRotations[level]!![i]!![j]
					@Pc(55) val overlayId: Int = this.overlayIds[level]!![i]!![j].toInt() and 0xFF
					@Pc(66) val underlayId: Int = this.underlayIds[level]!![i]!![j].toInt() and 0xFF
					@Pc(81) val spotAnimType: FloType? = if (overlayId == 0) null else this.floorOverlayTypeList!!.list(overlayId - 1)
					if (overlayShape.toInt() == 0 && spotAnimType == null) {
						overlayShape = 12
					}
					@Pc(105) val floorUnderlayType: FluType? = if (underlayId == 0) null else this.underlayTypeList!!.list(underlayId - 1)
					@Pc(107) var underlayFaceCount: Int = 0
					@Pc(109) var overlayFaceCount: Int = 0
					if (overlayShape.toInt() != 0) {
						overlayFaceCount = if (spotAnimType == null) 0 else Terrain.faceVertices[overlayShape.toInt()]
						underlayFaceCount = if (floorUnderlayType == null) 0 else MapLoader.UNDERLAY_FACE_COUNT[overlayShape.toInt()]
					} else if (spotAnimType != null) {
						overlayFaceCount = Terrain.faceVertices[overlayShape.toInt()]
					} else if (floorUnderlayType != null) {
						underlayFaceCount = Terrain.faceVertices[overlayShape.toInt()]
					}
					@Pc(147) val faceCount: Int = overlayFaceCount + underlayFaceCount
					@Pc(149) var faceIndex: Int = 0
					if (faceCount != 0) {
						@Pc(161) val overlayTextureId: Int = if (spotAnimType == null) -1 else spotAnimType.texture
						@Pc(168) val underlayTextureId: Int = if (floorUnderlayType == null) -1 else floorUnderlayType.texture
						@Pc(171) val colours: IntArray = IntArray(faceCount)
						@Pc(174) var minimapColours: IntArray? = IntArray(faceCount)
						@Pc(177) val textures: IntArray = IntArray(faceCount)
						@Pc(180) val textureScales: IntArray = IntArray(faceCount)
						@Pc(182) var hasMinimapColour: Boolean = false
						@Pc(184) var noOverlay: Boolean = false
						@Pc(202) var length: Int
						if (spotAnimType == null || spotAnimType.tileColorHsl == -1 && spotAnimType.minimapColorHsl == -1 && overlayTextureId == -1) {
							noOverlay = true
							length = 0
							while (length < overlayFaceCount) {
								colours[faceIndex] = -1
								faceIndex++
								length++
							}
						} else {
							length = 0
							while (length < overlayFaceCount) {
								textures[faceIndex] = overlayTextureId
								textureScales[faceIndex] = spotAnimType.textureScale
								if (spotAnimType.tileColorHsl == -1) {
									colours[faceIndex] = -1
								} else {
									colours[faceIndex] = spotAnimType.tileColorHsl
								}
								if (spotAnimType.minimapColorHsl == -1) {
									minimapColours!![faceIndex] = -1
								} else {
									hasMinimapColour = true
									minimapColours!![faceIndex] = spotAnimType.minimapColorHsl
								}
								faceIndex++
								length++
							}
							if (!this.underwater && level == 0) {
								Terrain.addWater(i, j, spotAnimType.waterColor, spotAnimType.waterFogDepth * 8, spotAnimType.waterIntensity)
							}
						}
						if (!hasMinimapColour) {
							minimapColours = null
						}
						if (floorUnderlayType == null) {
							if (noOverlay) {
								continue
							}
							length = 0
							while (length < underlayFaceCount) {
								colours[faceIndex] = -1
								faceIndex++
								length++
							}
						} else {
							length = 0
							while (length < underlayFaceCount) {
								textures[faceIndex] = underlayTextureId
								textureScales[faceIndex] = floorUnderlayType.scale
								colours[faceIndex] = floorColours!![i]!![j]
								if (minimapColours != null) {
									minimapColours[faceIndex] = -1
								}
								faceIndex++
								length++
							}
						}
						length = this.tileSizeDeltasX.size
						@Pc(356) val sizesX: IntArray = IntArray(length)
						@Pc(359) val sizesZ: IntArray = IntArray(length)
						@Pc(366) val nextLevelHeightDiffs: IntArray? = if (ground == null) null else IntArray(length)
						@Pc(375) val localLevelHeightDiffs: IntArray? = if (ground == null && ground3 == null) null else IntArray(length)
						@Pc(384) var local384: Int
						@Pc(389) var local389: Int
						@Pc(474) var local474: Int
						@Pc(483) var local483: Int
						@Pc(377) var k: Int = 0
						while (k < length) {
							val deltaX: Int = this.tileSizeDeltasX[k]
							val deltaZ: Int = this.tileSizeDeltasZ[k]
							if (overlayRotation.toInt() == 0) {
								sizesX[k] = deltaX
								sizesZ[k] = deltaZ
							} else if (overlayRotation.toInt() == 1) {
								sizesX[k] = deltaZ
								sizesZ[k] = 512 - deltaX
							} else if (overlayRotation.toInt() == 2) {
								sizesX[k] = 512 - deltaX
								sizesZ[k] = 512 - deltaZ
							} else if (overlayRotation.toInt() == 3) {
								sizesX[k] = 512 - deltaZ
								sizesZ[k] = deltaX
							}
							if (nextLevelHeightDiffs != null && MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape.toInt()][k]) {
								val worldX: Int = sizesX[k] + (i shl 9)
								val worldZ: Int = (j shl 9) + sizesZ[k]
								nextLevelHeightDiffs[k] = ground!!.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
							}
							if (localLevelHeightDiffs != null) {
								if (ground != null && !MapLoader.OVERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape.toInt()][k]) {
									val worldX: Int = sizesX[k] + (i shl 9)
									val worldZ: Int = (j shl 9) + sizesZ[k]
									localLevelHeightDiffs[k] = ground2!!.averageHeight(worldX, worldZ) - ground.averageHeight(worldX, worldZ)
								} else if (ground3 != null && !MapLoader.UNDERLAY_SHAPE_SUPPORTS_HEIGHT[overlayShape.toInt()][k]) {
									val worldX: Int = sizesX[k] + (i shl 9)
									val worldZ: Int = sizesZ[k] + (j shl 9)
									localLevelHeightDiffs[k] = ground3.averageHeight(worldX, worldZ) - ground2!!.averageHeight(worldX, worldZ)
								}
							}
							k++
						}
						val southWestHeight: Int = ground2!!.getHeight(j, i)
						val southEastHeight: Int = ground2.getHeight(j, i + 1)
						val northEastHeight: Int = ground2.getHeight(j + 1, i - -1)
						val northWestHeight: Int = ground2.getHeight(j + 1, i)
						@Pc(620) val bridge: Boolean = SettingsBits.isBridge(j, i)
						if (bridge && level > 1 || !bridge && level > 0) {
							@Pc(631) var occludes: Boolean = true
							if (floorUnderlayType != null && !floorUnderlayType.occlude) {
								occludes = false
							} else if (underlayId == 0 && overlayShape.toInt() != 0) {
								occludes = false
							} else if (overlayId > 0 && spotAnimType != null && !spotAnimType.occlude) {
								occludes = false
							}
							if (occludes && southWestHeight == southEastHeight && northEastHeight == southWestHeight && northWestHeight == southWestHeight) {
								this.occluderFlags!![level]!![i]!![j] = (this.occluderFlags!![level]!![i]!![j].toInt() or 0x4).toByte()
							}
						}
						@Pc(704) var waterColour: Int = 0
						@Pc(706) var waterScale: Int = 0
						@Pc(708) var waterIntensity: Int = 0
						if (this.underwater) {
							waterColour = Scene.getWaterColor(i, j)
							waterScale = Scene.getWaterScale(i, j)
							waterIntensity = Scene.getWaterIntensity(i, j)
						}
						ground2.addTile(i, j, sizesX, nextLevelHeightDiffs, sizesZ, localLevelHeightDiffs, MapLoader.SHAPE_VERTEX_A[overlayShape.toInt()], MapLoader.SHAPE_VERTEX_B[overlayShape.toInt()], MapLoader.SHAPE_VERTEX_C[overlayShape.toInt()], colours, minimapColours, textures, textureScales, waterColour, waterScale, waterIntensity)
						Scene.loadTiles(level, i, j)
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIILclient!cea;III[Lclient!fe;II)V")
	fun decodeTilesServer(@OriginalArg(0) chunkStartX: Int, @OriginalArg(1) level: Int, @OriginalArg(3) packet: Packet?, @OriginalArg(4) sourceChunkZ: Int, @OriginalArg(5) chunkStartZ: Int, @OriginalArg(6) sourceChunkX: Int, @OriginalArg(7) collisionMaps: Array<CollisionMap?>?, @OriginalArg(8) rotation: Int, @OriginalArg(9) sourceLevel: Int) {
		@Pc(15) val sourceTileX: Int = (sourceChunkX and 0x7) * 8
		@Pc(21) val sourceTileZ: Int = (sourceChunkZ and 0x7) * 8
		@Pc(30) var local30: Int
		@Pc(60) var local60: Int
		if (!this.underwater) {
			@Pc(28) val collisionMap: CollisionMap? = collisionMaps!![level]
			var localX: Int = 0
			while (localX < 8) {
				@Pc(34) var i: Int = 0
				while (i < 8) {
					@Pc(48) val clipX: Int = chunkStartX + Terrain.rotateTerrainBlockX(i and 0x7, rotation, localX and 0x7)
					val clipZ: Int = TerrainBlockRotationY.rotateTerrainBlockY(localX and 0x7, i and 0x7, rotation) + chunkStartZ
					if (clipX > 0 && clipX < this.regionSizeX - 1 && clipZ > 0 && this.regionSizeZ - 1 > clipZ) {
						collisionMap!!.unflagBlocked(clipZ, clipX)
					}
					i++
				}
				localX++
			}
		}
		@Pc(98) val regionBaseX: Int = (sourceChunkX and 0xFFFFFFF8.toInt()) shl 3
		val regionBaseZ: Int = (sourceChunkZ and 0xFFFFFFF8.toInt()) shl 3
		@Pc(106) var offsetX: Byte = 0
		@Pc(108) var offsetZ: Byte = 0
		if (rotation == 1) {
			offsetZ = 1
		} else if (rotation == 2) {
			offsetZ = 1
			offsetX = 1
		} else if (rotation == 3) {
			offsetX = 1
		}
		var currentLevel: Int = 0
		while (currentLevel < this.levels) {
			@Pc(139) var j: Int = 0
			while (j < 64) {
				@Pc(143) var k: Int = 0
				while (k < 64) {
					if (sourceLevel == currentLevel && j >= sourceTileX && sourceTileX + 8 >= j && k >= sourceTileZ && k <= sourceTileZ + 8) {
						@Pc(217) val destX: Int
						@Pc(229) val destZ: Int
						if (j == sourceTileX + 8 || sourceTileZ + 8 == k) {
							if (rotation == 0) {
								destZ = k + chunkStartZ - sourceTileZ
								destX = j + chunkStartX - sourceTileX
							} else if (rotation == 1) {
								destZ = chunkStartZ + sourceTileX + 8 - j
								destX = k + chunkStartX - sourceTileZ
							} else if (rotation == 2) {
								destZ = chunkStartZ + sourceTileZ + 8 - k
								destX = sourceTileX + chunkStartX + 8 - j
							} else {
								destX = sourceTileZ + chunkStartX + 8 - k
								destZ = chunkStartZ + j - sourceTileX
							}
							this.decodeTile(true, 0, destZ, 0, packet, 0, destX, j + regionBaseX, k + regionBaseZ, level)
						} else {
							destX = Terrain.rotateTerrainBlockX(k and 0x7, rotation, j and 0x7) + chunkStartX
							destZ = chunkStartZ + TerrainBlockRotationY.rotateTerrainBlockY(j and 0x7, k and 0x7, rotation)
							this.decodeTile(false, offsetZ.toInt(), destZ, rotation, packet, offsetX.toInt(), destX, regionBaseX + j, k + regionBaseZ, level)
						}
						if (j == 63 || k == 63) {
							@Pc(355) var edgeCount: Byte = 1
							if (j == 63 && k == 63) {
								edgeCount = 3
							}
							@Pc(369) var m: Int = 0
							while (m < edgeCount) {
								@Pc(373) var edgeTileX: Int = j
								@Pc(375) var edgeTileZ: Int = k
								if (m == 0) {
									edgeTileZ = if (k == 63) 64 else k
									edgeTileX = if (j == 63) 64 else j
								} else if (m == 1) {
									edgeTileX = 64
								} else if (m == 2) {
									edgeTileZ = 64
								}
								@Pc(426) val edgeDestX: Int
								@Pc(419) val edgeDestZ: Int
								if (rotation == 0) {
									edgeDestZ = edgeTileZ + chunkStartZ - sourceTileZ
									edgeDestX = edgeTileX + chunkStartX - sourceTileX
								} else if (rotation == 1) {
									edgeDestZ = sourceTileX + chunkStartZ + 8 - edgeTileX
									edgeDestX = chunkStartX + edgeTileZ - sourceTileZ
								} else if (rotation == 2) {
									edgeDestZ = chunkStartZ + sourceTileZ + 8 - edgeTileZ
									edgeDestX = sourceTileX + chunkStartX + 8 - edgeTileX
								} else {
									edgeDestX = sourceTileZ + chunkStartX + 8 - edgeTileZ
									edgeDestZ = edgeTileX + chunkStartZ - sourceTileX
								}
								if (edgeDestX >= 0 && this.regionSizeX > edgeDestX && edgeDestZ >= 0 && edgeDestZ < this.regionSizeZ) {
									this.tileHeights[level]!![edgeDestX]!![edgeDestZ] = this.tileHeights[level]!![offsetX + destX]!![offsetZ + destZ]
								}
								m++
							}
						}
					} else {
						this.decodeTile(false, 0, -1, 0, packet, 0, -1, 0, 0, 0)
					}
					k++
				}
				j++
			}
			currentLevel++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!ha;[[[II[Lclient!fe;)V")
	fun initializeFloor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) underwaterHeights: Array<Array<IntArray?>?>?, @OriginalArg(3) collisionMaps: Array<CollisionMap?>?) {
		@Pc(19) var level: Int
		@Pc(23) var local23: Int
		@Pc(27) var local27: Int
		if (!this.underwater) {
			level = 0
			while (level < 4) {
				var tileX: Int = 0
				while (tileX < this.regionSizeX) {
					var tileZ: Int = 0
					while (tileZ < this.regionSizeZ) {
						if ((SettingsBits.tileFunctionMasks!![level]!![tileX]!![tileZ].toInt() and 0x1) != 0) {
							@Pc(44) var clipLevel: Int = level
							if ((SettingsBits.tileFunctionMasks!![1]!![tileX]!![tileZ].toInt() and 0x2) != 0) {
								clipLevel = level - 1
							}
							if (clipLevel >= 0) {
								collisionMaps!![clipLevel]!!.flagBlocked(tileZ, tileX)
							}
						}
						tileZ++
					}
					tileX++
				}
				level++
			}
		}
		level = 0
		while (level < this.levels) {
			var groundFlags: Int = 0
			var featureFlags: Int = 0
			if (!this.underwater) {
				if (Terrain.hdLight) {
					groundFlags = 2
				}
				if (Terrain.hdWater) {
					featureFlags = 8
				}
				if (MapLoader.hdShadows != 0) {
					if ((level == 0) or Terrain.highLightDetail) {
						featureFlags = featureFlags or 0x10
					}
					groundFlags = groundFlags or 0x1
				}
			}
			if (Terrain.hdLight) {
				featureFlags = featureFlags or 0x7
			}
			if (!Terrain.enableTextures) {
				featureFlags = featureFlags or 0x20
			}
			@Pc(163) val nextHeights: Array<IntArray?>? = if (underwaterHeights == null || level >= underwaterHeights.size) this.tileHeights[level] else underwaterHeights[level]
			Scene.setLocalGround(level, toolkit!!.createGround(this.regionSizeX, this.regionSizeZ, this.tileHeights[level], nextHeights, groundFlags, featureFlags))
			level++
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(IIIII)V")
	fun setMapSquareHeights(@OriginalArg(0) startZ: Int, @OriginalArg(4) z: Int) {
		@Pc(7) var level: Int = 0
		while (level < this.levels) {
			this.setTileHeights(z, 64, startZ, 64, level)
			level++
		}
	}

	companion object {
		@OriginalMember(owner = "client!qha", name = "N", descriptor = "[I")
		@JvmField
		val faceVertices: IntArray = intArrayOf(2, 1, 1, 1, 2, 2, 2, 1, 3, 3, 3, 2, 0, 4, 0)
		@OriginalMember(owner = "client!fi", name = "U", descriptor = "[I")
		@JvmField
		val overlayRenderColours: IntArray = IntArray(13)

		@OriginalMember(owner = "client!dk", name = "b", descriptor = "[I")
		@JvmField
		val OVERLAY_FACE_COUNTS: IntArray = intArrayOf(4, 2, 1, 1, 2, 2, 3, 1, 3, 3, 3, 2, 0)

		@OriginalMember(owner = "client!gb", name = "v", descriptor = "[[Z")
		@JvmField
		val BLENDING_SHAPE_EDGE_FACES: Array<BooleanArray?> = arrayOf<BooleanArray?>(BooleanArray(4), booleanArrayOf(false, true, true, false), booleanArrayOf(true, false, true, false), booleanArrayOf(true, false, true, false), booleanArrayOf(false, false, true, false), booleanArrayOf(false, false, true, false), booleanArrayOf(true, false, true, false), booleanArrayOf(true, false, false, true), booleanArrayOf(true, false, false, true), booleanArrayOf(true, true, false, false), BooleanArray(4), booleanArrayOf(false, true, false, true), BooleanArray(4))

		@OriginalMember(owner = "client!iq", name = "n", descriptor = "[I")
		@JvmField
		val OVERLAY_FACE_COUNT_BLENDED: IntArray = intArrayOf(4, 2, 1, 1, 2, 2, 3, 1, 3, 3, 3, 2, 0)

		@OriginalMember(owner = "client!il", name = "A", descriptor = "[[I")
		@JvmField
		val BLENDING_SHAPE_VERTEX_C: Array<IntArray?> = arrayOf<IntArray?>(intArrayOf(12, 12, 12, 12), intArrayOf(12, 12, 12, 12, 12, 5), intArrayOf(5, 5, 1, 1), intArrayOf(5, 1, 1, 5), intArrayOf(5, 5, 5), intArrayOf(5, 5, 5), intArrayOf(12, 12, 12, 12, 12, 12), intArrayOf(1, 12, 12, 12, 12, 12), intArrayOf(1, 1, 7, 1), intArrayOf(8, 9, 9, 8, 8, 3, 1, 9), intArrayOf(8, 8, 9, 8, 9, 9), intArrayOf(10, 10, 11, 11, 11, 7, 3, 7), intArrayOf(12, 12, 12, 12))

		@OriginalMember(owner = "client!il", name = "v", descriptor = "[I")
		@JvmField
		val overlayTileColours: IntArray = IntArray(13)
		@OriginalMember(owner = "client!uk", name = "g", descriptor = "[[Z")
		@JvmField
		val BLENDED_SHAPE_EDGE_FACES: Array<BooleanArray?> = arrayOf<BooleanArray?>(BooleanArray(4), BooleanArray(4), booleanArrayOf(false, false, true, false), booleanArrayOf(false, false, true, false), booleanArrayOf(false, false, true, false), booleanArrayOf(false, false, true, false), booleanArrayOf(true, false, true, false), booleanArrayOf(true, false, false, true), booleanArrayOf(true, false, false, true), BooleanArray(4), BooleanArray(4), BooleanArray(4), BooleanArray(4))

		@OriginalMember(owner = "client!mia", name = "h", descriptor = "[[I")
		@JvmField
		val BLENDING_FACE_MAPPING: Array<IntArray?> = arrayOf<IntArray?>(intArrayOf(0, 1, 2, 3), intArrayOf(1, -1, -1, 0), intArrayOf(-1, 2, -1, 0), intArrayOf(-1, 0, -1, 2), intArrayOf(0, 1, -1, 2), intArrayOf(1, 2, -1, 0), intArrayOf(-1, 4, -1, 1), intArrayOf(-1, 3, 4, -1), intArrayOf(-1, 0, 2, -1), intArrayOf(-1, -1, 2, 0), intArrayOf(0, 2, 5, 3), intArrayOf(0, -1, 6, -1), intArrayOf(0, 1, 2, 3))

		@OriginalMember(owner = "client!wc", name = "a", descriptor = "(IIIB)I")
		@JvmStatic
		fun rotateTerrainBlockX(@OriginalArg(0) localZ: Int, @OriginalArg(1) rotation: Int, @OriginalArg(2) localX: Int): Int {
			@Pc(12) val rotationMasked: Int = rotation and 0x3
			if (rotationMasked == 0) {
				return localX
			} else if (rotationMasked == 1) {
				return localZ
			} else if (rotationMasked == 2) {
				return 7 - localX
			} else {
				return 7 - localZ
			}
		}

		@OriginalMember(owner = "client!uk", name = "f", descriptor = "[I")
		@JvmField
		var underlayDividers: IntArray? = null

		@OriginalMember(owner = "client!gia", name = "p", descriptor = "[I")
		@JvmField
		var underlayLightness: IntArray? = null

		@OriginalMember(owner = "client!ws", name = "b", descriptor = "[I")
		@JvmField
		val overlaySlots: IntArray = IntArray(13)
		@OriginalMember(owner = "client!gv", name = "a", descriptor = "Z")
		@JvmField
		var blendFloor: Boolean = false

		@OriginalMember(owner = "client!eh", name = "d", descriptor = "[I")
		@JvmField
		val overlayFaceVertices: IntArray = IntArray(6)

		@OriginalMember(owner = "client!ad", name = "g", descriptor = "[I")
		@JvmField
		val overlayTextureIds: IntArray = IntArray(13)

		@OriginalMember(owner = "client!ls", name = "v", descriptor = "[I")
		@JvmField
		var underlayHues: IntArray? = null

		@OriginalMember(owner = "client!aga", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun addWater(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) colour: Int, @OriginalArg(3) fogDepth: Int, @OriginalArg(4) intensity: Int) {
			if (Scene.waterColors != null) {
				Scene.waterColors!![x]!![z] = colour or 0xFF000000.toInt()
			}
			if (Scene.waterScales != null) {
				Scene.waterScales!![x]!![z] = fogDepth.toShort()
			}
			if (Scene.waterIntensities != null) {
				Scene.waterIntensities!![x]!![z] = intensity.toByte()
			}
		}

		@OriginalMember(owner = "client!qg", name = "c", descriptor = "Z")
		@JvmField
		var hdLight: Boolean = false

		@OriginalMember(owner = "client!fn", name = "n", descriptor = "Z")
		@JvmField
		var hdWater: Boolean = false

		@OriginalMember(owner = "client!wda", name = "c", descriptor = "Z")
		@JvmField
		var highLightDetail: Boolean = false

		@OriginalMember(owner = "client!ev", name = "o", descriptor = "[I")
		@JvmField
		var underlayCounts: IntArray? = null

		@OriginalMember(owner = "client!vca", name = "u", descriptor = "[[I")
		@JvmField
		val BLENDED_SHAPE_VERTEX_C: Array<IntArray?> = arrayOf<IntArray?>(intArrayOf(12, 12, 12, 12), intArrayOf(12, 12, 12, 12), intArrayOf(5, 5, 5), intArrayOf(5, 5, 5), intArrayOf(5, 5, 5), intArrayOf(5, 5, 5), intArrayOf(12, 12, 12, 12, 12, 12), intArrayOf(1, 1, 1, 7), intArrayOf(1, 1, 7, 1), intArrayOf(8, 9, 9, 8, 8, 9), intArrayOf(8, 8, 9, 8, 9, 9), intArrayOf(10, 10, 11, 11, 11, 10), intArrayOf(12, 12, 12, 12))

		@OriginalMember(owner = "client!vh", name = "d", descriptor = "[I")
		@JvmField
		val UNDERLAY_FACE_COUNT_BLENDED: IntArray = intArrayOf(0, 4, 3, 3, 1, 1, 3, 5, 1, 5, 3, 6, 4)

		@OriginalMember(owner = "client!gba", name = "p", descriptor = "[I")
		@JvmField
		val overlayTextureScales: IntArray = IntArray(13)

		@OriginalMember(owner = "client!ca", name = "a", descriptor = "[I")
		@JvmField
		val UNDERLAY_FACE_COUNTS: IntArray = intArrayOf(0, 2, 2, 2, 1, 1, 3, 3, 1, 3, 3, 4, 4)
		@OriginalMember(owner = "client!lf", name = "a", descriptor = "(III)I")
		@JvmStatic
		fun calculateTileHeight(@OriginalArg(1) x: Int, @OriginalArg(2) y: Int): Int {
			@Pc(39) var height: Int = Terrain.perlinNoise(4, y + 91923, x + 45365) + ((Terrain.perlinNoise(2, y + 37821, x + 10294) - 128) shr 1) + ((Terrain.perlinNoise(1, y, x) + -128) shr 2) - 128
			height = (height.toDouble() * 0.3).toInt() + 35
			if (height < 10) {
				height = 10
			} else if (height > 60) {
				height = 60
			}
			return height
		}

		@OriginalMember(owner = "client!mda", name = "c", descriptor = "[[I")
		@JvmField
		val BLENDING_SHAPE_VERTEX_A: Array<IntArray?> = arrayOf<IntArray?>(intArrayOf(0, 2, 4, 6), intArrayOf(6, 0, 2, 3, 5, 3), intArrayOf(6, 0, 2, 4), intArrayOf(2, 5, 6, 1), intArrayOf(0, 2, 6), intArrayOf(6, 0, 2), intArrayOf(5, 6, 0, 1, 2, 4), intArrayOf(7, 7, 1, 2, 4, 6), intArrayOf(2, 4, 4, 7), intArrayOf(6, 6, 4, 0, 1, 1, 3, 3), intArrayOf(0, 2, 2, 6, 6, 4), intArrayOf(0, 2, 2, 3, 7, 0, 4, 3), intArrayOf(0, 2, 4, 6))

		@OriginalMember(owner = "client!qaa", name = "a", descriptor = "[I")
		@JvmField
		val overlayNeighbourMasks: IntArray = IntArray(13)

		@OriginalMember(owner = "client!io", name = "c", descriptor = "Z")
		@JvmField
		var enableTextures: Boolean = false
		@OriginalMember(owner = "client!cv", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun releaseUnderlayData() {
			Terrain.underlayHues = null
			Terrain.underlayCounts = null
			PlayerList.underlaySaturation = null
			MapRegion.lowDetailShadows = false
			Terrain.underlayDividers = null
			Terrain.underlayLightness = null
		}

		@OriginalMember(owner = "client!fu", name = "a", descriptor = "(ZZ)Z")
		@JvmStatic
		fun booleanOr(@OriginalArg(0) lhs: Boolean, @OriginalArg(1) rhs: Boolean): Boolean {
			return lhs or rhs
		}

		@OriginalMember(owner = "client!ik", name = "a", descriptor = "(III)I")
		@JvmStatic
		fun randomNoiseWeightedSum(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int): Int {
			@Pc(41) val corners: Int = Terrain.randomNoise(x - 1, y - 1) + Terrain.randomNoise(x + 1, y + -1) + Terrain.randomNoise(x - 1, y + 1) + Terrain.randomNoise(x - -1, y - -1)
			@Pc(71) val sides: Int = Terrain.randomNoise(x - 1, y) + Terrain.randomNoise(x + 1, y) + Terrain.randomNoise(x, y - 1) + Terrain.randomNoise(x, y + 1)
			@Pc(76) val center: Int = Terrain.randomNoise(x, y)
			return center / 4 + sides / 8 + corners / 16
		}

		@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IBIII)I")
		@JvmStatic
		fun cosineInterpolate(@OriginalArg(0) b: Int, @OriginalArg(2) frequencyReciprocal: Int, @OriginalArg(3) angle: Int, @OriginalArg(4) a: Int): Int {
			@Pc(16) val cosine: Int = (65536 - TextureCombineSource.COSINE[angle * 8192 / frequencyReciprocal]) shr 1
			return ((cosine * b) shr 16) + ((a * (65536 - cosine)) shr 16)
		}

		@OriginalMember(owner = "client!nl", name = "a", descriptor = "(IIB)I")
		@JvmStatic
		fun randomNoise(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int): Int {
			@Pc(10) val seed: Int = x + y * 57
			@Pc(16) val n: Int = seed xor (seed shl 13)
			@Pc(38) val value: Int = (n * (n * 15731 * n + 789221) + 1376312589) and Int.MAX_VALUE
			return (value shr 19) and 0xFF
		}

		@OriginalMember(owner = "client!cda", name = "a", descriptor = "(IIBI)I")
		@JvmStatic
		fun perlinNoise(@OriginalArg(0) frequencyReciprocal: Int, @OriginalArg(1) y: Int, @OriginalArg(3) x: Int): Int {
			@Pc(15) val adjX: Int = x / frequencyReciprocal
			@Pc(21) val angleX: Int = (frequencyReciprocal - 1) and x
			@Pc(25) val adjY: Int = y / frequencyReciprocal
			@Pc(31) val angleY: Int = y and (frequencyReciprocal - 1)
			@Pc(36) val noiseBase: Int = Terrain.randomNoiseWeightedSum(adjX, adjY)
			@Pc(43) val noiseEast: Int = Terrain.randomNoiseWeightedSum(adjX + 1, adjY)
			@Pc(50) val noiseSouth: Int = Terrain.randomNoiseWeightedSum(adjX, adjY + 1)
			@Pc(59) val noiseSouthEast: Int = Terrain.randomNoiseWeightedSum(adjX + 1, adjY + 1)
			@Pc(66) val interpolatedNoiseNorth: Int = Terrain.cosineInterpolate(noiseEast, frequencyReciprocal, angleX, noiseBase)
			@Pc(73) val interpolatedNoiseSouth: Int = Terrain.cosineInterpolate(noiseSouthEast, frequencyReciprocal, angleX, noiseSouth)
			return Terrain.cosineInterpolate(interpolatedNoiseSouth, frequencyReciprocal, angleY, interpolatedNoiseNorth)
		}
	}
}
