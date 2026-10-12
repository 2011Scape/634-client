package com.jagex.game.runetek5.graphics.dx

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.D3DTexture
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.D3DEnumTranslator
import com.jagex.game.runetek5.graphics.GpuTexture
import jagdx.IDirect3DBaseTexture
import jagdx.IDirect3DTexture
import jagdx.PixelBuffer
import jagdx.ue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!kt")
class D3DTexture2D : D3DTexture, GpuTexture {

	@OriginalMember(owner = "client!kt", name = "i", descriptor = "Z")
	@JvmField
	var repeatU: Boolean = false

	@OriginalMember(owner = "client!kt", name = "j", descriptor = "Z")
	@JvmField
	var repeatV: Boolean = false

	@OriginalMember(owner = "client!kt", name = "h", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!kt", name = "f", descriptor = "I")
	private val height: Int

	@OriginalMember(owner = "client!kt", name = "g", descriptor = "Lclient!jagdx/IDirect3DTexture;")
	private val texture: IDirect3DTexture?

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;IIZ[III)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) mipmapped: Boolean, @OriginalArg(4) pixels: IntArray?, @OriginalArg(5) offset: Int, @OriginalArg(6) rowLength: Int) : super(d3DToolkit, GLFormat.HDR, GLFormat.RGBA, mipmapped && d3DToolkit!!.mipmapSupported, height * width) {
		var rowLength: Int = rowLength
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.height = height
			this.width = width
		} else {
			this.width = Utils.nextPowerOfTwo(width)
			this.height = Utils.nextPowerOfTwo(height)
		}
		if (mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.width, this.height, 0, 1024, 21, 1)
		} else {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.width, this.height, 1, 0, 21, 1)
		}
		@Pc(71) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(82) val result: Int = this.texture!!.LockRect(0, 0, 0, width, height, 0, pixelBuffer)
		if (ue.a(result, false)) {
			if (rowLength == 0) {
				rowLength = width
			}
			@Pc(95) val rowPitch: Int = pixelBuffer!!.getRowPitch()
			if (rowPitch == width * 4 && width == rowLength) {
				pixelBuffer.a(pixels as IntArray?, offset, 0, width * height)
			} else {
				@Pc(114) var i: Int = 0
				while (i < height) {
					pixelBuffer.a(pixels, i * rowLength + offset, rowPitch * i, width)
					i++
				}
			}
			this.texture.UnlockRect(0)
		}
	}

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;Lclient!baa;II)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) format: PixelFormat?, @OriginalArg(2) type: DataType?, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int) : super(d3DToolkit, format, type, false, height * width) {
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.width = width
			this.height = height
		} else {
			this.width = Utils.nextPowerOfTwo(width)
			this.height = Utils.nextPowerOfTwo(height)
		}
		this.texture = this.toolkit.iDirect3DDevice!!.a(width, height, 1, 0, D3DEnumTranslator.toD3DFormat(this.type, this.format), 1)
	}

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;IIZ[BII)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) format: PixelFormat?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) mipmapped: Boolean, @OriginalArg(5) data: ByteArray?, @OriginalArg(6) offset: Int, @OriginalArg(7) rowLength: Int) : super(d3DToolkit, format, GLFormat.RGBA, mipmapped && d3DToolkit!!.mipmapSupported, width * height) {
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.width = width
			this.height = height
		} else {
			this.width = Utils.nextPowerOfTwo(width)
			this.height = Utils.nextPowerOfTwo(height)
		}
		if (mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.width, this.height, 0, 1024, D3DEnumTranslator.toD3DFormat(GLFormat.RGBA, this.format), 1)
		} else {
			this.texture = this.toolkit.iDirect3DDevice!!.a(this.width, this.height, 1, 0, D3DEnumTranslator.toD3DFormat(GLFormat.RGBA, this.format), 1)
		}
		@Pc(79) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(90) val result: Int = this.texture!!.LockRect(0, 0, 0, width, height, 0, pixelBuffer)
		if (ue.a(result, false)) {
			@Pc(105) val rowBytes: Int = width * this.format.componentCount
			@Pc(111) val srcRowBytes: Int = width * this.format.componentCount
			@Pc(114) val rowPitch: Int = pixelBuffer!!.getRowPitch()
			if (rowPitch == rowBytes && srcRowBytes == rowBytes) {
				pixelBuffer.a(data as ByteArray?, offset, 0, rowBytes * height)
			} else {
				@Pc(130) var i: Int = 0
				while (i < height) {
					pixelBuffer.a(data, i * srcRowBytes + offset, rowPitch * i, rowBytes)
					i++
				}
			}
			this.texture.UnlockRect(0)
		}
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(Lclient!ria;I)V")
	override fun setFilterMode(@OriginalArg(0) filter: FilterMode?) {
		super.setFilterMode(filter)
	}

	@OriginalMember(owner = "client!kt", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	override fun getTexture(): IDirect3DBaseTexture? {
		return this.texture
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(Z)I")
	override fun getTextureWidth(): Int {
		return this.width
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(I)I")
	override fun getTextureHeight(): Int {
		return this.height
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(I[IIIIBI)V")
	override fun readPixels(@OriginalArg(1) dest: IntArray?, @OriginalArg(2) width: Int, @OriginalArg(4) height: Int) {
		if (GLFormat.HDR !== this.format || this.type !== GLFormat.RGBA) {
			throw RuntimeException()
		}
		@Pc(22) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(33) val result: Int = this.texture!!.LockRect(0, 0, 0, width, height, 16, pixelBuffer)
		if (!ue.a(result, false)) {
			return
		}
		@Pc(41) val rowPitch: Int = pixelBuffer!!.getRowPitch()
		if (rowPitch == width * 4) {
			pixelBuffer.b(dest, 0, 0, dest!!.size)
		} else {
			@Pc(48) var i: Int = 0
			while (i < height) {
				pixelBuffer.b(dest, width * i, i * rowPitch, width)
				i++
			}
		}
		this.texture.UnlockRect(0)
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(BF)F")
	override fun toTextureCoordT(@OriginalArg(1) y: Float): Float {
		return y / this.height.toFloat()
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(II[IIIIII)V")
	override fun updateTextureRegion(@OriginalArg(0) rowLength: Int, @OriginalArg(2) pixels: IntArray?, @OriginalArg(3) x: Int, @OriginalArg(5) width: Int, @OriginalArg(6) y: Int, @OriginalArg(7) height: Int) {
		if (this.format !== GLFormat.HDR || this.type !== GLFormat.RGBA) {
			throw RuntimeException()
		}
		@Pc(16) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(27) val result: Int = this.texture!!.LockRect(0, x, y, width, height, 0, pixelBuffer)
		if (!ue.a(result, false)) {
			return
		}
		@Pc(35) val rowPitch: Int = pixelBuffer!!.getRowPitch()
		if (width * 4 == rowPitch && rowLength == width) {
			pixelBuffer.a(pixels as IntArray?, 0, 0, height * width)
		} else {
			@Pc(53) var i: Int = 0
			while (i < height) {
				pixelBuffer.a(pixels, rowLength * i, i * rowPitch, width)
				i++
			}
		}
		this.texture.UnlockRect(0)
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(FI)F")
	override fun toTextureCoordS(@OriginalArg(0) x: Float): Float {
		return x / this.width.toFloat()
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(B)V")
	override fun bindTexture() {
		this.toolkit.bindTexture2D(this)
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	override fun upload(@OriginalArg(3) rowLength: Int, @OriginalArg(4) height: Int, @OriginalArg(5) width: Int, @OriginalArg(7) data: ByteArray?, @OriginalArg(8) format: PixelFormat?) {
		if (this.format !== format || this.type !== GLFormat.RGBA) {
			throw RuntimeException()
		}
		@Pc(20) val pixelBuffer: PixelBuffer? = this.toolkit.pixelBuffer
		@Pc(31) val result: Int = this.texture!!.LockRect(0, 0, 0, width, height, 0, pixelBuffer)
		if (!ue.a(result, false)) {
			return
		}
		@Pc(42) val srcRowBytes: Int = rowLength * this.format.componentCount
		@Pc(48) val rowBytes: Int = width * this.format.componentCount
		@Pc(51) val rowPitch: Int = pixelBuffer!!.getRowPitch()
		if (rowBytes == rowPitch && srcRowBytes == rowBytes) {
			pixelBuffer.a(data as ByteArray?, 0, 0, rowBytes * height)
		} else {
			@Pc(72) var i: Int = 0
			while (i < height) {
				pixelBuffer.a(data, i * srcRowBytes, i * rowPitch, rowBytes)
				i++
			}
		}
		this.texture.UnlockRect(0)
	}

	@OriginalMember(owner = "client!kt", name = "b", descriptor = "(I)Z")
	override fun supportsTextureRepeat(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(ZZI)V")
	override fun setWarp(@OriginalArg(0) repeatU: Boolean, @OriginalArg(1) repeatV: Boolean) {
		this.repeatV = repeatV
		this.repeatU = repeatU
	}
}
