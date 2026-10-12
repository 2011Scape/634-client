package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!gd")
class TextureOpHerringbone @OriginalMember(owner = "client!gd", name = "<init>", descriptor = "()V") constructor() : TextureOperation(0, true) {

	@OriginalMember(owner = "client!gd", name = "I", descriptor = "I")
	private var scaleY: Int = 1

	@OriginalMember(owner = "client!gd", name = "L", descriptor = "I")
	private var threshold: Int = 204

	@OriginalMember(owner = "client!gd", name = "M", descriptor = "I")
	private var scaleX: Int = 1

	@OriginalMember(owner = "client!gd", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.scaleX = packet!!.readUByte()
		} else if (opcode == 1) {
			this.scaleY = packet!!.readUByte()
		} else if (opcode == 2) {
			this.threshold = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!gd", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(17) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(23) val horizontalPos: Int = EnvironmentLight.horizontalTable!![i]
				@Pc(27) val verticalPos: Int = TextureRaster.verticalTable!![row]
				@Pc(34) var local34: Int = (this.scaleX * horizontalPos) shr 12
				@Pc(41) val tileY: Int = (this.scaleY * verticalPos) shr 12
				@Pc(51) val offsetX: Int = horizontalPos % (4096 / this.scaleX) * this.scaleX
				@Pc(61) val offsetY: Int = verticalPos % (4096 / this.scaleY) * this.scaleY
				if (this.threshold > offsetY) {
					local34 -= tileY
					while (local34 < 0) {
						local34 += 4
					}
					while (local34 > 3) {
						local34 -= 4
					}
					if (local34 != 1) {
						output[i] = 0
						i++
						continue
					}
					if (this.threshold > offsetX) {
						output[i] = 0
						i++
						continue
					}
				}
				if (this.threshold > offsetX) {
					local34 -= tileY
					while (local34 < 0) {
						local34 += 4
					}
					while (local34 > 3) {
						local34 -= 4
					}
					if (local34 > 0) {
						output[i] = 0
						i++
						continue
					}
				}
				output[i] = 4096
				i++
			}
		}
		return output
	}
}
