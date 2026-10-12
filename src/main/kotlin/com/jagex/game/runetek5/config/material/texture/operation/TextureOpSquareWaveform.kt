package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!st")
class TextureOpSquareWaveform : TextureOperation {

	@OriginalMember(owner = "client!st", name = "R", descriptor = "[I")
	private var stepStartBoundaries: IntArray? = null

	@OriginalMember(owner = "client!st", name = "S", descriptor = "[I")
	private var pulseEndBoundaries: IntArray? = null

	@OriginalMember(owner = "client!st", name = "K", descriptor = "I")
	private var waveAxis: Int = 0

	@OriginalMember(owner = "client!st", name = "O", descriptor = "I")
	private var stepCount: Int = 10

	@OriginalMember(owner = "client!st", name = "T", descriptor = "I")
	private var dutyCycle: Int = 2048

	@OriginalMember(owner = "client!st", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!st", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(17) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(25) val verticalPos: Int = TextureRaster.verticalTable!![row]
			@Pc(34) var local34: Int
			if (this.waveAxis == 0) {
				@Pc(134) var variableToSet: Short = 0
				var stepIndex: Int = 0
				while (stepIndex < this.stepCount) {
					if (this.stepStartBoundaries!![stepIndex] <= verticalPos && this.stepStartBoundaries!![stepIndex + 1] > verticalPos) {
						if (this.pulseEndBoundaries!![stepIndex] > verticalPos) {
							variableToSet = 4096
						}
						break
					}
					stepIndex++
				}
				Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, variableToSet.toInt())
			} else {
				@Pc(30) var i: Int = 0
				while (i < EnvironmentLight.textureOpWidth) {
					var wavePosition: Int = 0
					@Pc(36) var value: Short = 0
					@Pc(40) val horizontalPos: Int = EnvironmentLight.horizontalTable!![i]
					@Pc(43) var waveAxis: Int = this.waveAxis
					if (waveAxis == 1) {
						wavePosition = horizontalPos
					} else if (waveAxis == 2) {
						wavePosition = ((horizontalPos + verticalPos - 4096) shr 1) + 2048
					} else if (waveAxis == 3) {
						wavePosition = ((horizontalPos - verticalPos) shr 1) + 2048
					}
					waveAxis = 0
					while (waveAxis < this.stepCount) {
						if (wavePosition >= this.stepStartBoundaries!![waveAxis] && wavePosition < this.stepStartBoundaries!![waveAxis + 1]) {
							if (this.pulseEndBoundaries!![waveAxis] > wavePosition) {
								value = 4096
							}
							break
						}
						waveAxis++
					}
					output[i] = value.toInt()
					i++
				}
			}
		}
		return output
	}

	@OriginalMember(owner = "client!st", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.computeStepBoundaries()
	}

	@OriginalMember(owner = "client!st", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.stepCount = packet!!.readUByte()
		} else if (opcode == 1) {
			this.dutyCycle = packet!!.readUShort()
		} else if (opcode == 2) {
			this.waveAxis = packet!!.readUByte()
		}
	}

	@OriginalMember(owner = "client!st", name = "b", descriptor = "(B)V")
	private fun computeStepBoundaries() {
		@Pc(7) var position: Int = 0
		this.pulseEndBoundaries = IntArray(this.stepCount + 1)
		this.stepStartBoundaries = IntArray(this.stepCount + 1)
		@Pc(26) val stepSize: Int = 4096 / this.stepCount
		@Pc(33) val pulseWidth: Int = (stepSize * this.dutyCycle) shr 12
		@Pc(35) var i: Int = 0
		while (i < this.stepCount) {
			this.stepStartBoundaries!![i] = position
			this.pulseEndBoundaries!![i] = pulseWidth + position
			position += stepSize
			i++
		}
		this.stepStartBoundaries!![this.stepCount] = 4096
		this.pulseEndBoundaries!![this.stepCount] = this.pulseEndBoundaries!![0] + 4096
	}
}
