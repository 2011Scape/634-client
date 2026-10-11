package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import com.jagex.game.runetek5.cache.node.lru.MonoStereoData;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;
import com.jagex.game.runetek5.cache.node.CacheableNodeQueue;

@OriginalClass("client!iha")
public final class VorbisPcmStream extends PcmStream {

	@OriginalMember(owner = "client!iha", name = "Q", descriptor = "I")
	private int sampleOffset;

	@OriginalMember(owner = "client!iha", name = "R", descriptor = "Z")
	private boolean isStopping;

	@OriginalMember(owner = "client!iha", name = "T", descriptor = "Z")
	private boolean isMuted;

	@OriginalMember(owner = "client!iha", name = "t", descriptor = "Lclient!uh;")
	private final Deque nodeCollection = new Deque();

	@OriginalMember(owner = "client!iha", name = "S", descriptor = "I")
	private int rightVolume = 256;

	@OriginalMember(owner = "client!iha", name = "U", descriptor = "I")
	private int leftVolume = 256;

	@OriginalMember(owner = "client!iha", name = "V", descriptor = "I")
	private int capacityUsed = 0;

	@OriginalMember(owner = "client!iha", name = "M", descriptor = "I")
	private final int channelCount;

	@OriginalMember(owner = "client!iha", name = "<init>", descriptor = "(I)V")
	public VorbisPcmStream(@OriginalArg(0) int channelCount) {
		this.channelCount = channelCount;
	}

	@OriginalMember(owner = "client!iha", name = "c", descriptor = "(II)V")
	public void setVolume(@OriginalArg(1) int volume) {
		this.rightVolume = volume;
		this.leftVolume = volume;
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "(I)Lclient!lm;")
	private synchronized MonoStereoData getFirst() {
		return (MonoStereoData) this.nodeCollection.first();
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getNextStream() {
		return null;
	}

	@OriginalMember(owner = "client!iha", name = "d", descriptor = "(I)I")
	public synchronized int size() {
		return this.capacityUsed;
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(B)V")
	private synchronized void removeFirst() {
		@Pc(15) MonoStereoData block = this.getFirst();
		if (block != null) {
			block.unlink();
			this.capacityUsed--;
			this.sampleOffset = 0;
			VorbisPcmStream.aClass379_3.put(block.getCacheKey(), block);
		}
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "([III)V")
	@Override
	public synchronized void fill(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		if (this.isMuted) {
			return;
		}
		if (this.getFirst() != null) {
			@Pc(28) int end = offset + length;
			if (PcmPlayerManager.stereo) {
				end <<= 0x1;
			}
			@Pc(38) byte right = 0;
			if (this.channelCount == 2) {
				right = 1;
			}
			while (offset < end) {
				@Pc(52) MonoStereoData block = this.getFirst();
				if (block == null) {
					return;
				}
				@Pc(58) short[][] samples = block.samples;
				while (offset < end && this.sampleOffset < samples[0].length) {
					if (PcmPlayerManager.stereo) {
						mix[offset++] = this.rightVolume * samples[0][this.sampleOffset];
						mix[offset++] = samples[right][this.sampleOffset] * this.leftVolume;
					} else {
						@Pc(92) int slot = offset++;
						mix[slot] += samples[right][this.sampleOffset] * this.leftVolume + this.rightVolume * samples[0][this.sampleOffset];
					}
					this.sampleOffset++;
				}
				if (samples[0].length <= this.sampleOffset) {
					this.removeFirst();
				}
			}
		} else if (this.isStopping) {
			this.unlink();
			VorbisPcmStream.aClass379_3.clear();
		}
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(IZ)V")
	public synchronized void setPaused(@OriginalArg(1) boolean isMuted) {
		this.isMuted = isMuted;
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(Lclient!lm;I)V")
	public synchronized void enqueueSampleData(@OriginalArg(0) MonoStereoData node) {
		while (this.capacityUsed >= 100) {
			this.nodeCollection.removeFirst();
			this.capacityUsed--;
		}
		this.nodeCollection.addLast(node);
		this.capacityUsed++;
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getFirstStream() {
		return null;
	}

	@OriginalMember(owner = "client!iha", name = "f", descriptor = "(I)V")
	public synchronized void finish() {
		this.isStopping = true;
	}

	@OriginalMember(owner = "client!iha", name = "g", descriptor = "(I)D")
	public synchronized double getPlaybackTime() {
		if (this.capacityUsed < 1) {
			return -1.0D;
		} else {
			@Pc(21) MonoStereoData sampleData = (MonoStereoData) this.nodeCollection.first();
			return sampleData == null ? -1.0D : (double) -((float) sampleData.samples[0].length / (float) PcmPlayerManager.sampleRate) + sampleData.timestamp;
		}
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(IDI)Lclient!lm;")
	public MonoStereoData allocateSampleData(@OriginalArg(0) int sampleCount, @OriginalArg(1) double timestamp) {
		@Pc(11) long cacheKey = (long) (sampleCount | this.channelCount << 0);
		@Pc(17) MonoStereoData sampleData = (MonoStereoData) VorbisPcmStream.aClass379_3.get(cacheKey);
		if (sampleData == null) {
			sampleData = new MonoStereoData(new short[this.channelCount][sampleCount], timestamp);
		} else {
			sampleData.timestamp = timestamp;
			VorbisPcmStream.aClass379_3.remove(cacheKey);
		}
		return sampleData;
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(I)V")
	@Override
	public synchronized void skip(@OriginalArg(0) int length) {
		if (this.isMuted) {
			return;
		}
		while (true) {
			@Pc(11) MonoStereoData block = this.getFirst();
			if (block == null) {
				if (this.isStopping) {
					this.unlink();
					VorbisPcmStream.aClass379_3.clear();
				}
				return;
			}
			if (length < block.samples[0].length - this.sampleOffset) {
				this.sampleOffset += length;
				return;
			}
			length -= block.samples[0].length - this.sampleOffset;
			this.removeFirst();
		}
	}

	@OriginalMember(owner = "client!iha", name = "d", descriptor = "()I")
	@Override
	public int getActiveVoiceCount() {
		return 1;
	}
	@OriginalMember(owner = "client!kq", name = "O", descriptor = "Lclient!wu;")
	public static final CacheableNodeQueue aClass379_3 = new CacheableNodeQueue(64);
}
