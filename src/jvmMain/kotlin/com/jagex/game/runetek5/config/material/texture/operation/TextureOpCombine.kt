package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!pda")
class TextureOpCombine @OriginalMember(owner = "client!pda", name = "<init>", descriptor = "()V") constructor() : TextureOperation(2, false) {

	@OriginalMember(owner = "client!pda", name = "M", descriptor = "I")
	private var blendMode: Int = 6

	@OriginalMember(owner = "client!pda", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.blendMode = packet!!.readUByte()
		} else if (opcode == 1) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!pda", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(18) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(28) val first: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(34) val second: Array<IntArray?> = this.getChildColorOutput(row, 1)!!
			@Pc(38) val outputRed: IntArray = output[0]!!
			@Pc(42) val outputGreen: IntArray = output[1]!!
			@Pc(46) val outputBlue: IntArray = output[2]!!
			@Pc(50) val firstRed: IntArray = first[0]!!
			@Pc(54) val firstGreen: IntArray = first[1]!!
			@Pc(58) val firstBlue: IntArray = first[2]!!
			@Pc(62) val secondRed: IntArray = second[0]!!
			@Pc(66) val secondGreen: IntArray = second[1]!!
			@Pc(70) val secondBlue: IntArray = second[2]!!
			@Pc(73) var blendMode: Int = this.blendMode
			if (blendMode == 1) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					outputRed[blendMode] = secondRed[blendMode] + firstRed[blendMode]
					outputGreen[blendMode] = firstGreen[blendMode] + secondGreen[blendMode]
					outputBlue[blendMode] = firstBlue[blendMode] + secondBlue[blendMode]
					blendMode++
				}
			} else if (blendMode == 2) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					outputRed[blendMode] = firstRed[blendMode] - secondRed[blendMode]
					outputGreen[blendMode] = firstGreen[blendMode] - secondGreen[blendMode]
					outputBlue[blendMode] = firstBlue[blendMode] - secondBlue[blendMode]
					blendMode++
				}
			} else if (blendMode == 3) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					outputRed[blendMode] = (secondRed[blendMode] * firstRed[blendMode]) shr 12
					outputGreen[blendMode] = (secondGreen[blendMode] * firstGreen[blendMode]) shr 12
					outputBlue[blendMode] = (firstBlue[blendMode] * secondBlue[blendMode]) shr 12
					blendMode++
				}
			} else {
				@Pc(265) var secondRedValue: Int
				@Pc(273) var secondGreenValue: Int
				@Pc(269) var secondBlueValue: Int
				if (blendMode == 4) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						secondRedValue = secondRed[blendMode]
						secondBlueValue = secondBlue[blendMode]
						secondGreenValue = secondGreen[blendMode]
						outputRed[blendMode] = if (secondRedValue == 0) 4096 else (firstRed[blendMode] shl 12) / secondRedValue
						outputGreen[blendMode] = if (secondGreenValue == 0) 4096 else (firstGreen[blendMode] shl 12) / secondGreenValue
						outputBlue[blendMode] = if (secondBlueValue == 0) 4096 else (firstBlue[blendMode] shl 12) / secondBlueValue
						blendMode++
					}
				} else if (blendMode == 5) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						outputRed[blendMode] = 4096 - (((4096 - secondRed[blendMode]) * (4096 - firstRed[blendMode])) shr 12)
						outputGreen[blendMode] = 4096 - (((4096 - secondGreen[blendMode]) * (4096 - firstGreen[blendMode])) shr 12)
						outputBlue[blendMode] = 4096 - (((4096 - secondBlue[blendMode]) * (4096 - firstBlue[blendMode])) shr 12)
						blendMode++
					}
				} else if (blendMode == 6) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						secondRedValue = secondRed[blendMode]
						secondBlueValue = secondBlue[blendMode]
						secondGreenValue = secondGreen[blendMode]
						outputRed[blendMode] = if (secondRedValue >= 2048) 4096 - (((4096 - firstRed[blendMode]) * (-secondRedValue + 4096)) shr 11) else (firstRed[blendMode] * secondRedValue) shr 11
						outputGreen[blendMode] = if (secondGreenValue >= 2048) 4096 - (((4096 - secondGreenValue) * (4096 - firstGreen[blendMode])) shr 11) else (secondGreenValue * firstGreen[blendMode]) shr 11
						outputBlue[blendMode] = if (secondBlueValue < 2048) (secondBlueValue * firstBlue[blendMode]) shr 11 else 4096 - (((4096 - firstBlue[blendMode]) * (-secondBlueValue + 4096)) shr 11)
						blendMode++
					}
				} else {
					@Pc(525) var firstRedValue: Int
					@Pc(521) var firstGreenValue: Int
					@Pc(517) var firstBlueValue: Int
					if (blendMode == 7) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstBlueValue = firstBlue[blendMode]
							firstGreenValue = firstGreen[blendMode]
							firstRedValue = firstRed[blendMode]
							outputRed[blendMode] = if (firstRedValue == 4096) 4096 else (secondRed[blendMode] shl 12) / (4096 - firstRedValue)
							outputGreen[blendMode] = if (firstGreenValue == 4096) 4096 else (secondGreen[blendMode] shl 12) / (4096 - firstGreenValue)
							outputBlue[blendMode] = if (firstBlueValue == 4096) 4096 else (secondBlue[blendMode] shl 12) / (4096 - firstBlueValue)
							blendMode++
						}
					} else if (blendMode == 8) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstGreenValue = firstGreen[blendMode]
							firstRedValue = firstRed[blendMode]
							firstBlueValue = firstBlue[blendMode]
							outputRed[blendMode] = if (firstRedValue == 0) 0 else 4096 - ((4096 - secondRed[blendMode]) shl 12) / firstRedValue
							outputGreen[blendMode] = if (firstGreenValue == 0) 0 else 4096 - ((4096 - secondGreen[blendMode]) shl 12) / firstGreenValue
							outputBlue[blendMode] = if (firstBlueValue == 0) 0 else 4096 - ((4096 - secondBlue[blendMode]) shl 12) / firstBlueValue
							blendMode++
						}
					} else if (blendMode == 9) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstGreenValue = firstGreen[blendMode]
							secondGreenValue = secondGreen[blendMode]
							secondBlueValue = secondBlue[blendMode]
							secondRedValue = secondRed[blendMode]
							firstBlueValue = firstBlue[blendMode]
							firstRedValue = firstRed[blendMode]
							outputRed[blendMode] = if (firstRedValue < secondRedValue) firstRedValue else secondRedValue
							outputGreen[blendMode] = if (firstGreenValue < secondGreenValue) firstGreenValue else secondGreenValue
							outputBlue[blendMode] = if (secondBlueValue > firstBlueValue) firstBlueValue else secondBlueValue
							blendMode++
						}
					} else if (blendMode == 10) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstGreenValue = firstGreen[blendMode]
							firstBlueValue = firstBlue[blendMode]
							secondGreenValue = secondGreen[blendMode]
							secondRedValue = secondRed[blendMode]
							secondBlueValue = secondBlue[blendMode]
							firstRedValue = firstRed[blendMode]
							outputRed[blendMode] = if (secondRedValue < firstRedValue) firstRedValue else secondRedValue
							outputGreen[blendMode] = if (secondGreenValue >= firstGreenValue) secondGreenValue else firstGreenValue
							outputBlue[blendMode] = if (firstBlueValue <= secondBlueValue) secondBlueValue else firstBlueValue
							blendMode++
						}
					} else if (blendMode == 11) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							secondBlueValue = secondBlue[blendMode]
							firstGreenValue = firstGreen[blendMode]
							firstBlueValue = firstBlue[blendMode]
							secondRedValue = secondRed[blendMode]
							firstRedValue = firstRed[blendMode]
							secondGreenValue = secondGreen[blendMode]
							outputRed[blendMode] = if (secondRedValue < firstRedValue) firstRedValue - secondRedValue else secondRedValue - firstRedValue
							outputGreen[blendMode] = if (firstGreenValue <= secondGreenValue) secondGreenValue - firstGreenValue else -secondGreenValue + firstGreenValue
							outputBlue[blendMode] = if (firstBlueValue <= secondBlueValue) secondBlueValue - firstBlueValue else -secondBlueValue + firstBlueValue
							blendMode++
						}
					} else if (blendMode == 12) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstRedValue = firstRed[blendMode]
							firstBlueValue = firstBlue[blendMode]
							secondBlueValue = secondBlue[blendMode]
							secondRedValue = secondRed[blendMode]
							firstGreenValue = firstGreen[blendMode]
							secondGreenValue = secondGreen[blendMode]
							outputRed[blendMode] = secondRedValue + firstRedValue - ((firstRedValue * secondRedValue) shr 11)
							outputGreen[blendMode] = firstGreenValue + secondGreenValue - ((firstGreenValue * secondGreenValue) shr 11)
							outputBlue[blendMode] = secondBlueValue + firstBlueValue - ((firstBlueValue * secondBlueValue) shr 11)
							blendMode++
						}
					}
				}
			}
		}
		return output
	}

	@OriginalMember(owner = "client!pda", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(19) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(29) val first: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(35) val second: IntArray = this.getChildMonochromeOutput(row, 1)!!
			@Pc(38) var blendMode: Int = this.blendMode
			if (blendMode == 1) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					output[blendMode] = first[blendMode] + second[blendMode]
					blendMode++
				}
			} else if (blendMode == 2) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					output[blendMode] = first[blendMode] - second[blendMode]
					blendMode++
				}
			} else if (blendMode == 3) {
				blendMode = 0
				while (blendMode < EnvironmentLight.textureOpWidth) {
					output[blendMode] = (second[blendMode] * first[blendMode]) shr 12
					blendMode++
				}
			} else {
				@Pc(171) var secondValue: Int
				if (blendMode == 4) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						secondValue = second[blendMode]
						output[blendMode] = if (secondValue == 0) 4096 else (first[blendMode] shl 12) / secondValue
						blendMode++
					}
				} else if (blendMode == 5) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						output[blendMode] = 4096 - (((4096 - first[blendMode]) * (4096 - second[blendMode])) shr 12)
						blendMode++
					}
				} else if (blendMode == 6) {
					blendMode = 0
					while (blendMode < EnvironmentLight.textureOpWidth) {
						secondValue = second[blendMode]
						output[blendMode] = if (secondValue >= 2048) 4096 - (((4096 - secondValue) * (4096 - first[blendMode])) shr 11) else (secondValue * first[blendMode]) shr 11
						blendMode++
					}
				} else {
					@Pc(278) var firstValue: Int
					if (blendMode == 7) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstValue = first[blendMode]
							output[blendMode] = if (firstValue == 4096) 4096 else (second[blendMode] shl 12) / (4096 - firstValue)
							blendMode++
						}
					} else if (blendMode == 8) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							firstValue = first[blendMode]
							output[blendMode] = if (firstValue == 0) 0 else 4096 - ((4096 - second[blendMode]) shl 12) / firstValue
							blendMode++
						}
					} else if (blendMode == 9) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							secondValue = second[blendMode]
							firstValue = first[blendMode]
							output[blendMode] = if (firstValue < secondValue) firstValue else secondValue
							blendMode++
						}
					} else if (blendMode == 10) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							secondValue = second[blendMode]
							firstValue = first[blendMode]
							output[blendMode] = if (firstValue > secondValue) firstValue else secondValue
							blendMode++
						}
					} else if (blendMode == 11) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							secondValue = second[blendMode]
							firstValue = first[blendMode]
							output[blendMode] = if (firstValue <= secondValue) secondValue - firstValue else firstValue - secondValue
							blendMode++
						}
					} else if (blendMode == 12) {
						blendMode = 0
						while (blendMode < EnvironmentLight.textureOpWidth) {
							secondValue = second[blendMode]
							firstValue = first[blendMode]
							output[blendMode] = firstValue + secondValue - ((firstValue * secondValue) shr 11)
							blendMode++
						}
					}
				}
			}
		}
		return output
	}
}
