package com.jagex.game.runetek5.js5.request.remote;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.js5.request.Js5WorkerRequestMessage;

@OriginalClass("client!mba")
public final class Js5NetQueueRequest extends Js5WorkerRequestMessage {

	@OriginalMember(owner = "client!mba", name = "G", descriptor = "B")
	public byte padding;

	@OriginalMember(owner = "client!mba", name = "L", descriptor = "Lclient!cea;")
	public Packet stream;

	@OriginalMember(owner = "client!mba", name = "O", descriptor = "I")
	public int blockBytesRead;

	@OriginalMember(owner = "client!mba", name = "f", descriptor = "(I)[B")
	@Override
	public byte[] getData() {
		if (super.waiting || this.stream.currentPosition < this.stream.buffer.length - this.padding) {
			throw new RuntimeException();
		}
		return this.stream.buffer;
	}

	@OriginalMember(owner = "client!mba", name = "e", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		return this.stream == null ? 0 : this.stream.currentPosition * 100 / (this.stream.buffer.length - this.padding);
	}
}
