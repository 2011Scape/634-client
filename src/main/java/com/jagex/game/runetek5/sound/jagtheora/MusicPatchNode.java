package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;

@OriginalClass("client!vv")
public final class MusicPatchNode extends Node {

	@OriginalMember(owner = "client!vv", name = "p", descriptor = "I")
	public int maxVolumeLevel;

	@OriginalMember(owner = "client!vv", name = "q", descriptor = "I")
	public int currentTrack;

	@OriginalMember(owner = "client!vv", name = "s", descriptor = "I")
	public int volumeEnvelopeIndex;

	@OriginalMember(owner = "client!vv", name = "t", descriptor = "I")
	public int volumeEnvelopePosition;

	@OriginalMember(owner = "client!vv", name = "u", descriptor = "I")
	public int currentNotePitch;

	@OriginalMember(owner = "client!vv", name = "v", descriptor = "I")
	public int portamentoPitchOffset;

	@OriginalMember(owner = "client!vv", name = "x", descriptor = "I")
	public int currentPanValue;

	@OriginalMember(owner = "client!vv", name = "y", descriptor = "Lclient!ho;")
	public MusicPatchInfo musicPatchInfo;

	@OriginalMember(owner = "client!vv", name = "z", descriptor = "I")
	public int vibratoPhase;

	@OriginalMember(owner = "client!vv", name = "A", descriptor = "Lclient!ob;")
	public VariableRateSoundPacket audioFile;

	@OriginalMember(owner = "client!vv", name = "D", descriptor = "I")
	public int notePitchAdjustment;

	@OriginalMember(owner = "client!vv", name = "F", descriptor = "Lclient!via;")
	public AreaSoundStream stream;

	@OriginalMember(owner = "client!vv", name = "G", descriptor = "I")
	public int retriggerPhase;

	@OriginalMember(owner = "client!vv", name = "H", descriptor = "I")
	public int tickCounter;

	@OriginalMember(owner = "client!vv", name = "I", descriptor = "I")
	public int releaseEnvelopeIndex;

	@OriginalMember(owner = "client!vv", name = "J", descriptor = "Lclient!jf;")
	public MusicPatch patch;

	@OriginalMember(owner = "client!vv", name = "K", descriptor = "I")
	public int tickSampleLength;

	@OriginalMember(owner = "client!vv", name = "L", descriptor = "I")
	public int releasePhase;

	@OriginalMember(owner = "client!vv", name = "M", descriptor = "I")
	public int loopVariable;

	@OriginalMember(owner = "client!vv", name = "N", descriptor = "I")
	public int portamentoFactor;

	@OriginalMember(owner = "client!vv", name = "O", descriptor = "I")
	public int volumeEnvelopeTime;

	@OriginalMember(owner = "client!vv", name = "a", descriptor = "(B)V")
	public void clear() {
		this.stream = null;
		this.audioFile = null;
		this.musicPatchInfo = null;
		this.patch = null;
	}
}
