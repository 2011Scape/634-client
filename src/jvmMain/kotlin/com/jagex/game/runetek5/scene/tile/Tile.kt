package com.jagex.game.runetek5.scene.tile

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.GroundDecor
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.Wall
import com.jagex.game.runetek5.entity.WallDecor
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem

@OriginalClass("client!es")
class Tile @OriginalMember(owner = "client!es", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) plane: Int) {

	@OriginalMember(owner = "client!es", name = "d", descriptor = "Lclient!un;")
	@JvmField
	var primaryWallDecoration: WallDecor? = null

	@OriginalMember(owner = "client!es", name = "e", descriptor = "Lclient!oga;")
	@JvmField
	var interactives: InteractiveEntityContainer? = null

	@OriginalMember(owner = "client!es", name = "f", descriptor = "S")
	@JvmField
	var cullOffsetZ: Short = 0

	@OriginalMember(owner = "client!es", name = "g", descriptor = "S")
	@JvmField
	var cullHeightZ: Short = 0

	@OriginalMember(owner = "client!es", name = "i", descriptor = "Lclient!es;")
	@JvmField
	var bridge: Tile? = null

	@OriginalMember(owner = "client!es", name = "j", descriptor = "S")
	@JvmField
	var cullOffsetX: Short = 0

	@OriginalMember(owner = "client!es", name = "k", descriptor = "Lclient!aba;")
	@JvmField
	var groundDecoration: GroundDecor? = null

	@OriginalMember(owner = "client!es", name = "l", descriptor = "Lclient!qfa;")
	@JvmField
	var secondaryWall: Wall? = null

	@OriginalMember(owner = "client!es", name = "o", descriptor = "Lclient!qfa;")
	@JvmField
	var primaryWall: Wall? = null

	@OriginalMember(owner = "client!es", name = "p", descriptor = "Lclient!un;")
	@JvmField
	var secondaryWallDecoration: WallDecor? = null

	@OriginalMember(owner = "client!es", name = "q", descriptor = "Lclient!fha;")
	@JvmField
	var groundItem: AbstractGroundItem? = null

	@OriginalMember(owner = "client!es", name = "r", descriptor = "S")
	@JvmField
	var cullHeightX: Short = 0

	@OriginalMember(owner = "client!es", name = "h", descriptor = "B")
	@JvmField
	var plane: Byte = 0

	init {
		this.plane = plane.toByte()
	}

	@OriginalMember(owner = "client!es", name = "a", descriptor = "(Z)V")
	fun clearInteractives() {
		while (this.interactives != null) {
			@Pc(7) val positionEntityNode: InteractiveEntityContainer? = this.interactives!!.child
			this.interactives!!.release()
			this.interactives = positionEntityNode
		}
	}
}
