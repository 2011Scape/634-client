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

@OriginalClass("client!fn")
open class AnchoredElement : ITipDetails {

	@OriginalMember(owner = "client!fn", name = "c", descriptor = "I")
	@JvmField
	val textColor: Int

	@OriginalMember(owner = "client!fn", name = "m", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!fn", name = "g", descriptor = "I")
	@JvmField
	val fileId: Int

	@OriginalMember(owner = "client!fn", name = "h", descriptor = "I")
	@JvmField
	val textOffsetY: Int

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "I")
	@JvmField
	val offsetY: Int

	@OriginalMember(owner = "client!fn", name = "o", descriptor = "Lclient!ec;")
	@JvmField
	val verticalAnchor: LoadingScreenAlignmentY?

	@OriginalMember(owner = "client!fn", name = "l", descriptor = "I")
	@JvmField
	val offsetX: Int

	@OriginalMember(owner = "client!fn", name = "j", descriptor = "Lclient!sd;")
	@JvmField
	val horizontalAnchor: LoadingScreenAlignmentX?

	@OriginalMember(owner = "client!fn", name = "e", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!fn", name = "<init>", descriptor = "(Lclient!sd;Lclient!ec;IIIIIII)V")
	constructor(@OriginalArg(0) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(1) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(2) fileId: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) textOffsetY: Int, @OriginalArg(7) fontId: Int, @OriginalArg(8) textColor: Int) {
		this.textColor = textColor
		this.width = width
		this.fileId = fontId
		this.textOffsetY = textOffsetY
		this.offsetY = offsetY
		this.verticalAnchor = loadingScreenAlignmentY
		this.offsetX = fileId
		this.horizontalAnchor = loadingScreenAlignmentX
		this.height = height
	}

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return null
	}

	companion object {
		@OriginalMember(owner = "client!iha", name = "a", descriptor = "(BLclient!cea;)Lclient!fn;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): AnchoredElement {
			@Pc(10) val loadingScreenAlignmentX: LoadingScreenAlignmentX? = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet!!.readUByte()]
			@Pc(22) val loadingScreenAlignmentY: LoadingScreenAlignmentY? = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()]
			@Pc(26) val offsetX: Int = packet.readShort()
			@Pc(30) val offsetY: Int = packet.readShort()
			@Pc(34) val width: Int = packet.readUShort()
			@Pc(38) val height: Int = packet.readUShort()
			@Pc(42) val textOffsetY: Int = packet.readShort()
			@Pc(46) val fileId: Int = packet.readInt()
			@Pc(50) val textColor: Int = packet.readInt()
			return AnchoredElement(loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textOffsetY, fileId, textColor)
		}
	}
}
