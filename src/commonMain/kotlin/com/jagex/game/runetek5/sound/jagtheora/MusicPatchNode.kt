package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import kotlin.jvm.JvmField

@OriginalClass("client!vv")
class MusicPatchNode : Node() {

	@OriginalMember(owner = "client!vv", name = "p", descriptor = "I")
	@JvmField
	var maxVolumeLevel: Int = 0

	@OriginalMember(owner = "client!vv", name = "q", descriptor = "I")
	@JvmField
	var currentTrack: Int = 0

	@OriginalMember(owner = "client!vv", name = "s", descriptor = "I")
	@JvmField
	var volumeEnvelopeIndex: Int = 0

	@OriginalMember(owner = "client!vv", name = "t", descriptor = "I")
	@JvmField
	var volumeEnvelopePosition: Int = 0

	@OriginalMember(owner = "client!vv", name = "u", descriptor = "I")
	@JvmField
	var currentNotePitch: Int = 0

	@OriginalMember(owner = "client!vv", name = "v", descriptor = "I")
	@JvmField
	var portamentoPitchOffset: Int = 0

	@OriginalMember(owner = "client!vv", name = "x", descriptor = "I")
	@JvmField
	var currentPanValue: Int = 0

	@OriginalMember(owner = "client!vv", name = "y", descriptor = "Lclient!ho;")
	@JvmField
	var musicPatchInfo: MusicPatchInfo? = null

	@OriginalMember(owner = "client!vv", name = "z", descriptor = "I")
	@JvmField
	var vibratoPhase: Int = 0

	@OriginalMember(owner = "client!vv", name = "A", descriptor = "Lclient!ob;")
	@JvmField
	var audioFile: VariableRateSoundPacket? = null

	@OriginalMember(owner = "client!vv", name = "D", descriptor = "I")
	@JvmField
	var notePitchAdjustment: Int = 0

	@OriginalMember(owner = "client!vv", name = "F", descriptor = "Lclient!via;")
	@JvmField
	var stream: AreaSoundStream? = null

	@OriginalMember(owner = "client!vv", name = "G", descriptor = "I")
	@JvmField
	var retriggerPhase: Int = 0

	@OriginalMember(owner = "client!vv", name = "H", descriptor = "I")
	@JvmField
	var tickCounter: Int = 0

	@OriginalMember(owner = "client!vv", name = "I", descriptor = "I")
	@JvmField
	var releaseEnvelopeIndex: Int = 0

	@OriginalMember(owner = "client!vv", name = "J", descriptor = "Lclient!jf;")
	@JvmField
	var patch: MusicPatch? = null

	@OriginalMember(owner = "client!vv", name = "K", descriptor = "I")
	@JvmField
	var tickSampleLength: Int = 0

	@OriginalMember(owner = "client!vv", name = "L", descriptor = "I")
	@JvmField
	var releasePhase: Int = 0

	@OriginalMember(owner = "client!vv", name = "M", descriptor = "I")
	@JvmField
	var loopVariable: Int = 0

	@OriginalMember(owner = "client!vv", name = "N", descriptor = "I")
	@JvmField
	var portamentoFactor: Int = 0

	@OriginalMember(owner = "client!vv", name = "O", descriptor = "I")
	@JvmField
	var volumeEnvelopeTime: Int = 0

	@OriginalMember(owner = "client!vv", name = "a", descriptor = "(B)V")
	fun clear() {
		this.stream = null
		this.audioFile = null
		this.musicPatchInfo = null
		this.patch = null
	}
}
