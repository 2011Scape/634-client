package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!tga")
public final class TextureOpMandelbrot extends TextureOperation {

	@OriginalMember(owner = "client!tga", name = "L", descriptor = "I")
	private int centerY = 0;

	@OriginalMember(owner = "client!tga", name = "M", descriptor = "I")
	private int centerX = 0;

	@OriginalMember(owner = "client!tga", name = "N", descriptor = "I")
	private int zoom = 1365;

	@OriginalMember(owner = "client!tga", name = "Q", descriptor = "I")
	private int maxIterations = 20;

	@OriginalMember(owner = "client!tga", name = "<init>", descriptor = "()V")
	public TextureOpMandelbrot() {
		super(0, true);
	}

	@OriginalMember(owner = "client!tga", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.zoom = packet.readUShort();
		} else if (opcode == 1) {
			this.maxIterations = packet.readUShort();
		} else if (opcode == 2) {
			this.centerX = packet.readUShort();
		} else if (opcode == 3) {
			this.centerY = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!tga", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			for (@Pc(22) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(36) int cReal = this.centerX + (EnvironmentLight.horizontalTable[i] << 12) / this.zoom;
				@Pc(48) int cImaginary = this.centerY + (TextureRaster.verticalTable[row] << 12) / this.zoom;
				@Pc(54) int real = cReal;
				@Pc(56) int imaginary = cImaginary;
				@Pc(62) int realSquared = cReal * cReal >> 12;
				@Pc(68) int imaginarySquared = cImaginary * cImaginary >> 12;
				@Pc(70) int iterations = 0;
				while (imaginarySquared + realSquared < 16384 && iterations < this.maxIterations) {
					imaginary = cImaginary + (imaginary * real >> 12) * 2;
					real = cReal + realSquared - imaginarySquared;
					iterations++;
					imaginarySquared = imaginary * imaginary >> 12;
					realSquared = real * real >> 12;
				}
				output[i] = this.maxIterations - 1 <= iterations ? 0 : (iterations << 12) / this.maxIterations;
			}
		}
		return output;
	}
}
