package com.jagex.game.runetek5.toolkit.vertexdeclaration;

import com.jagex.game.runetek5.VertexAttribute;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jl")
public final class VertexDeclarationComponent {

	@OriginalMember(owner = "client!jl", name = "b", descriptor = "J")
	private long bitpacked;

	@OriginalMember(owner = "client!jl", name = "c", descriptor = "I")
	private int size;

	@OriginalMember(owner = "client!jl", name = "<init>", descriptor = "(Lclient!daa;)V")
	public VertexDeclarationComponent(@OriginalArg(0) VertexAttribute action) {
		this.bitpacked = action.slot;
		this.size = 1;
	}

	@OriginalMember(owner = "client!jl", name = "<init>", descriptor = "([Lclient!daa;)V")
	public VertexDeclarationComponent(@OriginalArg(0) VertexAttribute[] actions) {
		for (@Pc(5) int i = 0; i < actions.length; i++) {
			this.pack(actions[i]);
		}
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(Lclient!daa;I)V")
	private void pack(@OriginalArg(0) VertexAttribute action) {
		this.bitpacked |= action.slot << this.size++ * VertexDeclarationAction.BITS_PER_SLOT;
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(BI)I")
	private int getSlotIndex(@OriginalArg(1) int index) {
		return (int) (this.bitpacked >> VertexDeclarationAction.BITS_PER_SLOT * index) & 0xF;
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(II)Lclient!daa;")
	public VertexAttribute getAction(@OriginalArg(0) int index) {
		return VertexDeclarationAction.forIndex(this.getSlotIndex(index));
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(B)I")
	public int getSize() {
		return this.size;
	}
}
