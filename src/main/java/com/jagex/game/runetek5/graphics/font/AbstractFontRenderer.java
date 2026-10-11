package com.jagex.game.runetek5.graphics.font;

import com.jagex.game.runetek5.GlxShaderCompileStatus;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!da")
public abstract class AbstractFontRenderer {

	@OriginalMember(owner = "client!da", name = "s", descriptor = "Lclient!oea;")
	private final FontMetrics fontMetrics;

	@OriginalMember(owner = "client!da", name = "d", descriptor = "Lclient!ha;")
	private final Toolkit toolkit;

	@OriginalMember(owner = "client!da", name = "<init>", descriptor = "(Lclient!ha;Lclient!oea;)V")
	public AbstractFontRenderer(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
		this.toolkit = toolkit;
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([Lclient!mi;[II[I[IILjava/lang/String;I)V")
	private void renderLineWithOffsets(@OriginalArg(0) NativeSprite[] sprites, @OriginalArg(1) int[] offsetsY, @OriginalArg(2) int x, @OriginalArg(3) int[] spriteHeights, @OriginalArg(4) int[] offsetsX, @OriginalArg(5) int y, @OriginalArg(6) String string) {
		@Pc(7) int drawY = y - this.fontMetrics.verticalSpacing;
		@Pc(13) int tagStart = -1;
		@Pc(15) int previousChar = -1;
		@Pc(17) int offsetIndex = 0;
		@Pc(24) int length = string.length();
		for (@Pc(26) int i = 0; i < length; i++) {
			@Pc(37) char ch = (char) (Utils.charToCp1252(string.charAt(i)) & 0xFF);
			if (ch == '<') {
				tagStart = i;
			} else {
				@Pc(123) int local123;
				@Pc(133) int local133;
				if (ch == '>' && tagStart != -1) {
					@Pc(59) String string2 = string.substring(tagStart + 1, i);
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
							if (string2.startsWith("img=")) {
								try {
									int imageOffsetX;
									if (offsetsX == null) {
										imageOffsetX = 0;
									} else {
										imageOffsetX = offsetsX[offsetIndex];
									}
									int imageOffsetY;
									if (offsetsY == null) {
										imageOffsetY = 0;
									} else {
										imageOffsetY = offsetsY[offsetIndex];
									}
									offsetIndex++;
									@Pc(144) int spriteIndex = Utils.parseInt(string2.substring(4));
									@Pc(148) NativeSprite sprite = sprites[spriteIndex];
									@Pc(157) int spriteHeight = spriteHeights == null ? sprite.scaleHeight() : spriteHeights[spriteIndex];
									sprite.render(x + imageOffsetX, -spriteHeight + this.fontMetrics.verticalSpacing + (drawY - -imageOffsetY), 1, 0, 1);
									x += sprites[spriteIndex].scaleWidth();
									previousChar = -1;
								} catch (@Pc(187) Exception ex) {
								}
							} else {
								this.updateTextEffects(string2);
							}
							continue;
						}
						ch = '®';
					}
				}
				if (tagStart == -1) {
					if (previousChar != -1) {
						x += this.fontMetrics.getKerning(previousChar, ch);
					}
					@Pc(225) int offsetX;
					if (offsetsX == null) {
						offsetX = 0;
					} else {
						offsetX = offsetsX[offsetIndex];
					}
					int offsetY;
					if (offsetsY == null) {
						offsetY = 0;
					} else {
						offsetY = offsetsY[offsetIndex];
					}
					if (ch != ' ') {
						if ((AbstractFontRenderer.shadingEffect & 0xFF000000) != 0) {
							this.fa(ch, x + offsetX + 1, drawY - -1 + offsetY, AbstractFontRenderer.shadingEffect, true);
						}
						this.fa(ch, x + offsetX, drawY + offsetY, AbstractFontRenderer.argbColorEffect, false);
					} else if (AbstractFontRenderer.justifiedSpaceWidth > 0) {
						AbstractFontRenderer.justifiedSpaceAccumulator += AbstractFontRenderer.justifiedSpaceWidth;
						x += AbstractFontRenderer.justifiedSpaceAccumulator >> 8;
						AbstractFontRenderer.justifiedSpaceAccumulator &= 0xFF;
					}
					offsetIndex++;
					int glyphWidth = this.fontMetrics.glyphWidth(ch);
					if (AbstractFontRenderer.strikeThroughEffect != -1) {
						this.toolkit.horizontalLine(glyphWidth, drawY + (int) ((double) this.fontMetrics.verticalSpacing * 0.7D), AbstractFontRenderer.strikeThroughEffect, x);
					}
					if (AbstractFontRenderer.underlineEffect != -1) {
						this.toolkit.horizontalLine(glyphWidth, this.fontMetrics.verticalSpacing + drawY, AbstractFontRenderer.underlineEffect, x);
					}
					previousChar = ch;
					x += glyphWidth;
				}
			}
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(ILjava/lang/String;BIII[Lclient!mi;[I)V")
	public final void drawStringWithSprites(@OriginalArg(0) int y, @OriginalArg(1) String string, @OriginalArg(3) int color, @OriginalArg(5) int x, @OriginalArg(6) NativeSprite[] sprites, @OriginalArg(7) int[] spriteHeights) {
		if (string != null) {
			this.calibrate(0, color);
			this.renderLine(0, y, 0, sprites, null, string, spriteHeights, x);
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([IIBLclient!aa;I[Lclient!mi;IIIILjava/lang/String;IIIIII)I")
	public final int renderText(@OriginalArg(0) int[] spriteHeights, @OriginalArg(1) int height, @OriginalArg(3) ComponentSurface clippingMask, @OriginalArg(4) int surfaceOffsetX, @OriginalArg(5) NativeSprite[] sprites, @OriginalArg(6) int textColor, @OriginalArg(7) int y, @OriginalArg(8) int width, @OriginalArg(9) int verticalAlign, @OriginalArg(10) String string, @OriginalArg(11) int surfaceOffsetY, @OriginalArg(12) int x, @OriginalArg(13) int horizontalAlign, @OriginalArg(14) int shadowColor, @OriginalArg(15) int maxLines, @OriginalArg(16) int lineSpacing) {
		if (string == null) {
			return 0;
		}
		this.calibrate(shadowColor, textColor);
		if (lineSpacing == 0) {
			lineSpacing = this.fontMetrics.verticalSpacing;
		}
		@Pc(48) int[] lineWidths;
		if (this.fontMetrics.paddingBottom + this.fontMetrics.paddingTop + lineSpacing > height && height < lineSpacing + lineSpacing) {
			lineWidths = null;
		} else {
			lineWidths = new int[] { width };
		}
		@Pc(61) int lineCount = this.fontMetrics.splitLinesToBuffer(string, lineWidths, AbstractFontRenderer.LINE_BUFFER, sprites);
		if (maxLines == -1) {
			maxLines = height / lineSpacing;
			if (maxLines <= 0) {
				maxLines = 1;
			}
		}
		if (maxLines > 0 && lineCount >= maxLines) {
			AbstractFontRenderer.LINE_BUFFER[maxLines - 1] = this.fontMetrics.fitText(sprites, width, AbstractFontRenderer.LINE_BUFFER[maxLines - 1]);
			lineCount = maxLines;
		}
		if (verticalAlign == 3 && lineCount == 1) {
			verticalAlign = 1;
		}
		@Pc(128) int lineY;
		@Pc(163) int local163;
		if (verticalAlign == 0) {
			lineY = this.fontMetrics.paddingTop + y;
		} else if (verticalAlign == 1) {
			lineY = (height - this.fontMetrics.paddingBottom - this.fontMetrics.paddingTop - lineSpacing * (lineCount + -1)) / 2 + this.fontMetrics.paddingTop + y;
		} else if (verticalAlign == 2) {
			lineY = height + y - this.fontMetrics.paddingBottom - (lineCount + -1) * lineSpacing;
		} else {
			int lineGap = (height - lineSpacing * (lineCount - 1) - this.fontMetrics.paddingTop - this.fontMetrics.paddingBottom) / (lineCount + 1);
			if (lineGap < 0) {
				lineGap = 0;
			}
			lineSpacing += lineGap;
			lineY = this.fontMetrics.paddingTop + y + lineGap;
		}
		for (int line = 0; line < lineCount; line++) {
			if (horizontalAlign == 0) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x);
			} else if (horizontalAlign == 1) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, (width - this.fontMetrics.getWidthNoSprites(AbstractFontRenderer.LINE_BUFFER[line])) / 2 + x);
			} else if (horizontalAlign == 2) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, width + x - this.fontMetrics.getWidthNoSprites(AbstractFontRenderer.LINE_BUFFER[line]));
			} else if (line == lineCount - 1) {
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x);
			} else {
				this.calculateJustifiedSpacing(AbstractFontRenderer.LINE_BUFFER[line], width);
				this.renderLine(surfaceOffsetY, lineY, surfaceOffsetX, sprites, clippingMask, AbstractFontRenderer.LINE_BUFFER[line], spriteHeights, x);
				AbstractFontRenderer.justifiedSpaceWidth = 0;
			}
			lineY += lineSpacing;
		}
		return lineCount;
	}

	@OriginalMember(owner = "client!da", name = "fa", descriptor = "(CIIIZ)V")
	public abstract void fa(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow);

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;IIIII)V")
	public final void renderRightAligned(@OriginalArg(0) String string, @OriginalArg(1) int y, @OriginalArg(2) int color, @OriginalArg(3) int x, @OriginalArg(5) int shadow) {
		if (string != null) {
			this.calibrate(shadow, color);
			this.renderLine(0, y, 0, null, null, string, null, x - this.fontMetrics.getWidthNoSprites(string));
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(II[ILjava/lang/String;II[Lclient!mi;IILjava/util/Random;)I")
	public final int renderAntiMacroText(@OriginalArg(1) int seed, @OriginalArg(2) int[] spriteHeights, @OriginalArg(3) String string, @OriginalArg(6) NativeSprite[] sprites, @OriginalArg(7) int y, @OriginalArg(8) int x, @OriginalArg(9) Random random) {
		if (string == null) {
			return 0;
		}
		random.setSeed((long) seed);
		@Pc(20) int alpha = (random.nextInt() & 0x1F) + 192;
		this.calibrate((alpha | 0x0) << 24, alpha << 24 | 0xFFFFFF);
		@Pc(42) int length = string.length();
		@Pc(50) int[] offsetsX = new int[length];
		@Pc(52) int extraWidth = 0;
		for (@Pc(54) int i = 0; i < length; i++) {
			offsetsX[i] = extraWidth;
			if ((random.nextInt() & 0x3) == 0) {
				extraWidth++;
			}
		}
		this.renderLineWithOffsets(sprites, null, x, spriteHeights, offsetsX, y, string);
		return extraWidth;
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(BLjava/lang/String;)V")
	private void updateTextEffects(@OriginalArg(1) String string) {
		try {
			if (string.startsWith("col=")) {
				AbstractFontRenderer.argbColorEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | Utils.parseHex(string.substring(4)) & 0xFFFFFF;
			} else if (string.equals("/col")) {
				AbstractFontRenderer.argbColorEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | AbstractFontRenderer.baseTextColor & 0xFFFFFF;
			}
			if (string.startsWith("argb=")) {
				AbstractFontRenderer.argbColorEffect = Utils.parseHex(string.substring(5));
			} else if (string.equals("/argb")) {
				AbstractFontRenderer.argbColorEffect = AbstractFontRenderer.baseTextColor;
			} else if (string.startsWith("str=")) {
				AbstractFontRenderer.strikeThroughEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | Utils.parseHex(string.substring(4));
			} else if (string.equals("str")) {
				AbstractFontRenderer.strikeThroughEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | 0x800000;
			} else if (string.equals("/str")) {
				AbstractFontRenderer.strikeThroughEffect = -1;
			} else if (string.startsWith("u=")) {
				AbstractFontRenderer.underlineEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | Utils.parseHex(string.substring(2));
			} else if (string.equals("u")) {
				AbstractFontRenderer.underlineEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000;
			} else if (string.equals("/u")) {
				AbstractFontRenderer.underlineEffect = -1;
			} else if (string.equalsIgnoreCase("shad=-1")) {
				AbstractFontRenderer.shadingEffect = 0;
			} else if (string.startsWith("shad=")) {
				AbstractFontRenderer.shadingEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000 | Utils.parseHex(string.substring(5));
			} else if (string.equals("shad")) {
				AbstractFontRenderer.shadingEffect = AbstractFontRenderer.argbColorEffect & 0xFF000000;
			} else if (string.equals("/shad")) {
				AbstractFontRenderer.shadingEffect = AbstractFontRenderer.baseShadowColor;
			} else if (string.equals("br")) {
				this.calibrate(AbstractFontRenderer.baseShadowColor, AbstractFontRenderer.baseTextColor);
				return;
			}
		} catch (@Pc(173) Exception ex) {
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(IILjava/lang/String;IIIZ)V")
	public final void renderCenteredWave(@OriginalArg(0) int color, @OriginalArg(1) int y, @OriginalArg(2) String string, @OriginalArg(3) int x, @OriginalArg(5) int waveSeed) {
		if (string == null) {
			return;
		}
		this.calibrate(-16777216, color);
		@Pc(16) int length = string.length();
		@Pc(19) int[] offsetsY = new int[length];
		for (@Pc(27) int i = 0; i < length; i++) {
			offsetsY[i] = (int) (Math.sin((double) i / 2.0D + (double) waveSeed / 5.0D) * 5.0D);
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics.getWidthNoSprites(string) / 2, null, null, y, string);
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(BIILjava/lang/String;II)V")
	public final void renderCentered(@OriginalArg(1) int x, @OriginalArg(2) int color, @OriginalArg(3) String string, @OriginalArg(4) int shadow, @OriginalArg(5) int y) {
		if (string != null) {
			this.calibrate(shadow, color);
			this.renderLine(0, y, 0, null, null, string, null, x - this.fontMetrics.getWidthNoSprites(string) / 2);
		}
	}

	@OriginalMember(owner = "client!da", name = "b", descriptor = "(Ljava/lang/String;IIIII)V")
	public final void drawString(@OriginalArg(0) String string, @OriginalArg(1) int color, @OriginalArg(2) int y, @OriginalArg(3) int x, @OriginalArg(4) int shadow) {
		if (string != null) {
			this.calibrate(shadow, color);
			this.renderLine(0, y, 0, null, null, string, null, x);
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(IILjava/lang/String;IIIII)V")
	public final void renderCenteredShake(@OriginalArg(0) int shakeSeed, @OriginalArg(1) int y, @OriginalArg(2) String string, @OriginalArg(3) int color, @OriginalArg(5) int effectProgress, @OriginalArg(6) int x) {
		if (string == null) {
			return;
		}
		this.calibrate(-16777216, color);
		@Pc(20) double amplitude = 7.0D - (double) effectProgress / 8.0D;
		if (amplitude < 0.0D) {
			amplitude = 0.0D;
		}
		@Pc(29) int length = string.length();
		@Pc(42) int[] offsetsY = new int[length];
		for (@Pc(44) int i = 0; i < length; i++) {
			offsetsY[i] = (int) (Math.sin((double) shakeSeed + (double) i / 1.5D) * amplitude);
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics.getWidthNoSprites(string) / 2, null, null, y, string);
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	public abstract void renderSymbol(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow, @OriginalArg(5) ComponentSurface mask, @OriginalArg(6) int offsetX, @OriginalArg(7) int offsetY);

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(III)V")
	private void calibrate(@OriginalArg(0) int shadowColor, @OriginalArg(2) int textColor) {
		AbstractFontRenderer.underlineEffect = -1;
		if (shadowColor == -1) {
			shadowColor = 0;
		}
		AbstractFontRenderer.baseTextColor = textColor;
		AbstractFontRenderer.argbColorEffect = textColor;
		AbstractFontRenderer.justifiedSpaceAccumulator = 0;
		AbstractFontRenderer.justifiedSpaceWidth = 0;
		AbstractFontRenderer.strikeThroughEffect = -1;
		AbstractFontRenderer.baseShadowColor = shadowColor;
		AbstractFontRenderer.shadingEffect = shadowColor;
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;II)V")
	private void calculateJustifiedSpacing(@OriginalArg(0) String string, @OriginalArg(2) int width) {
		@Pc(7) int spaceCount = 0;
		@Pc(9) boolean inTag = false;
		for (@Pc(11) int i = 0; i < string.length(); i++) {
			@Pc(17) char ch = string.charAt(i);
			if (ch == '<') {
				inTag = true;
			} else if (ch == '>') {
				inTag = false;
			} else if (!inTag && ch == ' ') {
				spaceCount++;
			}
		}
		if (spaceCount > 0) {
			AbstractFontRenderer.justifiedSpaceWidth = (width - this.fontMetrics.getWidthNoSprites(string) << 8) / spaceCount;
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Ljava/lang/String;IIIBII)V")
	public final void renderCenteredWave2(@OriginalArg(0) String string, @OriginalArg(1) int color, @OriginalArg(2) int waveSeed, @OriginalArg(3) int y, @OriginalArg(6) int x) {
		if (string == null) {
			return;
		}
		this.calibrate(-16777216, color);
		@Pc(22) int length = string.length();
		@Pc(25) int[] offsetsX = new int[length];
		@Pc(28) int[] offsetsY = new int[length];
		for (@Pc(30) int i = 0; i < length; i++) {
			offsetsX[i] = (int) (Math.sin((double) waveSeed / 5.0D + (double) i / 5.0D) * 5.0D);
			offsetsY[i] = (int) (Math.sin((double) i / 3.0D + (double) waveSeed / 5.0D) * 5.0D);
		}
		this.renderLineWithOffsets(null, offsetsY, x - this.fontMetrics.getWidthNoSprites(string) / 2, null, offsetsX, y, string);
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(III[Lclient!mi;Lclient!aa;Ljava/lang/String;[III)V")
	private void renderLine(@OriginalArg(0) int surfaceOffsetY, @OriginalArg(1) int y, @OriginalArg(2) int surfaceOffsetX, @OriginalArg(3) NativeSprite[] sprites, @OriginalArg(4) ComponentSurface clippingMask, @OriginalArg(5) String string, @OriginalArg(6) int[] spriteHeights, @OriginalArg(8) int x) {
		@Pc(7) int drawY = y - this.fontMetrics.verticalSpacing;
		@Pc(13) int tagStart = -1;
		@Pc(15) int previousChar = -1;
		@Pc(18) int length = string.length();
		for (@Pc(24) int i = 0; i < length; i++) {
			@Pc(35) char ch = (char) (Utils.charToCp1252(string.charAt(i)) & 0xFF);
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
							if (string2.startsWith("img=")) {
								try {
									@Pc(121) int spriteIndex = Utils.parseInt(string2.substring(4));
									@Pc(125) NativeSprite sprite = sprites[spriteIndex];
									@Pc(134) int spriteHeight = spriteHeights == null ? sprite.scaleHeight() : spriteHeights[spriteIndex];
									if ((AbstractFontRenderer.argbColorEffect & -16777216) == -16777216) {
										sprite.render(x, this.fontMetrics.verticalSpacing + drawY - spriteHeight, 1, 0, 1);
									} else {
										sprite.render(x, this.fontMetrics.verticalSpacing + drawY - spriteHeight, 0, AbstractFontRenderer.argbColorEffect & 0xFF000000 | 0xFFFFFF, 1);
									}
									previousChar = -1;
									x += sprites[spriteIndex].scaleWidth();
								} catch (@Pc(183) Exception ex) {
								}
							} else {
								this.updateTextEffects(string2);
							}
							continue;
						}
						ch = '®';
					}
				}
				if (tagStart == -1) {
					if (previousChar != -1) {
						x += this.fontMetrics.getKerning(previousChar, ch);
					}
					if (ch == ' ') {
						if (AbstractFontRenderer.justifiedSpaceWidth > 0) {
							AbstractFontRenderer.justifiedSpaceAccumulator += AbstractFontRenderer.justifiedSpaceWidth;
							x += AbstractFontRenderer.justifiedSpaceAccumulator >> 8;
							AbstractFontRenderer.justifiedSpaceAccumulator &= 0xFF;
						}
					} else if (clippingMask == null) {
						if ((AbstractFontRenderer.shadingEffect & 0xFF000000) != 0) {
							this.fa(ch, x + 1, drawY + 1, AbstractFontRenderer.shadingEffect, true);
						}
						this.fa(ch, x, drawY, AbstractFontRenderer.argbColorEffect, false);
					} else {
						if ((AbstractFontRenderer.shadingEffect & 0xFF000000) != 0) {
							this.renderSymbol(ch, x + 1, drawY + 1, AbstractFontRenderer.shadingEffect, true, clippingMask, surfaceOffsetX, surfaceOffsetY);
						}
						this.renderSymbol(ch, x, drawY, AbstractFontRenderer.argbColorEffect, false, clippingMask, surfaceOffsetX, surfaceOffsetY);
					}
					@Pc(298) int glyphWidth = this.fontMetrics.glyphWidth(ch);
					if (AbstractFontRenderer.strikeThroughEffect != -1) {
						this.toolkit.horizontalLine(glyphWidth, (int) ((double) this.fontMetrics.verticalSpacing * 0.7D) + drawY, AbstractFontRenderer.strikeThroughEffect, x);
					}
					if (AbstractFontRenderer.underlineEffect != -1) {
						this.toolkit.horizontalLine(glyphWidth, this.fontMetrics.verticalSpacing + drawY + 1, AbstractFontRenderer.underlineEffect, x);
					}
					x += glyphWidth;
					previousChar = ch;
				}
			}
		}
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([III[Lclient!mi;IIILclient!aa;IIIIIZILjava/lang/String;)I")
	public final int drawText(@OriginalArg(0) int[] spriteHeights, @OriginalArg(1) int lineSpacing, @OriginalArg(2) int shadowColor, @OriginalArg(3) NativeSprite[] sprites, @OriginalArg(4) int textColor, @OriginalArg(5) int verticalAlign, @OriginalArg(6) int height, @OriginalArg(7) ComponentSurface clippingMask, @OriginalArg(8) int y, @OriginalArg(9) int surfaceOffsetX, @OriginalArg(10) int surfaceOffsetY, @OriginalArg(11) int width, @OriginalArg(12) int x, @OriginalArg(14) int horizontalAlign, @OriginalArg(15) String string) {
		return this.renderText(spriteHeights, height, clippingMask, surfaceOffsetX, sprites, textColor, y, width, verticalAlign, string, surfaceOffsetY, x, horizontalAlign, shadowColor, 0, lineSpacing);
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "([II[Lclient!mi;IIIILjava/util/Random;IIIIILjava/lang/String;[I)I")
	public final int renderTextAntiMacro(@OriginalArg(0) int[] spriteHeights, @OriginalArg(1) int maxWidth, @OriginalArg(2) NativeSprite[] sprites, @OriginalArg(3) int verticalAlign, @OriginalArg(4) int color, @OriginalArg(5) int maxHeight, @OriginalArg(6) int shadow, @OriginalArg(7) Random random, @OriginalArg(8) int horizontalAlign, @OriginalArg(9) int y, @OriginalArg(10) int seed, @OriginalArg(12) int x, @OriginalArg(13) String string, @OriginalArg(14) int[] bounds) {
		if (string == null) {
			return 0;
		}
		random.setSeed((long) seed);
		@Pc(20) int alpha = (random.nextInt() & 0x1F) + 192;
		this.calibrate(alpha << 24 | shadow & 0xFFFFFF, color & 0xFFFFFF | alpha << 24);
		@Pc(40) int length = string.length();
		@Pc(43) int[] offsetsX = new int[length];
		@Pc(45) int extraWidth = 0;
		for (@Pc(47) int i = 0; i < length; i++) {
			offsetsX[i] = extraWidth;
			if ((random.nextInt() & 0x3) == 0) {
				extraWidth++;
			}
		}
		@Pc(72) int drawX = x;
		@Pc(78) int drawY = this.fontMetrics.paddingTop + y;
		@Pc(80) int lineWidth = -1;
		if (verticalAlign == 1) {
			drawY += (maxHeight - this.fontMetrics.paddingTop - this.fontMetrics.paddingBottom) / 2;
		} else if (verticalAlign == 2) {
			drawY = maxHeight + y - this.fontMetrics.paddingBottom;
		}
		if (horizontalAlign == 1) {
			lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth;
			drawX = x + (maxWidth - lineWidth) / 2;
		} else if (horizontalAlign == 2) {
			lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth;
			drawX = x + maxWidth - lineWidth;
		}
		this.renderLineWithOffsets(sprites, null, drawX, spriteHeights, offsetsX, drawY, string);
		if (bounds != null) {
			if (lineWidth == -1) {
				lineWidth = this.fontMetrics.getWidthNoSprites(string) + extraWidth;
			}
			bounds[2] = lineWidth;
			bounds[3] = this.fontMetrics.paddingBottom + this.fontMetrics.paddingTop;
			bounds[0] = drawX;
			bounds[1] = drawY - this.fontMetrics.paddingTop;
		}
		return extraWidth;
	}
	@OriginalMember(owner = "client!ke", name = "oe", descriptor = "I")
	public static int strikeThroughEffect = -1;

	@OriginalMember(owner = "client!bda", name = "A", descriptor = "I")
	public static int argbColorEffect = 0;

	@OriginalMember(owner = "client!gd", name = "O", descriptor = "I")
	public static int underlineEffect = -1;

	@OriginalMember(owner = "client!hia", name = "e", descriptor = "I")
	public static int shadingEffect = 0;

	@OriginalMember(owner = "client!nq", name = "b", descriptor = "I")
	public static int justifiedSpaceWidth = 0;
	@OriginalMember(owner = "client!ud", name = "b", descriptor = "I")
	public static int justifiedSpaceAccumulator = 0;
	@OriginalMember(owner = "client!os", name = "c", descriptor = "[Ljava/lang/String;")
	public static final String[] LINE_BUFFER = new String[100];
	@OriginalMember(owner = "client!la", name = "l", descriptor = "I")
	public static int baseTextColor = 0;
	@OriginalMember(owner = "client!ok", name = "d", descriptor = "I")
	public static int baseShadowColor = 0;
}
