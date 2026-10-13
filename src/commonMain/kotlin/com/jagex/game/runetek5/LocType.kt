package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.config.loctype.LocOcclusionMode
import com.jagex.game.runetek5.config.loctype.LocTypeList
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.ModelAndShadow
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.`object`.ObjectType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!iv")
class LocType {

	@OriginalMember(owner = "client!iv", name = "g", descriptor = "[B")
	private var recolorDPalette: ByteArray? = null

	@OriginalMember(owner = "client!iv", name = "n", descriptor = "B")
	private var tintLightness: Byte = 0

	@OriginalMember(owner = "client!iv", name = "p", descriptor = "Lclient!eq;")
	private var parameters: IterableHashTable? = null

	@OriginalMember(owner = "client!iv", name = "t", descriptor = "[B")
	private var shapes: ByteArray? = null

	@OriginalMember(owner = "client!iv", name = "A", descriptor = "[S")
	private var retextureDst: ShortArray? = null

	@OriginalMember(owner = "client!iv", name = "M", descriptor = "[I")
	@JvmField
	var soundGroupIds: IntArray? = null

	@OriginalMember(owner = "client!iv", name = "Q", descriptor = "[[I")
	private var objectModelIds: Array<IntArray?>? = null

	// Not part of the original client: the model ids, for 634HD's data tools (SmokeTest locmodels:).
	fun hdModelIds(): Array<IntArray?>? = this.objectModelIds

	@OriginalMember(owner = "client!iv", name = "R", descriptor = "[S")
	private var recolorDst: ShortArray? = null

	@OriginalMember(owner = "client!iv", name = "ab", descriptor = "[I")
	@JvmField
	var quests: IntArray? = null

	@OriginalMember(owner = "client!iv", name = "cb", descriptor = "[S")
	private var retextureSrc: ShortArray? = null

	@OriginalMember(owner = "client!iv", name = "qb", descriptor = "Lclient!uha;")
	@JvmField
	var locTypeList: LocTypeList? = null

	@OriginalMember(owner = "client!iv", name = "xb", descriptor = "[S")
	private var recolorSrc: ShortArray? = null

	@OriginalMember(owner = "client!iv", name = "yb", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!iv", name = "Bb", descriptor = "B")
	private var tintHue: Byte = 0

	@OriginalMember(owner = "client!iv", name = "Cb", descriptor = "[I")
	@JvmField
	var alternativeIds: IntArray? = null

	@OriginalMember(owner = "client!iv", name = "Jb", descriptor = "B")
	private var tintSaturation: Byte = 0

	@OriginalMember(owner = "client!iv", name = "c", descriptor = "I")
	@JvmField
	var animationId: Int = -1

	@OriginalMember(owner = "client!iv", name = "k", descriptor = "Z")
	@JvmField
	var ignoreClipOnAltRoute: Boolean = false

	@OriginalMember(owner = "client!iv", name = "d", descriptor = "I")
	@JvmField
	var primaryCursor: Int = -1

	@OriginalMember(owner = "client!iv", name = "y", descriptor = "I")
	private var shadowOffsetZ: Int = 0

	@OriginalMember(owner = "client!iv", name = "E", descriptor = "Z")
	@JvmField
	var blocksProjectiles: Boolean = true

	@OriginalMember(owner = "client!iv", name = "i", descriptor = "I")
	@JvmField
	var hasActions: Int = -1

	@OriginalMember(owner = "client!iv", name = "q", descriptor = "I")
	private var resizeZ: Int = 128

	@OriginalMember(owner = "client!iv", name = "s", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = "null"

	@OriginalMember(owner = "client!iv", name = "o", descriptor = "I")
	private var contrast: Int = 0

	@OriginalMember(owner = "client!iv", name = "u", descriptor = "B")
	private var groundContourType: Byte = 0

	@OriginalMember(owner = "client!iv", name = "w", descriptor = "Z")
	@JvmField
	var instrumentSoundEffect: Boolean = false

	@OriginalMember(owner = "client!iv", name = "O", descriptor = "Z")
	@JvmField
	var delayShading: Boolean = false

	@OriginalMember(owner = "client!iv", name = "L", descriptor = "Z")
	@JvmField
	var instrumentAmbientSound: Boolean = false

	@OriginalMember(owner = "client!iv", name = "l", descriptor = "I")
	@JvmField
	var secondaryCursor: Int = -1

	@OriginalMember(owner = "client!iv", name = "X", descriptor = "I")
	@JvmField
	var accessBlockFlags: Int = 0

	@OriginalMember(owner = "client!iv", name = "j", descriptor = "I")
	@JvmField
	var mapIconId: Int = -1

	@OriginalMember(owner = "client!iv", name = "W", descriptor = "[Ljava/lang/String;")
	@JvmField
	var actions: Array<String?>? = arrayOfNulls<String>(5)

	@OriginalMember(owner = "client!iv", name = "V", descriptor = "Z")
	@JvmField
	var mapIconRotates: Boolean = false

	@OriginalMember(owner = "client!iv", name = "C", descriptor = "Z")
	@JvmField
	var dynamicShadow: Boolean = true

	@OriginalMember(owner = "client!iv", name = "hb", descriptor = "Z")
	@JvmField
	var replaySequence: Boolean = true

	@OriginalMember(owner = "client!iv", name = "gb", descriptor = "I")
	@JvmField
	var mapIconRotation: Int = 0

	@OriginalMember(owner = "client!iv", name = "h", descriptor = "I")
	private var offsetY: Int = 0

	@OriginalMember(owner = "client!iv", name = "ob", descriptor = "Z")
	@JvmField
	var requiresTextures: Boolean = false

	@OriginalMember(owner = "client!iv", name = "U", descriptor = "I")
	private var animationOddsTotal: Int = 0

	@OriginalMember(owner = "client!iv", name = "Z", descriptor = "I")
	@JvmField
	var ambientSoundMinDelay: Int = 256

	@OriginalMember(owner = "client!iv", name = "eb", descriptor = "I")
	@JvmField
	var mapCategoryid: Int = -1

	@OriginalMember(owner = "client!iv", name = "wb", descriptor = "I")
	private var varpId: Int = -1

	@OriginalMember(owner = "client!iv", name = "r", descriptor = "I")
	@JvmField
	var decorDisplacement: Int = 64

	@OriginalMember(owner = "client!iv", name = "jb", descriptor = "I")
	@JvmField
	var sizeY: Int = 1

	@OriginalMember(owner = "client!iv", name = "lb", descriptor = "I")
	private var resizeX: Int = 128

	@OriginalMember(owner = "client!iv", name = "S", descriptor = "I")
	@JvmField
	var cullY: Int = 960

	@OriginalMember(owner = "client!iv", name = "rb", descriptor = "I")
	private var varpbitId: Int = -1

	@OriginalMember(owner = "client!iv", name = "x", descriptor = "I")
	@JvmField
	var soundRadius: Int = 0

	@OriginalMember(owner = "client!iv", name = "D", descriptor = "I")
	@JvmField
	var supportsItems: Int = -1

	@OriginalMember(owner = "client!iv", name = "fb", descriptor = "B")
	private var tintOpacity: Byte = 0

	@OriginalMember(owner = "client!iv", name = "Eb", descriptor = "Z")
	@JvmField
	var forceDisplayDecoration: Boolean = false

	@OriginalMember(owner = "client!iv", name = "m", descriptor = "I")
	@JvmField
	var ambientSoundMaxDelay: Int = 256

	@OriginalMember(owner = "client!iv", name = "B", descriptor = "I")
	private var shadowOffsetX: Int = 0

	@OriginalMember(owner = "client!iv", name = "tb", descriptor = "I")
	@JvmField
	var soundVolume: Int = 255

	@OriginalMember(owner = "client!iv", name = "Ab", descriptor = "[I")
	@JvmField
	var animations: IntArray? = null

	@OriginalMember(owner = "client!iv", name = "K", descriptor = "Z")
	@JvmField
	var inverted: Boolean = false

	@OriginalMember(owner = "client!iv", name = "bb", descriptor = "Z")
	@JvmField
	var staticShadow: Boolean = true

	@OriginalMember(owner = "client!iv", name = "Gb", descriptor = "I")
	private var ambient: Int = 0

	@OriginalMember(owner = "client!iv", name = "zb", descriptor = "Z")
	@JvmField
	var members: Boolean = false

	@OriginalMember(owner = "client!iv", name = "Kb", descriptor = "I")
	@JvmField
	var cullXZ: Int = 0

	@OriginalMember(owner = "client!iv", name = "Lb", descriptor = "I")
	private var shadowOffsetY: Int = 0

	@OriginalMember(owner = "client!iv", name = "ub", descriptor = "I")
	@JvmField
	var soundMaxInterval: Int = 0

	@OriginalMember(owner = "client!iv", name = "I", descriptor = "I")
	private var offsetX: Int = 0

	@OriginalMember(owner = "client!iv", name = "nb", descriptor = "I")
	@JvmField
	var groundDecorationHeight: Int = 0

	@OriginalMember(owner = "client!iv", name = "Y", descriptor = "I")
	private var goundContourModifier: Int = -1

	@OriginalMember(owner = "client!iv", name = "Ib", descriptor = "I")
	@JvmField
	var secondaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!iv", name = "Mb", descriptor = "I")
	@JvmField
	var occlusionMode: Int = -1

	@OriginalMember(owner = "client!iv", name = "ib", descriptor = "Z")
	@JvmField
	var mapIconFlipped: Boolean = false

	@OriginalMember(owner = "client!iv", name = "Db", descriptor = "I")
	private var offsetZ: Int = 0

	@OriginalMember(owner = "client!iv", name = "Hb", descriptor = "I")
	@JvmField
	var primaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!iv", name = "Qb", descriptor = "[I")
	private var animationOdds: IntArray? = null

	@OriginalMember(owner = "client!iv", name = "db", descriptor = "I")
	@JvmField
	var blocksMovement: Int = 2

	@OriginalMember(owner = "client!iv", name = "Sb", descriptor = "I")
	@JvmField
	var sizeX: Int = 1

	@OriginalMember(owner = "client!iv", name = "mb", descriptor = "Z")
	@JvmField
	var hasAnimation: Boolean = false

	@OriginalMember(owner = "client!iv", name = "sb", descriptor = "I")
	private var resizeY: Int = 128

	@OriginalMember(owner = "client!iv", name = "Tb", descriptor = "I")
	@JvmField
	var soundMinInterval: Int = 0

	@OriginalMember(owner = "client!iv", name = "v", descriptor = "I")
	@JvmField
	var soundId: Int = -1

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(B)Z")
	fun hasSounds(): Boolean {
		if (this.alternativeIds == null) {
			return this.soundId != -1 || this.soundGroupIds != null
		}
		@Pc(21) var i: Int = 0
		while (i < this.alternativeIds!!.size) {
			if (this.alternativeIds!![i] != -1) {
				@Pc(39) val locType: LocType = this.locTypeList!!.list(this.alternativeIds!![i])
				if (locType.soundId != -1 || locType.soundGroupIds != null) {
					return true
				}
			}
			i++
		}
		return false
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Ljava/lang/String;II)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(0) string: String?, @OriginalArg(1) key: Int): String? {
		if (this.parameters == null) {
			return string
		} else {
			@Pc(21) val stringNode: StringNode? = this.parameters!!.get(key.toLong()) as StringNode?
			return if (stringNode == null) string else stringNode.value
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!ha;Lclient!s;IIIZLclient!s;IIII)Lclient!ak;")
	fun getModelAndShadow(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) ground: Ground?, @OriginalArg(2) shape: Int, @OriginalArg(3) functionMask: Int, @OriginalArg(4) sceneX: Int, @OriginalArg(5) castShadow: Boolean, @OriginalArg(6) ground2: Ground?, @OriginalArg(7) averageHeight: Int, @OriginalArg(8) sceneZ: Int, @OriginalArg(9) rotation: Int): ModelAndShadow? {
		var shape: Int = shape
		if (LocShapes.isWallDecoration(shape)) {
			shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET
		}
		@Pc(23) val uid: Long = (rotation + (shape shl 3) + (this.id shl 10)).toLong()
		@Pc(31) val cacheKey: Long = uid or (toolkit!!.index shl 29).toLong()
		@Pc(33) var adjustedFunctionMask: Int = functionMask
		if (this.groundContourType.toInt() == 3) {
			adjustedFunctionMask = functionMask or 0x7
		} else {
			if (this.groundContourType.toInt() != 0 || this.shadowOffsetY != 0) {
				adjustedFunctionMask = functionMask or 0x2
			}
			if (this.shadowOffsetX != 0) {
				adjustedFunctionMask = adjustedFunctionMask or 0x1
			}
			if (this.shadowOffsetZ != 0) {
				adjustedFunctionMask = adjustedFunctionMask or 0x4
			}
		}
		if (castShadow) {
			adjustedFunctionMask = adjustedFunctionMask or 0x40000
		}
		@Pc(83) val referenceCache: LRUCache = this.locTypeList!!.modelAndShadowCache
		@Pc(95) var modelAndShadow: ModelAndShadow?
		withLock(this.locTypeList!!.modelAndShadowCache) {
			modelAndShadow = this.locTypeList!!.modelAndShadowCache.get(cacheKey) as ModelAndShadow?
		}
		@Pc(108) var model: Model? = if (modelAndShadow == null) null else modelAndShadow!!.model
		@Pc(110) var shadow: Shadow? = null
		if (model != null && toolkit.compareFunctionMasks(model.ua(), adjustedFunctionMask) == 0) {
			model = modelAndShadow!!.model
			shadow = modelAndShadow!!.shadow
			if (castShadow && shadow == null) {
				val newShadow: Shadow? = model!!.ba(null)
				modelAndShadow!!.shadow = newShadow
				shadow = newShadow
			}
		} else {
			if (model != null) {
				adjustedFunctionMask = toolkit.combineFunctionMasks(adjustedFunctionMask, model.ua())
			}
			@Pc(128) var stationaryFunctionMask: Int = adjustedFunctionMask
			if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
				stationaryFunctionMask = adjustedFunctionMask or 0x5
			}
			model = this.getStationaryModel(rotation, toolkit, shape, stationaryFunctionMask)
			if (model == null) {
				return null
			}
			if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
				model.a(2048)
			}
			if (castShadow) {
				shadow = model.ba(null)
			}
			model.s(adjustedFunctionMask)
			modelAndShadow = ModelAndShadow()
			modelAndShadow!!.model = model
			modelAndShadow!!.shadow = shadow
			@Pc(186) val referenceCache2: LRUCache = this.locTypeList!!.modelAndShadowCache
			withLock(this.locTypeList!!.modelAndShadowCache) {
				this.locTypeList!!.modelAndShadowCache.put(modelAndShadow, cacheKey)
			}
		}
		@Pc(243) val hasGroundContour: Boolean = this.groundContourType.toInt() != 0 && (ground2 != null || ground != null)
		@Pc(259) val hasTranslation: Boolean = this.shadowOffsetX != 0 || this.shadowOffsetY != 0 || this.shadowOffsetZ != 0
		if (hasGroundContour || hasTranslation) {
			model = model!!.copy(0.toByte(), adjustedFunctionMask, true)
			if (hasGroundContour) {
				model!!.p(this.groundContourType.toInt(), this.goundContourModifier, ground2, ground, sceneX, averageHeight, sceneZ)
			}
			if (hasTranslation) {
				model!!.H(this.shadowOffsetX, this.shadowOffsetY, this.shadowOffsetZ)
			}
			model!!.s(functionMask)
		} else {
			model = model!!.copy(0.toByte(), functionMask, true)
		}
		LocType.aClass16_31.model = model
		LocType.aClass16_31.shadow = shadow
		return LocType.aClass16_31
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(ILclient!ha;III)Lclient!ka;")
	private fun getStationaryModel(@OriginalArg(0) rotation: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) shape: Int, @OriginalArg(4) functionMask: Int): Model? {
		var functionMask: Int = functionMask
		@Pc(10) val adjustedAmbient: Int = this.ambient + 64
		@Pc(15) val adjustedContrast: Int = this.contrast + 850
		@Pc(17) val initialFunctionMask: Int = functionMask
		@Pc(32) val invert: Boolean = this.inverted || shape == LocShapes.WALL_L && rotation > 3
		if (invert) {
			functionMask = functionMask or 0x10
		}
		if (rotation == 0) {
			if (this.resizeX != 128 || this.offsetX != 0) {
				functionMask = functionMask or 0x1
			}
			if (this.resizeZ != 128 || this.offsetZ != 0) {
				functionMask = functionMask or 0x4
			}
		} else {
			functionMask = functionMask or 0xD
		}
		if (this.resizeY != 128 || this.offsetY != 0) {
			functionMask = functionMask or 0x2
		}
		if (this.recolorSrc != null) {
			functionMask = functionMask or 0x4000
		}
		if (this.retextureSrc != null) {
			functionMask = functionMask or 0x8000
		}
		if (this.tintOpacity.toInt() != 0) {
			functionMask = functionMask or 0x80000
		}
		@Pc(110) var model: Model? = null
		if (this.shapes == null) {
			return null
		}
		@Pc(117) var shapeIndex: Int = -1
		@Pc(119) var i: Int = 0
		while (i < this.shapes!!.size) {
			if (shape == this.shapes!![i].toInt()) {
				shapeIndex = i
				break
			}
			i++
		}
		if (shapeIndex == -1) {
			return null
		}
		@Pc(157) val modelCount: Int = this.objectModelIds!![shapeIndex]!!.size
		if (modelCount > 0) {
			@Pc(166) var uid: Long = toolkit!!.index.toLong()
			@Pc(168) var j: Int = 0
			while (j < modelCount) {
				uid = uid * 67783L + this.objectModelIds!![shapeIndex]!![j].toLong()
				j++
			}
			@Pc(194) val referenceCache: LRUCache = this.locTypeList!!.modelsByShapeCache
			withLock(this.locTypeList!!.modelsByShapeCache) {
				model = this.locTypeList!!.modelsByShapeCache.get(uid) as Model?
			}
			if (model != null) {
				if (model!!.WA() != adjustedAmbient) {
					functionMask = functionMask or 0x1000
				}
				if (adjustedContrast != model!!.da()) {
					functionMask = functionMask or 0x2000
				}
			}
			if (model == null || toolkit.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
				@Pc(247) var meshFunctionMask: Int = functionMask or 0x1F01F
				if (model != null) {
					meshFunctionMask = toolkit.combineFunctionMasks(meshFunctionMask, model!!.ua())
				}
				@Pc(257) var mesh: Mesh? = null
				@Pc(259) val meshes: Array<Mesh?> = LocType.aClass32Array1
				withLock(LocType.aClass32Array1) {
					@Pc(263) var k: Int = 0
					while (k < modelCount) {
						@Pc(268) val meshesLock: Js5ResourceProvider? = this.locTypeList!!.meshResource
						withLock(this.locTypeList!!.meshResource!!) {
							mesh = Mesh.fromResource(this.locTypeList!!.meshResource, this.objectModelIds!![shapeIndex]!![k] and 0xFFFF)
						}
						if (mesh == null) {
							return null
						}
						if (mesh!!.version < 13) {
							mesh!!.upscale()
						}
						if (modelCount > 1) {
							LocType.aClass32Array1[k] = mesh
						}
						k++
					}
					if (modelCount > 1) {
						mesh = Mesh(LocType.aClass32Array1, modelCount)
					}
				}
				model = toolkit.createModel(mesh, meshFunctionMask, this.locTypeList!!.featureMask, adjustedAmbient, adjustedContrast)
				@Pc(349) val referenceCache2: LRUCache = this.locTypeList!!.modelsByShapeCache
				withLock(this.locTypeList!!.modelsByShapeCache) {
					this.locTypeList!!.modelsByShapeCache.put(model, uid)
				}
			}
		}
		@Pc(370) val model2: Model? = model!!.copy(0.toByte(), functionMask, true)
		if (model!!.WA() != adjustedAmbient) {
			model2!!.C(adjustedAmbient)
		}
		if (model!!.da() != adjustedContrast) {
			model2!!.LA(adjustedContrast)
		}
		if (invert) {
			model2!!.v()
		}
		if (shape == 4 && rotation > 3) {
			model2!!.k(2048)
			model2.H(180, 0, -180)
		}
		@Pc(414) val rotationQuarter: Int = rotation and 0x3
		if (rotationQuarter == 1) {
			model2!!.k(4096)
		} else if (rotationQuarter == 2) {
			model2!!.k(8192)
		} else if (rotationQuarter == 3) {
			model2!!.k(12288)
		}
		@Pc(443) var index: Int
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				if (this.recolorDPalette == null || index >= this.recolorDPalette!!.size) {
					model2!!.ia(this.recolorSrc!![index], this.recolorDst!![index])
				} else {
					model2!!.ia(this.recolorSrc!![index], ObjectType.locRecolorPalette!![this.recolorDPalette!![index].toInt() and 0xFF])
				}
				index++
			}
		}
		if (this.retextureSrc != null) {
			index = 0
			while (index < this.retextureSrc!!.size) {
				model2!!.aa(this.retextureSrc!![index], this.retextureDst!![index])
				index++
			}
		}
		if (this.tintOpacity.toInt() != 0) {
			model2!!.adjustColours(this.tintHue.toInt(), this.tintSaturation.toInt(), this.tintLightness.toInt(), this.tintOpacity.toInt() and 0xFF)
		}
		if (this.resizeX != 128 || this.resizeY != 128 || this.resizeZ != 128) {
			model2!!.O(this.resizeX, this.resizeY, this.resizeZ)
		}
		if (this.offsetX != 0 || this.offsetY != 0 || this.offsetZ != 0) {
			model2!!.H(this.offsetX, this.offsetY, this.offsetZ)
		}
		model2!!.s(initialFunctionMask)
		return model2
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(II)Z")
	fun loadedModels(@OriginalArg(0) shape: Int): Boolean {
		if (this.objectModelIds == null) {
			return true
		}
		@Pc(19) val meshesLock: Js5ResourceProvider? = this.locTypeList!!.meshResource
		withLock(this.locTypeList!!.meshResource!!) {
			@Pc(23) var i: Int = 0
			while (i < this.shapes!!.size) {
				if (this.shapes!![i].toInt() == shape) {
					@Pc(33) var j: Int = 0
					while (j < this.objectModelIds!![i]!!.size) {
						if (!this.locTypeList!!.meshResource!!.isFileReady(this.objectModelIds!![i]!![j], 0)) {
							return false
						}
						j++
					}
					return true
				}
				i++
			}
			return true
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(BLclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(9) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decodeOpcode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!dd;B)Lclient!iv;")
	fun getMultiLoc(@OriginalArg(0) varDomain: VarDomain?): LocType? {
		@Pc(5) var index: Int = -1
		if (this.varpbitId != -1) {
			index = varDomain!!.getVarBitValue(this.varpbitId)
		} else if (this.varpId != -1) {
			index = varDomain!!.getVarValueInt(this.varpId)
		}
		if (index < 0 || this.alternativeIds!!.size - 1 <= index || this.alternativeIds!![index] == -1) {
			@Pc(61) val fallbackId: Int = this.alternativeIds!![this.alternativeIds!!.size - 1]
			return if (fallbackId == -1) null else this.locTypeList!!.list(fallbackId)
		} else {
			return this.locTypeList!!.list(this.alternativeIds!![index])
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(BI)Z")
	fun hasAnimation(@OriginalArg(1) animationId: Int): Boolean {
		if (animationId == -1) {
			return false
		} else if (animationId == this.animationId) {
			return true
		} else {
			if (this.animations != null) {
				@Pc(21) var i: Int = 0
				while (i < this.animations!!.size) {
					if (animationId == this.animations!![i]) {
						return true
					}
					i++
				}
			}
			return false
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!cea;IB)V")
	private fun decodeOpcode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		@Pc(391) var local391: Int
		@Pc(401) var local401: Int
		@Pc(414) var local414: Int
		@Pc(583) var local583: Int
		if (opcode == 1 || opcode == 5) {
			if (opcode == 5 && this.locTypeList!!.lowDetail) {
				this.skipUnusedModelData(packet)
			}
			val shapeCount: Int = packet!!.readUByte()
			this.objectModelIds = arrayOfNulls<IntArray>(shapeCount)
			this.shapes = ByteArray(shapeCount)
			var shapeIndex: Int = 0
			while (shapeIndex < shapeCount) {
				this.shapes!![shapeIndex] = packet.readByte()
				val modelCount: Int = packet.readUByte()
				this.objectModelIds!![shapeIndex] = IntArray(modelCount)
				var modelIndex: Int = 0
				while (modelIndex < modelCount) {
					this.objectModelIds!![shapeIndex]!![modelIndex] = packet.readUShort()
					modelIndex++
				}
				shapeIndex++
			}
			if (opcode == 5 && !this.locTypeList!!.lowDetail) {
				this.skipUnusedModelData(packet)
			}
		} else if (opcode == 2) {
			this.name = packet!!.readString()
		} else if (opcode == 14) {
			this.sizeX = packet!!.readUByte()
		} else if (opcode == 15) {
			this.sizeY = packet!!.readUByte()
		} else if (opcode == 17) {
			this.blocksProjectiles = false
			this.blocksMovement = 0
		} else if (opcode == 18) {
			this.blocksProjectiles = false
		} else if (opcode == 19) {
			this.hasActions = packet!!.readUByte()
		} else if (opcode == 21) {
			this.groundContourType = 1
		} else if (opcode == 22) {
			this.delayShading = true
		} else if (opcode == 23) {
			this.occlusionMode = LocOcclusionMode.ALL
		} else if (opcode == 24) {
			this.animationId = packet!!.readUShort()
			if (this.animationId == 65535) {
				this.animationId = -1
			}
		} else if (opcode == 27) {
			this.blocksMovement = 1
		} else if (opcode == 28) {
			this.decorDisplacement = packet!!.readUByte() shl 2
		} else if (opcode == 29) {
			this.ambient = packet!!.readByte().toInt()
		} else if (opcode == 39) {
			this.contrast = packet!!.readByte().toInt() * 5
		} else if (opcode >= 30 && opcode < 35) {
			this.actions!![opcode - 30] = packet!!.readString()
		} else if (opcode == 40) {
			val recolorCount: Int = packet!!.readUByte()
			this.recolorSrc = ShortArray(recolorCount)
			this.recolorDst = ShortArray(recolorCount)
			var recolorIndex: Int = 0
			while (recolorIndex < recolorCount) {
				this.recolorSrc!![recolorIndex] = packet.readUShort().toShort()
				this.recolorDst!![recolorIndex] = packet.readUShort().toShort()
				recolorIndex++
			}
		} else if (opcode == 41) {
			val retextureCount: Int = packet!!.readUByte()
			this.retextureDst = ShortArray(retextureCount)
			this.retextureSrc = ShortArray(retextureCount)
			var retextureIndex: Int = 0
			while (retextureIndex < retextureCount) {
				this.retextureSrc!![retextureIndex] = packet.readUShort().toShort()
				this.retextureDst!![retextureIndex] = packet.readUShort().toShort()
				retextureIndex++
			}
		} else if (opcode == 42) {
			val paletteCount: Int = packet!!.readUByte()
			this.recolorDPalette = ByteArray(paletteCount)
			var paletteIndex: Int = 0
			while (paletteIndex < paletteCount) {
				this.recolorDPalette!![paletteIndex] = packet.readByte()
				paletteIndex++
			}
		} else if (opcode == 62) {
			this.inverted = true
		} else if (opcode == 64) {
			this.staticShadow = false
		} else if (opcode == 65) {
			this.resizeX = packet!!.readUShort()
		} else if (opcode == 66) {
			this.resizeY = packet!!.readUShort()
		} else if (opcode == 67) {
			this.resizeZ = packet!!.readUShort()
		} else if (opcode == 69) {
			this.accessBlockFlags = packet!!.readUByte()
		} else if (opcode == 70) {
			this.offsetX = packet!!.readShort() shl 2
		} else if (opcode == 71) {
			this.offsetY = packet!!.readShort() shl 2
		} else if (opcode == 72) {
			this.offsetZ = packet!!.readShort() shl 2
		} else if (opcode == 73) {
			this.forceDisplayDecoration = true
		} else if (opcode == 74) {
			this.ignoreClipOnAltRoute = true
		} else if (opcode == 75) {
			this.supportsItems = packet!!.readUByte()
		} else if (opcode == 77 || opcode == 92) {
			this.varpbitId = packet!!.readUShort()
			if (this.varpbitId == 65535) {
				this.varpbitId = -1
			}
			this.varpId = packet.readUShort()
			if (this.varpId == 65535) {
				this.varpId = -1
			}
			var defaultAlternativeId: Int = -1
			if (opcode == 92) {
				defaultAlternativeId = packet.readUShort()
				if (defaultAlternativeId == 65535) {
					defaultAlternativeId = -1
				}
			}
			val alternativeCount: Int = packet.readUByte()
			this.alternativeIds = IntArray(alternativeCount + 2)
			var alternativeIndex: Int = 0
			while (alternativeIndex <= alternativeCount) {
				this.alternativeIds!![alternativeIndex] = packet.readUShort()
				if (this.alternativeIds!![alternativeIndex] == 65535) {
					this.alternativeIds!![alternativeIndex] = -1
				}
				alternativeIndex++
			}
			this.alternativeIds!![alternativeCount + 1] = defaultAlternativeId
		} else if (opcode == 78) {
			this.soundId = packet!!.readUShort()
			this.soundRadius = packet.readUByte()
		} else if (opcode == 79) {
			this.soundMinInterval = packet!!.readUShort()
			this.soundMaxInterval = packet.readUShort()
			this.soundRadius = packet.readUByte()
			val soundCount: Int = packet.readUByte()
			this.soundGroupIds = IntArray(soundCount)
			var soundIndex: Int = 0
			while (soundIndex < soundCount) {
				this.soundGroupIds!![soundIndex] = packet.readUShort()
				soundIndex++
			}
		} else if (opcode == 81) {
			this.groundContourType = 2
			this.goundContourModifier = packet!!.readUByte() * 256
		} else if (opcode == 82) {
			this.requiresTextures = true
		} else if (opcode == 88) {
			this.dynamicShadow = false
		} else if (opcode == 89) {
			this.replaySequence = false
		} else if (opcode == 91) {
			this.members = true
		} else if (opcode == 93) {
			this.groundContourType = 3
			this.goundContourModifier = packet!!.readUShort()
		} else if (opcode == 94) {
			this.groundContourType = 4
		} else if (opcode == 95) {
			this.groundContourType = 5
			this.goundContourModifier = packet!!.readShort()
		} else if (opcode == 97) {
			this.mapIconRotates = true
		} else if (opcode == 98) {
			this.hasAnimation = true
		} else if (opcode == 99) {
			this.primaryCursorActionIndex = packet!!.readUByte()
			this.primaryCursor = packet.readUShort()
		} else if (opcode == 100) {
			this.secondaryCursorActionIndex = packet!!.readUByte()
			this.secondaryCursor = packet.readUShort()
		} else if (opcode == 101) {
			this.mapIconRotation = packet!!.readUByte()
		} else if (opcode == 102) {
			this.mapIconId = packet!!.readUShort()
		} else if (opcode == 103) {
			this.occlusionMode = LocOcclusionMode.ROOFS
		} else if (opcode == 104) {
			this.soundVolume = packet!!.readUByte()
		} else if (opcode == 105) {
			this.mapIconFlipped = true
		} else if (opcode == 106) {
			val animationCount: Int = packet!!.readUByte()
			this.animations = IntArray(animationCount)
			this.animationOdds = IntArray(animationCount)
			var animationIndex: Int = 0
			while (animationIndex < animationCount) {
				this.animations!![animationIndex] = packet.readUShort()
				val odds: Int = packet.readUByte()
				this.animationOdds!![animationIndex] = odds
				this.animationOddsTotal += odds
				animationIndex++
			}
		} else if (opcode == 107) {
			this.mapCategoryid = packet!!.readUShort()
		} else if (opcode >= 150 && opcode < 155) {
			this.actions!![opcode - 150] = packet!!.readString()
			if (!this.locTypeList!!.allowMembers) {
				this.actions!![opcode - 150] = null
			}
		} else if (opcode == 160) {
			val questCount: Int = packet!!.readUByte()
			this.quests = IntArray(questCount)
			var questIndex: Int = 0
			while (questIndex < questCount) {
				this.quests!![questIndex] = packet.readUShort()
				questIndex++
			}
		} else if (opcode == 162) {
			this.groundContourType = 3
			this.goundContourModifier = packet!!.readInt()
		} else if (opcode == 163) {
			this.tintHue = packet!!.readByte()
			this.tintSaturation = packet.readByte()
			this.tintLightness = packet.readByte()
			this.tintOpacity = packet.readByte()
		} else if (opcode == 164) {
			this.shadowOffsetX = packet!!.readShort()
		} else if (opcode == 165) {
			this.shadowOffsetY = packet!!.readShort()
		} else if (opcode == 166) {
			this.shadowOffsetZ = packet!!.readShort()
		} else if (opcode == 167) {
			this.groundDecorationHeight = packet!!.readUShort()
		} else if (opcode == 168) {
			this.instrumentSoundEffect = true
		} else if (opcode == 169) {
			this.instrumentAmbientSound = true
		} else if (opcode == 170) {
			this.cullY = packet!!.readUSmart()
		} else if (opcode == 171) {
			this.cullXZ = packet!!.readUSmart()
		} else if (opcode == 173) {
			this.ambientSoundMaxDelay = packet!!.readUShort()
			this.ambientSoundMinDelay = packet.readUShort()
		} else if (opcode == 249) {
			val paramCount: Int = packet!!.readUByte()
			if (this.parameters == null) {
				val bucketCount: Int = Utils.nextPowerOfTwo(paramCount)
				this.parameters = IterableHashTable(bucketCount)
			}
			var paramIndex: Int = 0
			while (paramIndex < paramCount) {
				@Pc(579) val isString: Boolean = packet.readUByte() == 1
				val paramKey: Int = packet.readUMedium()
				@Pc(592) var node: Node?
				if (isString) {
					node = StringNode(packet.readString())
				} else {
					node = IntNode(packet.readInt())
				}
				this.parameters!!.put(paramKey.toLong(), node)
				paramIndex++
			}
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(IIIILclient!ha;IIBLclient!s;IIILclient!hg;Lclient!s;)Lclient!ka;")
	fun getAnimatedModel(@OriginalArg(0) nextFrame: Int, @OriginalArg(1) frameCycle: Int, @OriginalArg(2) shape: Int, @OriginalArg(3) averageHeight: Int, @OriginalArg(4) toolkit: Toolkit?, @OriginalArg(5) rotation: Int, @OriginalArg(6) functionMask: Int, @OriginalArg(8) ground: Ground?, @OriginalArg(9) sceneZ: Int, @OriginalArg(10) currentFrame: Int, @OriginalArg(11) sceneX: Int, @OriginalArg(12) seqType: SeqType?, @OriginalArg(13) ground2: Ground?): Model? {
		var shape: Int = shape
		var functionMask: Int = functionMask
		if (LocShapes.isWallDecoration(shape)) {
			shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET
		}
		@Pc(22) val uid: Long = ((this.id shl 10) + ((shape shl 3) + rotation)).toLong()
		@Pc(24) val initialFunctionMask: Int = functionMask
		@Pc(32) val cacheKey: Long = uid or (toolkit!!.index shl 29).toLong()
		if (seqType != null) {
			functionMask = functionMask or seqType.getFunctionMask(nextFrame, currentFrame, false)
		}
		if (this.groundContourType.toInt() == 3) {
			functionMask = functionMask or 0x7
		} else {
			if (this.groundContourType.toInt() != 0 || this.shadowOffsetY != 0) {
				functionMask = functionMask or 0x2
			}
			if (this.shadowOffsetX != 0) {
				functionMask = functionMask or 0x1
			}
			if (this.shadowOffsetZ != 0) {
				functionMask = functionMask or 0x4
			}
		}
		if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
			functionMask = functionMask or 0x5
		}
		@Pc(104) var referenceCache: LRUCache = this.locTypeList!!.modelCache
		@Pc(114) var model: Model?
		withLock(this.locTypeList!!.modelCache) {
			model = this.locTypeList!!.modelCache.get(cacheKey) as Model?
		}
		if (model == null || toolkit.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit.combineFunctionMasks(functionMask, model!!.ua())
			}
			model = this.getStationaryModel(rotation, toolkit, shape, functionMask)
			if (model == null) {
				return null
			}
			referenceCache = this.locTypeList!!.modelCache
			withLock(this.locTypeList!!.modelCache) {
				this.locTypeList!!.modelCache.put(model, cacheKey)
			}
		}
		@Pc(169) var copied: Boolean = false
		if (seqType != null) {
			model = seqType.animateRotatedModel(model, rotation and 0x3, nextFrame, 1.toByte(), currentFrame, frameCycle, functionMask)
			copied = true
		}
		if (shape == LocShapes.CENTREPIECE_STRAIGHT && rotation > 3) {
			if (!copied) {
				model = model!!.copy(3.toByte(), functionMask, true)
				copied = true
			}
			model!!.a(2048)
		}
		if (this.groundContourType.toInt() != 0) {
			if (!copied) {
				copied = true
				model = model!!.copy(3.toByte(), functionMask, true)
			}
			model!!.p(this.groundContourType.toInt(), this.goundContourModifier, ground2, ground, sceneX, averageHeight, sceneZ)
		}
		if (this.shadowOffsetX != 0 || this.shadowOffsetY != 0 || this.shadowOffsetZ != 0) {
			if (!copied) {
				copied = true
				model = model!!.copy(3.toByte(), functionMask, true)
			}
			model!!.H(this.shadowOffsetX, this.shadowOffsetY, this.shadowOffsetZ)
		}
		if (copied) {
			model!!.s(initialFunctionMask)
		}
		return model
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(Lclient!cea;I)V")
	private fun skipUnusedModelData(@OriginalArg(0) packet: Packet?) {
		@Pc(14) val count: Int = packet!!.readUByte()
		@Pc(16) var i: Int = 0
		while (i < count) {
			packet.currentPosition++
			@Pc(30) val length: Int = packet.readUByte()
			packet.currentPosition += length * 2
			i++
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(I)Z")
	fun isLoaded(): Boolean {
		if (this.objectModelIds == null) {
			return true
		}
		@Pc(19) var loaded: Boolean = true
		@Pc(23) val meshesLock: Js5ResourceProvider? = this.locTypeList!!.meshResource
		withLock(this.locTypeList!!.meshResource!!) {
			@Pc(27) var i: Int = 0
			while (i < this.objectModelIds!!.size) {
				@Pc(31) var j: Int = 0
				while (j < this.objectModelIds!![i]!!.size) {
					loaded = loaded and this.locTypeList!!.meshResource!!.isFileReady(this.objectModelIds!![i]!![j], 0)
					j++
				}
				i++
			}
			return loaded
		}
	}

	@OriginalMember(owner = "client!iv", name = "a", descriptor = "(III)I")
	fun getIntParam(@OriginalArg(0) key: Int, @OriginalArg(1) defaultInt: Int): Int {
		if (this.parameters == null) {
			return defaultInt
		} else {
			@Pc(24) val intNode: IntNode? = this.parameters!!.get(key.toLong()) as IntNode?
			return if (intNode == null) defaultInt else intNode.value
		}
	}

	@OriginalMember(owner = "client!iv", name = "c", descriptor = "(I)V")
	fun postDecode() {
		if (this.hasActions == -1) {
			this.hasActions = LocInteractivity.NONINTERACTIVE
			if (this.shapes != null && this.shapes!!.size == 1 && this.shapes!![0].toInt() == 10) {
				this.hasActions = LocInteractivity.INTERACTIVE
			}
			@Pc(30) var i: Int = 0
			while (i < 5) {
				if (this.actions!![i] != null) {
					this.hasActions = LocInteractivity.INTERACTIVE
					break
				}
				i++
			}
		}
		if (this.supportsItems == -1) {
			this.supportsItems = if (this.blocksMovement == 0) 0 else 1
		}
	}

	@OriginalMember(owner = "client!iv", name = "d", descriptor = "(I)I")
	fun randomAnimation(): Int {
		if (this.animations == null) {
			return -1
		}
		@Pc(15) var random: Int = (util.random() * this.animationOddsTotal.toDouble()).toInt()
		@Pc(17) var i: Int
		i = 0
		while (this.animationOdds!![i] <= random) {
			random -= this.animationOdds!![i]
			i++
		}
		return this.animations!![i]
	}

	companion object {
		@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IZI)Z")
		@JvmStatic
		fun isModelReady(@OriginalArg(0) shape: Int, @OriginalArg(2) locId: Int): Boolean {
			var shape: Int = shape
			@Pc(8) val locType: LocType = TypeLists.LOC!!.list(locId)
			if (shape == 11) {
				shape = 10
			}
			if (shape >= LocShapes.WALLDECOR_STRAIGHT_OFFSET && shape <= LocShapes.WALLDECOR_DIAGONAL_BOTH) {
				shape = LocShapes.WALLDECOR_STRAIGHT_NOOFFSET
			}
			return locType.loadedModels(shape)
		}
		@OriginalMember(owner = "client!ec", name = "k", descriptor = "[Lclient!bh;")
		@JvmField
		val aClass32Array1: Array<Mesh?> = arrayOfNulls<Mesh>(4)

		@OriginalMember(owner = "client!vl", name = "p", descriptor = "Lclient!ak;")
		@JvmField
		val aClass16_31: ModelAndShadow = ModelAndShadow()
	}
}
