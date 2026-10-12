package com.jagex.game.runetek5.graphics.font

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.js5.Js5ResourceProvider

@OriginalClass("client!oea")
class FontMetrics {

	@OriginalMember(owner = "client!oea", name = "i", descriptor = "[B")
	private val glyphWidths: ByteArray

	@OriginalMember(owner = "client!oea", name = "b", descriptor = "[[B")
	private var kerning: Array<ByteArray?>? = null

	@OriginalMember(owner = "client!oea", name = "o", descriptor = "I")
	@JvmField
	val verticalSpacing: Int

	@OriginalMember(owner = "client!oea", name = "k", descriptor = "I")
	@JvmField
	val paddingTop: Int

	@OriginalMember(owner = "client!oea", name = "p", descriptor = "I")
	@JvmField
	val paddingBottom: Int

	@OriginalMember(owner = "client!oea", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) bytes: ByteArray?) {
		@Pc(8) val packet: Packet = Packet(bytes)
		@Pc(12) val version: Int = packet.readUByte()
		if (version != 0) {
			throw RuntimeException("")
		}
		@Pc(31) val hasKerning: Boolean = packet.readUByte() == 1
		this.glyphWidths = ByteArray(256)
		packet.readBytes(0, 256, this.glyphWidths)
		if (hasKerning) {
			@Pc(47) val widths: IntArray = IntArray(256)
			@Pc(50) val heights: IntArray = IntArray(256)
			for (@Pc(52) i in 0 until 256) {
				widths[i] = packet.readUByte()
			}
			for (@Pc(66) j in 0 until 256) {
				heights[j] = packet.readUByte()
			}
			@Pc(83) val horizontalOffsets: Array<ByteArray?> = arrayOfNulls<ByteArray>(256)
			@Pc(98) val local98: Int
			for (@Pc(85) k in 0 until 256) {
				horizontalOffsets[k] = ByteArray(widths[k])
				@Pc(96) var horizontalOffset: Byte = 0
				for (column in 0 until horizontalOffsets[k]!!.size) {
					horizontalOffset = (horizontalOffset + packet.readByte()).toByte()
					horizontalOffsets[k]!![column] = horizontalOffset
				}
			}
			@Pc(131) val verticalOffsets: Array<ByteArray?> = arrayOfNulls<ByteArray>(256)
			@Pc(146) val local146: Int
			for (glyph in 0 until 256) {
				verticalOffsets[glyph] = ByteArray(widths[glyph])
				@Pc(144) var verticalOffset: Byte = 0
				for (row in 0 until verticalOffsets[glyph]!!.size) {
					verticalOffset = (verticalOffset + packet.readByte()).toByte()
					verticalOffsets[glyph]!![row] = verticalOffset
				}
			}
			this.kerning = Array<ByteArray?>(256) { ByteArray(256) }
			for (@Pc(183) m in 0 until 256) {
				if (m != 32 && m != 160) {
					for (nextGlyph in 0 until 256) {
						if (nextGlyph != 32 && nextGlyph != 160) {
							this.kerning!![m]!![nextGlyph] = Utils.calculateKerning(this.glyphWidths, nextGlyph, m, horizontalOffsets, verticalOffsets, heights, widths).toByte()
						}
					}
				}
			}
			this.verticalSpacing = heights[32] + widths[32]
		} else {
			this.verticalSpacing = packet.readUByte()
		}
		packet.readUByte()
		packet.readUByte()
		this.paddingTop = packet.readUByte()
		this.paddingBottom = packet.readUByte()
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;ILjava/lang/String;B)Ljava/lang/String;")
	fun fitText(@OriginalArg(0) sprites: Array<NativeSprite?>?, @OriginalArg(1) maxWidth: Int, @OriginalArg(2) string: String?): String? {
		if (this.getTextWidth(string, sprites) <= maxWidth) {
			return string
		}
		@Pc(26) val availableWidth: Int = maxWidth - this.getTextWidth("...", null)
		@Pc(28) var tagStart: Int = -1
		@Pc(35) var previousChar: Int = -1
		@Pc(37) var width: Int = 0
		@Pc(40) val length: Int = string!!.length
		@Pc(42) var string2: String = ""
		for (@Pc(44) i in 0 until length) {
			@Pc(50) var ch: Char = string[i]
			if (ch == '<') {
				tagStart = i
			} else {
				if (ch == '>' && tagStart != -1) {
					@Pc(73) val string3: String = string.substring(tagStart + 1, i)
					tagStart = -1
					if (string3 == "lt") {
						ch = '<'
					} else if (string3 == "gt") {
						ch = '>'
					} else if (string3 == "nbsp") {
						ch = '\u00a0'
					} else if (string3 == "shy") {
						ch = '­'
					} else if (string3 == "times") {
						ch = '×'
					} else if (string3 == "euro") {
						ch = '€'
					} else if (string3 == "copy") {
						ch = '©'
					} else {
						if (string3 != "reg") {
							if (string3.startsWith("img=") && sprites != null) {
								try {
									@Pc(129) val spriteIndex: Int = Utils.parseInt(string3.substring(4))
									previousChar = -1
									width += sprites[spriteIndex]!!.scaleWidth()
									if (width > availableWidth) {
										return string2 + "..."
									}
									string2 = string.substring(0, i + 1)
								} catch (@Pc(159) ex: Exception) {
								}
							}
							continue
						}
						ch = '®'
					}
				}
				if (tagStart == -1) {
					width += this.glyphWidths[Utils.charToCp1252(ch).toInt() and 0xFF].toInt() and 0xFF
					if (this.kerning != null && previousChar != -1) {
						width += this.kerning!![previousChar]!![ch.code].toInt()
					}
					previousChar = ch.code
					@Pc(220) var widthWithDot: Int = width
					if (this.kerning != null) {
						widthWithDot = width + this.kerning!![ch.code]!![46]
					}
					if (widthWithDot > availableWidth) {
						return string2 + "..."
					}
					string2 = string.substring(0, i + 1)
				}
			}
		}
		return string
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(IBC)I")
	fun getKerning(@OriginalArg(0) previousChar: Int, @OriginalArg(2) ch: Char): Int {
		return if (this.kerning == null) 0 else this.kerning!![previousChar]!![ch.code].toInt()
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(ZLjava/lang/String;)I")
	fun getWidthNoSprites(@OriginalArg(1) string: String?): Int {
		return this.getTextWidth(string, null)
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(BI)I")
	fun glyphWidth(@OriginalArg(1) i: Int): Int {
		return this.glyphWidths[i].toInt() and 0xFF
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;IIILjava/lang/String;)I")
	fun getTextHeightMultiline(@OriginalArg(0) sprites: Array<NativeSprite?>?, @OriginalArg(2) lineHeight: Int, @OriginalArg(3) width: Int, @OriginalArg(4) string: String?): Int {
		var lineHeight: Int = lineHeight
		if (lineHeight == 0) {
			lineHeight = this.verticalSpacing
		}
		@Pc(22) val lineCount: Int = this.splitLinesToBuffer(string, intArrayOf(width), FontMetrics.lineBuffer, sprites)
		@Pc(28) val linesHeight: Int = (lineCount - 1) * lineHeight
		return this.paddingTop + linesHeight + this.paddingBottom
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;[Lclient!mi;Z)I")
	fun getTextWidth(@OriginalArg(0) string: String?, @OriginalArg(1) sprites: Array<NativeSprite?>?): Int {
		if (string == null) {
			return 0
		}
		@Pc(17) var tagStart: Int = -1
		@Pc(19) var previousChar: Int = -1
		@Pc(21) var width: Int = 0
		@Pc(24) val length: Int = string.length
		for (@Pc(26) i in 0 until length) {
			@Pc(32) var ch: Char = string[i]
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
						ch = '\u00a0'
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
							if (string2.startsWith("img=") && sprites != null) {
								try {
									@Pc(115) val spriteIndex: Int = Utils.parseInt(string2.substring(4))
									previousChar = -1
									width += sprites[spriteIndex]!!.scaleWidth()
								} catch (@Pc(126) ex: Exception) {
								}
							}
							continue
						}
						ch = '®'
					}
				}
				if (tagStart == -1) {
					width += this.glyphWidths[Utils.charToCp1252(ch).toInt() and 0xFF].toInt() and 0xFF
					if (this.kerning != null && previousChar != -1) {
						width += this.kerning!![previousChar]!![ch.code].toInt()
					}
					previousChar = ch.code
				}
			}
		}
		return width
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;ZI[Lclient!mi;)I")
	fun getTextWidthMultiline(@OriginalArg(0) string: String?, @OriginalArg(2) width: Int, @OriginalArg(3) sprites: Array<NativeSprite?>?): Int {
		@Pc(18) val lineCount: Int = this.splitLinesToBuffer(string, intArrayOf(width), FontMetrics.lineBuffer, sprites)
		@Pc(20) var maxWidth: Int = 0
		for (@Pc(28) i in 0 until lineCount) {
			@Pc(38) val lineWidth: Int = this.getTextWidth(FontMetrics.lineBuffer[i], sprites)
			if (lineWidth > maxWidth) {
				maxWidth = lineWidth
			}
		}
		return maxWidth
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;[I[Ljava/lang/String;B[Lclient!mi;)I")
	fun splitLinesToBuffer(@OriginalArg(0) string: String?, @OriginalArg(1) widths: IntArray?, @OriginalArg(2) strings: Array<String?>?, @OriginalArg(4) sprites: Array<NativeSprite?>?): Int {
		if (string == null) {
			return 0
		}
		@Pc(21) var currentWidth: Int = 0
		@Pc(23) var lineStart: Int = 0
		@Pc(25) var lastSpace: Int = -1
		@Pc(27) var lastSpaceWidth: Int = 0
		@Pc(29) var spaceTrailing: Byte = 0
		@Pc(31) var tagStart: Int = -1
		@Pc(33) var previousChar: Int = -1
		@Pc(35) var lineCount: Int = 0
		@Pc(38) val length: Int = string.length
		for (@Pc(40) i in 0 until length) {
			@Pc(50) var charCode: Int = Utils.charToCp1252(string[i]).toInt() and 0xFF
			@Pc(52) var charWidth: Int = 0
			if (charCode == 60) {
				tagStart = i
			} else {
				@Pc(69) val charStart: Int
				if (tagStart == -1) {
					charWidth = this.glyphWidth(charCode)
					charStart = i
					if (this.kerning != null && previousChar != -1) {
						charWidth += this.kerning!![previousChar]!![charCode]
					}
					previousChar = charCode
				} else {
					if (charCode != 62) {
						continue
					}
					charStart = tagStart
					@Pc(76) val string2: String = string.substring(tagStart + 1, i)
					tagStart = -1
					if (string2 == "br") {
						strings!![lineCount] = string.substring(lineStart, i + 1)
						lineCount++
						if (strings.size <= lineCount) {
							return 0
						}
						lineStart = i + 1
						previousChar = -1
						currentWidth = 0
						lastSpace = -1
						continue
					}
					if (string2 == "lt") {
						charWidth = this.glyphWidth(60)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![60]
						}
						previousChar = 60
					} else if (string2 == "gt") {
						charWidth = this.glyphWidth(62)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![62]
						}
						previousChar = 62
					} else if (string2 == "nbsp") {
						charWidth = this.glyphWidth(160)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![160]
						}
						previousChar = 160
					} else if (string2 == "shy") {
						charWidth = this.glyphWidth(173)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![173]
						}
						previousChar = 173
					} else if (string2 == "times") {
						charWidth = this.glyphWidth(215)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![215]
						}
						previousChar = 215
					} else if (string2 == "euro") {
						charWidth = this.glyphWidth(8364)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![8364]
						}
						previousChar = 8364
					} else if (string2 == "copy") {
						charWidth = this.glyphWidth(169)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![169]
						}
						previousChar = 169
					} else if (string2 == "reg") {
						charWidth = this.glyphWidth(174)
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning!![previousChar]!![174]
						}
						previousChar = 174
					} else if (string2.startsWith("img=") && sprites != null) {
						try {
							@Pc(232) val spriteIndex: Int = Utils.parseInt(string2.substring(4))
							previousChar = -1
							charWidth = sprites[spriteIndex]!!.scaleWidth()
						} catch (@Pc(243) ex: Exception) {
						}
					}
					charCode = -1
				}
				if (charWidth > 0) {
					currentWidth += charWidth
					if (widths != null) {
						if (charCode == 32) {
							spaceTrailing = 1
							lastSpaceWidth = currentWidth
							lastSpace = i
						}
						if (widths[if (widths.size > lineCount) lineCount else widths.size - 1] < currentWidth) {
							if (lastSpace >= 0) {
								strings!![lineCount] = string.substring(lineStart, lastSpace + 1 - spaceTrailing)
								lineCount++
								if (lineCount >= strings.size) {
									return 0
								}
								lineStart = lastSpace + 1
								previousChar = -1
								lastSpace = -1
								currentWidth -= lastSpaceWidth
							} else {
								strings!![lineCount] = string.substring(lineStart, charStart)
								lineCount++
								if (strings.size <= lineCount) {
									return 0
								}
								lineStart = charStart
								previousChar = -1
								lastSpace = -1
								currentWidth = charWidth
							}
						}
						if (charCode == 45) {
							lastSpaceWidth = currentWidth
							lastSpace = i
							spaceTrailing = 0
						}
					}
				}
			}
		}
		if (lineStart < string.length) {
			strings!![lineCount] = string.substring(lineStart, string.length)
			lineCount++
		}
		return lineCount
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;ILjava/lang/String;I)I")
	fun getLineCount(@OriginalArg(0) sprites: Array<NativeSprite?>?, @OriginalArg(2) string: String?, @OriginalArg(3) width: Int): Int {
		return this.splitLinesToBuffer(string, intArrayOf(width), FontMetrics.lineBuffer, sprites)
	}

	companion object {
		@OriginalMember(owner = "client!kga", name = "d", descriptor = "Z")
		@JvmField
		var useBoldFont: Boolean = false

		@OriginalMember(owner = "client!fn", name = "d", descriptor = "Lclient!oea;")
		@JvmField
		var defaultFontMetrics: FontMetrics? = null
		@OriginalMember(owner = "client!qg", name = "e", descriptor = "[Ljava/lang/String;")
		@JvmField
		val lineBuffer: Array<String?> = arrayOfNulls<String>(100)
		@OriginalMember(owner = "client!od", name = "a", descriptor = "(BILclient!in;)Lclient!oea;")
		@JvmStatic
		fun list(@OriginalArg(1) id: Int, @OriginalArg(2) resource: Js5ResourceProvider?): FontMetrics? {
			@Pc(13) val data: ByteArray? = resource!!.getFile(id)
			return if (data == null) null else FontMetrics(data)
		}
	}
}
