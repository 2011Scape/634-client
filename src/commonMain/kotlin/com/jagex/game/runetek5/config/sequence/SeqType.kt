package com.jagex.game.runetek5.config.sequence

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.model.Model
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import kotlin.jvm.JvmField

@OriginalClass("client!hg")
class SeqType {

	@OriginalMember(owner = "client!hg", name = "b", descriptor = "[I")
	@JvmField
	var soundMaxDelay: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "c", descriptor = "[I")
	@JvmField
	var frames: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "q", descriptor = "Lclient!lfa;")
	@JvmField
	var seqtypelist: SeqTypeList? = null

	@OriginalMember(owner = "client!hg", name = "s", descriptor = "[I")
	@JvmField
	var frameSoundVolume: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "z", descriptor = "[I")
	private var interfaceFrames: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "C", descriptor = "[Z")
	@JvmField
	var interLeaveOrder: BooleanArray? = null

	@OriginalMember(owner = "client!hg", name = "D", descriptor = "[[I")
	@JvmField
	var frameSounds: Array<IntArray?>? = null

	@OriginalMember(owner = "client!hg", name = "E", descriptor = "[I")
	@JvmField
	var soundMinDelay: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "G", descriptor = "[I")
	@JvmField
	var durations: IntArray? = null

	@OriginalMember(owner = "client!hg", name = "I", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!hg", name = "h", descriptor = "Z")
	@JvmField
	var lights: Boolean = false

	@OriginalMember(owner = "client!hg", name = "d", descriptor = "I")
	@JvmField
	var replayInterval: Int = -1

	@OriginalMember(owner = "client!hg", name = "g", descriptor = "Z")
	@JvmField
	var tweened: Boolean = false

	@OriginalMember(owner = "client!hg", name = "k", descriptor = "I")
	@JvmField
	var moveType: Int = -1

	@OriginalMember(owner = "client!hg", name = "e", descriptor = "I")
	@JvmField
	var priority: Int = 5

	@OriginalMember(owner = "client!hg", name = "t", descriptor = "Z")
	@JvmField
	var shadowTweened: Boolean = false

	@OriginalMember(owner = "client!hg", name = "B", descriptor = "I")
	@JvmField
	var interruptType: Int = -1

	@OriginalMember(owner = "client!hg", name = "o", descriptor = "I")
	@JvmField
	var replacementShield: Int = -1

	@OriginalMember(owner = "client!hg", name = "n", descriptor = "I")
	@JvmField
	var loopType: Int = 2

	@OriginalMember(owner = "client!hg", name = "A", descriptor = "I")
	@JvmField
	var replacementWeapon: Int = -1

	@OriginalMember(owner = "client!hg", name = "j", descriptor = "I")
	@JvmField
	var replayCount: Int = 99

	@OriginalMember(owner = "client!hg", name = "H", descriptor = "Z")
	@JvmField
	var vorbis: Boolean = false

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(IIIZ)I")
	fun getFunctionMask(@OriginalArg(0) nextFrameId: Int, @OriginalArg(2) currentFrameId: Int, @OriginalArg(3) includeInterfaceFrames: Boolean): Int {
		@Pc(5) var functionMask: Int = 0
		@Pc(7) var nextFrame: Int = 0
		@Pc(12) val frame: Int = this.frames!![currentFrameId]
		@Pc(14) var animFrameset: FrameSet? = null
		@Pc(22) val animFrameset2: FrameSet? = this.seqtypelist!!.listFrameSet(frame shr 16)
		@Pc(26) val frameIndex: Int = frame and 0xFFFF
		if (animFrameset2 == null) {
			return 0
		}
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.frames!!.size) {
			nextFrame = this.frames!![nextFrameId]
			animFrameset = this.seqtypelist!!.listFrameSet(nextFrame shr 16)
			nextFrame = nextFrame and 0xFFFF
		}
		if (this.lights) {
			functionMask = 512
		}
		if (animFrameset2.modifiesColors(frameIndex)) {
			functionMask = functionMask or 0x80
		}
		if (animFrameset2.modifiesAlpha(frameIndex)) {
			functionMask = functionMask or 0x100
		}
		if (animFrameset2.modifiesBillboards(frameIndex)) {
			functionMask = functionMask or 0x400
		}
		if (animFrameset != null) {
			if (animFrameset.modifiesColors(nextFrame)) {
				functionMask = functionMask or 0x80
			}
			if (animFrameset.modifiesAlpha(nextFrame)) {
				functionMask = functionMask or 0x100
			}
			if (animFrameset.modifiesBillboards(nextFrame)) {
				functionMask = functionMask or 0x400
			}
		}
		if (this.interfaceFrames != null && includeInterfaceFrames) {
			@Pc(148) var interfaceFrame: Int
			@Pc(161) var animFrameset3: FrameSet?
			if (currentFrameId < this.interfaceFrames!!.size) {
				interfaceFrame = this.interfaceFrames!![currentFrameId]
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist!!.listFrameSet(interfaceFrame shr 16)
					interfaceFrame = interfaceFrame and 0xFFFF
					if (animFrameset3 != null) {
						if (animFrameset3.modifiesColors(interfaceFrame)) {
							functionMask = functionMask or 0x80
						}
						if (animFrameset3.modifiesAlpha(interfaceFrame)) {
							functionMask = functionMask or 0x100
						}
						if (animFrameset3.modifiesBillboards(interfaceFrame)) {
							functionMask = functionMask or 0x400
						}
					}
				}
			}
			if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.interfaceFrames!!.size) {
				interfaceFrame = this.interfaceFrames!![nextFrameId]
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist!!.listFrameSet(interfaceFrame shr 16)
					interfaceFrame = interfaceFrame and 0xFFFF
					if (animFrameset3 != null) {
						if (animFrameset3.modifiesColors(interfaceFrame)) {
							functionMask = functionMask or 0x80
						}
						if (animFrameset3.modifiesAlpha(interfaceFrame)) {
							functionMask = functionMask or 0x100
						}
						if (animFrameset3.modifiesBillboards(interfaceFrame)) {
							functionMask = functionMask or 0x400
						}
					}
				}
			}
		}
		return functionMask or 0x20
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		@Pc(14) val local14: Int
		@Pc(20) var i: Int
		@Pc(39) val local39: Int
		@Pc(54) var j: Int
		if (opcode == 1) {
			val frameCount: Int = packet!!.readUShort()
			this.durations = IntArray(frameCount)
			i = 0
			while (i < frameCount) {
				this.durations!![i] = packet.readUShort()
				i++
			}
			this.frames = IntArray(frameCount)
			for (frameIndex in 0 until frameCount) {
				this.frames!![frameIndex] = packet.readUShort()
			}
			j = 0
			while (j < frameCount) {
				this.frames!![j] += packet.readUShort() shl 16
				j++
			}
		} else if (opcode == 2) {
			this.replayInterval = packet!!.readUShort()
		} else if (opcode == 3) {
			this.interLeaveOrder = BooleanArray(256)
			val orderCount: Int = packet!!.readUByte()
			i = 0
			while (i < orderCount) {
				this.interLeaveOrder!![packet.readUByte()] = true
				i++
			}
		} else if (opcode == 5) {
			this.priority = packet!!.readUByte()
		} else if (opcode == 6) {
			this.replacementShield = packet!!.readUShort()
		} else if (opcode == 7) {
			this.replacementWeapon = packet!!.readUShort()
		} else if (opcode == 8) {
			this.replayCount = packet!!.readUByte()
		} else if (opcode == 9) {
			this.interruptType = packet!!.readUByte()
		} else if (opcode == 10) {
			this.moveType = packet!!.readUByte()
		} else if (opcode == 11) {
			this.loopType = packet!!.readUByte()
		} else if (opcode == 12) {
			val interfaceFrameCount: Int = packet!!.readUByte()
			this.interfaceFrames = IntArray(interfaceFrameCount)
			i = 0
			while (i < interfaceFrameCount) {
				this.interfaceFrames!![i] = packet.readUShort()
				i++
			}
			for (frameIndex in 0 until interfaceFrameCount) {
				this.interfaceFrames!![frameIndex] = (packet.readUShort() shl 16) + this.interfaceFrames!![frameIndex]
			}
		} else if (opcode == 13) {
			val frameSoundCount: Int = packet!!.readUShort()
			this.frameSounds = arrayOfNulls<IntArray>(frameSoundCount)
			i = 0
			while (i < frameSoundCount) {
				val soundCount: Int = packet.readUByte()
				if (soundCount > 0) {
					this.frameSounds!![i] = IntArray(soundCount)
					this.frameSounds!![i]!![0] = packet.readUMedium()
					j = 1
					while (j < soundCount) {
						this.frameSounds!![i]!![j] = packet.readUShort()
						j++
					}
				}
				i++
			}
		} else if (opcode == 14) {
			this.lights = true
		} else if (opcode == 15) {
			this.tweened = true
		} else if (opcode == 16) {
			this.shadowTweened = true
		} else if (opcode == 18) {
			this.vorbis = true
		} else if (opcode == 19) {
			if (this.frameSoundVolume == null) {
				this.frameSoundVolume = IntArray(this.frameSounds!!.size)
				for (frameIndex in 0 until this.frameSounds!!.size) {
					this.frameSoundVolume!![frameIndex] = 255
				}
			}
			this.frameSoundVolume!![packet!!.readUByte()] = packet.readUByte()
		} else if (opcode == 20) {
			if (this.soundMaxDelay == null || this.soundMinDelay == null) {
				this.soundMaxDelay = IntArray(this.frameSounds!!.size)
				this.soundMinDelay = IntArray(this.frameSounds!!.size)
				for (frameIndex in 0 until this.frameSounds!!.size) {
					this.soundMaxDelay!![frameIndex] = 256
					this.soundMinDelay!![frameIndex] = 256
				}
			}
			val frameIndex: Int = packet!!.readUByte()
			this.soundMaxDelay!![frameIndex] = packet.readUShort()
			this.soundMinDelay!![frameIndex] = packet.readUShort()
		}
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!ka;IIBIIII)Lclient!ka;")
	fun animateRotatedModel(@OriginalArg(0) model: Model?, @OriginalArg(1) rotation: Int, @OriginalArg(2) nextFrameId: Int, @OriginalArg(3) copyType: Byte, @OriginalArg(4) currentFrameId: Int, @OriginalArg(5) frameCycle: Int, @OriginalArg(7) functionMask: Int): Model? {
		var nextFrameId: Int = nextFrameId
		var functionMask: Int = functionMask
		@Pc(8) val frameDuration: Int = this.durations!![currentFrameId]
		@Pc(13) val frame: Int = this.frames!![currentFrameId]
		@Pc(21) val animFrameset: FrameSet? = this.seqtypelist!!.listFrameSet(frame shr 16)
		@Pc(25) val frameIndex: Int = frame and 0xFFFF
		if (animFrameset == null) {
			return model!!.copy(copyType, functionMask, true)
		}
		@Pc(35) var animFrameset2: FrameSet? = null
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && nextFrameId < this.frames!!.size) {
			@Pc(57) val nextFrame: Int = this.frames!![nextFrameId]
			animFrameset2 = this.seqtypelist!!.listFrameSet(nextFrame shr 16)
			nextFrameId = nextFrame and 0xFFFF
		}
		if (this.lights) {
			functionMask = functionMask or 0x200
		}
		if (animFrameset.modifiesColors(frameIndex)) {
			functionMask = functionMask or 0x80
		}
		if (animFrameset.modifiesAlpha(frameIndex)) {
			functionMask = functionMask or 0x100
		}
		if (animFrameset.modifiesBillboards(frameIndex)) {
			functionMask = functionMask or 0x400
		}
		if (animFrameset2 != null) {
			if (animFrameset2.modifiesColors(nextFrameId)) {
				functionMask = functionMask or 0x80
			}
			if (animFrameset2.modifiesAlpha(nextFrameId)) {
				functionMask = functionMask or 0x100
			}
			if (animFrameset2.modifiesBillboards(nextFrameId)) {
				functionMask = functionMask or 0x400
			}
		}
		functionMask = functionMask or 0x20
		@Pc(152) val model2: Model? = model!!.copy(copyType, functionMask, true)
		model2!!.animateModelFrame(frameIndex, frameDuration, animFrameset2, rotation, animFrameset, this.lights, nextFrameId, frameCycle - 1)
		return model2
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(Lclient!cea;Z)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(9) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(ILclient!ka;IIII)Lclient!ka;")
	fun animateModel(@OriginalArg(1) model: Model?, @OriginalArg(2) nextFrameId: Int, @OriginalArg(3) frameCycle: Int, @OriginalArg(4) functionMask: Int, @OriginalArg(5) currentFrameId: Int): Model? {
		var nextFrameId: Int = nextFrameId
		var functionMask: Int = functionMask
		@Pc(8) val frameDuration: Int = this.durations!![currentFrameId]
		@Pc(13) val frame: Int = this.frames!![currentFrameId]
		@Pc(21) val animFrameset: FrameSet? = this.seqtypelist!!.listFrameSet(frame shr 16)
		@Pc(25) val frameIndex: Int = frame and 0xFFFF
		if (animFrameset == null) {
			return model!!.copy(1.toByte(), functionMask, true)
		}
		@Pc(35) var animFrameset2: FrameSet? = null
		if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && this.frames!!.size > nextFrameId) {
			@Pc(53) val nextFrame: Int = this.frames!![nextFrameId]
			animFrameset2 = this.seqtypelist!!.listFrameSet(nextFrame shr 16)
			nextFrameId = nextFrame and 0xFFFF
		}
		@Pc(67) var animFrameset3: FrameSet? = null
		@Pc(69) var animFrameset4: FrameSet? = null
		@Pc(71) var interfaceFrame: Int = 0
		@Pc(73) var nextInterfaceFrame: Int = 0
		if (this.interfaceFrames != null) {
			if (this.interfaceFrames!!.size > currentFrameId) {
				interfaceFrame = this.interfaceFrames!![currentFrameId]
				if (interfaceFrame != 65535) {
					animFrameset3 = this.seqtypelist!!.listFrameSet(interfaceFrame shr 16)
					interfaceFrame = interfaceFrame and 0xFFFF
				}
			}
			if ((this.tweened || SeqType.forceTweening) && nextFrameId != -1 && this.interfaceFrames!!.size > nextFrameId) {
				nextInterfaceFrame = this.interfaceFrames!![nextFrameId]
				if (nextInterfaceFrame != 65535) {
					animFrameset4 = this.seqtypelist!!.listFrameSet(nextInterfaceFrame shr 16)
					nextInterfaceFrame = nextInterfaceFrame and 0xFFFF
				}
			}
		}
		if (this.lights) {
			functionMask = functionMask or 0x200
		}
		if (animFrameset.modifiesColors(frameIndex)) {
			functionMask = functionMask or 0x80
		}
		if (animFrameset.modifiesAlpha(frameIndex)) {
			functionMask = functionMask or 0x100
		}
		if (animFrameset.modifiesBillboards(frameIndex)) {
			functionMask = functionMask or 0x400
		}
		if (animFrameset3 != null) {
			if (animFrameset3.modifiesColors(interfaceFrame)) {
				functionMask = functionMask or 0x80
			}
			if (animFrameset3.modifiesAlpha(interfaceFrame)) {
				functionMask = functionMask or 0x100
			}
			if (animFrameset3.modifiesBillboards(interfaceFrame)) {
				functionMask = functionMask or 0x400
			}
		}
		if (animFrameset2 != null) {
			if (animFrameset2.modifiesColors(nextFrameId)) {
				functionMask = functionMask or 0x80
			}
			if (animFrameset2.modifiesAlpha(nextFrameId)) {
				functionMask = functionMask or 0x100
			}
			if (animFrameset2.modifiesBillboards(nextFrameId)) {
				functionMask = functionMask or 0x400
			}
		}
		if (animFrameset4 != null) {
			if (animFrameset4.modifiesColors(nextInterfaceFrame)) {
				functionMask = functionMask or 0x80
			}
			if (animFrameset4.modifiesAlpha(nextInterfaceFrame)) {
				functionMask = functionMask or 0x100
			}
			if (animFrameset4.modifiesBillboards(nextInterfaceFrame)) {
				functionMask = functionMask or 0x400
			}
		}
		functionMask = functionMask or 0x20
		@Pc(272) val model2: Model? = model!!.copy(1.toByte(), functionMask, true)
		model2!!.animateModelFrame(frameIndex, frameDuration, animFrameset2, 0, animFrameset, this.lights, nextFrameId, frameCycle - 1)
		if (animFrameset3 != null) {
			model2.animateModelFrame(interfaceFrame, frameDuration, animFrameset4, 0, animFrameset3, this.lights, nextInterfaceFrame, frameCycle - 1)
		}
		return model2
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(B)V")
	fun postDecode() {
		if (this.interruptType == -1) {
			if (this.interLeaveOrder == null) {
				this.interruptType = 0
			} else {
				this.interruptType = 2
			}
		}
		if (this.moveType != -1) {
			return
		}
		if (this.interLeaveOrder == null) {
			this.moveType = 0
		} else {
			this.moveType = 2
		}
	}

	companion object {
		@OriginalMember(owner = "client!hu", name = "g", descriptor = "Z")
		@JvmField
		var forceTweening: Boolean = false
	}
}
