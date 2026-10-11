import com.jagex.game.runetek5.JavaSpriteBlitState;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!faa")
public final class PaletteSpriteSM extends SpriteSM {

	@OriginalMember(owner = "client!faa", name = "H", descriptor = "[B")
	private final byte[] spritePixels;

	@OriginalMember(owner = "client!faa", name = "I", descriptor = "[I")
	private final int[] spritePalette;

	@OriginalMember(owner = "client!faa", name = "<init>", descriptor = "(Lclient!ao;[B[III)V")
	public PaletteSpriteSM(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) byte[] spritePixels, @OriginalArg(2) int[] spritePalette, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		super(javaToolkit, width, height);
		this.spritePixels = spritePixels;
		this.spritePalette = spritePalette;
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void captureScreenRegion(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(III)V")
	@Override
	public void copyAlpha(@OriginalArg(2) int channel) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIIIIII)V")
	@Override
	public void renderDepthTestedRgb(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) int op, @OriginalArg(6) int colour, @OriginalArg(7) int mode) {
		if (width <= 0 || height <= 0) {
			return;
		}
		@Pc(6) int u = 0;
		@Pc(8) int v = 0;
		@Pc(17) int scaleWidth = super.leftMargin + super.imageWidth + super.trimRight;
		@Pc(26) int scaleHeight = super.topMargin + super.imageHeight + super.trimBottom;
		@Pc(32) int uStep = (scaleWidth << 16) / width;
		@Pc(38) int vStep = (scaleHeight << 16) / height;
		@Pc(52) int offset;
		if (super.leftMargin > 0) {
			offset = ((super.leftMargin << 16) + uStep - 1) / uStep;
			x += offset;
			u = offset * uStep - (super.leftMargin << 16);
		}
		if (super.topMargin > 0) {
			offset = ((super.topMargin << 16) + vStep - 1) / vStep;
			y += offset;
			v = offset * vStep - (super.topMargin << 16);
		}
		if (super.imageWidth < scaleWidth) {
			width = ((super.imageWidth << 16) + uStep - u - 1) / uStep;
		}
		if (super.imageHeight < scaleHeight) {
			height = ((super.imageHeight << 16) + vStep - v - 1) / vStep;
		}
		offset = x + y * super.toolkit.surfaceWidth;
		@Pc(144) int dstStep = super.toolkit.surfaceWidth - width;
		if (y + height > super.toolkit.clipY2) {
			height -= y + height - super.toolkit.clipY2;
		}
		@Pc(172) int clip;
		if (y < super.toolkit.clipY1) {
			clip = super.toolkit.clipY1 - y;
			height -= clip;
			offset += clip * super.toolkit.surfaceWidth;
			v += vStep * clip;
		}
		if (x + width > super.toolkit.clipX2) {
			clip = x + width - super.toolkit.clipX2;
			width -= clip;
			dstStep += clip;
		}
		if (x < super.toolkit.clipX1) {
			clip = super.toolkit.clipX1 - x;
			width -= clip;
			offset += clip;
			u += uStep * clip;
			dstStep += clip;
		}
		@Pc(246) float[] depth = super.toolkit.depthBuffer;
		@Pc(250) int[] raster = super.toolkit.surfaceRaster;
		@Pc(257) int local257;
		@Pc(260) int local260;
		@Pc(268) int local268;
		@Pc(271) int local271;
		@Pc(345) int local345;
		@Pc(353) int local353;
		@Pc(356) int local356;
		@Pc(378) int local378;
		@Pc(394) int local394;
		@Pc(402) int local402;
		@Pc(578) int lerpColour;
		@Pc(475) byte paletteIndex;
		@Pc(386) int local386;
		if (mode != 0) {
			@Pc(1295) byte local1295;
			@Pc(945) int local945;
			@Pc(953) int local953;
			@Pc(961) int local961;
			@Pc(973) int local973;
			if (mode == 1) {
				if (op == 1) {
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							if ((float) z < depth[offset]) {
								paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
								if (paletteIndex != 0) {
									raster[offset] = this.spritePalette[paletteIndex & 0xFF];
									depth[offset] = z;
								}
							}
							u += uStep;
							offset++;
						}
						v += vStep;
						u = uStart;
						offset += dstStep;
					}
				} else {
					@Pc(798) byte local798;
					if (op == 0) {
						int uStart = u;
						if ((colour & 0xFFFFFF) == 16777215) {
							int alpha = colour >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								int srcRowOffset = (v >> 16) * super.imageWidth;
								for (int column = -width; column < 0; column++) {
									if ((float) z < depth[offset]) {
										byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
										if (colourIndex != 0) {
											int srcColour = this.spritePalette[colourIndex & 0xFF];
											int dstColour = raster[offset];
											raster[offset] = ((srcColour & 0xFF00FF) * alpha + (dstColour & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (dstColour & 0xFF00) * invAlpha & 0xFF0000) >> 8;
											depth[offset] = z;
										}
									}
									u += uStep;
									offset++;
								}
								v += vStep;
								u = uStart;
								offset += dstStep;
							}
						} else {
							int red = colour >> 16 & 0xFF;
							int green = colour >> 8 & 0xFF;
							int blue = colour & 0xFF;
							int alpha = colour >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								int srcRowOffset = (v >> 16) * super.imageWidth;
								for (int column = -width; column < 0; column++) {
									if ((float) z < depth[offset]) {
										@Pc(925) byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
										if (colourIndex != 0) {
											int srcColour = this.spritePalette[colourIndex & 0xFF];
											if (alpha == 255) {
												int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
												int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
												int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
												raster[offset] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
												depth[offset] = z;
											} else {
												int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
												int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
												int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
												srcColour = (scaledRed | scaledGreen | scaledBlue) >>> 8;
												int dstColour = raster[offset];
												raster[offset] = ((srcColour & 0xFF00FF) * alpha + (dstColour & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (dstColour & 0xFF00) * invAlpha & 0xFF0000) >> 8;
												depth[offset] = z;
											}
										}
									}
									u += uStep;
									offset++;
								}
								v += vStep;
								u = uStart;
								offset += dstStep;
							}
						}
					} else if (op == 3) {
						int uStart = u;
						int alpha = colour >>> 24;
						int invAlpha = 256 - alpha;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								if ((float) z < depth[offset]) {
									byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
									int srcColour = colourIndex > 0 ? this.spritePalette[colourIndex] : 0;
									int sum = srcColour + colour;
									int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
									int pixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
									pixel = sum - pixel | pixel - (pixel >>> 8);
									if (srcColour == 0 && alpha != 255) {
										srcColour = pixel;
										pixel = raster[offset];
										pixel = ((srcColour & 0xFF00FF) * alpha + (pixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (pixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									}
									raster[offset] = pixel;
									depth[offset] = z;
								}
								u += uStep;
								offset++;
							}
							v += vStep;
							u = uStart;
							offset += dstStep;
						}
					} else if (op == 2) {
						int alpha = colour >>> 24;
						int invAlpha = 256 - alpha;
						int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
						int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
						lerpColour = (redBlue | green) >>> 8;
						int uStart = u;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								if ((float) z < depth[offset]) {
									byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
									if (colourIndex != 0) {
										int srcColour = this.spritePalette[colourIndex & 0xFF];
										redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
										green = (srcColour & 0xFF00) * alpha & 0xFF0000;
										raster[offset] = ((redBlue | green) >>> 8) + lerpColour;
										depth[offset] = z;
									}
								}
								u += uStep;
								offset++;
							}
							v += vStep;
							u = uStart;
							offset += dstStep;
						}
					} else {
						throw new IllegalArgumentException();
					}
				}
			} else if (mode != 2) {
				throw new IllegalArgumentException();
			} else if (op == 1) {
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (paletteIndex != 0) {
								int srcColour = this.spritePalette[paletteIndex & 0xFF];
								int dstColour = raster[offset];
								int sum = srcColour + dstColour;
								int redBlueSum = (srcColour & 0xFF00FF) + (dstColour & 0xFF00FF);
								dstColour = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset] = sum - dstColour | dstColour - (dstColour >>> 8);
								depth[offset] = z;
							}
						}
						u += uStep;
						offset++;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 0) {
				int uStart = u;
				int red = colour >> 16 & 0xFF;
				int green = colour >> 8 & 0xFF;
				int blue = colour & 0xFF;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							@Pc(1525) byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (colourIndex != 0) {
								int srcColour = this.spritePalette[colourIndex & 0xFF];
								int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
								int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
								int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
								srcColour = (scaledRed | scaledGreen | scaledBlue) >>> 8;
								int dstColour = raster[offset];
								int sum = srcColour + dstColour;
								int redBlueSum = (srcColour & 0xFF00FF) + (dstColour & 0xFF00FF);
								dstColour = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset] = sum - dstColour | dstColour - (dstColour >>> 8);
								depth[offset] = z;
							}
						}
						u += uStep;
						offset++;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 3) {
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							int srcColour = paletteIndex > 0 ? this.spritePalette[paletteIndex] : 0;
							int sum = srcColour + colour;
							int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
							int carry = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							srcColour = sum - carry | carry - (carry >>> 8);
							@Pc(1712) int dst = raster[offset];
							sum = srcColour + dst;
							redBlueSum = (srcColour & 0xFF00FF) + (dst & 0xFF00FF);
							@Pc(1734) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							raster[offset] = sum - overflow | overflow - (overflow >>> 8);
							depth[offset] = z;
						}
						u += uStep;
						offset++;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 2) {
				int alpha = colour >>> 24;
				int invAlpha = 256 - alpha;
				int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (redBlue | green) >>> 8;
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (colourIndex != 0) {
								int srcColour = this.spritePalette[colourIndex & 0xFF];
								redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
								green = (srcColour & 0xFF00) * alpha & 0xFF0000;
								@Pc(1872) int blended = ((redBlue | green) >>> 8) + lerpColour;
								int dstColour = raster[offset];
								int sum = blended + dstColour;
								int redBlueSum = (blended & 0xFF00FF) + (dstColour & 0xFF00FF);
								@Pc(1898) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset] = sum - overflow | overflow - (overflow >>> 8);
								depth[offset] = z;
							}
						}
						u += uStep;
						offset++;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else {
				throw new IllegalArgumentException();
			}
		} else if (op == 1) {
			int uStart = u;
			for (int row = -height; row < 0; row++) {
				int srcRowOffset = (v >> 16) * super.imageWidth;
				for (int column = -width; column < 0; column++) {
					if ((float) z < depth[offset]) {
						raster[offset] = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						depth[offset] = z;
					}
					u += uStep;
					offset++;
				}
				v += vStep;
				u = uStart;
				offset += dstStep;
			}
		} else if (op == 0) {
			int red = colour >> 16 & 0xFF;
			int green = colour >> 8 & 0xFF;
			int blue = colour & 0xFF;
			int uStart = u;
			for (int row = -height; row < 0; row++) {
				int srcRowOffset = (v >> 16) * super.imageWidth;
				for (int column = -width; column < 0; column++) {
					if ((float) z < depth[offset]) {
						int srcColour = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
						int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
						int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
						raster[offset] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
						depth[offset] = z;
					}
					u += uStep;
					offset++;
				}
				v += vStep;
				u = uStart;
				offset += dstStep;
			}
		} else if (op == 3) {
			int uStart = u;
			for (int row = -height; row < 0; row++) {
				int srcRowOffset = (v >> 16) * super.imageWidth;
				for (int column = -width; column < 0; column++) {
					if ((float) z < depth[offset]) {
						paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
						int srcColour = paletteIndex > 0 ? this.spritePalette[paletteIndex] : 0;
						int sum = srcColour + colour;
						int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
						int carry = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						raster[offset] = sum - carry | carry - (carry >>> 8);
						depth[offset] = z;
					}
					u += uStep;
					offset++;
				}
				v += vStep;
				u = uStart;
				offset += dstStep;
			}
		} else if (op == 2) {
			int alpha = colour >>> 24;
			int invAlpha = 256 - alpha;
			int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
			int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
			lerpColour = (redBlue | green) >>> 8;
			int uStart = u;
			for (int row = -height; row < 0; row++) {
				int srcRowOffset = (v >> 16) * super.imageWidth;
				for (int column = -width; column < 0; column++) {
					if ((float) z < depth[offset]) {
						int srcColour = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
						green = (srcColour & 0xFF00) * alpha & 0xFF0000;
						raster[offset] = ((redBlue | green) >>> 8) + lerpColour;
						depth[offset] = z;
					}
					u += uStep;
					offset++;
				}
				v += vStep;
				u = uStart;
				offset += dstStep;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIIIIII)V")
	@Override
	public void renderImpl(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour, @OriginalArg(6) int mode) {
		if (super.toolkit.stopped()) {
			throw new IllegalStateException();
		} else if (width > 0 && height > 0) {
			@Pc(14) int u = 0;
			@Pc(16) int v = 0;
			@Pc(20) int dstStride = super.toolkit.surfaceWidth;
			@Pc(29) int scaleWidth = super.leftMargin + super.imageWidth + super.trimRight;
			@Pc(38) int scaleHeight = super.topMargin + super.imageHeight + super.trimBottom;
			@Pc(44) int uStep = (scaleWidth << 16) / width;
			@Pc(50) int vStep = (scaleHeight << 16) / height;
			@Pc(64) int offset;
			if (super.leftMargin > 0) {
				offset = ((super.leftMargin << 16) + uStep - 1) / uStep;
				x += offset;
				u = offset * uStep - (super.leftMargin << 16);
			}
			if (super.topMargin > 0) {
				offset = ((super.topMargin << 16) + vStep - 1) / vStep;
				y += offset;
				v = offset * vStep - (super.topMargin << 16);
			}
			if (super.imageWidth < scaleWidth) {
				width = ((super.imageWidth << 16) + uStep - u - 1) / uStep;
			}
			if (super.imageHeight < scaleHeight) {
				height = ((super.imageHeight << 16) + vStep - v - 1) / vStep;
			}
			offset = x + y * dstStride;
			@Pc(152) int dstStep = dstStride - width;
			if (y + height > super.toolkit.clipY2) {
				height -= y + height - super.toolkit.clipY2;
			}
			@Pc(180) int clip;
			if (y < super.toolkit.clipY1) {
				clip = super.toolkit.clipY1 - y;
				height -= clip;
				offset += clip * dstStride;
				v += vStep * clip;
			}
			if (x + width > super.toolkit.clipX2) {
				clip = x + width - super.toolkit.clipX2;
				width -= clip;
				dstStep += clip;
			}
			if (x < super.toolkit.clipX1) {
				clip = super.toolkit.clipX1 - x;
				width -= clip;
				offset += clip;
				u += uStep * clip;
				dstStep += clip;
			}
			@Pc(252) int[] raster = super.toolkit.surfaceRaster;
			@Pc(259) int local259;
			@Pc(262) int local262;
			@Pc(270) int local270;
			@Pc(273) int local273;
			@Pc(335) int local335;
			@Pc(343) int local343;
			@Pc(346) int local346;
			@Pc(361) int local361;
			@Pc(377) int local377;
			@Pc(385) int local385;
			@Pc(544) int lerpColour;
			@Pc(446) byte paletteIndex;
			@Pc(369) int local369;
			if (mode != 0) {
				@Pc(1196) byte local1196;
				@Pc(872) int local872;
				@Pc(880) int local880;
				@Pc(888) int local888;
				@Pc(900) int local900;
				if (mode == 1) {
					if (op == 1) {
						int uStart = u;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
								if (paletteIndex == 0) {
									offset++;
								} else {
									raster[offset++] = this.spritePalette[paletteIndex & 0xFF];
								}
								u += uStep;
							}
							v += vStep;
							u = uStart;
							offset += dstStep;
						}
					} else {
						@Pc(735) byte local735;
						if (op == 0) {
							int uStart = u;
							if ((colour & 0xFFFFFF) == 16777215) {
								int alpha = colour >>> 24;
								int invAlpha = 256 - alpha;
								for (int row = -height; row < 0; row++) {
									int srcRowOffset = (v >> 16) * super.imageWidth;
									for (int column = -width; column < 0; column++) {
										byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
										if (colourIndex == 0) {
											offset++;
										} else {
											int srcColour = this.spritePalette[colourIndex & 0xFF];
											int dstColour = raster[offset];
											raster[offset++] = ((srcColour & 0xFF00FF) * alpha + (dstColour & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (dstColour & 0xFF00) * invAlpha & 0xFF0000) >> 8;
										}
										u += uStep;
									}
									v += vStep;
									u = uStart;
									offset += dstStep;
								}
							} else {
								int red = colour >> 16 & 0xFF;
								int green = colour >> 8 & 0xFF;
								int blue = colour & 0xFF;
								int alpha = colour >>> 24;
								int invAlpha = 256 - alpha;
								for (int row = -height; row < 0; row++) {
									int srcRowOffset = (v >> 16) * super.imageWidth;
									for (int column = -width; column < 0; column++) {
										@Pc(852) byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
										if (colourIndex == 0) {
											offset++;
										} else {
											int srcColour = this.spritePalette[colourIndex & 0xFF];
											if (alpha == 255) {
												int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
												int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
												int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
												raster[offset++] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
											} else {
												int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
												int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
												int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
												srcColour = (scaledRed | scaledGreen | scaledBlue) >>> 8;
												int dstColour = raster[offset];
												raster[offset++] = ((srcColour & 0xFF00FF) * alpha + (dstColour & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (dstColour & 0xFF00) * invAlpha & 0xFF0000) >> 8;
											}
										}
										u += uStep;
									}
									v += vStep;
									u = uStart;
									offset += dstStep;
								}
							}
						} else if (op == 3) {
							int uStart = u;
							int alpha = colour >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								int srcRowOffset = (v >> 16) * super.imageWidth;
								for (int column = -width; column < 0; column++) {
									byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
									int srcColour = colourIndex > 0 ? this.spritePalette[colourIndex] : 0;
									int sum = srcColour + colour;
									int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
									int pixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
									pixel = sum - pixel | pixel - (pixel >>> 8);
									if (srcColour == 0 && alpha != 255) {
										srcColour = pixel;
										pixel = raster[offset];
										pixel = ((srcColour & 0xFF00FF) * alpha + (pixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcColour & 0xFF00) * alpha + (pixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									}
									raster[offset++] = pixel;
									u += uStep;
								}
								v += vStep;
								u = uStart;
								offset += dstStep;
							}
						} else if (op == 2) {
							int alpha = colour >>> 24;
							int invAlpha = 256 - alpha;
							int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
							int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
							lerpColour = (redBlue | green) >>> 8;
							int uStart = u;
							for (int row = -height; row < 0; row++) {
								int srcRowOffset = (v >> 16) * super.imageWidth;
								for (int column = -width; column < 0; column++) {
									byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
									if (colourIndex == 0) {
										offset++;
									} else {
										int srcColour = this.spritePalette[colourIndex & 0xFF];
										redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
										green = (srcColour & 0xFF00) * alpha & 0xFF0000;
										raster[offset++] = ((redBlue | green) >>> 8) + lerpColour;
									}
									u += uStep;
								}
								v += vStep;
								u = uStart;
								offset += dstStep;
							}
						} else {
							throw new IllegalArgumentException();
						}
					}
				} else if (mode != 2) {
					throw new IllegalArgumentException();
				} else if (op == 1) {
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (paletteIndex == 0) {
								offset++;
							} else {
								int srcColour = this.spritePalette[paletteIndex & 0xFF];
								int dstColour = raster[offset];
								int sum = srcColour + dstColour;
								int redBlueSum = (srcColour & 0xFF00FF) + (dstColour & 0xFF00FF);
								dstColour = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset++] = sum - dstColour | dstColour - (dstColour >>> 8);
							}
							u += uStep;
						}
						v += vStep;
						u = uStart;
						offset += dstStep;
					}
				} else if (op == 0) {
					int uStart = u;
					int red = colour >> 16 & 0xFF;
					int green = colour >> 8 & 0xFF;
					int blue = colour & 0xFF;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							@Pc(1406) byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (colourIndex == 0) {
								offset++;
							} else {
								int srcColour = this.spritePalette[colourIndex & 0xFF];
								int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
								int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
								int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
								srcColour = (scaledRed | scaledGreen | scaledBlue) >>> 8;
								int dstColour = raster[offset];
								int sum = srcColour + dstColour;
								int redBlueSum = (srcColour & 0xFF00FF) + (dstColour & 0xFF00FF);
								dstColour = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset++] = sum - dstColour | dstColour - (dstColour >>> 8);
							}
							u += uStep;
						}
						v += vStep;
						u = uStart;
						offset += dstStep;
					}
				} else if (op == 3) {
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							int srcColour = paletteIndex > 0 ? this.spritePalette[paletteIndex] : 0;
							int sum = srcColour + colour;
							int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
							int carry = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							srcColour = sum - carry | carry - (carry >>> 8);
							@Pc(1583) int dst = raster[offset];
							sum = srcColour + dst;
							redBlueSum = (srcColour & 0xFF00FF) + (dst & 0xFF00FF);
							@Pc(1605) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							raster[offset++] = sum - overflow | overflow - (overflow >>> 8);
							u += uStep;
						}
						v += vStep;
						u = uStart;
						offset += dstStep;
					}
				} else if (op == 2) {
					int alpha = colour >>> 24;
					int invAlpha = 256 - alpha;
					int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
					int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
					lerpColour = (redBlue | green) >>> 8;
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							byte colourIndex = this.spritePixels[(u >> 16) + srcRowOffset];
							if (colourIndex == 0) {
								offset++;
							} else {
								int srcColour = this.spritePalette[colourIndex & 0xFF];
								redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
								green = (srcColour & 0xFF00) * alpha & 0xFF0000;
								@Pc(1731) int blended = ((redBlue | green) >>> 8) + lerpColour;
								int dstColour = raster[offset];
								int sum = blended + dstColour;
								int redBlueSum = (blended & 0xFF00FF) + (dstColour & 0xFF00FF);
								@Pc(1757) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset++] = sum - overflow | overflow - (overflow >>> 8);
							}
							u += uStep;
						}
						v += vStep;
						u = uStart;
						offset += dstStep;
					}
				} else {
					throw new IllegalArgumentException();
				}
			} else if (op == 1) {
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						raster[offset++] = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						u += uStep;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 0) {
				int red = colour >> 16 & 0xFF;
				int green = colour >> 8 & 0xFF;
				int blue = colour & 0xFF;
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						int srcColour = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						int scaledRed = (srcColour & 0xFF0000) * red & 0xFF000000;
						int scaledGreen = (srcColour & 0xFF00) * green & 0xFF0000;
						int scaledBlue = (srcColour & 0xFF) * blue & 0xFF00;
						raster[offset++] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
						u += uStep;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 3) {
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						paletteIndex = this.spritePixels[(u >> 16) + srcRowOffset];
						int srcColour = paletteIndex > 0 ? this.spritePalette[paletteIndex] : 0;
						int sum = srcColour + colour;
						int redBlueSum = (srcColour & 0xFF00FF) + (colour & 0xFF00FF);
						int carry = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						raster[offset++] = sum - carry | carry - (carry >>> 8);
						u += uStep;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else if (op == 2) {
				int alpha = colour >>> 24;
				int invAlpha = 256 - alpha;
				int redBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int green = (colour & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (redBlue | green) >>> 8;
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						int srcColour = this.spritePalette[this.spritePixels[(u >> 16) + srcRowOffset] & 0xFF];
						redBlue = (srcColour & 0xFF00FF) * alpha & 0xFF00FF00;
						green = (srcColour & 0xFF00) * alpha & 0xFF0000;
						raster[offset++] = ((redBlue | green) >>> 8) + lerpColour;
						u += uStep;
					}
					v += vStep;
					u = uStart;
					offset += dstStep;
				}
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "([I[III)V")
	@Override
	protected void blitParallelogramMasked(@OriginalArg(0) int[] lineOffsets, @OriginalArg(1) int[] lineWidths, @OriginalArg(2) int maskOffsetX, @OriginalArg(3) int maskOffsetY) {
		@Pc(3) int[] raster = super.toolkit.surfaceRaster;
		@Pc(186) int skip;
		@Pc(9) int row;
		@Pc(14) int maskIndex;
		@Pc(23) int dstIndex;
		@Pc(25) int u;
		@Pc(27) int v;
		@Pc(29) int column;
		@Pc(53) int maskStart;
		@Pc(58) int maskCount;
		@Pc(64) int maskSkip;
		@Pc(111) byte index;
		if (JavaSpriteBlitState.dudx == 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase;
						v = JavaSpriteBlitState.vBase;
						column = JavaSpriteBlitState.drawWidth;
						if (u >= 0 && v >= 0 && u - (super.imageWidth << 12) < 0 && v - (super.imageHeight << 12) < 0) {
							maskStart = lineOffsets[maskIndex] - maskOffsetX;
							maskCount = -lineWidths[maskIndex];
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
							if (maskSkip > 0) {
								dstIndex += maskSkip;
								column += maskSkip;
								u += JavaSpriteBlitState.dudx * maskSkip;
								v += JavaSpriteBlitState.dvdx * maskSkip;
							} else {
								maskCount -= maskSkip;
							}
							if (column < maskCount) {
								column = maskCount;
							}
							while (column < 0) {
								index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (index == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = this.spritePalette[index & 0xFF];
								}
								column++;
							}
						}
					}
					row++;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase;
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
						column = JavaSpriteBlitState.drawWidth;
						if (u >= 0 && u - (super.imageWidth << 12) < 0) {
							@Pc(179) int vOverrun;
							if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
								skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
								column += skip;
								v += JavaSpriteBlitState.dvdx * skip;
								dstIndex += skip;
							}
							@Pc(207) int vBound;
							if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
								column = vBound;
							}
							maskStart = lineOffsets[maskIndex] - maskOffsetX;
							maskCount = -lineWidths[maskIndex];
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
							if (maskSkip > 0) {
								dstIndex += maskSkip;
								column += maskSkip;
								u += JavaSpriteBlitState.dudx * maskSkip;
								v += JavaSpriteBlitState.dvdx * maskSkip;
							} else {
								maskCount -= maskSkip;
							}
							if (column < maskCount) {
								column = maskCount;
							}
							while (column < 0) {
								index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (index == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = this.spritePalette[index & 0xFF];
								}
								v += JavaSpriteBlitState.dvdx;
								column++;
							}
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase;
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
						column = JavaSpriteBlitState.drawWidth;
						if (u >= 0 && u - (super.imageWidth << 12) < 0) {
							if (v < 0) {
								skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
								column += skip;
								v += JavaSpriteBlitState.dvdx * skip;
								dstIndex += skip;
							}
							@Pc(379) int vEndBound;
							if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
								column = vEndBound;
							}
							maskStart = lineOffsets[maskIndex] - maskOffsetX;
							maskCount = -lineWidths[maskIndex];
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
							if (maskSkip > 0) {
								dstIndex += maskSkip;
								column += maskSkip;
								u += JavaSpriteBlitState.dudx * maskSkip;
								v += JavaSpriteBlitState.dvdx * maskSkip;
							} else {
								maskCount -= maskSkip;
							}
							if (column < maskCount) {
								column = maskCount;
							}
							while (column < 0) {
								index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (index == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = this.spritePalette[index & 0xFF];
								}
								v += JavaSpriteBlitState.dvdx;
								column++;
							}
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			}
		} else if (JavaSpriteBlitState.dudx < 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
						v = JavaSpriteBlitState.vBase;
						column = JavaSpriteBlitState.drawWidth;
						if (v >= 0 && v - (super.imageHeight << 12) < 0) {
							@Pc(525) int uOverrun;
							if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
								skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
								column += skip;
								u += JavaSpriteBlitState.dudx * skip;
								dstIndex += skip;
							}
							@Pc(553) int uBound;
							if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
								column = uBound;
							}
							maskStart = lineOffsets[maskIndex] - maskOffsetX;
							maskCount = -lineWidths[maskIndex];
							maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
							if (maskSkip > 0) {
								dstIndex += maskSkip;
								column += maskSkip;
								u += JavaSpriteBlitState.dudx * maskSkip;
								v += JavaSpriteBlitState.dvdx * maskSkip;
							} else {
								maskCount -= maskSkip;
							}
							if (column < maskCount) {
								column = maskCount;
							}
							while (column < 0) {
								index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (index == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = this.spritePalette[index & 0xFF];
								}
								u += JavaSpriteBlitState.dudx;
								column++;
							}
						}
					}
					row++;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
						column = JavaSpriteBlitState.drawWidth;
						@Pc(690) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(724) int uBound;
						if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uBound;
						}
						@Pc(736) int vOverrun;
						if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(770) int vBound;
						if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vBound;
						}
						maskStart = lineOffsets[maskIndex] - maskOffsetX;
						maskCount = -lineWidths[maskIndex];
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
						if (maskSkip > 0) {
							dstIndex += maskSkip;
							column += maskSkip;
							u += JavaSpriteBlitState.dudx * maskSkip;
							v += JavaSpriteBlitState.dvdx * maskSkip;
						} else {
							maskCount -= maskSkip;
						}
						if (column < maskCount) {
							column = maskCount;
						}
						while (column < 0) {
							index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (index == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = this.spritePalette[index & 0xFF];
							}
							u += JavaSpriteBlitState.dudx;
							v += JavaSpriteBlitState.dvdx;
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					maskIndex = row + maskOffsetY;
					if (maskIndex >= 0) {
						if (maskIndex >= lineOffsets.length) {
							return;
						}
						dstIndex = JavaSpriteBlitState.pixelOffset;
						u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
						v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
						column = JavaSpriteBlitState.drawWidth;
						@Pc(913) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(947) int uBound;
						if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uBound;
						}
						if (v < 0) {
							skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(995) int vEndBound;
						if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vEndBound;
						}
						maskStart = lineOffsets[maskIndex] - maskOffsetX;
						maskCount = -lineWidths[maskIndex];
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
						if (maskSkip > 0) {
							dstIndex += maskSkip;
							column += maskSkip;
							u += JavaSpriteBlitState.dudx * maskSkip;
							v += JavaSpriteBlitState.dvdx * maskSkip;
						} else {
							maskCount -= maskSkip;
						}
						if (column < maskCount) {
							column = maskCount;
						}
						while (column < 0) {
							index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (index == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = this.spritePalette[index & 0xFF];
							}
							u += JavaSpriteBlitState.dudx;
							v += JavaSpriteBlitState.dvdx;
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			}
		} else if (JavaSpriteBlitState.dvdx == 0) {
			row = JavaSpriteBlitState.drawHeight;
			while (row < 0) {
				maskIndex = row + maskOffsetY;
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets.length) {
						return;
					}
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase;
					column = JavaSpriteBlitState.drawWidth;
					if (v >= 0 && v - (super.imageHeight << 12) < 0) {
						if (u < 0) {
							skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							dstIndex += skip;
						}
						@Pc(1177) int uEndBound;
						if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uEndBound;
						}
						maskStart = lineOffsets[maskIndex] - maskOffsetX;
						maskCount = -lineWidths[maskIndex];
						maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
						if (maskSkip > 0) {
							dstIndex += maskSkip;
							column += maskSkip;
							u += JavaSpriteBlitState.dudx * maskSkip;
							v += JavaSpriteBlitState.dvdx * maskSkip;
						} else {
							maskCount -= maskSkip;
						}
						if (column < maskCount) {
							column = maskCount;
						}
						while (column < 0) {
							index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (index == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = this.spritePalette[index & 0xFF];
							}
							u += JavaSpriteBlitState.dudx;
							column++;
						}
					}
				}
				row++;
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		} else if (JavaSpriteBlitState.dvdx < 0) {
			row = JavaSpriteBlitState.drawHeight;
			while (row < 0) {
				maskIndex = row + maskOffsetY;
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets.length) {
						return;
					}
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(1354) int uEndBound;
					if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uEndBound;
					}
					@Pc(1366) int vOverrun;
					if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(1400) int vBound;
					if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vBound;
					}
					maskStart = lineOffsets[maskIndex] - maskOffsetX;
					maskCount = -lineWidths[maskIndex];
					maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
					if (maskSkip > 0) {
						dstIndex += maskSkip;
						column += maskSkip;
						u += JavaSpriteBlitState.dudx * maskSkip;
						v += JavaSpriteBlitState.dvdx * maskSkip;
					} else {
						maskCount -= maskSkip;
					}
					if (column < maskCount) {
						column = maskCount;
					}
					while (column < 0) {
						index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
						if (index == 0) {
							dstIndex++;
						} else {
							raster[dstIndex++] = this.spritePalette[index & 0xFF];
						}
						u += JavaSpriteBlitState.dudx;
						v += JavaSpriteBlitState.dvdx;
						column++;
					}
				}
				row++;
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		} else {
			row = JavaSpriteBlitState.drawHeight;
			while (row < 0) {
				maskIndex = row + maskOffsetY;
				if (maskIndex >= 0) {
					if (maskIndex >= lineOffsets.length) {
						return;
					}
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(1579) int uEndBound;
					if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uEndBound;
					}
					if (v < 0) {
						skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(1627) int vEndBound;
					if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vEndBound;
					}
					maskStart = lineOffsets[maskIndex] - maskOffsetX;
					maskCount = -lineWidths[maskIndex];
					maskSkip = maskStart + JavaSpriteBlitState.pixelOffset - dstIndex;
					if (maskSkip > 0) {
						dstIndex += maskSkip;
						column += maskSkip;
						u += JavaSpriteBlitState.dudx * maskSkip;
						v += JavaSpriteBlitState.dvdx * maskSkip;
					} else {
						maskCount -= maskSkip;
					}
					if (column < maskCount) {
						column = maskCount;
					}
					while (column < 0) {
						index = this.spritePixels[(v >> 12) * super.imageWidth + (u >> 12)];
						if (index == 0) {
							dstIndex++;
						} else {
							raster[dstIndex++] = this.spritePalette[index & 0xFF];
						}
						u += JavaSpriteBlitState.dudx;
						v += JavaSpriteBlitState.dvdx;
						column++;
					}
				}
				row++;
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		}
	}

	@OriginalMember(owner = "client!faa", name = "b", descriptor = "(II)V")
	@Override
	protected void blitParallelogram(@OriginalArg(0) int op) {
		@Pc(3) int[] raster = super.toolkit.surfaceRaster;
		@Pc(935) int skip;
		@Pc(9) int row;
		@Pc(12) int dstIndex;
		@Pc(14) int u;
		@Pc(16) int v;
		@Pc(18) int column;
		@Pc(48) int texel;
		@Pc(51) int dst;
		@Pc(227) byte index;
		@Pc(255) int src;
		@Pc(264) int local264;
		@Pc(268) int local268;
		@Pc(272) int local272;
		@Pc(344) int local344;
		if (JavaSpriteBlitState.dudx == 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase;
					v = JavaSpriteBlitState.vBase;
					column = JavaSpriteBlitState.drawWidth;
					if (u >= 0 && v >= 0 && u - (super.imageWidth << 12) < 0 && v - (super.imageHeight << 12) < 0) {
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								index = this.spritePixels[texel];
								if (index != 0) {
									raster[dst] = this.spritePalette[index & 0xFF];
								}
							} else if (op == 0) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redPart | greenPart | bluePart) >>> 8;
									} else {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redPart | greenPart | bluePart) >>> 8;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								index = this.spritePixels[texel];
								src = index > 0 ? this.spritePalette[index] : 0;
								int tint = JavaSpriteBlitState.colour;
								int sum = src + tint;
								int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow;
									overflow = raster[dst];
									overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = overflow;
							} else if (op == 2) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
								}
							} else {
								throw new IllegalArgumentException();
							}
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					if (u >= 0 && u - (super.imageWidth << 12) < 0) {
						@Pc(928) int vOverrun;
						if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
							column += skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(956) int vBound;
						if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								index = this.spritePixels[texel];
								if (index != 0) {
									raster[dst] = this.spritePalette[index & 0xFF];
								}
							} else if (op == 0) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redPart | greenPart | bluePart) >>> 8;
									} else {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redPart | greenPart | bluePart) >>> 8;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								index = this.spritePixels[texel];
								src = index > 0 ? this.spritePalette[index] : 0;
								int tint = JavaSpriteBlitState.colour;
								int sum = src + tint;
								int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow;
									overflow = raster[dst];
									overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = overflow;
							} else if (op == 2) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
								}
							} else {
								throw new IllegalArgumentException();
							}
							v += JavaSpriteBlitState.dvdx;
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					if (u >= 0 && u - (super.imageWidth << 12) < 0) {
						if (v < 0) {
							skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
							column += skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(1888) int vEndBound;
						if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vEndBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								index = this.spritePixels[texel];
								if (index != 0) {
									raster[dst] = this.spritePalette[index & 0xFF];
								}
							} else if (op == 0) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redPart | greenPart | bluePart) >>> 8;
									} else {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redPart | greenPart | bluePart) >>> 8;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								index = this.spritePixels[texel];
								src = index > 0 ? this.spritePalette[index] : 0;
								int tint = JavaSpriteBlitState.colour;
								int sum = src + tint;
								int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow;
									overflow = raster[dst];
									overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = overflow;
							} else if (op == 2) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
								}
							} else {
								throw new IllegalArgumentException();
							}
							v += JavaSpriteBlitState.dvdx;
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			}
		} else if (JavaSpriteBlitState.dudx < 0) {
			if (JavaSpriteBlitState.dvdx == 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase;
					column = JavaSpriteBlitState.drawWidth;
					if (v >= 0 && v - (super.imageHeight << 12) < 0) {
						@Pc(2794) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							dstIndex += skip;
						}
						@Pc(2822) int uBound;
						if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								index = this.spritePixels[texel];
								if (index != 0) {
									raster[dst] = this.spritePalette[index & 0xFF];
								}
							} else if (op == 0) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redPart | greenPart | bluePart) >>> 8;
									} else {
										int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redPart | greenPart | bluePart) >>> 8;
										int background = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								index = this.spritePixels[texel];
								src = index > 0 ? this.spritePalette[index] : 0;
								int tint = JavaSpriteBlitState.colour;
								int sum = src + tint;
								int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = overflow;
									overflow = raster[dst];
									overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = overflow;
							} else if (op == 2) {
								index = this.spritePixels[texel];
								if (index != 0) {
									src = this.spritePalette[index & 0xFF];
									int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
								}
							} else {
								throw new IllegalArgumentException();
							}
							u += JavaSpriteBlitState.dudx;
							column++;
						}
					}
					row++;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else if (JavaSpriteBlitState.dvdx < 0) {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					@Pc(3719) int uOverrun;
					if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(3753) int uBound;
					if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uBound;
					}
					@Pc(3765) int vOverrun;
					if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(3799) int vBound;
					if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							index = this.spritePixels[texel];
							if (index != 0) {
								raster[dst] = this.spritePalette[index & 0xFF];
							}
						} else if (op == 0) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redPart | greenPart | bluePart) >>> 8;
								} else {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redPart | greenPart | bluePart) >>> 8;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							index = this.spritePixels[texel];
							src = index > 0 ? this.spritePalette[index] : 0;
							int tint = JavaSpriteBlitState.colour;
							int sum = src + tint;
							int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							overflow = sum - overflow | overflow - (overflow >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow;
								overflow = raster[dst];
								overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = overflow;
						} else if (op == 2) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
							}
						} else {
							throw new IllegalArgumentException();
						}
						u += JavaSpriteBlitState.dudx;
						v += JavaSpriteBlitState.dvdx;
						column++;
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			} else {
				row = JavaSpriteBlitState.drawHeight;
				while (row < 0) {
					dstIndex = JavaSpriteBlitState.pixelOffset;
					u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
					v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
					column = JavaSpriteBlitState.drawWidth;
					@Pc(4702) int uOverrun;
					if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(4736) int uBound;
					if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uBound;
					}
					if (v < 0) {
						skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(4784) int vEndBound;
					if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vEndBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							index = this.spritePixels[texel];
							if (index != 0) {
								raster[dst] = this.spritePalette[index & 0xFF];
							}
						} else if (op == 0) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redPart | greenPart | bluePart) >>> 8;
								} else {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redPart | greenPart | bluePart) >>> 8;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							index = this.spritePixels[texel];
							src = index > 0 ? this.spritePalette[index] : 0;
							int tint = JavaSpriteBlitState.colour;
							int sum = src + tint;
							int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							overflow = sum - overflow | overflow - (overflow >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow;
								overflow = raster[dst];
								overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = overflow;
						} else if (op == 2) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
							}
						} else {
							throw new IllegalArgumentException();
						}
						u += JavaSpriteBlitState.dudx;
						v += JavaSpriteBlitState.dvdx;
						column++;
					}
					row++;
					JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
					JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
					JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
				}
			}
		} else if (JavaSpriteBlitState.dvdx == 0) {
			row = JavaSpriteBlitState.drawHeight;
			while (row < 0) {
				dstIndex = JavaSpriteBlitState.pixelOffset;
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
				v = JavaSpriteBlitState.vBase;
				column = JavaSpriteBlitState.drawWidth;
				if (v >= 0 && v - (super.imageHeight << 12) < 0) {
					if (u < 0) {
						skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						dstIndex += skip;
					}
					@Pc(5726) int uEndBound;
					if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uEndBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							index = this.spritePixels[texel];
							if (index != 0) {
								raster[dst] = this.spritePalette[index & 0xFF];
							}
						} else if (op == 0) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redPart | greenPart | bluePart) >>> 8;
								} else {
									int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redPart | greenPart | bluePart) >>> 8;
									int background = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							index = this.spritePixels[texel];
							src = index > 0 ? this.spritePalette[index] : 0;
							int tint = JavaSpriteBlitState.colour;
							int sum = src + tint;
							int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							overflow = sum - overflow | overflow - (overflow >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = overflow;
								overflow = raster[dst];
								overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = overflow;
						} else if (op == 2) {
							index = this.spritePixels[texel];
							if (index != 0) {
								src = this.spritePalette[index & 0xFF];
								int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
							}
						} else {
							throw new IllegalArgumentException();
						}
						u += JavaSpriteBlitState.dudx;
						column++;
					}
				}
				row++;
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		} else if (JavaSpriteBlitState.dvdx < 0) {
			for (row = JavaSpriteBlitState.drawHeight; row < 0; row++) {
				dstIndex = JavaSpriteBlitState.pixelOffset;
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
				v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
				column = JavaSpriteBlitState.drawWidth;
				if (u < 0) {
					skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
					column += skip;
					u += JavaSpriteBlitState.dudx * skip;
					v += JavaSpriteBlitState.dvdx * skip;
					dstIndex += skip;
				}
				@Pc(6663) int uEndBound;
				if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
					column = uEndBound;
				}
				@Pc(6675) int vOverrun;
				if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
					skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
					column += skip;
					u += JavaSpriteBlitState.dudx * skip;
					v += JavaSpriteBlitState.dvdx * skip;
					dstIndex += skip;
				}
				@Pc(6709) int vBound;
				if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
					column = vBound;
				}
				while (column < 0) {
					texel = (v >> 12) * super.imageWidth + (u >> 12);
					dst = dstIndex++;
					if (op == 1) {
						index = this.spritePixels[texel];
						if (index != 0) {
							raster[dst] = this.spritePalette[index & 0xFF];
						}
					} else if (op == 0) {
						index = this.spritePixels[texel];
						if (index != 0) {
							src = this.spritePalette[index & 0xFF];
							if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
								int alpha = JavaSpriteBlitState.colour >>> 24;
								int invAlpha = 256 - alpha;
								int background = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
							} else if (JavaSpriteBlitState.alpha == 255) {
								int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								raster[dst] = (redPart | greenPart | bluePart) >>> 8;
							} else {
								int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								src = (redPart | greenPart | bluePart) >>> 8;
								int background = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
						}
					} else if (op == 3) {
						index = this.spritePixels[texel];
						src = index > 0 ? this.spritePalette[index] : 0;
						int tint = JavaSpriteBlitState.colour;
						int sum = src + tint;
						int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
						int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						overflow = sum - overflow | overflow - (overflow >>> 8);
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = overflow;
							overflow = raster[dst];
							overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
						}
						raster[dst] = overflow;
					} else if (op == 2) {
						index = this.spritePixels[texel];
						if (index != 0) {
							src = this.spritePalette[index & 0xFF];
							int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
							int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
							raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
						}
					} else {
						throw new IllegalArgumentException();
					}
					u += JavaSpriteBlitState.dudx;
					v += JavaSpriteBlitState.dvdx;
					column++;
				}
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		} else {
			for (row = JavaSpriteBlitState.drawHeight; row < 0; row++) {
				dstIndex = JavaSpriteBlitState.pixelOffset;
				u = JavaSpriteBlitState.uBase + JavaSpriteBlitState.uOffsetX;
				v = JavaSpriteBlitState.vBase + JavaSpriteBlitState.vOffsetX;
				column = JavaSpriteBlitState.drawWidth;
				if (u < 0) {
					skip = (JavaSpriteBlitState.dudx - u - 1) / JavaSpriteBlitState.dudx;
					column += skip;
					u += JavaSpriteBlitState.dudx * skip;
					v += JavaSpriteBlitState.dvdx * skip;
					dstIndex += skip;
				}
				@Pc(7648) int uEndBound;
				if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
					column = uEndBound;
				}
				if (v < 0) {
					skip = (JavaSpriteBlitState.dvdx - v - 1) / JavaSpriteBlitState.dvdx;
					column += skip;
					u += JavaSpriteBlitState.dudx * skip;
					v += JavaSpriteBlitState.dvdx * skip;
					dstIndex += skip;
				}
				@Pc(7696) int vEndBound;
				if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
					column = vEndBound;
				}
				while (column < 0) {
					texel = (v >> 12) * super.imageWidth + (u >> 12);
					dst = dstIndex++;
					if (op == 1) {
						index = this.spritePixels[texel];
						if (index != 0) {
							raster[dst] = this.spritePalette[index & 0xFF];
						}
					} else if (op == 0) {
						index = this.spritePixels[texel];
						if (index != 0) {
							src = this.spritePalette[index & 0xFF];
							if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
								int alpha = JavaSpriteBlitState.colour >>> 24;
								int invAlpha = 256 - alpha;
								int background = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
							} else if (JavaSpriteBlitState.alpha == 255) {
								int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								raster[dst] = (redPart | greenPart | bluePart) >>> 8;
							} else {
								int redPart = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenPart = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int bluePart = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								src = (redPart | greenPart | bluePart) >>> 8;
								int background = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (background & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (background & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
						}
					} else if (op == 3) {
						index = this.spritePixels[texel];
						src = index > 0 ? this.spritePalette[index] : 0;
						int tint = JavaSpriteBlitState.colour;
						int sum = src + tint;
						int redBlueSum = (src & 0xFF00FF) + (tint & 0xFF00FF);
						int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						overflow = sum - overflow | overflow - (overflow >>> 8);
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = overflow;
							overflow = raster[dst];
							overflow = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (overflow & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (overflow & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
						}
						raster[dst] = overflow;
					} else if (op == 2) {
						index = this.spritePixels[texel];
						if (index != 0) {
							src = this.spritePalette[index & 0xFF];
							int redBlue = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
							int green = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
							raster[dst++] = ((redBlue | green) >>> 8) + JavaSpriteBlitState.lerpColour;
						}
					} else {
						throw new IllegalArgumentException();
					}
					u += JavaSpriteBlitState.dudx;
					v += JavaSpriteBlitState.dvdx;
					column++;
				}
				JavaSpriteBlitState.uBase += JavaSpriteBlitState.dudy;
				JavaSpriteBlitState.vBase += JavaSpriteBlitState.dvdy;
				JavaSpriteBlitState.pixelOffset += JavaSpriteBlitState.viewportStride;
			}
		}
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IILclient!aa;II)V")
	@Override
	public void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface surface, @OriginalArg(3) int maskX, @OriginalArg(4) int maskY) {
		if (super.toolkit.stopped()) {
			throw new IllegalStateException();
		}
		x += super.leftMargin;
		y += super.topMargin;
		@Pc(19) int srcIndex = 0;
		@Pc(23) int surfaceWidth = super.toolkit.surfaceWidth;
		@Pc(26) int imageWidth = super.imageWidth;
		@Pc(29) int imageHeight = super.imageHeight;
		@Pc(33) int dstStep = surfaceWidth - imageWidth;
		@Pc(35) int srcStep = 0;
		@Pc(41) int dstIndex = x + y * surfaceWidth;
		@Pc(52) int clip;
		if (y < super.toolkit.clipY1) {
			clip = super.toolkit.clipY1 - y;
			imageHeight -= clip;
			y = super.toolkit.clipY1;
			srcIndex = clip * imageWidth;
			dstIndex += clip * surfaceWidth;
		}
		if (y + imageHeight > super.toolkit.clipY2) {
			imageHeight -= y + imageHeight - super.toolkit.clipY2;
		}
		if (x < super.toolkit.clipX1) {
			clip = super.toolkit.clipX1 - x;
			imageWidth -= clip;
			x = super.toolkit.clipX1;
			srcIndex += clip;
			dstIndex += clip;
			srcStep = clip;
			dstStep += clip;
		}
		if (x + imageWidth > super.toolkit.clipX2) {
			clip = x + imageWidth - super.toolkit.clipX2;
			imageWidth -= clip;
			srcStep += clip;
			dstStep += clip;
		}
		if (imageWidth <= 0 || imageHeight <= 0) {
			return;
		}
		@Pc(159) JavaClippingMask javaClippingMask = (JavaClippingMask) surface;
		@Pc(162) int[] lineWidths = javaClippingMask.lineWidths;
		@Pc(165) int[] lineOffsets = javaClippingMask.lineOffsets;
		@Pc(169) int[] surfaceRaster = super.toolkit.surfaceRaster;
		@Pc(171) int startY = y;
		if (maskY > y) {
			startY = maskY;
			dstIndex += (maskY - y) * surfaceWidth;
			srcIndex += (maskY - y) * super.imageWidth;
		}
		@Pc(210) int endY = maskY + lineWidths.length < y + imageHeight ? maskY + lineWidths.length : y + imageHeight;
		for (@Pc(212) int i = startY; i < endY; i++) {
			@Pc(221) int spanStart = lineWidths[i - maskY] + maskX;
			@Pc(227) int spanWidth = lineOffsets[i - maskY];
			@Pc(229) int remaining = imageWidth;
			@Pc(236) int skip;
			if (x > spanStart) {
				skip = x - spanStart;
				if (skip >= spanWidth) {
					srcIndex += imageWidth + srcStep;
					dstIndex += imageWidth + dstStep;
					continue;
				}
				spanWidth -= skip;
			} else {
				skip = spanStart - x;
				if (skip >= imageWidth) {
					srcIndex += imageWidth + srcStep;
					dstIndex += imageWidth + dstStep;
					continue;
				}
				srcIndex += skip;
				remaining = imageWidth - skip;
				dstIndex += skip;
			}
			skip = 0;
			if (remaining < spanWidth) {
				spanWidth = remaining;
			} else {
				skip = remaining - spanWidth;
			}
			for (@Pc(304) int j = -spanWidth; j < 0; j++) {
				@Pc(311) byte paletteIndex = this.spritePixels[srcIndex++];
				if (paletteIndex == 0) {
					dstIndex++;
				} else {
					surfaceRaster[dstIndex++] = this.spritePalette[paletteIndex & 0xFF];
				}
			}
			srcIndex += skip + srcStep;
			dstIndex += skip + dstStep;
		}
	}

	@OriginalMember(owner = "client!faa", name = "a", descriptor = "(IIIII)V")
	@Override
	public void render(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int op, @OriginalArg(3) int color, @OriginalArg(4) int mode) {
		if (super.toolkit.stopped()) {
			throw new IllegalStateException();
		}
		@Pc(11) int dstStride = super.toolkit.surfaceWidth;
		x += super.leftMargin;
		y += super.topMargin;
		@Pc(27) int dstIndex = y * dstStride + x;
		@Pc(29) int srcIndex = 0;
		@Pc(32) int height = super.imageHeight;
		@Pc(35) int width = super.imageWidth;
		@Pc(39) int dstStep = dstStride - width;
		@Pc(41) int srcStep = 0;
		@Pc(52) int clip;
		if (y < super.toolkit.clipY1) {
			clip = super.toolkit.clipY1 - y;
			height -= clip;
			y = super.toolkit.clipY1;
			srcIndex = clip * width;
			dstIndex += clip * dstStride;
		}
		if (y + height > super.toolkit.clipY2) {
			height -= y + height - super.toolkit.clipY2;
		}
		if (x < super.toolkit.clipX1) {
			clip = super.toolkit.clipX1 - x;
			width -= clip;
			x = super.toolkit.clipX1;
			srcIndex += clip;
			dstIndex += clip;
			srcStep = clip;
			dstStep += clip;
		}
		if (x + width > super.toolkit.clipX2) {
			clip = x + width - super.toolkit.clipX2;
			width -= clip;
			srcStep += clip;
			dstStep += clip;
		}
		if (width <= 0 || height <= 0) {
			return;
		}
		@Pc(160) int[] raster = super.toolkit.surfaceRaster;
		@Pc(168) int local168;
		@Pc(175) int local175;
		@Pc(284) int local284;
		@Pc(287) int local287;
		@Pc(291) int local291;
		@Pc(303) int local303;
		@Pc(319) int local319;
		@Pc(327) int local327;
		@Pc(458) int lerpColour;
		@Pc(311) int local311;
		if (mode != 0) {
			@Pc(1054) byte local1054;
			@Pc(766) int local766;
			@Pc(774) int local774;
			@Pc(782) int local782;
			@Pc(794) int local794;
			@Pc(543) byte paletteIndex;
			if (mode == 1) {
				if (op == 1) {
					for (int row = -height; row < 0; row++) {
						for (int column = -width; column < 0; column++) {
							paletteIndex = this.spritePixels[srcIndex++];
							if (paletteIndex == 0) {
								dstIndex++;
							} else {
								int srcPixel = this.spritePalette[paletteIndex & 0xFF] | 0xFF000000;
								int dstPixel = raster[dstIndex];
								raster[dstIndex++] = ((srcPixel & 0xFF00FF) * 255 + (dstPixel & 0xFF00FF) * 0 >> 8 & 0xFFFF00FF) + (((srcPixel & 0xFF00FF00) >>> 8) * 255 + ((dstPixel & 0xFF00FF00) >>> 8) * 0 & 0xFF00FF00);
							}
						}
						dstIndex += dstStep;
						srcIndex += srcStep;
					}
				} else {
					@Pc(645) byte local645;
					if (op == 0) {
						if ((color & 0xFFFFFF) == 16777215) {
							int alpha = color >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								for (int column = -width; column < 0; column++) {
									byte paletteSlot = this.spritePixels[srcIndex++];
									if (paletteSlot == 0) {
										dstIndex++;
									} else {
										int srcPixel = this.spritePalette[paletteSlot & 0xFF];
										int dstPixel = raster[dstIndex];
										raster[dstIndex++] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									}
								}
								dstIndex += dstStep;
								srcIndex += srcStep;
							}
						} else {
							int red = color >> 16 & 0xFF;
							int green = color >> 8 & 0xFF;
							int blue = color & 0xFF;
							int alpha = color >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								for (int column = -width; column < 0; column++) {
									@Pc(746) byte colourIndex = this.spritePixels[srcIndex++];
									if (colourIndex == 0) {
										dstIndex++;
									} else {
										int srcPixel = this.spritePalette[colourIndex & 0xFF];
										if (alpha == 255) {
											int redTerm = (srcPixel & 0xFF0000) * red & 0xFF000000;
											int greenTerm = (srcPixel & 0xFF00) * green & 0xFF0000;
											int blueTerm = (srcPixel & 0xFF) * blue & 0xFF00;
											raster[dstIndex++] = (redTerm | greenTerm | blueTerm) >>> 8;
										} else {
											int redTerm = (srcPixel & 0xFF0000) * red & 0xFF000000;
											int greenTerm = (srcPixel & 0xFF00) * green & 0xFF0000;
											int blueTerm = (srcPixel & 0xFF) * blue & 0xFF00;
											srcPixel = (redTerm | greenTerm | blueTerm) >>> 8;
											int dstPixel = raster[dstIndex];
											raster[dstIndex++] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
										}
									}
								}
								dstIndex += dstStep;
								srcIndex += srcStep;
							}
						}
					} else if (op == 3) {
						int alpha = color >>> 24;
						int invAlpha = 256 - alpha;
						for (int row = -height; row < 0; row++) {
							for (int column = -width; column < 0; column++) {
								byte paletteSlot = this.spritePixels[srcIndex++];
								int srcPixel = paletteSlot > 0 ? this.spritePalette[paletteSlot] : 0;
								int sum = srcPixel + color;
								int lowSum = (srcPixel & 0xFF00FF) + (color & 0xFF00FF);
								int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
								blended = sum - blended | blended - (blended >>> 8);
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = blended;
									blended = raster[dstIndex];
									blended = ((srcPixel & 0xFF00FF) * alpha + (blended & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (blended & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								}
								raster[dstIndex++] = blended;
							}
							dstIndex += dstStep;
							srcIndex += srcStep;
						}
					} else if (op == 2) {
						int alpha = color >>> 24;
						int invAlpha = 256 - alpha;
						int redBlueTerm = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
						int greenTerm = (color & 0xFF00) * invAlpha & 0xFF0000;
						lerpColour = (redBlueTerm | greenTerm) >>> 8;
						for (int row = -height; row < 0; row++) {
							for (int column = -width; column < 0; column++) {
								byte paletteSlot = this.spritePixels[srcIndex++];
								if (paletteSlot == 0) {
									dstIndex++;
								} else {
									int srcPixel = this.spritePalette[paletteSlot & 0xFF];
									redBlueTerm = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
									greenTerm = (srcPixel & 0xFF00) * alpha & 0xFF0000;
									raster[dstIndex++] = ((redBlueTerm | greenTerm) >>> 8) + lerpColour;
								}
							}
							dstIndex += dstStep;
							srcIndex += srcStep;
						}
					} else {
						throw new IllegalArgumentException();
					}
				}
			} else if (mode != 2) {
				throw new IllegalArgumentException();
			} else if (op == 1) {
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						paletteIndex = this.spritePixels[srcIndex++];
						if (paletteIndex == 0) {
							dstIndex++;
						} else {
							int srcPixel = this.spritePalette[paletteIndex & 0xFF];
							int dstPixel = raster[dstIndex];
							int sum = srcPixel + dstPixel;
							int lowSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
							dstPixel = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							raster[dstIndex++] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
						}
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else if (op == 0) {
				int red = color >> 16 & 0xFF;
				int green = color >> 8 & 0xFF;
				int blue = color & 0xFF;
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						@Pc(1228) byte colourIndex = this.spritePixels[srcIndex++];
						if (colourIndex == 0) {
							dstIndex++;
						} else {
							int srcPixel = this.spritePalette[colourIndex & 0xFF];
							int redTerm = (srcPixel & 0xFF0000) * red & 0xFF000000;
							int greenTerm = (srcPixel & 0xFF00) * green & 0xFF0000;
							int blueTerm = (srcPixel & 0xFF) * blue & 0xFF00;
							srcPixel = (redTerm | greenTerm | blueTerm) >>> 8;
							int dstPixel = raster[dstIndex];
							int sum = srcPixel + dstPixel;
							int lowSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
							dstPixel = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							raster[dstIndex++] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
						}
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else if (op == 3) {
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						paletteIndex = this.spritePixels[srcIndex++];
						int srcPixel = paletteIndex > 0 ? this.spritePalette[paletteIndex] : 0;
						int sum = srcPixel + color;
						int lowSum = (srcPixel & 0xFF00FF) + (color & 0xFF00FF);
						int carry = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
						srcPixel = sum - carry | carry - (carry >>> 8);
						@Pc(1387) int dst = raster[dstIndex];
						sum = srcPixel + dst;
						lowSum = (srcPixel & 0xFF00FF) + (dst & 0xFF00FF);
						@Pc(1409) int overflow = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
						raster[dstIndex++] = sum - overflow | overflow - (overflow >>> 8);
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else if (op == 2) {
				int alpha = color >>> 24;
				int invAlpha = 256 - alpha;
				int redBlueTerm = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int greenTerm = (color & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (redBlueTerm | greenTerm) >>> 8;
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						byte paletteSlot = this.spritePixels[srcIndex++];
						if (paletteSlot == 0) {
							dstIndex++;
						} else {
							int srcPixel = this.spritePalette[paletteSlot & 0xFF];
							redBlueTerm = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
							greenTerm = (srcPixel & 0xFF00) * alpha & 0xFF0000;
							@Pc(1517) int blended = ((redBlueTerm | greenTerm) >>> 8) + lerpColour;
							int dstPixel = raster[dstIndex];
							int sum = blended + dstPixel;
							int lowSum = (blended & 0xFF00FF) + (dstPixel & 0xFF00FF);
							@Pc(1543) int overflow = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							raster[dstIndex++] = sum - overflow | overflow - (overflow >>> 8);
						}
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else {
				throw new IllegalArgumentException();
			}
		} else if (op == 1) {
			for (int row = -height; row < 0; row++) {
				int rowEnd = dstIndex + width - 3;
				while (dstIndex < rowEnd) {
					raster[dstIndex++] = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					raster[dstIndex++] = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					raster[dstIndex++] = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					raster[dstIndex++] = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
				}
				rowEnd += 3;
				while (dstIndex < rowEnd) {
					raster[dstIndex++] = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else if (op == 0) {
			int red = color >> 16 & 0xFF;
			int green = color >> 8 & 0xFF;
			int blue = color & 0xFF;
			for (int row = -height; row < 0; row++) {
				for (int column = -width; column < 0; column++) {
					int srcPixel = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					int redTerm = (srcPixel & 0xFF0000) * red & 0xFF000000;
					int greenTerm = (srcPixel & 0xFF00) * green & 0xFF0000;
					int blueTerm = (srcPixel & 0xFF) * blue & 0xFF00;
					raster[dstIndex++] = (redTerm | greenTerm | blueTerm) >>> 8;
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else if (op == 3) {
			for (int row = -height; row < 0; row++) {
				for (int column = -width; column < 0; column++) {
					int srcPixel = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					int sum = srcPixel + color;
					int lowSum = (srcPixel & 0xFF00FF) + (color & 0xFF00FF);
					int carry = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
					raster[dstIndex++] = sum - carry | carry - (carry >>> 8);
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else if (op == 2) {
			int alpha = color >>> 24;
			int invAlpha = 256 - alpha;
			int redBlueTerm = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
			int greenTerm = (color & 0xFF00) * invAlpha & 0xFF0000;
			lerpColour = (redBlueTerm | greenTerm) >>> 8;
			for (int row = -height; row < 0; row++) {
				for (int column = -width; column < 0; column++) {
					int srcPixel = this.spritePalette[this.spritePixels[srcIndex++] & 0xFF];
					redBlueTerm = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
					greenTerm = (srcPixel & 0xFF00) * alpha & 0xFF0000;
					raster[dstIndex++] = ((redBlueTerm | greenTerm) >>> 8) + lerpColour;
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
