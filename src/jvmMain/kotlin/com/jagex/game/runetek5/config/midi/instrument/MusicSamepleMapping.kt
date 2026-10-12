package com.jagex.game.runetek5.config.midi.instrument

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.VorbisSound

@OriginalClass("client!vba")
class MusicSamepleMapping {

	@OriginalMember(owner = "client!vba", name = "b", descriptor = "I")
	@JvmField
	val submaps: Int

	@OriginalMember(owner = "client!vba", name = "d", descriptor = "I")
	@JvmField
	var mux: Int = 0

	@OriginalMember(owner = "client!vba", name = "c", descriptor = "[I")
	@JvmField
	val submapFloors: IntArray

	@OriginalMember(owner = "client!vba", name = "a", descriptor = "[I")
	@JvmField
	val submapResidues: IntArray

	@OriginalMember(owner = "client!vba", name = "<init>", descriptor = "()V")
	constructor() {
		MusicSample.getBits(16)
		this.submaps = if (VorbisSound.getBit() == 0) 1 else MusicSample.getBits(4) + 1
		if (VorbisSound.getBit() != 0) {
			MusicSample.getBits(8)
		}
		MusicSample.getBits(2)
		if (this.submaps > 1) {
			this.mux = MusicSample.getBits(4)
		}
		this.submapFloors = IntArray(this.submaps)
		this.submapResidues = IntArray(this.submaps)
		for (@Pc(42) i in 0 until this.submaps) {
			MusicSample.getBits(8)
			this.submapFloors[i] = MusicSample.getBits(8)
			this.submapResidues[i] = MusicSample.getBits(8)
		}
	}
}
