package com.jagex.game.runetek5.toolkit

import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.constants.PciVendorId
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaclib.memory.Stream
import jaggl.MapBuffer
import jaggl.OpenGL
import awt.Canvas
import awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.VertexDeclarationGL
import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.OpenGlArbArrayBuffer
import com.jagex.game.runetek5.OpenGlArbElementArrayBuffer
import com.jagex.game.runetek5.TexCoordGenMode
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.VertexAttribute
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.GlTransparentWaterPass
import com.jagex.game.runetek5.GlUnderwaterPass
import com.jagex.game.runetek5.GlslEnvironmentMappedWaterPass
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.CubeTextureSampler
import com.jagex.game.runetek5.RenderPass
import com.jagex.game.runetek5.graphics.texture.sampler.GlCubeMapTexture
import com.jagex.game.runetek5.graphics.texture.sampler.GlRectangleTexture
import com.jagex.game.runetek5.graphics.texture.sampler.GlTexture2D
import com.jagex.game.runetek5.graphics.texture.sampler.GlTexture3D
import com.jagex.game.runetek5.graphics.texture.TextureState
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode
import com.jagex.game.runetek5.toolkit.companion.PrimitiveDrawMode
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import com.jagex.game.runetek5.statics.D3DToolkitFactory
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.TextureCombineFunction
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.jvm.Synchronized
import util.Locale
import util.lowercase

@OriginalClass("client!bga")
class GlxToolkit : AbstractNativeToolkit {

	@OriginalMember(owner = "client!bga", name = "Di", descriptor = "J")
	private var lastGcTime: Long = 0L

	@OriginalMember(owner = "client!bga", name = "Fi", descriptor = "Z")
	private var enableVertexColor: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Gi", descriptor = "I")
	private var textureAllocationPosition: Int = 0

	@OriginalMember(owner = "client!bga", name = "Hi", descriptor = "Z")
	private var enableVertexArray: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Ii", descriptor = "Z")
	private var modelMatrixPushed: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Ji", descriptor = "Z")
	private var enableVertexNormal: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Li", descriptor = "Z")
	private var viewMatrixLoaded: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Sh", descriptor = "Lclient!uh;")
	private val displayListsToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "wi", descriptor = "Lclient!uh;")
	private val textureBuffersToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "yi", descriptor = "Lclient!uh;")
	private val texturesToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "zi", descriptor = "Lclient!uh;")
	private val frameBuffersToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "Ai", descriptor = "Lclient!uh;")
	private val renderBuffersToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "Bi", descriptor = "Lclient!uh;")
	private val programsToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "Ci", descriptor = "Lclient!uh;")
	private val shadersToDelete: Deque = Deque()

	@OriginalMember(owner = "client!bga", name = "Ei", descriptor = "[Lclient!bba;")
	private val activeArrayBuffers: Array<OpenGlArbArrayBuffer?> = arrayOfNulls<OpenGlArbArrayBuffer>(16)

	@OriginalMember(owner = "client!bga", name = "Ki", descriptor = "Lclient!jaggl/MapBuffer;")
	@JvmField
	val elementArrayMapBuffer: MapBuffer = MapBuffer()

	@OriginalMember(owner = "client!bga", name = "Mi", descriptor = "Lclient!jaggl/MapBuffer;")
	@JvmField
	val arrayMapBuffer: MapBuffer = MapBuffer()

	@OriginalMember(owner = "client!bga", name = "Wi", descriptor = "I")
	private val polygonCount: Int = 0

	@OriginalMember(owner = "client!bga", name = "Fh", descriptor = "Lclient!jaggl/OpenGL;")
	private var openGL: OpenGL? = null

	@OriginalMember(owner = "client!bga", name = "Oi", descriptor = "Ljava/lang/String;")
	private val gpuVendor: String

	@OriginalMember(owner = "client!bga", name = "Ni", descriptor = "Ljava/lang/String;")
	private val gpuManufactorer: String

	@OriginalMember(owner = "client!bga", name = "Vi", descriptor = "I")
	private val combinedOpenglVersion: Int

	@OriginalMember(owner = "client!bga", name = "Ri", descriptor = "Z")
	@JvmField
	var GLARBVertexBufferObject: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Qi", descriptor = "Z")
	private var GLARBTextureRectangle: Boolean = false

	@OriginalMember(owner = "client!bga", name = "Xi", descriptor = "Z")
	private val GLARBTextureNonPowerOfTwo: Boolean

	@OriginalMember(owner = "client!bga", name = "Si", descriptor = "Z")
	@JvmField
	val GLARBVertexShader: Boolean

	@OriginalMember(owner = "client!bga", name = "Ui", descriptor = "Z")
	@JvmField
	val GLARBVertexProgram: Boolean

	@OriginalMember(owner = "client!bga", name = "Ti", descriptor = "Z")
	@JvmField
	val GLARBFragmentShader: Boolean

	@OriginalMember(owner = "client!bga", name = "Yi", descriptor = "[I")
	@JvmField
	val textureSlots: IntArray

	@OriginalMember(owner = "client!bga", name = "Pi", descriptor = "I")
	@JvmField
	val pixelType: Int

	@OriginalMember(owner = "client!bga", name = "<init>", descriptor = "(Lclient!jaggl/OpenGL;Ljava/awt/Canvas;JLclient!d;Lclient!in;I)V")
	constructor(@OriginalArg(0) openGL: OpenGL?, @OriginalArg(1) canvas: Canvas?, @OriginalArg(2) contextHandle: Long, @OriginalArg(3) textureSource: MaterialProvider?, @OriginalArg(4) shaders: Js5ResourceProvider?, @OriginalArg(5) multiSampleCount: Int) : super(canvas, (contextHandle), textureSource, shaders, multiSampleCount, 1) {
		try {
			this.openGL = openGL
			this.openGL!!.b()
			this.gpuVendor = OpenGL.glGetString(OpenGLConstants.GL_VENDOR)!!.lowercase(util.defaultLocale())
			this.gpuManufactorer = OpenGL.glGetString(OpenGLConstants.GL_RENDERER)!!.lowercase(util.defaultLocale())
			if (this.gpuVendor.indexOf("microsoft") != -1 || this.gpuVendor.indexOf("brian paul") != -1 || this.gpuVendor.indexOf("mesa") != -1) {
				throw RuntimeException("")
			}
			@Pc(108) val string: String? = OpenGL.glGetString(OpenGLConstants.GL_VERSION)
			@Pc(116) val strings: Array<String?> = Utils.split(' ', string!!.replace('.', ' '))
			if (strings.size < 2) {
				throw RuntimeException("")
			}
			@Pc(137) var local137: Int
			try {
				@Pc(131) val majorVersion: Int = Utils.parseInt(strings[0]!!)
				val minorVersion: Int = Utils.parseInt(strings[1]!!)
				this.combinedOpenglVersion = majorVersion * 10 + minorVersion
			} catch (@Pc(147) ex: NumberFormatException) {
				throw RuntimeException("")
			}
			if (this.combinedOpenglVersion < 12) {
				throw RuntimeException("")
			} else if (!this.openGL!!.a("GL_ARB_multitexture")) {
				throw RuntimeException("")
			} else if (this.openGL!!.a("GL_ARB_texture_env_combine")) {
				@Pc(184) val maxTextureUnits = IntArray(1)
				OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_UNITS, maxTextureUnits, 0)
				this.maxConcurrentTextures = maxTextureUnits[0]
				if (this.maxConcurrentTextures < 2) {
					throw RuntimeException("")
				}
				this.maxActiveLights = 8
				this.GLARBVertexBufferObject = this.openGL!!.a("GL_ARB_vertex_buffer_object")
				this.GLARBMultisample = this.openGL!!.a("GL_ARB_multisample")
				this.GLARBTextureRectangle = this.openGL!!.a("GL_ARB_texture_rectangle")
				this.cubeMapSupported = this.openGL!!.a("GL_ARB_texture_cube_map")
				this.GLARBTextureNonPowerOfTwo = this.openGL!!.a("GL_ARB_texture_non_power_of_two")
				this.SUPPORTS_TEXTURE3D = this.openGL!!.a("GL_EXT_texture3D")
				this.GLARBVertexShader = this.openGL!!.a("GL_ARB_vertex_shader")
				this.GLARBVertexProgram = this.openGL!!.a("GL_ARB_vertex_program")
				this.GLARBFragmentShader = this.openGL!!.a("GL_ARB_fragment_shader")
				this.openGL!!.a("GL_ARB_fragment_program")
				this.textureSlots = IntArray(this.maxConcurrentTextures)
				this.pixelType = if (Stream.c()) 33639 else 5121
				if (this.gpuManufactorer.indexOf("radeon") != -1) {
					var radeonModel = 0
					@Pc(286) var isHd = false
					@Pc(288) var isXSeries = false
					@Pc(297) val strings2: Array<String?> = Utils.split(' ', this.gpuManufactorer.replace('/', ' '))
					for (@Pc(299) i in 0 until strings2.size) {
						@Pc(305) var string2: String? = strings2[i]
						try {
							if (string2!!.length > 0) {
								if (string2[0] == 'x' && string2.length >= 3 && Utils.isValidInteger(string2.substring(1, 3))) {
									isXSeries = true
									string2 = string2.substring(1)
								}
								if (string2 == "hd") {
									isHd = true
								} else {
									if (string2.startsWith("hd")) {
										isHd = true
										string2 = string2.substring(2)
									}
									if (string2.length >= 4 && Utils.isValidInteger(string2.substring(0, 4))) {
										radeonModel = Utils.parseInt(string2.substring(0, 4))
										break
									}
								}
							}
						} catch (@Pc(373) ex2: Exception) {
						}
					}
					if (!isXSeries && !isHd) {
						if (radeonModel >= 7000 && radeonModel <= 7999) {
							this.GLARBVertexBufferObject = false
						}
						if (radeonModel >= 7000 && radeonModel <= 9250) {
							this.SUPPORTS_TEXTURE3D = false
						}
					}
					this.GLARBTextureRectangle = this.GLARBTextureRectangle and this.openGL!!.a("GL_ARB_half_float_pixel")
				}
				this.gpuVendor.indexOf("intel")
				if (this.GLARBVertexBufferObject) {
					try {
						@Pc(427) val bufferIds = IntArray(1)
						OpenGL.glGenBuffersARB(1, bufferIds, 0)
					} catch (@Pc(433) ex3: Throwable) {
						throw RuntimeException("")
					}
				}
			} else {
				throw RuntimeException("")
			}
		} catch (@Pc(439) ex4: Throwable) {
			ex4.printStackTrace()
			this.free()
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(I)V")
	@Synchronized
	override fun tick(@OriginalArg(0) time: Int) {
		@Pc(7) var count = 0
		@Pc(11) val cycle: Int = time and Int.MAX_VALUE
		@Pc(19) var intNode: IntNode
		while (!this.textureBuffersToDelete.isEmpty()) {
			intNode = this.textureBuffersToDelete.removeFirst() as IntNode
			GlxToolkit.deleteIdBuffer[count++] = intNode.key.toInt()
			this.textureBuffersSizeOccupied -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteBuffersARB(count, GlxToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteBuffersARB(count, GlxToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.texturesToDelete.isEmpty()) {
			intNode = this.texturesToDelete.removeFirst() as IntNode
			GlxToolkit.deleteIdBuffer[count++] = intNode.key.toInt()
			this.texturesSizeOccupied -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteTextures(count, GlxToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteTextures(count, GlxToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.frameBuffersToDelete.isEmpty()) {
			intNode = this.frameBuffersToDelete.removeFirst() as IntNode
			GlxToolkit.deleteIdBuffer[count++] = intNode.value
			if (count == 1000) {
				OpenGL.glDeleteFramebuffersEXT(count, GlxToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteFramebuffersEXT(count, GlxToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.renderBuffersToDelete.isEmpty()) {
			intNode = this.renderBuffersToDelete.removeFirst() as IntNode
			GlxToolkit.deleteIdBuffer[count++] = intNode.key.toInt()
			this.renderBufferSizeOccupied -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteRenderbuffersEXT(count, GlxToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteRenderbuffersEXT(count, GlxToolkit.deleteIdBuffer, 0)
		}
		while (!this.displayListsToDelete.isEmpty()) {
			intNode = this.displayListsToDelete.removeFirst() as IntNode
			OpenGL.glDeleteLists(intNode.key.toInt(), intNode.value)
		}
		@Pc(214) var node: Node
		while (!this.programsToDelete.isEmpty()) {
			node = this.programsToDelete.removeFirst()!!
			OpenGL.glDeleteProgramARB(node.key.toInt())
		}
		while (!this.shadersToDelete.isEmpty()) {
			node = this.shadersToDelete.removeFirst()!!
			OpenGL.glDeleteObjectARB(node.key)
		}
		while (!this.displayListsToDelete.isEmpty()) {
			intNode = this.displayListsToDelete.removeFirst() as IntNode
			OpenGL.glDeleteLists(intNode.key.toInt(), intNode.value)
		}
		if (this.E() > 100663296 && this.lastGcTime + TimeUtils.MILLISECONDS_PER_MINUTE < Utils.currentTimeMillis()) {
			lang.gc()
			this.lastGcTime = Utils.currentTimeMillis()
		}
		super.tick(cycle)
	}

	@OriginalMember(owner = "client!bga", name = "j", descriptor = "(B)V")
	override fun setDepthRenderState() {
		OpenGL.glDepthMask(this.depthBufferingEnabled && this.depthMaskEnabled)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	override fun createVertexDeclaration(@OriginalArg(1) vertexDeclarationElements: Array<VertexDeclarationComponent?>?): VertexDeclaration {
		return VertexDeclarationGL(vertexDeclarationElements)
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(Z)V")
	override fun toggleSun() {
		if (this.sunEnabled) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHT0)
			OpenGL.glEnable(OpenGLConstants.GL_LIGHT1)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHT0)
			OpenGL.glDisable(OpenGLConstants.GL_LIGHT1)
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	override fun setSurface(@OriginalArg(0) `object`: Any?, @OriginalArg(2) canvas: Canvas?) {
		@Pc(10) val long2: Long? = `object` as Long?
		if (!this.openGL!!.setSurface(long2!!)) {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!bga", name = "t", descriptor = "(B)V")
	override fun uploadModelMatrix() {
		this.loadModelViewMatrix()
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	override fun setTextureColourSource(@OriginalArg(0) useAlpha: Boolean, @OriginalArg(2) sourceIndex: Int, @OriginalArg(3) state: TextureCombineSource?, @OriginalArg(4) invert: Boolean) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_SRC0_RGB, GlxToolkit.toGlCombineSource(state))
		if (useAlpha) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_OPERAND0_RGB, if (invert) OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA else OpenGLConstants.GL_SRC_ALPHA)
		} else {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_OPERAND0_RGB, if (invert) OpenGLConstants.GL_ONE_MINUS_SRC_COLOR else OpenGLConstants.GL_SRC_COLOR)
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	override fun supportsTextureFormat(@OriginalArg(0) type: DataType?, @OriginalArg(1) format: PixelFormat?): Boolean {
		return true
	}

	@OriginalMember(owner = "client!bga", name = "c", descriptor = "(Z)V")
	override fun uploadProjectionMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadMatrixf(this.activeProjectionSource, 0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!bga", name = "ya", descriptor = "()V")
	override fun ya() {
		this.setDepthMaskEnabled(true)
		OpenGL.glClear(OpenGLConstants.GL_DEPTH_BUFFER_BIT)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(JI)V")
	@Synchronized
	fun requestShaderDeletion(@OriginalArg(0) pointer: Long) {
		@Pc(7) val node = Node()
		node.key = pointer
		this.shadersToDelete.addLast(node)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(II)Lclient!vm;")
	override fun createDepthBuffer(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): DepthBuffer? {
		return null
	}

	@OriginalMember(owner = "client!bga", name = "K", descriptor = "(I)V")
	override fun setTextureAlphaStage() {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, TextureCombineFunction.toGlCombineFunction(this.textureAlphaCombineModes!![this.currentTextureSlot]))
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	override fun create3DTexture(@OriginalArg(2) pixelFormat: PixelFormat?, @OriginalArg(5) data: ByteArray?): Texture3DSampler {
		return GlTexture3D(this, pixelFormat, 128, 128, 16, data)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IBI)V")
	@Synchronized
	fun requestTextureDeletion(@OriginalArg(0) value: Int, @OriginalArg(2) pointer: Int) {
		@Pc(8) val intNode = IntNode(value)
		intNode.key = pointer.toLong()
		this.texturesToDelete.addLast(intNode)
	}

	@OriginalMember(owner = "client!bga", name = "r", descriptor = "(B)V")
	override fun enableLights() {
	}

	@OriginalMember(owner = "client!bga", name = "H", descriptor = "(I)V")
	override fun setFogColorAndIntensity() {
		GlxToolkit.scratchFloat4[0] = this.baseLightColorRed * this.intensity
		GlxToolkit.scratchFloat4[2] = this.intensity * this.baseLightColorBlue
		GlxToolkit.scratchFloat4[1] = this.baseLightColorGreen * this.intensity
		GlxToolkit.scratchFloat4[3] = 1.0f
		OpenGL.glLightModelfv(OpenGLConstants.GL_LIGHT_MODEL_AMBIENT, GlxToolkit.scratchFloat4, 0)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	override fun createNativeTextureSampler(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(4) pixels: ByteArray?, @OriginalArg(5) pixelFormat: PixelFormat?, @OriginalArg(7) mipmapping: Boolean): GpuTexture {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return GlTexture2D(this, pixelFormat, width, height, mipmapping, pixels, 0, 0)
		} else if (this.GLARBTextureRectangle) {
			return GlRectangleTexture(this, pixelFormat, width, height, pixels, 0, 0)
		} else {
			@Pc(51) val glTexture2D = GlTexture2D(this, pixelFormat, GLFormat.RGBA, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
			glTexture2D.upload(0, height, width, pixels, pixelFormat)
			return glTexture2D
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(BI)V")
	override fun setTexCoordIndex(@OriginalArg(1) index: Int) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!bf;I)V")
	override fun setTexCoordGeneration(@OriginalArg(0) mode: TexCoordGenMode?) {
		if (mode === TexCoordGenMode.aClass31_3) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R)
			return
		}
		@Pc(13) val genMode: Int = GlxToolkit.toGlTexGenMode(mode)
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(BZ)V")
	override fun toggleMultisampling(@OriginalArg(1) enabled: Boolean) {
		if (enabled) {
			OpenGL.glEnable(32925)
		} else {
			OpenGL.glDisable(32925)
		}
	}

	@OriginalMember(owner = "client!bga", name = "M", descriptor = "(I)V")
	override fun setTextureMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		if (TextureState.INITIALIZE === this.textureStates!![this.currentTextureSlot]) {
			OpenGL.glLoadIdentity()
		} else {
			OpenGL.glLoadMatrixf(this.textureMatrices!![this.currentTextureSlot]!!.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0)
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILclient!tc;I)V")
	override fun setStreamSource(@OriginalArg(1) vertexBuffer: NativeStream?, @OriginalArg(2) streamIndex: Int) {
		this.activeArrayBuffers[streamIndex] = vertexBuffer as OpenGlArbArrayBuffer?
	}

	@OriginalMember(owner = "client!bga", name = "F", descriptor = "(I)V")
	override fun setTextureColorStage() {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, TextureCombineFunction.toGlCombineFunction(this.textureRgbCombineModes!![this.currentTextureSlot]))
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(II)Lclient!he;")
	override fun method7268(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): Sprite? {
		return null
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(B)V")
	override fun unbindCurrentTexture() {
		@Pc(9) val target: Int = this.textureSlots[this.currentTextureSlot]
		if (target != 0) {
			this.textureSlots[this.currentTextureSlot] = 0
			OpenGL.glBindTexture(target, 0)
			OpenGL.glDisable(target)
		}
	}

	@OriginalMember(owner = "client!bga", name = "q", descriptor = "(B)V")
	override fun setupGraphics() {
		@Pc(10) var i: Int = this.maxConcurrentTextures - 1
		while (i >= 0) {
			OpenGL.glActiveTexture(i + OpenGLConstants.GL_TEXTURE0)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_MODE, OpenGLConstants.GL_COMBINE)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, OpenGLConstants.GL_MODULATE)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_RGB, OpenGLConstants.GL_CONSTANT)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, OpenGLConstants.GL_MODULATE)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_ALPHA, OpenGLConstants.GL_CONSTANT)
			i--
		}
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_ALPHA, OpenGLConstants.GL_PREVIOUS)
		OpenGL.glShadeModel(OpenGLConstants.GL_SMOOTH)
		OpenGL.glClearDepth(1.0f)
		OpenGL.glDepthFunc(OpenGLConstants.GL_LEQUAL)
		OpenGL.glPolygonMode(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_FILL)
		OpenGL.glEnable(OpenGLConstants.GL_CULL_FACE)
		OpenGL.glCullFace(OpenGLConstants.GL_BACK)
		OpenGL.glAlphaFunc(OpenGLConstants.GL_GREATER, 0.0f)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadIdentity()
		OpenGL.glColorMaterial(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_AMBIENT_AND_DIFFUSE)
		OpenGL.glEnable(OpenGLConstants.GL_COLOR_MATERIAL)
		@Pc(74) val black: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		for (@Pc(76) j in 0 until 8) {
			@Pc(88) val light: Int = j + 16384
			OpenGL.glLightfv(light, OpenGLConstants.GL_AMBIENT, black, 0)
			OpenGL.glLightf(light, OpenGLConstants.GL_CONSTANT_ATTENUATION, 0.0f)
			OpenGL.glLightf(light, OpenGLConstants.GL_LINEAR_ATTENUATION, 0.0f)
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_DENSITY, 0.95f)
		OpenGL.glFogi(OpenGLConstants.GL_FOG_MODE, OpenGLConstants.GL_LINEAR)
		OpenGL.glHint(OpenGLConstants.GL_FOG_HINT, OpenGLConstants.GL_FASTEST)
		this.openGL!!.setSwapInterval(0)
		super.setupGraphics()
	}

	@OriginalMember(owner = "client!bga", name = "g", descriptor = "(B)V")
	override fun toggleFog() {
		if (this.fogSupported && this.fogEnabled && this.fogDistance >= 0) {
			OpenGL.glEnable(OpenGLConstants.GL_FOG)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_FOG)
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	override fun createOffscreenSurface(@OriginalArg(0) surface: Sprite?, @OriginalArg(1) buffer: DepthBuffer?): OffscreenSurface? {
		return null
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	override fun createSurface(@OriginalArg(1) canvas: Canvas?): Any {
		@Pc(14) val handle: Long = this.openGL!!.prepareSurface(canvas)
		if (handle == -1L) {
			throw RuntimeException()
		}
		return (handle)
	}

	@OriginalMember(owner = "client!bga", name = "c", descriptor = "()Lclient!fia;")
	override fun renderer(): Renderer {
		@Pc(7) var vendor = -1
		if (this.gpuVendor.indexOf("nvidia") != -1) {
			vendor = PciVendorId.NVIDIA
		} else if (this.gpuVendor.indexOf("intel") != -1) {
			vendor = PciVendorId.INTEL
		} else if (this.gpuVendor.indexOf("ati") != -1) {
			vendor = PciVendorId.AMD
		}
		return Renderer(vendor, "OpenGL", this.combinedOpenglVersion, this.gpuManufactorer, 0L)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	override fun drawQuad(@OriginalArg(0) environmentSampler: EnvironmentSampler?, @OriginalArg(1) environmentSampler2: EnvironmentSampler?, @OriginalArg(2) alpha: Float, @OriginalArg(3) environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		return if (alpha < 0.5f) environmentSampler else environmentSampler2
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(III)V")
	@Synchronized
	fun requestTextureBufferDeletion(@OriginalArg(0) value: Int, @OriginalArg(1) pointer: Int) {
		@Pc(14) val intNode = IntNode(value)
		intNode.key = pointer.toLong()
		this.textureBuffersToDelete.addLast(intNode)
	}

	@OriginalMember(owner = "client!bga", name = "h", descriptor = "(Z)V")
	override fun setSunParameters() {
		GlxToolkit.scratchFloat4[2] = this.baseLightColorBlue * this.sunColorIntensity
		GlxToolkit.scratchFloat4[3] = 1.0f
		GlxToolkit.scratchFloat4[0] = this.baseLightColorRed * this.sunColorIntensity
		GlxToolkit.scratchFloat4[1] = this.baseLightColorGreen * this.sunColorIntensity
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0)
		GlxToolkit.scratchFloat4[0] = this.baseLightColorRed * -this.antiSunColorIntensity
		GlxToolkit.scratchFloat4[2] = -this.antiSunColorIntensity * this.baseLightColorBlue
		GlxToolkit.scratchFloat4[3] = 1.0f
		GlxToolkit.scratchFloat4[1] = -this.antiSunColorIntensity * this.baseLightColorGreen
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0)
	}

	@OriginalMember(owner = "client!bga", name = "U", descriptor = "(I)V")
	private fun loadModelViewMatrix() {
		if (this.modelMatrixPushed) {
			OpenGL.glPopMatrix()
		}
		if (this.matrixMode!!.isSceneMode()) {
			if (!this.viewMatrixLoaded) {
				OpenGL.glLoadMatrixf(this.flippedViewMatrix!!.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0)
				this.viewMatrixLoaded = true
				this.setLightPosition()
				this.updateSceneLights()
			}
			if (this.isModelMatrixIdentity) {
				this.modelMatrixPushed = false
			} else {
				OpenGL.glPushMatrix()
				OpenGL.glMultMatrixf(this.modelMatrix43!!.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0)
				this.modelMatrixPushed = true
			}
		} else if (this.isModelMatrixIdentity) {
			OpenGL.glLoadIdentity()
			this.modelMatrixPushed = false
		} else {
			OpenGL.glLoadMatrixf(this.modelMatrix43!!.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0)
			this.modelMatrixPushed = false
		}
	}

	@OriginalMember(owner = "client!bga", name = "h", descriptor = "()V")
	override fun stop() {
		super.stop()
		if (this.openGL != null) {
			this.openGL!!.a()
			this.openGL!!.release()
			this.openGL = null
		}
	}

	@OriginalMember(owner = "client!bga", name = "o", descriptor = "(I)V")
	override fun configureFog() {
		this.fogEnd = (this.camFar - this.fogOffset).toFloat()
		this.fogStart = (-this.fogDistance).toFloat() + this.fogEnd
		if (this.fogStart < this.camNear.toFloat()) {
			this.fogStart = this.camNear.toFloat()
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_START, this.fogStart)
		OpenGL.glFogf(OpenGLConstants.GL_FOG_END, this.fogEnd)
		GlxToolkit.scratchFloat4[2] = (this.fogColorRgb and 0xFF).toFloat() / 255.0f
		GlxToolkit.scratchFloat4[0] = (this.fogColorRgb and 0xFF0000).toFloat() / 1.671168E7f
		GlxToolkit.scratchFloat4[1] = (this.fogColorRgb and 0xFF00).toFloat() / 65280.0f
		OpenGL.glFogfv(OpenGLConstants.GL_FOG_COLOR, GlxToolkit.scratchFloat4, 0)
	}

	@OriginalMember(owner = "client!bga", name = "o", descriptor = "()Z")
	override fun hasBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!bga", name = "u", descriptor = "(I)V")
	override fun toggleLights() {
		if (this.lightingEnabled && !this.forceLightsDisabled) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHTING)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHTING)
		}
	}

	@OriginalMember(owner = "client!bga", name = "G", descriptor = "(I)V")
	override fun toggleScissorTest() {
		if (this.boundsCalculated) {
			OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		}
	}

	@OriginalMember(owner = "client!bga", name = "D", descriptor = "(B)V")
	override fun setViewport() {
		OpenGL.glViewport(this.viewportOffsetX, this.viewportOffsetY, this.surfaceWidth, this.surfaceHeight)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IIII)V")
	override fun method7261(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	override fun drawIndexedPrimitive(@OriginalArg(0) drawMode: PrimitiveType?, @OriginalArg(1) primitiveCount: Int, @OriginalArg(2) gpuIndexBuffer: GpuIndexBuffer?, @OriginalArg(3) minIndex: Int, @OriginalArg(4) startIndex: Int, @OriginalArg(5) vertexCount: Int) {
		@Pc(23) val glMode: Byte
		@Pc(21) val indexCount: Int
		if (PrimitiveDrawMode.LINES === drawMode) {
			glMode = 1
			indexCount = primitiveCount * 2
		} else if (drawMode === PrimitiveType.aClass134_5) {
			indexCount = primitiveCount + 1
			glMode = 3
		} else if (drawMode === PrimitiveType.aClass134_6) {
			glMode = 4
			indexCount = primitiveCount * 3
		} else if (PrimitiveType.aClass134_3 === drawMode) {
			glMode = 6
			indexCount = primitiveCount + 2
		} else if (drawMode === PrimitiveType.aClass134_4) {
			indexCount = primitiveCount + 2
			glMode = 5
		} else {
			indexCount = primitiveCount
			glMode = 0
		}
		@Pc(74) val indexType: DataType = gpuIndexBuffer!!.getDataType()!!
		@Pc(77) val glIndexBuffer: OpenGlArbElementArrayBuffer = gpuIndexBuffer as OpenGlArbElementArrayBuffer
		glIndexBuffer.bind()
		OpenGL.glDrawElements(glMode.toInt(), indexCount, DataType.getGlType(indexType), glIndexBuffer.getBufferAddress() + (startIndex * indexType.bytesPerEntry).toLong())
	}

	@OriginalMember(owner = "client!bga", name = "e", descriptor = "(B)V")
	override fun updateSceneLights() {
		this.resetModelMatrix()
		@Pc(18) var lightIndex: Int
		lightIndex = 0
		while (this.lastSceneLightCount > lightIndex) {
			@Pc(25) val pointLight: PointLight = this.lightSources!![lightIndex]!!
			@Pc(29) val range: Int = pointLight.getColour()
			@Pc(33) val glLight: Int = lightIndex + 16386
			@Pc(39) val intensityScale: Float = pointLight.getIntensity() / 255.0f
			GlxToolkit.scratchFloat4[0] = pointLight.getX().toFloat()
			GlxToolkit.scratchFloat4[1] = pointLight.getY().toFloat()
			GlxToolkit.scratchFloat4[2] = pointLight.getZ().toFloat()
			GlxToolkit.scratchFloat4[3] = 1.0f
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_POSITION, GlxToolkit.scratchFloat4, 0)
			GlxToolkit.scratchFloat4[2] = (range and 0xFF).toFloat() * intensityScale
			GlxToolkit.scratchFloat4[1] = intensityScale * ((range shr 8) and 0xFF).toFloat()
			GlxToolkit.scratchFloat4[0] = ((range shr 16) and 0xFF).toFloat() * intensityScale
			GlxToolkit.scratchFloat4[3] = 1.0f
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0)
			OpenGL.glLightf(glLight, OpenGLConstants.GL_QUADRATIC_ATTENUATION, 1.0f / (pointLight.getRadius() * pointLight.getRadius()).toFloat())
			OpenGL.glEnable(glLight)
			lightIndex++
		}
		while (this.currentSceneLightCount > lightIndex) {
			OpenGL.glDisable(lightIndex + OpenGLConstants.GL_LIGHT2)
			lightIndex++
		}
		super.updateSceneLights()
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	override fun resizeSurface(@OriginalArg(1) canvas: Canvas?, @OriginalArg(2) `object`: Any?) {
		@Pc(12) val long2: Long? = `object` as Long?
		this.openGL!!.surfaceResized(long2!!)
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "()V")
	override fun finish() {
		OpenGL.glFinish()
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	override fun supports3DTextureFormat(@OriginalArg(1) format: PixelFormat?, @OriginalArg(2) type: DataType?): Boolean {
		return true
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IILclient!hj;Z)V")
	override fun drawPrimitive(@OriginalArg(0) primitiveCount: Int, @OriginalArg(1) first: Int, @OriginalArg(2) drawMode: PrimitiveType?) {
		@Pc(22) val glMode: Byte
		@Pc(26) val vertexCount: Int
		if (drawMode === PrimitiveDrawMode.LINES) {
			vertexCount = primitiveCount * 2
			glMode = 1
		} else if (PrimitiveType.aClass134_5 === drawMode) {
			vertexCount = primitiveCount + 1
			glMode = 3
		} else if (PrimitiveType.aClass134_6 === drawMode) {
			glMode = 4
			vertexCount = primitiveCount * 3
		} else if (drawMode === PrimitiveType.aClass134_3) {
			glMode = 6
			vertexCount = primitiveCount + 2
		} else if (PrimitiveType.aClass134_4 === drawMode) {
			vertexCount = primitiveCount + 2
			glMode = 5
		} else {
			vertexCount = primitiveCount
			glMode = 0
		}
		OpenGL.glDrawArrays(glMode.toInt(), first, vertexCount)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "([[IZII)Lclient!nj;")
	override fun generateNativeCubeTexture(@OriginalArg(0) pixels: Array<IntArray?>?, @OriginalArg(1) mipmapping: Boolean, @OriginalArg(3) size: Int): CubeTextureSampler {
		return GlCubeMapTexture(this, size, mipmapping, pixels)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	override fun createNativeTexture(@OriginalArg(0) pixelFormat: PixelFormat?, @OriginalArg(1) width: Int, @OriginalArg(3) pixels: FloatArray?, @OriginalArg(4) mipmapping: Boolean, @OriginalArg(7) height: Int): GpuTexture {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return GlTexture2D(this, pixelFormat, width, height, mipmapping, pixels, 0, 0)
		} else if (this.GLARBTextureRectangle) {
			return GlRectangleTexture(this, pixelFormat, width, height, pixels, 0, 0)
		} else {
			@Pc(47) val glTexture2D = GlTexture2D(this, pixelFormat, DataType.aClass26_8, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
			glTexture2D.updateFloatData(pixels, pixelFormat, width, height)
			return glTexture2D
		}
	}

	@OriginalMember(owner = "client!bga", name = "m", descriptor = "()Z")
	override fun bloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IB)Lclient!fj;")
	override fun createRenderPass(@OriginalArg(0) effect: Int): RenderPass? {
		if (effect == 3) {
			return GlUnderwaterPass(this, this.shaderResource)
		} else if (effect == 4) {
			return GlTransparentWaterPass(this, this.shaderResource, this.waterTexture)
		} else if (effect == 8) {
			return GlslEnvironmentMappedWaterPass(this, this.shaderResource, this.waterTexture)
		} else {
			return super.createRenderPass(effect)
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	override fun createTextureSampler(@OriginalArg(0) width: Int, @OriginalArg(1) pixels: IntArray?, @OriginalArg(2) mipmapping: Boolean, @OriginalArg(4) offset: Int, @OriginalArg(5) rowLength: Int, @OriginalArg(6) height: Int): GpuTexture {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return GlTexture2D(this, width, height, mipmapping, pixels, 0, rowLength)
		} else if (this.GLARBTextureRectangle) {
			return GlRectangleTexture(this, width, height, pixels, 0, rowLength)
		} else {
			@Pc(49) val glTexture2D = GlTexture2D(this, GLFormat.HDR, GLFormat.RGBA, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
			glTexture2D.updateTextureRegion(rowLength, pixels, 0, width, 0, height)
			return glTexture2D
		}
	}

	@OriginalMember(owner = "client!bga", name = "F", descriptor = "(II)V")
	override fun F(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int) {
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	override fun flip(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		this.openGL!!.swapBuffers()
	}

	@OriginalMember(owner = "client!bga", name = "q", descriptor = "()V")
	override fun renderLastFrame() {
		if (this.surfaceWidth <= 0 && this.surfaceHeight <= 0) {
			return
		}
		@Pc(19) val clipLeft: Int = this.cLeft
		@Pc(22) val clipRight: Int = this.cRight
		@Pc(25) val clipTop: Int = this.cTop
		@Pc(28) val clipBottom: Int = this.cBottom
		this.la()
		OpenGL.glReadBuffer(OpenGLConstants.GL_FRONT)
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.setOrthoMatrixMode()
		this.toggleFog(false)
		this.toggleLights(false)
		this.toggleDepthTest(false)
		this.setDepthMaskEnabled(false)
		this.setTextureSampler(null)
		this.setActiveMaterial(false, false, -2)
		this.setColourType(1)
		this.toggleBlending(0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadIdentity()
		OpenGL.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadIdentity()
		OpenGL.glRasterPos2i(0, 0)
		OpenGL.glCopyPixels(0, 0, this.surfaceWidth, this.surfaceHeight, OpenGLConstants.GL_COLOR)
		OpenGL.glFlush()
		OpenGL.glReadBuffer(OpenGLConstants.GL_BACK)
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.KA(clipLeft, clipTop, clipRight, clipBottom)
	}

	@OriginalMember(owner = "client!bga", name = "Q", descriptor = "(I)V")
	override fun toggleBlending() {
		if (this.enableBlending) {
			OpenGL.glEnable(OpenGLConstants.GL_BLEND)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_BLEND)
		}
	}

	@OriginalMember(owner = "client!bga", name = "l", descriptor = "(II)V")
	@Synchronized
	fun destroyProgram(@OriginalArg(1) programHandle: Int) {
		@Pc(7) val node = Node()
		node.key = programHandle.toLong()
		this.programsToDelete.addLast(node)
	}

	@OriginalMember(owner = "client!bga", name = "p", descriptor = "(I)V")
	override fun toggleDepthTest() {
		if (this.enableDepthTest) {
			OpenGL.glEnable(OpenGLConstants.GL_DEPTH_TEST)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_DEPTH_TEST)
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	override fun setTextureAlphaStage(@OriginalArg(0) state: TextureCombineSource?, @OriginalArg(1) sourceIndex: Int) {
		OpenGL.glTexEnvi(8960, sourceIndex + 34184, GlxToolkit.toGlCombineSource(state))
		OpenGL.glTexEnvi(8960, sourceIndex + 34200, 770)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	override fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		this.flip(x, y)
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILclient!np;)V")
	override fun setVertexDeclaration(@OriginalArg(1) vertexDeclaration: VertexDeclaration?) {
		@Pc(9) val vertexDeclarationElements: Array<VertexDeclarationComponent?> = (vertexDeclaration as VertexDeclarationGL).vertexDeclarationElements!!
		@Pc(11) var texCoordUnit = 0
		@Pc(13) var hasColour = false
		@Pc(15) var hasNormal = false
		@Pc(17) var hasPosition = false
		for (@Pc(19) i in 0 until vertexDeclarationElements.size) {
			@Pc(25) val vertexDeclarationElement: VertexDeclarationComponent = vertexDeclarationElements[i]!!
			@Pc(30) val arrayBuffer: OpenGlArbArrayBuffer = this.activeArrayBuffers[i]!!
			@Pc(32) var offset = 0
			@Pc(36) val stride: Int = arrayBuffer.getStride()
			@Pc(40) val address: Long = arrayBuffer.getBufferAddress()
			arrayBuffer.bind()
			@Pc(45) var j = 0
			while (vertexDeclarationElement.getSize() > j) {
				@Pc(52) val action: VertexAttribute = vertexDeclarationElement.getAction(j)!!
				if (action === VertexDeclarationAction.POSITION) {
					OpenGL.glVertexPointer(3, OpenGLConstants.GL_FLOAT, stride, offset.toLong() + address)
					hasPosition = true
				} else if (VertexDeclarationAction.NORMAL_POINTER_ACTION === action) {
					hasNormal = true
					OpenGL.glNormalPointer(5126, stride, offset.toLong() + address)
				} else if (action === VertexDeclarationAction.COLOR) {
					OpenGL.glColorPointer(4, OpenGLConstants.GL_UNSIGNED_BYTE, stride, offset.toLong() + address)
					hasColour = true
				} else if (VertexDeclarationAction.aClass69_4 === action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0)
					OpenGL.glTexCoordPointer(1, OpenGLConstants.GL_FLOAT, stride, offset.toLong() + address)
				} else if (VertexDeclarationAction.TEXCOORD === action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0)
					OpenGL.glTexCoordPointer(2, OpenGLConstants.GL_FLOAT, stride, offset.toLong() + address)
				} else if (VertexDeclarationAction.aClass69_6 === action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0)
					OpenGL.glTexCoordPointer(3, OpenGLConstants.GL_FLOAT, stride, offset.toLong() + address)
				} else if (action === VertexDeclarationAction.aClass69_7) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0)
					OpenGL.glTexCoordPointer(4, OpenGLConstants.GL_FLOAT, stride, address + offset.toLong())
				}
				offset += action.pointerOffsetStep
				j++
			}
		}
		if (this.enableVertexArray != hasPosition) {
			if (hasPosition) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
			}
			this.enableVertexArray = hasPosition
		}
		if (this.enableVertexNormal != hasNormal) {
			if (hasNormal) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
			}
			this.enableVertexNormal = hasNormal
		}
		if (hasColour != this.enableVertexColor) {
			if (hasColour) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_COLOR_ARRAY)
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_COLOR_ARRAY)
			}
			this.enableVertexColor = hasColour
		}
		@Pc(258) var unit: Int
		if (this.textureAllocationPosition < texCoordUnit) {
			unit = this.textureAllocationPosition
			while (unit < texCoordUnit) {
				OpenGL.glClientActiveTexture(unit + OpenGLConstants.GL_TEXTURE0)
				OpenGL.glEnableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
				unit++
			}
			this.textureAllocationPosition = texCoordUnit
		} else if (this.textureAllocationPosition > texCoordUnit) {
			unit = texCoordUnit
			while (unit < this.textureAllocationPosition) {
				OpenGL.glClientActiveTexture(unit + OpenGLConstants.GL_TEXTURE0)
				OpenGL.glDisableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
				unit++
			}
			this.textureAllocationPosition = texCoordUnit
			return
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(IIIID)V")
	override fun b(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depthOffset: Double) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IZ)Lclient!uq;")
	override fun createIndexBuffer(@OriginalArg(1) drawStream: Boolean): GpuIndexBuffer {
		return OpenGlArbElementArrayBuffer(this, DataType.aClass26_5, drawStream)
	}

	@OriginalMember(owner = "client!bga", name = "I", descriptor = "()I")
	override fun I(): Int {
		return this.polygonCount
	}

	@OriginalMember(owner = "client!bga", name = "j", descriptor = "(Z)V")
	override fun activateCurrentTexture() {
		OpenGL.glActiveTexture(this.currentTextureSlot + OpenGLConstants.GL_TEXTURE0)
	}

	@OriginalMember(owner = "client!bga", name = "z", descriptor = "(I)V")
	override fun uploadViewMatrix() {
		this.viewMatrixLoaded = false
		this.loadModelViewMatrix()
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(FFF)V")
	override fun setBloomParams(@OriginalArg(0) whitePoint: Float, @OriginalArg(1) bloom: Float, @OriginalArg(2) brightpass: Float) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	override fun destroySurface(@OriginalArg(0) canvas: Canvas?, @OriginalArg(2) `object`: Any?) {
		@Pc(6) val long2: Long? = `object` as Long?
		this.openGL!!.releaseSurface(canvas, long2!!)
	}

	@OriginalMember(owner = "client!bga", name = "P", descriptor = "(I)V")
	override fun setTextureEnvironmentColor() {
		GlxToolkit.scratchFloat4[1] = (this.envColor and 0xFF00).toFloat() / 65280.0f
		GlxToolkit.scratchFloat4[2] = (this.envColor and 0xFF).toFloat() / 255.0f
		GlxToolkit.scratchFloat4[3] = (this.envColor ushr 24).toFloat() / 255.0f
		GlxToolkit.scratchFloat4[0] = (this.envColor and 0xFF0000).toFloat() / 1.671168E7f
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, GlxToolkit.scratchFloat4, 0)
	}

	@OriginalMember(owner = "client!bga", name = "x", descriptor = "(I)V")
	override fun setLightPosition() {
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_POSITION, this.sunPosition, 0)
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_POSITION, this.lightPosition, 0)
	}

	@OriginalMember(owner = "client!bga", name = "GA", descriptor = "(I)V")
	override fun GA(@OriginalArg(0) colour: Int) {
		OpenGL.glClearColor((colour and 0xFF0000).toFloat() / 1.671168E7f, (colour and 0xFF00).toFloat() / 65280.0f, (colour and 0xFF).toFloat() / 255.0f, (colour ushr 24).toFloat() / 255.0f)
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
	}

	@OriginalMember(owner = "client!bga", name = "p", descriptor = "()V")
	override fun restoreSurface() {
	}

	@OriginalMember(owner = "client!bga", name = "E", descriptor = "(B)V")
	override fun toggleAlpha() {
		if (this.enableAlphaTest) {
			OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_ALPHA_TEST)
		}
	}

	@OriginalMember(owner = "client!bga", name = "A", descriptor = "()V")
	override fun stopBloom() {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	override fun createNativeTexture2D(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(3) pixelActionFormat: DataType?, @OriginalArg(4) pixelFormat: PixelFormat?): GpuTexture {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return GlTexture2D(this, pixelFormat, pixelActionFormat, width, height)
		} else if (this.GLARBTextureRectangle) {
			return GlRectangleTexture(this, pixelFormat, pixelActionFormat, width, height)
		} else {
			return GlTexture2D(this, pixelFormat, pixelActionFormat, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height))
		}
	}

	@OriginalMember(owner = "client!bga", name = "v", descriptor = "()V")
	override fun compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!bga", name = "w", descriptor = "(I)V")
	override fun setScissorRect() {
		OpenGL.glScissor(this.viewportOffsetX + this.cLeft, -this.cBottom + (this.viewportOffsetY - -this.surfaceHeight), -this.cLeft + this.cRight, this.cBottom - this.cTop)
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(Z)V")
	override fun method7250(@OriginalArg(0) enabled: Boolean) {
	}

	@OriginalMember(owner = "client!bga", name = "S", descriptor = "(I)V")
	override fun blendDrawBuffer() {
		if (BlendFuncMode.ALPHA_BLENDING === this.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA)
		} else if (BlendFuncMode.OPAQUE_BLENDING === this.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_ONE, OpenGLConstants.GL_ONE)
		} else if (BlendFuncMode.COLOR_BLENDING === this.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_DST_COLOR, OpenGLConstants.GL_ONE)
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(ZI)Lclient!tc;")
	override fun createStream(@OriginalArg(0) drawStream: Boolean): NativeStream {
		return OpenGlArbArrayBuffer(this, drawStream)
	}

	@OriginalMember(owner = "client!bga", name = "i", descriptor = "(Z)F")
	override fun getPixelOffset(): Float {
		return 0.0f
	}

	@OriginalMember(owner = "client!bga", name = "na", descriptor = "(IIII)[I")
	override fun na(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int): IntArray {
		@Pc(10) val pixels = IntArray(height * width)
		for (@Pc(12) i in 0 until height) {
			OpenGL.glReadPixelsi(x, this.surfaceHeight - i - y - 1, width, 1, OpenGLConstants.GL_BGRA, this.pixelType, pixels, width * i)
		}
		return pixels
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!is;)V")
	override fun swapSurface(@OriginalArg(0) surface: OffscreenSurface?) {
	}

	companion object {
		@OriginalMember(owner = "client!lt", name = "a", descriptor = "(Ljava/awt/Canvas;Lclient!d;Lclient!in;II)Lclient!ha;")
		@JvmStatic
		fun createToolkit(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) shaders: Js5ResourceProvider?, @OriginalArg(4) multiSampleCount: Int): Toolkit {
			if (!D3DToolkitFactory.loadHw3dLibraries()) {
				throw RuntimeException("")
			} else if (NativeLibraryOwner.loadLibrary("jaggl")) {
				@Pc(26) val openGL = OpenGL()
				@Pc(36) val contextHandle: Long = openGL.init(canvas, 8, 8, 8, 24, 0, multiSampleCount)
				if (contextHandle == 0L) {
					throw RuntimeException("")
				}
				@Pc(60) val glxToolkit = GlxToolkit(openGL, canvas, contextHandle, textureSource, shaders, multiSampleCount)
				glxToolkit.initializeTables()
				return glxToolkit
			} else {
				throw RuntimeException("")
			}
		}

		@OriginalMember(owner = "client!b", name = "a", descriptor = "(BLclient!kf;)I")
		@JvmStatic
		fun toGlCombineSource(@OriginalArg(1) source: TextureCombineSource?): Int {
			if (TextureCombineSource.TEXTURE === source) {
				return 5890
			} else if (source === TextureCombineSource.PRIMARY_COLOR) {
				return 34167
			} else if (TextureCombineSource.PREVIOUS === source) {
				return 34168
			} else if (TextureCombineSource.CONSTANT_COLOR === source) {
				return 34166
			} else {
				throw IllegalArgumentException()
			}
		}

		@OriginalMember(owner = "client!mn", name = "i", descriptor = "[I")
		@JvmField
		val deleteIdBuffer: IntArray = IntArray(1000)

		@OriginalMember(owner = "client!kg", name = "i", descriptor = "[F")
		@JvmField
		val scratchFloat4: FloatArray = FloatArray(4)

		@OriginalMember(owner = "client!jb", name = "a", descriptor = "(Lclient!bf;I)I")
		@JvmStatic
		fun toGlTexGenMode(@OriginalArg(0) mode: TexCoordGenMode?): Int {
			if (mode === TexCoordGenMode.aClass31_1) {
				return 9216
			} else if (mode === TexCoordGenMode.aClass31_2) {
				return 34065
			} else if (TexCoordGenMode.aClass31_4 === mode) {
				return 34066
			} else {
				throw IllegalArgumentException()
			}
		}
	}
}
