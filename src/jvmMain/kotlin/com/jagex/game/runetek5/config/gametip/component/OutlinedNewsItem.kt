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

@OriginalClass("client!nha")
class OutlinedNewsItem : ITipDetails {

	@OriginalMember(owner = "client!nha", name = "b", descriptor = "Z")
	@JvmField
	val drawOutline: Boolean

	@OriginalMember(owner = "client!nha", name = "k", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!nha", name = "f", descriptor = "I")
	@JvmField
	val item: Int

	@OriginalMember(owner = "client!nha", name = "l", descriptor = "I")
	@JvmField
	val x: Int

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "I")
	@JvmField
	val textColor: Int

	@OriginalMember(owner = "client!nha", name = "q", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!nha", name = "h", descriptor = "I")
	@JvmField
	val y: Int

	@OriginalMember(owner = "client!nha", name = "e", descriptor = "I")
	@JvmField
	val lineColour: Int

	@OriginalMember(owner = "client!nha", name = "m", descriptor = "Lclient!ec;")
	@JvmField
	val verticalAlignment: LoadingScreenAlignmentY?

	@OriginalMember(owner = "client!nha", name = "p", descriptor = "I")
	@JvmField
	val backgroundColor: Int

	@OriginalMember(owner = "client!nha", name = "j", descriptor = "Lclient!sd;")
	@JvmField
	val horizontalAlignment: LoadingScreenAlignmentX?

	@OriginalMember(owner = "client!nha", name = "<init>", descriptor = "(ILclient!sd;Lclient!ec;IIIIIIIZ)V")
	constructor(@OriginalArg(0) item: Int, @OriginalArg(1) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(2) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(3) offsetX: Int, @OriginalArg(4) offsetY: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) textColor: Int, @OriginalArg(8) shadeColor: Int, @OriginalArg(9) lineColour: Int, @OriginalArg(10) drawOutline: Boolean) {
		this.drawOutline = drawOutline
		this.height = height
		this.item = item
		this.x = offsetX
		this.textColor = textColor
		this.width = width
		this.y = offsetY
		this.lineColour = lineColour
		this.verticalAlignment = loadingScreenAlignmentY
		this.backgroundColor = shadeColor
		this.horizontalAlignment = loadingScreenAlignmentX
	}

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_4
	}

	companion object {
		@OriginalMember(owner = "client!wp", name = "a", descriptor = "(Lclient!cea;B)Lclient!nha;")
		@JvmStatic
		fun decode(@OriginalArg(0) packet: Packet?): OutlinedNewsItem {
			@Pc(7) val itemIndex: Int = packet!!.readUByte()
			@Pc(14) val loadingScreenAlignmentX: LoadingScreenAlignmentX? = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()]
			@Pc(21) val loadingScreenAlignmentY: LoadingScreenAlignmentY? = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()]
			@Pc(25) val offsetX: Int = packet.readShort()
			@Pc(29) val offsetY: Int = packet.readShort()
			@Pc(33) val width: Int = packet.readUShort()
			@Pc(37) val height: Int = packet.readUShort()
			@Pc(41) val textColor: Int = packet.readInt()
			@Pc(45) val shadeColor: Int = packet.readInt()
			@Pc(49) val lineColour: Int = packet.readInt()
			@Pc(66) val drawOutline: Boolean = packet.readUByte() == 1
			return OutlinedNewsItem(itemIndex, loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, width, height, textColor, shadeColor, lineColour, drawOutline)
		}
	}
}
