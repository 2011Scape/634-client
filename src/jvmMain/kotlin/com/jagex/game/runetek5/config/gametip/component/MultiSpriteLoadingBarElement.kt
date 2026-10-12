package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!um")
open class MultiSpriteLoadingBarElement : AnchoredElement {

	@OriginalMember(owner = "client!um", name = "v", descriptor = "I")
	@JvmField
	val spriteA: Int

	@OriginalMember(owner = "client!um", name = "x", descriptor = "I")
	@JvmField
	val spriteB: Int

	@OriginalMember(owner = "client!um", name = "y", descriptor = "I")
	@JvmField
	val spriteC: Int

	@OriginalMember(owner = "client!um", name = "u", descriptor = "I")
	@JvmField
	val spriteD: Int

	@OriginalMember(owner = "client!um", name = "s", descriptor = "I")
	@JvmField
	val spriteE: Int

	@OriginalMember(owner = "client!um", name = "q", descriptor = "I")
	@JvmField
	val spriteF: Int

	@OriginalMember(owner = "client!um", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIIIIII)V")
	constructor(@OriginalArg(0) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(1) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(2) offsetX: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) textOffsetY: Int, @OriginalArg(7) fileId: Int, @OriginalArg(8) textColor: Int, @OriginalArg(9) spriteD: Int, @OriginalArg(10) spriteF: Int, @OriginalArg(11) spriteC: Int, @OriginalArg(12) spriteB: Int, @OriginalArg(13) spriteA: Int, @OriginalArg(14) spriteE: Int) : super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor) {
		this.spriteA = spriteA
		this.spriteB = spriteB
		this.spriteC = spriteC
		this.spriteD = spriteD
		this.spriteE = spriteE
		this.spriteF = spriteF
	}

	@OriginalMember(owner = "client!um", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.OPPLAYER5
	}

	companion object {
		@OriginalMember(owner = "client!wl", name = "a", descriptor = "(ILclient!cea;)Lclient!um;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): MultiSpriteLoadingBarElement {
			@Pc(7) val progressBarInstance: AnchoredElement = AnchoredElement.decode(packet)
			@Pc(13) val spriteD: Int = packet!!.readUShort()
			@Pc(17) val spriteF: Int = packet.readUShort()
			@Pc(25) val spriteC: Int = packet.readUShort()
			@Pc(31) val spriteB: Int = packet.readUShort()
			@Pc(35) val spriteA: Int = packet.readUShort()
			@Pc(39) val spriteE: Int = packet.readUShort()
			return MultiSpriteLoadingBarElement(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, spriteD, spriteF, spriteC, spriteB, spriteA, spriteE)
		}
	}
}
