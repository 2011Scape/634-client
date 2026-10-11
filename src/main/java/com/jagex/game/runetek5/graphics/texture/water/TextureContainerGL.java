package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.GlVolumeTexture;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.shader.Texture3DShaderGL;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.ByteArrayWrapper;

@OriginalClass("client!lc")
public final class TextureContainerGL {

	@OriginalMember(owner = "client!lc", name = "d", descriptor = "Lclient!gj;")
	public GlVolumeTexture aClass85_Sub2_2 = null;

	@OriginalMember(owner = "client!lc", name = "e", descriptor = "[Lclient!tp;")
	public OpenGl2DTexture[] waveTextures2d = null;

	@OriginalMember(owner = "client!lc", name = "g", descriptor = "Lclient!gj;")
	public GlVolumeTexture texture3d = null;

	@OriginalMember(owner = "client!lc", name = "a", descriptor = "Lclient!gj;")
	public GlVolumeTexture waveTexture3d = null;

	@OriginalMember(owner = "client!lc", name = "i", descriptor = "[Lclient!tp;")
	public OpenGl2DTexture[] billowTextures2d = null;

	@OriginalMember(owner = "client!lc", name = "c", descriptor = "Z")
	public final boolean texture3dSupported;

	@OriginalMember(owner = "client!lc", name = "<init>", descriptor = "(Lclient!qo;)V")
	public TextureContainerGL(@OriginalArg(0) GlToolkit glToolkit) {
		this.texture3dSupported = glToolkit.texture3dSupported;
		TextureContainerGL.initializeTextureData(glToolkit);
		if (this.texture3dSupported) {
			@Pc(33) byte[] data = ByteArrayWrapper.unwrap(false, TextureContainerGL.waveTextureBuffer);
			this.waveTexture3d = new GlVolumeTexture(glToolkit, 6410, 128, 128, 16, data, 6410);
			@Pc(50) byte[] billowData = ByteArrayWrapper.unwrap(false, TextureContainerGL.billowTextureBuffer);
			this.texture3d = new GlVolumeTexture(glToolkit, 6410, 128, 128, 16, billowData, 6410);
			@Pc(65) Texture3DShaderGL shader = glToolkit.texture3dShader;
			if (shader.createShader()) {
				data = ByteArrayWrapper.unwrap(false, TextureContainerGL.noiseTextureBuffer);
				this.aClass85_Sub2_2 = new GlVolumeTexture(glToolkit, 6408, 128, 128, 16);
				@Pc(95) GlVolumeTexture glVolumeTexture = new GlVolumeTexture(glToolkit, 6409, 128, 128, 16, data, 6409);
				if (shader.activate(this.aClass85_Sub2_2, glVolumeTexture, 2.0F)) {
					this.aClass85_Sub2_2.generateMipmaps();
				} else {
					this.aClass85_Sub2_2.delete();
					this.aClass85_Sub2_2 = null;
				}
				glVolumeTexture.delete();
				return;
			}
		} else {
			this.waveTextures2d = new OpenGl2DTexture[16];
			for (@Pc(127) int i = 0; i < 16; i++) {
				@Pc(141) byte[] waveFrame = ByteArrayWrapper.unwrapBlock(TextureContainerGL.waveTextureBuffer, 2 * 128 * i * 128);
				this.waveTextures2d[i] = new OpenGl2DTexture(glToolkit, 3553, 6410, 128, 128, true, waveFrame, 6410, false);
			}
			this.billowTextures2d = new OpenGl2DTexture[16];
			for (@Pc(167) int j = 0; j < 16; j++) {
				@Pc(179) byte[] billowFrame = ByteArrayWrapper.unwrapBlock(TextureContainerGL.billowTextureBuffer, j * 2 * 16384);
				this.billowTextures2d[j] = new OpenGl2DTexture(glToolkit, 3553, 6410, 128, 128, true, billowFrame, 6410, false);
			}
		}
	}
	@OriginalMember(owner = "client!ou", name = "x", descriptor = "Ljava/lang/Object;")
	public static Object noiseTextureBuffer;

	@OriginalMember(owner = "client!cn", name = "h", descriptor = "Ljava/lang/Object;")
	public static Object billowTextureBuffer;

	@OriginalMember(owner = "client!sa", name = "a", descriptor = "(ILclient!qo;)V")
	public static void initializeTextureData(@OriginalArg(1) GlToolkit glToolkit) {
		@Pc(16) byte[] data;
		if (TextureContainerGL.waveTextureBuffer == null) {
			@Pc(9) NativeWaveTextureData nativeRippleNoiseTexture = new NativeWaveTextureData();
			data = nativeRippleNoiseTexture.getData();
			TextureContainerGL.waveTextureBuffer = ByteArrayWrapper.wrap(data);
		}
		if (TextureContainerGL.billowTextureBuffer == null) {
			@Pc(27) GLBillowTextureData billowTextureData = new GLBillowTextureData();
			data = billowTextureData.getData();
			TextureContainerGL.billowTextureBuffer = ByteArrayWrapper.wrap(data);
		}
		@Pc(49) Texture3DShaderGL shader = glToolkit.texture3dShader;
		if (shader.createShader() && TextureContainerGL.noiseTextureBuffer == null) {
			data = NoiseGenerator.generateHeightData(0.6F, 4.0F, 4.0F, 0.5F, 16.0F, new PerlinNoiseGenerator(419684));
			TextureContainerGL.noiseTextureBuffer = ByteArrayWrapper.wrap(data);
		}
	}

	@OriginalMember(owner = "client!dw", name = "e", descriptor = "Ljava/lang/Object;")
	public static Object waveTextureBuffer;
}
