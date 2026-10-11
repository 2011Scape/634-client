package com.jagex.game.runetek5.js5;

import com.jagex.game.runetek5.core.constants.CompressionType;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.core.io.BufferedSocket;
import com.jagex.game.runetek5.buffer.Packet;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.js5.request.remote.Js5NetQueueRequest;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!tha")
public final class Js5WorkerThread {

	@OriginalMember(owner = "client!tha", name = "w", descriptor = "J")
	private long lastAttempt;

	@OriginalMember(owner = "client!tha", name = "y", descriptor = "Lclient!re;")
	private BufferedSocket socket;

	@OriginalMember(owner = "client!tha", name = "z", descriptor = "I")
	private int latency;

	@OriginalMember(owner = "client!tha", name = "E", descriptor = "Lclient!mba;")
	private Js5NetQueueRequest current;

	@OriginalMember(owner = "client!tha", name = "m", descriptor = "Lclient!mn;")
	private final Queue pendingUrgentRequests = new Queue();

	@OriginalMember(owner = "client!tha", name = "t", descriptor = "Lclient!mn;")
	private final Queue inFlightUrgentRequests = new Queue();

	@OriginalMember(owner = "client!tha", name = "u", descriptor = "Lclient!mn;")
	private final Queue prefetchRequests = new Queue();

	@OriginalMember(owner = "client!tha", name = "v", descriptor = "Lclient!mn;")
	private final Queue pendingPrefetchRequests = new Queue();

	@OriginalMember(owner = "client!tha", name = "x", descriptor = "Lclient!cea;")
	private final Packet write = new Packet(4);

	@OriginalMember(owner = "client!tha", name = "B", descriptor = "B")
	private byte xorCode = 0;

	@OriginalMember(owner = "client!tha", name = "C", descriptor = "I")
	public volatile int errors = 0;

	@OriginalMember(owner = "client!tha", name = "D", descriptor = "I")
	public volatile int response = 0;

	@OriginalMember(owner = "client!tha", name = "A", descriptor = "Lclient!cea;")
	private final Packet read = new Packet(8);

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(IZ)V")
	public void writeLoggedIn(@OriginalArg(1) boolean loggedIn) {
		if (this.socket == null) {
			return;
		}
		try {
			this.write.currentPosition = 0;
			this.write.writeByte(loggedIn ? 2 : 3);
			this.write.writeMedium(0);
			this.socket.write(this.write.buffer, 4);
		} catch (@Pc(40) IOException ex) {
			try {
				this.socket.close();
			} catch (@Pc(46) Exception ex2) {
			}
			this.errors++;
			this.socket = null;
			this.response = -2;
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(B)Z")
	public boolean loop() {
		@Pc(14) int local14;
		if (this.socket != null) {
			@Pc(7) long currentTime = Utils.currentTimeMillis();
			int elapsed = (int) (currentTime - this.lastAttempt);
			if (elapsed > 200) {
				elapsed = 200;
			}
			this.lastAttempt = currentTime;
			this.latency += elapsed;
			if (this.latency > 30000) {
				try {
					this.socket.close();
				} catch (@Pc(40) Exception ex) {
				}
				this.socket = null;
			}
		}
		if (this.socket == null) {
			return this.getUrgentRequestCount() == 0 && this.getPrefetchRequestCount() == 0;
		}
		try {
			this.socket.sanityCheck();
			for (@Pc(78) Js5NetQueueRequest js5WorkerRequestMessage = (Js5NetQueueRequest) this.pendingUrgentRequests.first(); js5WorkerRequestMessage != null; js5WorkerRequestMessage = (Js5NetQueueRequest) this.pendingUrgentRequests.next()) {
				this.write.currentPosition = 0;
				this.write.writeByte(Js5RequestCode.URGENT);
				this.write.writeMedium((int) js5WorkerRequestMessage.indexReference);
				this.socket.write(this.write.buffer, 4);
				this.inFlightUrgentRequests.insert(js5WorkerRequestMessage);
			}
			for (@Pc(124) Js5NetQueueRequest js5WorkerRequestMessage2 = (Js5NetQueueRequest) this.prefetchRequests.first(); js5WorkerRequestMessage2 != null; js5WorkerRequestMessage2 = (Js5NetQueueRequest) this.prefetchRequests.next()) {
				this.write.currentPosition = 0;
				this.write.writeByte(Js5RequestCode.PREFETCH);
				this.write.writeMedium((int) js5WorkerRequestMessage2.indexReference);
				this.socket.write(this.write.buffer, 4);
				this.pendingPrefetchRequests.insert(js5WorkerRequestMessage2);
			}
			for (int readIteration = 0; readIteration < 100; readIteration++) {
				@Pc(176) int available = this.socket.available();
				if (available < 0) {
					throw new IOException();
				}
				if (available == 0) {
					break;
				}
				this.latency = 0;
				@Pc(192) byte expectedHeaderSize = 0;
				if (this.current == null) {
					expectedHeaderSize = 8;
				} else if (this.current.blockBytesRead == 0) {
					expectedHeaderSize = 1;
				}
				@Pc(224) int local224;
				@Pc(231) int local231;
				@Pc(274) int local274;
				if (expectedHeaderSize <= 0) {
					int streamLength = this.current.stream.buffer.length - this.current.padding;
					int chunkSize = 512 - this.current.blockBytesRead;
					if (streamLength - this.current.stream.currentPosition < chunkSize) {
						chunkSize = streamLength - this.current.stream.currentPosition;
					}
					if (available < chunkSize) {
						chunkSize = available;
					}
					this.socket.readFully(this.current.stream.buffer, this.current.stream.currentPosition, chunkSize);
					if (this.xorCode != 0) {
						for (int byteIndex = 0; byteIndex < chunkSize; byteIndex++) {
							this.current.stream.buffer[byteIndex + this.current.stream.currentPosition] ^= this.xorCode;
						}
					}
					this.current.blockBytesRead += chunkSize;
					this.current.stream.currentPosition += chunkSize;
					if (this.current.stream.currentPosition == streamLength) {
						this.current.unlinkSecondary();
						this.current.waiting = false;
						this.current = null;
					} else if (this.current.blockBytesRead == 512) {
						this.current.blockBytesRead = 0;
					}
				} else {
					int headerBytes = expectedHeaderSize - this.read.currentPosition;
					if (available < headerBytes) {
						headerBytes = available;
					}
					this.socket.readFully(this.read.buffer, this.read.currentPosition, headerBytes);
					if (this.xorCode != 0) {
						for (int byteIndex = 0; byteIndex < headerBytes; byteIndex++) {
							this.read.buffer[byteIndex + this.read.currentPosition] ^= this.xorCode;
						}
					}
					this.read.currentPosition += headerBytes;
					if (this.read.currentPosition >= expectedHeaderSize) {
						if (this.current == null) {
							this.read.currentPosition = 0;
							int archiveId = this.read.readUByte();
							int groupId = this.read.readUShort();
							@Pc(452) int settings = this.read.readUByte();
							@Pc(457) int payloadSize = this.read.readInt();
							@Pc(461) int compression = settings & 0x7F;
							@Pc(472) boolean prefetch = (settings & 0x80) != 0;
							@Pc(479) long indexReference = (long) ((archiveId << 16) + groupId);
							@Pc(489) Js5NetQueueRequest js5WorkerRequestMessage3;
							if (prefetch) {
								for (js5WorkerRequestMessage3 = (Js5NetQueueRequest) this.pendingPrefetchRequests.first(); js5WorkerRequestMessage3 != null && indexReference != js5WorkerRequestMessage3.indexReference; js5WorkerRequestMessage3 = (Js5NetQueueRequest) this.pendingPrefetchRequests.next()) {
								}
							} else {
								for (js5WorkerRequestMessage3 = (Js5NetQueueRequest) this.inFlightUrgentRequests.first(); js5WorkerRequestMessage3 != null && js5WorkerRequestMessage3.indexReference != indexReference; js5WorkerRequestMessage3 = (Js5NetQueueRequest) this.inFlightUrgentRequests.next()) {
								}
							}
							if (js5WorkerRequestMessage3 == null) {
								throw new IOException();
							}
							@Pc(549) int headerSize = compression == CompressionType.NONE ? 5 : 9;
							this.current = js5WorkerRequestMessage3;
							this.current.stream = new Packet(this.current.padding + headerSize + payloadSize);
							this.current.stream.writeByte(compression);
							this.current.stream.writeInt(payloadSize);
							this.read.currentPosition = 0;
							this.current.blockBytesRead = 8;
						} else if (this.current.blockBytesRead != 0) {
							throw new IOException();
						} else if (this.read.buffer[0] == -1) {
							this.current.blockBytesRead = 1;
							this.read.currentPosition = 0;
						} else {
							this.current = null;
						}
					}
				}
			}
			return true;
		} catch (@Pc(629) IOException ex2) {
			try {
				this.socket.close();
			} catch (@Pc(635) Exception ex3) {
			}
			this.response = -2;
			this.errors++;
			this.socket = null;
			return this.getUrgentRequestCount() == 0 && this.getPrefetchRequestCount() == 0;
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(I)I")
	private int getPrefetchRequestCount() {
		return this.prefetchRequests.size() + this.pendingPrefetchRequests.size();
	}

	@OriginalMember(owner = "client!tha", name = "b", descriptor = "(I)V")
	private void writeInitiating() {
		if (this.socket == null) {
			return;
		}
		try {
			this.write.currentPosition = 0;
			this.write.writeByte(6);
			this.write.writeMedium(3);
			this.socket.write(this.write.buffer, 4);
		} catch (@Pc(36) IOException ex) {
			try {
				this.socket.close();
			} catch (@Pc(42) Exception ignored) {
			}
			this.response = -2;
			this.socket = null;
			this.errors++;
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(Z)V")
	public void breakConnection() {
		if (this.socket != null) {
			this.socket.breakConnection();
		}
	}

	@OriginalMember(owner = "client!tha", name = "c", descriptor = "(I)Z")
	public boolean isPrefetchRequestQueueFull() {
		return this.getPrefetchRequestCount() >= 20;
	}

	@OriginalMember(owner = "client!tha", name = "d", descriptor = "(I)Z")
	public boolean isUrgentRequestQueueFull() {
		return this.getUrgentRequestCount() >= 20;
	}

	@OriginalMember(owner = "client!tha", name = "e", descriptor = "(I)V")
	public void quit() {
		if (this.socket != null) {
			this.socket.close();
		}
	}

	@OriginalMember(owner = "client!tha", name = "f", descriptor = "(I)I")
	public int getUrgentRequestCount() {
		return this.pendingUrgentRequests.size() + this.inFlightUrgentRequests.size();
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(ZZLclient!re;)V")
	public void init(@OriginalArg(1) boolean loggedIn, @OriginalArg(2) BufferedSocket bufferedSocket) {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (@Pc(10) Exception ex) {
			}
			this.socket = null;
		}
		this.socket = bufferedSocket;
		this.writeInitiating();
		this.writeLoggedIn(loggedIn);
		this.read.currentPosition = 0;
		this.current = null;
		while (true) {
			@Pc(46) Js5NetQueueRequest js5WorkerRequestMessage = (Js5NetQueueRequest) this.inFlightUrgentRequests.removeFirst();
			if (js5WorkerRequestMessage == null) {
				while (true) {
					js5WorkerRequestMessage = (Js5NetQueueRequest) this.pendingPrefetchRequests.removeFirst();
					if (js5WorkerRequestMessage == null) {
						if (this.xorCode != 0) {
							try {
								this.write.currentPosition = 0;
								this.write.writeByte(Js5RequestCode.ENCRYPTION);
								this.write.writeByte(this.xorCode);
								this.write.writeShort(0);
								this.socket.write(this.write.buffer, 4);
							} catch (@Pc(108) IOException ex2) {
								try {
									this.socket.close();
								} catch (@Pc(114) Exception ex3) {
								}
								this.response = -2;
								this.errors++;
								this.socket = null;
							}
						}
						this.latency = 0;
						this.lastAttempt = Utils.currentTimeMillis();
						return;
					}
					this.prefetchRequests.insert(js5WorkerRequestMessage);
				}
			}
			this.pendingUrgentRequests.insert(js5WorkerRequestMessage);
		}
	}

	@OriginalMember(owner = "client!tha", name = "g", descriptor = "(I)V")
	public void rekey() {
		try {
			this.socket.close();
		} catch (@Pc(5) Exception ex) {
		}
		this.socket = null;
		this.errors++;
		this.xorCode = (byte) (Math.random() * 255.0D + 1.0D);
		this.response = -1;
	}

	@OriginalMember(owner = "client!tha", name = "h", descriptor = "(I)V")
	public void writeTerminate() {
		if (this.socket == null) {
			return;
		}
		try {
			this.write.currentPosition = 0;
			this.write.writeByte(7);
			this.write.writeMedium(0);
			this.socket.write(this.write.buffer, 4);
		} catch (@Pc(34) IOException ex) {
			try {
				this.socket.close();
			} catch (@Pc(40) Exception ex2) {
			}
			this.response = -2;
			this.errors++;
			this.socket = null;
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(BBZII)Lclient!mba;")
	public Js5NetQueueRequest request(@OriginalArg(1) byte padding, @OriginalArg(2) boolean highPriority, @OriginalArg(3) int indexId, @OriginalArg(4) int archiveId) {
		@Pc(19) long indexReference = (long) ((archiveId << 16) + indexId);
		@Pc(23) Js5NetQueueRequest js5WorkerRequestMessage = new Js5NetQueueRequest();
		js5WorkerRequestMessage.highPriority = highPriority;
		js5WorkerRequestMessage.indexReference = indexReference;
		js5WorkerRequestMessage.padding = padding;
		if (highPriority) {
			if (this.getUrgentRequestCount() >= 20) {
				throw new RuntimeException();
			}
			this.pendingUrgentRequests.insert(js5WorkerRequestMessage);
		} else if (this.getPrefetchRequestCount() < 20) {
			this.prefetchRequests.insert(js5WorkerRequestMessage);
		} else {
			throw new RuntimeException();
		}
		return js5WorkerRequestMessage;
	}
}
