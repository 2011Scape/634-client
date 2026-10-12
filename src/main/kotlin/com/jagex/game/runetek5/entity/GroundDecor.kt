package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.cache.node.PointLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.scene.culling.Culling

@OriginalClass("client!aba")
abstract class GroundDecor : Entity {

	@OriginalMember(owner = "client!aba", name = "F", descriptor = "S")
	@JvmField
	val offsetY: Short

	@OriginalMember(owner = "client!aba", name = "<init>", descriptor = "(IIIIII)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int, @OriginalArg(5) groundDecorationHeight: Int) : super() {
		this.virtualLevel = virtualLevel.toByte()
		this.x = x
		this.z = z
		this.offsetY = groundDecorationHeight.toShort()
		this.y = y
		this.level = level.toByte()
	}

	@OriginalMember(owner = "client!aba", name = "c", descriptor = "(B)Z")
	final override fun isOccluded(): Boolean {
		return Culling.isTileHidden(this.virtualLevel.toInt(), this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
	}

	@OriginalMember(owner = "client!aba", name = "a", descriptor = "([Lclient!ab;I)I")
	final override fun findLights(@OriginalArg(0) pointLights: Array<PointLight?>?): Int {
		return this.findTileLights(pointLights, this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
	}

	@OriginalMember(owner = "client!aba", name = "h", descriptor = "(I)Z")
	final override fun canRender(): Boolean {
		return Scene.tileInFrustum!![SceneVisibleTiles.visibleTiles + (this.x shr Scene.tileUnits) - SceneDrawer.cameraGridPositionX]!![(this.z shr Scene.tileUnits) + SceneVisibleTiles.visibleTiles - Scene.cameraTileZ]
	}
}
