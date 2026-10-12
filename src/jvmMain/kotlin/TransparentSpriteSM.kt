import com.jagex.game.runetek5.JavaSpriteBlitState
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ln")
class TransparentSpriteSM : SpriteSM {
	@OriginalMember(owner = "client!ln", name = "H", descriptor = "[I")
	@JvmField val pixels: IntArray?

	@OriginalMember(owner = "client!ln", name = "<init>", descriptor = "(Lclient!ao;[III)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) srcPixels: IntArray?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) : super(javaToolkit, width, height) {
		this.pixels = srcPixels
	}

	@OriginalMember(owner = "client!ln", name = "<init>", descriptor = "(Lclient!ao;[IIIIIZ)V")
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
				this.pixels!![dstIndex++] = srcPixels!![srcOffset++]
			}
			srcOffset += srcSkip
		}
	}

	@OriginalMember(owner = "client!ln", name = "<init>", descriptor = "(Lclient!ao;II)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) : super(javaToolkit, width, height) {
		this.pixels = IntArray(width * height)
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "([I[III)V")
	override fun blitParallelogramMasked(@OriginalArg(0) rowStartX: IntArray?, @OriginalArg(1) rowWidth: IntArray?, @OriginalArg(2) maskX: Int, @OriginalArg(3) maskY: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(217) var skip: Int
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
		@Pc(115) var srcAlpha: Int
		@Pc(119) var srcInvAlpha: Int
		@Pc(123) var dst: Int
		if (JavaSpriteBlitState.dudx == 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight
				while (row < 0) {
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase
						v = JavaSpriteBlitState.vBase
						column = JavaSpriteBlitState.drawWidth
						if (u >= 0 && v >= 0 && u - (this.imageWidth shl 12) < 0 && v - (this.imageHeight shl 12) < 0) {
							maskStart = rowStartX[maskIndex] - maskX
							maskCount = -rowWidth!![maskIndex]
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
								srcAlpha = src ushr 24
								srcInvAlpha = 256 - srcAlpha
								dst = raster!![dstIndex]
								raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						if (u >= 0 && u - (this.imageWidth shl 12) < 0) {
							@Pc(210) var vOverrun: Int
							vOverrun = v - (this.imageHeight shl 12)
							if (vOverrun >= 0) {
								skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
								column += skip
								v += JavaSpriteBlitState.dvdx * skip
								dstIndex += skip
							}
							@Pc(238) var vBound: Int
							vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
							if (vBound > column) {
								column = vBound
							}
							maskStart = rowStartX[maskIndex] - maskX
							maskCount = -rowWidth!![maskIndex]
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
								srcAlpha = src ushr 24
								srcInvAlpha = 256 - srcAlpha
								dst = raster!![dstIndex]
								raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
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
							@Pc(441) var vEndBound: Int
							vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
							if (vEndBound > column) {
								column = vEndBound
							}
							maskStart = rowStartX[maskIndex] - maskX
							maskCount = -rowWidth!![maskIndex]
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
								srcAlpha = src ushr 24
								srcInvAlpha = 256 - srcAlpha
								dst = raster!![dstIndex]
								raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase
						column = JavaSpriteBlitState.drawWidth
						if (v >= 0 && v - (this.imageHeight shl 12) < 0) {
							@Pc(618) var uOverrun: Int
							uOverrun = u - (this.imageWidth shl 12)
							if (uOverrun >= 0) {
								skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
								column += skip
								u += JavaSpriteBlitState.dudx * skip
								dstIndex += skip
							}
							@Pc(646) var uBound: Int
							uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
							if (uBound > column) {
								column = uBound
							}
							maskStart = rowStartX[maskIndex] - maskX
							maskCount = -rowWidth!![maskIndex]
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
								srcAlpha = src ushr 24
								srcInvAlpha = 256 - srcAlpha
								dst = raster!![dstIndex]
								raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						@Pc(814) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(848) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						@Pc(860) var vOverrun: Int
						vOverrun = v - (this.imageHeight shl 12)
						if (vOverrun >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(894) var vBound: Int
						vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vBound > column) {
							column = vBound
						}
						maskStart = rowStartX[maskIndex] - maskX
						maskCount = -rowWidth!![maskIndex]
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
							srcAlpha = src ushr 24
							srcInvAlpha = 256 - srcAlpha
							dst = raster!![dstIndex]
							raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					maskIndex = row + maskY
					if (maskIndex >= 0) {
						if (maskIndex >= rowStartX!!.size) {
							return
						}
						dstIndex = JavaSpriteBlitState.pixelOffset
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX
						column = JavaSpriteBlitState.drawWidth
						@Pc(1068) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(1102) var uBound: Int
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
						@Pc(1150) var vEndBound: Int
						vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vEndBound > column) {
							column = vEndBound
						}
						maskStart = rowStartX[maskIndex] - maskX
						maskCount = -rowWidth!![maskIndex]
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
							srcAlpha = src ushr 24
							srcInvAlpha = 256 - srcAlpha
							dst = raster!![dstIndex]
							raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
				maskIndex = row + maskY
				if (maskIndex >= 0) {
					if (maskIndex >= rowStartX!!.size) {
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
						@Pc(1363) var uEndBound: Int
						uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uEndBound > column) {
							column = uEndBound
						}
						maskStart = rowStartX[maskIndex] - maskX
						maskCount = -rowWidth!![maskIndex]
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
							srcAlpha = src ushr 24
							srcInvAlpha = 256 - srcAlpha
							dst = raster!![dstIndex]
							raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
				maskIndex = row + maskY
				if (maskIndex >= 0) {
					if (maskIndex >= rowStartX!!.size) {
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
					@Pc(1571) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					@Pc(1583) var vOverrun: Int
					vOverrun = v - (this.imageHeight shl 12)
					if (vOverrun >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1617) var vBound: Int
					vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vBound > column) {
						column = vBound
					}
					maskStart = rowStartX[maskIndex] - maskX
					maskCount = -rowWidth!![maskIndex]
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
						srcAlpha = src ushr 24
						srcInvAlpha = 256 - srcAlpha
						dst = raster!![dstIndex]
						raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
				maskIndex = row + maskY
				if (maskIndex >= 0) {
					if (maskIndex >= rowStartX!!.size) {
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
					@Pc(1827) var uEndBound: Int
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
					@Pc(1875) var vEndBound: Int
					vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vEndBound > column) {
						column = vEndBound
					}
					maskStart = rowStartX[maskIndex] - maskX
					maskCount = -rowWidth!![maskIndex]
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
						srcAlpha = src ushr 24
						srcInvAlpha = 256 - srcAlpha
						dst = raster!![dstIndex]
						raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (dst and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dst and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) srcX: Int, @OriginalArg(5) srcY: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		for (@Pc(5) row in 0 until height) {
			@Pc(14) val dstIndex: Int = (y + row) * width + x
			@Pc(22) val srcIndex: Int = (srcY + row) * width + srcX
			for (@Pc(24) column in 0 until width) {
				this.pixels!![dstIndex + column] = raster!![srcIndex + column] and 0xFFFFFF
			}
		}
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(IIIII)V")
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
			@Pc(717) var local717: Int
			@Pc(725) var local725: Int
			@Pc(737) var local737: Int
			if (mode == 1) {
				if (op == 1) {
					for (row in -height until 0) {
						for (column in -width until 0) {
							var src: Int = this.pixels!![srcIndex++]
							val alpha: Int = src ushr 24
							val invAlpha: Int = 256 - alpha
							var background: Int = raster!![dstIndex]
							raster[dstIndex++] = (((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha shr 8) and 0xFFFF00FF.toInt()) + (((src and 0xFF00FF00.toInt()) ushr 8) * alpha + ((background and 0xFF00FF00.toInt()) ushr 8) * invAlpha and 0xFF00FF00.toInt())
						}
						dstIndex += dstStep
						srcIndex += srcStep
					}
				} else if (op == 0) {
					if (color and 0xFFFFFF == 16777215) {
						for (row in -height until 0) {
							for (column in -width until 0) {
								var src: Int = this.pixels!![srcIndex++]
								val alpha: Int = (src ushr 24) * (color ushr 24) shr 8
								val invAlpha: Int = 256 - alpha
								var background: Int = raster!![dstIndex]
								raster[dstIndex++] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					} else {
						val red: Int = (color shr 16) and 0xFF
						var green: Int = (color shr 8) and 0xFF
						val blue: Int = color and 0xFF
						for (row in -height until 0) {
							for (column in -width until 0) {
								var src: Int = this.pixels!![srcIndex++]
								val alpha: Int = (src ushr 24) * (color ushr 24) shr 8
								val invAlpha: Int = 256 - alpha
								if (alpha == 255) {
									val redPart: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * blue and 0xFF00
									raster!![dstIndex++] = (redPart or greenPart or bluePart) ushr 8
								} else {
									val redPart: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * blue and 0xFF00
									src = (redPart or greenPart or bluePart) ushr 8
									var background: Int = raster!![dstIndex]
									raster[dstIndex++] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					}
				} else if (op == 3) {
					for (row in -height until 0) {
						for (column in -width until 0) {
							var src: Int = this.pixels!![srcIndex++]
							var sum: Int = src + color
							var redBlueSum: Int = (src and 0xFF00FF) + (color and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							overflow = sum - overflow or overflow - (overflow ushr 8)
							val alpha: Int = (overflow ushr 24) * (color ushr 24) shr 8
							val invAlpha: Int = 256 - alpha
							if (alpha != 255) {
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
					val tintAlpha: Int = color ushr 24
					val tintInvAlpha: Int = 256 - tintAlpha
					var redBlue: Int = (color and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
					var green: Int = (color and 0xFF00) * tintInvAlpha and 0xFF0000
					lerpColour = (redBlue or green) ushr 8
					for (row in -height until 0) {
						for (column in -width until 0) {
							var src: Int = this.pixels!![srcIndex++]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							redBlue = (src and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
							green = (src and 0xFF00) * tintAlpha and 0xFF0000
							src = ((redBlue or green) ushr 8) + lerpColour
							var background: Int = raster!![dstIndex]
							raster[dstIndex++] = ((src and 0xFF00FF) * srcAlpha + (background and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (background and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
							var background: Int = raster!![dstIndex]
							var sum: Int = src + background
							var redBlueSum: Int = (src and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster[dstIndex++] = sum - background or background - (background ushr 8)
						}
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 0) {
				val red: Int = (color shr 16) and 0xFF
				var green: Int = (color shr 8) and 0xFF
				val blue: Int = color and 0xFF
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						if (src == 0) {
							dstIndex++
						} else {
							val redPart: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
							val greenPart: Int = (src and 0xFF00) * green and 0xFF0000
							val bluePart: Int = (src and 0xFF) * blue and 0xFF00
							src = (redPart or greenPart or bluePart) ushr 8
							var background: Int = raster!![dstIndex]
							var sum: Int = src + background
							var redBlueSum: Int = (src and 0xFF00FF) + (background and 0xFF00FF)
							background = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster[dstIndex++] = sum - background or background - (background ushr 8)
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
						raster[dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 2) {
				val tintAlpha: Int = color ushr 24
				val tintInvAlpha: Int = 256 - tintAlpha
				var redBlue: Int = (color and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
				var green: Int = (color and 0xFF00) * tintInvAlpha and 0xFF0000
				lerpColour = (redBlue or green) ushr 8
				for (row in -height until 0) {
					for (column in -width until 0) {
						var src: Int = this.pixels!![srcIndex++]
						if (src == 0) {
							dstIndex++
						} else {
							redBlue = (src and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
							green = (src and 0xFF00) * tintAlpha and 0xFF0000
							src = ((redBlue or green) ushr 8) + lerpColour
							var background: Int = raster!![dstIndex]
							var sum: Int = src + background
							var redBlueSum: Int = (src and 0xFF00FF) + (background and 0xFF00FF)
							@Pc(1480) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							raster[dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
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
					raster[dstIndex++] = this.pixels!![srcIndex++]
					raster[dstIndex++] = this.pixels!![srcIndex++]
					raster[dstIndex++] = this.pixels!![srcIndex++]
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
			var green: Int = (color shr 8) and 0xFF
			val blue: Int = color and 0xFF
			for (row in -height until 0) {
				for (column in -width until 0) {
					var src: Int = this.pixels!![srcIndex++]
					val redPart: Int = (src and 0xFF0000) * red and 0xFF000000.toInt()
					val greenPart: Int = (src and 0xFF00) * green and 0xFF0000
					val bluePart: Int = (src and 0xFF) * blue and 0xFF00
					raster!![dstIndex++] = (redPart or greenPart or bluePart) ushr 8
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
			val tintAlpha: Int = color ushr 24
			val tintInvAlpha: Int = 256 - tintAlpha
			var redBlue: Int = (color and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
			var green: Int = (color and 0xFF00) * tintInvAlpha and 0xFF0000
			lerpColour = (redBlue or green) ushr 8
			for (row in -height until 0) {
				for (column in -width until 0) {
					var src: Int = this.pixels!![srcIndex++]
					redBlue = (src and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
					green = (src and 0xFF00) * tintAlpha and 0xFF0000
					raster!![dstIndex++] = ((redBlue or green) ushr 8) + lerpColour
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(IILclient!aa;II)V")
	override fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) surface: ComponentSurface?, @OriginalArg(3) maskX: Int, @OriginalArg(4) maskY: Int) {
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
		@Pc(159) val javaClippingMask: JavaClippingMask? = surface as JavaClippingMask?
		@Pc(162) val lineWidths: IntArray? = javaClippingMask!!.lineWidths
		@Pc(165) val lineOffsets: IntArray? = javaClippingMask.lineOffsets
		@Pc(169) val surfaceRaster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(171) var startY: Int = y
		if (maskY > y) {
			startY = maskY
			dstIndex += (maskY - y) * surfaceWidth
			srcIndex += (maskY - y) * this.imageWidth
		}
		@Pc(210) val endY: Int = if (maskY + lineWidths!!.size < y + imageHeight) maskY + lineWidths.size else y + imageHeight
		for (@Pc(212) i in startY until endY) {
			@Pc(221) val spanStart: Int = lineWidths[i - maskY] + maskX
			@Pc(227) var spanWidth: Int = lineOffsets!![i - maskY]
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
				@Pc(315) val alpha: Int = src ushr 24
				@Pc(319) val invAlpha: Int = 256 - alpha
				@Pc(323) val dst: Int = surfaceRaster!![dstIndex]
				surfaceRaster[dstIndex++] = ((src and 0xFF00FF) * alpha + (dst and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (dst and 0xFF00) * invAlpha and 0xFF0000) shr 8
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(IIIIIIII)V")
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
				@Pc(883) var local883: Int
				@Pc(891) var local891: Int
				@Pc(903) var local903: Int
				if (mode == 1) {
					if (op == 1) {
						val rowStartU: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								val alpha: Int = srcPixel ushr 24
								val invAlpha: Int = 256 - alpha
								var dstPixel: Int = raster!![offset]
								raster[offset++] = (((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha shr 8) and 0xFFFF00FF.toInt()) + (((srcPixel and 0xFF00FF00.toInt()) ushr 8) * alpha + ((dstPixel and 0xFF00FF00.toInt()) ushr 8) * invAlpha and 0xFF00FF00.toInt())
								u += uStep
							}
							v += vStep
							u = rowStartU
							offset += dstStep
						}
					} else if (op == 0) {
						val rowStartU: Int = u
						if (colour and 0xFFFFFF == 16777215) {
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									val alpha: Int = (srcPixel ushr 24) * (colour ushr 24) shr 8
									val invAlpha: Int = 256 - alpha
									var dstPixel: Int = raster!![offset]
									raster[offset++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									u += uStep
								}
								v += vStep
								u = rowStartU
								offset += dstStep
							}
						} else {
							val tintRed: Int = (colour shr 16) and 0xFF
							val tintGreen: Int = (colour shr 8) and 0xFF
							val tintBlue: Int = colour and 0xFF
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									val alpha: Int = (srcPixel ushr 24) * (colour ushr 24) shr 8
									val invAlpha: Int = 256 - alpha
									if (alpha == 255) {
										val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
										raster!![offset++] = (tintedRed or tintedGreen or tintedBlue) ushr 8
									} else {
										val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
										srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
										var dstPixel: Int = raster!![offset]
										raster[offset++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									}
									u += uStep
								}
								v += vStep
								u = rowStartU
								offset += dstStep
							}
						}
					} else if (op == 3) {
						val rowStartU: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								var sum: Int = srcPixel + colour
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
								var outPixel: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								outPixel = sum - outPixel or outPixel - (outPixel ushr 8)
								val alpha: Int = (outPixel ushr 24) * (colour ushr 24) shr 8
								val invAlpha: Int = 256 - alpha
								if (alpha != 255) {
									srcPixel = outPixel
									outPixel = raster!![offset]
									outPixel = ((srcPixel and 0xFF00FF) * alpha + (outPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (outPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
								raster!![offset++] = outPixel
								u += uStep
							}
							v += vStep
							u = rowStartU
							offset += dstStep
						}
					} else if (op == 2) {
						val tintAlpha: Int = colour ushr 24
						val tintInvAlpha: Int = 256 - tintAlpha
						var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
						var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
						lerpColour = (redBlueScaled or greenScaled) ushr 8
						val rowStartU: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								val alpha: Int = srcPixel ushr 24
								val invAlpha: Int = 256 - alpha
								redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
								greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
								srcPixel = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								raster[offset++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								u += uStep
							}
							v += vStep
							u = rowStartU
							offset += dstStep
						}
					} else {
						throw IllegalArgumentException()
					}
				} else if (mode != 2) {
					throw IllegalArgumentException()
				} else if (op == 1) {
					val rowStartU: Int = u
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
								raster[offset++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else if (op == 0) {
					val rowStartU: Int = u
					val tintRed: Int = (colour shr 16) and 0xFF
					val tintGreen: Int = (colour shr 8) and 0xFF
					val tintBlue: Int = colour and 0xFF
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel == 0) {
								offset++
							} else {
								val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
								val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
								val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
								srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster[offset++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else if (op == 3) {
					val rowStartU: Int = u
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
							raster[offset++] = sum - overflow or overflow - (overflow ushr 8)
							u += uStep
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else if (op == 2) {
					val tintAlpha: Int = colour ushr 24
					val tintInvAlpha: Int = 256 - tintAlpha
					var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
					var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
					lerpColour = (redBlueScaled or greenScaled) ushr 8
					val rowStartU: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel == 0) {
								offset++
							} else {
								redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
								greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
								srcPixel = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								@Pc(1754) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster[offset++] = sum - overflow or overflow - (overflow ushr 8)
							}
							u += uStep
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else {
					throw IllegalArgumentException()
				}
			} else if (op == 1) {
				val rowStartU: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						raster!![offset++] = this.pixels!![(u shr 16) + srcRowOffset]
						u += uStep
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 0) {
				val tintRed: Int = (colour shr 16) and 0xFF
				val tintGreen: Int = (colour shr 8) and 0xFF
				val tintBlue: Int = colour and 0xFF
				val rowStartU: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
						val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
						val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
						raster!![offset++] = (tintedRed or tintedGreen or tintedBlue) ushr 8
						u += uStep
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 3) {
				val rowStartU: Int = u
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
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 2) {
				val tintAlpha: Int = colour ushr 24
				val tintInvAlpha: Int = 256 - tintAlpha
				var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
				var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
				lerpColour = (redBlueScaled or greenScaled) ushr 8
				val rowStartU: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
						greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
						raster!![offset++] = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
						u += uStep
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else {
				throw IllegalArgumentException()
			}
		}
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(IIIIIIIII)V")
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
			@Pc(960) var local960: Int
			@Pc(968) var local968: Int
			@Pc(980) var local980: Int
			if (mode == 1) {
				if (op == 1) {
					val rowStartU: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								val alpha: Int = srcPixel ushr 24
								val invAlpha: Int = 256 - alpha
								var dstPixel: Int = raster!![offset]
								raster[offset] = (((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha shr 8) and 0xFFFF00FF.toInt()) + (((srcPixel and 0xFF00FF00.toInt()) ushr 8) * alpha + ((dstPixel and 0xFF00FF00.toInt()) ushr 8) * invAlpha and 0xFF00FF00.toInt())
								depth[offset] = z.toFloat()
							}
							u += uStep
							offset++
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else if (op == 0) {
					val rowStartU: Int = u
					if (colour and 0xFFFFFF == 16777215) {
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								if (z.toFloat() < depth!![offset]) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									val alpha: Int = (srcPixel ushr 24) * (colour ushr 24) shr 8
									val invAlpha: Int = 256 - alpha
									var dstPixel: Int = raster!![offset]
									raster[offset] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									depth[offset] = z.toFloat()
								}
								u += uStep
								offset++
							}
							v += vStep
							u = rowStartU
							offset += dstStep
						}
					} else {
						val tintRed: Int = (colour shr 16) and 0xFF
						val tintGreen: Int = (colour shr 8) and 0xFF
						val tintBlue: Int = colour and 0xFF
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								if (z.toFloat() < depth!![offset]) {
									var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
									val alpha: Int = (srcPixel ushr 24) * (colour ushr 24) shr 8
									val invAlpha: Int = 256 - alpha
									if (alpha == 255) {
										val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
										raster!![offset] = (tintedRed or tintedGreen or tintedBlue) ushr 8
										depth[offset] = z.toFloat()
									} else {
										val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
										val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
										val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
										srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
										var dstPixel: Int = raster!![offset]
										raster[offset] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
										depth[offset] = z.toFloat()
									}
								}
								u += uStep
								offset++
							}
							v += vStep
							u = rowStartU
							offset += dstStep
						}
					}
				} else if (op == 3) {
					val rowStartU: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								var sum: Int = srcPixel + colour
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
								var outPixel: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								outPixel = sum - outPixel or outPixel - (outPixel ushr 8)
								val alpha: Int = (outPixel ushr 24) * (colour ushr 24) shr 8
								val invAlpha: Int = 256 - alpha
								if (alpha != 255) {
									srcPixel = outPixel
									outPixel = raster!![offset]
									outPixel = ((srcPixel and 0xFF00FF) * alpha + (outPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (outPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
								raster!![offset] = outPixel
								depth[offset] = z.toFloat()
							}
							u += uStep
							offset++
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else if (op == 2) {
					val tintAlpha: Int = colour ushr 24
					val tintInvAlpha: Int = 256 - tintAlpha
					var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
					var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
					lerpColour = (redBlueScaled or greenScaled) ushr 8
					val rowStartU: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
								val alpha: Int = srcPixel ushr 24
								val invAlpha: Int = 256 - alpha
								redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
								greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
								srcPixel = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								raster[offset] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
								depth[offset] = z.toFloat()
							}
							u += uStep
							offset++
						}
						v += vStep
						u = rowStartU
						offset += dstStep
					}
				} else {
					throw IllegalArgumentException()
				}
			} else if (mode != 2) {
				throw IllegalArgumentException()
			} else if (op == 1) {
				val rowStartU: Int = u
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
								raster[offset] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
								depth[offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 0) {
				val rowStartU: Int = u
				val tintRed: Int = (colour shr 16) and 0xFF
				val tintGreen: Int = (colour shr 8) and 0xFF
				val tintBlue: Int = colour and 0xFF
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel != 0) {
								val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
								val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
								val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
								srcPixel = (tintedRed or tintedGreen or tintedBlue) ushr 8
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								dstPixel = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster[offset] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
								depth[offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 3) {
				val rowStartU: Int = u
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
							raster[offset] = sum - overflow or overflow - (overflow ushr 8)
							depth[offset] = z.toFloat()
						}
						u += uStep
						offset++
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else if (op == 2) {
				val tintAlpha: Int = colour ushr 24
				val tintInvAlpha: Int = 256 - tintAlpha
				var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
				var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
				lerpColour = (redBlueScaled or greenScaled) ushr 8
				val rowStartU: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
							if (srcPixel != 0) {
								redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
								greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
								srcPixel = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
								var dstPixel: Int = raster!![offset]
								var sum: Int = srcPixel + dstPixel
								var redBlueSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
								@Pc(1903) var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster[offset] = sum - overflow or overflow - (overflow ushr 8)
								depth[offset] = z.toFloat()
							}
						}
						u += uStep
						offset++
					}
					v += vStep
					u = rowStartU
					offset += dstStep
				}
			} else {
				throw IllegalArgumentException()
			}
		} else if (op == 1) {
			val rowStartU: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						raster!![offset] = this.pixels!![(u shr 16) + srcRowOffset]
						depth[offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = rowStartU
				offset += dstStep
			}
		} else if (op == 0) {
			val tintRed: Int = (colour shr 16) and 0xFF
			val tintGreen: Int = (colour shr 8) and 0xFF
			val tintBlue: Int = colour and 0xFF
			val rowStartU: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						val tintedRed: Int = (srcPixel and 0xFF0000) * tintRed and 0xFF000000.toInt()
						val tintedGreen: Int = (srcPixel and 0xFF00) * tintGreen and 0xFF0000
						val tintedBlue: Int = (srcPixel and 0xFF) * tintBlue and 0xFF00
						raster!![offset] = (tintedRed or tintedGreen or tintedBlue) ushr 8
						depth[offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = rowStartU
				offset += dstStep
			}
		} else if (op == 3) {
			val rowStartU: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						var sum: Int = srcPixel + colour
						var redBlueSum: Int = (srcPixel and 0xFF00FF) + (colour and 0xFF00FF)
						var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![offset] = sum - overflow or overflow - (overflow ushr 8)
						depth[offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = rowStartU
				offset += dstStep
			}
		} else if (op == 2) {
			val tintAlpha: Int = colour ushr 24
			val tintInvAlpha: Int = 256 - tintAlpha
			var redBlueScaled: Int = (colour and 0xFF00FF) * tintInvAlpha and 0xFF00FF00.toInt()
			var greenScaled: Int = (colour and 0xFF00) * tintInvAlpha and 0xFF0000
			lerpColour = (redBlueScaled or greenScaled) ushr 8
			val rowStartU: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcPixel: Int = this.pixels!![(u shr 16) + srcRowOffset]
						redBlueScaled = (srcPixel and 0xFF00FF) * tintAlpha and 0xFF00FF00.toInt()
						greenScaled = (srcPixel and 0xFF00) * tintAlpha and 0xFF0000
						raster!![offset] = ((redBlueScaled or greenScaled) ushr 8) + lerpColour
						depth[offset] = z.toFloat()
					}
					u += uStep
					offset++
				}
				v += vStep
				u = rowStartU
				offset += dstStep
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ln", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) channel: Int) {
		@Pc(5) var surfaceRaster: IntArray?
		@Pc(7) var y: Int
		@Pc(13) var pixelRow: Int
		@Pc(23) var surfaceRow: Int
		@Pc(25) var x: Int
		if (channel == 0) {
			surfaceRaster = this.toolkit!!.surfaceRaster
			y = 0
			while (y < this.imageHeight) {
				pixelRow = y * this.imageWidth
				surfaceRow = y * this.toolkit!!.surfaceWidth
				x = 0
				while (x < this.imageWidth) {
					this.pixels!![pixelRow + x] = (this.pixels!![pixelRow + x] and 0xFFFFFF) or ((surfaceRaster!![surfaceRow + x] and 0xFF0000) shl 8)
					x++
				}
				y++
			}
		} else if (channel == 3) {
			surfaceRaster = this.toolkit!!.surfaceRaster
			y = 0
			while (y < this.imageHeight) {
				pixelRow = y * this.imageWidth
				surfaceRow = y * this.toolkit!!.surfaceWidth
				x = 0
				while (x < this.imageWidth) {
					this.pixels!![pixelRow + x] = (this.pixels!![pixelRow + x] and 0xFFFFFF) or (if (surfaceRaster!![surfaceRow + x] == 0) 0 else -16777216)
					x++
				}
				y++
			}
		}
	}

	@OriginalMember(owner = "client!ln", name = "b", descriptor = "(II)V")
	override fun blitParallelogram(@OriginalArg(0) op: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(935) var skip: Int
		@Pc(9) var row: Int
		@Pc(12) var dstIndex: Int
		@Pc(14) var u: Int
		@Pc(16) var v: Int
		@Pc(18) var column: Int
		@Pc(48) var texel: Int
		@Pc(51) var dst: Int
		@Pc(209) var src: Int
		@Pc(213) var local213: Int
		@Pc(217) var local217: Int
		@Pc(221) var local221: Int
		@Pc(334) var local334: Int
		@Pc(342) var local342: Int
		@Pc(354) var local354: Int
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
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (op == 0) {
								src = this.pixels!![texel]
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								} else if (srcAlpha == 255) {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (red or green or blue) ushr 8
								} else {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (red or green or blue) ushr 8
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val blendColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + blendColour
								val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
								var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								result = sum - result or result - (result ushr 8)
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (srcAlpha != 255) {
									src = result
									result = raster!![dst]
									result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = result
							} else if (op == 2) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
						@Pc(928) var vOverrun: Int
						vOverrun = v - (this.imageHeight shl 12)
						if (vOverrun >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
							column += skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(956) var vBound: Int
						vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vBound > column) {
							column = vBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (op == 0) {
								src = this.pixels!![texel]
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								} else if (srcAlpha == 255) {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (red or green or blue) ushr 8
								} else {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (red or green or blue) ushr 8
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val blendColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + blendColour
								val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
								var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								result = sum - result or result - (result ushr 8)
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (srcAlpha != 255) {
									src = result
									result = raster!![dst]
									result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = result
							} else if (op == 2) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
						@Pc(1888) var vEndBound: Int
						vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vEndBound > column) {
							column = vEndBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (op == 0) {
								src = this.pixels!![texel]
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								} else if (srcAlpha == 255) {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (red or green or blue) ushr 8
								} else {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (red or green or blue) ushr 8
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val blendColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + blendColour
								val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
								var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								result = sum - result or result - (result ushr 8)
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (srcAlpha != 255) {
									src = result
									result = raster!![dst]
									result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = result
							} else if (op == 2) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
						@Pc(2794) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							dstIndex += skip
						}
						@Pc(2822) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (op == 0) {
								src = this.pixels!![texel]
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								} else if (srcAlpha == 255) {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (red or green or blue) ushr 8
								} else {
									val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (red or green or blue) ushr 8
									val dstPixel: Int = raster!![dst]
									raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
							} else if (op == 3) {
								src = this.pixels!![texel]
								val blendColour: Int = JavaSpriteBlitState.colour
								val sum: Int = src + blendColour
								val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
								var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								result = sum - result or result - (result ushr 8)
								val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
								val srcInvAlpha: Int = 256 - srcAlpha
								if (srcAlpha != 255) {
									src = result
									result = raster!![dst]
									result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = result
							} else if (op == 2) {
								src = this.pixels!![texel]
								val srcAlpha: Int = src ushr 24
								val srcInvAlpha: Int = 256 - srcAlpha
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					@Pc(3719) var uOverrun: Int
					uOverrun = u - (this.imageWidth shl 12)
					if (uOverrun >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(3753) var uBound: Int
					uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uBound > column) {
						column = uBound
					}
					@Pc(3765) var vOverrun: Int
					vOverrun = v - (this.imageHeight shl 12)
					if (vOverrun >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(3799) var vBound: Int
					vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vBound > column) {
						column = vBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						} else if (op == 0) {
							src = this.pixels!![texel]
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (srcAlpha == 255) {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								raster!![dst] = (red or green or blue) ushr 8
							} else {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								src = (red or green or blue) ushr 8
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val blendColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + blendColour
							val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
							var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							result = sum - result or result - (result ushr 8)
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (srcAlpha != 255) {
								src = result
								result = raster!![dst]
								result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = result
						} else if (op == 2) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
							src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					@Pc(4702) var uOverrun: Int
					uOverrun = u - (this.imageWidth shl 12)
					if (uOverrun >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(4736) var uBound: Int
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
					@Pc(4784) var vEndBound: Int
					vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
					if (vEndBound > column) {
						column = vEndBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						} else if (op == 0) {
							src = this.pixels!![texel]
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (srcAlpha == 255) {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								raster!![dst] = (red or green or blue) ushr 8
							} else {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								src = (red or green or blue) ushr 8
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val blendColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + blendColour
							val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
							var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							result = sum - result or result - (result ushr 8)
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (srcAlpha != 255) {
								src = result
								result = raster!![dst]
								result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = result
						} else if (op == 2) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
							src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
					@Pc(5726) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						} else if (op == 0) {
							src = this.pixels!![texel]
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							} else if (srcAlpha == 255) {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								raster!![dst] = (red or green or blue) ushr 8
							} else {
								val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
								val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
								src = (red or green or blue) ushr 8
								val dstPixel: Int = raster!![dst]
								raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
						} else if (op == 3) {
							src = this.pixels!![texel]
							val blendColour: Int = JavaSpriteBlitState.colour
							val sum: Int = src + blendColour
							val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
							var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							result = sum - result or result - (result ushr 8)
							val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
							val srcInvAlpha: Int = 256 - srcAlpha
							if (srcAlpha != 255) {
								src = result
								result = raster!![dst]
								result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = result
						} else if (op == 2) {
							src = this.pixels!![texel]
							val srcAlpha: Int = src ushr 24
							val srcInvAlpha: Int = 256 - srcAlpha
							val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
							src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
				@Pc(6663) var uEndBound: Int
				uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
				if (uEndBound > column) {
					column = uEndBound
				}
				@Pc(6675) var vOverrun: Int
				vOverrun = v - (this.imageHeight shl 12)
				if (vOverrun >= 0) {
					skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
					column += skip
					u += JavaSpriteBlitState.dudx * skip
					v += JavaSpriteBlitState.dvdx * skip
					dstIndex += skip
				}
				@Pc(6709) var vBound: Int
				vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
				if (vBound > column) {
					column = vBound
				}
				while (column < 0) {
					texel = (v shr 12) * this.imageWidth + (u shr 12)
					dst = dstIndex++
					if (op == 1) {
						src = this.pixels!![texel]
						val srcAlpha: Int = src ushr 24
						val srcInvAlpha: Int = 256 - srcAlpha
						val dstPixel: Int = raster!![dst]
						raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
					} else if (op == 0) {
						src = this.pixels!![texel]
						val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
						val srcInvAlpha: Int = 256 - srcAlpha
						if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						} else if (srcAlpha == 255) {
							val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							raster!![dst] = (red or green or blue) ushr 8
						} else {
							val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							src = (red or green or blue) ushr 8
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						}
					} else if (op == 3) {
						src = this.pixels!![texel]
						val blendColour: Int = JavaSpriteBlitState.colour
						val sum: Int = src + blendColour
						val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
						var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						result = sum - result or result - (result ushr 8)
						val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
						val srcInvAlpha: Int = 256 - srcAlpha
						if (srcAlpha != 255) {
							src = result
							result = raster!![dst]
							result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						}
						raster!![dst] = result
					} else if (op == 2) {
						src = this.pixels!![texel]
						val srcAlpha: Int = src ushr 24
						val srcInvAlpha: Int = 256 - srcAlpha
						val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
						val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
						src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
						val dstPixel: Int = raster!![dst]
						raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
				@Pc(7648) var uEndBound: Int
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
				@Pc(7696) var vEndBound: Int
				vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
				if (vEndBound > column) {
					column = vEndBound
				}
				while (column < 0) {
					texel = (v shr 12) * this.imageWidth + (u shr 12)
					dst = dstIndex++
					if (op == 1) {
						src = this.pixels!![texel]
						val srcAlpha: Int = src ushr 24
						val srcInvAlpha: Int = 256 - srcAlpha
						val dstPixel: Int = raster!![dst]
						raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
					} else if (op == 0) {
						src = this.pixels!![texel]
						val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
						val srcInvAlpha: Int = 256 - srcAlpha
						if (JavaSpriteBlitState.colour and 0xFFFFFF == 16777215) {
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						} else if (srcAlpha == 255) {
							val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							raster!![dst] = (red or green or blue) ushr 8
						} else {
							val red: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val green: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val blue: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							src = (red or green or blue) ushr 8
							val dstPixel: Int = raster!![dst]
							raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						}
					} else if (op == 3) {
						src = this.pixels!![texel]
						val blendColour: Int = JavaSpriteBlitState.colour
						val sum: Int = src + blendColour
						val redBlueSum: Int = (src and 0xFF00FF) + (blendColour and 0xFF00FF)
						var result: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						result = sum - result or result - (result ushr 8)
						val srcAlpha: Int = (src ushr 24) * JavaSpriteBlitState.alpha shr 8
						val srcInvAlpha: Int = 256 - srcAlpha
						if (srcAlpha != 255) {
							src = result
							result = raster!![dst]
							result = ((src and 0xFF00FF) * srcAlpha + (result and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (result and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
						}
						raster!![dst] = result
					} else if (op == 2) {
						src = this.pixels!![texel]
						val srcAlpha: Int = src ushr 24
						val srcInvAlpha: Int = 256 - srcAlpha
						val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
						val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
						src = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
						val dstPixel: Int = raster!![dst]
						raster[dst] = ((src and 0xFF00FF) * srcAlpha + (dstPixel and 0xFF00FF) * srcInvAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * srcAlpha + (dstPixel and 0xFF00) * srcInvAlpha and 0xFF0000) shr 8
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
}
