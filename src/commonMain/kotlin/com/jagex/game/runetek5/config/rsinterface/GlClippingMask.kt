package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ag")
class GlClippingMask : ComponentSurface {

	@OriginalMember(owner = "client!ag", name = "f", descriptor = "Lclient!eba;")
	@JvmField
	val surface: OpenGl2DImageTexture?

	@OriginalMember(owner = "client!ag", name = "<init>", descriptor = "(Lclient!qo;II[B)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) maskData: ByteArray?) : super() {
		this.surface = OpenGl2DImageTexture.createSurface(maskData, height, glToolkit, width)
		this.surface!!.setRepeat(false, false)
	}

	companion object {
		@OriginalMember(owner = "client!kg", name = "a", descriptor = "(I[I[IILclient!qo;I)Lclient!ag;")
		@JvmStatic
		fun create(@OriginalArg(0) width: Int, @OriginalArg(1) rowOffsets: IntArray?, @OriginalArg(2) rowWidths: IntArray?, @OriginalArg(4) glToolkit: GlToolkit?, @OriginalArg(5) height: Int): GlClippingMask {
			@Pc(10) val maskData: ByteArray = ByteArray(width * height)
			@Pc(12) var i: Int = 0
			while (i < height) {
				@Pc(23) var offset: Int = width * i + rowOffsets!![i]
				@Pc(25) var j: Int = 0
				while (j < rowWidths!![i]) {
					maskData[offset++] = -1
					j++
				}
				i++
			}
			return GlClippingMask(glToolkit, width, height, maskData)
		}
	}
}
