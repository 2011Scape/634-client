package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ClientTrigger;

@OriginalClass("client!fc")
public final class GpuShadow extends Shadow {

	@OriginalMember(owner = "client!fc", name = "F", descriptor = "I")
	public int sizeX;

	@OriginalMember(owner = "client!fc", name = "G", descriptor = "I")
	public int sizeZ;

	@OriginalMember(owner = "client!fc", name = "H", descriptor = "I")
	public int startX;

	@OriginalMember(owner = "client!fc", name = "O", descriptor = "I")
	public int startZ;

	@OriginalMember(owner = "client!fc", name = "K", descriptor = "[B")
	public final byte[] shade;

	static {
		new ClientTrigger("", 76);
	}

	@OriginalMember(owner = "client!fc", name = "<init>", descriptor = "(Lclient!wga;II)V")
	public GpuShadow(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int sizeX, @OriginalArg(2) int sizeZ) {
		this.shade = new byte[sizeZ * sizeX];
	}

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIIII)V")
	public void setDimension(@OriginalArg(0) int endX, @OriginalArg(2) int endZ, @OriginalArg(3) int startZ, @OriginalArg(4) int startX) {
		this.sizeX = endX - startX;
		this.sizeZ = endZ - startZ;
		this.startX = startX;
		this.startZ = startZ;
	}

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIIIIII)V")
	public void fillTriangle(@OriginalArg(0) int zA, @OriginalArg(2) int xA, @OriginalArg(3) int xB, @OriginalArg(4) int zB, @OriginalArg(5) int zC, @OriginalArg(6) int xC) {
		@Pc(7) int slopeAB = 0;
		if (zB != zA) {
			slopeAB = (xB - xA << 16) / (zB - zA);
		}
		@Pc(24) int slopeBC = 0;
		if (zC != zB) {
			slopeBC = (xC - xB << 16) / (zC - zB);
		}
		@Pc(53) int slopeCA = 0;
		if (zA != zC) {
			slopeCA = (xA - xC << 16) / (zA - zC);
		}
		if (zB >= zA && zA <= zC) {
			if (zB >= zC) {
				xB = xA <<= 0x10;
				xC <<= 0x10;
				if (zA < 0) {
					xB -= zA * slopeCA;
					xA -= slopeAB * zA;
					zA = 0;
				}
				if (zC < 0) {
					xC -= zC * slopeBC;
					zC = 0;
				}
				if (zC != zA && slopeCA < slopeAB || zC == zA && slopeAB < slopeBC) {
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
								GpuShadow.fillShadowSpan(zA, xC >> 16, this.shade, xA >> 16);
								zA += this.sizeX;
								xC += slopeBC;
								xA += slopeAB;
							}
						}
						GpuShadow.fillShadowSpan(zA, xB >> 16, this.shade, xA >> 16);
						zA += this.sizeX;
						xA += slopeAB;
						xB += slopeCA;
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
								GpuShadow.fillShadowSpan(zA, xA >> 16, this.shade, xC >> 16);
								zA += this.sizeX;
								xC += slopeBC;
								xA += slopeAB;
							}
						}
						GpuShadow.fillShadowSpan(zA, xA >> 16, this.shade, xB >> 16);
						xB += slopeCA;
						xA += slopeAB;
						zA += this.sizeX;
					}
				}
			} else {
				xC = xA <<= 0x10;
				if (zA < 0) {
					xA -= slopeAB * zA;
					xC -= slopeCA * zA;
					zA = 0;
				}
				xB <<= 0x10;
				if (zB < 0) {
					xB -= zB * slopeBC;
					zB = 0;
				}
				if ((zB == zA || slopeAB <= slopeCA) && (zA != zB || slopeBC >= slopeCA)) {
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
								GpuShadow.fillShadowSpan(zA, xB >> 16, this.shade, xC >> 16);
								xB += slopeBC;
								zA += this.sizeX;
								xC += slopeCA;
							}
						}
						GpuShadow.fillShadowSpan(zA, xA >> 16, this.shade, xC >> 16);
						xC += slopeCA;
						zA += this.sizeX;
						xA += slopeAB;
					}
				} else {
					zC -= zB;
					zB -= zA;
					zA *= this.sizeX;
					while (true) {
						zB--;
						if (zB < 0) {
							while (true) {
								zC--;
								if (zC < 0) {
									return;
								}
								GpuShadow.fillShadowSpan(zA, xC >> 16, this.shade, xB >> 16);
								zA += this.sizeX;
								xC += slopeCA;
								xB += slopeBC;
							}
						}
						GpuShadow.fillShadowSpan(zA, xC >> 16, this.shade, xA >> 16);
						xA += slopeAB;
						xC += slopeCA;
						zA += this.sizeX;
					}
				}
			}
		} else if (zB <= zC) {
			if (zC >= zA) {
				xC = xB <<= 0x10;
				if (zB < 0) {
					xB -= zB * slopeBC;
					xC -= zB * slopeAB;
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
								GpuShadow.fillShadowSpan(zB, xA >> 16, this.shade, xB >> 16);
								xB += slopeBC;
								zB += this.sizeX;
								xA += slopeCA;
							}
						}
						GpuShadow.fillShadowSpan(zB, xC >> 16, this.shade, xB >> 16);
						xC += slopeAB;
						zB += this.sizeX;
						xB += slopeBC;
					}
				} else {
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
								GpuShadow.fillShadowSpan(zB, xB >> 16, this.shade, xA >> 16);
								xB += slopeBC;
								xA += slopeCA;
								zB += this.sizeX;
							}
						}
						GpuShadow.fillShadowSpan(zB, xB >> 16, this.shade, xC >> 16);
						zB += this.sizeX;
						xB += slopeBC;
						xC += slopeAB;
					}
				}
			} else {
				xA = xB <<= 0x10;
				if (zB < 0) {
					xB -= slopeBC * zB;
					xA -= slopeAB * zB;
					zB = 0;
				}
				xC <<= 0x10;
				if (zC < 0) {
					xC -= zC * slopeCA;
					zC = 0;
				}
				if ((zB == zC || slopeBC <= slopeAB) && (zB != zC || slopeAB <= slopeCA)) {
					zA -= zC;
					zC -= zB;
					zB *= this.sizeX;
					while (true) {
						zC--;
						if (zC < 0) {
							while (true) {
								zA--;
								if (zA < 0) {
									return;
								}
								GpuShadow.fillShadowSpan(zB, xC >> 16, this.shade, xA >> 16);
								zB += this.sizeX;
								xA += slopeAB;
								xC += slopeCA;
							}
						}
						GpuShadow.fillShadowSpan(zB, xB >> 16, this.shade, xA >> 16);
						zB += this.sizeX;
						xA += slopeAB;
						xB += slopeBC;
					}
				} else {
					zA -= zC;
					zC -= zB;
					zB *= this.sizeX;
					while (true) {
						zC--;
						if (zC < 0) {
							while (true) {
								zA--;
								if (zA < 0) {
									return;
								}
								GpuShadow.fillShadowSpan(zB, xA >> 16, this.shade, xC >> 16);
								zB += this.sizeX;
								xC += slopeCA;
								xA += slopeAB;
							}
						}
						GpuShadow.fillShadowSpan(zB, xA >> 16, this.shade, xB >> 16);
						xB += slopeBC;
						zB += this.sizeX;
						xA += slopeAB;
					}
				}
			}
		} else if (zA < zB) {
			xB = xC <<= 0x10;
			xA <<= 0x10;
			if (zC < 0) {
				xC -= slopeCA * zC;
				xB -= slopeBC * zC;
				zC = 0;
			}
			if (zA < 0) {
				xA -= slopeAB * zA;
				zA = 0;
			}
			if (slopeBC < slopeCA) {
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
							GpuShadow.fillShadowSpan(zC, xB >> 16, this.shade, xA >> 16);
							xA += slopeAB;
							zC += this.sizeX;
							xB += slopeBC;
						}
					}
					GpuShadow.fillShadowSpan(zC, xB >> 16, this.shade, xC >> 16);
					zC += this.sizeX;
					xB += slopeBC;
					xC += slopeCA;
				}
			} else {
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
							GpuShadow.fillShadowSpan(zC, xA >> 16, this.shade, xB >> 16);
							xA += slopeAB;
							zC += this.sizeX;
							xB += slopeBC;
						}
					}
					GpuShadow.fillShadowSpan(zC, xC >> 16, this.shade, xB >> 16);
					xB += slopeBC;
					zC += this.sizeX;
					xC += slopeCA;
				}
			}
		} else {
			xA = xC <<= 0x10;
			if (zC < 0) {
				xC -= slopeCA * zC;
				xA -= zC * slopeBC;
				zC = 0;
			}
			xB <<= 0x10;
			if (zB < 0) {
				xB -= slopeAB * zB;
				zB = 0;
			}
			if (slopeCA <= slopeBC) {
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
							GpuShadow.fillShadowSpan(zC, xC >> 16, this.shade, xB >> 16);
							xB += slopeAB;
							xC += slopeCA;
							zC += this.sizeX;
						}
					}
					GpuShadow.fillShadowSpan(zC, xC >> 16, this.shade, xA >> 16);
					zC += this.sizeX;
					xA += slopeBC;
					xC += slopeCA;
				}
			} else {
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
							GpuShadow.fillShadowSpan(zC, xB >> 16, this.shade, xC >> 16);
							xC += slopeCA;
							zC += this.sizeX;
							xB += slopeAB;
						}
					}
					GpuShadow.fillShadowSpan(zC, xA >> 16, this.shade, xC >> 16);
					xA += slopeBC;
					xC += slopeCA;
					zC += this.sizeX;
				}
			}
		}
	}

	@OriginalMember(owner = "client!fc", name = "d", descriptor = "(I)V")
	public void reset() {
		@Pc(17) int index = -1;
		@Pc(23) int limit = this.shade.length - 8;
		while (limit > index) {
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
		while (index < this.shade.length - 1) {
			index++;
			this.shade[index] = 0;
		}
	}

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIB)Z")
	public boolean fitsDimension(@OriginalArg(0) int sizeX, @OriginalArg(1) int sizeZ) {
		return this.shade.length >= sizeZ * sizeX;
	}
	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(II[BIII)V")
	public static void fillShadowSpan(@OriginalArg(0) int rowOffset, @OriginalArg(1) int startX, @OriginalArg(2) byte[] dst, @OriginalArg(5) int endX) {
		if (startX >= endX) {
			return;
		}
		rowOffset += startX;
		@Pc(24) int remaining = endX - startX >> 2;
		while (true) {
			remaining--;
			if (remaining < 0) {
				remaining = endX - startX & 0x3;
				while (true) {
					remaining--;
					if (remaining < 0) {
						return;
					}
					dst[rowOffset++] = 1;
				}
			}
			@Pc(29) int rowOffset1 = rowOffset + 1;
			dst[rowOffset] = 1;
			@Pc(34) int rowOffset2 = rowOffset1 + 1;
			dst[rowOffset1] = 1;
			@Pc(39) int rowOffset3 = rowOffset2 + 1;
			dst[rowOffset2] = 1;
			rowOffset = rowOffset3 + 1;
			dst[rowOffset3] = 1;
		}
	}
}
