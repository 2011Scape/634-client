package com.jagex.game.runetek5.graphics.font

import com.jagex.game.runetek5.GlxShaderCompileStatus
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!da")
abstract class AbstractFontRenderer {

	@OriginalMember(owner = "client!da", name = "s", descriptor = "Lclient!oea;")
	private val fontMetrics: FontMetrics?

	/** Not part of the original client: the width of [string] in this font, for the plugin API (ToolkitGraphics). */
	/** Not part of the original client: the font's line height, baseline to baseline, for the plugin API. */
	fun lineHeight(): Int {
		return this.fontMetrics!!.verticalSpacing
	}

	fun textWidth(string: String?): Int {
		return this.fontMetrics!!.getWidthNoSprites(string)
	}

	@OriginalMember(owner = "client!da", name = "d", descriptor = "Lclient!ha;")
	private val toolkit: Toolkit?

	@OriginalMember(owner = "client!da", name = "<init>", descriptor = "(Lclient!ha;Lclient!oea;)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) fontMetrics: FontMetrics?) {
		this.fontMetrics = fontMetrics
		this.toolkit = toolkit
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([Lclient!mi;[II[I[IILjava/lang/String;I)V")
	private fun renderLineWithOffsets(@OriginalArg(0) sprites: Array<NativeSprite?>?, @OriginalArg(1) offsetsY: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) spriteHeights: IntArray?, @OriginalArg(4) offsetsX: IntArray?, @OriginalArg(5) y: Int, @OriginalArg(6) string: String?) {
		var x: Int = x
		@Pc(7) val drawY: Int = y - this.fontMetrics!!.verticalSpacing
		@Pc(13) var tagStart: Int = -1
		@Pc(15) var previousChar: Int = -1
		@Pc(17) var offsetIndex: Int = 0
		@Pc(24) val length: Int = string!!.length
		for (@Pc(26) i in 0 until length) {
			@Pc(37) var ch: Char = (Utils.charToCp1252(string[i]).toInt() and 0xFF).toChar()
			if (ch == '<') {
				tagStart = i
			} else {
				@Pc(123) val local123: Int
				@Pc(133) val local133: Int
				if (ch == '>' && tagStart != -1) {
					@Pc(59) val string2: String = string.substring(tagStart + 1, i)
					tagStart = -1
					if (string2 == "lt") {
						ch = '<'
					} else if (string2 == "gt") {
						ch = '>'
					} else if (string2 == "nbsp") {
						ch = ' '
					} else if (string2 == "shy") {
						ch = '­'
					} else if (string2 == "times") {
						ch = '×'
					} else if (string2 == "euro") {
						ch = '€'
					} else if (string2 == "copy") {
						ch = '©'
					} else {
						if (string2 != "reg") {
							if (string2.startsWith("img=")) {
								try {
									val imageOffsetX: Int
									if (offsetsX == null) {
										imageOffsetX = 0
									} else {
										imageOffsetX = offsetsX[offsetIndex]
									}
									val imageOffsetY: Int
									if (offsetsY == null) {
										imageOffsetY = 0
									} else {
										imageOffsetY = offsetsY[offsetIndex]
									}
									offsetIndex++
									@Pc(144) val spriteIndex: Int = Utils.parseInt(string2.substring(4))
									@Pc(148) val sprite: NativeSprite? = sprites!![spriteIndex]
									@Pc(157) val spriteHeight: Int = if (spriteHeights == null) sprite!!.scaleHeight() else spriteHeights[spriteIndex]
									sprite!!.render(x + imageOffsetX, -spriteHeight + this.fontMetrics.verticalSpacing + (drawY - -imageOffsetY), 1, 0, 1)
									x += sprites[spriteIndex]!!.scaleWidth()
									previousChar = -1
								} catch (@Pc(187) ex: Exception) {
								}
							} else {
								this.updateTextEffects(string2)
							}
							continue
						}
						ch = '®'
					}
				}
				if (tagStart == -1) {
					if (previousChar != -1) {
						x += this.fontMetrics.getKerning(previousChar, ch)
					}
					@Pc(225) val offsetX: Int
					if (offsetsX == null) {
						offsetX = 0
					} else {
						offsetX = offsetsX[offsetIndex]
					}
					val offsetY: Int
					if (offsetsY == null) {
						offsetY = 0
					} else {
						offsetY = offsetsY[offsetIndex]
					}
					if (ch != ' ') {
						if ((AbstractFontRenderer.shadingEffect and 0xFF000000.toInt()) != 0) {
							this.fa(ch, x + offsetX + 1, drawY - -1 + offsetY, AbstractFontRenderer.shadingEffect, true)
						}
						this.fa(ch, x + offsetX, drawY + offsetY, AbstractFontRenderer.argbColorEffect, false)
					} else if (AbstractFontRenderer.justifiedSpaceWidth > 0) {
						AbstractFontRenderer.justifiedSpaceAccumulator += AbstractFontRenderer.justifiedSpaceWidth
						x += AbstractFontRenderer.justifiedSpaceAccumulator shr 8
						AbstractFontRenderer.justifiedSpaceAccumulator = AbstractFontRenderer.justifiedSpaceAccumulator and 0xFF
					}
					offsetIndex++
					val glyphWidth: Int = this.fontMetrics.glyphWidth(ch.code)
					if (AbstractFontRenderer.strikeThroughEffect != -1) {
						this.toolkit!!.horizontalLine(glyphWidth, drawY + (this.fontMetrics.verticalSpacing.toDouble() * 0.7).toInt(), AbstractFontRenderer.strikeThroughEffect, x)
					}
					if (AbstractFontRenderer.underlineEffect != -1) {
						this.toolkit!!.horizontalLine(glyphWidth, this.fontMetrics.verticalSpacing + drawY, AbstractFontRenderer.underlineEffect, x)
					}
					previousChar = ch.code
					x += glyphWidth
				}
			}
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(ILjava/lang/String;BIII[Lclient!mi;[I)V")
	fun drawStringWithSprites(@OriginalArg(0) y: Int, @OriginalArg(1) string: String?, @OriginalArg(3) color: Int, @OriginalArg(5) x: Int, @OriginalArg(6) sprites: Array<NativeSprite?>?, @OriginalArg(7) spriteHeights: IntArray?) {
		if (string != null) {
			this.calibrate(0, color)
			this.renderLine(0, y, 0, sprites, null, string, spriteHeights, x)
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([IIBLclient!aa;I[Lclient!mi;IIIILjava/lang/String;IIIIII)I")
	fun renderText(@OriginalArg(0) spriteHeights: IntArray?, @OriginalArg(1) height: Int, @OriginalArg(3) clippingMask: ComponentSurface?, @OriginalArg(4) surfaceOffsetX: Int, @OriginalArg(5) sprites: Array<NativeSprite?>?, @OriginalArg(6) textColor: Int, @OriginalArg(7) y: Int, @OriginalArg(8) width: Int, @OriginalArg(9) verticalAlign: Int, @OriginalArg(10) string: String?, @OriginalArg(11) surfaceOffsetY: Int, @OriginalArg(12) x: Int, @OriginalArg(13) horizontalAlign: Int, @OriginalArg(14) shadowColor: Int, @OriginalArg(15) maxLines: Int, @OriginalArg(16) lineSpacing: Int): Int {
		var verticalAlign: Int = verticalAlign
		var maxLines: Int = maxLines
		var lineSpacing: Int = lineSpacing
		if (string == null) {
			return 0
		}
		this.calibrate(shadowColor, textColor)
		if (lineSpacing == 0) {
			lineSpacing = this.fontMetrics!!.verticalSpacing
		}
		@Pc(48) val lineWidths: IntArray?
		if (this.fontMetrics!!.paddingBottom + this.fontMetrics.paddingTop + lineSpacing > height && height < lineSpacing + lineSpacing) {
			lineWidths = null
		} else {
			lineWidths = intArrayOf(width)
		}
		@Pc(61) var lineCount: Int = this.fontMetrics.splitLinesToBuffer(string, lineWidths, AbstractFontRenderer.LINE_BUFFER, sprites)
		if (maxLines == -1) {
			maxLines = height / lineSpacing
			if (maxLines <= 0) {
				maxLines = 1
			}
		}
		if (maxLines > 0 && lineCount >= maxLines) {
			AbstractFontRenderer.LINE_BUFFER[maxLines - 1] = this.fontMetrics.fitText(sprites, width, AbstractFontRenderer.LINE_BUFFER[maxLines - 1])
			lineCount = maxLines
		}
		if (verticalAlign == 3 && lineCount == 1) {
			verticalAlign = 1
		}
		@Pc(128) var lineY: Int
		@Pc(163) val local163: Int
		if (verticalAlign == 0) {
			lineY = this.fontMetrics.paddingTop + y
		} else if (verticalAlign == 1) {
			lineY = (height - this.fontMetrics.paddingBottom - this.fontMetrics.paddingTop - lineSpacing * (lineCount + -1)) / 2 + this.fontMetrics.paddingTop + y
		} else if (verticalAlign == 2) {
			lineY = height + y - this.fontMetrics.paddingBottom - (lineCount + -1) * lineSpacing
		} else {
			var lineGap: Int = (height - lineSpacing * (lineCount - 1) - this.fontMetrics.paddingTop - this.fontMetrics.paddingBottom) / (lineCount + 1)
			if (lineGap < 0) {
				lineGap = 0
			}
			lineSpacing += lineGap
			lineY = this.fontMetrics.paddingTop + y + lineGap
		}
		for (line in 0 until lineCount) {
			if (horizontalAlign == 0) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x)
			} else if (horizontalAlign == 1) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, (width - this.fontMetrics.getWidthNoSprites(AbstractFontRenderer.LINE_BUFFER[line])) / 2 + x)
			} else if (horizontalAlign == 2) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, width + x - this.fontMetrics.getWidthNoSprites(AbstractFontRenderer.LINE_BUFFER[line]))
			} else if (line == lineCount - 1) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x)
			} else {
				this.calculateJustifiedSpacing(AbstractFontRenderer.LINE_BUFFER[line], width)
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x)
				AbstractFontRenderer.justifiedSpaceWidth = 0
			}
			lineY += lineSpacing
		}
		return lineCount
	}

	@OriginalMember(owner = "client!da", name = "fa", descriptor = "(CIIIZ)V")
	abstract fun fa(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean)

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;IIIII)V")
	fun renderRightAligned(@OriginalArg(0) string: String?, @OriginalArg(1) y: Int, @OriginalArg(2) color: Int, @OriginalArg(3) x: Int, @OriginalArg(5) shadow: Int) {
		if (string != null) {
			this.calibrate(shadow, color)
			this.renderLine(0, y, 0, null, null, string, null, x - this.fontMetrics!!.getWidthNoSprites(string))
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(II[ILjava/lang/String;II[Lclient!mi;IILjava/util/Random;)I")
	fun renderAntiMacroText(@OriginalArg(1) seed: Int, @OriginalArg(2) spriteHeights: IntArray?, @OriginalArg(3) string: String?, @OriginalArg(6) sprites: Array<NativeSprite?>?, @OriginalArg(7) y: Int, @OriginalArg(8) x: Int, @OriginalArg(9) random: Random?): Int {
		if (string == null) {
			return 0
		}
		random!!.setSeed(seed.toLong())
		@Pc(20) val alpha: Int = (random.nextInt() and 0x1F) + 192
		this.calibrate((alpha or 0x0) shl 24, (alpha shl 24) or 0xFFFFFF)
		@Pc(42) val length: Int = string.length
		@Pc(50) val offsetsX: IntArray = IntArray(length)
		@Pc(52) var extraWidth: Int = 0
		for (@Pc(54) i in 0 until length) {
			offsetsX[i] = extraWidth
			if ((random.nextInt() and 0x3) == 0) {
				extraWidth++
			}
		}
		this.renderLineWithOffsets(sprites, null, x, spriteHeights, offsetsX, y, string)
		return extraWidth
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(BLjava/lang/String;)V")
	private fun updateTextEffects(@OriginalArg(1) string: String?) {
		try {
			if (string!!.startsWith("col=")) {
				AbstractFontRenderer.argbColorEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or (Utils.parseHex(string.substring(4)) and 0xFFFFFF)
			} else if (string == "/col") {
				AbstractFontRenderer.argbColorEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or (AbstractFontRenderer.baseTextColor and 0xFFFFFF)
			}
			if (string.startsWith("argb=")) {
				AbstractFontRenderer.argbColorEffect = Utils.parseHex(string.substring(5))
			} else if (string == "/argb") {
				AbstractFontRenderer.argbColorEffect = AbstractFontRenderer.baseTextColor
			} else if (string.startsWith("str=")) {
				AbstractFontRenderer.strikeThroughEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or Utils.parseHex(string.substring(4))
			} else if (string == "str") {
				AbstractFontRenderer.strikeThroughEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or 0x800000
			} else if (string == "/str") {
				AbstractFontRenderer.strikeThroughEffect = -1
			} else if (string.startsWith("u=")) {
				AbstractFontRenderer.underlineEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or Utils.parseHex(string.substring(2))
			} else if (string == "u") {
				AbstractFontRenderer.underlineEffect = AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()
			} else if (string == "/u") {
				AbstractFontRenderer.underlineEffect = -1
			} else if (string.equals("shad=-1", ignoreCase = true)) {
				AbstractFontRenderer.shadingEffect = 0
			} else if (string.startsWith("shad=")) {
				AbstractFontRenderer.shadingEffect = (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or Utils.parseHex(string.substring(5))
			} else if (string == "shad") {
				AbstractFontRenderer.shadingEffect = AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()
			} else if (string == "/shad") {
				AbstractFontRenderer.shadingEffect = AbstractFontRenderer.baseShadowColor
			} else if (string == "br") {
				this.calibrate(AbstractFontRenderer.baseShadowColor, AbstractFontRenderer.baseTextColor)
				return
			}
		} catch (@Pc(173) ex: Exception) {
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(IILjava/lang/String;IIIZ)V")
	fun renderCenteredWave(@OriginalArg(0) color: Int, @OriginalArg(1) y: Int, @OriginalArg(2) string: String?, @OriginalArg(3) x: Int, @OriginalArg(5) waveSeed: Int) {
		if (string == null) {
			return
		}
		this.calibrate(-16777216, color)
		@Pc(16) val length: Int = string.length
		@Pc(19) val offsetsY: IntArray = IntArray(length)
		for (@Pc(27) i in 0 until length) {
			offsetsY[i] = (kotlin.math.sin(i.toDouble() / 2.0 + waveSeed.toDouble() / 5.0) * 5.0).toInt()
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics!!.getWidthNoSprites(string) / 2, null, null, y, string)
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(BIILjava/lang/String;II)V")
	fun renderCentered(@OriginalArg(1) x: Int, @OriginalArg(2) color: Int, @OriginalArg(3) string: String?, @OriginalArg(4) shadow: Int, @OriginalArg(5) y: Int) {
		if (string != null) {
			this.calibrate(shadow, color)
			this.renderLine(0, y, 0, null, null, string, null, x - this.fontMetrics!!.getWidthNoSprites(string) / 2)
		}
	}

	/** Not part of the original client: [renderCentered] with `<img=N>` icons drawn, for a plugin's overhead text (Emojis). */
	fun renderCenteredWithSprites(x: Int, color: Int, string: String?, shadow: Int, y: Int, sprites: Array<NativeSprite?>?, spriteHeights: IntArray?) {
		if (string != null) {
			this.calibrate(shadow, color)
			this.renderLine(0, y, 0, sprites, null, string, spriteHeights, x - this.fontMetrics!!.getTextWidth(string, sprites) / 2)
		}
	}

	@OriginalMember(owner = "client!da", name = "b", descriptor = "(Ljava/lang/String;IIIII)V")
	fun drawString(@OriginalArg(0) string: String?, @OriginalArg(1) color: Int, @OriginalArg(2) y: Int, @OriginalArg(3) x: Int, @OriginalArg(4) shadow: Int) {
		if (string != null) {
			this.calibrate(shadow, color)
			this.renderLine(0, y, 0, null, null, string, null, x)
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(IILjava/lang/String;IIIII)V")
	fun renderCenteredShake(@OriginalArg(0) shakeSeed: Int, @OriginalArg(1) y: Int, @OriginalArg(2) string: String?, @OriginalArg(3) color: Int, @OriginalArg(5) effectProgress: Int, @OriginalArg(6) x: Int) {
		if (string == null) {
			return
		}
		this.calibrate(-16777216, color)
		@Pc(20) var amplitude: Double = 7.0 - effectProgress.toDouble() / 8.0
		if (amplitude < 0.0) {
			amplitude = 0.0
		}
		@Pc(29) val length: Int = string.length
		@Pc(42) val offsetsY: IntArray = IntArray(length)
		for (@Pc(44) i in 0 until length) {
			offsetsY[i] = (kotlin.math.sin(shakeSeed.toDouble() + i.toDouble() / 1.5) * amplitude).toInt()
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics!!.getWidthNoSprites(string) / 2, null, null, y, string)
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	abstract fun renderSymbol(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean, @OriginalArg(5) mask: ComponentSurface?, @OriginalArg(6) offsetX: Int, @OriginalArg(7) offsetY: Int)

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(III)V")
	private fun calibrate(@OriginalArg(0) shadowColor: Int, @OriginalArg(2) textColor: Int) {
		var shadowColor: Int = shadowColor
		AbstractFontRenderer.underlineEffect = -1
		if (shadowColor == -1) {
			shadowColor = 0
		}
		AbstractFontRenderer.baseTextColor = textColor
		AbstractFontRenderer.argbColorEffect = textColor
		AbstractFontRenderer.justifiedSpaceAccumulator = 0
		AbstractFontRenderer.justifiedSpaceWidth = 0
		AbstractFontRenderer.strikeThroughEffect = -1
		AbstractFontRenderer.baseShadowColor = shadowColor
		AbstractFontRenderer.shadingEffect = shadowColor
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;II)V")
	private fun calculateJustifiedSpacing(@OriginalArg(0) string: String?, @OriginalArg(2) width: Int) {
		@Pc(7) var spaceCount: Int = 0
		@Pc(9) var inTag: Boolean = false
		@Pc(11) var i: Int = 0
		while (i < string!!.length) {
			@Pc(17) val ch: Char = string[i]
			if (ch == '<') {
				inTag = true
			} else if (ch == '>') {
				inTag = false
			} else if (!inTag && ch == ' ') {
				spaceCount++
			}
			i++
		}
		if (spaceCount > 0) {
			AbstractFontRenderer.justifiedSpaceWidth = ((width - this.fontMetrics!!.getWidthNoSprites(string)) shl 8) / spaceCount
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;IIIBII)V")
	fun renderCenteredWave2(@OriginalArg(0) string: String?, @OriginalArg(1) color: Int, @OriginalArg(2) waveSeed: Int, @OriginalArg(3) y: Int, @OriginalArg(6) x: Int) {
		if (string == null) {
			return
		}
		this.calibrate(-16777216, color)
		@Pc(22) val length: Int = string.length
		@Pc(25) val offsetsX: IntArray = IntArray(length)
		@Pc(28) val offsetsY: IntArray = IntArray(length)
		for (@Pc(30) i in 0 until length) {
			offsetsX[i] = (kotlin.math.sin(waveSeed.toDouble() / 5.0 + i.toDouble() / 5.0) * 5.0).toInt()
			offsetsY[i] = (kotlin.math.sin(i.toDouble() / 3.0 + waveSeed.toDouble() / 5.0) * 5.0).toInt()
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics!!.getWidthNoSprites(string) / 2, null, offsetsX, y, string)
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(III[Lclient!mi;Lclient!aa;Ljava/lang/String;[III)V")
	private fun renderLine(@OriginalArg(0) surfaceOffsetY: Int, @OriginalArg(1) y: Int, @OriginalArg(2) surfaceOffsetX: Int, @OriginalArg(3) sprites: Array<NativeSprite?>?, @OriginalArg(4) clippingMask: ComponentSurface?, @OriginalArg(5) string: String?, @OriginalArg(6) spriteHeights: IntArray?, @OriginalArg(8) x: Int) {
		var x: Int = x
		@Pc(7) val drawY: Int = y - this.fontMetrics!!.verticalSpacing
		@Pc(13) var tagStart: Int = -1
		@Pc(15) var previousChar: Int = -1
		@Pc(18) val length: Int = string!!.length
		for (@Pc(24) i in 0 until length) {
			@Pc(35) var ch: Char = (Utils.charToCp1252(string[i]).toInt() and 0xFF).toChar()
			if (ch == '<') {
				tagStart = i
			} else {
				if (ch == '>' && tagStart != -1) {
					@Pc(57) val string2: String = string.substring(tagStart + 1, i)
					tagStart = -1
					if (string2 == "lt") {
						ch = '<'
					} else if (string2 == "gt") {
						ch = '>'
					} else if (string2 == "nbsp") {
						ch = ' '
					} else if (string2 == "shy") {
						ch = '­'
					} else if (string2 == "times") {
						ch = '×'
					} else if (string2 == "euro") {
						ch = '€'
					} else if (string2 == "copy") {
						ch = '©'
					} else {
						if (string2 != "reg") {
							if (string2.startsWith("img=")) {
								try {
									@Pc(121) val spriteIndex: Int = Utils.parseInt(string2.substring(4))
									@Pc(125) val sprite: NativeSprite? = sprites!![spriteIndex]
									@Pc(134) val spriteHeight: Int = if (spriteHeights == null) sprite!!.scaleHeight() else spriteHeights[spriteIndex]
									if ((AbstractFontRenderer.argbColorEffect and -16777216) == -16777216) {
										sprite!!.render(x, this.fontMetrics.verticalSpacing + drawY - spriteHeight, 1, 0, 1)
									} else {
										sprite!!.render(x, this.fontMetrics.verticalSpacing + drawY - spriteHeight, 0, (AbstractFontRenderer.argbColorEffect and 0xFF000000.toInt()) or 0xFFFFFF, 1)
									}
									previousChar = -1
									x += sprites[spriteIndex]!!.scaleWidth()
								} catch (@Pc(183) ex: Exception) {
								}
							} else {
								this.updateTextEffects(string2)
							}
							continue
						}
						ch = '®'
					}
				}
				if (tagStart == -1) {
					if (previousChar != -1) {
						x += this.fontMetrics.getKerning(previousChar, ch)
					}
					if (ch == ' ') {
						if (AbstractFontRenderer.justifiedSpaceWidth > 0) {
							AbstractFontRenderer.justifiedSpaceAccumulator += AbstractFontRenderer.justifiedSpaceWidth
							x += AbstractFontRenderer.justifiedSpaceAccumulator shr 8
							AbstractFontRenderer.justifiedSpaceAccumulator = AbstractFontRenderer.justifiedSpaceAccumulator and 0xFF
						}
					} else if (clippingMask == null) {
						if ((AbstractFontRenderer.shadingEffect and 0xFF000000.toInt()) != 0) {
							this.fa(ch, x + 1, drawY + 1, AbstractFontRenderer.shadingEffect, true)
						}
						this.fa(ch, x, drawY, AbstractFontRenderer.argbColorEffect, false)
					} else {
						if ((AbstractFontRenderer.shadingEffect and 0xFF000000.toInt()) != 0) {
							this.renderSymbol(ch, x + 1, drawY + 1, AbstractFontRenderer.shadingEffect, true, clippingMask, surfaceOffsetX, surfaceOffsetY)
						}
						this.renderSymbol(ch, x, drawY, AbstractFontRenderer.argbColorEffect, false, clippingMask, surfaceOffsetX, surfaceOffsetY)
					}
					@Pc(298) val glyphWidth: Int = this.fontMetrics.glyphWidth(ch.code)
					if (AbstractFontRenderer.strikeThroughEffect != -1) {
						this.toolkit!!.horizontalLine(glyphWidth, (this.fontMetrics.verticalSpacing.toDouble() * 0.7).toInt() + drawY, AbstractFontRenderer.strikeThroughEffect, x)
					}
					if (AbstractFontRenderer.underlineEffect != -1) {
						this.toolkit!!.horizontalLine(glyphWidth, this.fontMetrics.verticalSpacing + drawY + 1, AbstractFontRenderer.underlineEffect, x)
					}
					x += glyphWidth
					previousChar = ch.code
				}
			}
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([III[Lclient!mi;IIILclient!aa;IIIIIZILjava/lang/String;)I")
	fun drawText(@OriginalArg(0) spriteHeights: IntArray?, @OriginalArg(1) lineSpacing: Int, @OriginalArg(2) shadowColor: Int, @OriginalArg(3) sprites: Array<NativeSprite?>?, @OriginalArg(4) textColor: Int, @OriginalArg(5) verticalAlign: Int, @OriginalArg(6) height: Int, @OriginalArg(7) clippingMask: ComponentSurface?, @OriginalArg(8) y: Int, @OriginalArg(9) surfaceOffsetX: Int, @OriginalArg(10) surfaceOffsetY: Int, @OriginalArg(11) width: Int, @OriginalArg(12) x: Int, @OriginalArg(14) horizontalAlign: Int, @OriginalArg(15) string: String?): Int {
		return this.renderText(spriteHeights, height, clippingMask, surfaceOffsetX, sprites, textColor, y, width, verticalAlign, string, surfaceOffsetY, x, horizontalAlign, shadowColor, 0, lineSpacing)
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([II[Lclient!mi;IIIILjava/util/Random;IIIIILjava/lang/String;[I)I")
	fun renderTextAntiMacro(@OriginalArg(0) spriteHeights: IntArray?, @OriginalArg(1) maxWidth: Int, @OriginalArg(2) sprites: Array<NativeSprite?>?, @OriginalArg(3) verticalAlign: Int, @OriginalArg(4) color: Int, @OriginalArg(5) maxHeight: Int, @OriginalArg(6) shadow: Int, @OriginalArg(7) random: Random?, @OriginalArg(8) horizontalAlign: Int, @OriginalArg(9) y: Int, @OriginalArg(10) seed: Int, @OriginalArg(12) x: Int, @OriginalArg(13) string: String?, @OriginalArg(14) bounds: IntArray?): Int {
		if (string == null) {
			return 0
		}
		random!!.setSeed(seed.toLong())
		@Pc(20) val alpha: Int = (random.nextInt() and 0x1F) + 192
		this.calibrate((alpha shl 24) or (shadow and 0xFFFFFF), (color and 0xFFFFFF) or (alpha shl 24))
		@Pc(40) val length: Int = string.length
		@Pc(43) val offsetsX: IntArray = IntArray(length)
		@Pc(45) var extraWidth: Int = 0
		for (@Pc(47) i in 0 until length) {
			offsetsX[i] = extraWidth
			if ((random.nextInt() and 0x3) == 0) {
				extraWidth++
			}
		}
		@Pc(72) var drawX: Int = x
		@Pc(78) var drawY: Int = this.fontMetrics!!.paddingTop + y
		@Pc(80) var lineWidth: Int = -1
		if (verticalAlign == 1) {
			drawY += (maxHeight - this.fontMetrics.paddingTop - this.fontMetrics.paddingBottom) / 2
		} else if (verticalAlign == 2) {
			drawY = maxHeight + y - this.fontMetrics.paddingBottom
		}
		if (horizontalAlign == 1) {
			lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth
			drawX = x + (maxWidth - lineWidth) / 2
		} else if (horizontalAlign == 2) {
			lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth
			drawX = x + maxWidth - lineWidth
		}
		this.renderLineWithOffsets(sprites, null, drawX, spriteHeights, offsetsX, drawY, string)
		if (bounds != null) {
			if (lineWidth == -1) {
				lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth
			}
			bounds[2] = lineWidth
			bounds[3] = this.fontMetrics.paddingBottom + this.fontMetrics.paddingTop
			bounds[0] = drawX
			bounds[1] = drawY - this.fontMetrics.paddingTop
		}
		return extraWidth
	}

	companion object {
		@OriginalMember(owner = "client!ke", name = "oe", descriptor = "I")
		@JvmField
		var strikeThroughEffect: Int = -1

		@OriginalMember(owner = "client!bda", name = "A", descriptor = "I")
		@JvmField
		var argbColorEffect: Int = 0

		@OriginalMember(owner = "client!gd", name = "O", descriptor = "I")
		@JvmField
		var underlineEffect: Int = -1

		@OriginalMember(owner = "client!hia", name = "e", descriptor = "I")
		@JvmField
		var shadingEffect: Int = 0

		@OriginalMember(owner = "client!nq", name = "b", descriptor = "I")
		@JvmField
		var justifiedSpaceWidth: Int = 0
		@OriginalMember(owner = "client!ud", name = "b", descriptor = "I")
		@JvmField
		var justifiedSpaceAccumulator: Int = 0
		@OriginalMember(owner = "client!os", name = "c", descriptor = "[Ljava/lang/String;")
		@JvmField
		val LINE_BUFFER: Array<String?> = arrayOfNulls<String>(100)
		@OriginalMember(owner = "client!la", name = "l", descriptor = "I")
		@JvmField
		var baseTextColor: Int = 0
		@OriginalMember(owner = "client!ok", name = "d", descriptor = "I")
		@JvmField
		var baseShadowColor: Int = 0
	}
}
