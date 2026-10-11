package com.jagex.game.runetek5.graphics.font;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

@OriginalClass("client!oea")
public final class FontMetrics {

	@OriginalMember(owner = "client!oea", name = "i", descriptor = "[B")
	private final byte[] glyphWidths;

	@OriginalMember(owner = "client!oea", name = "b", descriptor = "[[B")
	private byte[][] kerning;

	@OriginalMember(owner = "client!oea", name = "o", descriptor = "I")
	public final int verticalSpacing;

	@OriginalMember(owner = "client!oea", name = "k", descriptor = "I")
	public final int paddingTop;

	@OriginalMember(owner = "client!oea", name = "p", descriptor = "I")
	public final int paddingBottom;

	@OriginalMember(owner = "client!oea", name = "<init>", descriptor = "([B)V")
	public FontMetrics(@OriginalArg(0) byte[] bytes) {
		@Pc(8) Packet packet = new Packet(bytes);
		@Pc(12) int version = packet.readUByte();
		if (version != 0) {
			throw new RuntimeException("");
		}
		@Pc(31) boolean hasKerning = packet.readUByte() == 1;
		this.glyphWidths = new byte[256];
		packet.readBytes(0, 256, this.glyphWidths);
		if (hasKerning) {
			@Pc(47) int[] widths = new int[256];
			@Pc(50) int[] heights = new int[256];
			for (@Pc(52) int i = 0; i < 256; i++) {
				widths[i] = packet.readUByte();
			}
			for (@Pc(66) int j = 0; j < 256; j++) {
				heights[j] = packet.readUByte();
			}
			@Pc(83) byte[][] horizontalOffsets = new byte[256][];
			@Pc(98) int local98;
			for (@Pc(85) int k = 0; k < 256; k++) {
				horizontalOffsets[k] = new byte[widths[k]];
				@Pc(96) byte horizontalOffset = 0;
				for (int column = 0; column < horizontalOffsets[k].length; column++) {
					horizontalOffset += packet.readByte();
					horizontalOffsets[k][column] = horizontalOffset;
				}
			}
			@Pc(131) byte[][] verticalOffsets = new byte[256][];
			@Pc(146) int local146;
			for (int glyph = 0; glyph < 256; glyph++) {
				verticalOffsets[glyph] = new byte[widths[glyph]];
				@Pc(144) byte verticalOffset = 0;
				for (int row = 0; row < verticalOffsets[glyph].length; row++) {
					verticalOffset += packet.readByte();
					verticalOffsets[glyph][row] = verticalOffset;
				}
			}
			this.kerning = new byte[256][256];
			for (@Pc(183) int m = 0; m < 256; m++) {
				if (m != 32 && m != 160) {
					for (int nextGlyph = 0; nextGlyph < 256; nextGlyph++) {
						if (nextGlyph != 32 && nextGlyph != 160) {
							this.kerning[m][nextGlyph] = (byte) Utils.calculateKerning(this.glyphWidths, nextGlyph, m, horizontalOffsets, verticalOffsets, heights, widths);
						}
					}
				}
			}
			this.verticalSpacing = heights[32] + widths[32];
		} else {
			this.verticalSpacing = packet.readUByte();
		}
		packet.readUByte();
		packet.readUByte();
		this.paddingTop = packet.readUByte();
		this.paddingBottom = packet.readUByte();
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;ILjava/lang/String;B)Ljava/lang/String;")
	public String fitText(@OriginalArg(0) NativeSprite[] sprites, @OriginalArg(1) int maxWidth, @OriginalArg(2) String string) {
		if (this.getTextWidth(string, sprites) <= maxWidth) {
			return string;
		}
		@Pc(26) int availableWidth = maxWidth - this.getTextWidth("...", null);
		@Pc(28) int tagStart = -1;
		@Pc(35) int previousChar = -1;
		@Pc(37) int width = 0;
		@Pc(40) int length = string.length();
		@Pc(42) String string2 = "";
		for (@Pc(44) int i = 0; i < length; i++) {
			@Pc(50) char ch = string.charAt(i);
			if (ch == '<') {
				tagStart = i;
			} else {
				if (ch == '>' && tagStart != -1) {
					@Pc(73) String string3 = string.substring(tagStart + 1, i);
					tagStart = -1;
					if (string3.equals("lt")) {
						ch = '<';
					} else if (string3.equals("gt")) {
						ch = '>';
					} else if (string3.equals("nbsp")) {
						ch = ' ';
					} else if (string3.equals("shy")) {
						ch = '\u00ad';
					} else if (string3.equals("times")) {
						ch = '×';
					} else if (string3.equals("euro")) {
						ch = '€';
					} else if (string3.equals("copy")) {
						ch = '©';
					} else {
						if (!string3.equals("reg")) {
							if (string3.startsWith("img=") && sprites != null) {
								try {
									@Pc(129) int spriteIndex = Utils.parseInt(string3.substring(4));
									previousChar = -1;
									width += sprites[spriteIndex].scaleWidth();
									if (width > availableWidth) {
										return string2 + "...";
									}
									string2 = string.substring(0, i + 1);
								} catch (@Pc(159) Exception ex) {
								}
							}
							continue;
						}
						ch = '®';
					}
				}
				if (tagStart == -1) {
					width += this.glyphWidths[Utils.charToCp1252(ch) & 0xFF] & 0xFF;
					if (this.kerning != null && previousChar != -1) {
						width += this.kerning[previousChar][ch];
					}
					previousChar = ch;
					@Pc(220) int widthWithDot = width;
					if (this.kerning != null) {
						widthWithDot = width + this.kerning[ch][46];
					}
					if (widthWithDot > availableWidth) {
						return string2 + "...";
					}
					string2 = string.substring(0, i + 1);
				}
			}
		}
		return string;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(IBC)I")
	public int getKerning(@OriginalArg(0) int previousChar, @OriginalArg(2) char ch) {
		return this.kerning == null ? 0 : this.kerning[previousChar][ch];
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(ZLjava/lang/String;)I")
	public int getWidthNoSprites(@OriginalArg(1) String string) {
		return this.getTextWidth(string, null);
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(BI)I")
	public int glyphWidth(@OriginalArg(1) int i) {
		return this.glyphWidths[i] & 0xFF;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;IIILjava/lang/String;)I")
	public int getTextHeightMultiline(@OriginalArg(0) NativeSprite[] sprites, @OriginalArg(2) int lineHeight, @OriginalArg(3) int width, @OriginalArg(4) String string) {
		if (lineHeight == 0) {
			lineHeight = this.verticalSpacing;
		}
		@Pc(22) int lineCount = this.splitLinesToBuffer(string, new int[] { width }, FontMetrics.lineBuffer, sprites);
		@Pc(28) int linesHeight = (lineCount - 1) * lineHeight;
		return this.paddingTop + linesHeight + this.paddingBottom;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;[Lclient!mi;Z)I")
	public int getTextWidth(@OriginalArg(0) String string, @OriginalArg(1) NativeSprite[] sprites) {
		if (string == null) {
			return 0;
		}
		@Pc(17) int tagStart = -1;
		@Pc(19) int previousChar = -1;
		@Pc(21) int width = 0;
		@Pc(24) int length = string.length();
		for (@Pc(26) int i = 0; i < length; i++) {
			@Pc(32) char ch = string.charAt(i);
			if (ch == '<') {
				tagStart = i;
			} else {
				if (ch == '>' && tagStart != -1) {
					@Pc(57) String string2 = string.substring(tagStart + 1, i);
					tagStart = -1;
					if (string2.equals("lt")) {
						ch = '<';
					} else if (string2.equals("gt")) {
						ch = '>';
					} else if (string2.equals("nbsp")) {
						ch = ' ';
					} else if (string2.equals("shy")) {
						ch = '\u00ad';
					} else if (string2.equals("times")) {
						ch = '×';
					} else if (string2.equals("euro")) {
						ch = '€';
					} else if (string2.equals("copy")) {
						ch = '©';
					} else {
						if (!string2.equals("reg")) {
							if (string2.startsWith("img=") && sprites != null) {
								try {
									@Pc(115) int spriteIndex = Utils.parseInt(string2.substring(4));
									previousChar = -1;
									width += sprites[spriteIndex].scaleWidth();
								} catch (@Pc(126) Exception ex) {
								}
							}
							continue;
						}
						ch = '®';
					}
				}
				if (tagStart == -1) {
					width += this.glyphWidths[Utils.charToCp1252(ch) & 0xFF] & 0xFF;
					if (this.kerning != null && previousChar != -1) {
						width += this.kerning[previousChar][ch];
					}
					previousChar = ch;
				}
			}
		}
		return width;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;ZI[Lclient!mi;)I")
	public int getTextWidthMultiline(@OriginalArg(0) String string, @OriginalArg(2) int width, @OriginalArg(3) NativeSprite[] sprites) {
		@Pc(18) int lineCount = this.splitLinesToBuffer(string, new int[] { width }, FontMetrics.lineBuffer, sprites);
		@Pc(20) int maxWidth = 0;
		for (@Pc(28) int i = 0; i < lineCount; i++) {
			@Pc(38) int lineWidth = this.getTextWidth(FontMetrics.lineBuffer[i], sprites);
			if (lineWidth > maxWidth) {
				maxWidth = lineWidth;
			}
		}
		return maxWidth;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "(Ljava/lang/String;[I[Ljava/lang/String;B[Lclient!mi;)I")
	public int splitLinesToBuffer(@OriginalArg(0) String string, @OriginalArg(1) int[] widths, @OriginalArg(2) String[] strings, @OriginalArg(4) NativeSprite[] sprites) {
		if (string == null) {
			return 0;
		}
		@Pc(21) int currentWidth = 0;
		@Pc(23) int lineStart = 0;
		@Pc(25) int lastSpace = -1;
		@Pc(27) int lastSpaceWidth = 0;
		@Pc(29) byte spaceTrailing = 0;
		@Pc(31) int tagStart = -1;
		@Pc(33) int previousChar = -1;
		@Pc(35) int lineCount = 0;
		@Pc(38) int length = string.length();
		for (@Pc(40) int i = 0; i < length; i++) {
			@Pc(50) int charCode = Utils.charToCp1252(string.charAt(i)) & 0xFF;
			@Pc(52) int charWidth = 0;
			if (charCode == 60) {
				tagStart = i;
			} else {
				@Pc(69) int charStart;
				if (tagStart == -1) {
					charWidth = this.glyphWidth(charCode);
					charStart = i;
					if (this.kerning != null && previousChar != -1) {
						charWidth += this.kerning[previousChar][charCode];
					}
					previousChar = charCode;
				} else {
					if (charCode != 62) {
						continue;
					}
					charStart = tagStart;
					@Pc(76) String string2 = string.substring(tagStart + 1, i);
					tagStart = -1;
					if (string2.equals("br")) {
						strings[lineCount] = string.substring(lineStart, i + 1);
						lineCount++;
						if (strings.length <= lineCount) {
							return 0;
						}
						lineStart = i + 1;
						previousChar = -1;
						currentWidth = 0;
						lastSpace = -1;
						continue;
					}
					if (string2.equals("lt")) {
						charWidth = this.glyphWidth(60);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][60];
						}
						previousChar = 60;
					} else if (string2.equals("gt")) {
						charWidth = this.glyphWidth(62);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][62];
						}
						previousChar = 62;
					} else if (string2.equals("nbsp")) {
						charWidth = this.glyphWidth(160);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][160];
						}
						previousChar = 160;
					} else if (string2.equals("shy")) {
						charWidth = this.glyphWidth(173);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][173];
						}
						previousChar = 173;
					} else if (string2.equals("times")) {
						charWidth = this.glyphWidth(215);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][215];
						}
						previousChar = 215;
					} else if (string2.equals("euro")) {
						charWidth = this.glyphWidth(8364);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][8364];
						}
						previousChar = 8364;
					} else if (string2.equals("copy")) {
						charWidth = this.glyphWidth(169);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][169];
						}
						previousChar = 169;
					} else if (string2.equals("reg")) {
						charWidth = this.glyphWidth(174);
						if (this.kerning != null && previousChar != -1) {
							charWidth += this.kerning[previousChar][174];
						}
						previousChar = 174;
					} else if (string2.startsWith("img=") && sprites != null) {
						try {
							@Pc(232) int spriteIndex = Utils.parseInt(string2.substring(4));
							previousChar = -1;
							charWidth = sprites[spriteIndex].scaleWidth();
						} catch (@Pc(243) Exception ex) {
						}
					}
					charCode = -1;
				}
				if (charWidth > 0) {
					currentWidth += charWidth;
					if (widths != null) {
						if (charCode == 32) {
							spaceTrailing = 1;
							lastSpaceWidth = currentWidth;
							lastSpace = i;
						}
						if (widths[widths.length > lineCount ? lineCount : widths.length - 1] < currentWidth) {
							if (lastSpace >= 0) {
								strings[lineCount] = string.substring(lineStart, lastSpace + 1 - spaceTrailing);
								lineCount++;
								if (lineCount >= strings.length) {
									return 0;
								}
								lineStart = lastSpace + 1;
								previousChar = -1;
								lastSpace = -1;
								currentWidth -= lastSpaceWidth;
							} else {
								strings[lineCount] = string.substring(lineStart, charStart);
								lineCount++;
								if (strings.length <= lineCount) {
									return 0;
								}
								lineStart = charStart;
								previousChar = -1;
								lastSpace = -1;
								currentWidth = charWidth;
							}
						}
						if (charCode == 45) {
							lastSpaceWidth = currentWidth;
							lastSpace = i;
							spaceTrailing = 0;
						}
					}
				}
			}
		}
		if (lineStart < string.length()) {
			strings[lineCount] = string.substring(lineStart, string.length());
			lineCount++;
		}
		return lineCount;
	}

	@OriginalMember(owner = "client!oea", name = "a", descriptor = "([Lclient!mi;ILjava/lang/String;I)I")
	public int getLineCount(@OriginalArg(0) NativeSprite[] sprites, @OriginalArg(2) String string, @OriginalArg(3) int width) {
		return this.splitLinesToBuffer(string, new int[] { width }, FontMetrics.lineBuffer, sprites);
	}
	@OriginalMember(owner = "client!kga", name = "d", descriptor = "Z")
	public static boolean useBoldFont = false;

	@OriginalMember(owner = "client!fn", name = "d", descriptor = "Lclient!oea;")
	public static FontMetrics defaultFontMetrics;
	@OriginalMember(owner = "client!qg", name = "e", descriptor = "[Ljava/lang/String;")
	public static final String[] lineBuffer = new String[100];
	@OriginalMember(owner = "client!od", name = "a", descriptor = "(BILclient!in;)Lclient!oea;")
	public static FontMetrics list(@OriginalArg(1) int id, @OriginalArg(2) Js5ResourceProvider resource) {
		@Pc(13) byte[] data = resource.getFile(id);
		return data == null ? null : new FontMetrics(data);
	}
}
