package com.jagex.game.runetek5.config.sequence.frameset;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!raa")
public final class AnimBase extends Node {

	@OriginalMember(owner = "client!raa", name = "s", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!raa", name = "z", descriptor = "I")
	public final int numTransformation;

	@OriginalMember(owner = "client!raa", name = "t", descriptor = "[[I")
	public final int[][] labels;

	@OriginalMember(owner = "client!raa", name = "r", descriptor = "[I")
	public final int[] transformationType;

	@OriginalMember(owner = "client!raa", name = "u", descriptor = "[I")
	public final int[] submeshes;

	@OriginalMember(owner = "client!raa", name = "o", descriptor = "[Z")
	public final boolean[] shadowed;

	@OriginalMember(owner = "client!raa", name = "<init>", descriptor = "(I[B)V")
	public AnimBase(@OriginalArg(0) int id, @OriginalArg(1) byte[] bytes) {
		this.id = id;
		@Pc(11) Packet packet = new Packet(bytes);
		this.numTransformation = packet.readUByte();
		this.labels = new int[this.numTransformation][];
		this.transformationType = new int[this.numTransformation];
		this.submeshes = new int[this.numTransformation];
		this.shadowed = new boolean[this.numTransformation];
		for (@Pc(38) int i = 0; i < this.numTransformation; i++) {
			this.transformationType[i] = packet.readUByte();
			if (this.transformationType[i] == 6) {
				this.transformationType[i] = 2;
			}
		}
		for (@Pc(69) int j = 0; j < this.numTransformation; j++) {
			this.shadowed[j] = packet.readUByte() == 1;
		}
		for (@Pc(96) int k = 0; k < this.numTransformation; k++) {
			this.submeshes[k] = packet.readUShort();
		}
		for (@Pc(116) int m = 0; m < this.numTransformation; m++) {
			this.labels[m] = new int[packet.readUByte()];
		}
		for (@Pc(137) int transform = 0; transform < this.numTransformation; transform++) {
			for (@Pc(141) int label = 0; label < this.labels[transform].length; label++) {
				this.labels[transform][label] = packet.readUByte();
			}
		}
	}
}
