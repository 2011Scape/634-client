package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!wg")
class RotatingImageInstance : SpritePositioning {

	@OriginalMember(owner = "client!wg", name = "m", descriptor = "I")
	@JvmField
	val utAngle: Int

	@OriginalMember(owner = "client!wg", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;III)V")
	constructor(@OriginalArg(0) spriteId: Int, @OriginalArg(1) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(2) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(3) spriteOffsetX: Int, @OriginalArg(4) spriteOffsetY: Int, @OriginalArg(5) utAngle: Int) : super(spriteId, loadingScreenAlignmentX, loadingScreenAlignmentY, spriteOffsetX, spriteOffsetY) {
		this.utAngle = utAngle
	}

	@OriginalMember(owner = "client!wg", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_5
	}

	companion object {
		@OriginalMember(owner = "client!dw", name = "a", descriptor = "(BLclient!cea;)Lclient!wg;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): RotatingImageInstance {
			@Pc(14) val imageInstance: SpritePositioning = SpritePositioning.decode(packet)
			@Pc(18) val angleStep: Int = packet!!.readMedium()
			return RotatingImageInstance(imageInstance.spriteId, imageInstance.horizontalAnchor, imageInstance.verticalAnchor, imageInstance.y, imageInstance.x, angleStep)
		}
	}
}
