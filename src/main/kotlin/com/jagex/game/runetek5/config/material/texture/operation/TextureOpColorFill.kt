package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!qha")
class TextureOpColorFill @OriginalMember(owner = "client!qha", name = "<init>", descriptor = "(I)V") private constructor(@OriginalArg(0) rgb: Int) : TextureOperation(0, false) {

	@OriginalMember(owner = "client!qha", name = "I", descriptor = "I")
	private var redComponent: Int = 0

	@OriginalMember(owner = "client!qha", name = "O", descriptor = "I")
	private var blueComponent: Int = 0

	@OriginalMember(owner = "client!qha", name = "Q", descriptor = "I")
	private var greenComponent: Int = 0

	init {
		this.setColour(rgb)
	}

	@OriginalMember(owner = "client!qha", name = "<init>", descriptor = "()V")
	constructor() : this(0)

	@OriginalMember(owner = "client!qha", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(13) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(21) val outputRed: IntArray = output[0]!!
			@Pc(25) val outputGreen: IntArray = output[1]!!
			@Pc(29) val outputBlue: IntArray = output[2]!!
			@Pc(31) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				outputRed[i] = this.redComponent
				outputGreen[i] = this.greenComponent
				outputBlue[i] = this.blueComponent
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!qha", name = "e", descriptor = "(II)V")
	private fun setColour(@OriginalArg(1) rgb: Int) {
		this.redComponent = (rgb shr 12) and 0xFF0
		this.greenComponent = (rgb shr 4) and 0xFF0
		this.blueComponent = (rgb and 0xFF) shl 4
	}

	@OriginalMember(owner = "client!qha", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.setColour(packet!!.readUMedium())
		}
	}
}
