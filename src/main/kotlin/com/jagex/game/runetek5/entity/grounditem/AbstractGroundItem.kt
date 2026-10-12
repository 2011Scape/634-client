package com.jagex.game.runetek5.entity.grounditem

import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!fha")
abstract class AbstractGroundItem @OriginalMember(owner = "client!fha", name = "<init>", descriptor = "(IIIII)V") constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int) : Entity() {

	init {
		this.x = x
		this.level = level.toByte()
		this.y = y
		this.virtualLevel = virtualLevel.toByte()
		this.z = z
	}

	@OriginalMember(owner = "client!fha", name = "c", descriptor = "(B)Z")
	final override fun isOccluded(): Boolean {
		@Pc(20) val positionEntityNode: InteractiveEntityContainer? = Scene.getTileInteractives(this.level.toInt(), this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
		return if (positionEntityNode != null && positionEntityNode.interactiveObject!!.supportsItems) Culling.isTileHidden(this.z shr Scene.tileUnits, positionEntityNode.interactiveObject!!.getModelMinY() + this.getModelMinY(), this.level.toInt(), this.x shr Scene.tileUnits) else Culling.isTileHidden(this.level.toInt(), this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
	}

	@OriginalMember(owner = "client!fha", name = "h", descriptor = "(I)Z")
	final override fun canRender(): Boolean {
		return Scene.tileInFrustum!![SceneVisibleTiles.visibleTiles + (this.x shr Scene.tileUnits) - SceneDrawer.cameraGridPositionX]!![SceneVisibleTiles.visibleTiles + (this.z shr Scene.tileUnits) - Scene.cameraTileZ]
	}

	@OriginalMember(owner = "client!fha", name = "b", descriptor = "(Z)V")
	final override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!fha", name = "a", descriptor = "([Lclient!ab;I)I")
	final override fun findLights(@OriginalArg(0) pointLights: Array<PointLight?>?): Int {
		return this.findTileLights(pointLights, this.x shr Scene.tileUnits, this.z shr Scene.tileUnits)
	}

	@OriginalMember(owner = "client!fha", name = "j", descriptor = "(I)Z")
	final override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!fha", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	final override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}
}
