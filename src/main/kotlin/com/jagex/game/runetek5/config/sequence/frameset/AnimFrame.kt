package com.jagex.game.runetek5.config.sequence.frameset

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!gk")
class AnimFrame {

	@OriginalMember(owner = "client!gk", name = "f", descriptor = "Z")
	@JvmField
	var modifiesColor: Boolean = false

	@OriginalMember(owner = "client!gk", name = "d", descriptor = "Z")
	@JvmField
	var modifiesBillboard: Boolean = false

	@OriginalMember(owner = "client!gk", name = "i", descriptor = "Lclient!raa;")
	@JvmField
	var base: AnimBase? = null

	@OriginalMember(owner = "client!gk", name = "n", descriptor = "Z")
	@JvmField
	var modifiesAlpha: Boolean = false

	@OriginalMember(owner = "client!gk", name = "a", descriptor = "I")
	@JvmField
	var numTransformations: Int = 0

	@OriginalMember(owner = "client!gk", name = "e", descriptor = "[S")
	@JvmField
	var baseIndices: ShortArray? = null

	@OriginalMember(owner = "client!gk", name = "o", descriptor = "[S")
	@JvmField
	var transformationX: ShortArray? = null

	@OriginalMember(owner = "client!gk", name = "l", descriptor = "[S")
	@JvmField
	var transformationY: ShortArray? = null

	@OriginalMember(owner = "client!gk", name = "j", descriptor = "[S")
	@JvmField
	var transformationZ: ShortArray? = null

	@OriginalMember(owner = "client!gk", name = "c", descriptor = "[S")
	@JvmField
	var labels: ShortArray? = null

	@OriginalMember(owner = "client!gk", name = "g", descriptor = "[B")
	@JvmField
	var tweeningProperties: ByteArray? = null

	@OriginalMember(owner = "client!gk", name = "<init>", descriptor = "([BLclient!raa;)V")
	constructor(@OriginalArg(0) data: ByteArray?, @OriginalArg(1) animBase: AnimBase?) {
		this.base = animBase
		try {
			@Pc(24) val packet: Packet = Packet(data)
			@Pc(29) val packet2: Packet = Packet(data)
			packet.readUByte()
			packet.currentPosition += 2
			@Pc(43) val count: Int = packet.readUByte()
			@Pc(45) var numTransformations: Int = 0
			@Pc(47) var last: Int = -1
			@Pc(49) var lastUsed: Int = -1
			packet2.currentPosition = packet.currentPosition + count
			@Pc(64) val local64: Int
			for (@Pc(57) i in 0 until count) {
				val transformType: Int = this.base!!.transformationType[i]
				if (transformType == 0) {
					last = i
				}
				@Pc(72) val attribute: Int = packet.readUByte()
				if (attribute > 0) {
					if (transformType == 0) {
						lastUsed = i
					}
					AnimFrame.INDICES[numTransformations] = i.toShort()
					@Pc(85) var defaultValue: Short = 0
					if (transformType == 3 || transformType == 10) {
						defaultValue = 128
					}
					if ((attribute and 0x1) == 0) {
						AnimFrame.TRANSFORM_X[numTransformations] = defaultValue
					} else {
						AnimFrame.TRANSFORM_X[numTransformations] = packet2.readSmart().toShort()
					}
					if ((attribute and 0x2) == 0) {
						AnimFrame.TRANSFORM_Y[numTransformations] = defaultValue
					} else {
						AnimFrame.TRANSFORM_Y[numTransformations] = packet2.readSmart().toShort()
					}
					if ((attribute and 0x4) == 0) {
						AnimFrame.TRANSFORM_Z[numTransformations] = defaultValue
					} else {
						AnimFrame.TRANSFORM_Z[numTransformations] = packet2.readSmart().toShort()
					}
					AnimFrame.FLAGS[numTransformations] = ((attribute ushr 3) and 0x3).toByte()
					if (transformType == 2 || transformType == 9) {
						AnimFrame.TRANSFORM_X[numTransformations] = ((AnimFrame.TRANSFORM_X[numTransformations].toInt() shl 2) and 0x3FFF).toShort()
						AnimFrame.TRANSFORM_Y[numTransformations] = ((AnimFrame.TRANSFORM_Y[numTransformations].toInt() shl 2) and 0x3FFF).toShort()
						AnimFrame.TRANSFORM_Z[numTransformations] = ((AnimFrame.TRANSFORM_Z[numTransformations].toInt() shl 2) and 0x3FFF).toShort()
					}
					AnimFrame.SKIPPED[numTransformations] = -1
					if (transformType == 1 || transformType == 2 || transformType == 3) {
						if (last > lastUsed) {
							AnimFrame.SKIPPED[numTransformations] = last.toShort()
							lastUsed = last
						}
					} else if (transformType == 5) {
						this.modifiesAlpha = true
					} else if (transformType == 7) {
						this.modifiesColor = true
					} else if (transformType == 9 || transformType == 10 || transformType == 8) {
						this.modifiesBillboard = true
					}
					numTransformations++
				}
			}
			if (packet2.currentPosition != data!!.size) {
				throw RuntimeException()
			}
			this.numTransformations = numTransformations
			this.baseIndices = ShortArray(numTransformations)
			this.transformationX = ShortArray(numTransformations)
			this.transformationY = ShortArray(numTransformations)
			this.transformationZ = ShortArray(numTransformations)
			this.labels = ShortArray(numTransformations)
			this.tweeningProperties = ByteArray(numTransformations)
			for (transformIndex in 0 until numTransformations) {
				this.baseIndices!![transformIndex] = AnimFrame.INDICES[transformIndex]
				this.transformationX!![transformIndex] = AnimFrame.TRANSFORM_X[transformIndex]
				this.transformationY!![transformIndex] = AnimFrame.TRANSFORM_Y[transformIndex]
				this.transformationZ!![transformIndex] = AnimFrame.TRANSFORM_Z[transformIndex]
				this.labels!![transformIndex] = AnimFrame.SKIPPED[transformIndex]
				this.tweeningProperties!![transformIndex] = AnimFrame.FLAGS[transformIndex]
			}
		} catch (@Pc(331) ex: Exception) {
			this.numTransformations = 0
			this.modifiesAlpha = false
			this.modifiesColor = false
		}
	}

	companion object {
		@OriginalMember(owner = "client!gk", name = "b", descriptor = "[S")
		@JvmField
		val TRANSFORM_X: ShortArray = ShortArray(500)

		@OriginalMember(owner = "client!gk", name = "h", descriptor = "[S")
		@JvmField
		val TRANSFORM_Y: ShortArray = ShortArray(500)

		@OriginalMember(owner = "client!gk", name = "k", descriptor = "[B")
		@JvmField
		val FLAGS: ByteArray = ByteArray(500)

		@OriginalMember(owner = "client!gk", name = "m", descriptor = "[S")
		@JvmField
		val SKIPPED: ShortArray = ShortArray(500)

		@OriginalMember(owner = "client!gk", name = "p", descriptor = "[S")
		@JvmField
		val INDICES: ShortArray = ShortArray(500)

		@OriginalMember(owner = "client!gk", name = "q", descriptor = "[S")
		@JvmField
		val TRANSFORM_Z: ShortArray = ShortArray(500)
	}
}
