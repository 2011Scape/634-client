package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!uc")
class GlShadow @OriginalMember(owner = "client!uc", name = "<init>", descriptor = "(Lclient!qo;II)V") constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) sizeX: Int, @OriginalArg(2) sizeZ: Int) : Shadow() {

	@OriginalMember(owner = "client!uc", name = "H", descriptor = "I")
	@JvmField
	var sizeX: Int = 0

	@OriginalMember(owner = "client!uc", name = "K", descriptor = "I")
	@JvmField
	var startZ: Int = 0

	@OriginalMember(owner = "client!uc", name = "N", descriptor = "I")
	@JvmField
	var sizeZ: Int = 0

	@OriginalMember(owner = "client!uc", name = "P", descriptor = "I")
	@JvmField
	var startX: Int = 0

	@OriginalMember(owner = "client!uc", name = "M", descriptor = "[B")
	@JvmField
	val shade: ByteArray = ByteArray(sizeX * sizeZ)

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(IIIII)V")
	fun setDimension(@OriginalArg(0) endZ: Int, @OriginalArg(1) endX: Int, @OriginalArg(2) startZ: Int, @OriginalArg(4) startX: Int) {
		this.startZ = startZ
		this.startX = startX
		this.sizeZ = endZ - startZ
		this.sizeX = endX - startX
	}

	@OriginalMember(owner = "client!uc", name = "e", descriptor = "(B)V")
	fun reset() {
		@Pc(7) var index: Int = -1
		@Pc(13) var limit: Int = shade.size - 8
		while (index < limit) {
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
			index++
			shade[index] = 0
		}
		while (shade.size - 1 > index) {
			index++
			shade[index] = 0
		}
	}

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(IIIIIII)V")
	fun fillTriangle(@OriginalArg(0) xC: Int, @OriginalArg(1) xA: Int, @OriginalArg(2) zA: Int, @OriginalArg(4) xB: Int, @OriginalArg(5) zC: Int, @OriginalArg(6) zB: Int) {
		var xC: Int = xC
		var xA: Int = xA
		var zA: Int = zA
		var xB: Int = xB
		var zC: Int = zC
		var zB: Int = zB
		@Pc(7) var slopeAB: Int = 0
		if (zB != zA) {
			slopeAB = ((xB - xA) shl 16) / (zB - zA)
		}
		@Pc(32) var slopeBC: Int = 0
		if (zB != zC) {
			slopeBC = ((xC - xB) shl 16) / (zC - zB)
		}
		@Pc(48) var slopeCA: Int = 0
		if (zA != zC) {
			slopeCA = ((xA - xC) shl 16) / (zA - zC)
		}
		if (zB >= zA && zA <= zC) {
			if (zB < zC) {
				xA = xA shl 16
				xC = xA
				if (zA < 0) {
					xC -= zA * slopeCA
					xA -= slopeAB * zA
					zA = 0
				}
				xB = xB shl 16
				if (zB < 0) {
					xB -= slopeBC * zB
					zB = 0
				}
				if (zA != zB && slopeCA < slopeAB || zB == zA && slopeCA > slopeBC) {
					zC -= zB
					zB -= zA
					zA = sizeX * zA
					while (true) {
						zB--
						if (zB < 0) {
							while (true) {
								zC--
								if (zC < 0) {
									return
								}
								fillSpan(xC shr 16, zA, shade, xB shr 16)
								xB += slopeBC
								zA += sizeX
								xC += slopeCA
							}
						}
						fillSpan(xC shr 16, zA, shade, xA shr 16)
						zA += sizeX
						xC += slopeCA
						xA += slopeAB
					}
				} else {
					zC -= zB
					zB -= zA
					zA = sizeX * zA
					while (true) {
						zB--
						if (zB < 0) {
							while (true) {
								zC--
								if (zC < 0) {
									return
								}
								fillSpan(xB shr 16, zA, shade, xC shr 16)
								xC += slopeCA
								zA += sizeX
								xB += slopeBC
							}
						}
						fillSpan(xA shr 16, zA, shade, xC shr 16)
						xA += slopeAB
						zA += sizeX
						xC += slopeCA
					}
				}
			} else {
				xA = xA shl 16
				xB = xA
				if (zA < 0) {
					xA -= slopeAB * zA
					xB -= slopeCA * zA
					zA = 0
				}
				xC = xC shl 16
				if (zC < 0) {
					xC -= slopeBC * zC
					zC = 0
				}
				if ((zC == zA || slopeAB <= slopeCA) && (zA != zC || slopeBC <= slopeAB)) {
					zB -= zC
					zC -= zA
					zA = sizeX * zA
					while (true) {
						zC--
						if (zC < 0) {
							while (true) {
								zB--
								if (zB < 0) {
									return
								}
								fillSpan(xA shr 16, zA, shade, xC shr 16)
								zA += sizeX
								xA += slopeAB
								xC += slopeBC
							}
						}
						fillSpan(xA shr 16, zA, shade, xB shr 16)
						xB += slopeCA
						zA += sizeX
						xA += slopeAB
					}
				} else {
					zB -= zC
					zC -= zA
					zA *= sizeX
					while (true) {
						zC--
						if (zC < 0) {
							while (true) {
								zB--
								if (zB < 0) {
									return
								}
								fillSpan(xC shr 16, zA, shade, xA shr 16)
								xC += slopeBC
								xA += slopeAB
								zA += sizeX
							}
						}
						fillSpan(xB shr 16, zA, shade, xA shr 16)
						zA += sizeX
						xB += slopeCA
						xA += slopeAB
					}
				}
			}
		} else if (zB > zC) {
			if (zB <= zA) {
				xC = xC shl 16
				xA = xC
				xB = xB shl 16
				if (zC < 0) {
					xC -= zC * slopeCA
					xA -= zC * slopeBC
					zC = 0
				}
				if (zB < 0) {
					xB -= zB * slopeAB
					zB = 0
				}
				if (slopeBC >= slopeCA) {
					zA -= zB
					zB -= zC
					zC = sizeX * zC
					while (true) {
						zB--
						if (zB < 0) {
							while (true) {
								zA--
								if (zA < 0) {
									return
								}
								fillSpan(xC shr 16, zC, shade, xB shr 16)
								xB += slopeAB
								xC += slopeCA
								zC += sizeX
							}
						}
						fillSpan(xC shr 16, zC, shade, xA shr 16)
						xA += slopeBC
						zC += sizeX
						xC += slopeCA
					}
				} else {
					zA -= zB
					zB -= zC
					zC *= sizeX
					while (true) {
						zB--
						if (zB < 0) {
							while (true) {
								zA--
								if (zA < 0) {
									return
								}
								fillSpan(xB shr 16, zC, shade, xC shr 16)
								xC += slopeCA
								xB += slopeAB
								zC += sizeX
							}
						}
						fillSpan(xA shr 16, zC, shade, xC shr 16)
						xC += slopeCA
						xA += slopeBC
						zC += sizeX
					}
				}
			} else {
				xC = xC shl 16
				xB = xC
				xA = xA shl 16
				if (zC < 0) {
					xB -= slopeBC * zC
					xC -= zC * slopeCA
					zC = 0
				}
				if (zA < 0) {
					xA -= zA * slopeAB
					zA = 0
				}
				if (slopeCA > slopeBC) {
					zB -= zA
					zA -= zC
					zC = sizeX * zC
					while (true) {
						zA--
						if (zA < 0) {
							while (true) {
								zB--
								if (zB < 0) {
									return
								}
								fillSpan(xB shr 16, zC, shade, xA shr 16)
								xB += slopeBC
								xA += slopeAB
								zC += sizeX
							}
						}
						fillSpan(xB shr 16, zC, shade, xC shr 16)
						zC += sizeX
						xC += slopeCA
						xB += slopeBC
					}
				} else {
					zB -= zA
					zA -= zC
					zC *= sizeX
					while (true) {
						zA--
						if (zA < 0) {
							while (true) {
								zB--
								if (zB < 0) {
									return
								}
								fillSpan(xA shr 16, zC, shade, xB shr 16)
								zC += sizeX
								xB += slopeBC
								xA += slopeAB
							}
						}
						fillSpan(xC shr 16, zC, shade, xB shr 16)
						xC += slopeCA
						xB += slopeBC
						zC += sizeX
					}
				}
			}
		} else if (zC >= zA) {
			xB = xB shl 16
			xC = xB
			if (zB < 0) {
				xC -= zB * slopeAB
				xB -= slopeBC * zB
				zB = 0
			}
			xA = xA shl 16
			if (zA < 0) {
				xA -= zA * slopeCA
				zA = 0
			}
			if (slopeAB < slopeBC) {
				zC -= zA
				zA -= zB
				zB = sizeX * zB
				while (true) {
					zA--
					if (zA < 0) {
						while (true) {
							zC--
							if (zC < 0) {
								return
							}
							fillSpan(xA shr 16, zB, shade, xB shr 16)
							xA += slopeCA
							xB += slopeBC
							zB += sizeX
						}
					}
					fillSpan(xC shr 16, zB, shade, xB shr 16)
					xC += slopeAB
					xB += slopeBC
					zB += sizeX
				}
			} else {
				zC -= zA
				zA -= zB
				zB *= sizeX
				while (true) {
					zA--
					if (zA < 0) {
						while (true) {
							zC--
							if (zC < 0) {
								return
							}
							fillSpan(xB shr 16, zB, shade, xA shr 16)
							xB += slopeBC
							xA += slopeCA
							zB += sizeX
						}
					}
					fillSpan(xB shr 16, zB, shade, xC shr 16)
					xB += slopeBC
					xC += slopeAB
					zB += sizeX
				}
			}
		} else {
			xB = xB shl 16
			xA = xB
			if (zB < 0) {
				xA -= slopeAB * zB
				xB -= slopeBC * zB
				zB = 0
			}
			xC = xC shl 16
			if (zC < 0) {
				xC -= slopeCA * zC
				zC = 0
			}
			if (zB != zC && slopeBC > slopeAB || zC == zB && slopeAB > slopeCA) {
				zA -= zC
				zC -= zB
				zB = sizeX * zB
				while (true) {
					zC--
					if (zC < 0) {
						while (true) {
							zA--
							if (zA < 0) {
								return
							}
							fillSpan(xA shr 16, zB, shade, xC shr 16)
							xA += slopeAB
							xC += slopeCA
							zB += sizeX
						}
					}
					fillSpan(xA shr 16, zB, shade, xB shr 16)
					zB += sizeX
					xA += slopeAB
					xB += slopeBC
				}
			} else {
				zA -= zC
				zC -= zB
				zB = sizeX * zB
				while (true) {
					zC--
					if (zC < 0) {
						while (true) {
							zA--
							if (zA < 0) {
								return
							}
							fillSpan(xC shr 16, zB, shade, xA shr 16)
							zB += sizeX
							xC += slopeCA
							xA += slopeAB
						}
					}
					fillSpan(xB shr 16, zB, shade, xA shr 16)
					xA += slopeAB
					zB += sizeX
					xB += slopeBC
				}
			}
		}
	}

	@OriginalMember(owner = "client!uc", name = "a", descriptor = "(BII)Z")
	fun fitsDimension(@OriginalArg(1) sizeZ: Int, @OriginalArg(2) sizeX: Int): Boolean {
		return sizeZ * sizeX <= this.shade.size
	}

	companion object {
		@OriginalMember(owner = "client!wq", name = "a", descriptor = "(IIII[BI)V")
		@JvmStatic
		fun fillSpan(@OriginalArg(0) startX: Int, @OriginalArg(1) offset: Int, @OriginalArg(4) data: ByteArray?, @OriginalArg(5) endX: Int) {
			var offset: Int = offset
			if (endX <= startX) {
				return
			}
			offset += startX
			@Pc(28) var count: Int = (endX - startX) shr 2
			while (true) {
				count--
				if (count < 0) {
					count = (endX - startX) and 0x3
					while (true) {
						count--
						if (count < 0) {
							return
						}
						data!![offset++] = 1
					}
				}
				@Pc(33) val offset1: Int = offset + 1
				data!![offset] = 1
				@Pc(38) val offset2: Int = offset1 + 1
				data!![offset1] = 1
				@Pc(43) val offset3: Int = offset2 + 1
				data!![offset2] = 1
				offset = offset3 + 1
				data!![offset3] = 1
			}
		}
	}
}
