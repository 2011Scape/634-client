package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.cache.node.PointLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.Protocol
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.scene.culling.Culling
import kotlin.jvm.JvmField

@OriginalClass("client!qfa")
abstract class Wall : Entity {

	@OriginalMember(owner = "client!qfa", name = "B", descriptor = "S")
	@JvmField
	val sideMask: Short

	@OriginalMember(owner = "client!qfa", name = "<init>", descriptor = "(IIIIII)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int, @OriginalArg(5) sideMask: Int) : super() {
		this.x = x
		this.y = y
		this.virtualLevel = virtualLevel.toByte()
		this.z = z
		this.level = level.toByte()
		this.sideMask = sideMask.toShort()
	}

	@OriginalMember(owner = "client!qfa", name = "a", descriptor = "([Lclient!ab;I)I")
	final override fun findLights(@OriginalArg(0) lights: Array<PointLight?>?): Int {
		@Pc(10) val localX: Int = this.x shr Scene.tileUnits
		@Pc(15) val localZ: Int = this.z shr Scene.tileUnits
		@Pc(25) var directionIndex: Int = 0
		if (localX == SceneDrawer.cameraGridPositionX) {
			directionIndex++
		} else if (localX > SceneDrawer.cameraGridPositionX) {
			directionIndex += 2
		}
		if (localZ == Scene.cameraTileZ) {
			directionIndex += 3
		} else if (localZ < Scene.cameraTileZ) {
			directionIndex += 6
		}
		@Pc(57) val facingSides: Int = Protocol.removedIds[directionIndex]
		val sideMask: Int = this.sideMask.toInt()
		if ((sideMask and facingSides) != 0) {
			return this.findTileLights(lights, localX, localZ)
		} else if (sideMask == 1 && localX > 0) {
			return this.findTileLights(lights, localX - 1, localZ)
		} else if (sideMask == 4 && localX <= Scene.sceneWidth) {
			return this.findTileLights(lights, localX + 1, localZ)
		} else if (sideMask == 8 && localZ > 0) {
			return this.findTileLights(lights, localX, localZ - 1)
		} else if (sideMask == 2 && Camera.sceneLength >= localZ) {
			return this.findTileLights(lights, localX, localZ + 1)
		} else if (sideMask == 16 && localX > 0 && localZ <= Camera.sceneLength) {
			return this.findTileLights(lights, localX - 1, localZ + 1)
		} else if (sideMask == 32 && Scene.sceneWidth >= localX && Camera.sceneLength >= localZ) {
			return this.findTileLights(lights, localX + 1, localZ + 1)
		} else if (sideMask == 128 && localX > 0 && localZ > 0) {
			return this.findTileLights(lights, localX - 1, localZ - 1)
		} else if (sideMask == 64 && Scene.sceneWidth >= localX && localZ > 0) {
			return this.findTileLights(lights, localX + 1, localZ + -1)
		} else {
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!qfa", name = "c", descriptor = "(B)Z")
	final override fun isOccluded(): Boolean {
		return Culling.hideWall(this.z shr Scene.tileUnits, this.x shr Scene.tileUnits, this.virtualLevel.toInt(), this)
	}

	@OriginalMember(owner = "client!qfa", name = "h", descriptor = "(I)Z")
	final override fun canRender(): Boolean {
		return Scene.tileInFrustum!![(this.x shr Scene.tileUnits) + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX]!![SceneVisibleTiles.visibleTiles + (this.z shr Scene.tileUnits) - Scene.cameraTileZ]
	}
}
