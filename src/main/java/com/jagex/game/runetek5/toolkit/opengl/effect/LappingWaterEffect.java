package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL;
import com.jagex.game.runetek5.toolkit.opengl.effect.MaterialEffectGL;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.GlArbProgram;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise;

@OriginalClass("client!em")
public final class LappingWaterEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!em", name = "j", descriptor = "I")
	private int animatingProgression;

	@OriginalMember(owner = "client!em", name = "m", descriptor = "Lclient!jv;")
	private GlRenderList listUtils;

	@OriginalMember(owner = "client!em", name = "p", descriptor = "F")
	private float texture3dAnimatingProgress;

	@OriginalMember(owner = "client!em", name = "r", descriptor = "Lclient!lc;")
	private final TextureContainerGL textureContainer;

	@OriginalMember(owner = "client!em", name = "k", descriptor = "Lclient!pl;")
	private GlArbProgram program;

	@OriginalMember(owner = "client!em", name = "i", descriptor = "[F")
	private float[] noise;

	@OriginalMember(owner = "client!em", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	public LappingWaterEffect(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) TextureContainerGL glWaterNoiseTextureSet) {
		super(glToolkit);
		this.textureContainer = glWaterNoiseTextureSet;
		if (super.toolkit.arbVertexProgram && super.toolkit.maxTextureUnits >= 2) {
			this.program = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nOPTION  ARB_position_invariant;\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   time         = program.local[65];\nPARAM   turbulence   = program.local[64];\nPARAM   lightAmbient = program.local[66]; \nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   ivMatrix[4]  = { state.matrix.texture[1] };\nPARAM   texMatrix[4]  = { state.matrix.texture[0] };\nPARAM   fNoise[64]   = { program.local[0..63] };\nTEMP    noise, viewPos, worldPos, texCoord;\nADDRESS noiseAddr;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nMOV   oFogCoord.x, -viewPos.z;\nDP4   worldPos.x, ivMatrix[0], viewPos;\nDP4   worldPos.y, ivMatrix[1], viewPos;\nDP4   worldPos.z, ivMatrix[2], viewPos;\nDP4   worldPos.w, ivMatrix[3], viewPos;\nADD   noise.x, worldPos.x, worldPos.z;SUB   noise.y, worldPos.z, worldPos.x;MUL   noise, noise, 0.0001220703125;\nFRC   noise, noise;\nMUL   noise, noise, 64;\nARL   noiseAddr.x, noise.x;\nMOV   noise.x, fNoise[noiseAddr.x].x;\nARL   noiseAddr.x, noise.y;\nMOV   noise.y, fNoise[noiseAddr.x].y;\nMUL   noise, noise, turbulence.x;\nDP4   texCoord.x, texMatrix[0], iTexCoord;\nDP4   texCoord.y, texMatrix[1], iTexCoord;\nADD   oTexCoord0.xy, texCoord, noise;\nMOV   oTexCoord0.z, 0;\nMOV   oTexCoord0.w, 1;\nMUL   oTexCoord1.xy, texCoord, 0.125;\nMOV   oTexCoord1.zw, time.xxxw;\nMUL   oColour.xyz, iColour, lightAmbient;\nMOV   oColour.w, iColour.w;\nEND");
			if (this.program != null) {
				@Pc(39) int[][] noiseMapX = LappingWaterEffect.generateNoiseMap(0.4F);
				@Pc(50) int[][] noiseMapY = LappingWaterEffect.generateNoiseMap(0.4F);
				this.noise = new float[32768];
				@Pc(56) int offset = 0;
				for (@Pc(58) int i = 0; i < 256; i++) {
					@Pc(64) int[] rowX = noiseMapX[i];
					@Pc(68) int[] rowY = noiseMapY[i];
					for (@Pc(70) int j = 0; j < 64; j++) {
						this.noise[offset++] = (float) rowX[j] / 4096.0F;
						this.noise[offset++] = (float) rowY[j] / 4096.0F;
					}
				}
				this.buildDisplayList();
			}
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
		if (this.listUtils == null) {
			return;
		}
		super.toolkit.activateTexture(1);
		@Pc(81) int frame;
		if ((effectParam1 & 0x80) != 0) {
			super.toolkit.setActiveTexture(null);
		} else if ((effectParam2 & 0x1) != 1) {
			if (this.textureContainer.texture3dSupported) {
				super.toolkit.setActiveTexture(this.textureContainer.waveTexture3d);
			} else {
				super.toolkit.setActiveTexture(this.textureContainer.waveTextures2d[0]);
			}
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0F, 0.0F, 0.0F, 1.0F);
		} else if (this.textureContainer.texture3dSupported) {
			super.toolkit.setActiveTexture(this.textureContainer.waveTexture3d);
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, this.texture3dAnimatingProgress, 0.0F, 0.0F, 1.0F);
		} else {
			frame = super.toolkit.lastTickMillis % 4000 * 16 / 4000;
			super.toolkit.setActiveTexture(this.textureContainer.waveTextures2d[frame]);
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0F, 0.0F, 0.0F, 1.0F);
		}
		super.toolkit.activateTexture(0);
		if ((effectParam1 & 0x40) == 0) {
			LappingWaterEffect.SUN_COLOUR[2] = super.toolkit.sunColorBlue * super.toolkit.sunBrightness;
			LappingWaterEffect.SUN_COLOUR[0] = super.toolkit.sunColorRed * super.toolkit.sunBrightness;
			LappingWaterEffect.SUN_COLOUR[1] = super.toolkit.sunBrightness * super.toolkit.sunColorGreen;
			OpenGL.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, LappingWaterEffect.SUN_COLOUR, 0);
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, 1.0F, 1.0F, 1.0F, 1.0F);
		}
		frame = effectParam1 & 0x3;
		if (frame == 2) {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.05F, 1.0F, 1.0F, 1.0F);
		} else if (frame == 3) {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.1F, 1.0F, 1.0F, 1.0F);
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.025F, 1.0F, 1.0F, 1.0F);
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		super.toolkit.setActiveTexture(texture);
		super.toolkit.setColourOp(combineMode);
	}

	@OriginalMember(owner = "client!em", name = "e", descriptor = "(I)V")
	private void buildDisplayList() {
		this.listUtils = new GlRenderList(super.toolkit, 2);
		this.listUtils.newList(0);
		super.toolkit.activateTexture(1);
		super.toolkit.setTextureEnvColour(-16777216);
		super.toolkit.loadTextureCombineMode(260, 7681);
		super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_CONSTANT);
		super.toolkit.activateTexture(0);
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, this.program.programId);
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
		this.listUtils.endList();
		this.listUtils.newList(1);
		super.toolkit.activateTexture(1);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glLoadIdentity();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		super.toolkit.setColourOp(0);
		super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
		super.toolkit.activateTexture(0);
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
		OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
		OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB);
		this.listUtils.endList();
	}

	@OriginalMember(owner = "client!em", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		if (this.listUtils != null) {
			this.listUtils.callList('\u0001');
			super.toolkit.activateTexture(1);
			super.toolkit.setActiveTexture(null);
			super.toolkit.activateTexture(0);
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		if (this.listUtils == null) {
			return;
		}
		this.listUtils.callList('\u0000');
		super.toolkit.activateTexture(1);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glLoadMatrixf(super.toolkit.inverseModelViewMatrix.getColumnMajor(), 0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		super.toolkit.activateTexture(0);
		if (this.animatingProgression == super.toolkit.lastTickMillis) {
			return;
		}
		@Pc(59) int offset = super.toolkit.lastTickMillis % 5000 * 128 / 5000;
		for (@Pc(61) int param = 0; param < 64; param++) {
			OpenGL.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, param, this.noise, offset);
			offset += 2;
		}
		if (this.textureContainer.texture3dSupported) {
			this.texture3dAnimatingProgress = (float) (super.toolkit.lastTickMillis % 4000) / 4000.0F;
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0F, 0.0F, 0.0F, 1.0F);
		}
		this.animatingProgression = super.toolkit.lastTickMillis;
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}
	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(IIIIIZIFZ)[[I")
	public static int[][] generateNoiseMap(@OriginalArg(7) float noiseAmplitude) {
		@Pc(9) int[][] noise = new int[256][64];
		@Pc(13) TextureOpPerlinNoise textureOp34 = new TextureOpPerlinNoise();
		textureOp34.noiseAmplitude = (int) (noiseAmplitude * 4096.0F);
		textureOp34.numNoiseSteps = 3;
		textureOp34.texCoordScaleX = 4;
		textureOp34.texCoordScaleY = 4;
		textureOp34.useFogEffect = false;
		textureOp34.postDecode();
		TextureRaster.setDimensions(256, 64);
		for (@Pc(45) int i = 0; i < 256; i++) {
			textureOp34.computeNoiseRow(i, noise[i]);
		}
		return noise;
	}

	@OriginalMember(owner = "client!bda", name = "z", descriptor = "[F")
	public static final float[] SUN_COLOUR = new float[4];
}
