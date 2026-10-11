package com.jagex.game.runetek5.js5.request;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.Decompressor;

@OriginalClass("client!qh")
public final class Js5ResourceRequest extends Js5WorkerRequestMessage {

	@OriginalMember(owner = "client!qh", name = "A", descriptor = "I")
	public int type;

	@OriginalMember(owner = "client!qh", name = "D", descriptor = "Lclient!nw;")
	public Decompressor fileSystem;

	@OriginalMember(owner = "client!qh", name = "I", descriptor = "[B")
	public byte[] data;

	@OriginalMember(owner = "client!qh", name = "e", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		return super.waiting ? 0 : 100;
	}

	@OriginalMember(owner = "client!qh", name = "f", descriptor = "(I)[B")
	@Override
	public byte[] getData() {
		if (super.waiting) {
			throw new RuntimeException();
		}
		return this.data;
	}
}
