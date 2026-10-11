package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pba")
public final class TextureOpConstant extends TextureOperation {

	@OriginalMember(owner = "client!pba", name = "M", descriptor = "I")
	private int intensity;

	@OriginalMember(owner = "client!pba", name = "<init>", descriptor = "(I)V")
	public TextureOpConstant(@OriginalArg(0) int intensity) {
		super(0, true);
		this.intensity = 4096;
		this.intensity = intensity;
	}

	@OriginalMember(owner = "client!pba", name = "<init>", descriptor = "()V")
	public TextureOpConstant() {
		this(4096);
	}

	@OriginalMember(owner = "client!pba", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.intensity = (packet.readUByte() << 12) / 255;
		}
	}

	@OriginalMember(owner = "client!pba", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(9) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, this.intensity);
		}
		return output;
	}
}
