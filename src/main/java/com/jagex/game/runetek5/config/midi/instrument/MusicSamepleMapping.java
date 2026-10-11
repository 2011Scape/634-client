package com.jagex.game.runetek5.config.midi.instrument;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.VorbisSound;

@OriginalClass("client!vba")
public final class MusicSamepleMapping {

	@OriginalMember(owner = "client!vba", name = "b", descriptor = "I")
	public final int submaps;

	@OriginalMember(owner = "client!vba", name = "d", descriptor = "I")
	public int mux;

	@OriginalMember(owner = "client!vba", name = "c", descriptor = "[I")
	public final int[] submapFloors;

	@OriginalMember(owner = "client!vba", name = "a", descriptor = "[I")
	public final int[] submapResidues;

	@OriginalMember(owner = "client!vba", name = "<init>", descriptor = "()V")
	public MusicSamepleMapping() {
		MusicSample.getBits(16);
		this.submaps = VorbisSound.getBit() == 0 ? 1 : MusicSample.getBits(4) + 1;
		if (VorbisSound.getBit() != 0) {
			MusicSample.getBits(8);
		}
		MusicSample.getBits(2);
		if (this.submaps > 1) {
			this.mux = MusicSample.getBits(4);
		}
		this.submapFloors = new int[this.submaps];
		this.submapResidues = new int[this.submaps];
		for (@Pc(42) int i = 0; i < this.submaps; i++) {
			MusicSample.getBits(8);
			this.submapFloors[i] = MusicSample.getBits(8);
			this.submapResidues[i] = MusicSample.getBits(8);
		}
	}
}
