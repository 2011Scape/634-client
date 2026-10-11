import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ColorUtil;

@OriginalClass("client!mp")
public final class Rasterizer {

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "I")
	public int minX;

	@OriginalMember(owner = "client!mp", name = "d", descriptor = "I")
	public int minY;

	@OriginalMember(owner = "client!mp", name = "h", descriptor = "I")
	public int height;

	@OriginalMember(owner = "client!mp", name = "o", descriptor = "I")
	public int width;

	@OriginalMember(owner = "client!mp", name = "F", descriptor = "I")
	private int fogColour;

	@OriginalMember(owner = "client!mp", name = "c", descriptor = "Z")
	public boolean halfBlend = false;

	@OriginalMember(owner = "client!mp", name = "j", descriptor = "I")
	public int alpha = 0;

	@OriginalMember(owner = "client!mp", name = "k", descriptor = "Z")
	private boolean wireframe = false;

	@OriginalMember(owner = "client!mp", name = "l", descriptor = "[I")
	public final int[] lineOffsets = new int[4096];

	@OriginalMember(owner = "client!mp", name = "e", descriptor = "Z")
	public boolean fastScanline = true;

	@OriginalMember(owner = "client!mp", name = "g", descriptor = "Z")
	public boolean clamp = false;

	@OriginalMember(owner = "client!mp", name = "p", descriptor = "Z")
	private final boolean depthDisabled = false;

	@OriginalMember(owner = "client!mp", name = "r", descriptor = "F")
	private float textureScale3 = 0.0F;

	@OriginalMember(owner = "client!mp", name = "u", descriptor = "[I")
	private int[] texels2 = null;

	@OriginalMember(owner = "client!mp", name = "w", descriptor = "I")
	private final int textureId3 = -1;

	@OriginalMember(owner = "client!mp", name = "t", descriptor = "F")
	private float textureScale2 = 0.0F;

	@OriginalMember(owner = "client!mp", name = "C", descriptor = "I")
	private int textureSize = 0;

	@OriginalMember(owner = "client!mp", name = "A", descriptor = "I")
	private int textureSize2 = 0;

	@OriginalMember(owner = "client!mp", name = "B", descriptor = "[I")
	private int[] texels3 = null;

	@OriginalMember(owner = "client!mp", name = "y", descriptor = "I")
	private final int textureId2 = -1;

	@OriginalMember(owner = "client!mp", name = "s", descriptor = "I")
	private int textureBlendMode = 0;

	@OriginalMember(owner = "client!mp", name = "q", descriptor = "F")
	private float textureScale = 0.0F;

	@OriginalMember(owner = "client!mp", name = "D", descriptor = "Z")
	private boolean textureRepeats = true;

	@OriginalMember(owner = "client!mp", name = "x", descriptor = "I")
	private int textureMask3 = 0;

	@OriginalMember(owner = "client!mp", name = "E", descriptor = "I")
	private int textureSize3 = 0;

	@OriginalMember(owner = "client!mp", name = "v", descriptor = "I")
	private int textureMask2 = 0;

	@OriginalMember(owner = "client!mp", name = "z", descriptor = "I")
	private int textureMask = 0;

	@OriginalMember(owner = "client!mp", name = "G", descriptor = "I")
	private final int textureId = -1;

	@OriginalMember(owner = "client!mp", name = "H", descriptor = "[I")
	private int[] texels = null;

	@OriginalMember(owner = "client!mp", name = "b", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!mp", name = "f", descriptor = "Lclient!pga;")
	private final JavaThreadResource threadResource;

	@OriginalMember(owner = "client!mp", name = "n", descriptor = "I")
	private final int surfaceWidth;

	@OriginalMember(owner = "client!mp", name = "i", descriptor = "[I")
	private final int[] raster;

	@OriginalMember(owner = "client!mp", name = "m", descriptor = "[F")
	private final float[] depthBuffer;

	@OriginalMember(owner = "client!mp", name = "<init>", descriptor = "(Lclient!ao;Lclient!pga;)V")
	public Rasterizer(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) JavaThreadResource javaThreadResource) {
		this.toolkit = javaToolkit;
		this.threadResource = javaThreadResource;
		this.surfaceWidth = this.toolkit.surfaceWidth;
		this.raster = this.toolkit.surfaceRaster;
		this.depthBuffer = this.toolkit.depthBuffer;
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[IIIIFFFFFFFFFFFFFFFF)V")
	private void drawTexturedSpanRgb(@OriginalArg(0) int[] dst, @OriginalArg(1) int[] texels, @OriginalArg(2) int rowOffset, @OriginalArg(3) int startX, @OriginalArg(4) int endX, @OriginalArg(5) float invZLeft, @OriginalArg(6) float invZRight, @OriginalArg(7) float uLeft, @OriginalArg(8) float uRight, @OriginalArg(9) float vLeft, @OriginalArg(10) float vRight, @OriginalArg(11) float fogLeft, @OriginalArg(12) float fogRight, @OriginalArg(13) float alphaLeft, @OriginalArg(14) float alphaRight, @OriginalArg(15) float redLeft, @OriginalArg(16) float redRight, @OriginalArg(17) float greenLeft, @OriginalArg(18) float greenRight, @OriginalArg(19) float blueLeft, @OriginalArg(20) float blueRight) {
		@Pc(3) int lengthX = endX - startX;
		@Pc(8) float scaleX = 1.0F / (float) lengthX;
		@Pc(14) float slopeInvZ = (invZRight - invZLeft) * scaleX;
		@Pc(20) float slopeU = (uRight - uLeft) * scaleX;
		@Pc(26) float slopeV = (vRight - vLeft) * scaleX;
		@Pc(32) float slopeFog = (fogRight - fogLeft) * scaleX;
		@Pc(38) float slopeAlpha = (alphaRight - alphaLeft) * scaleX;
		@Pc(44) float slopeRed = (redRight - redLeft) * scaleX;
		@Pc(50) float slopeGreen = (greenRight - greenLeft) * scaleX;
		@Pc(56) float slopeBlue = (blueRight - blueLeft) * scaleX;
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width;
			}
			if (startX < 0) {
				invZLeft -= slopeInvZ * (float) startX;
				uLeft -= slopeU * (float) startX;
				vLeft -= slopeV * (float) startX;
				fogLeft -= slopeFog * (float) startX;
				alphaLeft -= slopeAlpha * (float) startX;
				redLeft -= slopeRed * (float) startX;
				greenLeft -= slopeGreen * (float) startX;
				blueLeft -= slopeBlue * (float) startX;
				startX = 0;
			}
		}
		if (startX >= endX) {
			return;
		}
		lengthX = endX - startX;
		@Pc(138) int pixelIndex = rowOffset + startX;
		while (lengthX-- > 0) {
			@Pc(143) float depth = 1.0F / invZLeft;
			if (depth < this.depthBuffer[pixelIndex]) {
				@Pc(159) int texU = (int) (uLeft * depth * (float) this.textureSize);
				if (this.textureRepeats) {
					texU &= this.textureMask;
				} else if (texU < 0) {
					texU = 0;
				} else if (texU > this.textureMask) {
					texU = this.textureMask;
				}
				@Pc(189) int texV = (int) (vLeft * depth * (float) this.textureSize);
				if (this.textureRepeats) {
					texV &= this.textureMask;
				} else if (texV < 0) {
					texV = 0;
				} else if (texV > this.textureMask) {
					texV = this.textureMask;
				}
				@Pc(220) int texel = this.texels[texV * this.textureSize + texU];
				@Pc(232) int texelAlpha;
				if (this.textureBlendMode == 2) {
					texelAlpha = texel >> 24 & 0xFF;
				} else if (this.textureBlendMode == 1) {
					texelAlpha = texel == 0 ? 0 : 255;
				} else {
					texelAlpha = (int) alphaLeft;
				}
				if (texelAlpha != 0) {
					@Pc(290) int colour;
					@Pc(299) int local299;
					@Pc(321) int local321;
					if (texelAlpha == 255) {
						colour = ((int) (redLeft * (float) (texel >> 16 & 0xFF)) & 0xFF00 | 0xFF0000) << 8 | (int) (greenLeft * (float) (texel >> 8 & 0xFF)) & 0xFF00 | (int) (blueLeft * (float) (texel & 0xFF)) >> 8;
						if (fogLeft != 0.0F) {
							int fogInverse = (int) (255.0F - fogLeft);
							int fogTerm = ((this.fogColour & 0xFF00FF) * (int) fogLeft & 0xFF00FF00 | (this.fogColour & 0xFF00) * (int) fogLeft & 0xFF0000) >>> 8;
							colour = (((colour & 0xFF00FF) * fogInverse & 0xFF00FF00 | (colour & 0xFF00) * fogInverse & 0xFF0000) >>> 8) + fogTerm;
						}
						dst[pixelIndex] = colour;
						this.depthBuffer[pixelIndex] = depth;
					} else {
						colour = ((int) (redLeft * (float) (texel >> 16 & 0xFF)) & 0xFF00 | 0xFF0000) << 8 | (int) (greenLeft * (float) (texel >> 8 & 0xFF)) & 0xFF00 | (int) (blueLeft * (float) (texel & 0xFF)) >> 8;
						if (fogLeft != 0.0F) {
							int fogInverse = (int) (255.0F - fogLeft);
							int fogTerm = ((this.fogColour & 0xFF00FF) * (int) fogLeft & 0xFF00FF00 | (this.fogColour & 0xFF00) * (int) fogLeft & 0xFF0000) >>> 8;
							colour = (((colour & 0xFF00FF) * fogInverse & 0xFF00FF00 | (colour & 0xFF00) * fogInverse & 0xFF0000) >>> 8) + fogTerm;
						}
						int dstColour = dst[pixelIndex];
						int inverseAlpha = 255 - texelAlpha;
						colour = ((dstColour & 0xFF00FF) * inverseAlpha + (colour & 0xFF00FF) * texelAlpha & 0xFF00FF00) + ((dstColour & 0xFF00) * inverseAlpha + (colour & 0xFF00) * texelAlpha & 0xFF0000) >> 8;
						dst[pixelIndex] = colour;
						this.depthBuffer[pixelIndex] = depth;
					}
				}
			}
			pixelIndex++;
			invZLeft += slopeInvZ;
			uLeft += slopeU;
			vLeft += slopeV;
			fogLeft += slopeFog;
			alphaLeft += slopeAlpha;
			redLeft += slopeRed;
			greenLeft += slopeGreen;
			blueLeft += slopeBlue;
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "()I")
	public int offsetY() {
		return this.lineOffsets[0] / this.surfaceWidth;
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFI)V")
	public void renderFlatTriangleRgb(@OriginalArg(0) float yA, @OriginalArg(1) float yB, @OriginalArg(2) float yC, @OriginalArg(3) float xA, @OriginalArg(4) float xB, @OriginalArg(5) float xC, @OriginalArg(6) float zA, @OriginalArg(7) float zB, @OriginalArg(8) float zC, @OriginalArg(9) int colour) {
		if (this.wireframe) {
			this.toolkit.drawOpaqueLine((int) yA, (int) xA, (int) xB, colour, (int) yB);
			this.toolkit.drawOpaqueLine((int) yB, (int) xB, (int) xC, colour, (int) yC);
			this.toolkit.drawOpaqueLine((int) yC, (int) xC, (int) xA, colour, (int) yA);
			return;
		}
		@Pc(46) float slopeXBa = xB - xA;
		@Pc(50) float slopeYBa = yB - yA;
		@Pc(54) float slopeXCa = xC - xA;
		@Pc(58) float slopeYCa = yC - yA;
		@Pc(62) float slopeZBa = zB - zA;
		@Pc(66) float slopeZCa = zC - zA;
		@Pc(68) float scaleXBa = 0.0F;
		if (yB != yA) {
			scaleXBa = (xB - xA) / (yB - yA);
		}
		@Pc(82) float scaleXCb = 0.0F;
		if (yC != yB) {
			scaleXCb = (xC - xB) / (yC - yB);
		}
		@Pc(96) float scaleXCa = 0.0F;
		if (yC != yA) {
			scaleXCa = (xA - xC) / (yA - yC);
		}
		@Pc(116) float determinant = slopeXBa * slopeYCa - slopeXCa * slopeYBa;
		if (determinant == 0.0F) {
			return;
		}
		@Pc(131) float slopeDepthX = (slopeZBa * slopeYCa - slopeZCa * slopeYBa) / determinant;
		@Pc(141) float slopeDepthY = (slopeZCa * slopeXBa - slopeZBa * slopeXCa) / determinant;
		if (yA <= yB && yA <= yC) {
			if (!(yA >= (float) this.height)) {
				if (yB > (float) this.height) {
					yB = this.height;
				}
				if (yC > (float) this.height) {
					yC = this.height;
				}
				zA = zA + slopeDepthX - slopeDepthX * xA;
				if (yB < yC) {
					xC = xA;
					if (yA < 0.0F) {
						xC = xA - scaleXCa * yA;
						xA -= scaleXBa * yA;
						zA -= slopeDepthY * yA;
						yA = 0.0F;
					}
					if (yB < 0.0F) {
						xB -= scaleXCb * yB;
						yB = 0.0F;
					}
					if (yA != yB && scaleXCa < scaleXBa || yA == yB && scaleXCa > scaleXCb) {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xC, (int) xA, zA, slopeDepthX);
							xC += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xC, (int) xB, zA, slopeDepthX);
							xC += scaleXCa;
							xB += scaleXCb;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xA, (int) xC, zA, slopeDepthX);
							xC += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xB, (int) xC, zA, slopeDepthX);
							xC += scaleXCa;
							xB += scaleXCb;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					}
				} else {
					xB = xA;
					if (yA < 0.0F) {
						xB = xA - scaleXCa * yA;
						xA -= scaleXBa * yA;
						zA -= slopeDepthY * yA;
						yA = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= scaleXCb * yC;
						yC = 0.0F;
					}
					if ((yA == yC || !(scaleXCa < scaleXBa)) && (yA != yC || !(scaleXCb > scaleXBa))) {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xA, (int) xB, zA, slopeDepthX);
							xB += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xA, (int) xC, zA, slopeDepthX);
							xC += scaleXCb;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xB, (int) xA, zA, slopeDepthX);
							xB += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yA, colour, (int) xC, (int) xA, zA, slopeDepthX);
							xC += scaleXCb;
							xA += scaleXBa;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= (float) this.height)) {
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yA > (float) this.height) {
					yA = this.height;
				}
				zB = zB + slopeDepthX - slopeDepthX * xB;
				if (yC < yA) {
					xA = xB;
					if (yB < 0.0F) {
						xA = xB - scaleXBa * yB;
						xB -= scaleXCb * yB;
						zB -= slopeDepthY * yB;
						yB = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= scaleXCa * yC;
						yC = 0.0F;
					}
					if (yB != yC && scaleXBa < scaleXCb || yB == yC && scaleXBa > scaleXCa) {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xA, (int) xB, zB, slopeDepthX);
							xA += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xA, (int) xC, zB, slopeDepthX);
							xA += scaleXBa;
							xC += scaleXCa;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xB, (int) xA, zB, slopeDepthX);
							xA += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xC, (int) xA, zB, slopeDepthX);
							xA += scaleXBa;
							xC += scaleXCa;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					}
				} else {
					xC = xB;
					if (yB < 0.0F) {
						xC = xB - scaleXBa * yB;
						xB -= scaleXCb * yB;
						zB -= slopeDepthY * yB;
						yB = 0.0F;
					}
					if (yA < 0.0F) {
						xA -= scaleXCa * yA;
						yA = 0.0F;
					}
					if (scaleXBa < scaleXCb) {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xC, (int) xB, zB, slopeDepthX);
							xC += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xA, (int) xB, zB, slopeDepthX);
							xA += scaleXCa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					} else {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xB, (int) xC, zB, slopeDepthX);
							xC += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yB, colour, (int) xB, (int) xA, zB, slopeDepthX);
							xA += scaleXCa;
							xB += scaleXCb;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					}
				}
			}
		} else if (!(yC >= (float) this.height)) {
			if (yA > (float) this.height) {
				yA = this.height;
			}
			if (yB > (float) this.height) {
				yB = this.height;
			}
			zC = zC + slopeDepthX - slopeDepthX * xC;
			if (yA < yB) {
				xB = xC;
				if (yC < 0.0F) {
					xB = xC - scaleXCb * yC;
					xC -= scaleXCa * yC;
					zC -= slopeDepthY * yC;
					yC = 0.0F;
				}
				if (yA < 0.0F) {
					xA -= scaleXBa * yA;
					yA = 0.0F;
				}
				if (scaleXCb < scaleXCa) {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xB, (int) xC, zC, slopeDepthX);
						xB += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xB, (int) xA, zC, slopeDepthX);
						xB += scaleXCb;
						xA += scaleXBa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				} else {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xC, (int) xB, zC, slopeDepthX);
						xB += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xA, (int) xB, zC, slopeDepthX);
						xB += scaleXCb;
						xA += scaleXBa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				}
			} else {
				xA = xC;
				if (yC < 0.0F) {
					xA = xC - scaleXCb * yC;
					xC -= scaleXCa * yC;
					zC -= slopeDepthY * yC;
					yC = 0.0F;
				}
				if (yB < 0.0F) {
					xB -= scaleXBa * yB;
					yB = 0.0F;
				}
				if (scaleXCb < scaleXCa) {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xA, (int) xC, zC, slopeDepthX);
						xA += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xB, (int) xC, zC, slopeDepthX);
						xB += scaleXBa;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				} else {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xC, (int) xA, zC, slopeDepthX);
						xA += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawFlatSpanRgb(this.raster, this.depthBuffer, (int) yC, colour, (int) xC, (int) xB, zC, slopeDepthX);
						xB += scaleXBa;
						xC += scaleXCa;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFFFF)V")
	private void drawHslSpanRgb(@OriginalArg(0) int[] dst, @OriginalArg(1) float[] depths, @OriginalArg(2) int rowOffset, @OriginalArg(5) int startX, @OriginalArg(6) int endX, @OriginalArg(7) float hsl, @OriginalArg(8) float hslStep, @OriginalArg(9) float z, @OriginalArg(10) float zStep) {
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width;
			}
			if (startX < 0) {
				startX = 0;
			}
		}
		if (startX >= endX) {
			return;
		}
		rowOffset += startX - 1;
		hsl += hslStep * (float) startX;
		z += zStep * (float) startX;
		@Pc(186) int alpha;
		@Pc(191) int invAlpha;
		@Pc(233) int background;
		@Pc(50) int remaining;
		@Pc(64) int colour;
		@Pc(71) int index;
		@Pc(54) float hslStep4;
		@Pc(87) float nextZ;
		if (this.threadResource.zWrite) {
			if (this.fastScanline) {
				remaining = endX - startX >> 2;
				hslStep4 = hslStep * 4.0F;
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = ColorUtil.hsvToRgbTable[(int) hsl];
							hsl += hslStep4;
							index = rowOffset + 1;
							if (z < depths[index]) {
								dst[index] = colour;
								depths[index] = z;
							}
							nextZ = z + zStep;
							index++;
							if (nextZ < depths[index]) {
								dst[index] = colour;
								depths[index] = nextZ;
							}
							nextZ += zStep;
							index++;
							if (nextZ < depths[index]) {
								dst[index] = colour;
								depths[index] = nextZ;
							}
							nextZ += zStep;
							rowOffset = index + 1;
							if (nextZ < depths[rowOffset]) {
								dst[rowOffset] = colour;
								depths[rowOffset] = nextZ;
							}
							z = nextZ + zStep;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = ColorUtil.hsvToRgbTable[(int) hsl];
						do {
							rowOffset++;
							if (z < depths[rowOffset]) {
								dst[rowOffset] = colour;
								depths[rowOffset] = z;
							}
							z += zStep;
							remaining--;
						} while (remaining > 0);
						return;
					}
				} else {
					alpha = this.alpha;
					invAlpha = 256 - this.alpha;
					if (remaining > 0) {
						do {
							colour = ColorUtil.hsvToRgbTable[(int) hsl];
							hsl += hslStep4;
							colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
							index = rowOffset + 1;
							if (z < depths[index]) {
								background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = z;
							}
							nextZ = z + zStep;
							index++;
							if (nextZ < depths[index]) {
								background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							index++;
							if (nextZ < depths[index]) {
								background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							rowOffset = index + 1;
							if (nextZ < depths[rowOffset]) {
								background = dst[rowOffset];
								dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[rowOffset] = nextZ;
							}
							z = nextZ + zStep;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining <= 0) {
						return;
					}
					colour = ColorUtil.hsvToRgbTable[(int) hsl];
					colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							depths[rowOffset] = z;
						}
						z += zStep;
						remaining--;
					} while (remaining > 0);
				}
			} else {
				remaining = endX - startX;
				if (this.alpha == 0) {
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset] = ColorUtil.hsvToRgbTable[(int) hsl];
							depths[rowOffset] = z;
						}
						z += zStep;
						hsl += hslStep;
						remaining--;
					} while (remaining > 0);
				} else {
					alpha = this.alpha;
					invAlpha = 256 - this.alpha;
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							colour = ColorUtil.hsvToRgbTable[(int) hsl];
							colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
							background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							depths[rowOffset] = z;
						}
						hsl += hslStep;
						z += zStep;
						remaining--;
					} while (remaining > 0);
				}
			}
		} else if (this.fastScanline) {
			remaining = endX - startX >> 2;
			hslStep4 = hslStep * 4.0F;
			if (this.alpha == 0) {
				if (remaining > 0) {
					do {
						colour = ColorUtil.hsvToRgbTable[(int) hsl];
						hsl += hslStep4;
						index = rowOffset + 1;
						if (z < depths[index]) {
							dst[index] = colour;
						}
						nextZ = z + zStep;
						index++;
						if (nextZ < depths[index]) {
							dst[index] = colour;
						}
						nextZ += zStep;
						index++;
						if (nextZ < depths[index]) {
							dst[index] = colour;
						}
						nextZ += zStep;
						rowOffset = index + 1;
						if (nextZ < depths[rowOffset]) {
							dst[rowOffset] = colour;
						}
						z = nextZ + zStep;
						remaining--;
					} while (remaining > 0);
				}
				remaining = endX - startX & 0x3;
				if (remaining > 0) {
					colour = ColorUtil.hsvToRgbTable[(int) hsl];
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset] = colour;
						}
						z += zStep;
						remaining--;
					} while (remaining > 0);
					return;
				}
			} else {
				alpha = this.alpha;
				invAlpha = 256 - this.alpha;
				if (remaining > 0) {
					do {
						colour = ColorUtil.hsvToRgbTable[(int) hsl];
						hsl += hslStep4;
						colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
						index = rowOffset + 1;
						if (z < depths[index]) {
							background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ = z + zStep;
						index++;
						if (nextZ < depths[index]) {
							background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ += zStep;
						index++;
						if (nextZ < depths[index]) {
							background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ += zStep;
						rowOffset = index + 1;
						if (nextZ < depths[rowOffset]) {
							background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						z = nextZ + zStep;
						remaining--;
					} while (remaining > 0);
				}
				remaining = endX - startX & 0x3;
				if (remaining <= 0) {
					return;
				}
				colour = ColorUtil.hsvToRgbTable[(int) hsl];
				colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						background = dst[rowOffset];
						dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
					}
					z += zStep;
					remaining--;
				} while (remaining > 0);
			}
		} else {
			remaining = endX - startX;
			if (this.alpha == 0) {
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						dst[rowOffset] = ColorUtil.hsvToRgbTable[(int) hsl];
					}
					z += zStep;
					hsl += hslStep;
					remaining--;
				} while (remaining > 0);
			} else {
				alpha = this.alpha;
				invAlpha = 256 - this.alpha;
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						colour = ColorUtil.hsvToRgbTable[(int) hsl];
						colour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
						background = dst[rowOffset];
						dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
					}
					hsl += hslStep;
					z += zStep;
					remaining--;
				} while (remaining > 0);
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFFFFFIIIIIIIIFIFIF)V")
	public void renderBlendedTexturedTriangle(@OriginalArg(0) float yA, @OriginalArg(1) float yB, @OriginalArg(2) float yC, @OriginalArg(3) float xA, @OriginalArg(4) float xB, @OriginalArg(5) float xC, @OriginalArg(6) float zA, @OriginalArg(7) float zB, @OriginalArg(8) float zC, @OriginalArg(9) float uA, @OriginalArg(10) float uB, @OriginalArg(11) float uC, @OriginalArg(12) float vA, @OriginalArg(13) float vB, @OriginalArg(14) float vC, @OriginalArg(15) int colourA, @OriginalArg(16) int colourB, @OriginalArg(17) int colourC, @OriginalArg(18) int fogColour, @OriginalArg(19) int fogA, @OriginalArg(20) int fogB, @OriginalArg(21) int fogC, @OriginalArg(22) int texture, @OriginalArg(23) float textureScale, @OriginalArg(24) int texture2, @OriginalArg(25) float textureScale2, @OriginalArg(26) int texture3, @OriginalArg(27) float textureScale3) {
		if (texture != this.textureId) {
			this.texels = this.toolkit.getTexels(texture);
			if (this.texels == null) {
				this.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) zA, (float) zB, (float) zC, ColorUtil.alphaBlend(colourA, fogColour | fogA << 24), ColorUtil.alphaBlend(colourB, fogColour | fogB << 24), ColorUtil.alphaBlend(colourC, fogColour | fogC << 24));
				return;
			}
			this.textureSize = this.toolkit.smallTexture(texture) ? 64 : this.toolkit.textureSize;
			this.textureMask = this.textureSize - 1;
			this.textureBlendMode = this.toolkit.textureAlphaBlendMode(texture);
		}
		this.textureScale = textureScale;
		if (texture2 != this.textureId2) {
			this.texels2 = this.toolkit.getTexels(texture2);
			if (this.texels2 == null) {
				this.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) zA, (float) zB, (float) zC, ColorUtil.alphaBlend(colourA, fogColour | fogA << 24), ColorUtil.alphaBlend(colourB, fogColour | fogB << 24), ColorUtil.alphaBlend(colourC, fogColour | fogC << 24));
				return;
			}
			this.textureSize2 = this.toolkit.smallTexture(texture2) ? 64 : this.toolkit.textureSize;
			this.textureMask2 = this.textureSize2 - 1;
		}
		this.textureScale2 = textureScale2;
		if (texture3 != this.textureId3) {
			this.texels3 = this.toolkit.getTexels(texture3);
			if (this.texels3 == null) {
				this.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) zA, (float) zB, (float) zC, ColorUtil.alphaBlend(colourA, fogColour | fogA << 24), ColorUtil.alphaBlend(colourB, fogColour | fogB << 24), ColorUtil.alphaBlend(colourC, fogColour | fogC << 24));
				return;
			}
			this.textureSize3 = this.toolkit.smallTexture(texture3) ? 64 : this.toolkit.textureSize;
			this.textureMask3 = this.textureSize3 - 1;
		}
		this.textureScale3 = textureScale3;
		this.fogColour = fogColour;
		@Pc(279) float alphaA = (float) (colourA >> 24 & 0xFF);
		@Pc(286) float alphaB = (float) (colourB >> 24 & 0xFF);
		@Pc(293) float alphaC = (float) (colourC >> 24 & 0xFF);
		@Pc(300) float redA = (float) (colourA >> 16 & 0xFF);
		@Pc(307) float redB = (float) (colourB >> 16 & 0xFF);
		@Pc(314) float redC = (float) (colourC >> 16 & 0xFF);
		@Pc(321) float greenA = (float) (colourA >> 8 & 0xFF);
		@Pc(328) float greenB = (float) (colourB >> 8 & 0xFF);
		@Pc(335) float greenC = (float) (colourC >> 8 & 0xFF);
		@Pc(340) float blueA = (float) (colourA & 0xFF);
		@Pc(345) float blueB = (float) (colourB & 0xFF);
		@Pc(350) float blueC = (float) (colourC & 0xFF);
		uA /= zA;
		uB /= zB;
		uC /= zC;
		vA /= zA;
		vB /= zB;
		vC /= zC;
		zA = 1.0F / zA;
		zB = 1.0F / zB;
		zC = 1.0F / zC;
		@Pc(388) float weightA = 1.0F;
		@Pc(390) float weightB = 0.0F;
		@Pc(392) float weightC = 0.0F;
		@Pc(394) float weight2A = 0.0F;
		@Pc(396) float weight2B = 1.0F;
		@Pc(398) float weight2C = 0.0F;
		@Pc(400) float slopeXBa = 0.0F;
		@Pc(402) float slopeZBa = 0.0F;
		@Pc(404) float slopeUBa = 0.0F;
		@Pc(406) float slopeVBa = 0.0F;
		@Pc(408) float slopeFogBa = 0.0F;
		@Pc(410) float slopeAlphaBa = 0.0F;
		@Pc(412) float slopeRedBa = 0.0F;
		@Pc(414) float slopeGreenBa = 0.0F;
		@Pc(416) float slopeBlueBa = 0.0F;
		@Pc(418) float slopeWeightBa = 0.0F;
		@Pc(420) float slopeWeight2Ba = 0.0F;
		@Pc(428) float local428;
		if (yB != yA) {
			float deltaYBa = yB - yA;
			slopeXBa = (xB - xA) / deltaYBa;
			slopeZBa = (zB - zA) / deltaYBa;
			slopeUBa = (uB - uA) / deltaYBa;
			slopeVBa = (vB - vA) / deltaYBa;
			slopeFogBa = (float) (fogB - fogA) / deltaYBa;
			slopeAlphaBa = (alphaB - alphaA) / deltaYBa;
			slopeRedBa = (redB - redA) / deltaYBa;
			slopeGreenBa = (greenB - greenA) / deltaYBa;
			slopeBlueBa = (blueB - blueA) / deltaYBa;
			slopeWeightBa = (weightB - weightA) / deltaYBa;
			slopeWeight2Ba = (weight2B - weight2A) / deltaYBa;
		}
		float slopeXCb = 0.0F;
		@Pc(499) float slopeZCb = 0.0F;
		@Pc(501) float slopeUCb = 0.0F;
		@Pc(503) float slopeVCb = 0.0F;
		@Pc(505) float slopeFogCb = 0.0F;
		@Pc(507) float slopeAlphaCb = 0.0F;
		@Pc(509) float slopeRedCb = 0.0F;
		@Pc(511) float slopeGreenCb = 0.0F;
		@Pc(513) float slopeBlueCb = 0.0F;
		@Pc(515) float slopeWeightCb = 0.0F;
		@Pc(517) float slopeWeight2Cb = 0.0F;
		@Pc(525) float local525;
		if (yC != yB) {
			float deltaYCb = yC - yB;
			slopeXCb = (xC - xB) / deltaYCb;
			slopeZCb = (zC - zB) / deltaYCb;
			slopeUCb = (uC - uB) / deltaYCb;
			slopeVCb = (vC - vB) / deltaYCb;
			slopeFogCb = (float) (fogC - fogB) / deltaYCb;
			slopeAlphaCb = (alphaC - alphaB) / deltaYCb;
			slopeRedCb = (redC - redB) / deltaYCb;
			slopeGreenCb = (greenC - greenB) / deltaYCb;
			slopeBlueCb = (blueC - blueB) / deltaYCb;
			slopeWeightCb = (weightC - weightB) / deltaYCb;
			slopeWeight2Cb = (weight2C - weight2B) / deltaYCb;
		}
		float slopeXCa = 0.0F;
		@Pc(596) float slopeZCa = 0.0F;
		@Pc(598) float slopeUCa = 0.0F;
		@Pc(600) float slopeVCa = 0.0F;
		@Pc(602) float slopeFogCa = 0.0F;
		@Pc(604) float slopeAlphaCa = 0.0F;
		@Pc(606) float slopeRedCa = 0.0F;
		@Pc(608) float slopeGreenCa = 0.0F;
		@Pc(610) float slopeBlueCa = 0.0F;
		@Pc(612) float slopeWeightCa = 0.0F;
		@Pc(614) float slopeWeight2Ca = 0.0F;
		if (yA != yC) {
			@Pc(622) float deltaYAc = yA - yC;
			slopeXCa = (xA - xC) / deltaYAc;
			slopeZCa = (zA - zC) / deltaYAc;
			slopeUCa = (uA - uC) / deltaYAc;
			slopeVCa = (vA - vC) / deltaYAc;
			slopeFogCa = (float) (fogA - fogC) / deltaYAc;
			slopeAlphaCa = (alphaA - alphaC) / deltaYAc;
			slopeRedCa = (redA - redC) / deltaYAc;
			slopeGreenCa = (greenA - greenC) / deltaYAc;
			slopeBlueCa = (blueA - blueC) / deltaYAc;
			slopeWeightCa = (weightA - weightC) / deltaYAc;
			slopeWeight2Ca = (weight2A - weight2C) / deltaYAc;
		}
		if (yA <= yB && yA <= yC) {
			if (!(yA >= (float) this.height)) {
				if (yB > (float) this.height) {
					yB = this.height;
				}
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yB < yC) {
					xC = xA;
					zC = zA;
					uC = uA;
					vC = vA;
					fogC = fogA;
					alphaC = alphaA;
					redC = redA;
					greenC = greenA;
					blueC = blueA;
					weightC = weightA;
					weight2C = weight2A;
					if (yA < 0.0F) {
						xA -= slopeXBa * yA;
						xC -= slopeXCa * yA;
						zA -= slopeZBa * yA;
						zC -= slopeZCa * yA;
						uA -= slopeUBa * yA;
						uC -= slopeUCa * yA;
						vA -= slopeVBa * yA;
						vC -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogBa * yA);
						fogC = (int) ((float) fogC - slopeFogCa * yA);
						alphaA -= slopeAlphaBa * yA;
						alphaC -= slopeAlphaCa * yA;
						redA -= slopeRedBa * yA;
						redC -= slopeRedCa * yA;
						greenA -= slopeGreenBa * yA;
						greenC -= slopeGreenCa * yA;
						blueA -= slopeBlueBa * yA;
						blueC -= slopeBlueCa * yA;
						weightA -= slopeWeightBa * yA;
						weightC -= slopeWeightCa * yA;
						weight2A -= slopeWeight2Ba * yA;
						weight2C -= slopeWeight2Ca * yA;
						yA = 0.0F;
					}
					if (yB < 0.0F) {
						xB -= slopeXCb * yB;
						zB -= slopeZCb * yB;
						uB -= slopeUCb * yB;
						vB -= slopeVCb * yB;
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaB -= slopeAlphaCb * yB;
						redB -= slopeRedCb * yB;
						greenB -= slopeGreenCb * yB;
						blueB -= slopeBlueCb * yB;
						weightB -= slopeWeightCb * yB;
						weight2B -= slopeWeight2Cb * yB;
						yB = 0.0F;
					}
					if (yA != yB && slopeXCa < slopeXBa || yA == yB && slopeXCa > slopeXCb) {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							weightA += slopeWeightBa;
							weightC += slopeWeightCa;
							weight2A += slopeWeight2Ba;
							weight2C += slopeWeight2Ba;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B);
							xB += slopeXCb;
							xC += slopeXCa;
							zB += slopeZCb;
							zC += slopeZCa;
							uB += slopeUCb;
							uC += slopeUCa;
							vB += slopeVCb;
							vC += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaCa;
							redB += slopeRedCb;
							redC += slopeRedCa;
							greenB += slopeGreenCb;
							greenC += slopeGreenCa;
							blueB += slopeBlueCb;
							blueC += slopeBlueCa;
							weightB += slopeWeightCb;
							weightC += slopeWeightCa;
							weight2B += slopeWeight2Cb;
							weight2C += slopeWeight2Ca;
							yA += this.surfaceWidth;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							weightA += slopeWeightBa;
							weightC += slopeWeightCa;
							weight2A += slopeWeight2Ba;
							weight2C += slopeWeight2Ca;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C);
							xB += slopeXCb;
							xC += slopeXCa;
							zB += slopeZCb;
							zC += slopeZCa;
							uB += slopeUCb;
							uC += slopeUCa;
							vB += slopeVCb;
							vC += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaCa;
							redB += slopeRedCb;
							redC += slopeRedCa;
							greenB += slopeGreenCb;
							greenC += slopeGreenCa;
							blueB += slopeBlueCb;
							blueC += slopeBlueCa;
							weightB += slopeWeightCb;
							weightC += slopeWeightCa;
							weight2B += slopeWeight2Cb;
							weight2C += slopeWeight2Ca;
							yA += this.surfaceWidth;
						}
					}
				} else {
					xB = xA;
					zB = zA;
					uB = uA;
					vB = vA;
					fogB = fogA;
					alphaB = alphaA;
					redB = redA;
					greenB = greenA;
					blueB = blueA;
					weightB = weightA;
					weight2B = weight2A;
					if (yA < 0.0F) {
						xA -= slopeXBa * yA;
						xB -= slopeXCa * yA;
						zA -= slopeZBa * yA;
						zB -= slopeZCa * yA;
						uA -= slopeUBa * yA;
						uB -= slopeUCa * yA;
						vA -= slopeVBa * yA;
						vB -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogBa * yA);
						fogB = (int) ((float) fogB - slopeFogCa * yA);
						alphaA -= slopeAlphaBa * yA;
						alphaB -= slopeAlphaCa * yA;
						redA -= slopeRedBa * yA;
						redB -= slopeRedCa * yA;
						greenA -= slopeGreenBa * yA;
						greenB -= slopeGreenCa * yA;
						blueA -= slopeBlueBa * yA;
						blueB -= slopeBlueCa * yA;
						weightA -= slopeWeightBa * yA;
						weightB -= slopeWeightCa * yA;
						weight2A -= slopeWeight2Ba * yA;
						weight2B -= slopeWeight2Ca * yA;
						yA = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCb * yC;
						zC -= slopeZCb * yC;
						uC -= slopeUCb * yC;
						vC -= slopeVCb * yC;
						fogC = (int) ((float) fogC - slopeFogCb * yC);
						alphaC -= slopeAlphaCb * yC;
						redC -= slopeRedCb * yC;
						greenC -= slopeGreenCb * yC;
						blueC -= slopeBlueCb * yC;
						weightC -= slopeWeightCb * yC;
						weight2C -= slopeWeight2Cb * yC;
						yC = 0.0F;
					}
					if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A);
							xA += slopeXBa;
							xB += slopeXCa;
							zA += slopeZBa;
							zB += slopeZCa;
							uA += slopeUBa;
							uB += slopeUCa;
							vA += slopeVBa;
							vB += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaB += slopeAlphaCa;
							redA += slopeRedBa;
							redB += slopeRedCa;
							greenA += slopeGreenBa;
							greenB += slopeGreenCa;
							blueA += slopeBlueBa;
							blueB += slopeBlueCa;
							weightA += slopeWeightBa;
							weightB += slopeWeightCa;
							weight2A += slopeWeight2Ba;
							weight2B += slopeWeight2Ca;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A);
							xC += slopeXCb;
							xA += slopeXBa;
							zC += slopeZCb;
							zA += slopeZBa;
							uC += slopeUCb;
							uA += slopeUBa;
							vC += slopeVCb;
							vA += slopeVBa;
							fogC = (int) ((float) fogC + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaC += slopeAlphaCb;
							alphaA += slopeAlphaBa;
							redC += slopeRedCb;
							redA += slopeRedBa;
							greenC += slopeGreenCb;
							greenA += slopeGreenBa;
							blueC += slopeBlueCb;
							blueA += slopeBlueBa;
							weightC += slopeWeightCb;
							weightA += slopeWeightBa;
							weight2C += slopeWeight2Cb;
							weight2A += slopeWeight2Ba;
							yA += this.surfaceWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B);
							xB += slopeXCa;
							xA += slopeXBa;
							zB += slopeZCa;
							zA += slopeZBa;
							uB += slopeUCa;
							uA += slopeUBa;
							vB += slopeVCa;
							vA += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCa);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaB += slopeAlphaCa;
							alphaA += slopeAlphaBa;
							redB += slopeRedCa;
							redA += slopeRedBa;
							greenB += slopeGreenCa;
							greenA += slopeGreenBa;
							blueB += slopeBlueCa;
							blueA += slopeBlueBa;
							weightB += slopeWeightCa;
							weightA += slopeWeightBa;
							weight2B += slopeWeight2Ca;
							weight2A += slopeWeight2Ba;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yA, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C);
							xA += slopeXBa;
							xC += slopeXCb;
							zA += slopeZBa;
							zC += slopeZCb;
							uA += slopeUBa;
							uC += slopeUCb;
							vA += slopeVBa;
							vC += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCb);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCb;
							redA += slopeRedBa;
							redC += slopeRedCb;
							greenA += slopeGreenBa;
							greenC += slopeGreenCb;
							blueA += slopeBlueBa;
							blueC += slopeBlueCb;
							weightA += slopeWeightBa;
							weightC += slopeWeightCb;
							weight2A += slopeWeight2Ba;
							weight2C += slopeWeight2Cb;
							yA += this.surfaceWidth;
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= (float) this.height)) {
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yA > (float) this.height) {
					yA = this.height;
				}
				if (yC < yA) {
					xA = xB;
					zA = zB;
					uA = uB;
					vA = vB;
					fogA = fogB;
					alphaA = alphaB;
					redA = redB;
					greenA = greenB;
					blueA = blueB;
					weightA = weightB;
					weight2A = weight2B;
					if (yB < 0.0F) {
						xA = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						zA = zB - slopeZBa * yB;
						zB -= slopeZCb * yB;
						uA = uB - slopeUBa * yB;
						uB -= slopeUCb * yB;
						vA = vB - slopeVBa * yB;
						vB -= slopeVCb * yB;
						fogA = (int) ((float) fogB - slopeFogBa * yB);
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaA = alphaB - slopeAlphaBa * yB;
						alphaB -= slopeAlphaCb * yB;
						redA = redB - slopeRedBa * yB;
						redB -= slopeRedCb * yB;
						greenA = greenB - slopeGreenBa * yB;
						greenB -= slopeGreenCb * yB;
						blueA = blueB - slopeBlueBa * yB;
						blueB -= slopeBlueCb * yB;
						weightA = weightB - slopeWeightBa * yB;
						weightB -= slopeWeightCb * yB;
						weight2A = weight2B - slopeWeight2Ba * yB;
						weight2B -= slopeWeight2Cb * yB;
						yB = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCa * yC;
						zC -= slopeZCa * yC;
						uC -= slopeUCa * yC;
						vC -= slopeVCa * yC;
						fogC = (int) ((float) fogC - slopeFogCa * yC);
						alphaC -= slopeAlphaCa * yC;
						redC -= slopeRedCa * yC;
						greenC -= slopeGreenCa * yC;
						blueC -= slopeBlueCa * yC;
						weightC -= slopeWeightCa * yC;
						weight2C -= slopeWeight2Ca * yC;
						yC = 0.0F;
					}
					if (yB != yC && slopeXBa < slopeXCb || yB == yC && slopeXBa > slopeXCa) {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B);
							xA += slopeXBa;
							xB += slopeXCb;
							zA += slopeZBa;
							zB += slopeZCb;
							uA += slopeUBa;
							uB += slopeUCb;
							vA += slopeVBa;
							vB += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaA += slopeAlphaBa;
							alphaB += slopeAlphaCb;
							redA += slopeRedBa;
							redB += slopeRedCb;
							greenA += slopeGreenBa;
							greenB += slopeGreenCb;
							blueA += slopeBlueBa;
							blueB += slopeBlueCb;
							weightA += slopeWeightBa;
							weightB += slopeWeightCb;
							weight2A += slopeWeight2Ba;
							weight2B += slopeWeight2Cb;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							weightA += slopeWeightBa;
							weightC += slopeWeightCa;
							weight2A += slopeWeight2Ba;
							weight2C += slopeWeight2Ca;
							yB += this.surfaceWidth;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A);
							xB += slopeXCb;
							xA += slopeXBa;
							zB += slopeZCb;
							zA += slopeZBa;
							uB += slopeUCb;
							uA += slopeUBa;
							vB += slopeVCb;
							vA += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaB += slopeAlphaCb;
							alphaA += slopeAlphaBa;
							redB += slopeRedCb;
							redA += slopeRedBa;
							greenB += slopeGreenCb;
							greenA += slopeGreenBa;
							blueB += slopeBlueCb;
							blueA += slopeBlueBa;
							weightC += slopeWeightCb;
							weightA += slopeWeightBa;
							weight2B += slopeWeight2Cb;
							weight2A += slopeWeight2Ba;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A);
							xC += slopeXCa;
							xA += slopeXBa;
							zC += slopeZCa;
							zA += slopeZBa;
							uC += slopeUCa;
							uA += slopeUBa;
							vC += slopeVCa;
							vA += slopeVBa;
							fogC = (int) ((float) fogC + slopeFogCa);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaC += slopeAlphaCa;
							alphaA += slopeAlphaBa;
							redC += slopeRedCa;
							redA += slopeRedBa;
							greenC += slopeGreenCa;
							greenA += slopeGreenBa;
							blueC += slopeBlueCa;
							blueA += slopeBlueBa;
							weightC += slopeWeightCa;
							weightA += slopeWeightBa;
							weight2C += slopeWeight2Ca;
							weight2A += slopeWeight2Ba;
							yB += this.surfaceWidth;
						}
					}
				} else {
					xC = xB;
					zC = zB;
					uC = uB;
					vC = vB;
					fogC = fogB;
					alphaC = alphaB;
					redC = redB;
					greenC = greenB;
					blueC = blueB;
					weightC = weightB;
					weight2C = weight2B;
					if (yB < 0.0F) {
						xC = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						zC = zB - slopeZBa * yB;
						zB -= slopeZCb * yB;
						uC = uB - slopeUBa * yB;
						uB -= slopeUCb * yB;
						vC = vB - slopeVBa * yB;
						vB -= slopeVCb * yB;
						fogC = (int) ((float) fogB - slopeFogBa * yB);
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaC = alphaB - slopeAlphaBa * yB;
						alphaB -= slopeAlphaCb * yB;
						redC = redB - slopeRedBa * yB;
						redB -= slopeRedCb * yB;
						greenC = greenB - slopeGreenBa * yB;
						greenB -= slopeGreenCb * yB;
						blueC = blueB - slopeBlueBa * yB;
						blueB -= slopeBlueCb * yB;
						weightC = weightB - slopeWeightBa * yB;
						weightB -= slopeWeightCb * yB;
						weight2C = weight2B - slopeWeight2Ba * yB;
						weight2B -= slopeWeight2Cb * yB;
						yB = 0.0F;
					}
					if (yA < 0.0F) {
						xA -= slopeXCa * yA;
						zA -= slopeZCa * yA;
						uA -= slopeUCa * yA;
						vA -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogCa * yA);
						alphaA -= slopeAlphaCa * yA;
						redA -= slopeRedCa * yA;
						greenA -= slopeGreenCa * yA;
						blueA -= slopeBlueCa * yA;
						weightA -= slopeWeightCa * yA;
						weight2A -= slopeWeight2Ca * yA;
						yA = 0.0F;
					}
					yC -= yA;
					yA -= yB;
					yB = this.lineOffsets[(int) yB];
					if (slopeXBa < slopeXCb) {
						while (--yA >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B);
							xC += slopeXBa;
							xB += slopeXCb;
							zC += slopeZBa;
							zB += slopeZCb;
							uC += slopeUBa;
							uB += slopeUCb;
							vC += slopeVBa;
							vB += slopeVCb;
							fogC = (int) ((float) fogC + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaC += slopeAlphaBa;
							alphaB += slopeAlphaCb;
							redC += slopeRedBa;
							redB += slopeRedCb;
							greenC += slopeGreenBa;
							greenB += slopeGreenCb;
							blueC += slopeBlueBa;
							blueB += slopeBlueCb;
							weightC += slopeWeightBa;
							weightB += slopeWeightCb;
							weight2C += slopeWeight2Ba;
							weight2B += slopeWeight2Cb;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B);
							xA += slopeXCa;
							xB += slopeXCb;
							zA += slopeZCa;
							zB += slopeZCb;
							uA += slopeUCa;
							uB += slopeUCb;
							vA += slopeVCa;
							vB += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogCa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaA += slopeAlphaCa;
							alphaB += slopeAlphaCb;
							redA += slopeRedCa;
							redB += slopeRedCb;
							greenA += slopeGreenCa;
							greenB += slopeGreenCb;
							blueA += slopeBlueCa;
							blueB += slopeBlueCb;
							weightA += slopeWeightCa;
							weightB += slopeWeightCb;
							weight2A += slopeWeight2Ca;
							weight2B += slopeWeight2Cb;
							yB += this.surfaceWidth;
						}
					} else {
						while (--yA >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C);
							xB += slopeXCb;
							xC += slopeXBa;
							zB += slopeZCb;
							zC += slopeZBa;
							uB += slopeUCb;
							uC += slopeUBa;
							vB += slopeVCb;
							vC += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogBa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaBa;
							redB += slopeRedCb;
							redC += slopeRedBa;
							greenB += slopeGreenCb;
							greenC += slopeGreenBa;
							blueB += slopeBlueCb;
							blueC += slopeBlueBa;
							weightB += slopeWeightCb;
							weightC += slopeWeightBa;
							weight2B += slopeWeight2Cb;
							weight2C += slopeWeight2Ba;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yB, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A);
							xB += slopeXCb;
							xA += slopeXCa;
							zB += slopeZCb;
							zA += slopeZCa;
							uB += slopeUCb;
							uA += slopeUCa;
							vB += slopeVCb;
							vA += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaA += slopeAlphaCa;
							redB += slopeRedCb;
							redA += slopeRedCa;
							greenB += slopeGreenCb;
							greenA += slopeGreenCa;
							blueB += slopeBlueCb;
							blueA += slopeBlueCa;
							weightB += slopeWeightCb;
							weightA += slopeWeightCa;
							weight2B += slopeWeight2Cb;
							weight2A += slopeWeight2Ca;
							yB += this.surfaceWidth;
						}
					}
				}
			}
		} else if (!(yC >= (float) this.height)) {
			if (yA > (float) this.height) {
				yA = this.height;
			}
			if (yB > (float) this.height) {
				yB = this.height;
			}
			if (yA < yB) {
				xB = xC;
				zB = zC;
				uB = uC;
				vB = vC;
				fogB = fogC;
				alphaB = alphaC;
				redB = redC;
				greenB = greenC;
				blueB = blueC;
				weightB = weightC;
				weight2B = weight2C;
				if (yC < 0.0F) {
					xC -= slopeXCa * yC;
					xB -= slopeXCb * yC;
					zC -= slopeZCa * yC;
					zB -= slopeZCb * yC;
					uC -= slopeUCa * yC;
					uB -= slopeUCb * yC;
					vC -= slopeVCa * yC;
					vB -= slopeVCb * yC;
					fogC = (int) ((float) fogC - slopeFogCa * 3.0F);
					fogB = (int) ((float) fogB - slopeFogCb * yC);
					alphaC -= slopeAlphaCa * yC;
					alphaB -= slopeAlphaCb * yC;
					redC -= slopeRedCa * yC;
					redB -= slopeRedCb * yC;
					greenC -= slopeGreenCa * yC;
					greenB -= slopeGreenCb * yC;
					blueC -= slopeBlueCa * yC;
					blueB -= slopeBlueCb * yC;
					weightC -= slopeWeightCa * yC;
					weightB -= slopeWeightCb * yC;
					weight2C -= slopeWeight2Ca * yC;
					weight2B -= slopeWeight2Cb * yC;
					yC = 0.0F;
				}
				if (yA < 0.0F) {
					xA -= slopeXBa * yA;
					zA -= slopeZBa * yA;
					uA -= slopeUBa * yA;
					vA -= slopeVBa * yA;
					fogA = (int) ((float) fogA - slopeFogBa * yA);
					alphaA -= slopeAlphaBa * yA;
					redA -= slopeRedBa * yA;
					greenA -= slopeGreenBa * yA;
					blueA -= slopeBlueBa * yA;
					weightA -= slopeWeightBa * yA;
					weight2A -= slopeWeight2Ba * yA;
					yA = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C);
						xB += slopeXCb;
						xC += slopeXCa;
						zB += slopeZCb;
						zC += slopeZCa;
						uB += slopeUCb;
						uC += slopeUCa;
						vB += slopeVCb;
						vC += slopeVCa;
						fogB = (int) ((float) fogB + slopeFogCb);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaB += slopeAlphaCb;
						alphaC += slopeAlphaCa;
						redB += slopeRedCb;
						redC += slopeRedCa;
						greenB += slopeGreenCb;
						greenC += slopeGreenCa;
						blueB += slopeBlueCb;
						blueC += slopeBlueCa;
						weightB += slopeWeightCb;
						weightC += slopeWeightCa;
						weight2B += slopeWeight2Cb;
						weight2C += slopeWeight2Ca;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA, weightB, weightA, weight2B, weight2A);
						xB += slopeXCb;
						xA += slopeXBa;
						zB += slopeZCb;
						zA += slopeZBa;
						uB += slopeUCb;
						uA += slopeUBa;
						vB += slopeVCb;
						vA += slopeVBa;
						fogB = (int) ((float) fogB + slopeFogCb);
						fogA = (int) ((float) fogA + slopeFogBa);
						alphaB += slopeAlphaCb;
						alphaA += slopeAlphaBa;
						redB += slopeRedCb;
						redA += slopeRedBa;
						greenB += slopeGreenCb;
						greenA += slopeGreenBa;
						blueB += slopeBlueCb;
						blueA += slopeBlueBa;
						weightB += slopeWeightCb;
						weightA += slopeWeightBa;
						weight2B += slopeWeight2Cb;
						weight2A += slopeWeight2Ba;
						yC += this.surfaceWidth;
					}
				} else {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B);
						xC += slopeXCa;
						xB += slopeXCb;
						zC += slopeZCa;
						zB += slopeZCb;
						uC += slopeUCa;
						uB += slopeUCb;
						vC += slopeVCa;
						vB += slopeVCb;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogB = (int) ((float) fogB + slopeFogCb);
						alphaC += slopeAlphaCa;
						alphaB += slopeAlphaCb;
						redC += slopeRedCa;
						redB += slopeRedCb;
						greenC += slopeGreenCa;
						greenB += slopeGreenCb;
						blueC += slopeBlueCa;
						blueB += slopeBlueCb;
						weightC += slopeWeightCa;
						weightB += slopeWeightCb;
						weight2C += slopeWeight2Ca;
						weight2B += slopeWeight2Cb;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB, weightA, weightB, weight2A, weight2B);
						xA += slopeXBa;
						xB += slopeXCb;
						zA += slopeZBa;
						zB += slopeZCb;
						uA += slopeUBa;
						uB += slopeUCb;
						vA += slopeVBa;
						vB += slopeVCb;
						fogA = (int) ((float) fogA + slopeFogBa);
						fogB = (int) ((float) fogB + slopeFogCb);
						alphaA += slopeAlphaBa;
						alphaB += slopeAlphaCb;
						redA += slopeRedBa;
						redB += slopeRedCb;
						greenA += slopeGreenBa;
						greenB += slopeGreenCb;
						blueA += slopeBlueBa;
						blueB += slopeBlueCb;
						weightA += slopeWeightBa;
						weightB += slopeWeightCb;
						weight2A += slopeWeight2Ba;
						weight2B += slopeWeight2Cb;
						yC += this.surfaceWidth;
					}
				}
			} else {
				xA = xC;
				zA = zC;
				uA = uC;
				vA = vC;
				fogA = fogC;
				alphaA = alphaC;
				redA = redC;
				greenA = greenC;
				blueA = blueC;
				weightA = weightC;
				weight2A = weight2C;
				if (yC < 0.0F) {
					xC -= slopeXCa * yC;
					xA -= slopeXCb * yC;
					zC -= slopeZCa * yC;
					zA -= slopeZCb * yC;
					uC -= slopeUCa * yC;
					uA -= slopeUCb * yC;
					vC -= slopeVCa * yC;
					vA -= slopeVCb * yC;
					fogC = (int) ((float) fogC - slopeFogCa * 3.0F);
					fogA = (int) ((float) fogA - slopeFogCb * yC);
					alphaC -= slopeAlphaCa * yC;
					alphaA -= slopeAlphaCb * yC;
					redC -= slopeRedCa * yC;
					redA -= slopeRedCb * yC;
					greenC -= slopeGreenCa * yC;
					greenA -= slopeGreenCb * yC;
					blueC -= slopeBlueCa * yC;
					blueA -= slopeBlueCb * yC;
					weightC -= slopeWeightCa * yC;
					weightA -= slopeWeightCb * yC;
					weight2C -= slopeWeight2Ca * yC;
					weight2A -= slopeWeight2Cb * yC;
					yC = 0.0F;
				}
				if (yB < 0.0F) {
					xB -= slopeXBa * yB;
					zB -= slopeZBa * yB;
					uB -= slopeUBa * yB;
					vB -= slopeVBa * yB;
					fogB = (int) ((float) fogB - slopeFogBa * yB);
					alphaB -= slopeAlphaBa * yB;
					redB -= slopeRedBa * yB;
					greenB -= slopeGreenBa * yB;
					blueB -= slopeBlueBa * yB;
					weightB -= slopeWeightBa * yB;
					weight2B -= slopeWeight2Ba * yB;
					yB = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC, weightA, weightC, weight2A, weight2C);
						xA += slopeXCb;
						xC += slopeXCa;
						zA += slopeZCb;
						zC += slopeZCa;
						uA += slopeUCb;
						uC += slopeUCa;
						vA += slopeVCb;
						vC += slopeVCa;
						fogA = (int) ((float) fogA + slopeFogCb);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaA += slopeAlphaCb;
						alphaC += slopeAlphaCa;
						redA += slopeRedCb;
						redC += slopeRedCa;
						greenA += slopeGreenCb;
						greenC += slopeGreenCa;
						blueA += slopeBlueCb;
						blueC += slopeBlueCa;
						weightA += slopeWeightCb;
						weightC += slopeWeightCa;
						weight2A += slopeWeight2Cb;
						weight2C += slopeWeight2Ca;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC, weightB, weightC, weight2B, weight2C);
						xB += slopeXBa;
						xC += slopeXCa;
						zB += slopeZBa;
						zC += slopeZCa;
						uB += slopeUBa;
						uC += slopeUCa;
						vB += slopeVBa;
						vC += slopeVCa;
						fogB = (int) ((float) fogB + slopeFogBa);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaB += slopeAlphaBa;
						alphaC += slopeAlphaCa;
						redB += slopeRedBa;
						redC += slopeRedCa;
						greenB += slopeGreenBa;
						greenC += slopeGreenCa;
						blueB += slopeBlueBa;
						blueC += slopeBlueCa;
						weightB += slopeWeightBa;
						weightC += slopeWeightCa;
						weight2B += slopeWeight2Ba;
						weight2C += slopeWeight2Ca;
						yC += this.surfaceWidth;
					}
				} else {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA, weightC, weightA, weight2C, weight2A);
						xC += slopeXCa;
						xA += slopeXCb;
						zC += slopeZCa;
						zA += slopeZCb;
						uC += slopeUCa;
						uA += slopeUCb;
						vC += slopeVCa;
						vA += slopeVCb;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogA = (int) ((float) fogA + slopeFogCb);
						alphaC += slopeAlphaCa;
						alphaA += slopeAlphaCb;
						redC += slopeRedCa;
						redA += slopeRedCb;
						greenC += slopeGreenCa;
						greenA += slopeGreenCb;
						blueC += slopeBlueCa;
						blueA += slopeBlueCb;
						weightC += slopeWeightCa;
						weightA += slopeWeightCb;
						weight2C += slopeWeight2Ca;
						weight2A += slopeWeight2Cb;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawBlendedTexturedSpan(this.raster, this.texels, (int) yC, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB, weightC, weightB, weight2C, weight2B);
						xC += slopeXCa;
						xB += slopeXBa;
						zC += slopeZCa;
						zB += slopeZBa;
						uC += slopeUCa;
						uB += slopeUBa;
						vC += slopeVCa;
						vB += slopeVBa;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogB = (int) ((float) fogB + slopeFogBa);
						alphaC += slopeAlphaCa;
						alphaB += slopeAlphaBa;
						redC += slopeRedCa;
						redB += slopeRedBa;
						greenC += slopeGreenCa;
						greenB += slopeGreenBa;
						blueC += slopeBlueCa;
						blueB += slopeBlueBa;
						weightC += slopeWeightCa;
						weightB += slopeWeightBa;
						weight2C += slopeWeight2Ca;
						weight2B += slopeWeight2Ba;
						yC += this.surfaceWidth;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFFFFFFFF)V")
	private void drawGouraudSpanRgb(@OriginalArg(0) int[] dst, @OriginalArg(1) float[] depths, @OriginalArg(2) int rowOffset, @OriginalArg(5) int startX, @OriginalArg(6) int endX, @OriginalArg(7) float z, @OriginalArg(8) float zStep, @OriginalArg(9) float red, @OriginalArg(10) float redStep, @OriginalArg(11) float green, @OriginalArg(12) float greenStep, @OriginalArg(13) float blue, @OriginalArg(14) float blueStep) {
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width;
			}
			if (startX < 0) {
				startX = 0;
			}
		}
		if (startX >= endX) {
			return;
		}
		@Pc(163) int alpha;
		@Pc(168) int local168;
		@Pc(223) int local223;
		@Pc(456) int local456;
		@Pc(460) int sum;
		@Pc(468) int redBlueSum;
		@Pc(54) int remaining;
		@Pc(88) int colour;
		@Pc(103) int index;
		@Pc(58) float redStep4;
		@Pc(62) float greenStep4;
		@Pc(66) float blueStep4;
		if (this.depthDisabled) {
			rowOffset += startX;
			red += redStep * (float) startX;
			green += greenStep * (float) startX;
			blue += blueStep * (float) startX;
			if (this.fastScanline) {
				remaining = endX - startX >> 2;
				redStep4 = redStep * 4.0F;
				greenStep4 = greenStep * 4.0F;
				blueStep4 = blueStep * 4.0F;
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							index = rowOffset + 1;
							dst[rowOffset] = colour;
							dst[index++] = colour;
							dst[index++] = colour;
							rowOffset = index + 1;
							dst[index] = colour;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						do {
							dst[rowOffset++] = colour;
							remaining--;
						} while (remaining > 0);
						return;
					}
				} else if (this.halfBlend) {
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							index = rowOffset + 1;
							int background = dst[rowOffset];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
							@Pc(497) int index1 = index++;
							@Pc(503) int background1 = dst[index1];
							@Pc(507) int sum1 = colour + background1;
							@Pc(515) int redBlueSum1 = (colour & 0xFF00FF) + (background1 & 0xFF00FF);
							@Pc(525) int overflow1 = (redBlueSum1 & 0x1000100) + (sum1 - redBlueSum1 & 0x10000);
							dst[index1] = sum1 - overflow1 | 0xFF000000 | overflow1 - (overflow1 >>> 8);
							@Pc(544) int index2 = index++;
							@Pc(550) int background2 = dst[index2];
							@Pc(554) int sum2 = colour + background2;
							@Pc(562) int redBlueSum2 = (colour & 0xFF00FF) + (background2 & 0xFF00FF);
							@Pc(572) int overflow2 = (redBlueSum2 & 0x1000100) + (sum2 - redBlueSum2 & 0x10000);
							dst[index2] = sum2 - overflow2 | 0xFF000000 | overflow2 - (overflow2 >>> 8);
							rowOffset = index + 1;
							@Pc(597) int background3 = dst[index];
							@Pc(601) int sum3 = colour + background3;
							@Pc(609) int redBlueSum3 = (colour & 0xFF00FF) + (background3 & 0xFF00FF);
							@Pc(619) int overflow3 = (redBlueSum3 & 0x1000100) + (sum3 - redBlueSum3 & 0x10000);
							dst[index] = sum3 - overflow3 | 0xFF000000 | overflow3 - (overflow3 >>> 8);
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						do {
							int pixelIndex = rowOffset++;
							int background = dst[pixelIndex];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[pixelIndex] = sum - background | 0xFF000000 | background - (background >>> 8);
							remaining--;
						} while (remaining > 0);
					}
				} else {
					alpha = this.alpha;
					int inverseAlpha = 256 - this.alpha;
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							int background = dst[rowOffset];
							index = rowOffset + 1;
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							background = dst[index];
							dst[index++] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							background = dst[index];
							dst[index++] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							background = dst[index];
							rowOffset = index + 1;
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						do {
							int background = dst[rowOffset];
							dst[rowOffset++] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							remaining--;
						} while (remaining > 0);
						return;
					}
				}
			} else {
				remaining = endX - startX;
				if (this.alpha == 0) {
					do {
						dst[rowOffset++] = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						red += redStep;
						green += greenStep;
						blue += blueStep;
						remaining--;
					} while (remaining > 0);
				} else if (this.halfBlend) {
					do {
						int pixelIndex = rowOffset++;
						int srcColour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						int background = dst[pixelIndex];
						sum = srcColour + background;
						redBlueSum = (srcColour & 0xFF00FF) + (background & 0xFF00FF);
						background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						dst[pixelIndex] = sum - background | 0xFF000000 | background - (background >>> 8);
						red += redStep;
						green += greenStep;
						blue += blueStep;
						remaining--;
					} while (remaining > 0);
				} else {
					alpha = this.alpha;
					int inverseAlpha = 256 - this.alpha;
					do {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						red += redStep;
						green += greenStep;
						blue += blueStep;
						colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						int background = dst[rowOffset];
						dst[rowOffset++] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						remaining--;
					} while (remaining > 0);
				}
			}
			return;
		}
		rowOffset += startX - 1;
		z += zStep * (float) startX;
		red += redStep * (float) startX;
		green += greenStep * (float) startX;
		blue += blueStep * (float) startX;
		@Pc(1034) float nextZ;
		if (this.threadResource.zWrite) {
			if (this.fastScanline) {
				remaining = endX - startX >> 2;
				redStep4 = redStep * 4.0F;
				greenStep4 = greenStep * 4.0F;
				blueStep4 = blueStep * 4.0F;
				if (this.alpha == 0) {
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							index = rowOffset + 1;
							if (z < depths[index]) {
								dst[index] = colour;
								depths[index] = z;
							}
							nextZ = z + zStep;
							index++;
							if (nextZ < depths[index]) {
								dst[index] = colour;
								depths[index] = nextZ;
							}
							nextZ += zStep;
							index++;
							if (nextZ < depths[index]) {
								dst[index] = colour;
								depths[index] = nextZ;
							}
							nextZ += zStep;
							rowOffset = index + 1;
							if (nextZ < depths[rowOffset]) {
								dst[rowOffset] = colour;
								depths[rowOffset] = nextZ;
							}
							z = nextZ + zStep;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						do {
							rowOffset++;
							if (z < depths[rowOffset]) {
								dst[rowOffset] = colour;
								depths[rowOffset] = z;
							}
							z += zStep;
							remaining--;
						} while (remaining > 0);
						return;
					}
				} else if (this.halfBlend) {
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							index = rowOffset + 1;
							if (z < depths[index]) {
								int background = dst[index];
								sum = colour + background;
								redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
								background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
								depths[index] = z;
							}
							nextZ = z + zStep;
							index++;
							if (nextZ < depths[index]) {
								int background = dst[index];
								sum = colour + background;
								redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
								background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							index++;
							if (nextZ < depths[index]) {
								int background = dst[index];
								sum = colour + background;
								redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
								background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							rowOffset = index + 1;
							if (nextZ < depths[rowOffset]) {
								int background = dst[rowOffset];
								sum = colour + background;
								redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
								background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
								depths[rowOffset] = nextZ;
							}
							z = nextZ + zStep;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						do {
							rowOffset++;
							if (z < depths[rowOffset]) {
								int background = dst[rowOffset];
								sum = colour + background;
								redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
								background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
								dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
								depths[rowOffset] = z;
							}
							z += zStep;
							remaining--;
						} while (remaining > 0);
					}
				} else {
					alpha = this.alpha;
					int inverseAlpha = 256 - this.alpha;
					if (remaining > 0) {
						do {
							colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							red += redStep4;
							green += greenStep4;
							blue += blueStep4;
							colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							index = rowOffset + 1;
							if (z < depths[index]) {
								int background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = z;
							}
							nextZ = z + zStep;
							index++;
							if (nextZ < depths[index]) {
								int background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							index++;
							if (nextZ < depths[index]) {
								int background = dst[index];
								dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[index] = nextZ;
							}
							nextZ += zStep;
							rowOffset = index + 1;
							if (nextZ < depths[rowOffset]) {
								int background = dst[rowOffset];
								dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[rowOffset] = nextZ;
							}
							z = nextZ + zStep;
							remaining--;
						} while (remaining > 0);
					}
					remaining = endX - startX & 0x3;
					if (remaining > 0) {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						do {
							rowOffset++;
							if (z < depths[rowOffset]) {
								int background = dst[rowOffset];
								dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[rowOffset] = z;
							}
							z += zStep;
							remaining--;
						} while (remaining > 0);
						return;
					}
				}
			} else {
				remaining = endX - startX;
				if (this.alpha == 0) {
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset] = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							depths[rowOffset] = z;
						}
						z += zStep;
						red += redStep;
						green += greenStep;
						blue += blueStep;
						remaining--;
					} while (remaining > 0);
				} else if (this.halfBlend) {
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							int srcColour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							int background = dst[rowOffset];
							sum = srcColour + background;
							redBlueSum = (srcColour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
							depths[rowOffset] = z;
						}
						z += zStep;
						red += redStep;
						green += greenStep;
						blue += blueStep;
						remaining--;
					} while (remaining > 0);
				} else {
					alpha = this.alpha;
					int inverseAlpha = 256 - this.alpha;
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
							colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							int background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
							depths[rowOffset] = z;
						}
						z += zStep;
						red += redStep;
						green += greenStep;
						blue += blueStep;
						remaining--;
					} while (remaining > 0);
				}
			}
		} else if (this.fastScanline) {
			remaining = endX - startX >> 2;
			redStep4 = redStep * 4.0F;
			greenStep4 = greenStep * 4.0F;
			blueStep4 = blueStep * 4.0F;
			if (this.alpha == 0) {
				if (remaining > 0) {
					do {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						red += redStep4;
						green += greenStep4;
						blue += blueStep4;
						index = rowOffset + 1;
						if (z < depths[index]) {
							dst[index] = colour;
						}
						nextZ = z + zStep;
						index++;
						if (nextZ < depths[index]) {
							dst[index] = colour;
						}
						nextZ += zStep;
						index++;
						if (nextZ < depths[index]) {
							dst[index] = colour;
						}
						nextZ += zStep;
						rowOffset = index + 1;
						if (nextZ < depths[rowOffset]) {
							dst[rowOffset] = colour;
						}
						z = nextZ + zStep;
						remaining--;
					} while (remaining > 0);
				}
				remaining = endX - startX & 0x3;
				if (remaining > 0) {
					colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset] = colour;
						}
						z += zStep;
						remaining--;
					} while (remaining > 0);
					return;
				}
			} else if (this.halfBlend) {
				if (remaining > 0) {
					do {
						colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						red += redStep4;
						green += greenStep4;
						blue += blueStep4;
						index = rowOffset + 1;
						if (z < depths[index]) {
							int background = dst[index];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
						}
						nextZ = z + zStep;
						index++;
						if (nextZ < depths[index]) {
							int background = dst[index];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
						}
						nextZ += zStep;
						index++;
						if (nextZ < depths[index]) {
							int background = dst[index];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[index] = sum - background | 0xFF000000 | background - (background >>> 8);
						}
						nextZ += zStep;
						rowOffset = index + 1;
						if (nextZ < depths[rowOffset]) {
							int background = dst[rowOffset];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
						}
						z = nextZ + zStep;
						remaining--;
					} while (remaining > 0);
				}
				remaining = endX - startX & 0x3;
				if (remaining > 0) {
					colour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							int background = dst[rowOffset];
							sum = colour + background;
							redBlueSum = (colour & 0xFF00FF) + (background & 0xFF00FF);
							background = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							dst[rowOffset] = sum - background | 0xFF000000 | background - (background >>> 8);
						}
						z += zStep;
						remaining--;
					} while (remaining > 0);
				}
			} else {
				alpha = this.alpha;
				int inverseAlpha = 256 - this.alpha;
				if (remaining > 0) {
					do {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						red += redStep4;
						green += greenStep4;
						blue += blueStep4;
						colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						index = rowOffset + 1;
						if (z < depths[index]) {
							int background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ = z + zStep;
						index++;
						if (nextZ < depths[index]) {
							int background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ += zStep;
						index++;
						if (nextZ < depths[index]) {
							int background = dst[index];
							dst[index] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						nextZ += zStep;
						rowOffset = index + 1;
						if (nextZ < depths[rowOffset]) {
							int background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						z = nextZ + zStep;
						remaining--;
					} while (remaining > 0);
				}
				remaining = endX - startX & 0x3;
				if (remaining > 0) {
					colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
					colour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
					do {
						rowOffset++;
						if (z < depths[rowOffset]) {
							int background = dst[rowOffset];
							dst[rowOffset] = colour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						z += zStep;
						remaining--;
					} while (remaining > 0);
					return;
				}
			}
		} else {
			remaining = endX - startX;
			if (this.alpha == 0) {
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						dst[rowOffset] = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
					}
					z += zStep;
					red += redStep;
					green += greenStep;
					blue += blueStep;
					remaining--;
				} while (remaining > 0);
			} else if (this.halfBlend) {
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						int srcColour = (int) red & 0xFF0000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						int background = dst[rowOffset];
						sum = srcColour + background;
						redBlueSum = (srcColour & 0xFF00FF) + (background & 0xFF00FF);
						@Pc(3112) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						dst[rowOffset] = sum - overflow | 0xFF000000 | overflow - (overflow >>> 8);
					}
					z += zStep;
					red += redStep;
					green += greenStep;
					blue += blueStep;
					remaining--;
				} while (remaining > 0);
			} else {
				alpha = this.alpha;
				int inverseAlpha = 256 - this.alpha;
				do {
					rowOffset++;
					if (z < depths[rowOffset]) {
						colour = (int) red & 0xFF0000 | 0xFF000000 | (int) green & 0xFF00 | (int) blue & 0xFF;
						@Pc(3012) int scaledColour = ((colour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						int background = dst[rowOffset];
						dst[rowOffset] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
					}
					z += zStep;
					red += redStep;
					green += greenStep;
					blue += blueStep;
					remaining--;
				} while (remaining > 0);
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFF)V")
	public void renderTriangleHslRgb(@OriginalArg(0) float yA, @OriginalArg(1) float yB, @OriginalArg(2) float yC, @OriginalArg(3) float xA, @OriginalArg(4) float xB, @OriginalArg(5) float xC, @OriginalArg(6) float zA, @OriginalArg(7) float zB, @OriginalArg(8) float zC, @OriginalArg(9) float hslA, @OriginalArg(10) float hslB, @OriginalArg(11) float hslC) {
		if (this.wireframe) {
			this.toolkit.drawOpaqueLine((int) yA, (int) xA, (int) xB, ColorUtil.hsvToRgbTable[(int) hslA], (int) yB);
			this.toolkit.drawOpaqueLine((int) yB, (int) xB, (int) xC, ColorUtil.hsvToRgbTable[(int) hslA], (int) yC);
			this.toolkit.drawOpaqueLine((int) yC, (int) xC, (int) xA, ColorUtil.hsvToRgbTable[(int) hslA], (int) yA);
			return;
		}
		@Pc(55) float deltaXBa = xB - xA;
		@Pc(59) float deltaYBa = yB - yA;
		@Pc(63) float deltaXCa = xC - xA;
		@Pc(67) float deltaYCa = yC - yA;
		@Pc(71) float deltaHslBa = hslB - hslA;
		@Pc(75) float deltaHslCa = hslC - hslA;
		@Pc(79) float deltaZBa = zB - zA;
		@Pc(83) float deltaZCa = zC - zA;
		@Pc(95) float slopeXCb;
		if (yC == yB) {
			slopeXCb = 0.0F;
		} else {
			slopeXCb = (xC - xB) / (yC - yB);
		}
		@Pc(106) float slopeXBa;
		if (yB == yA) {
			slopeXBa = 0.0F;
		} else {
			slopeXBa = deltaXBa / deltaYBa;
		}
		@Pc(117) float slopeXCa;
		if (yC == yA) {
			slopeXCa = 0.0F;
		} else {
			slopeXCa = deltaXCa / deltaYCa;
		}
		@Pc(128) float determinant = deltaXBa * deltaYCa - deltaXCa * deltaYBa;
		if (determinant == 0.0F) {
			return;
		}
		@Pc(143) float slopeHslX = (deltaHslBa * deltaYCa - deltaHslCa * deltaYBa) / determinant;
		@Pc(153) float slopeHslY = (deltaHslCa * deltaXBa - deltaHslBa * deltaXCa) / determinant;
		@Pc(163) float slopeDepthX = (deltaZBa * deltaYCa - deltaZCa * deltaYBa) / determinant;
		@Pc(173) float slopeDepthY = (deltaZCa * deltaXBa - deltaZBa * deltaXCa) / determinant;
		if (yA <= yB && yA <= yC) {
			if (!(yA >= (float) this.height)) {
				if (yB > (float) this.height) {
					yB = this.height;
				}
				if (yC > (float) this.height) {
					yC = this.height;
				}
				hslA = hslA + slopeHslX - slopeHslX * xA;
				zA = zA + slopeDepthX - slopeDepthX * xA;
				if (yB < yC) {
					xC = xA;
					if (yA < 0.0F) {
						xC = xA - slopeXCa * yA;
						xA -= slopeXBa * yA;
						hslA -= slopeHslY * yA;
						zA -= slopeDepthY * yA;
						yA = 0.0F;
					}
					if (yB < 0.0F) {
						xB -= slopeXCb * yB;
						yB = 0.0F;
					}
					if ((yA == yB || !(slopeXCa < slopeXBa)) && (yA != yB || !(slopeXCa > slopeXCb))) {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xC, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCa;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xB, (int) xC, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCa;
							xB += slopeXCb;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xA, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCa;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xB, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCa;
							xB += slopeXCb;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					}
				} else {
					xB = xA;
					if (yA < 0.0F) {
						xB = xA - slopeXCa * yA;
						xA -= slopeXBa * yA;
						hslA -= slopeHslY * yA;
						zA -= slopeDepthY * yA;
						yA = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCb * yC;
						yC = 0.0F;
					}
					if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xB, (int) xA, hslA, slopeHslX, zA, slopeDepthX);
							xB += slopeXCa;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xA, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCb;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xB, hslA, slopeHslX, zA, slopeDepthX);
							xB += slopeXCa;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xC, hslA, slopeHslX, zA, slopeDepthX);
							xC += slopeXCb;
							xA += slopeXBa;
							hslA += slopeHslY;
							zA += slopeDepthY;
							yA += this.surfaceWidth;
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= (float) this.height)) {
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yA > (float) this.height) {
					yA = this.height;
				}
				hslB = hslB + slopeHslX - slopeHslX * xB;
				zB = zB + slopeDepthX - slopeDepthX * xB;
				if (yC < yA) {
					xA = xB;
					if (yB < 0.0F) {
						xA = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						hslB -= slopeHslY * yB;
						zB -= slopeDepthY * yB;
						yB = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCa * yC;
						yC = 0.0F;
					}
					if ((yB == yC || !(slopeXBa < slopeXCb)) && (yB != yC || !(slopeXBa > slopeXCa))) {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xA, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXBa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xC, (int) xA, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXBa;
							xC += slopeXCa;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xB, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXBa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xC, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXBa;
							xC += slopeXCa;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					}
				} else {
					xC = xB;
					if (yB < 0.0F) {
						xC = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						hslB -= slopeHslY * yB;
						zB -= slopeDepthY * yB;
						yB = 0.0F;
					}
					if (yA < 0.0F) {
						xA -= slopeXCa * yA;
						yA = 0.0F;
					}
					if (slopeXBa < slopeXCb) {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xC, (int) xB, hslB, slopeHslX, zB, slopeDepthX);
							xC += slopeXBa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xB, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXCa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					} else {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xC, hslB, slopeHslX, zB, slopeDepthX);
							xC += slopeXBa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xA, hslB, slopeHslX, zB, slopeDepthX);
							xA += slopeXCa;
							xB += slopeXCb;
							hslB += slopeHslY;
							zB += slopeDepthY;
							yB += this.surfaceWidth;
						}
					}
				}
			}
		} else if (!(yC >= (float) this.height)) {
			if (yA > (float) this.height) {
				yA = this.height;
			}
			if (yB > (float) this.height) {
				yB = this.height;
			}
			hslC = hslC + slopeHslX - slopeHslX * xC;
			zC = zC + slopeDepthX - slopeDepthX * xC;
			if (yA < yB) {
				xB = xC;
				if (yC < 0.0F) {
					xB = xC - slopeXCb * yC;
					xC -= slopeXCa * yC;
					hslC -= slopeHslY * yC;
					zC -= slopeDepthY * yC;
					yC = 0.0F;
				}
				if (yA < 0.0F) {
					xA -= slopeXBa * yA;
					yA = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xC, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXCb;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xA, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXCb;
						xA += slopeXBa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				} else {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xB, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXCb;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xA, (int) xB, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXCb;
						xA += slopeXBa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				}
			} else {
				xA = xC;
				if (yC < 0.0F) {
					xA = xC - slopeXCb * yC;
					xC -= slopeXCa * yC;
					hslC -= slopeHslY * yC;
					zC -= slopeDepthY * yC;
					yC = 0.0F;
				}
				if (yB < 0.0F) {
					xB -= slopeXBa * yB;
					yB = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xA, (int) xC, hslC, slopeHslX, zC, slopeDepthX);
						xA += slopeXCb;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xC, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXBa;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				} else {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xA, hslC, slopeHslX, zC, slopeDepthX);
						xA += slopeXCb;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawHslSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xB, hslC, slopeHslX, zC, slopeDepthX);
						xB += slopeXBa;
						xC += slopeXCa;
						hslC += slopeHslY;
						zC += slopeDepthY;
						yC += this.surfaceWidth;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(Z)V")
	public void setWireframe(@OriginalArg(0) boolean wireframe) {
		this.wireframe = wireframe;
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFFFFFFFIIIIIIII)V")
	public void renderTexturedTriangleRgb(@OriginalArg(0) float yA, @OriginalArg(1) float yB, @OriginalArg(2) float yC, @OriginalArg(3) float xA, @OriginalArg(4) float xB, @OriginalArg(5) float xC, @OriginalArg(6) float zA, @OriginalArg(7) float zB, @OriginalArg(8) float zC, @OriginalArg(9) float uA, @OriginalArg(10) float uB, @OriginalArg(11) float uC, @OriginalArg(12) float vA, @OriginalArg(13) float vB, @OriginalArg(14) float vC, @OriginalArg(15) int colourA, @OriginalArg(16) int colourB, @OriginalArg(17) int colourC, @OriginalArg(18) int fogColour, @OriginalArg(19) int fogA, @OriginalArg(20) int fogB, @OriginalArg(21) int fogC, @OriginalArg(22) int texture) {
		if (texture != this.textureId) {
			this.texels = this.toolkit.getArgbTexture(texture);
			if (this.texels == null) {
				this.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) zA, (float) zB, (float) zC, ColorUtil.alphaBlend(colourA, fogColour | fogA << 24), ColorUtil.alphaBlend(colourB, fogColour | fogB << 24), ColorUtil.alphaBlend(colourC, fogColour | fogC << 24));
				return;
			}
			this.textureSize = this.toolkit.smallTexture(texture) ? 64 : this.toolkit.textureSize;
			this.textureMask = this.textureSize - 1;
			this.textureBlendMode = this.toolkit.textureAlphaBlendMode(texture);
			this.textureRepeats = this.toolkit.textureRepeats(texture);
		}
		this.fogColour = fogColour;
		@Pc(106) float alphaA = (float) (colourA >> 24 & 0xFF);
		@Pc(113) float alphaB = (float) (colourB >> 24 & 0xFF);
		@Pc(120) float alphaC = (float) (colourC >> 24 & 0xFF);
		@Pc(127) float redA = (float) (colourA >> 16 & 0xFF);
		@Pc(134) float redB = (float) (colourB >> 16 & 0xFF);
		@Pc(141) float redC = (float) (colourC >> 16 & 0xFF);
		@Pc(148) float greenA = (float) (colourA >> 8 & 0xFF);
		@Pc(155) float greenB = (float) (colourB >> 8 & 0xFF);
		@Pc(162) float greenC = (float) (colourC >> 8 & 0xFF);
		@Pc(167) float blueA = (float) (colourA & 0xFF);
		@Pc(172) float blueB = (float) (colourB & 0xFF);
		@Pc(177) float blueC = (float) (colourC & 0xFF);
		uA /= zA;
		uB /= zB;
		uC /= zC;
		vA /= zA;
		vB /= zB;
		vC /= zC;
		zA = 1.0F / zA;
		zB = 1.0F / zB;
		zC = 1.0F / zC;
		@Pc(215) float slopeXBa = 0.0F;
		@Pc(217) float slopeZBa = 0.0F;
		@Pc(219) float slopeUBa = 0.0F;
		@Pc(221) float slopeVBa = 0.0F;
		@Pc(223) float slopeFogBa = 0.0F;
		@Pc(225) float slopeAlphaBa = 0.0F;
		@Pc(227) float slopeRedBa = 0.0F;
		@Pc(229) float slopeGreenBa = 0.0F;
		@Pc(231) float slopeBlueBa = 0.0F;
		@Pc(239) float local239;
		if (yB != yA) {
			float deltaYBa = yB - yA;
			slopeXBa = (xB - xA) / deltaYBa;
			slopeZBa = (zB - zA) / deltaYBa;
			slopeUBa = (uB - uA) / deltaYBa;
			slopeVBa = (vB - vA) / deltaYBa;
			slopeFogBa = (float) (fogB - fogA) / deltaYBa;
			slopeAlphaBa = (alphaB - alphaA) / deltaYBa;
			slopeRedBa = (redB - redA) / deltaYBa;
			slopeGreenBa = (greenB - greenA) / deltaYBa;
			slopeBlueBa = (blueB - blueA) / deltaYBa;
		}
		float slopeXCb = 0.0F;
		@Pc(298) float slopeZCb = 0.0F;
		@Pc(300) float slopeUCb = 0.0F;
		@Pc(302) float slopeVCb = 0.0F;
		@Pc(304) float slopeFogCb = 0.0F;
		@Pc(306) float slopeAlphaCb = 0.0F;
		@Pc(308) float slopeRedCb = 0.0F;
		@Pc(310) float slopeGreenCb = 0.0F;
		@Pc(312) float slopeBlueCb = 0.0F;
		@Pc(320) float local320;
		if (yC != yB) {
			float deltaYCb = yC - yB;
			slopeXCb = (xC - xB) / deltaYCb;
			slopeZCb = (zC - zB) / deltaYCb;
			slopeUCb = (uC - uB) / deltaYCb;
			slopeVCb = (vC - vB) / deltaYCb;
			slopeFogCb = (float) (fogC - fogB) / deltaYCb;
			slopeAlphaCb = (alphaC - alphaB) / deltaYCb;
			slopeRedCb = (redC - redB) / deltaYCb;
			slopeGreenCb = (greenC - greenB) / deltaYCb;
			slopeBlueCb = (blueC - blueB) / deltaYCb;
		}
		float slopeXCa = 0.0F;
		@Pc(379) float slopeZCa = 0.0F;
		@Pc(381) float slopeUCa = 0.0F;
		@Pc(383) float slopeVCa = 0.0F;
		@Pc(385) float slopeFogCa = 0.0F;
		@Pc(387) float slopeAlphaCa = 0.0F;
		@Pc(389) float slopeRedCa = 0.0F;
		@Pc(391) float slopeGreenCa = 0.0F;
		@Pc(393) float slopeBlueCa = 0.0F;
		if (yA != yC) {
			@Pc(401) float deltaYAc = yA - yC;
			slopeXCa = (xA - xC) / deltaYAc;
			slopeZCa = (zA - zC) / deltaYAc;
			slopeUCa = (uA - uC) / deltaYAc;
			slopeVCa = (vA - vC) / deltaYAc;
			slopeFogCa = (float) (fogA - fogC) / deltaYAc;
			slopeAlphaCa = (alphaA - alphaC) / deltaYAc;
			slopeRedCa = (redA - redC) / deltaYAc;
			slopeGreenCa = (greenA - greenC) / deltaYAc;
			slopeBlueCa = (blueA - blueC) / deltaYAc;
		}
		if (yA <= yB && yA <= yC) {
			if (!(yA >= (float) this.height)) {
				if (yB > (float) this.height) {
					yB = this.height;
				}
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yB < yC) {
					xC = xA;
					zC = zA;
					uC = uA;
					vC = vA;
					fogC = fogA;
					alphaC = alphaA;
					redC = redA;
					greenC = greenA;
					blueC = blueA;
					if (yA < 0.0F) {
						xA -= slopeXBa * yA;
						xC -= slopeXCa * yA;
						zA -= slopeZBa * yA;
						zC -= slopeZCa * yA;
						uA -= slopeUBa * yA;
						uC -= slopeUCa * yA;
						vA -= slopeVBa * yA;
						vC -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogBa * yA);
						fogC = (int) ((float) fogC - slopeFogCa * yA);
						alphaA -= slopeAlphaBa * yA;
						alphaC -= slopeAlphaCa * yA;
						redA -= slopeAlphaBa * yA;
						redC -= slopeAlphaCa * yA;
						greenA -= slopeAlphaBa * yA;
						greenC -= slopeAlphaCa * yA;
						blueA -= slopeAlphaBa * yA;
						blueC -= slopeAlphaCa * yA;
						yA = 0.0F;
					}
					if (yB < 0.0F) {
						xB -= slopeXCb * yB;
						zB -= slopeZCb * yB;
						uB -= slopeUCb * yB;
						vB -= slopeVCb * yB;
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaB -= slopeAlphaCb * yB;
						redB -= slopeRedCb * yB;
						greenB -= slopeGreenCb * yB;
						blueB -= slopeBlueCb * yB;
						yB = 0.0F;
					}
					if (yA != yB && slopeXCa < slopeXBa || yA == yB && slopeXCa > slopeXCb) {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB);
							xB += slopeXCb;
							xC += slopeXCa;
							zB += slopeZCb;
							zC += slopeZCa;
							uB += slopeUCb;
							uC += slopeUCa;
							vB += slopeVCb;
							vC += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaCa;
							redB += slopeRedCb;
							redC += slopeRedCa;
							greenB += slopeGreenCb;
							greenC += slopeGreenCa;
							blueB += slopeBlueCb;
							blueC += slopeBlueCa;
							yA += this.surfaceWidth;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC);
							xB += slopeXCb;
							xC += slopeXCa;
							zB += slopeZCb;
							zC += slopeZCa;
							uB += slopeUCb;
							uC += slopeUCa;
							vB += slopeVCb;
							vC += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaCa;
							redB += slopeRedCb;
							redC += slopeRedCa;
							greenB += slopeGreenCb;
							greenC += slopeGreenCa;
							blueB += slopeBlueCb;
							blueC += slopeBlueCa;
							yA += this.surfaceWidth;
						}
					}
				} else {
					xB = xA;
					zB = zA;
					uB = uA;
					vB = vA;
					fogB = fogA;
					alphaB = alphaA;
					redB = redA;
					greenB = greenA;
					blueB = blueA;
					if (yA < 0.0F) {
						xA -= slopeXBa * yA;
						xB -= slopeXCa * yA;
						zA -= slopeZBa * yA;
						zB -= slopeZCa * yA;
						uA -= slopeUBa * yA;
						uB -= slopeUCa * yA;
						vA -= slopeVBa * yA;
						vB -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogBa * yA);
						fogB = (int) ((float) fogB - slopeFogCa * yA);
						alphaA -= slopeAlphaBa * yA;
						alphaB -= slopeAlphaCa * yA;
						redA -= slopeAlphaBa * yA;
						redB -= slopeAlphaCa * yA;
						greenA -= slopeAlphaBa * yA;
						greenB -= slopeAlphaCa * yA;
						blueA -= slopeAlphaBa * yA;
						blueB -= slopeAlphaCa * yA;
						yA = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCb * yC;
						zC -= slopeZCb * yC;
						uC -= slopeUCb * yC;
						vC -= slopeVCb * yC;
						fogC = (int) ((float) fogC - slopeFogCb * yC);
						alphaC -= slopeAlphaCb * yC;
						redC -= slopeRedCb * yC;
						greenC -= slopeGreenCb * yC;
						blueC -= slopeBlueCb * yC;
						yC = 0.0F;
					}
					if (yA != yC && slopeXCa < slopeXBa || yA == yC && slopeXCb > slopeXBa) {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA);
							xA += slopeXBa;
							xB += slopeXCa;
							zA += slopeZBa;
							zB += slopeZCa;
							uA += slopeUBa;
							uB += slopeUCa;
							vA += slopeVBa;
							vB += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaB += slopeAlphaCa;
							redA += slopeRedBa;
							redB += slopeRedCa;
							greenA += slopeGreenBa;
							greenB += slopeGreenCa;
							blueA += slopeBlueBa;
							blueB += slopeBlueCa;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA);
							xC += slopeXCb;
							xA += slopeXBa;
							zC += slopeZCb;
							zA += slopeZBa;
							uC += slopeUCb;
							uA += slopeUBa;
							vC += slopeVCb;
							vA += slopeVBa;
							fogC = (int) ((float) fogC + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaC += slopeAlphaCb;
							alphaA += slopeAlphaBa;
							redC += slopeRedCb;
							redA += slopeRedBa;
							greenC += slopeGreenCb;
							greenA += slopeGreenBa;
							blueC += slopeBlueCb;
							blueA += slopeBlueBa;
							yA += this.surfaceWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB);
							xB += slopeXCa;
							xA += slopeXBa;
							zB += slopeZCa;
							zA += slopeZBa;
							uB += slopeUCa;
							uA += slopeUBa;
							vB += slopeVCa;
							vA += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCa);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaB += slopeAlphaCa;
							alphaA += slopeAlphaBa;
							redB += slopeRedCa;
							redA += slopeRedBa;
							greenB += slopeGreenCa;
							greenA += slopeGreenBa;
							blueB += slopeBlueCa;
							blueA += slopeBlueBa;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yA, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC);
							xA += slopeXBa;
							xC += slopeXCb;
							zA += slopeZBa;
							zC += slopeZCb;
							uA += slopeUBa;
							uC += slopeUCb;
							vA += slopeVBa;
							vC += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCb);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCb;
							redA += slopeRedBa;
							redC += slopeRedCb;
							greenA += slopeGreenBa;
							greenC += slopeGreenCb;
							blueA += slopeBlueBa;
							blueC += slopeBlueCb;
							yA += this.surfaceWidth;
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= (float) this.height)) {
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yA > (float) this.height) {
					yA = this.height;
				}
				if (yC < yA) {
					xA = xB;
					zA = zB;
					uA = uB;
					vA = vB;
					fogA = fogB;
					alphaA = alphaB;
					redA = redB;
					greenA = greenB;
					blueA = blueB;
					if (yB < 0.0F) {
						xA = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						zA = zB - slopeZBa * yB;
						zB -= slopeZCb * yB;
						uA = uB - slopeUBa * yB;
						uB -= slopeUCb * yB;
						vA = vB - slopeVBa * yB;
						vB -= slopeVCb * yB;
						fogA = (int) ((float) fogB - slopeFogBa * yB);
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaA = alphaB - slopeAlphaBa * yB;
						alphaB -= slopeAlphaCb * yB;
						redA = redB - slopeRedBa * yB;
						redB -= slopeRedCb * yB;
						greenA = greenB - slopeGreenBa * yB;
						greenB -= slopeGreenCb * yB;
						blueA = blueB - slopeBlueBa * yB;
						blueB -= slopeBlueCb * yB;
						yB = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= slopeXCa * yC;
						zC -= slopeZCa * yC;
						uC -= slopeUCa * yC;
						vC -= slopeVCa * yC;
						fogC = (int) ((float) fogC - slopeFogCa * yC);
						alphaC -= slopeAlphaCa * yC;
						redC -= slopeRedCa * yC;
						greenC -= slopeGreenCa * yC;
						blueC -= slopeBlueCa * yC;
						yC = 0.0F;
					}
					if ((yB == yC || !(slopeXBa < slopeXCb)) && (yB != yC || !(slopeXBa > slopeXCa))) {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA);
							xB += slopeXCb;
							xA += slopeXBa;
							zB += slopeZCb;
							zA += slopeZBa;
							uB += slopeUCb;
							uA += slopeUBa;
							vB += slopeVCb;
							vA += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaB += slopeAlphaCb;
							alphaA += slopeAlphaBa;
							redB += slopeRedCb;
							redA += slopeRedBa;
							greenB += slopeGreenCb;
							greenA += slopeGreenBa;
							blueB += slopeBlueCb;
							blueA += slopeBlueBa;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA);
							xC += slopeXCa;
							xA += slopeXBa;
							zC += slopeZCa;
							zA += slopeZBa;
							uC += slopeUCa;
							uA += slopeUBa;
							vC += slopeVCa;
							vA += slopeVBa;
							fogC = (int) ((float) fogC + slopeFogCa);
							fogA = (int) ((float) fogA + slopeFogBa);
							alphaC += slopeAlphaCa;
							alphaA += slopeAlphaBa;
							redC += slopeRedCa;
							redA += slopeRedBa;
							greenC += slopeGreenCa;
							greenA += slopeGreenBa;
							blueC += slopeBlueCa;
							blueA += slopeBlueBa;
							yB += this.surfaceWidth;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB);
							xA += slopeXBa;
							xB += slopeXCb;
							zA += slopeZBa;
							zB += slopeZCb;
							uA += slopeUBa;
							uB += slopeUCb;
							vA += slopeVBa;
							vB += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaA += slopeAlphaBa;
							alphaB += slopeAlphaCb;
							redA += slopeRedBa;
							redB += slopeRedCb;
							greenA += slopeGreenBa;
							greenB += slopeGreenCb;
							blueA += slopeBlueBa;
							blueB += slopeBlueCb;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC);
							xA += slopeXBa;
							xC += slopeXCa;
							zA += slopeZBa;
							zC += slopeZCa;
							uA += slopeUBa;
							uC += slopeUCa;
							vA += slopeVBa;
							vC += slopeVCa;
							fogA = (int) ((float) fogA + slopeFogBa);
							fogC = (int) ((float) fogC + slopeFogCa);
							alphaA += slopeAlphaBa;
							alphaC += slopeAlphaCa;
							redA += slopeRedBa;
							redC += slopeRedCa;
							greenA += slopeGreenBa;
							greenC += slopeGreenCa;
							blueA += slopeBlueBa;
							blueC += slopeBlueCa;
							yB += this.surfaceWidth;
						}
					}
				} else {
					xC = xB;
					zC = zB;
					uC = uB;
					vC = vB;
					fogC = fogB;
					alphaC = alphaB;
					redC = redB;
					greenC = greenB;
					blueC = blueB;
					if (yB < 0.0F) {
						xC = xB - slopeXBa * yB;
						xB -= slopeXCb * yB;
						zC = zB - slopeZBa * yB;
						zB -= slopeZCb * yB;
						uC = uB - slopeUBa * yB;
						uB -= slopeUCb * yB;
						vC = vB - slopeVBa * yB;
						vB -= slopeVCb * yB;
						fogC = (int) ((float) fogB - slopeFogBa * yB);
						fogB = (int) ((float) fogB - slopeFogCb * yB);
						alphaC = alphaB - slopeAlphaBa * yB;
						alphaB -= slopeAlphaCb * yB;
						redC = redB - slopeRedBa * yB;
						redB -= slopeRedCb * yB;
						greenC = greenB - slopeGreenBa * yB;
						greenB -= slopeGreenCb * yB;
						blueC = blueB - slopeBlueBa * yB;
						blueB -= slopeBlueCb * yB;
						yB = 0.0F;
					}
					if (yA < 0.0F) {
						xA -= slopeXCa * yA;
						zA -= slopeZCa * yA;
						uA -= slopeUCa * yA;
						vA -= slopeVCa * yA;
						fogA = (int) ((float) fogA - slopeFogCa * yA);
						alphaA -= slopeAlphaCa * yA;
						redA -= slopeRedCa * yA;
						greenA -= slopeGreenCa * yA;
						blueA -= slopeBlueCa * yA;
						yA = 0.0F;
					}
					yC -= yA;
					yA -= yB;
					yB = this.lineOffsets[(int) yB];
					if (slopeXBa < slopeXCb) {
						while (--yA >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB);
							xC += slopeXBa;
							xB += slopeXCb;
							zC += slopeZBa;
							zB += slopeZCb;
							uC += slopeUBa;
							uB += slopeUCb;
							vC += slopeVBa;
							vB += slopeVCb;
							fogC = (int) ((float) fogC + slopeFogBa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaC += slopeAlphaBa;
							alphaB += slopeAlphaCb;
							redC += slopeRedBa;
							redB += slopeRedCb;
							greenC += slopeGreenBa;
							greenB += slopeGreenCb;
							blueC += slopeBlueBa;
							blueB += slopeBlueCb;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB);
							xA += slopeXCa;
							xB += slopeXCb;
							zA += slopeZCa;
							zB += slopeZCb;
							uA += slopeUCa;
							uB += slopeUCb;
							vA += slopeVCa;
							vB += slopeVCb;
							fogA = (int) ((float) fogA + slopeFogCa);
							fogB = (int) ((float) fogB + slopeFogCb);
							alphaA += slopeAlphaCa;
							alphaB += slopeAlphaCb;
							redA += slopeRedCa;
							redB += slopeRedCb;
							greenA += slopeGreenCa;
							greenB += slopeGreenCb;
							blueA += slopeBlueCa;
							blueB += slopeBlueCb;
							yB += this.surfaceWidth;
						}
					} else {
						while (--yA >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC);
							xB += slopeXCb;
							xC += slopeXBa;
							zB += slopeZCb;
							zC += slopeZBa;
							uB += slopeUCb;
							uC += slopeUBa;
							vB += slopeVCb;
							vC += slopeVBa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogC = (int) ((float) fogC + slopeFogBa);
							alphaB += slopeAlphaCb;
							alphaC += slopeAlphaBa;
							redB += slopeRedCb;
							redC += slopeRedBa;
							greenB += slopeGreenCb;
							greenC += slopeGreenBa;
							blueB += slopeBlueCb;
							blueC += slopeBlueBa;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawTexturedSpanRgb(this.raster, this.texels, (int) yB, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA);
							xB += slopeXCb;
							xA += slopeXCa;
							zB += slopeZCb;
							zA += slopeZCa;
							uB += slopeUCb;
							uA += slopeUCa;
							vB += slopeVCb;
							vA += slopeVCa;
							fogB = (int) ((float) fogB + slopeFogCb);
							fogA = (int) ((float) fogA + slopeFogCa);
							alphaB += slopeAlphaCb;
							alphaA += slopeAlphaCa;
							redB += slopeRedCb;
							redA += slopeRedCa;
							greenB += slopeGreenCb;
							greenA += slopeGreenCa;
							blueB += slopeBlueCb;
							blueA += slopeBlueCa;
							yB += this.surfaceWidth;
						}
					}
				}
			}
		} else if (!(yC >= (float) this.height)) {
			if (yA > (float) this.height) {
				yA = this.height;
			}
			if (yB > (float) this.height) {
				yB = this.height;
			}
			if (yA < yB) {
				xB = xC;
				zB = zC;
				uB = uC;
				vB = vC;
				fogB = fogC;
				alphaB = alphaC;
				redB = redC;
				greenB = greenC;
				blueB = blueC;
				if (yC < 0.0F) {
					xC -= slopeXCa * yC;
					xB -= slopeXCb * yC;
					zC -= slopeZCa * yC;
					zB -= slopeZCb * yC;
					uC -= slopeUCa * yC;
					uB -= slopeUCb * yC;
					vC -= slopeVCa * yC;
					vB -= slopeVCb * yC;
					fogC = (int) ((float) fogC - slopeFogCa * 3.0F);
					fogB = (int) ((float) fogB - slopeFogCb * yC);
					alphaC -= slopeAlphaCa * yC;
					alphaB -= slopeAlphaCb * yC;
					redC -= slopeRedCa * yC;
					redB -= slopeRedCb * yC;
					greenC -= slopeGreenCa * yC;
					greenB -= slopeGreenCb * yC;
					blueC -= slopeBlueCa * yC;
					blueB -= slopeBlueCb * yC;
					yC = 0.0F;
				}
				if (yA < 0.0F) {
					xA -= slopeXBa * yA;
					zA -= slopeZBa * yA;
					uA -= slopeUBa * yA;
					vA -= slopeVBa * yA;
					fogA = (int) ((float) fogA - slopeFogBa * yA);
					alphaA -= slopeAlphaBa * yA;
					redA -= slopeRedBa * yA;
					greenA -= slopeGreenBa * yA;
					blueA -= slopeBlueBa * yA;
					yA = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC);
						xB += slopeXCb;
						xC += slopeXCa;
						zB += slopeZCb;
						zC += slopeZCa;
						uB += slopeUCb;
						uC += slopeUCa;
						vB += slopeVCb;
						vC += slopeVCa;
						fogB = (int) ((float) fogB + slopeFogCb);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaB += slopeAlphaCb;
						alphaC += slopeAlphaCa;
						redB += slopeRedCb;
						redC += slopeRedCa;
						greenB += slopeGreenCb;
						greenC += slopeGreenCa;
						blueB += slopeBlueCb;
						blueC += slopeBlueCa;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xB, (int) xA, zB, zA, uB, uA, vB, vA, (float) fogB, (float) fogA, alphaB, alphaA, redB, redA, greenB, greenA, blueB, blueA);
						xB += slopeXCb;
						xA += slopeXBa;
						zB += slopeZCb;
						zA += slopeZBa;
						uB += slopeUCb;
						uA += slopeUBa;
						vB += slopeVCb;
						vA += slopeVBa;
						fogB = (int) ((float) fogB + slopeFogCb);
						fogA = (int) ((float) fogA + slopeFogBa);
						alphaB += slopeAlphaCb;
						alphaA += slopeAlphaBa;
						redB += slopeRedCb;
						redA += slopeRedBa;
						greenB += slopeGreenCb;
						greenA += slopeGreenBa;
						blueB += slopeBlueCb;
						blueA += slopeBlueBa;
						yC += this.surfaceWidth;
					}
				} else {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB);
						xC += slopeXCa;
						xB += slopeXCb;
						zC += slopeZCa;
						zB += slopeZCb;
						uC += slopeUCa;
						uB += slopeUCb;
						vC += slopeVCa;
						vB += slopeVCb;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogB = (int) ((float) fogB + slopeFogCb);
						alphaC += slopeAlphaCa;
						alphaB += slopeAlphaCb;
						redC += slopeRedCa;
						redB += slopeRedCb;
						greenC += slopeGreenCa;
						greenB += slopeGreenCb;
						blueC += slopeBlueCa;
						blueB += slopeBlueCb;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xA, (int) xB, zA, zB, uA, uB, vA, vB, (float) fogA, (float) fogB, alphaA, alphaB, redA, redB, greenA, greenB, blueA, blueB);
						xA += slopeXBa;
						xB += slopeXCb;
						zA += slopeZBa;
						zB += slopeZCb;
						uA += slopeUBa;
						uB += slopeUCb;
						vA += slopeVBa;
						vB += slopeVCb;
						fogA = (int) ((float) fogA + slopeFogBa);
						fogB = (int) ((float) fogB + slopeFogCb);
						alphaA += slopeAlphaBa;
						alphaB += slopeAlphaCb;
						redA += slopeRedBa;
						redB += slopeRedCb;
						greenA += slopeGreenBa;
						greenB += slopeGreenCb;
						blueA += slopeBlueBa;
						blueB += slopeBlueCb;
						yC += this.surfaceWidth;
					}
				}
			} else {
				xA = xC;
				zA = zC;
				uA = uC;
				vA = vC;
				fogA = fogC;
				alphaA = alphaC;
				redA = redC;
				greenA = greenC;
				blueA = blueC;
				if (yC < 0.0F) {
					xC -= slopeXCa * yC;
					xA -= slopeXCb * yC;
					zC -= slopeZCa * yC;
					zA -= slopeZCb * yC;
					uC -= slopeUCa * yC;
					uA -= slopeUCb * yC;
					vC -= slopeVCa * yC;
					vA -= slopeVCb * yC;
					fogC = (int) ((float) fogC - slopeFogCa * 3.0F);
					fogA = (int) ((float) fogA - slopeFogCb * yC);
					alphaC -= slopeAlphaCa * yC;
					alphaA -= slopeAlphaCb * yC;
					redC -= slopeRedCa * yC;
					redA -= slopeRedCb * yC;
					greenC -= slopeGreenCa * yC;
					greenA -= slopeGreenCb * yC;
					blueC -= slopeBlueCa * yC;
					blueA -= slopeBlueCb * yC;
					yC = 0.0F;
				}
				if (yB < 0.0F) {
					xB -= slopeXBa * yB;
					zB -= slopeZBa * yB;
					uB -= slopeUBa * yB;
					vB -= slopeVBa * yB;
					fogB = (int) ((float) fogB - slopeFogBa * yB);
					alphaB -= slopeAlphaBa * yB;
					redB -= slopeRedBa * yB;
					greenB -= slopeGreenBa * yB;
					blueB -= slopeBlueBa * yB;
					yB = 0.0F;
				}
				if (slopeXCb < slopeXCa) {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xA, (int) xC, zA, zC, uA, uC, vA, vC, (float) fogA, (float) fogC, alphaA, alphaC, redA, redC, greenA, greenC, blueA, blueC);
						xA += slopeXCb;
						xC += slopeXCa;
						zA += slopeZCb;
						zC += slopeZCa;
						uA += slopeUCb;
						uC += slopeUCa;
						vA += slopeVCb;
						vC += slopeVCa;
						fogA = (int) ((float) fogA + slopeFogCb);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaA += slopeAlphaCb;
						alphaC += slopeAlphaCa;
						redA += slopeRedCb;
						redC += slopeRedCa;
						greenA += slopeGreenCb;
						greenC += slopeGreenCa;
						blueA += slopeBlueCb;
						blueC += slopeBlueCa;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xB, (int) xC, zB, zC, uB, uC, vB, vC, (float) fogB, (float) fogC, alphaB, alphaC, redB, redC, greenB, greenC, blueB, blueC);
						xB += slopeXBa;
						xC += slopeXCa;
						zB += slopeZBa;
						zC += slopeZCa;
						uB += slopeUBa;
						uC += slopeUCa;
						vB += slopeVBa;
						vC += slopeVCa;
						fogB = (int) ((float) fogB + slopeFogBa);
						fogC = (int) ((float) fogC + slopeFogCa);
						alphaB += slopeAlphaBa;
						alphaC += slopeAlphaCa;
						redB += slopeRedBa;
						redC += slopeRedCa;
						greenB += slopeGreenBa;
						greenC += slopeGreenCa;
						blueB += slopeBlueBa;
						blueC += slopeBlueCa;
						yC += this.surfaceWidth;
					}
				} else {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xC, (int) xA, zC, zA, uC, uA, vC, vA, (float) fogC, (float) fogA, alphaC, alphaA, redC, redA, greenC, greenA, blueC, blueA);
						xC += slopeXCa;
						xA += slopeXCb;
						zC += slopeZCa;
						zA += slopeZCb;
						uC += slopeUCa;
						uA += slopeUCb;
						vC += slopeVCa;
						vA += slopeVCb;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogA = (int) ((float) fogA + slopeFogCb);
						alphaC += slopeAlphaCa;
						alphaA += slopeAlphaCb;
						redC += slopeRedCa;
						redA += slopeRedCb;
						greenC += slopeGreenCa;
						greenA += slopeGreenCb;
						blueC += slopeBlueCa;
						blueA += slopeBlueCb;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawTexturedSpanRgb(this.raster, this.texels, (int) yC, (int) xC, (int) xB, zC, zB, uC, uB, vC, vB, (float) fogC, (float) fogB, alphaC, alphaB, redC, redB, greenC, greenB, blueC, blueB);
						xC += slopeXCa;
						xB += slopeXBa;
						zC += slopeZCa;
						zB += slopeZBa;
						uC += slopeUCa;
						uB += slopeUBa;
						vC += slopeVCa;
						vB += slopeVBa;
						fogC = (int) ((float) fogC + slopeFogCa);
						fogB = (int) ((float) fogB + slopeFogBa);
						alphaC += slopeAlphaCa;
						alphaB += slopeAlphaBa;
						redC += slopeRedCa;
						redB += slopeRedBa;
						greenC += slopeGreenCa;
						greenB += slopeGreenBa;
						blueC += slopeBlueCa;
						blueB += slopeBlueBa;
						yC += this.surfaceWidth;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[IIIIFFFFFFFFFFFFFFFFFFFF)V")
	private void drawBlendedTexturedSpan(@OriginalArg(0) int[] dst, @OriginalArg(1) int[] texels, @OriginalArg(2) int rowOffset, @OriginalArg(3) int startX, @OriginalArg(4) int endX, @OriginalArg(5) float invZLeft, @OriginalArg(6) float invZRight, @OriginalArg(7) float uLeft, @OriginalArg(8) float uRight, @OriginalArg(9) float vLeft, @OriginalArg(10) float vRight, @OriginalArg(11) float fogLeft, @OriginalArg(12) float fogRight, @OriginalArg(13) float alphaLeft, @OriginalArg(14) float alphaRight, @OriginalArg(15) float redLeft, @OriginalArg(16) float redRight, @OriginalArg(17) float greenLeft, @OriginalArg(18) float greenRight, @OriginalArg(19) float blueLeft, @OriginalArg(20) float blueRight, @OriginalArg(21) float weightLeft, @OriginalArg(22) float weightRight, @OriginalArg(23) float weight2Left, @OriginalArg(24) float weight2Right) {
		@Pc(3) int lengthX = endX - startX;
		@Pc(8) float scaleX = 1.0F / (float) lengthX;
		@Pc(14) float slopeInvZ = (invZRight - invZLeft) * scaleX;
		@Pc(20) float slopeU = (uRight - uLeft) * scaleX;
		@Pc(26) float slopeV = (vRight - vLeft) * scaleX;
		@Pc(32) float slopeFog = (fogRight - fogLeft) * scaleX;
		@Pc(38) float slopeRed = (redRight - redLeft) * scaleX;
		@Pc(44) float slopeGreen = (greenRight - greenLeft) * scaleX;
		@Pc(50) float slopeBlue = (blueRight - blueLeft) * scaleX;
		@Pc(56) float slopeWeight = (weightRight - weightLeft) * scaleX;
		@Pc(62) float slopeWeight2 = (weight2Right - weight2Left) * scaleX;
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width;
			}
			if (startX < 0) {
				invZLeft -= slopeInvZ * (float) startX;
				uLeft -= slopeU * (float) startX;
				vLeft -= slopeV * (float) startX;
				fogLeft -= slopeFog * (float) startX;
				redLeft -= slopeRed * (float) startX;
				greenLeft -= slopeGreen * (float) startX;
				blueLeft -= slopeBlue * (float) startX;
				weightLeft -= slopeWeight * (float) startX;
				weight2Left -= slopeWeight2 * (float) startX;
				startX = 0;
			}
		}
		if (startX >= endX) {
			return;
		}
		lengthX = endX - startX;
		@Pc(151) int pixelIndex = rowOffset + startX;
		while (lengthX-- > 0) {
			@Pc(156) float depth = 1.0F / invZLeft;
			if (depth < this.depthBuffer[pixelIndex]) {
				@Pc(167) float u = uLeft * depth;
				@Pc(171) float v = vLeft * depth;
				@Pc(184) int texU = (int) (u * (float) this.textureSize * this.textureScale) & this.textureMask;
				@Pc(197) int texV = (int) (v * (float) this.textureSize * this.textureScale) & this.textureMask;
				@Pc(207) int texel = this.texels[texV * this.textureSize + texU];
				@Pc(220) int texU2 = (int) (u * (float) this.textureSize2 * this.textureScale2) & this.textureMask2;
				@Pc(233) int texV2 = (int) (v * (float) this.textureSize2 * this.textureScale2) & this.textureMask2;
				@Pc(243) int texel2 = this.texels2[texV2 * this.textureSize2 + texU2];
				@Pc(256) int texU3 = (int) (u * (float) this.textureSize3 * this.textureScale3) & this.textureMask3;
				@Pc(269) int texV3 = (int) (v * (float) this.textureSize3 * this.textureScale3) & this.textureMask3;
				@Pc(279) int texel3 = this.texels3[texV3 * this.textureSize3 + texU3];
				@Pc(285) float weight3 = 1.0F - weightLeft - weight2Left;
				@Pc(319) int weighted = ((int) (weightLeft * (float) (texel >> 16 & 0xFF)) | 0xFF00) << 16 | (int) (weightLeft * (float) (texel >> 8 & 0xFF)) << 8 | (int) (weightLeft * (float) (texel & 0xFF));
				@Pc(353) int weighted2 = ((int) (weight2Left * (float) (texel2 >> 16 & 0xFF)) | 0xFF00) << 16 | (int) (weight2Left * (float) (texel2 >> 8 & 0xFF)) << 8 | (int) (weight2Left * (float) (texel2 & 0xFF));
				@Pc(387) int weighted3 = ((int) (weight3 * (float) (texel3 >> 16 & 0xFF)) | 0xFF00) << 16 | (int) (weight3 * (float) (texel3 >> 8 & 0xFF)) << 8 | (int) (weight3 * (float) (texel3 & 0xFF));
				@Pc(393) int blendedTexel = weighted + weighted2 + weighted3;
				@Pc(431) int colour = ((int) (redLeft * (float) (blendedTexel >> 16 & 0xFF)) & 0xFF00 | 0xFF0000) << 8 | (int) (greenLeft * (float) (blendedTexel >> 8 & 0xFF)) & 0xFF00 | (int) (blueLeft * (float) (blendedTexel & 0xFF)) >> 8;
				if (fogLeft != 0.0F) {
					@Pc(440) int invFog = (int) (255.0F - fogLeft);
					@Pc(462) int fogTint = ((this.fogColour & 0xFF00FF) * (int) fogLeft & 0xFF00FF00 | (this.fogColour & 0xFF00) * (int) fogLeft & 0xFF0000) >>> 8;
					colour = (((colour & 0xFF00FF) * invFog & 0xFF00FF00 | (colour & 0xFF00) * invFog & 0xFF0000) >>> 8) + fogTint;
				}
				dst[pixelIndex] = colour;
				this.depthBuffer[pixelIndex] = depth;
			}
			pixelIndex++;
			invZLeft += slopeInvZ;
			uLeft += slopeU;
			vLeft += slopeV;
			fogLeft += slopeFog;
			redLeft += slopeRed;
			greenLeft += slopeGreen;
			blueLeft += slopeBlue;
			weightLeft += slopeWeight;
			weight2Left += slopeWeight2;
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "([I[FIIIIIFF)V")
	private void drawFlatSpanRgb(@OriginalArg(0) int[] dst, @OriginalArg(1) float[] depths, @OriginalArg(2) int rowOffset, @OriginalArg(3) int colour, @OriginalArg(5) int startX, @OriginalArg(6) int endX, @OriginalArg(7) float z, @OriginalArg(8) float zStep) {
		if (this.clamp) {
			if (endX > this.width) {
				endX = this.width;
			}
			if (startX < 0) {
				startX = 0;
			}
		}
		if (startX >= endX) {
			return;
		}
		rowOffset += startX - 1;
		@Pc(29) int remaining = endX - startX >> 2;
		z += zStep * (float) startX;
		@Pc(278) int alpha;
		@Pc(283) int invAlpha;
		@Pc(315) int background;
		@Pc(47) int index;
		@Pc(303) int scaledColour;
		@Pc(63) float nextZ;
		if (this.threadResource.zWrite) {
			if (this.alpha == 0) {
				while (true) {
					remaining--;
					if (remaining < 0) {
						remaining = endX - startX & 0x3;
						while (true) {
							remaining--;
							if (remaining < 0) {
								return;
							}
							rowOffset++;
							if (z < depths[rowOffset]) {
								dst[rowOffset] = colour;
								depths[rowOffset] = z;
							}
							z += zStep;
						}
					}
					index = rowOffset + 1;
					if (z < depths[index]) {
						dst[index] = colour;
						depths[index] = z;
					}
					nextZ = z + zStep;
					index++;
					if (nextZ < depths[index]) {
						dst[index] = colour;
						depths[index] = nextZ;
					}
					nextZ += zStep;
					index++;
					if (nextZ < depths[index]) {
						dst[index] = colour;
						depths[index] = nextZ;
					}
					nextZ += zStep;
					rowOffset = index + 1;
					if (nextZ < depths[rowOffset]) {
						dst[rowOffset] = colour;
						depths[rowOffset] = nextZ;
					}
					z = nextZ + zStep;
				}
			} else if (this.alpha != 254) {
				alpha = this.alpha;
				invAlpha = 256 - this.alpha;
				scaledColour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
				while (true) {
					remaining--;
					if (remaining < 0) {
						remaining = endX - startX & 0x3;
						while (true) {
							remaining--;
							if (remaining < 0) {
								return;
							}
							rowOffset++;
							if (z < depths[rowOffset]) {
								background = dst[rowOffset];
								dst[rowOffset] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
								depths[rowOffset] = z;
							}
							z += zStep;
						}
					}
					index = rowOffset + 1;
					if (z < depths[index]) {
						background = dst[index];
						dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						depths[index] = z;
					}
					nextZ = z + zStep;
					index++;
					if (nextZ < depths[index]) {
						background = dst[index];
						dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						depths[index] = nextZ;
					}
					nextZ += zStep;
					index++;
					if (nextZ < depths[index]) {
						background = dst[index];
						dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						depths[index] = nextZ;
					}
					nextZ += zStep;
					rowOffset = index + 1;
					if (nextZ < depths[rowOffset]) {
						background = dst[rowOffset];
						dst[rowOffset] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						depths[rowOffset] = nextZ;
					}
					z = nextZ + zStep;
				}
			} else if (startX != 0 && endX <= this.width - 1) {
				while (true) {
					remaining--;
					if (remaining < 0) {
						remaining = endX - startX & 0x3;
						while (true) {
							remaining--;
							if (remaining < 0) {
								return;
							}
							rowOffset++;
							if (z < depths[rowOffset]) {
								dst[rowOffset - 1] = dst[rowOffset];
							}
							z += zStep;
						}
					}
					index = rowOffset + 1;
					if (z < depths[index]) {
						dst[index - 1] = dst[index];
					}
					nextZ = z + zStep;
					index++;
					if (nextZ < depths[index]) {
						dst[index - 1] = dst[index];
					}
					nextZ += zStep;
					index++;
					if (nextZ < depths[index]) {
						dst[index - 1] = dst[index];
					}
					nextZ += zStep;
					rowOffset = index + 1;
					if (nextZ < depths[rowOffset]) {
						dst[rowOffset - 1] = dst[rowOffset];
					}
					z = nextZ + zStep;
				}
			}
		} else if (this.alpha == 0) {
			while (true) {
				remaining--;
				if (remaining < 0) {
					remaining = endX - startX & 0x3;
					while (true) {
						remaining--;
						if (remaining < 0) {
							return;
						}
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset] = colour;
						}
						z += zStep;
					}
				}
				index = rowOffset + 1;
				if (z < depths[index]) {
					dst[index] = colour;
				}
				nextZ = z + zStep;
				index++;
				if (nextZ < depths[index]) {
					dst[index] = colour;
				}
				nextZ += zStep;
				index++;
				if (nextZ < depths[index]) {
					dst[index] = colour;
				}
				nextZ += zStep;
				rowOffset = index + 1;
				if (nextZ < depths[rowOffset]) {
					dst[rowOffset] = colour;
				}
				z = nextZ + zStep;
			}
		} else if (this.alpha != 254) {
			alpha = this.alpha;
			invAlpha = 256 - this.alpha;
			scaledColour = ((colour & 0xFF00FF) * invAlpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * invAlpha >> 8 & 0xFF00);
			while (true) {
				remaining--;
				if (remaining < 0) {
					remaining = endX - startX & 0x3;
					while (true) {
						remaining--;
						if (remaining < 0) {
							return;
						}
						rowOffset++;
						if (z < depths[rowOffset]) {
							background = dst[rowOffset];
							dst[rowOffset] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
						}
						z += zStep;
					}
				}
				index = rowOffset + 1;
				if (z < depths[index]) {
					background = dst[index];
					dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
				}
				nextZ = z + zStep;
				index++;
				if (nextZ < depths[index]) {
					background = dst[index];
					dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
				}
				nextZ += zStep;
				index++;
				if (nextZ < depths[index]) {
					background = dst[index];
					dst[index] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
				}
				nextZ += zStep;
				rowOffset = index + 1;
				if (nextZ < depths[rowOffset]) {
					background = dst[rowOffset];
					dst[rowOffset] = scaledColour + ((background & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((background & 0xFF00) * alpha >> 8 & 0xFF00);
				}
				z = nextZ + zStep;
			}
		} else if (startX != 0 && endX <= this.width - 1) {
			while (true) {
				remaining--;
				if (remaining < 0) {
					remaining = endX - startX & 0x3;
					while (true) {
						remaining--;
						if (remaining < 0) {
							return;
						}
						rowOffset++;
						if (z < depths[rowOffset]) {
							dst[rowOffset - 1] = dst[rowOffset];
						}
						z += zStep;
					}
				}
				index = rowOffset + 1;
				if (z < depths[index]) {
					dst[index - 1] = dst[index];
				}
				nextZ = z + zStep;
				index++;
				if (nextZ < depths[index]) {
					dst[index - 1] = dst[index];
				}
				nextZ += zStep;
				index++;
				if (nextZ < depths[index]) {
					dst[index - 1] = dst[index];
				}
				nextZ += zStep;
				rowOffset = index + 1;
				if (nextZ < depths[rowOffset]) {
					dst[rowOffset - 1] = dst[rowOffset];
				}
				z = nextZ + zStep;
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "a", descriptor = "(FFFFFFFFFIII)V")
	public void renderTriangleRgb(@OriginalArg(0) float yA, @OriginalArg(1) float yB, @OriginalArg(2) float yC, @OriginalArg(3) float xA, @OriginalArg(4) float xB, @OriginalArg(5) float xC, @OriginalArg(6) float zA, @OriginalArg(7) float zB, @OriginalArg(8) float zC, @OriginalArg(9) int colourA, @OriginalArg(10) int colourB, @OriginalArg(11) int colourC) {
		if (this.wireframe) {
			this.toolkit.drawOpaqueLine((int) yA, (int) xA, (int) xB, colourA | 0xFF000000, (int) yB);
			this.toolkit.drawOpaqueLine((int) yB, (int) xB, (int) xC, colourA | 0xFF000000, (int) yC);
			this.toolkit.drawOpaqueLine((int) yC, (int) xC, (int) xA, colourA | 0xFF000000, (int) yA);
			return;
		}
		@Pc(52) float slopeXBa = xB - xA;
		@Pc(56) float slopeYBa = yB - yA;
		@Pc(60) float slopeXCa = xC - xA;
		@Pc(64) float slopeYCa = yC - yA;
		@Pc(68) float slopeZBa = zB - zA;
		@Pc(72) float slopeZCa = zC - zA;
		@Pc(81) float slopeRedBa = (float) ((colourB & 0xFF0000) - (colourA & 0xFF0000));
		@Pc(90) float slopeRedCa = (float) ((colourC & 0xFF0000) - (colourA & 0xFF0000));
		@Pc(99) float slopeGreenBa = (float) ((colourB & 0xFF00) - (colourA & 0xFF00));
		@Pc(108) float slopeGreenCa = (float) ((colourC & 0xFF00) - (colourA & 0xFF00));
		@Pc(117) float slopeBlueBa = (float) ((colourB & 0xFF) - (colourA & 0xFF));
		@Pc(126) float slopeBlueCa = (float) ((colourC & 0xFF) - (colourA & 0xFF));
		@Pc(138) float scaleXCb;
		if (yC == yB) {
			scaleXCb = 0.0F;
		} else {
			scaleXCb = (xC - xB) / (yC - yB);
		}
		@Pc(149) float scaleXBa;
		if (yB == yA) {
			scaleXBa = 0.0F;
		} else {
			scaleXBa = slopeXBa / slopeYBa;
		}
		@Pc(160) float scaleXCa;
		if (yC == yA) {
			scaleXCa = 0.0F;
		} else {
			scaleXCa = slopeXCa / slopeYCa;
		}
		@Pc(171) float determinant = slopeXBa * slopeYCa - slopeXCa * slopeYBa;
		if (determinant == 0.0F) {
			return;
		}
		@Pc(186) float slopeDepthX = (slopeZBa * slopeYCa - slopeZCa * slopeYBa) / determinant;
		@Pc(196) float slopeDepthY = (slopeZCa * slopeXBa - slopeZBa * slopeXCa) / determinant;
		@Pc(206) float slopeRedX = (slopeRedBa * slopeYCa - slopeRedCa * slopeYBa) / determinant;
		@Pc(216) float slopeRedY = (slopeRedCa * slopeXBa - slopeRedBa * slopeXCa) / determinant;
		@Pc(226) float slopeGreenX = (slopeGreenBa * slopeYCa - slopeGreenCa * slopeYBa) / determinant;
		@Pc(236) float slopeGreenY = (slopeGreenCa * slopeXBa - slopeGreenBa * slopeXCa) / determinant;
		@Pc(246) float slopeBlueX = (slopeBlueBa * slopeYCa - slopeBlueCa * slopeYBa) / determinant;
		@Pc(256) float slopeBlueY = (slopeBlueCa * slopeXBa - slopeBlueBa * slopeXCa) / determinant;
		@Pc(310) float red;
		@Pc(321) float green;
		@Pc(332) float blue;
		if (yA <= yB && yA <= yC) {
			if (!(yA >= (float) this.height)) {
				if (yB > (float) this.height) {
					yB = this.height;
				}
				if (yC > (float) this.height) {
					yC = this.height;
				}
				zA = zA + slopeDepthX - slopeDepthX * xA;
				red = (float) (colourA & 0xFF0000) + slopeRedX - slopeRedX * xA;
				green = (float) (colourA & 0xFF00) + slopeGreenX - slopeGreenX * xA;
				blue = (float) (colourA & 0xFF) + slopeBlueX - slopeBlueX * xA;
				if (yB < yC) {
					xC = xA;
					if (yA < 0.0F) {
						xC = xA - scaleXCa * yA;
						xA -= scaleXBa * yA;
						zA -= slopeDepthY * yA;
						red -= slopeRedY * yA;
						green -= slopeGreenY * yA;
						blue -= slopeBlueY * yA;
						yA = 0.0F;
					}
					if (yB < 0.0F) {
						xB -= scaleXCb * yB;
						yB = 0.0F;
					}
					if ((yA == yB || !(scaleXCa < scaleXBa)) && (yA != yB || !(scaleXCa > scaleXCb))) {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xC, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xB, (int) xC, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCa;
							xB += scaleXCb;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
					} else {
						yC -= yB;
						yB -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yB >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xA, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xB, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCa;
							xB += scaleXCb;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
					}
				} else {
					xB = xA;
					if (yA < 0.0F) {
						xB = xA - scaleXCa * yA;
						xA -= scaleXBa * yA;
						zA -= slopeDepthY * yA;
						red -= slopeRedY * yA;
						green -= slopeGreenY * yA;
						blue -= slopeBlueY * yA;
						yA = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= scaleXCb * yC;
						yC = 0.0F;
					}
					if (yA != yC && scaleXCa < scaleXBa || yA == yC && scaleXCb > scaleXBa) {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xB, (int) xA, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xB += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xC, (int) xA, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCb;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
					} else {
						yB -= yC;
						yC -= yA;
						yA = this.lineOffsets[(int) yA];
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xB, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xB += scaleXCa;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
						while (--yB >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yA, (int) xA, (int) xC, zA, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXCb;
							xA += scaleXBa;
							zA += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yA += this.surfaceWidth;
						}
					}
				}
			}
		} else if (yB <= yC) {
			if (!(yB >= (float) this.height)) {
				if (yC > (float) this.height) {
					yC = this.height;
				}
				if (yA > (float) this.height) {
					yA = this.height;
				}
				zB = zB + slopeDepthX - slopeDepthX * xB;
				red = (float) (colourB & 0xFF0000) + slopeRedX - slopeRedX * xB;
				green = (float) (colourB & 0xFF00) + slopeGreenX - slopeGreenX * xB;
				blue = (float) (colourB & 0xFF) + slopeBlueX - slopeBlueX * xB;
				if (yC < yA) {
					xA = xB;
					if (yB < 0.0F) {
						xA = xB - scaleXBa * yB;
						xB -= scaleXCb * yB;
						zB -= slopeDepthY * yB;
						red -= slopeRedY * yB;
						green -= slopeGreenY * yB;
						blue -= slopeBlueY * yB;
						yB = 0.0F;
					}
					if (yC < 0.0F) {
						xC -= scaleXCa * yC;
						yC = 0.0F;
					}
					if ((yB == yC || !(scaleXBa < scaleXCb)) && (yB != yC || !(scaleXBa > scaleXCa))) {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xA, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xC, (int) xA, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXBa;
							xC += scaleXCa;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
					} else {
						yA -= yC;
						yC -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xB, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
						while (--yA >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xC, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXBa;
							xC += scaleXCa;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
					}
				} else {
					xC = xB;
					if (yB < 0.0F) {
						xC = xB - scaleXBa * yB;
						xB -= scaleXCb * yB;
						zB -= slopeDepthY * yB;
						red -= slopeRedY * yB;
						green -= slopeGreenY * yB;
						blue -= slopeBlueY * yB;
						yB = 0.0F;
					}
					if (yA < 0.0F) {
						xA -= scaleXCa * yA;
						yA = 0.0F;
					}
					if (scaleXBa < scaleXCb) {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xC, (int) xB, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xA, (int) xB, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXCa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
					} else {
						yC -= yA;
						yA -= yB;
						yB = this.lineOffsets[(int) yB];
						while (--yA >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xC, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xC += scaleXBa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
						while (--yC >= 0.0F) {
							this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yB, (int) xB, (int) xA, zB, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
							xA += scaleXCa;
							xB += scaleXCb;
							zB += slopeDepthY;
							red += slopeRedY;
							green += slopeGreenY;
							blue += slopeBlueY;
							yB += this.surfaceWidth;
						}
					}
				}
			}
		} else if (!(yC >= (float) this.height)) {
			if (yA > (float) this.height) {
				yA = this.height;
			}
			if (yB > (float) this.height) {
				yB = this.height;
			}
			zC = zC + slopeDepthX - slopeDepthX * xC;
			red = (float) (colourC & 0xFF0000) + slopeRedX - slopeRedX * xC;
			green = (float) (colourC & 0xFF00) + slopeGreenX - slopeGreenX * xC;
			blue = (float) (colourC & 0xFF) + slopeBlueX - slopeBlueX * xC;
			if (yA < yB) {
				xB = xC;
				if (yC < 0.0F) {
					xB = xC - scaleXCb * yC;
					xC -= scaleXCa * yC;
					zC -= slopeDepthY * yC;
					red -= slopeRedY * yC;
					green -= slopeGreenY * yC;
					blue -= slopeBlueY * yC;
					yC = 0.0F;
				}
				if (yA < 0.0F) {
					xA -= scaleXBa * yA;
					yA = 0.0F;
				}
				if (scaleXCb < scaleXCa) {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xC, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xA, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXCb;
						xA += scaleXBa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
				} else {
					yB -= yA;
					yA -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yA >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xB, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
					while (--yB >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xA, (int) xB, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXCb;
						xA += scaleXBa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
				}
			} else {
				xA = xC;
				if (yC < 0.0F) {
					xA = xC - scaleXCb * yC;
					xC -= scaleXCa * yC;
					zC -= slopeDepthY * yC;
					red -= slopeRedY * yC;
					green -= slopeGreenY * yC;
					blue -= slopeBlueY * yC;
					yC = 0.0F;
				}
				if (yB < 0.0F) {
					xB -= scaleXBa * yB;
					yB = 0.0F;
				}
				if (scaleXCb < scaleXCa) {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xA, (int) xC, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xA += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xB, (int) xC, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXBa;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
				} else {
					yA -= yB;
					yB -= yC;
					yC = this.lineOffsets[(int) yC];
					while (--yB >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xA, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xA += scaleXCb;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
					while (--yA >= 0.0F) {
						this.drawGouraudSpanRgb(this.raster, this.depthBuffer, (int) yC, (int) xC, (int) xB, zC, slopeDepthX, red, slopeRedX, green, slopeGreenX, blue, slopeBlueX);
						xB += scaleXBa;
						xC += scaleXCa;
						zC += slopeDepthY;
						red += slopeRedY;
						green += slopeGreenY;
						blue += slopeBlueY;
						yC += this.surfaceWidth;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!mp", name = "b", descriptor = "()I")
	public int offsetX() {
		return this.lineOffsets[0] % this.surfaceWidth;
	}
}
