package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.Shadow;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!uc")
public final class GlShadow extends Shadow {

	@OriginalMember(owner = "client!uc", name = "H", descriptor = "I")
	public int sizeX;

	@OriginalMember(owner = "client!uc", name = "K", descriptor = "I")
	public int startZ;

	@OriginalMember(owner = "client!uc", name = "N", descriptor = "I")
	public int sizeZ;

	@OriginalMember(owner = "client!uc", name = "P", descriptor = "I")
	public int startX;

	@OriginalMember(owner = "client!uc", name = "M", descriptor = "[B")
	public final byte[] shade;

	@OriginalMember(owner = "client!uc", name = "<init>", descriptor = "(Lclient!qo;II)V")
	public GlShadow(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int sizeX, @OriginalArg(2) int sizeZ) {
		this.shade = new byte[sizeX * sizeZ];
	}

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(IIIII)V")
	public void setDimension(@OriginalArg(0) int endZ, @OriginalArg(1) int endX, @OriginalArg(2) int startZ, @OriginalArg(4) int startX) {
		this.startZ = startZ;
		this.startX = startX;
		this.sizeZ = endZ - startZ;
		this.sizeX = endX - startX;
	}

	@OriginalMember(owner = "client!uc", name = "e", descriptor = "(B)V")
	public void reset() {
		@Pc(7) int index = -1;
		@Pc(13) int limit = this.shade.length - 8;
		while (index < limit) {
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
			index++;
			this.shade[index] = 0;
		}
		while (this.shade.length - 1 > index) {
			index++;
			this.shade[index] = 0;
		}
	}

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(IIIIIII)V")
	public void fillTriangle(@OriginalArg(0) int xC, @OriginalArg(1) int xA, @OriginalArg(2) int zA, @OriginalArg(4) int xB, @OriginalArg(5) int zC, @OriginalArg(6) int zB) {
		@Pc(7) int slopeAB = 0;
		if (zB != zA) {
			slopeAB = (xB - xA << 16) / (zB - zA);
		}
		@Pc(32) int slopeBC = 0;
		if (zB != zC) {
			slopeBC = (xC - xB << 16) / (zC - zB);
		}
		@Pc(48) int slopeCA = 0;
		if (zA != zC) {
			slopeCA = (xA - xC << 16) / (zA - zC);
		}
		if (zB >= zA && zA <= zC) {
			if (zB < zC) {
				xC = xA <<= 0x10;
				if (zA < 0) {
					xC -= zA * slopeCA;
					xA -= slopeAB * zA;
					zA = 0;
				}
				xB <<= 0x10;
				if (zB < 0) {
					xB -= slopeBC * zB;
					zB = 0;
				}
				if (zA != zB && slopeCA < slopeAB || zB == zA && slopeCA > slopeBC) {
					zC -= zB;
					zB -= zA;
					zA = this.sizeX * zA;
					while (true) {
						zB--;
						if (zB < 0) {
							while (true) {
								zC--;
								if (zC < 0) {
									return;
								}
								GlShadow.fillSpan(xC >> 16, zA, this.shade, xB >> 16);
								xB += slopeBC;
								zA += this.sizeX;
								xC += slopeCA;
							}
						}
						GlShadow.fillSpan(xC >> 16, zA, this.shade, xA >> 16);
						zA += this.sizeX;
						xC += slopeCA;
						xA += slopeAB;
					}
				} else {
					zC -= zB;
					zB -= zA;
					zA = this.sizeX * zA;
					while (true) {
						zB--;
						if (zB < 0) {
							while (true) {
								zC--;
								if (zC < 0) {
									return;
								}
								GlShadow.fillSpan(xB >> 16, zA, this.shade, xC >> 16);
								xC += slopeCA;
								zA += this.sizeX;
								xB += slopeBC;
							}
						}
						GlShadow.fillSpan(xA >> 16, zA, this.shade, xC >> 16);
						xA += slopeAB;
						zA += this.sizeX;
						xC += slopeCA;
					}
				}
			} else {
				xB = xA <<= 0x10;
				if (zA < 0) {
					xA -= slopeAB * zA;
					xB -= slopeCA * zA;
					zA = 0;
				}
				xC <<= 0x10;
				if (zC < 0) {
					xC -= slopeBC * zC;
					zC = 0;
				}
				if ((zC == zA || slopeAB <= slopeCA) && (zA != zC || slopeBC <= slopeAB)) {
					zB -= zC;
					zC -= zA;
					zA = this.sizeX * zA;
					while (true) {
						zC--;
						if (zC < 0) {
							while (true) {
								zB--;
								if (zB < 0) {
									return;
								}
								GlShadow.fillSpan(xA >> 16, zA, this.shade, xC >> 16);
								zA += this.sizeX;
								xA += slopeAB;
								xC += slopeBC;
							}
						}
						GlShadow.fillSpan(xA >> 16, zA, this.shade, xB >> 16);
						xB += slopeCA;
						zA += this.sizeX;
						xA += slopeAB;
					}
				} else {
					zB -= zC;
					zC -= zA;
					zA *= this.sizeX;
					while (true) {
						zC--;
						if (zC < 0) {
							while (true) {
								zB--;
								if (zB < 0) {
									return;
								}
								GlShadow.fillSpan(xC >> 16, zA, this.shade, xA >> 16);
								xC += slopeBC;
								xA += slopeAB;
								zA += this.sizeX;
							}
						}
						GlShadow.fillSpan(xB >> 16, zA, this.shade, xA >> 16);
						zA += this.sizeX;
						xB += slopeCA;
						xA += slopeAB;
					}
				}
			}
		} else if (zB > zC) {
			if (zB <= zA) {
				xA = xC <<= 0x10;
				xB <<= 0x10;
				if (zC < 0) {
					xC -= zC * slopeCA;
					xA -= zC * slopeBC;
					zC = 0;
				}
				if (zB < 0) {
					xB -= zB * slopeAB;
					zB = 0;
				}
				if (slopeBC >= slopeCA) {
					zA -= zB;
					zB -= zC;
					zC = this.sizeX * zC;
					while (true) {
						zB--;
						if (zB < 0) {
							while (true) {
								zA--;
								if (zA < 0) {
									return;
								}
								GlShadow.fillSpan(xC >> 16, zC, this.shade, xB >> 16);
								xB += slopeAB;
								xC += slopeCA;
								zC += this.sizeX;
							}
						}
						GlShadow.fillSpan(xC >> 16, zC, this.shade, xA >> 16);
						xA += slopeBC;
						zC += this.sizeX;
						xC += slopeCA;
					}
				} else {
					zA -= zB;
					zB -= zC;
					zC *= this.sizeX;
					while (true) {
						zB--;
						if (zB < 0) {
							while (true) {
								zA--;
								if (zA < 0) {
									return;
								}
								GlShadow.fillSpan(xB >> 16, zC, this.shade, xC >> 16);
								xC += slopeCA;
								xB += slopeAB;
								zC += this.sizeX;
							}
						}
						GlShadow.fillSpan(xA >> 16, zC, this.shade, xC >> 16);
						xC += slopeCA;
						xA += slopeBC;
						zC += this.sizeX;
					}
				}
			} else {
				xB = xC <<= 0x10;
				xA <<= 0x10;
				if (zC < 0) {
					xB -= slopeBC * zC;
					xC -= zC * slopeCA;
					zC = 0;
				}
				if (zA < 0) {
					xA -= zA * slopeAB;
					zA = 0;
				}
				if (slopeCA > slopeBC) {
					zB -= zA;
					zA -= zC;
					zC = this.sizeX * zC;
					while (true) {
						zA--;
						if (zA < 0) {
							while (true) {
								zB--;
								if (zB < 0) {
									return;
								}
								GlShadow.fillSpan(xB >> 16, zC, this.shade, xA >> 16);
								xB += slopeBC;
								xA += slopeAB;
								zC += this.sizeX;
							}
						}
						GlShadow.fillSpan(xB >> 16, zC, this.shade, xC >> 16);
						zC += this.sizeX;
						xC += slopeCA;
						xB += slopeBC;
					}
				} else {
					zB -= zA;
					zA -= zC;
					zC *= this.sizeX;
					while (true) {
						zA--;
						if (zA < 0) {
							while (true) {
								zB--;
								if (zB < 0) {
									return;
								}
								GlShadow.fillSpan(xA >> 16, zC, this.shade, xB >> 16);
								zC += this.sizeX;
								xB += slopeBC;
								xA += slopeAB;
							}
						}
						GlShadow.fillSpan(xC >> 16, zC, this.shade, xB >> 16);
						xC += slopeCA;
						xB += slopeBC;
						zC += this.sizeX;
					}
				}
			}
		} else if (zC >= zA) {
			xC = xB <<= 0x10;
			if (zB < 0) {
				xC -= zB * slopeAB;
				xB -= slopeBC * zB;
				zB = 0;
			}
			xA <<= 0x10;
			if (zA < 0) {
				xA -= zA * slopeCA;
				zA = 0;
			}
			if (slopeAB < slopeBC) {
				zC -= zA;
				zA -= zB;
				zB = this.sizeX * zB;
				while (true) {
					zA--;
					if (zA < 0) {
						while (true) {
							zC--;
							if (zC < 0) {
								return;
							}
							GlShadow.fillSpan(xA >> 16, zB, this.shade, xB >> 16);
							xA += slopeCA;
							xB += slopeBC;
							zB += this.sizeX;
						}
					}
					GlShadow.fillSpan(xC >> 16, zB, this.shade, xB >> 16);
					xC += slopeAB;
					xB += slopeBC;
					zB += this.sizeX;
				}
			} else {
				zC -= zA;
				zA -= zB;
				zB *= this.sizeX;
				while (true) {
					zA--;
					if (zA < 0) {
						while (true) {
							zC--;
							if (zC < 0) {
								return;
							}
							GlShadow.fillSpan(xB >> 16, zB, this.shade, xA >> 16);
							xB += slopeBC;
							xA += slopeCA;
							zB += this.sizeX;
						}
					}
					GlShadow.fillSpan(xB >> 16, zB, this.shade, xC >> 16);
					xB += slopeBC;
					xC += slopeAB;
					zB += this.sizeX;
				}
			}
		} else {
			xA = xB <<= 0x10;
			if (zB < 0) {
				xA -= slopeAB * zB;
				xB -= slopeBC * zB;
				zB = 0;
			}
			xC <<= 0x10;
			if (zC < 0) {
				xC -= slopeCA * zC;
				zC = 0;
			}
			if (zB != zC && slopeBC > slopeAB || zC == zB && slopeAB > slopeCA) {
				zA -= zC;
				zC -= zB;
				zB = this.sizeX * zB;
				while (true) {
					zC--;
					if (zC < 0) {
						while (true) {
							zA--;
							if (zA < 0) {
								return;
							}
							GlShadow.fillSpan(xA >> 16, zB, this.shade, xC >> 16);
							xA += slopeAB;
							xC += slopeCA;
							zB += this.sizeX;
						}
					}
					GlShadow.fillSpan(xA >> 16, zB, this.shade, xB >> 16);
					zB += this.sizeX;
					xA += slopeAB;
					xB += slopeBC;
				}
			} else {
				zA -= zC;
				zC -= zB;
				zB = this.sizeX * zB;
				while (true) {
					zC--;
					if (zC < 0) {
						while (true) {
							zA--;
							if (zA < 0) {
								return;
							}
							GlShadow.fillSpan(xC >> 16, zB, this.shade, xA >> 16);
							zB += this.sizeX;
							xC += slopeCA;
							xA += slopeAB;
						}
					}
					GlShadow.fillSpan(xB >> 16, zB, this.shade, xA >> 16);
					xA += slopeAB;
					zB += this.sizeX;
					xB += slopeBC;
				}
			}
		}
	}

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(BII)Z")
	public boolean fitsDimension(@OriginalArg(1) int sizeZ, @OriginalArg(2) int sizeX) {
		return sizeZ * sizeX <= this.shade.length;
	}
	@OriginalMember(owner = "client!wq", name = "a", descriptor = "(IIII[BI)V")
	public static void fillSpan(@OriginalArg(0) int startX, @OriginalArg(1) int offset, @OriginalArg(4) byte[] data, @OriginalArg(5) int endX) {
		if (endX <= startX) {
			return;
		}
		offset += startX;
		@Pc(28) int count = endX - startX >> 2;
		while (true) {
			count--;
			if (count < 0) {
				count = endX - startX & 0x3;
				while (true) {
					count--;
					if (count < 0) {
						return;
					}
					data[offset++] = 1;
				}
			}
			@Pc(33) int offset1 = offset + 1;
			data[offset] = 1;
			@Pc(38) int offset2 = offset1 + 1;
			data[offset1] = 1;
			@Pc(43) int offset3 = offset2 + 1;
			data[offset2] = 1;
			offset = offset3 + 1;
			data[offset3] = 1;
		}
	}
}
