package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object TextureRaster {
	@OriginalMember(owner = "client!tl", name = "u", descriptor = "[I")
	@JvmField
	var verticalTable: IntArray? = null

	@OriginalMember(owner = "client!tha", name = "q", descriptor = "I")
	@JvmField
	var scaledWidth: Int = 0

	@OriginalMember(owner = "client!bv", name = "s", descriptor = "[I")
	@JvmField
	var PALETTE_SIN: IntArray? = null

	@OriginalMember(owner = "client!nl", name = "c", descriptor = "[I")
	@JvmField
	var PALETTE_COS: IntArray? = null

	@OriginalMember(owner = "client!sca", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun calculateTrigTables() {
		if (TextureRaster.PALETTE_SIN != null && TextureRaster.PALETTE_COS != null) {
			return
		}
		TextureRaster.PALETTE_COS = IntArray(256)
		TextureRaster.PALETTE_SIN = IntArray(256)
		@Pc(13) var i: Int = 0
		while (i < 256) {
			@Pc(22) val angle: Double = i.toDouble() / 255.0 * 6.283185307179586
			TextureRaster.PALETTE_SIN!![i] = (Math.sin(angle) * 4096.0).toInt()
			TextureRaster.PALETTE_COS!![i] = (Math.cos(angle) * 4096.0).toInt()
			i++
		}
	}

	@OriginalMember(owner = "client!wca", name = "u", descriptor = "I")
	@JvmField
	var widthMask: Int = 0

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(IIB)V")
	@JvmStatic
	fun setDimensions(@OriginalArg(0) textureOpHeight: Int, @OriginalArg(1) textureOpWidth: Int) {
		@Pc(17) var idx: Int
		if (textureOpWidth != EnvironmentLight.textureOpWidth) {
			EnvironmentLight.horizontalTable = IntArray(textureOpWidth)
			idx = 0
			while (idx < textureOpWidth) {
				EnvironmentLight.horizontalTable!![idx] = (idx shl 12) / textureOpWidth
				idx++
			}
			TextureRaster.widthMask = textureOpWidth - 1
			EnvironmentLight.textureOpWidth = textureOpWidth
			TextureRaster.scaledWidth = textureOpWidth * 32
		}
		if (textureOpHeight == TextureOperation.currentHeight) {
			return
		}
		if (EnvironmentLight.textureOpWidth == textureOpHeight) {
			TextureRaster.verticalTable = EnvironmentLight.horizontalTable
		} else {
			TextureRaster.verticalTable = IntArray(textureOpHeight)
			idx = 0
			while (idx < textureOpHeight) {
				TextureRaster.verticalTable!![idx] = (idx shl 12) / textureOpHeight
				idx++
			}
		}
		TextureOperation.currentHeight = textureOpHeight
		TextureOperation.columnEnd = textureOpHeight - 1
	}
}
