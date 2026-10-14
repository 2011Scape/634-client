package com.jagex.game.runetek5.scene.ground

import com.jagex.game.runetek5.cache.node.PointLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!s")
abstract class Ground {

	@OriginalMember(owner = "client!s", name = "d", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!s", name = "g", descriptor = "I")
	@JvmField
	val length: Int

	@OriginalMember(owner = "client!s", name = "i", descriptor = "I")
	@JvmField
	val tileUnits: Int

	@OriginalMember(owner = "client!s", name = "a", descriptor = "[[I")
	@JvmField
	val tileHeights: Array<IntArray?>?

	@OriginalMember(owner = "client!s", name = "e", descriptor = "I")
	@JvmField
	val tileScale: Int

	@OriginalMember(owner = "client!s", name = "<init>", descriptor = "(III[[I)V")
	constructor(@OriginalArg(0) width: Int, @OriginalArg(1) length: Int, @OriginalArg(2) scaling: Int, @OriginalArg(3) tileHeights: Array<IntArray?>?) {
		var scaling: Int = scaling
		this.width = width
		this.length = length
		@Pc(11) var tileScale: Int = 0
		while (scaling > 1) {
			tileScale++
			scaling = scaling shr 0x1
		}
		this.tileUnits = 0x1 shl tileScale
		this.tileHeights = tileHeights
		this.tileScale = tileScale
	}

	/**
	 * Not part of the original client: called by Terrain just before tile ([x], [z]) of [level] is
	 * added, with its floor ids and its faces' materials and texture scales, which may be changed.
	 * 634HD's tile overrides (HdGround) put its own materials on tiles the game leaves untextured; the
	 * other grounds leave them as they are.
	 */
	open fun hdRetexture(level: Int, x: Int, z: Int, underlayId: Int, overlayId: Int, textures: IntArray?, textureScales: IntArray?) {
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	abstract fun addTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) faceA: IntArray?, @OriginalArg(7) faceB: IntArray?, @OriginalArg(8) faceC: IntArray?, @OriginalArg(9) colours: IntArray?, @OriginalArg(10) blendedColours: IntArray?, @OriginalArg(11) materialIds: IntArray?, @OriginalArg(12) textureScales: IntArray?, @OriginalArg(13) waterColour: Int, @OriginalArg(14) waterDepth: Int, @OriginalArg(15) waterBias: Int)

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(II)V")
	abstract fun renderTile(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int)

	@OriginalMember(owner = "client!s", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	abstract fun wa(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean)

	@OriginalMember(owner = "client!s", name = "ka", descriptor = "(III)V")
	abstract fun ka(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadowLevel: Int)

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(Lclient!ab;[I)V")
	abstract fun addLight(@OriginalArg(0) pointLight: PointLight?, @OriginalArg(1) tileMask: IntArray?)

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(BII)I")
	fun getHeight(@OriginalArg(1) z: Int, @OriginalArg(2) x: Int): Int {
		return this.tileHeights!![x]!![z]
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(III[[ZZI)V")
	abstract fun renderTiles(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) skipFlags: Int)

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(III[[ZZII)V")
	abstract fun renderFloor(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) visibility: Array<BooleanArray?>?, @OriginalArg(4) belowLowestPlane: Boolean, @OriginalArg(5) orthoZoom: Int, @OriginalArg(6) skipFlags: Int)

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(IIB)I")
	fun averageHeight(@OriginalArg(0) z: Int, @OriginalArg(1) x: Int): Int {
		@Pc(8) val x1: Int = z shr this.tileScale
		@Pc(13) val z1: Int = x shr this.tileScale
		if (x1 < 0 || z1 < 0 || x1 > this.width - 1 || this.length - 1 < z1) {
			return 0
		}
		@Pc(53) val offsetX: Int = z and (this.tileUnits - 1)
		@Pc(60) val offsetZ: Int = x and (this.tileUnits - 1)
		@Pc(88) val height0: Int = ((this.tileUnits - offsetX) * this.tileHeights!![x1]!![z1] + this.tileHeights[x1 + 1]!![z1] * offsetX) shr this.tileScale
		@Pc(118) val height1: Int = (offsetX * this.tileHeights[x1 + 1]!![z1 + 1] + (this.tileUnits - offsetX) * this.tileHeights[x1]!![z1 + 1]) shr this.tileScale
		return (offsetZ * height1 + (this.tileUnits - offsetZ) * height0) shr this.tileScale
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(IIIIIII[[Z)V")
	abstract fun drawMinimap(@OriginalArg(3) x1: Int, @OriginalArg(4) y1: Int, @OriginalArg(5) x2: Int, @OriginalArg(6) y2: Int, @OriginalArg(7) visibility: Array<BooleanArray?>?)

	@OriginalMember(owner = "client!s", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	abstract fun U(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) offsetsX: IntArray?, @OriginalArg(3) heightOffsets: IntArray?, @OriginalArg(4) offsetsZ: IntArray?, @OriginalArg(5) waterDepths: IntArray?, @OriginalArg(6) colours: IntArray?, @OriginalArg(7) blendedColours: IntArray?, @OriginalArg(8) materialIds: IntArray?, @OriginalArg(9) textureScales: IntArray?, @OriginalArg(10) waterColour: Int, @OriginalArg(11) waterDepth: Int, @OriginalArg(12) waterBias: Int, @OriginalArg(13) hasShadow: Boolean)

	@OriginalMember(owner = "client!s", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	abstract fun CA(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean)

	/** Casts the shadow onto this ground at x, z, raised by heightOffset; returns whether this level took it (the Java ground takes none). */
	@OriginalMember(owner = "client!s", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	abstract fun method8002(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) x: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) z: Int): Boolean

	@OriginalMember(owner = "client!s", name = "YA", descriptor = "()V")
	abstract fun YA()

	@OriginalMember(owner = "client!s", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	abstract fun fa(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) shadow: Shadow?): Shadow?

	companion object {
		@OriginalMember(owner = "client!tp", name = "a", descriptor = "()V")
		@JvmStatic
		fun addHdShadow() {
			Ground.addHdShadow(Scene.length)
		}

		@OriginalMember(owner = "client!za", name = "c", descriptor = "(II)V")
		@JvmStatic
		fun addHdShadow(@OriginalArg(1) planeCount: Int) {
			@Pc(1) var shadow: Shadow? = null
			for (@Pc(3) i in 1 until planeCount) {
				@Pc(8) val ground: Ground? = Scene.aClass65Array2!![i]
				if (ground != null) {
					@Pc(12) var j: Int = 0
					while (j < Camera.sceneLength) {
						@Pc(15) var k: Int = 0
						while (k < Scene.sceneWidth) {
							shadow = ground.fa(k, j, shadow)
							if (shadow != null) {
								@Pc(28) val x: Int = k shl Scene.tileUnits
								@Pc(32) val z: Int = j shl Scene.tileUnits
								@Pc(36) var m: Int = i - 1
								while (m >= 0) {
									@Pc(41) val ground2: Ground? = Scene.aClass65Array2!![m]
									if (ground2 != null) {
										@Pc(55) val heightDiffSw: Int = ground.getHeight(j, k) - ground2.getHeight(j, k)
										@Pc(71) val heightDiffSe: Int = ground.getHeight(j, k + 1) - ground2.getHeight(j, k + 1)
										@Pc(91) val heightDiffNe: Int = ground.getHeight(j + 1, k + 1) - ground2.getHeight(j + 1, k + 1)
										@Pc(107) val heightDiffNw: Int = ground.getHeight(j + 1, k) - ground2.getHeight(j + 1, k)
										ground2.CA(shadow, x, (heightDiffSw + heightDiffSe + heightDiffNe + heightDiffNw) / 4, z, 0, false)
									}
									m--
								}
							}
							k++
						}
						j++
					}
				}
			}
		}
	}
}
