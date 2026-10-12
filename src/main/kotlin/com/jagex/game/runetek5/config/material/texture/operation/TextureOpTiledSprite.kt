package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!cl")
class TextureOpTiledSprite : TextureOpSprite() {

	@OriginalMember(owner = "client!cl", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty && this.loadSprite()) {
			@Pc(28) val outputRed: IntArray = output[0]!!
			@Pc(32) val outputGreen: IntArray = output[1]!!
			@Pc(36) val outputBlue: IntArray = output[2]!!
			@Pc(44) val rowOffset: Int = this.spriteHeight * (count % this.spriteHeight)
			@Pc(46) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(58) val pixel: Int = this.spritePixels!![i % this.spriteWidth + rowOffset]
				outputBlue[i] = (pixel and 0xFF) shl 4
				outputGreen[i] = (pixel shr 4) and 0xFF0
				outputRed[i] = (pixel shr 12) and 0xFF0
				i++
			}
		}
		return output
	}
}
