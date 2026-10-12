package com.jagex.game.runetek5.config.gametip.component

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kh")
class ImageProgressBarInstance : AnchoredElement {

	@OriginalMember(owner = "client!kh", name = "q", descriptor = "I")
	@JvmField
	val colour: Int

	@OriginalMember(owner = "client!kh", name = "s", descriptor = "I")
	@JvmField
	val sprite: Int

	@OriginalMember(owner = "client!kh", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIIIIII)V")
	constructor(@OriginalArg(0) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(1) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(2) fileId: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) textOffsetY: Int, @OriginalArg(7) fontId: Int, @OriginalArg(8) textColor: Int, @OriginalArg(9) fillColor: Int, @OriginalArg(10) colour: Int, @OriginalArg(11) sprite: Int) : super(loadingScreenAlignmentX, loadingScreenAlignmentY, fileId, offsetY, width, height, textOffsetY, fontId, textColor) {
		this.colour = colour
		this.sprite = sprite
	}

	@OriginalMember(owner = "client!kh", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_2
	}

	companion object {
		@OriginalMember(owner = "client!gl", name = "a", descriptor = "(ILclient!cea;)Lclient!kh;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): ImageProgressBarInstance {
			@Pc(7) val progressBarInstance: AnchoredElement = AnchoredElement.decode(packet)
			@Pc(11) val fillColor: Int = packet!!.readInt()
			@Pc(15) val outlineColour: Int = packet.readInt()
			@Pc(24) val sprite: Int = packet.readUShort()
			return ImageProgressBarInstance(progressBarInstance.horizontalAnchor, progressBarInstance.verticalAnchor, progressBarInstance.offsetX, progressBarInstance.offsetY, progressBarInstance.width, progressBarInstance.height, progressBarInstance.textOffsetY, progressBarInstance.fileId, progressBarInstance.textColor, fillColor, outlineColour, sprite)
		}
	}
}
