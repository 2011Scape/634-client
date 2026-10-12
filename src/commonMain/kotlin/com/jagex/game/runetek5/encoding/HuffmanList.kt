package com.jagex.game.runetek5.encoding

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object HuffmanList {
	@OriginalMember(owner = "client!ka", name = "h", descriptor = "Lclient!wda;")
	@JvmField
	var HUFFMAN: Huffman? = null

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wda;B)V")
	@JvmStatic
	fun setHuffman(@OriginalArg(0) huffman: Huffman?) {
		HuffmanList.HUFFMAN = huffman
	}

	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(ILclient!cea;I)Ljava/lang/String;")
	@JvmStatic
	fun readHuffmanString(@OriginalArg(1) packet: Packet?): String? {
		try {
			@Pc(15) var size: Int = packet!!.readUSmart()
			if (size > 32767) {
				size = 32767
			}
			@Pc(27) val data: ByteArray = ByteArray(size)
			packet.currentPosition += HuffmanList.HUFFMAN!!.decompress(data, packet.currentPosition, packet.buffer, size, 0)
			return Utils.readString(data, size, 0)
		} catch (@Pc(51) ex: Exception) {
			return "Cabbage"
		}
	}

	@OriginalMember(owner = "client!wi", name = "a", descriptor = "(BLclient!cea;)Ljava/lang/String;")
	@JvmStatic
	fun decodeHuffmanString(@OriginalArg(1) packet: Packet?): String? {
		return HuffmanList.readHuffmanString(packet)
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(Lclient!cea;ILjava/lang/String;)I")
	@JvmStatic
	fun writeChatMessage(@OriginalArg(0) packet: Packet?, @OriginalArg(2) string: String?): Int {
		@Pc(6) val currentPosition: Int = packet!!.currentPosition
		@Pc(10) val data: ByteArray = Utils.toCp1252Bytes(string!!)
		packet.writeSmart(data.size)
		packet.currentPosition += HuffmanList.HUFFMAN!!.compress(data.size, 0, packet.currentPosition, data, packet.buffer)
		return packet.currentPosition - currentPosition
	}
}
