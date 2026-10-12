package com.jagex.game.runetek5.graphics.model

import com.jagex.game.runetek5.config.sequence.frameset.AnimBase
import com.jagex.game.runetek5.config.sequence.frameset.AnimFrame
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ka")
abstract class Model @OriginalMember(owner = "client!ka", name = "<init>", descriptor = "()V") constructor() {

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "Z")
	@JvmField
	var isLocked: Boolean = false

	@OriginalMember(owner = "client!ka", name = "FA", descriptor = "(I)V")
	abstract fun FA(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!ka", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	abstract fun p(@OriginalArg(0) contourType: Int, @OriginalArg(1) contourModifier: Int, @OriginalArg(2) ground: Ground?, @OriginalArg(3) upperGround: Ground?, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(ILclient!raa;IZILclient!gk;I[I[ZLclient!gk;IZ)V")
	private fun interpolateFrames(@OriginalArg(0) rotation: Int, @OriginalArg(1) animBase: AnimBase?, @OriginalArg(3) secondaryPass: Boolean, @OriginalArg(4) length: Int, @OriginalArg(5) animFrame: AnimFrame?, @OriginalArg(6) cycle: Int, @OriginalArg(7) matrix: IntArray?, @OriginalArg(8) animateLabels: BooleanArray?, @OriginalArg(9) animFrame2: AnimFrame?, @OriginalArg(10) submesh: Int, @OriginalArg(11) lights: Boolean) {
		var length: Int = length
		var cycle: Int = cycle
		if (ClientFeatures.FPS > 0 && animFrame != null && cycle >= 0 && length > 0) {
			// Not part of the original client: with client.fps the blend between the two keyframes
			// moves on between game cycles too, by how far the cycle has run.
			cycle = cycle * ClientFeatures.ANIMATION_STEPS + ClientFeatures.animationStep()
			length = length * ClientFeatures.ANIMATION_STEPS
		}
		@Pc(14) var currentTransformation: Int
		if (animFrame == null || cycle == 0) {
			currentTransformation = 0
			while (currentTransformation < animFrame2!!.numTransformations) {
				@Pc(21) val baseIndex: Short = animFrame2.baseIndices!![currentTransformation]
				if (animateLabels == null || animateLabels[baseIndex.toInt()] == secondaryPass || animBase!!.transformationType[baseIndex.toInt()] == 0) {
					@Pc(42) val label: Short = animFrame2.labels!![currentTransformation]
					if (label.toInt() != -1) {
						this.animateTransform(animBase!!.labels[label.toInt()], lights, rotation, 0, 0, submesh and animBase.submeshes[label.toInt()], 0, matrix, 0)
					}
					this.animateTransform(animBase!!.labels[baseIndex.toInt()], lights, rotation, animFrame2.transformationY!![currentTransformation].toInt(), animBase.transformationType[baseIndex.toInt()], animBase.submeshes[baseIndex.toInt()] and submesh, animFrame2.transformationX!![currentTransformation].toInt(), matrix, animFrame2.transformationZ!![currentTransformation].toInt())
				}
				currentTransformation++
			}
			return
		}
		currentTransformation = 0
		@Pc(107) var nextTransformation: Int = 0
		@Pc(109) var i: Int = 0
		while (i < animBase!!.numTransformation) {
			@Pc(121) var hasCurrent: Boolean = false
			if (currentTransformation < animFrame2!!.numTransformations && animFrame2.baseIndices!![currentTransformation].toInt() == i) {
				hasCurrent = true
			}
			@Pc(139) var hasNext: Boolean = false
			if (nextTransformation < animFrame.numTransformations && animFrame.baseIndices!![nextTransformation].toInt() == i) {
				hasNext = true
			}
			if (hasCurrent || hasNext) {
				if (animateLabels == null || secondaryPass == animateLabels[i] || animBase.transformationType[i] == 0) {
					@Pc(189) var defaultValue: Short = 0
					@Pc(194) val transformationType: Int = animBase.transformationType[i]
					if (transformationType == 3 || transformationType == 10) {
						defaultValue = 128
					}
					@Pc(216) val currentX: Short
					@Pc(210) val currentY: Short
					@Pc(214) val currentZ: Short
					@Pc(212) val currentLabel: Short
					@Pc(218) val currentTweening: Byte
					if (hasCurrent) {
						currentZ = animFrame2.transformationZ!![currentTransformation]
						currentLabel = animFrame2.labels!![currentTransformation]
						currentY = animFrame2.transformationY!![currentTransformation]
						currentTweening = animFrame2.tweeningProperties!![currentTransformation]
						currentX = animFrame2.transformationX!![currentTransformation]
						currentTransformation++
					} else {
						currentY = defaultValue
						currentLabel = -1
						currentZ = defaultValue
						currentX = defaultValue
						currentTweening = 0
					}
					@Pc(254) val nextX: Short
					@Pc(256) val nextY: Short
					@Pc(250) val nextZ: Short
					@Pc(252) val nextLabel: Short
					@Pc(258) val nextTweening: Byte
					if (hasNext) {
						nextY = animFrame.transformationY!![nextTransformation]
						nextZ = animFrame.transformationZ!![nextTransformation]
						nextTweening = animFrame.tweeningProperties!![nextTransformation]
						nextX = animFrame.transformationX!![nextTransformation]
						nextLabel = animFrame.labels!![nextTransformation]
						nextTransformation++
					} else {
						nextZ = defaultValue
						nextLabel = -1
						nextX = defaultValue
						nextY = defaultValue
						nextTweening = 0
					}
					if (currentLabel.toInt() != -1) {
						this.animateTransform(animBase.labels[currentLabel.toInt()], lights, rotation, 0, 0, submesh and animBase.submeshes[currentLabel.toInt()], 0, matrix, 0)
					} else if (nextLabel.toInt() != -1) {
						this.animateTransform(animBase.labels[nextLabel.toInt()], lights, rotation, 0, 0, submesh and animBase.submeshes[nextLabel.toInt()], 0, matrix, 0)
					}
					@Pc(348) val interpolatedX: Int
					@Pc(352) val interpolatedY: Int
					@Pc(350) val interpolatedZ: Int
					if ((currentTweening.toInt() and 0x2) == 0 && (nextTweening.toInt() and 0x1) == 0) {
						@Pc(371) var deltaX: Int
						if (transformationType == 2) {
							deltaX = (nextX - currentX) and 0x3FFF
							@Pc(490) var deltaY: Int = (nextY - currentY) and 0x3FFF
							@Pc(496) var deltaZ: Int = (nextZ - currentZ) and 0x3FFF
							if (deltaX >= 8192) {
								deltaX -= 16384
							}
							if (deltaY >= 8192) {
								deltaY -= 16384
							}
							if (deltaZ >= 8192) {
								deltaZ -= 16384
							}
							interpolatedX = (currentX + deltaX * cycle / length) and 0x3FFF
							interpolatedY = (deltaY * cycle / length + currentY) and 0x3FFF
							interpolatedZ = (currentZ + cycle * deltaZ / length) and 0x3FFF
						} else if (transformationType == 9) {
							deltaX = (nextX - currentX) and 0x3FFF
							if (deltaX >= 8192) {
								deltaX -= 16384
							}
							interpolatedX = (deltaX * cycle / length + currentX) and 0x3FFF
							interpolatedZ = 0
							interpolatedY = 0
						} else if (transformationType == 7) {
							deltaX = (nextX - currentX) and 0x3F
							if (deltaX >= 32) {
								deltaX -= 64
							}
							interpolatedX = (currentX + deltaX * cycle / length) and 0x3F
							interpolatedY = cycle * (nextY - currentY) / length + currentY
							interpolatedZ = (nextZ - currentZ) * cycle / length + currentZ
						} else {
							interpolatedX = currentX + cycle * (nextX - currentX) / length
							interpolatedZ = currentZ + (nextZ - currentZ) * cycle / length
							interpolatedY = cycle * (nextY - currentY) / length + currentY
						}
					} else {
						interpolatedX = currentX.toInt()
						interpolatedZ = currentZ.toInt()
						interpolatedY = currentY.toInt()
					}
					this.animateTransform(animBase.labels[i], lights, rotation, interpolatedY, transformationType, submesh and animBase.submeshes[i], interpolatedX, matrix, interpolatedZ)
				} else {
					if (hasNext) {
						nextTransformation++
					}
					if (hasCurrent) {
						currentTransformation++
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(BI[IIIILclient!gw;ILclient!gw;ZI)V")
	fun animateUnrotatedSubmesh(@OriginalArg(1) cycle: Int, @OriginalArg(2) matrix: IntArray?, @OriginalArg(3) submesh: Int, @OriginalArg(4) frameId: Int, @OriginalArg(5) nextFrameId: Int, @OriginalArg(6) animFrameset: FrameSet?, @OriginalArg(8) animFrameset2: FrameSet?, @OriginalArg(9) lights: Boolean, @OriginalArg(10) length: Int) {
		if (frameId == -1) {
			return
		}
		this.lock()
		if (!this.NA()) {
			this.unlock()
			return
		}
		@Pc(20) val animFrame: AnimFrame? = animFrameset2!!.frames!![frameId]
		@Pc(29) val animBase: AnimBase? = animFrame!!.base
		@Pc(31) var animFrame2: AnimFrame? = null
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames!![nextFrameId]
			if (animFrame2!!.base !== animBase) {
				animFrame2 = null
			}
		}
		this.interpolateFrames(0, animBase, false, length, animFrame2, cycle, matrix, null, animFrame, submesh, lights)
		this.wa()
		this.unlock()
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "()[Lclient!eca;")
	abstract fun getEffectiveVertices(): Array<EffectiveVertex?>?

	@OriginalMember(owner = "client!ka", name = "C", descriptor = "(I)V")
	abstract fun C(@OriginalArg(0) ambient: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I[IIIIIZ)V")
	abstract fun applyTransformUnmasked(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) lights: Boolean)

	@OriginalMember(owner = "client!ka", name = "wa", descriptor = "()V")
	abstract fun wa()

	@OriginalMember(owner = "client!ka", name = "I", descriptor = "(I[IIIIZI[I)V")
	abstract fun I(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) lights: Boolean, @OriginalArg(6) submesh: Int, @OriginalArg(7) matrix: IntArray?)

	@OriginalMember(owner = "client!ka", name = "G", descriptor = "()I")
	abstract fun G(): Int

	@OriginalMember(owner = "client!ka", name = "NA", descriptor = "()Z")
	abstract fun NA(): Boolean

	@OriginalMember(owner = "client!ka", name = "LA", descriptor = "(I)V")
	abstract fun LA(@OriginalArg(0) contrast: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	abstract fun renderOrtho(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) boundingBox: BoundingBoxPolygon?, @OriginalArg(2) zoom: Int, @OriginalArg(3) flags: Int)

	@OriginalMember(owner = "client!ka", name = "ua", descriptor = "()I")
	abstract fun ua(): Int

	@OriginalMember(owner = "client!ka", name = "HA", descriptor = "()I")
	abstract fun HA(): Int

	@OriginalMember(owner = "client!ka", name = "ma", descriptor = "()I")
	abstract fun ma(): Int

	@OriginalMember(owner = "client!ka", name = "F", descriptor = "()Z")
	abstract fun F(): Boolean

	@OriginalMember(owner = "client!ka", name = "O", descriptor = "(III)V")
	abstract fun O(@OriginalArg(0) scaleX: Int, @OriginalArg(1) scaleY: Int, @OriginalArg(2) scaleZ: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;IZ)V")
	abstract fun transform(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) submesh: Int, @OriginalArg(2) relative: Boolean)

	@OriginalMember(owner = "client!ka", name = "EA", descriptor = "()I")
	abstract fun EA(): Int

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!gw;)V")
	fun animateShadowFrame(@OriginalArg(0) frameId: Int, @OriginalArg(2) animFrameset: FrameSet?) {
		if (frameId == -1) {
			return
		}
		this.lock()
		if (!this.NA()) {
			this.unlock()
			return
		}
		@Pc(23) val animFrame: AnimFrame? = animFrameset!!.frames!![frameId]
		@Pc(26) val animBase: AnimBase? = animFrame!!.base
		@Pc(28) var i: Int = 0
		while (i < animFrame.numTransformations) {
			@Pc(35) val baseIndex: Short = animFrame.baseIndices!![i]
			if (animBase!!.shadowed[baseIndex.toInt()]) {
				if (animFrame.labels!![i].toInt() != -1) {
					this.P(0, 0, 0, 0)
				}
				this.P(animBase.transformationType[baseIndex.toInt()], animFrame.transformationX!![i].toInt(), animFrame.transformationY!![i].toInt(), animFrame.transformationZ!![i].toInt())
			}
			i++
		}
		this.wa()
		this.unlock()
	}

	@OriginalMember(owner = "client!ka", name = "b", descriptor = "()V")
	abstract fun uploadToGpu()

	@OriginalMember(owner = "client!ka", name = "da", descriptor = "()I")
	abstract fun da(): Int

	@OriginalMember(owner = "client!ka", name = "s", descriptor = "(I)V")
	abstract fun s(@OriginalArg(0) functionMask: Int)

	@OriginalMember(owner = "client!ka", name = "RA", descriptor = "()I")
	abstract fun RA(): Int

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	abstract fun shareLight(@OriginalArg(0) model: Model?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int, @OriginalArg(4) renderTypeAdjustment: Boolean)

	@OriginalMember(owner = "client!ka", name = "v", descriptor = "()V")
	abstract fun v()

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(BIZ)Lclient!ka;")
	abstract fun copy(@OriginalArg(0) copyType: Byte, @OriginalArg(1) functionMask: Int, @OriginalArg(2) ensureLit: Boolean): Model?

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	abstract fun render(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) boundingBox: BoundingBoxPolygon?, @OriginalArg(2) flags: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!gw;ILclient!gw;ZZII)V")
	fun animateModelFrame(@OriginalArg(0) frameId: Int, @OriginalArg(1) length: Int, @OriginalArg(2) animFrameset: FrameSet?, @OriginalArg(3) rotation: Int, @OriginalArg(4) animFrameset2: FrameSet?, @OriginalArg(6) lights: Boolean, @OriginalArg(7) nextFrameId: Int, @OriginalArg(8) cycle: Int) {
		if (frameId == -1) {
			return
		}
		this.lock()
		if (!this.NA()) {
			this.unlock()
			return
		}
		@Pc(20) val animFrame: AnimFrame? = animFrameset2!!.frames!![frameId]
		@Pc(23) val animBase: AnimBase? = animFrame!!.base
		@Pc(25) var animFrame2: AnimFrame? = null
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames!![nextFrameId]
			if (animBase !== animFrame2!!.base) {
				animFrame2 = null
			}
		}
		this.interpolateFrames(rotation, animBase, false, length, animFrame2, cycle, null, null, animFrame, 65535, lights)
		this.wa()
		this.unlock()
	}

	@OriginalMember(owner = "client!ka", name = "r", descriptor = "()Z")
	abstract fun r(): Boolean

	@OriginalMember(owner = "client!ka", name = "c", descriptor = "()Z")
	abstract fun loadedTextures(): Boolean

	@OriginalMember(owner = "client!ka", name = "fa", descriptor = "()I")
	abstract fun fa(): Int

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I)V")
	abstract fun a(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!ka", name = "d", descriptor = "()[Lclient!nn;")
	abstract fun getEmissiveTriangles(): Array<EmissiveTriangle?>?

	@OriginalMember(owner = "client!ka", name = "V", descriptor = "()I")
	abstract fun V(): Int

	@OriginalMember(owner = "client!ka", name = "H", descriptor = "(III)V")
	abstract fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;)V")
	abstract fun updateParticlePosition(@OriginalArg(0) matrix: Matrix?)

	@OriginalMember(owner = "client!ka", name = "e", descriptor = "()V")
	abstract fun unlock()

	@OriginalMember(owner = "client!ka", name = "f", descriptor = "()V")
	abstract fun lock()

	@OriginalMember(owner = "client!ka", name = "k", descriptor = "(I)V")
	abstract fun k(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	abstract fun pickedOrtho(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int, @OriginalArg(5) zoom: Int): Boolean

	@OriginalMember(owner = "client!ka", name = "P", descriptor = "(IIII)V")
	abstract fun P(@OriginalArg(0) type: Int, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int)

	@OriginalMember(owner = "client!ka", name = "WA", descriptor = "()I")
	abstract fun WA(): Int

	@OriginalMember(owner = "client!ka", name = "VA", descriptor = "(I)V")
	abstract fun VA(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IIII)V")
	abstract fun adjustColours(@OriginalArg(0) hue: Int, @OriginalArg(1) saturation: Int, @OriginalArg(2) lightness: Int, @OriginalArg(3) opacity: Int)

	@OriginalMember(owner = "client!ka", name = "na", descriptor = "()I")
	abstract fun na(): Int

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!gw;IBILclient!gw;IIILclient!gw;ZILclient!gw;I[ZI)V")
	fun doubleAnimation(@OriginalArg(0) animFrameset: FrameSet?, @OriginalArg(1) length: Int, @OriginalArg(3) secondaryLength: Int, @OriginalArg(4) animFrameset2: FrameSet?, @OriginalArg(5) frameId: Int, @OriginalArg(6) secondaryFrameId: Int, @OriginalArg(7) cycle: Int, @OriginalArg(8) animFrameset3: FrameSet?, @OriginalArg(9) lights: Boolean, @OriginalArg(10) nextFrameId: Int, @OriginalArg(11) animFrameset4: FrameSet?, @OriginalArg(12) secondaryCycle: Int, @OriginalArg(13) animateLabels: BooleanArray?, @OriginalArg(14) secondaryNextFrameId: Int) {
		if (frameId == -1) {
			return
		}
		if (animateLabels == null || secondaryFrameId == -1) {
			this.animateModelFrame(frameId, length, animFrameset, 0, animFrameset3, lights, nextFrameId, cycle)
			return
		}
		this.lock()
		if (!this.NA()) {
			this.unlock()
			return
		}
		@Pc(43) val animFrame: AnimFrame? = animFrameset3!!.frames!![frameId]
		@Pc(46) val animBase: AnimBase? = animFrame!!.base
		@Pc(48) var animFrame2: AnimFrame? = null
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames!![nextFrameId]
			if (animBase !== animFrame2!!.base) {
				animFrame2 = null
			}
		}
		this.interpolateFrames(0, animBase, false, length, animFrame2, cycle, null, animateLabels, animFrame, 65535, lights)
		@Pc(80) val animFrame3: AnimFrame? = animFrameset2!!.frames!![secondaryFrameId]
		@Pc(82) var animFrame4: AnimFrame? = null
		if (animFrameset4 != null) {
			animFrame4 = animFrameset4.frames!![secondaryNextFrameId]
			if (animFrame4!!.base !== animBase) {
				animFrame4 = null
			}
		}
		this.applyTransformUnmasked(0, IntArray(0), 0, 0, 0, 0, lights)
		this.interpolateFrames(0, animFrame3!!.base, true, secondaryLength, animFrame4, secondaryCycle, null, animateLabels, animFrame3, 65535, lights)
		this.wa()
		this.unlock()
	}

	@OriginalMember(owner = "client!ka", name = "ia", descriptor = "(SS)V")
	abstract fun ia(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short)

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IIIIIIILclient!s;I)V")
	fun hillChange(@OriginalArg(0) sizeX: Int, @OriginalArg(2) maxRotateZ: Int, @OriginalArg(3) x: Int, @OriginalArg(4) sizeZ: Int, @OriginalArg(5) y: Int, @OriginalArg(6) maxRotateX: Int, @OriginalArg(7) ground: Ground?, @OriginalArg(8) z: Int) {
		@Pc(16) val deltaXSw: Int = -sizeX / 2
		@Pc(21) val deltaZSw: Int = -sizeZ / 2
		@Pc(31) val southWest: Int = ground!!.averageHeight(x + deltaXSw, deltaZSw + z)
		@Pc(35) val deltaXSe: Int = sizeX / 2
		@Pc(40) val deltaZSe: Int = -sizeZ / 2
		@Pc(51) val southEast: Int = ground.averageHeight(deltaXSe + x, z - -deltaZSe)
		@Pc(56) val deltaXNw: Int = -sizeX / 2
		@Pc(60) val deltaZNw: Int = sizeZ / 2
		@Pc(71) val northWest: Int = ground.averageHeight(x + deltaXNw, z - -deltaZNw)
		@Pc(75) val deltaXNe: Int = sizeX / 2
		@Pc(85) val deltaZNe: Int = sizeZ / 2
		@Pc(95) val northEast: Int = ground.averageHeight(x + deltaXNe, deltaZNe + z)
		@Pc(106) val lowestSouth: Int = if (southWest < southEast) southWest else southEast
		@Pc(113) val lowestNorth: Int = if (northEast <= northWest) northEast else northWest
		@Pc(124) val lowestEast: Int = if (southEast < northEast) southEast else northEast
		@Pc(157) var inverse: Int
		if (sizeZ != 0) {
			@Pc(142) var rotationX: Int = (kotlin.math.atan2((lowestSouth - lowestNorth).toDouble(), sizeZ.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
			if (rotationX != 0) {
				if (maxRotateX != 0) {
					if (rotationX > 8192) {
						inverse = 16384 - maxRotateX
						if (inverse > rotationX) {
							rotationX = inverse
						}
					} else if (rotationX > maxRotateX) {
						rotationX = maxRotateX
					}
				}
				this.FA(rotationX)
			}
		}
		@Pc(183) val lowestWest: Int = if (southWest >= northWest) northWest else southWest
		if (sizeX != 0) {
			@Pc(202) var rotationZ: Int = (kotlin.math.atan2((lowestWest - lowestEast).toDouble(), sizeX.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
			if (rotationZ != 0) {
				if (maxRotateZ != 0) {
					if (rotationZ > 8192) {
						inverse = 16384 - maxRotateZ
						if (inverse > rotationZ) {
							rotationZ = inverse
						}
					} else if (maxRotateZ < rotationZ) {
						rotationZ = maxRotateZ
					}
				}
				this.VA(rotationZ)
			}
		}
		@Pc(237) var height: Int = northEast + southWest
		if (height > northWest + southEast) {
			height = northWest + southEast
		}
		height = (height shr 1) - y
		if (height != 0) {
			this.H(0, height, 0)
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "([IZIIIIBI[II)V")
	private fun animateTransform(@OriginalArg(0) labels: IntArray?, @OriginalArg(1) lights: Boolean, @OriginalArg(2) rotation: Int, @OriginalArg(3) y: Int, @OriginalArg(4) transformType: Int, @OriginalArg(5) submesh: Int, @OriginalArg(7) x: Int, @OriginalArg(8) matrix: IntArray?, @OriginalArg(9) z: Int) {
		var x: Int = x
		var z: Int = z
		@Pc(41) var temp: Int
		if (rotation == 1) {
			if (transformType == 0 || transformType == 1) {
				temp = -x
				x = z
				z = temp
			} else if (transformType == 3) {
				temp = x
				x = z
				z = temp
			} else if (transformType == 2) {
				temp = x
				x = -z and 0x3FFF
				z = temp and 0x3FFF
			}
		} else if (rotation == 2) {
			if (transformType == 0 || transformType == 1) {
				x = -x
				z = -z
			} else if (transformType == 2) {
				z = -z and 0x3FFF
				x = -x and 0x3FFF
			}
		} else if (rotation == 3) {
			if (transformType == 0 || transformType == 1) {
				temp = x
				x = -z
				z = temp
			} else if (transformType == 3) {
				temp = x
				x = z
				z = temp
			} else if (transformType == 2) {
				temp = x
				x = z and 0x3FFF
				z = -temp and 0x3FFF
			}
		}
		if (submesh == 65535) {
			this.applyTransformUnmasked(transformType, labels, x, y, z, rotation, lights)
		} else {
			this.I(transformType, labels, x, y, z, lights, submesh, matrix)
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	abstract fun picked(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int): Boolean

	@OriginalMember(owner = "client!ka", name = "aa", descriptor = "(SS)V")
	abstract fun aa(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short)

	@OriginalMember(owner = "client!ka", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	abstract fun ba(@OriginalArg(0) shadow: Shadow?): Shadow?

	companion object {
		@OriginalMember(owner = "client!ca", name = "a", descriptor = "(IIIII[FIFIIF[F)V")
		@JvmStatic
		fun mapCylindricalUv(@OriginalArg(0) centerZ: Int, @OriginalArg(1) vertexZ: Int, @OriginalArg(2) direction: Int, @OriginalArg(4) vertexX: Int, @OriginalArg(5) uv: FloatArray?, @OriginalArg(6) vertexY: Int, @OriginalArg(7) speedOffset: Float, @OriginalArg(8) centerY: Int, @OriginalArg(9) centerX: Int, @OriginalArg(10) uScale: Float, @OriginalArg(11) matrix: FloatArray?) {
			@Pc(5) val dy: Int = vertexY - centerY
			@Pc(9) val dz: Int = vertexZ - centerZ
			@Pc(17) val dx: Int = vertexX - centerX
			@Pc(38) val tx: Float = matrix!![2] * dz.toFloat() + matrix[1] * dy.toFloat() + dx.toFloat() * matrix[0]
			@Pc(59) val ty: Float = matrix[5] * dz.toFloat() + matrix[3] * dx.toFloat() + dy.toFloat() * matrix[4]
			@Pc(80) val tz: Float = dx.toFloat() * matrix[6] + matrix[7] * dy.toFloat() + dz.toFloat() * matrix[8]
			@Pc(91) var u: Float = kotlin.math.atan2(tx.toDouble(), tz.toDouble()).toFloat() / 6.2831855f + 0.5f
			if (uScale != 1.0f) {
				u *= uScale
			}
			@Pc(105) var v: Float = ty + speedOffset + 0.5f
			@Pc(110) var tmp: Float
			if (direction == 1) {
				tmp = u
				u = -v
				v = tmp
			} else if (direction == 2) {
				u = -u
				v = -v
			} else if (direction == 3) {
				tmp = u
				u = v
				v = -tmp
			}
			uv!![1] = v
			uv[0] = u
		}

		@OriginalMember(owner = "client!qb", name = "a", descriptor = "(IIFI[FIIIII[F)V")
		@JvmStatic
		fun mapSphericalUv(@OriginalArg(0) centerZ: Int, @OriginalArg(1) direction: Int, @OriginalArg(2) speedOffset: Float, @OriginalArg(3) vertexX: Int, @OriginalArg(4) uv: FloatArray?, @OriginalArg(5) vertexZ: Int, @OriginalArg(6) centerX: Int, @OriginalArg(7) vertexY: Int, @OriginalArg(8) centerY: Int, @OriginalArg(10) matrix: FloatArray?) {
			@Pc(5) val dz: Int = vertexZ - centerZ
			@Pc(13) val dx: Int = vertexX - centerX
			@Pc(17) val dy: Int = vertexY - centerY
			@Pc(38) val tx: Float = dz.toFloat() * matrix!![2] + matrix[1] * dy.toFloat() + matrix[0] * dx.toFloat()
			@Pc(59) val ty: Float = dz.toFloat() * matrix[5] + dx.toFloat() * matrix[3] + dy.toFloat() * matrix[4]
			@Pc(80) val tz: Float = matrix[6] * dx.toFloat() + dy.toFloat() * matrix[7] + dz.toFloat() * matrix[8]
			@Pc(95) val dist: Float = kotlin.math.sqrt((tx * tx + ty * ty + tz * tz).toDouble()).toFloat()
			@Pc(106) var u: Float = kotlin.math.atan2(tx.toDouble(), tz.toDouble()).toFloat() / 6.2831855f + 0.5f
			@Pc(124) var v: Float = speedOffset + kotlin.math.asin((ty / dist).toDouble()).toFloat() / 3.1415927f + 0.5f
			@Pc(139) var tmp: Float
			if (direction == 1) {
				tmp = u
				u = -v
				v = tmp
			} else if (direction == 2) {
				v = -v
				u = -u
			} else if (direction == 3) {
				tmp = u
				u = v
				v = -tmp
			}
			uv!![0] = u
			uv[1] = v
		}

		@OriginalMember(owner = "client!dia", name = "a", descriptor = "(FZFF)I")
		@JvmStatic
		fun getCubeFace(@OriginalArg(0) x: Float, @OriginalArg(2) y: Float, @OriginalArg(3) z: Float): Int {
			@Pc(12) val absZ: Float = if (z < 0.0f) -z else z
			@Pc(21) val absX: Float = if (x < 0.0f) -x else x
			@Pc(35) val absY: Float = if (y < 0.0f) -y else y
			if (absZ < absX && absX > absY) {
				return if (x > 0.0f) 0 else 1
			} else if (absY > absZ && absX < absY) {
				return if (y > 0.0f) 2 else 3
			} else if (z > 0.0f) {
				return 4
			} else {
				return 5
			}
		}

		@OriginalMember(owner = "client!ts", name = "b", descriptor = "(III)Z")
		@JvmStatic
		fun retainFaceAlpha(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
			return (functionMask and 0x100) != 0
		}

		@OriginalMember(owner = "client!gt", name = "a", descriptor = "(IZI)Z")
		@JvmStatic
		fun retainFaceMaterials(@OriginalArg(0) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
			return (((functionMask and 0x800) != 0) or GpuModelRetainMasks.altersTextures(preferenceFlags, functionMask)) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags)
		}

		@OriginalMember(owner = "client!uh", name = "a", descriptor = "(FF[FIIZIIIIF[FII)V")
		@JvmStatic
		fun mapCubeUv(@OriginalArg(0) vTrans: Float, @OriginalArg(1) speedOffset: Float, @OriginalArg(2) matrix: FloatArray?, @OriginalArg(3) vertexZ: Int, @OriginalArg(4) centerZ: Int, @OriginalArg(6) direction: Int, @OriginalArg(7) centerX: Int, @OriginalArg(8) vertexX: Int, @OriginalArg(9) vertexY: Int, @OriginalArg(10) uTrans: Float, @OriginalArg(11) uv: FloatArray?, @OriginalArg(12) centerY: Int, @OriginalArg(13) cubeFace: Int) {
			@Pc(9) val vX: Int = vertexX - centerX
			@Pc(13) val vZ: Int = vertexZ - centerZ
			@Pc(17) val vY: Int = vertexY - centerY
			@Pc(38) val tx: Float = matrix!![2] * vZ.toFloat() + matrix[1] * vY.toFloat() + matrix[0] * vX.toFloat()
			@Pc(59) val ty: Float = vX.toFloat() * matrix[3] + vY.toFloat() * matrix[4] + vZ.toFloat() * matrix[5]
			@Pc(80) val tz: Float = matrix[8] * vZ.toFloat() + matrix[6] * vX.toFloat() + vY.toFloat() * matrix[7]
			@Pc(102) var u: Float
			@Pc(96) var v: Float
			if (cubeFace == 0) {
				u = speedOffset + tx + 0.5f
				v = vTrans + 0.5f - tz
			} else if (cubeFace == 1) {
				v = tz + vTrans + 0.5f
				u = speedOffset + tx + 0.5f
			} else if (cubeFace == 2) {
				u = speedOffset + 0.5f - tx
				v = uTrans + 0.5f - ty
			} else if (cubeFace == 3) {
				v = uTrans + 0.5f - ty
				u = tx + speedOffset + 0.5f
			} else if (cubeFace == 4) {
				u = tz + vTrans + 0.5f
				v = uTrans + 0.5f - ty
			} else {
				u = vTrans + 0.5f - tz
				v = uTrans + 0.5f - ty
			}
			@Pc(197) var tmp: Float
			if (direction == 1) {
				tmp = u
				u = -v
				v = tmp
			} else if (direction == 2) {
				v = -v
				u = -u
			} else if (direction == 3) {
				tmp = u
				u = v
				v = -tmp
			}
			uv!![1] = v
			uv[0] = u
		}
	}
}
