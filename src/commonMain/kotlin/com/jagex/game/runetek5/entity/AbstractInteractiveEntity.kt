package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.cache.node.PointLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.SceneDrawer
import kotlin.jvm.JvmField

@OriginalClass("client!jb")
abstract class AbstractInteractiveEntity : Entity {

	@OriginalMember(owner = "client!jb", name = "B", descriptor = "Z")
	@JvmField
	val supportsItems: Boolean

	@OriginalMember(owner = "client!jb", name = "H", descriptor = "S")
	@JvmField
	var endZ: Short

	@OriginalMember(owner = "client!jb", name = "F", descriptor = "B")
	@JvmField
	val rotation: Byte

	@OriginalMember(owner = "client!jb", name = "M", descriptor = "S")
	@JvmField
	var endX: Short

	@OriginalMember(owner = "client!jb", name = "L", descriptor = "S")
	@JvmField
	var startZ: Short

	@OriginalMember(owner = "client!jb", name = "D", descriptor = "S")
	@JvmField
	var startX: Short

	@OriginalMember(owner = "client!jb", name = "<init>", descriptor = "(IIIIIIIIIZB)V")
	constructor(@OriginalArg(0) level: Int, @OriginalArg(1) virtualLevel: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) startX: Int, @OriginalArg(6) endX: Int, @OriginalArg(7) startZ: Int, @OriginalArg(8) endZ: Int, @OriginalArg(9) supportsItems: Boolean, @OriginalArg(10) rotation: Byte) : super() {
		this.y = y
		this.supportsItems = supportsItems
		this.endZ = endZ.toShort()
		this.x = x
		this.rotation = rotation
		this.endX = endX.toShort()
		this.z = z
		this.level = level.toByte()
		this.startZ = startZ.toShort()
		this.startX = startX.toShort()
		this.virtualLevel = virtualLevel.toByte()
	}

	@OriginalMember(owner = "client!jb", name = "h", descriptor = "(I)Z")
	final override fun canRender(): Boolean {
		@Pc(13) var i: Int = this.startX.toInt()
		while (i <= this.endX) {
			@Pc(18) var j: Int = this.startZ.toInt()
			while (j <= this.endZ) {
				@Pc(27) val tileX: Int = i + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX
				if (tileX >= 0 && tileX < Scene.tileInFrustum!!.size) {
					@Pc(48) val tileZ: Int = SceneVisibleTiles.visibleTiles + j - Scene.cameraTileZ
					if (tileZ >= 0 && tileZ < Scene.tileInFrustum!!.size && Scene.tileInFrustum!![tileX]!![tileZ]) {
						return true
					}
				}
				j++
			}
			i++
		}
		return false
	}

	@OriginalMember(owner = "client!jb", name = "c", descriptor = "(B)Z")
	final override fun isOccluded(): Boolean {
		return Culling.hideTile(this.virtualLevel.toInt(), this.endZ.toInt(), this.startZ.toInt(), this.getModelMinY(), this.startX.toInt(), this.endX.toInt())
	}

	@OriginalMember(owner = "client!jb", name = "e", descriptor = "(B)V")
	open fun updateBounds() {
	}

	@OriginalMember(owner = "client!jb", name = "a", descriptor = "([Lclient!ab;I)I")
	final override fun findLights(@OriginalArg(0) pointLights: Array<PointLight?>?): Int {
		@Pc(7) var lightCount: Int = 0
		@Pc(53) val local53: Int
		label113@ for (@Pc(10) i in this.startX.toInt()..this.endX.toInt()) {
			label111@ for (@Pc(15) j in this.startZ.toInt()..this.endZ.toInt()) {
				@Pc(26) val lightUids: Long = Scene.tileLightUids!![this.level.toInt()]!![i]!![j]
				@Pc(28) var uidShift: Long = 0L
				while (true) {
					label106@ while (true) {
						if (uidShift > 48L) {
							continue@label111
						}
						@Pc(38) val slot: Int = ((lightUids ushr uidShift.toInt()) and 0xFFFFL).toInt()
						if (slot <= 0) {
							continue@label111
						}
						@Pc(51) val staticPointLight: StaticPointLight? = Scene.aClass300Array1!![slot - 1]
						var lightIndex: Int = 0
						while (lightIndex < lightCount) {
							if (staticPointLight!!.pointLight === pointLights!![lightIndex]) {
								uidShift += 16L
								continue@label106
							}
							lightIndex++
						}
						pointLights!![lightCount++] = staticPointLight!!.pointLight
						if (lightCount == 4) {
							break@label113
						}
						uidShift += 16L
					}
				}
			}
		}
		@Pc(114) var k: Int = lightCount
		while (k < 4) {
			pointLights!![k] = null
			k++
		}
		if (this.rotation.toInt() != 0) {
			@Pc(135) val cameraOffsetX: Int = this.startX - SceneDrawer.cameraGridPositionX
			val cameraOffsetZ: Int = this.startZ - Scene.cameraTileZ
			@Pc(153) val adjacentX1: Short
			@Pc(166) val adjacentX2: Int
			@Pc(161) val adjacentZ1: Int
			@Pc(156) val adjacentZ2: Short
			if (this.rotation.toInt() == 1) {
				if (cameraOffsetX >= cameraOffsetZ) {
					adjacentX1 = this.startX
					adjacentZ2 = this.startZ
					adjacentZ1 = this.startZ + 1
					adjacentX2 = this.startX - 1
				} else {
					adjacentZ2 = this.startZ
					adjacentZ1 = this.startZ - 1
					adjacentX1 = this.startX
					adjacentX2 = this.startX + 1
				}
			} else if (cameraOffsetZ > -cameraOffsetX) {
				adjacentX2 = this.startX - 1
				adjacentZ2 = this.startZ
				adjacentZ1 = this.startZ - 1
				adjacentX1 = this.startX
			} else {
				adjacentZ1 = this.startZ + 1
				adjacentX1 = this.startX
				adjacentZ2 = this.startZ
				adjacentX2 = this.startX + 1
			}
			@Pc(230) var m: Int = 0
			label77@ while (m < lightCount) {
				@Pc(241) var adjacentLightUids: Long = Scene.tileLightUids!![this.level.toInt()]!![adjacentX1.toInt()]!![adjacentZ1]
				@Pc(252) var staticPointLight2: StaticPointLight?
				while (adjacentLightUids != 0L) {
					staticPointLight2 = Scene.aClass300Array1!![((adjacentLightUids and 0xFFFFL) - 1L).toInt()]
					adjacentLightUids = adjacentLightUids ushr 0x10
					if (staticPointLight2!!.pointLight === pointLights!![m]) {
						m++
						continue@label77
					}
				}
				adjacentLightUids = Scene.tileLightUids!![this.level.toInt()]!![adjacentX2]!![adjacentZ2.toInt()]
				while (adjacentLightUids != 0L) {
					staticPointLight2 = Scene.aClass300Array1!![((adjacentLightUids and 0xFFFFL) - 1L).toInt()]
					adjacentLightUids = adjacentLightUids ushr 0x10
					if (pointLights!![m] === staticPointLight2!!.pointLight) {
						m++
						continue@label77
					}
				}
				@Pc(308) var index: Int = m
				while (index < lightCount - 1) {
					pointLights!![index] = pointLights[index + 1]
					index++
				}
				lightCount--
				m++
			}
		}
		return lightCount
	}
}
