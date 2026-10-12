package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.network.Connection
import java.io.IOException
import java.net.Socket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!gea")
class AsyncConnection : Connection {

	@OriginalMember(owner = "client!gea", name = "k", descriptor = "Ljava/net/Socket;")
	private val socket: Socket

	@OriginalMember(owner = "client!gea", name = "i", descriptor = "Lclient!gha;")
	private val inputStream: AsyncInputStream

	@OriginalMember(owner = "client!gea", name = "l", descriptor = "Lclient!rha;")
	private val outputStream: AsyncOutputStream

	@OriginalMember(owner = "client!gea", name = "<init>", descriptor = "(Ljava/net/Socket;I)V")
	@Throws(IOException::class)
	constructor(@OriginalArg(0) socket2: Socket?, @OriginalArg(1) capacity: Int) : super() {
		this.socket = socket2!!
		this.socket.setSoTimeout(30000)
		this.socket.setTcpNoDelay(true)
		this.inputStream = AsyncInputStream(this.socket.getInputStream(), capacity)
		this.outputStream = AsyncOutputStream(this.socket.getOutputStream(), capacity)
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(III[B)V")
	@Throws(IOException::class)
	override fun write(@OriginalArg(2) length: Int, @OriginalArg(3) buffer: ByteArray?) {
		this.outputStream.write(length, buffer)
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(I)V")
	override fun breakConnection() {
		this.inputStream.drop()
		this.outputStream.reset()
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(B)V")
	override fun close() {
		try {
			this.socket.close()
		} catch (@Pc(8) ignored: IOException) {
		}
		this.inputStream.shutdown()
		this.outputStream.stop()
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(IIB[B)I")
	@Throws(IOException::class)
	override fun read(@OriginalArg(0) length: Int, @OriginalArg(1) offset: Int, @OriginalArg(3) buffer: ByteArray?): Int {
		return this.inputStream.read(length, offset, buffer)
	}

	@OriginalMember(owner = "client!gea", name = "a", descriptor = "(II)Z")
	@Throws(IOException::class)
	override fun hasAvailable(@OriginalArg(0) n: Int): Boolean {
		return this.inputStream.available(n)
	}

	@OriginalMember(owner = "client!gea", name = "finalize", descriptor = "()V")
	fun finalize() {
		this.close()
	}

	companion object {
		@OriginalMember(owner = "client!pk", name = "a", descriptor = "(Ljava/net/Socket;BI)Lclient!ss;")
		@JvmStatic
		@Throws(IOException::class)
		fun create(@OriginalArg(0) socket: Socket?): Connection? {
			return AsyncConnection(socket, 7500)
		}
	}
}
