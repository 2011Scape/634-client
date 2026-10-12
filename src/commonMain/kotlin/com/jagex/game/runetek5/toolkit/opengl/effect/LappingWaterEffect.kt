package com.jagex.game.runetek5.toolkit.opengl.effect

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.GlArbProgram
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!em")
class LappingWaterEffect : MaterialEffectGL {

	@OriginalMember(owner = "client!em", name = "j", descriptor = "I")
	private var animatingProgression: Int = 0

	@OriginalMember(owner = "client!em", name = "m", descriptor = "Lclient!jv;")
	private var listUtils: GlRenderList? = null

	@OriginalMember(owner = "client!em", name = "p", descriptor = "F")
	private var texture3dAnimatingProgress: Float = 0f

	@OriginalMember(owner = "client!em", name = "r", descriptor = "Lclient!lc;")
	private val textureContainer: TextureContainerGL?

	@OriginalMember(owner = "client!em", name = "k", descriptor = "Lclient!pl;")
	private var program: GlArbProgram? = null

	@OriginalMember(owner = "client!em", name = "i", descriptor = "[F")
	private var noise: FloatArray? = null

	@OriginalMember(owner = "client!em", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glWaterNoiseTextureSet: TextureContainerGL?) : super(glToolkit) {
		this.textureContainer = glWaterNoiseTextureSet
		if (super.toolkit!!.arbVertexProgram && super.toolkit!!.maxTextureUnits >= 2) {
			this.program = GlArbProgram.create(super.toolkit, "!!ARBvp1.0\nOPTION  ARB_position_invariant;\nATTRIB  iPos         = vertex.position;\nATTRIB  iColour      = vertex.color;\nATTRIB  iTexCoord    = vertex.texcoord[0];\nOUTPUT  oColour      = result.color;\nOUTPUT  oTexCoord0   = result.texcoord[0];\nOUTPUT  oTexCoord1   = result.texcoord[1];\nOUTPUT  oFogCoord    = result.fogcoord;\nPARAM   time         = program.local[65];\nPARAM   turbulence   = program.local[64];\nPARAM   lightAmbient = program.local[66]; \nPARAM   pMatrix[4]   = { state.matrix.projection };\nPARAM   mvMatrix[4]  = { state.matrix.modelview };\nPARAM   ivMatrix[4]  = { state.matrix.texture[1] };\nPARAM   texMatrix[4]  = { state.matrix.texture[0] };\nPARAM   fNoise[64]   = { program.local[0..63] };\nTEMP    noise, viewPos, worldPos, texCoord;\nADDRESS noiseAddr;\nDP4   viewPos.x, mvMatrix[0], iPos;\nDP4   viewPos.y, mvMatrix[1], iPos;\nDP4   viewPos.z, mvMatrix[2], iPos;\nDP4   viewPos.w, mvMatrix[3], iPos;\nMOV   oFogCoord.x, -viewPos.z;\nDP4   worldPos.x, ivMatrix[0], viewPos;\nDP4   worldPos.y, ivMatrix[1], viewPos;\nDP4   worldPos.z, ivMatrix[2], viewPos;\nDP4   worldPos.w, ivMatrix[3], viewPos;\nADD   noise.x, worldPos.x, worldPos.z;SUB   noise.y, worldPos.z, worldPos.x;MUL   noise, noise, 0.0001220703125;\nFRC   noise, noise;\nMUL   noise, noise, 64;\nARL   noiseAddr.x, noise.x;\nMOV   noise.x, fNoise[noiseAddr.x].x;\nARL   noiseAddr.x, noise.y;\nMOV   noise.y, fNoise[noiseAddr.x].y;\nMUL   noise, noise, turbulence.x;\nDP4   texCoord.x, texMatrix[0], iTexCoord;\nDP4   texCoord.y, texMatrix[1], iTexCoord;\nADD   oTexCoord0.xy, texCoord, noise;\nMOV   oTexCoord0.z, 0;\nMOV   oTexCoord0.w, 1;\nMUL   oTexCoord1.xy, texCoord, 0.125;\nMOV   oTexCoord1.zw, time.xxxw;\nMUL   oColour.xyz, iColour, lightAmbient;\nMOV   oColour.w, iColour.w;\nEND")
			if (this.program != null) {
				@Pc(39) val noiseMapX: Array<IntArray?> = LappingWaterEffect.generateNoiseMap(0.4f)
				@Pc(50) val noiseMapY: Array<IntArray?> = LappingWaterEffect.generateNoiseMap(0.4f)
				this.noise = FloatArray(32768)
				@Pc(56) var offset: Int = 0
				for (@Pc(58) i in 0 until 256) {
					@Pc(64) val rowX: IntArray? = noiseMapX[i]
					@Pc(68) val rowY: IntArray? = noiseMapY[i]
					for (@Pc(70) j in 0 until 64) {
						this.noise!![offset++] = rowX!![j].toFloat() / 4096.0f
						this.noise!![offset++] = rowY!![j].toFloat() / 4096.0f
					}
				}
				this.buildDisplayList()
			}
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(IIB)V")
	override fun setEffectParams(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) effectParam2: Int) {
		if (this.listUtils == null) {
			return
		}
		super.toolkit!!.activateTexture(1)
		@Pc(81) var frame: Int
		if ((effectParam1 and 0x80) != 0) {
			super.toolkit!!.setActiveTexture(null)
		} else if ((effectParam2 and 0x1) != 1) {
			if (this.textureContainer!!.texture3dSupported) {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			} else {
				super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![0])
			}
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		} else if (this.textureContainer!!.texture3dSupported) {
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTexture3d)
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, this.texture3dAnimatingProgress, 0.0f, 0.0f, 1.0f)
		} else {
			frame = super.toolkit!!.lastTickMillis % 4000 * 16 / 4000
			super.toolkit!!.setActiveTexture(this.textureContainer.waveTextures2d!![frame])
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		}
		super.toolkit!!.activateTexture(0)
		if ((effectParam1 and 0x40) == 0) {
			LappingWaterEffect.SUN_COLOUR[2] = super.toolkit!!.sunColorBlue * super.toolkit!!.sunBrightness
			LappingWaterEffect.SUN_COLOUR[0] = super.toolkit!!.sunColorRed * super.toolkit!!.sunBrightness
			LappingWaterEffect.SUN_COLOUR[1] = super.toolkit!!.sunBrightness * super.toolkit!!.sunColorGreen
			OpenGL.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, LappingWaterEffect.SUN_COLOUR, 0)
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 66, 1.0f, 1.0f, 1.0f, 1.0f)
		}
		frame = effectParam1 and 0x3
		if (frame == 2) {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.05f, 1.0f, 1.0f, 1.0f)
		} else if (frame == 3) {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.1f, 1.0f, 1.0f, 1.0f)
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 64, 0.025f, 1.0f, 1.0f, 1.0f)
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(Lclient!ud;BI)V")
	override fun bindTexture(@OriginalArg(0) texture: GlTexture?, @OriginalArg(2) combineMode: Int) {
		super.toolkit!!.setActiveTexture(texture)
		super.toolkit!!.setColourOp(combineMode)
	}

	@OriginalMember(owner = "client!em", name = "e", descriptor = "(I)V")
	private fun buildDisplayList() {
		this.listUtils = GlRenderList(super.toolkit, 2)
		this.listUtils!!.newList(0)
		super.toolkit!!.activateTexture(1)
		super.toolkit!!.setTextureEnvColour(-16777216)
		super.toolkit!!.loadTextureCombineMode(260, 7681)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_CONSTANT)
		super.toolkit!!.activateTexture(0)
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, this.program!!.programId)
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		this.listUtils!!.endList()
		this.listUtils!!.newList(1)
		super.toolkit!!.activateTexture(1)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadIdentity()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.setColourOp(0)
		super.toolkit!!.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE)
		super.toolkit!!.activateTexture(0)
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
		OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB)
		this.listUtils!!.endList()
	}

	@OriginalMember(owner = "client!em", name = "c", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(I)V")
	override fun disable() {
		if (this.listUtils != null) {
			this.listUtils!!.callList('\u0001')
			super.toolkit!!.activateTexture(1)
			super.toolkit!!.setActiveTexture(null)
			super.toolkit!!.activateTexture(0)
		}
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZZ)V")
	override fun enable(@OriginalArg(0) lit: Boolean) {
		if (this.listUtils == null) {
			return
		}
		this.listUtils!!.callList('\u0000')
		super.toolkit!!.activateTexture(1)
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		OpenGL.glLoadMatrixf(super.toolkit!!.inverseModelViewMatrix.getColumnMajor(), 0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.toolkit!!.activateTexture(0)
		if (this.animatingProgression == super.toolkit!!.lastTickMillis) {
			return
		}
		@Pc(59) var offset: Int = super.toolkit!!.lastTickMillis % 5000 * 128 / 5000
		for (@Pc(61) param in 0 until 64) {
			OpenGL.glProgramLocalParameter4fvARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, param, this.noise, offset)
			offset += 2
		}
		if (this.textureContainer!!.texture3dSupported) {
			this.texture3dAnimatingProgress = (super.toolkit!!.lastTickMillis % 4000).toFloat() / 4000.0f
		} else {
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 65, 0.0f, 0.0f, 0.0f, 1.0f)
		}
		this.animatingProgression = super.toolkit!!.lastTickMillis
	}

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	companion object {
		@OriginalMember(owner = "client!ua", name = "a", descriptor = "(IIIIIZIFZ)[[I")
		@JvmStatic
		fun generateNoiseMap(@OriginalArg(7) noiseAmplitude: Float): Array<IntArray?> {
			@Pc(9) val noise: Array<IntArray?> = Array<IntArray?>(256) { IntArray(64) }
			@Pc(13) val textureOp34: TextureOpPerlinNoise = TextureOpPerlinNoise()
			textureOp34.noiseAmplitude = (noiseAmplitude * 4096.0f).toInt()
			textureOp34.numNoiseSteps = 3
			textureOp34.texCoordScaleX = 4
			textureOp34.texCoordScaleY = 4
			textureOp34.useFogEffect = false
			textureOp34.postDecode()
			TextureRaster.setDimensions(256, 64)
			for (@Pc(45) i in 0 until 256) {
				textureOp34.computeNoiseRow(i, noise[i])
			}
			return noise
		}

		@OriginalMember(owner = "client!bda", name = "z", descriptor = "[F")
		@JvmField
		val SUN_COLOUR: FloatArray = FloatArray(4)
	}
}
