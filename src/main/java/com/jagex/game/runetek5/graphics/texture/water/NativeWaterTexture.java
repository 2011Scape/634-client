package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.graphics.texture.water.PerlinNoiseGenerator;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.ByteArrayWrapper;

@OriginalClass("client!ur")
public final class NativeWaterTexture {

	@OriginalMember(owner = "client!ur", name = "c", descriptor = "Lclient!ir;")
	public Texture3DSampler waterSampler3d = null;

	@OriginalMember(owner = "client!ur", name = "f", descriptor = "Lclient!ir;")
	public Texture3DSampler normalSampler = null;

	@OriginalMember(owner = "client!ur", name = "e", descriptor = "[Lclient!wfa;")
	private GpuTexture[] textureSamplers = null;

	@OriginalMember(owner = "client!ur", name = "j", descriptor = "[Lclient!wfa;")
	public GpuTexture[] waterTextureSamplers = null;

	@OriginalMember(owner = "client!ur", name = "h", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit activeToolkit;

	@OriginalMember(owner = "client!ur", name = "i", descriptor = "Z")
	public boolean hasDepth;

	@OriginalMember(owner = "client!ur", name = "<init>", descriptor = "(Lclient!wga;)V")
	public NativeWaterTexture(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		this.activeToolkit = nativeToolkit;
		this.hasDepth = this.activeToolkit.SUPPORTS_TEXTURE3D;
		if (this.hasDepth && !this.activeToolkit.supports3DTextureFormat(PixelFormat.aClass375_39, GLFormat.RGBA)) {
			this.hasDepth = false;
		}
		if (this.hasDepth || this.activeToolkit.supportsTextureFormat(GLFormat.RGBA, PixelFormat.aClass375_39)) {
			NativeWaterTexture.generateTextureData();
			if (this.hasDepth) {
				@Pc(134) byte[] waveData = ByteArrayWrapper.unwrap(false, NativeWaterTexture.waveTextureDataBuffer);
				this.waterSampler3d = this.activeToolkit.create3DTexture(PixelFormat.aClass375_39, waveData);
				@Pc(150) byte[] billowData = ByteArrayWrapper.unwrap(false, NativeWaterTexture.billowTextureDataBuffer);
				this.activeToolkit.create3DTexture(PixelFormat.aClass375_39, billowData);
			} else {
				this.waterTextureSamplers = new GpuTexture[16];
				for (@Pc(58) int i = 0; i < 16; i++) {
					@Pc(72) byte[] waveFrame = ByteArrayWrapper.unwrapBlock(NativeWaterTexture.waveTextureDataBuffer, i * 128 * 128 * 2);
					this.waterTextureSamplers[i] = this.activeToolkit.createTextureSampler(waveFrame, 128, PixelFormat.aClass375_39, true, 128);
				}
				this.textureSamplers = new GpuTexture[16];
				for (@Pc(95) int j = 0; j < 16; j++) {
					@Pc(109) byte[] billowFrame = ByteArrayWrapper.unwrapBlock(NativeWaterTexture.billowTextureDataBuffer, 128 * 128 * j * 2);
					this.textureSamplers[j] = this.activeToolkit.createTextureSampler(billowFrame, 128, PixelFormat.aClass375_39, true, 128);
				}
			}
		}
	}

	@OriginalMember(owner = "client!ur", name = "a", descriptor = "(I)Z")
	public boolean readyToSample() {
		if (this.hasDepth) {
			return this.waterSampler3d != null;
		} else {
			return this.waterTextureSamplers != null;
		}
	}

	@OriginalMember(owner = "client!ur", name = "a", descriptor = "(B)Z")
	public boolean loadNormalMap() {
		if (this.normalSampler == null) {
			@Pc(27) byte[] heightData;
			if (NativeWaterTexture.hdrDatabuffer == null) {
				heightData = NoiseGenerator.generateHeightData(0.6F, 4.0F, 4.0F, 0.5F, 16.0F, new PerlinNoiseGenerator(419684));
				NativeWaterTexture.hdrDatabuffer = ByteArrayWrapper.wrap(heightData);
			}
			heightData = ByteArrayWrapper.unwrap(false, NativeWaterTexture.hdrDatabuffer);
			@Pc(43) byte[] normals = new byte[heightData.length * 4];
			@Pc(45) int outIndex = 0;
			for (@Pc(47) int i = 0; i < 16; i++) {
				@Pc(55) int sourceIndex = i * 128 * 128;
				@Pc(57) int depthOffset = sourceIndex;
				for (@Pc(59) int j = 0; j < 128; j++) {
					@Pc(67) int currentRow = j * 128 + depthOffset;
					@Pc(77) int prevRow = (j - 1 & 0x7F) * 128 + depthOffset;
					@Pc(87) int nextRow = depthOffset + (j + 1 & 0x7F) * 128;
					for (@Pc(89) int k = 0; k < 128; k++) {
						@Pc(108) float verticalGradient = (float) ((heightData[k + prevRow] & 0xFF) - (heightData[k + nextRow] & 0xFF));
						@Pc(134) float horizontalGradient = (float) ((heightData[(k - 1 & 0x7F) + currentRow] & 0xFF) - (heightData[currentRow + (k + 1 & 0x7F)] & 0xFF));
						@Pc(149) float normalScale = (float) (128.0D / Math.sqrt((double) (verticalGradient * verticalGradient + horizontalGradient * horizontalGradient + 16384.0F)));
						normals[outIndex++] = (byte) (horizontalGradient * normalScale + 127.0F);
						normals[outIndex++] = (byte) (normalScale * 128.0F + 127.0F);
						normals[outIndex++] = (byte) (normalScale * verticalGradient + 127.0F);
						normals[outIndex++] = heightData[sourceIndex++];
					}
				}
			}
			this.normalSampler = this.activeToolkit.create3DTexture(GLFormat.HDR, normals);
		}
		return this.normalSampler != null;
	}
	@OriginalMember(owner = "client!sh", name = "d", descriptor = "Ljava/lang/Object;")
	public static Object billowTextureDataBuffer;

	@OriginalMember(owner = "client!aha", name = "e", descriptor = "Ljava/lang/Object;")
	public static Object waveTextureDataBuffer;

	@OriginalMember(owner = "client!uh", name = "i", descriptor = "Ljava/lang/Object;")
	public static Object hdrDatabuffer;
	@OriginalMember(owner = "client!sc", name = "b", descriptor = "(B)V")
	public static void generateTextureData() {
		@Pc(22) byte[] data;
		if (NativeWaterTexture.waveTextureDataBuffer == null) {
			@Pc(15) NativeWaveNoiseData waveNoiseData = new NativeWaveNoiseData();
			data = waveNoiseData.getData();
			NativeWaterTexture.waveTextureDataBuffer = ByteArrayWrapper.wrap(data);
		}
		if (NativeWaterTexture.billowTextureDataBuffer == null) {
			@Pc(37) NativeBillowTextureData billowData = new NativeBillowTextureData();
			data = billowData.getData();
			NativeWaterTexture.billowTextureDataBuffer = ByteArrayWrapper.wrap(data);
		}
	}
}
