package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!vn")
class TextureOpTile : TextureOperation {

	@OriginalMember(owner = "client!vn", name = "J", descriptor = "I")
	private var tileCountX: Int = 4

	@OriginalMember(owner = "client!vn", name = "O", descriptor = "I")
	private var tileCountY: Int = 4

	@OriginalMember(owner = "client!vn", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!vn", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(25) val tileWidth: Int = EnvironmentLight.textureOpWidth / this.tileCountX
			@Pc(30) val tileHeight: Int = TextureOperation.currentHeight / this.tileCountY
			@Pc(46) val input: Array<IntArray?>
			if (tileHeight > 0) {
				@Pc(36) val tileY: Int = count % tileHeight
				input = this.getChildColorOutput(TextureOperation.currentHeight * tileY / tileHeight, 0)!!
			} else {
				input = this.getChildColorOutput(0, 0)!!
			}
			@Pc(58) val inputRed: IntArray = input[0]!!
			@Pc(62) val inputGreen: IntArray = input[1]!!
			@Pc(66) val inputBlue: IntArray = input[2]!!
			@Pc(70) val outputRed: IntArray = output[0]!!
			@Pc(74) val outputGreen: IntArray = output[1]!!
			@Pc(78) val outputBlue: IntArray = output[2]!!
			@Pc(80) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(89) var sourceX: Int
				if (tileWidth <= 0) {
					sourceX = 0
				} else {
					@Pc(95) val tileX: Int = i % tileWidth
					sourceX = EnvironmentLight.textureOpWidth * tileX / tileWidth
				}
				outputRed[i] = inputRed[sourceX]
				outputGreen[i] = inputGreen[sourceX]
				outputBlue[i] = inputBlue[sourceX]
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!vn", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.tileCountX = packet!!.readUByte()
		} else if (opcode == 1) {
			this.tileCountY = packet!!.readUByte()
		}
	}

	@OriginalMember(owner = "client!vn", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(16) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(25) val tileWidth: Int = EnvironmentLight.textureOpWidth / this.tileCountX
			@Pc(30) val tileHeight: Int = TextureOperation.currentHeight / this.tileCountY
			@Pc(49) val input: IntArray
			@Pc(39) var local39: Int
			if (tileHeight > 0) {
				val tileY: Int = row % tileHeight
				input = this.getChildMonochromeOutput(tileY * TextureOperation.currentHeight / tileHeight, 0)!!
			} else {
				input = this.getChildMonochromeOutput(0, 0)!!
			}
			var column: Int = 0
			while (column < EnvironmentLight.textureOpWidth) {
				if (tileWidth <= 0) {
					output[column] = input[0]
				} else {
					@Pc(75) val tileX: Int = column % tileWidth
					output[column] = input[EnvironmentLight.textureOpWidth * tileX / tileWidth]
				}
				column++
			}
		}
		return output
	}
}
