package com.jagex.game.runetek5.cache;

import com.jagex.game.runetek5.cache.JFS32;
import com.jagex.game.runetek5.core.util.Arrays;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

@OriginalClass("client!l")
public final class FileOnDisk {

	@OriginalMember(owner = "client!l", name = "l", descriptor = "J")
	private long fileChannelPosition;

	@OriginalMember(owner = "client!l", name = "q", descriptor = "I")
	private int readBufferValidBytes;

	@OriginalMember(owner = "client!l", name = "k", descriptor = "I")
	private int obfuscatedWriteBufferSize = 0;

	@OriginalMember(owner = "client!l", name = "p", descriptor = "J")
	private long readBufferFilePosition = -1L;

	@OriginalMember(owner = "client!l", name = "r", descriptor = "J")
	private long writeBufferFilePosition = -1L;

	@OriginalMember(owner = "client!l", name = "i", descriptor = "Lclient!so;")
	private final JFS32 fs32;

	@OriginalMember(owner = "client!l", name = "n", descriptor = "J")
	private long occupiedSize;

	@OriginalMember(owner = "client!l", name = "t", descriptor = "J")
	private long initialCapacity;

	@OriginalMember(owner = "client!l", name = "b", descriptor = "[B")
	private final byte[] writeBuffer;

	@OriginalMember(owner = "client!l", name = "h", descriptor = "[B")
	private final byte[] readBuffer;

	@OriginalMember(owner = "client!l", name = "u", descriptor = "J")
	private long currentPosition;

	@OriginalMember(owner = "client!l", name = "<init>", descriptor = "(Lclient!so;II)V")
	public FileOnDisk(@OriginalArg(0) JFS32 fileOnDisk, @OriginalArg(1) int readBufferSize, @OriginalArg(2) int writeBufferSize) throws IOException {
		this.fs32 = fileOnDisk;
		this.initialCapacity = this.occupiedSize = fileOnDisk.length();
		this.writeBuffer = new byte[writeBufferSize];
		this.readBuffer = new byte[readBufferSize];
		this.currentPosition = 0L;
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(IIZ[B)V")
	public void write(@OriginalArg(0) int offset, @OriginalArg(1) int length, @OriginalArg(3) byte[] data) throws IOException {
		try {
			if (this.initialCapacity < this.currentPosition + (long) length) {
				this.initialCapacity = (long) length + this.currentPosition;
			}
			if (this.writeBufferFilePosition != -1L && (this.currentPosition < this.writeBufferFilePosition || (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition < this.currentPosition)) {
				this.flushWriteBuffer();
			}
			if (this.writeBufferFilePosition != -1L && this.currentPosition + (long) length > (long) this.writeBuffer.length + this.writeBufferFilePosition) {
				@Pc(92) int spaceAvailable = (int) ((long) this.writeBuffer.length + this.writeBufferFilePosition - this.currentPosition);
				Arrays.copy(data, offset, this.writeBuffer, (int) (this.currentPosition - this.writeBufferFilePosition), spaceAvailable);
				offset += spaceAvailable;
				length -= spaceAvailable;
				this.currentPosition += spaceAvailable;
				this.obfuscatedWriteBufferSize = this.writeBuffer.length;
				this.flushWriteBuffer();
			}
			if (length > this.writeBuffer.length) {
				if (this.fileChannelPosition != this.currentPosition) {
					this.fs32.seek(this.currentPosition);
					this.fileChannelPosition = this.currentPosition;
				}
				this.fs32.write(offset, length, data);
				this.fileChannelPosition += length;
				if (this.fileChannelPosition > this.occupiedSize) {
					this.occupiedSize = this.fileChannelPosition;
				}
				@Pc(183) long overlapStart = -1L;
				if (this.currentPosition >= this.readBufferFilePosition && this.currentPosition < this.readBufferFilePosition + (long) this.readBufferValidBytes) {
					overlapStart = this.currentPosition;
				} else if (this.readBufferFilePosition >= this.currentPosition && this.readBufferFilePosition < this.currentPosition + (long) length) {
					overlapStart = this.readBufferFilePosition;
				}
				@Pc(237) long overlapEnd = -1L;
				if ((long) length + this.currentPosition > this.readBufferFilePosition && (long) this.readBufferValidBytes + this.readBufferFilePosition >= this.currentPosition + (long) length) {
					overlapEnd = (long) length + this.currentPosition;
				} else if ((long) this.readBufferValidBytes + this.readBufferFilePosition > this.currentPosition && (long) length + this.currentPosition >= this.readBufferFilePosition + (long) this.readBufferValidBytes) {
					overlapEnd = (long) this.readBufferValidBytes + this.readBufferFilePosition;
				}
				if (overlapStart > -1L && overlapEnd > overlapStart) {
					@Pc(315) int overlapLength = (int) (overlapEnd - overlapStart);
					Arrays.copy(data, (int) (overlapStart + (long) offset - this.currentPosition), this.readBuffer, (int) (overlapStart - this.readBufferFilePosition), overlapLength);
				}
				this.currentPosition += length;
			} else if (length > 0) {
				if (this.writeBufferFilePosition == -1L) {
					this.writeBufferFilePosition = this.currentPosition;
				}
				Arrays.copy(data, offset, this.writeBuffer, (int) (this.currentPosition - this.writeBufferFilePosition), length);
				this.currentPosition += length;
				if (this.currentPosition - this.writeBufferFilePosition > (long) this.obfuscatedWriteBufferSize) {
					this.obfuscatedWriteBufferSize = (int) (this.currentPosition - this.writeBufferFilePosition);
				}
			}
		} catch (@Pc(399) IOException ex) {
			this.fileChannelPosition = -1L;
			throw ex;
		}
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I[B)V")
	public void read(@OriginalArg(1) byte[] data) throws IOException {
		this.read(0, data, data.length);
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I)Ljava/io/File;")
	private File getFile() {
		return this.fs32.getSource();
	}

	@OriginalMember(owner = "client!l", name = "b", descriptor = "(I)J")
	public long getInitialCapacity() {
		return this.initialCapacity;
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I[BII)V")
	public void read(@OriginalArg(0) int offset, @OriginalArg(1) byte[] data, @OriginalArg(2) int length) throws IOException {
		try {
			if (length > data.length) {
				throw new ArrayIndexOutOfBoundsException(length - data.length);
			}
			if (this.writeBufferFilePosition != -1L && this.writeBufferFilePosition <= this.currentPosition && (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition >= this.currentPosition - -((long) length)) {
				Arrays.copy(this.writeBuffer, (int) (this.currentPosition - this.writeBufferFilePosition), data, 0, length);
				this.currentPosition += length;
				return;
			}
			@Pc(84) long currentPosition = this.currentPosition;
			@Pc(88) int requestedLength = length;
			@Pc(121) int local121;
			if (this.currentPosition >= this.readBufferFilePosition && this.currentPosition < this.readBufferFilePosition + (long) this.readBufferValidBytes) {
				int bufferedCount = (int) ((long) this.readBufferValidBytes + this.readBufferFilePosition - this.currentPosition);
				if (length < bufferedCount) {
					bufferedCount = length;
				}
				Arrays.copy(this.readBuffer, (int) (this.currentPosition - this.readBufferFilePosition), data, 0, bufferedCount);
				this.currentPosition += bufferedCount;
				length -= bufferedCount;
				offset = bufferedCount;
			}
			if (length > this.readBuffer.length) {
				this.fs32.seek(this.currentPosition);
				this.fileChannelPosition = this.currentPosition;
				while (length > 0) {
					int bytesRead = this.fs32.read(data, offset, length);
					if (bytesRead == -1) {
						break;
					}
					offset += bytesRead;
					this.fileChannelPosition += bytesRead;
					this.currentPosition += bytesRead;
					length -= bytesRead;
				}
			} else if (length > 0) {
				this.fillReadBuffer();
				int copyCount = length;
				if (length > this.readBufferValidBytes) {
					copyCount = this.readBufferValidBytes;
				}
				Arrays.copy(this.readBuffer, 0, data, offset, copyCount);
				offset += copyCount;
				this.currentPosition += copyCount;
				length -= copyCount;
			}
			if (this.writeBufferFilePosition != -1L) {
				if (this.writeBufferFilePosition > this.currentPosition && length > 0) {
					int zeroFillEnd = (int) (this.writeBufferFilePosition - this.currentPosition) + offset;
					if (zeroFillEnd > offset + length) {
						zeroFillEnd = offset + length;
					}
					while (offset < zeroFillEnd) {
						data[offset++] = 0;
						length--;
						this.currentPosition++;
					}
				}
				@Pc(318) long overlapStart = -1L;
				if (currentPosition <= this.writeBufferFilePosition && this.writeBufferFilePosition < currentPosition + (long) requestedLength) {
					overlapStart = this.writeBufferFilePosition;
				} else if (currentPosition >= this.writeBufferFilePosition && this.writeBufferFilePosition + (long) this.obfuscatedWriteBufferSize > currentPosition) {
					overlapStart = currentPosition;
				}
				@Pc(372) long overlapEnd = -1L;
				if (currentPosition < (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition && (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition <= (long) requestedLength + currentPosition) {
					overlapEnd = this.writeBufferFilePosition + (long) this.obfuscatedWriteBufferSize;
				} else if (currentPosition + (long) requestedLength > this.writeBufferFilePosition && currentPosition + (long) requestedLength <= (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition) {
					overlapEnd = currentPosition + (long) requestedLength;
				}
				if (overlapStart > -1L && overlapEnd > overlapStart) {
					@Pc(464) int overlapLength = (int) (overlapEnd - overlapStart);
					Arrays.copy(this.writeBuffer, (int) (overlapStart - this.writeBufferFilePosition), data, (int) (overlapStart - currentPosition), overlapLength);
					if (this.currentPosition < overlapEnd) {
						length = (int) ((long) length + this.currentPosition - overlapEnd);
						this.currentPosition = overlapEnd;
					}
				}
			}
		} catch (@Pc(506) IOException ex) {
			this.fileChannelPosition = -1L;
			throw ex;
		}
		if (length > 0) {
			throw new EOFException();
		}
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(JB)V")
	public void seek(@OriginalArg(0) long currentPosition) throws IOException {
		if (currentPosition < 0L) {
			throw new IOException("Invalid seek to " + currentPosition + " in file " + this.getFile());
		}
		this.currentPosition = currentPosition;
	}

	@OriginalMember(owner = "client!l", name = "b", descriptor = "(B)V")
	public void close() throws IOException {
		this.flushWriteBuffer();
		this.fs32.close();
	}

	@OriginalMember(owner = "client!l", name = "c", descriptor = "(I)V")
	private void flushWriteBuffer() throws IOException {
		if (this.writeBufferFilePosition == -1L) {
			return;
		}
		if (this.fileChannelPosition != this.writeBufferFilePosition) {
			this.fs32.seek(this.writeBufferFilePosition);
			this.fileChannelPosition = this.writeBufferFilePosition;
		}
		this.fs32.write(0, this.obfuscatedWriteBufferSize, this.writeBuffer);
		this.fileChannelPosition += this.obfuscatedWriteBufferSize;
		if (this.occupiedSize < this.fileChannelPosition) {
			this.occupiedSize = this.fileChannelPosition;
		}
		@Pc(59) long overlapStart = -1L;
		@Pc(61) long overlapEnd = -1L;
		if (this.writeBufferFilePosition >= this.readBufferFilePosition && (long) this.readBufferValidBytes + this.readBufferFilePosition > this.writeBufferFilePosition) {
			overlapStart = this.writeBufferFilePosition;
		} else if (this.writeBufferFilePosition <= this.readBufferFilePosition && this.writeBufferFilePosition + (long) this.obfuscatedWriteBufferSize > this.readBufferFilePosition) {
			overlapStart = this.readBufferFilePosition;
		}
		if (this.readBufferFilePosition < (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition && (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition <= (long) this.readBufferValidBytes + this.readBufferFilePosition) {
			overlapEnd = this.writeBufferFilePosition + (long) this.obfuscatedWriteBufferSize;
		} else if (this.readBufferFilePosition + (long) this.readBufferValidBytes > this.writeBufferFilePosition && this.readBufferFilePosition + (long) this.readBufferValidBytes <= (long) this.obfuscatedWriteBufferSize + this.writeBufferFilePosition) {
			overlapEnd = (long) this.readBufferValidBytes + this.readBufferFilePosition;
		}
		if (overlapStart > -1L && overlapStart < overlapEnd) {
			@Pc(207) int overlapLength = (int) (overlapEnd - overlapStart);
			Arrays.copy(this.writeBuffer, (int) (overlapStart - this.writeBufferFilePosition), this.readBuffer, (int) (overlapStart - this.readBufferFilePosition), overlapLength);
		}
		this.writeBufferFilePosition = -1L;
		this.obfuscatedWriteBufferSize = 0;
	}

	@OriginalMember(owner = "client!l", name = "c", descriptor = "(B)V")
	private void fillReadBuffer() throws IOException {
		this.readBufferValidBytes = 0;
		if (this.fileChannelPosition != this.currentPosition) {
			this.fs32.seek(this.currentPosition);
			this.fileChannelPosition = this.currentPosition;
		}
		this.readBufferFilePosition = this.currentPosition;
		while (this.readBuffer.length > this.readBufferValidBytes) {
			@Pc(45) int readLength = this.readBuffer.length - this.readBufferValidBytes;
			if (readLength > 200000000) {
				readLength = 200000000;
			}
			@Pc(60) int bytesRead = this.fs32.read(this.readBuffer, this.readBufferValidBytes, readLength);
			if (bytesRead == -1) {
				break;
			}
			this.readBufferValidBytes += bytesRead;
			this.fileChannelPosition += bytesRead;
		}
	}
	@OriginalMember(owner = "client!gca", name = "a", descriptor = "([BBILjava/io/File;)V")
	public static void readFile(@OriginalArg(0) byte[] buffer, @OriginalArg(2) int length, @OriginalArg(3) File file) throws IOException {
		@Pc(14) DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		try {
			dataInputStream.readFully(buffer, 0, length);
		} catch (@Pc(29) EOFException ex) {
		}
		dataInputStream.close();
	}
	@OriginalMember(owner = "client!de", name = "a", descriptor = "(ILjava/io/File;I)[B")
	public static byte[] readFileBytes(@OriginalArg(0) int length, @OriginalArg(1) File file) {
		try {
			@Pc(14) byte[] data = new byte[length];
			FileOnDisk.readFile(data, length, file);
			return data;
		} catch (@Pc(22) IOException ex) {
			return null;
		}
	}
}
