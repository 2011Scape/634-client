package com.jagex.game.runetek5.js5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!cj")
public final class NamedFileReference {

	@OriginalMember(owner = "client!cj", name = "d", descriptor = "[I")
	private final int[] nameHashes;

	@OriginalMember(owner = "client!cj", name = "<init>", descriptor = "([I)V")
	public NamedFileReference(@OriginalArg(0) int[] hashes) {
		@Pc(5) int capacity;
		for (capacity = 1; capacity <= hashes.length + (hashes.length >> 1); capacity <<= 0x1) {
		}
		this.nameHashes = new int[capacity + capacity];
		for (@Pc(29) int i = 0; i < capacity + capacity; i++) {
			this.nameHashes[i] = -1;
		}
		@Pc(48) int index = 0;
		while (index < hashes.length) {
			@Pc(58) int position;
			for (position = hashes[index] & capacity - 1; this.nameHashes[position + position + 1] != -1; position = capacity - 1 & position + 1) {
			}
			this.nameHashes[position + position] = hashes[index];
			this.nameHashes[position + position + 1] = index++;
		}
	}

	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(II)I")
	public int find(@OriginalArg(1) int name) {
		@Pc(19) int size = (this.nameHashes.length >> 1) - 1;
		@Pc(23) int position = size & name;
		while (true) {
			@Pc(32) int index = this.nameHashes[position + position + 1];
			if (index == -1) {
				return -1;
			}
			if (name == this.nameHashes[position + position]) {
				return index;
			}
			position = size & position + 1;
		}
	}
}
