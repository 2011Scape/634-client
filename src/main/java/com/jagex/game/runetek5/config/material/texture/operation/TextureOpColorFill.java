package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qha")
public final class TextureOpColorFill extends TextureOperation {

	@OriginalMember(owner = "client!qha", name = "I", descriptor = "I")
	private int redComponent;

	@OriginalMember(owner = "client!qha", name = "O", descriptor = "I")
	private int blueComponent;

	@OriginalMember(owner = "client!qha", name = "Q", descriptor = "I")
	private int greenComponent;

	@OriginalMember(owner = "client!qha", name = "<init>", descriptor = "(I)V")
	private TextureOpColorFill(@OriginalArg(0) int rgb) {
		super(0, false);
		this.setColour(rgb);
	}

	@OriginalMember(owner = "client!qha", name = "<init>", descriptor = "()V")
	public TextureOpColorFill() {
		this(0);
	}

	@OriginalMember(owner = "client!qha", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(13) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(21) int[] outputRed = output[0];
			@Pc(25) int[] outputGreen = output[1];
			@Pc(29) int[] outputBlue = output[2];
			for (@Pc(31) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				outputRed[i] = this.redComponent;
				outputGreen[i] = this.greenComponent;
				outputBlue[i] = this.blueComponent;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!qha", name = "e", descriptor = "(II)V")
	private void setColour(@OriginalArg(1) int rgb) {
		this.redComponent = rgb >> 12 & 0xFF0;
		this.greenComponent = rgb >> 4 & 0xFF0;
		this.blueComponent = (rgb & 0xFF) << 4;
	}

	@OriginalMember(owner = "client!qha", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.setColour(packet.readUMedium());
		}
	}
}
