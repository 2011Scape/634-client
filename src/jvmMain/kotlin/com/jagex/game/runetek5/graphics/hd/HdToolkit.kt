/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlToolkit (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.hd.gl.CoreGl

import com.jagex.game.runetek5.graphics.hd.scene.HdFeatures

import com.jagex.game.runetek5.graphics.hd.scene.HdFrameUniforms

import com.jagex.game.runetek5.graphics.hd.scene.HdPost

import com.jagex.game.runetek5.client.features.HdSettings

import com.jagex.game.runetek5.scene.Scene

import com.jagex.game.runetek5.client.features.RenderStats

import com.jagex.game.runetek5.graphics.hd.scene.HdDynamicStream
import com.jagex.game.runetek5.graphics.hd.scene.HdSceneRenderer

import com.jagex.game.runetek5.client.features.SceneTargetToolkit
import com.jagex.game.runetek5.client.GameCanvas
import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.cache.node.GlPointLight
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.GlRenderTarget
import com.jagex.game.runetek5.Matrix43
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.constants.PciVendorId
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import jaclib.memory.Buffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeap
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import lang.reflect.Method
import util.Enumeration
import util.Hashtable
import util.Locale
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.cache.node.GlMemoryPool
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.toolkit.OpenGlToolkit
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.utils.ColorUtil
import kotlin.jvm.JvmField
import lang.Class
import kotlin.jvm.JvmStatic
import lang.Synchronized
import lang.getMethod
import util.lowercase
import awt.height
import awt.width

class HdToolkit : Toolkit, SceneTargetToolkit {
	private var height: Int = 0

	@JvmField
	var viewportHeight: Int = 0

	private var width: Int = 0

	@JvmField
	var viewportWidth: Int = 0

	@JvmField
	var nativeHeap: NativeHeap? = null

	@JvmField
	var lastTickMillis: Int = 0

	private var offscreenFramebuffer: HdFramebuffer? = null

	private var drawTarget: GlRenderTarget? = null

	private var offscreenSprite: HdSprite? = null

	private var readTarget: GlRenderTarget? = null

	@JvmField
	var vboBuffersSize: Int = 0

	@JvmField
	var textureBuffersSize: Int = 0

	private var renderBuffersSize: Int = 0

	private var lastGcTime: Long = 0L

	private var colormaskActive: Boolean = false

	private var enableDepthMask: Boolean = false

	private var alphaTestEnabled: Boolean = false

	private var activeBlendType: Int = 0

	private var depthTestEnabled: Boolean = false

	private var activeBlendAlpha: Int = 0

	private var functionMask: Int = 0

	@JvmField
	var sunBrightness: Float = 0.0f

	private var sunCubeTextureGenerator: HdEnvironmentSampler? = null

	@JvmField
	var sunSlopeX: Int = 0

	@JvmField
	var bigEndianess: Boolean = false

	private var worldMapTexture: Hd2DImageTexture? = null

	@JvmField
	var sunSlopeZ: Int = 0

	@JvmField
	var integratedGraphicsImageProducer: HdFramebuffer? = null

	@JvmField
	var copyBuffer1: HdModel? = null

	@JvmField
	var arbFragmentShader: Boolean = false

	private var lightingEnabled: Boolean = false

	@JvmField
	var copyBuffer5: HdModel? = null

	@JvmField
	var waterHeight: Int = 0

	private var openglVersion: Int = 0

	private var gpuNameString: String? = null

	@JvmField
	var arbVertexShader: Boolean = false

	@JvmField
	var fogStartPosition: Float = 0.0f

	@JvmField
	var framebufferObjectSupported: Boolean = false

	@JvmField
	var maxTextureUnits: Int = 0

	private var lastMaterialId: Int = 0

	private var screenQuadArrayBuffer: ArrayBufferGL? = null

	private var maxTextureCoordinates: Int = 0

	private var fogEnabled: Boolean = false

	@JvmField
	var copyBuffer2: HdModel? = null

	@JvmField
	var copyTarget3: HdModel? = null

	private var hideLights: Boolean = false

	@JvmField
	var framebufferMultisample: Boolean = false

	@JvmField
	var framebufferBlit: Boolean = false

	private var glBinding: ElementsBufferGL? = null

	private var maxTextureImageUnits: Int = 0

	@JvmField
	var copyBuffer3: HdModel? = null

	private var advancedFramebuffers: Boolean = false

	private var fogEndPosition: Float = 0.0f

	@JvmField
	var texture2d: Hd2DTexture? = null

	private var openglTextures: Array<HdTexture?>? = null

	@JvmField
	var viewY2: Float = 0.0f

	@JvmField
	var quadTexCoordBuffer: OpenGlArrayDrawBuffer? = null

	@JvmField
	var copyTarget4: HdModel? = null

	@JvmField
	var viewX2: Float = 0.0f

	@JvmField
	var viewY1: Float = 0.0f

	@JvmField
	var copyTarget2: HdModel? = null

	private var activeLightCount: Int = 0

	@JvmField
	var copyTarget5: HdModel? = null

	@JvmField
	var arbVertexProgram: Boolean = false

	private var projectionDepthOffset: Float = 0.0f

	private var arbMultisample: Boolean = false

	@JvmField
	var copyBuffer4: HdModel? = null

	private var gpuCompanyString: String? = null

	@JvmField
	var stillWaterToggled: Boolean = false

	@JvmField
	var quadVertexBuffer: OpenGlArrayDrawBuffer? = null

	private var currentArrayBuffer: ArrayBufferGL? = null

	private var projectionDepthScale: Float = 0.0f

	private var textureUpdate: Boolean = false

	private var lastSceneLightCount: Int = 0

	@JvmField
	var arbTextureCubeMap: Boolean = false

	@JvmField
	var copyTarget1: HdModel? = null

	private var depthClippingMode: Int = 0

	private var pbuffersAvailable: Boolean = false

	@JvmField
	var viewX1: Float = 0.0f

	private var glTexturesCursorPos: Int = 0

	private var polygonCount: Int = 0

	private var modelCount: Int = 0

	private val canvasSurfaces: Hashtable<Any?, Any?> = Hashtable()

	@JvmField
	var size: Int = 128

	private val particleRenderer: HdParticleRenderer = HdParticleRenderer()

	private val scratchMatrix: Matrix43 = Matrix43()

	@JvmField
	val billboardMatrix: Matrix43 = Matrix43()

	@JvmField
	var shadowScaleShift: Int = 3

	private var colortableReset: Boolean = false

	@JvmField
	var shadowScale: Int = 8

	private val heapManager: Deque = Deque()

	private val drawTargetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	private var readTargetIndex: Int = -1

	private var drawTargetIndex: Int = -1

	// Not part of the original client: with client.scale, the frame is drawn into this game-sized
	// framebuffer and copied to the window scaled at flip; see updateScaledTarget.
	private var scaledTarget: HdFramebuffer? = null
	private var scaledColour: Hd2DTexture? = null
	private var scaledDepth: HdRenderBuffer? = null
	private var scaledWidth: Int = 0
	private var scaledHeight: Int = 0
	// With anti-aliasing, the frame is drawn into this multisampled framebuffer and resolved into
	// the scaled target before the copy to the window, as the window's own surface would have been.
	private var scaledMultisample: HdFramebuffer? = null
	private var scaledMultisampleColour: HdRenderBuffer? = null
	private var scaledMultisampleDepth: HdRenderBuffer? = null

	private val readTargetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	private val targetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	private var targetIndex: Int = -1

	private val listsAwaitingDeallocation: Deque

	private val vboBuffersAwaitingDeallocation: Deque

	private val texturesAwaitingDeallocation: Deque

	private val frameBuffersAwaitingDeallocation: Deque

	private val renderBuffersAwaitingRemoval: Deque

	private val arbProgramsAwaitingDeallocation: Deque

	private val shaderProgramsAwaitingDeallocation: Deque

	@JvmField
	val viewMatrix43: Matrix43

	@JvmField
	val modelViewMatrix: Matrix43

	@JvmField
	val inverseModelViewMatrix: Matrix43

	private var viewportOffsetX: Int = 0

	private var fogMin: Float = 0.0f

	private var clipTop: Int = 0

	@JvmField
	var sunColorRed: Float = 0.0f

	@JvmField
	var waterFogColour: Int = 0

	@JvmField
	var projectionScaleX: Int = 0

	private var outlineCirclePointSize: Float = 0.0f

	private var sunColorRgb: Int = 0

	@JvmField
	var sunColorGreen: Float = 0.0f

	private var textureCombineRgb: Int = 0

	private var fillCirclePointSize: Float = 0.0f

	@JvmField
	var projectionCenterY: Int = 0

	@JvmField
	var sunColorIntensity: Float = 0.0f

	@JvmField
	val sunDirection: FloatArray

	@JvmField
	var waterIntensity: Int = 0

	private val lightingDirection: FloatArray

	@JvmField
	var sunColorBlue: Float = 0.0f

	@JvmField
	var projectionScaleY: Int = 0

	private var zFar: Int = 0

	private var clipRight: Int = 0

	private var densityFactor: Float = 0.0f

	private var clipBottom: Int = 0

	private var renderStillWater: Boolean = false

	private val sunPreviousPosition: FloatArray

	private var orthoZoom: Float = 0.0f

	@JvmField
	var zNear: Int = 0

	private var textureCombineAlpha: Int = 0

	@JvmField
	var fogOffset: Int = 0

	@JvmField
	var fogColor: Int = 0

	private var clipLeft: Int = 0

	private val projectionMatrix: FloatArray

	private var depthBufferingEnabled: Boolean = false

	@JvmField
	var antiSunColorIntensity: Float = 0.0f

	private val lightNodes: Array<PointLight?>

	@JvmField
	var projectionCenterX: Int = 0

	private var viewportOffsetY: Int = 0

	@JvmField
	var farClipDistance: Float = 0.0f

	@JvmField
	var projectedFarClipDistance: Float = 0.0f

	@JvmField
	var waterDepth: Int = 0

	private val antiSunPreviousPosition: FloatArray

	@JvmField
	var fogDensity: Int = 0

	@JvmField
	var textureBuffer: GlBitBuffer

	@JvmField
	val shadowTextureData: ByteArray

	@JvmField
	var scratchVerticesX: IntArray

	@JvmField
	var scratchVerticesY: IntArray

	@JvmField
	var visibleTileIndices: IntArray

	private val canvas: Canvas?

	private var currentCanvas: Canvas? = null

	@JvmField
	val multiSamplesPreference: Int

	private var activeOpenglInstance: HdContext? = null

	private val canvasSurface: Long

	private var currentSurface: Long = 0L

	@JvmField
	val pixelType: Int

	private var vertexbufferSupported: Boolean = false

	@JvmField
	var texture3dSupported: Boolean = false

	@JvmField
	var arbTextureFloat: Boolean = false

	@JvmField
	var arbRectangleTexturesSupported: Boolean = false

	@JvmField
	var isAmd: Boolean = false

	private var shouldUseArbVbo: Boolean = false

	@JvmField
	var extFramebufferObject: Boolean = false

	private val s3Graphics: Boolean

	val textureCache: HdTextureCache

	@JvmField
	val texture3dShader: HdTexture3DShader

	private val framebufferCompositor: HdFramebufferCompositor?

	private var rectangularTexture: HdRectangularTexture? = null

	private val effectManager: HdEffectManager

	constructor(canvas: Canvas?, textureSource: MaterialProvider?, multiSamples: Int) : super(textureSource) {
		Queue()
		IterableHashTable(16)
		this.listsAwaitingDeallocation = Deque()
		this.vboBuffersAwaitingDeallocation = Deque()
		this.texturesAwaitingDeallocation = Deque()
		this.frameBuffersAwaitingDeallocation = Deque()
		this.renderBuffersAwaitingRemoval = Deque()
		this.arbProgramsAwaitingDeallocation = Deque()
		this.shaderProgramsAwaitingDeallocation = Deque()
		this.viewMatrix43 = Matrix43()
		this.modelViewMatrix = Matrix43()
		this.inverseModelViewMatrix = Matrix43()
		this.viewportOffsetX = 0
		this.fogMin = 0.0f
		this.clipTop = 0
		this.sunColorRed = 1.0f
		this.waterFogColour = -1
		this.projectionScaleX = 512
		this.outlineCirclePointSize = -1.0f
		this.sunColorRgb = -1
		this.sunColorGreen = 1.0f
		this.textureCombineRgb = 8448
		this.fillCirclePointSize = -1.0f
		this.projectionCenterY = 0
		this.sunColorIntensity = -1.0f
		this.sunDirection = FloatArray(4)
		this.waterIntensity = 0
		this.lightingDirection = FloatArray(4)
		this.sunColorBlue = 1.0f
		this.projectionScaleY = 512
		this.zFar = 3584
		this.clipRight = 0
		this.densityFactor = 1.0f
		this.clipBottom = 0
		this.renderStillWater = false
		this.sunPreviousPosition = FloatArray(4)
		this.orthoZoom = 1.0f
		this.zNear = 50
		this.textureCombineAlpha = 8448
		this.fogOffset = 0
		this.fogColor = -1
		this.clipLeft = 0
		this.projectionMatrix = FloatArray(16)
		this.depthBufferingEnabled = true
		this.antiSunColorIntensity = -1.0f
		this.lightNodes = arrayOfNulls<PointLight>(HdToolkit.MAX_LIGHTS)
		this.projectionCenterX = 0
		this.viewportOffsetY = 0
		this.farClipDistance = 3584.0f
		this.projectedFarClipDistance = 3584.0f
		this.waterDepth = -1
		this.antiSunPreviousPosition = FloatArray(4)
		this.fogDensity = -1
		this.textureBuffer = GlBitBuffer(8192)
		this.shadowTextureData = ByteArray(16384)
		this.scratchVerticesX = IntArray(1)
		this.scratchVerticesY = IntArray(1)
		this.visibleTileIndices = IntArray(1)
		this.canvas = canvas
		this.currentCanvas = this.canvas
		// Not part of the original client: with the anti-aliasing pipeline the window has no samples;
		// the 3D view is multisampled in its own framebuffer instead (beginScene).
		this.sceneSettingSamples = multiSamples
		this.multiSamplesPreference = if (ClientFeatures.AA_PIPELINE && !ClientFeatures.aaPipelineUnavailable) 0 else multiSamples
		// 634HD needs no Jagex native library: the context is LWJGL's, the GL calls go through HdGl.
		try {
			this.activeOpenglInstance = HdContext()
			this.canvasSurface = this.activeOpenglInstance!!.init(canvas, 8, 8, 8, 24, 0, this.multiSamplesPreference)
			this.currentSurface = this.canvasSurface
			if (this.canvasSurface == 0L) {
				throw RuntimeException("no OpenGL surface on the canvas")
			}
			this.attach()
			val capabilityErrors: Int = this.accumulateCapabilities()
			if (capabilityErrors != 0) {
				throw RuntimeException("the OpenGL driver lacks what 634HD needs (" + this.activeOpenglInstance!!.description + ", checks 0x" + Integer.toHexString(capabilityErrors) + ")")
			}
			this.pixelType = if (this.bigEndianess) 33639 else 5121
			// A core context has no client-side arrays: every vertex goes in a buffer object. The 2010
			// driver workarounds (no buffer objects or 3D textures on old Radeons, no framebuffer objects
			// on Intel) are left out: a driver with a 3.3 core context has both.
			this.shouldUseArbVbo = this.vertexbufferSupported
			this.checkScenePipeline() // Not part of the original client: see checkScenePipeline.
			this.s3Graphics = this.gpuCompanyString != "s3 graphics"
			ColorUtil.initColorTables(false, true)
			this.colortableReset = true
			this.textureCache = HdTextureCache(this, this.materialProvider)
			this.buildTables()
			this.texture3dShader = HdTexture3DShader(this)
			this.framebufferCompositor = HdFramebufferCompositor(this)
			if (this.framebufferCompositor!!.isDisplayAvailable()) {
				this.rectangularTexture = HdRectangularTexture(this)
				if (!this.rectangularTexture!!.isSupported()) {
					this.rectangularTexture!!.destroy()
					this.rectangularTexture = null
				}
			}
			this.effectManager = HdEffectManager(this)
			// 634HD: the features' uniform block is bound from the start (HdFrameUniforms)
			this.frameUniforms.upload(0)
			this.prepareForRendering()
			this.updateCanvasSize()
			this.renderLastFrame()
		} catch (ex3: Throwable) {
			ex3.printStackTrace()
			this.stop()
			throw RuntimeException(ex3.message ?: ex3.toString(), ex3)
		}
	}

	// ---- 634HD features (HdSettings) ----

	/** While the game view is drawn (beginScene to endScene). */
	private var sceneDrawing: Boolean = false

	private var frameNumber: Int = 0

	/** Whether this frame's uniform block went up (at the first setCamera of the game view). */
	private var frameUploaded: Boolean = false

	val frameUniforms: HdFrameUniforms = HdFrameUniforms()

	val hdPost: HdPost = HdPost()

	val hdFeatures: HdFeatures = HdFeatures()

	/** The draws of the game view in perspective get 634HD's features (hd_lib.glsl uHdScene); nothing else does. */
	private fun updateHdScene() {
		HdGl.state.hdScene = if (this.sceneDrawing && this.depthClippingMode == 2) 1 else 0
	}

	// ---- 634HD retained scene (scene/HdSceneRenderer) ----

	val sceneRenderer: HdSceneRenderer = HdSceneRenderer(this)

	/** Streamed dynamic geometry: animated models' vertices and indices, a range per upload. */
	val vertexStream: HdDynamicStream = HdDynamicStream(this, HdDynamicStream.ARRAY)

	val elementStream: HdDynamicStream = HdDynamicStream(this, HdDynamicStream.ELEMENTS)

	/**
	 * Whether models draw through the retained scene now: once it is built, and not in the
	 * underwater pass (per-entity water parameters) or the orthographic view (its own static layer).
	 */
	fun sceneRetained(): Boolean {
		return this.sceneRenderer.built && !this.stillWaterToggled && this.depthClippingMode != 3
	}

	override fun sceneBuilt() {
		this.sceneRenderer.build()
	}

	override fun sceneChanged(level: Int, x: Int, z: Int) {
		this.sceneRenderer.sceneChanged(x, z)
	}

	override fun transparentEntitiesDone() {
		this.sceneRenderer.endPass()
	}

	override fun opaqueEntitiesDone() {
		if (!this.sceneRenderer.built) {
			// a toolkit switched to with a scene already built (the scene is not built again)
			if (Scene.stringStack != null) {
				this.sceneRenderer.build()
			}
			return
		}
		RenderStats.category = RenderStats.CATEGORY_MODEL
		this.sceneRenderer.flushOpaque()
		RenderStats.category = RenderStats.CATEGORY_OTHER
	}

	override fun L(fogColour: Int, fogDensity: Int, fogOffset: Int) {
		if (this.fogColor == fogColour && this.fogDensity == fogDensity && this.fogOffset == fogOffset) {
			return
		}
		this.fogDensity = fogDensity
		this.fogColor = fogColour
		this.fogOffset = fogOffset
		this.updateFog()
		this.toggleFog()
	}

	fun setLightingEnabled(enabled: Boolean) {
		if (this.lightingEnabled != enabled) {
			this.lightingEnabled = enabled
			this.toggleLighting()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
		}
	}

	override fun createModel(mesh: Mesh?, functionMask: Int, featureMask: Int, ambient: Int, contrast: Int): Model {
		return HdModel(this, mesh, functionMask, ambient, contrast, featureMask)
	}

	fun loadTextureCombineMode(combineRgb: Int, combineAlpha: Int) {
		if (this.glTexturesCursorPos != 0) {
			HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb)
			HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha)
			return
		}
		var changed: Boolean = false
		if (this.textureCombineRgb != combineRgb) {
			HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb)
			changed = true
			this.textureCombineRgb = combineRgb
		}
		if (this.textureCombineAlpha != combineAlpha) {
			HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha)
			this.textureCombineAlpha = combineAlpha
			changed = true
		}
		if (changed) {
			this.functionMask = this.functionMask and 0xFFFFFFE2.toInt()
		}
	}

	private fun loadProjectionMatrix() {
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadMatrixf(this.projectionMatrix, 0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	override fun setSunCubeTexture(environmentSampler: EnvironmentSampler?) {
		this.sunCubeTextureGenerator = environmentSampler as HdEnvironmentSampler?
	}

	fun createArrayBuffer(isDynamic: Boolean, size: Int, buffer: ByteArray?, position: Int): ArrayBufferGL {
		return (if (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo)) HdVboArrayBuffer(this, size, buffer, position, isDynamic) else HdArrayBuffer(this, size, buffer, position)) as ArrayBufferGL
	}

	override fun DA(centerX: Int, centerY: Int, scaleX: Int, scaleY: Int) {
		this.projectionScaleX = scaleX
		this.projectionScaleY = scaleY
		this.projectionCenterY = centerY
		this.projectionCenterX = centerX
		this.updateProjectionMatrix()
		this.updateViewBounds()
		if (this.depthClippingMode == 3) {
			this.loadOrthoProjection()
		} else if (this.depthClippingMode == 2) {
			this.loadProjectionMatrix()
		}
	}

	private fun toggleFog() {
		if (this.fogEnabled && this.fogDensity >= 0) {
			HdGl.glEnable(OpenGLConstants.GL_FOG)
		} else {
			HdGl.glDisable(OpenGLConstants.GL_FOG)
		}
	}

	fun createElementsArrayBuffer(size: Int, data: ByteArray?, isDynamic: Boolean): ElementsBufferGL {
		return (if (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo)) HdVboElementsArrayBuffer(this, 5123, data, size, isDynamic) else HdElementsArrayBuffer(this, 5123, data, size)) as ElementsBufferGL
	}

	fun popMatrix() {
		HdGl.glPopMatrix()
	}

	override fun createPointLight(x: Int, y: Int, z: Int, radius: Int, range: Int, intensity: Float): PointLight {
		return GlPointLight(x, y, z, radius, range, intensity)
	}

	fun setupTextRendering() {
		if (this.functionMask == 4) {
			return
		}
		this.setScreenProjectionMode()
		this.toggleFog(false)
		this.setLightingEnabled(false)
		this.toggleZbuffer(false)
		this.setDepthMask(false)
		this.renderTexture(-2)
		this.setBlendMode(1)
		this.functionMask = 4
	}

	override fun XA(): Int {
		return this.zFar
	}

	override fun drawSurfaceLine(startX: Int, startY: Int, endX: Int, endY: Int, colour: Int, clippingMask: ComponentSurface?, maskX: Int, maskY: Int) {
		val mask: HdClippingMask? = clippingMask as HdClippingMask?
		val surface: Hd2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		val dx: Float = endX.toFloat() - startX.toFloat()
		val dy: Float = endY.toFloat() - startY.toFloat()
		val inverseLength: Float = (1.0 / kotlin.math.sqrt((dy * dy + dx * dx).toDouble())).toFloat()
		val dirX: Float = dx * inverseLength
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		val dirY: Float = dy * inverseLength
		HdGl.glBegin(OpenGLConstants.GL_LINES)
		HdGl.glTexCoord2f((startX - maskX).toFloat() * texScaleS, texScaleT * (startY - maskY).toFloat())
		HdGl.glVertex2f(startX.toFloat() + 0.35f, startY.toFloat() + 0.35f)
		HdGl.glTexCoord2f(texScaleS * (endX - maskX).toFloat(), (endY - maskY).toFloat() * texScaleT)
		HdGl.glVertex2f(dirX + endX.toFloat() + 0.35f, endY.toFloat() + 0.35f + dirY)
		HdGl.glEnd()
		this.setTexture(5890, 768, 0)
	}

	override fun Y(): IntArray {
		return intArrayOf(this.projectionCenterX, this.projectionCenterY, this.projectionScaleX, this.projectionScaleY)
	}

	@Synchronized
	fun requestTextureCleanup(handle: Int, sizeRequirement: Int) {
		val intNode: IntNode = IntNode(sizeRequirement)
		intNode.key = handle.toLong()
		this.texturesAwaitingDeallocation.addLast(intNode)
	}

	fun activateTexture(textureUnit: Int) {
		if (textureUnit != this.glTexturesCursorPos) {
			HdGl.glActiveTexture(textureUnit + OpenGLConstants.GL_TEXTURE0)
			this.glTexturesCursorPos = textureUnit
		}
	}

	fun createDataArrayBuffer(buffer: Buffer?, stride: Int, size: Int): ArrayBufferGL {
		return (if (this.vertexbufferSupported) HdVboArrayBuffer(this, stride, buffer, size, false) else HdArrayBuffer(this, stride, buffer)) as ArrayBufferGL
	}

	private fun setModelViewMatrixMode() {
		if (this.depthClippingMode != 2) {
			this.depthClippingMode = 2
			this.loadProjectionMatrix()
			this.updateModelViewAndLights()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
			this.updateHdScene()
		}
	}

	override fun hasTextures(): Boolean {
		return true
	}

	fun getCubeMap(): HdCubeTexture? {
		return if (this.sunCubeTextureGenerator == null) null else this.sunCubeTextureGenerator!!.getCubeTexture()
	}

	override fun HA(x: Int, y: Int, z: Int, projectionDepth: Int, output: IntArray?) {
		val depth: Float = z.toFloat() * this.viewMatrix43.zC + y.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x.toFloat() + this.viewMatrix43.zD
		if (this.zNear.toFloat() > depth || this.zFar.toFloat() < depth) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		val screenX: Int = ((this.viewMatrix43.xD + this.viewMatrix43.xB * y.toFloat() + x.toFloat() * this.viewMatrix43.xA + this.viewMatrix43.xC * z.toFloat()) * this.projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt()
		val screenY: Int = (this.projectionScaleY.toFloat() * (this.viewMatrix43.yD + this.viewMatrix43.yA * x.toFloat() + y.toFloat() * this.viewMatrix43.yB + z.toFloat() * this.viewMatrix43.yC) / projectionDepth.toFloat()).toInt()
		if (screenX.toFloat() >= this.viewX1 && this.viewX2 >= screenX.toFloat() && this.viewY1 <= screenY.toFloat() && this.viewY2 >= screenY.toFloat()) {
			output!![2] = depth.toInt()
			output[1] = (screenY.toFloat() - this.viewY1).toInt()
			output[0] = (screenX.toFloat() - this.viewX1).toInt()
		} else {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
		}
	}

	override fun EA(waterHeight: Int, fogColour: Int, depth: Int, intensity: Int) {
		if (!this.stillWaterToggled) {
			throw RuntimeException("")
		}
		this.waterHeight = waterHeight
		this.waterFogColour = fogColour
		this.waterDepth = depth
		this.waterIntensity = intensity
		if (this.renderStillWater) {
			this.effectManager.waterEffect.bindProgram()
			this.effectManager.waterEffect.updateFogParams()
		}
	}

	private fun setOrthoProjectionMode() {
		if (this.depthClippingMode != 3) {
			this.depthClippingMode = 3
			this.loadOrthoProjection()
			this.updateModelViewAndLights()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
		}
	}

	override fun supportsFullscreen(): Boolean {
		return true
	}

	fun adjustFog(fogMin: Float, densityFactor: Float) {
		this.fogMin = fogMin
		this.densityFactor = densityFactor
		this.updateFog()
	}

	private fun updateCanvasSize() {
		if (this.currentCanvas == null) {
			this.height = 0
			this.width = 0
		} else {
			val dimension: Dimension = this.currentCanvas!!.getSize()
			this.width = dimension.width
			this.height = dimension.height
		}
		this.updateScaledTarget()
		if (this.drawTarget == null) {
			this.viewportWidth = this.width
			this.viewportHeight = this.height
			this.updateViewport()
		}
		this.setOrthoMatrixMode()
		this.la()
	}

	fun renderTexture(hasLight: Boolean, materialId: Int, allowStillWater: Boolean) {
		if (materialId != this.lastMaterialId || this.renderStillWater != this.stillWaterToggled) {
			var texture: Hd2DTexture? = null
			var combineMode: Int = 0
			var effectParam1: Byte = 0
			var effectParam2: Int = 0
			var effectType: Int = if (this.stillWaterToggled) 3 else 0
			if (materialId < 0) {
				this.renderStationaryTexture()
				this.setSceneAlphaToCoverage(false) // Not part of the original client: see setSceneAlphaToCoverage.
			} else {
				texture = this.textureCache.getTexture(materialId)
				val textureMetrics: MaterialType? = this.materialProvider!!.getMetrics(materialId)
				this.setSceneAlphaToCoverage(textureMetrics!!.effectCombiner == 1) // Not part of the original client: see setSceneAlphaToCoverage.
				if (textureMetrics.speedU.toInt() == 0 && textureMetrics.speedV.toInt() == 0) {
					this.renderStationaryTexture()
				} else {
					val textureSize: Int = if (textureMetrics.isSmall) 64 else 128
					val animationPeriod: Int = textureSize * 50
					this.renderAnimatedTexture((this.lastTickMillis % animationPeriod * textureMetrics.speedV).toFloat() / animationPeriod.toFloat(), 0.0f, (textureMetrics.speedU * (this.lastTickMillis % animationPeriod)).toFloat() / animationPeriod.toFloat())
				}
				if (!this.stillWaterToggled) {
					effectType = textureMetrics.effectType.toInt()
					effectParam2 = textureMetrics.effectParam2
					effectParam1 = textureMetrics.effectParam1
				}
				combineMode = textureMetrics.combineMode
			}
			this.effectManager.loadEffect(effectParam1.toInt(), hasLight, effectType, effectParam2, allowStillWater)
			if (!this.effectManager.bindTexture(combineMode, texture)) {
				this.setActiveTexture(texture)
				this.setColourOp(combineMode)
			}
			this.renderStillWater = this.stillWaterToggled
			this.lastMaterialId = materialId
		}
		this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
	}

	private fun loadOrthoProjection() {
		val left: Float = (-this.projectionCenterX).toFloat() * this.orthoZoom / this.projectionScaleX.toFloat()
		val top: Float = this.orthoZoom * (-this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		val right: Float = this.orthoZoom * (this.viewportWidth - this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		val bottom: Float = this.orthoZoom * (this.viewportHeight - this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadIdentity()
		if (left != right && top != bottom) {
			HdGl.glOrtho(left.toDouble(), right.toDouble(), (-bottom).toDouble(), (-top).toDouble(), this.zNear.toDouble(), this.zFar.toDouble())
		}
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	override fun supportsBloom(): Boolean {
		return this.rectangularTexture != null && (this.multiSamplesPreference <= 1 || this.advancedFramebuffers)
	}

	fun setDepthMask(enableMask: Boolean) {
		if (this.enableDepthMask != enableMask) {
			this.enableDepthMask = enableMask
			this.setDepthMask()
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
		}
	}

	override fun K(bounds: IntArray?) {
		bounds!![1] = this.clipTop
		bounds[3] = this.clipBottom
		bounds[0] = this.clipLeft
		bounds[2] = this.clipRight
	}

	override fun Q(x: Int, y: Int, width: Int, height: Int, colour: Int, envColour: Int, alphaData: ByteArray?, maskWidth: Int, blendMode: Int) {
		val maxU: Float
		val maxV: Float
		if (this.worldMapTexture == null || this.worldMapTexture!!.width < width || this.worldMapTexture!!.length < height) {
			this.worldMapTexture = Hd2DImageTexture.createSurface(alphaData, height, this, width)
			this.worldMapTexture!!.setRepeat(false, false)
			maxU = this.worldMapTexture!!.t
			maxV = this.worldMapTexture!!.s
		} else {
			this.worldMapTexture!!.uploadTextureData(width, false, height, 6406, alphaData)
			maxU = height.toFloat() * this.worldMapTexture!!.t / this.worldMapTexture!!.length.toFloat()
			maxV = width.toFloat() * this.worldMapTexture!!.s / this.worldMapTexture!!.width.toFloat()
		}
		this.setupSpriteRendering()
		this.setActiveTexture(this.worldMapTexture)
		this.setBlendMode(blendMode)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		this.setTextureEnvColour(envColour)
		this.loadTextureCombineMode(34165, 34165)
		this.setTexture(34166, 768, 0)
		this.setTexture(5890, 770, 2)
		this.setTextureTransparency(0, 34166)
		this.setTextureTransparency(2, 5890)
		val left: Float = x.toFloat()
		val top: Float = y.toFloat()
		val right: Float = width.toFloat() + left
		val bottom: Float = height.toFloat() + top
		HdGl.glBegin(7)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2f(left, top)
		HdGl.glTexCoord2f(0.0f, maxV)
		HdGl.glVertex2f(left, bottom)
		HdGl.glTexCoord2f(maxU, maxV)
		HdGl.glVertex2f(right, bottom)
		HdGl.glTexCoord2f(maxU, 0.0f)
		HdGl.glVertex2f(right, top)
		HdGl.glEnd()
		this.setTexture(5890, 768, 0)
		this.setTexture(34166, 770, 2)
		this.setTextureTransparency(0, 5890)
		this.setTextureTransparency(2, 34166)
	}

	override fun render(particleList: ParticleList?) {
		this.particleRenderer.drawParticles(this, -1, particleList)
	}

	fun updatePointSizeRange(alpha: Float, red: Float, blue: Float, green: Float) {
		OpenGlToolkit.BUFFER_4F[2] = blue
		OpenGlToolkit.BUFFER_4F[0] = red
		OpenGlToolkit.BUFFER_4F[1] = green
		OpenGlToolkit.BUFFER_4F[3] = alpha
		HdGl.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	override fun setCamera(matrix: Matrix?) {
		this.viewMatrix43.apply(matrix)
		this.modelViewMatrix.apply(this.viewMatrix43)
		this.modelViewMatrix.negateYZ()
		this.inverseModelViewMatrix.setInverse(this.modelViewMatrix)
		if (this.depthClippingMode != 1) {
			this.updateModelViewAndLights()
		}
		if (this.sceneDrawing && !this.frameUploaded) {
			// 634HD: the camera is known, so the frame's feature uniforms can go up
			this.setupMatrixRendering()
			this.hdFeatures.prepare(this, this.frameUniforms)
			this.frameUniforms.upload(HdSettings.shadows)
			this.frameUploaded = true
		}
	}

	fun setArrayBuffer(arrayBuffer: ArrayBufferGL?) {
		if (arrayBuffer !== this.currentArrayBuffer) {
			if (this.vertexbufferSupported) {
				HdGl.glBindBufferARB(OpenGLConstants.GL_ARRAY_BUFFER, arrayBuffer!!.getId())
			}
			this.currentArrayBuffer = arrayBuffer
		}
	}

	override fun drawTriangle() {
		this.enter2dMode()
		this.setBlendMode(1)
		HdGl.glBegin(OpenGLConstants.GL_TRIANGLES)
		HdGl.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		HdGl.glVertex3f(5.toFloat() + 0.35f, 10.toFloat() + 0.35f, 100.toFloat())
		HdGl.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		HdGl.glVertex3f(75.toFloat() + 0.35f, 50.toFloat() + 0.35f, 100.toFloat())
		HdGl.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		HdGl.glVertex3f(15.toFloat() + 0.35f, 90.toFloat() + 0.35f, 100.toFloat())
		HdGl.glEnd()
	}

	override fun restoreSurface() {
		if (this.extFramebufferObject) {
			if (this.drawTarget !== this.offscreenFramebuffer) {
				throw RuntimeException()
			}
			this.offscreenFramebuffer!!.clearAttachment(0)
			this.offscreenFramebuffer!!.clearAttachment(8)
			this.popTarget(this.offscreenFramebuffer)
		} else if (this.pbuffersAvailable) {
			this.offscreenSprite!!.captureScreenRegion(0, 0, this.viewportWidth, this.viewportHeight, 0, 0)
			this.activeOpenglInstance!!.setSurface(this.currentSurface)
		} else {
			throw RuntimeException("")
		}
		this.viewportWidth = this.width
		this.offscreenSprite = null
		this.viewportHeight = this.height
		this.setOrthoMatrixMode()
		this.updateViewport()
		this.la()
	}

	fun pushDrawTarget(target: GlRenderTarget?) {
		if (this.drawTargetIndex >= 3) {
			throw RuntimeException()
		}
		if (this.drawTargetIndex >= 0) {
			this.drawTargetStack[this.drawTargetIndex]!!.unbindDraw()
		}
		this.drawTargetStack[++this.drawTargetIndex] = target
		this.drawTarget = target
		this.drawTarget!!.bindDraw()
	}

	fun toggleZbuffer(zbuffer: Boolean) {
		if (zbuffer == this.depthTestEnabled) {
			return
		}
		if (zbuffer) {
			HdGl.glEnable(OpenGLConstants.GL_DEPTH_TEST)
		} else {
			HdGl.glDisable(OpenGLConstants.GL_DEPTH_TEST)
		}
		this.depthTestEnabled = zbuffer
		this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
	}

	override fun createFontRenderer(metrics: FontMetrics?, image: Array<SpriteType?>?): AbstractFontRenderer {
		return HdFont(this, metrics, image, true)
	}

	override fun aa(x: Int, y: Int, width: Int, height: Int, colour: Int, blendMode: Int) {
		val left: Float = x.toFloat() + 0.35f
		val top: Float = y.toFloat() + 0.35f
		val right: Float = width.toFloat() + left
		this.enter2dMode()
		val bottom: Float = top + height.toFloat()
		this.setBlendMode(blendMode)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.arbMultisample) {
			HdGl.glDisable(32925)
		}
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glVertex2f(left, top)
		HdGl.glVertex2f(left, bottom)
		HdGl.glVertex2f(right, bottom)
		HdGl.glVertex2f(right, top)
		HdGl.glEnd()
		if (this.arbMultisample) {
			HdGl.glEnable(32925)
		}
	}

	override fun ZA(colour: Int, intensity: Float, antiIntensity: Float, directionX: Float, directionY: Float, directionZ: Float) {
		val colourChanged: Boolean = this.sunColorRgb != colour
		if (colourChanged || this.sunColorIntensity != intensity || antiIntensity != this.antiSunColorIntensity) {
			this.sunColorIntensity = intensity
			this.sunColorRgb = colour
			this.antiSunColorIntensity = antiIntensity
			if (colourChanged) {
				this.sunColorBlue = (this.sunColorRgb and 0xFF).toFloat() / 255.0f
				this.sunColorRed = (this.sunColorRgb and 0xFF0000).toFloat() / 1.671168E7f
				this.sunColorGreen = (this.sunColorRgb and 0xFF00).toFloat() / 65280.0f
				this.updateAmbient()
			}
			this.updateSunDiffuse()
		}
		if (directionX == this.sunPreviousPosition[0] && this.sunPreviousPosition[1] == directionY && this.sunPreviousPosition[2] == directionZ) {
			return
		}
		this.sunPreviousPosition[2] = directionZ
		this.sunPreviousPosition[1] = directionY
		this.sunPreviousPosition[0] = directionX
		this.antiSunPreviousPosition[0] = -directionX
		this.antiSunPreviousPosition[2] = -directionZ
		this.antiSunPreviousPosition[1] = -directionY
		val inverseLength: Float = (1.0 / kotlin.math.sqrt((directionZ * directionZ + directionY * directionY + directionX * directionX).toDouble())).toFloat()
		this.sunDirection[0] = inverseLength * directionX
		this.sunDirection[2] = directionZ * inverseLength
		this.sunDirection[1] = inverseLength * directionY
		this.lightingDirection[2] = -this.sunDirection[2]
		this.lightingDirection[1] = -this.sunDirection[1]
		this.lightingDirection[0] = -this.sunDirection[0]
		this.setLightingSourceParams()
		this.sunSlopeZ = (directionZ * 256.0f / directionY).toInt()
		this.sunSlopeX = (directionX * 256.0f / directionY).toInt()
	}

	override fun combineFunctionMasks(maskA: Int, maskB: Int): Int {
		return maskA or maskB
	}

	override fun GA(colour: Int) {
		this.setBlendMode(0)
		HdGl.glClearColor((colour and 0xFF0000).toFloat() / 1.671168E7f, (colour and 0xFF00).toFloat() / 65280.0f, (colour and 0xFF).toFloat() / 255.0f, (colour ushr 24).toFloat() / 255.0f)
		HdGl.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
	}

	override fun initialize() {
	}

	fun setBlendMode(mode: Int) {
		if (mode == this.activeBlendAlpha) {
			return
		}
		val alphaTest: Boolean
		val blendType: Byte
		if (mode == 1) {
			blendType = 1
			alphaTest = true
		} else if (mode == 2) {
			blendType = 2
			alphaTest = false
		} else if (mode == 128) {
			blendType = 3
			alphaTest = true
		} else {
			alphaTest = false
			blendType = 0
		}
		if (!this.colormaskActive) {
			HdGl.glColorMask(true, true, true, true)
			this.colormaskActive = true
		}
		if (alphaTest != this.alphaTestEnabled) {
			if (alphaTest) {
				HdGl.glEnable(OpenGLConstants.GL_ALPHA_TEST)
			} else {
				HdGl.glDisable(OpenGLConstants.GL_ALPHA_TEST)
			}
			this.alphaTestEnabled = alphaTest
		}
		if (blendType.toInt() != this.activeBlendType) {
			if (blendType.toInt() == 1) {
				HdGl.glEnable(OpenGLConstants.GL_BLEND)
				HdGl.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA)
			} else if (blendType.toInt() == 2) {
				HdGl.glEnable(OpenGLConstants.GL_BLEND)
				HdGl.glBlendFunc(OpenGLConstants.GL_ONE, OpenGLConstants.GL_ONE)
			} else if (blendType.toInt() == 3) {
				HdGl.glEnable(OpenGLConstants.GL_BLEND)
				HdGl.glBlendFunc(OpenGLConstants.GL_DST_COLOR, OpenGLConstants.GL_ONE)
			} else {
				HdGl.glDisable(OpenGLConstants.GL_BLEND)
			}
			this.activeBlendType = blendType.toInt()
		}
		this.activeBlendAlpha = mode
		this.functionMask = this.functionMask and 0xFFFFFFE3.toInt()
	}

	private fun updateModelViewAndLights() {
		HdGl.glLoadIdentity()
		HdGl.glMultMatrixf(this.modelViewMatrix.getColumnMajor(), 0)
		if (this.renderStillWater) {
			this.effectManager.waterEffect.bindProgram()
		}
		this.setLightingSourceParams()
		this.updatePointLights()
	}

	fun setOrthoMatrixMode() {
		if (this.depthClippingMode != 0) {
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
			this.depthClippingMode = 0
		}
	}

	fun drawArrays(count: Int) {
		HdGl.glDrawArrays(OpenGLConstants.GL_QUADS, 0, count)
	}

	override fun U(x: Int, y: Int, width: Int, colour: Int, blendMode: Int) {
		this.enter2dMode()
		this.setBlendMode(blendMode)
		val startX: Float = x.toFloat() + 0.35f
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		val startY: Float = y.toFloat() + 0.35f
		HdGl.glBegin(OpenGLConstants.GL_LINES)
		HdGl.glVertex2f(startX, startY)
		HdGl.glVertex2f(width.toFloat() + startX, startY)
		HdGl.glEnd()
	}

	override fun setTextureSize(size: Int) {
		if (size < 128 || size > 1024) {
			throw IllegalArgumentException()
		}
		this.size = size
		this.textureCache.reset()
	}

	override fun xa(brightness: Float) {
		if (this.sunBrightness != brightness) {
			this.sunBrightness = brightness
			this.updateAmbient()
		}
	}

	fun renderTexture(materialId: Int) {
		this.renderTexture(true, materialId)
	}

	fun multiMatrix(matrix44: Matrix43?) {
		HdGl.glPushMatrix()
		HdGl.glMultMatrixf(matrix44!!.getColumnMajor(), 0)
	}

	override fun createNativeSprite(x: Int, y: Int, width: Int, height: Int, hasAlpha: Boolean): NativeSprite {
		return HdSprite(this, x, y, width, height)
	}

	fun drawElements(indexCount: Int, elementsBuffer: ElementsBufferGL?, startIndex: Int) {
		val indexType: Int = elementsBuffer!!.getIndexType()
		val byteOffset: Int = startIndex * this.getDataTypeSize(indexType)
		this.setElementsBuffer(elementsBuffer)
		HdGl.glDrawElements(OpenGLConstants.GL_TRIANGLES, indexCount, indexType, elementsBuffer.getAddress() + byteOffset.toLong())
	}

	@Synchronized
	override fun tick(time: Int) {
		var count: Int = 0
		val maskedTime: Int = time and Int.MAX_VALUE
		var intNode: IntNode?
		while (!this.vboBuffersAwaitingDeallocation.isEmpty()) {
			intNode = this.vboBuffersAwaitingDeallocation.removeFirst() as IntNode?
			HdToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.vboBuffersSize -= intNode.value
			if (count == 1000) {
				HdGl.glDeleteBuffersARB(count, HdToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			HdGl.glDeleteBuffersARB(count, HdToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.texturesAwaitingDeallocation.isEmpty()) {
			intNode = this.texturesAwaitingDeallocation.removeFirst() as IntNode?
			HdToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.textureBuffersSize -= intNode.value
			if (count == 1000) {
				HdGl.glDeleteTextures(count, HdToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			HdGl.glDeleteTextures(count, HdToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.frameBuffersAwaitingDeallocation.isEmpty()) {
			intNode = this.frameBuffersAwaitingDeallocation.removeFirst() as IntNode?
			HdToolkit.deleteIdBuffer[count++] = intNode!!.value
			if (count == 1000) {
				HdGl.glDeleteFramebuffersEXT(count, HdToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			HdGl.glDeleteFramebuffersEXT(count, HdToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.renderBuffersAwaitingRemoval.isEmpty()) {
			intNode = this.renderBuffersAwaitingRemoval.removeFirst() as IntNode?
			HdToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.renderBuffersSize -= intNode.value
			if (count == 1000) {
				HdGl.glDeleteRenderbuffersEXT(count, HdToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			HdGl.glDeleteRenderbuffersEXT(count, HdToolkit.deleteIdBuffer, 0)
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = this.listsAwaitingDeallocation.removeFirst() as IntNode?
			HdGl.glDeleteLists(intNode!!.key.toInt(), intNode.value)
		}
		var node: Node?
		while (!this.arbProgramsAwaitingDeallocation.isEmpty()) {
			node = this.arbProgramsAwaitingDeallocation.removeFirst()
			HdGl.glDeleteProgramARB(node!!.key.toInt())
		}
		while (!this.shaderProgramsAwaitingDeallocation.isEmpty()) {
			node = this.shaderProgramsAwaitingDeallocation.removeFirst()
			HdGl.glDeleteObjectARB(node!!.key)
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = this.listsAwaitingDeallocation.removeFirst() as IntNode?
			HdGl.glDeleteLists(intNode!!.key.toInt(), intNode.value)
		}
		this.textureCache.clean()
		if (this.E() > 100663296 && this.lastGcTime + TimeUtils.MILLISECONDS_PER_MINUTE < Utils.currentTimeMillis()) {
			lang.gc()
			this.lastGcTime = Utils.currentTimeMillis()
		}
		this.lastTickMillis = maskedTime
	}

	override fun line(x1: Int, y1: Int, x2: Int, y2: Int, colour: Int, mode: Int) {
		this.enter2dMode()
		this.setBlendMode(mode)
		var dx: Float = x2.toFloat() - x1.toFloat()
		var dy: Float = (-y1).toFloat() + y2.toFloat()
		if (dx == 0.0f && dy == 0.0f) {
			dx = 1.0f
		} else {
			val inverseLength: Float = (1.0 / kotlin.math.sqrt((dx * dx + dy * dy).toDouble())).toFloat()
			dx *= inverseLength
			dy *= inverseLength
		}
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		HdGl.glBegin(OpenGLConstants.GL_LINES)
		HdGl.glVertex2f(x1.toFloat() + 0.35f, y1.toFloat() + 0.35f)
		HdGl.glVertex2f(dx + x2.toFloat() + 0.35f, dy + 0.35f + y2.toFloat())
		HdGl.glEnd()
	}

	override fun b(x: Int, y: Int, width: Int, height: Int, depthOffset: Double) {
	}

	fun setupOrthoMatrixRendering() {
		if (this.functionMask == 16) {
			return
		}
		this.setOrthoProjectionMode()
		this.toggleFog(true)
		this.toggleZbuffer(true)
		this.setDepthMask(true)
		this.setBlendMode(1)
		this.functionMask = 16
	}

	fun setColourOp(op: Int) {
		if (op == 1) {
			this.loadTextureCombineMode(7681, 7681)
		} else if (op == 0) {
			this.loadTextureCombineMode(8448, 8448)
		} else if (op == 2) {
			this.loadTextureCombineMode(34165, 7681)
		} else if (op == 3) {
			this.loadTextureCombineMode(260, 8448)
		} else if (op == 4) {
			this.loadTextureCombineMode(34023, 34023)
			return
		}
	}

	override fun forceFixedScreenmode(): Boolean {
		return true
	}

	fun setTexture(source: Int, operand: Int, activeUnit: Int) {
		HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_SRC0_RGB, source)
		HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_OPERAND0_RGB, operand)
	}

	private fun setScreenProjectionMode() {
		if (this.depthClippingMode == 1) {
			return
		}
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadIdentity()
		if (this.viewportWidth > 0 && this.viewportHeight > 0) {
			HdGl.glOrtho(0.0, this.viewportWidth.toDouble(), this.viewportHeight.toDouble(), 0.0, -1.0, 1.0)
		}
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glLoadIdentity()
		this.depthClippingMode = 1
		this.functionMask = this.functionMask and 0xFFFFFFE7.toInt()
		this.updateHdScene()
	}

	fun popDrawTarget(target: GlRenderTarget?) {
		if (this.drawTargetIndex < 0 || this.drawTargetStack[this.drawTargetIndex] !== target) {
			throw RuntimeException()
		}
		this.drawTargetStack[this.drawTargetIndex--] = null
		target!!.unbindDraw()
		if (this.drawTargetIndex < 0) {
			this.drawTarget = null
		} else {
			this.drawTarget = this.drawTargetStack[this.drawTargetIndex]
			this.drawTarget!!.bindDraw()
		}
	}

	@Synchronized
	fun requestArbProgramCleanup(programHandle: Int) {
		val node: Node = Node()
		node.key = programHandle.toLong()
		this.arbProgramsAwaitingDeallocation.addLast(node)
	}

	fun loadMatrix(matrix4x4: Matrix43?) {
		HdGl.glLoadMatrixf(matrix4x4!!.getColumnMajor(), 0)
	}

	override fun setBloomParams(whitePoint: Float, bloom: Float, brightpass: Float) {
		HdRectangularTexture.brightpass = brightpass
		HdRectangularTexture.whitePoint = whitePoint
		HdRectangularTexture.bloom = bloom
	}

	override fun releaseSurface(canvas: Canvas?) {
		if (canvas === this.canvas) {
			throw RuntimeException()
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
			this.activeOpenglInstance!!.releaseSurface(canvas, long2!!)
			this.canvasSurfaces.remove(canvas)
		}
	}

	private fun renderStationaryTexture() {
		if (this.textureUpdate) {
			HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
			HdGl.glLoadIdentity()
			HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			this.textureUpdate = false
		}
	}

	override fun extendBuildArea(): Boolean {
		return true
	}

	override fun za(x: Int, y: Int, radius: Int, colour: Int, blendMode: Int) {
		var radius: Int = radius
		if (radius < 0) {
			radius = -radius
		}
		if (x + radius < this.clipLeft || this.clipRight < x - radius || this.clipTop > y + radius || this.clipBottom < y - radius) {
			return
		}
		this.enter2dMode()
		this.setBlendMode(blendMode)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		val centerX: Float = x.toFloat() + 0.35f
		val centerY: Float = y.toFloat() + 0.35f
		val diameter: Int = radius shl 1
		if (this.fillCirclePointSize > diameter.toFloat()) {
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glVertex2f(centerX + 1.0f, centerY + 1.0f)
			HdGl.glVertex2f(centerX + 1.0f, -1.0f + centerY)
			HdGl.glVertex2f(centerX - 1.0f, centerY - 1.0f)
			HdGl.glVertex2f(centerX - 1.0f, centerY + 1.0f)
			HdGl.glEnd()
		} else if (this.outlineCirclePointSize >= diameter.toFloat()) {
			HdGl.glEnable(OpenGLConstants.GL_POINT_SMOOTH)
			HdGl.glPointSize(diameter.toFloat())
			HdGl.glBegin(OpenGLConstants.GL_POINTS)
			HdGl.glVertex2f(centerX, centerY)
			HdGl.glEnd()
			HdGl.glDisable(OpenGLConstants.GL_POINT_SMOOTH)
		} else {
			HdGl.glBegin(OpenGLConstants.GL_TRIANGLE_FAN)
			HdGl.glVertex2f(centerX, centerY)
			var angleStep: Int = 262144 / (radius * 6)
			if (angleStep <= 64) {
				angleStep = 64
			} else if (angleStep > 512) {
				angleStep = 512
			}
			angleStep = Utils.floorPowerOfTwo(angleStep)
			HdGl.glVertex2f(radius.toFloat() + centerX, centerY)
			var i: Int = 16384 - angleStep
			while (i > 0) {
				HdGl.glVertex2f(radius.toFloat() * OggStreamDecoder.COSINE[i] + centerX, radius.toFloat() * OggStreamDecoder.SINE[i] + centerY)
				i -= angleStep
			}
			HdGl.glVertex2f(centerX + radius.toFloat(), centerY)
			HdGl.glEnd()
		}
	}

	override fun linkThreads(threadId: Int) {
		this.attach()
	}

	private fun toggleLighting() {
		if (this.lightingEnabled && !this.hideLights) {
			HdGl.glEnable(OpenGLConstants.GL_LIGHTING)
		} else {
			HdGl.glDisable(OpenGLConstants.GL_LIGHTING)
		}
	}

	override fun stop() {
		if (this.activeOpenglInstance != null) {
			this.hdPost.release()
			this.frameUniforms.release()
			this.hdFeatures.release()
			this.sceneRenderer.release()
			this.vertexStream.release()
			this.elementStream.release()
		}
		var node: Node? = this.heapManager.first()
		while (node != null) {
			(node as GlMemoryPool).deallocate()
			node = this.heapManager.next()
		}
		if (this.framebufferCompositor != null) {
			this.framebufferCompositor!!.shutdown()
		}
		this.dropSceneTarget() // Not part of the original client: see beginScene.
		if (this.activeOpenglInstance != null) {
			this.detach()
			val enumeration: Enumeration<Any?> = this.canvasSurfaces.keys()
			while (enumeration.hasMoreElements()) {
				val canvas: Canvas? = enumeration.nextElement() as Canvas?
				val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
				this.activeOpenglInstance!!.releaseSurface(canvas, long2!!)
			}
			this.activeOpenglInstance!!.release()
			this.activeOpenglInstance = null
		}
		if (this.colortableReset) {
			ColorUtil.releaseColorTables(true, false)
			this.colortableReset = false
		}
	}

	override fun renderLastFrame() {
		if (!this.s3Graphics || this.viewportWidth <= 0 || this.viewportHeight <= 0) {
			return
		}
		if (this.scaledTarget != null) {
			// Not part of the original client: the scaled target keeps the last frame, so there is no
			// front buffer to copy back from, and the window's buffers cannot be chosen while it is bound.
			return
		}
		val savedClipLeft: Int = this.clipLeft
		val savedClipRight: Int = this.clipRight
		val savedClipTop: Int = this.clipTop
		val savedClipBottom: Int = this.clipBottom
		this.la()
		HdGl.glReadBuffer(OpenGLConstants.GL_FRONT)
		HdGl.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.setOrthoMatrixMode()
		this.toggleFog(false)
		this.setLightingEnabled(false)
		this.toggleZbuffer(false)
		this.setDepthMask(false)
		this.setActiveTexture(null)
		this.renderTexture(-2)
		this.setColourOp(1)
		this.setBlendMode(0)
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadIdentity()
		HdGl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glLoadIdentity()
		HdGl.glRasterPos2i(0, 0)
		HdGl.glCopyPixels(0, 0, this.viewportWidth, this.viewportHeight, OpenGLConstants.GL_COLOR)
		HdGl.glFlush()
		HdGl.glReadBuffer(OpenGLConstants.GL_BACK)
		HdGl.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.KA(savedClipLeft, savedClipTop, savedClipRight, savedClipBottom)
	}

	override fun createCubeTexture(materialAtPositiveX: Int, materialAtNegativeX: Int, materialAtPositiveY: Int, materialAtNegativeY: Int, materialAtPositiveZ: Int, materialAtNegativeZ: Int): EnvironmentSampler? {
		return if (this.arbTextureCubeMap) HdArbTextureCubeMapGenerator(this, materialAtPositiveX, materialAtNegativeX, materialAtPositiveY, materialAtNegativeY, materialAtPositiveZ, materialAtNegativeZ) else null
	}

	@Synchronized
	fun requestShaderProgramCleanup(programHandle: Long) {
		val node: Node = Node()
		node.key = programHandle
		this.shaderProgramsAwaitingDeallocation.addLast(node)
	}

	fun popTarget(target: GlRenderTarget?) {
		if (this.framebufferBlit) {
			this.popReadTarget(target)
			this.popDrawTarget(target)
		} else if (this.targetIndex >= 0 && this.targetStack[this.targetIndex] === target) {
			this.targetStack[this.targetIndex--] = null
			target!!.unbind()
			if (this.targetIndex < 0) {
				this.drawTarget = null
				this.readTarget = null
			} else {
				this.drawTarget = this.targetStack[this.targetIndex]
				this.readTarget = this.drawTarget
				this.readTarget!!.bind()
			}
		} else {
			throw RuntimeException()
		}
	}

	fun setActiveTexture(replacingCombiner: HdTexture?) {
		val current: HdTexture? = this.openglTextures!![this.glTexturesCursorPos]
		if (current !== replacingCombiner) {
			if (replacingCombiner == null) {
				HdGl.glDisable(current!!.target)
			} else {
				if (current == null) {
					HdGl.glEnable(replacingCombiner.target)
				} else if (replacingCombiner.target != current.target) {
					HdGl.glDisable(current.target)
					HdGl.glEnable(replacingCombiner.target)
				}
				HdGl.glBindTexture(replacingCombiner.target, replacingCombiner.getId())
			}
			this.openglTextures!![this.glTexturesCursorPos] = replacingCombiner
		}
		this.functionMask = this.functionMask and 0xFFFFFFFE.toInt()
	}

	override fun stopBloom() {
		if (this.rectangularTexture != null && this.rectangularTexture!!.isActive()) {
			this.framebufferCompositor!!.removeEffect(this.rectangularTexture)
			this.textureCache.reset()
		}
	}

	override fun drawLine(startX: Int, startY: Int, endX: Int, endY: Int, colorArgb: Int, dashLength: Int, gapLength: Int, offset: Int) {
		if (startX == endX && startY == endY) {
			return
		}
		this.enter2dMode()
		this.setBlendMode(1)
		val dx: Float = endX.toFloat() - startX.toFloat()
		val dy: Float = endY.toFloat() - startY.toFloat()
		val inverseLength: Float = (1.0 / kotlin.math.sqrt((dy * dy + dx * dx).toDouble())).toFloat()
		val dirX: Float = dx * inverseLength
		val dirY: Float = dy * inverseLength
		HdGl.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		val phase: Int = offset % (dashLength + gapLength)
		val dashX: Float = dashLength.toFloat() * dirX
		val dashY: Float = dirY * dashLength.toFloat()
		var startOffsetX: Float = 0.0f
		var startOffsetY: Float = 0.0f
		var segmentX: Float = dashX
		var segmentY: Float = dashY
		if (phase <= dashLength) {
			segmentY = (dashLength - phase).toFloat() * dirY
			segmentX = dirX * (dashLength - phase).toFloat()
		} else {
			startOffsetY = (gapLength + dashLength - phase).toFloat() * dirY
			startOffsetX = (gapLength + dashLength - phase).toFloat() * dirX
		}
		var currentX: Float = startOffsetX + startX.toFloat() + 0.35f
		var currentY: Float = startY.toFloat() + startOffsetY + 0.35f
		val gapX: Float = dirX * gapLength.toFloat()
		val gapY: Float = dirY * gapLength.toFloat()
		while (true) {
			if (endX <= startX) {
				if (endX.toFloat() + 0.35f > currentX) {
					break
				}
				if (currentX + segmentX < endX.toFloat()) {
					segmentX = endX.toFloat() - currentX
				}
			} else {
				if (currentX > endX.toFloat() + 0.35f) {
					break
				}
				if (currentX + segmentX > endX.toFloat()) {
					segmentX = endX.toFloat() - currentX
				}
			}
			if (startY >= endY) {
				if (endY.toFloat() + 0.35f > currentY) {
					break
				}
				if (segmentY + currentY < endY.toFloat()) {
					segmentY = endY.toFloat() - currentY
				}
			} else {
				if (endY.toFloat() + 0.35f < currentY) {
					break
				}
				if (segmentY + currentY > endY.toFloat()) {
					segmentY = endY.toFloat() - currentY
				}
			}
			HdGl.glBegin(OpenGLConstants.GL_LINES)
			HdGl.glVertex2f(currentX, currentY)
			HdGl.glVertex2f(currentX + segmentX, segmentY + currentY)
			HdGl.glEnd()
			currentY += segmentY + gapY
			currentX += gapX + segmentX
			segmentX = dashX
			segmentY = dashY
		}
	}

	override fun addCanvas(canvas: Canvas?, width: Int, height: Int) {
		if (this.canvas === canvas) {
			throw RuntimeException()
		} else if (!this.canvasSurfaces.containsKey(canvas)) {
			if (!canvas!!.isShowing()) {
				throw RuntimeException()
			}
			try {
				val class2: Class<*> = lang.forName("java.awt.Canvas")
				val method: Method = class2.getMethod("setIgnoreRepaint", lang.BOOLEAN_TYPE)
				method.invoke(canvas, true)
			} catch (ex: Exception) {
			}
			val surface: Long = this.activeOpenglInstance!!.prepareSurface(canvas)
			if (surface == -1L) {
				throw RuntimeException()
			}
			this.canvasSurfaces.put(canvas, (surface))
		}
	}

	private fun updateViewBounds() {
		this.viewX2 = (this.clipRight - this.projectionCenterX).toFloat()
		this.viewX1 = (this.clipLeft - this.projectionCenterX).toFloat()
		this.viewY1 = (this.clipTop - this.projectionCenterY).toFloat()
		this.viewY2 = (this.clipBottom - this.projectionCenterY).toFloat()
	}

	override fun createHeap(size: Int): MemoryPool {
		val glMemoryPool: GlMemoryPool = GlMemoryPool(size)
		this.heapManager.addLast(glMemoryPool)
		return glMemoryPool
	}

	override fun createGround(width: Int, length: Int, tileHeights: Array<IntArray?>?, nextLevelHeights: Array<IntArray?>?, groundFlags: Int, featureFlags: Int): Ground {
		return HdGround(this, groundFlags, featureFlags, width, length, tileHeights, nextLevelHeights, 512)
	}

	override fun hardShadow(): Boolean {
		return true
	}

	fun pushTarget(target: GlRenderTarget?) {
		if (this.framebufferBlit) {
			this.pushReadTarget(target)
			this.pushDrawTarget(target)
		} else if (this.targetIndex >= 3) {
			throw RuntimeException()
		} else {
			if (this.targetIndex >= 0) {
				this.targetStack[this.targetIndex]!!.unbind()
			}
			this.targetStack[++this.targetIndex] = target
			this.drawTarget = target
			this.readTarget = target
			this.readTarget!!.bind()
		}
	}

	private fun accumulateCapabilities(): Int {
		var errors: Int = 0
		this.gpuCompanyString = HdGl.glGetString(OpenGLConstants.GL_VENDOR)!!.lowercase(util.defaultLocale())
		this.gpuNameString = HdGl.glGetString(OpenGLConstants.GL_RENDERER)!!.lowercase(util.defaultLocale())
		if (this.gpuCompanyString!!.indexOf("microsoft") != -1) {
			errors = 1
		}
		// Not the original's rejection of Mesa: on Linux the real drivers are Mesa's, and every one
		// with a 3.3 core context will do.
		val string: String = HdGl.glGetString(OpenGLConstants.GL_VERSION)!!
		val strings: Array<String?> = Utils.split(' ', string.replace('.', ' '))
		if (strings.size >= 2) {
			try {
				val major: Int = Utils.parseInt(strings[0]!!)
				val minor: Int = Utils.parseInt(strings[1]!!)
				this.openglVersion = major * 10 + minor
			} catch (ex: NumberFormatException) {
				errors = errors or 0x4
			}
		} else {
			errors = errors or 0x4
		}
		if (this.openglVersion < 12) {
			errors = errors or 0x2
		}
		if (!this.activeOpenglInstance!!.a("GL_ARB_multitexture")) {
			errors = errors or 0x8
		}
		if (!this.activeOpenglInstance!!.a("GL_ARB_texture_env_combine")) {
			errors = errors or 0x20
		}
		val values: IntArray = IntArray(1)
		HdGl.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_UNITS, values, 0)
		this.maxTextureUnits = values[0]
		HdGl.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_COORDS, values, 0)
		this.maxTextureCoordinates = values[0]
		HdGl.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_IMAGE_UNITS, values, 0)
		this.maxTextureImageUnits = values[0]
		if (this.maxTextureUnits < 2 || this.maxTextureCoordinates < 2 || this.maxTextureImageUnits < 2) {
			errors = errors or 0x10
		}
		this.bigEndianess = Stream.c()
		this.pbuffersAvailable = this.activeOpenglInstance!!.arePbuffersAvailable()
		this.vertexbufferSupported = this.activeOpenglInstance!!.a("GL_ARB_vertex_buffer_object")
		this.arbMultisample = this.activeOpenglInstance!!.a("GL_ARB_multisample")
		this.arbVertexProgram = this.activeOpenglInstance!!.a("GL_ARB_vertex_program")
		this.activeOpenglInstance!!.a("GL_ARB_fragment_program")
		this.arbVertexShader = this.activeOpenglInstance!!.a("GL_ARB_vertex_shader")
		this.arbFragmentShader = this.activeOpenglInstance!!.a("GL_ARB_fragment_shader")
		this.texture3dSupported = this.activeOpenglInstance!!.a("GL_EXT_texture3D")
		this.arbRectangleTexturesSupported = this.activeOpenglInstance!!.a("GL_ARB_texture_rectangle")
		this.arbTextureCubeMap = this.activeOpenglInstance!!.a("GL_ARB_texture_cube_map")
		this.arbTextureFloat = this.activeOpenglInstance!!.a("GL_ARB_texture_float")
		this.framebufferObjectSupported = false
		this.extFramebufferObject = this.activeOpenglInstance!!.a("GL_EXT_framebuffer_object")
		this.framebufferBlit = this.activeOpenglInstance!!.a("GL_EXT_framebuffer_blit")
		this.framebufferMultisample = this.activeOpenglInstance!!.a("GL_EXT_framebuffer_multisample")
		this.advancedFramebuffers = this.framebufferMultisample and this.framebufferBlit
		HdGl.glGetFloatv(OpenGLConstants.GL_POINT_SIZE_RANGE, OpenGlToolkit.BUFFER_4F, 0)
		this.fillCirclePointSize = OpenGlToolkit.BUFFER_4F[0]
		this.outlineCirclePointSize = OpenGlToolkit.BUFFER_4F[1]
		return if (errors == 0) 0 else errors
	}

	override fun drawTexturedLine(startX: Int, startY: Int, endX: Int, endY: Int, colour: Int, clippingMask: ComponentSurface?, maskX: Int, maskY: Int, dashLength: Int, gapLength: Int, offset: Int) {
		if (endX == startX && startY == endY) {
			return
		}
		val mask: HdClippingMask? = clippingMask as HdClippingMask?
		val surface: Hd2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		val dx: Float = (-startX).toFloat() + endX.toFloat()
		val dy: Float = endY.toFloat() - startY.toFloat()
		val inverseLength: Float = (1.0 / kotlin.math.sqrt((dx * dx + dy * dy).toDouble())).toFloat()
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		val dirX: Float = dx * inverseLength
		val dirY: Float = dy * inverseLength
		val phase: Int = offset % (gapLength + dashLength)
		val dashX: Float = dashLength.toFloat() * dirX
		val dashY: Float = dirY * dashLength.toFloat()
		var startOffsetX: Float = 0.0f
		var startOffsetY: Float = 0.0f
		var segmentX: Float = dashX
		var segmentY: Float = dashY
		if (phase <= dashLength) {
			segmentX = dirX * (dashLength - phase).toFloat()
			segmentY = dirY * (dashLength - phase).toFloat()
		} else {
			startOffsetY = (dashLength + gapLength - phase).toFloat() * dirY
			startOffsetX = (dashLength + gapLength - phase).toFloat() * dirX
		}
		var currentX: Float = startOffsetX + startX.toFloat() + 0.35f
		var currentY: Float = startY.toFloat() + startOffsetY + 0.35f
		val gapX: Float = gapLength.toFloat() * dirX
		val gapY: Float = gapLength.toFloat() * dirY
		while (true) {
			if (endX > startX) {
				if (currentX > endX.toFloat() + 0.35f) {
					break
				}
				if (endX.toFloat() < currentX + segmentX) {
					segmentX = endX.toFloat() - currentX
				}
			} else {
				if (endX.toFloat() + 0.35f > currentX) {
					break
				}
				if (endX.toFloat() > currentX + segmentX) {
					segmentX = endX.toFloat() - currentX
				}
			}
			if (endY <= startY) {
				if (endY.toFloat() + 0.35f > currentY) {
					break
				}
				if (endY.toFloat() > currentY + segmentY) {
					segmentY = endY.toFloat() - currentY
				}
			} else {
				if (currentY > endY.toFloat() + 0.35f) {
					break
				}
				if (endY.toFloat() < segmentY + currentY) {
					segmentY = endY.toFloat() - currentY
				}
			}
			HdGl.glBegin(OpenGLConstants.GL_LINES)
			HdGl.glTexCoord2f(texScaleS * ((-maskX).toFloat() + currentX), texScaleT * ((-maskY).toFloat() + currentY))
			HdGl.glVertex2f(currentX, currentY)
			HdGl.glTexCoord2f((currentX + segmentX - maskX.toFloat()) * texScaleS, ((-maskY).toFloat() + currentY + segmentY) * texScaleT)
			HdGl.glVertex2f(segmentX + currentX, currentY + segmentY)
			HdGl.glEnd()
			currentX += segmentX + gapX
			currentY += segmentY + gapY
			segmentY = dashY
			segmentX = dashX
		}
		this.setTexture(5890, 768, 0)
	}

	override fun X(scale: Int) {
		var scale: Int = scale
		this.shadowScaleShift = 0
		while (scale > 1) {
			this.shadowScaleShift++
			scale = scale shr 0x1
		}
		this.shadowScale = 0x1 shl this.shadowScaleShift
	}

	override fun createMask(width: Int, height: Int, rowOffsets: IntArray?, rowWidths: IntArray?): ComponentSurface {
		return HdClippingMask.create(width, rowOffsets, rowWidths, this, height)
	}

	override fun accumulateViewportDimensions(dimensions: IntArray?) {
		dimensions!![0] = this.viewportWidth
		dimensions[1] = this.viewportHeight
	}

	override fun setHeap(memoryPool: MemoryPool?) {
		this.nativeHeap = (memoryPool as GlMemoryPool).heap
		if (this.screenQuadArrayBuffer != null) {
			return
		}
		val gpuPacket: GlBitBuffer = GlBitBuffer(80)
		if (this.bigEndianess) {
			gpuPacket.writeFloatBigEndian(-1.0f)
			gpuPacket.writeFloatBigEndian(-1.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(-1.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(-1.0f)
			gpuPacket.writeFloatBigEndian(1.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
			gpuPacket.writeFloatBigEndian(0.0f)
		} else {
			gpuPacket.writeFloatLittleEndian(-1.0f)
			gpuPacket.writeFloatLittleEndian(-1.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(-1.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(-1.0f)
			gpuPacket.writeFloatLittleEndian(1.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
			gpuPacket.writeFloatLittleEndian(0.0f)
		}
		this.screenQuadArrayBuffer = this.createArrayBuffer(false, 20, gpuPacket.buffer, gpuPacket.currentPosition)
		this.quadVertexBuffer = OpenGlArrayDrawBuffer(this.screenQuadArrayBuffer, 5126, 3, 0)
		this.quadTexCoordBuffer = OpenGlArrayDrawBuffer(this.screenQuadArrayBuffer, 5126, 2, 12)
		this.particleRenderer.initialize(this)
	}

	override fun A(colour: Int, clippingMask: ComponentSurface?, maskX: Int, maskY: Int) {
		val mask: HdClippingMask? = clippingMask as HdClippingMask?
		val surface: Hd2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glTexCoord2f(texScaleS * (this.clipLeft - maskX).toFloat(), (this.clipTop - maskY).toFloat() * texScaleT)
		HdGl.glVertex2i(this.clipLeft, this.clipTop)
		HdGl.glTexCoord2f(texScaleS * (this.clipLeft - maskX).toFloat(), texScaleT * (this.clipBottom - maskY).toFloat())
		HdGl.glVertex2i(this.clipLeft, this.clipBottom)
		HdGl.glTexCoord2f((this.clipRight - maskX).toFloat() * texScaleS, (this.clipBottom - maskY).toFloat() * texScaleT)
		HdGl.glVertex2i(this.clipRight, this.clipBottom)
		HdGl.glTexCoord2f(texScaleS * (this.clipRight - maskX).toFloat(), texScaleT * (this.clipTop - maskY).toFloat())
		HdGl.glVertex2i(this.clipRight, this.clipTop)
		HdGl.glEnd()
		this.setTexture(5890, 768, 0)
	}

	override fun replaceCanvasOnDisplayUpdate(): Boolean {
		return false
	}

	override fun createDepthBuffer(width: Int, height: Int): DepthBuffer? {
		return null
	}

	fun setTextureTransparency(textureUnit: Int, alpha: Int) {
		HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_SRC0_ALPHA, alpha)
		HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_OPERAND0_ALPHA, OpenGLConstants.GL_SRC_ALPHA)
	}

	private fun attach() {
		var attempts: Int = 0
		while (!this.activeOpenglInstance!!.b()) {
			if (attempts++ > 5) {
				throw RuntimeException("")
			}
			TimeUtils.sleep(1000L)
		}
	}

	override fun strongLine(x1: Int, y1: Int, x2: Int, y2: Int, colour: Int, width: Int) {
		HdGl.glLineWidth(width.toFloat())
		this.line(x1, y1, x2, y2, colour, 0)
		HdGl.glLineWidth(1.0f)
	}

	override fun ra(waterHeight: Int, fogColour: Int, depth: Int, intensity: Int) {
		this.waterHeight = waterHeight
		this.waterDepth = depth
		this.waterFogColour = fogColour
		this.stillWaterToggled = true
		this.waterIntensity = intensity
	}

	private fun updateDepthRange() {
		this.projectionMatrix[10] = this.projectionDepthScale
		this.projectionMatrix[14] = this.projectionDepthOffset
		this.farClipDistance = this.zFar.toFloat()
		this.projectedFarClipDistance = (this.projectionMatrix[14] - this.zFar.toFloat()) / this.projectionMatrix[10]
	}

	override fun T(left: Int, top: Int, right: Int, bottom: Int) {
		if (this.clipTop < top) {
			this.clipTop = top
		}
		if (this.clipBottom > bottom) {
			this.clipBottom = bottom
		}
		if (this.clipLeft < left) {
			this.clipLeft = left
		}
		if (right < this.clipRight) {
			this.clipRight = right
		}
		HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
		this.defineScissorBox()
	}

	override fun resizeCanvas(canvas: Canvas?, width: Int, height: Int) {
		var surface: Long = 0L
		if (canvas == null || this.canvas === canvas) {
			surface = this.canvasSurface
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
			surface = long2!!
		}
		if (surface == 0L) {
			throw RuntimeException()
		}
		this.activeOpenglInstance!!.surfaceResized(surface)
		if (this.currentCanvas === canvas) {
			this.updateCanvasSize()
		}
	}

	override fun createNativeSprite(pixels: IntArray?, rowLength: Int, width: Int, height: Int, copyPixels: Boolean): NativeSprite {
		return HdSprite(this, width, height, pixels, 0, rowLength)
	}

	private fun enter2dMode() {
		if (this.functionMask == 1) {
			return
		}
		this.setScreenProjectionMode()
		this.toggleFog(false)
		this.setLightingEnabled(false)
		this.toggleZbuffer(false)
		this.setDepthMask(false)
		this.setActiveTexture(null)
		this.renderTexture(-2)
		this.setColourOp(1)
		this.functionMask = 1
	}

	private fun updateSunDiffuse() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorIntensity * this.sunColorRed
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunColorIntensity
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunColorIntensity
		HdGl.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0)
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * -this.antiSunColorIntensity
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * -this.antiSunColorIntensity
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorRed * -this.antiSunColorIntensity
		HdGl.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0)
	}

	@Synchronized
	fun requestArbBufferCleanup(bufferSize: Int, bufferHandle: Int) {
		val intNode: IntNode = IntNode(bufferSize)
		intNode.key = bufferHandle.toLong()
		this.vboBuffersAwaitingDeallocation.addLast(intNode)
	}

	override fun M(): Int {
		val count: Int = this.modelCount
		this.modelCount = 0
		return count
	}

	fun getCombineModeEffectType(mode: Int): Int {
		if (mode == 1) {
			return 7681
		} else if (mode == 0) {
			return 8448
		} else if (mode == 2) {
			return 34165
		} else if (mode == 3) {
			return 260
		} else if (mode == 4) {
			return 34023
		} else {
			throw IllegalArgumentException()
		}
	}

	fun pushReadTarget(target: GlRenderTarget?) {
		if (this.readTargetIndex >= 3) {
			throw RuntimeException()
		}
		if (this.readTargetIndex >= 0) {
			this.readTargetStack[this.readTargetIndex]!!.unbindRead()
		}
		this.readTargetStack[++this.readTargetIndex] = target
		this.readTarget = target
		this.readTarget!!.bindRead()
	}

	override fun outlineRect(x: Int, y: Int, width: Int, height: Int, colour: Int, mode: Int) {
		val left: Float = x.toFloat() + 0.35f
		val top: Float = y.toFloat() + 0.35f
		val right: Float = left + width.toFloat() - 1.0f
		this.enter2dMode()
		val bottom: Float = top + height.toFloat() - 1.0f
		this.setBlendMode(mode)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.arbMultisample) {
			HdGl.glDisable(32925)
		}
		HdGl.glBegin(OpenGLConstants.GL_LINE_LOOP)
		HdGl.glVertex2f(left, top)
		HdGl.glVertex2f(left, bottom)
		HdGl.glVertex2f(right, bottom)
		HdGl.glVertex2f(right, top)
		HdGl.glEnd()
		if (this.arbMultisample) {
			HdGl.glEnable(32925)
		}
	}

	override fun H(x: Int, y: Int, z: Int, output: IntArray?) {
		val depth: Float = this.viewMatrix43.zD + x.toFloat() * this.viewMatrix43.zA + this.viewMatrix43.zB * y.toFloat() + this.viewMatrix43.zC * z.toFloat()
		if (depth == 0.0f) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		val screenX: Int = ((this.viewMatrix43.xD + this.viewMatrix43.xC * z.toFloat() + this.viewMatrix43.xB * y.toFloat() + this.viewMatrix43.xA * x.toFloat()) * this.projectionScaleX.toFloat() / depth).toInt()
		val screenY: Int = ((x.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yB * y.toFloat() + this.viewMatrix43.yC * z.toFloat() + this.viewMatrix43.yD) * this.projectionScaleY.toFloat() / depth).toInt()
		output!![0] = (screenX.toFloat() - this.viewX1).toInt()
		output[2] = depth.toInt()
		output[1] = (screenY.toFloat() - this.viewY1).toInt()
	}

	override fun createMatrix(): Matrix {
		return Matrix43()
	}

	override fun hasBloom(): Boolean {
		if (this.rectangularTexture == null) {
			return false
		}
		if (!this.rectangularTexture!!.isActive()) {
			if (!this.framebufferCompositor!!.addEffect(this.rectangularTexture)) {
				return false
			}
			this.textureCache.reset()
		}
		return true
	}

	private fun updatePointLights() {
		var lightIndex: Int = 0
		while (lightIndex < this.lastSceneLightCount) {
			val pointLight: PointLight? = this.lightNodes[lightIndex]
			OpenGlToolkit.LIGHT_MATRIX[0] = pointLight!!.getX().toFloat()
			val glLight: Int = lightIndex + 16386
			OpenGlToolkit.LIGHT_MATRIX[1] = pointLight.getY().toFloat()
			OpenGlToolkit.LIGHT_MATRIX[2] = pointLight.getZ().toFloat()
			OpenGlToolkit.LIGHT_MATRIX[3] = 1.0f
			HdGl.glLightfv(glLight, OpenGLConstants.GL_POSITION, OpenGlToolkit.LIGHT_MATRIX, 0)
			val range: Int = pointLight.getColour()
			val intensityScale: Float = pointLight.getIntensity() / 255.0f
			OpenGlToolkit.LIGHT_MATRIX[2] = (range and 0xFF).toFloat() * intensityScale
			OpenGlToolkit.LIGHT_MATRIX[0] = (range shr 16 and 0xFF).toFloat() * intensityScale
			OpenGlToolkit.LIGHT_MATRIX[1] = (range shr 8 and 0xFF).toFloat() * intensityScale
			HdGl.glLightfv(glLight, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.LIGHT_MATRIX, 0)
			HdGl.glLightf(glLight, OpenGLConstants.GL_QUADRATIC_ATTENUATION, 1.0f / (pointLight.getRadius() * pointLight.getRadius()).toFloat())
			HdGl.glEnable(glLight)
			lightIndex++
		}
		while (lightIndex < this.activeLightCount) {
			HdGl.glDisable(lightIndex + OpenGLConstants.GL_LIGHT2)
			lightIndex++
		}
		this.activeLightCount = this.lastSceneLightCount
	}

	fun setupMatrixRendering() {
		if (this.functionMask == 8) {
			return
		}
		this.setModelViewMatrixMode()
		this.toggleFog(true)
		this.toggleZbuffer(true)
		this.setDepthMask(true)
		this.setBlendMode(1)
		this.functionMask = 8
	}

	fun getDataTypeSize(dataType: Int): Int {
		if (dataType == 5121 || dataType == 5120) {
			return 1
		} else if (dataType == 5123 || dataType == 5122) {
			return 2
		} else if (dataType == 5125 || dataType == 5124 || dataType == 5126) {
			return 4
		} else {
			throw IllegalArgumentException("")
		}
	}

	override fun renderOrtho(particleList: ParticleList?, zoom: Int) {
		this.particleRenderer.drawParticles(this, zoom, particleList)
	}

	private fun buildTables() {
		this.openglTextures = arrayOfNulls<HdTexture>(this.maxTextureUnits)
		this.texture2d = Hd2DTexture(this, 3553, 6408, 1, 1)
		Hd2DTexture(this, 3553, 6408, 1, 1)
		Hd2DTexture(this, 3553, 6408, 1, 1)
		this.copyTarget1 = HdModel(this)
		this.copyTarget2 = HdModel(this)
		this.copyTarget3 = HdModel(this)
		this.copyTarget4 = HdModel(this)
		this.copyTarget5 = HdModel(this)
		this.copyBuffer1 = HdModel(this)
		this.copyBuffer2 = HdModel(this)
		this.copyBuffer3 = HdModel(this)
		this.copyBuffer4 = HdModel(this)
		this.copyBuffer5 = HdModel(this)
		if (this.extFramebufferObject) {
			this.integratedGraphicsImageProducer = HdFramebuffer(this)
			HdFramebuffer(this)
		}
	}

	private fun updateAmbient() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[0] = this.sunBrightness * this.sunColorRed
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunBrightness
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunBrightness
		HdGl.glLightModelfv(OpenGLConstants.GL_LIGHT_MODEL_AMBIENT, OpenGlToolkit.BUFFER_4F, 0)
	}

	override fun renderer(): Renderer {
		var vendor: Int = -1
		if (this.gpuCompanyString!!.indexOf("nvidia") != -1) {
			vendor = PciVendorId.NVIDIA
		} else if (this.gpuCompanyString!!.indexOf("intel") != -1) {
			vendor = PciVendorId.INTEL
		} else if (this.gpuCompanyString!!.indexOf("ati") != -1) {
			vendor = PciVendorId.AMD
		}
		return Renderer(vendor, "HdContext", this.openglVersion, this.gpuNameString, 0L)
	}

	override fun da(x: Int, y: Int, z: Int, output: IntArray?) {
		val depth: Float = y.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x.toFloat() + this.viewMatrix43.zC * z.toFloat() + this.viewMatrix43.zD
		if (depth < this.zNear.toFloat() || depth > this.zFar.toFloat()) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		val screenX: Int = (this.projectionScaleX.toFloat() * (y.toFloat() * this.viewMatrix43.xB + this.viewMatrix43.xA * x.toFloat() + this.viewMatrix43.xC * z.toFloat() + this.viewMatrix43.xD) / depth).toInt()
		val screenY: Int = ((this.viewMatrix43.yD + x.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yB * y.toFloat() + this.viewMatrix43.yC * z.toFloat()) * this.projectionScaleY.toFloat() / depth).toInt()
		if (this.viewX1 <= screenX.toFloat() && screenX.toFloat() <= this.viewX2 && this.viewY1 <= screenY.toFloat() && this.viewY2 >= screenY.toFloat()) {
			output!![2] = depth.toInt()
			output[0] = (screenX.toFloat() - this.viewX1).toInt()
			output[1] = (screenY.toFloat() - this.viewY1).toInt()
		} else {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
		}
	}

	fun pushMatrix() {
		HdGl.glPushMatrix()
	}

	override fun F(offsetX: Int, offsetY: Int) {
	}

	override fun swapSurface(surface: OffscreenSurface?) {
	}

	override fun KA(left: Int, top: Int, right: Int, bottom: Int) {
		var left: Int = left
		var top: Int = top
		var right: Int = right
		var bottom: Int = bottom
		if (top < 0) {
			top = 0
		}
		if (this.viewportWidth < right) {
			right = this.viewportWidth
		}
		if (left < 0) {
			left = 0
		}
		if (this.viewportHeight < bottom) {
			bottom = this.viewportHeight
		}
		this.clipRight = right
		this.clipBottom = bottom
		this.clipTop = top
		this.clipLeft = left
		HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
		this.defineScissorBox()
	}

	fun setOrthoZoom(zoom: Float) {
		if (this.orthoZoom != zoom) {
			this.orthoZoom = zoom
			if (this.depthClippingMode == 3) {
				this.loadOrthoProjection()
			}
		}
	}

	override fun la() {
		this.clipTop = 0
		this.clipBottom = this.viewportHeight
		this.clipRight = this.viewportWidth
		this.clipLeft = 0
		HdGl.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
	}

	override fun C(enabled: Boolean) {
		this.depthBufferingEnabled = enabled
		this.setDepthMask()
	}

	override fun cacheReset() {
	}

	fun setViewportOffset(offsetY: Int, offsetX: Int) {
		this.viewportOffsetY = offsetY
		this.viewportOffsetX = offsetX
		this.updateViewport()
		this.defineScissorBox()
	}

	override fun JA(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Int {
		var outcode: Int = 0
		var depth1: Float = z1.toFloat() * this.viewMatrix43.zC + x1.toFloat() * this.viewMatrix43.zA + y1.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zD
		if (depth1 < 1.0f) {
			depth1 = 1.0f
		}
		var depth2: Float = z2.toFloat() * this.viewMatrix43.zC + y2.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x2.toFloat() + this.viewMatrix43.zD
		if (depth2 < 1.0f) {
			depth2 = 1.0f
		}
		if (this.zNear.toFloat() > depth1 && this.zNear.toFloat() > depth2) {
			outcode = outcode or 0x10
		} else if (depth1 > this.zFar.toFloat() && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		val screenX1: Int = ((this.viewMatrix43.xC * z1.toFloat() + this.viewMatrix43.xB * y1.toFloat() + this.viewMatrix43.xA * x1.toFloat() + this.viewMatrix43.xD) * this.projectionScaleX.toFloat() / depth1).toInt()
		val screenX2: Int = (this.projectionScaleX.toFloat() * (z2.toFloat() * this.viewMatrix43.xC + this.viewMatrix43.xA * x2.toFloat() + this.viewMatrix43.xB * y2.toFloat() + this.viewMatrix43.xD) / depth2).toInt()
		if (screenX1.toFloat() < this.viewX1 && this.viewX1 > screenX2.toFloat()) {
			outcode = outcode or 0x1
		} else if (screenX1.toFloat() > this.viewX2 && screenX2.toFloat() > this.viewX2) {
			outcode = outcode or 0x2
		}
		val screenY1: Int = (this.projectionScaleY.toFloat() * (this.viewMatrix43.yC * z1.toFloat() + y1.toFloat() * this.viewMatrix43.yB + x1.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yD) / depth1).toInt()
		val screenY2: Int = ((this.viewMatrix43.yD + x2.toFloat() * this.viewMatrix43.yA + y2.toFloat() * this.viewMatrix43.yB + this.viewMatrix43.yC * z2.toFloat()) * this.projectionScaleY.toFloat() / depth2).toInt()
		if (screenY1.toFloat() < this.viewY1 && this.viewY1 > screenY2.toFloat()) {
			outcode = outcode or 0x4
		} else if (screenY1.toFloat() > this.viewY2 && screenY2.toFloat() > this.viewY2) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	override fun utilisesNativeHeap(): Boolean {
		return true
	}

	override fun generateEmptySprite(width: Int, height: Int, hasAlpha: Boolean): NativeSprite {
		return HdSprite(this, width, height, hasAlpha)
	}

	override fun method7281(threadId: Int) {
		this.detach()
	}

	private fun prepareForRendering() {
		this.renderTexture(-2)
		var i: Int = this.maxTextureUnits - 1
		while (i >= 0) {
			this.activateTexture(i)
			this.setActiveTexture(null)
			HdGl.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_MODE, OpenGLConstants.GL_COMBINE)
			i--
		}
		this.loadTextureCombineMode(8448, 8448)
		this.setTexture(34168, 770, 2)
		this.renderStationaryTexture()
		this.activeBlendAlpha = 1
		HdGl.glEnable(OpenGLConstants.GL_BLEND)
		HdGl.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA)
		this.activeBlendType = 1
		HdGl.glEnable(OpenGLConstants.GL_ALPHA_TEST)
		HdGl.glAlphaFunc(OpenGLConstants.GL_GREATER, 0.0f)
		this.alphaTestEnabled = true
		HdGl.glColorMask(true, true, true, true)
		this.colormaskActive = true
		this.toggleFog(true)
		this.setLightingEnabled(true)
		this.toggleZbuffer(true)
		this.setDepthMask(true)
		this.setOrthoMatrixMode()
		this.activeOpenglInstance!!.setSwapInterval(0)
		HdGl.glShadeModel(OpenGLConstants.GL_SMOOTH)
		HdGl.glClearDepth(1.0f)
		HdGl.glDepthFunc(OpenGLConstants.GL_LEQUAL)
		HdGl.glPolygonMode(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_FILL)
		HdGl.glEnable(OpenGLConstants.GL_CULL_FACE)
		HdGl.glCullFace(OpenGLConstants.GL_BACK)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glLoadIdentity()
		HdGl.glColorMaterial(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_AMBIENT_AND_DIFFUSE)
		HdGl.glEnable(OpenGLConstants.GL_COLOR_MATERIAL)
		val zeroAmbient: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		for (j in 0 until 8) {
			val glLight: Int = j + 16384
			HdGl.glLightfv(glLight, OpenGLConstants.GL_AMBIENT, zeroAmbient, 0)
			HdGl.glLightf(glLight, OpenGLConstants.GL_CONSTANT_ATTENUATION, 0.0f)
			HdGl.glLightf(glLight, OpenGLConstants.GL_LINEAR_ATTENUATION, 0.0f)
		}
		HdGl.glEnable(OpenGLConstants.GL_LIGHT0)
		HdGl.glEnable(OpenGLConstants.GL_LIGHT1)
		HdGl.glFogf(OpenGLConstants.GL_FOG_DENSITY, 0.95f)
		HdGl.glFogi(OpenGLConstants.GL_FOG_MODE, OpenGLConstants.GL_LINEAR)
		HdGl.glHint(OpenGLConstants.GL_FOG_HINT, OpenGLConstants.GL_FASTEST)
		this.fogColor = -1
		this.sunColorRgb = -1
		this.la()
	}

	fun setupSpriteRendering() {
		if (this.functionMask == 2) {
			return
		}
		this.setScreenProjectionMode()
		this.toggleFog(false)
		this.setLightingEnabled(false)
		this.toggleZbuffer(false)
		this.setDepthMask(false)
		this.renderTexture(-2)
		this.functionMask = 2
	}

	override fun I(): Int {
		val count: Int = this.polygonCount
		this.polygonCount = 0
		return count
	}

	fun setElementsBuffer(elementsBuffer: ElementsBufferGL?) {
		if (this.glBinding !== elementsBuffer) {
			if (this.vertexbufferSupported) {
				HdGl.glBindBufferARB(OpenGLConstants.GL_ELEMENT_ARRAY_BUFFER, elementsBuffer!!.getId())
			}
			this.glBinding = elementsBuffer
		}
	}

	fun toggleDrawingClientstates(vertices: OpenGlArrayDrawBuffer?, colors: OpenGlArrayDrawBuffer?, textureCoordinates: OpenGlArrayDrawBuffer?, normals: OpenGlArrayDrawBuffer?) {
		if (vertices == null) {
			HdGl.glDisableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
		} else {
			this.setArrayBuffer(vertices.buffer)
			HdGl.glVertexPointer(vertices.size.toInt(), vertices.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + vertices.offset.toLong())
			HdGl.glEnableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
		}
		if (normals == null) {
			HdGl.glDisableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
		} else {
			this.setArrayBuffer(normals.buffer)
			HdGl.glNormalPointer(normals.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + normals.offset.toLong())
			HdGl.glEnableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
		}
		if (colors == null) {
			HdGl.glDisableClientState(OpenGLConstants.GL_COLOR_ARRAY)
		} else {
			this.setArrayBuffer(colors.buffer)
			HdGl.glColorPointer(colors.size.toInt(), colors.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + colors.offset.toLong())
			HdGl.glEnableClientState(OpenGLConstants.GL_COLOR_ARRAY)
		}
		if (textureCoordinates == null) {
			HdGl.glDisableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
		} else {
			this.setArrayBuffer(textureCoordinates.buffer)
			HdGl.glTexCoordPointer(textureCoordinates.size.toInt(), textureCoordinates.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + textureCoordinates.offset.toLong())
			HdGl.glEnableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
		}
	}

	override fun getMaxLights(): Int {
		return 4
	}

	override fun createOffscreenSurface(surface: Sprite?, buffer: DepthBuffer?): OffscreenSurface? {
		return null
	}

	override fun camera(): Matrix {
		return this.viewMatrix43
	}

	private fun setDepthMask() {
		HdGl.glDepthMask(this.enableDepthMask && this.depthBufferingEnabled)
	}

	override fun scratchMatrix(): Matrix {
		return this.scratchMatrix
	}

	override fun i(): Int {
		return this.zNear
	}

	override fun ya() {
		this.setDepthMask(true)
		HdGl.glClear(OpenGLConstants.GL_DEPTH_BUFFER_BIT)
	}

	override fun method7250(enabled: Boolean) {
	}

	fun setLightingSourceParams() {
		HdGl.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_POSITION, this.sunDirection, 0)
		HdGl.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_POSITION, this.lightingDirection, 0)
	}

	override fun stillwaterEffectLoaded(): Boolean {
		return this.effectManager.isStillWaterSupported()
	}

	override fun bloom(): Boolean {
		return this.rectangularTexture != null && this.rectangularTexture!!.isActive()
	}

	override fun copyLights(count: Int, pointLights: Array<PointLight?>?) {
		for (i in 0 until count) {
			this.lightNodes[i] = pointLights!![i]
		}
		this.lastSceneLightCount = count
		if (this.depthClippingMode != 1) {
			this.updatePointLights()
		}
	}

	override fun setShrinkTextures(shrink: Boolean) {
	}

	private fun detach() {
		this.activeOpenglInstance!!.a()
	}

	override fun pa() {
		this.stillWaterToggled = false
	}

	override fun r(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, projectionDepth: Int): Int {
		val depth1: Float = z1.toFloat() * this.viewMatrix43.zC + y1.toFloat() * this.viewMatrix43.zB + x1.toFloat() * this.viewMatrix43.zA + this.viewMatrix43.zD
		val depth2: Float = z2.toFloat() * this.viewMatrix43.zC + this.viewMatrix43.zA * x2.toFloat() + y2.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zD
		var outcode: Int = 0
		if (depth1 < this.zNear.toFloat() && this.zNear.toFloat() > depth2) {
			outcode = outcode or 0x10
		} else if (this.zFar.toFloat() < depth1 && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		val screenX1: Int = (this.projectionScaleX.toFloat() * (this.viewMatrix43.xC * z1.toFloat() + this.viewMatrix43.xB * y1.toFloat() + this.viewMatrix43.xA * x1.toFloat() + this.viewMatrix43.xD) / projectionDepth.toFloat()).toInt()
		val screenX2: Int = (this.projectionScaleX.toFloat() * (this.viewMatrix43.xD + this.viewMatrix43.xC * z2.toFloat() + x2.toFloat() * this.viewMatrix43.xA + this.viewMatrix43.xB * y2.toFloat()) / projectionDepth.toFloat()).toInt()
		if (this.viewX1 > screenX1.toFloat() && this.viewX1 > screenX2.toFloat()) {
			outcode = outcode or 0x1
		} else if (this.viewX2 < screenX1.toFloat() && screenX2.toFloat() > this.viewX2) {
			outcode = outcode or 0x2
		}
		val screenY1: Int = ((this.viewMatrix43.yD + this.viewMatrix43.yC * z1.toFloat() + this.viewMatrix43.yA * x1.toFloat() + y1.toFloat() * this.viewMatrix43.yB) * this.projectionScaleY.toFloat() / projectionDepth.toFloat()).toInt()
		val screenY2: Int = (this.projectionScaleY.toFloat() * (z2.toFloat() * this.viewMatrix43.yC + y2.toFloat() * this.viewMatrix43.yB + this.viewMatrix43.yA * x2.toFloat() + this.viewMatrix43.yD) / projectionDepth.toFloat()).toInt()
		if (this.viewY1 > screenY1.toFloat() && screenY2.toFloat() < this.viewY1) {
			outcode = outcode or 0x4
		} else if (screenY1.toFloat() > this.viewY2 && this.viewY2 < screenY2.toFloat()) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	fun getBytesPerPixel(format: Int): Int {
		if (format == 6406 || format == 6409) {
			return 1
		} else if (format == 6410 || format == 34846 || format == 34844) {
			return 2
		} else if (format == 6407) {
			return 3
		} else if (format == 6408 || format == 34847) {
			return 4
		} else if (format == 34843) {
			return 6
		} else if (format == 34842) {
			return 8
		} else if (format == 6402) {
			return 3
		} else if (format == 6401) {
			return 1
		} else {
			throw IllegalArgumentException("")
		}
	}

	private fun renderAnimatedTexture(offsetV: Float, offsetW: Float, offsetU: Float) {
		HdGl.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		if (this.textureUpdate) {
			HdGl.glLoadIdentity()
		}
		HdGl.glTranslatef(offsetU, offsetV, offsetW)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		this.textureUpdate = true
	}

	@Synchronized
	fun requestFrameBufferCleanup(bufferSize: Int) {
		val intNode: IntNode = IntNode(bufferSize)
		this.frameBuffersAwaitingDeallocation.addLast(intNode)
	}

	override fun compositeBloomEffects() {
		this.framebufferCompositor!!.compositeEffects()
	}

	override fun P(x: Int, y: Int, height: Int, colour: Int, blendMode: Int) {
		this.enter2dMode()
		this.setBlendMode(blendMode)
		val startX: Float = x.toFloat() + 0.35f
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		val startY: Float = y.toFloat() + 0.35f
		HdGl.glBegin(OpenGLConstants.GL_LINES)
		HdGl.glVertex2f(startX, startY)
		HdGl.glVertex2f(startX, height.toFloat() + startY)
		HdGl.glEnd()
	}

	override fun E(): Int {
		return this.renderBuffersSize + this.textureBuffersSize + this.vboBuffersSize
	}

	override fun drawQuad(environmentSampler: EnvironmentSampler?, environmentSampler2: EnvironmentSampler?, alpha: Float, environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		if (environmentSampler != null && environmentSampler2 != null && this.arbTextureCubeMap && this.extFramebufferObject) {
			var result: HdCubeTextureGenerator? = null
			val glEnvironmentSampler: HdEnvironmentSampler = environmentSampler as HdEnvironmentSampler
			val glEnvironmentSampler2: HdEnvironmentSampler = environmentSampler2 as HdEnvironmentSampler
			val glCubeTexture: HdCubeTexture? = glEnvironmentSampler.getCubeTexture()
			val glCubeTexture2: HdCubeTexture? = glEnvironmentSampler2.getCubeTexture()
			if (glCubeTexture != null && glCubeTexture2 != null) {
				val size: Int = if (glCubeTexture2.size >= glCubeTexture.size) glCubeTexture2.size else glCubeTexture.size
				if (environmentSampler3 !== environmentSampler && environmentSampler2 !== environmentSampler3 && environmentSampler3 is HdCubeTextureGenerator) {
					val previous: HdCubeTextureGenerator = environmentSampler3
					if (size == previous.getSize()) {
						result = previous
					}
				}
				if (result == null) {
					result = HdCubeTextureGenerator(this, size)
				}
				if (result.blendCubeTextures(glCubeTexture, alpha, glCubeTexture2)) {
					return result
				}
			}
		}
		return if (alpha < 0.5f) environmentSampler else environmentSampler2
	}

	override fun setCanvas(canvas: Canvas?) {
		this.currentCanvas = null
		this.currentSurface = 0L
		if (canvas == null || canvas === this.canvas) {
			this.currentCanvas = this.canvas
			this.currentSurface = this.canvasSurface
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
			this.currentSurface = long2!!
			this.currentCanvas = canvas
		}
		if (this.currentCanvas == null || this.currentSurface == 0L) {
			throw RuntimeException()
		}
		this.activeOpenglInstance!!.setSurface(this.currentSurface)
		this.updateCanvasSize()
	}

	fun renderTexture(hasLight: Boolean, materialId: Int) {
		this.renderTexture(hasLight, materialId, true)
	}

	override fun compareFunctionMasks(maskA: Int, maskB: Int): Int {
		return maskB xor (maskA and maskB)
	}

	@Throws(FlipException::class)
	override fun flipDirtyRect(rectangles: Array<Rectangle?>?, count: Int, x: Int, y: Int) {
		this.flip(x, y)
	}

	private fun updateFog() {
		this.fogEndPosition = (this.zFar - this.fogOffset).toFloat() - this.fogMin
		this.fogStartPosition = this.fogEndPosition - this.densityFactor * this.fogDensity.toFloat()
		if (this.zNear.toFloat() > this.fogStartPosition) {
			this.fogStartPosition = this.zNear.toFloat()
		}
		HdGl.glFogf(OpenGLConstants.GL_FOG_START, this.fogStartPosition)
		HdGl.glFogf(OpenGLConstants.GL_FOG_END, this.fogEndPosition)
		OpenGlToolkit.BUFFER_4F[0] = (this.fogColor and 0xFF0000).toFloat() / 1.671168E7f
		OpenGlToolkit.BUFFER_4F[1] = (this.fogColor and 0xFF00).toFloat() / 65280.0f
		OpenGlToolkit.BUFFER_4F[2] = (this.fogColor and 0xFF).toFloat() / 255.0f
		HdGl.glFogfv(OpenGLConstants.GL_FOG_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	fun setHideLights(enabled: Boolean) {
		if (enabled != this.hideLights) {
			this.hideLights = enabled
			this.toggleLighting()
		}
	}

	@Throws(FlipException::class)
	override fun flip(x: Int, y: Int) {
		try {
			if (this.scaledTarget != null) {
				// Not part of the original client: the frame was drawn into the scaled target at the
				// game's size; copy it to the window, scaled, before the buffers swap.
				HdGl.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
				if (this.scaledMultisample != null) {
					HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
					HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
					HdGl.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, this.scaledWidth, this.scaledHeight, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
				}
				HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
				HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, 0)
				HdGl.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, ClientFeatures.scale(this.scaledWidth), ClientFeatures.scale(this.scaledHeight),
					OpenGLConstants.GL_COLOR_BUFFER_BIT, if (ClientFeatures.wholeScale()) OpenGLConstants.GL_NEAREST else OpenGLConstants.GL_LINEAR)
				if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
					HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
				}
				this.activeOpenglInstance!!.swapBuffers()
				HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer())
				return
			}
			this.activeOpenglInstance!!.swapBuffers()
		} catch (ex: Exception) {
		}
	}

	// Not part of the original client: a copy or read from the frame cannot come from a multisampled
	// framebuffer, so while the frame is the multisampled scaled target, the part wanted is resolved
	// into the scaled target and read from there until endReadFrame; a pushed framebuffer reads as is.
	private var readingResolved: Boolean = false

	fun beginReadFrame(x: Int, y: Int, width: Int, height: Int) {
		if (this.scaledMultisample == null || this.readTargetIndex >= 0 || this.drawTargetIndex >= 0) {
			return
		}
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		HdGl.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		HdGl.glBlitFramebufferEXT(x, y, x + width, y + height, x, y, x + width, y + height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
		if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
			HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		}
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		this.readingResolved = true
	}

	fun endReadFrame() {
		if (this.readingResolved) {
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
			this.readingResolved = false
		}
	}

	// Not part of the original client: the anti-aliasing pipeline (client.msaa, client.smaa,
	// client.renderScale). The 3D view of each frame is drawn into a framebuffer of its own, the
	// view's size, multisampled by the samples asked for, then resolved and drawn into the frame as
	// a textured quad, so that the interface drawn before and after it is never multisampled.
	private val sceneSettingSamples: Int
	private var sceneAvailable: Boolean = false
	private var sceneMaxSamples: Int = 0
	private var sceneTarget: HdSceneTarget? = null
	private var sceneActive: Boolean = false
	private var sceneX: Int = 0
	private var sceneY: Int = 0
	private var sceneWidth: Int = 0
	private var sceneHeight: Int = 0
	private var sceneSavedOffsetX: Int = 0
	private var sceneSavedOffsetY: Int = 0
	private var sceneScale: Double = 1.0
	private var smaaFilter: HdSmaaFilter? = null

	// Whether the viewport and clip are to be scaled now: the scene framebuffer is the draw target and
	// is drawn larger than the view. Another framebuffer pushed over it keeps its own pixels.
	private fun sceneScaling(): Boolean {
		return this.sceneActive && this.sceneScale != 1.0 && this.sceneTarget != null && this.drawTarget === this.sceneTarget!!.target()
	}

	private var sceneAlphaToCoverage: Boolean = false

	// Alpha-to-coverage for cut-out textures (alphaBlendMode 1) while the multisampled 3D view is the
	// draw target, so leaves and fences get smooth edges; off everywhere else, and with client.a2c=false.
	private fun setSceneAlphaToCoverage(cutout: Boolean) {
		val on: Boolean = cutout && ClientFeatures.a2c && this.sceneActive && this.sceneTarget != null && this.sceneTarget!!.multisample != null && this.drawTarget === this.sceneTarget!!.multisample
		if (on != this.sceneAlphaToCoverage) {
			if (on) {
				HdGl.glEnable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE)
			} else {
				HdGl.glDisable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE)
			}
			this.sceneAlphaToCoverage = on
		}
	}

	private fun sceneScaled(value: Int): Int {
		return kotlin.math.floor(value.toDouble() * this.sceneScale + 0.5).toInt()
	}

	// Decides, once the extensions are known, whether the pipeline can run on this driver. Without
	// framebuffer objects, blit and multisample it cannot. The window was made without samples before
	// the extensions could be known, and a failed renderer is rebuilt from a setting that is safe mode
	// at startup, so the renderer stays HdContext without anti-aliasing and says so.
	private fun checkScenePipeline() {
		// 634HD: the 3D view's framebuffer also carries the post-processing features (HdSettings),
		// so it is wanted whatever the anti-aliasing switches say
		if (ClientFeatures.aaPipelineUnavailable) {
			return
		}
		if (!this.extFramebufferObject || !this.framebufferBlit || !this.framebufferMultisample) {
			lang.printlnErr("client.msaa: this HdContext driver has no framebuffer objects, blit or multisample; anti-aliasing is off")
			ClientFeatures.aaPipelineUnavailable = true
			return
		}
		val max: IntArray = IntArray(1)
		HdGl.glGetIntegerv(OpenGLConstants.GL_MAX_SAMPLES, max, 0)
		this.sceneMaxSamples = max[0]
		this.sceneAvailable = true
		if (ClientFeatures.AA_PIPELINE) {
			println("client.msaa: the 3D view is drawn in its own framebuffer; up to " + this.sceneMaxSamples + " samples")
		}
	}

	// The samples the view is drawn with now: the switch or the game's setting, within the driver's limit.
	private fun sceneSamples(): Int {
		val samples: Int = ClientFeatures.sceneSamples(this.sceneSettingSamples)
		return if (samples > this.sceneMaxSamples) this.sceneMaxSamples else samples
	}

	// Starts the 3D view at the given place in the frame: the scene framebuffer is made or remade to
	// fit, pushed as the target, and the viewport moved so that the view lands at its origin, as the
	// bloom filter does for its layer. The target's size and the projection stay in the frame's pixels.
	override fun beginScene(x: Int, y: Int, width: Int, height: Int) {
		// 634HD: the frame's uniforms for the features, and the 3D view's draws marked as such
		this.sceneDrawing = true
		this.frameUploaded = false
		this.updateHdScene()
		this.hdFeatures.beginFrame(this, x, y, width, height)
		if (!ClientFeatures.AA_PIPELINE && !HdSettings.postProcessing()) {
			return
		}
		if (!this.sceneAvailable || this.sceneActive || width <= 0 || height <= 0) {
			return
		}
		val samples: Int = this.sceneSamples()
		val format: Int = if (HdSettings.tonemap != 0) 34842 else OpenGLConstants.GL_RGBA
		val scale: Double = ClientFeatures.renderScale
		val targetWidth: Int = kotlin.math.floor(width.toDouble() * scale + 0.5).toInt()
		val targetHeight: Int = kotlin.math.floor(height.toDouble() * scale + 0.5).toInt()
		if (this.sceneTarget == null || this.sceneTarget!!.width != targetWidth || this.sceneTarget!!.height != targetHeight || this.sceneTarget!!.samples != samples || this.sceneTarget!!.format != format) {
			this.dropSceneTarget()
			val target: HdSceneTarget = HdSceneTarget(this, targetWidth, targetHeight, samples, true, format)
			this.rebindTargets()
			if (!target.complete) {
				target.release()
				this.rebindTargets()
				lang.printlnErr("client.msaa: the scene framebuffer is incomplete on this HdContext driver; the 3D view is drawn in the frame")
				this.sceneAvailable = false
				return
			}
			this.sceneTarget = target
			println("client.msaa: scene framebuffer " + targetWidth + "x" + targetHeight + " for a " + width + "x" + height + " view, " + samples + " samples")
		}
		this.sceneX = x
		this.sceneY = y
		this.sceneWidth = width
		this.sceneHeight = height
		this.sceneSavedOffsetX = this.viewportOffsetX
		this.sceneSavedOffsetY = this.viewportOffsetY
		this.sceneScale = targetWidth.toDouble() / width.toDouble()
		this.sceneActive = true
		this.pushTarget(this.sceneTarget!!.target())
		this.setViewportOffset(height + y - this.viewportHeight, -x)
	}

	// Ends the 3D view: the target is popped, the view resolved into its texture and drawn over its
	// place in the frame. The clip is not reset through la(), which would move the picking bounds.
	override fun endScene() {
		this.sceneDrawing = false
		this.updateHdScene()
		if (!this.sceneActive) {
			return
		}
		this.setSceneAlphaToCoverage(false)
		this.popTarget(this.sceneTarget!!.target())
		this.sceneActive = false
		this.setViewportOffset(this.sceneSavedOffsetY, this.sceneSavedOffsetX)
		HdGl.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		if (this.sceneTarget!!.multisample != null) {
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.sceneTarget!!.multisample!!.framebufferId())
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.sceneTarget!!.resolve.framebufferId())
			HdGl.glBlitFramebufferEXT(0, 0, this.sceneTarget!!.width, this.sceneTarget!!.height, 0, 0, this.sceneTarget!!.width, this.sceneTarget!!.height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
			this.rebindTargets()
		}
		this.setupSpriteRendering()
		this.setBlendMode(0)
		var view: Hd2DTexture? = this.sceneTarget!!.colour
		if (ClientFeatures.smaa) {
			if (this.smaaFilter == null) {
				this.smaaFilter = HdSmaaFilter(this)
			}
			if (this.smaaFilter!!.available) {
				this.smaaFilter!!.apply(view, this.sceneTarget!!.width, this.sceneTarget!!.height)
				view = this.smaaFilter!!.output
			}
		}
		if (HdSettings.postProcessing()) {
			// 634HD: bloom, tonemapping, grading, FXAA (scene/HdPost), into the view's place in the frame
			this.hdPost.compose(CoreGl.textureName(view!!.getId()), this.sceneTarget!!.width, this.sceneTarget!!.height, this.sceneX, this.viewportHeight - this.sceneY - this.sceneHeight, this.sceneWidth, this.sceneHeight, this.frameNumber++)
			HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
			this.defineScissorBox()
			return
		}
		this.setColourOp(1)
		this.activateTexture(0)
		this.setActiveTexture(view)
		HdGl.glColor4ub((-1).toByte(), (-1).toByte(), (-1).toByte(), (-1).toByte())
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		// Wound as the sprites are, since back faces stay culled in 2D.
		HdGl.glTexCoord2f(0.0f, 1.0f)
		HdGl.glVertex2i(this.sceneX, this.sceneY)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2i(this.sceneX, this.sceneY + this.sceneHeight)
		HdGl.glTexCoord2f(1.0f, 0.0f)
		HdGl.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY + this.sceneHeight)
		HdGl.glTexCoord2f(1.0f, 1.0f)
		HdGl.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY)
		HdGl.glEnd()
		this.setActiveTexture(null)
		HdGl.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.defineScissorBox()
	}

	// Binds the read and draw framebuffers the target stacks say are current, after a bind of our own.
	private fun rebindTargets() {
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, if (this.drawTargetIndex >= 0) (this.drawTargetStack[this.drawTargetIndex] as HdFramebuffer).framebufferId() else this.defaultFramebuffer())
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, if (this.readTargetIndex >= 0) (this.readTargetStack[this.readTargetIndex] as HdFramebuffer).framebufferId() else this.defaultFramebuffer())
	}

	// Lets go of the scene framebuffer; it is made again on the next frame if still wanted.
	override fun dropSceneTarget() {
		if (this.smaaFilter != null) {
			this.smaaFilter!!.release()
		}
		if (this.sceneTarget != null) {
			this.sceneTarget!!.release()
			this.sceneTarget = null
			if (this.activeOpenglInstance != null) {
				this.rebindTargets()
			}
		}
	}

	// Not part of the original client: the framebuffer that stands for the window, 0 unless the frame
	// is drawn into the scaled target.
	fun defaultFramebuffer(): Int {
		if (this.scaledMultisample != null) {
			return this.scaledMultisample!!.framebufferId()
		}
		return if (this.scaledTarget == null) 0 else this.scaledTarget!!.framebufferId()
	}

	// Not part of the original client: makes, resizes or drops the scaled target to match client.scale
	// and the game's size. Needs the framebuffer object and blit extensions; without them the scale is
	// given up, and the client lays the window out at the game's own size again.
	private fun updateScaledTarget() {
		var wanted: Boolean = ClientFeatures.scaled() && this.currentCanvas is GameCanvas && this.width > 0 && this.height > 0
		if (wanted && (!this.extFramebufferObject || !this.framebufferBlit)) {
			lang.printlnErr("client.scale: this HdContext driver has no framebuffer objects, so the game is drawn at its own size")
			ClientFeatures.scaleUnavailable = true
			wanted = false
		}
		if (!wanted) {
			this.dropScaledTarget()
			return
		}
		if (this.scaledTarget != null && this.scaledWidth == this.width && this.scaledHeight == this.height) {
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer())
			return
		}
		this.dropScaledTarget()
		val target: HdFramebuffer = HdFramebuffer(this)
		val colour: Hd2DTexture = Hd2DTexture(this, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, this.width, this.height)
		val depth: HdRenderBuffer = HdRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height)
		target.bind()
		target.attachTexture(colour, 0)
		target.attachRenderbuffer(8, depth)
		target.drawBuffer(0)
		if (!target.isFramebufferComplete()) {
			target.clearAttachment(0)
			target.clearAttachment(8)
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0)
			lang.printlnErr("client.scale: the scaled framebuffer is incomplete on this HdContext driver, so the game is drawn at its own size")
			ClientFeatures.scaleUnavailable = true
			return
		}
		this.scaledTarget = target
		this.scaledColour = colour
		this.scaledDepth = depth
		this.scaledWidth = this.width
		this.scaledHeight = this.height
		if (this.multiSamplesPreference > 1 && this.framebufferMultisample) {
			val multisample: HdFramebuffer = HdFramebuffer(this)
			val multisampleColour: HdRenderBuffer = HdRenderBuffer(this, OpenGLConstants.GL_RGBA, this.width, this.height, this.multiSamplesPreference)
			val multisampleDepth: HdRenderBuffer = HdRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height, this.multiSamplesPreference)
			multisample.bind()
			multisample.attachRenderbuffer(0, multisampleColour)
			multisample.attachRenderbuffer(8, multisampleDepth)
			multisample.drawBuffer(0)
			if (multisample.isFramebufferComplete()) {
				this.scaledMultisample = multisample
				this.scaledMultisampleColour = multisampleColour
				this.scaledMultisampleDepth = multisampleDepth
			} else {
				multisample.clearAttachment(0)
				multisample.clearAttachment(8)
				multisampleColour.dispose()
				multisampleDepth.dispose()
				HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, target.framebufferId())
				lang.printlnErr("client.scale: no multisampled framebuffer on this HdContext driver, so the scaled frame has no anti-aliasing")
			}
		}
	}

	// Not part of the original client: detaches and lets go of the scaled target; its GL objects are
	// deleted when their finalisers run, as the toolkit's other textures and renderbuffers are.
	private fun dropScaledTarget() {
		if (this.scaledTarget == null) {
			return
		}
		if (this.scaledMultisample != null) {
			HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
			this.scaledMultisample!!.clearAttachment(0)
			this.scaledMultisample!!.clearAttachment(8)
			this.scaledMultisampleColour!!.dispose()
			this.scaledMultisampleDepth!!.dispose()
			this.scaledMultisample = null
			this.scaledMultisampleColour = null
			this.scaledMultisampleDepth = null
		}
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		this.scaledTarget!!.clearAttachment(0)
		this.scaledTarget!!.clearAttachment(8)
		this.scaledColour!!.delete()
		this.scaledDepth!!.dispose()
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0)
		this.scaledTarget = null
		this.scaledColour = null
		this.scaledDepth = null
	}

	override fun f(near: Int, far: Int) {
		if (near == this.zNear && this.zFar == far) {
			return
		}
		this.zNear = near
		this.zFar = far
		this.updateProjectionMatrix()
		this.updateFog()
		if (this.depthClippingMode == 3) {
			this.loadOrthoProjection()
			return
		}
		if (this.depthClippingMode != 2) {
			return
		}
		this.loadProjectionMatrix()
	}

	override fun method7261(x: Int, y: Int, width: Int, height: Int) {
		this.framebufferCompositor!!.prepareForRendering(width, x, height, y)
	}

	fun popReadTarget(target: GlRenderTarget?) {
		if (this.readTargetIndex < 0 || target !== this.readTargetStack[this.readTargetIndex]) {
			throw RuntimeException()
		}
		this.readTargetStack[this.readTargetIndex--] = null
		target!!.unbindRead()
		if (this.readTargetIndex < 0) {
			this.readTarget = null
		} else {
			this.readTarget = this.readTargetStack[this.readTargetIndex]
			this.readTarget!!.bindRead()
		}
	}

	override fun allocateThreads(threadCount: Int) {
		if (threadCount != 1) {
			throw IllegalArgumentException("")
		}
	}

	override fun method7268(width: Int, height: Int): Sprite? {
		return null
	}

	private fun updateProjectionMatrix() {
		val matrix: FloatArray = this.projectionMatrix
		val left: Float = (this.zNear * -this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		val right: Float = ((this.viewportWidth - this.projectionCenterX) * this.zNear).toFloat() / this.projectionScaleX.toFloat()
		val top: Float = (this.zNear * this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		val bottom: Float = ((this.projectionCenterY - this.viewportHeight) * this.zNear).toFloat() / this.projectionScaleY.toFloat()
		if (right == left || bottom == top) {
			matrix[13] = 0.0f
			matrix[15] = 1.0f
			matrix[3] = 0.0f
			matrix[11] = 0.0f
			matrix[12] = 0.0f
			matrix[4] = 0.0f
			matrix[6] = 0.0f
			matrix[8] = 0.0f
			matrix[7] = 0.0f
			matrix[1] = 0.0f
			matrix[10] = 1.0f
			matrix[14] = 0.0f
			matrix[5] = 1.0f
			matrix[0] = 1.0f
			matrix[9] = 0.0f
			matrix[2] = 0.0f
		} else {
			val twoNear: Float = this.zNear.toFloat() * 2.0f
			matrix[1] = 0.0f
			matrix[5] = twoNear / (top - bottom)
			matrix[9] = (top + bottom) / (-bottom + top)
			matrix[3] = 0.0f
			matrix[4] = 0.0f
			matrix[8] = (right + left) / (right - left)
			matrix[7] = 0.0f
			this.projectionDepthOffset = -(this.zFar.toFloat() * twoNear) / (this.zFar - this.zNear).toFloat()
			matrix[14] = this.projectionDepthOffset
			matrix[2] = 0.0f
			matrix[11] = -1.0f
			matrix[15] = 0.0f
			this.projectionDepthScale = (-(this.zFar + this.zNear)).toFloat() / (this.zFar - this.zNear).toFloat()
			matrix[10] = this.projectionDepthScale
			matrix[12] = 0.0f
			matrix[0] = twoNear / (right - left)
			matrix[13] = 0.0f
			matrix[6] = 0.0f
		}
		this.updateDepthRange()
	}

	override fun finish() {
		HdGl.glFinish()
	}

	override fun generateSprite(indexedImage: SpriteType?, paletteSprite: Boolean): NativeSprite {
		val pixels: IntArray = IntArray(indexedImage!!.width * indexedImage.height)
		var srcIndex: Int = 0
		var dstIndex: Int = 0
		var y: Int
		var x: Int
		if (indexedImage.alpha == null) {
			y = 0
			while (y < indexedImage.height) {
				x = 0
				while (x < indexedImage.width) {
					val colour: Int = indexedImage.pallete!![indexedImage.pixels!![srcIndex++].toInt() and 0xFF]
					pixels[dstIndex++] = if (colour == 0) 0 else colour or 0xFF000000.toInt()
					x++
				}
				y++
			}
		} else {
			y = 0
			while (y < indexedImage.height) {
				x = 0
				while (x < indexedImage.width) {
					pixels[dstIndex++] = indexedImage.alpha!![srcIndex].toInt() shl 24 or indexedImage.pallete!![indexedImage.pixels!![srcIndex].toInt() and 0xFF]
					srcIndex++
					x++
				}
				y++
			}
		}
		val sprite: NativeSprite? = this.createNativeSprite(indexedImage.width, pixels, indexedImage.width, indexedImage.height)
		sprite!!.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY)
		return sprite
	}

	fun toggleFog(enableFog: Boolean) {
		if (enableFog != this.fogEnabled) {
			this.fogEnabled = enableFog
			this.toggleFog()
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
		}
	}

	fun setTextureEnvColour(colour: Int) {
		OpenGlToolkit.BUFFER_4F[2] = (colour and 0xFF).toFloat() / 255.0f
		OpenGlToolkit.BUFFER_4F[3] = (colour ushr 24).toFloat() / 255.0f
		OpenGlToolkit.BUFFER_4F[1] = (colour and 0xFF00).toFloat() / 65280.0f
		OpenGlToolkit.BUFFER_4F[0] = (colour and 0xFF0000).toFloat() / 1.671168E7f
		HdGl.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	override fun na(x: Int, y: Int, width: Int, height: Int): IntArray {
		val pixels: IntArray = IntArray(width * height)
		this.beginReadFrame(x, this.viewportHeight - y - height, width, height) // Not part of the original client: see beginReadFrame.
		for (i in 0 until height) {
			HdGl.glReadPixelsi(x, this.viewportHeight - y - i, width, 1, OpenGLConstants.GL_BGRA, this.pixelType, pixels, width * i)
		}
		this.endReadFrame()
		return pixels
	}

	private fun updateViewport() {
		if (this.sceneScaling()) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			HdGl.glViewport(this.sceneScaled(this.viewportOffsetX), this.sceneScaled(this.viewportOffsetY), this.sceneScaled(this.viewportWidth), this.sceneScaled(this.viewportHeight))
			return
		}
		HdGl.glViewport(this.viewportOffsetX, this.viewportOffsetY, this.viewportWidth, this.viewportHeight)
	}

	override fun supportsAntiAliasing(): Boolean {
		return this.arbMultisample && (!this.bloom() || this.advancedFramebuffers)
	}

	@Synchronized
	fun requestRenderBufferCleanup(handle: Int, bufferSize: Int) {
		val intNode: IntNode = IntNode(bufferSize)
		intNode.key = handle.toLong()
		this.renderBuffersAwaitingRemoval.addLast(intNode)
	}

	private fun defineScissorBox() {
		if (this.sceneScaling() && this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			val x0: Int = this.sceneScaled(this.viewportOffsetX + this.clipLeft)
			val y0: Int = this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipBottom)
			HdGl.glScissor(x0, y0, this.sceneScaled(this.viewportOffsetX + this.clipRight) - x0, this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipTop) - y0)
			return
		}
		if (this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			HdGl.glScissor(this.viewportOffsetX + this.clipLeft, this.viewportOffsetY + this.viewportHeight - this.clipBottom, this.clipRight - this.clipLeft, -this.clipTop + this.clipBottom)
		} else {
			HdGl.glScissor(0, 0, 0, 0)
		}
	}

	override fun method7311(): Boolean {
		return false
	}

	companion object {

		@JvmField
		val SOUND_EFFECT_MUSIC_ENDED: ClientPacket = ClientPacket(52, 4)

		private const val GL_DEPTH_COMPONENT: Int = 6402

		const val MAX_LIGHTS: Int = 4

		@JvmField
		val deleteIdBuffer: IntArray = IntArray(1000)

		@JvmStatic
		fun create(multiSamples: Int, canvas: Canvas?, textureSource: MaterialProvider?): Toolkit {
			return HdToolkit(canvas, textureSource, multiSamples)
		}
	}
}
