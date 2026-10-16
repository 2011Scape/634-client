// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

package com.jagex.game.runetek5.entity.pathingentity.player

import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.bas.BasTypeList
import com.jagex.game.runetek5.config.idktype.IDKTypeList
import com.jagex.game.runetek5.config.npctype.NPCTypeList
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.hint.HintTrail
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!oo")
class PlayerAppearance {

	@OriginalMember(owner = "client!oo", name = "c", descriptor = "[I")
	private var appearance: IntArray? = null

	@OriginalMember(owner = "client!oo", name = "e", descriptor = "J")
	private var bodyModelHash: Long = 0L

	@OriginalMember(owner = "client!oo", name = "f", descriptor = "[I")
	@JvmField
	var recolorDstIndices: IntArray? = null

	@OriginalMember(owner = "client!oo", name = "k", descriptor = "Z")
	@JvmField
	var female: Boolean = false

	@OriginalMember(owner = "client!oo", name = "m", descriptor = "J")
	private var playerAppearanceHash: Long = 0L

	@OriginalMember(owner = "client!oo", name = "n", descriptor = "I")
	private var basId: Int = 0

	@OriginalMember(owner = "client!oo", name = "d", descriptor = "I")
	@JvmField
	var morphism: Int = -1

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(Lclient!dd;Lclient!hg;[Lclient!qca;ZLclient!ub;ILclient!hg;IZI[IILclient!oha;ILclient!ha;Lclient!vda;Lclient!lfa;IIILclient!uga;)Lclient!ka;")
	fun getBodyModel(@OriginalArg(0) varDomain: VarDomain?, @OriginalArg(1) seqType: SeqType?, @OriginalArg(2) sequences: Array<PathingEntitySequence?>?, @OriginalArg(4) objTypeList: ObjTypeList?, @OriginalArg(5) rotation: Int, @OriginalArg(6) seqType2: SeqType?, @OriginalArg(7) animNextFrameIndex: Int, @OriginalArg(9) moveNextFrameIndex: Int, @OriginalArg(10) wornRotations: IntArray?, @OriginalArg(11) initialFunctionMask: Int, @OriginalArg(12) idkTypeList: IDKTypeList?, @OriginalArg(13) animFrameDuration: Int, @OriginalArg(14) toolkit: Toolkit?, @OriginalArg(15) npcTypeList: NPCTypeList?, @OriginalArg(16) seqTypeList: SeqTypeList?, @OriginalArg(17) moveFrameIndex: Int, @OriginalArg(18) animFrameIndex: Int, @OriginalArg(19) moveFrameDuration: Int, @OriginalArg(20) basTypeList: BasTypeList?): Model? {
		if (this.morphism != -1) {
			return npcTypeList!!.list(this.morphism).getBodyModel(rotation, sequences, seqTypeList, seqType, moveFrameDuration, basTypeList, animFrameIndex, seqType2, varDomain, toolkit, moveNextFrameIndex, wornRotations, animNextFrameIndex, moveFrameIndex, initialFunctionMask, animFrameDuration)
		}
		@Pc(39) var functionMask: Int = initialFunctionMask
		@Pc(42) var appearanceHash: Long = this.playerAppearanceHash
		@Pc(45) var appearanceParts: IntArray? = this.appearance
		if (seqType2 != null && (seqType2.replacementShield >= 0 || seqType2.replacementWeapon >= 0)) {
			appearanceParts = IntArray(12)
			@Pc(64) var i: Int = 0
			while (i < 12) {
				appearanceParts[i] = this.appearance!![i]
				i++
			}
			if (seqType2.replacementShield >= 0) {
				if (seqType2.replacementShield == 65535) {
					appearanceParts[5] = 0
					appearanceHash = appearanceHash xor -0x100000000L
				} else {
					appearanceParts[5] = seqType2.replacementShield or 0x40000000
					appearanceHash = appearanceHash xor (appearanceParts[5].toLong() shl 32)
				}
			}
			if (seqType2.replacementWeapon >= 0) {
				if (seqType2.replacementWeapon == 65535) {
					appearanceParts[3] = 0
					appearanceHash = appearanceHash xor 0xFFFFFFFFL
				} else {
					appearanceParts[3] = seqType2.replacementWeapon or 0x40000000
					appearanceHash = appearanceHash xor appearanceParts[3].toLong()
				}
			}
		}
		@Pc(148) var modifiesAlpha: Boolean = false
		@Pc(150) var modifiesColor: Boolean = false
		@Pc(152) var modifiesBillboard: Boolean = false
		@Pc(160) var animated: Boolean = seqType2 != null || seqType != null
		@Pc(167) val sequenceCount: Int = if (sequences == null) 0 else sequences.size
		@Pc(201) var local201: Int
		@Pc(206) var local206: Int
		@Pc(169) var j: Int = 0
		while (j < sequenceCount) {
			PlayerAppearance.aClass2_Sub6_Sub9Array3[j] = null
			if (sequences!![j] != null) {
				@Pc(187) val seqType3: SeqType = seqTypeList!!.list(sequences[j]!!.sequenceId)
				if (seqType3.frames != null) {
					animated = true
					PlayerAppearance.aClass130Array1[j] = seqType3
					val currentFrameIndex: Int = sequences[j]!!.currentFrameId
					val nextFrameIndex: Int = sequences[j]!!.nextFrameId
					@Pc(211) val packedFrame: Int = seqType3.frames!![currentFrameIndex]
					PlayerAppearance.aClass2_Sub6_Sub9Array3[j] = seqTypeList.listFrameSet(packedFrame ushr 16)
					@Pc(224) val frame: Int = packedFrame and 0xFFFF
					PlayerAppearance.frameIds[j] = frame
					if (PlayerAppearance.aClass2_Sub6_Sub9Array3[j] != null) {
						modifiesColor = modifiesColor or PlayerAppearance.aClass2_Sub6_Sub9Array3[j]!!.modifiesColors(frame)
						modifiesAlpha = modifiesAlpha or PlayerAppearance.aClass2_Sub6_Sub9Array3[j]!!.modifiesAlpha(frame)
						modifiesBillboard = modifiesBillboard or PlayerAppearance.aClass2_Sub6_Sub9Array3[j]!!.modifiesBillboards(frame)
					}
					if ((seqType3.tweened || SeqType.forceTweening) && nextFrameIndex != -1 && seqType3.frames!!.size > nextFrameIndex) {
						PlayerAppearance.frameDurations[j] = seqType3.durations!![currentFrameIndex]
						PlayerAppearance.frameCycles[j] = sequences[j]!!.currentFrameDuration
						@Pc(291) val nextPackedFrame: Int = seqType3.frames!![nextFrameIndex]
						PlayerAppearance.aClass2_Sub6_Sub9Array4[j] = seqTypeList.listFrameSet(nextPackedFrame ushr 16)
						@Pc(304) val nextFrame: Int = nextPackedFrame and 0xFFFF
						PlayerAppearance.nextFrameIds[j] = nextFrame
						if (PlayerAppearance.aClass2_Sub6_Sub9Array4[j] != null) {
							modifiesColor = modifiesColor or PlayerAppearance.aClass2_Sub6_Sub9Array4[j]!!.modifiesColors(nextFrame)
							modifiesAlpha = modifiesAlpha or PlayerAppearance.aClass2_Sub6_Sub9Array4[j]!!.modifiesAlpha(nextFrame)
							modifiesBillboard = modifiesBillboard or PlayerAppearance.aClass2_Sub6_Sub9Array4[j]!!.modifiesBillboards(nextFrame)
						}
					} else {
						PlayerAppearance.frameDurations[j] = 0
						PlayerAppearance.frameCycles[j] = 0
						PlayerAppearance.aClass2_Sub6_Sub9Array4[j] = null
						PlayerAppearance.nextFrameIds[j] = -1
					}
				}
			}
			j++
		}
		@Pc(363) var animFrame: Int = -1
		var animNextFrame: Int = -1
		var animFrameLength: Int = 0
		@Pc(369) var animFrameset: FrameSet? = null
		@Pc(371) var animFrameset2: FrameSet? = null
		@Pc(373) var moveFrame: Int = -1
		@Pc(375) var moveNextFrame: Int = -1
		@Pc(377) var moveFrameLength: Int = 0
		@Pc(379) var animFrameset3: FrameSet? = null
		@Pc(381) var animFrameset4: FrameSet? = null
		if (animated) {
			@Pc(394) var framesetId: Int
			@Pc(458) var nextFramesetId: Int
			if (seqType2 != null) {
				animFrame = seqType2.frames!![animFrameIndex]
				framesetId = animFrame ushr 16
				animFrameset = seqTypeList!!.listFrameSet(framesetId)
				animFrame = animFrame and 0xFFFF
				if (animFrameset != null) {
					modifiesColor = modifiesColor or animFrameset.modifiesColors(animFrame)
					modifiesAlpha = modifiesAlpha or animFrameset.modifiesAlpha(animFrame)
					modifiesBillboard = modifiesBillboard or animFrameset.modifiesBillboards(animFrame)
				}
				if ((seqType2.tweened || SeqType.forceTweening) && animNextFrameIndex != -1 && seqType2.frames!!.size > animNextFrameIndex) {
					animNextFrame = seqType2.frames!![animNextFrameIndex]
					animFrameLength = seqType2.durations!![animFrameIndex]
					nextFramesetId = animNextFrame ushr 16
					animFrameset2 = if (nextFramesetId == framesetId) animFrameset else seqTypeList.listFrameSet(nextFramesetId)
					animNextFrame = animNextFrame and 0xFFFF
					if (animFrameset2 != null) {
						modifiesColor = modifiesColor or animFrameset2.modifiesColors(animNextFrame)
						modifiesAlpha = modifiesAlpha or animFrameset2.modifiesAlpha(animNextFrame)
						modifiesBillboard = modifiesBillboard or animFrameset2.modifiesBillboards(animNextFrame)
					}
				}
			}
			functionMask = initialFunctionMask or 0x20
			if (seqType != null) {
				moveFrame = seqType.frames!![moveFrameIndex]
				framesetId = moveFrame ushr 16
				moveFrame = moveFrame and 0xFFFF
				animFrameset3 = seqTypeList!!.listFrameSet(framesetId)
				if (animFrameset3 != null) {
					modifiesColor = modifiesColor or animFrameset3.modifiesColors(moveFrame)
					modifiesAlpha = modifiesAlpha or animFrameset3.modifiesAlpha(moveFrame)
					modifiesBillboard = modifiesBillboard or animFrameset3.modifiesBillboards(moveFrame)
				}
				if ((seqType.tweened || SeqType.forceTweening) && moveNextFrameIndex != -1 && seqType.frames!!.size > moveNextFrameIndex) {
					moveNextFrame = seqType.frames!![moveNextFrameIndex]
					moveFrameLength = seqType.durations!![moveFrameIndex]
					nextFramesetId = moveNextFrame ushr 16
					moveNextFrame = moveNextFrame and 0xFFFF
					animFrameset4 = if (framesetId == nextFramesetId) animFrameset3 else seqTypeList.listFrameSet(nextFramesetId)
					if (animFrameset4 != null) {
						modifiesColor = modifiesColor or animFrameset4.modifiesColors(moveNextFrame)
						modifiesAlpha = modifiesAlpha or animFrameset4.modifiesAlpha(moveNextFrame)
						modifiesBillboard = modifiesBillboard or animFrameset4.modifiesBillboards(moveNextFrame)
					}
				}
			}
			if (modifiesColor) {
				functionMask = functionMask or 0x80
			}
			if (modifiesAlpha) {
				functionMask = functionMask or 0x100
			}
			if (modifiesBillboard) {
				functionMask = functionMask or 0x400
			}
		}
		@Pc(639) val referenceCache: LRUCache = PlayerAppearance.aClass169_4
		@Pc(647) var model: Model?
		withLock(PlayerAppearance.aClass169_4) {
			model = PlayerAppearance.aClass169_4.get(appearanceHash) as Model?
		}
		@Pc(655) var basType: BasType? = null
		if (this.basId != -1) {
			basType = basTypeList!!.list(this.basId)
		}
		@Pc(686) var local686: Boolean
		@Pc(688) var slot: Int
		@Pc(694) var local694: Int
		@Pc(739) var local739: Int
		@Pc(745) var local745: Int
		@Pc(818) var local818: Int
		if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit!!.combineFunctionMasks(functionMask, model!!.ua())
			}
			var partsNotCached: Boolean = false
			slot = 0
			while (slot < 12) {
				val appearancePart: Int = appearanceParts!![slot]
				if ((appearancePart and 0x40000000) == 0) {
					if ((Int.MIN_VALUE and appearancePart) != 0 && !idkTypeList!!.list(appearancePart and 0x3FFFFFFF).isBodyCached()) {
						partsNotCached = true
					}
				} else if (!objTypeList!!.list(appearancePart and 0x3FFFFFFF).isBodyCached(this.female)) {
					partsNotCached = true
				}
				slot++
			}
			if (partsNotCached) {
				if (this.bodyModelHash != -1L) {
					@Pc(1042) val referenceCache2: LRUCache = PlayerAppearance.aClass169_4
					withLock(PlayerAppearance.aClass169_4) {
						model = PlayerAppearance.aClass169_4.get(this.bodyModelHash) as Model?
					}
				}
				if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
					return null
				}
			} else {
				@Pc(737) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(12)
				var meshSlot: Int = 0
				while (meshSlot < 12) {
					val part: Int = appearanceParts!![meshSlot]
					@Pc(768) var mesh: Mesh?
					if ((part and 0x40000000) != 0) {
						mesh = objTypeList!!.list(part and 0x3FFFFFFF).getBodyMesh(this.female)
						if (mesh != null) {
							meshes[meshSlot] = mesh
						}
					} else if ((Int.MIN_VALUE and part) != 0) {
						mesh = idkTypeList!!.list(part and 0x3FFFFFFF).getBodyMesh()
						if (mesh != null) {
							meshes[meshSlot] = mesh
						}
					}
					meshSlot++
				}
				@Pc(820) var local820: Int
				if (basType != null && basType.equipmentTransforms != null) {
					var transformSlot: Int = 0
					while (transformSlot < basType.equipmentTransforms!!.size) {
						if (meshes[transformSlot] != null) {
							var translateX: Int = 0
							var translateY: Int = 0
							@Pc(822) var translateZ: Int = 0
							@Pc(824) var rotateX: Int = 0
							@Pc(826) var rotateY: Int = 0
							@Pc(828) var rotateZ: Int = 0
							if (basType.equipmentTransforms!![transformSlot] != null) {
								rotateY = basType.equipmentTransforms!![transformSlot]!![4] shl 3
								translateY = basType.equipmentTransforms!![transformSlot]!![1]
								rotateZ = basType.equipmentTransforms!![transformSlot]!![5] shl 3
								translateX = basType.equipmentTransforms!![transformSlot]!![0]
								rotateX = basType.equipmentTransforms!![transformSlot]!![3] shl 3
								translateZ = basType.equipmentTransforms!![transformSlot]!![2]
							}
							if (rotateX != 0 || rotateY != 0 || rotateZ != 0) {
								meshes[transformSlot]!!.rotateWithoutNormals(rotateY, rotateZ, rotateX)
							}
							if (translateX != 0 || translateY != 0 || translateZ != 0) {
								meshes[transformSlot]!!.translate(translateZ, translateX, translateY)
							}
						}
						transformSlot++
					}
				}
				@Pc(941) val mesh2: Mesh = Mesh(meshes, meshes.size)
				@Pc(945) val adjustmentFunctionMask: Int = functionMask or 0x4000
				model = toolkit!!.createModel(mesh2, adjustmentFunctionMask, HintTrail.trailPreferenceFlags, 64, 850)
				var recolorSlot: Int = 0
				while (recolorSlot < 5) {
					var paletteIndex: Int = 0
					while (paletteIndex < PlayerAppearance.recolorPaletteDst!!.size) {
						if (this.recolorDstIndices!![recolorSlot] < PlayerAppearance.recolorPaletteDst!![paletteIndex]!![recolorSlot]!!.size) {
							model!!.ia(PlayerAppearance.recolorPaletteSrc!![paletteIndex]!![recolorSlot], PlayerAppearance.recolorPaletteDst!![paletteIndex]!![recolorSlot]!![this.recolorDstIndices!![recolorSlot]])
						}
						paletteIndex++
					}
					recolorSlot++
				}
				model!!.s(functionMask)
				@Pc(1015) val referenceCache3: LRUCache = PlayerAppearance.aClass169_4
				withLock(PlayerAppearance.aClass169_4) {
					PlayerAppearance.aClass169_4.put(model, appearanceHash)
				}
				this.bodyModelHash = appearanceHash
			}
		}
		@Pc(1073) val model2: Model? = model!!.copy(4.toByte(), functionMask, true)
		var hasWornRotation: Boolean = false
		if (wornRotations != null) {
			slot = 0
			while (slot < 12) {
				if (wornRotations[slot] != -1) {
					hasWornRotation = true
				}
				slot++
			}
		}
		if (!animated && !hasWornRotation) {
			return model2
		}
		@Pc(1102) var matrixes: Array<Matrix?>? = null
		if (basType != null) {
			matrixes = basType.getSubmeshMatrices(toolkit)
		}
		if (hasWornRotation && matrixes != null) {
			var submeshIndex: Int = 0
			while (submeshIndex < 12) {
				if (matrixes[submeshIndex] != null) {
					model2!!.transform(matrixes[submeshIndex], 0x1 shl submeshIndex, true)
				}
				submeshIndex++
			}
		}
		var sequenceIndex: Int = 0
		var submeshMask: Int = 1
		while (sequenceIndex < sequenceCount) {
			if (PlayerAppearance.aClass2_Sub6_Sub9Array3[sequenceIndex] != null) {
				model2!!.animateUnrotatedSubmesh(PlayerAppearance.frameCycles[sequenceIndex] - 1, null, submeshMask, PlayerAppearance.frameIds[sequenceIndex], PlayerAppearance.nextFrameIds[sequenceIndex], PlayerAppearance.aClass2_Sub6_Sub9Array4[sequenceIndex], PlayerAppearance.aClass2_Sub6_Sub9Array3[sequenceIndex], false, PlayerAppearance.frameDurations[sequenceIndex])
			}
			submeshMask = submeshMask shl 0x1
			sequenceIndex++
		}
		if (hasWornRotation) {
			var rotationSlot: Int = 0
			while (rotationSlot < 12) {
				if (wornRotations!![rotationSlot] != -1) {
					var angle: Int = wornRotations[rotationSlot] - rotation
					angle = angle and 0x3FFF
					@Pc(1209) val matrix: Matrix = toolkit!!.createMatrix()!!
					matrix.rotate(angle)
					model2!!.transform(matrix, 0x1 shl rotationSlot, false)
				}
				rotationSlot++
			}
		}
		if (hasWornRotation && matrixes != null) {
			var matrixSlot: Int = 0
			while (matrixSlot < 12) {
				if (matrixes[matrixSlot] != null) {
					model2!!.transform(matrixes[matrixSlot], 0x1 shl matrixSlot, false)
				}
				matrixSlot++
			}
		}
		if (animFrameset != null && animFrameset3 != null) {
			model2!!.doubleAnimation(animFrameset2, animFrameLength, moveFrameLength, animFrameset3, animFrame, moveFrame, animFrameDuration - 1, animFrameset, false, animNextFrame, animFrameset4, moveFrameDuration - 1, seqType2!!.interLeaveOrder, moveNextFrame)
		} else if (animFrameset != null) {
			model2!!.animateModelFrame(animFrame, animFrameLength, animFrameset2, 0, animFrameset, false, animNextFrame, animFrameDuration - 1)
		} else if (animFrameset3 != null) {
			model2!!.animateModelFrame(moveFrame, moveFrameLength, animFrameset4, 0, animFrameset3, false, moveNextFrame, moveFrameDuration - 1)
		}
		var seqSlot: Int = 0
		while (seqSlot < sequenceCount) {
			PlayerAppearance.aClass2_Sub6_Sub9Array3[seqSlot] = null
			PlayerAppearance.aClass2_Sub6_Sub9Array4[seqSlot] = null
			PlayerAppearance.aClass130Array1[seqSlot] = null
			seqSlot++
		}
		return model2
	}

	/** Not part of the original client: a copy of the worn parts, for the smoke test's outfits to put back. */
	fun copyParts(): IntArray? {
		return this.appearance?.copyOf()
	}

	/** Not part of the original client: puts back parts taken with copyParts. */
	fun restoreParts(parts: IntArray) {
		val appearance: IntArray = this.appearance ?: return
		for (i in 0 until kotlin.math.min(parts.size, appearance.size)) {
			appearance[i] = parts[i]
		}
		this.calculateAppearanceHash()
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(BIILclient!ub;)V")
	fun addAppearanceItem(@OriginalArg(1) slot: Int, @OriginalArg(2) objId: Int, @OriginalArg(3) objTypeList: ObjTypeList?) {
		if (objId == -1) {
			this.appearance!![slot] = 0
		} else if (objTypeList!!.list(objId) != null) {
			this.appearance!![slot] = objId or 0x40000000
			this.calculateAppearanceHash()
		}
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(III[IZ[I)V")
	fun setAppearance(@OriginalArg(0) morphism: Int, @OriginalArg(2) basId: Int, @OriginalArg(3) appearance: IntArray?, @OriginalArg(4) female: Boolean, @OriginalArg(5) recolorDstIndices: IntArray?) {
		if (this.basId != basId) {
			this.basId = basId
		}
		this.appearance = appearance
		this.recolorDstIndices = recolorDstIndices
		this.morphism = morphism
		this.female = female
		this.calculateAppearanceHash()
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(BZ)V")
	fun changeGender(@OriginalArg(1) female: Boolean) {
		this.female = female
		this.calculateAppearanceHash()
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(Lclient!ub;ILclient!lfa;Lclient!oha;Lclient!dd;Lclient!vda;IIILclient!hg;ILclient!ha;)Lclient!ka;")
	fun getAnimatedHeadModel(@OriginalArg(0) objTypeList: ObjTypeList?, @OriginalArg(2) seqTypeList: SeqTypeList?, @OriginalArg(3) idkTypeList: IDKTypeList?, @OriginalArg(4) varDomain: VarDomain?, @OriginalArg(5) npcTypeList: NPCTypeList?, @OriginalArg(6) currentFrameId: Int, @OriginalArg(7) nextFrameId: Int, @OriginalArg(9) seqType: SeqType?, @OriginalArg(10) currentFrameDuration: Int, @OriginalArg(11) toolkit: Toolkit?): Model? {
		if (this.morphism != -1) {
			return npcTypeList!!.list(this.morphism).getAnimatedHeadModel(varDomain, toolkit, currentFrameDuration, nextFrameId, seqType, seqTypeList, currentFrameId)
		}
		@Pc(28) var functionMask: Int = 2048
		@Pc(36) var local36: Boolean
		@Pc(55) var local55: Int
		@Pc(114) var local114: Int
		@Pc(59) var local59: Int
		@Pc(118) var local118: Int
		if (seqType != null) {
			@Pc(32) var modifiesAlpha: Boolean = false
			@Pc(34) var modifiesColor: Boolean = false
			var modifiesLights: Boolean = false
			@Pc(38) var modifiesBillboard: Boolean = false
			functionMask = 2080
			val packedFrame: Int = seqType.frames!![currentFrameId]
			val framesetId: Int = packedFrame ushr 16
			@Pc(63) val frame: Int = packedFrame and 0xFFFF
			@Pc(68) val animFrameset: FrameSet? = seqTypeList!!.listFrameSet(framesetId)
			if (animFrameset != null) {
				modifiesColor = animFrameset.modifiesColors(frame) or false
				modifiesAlpha = animFrameset.modifiesAlpha(frame) or false
				modifiesBillboard = animFrameset.modifiesBillboards(frame) or false
				modifiesLights = seqType.lights or false
			}
			if ((seqType.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < seqType.frames!!.size) {
				val nextPackedFrame: Int = seqType.frames!![nextFrameId]
				val nextFramesetId: Int = nextPackedFrame ushr 16
				@Pc(134) val animFrameset2: FrameSet? = if (framesetId == nextFramesetId) animFrameset else seqTypeList.listFrameSet(nextFramesetId)
				@Pc(138) val nextFrame: Int = nextPackedFrame and 0xFFFF
				if (animFrameset2 != null) {
					modifiesColor = modifiesColor or animFrameset2.modifiesColors(nextFrame)
					modifiesAlpha = modifiesAlpha or animFrameset2.modifiesAlpha(nextFrame)
					modifiesBillboard = modifiesBillboard or animFrameset2.modifiesBillboards(nextFrame)
				}
			}
			if (modifiesColor) {
				functionMask = 2208
			}
			if (modifiesAlpha) {
				functionMask = functionMask or 0x100
			}
			if (modifiesLights) {
				functionMask = functionMask or 0x200
			}
			if (modifiesBillboard) {
				functionMask = functionMask or 0x400
			}
		}
		@Pc(193) val referenceCache: LRUCache = PlayerAppearance.aClass169_18
		@Pc(204) var model: Model?
		withLock(PlayerAppearance.aClass169_18) {
			model = PlayerAppearance.aClass169_18.get(this.playerAppearanceHash) as Model?
		}
		if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit!!.combineFunctionMasks(functionMask, model!!.ua())
			}
			var partsNotCached: Boolean = false
			@Pc(235) var i: Int = 0
			while (i < 12) {
				val part: Int = this.appearance!![i]
				if ((part and 0x40000000) == 0) {
					if ((Int.MIN_VALUE and part) != 0 && !idkTypeList!!.list(part and 0x3FFFFFFF).isHeadCached()) {
						partsNotCached = true
					}
				} else if (!objTypeList!!.list(part and 0x3FFFFFFF).isHeadCached(this.female)) {
					partsNotCached = true
				}
				i++
			}
			if (partsNotCached) {
				return null
			}
			@Pc(295) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(12)
			var meshCount: Int = 0
			@Pc(299) var j: Int = 0
			while (j < 12) {
				@Pc(306) val appearancePart: Int = this.appearance!![j]
				@Pc(326) var mesh: Mesh?
				if ((appearancePart and 0x40000000) != 0) {
					mesh = objTypeList!!.list(appearancePart and 0x3FFFFFFF).getHeadMesh(this.female)
					if (mesh != null) {
						meshes[meshCount++] = mesh
					}
				} else if ((appearancePart and Int.MIN_VALUE) != 0) {
					mesh = idkTypeList!!.list(appearancePart and 0x3FFFFFFF).getHeadMesh()
					if (mesh != null) {
						meshes[meshCount++] = mesh
					}
				}
				j++
			}
			@Pc(363) val adjustmentFunctionMask: Int = functionMask or 0x4000
			@Pc(369) val mesh2: Mesh = Mesh(meshes, meshCount)
			model = toolkit!!.createModel(mesh2, adjustmentFunctionMask, HintTrail.trailPreferenceFlags, 64, 768)
			var recolorSlot: Int = 0
			while (recolorSlot < 5) {
				var paletteIndex: Int = 0
				while (paletteIndex < PlayerAppearance.recolorPaletteDst!!.size) {
					if (PlayerAppearance.recolorPaletteDst!![paletteIndex]!![recolorSlot]!!.size > this.recolorDstIndices!![recolorSlot]) {
						model!!.ia(PlayerAppearance.recolorPaletteSrc!![paletteIndex]!![recolorSlot], PlayerAppearance.recolorPaletteDst!![paletteIndex]!![recolorSlot]!![this.recolorDstIndices!![recolorSlot]])
					}
					paletteIndex++
				}
				recolorSlot++
			}
			model!!.s(functionMask)
			@Pc(433) val referenceCache2: LRUCache = PlayerAppearance.aClass169_18
			withLock(PlayerAppearance.aClass169_18) {
				PlayerAppearance.aClass169_18.put(model, this.playerAppearanceHash)
			}
		}
		if (seqType == null) {
			return model
		} else {
			model!!.copy(4.toByte(), functionMask, true)
			return seqType.animateModel(model, nextFrameId, currentFrameDuration, 2048, currentFrameId)
		}
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(III)V")
	fun recolor(@OriginalArg(0) slot: Int, @OriginalArg(2) colorIndex: Int) {
		this.recolorDstIndices!![slot] = colorIndex
		this.calculateAppearanceHash()
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(IIIILclient!lfa;Lclient!oha;IIILclient!ha;ILclient!hg;)Lclient!ka;")
	fun getPlainAnimatedHeadModel(@OriginalArg(0) nextFrameId: Int, @OriginalArg(2) tertiaryHead: Int, @OriginalArg(3) secondaryHead: Int, @OriginalArg(4) seqTypeList: SeqTypeList?, @OriginalArg(5) idkTypeList: IDKTypeList?, @OriginalArg(7) primaryHead: Int, @OriginalArg(8) currentFrameDuration: Int, @OriginalArg(9) toolkit: Toolkit?, @OriginalArg(10) currentFrameId: Int, @OriginalArg(11) seqType: SeqType?): Model? {
		@Pc(7) var functionMask: Int = 2048
		@Pc(95) var local95: Int
		@Pc(38) var local38: Int
		if (seqType != null) {
			@Pc(11) var modifiesAlpha: Boolean = false
			@Pc(13) var modifiesColor: Boolean = false
			@Pc(15) var modifiesLights: Boolean = false
			@Pc(17) var modifiesBillboard: Boolean = false
			functionMask = 2080
			@Pc(34) val packedFrame: Int = seqType.frames!![currentFrameId]
			val framesetId: Int = packedFrame ushr 16
			@Pc(43) val animFrameset: FrameSet? = seqTypeList!!.listFrameSet(framesetId)
			@Pc(47) val frame: Int = packedFrame and 0xFFFF
			if (animFrameset != null) {
				modifiesColor = animFrameset.modifiesColors(frame) or false
				modifiesAlpha = animFrameset.modifiesAlpha(frame) or false
				modifiesBillboard = animFrameset.modifiesBillboards(frame) or false
				modifiesLights = seqType.lights or false
			}
			if ((seqType.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < seqType.frames!!.size) {
				val nextPackedFrame: Int = seqType.frames!![nextFrameId]
				@Pc(99) val nextFramesetId: Int = nextPackedFrame ushr 16
				@Pc(103) val nextFrame: Int = nextPackedFrame and 0xFFFF
				@Pc(117) val animFrameset2: FrameSet?
				if (nextFramesetId == framesetId) {
					animFrameset2 = animFrameset
				} else {
					animFrameset2 = seqTypeList.listFrameSet(nextFrame ushr 16)
				}
				if (animFrameset2 != null) {
					modifiesColor = modifiesColor or animFrameset2.modifiesColors(nextFrame)
					modifiesAlpha = modifiesAlpha or animFrameset2.modifiesAlpha(nextFrame)
					modifiesBillboard = modifiesBillboard or animFrameset2.modifiesBillboards(nextFrame)
				}
			}
			if (modifiesColor) {
				functionMask = 2208
			}
			if (modifiesAlpha) {
				functionMask = functionMask or 0x100
			}
			if (modifiesLights) {
				functionMask = functionMask or 0x200
			}
			if (modifiesBillboard) {
				functionMask = functionMask or 0x400
			}
		}
		@Pc(180) val headUid: Long = primaryHead.toLong() or (secondaryHead shl 16).toLong() or (tertiaryHead.toLong() shl 32)
		@Pc(182) val referenceCache: LRUCache = PlayerAppearance.aClass169_18
		@Pc(192) var model: Model?
		withLock(PlayerAppearance.aClass169_18) {
			model = PlayerAppearance.aClass169_18.get(headUid) as Model?
		}
		if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit!!.combineFunctionMasks(functionMask, model!!.ua())
			}
			@Pc(219) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(3)
			var meshCount: Int = 0
			if (!idkTypeList!!.list(primaryHead).isHeadCached() || !idkTypeList.list(secondaryHead).isHeadCached() || !idkTypeList.list(tertiaryHead).isHeadCached()) {
				return null
			}
			@Pc(251) var mesh: Mesh? = idkTypeList.list(primaryHead).getHeadMesh()
			if (mesh != null) {
				meshCount++
				meshes[0] = mesh
			}
			mesh = idkTypeList.list(secondaryHead).getHeadMesh()
			if (mesh != null) {
				meshes[meshCount++] = mesh
			}
			mesh = idkTypeList.list(tertiaryHead).getHeadMesh()
			if (mesh != null) {
				meshes[meshCount++] = mesh
			}
			mesh = Mesh(meshes, meshCount)
			@Pc(296) val adjustmentFunctionMask: Int = functionMask or 0x4000
			model = toolkit!!.createModel(mesh, adjustmentFunctionMask, HintTrail.trailPreferenceFlags, 64, 768)
			@Pc(306) var i: Int = 0
			while (i < 5) {
				var paletteIndex: Int = 0
				while (paletteIndex < PlayerAppearance.recolorPaletteDst!!.size) {
					if (PlayerAppearance.recolorPaletteDst!![paletteIndex]!![i]!!.size > this.recolorDstIndices!![i]) {
						model!!.ia(PlayerAppearance.recolorPaletteSrc!![paletteIndex]!![i], PlayerAppearance.recolorPaletteDst!![paletteIndex]!![i]!![this.recolorDstIndices!![i]])
					}
					paletteIndex++
				}
				i++
			}
			model!!.s(functionMask)
			@Pc(364) val referenceCache2: LRUCache = PlayerAppearance.aClass169_18
			withLock(PlayerAppearance.aClass169_18) {
				PlayerAppearance.aClass169_18.put(model, headUid)
			}
		}
		if (seqType == null) {
			return model
		} else {
			model = model!!.copy(4.toByte(), functionMask, true)
			return seqType.animateModel(model, nextFrameId, currentFrameDuration, 2048, currentFrameId)
		}
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(Lclient!oha;III)V")
	fun addAppearanceIdentity(@OriginalArg(0) idkTypeList: IDKTypeList?, @OriginalArg(1) idkId: Int, @OriginalArg(3) slot: Int) {
		@Pc(7) val partIndex: Int = PlayerAppearance.IDK_PART_TABLE[slot]
		if (idkTypeList!!.list(idkId) != null) {
			this.appearance!![partIndex] = idkId or Int.MIN_VALUE
			this.calculateAppearanceHash()
		}
	}

	@OriginalMember(owner = "client!oo", name = "a", descriptor = "(I)V")
	private fun calculateAppearanceHash() {
		this.playerAppearanceHash = -1L
		@Pc(10) val crc64Table: LongArray = TextureOpDiagonalWaves.CRC64_TABLE
		this.playerAppearanceHash = crc64Table[(((this.basId shr 8).toLong() xor this.playerAppearanceHash) and 0xFFL).toInt()] xor (this.playerAppearanceHash ushr 8)
		this.playerAppearanceHash = (this.playerAppearanceHash ushr 8) xor crc64Table[((this.basId.toLong() xor this.playerAppearanceHash) and 0xFFL).toInt()]
		@Pc(56) var i: Int = 0
		while (i < 12) {
			this.playerAppearanceHash = (this.playerAppearanceHash ushr 8) xor crc64Table[(((this.appearance!![i] shr 24).toLong() xor this.playerAppearanceHash) and 0xFFL).toInt()]
			this.playerAppearanceHash = crc64Table[(((this.appearance!![i] shr 16).toLong() xor this.playerAppearanceHash) and 0xFFL).toInt()] xor (this.playerAppearanceHash ushr 8)
			this.playerAppearanceHash = (this.playerAppearanceHash ushr 8) xor crc64Table[((this.playerAppearanceHash xor (this.appearance!![i] shr 8).toLong()) and 0xFFL).toInt()]
			this.playerAppearanceHash = crc64Table[((this.playerAppearanceHash xor this.appearance!![i].toLong()) and 0xFFL).toInt()] xor (this.playerAppearanceHash ushr 8)
			i++
		}
		@Pc(150) var j: Int = 0
		while (j < 5) {
			this.playerAppearanceHash = crc64Table[((this.playerAppearanceHash xor this.recolorDstIndices!![j].toLong()) and 0xFFL).toInt()] xor (this.playerAppearanceHash ushr 8)
			j++
		}
		this.playerAppearanceHash = (this.playerAppearanceHash ushr 8) xor crc64Table[((this.playerAppearanceHash xor (if (this.female) 1 else 0).toLong()) and 0xFFL).toInt()]
	}

	companion object {
		@OriginalMember(owner = "client!wi", name = "P", descriptor = "[S")
		@JvmField
		val DEFAULT_RECOLOR_SRC_1: ShortArray = shortArrayOf(-10304, 9104, 25485, 4620, 4540)

		@OriginalMember(owner = "client!wi", name = "Q", descriptor = "[S")
		@JvmField
		val DEFAULT_RECOLOR_SRC_2: ShortArray = shortArrayOf(-1, -1, -1, -1, -1)

		@OriginalMember(owner = "client!wi", name = "R", descriptor = "[S")
		@JvmField
		val DEFAULT_RECOLOR_SRC_0: ShortArray = shortArrayOf(6798, 8741, 25238, 4626, 4550)

		@OriginalMember(owner = "client!wi", name = "S", descriptor = "[[S")
		@JvmField
		val DEFAULT_RECOLOR_PALETTE_SRC: Array<ShortArray?> = arrayOf<ShortArray?>(DEFAULT_RECOLOR_SRC_0, DEFAULT_RECOLOR_SRC_1, DEFAULT_RECOLOR_SRC_2)

		@OriginalMember(owner = "client!ar", name = "v", descriptor = "[[S")
		@JvmField
		val DEFAULT_RECOLOR_DST_0: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(6798, 107, 10283, 16, 4797, 7744, 5799, 4634, -31839, 22433, 2983, -11343, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010), shortArrayOf(8741, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533, 25239, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010, 7085, 4001, 317, 918, 9517, 9125, 3608, 16450, -32593, 22026, 2700, -22256, 10289, 3877, -27999, 10770, 8982, 3753, 10275, 924, 8637, -26428, 26785, -15202, 8736, 2994, -25710, 928, 6859, -25016, -5449, -14823, -23401, 12, -22456, -27471, -11229, 21673, 57, -24046, -27190, -22877, -30041, 20013, 21596, -31414, 29000, -11209, 6361, -25552, -25450, -14174, -10998, -27357, 3249, 7465, 8913, 13774, 4584, 11728, 10454, 8123, 2619, 13756, -28220, -26323, -23901, -31385, -32342, -29806, -29902, -24128, 933, 1189, 3873, 6057, 8590, 1812, 4895, -5366, 15019, 20793, 7599, 9536, 21778, 14102, 6938, 14648, 5919, 6472, 10128, 17940, 3226, 267, 5421, -27570, -25177, -26319, -28217, -28132, -23792, -23249, -23001, -19033, 5427, 6340, 5673, 2458, 3625, 8646, 5186, 10382, 6826, 7442, 158, 2454, 9655, -27496, -27578, -31687, 11146, 18712, 10785, 21902, 10566, 9500, 11819, 11144, -15965, -24170, -32488, -29277, 29097, -9567, -18794, -27116, 921, 6028, 2974, -5230, 6550, 15244, 14, 49, 3994, 2332, 6038, -31701, 520, 41, 9402, -4825, 28978, -25278, 30876, 13475, -28395, -24010, -14027, -22454, -18158, 20503, -27618, -22258, -1776, -31468, 9616, 7333, 650, 784, 796, 813, -616, -3194, -8152, 1686, 29074, 22422, -32245, 29084, 22411, -22000, 3982, 7062, 10382, 3230, 8076, 20, 1936, 10, -22473, 4892, 5805, 5904, 10, 48, 1936, 5640, 8472, -25529), shortArrayOf(25238, 8742, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010, 7085, 4001, 317, 918, 9517, 9125, 3608, 16450, -32593, 22026, 2700, -22256, 10289, 3877, -27999, 10770, 8982, 3753, 10275, 924, 8637, -26428, 26785, -15202, 8736, 2994, -25710, 928, 6859, -25016, -5449, -14823, -23401, 12, -22456, -27471, -11229, 21673, 57, -24046, -27190, -22877, -30041, 20013, 21596, -31414, 29000, -11209, 6361, -25552, -25450, -14174, -10998, -27357, 3249, 7465, 8913, 13774, 4584, 11728, 10454, 8123, 2619, 13756, -28220, -26323, -23901, -31385, -32342, -29806, -29902, -24128, 933, 1189, 3873, 6057, 8590, 1812, 4895, -5366, 15019, 20793, 7599, 9536, 21778, 14102, 6938, 14648, 5919, 6472, 10128, 17940, 3226, 267, 5421, -27570, -25177, -26319, -28217, -28132, -23792, -23249, -23001, -19033, 5427, 6340, 5673, 2458, 3625, 8646, 5186, 10382, 6826, 7442, 158, 2454, 9655, -27496, -27578, -31687, 11146, 18712, 10785, 21902, 10566, 9500, 11819, 11144, -15965, -24170, -32488, -29277, 29097, -9567, -18794, -27116, 921, 6028, 2974, -5230, 6550, 15244, 14, 49, 3994, 2332, 6038, -31701, 520, 41, 9402, -4825, 28978, -25278, 30876, 13475, -28395, -24010, -14027, -22454, -18158, 20503, -27618, -22258, -1776, -31468, 9616, 7333, 650, 784, 796, 813, -616, -3194, -8152, 1686, 29074, 22422, -32245, 29084, 22411, -22000, 3982, 7062, 10382, 3230, 8076, 20, 1936, 10, -22473, 4892, 5805, 5904, 10, 48, 1936, 5640, 8472, -25529), shortArrayOf(4626, 11146, 6439, 12, 4758, 10270, 7085, 4001, 317, 918, 9517, 9125, 3608, 16450, -32593, 22026, 2700, -22256, 10289, 3877, -27999, 10770, 8982, 3753, 10275, 924, 8637, -26428, 26785, -15202, 8736, 2994, -25710, 928, 6859, -25016, -5449, -14823, -23401, 12, -22456, -27471, -11229, 21673, 57, -24046, -27190, -22877, -30041, 20013, 21596, -31414, 29000, -11209, 6361, -25552, -25450, -14174, -10998, -27357, 3249, 7465, 8913, 13774, 4584, 11728, 10454, 8123, 2619, 13756, -28220, -26323, -23901, -31385, -32342, -29806, -29902, -24128, 933, 1189, 3873, 6057, 8590, 1812, 4895, -5366, 15019, 20793, 7599, 9536, 21778, 14102, 6938, 14648, 5919, 6472, 10128, 17940, 3226, 267, 5421, -27570, -25177, -26319, -28217, -28132, -23792, -23249, -23001, -19033, 5427, 6340, 5673, 2458, 3625, 8646, 5186, 10382, 6826, 7442, 158, 2454, 9655, -27496, -27578, -31687, 11146, 18712, 10785, 21902, 10566, 9500, 11819, 11144, -15965, -24170, -32488, -29277, 29097, -9567, -18794, -27116, 921, 6028, 2974, -5230, 6550, 15244, 14, 49, 3994, 2332, 6038, -31701, 520, 41, 9402, -4825, 28978, -25278, 30876, 13475, -28395, -24010, -14027, -22454, -18158, 20503, -27618, -22258, -1776, -31468, 9616, 7333, 650, 784, 796, 813, -616, -3194, -8152, 1686, 29074, 22422, -32245, 29084, 22411, -22000, 3982, 7062, 10382, 3230, 8076, 20, 1936, 10, -22473, 4892, 5805, 5904, 10, 48, 1936, 5640, 8472, -25529), shortArrayOf(4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 4444, 4328, 7052, 4490))

		@OriginalMember(owner = "client!ar", name = "y", descriptor = "[[S")
		@JvmField
		val DEFAULT_RECOLOR_DST_1: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(6554, 115, 10304, 28, 5702, 7756, 5681, 4510, -31835, 22437, 2859, -11339, 16, 5157, 10446, 3658, -27314, -21965, 472, 580, 784, 21966, 28950, -15697, -14002), shortArrayOf(9104, 10275, 7595, 3610, 7975, 8526, 918, -26734, 24466, 10145, -6882, 5027, 1457, 16565, -30545, 25486, 24, 5392, 10429, 3673, -27335, -21957, 192, 687, 412, 21821, 28835, -15460, -14019, 7072, 3990, 309, 910, 9509, 9117, 3600, 16442, -32601, 22020, 2694, -22264, 10281, 3869, -28007, 10764, 8974, 3745, 10269, 914, 8629, -26438, 26779, -15208, 8728, 2982, -25718, 918, 6842, -25030, -5462, -14833, -22386, 8, -22468, -27479, -11237, 21664, 45, -24054, -27207, -22887, -30051, 20003, 21584, -31426, 28990, -11219, 6353, -25560, -25458, -14182, -11006, -27365, 3241, 7457, 8899, 13760, 4565, 11711, 10436, 8103, 2604, 13740, -28232, -26333, -23910, -31403, -32352, -29812, -29909, -24137, 925, 1181, 3865, 6049, 8582, 1804, 4887, -5372, 15011, 20785, 7591, 9528, 21770, 14094, 6930, 14635, 5911, 6463, 10124, 17935, 3218, 262, 5413, -27579, -25189, -26331, -28236, -28144, -23804, -23261, -23013, -19045, 5419, 6332, 5665, 2450, 3617, 8638, 5178, 10374, 6819, 7434, 150, 2446, 9647, -27504, -27586, -31695, 11142, 18704, 10775, 21898, 10554, 9492, 11811, 11140, -15978, -24180, -32498, -29288, 29084, -9579, -18799, -27122, 913, 6024, 2966, -5238, 6542, 15240, 10, 39, 3986, 2324, 6030, -31709, 512, 33, 9394, -4833, 28967, -25290, 30866, 13466, -28403, -24022, -14042, -22472, -18166, 20493, -27626, -22266, -1784, -31473, 9612, 7324, 646, 778, 786, 800, -624, -3198, -8164, 1678, 29068, 22412, -32253, 29075, 22404, -22006, 3974, 7054, 10376, 3222, 8070, 12, 1930, 2, -22481, 4884, 5797, 5900, 6, 40, 1932, 5638, 8466, -25537), shortArrayOf(25485, 9105, 10275, 7595, 3610, 7975, 8526, 918, -26734, 24466, 10145, -6882, 5027, 1457, 16565, -30545, 24, 5392, 10429, 3673, -27335, -21957, 192, 687, 412, 21821, 28835, -15460, -14019, 7072, 3990, 309, 910, 9509, 9117, 3600, 16442, -32601, 22020, 2694, -22264, 10281, 3869, -28007, 10764, 8974, 3745, 10269, 914, 8629, -26438, 26779, -15208, 8728, 2982, -25718, 918, 6842, -25030, -5462, -14833, -22386, 8, -22468, -27479, -11237, 21664, 45, -24054, -27207, -22887, -30051, 20003, 21584, -31426, 28990, -11219, 6353, -25560, -25458, -14182, -11006, -27365, 3241, 7457, 8899, 13760, 4565, 11711, 10436, 8103, 2604, 13740, -28232, -26333, -23910, -31403, -32352, -29812, -29909, -24137, 925, 1181, 3865, 6049, 8582, 1804, 4887, -5372, 15011, 20785, 7591, 9528, 21770, 14094, 6930, 14635, 5911, 6463, 10124, 17935, 3218, 262, 5413, -27579, -25189, -26331, -28236, -28144, -23804, -23261, -23013, -19045, 5419, 6332, 5665, 2450, 3617, 8638, 5178, 10374, 6819, 7434, 150, 2446, 9647, -27504, -27586, -31695, 11142, 18704, 10775, 21898, 10554, 9492, 11811, 11140, -15978, -24180, -32498, -29288, 29084, -9579, -18799, -27122, 913, 6024, 2966, -5238, 6542, 15240, 10, 39, 3986, 2324, 6030, -31709, 512, 33, 9394, -4833, 28967, -25290, 30866, 13466, -28403, -24022, -14042, -22472, -18166, 20493, -27626, -22266, -1784, -31473, 9612, 7324, 646, 778, 786, 800, -624, -3198, -8164, 1678, 29068, 22412, -32253, 29075, 22404, -22006, 3974, 7054, 10376, 3222, 8070, 12, 1930, 2, -22481, 4884, 5797, 5900, 6, 40, 1932, 5638, 8466, -25537), shortArrayOf(4620, 11140, 6433, 6, 4752, 10264, 7072, 3990, 309, 910, 9509, 9117, 3600, 16442, -32601, 22020, 2694, -22264, 10281, 3869, -28007, 10764, 8974, 3745, 10269, 914, 8629, -26438, 26779, -15208, 8728, 2982, -25718, 918, 6842, -25030, -5462, -14833, -22386, 8, -22468, -27479, -11237, 21664, 45, -24054, -27207, -22887, -30051, 20003, 21584, -31426, 28990, -11219, 6353, -25560, -25458, -14182, -11006, -27365, 3241, 7457, 8899, 13760, 4565, 11711, 10436, 8103, 2604, 13740, -28232, -26333, -23910, -31403, -32352, -29812, -29909, -24137, 925, 1181, 3865, 6049, 8582, 1804, 4887, -5372, 15011, 20785, 7591, 9528, 21770, 14094, 6930, 14635, 5911, 6463, 10124, 17935, 3218, 262, 5413, -27579, -25189, -26331, -28236, -28144, -23804, -23261, -23013, -19045, 5419, 6332, 5665, 2450, 3617, 8638, 5178, 10374, 6819, 7434, 150, 2446, 9647, -27504, -27586, -31695, 11142, 18704, 10775, 21898, 10554, 9492, 11811, 11140, -15978, -24180, -32498, -29288, 29084, -9579, -18799, -27122, 913, 6024, 2966, -5238, 6542, 15240, 10, 39, 3986, 2324, 6030, -31709, 512, 33, 9394, -4833, 28967, -25290, 30866, 13466, -28403, -24022, -14042, -22472, -18166, 20493, -27626, -22266, -1784, -31473, 9612, 7324, 646, 778, 786, 800, -624, -3198, -8164, 1678, 29068, 22412, -32253, 29075, 22404, -22006, 3974, 7054, 10376, 3222, 8070, 12, 1930, 2, -22481, 4884, 5797, 5900, 6, 40, 1932, 5638, 8466, -25537), shortArrayOf(4540, 4529, 5674, 5667, 5785, 6802, 8072, 4562, 4437, 4317, 5896, 4488))

		@OriginalMember(owner = "client!ar", name = "D", descriptor = "[[S")
		@JvmField
		val DEFAULT_RECOLOR_DST_2: Array<ShortArray?> = arrayOf<ShortArray?>(ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0))

		@OriginalMember(owner = "client!ar", name = "q", descriptor = "[[[S")
		@JvmField
		val DEFAULT_RECOLOR_PALETTE_DST: Array<Array<ShortArray?>?> = arrayOf<Array<ShortArray?>?>(DEFAULT_RECOLOR_DST_0, DEFAULT_RECOLOR_DST_1, DEFAULT_RECOLOR_DST_2)

		@OriginalMember(owner = "client!dh", name = "r", descriptor = "[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_DST_2: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906), shortArrayOf(8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906), shortArrayOf(8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906), shortArrayOf(8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 13756, 13735, 13716, 13880, 13733, 13842, 17592, 18595, 21650, 23990, 24987, 24078, 27962, 25893, 26894, 27183, 27165, 27146, 30010, 28965, 29966, 12472, 13475, 10382, 10682, 10659, 10766, 6707, 6685, 7820, 6961, 6941, 5900, 3379, 3359, 3346, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), shortArrayOf(4296, 3290, 3299, 3260, 4502, 4378, 4383, 5289, 7618, 7380, 8408, 7492, 82, 119, 107, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 8, 22, 40, 60, 80, 100, 300, 674, 694, 546, 930, 950, 6444, 6942, 6962, 2348, 2722, 2742, 10540, 10914, 10934, 10300, 10546, 10566, 14636, 15010, 15030, 19756, 20130, 20150, -29396, -29022, -29002, 31020, 31266, 31286, -24276, -23902, -23882, -19156, -18782, -18762, -14036, -13662, -13642, -6868, -6494, -6474, 518, 534, 6790, 6806, 11014, 11030, 14982, 14998, 21126, 21142, -28922, -28906, -22010, -21994, -12922, -12906, 10337, 10572, 10419, 10397, 10349, 8404, 9530, 10446, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

		@OriginalMember(owner = "client!dh", name = "w", descriptor = "[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_DST_0: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898), shortArrayOf(10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898), shortArrayOf(10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898), shortArrayOf(10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 13766, 13745, 13726, 13890, 13743, 13852, 17602, 18605, 21660, 24000, 24997, 24088, 27972, 25903, 26904, 27193, 27175, 27156, 30020, 28975, 29976, 12482, 13485, 10392, 10692, 10669, 10776, 6717, 6695, 7830, 6971, 6951, 5910, 3389, 3369, 3356, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), shortArrayOf(4300, 3294, 3303, 3264, 4506, 4382, 4387, 5293, 7622, 7384, 8412, 7496, 86, 123, 111, 99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10, 30, 50, 70, 90, 110, 310, 684, 704, 556, 940, 960, 6454, 6952, 6972, 2358, 2732, 2752, 10550, 10924, 10944, 10310, 10556, 10576, 14646, 15020, 15040, 19766, 20140, 20160, -29386, -29012, -28992, 31030, 31276, 31296, -24266, -23892, -23872, -19146, -18772, -18752, -14026, -13652, -13632, -6858, -6484, -6464, 522, 542, 6794, 6814, 11018, 11038, 14986, 15006, 21130, 21150, -28918, -28898, -22006, -21986, -12918, -12898, 10347, 10582, 10429, 10407, 10359, 8414, 9540, 10456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

		@OriginalMember(owner = "client!dh", name = "O", descriptor = "[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_DST_3: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910), shortArrayOf(7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910), shortArrayOf(7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910), shortArrayOf(7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 13751, 13730, 13711, 13875, 13728, 13837, 17587, 18590, 21645, 23985, 24982, 24073, 27957, 25888, 26889, 27178, 27160, 27141, 30005, 28960, 29961, 12467, 13470, 10377, 10677, 10654, 10761, 6702, 6680, 7815, 6956, 6936, 5895, 3374, 3354, 3341, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), shortArrayOf(4294, 3288, 3297, 3258, 4500, 4376, 4381, 5287, 7616, 7378, 8406, 7490, 80, 117, 105, 93, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 7, 18, 35, 55, 75, 95, 295, 669, 689, 541, 925, 945, 6439, 6937, 6957, 2343, 2717, 2737, 10535, 10909, 10929, 10295, 10541, 10561, 14631, 15005, 15025, 19751, 20125, 20145, -29401, -29027, -29007, 31015, 31261, 31281, -24281, -23907, -23887, -19161, -18787, -18767, -14041, -13667, -13647, -6873, -6499, -6479, 516, 530, 6788, 6802, 11012, 11026, 14980, 14994, 21124, 21138, -28924, -28910, -22012, -21998, -12924, -12910, 10332, 10567, 10414, 10392, 10344, 8399, 9525, 10441, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

		@OriginalMember(owner = "client!dh", name = "pb", descriptor = "[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_DST_1: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902), shortArrayOf(9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902), shortArrayOf(9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902), shortArrayOf(9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 13761, 13740, 13721, 13885, 13738, 13847, 17597, 18600, 21655, 23995, 24992, 24083, 27967, 25898, 26899, 27188, 27170, 27151, 30015, 28970, 29971, 12477, 13480, 10387, 10687, 10664, 10771, 6712, 6690, 7825, 6966, 6946, 5905, 3384, 3364, 3351, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), shortArrayOf(4298, 3292, 3301, 3262, 4504, 4380, 4385, 5291, 7620, 7382, 8410, 7494, 84, 121, 109, 97, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 9, 26, 45, 65, 85, 105, 305, 679, 699, 551, 935, 955, 6449, 6947, 6967, 2353, 2727, 2747, 10545, 10919, 10939, 10305, 10551, 10571, 14641, 15015, 15035, 19761, 20135, 20166, -29380, -29017, -28997, 31025, 31271, 31291, -24271, -23897, -23877, -19151, -18777, -18757, -14031, -13657, -13637, -6863, -6489, -6469, 520, 538, 6792, 6810, 11016, 11034, 14984, 15002, 21128, 21146, -28920, -28902, -22008, -21990, -12920, -12902, 10342, 10577, 10424, 10402, 10354, 8409, 9535, 10451, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))

		@OriginalMember(owner = "client!dh", name = "jb", descriptor = "[[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_PALETTE_DST: Array<Array<ShortArray?>?> = arrayOf<Array<ShortArray?>?>(STELLAR_DAWN_RECOLOR_DST_0, STELLAR_DAWN_RECOLOR_DST_1, STELLAR_DAWN_RECOLOR_DST_2, STELLAR_DAWN_RECOLOR_DST_3)

		@OriginalMember(owner = "client!cb", name = "b", descriptor = "[S")
		@JvmField
		val GAME4_RECOLOR_SRC_2: ShortArray = shortArrayOf(-1, -1, -1, -1, -1)

		@OriginalMember(owner = "client!cb", name = "i", descriptor = "[S")
		@JvmField
		val GAME4_RECOLOR_SRC_1: ShortArray = shortArrayOf(-1, -1, -1, -1, -1)

		@OriginalMember(owner = "client!cb", name = "u", descriptor = "[S")
		@JvmField
		val GAME4_RECOLOR_SRC_0: ShortArray = shortArrayOf(6798, 8741, 25238, 4626, 4550)

		@OriginalMember(owner = "client!cb", name = "o", descriptor = "[[S")
		@JvmField
		val GAME4_RECOLOR_PALETTE_SRC: Array<ShortArray?> = arrayOf<ShortArray?>(GAME4_RECOLOR_SRC_0, GAME4_RECOLOR_SRC_1, GAME4_RECOLOR_SRC_2)

		@OriginalMember(owner = "client!kda", name = "V", descriptor = "[[S")
		@JvmField
		val GAME4_RECOLOR_DST_1: Array<ShortArray?> = arrayOf<ShortArray?>(ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0))

		@OriginalMember(owner = "client!kda", name = "Lb", descriptor = "[[S")
		@JvmField
		val GAME4_RECOLOR_DST_2: Array<ShortArray?> = arrayOf<ShortArray?>(ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0), ShortArray(0))

		@OriginalMember(owner = "client!kda", name = "Xb", descriptor = "[[S")
		@JvmField
		val GAME4_RECOLOR_DST_0: Array<ShortArray?> = arrayOf<ShortArray?>(shortArrayOf(6798, 12, 78, 8384, 14511, 9162, 5056, 939, 5025, 4760, 9108, 7719, 14241, 22443, 30247, -29781, -25675, -21568, -17472, -12373, -8256, -3545), shortArrayOf(8741, 12, 78, 8384, 14511, 9162, 5056, 939, 5025, 4760, 9108, 7719, 14241, 22443, 30247, -29781, -25675, -21568, -17472, -12373, -8256, -3545), shortArrayOf(25238, 12, 78, 8384, 14511, 9162, 5056, 939, 5025, 4760, 9108, 7719, 14241, 22443, 30247, -29781, -25675, -21568, -17472, -12373, -8256, -3545), shortArrayOf(4626, 12, 78, 8384, 14511, 9162, 5056, 939, 5025, 4760, 9108, 7719, 14241, 22443, 30247, -29781, -25675, -21568, -17472, -12373, -8256, -3545), shortArrayOf(4550, 12, 78, 8384, 14511, 9162, 5056, 939, 5025, 4760, 9108, 7719, 14241, 22443, 30247, -29781, -25675, -21568, -17472, -12373, -8256, -3545))

		@OriginalMember(owner = "client!kda", name = "Sb", descriptor = "[[[S")
		@JvmField
		val GAME4_RECOLOR_PALETTE_DST: Array<Array<ShortArray?>?> = arrayOf<Array<ShortArray?>?>(GAME4_RECOLOR_DST_0, GAME4_RECOLOR_DST_1, GAME4_RECOLOR_DST_2)

		@OriginalMember(owner = "client!qea", name = "d", descriptor = "[I")
		@JvmField
		val frameIds: IntArray = IntArray(14)

		@OriginalMember(owner = "client!ov", name = "m", descriptor = "[[[S")
		@JvmField
		var recolorPaletteDst: Array<Array<ShortArray?>?>? = null

		@OriginalMember(owner = "client!vh", name = "f", descriptor = "[I")
		@JvmField
		val IDK_PART_TABLE: IntArray = intArrayOf(8, 11, 4, 6, 9, 7, 10, 0)

		@OriginalMember(owner = "client!hc", name = "k", descriptor = "[I")
		@JvmField
		val frameCycles: IntArray = IntArray(14)

		@OriginalMember(owner = "client!w", name = "c", descriptor = "(I)I")
		@JvmStatic
		fun countBodyModels(): Int {
			@Pc(5) val referenceCache: LRUCache = PlayerAppearance.aClass169_4
			withLock(PlayerAppearance.aClass169_4) {
				return PlayerAppearance.aClass169_4.hardCount()
			}
		}

		@OriginalMember(owner = "client!um", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun resetCache() {
			@Pc(1) var referenceCache: LRUCache = PlayerAppearance.aClass169_4
			withLock(PlayerAppearance.aClass169_4) {
				PlayerAppearance.aClass169_4.reset()
			}
			referenceCache = PlayerAppearance.aClass169_18
			withLock(PlayerAppearance.aClass169_18) {
				PlayerAppearance.aClass169_18.reset()
			}
		}

		@OriginalMember(owner = "client!nv", name = "e", descriptor = "[[S")
		@JvmField
		var recolorPaletteSrc: Array<ShortArray?>? = null

		@OriginalMember(owner = "client!ap", name = "b", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_4: LRUCache = LRUCache(260)

		@OriginalMember(owner = "client!maa", name = "e", descriptor = "[Lclient!gw;")
		@JvmField
		val aClass2_Sub6_Sub9Array3: Array<FrameSet?> = arrayOfNulls<FrameSet>(14)

		@OriginalMember(owner = "client!wc", name = "c", descriptor = "[Lclient!gw;")
		@JvmField
		val aClass2_Sub6_Sub9Array4: Array<FrameSet?> = arrayOfNulls<FrameSet>(14)

		@OriginalMember(owner = "client!b", name = "c", descriptor = "[Lclient!hg;")
		@JvmField
		val aClass130Array1: Array<SeqType?> = arrayOfNulls<SeqType>(14)

		@OriginalMember(owner = "client!np", name = "a", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_18: LRUCache = LRUCache(5)

		@OriginalMember(owner = "client!sh", name = "b", descriptor = "[I")
		@JvmField
		val nextFrameIds: IntArray = IntArray(14)

		@OriginalMember(owner = "client!mc", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun processCacheCycle() {
			@Pc(5) var referenceCache: LRUCache = PlayerAppearance.aClass169_4
			withLock(PlayerAppearance.aClass169_4) {
				PlayerAppearance.aClass169_4.clean(5)
			}
			referenceCache = PlayerAppearance.aClass169_18
			withLock(PlayerAppearance.aClass169_18) {
				PlayerAppearance.aClass169_18.clean(5)
			}
		}

		@OriginalMember(owner = "client!mea", name = "g", descriptor = "[I")
		@JvmField
		val frameDurations: IntArray = IntArray(14)

		@OriginalMember(owner = "client!wu", name = "d", descriptor = "[[B")
		@JvmField
		var TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA: Array<ByteArray?>? = null

		@OriginalMember(owner = "client!dia", name = "c", descriptor = "(I)V")
		@JvmStatic
		fun removePlayerAppearanceSoftReferences() {
			@Pc(1) var referenceCache: LRUCache = PlayerAppearance.aClass169_4
			withLock(PlayerAppearance.aClass169_4) {
				PlayerAppearance.aClass169_4.removeSoftReferences()
			}
			referenceCache = PlayerAppearance.aClass169_18
			withLock(PlayerAppearance.aClass169_18) {
				PlayerAppearance.aClass169_18.removeSoftReferences()
			}
		}

		@OriginalMember(owner = "client!gia", name = "v", descriptor = "[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_SRC_0: ShortArray = shortArrayOf(967, 20428, -21577, 11219, -10290)

		@OriginalMember(owner = "client!gia", name = "z", descriptor = "[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_SRC_1: ShortArray = shortArrayOf(962, 20423, -21582, 11214, -10295)

		@OriginalMember(owner = "client!gia", name = "x", descriptor = "[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_SRC_2: ShortArray = shortArrayOf(957, 20418, -21587, 11209, -10300)

		@OriginalMember(owner = "client!gia", name = "s", descriptor = "[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_SRC_3: ShortArray = shortArrayOf(952, 20413, -21592, 11204, -10305)

		@OriginalMember(owner = "client!gia", name = "y", descriptor = "[[S")
		@JvmField
		val STELLAR_DAWN_RECOLOR_SRC: Array<ShortArray?> = arrayOf<ShortArray?>(STELLAR_DAWN_RECOLOR_SRC_0, STELLAR_DAWN_RECOLOR_SRC_1, STELLAR_DAWN_RECOLOR_SRC_2, STELLAR_DAWN_RECOLOR_SRC_3)
	}
}
