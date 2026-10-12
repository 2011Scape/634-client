package com.jagex.game.runetek5.cache

import io.EOFException
import io.File
import io.IOException
import io.RandomAccessFile
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!so")
class JFS32 {

	@OriginalMember(owner = "client!so", name = "a", descriptor = "Ljava/io/RandomAccessFile;")
	private var rwFile: RandomAccessFile? = null

	@OriginalMember(owner = "client!so", name = "b", descriptor = "J")
	private val maxCapacity: Long

	@OriginalMember(owner = "client!so", name = "d", descriptor = "J")
	private var seekPosition: Long = 0L

	@OriginalMember(owner = "client!so", name = "k", descriptor = "Ljava/io/File;")
	private val source: File?

	@OriginalMember(owner = "client!so", name = "<init>", descriptor = "(Ljava/io/File;Ljava/lang/String;J)V")
	@Throws(IOException::class)
	constructor(@OriginalArg(0) file2: File?, @OriginalArg(1) string: String?, @OriginalArg(2) maxCapacity: Long) {
		var maxCapacity: Long = maxCapacity
		if (maxCapacity == -1L) {
			maxCapacity = Long.MAX_VALUE
		}
		if (maxCapacity < file2!!.length()) {
			file2.delete()
		}
		this.rwFile = RandomAccessFile(file2, string)
		this.maxCapacity = maxCapacity
		this.seekPosition = 0L
		this.source = file2
		@Pc(37) val firstByte: Int = this.rwFile!!.read()
		if (firstByte != -1 && !string!!.equals("r")) {
			this.rwFile!!.seek(0L)
			this.rwFile!!.write(firstByte)
		}
		this.rwFile!!.seek(0L)
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "([BIBI)I")
	@Throws(IOException::class)
	fun read(@OriginalArg(0) data: ByteArray?, @OriginalArg(1) offset: Int, @OriginalArg(3) length: Int): Int {
		@Pc(10) val bytesRead: Int = this.rwFile!!.read(data, offset, length)
		if (bytesRead > 0) {
			this.seekPosition += bytesRead.toLong()
		}
		return bytesRead
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(Z)V")
	@Throws(IOException::class)
	fun close() {
		if (this.rwFile != null) {
			this.rwFile!!.close()
			this.rwFile = null
		}
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(BII[B)V")
	@Throws(IOException::class)
	fun write(@OriginalArg(1) offset: Int, @OriginalArg(2) length: Int, @OriginalArg(3) data: ByteArray?) {
		if (length.toLong() + this.seekPosition > this.maxCapacity) {
			this.rwFile!!.seek(this.maxCapacity)
			this.rwFile!!.write(1)
			throw EOFException()
		} else {
			this.rwFile!!.write(data, offset, length)
			this.seekPosition += length.toLong()
		}
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(I)Ljava/io/File;")
	fun getSource(): File? {
		return this.source
	}

	@OriginalMember(owner = "client!so", name = "a", descriptor = "(IJ)V")
	@Throws(IOException::class)
	fun seek(@OriginalArg(1) seekPosition: Long) {
		this.rwFile!!.seek(seekPosition)
		this.seekPosition = seekPosition
	}

	@OriginalMember(owner = "client!so", name = "b", descriptor = "(B)J")
	@Throws(IOException::class)
	fun length(): Long {
		return this.rwFile!!.length()
	}

	@OriginalMember(owner = "client!so", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	public fun finalize() {
		if (this.rwFile != null) {
			println("Warning! fileondisk " + this.source + " not closed correctly using close(). Auto-closing instead. ")
			this.close()
		}
	}
}
