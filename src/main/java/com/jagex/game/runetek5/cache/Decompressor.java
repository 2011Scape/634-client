package com.jagex.game.runetek5.cache;

import com.jagex.game.runetek5.cache.FileOnDisk;
import java.io.EOFException;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!nw")
public final class Decompressor {

	@OriginalMember(owner = "client!nw", name = "c", descriptor = "Lclient!l;")
	private FileOnDisk indexFile = null;

	@OriginalMember(owner = "client!nw", name = "h", descriptor = "Lclient!l;")
	private FileOnDisk datFile = null;

	@OriginalMember(owner = "client!nw", name = "i", descriptor = "I")
	private int sizeCap = 65000;

	@OriginalMember(owner = "client!nw", name = "b", descriptor = "I")
	private final int index;

	@OriginalMember(owner = "client!nw", name = "<init>", descriptor = "(ILclient!l;Lclient!l;I)V")
	public Decompressor(@OriginalArg(0) int index, @OriginalArg(1) FileOnDisk bufferedFile, @OriginalArg(2) FileOnDisk bufferedFile2, @OriginalArg(3) int sizeCap) {
		this.index = index;
		this.indexFile = bufferedFile;
		this.datFile = bufferedFile2;
		this.sizeCap = sizeCap;
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(II[BI)Z")
	public boolean put(@OriginalArg(0) int size, @OriginalArg(1) int fileId, @OriginalArg(2) byte[] data) {
		@Pc(11) FileOnDisk bufferedFile = this.indexFile;
		synchronized (this.indexFile) {
			if (size < 0 || this.sizeCap < size) {
				throw new IllegalArgumentException();
			}
			@Pc(38) boolean successful = this.put(data, fileId, size, true);
			if (!successful) {
				successful = this.put(data, fileId, size, false);
			}
			return successful;
		}
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(BI)[B")
	public byte[] read(@OriginalArg(1) int fileId) {
		@Pc(8) FileOnDisk bufferedFile = this.indexFile;
		synchronized (this.indexFile) {
			try {
				if (this.datFile.getInitialCapacity() < (long) (fileId * 6 + 6)) {
					return null;
				}
				this.datFile.seek((long) (fileId * 6));
				this.datFile.read(0, Decompressor.buffer, 6);
				@Pc(71) int dataSize = (Decompressor.buffer[2] & 0xFF) + ((Decompressor.buffer[1] & 0xFF) << 8) + ((Decompressor.buffer[0] & 0xFF) << 16);
				@Pc(94) int sector = (Decompressor.buffer[5] & 0xFF) + ((Decompressor.buffer[3] & 0xFF) << 16) + ((Decompressor.buffer[4] & 0xFF) << 8);
				if (dataSize < 0 || dataSize > this.sizeCap) {
					return null;
				} else if (sector > 0 && (long) sector <= this.indexFile.getInitialCapacity() / 520L) {
					@Pc(134) byte[] data = new byte[dataSize];
					@Pc(136) int bytesRead = 0;
					@Pc(147) int chunkIndex = 0;
					label55: while (bytesRead < dataSize) {
						if (sector == 0) {
							return null;
						}
						this.indexFile.seek((long) (sector * 520));
						@Pc(173) int chunkSize = dataSize - bytesRead;
						if (chunkSize > 512) {
							chunkSize = 512;
						}
						this.indexFile.read(0, Decompressor.buffer, chunkSize + 8);
						@Pc(203) int storedFileId = ((Decompressor.buffer[0] & 0xFF) << 8) + (Decompressor.buffer[1] & 0xFF);
						@Pc(217) int storedChunkIndex = ((Decompressor.buffer[2] & 0xFF) << 8) + (Decompressor.buffer[3] & 0xFF);
						@Pc(239) int nextSector = ((Decompressor.buffer[5] & 0xFF) << 8) + ((Decompressor.buffer[4] & 0xFF) << 16) + (Decompressor.buffer[6] & 0xFF);
						@Pc(245) int storedIndex = Decompressor.buffer[7] & 0xFF;
						if (fileId == storedFileId && chunkIndex == storedChunkIndex && this.index == storedIndex) {
							if (nextSector >= 0 && (long) nextSector <= this.indexFile.getInitialCapacity() / 520L) {
								sector = nextSector;
								chunkIndex++;
								@Pc(294) int offset = 0;
								while (true) {
									if (offset >= chunkSize) {
										continue label55;
									}
									data[bytesRead++] = Decompressor.buffer[offset + 8];
									offset++;
								}
							}
							return null;
						}
						return null;
					}
					return data;
				} else {
					return null;
				}
			} catch (@Pc(323) IOException ex) {
				return null;
			}
		}
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "([BIIIZ)Z")
	private boolean put(@OriginalArg(0) byte[] data, @OriginalArg(2) int fileId, @OriginalArg(3) int size, @OriginalArg(4) boolean overwrite) {
		@Pc(8) FileOnDisk bufferedFile = this.indexFile;
		synchronized (this.indexFile) {
			try {
				@Pc(69) int sector;
				if (overwrite) {
					if (this.datFile.getInitialCapacity() < (long) (fileId * 6 + 6)) {
						return false;
					}
					this.datFile.seek((long) (fileId * 6));
					this.datFile.read(0, Decompressor.buffer, 6);
					sector = (Decompressor.buffer[5] & 0xFF) + ((Decompressor.buffer[3] & 0xFF) << 16) + ((Decompressor.buffer[4] & 0xFF) << 8);
					if (sector <= 0 || (long) sector > this.indexFile.getInitialCapacity() / 520L) {
						return false;
					}
				} else {
					sector = (int) ((this.indexFile.getInitialCapacity() + 519L) / 520L);
					if (sector == 0) {
						sector = 1;
					}
				}
				Decompressor.buffer[0] = (byte) (size >> 16);
				Decompressor.buffer[4] = (byte) (sector >> 8);
				Decompressor.buffer[2] = (byte) size;
				Decompressor.buffer[1] = (byte) (size >> 8);
				Decompressor.buffer[3] = (byte) (sector >> 16);
				Decompressor.buffer[5] = (byte) sector;
				this.datFile.seek((long) (fileId * 6));
				this.datFile.write(0, 6, Decompressor.buffer);
				@Pc(168) int bytesWritten = 0;
				@Pc(176) int chunkIndex = 0;
				while (bytesWritten < size) {
					@Pc(180) int nextSector = 0;
					@Pc(215) int local215;
					if (overwrite) {
						this.indexFile.seek((long) (sector * 520));
						try {
							this.indexFile.read(0, Decompressor.buffer, 8);
						} catch (@Pc(199) EOFException ex) {
							return true;
						}
						int storedFileId = (Decompressor.buffer[1] & 0xFF) + ((Decompressor.buffer[0] & 0xFF) << 8);
						nextSector = ((Decompressor.buffer[5] & 0xFF) << 8) + (((Decompressor.buffer[4] & 0xFF) << 16) + (Decompressor.buffer[6] & 0xFF));
						@Pc(252) int storedChunkIndex = ((Decompressor.buffer[2] & 0xFF) << 8) + (Decompressor.buffer[3] & 0xFF);
						@Pc(258) int storedIndex = Decompressor.buffer[7] & 0xFF;
						if (fileId != storedFileId || storedChunkIndex != chunkIndex || storedIndex != this.index) {
							return false;
						}
						if (nextSector < 0 || (long) nextSector > this.indexFile.getInitialCapacity() / 520L) {
							return false;
						}
					}
					if (nextSector == 0) {
						overwrite = false;
						nextSector = (int) ((this.indexFile.getInitialCapacity() + 519L) / 520L);
						if (nextSector == 0) {
							nextSector++;
						}
						if (nextSector == sector) {
							nextSector++;
						}
					}
					if (size - bytesWritten <= 512) {
						nextSector = 0;
					}
					Decompressor.buffer[3] = (byte) chunkIndex;
					Decompressor.buffer[2] = (byte) (chunkIndex >> 8);
					Decompressor.buffer[0] = (byte) (fileId >> 8);
					Decompressor.buffer[1] = (byte) fileId;
					Decompressor.buffer[5] = (byte) (nextSector >> 8);
					Decompressor.buffer[6] = (byte) nextSector;
					Decompressor.buffer[7] = (byte) this.index;
					Decompressor.buffer[4] = (byte) (nextSector >> 16);
					this.indexFile.seek((long) (sector * 520));
					this.indexFile.write(0, 8, Decompressor.buffer);
					int chunkSize = size - bytesWritten;
					if (chunkSize > 512) {
						chunkSize = 512;
					}
					this.indexFile.write(bytesWritten, chunkSize, data);
					chunkIndex++;
					bytesWritten += chunkSize;
					sector = nextSector;
				}
				return true;
			} catch (@Pc(437) IOException ex2) {
				return false;
			}
		}
	}

	@OriginalMember(owner = "client!nw", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		return "Cache:" + this.index;
	}
	@OriginalMember(owner = "client!su", name = "k", descriptor = "[B")
	public static final byte[] buffer = new byte[520];
}
