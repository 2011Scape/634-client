import com.jagex.game.runetek5.JavaComplexBlendedTile
import com.jagex.game.runetek5.JavaGenericBlendedTile
import com.jagex.game.runetek5.JavaMatrix
import com.jagex.game.runetek5.JavaSimpleBlendedTile
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.JavaComplexTile
import com.jagex.game.runetek5.scene.tile.PlainTile
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.graphics.safe.PureJavaFloorModel
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.math.ColourUtils
import lang.Thread

@OriginalClass("client!cr")
class JavaGround : Ground {

	@OriginalMember(owner = "client!cr", name = "k", descriptor = "F")
	private var cameraE1_1: Float = 0f

	@OriginalMember(owner = "client!cr", name = "l", descriptor = "F")
	private var cameraTx: Float = 0f

	@OriginalMember(owner = "client!cr", name = "m", descriptor = "F")
	private var cameraE2_1: Float = 0f

	@OriginalMember(owner = "client!cr", name = "n", descriptor = "[[Lclient!mga;")
	private var simpleTiles: Array<Array<PlainTile?>>? = null

	@OriginalMember(owner = "client!cr", name = "o", descriptor = "F")
	private var cameraE1_2: Float = 0f

	@OriginalMember(owner = "client!cr", name = "q", descriptor = "F")
	private var cameraTz: Float = 0f

	@OriginalMember(owner = "client!cr", name = "r", descriptor = "[[Lclient!rb;")
	private var genericBlendedTiles: Array<Array<JavaGenericBlendedTile?>>? = null

	@OriginalMember(owner = "client!cr", name = "t", descriptor = "F")
	private var cameraE2_2: Float = 0f

	@OriginalMember(owner = "client!cr", name = "u", descriptor = "[[Lclient!sp;")
	private var complexTiles: Array<Array<JavaComplexTile?>>? = null

	@OriginalMember(owner = "client!cr", name = "w", descriptor = "F")
	private var cameraE1_3: Float = 0f

	@OriginalMember(owner = "client!cr", name = "x", descriptor = "[[Lclient!ofa;")
	private var complexBlendedTiles: Array<Array<JavaComplexBlendedTile?>>? = null

	@OriginalMember(owner = "client!cr", name = "y", descriptor = "F")
	private var cameraTy: Float = 0f

	@OriginalMember(owner = "client!cr", name = "z", descriptor = "F")
	private var cameraE3_2: Float = 0f

	@OriginalMember(owner = "client!cr", name = "B", descriptor = "F")
	private var cameraE2_3: Float = 0f

	@OriginalMember(owner = "client!cr", name = "C", descriptor = "[[Lclient!ul;")
	private var simpleBlendedTiles: Array<Array<JavaSimpleBlendedTile?>>? = null

	@OriginalMember(owner = "client!cr", name = "D", descriptor = "F")
	private var cameraE3_3: Float = 0f

	@OriginalMember(owner = "client!cr", name = "F", descriptor = "F")
	private var cameraE3_1: Float = 0f

	@OriginalMember(owner = "client!cr", name = "A", descriptor = "I")
	private var depthOverride: Int = -1

	@OriginalMember(owner = "client!cr", name = "p", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit

	@OriginalMember(owner = "client!cr", name = "v", descriptor = "I")
	private val featureFlags: Int

	@OriginalMember(owner = "client!cr", name = "s", descriptor = "[[B")
	private var lightLevels: Array<ByteArray>?

	@OriginalMember(owner = "client!cr", name = "E", descriptor = "[[B")
	private var shadowLevels: Array<ByteArray>?

	@OriginalMember(owner = "client!cr", name = "<init>", descriptor = "(Lclient!ao;IIII[[I[[II)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) groundFlags: Int, @OriginalArg(2) featureFlags: Int, @OriginalArg(3) width: Int, @OriginalArg(4) length: Int, @OriginalArg(5) tileHeights: Array<IntArray?>?, @OriginalArg(6) heights: Array<IntArray?>?, @OriginalArg(7) scaling: Int) : super(width, length, scaling, tileHeights) {
		this.toolkit = javaToolkit!!
		this.featureFlags = featureFlags
		this.lightLevels = Array(width + 1) { ByteArray(length + 1) }
		@Pc(29) val ambient: Int = this.toolkit.ambient shr 9
		for (@Pc(31) i in 1 until length) {
			for (@Pc(34) j in 1 until width) {
				@Pc(53) val dx: Int = heights!![j + 1]!![i] - heights[j - 1]!![i]
				@Pc(69) val dz: Int = heights[j]!![i + 1] - heights[j]!![i - 1]
				@Pc(84) val magnitude: Int = kotlin.math.sqrt((dx * dx + scaling * 512 + dz * dz).toDouble()).toInt()
				@Pc(90) val normalX: Int = (dx shl 8) / magnitude
				@Pc(96) val normalY: Int = scaling * -512 / magnitude
				@Pc(102) val normalZ: Int = (dz shl 8) / magnitude
				@Pc(124) var light: Int = ambient + ((this.toolkit.sunX * normalX + this.toolkit.sunY * normalY + this.toolkit.sunZ * normalZ) shr 17)
				light = light shr 0x1
				if (light < 2) {
					light = 2
				} else if (light > 126) {
					light = 126
				}
				this.lightLevels!![j][i] = light.toByte()
			}
		}
		this.shadowLevels = Array(width + 1) { ByteArray(length + 1) }
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(Lclient!ab;[I)V")
	override fun addLight(@OriginalArg(0) pointLight: PointLight?, @OriginalArg(1) tileMask: IntArray?) {
	}

	@OriginalMember(owner = "client!cr", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) colours: IntArray?, @OriginalArg(7) blendedColours: IntArray?, @OriginalArg(8) textures: IntArray?, @OriginalArg(9) sizes: IntArray?, @OriginalArg(10) fogColour: Int, @OriginalArg(11) maxWaterDepth: Int, @OriginalArg(12) waterBias: Int, @OriginalArg(13) hasShadow: Boolean) {
		@Pc(8) val generic: Boolean = (this.featureFlags and 0x20) == 0
		if (this.simpleBlendedTiles == null && !generic) {
			this.simpleBlendedTiles = Array(this.width) { arrayOfNulls<JavaSimpleBlendedTile>(this.length) }
			this.complexBlendedTiles = Array(this.width) { arrayOfNulls<JavaComplexBlendedTile>(this.length) }
		} else if (this.genericBlendedTiles == null && generic) {
			this.genericBlendedTiles = Array(this.width) { arrayOfNulls<JavaGenericBlendedTile>(this.length) }
		} else if (this.simpleTiles != null) {
			throw IllegalStateException()
		}
		if (offsetsX == null || offsetsX.size == 0) {
			return
		}
		for (@Pc(56) i in 0 until colours!!.size) {
			if (colours[i] == -1) {
				colours[i] = 0
			} else {
				colours[i] = (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(colours[i]).toInt() and 0xFFFF] shl 8) or 0xFF
			}
		}
		if (blendedColours != null) {
			for (@Pc(92) j in 0 until blendedColours.size) {
				if (blendedColours[j] == -1) {
					blendedColours[j] = 0
				} else {
					blendedColours[j] = (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(blendedColours[j]).toInt() and 0xFFFF] shl 8) or 0xFF
				}
			}
		}
		@Pc(186) var local186: Int
		@Pc(191) var local191: Int
		@Pc(195) var local195: Int
		@Pc(215) var local215: Int
		@Pc(336) var local336: Int
		@Pc(383) var local383: Int
		@Pc(467) var local467: Int
		@Pc(499) var local499: Int
		if (generic) {
			@Pc(130) val javaGenericBlendedTile: JavaGenericBlendedTile = JavaGenericBlendedTile()
			javaGenericBlendedTile.vertexCount = offsetsX.size.toShort()
			javaGenericBlendedTile.faceCount = (offsetsX.size / 3).toShort()
			javaGenericBlendedTile.verticesX = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.verticesY = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.verticesZ = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.vertexColours = IntArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.vertexTextures = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.vertexSizes = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			javaGenericBlendedTile.vertexLight = ByteArray(javaGenericBlendedTile.vertexCount.toInt())
			if (waterDepths != null) {
				javaGenericBlendedTile.waterDepths = ShortArray(javaGenericBlendedTile.vertexCount.toInt())
			}
			var vertexIndex: Int = 0
			while (vertexIndex < javaGenericBlendedTile.vertexCount) {
				val vertexX: Int = offsetsX[vertexIndex]
				val vertexZ: Int = offsetsZ!![vertexIndex]
				var vertexLightLevel: Int
				if (vertexX == 0 && vertexZ == 0) {
					vertexLightLevel = this.lightLevels!![x][z] - this.shadowLevels!![x][z]
				} else if (vertexX == 0 && vertexZ == this.tileUnits) {
					vertexLightLevel = this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]
				} else if (vertexX == this.tileUnits && vertexZ == this.tileUnits) {
					vertexLightLevel = this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]
				} else if (vertexX == this.tileUnits && vertexZ == 0) {
					vertexLightLevel = this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]
				} else {
					val southLight: Int = (this.lightLevels!![x][z] - this.shadowLevels!![x][z]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]) * vertexX
					val northLight: Int = (this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]) * vertexX
					vertexLightLevel = (southLight * (this.tileUnits - vertexZ) + northLight * vertexZ) shr (this.tileScale * 2)
				}
				val worldX: Int = (x shl this.tileScale) + vertexX
				val worldZ: Int = (z shl this.tileScale) + vertexZ
				javaGenericBlendedTile.verticesX!![vertexIndex] = vertexX.toShort()
				javaGenericBlendedTile.verticesZ!![vertexIndex] = vertexZ.toShort()
				javaGenericBlendedTile.verticesY!![vertexIndex] = (this.averageHeight(worldX, worldZ) + (if (heightOffsets == null) 0 else heightOffsets[vertexIndex])).toShort()
				if (vertexLightLevel < 0) {
					vertexLightLevel = 0
				}
				if (colours[vertexIndex] == 0) {
					javaGenericBlendedTile.vertexColours!![vertexIndex] = 0
					if (blendedColours != null) {
						javaGenericBlendedTile.vertexLight!![vertexIndex] = vertexLightLevel.toByte()
					}
				} else {
					var fogAlpha: Int = 0
					if (waterDepths != null) {
						@Pc(479) val waterDepth: Short = waterDepths[vertexIndex].toShort()
						javaGenericBlendedTile.waterDepths!![vertexIndex] = waterDepth
						if (maxWaterDepth != 0) {
							fogAlpha = waterDepth * 255 / maxWaterDepth
							if (fogAlpha < 0) {
								fogAlpha = 0
							} else if (fogAlpha > 255) {
								fogAlpha = 255
							}
						}
					}
					var colourAlpha: Int = -16777216
					if (textures!![vertexIndex] != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(textures[vertexIndex])!!.effectType.toInt())) {
						colourAlpha = -1694498816
					}
					javaGenericBlendedTile.vertexColours!![vertexIndex] = colourAlpha or ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[vertexIndex] shr 8, vertexLightLevel), fogAlpha, fogColour)
					if (blendedColours != null) {
						javaGenericBlendedTile.vertexLight!![vertexIndex] = vertexLightLevel.toByte()
					}
				}
				javaGenericBlendedTile.vertexTextures!![vertexIndex] = textures!![vertexIndex].toShort()
				javaGenericBlendedTile.vertexSizes!![vertexIndex] = sizes!![vertexIndex].toShort()
				vertexIndex++
			}
			if (blendedColours != null) {
				javaGenericBlendedTile.faceBlendedColours = IntArray(javaGenericBlendedTile.faceCount.toInt())
			}
			var faceIndex: Int = 0
			while (faceIndex < javaGenericBlendedTile.faceCount) {
				val vertexA: Int = faceIndex * 3
				if (blendedColours != null && blendedColours[vertexA] != 0) {
					javaGenericBlendedTile.faceBlendedColours!![faceIndex] = (blendedColours[vertexA] shr 8) or 0xFF000000.toInt()
				}
				faceIndex++
			}
			this.genericBlendedTiles!![x][z] = javaGenericBlendedTile
			return
		}
		@Pc(610) var simple: Boolean = true
		var southWestVertex: Int = -1
		var southEastVertex: Int = -1
		var northEastVertex: Int = -1
		var northWestVertex: Int = -1
		if (offsetsX.size == 6) {
			for (vertexIndex in 0 until 6) {
				if (offsetsX[vertexIndex] == 0 && offsetsZ!![vertexIndex] == 0) {
					if (southWestVertex != -1 && colours[southWestVertex] != colours[vertexIndex]) {
						simple = false
						break
					}
					southWestVertex = vertexIndex
				} else if (offsetsX[vertexIndex] == this.tileUnits && offsetsZ!![vertexIndex] == 0) {
					if (southEastVertex != -1 && colours[southEastVertex] != colours[vertexIndex]) {
						simple = false
						break
					}
					southEastVertex = vertexIndex
				} else if (offsetsX[vertexIndex] == this.tileUnits && offsetsZ!![vertexIndex] == this.tileUnits) {
					if (northEastVertex != -1 && colours[northEastVertex] != colours[vertexIndex]) {
						simple = false
						break
					}
					northEastVertex = vertexIndex
				} else if (offsetsX[vertexIndex] == 0 && offsetsZ!![vertexIndex] == this.tileUnits) {
					if (northWestVertex != -1 && colours[northWestVertex] != colours[vertexIndex]) {
						simple = false
						break
					}
					northWestVertex = vertexIndex
				}
			}
			if (southWestVertex == -1 || southEastVertex == -1 || northEastVertex == -1 || northWestVertex == -1) {
				simple = false
			}
			if (simple) {
				if (heightOffsets != null) {
					for (vertexIndex in 0 until 4) {
						if (heightOffsets[vertexIndex] != 0) {
							simple = false
							break
						}
					}
				}
				if (simple) {
					for (vertexIndex in 1 until 4) {
						if (offsetsX[vertexIndex] != offsetsX[0] && offsetsX[vertexIndex] != offsetsX[0] + this.tileUnits && offsetsX[vertexIndex] != offsetsX[0] - this.tileUnits) {
							simple = false
							break
						}
						if (offsetsZ!![vertexIndex] != offsetsZ[0] && offsetsZ[vertexIndex] != offsetsZ[0] + this.tileUnits && offsetsZ[vertexIndex] != offsetsZ[0] - this.tileUnits) {
							simple = false
							break
						}
					}
				}
			}
		} else {
			simple = false
		}
		if (!simple) {
			@Pc(1623) val javaComplexBlendedTile: JavaComplexBlendedTile = JavaComplexBlendedTile()
			javaComplexBlendedTile.vertexCount = offsetsX.size.toShort()
			javaComplexBlendedTile.faceCount = (offsetsX.size / 3).toShort()
			javaComplexBlendedTile.verticesX = ShortArray(javaComplexBlendedTile.vertexCount.toInt())
			javaComplexBlendedTile.verticesY = ShortArray(javaComplexBlendedTile.vertexCount.toInt())
			javaComplexBlendedTile.verticesZ = ShortArray(javaComplexBlendedTile.vertexCount.toInt())
			javaComplexBlendedTile.vertexColours = IntArray(javaComplexBlendedTile.vertexCount.toInt())
			if (waterDepths != null) {
				javaComplexBlendedTile.waterDepths = ShortArray(javaComplexBlendedTile.vertexCount.toInt())
			}
			@Pc(1693) var local1693: Int
			@Pc(1814) var local1814: Int
			@Pc(1861) var local1861: Int
			@Pc(1947) var local1947: Int
			var vertexIndex: Int = 0
			while (vertexIndex < javaComplexBlendedTile.vertexCount) {
				val vertexX: Int = offsetsX[vertexIndex]
				val vertexZ: Int = offsetsZ!![vertexIndex]
				var vertexLightLevel: Int
				if (vertexX == 0 && vertexZ == 0) {
					vertexLightLevel = this.lightLevels!![x][z] - this.shadowLevels!![x][z]
				} else if (vertexX == 0 && vertexZ == this.tileUnits) {
					vertexLightLevel = this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]
				} else if (vertexX == this.tileUnits && vertexZ == this.tileUnits) {
					vertexLightLevel = this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]
				} else if (vertexX == this.tileUnits && vertexZ == 0) {
					vertexLightLevel = this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]
				} else {
					val southLight: Int = (this.lightLevels!![x][z] - this.shadowLevels!![x][z]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]) * vertexX
					val northLight: Int = (this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]) * vertexX
					vertexLightLevel = (southLight * (this.tileUnits - vertexZ) + northLight * vertexZ) shr (this.tileScale * 2)
				}
				val worldX: Int = (x shl this.tileScale) + vertexX
				val worldZ: Int = (z shl this.tileScale) + vertexZ
				javaComplexBlendedTile.verticesX!![vertexIndex] = vertexX.toShort()
				javaComplexBlendedTile.verticesZ!![vertexIndex] = vertexZ.toShort()
				javaComplexBlendedTile.verticesY!![vertexIndex] = (this.averageHeight(worldX, worldZ) + (if (heightOffsets == null) 0 else heightOffsets[vertexIndex])).toShort()
				if (vertexLightLevel < 0) {
					vertexLightLevel = 0
				}
				if (colours[vertexIndex] != 0) {
					var fogAlpha: Int = 0
					if (waterDepths != null) {
						@Pc(1959) val waterDepth: Short = waterDepths[vertexIndex].toShort()
						javaComplexBlendedTile.waterDepths!![vertexIndex] = waterDepth
						if (maxWaterDepth != 0) {
							fogAlpha = waterDepth * 255 / maxWaterDepth
							if (fogAlpha < 0) {
								fogAlpha = 0
							} else if (fogAlpha > 255) {
								fogAlpha = 255
							}
						}
					}
					javaComplexBlendedTile.vertexColours!![vertexIndex] = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[vertexIndex] shr 8, vertexLightLevel), fogAlpha, fogColour)
					if (blendedColours != null) {
						javaComplexBlendedTile.vertexColours!![vertexIndex] = javaComplexBlendedTile.vertexColours!![vertexIndex] or (vertexLightLevel shl 25)
					}
				} else if (blendedColours == null) {
					javaComplexBlendedTile.vertexColours!![vertexIndex] = 0
				} else {
					javaComplexBlendedTile.vertexColours!![vertexIndex] = vertexLightLevel shl 25
				}
				vertexIndex++
			}
			@Pc(2011) var hasTextures: Boolean = false
			var faceIndex: Int = 0
			while (faceIndex < javaComplexBlendedTile.faceCount) {
				if (textures!![faceIndex * 3] != -1 && !this.toolkit.materialProvider!!.getMetrics(textures[faceIndex * 3])!!.detailsOnly) {
					hasTextures = true
				}
				faceIndex++
			}
			if (blendedColours != null) {
				javaComplexBlendedTile.faceBlendedColours = IntArray(javaComplexBlendedTile.faceCount.toInt())
			}
			if (hasTextures) {
				javaComplexBlendedTile.faceTextures = ShortArray(javaComplexBlendedTile.faceCount.toInt())
				javaComplexBlendedTile.faceSizes = ShortArray(javaComplexBlendedTile.faceCount.toInt())
			}
			faceIndex = 0
			while (faceIndex < javaComplexBlendedTile.faceCount) {
				val vertexA: Int = faceIndex * 3
				if (blendedColours != null && blendedColours[vertexA] != 0) {
					javaComplexBlendedTile.faceBlendedColours!![faceIndex] = blendedColours[vertexA] shr 8
				}
				if (hasTextures) {
					val vertexB: Int = vertexA + 1
					val vertexC: Int = vertexB + 1
					@Pc(2093) var anyTextured: Boolean = false
					@Pc(2095) var allTextured: Boolean = true
					@Pc(2099) var texture: Int = textures!![vertexA]
					if (texture == -1 || this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
						allTextured = false
					} else {
						anyTextured = true
					}
					texture = textures[vertexB]
					if (texture == -1 || this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
						allTextured = false
					} else {
						anyTextured = true
					}
					texture = textures[vertexC]
					if (texture == -1 || this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
						allTextured = false
					} else {
						anyTextured = true
					}
					if (allTextured) {
						javaComplexBlendedTile.faceTextures!![faceIndex] = texture.toShort()
						javaComplexBlendedTile.faceSizes!![faceIndex] = sizes!![vertexA].toShort()
					} else {
						if (anyTextured) {
							texture = textures[vertexA]
							if (texture != -1 && !this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
								javaComplexBlendedTile.vertexColours!![vertexA] = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.toolkit.materialProvider!!.getMetrics(texture)!!.colorHsl.toInt() and 0xFFFF).toInt() and 0xFFFF]
							}
							texture = textures[vertexB]
							if (texture != -1 && !this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
								javaComplexBlendedTile.vertexColours!![vertexB] = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.toolkit.materialProvider!!.getMetrics(texture)!!.colorHsl.toInt() and 0xFFFF).toInt() and 0xFFFF]
							}
							texture = textures[vertexC]
							if (texture != -1 && !this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
								javaComplexBlendedTile.vertexColours!![vertexC] = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.toolkit.materialProvider!!.getMetrics(texture)!!.colorHsl.toInt() and 0xFFFF).toInt() and 0xFFFF]
							}
						}
						javaComplexBlendedTile.faceTextures!![faceIndex] = -1
					}
				}
				faceIndex++
			}
			this.complexBlendedTiles!![x][z] = javaComplexBlendedTile
			return
		}
		@Pc(842) val javaSimpleBlendedTile: JavaSimpleBlendedTile = JavaSimpleBlendedTile()
		val colour: Int = colours[0]
		val texture: Int = textures!![0]
		if (blendedColours != null) {
			javaSimpleBlendedTile.blendedColour = blendedColours[0] shr 8
			if (colour == 0) {
				javaSimpleBlendedTile.flags = (javaSimpleBlendedTile.flags.toInt() or 0x2).toByte()
			}
		} else if (colour == 0) {
			return
		}
		if (this.tileHeights!![x]!![z] == this.tileHeights[x + 1]!![z] && this.tileHeights[x]!![z] == this.tileHeights[x + 1]!![z + 1] && this.tileHeights[x]!![z] == this.tileHeights[x]!![z + 1]) {
			javaSimpleBlendedTile.flags = (javaSimpleBlendedTile.flags.toInt() or 0x1).toByte()
		}
		if (texture == -1 || (javaSimpleBlendedTile.flags.toInt() and 0x2) != 0 || this.toolkit.materialProvider!!.getMetrics(texture)!!.detailsOnly) {
			var fogAlpha: Int
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[southWestVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourSw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southWestVertex] shr 8, this.lightLevels!![x][z] - this.shadowLevels!![x][z]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSw = javaSimpleBlendedTile.colourSw or ((this.shadowLevels!![x][z] + 255 - this.lightLevels!![x][z]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[southEastVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourSe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southEastVertex] shr 8, this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSe = javaSimpleBlendedTile.colourSe or ((this.shadowLevels!![x + 1][z] + 255 - this.lightLevels!![x + 1][z]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[northEastVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourNe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northEastVertex] shr 8, this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNe = javaSimpleBlendedTile.colourNe or ((this.shadowLevels!![x + 1][z + 1] + 255 - this.lightLevels!![x + 1][z + 1]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[northWestVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourNw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northWestVertex] shr 8, this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNw = javaSimpleBlendedTile.colourNw or ((this.shadowLevels!![x][z + 1] + 255 - this.lightLevels!![x][z + 1]) shl 25)
			}
			javaSimpleBlendedTile.texture = -1
		} else {
			var fogAlpha: Int
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[southWestVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourSw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southWestVertex] shr 8, this.lightLevels!![x][z] - this.shadowLevels!![x][z]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSw = javaSimpleBlendedTile.colourSw or ((this.shadowLevels!![x][z] + 255 - this.lightLevels!![x][z]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[southEastVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourSe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southEastVertex] shr 8, this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSe = javaSimpleBlendedTile.colourSe or ((this.shadowLevels!![x + 1][z] + 255 - this.lightLevels!![x + 1][z]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[northEastVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourNe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northEastVertex] shr 8, this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]), fogAlpha, fogColour)
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNe = javaSimpleBlendedTile.colourNe or ((this.shadowLevels!![x + 1][z + 1] + 255 - this.lightLevels!![x + 1][z + 1]) shl 25)
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0
			} else {
				fogAlpha = waterDepths[northWestVertex] * 255 / maxWaterDepth
				if (fogAlpha < 0) {
					fogAlpha = 0
				} else if (fogAlpha > 255) {
					fogAlpha = 255
				}
			}
			javaSimpleBlendedTile.colourNw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northWestVertex] shr 8, this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]), fogAlpha, fogColour)
			javaSimpleBlendedTile.texture = texture.toShort()
		}
		if (waterDepths != null) {
			javaSimpleBlendedTile.waterDepthNe = waterDepths[northEastVertex].toShort()
			javaSimpleBlendedTile.waterDepthNw = waterDepths[northWestVertex].toShort()
			javaSimpleBlendedTile.waterDepthSe = waterDepths[southEastVertex].toShort()
			javaSimpleBlendedTile.waterDepthSw = waterDepths[southWestVertex].toShort()
		}
		this.simpleBlendedTiles!![x][z] = javaSimpleBlendedTile
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	override fun addTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetX: IntArray?, @OriginalArg(3) offsetLevel: IntArray?, @OriginalArg(4) offsetY: IntArray?, @OriginalArg(5) depths: IntArray?, @OriginalArg(6) faceA: IntArray?, @OriginalArg(7) faceB: IntArray?, @OriginalArg(8) faceC: IntArray?, @OriginalArg(9) colours: IntArray?, @OriginalArg(10) blendedColours: IntArray?, @OriginalArg(11) textures: IntArray?, @OriginalArg(12) sizes: IntArray?, @OriginalArg(13) waterColour: Int, @OriginalArg(14) waterDepth: Int, @OriginalArg(15) waterBias: Int) {
		if (this.simpleTiles == null) {
			this.simpleTiles = Array(this.width) { arrayOfNulls<PlainTile>(this.length) }
			this.complexTiles = Array(this.width) { arrayOfNulls<JavaComplexTile>(this.length) }
		} else if (this.simpleBlendedTiles != null || this.genericBlendedTiles != null) {
			throw IllegalStateException()
		}
		@Pc(29) var simple: Boolean = false
		@Pc(67) var local67: Int
		@Pc(73) var local73: Int
		if (colours!!.size == 2 && faceA!!.size == 2 && (colours[0] == colours[1] || textures!![0] != -1 && textures[0] == textures[1])) {
			simple = true
			for (@Pc(60) i in 1 until 2) {
				val vertexX: Int = offsetX!![faceA[i]]
				val vertexZ: Int = offsetY!![faceA[i]]
				if (vertexX != 0 && vertexX != this.tileUnits || vertexZ != 0 && vertexZ != this.tileUnits) {
					simple = false
					break
				}
			}
		}
		if (!simple) {
			@Pc(98) val javaComplexTile: JavaComplexTile = JavaComplexTile()
			@Pc(102) val vertexCount: Short = offsetX!!.size.toShort()
			@Pc(106) val length: Short = colours.size.toShort()
			javaComplexTile.vertexCount = vertexCount
			javaComplexTile.verticesLight = ShortArray(vertexCount.toInt())
			javaComplexTile.verticesX = ShortArray(vertexCount.toInt())
			javaComplexTile.verticesY = ShortArray(vertexCount.toInt())
			javaComplexTile.verticesZ = ShortArray(vertexCount.toInt())
			@Pc(136) var local136: Int
			@Pc(291) var local291: Int
			@Pc(338) var local338: Int
			for (@Pc(127) j in 0 until vertexCount.toInt()) {
				@Pc(132) val vertexX: Int = offsetX[j]
				val vertexZ: Int = offsetY!![j]
				if (vertexX == 0 && vertexZ == 0) {
					javaComplexTile.verticesLight!![j] = (this.lightLevels!![x][z] - this.shadowLevels!![x][z]).toShort()
				} else if (vertexX == 0 && vertexZ == this.tileUnits) {
					javaComplexTile.verticesLight!![j] = (this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]).toShort()
				} else if (vertexX == this.tileUnits && vertexZ == this.tileUnits) {
					javaComplexTile.verticesLight!![j] = (this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]).toShort()
				} else if (vertexX == this.tileUnits && vertexZ == 0) {
					javaComplexTile.verticesLight!![j] = (this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]).toShort()
				} else {
					val southLight: Int = (this.lightLevels!![x][z] - this.shadowLevels!![x][z]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]) * vertexX
					val northLight: Int = (this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]) * (this.tileUnits - vertexX) + (this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]) * vertexX
					javaComplexTile.verticesLight!![j] = ((southLight * (this.tileUnits - vertexZ) + northLight * vertexZ) shr (this.tileScale * 2)).toShort()
				}
				val worldX: Int = (x shl this.tileScale) + vertexX
				val worldZ: Int = (z shl this.tileScale) + vertexZ
				javaComplexTile.verticesX!![j] = vertexX.toShort()
				javaComplexTile.verticesZ!![j] = vertexZ.toShort()
				javaComplexTile.verticesY!![j] = (this.averageHeight(worldX, worldZ) + (if (offsetLevel == null) 0 else offsetLevel[j])).toShort()
				if (javaComplexTile.verticesLight!![j] < 2) {
					javaComplexTile.verticesLight!![j] = 2
				}
			}
			@Pc(419) var textured: Boolean = false
			var faceCount: Int = 0
			for (faceIndex in 0 until length.toInt()) {
				if (colours[faceIndex] >= 0 || blendedColours != null && blendedColours[faceIndex] >= 0) {
					faceCount++
				}
				val texture: Int = textures!![faceIndex]
				if (texture != -1) {
					@Pc(449) val textureMetrics: MaterialType = this.toolkit.materialProvider!!.getMetrics(texture)!!
					if (!textureMetrics.detailsOnly) {
						textured = true
						if (this.isWaterEffect(textureMetrics.effectType.toInt()) || textureMetrics.speedU.toInt() != 0 || textureMetrics.speedV.toInt() != 0) {
							javaComplexTile.flags = (javaComplexTile.flags.toInt() or 0x4).toByte()
						}
					}
				}
			}
			javaComplexTile.faceColours = IntArray(faceCount)
			if (blendedColours != null) {
				javaComplexTile.faceBlendedColours = IntArray(faceCount)
			}
			javaComplexTile.faceA = ShortArray(faceCount)
			javaComplexTile.faceB = ShortArray(faceCount)
			javaComplexTile.faceC = ShortArray(faceCount)
			if (textured) {
				javaComplexTile.faceTextures = ShortArray(faceCount)
				javaComplexTile.faceSizes = ShortArray(faceCount)
			}
			for (faceIndex in 0 until length.toInt()) {
				if (colours[faceIndex] >= 0 || blendedColours != null && blendedColours[faceIndex] >= 0) {
					if (colours[faceIndex] >= 0) {
						javaComplexTile.faceColours!![javaComplexTile.faceCount.toInt()] = ColorUtil.hslToHsv(colours[faceIndex]).toInt()
					} else {
						javaComplexTile.faceColours!![javaComplexTile.faceCount.toInt()] = -1
					}
					if (blendedColours != null) {
						if (blendedColours[faceIndex] == -1) {
							javaComplexTile.faceBlendedColours!![javaComplexTile.faceCount.toInt()] = -1
						} else {
							javaComplexTile.faceBlendedColours!![javaComplexTile.faceCount.toInt()] = ColorUtil.hslToHsv(blendedColours[faceIndex]).toInt()
						}
					}
					javaComplexTile.faceA!![javaComplexTile.faceCount.toInt()] = faceA!![faceIndex].toShort()
					javaComplexTile.faceB!![javaComplexTile.faceCount.toInt()] = faceB!![faceIndex].toShort()
					javaComplexTile.faceC!![javaComplexTile.faceCount.toInt()] = faceC!![faceIndex].toShort()
					if (textured) {
						if (textures!![faceIndex] == -1 || this.toolkit.materialProvider!!.getMetrics(textures[faceIndex])!!.detailsOnly) {
							javaComplexTile.faceTextures!![javaComplexTile.faceCount.toInt()] = -1
						} else {
							javaComplexTile.faceTextures!![javaComplexTile.faceCount.toInt()] = textures[faceIndex].toShort()
							javaComplexTile.faceSizes!![javaComplexTile.faceCount.toInt()] = sizes!![faceIndex].toShort()
						}
					}
					javaComplexTile.faceCount++
				}
			}
			this.complexTiles!![x][z] = javaComplexTile
		} else if (colours[0] >= 0 || blendedColours != null && blendedColours[0] >= 0) {
			@Pc(668) val javaSimpleTile: PlainTile = PlainTile()
			val colour: Int = colours[0]
			val texture: Int = textures!![0]
			if (blendedColours != null) {
				javaSimpleTile.color = JavaModel.applyLightness(this.lightLevels!![x][z] - this.shadowLevels!![x][z], ColorUtil.hslToHsv(blendedColours[0]).toInt())
				if (colour == -1) {
					javaSimpleTile.functionMask = (javaSimpleTile.functionMask.toInt() or 0x2).toByte()
				}
			}
			if (this.tileHeights!![x]!![z] == this.tileHeights[x + 1]!![z] && this.tileHeights[x]!![z] == this.tileHeights[x + 1]!![z + 1] && this.tileHeights[x]!![z] == this.tileHeights[x]!![z + 1]) {
				javaSimpleTile.functionMask = (javaSimpleTile.functionMask.toInt() or 0x1).toByte()
			}
			@Pc(766) var textureMetrics2: MaterialType? = null
			if (texture != -1) {
				textureMetrics2 = this.toolkit.materialProvider!!.getMetrics(texture)
			}
			if (textureMetrics2 == null || (javaSimpleTile.functionMask.toInt() and 0x2) != 0 || textureMetrics2.detailsOnly) {
				@Pc(893) val hsl: Short = ColorUtil.hslToHsv(colour)
				javaSimpleTile.hslSw = JavaModel.applyLightness(this.lightLevels!![x][z] - this.shadowLevels!![x][z], hsl.toInt()).toShort()
				javaSimpleTile.hslSe = JavaModel.applyLightness(this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z], hsl.toInt()).toShort()
				javaSimpleTile.hslNe = JavaModel.applyLightness(this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1], hsl.toInt()).toShort()
				javaSimpleTile.hslNw = JavaModel.applyLightness(this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1], hsl.toInt()).toShort()
				javaSimpleTile.texture = -1
			} else {
				javaSimpleTile.hslSw = (this.lightLevels!![x][z] - this.shadowLevels!![x][z]).toShort()
				javaSimpleTile.hslSe = (this.lightLevels!![x + 1][z] - this.shadowLevels!![x + 1][z]).toShort()
				javaSimpleTile.hslNe = (this.lightLevels!![x + 1][z + 1] - this.shadowLevels!![x + 1][z + 1]).toShort()
				javaSimpleTile.hslNw = (this.lightLevels!![x][z + 1] - this.shadowLevels!![x][z + 1]).toShort()
				javaSimpleTile.texture = texture.toShort()
				if (this.isWaterEffect(textureMetrics2.effectType.toInt()) || textureMetrics2.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0) {
					javaSimpleTile.functionMask = (javaSimpleTile.functionMask.toInt() or 0x4).toByte()
				}
			}
			this.simpleTiles!![x][z] = javaSimpleTile
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III[[ZZII)V")
	override fun renderFloor(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) depth: Int, @OriginalArg(6) skipFlags: Int) {
		@Pc(3) val javaMatrix: JavaMatrix = this.toolkit.camera!!
		this.depthOverride = depth
		this.cameraE1_1 = javaMatrix.e1_1
		this.cameraE1_2 = javaMatrix.e1_2
		this.cameraE1_3 = javaMatrix.e1_3
		this.cameraTx = javaMatrix.tx
		this.cameraE2_1 = javaMatrix.e2_1
		this.cameraE2_2 = javaMatrix.e2_2
		this.cameraE2_3 = javaMatrix.e2_3
		this.cameraTy = javaMatrix.ty
		this.cameraE3_1 = javaMatrix.e3_1
		this.cameraE3_2 = javaMatrix.e3_2
		this.cameraE3_3 = javaMatrix.e3_3
		this.cameraTz = javaMatrix.tz
		for (@Pc(56) i in 0 until radius + radius) {
			for (@Pc(59) j in 0 until radius + radius) {
				if (visibility!![i]!![j]) {
					@Pc(72) val tileX: Int = x + i - radius
					@Pc(78) val tileZ: Int = z + j - radius
					if (tileX >= 0 && tileX < this.width && tileZ >= 0 && tileZ < this.length) {
						this.renderTile(tileX, tileZ, skipFlags)
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "ka", descriptor = "(III)V")
	override fun ka(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadowLevel: Int) {
		if (this.shadowLevels!![x][z] < shadowLevel) {
			this.shadowLevels!![x][z] = shadowLevel.toByte()
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIZLclient!pga;Lclient!mp;[I[I[I[II)V")
	private fun renderBlendedTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) water: Boolean, @OriginalArg(3) resource: JavaThreadResource, @OriginalArg(4) rasterizer: Rasterizer, @OriginalArg(5) screenX: IntArray, @OriginalArg(6) screenY: IntArray, @OriginalArg(7) depths: IntArray, @OriginalArg(8) fogLevels: IntArray, @OriginalArg(9) skipFlags: Int) {
		@Pc(6) val javaSimpleBlendedTile: JavaSimpleBlendedTile? = this.simpleBlendedTiles!![x][z]
		@Pc(41) var local41: Int
		@Pc(46) var local46: Int
		@Pc(51) var local51: Int
		@Pc(544) var local544: Int
		@Pc(394) var local394: Int
		@Pc(454) var local454: Int
		@Pc(514) var local514: Int
		@Pc(574) var local574: Int
		if (javaSimpleBlendedTile == null) {
			@Pc(3030) val javaComplexBlendedTile: JavaComplexBlendedTile? = this.complexBlendedTiles!![x][z]
			if (javaComplexBlendedTile != null) {
				if (skipFlags != 0) {
					if ((javaComplexBlendedTile.flags.toInt() and 0x4) == 0) {
						if ((skipFlags and 0x2) != 0) {
							return
						}
					} else if ((skipFlags and 0x1) != 0) {
						return
					}
				}
				@Pc(3196) var viewX: Float
				@Pc(3217) var viewY: Float
				@Pc(3102) var viewZ: Float
				if (this.depthOverride == -1) {
					var vertexIndex: Int = 0
					while (vertexIndex < javaComplexBlendedTile.vertexCount) {
						val vertexX: Int = javaComplexBlendedTile.verticesX!![vertexIndex] + (x shl this.tileScale)
						var vertexY: Int = javaComplexBlendedTile.verticesY!![vertexIndex].toInt()
						val vertexZ: Int = javaComplexBlendedTile.verticesZ!![vertexIndex] + (z shl this.tileScale)
						viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
						if (viewZ <= this.toolkit.zNear.toFloat()) {
							return
						}
						fogLevels[vertexIndex] = 0
						if (water) {
							var fogLevel: Int = (viewZ - resource.fogPlane.toFloat()).toInt()
							if (fogLevel > 255) {
								fogLevel = 255
							}
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel
								val waterDepthOffset: Int = javaComplexBlendedTile.waterDepths!![vertexIndex] * fogLevel / 255
								if (waterDepthOffset > 0) {
									vertexY -= waterDepthOffset
								}
							}
						} else if (resource.fogActive) {
							val fogLevel: Int = (viewZ - resource.fogPlane.toFloat()).toInt()
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel
								if (fogLevels[vertexIndex] > 255) {
									fogLevels[vertexIndex] = 255
								}
							}
						}
						viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
						viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
						screenX[vertexIndex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / viewZ).toInt()
						screenY[vertexIndex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / viewZ).toInt()
						depths[vertexIndex] = viewZ.toInt()
						vertexIndex++
					}
				} else {
					var vertexIndex: Int = 0
					while (vertexIndex < javaComplexBlendedTile.vertexCount) {
						val vertexX: Int = javaComplexBlendedTile.verticesX!![vertexIndex] + (x shl this.tileScale)
						var vertexY: Int = javaComplexBlendedTile.verticesY!![vertexIndex].toInt()
						val vertexZ: Int = javaComplexBlendedTile.verticesZ!![vertexIndex] + (z shl this.tileScale)
						viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
						fogLevels[vertexIndex] = 0
						if (water) {
							var fogLevel: Int = this.depthOverride - resource.fogPlane
							if (fogLevel > 255) {
								fogLevel = 255
							}
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel
								val waterDepthOffset: Int = javaComplexBlendedTile.waterDepths!![vertexIndex] * fogLevel / 255
								if (waterDepthOffset > 0) {
									vertexY -= waterDepthOffset
								}
							}
						} else if (resource.fogActive) {
							val fogLevel: Int = this.depthOverride - resource.fogPlane
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel
								if (fogLevels[vertexIndex] > 255) {
									fogLevels[vertexIndex] = 255
								}
							}
						}
						viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
						viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
						screenX[vertexIndex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
						screenY[vertexIndex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
						depths[vertexIndex] = viewZ.toInt()
						vertexIndex++
					}
				}
				@Pc(3486) var xB: Int
				@Pc(3490) var xC: Int
				@Pc(3494) var yA: Int
				@Pc(3498) var yB: Int
				@Pc(3502) var yC: Int
				@Pc(3514) var fogTotal: Int
				if (javaComplexBlendedTile.faceTextures != null) {
					@Pc(3558) var texture: Short
					@Pc(3568) var alphaMask: Int
					if (this.depthOverride == -1) {
						var faceIndex: Int = 0
						while (faceIndex < javaComplexBlendedTile.faceCount) {
							val vertexA: Int = faceIndex * 3
							val vertexB: Int = vertexA + 1
							val vertexC: Int = vertexB + 1
							val xA: Int = screenX[vertexA]
							xB = screenX[vertexB]
							xC = screenX[vertexC]
							yA = screenY[vertexA]
							yB = screenY[vertexB]
							yC = screenY[vertexC]
							fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC]
							if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
								rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
								texture = javaComplexBlendedTile.faceTextures!![faceIndex]
								if (fogTotal >= 765) {
									rasterizer.renderFlatTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), resource.fogColour)
								} else if (fogTotal > 0) {
									if (texture.toInt() != -1) {
										alphaMask = -16777216
										if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
											alphaMask = -1694498816
										}
										rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.verticesX!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexC].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexC].toFloat() / this.tileUnits.toFloat(), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexB] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexC] and 0xFFFFFF), resource.fogColour, fogLevels[vertexA], fogLevels[vertexB], fogLevels[vertexC], texture.toInt())
									} else if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
										if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
											rasterizer.alpha = -1694498816
										}
										rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexA], (fogLevels[vertexA] shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexB], (fogLevels[vertexB] shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexC], (fogLevels[vertexC] shl 24) or resource.fogColour))
										rasterizer.alpha = 0
									}
								} else if (texture.toInt() != -1) {
									alphaMask = -16777216
									if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
										alphaMask = -1694498816
									}
									rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.verticesX!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexC].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexC].toFloat() / this.tileUnits.toFloat(), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexB] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexC] and 0xFFFFFF), 0, 0, 0, 0, texture.toInt())
								} else if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
									if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
										rasterizer.alpha = -1694498816
									}
									rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.vertexColours!![vertexA], javaComplexBlendedTile.vertexColours!![vertexB], javaComplexBlendedTile.vertexColours!![vertexC])
									rasterizer.alpha = 0
								}
							}
							faceIndex++
						}
						return
					}
					var faceIndex: Int = 0
					while (faceIndex < javaComplexBlendedTile.faceCount) {
						val vertexA: Int = faceIndex * 3
						val vertexB: Int = vertexA + 1
						val vertexC: Int = vertexB + 1
						val xA: Int = screenX[vertexA]
						xB = screenX[vertexB]
						xC = screenX[vertexC]
						yA = screenY[vertexA]
						yB = screenY[vertexB]
						yC = screenY[vertexC]
						fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC]
						if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
							texture = javaComplexBlendedTile.faceTextures!![faceIndex]
							if (fogTotal < 765) {
								if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
									rasterizer.alpha = -1694498816
								}
								if (fogTotal > 0) {
									if (texture.toInt() != -1) {
										alphaMask = -16777216
										if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
											alphaMask = -1694498816
										}
										rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.verticesX!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexC].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexC].toFloat() / this.tileUnits.toFloat(), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexB] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexC] and 0xFFFFFF), resource.fogColour, fogLevels[vertexA], fogLevels[vertexB], fogLevels[vertexC], texture.toInt())
									} else if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
										if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
											rasterizer.alpha = -1694498816
										}
										rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexA], (fogLevels[vertexA] shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexB], (fogLevels[vertexB] shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours!![vertexC], (fogLevels[vertexC] shl 24) or resource.fogColour))
										rasterizer.alpha = 0
									}
								} else if (texture.toInt() != -1) {
									alphaMask = -16777216
									if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
										alphaMask = -1694498816
									}
									rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.verticesX!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesX!![vertexC].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexA].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexB].toFloat() / this.tileUnits.toFloat(), javaComplexBlendedTile.verticesZ!![vertexC].toFloat() / this.tileUnits.toFloat(), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexB] and 0xFFFFFF), alphaMask or (javaComplexBlendedTile.vertexColours!![vertexC] and 0xFFFFFF), 0, 0, 0, 0, texture.toInt())
								} else if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
									if (texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(texture.toInt())!!.effectType.toInt())) {
										rasterizer.alpha = -1694498816
									}
									rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.vertexColours!![vertexA], javaComplexBlendedTile.vertexColours!![vertexB], javaComplexBlendedTile.vertexColours!![vertexC])
									rasterizer.alpha = 0
								}
								rasterizer.alpha = 0
							} else {
								rasterizer.renderFlatTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), resource.fogColour)
							}
						}
						faceIndex++
					}
					return
				}
				var faceIndex: Int = 0
				while (faceIndex < javaComplexBlendedTile.faceCount) {
					val vertexA: Int = faceIndex * 3
					val vertexB: Int = vertexA + 1
					val vertexC: Int = vertexB + 1
					val xA: Int = screenX[vertexA]
					xB = screenX[vertexB]
					xC = screenX[vertexC]
					yA = screenY[vertexA]
					yB = screenY[vertexB]
					yC = screenY[vertexC]
					fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC]
					if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
						rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
						if (fogTotal >= 765) {
							rasterizer.renderFlatTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), resource.fogColour)
						} else if (fogTotal > 0) {
							if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
								rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), ColourUtils.blendColours(javaComplexBlendedTile.vertexColours!![vertexA], fogLevels[vertexA], resource.fogColour), ColourUtils.blendColours(javaComplexBlendedTile.vertexColours!![vertexB], fogLevels[vertexB], resource.fogColour), ColourUtils.blendColours(javaComplexBlendedTile.vertexColours!![vertexC], fogLevels[vertexC], resource.fogColour))
							}
						} else if ((javaComplexBlendedTile.vertexColours!![vertexA] and 0xFFFFFF) != 0) {
							rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[vertexA].toFloat(), depths[vertexB].toFloat(), depths[vertexC].toFloat(), javaComplexBlendedTile.vertexColours!![vertexA], javaComplexBlendedTile.vertexColours!![vertexB], javaComplexBlendedTile.vertexColours!![vertexC])
						}
					}
					faceIndex++
				}
			}
		} else if ((javaSimpleBlendedTile.flags.toInt() and 0x2) == 0) {
			if (skipFlags != 0) {
				if ((javaSimpleBlendedTile.flags.toInt() and 0x4) == 0) {
					if ((skipFlags and 0x2) != 0) {
						return
					}
				} else if ((skipFlags and 0x1) != 0) {
					return
				}
			}
			@Pc(36) val minX: Int = x * this.tileUnits
			val maxX: Int = minX + this.tileUnits
			val minZ: Int = z * this.tileUnits
			val maxZ: Int = minZ + this.tileUnits
			@Pc(53) var fogSw: Int = 0
			@Pc(55) var fogSe: Int = 0
			@Pc(57) var fogNe: Int = 0
			@Pc(59) var fogNw: Int = 0
			@Pc(100) val depthSw: Float
			@Pc(125) val depthSe: Float
			@Pc(150) val depthNe: Float
			@Pc(175) val depthNw: Float
			@Pc(261) var fog: Int
			@Pc(73) var heightSw: Int
			@Pc(848) var local848: Int
			@Pc(859) var local859: Int
			@Pc(364) val screenXSw: Int
			@Pc(424) val screenXSe: Int
			@Pc(484) val screenXNe: Int
			@Pc(351) val viewXSw: Float
			@Pc(411) val viewXSe: Float
			@Pc(471) val viewXNe: Float
			@Pc(531) val viewXNw: Float
			@Pc(381) val viewYSw: Float
			@Pc(441) val viewYSe: Float
			@Pc(501) val viewYNe: Float
			@Pc(561) val viewYNw: Float
			val screenXNw: Int
			val screenYSw: Int
			val screenYSe: Int
			val screenYNe: Int
			val screenYNw: Int
			if ((javaSimpleBlendedTile.flags.toInt() and 0x1) == 0 || water) {
				heightSw = this.tileHeights!![x]!![z]
				var heightSe: Int = this.tileHeights[x + 1]!![z]
				var heightNe: Int = this.tileHeights[x + 1]!![z + 1]
				@Pc(868) var heightNw: Int = this.tileHeights[x]!![z + 1]
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightSw.toFloat() + this.cameraE3_3 * minZ.toFloat()
					if (depthSw <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightSe.toFloat() + this.cameraE3_3 * minZ.toFloat()
					if (depthSe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightNe.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					if (depthNe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightNw.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					if (depthNw <= this.toolkit.zNear.toFloat()) {
						return
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightSw.toFloat() + this.cameraE3_3 * minZ.toFloat()
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightSe.toFloat() + this.cameraE3_3 * minZ.toFloat()
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightNe.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightNw.toFloat() + this.cameraE3_3 * maxZ.toFloat()
				}
				if (water) {
					fog = (depthSw - resource.fogPlane.toFloat()).toInt()
					if (fog > 255) {
						fog = 255
					}
					@Pc(1097) var waterDepth: Int
					if (fog > 0) {
						fogSw = fog
						waterDepth = javaSimpleBlendedTile.waterDepthSw * fog / 255
						if (waterDepth > 0) {
							heightSw -= waterDepth
						}
					}
					fog = (depthSe - resource.fogPlane.toFloat()).toInt()
					if (fog > 255) {
						fog = 255
					}
					if (fog > 0) {
						fogSe = fog
						waterDepth = javaSimpleBlendedTile.waterDepthSe * fog / 255
						if (waterDepth > 0) {
							heightSe -= waterDepth
						}
					}
					fog = (depthNe - resource.fogPlane.toFloat()).toInt()
					if (fog > 255) {
						fog = 255
					}
					if (fog > 0) {
						fogNe = fog
						waterDepth = javaSimpleBlendedTile.waterDepthNe * fog / 255
						if (waterDepth > 0) {
							heightNe -= waterDepth
						}
					}
					fog = (depthNw - resource.fogPlane.toFloat()).toInt()
					if (fog > 255) {
						fog = 255
					}
					if (fog > 0) {
						fogNw = fog
						waterDepth = javaSimpleBlendedTile.waterDepthNw * fog / 255
						if (waterDepth > 0) {
							heightNw -= waterDepth
						}
					}
				} else if (resource.fogActive) {
					fog = (depthSw - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogSw = fog
						if (fog > 255) {
							fogSw = 255
						}
					}
					fog = (depthSe - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogSe = fog
						if (fog > 255) {
							fogSe = 255
						}
					}
					fog = (depthNe - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogNe = fog
						if (fog > 255) {
							fogNe = 255
						}
					}
					fog = (depthNw - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogNw = fog
						if (fog > 255) {
							fogNw = 255
						}
					}
				}
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightSw.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / depthSw).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightSw.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / depthSw).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightSe.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / depthSe).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightSe.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / depthSe).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightNe.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / depthNe).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightNe.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / depthNe).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightNw.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / depthNw).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightNw.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / depthNw).toInt()
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightSw.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightSw.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightSe.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightSe.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightNe.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightNe.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightNw.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightNw.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
				}
			} else {
				heightSw = this.tileHeights!![x]!![z]
				@Pc(79) val heightZ: Float = this.cameraE3_2 * heightSw.toFloat()
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					if (depthSw <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					if (depthSe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					if (depthNe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					if (depthNw <= this.toolkit.zNear.toFloat()) {
						return
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
				}
				if (resource.fogActive) {
					fog = (depthSw - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogSw = fog
						if (fog > 255) {
							fogSw = 255
						}
					}
					fog = (depthSe - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogSe = fog
						if (fog > 255) {
							fogSe = 255
						}
					}
					fog = (depthNe - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogNe = fog
						if (fog > 255) {
							fogNe = 255
						}
					}
					fog = (depthNw - resource.fogPlane.toFloat()).toInt()
					if (fog > 0) {
						fogNw = fog
						if (fog > 255) {
							fogNw = 255
						}
					}
				}
				@Pc(324) val heightX: Float = this.cameraE1_2 * heightSw.toFloat()
				@Pc(330) val heightY: Float = this.cameraE2_2 * heightSw.toFloat()
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / depthSw).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / depthSw).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / depthSe).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / depthSe).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / depthNe).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / depthNe).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / depthNw).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / depthNw).toInt()
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
				}
			}
			@Pc(1842) val waterEffect: Boolean = javaSimpleBlendedTile.texture.toInt() != -1 && this.isWaterEffect(this.toolkit.materialProvider!!.getMetrics(javaSimpleBlendedTile.texture.toInt())!!.effectType.toInt())
			if (this.depthOverride == -1) {
				var fogTotal: Int = fogSe + fogNe + fogNw
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width
					if (fogTotal >= 765) {
						rasterizer.renderFlatTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, resource.fogColour)
					} else if (fogTotal > 0) {
						if (javaSimpleBlendedTile.texture >= 0) {
							var alphaMask: Int = -16777216
							if (waterEffect) {
								alphaMask = -1694498816
							}
							rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, alphaMask or (javaSimpleBlendedTile.colourNe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), resource.fogColour, fogNe, fogNw, fogSe, javaSimpleBlendedTile.texture.toInt())
						} else {
							if (waterEffect) {
								rasterizer.alpha = 100
							}
							rasterizer.renderTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNe, (fogNe shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, (fogNw shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, (fogSe shl 24) or resource.fogColour))
							rasterizer.alpha = 0
						}
					} else if (javaSimpleBlendedTile.texture >= 0) {
						var alphaMask: Int = -16777216
						if (waterEffect) {
							alphaMask = -1694498816
						}
						rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, alphaMask or (javaSimpleBlendedTile.colourNe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), 0, 0, 0, 0, javaSimpleBlendedTile.texture.toInt())
					} else {
						if (waterEffect) {
							rasterizer.alpha = 100
						}
						rasterizer.renderTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe)
						rasterizer.alpha = 0
					}
				}
				fogTotal = fogSw + fogSe + fogNw
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								var alphaMask: Int = -16777216
								if (waterEffect) {
									alphaMask = -1694498816
								}
								rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, alphaMask or (javaSimpleBlendedTile.colourSw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), resource.fogColour, fogSw, fogSe, fogNw, javaSimpleBlendedTile.texture.toInt())
								return
							}
							if (waterEffect) {
								rasterizer.alpha = 100
							}
							rasterizer.renderTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSw, (fogSw shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, (fogSe shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, (fogNw shl 24) or resource.fogColour))
							rasterizer.alpha = 0
							return
						}
						if (javaSimpleBlendedTile.texture >= 0) {
							var alphaMask: Int = -16777216
							if (waterEffect) {
								alphaMask = -1694498816
							}
							rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, alphaMask or (javaSimpleBlendedTile.colourSw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), 0, 0, 0, 0, javaSimpleBlendedTile.texture.toInt())
							return
						}
						if (waterEffect) {
							rasterizer.alpha = 100
						}
						rasterizer.renderTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw)
						rasterizer.alpha = 0
						return
					}
					rasterizer.renderFlatTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, resource.fogColour)
					return
				}
			} else {
				var fogTotal: Int = fogSe + fogNe + fogNw
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								var alphaMask: Int = -16777216
								if (waterEffect) {
									alphaMask = -1694498816
								}
								rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, alphaMask or (javaSimpleBlendedTile.colourNe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), resource.fogColour, fogNe, fogNw, fogSe, javaSimpleBlendedTile.texture.toInt())
							} else {
								if (waterEffect) {
									rasterizer.alpha = 100
								}
								rasterizer.renderTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNe, (fogNe shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, (fogNw shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, (fogSe shl 24) or resource.fogColour))
								rasterizer.alpha = 0
							}
						} else if (javaSimpleBlendedTile.texture >= 0) {
							var alphaMask: Int = -16777216
							if (waterEffect) {
								alphaMask = -1694498816
							}
							rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, alphaMask or (javaSimpleBlendedTile.colourNe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), 0, 0, 0, 0, javaSimpleBlendedTile.texture.toInt())
						} else {
							if (waterEffect) {
								rasterizer.alpha = 100
							}
							rasterizer.renderTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe)
							rasterizer.alpha = 0
						}
					} else {
						rasterizer.renderFlatTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, resource.fogColour)
					}
				}
				fogTotal = fogSw + fogSe + fogNw
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								var alphaMask: Int = -16777216
								if (waterEffect) {
									alphaMask = -1694498816
								}
								rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, alphaMask or (javaSimpleBlendedTile.colourSw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), resource.fogColour, fogSw, fogSe, fogNw, javaSimpleBlendedTile.texture.toInt())
								return
							}
							if (waterEffect) {
								rasterizer.alpha = 100
							}
							rasterizer.renderTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSw, (fogSw shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, (fogSe shl 24) or resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, (fogNw shl 24) or resource.fogColour))
							rasterizer.alpha = 0
							return
						}
						if (javaSimpleBlendedTile.texture >= 0) {
							var alphaMask: Int = -16777216
							if (waterEffect) {
								alphaMask = -1694498816
							}
							rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, alphaMask or (javaSimpleBlendedTile.colourSw and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourSe and 0xFFFFFF), alphaMask or (javaSimpleBlendedTile.colourNw and 0xFFFFFF), 0, 0, 0, 0, javaSimpleBlendedTile.texture.toInt())
							return
						}
						if (waterEffect) {
							rasterizer.alpha = 100
						}
						rasterizer.renderTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw)
						rasterizer.alpha = 0
						return
					}
					rasterizer.renderFlatTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, resource.fogColour)
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III[[ZZI)V")
	override fun renderTiles(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) skipFlags: Int) {
		@Pc(3) val javaMatrix: JavaMatrix = this.toolkit.camera!!
		this.depthOverride = -1
		this.cameraE1_1 = javaMatrix.e1_1
		this.cameraE1_2 = javaMatrix.e1_2
		this.cameraE1_3 = javaMatrix.e1_3
		this.cameraTx = javaMatrix.tx
		this.cameraE2_1 = javaMatrix.e2_1
		this.cameraE2_2 = javaMatrix.e2_2
		this.cameraE2_3 = javaMatrix.e2_3
		this.cameraTy = javaMatrix.ty
		this.cameraE3_1 = javaMatrix.e3_1
		this.cameraE3_2 = javaMatrix.e3_2
		this.cameraE3_3 = javaMatrix.e3_3
		this.cameraTz = javaMatrix.tz
		for (@Pc(56) i in 0 until radius + radius) {
			for (@Pc(59) j in 0 until radius + radius) {
				if (visibility!![i]!![j]) {
					@Pc(72) val tileX: Int = x + i - radius
					@Pc(78) val tileZ: Int = z + j - radius
					if (tileX >= 0 && tileX < this.width && tileZ >= 0 && tileZ < this.length) {
						this.renderTile(tileX, tileZ, skipFlags)
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III)V")
	private fun renderTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) skipFlags: Int) {
		@Pc(4) val javaThreadResource: JavaThreadResource = this.toolkit.threadResource(lang.currentThread())!!
		javaThreadResource.rasterizer!!.alpha = 0
		if (this.simpleBlendedTiles != null) {
			this.renderBlendedTile(x, z, javaThreadResource.water, javaThreadResource, javaThreadResource.rasterizer!!, javaThreadResource.screenX!!, javaThreadResource.screenY!!, javaThreadResource.depths!!, javaThreadResource.fogLevels!!, skipFlags)
		} else if (this.simpleTiles != null) {
			this.renderUnblendedTile(x, z, javaThreadResource.rasterizer!!, javaThreadResource.screenX!!, javaThreadResource.screenY!!, javaThreadResource.depths!!, javaThreadResource.fogLevels!!, skipFlags)
		} else if (this.genericBlendedTiles != null) {
			this.renderGenericBlendedTile(x, z, javaThreadResource.water, javaThreadResource, javaThreadResource.rasterizer!!, javaThreadResource.screenX!!, javaThreadResource.screenY!!, javaThreadResource.depths!!, javaThreadResource.fogLevels!!, skipFlags)
		}
	}

	@OriginalMember(owner = "client!cr", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	override fun wa(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIIIIII[[ZLclient!pga;Lclient!mp;[I[I)V")
	private fun drawBlendedMinimapTiles(@OriginalArg(3) x1: Int, @OriginalArg(4) z1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) z2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?, @OriginalArg(8) resource: JavaThreadResource, @OriginalArg(9) rasterizer: Rasterizer, @OriginalArg(10) screenX: IntArray, @OriginalArg(11) screenY: IntArray) {
		@Pc(7) val mapHeight: Int = (z2 - z1) * 1024 / 256
		@Pc(14) val zWrite: Boolean = resource.zWrite
		this.toolkit.C(false)
		rasterizer.fastScanline = false
		rasterizer.halfBlend = false
		@Pc(26) var drawX: Int = 0
		@Pc(30) var drawY: Int = mapHeight
		for (@Pc(32) i in x1 until x2) {
			for (@Pc(35) j in z1 until z2) {
				if (visibility!![i - x1]!![j - z1]) {
					@Pc(81) var local81: Int
					@Pc(370) var face: Int
					@Pc(375) var a: Int
					@Pc(379) var b: Int
					@Pc(383) var c: Int
					@Pc(387) var xA: Int
					@Pc(391) var xB: Int
					@Pc(395) var xC: Int
					@Pc(399) var yA: Int
					@Pc(403) var yB: Int
					@Pc(407) var yC: Int
					@Pc(432) var colour: Int
					if (this.simpleBlendedTiles == null) {
						if (this.genericBlendedTiles!![i][j] != null) {
							@Pc(573) val javaGenericBlendedTile: JavaGenericBlendedTile = this.genericBlendedTiles!![i][j]!!
							var vertexIndex: Int = 0
							while (vertexIndex < javaGenericBlendedTile.vertexCount) {
								screenX[vertexIndex] = drawX + javaGenericBlendedTile.verticesX!![vertexIndex] * 4 / this.tileUnits
								screenY[vertexIndex] = drawY - javaGenericBlendedTile.verticesZ!![vertexIndex] * 4 / this.tileUnits
								vertexIndex++
							}
							face = 0
							while (face < javaGenericBlendedTile.faceCount) {
								a = face * 3
								b = a + 1
								c = b + 1
								xA = screenX[a]
								xB = screenX[b]
								xC = screenX[c]
								yA = screenY[a]
								yB = screenY[b]
								yC = screenY[c]
								if (javaGenericBlendedTile.faceBlendedColours == null || javaGenericBlendedTile.faceBlendedColours!![face] == 0) {
									rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, javaGenericBlendedTile.vertexColours!![a], javaGenericBlendedTile.vertexColours!![b], javaGenericBlendedTile.vertexColours!![c])
								} else {
									colour = javaGenericBlendedTile.faceBlendedColours!![face]
									rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, colour, colour, colour)
								}
								face++
							}
						}
					} else if (this.simpleBlendedTiles!![i][j] != null) {
						@Pc(63) val javaSimpleBlendedTile: JavaSimpleBlendedTile = this.simpleBlendedTiles!![i][j]!!
						if (javaSimpleBlendedTile.texture.toInt() != -1 && (javaSimpleBlendedTile.flags.toInt() and 0x2) == 0 && javaSimpleBlendedTile.blendedColour == 0) {
							val textureColour: Int = this.toolkit.textureHsl(javaSimpleBlendedTile.texture.toInt())
							rasterizer.renderTriangleHslRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaSimpleBlendedTile.colourNe, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleBlendedTile.colourNw, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleBlendedTile.colourSe, textureColour).toFloat())
							rasterizer.renderTriangleHslRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaSimpleBlendedTile.colourSw, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleBlendedTile.colourSe, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleBlendedTile.colourNw, textureColour).toFloat())
						} else if (javaSimpleBlendedTile.blendedColour == 0) {
							rasterizer.renderTriangleRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe)
							rasterizer.renderTriangleRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw)
						} else {
							val blendedColour: Int = javaSimpleBlendedTile.blendedColour
							rasterizer.renderTriangleRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNe and 0xFF000000.toInt()), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNw and 0xFF000000.toInt()), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSe and 0xFF000000.toInt()))
							rasterizer.renderTriangleRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSw and 0xFF000000.toInt()), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSe and 0xFF000000.toInt()), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNw and 0xFF000000.toInt()))
						}
					} else if (this.complexBlendedTiles!![i][j] != null) {
						@Pc(332) val javaComplexBlendedTile: JavaComplexBlendedTile = this.complexBlendedTiles!![i][j]!!
						var vertexIndex: Int = 0
						while (vertexIndex < javaComplexBlendedTile.vertexCount) {
							screenX[vertexIndex] = drawX + javaComplexBlendedTile.verticesX!![vertexIndex] * 4 / this.tileUnits
							screenY[vertexIndex] = drawY - javaComplexBlendedTile.verticesZ!![vertexIndex] * 4 / this.tileUnits
							vertexIndex++
						}
						face = 0
						while (face < javaComplexBlendedTile.faceCount) {
							a = face * 3
							b = a + 1
							c = b + 1
							xA = screenX[a]
							xB = screenX[b]
							xC = screenX[c]
							yA = screenY[a]
							yB = screenY[b]
							yC = screenY[c]
							if (javaComplexBlendedTile.faceBlendedColours != null && javaComplexBlendedTile.faceBlendedColours!![face] != 0 && (javaComplexBlendedTile.faceTextures == null || javaComplexBlendedTile.faceTextures != null && javaComplexBlendedTile.faceTextures!![face].toInt() == -1)) {
								colour = javaComplexBlendedTile.faceBlendedColours!![face]
								rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours!![a] and -16777216) - 16777216), ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours!![b] and -16777216) - 16777216), ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours!![c] and -16777216) - 16777216))
							} else if (javaComplexBlendedTile.faceTextures == null || javaComplexBlendedTile.faceTextures!![face].toInt() == -1) {
								rasterizer.renderTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, javaComplexBlendedTile.vertexColours!![a], javaComplexBlendedTile.vertexColours!![b], javaComplexBlendedTile.vertexColours!![c])
							} else {
								colour = this.toolkit.textureHsl(javaComplexBlendedTile.faceTextures!![face].toInt())
								rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, colour.toFloat(), colour.toFloat(), colour.toFloat())
							}
							face++
						}
					}
				}
				drawY -= 4
			}
			drawY = mapHeight
			drawX += 4
		}
		rasterizer.fastScanline = true
		this.toolkit.C(zWrite)
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IILclient!mp;[I[I[I[II)V")
	private fun renderUnblendedTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) rasterizer: Rasterizer, @OriginalArg(3) screenX: IntArray, @OriginalArg(4) screenY: IntArray, @OriginalArg(5) depths: IntArray, @OriginalArg(6) fogLevels: IntArray, @OriginalArg(7) skipFlags: Int) {
		@Pc(6) val javaSimpleTile: PlainTile? = this.simpleTiles!![x][z]
		@Pc(41) var local41: Int
		@Pc(51) var local51: Int
		@Pc(467) var local467: Int
		@Pc(497) var local497: Int
		if (javaSimpleTile == null) {
			@Pc(2129) val javaComplexTile: JavaComplexTile? = this.complexTiles!![x][z]
			if (javaComplexTile != null) {
				if (skipFlags != 0) {
					if ((javaComplexTile.flags.toInt() and 0x4) == 0) {
						if ((skipFlags and 0x2) != 0) {
							return
						}
					} else if ((skipFlags and 0x1) != 0) {
						return
					}
				}
				@Pc(2170) var vertexY: Short
				@Pc(2230) var viewX: Float
				@Pc(2251) var viewY: Float
				@Pc(2201) var viewZ: Float
				if (this.depthOverride == -1) {
					var vertexIndex: Int = 0
					while (vertexIndex < javaComplexTile.vertexCount) {
						val vertexX: Int = javaComplexTile.verticesX!![vertexIndex] + (x shl this.tileScale)
						vertexY = javaComplexTile.verticesY!![vertexIndex]
						val vertexZ: Int = javaComplexTile.verticesZ!![vertexIndex] + (z shl this.tileScale)
						viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
						if (viewZ <= this.toolkit.zNear.toFloat()) {
							return
						}
						viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
						viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
						screenX[vertexIndex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / viewZ).toInt()
						screenY[vertexIndex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / viewZ).toInt()
						depths[vertexIndex] = viewZ.toInt()
						vertexIndex++
					}
				} else {
					var vertexIndex: Int = 0
					while (vertexIndex < javaComplexTile.vertexCount) {
						val vertexX: Int = javaComplexTile.verticesX!![vertexIndex] + (x shl this.tileScale)
						vertexY = javaComplexTile.verticesY!![vertexIndex]
						val vertexZ: Int = javaComplexTile.verticesZ!![vertexIndex] + (z shl this.tileScale)
						viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
						viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
						viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
						screenX[vertexIndex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
						screenY[vertexIndex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
						depths[vertexIndex] = viewZ.toInt()
						vertexIndex++
					}
				}
				@Pc(2442) var a: Short
				@Pc(2447) var b: Short
				@Pc(2452) var c: Short
				@Pc(2460) var xB: Int
				@Pc(2464) var xC: Int
				@Pc(2468) var yA: Int
				@Pc(2472) var yB: Int
				@Pc(2476) var yC: Int
				if (javaComplexTile.faceTextures != null) {
					@Pc(2650) var colour: Int
					@Pc(2520) var texture: Short
					if (this.depthOverride == -1) {
						var faceIndex: Int = 0
						while (faceIndex < javaComplexTile.faceCount) {
							a = javaComplexTile.faceA!![faceIndex]
							b = javaComplexTile.faceB!![faceIndex]
							c = javaComplexTile.faceC!![faceIndex]
							val xA: Int = screenX[a.toInt()]
							xB = screenX[b.toInt()]
							xC = screenX[c.toInt()]
							yA = screenY[a.toInt()]
							yB = screenY[b.toInt()]
							yC = screenY[c.toInt()]
							if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
								rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
								texture = javaComplexTile.faceTextures!![faceIndex]
								if (texture.toInt() == -1) {
									colour = javaComplexTile.faceColours!![faceIndex]
									if (colour != -1) {
										rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a.toInt()].toFloat(), depths[b.toInt()].toFloat(), depths[c.toInt()].toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), colour).toFloat())
									}
								} else {
									rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a.toInt()].toFloat(), depths[b.toInt()].toFloat(), depths[c.toInt()].toFloat(), javaComplexTile.verticesX!![a.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesX!![b.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesX!![c.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![a.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![b.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![c.toInt()].toFloat() / this.tileUnits.toFloat(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![a.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![b.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![c.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, texture.toInt())
								}
							}
							faceIndex++
						}
						return
					}
					var faceIndex: Int = 0
					while (faceIndex < javaComplexTile.faceCount) {
						a = javaComplexTile.faceA!![faceIndex]
						b = javaComplexTile.faceB!![faceIndex]
						c = javaComplexTile.faceC!![faceIndex]
						val xA: Int = screenX[a.toInt()]
						xB = screenX[b.toInt()]
						xC = screenX[c.toInt()]
						yA = screenY[a.toInt()]
						yB = screenY[b.toInt()]
						yC = screenY[c.toInt()]
						if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
							texture = javaComplexTile.faceTextures!![faceIndex]
							if (texture.toInt() == -1) {
								colour = javaComplexTile.faceColours!![faceIndex]
								if (colour != -1) {
									rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a.toInt()].toFloat(), depths[b.toInt()].toFloat(), depths[c.toInt()].toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), colour).toFloat())
								}
							} else {
								rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a.toInt()].toFloat(), depths[b.toInt()].toFloat(), depths[c.toInt()].toFloat(), javaComplexTile.verticesX!![a.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesX!![b.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesX!![c.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![a.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![b.toInt()].toFloat() / this.tileUnits.toFloat(), javaComplexTile.verticesZ!![c.toInt()].toFloat() / this.tileUnits.toFloat(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![a.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![b.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaComplexTile.verticesLight!![c.toInt()].toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, texture.toInt())
							}
						}
						faceIndex++
					}
					return
				}
				var faceIndex: Int = 0
				while (faceIndex < javaComplexTile.faceCount) {
					a = javaComplexTile.faceA!![faceIndex]
					b = javaComplexTile.faceB!![faceIndex]
					c = javaComplexTile.faceC!![faceIndex]
					val xA: Int = screenX[a.toInt()]
					xB = screenX[b.toInt()]
					xC = screenX[c.toInt()]
					yA = screenY[a.toInt()]
					yB = screenY[b.toInt()]
					yC = screenY[c.toInt()]
					if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
						@Pc(3047) val faceColour: Int = javaComplexTile.faceColours!![faceIndex]
						if (faceColour != -1) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
							rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a.toInt()].toFloat(), depths[b.toInt()].toFloat(), depths[c.toInt()].toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), faceColour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), faceColour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), faceColour).toFloat())
						}
					}
					faceIndex++
				}
			}
		} else if ((javaSimpleTile.functionMask.toInt() and 0x2) == 0) {
			if (skipFlags != 0) {
				if ((javaSimpleTile.functionMask.toInt() and 0x4) == 0) {
					if ((skipFlags and 0x2) != 0) {
						return
					}
				} else if ((skipFlags and 0x1) != 0) {
					return
				}
			}
			@Pc(36) val minX: Int = x * this.tileUnits
			val maxX: Int = minX + this.tileUnits
			@Pc(46) val minZ: Int = z * this.tileUnits
			val maxZ: Int = minZ + this.tileUnits
			@Pc(484) val viewYNw: Float
			@Pc(90) val depthSw: Float
			@Pc(115) val depthSe: Float
			@Pc(140) val depthNe: Float
			@Pc(165) val depthNw: Float
			@Pc(63) val heightSw: Int
			@Pc(287) val screenXSw: Int
			@Pc(347) val screenXSe: Int
			@Pc(407) val screenXNe: Int
			@Pc(317) val screenYSw: Int
			@Pc(377) val screenYSe: Int
			@Pc(437) val screenYNe: Int
			@Pc(274) val viewXSw: Float
			@Pc(334) val viewXSe: Float
			@Pc(394) val viewXNe: Float
			@Pc(454) val viewXNw: Float
			@Pc(304) val viewYSw: Float
			@Pc(364) val viewYSe: Float
			@Pc(424) val viewYNe: Float
			val screenXNw: Int
			val screenYNw: Int
			if ((javaSimpleTile.functionMask.toInt() and 0x1) == 0) {
				heightSw = this.tileHeights!![x]!![z]
				@Pc(771) val heightSe: Int = this.tileHeights[x + 1]!![z]
				@Pc(782) val heightNe: Int = this.tileHeights[x + 1]!![z + 1]
				@Pc(791) val heightNw: Int = this.tileHeights[x]!![z + 1]
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightSw.toFloat() + this.cameraE3_3 * minZ.toFloat()
					if (depthSw <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightSe.toFloat() + this.cameraE3_3 * minZ.toFloat()
					if (depthSe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightNe.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					if (depthNe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightNw.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					if (depthNw <= this.toolkit.zNear.toFloat()) {
						return
					}
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightSw.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / depthSw).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightSw.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / depthSw).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightSe.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / depthSe).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightSe.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / depthSe).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightNe.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / depthNe).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightNe.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / depthNe).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightNw.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / depthNw).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightNw.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / depthNw).toInt()
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightSw.toFloat() + this.cameraE3_3 * minZ.toFloat()
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightSe.toFloat() + this.cameraE3_3 * minZ.toFloat()
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + this.cameraE3_2 * heightNe.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + this.cameraE3_2 * heightNw.toFloat() + this.cameraE3_3 * maxZ.toFloat()
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightSw.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightSw.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightSe.toFloat() + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightSe.toFloat() + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + this.cameraE1_2 * heightNe.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + this.cameraE2_2 * heightNe.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + this.cameraE1_2 * heightNw.toFloat() + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + this.cameraE2_2 * heightNw.toFloat() + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
				}
			} else {
				heightSw = this.tileHeights!![x]!![z]
				@Pc(69) val heightZ: Float = this.cameraE3_2 * heightSw.toFloat()
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					if (depthSw <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					if (depthSe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					if (depthNe <= this.toolkit.zNear.toFloat()) {
						return
					}
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					if (depthNw <= this.toolkit.zNear.toFloat()) {
						return
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					depthSe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * minZ.toFloat()
					depthNe = this.cameraTz + this.cameraE3_1 * maxX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
					depthNw = this.cameraTz + this.cameraE3_1 * minX.toFloat() + heightZ + this.cameraE3_3 * maxZ.toFloat()
				}
				@Pc(247) val heightX: Float = this.cameraE1_2 * heightSw.toFloat()
				@Pc(253) val heightY: Float = this.cameraE2_2 * heightSw.toFloat()
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / depthSw).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / depthSw).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / depthSe).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / depthSe).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / depthNe).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / depthNe).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / depthNw).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / depthNw).toInt()
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSw = rasterizer.minX + (viewXSw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSw = rasterizer.minY + (viewYSw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXSe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * minZ.toFloat()
					screenXSe = rasterizer.minX + (viewXSe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYSe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * minZ.toFloat()
					screenYSe = rasterizer.minY + (viewYSe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNe = this.cameraTx + this.cameraE1_1 * maxX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNe = rasterizer.minX + (viewXNe * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNe = this.cameraTy + this.cameraE2_1 * maxX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNe = rasterizer.minY + (viewYNe * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
					viewXNw = this.cameraTx + this.cameraE1_1 * minX.toFloat() + heightX + this.cameraE1_3 * maxZ.toFloat()
					screenXNw = rasterizer.minX + (viewXNw * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
					viewYNw = this.cameraTy + this.cameraE2_1 * minX.toFloat() + heightY + this.cameraE2_3 * maxZ.toFloat()
					screenYNw = rasterizer.minY + (viewYNw * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
				}
			}
			if (this.depthOverride == -1) {
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, javaSimpleTile.texture.toInt())
					} else {
						rasterizer.renderTriangleHslRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, (javaSimpleTile.hslNe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat())
					}
				}
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, javaSimpleTile.texture.toInt())
						return
					}
					rasterizer.renderTriangleHslRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, (javaSimpleTile.hslSw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat())
					return
				}
			} else {
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, javaSimpleTile.texture.toInt())
					} else {
						rasterizer.renderTriangleHslRgb(screenYNe.toFloat(), screenYNw.toFloat(), screenYSe.toFloat(), screenXNe.toFloat(), screenXNw.toFloat(), screenXSe.toFloat(), depthNe, depthNw, depthSe, (javaSimpleTile.hslNe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat())
					}
				}
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslSe.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), (ColorUtil.hsvToRgbTable!![javaSimpleTile.hslNw.toInt() and 0xFFFF] and 0xFFFFFF) or 0xFF000000.toInt(), 0, 0, 0, 0, javaSimpleTile.texture.toInt())
						return
					}
					rasterizer.renderTriangleHslRgb(screenYSw.toFloat(), screenYSe.toFloat(), screenYNw.toFloat(), screenXSw.toFloat(), screenXSe.toFloat(), screenXNw.toFloat(), depthSw, depthSe, depthNw, (javaSimpleTile.hslSw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat())
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	override fun method8002(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int): Boolean {
		return false
	}

	@OriginalMember(owner = "client!cr", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	override fun fa(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadow: Shadow?): Shadow? {
		return null
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIIIIII[[Z)V")
	override fun drawMinimap(@OriginalArg(3) x1: Int, @OriginalArg(4) y1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) y2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?) {
		@Pc(4) val javaThreadResource: JavaThreadResource = this.toolkit.threadResource(lang.currentThread())!!
		@Pc(7) val rasterizer: Rasterizer = javaThreadResource.rasterizer!!
		rasterizer.alpha = 0
		rasterizer.clamp = true
		this.toolkit.ya()
		if (this.simpleBlendedTiles != null || this.genericBlendedTiles != null) {
			this.drawBlendedMinimapTiles(x1, y1, x2, y2, visibility, javaThreadResource, rasterizer, javaThreadResource.screenX!!, javaThreadResource.screenY!!)
		} else if (this.simpleTiles != null) {
			this.drawUnblendedMinimapTiles(x1, y1, x2, y2, visibility, javaThreadResource, rasterizer, javaThreadResource.screenX!!, javaThreadResource.screenY!!)
		}
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(IIZLclient!pga;Lclient!mp;[I[I[I[II)V")
	private fun renderGenericBlendedTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) water: Boolean, @OriginalArg(3) resource: JavaThreadResource, @OriginalArg(4) rasterizer: Rasterizer, @OriginalArg(5) screenX: IntArray, @OriginalArg(6) screenY: IntArray, @OriginalArg(7) depths: IntArray, @OriginalArg(8) fogLevels: IntArray, @OriginalArg(9) skipFlags: Int) {
		@Pc(6) val javaGenericBlendedTile: JavaGenericBlendedTile? = this.genericBlendedTiles!![x][z]
		if (skipFlags != 0 && (skipFlags and 0x2) != 0 || javaGenericBlendedTile == null) {
			return
		}
		@Pc(32) var vertexX: Int
		@Pc(37) var vertexY: Int
		@Pc(47) var vertexZ: Int
		@Pc(162) var viewX: Float
		@Pc(183) var viewY: Float
		@Pc(68) var viewZ: Float
		@Pc(21) var vertex: Int
		@Pc(89) var local89: Int
		@Pc(109) var local109: Int
		if (this.depthOverride == -1) {
			vertex = 0
			while (vertex < javaGenericBlendedTile.vertexCount) {
				vertexX = javaGenericBlendedTile.verticesX!![vertex] + (x shl this.tileScale)
				vertexY = javaGenericBlendedTile.verticesY!![vertex].toInt()
				vertexZ = javaGenericBlendedTile.verticesZ!![vertex] + (z shl this.tileScale)
				viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
				if (viewZ <= this.toolkit.zNear.toFloat()) {
					return
				}
				fogLevels[vertex] = 0
				if (water) {
					var fogLevel: Int = (viewZ - resource.fogPlane.toFloat()).toInt()
					if (fogLevel > 255) {
						fogLevel = 255
					}
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel
						val waterDepth: Int = javaGenericBlendedTile.waterDepths!![vertex] * fogLevel / 255
						if (waterDepth > 0) {
							vertexY -= waterDepth
						}
					}
				} else if (resource.fogActive) {
					val fogLevel: Int = (viewZ - resource.fogPlane.toFloat()).toInt()
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel
						if (fogLevels[vertex] > 255) {
							fogLevels[vertex] = 255
						}
					}
				}
				viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
				viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
				screenX[vertex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / viewZ).toInt()
				screenY[vertex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / viewZ).toInt()
				depths[vertex] = viewZ.toInt()
				vertex++
			}
		} else {
			vertex = 0
			while (vertex < javaGenericBlendedTile.vertexCount) {
				vertexX = javaGenericBlendedTile.verticesX!![vertex] + (x shl this.tileScale)
				vertexY = javaGenericBlendedTile.verticesY!![vertex].toInt()
				vertexZ = javaGenericBlendedTile.verticesZ!![vertex] + (z shl this.tileScale)
				viewZ = this.cameraTz + this.cameraE3_1 * vertexX.toFloat() + this.cameraE3_2 * vertexY.toFloat() + this.cameraE3_3 * vertexZ.toFloat()
				fogLevels[vertex] = 0
				if (water) {
					var fogLevel: Int = this.depthOverride - resource.fogPlane
					if (fogLevel > 255) {
						fogLevel = 255
					}
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel
						val waterDepth: Int = javaGenericBlendedTile.waterDepths!![vertex] * fogLevel / 255
						if (waterDepth > 0) {
							vertexY -= waterDepth
						}
					}
				} else if (resource.fogActive) {
					val fogLevel: Int = this.depthOverride - resource.fogPlane
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel
						if (fogLevels[vertex] > 255) {
							fogLevels[vertex] = 255
						}
					}
				}
				viewX = this.cameraTx + this.cameraE1_1 * vertexX.toFloat() + this.cameraE1_2 * vertexY.toFloat() + this.cameraE1_3 * vertexZ.toFloat()
				viewY = this.cameraTy + this.cameraE2_1 * vertexX.toFloat() + this.cameraE2_2 * vertexY.toFloat() + this.cameraE2_3 * vertexZ.toFloat()
				screenX[vertex] = rasterizer.minX + (viewX * this.toolkit.projectionScaleX.toFloat() / this.depthOverride.toFloat()).toInt()
				screenY[vertex] = rasterizer.minY + (viewY * this.toolkit.projectionScaleY.toFloat() / this.depthOverride.toFloat()).toInt()
				depths[vertex] = viewZ.toInt()
				vertex++
			}
		}
		@Pc(426) val tileUnits: Float = this.tileUnits.toFloat()
		var faceIndex: Int = 0
		while (faceIndex < javaGenericBlendedTile.faceCount) {
			val a: Int = faceIndex * 3
			@Pc(437) val b: Int = a + 1
			@Pc(441) val c: Int = b + 1
			@Pc(445) val xA: Int = screenX[a]
			@Pc(449) val xB: Int = screenX[b]
			@Pc(453) val xC: Int = screenX[c]
			@Pc(457) val yA: Int = screenY[a]
			@Pc(461) val yB: Int = screenY[b]
			@Pc(465) val yC: Int = screenY[c]
			if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
				rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width
				if (fogLevels[a] + fogLevels[b] + fogLevels[c] < 765) {
					@Pc(522) val originX: Int = x shl this.tileScale
					@Pc(527) val originZ: Int = z shl this.tileScale
					if ((javaGenericBlendedTile.vertexColours!![a] and 0xFFFFFF) != 0) {
						if (javaGenericBlendedTile.vertexTextures!![a] == javaGenericBlendedTile.vertexTextures!![b] && javaGenericBlendedTile.vertexTextures!![a] == javaGenericBlendedTile.vertexTextures!![c] && javaGenericBlendedTile.vertexSizes!![a] == javaGenericBlendedTile.vertexSizes!![b] && javaGenericBlendedTile.vertexSizes!![a] == javaGenericBlendedTile.vertexSizes!![c]) {
							rasterizer.renderTexturedTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a].toFloat(), depths[b].toFloat(), depths[c].toFloat(), (originX + javaGenericBlendedTile.verticesX!![a]).toFloat() / javaGenericBlendedTile.vertexSizes!![a].toFloat(), (originX + javaGenericBlendedTile.verticesX!![b]).toFloat() / javaGenericBlendedTile.vertexSizes!![b].toFloat(), (originX + javaGenericBlendedTile.verticesX!![c]).toFloat() / javaGenericBlendedTile.vertexSizes!![c].toFloat(), (originZ + javaGenericBlendedTile.verticesZ!![a]).toFloat() / javaGenericBlendedTile.vertexSizes!![a].toFloat(), (originZ + javaGenericBlendedTile.verticesZ!![b]).toFloat() / javaGenericBlendedTile.vertexSizes!![b].toFloat(), (originZ + javaGenericBlendedTile.verticesZ!![c]).toFloat() / javaGenericBlendedTile.vertexSizes!![c].toFloat(), javaGenericBlendedTile.vertexColours!![a], javaGenericBlendedTile.vertexColours!![b], javaGenericBlendedTile.vertexColours!![c], resource.fogColour, fogLevels[a], fogLevels[b], fogLevels[c], javaGenericBlendedTile.vertexTextures!![a].toInt())
						} else {
							rasterizer.renderBlendedTexturedTriangle(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a].toFloat(), depths[b].toFloat(), depths[c].toFloat(), (originX + javaGenericBlendedTile.verticesX!![a]).toFloat() / tileUnits, (originX + javaGenericBlendedTile.verticesX!![b]).toFloat() / tileUnits, (originX + javaGenericBlendedTile.verticesX!![c]).toFloat() / tileUnits, (originZ + javaGenericBlendedTile.verticesZ!![a]).toFloat() / tileUnits, (originZ + javaGenericBlendedTile.verticesZ!![b]).toFloat() / tileUnits, (originZ + javaGenericBlendedTile.verticesZ!![c]).toFloat() / tileUnits, javaGenericBlendedTile.vertexColours!![a], javaGenericBlendedTile.vertexColours!![b], javaGenericBlendedTile.vertexColours!![c], resource.fogColour, fogLevels[a], fogLevels[b], fogLevels[c], javaGenericBlendedTile.vertexTextures!![a].toInt(), tileUnits / javaGenericBlendedTile.vertexSizes!![a].toFloat(), javaGenericBlendedTile.vertexTextures!![b].toInt(), tileUnits / javaGenericBlendedTile.vertexSizes!![b].toFloat(), javaGenericBlendedTile.vertexTextures!![c].toInt(), tileUnits / javaGenericBlendedTile.vertexSizes!![c].toFloat())
						}
					}
				} else {
					rasterizer.renderFlatTriangleRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), depths[a].toFloat(), depths[b].toFloat(), depths[c].toFloat(), resource.fogColour)
				}
			}
			faceIndex++
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(II)V")
	override fun renderTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int) {
		this.renderTile(x, z, 0)
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(IIIIIII[[ZLclient!pga;Lclient!mp;[I[I)V")
	private fun drawUnblendedMinimapTiles(@OriginalArg(3) x1: Int, @OriginalArg(4) z1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) z2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?, @OriginalArg(8) resource: JavaThreadResource, @OriginalArg(9) rasterizer: Rasterizer, @OriginalArg(10) screenX: IntArray, @OriginalArg(11) screenY: IntArray) {
		@Pc(7) val mapHeight: Int = (z2 - z1) * 1024 / 256
		@Pc(14) val zWrite: Boolean = resource.zWrite
		this.toolkit.C(false)
		rasterizer.fastScanline = false
		rasterizer.halfBlend = false
		@Pc(26) var drawX: Int = 0
		@Pc(30) var drawY: Int = mapHeight
		for (@Pc(32) i in x1 until x2) {
			for (@Pc(35) j in z1 until z2) {
				if (visibility!![i - x1]!![j - z1]) {
					@Pc(79) var local79: Int
					if (this.simpleTiles!![i][j] != null) {
						@Pc(60) val javaSimpleTile: PlainTile = this.simpleTiles!![i][j]!!
						if (javaSimpleTile.texture.toInt() != -1 && (javaSimpleTile.functionMask.toInt() and 0x2) == 0 && javaSimpleTile.color == -1) {
							val textureColour: Int = this.toolkit.textureHsl(javaSimpleTile.texture.toInt())
							rasterizer.renderTriangleHslRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaSimpleTile.hslNe.toInt() and 0xFFFF, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleTile.hslNw.toInt() and 0xFFFF, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleTile.hslSe.toInt() and 0xFFFF, textureColour).toFloat())
							rasterizer.renderTriangleHslRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaSimpleTile.hslSw.toInt() and 0xFFFF, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleTile.hslSe.toInt() and 0xFFFF, textureColour).toFloat(), JavaModel.applyLightness(javaSimpleTile.hslNw.toInt() and 0xFFFF, textureColour).toFloat())
						} else if (javaSimpleTile.color == -1) {
							rasterizer.renderTriangleHslRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, (javaSimpleTile.hslNe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat())
							rasterizer.renderTriangleHslRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, (javaSimpleTile.hslSw.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslSe.toInt() and 0xFFFF).toFloat(), (javaSimpleTile.hslNw.toInt() and 0xFFFF).toFloat())
						} else {
							val colour: Int = javaSimpleTile.color
							rasterizer.renderTriangleHslRgb((drawY - 4).toFloat(), (drawY - 4).toFloat(), drawY.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), 100.0f, 100.0f, 100.0f, colour.toFloat(), colour.toFloat(), colour.toFloat())
							rasterizer.renderTriangleHslRgb(drawY.toFloat(), drawY.toFloat(), (drawY - 4).toFloat(), drawX.toFloat(), (drawX + 4).toFloat(), drawX.toFloat(), 100.0f, 100.0f, 100.0f, colour.toFloat(), colour.toFloat(), colour.toFloat())
						}
					} else if (this.complexTiles!![i][j] != null) {
						@Pc(331) val javaComplexTile: JavaComplexTile = this.complexTiles!![i][j]!!
						var vertexIndex: Int = 0
						while (vertexIndex < javaComplexTile.vertexCount) {
							screenX[vertexIndex] = drawX + javaComplexTile.verticesX!![vertexIndex] * 4 / this.tileUnits
							screenY[vertexIndex] = drawY - javaComplexTile.verticesZ!![vertexIndex] * 4 / this.tileUnits
							vertexIndex++
						}
						for (@Pc(369) k in 0 until javaComplexTile.faceCount.toInt()) {
							@Pc(375) val a: Short = javaComplexTile.faceA!![k]
							@Pc(380) val b: Short = javaComplexTile.faceB!![k]
							@Pc(385) val c: Short = javaComplexTile.faceC!![k]
							@Pc(389) val xA: Int = screenX[a.toInt()]
							@Pc(393) val xB: Int = screenX[b.toInt()]
							@Pc(397) val xC: Int = screenX[c.toInt()]
							@Pc(401) val yA: Int = screenY[a.toInt()]
							@Pc(405) val yB: Int = screenY[b.toInt()]
							@Pc(409) val yC: Int = screenY[c.toInt()]
							@Pc(423) var colour: Int
							if (javaComplexTile.faceBlendedColours != null && javaComplexTile.faceBlendedColours!![k] != -1) {
								colour = javaComplexTile.faceBlendedColours!![k]
								rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), colour).toFloat())
							} else if (javaComplexTile.faceTextures == null || javaComplexTile.faceTextures!![k].toInt() == -1) {
								colour = javaComplexTile.faceColours!![k]
								rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), colour).toFloat())
							} else {
								colour = this.toolkit.textureHsl(javaComplexTile.faceTextures!![k].toInt())
								rasterizer.renderTriangleHslRgb(yA.toFloat(), yB.toFloat(), yC.toFloat(), xA.toFloat(), xB.toFloat(), xC.toFloat(), 100.0f, 100.0f, 100.0f, JavaModel.applyLightness(javaComplexTile.verticesLight!![a.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![b.toInt()].toInt(), colour).toFloat(), JavaModel.applyLightness(javaComplexTile.verticesLight!![c.toInt()].toInt(), colour).toFloat())
							}
						}
					}
				}
				drawY -= 4
			}
			drawY = mapHeight
			drawX += 4
		}
		rasterizer.fastScanline = true
		this.toolkit.C(zWrite)
	}

	@OriginalMember(owner = "client!cr", name = "YA", descriptor = "()V")
	override fun YA() {
		this.lightLevels = null
		this.shadowLevels = null
	}

	@OriginalMember(owner = "client!cr", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	override fun CA(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(I)Z")
	private fun isWaterEffect(@OriginalArg(0) effectType: Int): Boolean {
		if ((this.featureFlags and 0x8) == 0) {
			return false
		} else if (effectType == 4) {
			return true
		} else if (effectType == 8) {
			return true
		} else {
			return effectType == 9
		}
	}
}
