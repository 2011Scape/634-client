import com.jagex.game.runetek5.JavaSpriteBlitState
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!c")
class OpaqueSpriteSM : SpriteSM {

	@OriginalMember(owner = "client!c", name = "H", descriptor = "[I")
	@JvmField
	val pixels: IntArray?

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;II)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) : super(javaToolkit, width, height) {
		this.pixels = IntArray(width * height)
	}

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;[IIIIIZ)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) srcPixels: IntArray?, @OriginalArg(2) srcOffset: Int, @OriginalArg(3) srcStride: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) copyPixels: Boolean) : super(javaToolkit, width, height) {
		var srcOffset: Int = srcOffset
		if (copyPixels) {
			this.pixels = IntArray(width * height)
		} else {
			this.pixels = srcPixels
		}
		@Pc(21) val srcSkip: Int = srcStride - this.imageWidth
		@Pc(23) var dstIndex: Int = 0
		for (@Pc(25) i in 0 until height) {
			for (@Pc(28) j in 0 until width) {
				@Pc(34) val argb: Int = srcPixels!![srcOffset++]
				if ((argb ushr 24) == 255) {
					this.pixels!![dstIndex++] = if ((argb and 0xFFFFFF) == 0) -16777215 else argb
				} else {
					this.pixels!![dstIndex++] = 0
				}
			}
			srcOffset += srcSkip
		}
	}

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;[III)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) srcPixels: IntArray?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) : super(javaToolkit, width, height) {
		this.pixels = srcPixels
	}

	@OriginalMember(owner = "client!c", name = "b", descriptor = "(II)V")
	override fun blitParallelogram(@OriginalArg(0) op: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(852) var skip: Int
		@Pc(9) var row: Int
		@Pc(12) var dstIndex: Int
		@Pc(14) var u: Int
		@Pc(16) var v: Int
		@Pc(18) var column: Int
		@Pc(48) var texel: Int
		@Pc(51) var dst: Int
		@Pc(209) var src: Int
		@Pc(234) var local234: Int
		@Pc(238) var local238: Int
		@Pc(242) var local242: Int
		@Pc(314) var local314: Int
		if (JavaSpriteBlitState.dudx == 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase
					v = JavaSpriteBlitState.vBase
					column = JavaSpriteBlitState.drawWidth
					if (u >= 0 && v >= 0 && u - (this.imageWidth shl 12) < 0 && v - (this.imageHeight shl 12) < 0) {
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								if (src != 0) {
									raster!![dst] = src
								}
							} else if (op == 0) {
								src = this.pixels!![texel]
								if (src != 0) {
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
									} else {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redTerm or greenTerm or blueTerm) ushr 8
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val tintColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tintColour
								val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
								var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
								blended = sum - blended or blended - (blended ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended
									blended = raster!![dst]
									blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = blended
							} else if (op == 2) {
								src = this.pixels!![texel]
								if (src != 0) {
									val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
								}
							} else {
								throw IllegalArgumentException()
							}
							column++
						}
					}
					row++
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					if (u >= 0 && u - (this.imageWidth shl 12) < 0) {
						@Pc(845) var vOverrun: Int
						vOverrun = v - (this.imageHeight shl 12)
						if (vOverrun >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
							column += skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(873) var vBound: Int
						vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vBound > column) {
							column = vBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								if (src != 0) {
									raster!![dst] = src
								}
							} else if (op == 0) {
								src = this.pixels!![texel]
								if (src != 0) {
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
									} else {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redTerm or greenTerm or blueTerm) ushr 8
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val tintColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tintColour
								val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
								var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
								blended = sum - blended or blended - (blended ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended
									blended = raster!![dst]
									blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = blended
							} else if (op == 2) {
								src = this.pixels!![texel]
								if (src != 0) {
									val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
								}
							} else {
								throw IllegalArgumentException()
							}
							v += JavaSpriteBlitState.dvdx
							column++
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					if (u >= 0 && u - (this.imageWidth shl 12) < 0) {
						if (v < 0) {
							skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
							column += skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(1722) var vEndBound: Int
						vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vEndBound > column) {
							column = vEndBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								if (src != 0) {
									raster!![dst] = src
								}
							} else if (op == 0) {
								src = this.pixels!![texel]
								if (src != 0) {
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
									} else {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redTerm or greenTerm or blueTerm) ushr 8
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val tintColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tintColour
								val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
								var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
								blended = sum - blended or blended - (blended ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended
									blended = raster!![dst]
									blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = blended
							} else if (op == 2) {
								src = this.pixels!![texel]
								if (src != 0) {
									val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
								}
							} else {
								throw IllegalArgumentException()
							}
							v += JavaSpriteBlitState.dvdx
							column++
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			}
		} else if (JavaSpriteBlitState.dudx < 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase
					column = JavaSpriteBlitState.drawWidth
					if (v >= 0 && v - (this.imageHeight shl 12) < 0) {
						@Pc(2545) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							dstIndex += skip
						}
						@Pc(2573) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								if (src != 0) {
									raster!![dst] = src
								}
							} else if (op == 0) {
								src = this.pixels!![texel]
								if (src != 0) {
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
									} else {
										val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redTerm or greenTerm or blueTerm) ushr 8
										val dstPixel: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val tintColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tintColour
								val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
								var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
								blended = sum - blended or blended - (blended ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended
									blended = raster!![dst]
									blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = blended
							} else if (op == 2) {
								src = this.pixels!![texel]
								if (src != 0) {
									val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
								}
							} else {
								throw IllegalArgumentException()
							}
							u += JavaSpriteBlitState.dudx
							column++
						}
					}
					row++
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					@Pc(3387) var uOverrun: Int
					uOverrun = u - (this.imageWidth shl 12)
					if (uOverrun >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(3421) var uBound: Int
					uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uBound > column) {
						column = uBound
					}
					@Pc(3433) var vOverrun: Int
					vOverrun = v - (this.imageHeight shl 12)
					if (vOverrun >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(3467) var vBound: Int
					vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vBound > column) {
						column = vBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							if (src != 0) {
								raster!![dst] = src
							}
						} else if (op == 0) {
							src = this.pixels!![texel]
							if (src != 0) {
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
								} else {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redTerm or greenTerm or blueTerm) ushr 8
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val tintColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tintColour
							val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
							var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
							blended = sum - blended or blended - (blended ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended
								blended = raster!![dst]
								blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = blended
						} else if (op == 2) {
							src = this.pixels!![texel]
							if (src != 0) {
								val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
							}
						} else {
							throw IllegalArgumentException()
						}
						u += JavaSpriteBlitState.dudx
						v += JavaSpriteBlitState.dvdx
						column++
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					@Pc(4287) var uOverrun: Int
					uOverrun = u - (this.imageWidth shl 12)
					if (uOverrun >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(4321) var uBound: Int
					uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uBound > column) {
						column = uBound
					}
					if (v < 0) {
						skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(4369) var vEndBound: Int
					vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vEndBound > column) {
						column = vEndBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							if (src != 0) {
								raster!![dst] = src
							}
						} else if (op == 0) {
							src = this.pixels!![texel]
							if (src != 0) {
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
								} else {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redTerm or greenTerm or blueTerm) ushr 8
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val tintColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tintColour
							val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
							var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
							blended = sum - blended or blended - (blended ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended
								blended = raster!![dst]
								blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = blended
						} else if (op == 2) {
							src = this.pixels!![texel]
							if (src != 0) {
								val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
							}
						} else {
							throw IllegalArgumentException()
						}
						u += JavaSpriteBlitState.dudx
						v += JavaSpriteBlitState.dvdx
						column++
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			}
		} else if (JavaSpriteBlitState.dvdx == 0) {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				dstIndex = JavaSpriteBlitState.pixelOffset
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
				v = JavaSpriteBlitState.vBase
				column = JavaSpriteBlitState.drawWidth
				if (v >= 0 && v - (this.imageHeight shl 12) < 0) {
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						dstIndex += skip
					}
					@Pc(5228) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							if (src != 0) {
								raster!![dst] = src
							}
						} else if (op == 0) {
							src = this.pixels!![texel]
							if (src != 0) {
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
								} else {
									val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redTerm or greenTerm or blueTerm) ushr 8
									val dstPixel: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val tintColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tintColour
							val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
							var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
							blended = sum - blended or blended - (blended ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended
								blended = raster!![dst]
								blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = blended
						} else if (op == 2) {
							src = this.pixels!![texel]
							if (src != 0) {
								val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
							}
						} else {
							throw IllegalArgumentException()
						}
						u += JavaSpriteBlitState.dudx
						column++
					}
				}
				row++
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
			}
		} else if (JavaSpriteBlitState.dvdx < 0) {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				dstIndex = JavaSpriteBlitState.pixelOffset
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
				v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
				column = JavaSpriteBlitState.drawWidth
				if (u < 0) {
					skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
					column += skip
					u += JavaSpriteBlitState.dudx * skip
					v += JavaSpriteBlitState.dvdx * skip
					dstIndex += skip
				}
				@Pc(6082) var uEndBound: Int
				uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
				if (uEndBound > column) {
					column = uEndBound
				}
				@Pc(6094) var vOverrun: Int
				vOverrun = v - (this.imageHeight shl 12)
				if (vOverrun >= 0) {
					skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
					column += skip
					u += JavaSpriteBlitState.dudx * skip
					v += JavaSpriteBlitState.dvdx * skip
					dstIndex += skip
				}
				@Pc(6128) var vBound: Int
				vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
				if (vBound > column) {
					column = vBound
				}
				while (column < 0) {
					texel = (v shr 12) * this.imageWidth + (u shr 12)
					dst = dstIndex++
					if (op == 1) {
						src = this.pixels!![texel]
						if (src != 0) {
							raster!![dst] = src
						}
					} else if (op == 0) {
						src = this.pixels!![texel]
						if (src != 0) {
							if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
								val alpha: Int = JavaSpriteBlitState.colour ushr 24
								val invAlpha: Int = 256 - alpha
								val dstPixel: Int = raster!![dst]
								raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
							} else if (JavaSpriteBlitState.alpha == 255) {
								val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
							} else {
								val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								src = (redTerm or greenTerm or blueTerm) ushr 8
								val dstPixel: Int = raster!![dst]
								raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
						}
					} else if (op == 3) {
						src = this.pixels!![texel]
						val tintColour: Int = JavaSpriteBlitState.colour
						val sum: Int = src + tintColour
						val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
						var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
						blended = sum - blended or blended - (blended ushr 8)
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = blended
							blended = raster!![dst]
							blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
						}
						raster!![dst] = blended
					} else if (op == 2) {
						src = this.pixels!![texel]
						if (src != 0) {
							val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
							val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
							raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
						}
					} else {
						throw IllegalArgumentException()
					}
					u += JavaSpriteBlitState.dudx
					v += JavaSpriteBlitState.dvdx
					column++
				}
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				row++
			}
		} else {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				dstIndex = JavaSpriteBlitState.pixelOffset
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
				v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
				column = JavaSpriteBlitState.drawWidth
				if (u < 0) {
					skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
					column += skip
					u += JavaSpriteBlitState.dudx * skip
					v += JavaSpriteBlitState.dvdx * skip
					dstIndex += skip
				}
				@Pc(6984) var uEndBound: Int
				uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
				if (uEndBound > column) {
					column = uEndBound
				}
				if (v < 0) {
					skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
					column += skip
					u += JavaSpriteBlitState.dudx * skip
					v += JavaSpriteBlitState.dvdx * skip
					dstIndex += skip
				}
				@Pc(7032) var vEndBound: Int
				vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
				if (vEndBound > column) {
					column = vEndBound
				}
				while (column < 0) {
					texel = (v shr 12) * this.imageWidth + (u shr 12)
					dst = dstIndex++
					if (op == 1) {
						src = this.pixels!![texel]
						if (src != 0) {
							raster!![dst] = src
						}
					} else if (op == 0) {
						src = this.pixels!![texel]
						if (src != 0) {
							if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
								val alpha: Int = JavaSpriteBlitState.colour ushr 24
								val invAlpha: Int = 256 - alpha
								val dstPixel: Int = raster!![dst]
								raster!![dst] = ((src and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
							} else if (JavaSpriteBlitState.alpha == 255) {
								val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								raster!![dst] = (redTerm or greenTerm or blueTerm) ushr 8
							} else {
								val redTerm: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blueTerm: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								src = (redTerm or greenTerm or blueTerm) ushr 8
								val dstPixel: Int = raster!![dst]
								raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
						}
					} else if (op == 3) {
						src = this.pixels!![texel]
						val tintColour: Int = JavaSpriteBlitState.colour
						val sum: Int = src + tintColour
						val lowSum: Int = (src and 0xFF00FF) + (tintColour and 0xFF00FF)
						var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
						blended = sum - blended or blended - (blended ushr 8)
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = blended
							blended = raster!![dst]
							blended = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (blended and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (blended and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
						}
						raster!![dst] = blended
					} else if (op == 2) {
						src = this.pixels!![texel]
						if (src != 0) {
							val redBlueTerm: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
							val greenTerm: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
							raster!![dst++] = ((redBlueTerm or greenTerm) ushr 8) + JavaSpriteBlitState.lerpColour
						}
					} else {
						throw IllegalArgumentException()
					}
					u += JavaSpriteBlitState.dudx
					v += JavaSpriteBlitState.dvdx
					column++
				}
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				row++
			}
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) dstX: Int, @OriginalArg(1) dstY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) srcX: Int, @OriginalArg(5) srcY: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		for (@Pc(5) row in 0 until height) {
			@Pc(15) val dstIndex: Int = (dstY + row) * this.imageWidth + dstX
			@Pc(25) val srcIndex: Int = (srcY + row) * this.toolkit!!.surfaceWidth + srcX
			for (@Pc(27) column in 0 until width) {
				this.pixels!![dstIndex + column] = raster!![srcIndex + column]
			}
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIIIII)V")
	override fun renderImpl(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int, @OriginalArg(6) mode: Int) {
		var x: Int = x
		var y: Int = y
		var width: Int = width
		var height: Int = height
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		} else if (width > 0 && height > 0) {
			@Pc(14) var u: Int = 0
			@Pc(16) var v: Int = 0
			@Pc(20) val dstStride: Int = this.toolkit!!.surfaceWidth
			@Pc(29) val scaleWidth: Int = this.leftMargin + this.imageWidth + this.trimRight
			@Pc(38) val scaleHeight: Int = this.topMargin + this.imageHeight + this.trimBottom
			@Pc(44) val uStep: Int = (scaleWidth shl 16) / width
			@Pc(50) val vStep: Int = (scaleHeight shl 16) / height
			@Pc(64) var offset: Int
			if (this.leftMargin > 0) {
				offset = ((this.leftMargin shl 16) + uStep - 1) / uStep
				x += offset
				u = offset * uStep - (this.leftMargin shl 16)
			}
			if (this.topMargin > 0) {
				offset = ((this.topMargin shl 16) + vStep - 1) / vStep
				y += offset
				v = offset * vStep - (this.topMargin shl 16)
			}
			if (this.imageWidth < scaleWidth) {
				width = ((this.imageWidth shl 16) + uStep - u - 1) / uStep
			}
			if (this.imageHeight < scaleHeight) {
				height = ((this.imageHeight shl 16) + vStep - v - 1) / vStep
			}
			offset = x + y * dstStride
			@Pc(152) var dstStep: Int = dstStride - width
			if (y + height > this.toolkit!!.clipY2) {
				height -= y + height - this.toolkit!!.clipY2
			}
			@Pc(180) var clip: Int
			if (y < this.toolkit!!.clipY1) {
				clip = this.toolkit!!.clipY1 - y
				height -= clip
				offset += clip * dstStride
				v += vStep * clip
			}
			if (x + width > this.toolkit!!.clipX2) {
				clip = x + width - this.toolkit!!.clipX2
				width -= clip
				dstStep += clip
			}
			if (x < this.toolkit!!.clipX1) {
				clip = this.toolkit!!.clipX1 - x
				width -= clip
				offset += clip
				u += uStep * clip
				dstStep += clip
			}
			@Pc(252) val raster: IntArray? = this.toolkit!!.surfaceRaster
			@Pc(259) var local259: Int
			@Pc(262) var local262: Int
			@Pc(270) var local270: Int
			@Pc(273) var local273: Int
			@Pc(330) var local330: Int
			@Pc(338) var local338: Int
			@Pc(341) var local341: Int
			@Pc(351) var local351: Int
			@Pc(359) var local359: Int
			@Pc(367) var local367: Int
			@Pc(375) var local375: Int
			@Pc(525) var lerpColour: Int
			if (mode != 0) {
				@Pc(837) var local837: Int
				@Pc(845) var local845: Int
				@Pc(857) var local857: Int
				if (mode == 1) {
					if (op == 1) {
						val uStart: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								if (srcPixel == 0) {
									offset++
								} else {
									raster!![offset++] = srcPixel
								}
								u += uStep
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					} else if (op == 0) {
						val uStart: Int = u
						if ((colour and 0xFFFFFF) == 16777215) {
							val alpha: Int = colour ushr 24
							val invAlpha: Int = 256 - alpha
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									if (srcPixel == 0) {
										offset++
									} else {
										var dstPixel: Int = raster!![offset]
										raster!![offset++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									}
									u += uStep
								}
								v += vStep
								u = uStart
								offset += dstStep
							}
						} else {
							val red: Int = (colour shr 16) and 0xFF
							val green: Int = (colour shr 8) and 0xFF
							val blue: Int = colour and 0xFF
							val alpha: Int = colour ushr 24
							val invAlpha: Int = 256 - alpha
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									if (srcPixel == 0) {
										offset++
									} else if (alpha == 255) {
										val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
										raster!![offset++] = (tintedRed or tintedGreen or tintedBlue) ushr 8
									} else {
										val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
										srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
										var dstPixel: Int = raster!![offset]
										raster!![offset++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									}
									u += uStep
								}
								v += vStep
								u = uStart
								offset += dstStep
							}
						}
					} else if (op == 3) {
						val uStart: Int = u
						val alpha: Int = colour ushr 24
						val invAlpha: Int = 256 - alpha
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								var sum: Int = srcPixel + colour
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = overflow
									overflow = raster!![offset]
									overflow = ((srcPixel and 0xFF00FF) * alpha + (overflow and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (overflow and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
								raster!![offset++] = overflow
								u += uStep
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					} else if (op == 2) {
						val alpha: Int = colour ushr 24
						val invAlpha: Int = 256 - alpha
						var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
						var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
						lerpColour = (scaledRedBlue or scaledGreen) ushr 8
						val uStart: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								if (srcPixel == 0) {
									offset++
								} else {
									scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
									scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
									raster!![offset++] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
								}
								u += uStep
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					} else {
						throw IllegalArgumentException()
					}
				} else if (mode != 2) {
					throw IllegalArgumentException()
				} else if (op == 1) {
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel == 0) {
								offset++
							} else {
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else if (op == 0) {
					val uStart: Int = u
					val red: Int = (colour shr 16) and 0xFF
					val green: Int = (colour shr 8) and 0xFF
					val blue: Int = colour and 0xFF
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel == 0) {
								offset++
							} else {
								val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
								val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
								val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
								srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else if (op == 3) {
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							var sum: Int = srcPixel + colour
							var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							srcPixel = sum - overflow or overflow - (overflow ushr 8)
							overflow = raster!![offset]
							sum = srcPixel + overflow
							redBlueSum = (srcPixel and 0xFF00FF) + (overflow and 0xFF00FF)
							overflow = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster!![offset++] = sum - overflow or overflow - (overflow ushr 8)
							u += uStep
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else if (op == 2) {
					val alpha: Int = colour ushr 24
					val invAlpha: Int = 256 - alpha
					var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
					var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
					lerpColour = (scaledRedBlue or scaledGreen) ushr 8
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel == 0) {
								offset++
							} else {
								scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
								scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
								srcPixel = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								@Pc(1668) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset++] = sum - overflow or overflow - (overflow ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else {
					throw IllegalArgumentException()
				}
			} else if (op == 1) {
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						raster!![offset++] = this.pixels!![(u shr 16) + srcRowOffset]
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 0) {
				val red: Int = (colour shr 16) and 0xFF
				val green: Int = (colour shr 8) and 0xFF
				val blue: Int = colour and 0xFF
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
						val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
						val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
						raster!![offset++] = (tintedRed or tintedGreen or tintedBlue) ushr 8
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 3) {
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						var sum: Int = srcPixel + colour
						var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
						var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![offset++] = sum - overflow or overflow - (overflow ushr 8)
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 2) {
				val alpha: Int = colour ushr 24
				val invAlpha: Int = 256 - alpha
				var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (scaledRedBlue or scaledGreen) ushr 8
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
						scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
						raster!![offset++] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else {
				throw IllegalArgumentException()
			}
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) channel: Int) {
		throw IllegalStateException("Can't capture alpha into a java_sprite_24")
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "([I[III)V")
	override fun blitParallelogramMasked(@OriginalArg(0) lineOffsets: IntArray?, @OriginalArg(1) lineWidths: IntArray?, @OriginalArg(2) maskOffsetX: Int, @OriginalArg(3) maskOffsetY: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(181) var skip: Int
		@Pc(9) var row: Int
		@Pc(14) var maskIndex: Int
		@Pc(23) var dstIndex: Int
		@Pc(25) var u: Int
		@Pc(27) var v: Int
		@Pc(29) var column: Int
		@Pc(53) var maskStart: Int
		@Pc(58) var maskCount: Int
		@Pc(64) var maskSkip: Int
		@Pc(111) var src: Int
		if (JavaSpriteBlitState.dudx == 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase
						v = JavaSpriteBlitState.vBase
						column = JavaSpriteBlitState.drawWidth
						if (u >= 0 && v >= 0 && u - (this.imageWidth shl 12) < 0 && v - (this.imageHeight shl 12) < 0) {
							maskStart = lineOffsets!![maskIndex] - maskOffsetX
							maskCount = -lineWidths!![maskIndex]
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
							if (maskSkip > 0) {
								dstIndex += maskSkip
								column += maskSkip
								u += JavaSpriteBlitState.dudx * maskSkip
								v += JavaSpriteBlitState.dvdx * maskSkip
							} else {
								maskCount -= maskSkip
							}
							if (column < maskCount) {
								column = maskCount
							}
							while (column < 0) {
								src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (src == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = src
								}
								column++
							}
						}
					}
					row++
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						if (u >= 0 && u - (this.imageWidth shl 12) < 0) {
							@Pc(174) var vOverrun: Int
							vOverrun = v - (this.imageHeight shl 12)
							if (vOverrun >= 0) {
								skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
								column += skip
								v += JavaSpriteBlitState.dvdx * skip
								dstIndex += skip
							}
							@Pc(202) var vBound: Int
							vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
							if (vBound > column) {
								column = vBound
							}
							maskStart = lineOffsets!![maskIndex] - maskOffsetX
							maskCount = -lineWidths!![maskIndex]
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
							if (maskSkip > 0) {
								dstIndex += maskSkip
								column += maskSkip
								u += JavaSpriteBlitState.dudx * maskSkip
								v += JavaSpriteBlitState.dvdx * maskSkip
							} else {
								maskCount -= maskSkip
							}
							if (column < maskCount) {
								column = maskCount
							}
							while (column < 0) {
								src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (src == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = src
								}
								v += JavaSpriteBlitState.dvdx
								column++
							}
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						if (u >= 0 && u - (this.imageWidth shl 12) < 0) {
							if (v < 0) {
								skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
								column += skip
								v += JavaSpriteBlitState.dvdx * skip
								dstIndex += skip
							}
							@Pc(369) var vEndBound: Int
							vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
							if (vEndBound > column) {
								column = vEndBound
							}
							maskStart = lineOffsets!![maskIndex] - maskOffsetX
							maskCount = -lineWidths!![maskIndex]
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
							if (maskSkip > 0) {
								dstIndex += maskSkip
								column += maskSkip
								u += JavaSpriteBlitState.dudx * maskSkip
								v += JavaSpriteBlitState.dvdx * maskSkip
							} else {
								maskCount -= maskSkip
							}
							if (column < maskCount) {
								column = maskCount
							}
							while (column < 0) {
								src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (src == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = src
								}
								v += JavaSpriteBlitState.dvdx
								column++
							}
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			}
		} else if (JavaSpriteBlitState.dudx < 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase
						column = JavaSpriteBlitState.drawWidth
						if (v >= 0 && v - (this.imageHeight shl 12) < 0) {
							@Pc(510) var uOverrun: Int
							uOverrun = u - (this.imageWidth shl 12)
							if (uOverrun >= 0) {
								skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
								column += skip
								u += JavaSpriteBlitState.dudx * skip
								dstIndex += skip
							}
							@Pc(538) var uBound: Int
							uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
							if (uBound > column) {
								column = uBound
							}
							maskStart = lineOffsets!![maskIndex] - maskOffsetX
							maskCount = -lineWidths!![maskIndex]
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
							if (maskSkip > 0) {
								dstIndex += maskSkip
								column += maskSkip
								u += JavaSpriteBlitState.dudx * maskSkip
								v += JavaSpriteBlitState.dvdx * maskSkip
							} else {
								maskCount -= maskSkip
							}
							if (column < maskCount) {
								column = maskCount
							}
							while (column < 0) {
								src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (src == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = src
								}
								u += JavaSpriteBlitState.dudx
								column++
							}
						}
					}
					row++
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						@Pc(670) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(704) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						@Pc(716) var vOverrun: Int
						vOverrun = v - (this.imageHeight shl 12)
						if (vOverrun >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(750) var vBound: Int
						vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vBound > column) {
							column = vBound
						}
						maskStart = lineOffsets!![maskIndex] - maskOffsetX
						maskCount = -lineWidths!![maskIndex]
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
						if (maskSkip > 0) {
							dstIndex += maskSkip
							column += maskSkip
							u += JavaSpriteBlitState.dudx * maskSkip
							v += JavaSpriteBlitState.dvdx * maskSkip
						} else {
							maskCount -= maskSkip
						}
						if (column < maskCount) {
							column = maskCount
						}
						while (column < 0) {
							src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							u += JavaSpriteBlitState.dudx
							v += JavaSpriteBlitState.dvdx
							column++
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			} else {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskOffsetY
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						@Pc(888) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(922) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						if (v < 0) {
							skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(970) var vEndBound: Int
						vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vEndBound > column) {
							column = vEndBound
						}
						maskStart = lineOffsets!![maskIndex] - maskOffsetX
						maskCount = -lineWidths!![maskIndex]
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
						if (maskSkip > 0) {
							dstIndex += maskSkip
							column += maskSkip
							u += JavaSpriteBlitState.dudx * maskSkip
							v += JavaSpriteBlitState.dvdx * maskSkip
						} else {
							maskCount -= maskSkip
						}
						if (column < maskCount) {
							column = maskCount
						}
						while (column < 0) {
							src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							u += JavaSpriteBlitState.dudx
							v += JavaSpriteBlitState.dvdx
							column++
						}
					}
					row++
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
				}
			}
		} else if (JavaSpriteBlitState.dvdx == 0) {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				maskIndex = row + maskOffsetY
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets!!.size) {
						return
					}
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase
					column = JavaSpriteBlitState.drawWidth
					if (v >= 0 && v - (this.imageHeight shl 12) < 0) {
						if (u < 0) {
							skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							dstIndex += skip
						}
						@Pc(1147) var uEndBound: Int
						uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uEndBound > column) {
							column = uEndBound
						}
						maskStart = lineOffsets!![maskIndex] - maskOffsetX
						maskCount = -lineWidths!![maskIndex]
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
						if (maskSkip > 0) {
							dstIndex += maskSkip
							column += maskSkip
							u += JavaSpriteBlitState.dudx * maskSkip
							v += JavaSpriteBlitState.dvdx * maskSkip
						} else {
							maskCount -= maskSkip
						}
						if (column < maskCount) {
							column = maskCount
						}
						while (column < 0) {
							src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							u += JavaSpriteBlitState.dudx
							column++
						}
					}
				}
				row++
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
			}
		} else if (JavaSpriteBlitState.dvdx < 0) {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				maskIndex = row + maskOffsetY
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets!!.size) {
						return
					}
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1319) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					@Pc(1331) var vOverrun: Int
					vOverrun = v - (this.imageHeight shl 12)
					if (vOverrun >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1365) var vBound: Int
					vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vBound > column) {
						column = vBound
					}
					maskStart = lineOffsets!![maskIndex] - maskOffsetX
					maskCount = -lineWidths!![maskIndex]
					maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
					if (maskSkip > 0) {
						dstIndex += maskSkip
						column += maskSkip
						u += JavaSpriteBlitState.dudx * maskSkip
						v += JavaSpriteBlitState.dvdx * maskSkip
					} else {
						maskCount -= maskSkip
					}
					if (column < maskCount) {
						column = maskCount
					}
					while (column < 0) {
						src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
						if (src == 0) {
							dstIndex++
						} else {
							raster!![dstIndex++] = src
						}
						u += JavaSpriteBlitState.dudx
						v += JavaSpriteBlitState.dvdx
						column++
					}
				}
				row++
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
			}
		} else {
			row = JavaSpriteBlitState.drawHeight
			while (row < 0) {
				maskIndex = row + maskOffsetY
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets!!.size) {
						return
					}
					dstIndex = JavaSpriteBlitState.pixelOffset
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
					column = JavaSpriteBlitState.drawWidth
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1539) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					if (v < 0) {
						skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1587) var vEndBound: Int
					vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vEndBound > column) {
						column = vEndBound
					}
					maskStart = lineOffsets!![maskIndex] - maskOffsetX
					maskCount = -lineWidths!![maskIndex]
					maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex
					if (maskSkip > 0) {
						dstIndex += maskSkip
						column += maskSkip
						u += JavaSpriteBlitState.dudx * maskSkip
						v += JavaSpriteBlitState.dvdx * maskSkip
					} else {
						maskCount -= maskSkip
					}
					if (column < maskCount) {
						column = maskCount
					}
					while (column < 0) {
						src = this.pixels!![(v shr 12) * this.imageWidth + (u shr 12)]
						if (src == 0) {
							dstIndex++
						} else {
							raster!![dstIndex++] = src
						}
						u += JavaSpriteBlitState.dudx
						v += JavaSpriteBlitState.dvdx
						column++
					}
				}
				row++
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride
			}
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IILclient!aa;II)V")
	override fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) surfaceOffsetX: Int, @OriginalArg(4) surfaceOffsetY: Int) {
		var x: Int = x
		var y: Int = y
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		}
		x += this.leftMargin
		y += this.topMargin
		@Pc(19) var srcIndex: Int = 0
		@Pc(23) val surfaceWidth: Int = this.toolkit!!.surfaceWidth
		@Pc(26) var imageWidth: Int = this.imageWidth
		@Pc(29) var imageHeight: Int = this.imageHeight
		@Pc(33) var dstStep: Int = surfaceWidth - imageWidth
		@Pc(35) var srcStep: Int = 0
		@Pc(41) var dstIndex: Int = x + y * surfaceWidth
		@Pc(52) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			imageHeight -= clip
			y = this.toolkit!!.clipY1
			srcIndex = clip * imageWidth
			dstIndex += clip * surfaceWidth
		}
		if (y + imageHeight > this.toolkit!!.clipY2) {
			imageHeight -= y + imageHeight - this.toolkit!!.clipY2
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			imageWidth -= clip
			x = this.toolkit!!.clipX1
			srcIndex += clip
			dstIndex += clip
			srcStep = clip
			dstStep += clip
		}
		if (x + imageWidth > this.toolkit!!.clipX2) {
			clip = x + imageWidth - this.toolkit!!.clipX2
			imageWidth -= clip
			srcStep += clip
			dstStep += clip
		}
		if (imageWidth <= 0 || imageHeight <= 0) {
			return
		}
		@Pc(159) val javaClippingMask: JavaClippingMask? = clippingMask as JavaClippingMask?
		@Pc(162) val lineWidths: IntArray? = javaClippingMask!!.lineWidths
		@Pc(165) val lineOffsets: IntArray? = javaClippingMask!!.lineOffsets
		@Pc(169) val surfaceRaster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(171) var startY: Int = y
		if (surfaceOffsetY > y) {
			startY = surfaceOffsetY
			dstIndex += (surfaceOffsetY - y) * surfaceWidth
			srcIndex += (surfaceOffsetY - y) * this.imageWidth
		}
		@Pc(210) val endY: Int = if (surfaceOffsetY + lineWidths!!.size < y + imageHeight) surfaceOffsetY + lineWidths!!.size else y + imageHeight
		for (@Pc(212) i in startY until endY) {
			@Pc(221) val spanStart: Int = lineWidths!![i - surfaceOffsetY] + surfaceOffsetX
			@Pc(227) var spanWidth: Int = lineOffsets!![i - surfaceOffsetY]
			@Pc(229) var remaining: Int = imageWidth
			@Pc(236) var skip: Int
			if (x > spanStart) {
				skip = x - spanStart
				if (skip >= spanWidth) {
					srcIndex += imageWidth + srcStep
					dstIndex += imageWidth + dstStep
					continue
				}
				spanWidth -= skip
			} else {
				skip = spanStart - x
				if (skip >= imageWidth) {
					srcIndex += imageWidth + srcStep
					dstIndex += imageWidth + dstStep
					continue
				}
				srcIndex += skip
				remaining = imageWidth - skip
				dstIndex += skip
			}
			skip = 0
			if (remaining < spanWidth) {
				spanWidth = remaining
			} else {
				skip = remaining - spanWidth
			}
			for (@Pc(304) j in -spanWidth until 0) {
				@Pc(311) val src: Int = this.pixels!![srcIndex++]
				if (src == 0) {
					dstIndex++
				} else {
					surfaceRaster!![dstIndex++] = src
				}
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIII)V")
	override fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) op: Int, @OriginalArg(3) color: Int, @OriginalArg(4) mode: Int) {
		var x: Int = x
		var y: Int = y
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		}
		@Pc(11) val dstStride: Int = this.toolkit!!.surfaceWidth
		x += this.leftMargin
		y += this.topMargin
		@Pc(27) var dstIndex: Int = y * dstStride + x
		@Pc(29) var srcIndex: Int = 0
		@Pc(32) var height: Int = this.imageHeight
		@Pc(35) var width: Int = this.imageWidth
		@Pc(39) var dstStep: Int = dstStride - width
		@Pc(41) var srcStep: Int = 0
		@Pc(52) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			height -= clip
			y = this.toolkit!!.clipY1
			srcIndex = clip * width
			dstIndex += clip * dstStride
		}
		if (y + height > this.toolkit!!.clipY2) {
			height -= y + height - this.toolkit!!.clipY2
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			width -= clip
			x = this.toolkit!!.clipX1
			srcIndex += clip
			dstIndex += clip
			srcStep = clip
			dstStep += clip
		}
		if (x + width > this.toolkit!!.clipX2) {
			clip = x + width - this.toolkit!!.clipX2
			width -= clip
			srcStep += clip
			dstStep += clip
		}
		if (width <= 0 || height <= 0) {
			return
		}
		@Pc(160) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(168) var local168: Int
		@Pc(175) var local175: Int
		@Pc(259) var local259: Int
		@Pc(262) var local262: Int
		@Pc(266) var local266: Int
		@Pc(273) var local273: Int
		@Pc(281) var local281: Int
		@Pc(289) var local289: Int
		@Pc(297) var local297: Int
		@Pc(423) var lerpColour: Int
		if (mode != 0) {
			@Pc(739) var local739: Int
			@Pc(747) var local747: Int
			@Pc(759) var local759: Int
			if (mode == 1) {
				if (op == 1) {
					for (row in -height until 0) {
						var rowEnd: Int = dstIndex + width - 3
						while (dstIndex < rowEnd) {
							var src: Int = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							src = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							src = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
							src = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
						}
						rowEnd += 3
						while (dstIndex < rowEnd) {
							var src: Int = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = src
							}
						}
						dstIndex += dstStep
						srcIndex += srcStep
					}
				} else if (op == 0) {
					if ((color and 0xFFFFFF) == 16777215) {
						val alpha: Int = color ushr 24
						val invAlpha: Int = 256 - alpha
						for (row in -height until 0) {
							for (column in -width until 0) {
								var src: Int = this.pixels!![srcIndex++]
								if (src == 0) {
									dstIndex++
								} else {
									var dst: Int = raster!![dstIndex]
									raster!![dstIndex++] = ((src and 0xFF00FF) * alpha + (dst and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dst and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					} else {
						val red: Int = (color shr 16) and 0xFF
						val green: Int = (color shr 8) and 0xFF
						val blue: Int = color and 0xFF
						val alpha: Int = color ushr 24
						val invAlpha: Int = 256 - alpha
						for (row in -height until 0) {
							for (column in -width until 0) {
								var src: Int = this.pixels!![srcIndex++]
								if (src == 0) {
									dstIndex++
								} else if (alpha == 255) {
									val scaledRed: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
									var scaledGreen: Int = (src and 0xFF00) * green and 0xFF0000
									val scaledBlue: Int = (src and 0xFF) * blue and 0xFF00
									raster!![dstIndex++] = (scaledRed or scaledGreen or scaledBlue) ushr 8
								} else {
									val scaledRed: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
									var scaledGreen: Int = (src and 0xFF00) * green and 0xFF0000
									val scaledBlue: Int = (src and 0xFF) * blue and 0xFF00
									src = (scaledRed or scaledGreen or scaledBlue) ushr 8
									var dst: Int = raster!![dstIndex]
									raster!![dstIndex++] = ((src and 0xFF00FF) * alpha + (dst and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dst and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					}
				} else if (op == 3) {
					val alpha: Int = color ushr 24
					val invAlpha: Int = 256 - alpha
					for (row in -height until 0) {
						for (column in -width until 0) {
							var src: Int = this.pixels!![srcIndex++]
							var sum: Int = src + color
							var redBlueSum: Int = (src and 0xFF00FF) + (color and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							overflow = sum - overflow or overflow - (overflow ushr 8)
							if (src == 0 && alpha != 255) {
								src = overflow
								overflow = raster!![dstIndex]
								overflow = ((src and 0xFF00FF) * alpha + (overflow and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (overflow and 0xFF00) * invAlpha and 0xFF0000) shr 8
							}
							raster!![dstIndex++] = overflow
						}
						dstIndex += dstStep
						srcIndex += srcStep
					}
				} else if (op == 2) {
					val alpha: Int = color ushr 24
					val invAlpha: Int = 256 - alpha
					var scaledRedBlue: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
					var scaledGreen: Int = (color and 0xFF00) * invAlpha and 0xFF0000
					lerpColour = (scaledRedBlue or scaledGreen) ushr 8
					for (row in -height until 0) {
						for (column in -width until 0) {
							var src: Int = this.pixels!![srcIndex++]
							if (src == 0) {
								dstIndex++
							} else {
								scaledRedBlue = (src and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
								scaledGreen = (src and 0xFF00) * alpha and 0xFF0000
								raster!![dstIndex++] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
							}
						}
						dstIndex += dstStep
						srcIndex += srcStep
					}
				} else {
					throw IllegalArgumentException()
				}
			} else if (mode != 2) {
				throw IllegalArgumentException()
			} else if (op == 1) {
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						if (src == 0) {
							dstIndex++
						} else {
							var dst: Int = raster!![dstIndex]
							var sum: Int = src + dst
							var redBlueSum: Int = (src and 0xFF00FF) + (dst and 0xFF00FF)
							dst = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster!![dstIndex++] = sum - dst or dst - (dst ushr 8)
						}
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 0) {
				val red: Int = (color shr 16) and 0xFF
				val green: Int = (color shr 8) and 0xFF
				val blue: Int = color and 0xFF
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						if (src == 0) {
							dstIndex++
						} else {
							val scaledRed: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
							var scaledGreen: Int = (src and 0xFF00) * green and 0xFF0000
							val scaledBlue: Int = (src and 0xFF) * blue and 0xFF00
							src = (scaledRed or scaledGreen or scaledBlue) ushr 8
							var dst: Int = raster!![dstIndex]
							var sum: Int = src + dst
							var redBlueSum: Int = (src and 0xFF00FF) + (dst and 0xFF00FF)
							dst = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster!![dstIndex++] = sum - dst or dst - (dst ushr 8)
						}
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 3) {
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						var sum: Int = src + color
						var redBlueSum: Int = (src and 0xFF00FF) + (color and 0xFF00FF)
						var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						src = sum - overflow or overflow - (overflow ushr 8)
						overflow = raster!![dstIndex]
						sum = src + overflow
						redBlueSum = (src and 0xFF00FF) + (overflow and 0xFF00FF)
						overflow = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 2) {
				val alpha: Int = color ushr 24
				val invAlpha: Int = 256 - alpha
				var scaledRedBlue: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var scaledGreen: Int = (color and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (scaledRedBlue or scaledGreen) ushr 8
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						if (src == 0) {
							dstIndex++
						} else {
							scaledRedBlue = (src and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
							scaledGreen = (src and 0xFF00) * alpha and 0xFF0000
							src = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
							var dst: Int = raster!![dstIndex]
							var sum: Int = src + dst
							var redBlueSum: Int = (src and 0xFF00FF) + (dst and 0xFF00FF)
							@Pc(1462) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster!![dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
						}
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else {
				throw IllegalArgumentException()
			}
		} else if (op == 1) {
			for (row in -height until 0) {
				var rowEnd: Int = dstIndex + width - 3
				while (dstIndex < rowEnd) {
					raster!![dstIndex++] = this.pixels!![srcIndex++]
					raster!![dstIndex++] = this.pixels!![srcIndex++]
					raster!![dstIndex++] = this.pixels!![srcIndex++]
					raster!![dstIndex++] = this.pixels!![srcIndex++]
				}
				rowEnd += 3
				while (dstIndex < rowEnd) {
					raster!![dstIndex++] = this.pixels!![srcIndex++]
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else if (op == 0) {
			val red: Int = (color shr 16) and 0xFF
			val green: Int = (color shr 8) and 0xFF
			val blue: Int = color and 0xFF
			for (row in -height until 0) {
				for (column in -width until 0) {
					var src: Int = this.pixels!![srcIndex++]
					val scaledRed: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
					var scaledGreen: Int = (src and 0xFF00) * green and 0xFF0000
					val scaledBlue: Int = (src and 0xFF) * blue and 0xFF00
					raster!![dstIndex++] = (scaledRed or scaledGreen or scaledBlue) ushr 8
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else if (op == 3) {
			for (row in -height until 0) {
				for (column in -width until 0) {
					var src: Int = this.pixels!![srcIndex++]
					var sum: Int = src + color
					var redBlueSum: Int = (src and 0xFF00FF) + (color and 0xFF00FF)
					var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
					raster!![dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else if (op == 2) {
			val alpha: Int = color ushr 24
			val invAlpha: Int = 256 - alpha
			var scaledRedBlue: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
			var scaledGreen: Int = (color and 0xFF00) * invAlpha and 0xFF0000
			lerpColour = (scaledRedBlue or scaledGreen) ushr 8
			for (row in -height until 0) {
				for (column in -width until 0) {
					var src: Int = this.pixels!![srcIndex++]
					scaledRedBlue = (src and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
					scaledGreen = (src and 0xFF00) * alpha and 0xFF0000
					raster!![dstIndex++] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIIIIII)V")
	override fun renderDepthTestedRgb(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) op: Int, @OriginalArg(6) colour: Int, @OriginalArg(7) mode: Int) {
		var x: Int = x
		var y: Int = y
		var width: Int = width
		var height: Int = height
		if (width <= 0 || height <= 0) {
			return
		}
		@Pc(6) var u: Int = 0
		@Pc(8) var v: Int = 0
		@Pc(17) val scaleWidth: Int = this.leftMargin + this.imageWidth + this.trimRight
		@Pc(26) val scaleHeight: Int = this.topMargin + this.imageHeight + this.trimBottom
		@Pc(32) val uStep: Int = (scaleWidth shl 16) / width
		@Pc(38) val vStep: Int = (scaleHeight shl 16) / height
		@Pc(52) var offset: Int
		if (this.leftMargin > 0) {
			offset = ((this.leftMargin shl 16) + uStep - 1) / uStep
			x += offset
			u = offset * uStep - (this.leftMargin shl 16)
		}
		if (this.topMargin > 0) {
			offset = ((this.topMargin shl 16) + vStep - 1) / vStep
			y += offset
			v = offset * vStep - (this.topMargin shl 16)
		}
		if (this.imageWidth < scaleWidth) {
			width = ((this.imageWidth shl 16) + uStep - u - 1) / uStep
		}
		if (this.imageHeight < scaleHeight) {
			height = ((this.imageHeight shl 16) + vStep - v - 1) / vStep
		}
		offset = x + y * this.toolkit!!.surfaceWidth
		@Pc(144) var dstStep: Int = this.toolkit!!.surfaceWidth - width
		if (y + height > this.toolkit!!.clipY2) {
			height -= y + height - this.toolkit!!.clipY2
		}
		@Pc(172) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			height -= clip
			offset += clip * this.toolkit!!.surfaceWidth
			v += vStep * clip
		}
		if (x + width > this.toolkit!!.clipX2) {
			clip = x + width - this.toolkit!!.clipX2
			width -= clip
			dstStep += clip
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			width -= clip
			offset += clip
			u += uStep * clip
			dstStep += clip
		}
		@Pc(246) val depth: FloatArray? = this.toolkit!!.depthBuffer
		@Pc(250) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(257) var local257: Int
		@Pc(260) var local260: Int
		@Pc(268) var local268: Int
		@Pc(271) var local271: Int
		@Pc(340) var local340: Int
		@Pc(348) var local348: Int
		@Pc(351) var local351: Int
		@Pc(368) var local368: Int
		@Pc(376) var local376: Int
		@Pc(384) var local384: Int
		@Pc(392) var local392: Int
		@Pc(559) var lerpColour: Int
		if (mode != 0) {
			@Pc(910) var local910: Int
			@Pc(918) var local918: Int
			@Pc(930) var local930: Int
			if (mode == 1) {
				if (op == 1) {
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								if (srcPixel != 0) {
									raster!![offset] = srcPixel
									depth!![offset] = z.toFloat()
								}
							}
							u += uStep
							offset++
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else if (op == 0) {
					val uStart: Int = u
					if ((colour and 0xFFFFFF) == 16777215) {
						val alpha: Int = colour ushr 24
						val invAlpha: Int = 256 - alpha
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								if (z.toFloat() < depth!![offset]) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									if (srcPixel != 0) {
										var dstPixel: Int = raster!![offset]
										raster!![offset] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
										depth!![offset] = z.toFloat()
									}
								}
								u += uStep
								offset++
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					} else {
						val red: Int = (colour shr 16) and 0xFF
						val green: Int = (colour shr 8) and 0xFF
						val blue: Int = colour and 0xFF
						val alpha: Int = colour ushr 24
						val invAlpha: Int = 256 - alpha
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								if (z.toFloat() < depth!![offset]) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									if (srcPixel != 0) {
										if (alpha == 255) {
											val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
											val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
											val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
											raster!![offset] = (tintedRed or tintedGreen or tintedBlue) ushr 8
											depth!![offset] = z.toFloat()
										} else {
											val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
											val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
											val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
											srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
											var dstPixel: Int = raster!![offset]
											raster!![offset] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
											depth!![offset] = z.toFloat()
										}
									}
								}
								u += uStep
								offset++
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					}
				} else if (op == 3) {
					val uStart: Int = u
					val alpha: Int = colour ushr 24
					val invAlpha: Int = 256 - alpha
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								var sum: Int = srcPixel + colour
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = overflow
									overflow = raster!![offset]
									overflow = ((srcPixel and 0xFF00FF) * alpha + (overflow and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (overflow and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
								raster!![offset] = overflow
								depth!![offset] = z.toFloat()
							}
							u += uStep
							offset++
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else if (op == 2) {
					val alpha: Int = colour ushr 24
					val invAlpha: Int = 256 - alpha
					var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
					var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
					lerpColour = (scaledRedBlue or scaledGreen) ushr 8
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								if (srcPixel != 0) {
									scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
									scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
									raster!![offset] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
									depth!![offset] = z.toFloat()
								}
							}
							u += uStep
							offset++
						}
						v += vStep
						u = uStart
						offset += dstStep
					}
				} else {
					throw IllegalArgumentException()
				}
			} else if (mode != 2) {
				throw IllegalArgumentException()
			} else if (op == 1) {
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel != 0) {
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
								depth!![offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 0) {
				val uStart: Int = u
				val red: Int = (colour shr 16) and 0xFF
				val green: Int = (colour shr 8) and 0xFF
				val blue: Int = colour and 0xFF
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel != 0) {
								val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
								val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
								val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
								srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
								depth!![offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 3) {
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							var sum: Int = srcPixel + colour
							var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							srcPixel = sum - overflow or overflow - (overflow ushr 8)
							overflow = raster!![offset]
							sum = srcPixel + overflow
							redBlueSum = (srcPixel and 0xFF00FF) + (overflow and 0xFF00FF)
							overflow = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster!![offset] = sum - overflow or overflow - (overflow ushr 8)
							depth!![offset] = z.toFloat()
						}
						u += uStep
						offset++
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 2) {
				val alpha: Int = colour ushr 24
				val invAlpha: Int = 256 - alpha
				var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (scaledRedBlue or scaledGreen) ushr 8
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel != 0) {
								scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
								scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
								srcPixel = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								@Pc(1809) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset] = sum - overflow or overflow - (overflow ushr 8)
								depth!![offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else {
				throw IllegalArgumentException()
			}
		} else if (op == 1) {
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						raster!![offset] = this.pixels!![(u shr 16) + srcRowOffset]
						depth!![offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = uStart
				offset += dstStep
			}
		} else if (op == 0) {
			val red: Int = (colour shr 16) and 0xFF
			val green: Int = (colour shr 8) and 0xFF
			val blue: Int = colour and 0xFF
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						val tintedRed: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
						val tintedGreen: Int = (srcPixel and 0xFF00) * green and 0xFF0000
						val tintedBlue: Int = (srcPixel and 0xFF) * blue and 0xFF00
						raster!![offset] = (tintedRed or tintedGreen or tintedBlue) ushr 8
						depth!![offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = uStart
				offset += dstStep
			}
		} else if (op == 3) {
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						var sum: Int = srcPixel + colour
						var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
						var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![offset] = sum - overflow or overflow - (overflow ushr 8)
						depth!![offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = uStart
				offset += dstStep
			}
		} else if (op == 2) {
			val alpha: Int = colour ushr 24
			val invAlpha: Int = 256 - alpha
			var scaledRedBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
			var scaledGreen: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
			lerpColour = (scaledRedBlue or scaledGreen) ushr 8
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						scaledRedBlue = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
						scaledGreen = (srcPixel and 0xFF00) * alpha and 0xFF0000
						raster!![offset] = ((scaledRedBlue or scaledGreen) ushr 8) + lerpColour
						depth!![offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = uStart
				offset += dstStep
			}
		} else {
			throw IllegalArgumentException()
		}
	}
}
