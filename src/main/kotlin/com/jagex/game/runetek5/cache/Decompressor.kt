package com.jagex.game.runetek5.cache

import java.io.EOFException
import java.io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!nw")
class Decompressor {

	@OriginalMember(owner = "client!nw", name = "c", descriptor = "Lclient!l;")
	private var indexFile: FileOnDisk? = null

	@OriginalMember(owner = "client!nw", name = "h", descriptor = "Lclient!l;")
	private var datFile: FileOnDisk? = null

	@OriginalMember(owner = "client!nw", name = "i", descriptor = "I")
	private var sizeCap: Int = 65000

	@OriginalMember(owner = "client!nw", name = "b", descriptor = "I")
	private val index: Int

	@OriginalMember(owner = "client!nw", name = "<init>", descriptor = "(ILclient!l;Lclient!l;I)V")
	constructor(@OriginalArg(0) index: Int, @OriginalArg(1) bufferedFile: FileOnDisk?, @OriginalArg(2) bufferedFile2: FileOnDisk?, @OriginalArg(3) sizeCap: Int) {
		this.index = index
		this.indexFile = bufferedFile
		this.datFile = bufferedFile2
		this.sizeCap = sizeCap
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(II[BI)Z")
	fun put(@OriginalArg(0) size: Int, @OriginalArg(1) fileId: Int, @OriginalArg(2) data: ByteArray?): Boolean {
		@Pc(11) val bufferedFile: FileOnDisk? = this.indexFile
		synchronized(this.indexFile!!) {
			if (size < 0 || this.sizeCap < size) {
				throw IllegalArgumentException()
			}
			@Pc(38) var successful: Boolean = this.put(data, fileId, size, true)
			if (!successful) {
				successful = this.put(data, fileId, size, false)
			}
			return successful
		}
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(BI)[B")
	fun read(@OriginalArg(1) fileId: Int): ByteArray? {
		@Pc(8) val bufferedFile: FileOnDisk? = this.indexFile
		synchronized(this.indexFile!!) {
			try {
				if (this.datFile!!.getInitialCapacity() < (fileId * 6 + 6).toLong()) {
					return null
				}
				this.datFile!!.seek((fileId * 6).toLong())
				this.datFile!!.read(0, Decompressor.buffer, 6)
				@Pc(71) val dataSize: Int = (Decompressor.buffer[2].toInt() and 0xFF) + ((Decompressor.buffer[1].toInt() and 0xFF) shl 8) + ((Decompressor.buffer[0].toInt() and 0xFF) shl 16)
				@Pc(94) var sector: Int = (Decompressor.buffer[5].toInt() and 0xFF) + ((Decompressor.buffer[3].toInt() and 0xFF) shl 16) + ((Decompressor.buffer[4].toInt() and 0xFF) shl 8)
				if (dataSize < 0 || dataSize > this.sizeCap) {
					return null
				} else if (sector > 0 && sector.toLong() <= this.indexFile!!.getInitialCapacity() / 520L) {
					@Pc(134) val data: ByteArray = ByteArray(dataSize)
					@Pc(136) var bytesRead: Int = 0
					@Pc(147) var chunkIndex: Int = 0
					label55@ while (bytesRead < dataSize) {
						if (sector == 0) {
							return null
						}
						this.indexFile!!.seek((sector * 520).toLong())
						@Pc(173) var chunkSize: Int = dataSize - bytesRead
						if (chunkSize > 512) {
							chunkSize = 512
						}
						this.indexFile!!.read(0, Decompressor.buffer, chunkSize + 8)
						@Pc(203) val storedFileId: Int = ((Decompressor.buffer[0].toInt() and 0xFF) shl 8) + (Decompressor.buffer[1].toInt() and 0xFF)
						@Pc(217) val storedChunkIndex: Int = ((Decompressor.buffer[2].toInt() and 0xFF) shl 8) + (Decompressor.buffer[3].toInt() and 0xFF)
						@Pc(239) val nextSector: Int = ((Decompressor.buffer[5].toInt() and 0xFF) shl 8) + ((Decompressor.buffer[4].toInt() and 0xFF) shl 16) + (Decompressor.buffer[6].toInt() and 0xFF)
						@Pc(245) val storedIndex: Int = Decompressor.buffer[7].toInt() and 0xFF
						if (fileId == storedFileId && chunkIndex == storedChunkIndex && this.index == storedIndex) {
							if (nextSector >= 0 && nextSector.toLong() <= this.indexFile!!.getInitialCapacity() / 520L) {
								sector = nextSector
								chunkIndex++
								@Pc(294) var offset: Int = 0
								while (true) {
									if (offset >= chunkSize) {
										continue@label55
									}
									data[bytesRead++] = Decompressor.buffer[offset + 8]
									offset++
								}
							}
							return null
						}
						return null
					}
					return data
				} else {
					return null
				}
			} catch (@Pc(323) ex: IOException) {
				return null
			}
		}
	}

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "([BIIIZ)Z")
	private fun put(@OriginalArg(0) data: ByteArray?, @OriginalArg(2) fileId: Int, @OriginalArg(3) size: Int, @OriginalArg(4) overwrite: Boolean): Boolean {
		var overwrite: Boolean = overwrite
		@Pc(8) val bufferedFile: FileOnDisk? = this.indexFile
		synchronized(this.indexFile!!) {
			try {
				@Pc(69) var sector: Int
				if (overwrite) {
					if (this.datFile!!.getInitialCapacity() < (fileId * 6 + 6).toLong()) {
						return false
					}
					this.datFile!!.seek((fileId * 6).toLong())
					this.datFile!!.read(0, Decompressor.buffer, 6)
					sector = (Decompressor.buffer[5].toInt() and 0xFF) + ((Decompressor.buffer[3].toInt() and 0xFF) shl 16) + ((Decompressor.buffer[4].toInt() and 0xFF) shl 8)
					if (sector <= 0 || sector.toLong() > this.indexFile!!.getInitialCapacity() / 520L) {
						return false
					}
				} else {
					sector = ((this.indexFile!!.getInitialCapacity() + 519L) / 520L).toInt()
					if (sector == 0) {
						sector = 1
					}
				}
				Decompressor.buffer[0] = (size shr 16).toByte()
				Decompressor.buffer[4] = (sector shr 8).toByte()
				Decompressor.buffer[2] = size.toByte()
				Decompressor.buffer[1] = (size shr 8).toByte()
				Decompressor.buffer[3] = (sector shr 16).toByte()
				Decompressor.buffer[5] = sector.toByte()
				this.datFile!!.seek((fileId * 6).toLong())
				this.datFile!!.write(0, 6, Decompressor.buffer)
				@Pc(168) var bytesWritten: Int = 0
				@Pc(176) var chunkIndex: Int = 0
				while (bytesWritten < size) {
					@Pc(180) var nextSector: Int = 0
					@Pc(215) var local215: Int
					if (overwrite) {
						this.indexFile!!.seek((sector * 520).toLong())
						try {
							this.indexFile!!.read(0, Decompressor.buffer, 8)
						} catch (@Pc(199) ex: EOFException) {
							return true
						}
						val storedFileId: Int = (Decompressor.buffer[1].toInt() and 0xFF) + ((Decompressor.buffer[0].toInt() and 0xFF) shl 8)
						nextSector = ((Decompressor.buffer[5].toInt() and 0xFF) shl 8) + (((Decompressor.buffer[4].toInt() and 0xFF) shl 16) + (Decompressor.buffer[6].toInt() and 0xFF))
						@Pc(252) val storedChunkIndex: Int = ((Decompressor.buffer[2].toInt() and 0xFF) shl 8) + (Decompressor.buffer[3].toInt() and 0xFF)
						@Pc(258) val storedIndex: Int = Decompressor.buffer[7].toInt() and 0xFF
						if (fileId != storedFileId || storedChunkIndex != chunkIndex || storedIndex != this.index) {
							return false
						}
						if (nextSector < 0 || nextSector.toLong() > this.indexFile!!.getInitialCapacity() / 520L) {
							return false
						}
					}
					if (nextSector == 0) {
						overwrite = false
						nextSector = ((this.indexFile!!.getInitialCapacity() + 519L) / 520L).toInt()
						if (nextSector == 0) {
							nextSector++
						}
						if (nextSector == sector) {
							nextSector++
						}
					}
					if (size - bytesWritten <= 512) {
						nextSector = 0
					}
					Decompressor.buffer[3] = chunkIndex.toByte()
					Decompressor.buffer[2] = (chunkIndex shr 8).toByte()
					Decompressor.buffer[0] = (fileId shr 8).toByte()
					Decompressor.buffer[1] = fileId.toByte()
					Decompressor.buffer[5] = (nextSector shr 8).toByte()
					Decompressor.buffer[6] = nextSector.toByte()
					Decompressor.buffer[7] = this.index.toByte()
					Decompressor.buffer[4] = (nextSector shr 16).toByte()
					this.indexFile!!.seek((sector * 520).toLong())
					this.indexFile!!.write(0, 8, Decompressor.buffer)
					var chunkSize: Int = size - bytesWritten
					if (chunkSize > 512) {
						chunkSize = 512
					}
					this.indexFile!!.write(bytesWritten, chunkSize, data)
					chunkIndex++
					bytesWritten += chunkSize
					sector = nextSector
				}
				return true
			} catch (@Pc(437) ex2: IOException) {
				return false
			}
		}
	}

	@OriginalMember(owner = "client!nw", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		return "Cache:" + this.index
	}

	companion object {
		@OriginalMember(owner = "client!su", name = "k", descriptor = "[B")
		@JvmField
		val buffer: ByteArray = ByteArray(520)
	}
}
