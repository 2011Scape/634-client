/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlShadow (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.lru.Shadow

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdShadow constructor(glToolkit: HdToolkit?, sizeX: Int, sizeZ: Int) : Shadow() {

	/** 634HD: cast by a model with water faces (HdGround keeps these with the sun's shadows on). */
	@JvmField
	var hdWater: Boolean = false

	/**
	 * 634HD: the same shadow cast by the water faces only, for the dry ground round a water model with
	 * the sun's shadows on (HdGround.waterShadowMap). Both are cast when the model's shadow is, so a
	 * cached shadow (LocTypeList.modelAndShadowCache) fits whichever the switch asks for.
	 */
	@JvmField
	var hdWaterFaces: HdShadow? = null

	@JvmField
	var sizeX: Int = 0

	@JvmField
	var startZ: Int = 0

	@JvmField
	var sizeZ: Int = 0

	@JvmField
	var startX: Int = 0

	@JvmField
	val shade: ByteArray = ByteArray(sizeX * sizeZ)

	fun setDimension(endZ: Int, endX: Int, startZ: Int, startX: Int) {
		this.startZ = startZ
		this.startX = startX
		this.sizeZ = endZ - startZ
		this.sizeX = endX - startX
	}

	fun reset() {
		var index: Int = -1
		var limit: Int = shade.size - 8
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

	fun fillTriangle(xC: Int, xA: Int, zA: Int, xB: Int, zC: Int, zB: Int) {
		var xC: Int = xC
		var xA: Int = xA
		var zA: Int = zA
		var xB: Int = xB
		var zC: Int = zC
		var zB: Int = zB
		var slopeAB: Int = 0
		if (zB != zA) {
			slopeAB = ((xB - xA) shl 16) / (zB - zA)
		}
		var slopeBC: Int = 0
		if (zB != zC) {
			slopeBC = ((xC - xB) shl 16) / (zC - zB)
		}
		var slopeCA: Int = 0
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

	fun fitsDimension(sizeZ: Int, sizeX: Int): Boolean {
		return sizeZ * sizeX <= this.shade.size
	}

	companion object {
		@JvmStatic
		fun fillSpan(startX: Int, offset: Int, data: ByteArray?, endX: Int) {
			var offset: Int = offset
			if (endX <= startX) {
				return
			}
			offset += startX
			var count: Int = (endX - startX) shr 2
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
				val offset1: Int = offset + 1
				data!![offset] = 1
				val offset2: Int = offset1 + 1
				data!![offset1] = 1
				val offset3: Int = offset2 + 1
				data!![offset2] = 1
				offset = offset3 + 1
				data!![offset3] = 1
			}
		}
	}
}
