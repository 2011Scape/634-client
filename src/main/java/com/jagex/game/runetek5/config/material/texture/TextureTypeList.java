package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class TextureTypeList {
	@OriginalMember(owner = "client!ufa", name = "c", descriptor = "Lclient!in;")
	public static Js5ResourceProvider modelResource;

	@OriginalMember(owner = "client!kv", name = "n", descriptor = "D")
	public static double currentBrightness = -1.0D;

	@OriginalMember(owner = "client!eh", name = "b", descriptor = "Lclient!in;")
	public static Js5ResourceProvider textureSpriteResource;

	@OriginalMember(owner = "client!kda", name = "fc", descriptor = "[I")
	public static final int[] BRIGHTNESS_TABLE = new int[256];

	private TextureTypeList() {
		/* empty */
	}
	@OriginalMember(owner = "client!gn", name = "e", descriptor = "Lclient!d;")
	public static MaterialProvider textureSource;
	@OriginalMember(owner = "client!sa", name = "a", descriptor = "(ILclient!cea;)Lclient!tf;")
	public static TextureOperation decodeTextureOperation(@OriginalArg(1) Packet packet) {
		packet.readUByte();
		@Pc(13) int implementationType = packet.readUByte();
		@Pc(17) TextureOperation textureOp = TextureOperation.implementationForType(implementationType);
		textureOp.imageCacheCapacity = packet.readUByte();
		@Pc(26) int opcodeCount = packet.readUByte();
		for (@Pc(33) int i = 0; i < opcodeCount; i++) {
			@Pc(39) int opcode = packet.readUByte();
			textureOp.decode(packet, opcode);
		}
		textureOp.postDecode();
		return textureOp;
	}
}
