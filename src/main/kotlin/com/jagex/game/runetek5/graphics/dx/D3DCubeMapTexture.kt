package com.jagex.game.runetek5.graphics.dx

import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.D3DTexture
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.CubeTextureSampler
import jagdx.IDirect3DBaseTexture
import jagdx.IDirect3DCubeTexture
import jagdx.PixelBuffer
import jagdx.ue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.opengl.GLFormat

@OriginalClass("client!gm")
class D3DCubeMapTexture : D3DTexture, CubeTextureSampler {

	@OriginalMember(owner = "client!gm", name = "f", descriptor = "I")
	private val size: Int

	@OriginalMember(owner = "client!gm", name = "g", descriptor = "Lclient!jagdx/IDirect3DCubeTexture;")
	private val texture: IDirect3DCubeTexture?

	@OriginalMember(owner = "client!gm", name = "<init>", descriptor = "(Lclient!bfa;IZ[[I)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) size: Int, @OriginalArg(2) mipmapped: Boolean, @OriginalArg(3) pixels: Array<IntArray?>?) : super(d3DToolkit, GLFormat.HDR, GLFormat.RGBA, mipmapped && d3DToolkit!!.mipmappedCubeMapSupported, size * 6 * size) {
		this.size = size
		if (this.mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.size, 0, 1024, 21, 1)
		} else {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.size, 1, 0, 21, 1)
		}
		@Pc(52) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(54) var i: Int = 0
		while (i < 6) {
			@Pc(73) val result: Int = this.texture!!.LockRect(i, 0, 0, 0, this.size, this.size, 0, pixelBuffer)
			if (ue.a(result, false)) {
				@Pc(81) val rowPitch: Int = pixelBuffer!!.getRowPitch()
				if (rowPitch == this.size * 4) {
					pixelBuffer.a(pixels!![i] as IntArray?, 0, 0, this.size * this.size)
				} else {
					@Pc(102) var j: Int = 0
					while (j < this.size) {
						pixelBuffer.a(pixels!![i], this.size * j, j * rowPitch, this.size)
						j++
					}
				}
				this.texture.UnlockRect(i, 0)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!gm", name = "a", descriptor = "(B)V")
	override fun bindTexture() {
		this.toolkit.bindTexture(this)
	}

	@OriginalMember(owner = "client!gm", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	override fun getTexture(): IDirect3DBaseTexture? {
		return this.texture
	}

	@OriginalMember(owner = "client!gm", name = "a", descriptor = "(Lclient!ria;I)V")
	override fun setFilterMode(@OriginalArg(0) filter: FilterMode?) {
		super.setFilterMode(filter)
	}
}
