package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.CacheableNodeQueue
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureType

@OriginalClass("client!tda")
class Js5TextureSource @OriginalMember(owner = "client!tda", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!in;)V") constructor(@OriginalArg(0) metricsArchive: Js5ResourceProvider?, @OriginalArg(1) textureArchive: Js5ResourceProvider?, @OriginalArg(2) spriteArchive: Js5ResourceProvider?) : MaterialProvider {

	@OriginalMember(owner = "client!tda", name = "n", descriptor = "Lclient!wu;")
	private val recentUse: CacheableNodeQueue = CacheableNodeQueue(256)

	@OriginalMember(owner = "client!tda", name = "k", descriptor = "Lclient!in;")
	private val textureArchive: Js5ResourceProvider?

	@OriginalMember(owner = "client!tda", name = "p", descriptor = "Lclient!in;")
	private val spriteArchive: Js5ResourceProvider?

	@OriginalMember(owner = "client!tda", name = "q", descriptor = "I")
	private val textureCount: Int

	@OriginalMember(owner = "client!tda", name = "c", descriptor = "[Lclient!hd;")
	private val metrics: Array<MaterialType?>

	init {
		this.textureArchive = textureArchive
		this.spriteArchive = spriteArchive
		@Pc(24) val packet: Packet = Packet(metricsArchive!!.getFile(0, 0))
		this.textureCount = packet.readUShort()
		this.metrics = arrayOfNulls<MaterialType>(this.textureCount)
		run {
			@Pc(36) var i: Int = 0
			while (i < this.textureCount) {
				if (packet.readUByte() == 1) {
					this.metrics[i] = MaterialType()
				}
				i++
			}
		}
		run {
			@Pc(61) var j: Int = 0
			while (j < this.textureCount) {
				if (this.metrics[j] != null) {
					this.metrics[j]!!.detailsOnly = packet.readUByte() == 0
				}
				j++
			}
		}
		run {
			@Pc(87) var k: Int = 0
			while (k < this.textureCount) {
				if (this.metrics[k] != null) {
					this.metrics[k]!!.isSmall = packet.readUByte() == 1
				}
				k++
			}
		}
		run {
			@Pc(118) var m: Int = 0
			while (m < this.textureCount) {
				if (this.metrics[m] != null) {
					this.metrics[m]!!.skipTriangles = packet.readUByte() == 1
				}
				m++
			}
		}
		run {
			@Pc(145) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.brightness = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(167) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.alpha = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(193) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.effectType = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(215) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.effectParam1 = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(237) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.colorHsl = packet.readUShort().toShort()
				}
				i++
			}
		}
		run {
			@Pc(260) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.speedU = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(286) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.speedV = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(312) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.unusedFlag = packet.readUByte() == 1
				}
				i++
			}
		}
		run {
			@Pc(345) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.transposed = packet.readUByte() == 1
				}
				i++
			}
		}
		run {
			@Pc(376) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.mipmapping = packet.readByte()
				}
				i++
			}
		}
		run {
			@Pc(398) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.repeatS = packet.readUByte() == 1
				}
				i++
			}
		}
		run {
			@Pc(427) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.repeatT = packet.readUByte() == 1
				}
				i++
			}
		}
		run {
			@Pc(456) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.hdr = packet.readUByte() == 1
				}
				i++
			}
		}
		run {
			@Pc(483) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.combineMode = packet.readUByte()
				}
				i++
			}
		}
		run {
			@Pc(505) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.effectParam2 = packet.readInt()
				}
				i++
			}
		}
		run {
			@Pc(527) var i: Int = 0
			while (i < this.textureCount) {
				if (this.metrics[i] != null) {
					this.metrics[i]!!.effectCombiner = packet.readUByte()
				}
				i++
			}
		}
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IIFIZI)[I")
	override fun getPixelsArgb(@OriginalArg(1) height: Int, @OriginalArg(2) brightness: Float, @OriginalArg(3) materialId: Int, @OriginalArg(5) width: Int): IntArray? {
		return this.getTextureType(materialId)!!.getPixelsArgb(this, width, height, this.metrics[materialId]!!.transposed, brightness.toDouble(), this.spriteArchive)
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IZ)Lclient!lr;")
	private fun getTextureType(@OriginalArg(0) materialId: Int): TextureType? {
		@Pc(10) val node2: CacheableNode? = this.recentUse.get(materialId.toLong())
		if (node2 != null) {
			return node2 as TextureType
		}
		@Pc(21) val data: ByteArray? = this.textureArchive!!.getFile(materialId)
		if (data == null) {
			return null
		} else {
			@Pc(45) val texture: TextureType = TextureType(Packet(data))
			this.recentUse.put(materialId.toLong(), texture)
			return texture
		}
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(II)Lclient!hd;")
	override fun getMetrics(@OriginalArg(0) id: Int): MaterialType? {
		return this.metrics[id]
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(ZIFIII)[I")
	override fun getPixels(@OriginalArg(0) flipX: Boolean, @OriginalArg(1) materialId: Int, @OriginalArg(2) brightness: Float, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int): IntArray? {
		return this.getTextureType(materialId)!!.getPixelsRgb(width, this, this.metrics[materialId]!!.transposed, brightness.toDouble(), flipX, this.spriteArchive, height)
	}

	@OriginalMember(owner = "client!tda", name = "b", descriptor = "(II)Z")
	override fun textureAvailable(@OriginalArg(1) id: Int): Boolean {
		@Pc(8) val texture: TextureType? = this.getTextureType(id)
		return texture != null && texture.isReady(this, this.spriteArchive)
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(IFZIII)[F")
	override fun floatArgbOutput(@OriginalArg(0) height: Int, @OriginalArg(1) brightness: Float, @OriginalArg(4) width: Int, @OriginalArg(5) materialId: Int): FloatArray? {
		return this.getTextureType(materialId)!!.getPixelsHdr(height, this, this.metrics[materialId]!!.transposed, width, this.spriteArchive)
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "(Z)I")
	override fun textureCount(): Int {
		return this.textureCount
	}

	companion object {
		@OriginalMember(owner = "client!hl", name = "K", descriptor = "Lclient!d;")
		@JvmField
		var anInterface4_10: MaterialProvider? = null
	}
}
