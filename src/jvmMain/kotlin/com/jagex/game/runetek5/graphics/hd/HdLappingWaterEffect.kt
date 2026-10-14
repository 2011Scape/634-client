/*
 * Not part of the original client: 634HD (toolkit 6), copied from LappingWaterEffect (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 *
 * One change from the original: the wave texture (unit 1) is placed by world position (x, z / 4096,
 * an eighth of a repeat per tile, as on the ground) instead of by the texture coordinates times
 * 0.125. Water models (Al Kharid's oasis, made of a piece per tile) restart their texture
 * coordinates on every piece, so the waves broke at each piece's edge and the water showed a grid.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdLappingWaterEffect : HdMaterialEffect {

	private var animatingProgression: Int = 0

	private var listUtils: HdRenderList? = null

	private var texture3dAnimatingProgress: Float = 0f

	private val textureContainer: HdTextureContainer?

	private var program: HdArbProgram? = null

	private var noise: FloatArray? = null

	constructor(glToolkit: HdToolkit?, glWaterNoiseTextureSet: HdTextureContainer?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		if (super.toolkit!!.arbVertexProgram && super.toolkit!!.maxTextureUnits >= 2) {
			this.program = HdArbProgram.create(super.toolkit, "!!ARBvp1.0\nOPTION  ARB_position_invariant;\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   time         = program.local[65];\nPARAM   turbulence   = program.local[64];\nPARAM   lightAmbient = program.local[66]; \nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   ivMatrix[4]  = { state.matrix.texture[1] };\nPARAM   texMatrix[4]  = { state.matrix.texture[0] };\nPARAM   fNoise[64]   = { program.local[0..63] };\nTEMP    noise, viewPos, worldPos, texCoord;\nADDRESS noiseAddr;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nMOV   oFogCoord.x, -viewPos.z;\nDP4   worldPos.x, ivMatrix[0], viewPos;\nDP4   worldPos.y, ivMatrix[1], viewPos;\nDP4   worldPos.z, ivMatrix[2], viewPos;\nDP4   worldPos.w, ivMatrix[3], viewPos;\nADD   noise.x, worldPos.x, worldPos.z;SUB   noise.y, worldPos.z, worldPos.x;MUL   noise, noise, 0.0001220703125;\nFRC   noise, noise;\nMUL   noise, noise, 64;\nARL   noiseAddr.x, noise.x;\nMOV   noise.x, fNoise[noiseAddr.x].x;\nARL   noiseAddr.x, noise.y;\nMOV   noise.y, fNoise[noiseAddr.x].y;\nMUL   noise, noise, turbulence.x;\nDP4   texCoord.x, texMatrix[0], iTexCoord;\nDP4   texCoord.y, texMatrix[1], iTexCoord;\nADD   oTexCoord0.xy, texCoord, noise;\nMOV   oTexCoord0.z, 0;\nMOV   oTexCoord0.w, 1;\nMUL   oTexCoord1.xy, worldPos.xzzz, 0.000244140625;\nMOV   oTexCoord1.zw, time.xxxw;\nMUL   oColour.xyz, iColour, lightAmbient;\nMOV   oColour.w, iColour.w;\nEND")
			if (this.program != null) {
				val noiseMapX: Array<IntArray?> = HdLappingWaterEffect.generateNoiseMap(0.4f)
				val noiseMapY: Array<IntArray?> = HdLappingWaterEffect.generateNoiseMap(0.4f)
				this.noise = FloatArray(32768)
				var offset: Int = 0
				for (i in 0 until 256) {
					val rowX: IntArray? = noiseMapX[i]
					val rowY: IntArray? = noiseMapY[i]
					for (j in 0 until 64) {
						this.noise!![offset++] = rowX!![j].toFloat() / 4096.0f
						this.noise!![offset++] = rowY!![j].toFloat() / 4096.0f
					}
				}
				this.buildDisplayList()
			}
		}
	}

	override fun setEffectParams(effectParam1: Int, effectParam2: Int) {
		if (this.listUtils == null) {
			return
		}
		super.toolkit!!.activateTexture(1)
		var frame: Int
		if ((effectParam1 and 0x80) != 0) {
			super.toolkit!!.setActiveTexture(null)
		} else if ((effectParam2 and 0x1) != 1) {
			if (this.textureContainer!!.texture3dSupported) {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			} else {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![0])
			}
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		} else if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, this.texture3dAnimatingProgress, 0.0f, 0.0f, 1.0f)
		} else {
			frame = super.toolkit!!.lastTickMillis % 4000 * 16 / 4000
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![frame])
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		}
		super.toolkit!!.activateTexture(0)
		if ((effectParam1 and 0x40) == 0) {
			HdLappingWaterEffect.SUN_COLOUR[2] = super.toolkit!!.sunColorBlue * super.toolkit!!.sunBrightness
			HdLappingWaterEffect.SUN_COLOUR[0] = super.toolkit!!.sunColorRed * super.toolkit!!.sunBrightness
			HdLappingWaterEffect.SUN_COLOUR[1] = super.toolkit!!.sunBrightness * super.toolkit!!.sunColorGreen
			HdGl.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, HdLappingWaterEffect.SUN_COLOUR, 0)
		} else {
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, 1.0f, 1.0f, 1.0f, 1.0f)
		}
		frame = effectParam1 and 0x3
		if (frame == 2) {
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.05f, 1.0f, 1.0f, 1.0f)
		} else if (frame == 3) {
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.1f, 1.0f, 1.0f, 1.0f)
		} else {
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.025f, 1.0f, 1.0f, 1.0f)
		}
	}

	override fun bindTexture(texture: HdTexture?, combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	private fun buildDisplayList() {
		this.listUtils = HdRenderList(super.toolkit, 2)
		this.listUtils!!.newList(0)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setTextureEnvColour(-16777216)
		super.toolkit!!.loadTextureCombineMode(260, 7681)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_CONSTANT)
		super.toolkit!!.activateTexture(0)
		HdGl.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, this.program!!.programId)
		HdGl.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		this.listUtils!!.endList()
		this.listUtils!!.newList(1)
		super.toolkit!!.activateTexture(1)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadIdentity()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.setColourOp(0)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		super.toolkit!!.activateTexture(0)
		HdGl.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
		HdGl.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		HdGl.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB)
		this.listUtils!!.endList()
	}

	override fun isSupported(): Boolean {
		return true
	}

	override fun disable() {
		if (this.listUtils != null) {
			this.listUtils!!.callList('\u0001')
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.activateTexture(0)
		}
	}

	override fun enable(lit: Boolean) {
		if (this.listUtils == null) {
			return
		}
		this.listUtils!!.callList('\u0000')
		super.toolkit!!.activateTexture(1)
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		HdGl.glLoadMatrixf(super.toolkit!!.inverseModelViewMatrix.getColumnMajor(), 0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.activateTexture(0)
		if (this.animatingProgression == super.toolkit!!.lastTickMillis) {
			return
		}
		var offset: Int = super.toolkit!!.lastTickMillis % 5000 * 128 / 5000
		for (param in 0 until 64) {
			HdGl.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, param, this.noise, offset)
			offset += 2
		}
		if (this.textureContainer!!.texture3dSupported) {
			this.texture3dAnimatingProgress = (super.toolkit!!.lastTickMillis % 4000).toFloat() / 4000.0f
		} else {
			HdGl.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		}
		this.animatingProgression = super.toolkit!!.lastTickMillis
	}

	override fun applyTextureCombine(lit: Boolean) {
	}

	companion object {
		@JvmStatic
		fun generateNoiseMap(noiseAmplitude: Float): Array<IntArray?> {
			val noise: Array<IntArray?> = Array<IntArray?>(256) { IntArray(64) }
			val textureOp34: TextureOpPerlinNoise = TextureOpPerlinNoise()
			textureOp34.noiseAmplitude = (noiseAmplitude * 4096.0f).toInt()
			textureOp34.numNoiseSteps = 3
			textureOp34.texCoordScaleX = 4
			textureOp34.texCoordScaleY = 4
			textureOp34.useFogEffect = false
			textureOp34.postDecode()
			TextureRaster.setDimensions(256, 64)
			for (i in 0 until 256) {
				textureOp34.computeNoiseRow(i, noise[i])
			}
			return noise
		}

		@JvmField
		val SUN_COLOUR: FloatArray = FloatArray(4)
	}
}
