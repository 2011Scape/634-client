package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ou")
class ColorLoadingBarElement : AnchoredElement {

	@OriginalMember(owner = "client!ou", name = "q", descriptor = "I")
	@JvmField
	val borderColour: Int

	@OriginalMember(owner = "client!ou", name = "w", descriptor = "I")
	@JvmField
	val backgroundColour: Int

	@OriginalMember(owner = "client!ou", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIII)V")
	constructor(@OriginalArg(0) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(1) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(2) offsetX: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) textOffsetY: Int, @OriginalArg(7) fileId: Int, @OriginalArg(8) textColor: Int, @OriginalArg(9) fillColor: Int, @OriginalArg(10) outlineColor: Int) : super(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor) {
		this.borderColour = outlineColor
		this.backgroundColour = fillColor
	}

	@OriginalMember(owner = "client!ou", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_12
	}

	companion object {
		@OriginalMember(owner = "client!jd", name = "a", descriptor = "(ILclient!cea;)Lclient!ou;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): ColorLoadingBarElement {
			@Pc(7) val progressBarInstance: AnchoredElement = AnchoredElement.decode(packet)
			@Pc(19) val fillColor: Int = packet!!.readInt()
			@Pc(23) val outlineColor: Int = packet.readInt()
			return ColorLoadingBarElement(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, fillColor, outlineColor)
		}
	}
}
