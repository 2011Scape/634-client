package com.jagex.game.runetek5.toolkit

import com.jagex.game.runetek5.config.material.NativeMaterialCache
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.BlendMode
import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.MatrixMode
import com.jagex.game.runetek5.TextureCombineFunction
import com.jagex.game.runetek5.TexCoordGenMode
import com.jagex.game.runetek5.TextureTransformMode
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.VertexAttribute
import com.jagex.game.runetek5.NativeCubeTextureGenerator
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.FixedFunctionEnvironmentMapPass
import com.jagex.game.runetek5.FixedFunctionSpecularPass
import com.jagex.game.runetek5.FixedFunctionWaterPass
import com.jagex.game.runetek5.graphics.font.NativeFont
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.CubeTextureSampler
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.NoEffectPass
import com.jagex.game.runetek5.cache.node.GpuPointLight
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.RenderPass
import com.jagex.game.runetek5.graphics.sprite.GpuSprite
import com.jagex.game.runetek5.UnlitPass
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaclib.memory.Buffer
import jaclib.memory.Stream
import jaclib.memory.heap.NativeHeap
import jaclib.memory.heap.NativeHeapBuffer
import jagex3.graphics2.hw.NativeInterface
import java.awt.Canvas
import java.awt.Dimension
import java.lang.reflect.Method
import java.util.Enumeration
import java.util.Hashtable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.NativeMemoryPool
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.model.GpuModel
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.scene.ground.GpuFloorModel
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import com.jagex.game.runetek5.graphics.GpuEnvironmentSampler
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.graphics.texture.TextureState
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode
import com.jagex.game.runetek5.toolkit.companion.PrimitiveDrawMode
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction
import com.jagex.game.runetek5.utils.ColorUtil

@OriginalClass("client!wga")
abstract class AbstractNativeToolkit : Toolkit {

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	@JvmField
	var nativeHeap: NativeHeap? = null

	@OriginalMember(owner = "client!wga", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!wga", name = "xb", descriptor = "Lclient!ur;")
	@JvmField
	var waterTexture: NativeWaterTexture? = null

	@OriginalMember(owner = "client!wga", name = "ic", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	@JvmField
	var stagingBuffer: NativeHeapBuffer? = null

	@OriginalMember(owner = "client!wga", name = "Wc", descriptor = "Ljava/util/Hashtable;")
	private var surfacesByCanvas: Hashtable<Any?, Any?>? = null

	@OriginalMember(owner = "client!wga", name = "Sd", descriptor = "I")
	@JvmField
	var texturesSizeOccupied: Int = 0

	@OriginalMember(owner = "client!wga", name = "Td", descriptor = "I")
	@JvmField
	var textureBuffersSizeOccupied: Int = 0

	@OriginalMember(owner = "client!wga", name = "je", descriptor = "I")
	@JvmField
	var renderBufferSizeOccupied: Int = 0

	@OriginalMember(owner = "client!wga", name = "qe", descriptor = "[Lclient!sia;")
	@JvmField
	var textureRgbCombineModes: Array<TextureCombineFunction?>? = null

	@OriginalMember(owner = "client!wga", name = "ue", descriptor = "I")
	@JvmField
	var maxConcurrentTextures: Int = 0

	@OriginalMember(owner = "client!wga", name = "we", descriptor = "[Lclient!sia;")
	@JvmField
	var textureAlphaCombineModes: Array<TextureCombineFunction?>? = null

	@OriginalMember(owner = "client!wga", name = "Ce", descriptor = "[Lclient!du;")
	private var textureSamplers: Array<TextureSampler?>? = null

	@OriginalMember(owner = "client!wga", name = "Ee", descriptor = "I")
	private var nativeFunctionMask: Int = 0

	@OriginalMember(owner = "client!wga", name = "Fe", descriptor = "Z")
	@JvmField
	var cubeMapSupported: Boolean = false

	@OriginalMember(owner = "client!wga", name = "He", descriptor = "F")
	@JvmField
	var viewY1: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "Re", descriptor = "[Lclient!tr;")
	@JvmField
	var textureStates: Array<TextureTransformMode?>? = null

	@OriginalMember(owner = "client!wga", name = "Se", descriptor = "I")
	@JvmField
	var shadowOffsetZ: Int = 0

	@OriginalMember(owner = "client!wga", name = "Te", descriptor = "F")
	@JvmField
	var fogEnd: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "Xe", descriptor = "I")
	@JvmField
	var envColor: Int = 0

	@OriginalMember(owner = "client!wga", name = "af", descriptor = "I")
	@JvmField
	var currentSceneLightCount: Int = 0

	@OriginalMember(owner = "client!wga", name = "bf", descriptor = "Z")
	@JvmField
	var fogEnabled: Boolean = false

	@OriginalMember(owner = "client!wga", name = "ef", descriptor = "F")
	@JvmField
	var viewX1: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "gf", descriptor = "F")
	private var perspectiveDepthOffset: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "kf", descriptor = "[Lclient!haa;")
	@JvmField
	var textureMatrices: Array<NativeMatrix?>? = null

	@OriginalMember(owner = "client!wga", name = "lf", descriptor = "[Lclient!ab;")
	@JvmField
	var lightSources: Array<PointLight?>? = null

	@OriginalMember(owner = "client!wga", name = "mf", descriptor = "I")
	@JvmField
	var shadowOffsetX: Int = 0

	@OriginalMember(owner = "client!wga", name = "pf", descriptor = "Lclient!ve;")
	private var particleRenderer: NativeParticleRenderer? = null

	@OriginalMember(owner = "client!wga", name = "rf", descriptor = "I")
	@JvmField
	var maxActiveLights: Int = 0

	@OriginalMember(owner = "client!wga", name = "wf", descriptor = "Lclient!fj;")
	private var activeWaterShader: RenderPass? = null

	@OriginalMember(owner = "client!wga", name = "zf", descriptor = "I")
	@JvmField
	var animatingCycle: Int = 0

	@OriginalMember(owner = "client!wga", name = "Af", descriptor = "Lclient!du;")
	@JvmField
	var defaultTexture: TextureSampler? = null

	@OriginalMember(owner = "client!wga", name = "Ef", descriptor = "I")
	@JvmField
	var lastSceneLightCount: Int = 0

	@OriginalMember(owner = "client!wga", name = "Lf", descriptor = "F")
	@JvmField
	var viewX2: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "Mf", descriptor = "Z")
	@JvmField
	var SUPPORTS_TEXTURE3D: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Nf", descriptor = "Z")
	@JvmField
	var missingWaterColor: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Pf", descriptor = "I")
	private var waterHeight: Int = 0

	@OriginalMember(owner = "client!wga", name = "Tf", descriptor = "F")
	private var perspectiveDepthScale: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "Yf", descriptor = "Lclient!vc;")
	private var sunCubeTextureGenerator: GpuEnvironmentSampler? = null

	@OriginalMember(owner = "client!wga", name = "ag", descriptor = "F")
	@JvmField
	var fogStart: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "jg", descriptor = "Z")
	@JvmField
	var GLARBMultisample: Boolean = false

	@OriginalMember(owner = "client!wga", name = "mg", descriptor = "F")
	@JvmField
	var viewY2: Float = 0.0f

	@OriginalMember(owner = "client!wga", name = "og", descriptor = "Lclient!nca;")
	@JvmField
	var copyType3Scratch: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "pg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType4Result: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "qg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType5Scratch: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "rg", descriptor = "Lclient!np;")
	private var circleVertexDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "sg", descriptor = "Lclient!np;")
	@JvmField
	var positionColorTexCoordDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "tg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType2Result: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "ug", descriptor = "Lclient!tc;")
	private var quadVertexStream: NativeStream? = null

	@OriginalMember(owner = "client!wga", name = "vg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType2Scratch: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "wg", descriptor = "Lclient!uq;")
	private var indexBuffer: GpuIndexBuffer? = null

	@OriginalMember(owner = "client!wga", name = "yg", descriptor = "Lclient!np;")
	private var quadVertexDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "zg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType1Scratch: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "Ag", descriptor = "Lclient!nca;")
	@JvmField
	var copyType4Scratch: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "Bg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType3Result: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "Cg", descriptor = "Lclient!tc;")
	private var circleVertexStream: NativeStream? = null

	@OriginalMember(owner = "client!wga", name = "Dg", descriptor = "Lclient!np;")
	@JvmField
	var positionColorDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "Eg", descriptor = "Lclient!tc;")
	private var vertexStream: NativeStream? = null

	@OriginalMember(owner = "client!wga", name = "Fg", descriptor = "Lclient!np;")
	@JvmField
	var positionTexCoordDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "Gg", descriptor = "Lclient!np;")
	private var vertexDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "Hg", descriptor = "Lclient!nca;")
	@JvmField
	var copyType1Result: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "Ig", descriptor = "Lclient!nca;")
	@JvmField
	var copyType5Result: GpuModel? = null

	@OriginalMember(owner = "client!wga", name = "Jg", descriptor = "Lclient!np;")
	@JvmField
	var positionColorTexCoordNormalDeclaration: VertexDeclaration? = null

	@OriginalMember(owner = "client!wga", name = "Kg", descriptor = "Z")
	@JvmField
	var boundsCalculated: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Lg", descriptor = "I")
	private var modelCount: Int = 0

	@OriginalMember(owner = "client!wga", name = "nb", descriptor = "Lclient!uh;")
	private val activeNativeMemory: Deque = Deque()

	@OriginalMember(owner = "client!wga", name = "Zd", descriptor = "Z")
	@JvmField
	var isModelMatrixIdentity: Boolean = true

	@OriginalMember(owner = "client!wga", name = "ee", descriptor = "Lclient!haa;")
	@JvmField
	val modelMatrix43: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "ke", descriptor = "Lclient!haa;")
	@JvmField
	var viewMatrix43: NativeMatrix? = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "me", descriptor = "Lclient!haa;")
	@JvmField
	val inverseSunMatrix43: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "ne", descriptor = "Lclient!haa;")
	@JvmField
	val flippedViewMatrix: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "oe", descriptor = "Lclient!haa;")
	private val inverseSunMatrix44: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "pe", descriptor = "Lclient!haa;")
	private val combinedModelViewMatrix43: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "xe", descriptor = "F")
	@JvmField
	var intensity: Float = 1.0f

	@OriginalMember(owner = "client!wga", name = "De", descriptor = "I")
	private var waterColour: Int = -1

	@OriginalMember(owner = "client!wga", name = "te", descriptor = "F")
	@JvmField
	var orthoFar: Float = 3584.0f

	@OriginalMember(owner = "client!wga", name = "ze", descriptor = "I")
	@JvmField
	var camNear: Int = 50

	@OriginalMember(owner = "client!wga", name = "ye", descriptor = "I")
	@JvmField
	var projectionCenterX: Int = 0

	@OriginalMember(owner = "client!wga", name = "re", descriptor = "F")
	@JvmField
	var baseLightColorGreen: Float = 1.0f

	@OriginalMember(owner = "client!wga", name = "Qe", descriptor = "Z")
	private var isOrthoSceneProjectionValid: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Ae", descriptor = "I")
	@JvmField
	var cBottom: Int = 0

	@OriginalMember(owner = "client!wga", name = "Je", descriptor = "I")
	@JvmField
	var fogOffset: Int = 0

	@OriginalMember(owner = "client!wga", name = "hf", descriptor = "I")
	@JvmField
	var projectionScaleX: Int = 512

	@OriginalMember(owner = "client!wga", name = "We", descriptor = "Z")
	@JvmField
	var depthMaskEnabled: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Me", descriptor = "I")
	private var activeEffectParam2: Int = 0

	@OriginalMember(owner = "client!wga", name = "ve", descriptor = "I")
	@JvmField
	var fogDistance: Int = -1

	@OriginalMember(owner = "client!wga", name = "Ze", descriptor = "Z")
	private var viewPositionCalibrated: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Ue", descriptor = "Z")
	@JvmField
	var enableBlending: Boolean = true

	@OriginalMember(owner = "client!wga", name = "df", descriptor = "I")
	@JvmField
	var activeFlags: Int = 8

	@OriginalMember(owner = "client!wga", name = "Ge", descriptor = "[F")
	@JvmField
	val lightPosition: FloatArray = floatArrayOf(0.0f, 0.0f, -1.0f, 0.0f)

	@OriginalMember(owner = "client!wga", name = "vf", descriptor = "[F")
	private val baseLightPosition: FloatArray = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f)

	@OriginalMember(owner = "client!wga", name = "Pe", descriptor = "[F")
	private val orthoProjectionMatrix: FloatArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)

	@OriginalMember(owner = "client!wga", name = "jf", descriptor = "I")
	@JvmField
	var projectionCenterY: Int = 0

	@OriginalMember(owner = "client!wga", name = "Kf", descriptor = "[F")
	private val orthoSceneProjectionMatrix: FloatArray = FloatArray(16)

	@OriginalMember(owner = "client!wga", name = "Oe", descriptor = "Z")
	@JvmField
	var sunEnabled: Boolean = true

	@OriginalMember(owner = "client!wga", name = "Ne", descriptor = "I")
	@JvmField
	val viewportOffsetY: Int = 0

	@OriginalMember(owner = "client!wga", name = "Ye", descriptor = "[F")
	private val screenOrthoMatrix: FloatArray = FloatArray(16)

	@OriginalMember(owner = "client!wga", name = "Ie", descriptor = "I")
	private var activeMaterialId: Int = -1

	@OriginalMember(owner = "client!wga", name = "cf", descriptor = "Z")
	@JvmField
	var depthBufferingEnabled: Boolean = true

	@OriginalMember(owner = "client!wga", name = "ff", descriptor = "Z")
	private var isOrthoProjectionValid: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Jf", descriptor = "I")
	private var activeEffectType: Int = 0

	@OriginalMember(owner = "client!wga", name = "Gf", descriptor = "Z")
	private var activeMaterialUnderwater: Boolean = false

	@OriginalMember(owner = "client!wga", name = "xf", descriptor = "I")
	@JvmField
	var fogColorRgb: Int = -1

	@OriginalMember(owner = "client!wga", name = "Hf", descriptor = "I")
	@JvmField
	var camFar: Int = 3584

	@OriginalMember(owner = "client!wga", name = "Le", descriptor = "I")
	@JvmField
	var shadowSizeShift: Int = 3

	@OriginalMember(owner = "client!wga", name = "sf", descriptor = "[Lclient!fj;")
	private val waterShadersByEffect: Array<RenderPass?> = arrayOfNulls<RenderPass>(10)

	@OriginalMember(owner = "client!wga", name = "Df", descriptor = "I")
	@JvmField
	var size: Int = 128

	@OriginalMember(owner = "client!wga", name = "nf", descriptor = "I")
	@JvmField
	var projectionScaleY: Int = 512

	@OriginalMember(owner = "client!wga", name = "Rf", descriptor = "Z")
	@JvmField
	var enableDepthTest: Boolean = false

	@OriginalMember(owner = "client!wga", name = "tf", descriptor = "[F")
	private val baseLightInvertedPosition: FloatArray = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f)

	@OriginalMember(owner = "client!wga", name = "Wf", descriptor = "F")
	@JvmField
	var perspectiveFar: Float = 3584.0f

	@OriginalMember(owner = "client!wga", name = "uf", descriptor = "Z")
	@JvmField
	var enableAlphaTest: Boolean = true

	@OriginalMember(owner = "client!wga", name = "Qf", descriptor = "Lclient!pn;")
	@JvmField
	var blendFuncMode: BlendMode? = BlendFuncMode.ALPHA_BLENDING

	@OriginalMember(owner = "client!wga", name = "Bf", descriptor = "Z")
	@JvmField
	var fogSupported: Boolean = true

	@OriginalMember(owner = "client!wga", name = "Be", descriptor = "Z")
	private var isModelViewValid: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Sf", descriptor = "I")
	@JvmField
	var cTop: Int = 0

	@OriginalMember(owner = "client!wga", name = "yf", descriptor = "Z")
	@JvmField
	var lightingEnabled: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Ff", descriptor = "[F")
	private val perspectiveProjectionMatrix: FloatArray = FloatArray(16)

	@OriginalMember(owner = "client!wga", name = "dg", descriptor = "Z")
	private var isShutdown: Boolean = false

	@OriginalMember(owner = "client!wga", name = "Of", descriptor = "I")
	private var activeEffectParam1: Int = 0

	@OriginalMember(owner = "client!wga", name = "Ke", descriptor = "I")
	@JvmField
	var cLeft: Int = 0

	@OriginalMember(owner = "client!wga", name = "Cf", descriptor = "Z")
	@JvmField
	var forceLightsDisabled: Boolean = false

	@OriginalMember(owner = "client!wga", name = "If", descriptor = "F")
	private var orthoZoom: Float = 1.0f

	@OriginalMember(owner = "client!wga", name = "of", descriptor = "[F")
	@JvmField
	var activeProjectionSource: FloatArray? = this.orthoProjectionMatrix

	@OriginalMember(owner = "client!wga", name = "Uf", descriptor = "I")
	private var blendFuncType: Int = 1

	@OriginalMember(owner = "client!wga", name = "gg", descriptor = "I")
	private var waterScale: Int = -1

	@OriginalMember(owner = "client!wga", name = "cg", descriptor = "I")
	@JvmField
	var currentTextureSlot: Int = 0

	@OriginalMember(owner = "client!wga", name = "eg", descriptor = "I")
	private var waterIntensity: Int = 0

	@OriginalMember(owner = "client!wga", name = "Vf", descriptor = "F")
	@JvmField
	var baseLightColorBlue: Float = 1.0f

	@OriginalMember(owner = "client!wga", name = "bg", descriptor = "F")
	@JvmField
	var sunColorIntensity: Float = -1.0f

	@OriginalMember(owner = "client!wga", name = "Xf", descriptor = "[F")
	@JvmField
	val sunPosition: FloatArray = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f)

	@OriginalMember(owner = "client!wga", name = "ig", descriptor = "I")
	@JvmField
	val viewportOffsetX: Int = 0

	@OriginalMember(owner = "client!wga", name = "lg", descriptor = "Lclient!qv;")
	@JvmField
	var matrixMode: MatrixMode? = MatrixMode.aClass286_7

	@OriginalMember(owner = "client!wga", name = "hg", descriptor = "F")
	@JvmField
	var baseLightColorRed: Float = 1.0f

	@OriginalMember(owner = "client!wga", name = "ng", descriptor = "F")
	@JvmField
	var antiSunColorIntensity: Float = -1.0f

	@OriginalMember(owner = "client!wga", name = "Zf", descriptor = "I")
	private var baseLightColor: Int = 16777215

	@OriginalMember(owner = "client!wga", name = "kg", descriptor = "I")
	@JvmField
	var cRight: Int = 0

	@OriginalMember(owner = "client!wga", name = "se", descriptor = "Lclient!jaclib/memory/Stream;")
	private val scratchStream: Stream = Stream()

	@OriginalMember(owner = "client!wga", name = "xg", descriptor = "Lclient!haa;")
	private val scratchMatrix: NativeMatrix = NativeMatrix()

	@OriginalMember(owner = "client!wga", name = "Ve", descriptor = "I")
	@JvmField
	val multiSampleCount: Int

	@OriginalMember(owner = "client!wga", name = "vd", descriptor = "Lclient!in;")
	@JvmField
	val shaderResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!wga", name = "lb", descriptor = "Ljava/awt/Canvas;")
	@JvmField
	val lb: Canvas?

	@OriginalMember(owner = "client!wga", name = "W", descriptor = "Ljava/awt/Canvas;")
	private var currentCanvas: Canvas? = null

	@OriginalMember(owner = "client!wga", name = "fb", descriptor = "Ljava/lang/Object;")
	@JvmField
	val primarySurface: Any?

	@OriginalMember(owner = "client!wga", name = "cd", descriptor = "Ljava/lang/Object;")
	private var currentSurface: Any? = null

	@OriginalMember(owner = "client!wga", name = "vc", descriptor = "I")
	private var height: Int = 0

	@OriginalMember(owner = "client!wga", name = "Wb", descriptor = "I")
	@JvmField
	var surfaceHeight: Int = 0

	@OriginalMember(owner = "client!wga", name = "Sb", descriptor = "I")
	private var width: Int = 0

	@OriginalMember(owner = "client!wga", name = "rb", descriptor = "I")
	@JvmField
	var surfaceWidth: Int = 0

	@OriginalMember(owner = "client!wga", name = "fg", descriptor = "I")
	@JvmField
	val endiannessType: Int

	@OriginalMember(owner = "client!wga", name = "kb", descriptor = "Lclient!jagex3/graphics2/hw/NativeInterface;")
	@JvmField
	val nativeInterface: NativeInterface

	@OriginalMember(owner = "client!wga", name = "qf", descriptor = "Lclient!fw;")
	private val materialCache: NativeMaterialCache?

	@OriginalMember(owner = "client!wga", name = "<init>", descriptor = "(Ljava/awt/Canvas;Ljava/lang/Object;Lclient!d;Lclient!in;II)V")
	constructor(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) `object`: Any?, @OriginalArg(2) textureSource: MaterialProvider?, @OriginalArg(3) shaderResource: Js5ResourceProvider?, @OriginalArg(4) multiSampleCount: Int, @OriginalArg(5) endiannessType: Int) : super(textureSource) {
		try {
			this.multiSampleCount = multiSampleCount
			this.shaderResource = shaderResource
			this.lb = canvas
			this.currentCanvas = this.lb
			this.primarySurface = `object`
			this.currentSurface = this.primarySurface
			@Pc(307) val dimension: Dimension = canvas!!.getSize()
			this.height = dimension.height
			this.surfaceHeight = this.height
			this.width = dimension.width
			this.surfaceWidth = this.width
			this.endiannessType = endiannessType
			ColorUtil.initColorTables(false, true)
			if (super.materialProvider == null) {
				this.nativeInterface = NativeInterface(0, this.endiannessType)
				this.materialCache = null
			} else {
				this.materialCache = NativeMaterialCache(this, super.materialProvider)
				this.nativeInterface = NativeInterface(super.materialProvider!!.textureCount(), this.endiannessType)
				@Pc(364) var i: Int = 0
				while (super.materialProvider!!.textureCount() > i) {
					@Pc(372) val textureMetrics: MaterialType? = super.materialProvider!!.getMetrics(i)
					if (textureMetrics != null) {
						this.nativeInterface.initTextureMetrics(i, textureMetrics.alpha, textureMetrics.brightness)
					}
					i++
				}
			}
		} catch (@Pc(391) ex: Throwable) {
			ex.printStackTrace()
			this.free()
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!wga", name = "da", descriptor = "(III[I)V")
	final override fun da(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?) {
		@Pc(14) val depth: Float = this.viewMatrix43!!.transformZ(x.toFloat(), z.toFloat(), y.toFloat())
		@Pc(40) val screenX: Int
		@Pc(58) val screenY: Int
		if (depth < -0.0078125f || depth > 0.0078125f) {
			screenX = (this.projectionScaleX.toFloat() * this.viewMatrix43!!.transformX(z.toFloat(), y.toFloat(), x.toFloat()) / depth).toInt()
			screenY = (this.projectionScaleY.toFloat() * this.viewMatrix43!!.transformY(x.toFloat(), z.toFloat(), y.toFloat()) / depth).toInt()
		} else {
			screenY = this.projectionCenterY
			screenX = this.projectionCenterX
		}
		screenCoords!![2] = depth.toInt()
		screenCoords[0] = (screenX.toFloat() - this.viewX1).toInt()
		screenCoords[1] = (screenY.toFloat() - this.viewY1).toInt()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!za;)V")
	final override fun setHeap(@OriginalArg(0) memoryNode: MemoryPool?) {
		this.nativeHeap = (memoryNode as NativeMemoryPool?)!!.heap
		this.stagingBuffer = this.nativeHeap!!.a(32768, false)
	}

	@OriginalMember(owner = "client!wga", name = "JA", descriptor = "(IIIIII)I")
	final override fun JA(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int): Int {
		val viewMatrix43: NativeMatrix = this.viewMatrix43!!
		@Pc(7) var clipFlags: Int = 0
		@Pc(32) var startDepth: Float = y1.toFloat() * viewMatrix43.zB + viewMatrix43.zA * x1.toFloat() + z1.toFloat() * viewMatrix43.zC + viewMatrix43.zD
		if (startDepth < 1.0f) {
			startDepth = 1.0f
		}
		@Pc(63) var endDepth: Float = viewMatrix43.zD + viewMatrix43.zB * y2.toFloat() + viewMatrix43.zA * x2.toFloat() + z2.toFloat() * viewMatrix43.zC
		if (endDepth < 1.0f) {
			endDepth = 1.0f
		}
		if (this.camNear.toFloat() > startDepth && this.camNear.toFloat() > endDepth) {
			clipFlags = clipFlags or 0x10
		} else if (startDepth > this.camFar.toFloat() && this.camFar.toFloat() < endDepth) {
			clipFlags = clipFlags or 0x20
		}
		@Pc(135) val startScreenX: Int = ((viewMatrix43.xD + z1.toFloat() * viewMatrix43.xC + x1.toFloat() * viewMatrix43.xA + viewMatrix43.xB * y1.toFloat()) * this.projectionScaleX.toFloat() / startDepth).toInt()
		@Pc(167) val endScreenX: Int = ((viewMatrix43.xD + viewMatrix43.xB * y2.toFloat() + x2.toFloat() * viewMatrix43.xA + z2.toFloat() * viewMatrix43.xC) * this.projectionScaleX.toFloat() / endDepth).toInt()
		if (startScreenX.toFloat() < this.viewX1 && endScreenX.toFloat() < this.viewX1) {
			clipFlags = clipFlags or 0x1
		} else if (this.viewX2 < startScreenX.toFloat() && endScreenX.toFloat() > this.viewX2) {
			clipFlags = clipFlags or 0x2
		}
		@Pc(233) val startScreenY: Int = (this.projectionScaleY.toFloat() * (viewMatrix43.yC * z1.toFloat() + viewMatrix43.yB * y1.toFloat() + x1.toFloat() * viewMatrix43.yA + viewMatrix43.yD) / startDepth).toInt()
		@Pc(265) val endScreenY: Int = (this.projectionScaleY.toFloat() * (z2.toFloat() * viewMatrix43.yC + y2.toFloat() * viewMatrix43.yB + x2.toFloat() * viewMatrix43.yA + viewMatrix43.yD) / endDepth).toInt()
		if (this.viewY1 > startScreenY.toFloat() && endScreenY.toFloat() < this.viewY1) {
			clipFlags = clipFlags or 0x4
		} else if (startScreenY.toFloat() > this.viewY2 && endScreenY.toFloat() > this.viewY2) {
			clipFlags = clipFlags or 0x8
		}
		return clipFlags
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	abstract fun createVertexDeclaration(@OriginalArg(1) components: Array<VertexDeclarationComponent?>?): VertexDeclaration?

	@OriginalMember(owner = "client!wga", name = "ZA", descriptor = "(IFFFFF)V")
	final override fun ZA(@OriginalArg(0) baseLightColor: Int, @OriginalArg(1) sunIntensity: Float, @OriginalArg(2) antiSunIntensity: Float, @OriginalArg(3) x: Float, @OriginalArg(4) y: Float, @OriginalArg(5) z: Float) {
		@Pc(15) val colourChanged: Boolean = this.baseLightColor != baseLightColor
		if (colourChanged || sunIntensity != this.sunColorIntensity || antiSunIntensity != this.antiSunColorIntensity) {
			this.baseLightColor = baseLightColor
			this.sunColorIntensity = sunIntensity
			this.antiSunColorIntensity = antiSunIntensity
			if (colourChanged) {
				this.baseLightColorGreen = (this.baseLightColor and 0xFF00).toFloat() / 65280.0f
				this.baseLightColorRed = (this.baseLightColor and 0xFF0000).toFloat() / 1.671168E7f
				this.baseLightColorBlue = (this.baseLightColor and 0xFF).toFloat() / 255.0f
				this.setFogColorAndIntensity()
			}
			this.nativeInterface.setSunColour(this.baseLightColorRed, this.baseLightColorGreen, this.baseLightColorBlue, sunIntensity, antiSunIntensity)
			this.setSunParameters()
		}
		if (this.baseLightPosition[0] != x || this.baseLightPosition[1] != y || this.baseLightPosition[2] != z) {
			this.baseLightPosition[2] = z
			this.baseLightPosition[1] = y
			this.baseLightPosition[0] = x
			this.baseLightInvertedPosition[2] = -z
			this.baseLightInvertedPosition[0] = -x
			this.baseLightInvertedPosition[1] = -y
			@Pc(153) val inverseLength: Float = (1.0 / Math.sqrt((y * y + x * x + z * z).toDouble())).toFloat()
			this.sunPosition[1] = inverseLength * y
			this.sunPosition[2] = inverseLength * z
			this.sunPosition[0] = x * inverseLength
			this.lightPosition[1] = -this.sunPosition[1]
			this.lightPosition[2] = -this.sunPosition[2]
			this.lightPosition[0] = -this.sunPosition[0]
			this.nativeInterface.setSunDirection(this.sunPosition[0], this.sunPosition[1], this.sunPosition[2])
			this.setLightPosition()
			this.shadowOffsetX = (x * 256.0f / y).toInt()
			this.shadowOffsetZ = (z * 256.0f / y).toInt()
		}
		this.enableLights()
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(Z)V")
	abstract fun uploadProjectionMatrix()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZIB)V")
	fun setActiveMaterial(@OriginalArg(0) allowStillwater: Boolean, @OriginalArg(1) lit: Boolean, @OriginalArg(2) materialId: Int) {
		if (materialId != this.activeMaterialId || this.activeMaterialUnderwater != this.missingWaterColor) {
			@Pc(28) var gpuTexture: GpuTexture? = null
			@Pc(30) var combineMode: Int = 0
			@Pc(32) var effectParam1: Byte = 0
			@Pc(34) var effectParam2: Int = 0
			@Pc(41) var effectType: Int = if (this.missingWaterColor) 3 else 0
			if (materialId < 0) {
				this.resetCurrentTextureMatrix()
			} else {
				gpuTexture = this.materialCache!!.getTexture(materialId)
				@Pc(55) val textureMetrics: MaterialType = super.materialProvider!!.getMetrics(materialId)!!
				if (textureMetrics.speedU.toInt() == 0 && textureMetrics.speedV.toInt() == 0) {
					this.resetCurrentTextureMatrix()
				} else {
					@Pc(73) val textureSize: Int = if (textureMetrics.isSmall) 64 else 128
					@Pc(77) val cycleLength: Int = textureSize * 50
					@Pc(81) val textureMatrix: NativeMatrix? = this.getCurrentTextureMatrix()
					textureMatrix!!.makeTranslation((this.animatingCycle % cycleLength * textureMetrics.speedV.toInt()).toFloat() / cycleLength.toFloat(), 0.0f, (textureMetrics.speedU.toInt() * (this.animatingCycle % cycleLength)).toFloat() / cycleLength.toFloat())
					this.setTextureState(TextureTransformMode.aClass332_6)
				}
				combineMode = textureMetrics.combineMode
				if (!this.missingWaterColor) {
					effectParam1 = textureMetrics.effectParam1
					effectType = textureMetrics.effectType.toInt()
					effectParam2 = textureMetrics.effectParam2
				}
			}
			this.setActiveEffect(effectParam1.toInt(), lit, allowStillwater, effectType, effectParam2)
			if (this.activeWaterShader == null) {
				this.setTextureSampler(gpuTexture)
				this.setColourType(combineMode)
			} else {
				this.activeWaterShader!!.setBaseTexture(combineMode, gpuTexture)
			}
			this.activeMaterialUnderwater = this.missingWaterColor
			this.activeMaterialId = materialId
		}
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFF8.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!haa;)V")
	fun setModelMatrix(@OriginalArg(1) matrix: NativeMatrix?) {
		this.modelMatrix43.apply(matrix)
		this.isModelMatrixIdentity = false
		this.onModelMatrixChanged()
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(B)V")
	private fun initializeLineStream() {
		this.vertexStream = this.createStream(true)
		this.vertexStream!!.create(24, 12)
		this.vertexDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(VertexDeclarationAction.POSITION)))
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(II)V")
	fun toggleBlending(@OriginalArg(1) blendFuncType: Int) {
		if (this.blendFuncType == blendFuncType) {
			return
		}
		@Pc(25) val blendFuncMode: BlendMode?
		@Pc(27) val enableBlending: Boolean
		@Pc(23) val enableAlphaTest: Boolean
		if (blendFuncType == 1) {
			enableAlphaTest = true
			blendFuncMode = BlendFuncMode.ALPHA_BLENDING
			enableBlending = true
		} else if (blendFuncType == 2) {
			blendFuncMode = BlendFuncMode.OPAQUE_BLENDING
			enableBlending = true
			enableAlphaTest = false
		} else if (blendFuncType == 128) {
			enableAlphaTest = true
			blendFuncMode = BlendFuncMode.COLOR_BLENDING
			enableBlending = true
		} else {
			enableBlending = false
			blendFuncMode = BlendFuncMode.OPAQUE
			enableAlphaTest = false
		}
		if (enableAlphaTest != this.enableAlphaTest) {
			this.enableAlphaTest = enableAlphaTest
			this.toggleAlpha()
		}
		if (enableBlending != this.enableBlending) {
			this.enableBlending = enableBlending
			this.toggleBlending()
		}
		if (blendFuncMode !== this.blendFuncMode) {
			this.blendFuncMode = blendFuncMode
			this.blendDrawBuffer()
		}
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE3.toInt()
		this.blendFuncType = blendFuncType
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	final override fun createMask(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) offsets: IntArray?, @OriginalArg(3) widths: IntArray?): ComponentSurface {
		return MaskedTextureSurface.create(height, this, offsets, widths, width)
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(Z)V")
	abstract fun toggleSun()

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(B)V")
	abstract fun unbindCurrentTexture()

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(Z)Lclient!haa;")
	fun getCurrentTextureMatrix(): NativeMatrix? {
		return this.textureMatrices!![this.currentTextureSlot]
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(I)V")
	fun applyOrthoMatrixMode() {
		this.setOrthoMatrixMode()
		this.calculateViewRange()
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(II)Lclient!uq;")
	fun getOrResizeIndexBuffer(@OriginalArg(1) requiredCapacity: Int): GpuIndexBuffer {
		if (requiredCapacity * 2 > this.indexBuffer!!.getId()) {
			this.indexBuffer!!.allocate(requiredCapacity)
		}
		return this.indexBuffer!!
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(B)V")
	open fun updateSceneLights() {
		this.currentSceneLightCount = this.lastSceneLightCount
		this.lastSceneLightCount = 0
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "()Z")
	final override fun supportsAntiAliasing(): Boolean {
		return this.GLARBMultisample
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "()Z")
	final override fun method7311(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(I)V")
	fun setupSceneRenderState() {
		if (this.nativeFunctionMask == 8) {
			return
		}
		this.setModelViewMatrixMode()
		this.toggleFog(true)
		this.toggleDepthTest(true)
		this.setDepthMaskEnabled(true)
		this.toggleBlending(1)
		this.nativeFunctionMask = 8
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "()V")
	override fun stop() {
		if (this.isShutdown) {
			return
		}
		@Pc(13) var node: Node? = this.activeNativeMemory.first()
		while (node != null) {
			(node as NativeMemoryPool).deallocate()
			node = this.activeNativeMemory.next()
		}
		@Pc(30) val enumeration: Enumeration<Any?> = this.surfacesByCanvas!!.keys()
		while (enumeration.hasMoreElements()) {
			@Pc(36) val canvas: Canvas? = enumeration.nextElement() as Canvas?
			this.destroySurface(canvas, this.surfacesByCanvas!!.get(canvas))
		}
		ColorUtil.releaseColorTables(true, false)
		this.nativeInterface.release()
		this.isShutdown = true
	}

	@OriginalMember(owner = "client!wga", name = "HA", descriptor = "(IIII[I)V")
	final override fun HA(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) zoom: Int, @OriginalArg(4) screenCoords: IntArray?) {
		@Pc(14) val depth: Float = this.viewMatrix43!!.transformZ(x.toFloat(), z.toFloat(), y.toFloat())
		if (this.camNear.toFloat() > depth || this.camFar.toFloat() < depth) {
			screenCoords!![2] = -1
			screenCoords[1] = screenCoords[2]
			screenCoords[0] = screenCoords[1]
			return
		}
		@Pc(58) val screenX: Int = (this.projectionScaleX.toFloat() * this.viewMatrix43!!.transformX(z.toFloat(), y.toFloat(), x.toFloat()) / zoom.toFloat()).toInt()
		@Pc(77) val screenY: Int = (this.projectionScaleY.toFloat() * this.viewMatrix43!!.transformY(x.toFloat(), z.toFloat(), y.toFloat()) / zoom.toFloat()).toInt()
		screenCoords!![2] = depth.toInt()
		screenCoords[1] = (screenY.toFloat() - this.viewY1).toInt()
		screenCoords[0] = (screenX.toFloat() - this.viewX1).toInt()
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "()Z")
	final override fun extendBuildArea(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(B)I")
	fun getWaterIntensity(): Int {
		return this.waterIntensity
	}

	@OriginalMember(owner = "client!wga", name = "Q", descriptor = "(IIIIII[BII)V")
	final override fun Q(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) backgroundColour: Int, @OriginalArg(5) foregroundColour: Int, @OriginalArg(6) mask: ByteArray?, @OriginalArg(7) maskWidth: Int, @OriginalArg(8) mode: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bca;)V")
	final override fun setCamera(@OriginalArg(0) matrix: Matrix?) {
		this.viewMatrix43 = matrix as NativeMatrix?
		this.flippedViewMatrix.apply(this.viewMatrix43)
		this.flippedViewMatrix.negateYZ()
		this.inverseSunMatrix44.setInverse(this.flippedViewMatrix)
		this.inverseSunMatrix43.setInverse(this.viewMatrix43)
		if (this.matrixMode!!.isSceneMode()) {
			this.requestViewPositionUpdate()
		}
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(B)V")
	abstract fun toggleFog()

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(Z)V")
	private fun initializeQuadStream() {
		this.quadVertexStream = this.createStream(false)
		this.quadVertexStream!!.create(140, 28)
		for (@Pc(20) i in 0 until 4) {
			@Pc(28) val buffer: Buffer? = this.quadVertexStream!!.lock()
			if (buffer != null) {
				@Pc(35) val stream: Stream = this.wrapBuffer(buffer)
				if (Stream.c()) {
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(1.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
					stream.a(0.0f)
				} else {
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(1.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
					stream.b(0.0f)
				}
				stream.a()
				if (this.quadVertexStream!!.unlock()) {
					break
				}
			}
		}
		this.quadVertexDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(arrayOf<VertexAttribute?>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.TEXCOORD))))
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	abstract fun setTextureAlphaStage(@OriginalArg(0) state: TextureCombineSource?, @OriginalArg(1) sourceIndex: Int)

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	abstract fun createTextureSampler(@OriginalArg(0) width: Int, @OriginalArg(1) pixels: IntArray?, @OriginalArg(2) mipmapped: Boolean, @OriginalArg(4) offset: Int, @OriginalArg(5) rowLength: Int, @OriginalArg(6) height: Int): GpuTexture?

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I)V")
	final override fun allocateThreads(@OriginalArg(0) threadCount: Int) {
		if (threadCount != 1) {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(B)V")
	private fun invalidateModelView() {
		this.isModelViewValid = false
		this.markModelViewValid()
		if (MatrixMode.aClass286_6 === this.matrixMode) {
			this.calculateViewRange()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IB)Lclient!fj;")
	open fun createRenderPass(@OriginalArg(0) effect: Int): RenderPass? {
		if (effect == 6) {
			return UnlitPass(this)
		} else if (effect == 1) {
			return FixedFunctionSpecularPass(this)
		} else if (effect == 2) {
			return FixedFunctionWaterPass(this, this.waterTexture)
		} else if (effect == 7) {
			return FixedFunctionEnvironmentMapPass(this)
		} else {
			return NoEffectPass(this)
		}
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(B)I")
	fun getWaterScale(): Int {
		return this.waterScale
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(I)Lclient!nj;")
	fun getEnvironmentMapSampler(): CubeTextureSampler? {
		return if (this.sunCubeTextureGenerator == null) null else this.sunCubeTextureGenerator!!.getNativeCubeTexture()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([FI)[F")
	fun getTransposedProjectionMatrix(@OriginalArg(0) dest: FloatArray?): FloatArray? {
		val activeProjectionSource: FloatArray = this.activeProjectionSource!!
		dest!![0] = activeProjectionSource[0]
		dest[4] = activeProjectionSource[1]
		dest[12] = activeProjectionSource[3]
		dest[8] = activeProjectionSource[2]
		dest[9] = activeProjectionSource[6]
		dest[13] = activeProjectionSource[7]
		dest[2] = activeProjectionSource[8]
		dest[1] = activeProjectionSource[4]
		dest[5] = activeProjectionSource[5]
		dest[10] = activeProjectionSource[10]
		dest[14] = activeProjectionSource[11]
		dest[3] = activeProjectionSource[12]
		dest[6] = activeProjectionSource[9]
		dest[7] = activeProjectionSource[13]
		dest[15] = activeProjectionSource[15]
		dest[11] = activeProjectionSource[14]
		return dest
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(I)V")
	private fun onModelMatrixChanged() {
		if (MatrixMode.aClass286_5 === this.matrixMode) {
			@Pc(7) val pixelOffset: Float = this.getPixelOffset()
			this.modelMatrix43.translate(pixelOffset, 0.0f, pixelOffset)
		}
		this.viewPositionCalibrated = false
		this.uploadModelMatrix()
		if (this.activeWaterShader != null) {
			this.activeWaterShader!!.onModelMatrixChanged()
		}
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(Z)[F")
	fun getOrthoProjectionMatrix(): FloatArray {
		return this.orthoProjectionMatrix
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZ)V")
	fun toggleFog(@OriginalArg(1) enabled: Boolean) {
		if (this.fogEnabled != enabled) {
			this.fogEnabled = enabled
			this.toggleFog()
			this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE0.toInt()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIZ[I)Lclient!wfa;")
	fun createTexture(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int, @OriginalArg(3) mipmapped: Boolean, @OriginalArg(4) pixels: IntArray?): GpuTexture? {
		return this.createTextureSampler(width, pixels, mipmapped, 0, 0, height)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZ)Lclient!uq;")
	abstract fun createIndexBuffer(@OriginalArg(1) dynamic: Boolean): GpuIndexBuffer?

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(B)V")
	abstract fun setDepthRenderState()

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(Z)V")
	abstract fun setSunParameters()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	abstract fun createNativeTextureSampler(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(4) pixels: ByteArray?, @OriginalArg(5) format: PixelFormat?, @OriginalArg(7) mipmapped: Boolean): GpuTexture?

	@OriginalMember(owner = "client!wga", name = "X", descriptor = "(I)V")
	final override fun X(@OriginalArg(0) shadowSize: Int) {
		var shadowSize: Int = shadowSize
		this.shadowSizeShift = 0
		while (shadowSize > 1) {
			this.shadowSizeShift++
			shadowSize = shadowSize shr 0x1
		}
		this.activeFlags = 0x1 shl this.shadowSizeShift
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	abstract fun resizeSurface(@OriginalArg(1) canvas: Canvas?, @OriginalArg(2) surface: Any?)

	@OriginalMember(owner = "client!wga", name = "aa", descriptor = "(IIIIII)V")
	final override fun aa(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		this.setupFlatUiRenderState()
		this.setColourValue(colour)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.toggleBlending(mode)
		this.modelMatrix43.setScale(1.0f, height.toFloat(), width.toFloat())
		this.modelMatrix43.translate(x, y, 0)
		this.commitModelMatrix()
		this.toggleMultisampling(false)
		this.drawQuadTriangleFan()
		this.toggleMultisampling(true)
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(B)V")
	private fun setProjectionMatrixMode() {
		if (MatrixMode.aClass286_5 === this.matrixMode) {
			return
		}
		@Pc(6) val matrixMode: MatrixMode? = this.matrixMode
		this.matrixMode = MatrixMode.aClass286_5
		if (matrixMode!!.isSceneMode()) {
			this.requestViewPositionUpdate()
		}
		this.loadWorldViewProjectionMatrix()
		this.activeProjectionSource = this.screenOrthoMatrix
		this.calculateViewRange()
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE7.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(B)V")
	private fun invalidateOrthoSceneProjection() {
		this.isOrthoSceneProjectionValid = false
		this.loadOrthoSceneProjectionMatrix()
		if (MatrixMode.aClass286_1 === this.matrixMode) {
			this.calculateViewRange()
		}
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(I)V")
	private fun setOrthoSceneMatrixMode() {
		if (MatrixMode.aClass286_1 === this.matrixMode) {
			return
		}
		@Pc(11) val matrixMode: MatrixMode? = this.matrixMode
		this.matrixMode = MatrixMode.aClass286_1
		if (!matrixMode!!.isSceneMode()) {
			this.requestViewPositionUpdate()
		}
		this.loadOrthoSceneProjectionMatrix()
		this.activeProjectionSource = this.orthoSceneProjectionMatrix
		this.calculateViewRange()
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFF8.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(I)Lclient!haa;")
	fun getTextureMatrix(): NativeMatrix? {
		return this.textureMatrices!![this.currentTextureSlot]
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BILclient!kf;)V")
	fun setTextureRgbStageState(@OriginalArg(1) sourceIndex: Int, @OriginalArg(2) state: TextureCombineSource?) {
		this.setTextureColourSource(false, sourceIndex, state, false)
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(IIIIII)V")
	final override fun line(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		@Pc(10) var dx: Float = (-x1).toFloat() + x2.toFloat()
		@Pc(16) var dy: Float = y2.toFloat() - y1.toFloat()
		if (dx == 0.0f && dy == 0.0f) {
			dx = 1.0f
		} else {
			@Pc(37) val inverseLength: Float = (1.0 / Math.sqrt((dy * dy + dx * dx).toDouble())).toFloat()
			dy *= inverseLength
			dx *= inverseLength
		}
		if (!this.uploadLineVertices(y1.toFloat(), x2.toFloat() + dx, 0.0f, dy + y2.toFloat(), 0.0f, x1.toFloat())) {
			return
		}
		this.setupFlatUiRenderState()
		this.setColourValue(colour)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.toggleBlending(mode)
		this.resetModelMatrix()
		this.toggleMultisampling(false)
		this.drawPrimitiveLines()
		this.toggleMultisampling(true)
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BLclient!du;)V")
	fun setTextureSampler(@OriginalArg(1) textureSampler: TextureSampler?) {
		if (this.textureSamplers!![this.currentTextureSlot] === textureSampler) {
			return
		}
		this.textureSamplers!![this.currentTextureSlot] = textureSampler
		if (textureSampler == null) {
			this.unbindCurrentTexture()
		} else {
			textureSampler.bindTexture()
		}
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFFE.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "()Lclient!bca;")
	final override fun camera(): Matrix? {
		return this.viewMatrix43
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(ILclient!aa;II)V")
	final override fun A(@OriginalArg(0) colour: Int, @OriginalArg(1) clippingMask: ComponentSurface?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		@Pc(6) val maskedSurface: MaskedTextureSurface? = clippingMask as MaskedTextureSurface?
		@Pc(9) val gpuTexture: GpuTexture? = maskedSurface!!.nativeTextureSampler
		this.setupUiRenderState()
		this.setTextureSampler(gpuTexture)
		this.toggleBlending(1)
		this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setColourValue(colour)
		this.modelMatrix43.setScale(0.0f, this.surfaceHeight.toFloat(), this.surfaceWidth.toFloat())
		this.commitModelMatrix()
		this.textureMatrices!![0]!!.setScale(1.0f, gpuTexture!!.toTextureCoordT(this.surfaceHeight.toFloat()), gpuTexture.toTextureCoordS(this.surfaceWidth.toFloat()))
		this.textureMatrices!![0]!!.translate(gpuTexture.toTextureCoordT((-y).toFloat()), 0.0f, gpuTexture.toTextureCoordS((-x).toFloat()))
		this.textureStates!![0] = TextureTransformMode.aClass332_6
		this.applyTextureMatrix()
		this.drawQuadTriangleFan()
		this.resetCurrentTextureMatrix()
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	final override fun createModel(@OriginalArg(0) mesh: Mesh?, @OriginalArg(1) functionMask: Int, @OriginalArg(2) featureMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int): Model {
		return GpuModel(this, mesh!!, functionMask, ambient, contrast, featureMask)
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	final override fun resizeCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(5) var `object`: Any? = null
		if (canvas == null || canvas === this.lb) {
			`object` = this.currentSurface
		} else if (this.surfacesByCanvas!!.containsKey(canvas)) {
			`object` = this.surfacesByCanvas!!.get(canvas)
		}
		if (`object` == null) {
			throw RuntimeException()
		}
		this.resizeSurface(canvas, `object`)
		if (canvas === this.currentCanvas) {
			this.updateSurfaceSize()
		}
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(I)V")
	abstract fun configureFog()

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "()Z")
	final override fun replaceCanvasOnDisplayUpdate(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(IZ)V")
	fun setFogSupported(@OriginalArg(1) supported: Boolean) {
		if (supported != this.fogSupported) {
			this.fogSupported = supported
			this.toggleFog()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!tr;)V")
	fun setTextureState(@OriginalArg(1) textureState: TextureTransformMode?) {
		this.textureStates!![this.currentTextureSlot] = textureState
		this.applyTextureMatrix()
	}

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "()Z")
	final override fun forceFixedScreenmode(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(B)Lclient!haa;")
	fun getInverseSunMatrix44(): NativeMatrix {
		return this.inverseSunMatrix44
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	abstract fun createNativeTexture(@OriginalArg(0) format: PixelFormat?, @OriginalArg(1) width: Int, @OriginalArg(3) pixels: FloatArray?, @OriginalArg(4) mipmapped: Boolean, @OriginalArg(7) height: Int): GpuTexture?

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(B)V")
	private fun updateFogAndWater() {
		if (this.activeWaterShader != null) {
			this.activeWaterShader!!.onFogChanged()
		}
		this.configureFog()
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(B)V")
	fun setupOrthoSceneRenderState() {
		if (this.nativeFunctionMask == 16) {
			return
		}
		this.setOrthoSceneMatrixMode()
		this.toggleFog(true)
		this.toggleDepthTest(true)
		this.setDepthMaskEnabled(true)
		this.toggleBlending(1)
		this.nativeFunctionMask = 16
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIZ)Lclient!mi;")
	final override fun generateEmptySprite(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) hasAlpha: Boolean): NativeSprite {
		return GpuSprite(this, width, height, hasAlpha)
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(Z)F")
	abstract fun getPixelOffset(): Float

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(I)V")
	abstract fun toggleDepthTest()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!hj;II)V")
	private fun drawQuadPrimitive(@OriginalArg(0) drawMode: PrimitiveType?, @OriginalArg(1) primitiveCount: Int) {
		this.setStreamSource(this.quadVertexStream, 0)
		this.setVertexDeclaration(this.quadVertexDeclaration)
		this.drawPrimitive(primitiveCount, 0, drawMode)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	abstract fun createNativeTexture2D(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(3) type: DataType?, @OriginalArg(4) format: PixelFormat?): GpuTexture?

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!np;)V")
	abstract fun setVertexDeclaration(@OriginalArg(1) declaration: VertexDeclaration?)

	@OriginalMember(owner = "client!wga", name = "q", descriptor = "(I)I")
	fun getWaterColour(): Int {
		return this.waterColour
	}

	@OriginalMember(owner = "client!wga", name = "K", descriptor = "([I)V")
	final override fun K(@OriginalArg(0) clip: IntArray?) {
		clip!![0] = this.cLeft
		clip[3] = this.cBottom
		clip[2] = this.cRight
		clip[1] = this.cTop
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(I)V")
	fun setupUiRenderState() {
		if (this.nativeFunctionMask == 2) {
			return
		}
		this.setProjectionMatrixMode()
		this.toggleFog(false)
		this.toggleLights(false)
		this.toggleDepthTest(false)
		this.setDepthMaskEnabled(false)
		this.setActiveMaterial(false, false, -2)
		this.nativeFunctionMask = 2
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "()Lclient!bca;")
	final override fun scratchMatrix(): Matrix {
		return this.scratchMatrix
	}

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(Z)V")
	final override fun C(@OriginalArg(0) enabled: Boolean) {
		this.depthBufferingEnabled = enabled
		this.setDepthRenderState()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZI)V")
	fun setSunEnabled(@OriginalArg(0) enabled: Boolean) {
		if (enabled != this.sunEnabled) {
			this.sunEnabled = enabled
			this.toggleSun()
		}
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(ZZ)V")
	fun toggleLights(@OriginalArg(0) enabled: Boolean) {
		if (this.lightingEnabled != enabled) {
			this.lightingEnabled = enabled
			this.toggleLights()
			this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFF8.toInt()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	final override fun drawTexturedLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int, @OriginalArg(9) lineSpacing: Int, @OriginalArg(10) lineLength: Int, @OriginalArg(11) offset: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "()Z")
	final override fun stillwaterEffectLoaded(): Boolean {
		return this.waterShadersByEffect[3]!!.isSupported()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(FI)V")
	fun setOrthoZoom(@OriginalArg(0) zoom: Float) {
		if (zoom != this.orthoZoom) {
			this.orthoZoom = zoom
			this.invalidateOrthoSceneProjection()
		}
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(Z)V")
	abstract fun activateCurrentTexture()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BZI)Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	fun generateNativeHeapBuffer(@OriginalArg(1) allocateDirect: Boolean, @OriginalArg(2) initialCapacity: Int): NativeHeapBuffer? {
		return this.nativeHeap!!.a(initialCapacity, allocateDirect)
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "()I")
	final override fun E(): Int {
		return this.renderBufferSizeOccupied + this.texturesSizeOccupied + this.textureBuffersSizeOccupied
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bf;I)V")
	abstract fun setTexCoordGeneration(@OriginalArg(0) mode: TexCoordGenMode?)

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	abstract fun create3DTexture(@OriginalArg(2) format: PixelFormat?, @OriginalArg(5) data: ByteArray?): Texture3DSampler?

	@OriginalMember(owner = "client!wga", name = "KA", descriptor = "(IIII)V")
	final override fun KA(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int) {
		if (left <= 0 && this.surfaceWidth - 1 <= right && top <= 0 && this.surfaceHeight - 1 <= bottom) {
			this.la()
			return
		}
		this.cTop = if (top >= 0) top else 0
		this.cLeft = if (left < 0) 0 else left
		this.cBottom = if (bottom <= this.surfaceWidth) bottom else 0
		this.cRight = if (right > this.surfaceWidth) 0 else right
		if (!this.boundsCalculated) {
			this.boundsCalculated = true
			this.toggleScissorTest()
		}
		this.setScissorRect()
		this.updateViewBounds()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	final override fun createGround(@OriginalArg(0) width: Int, @OriginalArg(1) length: Int, @OriginalArg(2) tileHeights: Array<IntArray?>?, @OriginalArg(3) nextLevelHeights: Array<IntArray?>?, @OriginalArg(5) groundFlags: Int, @OriginalArg(6) featureFlags: Int): Ground {
		return GpuFloorModel(this, groundFlags, featureFlags, width, length, tileHeights, nextLevelHeights, 512)
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(I)V")
	final override fun initialize() {
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(I)V")
	final override fun method7281(@OriginalArg(0) threadId: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!sia;ILclient!sia;)V")
	fun setTextureCombineModes(@OriginalArg(0) alphaMode: TextureCombineFunction?, @OriginalArg(2) rgbMode: TextureCombineFunction?) {
		@Pc(5) var changed: Boolean = false
		if (rgbMode !== this.textureRgbCombineModes!![this.currentTextureSlot]) {
			this.textureRgbCombineModes!![this.currentTextureSlot] = rgbMode
			this.setTextureColorStage()
			changed = true
		}
		if (this.textureAlphaCombineModes!![this.currentTextureSlot] !== alphaMode) {
			this.textureAlphaCombineModes!![this.currentTextureSlot] = alphaMode
			changed = true
			this.setTextureAlphaStage()
		}
		if (changed) {
			this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE2.toInt()
		}
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(I)V")
	final override fun setTextureSize(@OriginalArg(0) size: Int) {
		if (size < 128 || size > 1024) {
			throw IllegalArgumentException()
		}
		if (this.materialCache != null) {
			this.materialCache.reset()
		}
		this.size = size
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZZIII)V")
	private fun setActiveEffect(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) lit: Boolean, @OriginalArg(2) allowStillwater: Boolean, @OriginalArg(3) effectType: Int, @OriginalArg(4) effectParam2: Int) {
		var effectParam1: Int = effectParam1
		var effectType: Int = effectType
		var effectParam2: Int = effectParam2
		@Pc(14) val stillwaterAllowed: Boolean = allowStillwater and this.stillwaterEffectLoaded()
		if (!stillwaterAllowed && (effectType == 4 || effectType == 8 || effectType == 9)) {
			effectType = 2
			effectParam2 = 1
			effectParam1 = 0
		}
		if (effectType != 0 && lit) {
			effectType = effectType or Int.MIN_VALUE
		}
		if (this.activeEffectType != effectType) {
			if (this.activeEffectType != 0) {
				this.waterShadersByEffect[Int.MAX_VALUE and this.activeEffectType]!!.disable()
			}
			if (effectType == 0) {
				this.activeWaterShader = null
			} else {
				this.activeWaterShader = this.waterShadersByEffect[Int.MAX_VALUE and effectType]
				this.activeWaterShader!!.enable(lit)
				this.activeWaterShader!!.applyTextureCombine(lit)
				this.activeWaterShader!!.setEffectParams(effectParam1, effectParam2)
			}
			this.activeEffectType = effectType
			this.activeEffectParam1 = effectParam1
			this.activeEffectParam2 = effectParam2
		} else if (this.activeEffectType != 0) {
			this.waterShadersByEffect[Int.MAX_VALUE and this.activeEffectType]!!.applyTextureCombine(lit)
			if (this.activeEffectParam1 != effectParam1 || this.activeEffectParam2 != effectParam2) {
				this.waterShadersByEffect[this.activeEffectType and Int.MAX_VALUE]!!.setEffectParams(effectParam1, effectParam2)
				this.activeEffectParam2 = effectParam2
				this.activeEffectParam1 = effectParam1
			}
		}
	}

	@OriginalMember(owner = "client!wga", name = "za", descriptor = "(IIIII)V")
	final override fun za(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		this.setupFlatUiRenderState()
		this.setColourValue(colour)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.toggleBlending(mode)
		this.modelMatrix43.setScale(1.0f, radius.toFloat(), radius.toFloat())
		this.modelMatrix43.translate(x, y, 0)
		this.commitModelMatrix()
		this.toggleMultisampling(false)
		this.setStreamSource(this.circleVertexStream, 0)
		this.setVertexDeclaration(this.circleVertexDeclaration)
		this.drawPrimitive(256, 0, PrimitiveType.aClass134_3)
		this.toggleMultisampling(true)
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "()I")
	final override fun i(): Int {
		return this.camNear
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	abstract fun createSurface(@OriginalArg(1) canvas: Canvas?): Any?

	@OriginalMember(owner = "client!wga", name = "la", descriptor = "()V")
	final override fun la() {
		this.cRight = this.surfaceWidth
		this.cBottom = this.surfaceHeight
		this.cTop = 0
		this.cLeft = 0
		if (this.boundsCalculated) {
			this.boundsCalculated = false
			this.toggleScissorTest()
		}
		this.updateViewBounds()
	}

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(B)I")
	fun getCurrentTextureSlot(): Int {
		return this.currentTextureSlot
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(II)V")
	final override fun f(@OriginalArg(0) camNear: Int, @OriginalArg(1) camFar: Int) {
		if (this.camNear == camNear && camFar == this.camFar) {
			return
		}
		this.camFar = camFar
		this.camNear = camNear
		this.invalidateModelView()
		this.invalidateOrthoSceneProjection()
		this.updateFogAndWater()
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	final override fun createCubeTexture(@OriginalArg(0) materialPositiveX: Int, @OriginalArg(1) materialNegativeX: Int, @OriginalArg(2) materialPositiveY: Int, @OriginalArg(3) materialNegativeY: Int, @OriginalArg(4) materialPositiveZ: Int, @OriginalArg(5) materialNegativeZ: Int): EnvironmentSampler {
		return NativeCubeTextureGenerator(this, materialPositiveX, materialNegativeX, materialPositiveY, materialNegativeY, materialPositiveZ, materialNegativeZ)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(FIFFFFF)Z")
	private fun uploadLineVertices(@OriginalArg(0) startY: Float, @OriginalArg(2) endX: Float, @OriginalArg(3) startZ: Float, @OriginalArg(4) endY: Float, @OriginalArg(5) endZ: Float, @OriginalArg(6) startX: Float): Boolean {
		@Pc(13) val buffer: Buffer? = this.vertexStream!!.lock()
		if (buffer == null) {
			return false
		}
		@Pc(22) val stream: Stream = this.wrapBuffer(buffer)
		if (Stream.c()) {
			stream.a(startX)
			stream.a(startY)
			stream.a(startZ)
			stream.a(endX)
			stream.a(endY)
			stream.a(endZ)
		} else {
			stream.b(startX)
			stream.b(startY)
			stream.b(startZ)
			stream.b(endX)
			stream.b(endY)
			stream.b(endZ)
		}
		stream.a()
		return this.vertexStream!!.unlock()
	}

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "(I)V")
	fun resetCurrentTextureMatrix() {
		if (this.textureStates!![this.currentTextureSlot] !== TextureState.INITIALIZE) {
			this.textureStates!![this.currentTextureSlot] = TextureState.INITIALIZE
			this.textureMatrices!![this.currentTextureSlot]!!.makeIdentity()
			this.applyTextureMatrix()
		}
	}

	@OriginalMember(owner = "client!wga", name = "ra", descriptor = "(IIII)V")
	final override fun ra(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) waterColour: Int, @OriginalArg(2) waterScale: Int, @OriginalArg(3) waterIntensity: Int) {
		this.waterScale = waterScale
		this.waterIntensity = waterIntensity
		this.waterColour = waterColour
		this.missingWaterColor = true
		this.waterHeight = waterHeight
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	abstract fun supportsTextureFormat(@OriginalArg(0) type: DataType?, @OriginalArg(1) format: PixelFormat?): Boolean

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	abstract fun setSurface(@OriginalArg(0) surface: Any?, @OriginalArg(2) canvas: Canvas?)

	@OriginalMember(owner = "client!wga", name = "q", descriptor = "(B)V")
	open fun setupGraphics() {
		this.resetAllRenderState()
	}

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "(I)V")
	abstract fun toggleLights()

	@OriginalMember(owner = "client!wga", name = "xa", descriptor = "(F)V")
	final override fun xa(@OriginalArg(0) intensity: Float) {
		if (this.intensity != intensity) {
			this.intensity = intensity
			this.nativeInterface.setAmbient(intensity)
			this.setFogColorAndIntensity()
			this.enableLights()
		}
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(B)V")
	abstract fun enableLights()

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "()Z")
	final override fun supportsFullscreen(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(I)V")
	final override fun linkThreads(@OriginalArg(0) threadId: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "()Z")
	final override fun hasTextures(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	final override fun drawTriangle() {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZLclient!kf;)V")
	fun setTextureAlphaStageState(@OriginalArg(0) sourceIndex: Int, @OriginalArg(2) state: TextureCombineSource?) {
		this.setTextureAlphaStage(state, sourceIndex)
	}

	@OriginalMember(owner = "client!wga", name = "T", descriptor = "(IIII)V")
	final override fun T(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int) {
		@Pc(5) var changed: Boolean = false
		if (this.cLeft < left) {
			changed = true
			this.cLeft = left
		}
		if (right < this.cRight) {
			this.cRight = right
			changed = true
		}
		if (top > this.cTop) {
			changed = true
			this.cTop = top
		}
		if (bottom < this.cBottom) {
			this.cBottom = bottom
			changed = true
		}
		if (!changed) {
			return
		}
		if (!this.boundsCalculated) {
			this.boundsCalculated = true
			this.toggleScissorTest()
		}
		this.setScissorRect()
		this.updateViewBounds()
	}

	@OriginalMember(owner = "client!wga", name = "s", descriptor = "(B)Lclient!haa;")
	fun getFlippedViewMatrix(): NativeMatrix {
		return this.flippedViewMatrix
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "(I)V")
	abstract fun setScissorRect()

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "()Z")
	final override fun hardShadow(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(ZI)Lclient!tc;")
	abstract fun createStream(@OriginalArg(0) dynamic: Boolean): NativeStream?

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZB)V")
	fun toggleDepthTest(@OriginalArg(0) depthTest: Boolean) {
		if (depthTest != this.enableDepthTest) {
			this.enableDepthTest = depthTest
			this.toggleDepthTest()
			this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE0.toInt()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([FIILclient!wq;IZ)Lclient!wfa;")
	fun createHdrTexture(@OriginalArg(0) pixels: FloatArray?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) format: PixelFormat?, @OriginalArg(5) mipmapped: Boolean): GpuTexture? {
		return this.createNativeTexture(format, width, pixels, mipmapped, height)
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "(I)V")
	abstract fun setLightPosition()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!jaclib/memory/Buffer;I)Lclient!jaclib/memory/Stream;")
	fun wrapBuffer(@OriginalArg(0) buffer: Buffer?): Stream {
		this.scratchStream.a(buffer)
		return this.scratchStream
	}

	@OriginalMember(owner = "client!wga", name = "EA", descriptor = "(IIII)V")
	final override fun EA(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) waterColour: Int, @OriginalArg(2) waterScale: Int, @OriginalArg(3) waterIntensity: Int) {
		if (!this.missingWaterColor) {
			throw RuntimeException("")
		}
		this.waterScale = waterScale
		this.waterHeight = waterHeight
		this.waterIntensity = waterIntensity
		this.waterColour = waterColour
		if (this.activeMaterialUnderwater) {
			this.waterShadersByEffect[3]!!.onUnderwaterSettingsChanged()
			this.waterShadersByEffect[3]!!.onFogChanged()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([I)V")
	final override fun accumulateViewportDimensions(@OriginalArg(0) cacheInto: IntArray?) {
		cacheInto!![1] = this.surfaceHeight
		cacheInto[0] = this.surfaceWidth
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wh;)V")
	final override fun setSunCubeTexture(@OriginalArg(0) environmentSampler: EnvironmentSampler?) {
		this.sunCubeTextureGenerator = environmentSampler as GpuEnvironmentSampler?
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(II)V")
	fun setColourValue(@OriginalArg(1) color: Int) {
		if (this.envColor != color) {
			this.envColor = color
			this.setTextureEnvironmentColor()
		}
	}

	@OriginalMember(owner = "client!wga", name = "L", descriptor = "(III)V")
	final override fun L(@OriginalArg(0) colour: Int, @OriginalArg(1) distance: Int, @OriginalArg(2) offset: Int) {
		if (this.fogColorRgb == colour && this.fogDistance == distance && this.fogOffset == offset) {
			return
		}
		this.fogColorRgb = colour
		this.fogOffset = offset
		this.fogDistance = distance
		this.updateFogAndWater()
		this.toggleFog()
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "(I)V")
	private fun setModelViewMatrixMode() {
		if (MatrixMode.aClass286_6 === this.matrixMode) {
			return
		}
		@Pc(16) val matrixMode: MatrixMode? = this.matrixMode
		this.matrixMode = MatrixMode.aClass286_6
		if (!matrixMode!!.isSceneMode()) {
			this.requestViewPositionUpdate()
		}
		this.markModelViewValid()
		this.activeProjectionSource = this.perspectiveProjectionMatrix
		this.calculateViewRange()
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFF8.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(II)V")
	fun setTextureSlot(@OriginalArg(0) textureSlot: Int) {
		if (textureSlot != this.currentTextureSlot) {
			this.currentTextureSlot = textureSlot
			this.activateCurrentTexture()
		}
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "(I)V")
	abstract fun uploadViewMatrix()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Z)V")
	final override fun setShrinkTextures(@OriginalArg(0) shrink: Boolean) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IILclient!hj;Z)V")
	abstract fun drawPrimitive(@OriginalArg(0) primitiveCount: Int, @OriginalArg(1) first: Int, @OriginalArg(2) drawMode: PrimitiveType?)

	@OriginalMember(owner = "client!wga", name = "DA", descriptor = "(IIII)V")
	final override fun DA(@OriginalArg(0) projectionCenterX: Int, @OriginalArg(1) projectionCenterY: Int, @OriginalArg(2) projectionScaleX: Int, @OriginalArg(3) projectionScaleY: Int) {
		this.projectionCenterY = projectionCenterY
		this.projectionScaleY = projectionScaleY
		this.projectionCenterX = projectionCenterX
		this.projectionScaleX = projectionScaleX
		this.invalidateOrthoSceneProjection()
		this.invalidateModelView()
		this.setOrthoMatrixMode()
		this.updateViewBounds()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([[IZII)Lclient!nj;")
	abstract fun generateNativeCubeTexture(@OriginalArg(0) pixels: Array<IntArray?>?, @OriginalArg(1) mipmappingEnabled: Boolean, @OriginalArg(3) largestSize: Int): CubeTextureSampler?

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "(B)V")
	abstract fun uploadModelMatrix()

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "(B)V")
	fun initializeRenderers() {
		@Pc(21) val hashtable: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()
		if (this.surfacesByCanvas != null && !this.surfacesByCanvas!!.isEmpty()) {
			@Pc(32) val enumeration: Enumeration<Any?> = this.surfacesByCanvas!!.keys()
			while (enumeration.hasMoreElements()) {
				@Pc(38) val canvas: Canvas? = enumeration.nextElement() as Canvas?
				hashtable.put(canvas, this.createSurface(canvas))
			}
		}
		this.surfacesByCanvas = hashtable
		this.initializeQuadStream()
		this.initializeLineStream()
		this.initializeCircleStream()
		this.particleRenderer!!.resetVertexStream(this)
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(Z)V")
	private fun setupFlatUiRenderState() {
		if (this.nativeFunctionMask == 1) {
			return
		}
		this.setProjectionMatrixMode()
		this.toggleFog(false)
		this.toggleLights(false)
		this.toggleDepthTest(false)
		this.setDepthMaskEnabled(false)
		this.setActiveMaterial(false, false, -2)
		this.setColourType(1)
		this.setTextureSampler(this.defaultTexture)
		this.nativeFunctionMask = 1
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(IIIIIII)I")
	final override fun r(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int, @OriginalArg(6) zoom: Int): Int {
		val viewMatrix43: NativeMatrix = this.viewMatrix43!!
		@Pc(7) var clipFlags: Int = 0
		@Pc(32) val startDepth: Float = viewMatrix43.zD + viewMatrix43.zB * y1.toFloat() + x1.toFloat() * viewMatrix43.zA + z1.toFloat() * viewMatrix43.zC
		@Pc(57) val endDepth: Float = viewMatrix43.zA * x2.toFloat() + viewMatrix43.zB * y2.toFloat() + viewMatrix43.zC * z2.toFloat() + viewMatrix43.zD
		if (this.camNear.toFloat() > startDepth && endDepth < this.camNear.toFloat()) {
			clipFlags = clipFlags or 0x10
		} else if (this.camFar.toFloat() < startDepth && this.camFar.toFloat() < endDepth) {
			clipFlags = clipFlags or 0x20
		}
		@Pc(124) val startScreenX: Int = ((y1.toFloat() * viewMatrix43.xB + x1.toFloat() * viewMatrix43.xA + z1.toFloat() * viewMatrix43.xC + viewMatrix43.xD) * this.projectionScaleX.toFloat() / zoom.toFloat()).toInt()
		@Pc(157) val endScreenX: Int = (this.projectionScaleX.toFloat() * (viewMatrix43.xD + x2.toFloat() * viewMatrix43.xA + y2.toFloat() * viewMatrix43.xB + z2.toFloat() * viewMatrix43.xC) / zoom.toFloat()).toInt()
		if (this.viewX1 > startScreenX.toFloat() && endScreenX.toFloat() < this.viewX1) {
			clipFlags = clipFlags or 0x1
		} else if (this.viewX2 < startScreenX.toFloat() && endScreenX.toFloat() > this.viewX2) {
			clipFlags = clipFlags or 0x2
		}
		@Pc(224) val startScreenY: Int = (this.projectionScaleY.toFloat() * (viewMatrix43.yD + viewMatrix43.yC * z1.toFloat() + viewMatrix43.yA * x1.toFloat() + viewMatrix43.yB * y1.toFloat()) / zoom.toFloat()).toInt()
		@Pc(257) val endScreenY: Int = (this.projectionScaleY.toFloat() * (viewMatrix43.yC * z2.toFloat() + x2.toFloat() * viewMatrix43.yA + y2.toFloat() * viewMatrix43.yB + viewMatrix43.yD) / zoom.toFloat()).toInt()
		if (startScreenY.toFloat() < this.viewY1 && this.viewY1 > endScreenY.toFloat()) {
			clipFlags = clipFlags or 0x4
		} else if (this.viewY2 < startScreenY.toFloat() && this.viewY2 < endScreenY.toFloat()) {
			clipFlags = clipFlags or 0x8
		}
		return clipFlags
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(I)V")
	private fun updateViewBounds() {
		this.viewY1 = (this.cTop - this.projectionCenterY).toFloat()
		this.viewX1 = (this.cLeft - this.projectionCenterX).toFloat()
		this.viewY2 = (this.cBottom - this.projectionCenterY).toFloat()
		this.viewX2 = (this.cRight - this.projectionCenterX).toFloat()
	}

	@OriginalMember(owner = "client!wga", name = "v", descriptor = "(B)V")
	private fun applyTextureMatrix() {
		this.setTextureMatrix()
		if (this.activeWaterShader != null) {
			this.activeWaterShader!!.onTextureMatrixChanged()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	final override fun addCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		if (canvas === this.lb) {
			throw RuntimeException()
		} else if (!this.surfacesByCanvas!!.containsKey(canvas)) {
			if (!canvas!!.isShowing()) {
				throw RuntimeException()
			}
			try {
				@Pc(26) val class2: Class<*> = Class.forName("java.awt.Canvas")
				@Pc(36) val method: Method = class2.getMethod("setIgnoreRepaint", java.lang.Boolean.TYPE)
				method.invoke(canvas, java.lang.Boolean.TRUE)
			} catch (@Pc(48) ex: Exception) {
			}
			@Pc(53) val `object`: Any? = this.createSurface(canvas)
			if (`object` == null) {
				throw RuntimeException()
			}
			this.surfacesByCanvas!!.put(canvas, `object`)
		}
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "()I")
	final override fun getMaxLights(): Int {
		return this.maxActiveLights - 2
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "(B)V")
	private fun markModelViewValid() {
		if (this.isModelViewValid) {
			return
		}
		@Pc(13) val matrix: FloatArray = this.perspectiveProjectionMatrix
		@Pc(25) val left: Float = (this.camNear * -this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		@Pc(40) val right: Float = (this.camNear * (this.surfaceWidth - this.projectionCenterX)).toFloat() / this.projectionScaleX.toFloat()
		@Pc(51) val top: Float = (this.camNear * this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		@Pc(66) val bottom: Float = (this.camNear * (this.projectionCenterY - this.surfaceHeight)).toFloat() / this.projectionScaleY.toFloat()
		if (left == right || top == bottom) {
			matrix[12] = 0.0f
			matrix[0] = 1.0f
			matrix[13] = 0.0f
			matrix[4] = 0.0f
			matrix[3] = 0.0f
			matrix[10] = 1.0f
			matrix[15] = 1.0f
			matrix[9] = 0.0f
			matrix[14] = 0.0f
			matrix[1] = 0.0f
			matrix[2] = 0.0f
			matrix[11] = 0.0f
			matrix[6] = 0.0f
			matrix[7] = 0.0f
			matrix[8] = 0.0f
			matrix[5] = 1.0f
		} else {
			@Pc(146) val doubleNear: Float = this.camNear.toFloat() * 2.0f
			matrix[13] = 0.0f
			matrix[3] = 0.0f
			matrix[2] = 0.0f
			matrix[11] = -1.0f
			matrix[7] = 0.0f
			matrix[5] = doubleNear / (top - bottom)
			matrix[1] = 0.0f
			matrix[12] = 0.0f
			this.perspectiveDepthOffset = (this.camFar * this.camNear).toFloat() / (this.camNear - this.camFar).toFloat()
			matrix[14] = this.perspectiveDepthOffset
			matrix[0] = doubleNear / (right - left)
			matrix[15] = 0.0f
			matrix[8] = (right + left) / (-left + right)
			matrix[9] = (top + bottom) / (top - bottom)
			this.perspectiveDepthScale = this.camFar.toFloat() / (this.camNear - this.camFar).toFloat()
			matrix[10] = this.perspectiveDepthScale
			matrix[4] = 0.0f
			matrix[6] = 0.0f
		}
		this.updatePerspectiveFar()
		this.isModelViewValid = true
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(Z)V")
	private fun drawPrimitiveLines() {
		this.setStreamSource(this.vertexStream, 0)
		this.setVertexDeclaration(this.vertexDeclaration)
		this.drawPrimitive(1, 0, PrimitiveDrawMode.LINES)
	}

	@OriginalMember(owner = "client!wga", name = "Y", descriptor = "()[I")
	final override fun Y(): IntArray {
		return intArrayOf(this.projectionCenterX, this.projectionCenterY, this.projectionScaleX, this.projectionScaleY)
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "(B)V")
	private fun updateOrthoFar() {
		this.orthoFar = this.camFar.toFloat()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIII)V")
	final override fun strongLine(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) width: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	final override fun createNativeSprite(@OriginalArg(0) x: Int, @OriginalArg(1) colour: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) hasAlpha: Boolean): NativeSprite {
		@Pc(11) val sprite: GpuSprite = GpuSprite(this, width, height, hasAlpha)
		sprite.captureScreenRegion(0, 0, width, height, x, colour)
		return sprite
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BI)V")
	fun setUniformColourValue(@OriginalArg(0) brightness: Byte) {
		this.setColourValue(brightness.toInt() or (brightness.toInt() shl 8) or (brightness.toInt() shl 16) or (brightness.toInt() shl 24))
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(BI)V")
	abstract fun setTexCoordIndex(@OriginalArg(1) index: Int)

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!mda;I)V")
	final override fun renderOrtho(@OriginalArg(0) particleList: ParticleList?, @OriginalArg(1) zoom: Int) {
		this.particleRenderer!!.render(this, particleList, zoom)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	final override fun generateSprite(@OriginalArg(0) indexedImage: SpriteType?, @OriginalArg(1) paletteSprite: Boolean): NativeSprite? {
		@Pc(24) val sprite: NativeSprite?
		if (indexedImage!!.width == 0 || indexedImage.height == 0) {
			sprite = this.createNativeSprite(1, IntArray(1), 1, 1)
		} else {
			@Pc(33) val pixels: IntArray = IntArray(indexedImage.width * indexedImage.height)
			@Pc(35) var srcIndex: Int = 0
			@Pc(37) var dstIndex: Int = 0
			@Pc(42) var y: Int
			@Pc(46) var x: Int
			if (indexedImage.alpha == null) {
				y = 0
				while (y < indexedImage.height) {
					x = 0
					while (x < indexedImage.width) {
						@Pc(59) val colour: Int = indexedImage.pallete!![indexedImage.pixels!![srcIndex++].toInt() and 0xFF]
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
						pixels[dstIndex++] = indexedImage.pallete!![indexedImage.pixels!![srcIndex].toInt() and 0xFF] or (indexedImage.alpha!![srcIndex].toInt() shl 24)
						srcIndex++
						x++
					}
					y++
				}
			}
			sprite = this.createNativeSprite(indexedImage.width, pixels, indexedImage.width, indexedImage.height)
		}
		sprite!!.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY)
		return sprite
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	final override fun drawSurfaceLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int) {
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "()V")
	final override fun cacheReset() {
		if (this.materialCache != null) {
			this.materialCache.reset()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	abstract fun destroySurface(@OriginalArg(0) canvas: Canvas?, @OriginalArg(2) surface: Any?)

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "()Lclient!bca;")
	final override fun createMatrix(): Matrix {
		return NativeMatrix()
	}

	@OriginalMember(owner = "client!wga", name = "B", descriptor = "(I)V")
	private fun updatePerspectiveFar() {
		this.perspectiveProjectionMatrix[14] = this.perspectiveDepthOffset
		this.perspectiveProjectionMatrix[10] = this.perspectiveDepthScale
		this.perspectiveFar = (this.perspectiveProjectionMatrix[14] - this.camFar.toFloat()) / this.perspectiveProjectionMatrix[10]
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "(B)V")
	private fun requestViewPositionUpdate() {
		this.viewPositionCalibrated = false
		if (this.activeWaterShader != null) {
			this.activeWaterShader!!.onCameraChanged()
		}
		this.uploadViewMatrix()
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "(B)V")
	private fun initializeCircleStream() {
		this.circleVertexStream = this.createStream(false)
		this.circleVertexStream!!.create(3096, 12)
		for (@Pc(24) i in 0 until 4) {
			@Pc(32) val buffer: Buffer? = this.circleVertexStream!!.lock()
			if (buffer != null) {
				@Pc(39) val stream: Stream = this.wrapBuffer(buffer)
				stream.a(0.0f)
				stream.a(0.0f)
				stream.a(0.0f)
				for (@Pc(50) j in 0..256) {
					@Pc(61) val angle: Double = (j * 2).toDouble() * 3.141592653589793 / 256.0
					@Pc(65) val cos: Float = Math.cos(angle).toFloat()
					@Pc(69) val sin: Float = Math.sin(angle).toFloat()
					if (Stream.c()) {
						stream.a(sin)
						stream.a(cos)
						stream.a(0.0f)
					} else {
						stream.b(sin)
						stream.b(cos)
						stream.b(0.0f)
					}
				}
				stream.a()
				if (this.circleVertexStream!!.unlock()) {
					break
				}
			}
		}
		this.circleVertexDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(VertexDeclarationAction.POSITION)))
	}

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(I)V")
	fun commitModelMatrix() {
		this.isModelMatrixIdentity = false
		this.onModelMatrixChanged()
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(B)V")
	private fun resetAllRenderState() {
		this.setFogColorAndIntensity()
		this.setSunParameters()
		this.toggleLights()
		this.updateSceneLights()
		this.setLightPosition()
		this.enableLights()
		this.toggleSun()
		this.toggleDepthTest()
		this.setDepthRenderState()
		this.toggleFog()
		this.configureFog()
		this.toggleBlending()
		this.blendDrawBuffer()
		this.toggleAlpha()
		@Pc(62) var slot: Int = this.maxConcurrentTextures - 1
		while (slot >= 0) {
			this.setTextureSlot(slot)
			this.setTextureColorStage()
			this.setTextureAlphaStage()
			this.resetCurrentTextureMatrix()
			slot--
		}
		this.setTextureEnvironmentColor()
		this.setViewport()
		this.uploadProjectionMatrix()
		this.uploadModelMatrix()
		this.uploadViewMatrix()
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(Z)V")
	private fun updateSurfaceSize() {
		if (this.currentCanvas == null) {
			this.height = 1
			this.width = this.height
		} else {
			@Pc(14) val dimension: Dimension = this.currentCanvas!!.getSize()
			this.height = dimension.height
			this.width = dimension.width
		}
		this.surfaceHeight = this.height
		this.surfaceWidth = this.width
		this.invalidateOrthoProjection()
		this.invalidateModelView()
		this.invalidateOrthoSceneProjection()
		this.setViewport()
		this.updateViewBounds()
		this.setOrthoMatrixMode()
		this.la()
	}

	@OriginalMember(owner = "client!wga", name = "D", descriptor = "(I)V")
	private fun invalidateOrthoProjection() {
		this.isOrthoProjectionValid = false
		if (MatrixMode.aClass286_5 === this.matrixMode) {
			this.loadWorldViewProjectionMatrix()
			this.calculateViewRange()
		}
	}

	@OriginalMember(owner = "client!wga", name = "P", descriptor = "(IIIII)V")
	final override fun P(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) height: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		this.line(x, y, x, height + y, colour, mode)
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "(I)V")
	private fun loadWorldViewProjectionMatrix() {
		if (this.isOrthoProjectionValid) {
			return
		}
		@Pc(9) val matrix: FloatArray = this.screenOrthoMatrix
		if (this.surfaceWidth == 0 || this.surfaceHeight == 0) {
			matrix[3] = 0.0f
			matrix[12] = 0.0f
			matrix[6] = 0.0f
			matrix[9] = 0.0f
			matrix[11] = 0.0f
			matrix[15] = 1.0f
			matrix[1] = 0.0f
			matrix[8] = 0.0f
			matrix[2] = 0.0f
			matrix[14] = 0.0f
			matrix[5] = 1.0f
			matrix[13] = 0.0f
			matrix[7] = 0.0f
			matrix[10] = 1.0f
			matrix[4] = 0.0f
			matrix[0] = 1.0f
		} else {
			matrix[9] = 0.0f
			matrix[12] = -1.0f
			matrix[2] = 0.0f
			matrix[0] = 2.0f / this.surfaceWidth.toFloat()
			matrix[4] = 0.0f
			matrix[15] = 1.0f
			matrix[13] = 1.0f
			matrix[6] = 0.0f
			matrix[3] = 0.0f
			matrix[10] = 0.5f
			matrix[8] = 0.0f
			matrix[1] = 0.0f
			matrix[14] = 0.5f
			matrix[7] = 0.0f
			matrix[5] = -2.0f / this.surfaceHeight.toFloat()
			matrix[11] = 0.0f
		}
		this.isOrthoProjectionValid = true
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	final override fun setCanvas(@OriginalArg(0) canvas: Canvas?) {
		this.currentSurface = null
		this.currentCanvas = null
		if (canvas == null || canvas === this.lb) {
			this.currentSurface = this.primarySurface
			this.currentCanvas = this.lb
		} else if (this.surfacesByCanvas!!.containsKey(canvas)) {
			this.currentSurface = this.surfacesByCanvas!!.get(canvas)
			this.currentCanvas = canvas
		}
		if (this.currentCanvas == null || this.currentSurface == null) {
			throw RuntimeException()
		}
		this.setSurface(this.currentSurface, this.currentCanvas)
		this.updateSurfaceSize()
	}

	@OriginalMember(owner = "client!wga", name = "F", descriptor = "(I)V")
	abstract fun setTextureColorStage()

	@OriginalMember(owner = "client!wga", name = "G", descriptor = "(I)V")
	abstract fun toggleScissorTest()

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(I)V")
	override fun tick(@OriginalArg(0) time: Int) {
		if (this.materialCache != null) {
			this.materialCache.cacheClean()
		}
		this.animatingCycle = Int.MAX_VALUE and time
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIIII)V")
	final override fun drawLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) lineSpacing: Int, @OriginalArg(7) lineLength: Int, @OriginalArg(8) offset: Int) {
		@Pc(11) var dirX: Float = endX.toFloat() - startX.toFloat()
		@Pc(17) var dirY: Float = endY.toFloat() - startY.toFloat()
		@Pc(38) val local38: Float
		if (dirX == 0.0f && dirY == 0.0f) {
			dirX = 1.0f
		} else {
			val invLength: Float = (1.0 / Math.sqrt((dirY * dirY + dirX * dirX).toDouble())).toFloat()
			dirX *= invLength
			dirY *= invLength
		}
		this.setupFlatUiRenderState()
		this.setColourValue(colour)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.toggleBlending(1)
		this.resetModelMatrix()
		this.toggleMultisampling(false)
		@Pc(84) val phase: Int = offset % (lineSpacing + lineLength)
		val dashX: Float = lineSpacing.toFloat() * dirX
		@Pc(94) val dashY: Float = lineSpacing.toFloat() * dirY
		@Pc(96) var startOffsetX: Float = 0.0f
		@Pc(98) var startOffsetY: Float = 0.0f
		@Pc(100) var segmentX: Float = dashX
		@Pc(102) var segmentY: Float = dashY
		if (lineSpacing >= phase) {
			segmentY = (lineSpacing - phase).toFloat() * dirY
			segmentX = dirX * (lineSpacing - phase).toFloat()
		} else {
			startOffsetY = (lineSpacing + lineLength - phase).toFloat() * dirY
			startOffsetX = dirX * (lineSpacing + lineLength - phase).toFloat()
		}
		@Pc(148) var x: Float = startX.toFloat() + startOffsetX
		@Pc(153) var y: Float = startOffsetY + startY.toFloat()
		@Pc(158) val gapX: Float = lineLength.toFloat() * dirX
		@Pc(163) val gapY: Float = dirY * lineLength.toFloat()
		while (true) {
			if (startX < endX) {
				if (x > endX.toFloat()) {
					break
				}
				if (endX.toFloat() < x + segmentX) {
					segmentX = endX.toFloat() - x
				}
			} else {
				if (x < endX.toFloat()) {
					break
				}
				if (endX.toFloat() > x + segmentX) {
					segmentX = endX.toFloat() - x
				}
			}
			if (startY < endY) {
				if (endY.toFloat() < y) {
					break
				}
				if (segmentY + y > endY.toFloat()) {
					segmentY = endY.toFloat() - y
				}
			} else {
				if (y < endY.toFloat()) {
					break
				}
				if (segmentY + y < endY.toFloat()) {
					segmentY = endY.toFloat() - y
				}
			}
			if (!this.uploadLineVertices(y, x + segmentX, 0.0f, y + segmentY, 0.0f, x)) {
				return
			}
			this.drawPrimitiveLines()
			y += gapY + segmentY
			x += gapX + segmentX
			segmentX = dashX
			segmentY = dashY
		}
		this.toggleMultisampling(true)
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "pa", descriptor = "()V")
	final override fun pa() {
		this.missingWaterColor = false
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(Z)V")
	fun deallocate() {
		@Pc(15) val enumeration: Enumeration<Any?> = this.surfacesByCanvas!!.keys()
		while (enumeration.hasMoreElements()) {
			@Pc(21) val canvas: Canvas? = enumeration.nextElement() as Canvas?
			this.destroySurface(canvas, this.surfacesByCanvas!!.get(canvas))
		}
		this.quadVertexStream!!.deallocate()
		this.vertexStream!!.deallocate()
		this.circleVertexStream!!.deallocate()
		this.copyType1Scratch!!.releaseStreams()
		this.copyType2Scratch!!.releaseStreams()
		this.copyType3Scratch!!.releaseStreams()
		this.copyType4Scratch!!.releaseStreams()
		this.copyType5Scratch!!.releaseStreams()
		this.particleRenderer!!.release()
		this.indexBuffer!!.release()
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(ZI)V")
	fun setColourType(@OriginalArg(1) combineMode: Int) {
		if (combineMode == 1) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4)
		} else if (combineMode == 0) {
			this.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		} else if (combineMode == 2) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5)
		} else if (combineMode == 3) {
			this.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_2)
		} else if (combineMode == 4) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_1, TextureCombineFunction.aClass315_1)
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	abstract fun setTextureColourSource(@OriginalArg(0) useAlpha: Boolean, @OriginalArg(2) sourceIndex: Int, @OriginalArg(3) state: TextureCombineSource?, @OriginalArg(4) invert: Boolean)

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!tc;I)V")
	abstract fun setStreamSource(@OriginalArg(1) stream: NativeStream?, @OriginalArg(2) streamIndex: Int)

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BZ)V")
	abstract fun toggleMultisampling(@OriginalArg(1) enabled: Boolean)

	@OriginalMember(owner = "client!wga", name = "U", descriptor = "(IIIII)V")
	final override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		this.line(x, y, x + width, y, colour, mode)
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(Z)V")
	fun setupTexturedUiRenderState() {
		if (this.nativeFunctionMask == 4) {
			return
		}
		this.setProjectionMatrixMode()
		this.toggleFog(false)
		this.toggleLights(false)
		this.toggleDepthTest(false)
		this.setDepthMaskEnabled(false)
		this.setActiveMaterial(false, false, -2)
		this.toggleBlending(1)
		this.setColourType(0)
		this.nativeFunctionMask = 4
	}

	@OriginalMember(owner = "client!wga", name = "H", descriptor = "(I)V")
	abstract fun setFogColorAndIntensity()

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	final override fun releaseSurface(@OriginalArg(0) canvas: Canvas?) {
		if (this.lb === canvas) {
			throw RuntimeException()
		} else if (this.surfacesByCanvas!!.containsKey(canvas)) {
			this.destroySurface(canvas, this.surfacesByCanvas!!.get(canvas))
			this.surfacesByCanvas!!.remove(canvas)
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	final override fun createNativeSprite(@OriginalArg(0) pixels: IntArray?, @OriginalArg(2) rowLength: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) copyPixels: Boolean): NativeSprite {
		return GpuSprite(this, width, height, pixels, 0, rowLength)
	}

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(Z)V")
	private fun calculateViewRange() {
		this.uploadProjectionMatrix()
		if (this.activeWaterShader != null) {
			this.activeWaterShader!!.onProjectionChanged()
		}
	}

	@OriginalMember(owner = "client!wga", name = "B", descriptor = "(B)V")
	fun initializeTables() {
		this.textureMatrices = arrayOfNulls<NativeMatrix>(this.maxConcurrentTextures)
		this.textureSamplers = arrayOfNulls<TextureSampler>(this.maxConcurrentTextures)
		this.textureRgbCombineModes = arrayOfNulls<TextureCombineFunction>(this.maxConcurrentTextures)
		this.textureAlphaCombineModes = arrayOfNulls<TextureCombineFunction>(this.maxConcurrentTextures)
		this.textureStates = arrayOfNulls<TextureTransformMode>(this.maxConcurrentTextures)
		for (@Pc(32) i in 0 until this.maxConcurrentTextures) {
			this.textureAlphaCombineModes!![i] = TextureCombineMode.MODULATE
			this.textureRgbCombineModes!![i] = TextureCombineMode.MODULATE
			this.textureStates!![i] = TextureState.INITIALIZE
			this.textureMatrices!![i] = NativeMatrix()
		}
		this.lightSources = arrayOfNulls<PointLight>(this.maxActiveLights - 2)
		this.defaultTexture = this.createNativeTexture2D(1, 1, GLFormat.RGBA, GLFormat.HDR)
		this.setHeap(NativeMemoryPool(262144))
		this.positionTexCoordDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(arrayOf<VertexAttribute?>(VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD))))
		this.positionColorDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(arrayOf<VertexAttribute?>(VertexDeclarationAction.POSITION, VertexDeclarationAction.COLOR))))
		this.positionColorTexCoordNormalDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(VertexDeclarationAction.POSITION), VertexDeclarationComponent(VertexDeclarationAction.COLOR), VertexDeclarationComponent(VertexDeclarationAction.TEXCOORD), VertexDeclarationComponent(VertexDeclarationAction.NORMAL_POINTER_ACTION)))
		this.positionColorTexCoordDeclaration = this.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(VertexDeclarationAction.POSITION), VertexDeclarationComponent(VertexDeclarationAction.COLOR), VertexDeclarationComponent(VertexDeclarationAction.TEXCOORD)))
		this.copyType1Result = GpuModel(this, 0, 0, false, false)
		this.copyType1Scratch = GpuModel(this, 0, 0, true, true)
		this.copyType2Result = GpuModel(this, 0, 0, false, false)
		this.copyType2Scratch = GpuModel(this, 0, 0, true, true)
		this.copyType3Result = GpuModel(this, 0, 0, false, false)
		this.copyType3Scratch = GpuModel(this, 0, 0, true, true)
		this.copyType4Result = GpuModel(this, 0, 0, false, false)
		this.copyType4Scratch = GpuModel(this, 0, 0, true, true)
		this.copyType5Result = GpuModel(this, 0, 0, false, false)
		this.copyType5Scratch = GpuModel(this, 0, 0, true, true)
		this.particleRenderer = NativeParticleRenderer(this)
		this.indexBuffer = this.createIndexBuffer(true)
		this.initializeRenderers()
		this.waterTexture = NativeWaterTexture(this)
		this.waterShadersByEffect[1] = this.createRenderPass(1)
		this.waterShadersByEffect[2] = this.createRenderPass(2)
		this.waterShadersByEffect[4] = this.createRenderPass(4)
		this.waterShadersByEffect[5] = this.createRenderPass(5)
		this.waterShadersByEffect[6] = this.createRenderPass(6)
		this.waterShadersByEffect[7] = this.createRenderPass(7)
		this.waterShadersByEffect[3] = this.createRenderPass(3)
		this.waterShadersByEffect[8] = this.createRenderPass(8)
		this.waterShadersByEffect[9] = this.createRenderPass(9)
		if (!this.waterShadersByEffect[2]!!.isSupported()) {
			this.waterShadersByEffect[2] = this.createRenderPass(0)
		}
		if (!this.waterShadersByEffect[4]!!.isSupported()) {
			this.waterShadersByEffect[4] = this.waterShadersByEffect[2]
		}
		if (!this.waterShadersByEffect[8]!!.isSupported()) {
			this.waterShadersByEffect[8] = this.waterShadersByEffect[4]
		}
		if (!this.waterShadersByEffect[9]!!.isSupported()) {
			this.waterShadersByEffect[9] = this.waterShadersByEffect[8]
		}
		this.setupGraphics()
		this.la()
		this.renderLastFrame()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	abstract fun supports3DTextureFormat(@OriginalArg(1) format: PixelFormat?, @OriginalArg(2) type: DataType?): Boolean

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(B)V")
	fun setOrthoMatrixMode() {
		if (this.matrixMode === MatrixMode.aClass286_7) {
			return
		}
		@Pc(6) val matrixMode: MatrixMode? = this.matrixMode
		this.matrixMode = MatrixMode.aClass286_7
		if (matrixMode!!.isSceneMode()) {
			this.requestViewPositionUpdate()
		}
		this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE0.toInt()
		this.activeProjectionSource = this.orthoProjectionMatrix
	}

	@OriginalMember(owner = "client!wga", name = "H", descriptor = "(III[I)V")
	final override fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?) {
		@Pc(14) val depth: Float = this.viewMatrix43!!.transformZ(x.toFloat(), z.toFloat(), y.toFloat())
		@Pc(40) val screenX: Int
		@Pc(58) val screenY: Int
		if (depth < -0.0078125f || depth > 0.0078125f) {
			screenX = (this.projectionScaleX.toFloat() * this.viewMatrix43!!.transformX(z.toFloat(), y.toFloat(), x.toFloat()) / depth).toInt()
			screenY = (this.projectionScaleY.toFloat() * this.viewMatrix43!!.transformY(x.toFloat(), z.toFloat(), y.toFloat()) / depth).toInt()
		} else {
			screenY = this.projectionCenterY
			screenX = this.projectionCenterX
		}
		screenCoords!![1] = (screenY.toFloat() - this.viewY1).toInt()
		screenCoords[0] = (screenX.toFloat() - this.viewX1).toInt()
		screenCoords[2] = depth.toInt()
	}

	@OriginalMember(owner = "client!wga", name = "I", descriptor = "(I)V")
	fun drawQuadTriangleFan() {
		this.drawQuadPrimitive(PrimitiveType.aClass134_3, 2)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIII)V")
	final override fun outlineRect(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		@Pc(7) val pixelOffset: Float = this.getPixelOffset()
		this.setupFlatUiRenderState()
		this.setColourValue(colour)
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.toggleBlending(mode)
		this.modelMatrix43.setScale(1.0f, (height - 1).toFloat(), (width - 1).toFloat())
		this.modelMatrix43.translate(y.toFloat() - pixelOffset, 0.0f, x.toFloat() - pixelOffset)
		this.commitModelMatrix()
		this.toggleMultisampling(false)
		this.drawQuadPrimitive(PrimitiveType.aClass134_5, 4)
		this.toggleMultisampling(true)
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!wga", name = "J", descriptor = "(I)Lclient!haa;")
	fun getModelMatrix(): NativeMatrix {
		return this.modelMatrix43
	}

	@OriginalMember(owner = "client!wga", name = "K", descriptor = "(I)V")
	abstract fun setTextureAlphaStage()

	@OriginalMember(owner = "client!wga", name = "XA", descriptor = "()I")
	final override fun XA(): Int {
		return this.camFar
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(II)I")
	final override fun combineFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		return maskB or maskA
	}

	@OriginalMember(owner = "client!wga", name = "D", descriptor = "(B)V")
	abstract fun setViewport()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	abstract fun drawIndexedPrimitive(@OriginalArg(0) drawMode: PrimitiveType?, @OriginalArg(1) primitiveCount: Int, @OriginalArg(2) indexBuffer: GpuIndexBuffer?, @OriginalArg(3) minIndex: Int, @OriginalArg(4) startIndex: Int, @OriginalArg(5) vertexCount: Int)

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(II)I")
	final override fun compareFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		return (maskB and maskA) xor maskB
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "(B)V")
	abstract fun toggleAlpha()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	final override fun createFontRenderer(@OriginalArg(0) metrics: FontMetrics?, @OriginalArg(1) image: Array<SpriteType?>?): AbstractFontRenderer {
		return NativeFont(this, metrics, image, true)
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[Lclient!ab;)V")
	final override fun copyLights(@OriginalArg(0) lastSceneLightCount: Int, @OriginalArg(1) pointLights: Array<PointLight?>?) {
		for (@Pc(3) i in 0 until lastSceneLightCount) {
			this.lightSources!![i] = pointLights!![i]
		}
		this.lastSceneLightCount = lastSceneLightCount
		if (this.matrixMode!!.isSceneMode()) {
			this.updateSceneLights()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "()Z")
	final override fun supportsBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(I)Lclient!za;")
	final override fun createHeap(@OriginalArg(0) size: Int): MemoryPool {
		@Pc(8) val nativeMemoryPool: NativeMemoryPool = NativeMemoryPool(size)
		this.activeNativeMemory.addLast(nativeMemoryPool)
		return nativeMemoryPool
	}

	@OriginalMember(owner = "client!wga", name = "M", descriptor = "()I")
	final override fun M(): Int {
		return this.modelCount
	}

	@OriginalMember(owner = "client!wga", name = "M", descriptor = "(I)V")
	abstract fun setTextureMatrix()

	@OriginalMember(owner = "client!wga", name = "N", descriptor = "(I)I")
	fun getWaterHeight(): Int {
		return this.waterHeight
	}

	@OriginalMember(owner = "client!wga", name = "O", descriptor = "(I)V")
	fun resetModelMatrix() {
		this.modelMatrix43.makeIdentity()
		this.isModelMatrixIdentity = true
		this.onModelMatrixChanged()
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	final override fun createPointLight(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) range: Int, @OriginalArg(5) intensity: Float): PointLight {
		return GpuPointLight(x, y, z, radius, range, intensity)
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(ZI)V")
	fun setForceLightsDisabled(@OriginalArg(0) disabled: Boolean) {
		if (disabled != this.forceLightsDisabled) {
			this.forceLightsDisabled = disabled
			this.toggleLights()
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([BILclient!wq;IZI)Lclient!wfa;")
	fun createTextureSampler(@OriginalArg(0) pixels: ByteArray?, @OriginalArg(1) width: Int, @OriginalArg(2) format: PixelFormat?, @OriginalArg(4) mipmapped: Boolean, @OriginalArg(5) height: Int): GpuTexture? {
		return this.createNativeTextureSampler(height, width, pixels, format, mipmapped)
	}

	@OriginalMember(owner = "client!wga", name = "P", descriptor = "(I)V")
	abstract fun setTextureEnvironmentColor()

	@OriginalMember(owner = "client!wga", name = "s", descriptor = "()Z")
	final override fun utilisesNativeHeap(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(IZ)V")
	fun setDepthMaskEnabled(@OriginalArg(1) enabled: Boolean) {
		if (enabled != this.depthMaskEnabled) {
			this.depthMaskEnabled = enabled
			this.setDepthRenderState()
			this.nativeFunctionMask = this.nativeFunctionMask and 0xFFFFFFE0.toInt()
		}
	}

	@OriginalMember(owner = "client!wga", name = "Q", descriptor = "(I)V")
	abstract fun toggleBlending()

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!mda;)V")
	final override fun render(@OriginalArg(0) particleList: ParticleList?) {
		this.particleRenderer!!.render(this, particleList, -1)
	}

	@OriginalMember(owner = "client!wga", name = "R", descriptor = "(I)Lclient!haa;")
	fun calibrateViewPosition(): NativeMatrix {
		if (!this.viewPositionCalibrated) {
			this.combinedModelViewMatrix43.multiply(this.flippedViewMatrix, this.modelMatrix43)
			this.viewPositionCalibrated = true
		}
		return this.combinedModelViewMatrix43
	}

	@OriginalMember(owner = "client!wga", name = "F", descriptor = "(B)V")
	private fun loadOrthoSceneProjectionMatrix() {
		if (this.isOrthoSceneProjectionValid) {
			return
		}
		@Pc(9) val matrix: FloatArray = this.orthoSceneProjectionMatrix
		@Pc(13) val screenWidth: Float = this.camNear.toFloat()
		@Pc(17) val screenHeight: Float = this.camFar.toFloat()
		@Pc(29) val top: Float = this.orthoZoom * (-this.projectionCenterY).toFloat() / this.projectionScaleY.toFloat()
		@Pc(41) val left: Float = (-this.projectionCenterX).toFloat() * this.orthoZoom / this.projectionScaleX.toFloat()
		@Pc(56) val right: Float = this.orthoZoom * (this.surfaceWidth - this.projectionCenterX).toFloat() / this.projectionScaleX.toFloat()
		@Pc(71) val bottom: Float = (this.surfaceHeight - this.projectionCenterY).toFloat() * this.orthoZoom / this.projectionScaleY.toFloat()
		if (right == left || top == bottom) {
			matrix[11] = 0.0f
			matrix[8] = 0.0f
			matrix[7] = 0.0f
			matrix[10] = 1.0f
			matrix[15] = 1.0f
			matrix[9] = 0.0f
			matrix[0] = 1.0f
			matrix[1] = 0.0f
			matrix[13] = 0.0f
			matrix[4] = 0.0f
			matrix[6] = 0.0f
			matrix[12] = 0.0f
			matrix[5] = 1.0f
			matrix[2] = 0.0f
			matrix[14] = 0.0f
			matrix[3] = 0.0f
		} else {
			matrix[0] = 2.0f / (right - left)
			matrix[12] = (right + left) / (left - right)
			matrix[6] = 0.0f
			matrix[3] = 0.0f
			matrix[11] = 0.0f
			matrix[8] = 0.0f
			matrix[14] = screenWidth / (screenWidth - screenHeight)
			matrix[1] = 0.0f
			matrix[4] = 0.0f
			matrix[2] = 0.0f
			matrix[9] = 0.0f
			matrix[13] = (top + bottom) / (bottom - top)
			matrix[10] = 1.0f / (screenWidth - screenHeight)
			matrix[5] = 2.0f / (bottom - top)
			matrix[7] = 0.0f
			matrix[15] = 1.0f
		}
		this.updateOrthoFar()
		this.isOrthoSceneProjectionValid = true
	}

	@OriginalMember(owner = "client!wga", name = "S", descriptor = "(I)V")
	abstract fun blendDrawBuffer()
}
