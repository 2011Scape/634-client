package com.jagex.game.runetek5.network;

import com.jagex.game.runetek5.DroppedInputStream;
import com.jagex.game.runetek5.core.util.Arrays;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!gha")
public final class AsyncInputStream implements Runnable {

	@OriginalMember(owner = "client!gha", name = "o", descriptor = "Ljava/io/IOException;")
	private IOException readError;

	@OriginalMember(owner = "client!gha", name = "k", descriptor = "I")
	private int readPosition = 0;

	@OriginalMember(owner = "client!gha", name = "m", descriptor = "I")
	private int bufferPointer = 0;

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "I")
	private final int capacity;

	@OriginalMember(owner = "client!gha", name = "c", descriptor = "Ljava/io/InputStream;")
	private InputStream input;

	@OriginalMember(owner = "client!gha", name = "i", descriptor = "[B")
	private final byte[] buffer;

	@OriginalMember(owner = "client!gha", name = "l", descriptor = "Ljava/lang/Thread;")
	private final Thread executingThread;

	@OriginalMember(owner = "client!gha", name = "<init>", descriptor = "(Ljava/io/InputStream;I)V")
	public AsyncInputStream(@OriginalArg(0) InputStream inputStream, @OriginalArg(1) int size) {
		this.capacity = size + 1;
		this.input = inputStream;
		this.buffer = new byte[this.capacity];
		this.executingThread = new Thread(this);
		this.executingThread.setDaemon(true);
		this.executingThread.start();
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(I)V")
	public void shutdown() {
		synchronized (this) {
			if (this.readError == null) {
				this.readError = new IOException("");
			}
			this.notifyAll();
		}
		try {
			this.executingThread.join();
		} catch (@Pc(34) InterruptedException ex) {
		}
	}

	@OriginalMember(owner = "client!gha", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (true) {
			@Pc(33) int length;
			synchronized (this) {
				while (true) {
					if (this.readError != null) {
						return;
					}
					if (this.readPosition == 0) {
						length = this.capacity - this.bufferPointer - 1;
					} else if (this.readPosition < this.bufferPointer) {
						length = this.capacity - this.bufferPointer;
					} else {
						length = this.readPosition - this.bufferPointer - 1;
					}
					if (length > 0) {
						break;
					}
					try {
						this.wait();
					} catch (@Pc(63) InterruptedException ex) {
					}
				}
			}
			@Pc(80) int bytesRead;
			try {
				bytesRead = this.input.read(this.buffer, this.bufferPointer, length);
				if (bytesRead == -1) {
					throw new EOFException();
				}
			} catch (@Pc(88) IOException ex2) {
				@Pc(88) IOException local88x = ex2;
				synchronized (this) {
					this.readError = local88x;
					return;
				}
			}
			synchronized (this) {
				this.bufferPointer = (bytesRead + this.bufferPointer) % this.capacity;
			}
		}
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(III[B)I")
	public int read(@OriginalArg(0) int length, @OriginalArg(1) int offset, @OriginalArg(3) byte[] destination) throws IOException {
		if (length < 0 || offset < 0 || offset + length > destination.length) {
			throw new IOException();
		}
		synchronized (this) {
			@Pc(49) int availableBytes;
			if (this.bufferPointer >= this.readPosition) {
				availableBytes = this.bufferPointer - this.readPosition;
			} else {
				availableBytes = this.capacity + this.bufferPointer - this.readPosition;
			}
			if (length > availableBytes) {
				length = availableBytes;
			}
			if (length == 0 && this.readError != null) {
				throw new IOException(this.readError.toString());
			}
			if (this.capacity < length + this.readPosition) {
				@Pc(97) int tailLength = this.capacity - this.readPosition;
				Arrays.copy(this.buffer, this.readPosition, destination, offset, tailLength);
				Arrays.copy(this.buffer, 0, destination, offset + tailLength, length - tailLength);
			} else {
				Arrays.copy(this.buffer, this.readPosition, destination, offset, length);
			}
			this.readPosition = (this.readPosition + length) % this.capacity;
			this.notifyAll();
			return length;
		}
	}

	@OriginalMember(owner = "client!gha", name = "c", descriptor = "(I)V")
	public void drop() {
		this.input = new DroppedInputStream();
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(IZ)Z")
	public boolean available(@OriginalArg(0) int length) throws IOException {
		if (length <= 0 || length >= this.capacity) {
			throw new IOException();
		}
		synchronized (this) {
			@Pc(40) int availableBytes;
			if (this.bufferPointer < this.readPosition) {
				availableBytes = this.capacity + this.bufferPointer - this.readPosition;
			} else {
				availableBytes = this.bufferPointer - this.readPosition;
			}
			if (availableBytes >= length) {
				return true;
			} else if (this.readError == null) {
				return false;
			} else {
				throw new IOException(this.readError.toString());
			}
		}
	}
}
