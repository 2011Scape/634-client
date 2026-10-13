/*
 * Not part of the original client: 634HD (toolkit 6), copied from BreakingWaterEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdBreakingWaterEffect : HdMaterialEffect {

	private var initialized: Boolean = false

	private var loaded: Boolean = false

	private val textures: HdTextureContainer?

	private var shaders: HdProgram? = null

	constructor(glToolkit: HdToolkit?, glWaterNoiseTextureSet: HdTextureContainer?) : super(glToolkit) {
		this.textures = glWaterNoiseTextureSet
		if (this.textures!!.aClass85_Sub2_2 != null && super.toolkit!!.arbVertexShader && super.toolkit!!.arbFragmentShader) {
			val vertexShader: HdShader? = HdShader.compile(35633, super.toolkit, "uniform float time;\nuniform float scale;\nvarying vec3 wvVertex;\nvarying float waterDepth;\nvoid main() {\nwaterDepth = gl_MultiTexCoord0.z;\nvec4 ecVertex = gl_ModelViewMatrix*gl_Vertex;\nwvVertex.x = dot(gl_NormalMatrix[0], ecVertex.xyz);\nwvVertex.y = dot(gl_NormalMatrix[1], ecVertex.xyz);\nwvVertex.z = dot(gl_NormalMatrix[2], ecVertex.xyz);\ngl_TexCoord[0].x = dot(gl_TextureMatrix[0][0], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].y = dot(gl_TextureMatrix[0][1], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].z = time;\ngl_TexCoord[0].w = 1.0;\ngl_FogFragCoord = 1.0-clamp((gl_Fog.end+ecVertex.z)*gl_Fog.scale, 0.0, 1.0);\ngl_Position = ftransform();\n}\n")
			val fragmentShader: HdShader? = HdShader.compile(35632, super.toolkit, "varying vec3 wvVertex;\nvarying float waterDepth;\nuniform vec3 sunDir;\nuniform vec4 sunColour;\nuniform float sunExponent;\nuniform vec2 waveIntensity;\nuniform float waveExponent;\nuniform float breakWaterDepth;\nuniform float breakWaterOffset;\nuniform sampler3D normalSampler;\nuniform samplerCube envMapSampler;\nvoid main() {\nvec4 wnNormal = texture3D(normalSampler, gl_TexCoord[0].xyz).rbga;\nwnNormal.xyz = 2.0*wnNormal.xyz-1.0;\nvec3 wnVector = normalize(wvVertex);\nvec3 wnReflection = reflect(wnVector, wnNormal.xyz);\nvec3 envColour = textureCube(envMapSampler, wnReflection).rgb;\nvec4 specularColour = sunColour*pow(clamp(-dot(sunDir, wnReflection), 0.0, 1.0), sunExponent);\nfloat shoreFactor = clamp(waterDepth/breakWaterDepth-breakWaterOffset*wnNormal.w, 0.0, 1.0);\nfloat waveFactor = pow(1.0-shoreFactor, waveExponent)-0.5;\nwaveFactor = -4.0*waveFactor*waveFactor+1.0;\nfloat ndote = dot(wnVector, wnNormal.xyz);\nfloat fresnel = pow(1.0-abs(ndote), 1.0);\nvec4 surfaceColour = mix(vec4(envColour, fresnel*shoreFactor), (waveIntensity.x*wnNormal.wwww)+waveIntensity.y, waveFactor)+specularColour*shoreFactor;\ngl_FragColor = vec4(mix(surfaceColour.rgb, gl_Fog.color.rgb, gl_FogFragCoord), surfaceColour.a);\n}\n")
			this.shaders = HdProgram.link(super.toolkit, arrayOf<HdShader?>(vertexShader, fragmentShader))
			this.initialized = this.shaders != null
		}
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		if (!this.loaded) {
			super.toolkit!!.setActiveTexture(texture)
			super.toolkit!!.setColourOp(combineMode)
		}
	}

	override fun disable() {
		if (!this.loaded) {
			return
		}
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(null)
		super.toolkit!!.activateTexture(0)
		super.toolkit!!.setActiveTexture(null)
		HdGl.glUseProgramObjectARB(0L)
		this.loaded = false
	}

	override fun enable(lit: Boolean) {
		val environmentMap: HdCubeTexture? = super.toolkit!!.getCubeMap()
		if (!this.initialized || environmentMap == null) {
			return
		}
		val sunBoost: Float = (1.0f - kotlin.math.abs(super.toolkit!!.sunDirection[1])) * 2.0f + 1.0f
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(environmentMap)
		super.toolkit!!.activateTexture(0)
		super.toolkit!!.setActiveTexture(this.textures!!.aClass85_Sub2_2)
		val handle: Long = this.shaders!!.programHandle
		HdGl.glUseProgramObjectARB(handle)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(handle, "normalSampler"), 0)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(handle, "envMapSampler"), 1)
		HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(handle, "sunDir"), -super.toolkit!!.sunDirection[0], -super.toolkit!!.sunDirection[1], -super.toolkit!!.sunDirection[2])
		HdGl.glUniform4fARB(HdGl.glGetUniformLocationARB(handle, "sunColour"), super.toolkit!!.sunColorRed * sunBoost, super.toolkit!!.sunColorGreen * sunBoost, super.toolkit!!.sunColorBlue * sunBoost, 1.0f)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "sunExponent"), kotlin.math.abs(super.toolkit!!.sunDirection[1]) * 928.0f + 64.0f)
		this.loaded = true
	}

	override fun applyTextureCombine(lit: Boolean) {
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		if (!this.loaded) {
			return
		}
		val timeScale: Int = 0x1 shl (effectParam1 and 0x3)
		val scale: Float = (0x1 shl ((effectParam1 shr 3) and 0x7)).toFloat() / 32.0f
		val breakWaterDepth: Int = effectParam2 and 0xFFFF
		val breakWaterOffset: Float = ((effectParam2 shr 16) and 0x3).toFloat() / 8.0f
		val waveIntensityY: Float = ((effectParam2 shr 19) and 0xF).toFloat() / 16.0f
		val waveIntensityX: Float = ((effectParam2 shr 23) and 0xF).toFloat() / 16.0f
		val waveExponent: Int = (effectParam2 shr 27) and 0xF
		val handle: Long = this.shaders!!.programHandle
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "time"), (timeScale * super.toolkit!!.lastTickMillis % 40000).toFloat() / 40000.0f)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "scale"), scale)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "breakWaterDepth"), breakWaterDepth.toFloat())
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "breakWaterOffset"), breakWaterOffset)
		HdGl.glUniform2fARB(HdGl.glGetUniformLocationARB(handle, "waveIntensity"), waveIntensityX, waveIntensityY)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "waveExponent"), waveExponent.toFloat())
	}

	override fun isSupported(): Boolean {
		return this.initialized
	}
}
