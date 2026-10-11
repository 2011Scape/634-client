package com.jagex.game.runetek5.sound.player;

import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import com.jagex.game.runetek5.cache.node.impl.AudioNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!cba")
public class PcmPlayer {

	@OriginalMember(owner = "client!cba", name = "j", descriptor = "[I")
	public int[] bitpackedSoundData;

	@OriginalMember(owner = "client!cba", name = "k", descriptor = "Lclient!jba;")
	private PcmStream currentPcmStream;

	@OriginalMember(owner = "client!cba", name = "t", descriptor = "I")
	public int bufferCapacity;

	@OriginalMember(owner = "client!cba", name = "w", descriptor = "I")
	private int currentOffset;

	@OriginalMember(owner = "client!cba", name = "A", descriptor = "I")
	public int sampleRate2;

	@OriginalMember(owner = "client!cba", name = "h", descriptor = "I")
	private final int maxActiveVoices = 32;

	@OriginalMember(owner = "client!cba", name = "q", descriptor = "Z")
	private boolean shutdown = false;

	@OriginalMember(owner = "client!cba", name = "i", descriptor = "J")
	private long timeLastCycle = Utils.currentTimeMillis();

	@OriginalMember(owner = "client!cba", name = "u", descriptor = "J")
	private long nextBufferSwapTime = 0L;

	@OriginalMember(owner = "client!cba", name = "y", descriptor = "J")
	private long nextUpdate = 0L;

	@OriginalMember(owner = "client!cba", name = "v", descriptor = "I")
	private int streamUpdateCountdown = 0;

	@OriginalMember(owner = "client!cba", name = "x", descriptor = "I")
	private int lastDataConsumed = 0;

	@OriginalMember(owner = "client!cba", name = "B", descriptor = "[Lclient!jba;")
	private final PcmStream[] inactiveStreams = new PcmStream[8];

	@OriginalMember(owner = "client!cba", name = "D", descriptor = "Z")
	private boolean enabled = true;

	@OriginalMember(owner = "client!cba", name = "C", descriptor = "I")
	private int dataLeftLastCycle = 0;

	@OriginalMember(owner = "client!cba", name = "z", descriptor = "[Lclient!jba;")
	private final PcmStream[] activeStreams = new PcmStream[8];

	@OriginalMember(owner = "client!cba", name = "E", descriptor = "I")
	private int dataConsumed = 0;

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "()I")
	public int dataLeft() throws Exception {
		return this.bufferCapacity;
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(I)V")
	public void play(@OriginalArg(0) int bufferSize) throws Exception {
	}

	@OriginalMember(owner = "client!cba", name = "b", descriptor = "()V")
	public void open() throws Exception {
	}

	@OriginalMember(owner = "client!cba", name = "b", descriptor = "(I)V")
	public final synchronized void cycle() {
		if (this.shutdown) {
			return;
		}
		@Pc(12) long currentTime = Utils.currentTimeMillis();
		try {
			if (this.timeLastCycle + 6000L < currentTime) {
				this.timeLastCycle = currentTime - 6000L;
			}
			while (this.timeLastCycle + 5000L < currentTime) {
				this.skipAudioSamples();
				this.timeLastCycle += 256000 / PcmPlayerManager.sampleRate;
				currentTime = Utils.currentTimeMillis();
			}
		} catch (@Pc(61) Exception ex) {
			this.timeLastCycle = currentTime;
		}
		if (this.bitpackedSoundData == null) {
			return;
		}
		try {
			if (this.nextBufferSwapTime != 0L) {
				if (currentTime < this.nextBufferSwapTime) {
					return;
				}
				this.play(this.bufferCapacity);
				this.enabled = true;
				this.nextBufferSwapTime = 0L;
			}
			@Pc(98) int dataLeft = this.dataLeft();
			if (this.dataLeftLastCycle - dataLeft > this.dataConsumed) {
				this.dataConsumed = this.dataLeftLastCycle - dataLeft;
			}
			@Pc(120) int targetOffset = this.sampleRate2 + this.currentOffset;
			if (targetOffset + 256 > 16384) {
				targetOffset = 16128;
			}
			if (targetOffset + 256 > this.bufferCapacity) {
				this.bufferCapacity += 1024;
				if (this.bufferCapacity > 16384) {
					this.bufferCapacity = 16384;
				}
				this.close();
				this.play(this.bufferCapacity);
				dataLeft = 0;
				this.enabled = true;
				if (targetOffset + 256 > this.bufferCapacity) {
					targetOffset = this.bufferCapacity - 256;
					this.currentOffset = targetOffset - this.sampleRate2;
				}
			}
			while (targetOffset > dataLeft) {
				this.fill(this.bitpackedSoundData);
				this.writeToBuffer();
				dataLeft += 256;
			}
			if (currentTime > this.nextUpdate) {
				if (this.enabled) {
					this.enabled = false;
				} else if (this.dataConsumed == 0 && this.lastDataConsumed == 0) {
					this.close();
					this.nextBufferSwapTime = currentTime + 2000L;
					return;
				} else {
					this.currentOffset = Math.min(this.lastDataConsumed, this.dataConsumed);
					this.lastDataConsumed = this.dataConsumed;
				}
				this.nextUpdate = currentTime + 2000L;
				this.dataConsumed = 0;
			}
			this.dataLeftLastCycle = dataLeft;
		} catch (@Pc(248) Exception ex2) {
			this.close();
			this.nextBufferSwapTime = currentTime + 2000L;
		}
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(ILclient!jba;I)V")
	private void enqueueStreamByPriority(@OriginalArg(1) PcmStream audioBuss, @OriginalArg(2) int streamPriority) {
		@Pc(7) int bucketIndex = streamPriority >> 5;
		@Pc(19) PcmStream audioBuss2 = this.inactiveStreams[bucketIndex];
		if (audioBuss2 == null) {
			this.activeStreams[bucketIndex] = audioBuss;
		} else {
			audioBuss2.nextStream = audioBuss;
		}
		this.inactiveStreams[bucketIndex] = audioBuss;
		audioBuss.streamPriority = streamPriority;
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "([II)V")
	private void fill(@OriginalArg(0) int[] samples) {
		@Pc(1) short count = 256;
		if (PcmPlayerManager.stereo) {
			count = 512;
		}
		Arrays.clear(samples, 0, count);
		this.streamUpdateCountdown -= 256;
		if (this.currentPcmStream != null && this.streamUpdateCountdown <= 0) {
			this.streamUpdateCountdown += PcmPlayerManager.sampleRate >> 4;
			PcmStream.resetMixState(this.currentPcmStream);
			this.enqueueStreamByPriority(this.currentPcmStream, this.currentPcmStream.getPriority());
			@Pc(45) int totalCost = 0;
			@Pc(47) int pending = 255;
			@Pc(49) int pass = 7;
			@Pc(56) int bucket;
			label103: while (pending != 0) {
				@Pc(61) int round;
				if (pass < 0) {
					bucket = pass & 0x3;
					round = -(pass >> 2);
				} else {
					bucket = pass;
					round = 0;
				}
				for (@Pc(72) int bits = pending >>> bucket & 0x11111111; bits != 0; bits >>>= 0x4) {
					if ((bits & 0x1) != 0) {
						pending &= ~(0x1 << bucket);
						@Pc(88) PcmStream prev = null;
						@Pc(93) PcmStream buss = this.activeStreams[bucket];
						label97: while (true) {
							while (true) {
								if (buss == null) {
									break label97;
								}
								@Pc(97) AudioNode packet = buss.variableRateSoundPacket;
								if (packet == null || packet.activeVoiceCount <= round) {
									buss.fill = true;
									@Pc(121) int cost = buss.getActiveVoiceCount();
									totalCost += cost;
									if (packet != null) {
										packet.activeVoiceCount += cost;
									}
									if (totalCost >= this.maxActiveVoices) {
										break label103;
									}
									@Pc(140) PcmStream subBuss = buss.getFirstStream();
									if (subBuss != null) {
										@Pc(145) int parentPriority = buss.streamPriority;
										while (subBuss != null) {
											this.enqueueStreamByPriority(subBuss, parentPriority * subBuss.getPriority() >> 8);
											subBuss = buss.getNextStream();
										}
									}
									@Pc(164) PcmStream next = buss.nextStream;
									buss.nextStream = null;
									if (prev == null) {
										this.activeStreams[bucket] = next;
									} else {
										prev.nextStream = next;
									}
									if (next == null) {
										this.inactiveStreams[bucket] = prev;
									}
									buss = next;
								} else {
									pending |= 0x1 << bucket;
									prev = buss;
									buss = buss.nextStream;
								}
							}
						}
					}
					bucket += 4;
					round++;
				}
				pass--;
			}
			for (bucket = 0; bucket < 8; bucket++) {
				@Pc(207) PcmStream head = this.activeStreams[bucket];
				this.activeStreams[bucket] = this.inactiveStreams[bucket] = null;
				while (head != null) {
					@Pc(221) PcmStream audioBuss = head.nextStream;
					head.nextStream = null;
					head = audioBuss;
				}
			}
		}
		if (this.streamUpdateCountdown < 0) {
			this.streamUpdateCountdown = 0;
		}
		if (this.currentPcmStream != null) {
			this.currentPcmStream.fill(samples, 0, 256);
		}
		this.timeLastCycle = Utils.currentTimeMillis();
	}

	@OriginalMember(owner = "client!cba", name = "c", descriptor = "(I)V")
	public final synchronized void start() {
		this.enabled = true;
		try {
			this.open();
		} catch (@Pc(16) Exception ex) {
			this.close();
			this.nextBufferSwapTime = Utils.currentTimeMillis() + 2000L;
		}
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(ZLclient!jba;)V")
	public final synchronized void setCurrentPcmStream(@OriginalArg(1) PcmStream audioBuss) {
		this.currentPcmStream = audioBuss;
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(IB)V")
	private void skipAudioSamples() {
		this.streamUpdateCountdown -= 256;
		if (this.streamUpdateCountdown < 0) {
			this.streamUpdateCountdown = 0;
		}
		if (this.currentPcmStream != null) {
			this.currentPcmStream.skip(256);
		}
	}

	@OriginalMember(owner = "client!cba", name = "c", descriptor = "()V")
	public void close() {
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(Z)V")
	public final synchronized void shutdown() {
		if (PcmPlayerManager.runnable != null) {
			@Pc(9) boolean readyToExit = true;
			for (@Pc(11) int i = 0; i < 2; i++) {
				if (PcmPlayerManager.runnable.players[i] == this) {
					PcmPlayerManager.runnable.players[i] = null;
				}
				if (PcmPlayerManager.runnable.players[i] != null) {
					readyToExit = false;
				}
			}
			if (readyToExit) {
				PcmPlayerManager.runnable.stopping = true;
				while (PcmPlayerManager.runnable.running) {
					TimeUtils.sleep(50L);
				}
				PcmPlayerManager.runnable = null;
			}
		}
		this.close();
		this.bitpackedSoundData = null;
		this.shutdown = true;
	}

	@OriginalMember(owner = "client!cba", name = "d", descriptor = "()V")
	public void writeToBuffer() throws Exception {
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(Ljava/awt/Component;)V")
	public void init(@OriginalArg(0) java.awt.Component component) throws Exception {
	}
}
