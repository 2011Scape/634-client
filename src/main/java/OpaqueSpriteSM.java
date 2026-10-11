import com.jagex.game.runetek5.JavaSpriteBlitState;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!c")
public final class OpaqueSpriteSM extends SpriteSM {

	@OriginalMember(owner = "client!c", name = "H", descriptor = "[I")
	public final int[] pixels;

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;II)V")
	public OpaqueSpriteSM(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		super(javaToolkit, width, height);
		this.pixels = new int[width * height];
	}

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;[IIIIIZ)V")
	public OpaqueSpriteSM(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) int[] srcPixels, @OriginalArg(2) int srcOffset, @OriginalArg(3) int srcStride, @OriginalArg(4) int width, @OriginalArg(5) int height, @OriginalArg(6) boolean copyPixels) {
		super(javaToolkit, width, height);
		if (copyPixels) {
			this.pixels = new int[width * height];
		} else {
			this.pixels = srcPixels;
		}
		@Pc(21) int srcSkip = srcStride - super.imageWidth;
		@Pc(23) int dstIndex = 0;
		for (@Pc(25) int i = 0; i < height; i++) {
			for (@Pc(28) int j = 0; j < width; j++) {
				@Pc(34) int argb = srcPixels[srcOffset++];
				if (argb >>> 24 == 255) {
					this.pixels[dstIndex++] = (argb & 0xFFFFFF) == 0 ? -16777215 : argb;
				} else {
					this.pixels[dstIndex++] = 0;
				}
			}
			srcOffset += srcSkip;
		}
	}

	@OriginalMember(owner = "client!c", name = "<init>", descriptor = "(Lclient!ao;[III)V")
	public OpaqueSpriteSM(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) int[] srcPixels, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		super(javaToolkit, width, height);
		this.pixels = srcPixels;
	}

	@OriginalMember(owner = "client!c", name = "b", descriptor = "(II)V")
	@Override
	protected void blitParallelogram(@OriginalArg(0) int op) {
		@Pc(3) int[] raster = super.toolkit.surfaceRaster;
		@Pc(852) int skip;
		@Pc(9) int row;
		@Pc(12) int dstIndex;
		@Pc(14) int u;
		@Pc(16) int v;
		@Pc(18) int column;
		@Pc(48) int texel;
		@Pc(51) int dst;
		@Pc(209) int src;
		@Pc(234) int local234;
		@Pc(238) int local238;
		@Pc(242) int local242;
		@Pc(314) int local314;
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
								src = this.pixels[texel];
								if (src != 0) {
									raster[dst] = src;
								}
							} else if (op == 0) {
								src = this.pixels[texel];
								if (src != 0) {
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
									} else {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redTerm | greenTerm | blueTerm) >>> 8;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								src = this.pixels[texel];
								int tintColour = JavaSpriteBlitState.colour;
								int sum = src + tintColour;
								int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
								int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
								blended = sum - blended | blended - (blended >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended;
									blended = raster[dst];
									blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = blended;
							} else if (op == 2) {
								src = this.pixels[texel];
								if (src != 0) {
									int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
						@Pc(845) int vOverrun;
						if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
							column += skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(873) int vBound;
						if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								src = this.pixels[texel];
								if (src != 0) {
									raster[dst] = src;
								}
							} else if (op == 0) {
								src = this.pixels[texel];
								if (src != 0) {
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
									} else {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redTerm | greenTerm | blueTerm) >>> 8;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								src = this.pixels[texel];
								int tintColour = JavaSpriteBlitState.colour;
								int sum = src + tintColour;
								int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
								int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
								blended = sum - blended | blended - (blended >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended;
									blended = raster[dst];
									blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = blended;
							} else if (op == 2) {
								src = this.pixels[texel];
								if (src != 0) {
									int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
						@Pc(1722) int vEndBound;
						if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
							column = vEndBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								src = this.pixels[texel];
								if (src != 0) {
									raster[dst] = src;
								}
							} else if (op == 0) {
								src = this.pixels[texel];
								if (src != 0) {
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
									} else {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redTerm | greenTerm | blueTerm) >>> 8;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								src = this.pixels[texel];
								int tintColour = JavaSpriteBlitState.colour;
								int sum = src + tintColour;
								int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
								int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
								blended = sum - blended | blended - (blended >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended;
									blended = raster[dst];
									blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = blended;
							} else if (op == 2) {
								src = this.pixels[texel];
								if (src != 0) {
									int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
						@Pc(2545) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							dstIndex += skip;
						}
						@Pc(2573) int uBound;
						if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uBound;
						}
						while (column < 0) {
							texel = (v >> 12) * super.imageWidth + (u >> 12);
							dst = dstIndex++;
							if (op == 1) {
								src = this.pixels[texel];
								if (src != 0) {
									raster[dst] = src;
								}
							} else if (op == 0) {
								src = this.pixels[texel];
								if (src != 0) {
									if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
										int alpha = JavaSpriteBlitState.colour >>> 24;
										int invAlpha = 256 - alpha;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
									} else if (JavaSpriteBlitState.alpha == 255) {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
									} else {
										int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
										int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
										int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
										src = (redTerm | greenTerm | blueTerm) >>> 8;
										int dstPixel = raster[dst];
										raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
									}
								}
							} else if (op == 3) {
								src = this.pixels[texel];
								int tintColour = JavaSpriteBlitState.colour;
								int sum = src + tintColour;
								int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
								int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
								blended = sum - blended | blended - (blended >>> 8);
								if (src == 0 && JavaSpriteBlitState.alpha != 255) {
									src = blended;
									blended = raster[dst];
									blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
								raster[dst] = blended;
							} else if (op == 2) {
								src = this.pixels[texel];
								if (src != 0) {
									int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
									raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
					@Pc(3387) int uOverrun;
					if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(3421) int uBound;
					if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uBound;
					}
					@Pc(3433) int vOverrun;
					if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(3467) int vBound;
					if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							src = this.pixels[texel];
							if (src != 0) {
								raster[dst] = src;
							}
						} else if (op == 0) {
							src = this.pixels[texel];
							if (src != 0) {
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
								} else {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redTerm | greenTerm | blueTerm) >>> 8;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							src = this.pixels[texel];
							int tintColour = JavaSpriteBlitState.colour;
							int sum = src + tintColour;
							int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
							int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							blended = sum - blended | blended - (blended >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended;
								blended = raster[dst];
								blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = blended;
						} else if (op == 2) {
							src = this.pixels[texel];
							if (src != 0) {
								int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
					@Pc(4287) int uOverrun;
					if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(4321) int uBound;
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
					@Pc(4369) int vEndBound;
					if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
						column = vEndBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							src = this.pixels[texel];
							if (src != 0) {
								raster[dst] = src;
							}
						} else if (op == 0) {
							src = this.pixels[texel];
							if (src != 0) {
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
								} else {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redTerm | greenTerm | blueTerm) >>> 8;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							src = this.pixels[texel];
							int tintColour = JavaSpriteBlitState.colour;
							int sum = src + tintColour;
							int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
							int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							blended = sum - blended | blended - (blended >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended;
								blended = raster[dst];
								blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = blended;
						} else if (op == 2) {
							src = this.pixels[texel];
							if (src != 0) {
								int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
					@Pc(5228) int uEndBound;
					if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uEndBound;
					}
					while (column < 0) {
						texel = (v >> 12) * super.imageWidth + (u >> 12);
						dst = dstIndex++;
						if (op == 1) {
							src = this.pixels[texel];
							if (src != 0) {
								raster[dst] = src;
							}
						} else if (op == 0) {
							src = this.pixels[texel];
							if (src != 0) {
								if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
									int alpha = JavaSpriteBlitState.colour >>> 24;
									int invAlpha = 256 - alpha;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								} else if (JavaSpriteBlitState.alpha == 255) {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
								} else {
									int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
									int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
									int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
									src = (redTerm | greenTerm | blueTerm) >>> 8;
									int dstPixel = raster[dst];
									raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
								}
							}
						} else if (op == 3) {
							src = this.pixels[texel];
							int tintColour = JavaSpriteBlitState.colour;
							int sum = src + tintColour;
							int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
							int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
							blended = sum - blended | blended - (blended >>> 8);
							if (src == 0 && JavaSpriteBlitState.alpha != 255) {
								src = blended;
								blended = raster[dst];
								blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
							raster[dst] = blended;
						} else if (op == 2) {
							src = this.pixels[texel];
							if (src != 0) {
								int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
								raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
				@Pc(6082) int uEndBound;
				if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
					column = uEndBound;
				}
				@Pc(6094) int vOverrun;
				if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
					skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
					column += skip;
					u += JavaSpriteBlitState.dudx * skip;
					v += JavaSpriteBlitState.dvdx * skip;
					dstIndex += skip;
				}
				@Pc(6128) int vBound;
				if ((vBound = (v - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
					column = vBound;
				}
				while (column < 0) {
					texel = (v >> 12) * super.imageWidth + (u >> 12);
					dst = dstIndex++;
					if (op == 1) {
						src = this.pixels[texel];
						if (src != 0) {
							raster[dst] = src;
						}
					} else if (op == 0) {
						src = this.pixels[texel];
						if (src != 0) {
							if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
								int alpha = JavaSpriteBlitState.colour >>> 24;
								int invAlpha = 256 - alpha;
								int dstPixel = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
							} else if (JavaSpriteBlitState.alpha == 255) {
								int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
							} else {
								int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								src = (redTerm | greenTerm | blueTerm) >>> 8;
								int dstPixel = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
						}
					} else if (op == 3) {
						src = this.pixels[texel];
						int tintColour = JavaSpriteBlitState.colour;
						int sum = src + tintColour;
						int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
						int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
						blended = sum - blended | blended - (blended >>> 8);
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = blended;
							blended = raster[dst];
							blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
						}
						raster[dst] = blended;
					} else if (op == 2) {
						src = this.pixels[texel];
						if (src != 0) {
							int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
							int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
							raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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
				@Pc(6984) int uEndBound;
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
				@Pc(7032) int vEndBound;
				if ((vEndBound = (v + 1 - (super.imageHeight << 12) - JavaSpriteBlitState.dvdx) / JavaSpriteBlitState.dvdx) > column) {
					column = vEndBound;
				}
				while (column < 0) {
					texel = (v >> 12) * super.imageWidth + (u >> 12);
					dst = dstIndex++;
					if (op == 1) {
						src = this.pixels[texel];
						if (src != 0) {
							raster[dst] = src;
						}
					} else if (op == 0) {
						src = this.pixels[texel];
						if (src != 0) {
							if ((JavaSpriteBlitState.colour & 0xFFFFFF) == 16777215) {
								int alpha = JavaSpriteBlitState.colour >>> 24;
								int invAlpha = 256 - alpha;
								int dstPixel = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
							} else if (JavaSpriteBlitState.alpha == 255) {
								int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								raster[dst] = (redTerm | greenTerm | blueTerm) >>> 8;
							} else {
								int redTerm = (src & 0xFF0000) * JavaSpriteBlitState.red & 0xFF000000;
								int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.green & 0xFF0000;
								int blueTerm = (src & 0xFF) * JavaSpriteBlitState.blue & 0xFF00;
								src = (redTerm | greenTerm | blueTerm) >>> 8;
								int dstPixel = raster[dst];
								raster[dst] = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (dstPixel & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
							}
						}
					} else if (op == 3) {
						src = this.pixels[texel];
						int tintColour = JavaSpriteBlitState.colour;
						int sum = src + tintColour;
						int lowSum = (src & 0xFF00FF) + (tintColour & 0xFF00FF);
						int blended = (lowSum & 0x1000100) + (sum - lowSum & 0x10000);
						blended = sum - blended | blended - (blended >>> 8);
						if (src == 0 && JavaSpriteBlitState.alpha != 255) {
							src = blended;
							blended = raster[dst];
							blended = ((src & 0xFF00FF) * JavaSpriteBlitState.alpha + (blended & 0xFF00FF) * JavaSpriteBlitState.invAlpha & 0xFF00FF00) + ((src & 0xFF00) * JavaSpriteBlitState.alpha + (blended & 0xFF00) * JavaSpriteBlitState.invAlpha & 0xFF0000) >> 8;
						}
						raster[dst] = blended;
					} else if (op == 2) {
						src = this.pixels[texel];
						if (src != 0) {
							int redBlueTerm = (src & 0xFF00FF) * JavaSpriteBlitState.alpha & 0xFF00FF00;
							int greenTerm = (src & 0xFF00) * JavaSpriteBlitState.alpha & 0xFF0000;
							raster[dst++] = ((redBlueTerm | greenTerm) >>> 8) + JavaSpriteBlitState.lerpColour;
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

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void captureScreenRegion(@OriginalArg(0) int dstX, @OriginalArg(1) int dstY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int srcX, @OriginalArg(5) int srcY) {
		@Pc(3) int[] raster = super.toolkit.surfaceRaster;
		for (@Pc(5) int row = 0; row < height; row++) {
			@Pc(15) int dstIndex = (dstY + row) * super.imageWidth + dstX;
			@Pc(25) int srcIndex = (srcY + row) * super.toolkit.surfaceWidth + srcX;
			for (@Pc(27) int column = 0; column < width; column++) {
				this.pixels[dstIndex + column] = raster[srcIndex + column];
			}
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIIIII)V")
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
			@Pc(330) int local330;
			@Pc(338) int local338;
			@Pc(341) int local341;
			@Pc(351) int local351;
			@Pc(359) int local359;
			@Pc(367) int local367;
			@Pc(375) int local375;
			@Pc(525) int lerpColour;
			if (mode != 0) {
				@Pc(837) int local837;
				@Pc(845) int local845;
				@Pc(857) int local857;
				if (mode == 1) {
					if (op == 1) {
						int uStart = u;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								if (srcPixel == 0) {
									offset++;
								} else {
									raster[offset++] = srcPixel;
								}
								u += uStep;
							}
							v += vStep;
							u = uStart;
							offset += dstStep;
						}
					} else if (op == 0) {
						int uStart = u;
						if ((colour & 0xFFFFFF) == 16777215) {
							int alpha = colour >>> 24;
							int invAlpha = 256 - alpha;
							for (int row = -height; row < 0; row++) {
								int srcRowOffset = (v >> 16) * super.imageWidth;
								for (int column = -width; column < 0; column++) {
									int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
									if (srcPixel == 0) {
										offset++;
									} else {
										int dstPixel = raster[offset];
										raster[offset++] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
									int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
									if (srcPixel == 0) {
										offset++;
									} else if (alpha == 255) {
										int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
										int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
										int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
										raster[offset++] = (tintedRed | tintedGreen | tintedBlue) >>> 8;
									} else {
										int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
										int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
										int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
										srcPixel = (tintedRed | tintedGreen | tintedBlue) >>> 8;
										int dstPixel = raster[offset];
										raster[offset++] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								int sum = srcPixel + colour;
								int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = overflow;
									overflow = raster[offset];
									overflow = ((srcPixel & 0xFF00FF) * alpha + (overflow & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (overflow & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								}
								raster[offset++] = overflow;
								u += uStep;
							}
							v += vStep;
							u = uStart;
							offset += dstStep;
						}
					} else if (op == 2) {
						int alpha = colour >>> 24;
						int invAlpha = 256 - alpha;
						int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
						int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
						lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
						int uStart = u;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								if (srcPixel == 0) {
									offset++;
								} else {
									scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
									scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
									raster[offset++] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
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
				} else if (mode != 2) {
					throw new IllegalArgumentException();
				} else if (op == 1) {
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel == 0) {
								offset++;
							} else {
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								dstPixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset++] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
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
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel == 0) {
								offset++;
							} else {
								int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
								int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
								int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
								srcPixel = (tintedRed | tintedGreen | tintedBlue) >>> 8;
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								dstPixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset++] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
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
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							int sum = srcPixel + colour;
							int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							srcPixel = sum - overflow | overflow - (overflow >>> 8);
							overflow = raster[offset];
							sum = srcPixel + overflow;
							redBlueSum = (srcPixel & 0xFF00FF) + (overflow & 0xFF00FF);
							overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
					int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
					int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
					lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel == 0) {
								offset++;
							} else {
								scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
								scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
								srcPixel = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								@Pc(1668) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
						raster[offset++] = this.pixels[(u >> 16) + srcRowOffset];
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
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
						int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
						int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
						raster[offset++] = (tintedRed | tintedGreen | tintedBlue) >>> 8;
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
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						int sum = srcPixel + colour;
						int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
						int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
				int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
						scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
						raster[offset++] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
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

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(III)V")
	@Override
	public void copyAlpha(@OriginalArg(2) int channel) {
		throw new IllegalStateException("Can't capture alpha into a java_sprite_24");
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "([I[III)V")
	@Override
	protected void blitParallelogramMasked(@OriginalArg(0) int[] lineOffsets, @OriginalArg(1) int[] lineWidths, @OriginalArg(2) int maskOffsetX, @OriginalArg(3) int maskOffsetY) {
		@Pc(3) int[] raster = super.toolkit.surfaceRaster;
		@Pc(181) int skip;
		@Pc(9) int row;
		@Pc(14) int maskIndex;
		@Pc(23) int dstIndex;
		@Pc(25) int u;
		@Pc(27) int v;
		@Pc(29) int column;
		@Pc(53) int maskStart;
		@Pc(58) int maskCount;
		@Pc(64) int maskSkip;
		@Pc(111) int src;
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
								src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (src == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = src;
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
							@Pc(174) int vOverrun;
							if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
								skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
								column += skip;
								v += JavaSpriteBlitState.dvdx * skip;
								dstIndex += skip;
							}
							@Pc(202) int vBound;
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
								src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (src == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = src;
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
							@Pc(369) int vEndBound;
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
								src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (src == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = src;
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
							@Pc(510) int uOverrun;
							if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
								skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
								column += skip;
								u += JavaSpriteBlitState.dudx * skip;
								dstIndex += skip;
							}
							@Pc(538) int uBound;
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
								src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
								if (src == 0) {
									dstIndex++;
								} else {
									raster[dstIndex++] = src;
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
						@Pc(670) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(704) int uBound;
						if ((uBound = (u - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
							column = uBound;
						}
						@Pc(716) int vOverrun;
						if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(750) int vBound;
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
							src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
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
						@Pc(888) int uOverrun;
						if ((uOverrun = u - (super.imageWidth << 12)) >= 0) {
							skip = (JavaSpriteBlitState.dudx - uOverrun) / JavaSpriteBlitState.dudx;
							column += skip;
							u += JavaSpriteBlitState.dudx * skip;
							v += JavaSpriteBlitState.dvdx * skip;
							dstIndex += skip;
						}
						@Pc(922) int uBound;
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
						@Pc(970) int vEndBound;
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
							src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
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
						@Pc(1147) int uEndBound;
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
							src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
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
					@Pc(1319) int uEndBound;
					if ((uEndBound = (u + 1 - (super.imageWidth << 12) - JavaSpriteBlitState.dudx) / JavaSpriteBlitState.dudx) > column) {
						column = uEndBound;
					}
					@Pc(1331) int vOverrun;
					if ((vOverrun = v - (super.imageHeight << 12)) >= 0) {
						skip = (JavaSpriteBlitState.dvdx - vOverrun) / JavaSpriteBlitState.dvdx;
						column += skip;
						u += JavaSpriteBlitState.dudx * skip;
						v += JavaSpriteBlitState.dvdx * skip;
						dstIndex += skip;
					}
					@Pc(1365) int vBound;
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
						src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
						if (src == 0) {
							dstIndex++;
						} else {
							raster[dstIndex++] = src;
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
					@Pc(1539) int uEndBound;
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
					@Pc(1587) int vEndBound;
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
						src = this.pixels[(v >> 12) * super.imageWidth + (u >> 12)];
						if (src == 0) {
							dstIndex++;
						} else {
							raster[dstIndex++] = src;
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

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IILclient!aa;II)V")
	@Override
	public void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface clippingMask, @OriginalArg(3) int surfaceOffsetX, @OriginalArg(4) int surfaceOffsetY) {
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
		@Pc(159) JavaClippingMask javaClippingMask = (JavaClippingMask) clippingMask;
		@Pc(162) int[] lineWidths = javaClippingMask.lineWidths;
		@Pc(165) int[] lineOffsets = javaClippingMask.lineOffsets;
		@Pc(169) int[] surfaceRaster = super.toolkit.surfaceRaster;
		@Pc(171) int startY = y;
		if (surfaceOffsetY > y) {
			startY = surfaceOffsetY;
			dstIndex += (surfaceOffsetY - y) * surfaceWidth;
			srcIndex += (surfaceOffsetY - y) * super.imageWidth;
		}
		@Pc(210) int endY = surfaceOffsetY + lineWidths.length < y + imageHeight ? surfaceOffsetY + lineWidths.length : y + imageHeight;
		for (@Pc(212) int i = startY; i < endY; i++) {
			@Pc(221) int spanStart = lineWidths[i - surfaceOffsetY] + surfaceOffsetX;
			@Pc(227) int spanWidth = lineOffsets[i - surfaceOffsetY];
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
				@Pc(311) int src = this.pixels[srcIndex++];
				if (src == 0) {
					dstIndex++;
				} else {
					surfaceRaster[dstIndex++] = src;
				}
			}
			srcIndex += skip + srcStep;
			dstIndex += skip + dstStep;
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIII)V")
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
		@Pc(259) int local259;
		@Pc(262) int local262;
		@Pc(266) int local266;
		@Pc(273) int local273;
		@Pc(281) int local281;
		@Pc(289) int local289;
		@Pc(297) int local297;
		@Pc(423) int lerpColour;
		if (mode != 0) {
			@Pc(739) int local739;
			@Pc(747) int local747;
			@Pc(759) int local759;
			if (mode == 1) {
				if (op == 1) {
					for (int row = -height; row < 0; row++) {
						int rowEnd = dstIndex + width - 3;
						while (dstIndex < rowEnd) {
							int src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
							}
							src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
							}
							src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
							}
							src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
							}
						}
						rowEnd += 3;
						while (dstIndex < rowEnd) {
							int src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								raster[dstIndex++] = src;
							}
						}
						dstIndex += dstStep;
						srcIndex += srcStep;
					}
				} else if (op == 0) {
					if ((color & 0xFFFFFF) == 16777215) {
						int alpha = color >>> 24;
						int invAlpha = 256 - alpha;
						for (int row = -height; row < 0; row++) {
							for (int column = -width; column < 0; column++) {
								int src = this.pixels[srcIndex++];
								if (src == 0) {
									dstIndex++;
								} else {
									int dst = raster[dstIndex];
									raster[dstIndex++] = ((src & 0xFF00FF) * alpha + (dst & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dst & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
								int src = this.pixels[srcIndex++];
								if (src == 0) {
									dstIndex++;
								} else if (alpha == 255) {
									int scaledRed = (src & 0xFF0000) * red & 0xFF000000;
									int scaledGreen = (src & 0xFF00) * green & 0xFF0000;
									int scaledBlue = (src & 0xFF) * blue & 0xFF00;
									raster[dstIndex++] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
								} else {
									int scaledRed = (src & 0xFF0000) * red & 0xFF000000;
									int scaledGreen = (src & 0xFF00) * green & 0xFF0000;
									int scaledBlue = (src & 0xFF) * blue & 0xFF00;
									src = (scaledRed | scaledGreen | scaledBlue) >>> 8;
									int dst = raster[dstIndex];
									raster[dstIndex++] = ((src & 0xFF00FF) * alpha + (dst & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (dst & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
							int src = this.pixels[srcIndex++];
							int sum = src + color;
							int redBlueSum = (src & 0xFF00FF) + (color & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							overflow = sum - overflow | overflow - (overflow >>> 8);
							if (src == 0 && alpha != 255) {
								src = overflow;
								overflow = raster[dstIndex];
								overflow = ((src & 0xFF00FF) * alpha + (overflow & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((src & 0xFF00) * alpha + (overflow & 0xFF00) * invAlpha & 0xFF0000) >> 8;
							}
							raster[dstIndex++] = overflow;
						}
						dstIndex += dstStep;
						srcIndex += srcStep;
					}
				} else if (op == 2) {
					int alpha = color >>> 24;
					int invAlpha = 256 - alpha;
					int scaledRedBlue = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
					int scaledGreen = (color & 0xFF00) * invAlpha & 0xFF0000;
					lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
					for (int row = -height; row < 0; row++) {
						for (int column = -width; column < 0; column++) {
							int src = this.pixels[srcIndex++];
							if (src == 0) {
								dstIndex++;
							} else {
								scaledRedBlue = (src & 0xFF00FF) * alpha & 0xFF00FF00;
								scaledGreen = (src & 0xFF00) * alpha & 0xFF0000;
								raster[dstIndex++] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
							}
						}
						dstIndex += dstStep;
						srcIndex += srcStep;
					}
				} else {
					throw new IllegalArgumentException();
				}
			} else if (mode != 2) {
				throw new IllegalArgumentException();
			} else if (op == 1) {
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						int src = this.pixels[srcIndex++];
						if (src == 0) {
							dstIndex++;
						} else {
							int dst = raster[dstIndex];
							int sum = src + dst;
							int redBlueSum = (src & 0xFF00FF) + (dst & 0xFF00FF);
							dst = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							raster[dstIndex++] = sum - dst | dst - (dst >>> 8);
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
						int src = this.pixels[srcIndex++];
						if (src == 0) {
							dstIndex++;
						} else {
							int scaledRed = (src & 0xFF0000) * red & 0xFF000000;
							int scaledGreen = (src & 0xFF00) * green & 0xFF0000;
							int scaledBlue = (src & 0xFF) * blue & 0xFF00;
							src = (scaledRed | scaledGreen | scaledBlue) >>> 8;
							int dst = raster[dstIndex];
							int sum = src + dst;
							int redBlueSum = (src & 0xFF00FF) + (dst & 0xFF00FF);
							dst = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							raster[dstIndex++] = sum - dst | dst - (dst >>> 8);
						}
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else if (op == 3) {
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						int src = this.pixels[srcIndex++];
						int sum = src + color;
						int redBlueSum = (src & 0xFF00FF) + (color & 0xFF00FF);
						int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						src = sum - overflow | overflow - (overflow >>> 8);
						overflow = raster[dstIndex];
						sum = src + overflow;
						redBlueSum = (src & 0xFF00FF) + (overflow & 0xFF00FF);
						overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						raster[dstIndex++] = sum - overflow | overflow - (overflow >>> 8);
					}
					dstIndex += dstStep;
					srcIndex += srcStep;
				}
			} else if (op == 2) {
				int alpha = color >>> 24;
				int invAlpha = 256 - alpha;
				int scaledRedBlue = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int scaledGreen = (color & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
				for (int row = -height; row < 0; row++) {
					for (int column = -width; column < 0; column++) {
						int src = this.pixels[srcIndex++];
						if (src == 0) {
							dstIndex++;
						} else {
							scaledRedBlue = (src & 0xFF00FF) * alpha & 0xFF00FF00;
							scaledGreen = (src & 0xFF00) * alpha & 0xFF0000;
							src = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
							int dst = raster[dstIndex];
							int sum = src + dst;
							int redBlueSum = (src & 0xFF00FF) + (dst & 0xFF00FF);
							@Pc(1462) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
					raster[dstIndex++] = this.pixels[srcIndex++];
					raster[dstIndex++] = this.pixels[srcIndex++];
					raster[dstIndex++] = this.pixels[srcIndex++];
					raster[dstIndex++] = this.pixels[srcIndex++];
				}
				rowEnd += 3;
				while (dstIndex < rowEnd) {
					raster[dstIndex++] = this.pixels[srcIndex++];
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
					int src = this.pixels[srcIndex++];
					int scaledRed = (src & 0xFF0000) * red & 0xFF000000;
					int scaledGreen = (src & 0xFF00) * green & 0xFF0000;
					int scaledBlue = (src & 0xFF) * blue & 0xFF00;
					raster[dstIndex++] = (scaledRed | scaledGreen | scaledBlue) >>> 8;
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else if (op == 3) {
			for (int row = -height; row < 0; row++) {
				for (int column = -width; column < 0; column++) {
					int src = this.pixels[srcIndex++];
					int sum = src + color;
					int redBlueSum = (src & 0xFF00FF) + (color & 0xFF00FF);
					int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
					raster[dstIndex++] = sum - overflow | overflow - (overflow >>> 8);
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else if (op == 2) {
			int alpha = color >>> 24;
			int invAlpha = 256 - alpha;
			int scaledRedBlue = (color & 0xFF00FF) * invAlpha & 0xFF00FF00;
			int scaledGreen = (color & 0xFF00) * invAlpha & 0xFF0000;
			lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
			for (int row = -height; row < 0; row++) {
				for (int column = -width; column < 0; column++) {
					int src = this.pixels[srcIndex++];
					scaledRedBlue = (src & 0xFF00FF) * alpha & 0xFF00FF00;
					scaledGreen = (src & 0xFF00) * alpha & 0xFF0000;
					raster[dstIndex++] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
				}
				dstIndex += dstStep;
				srcIndex += srcStep;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(IIIIIIIII)V")
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
		@Pc(340) int local340;
		@Pc(348) int local348;
		@Pc(351) int local351;
		@Pc(368) int local368;
		@Pc(376) int local376;
		@Pc(384) int local384;
		@Pc(392) int local392;
		@Pc(559) int lerpColour;
		if (mode != 0) {
			@Pc(910) int local910;
			@Pc(918) int local918;
			@Pc(930) int local930;
			if (mode == 1) {
				if (op == 1) {
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							if ((float) z < depth[offset]) {
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								if (srcPixel != 0) {
									raster[offset] = srcPixel;
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
					if ((colour & 0xFFFFFF) == 16777215) {
						int alpha = colour >>> 24;
						int invAlpha = 256 - alpha;
						for (int row = -height; row < 0; row++) {
							int srcRowOffset = (v >> 16) * super.imageWidth;
							for (int column = -width; column < 0; column++) {
								if ((float) z < depth[offset]) {
									int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
									if (srcPixel != 0) {
										int dstPixel = raster[offset];
										raster[offset] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
									int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
									if (srcPixel != 0) {
										if (alpha == 255) {
											int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
											int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
											int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
											raster[offset] = (tintedRed | tintedGreen | tintedBlue) >>> 8;
											depth[offset] = z;
										} else {
											int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
											int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
											int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
											srcPixel = (tintedRed | tintedGreen | tintedBlue) >>> 8;
											int dstPixel = raster[offset];
											raster[offset] = ((srcPixel & 0xFF00FF) * alpha + (dstPixel & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (dstPixel & 0xFF00) * invAlpha & 0xFF0000) >> 8;
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
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								int sum = srcPixel + colour;
								int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
								int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								overflow = sum - overflow | overflow - (overflow >>> 8);
								if (srcPixel == 0 && alpha != 255) {
									srcPixel = overflow;
									overflow = raster[offset];
									overflow = ((srcPixel & 0xFF00FF) * alpha + (overflow & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((srcPixel & 0xFF00) * alpha + (overflow & 0xFF00) * invAlpha & 0xFF0000) >> 8;
								}
								raster[offset] = overflow;
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
					int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
					int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
					lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
					int uStart = u;
					for (int row = -height; row < 0; row++) {
						int srcRowOffset = (v >> 16) * super.imageWidth;
						for (int column = -width; column < 0; column++) {
							if ((float) z < depth[offset]) {
								int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
								if (srcPixel != 0) {
									scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
									scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
									raster[offset] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
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
			} else if (mode != 2) {
				throw new IllegalArgumentException();
			} else if (op == 1) {
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel != 0) {
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								dstPixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
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
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel != 0) {
								int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
								int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
								int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
								srcPixel = (tintedRed | tintedGreen | tintedBlue) >>> 8;
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								dstPixel = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								raster[offset] = sum - dstPixel | dstPixel - (dstPixel >>> 8);
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
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							int sum = srcPixel + colour;
							int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
							int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							srcPixel = sum - overflow | overflow - (overflow >>> 8);
							overflow = raster[offset];
							sum = srcPixel + overflow;
							redBlueSum = (srcPixel & 0xFF00FF) + (overflow & 0xFF00FF);
							overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
				int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
				int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
				lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
				int uStart = u;
				for (int row = -height; row < 0; row++) {
					int srcRowOffset = (v >> 16) * super.imageWidth;
					for (int column = -width; column < 0; column++) {
						if ((float) z < depth[offset]) {
							int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
							if (srcPixel != 0) {
								scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
								scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
								srcPixel = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
								int dstPixel = raster[offset];
								int sum = srcPixel + dstPixel;
								int redBlueSum = (srcPixel & 0xFF00FF) + (dstPixel & 0xFF00FF);
								@Pc(1809) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
						raster[offset] = this.pixels[(u >> 16) + srcRowOffset];
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
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						int tintedRed = (srcPixel & 0xFF0000) * red & 0xFF000000;
						int tintedGreen = (srcPixel & 0xFF00) * green & 0xFF0000;
						int tintedBlue = (srcPixel & 0xFF) * blue & 0xFF00;
						raster[offset] = (tintedRed | tintedGreen | tintedBlue) >>> 8;
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
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						int sum = srcPixel + colour;
						int redBlueSum = (srcPixel & 0xFF00FF) + (colour & 0xFF00FF);
						int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
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
			int scaledRedBlue = (colour & 0xFF00FF) * invAlpha & 0xFF00FF00;
			int scaledGreen = (colour & 0xFF00) * invAlpha & 0xFF0000;
			lerpColour = (scaledRedBlue | scaledGreen) >>> 8;
			int uStart = u;
			for (int row = -height; row < 0; row++) {
				int srcRowOffset = (v >> 16) * super.imageWidth;
				for (int column = -width; column < 0; column++) {
					if ((float) z < depth[offset]) {
						int srcPixel = this.pixels[(u >> 16) + srcRowOffset];
						scaledRedBlue = (srcPixel & 0xFF00FF) * alpha & 0xFF00FF00;
						scaledGreen = (srcPixel & 0xFF00) * alpha & 0xFF0000;
						raster[offset] = ((scaledRedBlue | scaledGreen) >>> 8) + lerpColour;
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
}
