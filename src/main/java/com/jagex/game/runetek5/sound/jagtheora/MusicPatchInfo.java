package com.jagex.game.runetek5.sound.jagtheora;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ho")
public final class MusicPatchInfo {

	@OriginalMember(owner = "client!ho", name = "a", descriptor = "[B")
	public byte[] releaseEnvelopeData;

	@OriginalMember(owner = "client!ho", name = "b", descriptor = "I")
	public int volumeEnvelopeKeyScaling;

	@OriginalMember(owner = "client!ho", name = "c", descriptor = "I")
	public int vibratoDelay;

	@OriginalMember(owner = "client!ho", name = "d", descriptor = "I")
	public int volumeEnvelopeDecay;

	@OriginalMember(owner = "client!ho", name = "e", descriptor = "[B")
	public byte[] volumeEnvelopeData;

	@OriginalMember(owner = "client!ho", name = "f", descriptor = "I")
	public int releaseEnvelopeKeyScaling;

	@OriginalMember(owner = "client!ho", name = "g", descriptor = "I")
	public int decayKeyScaling;

	@OriginalMember(owner = "client!ho", name = "i", descriptor = "I")
	public int volumeEnvelopeRelease;

	@OriginalMember(owner = "client!ho", name = "j", descriptor = "I")
	public int vibratoLfoPitch;
}
