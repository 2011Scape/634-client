package com.jagex.game.runetek5.cache;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!so")
public final class JFS32 {

	@OriginalMember(owner = "client!so", name = "a", descriptor = "Ljava/io/RandomAccessFile;")
	private RandomAccessFile rwFile;

	@OriginalMember(owner = "client!so", name = "b", descriptor = "J")
	private final long maxCapacity;

	@OriginalMember(owner = "client!so", name = "d", descriptor = "J")
	private long seekPosition;

	@OriginalMember(owner = "client!so", name = "k", descriptor = "Ljava/io/File;")
	private final File source;

	@OriginalMember(owner = "client!so", name = "<init>", descriptor = "(Ljava/io/File;Ljava/lang/String;J)V")
	public JFS32(@OriginalArg(0) File file2, @OriginalArg(1) String string, @OriginalArg(2) long maxCapacity) throws IOException {
		if (maxCapacity == -1L) {
			maxCapacity = Long.MAX_VALUE;
		}
		if (maxCapacity < file2.length()) {
			file2.delete();
		}
		this.rwFile = new RandomAccessFile(file2, string);
		this.maxCapacity = maxCapacity;
		this.seekPosition = 0L;
		this.source = file2;
		@Pc(37) int firstByte = this.rwFile.read();
		if (firstByte != -1 && !string.equals("r")) {
			this.rwFile.seek(0L);
			this.rwFile.write(firstByte);
		}
		this.rwFile.seek(0L);
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "([BIBI)I")
	public int read(@OriginalArg(0) byte[] data, @OriginalArg(1) int offset, @OriginalArg(3) int length) throws IOException {
		@Pc(10) int bytesRead = this.rwFile.read(data, offset, length);
		if (bytesRead > 0) {
			this.seekPosition += bytesRead;
		}
		return bytesRead;
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(Z)V")
	public void close() throws IOException {
		if (this.rwFile != null) {
			this.rwFile.close();
			this.rwFile = null;
		}
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(BII[B)V")
	public void write(@OriginalArg(1) int offset, @OriginalArg(2) int length, @OriginalArg(3) byte[] data) throws IOException {
		if ((long) length + this.seekPosition > this.maxCapacity) {
			this.rwFile.seek(this.maxCapacity);
			this.rwFile.write(1);
			throw new EOFException();
		} else {
			this.rwFile.write(data, offset, length);
			this.seekPosition += length;
		}
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(I)Ljava/io/File;")
	public File getSource() {
		return this.source;
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(IJ)V")
	public void seek(@OriginalArg(1) long seekPosition) throws IOException {
		this.rwFile.seek(seekPosition);
		this.seekPosition = seekPosition;
	}

	@OriginalMember(owner = "client!so", name = "b", descriptor = "(B)J")
	public long length() throws IOException {
		return this.rwFile.length();
	}

	@OriginalMember(owner = "client!so", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		if (this.rwFile != null) {
			System.out.println("Warning! fileondisk " + this.source + " not closed correctly using close(). Auto-closing instead. ");
			this.close();
		}
	}
}
