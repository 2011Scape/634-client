package com.jagex.game.runetek5.config.particle

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!qb")
class ParticleProducerType {

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "I")
	@JvmField
	var colorFadeStart: Int = 0

	@OriginalMember(owner = "client!qb", name = "e", descriptor = "I")
	@JvmField
	var speedStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "h", descriptor = "[I")
	@JvmField
	var effectiveVertexUids: IntArray? = null

	@OriginalMember(owner = "client!qb", name = "i", descriptor = "I")
	@JvmField
	var alphaFadeStart: Int = 0

	@OriginalMember(owner = "client!qb", name = "k", descriptor = "I")
	@JvmField
	var startSpeedChange: Int = 0

	@OriginalMember(owner = "client!qb", name = "m", descriptor = "[I")
	@JvmField
	var copiedParticleFileIds: IntArray? = null

	@OriginalMember(owner = "client!qb", name = "n", descriptor = "I")
	@JvmField
	var faceColorRgb: Int = 0

	@OriginalMember(owner = "client!qb", name = "p", descriptor = "I")
	@JvmField
	var startSizeChange: Int = 0

	@OriginalMember(owner = "client!qb", name = "q", descriptor = "I")
	@JvmField
	var maximumParticleRate: Int = 0

	@OriginalMember(owner = "client!qb", name = "r", descriptor = "I")
	@JvmField
	var minimumStartG: Int = 0

	@OriginalMember(owner = "client!qb", name = "s", descriptor = "I")
	@JvmField
	var minimumParticleRate: Int = 0

	@OriginalMember(owner = "client!qb", name = "t", descriptor = "I")
	@JvmField
	var minimumSpeed: Int = 0

	@OriginalMember(owner = "client!qb", name = "v", descriptor = "I")
	@JvmField
	var minimumLifetime: Int = 0

	@OriginalMember(owner = "client!qb", name = "w", descriptor = "[I")
	@JvmField
	var localEffectors: IntArray? = null

	@OriginalMember(owner = "client!qb", name = "A", descriptor = "I")
	@JvmField
	var minimumStartB: Int = 0

	@OriginalMember(owner = "client!qb", name = "E", descriptor = "[I")
	@JvmField
	var generalEffectors: IntArray? = null

	@OriginalMember(owner = "client!qb", name = "F", descriptor = "I")
	@JvmField
	var greenVariance: Int = 0

	@OriginalMember(owner = "client!qb", name = "H", descriptor = "I")
	@JvmField
	var blueFadeStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "J", descriptor = "I")
	@JvmField
	var maximumLifetime: Int = 0

	@OriginalMember(owner = "client!qb", name = "K", descriptor = "I")
	@JvmField
	var blueVariance: Int = 0

	@OriginalMember(owner = "client!qb", name = "M", descriptor = "I")
	@JvmField
	var alphaVariance: Int = 0

	@OriginalMember(owner = "client!qb", name = "O", descriptor = "I")
	private var maximumStartB: Int = 0

	@OriginalMember(owner = "client!qb", name = "P", descriptor = "I")
	@JvmField
	var minimumStartA: Int = 0

	@OriginalMember(owner = "client!qb", name = "R", descriptor = "S")
	@JvmField
	var maximalAngleH: Short = 0

	@OriginalMember(owner = "client!qb", name = "S", descriptor = "I")
	@JvmField
	var alphaFadeStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "T", descriptor = "I")
	@JvmField
	var maximumSize: Int = 0

	@OriginalMember(owner = "client!qb", name = "U", descriptor = "S")
	@JvmField
	var minimumAngleV: Short = 0

	@OriginalMember(owner = "client!qb", name = "V", descriptor = "I")
	private var maximumStartG: Int = 0

	@OriginalMember(owner = "client!qb", name = "W", descriptor = "I")
	private var maximumStartR: Int = 0

	@OriginalMember(owner = "client!qb", name = "X", descriptor = "I")
	@JvmField
	var greenFadeStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "cb", descriptor = "I")
	@JvmField
	var redFadeStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "eb", descriptor = "I")
	private var minimumStartColorRgb: Int = 0

	@OriginalMember(owner = "client!qb", name = "fb", descriptor = "I")
	@JvmField
	var minimumStartR: Int = 0

	@OriginalMember(owner = "client!qb", name = "hb", descriptor = "S")
	@JvmField
	var minimalAngleH: Short = 0

	@OriginalMember(owner = "client!qb", name = "jb", descriptor = "I")
	@JvmField
	var redVariance: Int = 0

	@OriginalMember(owner = "client!qb", name = "kb", descriptor = "I")
	private var maximumStartColorRgb: Int = 0

	@OriginalMember(owner = "client!qb", name = "nb", descriptor = "I")
	@JvmField
	var speedFallOffStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "ob", descriptor = "I")
	@JvmField
	var maximumSpeed: Int = 0

	@OriginalMember(owner = "client!qb", name = "pb", descriptor = "I")
	@JvmField
	var sizeChangeStep: Int = 0

	@OriginalMember(owner = "client!qb", name = "rb", descriptor = "S")
	@JvmField
	var maximalAngleV: Short = 0

	@OriginalMember(owner = "client!qb", name = "sb", descriptor = "I")
	@JvmField
	var minimumSize: Int = 0

	@OriginalMember(owner = "client!qb", name = "ub", descriptor = "I")
	private var maximumStartA: Int = 0

	@OriginalMember(owner = "client!qb", name = "g", descriptor = "I")
	@JvmField
	var minimumSetting: Int = 0

	@OriginalMember(owner = "client!qb", name = "j", descriptor = "Z")
	@JvmField
	var isTextured: Boolean = false

	@OriginalMember(owner = "client!qb", name = "o", descriptor = "I")
	@JvmField
	var nonTexturedProducerId: Int = -1

	@OriginalMember(owner = "client!qb", name = "G", descriptor = "I")
	@JvmField
	var endSpeed: Int = -1

	@OriginalMember(owner = "client!qb", name = "N", descriptor = "Z")
	@JvmField
	var periodic: Boolean = true

	@OriginalMember(owner = "client!qb", name = "l", descriptor = "I")
	@JvmField
	var lowestDisplayPlane: Int = -2

	@OriginalMember(owner = "client!qb", name = "y", descriptor = "Z")
	@JvmField
	var adjustsToScene: Boolean = false

	@OriginalMember(owner = "client!qb", name = "D", descriptor = "I")
	private var speedChange: Int = 100

	@OriginalMember(owner = "client!qb", name = "d", descriptor = "Z")
	@JvmField
	var killAboveSurface: Boolean = true

	@OriginalMember(owner = "client!qb", name = "u", descriptor = "I")
	@JvmField
	var emissionEndTime: Int = -1

	@OriginalMember(owner = "client!qb", name = "Z", descriptor = "I")
	@JvmField
	var highestDisplayPlane: Int = -2

	@OriginalMember(owner = "client!qb", name = "c", descriptor = "I")
	private var colorFading: Int = 100

	@OriginalMember(owner = "client!qb", name = "db", descriptor = "I")
	@JvmField
	var endSize: Int = -1

	@OriginalMember(owner = "client!qb", name = "bb", descriptor = "I")
	@JvmField
	var lifetime: Int = -1

	@OriginalMember(owner = "client!qb", name = "gb", descriptor = "Z")
	@JvmField
	var adjustsLightIntensity: Boolean = true

	@OriginalMember(owner = "client!qb", name = "Y", descriptor = "I")
	@JvmField
	var updatesPerCycle: Int = 0

	@OriginalMember(owner = "client!qb", name = "ab", descriptor = "Z")
	@JvmField
	var killOverlapping: Boolean = false

	@OriginalMember(owner = "client!qb", name = "Q", descriptor = "I")
	@JvmField
	var materialId: Int = -1

	@OriginalMember(owner = "client!qb", name = "ib", descriptor = "I")
	private var sizeChange: Int = 100

	@OriginalMember(owner = "client!qb", name = "qb", descriptor = "I")
	private var alphaFading: Int = 100

	@OriginalMember(owner = "client!qb", name = "mb", descriptor = "I")
	@JvmField
	var speedUpdateType: Int = 0

	@OriginalMember(owner = "client!qb", name = "lb", descriptor = "Z")
	@JvmField
	var unusedOpcode26: Boolean = true

	@OriginalMember(owner = "client!qb", name = "I", descriptor = "Z")
	@JvmField
	var activeFirst: Boolean = true

	@OriginalMember(owner = "client!qb", name = "tb", descriptor = "Z")
	@JvmField
	var uniformColorVariance: Boolean = true

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.minimalAngleH = packet.readUShort().toShort()
			this.maximalAngleH = packet.readUShort().toShort()
			this.minimumAngleV = packet.readUShort().toShort()
			this.maximalAngleV = packet.readUShort().toShort()
			this.minimalAngleH = (this.minimalAngleH.toInt() shl 3).toShort()
			this.minimumAngleV = (this.minimumAngleV.toInt() shl 3).toShort()
			this.maximalAngleV = (this.maximalAngleV.toInt() shl 3).toShort()
			this.maximalAngleH = (this.maximalAngleH.toInt() shl 3).toShort()
		} else if (opcode == 2) {
			packet.readUByte()
		} else if (opcode == 3) {
			this.minimumSpeed = packet.readInt()
			this.maximumSpeed = packet.readInt()
		} else if (opcode == 4) {
			this.speedUpdateType = packet.readUByte()
			this.speedFallOffStep = packet.readByte().toInt()
		} else if (opcode == 5) {
			this.maximumSize = packet.readUShort() shl 12 shl 2
			this.minimumSize = this.maximumSize
		} else if (opcode == 6) {
			this.minimumStartColorRgb = packet.readInt()
			this.maximumStartColorRgb = packet.readInt()
		} else if (opcode == 7) {
			this.minimumLifetime = packet.readUShort()
			this.maximumLifetime = packet.readUShort()
		} else if (opcode == 8) {
			this.minimumParticleRate = packet.readUShort()
			this.maximumParticleRate = packet.readUShort()
		} else {
			@Pc(223) var count: Int
			@Pc(229) var index: Int
			if (opcode == 9) {
				count = packet.readUByte()
				this.generalEffectors = IntArray(count)
				index = 0
				while (index < count) {
					this.generalEffectors!![index] = packet.readUShort()
					index++
				}
			} else if (opcode == 10) {
				count = packet.readUByte()
				this.localEffectors = IntArray(count)
				index = 0
				while (index < count) {
					this.localEffectors!![index] = packet.readUShort()
					index++
				}
			} else if (opcode == 12) {
				this.lowestDisplayPlane = packet.readByte().toInt()
			} else if (opcode == 13) {
				this.highestDisplayPlane = packet.readByte().toInt()
			} else if (opcode == 14) {
				this.updatesPerCycle = packet.readUShort()
			} else if (opcode == 15) {
				this.materialId = packet.readUShort()
			} else if (opcode == 16) {
				this.activeFirst = packet.readUByte() == 1
				this.emissionEndTime = packet.readUShort()
				this.lifetime = packet.readUShort()
				this.periodic = packet.readUByte() == 1
			} else if (opcode == 17) {
				this.nonTexturedProducerId = packet.readUShort()
			} else if (opcode == 18) {
				this.faceColorRgb = packet.readInt()
			} else if (opcode == 19) {
				this.minimumSetting = packet.readUByte()
			} else if (opcode == 20) {
				this.colorFading = packet.readUByte()
			} else if (opcode == 21) {
				this.alphaFading = packet.readUByte()
			} else if (opcode == 22) {
				this.endSpeed = packet.readInt()
			} else if (opcode == 23) {
				this.speedChange = packet.readUByte()
			} else if (opcode == 24) {
				this.uniformColorVariance = false
			} else if (opcode == 25) {
				count = packet.readUByte()
				this.effectiveVertexUids = IntArray(count)
				index = 0
				while (index < count) {
					this.effectiveVertexUids!![index] = packet.readUShort()
					index++
				}
			} else if (opcode == 26) {
				this.unusedOpcode26 = false
			} else if (opcode == 27) {
				this.endSize = packet.readUShort() shl 12 shl 2
			} else if (opcode == 28) {
				this.sizeChange = packet.readUByte()
			} else if (opcode == 29) {
				packet.readShort()
			} else if (opcode == 30) {
				this.isTextured = true
			} else if (opcode == 31) {
				this.minimumSize = packet.readUShort() shl 12 shl 2
				this.maximumSize = packet.readUShort() shl 12 shl 2
			} else if (opcode == 32) {
				this.adjustsLightIntensity = false
			} else if (opcode == 33) {
				this.killOverlapping = true
			} else if (opcode == 34) {
				this.killAboveSurface = false
				return
			}
		}
	}

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet) {
		while (true) {
			@Pc(9) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(I)V")
	fun postDecode() {
		this.minimumStartR = (this.minimumStartColorRgb shr 16) and 0xFF
		this.maximumStartR = (this.maximumStartColorRgb shr 16) and 0xFF
		if (this.lowestDisplayPlane > -2 || this.highestDisplayPlane > -2) {
			this.adjustsToScene = true
		}
		this.maximumStartG = (this.maximumStartColorRgb shr 8) and 0xFF
		this.minimumStartG = (this.minimumStartColorRgb shr 8) and 0xFF
		this.redVariance = this.maximumStartR - this.minimumStartR
		this.maximumStartB = this.maximumStartColorRgb and 0xFF
		this.minimumStartB = this.minimumStartColorRgb and 0xFF
		this.greenVariance = this.maximumStartG - this.minimumStartG
		this.minimumStartA = (this.minimumStartColorRgb shr 24) and 0xFF
		this.blueVariance = this.maximumStartB - this.minimumStartB
		this.maximumStartA = (this.maximumStartColorRgb shr 24) and 0xFF
		this.alphaVariance = this.maximumStartA - this.minimumStartA
		if (this.endSize != -1) {
			this.startSizeChange = this.maximumLifetime * this.sizeChange / 100
			if (this.startSizeChange == 0) {
				this.startSizeChange = 1
			}
			this.sizeChangeStep = (this.endSize - (this.maximumSize - this.minimumSize) / 2 - this.minimumSize) / this.startSizeChange
		}
		if (this.faceColorRgb != 0) {
			this.alphaFadeStart = this.maximumLifetime * this.alphaFading / 100
			this.colorFadeStart = this.colorFading * this.maximumLifetime / 100
			if (this.colorFadeStart == 0) {
				this.colorFadeStart = 1
			}
			this.redFadeStep = ((((this.faceColorRgb shr 16) and 0xFF) - this.minimumStartR - this.redVariance / 2) shl 8) / this.colorFadeStart
			this.blueFadeStep = (((this.faceColorRgb and 0xFF) - this.minimumStartB - this.blueVariance / 2) shl 8) / this.colorFadeStart
			if (this.alphaFadeStart == 0) {
				this.alphaFadeStart = 1
			}
			this.greenFadeStep = ((((this.faceColorRgb shr 8) and 0xFF) - this.greenVariance / 2 - this.minimumStartG) shl 8) / this.colorFadeStart
			this.redFadeStep += if (this.redFadeStep > 0) -4 else 4
			this.blueFadeStep += if (this.blueFadeStep <= 0) 4 else -4
			this.alphaFadeStep = ((((this.faceColorRgb shr 24) and 0xFF) - this.alphaVariance / 2 - this.minimumStartA) shl 8) / this.alphaFadeStart
			this.greenFadeStep += if (this.greenFadeStep > 0) -4 else 4
			this.alphaFadeStep += if (this.alphaFadeStep <= 0) 4 else -4
		}
		if (this.endSpeed == -1) {
			return
		}
		this.startSpeedChange = this.speedChange * this.maximumLifetime / 100
		if (this.startSpeedChange == 0) {
			this.startSpeedChange = 1
		}
		this.speedStep = (this.endSpeed - this.minimumSpeed - (this.maximumSpeed - this.minimumSpeed) / 2) / this.startSpeedChange
	}

	companion object {
		@OriginalMember(owner = "client!gl", name = "j", descriptor = "Lclient!jr;")
		@JvmField
		val particleProducers: LRUCache = LRUCache(64)

		@OriginalMember(owner = "client!g", name = "a", descriptor = "(II)Lclient!qb;")
		@JvmStatic
		fun get(@OriginalArg(0) id: Int): ParticleProducerType {
			@Pc(12) var particleEmitterType: ParticleProducerType? = ParticleProducerType.particleProducers.get(id.toLong()) as ParticleProducerType?
			if (particleEmitterType != null) {
				return particleEmitterType
			}
			@Pc(22) val data: ByteArray? = ParticleProducerType.aClass157_19!!.getFile(0, id)
			particleEmitterType = ParticleProducerType()
			if (data != null) {
				particleEmitterType.decode(Packet(data))
			}
			particleEmitterType.postDecode()
			ParticleProducerType.particleProducers.put(particleEmitterType, id.toLong())
			return particleEmitterType
		}

		@OriginalMember(owner = "client!ap", name = "a", descriptor = "(Z)V")
		@JvmStatic
		fun clearCache() {
			ParticleProducerType.particleProducers.reset()
		}

		@OriginalMember(owner = "client!av", name = "s", descriptor = "Lclient!in;")
		@JvmField
		var aClass157_19: Js5ResourceProvider? = null

		@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Lclient!in;B)V")
		@JvmStatic
		fun setParticleResourceProvider(@OriginalArg(0) particleResource: Js5ResourceProvider?) {
			ParticleProducerType.aClass157_19 = particleResource
		}
	}
}
