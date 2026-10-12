package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import kotlin.jvm.JvmField

@OriginalClass("client!rp")
class TextureOpDiagonalWaves @OriginalMember(owner = "client!rp", name = "<init>", descriptor = "()V") constructor() : TextureOperation(0, true) {

	@OriginalMember(owner = "client!rp", name = "I", descriptor = "I")
	private var pattern2OffsetX: Int = 0

	@OriginalMember(owner = "client!rp", name = "L", descriptor = "I")
	private var pattern1OffsetX: Int = 2048

	@OriginalMember(owner = "client!rp", name = "S", descriptor = "I")
	private var waveAmplitude: Int = 4096

	@OriginalMember(owner = "client!rp", name = "U", descriptor = "I")
	private var pattern1OffsetY: Int = 0

	@OriginalMember(owner = "client!rp", name = "V", descriptor = "I")
	private var waveFrequency: Int = 12288

	@OriginalMember(owner = "client!rp", name = "T", descriptor = "I")
	private var waveDecay: Int = 8192

	@OriginalMember(owner = "client!rp", name = "X", descriptor = "I")
	private var pattern2OffsetY: Int = 2048

	@OriginalMember(owner = "client!rp", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(30) val y: Int = TextureRaster.verticalTable!![row] - 2048
			@Pc(32) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(40) val x: Int = EnvironmentLight.horizontalTable!![i] - 2048
				@Pc(46) val offsetX1: Int = x + this.pattern1OffsetX
				@Pc(57) val wrappedX1: Int = if (offsetX1 < -2048) offsetX1 + 4096 else offsetX1
				@Pc(66) val x1: Int = if (wrappedX1 <= 2048) wrappedX1 else wrappedX1 - 4096
				@Pc(71) val offsetY1: Int = this.pattern1OffsetY + y
				@Pc(80) val wrappedY1: Int = if (offsetY1 < -2048) offsetY1 + 4096 else offsetY1
				@Pc(91) val y1: Int = if (wrappedY1 <= 2048) wrappedY1 else wrappedY1 - 4096
				@Pc(97) val offsetX2: Int = x + this.pattern2OffsetX
				@Pc(106) val wrappedX2: Int = if (offsetX2 < -2048) offsetX2 + 4096 else offsetX2
				@Pc(115) val x2: Int = if (wrappedX2 > 2048) wrappedX2 - 4096 else wrappedX2
				@Pc(120) val offsetY2: Int = this.pattern2OffsetY + y
				@Pc(129) val wrappedY2: Int = if (offsetY2 < -2048) offsetY2 + 4096 else offsetY2
				@Pc(138) val y2: Int = if (wrappedY2 > 2048) wrappedY2 - 4096 else wrappedY2
				output[i] = if (this.testDiagonalWave(y1, x1) || this.testAntiDiagonalWave(y2, x2)) 4096 else 0
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!rp", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		TextureRaster.calculateTrigTables()
	}

	@OriginalMember(owner = "client!rp", name = "d", descriptor = "(III)Z")
	private fun testAntiDiagonalWave(@OriginalArg(0) y: Int, @OriginalArg(2) x: Int): Boolean {
		@Pc(12) val phase: Int = ((x + y) * this.waveFrequency) shr 12
		@Pc(22) val wave: Int = TextureRaster.PALETTE_COS!![((phase * 255) shr 12) and 0xFF]
		@Pc(35) val normalized: Int = (wave shl 12) / this.waveFrequency
		@Pc(42) val decayed: Int = (normalized shl 12) / this.waveDecay
		@Pc(49) val envelope: Int = (decayed * this.waveAmplitude) shr 12
		return envelope > y - x && -envelope < y - x
	}

	@OriginalMember(owner = "client!rp", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.pattern1OffsetX = packet!!.readUShort()
		} else if (opcode == 1) {
			this.pattern1OffsetY = packet!!.readUShort()
		} else if (opcode == 2) {
			this.pattern2OffsetX = packet!!.readUShort()
		} else if (opcode == 3) {
			this.pattern2OffsetY = packet!!.readUShort()
		} else if (opcode == 4) {
			this.waveFrequency = packet!!.readUShort()
		} else if (opcode == 5) {
			this.waveAmplitude = packet!!.readUShort()
		} else if (opcode == 6) {
			this.waveDecay = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!rp", name = "e", descriptor = "(III)Z")
	private fun testDiagonalWave(@OriginalArg(0) y: Int, @OriginalArg(2) x: Int): Boolean {
		@Pc(17) val phase: Int = (this.waveFrequency * (y - x)) shr 12
		@Pc(27) val wave: Int = TextureRaster.PALETTE_COS!![((phase * 255) shr 12) and 0xFF]
		@Pc(34) val normalized: Int = (wave shl 12) / this.waveFrequency
		@Pc(41) val decayed: Int = (normalized shl 12) / this.waveDecay
		@Pc(48) val envelope: Int = (this.waveAmplitude * decayed) shr 12
		return x + y < envelope && y + x > -envelope
	}

	companion object {
		@OriginalMember(owner = "client!rp", name = "Z", descriptor = "[J")
		@JvmField
		val CRC64_TABLE: LongArray = LongArray(256)

		init {
			@Pc(15) var i: Int = 0
			while (i < 256) {
				@Pc(19) var crc: Long = i.toLong()
				@Pc(21) var bit: Int = 0
				while (bit < 8) {
					if ((crc and 0x1L) == 1L) {
						crc = (crc ushr 1) xor -0x3693a86a2878f0beL
					} else {
						crc = crc ushr 0x1
					}
					bit++
				}
				CRC64_TABLE[i] = crc
				i++
			}
		}
	}
}
