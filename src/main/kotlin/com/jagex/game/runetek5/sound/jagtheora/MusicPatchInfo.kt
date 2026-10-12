package com.jagex.game.runetek5.sound.jagtheora

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ho")
class MusicPatchInfo {

	@OriginalMember(owner = "client!ho", name = "a", descriptor = "[B")
	@JvmField
	var releaseEnvelopeData: ByteArray? = null

	@OriginalMember(owner = "client!ho", name = "b", descriptor = "I")
	@JvmField
	var volumeEnvelopeKeyScaling: Int = 0

	@OriginalMember(owner = "client!ho", name = "c", descriptor = "I")
	@JvmField
	var vibratoDelay: Int = 0

	@OriginalMember(owner = "client!ho", name = "d", descriptor = "I")
	@JvmField
	var volumeEnvelopeDecay: Int = 0

	@OriginalMember(owner = "client!ho", name = "e", descriptor = "[B")
	@JvmField
	var volumeEnvelopeData: ByteArray? = null

	@OriginalMember(owner = "client!ho", name = "f", descriptor = "I")
	@JvmField
	var releaseEnvelopeKeyScaling: Int = 0

	@OriginalMember(owner = "client!ho", name = "g", descriptor = "I")
	@JvmField
	var decayKeyScaling: Int = 0

	@OriginalMember(owner = "client!ho", name = "i", descriptor = "I")
	@JvmField
	var volumeEnvelopeRelease: Int = 0

	@OriginalMember(owner = "client!ho", name = "j", descriptor = "I")
	@JvmField
	var vibratoLfoPitch: Int = 0
}
