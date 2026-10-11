package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;

@OriginalClass("client!via")
public final class AreaSoundStream extends PcmStream {

	@OriginalMember(owner = "client!via", name = "t", descriptor = "I")
	public int leftGain;

	@OriginalMember(owner = "client!via", name = "u", descriptor = "I")
	public int leftGainStep;

	@OriginalMember(owner = "client!via", name = "v", descriptor = "I")
	private int fadeSamplesRemaining;

	@OriginalMember(owner = "client!via", name = "w", descriptor = "I")
	public int gainStep;

	@OriginalMember(owner = "client!via", name = "x", descriptor = "I")
	public int rightGain;

	@OriginalMember(owner = "client!via", name = "z", descriptor = "I")
	public int gain;

	@OriginalMember(owner = "client!via", name = "B", descriptor = "I")
	public int rightGainStep;

	@OriginalMember(owner = "client!via", name = "D", descriptor = "I")
	private int loops;

	@OriginalMember(owner = "client!via", name = "y", descriptor = "I")
	private final int start;

	@OriginalMember(owner = "client!via", name = "F", descriptor = "I")
	private final int minBitRate;

	@OriginalMember(owner = "client!via", name = "E", descriptor = "Z")
	private final boolean enableLoop;

	@OriginalMember(owner = "client!via", name = "C", descriptor = "I")
	private int rate;

	@OriginalMember(owner = "client!via", name = "s", descriptor = "I")
	private int targetVolume;

	@OriginalMember(owner = "client!via", name = "A", descriptor = "I")
	private int panPosition;

	@OriginalMember(owner = "client!via", name = "G", descriptor = "I")
	public int position;

	@OriginalMember(owner = "client!via", name = "<init>", descriptor = "(Lclient!ob;III)V")
	public AreaSoundStream(@OriginalArg(0) VariableRateSoundPacket variableRateSoundPacket, @OriginalArg(1) int rate, @OriginalArg(2) int volume, @OriginalArg(3) int pan) {
		super.variableRateSoundPacket = variableRateSoundPacket;
		this.start = variableRateSoundPacket.nominalBitRate;
		this.minBitRate = variableRateSoundPacket.minBitRate;
		this.enableLoop = variableRateSoundPacket.pingPong;
		this.rate = rate;
		this.targetVolume = volume;
		this.panPosition = pan;
		this.position = 0;
		this.recalculateChannelVolumes();
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(III)V")
	public synchronized void fadeToVolumeAndPan(@OriginalArg(0) int fadeDuration, @OriginalArg(1) int volume, @OriginalArg(2) int pan) {
		if (fadeDuration == 0) {
			this.update(volume, pan);
			return;
		}
		@Pc(10) int left = AreaSoundStream.calculateSoundAttenuation(volume, pan);
		@Pc(14) int right = AreaSoundStream.calculateSoundPan(volume, pan);
		if (this.leftGain == left && this.rightGain == right) {
			this.fadeSamplesRemaining = 0;
			return;
		}
		@Pc(31) int maxDelta = volume - this.gain;
		if (this.gain - volume > maxDelta) {
			maxDelta = this.gain - volume;
		}
		if (left - this.leftGain > maxDelta) {
			maxDelta = left - this.leftGain;
		}
		if (this.leftGain - left > maxDelta) {
			maxDelta = this.leftGain - left;
		}
		if (right - this.rightGain > maxDelta) {
			maxDelta = right - this.rightGain;
		}
		if (this.rightGain - right > maxDelta) {
			maxDelta = this.rightGain - right;
		}
		if (fadeDuration > maxDelta) {
			fadeDuration = maxDelta;
		}
		this.fadeSamplesRemaining = fadeDuration;
		this.targetVolume = volume;
		this.panPosition = pan;
		this.gainStep = (volume - this.gain) / fadeDuration;
		this.leftGainStep = (left - this.leftGain) / fadeDuration;
		this.rightGainStep = (right - this.rightGain) / fadeDuration;
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "()Z")
	public boolean isFinished() {
		return this.position < 0 || this.position >= ((VariableRateSoundPacket) super.variableRateSoundPacket).data.length << 8;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(Z)V")
	public synchronized void playBackward() {
		this.rate = (this.rate ^ this.rate >> 31) + (this.rate >>> 31);
		this.rate = -this.rate;
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "()I")
	public synchronized int getPanPosition() {
		return this.panPosition < 0 ? -1 : this.panPosition;
	}

	@OriginalMember(owner = "client!via", name = "c", descriptor = "()I")
	@Override
	public int getPriority() {
		@Pc(6) int priority = this.gain * 3 >> 6;
		priority = (priority ^ priority >> 31) + (priority >>> 31);
		if (this.loops == 0) {
			priority -= priority * this.position / (((VariableRateSoundPacket) super.variableRateSoundPacket).data.length << 8);
		} else if (this.loops >= 0) {
			priority -= priority * this.start / ((VariableRateSoundPacket) super.variableRateSoundPacket).data.length;
		}
		return priority > 255 ? 255 : priority;
	}

	@OriginalMember(owner = "client!via", name = "g", descriptor = "()Z")
	public boolean isFading() {
		return this.fadeSamplesRemaining != 0;
	}

	@OriginalMember(owner = "client!via", name = "h", descriptor = "()V")
	private void cancelFade() {
		if (this.fadeSamplesRemaining == 0) {
			return;
		}
		if (this.targetVolume == Integer.MIN_VALUE) {
			this.targetVolume = 0;
		}
		this.fadeSamplesRemaining = 0;
		this.recalculateChannelVolumes();
	}

	@OriginalMember(owner = "client!via", name = "i", descriptor = "()Z")
	private boolean updateFadeState() {
		@Pc(2) int target = this.targetVolume;
		@Pc(10) int targetLeft;
		@Pc(8) int targetRight;
		if (target == Integer.MIN_VALUE) {
			targetRight = 0;
			targetLeft = 0;
			target = 0;
		} else {
			targetLeft = AreaSoundStream.calculateSoundAttenuation(target, this.panPosition);
			targetRight = AreaSoundStream.calculateSoundPan(target, this.panPosition);
		}
		if (this.gain != target || this.leftGain != targetLeft || this.rightGain != targetRight) {
			if (this.gain < target) {
				this.gainStep = 1;
				this.fadeSamplesRemaining = target - this.gain;
			} else if (this.gain > target) {
				this.gainStep = -1;
				this.fadeSamplesRemaining = this.gain - target;
			} else {
				this.gainStep = 0;
			}
			if (this.leftGain < targetLeft) {
				this.leftGainStep = 1;
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > targetLeft - this.leftGain) {
					this.fadeSamplesRemaining = targetLeft - this.leftGain;
				}
			} else if (this.leftGain > targetLeft) {
				this.leftGainStep = -1;
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > this.leftGain - targetLeft) {
					this.fadeSamplesRemaining = this.leftGain - targetLeft;
				}
			} else {
				this.leftGainStep = 0;
			}
			if (this.rightGain < targetRight) {
				this.rightGainStep = 1;
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > targetRight - this.rightGain) {
					this.fadeSamplesRemaining = targetRight - this.rightGain;
				}
			} else if (this.rightGain > targetRight) {
				this.rightGainStep = -1;
				if (this.fadeSamplesRemaining == 0 || this.fadeSamplesRemaining > this.rightGain - targetRight) {
					this.fadeSamplesRemaining = this.rightGain - targetRight;
				}
			} else {
				this.rightGainStep = 0;
			}
			return false;
		} else if (this.targetVolume == Integer.MIN_VALUE) {
			this.targetVolume = 0;
			this.gain = this.leftGain = this.rightGain = 0;
			this.unlink();
			return true;
		} else {
			this.recalculateChannelVolumes();
			return false;
		}
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I)V")
	public synchronized void setPlaybackRate(@OriginalArg(0) int playbackRate) {
		if (this.rate < 0) {
			this.rate = -playbackRate;
		} else {
			this.rate = playbackRate;
		}
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "(I)V")
	public synchronized void fadeOutAndStop(@OriginalArg(0) int fadeDuration) {
		if (fadeDuration == 0) {
			this.mute();
			this.unlink();
		} else if (this.leftGain == 0 && this.rightGain == 0) {
			this.fadeSamplesRemaining = 0;
			this.targetVolume = 0;
			this.gain = 0;
			this.unlink();
		} else {
			@Pc(31) int maxGain = -this.gain;
			if (this.gain > maxGain) {
				maxGain = this.gain;
			}
			if (-this.leftGain > maxGain) {
				maxGain = -this.leftGain;
			}
			if (this.leftGain > maxGain) {
				maxGain = this.leftGain;
			}
			if (-this.rightGain > maxGain) {
				maxGain = -this.rightGain;
			}
			if (this.rightGain > maxGain) {
				maxGain = this.rightGain;
			}
			if (fadeDuration > maxGain) {
				fadeDuration = maxGain;
			}
			this.fadeSamplesRemaining = fadeDuration;
			this.targetVolume = Integer.MIN_VALUE;
			this.gainStep = -this.gain / fadeDuration;
			this.leftGainStep = -this.leftGain / fadeDuration;
			this.rightGainStep = -this.rightGain / fadeDuration;
		}
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "()I")
	@Override
	public int getActiveVoiceCount() {
		return this.targetVolume == 0 && this.fadeSamplesRemaining == 0 ? 0 : 1;
	}

	@OriginalMember(owner = "client!via", name = "j", descriptor = "()I")
	public synchronized int getTargetVolume() {
		return this.targetVolume == Integer.MIN_VALUE ? 0 : this.targetVolume;
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "(I)V")
	public synchronized void setPanPosition(@OriginalArg(0) int pan) {
		this.update(this.getTargetVolume(), pan);
	}

	@OriginalMember(owner = "client!via", name = "k", descriptor = "()I")
	public synchronized int getAbsolutePlaybackRate() {
		return this.rate < 0 ? -this.rate : this.rate;
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "(I)V")
	public synchronized void setVolume(@OriginalArg(0) int volume) {
		this.update(volume << 6, this.getPanPosition());
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getNextStream() {
		return null;
	}

	@OriginalMember(owner = "client!via", name = "e", descriptor = "(II)V")
	public synchronized void fadeToVolume(@OriginalArg(0) int samples, @OriginalArg(1) int volume) {
		this.fadeToVolumeAndPan(samples, volume, this.getPanPosition());
	}

	@OriginalMember(owner = "client!via", name = "g", descriptor = "(I)V")
	public synchronized void setNumLoops(@OriginalArg(0) int numLoops) {
		this.loops = numLoops;
	}

	@OriginalMember(owner = "client!via", name = "l", descriptor = "()V")
	private void recalculateChannelVolumes() {
		this.gain = this.targetVolume;
		this.leftGain = AreaSoundStream.calculateSoundAttenuation(this.targetVolume, this.panPosition);
		this.rightGain = AreaSoundStream.calculateSoundPan(this.targetVolume, this.panPosition);
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([IIIII)I")
	private int fillForward(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int limit, @OriginalArg(3) int end, @OriginalArg(4) int edgeSample) {
		do {
			if (this.fadeSamplesRemaining <= 0) {
				if (this.rate == 256 && (this.position & 0xFF) == 0) {
					if (PcmPlayerManager.stereo) {
						return AreaSoundSampleMixer.mixForwardStereo(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this);
					}
					return AreaSoundSampleMixer.mixForwardMono(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this);
				}
				if (PcmPlayerManager.stereo) {
					return AreaSoundSampleMixer.mixForwardStereoResampled(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this, this.rate, edgeSample);
				}
				return AreaSoundSampleMixer.mixForwardMonoResampled(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this, this.rate, edgeSample);
			}
			@Pc(5) int stop = offset + this.fadeSamplesRemaining;
			if (stop > end) {
				stop = end;
			}
			this.fadeSamplesRemaining += offset;
			if (this.rate == 256 && (this.position & 0xFF) == 0) {
				if (PcmPlayerManager.stereo) {
					offset = AreaSoundSampleMixer.mixForwardStereoRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this);
				} else {
					offset = AreaSoundSampleMixer.mixForwardMonoRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this);
				}
			} else if (PcmPlayerManager.stereo) {
				offset = AreaSoundSampleMixer.mixForwardStereoResampledRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this, this.rate, edgeSample);
			} else {
				offset = AreaSoundSampleMixer.mixForwardMonoResampledRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this, this.rate, edgeSample);
			}
			this.fadeSamplesRemaining -= offset;
			if (this.fadeSamplesRemaining != 0) {
				return offset;
			}
		} while (!this.updateFadeState());
		return end;
	}

	@OriginalMember(owner = "client!via", name = "h", descriptor = "(I)V")
	private synchronized void mute() {
		this.update(0, this.getPanPosition());
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([III)V")
	@Override
	public synchronized void fill(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		if (this.targetVolume == 0 && this.fadeSamplesRemaining == 0) {
			this.skip(length);
			return;
		}
		@Pc(13) VariableRateSoundPacket packet = (VariableRateSoundPacket) super.variableRateSoundPacket;
		@Pc(18) int loopStart = this.start << 8;
		@Pc(23) int loopEnd = this.minBitRate << 8;
		@Pc(29) int endPosition = packet.data.length << 8;
		@Pc(33) int loopLength = loopEnd - loopStart;
		if (loopLength <= 0) {
			this.loops = 0;
		}
		@Pc(40) int cursor = offset;
		@Pc(44) int end = length + offset;
		if (this.position < 0) {
			if (this.rate <= 0) {
				this.cancelFade();
				this.unlink();
				return;
			}
			this.position = 0;
		}
		if (this.position >= endPosition) {
			if (this.rate >= 0) {
				this.cancelFade();
				this.unlink();
				return;
			}
			this.position = endPosition - 1;
		}
		if (this.loops >= 0) {
			if (this.loops > 0) {
				if (this.enableLoop) {
					label130: {
						if (this.rate < 0) {
							cursor = this.fillBackward(mix, offset, loopStart, end, packet.data[this.start]);
							if (this.position >= loopStart) {
								return;
							}
							this.position = loopStart + loopStart - this.position - 1;
							this.rate = -this.rate;
							if (--this.loops == 0) {
								break label130;
							}
						}
						do {
							cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data[this.minBitRate - 1]);
							if (this.position < loopEnd) {
								return;
							}
							this.position = loopEnd + loopEnd - this.position - 1;
							this.rate = -this.rate;
							if (--this.loops == 0) {
								break;
							}
							cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data[this.start]);
							if (this.position >= loopStart) {
								return;
							}
							this.position = loopStart + loopStart - this.position - 1;
							this.rate = -this.rate;
						} while (--this.loops != 0);
					}
				} else {
					@Pc(416) int count;
					if (this.rate < 0) {
						while (true) {
							cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data[this.minBitRate - 1]);
							if (this.position >= loopStart) {
								return;
							}
							count = (loopEnd - this.position - 1) / loopLength;
							if (count >= this.loops) {
								this.position += loopLength * this.loops;
								this.loops = 0;
								break;
							}
							this.position += loopLength * count;
							this.loops -= count;
						}
					} else {
						while (true) {
							cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data[this.start]);
							if (this.position < loopEnd) {
								return;
							}
							count = (this.position - loopStart) / loopLength;
							if (count >= this.loops) {
								this.position -= loopLength * this.loops;
								this.loops = 0;
								break;
							}
							this.position -= loopLength * count;
							this.loops -= count;
						}
					}
				}
			}
			if (this.rate < 0) {
				this.fillBackward(mix, cursor, 0, end, 0);
				if (this.position < 0) {
					this.position = -1;
					this.cancelFade();
					this.unlink();
					return;
				}
			} else {
				this.fillForward(mix, cursor, endPosition, end, 0);
				if (this.position >= endPosition) {
					this.position = endPosition;
					this.cancelFade();
					this.unlink();
				}
			}
		} else if (this.enableLoop) {
			if (this.rate < 0) {
				cursor = this.fillBackward(mix, offset, loopStart, end, packet.data[this.start]);
				if (this.position >= loopStart) {
					return;
				}
				this.position = loopStart + loopStart - this.position - 1;
				this.rate = -this.rate;
			}
			while (true) {
				cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data[this.minBitRate - 1]);
				if (this.position < loopEnd) {
					return;
				}
				this.position = loopEnd + loopEnd - this.position - 1;
				this.rate = -this.rate;
				cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data[this.start]);
				if (this.position >= loopStart) {
					return;
				}
				this.position = loopStart + loopStart - this.position - 1;
				this.rate = -this.rate;
			}
		} else if (this.rate < 0) {
			while (true) {
				cursor = this.fillBackward(mix, cursor, loopStart, end, packet.data[this.minBitRate - 1]);
				if (this.position >= loopStart) {
					return;
				}
				this.position = loopEnd - (loopEnd - 1 - this.position) % loopLength - 1;
			}
		} else {
			while (true) {
				cursor = this.fillForward(mix, cursor, loopEnd, end, packet.data[this.start]);
				if (this.position < loopEnd) {
					return;
				}
				this.position = loopStart + (this.position - loopStart) % loopLength;
			}
		}
	}

	@OriginalMember(owner = "client!via", name = "i", descriptor = "(I)V")
	public synchronized void setPlaybackPosition(@OriginalArg(0) int position) {
		@Pc(7) int end = ((VariableRateSoundPacket) super.variableRateSoundPacket).data.length << 8;
		if (position < -1) {
			position = -1;
		}
		if (position > end) {
			position = end;
		}
		this.position = position;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getFirstStream() {
		return null;
	}

	@OriginalMember(owner = "client!via", name = "f", descriptor = "(II)V")
	private synchronized void update(@OriginalArg(0) int targetVolume, @OriginalArg(1) int panPosition) {
		this.targetVolume = targetVolume;
		this.panPosition = panPosition;
		this.fadeSamplesRemaining = 0;
		this.recalculateChannelVolumes();
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I)V")
	@Override
	public synchronized void skip(@OriginalArg(0) int length) {
		if (this.fadeSamplesRemaining > 0) {
			if (length >= this.fadeSamplesRemaining) {
				if (this.targetVolume == Integer.MIN_VALUE) {
					this.targetVolume = 0;
					this.gain = this.leftGain = this.rightGain = 0;
					this.unlink();
					length = this.fadeSamplesRemaining;
				}
				this.fadeSamplesRemaining = 0;
				this.recalculateChannelVolumes();
			} else {
				this.gain += this.gainStep * length;
				this.leftGain += this.leftGainStep * length;
				this.rightGain += this.rightGainStep * length;
				this.fadeSamplesRemaining -= length;
			}
		}
		@Pc(71) VariableRateSoundPacket packet = (VariableRateSoundPacket) super.variableRateSoundPacket;
		@Pc(76) int loopStart = this.start << 8;
		@Pc(81) int loopEnd = this.minBitRate << 8;
		@Pc(87) int endPosition = packet.data.length << 8;
		@Pc(91) int loopLength = loopEnd - loopStart;
		if (loopLength <= 0) {
			this.loops = 0;
		}
		if (this.position < 0) {
			if (this.rate <= 0) {
				this.cancelFade();
				this.unlink();
				return;
			}
			this.position = 0;
		}
		if (this.position >= endPosition) {
			if (this.rate >= 0) {
				this.cancelFade();
				this.unlink();
				return;
			}
			this.position = endPosition - 1;
		}
		this.position += this.rate * length;
		if (this.loops >= 0) {
			if (this.loops > 0) {
				if (this.enableLoop) {
					label125: {
						if (this.rate < 0) {
							if (this.position >= loopStart) {
								return;
							}
							this.position = loopStart + loopStart - this.position - 1;
							this.rate = -this.rate;
							if (--this.loops == 0) {
								break label125;
							}
						}
						do {
							if (this.position < loopEnd) {
								return;
							}
							this.position = loopEnd + loopEnd - this.position - 1;
							this.rate = -this.rate;
							if (--this.loops == 0) {
								break;
							}
							if (this.position >= loopStart) {
								return;
							}
							this.position = loopStart + loopStart - this.position - 1;
							this.rate = -this.rate;
						} while (--this.loops != 0);
					}
				} else {
					@Pc(361) int count;
					if (this.rate < 0) {
						if (this.position >= loopStart) {
							return;
						}
						count = (loopEnd - this.position - 1) / loopLength;
						if (count < this.loops) {
							this.position += loopLength * count;
							this.loops -= count;
							return;
						}
						this.position += loopLength * this.loops;
						this.loops = 0;
					} else if (this.position >= loopEnd) {
						count = (this.position - loopStart) / loopLength;
						if (count < this.loops) {
							this.position -= loopLength * count;
							this.loops -= count;
							return;
						}
						this.position -= loopLength * this.loops;
						this.loops = 0;
					} else {
						return;
					}
				}
			}
			if (this.rate < 0) {
				if (this.position < 0) {
					this.position = -1;
					this.cancelFade();
					this.unlink();
					return;
				}
			} else if (this.position >= endPosition) {
				this.position = endPosition;
				this.cancelFade();
				this.unlink();
			}
		} else if (this.enableLoop) {
			if (this.rate < 0) {
				if (this.position >= loopStart) {
					return;
				}
				this.position = loopStart + loopStart - this.position - 1;
				this.rate = -this.rate;
			}
			while (this.position >= loopEnd) {
				this.position = loopEnd + loopEnd - this.position - 1;
				this.rate = -this.rate;
				if (this.position >= loopStart) {
					return;
				}
				this.position = loopStart + loopStart - this.position - 1;
				this.rate = -this.rate;
			}
		} else if (this.rate < 0) {
			if (this.position < loopStart) {
				this.position = loopEnd - (loopEnd - 1 - this.position) % loopLength - 1;
			}
		} else if (this.position >= loopEnd) {
			this.position = loopStart + (this.position - loopStart) % loopLength;
		}
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([IIIII)I")
	private int fillBackward(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int limit, @OriginalArg(3) int end, @OriginalArg(4) int edgeSample) {
		do {
			if (this.fadeSamplesRemaining <= 0) {
				if (this.rate == -256 && (this.position & 0xFF) == 0) {
					if (PcmPlayerManager.stereo) {
						return AreaSoundSampleMixer.mixBackwardStereo(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this);
					}
					return AreaSoundSampleMixer.mixBackwardMono(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this);
				}
				if (PcmPlayerManager.stereo) {
					return AreaSoundSampleMixer.mixBackwardStereoResampled(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, end, limit, this, this.rate, edgeSample);
				}
				return AreaSoundSampleMixer.mixBackwardMonoResampled(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, end, limit, this, this.rate, edgeSample);
			}
			@Pc(5) int stop = offset + this.fadeSamplesRemaining;
			if (stop > end) {
				stop = end;
			}
			this.fadeSamplesRemaining += offset;
			if (this.rate == -256 && (this.position & 0xFF) == 0) {
				if (PcmPlayerManager.stereo) {
					offset = AreaSoundSampleMixer.mixBackwardStereoRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this);
				} else {
					offset = AreaSoundSampleMixer.mixBackwardMonoRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this);
				}
			} else if (PcmPlayerManager.stereo) {
				offset = AreaSoundSampleMixer.mixBackwardStereoResampledRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.leftGain, this.rightGain, this.leftGainStep, this.rightGainStep, stop, limit, this, this.rate, edgeSample);
			} else {
				offset = AreaSoundSampleMixer.mixBackwardMonoResampledRamped(((VariableRateSoundPacket) super.variableRateSoundPacket).data, mix, this.position, offset, this.gain, this.gainStep, stop, limit, this, this.rate, edgeSample);
			}
			this.fadeSamplesRemaining -= offset;
			if (this.fadeSamplesRemaining != 0) {
				return offset;
			}
		} while (!this.updateFadeState());
		return end;
	}

	@OriginalMember(owner = "client!via", name = "c", descriptor = "(II)I")
	public static int calculateSoundPan(@OriginalArg(0) int basePan, @OriginalArg(1) int distance) {
		return distance < 0 ? -basePan : (int) ((double) basePan * Math.sqrt((double) distance * 1.220703125E-4D) + 0.5D);
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "(II)I")
	public static int calculateSoundAttenuation(@OriginalArg(0) int baseVolume, @OriginalArg(1) int distance) {
		return distance < 0 ? baseVolume : (int) ((double) baseVolume * Math.sqrt((double) (16384 - distance) * 1.220703125E-4D) + 0.5D);
	}
}
