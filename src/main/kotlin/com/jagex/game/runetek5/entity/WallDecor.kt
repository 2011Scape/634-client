package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.SceneDrawer

@OriginalClass("client!un")
abstract class WallDecor : Entity {

	@OriginalMember(owner = "client!un", name = "D", descriptor = "S")
	@JvmField
	var offsetZ: Short

	@OriginalMember(owner = "client!un", name = "R", descriptor = "S")
	@JvmField
	var offsetX: Short

	@OriginalMember(owner = "client!un", name = "<init>", descriptor = "(IIIIIII)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int, @OriginalArg(5) offsetX: Int, @OriginalArg(6) offsetZ: Int) : super() {
		this.offsetZ = offsetZ.toShort()
		this.virtualLevel = virtualLevel.toByte()
		this.y = y
		this.x = x
		this.offsetX = offsetX.toShort()
		this.z = z
		this.level = level.toByte()
	}

	@OriginalMember(owner = "client!un", name = "h", descriptor = "(I)Z")
	final override fun canRender(): Boolean {
		return Scene.tileInFrustum!![(this.x shr Scene.tileUnits) + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX]!![SceneVisibleTiles.visibleTiles + (this.z shr Scene.tileUnits) - Scene.cameraTileZ]
	}

	@OriginalMember(owner = "client!un", name = "c", descriptor = "(B)Z")
	final override fun isOccluded(): Boolean {
		return Culling.isTileHidden(this.z shr Scene.tileUnits, this.getModelMinY(), this.virtualLevel.toInt(), this.x shr Scene.tileUnits)
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	final override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!un", name = "j", descriptor = "(I)Z")
	final override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!un", name = "b", descriptor = "(Z)V")
	final override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!un", name = "a", descriptor = "([Lclient!ab;I)I")
	final override fun findLights(@OriginalArg(0) pointLights: Array<PointLight?>?): Int {
		return this.findTileLights(pointLights, this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
	}
}
