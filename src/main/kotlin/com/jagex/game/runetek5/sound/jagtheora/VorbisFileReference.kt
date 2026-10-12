package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import java.io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!qc")
class VorbisFileReference : JagTheoraManager {

	@OriginalMember(owner = "client!qc", name = "M", descriptor = "[I")
	private var pages: IntArray? = null

	@OriginalMember(owner = "client!qc", name = "N", descriptor = "I")
	private var currentChunkIndex: Int = 0

	@OriginalMember(owner = "client!qc", name = "S", descriptor = "[[B")
	private val chunkBuffers: Array<ByteArray?> = arrayOfNulls<ByteArray>(10)

	@OriginalMember(owner = "client!qc", name = "R", descriptor = "Lclient!cea;")
	private val currentChunkPacket: Packet = Packet(null as ByteArray?)

	@OriginalMember(owner = "client!qc", name = "T", descriptor = "Lclient!cea;")
	private val byteBuffer: Packet = Packet(null as ByteArray?)

	@OriginalMember(owner = "client!qc", name = "K", descriptor = "Lclient!in;")
	private val resourceProvider: Js5ResourceProvider?

	@OriginalMember(owner = "client!qc", name = "P", descriptor = "I")
	private val fileId: Int

	@OriginalMember(owner = "client!qc", name = "<init>", descriptor = "(ILclient!in;I)V")
	constructor(@OriginalArg(0) bufferSize: Int, @OriginalArg(1) resourceProvider: Js5ResourceProvider?, @OriginalArg(2) fileId: Int) : super(bufferSize) {
		this.resourceProvider = resourceProvider
		this.fileId = fileId
	}

	@OriginalMember(owner = "client!qc", name = "c", descriptor = "(B)V")
	fun readNextPages() {
		if (this.pages == null) {
			return
		}
		for (@Pc(15) i in 0 until 10) {
			if (this.pages!!.size <= this.currentChunkIndex + i) {
				return
			}
			if (this.chunkBuffers[i] == null && this.resourceProvider!!.isFileReady(this.pages!![i + this.currentChunkIndex], 0)) {
				this.chunkBuffers[i] = this.resourceProvider.getFile(this.pages!![this.currentChunkIndex + i], 0)
			}
		}
	}

	@OriginalMember(owner = "client!qc", name = "a", descriptor = "(B[B)I")
	@Throws(IOException::class)
	override fun readPage(@OriginalArg(1) buffer: ByteArray?): Int {
		@Pc(46) val local46: Int
		@Pc(52) var i: Int
		if (this.pages == null) {
			if (!this.resourceProvider!!.isFileReady(this.fileId, 0)) {
				return 0
			}
			@Pc(26) val data: ByteArray? = this.resourceProvider.getFile(this.fileId, 0)
			if (data == null) {
				throw IllegalStateException("")
			}
			this.byteBuffer.buffer = data
			this.byteBuffer.currentPosition = 0
			val pageCount: Int = data.size shr 1
			this.pages = IntArray(pageCount)
			i = 0
			while (i < pageCount) {
				this.pages!![i] = this.byteBuffer.readUShort()
				i++
			}
		}
		if (this.currentChunkIndex >= this.pages!!.size) {
			return -1
		}
		this.readNextPages()
		this.byteBuffer.buffer = buffer
		this.byteBuffer.currentPosition = 0
		do {
			if (this.byteBuffer.buffer!!.size <= this.byteBuffer.currentPosition) {
				this.byteBuffer.buffer = null
				return buffer!!.size
			}
			if (this.currentChunkPacket.buffer == null) {
				if (this.chunkBuffers[0] == null) {
					this.byteBuffer.buffer = null
					return this.byteBuffer.currentPosition
				}
				this.currentChunkPacket.buffer = this.chunkBuffers[0]
			}
			@Pc(131) val availableBuffer: Int = this.byteBuffer.buffer!!.size - this.byteBuffer.currentPosition
			val chunkRemaining: Int = this.currentChunkPacket.buffer!!.size - this.currentChunkPacket.currentPosition
			if (availableBuffer < chunkRemaining) {
				this.currentChunkPacket.readBytes(this.byteBuffer.currentPosition, availableBuffer, this.byteBuffer.buffer)
				this.byteBuffer.buffer = null
				return buffer!!.size
			}
			this.byteBuffer.writeBytes(chunkRemaining, this.currentChunkPacket.currentPosition, this.currentChunkPacket.buffer)
			this.currentChunkPacket.currentPosition = 0
			this.currentChunkPacket.buffer = null
			this.currentChunkIndex++
			i = 0
			while (i < 9) {
				this.chunkBuffers[i] = this.chunkBuffers[i + 1]
				i++
			}
			this.chunkBuffers[9] = null
		} while (this.currentChunkIndex < this.pages!!.size)
		this.byteBuffer.buffer = null
		return this.byteBuffer.currentPosition
	}
}
