package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer
import com.jagex.game.runetek5.sound.player.PcmPlayerManager

@OriginalClass("client!via")
class AreaSoundStream : PcmStream {

	@OriginalMember(owner = "client!via", name = "t", descriptor = "I")
	@JvmField
	var leftGain: Int = 0

	@OriginalMember(owner = "client!via", name = "u", descriptor = "I")
	@JvmField
	var leftGainStep: Int = 0

	@OriginalMember(owner = "client!via", name = "v", descriptor = "I")
	private var fadeSamplesRemaining: Int = 0

	@OriginalMember(owner = "client!via", name = "w", descriptor = "I")
	@JvmField
	var gainStep: Int = 0

	@OriginalMember(owner = "client!via", name = "x", descriptor = "I")
	@JvmField
	var rightGain: Int = 0

	@OriginalMember(owner = "client!via", name = "z", descriptor = "I")
	@JvmField
	var gain: Int = 0

	@OriginalMember(owner = "client!via", name = "B", descriptor = "I")
	@JvmField
	var rightGainStep: Int = 0

	@OriginalMember(owner = "client!via", name = "D", descriptor = "I")
	private var loops: Int = 0

	@OriginalMember(owner = "client!via", name = "y", descriptor = "I")
	private val start: Int

	@OriginalMember(owner = "client!via", name = "F", descriptor = "I")
	private val minBitRate: Int

	@OriginalMember(owner = "client!via", name = "E", descriptor = "Z")
	private val enableLoop: Boolean

	@OriginalMember(owner = "client!via", name = "C", descriptor = "I")
	private var rate: Int = 0

	@OriginalMember(owner = "client!via", name = "s", descriptor = "I")
	private var targetVolume: Int = 0

	@OriginalMember(owner = "client!via", name = "A", descriptor = "I")
	private var panPosition: Int = 0

	@OriginalMember(owner = "client!via", name = "G", descriptor = "I")
	@JvmField
	var position: Int = 0

	@OriginalMember(owner = "client!via", name = "<init>", descriptor = "(Lclient!ob;III)V")
	constructor(@OriginalArg(0) variableRateSoundPacket: VariableRateSoundPacket?, @OriginalArg(1) rate: Int, @OriginalArg(2) volume: Int, @OriginalArg(3) pan: Int) : super() {
		this.variableRateSoundPacket = variableRateSoundPacket
		this.start = variableRateSoundPacket!!.nominalBitRate
		this.minBitRate = variableRateSoundPacket.minBitRate
		this.enableLoop = variableRateSoundPacket.pingPong
		this.rate = rate
		this.targetVolume = volume
		this.panPosition = pan
		this.position = 0
		this.recalculateChannelVolumes()
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(III)V")
	@Synchronized
	fun fadeToVolumeAndPan(@OriginalArg(0) fadeDuration: Int, @OriginalArg(1) volume: Int, @OriginalArg(2) pan: Int) {
		var fadeDuration: Int = fadeDuration
		if (fadeDuration == 0) {
			this.update(volume, pan)
			return
		}
		@Pc(10) val left: Int = AreaSoundStream.calculateSoundAttenuation(volume, pan)
		@Pc(14) val right: Int = AreaSoundStream.calculateSoundPan(volume, pan)
		if (this.leftGain == left && this.rightGain == right) {
			this.fadeSamplesRemaining = 0
			return
		}
		@Pc(31) var maxDelta: Int = volume - this.gain
		if (this.gain - volume > maxDelta) {
			maxDelta = this.gain - volume
		}
		if (left - this.leftGain > maxDelta) {
			maxDelta = left - this.leftGain
		}
		if (this.leftGain - left > maxDelta) {
			maxDelta = this.leftGain - left
		}
		if (right - this.rightGain > maxDelta) {
			maxDelta = right - this.rightGain
		}
		if (this.rightGain - right > maxDelta) {
			maxDelta = this.rightGain - right
		}
		if (fadeDuration > maxDelta) {
			fadeDuration = maxDelta
		}
		this.fadeSamplesRemaining = fadeDuration
		this.targetVolume = volume
		this.panPosition = pan
		this.gainStep = (volume - this.gain) / fadeDuration
		this.leftGainStep = (left - this.leftGain) / fadeDuration
		this.rightGainStep = (right - this.rightGain) / fadeDuration
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "()Z")
	fun isFinished(): Boolean {
		return this.position < 0 || this.position >= ((this.variableRateSoundPacket as VariableRateSoundPacket).data!!.size shl 8)
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(Z)V")
	@Synchronized
	fun playBackward() {
		this.rate = (this.rate xor (this.rate shr 31)) + (this.rate ushr 31)
		this.rate = -this.rate
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "()I")
	@Synchronized
	fun getPanPosition(): Int {
		return if (this.panPosition < 0) -1 else this.panPosition
	}

	@OriginalMember(owner = "client!via", name = "c", descriptor = "()I")
	override fun getPriority(): Int {
		@Pc(6) var priority: Int = (this.gain * 3) shr 6
		priority = (priority xor (priority shr 31)) + (priority ushr 31)
		if (this.loops == 0) {
			priority -= priority * this.position / ((this.variableRateSoundPacket as VariableRateSoundPacket).data!!.size shl 8)
		} else if (this.loops >= 0) {
			priority -= priority * this.start / (this.variableRateSoundPacket as VariableRateSoundPacket).data!!.size
		}
		return if (priority > 255) 255 else priority
	}

	@OriginalMember(owner = "client!via", name = "g", descriptor = "()Z")
	fun isFading(): Boolean {
		return this.fadeSamplesRemaining != 0
	}

	@OriginalMember(owner = "client!via", name = "h", descriptor = "()V")
	private fun cancelFade() {
		if (this.fadeSamplesRemaining == 0) {
			return
		}
		if (this.targetVolume == Int.MIN_VALUE) {
			this.targetVolume = 0
		}
		this.fadeSamplesRemaining = 0
		this.recalculateChannelVolumes()
	}

	@OriginalMember(owner = "client!via", name = "i", descriptor = "()Z")
	private fun updateFadeState(): Boolean {
		@Pc(2) var target: Int = this.targetVolume
		@Pc(10) val targetLeft: Int
		@Pc(8) val targetRight: Int
		if (target == Int.MIN_VALUE) {
			targetRight = 0
			targetLeft = 0
			target = 0
		} else {
			targetLeft = AreaSoundStream.calculateSoundAttenuation(target, this.panPosition)
			targetRight = AreaSoundStream.calculateSoundPan(target, this.panPosition)
		}
		if (this.gain != target || this.leftGain != targetLeft || this.rightGain != targetRight) {
			if (this.gain < target) {
				this.gainStep = 1
				this.fadeSamplesRemaining = target - this.gain
			} else if (this.gain > target) {
				this.gainStep = -1
				this.fadeSamplesRemaining = this.gain - target
			} else {
				this.gainStep = 0
			}
			if (this.leftGain < targetLeft) {
				this.leftGainStep = 1
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > targetLeft - this.leftGain) {
					this.fadeSamplesRemaining = targetLeft - this.leftGain
				}
			} else if (this.leftGain > targetLeft) {
				this.leftGainStep = -1
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > this.leftGain - targetLeft) {
					this.fadeSamplesRemaining = this.leftGain - targetLeft
				}
			} else {
				this.leftGainStep = 0
			}
			if (this.rightGain < targetRight) {
				this.rightGainStep = 1
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > targetRight - this.rightGain) {
					this.fadeSamplesRemaining = targetRight - this.rightGain
				}
			} else if (this.rightGain > targetRight) {
				this.rightGainStep = -1
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > this.rightGain - targetRight) {
					this.fadeSamplesRemaining = this.rightGain - targetRight
				}
			} else {
				this.rightGainStep = 0
			}
			return false
		} else if (this.targetVolume == Int.MIN_VALUE) {
			this.targetVolume = 0
			this.rightGain = 0
			this.leftGain = 0
			this.gain = 0
			this.unlink()
			return true
		} else {
			this.recalculateChannelVolumes()
			return false
		}
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I)V")
	@Synchronized
	fun setPlaybackRate(@OriginalArg(0) playbackRate: Int) {
		if (this.rate < 0) {
			this.rate = -playbackRate
		} else {
			this.rate = playbackRate
		}
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "(I)V")
	@Synchronized
	fun fadeOutAndStop(@OriginalArg(0) fadeDuration: Int) {
		var fadeDuration: Int = fadeDuration
		if (fadeDuration == 0) {
			this.mute()
			this.unlink()
		} else if (this.leftGain == 0 && this.rightGain == 0) {
			this.fadeSamplesRemaining = 0
			this.targetVolume = 0
			this.gain = 0
			this.unlink()
		} else {
			@Pc(31) var maxGain: Int = -this.gain
			if (this.gain > maxGain) {
				maxGain = this.gain
			}
			if (-this.leftGain > maxGain) {
				maxGain = -this.leftGain
			}
			if (this.leftGain > maxGain) {
				maxGain = this.leftGain
			}
			if (-this.rightGain > maxGain) {
				maxGain = -this.rightGain
			}
			if (this.rightGain > maxGain) {
				maxGain = this.rightGain
			}
			if (fadeDuration > maxGain) {
				fadeDuration = maxGain
			}
			this.fadeSamplesRemaining = fadeDuration
			this.targetVolume = Int.MIN_VALUE
			this.gainStep = -this.gain / fadeDuration
			this.leftGainStep = -this.leftGain / fadeDuration
			this.rightGainStep = -this.rightGain / fadeDuration
		}
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "()I")
	override fun getActiveVoiceCount(): Int {
		return if (this.targetVolume == 0 && this.fadeSamplesRemaining == 0) 0 else 1
	}

	@OriginalMember(owner = "client!via", name = "j", descriptor = "()I")
	@Synchronized
	fun getTargetVolume(): Int {
		return if (this.targetVolume == Int.MIN_VALUE) 0 else this.targetVolume
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "(I)V")
	@Synchronized
	fun setPanPosition(@OriginalArg(0) pan: Int) {
		this.update(this.getTargetVolume(), pan)
	}

	@OriginalMember(owner = "client!via", name = "k", descriptor = "()I")
	@Synchronized
	fun getAbsolutePlaybackRate(): Int {
		return if (this.rate < 0) -this.rate else this.rate
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "(I)V")
	@Synchronized
	fun setVolume(@OriginalArg(0) volume: Int) {
		this.update(volume shl 6, this.getPanPosition())
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "()Lclient!jba;")
	override fun getNextStream(): PcmStream? {
		return null
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "(II)V")
	@Synchronized
	fun fadeToVolume(@OriginalArg(0) samples: Int, @OriginalArg(1) volume: Int) {
		this.fadeToVolumeAndPan(samples, volume, this.getPanPosition())
	}

	@OriginalMember(owner = "client!via", name = "g", descriptor = "(I)V")
	@Synchronized
	fun setNumLoops(@OriginalArg(0) numLoops: Int) {
		this.loops = numLoops
	}

	@OriginalMember(owner = "client!via", name = "l", descriptor = "()V")
	private fun recalculateChannelVolumes() {
		this.gain = this.targetVolume
		this.leftGain = AreaSoundStream.calculateSoundAttenuation(this.targetVolume, this.panPosition)
		this.rightGain = AreaSoundStream.calculateSoundPan(this.targetVolume, this.panPosition)
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([IIIII)I")
	private fun fillForward(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) limit: Int, @OriginalArg(3) end: Int, @OriginalArg(4) edgeSample: Int): Int {
		var offset: Int = offset
		do {
			if (this.fadeSamplesRemaining <= 0) {
				if (this.rate == 256 && (this.position and 0xFF) == 0) {
					if (PcmPlayerManager.stereo) {
						return AreaSoundSampleMixer.mixForwardStereo((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this)
					}
					return AreaSoundSampleMixer.mixForwardMono((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this)
				}
				if (PcmPlayerManager.stereo) {
					return AreaSoundSampleMixer.mixForwardStereoResampled((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this, this.rate, edgeSample)
				}
				return AreaSoundSampleMixer.mixForwardMonoResampled((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this, this.rate, edgeSample)
			}
			@Pc(5) var stop: Int = offset + this.fadeSamplesRemaining
			if (stop > end) {
				stop = end
			}
			this.fadeSamplesRemaining += offset
			if (this.rate == 256 && (this.position and 0xFF) == 0) {
				if (PcmPlayerManager.stereo) {
					offset = AreaSoundSampleMixer.mixForwardStereoRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this)
				} else {
					offset = AreaSoundSampleMixer.mixForwardMonoRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this)
				}
			} else if (PcmPlayerManager.stereo) {
				offset = AreaSoundSampleMixer.mixForwardStereoResampledRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this, this.rate, edgeSample)
			} else {
				offset = AreaSoundSampleMixer.mixForwardMonoResampledRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this, this.rate, edgeSample)
			}
			this.fadeSamplesRemaining -= offset
			if (this.fadeSamplesRemaining != 0) {
				return offset
			}
		} while (!this.updateFadeState())
		return end
	}

	@OriginalMember(owner = "client!via", name = "h", descriptor = "(I)V")
	@Synchronized
	private fun mute() {
		this.update(0, this.getPanPosition())
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([III)V")
	@Synchronized
	override fun fill(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		if (this.targetVolume == 0 && this.fadeSamplesRemaining == 0) {
			this.skip(length)
			return
		}
		@Pc(13) val packet: VariableRateSoundPacket? = this.variableRateSoundPacket as VariableRateSoundPacket?
		@Pc(18) val loopStart: Int = this.start shl 8
		@Pc(23) val loopEnd: Int = this.minBitRate shl 8
		@Pc(29) val endPosition: Int = packet!!.data!!.size shl 8
		@Pc(33) val loopLength: Int = loopEnd - loopStart
		if (loopLength <= 0) {
			this.loops = 0
		}
		@Pc(40) var cursor: Int = offset
		@Pc(44) val end: Int = length + offset
		if (this.position < 0) {
			if (this.rate <= 0) {
				this.cancelFade()
				this.unlink()
				return
			}
			this.position = 0
		}
		if (this.position >= endPosition) {
			if (this.rate >= 0) {
				this.cancelFade()
				this.unlink()
				return
			}
			this.position = endPosition - 1
		}
		if (this.loops >= 0) {
			if (this.loops > 0) {
				if (this.enableLoop) {
					label130@ do {
						if (this.rate < 0) {
							cursor = this.fillBackward(mix, offset, loopStart, end, packet.data!![this.start].toInt())
							if (this.position >= loopStart) {
								return
							}
							this.position = loopStart + loopStart - this.position - 1
							this.rate = -this.rate
							if (--this.loops == 0) {
								break@label130
							}
						}
						do {
							cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data!![this.minBitRate - 1].toInt())
							if (this.position < loopEnd) {
								return
							}
							this.position = loopEnd + loopEnd - this.position - 1
							this.rate = -this.rate
							if (--this.loops == 0) {
								break
							}
							cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data!![this.start].toInt())
							if (this.position >= loopStart) {
								return
							}
							this.position = loopStart + loopStart - this.position - 1
							this.rate = -this.rate
						} while (--this.loops != 0)
					} while (false)
				} else {
					@Pc(416) var count: Int
					if (this.rate < 0) {
						while (true) {
							cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data!![this.minBitRate - 1].toInt())
							if (this.position >= loopStart) {
								return
							}
							count = (loopEnd - this.position - 1) / loopLength
							if (count >= this.loops) {
								this.position += loopLength * this.loops
								this.loops = 0
								break
							}
							this.position += loopLength * count
							this.loops -= count
						}
					} else {
						while (true) {
							cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data!![this.start].toInt())
							if (this.position < loopEnd) {
								return
							}
							count = (this.position - loopStart) / loopLength
							if (count >= this.loops) {
								this.position -= loopLength * this.loops
								this.loops = 0
								break
							}
							this.position -= loopLength * count
							this.loops -= count
						}
					}
				}
			}
			if (this.rate < 0) {
				this.fillBackward(mix, cursor, 0, end, 0)
				if (this.position < 0) {
					this.position = -1
					this.cancelFade()
					this.unlink()
					return
				}
			} else {
				this.fillForward(mix, cursor, endPosition, end, 0)
				if (this.position >= endPosition) {
					this.position = endPosition
					this.cancelFade()
					this.unlink()
				}
			}
		} else if (this.enableLoop) {
			if (this.rate < 0) {
				cursor = this.fillBackward(mix, offset, loopStart, end, packet.data!![this.start].toInt())
				if (this.position >= loopStart) {
					return
				}
				this.position = loopStart + loopStart - this.position - 1
				this.rate = -this.rate
			}
			while (true) {
				cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data!![this.minBitRate - 1].toInt())
				if (this.position < loopEnd) {
					return
				}
				this.position = loopEnd + loopEnd - this.position - 1
				this.rate = -this.rate
				cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data!![this.start].toInt())
				if (this.position >= loopStart) {
					return
				}
				this.position = loopStart + loopStart - this.position - 1
				this.rate = -this.rate
			}
		} else if (this.rate < 0) {
			while (true) {
				cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data!![this.minBitRate - 1].toInt())
				if (this.position >= loopStart) {
					return
				}
				this.position = loopEnd - (loopEnd - 1 - this.position) % loopLength - 1
			}
		} else {
			while (true) {
				cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data!![this.start].toInt())
				if (this.position < loopEnd) {
					return
				}
				this.position = loopStart + (this.position - loopStart) % loopLength
			}
		}
	}

	@OriginalMember(owner = "client!via", name = "i", descriptor = "(I)V")
	@Synchronized
	fun setPlaybackPosition(@OriginalArg(0) position: Int) {
		var position: Int = position
		@Pc(7) val end: Int = (this.variableRateSoundPacket as VariableRateSoundPacket).data!!.size shl 8
		if (position < -1) {
			position = -1
		}
		if (position > end) {
			position = end
		}
		this.position = position
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "()Lclient!jba;")
	override fun getFirstStream(): PcmStream? {
		return null
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "(II)V")
	@Synchronized
	private fun update(@OriginalArg(0) targetVolume: Int, @OriginalArg(1) panPosition: Int) {
		this.targetVolume = targetVolume
		this.panPosition = panPosition
		this.fadeSamplesRemaining = 0
		this.recalculateChannelVolumes()
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I)V")
	@Synchronized
	override fun skip(@OriginalArg(0) length: Int) {
		var length: Int = length
		if (this.fadeSamplesRemaining > 0) {
			if (length >= this.fadeSamplesRemaining) {
				if (this.targetVolume == Int.MIN_VALUE) {
					this.targetVolume = 0
					this.rightGain = 0
					this.leftGain = 0
					this.gain = 0
					this.unlink()
					length = this.fadeSamplesRemaining
				}
				this.fadeSamplesRemaining = 0
				this.recalculateChannelVolumes()
			} else {
				this.gain += this.gainStep * length
				this.leftGain += this.leftGainStep * length
				this.rightGain += this.rightGainStep * length
				this.fadeSamplesRemaining -= length
			}
		}
		@Pc(71) val packet: VariableRateSoundPacket? = this.variableRateSoundPacket as VariableRateSoundPacket?
		@Pc(76) val loopStart: Int = this.start shl 8
		@Pc(81) val loopEnd: Int = this.minBitRate shl 8
		@Pc(87) val endPosition: Int = packet!!.data!!.size shl 8
		@Pc(91) val loopLength: Int = loopEnd - loopStart
		if (loopLength <= 0) {
			this.loops = 0
		}
		if (this.position < 0) {
			if (this.rate <= 0) {
				this.cancelFade()
				this.unlink()
				return
			}
			this.position = 0
		}
		if (this.position >= endPosition) {
			if (this.rate >= 0) {
				this.cancelFade()
				this.unlink()
				return
			}
			this.position = endPosition - 1
		}
		this.position += this.rate * length
		if (this.loops >= 0) {
			if (this.loops > 0) {
				if (this.enableLoop) {
					label125@ do {
						if (this.rate < 0) {
							if (this.position >= loopStart) {
								return
							}
							this.position = loopStart + loopStart - this.position - 1
							this.rate = -this.rate
							if (--this.loops == 0) {
								break@label125
							}
						}
						do {
							if (this.position < loopEnd) {
								return
							}
							this.position = loopEnd + loopEnd - this.position - 1
							this.rate = -this.rate
							if (--this.loops == 0) {
								break
							}
							if (this.position >= loopStart) {
								return
							}
							this.position = loopStart + loopStart - this.position - 1
							this.rate = -this.rate
						} while (--this.loops != 0)
					} while (false)
				} else {
					@Pc(361) val count: Int
					if (this.rate < 0) {
						if (this.position >= loopStart) {
							return
						}
						count = (loopEnd - this.position - 1) / loopLength
						if (count < this.loops) {
							this.position += loopLength * count
							this.loops -= count
							return
						}
						this.position += loopLength * this.loops
						this.loops = 0
					} else if (this.position >= loopEnd) {
						count = (this.position - loopStart) / loopLength
						if (count < this.loops) {
							this.position -= loopLength * count
							this.loops -= count
							return
						}
						this.position -= loopLength * this.loops
						this.loops = 0
					} else {
						return
					}
				}
			}
			if (this.rate < 0) {
				if (this.position < 0) {
					this.position = -1
					this.cancelFade()
					this.unlink()
					return
				}
			} else if (this.position >= endPosition) {
				this.position = endPosition
				this.cancelFade()
				this.unlink()
			}
		} else if (this.enableLoop) {
			if (this.rate < 0) {
				if (this.position >= loopStart) {
					return
				}
				this.position = loopStart + loopStart - this.position - 1
				this.rate = -this.rate
			}
			while (this.position >= loopEnd) {
				this.position = loopEnd + loopEnd - this.position - 1
				this.rate = -this.rate
				if (this.position >= loopStart) {
					return
				}
				this.position = loopStart + loopStart - this.position - 1
				this.rate = -this.rate
			}
		} else if (this.rate < 0) {
			if (this.position < loopStart) {
				this.position = loopEnd - (loopEnd - 1 - this.position) % loopLength - 1
			}
		} else if (this.position >= loopEnd) {
			this.position = loopStart + (this.position - loopStart) % loopLength
		}
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([IIIII)I")
	private fun fillBackward(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) limit: Int, @OriginalArg(3) end: Int, @OriginalArg(4) edgeSample: Int): Int {
		var offset: Int = offset
		do {
			if (this.fadeSamplesRemaining <= 0) {
				if (this.rate == -256 && (this.position and 0xFF) == 0) {
					if (PcmPlayerManager.stereo) {
						return AreaSoundSampleMixer.mixBackwardStereo((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this)
					}
					return AreaSoundSampleMixer.mixBackwardMono((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this)
				}
				if (PcmPlayerManager.stereo) {
					return AreaSoundSampleMixer.mixBackwardStereoResampled((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this, this.rate, edgeSample)
				}
				return AreaSoundSampleMixer.mixBackwardMonoResampled((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this, this.rate, edgeSample)
			}
			@Pc(5) var stop: Int = offset + this.fadeSamplesRemaining
			if (stop > end) {
				stop = end
			}
			this.fadeSamplesRemaining += offset
			if (this.rate == -256 && (this.position and 0xFF) == 0) {
				if (PcmPlayerManager.stereo) {
					offset = AreaSoundSampleMixer.mixBackwardStereoRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this)
				} else {
					offset = AreaSoundSampleMixer.mixBackwardMonoRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this)
				}
			} else if (PcmPlayerManager.stereo) {
				offset = AreaSoundSampleMixer.mixBackwardStereoResampledRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this, this.rate, edgeSample)
			} else {
				offset = AreaSoundSampleMixer.mixBackwardMonoResampledRamped((this.variableRateSoundPacket as VariableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this, this.rate, edgeSample)
			}
			this.fadeSamplesRemaining -= offset
			if (this.fadeSamplesRemaining != 0) {
				return offset
			}
		} while (!this.updateFadeState())
		return end
	}

	companion object {
		@OriginalMember(owner = "client!via", name = "c", descriptor = "(II)I")
		@JvmStatic
		fun calculateSoundPan(@OriginalArg(0) basePan: Int, @OriginalArg(1) distance: Int): Int {
			return if (distance < 0) -basePan else (basePan.toDouble() * Math.sqrt(distance.toDouble() * 1.220703125E-4) + 0.5).toInt()
		}

		@OriginalMember(owner = "client!via", name = "d", descriptor = "(II)I")
		@JvmStatic
		fun calculateSoundAttenuation(@OriginalArg(0) baseVolume: Int, @OriginalArg(1) distance: Int): Int {
			return if (distance < 0) baseVolume else (baseVolume.toDouble() * Math.sqrt((16384 - distance).toDouble() * 1.220703125E-4) + 0.5).toInt()
		}
	}
}
