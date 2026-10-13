/*
 * Not part of the original client: 634HD (toolkit 6), copied from ReflectiveRipplingWaterEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdReflectiveRipplingWaterEffect : HdMaterialEffect {

	private var initialized: Boolean = false

	private var active: Boolean = false

	private val textures: HdTextureContainer?

	private var shaderManager: HdProgram? = null

	constructor(glToolkit: HdToolkit?, glWaterNoiseTextureSet: HdTextureContainer?) : super(glToolkit) {
		this.textures = glWaterNoiseTextureSet
		if (this.textures!!.aClass85_Sub2_2 != null && super.toolkit!!.arbVertexShader && super.toolkit!!.arbFragmentShader) {
			val vertexShader: HdShader? = HdShader.compile(35633, super.toolkit, "uniform float time;\nuniform float scale;\nvarying vec3 wvVertex;\nvarying float waterDepth;\nvoid main() {\nwaterDepth = gl_MultiTexCoord0.z;\nvec4 ecVertex = gl_ModelViewMatrix*gl_Vertex;\nwvVertex.x = dot(gl_NormalMatrix[0], ecVertex.xyz);\nwvVertex.y = dot(gl_NormalMatrix[1], ecVertex.xyz);\nwvVertex.z = dot(gl_NormalMatrix[2], ecVertex.xyz);\ngl_TexCoord[0].x = dot(gl_TextureMatrix[0][0], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].y = dot(gl_TextureMatrix[0][1], gl_MultiTexCoord0)*scale;\ngl_TexCoord[0].z = time;\ngl_TexCoord[0].w = 1.0;\ngl_FogFragCoord = 1.0-clamp((gl_Fog.end+ecVertex.z)*gl_Fog.scale, 0.0, 1.0);\ngl_Position = ftransform();\n}\n")
			val fragmentShader: HdShader? = HdShader.compile(35632, super.toolkit, "varying vec3 wvVertex;\nvarying float waterDepth;\nuniform vec3 sunDir;\nuniform vec4 sunColour;\nuniform float sunExponent;\nuniform float breakWaterDepth;\nuniform float breakWaterOffset;\nuniform sampler3D normalSampler;\nuniform samplerCube envMapSampler;\nvoid main() {\nvec4 wnNormal = texture3D(normalSampler, gl_TexCoord[0].xyz).rbga;\nwnNormal.xyz = 2.0*wnNormal.xyz-1.0;\nvec3 wnVector = normalize(wvVertex);\nvec3 wnReflection = reflect(wnVector, wnNormal.xyz);\nvec3 envColour = textureCube(envMapSampler, wnReflection).rgb;\nvec4 specularColour = sunColour*pow(clamp(-dot(sunDir, wnReflection), 0.0, 1.0), sunExponent);\nfloat shoreFactor = clamp(waterDepth/breakWaterDepth-breakWaterOffset*wnNormal.w, 0.0, 1.0);\nfloat ndote = dot(wnVector, wnNormal.xyz);\nfloat fresnel = pow(1.0-abs(ndote), 2.0);\nvec4 surfaceColour = vec4(envColour, fresnel*shoreFactor)+specularColour*shoreFactor;\ngl_FragColor = vec4(mix(surfaceColour.rgb, gl_Fog.color.rgb, gl_FogFragCoord), surfaceColour.a);\n}\n")
			this.shaderManager = HdProgram.link(super.toolkit, arrayOf<HdShader?>(vertexShader, fragmentShader))
			this.initialized = this.shaderManager != null
		}
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		if (!this.active) {
			super.toolkit!!.setActiveTexture(texture)
			super.toolkit!!.setColourOp(combineMode)
		}
	}

	override fun applyTextureCombine(lit: Boolean) {
	}

	override fun isSupported(): Boolean {
		return false
	}

	override fun enable(lit: Boolean) {
		val environmentMap: HdCubeTexture? = super.toolkit!!.getCubeMap()
		if (!this.initialized || environmentMap == null) {
			return
		}
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(environmentMap)
		super.toolkit!!.activateTexture(0)
		super.toolkit!!.setActiveTexture(this.textures!!.aClass85_Sub2_2)
		val handle: Long = this.shaderManager!!.programHandle
		HdGl.glUseProgramObjectARB(handle)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(handle, "normalSampler"), 0)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(handle, "envMapSampler"), 1)
		HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(handle, "sunDir"), -super.toolkit!!.sunDirection[0], -super.toolkit!!.sunDirection[1], -super.toolkit!!.sunDirection[2])
		HdGl.glUniform4fARB(HdGl.glGetUniformLocationARB(handle, "sunColour"), super.toolkit!!.sunColorRed, super.toolkit!!.sunColorGreen, super.toolkit!!.sunColorBlue, 1.0f)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "sunExponent"), kotlin.math.abs(super.toolkit!!.sunDirection[1]) * 928.0f + 96.0f)
		this.active = true
	}

	override fun disable() {
		if (!this.active) {
			return
		}
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(null)
		super.toolkit!!.activateTexture(0)
		super.toolkit!!.setActiveTexture(null)
		HdGl.glUseProgramObjectARB(0L)
		this.active = false
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		if (!this.active) {
			return
		}
		val timeScale: Int = 0x1 shl (effectParam1 and 0x3)
		val scale: Float = (0x1 shl ((effectParam1 shr 3) and 0x7)).toFloat() / 32.0f
		val breakWaterDepth: Int = effectParam2 and 0xFFFF
		val breakWaterOffset: Float = ((effectParam2 shr 16) and 0x3).toFloat() / 8.0f
		val handle: Long = this.shaderManager!!.programHandle
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "time"), (timeScale * super.toolkit!!.lastTickMillis % 40000).toFloat() / 40000.0f)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "scale"), scale)
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "breakWaterDepth"), breakWaterDepth.toFloat())
		HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(handle, "breakWaterOffset"), breakWaterOffset)
	}
}
