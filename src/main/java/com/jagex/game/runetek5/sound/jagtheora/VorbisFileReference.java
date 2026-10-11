package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qc")
public final class VorbisFileReference extends JagTheoraManager {

	@OriginalMember(owner = "client!qc", name = "M", descriptor = "[I")
	private int[] pages;

	@OriginalMember(owner = "client!qc", name = "N", descriptor = "I")
	private int currentChunkIndex;

	@OriginalMember(owner = "client!qc", name = "S", descriptor = "[[B")
	private final byte[][] chunkBuffers = new byte[10][];

	@OriginalMember(owner = "client!qc", name = "R", descriptor = "Lclient!cea;")
	private final Packet currentChunkPacket = new Packet(null);

	@OriginalMember(owner = "client!qc", name = "T", descriptor = "Lclient!cea;")
	private final Packet byteBuffer = new Packet(null);

	@OriginalMember(owner = "client!qc", name = "K", descriptor = "Lclient!in;")
	private final Js5ResourceProvider resourceProvider;

	@OriginalMember(owner = "client!qc", name = "P", descriptor = "I")
	private final int fileId;

	@OriginalMember(owner = "client!qc", name = "<init>", descriptor = "(ILclient!in;I)V")
	public VorbisFileReference(@OriginalArg(0) int bufferSize, @OriginalArg(1) Js5ResourceProvider resourceProvider, @OriginalArg(2) int fileId) {
		super(bufferSize);
		this.resourceProvider = resourceProvider;
		this.fileId = fileId;
	}

	@OriginalMember(owner = "client!qc", name = "c", descriptor = "(B)V")
	public void readNextPages() {
		if (this.pages == null) {
			return;
		}
		for (@Pc(15) int i = 0; i < 10; i++) {
			if (this.pages.length <= this.currentChunkIndex + i) {
				return;
			}
			if (this.chunkBuffers[i] == null && this.resourceProvider.isFileReady(this.pages[i + this.currentChunkIndex], 0)) {
				this.chunkBuffers[i] = this.resourceProvider.getFile(this.pages[this.currentChunkIndex + i], 0);
			}
		}
	}

	@OriginalMember(owner = "client!qc", name = "a", descriptor = "(B[B)I")
	@Override
	public int readPage(@OriginalArg(1) byte[] buffer) throws IOException {
		@Pc(46) int local46;
		@Pc(52) int i;
		if (this.pages == null) {
			if (!this.resourceProvider.isFileReady(this.fileId, 0)) {
				return 0;
			}
			@Pc(26) byte[] data = this.resourceProvider.getFile(this.fileId, 0);
			if (data == null) {
				throw new IllegalStateException("");
			}
			this.byteBuffer.buffer = data;
			this.byteBuffer.currentPosition = 0;
			int pageCount = data.length >> 1;
			this.pages = new int[pageCount];
			for (i = 0; i < pageCount; i++) {
				this.pages[i] = this.byteBuffer.readUShort();
			}
		}
		if (this.currentChunkIndex >= this.pages.length) {
			return -1;
		}
		this.readNextPages();
		this.byteBuffer.buffer = buffer;
		this.byteBuffer.currentPosition = 0;
		do {
			if (this.byteBuffer.buffer.length <= this.byteBuffer.currentPosition) {
				this.byteBuffer.buffer = null;
				return buffer.length;
			}
			if (this.currentChunkPacket.buffer == null) {
				if (this.chunkBuffers[0] == null) {
					this.byteBuffer.buffer = null;
					return this.byteBuffer.currentPosition;
				}
				this.currentChunkPacket.buffer = this.chunkBuffers[0];
			}
			@Pc(131) int availableBuffer = this.byteBuffer.buffer.length - this.byteBuffer.currentPosition;
			int chunkRemaining = this.currentChunkPacket.buffer.length - this.currentChunkPacket.currentPosition;
			if (availableBuffer < chunkRemaining) {
				this.currentChunkPacket.readBytes(this.byteBuffer.currentPosition, availableBuffer, this.byteBuffer.buffer);
				this.byteBuffer.buffer = null;
				return buffer.length;
			}
			this.byteBuffer.writeBytes(chunkRemaining, this.currentChunkPacket.currentPosition, this.currentChunkPacket.buffer);
			this.currentChunkPacket.currentPosition = 0;
			this.currentChunkPacket.buffer = null;
			this.currentChunkIndex++;
			for (i = 0; i < 9; i++) {
				this.chunkBuffers[i] = this.chunkBuffers[i + 1];
			}
			this.chunkBuffers[9] = null;
		} while (this.currentChunkIndex < this.pages.length);
		this.byteBuffer.buffer = null;
		return this.byteBuffer.currentPosition;
	}
}
