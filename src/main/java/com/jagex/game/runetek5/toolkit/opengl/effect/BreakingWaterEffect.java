package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.GlShader;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlCubeTexture;
import com.jagex.game.runetek5.graphics.gl.GlProgram;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!er")
public final class BreakingWaterEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!er", name = "i", descriptor = "Z")
	private boolean initialized = false;

	@OriginalMember(owner = "client!er", name = "e", descriptor = "Z")
	private boolean loaded = false;

	@OriginalMember(owner = "client!er", name = "n", descriptor = "Lclient!lc;")
	private final TextureContainerGL textures;

	@OriginalMember(owner = "client!er", name = "f", descriptor = "Lclient!dw;")
	private GlProgram shaders;

	@OriginalMember(owner = "client!er", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	public BreakingWaterEffect(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) TextureContainerGL glWaterNoiseTextureSet) {
		super(glToolkit);
		this.textures = glWaterNoiseTextureSet;
		if (this.textures.aClass85_Sub2_2 != null && super.toolkit.arbVertexShader && super.toolkit.arbFragmentShader) {
			@Pc(30) GlShader vertexShader = GlShader.compile(35633, super.toolkit, "uniform float time;\nuniform float scale;\nvarying vec3 wvVertex;\nvarying float waterDepth;\nvoid main() {\nwaterDepth = gl_MultiTexCoord0.z;\nvec4 ecVertex = gl_ModelViewMatrix*gl_Vertex;\nwvVertex.x = dot(gl_NormalMatrix[0], ecVertex.xyz);\nwvVertex.y = dot(gl_NormalMatrix[1], ecVertex.xyz);\nwvVertex.z = dot(gl_NormalMatrix[2], ecVertex.xyz);\ngl_TexCoord[0].x = dot(gl_TextureMatrix[0][0], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].y = dot(gl_TextureMatrix[0][1], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].z = time;\ngl_TexCoord[0].w = 1.0;\ngl_FogFragCoord = 1.0-clamp((gl_Fog.end+ecVertex.z)*gl_Fog.scale, 0.0, 1.0);\ngl_Position = ftransform();\n}\n");
			@Pc(37) GlShader fragmentShader = GlShader.compile(35632, super.toolkit, "varying vec3 wvVertex;\nvarying float waterDepth;\nuniform vec3 sunDir;\nuniform vec4 sunColour;\nuniform float sunExponent;\nuniform vec2 waveIntensity;\nuniform float waveExponent;\nuniform float breakWaterDepth;\nuniform float breakWaterOffset;\nuniform sampler3D normalSampler;\nuniform samplerCube envMapSampler;\nvoid main() {\nvec4 wnNormal = texture3D(normalSampler, gl_TexCoord[0].xyz).rbga;\nwnNormal.xyz = 2.0*wnNormal.xyz-1.0;\nvec3 wnVector = normalize(wvVertex);\nvec3 wnReflection = reflect(wnVector, wnNormal.xyz);\nvec3 envColour = textureCube(envMapSampler, wnReflection).rgb;\nvec4 specularColour = sunColour*pow(clamp(-dot(sunDir, wnReflection), 0.0, 1.0), sunExponent);\nfloat shoreFactor = clamp(waterDepth/breakWaterDepth-breakWaterOffset*wnNormal.w, 0.0, 1.0);\nfloat waveFactor = pow(1.0-shoreFactor, waveExponent)-0.5;\nwaveFactor = -4.0*waveFactor*waveFactor+1.0;\nfloat ndote = dot(wnVector, wnNormal.xyz);\nfloat fresnel = pow(1.0-abs(ndote), 1.0);\nvec4 surfaceColour = mix(vec4(envColour, fresnel*shoreFactor), (waveIntensity.x*wnNormal.wwww)+waveIntensity.y, waveFactor)+specularColour*shoreFactor;\ngl_FragColor = vec4(mix(surfaceColour.rgb, gl_Fog.color.rgb, gl_FogFragCoord), surfaceColour.a);\n}\n");
			this.shaders = GlProgram.link(super.toolkit, new GlShader[] { vertexShader, fragmentShader });
			this.initialized = this.shaders != null;
		}
	}

	@OriginalMember(owner = "client!er", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		if (!this.loaded) {
			super.toolkit.setActiveTexture(texture);
			super.toolkit.setColourOp(combineMode);
		}
	}

	@OriginalMember(owner = "client!er", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		if (!this.loaded) {
			return;
		}
		super.toolkit.activateTexture(1);
		super.toolkit.setActiveTexture(null);
		super.toolkit.activateTexture(0);
		super.toolkit.setActiveTexture(null);
		OpenGL.glUseProgramObjectARB(0L);
		this.loaded = false;
	}

	@OriginalMember(owner = "client!er", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		@Pc(14) GlCubeTexture environmentMap = super.toolkit.getCubeMap();
		if (!this.initialized || environmentMap == null) {
			return;
		}
		@Pc(33) float sunBoost = (1.0F - Math.abs(super.toolkit.sunDirection[1])) * 2.0F + 1.0F;
		super.toolkit.activateTexture(1);
		super.toolkit.setActiveTexture(environmentMap);
		super.toolkit.activateTexture(0);
		super.toolkit.setActiveTexture(this.textures.aClass85_Sub2_2);
		@Pc(59) long handle = this.shaders.programHandle;
		OpenGL.glUseProgramObjectARB(handle);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "normalSampler"), 0);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "envMapSampler"), 1);
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(handle, "sunDir"), -super.toolkit.sunDirection[0], -super.toolkit.sunDirection[1], -super.toolkit.sunDirection[2]);
		OpenGL.glUniform4fARB(OpenGL.glGetUniformLocationARB(handle, "sunColour"), super.toolkit.sunColorRed * sunBoost, super.toolkit.sunColorGreen * sunBoost, super.toolkit.sunColorBlue * sunBoost, 1.0F);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "sunExponent"), Math.abs(super.toolkit.sunDirection[1]) * 928.0F + 64.0F);
		this.loaded = true;
	}

	@OriginalMember(owner = "client!er", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!er", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
		if (!this.loaded) {
			return;
		}
		@Pc(8) int timeScale = 0x1 << (effectParam1 & 0x3);
		@Pc(19) float scale = (float) (0x1 << (effectParam1 >> 3 & 0x7)) / 32.0F;
		@Pc(23) int breakWaterDepth = effectParam2 & 0xFFFF;
		@Pc(32) float breakWaterOffset = (float) (effectParam2 >> 16 & 0x3) / 8.0F;
		@Pc(41) float waveIntensityY = (float) (effectParam2 >> 19 & 0xF) / 16.0F;
		@Pc(50) float waveIntensityX = (float) (effectParam2 >> 23 & 0xF) / 16.0F;
		@Pc(56) int waveExponent = effectParam2 >> 27 & 0xF;
		@Pc(60) long handle = this.shaders.programHandle;
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "time"), (float) (timeScale * super.toolkit.lastTickMillis % 40000) / 40000.0F);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "scale"), scale);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterDepth"), (float) breakWaterDepth);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterOffset"), breakWaterOffset);
		OpenGL.glUniform2fARB(OpenGL.glGetUniformLocationARB(handle, "waveIntensity"), waveIntensityX, waveIntensityY);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "waveExponent"), (float) waveExponent);
	}

	@OriginalMember(owner = "client!er", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.initialized;
	}
}
