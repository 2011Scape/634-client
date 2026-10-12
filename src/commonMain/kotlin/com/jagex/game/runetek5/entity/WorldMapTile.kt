package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!rt")
class WorldMapTile : EntityNode {

	@OriginalMember(owner = "client!rt", name = "o", descriptor = "B")
	@JvmField
	val x: Byte

	@OriginalMember(owner = "client!rt", name = "m", descriptor = "B")
	@JvmField
	val z: Byte

	@OriginalMember(owner = "client!rt", name = "n", descriptor = "I")
	@JvmField
	var underlayIdOrColour: Int

	@OriginalMember(owner = "client!rt", name = "i", descriptor = "B")
	@JvmField
	val overlayId: Byte

	@OriginalMember(owner = "client!rt", name = "l", descriptor = "B")
	@JvmField
	val shape: Byte

	@OriginalMember(owner = "client!rt", name = "j", descriptor = "[S")
	@JvmField
	val locIds: ShortArray?

	@OriginalMember(owner = "client!rt", name = "k", descriptor = "[B")
	@JvmField
	val locShapes: ByteArray?

	@OriginalMember(owner = "client!rt", name = "<init>", descriptor = "(IIIII[S[B)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) z: Int, @OriginalArg(2) underlayId: Int, @OriginalArg(3) overlayId: Int, @OriginalArg(4) shape: Int, @OriginalArg(5) locIds: ShortArray?, @OriginalArg(6) locShapes: ByteArray?) : super() {
		this.x = x.toByte()
		this.z = z.toByte()
		this.underlayIdOrColour = underlayId
		this.overlayId = overlayId.toByte()
		this.shape = shape.toByte()
		this.locIds = locIds
		this.locShapes = locShapes
	}
}
