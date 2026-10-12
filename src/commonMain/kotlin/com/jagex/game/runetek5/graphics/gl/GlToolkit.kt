package com.jagex.game.runetek5.graphics.gl

import clientArraysAvailable
import com.jagex.game.runetek5.client.GameCanvas
import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlVboElementsArrayBuffer
import com.jagex.game.runetek5.OpenGLArbTextureCubeMapGenerator
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.toolkit.opengl.OpenGLTextureCache
import com.jagex.game.runetek5.config.rsinterface.GlClippingMask
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer
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
import com.jagex.game.runetek5.graphics.FramebufferCompositor
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
import jaggl.OpenGL
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import lang.reflect.Method
import util.Enumeration
import util.Hashtable
import util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.cache.node.GlMemoryPool
import com.jagex.game.runetek5.graphics.font.GlFont
import com.jagex.game.runetek5.graphics.model.GlModel
import com.jagex.game.runetek5.graphics.shader.Texture3DShaderGL
import com.jagex.game.runetek5.graphics.sprite.SpriteGL
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture
import com.jagex.game.runetek5.graphics.texture.RectangularTextureGL
import com.jagex.game.runetek5.scene.ground.GlGround
import com.jagex.game.runetek5.toolkit.opengl.EffectManagerGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlArrayBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlVboArrayBuffer
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.toolkit.OpenGlToolkit
import com.jagex.game.runetek5.toolkit.opengl.GlParticleRenderer
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.NativeLibraryOwner
import kotlin.jvm.JvmField
import lang.Class
import kotlin.jvm.JvmStatic
import lang.Synchronized
import lang.getMethod
import util.lowercase
import awt.height
import awt.width

@OriginalClass("client!qo")
class GlToolkit : Toolkit {
	@OriginalMember(owner = "client!qo", name = "G", descriptor = "I")
	private var height: Int = 0

	@OriginalMember(owner = "client!qo", name = "Uc", descriptor = "I")
	@JvmField
	var viewportHeight: Int = 0

	@OriginalMember(owner = "client!qo", name = "Yc", descriptor = "I")
	private var width: Int = 0

	@OriginalMember(owner = "client!qo", name = "Pd", descriptor = "I")
	@JvmField
	var viewportWidth: Int = 0

	@OriginalMember(owner = "client!qo", name = "Fe", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	@JvmField
	var nativeHeap: NativeHeap? = null

	@OriginalMember(owner = "client!qo", name = "Ke", descriptor = "I")
	@JvmField
	var lastTickMillis: Int = 0

	@OriginalMember(owner = "client!qo", name = "Oe", descriptor = "Lclient!rg;")
	private var offscreenFramebuffer: GlFramebuffer? = null

	@OriginalMember(owner = "client!qo", name = "Pe", descriptor = "Lclient!tv;")
	private var drawTarget: GlRenderTarget? = null

	@OriginalMember(owner = "client!qo", name = "Te", descriptor = "Lclient!nda;")
	private var offscreenSprite: SpriteGL? = null

	@OriginalMember(owner = "client!qo", name = "Ue", descriptor = "Lclient!tv;")
	private var readTarget: GlRenderTarget? = null

	@OriginalMember(owner = "client!qo", name = "We", descriptor = "I")
	@JvmField
	var vboBuffersSize: Int = 0

	@OriginalMember(owner = "client!qo", name = "Xe", descriptor = "I")
	@JvmField
	var textureBuffersSize: Int = 0

	@OriginalMember(owner = "client!qo", name = "Ze", descriptor = "I")
	private var renderBuffersSize: Int = 0

	@OriginalMember(owner = "client!qo", name = "gf", descriptor = "J")
	private var lastGcTime: Long = 0L

	@OriginalMember(owner = "client!qo", name = "hf", descriptor = "Z")
	private var colormaskActive: Boolean = false

	@OriginalMember(owner = "client!qo", name = "jf", descriptor = "Z")
	private var enableDepthMask: Boolean = false

	@OriginalMember(owner = "client!qo", name = "lf", descriptor = "Z")
	private var alphaTestEnabled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "mf", descriptor = "I")
	private var activeBlendType: Int = 0

	@OriginalMember(owner = "client!qo", name = "nf", descriptor = "Z")
	private var depthTestEnabled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "of", descriptor = "I")
	private var activeBlendAlpha: Int = 0

	@OriginalMember(owner = "client!qo", name = "pf", descriptor = "I")
	private var functionMask: Int = 0

	@OriginalMember(owner = "client!qo", name = "sf", descriptor = "F")
	@JvmField
	var sunBrightness: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "tf", descriptor = "Lclient!dp;")
	private var sunCubeTextureGenerator: GlEnvironmentSampler? = null

	@OriginalMember(owner = "client!qo", name = "wf", descriptor = "I")
	@JvmField
	var sunSlopeX: Int = 0

	@OriginalMember(owner = "client!qo", name = "zf", descriptor = "Z")
	@JvmField
	var bigEndianess: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Af", descriptor = "Lclient!eba;")
	private var worldMapTexture: OpenGl2DImageTexture? = null

	@OriginalMember(owner = "client!qo", name = "Bf", descriptor = "I")
	@JvmField
	var sunSlopeZ: Int = 0

	@OriginalMember(owner = "client!qo", name = "Cf", descriptor = "Lclient!rg;")
	@JvmField
	var integratedGraphicsImageProducer: GlFramebuffer? = null

	@OriginalMember(owner = "client!qo", name = "Ef", descriptor = "Lclient!paa;")
	@JvmField
	var copyBuffer1: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Hf", descriptor = "Z")
	@JvmField
	var arbFragmentShader: Boolean = false

	@OriginalMember(owner = "client!qo", name = "If", descriptor = "Z")
	private var lightingEnabled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Jf", descriptor = "Lclient!paa;")
	@JvmField
	var copyBuffer5: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Mf", descriptor = "I")
	@JvmField
	var waterHeight: Int = 0

	@OriginalMember(owner = "client!qo", name = "Nf", descriptor = "I")
	private var openglVersion: Int = 0

	@OriginalMember(owner = "client!qo", name = "Of", descriptor = "Ljava/lang/String;")
	private var gpuNameString: String? = null

	@OriginalMember(owner = "client!qo", name = "Pf", descriptor = "Z")
	@JvmField
	var arbVertexShader: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Qf", descriptor = "F")
	@JvmField
	var fogStartPosition: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Rf", descriptor = "Z")
	@JvmField
	var framebufferObjectSupported: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Tf", descriptor = "I")
	@JvmField
	var maxTextureUnits: Int = 0

	@OriginalMember(owner = "client!qo", name = "Uf", descriptor = "I")
	private var lastMaterialId: Int = 0

	@OriginalMember(owner = "client!qo", name = "Vf", descriptor = "Lclient!hca;")
	private var screenQuadArrayBuffer: ArrayBufferGL? = null

	@OriginalMember(owner = "client!qo", name = "Xf", descriptor = "I")
	private var maxTextureCoordinates: Int = 0

	@OriginalMember(owner = "client!qo", name = "Zf", descriptor = "Z")
	private var fogEnabled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "ag", descriptor = "Lclient!paa;")
	@JvmField
	var copyBuffer2: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "bg", descriptor = "Lclient!paa;")
	@JvmField
	var copyTarget3: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "dg", descriptor = "Z")
	private var hideLights: Boolean = false

	@OriginalMember(owner = "client!qo", name = "fg", descriptor = "Z")
	@JvmField
	var framebufferMultisample: Boolean = false

	@OriginalMember(owner = "client!qo", name = "ng", descriptor = "Z")
	@JvmField
	var framebufferBlit: Boolean = false

	@OriginalMember(owner = "client!qo", name = "pg", descriptor = "Lclient!ni;")
	private var glBinding: ElementsBufferGL? = null

	@OriginalMember(owner = "client!qo", name = "rg", descriptor = "I")
	private var maxTextureImageUnits: Int = 0

	@OriginalMember(owner = "client!qo", name = "tg", descriptor = "Lclient!paa;")
	@JvmField
	var copyBuffer3: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "ug", descriptor = "Z")
	private var advancedFramebuffers: Boolean = false

	@OriginalMember(owner = "client!qo", name = "wg", descriptor = "F")
	private var fogEndPosition: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "zg", descriptor = "Lclient!tp;")
	@JvmField
	var texture2d: OpenGl2DTexture? = null

	@OriginalMember(owner = "client!qo", name = "Ag", descriptor = "[Lclient!ud;")
	private var openglTextures: Array<GlTexture?>? = null

	@OriginalMember(owner = "client!qo", name = "Cg", descriptor = "F")
	@JvmField
	var viewY2: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Fg", descriptor = "Lclient!nh;")
	@JvmField
	var quadTexCoordBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!qo", name = "Gg", descriptor = "Lclient!paa;")
	@JvmField
	var copyTarget4: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Hg", descriptor = "F")
	@JvmField
	var viewX2: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Ig", descriptor = "F")
	@JvmField
	var viewY1: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Kg", descriptor = "Lclient!paa;")
	@JvmField
	var copyTarget2: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Lg", descriptor = "I")
	private var activeLightCount: Int = 0

	@OriginalMember(owner = "client!qo", name = "Mg", descriptor = "Lclient!paa;")
	@JvmField
	var copyTarget5: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Ng", descriptor = "Z")
	@JvmField
	var arbVertexProgram: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Og", descriptor = "F")
	private var projectionDepthOffset: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Pg", descriptor = "Z")
	private var arbMultisample: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Qg", descriptor = "Lclient!paa;")
	@JvmField
	var copyBuffer4: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "Rg", descriptor = "Ljava/lang/String;")
	private var gpuCompanyString: String? = null

	@OriginalMember(owner = "client!qo", name = "Sg", descriptor = "Z")
	@JvmField
	var stillWaterToggled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Vg", descriptor = "Lclient!nh;")
	@JvmField
	var quadVertexBuffer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!qo", name = "Yg", descriptor = "Lclient!hca;")
	private var currentArrayBuffer: ArrayBufferGL? = null

	@OriginalMember(owner = "client!qo", name = "ah", descriptor = "F")
	private var projectionDepthScale: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "gh", descriptor = "Z")
	private var textureUpdate: Boolean = false

	@OriginalMember(owner = "client!qo", name = "ih", descriptor = "I")
	private var lastSceneLightCount: Int = 0

	@OriginalMember(owner = "client!qo", name = "jh", descriptor = "Z")
	@JvmField
	var arbTextureCubeMap: Boolean = false

	@OriginalMember(owner = "client!qo", name = "kh", descriptor = "Lclient!paa;")
	@JvmField
	var copyTarget1: GlModel? = null

	@OriginalMember(owner = "client!qo", name = "lh", descriptor = "I")
	private var depthClippingMode: Int = 0

	@OriginalMember(owner = "client!qo", name = "mh", descriptor = "Z")
	private var pbuffersAvailable: Boolean = false

	@OriginalMember(owner = "client!qo", name = "sh", descriptor = "F")
	@JvmField
	var viewX1: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "wh", descriptor = "I")
	private var glTexturesCursorPos: Int = 0

	@OriginalMember(owner = "client!qo", name = "yh", descriptor = "I")
	private var polygonCount: Int = 0

	@OriginalMember(owner = "client!qo", name = "Bh", descriptor = "I")
	private var modelCount: Int = 0

	@OriginalMember(owner = "client!qo", name = "Ib", descriptor = "Ljava/util/Hashtable;")
	private val canvasSurfaces: Hashtable<Any?, Any?> = Hashtable()

	@OriginalMember(owner = "client!qo", name = "ne", descriptor = "I")
	@JvmField
	var size: Int = 128

	@OriginalMember(owner = "client!qo", name = "me", descriptor = "Lclient!sn;")
	private val particleRenderer: GlParticleRenderer = GlParticleRenderer()

	@OriginalMember(owner = "client!qo", name = "Ae", descriptor = "Lclient!iaa;")
	private val scratchMatrix: Matrix43 = Matrix43()

	@OriginalMember(owner = "client!qo", name = "Ee", descriptor = "Lclient!iaa;")
	@JvmField
	val billboardMatrix: Matrix43 = Matrix43()

	@OriginalMember(owner = "client!qo", name = "Ge", descriptor = "I")
	@JvmField
	var shadowScaleShift: Int = 3

	@OriginalMember(owner = "client!qo", name = "Je", descriptor = "Z")
	private var colortableReset: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Ie", descriptor = "I")
	@JvmField
	var shadowScale: Int = 8

	@OriginalMember(owner = "client!qo", name = "He", descriptor = "Lclient!uh;")
	private val heapManager: Deque = Deque()

	@OriginalMember(owner = "client!qo", name = "Me", descriptor = "[Lclient!tv;")
	private val drawTargetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	@OriginalMember(owner = "client!qo", name = "Ne", descriptor = "I")
	private var readTargetIndex: Int = -1

	@OriginalMember(owner = "client!qo", name = "Re", descriptor = "I")
	private var drawTargetIndex: Int = -1

	// Not part of the original client: with client.scale, the frame is drawn into this game-sized
	// framebuffer and copied to the window scaled at flip; see updateScaledTarget.
	private var scaledTarget: GlFramebuffer? = null
	private var scaledColour: OpenGl2DTexture? = null
	private var scaledDepth: OpenGlRenderBuffer? = null
	private var scaledWidth: Int = 0
	private var scaledHeight: Int = 0
	// With anti-aliasing, the frame is drawn into this multisampled framebuffer and resolved into
	// the scaled target before the copy to the window, as the window's own surface would have been.
	private var scaledMultisample: GlFramebuffer? = null
	private var scaledMultisampleColour: OpenGlRenderBuffer? = null
	private var scaledMultisampleDepth: OpenGlRenderBuffer? = null

	@OriginalMember(owner = "client!qo", name = "Qe", descriptor = "[Lclient!tv;")
	private val readTargetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	@OriginalMember(owner = "client!qo", name = "Se", descriptor = "[Lclient!tv;")
	private val targetStack: Array<GlRenderTarget?> = arrayOfNulls<GlRenderTarget>(4)

	@OriginalMember(owner = "client!qo", name = "Ve", descriptor = "I")
	private var targetIndex: Int = -1

	@OriginalMember(owner = "client!qo", name = "Ye", descriptor = "Lclient!uh;")
	private val listsAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "af", descriptor = "Lclient!uh;")
	private val vboBuffersAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "bf", descriptor = "Lclient!uh;")
	private val texturesAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "cf", descriptor = "Lclient!uh;")
	private val frameBuffersAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "df", descriptor = "Lclient!uh;")
	private val renderBuffersAwaitingRemoval: Deque

	@OriginalMember(owner = "client!qo", name = "ef", descriptor = "Lclient!uh;")
	private val arbProgramsAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "ff", descriptor = "Lclient!uh;")
	private val shaderProgramsAwaitingDeallocation: Deque

	@OriginalMember(owner = "client!qo", name = "kf", descriptor = "Lclient!iaa;")
	@JvmField
	val viewMatrix43: Matrix43

	@OriginalMember(owner = "client!qo", name = "qf", descriptor = "Lclient!iaa;")
	@JvmField
	val modelViewMatrix: Matrix43

	@OriginalMember(owner = "client!qo", name = "rf", descriptor = "Lclient!iaa;")
	@JvmField
	val inverseModelViewMatrix: Matrix43

	@OriginalMember(owner = "client!qo", name = "uf", descriptor = "I")
	private var viewportOffsetX: Int = 0

	@OriginalMember(owner = "client!qo", name = "Kf", descriptor = "F")
	private var fogMin: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "xf", descriptor = "I")
	private var clipTop: Int = 0

	@OriginalMember(owner = "client!qo", name = "Ff", descriptor = "F")
	@JvmField
	var sunColorRed: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "eg", descriptor = "I")
	@JvmField
	var waterFogColour: Int = 0

	@OriginalMember(owner = "client!qo", name = "vf", descriptor = "I")
	@JvmField
	var projectionScaleX: Int = 0

	@OriginalMember(owner = "client!qo", name = "Yf", descriptor = "F")
	private var outlineCirclePointSize: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "gg", descriptor = "I")
	private var sunColorRgb: Int = 0

	@OriginalMember(owner = "client!qo", name = "og", descriptor = "F")
	@JvmField
	var sunColorGreen: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "cg", descriptor = "I")
	private var textureCombineRgb: Int = 0

	@OriginalMember(owner = "client!qo", name = "Bg", descriptor = "F")
	private var fillCirclePointSize: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "ig", descriptor = "I")
	@JvmField
	var projectionCenterY: Int = 0

	@OriginalMember(owner = "client!qo", name = "Eg", descriptor = "F")
	@JvmField
	var sunColorIntensity: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "xg", descriptor = "[F")
	@JvmField
	val sunDirection: FloatArray

	@OriginalMember(owner = "client!qo", name = "hg", descriptor = "I")
	@JvmField
	var waterIntensity: Int = 0

	@OriginalMember(owner = "client!qo", name = "jg", descriptor = "[F")
	private val lightingDirection: FloatArray

	@OriginalMember(owner = "client!qo", name = "vg", descriptor = "F")
	@JvmField
	var sunColorBlue: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Sf", descriptor = "I")
	@JvmField
	var projectionScaleY: Int = 0

	@OriginalMember(owner = "client!qo", name = "mg", descriptor = "I")
	private var zFar: Int = 0

	@OriginalMember(owner = "client!qo", name = "bh", descriptor = "I")
	private var clipRight: Int = 0

	@OriginalMember(owner = "client!qo", name = "dh", descriptor = "F")
	private var densityFactor: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Lf", descriptor = "I")
	private var clipBottom: Int = 0

	@OriginalMember(owner = "client!qo", name = "fh", descriptor = "Z")
	private var renderStillWater: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Wg", descriptor = "[F")
	private val sunPreviousPosition: FloatArray

	@OriginalMember(owner = "client!qo", name = "Xg", descriptor = "F")
	private var orthoZoom: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "yg", descriptor = "I")
	@JvmField
	var zNear: Int = 0

	@OriginalMember(owner = "client!qo", name = "hh", descriptor = "I")
	private var textureCombineAlpha: Int = 0

	@OriginalMember(owner = "client!qo", name = "lg", descriptor = "I")
	@JvmField
	var fogOffset: Int = 0

	@OriginalMember(owner = "client!qo", name = "ch", descriptor = "I")
	@JvmField
	var fogColor: Int = 0

	@OriginalMember(owner = "client!qo", name = "oh", descriptor = "I")
	private var clipLeft: Int = 0

	@OriginalMember(owner = "client!qo", name = "Df", descriptor = "[F")
	private val projectionMatrix: FloatArray

	@OriginalMember(owner = "client!qo", name = "qh", descriptor = "Z")
	private var depthBufferingEnabled: Boolean = false

	@OriginalMember(owner = "client!qo", name = "rh", descriptor = "F")
	@JvmField
	var antiSunColorIntensity: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "eh", descriptor = "[Lclient!ab;")
	private val lightNodes: Array<PointLight?>

	@OriginalMember(owner = "client!qo", name = "Zg", descriptor = "I")
	@JvmField
	var projectionCenterX: Int = 0

	@OriginalMember(owner = "client!qo", name = "nh", descriptor = "I")
	private var viewportOffsetY: Int = 0

	@OriginalMember(owner = "client!qo", name = "vh", descriptor = "F")
	@JvmField
	var farClipDistance: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "uh", descriptor = "F")
	@JvmField
	var projectedFarClipDistance: Float = 0.0f

	@OriginalMember(owner = "client!qo", name = "Ug", descriptor = "I")
	@JvmField
	var waterDepth: Int = 0

	@OriginalMember(owner = "client!qo", name = "xh", descriptor = "[F")
	private val antiSunPreviousPosition: FloatArray

	@OriginalMember(owner = "client!qo", name = "Gf", descriptor = "I")
	@JvmField
	var fogDensity: Int = 0

	@OriginalMember(owner = "client!qo", name = "Wf", descriptor = "Lclient!gh;")
	@JvmField
	var textureBuffer: GlBitBuffer

	@OriginalMember(owner = "client!qo", name = "zh", descriptor = "[B")
	@JvmField
	val shadowTextureData: ByteArray

	@OriginalMember(owner = "client!qo", name = "Ah", descriptor = "[I")
	@JvmField
	var scratchVerticesX: IntArray

	@OriginalMember(owner = "client!qo", name = "Dh", descriptor = "[I")
	@JvmField
	var scratchVerticesY: IntArray

	@OriginalMember(owner = "client!qo", name = "Ch", descriptor = "[I")
	@JvmField
	var visibleTileIndices: IntArray

	@OriginalMember(owner = "client!qo", name = "Fc", descriptor = "Ljava/awt/Canvas;")
	private val canvas: Canvas?

	@OriginalMember(owner = "client!qo", name = "Gb", descriptor = "Ljava/awt/Canvas;")
	private var currentCanvas: Canvas? = null

	@OriginalMember(owner = "client!qo", name = "oe", descriptor = "I")
	@JvmField
	val multiSamplesPreference: Int

	@OriginalMember(owner = "client!qo", name = "rd", descriptor = "Lclient!jaggl/OpenGL;")
	private var activeOpenglInstance: OpenGL? = null

	@OriginalMember(owner = "client!qo", name = "kb", descriptor = "J")
	private val canvasSurface: Long

	@OriginalMember(owner = "client!qo", name = "Pc", descriptor = "J")
	private var currentSurface: Long = 0L

	@OriginalMember(owner = "client!qo", name = "kg", descriptor = "I")
	@JvmField
	val pixelType: Int

	@OriginalMember(owner = "client!qo", name = "th", descriptor = "Z")
	private var vertexbufferSupported: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Dg", descriptor = "Z")
	@JvmField
	var texture3dSupported: Boolean = false

	@OriginalMember(owner = "client!qo", name = "qg", descriptor = "Z")
	@JvmField
	var arbTextureFloat: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Jg", descriptor = "Z")
	@JvmField
	var arbRectangleTexturesSupported: Boolean = false

	@OriginalMember(owner = "client!qo", name = "Tg", descriptor = "Z")
	@JvmField
	var isAmd: Boolean = false

	@OriginalMember(owner = "client!qo", name = "ph", descriptor = "Z")
	private var shouldUseArbVbo: Boolean = false

	@OriginalMember(owner = "client!qo", name = "sg", descriptor = "Z")
	@JvmField
	var extFramebufferObject: Boolean = false

	@OriginalMember(owner = "client!qo", name = "yf", descriptor = "Z")
	private val s3Graphics: Boolean

	@OriginalMember(owner = "client!qo", name = "ye", descriptor = "Lclient!eo;")
	private val textureCache: OpenGLTextureCache

	@OriginalMember(owner = "client!qo", name = "Le", descriptor = "Lclient!qi;")
	@JvmField
	val texture3dShader: Texture3DShaderGL

	@OriginalMember(owner = "client!qo", name = "ve", descriptor = "Lclient!hh;")
	private val framebufferCompositor: FramebufferCompositor?

	@OriginalMember(owner = "client!qo", name = "we", descriptor = "Lclient!rda;")
	private var rectangularTexture: RectangularTextureGL? = null

	@OriginalMember(owner = "client!qo", name = "ie", descriptor = "Lclient!mea;")
	private val effectManager: EffectManagerGL

	@OriginalMember(owner = "client!qo", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;I)V")
	constructor(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) multiSamples: Int) : super(textureSource) {
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
		this.lightNodes = arrayOfNulls<PointLight>(GlToolkit.MAX_LIGHTS)
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
		if (!NativeLibraryOwner.loadLibrary("jaclib")) {
			throw RuntimeException("")
		} else if (NativeLibraryOwner.loadLibrary("jaggl")) {
			try {
				this.activeOpenglInstance = OpenGL()
				this.canvasSurface = this.activeOpenglInstance!!.init(canvas, 8, 8, 8, 24, 0, this.multiSamplesPreference)
				this.currentSurface = this.canvasSurface
				if (this.canvasSurface == 0L) {
					throw RuntimeException("")
				}
				this.attach()
				@Pc(335) val capabilityErrors: Int = this.accumulateCapabilities()
				if (capabilityErrors != 0) {
					throw RuntimeException("")
				}
				this.pixelType = if (this.bigEndianess) 33639 else 5121
				if (this.gpuNameString!!.indexOf("radeon") != -1) {
					@Pc(361) var radeonModel: Int = 0
					@Pc(363) var isHd: Boolean = false
					@Pc(365) var hasXPrefix: Boolean = false
					@Pc(374) val strings: Array<String?> = Utils.split(' ', this.gpuNameString!!.replace('/', ' '))
					for (@Pc(376) i in 0 until strings.size) {
						@Pc(382) var string: String? = strings[i]
						try {
							if (string!!.length > 0) {
								if (string[0] == 'x' && string.length >= 3 && Utils.isValidInteger(string.substring(1, 3))) {
									string = string.substring(1)
									hasXPrefix = true
								}
								if (string == "hd") {
									isHd = true
								} else {
									if (string.startsWith("hd")) {
										isHd = true
										string = string.substring(2)
									}
									if (string.length >= 4 && Utils.isValidInteger(string.substring(0, 4))) {
										radeonModel = Utils.parseInt(string.substring(0, 4))
										break
									}
								}
							}
						} catch (@Pc(452) ex: Exception) {
						}
					}
					if (!hasXPrefix && !isHd) {
						if (radeonModel >= 7000 && radeonModel <= 7999) {
							this.vertexbufferSupported = false
						}
						if (radeonModel >= 7000 && radeonModel <= 9250) {
							this.texture3dSupported = false
						}
					}
					if (!isHd || radeonModel < 4000) {
						this.arbTextureFloat = false
					}
					this.arbRectangleTexturesSupported = this.arbRectangleTexturesSupported and this.activeOpenglInstance!!.a("GL_ARB_half_float_pixel")
					this.isAmd = true
					this.shouldUseArbVbo = this.vertexbufferSupported
				}
				if (this.gpuCompanyString!!.indexOf("intel") != -1) {
					this.extFramebufferObject = false
				}
				// Not part of the original client: without client-side arrays (the browser), dynamic vertex
				// data goes in buffer objects too (PlatformLoop.kt).
				if (!clientArraysAvailable) {
					this.shouldUseArbVbo = this.vertexbufferSupported
				}
				this.checkScenePipeline() // Not part of the original client: see checkScenePipeline.
				this.s3Graphics = this.gpuCompanyString != "s3 graphics"
				if (this.vertexbufferSupported) {
					try {
						@Pc(535) val bufferIds: IntArray = IntArray(1)
						OpenGL.glGenBuffersARB(1, bufferIds, 0)
					} catch (@Pc(541) ex2: Throwable) {
						throw RuntimeException("")
					}
				}
				ColorUtil.initColorTables(false, true)
				this.colortableReset = true
				this.textureCache = OpenGLTextureCache(this, this.materialProvider)
				this.buildTables()
				this.texture3dShader = Texture3DShaderGL(this)
				this.framebufferCompositor = FramebufferCompositor(this)
				if (this.framebufferCompositor!!.isDisplayAvailable()) {
					this.rectangularTexture = RectangularTextureGL(this)
					if (!this.rectangularTexture!!.isSupported()) {
						this.rectangularTexture!!.destroy()
						this.rectangularTexture = null
					}
				}
				this.effectManager = EffectManagerGL(this)
				this.prepareForRendering()
				this.updateCanvasSize()
				this.renderLastFrame()
			} catch (@Pc(615) ex3: Throwable) {
				ex3.printStackTrace()
				this.stop()
				throw RuntimeException("")
			}
		} else {
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!qo", name = "L", descriptor = "(III)V")
	override fun L(@OriginalArg(0) fogColour: Int, @OriginalArg(1) fogDensity: Int, @OriginalArg(2) fogOffset: Int) {
		if (this.fogColor == fogColour && this.fogDensity == fogDensity && this.fogOffset == fogOffset) {
			return
		}
		this.fogDensity = fogDensity
		this.fogColor = fogColour
		this.fogOffset = fogOffset
		this.updateFog()
		this.toggleFog()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZI)V")
	fun setLightingEnabled(@OriginalArg(0) enabled: Boolean) {
		if (this.lightingEnabled != enabled) {
			this.lightingEnabled = enabled
			this.toggleLighting()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	override fun createModel(@OriginalArg(0) mesh: Mesh?, @OriginalArg(1) functionMask: Int, @OriginalArg(2) featureMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int): Model {
		return GlModel(this, mesh, functionMask, ambient, contrast, featureMask)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IBI)V")
	fun loadTextureCombineMode(@OriginalArg(0) combineRgb: Int, @OriginalArg(2) combineAlpha: Int) {
		if (this.glTexturesCursorPos != 0) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha)
			return
		}
		@Pc(14) var changed: Boolean = false
		if (this.textureCombineRgb != combineRgb) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb)
			changed = true
			this.textureCombineRgb = combineRgb
		}
		if (this.textureCombineAlpha != combineAlpha) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha)
			this.textureCombineAlpha = combineAlpha
			changed = true
		}
		if (changed) {
			this.functionMask = this.functionMask and 0xFFFFFFE2.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(B)V")
	private fun loadProjectionMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadMatrixf(this.projectionMatrix, 0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!wh;)V")
	override fun setSunCubeTexture(@OriginalArg(0) environmentSampler: EnvironmentSampler?) {
		this.sunCubeTextureGenerator = environmentSampler as GlEnvironmentSampler?
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IZI[BI)Lclient!hca;")
	fun createArrayBuffer(@OriginalArg(1) isDynamic: Boolean, @OriginalArg(2) size: Int, @OriginalArg(3) buffer: ByteArray?, @OriginalArg(4) position: Int): ArrayBufferGL {
		return (if (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo)) GlVboArrayBuffer(this, size, buffer, position, isDynamic) else GlArrayBuffer(this, size, buffer, position)) as ArrayBufferGL
	}

	@OriginalMember(owner = "client!qo", name = "DA", descriptor = "(IIII)V")
	override fun DA(@OriginalArg(0) centerX: Int, @OriginalArg(1) centerY: Int, @OriginalArg(2) scaleX: Int, @OriginalArg(3) scaleY: Int) {
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

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(I)V")
	private fun toggleFog() {
		if (this.fogEnabled && this.fogDensity >= 0) {
			OpenGL.glEnable(OpenGLConstants.GL_FOG)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_FOG)
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(III[BZ)Lclient!ni;")
	fun createElementsArrayBuffer(@OriginalArg(2) size: Int, @OriginalArg(3) data: ByteArray?, @OriginalArg(4) isDynamic: Boolean): ElementsBufferGL {
		return (if (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo)) GlVboElementsArrayBuffer(this, 5123, data, size, isDynamic) else GlElementsArrayBuffer(this, 5123, data, size)) as ElementsBufferGL
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(Z)V")
	fun popMatrix() {
		OpenGL.glPopMatrix()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	override fun createPointLight(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) range: Int, @OriginalArg(5) intensity: Float): PointLight {
		return GlPointLight(x, y, z, radius, range, intensity)
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(I)V")
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

	@OriginalMember(owner = "client!qo", name = "XA", descriptor = "()I")
	override fun XA(): Int {
		return this.zFar
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	override fun drawSurfaceLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int) {
		@Pc(6) val mask: GlClippingMask? = clippingMask as GlClippingMask?
		@Pc(9) val surface: OpenGl2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		@Pc(39) val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		@Pc(46) val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		@Pc(52) val dx: Float = endX.toFloat() - startX.toFloat()
		@Pc(58) val dy: Float = endY.toFloat() - startY.toFloat()
		@Pc(71) val inverseLength: Float = (1.0 / kotlin.math.sqrt((dy * dy + dx * dx).toDouble())).toFloat()
		@Pc(75) val dirX: Float = dx * inverseLength
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		@Pc(94) val dirY: Float = dy * inverseLength
		OpenGL.glBegin(OpenGLConstants.GL_LINES)
		OpenGL.glTexCoord2f((startX - maskX).toFloat() * texScaleS, texScaleT * (startY - maskY).toFloat())
		OpenGL.glVertex2f(startX.toFloat() + 0.35f, startY.toFloat() + 0.35f)
		OpenGL.glTexCoord2f(texScaleS * (endX - maskX).toFloat(), (endY - maskY).toFloat() * texScaleT)
		OpenGL.glVertex2f(dirX + endX.toFloat() + 0.35f, endY.toFloat() + 0.35f + dirY)
		OpenGL.glEnd()
		this.setTexture(5890, 768, 0)
	}

	@OriginalMember(owner = "client!qo", name = "Y", descriptor = "()[I")
	override fun Y(): IntArray {
		return intArrayOf(this.projectionCenterX, this.projectionCenterY, this.projectionScaleX, this.projectionScaleY)
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IBI)V")
	@Synchronized
	fun requestTextureCleanup(@OriginalArg(0) handle: Int, @OriginalArg(2) sizeRequirement: Int) {
		@Pc(8) val intNode: IntNode = IntNode(sizeRequirement)
		intNode.key = handle.toLong()
		this.texturesAwaitingDeallocation.addLast(intNode)
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(II)V")
	fun activateTexture(@OriginalArg(1) textureUnit: Int) {
		if (textureUnit != this.glTexturesCursorPos) {
			OpenGL.glActiveTexture(textureUnit + OpenGLConstants.GL_TEXTURE0)
			this.glTexturesCursorPos = textureUnit
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!jaclib/memory/Buffer;ZII)Lclient!hca;")
	fun createDataArrayBuffer(@OriginalArg(1) buffer: Buffer?, @OriginalArg(3) stride: Int, @OriginalArg(4) size: Int): ArrayBufferGL {
		return (if (this.vertexbufferSupported) GlVboArrayBuffer(this, stride, buffer, size, false) else GlArrayBuffer(this, stride, buffer)) as ArrayBufferGL
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(I)V")
	private fun setModelViewMatrixMode() {
		if (this.depthClippingMode != 2) {
			this.depthClippingMode = 2
			this.loadProjectionMatrix()
			this.updateModelViewAndLights()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "()Z")
	override fun hasTextures(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(I)Lclient!q;")
	fun getCubeMap(): GlCubeTexture? {
		return if (this.sunCubeTextureGenerator == null) null else this.sunCubeTextureGenerator!!.getCubeTexture()
	}

	@OriginalMember(owner = "client!qo", name = "HA", descriptor = "(IIII[I)V")
	override fun HA(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) projectionDepth: Int, @OriginalArg(4) output: IntArray?) {
		@Pc(28) val depth: Float = z.toFloat() * this.viewMatrix43.zC + y.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x.toFloat() + this.viewMatrix43.zD
		if (this.zNear.toFloat() > depth || this.zFar.toFloat() < depth) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		@Pc(86) val screenX: Int = ((this.viewMatrix43.xD + this.viewMatrix43.xB * y.toFloat() + x.toFloat() * this.viewMatrix43.xA + this.viewMatrix43.xC * z.toFloat()) * this.projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt()
		@Pc(119) val screenY: Int = (this.projectionScaleY.toFloat() * (this.viewMatrix43.yD + this.viewMatrix43.yA * x.toFloat() + y.toFloat() * this.viewMatrix43.yB + z.toFloat() * this.viewMatrix43.yC) / projectionDepth.toFloat()).toInt()
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

	@OriginalMember(owner = "client!qo", name = "EA", descriptor = "(IIII)V")
	override fun EA(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) fogColour: Int, @OriginalArg(2) depth: Int, @OriginalArg(3) intensity: Int) {
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

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(I)V")
	private fun setOrthoProjectionMode() {
		if (this.depthClippingMode != 3) {
			this.depthClippingMode = 3
			this.loadOrthoProjection()
			this.updateModelViewAndLights()
			this.functionMask = this.functionMask and 0xFFFFFFF8.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "()Z")
	override fun supportsFullscreen(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FIF)V")
	fun adjustFog(@OriginalArg(0) fogMin: Float, @OriginalArg(2) densityFactor: Float) {
		this.fogMin = fogMin
		this.densityFactor = densityFactor
		this.updateFog()
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(B)V")
	private fun updateCanvasSize() {
		if (this.currentCanvas == null) {
			this.height = 0
			this.width = 0
		} else {
			@Pc(10) val dimension: Dimension = this.currentCanvas!!.getSize()
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZIZI)V")
	fun renderTexture(@OriginalArg(0) hasLight: Boolean, @OriginalArg(1) materialId: Int, @OriginalArg(2) allowStillWater: Boolean) {
		if (materialId != this.lastMaterialId || this.renderStillWater != this.stillWaterToggled) {
			@Pc(22) var texture: OpenGl2DTexture? = null
			@Pc(24) var combineMode: Int = 0
			@Pc(26) var effectParam1: Byte = 0
			@Pc(28) var effectParam2: Int = 0
			@Pc(35) var effectType: Int = if (this.stillWaterToggled) 3 else 0
			if (materialId < 0) {
				this.renderStationaryTexture()
				this.setSceneAlphaToCoverage(false) // Not part of the original client: see setSceneAlphaToCoverage.
			} else {
				texture = this.textureCache.getTexture(materialId)
				@Pc(49) val textureMetrics: MaterialType? = this.materialProvider!!.getMetrics(materialId)
				this.setSceneAlphaToCoverage(textureMetrics!!.effectCombiner == 1) // Not part of the original client: see setSceneAlphaToCoverage.
				if (textureMetrics.speedU.toInt() == 0 && textureMetrics.speedV.toInt() == 0) {
					this.renderStationaryTexture()
				} else {
					@Pc(62) val textureSize: Int = if (textureMetrics.isSmall) 64 else 128
					@Pc(66) val animationPeriod: Int = textureSize * 50
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

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(B)V")
	private fun loadOrthoProjection() {
		@Pc(15) val left: Float = (-this.projectionCenterX).toFloat() * this.orthoZoom / this.projectionScaleX.toFloat()
		@Pc(27) val top: Float = this.orthoZoom * (-this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		@Pc(42) val right: Float = this.orthoZoom * (this.viewportWidth - this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		@Pc(56) val bottom: Float = this.orthoZoom * (this.viewportHeight - this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadIdentity()
		if (left != right && top != bottom) {
			OpenGL.glOrtho(left.toDouble(), right.toDouble(), (-bottom).toDouble(), (-top).toDouble(), this.zNear.toDouble(), this.zFar.toDouble())
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "()Z")
	override fun supportsBloom(): Boolean {
		return this.rectangularTexture != null && (this.multiSamplesPreference <= 1 || this.advancedFramebuffers)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IZ)V")
	fun setDepthMask(@OriginalArg(1) enableMask: Boolean) {
		if (this.enableDepthMask != enableMask) {
			this.enableDepthMask = enableMask
			this.setDepthMask()
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "K", descriptor = "([I)V")
	override fun K(@OriginalArg(0) bounds: IntArray?) {
		bounds!![1] = this.clipTop
		bounds[3] = this.clipBottom
		bounds[0] = this.clipLeft
		bounds[2] = this.clipRight
	}

	@OriginalMember(owner = "client!qo", name = "Q", descriptor = "(IIIIII[BII)V")
	override fun Q(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) envColour: Int, @OriginalArg(6) alphaData: ByteArray?, @OriginalArg(7) maskWidth: Int, @OriginalArg(8) blendMode: Int) {
		@Pc(45) val maxU: Float
		@Pc(49) val maxV: Float
		if (this.worldMapTexture == null || this.worldMapTexture!!.width < width || this.worldMapTexture!!.length < height) {
			this.worldMapTexture = OpenGl2DImageTexture.createSurface(alphaData, height, this, width)
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
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		this.setTextureEnvColour(envColour)
		this.loadTextureCombineMode(34165, 34165)
		this.setTexture(34166, 768, 0)
		this.setTexture(5890, 770, 2)
		this.setTextureTransparency(0, 34166)
		this.setTextureTransparency(2, 5890)
		@Pc(151) val left: Float = x.toFloat()
		@Pc(154) val top: Float = y.toFloat()
		@Pc(159) val right: Float = width.toFloat() + left
		@Pc(164) val bottom: Float = height.toFloat() + top
		OpenGL.glBegin(7)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2f(left, top)
		OpenGL.glTexCoord2f(0.0f, maxV)
		OpenGL.glVertex2f(left, bottom)
		OpenGL.glTexCoord2f(maxU, maxV)
		OpenGL.glVertex2f(right, bottom)
		OpenGL.glTexCoord2f(maxU, 0.0f)
		OpenGL.glVertex2f(right, top)
		OpenGL.glEnd()
		this.setTexture(5890, 768, 0)
		this.setTexture(34166, 770, 2)
		this.setTextureTransparency(0, 5890)
		this.setTextureTransparency(2, 34166)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!mda;)V")
	override fun render(@OriginalArg(0) particleList: ParticleList?) {
		this.particleRenderer.drawParticles(this, -1, particleList)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFFBF)V")
	fun updatePointSizeRange(@OriginalArg(0) alpha: Float, @OriginalArg(1) red: Float, @OriginalArg(2) blue: Float, @OriginalArg(4) green: Float) {
		OpenGlToolkit.BUFFER_4F[2] = blue
		OpenGlToolkit.BUFFER_4F[0] = red
		OpenGlToolkit.BUFFER_4F[1] = green
		OpenGlToolkit.BUFFER_4F[3] = alpha
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!bca;)V")
	override fun setCamera(@OriginalArg(0) matrix: Matrix?) {
		this.viewMatrix43.apply(matrix)
		this.modelViewMatrix.apply(this.viewMatrix43)
		this.modelViewMatrix.negateYZ()
		this.inverseModelViewMatrix.setInverse(this.modelViewMatrix)
		if (this.depthClippingMode != 1) {
			this.updateModelViewAndLights()
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!hca;)V")
	fun setArrayBuffer(@OriginalArg(1) arrayBuffer: ArrayBufferGL?) {
		if (arrayBuffer !== this.currentArrayBuffer) {
			if (this.vertexbufferSupported) {
				OpenGL.glBindBufferARB(OpenGLConstants.GL_ARRAY_BUFFER, arrayBuffer!!.getId())
			}
			this.currentArrayBuffer = arrayBuffer
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	override fun drawTriangle() {
		this.enter2dMode()
		this.setBlendMode(1)
		OpenGL.glBegin(OpenGLConstants.GL_TRIANGLES)
		OpenGL.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		OpenGL.glVertex3f(5.toFloat() + 0.35f, 10.toFloat() + 0.35f, 100.toFloat())
		OpenGL.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		OpenGL.glVertex3f(75.toFloat() + 0.35f, 50.toFloat() + 0.35f, 100.toFloat())
		OpenGL.glColor4ub((-1).toByte(), 0.toByte(), 0.toByte(), (-1).toByte())
		OpenGL.glVertex3f(15.toFloat() + 0.35f, 90.toFloat() + 0.35f, 100.toFloat())
		OpenGL.glEnd()
	}

	@OriginalMember(owner = "client!qo", name = "p", descriptor = "()V")
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!tv;I)V")
	fun pushDrawTarget(@OriginalArg(0) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IZ)V")
	fun toggleZbuffer(@OriginalArg(1) zbuffer: Boolean) {
		if (zbuffer == this.depthTestEnabled) {
			return
		}
		if (zbuffer) {
			OpenGL.glEnable(OpenGLConstants.GL_DEPTH_TEST)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_DEPTH_TEST)
		}
		this.depthTestEnabled = zbuffer
		this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	override fun createFontRenderer(@OriginalArg(0) metrics: FontMetrics?, @OriginalArg(1) image: Array<SpriteType?>?): AbstractFontRenderer {
		return GlFont(this, metrics, image, true)
	}

	@OriginalMember(owner = "client!qo", name = "aa", descriptor = "(IIIIII)V")
	override fun aa(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) blendMode: Int) {
		@Pc(8) val left: Float = x.toFloat() + 0.35f
		@Pc(13) val top: Float = y.toFloat() + 0.35f
		@Pc(18) val right: Float = width.toFloat() + left
		this.enter2dMode()
		@Pc(26) val bottom: Float = top + height.toFloat()
		this.setBlendMode(blendMode)
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.arbMultisample) {
			OpenGL.glDisable(32925)
		}
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glVertex2f(left, top)
		OpenGL.glVertex2f(left, bottom)
		OpenGL.glVertex2f(right, bottom)
		OpenGL.glVertex2f(right, top)
		OpenGL.glEnd()
		if (this.arbMultisample) {
			OpenGL.glEnable(32925)
		}
	}

	@OriginalMember(owner = "client!qo", name = "ZA", descriptor = "(IFFFFF)V")
	override fun ZA(@OriginalArg(0) colour: Int, @OriginalArg(1) intensity: Float, @OriginalArg(2) antiIntensity: Float, @OriginalArg(3) directionX: Float, @OriginalArg(4) directionY: Float, @OriginalArg(5) directionZ: Float) {
		@Pc(15) val colourChanged: Boolean = this.sunColorRgb != colour
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
		@Pc(142) val inverseLength: Float = (1.0 / kotlin.math.sqrt((directionZ * directionZ + directionY * directionY + directionX * directionX).toDouble())).toFloat()
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

	@OriginalMember(owner = "client!qo", name = "g", descriptor = "(II)I")
	override fun combineFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		return maskA or maskB
	}

	@OriginalMember(owner = "client!qo", name = "GA", descriptor = "(I)V")
	override fun GA(@OriginalArg(0) colour: Int) {
		this.setBlendMode(0)
		OpenGL.glClearColor((colour and 0xFF0000).toFloat() / 1.671168E7f, (colour and 0xFF00).toFloat() / 65280.0f, (colour and 0xFF).toFloat() / 255.0f, (colour ushr 24).toFloat() / 255.0f)
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT)
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(I)V")
	override fun initialize() {
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(II)V")
	fun setBlendMode(@OriginalArg(0) mode: Int) {
		if (mode == this.activeBlendAlpha) {
			return
		}
		@Pc(22) val alphaTest: Boolean
		@Pc(20) val blendType: Byte
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
			OpenGL.glColorMask(true, true, true, true)
			this.colormaskActive = true
		}
		if (alphaTest != this.alphaTestEnabled) {
			if (alphaTest) {
				OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST)
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_ALPHA_TEST)
			}
			this.alphaTestEnabled = alphaTest
		}
		if (blendType.toInt() != this.activeBlendType) {
			if (blendType.toInt() == 1) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND)
				OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA)
			} else if (blendType.toInt() == 2) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND)
				OpenGL.glBlendFunc(OpenGLConstants.GL_ONE, OpenGLConstants.GL_ONE)
			} else if (blendType.toInt() == 3) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND)
				OpenGL.glBlendFunc(OpenGLConstants.GL_DST_COLOR, OpenGLConstants.GL_ONE)
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_BLEND)
			}
			this.activeBlendType = blendType.toInt()
		}
		this.activeBlendAlpha = mode
		this.functionMask = this.functionMask and 0xFFFFFFE3.toInt()
	}

	@OriginalMember(owner = "client!qo", name = "g", descriptor = "(B)V")
	private fun updateModelViewAndLights() {
		OpenGL.glLoadIdentity()
		OpenGL.glMultMatrixf(this.modelViewMatrix.getColumnMajor(), 0)
		if (this.renderStillWater) {
			this.effectManager.waterEffect.bindProgram()
		}
		this.setLightingSourceParams()
		this.updatePointLights()
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "(I)V")
	fun setOrthoMatrixMode() {
		if (this.depthClippingMode != 0) {
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
			this.depthClippingMode = 0
		}
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIII)V")
	fun drawArrays(@OriginalArg(0) count: Int) {
		OpenGL.glDrawArrays(OpenGLConstants.GL_QUADS, 0, count)
	}

	@OriginalMember(owner = "client!qo", name = "U", descriptor = "(IIIII)V")
	override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) blendMode: Int) {
		this.enter2dMode()
		this.setBlendMode(blendMode)
		@Pc(15) val startX: Float = x.toFloat() + 0.35f
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		@Pc(35) val startY: Float = y.toFloat() + 0.35f
		OpenGL.glBegin(OpenGLConstants.GL_LINES)
		OpenGL.glVertex2f(startX, startY)
		OpenGL.glVertex2f(width.toFloat() + startX, startY)
		OpenGL.glEnd()
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(I)V")
	override fun setTextureSize(@OriginalArg(0) size: Int) {
		if (size < 128 || size > 1024) {
			throw IllegalArgumentException()
		}
		this.size = size
		this.textureCache.reset()
	}

	@OriginalMember(owner = "client!qo", name = "xa", descriptor = "(F)V")
	override fun xa(@OriginalArg(0) brightness: Float) {
		if (this.sunBrightness != brightness) {
			this.sunBrightness = brightness
			this.updateAmbient()
		}
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(II)V")
	fun renderTexture(@OriginalArg(0) materialId: Int) {
		this.renderTexture(true, materialId)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZLclient!iaa;)V")
	fun multiMatrix(@OriginalArg(1) matrix44: Matrix43?) {
		OpenGL.glPushMatrix()
		OpenGL.glMultMatrixf(matrix44!!.getColumnMajor(), 0)
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	override fun createNativeSprite(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) hasAlpha: Boolean): NativeSprite {
		return SpriteGL(this, x, y, width, height)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIILclient!ni;I)V")
	fun drawElements(@OriginalArg(0) indexCount: Int, @OriginalArg(3) elementsBuffer: ElementsBufferGL?, @OriginalArg(4) startIndex: Int) {
		@Pc(7) val indexType: Int = elementsBuffer!!.getIndexType()
		@Pc(20) val byteOffset: Int = startIndex * this.getDataTypeSize(indexType)
		this.setElementsBuffer(elementsBuffer)
		OpenGL.glDrawElements(OpenGLConstants.GL_TRIANGLES, indexCount, indexType, elementsBuffer.getAddress() + byteOffset.toLong())
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(I)V")
	@Synchronized
	override fun tick(@OriginalArg(0) time: Int) {
		@Pc(7) var count: Int = 0
		@Pc(11) val maskedTime: Int = time and Int.MAX_VALUE
		@Pc(19) var intNode: IntNode?
		while (!this.vboBuffersAwaitingDeallocation.isEmpty()) {
			intNode = this.vboBuffersAwaitingDeallocation.removeFirst() as IntNode?
			GlToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.vboBuffersSize -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteBuffersARB(count, GlToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteBuffersARB(count, GlToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.texturesAwaitingDeallocation.isEmpty()) {
			intNode = this.texturesAwaitingDeallocation.removeFirst() as IntNode?
			GlToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.textureBuffersSize -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteTextures(count, GlToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteTextures(count, GlToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.frameBuffersAwaitingDeallocation.isEmpty()) {
			intNode = this.frameBuffersAwaitingDeallocation.removeFirst() as IntNode?
			GlToolkit.deleteIdBuffer[count++] = intNode!!.value
			if (count == 1000) {
				OpenGL.glDeleteFramebuffersEXT(count, GlToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteFramebuffersEXT(count, GlToolkit.deleteIdBuffer, 0)
			count = 0
		}
		while (!this.renderBuffersAwaitingRemoval.isEmpty()) {
			intNode = this.renderBuffersAwaitingRemoval.removeFirst() as IntNode?
			GlToolkit.deleteIdBuffer[count++] = intNode!!.key.toInt()
			this.renderBuffersSize -= intNode.value
			if (count == 1000) {
				OpenGL.glDeleteRenderbuffersEXT(count, GlToolkit.deleteIdBuffer, 0)
				count = 0
			}
		}
		if (count > 0) {
			OpenGL.glDeleteRenderbuffersEXT(count, GlToolkit.deleteIdBuffer, 0)
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = this.listsAwaitingDeallocation.removeFirst() as IntNode?
			OpenGL.glDeleteLists(intNode!!.key.toInt(), intNode.value)
		}
		@Pc(209) var node: Node?
		while (!this.arbProgramsAwaitingDeallocation.isEmpty()) {
			node = this.arbProgramsAwaitingDeallocation.removeFirst()
			OpenGL.glDeleteProgramARB(node!!.key.toInt())
		}
		while (!this.shaderProgramsAwaitingDeallocation.isEmpty()) {
			node = this.shaderProgramsAwaitingDeallocation.removeFirst()
			OpenGL.glDeleteObjectARB(node!!.key)
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = this.listsAwaitingDeallocation.removeFirst() as IntNode?
			OpenGL.glDeleteLists(intNode!!.key.toInt(), intNode.value)
		}
		this.textureCache.clean()
		if (this.E() > 100663296 && this.lastGcTime + TimeUtils.MILLISECONDS_PER_MINUTE < Utils.currentTimeMillis()) {
			lang.gc()
			this.lastGcTime = Utils.currentTimeMillis()
		}
		this.lastTickMillis = maskedTime
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(IIIIII)V")
	override fun line(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		this.enter2dMode()
		this.setBlendMode(mode)
		@Pc(16) var dx: Float = x2.toFloat() - x1.toFloat()
		@Pc(23) var dy: Float = (-y1).toFloat() + y2.toFloat()
		if (dx == 0.0f && dy == 0.0f) {
			dx = 1.0f
		} else {
			@Pc(48) val inverseLength: Float = (1.0 / kotlin.math.sqrt((dx * dx + dy * dy).toDouble())).toFloat()
			dx *= inverseLength
			dy *= inverseLength
		}
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		OpenGL.glBegin(OpenGLConstants.GL_LINES)
		OpenGL.glVertex2f(x1.toFloat() + 0.35f, y1.toFloat() + 0.35f)
		OpenGL.glVertex2f(dx + x2.toFloat() + 0.35f, dy + 0.35f + y2.toFloat())
		OpenGL.glEnd()
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIID)V")
	override fun b(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depthOffset: Double) {
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "(I)V")
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

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(II)V")
	fun setColourOp(@OriginalArg(1) op: Int) {
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

	@OriginalMember(owner = "client!qo", name = "u", descriptor = "()Z")
	override fun forceFixedScreenmode(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIBI)V")
	fun setTexture(@OriginalArg(0) source: Int, @OriginalArg(1) operand: Int, @OriginalArg(3) activeUnit: Int) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_SRC0_RGB, source)
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_OPERAND0_RGB, operand)
	}

	@OriginalMember(owner = "client!qo", name = "p", descriptor = "(I)V")
	private fun setScreenProjectionMode() {
		if (this.depthClippingMode == 1) {
			return
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadIdentity()
		if (this.viewportWidth > 0 && this.viewportHeight > 0) {
			OpenGL.glOrtho(0.0, this.viewportWidth.toDouble(), this.viewportHeight.toDouble(), 0.0, -1.0, 1.0)
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadIdentity()
		this.depthClippingMode = 1
		this.functionMask = this.functionMask and 0xFFFFFFE7.toInt()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!tv;)V")
	fun popDrawTarget(@OriginalArg(1) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BI)V")
	@Synchronized
	fun requestArbProgramCleanup(@OriginalArg(1) programHandle: Int) {
		@Pc(7) val node: Node = Node()
		node.key = programHandle.toLong()
		this.arbProgramsAwaitingDeallocation.addLast(node)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!iaa;)V")
	fun loadMatrix(@OriginalArg(1) matrix4x4: Matrix43?) {
		OpenGL.glLoadMatrixf(matrix4x4!!.getColumnMajor(), 0)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFF)V")
	override fun setBloomParams(@OriginalArg(0) whitePoint: Float, @OriginalArg(1) bloom: Float, @OriginalArg(2) brightpass: Float) {
		RectangularTextureGL.brightpass = brightpass
		RectangularTextureGL.whitePoint = whitePoint
		RectangularTextureGL.bloom = bloom
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	override fun releaseSurface(@OriginalArg(0) canvas: Canvas?) {
		if (canvas === this.canvas) {
			throw RuntimeException()
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(22) val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
			this.activeOpenglInstance!!.releaseSurface(canvas, long2!!)
			this.canvasSurfaces.remove(canvas)
		}
	}

	@OriginalMember(owner = "client!qo", name = "q", descriptor = "(I)V")
	private fun renderStationaryTexture() {
		if (this.textureUpdate) {
			OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
			OpenGL.glLoadIdentity()
			OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			this.textureUpdate = false
		}
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "()Z")
	override fun extendBuildArea(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "za", descriptor = "(IIIII)V")
	override fun za(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) blendMode: Int) {
		var radius: Int = radius
		if (radius < 0) {
			radius = -radius
		}
		if (x + radius < this.clipLeft || this.clipRight < x - radius || this.clipTop > y + radius || this.clipBottom < y - radius) {
			return
		}
		this.enter2dMode()
		this.setBlendMode(blendMode)
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		@Pc(72) val centerX: Float = x.toFloat() + 0.35f
		@Pc(77) val centerY: Float = y.toFloat() + 0.35f
		@Pc(81) val diameter: Int = radius shl 1
		if (this.fillCirclePointSize > diameter.toFloat()) {
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glVertex2f(centerX + 1.0f, centerY + 1.0f)
			OpenGL.glVertex2f(centerX + 1.0f, -1.0f + centerY)
			OpenGL.glVertex2f(centerX - 1.0f, centerY - 1.0f)
			OpenGL.glVertex2f(centerX - 1.0f, centerY + 1.0f)
			OpenGL.glEnd()
		} else if (this.outlineCirclePointSize >= diameter.toFloat()) {
			OpenGL.glEnable(OpenGLConstants.GL_POINT_SMOOTH)
			OpenGL.glPointSize(diameter.toFloat())
			OpenGL.glBegin(OpenGLConstants.GL_POINTS)
			OpenGL.glVertex2f(centerX, centerY)
			OpenGL.glEnd()
			OpenGL.glDisable(OpenGLConstants.GL_POINT_SMOOTH)
		} else {
			OpenGL.glBegin(OpenGLConstants.GL_TRIANGLE_FAN)
			OpenGL.glVertex2f(centerX, centerY)
			@Pc(150) var angleStep: Int = 262144 / (radius * 6)
			if (angleStep <= 64) {
				angleStep = 64
			} else if (angleStep > 512) {
				angleStep = 512
			}
			angleStep = Utils.floorPowerOfTwo(angleStep)
			OpenGL.glVertex2f(radius.toFloat() + centerX, centerY)
			@Pc(177) var i: Int = 16384 - angleStep
			while (i > 0) {
				OpenGL.glVertex2f(radius.toFloat() * OggStreamDecoder.COSINE[i] + centerX, radius.toFloat() * OggStreamDecoder.SINE[i] + centerY)
				i -= angleStep
			}
			OpenGL.glVertex2f(centerX + radius.toFloat(), centerY)
			OpenGL.glEnd()
		}
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(I)V")
	override fun linkThreads(@OriginalArg(0) threadId: Int) {
		this.attach()
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(B)V")
	private fun toggleLighting() {
		if (this.lightingEnabled && !this.hideLights) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHTING)
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHTING)
		}
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "()V")
	override fun stop() {
		@Pc(6) var node: Node? = this.heapManager.first()
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
			@Pc(40) val enumeration: Enumeration<Any?> = this.canvasSurfaces.keys()
			while (enumeration.hasMoreElements()) {
				@Pc(46) val canvas: Canvas? = enumeration.nextElement() as Canvas?
				@Pc(52) val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
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

	@OriginalMember(owner = "client!qo", name = "q", descriptor = "()V")
	override fun renderLastFrame() {
		if (!this.s3Graphics || this.viewportWidth <= 0 || this.viewportHeight <= 0) {
			return
		}
		if (this.scaledTarget != null) {
			// Not part of the original client: the scaled target keeps the last frame, so there is no
			// front buffer to copy back from, and the window's buffers cannot be chosen while it is bound.
			return
		}
		@Pc(22) val savedClipLeft: Int = this.clipLeft
		@Pc(25) val savedClipRight: Int = this.clipRight
		@Pc(28) val savedClipTop: Int = this.clipTop
		@Pc(31) val savedClipBottom: Int = this.clipBottom
		this.la()
		OpenGL.glReadBuffer(OpenGLConstants.GL_FRONT)
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.setOrthoMatrixMode()
		this.toggleFog(false)
		this.setLightingEnabled(false)
		this.toggleZbuffer(false)
		this.setDepthMask(false)
		this.setActiveTexture(null)
		this.renderTexture(-2)
		this.setColourOp(1)
		this.setBlendMode(0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glLoadIdentity()
		OpenGL.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadIdentity()
		OpenGL.glRasterPos2i(0, 0)
		OpenGL.glCopyPixels(0, 0, this.viewportWidth, this.viewportHeight, OpenGLConstants.GL_COLOR)
		OpenGL.glFlush()
		OpenGL.glReadBuffer(OpenGLConstants.GL_BACK)
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK)
		this.KA(savedClipLeft, savedClipTop, savedClipRight, savedClipBottom)
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	override fun createCubeTexture(@OriginalArg(0) materialAtPositiveX: Int, @OriginalArg(1) materialAtNegativeX: Int, @OriginalArg(2) materialAtPositiveY: Int, @OriginalArg(3) materialAtNegativeY: Int, @OriginalArg(4) materialAtPositiveZ: Int, @OriginalArg(5) materialAtNegativeZ: Int): EnvironmentSampler? {
		return if (this.arbTextureCubeMap) OpenGLArbTextureCubeMapGenerator(this, materialAtPositiveX, materialAtNegativeX, materialAtPositiveY, materialAtNegativeY, materialAtPositiveZ, materialAtNegativeZ) else null
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(JZ)V")
	@Synchronized
	fun requestShaderProgramCleanup(@OriginalArg(0) programHandle: Long) {
		@Pc(13) val node: Node = Node()
		node.key = programHandle
		this.shaderProgramsAwaitingDeallocation.addLast(node)
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(ILclient!tv;)V")
	fun popTarget(@OriginalArg(1) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!ud;)V")
	fun setActiveTexture(@OriginalArg(1) replacingCombiner: GlTexture?) {
		@Pc(20) val current: GlTexture? = this.openglTextures!![this.glTexturesCursorPos]
		if (current !== replacingCombiner) {
			if (replacingCombiner == null) {
				OpenGL.glDisable(current!!.target)
			} else {
				if (current == null) {
					OpenGL.glEnable(replacingCombiner.target)
				} else if (replacingCombiner.target != current.target) {
					OpenGL.glDisable(current.target)
					OpenGL.glEnable(replacingCombiner.target)
				}
				OpenGL.glBindTexture(replacingCombiner.target, replacingCombiner.getId())
			}
			this.openglTextures!![this.glTexturesCursorPos] = replacingCombiner
		}
		this.functionMask = this.functionMask and 0xFFFFFFFE.toInt()
	}

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "()V")
	override fun stopBloom() {
		if (this.rectangularTexture != null && this.rectangularTexture!!.isActive()) {
			this.framebufferCompositor!!.removeEffect(this.rectangularTexture)
			this.textureCache.reset()
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIIIII)V")
	override fun drawLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colorArgb: Int, @OriginalArg(6) dashLength: Int, @OriginalArg(7) gapLength: Int, @OriginalArg(8) offset: Int) {
		if (startX == endX && startY == endY) {
			return
		}
		this.enter2dMode()
		this.setBlendMode(1)
		@Pc(29) val dx: Float = endX.toFloat() - startX.toFloat()
		@Pc(35) val dy: Float = endY.toFloat() - startY.toFloat()
		@Pc(48) val inverseLength: Float = (1.0 / kotlin.math.sqrt((dy * dy + dx * dx).toDouble())).toFloat()
		@Pc(52) val dirX: Float = dx * inverseLength
		@Pc(56) val dirY: Float = dy * inverseLength
		OpenGL.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		@Pc(77) val phase: Int = offset % (dashLength + gapLength)
		@Pc(82) val dashX: Float = dashLength.toFloat() * dirX
		@Pc(87) val dashY: Float = dirY * dashLength.toFloat()
		@Pc(89) var startOffsetX: Float = 0.0f
		@Pc(91) var startOffsetY: Float = 0.0f
		@Pc(93) var segmentX: Float = dashX
		@Pc(95) var segmentY: Float = dashY
		if (phase <= dashLength) {
			segmentY = (dashLength - phase).toFloat() * dirY
			segmentX = dirX * (dashLength - phase).toFloat()
		} else {
			startOffsetY = (gapLength + dashLength - phase).toFloat() * dirY
			startOffsetX = (gapLength + dashLength - phase).toFloat() * dirX
		}
		@Pc(141) var currentX: Float = startOffsetX + startX.toFloat() + 0.35f
		@Pc(148) var currentY: Float = startY.toFloat() + startOffsetY + 0.35f
		@Pc(153) val gapX: Float = dirX * gapLength.toFloat()
		@Pc(158) val gapY: Float = dirY * gapLength.toFloat()
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
			OpenGL.glBegin(OpenGLConstants.GL_LINES)
			OpenGL.glVertex2f(currentX, currentY)
			OpenGL.glVertex2f(currentX + segmentX, segmentY + currentY)
			OpenGL.glEnd()
			currentY += segmentY + gapY
			currentX += gapX + segmentX
			segmentX = dashX
			segmentY = dashY
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	override fun addCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		if (this.canvas === canvas) {
			throw RuntimeException()
		} else if (!this.canvasSurfaces.containsKey(canvas)) {
			if (!canvas!!.isShowing()) {
				throw RuntimeException()
			}
			try {
				@Pc(26) val class2: Class<*> = lang.forName("java.awt.Canvas")
				@Pc(36) val method: Method = class2.getMethod("setIgnoreRepaint", lang.BOOLEAN_TYPE)
				method.invoke(canvas, true)
			} catch (@Pc(48) ex: Exception) {
			}
			@Pc(53) val surface: Long = this.activeOpenglInstance!!.prepareSurface(canvas)
			if (surface == -1L) {
				throw RuntimeException()
			}
			this.canvasSurfaces.put(canvas, (surface))
		}
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(B)V")
	private fun updateViewBounds() {
		this.viewX2 = (this.clipRight - this.projectionCenterX).toFloat()
		this.viewX1 = (this.clipLeft - this.projectionCenterX).toFloat()
		this.viewY1 = (this.clipTop - this.projectionCenterY).toFloat()
		this.viewY2 = (this.clipBottom - this.projectionCenterY).toFloat()
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(I)Lclient!za;")
	override fun createHeap(@OriginalArg(0) size: Int): MemoryPool {
		@Pc(8) val glMemoryPool: GlMemoryPool = GlMemoryPool(size)
		this.heapManager.addLast(glMemoryPool)
		return glMemoryPool
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	override fun createGround(@OriginalArg(0) width: Int, @OriginalArg(1) length: Int, @OriginalArg(2) tileHeights: Array<IntArray?>?, @OriginalArg(3) nextLevelHeights: Array<IntArray?>?, @OriginalArg(5) groundFlags: Int, @OriginalArg(6) featureFlags: Int): Ground {
		return GlGround(this, groundFlags, featureFlags, width, length, tileHeights, nextLevelHeights, 512)
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "()Z")
	override fun hardShadow(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(ILclient!tv;)V")
	fun pushTarget(@OriginalArg(1) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(B)I")
	private fun accumulateCapabilities(): Int {
		@Pc(5) var errors: Int = 0
		this.gpuCompanyString = OpenGL.glGetString(OpenGLConstants.GL_VENDOR)!!.lowercase(util.defaultLocale())
		this.gpuNameString = OpenGL.glGetString(OpenGLConstants.GL_RENDERER)!!.lowercase(util.defaultLocale())
		if (this.gpuCompanyString!!.indexOf("microsoft") != -1) {
			errors = 1
		}
		if (this.gpuCompanyString!!.indexOf("brian paul") != -1 || this.gpuCompanyString!!.indexOf("mesa") != -1) {
			errors = errors or 0x1
		}
		@Pc(44) val string: String = OpenGL.glGetString(OpenGLConstants.GL_VERSION)!!
		@Pc(52) val strings: Array<String?> = Utils.split(' ', string.replace('.', ' '))
		if (strings.size >= 2) {
			try {
				@Pc(62) val major: Int = Utils.parseInt(strings[0]!!)
				@Pc(68) val minor: Int = Utils.parseInt(strings[1]!!)
				this.openglVersion = major * 10 + minor
			} catch (@Pc(78) ex: NumberFormatException) {
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
		@Pc(124) val values: IntArray = IntArray(1)
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_UNITS, values, 0)
		this.maxTextureUnits = values[0]
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_COORDS, values, 0)
		this.maxTextureCoordinates = values[0]
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_IMAGE_UNITS, values, 0)
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
		OpenGL.glGetFloatv(OpenGLConstants.GL_POINT_SIZE_RANGE, OpenGlToolkit.BUFFER_4F, 0)
		this.fillCirclePointSize = OpenGlToolkit.BUFFER_4F[0]
		this.outlineCirclePointSize = OpenGlToolkit.BUFFER_4F[1]
		return if (errors == 0) 0 else errors
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	override fun drawTexturedLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int, @OriginalArg(9) dashLength: Int, @OriginalArg(10) gapLength: Int, @OriginalArg(11) offset: Int) {
		if (endX == startX && startY == endY) {
			return
		}
		@Pc(19) val mask: GlClippingMask? = clippingMask as GlClippingMask?
		@Pc(22) val surface: OpenGl2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		@Pc(52) val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		@Pc(59) val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		@Pc(66) val dx: Float = (-startX).toFloat() + endX.toFloat()
		@Pc(72) val dy: Float = endY.toFloat() - startY.toFloat()
		@Pc(85) val inverseLength: Float = (1.0 / kotlin.math.sqrt((dx * dx + dy * dy).toDouble())).toFloat()
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		@Pc(104) val dirX: Float = dx * inverseLength
		@Pc(108) val dirY: Float = dy * inverseLength
		@Pc(114) val phase: Int = offset % (gapLength + dashLength)
		@Pc(119) val dashX: Float = dashLength.toFloat() * dirX
		@Pc(124) val dashY: Float = dirY * dashLength.toFloat()
		@Pc(126) var startOffsetX: Float = 0.0f
		@Pc(128) var startOffsetY: Float = 0.0f
		@Pc(130) var segmentX: Float = dashX
		@Pc(132) var segmentY: Float = dashY
		if (phase <= dashLength) {
			segmentX = dirX * (dashLength - phase).toFloat()
			segmentY = dirY * (dashLength - phase).toFloat()
		} else {
			startOffsetY = (dashLength + gapLength - phase).toFloat() * dirY
			startOffsetX = (dashLength + gapLength - phase).toFloat() * dirX
		}
		@Pc(181) var currentX: Float = startOffsetX + startX.toFloat() + 0.35f
		@Pc(188) var currentY: Float = startY.toFloat() + startOffsetY + 0.35f
		@Pc(193) val gapX: Float = gapLength.toFloat() * dirX
		@Pc(198) val gapY: Float = gapLength.toFloat() * dirY
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
			OpenGL.glBegin(OpenGLConstants.GL_LINES)
			OpenGL.glTexCoord2f(texScaleS * ((-maskX).toFloat() + currentX), texScaleT * ((-maskY).toFloat() + currentY))
			OpenGL.glVertex2f(currentX, currentY)
			OpenGL.glTexCoord2f((currentX + segmentX - maskX.toFloat()) * texScaleS, ((-maskY).toFloat() + currentY + segmentY) * texScaleT)
			OpenGL.glVertex2f(segmentX + currentX, currentY + segmentY)
			OpenGL.glEnd()
			currentX += segmentX + gapX
			currentY += segmentY + gapY
			segmentY = dashY
			segmentX = dashX
		}
		this.setTexture(5890, 768, 0)
	}

	@OriginalMember(owner = "client!qo", name = "X", descriptor = "(I)V")
	override fun X(@OriginalArg(0) scale: Int) {
		var scale: Int = scale
		this.shadowScaleShift = 0
		while (scale > 1) {
			this.shadowScaleShift++
			scale = scale shr 0x1
		}
		this.shadowScale = 0x1 shl this.shadowScaleShift
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	override fun createMask(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) rowOffsets: IntArray?, @OriginalArg(3) rowWidths: IntArray?): ComponentSurface {
		return GlClippingMask.create(width, rowOffsets, rowWidths, this, height)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([I)V")
	override fun accumulateViewportDimensions(@OriginalArg(0) dimensions: IntArray?) {
		dimensions!![0] = this.viewportWidth
		dimensions[1] = this.viewportHeight
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!za;)V")
	override fun setHeap(@OriginalArg(0) memoryPool: MemoryPool?) {
		this.nativeHeap = (memoryPool as GlMemoryPool).heap
		if (this.screenQuadArrayBuffer != null) {
			return
		}
		@Pc(16) val gpuPacket: GlBitBuffer = GlBitBuffer(80)
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

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "(ILclient!aa;II)V")
	override fun A(@OriginalArg(0) colour: Int, @OriginalArg(1) clippingMask: ComponentSurface?, @OriginalArg(2) maskX: Int, @OriginalArg(3) maskY: Int) {
		@Pc(6) val mask: GlClippingMask? = clippingMask as GlClippingMask?
		@Pc(9) val surface: OpenGl2DImageTexture? = mask!!.surface
		this.setupSpriteRendering()
		this.setActiveTexture(mask.surface)
		this.setBlendMode(1)
		this.loadTextureCombineMode(7681, 8448)
		this.setTexture(34167, 768, 0)
		@Pc(39) val texScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		@Pc(46) val texScaleT: Float = surface.t / surface.singleLength.toFloat()
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glTexCoord2f(texScaleS * (this.clipLeft - maskX).toFloat(), (this.clipTop - maskY).toFloat() * texScaleT)
		OpenGL.glVertex2i(this.clipLeft, this.clipTop)
		OpenGL.glTexCoord2f(texScaleS * (this.clipLeft - maskX).toFloat(), texScaleT * (this.clipBottom - maskY).toFloat())
		OpenGL.glVertex2i(this.clipLeft, this.clipBottom)
		OpenGL.glTexCoord2f((this.clipRight - maskX).toFloat() * texScaleS, (this.clipBottom - maskY).toFloat() * texScaleT)
		OpenGL.glVertex2i(this.clipRight, this.clipBottom)
		OpenGL.glTexCoord2f(texScaleS * (this.clipRight - maskX).toFloat(), texScaleT * (this.clipTop - maskY).toFloat())
		OpenGL.glVertex2i(this.clipRight, this.clipTop)
		OpenGL.glEnd()
		this.setTexture(5890, 768, 0)
	}

	@OriginalMember(owner = "client!qo", name = "t", descriptor = "()Z")
	override fun replaceCanvasOnDisplayUpdate(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II)Lclient!vm;")
	override fun createDepthBuffer(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): DepthBuffer? {
		return null
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZIII)V")
	fun setTextureTransparency(@OriginalArg(1) textureUnit: Int, @OriginalArg(3) alpha: Int) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_SRC0_ALPHA, alpha)
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_OPERAND0_ALPHA, OpenGLConstants.GL_SRC_ALPHA)
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "(I)V")
	private fun attach() {
		@Pc(13) var attempts: Int = 0
		while (!this.activeOpenglInstance!!.b()) {
			if (attempts++ > 5) {
				throw RuntimeException("")
			}
			TimeUtils.sleep(1000L)
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIII)V")
	override fun strongLine(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) width: Int) {
		OpenGL.glLineWidth(width.toFloat())
		this.line(x1, y1, x2, y2, colour, 0)
		OpenGL.glLineWidth(1.0f)
	}

	@OriginalMember(owner = "client!qo", name = "ra", descriptor = "(IIII)V")
	override fun ra(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) fogColour: Int, @OriginalArg(2) depth: Int, @OriginalArg(3) intensity: Int) {
		this.waterHeight = waterHeight
		this.waterDepth = depth
		this.waterFogColour = fogColour
		this.stillWaterToggled = true
		this.waterIntensity = intensity
	}

	@OriginalMember(owner = "client!qo", name = "s", descriptor = "(I)V")
	private fun updateDepthRange() {
		this.projectionMatrix[10] = this.projectionDepthScale
		this.projectionMatrix[14] = this.projectionDepthOffset
		this.farClipDistance = this.zFar.toFloat()
		this.projectedFarClipDistance = (this.projectionMatrix[14] - this.zFar.toFloat()) / this.projectionMatrix[10]
	}

	@OriginalMember(owner = "client!qo", name = "T", descriptor = "(IIII)V")
	override fun T(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int) {
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
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
		this.defineScissorBox()
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	override fun resizeCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(5) var surface: Long = 0L
		if (canvas == null || this.canvas === canvas) {
			surface = this.canvasSurface
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(22) val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	override fun createNativeSprite(@OriginalArg(0) pixels: IntArray?, @OriginalArg(2) rowLength: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) copyPixels: Boolean): NativeSprite {
		return SpriteGL(this, width, height, pixels, 0, rowLength)
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(B)V")
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

	@OriginalMember(owner = "client!qo", name = "t", descriptor = "(I)V")
	private fun updateSunDiffuse() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorIntensity * this.sunColorRed
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunColorIntensity
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunColorIntensity
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0)
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * -this.antiSunColorIntensity
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * -this.antiSunColorIntensity
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorRed * -this.antiSunColorIntensity
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(III)V")
	@Synchronized
	fun requestArbBufferCleanup(@OriginalArg(0) bufferSize: Int, @OriginalArg(2) bufferHandle: Int) {
		@Pc(8) val intNode: IntNode = IntNode(bufferSize)
		intNode.key = bufferHandle.toLong()
		this.vboBuffersAwaitingDeallocation.addLast(intNode)
	}

	@OriginalMember(owner = "client!qo", name = "M", descriptor = "()I")
	override fun M(): Int {
		@Pc(6) val count: Int = this.modelCount
		this.modelCount = 0
		return count
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(BI)I")
	fun getCombineModeEffectType(@OriginalArg(1) mode: Int): Int {
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

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Lclient!tv;I)V")
	fun pushReadTarget(@OriginalArg(0) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIII)V")
	override fun outlineRect(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		@Pc(8) val left: Float = x.toFloat() + 0.35f
		@Pc(13) val top: Float = y.toFloat() + 0.35f
		@Pc(20) val right: Float = left + width.toFloat() - 1.0f
		this.enter2dMode()
		@Pc(30) val bottom: Float = top + height.toFloat() - 1.0f
		this.setBlendMode(mode)
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.arbMultisample) {
			OpenGL.glDisable(32925)
		}
		OpenGL.glBegin(OpenGLConstants.GL_LINE_LOOP)
		OpenGL.glVertex2f(left, top)
		OpenGL.glVertex2f(left, bottom)
		OpenGL.glVertex2f(right, bottom)
		OpenGL.glVertex2f(right, top)
		OpenGL.glEnd()
		if (this.arbMultisample) {
			OpenGL.glEnable(32925)
		}
	}

	@OriginalMember(owner = "client!qo", name = "H", descriptor = "(III[I)V")
	override fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) output: IntArray?) {
		@Pc(28) val depth: Float = this.viewMatrix43.zD + x.toFloat() * this.viewMatrix43.zA + this.viewMatrix43.zB * y.toFloat() + this.viewMatrix43.zC * z.toFloat()
		if (depth == 0.0f) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		@Pc(77) val screenX: Int = ((this.viewMatrix43.xD + this.viewMatrix43.xC * z.toFloat() + this.viewMatrix43.xB * y.toFloat() + this.viewMatrix43.xA * x.toFloat()) * this.projectionScaleX.toFloat() / depth).toInt()
		@Pc(109) val screenY: Int = ((x.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yB * y.toFloat() + this.viewMatrix43.yC * z.toFloat() + this.viewMatrix43.yD) * this.projectionScaleY.toFloat() / depth).toInt()
		output!![0] = (screenX.toFloat() - this.viewX1).toInt()
		output[2] = depth.toInt()
		output[1] = (screenY.toFloat() - this.viewY1).toInt()
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "()Lclient!bca;")
	override fun createMatrix(): Matrix {
		return Matrix43()
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "()Z")
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

	@OriginalMember(owner = "client!qo", name = "u", descriptor = "(I)V")
	private fun updatePointLights() {
		@Pc(7) var lightIndex: Int = 0
		while (lightIndex < this.lastSceneLightCount) {
			@Pc(14) val pointLight: PointLight? = this.lightNodes[lightIndex]
			OpenGlToolkit.LIGHT_MATRIX[0] = pointLight!!.getX().toFloat()
			@Pc(25) val glLight: Int = lightIndex + 16386
			OpenGlToolkit.LIGHT_MATRIX[1] = pointLight.getY().toFloat()
			OpenGlToolkit.LIGHT_MATRIX[2] = pointLight.getZ().toFloat()
			OpenGlToolkit.LIGHT_MATRIX[3] = 1.0f
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_POSITION, OpenGlToolkit.LIGHT_MATRIX, 0)
			@Pc(52) val range: Int = pointLight.getColour()
			@Pc(58) val intensityScale: Float = pointLight.getIntensity() / 255.0f
			OpenGlToolkit.LIGHT_MATRIX[2] = (range and 0xFF).toFloat() * intensityScale
			OpenGlToolkit.LIGHT_MATRIX[0] = (range shr 16 and 0xFF).toFloat() * intensityScale
			OpenGlToolkit.LIGHT_MATRIX[1] = (range shr 8 and 0xFF).toFloat() * intensityScale
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.LIGHT_MATRIX, 0)
			OpenGL.glLightf(glLight, OpenGLConstants.GL_QUADRATIC_ATTENUATION, 1.0f / (pointLight.getRadius() * pointLight.getRadius()).toFloat())
			OpenGL.glEnable(glLight)
			lightIndex++
		}
		while (lightIndex < this.activeLightCount) {
			OpenGL.glDisable(lightIndex + OpenGLConstants.GL_LIGHT2)
			lightIndex++
		}
		this.activeLightCount = this.lastSceneLightCount
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(B)V")
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

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(II)I")
	fun getDataTypeSize(@OriginalArg(0) dataType: Int): Int {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!mda;I)V")
	override fun renderOrtho(@OriginalArg(0) particleList: ParticleList?, @OriginalArg(1) zoom: Int) {
		this.particleRenderer.drawParticles(this, zoom, particleList)
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(Z)V")
	private fun buildTables() {
		this.openglTextures = arrayOfNulls<GlTexture>(this.maxTextureUnits)
		this.texture2d = OpenGl2DTexture(this, 3553, 6408, 1, 1)
		OpenGl2DTexture(this, 3553, 6408, 1, 1)
		OpenGl2DTexture(this, 3553, 6408, 1, 1)
		this.copyTarget1 = GlModel(this)
		this.copyTarget2 = GlModel(this)
		this.copyTarget3 = GlModel(this)
		this.copyTarget4 = GlModel(this)
		this.copyTarget5 = GlModel(this)
		this.copyBuffer1 = GlModel(this)
		this.copyBuffer2 = GlModel(this)
		this.copyBuffer3 = GlModel(this)
		this.copyBuffer4 = GlModel(this)
		this.copyBuffer5 = GlModel(this)
		if (this.extFramebufferObject) {
			this.integratedGraphicsImageProducer = GlFramebuffer(this)
			GlFramebuffer(this)
		}
	}

	@OriginalMember(owner = "client!qo", name = "v", descriptor = "(I)V")
	private fun updateAmbient() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0f
		OpenGlToolkit.BUFFER_4F[0] = this.sunBrightness * this.sunColorRed
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunBrightness
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunBrightness
		OpenGL.glLightModelfv(OpenGLConstants.GL_LIGHT_MODEL_AMBIENT, OpenGlToolkit.BUFFER_4F, 0)
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "()Lclient!fia;")
	override fun renderer(): Renderer {
		@Pc(7) var vendor: Int = -1
		if (this.gpuCompanyString!!.indexOf("nvidia") != -1) {
			vendor = PciVendorId.NVIDIA
		} else if (this.gpuCompanyString!!.indexOf("intel") != -1) {
			vendor = PciVendorId.INTEL
		} else if (this.gpuCompanyString!!.indexOf("ati") != -1) {
			vendor = PciVendorId.AMD
		}
		return Renderer(vendor, "OpenGL", this.openglVersion, this.gpuNameString, 0L)
	}

	@OriginalMember(owner = "client!qo", name = "da", descriptor = "(III[I)V")
	override fun da(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) output: IntArray?) {
		@Pc(28) val depth: Float = y.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x.toFloat() + this.viewMatrix43.zC * z.toFloat() + this.viewMatrix43.zD
		if (depth < this.zNear.toFloat() || depth > this.zFar.toFloat()) {
			output!![2] = -1
			output[1] = -1
			output[0] = -1
			return
		}
		@Pc(85) val screenX: Int = (this.projectionScaleX.toFloat() * (y.toFloat() * this.viewMatrix43.xB + this.viewMatrix43.xA * x.toFloat() + this.viewMatrix43.xC * z.toFloat() + this.viewMatrix43.xD) / depth).toInt()
		@Pc(117) val screenY: Int = ((this.viewMatrix43.yD + x.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yB * y.toFloat() + this.viewMatrix43.yC * z.toFloat()) * this.projectionScaleY.toFloat() / depth).toInt()
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

	@OriginalMember(owner = "client!qo", name = "w", descriptor = "(I)V")
	fun pushMatrix() {
		OpenGL.glPushMatrix()
	}

	@OriginalMember(owner = "client!qo", name = "F", descriptor = "(II)V")
	override fun F(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int) {
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!is;)V")
	override fun swapSurface(@OriginalArg(0) surface: OffscreenSurface?) {
	}

	@OriginalMember(owner = "client!qo", name = "KA", descriptor = "(IIII)V")
	override fun KA(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int) {
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
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
		this.defineScissorBox()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BF)V")
	fun setOrthoZoom(@OriginalArg(1) zoom: Float) {
		if (this.orthoZoom != zoom) {
			this.orthoZoom = zoom
			if (this.depthClippingMode == 3) {
				this.loadOrthoProjection()
			}
		}
	}

	@OriginalMember(owner = "client!qo", name = "la", descriptor = "()V")
	override fun la() {
		this.clipTop = 0
		this.clipBottom = this.viewportHeight
		this.clipRight = this.viewportWidth
		this.clipLeft = 0
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		this.updateViewBounds()
	}

	@OriginalMember(owner = "client!qo", name = "C", descriptor = "(Z)V")
	override fun C(@OriginalArg(0) enabled: Boolean) {
		this.depthBufferingEnabled = enabled
		this.setDepthMask()
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "()V")
	override fun cacheReset() {
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(III)V")
	fun setViewportOffset(@OriginalArg(1) offsetY: Int, @OriginalArg(2) offsetX: Int) {
		this.viewportOffsetY = offsetY
		this.viewportOffsetX = offsetX
		this.updateViewport()
		this.defineScissorBox()
	}

	@OriginalMember(owner = "client!qo", name = "JA", descriptor = "(IIIIII)I")
	override fun JA(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int): Int {
		@Pc(7) var outcode: Int = 0
		@Pc(32) var depth1: Float = z1.toFloat() * this.viewMatrix43.zC + x1.toFloat() * this.viewMatrix43.zA + y1.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zD
		if (depth1 < 1.0f) {
			depth1 = 1.0f
		}
		@Pc(63) var depth2: Float = z2.toFloat() * this.viewMatrix43.zC + y2.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zA * x2.toFloat() + this.viewMatrix43.zD
		if (depth2 < 1.0f) {
			depth2 = 1.0f
		}
		if (this.zNear.toFloat() > depth1 && this.zNear.toFloat() > depth2) {
			outcode = outcode or 0x10
		} else if (depth1 > this.zFar.toFloat() && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		@Pc(135) val screenX1: Int = ((this.viewMatrix43.xC * z1.toFloat() + this.viewMatrix43.xB * y1.toFloat() + this.viewMatrix43.xA * x1.toFloat() + this.viewMatrix43.xD) * this.projectionScaleX.toFloat() / depth1).toInt()
		@Pc(167) val screenX2: Int = (this.projectionScaleX.toFloat() * (z2.toFloat() * this.viewMatrix43.xC + this.viewMatrix43.xA * x2.toFloat() + this.viewMatrix43.xB * y2.toFloat() + this.viewMatrix43.xD) / depth2).toInt()
		if (screenX1.toFloat() < this.viewX1 && this.viewX1 > screenX2.toFloat()) {
			outcode = outcode or 0x1
		} else if (screenX1.toFloat() > this.viewX2 && screenX2.toFloat() > this.viewX2) {
			outcode = outcode or 0x2
		}
		@Pc(233) val screenY1: Int = (this.projectionScaleY.toFloat() * (this.viewMatrix43.yC * z1.toFloat() + y1.toFloat() * this.viewMatrix43.yB + x1.toFloat() * this.viewMatrix43.yA + this.viewMatrix43.yD) / depth1).toInt()
		@Pc(265) val screenY2: Int = ((this.viewMatrix43.yD + x2.toFloat() * this.viewMatrix43.yA + y2.toFloat() * this.viewMatrix43.yB + this.viewMatrix43.yC * z2.toFloat()) * this.projectionScaleY.toFloat() / depth2).toInt()
		if (screenY1.toFloat() < this.viewY1 && this.viewY1 > screenY2.toFloat()) {
			outcode = outcode or 0x4
		} else if (screenY1.toFloat() > this.viewY2 && screenY2.toFloat() > this.viewY2) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	@OriginalMember(owner = "client!qo", name = "s", descriptor = "()Z")
	override fun utilisesNativeHeap(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIZ)Lclient!mi;")
	override fun generateEmptySprite(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) hasAlpha: Boolean): NativeSprite {
		return SpriteGL(this, width, height, hasAlpha)
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(I)V")
	override fun method7281(@OriginalArg(0) threadId: Int) {
		this.detach()
	}

	@OriginalMember(owner = "client!qo", name = "x", descriptor = "(I)V")
	private fun prepareForRendering() {
		this.renderTexture(-2)
		@Pc(14) var i: Int = this.maxTextureUnits - 1
		while (i >= 0) {
			this.activateTexture(i)
			this.setActiveTexture(null)
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_MODE, OpenGLConstants.GL_COMBINE)
			i--
		}
		this.loadTextureCombineMode(8448, 8448)
		this.setTexture(34168, 770, 2)
		this.renderStationaryTexture()
		this.activeBlendAlpha = 1
		OpenGL.glEnable(OpenGLConstants.GL_BLEND)
		OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA)
		this.activeBlendType = 1
		OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST)
		OpenGL.glAlphaFunc(OpenGLConstants.GL_GREATER, 0.0f)
		this.alphaTestEnabled = true
		OpenGL.glColorMask(true, true, true, true)
		this.colormaskActive = true
		this.toggleFog(true)
		this.setLightingEnabled(true)
		this.toggleZbuffer(true)
		this.setDepthMask(true)
		this.setOrthoMatrixMode()
		this.activeOpenglInstance!!.setSwapInterval(0)
		OpenGL.glShadeModel(OpenGLConstants.GL_SMOOTH)
		OpenGL.glClearDepth(1.0f)
		OpenGL.glDepthFunc(OpenGLConstants.GL_LEQUAL)
		OpenGL.glPolygonMode(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_FILL)
		OpenGL.glEnable(OpenGLConstants.GL_CULL_FACE)
		OpenGL.glCullFace(OpenGLConstants.GL_BACK)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		OpenGL.glLoadIdentity()
		OpenGL.glColorMaterial(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_AMBIENT_AND_DIFFUSE)
		OpenGL.glEnable(OpenGLConstants.GL_COLOR_MATERIAL)
		@Pc(125) val zeroAmbient: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
		for (@Pc(127) j in 0 until 8) {
			@Pc(133) val glLight: Int = j + 16384
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_AMBIENT, zeroAmbient, 0)
			OpenGL.glLightf(glLight, OpenGLConstants.GL_CONSTANT_ATTENUATION, 0.0f)
			OpenGL.glLightf(glLight, OpenGLConstants.GL_LINEAR_ATTENUATION, 0.0f)
		}
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT0)
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT1)
		OpenGL.glFogf(OpenGLConstants.GL_FOG_DENSITY, 0.95f)
		OpenGL.glFogi(OpenGLConstants.GL_FOG_MODE, OpenGLConstants.GL_LINEAR)
		OpenGL.glHint(OpenGLConstants.GL_FOG_HINT, OpenGLConstants.GL_FASTEST)
		this.fogColor = -1
		this.sunColorRgb = -1
		this.la()
	}

	@OriginalMember(owner = "client!qo", name = "y", descriptor = "(I)V")
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

	@OriginalMember(owner = "client!qo", name = "I", descriptor = "()I")
	override fun I(): Int {
		@Pc(6) val count: Int = this.polygonCount
		this.polygonCount = 0
		return count
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!ni;)V")
	fun setElementsBuffer(@OriginalArg(1) elementsBuffer: ElementsBufferGL?) {
		if (this.glBinding !== elementsBuffer) {
			if (this.vertexbufferSupported) {
				OpenGL.glBindBufferARB(OpenGLConstants.GL_ELEMENT_ARRAY_BUFFER, elementsBuffer!!.getId())
			}
			this.glBinding = elementsBuffer
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!nh;Lclient!nh;ILclient!nh;Lclient!nh;)V")
	fun toggleDrawingClientstates(@OriginalArg(0) vertices: OpenGlArrayDrawBuffer?, @OriginalArg(1) colors: OpenGlArrayDrawBuffer?, @OriginalArg(3) textureCoordinates: OpenGlArrayDrawBuffer?, @OriginalArg(4) normals: OpenGlArrayDrawBuffer?) {
		if (vertices == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
		} else {
			this.setArrayBuffer(vertices.buffer)
			OpenGL.glVertexPointer(vertices.size.toInt(), vertices.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + vertices.offset.toLong())
			OpenGL.glEnableClientState(OpenGLConstants.GL_VERTEX_ARRAY)
		}
		if (normals == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
		} else {
			this.setArrayBuffer(normals.buffer)
			OpenGL.glNormalPointer(normals.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + normals.offset.toLong())
			OpenGL.glEnableClientState(OpenGLConstants.GL_NORMAL_ARRAY)
		}
		if (colors == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_COLOR_ARRAY)
		} else {
			this.setArrayBuffer(colors.buffer)
			OpenGL.glColorPointer(colors.size.toInt(), colors.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + colors.offset.toLong())
			OpenGL.glEnableClientState(OpenGLConstants.GL_COLOR_ARRAY)
		}
		if (textureCoordinates == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
		} else {
			this.setArrayBuffer(textureCoordinates.buffer)
			OpenGL.glTexCoordPointer(textureCoordinates.size.toInt(), textureCoordinates.type.toInt(), this.currentArrayBuffer!!.getStride(), this.currentArrayBuffer!!.getAddress() + textureCoordinates.offset.toLong())
			OpenGL.glEnableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY)
		}
	}

	@OriginalMember(owner = "client!qo", name = "x", descriptor = "()I")
	override fun getMaxLights(): Int {
		return 4
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	override fun createOffscreenSurface(@OriginalArg(0) surface: Sprite?, @OriginalArg(1) buffer: DepthBuffer?): OffscreenSurface? {
		return null
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "()Lclient!bca;")
	override fun camera(): Matrix {
		return this.viewMatrix43
	}

	@OriginalMember(owner = "client!qo", name = "z", descriptor = "(I)V")
	private fun setDepthMask() {
		OpenGL.glDepthMask(this.enableDepthMask && this.depthBufferingEnabled)
	}

	@OriginalMember(owner = "client!qo", name = "y", descriptor = "()Lclient!bca;")
	override fun scratchMatrix(): Matrix {
		return this.scratchMatrix
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "()I")
	override fun i(): Int {
		return this.zNear
	}

	@OriginalMember(owner = "client!qo", name = "ya", descriptor = "()V")
	override fun ya() {
		this.setDepthMask(true)
		OpenGL.glClear(OpenGLConstants.GL_DEPTH_BUFFER_BIT)
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Z)V")
	override fun method7250(@OriginalArg(0) enabled: Boolean) {
	}

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "(I)V")
	fun setLightingSourceParams() {
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_POSITION, this.sunDirection, 0)
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_POSITION, this.lightingDirection, 0)
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "()Z")
	override fun stillwaterEffectLoaded(): Boolean {
		return this.effectManager.isStillWaterSupported()
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "()Z")
	override fun bloom(): Boolean {
		return this.rectangularTexture != null && this.rectangularTexture!!.isActive()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(I[Lclient!ab;)V")
	override fun copyLights(@OriginalArg(0) count: Int, @OriginalArg(1) pointLights: Array<PointLight?>?) {
		for (@Pc(3) i in 0 until count) {
			this.lightNodes[i] = pointLights!![i]
		}
		this.lastSceneLightCount = count
		if (this.depthClippingMode != 1) {
			this.updatePointLights()
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Z)V")
	override fun setShrinkTextures(@OriginalArg(0) shrink: Boolean) {
	}

	@OriginalMember(owner = "client!qo", name = "B", descriptor = "(I)V")
	private fun detach() {
		this.activeOpenglInstance!!.a()
	}

	@OriginalMember(owner = "client!qo", name = "pa", descriptor = "()V")
	override fun pa() {
		this.stillWaterToggled = false
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "(IIIIIII)I")
	override fun r(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int, @OriginalArg(6) projectionDepth: Int): Int {
		@Pc(30) val depth1: Float = z1.toFloat() * this.viewMatrix43.zC + y1.toFloat() * this.viewMatrix43.zB + x1.toFloat() * this.viewMatrix43.zA + this.viewMatrix43.zD
		@Pc(55) val depth2: Float = z2.toFloat() * this.viewMatrix43.zC + this.viewMatrix43.zA * x2.toFloat() + y2.toFloat() * this.viewMatrix43.zB + this.viewMatrix43.zD
		@Pc(57) var outcode: Int = 0
		if (depth1 < this.zNear.toFloat() && this.zNear.toFloat() > depth2) {
			outcode = outcode or 0x10
		} else if (this.zFar.toFloat() < depth1 && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		@Pc(124) val screenX1: Int = (this.projectionScaleX.toFloat() * (this.viewMatrix43.xC * z1.toFloat() + this.viewMatrix43.xB * y1.toFloat() + this.viewMatrix43.xA * x1.toFloat() + this.viewMatrix43.xD) / projectionDepth.toFloat()).toInt()
		@Pc(157) val screenX2: Int = (this.projectionScaleX.toFloat() * (this.viewMatrix43.xD + this.viewMatrix43.xC * z2.toFloat() + x2.toFloat() * this.viewMatrix43.xA + this.viewMatrix43.xB * y2.toFloat()) / projectionDepth.toFloat()).toInt()
		if (this.viewX1 > screenX1.toFloat() && this.viewX1 > screenX2.toFloat()) {
			outcode = outcode or 0x1
		} else if (this.viewX2 < screenX1.toFloat() && screenX2.toFloat() > this.viewX2) {
			outcode = outcode or 0x2
		}
		@Pc(224) val screenY1: Int = ((this.viewMatrix43.yD + this.viewMatrix43.yC * z1.toFloat() + this.viewMatrix43.yA * x1.toFloat() + y1.toFloat() * this.viewMatrix43.yB) * this.projectionScaleY.toFloat() / projectionDepth.toFloat()).toInt()
		@Pc(257) val screenY2: Int = (this.projectionScaleY.toFloat() * (z2.toFloat() * this.viewMatrix43.yC + y2.toFloat() * this.viewMatrix43.yB + this.viewMatrix43.yA * x2.toFloat() + this.viewMatrix43.yD) / projectionDepth.toFloat()).toInt()
		if (this.viewY1 > screenY1.toFloat() && screenY2.toFloat() < this.viewY1) {
			outcode = outcode or 0x4
		} else if (screenY1.toFloat() > this.viewY2 && this.viewY2 < screenY2.toFloat()) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(II)I")
	fun getBytesPerPixel(@OriginalArg(0) format: Int): Int {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFFB)V")
	private fun renderAnimatedTexture(@OriginalArg(0) offsetV: Float, @OriginalArg(1) offsetW: Float, @OriginalArg(2) offsetU: Float) {
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE)
		if (this.textureUpdate) {
			OpenGL.glLoadIdentity()
		}
		OpenGL.glTranslatef(offsetU, offsetV, offsetW)
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		this.textureUpdate = true
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "(II)V")
	@Synchronized
	fun requestFrameBufferCleanup(@OriginalArg(1) bufferSize: Int) {
		@Pc(8) val intNode: IntNode = IntNode(bufferSize)
		this.frameBuffersAwaitingDeallocation.addLast(intNode)
	}

	@OriginalMember(owner = "client!qo", name = "v", descriptor = "()V")
	override fun compositeBloomEffects() {
		this.framebufferCompositor!!.compositeEffects()
	}

	@OriginalMember(owner = "client!qo", name = "P", descriptor = "(IIIII)V")
	override fun P(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) height: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) blendMode: Int) {
		this.enter2dMode()
		this.setBlendMode(blendMode)
		@Pc(15) val startX: Float = x.toFloat() + 0.35f
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		@Pc(35) val startY: Float = y.toFloat() + 0.35f
		OpenGL.glBegin(OpenGLConstants.GL_LINES)
		OpenGL.glVertex2f(startX, startY)
		OpenGL.glVertex2f(startX, height.toFloat() + startY)
		OpenGL.glEnd()
	}

	@OriginalMember(owner = "client!qo", name = "E", descriptor = "()I")
	override fun E(): Int {
		return this.renderBuffersSize + this.textureBuffersSize + this.vboBuffersSize
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	override fun drawQuad(@OriginalArg(0) environmentSampler: EnvironmentSampler?, @OriginalArg(1) environmentSampler2: EnvironmentSampler?, @OriginalArg(2) alpha: Float, @OriginalArg(3) environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		if (environmentSampler != null && environmentSampler2 != null && this.arbTextureCubeMap && this.extFramebufferObject) {
			@Pc(15) var result: OpenGlCubeTextureGenerator? = null
			@Pc(18) val glEnvironmentSampler: GlEnvironmentSampler = environmentSampler as GlEnvironmentSampler
			@Pc(21) val glEnvironmentSampler2: GlEnvironmentSampler = environmentSampler2 as GlEnvironmentSampler
			@Pc(25) val glCubeTexture: GlCubeTexture? = glEnvironmentSampler.getCubeTexture()
			@Pc(29) val glCubeTexture2: GlCubeTexture? = glEnvironmentSampler2.getCubeTexture()
			if (glCubeTexture != null && glCubeTexture2 != null) {
				@Pc(44) val size: Int = if (glCubeTexture2.size >= glCubeTexture.size) glCubeTexture2.size else glCubeTexture.size
				if (environmentSampler3 !== environmentSampler && environmentSampler2 !== environmentSampler3 && environmentSampler3 is OpenGlCubeTextureGenerator) {
					@Pc(56) val previous: OpenGlCubeTextureGenerator = environmentSampler3
					if (size == previous.getSize()) {
						result = previous
					}
				}
				if (result == null) {
					result = OpenGlCubeTextureGenerator(this, size)
				}
				if (result.blendCubeTextures(glCubeTexture, alpha, glCubeTexture2)) {
					return result
				}
			}
		}
		return if (alpha < 0.5f) environmentSampler else environmentSampler2
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	override fun setCanvas(@OriginalArg(0) canvas: Canvas?) {
		this.currentCanvas = null
		this.currentSurface = 0L
		if (canvas == null || canvas === this.canvas) {
			this.currentCanvas = this.canvas
			this.currentSurface = this.canvasSurface
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(36) val long2: Long? = this.canvasSurfaces.get(canvas) as Long?
			this.currentSurface = long2!!
			this.currentCanvas = canvas
		}
		if (this.currentCanvas == null || this.currentSurface == 0L) {
			throw RuntimeException()
		}
		this.activeOpenglInstance!!.setSurface(this.currentSurface)
		this.updateCanvasSize()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZII)V")
	fun renderTexture(@OriginalArg(0) hasLight: Boolean, @OriginalArg(1) materialId: Int) {
		this.renderTexture(hasLight, materialId, true)
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(II)I")
	override fun compareFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		return maskB xor (maskA and maskB)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	override fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		this.flip(x, y)
	}

	@OriginalMember(owner = "client!qo", name = "C", descriptor = "(I)V")
	private fun updateFog() {
		this.fogEndPosition = (this.zFar - this.fogOffset).toFloat() - this.fogMin
		this.fogStartPosition = this.fogEndPosition - this.densityFactor * this.fogDensity.toFloat()
		if (this.zNear.toFloat() > this.fogStartPosition) {
			this.fogStartPosition = this.zNear.toFloat()
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_START, this.fogStartPosition)
		OpenGL.glFogf(OpenGLConstants.GL_FOG_END, this.fogEndPosition)
		OpenGlToolkit.BUFFER_4F[0] = (this.fogColor and 0xFF0000).toFloat() / 1.671168E7f
		OpenGlToolkit.BUFFER_4F[1] = (this.fogColor and 0xFF00).toFloat() / 65280.0f
		OpenGlToolkit.BUFFER_4F[2] = (this.fogColor and 0xFF).toFloat() / 255.0f
		OpenGL.glFogfv(OpenGLConstants.GL_FOG_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZB)V")
	fun setHideLights(@OriginalArg(0) enabled: Boolean) {
		if (enabled != this.hideLights) {
			this.hideLights = enabled
			this.toggleLighting()
		}
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	override fun flip(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		try {
			if (this.scaledTarget != null) {
				// Not part of the original client: the frame was drawn into the scaled target at the
				// game's size; copy it to the window, scaled, before the buffers swap.
				OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
				if (this.scaledMultisample != null) {
					OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
					OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
					OpenGL.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, this.scaledWidth, this.scaledHeight, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
				}
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, 0)
				OpenGL.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, ClientFeatures.scale(this.scaledWidth), ClientFeatures.scale(this.scaledHeight),
					OpenGLConstants.GL_COLOR_BUFFER_BIT, if (ClientFeatures.wholeScale()) OpenGLConstants.GL_NEAREST else OpenGLConstants.GL_LINEAR)
				if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
					OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
				}
				this.activeOpenglInstance!!.swapBuffers()
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer())
				return
			}
			this.activeOpenglInstance!!.swapBuffers()
		} catch (@Pc(4) ex: Exception) {
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
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		OpenGL.glBlitFramebufferEXT(x, y, x + width, y + height, x, y, x + width, y + height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
		if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
			OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		}
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		this.readingResolved = true
	}

	fun endReadFrame() {
		if (this.readingResolved) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
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
	private var sceneTarget: GlSceneTarget? = null
	private var sceneActive: Boolean = false
	private var sceneX: Int = 0
	private var sceneY: Int = 0
	private var sceneWidth: Int = 0
	private var sceneHeight: Int = 0
	private var sceneSavedOffsetX: Int = 0
	private var sceneSavedOffsetY: Int = 0
	private var sceneScale: Double = 1.0
	private var smaaFilter: GlSmaaFilter? = null

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
				OpenGL.glEnable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE)
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE)
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
	// at startup, so the renderer stays OpenGL without anti-aliasing and says so.
	private fun checkScenePipeline() {
		if (!ClientFeatures.AA_PIPELINE || ClientFeatures.aaPipelineUnavailable) {
			return
		}
		if (!this.extFramebufferObject || !this.framebufferBlit || !this.framebufferMultisample) {
			lang.printlnErr("client.msaa: this OpenGL driver has no framebuffer objects, blit or multisample; anti-aliasing is off")
			ClientFeatures.aaPipelineUnavailable = true
			return
		}
		@Pc(0) val max: IntArray = IntArray(1)
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_SAMPLES, max, 0)
		this.sceneMaxSamples = max[0]
		this.sceneAvailable = true
		println("client.msaa: the 3D view is drawn in its own framebuffer; up to " + this.sceneMaxSamples + " samples")
	}

	// The samples the view is drawn with now: the switch or the game's setting, within the driver's limit.
	private fun sceneSamples(): Int {
		val samples: Int = ClientFeatures.sceneSamples(this.sceneSettingSamples)
		return if (samples > this.sceneMaxSamples) this.sceneMaxSamples else samples
	}

	// Starts the 3D view at the given place in the frame: the scene framebuffer is made or remade to
	// fit, pushed as the target, and the viewport moved so that the view lands at its origin, as the
	// bloom filter does for its layer. The target's size and the projection stay in the frame's pixels.
	fun beginScene(x: Int, y: Int, width: Int, height: Int) {
		if (!this.sceneAvailable || this.sceneActive || width <= 0 || height <= 0) {
			return
		}
		val samples: Int = this.sceneSamples()
		val scale: Double = ClientFeatures.renderScale
		val targetWidth: Int = kotlin.math.floor(width.toDouble() * scale + 0.5).toInt()
		val targetHeight: Int = kotlin.math.floor(height.toDouble() * scale + 0.5).toInt()
		if (this.sceneTarget == null || this.sceneTarget!!.width != targetWidth || this.sceneTarget!!.height != targetHeight || this.sceneTarget!!.samples != samples) {
			this.dropSceneTarget()
			val target: GlSceneTarget = GlSceneTarget(this, targetWidth, targetHeight, samples, true)
			this.rebindTargets()
			if (!target.complete) {
				target.release()
				this.rebindTargets()
				lang.printlnErr("client.msaa: the scene framebuffer is incomplete on this OpenGL driver; the 3D view is drawn in the frame")
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
	fun endScene() {
		if (!this.sceneActive) {
			return
		}
		this.setSceneAlphaToCoverage(false)
		this.popTarget(this.sceneTarget!!.target())
		this.sceneActive = false
		this.setViewportOffset(this.sceneSavedOffsetY, this.sceneSavedOffsetX)
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST)
		if (this.sceneTarget!!.multisample != null) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.sceneTarget!!.multisample!!.framebufferId())
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.sceneTarget!!.resolve.framebufferId())
			OpenGL.glBlitFramebufferEXT(0, 0, this.sceneTarget!!.width, this.sceneTarget!!.height, 0, 0, this.sceneTarget!!.width, this.sceneTarget!!.height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST)
			this.rebindTargets()
		}
		this.setupSpriteRendering()
		this.setBlendMode(0)
		var view: OpenGl2DTexture? = this.sceneTarget!!.colour
		if (ClientFeatures.smaa) {
			if (this.smaaFilter == null) {
				this.smaaFilter = GlSmaaFilter(this)
			}
			if (this.smaaFilter!!.available) {
				this.smaaFilter!!.apply(view, this.sceneTarget!!.width, this.sceneTarget!!.height)
				view = this.smaaFilter!!.output
			}
		}
		this.setColourOp(1)
		this.activateTexture(0)
		this.setActiveTexture(view)
		OpenGL.glColor4ub((-1).toByte(), (-1).toByte(), (-1).toByte(), (-1).toByte())
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		// Wound as the sprites are, since back faces stay culled in 2D.
		OpenGL.glTexCoord2f(0.0f, 1.0f)
		OpenGL.glVertex2i(this.sceneX, this.sceneY)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2i(this.sceneX, this.sceneY + this.sceneHeight)
		OpenGL.glTexCoord2f(1.0f, 0.0f)
		OpenGL.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY + this.sceneHeight)
		OpenGL.glTexCoord2f(1.0f, 1.0f)
		OpenGL.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY)
		OpenGL.glEnd()
		this.setActiveTexture(null)
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST)
		this.defineScissorBox()
	}

	// Binds the read and draw framebuffers the target stacks say are current, after a bind of our own.
	private fun rebindTargets() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, if (this.drawTargetIndex >= 0) (this.drawTargetStack[this.drawTargetIndex] as GlFramebuffer).framebufferId() else this.defaultFramebuffer())
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, if (this.readTargetIndex >= 0) (this.readTargetStack[this.readTargetIndex] as GlFramebuffer).framebufferId() else this.defaultFramebuffer())
	}

	// Lets go of the scene framebuffer; it is made again on the next frame if still wanted.
	fun dropSceneTarget() {
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
			lang.printlnErr("client.scale: this OpenGL driver has no framebuffer objects, so the game is drawn at its own size")
			ClientFeatures.scaleUnavailable = true
			wanted = false
		}
		if (!wanted) {
			this.dropScaledTarget()
			return
		}
		if (this.scaledTarget != null && this.scaledWidth == this.width && this.scaledHeight == this.height) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer())
			return
		}
		this.dropScaledTarget()
		val target: GlFramebuffer = GlFramebuffer(this)
		val colour: OpenGl2DTexture = OpenGl2DTexture(this, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, this.width, this.height)
		val depth: OpenGlRenderBuffer = OpenGlRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height)
		target.bind()
		target.attachTexture(colour, 0)
		target.attachRenderbuffer(8, depth)
		target.drawBuffer(0)
		if (!target.isFramebufferComplete()) {
			target.clearAttachment(0)
			target.clearAttachment(8)
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0)
			lang.printlnErr("client.scale: the scaled framebuffer is incomplete on this OpenGL driver, so the game is drawn at its own size")
			ClientFeatures.scaleUnavailable = true
			return
		}
		this.scaledTarget = target
		this.scaledColour = colour
		this.scaledDepth = depth
		this.scaledWidth = this.width
		this.scaledHeight = this.height
		if (this.multiSamplesPreference > 1 && this.framebufferMultisample) {
			val multisample: GlFramebuffer = GlFramebuffer(this)
			val multisampleColour: OpenGlRenderBuffer = OpenGlRenderBuffer(this, OpenGLConstants.GL_RGBA, this.width, this.height, this.multiSamplesPreference)
			val multisampleDepth: OpenGlRenderBuffer = OpenGlRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height, this.multiSamplesPreference)
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
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, target.framebufferId())
				lang.printlnErr("client.scale: no multisampled framebuffer on this OpenGL driver, so the scaled frame has no anti-aliasing")
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
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledMultisample!!.framebufferId())
			this.scaledMultisample!!.clearAttachment(0)
			this.scaledMultisample!!.clearAttachment(8)
			this.scaledMultisampleColour!!.dispose()
			this.scaledMultisampleDepth!!.dispose()
			this.scaledMultisample = null
			this.scaledMultisampleColour = null
			this.scaledMultisampleDepth = null
		}
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledTarget!!.framebufferId())
		this.scaledTarget!!.clearAttachment(0)
		this.scaledTarget!!.clearAttachment(8)
		this.scaledColour!!.delete()
		this.scaledDepth!!.dispose()
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0)
		this.scaledTarget = null
		this.scaledColour = null
		this.scaledDepth = null
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(II)V")
	override fun f(@OriginalArg(0) near: Int, @OriginalArg(1) far: Int) {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIII)V")
	override fun method7261(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
		this.framebufferCompositor!!.prepareForRendering(width, x, height, y)
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(ILclient!tv;)V")
	fun popReadTarget(@OriginalArg(1) target: GlRenderTarget?) {
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

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(I)V")
	override fun allocateThreads(@OriginalArg(0) threadCount: Int) {
		if (threadCount != 1) {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(II)Lclient!he;")
	override fun method7268(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): Sprite? {
		return null
	}

	@OriginalMember(owner = "client!qo", name = "D", descriptor = "(I)V")
	private fun updateProjectionMatrix() {
		@Pc(6) val matrix: FloatArray = this.projectionMatrix
		@Pc(18) val left: Float = (this.zNear * -this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		@Pc(32) val right: Float = ((this.viewportWidth - this.projectionCenterX) * this.zNear).toFloat() / this.projectionScaleX.toFloat()
		@Pc(49) val top: Float = (this.zNear * this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		@Pc(64) val bottom: Float = ((this.projectionCenterY - this.viewportHeight) * this.zNear).toFloat() / this.projectionScaleY.toFloat()
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
			@Pc(144) val twoNear: Float = this.zNear.toFloat() * 2.0f
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

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "()V")
	override fun finish() {
		OpenGL.glFinish()
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	override fun generateSprite(@OriginalArg(0) indexedImage: SpriteType?, @OriginalArg(1) paletteSprite: Boolean): NativeSprite {
		@Pc(12) val pixels: IntArray = IntArray(indexedImage!!.width * indexedImage.height)
		@Pc(14) var srcIndex: Int = 0
		@Pc(16) var dstIndex: Int = 0
		@Pc(21) var y: Int
		@Pc(25) var x: Int
		if (indexedImage.alpha == null) {
			y = 0
			while (y < indexedImage.height) {
				x = 0
				while (x < indexedImage.width) {
					@Pc(83) val colour: Int = indexedImage.pallete!![indexedImage.pixels!![srcIndex++].toInt() and 0xFF]
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
		@Pc(120) val sprite: NativeSprite? = this.createNativeSprite(indexedImage.width, pixels, indexedImage.width, indexedImage.height)
		sprite!!.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY)
		return sprite
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(ZI)V")
	fun toggleFog(@OriginalArg(0) enableFog: Boolean) {
		if (enableFog != this.fogEnabled) {
			this.fogEnabled = enableFog
			this.toggleFog()
			this.functionMask = this.functionMask and 0xFFFFFFE0.toInt()
		}
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "(II)V")
	fun setTextureEnvColour(@OriginalArg(0) colour: Int) {
		OpenGlToolkit.BUFFER_4F[2] = (colour and 0xFF).toFloat() / 255.0f
		OpenGlToolkit.BUFFER_4F[3] = (colour ushr 24).toFloat() / 255.0f
		OpenGlToolkit.BUFFER_4F[1] = (colour and 0xFF00).toFloat() / 65280.0f
		OpenGlToolkit.BUFFER_4F[0] = (colour and 0xFF0000).toFloat() / 1.671168E7f
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0)
	}

	@OriginalMember(owner = "client!qo", name = "na", descriptor = "(IIII)[I")
	override fun na(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int): IntArray {
		@Pc(10) val pixels: IntArray = IntArray(width * height)
		this.beginReadFrame(x, this.viewportHeight - y - height, width, height) // Not part of the original client: see beginReadFrame.
		for (@Pc(12) i in 0 until height) {
			OpenGL.glReadPixelsi(x, this.viewportHeight - y - i, width, 1, OpenGLConstants.GL_BGRA, this.pixelType, pixels, width * i)
		}
		this.endReadFrame()
		return pixels
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(Z)V")
	private fun updateViewport() {
		if (this.sceneScaling()) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			OpenGL.glViewport(this.sceneScaled(this.viewportOffsetX), this.sceneScaled(this.viewportOffsetY), this.sceneScaled(this.viewportWidth), this.sceneScaled(this.viewportHeight))
			return
		}
		OpenGL.glViewport(this.viewportOffsetX, this.viewportOffsetY, this.viewportWidth, this.viewportHeight)
	}

	@OriginalMember(owner = "client!qo", name = "w", descriptor = "()Z")
	override fun supportsAntiAliasing(): Boolean {
		return this.arbMultisample && (!this.bloom() || this.advancedFramebuffers)
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(III)V")
	@Synchronized
	fun requestRenderBufferCleanup(@OriginalArg(0) handle: Int, @OriginalArg(1) bufferSize: Int) {
		@Pc(12) val intNode: IntNode = IntNode(bufferSize)
		intNode.key = handle.toLong()
		this.renderBuffersAwaitingRemoval.addLast(intNode)
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(B)V")
	private fun defineScissorBox() {
		if (this.sceneScaling() && this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			val x0: Int = this.sceneScaled(this.viewportOffsetX + this.clipLeft)
			val y0: Int = this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipBottom)
			OpenGL.glScissor(x0, y0, this.sceneScaled(this.viewportOffsetX + this.clipRight) - x0, this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipTop) - y0)
			return
		}
		if (this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			OpenGL.glScissor(this.viewportOffsetX + this.clipLeft, this.viewportOffsetY + this.viewportHeight - this.clipBottom, this.clipRight - this.clipLeft, -this.clipTop + this.clipBottom)
		} else {
			OpenGL.glScissor(0, 0, 0, 0)
		}
	}

	@OriginalMember(owner = "client!qo", name = "z", descriptor = "()Z")
	override fun method7311(): Boolean {
		return false
	}

	companion object {

		@OriginalMember(owner = "client!qo", name = "lb", descriptor = "Lclient!ej;")
		@JvmField
		val SOUND_EFFECT_MUSIC_ENDED: ClientPacket = ClientPacket(52, 4)

		private const val GL_DEPTH_COMPONENT: Int = 6402

		@OriginalMember(owner = "client!jfa", name = "g", descriptor = "I")
		const val MAX_LIGHTS: Int = 4

		@OriginalMember(owner = "client!rl", name = "g", descriptor = "[I")
		@JvmField
		val deleteIdBuffer: IntArray = IntArray(1000)

		@OriginalMember(owner = "client!uh", name = "a", descriptor = "(IILjava/awt/Canvas;Lclient!d;)Lclient!ha;")
		@JvmStatic
		fun create(@OriginalArg(1) multiSamples: Int, @OriginalArg(2) canvas: Canvas?, @OriginalArg(3) textureSource: MaterialProvider?): Toolkit {
			return GlToolkit(canvas, textureSource, multiSamples)
		}
	}
}
