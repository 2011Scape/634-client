package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import kotlin.jvm.JvmStatic

@OriginalClass("client!o")
class LineRasterizer @OriginalMember(owner = "client!o", name = "<init>", descriptor = "(IIIIII)V") constructor(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) strokeColor: Int, @OriginalArg(5) lineWidth: Int) : TextureShapeRasterizer(-1, strokeColor, lineWidth) {

	@OriginalMember(owner = "client!o", name = "i", descriptor = "I")
	private val endY: Int

	@OriginalMember(owner = "client!o", name = "o", descriptor = "I")
	private val endX: Int

	@OriginalMember(owner = "client!o", name = "f", descriptor = "I")
	private val startX: Int

	@OriginalMember(owner = "client!o", name = "j", descriptor = "I")
	private val startY: Int

	init {
		this.endY = endY
		this.endX = endX
		this.startX = startX
		this.startY = startY
	}

	@OriginalMember(owner = "client!o", name = "a", descriptor = "(III)V")
	override fun drawFilledAndStroked(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int) {
	}

	@OriginalMember(owner = "client!o", name = "c", descriptor = "(III)V")
	override fun drawStroked(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
		@Pc(10) val x0: Int = (width * this.startX) shr 12
		@Pc(23) val x1: Int = (width * this.endX) shr 12
		@Pc(30) val y0: Int = (this.startY * height) shr 12
		@Pc(37) val y1: Int = (this.endY * height) shr 12
		TextureShapeRasterizer.drawLine(x0, this.strokeColor, y0, y1, x1)
	}

	@OriginalMember(owner = "client!o", name = "b", descriptor = "(III)V")
	override fun drawFilled(@OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
	}

	companion object {
		@OriginalMember(owner = "client!qca", name = "a", descriptor = "(ILclient!cea;)Lclient!o;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): LineRasterizer {
			return LineRasterizer(packet!!.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUByte())
		}
	}
}
