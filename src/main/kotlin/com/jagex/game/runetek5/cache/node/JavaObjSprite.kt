package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!gda")
class JavaObjSprite : Node {

	@OriginalMember(owner = "client!gda", name = "u", descriptor = "I")
	@JvmField
	val objNumMode: Int

	@OriginalMember(owner = "client!gda", name = "s", descriptor = "I")
	@JvmField
	val invCount: Int

	@OriginalMember(owner = "client!gda", name = "v", descriptor = "I")
	@JvmField
	val graphicShadow: Int

	@OriginalMember(owner = "client!gda", name = "x", descriptor = "Z")
	@JvmField
	val objWearCol: Boolean

	@OriginalMember(owner = "client!gda", name = "p", descriptor = "I")
	@JvmField
	val objId: Int

	@OriginalMember(owner = "client!gda", name = "w", descriptor = "I")
	@JvmField
	val outline: Int

	@OriginalMember(owner = "client!gda", name = "<init>", descriptor = "(IIIIIZ)V")
	constructor(@OriginalArg(0) outline: Int, @OriginalArg(1) graphicShadow: Int, @OriginalArg(2) invCount: Int, @OriginalArg(3) objNumMode: Int, @OriginalArg(4) objId: Int, @OriginalArg(5) objWearCol: Boolean) {
		this.objNumMode = objNumMode
		this.invCount = invCount
		this.graphicShadow = graphicShadow
		this.objWearCol = objWearCol
		this.objId = objId
		this.outline = outline
	}
}
