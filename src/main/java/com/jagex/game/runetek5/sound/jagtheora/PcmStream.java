package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.impl.AudioNode;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jba")
public abstract class PcmStream extends Node {

	@OriginalMember(owner = "client!jba", name = "p", descriptor = "Lclient!jba;")
	public PcmStream nextStream;

	@OriginalMember(owner = "client!jba", name = "q", descriptor = "I")
	public int streamPriority;

	@OriginalMember(owner = "client!jba", name = "r", descriptor = "Lclient!ji;")
	public AudioNode variableRateSoundPacket;

	@OriginalMember(owner = "client!jba", name = "o", descriptor = "Z")
	public volatile boolean fill = true;

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "([III)V")
	public final void process(@OriginalArg(0) int[] buffer, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		if (this.fill) {
			this.fill(buffer, offset, length);
		} else {
			this.skip(length);
		}
	}

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "()Lclient!jba;")
	public abstract PcmStream getFirstStream();

	@OriginalMember(owner = "client!jba", name = "b", descriptor = "([III)V")
	public abstract void fill(@OriginalArg(0) int[] buffer, @OriginalArg(1) int offset, @OriginalArg(2) int length);

	@OriginalMember(owner = "client!jba", name = "b", descriptor = "()Lclient!jba;")
	public abstract PcmStream getNextStream();

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "(I)V")
	public abstract void skip(@OriginalArg(0) int length);

	@OriginalMember(owner = "client!jba", name = "c", descriptor = "()I")
	public int getPriority() {
		return 255;
	}

	@OriginalMember(owner = "client!jba", name = "d", descriptor = "()I")
	public abstract int getActiveVoiceCount();
	@OriginalMember(owner = "client!il", name = "a", descriptor = "(Lclient!jba;I)V")
	public static void resetMixState(@OriginalArg(0) PcmStream audioBuss) {
		audioBuss.fill = false;
		if (audioBuss.variableRateSoundPacket != null) {
			audioBuss.variableRateSoundPacket.activeVoiceCount = 0;
		}
		for (@Pc(22) PcmStream audioBuss2 = audioBuss.getFirstStream(); audioBuss2 != null; audioBuss2 = audioBuss.getNextStream()) {
			resetMixState(audioBuss2);
		}
	}
}
