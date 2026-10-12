package com.jagex.game.runetek5.graphics.font

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import jaclib.memory.Buffer
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.TextureCombineFunction
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.FilterMode

@OriginalClass("client!kr")
class NativeFont : AbstractFontRenderer {

	@OriginalMember(owner = "client!kr", name = "x", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!kr", name = "v", descriptor = "Lclient!wfa;")
	private val glyphAtlasSampler: GpuTexture?

	@OriginalMember(owner = "client!kr", name = "w", descriptor = "Z")
	private val isAlphaOnlyTexture: Boolean

	@OriginalMember(owner = "client!kr", name = "y", descriptor = "Lclient!tc;")
	private val glyphVertexStream: NativeStream?

	@OriginalMember(owner = "client!kr", name = "<init>", descriptor = "(Lclient!wga;Lclient!oea;[Lclient!rh;Z)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) fontMetrics: FontMetrics?, @OriginalArg(2) indexedImages: Array<SpriteType?>?, @OriginalArg(3) isAlphaOnly: Boolean) : super(nativeToolkit, fontMetrics) {
		this.nativeToolkit = nativeToolkit
		@Pc(8) var maxDimension: Int = 0
		for (@Pc(10) i in 0 until 256) {
			@Pc(15) val indexedImage: SpriteType? = indexedImages!![i]
			if (indexedImage!!.height > maxDimension) {
				maxDimension = indexedImage.height
			}
			if (indexedImage.width > maxDimension) {
				maxDimension = indexedImage.width
			}
		}
		@Pc(37) val atlasSize: Int = maxDimension * 16
		@Pc(44) val pixels: ByteArray = ByteArray(atlasSize * atlasSize)
		@Pc(54) var height: Int
		@Pc(63) val local63: Int
		@Pc(75) val local75: Int
		@Pc(77) var width: Int
		@Pc(85) var minX: Int
		for (@Pc(46) j in 0 until 256) {
			@Pc(51) val indexedImage2: SpriteType? = indexedImages!![j]
			height = indexedImage2!!.height
			@Pc(57) val glyphWidth: Int = indexedImage2.width
			val cellX: Int = j % 16 * maxDimension
			@Pc(69) val cellY: Int = j / 16 * maxDimension
			var pixelIndex: Int = cellY * atlasSize + cellX
			width = 0
			@Pc(83) var maskBytes: ByteArray?
			@Pc(88) var x: Int
			if (indexedImage2.alpha == null) {
				maskBytes = indexedImage2.pixels
				minX = 0
				while (minX < height) {
					x = 0
					while (x < glyphWidth) {
						pixels[pixelIndex++] = (if (maskBytes!![width++].toInt() == 0) 0 else -1).toByte()
						x++
					}
					pixelIndex += atlasSize - glyphWidth
					minX++
				}
			} else {
				maskBytes = indexedImage2.alpha
				minX = 0
				while (minX < height) {
					x = 0
					while (x < glyphWidth) {
						pixels[pixelIndex++] = maskBytes!![width++]
						x++
					}
					pixelIndex += atlasSize - glyphWidth
					minX++
				}
			}
		}
		if (nativeToolkit!!.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
			this.glyphAtlasSampler = nativeToolkit.createTextureSampler(pixels, atlasSize, GLActionFormat.PIXEL_FORMAT, false, atlasSize)
		} else {
			@Pc(162) val argbPixels: IntArray = IntArray(pixels.size)
			height = 0
			while (height < pixels.size) {
				argbPixels[height] = pixels[height].toInt() shl 24
				height++
			}
			this.glyphAtlasSampler = nativeToolkit.createTexture(atlasSize, atlasSize, false, argbPixels)!!
		}
		this.isAlphaOnlyTexture = true
		this.glyphAtlasSampler!!.setFilterMode(FilterMode.aClass298_10)
		this.glyphVertexStream = nativeToolkit.createStream(false)
		this.glyphVertexStream!!.create(20480, 20)
		for (@Pc(366) k in 0 until 4) {
			@Pc(373) val buffer: Buffer? = this.glyphVertexStream!!.lock()
			if (buffer != null) {
				@Pc(381) val stream = this.nativeToolkit!!.wrapBuffer(buffer)
				@Pc(391) val sScale: Float = this.glyphAtlasSampler!!.toTextureCoordS(atlasSize.toFloat()) / atlasSize.toFloat()
				@Pc(401) val tScale: Float = this.glyphAtlasSampler!!.toTextureCoordT(atlasSize.toFloat()) / atlasSize.toFloat()
				for (glyphIndex in 0 until 256) {
					@Pc(408) val indexedImage3: SpriteType? = indexedImages!![glyphIndex]
					val glyphHeight: Int = indexedImage3!!.height
					width = indexedImage3.width
					@Pc(417) val minY: Int = indexedImage3.minY
					minX = indexedImage3.minX
					@Pc(427) val cellX: Float = (glyphIndex % 16 * maxDimension).toFloat()
					@Pc(434) val cellY: Float = (glyphIndex / 16 * maxDimension).toFloat()
					@Pc(438) val s0: Float = cellX * sScale
					@Pc(442) val t0: Float = cellY * tScale
					@Pc(449) val s1: Float = (cellX + width.toFloat()) * sScale
					@Pc(456) val t1: Float = (cellY + glyphHeight.toFloat()) * tScale
					if (Stream.c()) {
						stream.a(minX.toFloat())
						stream.a(minY.toFloat())
						stream.a(0.0f)
						stream.a(s0)
						stream.a(t0)
						stream.a(minX.toFloat())
						stream.a((minY + glyphHeight).toFloat())
						stream.a(0.0f)
						stream.a(s0)
						stream.a(t1)
						stream.a((minX + width).toFloat())
						stream.a((minY + glyphHeight).toFloat())
						stream.a(0.0f)
						stream.a(s1)
						stream.a(t1)
						stream.a((minX + width).toFloat())
						stream.a(minY.toFloat())
						stream.a(0.0f)
						stream.a(s1)
						stream.a(t0)
					} else {
						stream.b(minX.toFloat())
						stream.b(minY.toFloat())
						stream.b(0.0f)
						stream.b(s0)
						stream.b(t0)
						stream.b(minX.toFloat())
						stream.b((minY + glyphHeight).toFloat())
						stream.b(0.0f)
						stream.b(s0)
						stream.b(t1)
						stream.b((minX + width).toFloat())
						stream.b((minY + glyphHeight).toFloat())
						stream.b(0.0f)
						stream.b(s1)
						stream.b(t1)
						stream.b((minX + width).toFloat())
						stream.b(minY.toFloat())
						stream.b(0.0f)
						stream.b(s1)
						stream.b(t0)
					}
				}
				stream.a()
				if (this.glyphVertexStream!!.unlock()) {
					return
				}
			}
		}
	}

	@OriginalMember(owner = "client!kr", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	override fun renderSymbol(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean, @OriginalArg(5) mask: ComponentSurface?, @OriginalArg(6) offsetX: Int, @OriginalArg(7) offsetY: Int) {
	}

	@OriginalMember(owner = "client!kr", name = "fa", descriptor = "(CIIIZ)V")
	override fun fa(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean) {
		this.nativeToolkit!!.setupTexturedUiRenderState()
		this.nativeToolkit.setTextureSampler(this.glyphAtlasSampler)
		if (this.isAlphaOnlyTexture || shadow) {
			this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4)
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
			this.nativeToolkit.setColourValue(colour)
		} else {
			this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4)
		}
		@Pc(43) val modelMatrix = this.nativeToolkit.getModelMatrix()
		modelMatrix.applyTranslation(x, y, 0)
		this.nativeToolkit.commitModelMatrix()
		this.nativeToolkit.setStreamSource(this.glyphVertexStream, 0)
		this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionTexCoordDeclaration)
		this.nativeToolkit.drawPrimitive(2, c.code * 4, PrimitiveType.aClass134_3)
		if (this.isAlphaOnlyTexture || shadow) {
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		}
	}
}
