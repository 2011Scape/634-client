package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ClientTrigger

@OriginalClass("client!fc")
class GpuShadow @OriginalMember(owner = "client!fc", name = "<init>", descriptor = "(Lclient!wga;II)V") constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) sizeX: Int, @OriginalArg(2) sizeZ: Int) : Shadow() {

	@OriginalMember(owner = "client!fc", name = "F", descriptor = "I")
	@JvmField
	var sizeX: Int = 0

	@OriginalMember(owner = "client!fc", name = "G", descriptor = "I")
	@JvmField
	var sizeZ: Int = 0

	@OriginalMember(owner = "client!fc", name = "H", descriptor = "I")
	@JvmField
	var startX: Int = 0

	@OriginalMember(owner = "client!fc", name = "O", descriptor = "I")
	@JvmField
	var startZ: Int = 0

	@OriginalMember(owner = "client!fc", name = "K", descriptor = "[B")
	@JvmField
	val shade: ByteArray = ByteArray(sizeZ * sizeX)

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIIII)V")
	fun setDimension(@OriginalArg(0) endX: Int, @OriginalArg(2) endZ: Int, @OriginalArg(3) startZ: Int, @OriginalArg(4) startX: Int) {
		this.sizeX = endX - startX
		this.sizeZ = endZ - startZ
		this.startX = startX
		this.startZ = startZ
	}

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIIIIII)V")
	fun fillTriangle(@OriginalArg(0) zA: Int, @OriginalArg(2) xA: Int, @OriginalArg(3) xB: Int, @OriginalArg(4) zB: Int, @OriginalArg(5) zC: Int, @OriginalArg(6) xC: Int) {
		var zA: Int = zA
		var xA: Int = xA
		var xB: Int = xB
		var zB: Int = zB
		var zC: Int = zC
		var xC: Int = xC
		@Pc(7) var slopeAB: Int = 0
		if (zB != zA) {
			slopeAB = ((xB - xA) shl 16) / (zB - zA)
		}
		@Pc(24) var slopeBC: Int = 0
		if (zC != zB) {
			slopeBC = ((xC - xB) shl 16) / (zC - zB)
		}
		@Pc(53) var slopeCA: Int = 0
		if (zA != zC) {
			slopeCA = ((xA - xC) shl 16) / (zA - zC)
		}
		if (zB >= zA && zA <= zC) {
			if (zB >= zC) {
				xA = xA shl 16
				xB = xA
				xC = xC shl 16
				if (zA < 0) {
					xB -= zA * slopeCA
					xA -= slopeAB * zA
					zA = 0
				}
				if (zC < 0) {
					xC -= zC * slopeBC
					zC = 0
				}
				if (zC != zA && slopeCA < slopeAB || zC == zA && slopeAB < slopeBC) {
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
								fillShadowSpan(zA, xC shr 16, shade, xA shr 16)
								zA += sizeX
								xC += slopeBC
								xA += slopeAB
							}
						}
						fillShadowSpan(zA, xB shr 16, shade, xA shr 16)
						zA += sizeX
						xA += slopeAB
						xB += slopeCA
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
								fillShadowSpan(zA, xA shr 16, shade, xC shr 16)
								zA += sizeX
								xC += slopeBC
								xA += slopeAB
							}
						}
						fillShadowSpan(zA, xA shr 16, shade, xB shr 16)
						xB += slopeCA
						xA += slopeAB
						zA += sizeX
					}
				}
			} else {
				xA = xA shl 16
				xC = xA
				if (zA < 0) {
					xA -= slopeAB * zA
					xC -= slopeCA * zA
					zA = 0
				}
				xB = xB shl 16
				if (zB < 0) {
					xB -= zB * slopeBC
					zB = 0
				}
				if ((zB == zA || slopeAB <= slopeCA) && (zA != zB || slopeBC >= slopeCA)) {
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
								fillShadowSpan(zA, xB shr 16, shade, xC shr 16)
								xB += slopeBC
								zA += sizeX
								xC += slopeCA
							}
						}
						fillShadowSpan(zA, xA shr 16, shade, xC shr 16)
						xC += slopeCA
						zA += sizeX
						xA += slopeAB
					}
				} else {
					zC -= zB
					zB -= zA
					zA *= sizeX
					while (true) {
						zB--
						if (zB < 0) {
							while (true) {
								zC--
								if (zC < 0) {
									return
								}
								fillShadowSpan(zA, xC shr 16, shade, xB shr 16)
								zA += sizeX
								xC += slopeCA
								xB += slopeBC
							}
						}
						fillShadowSpan(zA, xC shr 16, shade, xA shr 16)
						xA += slopeAB
						xC += slopeCA
						zA += sizeX
					}
				}
			}
		} else if (zB <= zC) {
			if (zC >= zA) {
				xB = xB shl 16
				xC = xB
				if (zB < 0) {
					xB -= zB * slopeBC
					xC -= zB * slopeAB
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
								fillShadowSpan(zB, xA shr 16, shade, xB shr 16)
								xB += slopeBC
								zB += sizeX
								xA += slopeCA
							}
						}
						fillShadowSpan(zB, xC shr 16, shade, xB shr 16)
						xC += slopeAB
						zB += sizeX
						xB += slopeBC
					}
				} else {
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
								fillShadowSpan(zB, xB shr 16, shade, xA shr 16)
								xB += slopeBC
								xA += slopeCA
								zB += sizeX
							}
						}
						fillShadowSpan(zB, xB shr 16, shade, xC shr 16)
						zB += sizeX
						xB += slopeBC
						xC += slopeAB
					}
				}
			} else {
				xB = xB shl 16
				xA = xB
				if (zB < 0) {
					xB -= slopeBC * zB
					xA -= slopeAB * zB
					zB = 0
				}
				xC = xC shl 16
				if (zC < 0) {
					xC -= zC * slopeCA
					zC = 0
				}
				if ((zB == zC || slopeBC <= slopeAB) && (zB != zC || slopeAB <= slopeCA)) {
					zA -= zC
					zC -= zB
					zB *= sizeX
					while (true) {
						zC--
						if (zC < 0) {
							while (true) {
								zA--
								if (zA < 0) {
									return
								}
								fillShadowSpan(zB, xC shr 16, shade, xA shr 16)
								zB += sizeX
								xA += slopeAB
								xC += slopeCA
							}
						}
						fillShadowSpan(zB, xB shr 16, shade, xA shr 16)
						zB += sizeX
						xA += slopeAB
						xB += slopeBC
					}
				} else {
					zA -= zC
					zC -= zB
					zB *= sizeX
					while (true) {
						zC--
						if (zC < 0) {
							while (true) {
								zA--
								if (zA < 0) {
									return
								}
								fillShadowSpan(zB, xA shr 16, shade, xC shr 16)
								zB += sizeX
								xC += slopeCA
								xA += slopeAB
							}
						}
						fillShadowSpan(zB, xA shr 16, shade, xB shr 16)
						xB += slopeBC
						zB += sizeX
						xA += slopeAB
					}
				}
			}
		} else if (zA < zB) {
			xC = xC shl 16
			xB = xC
			xA = xA shl 16
			if (zC < 0) {
				xC -= slopeCA * zC
				xB -= slopeBC * zC
				zC = 0
			}
			if (zA < 0) {
				xA -= slopeAB * zA
				zA = 0
			}
			if (slopeBC < slopeCA) {
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
							fillShadowSpan(zC, xB shr 16, shade, xA shr 16)
							xA += slopeAB
							zC += sizeX
							xB += slopeBC
						}
					}
					fillShadowSpan(zC, xB shr 16, shade, xC shr 16)
					zC += sizeX
					xB += slopeBC
					xC += slopeCA
				}
			} else {
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
							fillShadowSpan(zC, xA shr 16, shade, xB shr 16)
							xA += slopeAB
							zC += sizeX
							xB += slopeBC
						}
					}
					fillShadowSpan(zC, xC shr 16, shade, xB shr 16)
					xB += slopeBC
					zC += sizeX
					xC += slopeCA
				}
			}
		} else {
			xC = xC shl 16
			xA = xC
			if (zC < 0) {
				xC -= slopeCA * zC
				xA -= zC * slopeBC
				zC = 0
			}
			xB = xB shl 16
			if (zB < 0) {
				xB -= slopeAB * zB
				zB = 0
			}
			if (slopeCA <= slopeBC) {
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
							fillShadowSpan(zC, xC shr 16, shade, xB shr 16)
							xB += slopeAB
							xC += slopeCA
							zC += sizeX
						}
					}
					fillShadowSpan(zC, xC shr 16, shade, xA shr 16)
					zC += sizeX
					xA += slopeBC
					xC += slopeCA
				}
			} else {
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
							fillShadowSpan(zC, xB shr 16, shade, xC shr 16)
							xC += slopeCA
							zC += sizeX
							xB += slopeAB
						}
					}
					fillShadowSpan(zC, xA shr 16, shade, xC shr 16)
					xA += slopeBC
					xC += slopeCA
					zC += sizeX
				}
			}
		}
	}

	@OriginalMember(owner = "client!fc", name = "d", descriptor = "(I)V")
	fun reset() {
		@Pc(17) var index: Int = -1
		@Pc(23) var limit: Int = shade.size - 8
		while (limit > index) {
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
		while (index < shade.size - 1) {
			index++
			shade[index] = 0
		}
	}

	@OriginalMember(owner = "client!fc", name = "a", descriptor = "(IIB)Z")
	fun fitsDimension(@OriginalArg(0) sizeX: Int, @OriginalArg(1) sizeZ: Int): Boolean {
		return this.shade.size >= sizeZ * sizeX
	}

	companion object {
		init {
			ClientTrigger("", 76)
		}

		@OriginalMember(owner = "client!iba", name = "a", descriptor = "(II[BIII)V")
		@JvmStatic
		fun fillShadowSpan(@OriginalArg(0) rowOffset: Int, @OriginalArg(1) startX: Int, @OriginalArg(2) dst: ByteArray?, @OriginalArg(5) endX: Int) {
			var rowOffset: Int = rowOffset
			if (startX >= endX) {
				return
			}
			rowOffset += startX
			@Pc(24) var remaining: Int = (endX - startX) shr 2
			while (true) {
				remaining--
				if (remaining < 0) {
					remaining = (endX - startX) and 0x3
					while (true) {
						remaining--
						if (remaining < 0) {
							return
						}
						dst!![rowOffset++] = 1
					}
				}
				@Pc(29) val rowOffset1: Int = rowOffset + 1
				dst!![rowOffset] = 1
				@Pc(34) val rowOffset2: Int = rowOffset1 + 1
				dst!![rowOffset1] = 1
				@Pc(39) val rowOffset3: Int = rowOffset2 + 1
				dst!![rowOffset2] = 1
				rowOffset = rowOffset3 + 1
				dst!![rowOffset3] = 1
			}
		}
	}
}
