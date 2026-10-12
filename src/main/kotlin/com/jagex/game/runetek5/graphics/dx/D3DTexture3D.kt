package com.jagex.game.runetek5.graphics.dx

import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.D3DTexture
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.D3DEnumTranslator
import jagdx.IDirect3DBaseTexture
import jagdx.IDirect3DVolumeTexture
import jagdx.PixelBuffer
import jagdx.ue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.opengl.GLFormat

@OriginalClass("client!nc")
class D3DTexture3D : D3DTexture, Texture3DSampler {

	@OriginalMember(owner = "client!nc", name = "h", descriptor = "I")
	private val depth: Int

	@OriginalMember(owner = "client!nc", name = "g", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!nc", name = "i", descriptor = "I")
	private val height: Int

	@OriginalMember(owner = "client!nc", name = "f", descriptor = "Lclient!jagdx/IDirect3DVolumeTexture;")
	private val texture: IDirect3DVolumeTexture?

	@OriginalMember(owner = "client!nc", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;III[B)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) format: PixelFormat?, @OriginalArg(2) depth: Int, @OriginalArg(3) height: Int, @OriginalArg(4) width: Int, @OriginalArg(5) data: ByteArray?) : super(d3DToolkit, format, GLFormat.RGBA, false, width * height * depth) {
		this.depth = depth
		this.width = width
		this.height = height
		this.texture = this.toolkit.iDirect3DDevice!!.a(depth, height, width, 1, 0, D3DEnumTranslator.toD3DFormat(this.type, format), 1)
		@Pc(40) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(53) val result: Int = this.texture!!.LockBox(0, 0, 0, 0, depth, height, width, 0, pixelBuffer)
		if (ue.a(result, false)) {
			@Pc(64) val rowBytes: Int = this.format.componentCount * this.depth
			@Pc(69) val sliceBytes: Int = rowBytes * this.height
			@Pc(72) val slicePitch: Int = pixelBuffer!!.getSlicePitch()
			if (slicePitch == sliceBytes) {
				pixelBuffer.a(data as ByteArray?, 0, 0, this.width * rowBytes * this.height)
			} else {
				@Pc(91) val rowPitch: Int = pixelBuffer.getRowPitch()
				@Pc(96) var slice: Int
				if (rowBytes == rowPitch) {
					slice = 0
					while (slice < this.width) {
						pixelBuffer.a(data, sliceBytes * slice, slice * slicePitch, sliceBytes)
						slice++
					}
				} else {
					slice = 0
					while (slice < this.width) {
						@Pc(102) var i: Int = 0
						while (i < this.height) {
							pixelBuffer.a(data, rowBytes * i + sliceBytes * slice, i * rowPitch + slice * slicePitch, rowBytes)
							i++
						}
						slice++
					}
				}
			}
			this.texture.UnlockBox(0)
		}
	}

	@OriginalMember(owner = "client!nc", name = "a", descriptor = "(B)V")
	override fun bindTexture() {
		this.toolkit.bindTexture3D(this)
	}

	@OriginalMember(owner = "client!nc", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	override fun getTexture(): IDirect3DBaseTexture? {
		return this.texture
	}

	@OriginalMember(owner = "client!nc", name = "a", descriptor = "(Lclient!ria;I)V")
	override fun setFilterMode(@OriginalArg(0) filter: FilterMode?) {
		super.setFilterMode(filter)
	}
}
