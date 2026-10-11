package com.jagex.game.runetek5;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;

@OriginalClass("client!mt")
public final class LoadingScreenManager {

	@OriginalMember(owner = "client!mt", name = "d", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!mt", name = "j", descriptor = "[[I")
	private final int[][] sequences;

	@OriginalMember(owner = "client!mt", name = "k", descriptor = "[Z")
	private final boolean[] shuffled;

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "I")
	private final int defaultScreen;

	@OriginalMember(owner = "client!mt", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public LoadingScreenManager(@OriginalArg(0) Game modeGame, @OriginalArg(1) int language, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(1);
		@Pc(22) Packet packet = new Packet(this.configClient.getFile(0, 0));
		@Pc(26) int version = packet.readUByte();
		if (version > 3) {
			this.sequences = new int[0][];
			this.shuffled = new boolean[0];
			this.defaultScreen = -1;
		} else {
			@Pc(47) int opCount = packet.readUByte();
			@Pc(50) TipRenderDetails[] opTypes = TipRenderDetails.values();
			@Pc(52) boolean compatible = true;
			@Pc(62) int local62;
			@Pc(68) int local68;
			if (opCount == opTypes.length) {
				for (int opIndex = 0; opIndex < opTypes.length; opIndex++) {
					int opVersion = packet.readUByte();
					if (opVersion != opTypes[opIndex].version) {
						compatible = false;
						break;
					}
				}
			} else {
				compatible = false;
			}
			if (compatible) {
				int sequenceCount = packet.readUByte();
				int maxSequenceId = packet.readUByte();
				if (version > 2) {
					this.defaultScreen = packet.readShort();
				} else {
					this.defaultScreen = -1;
				}
				this.sequences = new int[maxSequenceId + 1][];
				this.shuffled = new boolean[maxSequenceId + 1];
				@Pc(146) int sequenceId;
				for (@Pc(140) int i = 0; i < sequenceCount; i++) {
					sequenceId = packet.readUByte();
					this.shuffled[sequenceId] = packet.readUByte() == 1;
					@Pc(162) int length = packet.readUShort();
					@Pc(175) int j;
					if (this.defaultScreen == -1) {
						this.sequences[sequenceId] = new int[length];
						for (j = 0; j < length; j++) {
							this.sequences[sequenceId][j] = packet.readUShort();
						}
					} else {
						this.sequences[sequenceId] = new int[length + 1];
						this.sequences[sequenceId][0] = this.defaultScreen;
						for (j = 0; j < length; j++) {
							this.sequences[sequenceId][j + 1] = packet.readUShort();
						}
					}
				}
				for (sequenceId = 0; sequenceId < maxSequenceId + 1; sequenceId++) {
					if (this.sequences[sequenceId] == null) {
						if (this.defaultScreen == -1) {
							this.sequences[sequenceId] = new int[0];
						} else {
							this.sequences[sequenceId] = new int[] { this.defaultScreen };
						}
					}
				}
			} else {
				this.shuffled = new boolean[0];
				this.sequences = new int[0][];
				this.defaultScreen = -1;
			}
		}
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(IB)Lclient!vp;")
	public LoadingScreenType loadScreenType(@OriginalArg(0) int id) {
		@Pc(10) byte[] data = this.configClient.getFile(1, id);
		@Pc(19) LoadingScreenType loadingScreenType = new LoadingScreenType();
		loadingScreenType.decode(new Packet(data));
		return loadingScreenType;
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(I)Z")
	public boolean hasDefault() {
		return this.defaultScreen != -1;
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(II)[I")
	public int[] getSequence(@OriginalArg(0) int sequenceId) {
		if (sequenceId < 0 || sequenceId >= this.sequences.length) {
			return this.defaultScreen == -1 ? new int[0] : new int[] { this.defaultScreen };
		} else if (this.shuffled[sequenceId] && this.sequences[sequenceId].length > 1) {
			@Pc(60) int start = this.defaultScreen == -1 ? 0 : 1;
			@Pc(64) Random random = new Random();
			@Pc(71) int[] sequence = new int[this.sequences[sequenceId].length];
			Arrays.copy(this.sequences[sequenceId], 0, sequence, 0, sequence.length);
			for (@Pc(83) int i = start; i < sequence.length; i++) {
				@Pc(95) int swapIndex = start + Utils.randomInt(sequence.length - start, random);
				@Pc(99) int temp = sequence[i];
				sequence[i] = sequence[swapIndex];
				sequence[swapIndex] = temp;
			}
			return sequence;
		} else {
			return this.sequences[sequenceId];
		}
	}
	@OriginalMember(owner = "client!ef", name = "f", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_57 = new LRUCache(128, 4);

	@OriginalMember(owner = "client!la", name = "r", descriptor = "Lclient!mt;")
	public static LoadingScreenManager aClass214_1;
	@OriginalMember(owner = "client!wga", name = "v", descriptor = "(I)V")
	public static void clearLoadingScreenSpriteCache() {
		LoadingScreenManager.aClass169_57.reset();
	}
}
