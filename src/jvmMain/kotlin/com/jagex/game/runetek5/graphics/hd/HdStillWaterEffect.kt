/*
 * Not part of the original client: 634HD (toolkit 6), copied from StillWaterEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.Matrix43
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdStillWaterEffect : HdMaterialEffect {

	private var bound: Boolean = false

	private var calculateLights: Boolean = false

	private var isUsingFallbackTexture: Boolean = false

	private var waterNodepthColor: HdArbProgram? = null

	private var waterNodepthLit: HdArbProgram? = null

	private var waterDepthColor: HdArbProgram? = null

	private var waterDepthLit: HdArbProgram? = null

	private val initialized: Boolean

	private var openglTexture: Hd2DTexture? = null

	constructor(glToolkit: HdToolkit?) : super(glToolkit) {
		if (super.toolkit!!.arbVertexProgram) {
			this.waterNodepthColor = HdArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   tMatrix[4]   = { state.matrix.texture[0] };\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nTEMP    viewPos, fogFactor;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nMAD   fogFactor.y, iTexCoord.z, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMUL   fogFactor.x, fogFactor.x, fogFactor.y;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, iTexCoord.z;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nMOV   oColour, iColour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP3   oTexCoord0.x, tMatrix[0], iTexCoord;\nDP3   oTexCoord0.y, tMatrix[1], iTexCoord;\nMOV   oTexCoord0.zw, iTexCoord;\nEND\n")
			this.waterNodepthLit = HdArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iNormal      = vertex.normal;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   tMatrix[4]   = { state.matrix.texture[0] };\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nTEMP    viewPos, viewNormal, fogFactor, colour, ndotl;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nMAD   fogFactor.y, iTexCoord.z, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMUL   fogFactor.x, fogFactor.x, fogFactor.y;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, iTexCoord.z;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nDP3   viewNormal.x, mvMatrix[0], iNormal;\nDP3   viewNormal.y, mvMatrix[1], iNormal;\nDP3   viewNormal.z, mvMatrix[2], iNormal;\nDP3   ndotl.x, viewNormal, state.light[0].position;\nDP3   ndotl.y, viewNormal, state.light[1].position;\nMAX   ndotl, ndotl, 0;\nMOV   colour, state.lightmodel.ambient;\nMAD   colour, state.light[0].diffuse, ndotl.xxxx, colour;\nMAD   colour, state.light[1].diffuse, ndotl.yyyy, colour;\nMUL   oColour, iColour, colour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP3   oTexCoord0.x, tMatrix[0], iTexCoord;\nDP3   oTexCoord0.y, tMatrix[1], iTexCoord;\nMOV   oTexCoord0.zw, iTexCoord;\nEND\n")
			this.waterDepthColor = HdArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   texMatrix[4] = { state.matrix.texture[0] };\nTEMP    viewPos, fogFactor, depth;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nDP4   depth, waterPlane, viewPos;\nMAD   fogFactor.y, -depth, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, -depth;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nMOV   oColour, iColour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP4   oTexCoord0.x, texMatrix[0], iTexCoord;\nDP4   oTexCoord0.y, texMatrix[1], iTexCoord;\nDP4   oTexCoord0.z, texMatrix[2], iTexCoord;\nMOV   oTexCoord0.w, 1;\nEND\n")
			this.waterDepthLit = HdArbProgram.create(super.toolkit, "!!ARBvp1.0\nATTRIB  iPos         = vertex.position;\nATTRIB  iNormal      = vertex.normal;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oPos         = result.position;\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   fogParams    = program.local[0];\nPARAM   waterPlane   = program.local[1];\nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   texMatrix[4] = { state.matrix.texture[0] };\nTEMP    viewPos, viewNormal, fogFactor, depth, colour, ndotl;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nSUB   fogFactor.x, -viewPos.z, fogParams.x;\nMUL   fogFactor.x, fogFactor.x, 0.001953125;\nDP4   depth, waterPlane, viewPos;\nMAD   fogFactor.y, -depth, fogParams.z, fogParams.w;\nSUB   fogFactor.z, -viewPos.z, fogParams.y;\nMUL   fogFactor.z, fogFactor.z, 0.00390625;\nMIN   fogFactor, fogFactor, 1;\nMAX   fogFactor, fogFactor, 0;\nMUL   fogFactor.z, fogFactor.z, -depth;\nMAD   viewPos.xyz, waterPlane.xyzw, fogFactor.zzzz, viewPos.xyzw;\nMAX   oTexCoord1.xyz, fogFactor.xxxx, fogFactor.yyyy;\nMOV   oTexCoord1.w, 1;\nDP3   viewNormal.x, mvMatrix[0], iNormal;\nDP3   viewNormal.y, mvMatrix[1], iNormal;\nDP3   viewNormal.z, mvMatrix[2], iNormal;\nDP3   ndotl.x, viewNormal, state.light[0].position;\nDP3   ndotl.y, viewNormal, state.light[1].position;\nMAX   ndotl, ndotl, 0;\nMOV   colour, state.lightmodel.ambient;\nMAD   colour, state.light[0].diffuse, ndotl.xxxx, colour;\nMAD   colour, state.light[1].diffuse, ndotl.yyyy, colour;\nMUL   oColour, iColour, colour;\nDP4   oPos.x, pMatrix[0], viewPos;\nDP4   oPos.y, pMatrix[1], viewPos;\nDP4   oPos.z, pMatrix[2], viewPos;\nDP4   oPos.w, pMatrix[3], viewPos;\nMOV   oFogCoord.x, viewPos.z;\nDP4   oTexCoord0.x, texMatrix[0], iTexCoord;\nDP4   oTexCoord0.y, texMatrix[1], iTexCoord;\nDP4   oTexCoord0.z, texMatrix[2], iTexCoord;\nMOV   oTexCoord0.w, 1;\nEND\n")
			if ((this.waterDepthLit != null) and (this.waterNodepthColor != null) and (this.waterNodepthLit != null) and (this.waterDepthColor != null)) {
				this.openglTexture = Hd2DTexture(glToolkit, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_ALPHA, 2, 1, false, byteArrayOf(0, -1), OpenGLConstants.GL_ALPHA, false)
				this.openglTexture!!.setRepeat(false, false)
				this.initialized = true
			} else {
				this.initialized = false
			}
		} else {
			this.initialized = false
		}
	}

	fun bindProgram() {
		val modelViewMatrix: Matrix43? = super.toolkit!!.modelViewMatrix
		if (this.calculateLights) {
			HdGl.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, if (super.toolkit!!.waterHeight.inv() == Int.MIN_VALUE) this.waterNodepthLit!!.programId else this.waterDepthLit!!.programId)
		} else {
			HdGl.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, if (super.toolkit!!.waterHeight == Int.MAX_VALUE) this.waterNodepthColor!!.programId else this.waterDepthColor!!.programId)
		}
		modelViewMatrix!!.transformPlane(0.0f, 0.0f, -1.0f, super.toolkit!!.waterHeight.toFloat(), HdStillWaterEffect.waterPlane)
		HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 1, HdStillWaterEffect.waterPlane[0], HdStillWaterEffect.waterPlane[1], HdStillWaterEffect.waterPlane[2], HdStillWaterEffect.waterPlane[3])
		HdGl.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		this.bound = true
		this.updateFogParams()
	}

	override fun applyTextureCombine(lit: Boolean) {
	}

	override fun isSupported(): Boolean {
		return this.initialized
	}

	override fun enable(calculateLights: Boolean) {
		this.calculateLights = calculateLights
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(this.openglTexture)
		super.toolkit!!.loadTextureCombineMode(34165, 7681)
		super.toolkit!!.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_COLOR, 0)
		super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_ALPHA, 2)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS)
		super.toolkit!!.activateTexture(0)
		this.bindProgram()
	}

	fun updateFogParams() {
		if (!this.bound) {
			return
		}
		val fogFar: Int = super.toolkit!!.XA()
		val fogNear: Int = super.toolkit!!.i()
		val depthFadeStart: Float = fogFar.toFloat() - (fogFar - fogNear).toFloat() * 0.125f
		val blendFadeStart: Float = fogFar.toFloat() - (fogFar - fogNear).toFloat() * 0.25f
		HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, blendFadeStart, depthFadeStart, 1.0f / super.toolkit!!.waterDepth.toFloat(), super.toolkit!!.waterIntensity.toFloat() / 255.0f)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setTextureEnvColour(super.toolkit!!.waterFogColour)
		super.toolkit!!.activateTexture(0)
	}

	override fun disable() {
		if (this.bound) {
			HdGl.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
			HdGl.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB)
			HdGl.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
			this.bound = false
		}
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setActiveTexture(null)
		super.toolkit!!.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE)
		super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
		super.toolkit!!.setTexture(OpenGLConstants.GL_CONSTANT, OpenGLConstants.GL_SRC_ALPHA, 2)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		super.toolkit!!.activateTexture(0)
		if (this.isUsingFallbackTexture) {
			super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
			this.isUsingFallbackTexture = false
		}
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		if (texture != null) {
			if (this.isUsingFallbackTexture) {
				super.toolkit!!.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0)
				super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
				this.isUsingFallbackTexture = false
			}
			super.toolkit!!.setActiveTexture(texture)
			super.toolkit!!.setColourOp(combineMode)
		} else if (!this.isUsingFallbackTexture) {
			super.toolkit!!.setActiveTexture(super.toolkit!!.texture2d)
			super.toolkit!!.setColourOp(1)
			super.toolkit!!.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0)
			super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS)
			this.isUsingFallbackTexture = true
		}
	}

	companion object {
		@JvmField
		val waterPlane: FloatArray = floatArrayOf(0.0f, -1.0f, 0.0f, 0.0f)
	}
}
