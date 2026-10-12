import com.jagex.game.runetek5.JavaSpriteBlitState
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!faa")
class PaletteSpriteSM : SpriteSM {

	@OriginalMember(owner = "client!faa", name = "H", descriptor = "[B")
	private val spritePixels: ByteArray?

	@OriginalMember(owner = "client!faa", name = "I", descriptor = "[I")
	private val spritePalette: IntArray?

	@OriginalMember(owner = "client!faa", name = "<init>", descriptor = "(Lclient!ao;[B[III)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) spritePixels: ByteArray?, @OriginalArg(2) spritePalette: IntArray?, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int) : super(javaToolkit, width, height) {
		this.spritePixels = spritePixels
		this.spritePalette = spritePalette
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) channel: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIIIIII)V")
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
		@Pc(345) var local345: Int
		@Pc(353) var local353: Int
		@Pc(356) var local356: Int
		@Pc(378) var local378: Int
		@Pc(394) var local394: Int
		@Pc(402) var local402: Int
		@Pc(578) var lerpColour: Int
		@Pc(475) var paletteIndex: Byte
		@Pc(386) var local386: Int
		if (mode != 0) {
			@Pc(1295) var local1295: Byte
			@Pc(945) var local945: Int
			@Pc(953) var local953: Int
			@Pc(961) var local961: Int
			@Pc(973) var local973: Int
			if (mode == 1) {
				if (op == 1) {
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							if (z.toFloat() < depth!![offset]) {
								paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
								if (paletteIndex.toInt() != 0) {
									raster!![offset] = this.spritePalette!![paletteIndex.toInt() and 0xFF]
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
					@Pc(798) var local798: Byte
					if (op == 0) {
						val uStart: Int = u
						if ((colour and 0xFFFFFF) == 16777215) {
							val alpha: Int = colour ushr 24
							val invAlpha: Int = 256 - alpha
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									if (z.toFloat() < depth!![offset]) {
										val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
										if (colourIndex.toInt() != 0) {
											var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
											var dstColour: Int = raster!![offset]
											raster!![offset] = ((srcColour and 0xFF00FF) * alpha + (dstColour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (dstColour and 0xFF00) * invAlpha and 0xFF0000) shr 8
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
							var green: Int = (colour shr 8) and 0xFF
							val blue: Int = colour and 0xFF
							val alpha: Int = colour ushr 24
							val invAlpha: Int = 256 - alpha
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									if (z.toFloat() < depth!![offset]) {
										@Pc(925) val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
										if (colourIndex.toInt() != 0) {
											var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
											if (alpha == 255) {
												val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
												val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
												val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
												raster!![offset] = (scaledRed or scaledGreen or scaledBlue) ushr 8
												depth!![offset] = z.toFloat()
											} else {
												val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
												val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
												val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
												srcColour = (scaledRed or scaledGreen or scaledBlue) ushr 8
												var dstColour: Int = raster!![offset]
												raster!![offset] = ((srcColour and 0xFF00FF) * alpha + (dstColour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (dstColour and 0xFF00) * invAlpha and 0xFF0000) shr 8
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
									val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
									var srcColour: Int = if (colourIndex.toInt() > 0) this.spritePalette!![colourIndex.toInt()] else 0
									var sum: Int = srcColour + colour
									var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
									var pixel: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
									pixel = sum - pixel or pixel - (pixel ushr 8)
									if (srcColour == 0 && alpha != 255) {
										srcColour = pixel
										pixel = raster!![offset]
										pixel = ((srcColour and 0xFF00FF) * alpha + (pixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (pixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									}
									raster!![offset] = pixel
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
						var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
						var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
						lerpColour = (redBlue or green) ushr 8
						val uStart: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								if (z.toFloat() < depth!![offset]) {
									val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
									if (colourIndex.toInt() != 0) {
										var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
										redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
										green = (srcColour and 0xFF00) * alpha and 0xFF0000
										raster!![offset] = ((redBlue or green) ushr 8) + lerpColour
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
				}
			} else if (mode != 2) {
				throw IllegalArgumentException()
			} else if (op == 1) {
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (paletteIndex.toInt() != 0) {
								var srcColour: Int = this.spritePalette!![paletteIndex.toInt() and 0xFF]
								var dstColour: Int = raster!![offset]
								var sum: Int = srcColour + dstColour
								var redBlueSum: Int = (srcColour and 0xFF00FF) + (dstColour and 0xFF00FF)
								dstColour = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset] = sum - dstColour or dstColour - (dstColour ushr 8)
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
				var green: Int = (colour shr 8) and 0xFF
				val blue: Int = colour and 0xFF
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							@Pc(1525) val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (colourIndex.toInt() != 0) {
								var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
								val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
								val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
								val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
								srcColour = (scaledRed or scaledGreen or scaledBlue) ushr 8
								var dstColour: Int = raster!![offset]
								var sum: Int = srcColour + dstColour
								var redBlueSum: Int = (srcColour and 0xFF00FF) + (dstColour and 0xFF00FF)
								dstColour = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset] = sum - dstColour or dstColour - (dstColour ushr 8)
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
							paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
							var srcColour: Int = if (paletteIndex.toInt() > 0) this.spritePalette!![paletteIndex.toInt()] else 0
							var sum: Int = srcColour + colour
							var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
							val carry: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							srcColour = sum - carry or carry - (carry ushr 8)
							@Pc(1712) val dst: Int = raster!![offset]
							sum = srcColour + dst
							redBlueSum = (srcColour and 0xFF00FF) + (dst and 0xFF00FF)
							@Pc(1734) val overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
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
				var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (redBlue or green) ushr 8
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						if (z.toFloat() < depth!![offset]) {
							val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (colourIndex.toInt() != 0) {
								var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
								redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
								green = (srcColour and 0xFF00) * alpha and 0xFF0000
								@Pc(1872) val blended: Int = ((redBlue or green) ushr 8) + lerpColour
								var dstColour: Int = raster!![offset]
								var sum: Int = blended + dstColour
								var redBlueSum: Int = (blended and 0xFF00FF) + (dstColour and 0xFF00FF)
								@Pc(1898) val overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
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
						raster!![offset] = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
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
			var green: Int = (colour shr 8) and 0xFF
			val blue: Int = colour and 0xFF
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcColour: Int = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
						val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
						val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
						val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
						raster!![offset] = (scaledRed or scaledGreen or scaledBlue) ushr 8
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
						paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
						var srcColour: Int = if (paletteIndex.toInt() > 0) this.spritePalette!![paletteIndex.toInt()] else 0
						var sum: Int = srcColour + colour
						var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
						val carry: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![offset] = sum - carry or carry - (carry ushr 8)
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
			var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
			var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
			lerpColour = (redBlue or green) ushr 8
			val uStart: Int = u
			for (row in -height until 0) {
				val srcRowOffset: Int = (v shr 16) * this.imageWidth
				for (column in -width until 0) {
					if (z.toFloat() < depth!![offset]) {
						var srcColour: Int = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
						redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
						green = (srcColour and 0xFF00) * alpha and 0xFF0000
						raster!![offset] = ((redBlue or green) ushr 8) + lerpColour
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

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIIIII)V")
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
			@Pc(335) var local335: Int
			@Pc(343) var local343: Int
			@Pc(346) var local346: Int
			@Pc(361) var local361: Int
			@Pc(377) var local377: Int
			@Pc(385) var local385: Int
			@Pc(544) var lerpColour: Int
			@Pc(446) var paletteIndex: Byte
			@Pc(369) var local369: Int
			if (mode != 0) {
				@Pc(1196) var local1196: Byte
				@Pc(872) var local872: Int
				@Pc(880) var local880: Int
				@Pc(888) var local888: Int
				@Pc(900) var local900: Int
				if (mode == 1) {
					if (op == 1) {
						val uStart: Int = u
						for (row in -height until 0) {
							val srcRowOffset: Int = (v shr 16) * this.imageWidth
							for (column in -width until 0) {
								paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
								if (paletteIndex.toInt() == 0) {
									offset++
								} else {
									raster!![offset++] = this.spritePalette!![paletteIndex.toInt() and 0xFF]
								}
								u += uStep
							}
							v += vStep
							u = uStart
							offset += dstStep
						}
					} else {
						@Pc(735) var local735: Byte
						if (op == 0) {
							val uStart: Int = u
							if ((colour and 0xFFFFFF) == 16777215) {
								val alpha: Int = colour ushr 24
								val invAlpha: Int = 256 - alpha
								for (row in -height until 0) {
									val srcRowOffset: Int = (v shr 16) * this.imageWidth
									for (column in -width until 0) {
										val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
										if (colourIndex.toInt() == 0) {
											offset++
										} else {
											var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
											var dstColour: Int = raster!![offset]
											raster!![offset++] = ((srcColour and 0xFF00FF) * alpha + (dstColour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (dstColour and 0xFF00) * invAlpha and 0xFF0000) shr 8
										}
										u += uStep
									}
									v += vStep
									u = uStart
									offset += dstStep
								}
							} else {
								val red: Int = (colour shr 16) and 0xFF
								var green: Int = (colour shr 8) and 0xFF
								val blue: Int = colour and 0xFF
								val alpha: Int = colour ushr 24
								val invAlpha: Int = 256 - alpha
								for (row in -height until 0) {
									val srcRowOffset: Int = (v shr 16) * this.imageWidth
									for (column in -width until 0) {
										@Pc(852) val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
										if (colourIndex.toInt() == 0) {
											offset++
										} else {
											var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
											if (alpha == 255) {
												val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
												val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
												val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
												raster!![offset++] = (scaledRed or scaledGreen or scaledBlue) ushr 8
											} else {
												val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
												val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
												val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
												srcColour = (scaledRed or scaledGreen or scaledBlue) ushr 8
												var dstColour: Int = raster!![offset]
												raster!![offset++] = ((srcColour and 0xFF00FF) * alpha + (dstColour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (dstColour and 0xFF00) * invAlpha and 0xFF0000) shr 8
											}
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
									val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
									var srcColour: Int = if (colourIndex.toInt() > 0) this.spritePalette!![colourIndex.toInt()] else 0
									var sum: Int = srcColour + colour
									var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
									var pixel: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
									pixel = sum - pixel or pixel - (pixel ushr 8)
									if (srcColour == 0 && alpha != 255) {
										srcColour = pixel
										pixel = raster!![offset]
										pixel = ((srcColour and 0xFF00FF) * alpha + (pixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcColour and 0xFF00) * alpha + (pixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
									}
									raster!![offset++] = pixel
									u += uStep
								}
								v += vStep
								u = uStart
								offset += dstStep
							}
						} else if (op == 2) {
							val alpha: Int = colour ushr 24
							val invAlpha: Int = 256 - alpha
							var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
							var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
							lerpColour = (redBlue or green) ushr 8
							val uStart: Int = u
							for (row in -height until 0) {
								val srcRowOffset: Int = (v shr 16) * this.imageWidth
								for (column in -width until 0) {
									val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
									if (colourIndex.toInt() == 0) {
										offset++
									} else {
										var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
										redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
										green = (srcColour and 0xFF00) * alpha and 0xFF0000
										raster!![offset++] = ((redBlue or green) ushr 8) + lerpColour
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
					}
				} else if (mode != 2) {
					throw IllegalArgumentException()
				} else if (op == 1) {
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (paletteIndex.toInt() == 0) {
								offset++
							} else {
								var srcColour: Int = this.spritePalette!![paletteIndex.toInt() and 0xFF]
								var dstColour: Int = raster!![offset]
								var sum: Int = srcColour + dstColour
								var redBlueSum: Int = (srcColour and 0xFF00FF) + (dstColour and 0xFF00FF)
								dstColour = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset++] = sum - dstColour or dstColour - (dstColour ushr 8)
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
					var green: Int = (colour shr 8) and 0xFF
					val blue: Int = colour and 0xFF
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							@Pc(1406) val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (colourIndex.toInt() == 0) {
								offset++
							} else {
								var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
								val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
								val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
								val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
								srcColour = (scaledRed or scaledGreen or scaledBlue) ushr 8
								var dstColour: Int = raster!![offset]
								var sum: Int = srcColour + dstColour
								var redBlueSum: Int = (srcColour and 0xFF00FF) + (dstColour and 0xFF00FF)
								dstColour = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								raster!![offset++] = sum - dstColour or dstColour - (dstColour ushr 8)
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
							paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
							var srcColour: Int = if (paletteIndex.toInt() > 0) this.spritePalette!![paletteIndex.toInt()] else 0
							var sum: Int = srcColour + colour
							var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
							val carry: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							srcColour = sum - carry or carry - (carry ushr 8)
							@Pc(1583) val dst: Int = raster!![offset]
							sum = srcColour + dst
							redBlueSum = (srcColour and 0xFF00FF) + (dst and 0xFF00FF)
							@Pc(1605) val overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
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
					var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
					var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
					lerpColour = (redBlue or green) ushr 8
					val uStart: Int = u
					for (row in -height until 0) {
						val srcRowOffset: Int = (v shr 16) * this.imageWidth
						for (column in -width until 0) {
							val colourIndex: Byte = this.spritePixels!![(u shr 16) + srcRowOffset]
							if (colourIndex.toInt() == 0) {
								offset++
							} else {
								var srcColour: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
								redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
								green = (srcColour and 0xFF00) * alpha and 0xFF0000
								@Pc(1731) val blended: Int = ((redBlue or green) ushr 8) + lerpColour
								var dstColour: Int = raster!![offset]
								var sum: Int = blended + dstColour
								var redBlueSum: Int = (blended and 0xFF00FF) + (dstColour and 0xFF00FF)
								@Pc(1757) val overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
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
						raster!![offset++] = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 0) {
				val red: Int = (colour shr 16) and 0xFF
				var green: Int = (colour shr 8) and 0xFF
				val blue: Int = colour and 0xFF
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcColour: Int = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
						val scaledRed: Int = (srcColour and 0xFF0000) * red and 0xFF000000.toInt()
						val scaledGreen: Int = (srcColour and 0xFF00) * green and 0xFF0000
						val scaledBlue: Int = (srcColour and 0xFF) * blue and 0xFF00
						raster!![offset++] = (scaledRed or scaledGreen or scaledBlue) ushr 8
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
						paletteIndex = this.spritePixels!![(u shr 16) + srcRowOffset]
						var srcColour: Int = if (paletteIndex.toInt() > 0) this.spritePalette!![paletteIndex.toInt()] else 0
						var sum: Int = srcColour + colour
						var redBlueSum: Int = (srcColour and 0xFF00FF) + (colour and 0xFF00FF)
						val carry: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
						raster!![offset++] = sum - carry or carry - (carry ushr 8)
						u += uStep
					}
					v += vStep
					u = uStart
					offset += dstStep
				}
			} else if (op == 2) {
				val alpha: Int = colour ushr 24
				val invAlpha: Int = 256 - alpha
				var redBlue: Int = (colour and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var green: Int = (colour and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (redBlue or green) ushr 8
				val uStart: Int = u
				for (row in -height until 0) {
					val srcRowOffset: Int = (v shr 16) * this.imageWidth
					for (column in -width until 0) {
						var srcColour: Int = this.spritePalette!![this.spritePixels!![(u shr 16) + srcRowOffset].toInt() and 0xFF]
						redBlue = (srcColour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
						green = (srcColour and 0xFF00) * alpha and 0xFF0000
						raster!![offset++] = ((redBlue or green) ushr 8) + lerpColour
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

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "([I[III)V")
	override fun blitParallelogramMasked(@OriginalArg(0) lineOffsets: IntArray?, @OriginalArg(1) lineWidths: IntArray?, @OriginalArg(2) maskOffsetX: Int, @OriginalArg(3) maskOffsetY: Int) {
		@Pc(3) val raster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(186) var skip: Int
		@Pc(9) var row: Int
		@Pc(14) var maskIndex: Int
		@Pc(23) var dstIndex: Int
		@Pc(25) var u: Int
		@Pc(27) var v: Int
		@Pc(29) var column: Int
		@Pc(53) var maskStart: Int
		@Pc(58) var maskCount: Int
		@Pc(64) var maskSkip: Int
		@Pc(111) var index: Byte
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
								index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (index.toInt() == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
							@Pc(179) var vOverrun: Int
							vOverrun = v - (this.imageHeight shl 12)
							if (vOverrun >= 0) {
								skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
								column += skip
								v += JavaSpriteBlitState.dvdx * skip
								dstIndex += skip
							}
							@Pc(207) var vBound: Int
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
								index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (index.toInt() == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
							@Pc(379) var vEndBound: Int
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
								index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (index.toInt() == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
							@Pc(525) var uOverrun: Int
							uOverrun = u - (this.imageWidth shl 12)
							if (uOverrun >= 0) {
								skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
								column += skip
								u += JavaSpriteBlitState.dudx * skip
								dstIndex += skip
							}
							@Pc(553) var uBound: Int
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
								index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
								if (index.toInt() == 0) {
									dstIndex++
								} else {
									raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
						@Pc(690) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(724) var uBound: Int
						uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
						if (uBound > column) {
							column = uBound
						}
						@Pc(736) var vOverrun: Int
						vOverrun = v - (this.imageHeight shl 12)
						if (vOverrun >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(770) var vBound: Int
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
							index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (index.toInt() == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
						@Pc(913) var uOverrun: Int
						uOverrun = u - (this.imageWidth shl 12)
						if (uOverrun >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx
							column += skip
							u += JavaSpriteBlitState.dudx * skip
							v += JavaSpriteBlitState.dvdx * skip
							dstIndex += skip
						}
						@Pc(947) var uBound: Int
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
						@Pc(995) var vEndBound: Int
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
							index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (index.toInt() == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
						@Pc(1177) var uEndBound: Int
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
							index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
							if (index.toInt() == 0) {
								dstIndex++
							} else {
								raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
					@Pc(1354) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					@Pc(1366) var vOverrun: Int
					vOverrun = v - (this.imageHeight shl 12)
					if (vOverrun >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx
						column += skip
						u += JavaSpriteBlitState.dudx * skip
						v += JavaSpriteBlitState.dvdx * skip
						dstIndex += skip
					}
					@Pc(1400) var vBound: Int
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
						index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
						if (index.toInt() == 0) {
							dstIndex++
						} else {
							raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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
					@Pc(1579) var uEndBound: Int
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
					@Pc(1627) var vEndBound: Int
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
						index = this.spritePixels!![(v shr 12) * this.imageWidth + (u shr 12)]
						if (index.toInt() == 0) {
							dstIndex++
						} else {
							raster!![dstIndex++] = this.spritePalette!![index.toInt() and 0xFF]
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

	@OriginalMember(owner = "client!faa", name = "b", descriptor = "(II)V")
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
		@Pc(227) var index: Byte
		@Pc(255) var src: Int
		@Pc(264) var local264: Int
		@Pc(268) var local268: Int
		@Pc(272) var local272: Int
		@Pc(344) var local344: Int
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
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
								}
							} else if (op == 0) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redPart or greenPart or bluePart) ushr 8
									} else {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redPart or greenPart or bluePart) ushr 8
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								index = this.spritePixels!![texel]
								src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
								val tint: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tint
								val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow
									overflow = raster!![dst]
									overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = overflow
							} else if (op == 2) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
								}
							} else if (op == 0) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redPart or greenPart or bluePart) ushr 8
									} else {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redPart or greenPart or bluePart) ushr 8
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								index = this.spritePixels!![texel]
								src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
								val tint: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tint
								val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow
									overflow = raster!![dst]
									overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = overflow
							} else if (op == 2) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
						@Pc(1888) var vEndBound: Int
						vEndBound = (v + 1 - (this.imageHeight shl 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx
						if (vEndBound > column) {
							column = vEndBound
						}
						while (column < 0) {
							texel = (v shr 12) * this.imageWidth + (u shr 12)
							dst = dstIndex++
							if (op == 1) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
								}
							} else if (op == 0) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redPart or greenPart or bluePart) ushr 8
									} else {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redPart or greenPart or bluePart) ushr 8
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								index = this.spritePixels!![texel]
								src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
								val tint: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tint
								val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow
									overflow = raster!![dst]
									overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = overflow
							} else if (op == 2) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
								}
							} else if (op == 0) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
										val alpha: Int = JavaSpriteBlitState.colour ushr 24
										val invAlpha: Int = 256 - alpha
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
									} else if (JavaSpriteBlitState.alpha == 255) {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										raster!![dst] = (redPart or greenPart or bluePart) ushr 8
									} else {
										val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
										val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
										val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
										src = (redPart or greenPart or bluePart) ushr 8
										val background: Int = raster!![dst]
										raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
									}
								}
							} else if (op == 3) {
								index = this.spritePixels!![texel]
								src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
								val tint: Int = JavaSpriteBlitState.colour
								val sum: Int = src + tint
								val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
								var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
								overflow = sum - overflow or overflow - (overflow ushr 8)
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow
									overflow = raster!![dst]
									overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
								raster!![dst] = overflow
							} else if (op == 2) {
								index = this.spritePixels!![texel]
								if (index.toInt() != 0) {
									src = this.spritePalette!![index.toInt() and 0xFF]
									val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
									val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
									raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
							}
						} else if (op == 0) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redPart or greenPart or bluePart) ushr 8
								} else {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redPart or greenPart or bluePart) ushr 8
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							index = this.spritePixels!![texel]
							src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
							val tint: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tint
							val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							overflow = sum - overflow or overflow - (overflow ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow
								overflow = raster!![dst]
								overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = overflow
						} else if (op == 2) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
							}
						} else if (op == 0) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redPart or greenPart or bluePart) ushr 8
								} else {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redPart or greenPart or bluePart) ushr 8
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							index = this.spritePixels!![texel]
							src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
							val tint: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tint
							val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							overflow = sum - overflow or overflow - (overflow ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow
								overflow = raster!![dst]
								overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = overflow
						} else if (op == 2) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
					@Pc(5726) var uEndBound: Int
					uEndBound = (u + 1 - (this.imageWidth shl 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx
					if (uEndBound > column) {
						column = uEndBound
					}
					while (column < 0) {
						texel = (v shr 12) * this.imageWidth + (u shr 12)
						dst = dstIndex++
						if (op == 1) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
							}
						} else if (op == 0) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
									val alpha: Int = JavaSpriteBlitState.colour ushr 24
									val invAlpha: Int = 256 - alpha
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
								} else if (JavaSpriteBlitState.alpha == 255) {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									raster!![dst] = (redPart or greenPart or bluePart) ushr 8
								} else {
									val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
									val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
									val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
									src = (redPart or greenPart or bluePart) ushr 8
									val background: Int = raster!![dst]
									raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
								}
							}
						} else if (op == 3) {
							index = this.spritePixels!![texel]
							src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
							val tint: Int = JavaSpriteBlitState.colour
							val sum: Int = src + tint
							val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
							var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
							overflow = sum - overflow or overflow - (overflow ushr 8)
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow
								overflow = raster!![dst]
								overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
							}
							raster!![dst] = overflow
						} else if (op == 2) {
							index = this.spritePixels!![texel]
							if (index.toInt() != 0) {
								src = this.spritePalette!![index.toInt() and 0xFF]
								val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
								val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
								raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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
			this.blitParallelogramPart1(op, raster)
		} else {
			this.blitParallelogramPart2(op, raster)
		}
	}

	// Split out of blitParallelogram (the body of one of its branches, unchanged) so that neither method
	// exceeds HotSpot's 8000-byte limit for JIT compilation; the Kotlin version is larger than the Java one.
	@Suppress("NAME_SHADOWING", "UNUSED_VALUE", "UNUSED_VARIABLE")
	private fun blitParallelogramPart1(op: Int, raster: IntArray?) {
		var skip: Int
		var row: Int
		var dstIndex: Int
		var u: Int
		var v: Int
		var column: Int
		var texel: Int
		var dst: Int
		var index: Byte
		var src: Int
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
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
					}
				} else if (op == 0) {
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						src = this.spritePalette!![index.toInt() and 0xFF]
						if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
							val alpha: Int = JavaSpriteBlitState.colour ushr 24
							val invAlpha: Int = 256 - alpha
							val background: Int = raster!![dst]
							raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
						} else if (JavaSpriteBlitState.alpha == 255) {
							val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							raster!![dst] = (redPart or greenPart or bluePart) ushr 8
						} else {
							val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							src = (redPart or greenPart or bluePart) ushr 8
							val background: Int = raster!![dst]
							raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
						}
					}
				} else if (op == 3) {
					index = this.spritePixels!![texel]
					src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
					val tint: Int = JavaSpriteBlitState.colour
					val sum: Int = src + tint
					val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
					var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
					overflow = sum - overflow or overflow - (overflow ushr 8)
					if (src == 0 && JavaSpriteBlitState.alpha != 255) {
						src = overflow
						overflow = raster!![dst]
						overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
					}
					raster!![dst] = overflow
				} else if (op == 2) {
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						src = this.spritePalette!![index.toInt() and 0xFF]
						val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
						val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
						raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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

	// Split out of blitParallelogram (the body of one of its branches, unchanged) so that neither method
	// exceeds HotSpot's 8000-byte limit for JIT compilation; the Kotlin version is larger than the Java one.
	@Suppress("NAME_SHADOWING", "UNUSED_VALUE", "UNUSED_VARIABLE")
	private fun blitParallelogramPart2(op: Int, raster: IntArray?) {
		var skip: Int
		var row: Int
		var dstIndex: Int
		var u: Int
		var v: Int
		var column: Int
		var texel: Int
		var dst: Int
		var index: Byte
		var src: Int
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
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						raster!![dst] = this.spritePalette!![index.toInt() and 0xFF]
					}
				} else if (op == 0) {
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						src = this.spritePalette!![index.toInt() and 0xFF]
						if ((JavaSpriteBlitState.colour and 0xFFFFFF) == 16777215) {
							val alpha: Int = JavaSpriteBlitState.colour ushr 24
							val invAlpha: Int = 256 - alpha
							val background: Int = raster!![dst]
							raster!![dst] = ((src and 0xFF00FF) * alpha + (background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * alpha + (background and 0xFF00) * invAlpha and 0xFF0000) shr 8
						} else if (JavaSpriteBlitState.alpha == 255) {
							val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							raster!![dst] = (redPart or greenPart or bluePart) ushr 8
						} else {
							val redPart: Int = (src and 0xFF0000) * JavaSpriteBlitState.red and 0xFF000000.toInt()
							val greenPart: Int = (src and 0xFF00) * JavaSpriteBlitState.green and 0xFF0000
							val bluePart: Int = (src and 0xFF) * JavaSpriteBlitState.blue and 0xFF00
							src = (redPart or greenPart or bluePart) ushr 8
							val background: Int = raster!![dst]
							raster!![dst] = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (background and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (background and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
						}
					}
				} else if (op == 3) {
					index = this.spritePixels!![texel]
					src = if (index.toInt() > 0) this.spritePalette!![index.toInt()] else 0
					val tint: Int = JavaSpriteBlitState.colour
					val sum: Int = src + tint
					val redBlueSum: Int = (src and 0xFF00FF) + (tint and 0xFF00FF)
					var overflow: Int = (redBlueSum and 0x1000100) + (sum - redBlueSum and 0x10000)
					overflow = sum - overflow or overflow - (overflow ushr 8)
					if (src == 0 && JavaSpriteBlitState.alpha != 255) {
						src = overflow
						overflow = raster!![dst]
						overflow = ((src and 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow and 0xFF00FF) * JavaSpriteBlitState.invAlpha and 0xFF00FF00.toInt()) + ((src and 0xFF00) * JavaSpriteBlitState.alpha + (overflow and 0xFF00) * JavaSpriteBlitState.invAlpha and 0xFF0000) shr 8
					}
					raster!![dst] = overflow
				} else if (op == 2) {
					index = this.spritePixels!![texel]
					if (index.toInt() != 0) {
						src = this.spritePalette!![index.toInt() and 0xFF]
						val redBlue: Int = (src and 0xFF00FF) * JavaSpriteBlitState.alpha and 0xFF00FF00.toInt()
						val green: Int = (src and 0xFF00) * JavaSpriteBlitState.alpha and 0xFF0000
						raster!![dst++] = ((redBlue or green) ushr 8) + JavaSpriteBlitState.lerpColour
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

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IILclient!aa;II)V")
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
		@Pc(165) val lineOffsets: IntArray? = javaClippingMask!!.lineOffsets
		@Pc(169) val surfaceRaster: IntArray? = this.toolkit!!.surfaceRaster
		@Pc(171) var startY: Int = y
		if (maskY > y) {
			startY = maskY
			dstIndex += (maskY - y) * surfaceWidth
			srcIndex += (maskY - y) * this.imageWidth
		}
		@Pc(210) val endY: Int = if (maskY + lineWidths!!.size < y + imageHeight) maskY + lineWidths!!.size else y + imageHeight
		for (@Pc(212) i in startY until endY) {
			@Pc(221) val spanStart: Int = lineWidths!![i - maskY] + maskX
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
				@Pc(311) val paletteIndex: Byte = this.spritePixels!![srcIndex++]
				if (paletteIndex.toInt() == 0) {
					dstIndex++
				} else {
					surfaceRaster!![dstIndex++] = this.spritePalette!![paletteIndex.toInt() and 0xFF]
				}
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIII)V")
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
		@Pc(284) var local284: Int
		@Pc(287) var local287: Int
		@Pc(291) var local291: Int
		@Pc(303) var local303: Int
		@Pc(319) var local319: Int
		@Pc(327) var local327: Int
		@Pc(458) var lerpColour: Int
		@Pc(311) var local311: Int
		if (mode != 0) {
			@Pc(1054) var local1054: Byte
			@Pc(766) var local766: Int
			@Pc(774) var local774: Int
			@Pc(782) var local782: Int
			@Pc(794) var local794: Int
			@Pc(543) var paletteIndex: Byte
			if (mode == 1) {
				if (op == 1) {
					for (row in -height until 0) {
						for (column in -width until 0) {
							paletteIndex = this.spritePixels!![srcIndex++]
							if (paletteIndex.toInt() == 0) {
								dstIndex++
							} else {
								var srcPixel: Int = this.spritePalette!![paletteIndex.toInt() and 0xFF] or 0xFF000000.toInt()
								var dstPixel: Int = raster!![dstIndex]
								raster!![dstIndex++] = (((srcPixel and 0xFF00FF) * 255 + (dstPixel and 0xFF00FF) * 0 shr 8) and 0xFFFF00FF.toInt()) + (((srcPixel and 0xFF00FF00.toInt()) ushr 8) * 255 + ((dstPixel and 0xFF00FF00.toInt()) ushr 8) * 0 and 0xFF00FF00.toInt())
							}
						}
						dstIndex += dstStep
						srcIndex += srcStep
					}
				} else {
					@Pc(645) var local645: Byte
					if (op == 0) {
						if ((color and 0xFFFFFF) == 16777215) {
							val alpha: Int = color ushr 24
							val invAlpha: Int = 256 - alpha
							for (row in -height until 0) {
								for (column in -width until 0) {
									val paletteSlot: Byte = this.spritePixels!![srcIndex++]
									if (paletteSlot.toInt() == 0) {
										dstIndex++
									} else {
										var srcPixel: Int = this.spritePalette!![paletteSlot.toInt() and 0xFF]
										var dstPixel: Int = raster!![dstIndex]
										raster!![dstIndex++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
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
									@Pc(746) val colourIndex: Byte = this.spritePixels!![srcIndex++]
									if (colourIndex.toInt() == 0) {
										dstIndex++
									} else {
										var srcPixel: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
										if (alpha == 255) {
											val redTerm: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
											var greenTerm: Int = (srcPixel and 0xFF00) * green and 0xFF0000
											val blueTerm: Int = (srcPixel and 0xFF) * blue and 0xFF00
											raster!![dstIndex++] = (redTerm or greenTerm or blueTerm) ushr 8
										} else {
											val redTerm: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
											var greenTerm: Int = (srcPixel and 0xFF00) * green and 0xFF0000
											val blueTerm: Int = (srcPixel and 0xFF) * blue and 0xFF00
											srcPixel = (redTerm or greenTerm or blueTerm) ushr 8
											var dstPixel: Int = raster!![dstIndex]
											raster!![dstIndex++] = ((srcPixel and 0xFF00FF) * alpha + (dstPixel and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (dstPixel and 0xFF00) * invAlpha and 0xFF0000) shr 8
										}
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
								val paletteSlot: Byte = this.spritePixels!![srcIndex++]
								var srcPixel: Int = if (paletteSlot.toInt() > 0) this.spritePalette!![paletteSlot.toInt()] else 0
								var sum: Int = srcPixel + color
								var lowSum: Int = (srcPixel and 0xFF00FF) + (color and 0xFF00FF)
								var blended: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
								blended = sum - blended or blended - (blended ushr 8)
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = blended
									blended = raster!![dstIndex]
									blended = ((srcPixel and 0xFF00FF) * alpha + (blended and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((srcPixel and 0xFF00) * alpha + (blended and 0xFF00) * invAlpha and 0xFF0000) shr 8
								}
								raster!![dstIndex++] = blended
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					} else if (op == 2) {
						val alpha: Int = color ushr 24
						val invAlpha: Int = 256 - alpha
						var redBlueTerm: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
						var greenTerm: Int = (color and 0xFF00) * invAlpha and 0xFF0000
						lerpColour = (redBlueTerm or greenTerm) ushr 8
						for (row in -height until 0) {
							for (column in -width until 0) {
								val paletteSlot: Byte = this.spritePixels!![srcIndex++]
								if (paletteSlot.toInt() == 0) {
									dstIndex++
								} else {
									var srcPixel: Int = this.spritePalette!![paletteSlot.toInt() and 0xFF]
									redBlueTerm = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
									greenTerm = (srcPixel and 0xFF00) * alpha and 0xFF0000
									raster!![dstIndex++] = ((redBlueTerm or greenTerm) ushr 8) + lerpColour
								}
							}
							dstIndex += dstStep
							srcIndex += srcStep
						}
					} else {
						throw IllegalArgumentException()
					}
				}
			} else if (mode != 2) {
				throw IllegalArgumentException()
			} else if (op == 1) {
				for (row in -height until 0) {
					for (column in -width until 0) {
						paletteIndex = this.spritePixels!![srcIndex++]
						if (paletteIndex.toInt() == 0) {
							dstIndex++
						} else {
							var srcPixel: Int = this.spritePalette!![paletteIndex.toInt() and 0xFF]
							var dstPixel: Int = raster!![dstIndex]
							var sum: Int = srcPixel + dstPixel
							var lowSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
							dstPixel = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
							raster!![dstIndex++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
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
						@Pc(1228) val colourIndex: Byte = this.spritePixels!![srcIndex++]
						if (colourIndex.toInt() == 0) {
							dstIndex++
						} else {
							var srcPixel: Int = this.spritePalette!![colourIndex.toInt() and 0xFF]
							val redTerm: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
							var greenTerm: Int = (srcPixel and 0xFF00) * green and 0xFF0000
							val blueTerm: Int = (srcPixel and 0xFF) * blue and 0xFF00
							srcPixel = (redTerm or greenTerm or blueTerm) ushr 8
							var dstPixel: Int = raster!![dstIndex]
							var sum: Int = srcPixel + dstPixel
							var lowSum: Int = (srcPixel and 0xFF00FF) + (dstPixel and 0xFF00FF)
							dstPixel = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
							raster!![dstIndex++] = sum - dstPixel or dstPixel - (dstPixel ushr 8)
						}
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 3) {
				for (row in -height until 0) {
					for (column in -width until 0) {
						paletteIndex = this.spritePixels!![srcIndex++]
						var srcPixel: Int = if (paletteIndex.toInt() > 0) this.spritePalette!![paletteIndex.toInt()] else 0
						var sum: Int = srcPixel + color
						var lowSum: Int = (srcPixel and 0xFF00FF) + (color and 0xFF00FF)
						val carry: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
						srcPixel = sum - carry or carry - (carry ushr 8)
						@Pc(1387) val dst: Int = raster!![dstIndex]
						sum = srcPixel + dst
						lowSum = (srcPixel and 0xFF00FF) + (dst and 0xFF00FF)
						@Pc(1409) val overflow: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
						raster!![dstIndex++] = sum - overflow or overflow - (overflow ushr 8)
					}
					dstIndex += dstStep
					srcIndex += srcStep
				}
			} else if (op == 2) {
				val alpha: Int = color ushr 24
				val invAlpha: Int = 256 - alpha
				var redBlueTerm: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
				var greenTerm: Int = (color and 0xFF00) * invAlpha and 0xFF0000
				lerpColour = (redBlueTerm or greenTerm) ushr 8
				for (row in -height until 0) {
					for (column in -width until 0) {
						val paletteSlot: Byte = this.spritePixels!![srcIndex++]
						if (paletteSlot.toInt() == 0) {
							dstIndex++
						} else {
							var srcPixel: Int = this.spritePalette!![paletteSlot.toInt() and 0xFF]
							redBlueTerm = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
							greenTerm = (srcPixel and 0xFF00) * alpha and 0xFF0000
							@Pc(1517) var blended: Int = ((redBlueTerm or greenTerm) ushr 8) + lerpColour
							var dstPixel: Int = raster!![dstIndex]
							var sum: Int = blended + dstPixel
							var lowSum: Int = (blended and 0xFF00FF) + (dstPixel and 0xFF00FF)
							@Pc(1543) val overflow: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
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
					raster!![dstIndex++] = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					raster!![dstIndex++] = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					raster!![dstIndex++] = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					raster!![dstIndex++] = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
				}
				rowEnd += 3
				while (dstIndex < rowEnd) {
					raster!![dstIndex++] = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
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
					var srcPixel: Int = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					val redTerm: Int = (srcPixel and 0xFF0000) * red and 0xFF000000.toInt()
					var greenTerm: Int = (srcPixel and 0xFF00) * green and 0xFF0000
					val blueTerm: Int = (srcPixel and 0xFF) * blue and 0xFF00
					raster!![dstIndex++] = (redTerm or greenTerm or blueTerm) ushr 8
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else if (op == 3) {
			for (row in -height until 0) {
				for (column in -width until 0) {
					var srcPixel: Int = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					var sum: Int = srcPixel + color
					var lowSum: Int = (srcPixel and 0xFF00FF) + (color and 0xFF00FF)
					val carry: Int = (lowSum and 0x1000100) + (sum - lowSum and 0x10000)
					raster!![dstIndex++] = sum - carry or carry - (carry ushr 8)
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else if (op == 2) {
			val alpha: Int = color ushr 24
			val invAlpha: Int = 256 - alpha
			var redBlueTerm: Int = (color and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()
			var greenTerm: Int = (color and 0xFF00) * invAlpha and 0xFF0000
			lerpColour = (redBlueTerm or greenTerm) ushr 8
			for (row in -height until 0) {
				for (column in -width until 0) {
					var srcPixel: Int = this.spritePalette!![this.spritePixels!![srcIndex++].toInt() and 0xFF]
					redBlueTerm = (srcPixel and 0xFF00FF) * alpha and 0xFF00FF00.toInt()
					greenTerm = (srcPixel and 0xFF00) * alpha and 0xFF0000
					raster!![dstIndex++] = ((redBlueTerm or greenTerm) ushr 8) + lerpColour
				}
				dstIndex += dstStep
				srcIndex += srcStep
			}
		} else {
			throw IllegalArgumentException()
		}
	}
}
