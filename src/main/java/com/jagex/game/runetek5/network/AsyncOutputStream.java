package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.core.io.connection.DummyOutputStream;
import com.jagex.game.runetek5.core.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!rha")
public final class AsyncOutputStream implements Runnable {

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "Ljava/io/IOException;")
	private IOException error;

	@OriginalMember(owner = "client!rha", name = "b", descriptor = "I")
	private int bufferPointer = 0;

	@OriginalMember(owner = "client!rha", name = "j", descriptor = "I")
	private int writePointer = 0;

	@OriginalMember(owner = "client!rha", name = "i", descriptor = "Ljava/io/OutputStream;")
	private OutputStream outputstream;

	@OriginalMember(owner = "client!rha", name = "l", descriptor = "I")
	private final int bufferSize;

	@OriginalMember(owner = "client!rha", name = "c", descriptor = "[B")
	private final byte[] buffer;

	@OriginalMember(owner = "client!rha", name = "e", descriptor = "Ljava/lang/Thread;")
	private final Thread thread;

	@OriginalMember(owner = "client!rha", name = "<init>", descriptor = "(Ljava/io/OutputStream;I)V")
	public AsyncOutputStream(@OriginalArg(0) OutputStream outputStream, @OriginalArg(1) int capacity) {
		this.outputstream = outputStream;
		this.bufferSize = capacity + 1;
		this.buffer = new byte[this.bufferSize];
		this.thread = new Thread(this);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@OriginalMember(owner = "client!rha", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (true) {
			@Pc(35) int length;
			synchronized (this) {
				while (true) {
					if (this.error != null) {
						return;
					}
					if (this.writePointer < this.bufferPointer) {
						length = this.bufferSize + this.writePointer - this.bufferPointer;
					} else {
						length = this.writePointer - this.bufferPointer;
					}
					if (length > 0) {
						break;
					}
					try {
						this.wait();
					} catch (@Pc(52) InterruptedException ex) {
					}
				}
			}
			try {
				if (this.bufferSize >= this.bufferPointer + length) {
					this.outputstream.write(this.buffer, this.bufferPointer, length);
				} else {
					@Pc(85) int tailLength = this.bufferSize - this.bufferPointer;
					this.outputstream.write(this.buffer, this.bufferPointer, tailLength);
					this.outputstream.write(this.buffer, 0, length - tailLength);
				}
			} catch (@Pc(105) IOException ex2) {
				@Pc(105) IOException local105x = ex2;
				synchronized (this) {
					this.error = local105x;
					return;
				}
			}
			synchronized (this) {
				this.bufferPointer = (this.bufferPointer + length) % this.bufferSize;
			}
		}
	}

	@OriginalMember(owner = "client!rha", name = "b", descriptor = "(B)V")
	public void stop() {
		synchronized (this) {
			if (this.error == null) {
				this.error = new IOException("");
			}
			this.notifyAll();
		}
		try {
			this.thread.join();
		} catch (@Pc(35) InterruptedException ignored) {
		}
	}

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "(I)V")
	public void reset() {
		this.outputstream = new DummyOutputStream();
	}

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "(BII[B)V")
	public void write(@OriginalArg(1) int length, @OriginalArg(3) byte[] buffer) throws IOException {
		if (length < 0 || buffer.length < length) {
			throw new IOException();
		}
		synchronized (this) {
			if (this.error != null) {
				throw new IOException(this.error.toString());
			}
			@Pc(53) int freeSpace;
			if (this.bufferPointer <= this.writePointer) {
				freeSpace = this.bufferPointer + this.bufferSize - this.writePointer - 1;
			} else {
				freeSpace = this.bufferPointer - this.writePointer - 1;
			}
			if (freeSpace < length) {
				throw new IOException("");
			}
			if (length + this.writePointer > this.bufferSize) {
				@Pc(91) int tailLength = this.bufferSize - this.writePointer;
				Arrays.copy(buffer, 0, this.buffer, this.writePointer, tailLength);
				Arrays.copy(buffer, tailLength, this.buffer, 0, length - tailLength);
			} else {
				Arrays.copy(buffer, 0, this.buffer, this.writePointer, length);
			}
			this.writePointer = (length + this.writePointer) % this.bufferSize;
			this.notifyAll();
		}
	}
}
