package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!fu")
class AnimatedLoadingBarElement : MultiSpriteLoadingBarElement {

	@OriginalMember(owner = "client!fu", name = "D", descriptor = "I")
	@JvmField
	val scrollSpeed: Int

	@OriginalMember(owner = "client!fu", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIIIIIII)V")
	constructor(@OriginalArg(0) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(1) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(2) offsetX: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) textOffsetY: Int, @OriginalArg(7) fileId: Int, @OriginalArg(8) textColor: Int, @OriginalArg(9) fillSpriteId: Int, @OriginalArg(10) emptySpriteId: Int, @OriginalArg(11) leftCapSpriteId: Int, @OriginalArg(12) rightCapSpriteId: Int, @OriginalArg(13) topBorderSpriteId: Int, @OriginalArg(14) bottomBorderSpriteId: Int, @OriginalArg(15) scrollSpeed: Int) : super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor, fillSpriteId, emptySpriteId, leftCapSpriteId, rightCapSpriteId, topBorderSpriteId, bottomBorderSpriteId) {
		this.scrollSpeed = scrollSpeed
	}

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_9
	}

	companion object {
		@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ILclient!cea;)Lclient!fu;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): AnimatedLoadingBarElement {
			@Pc(9) val fancyProgressBarInstance: MultiSpriteLoadingBarElement = MultiSpriteLoadingBarElement.decode(packet)
			@Pc(13) val scrollSpeed: Int = packet!!.readShort()
			return AnimatedLoadingBarElement(fancyProgressBarInstance.horizontalAnchor, fancyProgressBarInstance.verticalAnchor, fancyProgressBarInstance.offsetX, fancyProgressBarInstance.offsetY, fancyProgressBarInstance.width, fancyProgressBarInstance.height, fancyProgressBarInstance.textOffsetY, fancyProgressBarInstance.fileId, fancyProgressBarInstance.textColor, fancyProgressBarInstance.spriteD, fancyProgressBarInstance.spriteF, fancyProgressBarInstance.spriteC, fancyProgressBarInstance.spriteB, fancyProgressBarInstance.spriteA, fancyProgressBarInstance.spriteE, scrollSpeed)
		}
	}
}
