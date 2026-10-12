package com.jagex.game.runetek5.toolkit

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.GpuTexture
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!lp")
class MaskedTextureSurface : ComponentSurface {

	@OriginalMember(owner = "client!lp", name = "f", descriptor = "Lclient!wfa;")
	@JvmField
	val nativeTextureSampler: GpuTexture?

	@OriginalMember(owner = "client!lp", name = "<init>", descriptor = "(Lclient!wga;II[B)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) bytes: ByteArray?) : super() {
		this.nativeTextureSampler = nativeToolkit!!.createTextureSampler(bytes, width, GLActionFormat.PIXEL_FORMAT, false, height)
		this.nativeTextureSampler!!.setWarp(false, false)
	}

	@OriginalMember(owner = "client!lp", name = "<init>", descriptor = "(Lclient!wga;II[I)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) pixels: IntArray?) : super() {
		this.nativeTextureSampler = nativeToolkit!!.createTexture(height, width, false, pixels)
		this.nativeTextureSampler!!.setWarp(false, false)
	}

	companion object {
		@OriginalMember(owner = "client!jda", name = "a", descriptor = "(ILclient!wga;[IB[II)Lclient!lp;")
		@JvmStatic
		fun create(@OriginalArg(0) height: Int, @OriginalArg(1) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(2) rowOffsets: IntArray?, @OriginalArg(4) rowLengths: IntArray?, @OriginalArg(5) width: Int): MaskedTextureSurface {
			@Pc(25) var y: Int
			@Pc(35) var index: Int
			@Pc(37) var x: Int
			if (!nativeToolkit!!.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
				@Pc(71) val pixels: IntArray = IntArray(height * width)
				y = 0
				while (y < height) {
					index = rowOffsets!![y] + y * width
					x = 0
					while (x < rowLengths!![y]) {
						pixels[index++] = -16777216
						x++
					}
					y++
				}
				return MaskedTextureSurface(nativeToolkit, width, height, pixels)
			}
			@Pc(23) val alphaMask: ByteArray = ByteArray(height * width)
			y = 0
			while (y < height) {
				index = rowOffsets!![y] + width * y
				x = 0
				while (x < rowLengths!![y]) {
					alphaMask[index++] = -1
					x++
				}
				y++
			}
			return MaskedTextureSurface(nativeToolkit, width, height, alphaMask)
		}
	}
}
