/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlClippingMask (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdClippingMask : ComponentSurface {

	@JvmField
	val surface: Hd2DImageTexture?

	constructor(glToolkit: HdToolkit?, width: Int, height: Int, maskData: ByteArray?) : super() {
		this.surface = Hd2DImageTexture.createSurface(maskData, height, glToolkit, width)
		this.surface!!.setRepeat(false, false)
	}

	companion object {
		@JvmStatic
		fun create(width: Int, rowOffsets: IntArray?, rowWidths: IntArray?, glToolkit: HdToolkit?, height: Int): HdClippingMask {
			val maskData: ByteArray = ByteArray(width * height)
			var i: Int = 0
			while (i < height) {
				var offset: Int = width * i + rowOffsets!![i]
				var j: Int = 0
				while (j < rowWidths!![i]) {
					maskData[offset++] = -1
					j++
				}
				i++
			}
			return HdClippingMask(glToolkit, width, height, maskData)
		}
	}
}
