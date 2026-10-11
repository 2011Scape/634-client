package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.GlArbProgram;
import com.jagex.game.runetek5.Matrix43;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.effect.MaterialEffectGL;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!an")
public final class StillWaterEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!an", name = "o", descriptor = "Z")
	private boolean bound;

	@OriginalMember(owner = "client!an", name = "s", descriptor = "Z")
	private boolean calculateLights;

	@OriginalMember(owner = "client!an", name = "e", descriptor = "Z")
	private boolean isUsingFallbackTexture = false;

	@OriginalMember(owner = "client!an", name = "j", descriptor = "Lclient!pl;")
	private GlArbProgram waterNodepthColor;

	@OriginalMember(owner = "client!an", name = "r", descriptor = "Lclient!pl;")
	private GlArbProgram waterNodepthLit;

	@OriginalMember(owner = "client!an", name = "u", descriptor = "Lclient!pl;")
	private GlArbProgram waterDepthColor;

	@OriginalMember(owner = "client!an", name = "i", descriptor = "Lclient!pl;")
	private GlArbProgram waterDepthLit;

	@OriginalMember(owner = "client!an", name = "t", descriptor = "Z")
	private final boolean initialized;

	@OriginalMember(owner = "client!an", name = "p", descriptor = "Lclient!tp;")
	private OpenGl2DTexture openglTexture;

	@OriginalMember(owner = "client!an", name = "<init>", descriptor = "(Lclient!qo;)V")
	public StillWaterEffect(@OriginalArg(0) GlToolkit glToolkit) {
		super(glToolkit);
		if (super.toolkit.arbVertexProgram) {
			this.waterNodepthColor = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   tMatrix[4]   = { state.matrix.texture[0] };\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nTEMP    viewPos, fogFactor;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nMAD   fogFactor.y, iTexCoord.z, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMUL   fogFactor.x, fogFactor.x, fogFactor.y;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, iTexCoord.z;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nMOV   oColour, iColour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP3   oTexCoord0.x, tMatrix[0], iTexCoord;\nDP3   oTexCoord0.y, tMatrix[1], iTexCoord;\nMOV   oTexCoord0.zw, iTexCoord;\nEND\n");
			this.waterNodepthLit = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iNormal      = vertex.normal;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   tMatrix[4]   = { state.matrix.texture[0] };\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nTEMP    viewPos, viewNormal, fogFactor, colour, ndotl;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nMAD   fogFactor.y, iTexCoord.z, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMUL   fogFactor.x, fogFactor.x, fogFactor.y;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, iTexCoord.z;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nDP3   viewNormal.x, mvMatrix[0], iNormal;\nDP3   viewNormal.y, mvMatrix[1], iNormal;\nDP3   viewNormal.z, mvMatrix[2], iNormal;\nDP3   ndotl.x, viewNormal, state.light[0].position;\nDP3   ndotl.y, viewNormal, state.light[1].position;\nMAX   ndotl, ndotl, 0;\nMOV   colour, state.lightmodel.ambient;\nMAD   colour, state.light[0].diffuse, ndotl.xxxx, colour;\nMAD   colour, state.light[1].diffuse, ndotl.yyyy, colour;\nMUL   oColour, iColour, colour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP3   oTexCoord0.x, tMatrix[0], iTexCoord;\nDP3   oTexCoord0.y, tMatrix[1], iTexCoord;\nMOV   oTexCoord0.zw, iTexCoord;\nEND\n");
			this.waterDepthColor = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   texMatrix[4] = { state.matrix.texture[0] };\nTEMP    viewPos, fogFactor, depth;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nDP4   depth, waterPlane, viewPos;\nMAD   fogFactor.y, -depth, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, -depth;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nMOV   oColour, iColour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP4   oTexCoord0.x, texMatrix[0], iTexCoord;\nDP4   oTexCoord0.y, texMatrix[1], iTexCoord;\nDP4   oTexCoord0.z, texMatrix[2], iTexCoord;\nMOV   oTexCoord0.w, 1;\nEND\n");
			this.waterDepthLit = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iNormal      = vertex.normal;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   texMatrix[4] = { state.matrix.texture[0] };\nTEMP    viewPos, viewNormal, fogFactor, depth, colour, ndotl;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nDP4   depth, waterPlane, viewPos;\nMAD   fogFactor.y, -depth, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, -depth;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nDP3   viewNormal.x, mvMatrix[0], iNormal;\nDP3   viewNormal.y, mvMatrix[1], iNormal;\nDP3   viewNormal.z, mvMatrix[2], iNormal;\nDP3   ndotl.x, viewNormal, state.light[0].position;\nDP3   ndotl.y, viewNormal, state.light[1].position;\nMAX   ndotl, ndotl, 0;\nMOV   colour, state.lightmodel.ambient;\nMAD   colour, state.light[0].diffuse, ndotl.xxxx, colour;\nMAD   colour, state.light[1].diffuse, ndotl.yyyy, colour;\nMUL   oColour, iColour, colour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP4   oTexCoord0.x, texMatrix[0], iTexCoord;\nDP4   oTexCoord0.y, texMatrix[1], iTexCoord;\nDP4   oTexCoord0.z, texMatrix[2], iTexCoord;\nMOV   oTexCoord0.w, 1;\nEND\n");
			if (this.waterDepthLit != null & this.waterNodepthColor != null & this.waterNodepthLit != null & this.waterDepthColor != null) {
				this.openglTexture = new OpenGl2DTexture(glToolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_ALPHA, 2, 1, false, new byte[] { 0, -1 }, OpenGLConstants.GL_ALPHA, false);
				this.openglTexture.setRepeat(false, false);
				this.initialized = true;
			} else {
				this.initialized = false;
			}
		} else {
			this.initialized = false;
		}
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(Z)V")
	public void bindProgram() {
		@Pc(11) Matrix43 modelViewMatrix = super.toolkit.modelViewMatrix;
		if (this.calculateLights) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, ~super.toolkit.waterHeight == Integer.MIN_VALUE ? this.waterNodepthLit.programId : this.waterDepthLit.programId);
		} else {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, super.toolkit.waterHeight == Integer.MAX_VALUE ? this.waterNodepthColor.programId : this.waterDepthColor.programId);
		}
		modelViewMatrix.transformPlane(0.0F, 0.0F, -1.0F, (float) super.toolkit.waterHeight, StillWaterEffect.waterPlane);
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 1, StillWaterEffect.waterPlane[0], StillWaterEffect.waterPlane[1], StillWaterEffect.waterPlane[2], StillWaterEffect.waterPlane[3]);
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
		this.bound = true;
		this.updateFogParams();
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!an", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.initialized;
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean calculateLights) {
		this.calculateLights = calculateLights;
		super.toolkit.activateTexture(1);
		super.toolkit.setActiveTexture(this.openglTexture);
		super.toolkit.loadTextureCombineMode(34165, 7681);
		super.toolkit.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_COLOR, 0);
		super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_ALPHA, 2);
		super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS);
		super.toolkit.activateTexture(0);
		this.bindProgram();
	}

	@OriginalMember(owner = "client!an", name = "e", descriptor = "(I)V")
	public void updateFogParams() {
		if (!this.bound) {
			return;
		}
		@Pc(10) int fogFar = super.toolkit.XA();
		@Pc(14) int fogNear = super.toolkit.i();
		@Pc(25) float depthFadeStart = (float) fogFar - (float) (fogFar - fogNear) * 0.125F;
		@Pc(35) float blendFadeStart = (float) fogFar - (float) (fogFar - fogNear) * 0.25F;
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, blendFadeStart, depthFadeStart, 1.0F / (float) super.toolkit.waterDepth, (float) super.toolkit.waterIntensity / 255.0F);
		super.toolkit.activateTexture(1);
		super.toolkit.setTextureEnvColour(super.toolkit.waterFogColour);
		super.toolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		if (this.bound) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
			OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB);
			OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
			this.bound = false;
		}
		super.toolkit.activateTexture(1);
		super.toolkit.setActiveTexture(null);
		super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
		super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
		super.toolkit.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_ALPHA, 2);
		super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
		super.toolkit.activateTexture(0);
		if (this.isUsingFallbackTexture) {
			super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
			this.isUsingFallbackTexture = false;
		}
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
	}

	@OriginalMember(owner = "client!an", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		if (texture != null) {
			if (this.isUsingFallbackTexture) {
				super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
				super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
				this.isUsingFallbackTexture = false;
			}
			super.toolkit.setActiveTexture(texture);
			super.toolkit.setColourOp(combineMode);
		} else if (!this.isUsingFallbackTexture) {
			super.toolkit.setActiveTexture(super.toolkit.texture2d);
			super.toolkit.setColourOp(1);
			super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS);
			this.isUsingFallbackTexture = true;
		}
	}
	@OriginalMember(owner = "client!lia", name = "e", descriptor = "[F")
	public static final float[] waterPlane = new float[] { 0.0F, -1.0F, 0.0F, 0.0F };
}
