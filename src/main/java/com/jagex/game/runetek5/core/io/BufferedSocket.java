package com.jagex.game.runetek5.core.io;

import com.jagex.game.runetek5.core.io.DummyOutputStream;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.TimeUtils;

@OriginalClass("client!re")
public final class BufferedSocket implements Runnable {

	@OriginalMember(owner = "client!re", name = "m", descriptor = "Lclient!of;")
	private SignedResource threadReference;

	@OriginalMember(owner = "client!re", name = "r", descriptor = "[B")
	private byte[] writeBuffer;

	@OriginalMember(owner = "client!re", name = "c", descriptor = "I")
	private int bufPos = 0;

	@OriginalMember(owner = "client!re", name = "i", descriptor = "Z")
	private boolean closed = false;

	@OriginalMember(owner = "client!re", name = "k", descriptor = "I")
	private int writePos = 0;

	@OriginalMember(owner = "client!re", name = "n", descriptor = "Z")
	private boolean error = false;

	@OriginalMember(owner = "client!re", name = "w", descriptor = "Ljava/net/Socket;")
	private final Socket socket;

	@OriginalMember(owner = "client!re", name = "d", descriptor = "Lclient!wea;")
	private final SignLink signlink;

	@OriginalMember(owner = "client!re", name = "g", descriptor = "Ljava/io/InputStream;")
	private InputStream inputStream;

	@OriginalMember(owner = "client!re", name = "l", descriptor = "Ljava/io/OutputStream;")
	private OutputStream outputStream;

	@OriginalMember(owner = "client!re", name = "x", descriptor = "I")
	private final int bufSize;

	@OriginalMember(owner = "client!re", name = "<init>", descriptor = "(Ljava/net/Socket;Lclient!wea;I)V")
	public BufferedSocket(@OriginalArg(0) Socket socket2, @OriginalArg(1) SignLink signLink, @OriginalArg(2) int bufSize) throws IOException {
		this.socket = socket2;
		this.signlink = signLink;
		this.socket.setSoTimeout(30000);
		this.socket.setTcpNoDelay(true);
		this.inputStream = this.socket.getInputStream();
		this.outputStream = this.socket.getOutputStream();
		this.bufSize = bufSize;
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(B)I")
	public int available() throws IOException {
		return this.closed ? 0 : this.inputStream.available();
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(I)V")
	public void breakConnection() {
		if (!this.closed) {
			this.inputStream = new DummyInputStream();
			this.outputStream = new DummyOutputStream();
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "([BIII)V")
	public void write(@OriginalArg(0) byte[] buffer, @OriginalArg(1) int length) throws IOException {
		if (this.closed) {
			return;
		}
		if (this.error) {
			this.error = false;
			throw new IOException();
		}
		if (this.writeBuffer == null) {
			this.writeBuffer = new byte[this.bufSize];
		}
		synchronized (this) {
			for (@Pc(39) int i = 0; i < length; i++) {
				this.writeBuffer[this.bufPos] = buffer[i];
				this.bufPos = (this.bufPos + 1) % this.bufSize;
				if ((this.writePos + this.bufSize - 100) % this.bufSize == this.bufPos) {
					throw new IOException();
				}
			}
			if (this.threadReference == null) {
				this.threadReference = this.signlink.startThread(this, 3);
			}
			this.notifyAll();
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(Z)V")
	public void sanityCheck() throws IOException {
		if (!this.closed && this.error) {
			this.error = false;
			throw new IOException();
		}
	}

	@OriginalMember(owner = "client!re", name = "c", descriptor = "(I)I")
	public int read() throws IOException {
		return this.closed ? 0 : this.inputStream.read();
	}

	@OriginalMember(owner = "client!re", name = "run", descriptor = "()V")
	@Override
	public void run() {
		try {
			while (true) {
				label80: {
					@Pc(35) int len;
					@Pc(24) int off;
					synchronized (this) {
						if (this.bufPos == this.writePos) {
							if (this.closed) {
								break label80;
							}
							try {
								this.wait();
							} catch (@Pc(20) InterruptedException ex) {
							}
						}
						off = this.writePos;
						if (this.writePos > this.bufPos) {
							len = this.bufSize - this.writePos;
						} else {
							len = this.bufPos - this.writePos;
						}
					}
					if (len <= 0) {
						continue;
					}
					try {
						this.outputStream.write(this.writeBuffer, off, len);
					} catch (@Pc(61) IOException ex2) {
						this.error = true;
					}
					this.writePos = (len + this.writePos) % this.bufSize;
					try {
						if (this.bufPos == this.writePos) {
							this.outputStream.flush();
						}
					} catch (@Pc(83) IOException ex3) {
						this.error = true;
					}
					continue;
				}
				try {
					if (this.inputStream != null) {
						this.inputStream.close();
					}
					if (this.outputStream != null) {
						this.outputStream.close();
					}
					if (this.socket != null) {
						this.socket.close();
					}
				} catch (@Pc(107) IOException ex4) {
				}
				this.writeBuffer = null;
				break;
			}
		} catch (@Pc(112) Exception ex5) {
			ExplicitRuntimeException.reportError(null, ex5);
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "([BIBI)V")
	public void readFully(@OriginalArg(0) byte[] buffer, @OriginalArg(1) int offset, @OriginalArg(3) int length) throws IOException {
		if (this.closed) {
			return;
		}
		while (length > 0) {
			@Pc(14) int bytesRead = this.inputStream.read(buffer, offset, length);
			if (bytesRead <= 0) {
				throw new EOFException();
			}
			length -= bytesRead;
			offset += bytesRead;
		}
	}

	@OriginalMember(owner = "client!re", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		this.close();
	}

	@OriginalMember(owner = "client!re", name = "c", descriptor = "(B)V")
	public void close() {
		if (this.closed) {
			return;
		}
		synchronized (this) {
			this.closed = true;
			this.notifyAll();
		}
		if (this.threadReference != null) {
			while (this.threadReference.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(1L);
			}
			if (this.threadReference.status == SignedResourceStatus.SUCCESS) {
				try {
					((Thread) this.threadReference.result).join();
				} catch (@Pc(54) InterruptedException ignored) {
				}
			}
		}
		this.threadReference = null;
	}
}
