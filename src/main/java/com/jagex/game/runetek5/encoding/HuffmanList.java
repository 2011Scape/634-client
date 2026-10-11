package com.jagex.game.runetek5.encoding;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.utils.Utils;
import org.openrs2.deob.annotation.Pc;

public final class HuffmanList {
	@OriginalMember(owner = "client!ka", name = "h", descriptor = "Lclient!wda;")
	public static Huffman HUFFMAN;

	private HuffmanList() {
		/* empty */
	}
	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wda;B)V")
	public static void setHuffman(@OriginalArg(0) Huffman huffman) {
		HuffmanList.HUFFMAN = huffman;
	}
	@OriginalMember(owner = "client!bd", name = "a", descriptor = "(ILclient!cea;I)Ljava/lang/String;")
	public static String readHuffmanString(@OriginalArg(1) Packet packet) {
		try {
			@Pc(15) int size = packet.readUSmart();
			if (size > 32767) {
				size = 32767;
			}
			@Pc(27) byte[] data = new byte[size];
			packet.currentPosition += HuffmanList.HUFFMAN.decompress(data, packet.currentPosition, packet.buffer, size, 0);
			return Utils.readString(data, size, 0);
		} catch (@Pc(51) Exception ex) {
			return "Cabbage";
		}
	}
	@OriginalMember(owner = "client!wi", name = "a", descriptor = "(BLclient!cea;)Ljava/lang/String;")
	public static String decodeHuffmanString(@OriginalArg(1) Packet packet) {
		return HuffmanList.readHuffmanString(packet);
	}
	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(Lclient!cea;ILjava/lang/String;)I")
	public static int writeChatMessage(@OriginalArg(0) Packet packet, @OriginalArg(2) String string) {
		@Pc(6) int currentPosition = packet.currentPosition;
		@Pc(10) byte[] data = Utils.toCp1252Bytes(string);
		packet.writeSmart(data.length);
		packet.currentPosition += HuffmanList.HUFFMAN.compress(data.length, 0, packet.currentPosition, data, packet.buffer);
		return packet.currentPosition - currentPosition;
	}
}
