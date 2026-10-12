package com.jagex.game.runetek5.cache

import com.jagex.game.runetek5.core.util.Arrays
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!l")
class FileOnDisk {

	@OriginalMember(owner = "client!l", name = "l", descriptor = "J")
	private var fileChannelPosition: Long = 0L

	@OriginalMember(owner = "client!l", name = "q", descriptor = "I")
	private var readBufferValidBytes: Int = 0

	@OriginalMember(owner = "client!l", name = "k", descriptor = "I")
	private var obfuscatedWriteBufferSize: Int = 0

	@OriginalMember(owner = "client!l", name = "p", descriptor = "J")
	private var readBufferFilePosition: Long = -1L

	@OriginalMember(owner = "client!l", name = "r", descriptor = "J")
	private var writeBufferFilePosition: Long = -1L

	@OriginalMember(owner = "client!l", name = "i", descriptor = "Lclient!so;")
	private val fs32: JFS32?

	@OriginalMember(owner = "client!l", name = "n", descriptor = "J")
	private var occupiedSize: Long = 0L

	@OriginalMember(owner = "client!l", name = "t", descriptor = "J")
	private var initialCapacity: Long = 0L

	@OriginalMember(owner = "client!l", name = "b", descriptor = "[B")
	private val writeBuffer: ByteArray

	@OriginalMember(owner = "client!l", name = "h", descriptor = "[B")
	private val readBuffer: ByteArray

	@OriginalMember(owner = "client!l", name = "u", descriptor = "J")
	private var currentPosition: Long = 0L

	@OriginalMember(owner = "client!l", name = "<init>", descriptor = "(Lclient!so;II)V")
	@Throws(IOException::class)
	constructor(@OriginalArg(0) fileOnDisk: JFS32?, @OriginalArg(1) readBufferSize: Int, @OriginalArg(2) writeBufferSize: Int) {
		this.fs32 = fileOnDisk
		this.occupiedSize = fileOnDisk!!.length()
		this.initialCapacity = this.occupiedSize
		this.writeBuffer = ByteArray(writeBufferSize)
		this.readBuffer = ByteArray(readBufferSize)
		this.currentPosition = 0L
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(IIZ[B)V")
	@Throws(IOException::class)
	fun write(@OriginalArg(0) offset: Int, @OriginalArg(1) length: Int, @OriginalArg(3) data: ByteArray?) {
		var offset: Int = offset
		var length: Int = length
		try {
			if (this.initialCapacity < this.currentPosition + length.toLong()) {
				this.initialCapacity = length.toLong() + this.currentPosition
			}
			if (this.writeBufferFilePosition != -1L && (this.currentPosition < this.writeBufferFilePosition || this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition < this.currentPosition)) {
				this.flushWriteBuffer()
			}
			if (this.writeBufferFilePosition != -1L && this.currentPosition + length.toLong() > this.writeBuffer.size.toLong() + this.writeBufferFilePosition) {
				@Pc(92) val spaceAvailable: Int = (this.writeBuffer.size.toLong() + this.writeBufferFilePosition - this.currentPosition).toInt()
				Arrays.copy(data, offset, this.writeBuffer, (this.currentPosition - this.writeBufferFilePosition).toInt(), spaceAvailable)
				offset += spaceAvailable
				length -= spaceAvailable
				this.currentPosition += spaceAvailable.toLong()
				this.obfuscatedWriteBufferSize = this.writeBuffer.size
				this.flushWriteBuffer()
			}
			if (length > this.writeBuffer.size) {
				if (this.fileChannelPosition != this.currentPosition) {
					this.fs32!!.seek(this.currentPosition)
					this.fileChannelPosition = this.currentPosition
				}
				this.fs32!!.write(offset, length, data)
				this.fileChannelPosition += length.toLong()
				if (this.fileChannelPosition > this.occupiedSize) {
					this.occupiedSize = this.fileChannelPosition
				}
				@Pc(183) var overlapStart: Long = -1L
				if (this.currentPosition >= this.readBufferFilePosition && this.currentPosition < this.readBufferFilePosition + this.readBufferValidBytes.toLong()) {
					overlapStart = this.currentPosition
				} else if (this.readBufferFilePosition >= this.currentPosition && this.readBufferFilePosition < this.currentPosition + length.toLong()) {
					overlapStart = this.readBufferFilePosition
				}
				@Pc(237) var overlapEnd: Long = -1L
				if (length.toLong() + this.currentPosition > this.readBufferFilePosition && this.readBufferValidBytes.toLong() + this.readBufferFilePosition >= this.currentPosition + length.toLong()) {
					overlapEnd = length.toLong() + this.currentPosition
				} else if (this.readBufferValidBytes.toLong() + this.readBufferFilePosition > this.currentPosition && length.toLong() + this.currentPosition >= this.readBufferFilePosition + this.readBufferValidBytes.toLong()) {
					overlapEnd = this.readBufferValidBytes.toLong() + this.readBufferFilePosition
				}
				if (overlapStart > -1L && overlapEnd > overlapStart) {
					@Pc(315) val overlapLength: Int = (overlapEnd - overlapStart).toInt()
					Arrays.copy(data, (overlapStart + offset.toLong() - this.currentPosition).toInt(), this.readBuffer, (overlapStart - this.readBufferFilePosition).toInt(), overlapLength)
				}
				this.currentPosition += length.toLong()
			} else if (length > 0) {
				if (this.writeBufferFilePosition == -1L) {
					this.writeBufferFilePosition = this.currentPosition
				}
				Arrays.copy(data, offset, this.writeBuffer, (this.currentPosition - this.writeBufferFilePosition).toInt(), length)
				this.currentPosition += length.toLong()
				if (this.currentPosition - this.writeBufferFilePosition > this.obfuscatedWriteBufferSize.toLong()) {
					this.obfuscatedWriteBufferSize = (this.currentPosition - this.writeBufferFilePosition).toInt()
				}
			}
		} catch (@Pc(399) ex: IOException) {
			this.fileChannelPosition = -1L
			throw ex
		}
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I[B)V")
	@Throws(IOException::class)
	fun read(@OriginalArg(1) data: ByteArray?) {
		this.read(0, data, data!!.size)
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I)Ljava/io/File;")
	private fun getFile(): File? {
		return this.fs32!!.getSource()
	}

	@OriginalMember(owner = "client!l", name = "b", descriptor = "(I)J")
	fun getInitialCapacity(): Long {
		return this.initialCapacity
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(I[BII)V")
	@Throws(IOException::class)
	fun read(@OriginalArg(0) offset: Int, @OriginalArg(1) data: ByteArray?, @OriginalArg(2) length: Int) {
		var offset: Int = offset
		var length: Int = length
		try {
			if (length > data!!.size) {
				throw ArrayIndexOutOfBoundsException(length - data.size)
			}
			if (this.writeBufferFilePosition != -1L && this.writeBufferFilePosition <= this.currentPosition && this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition >= this.currentPosition - -(length.toLong())) {
				Arrays.copy(this.writeBuffer, (this.currentPosition - this.writeBufferFilePosition).toInt(), data, 0, length)
				this.currentPosition += length.toLong()
				return
			}
			@Pc(84) val currentPosition: Long = this.currentPosition
			@Pc(88) val requestedLength: Int = length
			@Pc(121) var local121: Int
			if (this.currentPosition >= this.readBufferFilePosition && this.currentPosition < this.readBufferFilePosition + this.readBufferValidBytes.toLong()) {
				var bufferedCount: Int = (this.readBufferValidBytes.toLong() + this.readBufferFilePosition - this.currentPosition).toInt()
				if (length < bufferedCount) {
					bufferedCount = length
				}
				Arrays.copy(this.readBuffer, (this.currentPosition - this.readBufferFilePosition).toInt(), data, 0, bufferedCount)
				this.currentPosition += bufferedCount.toLong()
				length -= bufferedCount
				offset = bufferedCount
			}
			if (length > this.readBuffer.size) {
				this.fs32!!.seek(this.currentPosition)
				this.fileChannelPosition = this.currentPosition
				while (length > 0) {
					val bytesRead: Int = this.fs32.read(data, offset, length)
					if (bytesRead == -1) {
						break
					}
					offset += bytesRead
					this.fileChannelPosition += bytesRead.toLong()
					this.currentPosition += bytesRead.toLong()
					length -= bytesRead
				}
			} else if (length > 0) {
				this.fillReadBuffer()
				var copyCount: Int = length
				if (length > this.readBufferValidBytes) {
					copyCount = this.readBufferValidBytes
				}
				Arrays.copy(this.readBuffer, 0, data, offset, copyCount)
				offset += copyCount
				this.currentPosition += copyCount.toLong()
				length -= copyCount
			}
			if (this.writeBufferFilePosition != -1L) {
				if (this.writeBufferFilePosition > this.currentPosition && length > 0) {
					var zeroFillEnd: Int = (this.writeBufferFilePosition - this.currentPosition).toInt() + offset
					if (zeroFillEnd > offset + length) {
						zeroFillEnd = offset + length
					}
					while (offset < zeroFillEnd) {
						data[offset++] = 0
						length--
						this.currentPosition++
					}
				}
				@Pc(318) var overlapStart: Long = -1L
				if (currentPosition <= this.writeBufferFilePosition && this.writeBufferFilePosition < currentPosition + requestedLength.toLong()) {
					overlapStart = this.writeBufferFilePosition
				} else if (currentPosition >= this.writeBufferFilePosition && this.writeBufferFilePosition + this.obfuscatedWriteBufferSize.toLong() > currentPosition) {
					overlapStart = currentPosition
				}
				@Pc(372) var overlapEnd: Long = -1L
				if (currentPosition < this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition && this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition <= requestedLength.toLong() + currentPosition) {
					overlapEnd = this.writeBufferFilePosition + this.obfuscatedWriteBufferSize.toLong()
				} else if (currentPosition + requestedLength.toLong() > this.writeBufferFilePosition && currentPosition + requestedLength.toLong() <= this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition) {
					overlapEnd = currentPosition + requestedLength.toLong()
				}
				if (overlapStart > -1L && overlapEnd > overlapStart) {
					@Pc(464) val overlapLength: Int = (overlapEnd - overlapStart).toInt()
					Arrays.copy(this.writeBuffer, (overlapStart - this.writeBufferFilePosition).toInt(), data, (overlapStart - currentPosition).toInt(), overlapLength)
					if (this.currentPosition < overlapEnd) {
						length = (length.toLong() + this.currentPosition - overlapEnd).toInt()
						this.currentPosition = overlapEnd
					}
				}
			}
		} catch (@Pc(506) ex: IOException) {
			this.fileChannelPosition = -1L
			throw ex
		}
		if (length > 0) {
			throw EOFException()
		}
	}

	@OriginalMember(owner = "client!l", name = "a", descriptor = "(JB)V")
	@Throws(IOException::class)
	fun seek(@OriginalArg(0) currentPosition: Long) {
		if (currentPosition < 0L) {
			throw IOException("Invalid seek to " + currentPosition + " in file " + this.getFile())
		}
		this.currentPosition = currentPosition
	}

	@OriginalMember(owner = "client!l", name = "b", descriptor = "(B)V")
	@Throws(IOException::class)
	fun close() {
		this.flushWriteBuffer()
		this.fs32!!.close()
	}

	@OriginalMember(owner = "client!l", name = "c", descriptor = "(I)V")
	@Throws(IOException::class)
	private fun flushWriteBuffer() {
		if (this.writeBufferFilePosition == -1L) {
			return
		}
		if (this.fileChannelPosition != this.writeBufferFilePosition) {
			this.fs32!!.seek(this.writeBufferFilePosition)
			this.fileChannelPosition = this.writeBufferFilePosition
		}
		this.fs32!!.write(0, this.obfuscatedWriteBufferSize, this.writeBuffer)
		this.fileChannelPosition += this.obfuscatedWriteBufferSize.toLong()
		if (this.occupiedSize < this.fileChannelPosition) {
			this.occupiedSize = this.fileChannelPosition
		}
		@Pc(59) var overlapStart: Long = -1L
		@Pc(61) var overlapEnd: Long = -1L
		if (this.writeBufferFilePosition >= this.readBufferFilePosition && this.readBufferValidBytes.toLong() + this.readBufferFilePosition > this.writeBufferFilePosition) {
			overlapStart = this.writeBufferFilePosition
		} else if (this.writeBufferFilePosition <= this.readBufferFilePosition && this.writeBufferFilePosition + this.obfuscatedWriteBufferSize.toLong() > this.readBufferFilePosition) {
			overlapStart = this.readBufferFilePosition
		}
		if (this.readBufferFilePosition < this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition && this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition <= this.readBufferValidBytes.toLong() + this.readBufferFilePosition) {
			overlapEnd = this.writeBufferFilePosition + this.obfuscatedWriteBufferSize.toLong()
		} else if (this.readBufferFilePosition + this.readBufferValidBytes.toLong() > this.writeBufferFilePosition && this.readBufferFilePosition + this.readBufferValidBytes.toLong() <= this.obfuscatedWriteBufferSize.toLong() + this.writeBufferFilePosition) {
			overlapEnd = this.readBufferValidBytes.toLong() + this.readBufferFilePosition
		}
		if (overlapStart > -1L && overlapStart < overlapEnd) {
			@Pc(207) val overlapLength: Int = (overlapEnd - overlapStart).toInt()
			Arrays.copy(this.writeBuffer, (overlapStart - this.writeBufferFilePosition).toInt(), this.readBuffer, (overlapStart - this.readBufferFilePosition).toInt(), overlapLength)
		}
		this.writeBufferFilePosition = -1L
		this.obfuscatedWriteBufferSize = 0
	}

	@OriginalMember(owner = "client!l", name = "c", descriptor = "(B)V")
	@Throws(IOException::class)
	private fun fillReadBuffer() {
		this.readBufferValidBytes = 0
		if (this.fileChannelPosition != this.currentPosition) {
			this.fs32!!.seek(this.currentPosition)
			this.fileChannelPosition = this.currentPosition
		}
		this.readBufferFilePosition = this.currentPosition
		while (this.readBuffer.size > this.readBufferValidBytes) {
			@Pc(45) var readLength: Int = this.readBuffer.size - this.readBufferValidBytes
			if (readLength > 200000000) {
				readLength = 200000000
			}
			@Pc(60) val bytesRead: Int = this.fs32!!.read(this.readBuffer, this.readBufferValidBytes, readLength)
			if (bytesRead == -1) {
				break
			}
			this.readBufferValidBytes += bytesRead
			this.fileChannelPosition += bytesRead.toLong()
		}
	}

	companion object {
		@OriginalMember(owner = "client!gca", name = "a", descriptor = "([BBILjava/io/File;)V")
		@JvmStatic
		@Throws(IOException::class)
		fun readFile(@OriginalArg(0) buffer: ByteArray?, @OriginalArg(2) length: Int, @OriginalArg(3) file: File?) {
			@Pc(14) val dataInputStream: DataInputStream = DataInputStream(BufferedInputStream(FileInputStream(file)))
			try {
				dataInputStream.readFully(buffer, 0, length)
			} catch (@Pc(29) ex: EOFException) {
			}
			dataInputStream.close()
		}

		@OriginalMember(owner = "client!de", name = "a", descriptor = "(ILjava/io/File;I)[B")
		@JvmStatic
		fun readFileBytes(@OriginalArg(0) length: Int, @OriginalArg(1) file: File?): ByteArray? {
			try {
				@Pc(14) val data: ByteArray = ByteArray(length)
				FileOnDisk.readFile(data, length, file)
				return data
			} catch (@Pc(22) ex: IOException) {
				return null
			}
		}
	}
}
