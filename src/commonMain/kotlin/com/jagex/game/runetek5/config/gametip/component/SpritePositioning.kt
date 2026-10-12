package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.gametip.ITipDetails
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!vr")
open class SpritePositioning : ITipDetails {

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "I")
	@JvmField
	val y: Int

	@OriginalMember(owner = "client!vr", name = "i", descriptor = "I")
	@JvmField
	val spriteId: Int

	@OriginalMember(owner = "client!vr", name = "b", descriptor = "I")
	@JvmField
	val x: Int

	@OriginalMember(owner = "client!vr", name = "f", descriptor = "Lclient!sd;")
	@JvmField
	val horizontalAnchor: LoadingScreenAlignmentX?

	@OriginalMember(owner = "client!vr", name = "c", descriptor = "Lclient!ec;")
	@JvmField
	val verticalAnchor: LoadingScreenAlignmentY?

	@OriginalMember(owner = "client!vr", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;II)V")
	constructor(@OriginalArg(0) spriteId: Int, @OriginalArg(1) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(2) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(3) spriteOffsetX: Int, @OriginalArg(4) spriteOffsetY: Int) {
		this.y = spriteOffsetX
		this.spriteId = spriteId
		this.x = spriteOffsetY
		this.horizontalAnchor = loadingScreenAlignmentX
		this.verticalAnchor = loadingScreenAlignmentY
	}

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_10
	}

	companion object {
		@OriginalMember(owner = "client!wg", name = "a", descriptor = "(Lclient!cea;I)Lclient!vr;")
		@JvmStatic
		fun decode(@OriginalArg(0) packet: Packet?): SpritePositioning {
			@Pc(7) val spriteId: Int = packet!!.readUShort()
			@Pc(14) val loadingScreenAlignmentX: LoadingScreenAlignmentX? = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()]
			@Pc(21) val loadingScreenAlignmentY: LoadingScreenAlignmentY? = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()]
			@Pc(32) val spriteOffsetX: Int = packet.readShort()
			@Pc(36) val spriteOffsetY: Int = packet.readShort()
			return SpritePositioning(spriteId, loadingScreenAlignmentX, loadingScreenAlignmentY, spriteOffsetX, spriteOffsetY)
		}
	}
}
