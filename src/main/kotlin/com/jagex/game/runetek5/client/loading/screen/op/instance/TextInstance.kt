package com.jagex.game.runetek5.client.loading.screen.op.instance

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.gametip.ITipDetails
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ot")
class TextInstance @OriginalMember(owner = "client!ot", name = "<init>", descriptor = "(Ljava/lang/String;Lclient!sd;Lclient!ec;IIIIIIIIII)V") constructor(@OriginalArg(0) string: String?, @OriginalArg(1) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(2) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(3) x: Int, @OriginalArg(4) textHorizontalAlignment: Int, @OriginalArg(5) shadowColour: Int, @OriginalArg(6) textColour: Int, @OriginalArg(7) y: Int, @OriginalArg(8) verticalSpacing: Int, @OriginalArg(9) textVerticalAlignment: Int, @OriginalArg(10) font: Int, @OriginalArg(11) height: Int, @OriginalArg(12) width: Int) : ITipDetails {

	@OriginalMember(owner = "client!ot", name = "p", descriptor = "I")
	@JvmField
	val textColour: Int

	@OriginalMember(owner = "client!ot", name = "b", descriptor = "I")
	@JvmField
	val textHorizontalAlignment: Int

	@OriginalMember(owner = "client!ot", name = "m", descriptor = "I")
	@JvmField
	val shadowColour: Int

	@OriginalMember(owner = "client!ot", name = "n", descriptor = "I")
	@JvmField
	val font: Int

	@OriginalMember(owner = "client!ot", name = "l", descriptor = "Ljava/lang/String;")
	@JvmField
	val text: String?

	@OriginalMember(owner = "client!ot", name = "r", descriptor = "Lclient!sd;")
	@JvmField
	val horizontalAlignment: LoadingScreenAlignmentX?

	@OriginalMember(owner = "client!ot", name = "a", descriptor = "I")
	@JvmField
	val verticalSpacing: Int

	@OriginalMember(owner = "client!ot", name = "j", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!ot", name = "q", descriptor = "I")
	@JvmField
	val x: Int

	@OriginalMember(owner = "client!ot", name = "o", descriptor = "Lclient!ec;")
	@JvmField
	val verticalAlignment: LoadingScreenAlignmentY?

	@OriginalMember(owner = "client!ot", name = "f", descriptor = "I")
	@JvmField
	val textVerticalAlignment: Int

	@OriginalMember(owner = "client!ot", name = "e", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!ot", name = "g", descriptor = "I")
	@JvmField
	val y: Int

	init {
		this.textColour = textColour
		this.textHorizontalAlignment = textHorizontalAlignment
		this.shadowColour = shadowColour
		this.font = font
		this.text = string
		this.horizontalAlignment = loadingScreenAlignmentX
		this.verticalSpacing = verticalSpacing
		this.width = width
		this.x = x
		this.verticalAlignment = loadingScreenAlignmentY
		this.textVerticalAlignment = textVerticalAlignment
		this.height = height
		this.y = y
	}

	@OriginalMember(owner = "client!ot", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_7
	}

	companion object {
		@OriginalMember(owner = "client!o", name = "a", descriptor = "(ILclient!cea;)Lclient!ot;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): TextInstance? {
			@Pc(7) val string: String? = packet!!.readString()
			@Pc(16) val loadingScreenAlignmentX: LoadingScreenAlignmentX? = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()[packet.readUByte()]
			@Pc(30) val loadingScreenAlignmentY: LoadingScreenAlignmentY? = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()[packet.readUByte()]
			@Pc(34) val offsetX: Int = packet.readShort()
			@Pc(38) val offsetY: Int = packet.readShort()
			@Pc(42) val textAlignment: Int = packet.readUByte()
			@Pc(46) val textVerticalAlignment: Int = packet.readUByte()
			@Pc(50) val lineSpacing: Int = packet.readUByte()
			@Pc(56) val width: Int = packet.readUShort()
			@Pc(60) val height: Int = packet.readUShort()
			@Pc(64) val fontId: Int = packet.readInt()
			@Pc(68) val textColor: Int = packet.readInt()
			@Pc(72) val shadowColor: Int = packet.readInt()
			return TextInstance(string, loadingScreenAlignmentX, loadingScreenAlignmentY, offsetX, offsetY, textAlignment, textVerticalAlignment, lineSpacing, width, height, fontId, textColor, shadowColor)
		}
	}
}
