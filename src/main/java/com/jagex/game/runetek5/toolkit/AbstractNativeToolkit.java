package com.jagex.game.runetek5.toolkit;

import com.jagex.game.runetek5.config.material.NativeMaterialCache;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.BlendMode;
import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.MatrixMode;
import com.jagex.game.runetek5.TextureCombineFunction;
import com.jagex.game.runetek5.TexCoordGenMode;
import com.jagex.game.runetek5.TextureTransformMode;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.VertexAttribute;
import com.jagex.game.runetek5.NativeCubeTextureGenerator;
import com.jagex.game.runetek5.toolkit.MaskedTextureSurface;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.FixedFunctionEnvironmentMapPass;
import com.jagex.game.runetek5.FixedFunctionSpecularPass;
import com.jagex.game.runetek5.FixedFunctionWaterPass;
import com.jagex.game.runetek5.graphics.font.NativeFont;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.CubeTextureSampler;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.NoEffectPass;
import com.jagex.game.runetek5.cache.node.GpuPointLight;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.RenderPass;
import com.jagex.game.runetek5.graphics.sprite.GpuSprite;
import com.jagex.game.runetek5.ChatLineCount;
import com.jagex.game.runetek5.UnlitPass;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.cache.node.MemoryPool;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.statics.ZoneLocRotation;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeap;
import jaclib.memory.heap.NativeHeapBuffer;
import jagex3.graphics2.hw.NativeInterface;
import java.awt.Canvas;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.NativeMemoryPool;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.model.GpuModel;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.scene.ground.GpuFloorModel;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import com.jagex.game.runetek5.graphics.GpuEnvironmentSampler;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.NativeParticleRenderer;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.graphics.texture.TextureState;
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode;
import com.jagex.game.runetek5.toolkit.companion.PrimitiveDrawMode;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction;
import com.jagex.game.runetek5.utils.ColorUtil;

@OriginalClass("client!wga")
public abstract class AbstractNativeToolkit extends Toolkit {

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	public NativeHeap nativeHeap;

	@OriginalMember(owner = "client!wga", name = "peer", descriptor = "J")
	private long peer;

	@OriginalMember(owner = "client!wga", name = "xb", descriptor = "Lclient!ur;")
	public NativeWaterTexture waterTexture;

	@OriginalMember(owner = "client!wga", name = "ic", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	public NativeHeapBuffer stagingBuffer;

	@OriginalMember(owner = "client!wga", name = "Wc", descriptor = "Ljava/util/Hashtable;")
	private Hashtable surfacesByCanvas;

	@OriginalMember(owner = "client!wga", name = "Sd", descriptor = "I")
	public int texturesSizeOccupied;

	@OriginalMember(owner = "client!wga", name = "Td", descriptor = "I")
	public int textureBuffersSizeOccupied;

	@OriginalMember(owner = "client!wga", name = "je", descriptor = "I")
	public int renderBufferSizeOccupied;

	@OriginalMember(owner = "client!wga", name = "qe", descriptor = "[Lclient!sia;")
	public TextureCombineFunction[] textureRgbCombineModes;

	@OriginalMember(owner = "client!wga", name = "ue", descriptor = "I")
	public int maxConcurrentTextures;

	@OriginalMember(owner = "client!wga", name = "we", descriptor = "[Lclient!sia;")
	public TextureCombineFunction[] textureAlphaCombineModes;

	@OriginalMember(owner = "client!wga", name = "Ce", descriptor = "[Lclient!du;")
	private TextureSampler[] textureSamplers;

	@OriginalMember(owner = "client!wga", name = "Ee", descriptor = "I")
	private int nativeFunctionMask;

	@OriginalMember(owner = "client!wga", name = "Fe", descriptor = "Z")
	public boolean cubeMapSupported;

	@OriginalMember(owner = "client!wga", name = "He", descriptor = "F")
	public float viewY1;

	@OriginalMember(owner = "client!wga", name = "Re", descriptor = "[Lclient!tr;")
	public TextureTransformMode[] textureStates;

	@OriginalMember(owner = "client!wga", name = "Se", descriptor = "I")
	public int shadowOffsetZ;

	@OriginalMember(owner = "client!wga", name = "Te", descriptor = "F")
	public float fogEnd;

	@OriginalMember(owner = "client!wga", name = "Xe", descriptor = "I")
	public int envColor;

	@OriginalMember(owner = "client!wga", name = "af", descriptor = "I")
	public int currentSceneLightCount;

	@OriginalMember(owner = "client!wga", name = "bf", descriptor = "Z")
	public boolean fogEnabled;

	@OriginalMember(owner = "client!wga", name = "ef", descriptor = "F")
	public float viewX1;

	@OriginalMember(owner = "client!wga", name = "gf", descriptor = "F")
	private float perspectiveDepthOffset;

	@OriginalMember(owner = "client!wga", name = "kf", descriptor = "[Lclient!haa;")
	public NativeMatrix[] textureMatrices;

	@OriginalMember(owner = "client!wga", name = "lf", descriptor = "[Lclient!ab;")
	public PointLight[] lightSources;

	@OriginalMember(owner = "client!wga", name = "mf", descriptor = "I")
	public int shadowOffsetX;

	@OriginalMember(owner = "client!wga", name = "pf", descriptor = "Lclient!ve;")
	private NativeParticleRenderer particleRenderer;

	@OriginalMember(owner = "client!wga", name = "rf", descriptor = "I")
	public int maxActiveLights;

	@OriginalMember(owner = "client!wga", name = "wf", descriptor = "Lclient!fj;")
	private RenderPass activeWaterShader;

	@OriginalMember(owner = "client!wga", name = "zf", descriptor = "I")
	public int animatingCycle;

	@OriginalMember(owner = "client!wga", name = "Af", descriptor = "Lclient!du;")
	public TextureSampler defaultTexture;

	@OriginalMember(owner = "client!wga", name = "Ef", descriptor = "I")
	public int lastSceneLightCount;

	@OriginalMember(owner = "client!wga", name = "Lf", descriptor = "F")
	public float viewX2;

	@OriginalMember(owner = "client!wga", name = "Mf", descriptor = "Z")
	public boolean SUPPORTS_TEXTURE3D;

	@OriginalMember(owner = "client!wga", name = "Nf", descriptor = "Z")
	public boolean missingWaterColor;

	@OriginalMember(owner = "client!wga", name = "Pf", descriptor = "I")
	private int waterHeight;

	@OriginalMember(owner = "client!wga", name = "Tf", descriptor = "F")
	private float perspectiveDepthScale;

	@OriginalMember(owner = "client!wga", name = "Yf", descriptor = "Lclient!vc;")
	private GpuEnvironmentSampler sunCubeTextureGenerator;

	@OriginalMember(owner = "client!wga", name = "ag", descriptor = "F")
	public float fogStart;

	@OriginalMember(owner = "client!wga", name = "jg", descriptor = "Z")
	public boolean GLARBMultisample;

	@OriginalMember(owner = "client!wga", name = "mg", descriptor = "F")
	public float viewY2;

	@OriginalMember(owner = "client!wga", name = "og", descriptor = "Lclient!nca;")
	public GpuModel copyType3Scratch;

	@OriginalMember(owner = "client!wga", name = "pg", descriptor = "Lclient!nca;")
	public GpuModel copyType4Result;

	@OriginalMember(owner = "client!wga", name = "qg", descriptor = "Lclient!nca;")
	public GpuModel copyType5Scratch;

	@OriginalMember(owner = "client!wga", name = "rg", descriptor = "Lclient!np;")
	private VertexDeclaration circleVertexDeclaration;

	@OriginalMember(owner = "client!wga", name = "sg", descriptor = "Lclient!np;")
	public VertexDeclaration positionColorTexCoordDeclaration;

	@OriginalMember(owner = "client!wga", name = "tg", descriptor = "Lclient!nca;")
	public GpuModel copyType2Result;

	@OriginalMember(owner = "client!wga", name = "ug", descriptor = "Lclient!tc;")
	private NativeStream quadVertexStream;

	@OriginalMember(owner = "client!wga", name = "vg", descriptor = "Lclient!nca;")
	public GpuModel copyType2Scratch;

	@OriginalMember(owner = "client!wga", name = "wg", descriptor = "Lclient!uq;")
	private GpuIndexBuffer indexBuffer;

	@OriginalMember(owner = "client!wga", name = "yg", descriptor = "Lclient!np;")
	private VertexDeclaration quadVertexDeclaration;

	@OriginalMember(owner = "client!wga", name = "zg", descriptor = "Lclient!nca;")
	public GpuModel copyType1Scratch;

	@OriginalMember(owner = "client!wga", name = "Ag", descriptor = "Lclient!nca;")
	public GpuModel copyType4Scratch;

	@OriginalMember(owner = "client!wga", name = "Bg", descriptor = "Lclient!nca;")
	public GpuModel copyType3Result;

	@OriginalMember(owner = "client!wga", name = "Cg", descriptor = "Lclient!tc;")
	private NativeStream circleVertexStream;

	@OriginalMember(owner = "client!wga", name = "Dg", descriptor = "Lclient!np;")
	public VertexDeclaration positionColorDeclaration;

	@OriginalMember(owner = "client!wga", name = "Eg", descriptor = "Lclient!tc;")
	private NativeStream vertexStream;

	@OriginalMember(owner = "client!wga", name = "Fg", descriptor = "Lclient!np;")
	public VertexDeclaration positionTexCoordDeclaration;

	@OriginalMember(owner = "client!wga", name = "Gg", descriptor = "Lclient!np;")
	private VertexDeclaration vertexDeclaration;

	@OriginalMember(owner = "client!wga", name = "Hg", descriptor = "Lclient!nca;")
	public GpuModel copyType1Result;

	@OriginalMember(owner = "client!wga", name = "Ig", descriptor = "Lclient!nca;")
	public GpuModel copyType5Result;

	@OriginalMember(owner = "client!wga", name = "Jg", descriptor = "Lclient!np;")
	public VertexDeclaration positionColorTexCoordNormalDeclaration;

	@OriginalMember(owner = "client!wga", name = "Kg", descriptor = "Z")
	public boolean boundsCalculated;

	@OriginalMember(owner = "client!wga", name = "Lg", descriptor = "I")
	private int modelCount;

	@OriginalMember(owner = "client!wga", name = "nb", descriptor = "Lclient!uh;")
	private final Deque activeNativeMemory = new Deque();

	@OriginalMember(owner = "client!wga", name = "Zd", descriptor = "Z")
	public boolean isModelMatrixIdentity = true;

	@OriginalMember(owner = "client!wga", name = "ee", descriptor = "Lclient!haa;")
	public final NativeMatrix modelMatrix43 = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "ke", descriptor = "Lclient!haa;")
	public NativeMatrix viewMatrix43 = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "me", descriptor = "Lclient!haa;")
	public final NativeMatrix inverseSunMatrix43 = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "ne", descriptor = "Lclient!haa;")
	public final NativeMatrix flippedViewMatrix = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "oe", descriptor = "Lclient!haa;")
	private final NativeMatrix inverseSunMatrix44 = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "pe", descriptor = "Lclient!haa;")
	private final NativeMatrix combinedModelViewMatrix43 = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "xe", descriptor = "F")
	public float intensity = 1.0F;

	@OriginalMember(owner = "client!wga", name = "De", descriptor = "I")
	private int waterColour = -1;

	@OriginalMember(owner = "client!wga", name = "te", descriptor = "F")
	public float orthoFar = 3584.0F;

	@OriginalMember(owner = "client!wga", name = "ze", descriptor = "I")
	public int camNear = 50;

	@OriginalMember(owner = "client!wga", name = "ye", descriptor = "I")
	public int projectionCenterX = 0;

	@OriginalMember(owner = "client!wga", name = "re", descriptor = "F")
	public float baseLightColorGreen = 1.0F;

	@OriginalMember(owner = "client!wga", name = "Qe", descriptor = "Z")
	private boolean isOrthoSceneProjectionValid = false;

	@OriginalMember(owner = "client!wga", name = "Ae", descriptor = "I")
	public int cBottom = 0;

	@OriginalMember(owner = "client!wga", name = "Je", descriptor = "I")
	public int fogOffset = 0;

	@OriginalMember(owner = "client!wga", name = "hf", descriptor = "I")
	public int projectionScaleX = 512;

	@OriginalMember(owner = "client!wga", name = "We", descriptor = "Z")
	public boolean depthMaskEnabled = false;

	@OriginalMember(owner = "client!wga", name = "Me", descriptor = "I")
	private int activeEffectParam2 = 0;

	@OriginalMember(owner = "client!wga", name = "ve", descriptor = "I")
	public int fogDistance = -1;

	@OriginalMember(owner = "client!wga", name = "Ze", descriptor = "Z")
	private boolean viewPositionCalibrated = false;

	@OriginalMember(owner = "client!wga", name = "Ue", descriptor = "Z")
	public boolean enableBlending = true;

	@OriginalMember(owner = "client!wga", name = "df", descriptor = "I")
	public int activeFlags = 8;

	@OriginalMember(owner = "client!wga", name = "Ge", descriptor = "[F")
	public final float[] lightPosition = new float[] { 0.0F, 0.0F, -1.0F, 0.0F };

	@OriginalMember(owner = "client!wga", name = "vf", descriptor = "[F")
	private final float[] baseLightPosition = new float[] { 0.0F, 0.0F, 1.0F, 0.0F };

	@OriginalMember(owner = "client!wga", name = "Pe", descriptor = "[F")
	private final float[] orthoProjectionMatrix = new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };

	@OriginalMember(owner = "client!wga", name = "jf", descriptor = "I")
	public int projectionCenterY = 0;

	@OriginalMember(owner = "client!wga", name = "Kf", descriptor = "[F")
	private final float[] orthoSceneProjectionMatrix = new float[16];

	@OriginalMember(owner = "client!wga", name = "Oe", descriptor = "Z")
	public boolean sunEnabled = true;

	@OriginalMember(owner = "client!wga", name = "Ne", descriptor = "I")
	public final int viewportOffsetY = 0;

	@OriginalMember(owner = "client!wga", name = "Ye", descriptor = "[F")
	private final float[] screenOrthoMatrix = new float[16];

	@OriginalMember(owner = "client!wga", name = "Ie", descriptor = "I")
	private int activeMaterialId = -1;

	@OriginalMember(owner = "client!wga", name = "cf", descriptor = "Z")
	public boolean depthBufferingEnabled = true;

	@OriginalMember(owner = "client!wga", name = "ff", descriptor = "Z")
	private boolean isOrthoProjectionValid = false;

	@OriginalMember(owner = "client!wga", name = "Jf", descriptor = "I")
	private int activeEffectType = 0;

	@OriginalMember(owner = "client!wga", name = "Gf", descriptor = "Z")
	private boolean activeMaterialUnderwater = false;

	@OriginalMember(owner = "client!wga", name = "xf", descriptor = "I")
	public int fogColorRgb = -1;

	@OriginalMember(owner = "client!wga", name = "Hf", descriptor = "I")
	public int camFar = 3584;

	@OriginalMember(owner = "client!wga", name = "Le", descriptor = "I")
	public int shadowSizeShift = 3;

	@OriginalMember(owner = "client!wga", name = "sf", descriptor = "[Lclient!fj;")
	private final RenderPass[] waterShadersByEffect = new RenderPass[10];

	@OriginalMember(owner = "client!wga", name = "Df", descriptor = "I")
	public int size = 128;

	@OriginalMember(owner = "client!wga", name = "nf", descriptor = "I")
	public int projectionScaleY = 512;

	@OriginalMember(owner = "client!wga", name = "Rf", descriptor = "Z")
	public boolean enableDepthTest = false;

	@OriginalMember(owner = "client!wga", name = "tf", descriptor = "[F")
	private final float[] baseLightInvertedPosition = new float[] { 0.0F, 0.0F, 1.0F, 0.0F };

	@OriginalMember(owner = "client!wga", name = "Wf", descriptor = "F")
	public float perspectiveFar = 3584.0F;

	@OriginalMember(owner = "client!wga", name = "uf", descriptor = "Z")
	public boolean enableAlphaTest = true;

	@OriginalMember(owner = "client!wga", name = "Qf", descriptor = "Lclient!pn;")
	public BlendMode blendFuncMode = BlendFuncMode.ALPHA_BLENDING;

	@OriginalMember(owner = "client!wga", name = "Bf", descriptor = "Z")
	public boolean fogSupported = true;

	@OriginalMember(owner = "client!wga", name = "Be", descriptor = "Z")
	private boolean isModelViewValid = false;

	@OriginalMember(owner = "client!wga", name = "Sf", descriptor = "I")
	public int cTop = 0;

	@OriginalMember(owner = "client!wga", name = "yf", descriptor = "Z")
	public boolean lightingEnabled = false;

	@OriginalMember(owner = "client!wga", name = "Ff", descriptor = "[F")
	private final float[] perspectiveProjectionMatrix = new float[16];

	@OriginalMember(owner = "client!wga", name = "dg", descriptor = "Z")
	private boolean isShutdown = false;

	@OriginalMember(owner = "client!wga", name = "Of", descriptor = "I")
	private int activeEffectParam1 = 0;

	@OriginalMember(owner = "client!wga", name = "Ke", descriptor = "I")
	public int cLeft = 0;

	@OriginalMember(owner = "client!wga", name = "Cf", descriptor = "Z")
	public boolean forceLightsDisabled = false;

	@OriginalMember(owner = "client!wga", name = "If", descriptor = "F")
	private float orthoZoom = 1.0F;

	@OriginalMember(owner = "client!wga", name = "of", descriptor = "[F")
	public float[] activeProjectionSource = this.orthoProjectionMatrix;

	@OriginalMember(owner = "client!wga", name = "Uf", descriptor = "I")
	private int blendFuncType = 1;

	@OriginalMember(owner = "client!wga", name = "gg", descriptor = "I")
	private int waterScale = -1;

	@OriginalMember(owner = "client!wga", name = "cg", descriptor = "I")
	public int currentTextureSlot = 0;

	@OriginalMember(owner = "client!wga", name = "eg", descriptor = "I")
	private int waterIntensity = 0;

	@OriginalMember(owner = "client!wga", name = "Vf", descriptor = "F")
	public float baseLightColorBlue = 1.0F;

	@OriginalMember(owner = "client!wga", name = "bg", descriptor = "F")
	public float sunColorIntensity = -1.0F;

	@OriginalMember(owner = "client!wga", name = "Xf", descriptor = "[F")
	public final float[] sunPosition = new float[] { 0.0F, 0.0F, 1.0F, 0.0F };

	@OriginalMember(owner = "client!wga", name = "ig", descriptor = "I")
	public final int viewportOffsetX = 0;

	@OriginalMember(owner = "client!wga", name = "lg", descriptor = "Lclient!qv;")
	public MatrixMode matrixMode = MatrixMode.aClass286_7;

	@OriginalMember(owner = "client!wga", name = "hg", descriptor = "F")
	public float baseLightColorRed = 1.0F;

	@OriginalMember(owner = "client!wga", name = "ng", descriptor = "F")
	public float antiSunColorIntensity = -1.0F;

	@OriginalMember(owner = "client!wga", name = "Zf", descriptor = "I")
	private int baseLightColor = 16777215;

	@OriginalMember(owner = "client!wga", name = "kg", descriptor = "I")
	public int cRight = 0;

	@OriginalMember(owner = "client!wga", name = "se", descriptor = "Lclient!jaclib/memory/Stream;")
	private final Stream scratchStream = new Stream();

	@OriginalMember(owner = "client!wga", name = "xg", descriptor = "Lclient!haa;")
	private final NativeMatrix scratchMatrix = new NativeMatrix();

	@OriginalMember(owner = "client!wga", name = "Ve", descriptor = "I")
	public final int multiSampleCount;

	@OriginalMember(owner = "client!wga", name = "vd", descriptor = "Lclient!in;")
	public final Js5ResourceProvider shaderResource;

	@OriginalMember(owner = "client!wga", name = "lb", descriptor = "Ljava/awt/Canvas;")
	public final Canvas lb;

	@OriginalMember(owner = "client!wga", name = "W", descriptor = "Ljava/awt/Canvas;")
	private Canvas currentCanvas;

	@OriginalMember(owner = "client!wga", name = "fb", descriptor = "Ljava/lang/Object;")
	public final Object primarySurface;

	@OriginalMember(owner = "client!wga", name = "cd", descriptor = "Ljava/lang/Object;")
	private Object currentSurface;

	@OriginalMember(owner = "client!wga", name = "vc", descriptor = "I")
	private int height;

	@OriginalMember(owner = "client!wga", name = "Wb", descriptor = "I")
	public int surfaceHeight;

	@OriginalMember(owner = "client!wga", name = "Sb", descriptor = "I")
	private int width;

	@OriginalMember(owner = "client!wga", name = "rb", descriptor = "I")
	public int surfaceWidth;

	@OriginalMember(owner = "client!wga", name = "fg", descriptor = "I")
	public final int endiannessType;

	@OriginalMember(owner = "client!wga", name = "kb", descriptor = "Lclient!jagex3/graphics2/hw/NativeInterface;")
	public final NativeInterface nativeInterface;

	@OriginalMember(owner = "client!wga", name = "qf", descriptor = "Lclient!fw;")
	private final NativeMaterialCache materialCache;

	@OriginalMember(owner = "client!wga", name = "<init>", descriptor = "(Ljava/awt/Canvas;Ljava/lang/Object;Lclient!d;Lclient!in;II)V")
	public AbstractNativeToolkit(@OriginalArg(0) Canvas canvas, @OriginalArg(1) Object object, @OriginalArg(2) MaterialProvider textureSource, @OriginalArg(3) Js5ResourceProvider shaderResource, @OriginalArg(4) int multiSampleCount, @OriginalArg(5) int endiannessType) {
		super(textureSource);
		try {
			this.multiSampleCount = multiSampleCount;
			this.shaderResource = shaderResource;
			this.currentCanvas = this.lb = canvas;
			this.currentSurface = this.primarySurface = object;
			@Pc(307) Dimension dimension = canvas.getSize();
			this.surfaceHeight = this.height = dimension.height;
			this.surfaceWidth = this.width = dimension.width;
			this.endiannessType = endiannessType;
			ColorUtil.initColorTables(false, true);
			if (super.materialProvider == null) {
				this.nativeInterface = new NativeInterface(0, this.endiannessType);
				this.materialCache = null;
			} else {
				this.materialCache = new NativeMaterialCache(this, super.materialProvider);
				this.nativeInterface = new NativeInterface(super.materialProvider.textureCount(), this.endiannessType);
				for (@Pc(364) int i = 0; super.materialProvider.textureCount() > i; i++) {
					@Pc(372) MaterialType textureMetrics = super.materialProvider.getMetrics(i);
					if (textureMetrics != null) {
						this.nativeInterface.initTextureMetrics(i, textureMetrics.alpha, textureMetrics.brightness);
					}
				}
			}
		} catch (@Pc(391) Throwable ex) {
			ex.printStackTrace();
			this.free();
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!wga", name = "da", descriptor = "(III[I)V")
	@Override
	public final void da(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords) {
		@Pc(14) float depth = this.viewMatrix43.transformZ((float) x, (float) z, (float) y);
		@Pc(40) int screenX;
		@Pc(58) int screenY;
		if (depth < -0.0078125F || depth > 0.0078125F) {
			screenX = (int) ((float) this.projectionScaleX * this.viewMatrix43.transformX((float) z, (float) y, (float) x) / depth);
			screenY = (int) ((float) this.projectionScaleY * this.viewMatrix43.transformY((float) x, (float) z, (float) y) / depth);
		} else {
			screenY = this.projectionCenterY;
			screenX = this.projectionCenterX;
		}
		screenCoords[2] = (int) depth;
		screenCoords[0] = (int) ((float) screenX - this.viewX1);
		screenCoords[1] = (int) ((float) screenY - this.viewY1);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!za;)V")
	@Override
	public final void setHeap(@OriginalArg(0) MemoryPool memoryNode) {
		this.nativeHeap = ((NativeMemoryPool) memoryNode).heap;
		this.stagingBuffer = this.nativeHeap.a(32768, false);
	}

	@OriginalMember(owner = "client!wga", name = "JA", descriptor = "(IIIIII)I")
	@Override
	public final int JA(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2) {
		@Pc(7) int clipFlags = 0;
		@Pc(32) float startDepth = (float) y1 * this.viewMatrix43.zB + this.viewMatrix43.zA * (float) x1 + (float) z1 * this.viewMatrix43.zC + this.viewMatrix43.zD;
		if (startDepth < 1.0F) {
			startDepth = 1.0F;
		}
		@Pc(63) float endDepth = this.viewMatrix43.zD + this.viewMatrix43.zB * (float) y2 + this.viewMatrix43.zA * (float) x2 + (float) z2 * this.viewMatrix43.zC;
		if (endDepth < 1.0F) {
			endDepth = 1.0F;
		}
		if ((float) this.camNear > startDepth && (float) this.camNear > endDepth) {
			clipFlags |= 0x10;
		} else if (startDepth > (float) this.camFar && (float) this.camFar < endDepth) {
			clipFlags |= 0x20;
		}
		@Pc(135) int startScreenX = (int) ((this.viewMatrix43.xD + (float) z1 * this.viewMatrix43.xC + (float) x1 * this.viewMatrix43.xA + this.viewMatrix43.xB * (float) y1) * (float) this.projectionScaleX / startDepth);
		@Pc(167) int endScreenX = (int) ((this.viewMatrix43.xD + this.viewMatrix43.xB * (float) y2 + (float) x2 * this.viewMatrix43.xA + (float) z2 * this.viewMatrix43.xC) * (float) this.projectionScaleX / endDepth);
		if ((float) startScreenX < this.viewX1 && (float) endScreenX < this.viewX1) {
			clipFlags |= 0x1;
		} else if (this.viewX2 < (float) startScreenX && (float) endScreenX > this.viewX2) {
			clipFlags |= 0x2;
		}
		@Pc(233) int startScreenY = (int) ((float) this.projectionScaleY * (this.viewMatrix43.yC * (float) z1 + this.viewMatrix43.yB * (float) y1 + (float) x1 * this.viewMatrix43.yA + this.viewMatrix43.yD) / startDepth);
		@Pc(265) int endScreenY = (int) ((float) this.projectionScaleY * ((float) z2 * this.viewMatrix43.yC + (float) y2 * this.viewMatrix43.yB + (float) x2 * this.viewMatrix43.yA + this.viewMatrix43.yD) / endDepth);
		if (this.viewY1 > (float) startScreenY && (float) endScreenY < this.viewY1) {
			clipFlags |= 0x4;
		} else if ((float) startScreenY > this.viewY2 && (float) endScreenY > this.viewY2) {
			clipFlags |= 0x8;
		}
		return clipFlags;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	public abstract VertexDeclaration createVertexDeclaration(@OriginalArg(1) VertexDeclarationComponent[] components);

	@OriginalMember(owner = "client!wga", name = "ZA", descriptor = "(IFFFFF)V")
	@Override
	public final void ZA(@OriginalArg(0) int baseLightColor, @OriginalArg(1) float sunIntensity, @OriginalArg(2) float antiSunIntensity, @OriginalArg(3) float x, @OriginalArg(4) float y, @OriginalArg(5) float z) {
		@Pc(15) boolean colourChanged = this.baseLightColor != baseLightColor;
		if (colourChanged || sunIntensity != this.sunColorIntensity || antiSunIntensity != this.antiSunColorIntensity) {
			this.baseLightColor = baseLightColor;
			this.sunColorIntensity = sunIntensity;
			this.antiSunColorIntensity = antiSunIntensity;
			if (colourChanged) {
				this.baseLightColorGreen = (float) (this.baseLightColor & 0xFF00) / 65280.0F;
				this.baseLightColorRed = (float) (this.baseLightColor & 0xFF0000) / 1.671168E7F;
				this.baseLightColorBlue = (float) (this.baseLightColor & 0xFF) / 255.0F;
				this.setFogColorAndIntensity();
			}
			this.nativeInterface.setSunColour(this.baseLightColorRed, this.baseLightColorGreen, this.baseLightColorBlue, sunIntensity, antiSunIntensity);
			this.setSunParameters();
		}
		if (this.baseLightPosition[0] != x || this.baseLightPosition[1] != y || this.baseLightPosition[2] != z) {
			this.baseLightPosition[2] = z;
			this.baseLightPosition[1] = y;
			this.baseLightPosition[0] = x;
			this.baseLightInvertedPosition[2] = -z;
			this.baseLightInvertedPosition[0] = -x;
			this.baseLightInvertedPosition[1] = -y;
			@Pc(153) float inverseLength = (float) (1.0D / Math.sqrt((double) (y * y + x * x + z * z)));
			this.sunPosition[1] = inverseLength * y;
			this.sunPosition[2] = inverseLength * z;
			this.sunPosition[0] = x * inverseLength;
			this.lightPosition[1] = -this.sunPosition[1];
			this.lightPosition[2] = -this.sunPosition[2];
			this.lightPosition[0] = -this.sunPosition[0];
			this.nativeInterface.setSunDirection(this.sunPosition[0], this.sunPosition[1], this.sunPosition[2]);
			this.setLightPosition();
			this.shadowOffsetX = (int) (x * 256.0F / y);
			this.shadowOffsetZ = (int) (z * 256.0F / y);
		}
		this.enableLights();
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(Z)V")
	public abstract void uploadProjectionMatrix();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZIB)V")
	public final void setActiveMaterial(@OriginalArg(0) boolean allowStillwater, @OriginalArg(1) boolean lit, @OriginalArg(2) int materialId) {
		if (materialId != this.activeMaterialId || this.activeMaterialUnderwater != this.missingWaterColor) {
			@Pc(28) GpuTexture gpuTexture = null;
			@Pc(30) int combineMode = 0;
			@Pc(32) byte effectParam1 = 0;
			@Pc(34) int effectParam2 = 0;
			@Pc(41) int effectType = this.missingWaterColor ? 3 : 0;
			if (materialId < 0) {
				this.resetCurrentTextureMatrix();
			} else {
				gpuTexture = this.materialCache.getTexture(materialId);
				@Pc(55) MaterialType textureMetrics = super.materialProvider.getMetrics(materialId);
				if (textureMetrics.speedU == 0 && textureMetrics.speedV == 0) {
					this.resetCurrentTextureMatrix();
				} else {
					@Pc(73) int textureSize = textureMetrics.isSmall ? 64 : 128;
					@Pc(77) int cycleLength = textureSize * 50;
					@Pc(81) NativeMatrix textureMatrix = this.getCurrentTextureMatrix();
					textureMatrix.makeTranslation((float) (this.animatingCycle % cycleLength * textureMetrics.speedV) / (float) cycleLength, 0.0F, (float) (textureMetrics.speedU * (this.animatingCycle % cycleLength)) / (float) cycleLength);
					this.setTextureState(TextureTransformMode.aClass332_6);
				}
				combineMode = textureMetrics.combineMode;
				if (!this.missingWaterColor) {
					effectParam1 = textureMetrics.effectParam1;
					effectType = textureMetrics.effectType;
					effectParam2 = textureMetrics.effectParam2;
				}
			}
			this.setActiveEffect(effectParam1, lit, allowStillwater, effectType, effectParam2);
			if (this.activeWaterShader == null) {
				this.setTextureSampler(gpuTexture);
				this.setColourType(combineMode);
			} else {
				this.activeWaterShader.setBaseTexture(combineMode, gpuTexture);
			}
			this.activeMaterialUnderwater = this.missingWaterColor;
			this.activeMaterialId = materialId;
		}
		this.nativeFunctionMask &= 0xFFFFFFF8;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!haa;)V")
	public final void setModelMatrix(@OriginalArg(1) NativeMatrix matrix) {
		this.modelMatrix43.apply(matrix);
		this.isModelMatrixIdentity = false;
		this.onModelMatrixChanged();
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(B)V")
	private void initializeLineStream() {
		this.vertexStream = this.createStream(true);
		this.vertexStream.create(24, 12);
		this.vertexDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(VertexDeclarationAction.POSITION) });
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(II)V")
	public final void toggleBlending(@OriginalArg(1) int blendFuncType) {
		if (this.blendFuncType == blendFuncType) {
			return;
		}
		@Pc(25) BlendMode blendFuncMode;
		@Pc(27) boolean enableBlending;
		@Pc(23) boolean enableAlphaTest;
		if (blendFuncType == 1) {
			enableAlphaTest = true;
			blendFuncMode = BlendFuncMode.ALPHA_BLENDING;
			enableBlending = true;
		} else if (blendFuncType == 2) {
			blendFuncMode = BlendFuncMode.OPAQUE_BLENDING;
			enableBlending = true;
			enableAlphaTest = false;
		} else if (blendFuncType == 128) {
			enableAlphaTest = true;
			blendFuncMode = BlendFuncMode.COLOR_BLENDING;
			enableBlending = true;
		} else {
			enableBlending = false;
			blendFuncMode = BlendFuncMode.OPAQUE;
			enableAlphaTest = false;
		}
		if (enableAlphaTest != this.enableAlphaTest) {
			this.enableAlphaTest = enableAlphaTest;
			this.toggleAlpha();
		}
		if (enableBlending != this.enableBlending) {
			this.enableBlending = enableBlending;
			this.toggleBlending();
		}
		if (blendFuncMode != this.blendFuncMode) {
			this.blendFuncMode = blendFuncMode;
			this.blendDrawBuffer();
		}
		this.nativeFunctionMask &= 0xFFFFFFE3;
		this.blendFuncType = blendFuncType;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	@Override
	public final ComponentSurface createMask(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] offsets, @OriginalArg(3) int[] widths) {
		return MaskedTextureSurface.create(height, this, offsets, widths, width);
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(Z)V")
	public abstract void toggleSun();

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(B)V")
	public abstract void unbindCurrentTexture();

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(Z)Lclient!haa;")
	public final NativeMatrix getCurrentTextureMatrix() {
		return this.textureMatrices[this.currentTextureSlot];
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(I)V")
	public final void applyOrthoMatrixMode() {
		this.setOrthoMatrixMode();
		this.calculateViewRange();
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(II)Lclient!uq;")
	public final GpuIndexBuffer getOrResizeIndexBuffer(@OriginalArg(1) int requiredCapacity) {
		if (requiredCapacity * 2 > this.indexBuffer.getId()) {
			this.indexBuffer.allocate(requiredCapacity);
		}
		return this.indexBuffer;
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(B)V")
	public void updateSceneLights() {
		this.currentSceneLightCount = this.lastSceneLightCount;
		this.lastSceneLightCount = 0;
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "()Z")
	@Override
	public final boolean supportsAntiAliasing() {
		return this.GLARBMultisample;
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "()Z")
	@Override
	public final boolean method7311() {
		return false;
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(I)V")
	public final void setupSceneRenderState() {
		if (this.nativeFunctionMask == 8) {
			return;
		}
		this.setModelViewMatrixMode();
		this.toggleFog(true);
		this.toggleDepthTest(true);
		this.setDepthMaskEnabled(true);
		this.toggleBlending(1);
		this.nativeFunctionMask = 8;
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		if (this.isShutdown) {
			return;
		}
		for (@Pc(13) Node node = this.activeNativeMemory.first(); node != null; node = this.activeNativeMemory.next()) {
			((NativeMemoryPool) node).deallocate();
		}
		@Pc(30) Enumeration enumeration = this.surfacesByCanvas.keys();
		while (enumeration.hasMoreElements()) {
			@Pc(36) Canvas canvas = (Canvas) enumeration.nextElement();
			this.destroySurface(canvas, this.surfacesByCanvas.get(canvas));
		}
		ColorUtil.releaseColorTables(true, false);
		this.nativeInterface.release();
		this.isShutdown = true;
	}

	@OriginalMember(owner = "client!wga", name = "HA", descriptor = "(IIII[I)V")
	@Override
	public final void HA(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int zoom, @OriginalArg(4) int[] screenCoords) {
		@Pc(14) float depth = this.viewMatrix43.transformZ((float) x, (float) z, (float) y);
		if ((float) this.camNear > depth || (float) this.camFar < depth) {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
			return;
		}
		@Pc(58) int screenX = (int) ((float) this.projectionScaleX * this.viewMatrix43.transformX((float) z, (float) y, (float) x) / (float) zoom);
		@Pc(77) int screenY = (int) ((float) this.projectionScaleY * this.viewMatrix43.transformY((float) x, (float) z, (float) y) / (float) zoom);
		screenCoords[2] = (int) depth;
		screenCoords[1] = (int) ((float) screenY - this.viewY1);
		screenCoords[0] = (int) ((float) screenX - this.viewX1);
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "()Z")
	@Override
	public final boolean extendBuildArea() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(B)I")
	public final int getWaterIntensity() {
		return this.waterIntensity;
	}

	@OriginalMember(owner = "client!wga", name = "Q", descriptor = "(IIIIII[BII)V")
	@Override
	public final void Q(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int backgroundColour, @OriginalArg(5) int foregroundColour, @OriginalArg(6) byte[] mask, @OriginalArg(7) int maskWidth, @OriginalArg(8) int mode) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public final void setCamera(@OriginalArg(0) Matrix matrix) {
		this.viewMatrix43 = (NativeMatrix) matrix;
		this.flippedViewMatrix.apply(this.viewMatrix43);
		this.flippedViewMatrix.negateYZ();
		this.inverseSunMatrix44.setInverse(this.flippedViewMatrix);
		this.inverseSunMatrix43.setInverse(this.viewMatrix43);
		if (this.matrixMode.isSceneMode()) {
			this.requestViewPositionUpdate();
		}
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(B)V")
	public abstract void toggleFog();

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(Z)V")
	private void initializeQuadStream() {
		this.quadVertexStream = this.createStream(false);
		this.quadVertexStream.create(140, 28);
		for (@Pc(20) int i = 0; i < 4; i++) {
			@Pc(28) Buffer buffer = this.quadVertexStream.lock();
			if (buffer != null) {
				@Pc(35) Stream stream = this.wrapBuffer(buffer);
				if (Stream.c()) {
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(1.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
					stream.a(0.0F);
				} else {
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(1.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
					stream.b(0.0F);
				}
				stream.a();
				if (this.quadVertexStream.unlock()) {
					break;
				}
			}
		}
		this.quadVertexDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.TEXCOORD }) });
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	public abstract void setTextureAlphaStage(@OriginalArg(0) TextureCombineSource state, @OriginalArg(1) int sourceIndex);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	public abstract GpuTexture createTextureSampler(@OriginalArg(0) int width, @OriginalArg(1) int[] pixels, @OriginalArg(2) boolean mipmapped, @OriginalArg(4) int offset, @OriginalArg(5) int rowLength, @OriginalArg(6) int height);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I)V")
	@Override
	public final void allocateThreads(@OriginalArg(0) int threadCount) {
		if (threadCount != 1) {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(B)V")
	private void invalidateModelView() {
		this.isModelViewValid = false;
		this.markModelViewValid();
		if (MatrixMode.aClass286_6 == this.matrixMode) {
			this.calculateViewRange();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IB)Lclient!fj;")
	public RenderPass createRenderPass(@OriginalArg(0) int effect) {
		if (effect == 6) {
			return new UnlitPass(this);
		} else if (effect == 1) {
			return new FixedFunctionSpecularPass(this);
		} else if (effect == 2) {
			return new FixedFunctionWaterPass(this, this.waterTexture);
		} else if (effect == 7) {
			return new FixedFunctionEnvironmentMapPass(this);
		} else {
			return new NoEffectPass(this);
		}
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(B)I")
	public final int getWaterScale() {
		return this.waterScale;
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(I)Lclient!nj;")
	public final CubeTextureSampler getEnvironmentMapSampler() {
		return this.sunCubeTextureGenerator == null ? null : this.sunCubeTextureGenerator.getNativeCubeTexture();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([FI)[F")
	public final float[] getTransposedProjectionMatrix(@OriginalArg(0) float[] dest) {
		dest[0] = this.activeProjectionSource[0];
		dest[4] = this.activeProjectionSource[1];
		dest[12] = this.activeProjectionSource[3];
		dest[8] = this.activeProjectionSource[2];
		dest[9] = this.activeProjectionSource[6];
		dest[13] = this.activeProjectionSource[7];
		dest[2] = this.activeProjectionSource[8];
		dest[1] = this.activeProjectionSource[4];
		dest[5] = this.activeProjectionSource[5];
		dest[10] = this.activeProjectionSource[10];
		dest[14] = this.activeProjectionSource[11];
		dest[3] = this.activeProjectionSource[12];
		dest[6] = this.activeProjectionSource[9];
		dest[7] = this.activeProjectionSource[13];
		dest[15] = this.activeProjectionSource[15];
		dest[11] = this.activeProjectionSource[14];
		return dest;
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(I)V")
	private void onModelMatrixChanged() {
		if (MatrixMode.aClass286_5 == this.matrixMode) {
			@Pc(7) float pixelOffset = this.getPixelOffset();
			this.modelMatrix43.translate(pixelOffset, 0.0F, pixelOffset);
		}
		this.viewPositionCalibrated = false;
		this.uploadModelMatrix();
		if (this.activeWaterShader != null) {
			this.activeWaterShader.onModelMatrixChanged();
		}
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(Z)[F")
	public final float[] getOrthoProjectionMatrix() {
		return this.orthoProjectionMatrix;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZ)V")
	public final void toggleFog(@OriginalArg(1) boolean enabled) {
		if (this.fogEnabled != enabled) {
			this.fogEnabled = enabled;
			this.toggleFog();
			this.nativeFunctionMask &= 0xFFFFFFE0;
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIZ[I)Lclient!wfa;")
	public final GpuTexture createTexture(@OriginalArg(0) int height, @OriginalArg(1) int width, @OriginalArg(3) boolean mipmapped, @OriginalArg(4) int[] pixels) {
		return this.createTextureSampler(width, pixels, mipmapped, 0, 0, height);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZ)Lclient!uq;")
	public abstract GpuIndexBuffer createIndexBuffer(@OriginalArg(1) boolean dynamic);

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(B)V")
	public abstract void setDepthRenderState();

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(Z)V")
	public abstract void setSunParameters();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	public abstract GpuTexture createNativeTextureSampler(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(4) byte[] pixels, @OriginalArg(5) PixelFormat format, @OriginalArg(7) boolean mipmapped);

	@OriginalMember(owner = "client!wga", name = "X", descriptor = "(I)V")
	@Override
	public final void X(@OriginalArg(0) int shadowSize) {
		this.shadowSizeShift = 0;
		while (shadowSize > 1) {
			this.shadowSizeShift++;
			shadowSize >>= 0x1;
		}
		this.activeFlags = 0x1 << this.shadowSizeShift;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	public abstract void resizeSurface(@OriginalArg(1) Canvas canvas, @OriginalArg(2) Object surface);

	@OriginalMember(owner = "client!wga", name = "aa", descriptor = "(IIIIII)V")
	@Override
	public final void aa(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		this.setupFlatUiRenderState();
		this.setColourValue(colour);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.toggleBlending(mode);
		this.modelMatrix43.setScale(1.0F, (float) height, (float) width);
		this.modelMatrix43.translate(x, y, 0);
		this.commitModelMatrix();
		this.toggleMultisampling(false);
		this.drawQuadTriangleFan();
		this.toggleMultisampling(true);
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(B)V")
	private void setProjectionMatrixMode() {
		if (MatrixMode.aClass286_5 == this.matrixMode) {
			return;
		}
		@Pc(6) MatrixMode matrixMode = this.matrixMode;
		this.matrixMode = MatrixMode.aClass286_5;
		if (matrixMode.isSceneMode()) {
			this.requestViewPositionUpdate();
		}
		this.loadWorldViewProjectionMatrix();
		this.activeProjectionSource = this.screenOrthoMatrix;
		this.calculateViewRange();
		this.nativeFunctionMask &= 0xFFFFFFE7;
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(B)V")
	private void invalidateOrthoSceneProjection() {
		this.isOrthoSceneProjectionValid = false;
		this.loadOrthoSceneProjectionMatrix();
		if (MatrixMode.aClass286_1 == this.matrixMode) {
			this.calculateViewRange();
		}
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(I)V")
	private void setOrthoSceneMatrixMode() {
		if (MatrixMode.aClass286_1 == this.matrixMode) {
			return;
		}
		@Pc(11) MatrixMode matrixMode = this.matrixMode;
		this.matrixMode = MatrixMode.aClass286_1;
		if (!matrixMode.isSceneMode()) {
			this.requestViewPositionUpdate();
		}
		this.loadOrthoSceneProjectionMatrix();
		this.activeProjectionSource = this.orthoSceneProjectionMatrix;
		this.calculateViewRange();
		this.nativeFunctionMask &= 0xFFFFFFF8;
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(I)Lclient!haa;")
	public final NativeMatrix getTextureMatrix() {
		return this.textureMatrices[this.currentTextureSlot];
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BILclient!kf;)V")
	public final void setTextureRgbStageState(@OriginalArg(1) int sourceIndex, @OriginalArg(2) TextureCombineSource state) {
		this.setTextureColourSource(false, sourceIndex, state, false);
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(IIIIII)V")
	@Override
	public final void line(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		@Pc(10) float dx = (float) -x1 + (float) x2;
		@Pc(16) float dy = (float) y2 - (float) y1;
		if (dx == 0.0F && dy == 0.0F) {
			dx = 1.0F;
		} else {
			@Pc(37) float inverseLength = (float) (1.0D / Math.sqrt((double) (dy * dy + dx * dx)));
			dy *= inverseLength;
			dx *= inverseLength;
		}
		if (!this.uploadLineVertices((float) y1, (float) x2 + dx, 0.0F, dy + (float) y2, 0.0F, (float) x1)) {
			return;
		}
		this.setupFlatUiRenderState();
		this.setColourValue(colour);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.toggleBlending(mode);
		this.resetModelMatrix();
		this.toggleMultisampling(false);
		this.drawPrimitiveLines();
		this.toggleMultisampling(true);
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BLclient!du;)V")
	public final void setTextureSampler(@OriginalArg(1) TextureSampler textureSampler) {
		if (this.textureSamplers[this.currentTextureSlot] == textureSampler) {
			return;
		}
		this.textureSamplers[this.currentTextureSlot] = textureSampler;
		if (textureSampler == null) {
			this.unbindCurrentTexture();
		} else {
			textureSampler.bindTexture();
		}
		this.nativeFunctionMask &= 0xFFFFFFFE;
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "()Lclient!bca;")
	@Override
	public final Matrix camera() {
		return this.viewMatrix43;
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(ILclient!aa;II)V")
	@Override
	public final void A(@OriginalArg(0) int colour, @OriginalArg(1) ComponentSurface clippingMask, @OriginalArg(2) int x, @OriginalArg(3) int y) {
		@Pc(6) MaskedTextureSurface maskedSurface = (MaskedTextureSurface) clippingMask;
		@Pc(9) GpuTexture gpuTexture = maskedSurface.nativeTextureSampler;
		this.setupUiRenderState();
		this.setTextureSampler(gpuTexture);
		this.toggleBlending(1);
		this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setColourValue(colour);
		this.modelMatrix43.setScale(0.0F, (float) this.surfaceHeight, (float) this.surfaceWidth);
		this.commitModelMatrix();
		this.textureMatrices[0].setScale(1.0F, gpuTexture.toTextureCoordT((float) this.surfaceHeight), gpuTexture.toTextureCoordS((float) this.surfaceWidth));
		this.textureMatrices[0].translate(gpuTexture.toTextureCoordT((float) -y), 0.0F, gpuTexture.toTextureCoordS((float) -x));
		this.textureStates[0] = TextureTransformMode.aClass332_6;
		this.applyTextureMatrix();
		this.drawQuadTriangleFan();
		this.resetCurrentTextureMatrix();
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	@Override
	public final Model createModel(@OriginalArg(0) Mesh mesh, @OriginalArg(1) int functionMask, @OriginalArg(2) int featureMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast) {
		return new GpuModel(this, mesh, functionMask, ambient, contrast, featureMask);
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public final void resizeCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(5) Object object = null;
		if (canvas == null || canvas == this.lb) {
			object = this.currentSurface;
		} else if (this.surfacesByCanvas.containsKey(canvas)) {
			object = this.surfacesByCanvas.get(canvas);
		}
		if (object == null) {
			throw new RuntimeException();
		}
		this.resizeSurface(canvas, object);
		if (canvas == this.currentCanvas) {
			this.updateSurfaceSize();
		}
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(I)V")
	public abstract void configureFog();

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "()Z")
	@Override
	public final boolean replaceCanvasOnDisplayUpdate() {
		return false;
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(IZ)V")
	public final void setFogSupported(@OriginalArg(1) boolean supported) {
		if (supported != this.fogSupported) {
			this.fogSupported = supported;
			this.toggleFog();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!tr;)V")
	public final void setTextureState(@OriginalArg(1) TextureTransformMode textureState) {
		this.textureStates[this.currentTextureSlot] = textureState;
		this.applyTextureMatrix();
	}

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "()Z")
	@Override
	public final boolean forceFixedScreenmode() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(B)Lclient!haa;")
	public final NativeMatrix getInverseSunMatrix44() {
		return this.inverseSunMatrix44;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	public abstract GpuTexture createNativeTexture(@OriginalArg(0) PixelFormat format, @OriginalArg(1) int width, @OriginalArg(3) float[] pixels, @OriginalArg(4) boolean mipmapped, @OriginalArg(7) int height);

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(B)V")
	private void updateFogAndWater() {
		if (this.activeWaterShader != null) {
			this.activeWaterShader.onFogChanged();
		}
		this.configureFog();
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(B)V")
	public final void setupOrthoSceneRenderState() {
		if (this.nativeFunctionMask == 16) {
			return;
		}
		this.setOrthoSceneMatrixMode();
		this.toggleFog(true);
		this.toggleDepthTest(true);
		this.setDepthMaskEnabled(true);
		this.toggleBlending(1);
		this.nativeFunctionMask = 16;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIZ)Lclient!mi;")
	@Override
	public final NativeSprite generateEmptySprite(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) boolean hasAlpha) {
		return new GpuSprite(this, width, height, hasAlpha);
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(Z)F")
	public abstract float getPixelOffset();

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(I)V")
	public abstract void toggleDepthTest();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!hj;II)V")
	private void drawQuadPrimitive(@OriginalArg(0) PrimitiveType drawMode, @OriginalArg(1) int primitiveCount) {
		this.setStreamSource(this.quadVertexStream, 0);
		this.setVertexDeclaration(this.quadVertexDeclaration);
		this.drawPrimitive(primitiveCount, 0, drawMode);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	public abstract GpuTexture createNativeTexture2D(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(3) DataType type, @OriginalArg(4) PixelFormat format);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!np;)V")
	public abstract void setVertexDeclaration(@OriginalArg(1) VertexDeclaration declaration);

	@OriginalMember(owner = "client!wga", name = "q", descriptor = "(I)I")
	public final int getWaterColour() {
		return this.waterColour;
	}

	@OriginalMember(owner = "client!wga", name = "K", descriptor = "([I)V")
	@Override
	public final void K(@OriginalArg(0) int[] clip) {
		clip[0] = this.cLeft;
		clip[3] = this.cBottom;
		clip[2] = this.cRight;
		clip[1] = this.cTop;
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(I)V")
	public final void setupUiRenderState() {
		if (this.nativeFunctionMask == 2) {
			return;
		}
		this.setProjectionMatrixMode();
		this.toggleFog(false);
		this.toggleLights(false);
		this.toggleDepthTest(false);
		this.setDepthMaskEnabled(false);
		this.setActiveMaterial(false, false, -2);
		this.nativeFunctionMask = 2;
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "()Lclient!bca;")
	@Override
	public final Matrix scratchMatrix() {
		return this.scratchMatrix;
	}

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(Z)V")
	@Override
	public final void C(@OriginalArg(0) boolean enabled) {
		this.depthBufferingEnabled = enabled;
		this.setDepthRenderState();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZI)V")
	public final void setSunEnabled(@OriginalArg(0) boolean enabled) {
		if (enabled != this.sunEnabled) {
			this.sunEnabled = enabled;
			this.toggleSun();
		}
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(ZZ)V")
	public final void toggleLights(@OriginalArg(0) boolean enabled) {
		if (this.lightingEnabled != enabled) {
			this.lightingEnabled = enabled;
			this.toggleLights();
			this.nativeFunctionMask &= 0xFFFFFFF8;
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	@Override
	public final void drawTexturedLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY, @OriginalArg(9) int lineSpacing, @OriginalArg(10) int lineLength, @OriginalArg(11) int offset) {
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "()Z")
	@Override
	public final boolean stillwaterEffectLoaded() {
		return this.waterShadersByEffect[3].isSupported();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(FI)V")
	public final void setOrthoZoom(@OriginalArg(0) float zoom) {
		if (zoom != this.orthoZoom) {
			this.orthoZoom = zoom;
			this.invalidateOrthoSceneProjection();
		}
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(Z)V")
	public abstract void activateCurrentTexture();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BZI)Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	public final NativeHeapBuffer generateNativeHeapBuffer(@OriginalArg(1) boolean allocateDirect, @OriginalArg(2) int initialCapacity) {
		return this.nativeHeap.a(initialCapacity, allocateDirect);
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "()I")
	@Override
	public final int E() {
		return this.renderBufferSizeOccupied + this.texturesSizeOccupied + this.textureBuffersSizeOccupied;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!bf;I)V")
	public abstract void setTexCoordGeneration(@OriginalArg(0) TexCoordGenMode mode);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	public abstract Texture3DSampler create3DTexture(@OriginalArg(2) PixelFormat format, @OriginalArg(5) byte[] data);

	@OriginalMember(owner = "client!wga", name = "KA", descriptor = "(IIII)V")
	@Override
	public final void KA(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom) {
		if (left <= 0 && this.surfaceWidth - 1 <= right && top <= 0 && this.surfaceHeight - 1 <= bottom) {
			this.la();
			return;
		}
		this.cTop = top >= 0 ? top : 0;
		this.cLeft = left < 0 ? 0 : left;
		this.cBottom = bottom <= this.surfaceWidth ? bottom : 0;
		this.cRight = right > this.surfaceWidth ? 0 : right;
		if (!this.boundsCalculated) {
			this.boundsCalculated = true;
			this.toggleScissorTest();
		}
		this.setScissorRect();
		this.updateViewBounds();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	@Override
	public final Ground createGround(@OriginalArg(0) int width, @OriginalArg(1) int length, @OriginalArg(2) int[][] tileHeights, @OriginalArg(3) int[][] nextLevelHeights, @OriginalArg(5) int groundFlags, @OriginalArg(6) int featureFlags) {
		return new GpuFloorModel(this, groundFlags, featureFlags, width, length, tileHeights, nextLevelHeights, 512);
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(I)V")
	@Override
	public final void initialize() {
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(I)V")
	@Override
	public final void method7281(@OriginalArg(0) int threadId) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!sia;ILclient!sia;)V")
	public final void setTextureCombineModes(@OriginalArg(0) TextureCombineFunction alphaMode, @OriginalArg(2) TextureCombineFunction rgbMode) {
		@Pc(5) boolean changed = false;
		if (rgbMode != this.textureRgbCombineModes[this.currentTextureSlot]) {
			this.textureRgbCombineModes[this.currentTextureSlot] = rgbMode;
			this.setTextureColorStage();
			changed = true;
		}
		if (this.textureAlphaCombineModes[this.currentTextureSlot] != alphaMode) {
			this.textureAlphaCombineModes[this.currentTextureSlot] = alphaMode;
			changed = true;
			this.setTextureAlphaStage();
		}
		if (changed) {
			this.nativeFunctionMask &= 0xFFFFFFE2;
		}
	}

	@OriginalMember(owner = "client!wga", name = "h", descriptor = "(I)V")
	@Override
	public final void setTextureSize(@OriginalArg(0) int size) {
		if (size < 128 || size > 1024) {
			throw new IllegalArgumentException();
		}
		if (this.materialCache != null) {
			this.materialCache.reset();
		}
		this.size = size;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZZIII)V")
	private void setActiveEffect(@OriginalArg(0) int effectParam1, @OriginalArg(1) boolean lit, @OriginalArg(2) boolean allowStillwater, @OriginalArg(3) int effectType, @OriginalArg(4) int effectParam2) {
		@Pc(14) boolean stillwaterAllowed = allowStillwater & this.stillwaterEffectLoaded();
		if (!stillwaterAllowed && (effectType == 4 || effectType == 8 || effectType == 9)) {
			effectType = 2;
			effectParam2 = 1;
			effectParam1 = 0;
		}
		if (effectType != 0 && lit) {
			effectType |= Integer.MIN_VALUE;
		}
		if (this.activeEffectType != effectType) {
			if (this.activeEffectType != 0) {
				this.waterShadersByEffect[Integer.MAX_VALUE & this.activeEffectType].disable();
			}
			if (effectType == 0) {
				this.activeWaterShader = null;
			} else {
				this.activeWaterShader = this.waterShadersByEffect[Integer.MAX_VALUE & effectType];
				this.activeWaterShader.enable(lit);
				this.activeWaterShader.applyTextureCombine(lit);
				this.activeWaterShader.setEffectParams(effectParam1, effectParam2);
			}
			this.activeEffectType = effectType;
			this.activeEffectParam1 = effectParam1;
			this.activeEffectParam2 = effectParam2;
		} else if (this.activeEffectType != 0) {
			this.waterShadersByEffect[Integer.MAX_VALUE & this.activeEffectType].applyTextureCombine(lit);
			if (this.activeEffectParam1 != effectParam1 || this.activeEffectParam2 != effectParam2) {
				this.waterShadersByEffect[this.activeEffectType & Integer.MAX_VALUE].setEffectParams(effectParam1, effectParam2);
				this.activeEffectParam2 = effectParam2;
				this.activeEffectParam1 = effectParam1;
			}
		}
	}

	@OriginalMember(owner = "client!wga", name = "za", descriptor = "(IIIII)V")
	@Override
	public final void za(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int radius, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		this.setupFlatUiRenderState();
		this.setColourValue(colour);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.toggleBlending(mode);
		this.modelMatrix43.setScale(1.0F, (float) radius, (float) radius);
		this.modelMatrix43.translate(x, y, 0);
		this.commitModelMatrix();
		this.toggleMultisampling(false);
		this.setStreamSource(this.circleVertexStream, 0);
		this.setVertexDeclaration(this.circleVertexDeclaration);
		this.drawPrimitive(256, 0, PrimitiveType.aClass134_3);
		this.toggleMultisampling(true);
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "()I")
	@Override
	public final int i() {
		return this.camNear;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	public abstract Object createSurface(@OriginalArg(1) Canvas canvas);

	@OriginalMember(owner = "client!wga", name = "la", descriptor = "()V")
	@Override
	public final void la() {
		this.cRight = this.surfaceWidth;
		this.cBottom = this.surfaceHeight;
		this.cTop = 0;
		this.cLeft = 0;
		if (this.boundsCalculated) {
			this.boundsCalculated = false;
			this.toggleScissorTest();
		}
		this.updateViewBounds();
	}

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(B)I")
	public final int getCurrentTextureSlot() {
		return this.currentTextureSlot;
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "(II)V")
	@Override
	public final void f(@OriginalArg(0) int camNear, @OriginalArg(1) int camFar) {
		if (this.camNear == camNear && camFar == this.camFar) {
			return;
		}
		this.camFar = camFar;
		this.camNear = camNear;
		this.invalidateModelView();
		this.invalidateOrthoSceneProjection();
		this.updateFogAndWater();
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	@Override
	public final EnvironmentSampler createCubeTexture(@OriginalArg(0) int materialPositiveX, @OriginalArg(1) int materialNegativeX, @OriginalArg(2) int materialPositiveY, @OriginalArg(3) int materialNegativeY, @OriginalArg(4) int materialPositiveZ, @OriginalArg(5) int materialNegativeZ) {
		return new NativeCubeTextureGenerator(this, materialPositiveX, materialNegativeX, materialPositiveY, materialNegativeY, materialPositiveZ, materialNegativeZ);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(FIFFFFF)Z")
	private boolean uploadLineVertices(@OriginalArg(0) float startY, @OriginalArg(2) float endX, @OriginalArg(3) float startZ, @OriginalArg(4) float endY, @OriginalArg(5) float endZ, @OriginalArg(6) float startX) {
		@Pc(13) Buffer buffer = this.vertexStream.lock();
		if (buffer == null) {
			return false;
		}
		@Pc(22) Stream stream = this.wrapBuffer(buffer);
		if (Stream.c()) {
			stream.a(startX);
			stream.a(startY);
			stream.a(startZ);
			stream.a(endX);
			stream.a(endY);
			stream.a(endZ);
		} else {
			stream.b(startX);
			stream.b(startY);
			stream.b(startZ);
			stream.b(endX);
			stream.b(endY);
			stream.b(endZ);
		}
		stream.a();
		return this.vertexStream.unlock();
	}

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "(I)V")
	public final void resetCurrentTextureMatrix() {
		if (this.textureStates[this.currentTextureSlot] != TextureState.INITIALIZE) {
			this.textureStates[this.currentTextureSlot] = TextureState.INITIALIZE;
			this.textureMatrices[this.currentTextureSlot].makeIdentity();
			this.applyTextureMatrix();
		}
	}

	@OriginalMember(owner = "client!wga", name = "ra", descriptor = "(IIII)V")
	@Override
	public final void ra(@OriginalArg(0) int waterHeight, @OriginalArg(1) int waterColour, @OriginalArg(2) int waterScale, @OriginalArg(3) int waterIntensity) {
		this.waterScale = waterScale;
		this.waterIntensity = waterIntensity;
		this.waterColour = waterColour;
		this.missingWaterColor = true;
		this.waterHeight = waterHeight;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	public abstract boolean supportsTextureFormat(@OriginalArg(0) DataType type, @OriginalArg(1) PixelFormat format);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	public abstract void setSurface(@OriginalArg(0) Object surface, @OriginalArg(2) Canvas canvas);

	@OriginalMember(owner = "client!wga", name = "q", descriptor = "(B)V")
	public void setupGraphics() {
		this.resetAllRenderState();
	}

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "(I)V")
	public abstract void toggleLights();

	@OriginalMember(owner = "client!wga", name = "xa", descriptor = "(F)V")
	@Override
	public final void xa(@OriginalArg(0) float intensity) {
		if (this.intensity != intensity) {
			this.intensity = intensity;
			this.nativeInterface.setAmbient(intensity);
			this.setFogColorAndIntensity();
			this.enableLights();
		}
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(B)V")
	public abstract void enableLights();

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "()Z")
	@Override
	public final boolean supportsFullscreen() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(I)V")
	@Override
	public final void linkThreads(@OriginalArg(0) int threadId) {
	}

	@OriginalMember(owner = "client!wga", name = "f", descriptor = "()Z")
	@Override
	public final boolean hasTextures() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	@Override
	public final void drawTriangle() {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IZLclient!kf;)V")
	public final void setTextureAlphaStageState(@OriginalArg(0) int sourceIndex, @OriginalArg(2) TextureCombineSource state) {
		this.setTextureAlphaStage(state, sourceIndex);
	}

	@OriginalMember(owner = "client!wga", name = "T", descriptor = "(IIII)V")
	@Override
	public final void T(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom) {
		@Pc(5) boolean changed = false;
		if (this.cLeft < left) {
			changed = true;
			this.cLeft = left;
		}
		if (right < this.cRight) {
			this.cRight = right;
			changed = true;
		}
		if (top > this.cTop) {
			changed = true;
			this.cTop = top;
		}
		if (bottom < this.cBottom) {
			this.cBottom = bottom;
			changed = true;
		}
		if (!changed) {
			return;
		}
		if (!this.boundsCalculated) {
			this.boundsCalculated = true;
			this.toggleScissorTest();
		}
		this.setScissorRect();
		this.updateViewBounds();
	}

	@OriginalMember(owner = "client!wga", name = "s", descriptor = "(B)Lclient!haa;")
	public final NativeMatrix getFlippedViewMatrix() {
		return this.flippedViewMatrix;
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "(I)V")
	public abstract void setScissorRect();

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "()Z")
	@Override
	public final boolean hardShadow() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(ZI)Lclient!tc;")
	public abstract NativeStream createStream(@OriginalArg(0) boolean dynamic);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZB)V")
	public final void toggleDepthTest(@OriginalArg(0) boolean depthTest) {
		if (depthTest != this.enableDepthTest) {
			this.enableDepthTest = depthTest;
			this.toggleDepthTest();
			this.nativeFunctionMask &= 0xFFFFFFE0;
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([FIILclient!wq;IZ)Lclient!wfa;")
	public final GpuTexture createHdrTexture(@OriginalArg(0) float[] pixels, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) PixelFormat format, @OriginalArg(5) boolean mipmapped) {
		return this.createNativeTexture(format, width, pixels, mipmapped, height);
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "(I)V")
	public abstract void setLightPosition();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!jaclib/memory/Buffer;I)Lclient!jaclib/memory/Stream;")
	public final Stream wrapBuffer(@OriginalArg(0) Buffer buffer) {
		this.scratchStream.a(buffer);
		return this.scratchStream;
	}

	@OriginalMember(owner = "client!wga", name = "EA", descriptor = "(IIII)V")
	@Override
	public final void EA(@OriginalArg(0) int waterHeight, @OriginalArg(1) int waterColour, @OriginalArg(2) int waterScale, @OriginalArg(3) int waterIntensity) {
		if (!this.missingWaterColor) {
			throw new RuntimeException("");
		}
		this.waterScale = waterScale;
		this.waterHeight = waterHeight;
		this.waterIntensity = waterIntensity;
		this.waterColour = waterColour;
		if (this.activeMaterialUnderwater) {
			this.waterShadersByEffect[3].onUnderwaterSettingsChanged();
			this.waterShadersByEffect[3].onFogChanged();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([I)V")
	@Override
	public final void accumulateViewportDimensions(@OriginalArg(0) int[] cacheInto) {
		cacheInto[1] = this.surfaceHeight;
		cacheInto[0] = this.surfaceWidth;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!wh;)V")
	@Override
	public final void setSunCubeTexture(@OriginalArg(0) EnvironmentSampler environmentSampler) {
		this.sunCubeTextureGenerator = (GpuEnvironmentSampler) environmentSampler;
	}

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "(II)V")
	public final void setColourValue(@OriginalArg(1) int color) {
		if (this.envColor != color) {
			this.envColor = color;
			this.setTextureEnvironmentColor();
		}
	}

	@OriginalMember(owner = "client!wga", name = "L", descriptor = "(III)V")
	@Override
	public final void L(@OriginalArg(0) int colour, @OriginalArg(1) int distance, @OriginalArg(2) int offset) {
		if (this.fogColorRgb == colour && this.fogDistance == distance && this.fogOffset == offset) {
			return;
		}
		this.fogColorRgb = colour;
		this.fogOffset = offset;
		this.fogDistance = distance;
		this.updateFogAndWater();
		this.toggleFog();
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "(I)V")
	private void setModelViewMatrixMode() {
		if (MatrixMode.aClass286_6 == this.matrixMode) {
			return;
		}
		@Pc(16) MatrixMode matrixMode = this.matrixMode;
		this.matrixMode = MatrixMode.aClass286_6;
		if (!matrixMode.isSceneMode()) {
			this.requestViewPositionUpdate();
		}
		this.markModelViewValid();
		this.activeProjectionSource = this.perspectiveProjectionMatrix;
		this.calculateViewRange();
		this.nativeFunctionMask &= 0xFFFFFFF8;
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(II)V")
	public final void setTextureSlot(@OriginalArg(0) int textureSlot) {
		if (textureSlot != this.currentTextureSlot) {
			this.currentTextureSlot = textureSlot;
			this.activateCurrentTexture();
		}
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "(I)V")
	public abstract void uploadViewMatrix();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Z)V")
	@Override
	public final void setShrinkTextures(@OriginalArg(0) boolean shrink) {
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IILclient!hj;Z)V")
	public abstract void drawPrimitive(@OriginalArg(0) int primitiveCount, @OriginalArg(1) int first, @OriginalArg(2) PrimitiveType drawMode);

	@OriginalMember(owner = "client!wga", name = "DA", descriptor = "(IIII)V")
	@Override
	public final void DA(@OriginalArg(0) int projectionCenterX, @OriginalArg(1) int projectionCenterY, @OriginalArg(2) int projectionScaleX, @OriginalArg(3) int projectionScaleY) {
		this.projectionCenterY = projectionCenterY;
		this.projectionScaleY = projectionScaleY;
		this.projectionCenterX = projectionCenterX;
		this.projectionScaleX = projectionScaleX;
		this.invalidateOrthoSceneProjection();
		this.invalidateModelView();
		this.setOrthoMatrixMode();
		this.updateViewBounds();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([[IZII)Lclient!nj;")
	public abstract CubeTextureSampler generateNativeCubeTexture(@OriginalArg(0) int[][] pixels, @OriginalArg(1) boolean mipmappingEnabled, @OriginalArg(3) int largestSize);

	@OriginalMember(owner = "client!wga", name = "t", descriptor = "(B)V")
	public abstract void uploadModelMatrix();

	@OriginalMember(owner = "client!wga", name = "u", descriptor = "(B)V")
	public final void initializeRenderers() {
		@Pc(21) Hashtable hashtable = new Hashtable();
		if (this.surfacesByCanvas != null && !this.surfacesByCanvas.isEmpty()) {
			@Pc(32) Enumeration enumeration = this.surfacesByCanvas.keys();
			while (enumeration.hasMoreElements()) {
				@Pc(38) Canvas canvas = (Canvas) enumeration.nextElement();
				hashtable.put(canvas, this.createSurface(canvas));
			}
		}
		this.surfacesByCanvas = hashtable;
		this.initializeQuadStream();
		this.initializeLineStream();
		this.initializeCircleStream();
		this.particleRenderer.resetVertexStream(this);
	}

	@OriginalMember(owner = "client!wga", name = "k", descriptor = "(Z)V")
	private void setupFlatUiRenderState() {
		if (this.nativeFunctionMask == 1) {
			return;
		}
		this.setProjectionMatrixMode();
		this.toggleFog(false);
		this.toggleLights(false);
		this.toggleDepthTest(false);
		this.setDepthMaskEnabled(false);
		this.setActiveMaterial(false, false, -2);
		this.setColourType(1);
		this.setTextureSampler(this.defaultTexture);
		this.nativeFunctionMask = 1;
	}

	@OriginalMember(owner = "client!wga", name = "r", descriptor = "(IIIIIII)I")
	@Override
	public final int r(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2, @OriginalArg(6) int zoom) {
		@Pc(7) int clipFlags = 0;
		@Pc(32) float startDepth = this.viewMatrix43.zD + this.viewMatrix43.zB * (float) y1 + (float) x1 * this.viewMatrix43.zA + (float) z1 * this.viewMatrix43.zC;
		@Pc(57) float endDepth = this.viewMatrix43.zA * (float) x2 + this.viewMatrix43.zB * (float) y2 + this.viewMatrix43.zC * (float) z2 + this.viewMatrix43.zD;
		if ((float) this.camNear > startDepth && endDepth < (float) this.camNear) {
			clipFlags |= 0x10;
		} else if ((float) this.camFar < startDepth && (float) this.camFar < endDepth) {
			clipFlags |= 0x20;
		}
		@Pc(124) int startScreenX = (int) (((float) y1 * this.viewMatrix43.xB + (float) x1 * this.viewMatrix43.xA + (float) z1 * this.viewMatrix43.xC + this.viewMatrix43.xD) * (float) this.projectionScaleX / (float) zoom);
		@Pc(157) int endScreenX = (int) ((float) this.projectionScaleX * (this.viewMatrix43.xD + (float) x2 * this.viewMatrix43.xA + (float) y2 * this.viewMatrix43.xB + (float) z2 * this.viewMatrix43.xC) / (float) zoom);
		if (this.viewX1 > (float) startScreenX && (float) endScreenX < this.viewX1) {
			clipFlags |= 0x1;
		} else if (this.viewX2 < (float) startScreenX && (float) endScreenX > this.viewX2) {
			clipFlags |= 0x2;
		}
		@Pc(224) int startScreenY = (int) ((float) this.projectionScaleY * (this.viewMatrix43.yD + this.viewMatrix43.yC * (float) z1 + this.viewMatrix43.yA * (float) x1 + this.viewMatrix43.yB * (float) y1) / (float) zoom);
		@Pc(257) int endScreenY = (int) ((float) this.projectionScaleY * (this.viewMatrix43.yC * (float) z2 + (float) x2 * this.viewMatrix43.yA + (float) y2 * this.viewMatrix43.yB + this.viewMatrix43.yD) / (float) zoom);
		if ((float) startScreenY < this.viewY1 && this.viewY1 > (float) endScreenY) {
			clipFlags |= 0x4;
		} else if (this.viewY2 < (float) startScreenY && this.viewY2 < (float) endScreenY) {
			clipFlags |= 0x8;
		}
		return clipFlags;
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(I)V")
	private void updateViewBounds() {
		this.viewY1 = this.cTop - this.projectionCenterY;
		this.viewX1 = this.cLeft - this.projectionCenterX;
		this.viewY2 = this.cBottom - this.projectionCenterY;
		this.viewX2 = this.cRight - this.projectionCenterX;
	}

	@OriginalMember(owner = "client!wga", name = "v", descriptor = "(B)V")
	private void applyTextureMatrix() {
		this.setTextureMatrix();
		if (this.activeWaterShader != null) {
			this.activeWaterShader.onTextureMatrixChanged();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public final void addCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		if (canvas == this.lb) {
			throw new RuntimeException();
		} else if (!this.surfacesByCanvas.containsKey(canvas)) {
			if (!canvas.isShowing()) {
				throw new RuntimeException();
			}
			try {
				@Pc(26) Class class2 = Class.forName("java.awt.Canvas");
				@Pc(36) Method method = class2.getMethod("setIgnoreRepaint", Boolean.TYPE);
				method.invoke(canvas, Boolean.TRUE);
			} catch (@Pc(48) Exception ex) {
			}
			@Pc(53) Object object = this.createSurface(canvas);
			if (object == null) {
				throw new RuntimeException();
			}
			this.surfacesByCanvas.put(canvas, object);
		}
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "()I")
	@Override
	public final int getMaxLights() {
		return this.maxActiveLights - 2;
	}

	@OriginalMember(owner = "client!wga", name = "w", descriptor = "(B)V")
	private void markModelViewValid() {
		if (this.isModelViewValid) {
			return;
		}
		@Pc(13) float[] matrix = this.perspectiveProjectionMatrix;
		@Pc(25) float left = (float) (this.camNear * -this.projectionCenterX) / (float) this.projectionScaleX;
		@Pc(40) float right = (float) (this.camNear * (this.surfaceWidth - this.projectionCenterX)) / (float) this.projectionScaleX;
		@Pc(51) float top = (float) (this.camNear * this.projectionCenterY) / (float) this.projectionScaleY;
		@Pc(66) float bottom = (float) (this.camNear * (this.projectionCenterY - this.surfaceHeight)) / (float) this.projectionScaleY;
		if (left == right || top == bottom) {
			matrix[12] = 0.0F;
			matrix[0] = 1.0F;
			matrix[13] = 0.0F;
			matrix[4] = 0.0F;
			matrix[3] = 0.0F;
			matrix[10] = 1.0F;
			matrix[15] = 1.0F;
			matrix[9] = 0.0F;
			matrix[14] = 0.0F;
			matrix[1] = 0.0F;
			matrix[2] = 0.0F;
			matrix[11] = 0.0F;
			matrix[6] = 0.0F;
			matrix[7] = 0.0F;
			matrix[8] = 0.0F;
			matrix[5] = 1.0F;
		} else {
			@Pc(146) float doubleNear = (float) this.camNear * 2.0F;
			matrix[13] = 0.0F;
			matrix[3] = 0.0F;
			matrix[2] = 0.0F;
			matrix[11] = -1.0F;
			matrix[7] = 0.0F;
			matrix[5] = doubleNear / (top - bottom);
			matrix[1] = 0.0F;
			matrix[12] = 0.0F;
			matrix[14] = this.perspectiveDepthOffset = (float) (this.camFar * this.camNear) / (float) (this.camNear - this.camFar);
			matrix[0] = doubleNear / (right - left);
			matrix[15] = 0.0F;
			matrix[8] = (right + left) / (-left + right);
			matrix[9] = (top + bottom) / (top - bottom);
			matrix[10] = this.perspectiveDepthScale = (float) this.camFar / (float) (this.camNear - this.camFar);
			matrix[4] = 0.0F;
			matrix[6] = 0.0F;
		}
		this.updatePerspectiveFar();
		this.isModelViewValid = true;
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "(Z)V")
	private void drawPrimitiveLines() {
		this.setStreamSource(this.vertexStream, 0);
		this.setVertexDeclaration(this.vertexDeclaration);
		this.drawPrimitive(1, 0, PrimitiveDrawMode.LINES);
	}

	@OriginalMember(owner = "client!wga", name = "Y", descriptor = "()[I")
	@Override
	public final int[] Y() {
		return new int[] { this.projectionCenterX, this.projectionCenterY, this.projectionScaleX, this.projectionScaleY };
	}

	@OriginalMember(owner = "client!wga", name = "x", descriptor = "(B)V")
	private void updateOrthoFar() {
		this.orthoFar = this.camFar;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public final void strongLine(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int width) {
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	@Override
	public final NativeSprite createNativeSprite(@OriginalArg(0) int x, @OriginalArg(1) int colour, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean hasAlpha) {
		@Pc(11) GpuSprite sprite = new GpuSprite(this, width, height, hasAlpha);
		sprite.captureScreenRegion(0, 0, width, height, x, colour);
		return sprite;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BI)V")
	public final void setUniformColourValue(@OriginalArg(0) byte brightness) {
		this.setColourValue(brightness | brightness << 8 | brightness << 16 | brightness << 24);
	}

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(BI)V")
	public abstract void setTexCoordIndex(@OriginalArg(1) int index);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!mda;I)V")
	@Override
	public final void renderOrtho(@OriginalArg(0) ParticleList particleList, @OriginalArg(1) int zoom) {
		this.particleRenderer.render(this, particleList, zoom);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	@Override
	public final NativeSprite generateSprite(@OriginalArg(0) SpriteType indexedImage, @OriginalArg(1) boolean paletteSprite) {
		@Pc(24) NativeSprite sprite;
		if (indexedImage.width == 0 || indexedImage.height == 0) {
			sprite = this.createNativeSprite(1, new int[1], 1, 1);
		} else {
			@Pc(33) int[] pixels = new int[indexedImage.width * indexedImage.height];
			@Pc(35) int srcIndex = 0;
			@Pc(37) int dstIndex = 0;
			@Pc(42) int y;
			@Pc(46) int x;
			if (indexedImage.alpha == null) {
				for (y = 0; y < indexedImage.height; y++) {
					for (x = 0; x < indexedImage.width; x++) {
						@Pc(59) int colour = indexedImage.pallete[indexedImage.pixels[srcIndex++] & 0xFF];
						pixels[dstIndex++] = colour == 0 ? 0 : colour | 0xFF000000;
					}
				}
			} else {
				for (y = 0; y < indexedImage.height; y++) {
					for (x = 0; x < indexedImage.width; x++) {
						pixels[dstIndex++] = indexedImage.pallete[indexedImage.pixels[srcIndex] & 0xFF] | indexedImage.alpha[srcIndex] << 24;
						srcIndex++;
					}
				}
			}
			sprite = this.createNativeSprite(indexedImage.width, pixels, indexedImage.width, indexedImage.height);
		}
		sprite.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY);
		return sprite;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	@Override
	public final void drawSurfaceLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY) {
	}

	@OriginalMember(owner = "client!wga", name = "l", descriptor = "()V")
	@Override
	public final void cacheReset() {
		if (this.materialCache != null) {
			this.materialCache.reset();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	public abstract void destroySurface(@OriginalArg(0) Canvas canvas, @OriginalArg(2) Object surface);

	@OriginalMember(owner = "client!wga", name = "j", descriptor = "()Lclient!bca;")
	@Override
	public final Matrix createMatrix() {
		return new NativeMatrix();
	}

	@OriginalMember(owner = "client!wga", name = "B", descriptor = "(I)V")
	private void updatePerspectiveFar() {
		this.perspectiveProjectionMatrix[14] = this.perspectiveDepthOffset;
		this.perspectiveProjectionMatrix[10] = this.perspectiveDepthScale;
		this.perspectiveFar = (this.perspectiveProjectionMatrix[14] - (float) this.camFar) / this.perspectiveProjectionMatrix[10];
	}

	@OriginalMember(owner = "client!wga", name = "y", descriptor = "(B)V")
	private void requestViewPositionUpdate() {
		this.viewPositionCalibrated = false;
		if (this.activeWaterShader != null) {
			this.activeWaterShader.onCameraChanged();
		}
		this.uploadViewMatrix();
	}

	@OriginalMember(owner = "client!wga", name = "z", descriptor = "(B)V")
	private void initializeCircleStream() {
		this.circleVertexStream = this.createStream(false);
		this.circleVertexStream.create(3096, 12);
		for (@Pc(24) int i = 0; i < 4; i++) {
			@Pc(32) Buffer buffer = this.circleVertexStream.lock();
			if (buffer != null) {
				@Pc(39) Stream stream = this.wrapBuffer(buffer);
				stream.a(0.0F);
				stream.a(0.0F);
				stream.a(0.0F);
				for (@Pc(50) int j = 0; j <= 256; j++) {
					@Pc(61) double angle = (double) (j * 2) * 3.141592653589793D / 256.0D;
					@Pc(65) float cos = (float) Math.cos(angle);
					@Pc(69) float sin = (float) Math.sin(angle);
					if (Stream.c()) {
						stream.a(sin);
						stream.a(cos);
						stream.a(0.0F);
					} else {
						stream.b(sin);
						stream.b(cos);
						stream.b(0.0F);
					}
				}
				stream.a();
				if (this.circleVertexStream.unlock()) {
					break;
				}
			}
		}
		this.circleVertexDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(VertexDeclarationAction.POSITION) });
	}

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(I)V")
	public final void commitModelMatrix() {
		this.isModelMatrixIdentity = false;
		this.onModelMatrixChanged();
	}

	@OriginalMember(owner = "client!wga", name = "A", descriptor = "(B)V")
	private void resetAllRenderState() {
		this.setFogColorAndIntensity();
		this.setSunParameters();
		this.toggleLights();
		this.updateSceneLights();
		this.setLightPosition();
		this.enableLights();
		this.toggleSun();
		this.toggleDepthTest();
		this.setDepthRenderState();
		this.toggleFog();
		this.configureFog();
		this.toggleBlending();
		this.blendDrawBuffer();
		this.toggleAlpha();
		for (@Pc(62) int slot = this.maxConcurrentTextures - 1; slot >= 0; slot--) {
			this.setTextureSlot(slot);
			this.setTextureColorStage();
			this.setTextureAlphaStage();
			this.resetCurrentTextureMatrix();
		}
		this.setTextureEnvironmentColor();
		this.setViewport();
		this.uploadProjectionMatrix();
		this.uploadModelMatrix();
		this.uploadViewMatrix();
	}

	@OriginalMember(owner = "client!wga", name = "m", descriptor = "(Z)V")
	private void updateSurfaceSize() {
		if (this.currentCanvas == null) {
			this.width = this.height = 1;
		} else {
			@Pc(14) Dimension dimension = this.currentCanvas.getSize();
			this.height = dimension.height;
			this.width = dimension.width;
		}
		this.surfaceHeight = this.height;
		this.surfaceWidth = this.width;
		this.invalidateOrthoProjection();
		this.invalidateModelView();
		this.invalidateOrthoSceneProjection();
		this.setViewport();
		this.updateViewBounds();
		this.setOrthoMatrixMode();
		this.la();
	}

	@OriginalMember(owner = "client!wga", name = "D", descriptor = "(I)V")
	private void invalidateOrthoProjection() {
		this.isOrthoProjectionValid = false;
		if (MatrixMode.aClass286_5 == this.matrixMode) {
			this.loadWorldViewProjectionMatrix();
			this.calculateViewRange();
		}
	}

	@OriginalMember(owner = "client!wga", name = "P", descriptor = "(IIIII)V")
	@Override
	public final void P(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int height, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		this.line(x, y, x, height + y, colour, mode);
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "(I)V")
	private void loadWorldViewProjectionMatrix() {
		if (this.isOrthoProjectionValid) {
			return;
		}
		@Pc(9) float[] matrix = this.screenOrthoMatrix;
		if (this.surfaceWidth == 0 || this.surfaceHeight == 0) {
			matrix[3] = 0.0F;
			matrix[12] = 0.0F;
			matrix[6] = 0.0F;
			matrix[9] = 0.0F;
			matrix[11] = 0.0F;
			matrix[15] = 1.0F;
			matrix[1] = 0.0F;
			matrix[8] = 0.0F;
			matrix[2] = 0.0F;
			matrix[14] = 0.0F;
			matrix[5] = 1.0F;
			matrix[13] = 0.0F;
			matrix[7] = 0.0F;
			matrix[10] = 1.0F;
			matrix[4] = 0.0F;
			matrix[0] = 1.0F;
		} else {
			matrix[9] = 0.0F;
			matrix[12] = -1.0F;
			matrix[2] = 0.0F;
			matrix[0] = 2.0F / (float) this.surfaceWidth;
			matrix[4] = 0.0F;
			matrix[15] = 1.0F;
			matrix[13] = 1.0F;
			matrix[6] = 0.0F;
			matrix[3] = 0.0F;
			matrix[10] = 0.5F;
			matrix[8] = 0.0F;
			matrix[1] = 0.0F;
			matrix[14] = 0.5F;
			matrix[7] = 0.0F;
			matrix[5] = -2.0F / (float) this.surfaceHeight;
			matrix[11] = 0.0F;
		}
		this.isOrthoProjectionValid = true;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public final void setCanvas(@OriginalArg(0) Canvas canvas) {
		this.currentSurface = null;
		this.currentCanvas = null;
		if (canvas == null || canvas == this.lb) {
			this.currentSurface = this.primarySurface;
			this.currentCanvas = this.lb;
		} else if (this.surfacesByCanvas.containsKey(canvas)) {
			this.currentSurface = this.surfacesByCanvas.get(canvas);
			this.currentCanvas = canvas;
		}
		if (this.currentCanvas == null || this.currentSurface == null) {
			throw new RuntimeException();
		}
		this.setSurface(this.currentSurface, this.currentCanvas);
		this.updateSurfaceSize();
	}

	@OriginalMember(owner = "client!wga", name = "F", descriptor = "(I)V")
	public abstract void setTextureColorStage();

	@OriginalMember(owner = "client!wga", name = "G", descriptor = "(I)V")
	public abstract void toggleScissorTest();

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(I)V")
	@Override
	public void tick(@OriginalArg(0) int time) {
		if (this.materialCache != null) {
			this.materialCache.cacheClean();
		}
		this.animatingCycle = Integer.MAX_VALUE & time;
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIIIII)V")
	@Override
	public final void drawLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) int lineSpacing, @OriginalArg(7) int lineLength, @OriginalArg(8) int offset) {
		@Pc(11) float dirX = (float) endX - (float) startX;
		@Pc(17) float dirY = (float) endY - (float) startY;
		@Pc(38) float local38;
		if (dirX == 0.0F && dirY == 0.0F) {
			dirX = 1.0F;
		} else {
			float invLength = (float) (1.0D / Math.sqrt((double) (dirY * dirY + dirX * dirX)));
			dirX *= invLength;
			dirY *= invLength;
		}
		this.setupFlatUiRenderState();
		this.setColourValue(colour);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.toggleBlending(1);
		this.resetModelMatrix();
		this.toggleMultisampling(false);
		@Pc(84) int phase = offset % (lineSpacing + lineLength);
		float dashX = (float) lineSpacing * dirX;
		@Pc(94) float dashY = (float) lineSpacing * dirY;
		@Pc(96) float startOffsetX = 0.0F;
		@Pc(98) float startOffsetY = 0.0F;
		@Pc(100) float segmentX = dashX;
		@Pc(102) float segmentY = dashY;
		if (lineSpacing >= phase) {
			segmentY = (float) (lineSpacing - phase) * dirY;
			segmentX = dirX * (float) (lineSpacing - phase);
		} else {
			startOffsetY = (float) (lineSpacing + lineLength - phase) * dirY;
			startOffsetX = dirX * (float) (lineSpacing + lineLength - phase);
		}
		@Pc(148) float x = (float) startX + startOffsetX;
		@Pc(153) float y = startOffsetY + (float) startY;
		@Pc(158) float gapX = (float) lineLength * dirX;
		@Pc(163) float gapY = dirY * (float) lineLength;
		while (true) {
			if (startX < endX) {
				if (x > (float) endX) {
					break;
				}
				if ((float) endX < x + segmentX) {
					segmentX = (float) endX - x;
				}
			} else {
				if (x < (float) endX) {
					break;
				}
				if ((float) endX > x + segmentX) {
					segmentX = (float) endX - x;
				}
			}
			if (startY < endY) {
				if ((float) endY < y) {
					break;
				}
				if (segmentY + y > (float) endY) {
					segmentY = (float) endY - y;
				}
			} else {
				if (y < (float) endY) {
					break;
				}
				if (segmentY + y < (float) endY) {
					segmentY = (float) endY - y;
				}
			}
			if (!this.uploadLineVertices(y, x + segmentX, 0.0F, y + segmentY, 0.0F, x)) {
				return;
			}
			this.drawPrimitiveLines();
			y += gapY + segmentY;
			x += gapX + segmentX;
			segmentX = dashX;
			segmentY = dashY;
		}
		this.toggleMultisampling(true);
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "pa", descriptor = "()V")
	@Override
	public final void pa() {
		this.missingWaterColor = false;
	}

	@OriginalMember(owner = "client!wga", name = "n", descriptor = "(Z)V")
	public final void deallocate() {
		@Pc(15) Enumeration enumeration = this.surfacesByCanvas.keys();
		while (enumeration.hasMoreElements()) {
			@Pc(21) Canvas canvas = (Canvas) enumeration.nextElement();
			this.destroySurface(canvas, this.surfacesByCanvas.get(canvas));
		}
		this.quadVertexStream.deallocate();
		this.vertexStream.deallocate();
		this.circleVertexStream.deallocate();
		this.copyType1Scratch.releaseStreams();
		this.copyType2Scratch.releaseStreams();
		this.copyType3Scratch.releaseStreams();
		this.copyType4Scratch.releaseStreams();
		this.copyType5Scratch.releaseStreams();
		this.particleRenderer.release();
		this.indexBuffer.release();
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(ZI)V")
	public final void setColourType(@OriginalArg(1) int combineMode) {
		if (combineMode == 1) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4);
		} else if (combineMode == 0) {
			this.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		} else if (combineMode == 2) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5);
		} else if (combineMode == 3) {
			this.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_2);
		} else if (combineMode == 4) {
			this.setTextureCombineModes(TextureCombineFunction.aClass315_1, TextureCombineFunction.aClass315_1);
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	public abstract void setTextureColourSource(@OriginalArg(0) boolean useAlpha, @OriginalArg(2) int sourceIndex, @OriginalArg(3) TextureCombineSource state, @OriginalArg(4) boolean invert);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ILclient!tc;I)V")
	public abstract void setStreamSource(@OriginalArg(1) NativeStream stream, @OriginalArg(2) int streamIndex);

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(BZ)V")
	public abstract void toggleMultisampling(@OriginalArg(1) boolean enabled);

	@OriginalMember(owner = "client!wga", name = "U", descriptor = "(IIIII)V")
	@Override
	public final void U(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		this.line(x, y, x + width, y, colour, mode);
	}

	@OriginalMember(owner = "client!wga", name = "o", descriptor = "(Z)V")
	public final void setupTexturedUiRenderState() {
		if (this.nativeFunctionMask == 4) {
			return;
		}
		this.setProjectionMatrixMode();
		this.toggleFog(false);
		this.toggleLights(false);
		this.toggleDepthTest(false);
		this.setDepthMaskEnabled(false);
		this.setActiveMaterial(false, false, -2);
		this.toggleBlending(1);
		this.setColourType(0);
		this.nativeFunctionMask = 4;
	}

	@OriginalMember(owner = "client!wga", name = "H", descriptor = "(I)V")
	public abstract void setFogColorAndIntensity();

	@OriginalMember(owner = "client!wga", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public final void releaseSurface(@OriginalArg(0) Canvas canvas) {
		if (this.lb == canvas) {
			throw new RuntimeException();
		} else if (this.surfacesByCanvas.containsKey(canvas)) {
			this.destroySurface(canvas, this.surfacesByCanvas.get(canvas));
			this.surfacesByCanvas.remove(canvas);
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	@Override
	public final NativeSprite createNativeSprite(@OriginalArg(0) int[] pixels, @OriginalArg(2) int rowLength, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) boolean copyPixels) {
		return new GpuSprite(this, width, height, pixels, 0, rowLength);
	}

	@OriginalMember(owner = "client!wga", name = "p", descriptor = "(Z)V")
	private void calculateViewRange() {
		this.uploadProjectionMatrix();
		if (this.activeWaterShader != null) {
			this.activeWaterShader.onProjectionChanged();
		}
	}

	@OriginalMember(owner = "client!wga", name = "B", descriptor = "(B)V")
	public final void initializeTables() {
		this.textureMatrices = new NativeMatrix[this.maxConcurrentTextures];
		this.textureSamplers = new TextureSampler[this.maxConcurrentTextures];
		this.textureRgbCombineModes = new TextureCombineFunction[this.maxConcurrentTextures];
		this.textureAlphaCombineModes = new TextureCombineFunction[this.maxConcurrentTextures];
		this.textureStates = new TextureTransformMode[this.maxConcurrentTextures];
		for (@Pc(32) int i = 0; i < this.maxConcurrentTextures; i++) {
			this.textureAlphaCombineModes[i] = TextureCombineMode.MODULATE;
			this.textureRgbCombineModes[i] = TextureCombineMode.MODULATE;
			this.textureStates[i] = TextureState.INITIALIZE;
			this.textureMatrices[i] = new NativeMatrix();
		}
		this.lightSources = new PointLight[this.maxActiveLights - 2];
		this.defaultTexture = this.createNativeTexture2D(1, 1, GLFormat.RGBA, GLFormat.HDR);
		this.setHeap(new NativeMemoryPool(262144));
		this.positionTexCoordDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD }) });
		this.positionColorDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.COLOR }) });
		this.positionColorTexCoordNormalDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(VertexDeclarationAction.POSITION), new VertexDeclarationComponent(VertexDeclarationAction.COLOR), new VertexDeclarationComponent(VertexDeclarationAction.TEXCOORD), new VertexDeclarationComponent(VertexDeclarationAction.NORMAL_POINTER_ACTION) });
		this.positionColorTexCoordDeclaration = this.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(VertexDeclarationAction.POSITION), new VertexDeclarationComponent(VertexDeclarationAction.COLOR), new VertexDeclarationComponent(VertexDeclarationAction.TEXCOORD) });
		this.copyType1Result = new GpuModel(this, 0, 0, false, false);
		this.copyType1Scratch = new GpuModel(this, 0, 0, true, true);
		this.copyType2Result = new GpuModel(this, 0, 0, false, false);
		this.copyType2Scratch = new GpuModel(this, 0, 0, true, true);
		this.copyType3Result = new GpuModel(this, 0, 0, false, false);
		this.copyType3Scratch = new GpuModel(this, 0, 0, true, true);
		this.copyType4Result = new GpuModel(this, 0, 0, false, false);
		this.copyType4Scratch = new GpuModel(this, 0, 0, true, true);
		this.copyType5Result = new GpuModel(this, 0, 0, false, false);
		this.copyType5Scratch = new GpuModel(this, 0, 0, true, true);
		this.particleRenderer = new NativeParticleRenderer(this);
		this.indexBuffer = this.createIndexBuffer(true);
		this.initializeRenderers();
		this.waterTexture = new NativeWaterTexture(this);
		this.waterShadersByEffect[1] = this.createRenderPass(1);
		this.waterShadersByEffect[2] = this.createRenderPass(2);
		this.waterShadersByEffect[4] = this.createRenderPass(4);
		this.waterShadersByEffect[5] = this.createRenderPass(5);
		this.waterShadersByEffect[6] = this.createRenderPass(6);
		this.waterShadersByEffect[7] = this.createRenderPass(7);
		this.waterShadersByEffect[3] = this.createRenderPass(3);
		this.waterShadersByEffect[8] = this.createRenderPass(8);
		this.waterShadersByEffect[9] = this.createRenderPass(9);
		if (!this.waterShadersByEffect[2].isSupported()) {
			this.waterShadersByEffect[2] = this.createRenderPass(0);
		}
		if (!this.waterShadersByEffect[4].isSupported()) {
			this.waterShadersByEffect[4] = this.waterShadersByEffect[2];
		}
		if (!this.waterShadersByEffect[8].isSupported()) {
			this.waterShadersByEffect[8] = this.waterShadersByEffect[4];
		}
		if (!this.waterShadersByEffect[9].isSupported()) {
			this.waterShadersByEffect[9] = this.waterShadersByEffect[8];
		}
		this.setupGraphics();
		this.la();
		this.renderLastFrame();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	public abstract boolean supports3DTextureFormat(@OriginalArg(1) PixelFormat format, @OriginalArg(2) DataType type);

	@OriginalMember(owner = "client!wga", name = "C", descriptor = "(B)V")
	public final void setOrthoMatrixMode() {
		if (this.matrixMode == MatrixMode.aClass286_7) {
			return;
		}
		@Pc(6) MatrixMode matrixMode = this.matrixMode;
		this.matrixMode = MatrixMode.aClass286_7;
		if (matrixMode.isSceneMode()) {
			this.requestViewPositionUpdate();
		}
		this.nativeFunctionMask &= 0xFFFFFFE0;
		this.activeProjectionSource = this.orthoProjectionMatrix;
	}

	@OriginalMember(owner = "client!wga", name = "H", descriptor = "(III[I)V")
	@Override
	public final void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords) {
		@Pc(14) float depth = this.viewMatrix43.transformZ((float) x, (float) z, (float) y);
		@Pc(40) int screenX;
		@Pc(58) int screenY;
		if (depth < -0.0078125F || depth > 0.0078125F) {
			screenX = (int) ((float) this.projectionScaleX * this.viewMatrix43.transformX((float) z, (float) y, (float) x) / depth);
			screenY = (int) ((float) this.projectionScaleY * this.viewMatrix43.transformY((float) x, (float) z, (float) y) / depth);
		} else {
			screenY = this.projectionCenterY;
			screenX = this.projectionCenterX;
		}
		screenCoords[1] = (int) ((float) screenY - this.viewY1);
		screenCoords[0] = (int) ((float) screenX - this.viewX1);
		screenCoords[2] = (int) depth;
	}

	@OriginalMember(owner = "client!wga", name = "I", descriptor = "(I)V")
	public final void drawQuadTriangleFan() {
		this.drawQuadPrimitive(PrimitiveType.aClass134_3, 2);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIII)V")
	@Override
	public final void outlineRect(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		@Pc(7) float pixelOffset = this.getPixelOffset();
		this.setupFlatUiRenderState();
		this.setColourValue(colour);
		this.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.toggleBlending(mode);
		this.modelMatrix43.setScale(1.0F, (float) (height - 1), (float) (width - 1));
		this.modelMatrix43.translate((float) y - pixelOffset, 0.0F, (float) x - pixelOffset);
		this.commitModelMatrix();
		this.toggleMultisampling(false);
		this.drawQuadPrimitive(PrimitiveType.aClass134_5, 4);
		this.toggleMultisampling(true);
		this.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!wga", name = "J", descriptor = "(I)Lclient!haa;")
	public final NativeMatrix getModelMatrix() {
		return this.modelMatrix43;
	}

	@OriginalMember(owner = "client!wga", name = "K", descriptor = "(I)V")
	public abstract void setTextureAlphaStage();

	@OriginalMember(owner = "client!wga", name = "XA", descriptor = "()I")
	@Override
	public final int XA() {
		return this.camFar;
	}

	@OriginalMember(owner = "client!wga", name = "g", descriptor = "(II)I")
	@Override
	public final int combineFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		return maskB | maskA;
	}

	@OriginalMember(owner = "client!wga", name = "D", descriptor = "(B)V")
	public abstract void setViewport();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	public abstract void drawIndexedPrimitive(@OriginalArg(0) PrimitiveType drawMode, @OriginalArg(1) int primitiveCount, @OriginalArg(2) GpuIndexBuffer indexBuffer, @OriginalArg(3) int minIndex, @OriginalArg(4) int startIndex, @OriginalArg(5) int vertexCount);

	@OriginalMember(owner = "client!wga", name = "e", descriptor = "(II)I")
	@Override
	public final int compareFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		return maskB & maskA ^ maskB;
	}

	@OriginalMember(owner = "client!wga", name = "E", descriptor = "(B)V")
	public abstract void toggleAlpha();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	@Override
	public final AbstractFontRenderer createFontRenderer(@OriginalArg(0) FontMetrics metrics, @OriginalArg(1) SpriteType[] image) {
		return new NativeFont(this, metrics, image, true);
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(I[Lclient!ab;)V")
	@Override
	public final void copyLights(@OriginalArg(0) int lastSceneLightCount, @OriginalArg(1) PointLight[] pointLights) {
		for (@Pc(3) int i = 0; i < lastSceneLightCount; i++) {
			this.lightSources[i] = pointLights[i];
		}
		this.lastSceneLightCount = lastSceneLightCount;
		if (this.matrixMode.isSceneMode()) {
			this.updateSceneLights();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "()Z")
	@Override
	public final boolean supportsBloom() {
		return false;
	}

	@OriginalMember(owner = "client!wga", name = "i", descriptor = "(I)Lclient!za;")
	@Override
	public final MemoryPool createHeap(@OriginalArg(0) int size) {
		@Pc(8) NativeMemoryPool nativeMemoryPool = new NativeMemoryPool(size);
		this.activeNativeMemory.addLast(nativeMemoryPool);
		return nativeMemoryPool;
	}

	@OriginalMember(owner = "client!wga", name = "M", descriptor = "()I")
	@Override
	public final int M() {
		return this.modelCount;
	}

	@OriginalMember(owner = "client!wga", name = "M", descriptor = "(I)V")
	public abstract void setTextureMatrix();

	@OriginalMember(owner = "client!wga", name = "N", descriptor = "(I)I")
	public final int getWaterHeight() {
		return this.waterHeight;
	}

	@OriginalMember(owner = "client!wga", name = "O", descriptor = "(I)V")
	public final void resetModelMatrix() {
		this.modelMatrix43.makeIdentity();
		this.isModelMatrixIdentity = true;
		this.onModelMatrixChanged();
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	@Override
	public final PointLight createPointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int range, @OriginalArg(5) float intensity) {
		return new GpuPointLight(x, y, z, radius, range, intensity);
	}

	@OriginalMember(owner = "client!wga", name = "d", descriptor = "(ZI)V")
	public final void setForceLightsDisabled(@OriginalArg(0) boolean disabled) {
		if (disabled != this.forceLightsDisabled) {
			this.forceLightsDisabled = disabled;
			this.toggleLights();
		}
	}

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "([BILclient!wq;IZI)Lclient!wfa;")
	public final GpuTexture createTextureSampler(@OriginalArg(0) byte[] pixels, @OriginalArg(1) int width, @OriginalArg(2) PixelFormat format, @OriginalArg(4) boolean mipmapped, @OriginalArg(5) int height) {
		return this.createNativeTextureSampler(height, width, pixels, format, mipmapped);
	}

	@OriginalMember(owner = "client!wga", name = "P", descriptor = "(I)V")
	public abstract void setTextureEnvironmentColor();

	@OriginalMember(owner = "client!wga", name = "s", descriptor = "()Z")
	@Override
	public final boolean utilisesNativeHeap() {
		return true;
	}

	@OriginalMember(owner = "client!wga", name = "c", descriptor = "(IZ)V")
	public final void setDepthMaskEnabled(@OriginalArg(1) boolean enabled) {
		if (enabled != this.depthMaskEnabled) {
			this.depthMaskEnabled = enabled;
			this.setDepthRenderState();
			this.nativeFunctionMask &= 0xFFFFFFE0;
		}
	}

	@OriginalMember(owner = "client!wga", name = "Q", descriptor = "(I)V")
	public abstract void toggleBlending();

	@OriginalMember(owner = "client!wga", name = "a", descriptor = "(Lclient!mda;)V")
	@Override
	public final void render(@OriginalArg(0) ParticleList particleList) {
		this.particleRenderer.render(this, particleList, -1);
	}

	@OriginalMember(owner = "client!wga", name = "R", descriptor = "(I)Lclient!haa;")
	public final NativeMatrix calibrateViewPosition() {
		if (!this.viewPositionCalibrated) {
			this.combinedModelViewMatrix43.multiply(this.flippedViewMatrix, this.modelMatrix43);
			this.viewPositionCalibrated = true;
		}
		return this.combinedModelViewMatrix43;
	}

	@OriginalMember(owner = "client!wga", name = "F", descriptor = "(B)V")
	private void loadOrthoSceneProjectionMatrix() {
		if (this.isOrthoSceneProjectionValid) {
			return;
		}
		@Pc(9) float[] matrix = this.orthoSceneProjectionMatrix;
		@Pc(13) float screenWidth = (float) this.camNear;
		@Pc(17) float screenHeight = (float) this.camFar;
		@Pc(29) float top = this.orthoZoom * (float) -this.projectionCenterY / (float) this.projectionScaleY;
		@Pc(41) float left = (float) -this.projectionCenterX * this.orthoZoom / (float) this.projectionScaleX;
		@Pc(56) float right = this.orthoZoom * (float) (this.surfaceWidth - this.projectionCenterX) / (float) this.projectionScaleX;
		@Pc(71) float bottom = (float) (this.surfaceHeight - this.projectionCenterY) * this.orthoZoom / (float) this.projectionScaleY;
		if (right == left || top == bottom) {
			matrix[11] = 0.0F;
			matrix[8] = 0.0F;
			matrix[7] = 0.0F;
			matrix[10] = 1.0F;
			matrix[15] = 1.0F;
			matrix[9] = 0.0F;
			matrix[0] = 1.0F;
			matrix[1] = 0.0F;
			matrix[13] = 0.0F;
			matrix[4] = 0.0F;
			matrix[6] = 0.0F;
			matrix[12] = 0.0F;
			matrix[5] = 1.0F;
			matrix[2] = 0.0F;
			matrix[14] = 0.0F;
			matrix[3] = 0.0F;
		} else {
			matrix[0] = 2.0F / (right - left);
			matrix[12] = (right + left) / (left - right);
			matrix[6] = 0.0F;
			matrix[3] = 0.0F;
			matrix[11] = 0.0F;
			matrix[8] = 0.0F;
			matrix[14] = screenWidth / (screenWidth - screenHeight);
			matrix[1] = 0.0F;
			matrix[4] = 0.0F;
			matrix[2] = 0.0F;
			matrix[9] = 0.0F;
			matrix[13] = (top + bottom) / (bottom - top);
			matrix[10] = 1.0F / (screenWidth - screenHeight);
			matrix[5] = 2.0F / (bottom - top);
			matrix[7] = 0.0F;
			matrix[15] = 1.0F;
		}
		this.updateOrthoFar();
		this.isOrthoSceneProjectionValid = true;
	}

	@OriginalMember(owner = "client!wga", name = "S", descriptor = "(I)V")
	public abstract void blendDrawBuffer();
}
