package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureTypeList

@OriginalClass("client!vf")
class TextureOpTexture : TextureOperation {

	@OriginalMember(owner = "client!vf", name = "M", descriptor = "I")
	private var textureWidth: Int = 0

	@OriginalMember(owner = "client!vf", name = "N", descriptor = "[I")
	private var texturePixels: IntArray? = null

	@OriginalMember(owner = "client!vf", name = "R", descriptor = "I")
	private var textureHeight: Int = 0

	@OriginalMember(owner = "client!vf", name = "S", descriptor = "I")
	private var materialId: Int = -1

	@OriginalMember(owner = "client!vf", name = "<init>", descriptor = "()V")
	constructor() : super(0, false)

	@OriginalMember(owner = "client!vf", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(16) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(40) var pixelOffset: Int = this.textureWidth * (if (this.textureHeight == TextureOperation.currentHeight) count else this.textureHeight * count / TextureOperation.currentHeight)
			@Pc(44) val outputRed: IntArray = output[0]!!
			@Pc(48) val outputGreen: IntArray = output[1]!!
			@Pc(52) val outputBlue: IntArray = output[2]!!
			@Pc(62) var x: Int
			@Pc(71) var local71: Int
			if (this.textureWidth == EnvironmentLight.textureOpWidth) {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					val pixel: Int = this.texturePixels!![pixelOffset++]
					outputBlue[x] = (pixel and 0xFF) shl 4
					outputGreen[x] = (pixel shr 4) and 0xFF0
					outputRed[x] = (pixel shr 12) and 0xFF0
					x++
				}
			} else {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					val sourceX: Int = this.textureWidth * x / EnvironmentLight.textureOpWidth
					@Pc(78) val pixel: Int = this.texturePixels!![sourceX + pixelOffset]
					outputBlue[x] = (pixel and 0xFF) shl 4
					outputGreen[x] = (pixel shr 4) and 0xFF0
					outputRed[x] = (pixel shr 12) and 0xFF0
					x++
				}
			}
		}
		return output
	}

	@OriginalMember(owner = "client!vf", name = "b", descriptor = "(III)V")
	override fun initCache(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
		super.initCache(height, width)
		if (this.materialId >= 0 && TextureTypeList.textureSource != null) {
			@Pc(27) val size: Int = if (TextureTypeList.textureSource!!.getMetrics(this.materialId)!!.isSmall) 64 else 128
			this.texturePixels = TextureTypeList.textureSource!!.getPixels(false, this.materialId, 1.0f, size, size)
			this.textureHeight = size
			this.textureWidth = size
		}
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(B)V")
	override fun cacheReset() {
		super.cacheReset()
		this.texturePixels = null
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.materialId = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!vf", name = "d", descriptor = "(I)I")
	override fun getTextureId(): Int {
		return this.materialId
	}
}
