package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.material.texture.TextureTypeList
import com.jagex.game.runetek5.config.material.texture.TextureType
import kotlin.jvm.JvmField

@OriginalClass("client!pj")
open class TextureOpSprite : TextureOperation {

	@OriginalMember(owner = "client!pj", name = "I", descriptor = "[I")
	@JvmField
	var spritePixels: IntArray? = null

	@OriginalMember(owner = "client!pj", name = "O", descriptor = "I")
	@JvmField
	var spriteWidth: Int = 0

	@OriginalMember(owner = "client!pj", name = "S", descriptor = "I")
	@JvmField
	var spriteHeight: Int = 0

	@OriginalMember(owner = "client!pj", name = "U", descriptor = "I")
	private var spriteId: Int = -1

	@OriginalMember(owner = "client!pj", name = "<init>", descriptor = "()V")
	constructor() : super(0, false)

	@OriginalMember(owner = "client!pj", name = "b", descriptor = "(Z)Z")
	fun loadSprite(): Boolean {
		if (this.spritePixels != null) {
			return true
		} else if (this.spriteId >= 0) {
			@Pc(35) val image: SpriteType? = if (TextureType.SPRITE_GROUP_ID >= 0) SpriteDataProvider.listSingle(TextureTypeList.textureSpriteResource, TextureType.SPRITE_GROUP_ID, this.spriteId) else SpriteDataProvider.listSingle(TextureTypeList.textureSpriteResource, this.spriteId)
			image!!.expandToFullSize()
			this.spritePixels = image.getPixels()
			this.spriteWidth = image.width
			this.spriteHeight = image.height
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(Lclient!cea;II)V")
	final override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.spriteId = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(I)I")
	final override fun getSpriteId(): Int {
		return this.spriteId
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(B)V")
	final override fun cacheReset() {
		super.cacheReset()
		this.spritePixels = null
	}

	@OriginalMember(owner = "client!pj", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(19) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty && this.loadSprite()) {
			@Pc(31) val outputRed: IntArray = output[0]!!
			@Pc(35) val outputGreen: IntArray = output[1]!!
			@Pc(39) val outputBlue: IntArray = output[2]!!
			@Pc(59) var pixelOffset: Int = this.spriteWidth * (if (TextureOperation.currentHeight == this.spriteHeight) count else this.spriteHeight * count / TextureOperation.currentHeight)
			@Pc(65) var x: Int
			@Pc(73) var local73: Int
			if (EnvironmentLight.textureOpWidth == this.spriteWidth) {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					val pixel: Int = this.spritePixels!![pixelOffset++]
					outputBlue[x] = (pixel and 0xFF) shl 4
					outputGreen[x] = (pixel shr 4) and 0xFF0
					outputRed[x] = (pixel shr 12) and 0xFF0
					x++
				}
			} else {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					val sourceX: Int = this.spriteWidth * x / EnvironmentLight.textureOpWidth
					@Pc(125) val pixel: Int = this.spritePixels!![sourceX + pixelOffset]
					outputBlue[x] = (pixel and 0xFF) shl 4
					outputGreen[x] = (pixel shr 4) and 0xFF0
					outputRed[x] = (pixel shr 12) and 0xFF0
					x++
				}
			}
		}
		return output
	}
}
