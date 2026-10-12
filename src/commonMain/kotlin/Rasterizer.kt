import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ColorUtil
import kotlin.jvm.JvmField

@OriginalClass("client!mp")
class Rasterizer {

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "I")
	@JvmField var minX: Int = 0

	@OriginalMember(owner = "client!mp", name = "d", descriptor = "I")
	@JvmField var minY: Int = 0

	@OriginalMember(owner = "client!mp", name = "h", descriptor = "I")
	@JvmField var height: Int = 0

	@OriginalMember(owner = "client!mp", name = "o", descriptor = "I")
	@JvmField var width: Int = 0

	@OriginalMember(owner = "client!mp", name = "F", descriptor = "I")
	private var fogColour: Int = 0

	@OriginalMember(owner = "client!mp", name = "c", descriptor = "Z")
	@JvmField var halfBlend: Boolean = false

	@OriginalMember(owner = "client!mp", name = "j", descriptor = "I")
	@JvmField var alpha: Int = 0

	@OriginalMember(owner = "client!mp", name = "k", descriptor = "Z")
	private var wireframe: Boolean = false

	@OriginalMember(owner = "client!mp", name = "l", descriptor = "[I")
	@JvmField val lineOffsets: IntArray = IntArray(4096)

	@OriginalMember(owner = "client!mp", name = "e", descriptor = "Z")
	@JvmField var fastScanline: Boolean = true

	@OriginalMember(owner = "client!mp", name = "g", descriptor = "Z")
	@JvmField var clamp: Boolean = false

	@OriginalMember(owner = "client!mp", name = "p", descriptor = "Z")
	private val depthDisabled: Boolean = false

	@OriginalMember(owner = "client!mp", name = "r", descriptor = "F")
	private var textureScale3: Float = 0.0f

	@OriginalMember(owner = "client!mp", name = "u", descriptor = "[I")
	private var texels2: IntArray? = null

	@OriginalMember(owner = "client!mp", name = "w", descriptor = "I")
	private val textureId3: Int = -1

	@OriginalMember(owner = "client!mp", name = "t", descriptor = "F")
	private var textureScale2: Float = 0.0f

	@OriginalMember(owner = "client!mp", name = "C", descriptor = "I")
	private var textureSize: Int = 0

	@OriginalMember(owner = "client!mp", name = "A", descriptor = "I")
	private var textureSize2: Int = 0

	@OriginalMember(owner = "client!mp", name = "B", descriptor = "[I")
	private var texels3: IntArray? = null

	@OriginalMember(owner = "client!mp", name = "y", descriptor = "I")
	private val textureId2: Int = -1

	@OriginalMember(owner = "client!mp", name = "s", descriptor = "I")
	private var textureBlendMode: Int = 0

	@OriginalMember(owner = "client!mp", name = "q", descriptor = "F")
	private var textureScale: Float = 0.0f

	@OriginalMember(owner = "client!mp", name = "D", descriptor = "Z")
	private var textureRepeats: Boolean = true

	@OriginalMember(owner = "client!mp", name = "x", descriptor = "I")
	private var textureMask3: Int = 0

	@OriginalMember(owner = "client!mp", name = "E", descriptor = "I")
	private var textureSize3: Int = 0

	@OriginalMember(owner = "client!mp", name = "v", descriptor = "I")
	private var textureMask2: Int = 0

	@OriginalMember(owner = "client!mp", name = "z", descriptor = "I")
	private var textureMask: Int = 0

	@OriginalMember(owner = "client!mp", name = "G", descriptor = "I")
	private val textureId: Int = -1

	@OriginalMember(owner = "client!mp", name = "H", descriptor = "[I")
	private var texels: IntArray? = null

	@OriginalMember(owner = "client!mp", name = "b", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit?

	@OriginalMember(owner = "client!mp", name = "f", descriptor = "Lclient!pga;")
	private val threadResource: JavaThreadResource?

	@OriginalMember(owner = "client!mp", name = "n", descriptor = "I")
	private val surfaceWidth: Int

	@OriginalMember(owner = "client!mp", name = "i", descriptor = "[I")
	private val raster: IntArray?

	@OriginalMember(owner = "client!mp", name = "m", descriptor = "[F")
	private val depthBuffer: FloatArray?

	@OriginalMember(owner = "client!mp", name = "<init>", descriptor = "(Lclient!ao;Lclient!pga;)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) javaThreadResource: JavaThreadResource?) {
		this.toolkit = javaToolkit
		this.threadResource = javaThreadResource
		this.surfaceWidth = this.toolkit!!.surfaceWidth
		this.raster = this.toolkit!!.surfaceRaster
		this.depthBuffer = this.toolkit!!.depthBuffer
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[IIIIFFFFFFFFFFFFFFFF)V")
	private fun drawTexturedSpanRgb(@OriginalArg(0) dst: IntArray?, @OriginalArg(1) texels: IntArray?, @OriginalArg(2) rowOffset: Int, @OriginalArg(3) startX: Int, @OriginalArg(4) endX: Int, @OriginalArg(5) invZLeft: Float, @OriginalArg(6) invZRight: Float, @OriginalArg(7) uLeft: Float, @OriginalArg(8) uRight: Float, @OriginalArg(9) vLeft: Float, @OriginalArg(10) vRight: Float, @OriginalArg(11) fogLeft: Float, @OriginalArg(12) fogRight: Float, @OriginalArg(13) alphaLeft: Float, @OriginalArg(14) alphaRight: Float, @OriginalArg(15) redLeft: Float, @OriginalArg(16) redRight: Float, @OriginalArg(17) greenLeft: Float, @OriginalArg(18) greenRight: Float, @OriginalArg(19) blueLeft: Float, @OriginalArg(20) blueRight: Float) {
		var startX: Int = startX
		var endX: Int = endX
		var invZLeft: Float = invZLeft
		var uLeft: Float = uLeft
		var vLeft: Float = vLeft
		var fogLeft: Float = fogLeft
		var alphaLeft: Float = alphaLeft
		var redLeft: Float = redLeft
		var greenLeft: Float = greenLeft
		var blueLeft: Float = blueLeft
		@Pc(3) var lengthX: Int = endX - startX
		@Pc(8) val scaleX: Float = 1.0f / lengthX.toFloat()
		@Pc(14) val slopeInvZ: Float = (invZRight - invZLeft) * scaleX
		@Pc(20) val slopeU: Float = (uRight - uLeft) * scaleX
		@Pc(26) val slopeV: Float = (vRight - vLeft) * scaleX
		@Pc(32) val slopeFog: Float = (fogRight - fogLeft) * scaleX
		@Pc(38) val slopeAlpha: Float = (alphaRight - alphaLeft) * scaleX
		@Pc(44) val slopeRed: Float = (redRight - redLeft) * scaleX
		@Pc(50) val slopeGreen: Float = (greenRight - greenLeft) * scaleX
		@Pc(56) val slopeBlue: Float = (blueRight - blueLeft) * scaleX
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width
			}
			if (startX < 0) {
				invZLeft -= slopeInvZ * startX.toFloat()
				uLeft -= slopeU * startX.toFloat()
				vLeft -= slopeV * startX.toFloat()
				fogLeft -= slopeFog * startX.toFloat()
				alphaLeft -= slopeAlpha * startX.toFloat()
				redLeft -= slopeRed * startX.toFloat()
				greenLeft -= slopeGreen * startX.toFloat()
				blueLeft -= slopeBlue * startX.toFloat()
				startX = 0
			}
		}
		if (startX >= endX) {
			return
		}
		lengthX = endX - startX
		@Pc(138) var pixelIndex: Int = rowOffset + startX
		while (lengthX-- > 0) {
			@Pc(143) val depth: Float = 1.0f / invZLeft
			if (depth < this.depthBuffer!![pixelIndex]) {
				@Pc(159) var texU: Int = (uLeft * depth * this.textureSize.toFloat()).toInt()
				if (this.textureRepeats) {
					texU = texU and this.textureMask
				} else if (texU < 0) {
					texU = 0
				} else if (texU > this.textureMask) {
					texU = this.textureMask
				}
				@Pc(189) var texV: Int = (vLeft * depth * this.textureSize.toFloat()).toInt()
				if (this.textureRepeats) {
					texV = texV and this.textureMask
				} else if (texV < 0) {
					texV = 0
				} else if (texV > this.textureMask) {
					texV = this.textureMask
				}
				@Pc(220) val texel: Int = this.texels!![texV * this.textureSize + texU]
				@Pc(232) var texelAlpha: Int
				if (this.textureBlendMode == 2) {
					texelAlpha = (texel shr 24) and 0xFF
				} else if (this.textureBlendMode == 1) {
					texelAlpha = if (texel == 0) 0 else 255
				} else {
					texelAlpha = alphaLeft.toInt()
				}
				if (texelAlpha != 0) {
					@Pc(290) var colour: Int
					@Pc(299) var local299: Int
					@Pc(321) var local321: Int
					if (texelAlpha == 255) {
						colour = ((((redLeft * ((texel shr 16) and 0xFF).toFloat()).toInt() and 0xFF00) or 0xFF0000) shl 8) or ((greenLeft * ((texel shr 8) and 0xFF).toFloat()).toInt() and 0xFF00) or ((blueLeft * (texel and 0xFF).toFloat()).toInt() shr 8)
						if (fogLeft != 0.0f) {
							val fogInverse: Int = (255.0f - fogLeft).toInt()
							val fogTerm: Int = ((((this.fogColour and 0xFF00FF) * fogLeft.toInt()) and 0xFF00FF00.toInt()) or (((this.fogColour and 0xFF00) * fogLeft.toInt()) and 0xFF0000)) ushr 8
							colour = (((((colour and 0xFF00FF) * fogInverse) and 0xFF00FF00.toInt()) or (((colour and 0xFF00) * fogInverse) and 0xFF0000)) ushr 8) + fogTerm
						}
						dst!![pixelIndex] = colour
						this.depthBuffer!![pixelIndex] = depth
					} else {
						colour = ((((redLeft * ((texel shr 16) and 0xFF).toFloat()).toInt() and 0xFF00) or 0xFF0000) shl 8) or ((greenLeft * ((texel shr 8) and 0xFF).toFloat()).toInt() and 0xFF00) or ((blueLeft * (texel and 0xFF).toFloat()).toInt() shr 8)
						if (fogLeft != 0.0f) {
							val fogInverse: Int = (255.0f - fogLeft).toInt()
							val fogTerm: Int = ((((this.fogColour and 0xFF00FF) * fogLeft.toInt()) and 0xFF00FF00.toInt()) or (((this.fogColour and 0xFF00) * fogLeft.toInt()) and 0xFF0000)) ushr 8
							colour = (((((colour and 0xFF00FF) * fogInverse) and 0xFF00FF00.toInt()) or (((colour and 0xFF00) * fogInverse) and 0xFF0000)) ushr 8) + fogTerm
						}
						val dstColour: Int = dst!![pixelIndex]
						val inverseAlpha: Int = 255 - texelAlpha
						colour = ((((dstColour and 0xFF00FF) * inverseAlpha + (colour and 0xFF00FF) * texelAlpha) and 0xFF00FF00.toInt()) + (((dstColour and 0xFF00) * inverseAlpha + (colour and 0xFF00) * texelAlpha) and 0xFF0000)) shr 8
						dst!![pixelIndex] = colour
						this.depthBuffer!![pixelIndex] = depth
					}
				}
			}
			pixelIndex++
			invZLeft += slopeInvZ
			uLeft += slopeU
			vLeft += slopeV
			fogLeft += slopeFog
			alphaLeft += slopeAlpha
			redLeft += slopeRed
			greenLeft += slopeGreen
			blueLeft += slopeBlue
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "()I")
	fun offsetY(): Int {
		return this.lineOffsets[0] / this.surfaceWidth
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFI)V")
	fun renderFlatTriangleRgb(@OriginalArg(0) yA: Float, @OriginalArg(1) yB: Float, @OriginalArg(2) yC: Float, @OriginalArg(3) xA: Float, @OriginalArg(4) xB: Float, @OriginalArg(5) xC: Float, @OriginalArg(6) zA: Float, @OriginalArg(7) zB: Float, @OriginalArg(8) zC: Float, @OriginalArg(9) colour: Int) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		if (this.wireframe) {
			this.toolkit!!.drawOpaqueLine(yA.toInt(), xA.toInt(), xB.toInt(), colour, yB.toInt())
			this.toolkit!!.drawOpaqueLine(yB.toInt(), xB.toInt(), xC.toInt(), colour, yC.toInt())
			this.toolkit!!.drawOpaqueLine(yC.toInt(), xC.toInt(), xA.toInt(), colour, yA.toInt())
			return
		}
		@Pc(46) val slopeXBa: Float = xB - xA
		@Pc(50) val slopeYBa: Float = yB - yA
		@Pc(54) val slopeXCa: Float = xC - xA
		@Pc(58) val slopeYCa: Float = yC - yA
		@Pc(62) val slopeZBa: Float = zB - zA
		@Pc(66) val slopeZCa: Float = zC - zA
		@Pc(68) var scaleXBa: Float = 0.0f
		if (yB != yA) {
			scaleXBa = (xB - xA) / (yB - yA)
		}
		@Pc(82) var scaleXCb: Float = 0.0f
		if (yC != yB) {
			scaleXCb = (xC - xB) / (yC - yB)
		}
		@Pc(96) var scaleXCa: Float = 0.0f
		if (yC != yA) {
			scaleXCa = (xA - xC) / (yA - yC)
		}
		@Pc(116) val determinant: Float = slopeXBa * slopeYCa - slopeXCa * slopeYBa
		if (determinant == 0.0f) {
			return
		}
		@Pc(131) val slopeDepthX: Float = (slopeZBa * slopeYCa - slopeZCa * slopeYBa) / determinant
		@Pc(141) val slopeDepthY: Float = (slopeZCa * slopeXBa - slopeZBa * slopeXCa) / determinant
		if (yA <= yB && yA <= yC) {
			if (!(yA >= this.height.toFloat())) {
				if (yB > this.height.toFloat()) {
					yB = this.height.toFloat()
				}
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				zA = zA + slopeDepthX - slopeDepthX * xA
				if (yB < yC) {
					xC = xA
					if (yA < 0.0f) {
						xC = xA - scaleXCa * yA
						xA -= scaleXBa * yA
						zA -= slopeDepthY * yA
						yA = 0.0f
					}
					if (yB < 0.0f) {
						xB -= scaleXCb * yB
						yB = 0.0f
					}
					if (yA != yB && scaleXCa < scaleXBa || yA == yB && scaleXCa > scaleXCb) {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xC.toInt(), xA.toInt(), zA, slopeDepthX)
							xC += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xC.toInt(), xB.toInt(), zA, slopeDepthX)
							xC += scaleXCa
							xB += scaleXCb
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xA.toInt(), xC.toInt(), zA, slopeDepthX)
							xC += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xB.toInt(), xC.toInt(), zA, slopeDepthX)
							xC += scaleXCa
							xB += scaleXCb
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xB = xA
					if (yA < 0.0f) {
						xB = xA - scaleXCa * yA
						xA -= scaleXBa * yA
						zA -= slopeDepthY * yA
						yA = 0.0f
					}
					if (yC < 0.0f) {
						xC -= scaleXCb * yC
						yC = 0.0f
					}
					if ((yA == yC || !(scaleXCa < scaleXBa)) && (yA != yC || !(scaleXCb > scaleXBa))) {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xA.toInt(), xB.toInt(), zA, slopeDepthX)
							xB += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xA.toInt(), xC.toInt(), zA, slopeDepthX)
							xC += scaleXCb
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xB.toInt(), xA.toInt(), zA, slopeDepthX)
							xB += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yA.toInt(), colour, xC.toInt(), xA.toInt(), zA, slopeDepthX)
							xC += scaleXCb
							xA += scaleXBa
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= this.height.toFloat())) {
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				if (yA > this.height.toFloat()) {
					yA = this.height.toFloat()
				}
				zB = zB + slopeDepthX - slopeDepthX * xB
				if (yC < yA) {
					xA = xB
					if (yB < 0.0f) {
						xA = xB - scaleXBa * yB
						xB -= scaleXCb * yB
						zB -= slopeDepthY * yB
						yB = 0.0f
					}
					if (yC < 0.0f) {
						xC -= scaleXCa * yC
						yC = 0.0f
					}
					if (yB != yC && scaleXBa < scaleXCb || yB == yC && scaleXBa > scaleXCa) {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xA.toInt(), xB.toInt(), zB, slopeDepthX)
							xA += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xA.toInt(), xC.toInt(), zB, slopeDepthX)
							xA += scaleXBa
							xC += scaleXCa
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xB.toInt(), xA.toInt(), zB, slopeDepthX)
							xA += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xC.toInt(), xA.toInt(), zB, slopeDepthX)
							xA += scaleXBa
							xC += scaleXCa
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xC = xB
					if (yB < 0.0f) {
						xC = xB - scaleXBa * yB
						xB -= scaleXCb * yB
						zB -= slopeDepthY * yB
						yB = 0.0f
					}
					if (yA < 0.0f) {
						xA -= scaleXCa * yA
						yA = 0.0f
					}
					if (scaleXBa < scaleXCb) {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xC.toInt(), xB.toInt(), zB, slopeDepthX)
							xC += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xA.toInt(), xB.toInt(), zB, slopeDepthX)
							xA += scaleXCa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xB.toInt(), xC.toInt(), zB, slopeDepthX)
							xC += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, yB.toInt(), colour, xB.toInt(), xA.toInt(), zB, slopeDepthX)
							xA += scaleXCa
							xB += scaleXCb
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (!(yC >= this.height.toFloat())) {
			if (yA > this.height.toFloat()) {
				yA = this.height.toFloat()
			}
			if (yB > this.height.toFloat()) {
				yB = this.height.toFloat()
			}
			zC = zC + slopeDepthX - slopeDepthX * xC
			if (yA < yB) {
				xB = xC
				if (yC < 0.0f) {
					xB = xC - scaleXCb * yC
					xC -= scaleXCa * yC
					zC -= slopeDepthY * yC
					yC = 0.0f
				}
				if (yA < 0.0f) {
					xA -= scaleXBa * yA
					yA = 0.0f
				}
				if (scaleXCb < scaleXCa) {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xB.toInt(), xC.toInt(), zC, slopeDepthX)
						xB += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xB.toInt(), xA.toInt(), zC, slopeDepthX)
						xB += scaleXCb
						xA += scaleXBa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xC.toInt(), xB.toInt(), zC, slopeDepthX)
						xB += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xA.toInt(), xB.toInt(), zC, slopeDepthX)
						xB += scaleXCb
						xA += scaleXBa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xA = xC
				if (yC < 0.0f) {
					xA = xC - scaleXCb * yC
					xC -= scaleXCa * yC
					zC -= slopeDepthY * yC
					yC = 0.0f
				}
				if (yB < 0.0f) {
					xB -= scaleXBa * yB
					yB = 0.0f
				}
				if (scaleXCb < scaleXCa) {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xA.toInt(), xC.toInt(), zC, slopeDepthX)
						xA += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xB.toInt(), xC.toInt(), zC, slopeDepthX)
						xB += scaleXBa
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xC.toInt(), xA.toInt(), zC, slopeDepthX)
						xA += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, yC.toInt(), colour, xC.toInt(), xB.toInt(), zC, slopeDepthX)
						xB += scaleXBa
						xC += scaleXCa
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFFFF)V")
	private fun drawHslSpanRgb(@OriginalArg(0) dst: IntArray?, @OriginalArg(1) depths: FloatArray?, @OriginalArg(2) rowOffset: Int, @OriginalArg(5) startX: Int, @OriginalArg(6) endX: Int, @OriginalArg(7) hsl: Float, @OriginalArg(8) hslStep: Float, @OriginalArg(9) z: Float, @OriginalArg(10) zStep: Float) {
		var rowOffset: Int = rowOffset
		var startX: Int = startX
		var endX: Int = endX
		var hsl: Float = hsl
		var z: Float = z
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width
			}
			if (startX < 0) {
				startX = 0
			}
		}
		if (startX >= endX) {
			return
		}
		rowOffset += startX - 1
		hsl += hslStep * startX.toFloat()
		z += zStep * startX.toFloat()
		@Pc(186) var alpha: Int
		@Pc(191) var invAlpha: Int
		@Pc(233) var background: Int
		@Pc(50) var remaining: Int
		@Pc(64) var colour: Int
		@Pc(71) var index: Int
		@Pc(54) var hslStep4: Float
		@Pc(87) var nextZ: Float
		if (this.threadResource!!.zWrite) {
			if (this.fastScanline) {
				remaining = (endX - startX) shr 2
				hslStep4 = hslStep * 4.0f
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
							hsl += hslStep4
							index = rowOffset + 1
							if (z < depths!![index]) {
								dst!![index] = colour
								depths!![index] = z
							}
							nextZ = z + zStep
							index++
							if (nextZ < depths!![index]) {
								dst!![index] = colour
								depths!![index] = nextZ
							}
							nextZ += zStep
							index++
							if (nextZ < depths!![index]) {
								dst!![index] = colour
								depths!![index] = nextZ
							}
							nextZ += zStep
							rowOffset = index + 1
							if (nextZ < depths!![rowOffset]) {
								dst!![rowOffset] = colour
								depths!![rowOffset] = nextZ
							}
							z = nextZ + zStep
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
						do {
							rowOffset++
							if (z < depths!![rowOffset]) {
								dst!![rowOffset] = colour
								depths!![rowOffset] = z
							}
							z += zStep
							remaining--
						} while (remaining > 0)
						return
					}
				} else {
					alpha = this.alpha
					invAlpha = 256 - this.alpha
					if (remaining > 0) {
						do {
							colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
							hsl += hslStep4
							colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
							index = rowOffset + 1
							if (z < depths!![index]) {
								background = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = z
							}
							nextZ = z + zStep
							index++
							if (nextZ < depths!![index]) {
								background = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = nextZ
							}
							nextZ += zStep
							index++
							if (nextZ < depths!![index]) {
								background = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = nextZ
							}
							nextZ += zStep
							rowOffset = index + 1
							if (nextZ < depths!![rowOffset]) {
								background = dst!![rowOffset]
								dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![rowOffset] = nextZ
							}
							z = nextZ + zStep
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining <= 0) {
						return
					}
					colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
					colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							background = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							depths!![rowOffset] = z
						}
						z += zStep
						remaining--
					} while (remaining > 0)
				}
			} else {
				remaining = endX - startX
				if (this.alpha == 0) {
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset] = ColorUtil.hsvToRgbTable!![hsl.toInt()]
							depths!![rowOffset] = z
						}
						z += zStep
						hsl += hslStep
						remaining--
					} while (remaining > 0)
				} else {
					alpha = this.alpha
					invAlpha = 256 - this.alpha
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
							colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
							background = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							depths!![rowOffset] = z
						}
						hsl += hslStep
						z += zStep
						remaining--
					} while (remaining > 0)
				}
			}
		} else if (this.fastScanline) {
			remaining = (endX - startX) shr 2
			hslStep4 = hslStep * 4.0f
			if (this.alpha == 0) {
				if (remaining > 0) {
					do {
						colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
						hsl += hslStep4
						index = rowOffset + 1
						if (z < depths!![index]) {
							dst!![index] = colour
						}
						nextZ = z + zStep
						index++
						if (nextZ < depths!![index]) {
							dst!![index] = colour
						}
						nextZ += zStep
						index++
						if (nextZ < depths!![index]) {
							dst!![index] = colour
						}
						nextZ += zStep
						rowOffset = index + 1
						if (nextZ < depths!![rowOffset]) {
							dst!![rowOffset] = colour
						}
						z = nextZ + zStep
						remaining--
					} while (remaining > 0)
				}
				remaining = (endX - startX) and 0x3
				if (remaining > 0) {
					colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset] = colour
						}
						z += zStep
						remaining--
					} while (remaining > 0)
					return
				}
			} else {
				alpha = this.alpha
				invAlpha = 256 - this.alpha
				if (remaining > 0) {
					do {
						colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
						hsl += hslStep4
						colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
						index = rowOffset + 1
						if (z < depths!![index]) {
							background = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ = z + zStep
						index++
						if (nextZ < depths!![index]) {
							background = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ += zStep
						index++
						if (nextZ < depths!![index]) {
							background = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ += zStep
						rowOffset = index + 1
						if (nextZ < depths!![rowOffset]) {
							background = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						z = nextZ + zStep
						remaining--
					} while (remaining > 0)
				}
				remaining = (endX - startX) and 0x3
				if (remaining <= 0) {
					return
				}
				colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
				colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						background = dst!![rowOffset]
						dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
					}
					z += zStep
					remaining--
				} while (remaining > 0)
			}
		} else {
			remaining = endX - startX
			if (this.alpha == 0) {
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						dst!![rowOffset] = ColorUtil.hsvToRgbTable!![hsl.toInt()]
					}
					z += zStep
					hsl += hslStep
					remaining--
				} while (remaining > 0)
			} else {
				alpha = this.alpha
				invAlpha = 256 - this.alpha
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						colour = ColorUtil.hsvToRgbTable!![hsl.toInt()]
						colour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
						background = dst!![rowOffset]
						dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
					}
					hsl += hslStep
					z += zStep
					remaining--
				} while (remaining > 0)
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFFFFFIIIIIIIIFIFIF)V")
	fun renderBlendedTexturedTriangle(@OriginalArg(0) yA: Float, @OriginalArg(1) yB: Float, @OriginalArg(2) yC: Float, @OriginalArg(3) xA: Float, @OriginalArg(4) xB: Float, @OriginalArg(5) xC: Float, @OriginalArg(6) zA: Float, @OriginalArg(7) zB: Float, @OriginalArg(8) zC: Float, @OriginalArg(9) uA: Float, @OriginalArg(10) uB: Float, @OriginalArg(11) uC: Float, @OriginalArg(12) vA: Float, @OriginalArg(13) vB: Float, @OriginalArg(14) vC: Float, @OriginalArg(15) colourA: Int, @OriginalArg(16) colourB: Int, @OriginalArg(17) colourC: Int, @OriginalArg(18) fogColour: Int, @OriginalArg(19) fogA: Int, @OriginalArg(20) fogB: Int, @OriginalArg(21) fogC: Int, @OriginalArg(22) texture: Int, @OriginalArg(23) textureScale: Float, @OriginalArg(24) texture2: Int, @OriginalArg(25) textureScale2: Float, @OriginalArg(26) texture3: Int, @OriginalArg(27) textureScale3: Float) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var uA: Float = uA
		var uB: Float = uB
		var uC: Float = uC
		var vA: Float = vA
		var vB: Float = vB
		var vC: Float = vC
		var fogA: Int = fogA
		var fogB: Int = fogB
		var fogC: Int = fogC
		if (texture != this.textureId) {
			this.texels = this.toolkit!!.getTexels(texture)
			if (this.texels == null) {
				this.renderTriangleRgb(yA, yB, yC, xA, xB, xC, zA, zB, zC, ColorUtil.alphaBlend(colourA, fogColour or (fogA shl 24)), ColorUtil.alphaBlend(colourB, fogColour or (fogB shl 24)), ColorUtil.alphaBlend(colourC, fogColour or (fogC shl 24)))
				return
			}
			this.textureSize = if (this.toolkit!!.smallTexture(texture)) 64 else this.toolkit!!.textureSize
			this.textureMask = this.textureSize - 1
			this.textureBlendMode = this.toolkit!!.textureAlphaBlendMode(texture)
		}
		this.textureScale = textureScale
		if (texture2 != this.textureId2) {
			this.texels2 = this.toolkit!!.getTexels(texture2)
			if (this.texels2 == null) {
				this.renderTriangleRgb(yA, yB, yC, xA, xB, xC, zA, zB, zC, ColorUtil.alphaBlend(colourA, fogColour or (fogA shl 24)), ColorUtil.alphaBlend(colourB, fogColour or (fogB shl 24)), ColorUtil.alphaBlend(colourC, fogColour or (fogC shl 24)))
				return
			}
			this.textureSize2 = if (this.toolkit!!.smallTexture(texture2)) 64 else this.toolkit!!.textureSize
			this.textureMask2 = this.textureSize2 - 1
		}
		this.textureScale2 = textureScale2
		if (texture3 != this.textureId3) {
			this.texels3 = this.toolkit!!.getTexels(texture3)
			if (this.texels3 == null) {
				this.renderTriangleRgb(yA, yB, yC, xA, xB, xC, zA, zB, zC, ColorUtil.alphaBlend(colourA, fogColour or (fogA shl 24)), ColorUtil.alphaBlend(colourB, fogColour or (fogB shl 24)), ColorUtil.alphaBlend(colourC, fogColour or (fogC shl 24)))
				return
			}
			this.textureSize3 = if (this.toolkit!!.smallTexture(texture3)) 64 else this.toolkit!!.textureSize
			this.textureMask3 = this.textureSize3 - 1
		}
		this.textureScale3 = textureScale3
		this.fogColour = fogColour
		@Pc(279) var alphaA: Float = ((colourA shr 24) and 0xFF).toFloat()
		@Pc(286) var alphaB: Float = ((colourB shr 24) and 0xFF).toFloat()
		@Pc(293) var alphaC: Float = ((colourC shr 24) and 0xFF).toFloat()
		@Pc(300) var redA: Float = ((colourA shr 16) and 0xFF).toFloat()
		@Pc(307) var redB: Float = ((colourB shr 16) and 0xFF).toFloat()
		@Pc(314) var redC: Float = ((colourC shr 16) and 0xFF).toFloat()
		@Pc(321) var greenA: Float = ((colourA shr 8) and 0xFF).toFloat()
		@Pc(328) var greenB: Float = ((colourB shr 8) and 0xFF).toFloat()
		@Pc(335) var greenC: Float = ((colourC shr 8) and 0xFF).toFloat()
		@Pc(340) var blueA: Float = (colourA and 0xFF).toFloat()
		@Pc(345) var blueB: Float = (colourB and 0xFF).toFloat()
		@Pc(350) var blueC: Float = (colourC and 0xFF).toFloat()
		uA /= zA
		uB /= zB
		uC /= zC
		vA /= zA
		vB /= zB
		vC /= zC
		zA = 1.0f / zA
		zB = 1.0f / zB
		zC = 1.0f / zC
		@Pc(388) var weightA: Float = 1.0f
		@Pc(390) var weightB: Float = 0.0f
		@Pc(392) var weightC: Float = 0.0f
		@Pc(394) var weight2A: Float = 0.0f
		@Pc(396) var weight2B: Float = 1.0f
		@Pc(398) var weight2C: Float = 0.0f
		@Pc(400) var slopeXBa: Float = 0.0f
		@Pc(402) var slopeZBa: Float = 0.0f
		@Pc(404) var slopeUBa: Float = 0.0f
		@Pc(406) var slopeVBa: Float = 0.0f
		@Pc(408) var slopeFogBa: Float = 0.0f
		@Pc(410) var slopeAlphaBa: Float = 0.0f
		@Pc(412) var slopeRedBa: Float = 0.0f
		@Pc(414) var slopeGreenBa: Float = 0.0f
		@Pc(416) var slopeBlueBa: Float = 0.0f
		@Pc(418) var slopeWeightBa: Float = 0.0f
		@Pc(420) var slopeWeight2Ba: Float = 0.0f
		@Pc(428) var local428: Float
		if (yB != yA) {
			val deltaYBa: Float = yB - yA
			slopeXBa = (xB - xA) / deltaYBa
			slopeZBa = (zB - zA) / deltaYBa
			slopeUBa = (uB - uA) / deltaYBa
			slopeVBa = (vB - vA) / deltaYBa
			slopeFogBa = (fogB - fogA).toFloat() / deltaYBa
			slopeAlphaBa = (alphaB - alphaA) / deltaYBa
			slopeRedBa = (redB - redA) / deltaYBa
			slopeGreenBa = (greenB - greenA) / deltaYBa
			slopeBlueBa = (blueB - blueA) / deltaYBa
			slopeWeightBa = (weightB - weightA) / deltaYBa
			slopeWeight2Ba = (weight2B - weight2A) / deltaYBa
		}
		var slopeXCb: Float = 0.0f
		@Pc(499) var slopeZCb: Float = 0.0f
		@Pc(501) var slopeUCb: Float = 0.0f
		@Pc(503) var slopeVCb: Float = 0.0f
		@Pc(505) var slopeFogCb: Float = 0.0f
		@Pc(507) var slopeAlphaCb: Float = 0.0f
		@Pc(509) var slopeRedCb: Float = 0.0f
		@Pc(511) var slopeGreenCb: Float = 0.0f
		@Pc(513) var slopeBlueCb: Float = 0.0f
		@Pc(515) var slopeWeightCb: Float = 0.0f
		@Pc(517) var slopeWeight2Cb: Float = 0.0f
		@Pc(525) var local525: Float
		if (yC != yB) {
			val deltaYCb: Float = yC - yB
			slopeXCb = (xC - xB) / deltaYCb
			slopeZCb = (zC - zB) / deltaYCb
			slopeUCb = (uC - uB) / deltaYCb
			slopeVCb = (vC - vB) / deltaYCb
			slopeFogCb = (fogC - fogB).toFloat() / deltaYCb
			slopeAlphaCb = (alphaC - alphaB) / deltaYCb
			slopeRedCb = (redC - redB) / deltaYCb
			slopeGreenCb = (greenC - greenB) / deltaYCb
			slopeBlueCb = (blueC - blueB) / deltaYCb
			slopeWeightCb = (weightC - weightB) / deltaYCb
			slopeWeight2Cb = (weight2C - weight2B) / deltaYCb
		}
		var slopeXCa: Float = 0.0f
		@Pc(596) var slopeZCa: Float = 0.0f
		@Pc(598) var slopeUCa: Float = 0.0f
		@Pc(600) var slopeVCa: Float = 0.0f
		@Pc(602) var slopeFogCa: Float = 0.0f
		@Pc(604) var slopeAlphaCa: Float = 0.0f
		@Pc(606) var slopeRedCa: Float = 0.0f
		@Pc(608) var slopeGreenCa: Float = 0.0f
		@Pc(610) var slopeBlueCa: Float = 0.0f
		@Pc(612) var slopeWeightCa: Float = 0.0f
		@Pc(614) var slopeWeight2Ca: Float = 0.0f
		if (yA != yC) {
			@Pc(622) val deltaYAc: Float = yA - yC
			slopeXCa = (xA - xC) / deltaYAc
			slopeZCa = (zA - zC) / deltaYAc
			slopeUCa = (uA - uC) / deltaYAc
			slopeVCa = (vA - vC) / deltaYAc
			slopeFogCa = (fogA - fogC).toFloat() / deltaYAc
			slopeAlphaCa = (alphaA - alphaC) / deltaYAc
			slopeRedCa = (redA - redC) / deltaYAc
			slopeGreenCa = (greenA - greenC) / deltaYAc
			slopeBlueCa = (blueA - blueC) / deltaYAc
			slopeWeightCa = (weightA - weightC) / deltaYAc
			slopeWeight2Ca = (weight2A - weight2C) / deltaYAc
		}
		if (yA <= yB && yA <= yC) {
			if (!(yA >= this.height.toFloat())) {
				if (yB > this.height.toFloat()) {
					yB = this.height.toFloat()
				}
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				if (yB < yC) {
					xC = xA
					zC = zA
					uC = uA
					vC = vA
					fogC = fogA
					alphaC = alphaA
					redC = redA
					greenC = greenA
					blueC = blueA
					weightC = weightA
					weight2C = weight2A
					if (yA < 0.0f) {
						xA -= slopeXBa * yA
						xC -= slopeXCa * yA
						zA -= slopeZBa * yA
						zC -= slopeZCa * yA
						uA -= slopeUBa * yA
						uC -= slopeUCa * yA
						vA -= slopeVBa * yA
						vC -= slopeVCa * yA
						fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
						fogC = (fogC.toFloat() - slopeFogCa * yA).toInt()
						alphaA -= slopeAlphaBa * yA
						alphaC -= slopeAlphaCa * yA
						redA -= slopeRedBa * yA
						redC -= slopeRedCa * yA
						greenA -= slopeGreenBa * yA
						greenC -= slopeGreenCa * yA
						blueA -= slopeBlueBa * yA
						blueC -= slopeBlueCa * yA
						weightA -= slopeWeightBa * yA
						weightC -= slopeWeightCa * yA
						weight2A -= slopeWeight2Ba * yA
						weight2C -= slopeWeight2Ca * yA
						yA = 0.0f
					}
					if (yB < 0.0f) {
						xB -= slopeXCb * yB
						zB -= slopeZCb * yB
						uB -= slopeUCb * yB
						vB -= slopeVCb * yB
						fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
						alphaB -= slopeAlphaCb * yB
						redB -= slopeRedCb * yB
						greenB -= slopeGreenCb * yB
						blueB -= slopeBlueCb * yB
						weightB -= slopeWeightCb * yB
						weight2B -= slopeWeight2Cb * yB
						yB = 0.0f
					}
					if (yA != yB && slopeXCa < slopeXBa || yA == yB && slopeXCa > slopeXCb) {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A)
							xA += slopeXBa
							xC += slopeXCa
							zA += slopeZBa
							zC += slopeZCa
							uA += slopeUBa
							uC += slopeUCa
							vA += slopeVBa
							vC += slopeVCa
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							alphaA += slopeAlphaBa
							alphaC += slopeAlphaCa
							redA += slopeRedBa
							redC += slopeRedCa
							greenA += slopeGreenBa
							greenC += slopeGreenCa
							blueA += slopeBlueBa
							blueC += slopeBlueCa
							weightA += slopeWeightBa
							weightC += slopeWeightCa
							weight2A += slopeWeight2Ba
							weight2C += slopeWeight2Ba
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B)
							xB += slopeXCb
							xC += slopeXCa
							zB += slopeZCb
							zC += slopeZCa
							uB += slopeUCb
							uC += slopeUCa
							vB += slopeVCb
							vC += slopeVCa
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							alphaB += slopeAlphaCb
							alphaC += slopeAlphaCa
							redB += slopeRedCb
							redC += slopeRedCa
							greenB += slopeGreenCb
							greenC += slopeGreenCa
							blueB += slopeBlueCb
							blueC += slopeBlueCa
							weightB += slopeWeightCb
							weightC += slopeWeightCa
							weight2B += slopeWeight2Cb
							weight2C += slopeWeight2Ca
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C)
							xA += slopeXBa
							xC += slopeXCa
							zA += slopeZBa
							zC += slopeZCa
							uA += slopeUBa
							uC += slopeUCa
							vA += slopeVBa
							vC += slopeVCa
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							alphaA += slopeAlphaBa
							alphaC += slopeAlphaCa
							redA += slopeRedBa
							redC += slopeRedCa
							greenA += slopeGreenBa
							greenC += slopeGreenCa
							blueA += slopeBlueBa
							blueC += slopeBlueCa
							weightA += slopeWeightBa
							weightC += slopeWeightCa
							weight2A += slopeWeight2Ba
							weight2C += slopeWeight2Ca
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C)
							xB += slopeXCb
							xC += slopeXCa
							zB += slopeZCb
							zC += slopeZCa
							uB += slopeUCb
							uC += slopeUCa
							vB += slopeVCb
							vC += slopeVCa
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							alphaB += slopeAlphaCb
							alphaC += slopeAlphaCa
							redB += slopeRedCb
							redC += slopeRedCa
							greenB += slopeGreenCb
							greenC += slopeGreenCa
							blueB += slopeBlueCb
							blueC += slopeBlueCa
							weightB += slopeWeightCb
							weightC += slopeWeightCa
							weight2B += slopeWeight2Cb
							weight2C += slopeWeight2Ca
							yA += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xB = xA
					zB = zA
					uB = uA
					vB = vA
					fogB = fogA
					alphaB = alphaA
					redB = redA
					greenB = greenA
					blueB = blueA
					weightB = weightA
					weight2B = weight2A
					if (yA < 0.0f) {
						xA -= slopeXBa * yA
						xB -= slopeXCa * yA
						zA -= slopeZBa * yA
						zB -= slopeZCa * yA
						uA -= slopeUBa * yA
						uB -= slopeUCa * yA
						vA -= slopeVBa * yA
						vB -= slopeVCa * yA
						fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
						fogB = (fogB.toFloat() - slopeFogCa * yA).toInt()
						alphaA -= slopeAlphaBa * yA
						alphaB -= slopeAlphaCa * yA
						redA -= slopeRedBa * yA
						redB -= slopeRedCa * yA
						greenA -= slopeGreenBa * yA
						greenB -= slopeGreenCa * yA
						blueA -= slopeBlueBa * yA
						blueB -= slopeBlueCa * yA
						weightA -= slopeWeightBa * yA
						weightB -= slopeWeightCa * yA
						weight2A -= slopeWeight2Ba * yA
						weight2B -= slopeWeight2Ca * yA
						yA = 0.0f
					}
					if (yC < 0.0f) {
						xC -= slopeXCb * yC
						zC -= slopeZCb * yC
						uC -= slopeUCb * yC
						vC -= slopeVCb * yC
						fogC = (fogC.toFloat() - slopeFogCb * yC).toInt()
						alphaC -= slopeAlphaCb * yC
						redC -= slopeRedCb * yC
						greenC -= slopeGreenCb * yC
						blueC -= slopeBlueCb * yC
						weightC -= slopeWeightCb * yC
						weight2C -= slopeWeight2Cb * yC
						yC = 0.0f
					}
					if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A)
							xA += slopeXBa
							xB += slopeXCa
							zA += slopeZBa
							zB += slopeZCa
							uA += slopeUBa
							uB += slopeUCa
							vA += slopeVBa
							vB += slopeVCa
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogB = (fogB.toFloat() + slopeFogCa).toInt()
							alphaA += slopeAlphaBa
							alphaB += slopeAlphaCa
							redA += slopeRedBa
							redB += slopeRedCa
							greenA += slopeGreenBa
							greenB += slopeGreenCa
							blueA += slopeBlueBa
							blueB += slopeBlueCa
							weightA += slopeWeightBa
							weightB += slopeWeightCa
							weight2A += slopeWeight2Ba
							weight2B += slopeWeight2Ca
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A)
							xC += slopeXCb
							xA += slopeXBa
							zC += slopeZCb
							zA += slopeZBa
							uC += slopeUCb
							uA += slopeUBa
							vC += slopeVCb
							vA += slopeVBa
							fogC = (fogC.toFloat() + slopeFogCb).toInt()
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							alphaC += slopeAlphaCb
							alphaA += slopeAlphaBa
							redC += slopeRedCb
							redA += slopeRedBa
							greenC += slopeGreenCb
							greenA += slopeGreenBa
							blueC += slopeBlueCb
							blueA += slopeBlueBa
							weightC += slopeWeightCb
							weightA += slopeWeightBa
							weight2C += slopeWeight2Cb
							weight2A += slopeWeight2Ba
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B)
							xB += slopeXCa
							xA += slopeXBa
							zB += slopeZCa
							zA += slopeZBa
							uB += slopeUCa
							uA += slopeUBa
							vB += slopeVCa
							vA += slopeVBa
							fogB = (fogB.toFloat() + slopeFogCa).toInt()
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							alphaB += slopeAlphaCa
							alphaA += slopeAlphaBa
							redB += slopeRedCa
							redA += slopeRedBa
							greenB += slopeGreenCa
							greenA += slopeGreenBa
							blueB += slopeBlueCa
							blueA += slopeBlueBa
							weightB += slopeWeightCa
							weightA += slopeWeightBa
							weight2B += slopeWeight2Ca
							weight2A += slopeWeight2Ba
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yA.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C)
							xA += slopeXBa
							xC += slopeXCb
							zA += slopeZBa
							zC += slopeZCb
							uA += slopeUBa
							uC += slopeUCb
							vA += slopeVBa
							vC += slopeVCb
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogC = (fogC.toFloat() + slopeFogCb).toInt()
							alphaA += slopeAlphaBa
							alphaC += slopeAlphaCb
							redA += slopeRedBa
							redC += slopeRedCb
							greenA += slopeGreenBa
							greenC += slopeGreenCb
							blueA += slopeBlueBa
							blueC += slopeBlueCb
							weightA += slopeWeightBa
							weightC += slopeWeightCb
							weight2A += slopeWeight2Ba
							weight2C += slopeWeight2Cb
							yA += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= this.height.toFloat())) {
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				if (yA > this.height.toFloat()) {
					yA = this.height.toFloat()
				}
				if (yC < yA) {
					xA = xB
					zA = zB
					uA = uB
					vA = vB
					fogA = fogB
					alphaA = alphaB
					redA = redB
					greenA = greenB
					blueA = blueB
					weightA = weightB
					weight2A = weight2B
					if (yB < 0.0f) {
						xA = xB - slopeXBa * yB
						xB -= slopeXCb * yB
						zA = zB - slopeZBa * yB
						zB -= slopeZCb * yB
						uA = uB - slopeUBa * yB
						uB -= slopeUCb * yB
						vA = vB - slopeVBa * yB
						vB -= slopeVCb * yB
						fogA = (fogB.toFloat() - slopeFogBa * yB).toInt()
						fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
						alphaA = alphaB - slopeAlphaBa * yB
						alphaB -= slopeAlphaCb * yB
						redA = redB - slopeRedBa * yB
						redB -= slopeRedCb * yB
						greenA = greenB - slopeGreenBa * yB
						greenB -= slopeGreenCb * yB
						blueA = blueB - slopeBlueBa * yB
						blueB -= slopeBlueCb * yB
						weightA = weightB - slopeWeightBa * yB
						weightB -= slopeWeightCb * yB
						weight2A = weight2B - slopeWeight2Ba * yB
						weight2B -= slopeWeight2Cb * yB
						yB = 0.0f
					}
					if (yC < 0.0f) {
						xC -= slopeXCa * yC
						zC -= slopeZCa * yC
						uC -= slopeUCa * yC
						vC -= slopeVCa * yC
						fogC = (fogC.toFloat() - slopeFogCa * yC).toInt()
						alphaC -= slopeAlphaCa * yC
						redC -= slopeRedCa * yC
						greenC -= slopeGreenCa * yC
						blueC -= slopeBlueCa * yC
						weightC -= slopeWeightCa * yC
						weight2C -= slopeWeight2Ca * yC
						yC = 0.0f
					}
					if (yB != yC && slopeXBa < slopeXCb || yB == yC && slopeXBa > slopeXCa) {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B)
							xA += slopeXBa
							xB += slopeXCb
							zA += slopeZBa
							zB += slopeZCb
							uA += slopeUBa
							uB += slopeUCb
							vA += slopeVBa
							vB += slopeVCb
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							alphaA += slopeAlphaBa
							alphaB += slopeAlphaCb
							redA += slopeRedBa
							redB += slopeRedCb
							greenA += slopeGreenBa
							greenB += slopeGreenCb
							blueA += slopeBlueBa
							blueB += slopeBlueCb
							weightA += slopeWeightBa
							weightB += slopeWeightCb
							weight2A += slopeWeight2Ba
							weight2B += slopeWeight2Cb
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C)
							xA += slopeXBa
							xC += slopeXCa
							zA += slopeZBa
							zC += slopeZCa
							uA += slopeUBa
							uC += slopeUCa
							vA += slopeVBa
							vC += slopeVCa
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							alphaA += slopeAlphaBa
							alphaC += slopeAlphaCa
							redA += slopeRedBa
							redC += slopeRedCa
							greenA += slopeGreenBa
							greenC += slopeGreenCa
							blueA += slopeBlueBa
							blueC += slopeBlueCa
							weightA += slopeWeightBa
							weightC += slopeWeightCa
							weight2A += slopeWeight2Ba
							weight2C += slopeWeight2Ca
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A)
							xB += slopeXCb
							xA += slopeXBa
							zB += slopeZCb
							zA += slopeZBa
							uB += slopeUCb
							uA += slopeUBa
							vB += slopeVCb
							vA += slopeVBa
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							alphaB += slopeAlphaCb
							alphaA += slopeAlphaBa
							redB += slopeRedCb
							redA += slopeRedBa
							greenB += slopeGreenCb
							greenA += slopeGreenBa
							blueB += slopeBlueCb
							blueA += slopeBlueBa
							weightC += slopeWeightCb
							weightA += slopeWeightBa
							weight2B += slopeWeight2Cb
							weight2A += slopeWeight2Ba
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A)
							xC += slopeXCa
							xA += slopeXBa
							zC += slopeZCa
							zA += slopeZBa
							uC += slopeUCa
							uA += slopeUBa
							vC += slopeVCa
							vA += slopeVBa
							fogC = (fogC.toFloat() + slopeFogCa).toInt()
							fogA = (fogA.toFloat() + slopeFogBa).toInt()
							alphaC += slopeAlphaCa
							alphaA += slopeAlphaBa
							redC += slopeRedCa
							redA += slopeRedBa
							greenC += slopeGreenCa
							greenA += slopeGreenBa
							blueC += slopeBlueCa
							blueA += slopeBlueBa
							weightC += slopeWeightCa
							weightA += slopeWeightBa
							weight2C += slopeWeight2Ca
							weight2A += slopeWeight2Ba
							yB += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xC = xB
					zC = zB
					uC = uB
					vC = vB
					fogC = fogB
					alphaC = alphaB
					redC = redB
					greenC = greenB
					blueC = blueB
					weightC = weightB
					weight2C = weight2B
					if (yB < 0.0f) {
						xC = xB - slopeXBa * yB
						xB -= slopeXCb * yB
						zC = zB - slopeZBa * yB
						zB -= slopeZCb * yB
						uC = uB - slopeUBa * yB
						uB -= slopeUCb * yB
						vC = vB - slopeVBa * yB
						vB -= slopeVCb * yB
						fogC = (fogB.toFloat() - slopeFogBa * yB).toInt()
						fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
						alphaC = alphaB - slopeAlphaBa * yB
						alphaB -= slopeAlphaCb * yB
						redC = redB - slopeRedBa * yB
						redB -= slopeRedCb * yB
						greenC = greenB - slopeGreenBa * yB
						greenB -= slopeGreenCb * yB
						blueC = blueB - slopeBlueBa * yB
						blueB -= slopeBlueCb * yB
						weightC = weightB - slopeWeightBa * yB
						weightB -= slopeWeightCb * yB
						weight2C = weight2B - slopeWeight2Ba * yB
						weight2B -= slopeWeight2Cb * yB
						yB = 0.0f
					}
					if (yA < 0.0f) {
						xA -= slopeXCa * yA
						zA -= slopeZCa * yA
						uA -= slopeUCa * yA
						vA -= slopeVCa * yA
						fogA = (fogA.toFloat() - slopeFogCa * yA).toInt()
						alphaA -= slopeAlphaCa * yA
						redA -= slopeRedCa * yA
						greenA -= slopeGreenCa * yA
						blueA -= slopeBlueCa * yA
						weightA -= slopeWeightCa * yA
						weight2A -= slopeWeight2Ca * yA
						yA = 0.0f
					}
					yC -= yA
					yA -= yB
					yB = this.lineOffsets[yB.toInt()].toFloat()
					if (slopeXBa < slopeXCb) {
						while (--yA >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B)
							xC += slopeXBa
							xB += slopeXCb
							zC += slopeZBa
							zB += slopeZCb
							uC += slopeUBa
							uB += slopeUCb
							vC += slopeVBa
							vB += slopeVCb
							fogC = (fogC.toFloat() + slopeFogBa).toInt()
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							alphaC += slopeAlphaBa
							alphaB += slopeAlphaCb
							redC += slopeRedBa
							redB += slopeRedCb
							greenC += slopeGreenBa
							greenB += slopeGreenCb
							blueC += slopeBlueBa
							blueB += slopeBlueCb
							weightC += slopeWeightBa
							weightB += slopeWeightCb
							weight2C += slopeWeight2Ba
							weight2B += slopeWeight2Cb
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B)
							xA += slopeXCa
							xB += slopeXCb
							zA += slopeZCa
							zB += slopeZCb
							uA += slopeUCa
							uB += slopeUCb
							vA += slopeVCa
							vB += slopeVCb
							fogA = (fogA.toFloat() + slopeFogCa).toInt()
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							alphaA += slopeAlphaCa
							alphaB += slopeAlphaCb
							redA += slopeRedCa
							redB += slopeRedCb
							greenA += slopeGreenCa
							greenB += slopeGreenCb
							blueA += slopeBlueCa
							blueB += slopeBlueCb
							weightA += slopeWeightCa
							weightB += slopeWeightCb
							weight2A += slopeWeight2Ca
							weight2B += slopeWeight2Cb
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						while (--yA >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C)
							xB += slopeXCb
							xC += slopeXBa
							zB += slopeZCb
							zC += slopeZBa
							uB += slopeUCb
							uC += slopeUBa
							vB += slopeVCb
							vC += slopeVBa
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							fogC = (fogC.toFloat() + slopeFogBa).toInt()
							alphaB += slopeAlphaCb
							alphaC += slopeAlphaBa
							redB += slopeRedCb
							redC += slopeRedBa
							greenB += slopeGreenCb
							greenC += slopeGreenBa
							blueB += slopeBlueCb
							blueC += slopeBlueBa
							weightB += slopeWeightCb
							weightC += slopeWeightBa
							weight2B += slopeWeight2Cb
							weight2C += slopeWeight2Ba
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, yB.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A)
							xB += slopeXCb
							xA += slopeXCa
							zB += slopeZCb
							zA += slopeZCa
							uB += slopeUCb
							uA += slopeUCa
							vB += slopeVCb
							vA += slopeVCa
							fogB = (fogB.toFloat() + slopeFogCb).toInt()
							fogA = (fogA.toFloat() + slopeFogCa).toInt()
							alphaB += slopeAlphaCb
							alphaA += slopeAlphaCa
							redB += slopeRedCb
							redA += slopeRedCa
							greenB += slopeGreenCb
							greenA += slopeGreenCa
							blueB += slopeBlueCb
							blueA += slopeBlueCa
							weightB += slopeWeightCb
							weightA += slopeWeightCa
							weight2B += slopeWeight2Cb
							weight2A += slopeWeight2Ca
							yB += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (!(yC >= this.height.toFloat())) {
			if (yA > this.height.toFloat()) {
				yA = this.height.toFloat()
			}
			if (yB > this.height.toFloat()) {
				yB = this.height.toFloat()
			}
			if (yA < yB) {
				xB = xC
				zB = zC
				uB = uC
				vB = vC
				fogB = fogC
				alphaB = alphaC
				redB = redC
				greenB = greenC
				blueB = blueC
				weightB = weightC
				weight2B = weight2C
				if (yC < 0.0f) {
					xC -= slopeXCa * yC
					xB -= slopeXCb * yC
					zC -= slopeZCa * yC
					zB -= slopeZCb * yC
					uC -= slopeUCa * yC
					uB -= slopeUCb * yC
					vC -= slopeVCa * yC
					vB -= slopeVCb * yC
					fogC = (fogC.toFloat() - slopeFogCa * 3.0f).toInt()
					fogB = (fogB.toFloat() - slopeFogCb * yC).toInt()
					alphaC -= slopeAlphaCa * yC
					alphaB -= slopeAlphaCb * yC
					redC -= slopeRedCa * yC
					redB -= slopeRedCb * yC
					greenC -= slopeGreenCa * yC
					greenB -= slopeGreenCb * yC
					blueC -= slopeBlueCa * yC
					blueB -= slopeBlueCb * yC
					weightC -= slopeWeightCa * yC
					weightB -= slopeWeightCb * yC
					weight2C -= slopeWeight2Ca * yC
					weight2B -= slopeWeight2Cb * yC
					yC = 0.0f
				}
				if (yA < 0.0f) {
					xA -= slopeXBa * yA
					zA -= slopeZBa * yA
					uA -= slopeUBa * yA
					vA -= slopeVBa * yA
					fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
					alphaA -= slopeAlphaBa * yA
					redA -= slopeRedBa * yA
					greenA -= slopeGreenBa * yA
					blueA -= slopeBlueBa * yA
					weightA -= slopeWeightBa * yA
					weight2A -= slopeWeight2Ba * yA
					yA = 0.0f
				}
				if (slopeXCb < slopeXCa) {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C)
						xB += slopeXCb
						xC += slopeXCa
						zB += slopeZCb
						zC += slopeZCa
						uB += slopeUCb
						uC += slopeUCa
						vB += slopeVCb
						vC += slopeVCa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaB += slopeAlphaCb
						alphaC += slopeAlphaCa
						redB += slopeRedCb
						redC += slopeRedCa
						greenB += slopeGreenCb
						greenC += slopeGreenCa
						blueB += slopeBlueCb
						blueC += slopeBlueCa
						weightB += slopeWeightCb
						weightC += slopeWeightCa
						weight2B += slopeWeight2Cb
						weight2C += slopeWeight2Ca
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A)
						xB += slopeXCb
						xA += slopeXBa
						zB += slopeZCb
						zA += slopeZBa
						uB += slopeUCb
						uA += slopeUBa
						vB += slopeVCb
						vA += slopeVBa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						alphaB += slopeAlphaCb
						alphaA += slopeAlphaBa
						redB += slopeRedCb
						redA += slopeRedBa
						greenB += slopeGreenCb
						greenA += slopeGreenBa
						blueB += slopeBlueCb
						blueA += slopeBlueBa
						weightB += slopeWeightCb
						weightA += slopeWeightBa
						weight2B += slopeWeight2Cb
						weight2A += slopeWeight2Ba
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B)
						xC += slopeXCa
						xB += slopeXCb
						zC += slopeZCa
						zB += slopeZCb
						uC += slopeUCa
						uB += slopeUCb
						vC += slopeVCa
						vB += slopeVCb
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						alphaC += slopeAlphaCa
						alphaB += slopeAlphaCb
						redC += slopeRedCa
						redB += slopeRedCb
						greenC += slopeGreenCa
						greenB += slopeGreenCb
						blueC += slopeBlueCa
						blueB += slopeBlueCb
						weightC += slopeWeightCa
						weightB += slopeWeightCb
						weight2C += slopeWeight2Ca
						weight2B += slopeWeight2Cb
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B)
						xA += slopeXBa
						xB += slopeXCb
						zA += slopeZBa
						zB += slopeZCb
						uA += slopeUBa
						uB += slopeUCb
						vA += slopeVBa
						vB += slopeVCb
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						alphaA += slopeAlphaBa
						alphaB += slopeAlphaCb
						redA += slopeRedBa
						redB += slopeRedCb
						greenA += slopeGreenBa
						greenB += slopeGreenCb
						blueA += slopeBlueBa
						blueB += slopeBlueCb
						weightA += slopeWeightBa
						weightB += slopeWeightCb
						weight2A += slopeWeight2Ba
						weight2B += slopeWeight2Cb
						yC += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xA = xC
				zA = zC
				uA = uC
				vA = vC
				fogA = fogC
				alphaA = alphaC
				redA = redC
				greenA = greenC
				blueA = blueC
				weightA = weightC
				weight2A = weight2C
				if (yC < 0.0f) {
					xC -= slopeXCa * yC
					xA -= slopeXCb * yC
					zC -= slopeZCa * yC
					zA -= slopeZCb * yC
					uC -= slopeUCa * yC
					uA -= slopeUCb * yC
					vC -= slopeVCa * yC
					vA -= slopeVCb * yC
					fogC = (fogC.toFloat() - slopeFogCa * 3.0f).toInt()
					fogA = (fogA.toFloat() - slopeFogCb * yC).toInt()
					alphaC -= slopeAlphaCa * yC
					alphaA -= slopeAlphaCb * yC
					redC -= slopeRedCa * yC
					redA -= slopeRedCb * yC
					greenC -= slopeGreenCa * yC
					greenA -= slopeGreenCb * yC
					blueC -= slopeBlueCa * yC
					blueA -= slopeBlueCb * yC
					weightC -= slopeWeightCa * yC
					weightA -= slopeWeightCb * yC
					weight2C -= slopeWeight2Ca * yC
					weight2A -= slopeWeight2Cb * yC
					yC = 0.0f
				}
				if (yB < 0.0f) {
					xB -= slopeXBa * yB
					zB -= slopeZBa * yB
					uB -= slopeUBa * yB
					vB -= slopeVBa * yB
					fogB = (fogB.toFloat() - slopeFogBa * yB).toInt()
					alphaB -= slopeAlphaBa * yB
					redB -= slopeRedBa * yB
					greenB -= slopeGreenBa * yB
					blueB -= slopeBlueBa * yB
					weightB -= slopeWeightBa * yB
					weight2B -= slopeWeight2Ba * yB
					yB = 0.0f
				}
				if (slopeXCb < slopeXCa) {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C)
						xA += slopeXCb
						xC += slopeXCa
						zA += slopeZCb
						zC += slopeZCa
						uA += slopeUCb
						uC += slopeUCa
						vA += slopeVCb
						vC += slopeVCa
						fogA = (fogA.toFloat() + slopeFogCb).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaA += slopeAlphaCb
						alphaC += slopeAlphaCa
						redA += slopeRedCb
						redC += slopeRedCa
						greenA += slopeGreenCb
						greenC += slopeGreenCa
						blueA += slopeBlueCb
						blueC += slopeBlueCa
						weightA += slopeWeightCb
						weightC += slopeWeightCa
						weight2A += slopeWeight2Cb
						weight2C += slopeWeight2Ca
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C)
						xB += slopeXBa
						xC += slopeXCa
						zB += slopeZBa
						zC += slopeZCa
						uB += slopeUBa
						uC += slopeUCa
						vB += slopeVBa
						vC += slopeVCa
						fogB = (fogB.toFloat() + slopeFogBa).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaB += slopeAlphaBa
						alphaC += slopeAlphaCa
						redB += slopeRedBa
						redC += slopeRedCa
						greenB += slopeGreenBa
						greenC += slopeGreenCa
						blueB += slopeBlueBa
						blueC += slopeBlueCa
						weightB += slopeWeightBa
						weightC += slopeWeightCa
						weight2B += slopeWeight2Ba
						weight2C += slopeWeight2Ca
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A)
						xC += slopeXCa
						xA += slopeXCb
						zC += slopeZCa
						zA += slopeZCb
						uC += slopeUCa
						uA += slopeUCb
						vC += slopeVCa
						vA += slopeVCb
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						fogA = (fogA.toFloat() + slopeFogCb).toInt()
						alphaC += slopeAlphaCa
						alphaA += slopeAlphaCb
						redC += slopeRedCa
						redA += slopeRedCb
						greenC += slopeGreenCa
						greenA += slopeGreenCb
						blueC += slopeBlueCa
						blueA += slopeBlueCb
						weightC += slopeWeightCa
						weightA += slopeWeightCb
						weight2C += slopeWeight2Ca
						weight2A += slopeWeight2Cb
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, yC.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B)
						xC += slopeXCa
						xB += slopeXBa
						zC += slopeZCa
						zB += slopeZBa
						uC += slopeUCa
						uB += slopeUBa
						vC += slopeVCa
						vB += slopeVBa
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						fogB = (fogB.toFloat() + slopeFogBa).toInt()
						alphaC += slopeAlphaCa
						alphaB += slopeAlphaBa
						redC += slopeRedCa
						redB += slopeRedBa
						greenC += slopeGreenCa
						greenB += slopeGreenBa
						blueC += slopeBlueCa
						blueB += slopeBlueBa
						weightC += slopeWeightCa
						weightB += slopeWeightBa
						weight2C += slopeWeight2Ca
						weight2B += slopeWeight2Ba
						yC += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFFFFFFFF)V")
	private fun drawGouraudSpanRgb(@OriginalArg(0) dst: IntArray?, @OriginalArg(1) depths: FloatArray?, @OriginalArg(2) rowOffset: Int, @OriginalArg(5) startX: Int, @OriginalArg(6) endX: Int, @OriginalArg(7) z: Float, @OriginalArg(8) zStep: Float, @OriginalArg(9) red: Float, @OriginalArg(10) redStep: Float, @OriginalArg(11) green: Float, @OriginalArg(12) greenStep: Float, @OriginalArg(13) blue: Float, @OriginalArg(14) blueStep: Float) {
		var rowOffset: Int = rowOffset
		var startX: Int = startX
		var endX: Int = endX
		var z: Float = z
		var red: Float = red
		var green: Float = green
		var blue: Float = blue
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width
			}
			if (startX < 0) {
				startX = 0
			}
		}
		if (startX >= endX) {
			return
		}
		@Pc(163) var alpha: Int
		@Pc(168) var local168: Int
		@Pc(223) var local223: Int
		@Pc(456) var local456: Int
		@Pc(460) var sum: Int
		@Pc(468) var redBlueSum: Int
		@Pc(54) var remaining: Int
		@Pc(88) var colour: Int
		@Pc(103) var index: Int
		@Pc(58) var redStep4: Float
		@Pc(62) var greenStep4: Float
		@Pc(66) var blueStep4: Float
		if (this.depthDisabled) {
			rowOffset += startX
			red += redStep * startX.toFloat()
			green += greenStep * startX.toFloat()
			blue += blueStep * startX.toFloat()
			if (this.fastScanline) {
				remaining = (endX - startX) shr 2
				redStep4 = redStep * 4.0f
				greenStep4 = greenStep * 4.0f
				blueStep4 = blueStep * 4.0f
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							index = rowOffset + 1
							dst!![rowOffset] = colour
							dst!![index++] = colour
							dst!![index++] = colour
							rowOffset = index + 1
							dst!![index] = colour
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						do {
							dst!![rowOffset++] = colour
							remaining--
						} while (remaining > 0)
						return
					}
				} else if (this.halfBlend) {
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							index = rowOffset + 1
							var background: Int = dst!![rowOffset]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
							@Pc(497) val index1: Int = index++
							@Pc(503) val background1: Int = dst!![index1]
							@Pc(507) val sum1: Int = colour + background1
							@Pc(515) val redBlueSum1: Int = (colour and 0xFF00FF) + (background1 and 0xFF00FF)
							@Pc(525) val overflow1: Int = (redBlueSum1 and 0x1000100) + ((sum1 - redBlueSum1) and 0x10000)
							dst!![index1] = (sum1 - overflow1) or 0xFF000000.toInt() or (overflow1 - (overflow1 ushr 8))
							@Pc(544) val index2: Int = index++
							@Pc(550) val background2: Int = dst!![index2]
							@Pc(554) val sum2: Int = colour + background2
							@Pc(562) val redBlueSum2: Int = (colour and 0xFF00FF) + (background2 and 0xFF00FF)
							@Pc(572) val overflow2: Int = (redBlueSum2 and 0x1000100) + ((sum2 - redBlueSum2) and 0x10000)
							dst!![index2] = (sum2 - overflow2) or 0xFF000000.toInt() or (overflow2 - (overflow2 ushr 8))
							rowOffset = index + 1
							@Pc(597) val background3: Int = dst!![index]
							@Pc(601) val sum3: Int = colour + background3
							@Pc(609) val redBlueSum3: Int = (colour and 0xFF00FF) + (background3 and 0xFF00FF)
							@Pc(619) val overflow3: Int = (redBlueSum3 and 0x1000100) + ((sum3 - redBlueSum3) and 0x10000)
							dst!![index] = (sum3 - overflow3) or 0xFF000000.toInt() or (overflow3 - (overflow3 ushr 8))
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						do {
							val pixelIndex: Int = rowOffset++
							var background: Int = dst!![pixelIndex]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![pixelIndex] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
							remaining--
						} while (remaining > 0)
					}
				} else {
					alpha = this.alpha
					val inverseAlpha: Int = 256 - this.alpha
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							var background: Int = dst!![rowOffset]
							index = rowOffset + 1
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							background = dst!![index]
							dst!![index++] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							background = dst!![index]
							dst!![index++] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							background = dst!![index]
							rowOffset = index + 1
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						do {
							val background: Int = dst!![rowOffset]
							dst!![rowOffset++] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							remaining--
						} while (remaining > 0)
						return
					}
				}
			} else {
				remaining = endX - startX
				if (this.alpha == 0) {
					do {
						dst!![rowOffset++] = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						red += redStep
						green += greenStep
						blue += blueStep
						remaining--
					} while (remaining > 0)
				} else if (this.halfBlend) {
					do {
						val pixelIndex: Int = rowOffset++
						val srcColour: Int = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						var background: Int = dst!![pixelIndex]
						sum = srcColour + background
						redBlueSum = (srcColour and 0xFF00FF) + (background and 0xFF00FF)
						background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						dst!![pixelIndex] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						red += redStep
						green += greenStep
						blue += blueStep
						remaining--
					} while (remaining > 0)
				} else {
					alpha = this.alpha
					val inverseAlpha: Int = 256 - this.alpha
					do {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						red += redStep
						green += greenStep
						blue += blueStep
						colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						val background: Int = dst!![rowOffset]
						dst!![rowOffset++] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						remaining--
					} while (remaining > 0)
				}
			}
			return
		}
		rowOffset += startX - 1
		z += zStep * startX.toFloat()
		red += redStep * startX.toFloat()
		green += greenStep * startX.toFloat()
		blue += blueStep * startX.toFloat()
		@Pc(1034) var nextZ: Float
		if (this.threadResource!!.zWrite) {
			if (this.fastScanline) {
				remaining = (endX - startX) shr 2
				redStep4 = redStep * 4.0f
				greenStep4 = greenStep * 4.0f
				blueStep4 = blueStep * 4.0f
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							index = rowOffset + 1
							if (z < depths!![index]) {
								dst!![index] = colour
								depths!![index] = z
							}
							nextZ = z + zStep
							index++
							if (nextZ < depths!![index]) {
								dst!![index] = colour
								depths!![index] = nextZ
							}
							nextZ += zStep
							index++
							if (nextZ < depths!![index]) {
								dst!![index] = colour
								depths!![index] = nextZ
							}
							nextZ += zStep
							rowOffset = index + 1
							if (nextZ < depths!![rowOffset]) {
								dst!![rowOffset] = colour
								depths!![rowOffset] = nextZ
							}
							z = nextZ + zStep
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						do {
							rowOffset++
							if (z < depths!![rowOffset]) {
								dst!![rowOffset] = colour
								depths!![rowOffset] = z
							}
							z += zStep
							remaining--
						} while (remaining > 0)
						return
					}
				} else if (this.halfBlend) {
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							index = rowOffset + 1
							if (z < depths!![index]) {
								var background: Int = dst!![index]
								sum = colour + background
								redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
								background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
								dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
								depths!![index] = z
							}
							nextZ = z + zStep
							index++
							if (nextZ < depths!![index]) {
								var background: Int = dst!![index]
								sum = colour + background
								redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
								background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
								dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
								depths!![index] = nextZ
							}
							nextZ += zStep
							index++
							if (nextZ < depths!![index]) {
								var background: Int = dst!![index]
								sum = colour + background
								redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
								background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
								dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
								depths!![index] = nextZ
							}
							nextZ += zStep
							rowOffset = index + 1
							if (nextZ < depths!![rowOffset]) {
								var background: Int = dst!![rowOffset]
								sum = colour + background
								redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
								background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
								dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
								depths!![rowOffset] = nextZ
							}
							z = nextZ + zStep
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						do {
							rowOffset++
							if (z < depths!![rowOffset]) {
								var background: Int = dst!![rowOffset]
								sum = colour + background
								redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
								background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
								dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
								depths!![rowOffset] = z
							}
							z += zStep
							remaining--
						} while (remaining > 0)
					}
				} else {
					alpha = this.alpha
					val inverseAlpha: Int = 256 - this.alpha
					if (remaining > 0) {
						do {
							colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							red += redStep4
							green += greenStep4
							blue += blueStep4
							colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							index = rowOffset + 1
							if (z < depths!![index]) {
								val background: Int = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = z
							}
							nextZ = z + zStep
							index++
							if (nextZ < depths!![index]) {
								val background: Int = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = nextZ
							}
							nextZ += zStep
							index++
							if (nextZ < depths!![index]) {
								val background: Int = dst!![index]
								dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![index] = nextZ
							}
							nextZ += zStep
							rowOffset = index + 1
							if (nextZ < depths!![rowOffset]) {
								val background: Int = dst!![rowOffset]
								dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![rowOffset] = nextZ
							}
							z = nextZ + zStep
							remaining--
						} while (remaining > 0)
					}
					remaining = (endX - startX) and 0x3
					if (remaining > 0) {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						do {
							rowOffset++
							if (z < depths!![rowOffset]) {
								val background: Int = dst!![rowOffset]
								dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![rowOffset] = z
							}
							z += zStep
							remaining--
						} while (remaining > 0)
						return
					}
				}
			} else {
				remaining = endX - startX
				if (this.alpha == 0) {
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset] = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							depths!![rowOffset] = z
						}
						z += zStep
						red += redStep
						green += greenStep
						blue += blueStep
						remaining--
					} while (remaining > 0)
				} else if (this.halfBlend) {
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							val srcColour: Int = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							var background: Int = dst!![rowOffset]
							sum = srcColour + background
							redBlueSum = (srcColour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
							depths!![rowOffset] = z
						}
						z += zStep
						red += redStep
						green += greenStep
						blue += blueStep
						remaining--
					} while (remaining > 0)
				} else {
					alpha = this.alpha
					val inverseAlpha: Int = 256 - this.alpha
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
							colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							val background: Int = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
							depths!![rowOffset] = z
						}
						z += zStep
						red += redStep
						green += greenStep
						blue += blueStep
						remaining--
					} while (remaining > 0)
				}
			}
		} else if (this.fastScanline) {
			remaining = (endX - startX) shr 2
			redStep4 = redStep * 4.0f
			greenStep4 = greenStep * 4.0f
			blueStep4 = blueStep * 4.0f
			if (this.alpha == 0) {
				if (remaining > 0) {
					do {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						red += redStep4
						green += greenStep4
						blue += blueStep4
						index = rowOffset + 1
						if (z < depths!![index]) {
							dst!![index] = colour
						}
						nextZ = z + zStep
						index++
						if (nextZ < depths!![index]) {
							dst!![index] = colour
						}
						nextZ += zStep
						index++
						if (nextZ < depths!![index]) {
							dst!![index] = colour
						}
						nextZ += zStep
						rowOffset = index + 1
						if (nextZ < depths!![rowOffset]) {
							dst!![rowOffset] = colour
						}
						z = nextZ + zStep
						remaining--
					} while (remaining > 0)
				}
				remaining = (endX - startX) and 0x3
				if (remaining > 0) {
					colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset] = colour
						}
						z += zStep
						remaining--
					} while (remaining > 0)
					return
				}
			} else if (this.halfBlend) {
				if (remaining > 0) {
					do {
						colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						red += redStep4
						green += greenStep4
						blue += blueStep4
						index = rowOffset + 1
						if (z < depths!![index]) {
							var background: Int = dst!![index]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						}
						nextZ = z + zStep
						index++
						if (nextZ < depths!![index]) {
							var background: Int = dst!![index]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						}
						nextZ += zStep
						index++
						if (nextZ < depths!![index]) {
							var background: Int = dst!![index]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![index] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						}
						nextZ += zStep
						rowOffset = index + 1
						if (nextZ < depths!![rowOffset]) {
							var background: Int = dst!![rowOffset]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						}
						z = nextZ + zStep
						remaining--
					} while (remaining > 0)
				}
				remaining = (endX - startX) and 0x3
				if (remaining > 0) {
					colour = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							var background: Int = dst!![rowOffset]
							sum = colour + background
							redBlueSum = (colour and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							dst!![rowOffset] = (sum - background) or 0xFF000000.toInt() or (background - (background ushr 8))
						}
						z += zStep
						remaining--
					} while (remaining > 0)
				}
			} else {
				alpha = this.alpha
				val inverseAlpha: Int = 256 - this.alpha
				if (remaining > 0) {
					do {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						red += redStep4
						green += greenStep4
						blue += blueStep4
						colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						index = rowOffset + 1
						if (z < depths!![index]) {
							val background: Int = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ = z + zStep
						index++
						if (nextZ < depths!![index]) {
							val background: Int = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ += zStep
						index++
						if (nextZ < depths!![index]) {
							val background: Int = dst!![index]
							dst!![index] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						nextZ += zStep
						rowOffset = index + 1
						if (nextZ < depths!![rowOffset]) {
							val background: Int = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						z = nextZ + zStep
						remaining--
					} while (remaining > 0)
				}
				remaining = (endX - startX) and 0x3
				if (remaining > 0) {
					colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
					colour = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
					do {
						rowOffset++
						if (z < depths!![rowOffset]) {
							val background: Int = dst!![rowOffset]
							dst!![rowOffset] = colour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						z += zStep
						remaining--
					} while (remaining > 0)
					return
				}
			}
		} else {
			remaining = endX - startX
			if (this.alpha == 0) {
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						dst!![rowOffset] = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
					}
					z += zStep
					red += redStep
					green += greenStep
					blue += blueStep
					remaining--
				} while (remaining > 0)
			} else if (this.halfBlend) {
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						val srcColour: Int = (red.toInt() and 0xFF0000) or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						val background: Int = dst!![rowOffset]
						sum = srcColour + background
						redBlueSum = (srcColour and 0xFF00FF) + (background and 0xFF00FF)
						@Pc(3112) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						dst!![rowOffset] = (sum - overflow) or 0xFF000000.toInt() or (overflow - (overflow ushr 8))
					}
					z += zStep
					red += redStep
					green += greenStep
					blue += blueStep
					remaining--
				} while (remaining > 0)
			} else {
				alpha = this.alpha
				val inverseAlpha: Int = 256 - this.alpha
				do {
					rowOffset++
					if (z < depths!![rowOffset]) {
						colour = (red.toInt() and 0xFF0000) or 0xFF000000.toInt() or (green.toInt() and 0xFF00) or (blue.toInt() and 0xFF)
						@Pc(3012) val scaledColour: Int = ((((colour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						val background: Int = dst!![rowOffset]
						dst!![rowOffset] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
					}
					z += zStep
					red += redStep
					green += greenStep
					blue += blueStep
					remaining--
				} while (remaining > 0)
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFF)V")
	fun renderTriangleHslRgb(@OriginalArg(0) yA: Float, @OriginalArg(1) yB: Float, @OriginalArg(2) yC: Float, @OriginalArg(3) xA: Float, @OriginalArg(4) xB: Float, @OriginalArg(5) xC: Float, @OriginalArg(6) zA: Float, @OriginalArg(7) zB: Float, @OriginalArg(8) zC: Float, @OriginalArg(9) hslA: Float, @OriginalArg(10) hslB: Float, @OriginalArg(11) hslC: Float) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var hslA: Float = hslA
		var hslB: Float = hslB
		var hslC: Float = hslC
		if (this.wireframe) {
			this.toolkit!!.drawOpaqueLine(yA.toInt(), xA.toInt(), xB.toInt(), ColorUtil.hsvToRgbTable!![hslA.toInt()], yB.toInt())
			this.toolkit!!.drawOpaqueLine(yB.toInt(), xB.toInt(), xC.toInt(), ColorUtil.hsvToRgbTable!![hslA.toInt()], yC.toInt())
			this.toolkit!!.drawOpaqueLine(yC.toInt(), xC.toInt(), xA.toInt(), ColorUtil.hsvToRgbTable!![hslA.toInt()], yA.toInt())
			return
		}
		@Pc(55) val deltaXBa: Float = xB - xA
		@Pc(59) val deltaYBa: Float = yB - yA
		@Pc(63) val deltaXCa: Float = xC - xA
		@Pc(67) val deltaYCa: Float = yC - yA
		@Pc(71) val deltaHslBa: Float = hslB - hslA
		@Pc(75) val deltaHslCa: Float = hslC - hslA
		@Pc(79) val deltaZBa: Float = zB - zA
		@Pc(83) val deltaZCa: Float = zC - zA
		@Pc(95) var slopeXCb: Float
		if (yC == yB) {
			slopeXCb = 0.0f
		} else {
			slopeXCb = (xC - xB) / (yC - yB)
		}
		@Pc(106) var slopeXBa: Float
		if (yB == yA) {
			slopeXBa = 0.0f
		} else {
			slopeXBa = deltaXBa / deltaYBa
		}
		@Pc(117) var slopeXCa: Float
		if (yC == yA) {
			slopeXCa = 0.0f
		} else {
			slopeXCa = deltaXCa / deltaYCa
		}
		@Pc(128) val determinant: Float = deltaXBa * deltaYCa - deltaXCa * deltaYBa
		if (determinant == 0.0f) {
			return
		}
		@Pc(143) val slopeHslX: Float = (deltaHslBa * deltaYCa - deltaHslCa * deltaYBa) / determinant
		@Pc(153) val slopeHslY: Float = (deltaHslCa * deltaXBa - deltaHslBa * deltaXCa) / determinant
		@Pc(163) val slopeDepthX: Float = (deltaZBa * deltaYCa - deltaZCa * deltaYBa) / determinant
		@Pc(173) val slopeDepthY: Float = (deltaZCa * deltaXBa - deltaZBa * deltaXCa) / determinant
		if (yA <= yB && yA <= yC) {
			if (!(yA >= this.height.toFloat())) {
				if (yB > this.height.toFloat()) {
					yB = this.height.toFloat()
				}
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				hslA = hslA + slopeHslX - slopeHslX * xA
				zA = zA + slopeDepthX - slopeDepthX * xA
				if (yB < yC) {
					xC = xA
					if (yA < 0.0f) {
						xC = xA - slopeXCa * yA
						xA -= slopeXBa * yA
						hslA -= slopeHslY * yA
						zA -= slopeDepthY * yA
						yA = 0.0f
					}
					if (yB < 0.0f) {
						xB -= slopeXCb * yB
						yB = 0.0f
					}
					if ((yA == yB || !(slopeXCa < slopeXBa)) && (yA != yB || !(slopeXCa > slopeXCb))) {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xC.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCa
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xB.toInt(), xC.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCa
							xB += slopeXCb
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xA.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCa
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xB.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCa
							xB += slopeXCb
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xB = xA
					if (yA < 0.0f) {
						xB = xA - slopeXCa * yA
						xA -= slopeXBa * yA
						hslA -= slopeHslY * yA
						zA -= slopeDepthY * yA
						yA = 0.0f
					}
					if (yC < 0.0f) {
						xC -= slopeXCb * yC
						yC = 0.0f
					}
					if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xB.toInt(), xA.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xB += slopeXCa
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xA.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCb
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xB.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xB += slopeXCa
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xC.toInt(), hslA, slopeHslX, zA, slopeDepthX)
							xC += slopeXCb
							xA += slopeXBa
							hslA += slopeHslY
							zA += slopeDepthY
							yA += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= this.height.toFloat())) {
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				if (yA > this.height.toFloat()) {
					yA = this.height.toFloat()
				}
				hslB = hslB + slopeHslX - slopeHslX * xB
				zB = zB + slopeDepthX - slopeDepthX * xB
				if (yC < yA) {
					xA = xB
					if (yB < 0.0f) {
						xA = xB - slopeXBa * yB
						xB -= slopeXCb * yB
						hslB -= slopeHslY * yB
						zB -= slopeDepthY * yB
						yB = 0.0f
					}
					if (yC < 0.0f) {
						xC -= slopeXCa * yC
						yC = 0.0f
					}
					if ((yB == yC || !(slopeXBa < slopeXCb)) && (yB != yC || !(slopeXBa > slopeXCa))) {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xA.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXBa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xC.toInt(), xA.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXBa
							xC += slopeXCa
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xB.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXBa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xC.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXBa
							xC += slopeXCa
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xC = xB
					if (yB < 0.0f) {
						xC = xB - slopeXBa * yB
						xB -= slopeXCb * yB
						hslB -= slopeHslY * yB
						zB -= slopeDepthY * yB
						yB = 0.0f
					}
					if (yA < 0.0f) {
						xA -= slopeXCa * yA
						yA = 0.0f
					}
					if (slopeXBa < slopeXCb) {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xC.toInt(), xB.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xC += slopeXBa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xB.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXCa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xC.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xC += slopeXBa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xA.toInt(), hslB, slopeHslX, zB, slopeDepthX)
							xA += slopeXCa
							xB += slopeXCb
							hslB += slopeHslY
							zB += slopeDepthY
							yB += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (!(yC >= this.height.toFloat())) {
			if (yA > this.height.toFloat()) {
				yA = this.height.toFloat()
			}
			if (yB > this.height.toFloat()) {
				yB = this.height.toFloat()
			}
			hslC = hslC + slopeHslX - slopeHslX * xC
			zC = zC + slopeDepthX - slopeDepthX * xC
			if (yA < yB) {
				xB = xC
				if (yC < 0.0f) {
					xB = xC - slopeXCb * yC
					xC -= slopeXCa * yC
					hslC -= slopeHslY * yC
					zC -= slopeDepthY * yC
					yC = 0.0f
				}
				if (yA < 0.0f) {
					xA -= slopeXBa * yA
					yA = 0.0f
				}
				if (slopeXCb < slopeXCa) {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xC.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXCb
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xA.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXCb
						xA += slopeXBa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xB.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXCb
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xA.toInt(), xB.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXCb
						xA += slopeXBa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xA = xC
				if (yC < 0.0f) {
					xA = xC - slopeXCb * yC
					xC -= slopeXCa * yC
					hslC -= slopeHslY * yC
					zC -= slopeDepthY * yC
					yC = 0.0f
				}
				if (yB < 0.0f) {
					xB -= slopeXBa * yB
					yB = 0.0f
				}
				if (slopeXCb < slopeXCa) {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xA.toInt(), xC.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xA += slopeXCb
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xC.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXBa
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xA.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xA += slopeXCb
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xB.toInt(), hslC, slopeHslX, zC, slopeDepthX)
						xB += slopeXBa
						xC += slopeXCa
						hslC += slopeHslY
						zC += slopeDepthY
						yC += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(Z)V")
	fun setWireframe(@OriginalArg(0) wireframe: Boolean) {
		this.wireframe = wireframe
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFFFFFIIIIIIII)V")
	fun renderTexturedTriangleRgb(@OriginalArg(0) yA: Float, @OriginalArg(1) yB: Float, @OriginalArg(2) yC: Float, @OriginalArg(3) xA: Float, @OriginalArg(4) xB: Float, @OriginalArg(5) xC: Float, @OriginalArg(6) zA: Float, @OriginalArg(7) zB: Float, @OriginalArg(8) zC: Float, @OriginalArg(9) uA: Float, @OriginalArg(10) uB: Float, @OriginalArg(11) uC: Float, @OriginalArg(12) vA: Float, @OriginalArg(13) vB: Float, @OriginalArg(14) vC: Float, @OriginalArg(15) colourA: Int, @OriginalArg(16) colourB: Int, @OriginalArg(17) colourC: Int, @OriginalArg(18) fogColour: Int, @OriginalArg(19) fogA: Int, @OriginalArg(20) fogB: Int, @OriginalArg(21) fogC: Int, @OriginalArg(22) texture: Int) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var uA: Float = uA
		var uB: Float = uB
		var uC: Float = uC
		var vA: Float = vA
		var vB: Float = vB
		var vC: Float = vC
		var fogA: Int = fogA
		var fogB: Int = fogB
		var fogC: Int = fogC
		if (texture != this.textureId) {
			this.texels = this.toolkit!!.getArgbTexture(texture)
			if (this.texels == null) {
				this.renderTriangleRgb(yA, yB, yC, xA, xB, xC, zA, zB, zC, ColorUtil.alphaBlend(colourA, fogColour or (fogA shl 24)), ColorUtil.alphaBlend(colourB, fogColour or (fogB shl 24)), ColorUtil.alphaBlend(colourC, fogColour or (fogC shl 24)))
				return
			}
			this.textureSize = if (this.toolkit!!.smallTexture(texture)) 64 else this.toolkit!!.textureSize
			this.textureMask = this.textureSize - 1
			this.textureBlendMode = this.toolkit!!.textureAlphaBlendMode(texture)
			this.textureRepeats = this.toolkit!!.textureRepeats(texture)
		}
		this.fogColour = fogColour
		@Pc(106) var alphaA: Float = ((colourA shr 24) and 0xFF).toFloat()
		@Pc(113) var alphaB: Float = ((colourB shr 24) and 0xFF).toFloat()
		@Pc(120) var alphaC: Float = ((colourC shr 24) and 0xFF).toFloat()
		@Pc(127) var redA: Float = ((colourA shr 16) and 0xFF).toFloat()
		@Pc(134) var redB: Float = ((colourB shr 16) and 0xFF).toFloat()
		@Pc(141) var redC: Float = ((colourC shr 16) and 0xFF).toFloat()
		@Pc(148) var greenA: Float = ((colourA shr 8) and 0xFF).toFloat()
		@Pc(155) var greenB: Float = ((colourB shr 8) and 0xFF).toFloat()
		@Pc(162) var greenC: Float = ((colourC shr 8) and 0xFF).toFloat()
		@Pc(167) var blueA: Float = (colourA and 0xFF).toFloat()
		@Pc(172) var blueB: Float = (colourB and 0xFF).toFloat()
		@Pc(177) var blueC: Float = (colourC and 0xFF).toFloat()
		uA /= zA
		uB /= zB
		uC /= zC
		vA /= zA
		vB /= zB
		vC /= zC
		zA = 1.0f / zA
		zB = 1.0f / zB
		zC = 1.0f / zC
		@Pc(215) var slopeXBa: Float = 0.0f
		@Pc(217) var slopeZBa: Float = 0.0f
		@Pc(219) var slopeUBa: Float = 0.0f
		@Pc(221) var slopeVBa: Float = 0.0f
		@Pc(223) var slopeFogBa: Float = 0.0f
		@Pc(225) var slopeAlphaBa: Float = 0.0f
		@Pc(227) var slopeRedBa: Float = 0.0f
		@Pc(229) var slopeGreenBa: Float = 0.0f
		@Pc(231) var slopeBlueBa: Float = 0.0f
		@Pc(239) var local239: Float
		if (yB != yA) {
			val deltaYBa: Float = yB - yA
			slopeXBa = (xB - xA) / deltaYBa
			slopeZBa = (zB - zA) / deltaYBa
			slopeUBa = (uB - uA) / deltaYBa
			slopeVBa = (vB - vA) / deltaYBa
			slopeFogBa = (fogB - fogA).toFloat() / deltaYBa
			slopeAlphaBa = (alphaB - alphaA) / deltaYBa
			slopeRedBa = (redB - redA) / deltaYBa
			slopeGreenBa = (greenB - greenA) / deltaYBa
			slopeBlueBa = (blueB - blueA) / deltaYBa
		}
		var slopeXCb: Float = 0.0f
		@Pc(298) var slopeZCb: Float = 0.0f
		@Pc(300) var slopeUCb: Float = 0.0f
		@Pc(302) var slopeVCb: Float = 0.0f
		@Pc(304) var slopeFogCb: Float = 0.0f
		@Pc(306) var slopeAlphaCb: Float = 0.0f
		@Pc(308) var slopeRedCb: Float = 0.0f
		@Pc(310) var slopeGreenCb: Float = 0.0f
		@Pc(312) var slopeBlueCb: Float = 0.0f
		@Pc(320) var local320: Float
		if (yC != yB) {
			val deltaYCb: Float = yC - yB
			slopeXCb = (xC - xB) / deltaYCb
			slopeZCb = (zC - zB) / deltaYCb
			slopeUCb = (uC - uB) / deltaYCb
			slopeVCb = (vC - vB) / deltaYCb
			slopeFogCb = (fogC - fogB).toFloat() / deltaYCb
			slopeAlphaCb = (alphaC - alphaB) / deltaYCb
			slopeRedCb = (redC - redB) / deltaYCb
			slopeGreenCb = (greenC - greenB) / deltaYCb
			slopeBlueCb = (blueC - blueB) / deltaYCb
		}
		var slopeXCa: Float = 0.0f
		@Pc(379) var slopeZCa: Float = 0.0f
		@Pc(381) var slopeUCa: Float = 0.0f
		@Pc(383) var slopeVCa: Float = 0.0f
		@Pc(385) var slopeFogCa: Float = 0.0f
		@Pc(387) var slopeAlphaCa: Float = 0.0f
		@Pc(389) var slopeRedCa: Float = 0.0f
		@Pc(391) var slopeGreenCa: Float = 0.0f
		@Pc(393) var slopeBlueCa: Float = 0.0f
		if (yA != yC) {
			@Pc(401) val deltaYAc: Float = yA - yC
			slopeXCa = (xA - xC) / deltaYAc
			slopeZCa = (zA - zC) / deltaYAc
			slopeUCa = (uA - uC) / deltaYAc
			slopeVCa = (vA - vC) / deltaYAc
			slopeFogCa = (fogA - fogC).toFloat() / deltaYAc
			slopeAlphaCa = (alphaA - alphaC) / deltaYAc
			slopeRedCa = (redA - redC) / deltaYAc
			slopeGreenCa = (greenA - greenC) / deltaYAc
			slopeBlueCa = (blueA - blueC) / deltaYAc
		}
		if (yA <= yB && yA <= yC) {
			this.renderTexturedTriangleRgbPart1(yA, yB, yC, xA, xB, xC, zA, zB, zC, uA, uB, uC, vA, vB, vC, fogA, fogB, fogC, alphaA, alphaB, alphaC, redA, redB, redC, greenA, greenB, greenC, blueA, blueB, blueC, slopeXBa, slopeZBa, slopeUBa, slopeVBa, slopeFogBa, slopeAlphaBa, slopeRedBa, slopeGreenBa, slopeBlueBa, slopeXCb, slopeZCb, slopeUCb, slopeVCb, slopeFogCb, slopeAlphaCb, slopeRedCb, slopeGreenCb, slopeBlueCb, slopeXCa, slopeZCa, slopeUCa, slopeVCa, slopeFogCa, slopeAlphaCa, slopeRedCa, slopeGreenCa, slopeBlueCa)
		} else if (yB <= yC) {
			this.renderTexturedTriangleRgbPart2(yA, yB, yC, xA, xB, xC, zA, zB, zC, uA, uB, uC, vA, vB, vC, fogA, fogB, fogC, alphaA, alphaB, alphaC, redA, redB, redC, greenA, greenB, greenC, blueA, blueB, blueC, slopeXBa, slopeZBa, slopeUBa, slopeVBa, slopeFogBa, slopeAlphaBa, slopeRedBa, slopeGreenBa, slopeBlueBa, slopeXCb, slopeZCb, slopeUCb, slopeVCb, slopeFogCb, slopeAlphaCb, slopeRedCb, slopeGreenCb, slopeBlueCb, slopeXCa, slopeZCa, slopeUCa, slopeVCa, slopeFogCa, slopeAlphaCa, slopeRedCa, slopeGreenCa, slopeBlueCa)
		} else if (!(yC >= this.height.toFloat())) {
			this.renderTexturedTriangleRgbPart3(yA, yB, yC, xA, xB, xC, zA, zB, zC, uA, uB, uC, vA, vB, vC, fogA, fogB, fogC, alphaA, alphaB, alphaC, redA, redB, redC, greenA, greenB, greenC, blueA, blueB, blueC, slopeXBa, slopeZBa, slopeUBa, slopeVBa, slopeFogBa, slopeAlphaBa, slopeRedBa, slopeGreenBa, slopeBlueBa, slopeXCb, slopeZCb, slopeUCb, slopeVCb, slopeFogCb, slopeAlphaCb, slopeRedCb, slopeGreenCb, slopeBlueCb, slopeXCa, slopeZCa, slopeUCa, slopeVCa, slopeFogCa, slopeAlphaCa, slopeRedCa, slopeGreenCa, slopeBlueCa)
		}
	}

	// Split out of renderTexturedTriangleRgb (the body of one of its branches, unchanged) so that neither method
	// exceeds HotSpot's 8000-byte limit for JIT compilation; the Kotlin version is larger than the Java one.
	@Suppress("NAME_SHADOWING", "UNUSED_VALUE", "UNUSED_VARIABLE")
	private fun renderTexturedTriangleRgbPart1(yA: Float, yB: Float, yC: Float, xA: Float, xB: Float, xC: Float, zA: Float, zB: Float, zC: Float, uA: Float, uB: Float, uC: Float, vA: Float, vB: Float, vC: Float, fogA: Int, fogB: Int, fogC: Int, alphaA: Float, alphaB: Float, alphaC: Float, redA: Float, redB: Float, redC: Float, greenA: Float, greenB: Float, greenC: Float, blueA: Float, blueB: Float, blueC: Float, slopeXBa: Float, slopeZBa: Float, slopeUBa: Float, slopeVBa: Float, slopeFogBa: Float, slopeAlphaBa: Float, slopeRedBa: Float, slopeGreenBa: Float, slopeBlueBa: Float, slopeXCb: Float, slopeZCb: Float, slopeUCb: Float, slopeVCb: Float, slopeFogCb: Float, slopeAlphaCb: Float, slopeRedCb: Float, slopeGreenCb: Float, slopeBlueCb: Float, slopeXCa: Float, slopeZCa: Float, slopeUCa: Float, slopeVCa: Float, slopeFogCa: Float, slopeAlphaCa: Float, slopeRedCa: Float, slopeGreenCa: Float, slopeBlueCa: Float) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var uA: Float = uA
		var uB: Float = uB
		var uC: Float = uC
		var vA: Float = vA
		var vB: Float = vB
		var vC: Float = vC
		var fogA: Int = fogA
		var fogB: Int = fogB
		var fogC: Int = fogC
		var alphaA: Float = alphaA
		var alphaB: Float = alphaB
		var alphaC: Float = alphaC
		var redA: Float = redA
		var redB: Float = redB
		var redC: Float = redC
		var greenA: Float = greenA
		var greenB: Float = greenB
		var greenC: Float = greenC
		var blueA: Float = blueA
		var blueB: Float = blueB
		var blueC: Float = blueC
		if (!(yA >= this.height.toFloat())) {
			if (yB > this.height.toFloat()) {
				yB = this.height.toFloat()
			}
			if (yC > this.height.toFloat()) {
				yC = this.height.toFloat()
			}
			if (yB < yC) {
				xC = xA
				zC = zA
				uC = uA
				vC = vA
				fogC = fogA
				alphaC = alphaA
				redC = redA
				greenC = greenA
				blueC = blueA
				if (yA < 0.0f) {
					xA -= slopeXBa * yA
					xC -= slopeXCa * yA
					zA -= slopeZBa * yA
					zC -= slopeZCa * yA
					uA -= slopeUBa * yA
					uC -= slopeUCa * yA
					vA -= slopeVBa * yA
					vC -= slopeVCa * yA
					fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
					fogC = (fogC.toFloat() - slopeFogCa * yA).toInt()
					alphaA -= slopeAlphaBa * yA
					alphaC -= slopeAlphaCa * yA
					redA -= slopeAlphaBa * yA
					redC -= slopeAlphaCa * yA
					greenA -= slopeAlphaBa * yA
					greenC -= slopeAlphaCa * yA
					blueA -= slopeAlphaBa * yA
					blueC -= slopeAlphaCa * yA
					yA = 0.0f
				}
				if (yB < 0.0f) {
					xB -= slopeXCb * yB
					zB -= slopeZCb * yB
					uB -= slopeUCb * yB
					vB -= slopeVCb * yB
					fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
					alphaB -= slopeAlphaCb * yB
					redB -= slopeRedCb * yB
					greenB -= slopeGreenCb * yB
					blueB -= slopeBlueCb * yB
					yB = 0.0f
				}
				if (yA != yB && slopeXCa < slopeXBa || yA == yB && slopeXCa > slopeXCb) {
					yC -= yB
					yB -= yA
					yA = this.lineOffsets[yA.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA)
						xA += slopeXBa
						xC += slopeXCa
						zA += slopeZBa
						zC += slopeZCa
						uA += slopeUBa
						uC += slopeUCa
						vA += slopeVBa
						vC += slopeVCa
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaA += slopeAlphaBa
						alphaC += slopeAlphaCa
						redA += slopeRedBa
						redC += slopeRedCa
						greenA += slopeGreenBa
						greenC += slopeGreenCa
						blueA += slopeBlueBa
						blueC += slopeBlueCa
						yA += this.surfaceWidth.toFloat()
					}
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB)
						xB += slopeXCb
						xC += slopeXCa
						zB += slopeZCb
						zC += slopeZCa
						uB += slopeUCb
						uC += slopeUCa
						vB += slopeVCb
						vC += slopeVCa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaB += slopeAlphaCb
						alphaC += slopeAlphaCa
						redB += slopeRedCb
						redC += slopeRedCa
						greenB += slopeGreenCb
						greenC += slopeGreenCa
						blueB += slopeBlueCb
						blueC += slopeBlueCa
						yA += this.surfaceWidth.toFloat()
					}
				} else {
					yC -= yB
					yB -= yA
					yA = this.lineOffsets[yA.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC)
						xA += slopeXBa
						xC += slopeXCa
						zA += slopeZBa
						zC += slopeZCa
						uA += slopeUBa
						uC += slopeUCa
						vA += slopeVBa
						vC += slopeVCa
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaA += slopeAlphaBa
						alphaC += slopeAlphaCa
						redA += slopeRedBa
						redC += slopeRedCa
						greenA += slopeGreenBa
						greenC += slopeGreenCa
						blueA += slopeBlueBa
						blueC += slopeBlueCa
						yA += this.surfaceWidth.toFloat()
					}
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC)
						xB += slopeXCb
						xC += slopeXCa
						zB += slopeZCb
						zC += slopeZCa
						uB += slopeUCb
						uC += slopeUCa
						vB += slopeVCb
						vC += slopeVCa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaB += slopeAlphaCb
						alphaC += slopeAlphaCa
						redB += slopeRedCb
						redC += slopeRedCa
						greenB += slopeGreenCb
						greenC += slopeGreenCa
						blueB += slopeBlueCb
						blueC += slopeBlueCa
						yA += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xB = xA
				zB = zA
				uB = uA
				vB = vA
				fogB = fogA
				alphaB = alphaA
				redB = redA
				greenB = greenA
				blueB = blueA
				if (yA < 0.0f) {
					xA -= slopeXBa * yA
					xB -= slopeXCa * yA
					zA -= slopeZBa * yA
					zB -= slopeZCa * yA
					uA -= slopeUBa * yA
					uB -= slopeUCa * yA
					vA -= slopeVBa * yA
					vB -= slopeVCa * yA
					fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
					fogB = (fogB.toFloat() - slopeFogCa * yA).toInt()
					alphaA -= slopeAlphaBa * yA
					alphaB -= slopeAlphaCa * yA
					redA -= slopeAlphaBa * yA
					redB -= slopeAlphaCa * yA
					greenA -= slopeAlphaBa * yA
					greenB -= slopeAlphaCa * yA
					blueA -= slopeAlphaBa * yA
					blueB -= slopeAlphaCa * yA
					yA = 0.0f
				}
				if (yC < 0.0f) {
					xC -= slopeXCb * yC
					zC -= slopeZCb * yC
					uC -= slopeUCb * yC
					vC -= slopeVCb * yC
					fogC = (fogC.toFloat() - slopeFogCb * yC).toInt()
					alphaC -= slopeAlphaCb * yC
					redC -= slopeRedCb * yC
					greenC -= slopeGreenCb * yC
					blueC -= slopeBlueCb * yC
					yC = 0.0f
				}
				if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
					yB -= yC
					yC -= yA
					yA = this.lineOffsets[yA.toInt()].toFloat()
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA)
						xA += slopeXBa
						xB += slopeXCa
						zA += slopeZBa
						zB += slopeZCa
						uA += slopeUBa
						uB += slopeUCa
						vA += slopeVBa
						vB += slopeVCa
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogB = (fogB.toFloat() + slopeFogCa).toInt()
						alphaA += slopeAlphaBa
						alphaB += slopeAlphaCa
						redA += slopeRedBa
						redB += slopeRedCa
						greenA += slopeGreenBa
						greenB += slopeGreenCa
						blueA += slopeBlueBa
						blueB += slopeBlueCa
						yA += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA)
						xC += slopeXCb
						xA += slopeXBa
						zC += slopeZCb
						zA += slopeZBa
						uC += slopeUCb
						uA += slopeUBa
						vC += slopeVCb
						vA += slopeVBa
						fogC = (fogC.toFloat() + slopeFogCb).toInt()
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						alphaC += slopeAlphaCb
						alphaA += slopeAlphaBa
						redC += slopeRedCb
						redA += slopeRedBa
						greenC += slopeGreenCb
						greenA += slopeGreenBa
						blueC += slopeBlueCb
						blueA += slopeBlueBa
						yA += this.surfaceWidth.toFloat()
					}
				} else {
					yB -= yC
					yC -= yA
					yA = this.lineOffsets[yA.toInt()].toFloat()
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB)
						xB += slopeXCa
						xA += slopeXBa
						zB += slopeZCa
						zA += slopeZBa
						uB += slopeUCa
						uA += slopeUBa
						vB += slopeVCa
						vA += slopeVBa
						fogB = (fogB.toFloat() + slopeFogCa).toInt()
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						alphaB += slopeAlphaCa
						alphaA += slopeAlphaBa
						redB += slopeRedCa
						redA += slopeRedBa
						greenB += slopeGreenCa
						greenA += slopeGreenBa
						blueB += slopeBlueCa
						blueA += slopeBlueBa
						yA += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yA.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC)
						xA += slopeXBa
						xC += slopeXCb
						zA += slopeZBa
						zC += slopeZCb
						uA += slopeUBa
						uC += slopeUCb
						vA += slopeVBa
						vC += slopeVCb
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogC = (fogC.toFloat() + slopeFogCb).toInt()
						alphaA += slopeAlphaBa
						alphaC += slopeAlphaCb
						redA += slopeRedBa
						redC += slopeRedCb
						greenA += slopeGreenBa
						greenC += slopeGreenCb
						blueA += slopeBlueBa
						blueC += slopeBlueCb
						yA += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	// Split out of renderTexturedTriangleRgb (the body of one of its branches, unchanged) so that neither method
	// exceeds HotSpot's 8000-byte limit for JIT compilation; the Kotlin version is larger than the Java one.
	@Suppress("NAME_SHADOWING", "UNUSED_VALUE", "UNUSED_VARIABLE")
	private fun renderTexturedTriangleRgbPart2(yA: Float, yB: Float, yC: Float, xA: Float, xB: Float, xC: Float, zA: Float, zB: Float, zC: Float, uA: Float, uB: Float, uC: Float, vA: Float, vB: Float, vC: Float, fogA: Int, fogB: Int, fogC: Int, alphaA: Float, alphaB: Float, alphaC: Float, redA: Float, redB: Float, redC: Float, greenA: Float, greenB: Float, greenC: Float, blueA: Float, blueB: Float, blueC: Float, slopeXBa: Float, slopeZBa: Float, slopeUBa: Float, slopeVBa: Float, slopeFogBa: Float, slopeAlphaBa: Float, slopeRedBa: Float, slopeGreenBa: Float, slopeBlueBa: Float, slopeXCb: Float, slopeZCb: Float, slopeUCb: Float, slopeVCb: Float, slopeFogCb: Float, slopeAlphaCb: Float, slopeRedCb: Float, slopeGreenCb: Float, slopeBlueCb: Float, slopeXCa: Float, slopeZCa: Float, slopeUCa: Float, slopeVCa: Float, slopeFogCa: Float, slopeAlphaCa: Float, slopeRedCa: Float, slopeGreenCa: Float, slopeBlueCa: Float) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var uA: Float = uA
		var uB: Float = uB
		var uC: Float = uC
		var vA: Float = vA
		var vB: Float = vB
		var vC: Float = vC
		var fogA: Int = fogA
		var fogB: Int = fogB
		var fogC: Int = fogC
		var alphaA: Float = alphaA
		var alphaB: Float = alphaB
		var alphaC: Float = alphaC
		var redA: Float = redA
		var redB: Float = redB
		var redC: Float = redC
		var greenA: Float = greenA
		var greenB: Float = greenB
		var greenC: Float = greenC
		var blueA: Float = blueA
		var blueB: Float = blueB
		var blueC: Float = blueC
		if (!(yB >= this.height.toFloat())) {
			if (yC > this.height.toFloat()) {
				yC = this.height.toFloat()
			}
			if (yA > this.height.toFloat()) {
				yA = this.height.toFloat()
			}
			if (yC < yA) {
				xA = xB
				zA = zB
				uA = uB
				vA = vB
				fogA = fogB
				alphaA = alphaB
				redA = redB
				greenA = greenB
				blueA = blueB
				if (yB < 0.0f) {
					xA = xB - slopeXBa * yB
					xB -= slopeXCb * yB
					zA = zB - slopeZBa * yB
					zB -= slopeZCb * yB
					uA = uB - slopeUBa * yB
					uB -= slopeUCb * yB
					vA = vB - slopeVBa * yB
					vB -= slopeVCb * yB
					fogA = (fogB.toFloat() - slopeFogBa * yB).toInt()
					fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
					alphaA = alphaB - slopeAlphaBa * yB
					alphaB -= slopeAlphaCb * yB
					redA = redB - slopeRedBa * yB
					redB -= slopeRedCb * yB
					greenA = greenB - slopeGreenBa * yB
					greenB -= slopeGreenCb * yB
					blueA = blueB - slopeBlueBa * yB
					blueB -= slopeBlueCb * yB
					yB = 0.0f
				}
				if (yC < 0.0f) {
					xC -= slopeXCa * yC
					zC -= slopeZCa * yC
					uC -= slopeUCa * yC
					vC -= slopeVCa * yC
					fogC = (fogC.toFloat() - slopeFogCa * yC).toInt()
					alphaC -= slopeAlphaCa * yC
					redC -= slopeRedCa * yC
					greenC -= slopeGreenCa * yC
					blueC -= slopeBlueCa * yC
					yC = 0.0f
				}
				if ((yB == yC || !(slopeXBa < slopeXCb)) && (yB != yC || !(slopeXBa > slopeXCa))) {
					yA -= yC
					yC -= yB
					yB = this.lineOffsets[yB.toInt()].toFloat()
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA)
						xB += slopeXCb
						xA += slopeXBa
						zB += slopeZCb
						zA += slopeZBa
						uB += slopeUCb
						uA += slopeUBa
						vB += slopeVCb
						vA += slopeVBa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						alphaB += slopeAlphaCb
						alphaA += slopeAlphaBa
						redB += slopeRedCb
						redA += slopeRedBa
						greenB += slopeGreenCb
						greenA += slopeGreenBa
						blueB += slopeBlueCb
						blueA += slopeBlueBa
						yB += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA)
						xC += slopeXCa
						xA += slopeXBa
						zC += slopeZCa
						zA += slopeZBa
						uC += slopeUCa
						uA += slopeUBa
						vC += slopeVCa
						vA += slopeVBa
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						alphaC += slopeAlphaCa
						alphaA += slopeAlphaBa
						redC += slopeRedCa
						redA += slopeRedBa
						greenC += slopeGreenCa
						greenA += slopeGreenBa
						blueC += slopeBlueCa
						blueA += slopeBlueBa
						yB += this.surfaceWidth.toFloat()
					}
				} else {
					yA -= yC
					yC -= yB
					yB = this.lineOffsets[yB.toInt()].toFloat()
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB)
						xA += slopeXBa
						xB += slopeXCb
						zA += slopeZBa
						zB += slopeZCb
						uA += slopeUBa
						uB += slopeUCb
						vA += slopeVBa
						vB += slopeVCb
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						alphaA += slopeAlphaBa
						alphaB += slopeAlphaCb
						redA += slopeRedBa
						redB += slopeRedCb
						greenA += slopeGreenBa
						greenB += slopeGreenCb
						blueA += slopeBlueBa
						blueB += slopeBlueCb
						yB += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC)
						xA += slopeXBa
						xC += slopeXCa
						zA += slopeZBa
						zC += slopeZCa
						uA += slopeUBa
						uC += slopeUCa
						vA += slopeVBa
						vC += slopeVCa
						fogA = (fogA.toFloat() + slopeFogBa).toInt()
						fogC = (fogC.toFloat() + slopeFogCa).toInt()
						alphaA += slopeAlphaBa
						alphaC += slopeAlphaCa
						redA += slopeRedBa
						redC += slopeRedCa
						greenA += slopeGreenBa
						greenC += slopeGreenCa
						blueA += slopeBlueBa
						blueC += slopeBlueCa
						yB += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xC = xB
				zC = zB
				uC = uB
				vC = vB
				fogC = fogB
				alphaC = alphaB
				redC = redB
				greenC = greenB
				blueC = blueB
				if (yB < 0.0f) {
					xC = xB - slopeXBa * yB
					xB -= slopeXCb * yB
					zC = zB - slopeZBa * yB
					zB -= slopeZCb * yB
					uC = uB - slopeUBa * yB
					uB -= slopeUCb * yB
					vC = vB - slopeVBa * yB
					vB -= slopeVCb * yB
					fogC = (fogB.toFloat() - slopeFogBa * yB).toInt()
					fogB = (fogB.toFloat() - slopeFogCb * yB).toInt()
					alphaC = alphaB - slopeAlphaBa * yB
					alphaB -= slopeAlphaCb * yB
					redC = redB - slopeRedBa * yB
					redB -= slopeRedCb * yB
					greenC = greenB - slopeGreenBa * yB
					greenB -= slopeGreenCb * yB
					blueC = blueB - slopeBlueBa * yB
					blueB -= slopeBlueCb * yB
					yB = 0.0f
				}
				if (yA < 0.0f) {
					xA -= slopeXCa * yA
					zA -= slopeZCa * yA
					uA -= slopeUCa * yA
					vA -= slopeVCa * yA
					fogA = (fogA.toFloat() - slopeFogCa * yA).toInt()
					alphaA -= slopeAlphaCa * yA
					redA -= slopeRedCa * yA
					greenA -= slopeGreenCa * yA
					blueA -= slopeBlueCa * yA
					yA = 0.0f
				}
				yC -= yA
				yA -= yB
				yB = this.lineOffsets[yB.toInt()].toFloat()
				if (slopeXBa < slopeXCb) {
					while (--yA >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB)
						xC += slopeXBa
						xB += slopeXCb
						zC += slopeZBa
						zB += slopeZCb
						uC += slopeUBa
						uB += slopeUCb
						vC += slopeVBa
						vB += slopeVCb
						fogC = (fogC.toFloat() + slopeFogBa).toInt()
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						alphaC += slopeAlphaBa
						alphaB += slopeAlphaCb
						redC += slopeRedBa
						redB += slopeRedCb
						greenC += slopeGreenBa
						greenB += slopeGreenCb
						blueC += slopeBlueBa
						blueB += slopeBlueCb
						yB += this.surfaceWidth.toFloat()
					}
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB)
						xA += slopeXCa
						xB += slopeXCb
						zA += slopeZCa
						zB += slopeZCb
						uA += slopeUCa
						uB += slopeUCb
						vA += slopeVCa
						vB += slopeVCb
						fogA = (fogA.toFloat() + slopeFogCa).toInt()
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						alphaA += slopeAlphaCa
						alphaB += slopeAlphaCb
						redA += slopeRedCa
						redB += slopeRedCb
						greenA += slopeGreenCa
						greenB += slopeGreenCb
						blueA += slopeBlueCa
						blueB += slopeBlueCb
						yB += this.surfaceWidth.toFloat()
					}
				} else {
					while (--yA >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC)
						xB += slopeXCb
						xC += slopeXBa
						zB += slopeZCb
						zC += slopeZBa
						uB += slopeUCb
						uC += slopeUBa
						vB += slopeVCb
						vC += slopeVBa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogC = (fogC.toFloat() + slopeFogBa).toInt()
						alphaB += slopeAlphaCb
						alphaC += slopeAlphaBa
						redB += slopeRedCb
						redC += slopeRedBa
						greenB += slopeGreenCb
						greenC += slopeGreenBa
						blueB += slopeBlueCb
						blueC += slopeBlueBa
						yB += this.surfaceWidth.toFloat()
					}
					while (--yC >= 0.0f) {
						this.drawTexturedSpanRgb(this.raster, this.texels, yB.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA)
						xB += slopeXCb
						xA += slopeXCa
						zB += slopeZCb
						zA += slopeZCa
						uB += slopeUCb
						uA += slopeUCa
						vB += slopeVCb
						vA += slopeVCa
						fogB = (fogB.toFloat() + slopeFogCb).toInt()
						fogA = (fogA.toFloat() + slopeFogCa).toInt()
						alphaB += slopeAlphaCb
						alphaA += slopeAlphaCa
						redB += slopeRedCb
						redA += slopeRedCa
						greenB += slopeGreenCb
						greenA += slopeGreenCa
						blueB += slopeBlueCb
						blueA += slopeBlueCa
						yB += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	// Split out of renderTexturedTriangleRgb (the body of one of its branches, unchanged) so that neither method
	// exceeds HotSpot's 8000-byte limit for JIT compilation; the Kotlin version is larger than the Java one.
	@Suppress("NAME_SHADOWING", "UNUSED_VALUE", "UNUSED_VARIABLE")
	private fun renderTexturedTriangleRgbPart3(yA: Float, yB: Float, yC: Float, xA: Float, xB: Float, xC: Float, zA: Float, zB: Float, zC: Float, uA: Float, uB: Float, uC: Float, vA: Float, vB: Float, vC: Float, fogA: Int, fogB: Int, fogC: Int, alphaA: Float, alphaB: Float, alphaC: Float, redA: Float, redB: Float, redC: Float, greenA: Float, greenB: Float, greenC: Float, blueA: Float, blueB: Float, blueC: Float, slopeXBa: Float, slopeZBa: Float, slopeUBa: Float, slopeVBa: Float, slopeFogBa: Float, slopeAlphaBa: Float, slopeRedBa: Float, slopeGreenBa: Float, slopeBlueBa: Float, slopeXCb: Float, slopeZCb: Float, slopeUCb: Float, slopeVCb: Float, slopeFogCb: Float, slopeAlphaCb: Float, slopeRedCb: Float, slopeGreenCb: Float, slopeBlueCb: Float, slopeXCa: Float, slopeZCa: Float, slopeUCa: Float, slopeVCa: Float, slopeFogCa: Float, slopeAlphaCa: Float, slopeRedCa: Float, slopeGreenCa: Float, slopeBlueCa: Float) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		var uA: Float = uA
		var uB: Float = uB
		var uC: Float = uC
		var vA: Float = vA
		var vB: Float = vB
		var vC: Float = vC
		var fogA: Int = fogA
		var fogB: Int = fogB
		var fogC: Int = fogC
		var alphaA: Float = alphaA
		var alphaB: Float = alphaB
		var alphaC: Float = alphaC
		var redA: Float = redA
		var redB: Float = redB
		var redC: Float = redC
		var greenA: Float = greenA
		var greenB: Float = greenB
		var greenC: Float = greenC
		var blueA: Float = blueA
		var blueB: Float = blueB
		var blueC: Float = blueC
		if (yA > this.height.toFloat()) {
			yA = this.height.toFloat()
		}
		if (yB > this.height.toFloat()) {
			yB = this.height.toFloat()
		}
		if (yA < yB) {
			xB = xC
			zB = zC
			uB = uC
			vB = vC
			fogB = fogC
			alphaB = alphaC
			redB = redC
			greenB = greenC
			blueB = blueC
			if (yC < 0.0f) {
				xC -= slopeXCa * yC
				xB -= slopeXCb * yC
				zC -= slopeZCa * yC
				zB -= slopeZCb * yC
				uC -= slopeUCa * yC
				uB -= slopeUCb * yC
				vC -= slopeVCa * yC
				vB -= slopeVCb * yC
				fogC = (fogC.toFloat() - slopeFogCa * 3.0f).toInt()
				fogB = (fogB.toFloat() - slopeFogCb * yC).toInt()
				alphaC -= slopeAlphaCa * yC
				alphaB -= slopeAlphaCb * yC
				redC -= slopeRedCa * yC
				redB -= slopeRedCb * yC
				greenC -= slopeGreenCa * yC
				greenB -= slopeGreenCb * yC
				blueC -= slopeBlueCa * yC
				blueB -= slopeBlueCb * yC
				yC = 0.0f
			}
			if (yA < 0.0f) {
				xA -= slopeXBa * yA
				zA -= slopeZBa * yA
				uA -= slopeUBa * yA
				vA -= slopeVBa * yA
				fogA = (fogA.toFloat() - slopeFogBa * yA).toInt()
				alphaA -= slopeAlphaBa * yA
				redA -= slopeRedBa * yA
				greenA -= slopeGreenBa * yA
				blueA -= slopeBlueBa * yA
				yA = 0.0f
			}
			if (slopeXCb < slopeXCa) {
				yB -= yA
				yA -= yC
				yC = this.lineOffsets[yC.toInt()].toFloat()
				while (--yA >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC)
					xB += slopeXCb
					xC += slopeXCa
					zB += slopeZCb
					zC += slopeZCa
					uB += slopeUCb
					uC += slopeUCa
					vB += slopeVCb
					vC += slopeVCa
					fogB = (fogB.toFloat() + slopeFogCb).toInt()
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					alphaB += slopeAlphaCb
					alphaC += slopeAlphaCa
					redB += slopeRedCb
					redC += slopeRedCa
					greenB += slopeGreenCb
					greenC += slopeGreenCa
					blueB += slopeBlueCb
					blueC += slopeBlueCa
					yC += this.surfaceWidth.toFloat()
				}
				while (--yB >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xB.toInt(), xA.toInt(), zB, zA, uB, uA, vB, vA, fogB.toFloat(), fogA.toFloat(), alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA)
					xB += slopeXCb
					xA += slopeXBa
					zB += slopeZCb
					zA += slopeZBa
					uB += slopeUCb
					uA += slopeUBa
					vB += slopeVCb
					vA += slopeVBa
					fogB = (fogB.toFloat() + slopeFogCb).toInt()
					fogA = (fogA.toFloat() + slopeFogBa).toInt()
					alphaB += slopeAlphaCb
					alphaA += slopeAlphaBa
					redB += slopeRedCb
					redA += slopeRedBa
					greenB += slopeGreenCb
					greenA += slopeGreenBa
					blueB += slopeBlueCb
					blueA += slopeBlueBa
					yC += this.surfaceWidth.toFloat()
				}
			} else {
				yB -= yA
				yA -= yC
				yC = this.lineOffsets[yC.toInt()].toFloat()
				while (--yA >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB)
					xC += slopeXCa
					xB += slopeXCb
					zC += slopeZCa
					zB += slopeZCb
					uC += slopeUCa
					uB += slopeUCb
					vC += slopeVCa
					vB += slopeVCb
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					fogB = (fogB.toFloat() + slopeFogCb).toInt()
					alphaC += slopeAlphaCa
					alphaB += slopeAlphaCb
					redC += slopeRedCa
					redB += slopeRedCb
					greenC += slopeGreenCa
					greenB += slopeGreenCb
					blueC += slopeBlueCa
					blueB += slopeBlueCb
					yC += this.surfaceWidth.toFloat()
				}
				while (--yB >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xA.toInt(), xB.toInt(), zA, zB, uA, uB, vA, vB, fogA.toFloat(), fogB.toFloat(), alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB)
					xA += slopeXBa
					xB += slopeXCb
					zA += slopeZBa
					zB += slopeZCb
					uA += slopeUBa
					uB += slopeUCb
					vA += slopeVBa
					vB += slopeVCb
					fogA = (fogA.toFloat() + slopeFogBa).toInt()
					fogB = (fogB.toFloat() + slopeFogCb).toInt()
					alphaA += slopeAlphaBa
					alphaB += slopeAlphaCb
					redA += slopeRedBa
					redB += slopeRedCb
					greenA += slopeGreenBa
					greenB += slopeGreenCb
					blueA += slopeBlueBa
					blueB += slopeBlueCb
					yC += this.surfaceWidth.toFloat()
				}
			}
		} else {
			xA = xC
			zA = zC
			uA = uC
			vA = vC
			fogA = fogC
			alphaA = alphaC
			redA = redC
			greenA = greenC
			blueA = blueC
			if (yC < 0.0f) {
				xC -= slopeXCa * yC
				xA -= slopeXCb * yC
				zC -= slopeZCa * yC
				zA -= slopeZCb * yC
				uC -= slopeUCa * yC
				uA -= slopeUCb * yC
				vC -= slopeVCa * yC
				vA -= slopeVCb * yC
				fogC = (fogC.toFloat() - slopeFogCa * 3.0f).toInt()
				fogA = (fogA.toFloat() - slopeFogCb * yC).toInt()
				alphaC -= slopeAlphaCa * yC
				alphaA -= slopeAlphaCb * yC
				redC -= slopeRedCa * yC
				redA -= slopeRedCb * yC
				greenC -= slopeGreenCa * yC
				greenA -= slopeGreenCb * yC
				blueC -= slopeBlueCa * yC
				blueA -= slopeBlueCb * yC
				yC = 0.0f
			}
			if (yB < 0.0f) {
				xB -= slopeXBa * yB
				zB -= slopeZBa * yB
				uB -= slopeUBa * yB
				vB -= slopeVBa * yB
				fogB = (fogB.toFloat() - slopeFogBa * yB).toInt()
				alphaB -= slopeAlphaBa * yB
				redB -= slopeRedBa * yB
				greenB -= slopeGreenBa * yB
				blueB -= slopeBlueBa * yB
				yB = 0.0f
			}
			if (slopeXCb < slopeXCa) {
				yA -= yB
				yB -= yC
				yC = this.lineOffsets[yC.toInt()].toFloat()
				while (--yB >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xA.toInt(), xC.toInt(), zA, zC, uA, uC, vA, vC, fogA.toFloat(), fogC.toFloat(), alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC)
					xA += slopeXCb
					xC += slopeXCa
					zA += slopeZCb
					zC += slopeZCa
					uA += slopeUCb
					uC += slopeUCa
					vA += slopeVCb
					vC += slopeVCa
					fogA = (fogA.toFloat() + slopeFogCb).toInt()
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					alphaA += slopeAlphaCb
					alphaC += slopeAlphaCa
					redA += slopeRedCb
					redC += slopeRedCa
					greenA += slopeGreenCb
					greenC += slopeGreenCa
					blueA += slopeBlueCb
					blueC += slopeBlueCa
					yC += this.surfaceWidth.toFloat()
				}
				while (--yA >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xB.toInt(), xC.toInt(), zB, zC, uB, uC, vB, vC, fogB.toFloat(), fogC.toFloat(), alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC)
					xB += slopeXBa
					xC += slopeXCa
					zB += slopeZBa
					zC += slopeZCa
					uB += slopeUBa
					uC += slopeUCa
					vB += slopeVBa
					vC += slopeVCa
					fogB = (fogB.toFloat() + slopeFogBa).toInt()
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					alphaB += slopeAlphaBa
					alphaC += slopeAlphaCa
					redB += slopeRedBa
					redC += slopeRedCa
					greenB += slopeGreenBa
					greenC += slopeGreenCa
					blueB += slopeBlueBa
					blueC += slopeBlueCa
					yC += this.surfaceWidth.toFloat()
				}
			} else {
				yA -= yB
				yB -= yC
				yC = this.lineOffsets[yC.toInt()].toFloat()
				while (--yB >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xC.toInt(), xA.toInt(), zC, zA, uC, uA, vC, vA, fogC.toFloat(), fogA.toFloat(), alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA)
					xC += slopeXCa
					xA += slopeXCb
					zC += slopeZCa
					zA += slopeZCb
					uC += slopeUCa
					uA += slopeUCb
					vC += slopeVCa
					vA += slopeVCb
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					fogA = (fogA.toFloat() + slopeFogCb).toInt()
					alphaC += slopeAlphaCa
					alphaA += slopeAlphaCb
					redC += slopeRedCa
					redA += slopeRedCb
					greenC += slopeGreenCa
					greenA += slopeGreenCb
					blueC += slopeBlueCa
					blueA += slopeBlueCb
					yC += this.surfaceWidth.toFloat()
				}
				while (--yA >= 0.0f) {
					this.drawTexturedSpanRgb(this.raster, this.texels, yC.toInt(), xC.toInt(), xB.toInt(), zC, zB, uC, uB, vC, vB, fogC.toFloat(), fogB.toFloat(), alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB)
					xC += slopeXCa
					xB += slopeXBa
					zC += slopeZCa
					zB += slopeZBa
					uC += slopeUCa
					uB += slopeUBa
					vC += slopeVCa
					vB += slopeVBa
					fogC = (fogC.toFloat() + slopeFogCa).toInt()
					fogB = (fogB.toFloat() + slopeFogBa).toInt()
					alphaC += slopeAlphaCa
					alphaB += slopeAlphaBa
					redC += slopeRedCa
					redB += slopeRedBa
					greenC += slopeGreenCa
					greenB += slopeGreenBa
					blueC += slopeBlueCa
					blueB += slopeBlueBa
					yC += this.surfaceWidth.toFloat()
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[IIIIFFFFFFFFFFFFFFFFFFFF)V")
	private fun drawBlendedTexturedSpan(@OriginalArg(0) dst: IntArray?, @OriginalArg(1) texels: IntArray?, @OriginalArg(2) rowOffset: Int, @OriginalArg(3) startX: Int, @OriginalArg(4) endX: Int, @OriginalArg(5) invZLeft: Float, @OriginalArg(6) invZRight: Float, @OriginalArg(7) uLeft: Float, @OriginalArg(8) uRight: Float, @OriginalArg(9) vLeft: Float, @OriginalArg(10) vRight: Float, @OriginalArg(11) fogLeft: Float, @OriginalArg(12) fogRight: Float, @OriginalArg(13) alphaLeft: Float, @OriginalArg(14) alphaRight: Float, @OriginalArg(15) redLeft: Float, @OriginalArg(16) redRight: Float, @OriginalArg(17) greenLeft: Float, @OriginalArg(18) greenRight: Float, @OriginalArg(19) blueLeft: Float, @OriginalArg(20) blueRight: Float, @OriginalArg(21) weightLeft: Float, @OriginalArg(22) weightRight: Float, @OriginalArg(23) weight2Left: Float, @OriginalArg(24) weight2Right: Float) {
		var startX: Int = startX
		var endX: Int = endX
		var invZLeft: Float = invZLeft
		var uLeft: Float = uLeft
		var vLeft: Float = vLeft
		var fogLeft: Float = fogLeft
		var redLeft: Float = redLeft
		var greenLeft: Float = greenLeft
		var blueLeft: Float = blueLeft
		var weightLeft: Float = weightLeft
		var weight2Left: Float = weight2Left
		@Pc(3) var lengthX: Int = endX - startX
		@Pc(8) val scaleX: Float = 1.0f / lengthX.toFloat()
		@Pc(14) val slopeInvZ: Float = (invZRight - invZLeft) * scaleX
		@Pc(20) val slopeU: Float = (uRight - uLeft) * scaleX
		@Pc(26) val slopeV: Float = (vRight - vLeft) * scaleX
		@Pc(32) val slopeFog: Float = (fogRight - fogLeft) * scaleX
		@Pc(38) val slopeRed: Float = (redRight - redLeft) * scaleX
		@Pc(44) val slopeGreen: Float = (greenRight - greenLeft) * scaleX
		@Pc(50) val slopeBlue: Float = (blueRight - blueLeft) * scaleX
		@Pc(56) val slopeWeight: Float = (weightRight - weightLeft) * scaleX
		@Pc(62) val slopeWeight2: Float = (weight2Right - weight2Left) * scaleX
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width
			}
			if (startX < 0) {
				invZLeft -= slopeInvZ * startX.toFloat()
				uLeft -= slopeU * startX.toFloat()
				vLeft -= slopeV * startX.toFloat()
				fogLeft -= slopeFog * startX.toFloat()
				redLeft -= slopeRed * startX.toFloat()
				greenLeft -= slopeGreen * startX.toFloat()
				blueLeft -= slopeBlue * startX.toFloat()
				weightLeft -= slopeWeight * startX.toFloat()
				weight2Left -= slopeWeight2 * startX.toFloat()
				startX = 0
			}
		}
		if (startX >= endX) {
			return
		}
		lengthX = endX - startX
		@Pc(151) var pixelIndex: Int = rowOffset + startX
		while (lengthX-- > 0) {
			@Pc(156) val depth: Float = 1.0f / invZLeft
			if (depth < this.depthBuffer!![pixelIndex]) {
				@Pc(167) val u: Float = uLeft * depth
				@Pc(171) val v: Float = vLeft * depth
				@Pc(184) val texU: Int = (u * this.textureSize.toFloat() * this.textureScale).toInt() and this.textureMask
				@Pc(197) val texV: Int = (v * this.textureSize.toFloat() * this.textureScale).toInt() and this.textureMask
				@Pc(207) val texel: Int = this.texels!![texV * this.textureSize + texU]
				@Pc(220) val texU2: Int = (u * this.textureSize2.toFloat() * this.textureScale2).toInt() and this.textureMask2
				@Pc(233) val texV2: Int = (v * this.textureSize2.toFloat() * this.textureScale2).toInt() and this.textureMask2
				@Pc(243) val texel2: Int = this.texels2!![texV2 * this.textureSize2 + texU2]
				@Pc(256) val texU3: Int = (u * this.textureSize3.toFloat() * this.textureScale3).toInt() and this.textureMask3
				@Pc(269) val texV3: Int = (v * this.textureSize3.toFloat() * this.textureScale3).toInt() and this.textureMask3
				@Pc(279) val texel3: Int = this.texels3!![texV3 * this.textureSize3 + texU3]
				@Pc(285) val weight3: Float = 1.0f - weightLeft - weight2Left
				@Pc(319) val weighted: Int = (((weightLeft * ((texel shr 16) and 0xFF).toFloat()).toInt() or 0xFF00) shl 16) or ((weightLeft * ((texel shr 8) and 0xFF).toFloat()).toInt() shl 8) or (weightLeft * (texel and 0xFF).toFloat()).toInt()
				@Pc(353) val weighted2: Int = (((weight2Left * ((texel2 shr 16) and 0xFF).toFloat()).toInt() or 0xFF00) shl 16) or ((weight2Left * ((texel2 shr 8) and 0xFF).toFloat()).toInt() shl 8) or (weight2Left * (texel2 and 0xFF).toFloat()).toInt()
				@Pc(387) val weighted3: Int = (((weight3 * ((texel3 shr 16) and 0xFF).toFloat()).toInt() or 0xFF00) shl 16) or ((weight3 * ((texel3 shr 8) and 0xFF).toFloat()).toInt() shl 8) or (weight3 * (texel3 and 0xFF).toFloat()).toInt()
				@Pc(393) val blendedTexel: Int = weighted + weighted2 + weighted3
				@Pc(431) var colour: Int = ((((redLeft * ((blendedTexel shr 16) and 0xFF).toFloat()).toInt() and 0xFF00) or 0xFF0000) shl 8) or ((greenLeft * ((blendedTexel shr 8) and 0xFF).toFloat()).toInt() and 0xFF00) or ((blueLeft * (blendedTexel and 0xFF).toFloat()).toInt() shr 8)
				if (fogLeft != 0.0f) {
					@Pc(440) val invFog: Int = (255.0f - fogLeft).toInt()
					@Pc(462) val fogTint: Int = ((((this.fogColour and 0xFF00FF) * fogLeft.toInt()) and 0xFF00FF00.toInt()) or (((this.fogColour and 0xFF00) * fogLeft.toInt()) and 0xFF0000)) ushr 8
					colour = (((((colour and 0xFF00FF) * invFog) and 0xFF00FF00.toInt()) or (((colour and 0xFF00) * invFog) and 0xFF0000)) ushr 8) + fogTint
				}
				dst!![pixelIndex] = colour
				this.depthBuffer!![pixelIndex] = depth
			}
			pixelIndex++
			invZLeft += slopeInvZ
			uLeft += slopeU
			vLeft += slopeV
			fogLeft += slopeFog
			redLeft += slopeRed
			greenLeft += slopeGreen
			blueLeft += slopeBlue
			weightLeft += slopeWeight
			weight2Left += slopeWeight2
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFF)V")
	private fun drawFlatSpanRgb(@OriginalArg(0) dst: IntArray?, @OriginalArg(1) depths: FloatArray?, @OriginalArg(2) rowOffset: Int, @OriginalArg(3) colour: Int, @OriginalArg(5) startX: Int, @OriginalArg(6) endX: Int, @OriginalArg(7) z: Float, @OriginalArg(8) zStep: Float) {
		var rowOffset: Int = rowOffset
		var startX: Int = startX
		var endX: Int = endX
		var z: Float = z
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width
			}
			if (startX < 0) {
				startX = 0
			}
		}
		if (startX >= endX) {
			return
		}
		rowOffset += startX - 1
		@Pc(29) var remaining: Int = (endX - startX) shr 2
		z += zStep * startX.toFloat()
		@Pc(278) var alpha: Int
		@Pc(283) var invAlpha: Int
		@Pc(315) var background: Int
		@Pc(47) var index: Int
		@Pc(303) var scaledColour: Int
		@Pc(63) var nextZ: Float
		if (this.threadResource!!.zWrite) {
			if (this.alpha == 0) {
				while (true) {
					remaining--
					if (remaining < 0) {
						remaining = (endX - startX) and 0x3
						while (true) {
							remaining--
							if (remaining < 0) {
								return
							}
							rowOffset++
							if (z < depths!![rowOffset]) {
								dst!![rowOffset] = colour
								depths!![rowOffset] = z
							}
							z += zStep
						}
					}
					index = rowOffset + 1
					if (z < depths!![index]) {
						dst!![index] = colour
						depths!![index] = z
					}
					nextZ = z + zStep
					index++
					if (nextZ < depths!![index]) {
						dst!![index] = colour
						depths!![index] = nextZ
					}
					nextZ += zStep
					index++
					if (nextZ < depths!![index]) {
						dst!![index] = colour
						depths!![index] = nextZ
					}
					nextZ += zStep
					rowOffset = index + 1
					if (nextZ < depths!![rowOffset]) {
						dst!![rowOffset] = colour
						depths!![rowOffset] = nextZ
					}
					z = nextZ + zStep
				}
			} else if (this.alpha != 254) {
				alpha = this.alpha
				invAlpha = 256 - this.alpha
				scaledColour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
				while (true) {
					remaining--
					if (remaining < 0) {
						remaining = (endX - startX) and 0x3
						while (true) {
							remaining--
							if (remaining < 0) {
								return
							}
							rowOffset++
							if (z < depths!![rowOffset]) {
								background = dst!![rowOffset]
								dst!![rowOffset] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
								depths!![rowOffset] = z
							}
							z += zStep
						}
					}
					index = rowOffset + 1
					if (z < depths!![index]) {
						background = dst!![index]
						dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						depths!![index] = z
					}
					nextZ = z + zStep
					index++
					if (nextZ < depths!![index]) {
						background = dst!![index]
						dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						depths!![index] = nextZ
					}
					nextZ += zStep
					index++
					if (nextZ < depths!![index]) {
						background = dst!![index]
						dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						depths!![index] = nextZ
					}
					nextZ += zStep
					rowOffset = index + 1
					if (nextZ < depths!![rowOffset]) {
						background = dst!![rowOffset]
						dst!![rowOffset] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						depths!![rowOffset] = nextZ
					}
					z = nextZ + zStep
				}
			} else if (startX != 0 && endX <= this.width - 1) {
				while (true) {
					remaining--
					if (remaining < 0) {
						remaining = (endX - startX) and 0x3
						while (true) {
							remaining--
							if (remaining < 0) {
								return
							}
							rowOffset++
							if (z < depths!![rowOffset]) {
								dst!![rowOffset - 1] = dst!![rowOffset]
							}
							z += zStep
						}
					}
					index = rowOffset + 1
					if (z < depths!![index]) {
						dst!![index - 1] = dst!![index]
					}
					nextZ = z + zStep
					index++
					if (nextZ < depths!![index]) {
						dst!![index - 1] = dst!![index]
					}
					nextZ += zStep
					index++
					if (nextZ < depths!![index]) {
						dst!![index - 1] = dst!![index]
					}
					nextZ += zStep
					rowOffset = index + 1
					if (nextZ < depths!![rowOffset]) {
						dst!![rowOffset - 1] = dst!![rowOffset]
					}
					z = nextZ + zStep
				}
			}
		} else if (this.alpha == 0) {
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return
						}
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset] = colour
						}
						z += zStep
					}
				}
				index = rowOffset + 1
				if (z < depths!![index]) {
					dst!![index] = colour
				}
				nextZ = z + zStep
				index++
				if (nextZ < depths!![index]) {
					dst!![index] = colour
				}
				nextZ += zStep
				index++
				if (nextZ < depths!![index]) {
					dst!![index] = colour
				}
				nextZ += zStep
				rowOffset = index + 1
				if (nextZ < depths!![rowOffset]) {
					dst!![rowOffset] = colour
				}
				z = nextZ + zStep
			}
		} else if (this.alpha != 254) {
			alpha = this.alpha
			invAlpha = 256 - this.alpha
			scaledColour = ((((colour and 0xFF00FF) * invAlpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * invAlpha) shr 8) and 0xFF00)
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return
						}
						rowOffset++
						if (z < depths!![rowOffset]) {
							background = dst!![rowOffset]
							dst!![rowOffset] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
						}
						z += zStep
					}
				}
				index = rowOffset + 1
				if (z < depths!![index]) {
					background = dst!![index]
					dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
				}
				nextZ = z + zStep
				index++
				if (nextZ < depths!![index]) {
					background = dst!![index]
					dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
				}
				nextZ += zStep
				index++
				if (nextZ < depths!![index]) {
					background = dst!![index]
					dst!![index] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
				}
				nextZ += zStep
				rowOffset = index + 1
				if (nextZ < depths!![rowOffset]) {
					background = dst!![rowOffset]
					dst!![rowOffset] = scaledColour + ((((background and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((background and 0xFF00) * alpha) shr 8) and 0xFF00)
				}
				z = nextZ + zStep
			}
		} else if (startX != 0 && endX <= this.width - 1) {
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return
						}
						rowOffset++
						if (z < depths!![rowOffset]) {
							dst!![rowOffset - 1] = dst!![rowOffset]
						}
						z += zStep
					}
				}
				index = rowOffset + 1
				if (z < depths!![index]) {
					dst!![index - 1] = dst!![index]
				}
				nextZ = z + zStep
				index++
				if (nextZ < depths!![index]) {
					dst!![index - 1] = dst!![index]
				}
				nextZ += zStep
				index++
				if (nextZ < depths!![index]) {
					dst!![index - 1] = dst!![index]
				}
				nextZ += zStep
				rowOffset = index + 1
				if (nextZ < depths!![rowOffset]) {
					dst!![rowOffset - 1] = dst!![rowOffset]
				}
				z = nextZ + zStep
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFIII)V")
	fun renderTriangleRgb(@OriginalArg(0) yA: Float, @OriginalArg(1) yB: Float, @OriginalArg(2) yC: Float, @OriginalArg(3) xA: Float, @OriginalArg(4) xB: Float, @OriginalArg(5) xC: Float, @OriginalArg(6) zA: Float, @OriginalArg(7) zB: Float, @OriginalArg(8) zC: Float, @OriginalArg(9) colourA: Int, @OriginalArg(10) colourB: Int, @OriginalArg(11) colourC: Int) {
		var yA: Float = yA
		var yB: Float = yB
		var yC: Float = yC
		var xA: Float = xA
		var xB: Float = xB
		var xC: Float = xC
		var zA: Float = zA
		var zB: Float = zB
		var zC: Float = zC
		if (this.wireframe) {
			this.toolkit!!.drawOpaqueLine(yA.toInt(), xA.toInt(), xB.toInt(), colourA or 0xFF000000.toInt(), yB.toInt())
			this.toolkit!!.drawOpaqueLine(yB.toInt(), xB.toInt(), xC.toInt(), colourA or 0xFF000000.toInt(), yC.toInt())
			this.toolkit!!.drawOpaqueLine(yC.toInt(), xC.toInt(), xA.toInt(), colourA or 0xFF000000.toInt(), yA.toInt())
			return
		}
		@Pc(52) val slopeXBa: Float = xB - xA
		@Pc(56) val slopeYBa: Float = yB - yA
		@Pc(60) val slopeXCa: Float = xC - xA
		@Pc(64) val slopeYCa: Float = yC - yA
		@Pc(68) val slopeZBa: Float = zB - zA
		@Pc(72) val slopeZCa: Float = zC - zA
		@Pc(81) val slopeRedBa: Float = ((colourB and 0xFF0000) - (colourA and 0xFF0000)).toFloat()
		@Pc(90) val slopeRedCa: Float = ((colourC and 0xFF0000) - (colourA and 0xFF0000)).toFloat()
		@Pc(99) val slopeGreenBa: Float = ((colourB and 0xFF00) - (colourA and 0xFF00)).toFloat()
		@Pc(108) val slopeGreenCa: Float = ((colourC and 0xFF00) - (colourA and 0xFF00)).toFloat()
		@Pc(117) val slopeBlueBa: Float = ((colourB and 0xFF) - (colourA and 0xFF)).toFloat()
		@Pc(126) val slopeBlueCa: Float = ((colourC and 0xFF) - (colourA and 0xFF)).toFloat()
		@Pc(138) var scaleXCb: Float
		if (yC == yB) {
			scaleXCb = 0.0f
		} else {
			scaleXCb = (xC - xB) / (yC - yB)
		}
		@Pc(149) var scaleXBa: Float
		if (yB == yA) {
			scaleXBa = 0.0f
		} else {
			scaleXBa = slopeXBa / slopeYBa
		}
		@Pc(160) var scaleXCa: Float
		if (yC == yA) {
			scaleXCa = 0.0f
		} else {
			scaleXCa = slopeXCa / slopeYCa
		}
		@Pc(171) val determinant: Float = slopeXBa * slopeYCa - slopeXCa * slopeYBa
		if (determinant == 0.0f) {
			return
		}
		@Pc(186) val slopeDepthX: Float = (slopeZBa * slopeYCa - slopeZCa * slopeYBa) / determinant
		@Pc(196) val slopeDepthY: Float = (slopeZCa * slopeXBa - slopeZBa * slopeXCa) / determinant
		@Pc(206) val slopeRedX: Float = (slopeRedBa * slopeYCa - slopeRedCa * slopeYBa) / determinant
		@Pc(216) val slopeRedY: Float = (slopeRedCa * slopeXBa - slopeRedBa * slopeXCa) / determinant
		@Pc(226) val slopeGreenX: Float = (slopeGreenBa * slopeYCa - slopeGreenCa * slopeYBa) / determinant
		@Pc(236) val slopeGreenY: Float = (slopeGreenCa * slopeXBa - slopeGreenBa * slopeXCa) / determinant
		@Pc(246) val slopeBlueX: Float = (slopeBlueBa * slopeYCa - slopeBlueCa * slopeYBa) / determinant
		@Pc(256) val slopeBlueY: Float = (slopeBlueCa * slopeXBa - slopeBlueBa * slopeXCa) / determinant
		@Pc(310) var red: Float
		@Pc(321) var green: Float
		@Pc(332) var blue: Float
		if (yA <= yB && yA <= yC) {
			if (!(yA >= this.height.toFloat())) {
				if (yB > this.height.toFloat()) {
					yB = this.height.toFloat()
				}
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				zA = zA + slopeDepthX - slopeDepthX * xA
				red = (colourA and 0xFF0000).toFloat() + slopeRedX - slopeRedX * xA
				green = (colourA and 0xFF00).toFloat() + slopeGreenX - slopeGreenX * xA
				blue = (colourA and 0xFF).toFloat() + slopeBlueX - slopeBlueX * xA
				if (yB < yC) {
					xC = xA
					if (yA < 0.0f) {
						xC = xA - scaleXCa * yA
						xA -= scaleXBa * yA
						zA -= slopeDepthY * yA
						red -= slopeRedY * yA
						green -= slopeGreenY * yA
						blue -= slopeBlueY * yA
						yA = 0.0f
					}
					if (yB < 0.0f) {
						xB -= scaleXCb * yB
						yB = 0.0f
					}
					if ((yA == yB || !(scaleXCa < scaleXBa)) && (yA != yB || !(scaleXCa > scaleXCb))) {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xC.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xB.toInt(), xC.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCa
							xB += scaleXCb
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yB
						yB -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yB >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xA.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xB.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCa
							xB += scaleXCb
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xB = xA
					if (yA < 0.0f) {
						xB = xA - scaleXCa * yA
						xA -= scaleXBa * yA
						zA -= slopeDepthY * yA
						red -= slopeRedY * yA
						green -= slopeGreenY * yA
						blue -= slopeBlueY * yA
						yA = 0.0f
					}
					if (yC < 0.0f) {
						xC -= scaleXCb * yC
						yC = 0.0f
					}
					if (yA != yC && scaleXCa < scaleXBa || yA == yC && scaleXCb > scaleXBa) {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xB.toInt(), xA.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xB += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xC.toInt(), xA.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCb
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
					} else {
						yB -= yC
						yC -= yA
						yA = this.lineOffsets[yA.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xB.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xB += scaleXCa
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
						while (--yB >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yA.toInt(), xA.toInt(), xC.toInt(), zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXCb
							xA += scaleXBa
							zA += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yA += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= this.height.toFloat())) {
				if (yC > this.height.toFloat()) {
					yC = this.height.toFloat()
				}
				if (yA > this.height.toFloat()) {
					yA = this.height.toFloat()
				}
				zB = zB + slopeDepthX - slopeDepthX * xB
				red = (colourB and 0xFF0000).toFloat() + slopeRedX - slopeRedX * xB
				green = (colourB and 0xFF00).toFloat() + slopeGreenX - slopeGreenX * xB
				blue = (colourB and 0xFF).toFloat() + slopeBlueX - slopeBlueX * xB
				if (yC < yA) {
					xA = xB
					if (yB < 0.0f) {
						xA = xB - scaleXBa * yB
						xB -= scaleXCb * yB
						zB -= slopeDepthY * yB
						red -= slopeRedY * yB
						green -= slopeGreenY * yB
						blue -= slopeBlueY * yB
						yB = 0.0f
					}
					if (yC < 0.0f) {
						xC -= scaleXCa * yC
						yC = 0.0f
					}
					if ((yB == yC || !(scaleXBa < scaleXCb)) && (yB != yC || !(scaleXBa > scaleXCa))) {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xA.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xC.toInt(), xA.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXBa
							xC += scaleXCa
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yA -= yC
						yC -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xB.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yA >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xC.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXBa
							xC += scaleXCa
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
					}
				} else {
					xC = xB
					if (yB < 0.0f) {
						xC = xB - scaleXBa * yB
						xB -= scaleXCb * yB
						zB -= slopeDepthY * yB
						red -= slopeRedY * yB
						green -= slopeGreenY * yB
						blue -= slopeBlueY * yB
						yB = 0.0f
					}
					if (yA < 0.0f) {
						xA -= scaleXCa * yA
						yA = 0.0f
					}
					if (scaleXBa < scaleXCb) {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xC.toInt(), xB.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xA.toInt(), xB.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXCa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
					} else {
						yC -= yA
						yA -= yB
						yB = this.lineOffsets[yB.toInt()].toFloat()
						while (--yA >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xC.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xC += scaleXBa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
						while (--yC >= 0.0f) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yB.toInt(), xB.toInt(), xA.toInt(), zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
							xA += scaleXCa
							xB += scaleXCb
							zB += slopeDepthY
							red += slopeRedY
							green += slopeGreenY
							blue += slopeBlueY
							yB += this.surfaceWidth.toFloat()
						}
					}
				}
			}
		} else if (!(yC >= this.height.toFloat())) {
			if (yA > this.height.toFloat()) {
				yA = this.height.toFloat()
			}
			if (yB > this.height.toFloat()) {
				yB = this.height.toFloat()
			}
			zC = zC + slopeDepthX - slopeDepthX * xC
			red = (colourC and 0xFF0000).toFloat() + slopeRedX - slopeRedX * xC
			green = (colourC and 0xFF00).toFloat() + slopeGreenX - slopeGreenX * xC
			blue = (colourC and 0xFF).toFloat() + slopeBlueX - slopeBlueX * xC
			if (yA < yB) {
				xB = xC
				if (yC < 0.0f) {
					xB = xC - scaleXCb * yC
					xC -= scaleXCa * yC
					zC -= slopeDepthY * yC
					red -= slopeRedY * yC
					green -= slopeGreenY * yC
					blue -= slopeBlueY * yC
					yC = 0.0f
				}
				if (yA < 0.0f) {
					xA -= scaleXBa * yA
					yA = 0.0f
				}
				if (scaleXCb < scaleXCa) {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xC.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xA.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXCb
						xA += scaleXBa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yB -= yA
					yA -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yA >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xB.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yB >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xA.toInt(), xB.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXCb
						xA += scaleXBa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
				}
			} else {
				xA = xC
				if (yC < 0.0f) {
					xA = xC - scaleXCb * yC
					xC -= scaleXCa * yC
					zC -= slopeDepthY * yC
					red -= slopeRedY * yC
					green -= slopeGreenY * yC
					blue -= slopeBlueY * yC
					yC = 0.0f
				}
				if (yB < 0.0f) {
					xB -= scaleXBa * yB
					yB = 0.0f
				}
				if (scaleXCb < scaleXCa) {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xA.toInt(), xC.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xA += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xB.toInt(), xC.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXBa
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
				} else {
					yA -= yB
					yB -= yC
					yC = this.lineOffsets[yC.toInt()].toFloat()
					while (--yB >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xA.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xA += scaleXCb
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
					while (--yA >= 0.0f) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, yC.toInt(), xC.toInt(), xB.toInt(), zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX)
						xB += scaleXBa
						xC += scaleXCa
						zC += slopeDepthY
						red += slopeRedY
						green += slopeGreenY
						blue += slopeBlueY
						yC += this.surfaceWidth.toFloat()
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "b", descriptor = "()I")
	fun offsetX(): Int {
		return this.lineOffsets[0] % this.surfaceWidth
	}
}
