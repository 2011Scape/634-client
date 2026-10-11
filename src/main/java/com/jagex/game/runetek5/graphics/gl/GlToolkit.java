package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.client.GameCanvas;
import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlVboElementsArrayBuffer;
import com.jagex.game.runetek5.OpenGLArbTextureCubeMapGenerator;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.toolkit.opengl.OpenGLTextureCache;
import com.jagex.game.runetek5.config.rsinterface.GlClippingMask;
import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.cache.node.GlPointLight;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.GlRenderTarget;
import com.jagex.game.runetek5.Matrix43;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.DragOriginY;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.core.constants.PciVendorId;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.FramebufferCompositor;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.cache.node.MemoryPool;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.Sprite;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeap;
import jaggl.OpenGL;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.cache.node.GlMemoryPool;
import com.jagex.game.runetek5.graphics.font.GlFont;
import com.jagex.game.runetek5.graphics.model.GlModel;
import com.jagex.game.runetek5.graphics.shader.Texture3DShaderGL;
import com.jagex.game.runetek5.graphics.sprite.SpriteGL;
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture;
import com.jagex.game.runetek5.graphics.texture.RectangularTextureGL;
import com.jagex.game.runetek5.scene.ground.GlGround;
import com.jagex.game.runetek5.toolkit.opengl.EffectManagerGL;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlArrayBuffer;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlVboArrayBuffer;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.toolkit.OpenGlToolkit;
import com.jagex.game.runetek5.toolkit.opengl.GlParticleRenderer;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!qo")
public final class GlToolkit extends Toolkit {

	@OriginalMember(owner = "client!qo", name = "lb", descriptor = "Lclient!ej;")
	public static final ClientPacket SOUND_EFFECT_MUSIC_ENDED = new ClientPacket(52, 4);

	@OriginalMember(owner = "client!qo", name = "G", descriptor = "I")
	private int height;

	@OriginalMember(owner = "client!qo", name = "Uc", descriptor = "I")
	public int viewportHeight;

	@OriginalMember(owner = "client!qo", name = "Yc", descriptor = "I")
	private int width;

	@OriginalMember(owner = "client!qo", name = "Pd", descriptor = "I")
	public int viewportWidth;

	@OriginalMember(owner = "client!qo", name = "Fe", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	public NativeHeap nativeHeap;

	@OriginalMember(owner = "client!qo", name = "Ke", descriptor = "I")
	public int lastTickMillis;

	@OriginalMember(owner = "client!qo", name = "Oe", descriptor = "Lclient!rg;")
	private GlFramebuffer offscreenFramebuffer;

	@OriginalMember(owner = "client!qo", name = "Pe", descriptor = "Lclient!tv;")
	private GlRenderTarget drawTarget;

	@OriginalMember(owner = "client!qo", name = "Te", descriptor = "Lclient!nda;")
	private SpriteGL offscreenSprite;

	@OriginalMember(owner = "client!qo", name = "Ue", descriptor = "Lclient!tv;")
	private GlRenderTarget readTarget;

	@OriginalMember(owner = "client!qo", name = "We", descriptor = "I")
	public int vboBuffersSize;

	@OriginalMember(owner = "client!qo", name = "Xe", descriptor = "I")
	public int textureBuffersSize;

	@OriginalMember(owner = "client!qo", name = "Ze", descriptor = "I")
	private int renderBuffersSize;

	@OriginalMember(owner = "client!qo", name = "gf", descriptor = "J")
	private long lastGcTime;

	@OriginalMember(owner = "client!qo", name = "hf", descriptor = "Z")
	private boolean colormaskActive;

	@OriginalMember(owner = "client!qo", name = "jf", descriptor = "Z")
	private boolean enableDepthMask;

	@OriginalMember(owner = "client!qo", name = "lf", descriptor = "Z")
	private boolean alphaTestEnabled;

	@OriginalMember(owner = "client!qo", name = "mf", descriptor = "I")
	private int activeBlendType;

	@OriginalMember(owner = "client!qo", name = "nf", descriptor = "Z")
	private boolean depthTestEnabled;

	@OriginalMember(owner = "client!qo", name = "of", descriptor = "I")
	private int activeBlendAlpha;

	@OriginalMember(owner = "client!qo", name = "pf", descriptor = "I")
	private int functionMask;

	@OriginalMember(owner = "client!qo", name = "sf", descriptor = "F")
	public float sunBrightness;

	@OriginalMember(owner = "client!qo", name = "tf", descriptor = "Lclient!dp;")
	private GlEnvironmentSampler sunCubeTextureGenerator;

	@OriginalMember(owner = "client!qo", name = "wf", descriptor = "I")
	public int sunSlopeX;

	@OriginalMember(owner = "client!qo", name = "zf", descriptor = "Z")
	public boolean bigEndianess;

	@OriginalMember(owner = "client!qo", name = "Af", descriptor = "Lclient!eba;")
	private OpenGl2DImageTexture worldMapTexture;

	@OriginalMember(owner = "client!qo", name = "Bf", descriptor = "I")
	public int sunSlopeZ;

	@OriginalMember(owner = "client!qo", name = "Cf", descriptor = "Lclient!rg;")
	public GlFramebuffer integratedGraphicsImageProducer;

	@OriginalMember(owner = "client!qo", name = "Ef", descriptor = "Lclient!paa;")
	public GlModel copyBuffer1;

	@OriginalMember(owner = "client!qo", name = "Hf", descriptor = "Z")
	public boolean arbFragmentShader;

	@OriginalMember(owner = "client!qo", name = "If", descriptor = "Z")
	private boolean lightingEnabled;

	@OriginalMember(owner = "client!qo", name = "Jf", descriptor = "Lclient!paa;")
	public GlModel copyBuffer5;

	@OriginalMember(owner = "client!qo", name = "Mf", descriptor = "I")
	public int waterHeight;

	@OriginalMember(owner = "client!qo", name = "Nf", descriptor = "I")
	private int openglVersion;

	@OriginalMember(owner = "client!qo", name = "Of", descriptor = "Ljava/lang/String;")
	private String gpuNameString;

	@OriginalMember(owner = "client!qo", name = "Pf", descriptor = "Z")
	public boolean arbVertexShader;

	@OriginalMember(owner = "client!qo", name = "Qf", descriptor = "F")
	public float fogStartPosition;

	@OriginalMember(owner = "client!qo", name = "Rf", descriptor = "Z")
	public boolean framebufferObjectSupported;

	@OriginalMember(owner = "client!qo", name = "Tf", descriptor = "I")
	public int maxTextureUnits;

	@OriginalMember(owner = "client!qo", name = "Uf", descriptor = "I")
	private int lastMaterialId;

	@OriginalMember(owner = "client!qo", name = "Vf", descriptor = "Lclient!hca;")
	private ArrayBufferGL screenQuadArrayBuffer;

	@OriginalMember(owner = "client!qo", name = "Xf", descriptor = "I")
	private int maxTextureCoordinates;

	@OriginalMember(owner = "client!qo", name = "Zf", descriptor = "Z")
	private boolean fogEnabled;

	@OriginalMember(owner = "client!qo", name = "ag", descriptor = "Lclient!paa;")
	public GlModel copyBuffer2;

	@OriginalMember(owner = "client!qo", name = "bg", descriptor = "Lclient!paa;")
	public GlModel copyTarget3;

	@OriginalMember(owner = "client!qo", name = "dg", descriptor = "Z")
	private boolean hideLights;

	@OriginalMember(owner = "client!qo", name = "fg", descriptor = "Z")
	public boolean framebufferMultisample;

	@OriginalMember(owner = "client!qo", name = "ng", descriptor = "Z")
	public boolean framebufferBlit;

	@OriginalMember(owner = "client!qo", name = "pg", descriptor = "Lclient!ni;")
	private ElementsBufferGL glBinding;

	@OriginalMember(owner = "client!qo", name = "rg", descriptor = "I")
	private int maxTextureImageUnits;

	@OriginalMember(owner = "client!qo", name = "tg", descriptor = "Lclient!paa;")
	public GlModel copyBuffer3;

	@OriginalMember(owner = "client!qo", name = "ug", descriptor = "Z")
	private boolean advancedFramebuffers;

	@OriginalMember(owner = "client!qo", name = "wg", descriptor = "F")
	private float fogEndPosition;

	@OriginalMember(owner = "client!qo", name = "zg", descriptor = "Lclient!tp;")
	public OpenGl2DTexture texture2d;

	@OriginalMember(owner = "client!qo", name = "Ag", descriptor = "[Lclient!ud;")
	private GlTexture[] openglTextures;

	@OriginalMember(owner = "client!qo", name = "Cg", descriptor = "F")
	public float viewY2;

	@OriginalMember(owner = "client!qo", name = "Fg", descriptor = "Lclient!nh;")
	public OpenGlArrayDrawBuffer quadTexCoordBuffer;

	@OriginalMember(owner = "client!qo", name = "Gg", descriptor = "Lclient!paa;")
	public GlModel copyTarget4;

	@OriginalMember(owner = "client!qo", name = "Hg", descriptor = "F")
	public float viewX2;

	@OriginalMember(owner = "client!qo", name = "Ig", descriptor = "F")
	public float viewY1;

	@OriginalMember(owner = "client!qo", name = "Kg", descriptor = "Lclient!paa;")
	public GlModel copyTarget2;

	@OriginalMember(owner = "client!qo", name = "Lg", descriptor = "I")
	private int activeLightCount;

	@OriginalMember(owner = "client!qo", name = "Mg", descriptor = "Lclient!paa;")
	public GlModel copyTarget5;

	@OriginalMember(owner = "client!qo", name = "Ng", descriptor = "Z")
	public boolean arbVertexProgram;

	@OriginalMember(owner = "client!qo", name = "Og", descriptor = "F")
	private float projectionDepthOffset;

	@OriginalMember(owner = "client!qo", name = "Pg", descriptor = "Z")
	private boolean arbMultisample;

	@OriginalMember(owner = "client!qo", name = "Qg", descriptor = "Lclient!paa;")
	public GlModel copyBuffer4;

	@OriginalMember(owner = "client!qo", name = "Rg", descriptor = "Ljava/lang/String;")
	private String gpuCompanyString;

	@OriginalMember(owner = "client!qo", name = "Sg", descriptor = "Z")
	public boolean stillWaterToggled;

	@OriginalMember(owner = "client!qo", name = "Vg", descriptor = "Lclient!nh;")
	public OpenGlArrayDrawBuffer quadVertexBuffer;

	@OriginalMember(owner = "client!qo", name = "Yg", descriptor = "Lclient!hca;")
	private ArrayBufferGL currentArrayBuffer;

	@OriginalMember(owner = "client!qo", name = "ah", descriptor = "F")
	private float projectionDepthScale;

	@OriginalMember(owner = "client!qo", name = "gh", descriptor = "Z")
	private boolean textureUpdate;

	@OriginalMember(owner = "client!qo", name = "ih", descriptor = "I")
	private int lastSceneLightCount;

	@OriginalMember(owner = "client!qo", name = "jh", descriptor = "Z")
	public boolean arbTextureCubeMap;

	@OriginalMember(owner = "client!qo", name = "kh", descriptor = "Lclient!paa;")
	public GlModel copyTarget1;

	@OriginalMember(owner = "client!qo", name = "lh", descriptor = "I")
	private int depthClippingMode;

	@OriginalMember(owner = "client!qo", name = "mh", descriptor = "Z")
	private boolean pbuffersAvailable;

	@OriginalMember(owner = "client!qo", name = "sh", descriptor = "F")
	public float viewX1;

	@OriginalMember(owner = "client!qo", name = "wh", descriptor = "I")
	private int glTexturesCursorPos;

	@OriginalMember(owner = "client!qo", name = "yh", descriptor = "I")
	private int polygonCount;

	@OriginalMember(owner = "client!qo", name = "Bh", descriptor = "I")
	private int modelCount;

	@OriginalMember(owner = "client!qo", name = "Ib", descriptor = "Ljava/util/Hashtable;")
	private final Hashtable canvasSurfaces = new Hashtable();

	@OriginalMember(owner = "client!qo", name = "ne", descriptor = "I")
	public int size = 128;

	@OriginalMember(owner = "client!qo", name = "me", descriptor = "Lclient!sn;")
	private final GlParticleRenderer particleRenderer = new GlParticleRenderer();

	@OriginalMember(owner = "client!qo", name = "Ae", descriptor = "Lclient!iaa;")
	private final Matrix43 scratchMatrix = new Matrix43();

	@OriginalMember(owner = "client!qo", name = "Ee", descriptor = "Lclient!iaa;")
	public final Matrix43 billboardMatrix = new Matrix43();

	@OriginalMember(owner = "client!qo", name = "Ge", descriptor = "I")
	public int shadowScaleShift = 3;

	@OriginalMember(owner = "client!qo", name = "Je", descriptor = "Z")
	private boolean colortableReset = false;

	@OriginalMember(owner = "client!qo", name = "Ie", descriptor = "I")
	public int shadowScale = 8;

	@OriginalMember(owner = "client!qo", name = "He", descriptor = "Lclient!uh;")
	private final Deque heapManager = new Deque();

	@OriginalMember(owner = "client!qo", name = "Me", descriptor = "[Lclient!tv;")
	private final GlRenderTarget[] drawTargetStack = new GlRenderTarget[4];

	@OriginalMember(owner = "client!qo", name = "Ne", descriptor = "I")
	private int readTargetIndex = -1;

	@OriginalMember(owner = "client!qo", name = "Re", descriptor = "I")
	private int drawTargetIndex = -1;

	// Not part of the original client: with client.scale, the frame is drawn into this game-sized
	// framebuffer and copied to the window scaled at flip; see updateScaledTarget.
	private GlFramebuffer scaledTarget;
	private OpenGl2DTexture scaledColour;
	private OpenGlRenderBuffer scaledDepth;
	private int scaledWidth;
	private int scaledHeight;
	// With anti-aliasing, the frame is drawn into this multisampled framebuffer and resolved into
	// the scaled target before the copy to the window, as the window's own surface would have been.
	private GlFramebuffer scaledMultisample;
	private OpenGlRenderBuffer scaledMultisampleColour;
	private OpenGlRenderBuffer scaledMultisampleDepth;

	@OriginalMember(owner = "client!qo", name = "Qe", descriptor = "[Lclient!tv;")
	private final GlRenderTarget[] readTargetStack = new GlRenderTarget[4];

	@OriginalMember(owner = "client!qo", name = "Se", descriptor = "[Lclient!tv;")
	private final GlRenderTarget[] targetStack = new GlRenderTarget[4];

	@OriginalMember(owner = "client!qo", name = "Ve", descriptor = "I")
	private int targetIndex = -1;

	@OriginalMember(owner = "client!qo", name = "Ye", descriptor = "Lclient!uh;")
	private final Deque listsAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "af", descriptor = "Lclient!uh;")
	private final Deque vboBuffersAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "bf", descriptor = "Lclient!uh;")
	private final Deque texturesAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "cf", descriptor = "Lclient!uh;")
	private final Deque frameBuffersAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "df", descriptor = "Lclient!uh;")
	private final Deque renderBuffersAwaitingRemoval;

	@OriginalMember(owner = "client!qo", name = "ef", descriptor = "Lclient!uh;")
	private final Deque arbProgramsAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "ff", descriptor = "Lclient!uh;")
	private final Deque shaderProgramsAwaitingDeallocation;

	@OriginalMember(owner = "client!qo", name = "kf", descriptor = "Lclient!iaa;")
	public final Matrix43 viewMatrix43;

	@OriginalMember(owner = "client!qo", name = "qf", descriptor = "Lclient!iaa;")
	public final Matrix43 modelViewMatrix;

	@OriginalMember(owner = "client!qo", name = "rf", descriptor = "Lclient!iaa;")
	public final Matrix43 inverseModelViewMatrix;

	@OriginalMember(owner = "client!qo", name = "uf", descriptor = "I")
	private int viewportOffsetX;

	@OriginalMember(owner = "client!qo", name = "Kf", descriptor = "F")
	private float fogMin;

	@OriginalMember(owner = "client!qo", name = "xf", descriptor = "I")
	private int clipTop;

	@OriginalMember(owner = "client!qo", name = "Ff", descriptor = "F")
	public float sunColorRed;

	@OriginalMember(owner = "client!qo", name = "eg", descriptor = "I")
	public int waterFogColour;

	@OriginalMember(owner = "client!qo", name = "vf", descriptor = "I")
	public int projectionScaleX;

	@OriginalMember(owner = "client!qo", name = "Yf", descriptor = "F")
	private float outlineCirclePointSize;

	@OriginalMember(owner = "client!qo", name = "gg", descriptor = "I")
	private int sunColorRgb;

	@OriginalMember(owner = "client!qo", name = "og", descriptor = "F")
	public float sunColorGreen;

	@OriginalMember(owner = "client!qo", name = "cg", descriptor = "I")
	private int textureCombineRgb;

	@OriginalMember(owner = "client!qo", name = "Bg", descriptor = "F")
	private float fillCirclePointSize;

	@OriginalMember(owner = "client!qo", name = "ig", descriptor = "I")
	public int projectionCenterY;

	@OriginalMember(owner = "client!qo", name = "Eg", descriptor = "F")
	public float sunColorIntensity;

	@OriginalMember(owner = "client!qo", name = "xg", descriptor = "[F")
	public final float[] sunDirection;

	@OriginalMember(owner = "client!qo", name = "hg", descriptor = "I")
	public int waterIntensity;

	@OriginalMember(owner = "client!qo", name = "jg", descriptor = "[F")
	private final float[] lightingDirection;

	@OriginalMember(owner = "client!qo", name = "vg", descriptor = "F")
	public float sunColorBlue;

	@OriginalMember(owner = "client!qo", name = "Sf", descriptor = "I")
	public int projectionScaleY;

	@OriginalMember(owner = "client!qo", name = "mg", descriptor = "I")
	private int zFar;

	@OriginalMember(owner = "client!qo", name = "bh", descriptor = "I")
	private int clipRight;

	@OriginalMember(owner = "client!qo", name = "dh", descriptor = "F")
	private float densityFactor;

	@OriginalMember(owner = "client!qo", name = "Lf", descriptor = "I")
	private int clipBottom;

	@OriginalMember(owner = "client!qo", name = "fh", descriptor = "Z")
	private boolean renderStillWater;

	@OriginalMember(owner = "client!qo", name = "Wg", descriptor = "[F")
	private final float[] sunPreviousPosition;

	@OriginalMember(owner = "client!qo", name = "Xg", descriptor = "F")
	private float orthoZoom;

	@OriginalMember(owner = "client!qo", name = "yg", descriptor = "I")
	public int zNear;

	@OriginalMember(owner = "client!qo", name = "hh", descriptor = "I")
	private int textureCombineAlpha;

	@OriginalMember(owner = "client!qo", name = "lg", descriptor = "I")
	public int fogOffset;

	@OriginalMember(owner = "client!qo", name = "ch", descriptor = "I")
	public int fogColor;

	@OriginalMember(owner = "client!qo", name = "oh", descriptor = "I")
	private int clipLeft;

	@OriginalMember(owner = "client!qo", name = "Df", descriptor = "[F")
	private final float[] projectionMatrix;

	@OriginalMember(owner = "client!qo", name = "qh", descriptor = "Z")
	private boolean depthBufferingEnabled;

	@OriginalMember(owner = "client!qo", name = "rh", descriptor = "F")
	public float antiSunColorIntensity;

	@OriginalMember(owner = "client!qo", name = "eh", descriptor = "[Lclient!ab;")
	private final PointLight[] lightNodes;

	@OriginalMember(owner = "client!qo", name = "Zg", descriptor = "I")
	public int projectionCenterX;

	@OriginalMember(owner = "client!qo", name = "nh", descriptor = "I")
	private int viewportOffsetY;

	@OriginalMember(owner = "client!qo", name = "vh", descriptor = "F")
	public float farClipDistance;

	@OriginalMember(owner = "client!qo", name = "uh", descriptor = "F")
	public float projectedFarClipDistance;

	@OriginalMember(owner = "client!qo", name = "Ug", descriptor = "I")
	public int waterDepth;

	@OriginalMember(owner = "client!qo", name = "xh", descriptor = "[F")
	private final float[] antiSunPreviousPosition;

	@OriginalMember(owner = "client!qo", name = "Gf", descriptor = "I")
	public int fogDensity;

	@OriginalMember(owner = "client!qo", name = "Wf", descriptor = "Lclient!gh;")
	public GlBitBuffer textureBuffer;

	@OriginalMember(owner = "client!qo", name = "zh", descriptor = "[B")
	public final byte[] shadowTextureData;

	@OriginalMember(owner = "client!qo", name = "Ah", descriptor = "[I")
	public int[] scratchVerticesX;

	@OriginalMember(owner = "client!qo", name = "Dh", descriptor = "[I")
	public int[] scratchVerticesY;

	@OriginalMember(owner = "client!qo", name = "Ch", descriptor = "[I")
	public int[] visibleTileIndices;

	@OriginalMember(owner = "client!qo", name = "Fc", descriptor = "Ljava/awt/Canvas;")
	private final Canvas canvas;

	@OriginalMember(owner = "client!qo", name = "Gb", descriptor = "Ljava/awt/Canvas;")
	private Canvas currentCanvas;

	@OriginalMember(owner = "client!qo", name = "oe", descriptor = "I")
	public final int multiSamplesPreference;

	@OriginalMember(owner = "client!qo", name = "rd", descriptor = "Lclient!jaggl/OpenGL;")
	private OpenGL activeOpenglInstance;

	@OriginalMember(owner = "client!qo", name = "kb", descriptor = "J")
	private final long canvasSurface;

	@OriginalMember(owner = "client!qo", name = "Pc", descriptor = "J")
	private long currentSurface;

	@OriginalMember(owner = "client!qo", name = "kg", descriptor = "I")
	public final int pixelType;

	@OriginalMember(owner = "client!qo", name = "th", descriptor = "Z")
	private boolean vertexbufferSupported;

	@OriginalMember(owner = "client!qo", name = "Dg", descriptor = "Z")
	public boolean texture3dSupported;

	@OriginalMember(owner = "client!qo", name = "qg", descriptor = "Z")
	public boolean arbTextureFloat;

	@OriginalMember(owner = "client!qo", name = "Jg", descriptor = "Z")
	public boolean arbRectangleTexturesSupported;

	@OriginalMember(owner = "client!qo", name = "Tg", descriptor = "Z")
	public boolean isAmd;

	@OriginalMember(owner = "client!qo", name = "ph", descriptor = "Z")
	private boolean shouldUseArbVbo;

	@OriginalMember(owner = "client!qo", name = "sg", descriptor = "Z")
	public boolean extFramebufferObject;

	@OriginalMember(owner = "client!qo", name = "yf", descriptor = "Z")
	private final boolean s3Graphics;

	@OriginalMember(owner = "client!qo", name = "ye", descriptor = "Lclient!eo;")
	private final OpenGLTextureCache textureCache;

	@OriginalMember(owner = "client!qo", name = "Le", descriptor = "Lclient!qi;")
	public final Texture3DShaderGL texture3dShader;

	@OriginalMember(owner = "client!qo", name = "ve", descriptor = "Lclient!hh;")
	private final FramebufferCompositor framebufferCompositor;

	@OriginalMember(owner = "client!qo", name = "we", descriptor = "Lclient!rda;")
	private RectangularTextureGL rectangularTexture;

	@OriginalMember(owner = "client!qo", name = "ie", descriptor = "Lclient!mea;")
	private final EffectManagerGL effectManager;

	@OriginalMember(owner = "client!qo", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;I)V")
	public GlToolkit(@OriginalArg(0) Canvas canvas, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) int multiSamples) {
		super(textureSource);
		new Queue();
		new IterableHashTable(16);
		this.listsAwaitingDeallocation = new Deque();
		this.vboBuffersAwaitingDeallocation = new Deque();
		this.texturesAwaitingDeallocation = new Deque();
		this.frameBuffersAwaitingDeallocation = new Deque();
		this.renderBuffersAwaitingRemoval = new Deque();
		this.arbProgramsAwaitingDeallocation = new Deque();
		this.shaderProgramsAwaitingDeallocation = new Deque();
		this.viewMatrix43 = new Matrix43();
		this.modelViewMatrix = new Matrix43();
		this.inverseModelViewMatrix = new Matrix43();
		this.viewportOffsetX = 0;
		this.fogMin = 0.0F;
		this.clipTop = 0;
		this.sunColorRed = 1.0F;
		this.waterFogColour = -1;
		this.projectionScaleX = 512;
		this.outlineCirclePointSize = -1.0F;
		this.sunColorRgb = -1;
		this.sunColorGreen = 1.0F;
		this.textureCombineRgb = 8448;
		this.fillCirclePointSize = -1.0F;
		this.projectionCenterY = 0;
		this.sunColorIntensity = -1.0F;
		this.sunDirection = new float[4];
		this.waterIntensity = 0;
		this.lightingDirection = new float[4];
		this.sunColorBlue = 1.0F;
		this.projectionScaleY = 512;
		this.zFar = 3584;
		this.clipRight = 0;
		this.densityFactor = 1.0F;
		this.clipBottom = 0;
		this.renderStillWater = false;
		this.sunPreviousPosition = new float[4];
		this.orthoZoom = 1.0F;
		this.zNear = 50;
		this.textureCombineAlpha = 8448;
		this.fogOffset = 0;
		this.fogColor = -1;
		this.clipLeft = 0;
		this.projectionMatrix = new float[16];
		this.depthBufferingEnabled = true;
		this.antiSunColorIntensity = -1.0F;
		this.lightNodes = new PointLight[GlToolkit.MAX_LIGHTS];
		this.projectionCenterX = 0;
		this.viewportOffsetY = 0;
		this.farClipDistance = 3584.0F;
		this.projectedFarClipDistance = 3584.0F;
		this.waterDepth = -1;
		this.antiSunPreviousPosition = new float[4];
		this.fogDensity = -1;
		this.textureBuffer = new GlBitBuffer(8192);
		this.shadowTextureData = new byte[16384];
		this.scratchVerticesX = new int[1];
		this.scratchVerticesY = new int[1];
		this.visibleTileIndices = new int[1];
		this.currentCanvas = this.canvas = canvas;
		// Not part of the original client: with the anti-aliasing pipeline the window has no samples;
		// the 3D view is multisampled in its own framebuffer instead (beginScene).
		this.sceneSettingSamples = multiSamples;
		this.multiSamplesPreference = ClientFeatures.AA_PIPELINE && !ClientFeatures.aaPipelineUnavailable ? 0 : multiSamples;
		if (!NativeLibraryOwner.loadLibrary("jaclib")) {
			throw new RuntimeException("");
		} else if (NativeLibraryOwner.loadLibrary("jaggl")) {
			try {
				this.activeOpenglInstance = new OpenGL();
				this.currentSurface = this.canvasSurface = this.activeOpenglInstance.init(canvas, 8, 8, 8, 24, 0, this.multiSamplesPreference);
				if (this.canvasSurface == 0L) {
					throw new RuntimeException("");
				}
				this.attach();
				@Pc(335) int capabilityErrors = this.accumulateCapabilities();
				if (capabilityErrors != 0) {
					throw new RuntimeException("");
				}
				this.pixelType = this.bigEndianess ? 33639 : 5121;
				if (this.gpuNameString.indexOf("radeon") != -1) {
					@Pc(361) int radeonModel = 0;
					@Pc(363) boolean isHd = false;
					@Pc(365) boolean hasXPrefix = false;
					@Pc(374) String[] strings = Utils.split(' ', this.gpuNameString.replace('/', ' '));
					for (@Pc(376) int i = 0; i < strings.length; i++) {
						@Pc(382) String string = strings[i];
						try {
							if (string.length() > 0) {
								if (string.charAt(0) == 'x' && string.length() >= 3 && Utils.isValidInteger(string.substring(1, 3))) {
									string = string.substring(1);
									hasXPrefix = true;
								}
								if (string.equals("hd")) {
									isHd = true;
								} else {
									if (string.startsWith("hd")) {
										isHd = true;
										string = string.substring(2);
									}
									if (string.length() >= 4 && Utils.isValidInteger(string.substring(0, 4))) {
										radeonModel = Utils.parseInt(string.substring(0, 4));
										break;
									}
								}
							}
						} catch (@Pc(452) Exception ex) {
						}
					}
					if (!hasXPrefix && !isHd) {
						if (radeonModel >= 7000 && radeonModel <= 7999) {
							this.vertexbufferSupported = false;
						}
						if (radeonModel >= 7000 && radeonModel <= 9250) {
							this.texture3dSupported = false;
						}
					}
					if (!isHd || radeonModel < 4000) {
						this.arbTextureFloat = false;
					}
					this.arbRectangleTexturesSupported &= this.activeOpenglInstance.a("GL_ARB_half_float_pixel");
					this.isAmd = true;
					this.shouldUseArbVbo = this.vertexbufferSupported;
				}
				if (this.gpuCompanyString.indexOf("intel") != -1) {
					this.extFramebufferObject = false;
				}
				this.checkScenePipeline(); // Not part of the original client: see checkScenePipeline.
				this.s3Graphics = !this.gpuCompanyString.equals("s3 graphics");
				if (this.vertexbufferSupported) {
					try {
						@Pc(535) int[] bufferIds = new int[1];
						OpenGL.glGenBuffersARB(1, bufferIds, 0);
					} catch (@Pc(541) Throwable ex2) {
						throw new RuntimeException("");
					}
				}
				ColorUtil.initColorTables(false, true);
				this.colortableReset = true;
				this.textureCache = new OpenGLTextureCache(this, super.materialProvider);
				this.buildTables();
				this.texture3dShader = new Texture3DShaderGL(this);
				this.framebufferCompositor = new FramebufferCompositor(this);
				if (this.framebufferCompositor.isDisplayAvailable()) {
					this.rectangularTexture = new RectangularTextureGL(this);
					if (!this.rectangularTexture.isSupported()) {
						this.rectangularTexture.destroy();
						this.rectangularTexture = null;
					}
				}
				this.effectManager = new EffectManagerGL(this);
				this.prepareForRendering();
				this.updateCanvasSize();
				this.renderLastFrame();
			} catch (@Pc(615) Throwable ex3) {
				ex3.printStackTrace();
				this.stop();
				throw new RuntimeException("");
			}
		} else {
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!qo", name = "L", descriptor = "(III)V")
	@Override
	public void L(@OriginalArg(0) int fogColour, @OriginalArg(1) int fogDensity, @OriginalArg(2) int fogOffset) {
		if (this.fogColor == fogColour && this.fogDensity == fogDensity && this.fogOffset == fogOffset) {
			return;
		}
		this.fogDensity = fogDensity;
		this.fogColor = fogColour;
		this.fogOffset = fogOffset;
		this.updateFog();
		this.toggleFog();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZI)V")
	public void setLightingEnabled(@OriginalArg(0) boolean enabled) {
		if (this.lightingEnabled != enabled) {
			this.lightingEnabled = enabled;
			this.toggleLighting();
			this.functionMask &= 0xFFFFFFF8;
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	@Override
	public Model createModel(@OriginalArg(0) Mesh mesh, @OriginalArg(1) int functionMask, @OriginalArg(2) int featureMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast) {
		return new GlModel(this, mesh, functionMask, ambient, contrast, featureMask);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IBI)V")
	public void loadTextureCombineMode(@OriginalArg(0) int combineRgb, @OriginalArg(2) int combineAlpha) {
		if (this.glTexturesCursorPos != 0) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha);
			return;
		}
		@Pc(14) boolean changed = false;
		if (this.textureCombineRgb != combineRgb) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, combineRgb);
			changed = true;
			this.textureCombineRgb = combineRgb;
		}
		if (this.textureCombineAlpha != combineAlpha) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, combineAlpha);
			this.textureCombineAlpha = combineAlpha;
			changed = true;
		}
		if (changed) {
			this.functionMask &= 0xFFFFFFE2;
		}
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(B)V")
	private void loadProjectionMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadMatrixf(this.projectionMatrix, 0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!wh;)V")
	@Override
	public void setSunCubeTexture(@OriginalArg(0) EnvironmentSampler environmentSampler) {
		this.sunCubeTextureGenerator = (GlEnvironmentSampler) environmentSampler;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IZI[BI)Lclient!hca;")
	public ArrayBufferGL createArrayBuffer(@OriginalArg(1) boolean isDynamic, @OriginalArg(2) int size, @OriginalArg(3) byte[] buffer, @OriginalArg(4) int position) {
		return (ArrayBufferGL) (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo) ? new GlVboArrayBuffer(this, size, buffer, position, isDynamic) : new GlArrayBuffer(this, size, buffer, position));
	}

	@OriginalMember(owner = "client!qo", name = "DA", descriptor = "(IIII)V")
	@Override
	public void DA(@OriginalArg(0) int centerX, @OriginalArg(1) int centerY, @OriginalArg(2) int scaleX, @OriginalArg(3) int scaleY) {
		this.projectionScaleX = scaleX;
		this.projectionScaleY = scaleY;
		this.projectionCenterY = centerY;
		this.projectionCenterX = centerX;
		this.updateProjectionMatrix();
		this.updateViewBounds();
		if (this.depthClippingMode == 3) {
			this.loadOrthoProjection();
		} else if (this.depthClippingMode == 2) {
			this.loadProjectionMatrix();
		}
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(I)V")
	private void toggleFog() {
		if (this.fogEnabled && this.fogDensity >= 0) {
			OpenGL.glEnable(OpenGLConstants.GL_FOG);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_FOG);
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(III[BZ)Lclient!ni;")
	public ElementsBufferGL createElementsArrayBuffer(@OriginalArg(2) int size, @OriginalArg(3) byte[] data, @OriginalArg(4) boolean isDynamic) {
		return (ElementsBufferGL) (this.vertexbufferSupported && (!isDynamic || this.shouldUseArbVbo) ? new GlVboElementsArrayBuffer(this, 5123, data, size, isDynamic) : new GlElementsArrayBuffer(this, 5123, data, size));
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(Z)V")
	public void popMatrix() {
		OpenGL.glPopMatrix();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	@Override
	public PointLight createPointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int range, @OriginalArg(5) float intensity) {
		return new GlPointLight(x, y, z, radius, range, intensity);
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(I)V")
	public void setupTextRendering() {
		if (this.functionMask == 4) {
			return;
		}
		this.setScreenProjectionMode();
		this.toggleFog(false);
		this.setLightingEnabled(false);
		this.toggleZbuffer(false);
		this.setDepthMask(false);
		this.renderTexture(-2);
		this.setBlendMode(1);
		this.functionMask = 4;
	}

	@OriginalMember(owner = "client!qo", name = "XA", descriptor = "()I")
	@Override
	public int XA() {
		return this.zFar;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	@Override
	public void drawSurfaceLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY) {
		@Pc(6) GlClippingMask mask = (GlClippingMask) clippingMask;
		@Pc(9) OpenGl2DImageTexture surface = mask.surface;
		this.setupSpriteRendering();
		this.setActiveTexture(mask.surface);
		this.setBlendMode(1);
		this.loadTextureCombineMode(7681, 8448);
		this.setTexture(34167, 768, 0);
		@Pc(39) float texScaleS = surface.s / (float) surface.singleWidth;
		@Pc(46) float texScaleT = surface.t / (float) surface.singleLength;
		@Pc(52) float dx = (float) endX - (float) startX;
		@Pc(58) float dy = (float) endY - (float) startY;
		@Pc(71) float inverseLength = (float) (1.0D / Math.sqrt((double) (dy * dy + dx * dx)));
		@Pc(75) float dirX = dx * inverseLength;
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		@Pc(94) float dirY = dy * inverseLength;
		OpenGL.glBegin(OpenGLConstants.GL_LINES);
		OpenGL.glTexCoord2f((float) (startX - maskX) * texScaleS, texScaleT * (float) (startY - maskY));
		OpenGL.glVertex2f((float) startX + 0.35F, (float) startY + 0.35F);
		OpenGL.glTexCoord2f(texScaleS * (float) (endX - maskX), (float) (endY - maskY) * texScaleT);
		OpenGL.glVertex2f(dirX + (float) endX + 0.35F, (float) endY + 0.35F + dirY);
		OpenGL.glEnd();
		this.setTexture(5890, 768, 0);
	}

	@OriginalMember(owner = "client!qo", name = "Y", descriptor = "()[I")
	@Override
	public int[] Y() {
		return new int[] { this.projectionCenterX, this.projectionCenterY, this.projectionScaleX, this.projectionScaleY };
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IBI)V")
	public synchronized void requestTextureCleanup(@OriginalArg(0) int handle, @OriginalArg(2) int sizeRequirement) {
		@Pc(8) IntNode intNode = new IntNode(sizeRequirement);
		intNode.key = handle;
		this.texturesAwaitingDeallocation.addLast(intNode);
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(II)V")
	public void activateTexture(@OriginalArg(1) int textureUnit) {
		if (textureUnit != this.glTexturesCursorPos) {
			OpenGL.glActiveTexture(textureUnit + OpenGLConstants.GL_TEXTURE0);
			this.glTexturesCursorPos = textureUnit;
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!jaclib/memory/Buffer;ZII)Lclient!hca;")
	public ArrayBufferGL createDataArrayBuffer(@OriginalArg(1) Buffer buffer, @OriginalArg(3) int stride, @OriginalArg(4) int size) {
		return (ArrayBufferGL) (this.vertexbufferSupported ? new GlVboArrayBuffer(this, stride, buffer, size, false) : new GlArrayBuffer(this, stride, buffer));
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(I)V")
	private void setModelViewMatrixMode() {
		if (this.depthClippingMode != 2) {
			this.depthClippingMode = 2;
			this.loadProjectionMatrix();
			this.updateModelViewAndLights();
			this.functionMask &= 0xFFFFFFF8;
		}
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "()Z")
	@Override
	public boolean hasTextures() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(I)Lclient!q;")
	public GlCubeTexture getCubeMap() {
		return this.sunCubeTextureGenerator == null ? null : this.sunCubeTextureGenerator.getCubeTexture();
	}

	@OriginalMember(owner = "client!qo", name = "HA", descriptor = "(IIII[I)V")
	@Override
	public void HA(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int projectionDepth, @OriginalArg(4) int[] output) {
		@Pc(28) float depth = (float) z * this.viewMatrix43.zC + (float) y * this.viewMatrix43.zB + this.viewMatrix43.zA * (float) x + this.viewMatrix43.zD;
		if ((float) this.zNear > depth || (float) this.zFar < depth) {
			output[0] = output[1] = output[2] = -1;
			return;
		}
		@Pc(86) int screenX = (int) ((this.viewMatrix43.xD + this.viewMatrix43.xB * (float) y + (float) x * this.viewMatrix43.xA + this.viewMatrix43.xC * (float) z) * (float) this.projectionScaleX / (float) projectionDepth);
		@Pc(119) int screenY = (int) ((float) this.projectionScaleY * (this.viewMatrix43.yD + this.viewMatrix43.yA * (float) x + (float) y * this.viewMatrix43.yB + (float) z * this.viewMatrix43.yC) / (float) projectionDepth);
		if ((float) screenX >= this.viewX1 && this.viewX2 >= (float) screenX && this.viewY1 <= (float) screenY && this.viewY2 >= (float) screenY) {
			output[2] = (int) depth;
			output[1] = (int) ((float) screenY - this.viewY1);
			output[0] = (int) ((float) screenX - this.viewX1);
		} else {
			output[0] = output[1] = output[2] = -1;
		}
	}

	@OriginalMember(owner = "client!qo", name = "EA", descriptor = "(IIII)V")
	@Override
	public void EA(@OriginalArg(0) int waterHeight, @OriginalArg(1) int fogColour, @OriginalArg(2) int depth, @OriginalArg(3) int intensity) {
		if (!this.stillWaterToggled) {
			throw new RuntimeException("");
		}
		this.waterHeight = waterHeight;
		this.waterFogColour = fogColour;
		this.waterDepth = depth;
		this.waterIntensity = intensity;
		if (this.renderStillWater) {
			this.effectManager.waterEffect.bindProgram();
			this.effectManager.waterEffect.updateFogParams();
		}
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(I)V")
	private void setOrthoProjectionMode() {
		if (this.depthClippingMode != 3) {
			this.depthClippingMode = 3;
			this.loadOrthoProjection();
			this.updateModelViewAndLights();
			this.functionMask &= 0xFFFFFFF8;
		}
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "()Z")
	@Override
	public boolean supportsFullscreen() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FIF)V")
	public void adjustFog(@OriginalArg(0) float fogMin, @OriginalArg(2) float densityFactor) {
		this.fogMin = fogMin;
		this.densityFactor = densityFactor;
		this.updateFog();
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(B)V")
	private void updateCanvasSize() {
		if (this.currentCanvas == null) {
			this.width = this.height = 0;
		} else {
			@Pc(10) Dimension dimension = this.currentCanvas.getSize();
			this.width = dimension.width;
			this.height = dimension.height;
		}
		this.updateScaledTarget();
		if (this.drawTarget == null) {
			this.viewportWidth = this.width;
			this.viewportHeight = this.height;
			this.updateViewport();
		}
		this.setOrthoMatrixMode();
		this.la();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZIZI)V")
	public void renderTexture(@OriginalArg(0) boolean hasLight, @OriginalArg(1) int materialId, @OriginalArg(2) boolean allowStillWater) {
		if (materialId != this.lastMaterialId || this.renderStillWater != this.stillWaterToggled) {
			@Pc(22) OpenGl2DTexture texture = null;
			@Pc(24) int combineMode = 0;
			@Pc(26) byte effectParam1 = 0;
			@Pc(28) int effectParam2 = 0;
			@Pc(35) int effectType = this.stillWaterToggled ? 3 : 0;
			if (materialId < 0) {
				this.renderStationaryTexture();
				this.setSceneAlphaToCoverage(false); // Not part of the original client: see setSceneAlphaToCoverage.
			} else {
				texture = this.textureCache.getTexture(materialId);
				@Pc(49) MaterialType textureMetrics = super.materialProvider.getMetrics(materialId);
				this.setSceneAlphaToCoverage(textureMetrics.effectCombiner == 1); // Not part of the original client: see setSceneAlphaToCoverage.
				if (textureMetrics.speedU == 0 && textureMetrics.speedV == 0) {
					this.renderStationaryTexture();
				} else {
					@Pc(62) int textureSize = textureMetrics.isSmall ? 64 : 128;
					@Pc(66) int animationPeriod = textureSize * 50;
					this.renderAnimatedTexture((float) (this.lastTickMillis % animationPeriod * textureMetrics.speedV) / (float) animationPeriod, 0.0F, (float) (textureMetrics.speedU * (this.lastTickMillis % animationPeriod)) / (float) animationPeriod);
				}
				if (!this.stillWaterToggled) {
					effectType = textureMetrics.effectType;
					effectParam2 = textureMetrics.effectParam2;
					effectParam1 = textureMetrics.effectParam1;
				}
				combineMode = textureMetrics.combineMode;
			}
			this.effectManager.loadEffect(effectParam1, hasLight, effectType, effectParam2, allowStillWater);
			if (!this.effectManager.bindTexture(combineMode, texture)) {
				this.setActiveTexture(texture);
				this.setColourOp(combineMode);
			}
			this.renderStillWater = this.stillWaterToggled;
			this.lastMaterialId = materialId;
		}
		this.functionMask &= 0xFFFFFFF8;
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(B)V")
	private void loadOrthoProjection() {
		@Pc(15) float left = (float) -this.projectionCenterX * this.orthoZoom / (float) this.projectionScaleX;
		@Pc(27) float top = this.orthoZoom * (float) -this.projectionCenterY / (float) this.projectionScaleY;
		@Pc(42) float right = this.orthoZoom * (float) (this.viewportWidth - this.projectionCenterX) / (float) this.projectionScaleX;
		@Pc(56) float bottom = this.orthoZoom * (float) (this.viewportHeight - this.projectionCenterY) / (float) this.projectionScaleY;
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadIdentity();
		if (left != right && top != bottom) {
			OpenGL.glOrtho((double) left, (double) right, (double) -bottom, (double) -top, (double) this.zNear, (double) this.zFar);
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "()Z")
	@Override
	public boolean supportsBloom() {
		return this.rectangularTexture != null && (this.multiSamplesPreference <= 1 || this.advancedFramebuffers);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IZ)V")
	public void setDepthMask(@OriginalArg(1) boolean enableMask) {
		if (this.enableDepthMask != enableMask) {
			this.enableDepthMask = enableMask;
			this.setDepthMask();
			this.functionMask &= 0xFFFFFFE0;
		}
	}

	@OriginalMember(owner = "client!qo", name = "K", descriptor = "([I)V")
	@Override
	public void K(@OriginalArg(0) int[] bounds) {
		bounds[1] = this.clipTop;
		bounds[3] = this.clipBottom;
		bounds[0] = this.clipLeft;
		bounds[2] = this.clipRight;
	}

	@OriginalMember(owner = "client!qo", name = "Q", descriptor = "(IIIIII[BII)V")
	@Override
	public void Q(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int envColour, @OriginalArg(6) byte[] alphaData, @OriginalArg(7) int maskWidth, @OriginalArg(8) int blendMode) {
		@Pc(45) float maxU;
		@Pc(49) float maxV;
		if (this.worldMapTexture == null || this.worldMapTexture.width < width || this.worldMapTexture.length < height) {
			this.worldMapTexture = OpenGl2DImageTexture.createSurface(alphaData, height, this, width);
			this.worldMapTexture.setRepeat(false, false);
			maxU = this.worldMapTexture.t;
			maxV = this.worldMapTexture.s;
		} else {
			this.worldMapTexture.uploadTextureData(width, false, height, 6406, alphaData);
			maxU = (float) height * this.worldMapTexture.t / (float) this.worldMapTexture.length;
			maxV = (float) width * this.worldMapTexture.s / (float) this.worldMapTexture.width;
		}
		this.setupSpriteRendering();
		this.setActiveTexture(this.worldMapTexture);
		this.setBlendMode(blendMode);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		this.setTextureEnvColour(envColour);
		this.loadTextureCombineMode(34165, 34165);
		this.setTexture(34166, 768, 0);
		this.setTexture(5890, 770, 2);
		this.setTextureTransparency(0, 34166);
		this.setTextureTransparency(2, 5890);
		@Pc(151) float left = (float) x;
		@Pc(154) float top = (float) y;
		@Pc(159) float right = (float) width + left;
		@Pc(164) float bottom = (float) height + top;
		OpenGL.glBegin(7);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2f(left, top);
		OpenGL.glTexCoord2f(0.0F, maxV);
		OpenGL.glVertex2f(left, bottom);
		OpenGL.glTexCoord2f(maxU, maxV);
		OpenGL.glVertex2f(right, bottom);
		OpenGL.glTexCoord2f(maxU, 0.0F);
		OpenGL.glVertex2f(right, top);
		OpenGL.glEnd();
		this.setTexture(5890, 768, 0);
		this.setTexture(34166, 770, 2);
		this.setTextureTransparency(0, 5890);
		this.setTextureTransparency(2, 34166);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!mda;)V")
	@Override
	public void render(@OriginalArg(0) ParticleList particleList) {
		this.particleRenderer.drawParticles(this, -1, particleList);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFFBF)V")
	public void updatePointSizeRange(@OriginalArg(0) float alpha, @OriginalArg(1) float red, @OriginalArg(2) float blue, @OriginalArg(4) float green) {
		OpenGlToolkit.BUFFER_4F[2] = blue;
		OpenGlToolkit.BUFFER_4F[0] = red;
		OpenGlToolkit.BUFFER_4F[1] = green;
		OpenGlToolkit.BUFFER_4F[3] = alpha;
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void setCamera(@OriginalArg(0) Matrix matrix) {
		this.viewMatrix43.apply(matrix);
		this.modelViewMatrix.apply(this.viewMatrix43);
		this.modelViewMatrix.negateYZ();
		this.inverseModelViewMatrix.setInverse(this.modelViewMatrix);
		if (this.depthClippingMode != 1) {
			this.updateModelViewAndLights();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!hca;)V")
	public void setArrayBuffer(@OriginalArg(1) ArrayBufferGL arrayBuffer) {
		if (arrayBuffer != this.currentArrayBuffer) {
			if (this.vertexbufferSupported) {
				OpenGL.glBindBufferARB(OpenGLConstants.GL_ARRAY_BUFFER, arrayBuffer.getId());
			}
			this.currentArrayBuffer = arrayBuffer;
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	@Override
	public void drawTriangle() {
		this.enter2dMode();
		this.setBlendMode(1);
		OpenGL.glBegin(OpenGLConstants.GL_TRIANGLES);
		OpenGL.glColor4ub((byte) -1, (byte) 0, (byte) 0, (byte) -1);
		OpenGL.glVertex3f((float) 5 + 0.35F, (float) 10 + 0.35F, (float) 100);
		OpenGL.glColor4ub((byte) -1, (byte) 0, (byte) 0, (byte) -1);
		OpenGL.glVertex3f((float) 75 + 0.35F, (float) 50 + 0.35F, (float) 100);
		OpenGL.glColor4ub((byte) -1, (byte) 0, (byte) 0, (byte) -1);
		OpenGL.glVertex3f((float) 15 + 0.35F, (float) 90 + 0.35F, (float) 100);
		OpenGL.glEnd();
	}

	@OriginalMember(owner = "client!qo", name = "p", descriptor = "()V")
	@Override
	public void restoreSurface() {
		if (this.extFramebufferObject) {
			if (this.drawTarget != this.offscreenFramebuffer) {
				throw new RuntimeException();
			}
			this.offscreenFramebuffer.clearAttachment(0);
			this.offscreenFramebuffer.clearAttachment(8);
			this.popTarget(this.offscreenFramebuffer);
		} else if (this.pbuffersAvailable) {
			this.offscreenSprite.captureScreenRegion(0, 0, this.viewportWidth, this.viewportHeight, 0, 0);
			this.activeOpenglInstance.setSurface(this.currentSurface);
		} else {
			throw new RuntimeException("");
		}
		this.viewportWidth = this.width;
		this.offscreenSprite = null;
		this.viewportHeight = this.height;
		this.setOrthoMatrixMode();
		this.updateViewport();
		this.la();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!tv;I)V")
	public void pushDrawTarget(@OriginalArg(0) GlRenderTarget target) {
		if (this.drawTargetIndex >= 3) {
			throw new RuntimeException();
		}
		if (this.drawTargetIndex >= 0) {
			this.drawTargetStack[this.drawTargetIndex].unbindDraw();
		}
		this.drawTarget = this.drawTargetStack[++this.drawTargetIndex] = target;
		this.drawTarget.bindDraw();
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IZ)V")
	public void toggleZbuffer(@OriginalArg(1) boolean zbuffer) {
		if (zbuffer == this.depthTestEnabled) {
			return;
		}
		if (zbuffer) {
			OpenGL.glEnable(OpenGLConstants.GL_DEPTH_TEST);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_DEPTH_TEST);
		}
		this.depthTestEnabled = zbuffer;
		this.functionMask &= 0xFFFFFFE0;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	@Override
	public AbstractFontRenderer createFontRenderer(@OriginalArg(0) FontMetrics metrics, @OriginalArg(1) SpriteType[] image) {
		return new GlFont(this, metrics, image, true);
	}

	@OriginalMember(owner = "client!qo", name = "aa", descriptor = "(IIIIII)V")
	@Override
	public void aa(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int blendMode) {
		@Pc(8) float left = (float) x + 0.35F;
		@Pc(13) float top = (float) y + 0.35F;
		@Pc(18) float right = (float) width + left;
		this.enter2dMode();
		@Pc(26) float bottom = top + (float) height;
		this.setBlendMode(blendMode);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		if (this.arbMultisample) {
			OpenGL.glDisable(32925);
		}
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glVertex2f(left, top);
		OpenGL.glVertex2f(left, bottom);
		OpenGL.glVertex2f(right, bottom);
		OpenGL.glVertex2f(right, top);
		OpenGL.glEnd();
		if (this.arbMultisample) {
			OpenGL.glEnable(32925);
		}
	}

	@OriginalMember(owner = "client!qo", name = "ZA", descriptor = "(IFFFFF)V")
	@Override
	public void ZA(@OriginalArg(0) int colour, @OriginalArg(1) float intensity, @OriginalArg(2) float antiIntensity, @OriginalArg(3) float directionX, @OriginalArg(4) float directionY, @OriginalArg(5) float directionZ) {
		@Pc(15) boolean colourChanged = this.sunColorRgb != colour;
		if (colourChanged || this.sunColorIntensity != intensity || antiIntensity != this.antiSunColorIntensity) {
			this.sunColorIntensity = intensity;
			this.sunColorRgb = colour;
			this.antiSunColorIntensity = antiIntensity;
			if (colourChanged) {
				this.sunColorBlue = (float) (this.sunColorRgb & 0xFF) / 255.0F;
				this.sunColorRed = (float) (this.sunColorRgb & 0xFF0000) / 1.671168E7F;
				this.sunColorGreen = (float) (this.sunColorRgb & 0xFF00) / 65280.0F;
				this.updateAmbient();
			}
			this.updateSunDiffuse();
		}
		if (directionX == this.sunPreviousPosition[0] && this.sunPreviousPosition[1] == directionY && this.sunPreviousPosition[2] == directionZ) {
			return;
		}
		this.sunPreviousPosition[2] = directionZ;
		this.sunPreviousPosition[1] = directionY;
		this.sunPreviousPosition[0] = directionX;
		this.antiSunPreviousPosition[0] = -directionX;
		this.antiSunPreviousPosition[2] = -directionZ;
		this.antiSunPreviousPosition[1] = -directionY;
		@Pc(142) float inverseLength = (float) (1.0D / Math.sqrt((double) (directionZ * directionZ + directionY * directionY + directionX * directionX)));
		this.sunDirection[0] = inverseLength * directionX;
		this.sunDirection[2] = directionZ * inverseLength;
		this.sunDirection[1] = inverseLength * directionY;
		this.lightingDirection[2] = -this.sunDirection[2];
		this.lightingDirection[1] = -this.sunDirection[1];
		this.lightingDirection[0] = -this.sunDirection[0];
		this.setLightingSourceParams();
		this.sunSlopeZ = (int) (directionZ * 256.0F / directionY);
		this.sunSlopeX = (int) (directionX * 256.0F / directionY);
	}

	@OriginalMember(owner = "client!qo", name = "g", descriptor = "(II)I")
	@Override
	public int combineFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		return maskA | maskB;
	}

	@OriginalMember(owner = "client!qo", name = "GA", descriptor = "(I)V")
	@Override
	public void GA(@OriginalArg(0) int colour) {
		this.setBlendMode(0);
		OpenGL.glClearColor((float) (colour & 0xFF0000) / 1.671168E7F, (float) (colour & 0xFF00) / 65280.0F, (float) (colour & 0xFF) / 255.0F, (float) (colour >>> 24) / 255.0F);
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT);
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(I)V")
	@Override
	public void initialize() {
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(II)V")
	public void setBlendMode(@OriginalArg(0) int mode) {
		if (mode == this.activeBlendAlpha) {
			return;
		}
		@Pc(22) boolean alphaTest;
		@Pc(20) byte blendType;
		if (mode == 1) {
			blendType = 1;
			alphaTest = true;
		} else if (mode == 2) {
			blendType = 2;
			alphaTest = false;
		} else if (mode == 128) {
			blendType = 3;
			alphaTest = true;
		} else {
			alphaTest = false;
			blendType = 0;
		}
		if (!this.colormaskActive) {
			OpenGL.glColorMask(true, true, true, true);
			this.colormaskActive = true;
		}
		if (alphaTest != this.alphaTestEnabled) {
			if (alphaTest) {
				OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST);
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_ALPHA_TEST);
			}
			this.alphaTestEnabled = alphaTest;
		}
		if (blendType != this.activeBlendType) {
			if (blendType == 1) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND);
				OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA);
			} else if (blendType == 2) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND);
				OpenGL.glBlendFunc(OpenGLConstants.GL_ONE, OpenGLConstants.GL_ONE);
			} else if (blendType == 3) {
				OpenGL.glEnable(OpenGLConstants.GL_BLEND);
				OpenGL.glBlendFunc(OpenGLConstants.GL_DST_COLOR, OpenGLConstants.GL_ONE);
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_BLEND);
			}
			this.activeBlendType = blendType;
		}
		this.activeBlendAlpha = mode;
		this.functionMask &= 0xFFFFFFE3;
	}

	@OriginalMember(owner = "client!qo", name = "g", descriptor = "(B)V")
	private void updateModelViewAndLights() {
		OpenGL.glLoadIdentity();
		OpenGL.glMultMatrixf(this.modelViewMatrix.getColumnMajor(), 0);
		if (this.renderStillWater) {
			this.effectManager.waterEffect.bindProgram();
		}
		this.setLightingSourceParams();
		this.updatePointLights();
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "(I)V")
	public void setOrthoMatrixMode() {
		if (this.depthClippingMode != 0) {
			this.functionMask &= 0xFFFFFFE0;
			this.depthClippingMode = 0;
		}
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIII)V")
	public void drawArrays(@OriginalArg(0) int count) {
		OpenGL.glDrawArrays(OpenGLConstants.GL_QUADS, 0, count);
	}

	@OriginalMember(owner = "client!qo", name = "U", descriptor = "(IIIII)V")
	@Override
	public void U(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int colour, @OriginalArg(4) int blendMode) {
		this.enter2dMode();
		this.setBlendMode(blendMode);
		@Pc(15) float startX = (float) x + 0.35F;
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		@Pc(35) float startY = (float) y + 0.35F;
		OpenGL.glBegin(OpenGLConstants.GL_LINES);
		OpenGL.glVertex2f(startX, startY);
		OpenGL.glVertex2f((float) width + startX, startY);
		OpenGL.glEnd();
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(I)V")
	@Override
	public void setTextureSize(@OriginalArg(0) int size) {
		if (size < 128 || size > 1024) {
			throw new IllegalArgumentException();
		}
		this.size = size;
		this.textureCache.reset();
	}

	@OriginalMember(owner = "client!qo", name = "xa", descriptor = "(F)V")
	@Override
	public void xa(@OriginalArg(0) float brightness) {
		if (this.sunBrightness != brightness) {
			this.sunBrightness = brightness;
			this.updateAmbient();
		}
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(II)V")
	public void renderTexture(@OriginalArg(0) int materialId) {
		this.renderTexture(true, materialId);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZLclient!iaa;)V")
	public void multiMatrix(@OriginalArg(1) Matrix43 matrix44) {
		OpenGL.glPushMatrix();
		OpenGL.glMultMatrixf(matrix44.getColumnMajor(), 0);
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean hasAlpha) {
		return new SpriteGL(this, x, y, width, height);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIILclient!ni;I)V")
	public void drawElements(@OriginalArg(0) int indexCount, @OriginalArg(3) ElementsBufferGL elementsBuffer, @OriginalArg(4) int startIndex) {
		@Pc(7) int indexType = elementsBuffer.getIndexType();
		@Pc(20) int byteOffset = startIndex * this.getDataTypeSize(indexType);
		this.setElementsBuffer(elementsBuffer);
		OpenGL.glDrawElements(OpenGLConstants.GL_TRIANGLES, indexCount, indexType, elementsBuffer.getAddress() + (long) byteOffset);
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(I)V")
	@Override
	public synchronized void tick(@OriginalArg(0) int time) {
		@Pc(7) int count = 0;
		@Pc(11) int maskedTime = time & Integer.MAX_VALUE;
		@Pc(19) IntNode intNode;
		while (!this.vboBuffersAwaitingDeallocation.isEmpty()) {
			intNode = (IntNode) this.vboBuffersAwaitingDeallocation.removeFirst();
			GlToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			this.vboBuffersSize -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteBuffersARB(count, GlToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteBuffersARB(count, GlToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.texturesAwaitingDeallocation.isEmpty()) {
			intNode = (IntNode) this.texturesAwaitingDeallocation.removeFirst();
			GlToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			this.textureBuffersSize -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteTextures(count, GlToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteTextures(count, GlToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.frameBuffersAwaitingDeallocation.isEmpty()) {
			intNode = (IntNode) this.frameBuffersAwaitingDeallocation.removeFirst();
			GlToolkit.deleteIdBuffer[count++] = intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteFramebuffersEXT(count, GlToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteFramebuffersEXT(count, GlToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.renderBuffersAwaitingRemoval.isEmpty()) {
			intNode = (IntNode) this.renderBuffersAwaitingRemoval.removeFirst();
			GlToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			this.renderBuffersSize -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteRenderbuffersEXT(count, GlToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteRenderbuffersEXT(count, GlToolkit.deleteIdBuffer, 0);
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = (IntNode) this.listsAwaitingDeallocation.removeFirst();
			OpenGL.glDeleteLists((int) intNode.key, intNode.value);
		}
		@Pc(209) Node node;
		while (!this.arbProgramsAwaitingDeallocation.isEmpty()) {
			node = this.arbProgramsAwaitingDeallocation.removeFirst();
			OpenGL.glDeleteProgramARB((int) node.key);
		}
		while (!this.shaderProgramsAwaitingDeallocation.isEmpty()) {
			node = this.shaderProgramsAwaitingDeallocation.removeFirst();
			OpenGL.glDeleteObjectARB(node.key);
		}
		while (!this.listsAwaitingDeallocation.isEmpty()) {
			intNode = (IntNode) this.listsAwaitingDeallocation.removeFirst();
			OpenGL.glDeleteLists((int) intNode.key, intNode.value);
		}
		this.textureCache.clean();
		if (this.E() > 100663296 && this.lastGcTime + TimeUtils.MILLISECONDS_PER_MINUTE < Utils.currentTimeMillis()) {
			System.gc();
			this.lastGcTime = Utils.currentTimeMillis();
		}
		this.lastTickMillis = maskedTime;
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(IIIIII)V")
	@Override
	public void line(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		this.enter2dMode();
		this.setBlendMode(mode);
		@Pc(16) float dx = (float) x2 - (float) x1;
		@Pc(23) float dy = (float) -y1 + (float) y2;
		if (dx == 0.0F && dy == 0.0F) {
			dx = 1.0F;
		} else {
			@Pc(48) float inverseLength = (float) (1.0D / Math.sqrt((double) (dx * dx + dy * dy)));
			dx *= inverseLength;
			dy *= inverseLength;
		}
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		OpenGL.glBegin(OpenGLConstants.GL_LINES);
		OpenGL.glVertex2f((float) x1 + 0.35F, (float) y1 + 0.35F);
		OpenGL.glVertex2f(dx + (float) x2 + 0.35F, dy + 0.35F + (float) y2);
		OpenGL.glEnd();
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(IIIID)V")
	@Override
	public void b(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) double depthOffset) {
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "(I)V")
	public void setupOrthoMatrixRendering() {
		if (this.functionMask == 16) {
			return;
		}
		this.setOrthoProjectionMode();
		this.toggleFog(true);
		this.toggleZbuffer(true);
		this.setDepthMask(true);
		this.setBlendMode(1);
		this.functionMask = 16;
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(II)V")
	public void setColourOp(@OriginalArg(1) int op) {
		if (op == 1) {
			this.loadTextureCombineMode(7681, 7681);
		} else if (op == 0) {
			this.loadTextureCombineMode(8448, 8448);
		} else if (op == 2) {
			this.loadTextureCombineMode(34165, 7681);
		} else if (op == 3) {
			this.loadTextureCombineMode(260, 8448);
		} else if (op == 4) {
			this.loadTextureCombineMode(34023, 34023);
			return;
		}
	}

	@OriginalMember(owner = "client!qo", name = "u", descriptor = "()Z")
	@Override
	public boolean forceFixedScreenmode() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIBI)V")
	public void setTexture(@OriginalArg(0) int source, @OriginalArg(1) int operand, @OriginalArg(3) int activeUnit) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_SRC0_RGB, source);
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, activeUnit + OpenGLConstants.GL_OPERAND0_RGB, operand);
	}

	@OriginalMember(owner = "client!qo", name = "p", descriptor = "(I)V")
	private void setScreenProjectionMode() {
		if (this.depthClippingMode == 1) {
			return;
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadIdentity();
		if (this.viewportWidth > 0 && this.viewportHeight > 0) {
			OpenGL.glOrtho(0.0D, (double) this.viewportWidth, (double) this.viewportHeight, 0.0D, -1.0D, 1.0D);
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		this.depthClippingMode = 1;
		this.functionMask &= 0xFFFFFFE7;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!tv;)V")
	public void popDrawTarget(@OriginalArg(1) GlRenderTarget target) {
		if (this.drawTargetIndex < 0 || this.drawTargetStack[this.drawTargetIndex] != target) {
			throw new RuntimeException();
		}
		this.drawTargetStack[this.drawTargetIndex--] = null;
		target.unbindDraw();
		if (this.drawTargetIndex < 0) {
			this.drawTarget = null;
		} else {
			this.drawTarget = this.drawTargetStack[this.drawTargetIndex];
			this.drawTarget.bindDraw();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BI)V")
	public synchronized void requestArbProgramCleanup(@OriginalArg(1) int programHandle) {
		@Pc(7) Node node = new Node();
		node.key = programHandle;
		this.arbProgramsAwaitingDeallocation.addLast(node);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!iaa;)V")
	public void loadMatrix(@OriginalArg(1) Matrix43 matrix4x4) {
		OpenGL.glLoadMatrixf(matrix4x4.getColumnMajor(), 0);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFF)V")
	@Override
	public void setBloomParams(@OriginalArg(0) float whitePoint, @OriginalArg(1) float bloom, @OriginalArg(2) float brightpass) {
		RectangularTextureGL.brightpass = brightpass;
		RectangularTextureGL.whitePoint = whitePoint;
		RectangularTextureGL.bloom = bloom;
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void releaseSurface(@OriginalArg(0) Canvas canvas) {
		if (canvas == this.canvas) {
			throw new RuntimeException();
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(22) Long long2 = (Long) this.canvasSurfaces.get(canvas);
			this.activeOpenglInstance.releaseSurface(canvas, long2);
			this.canvasSurfaces.remove(canvas);
		}
	}

	@OriginalMember(owner = "client!qo", name = "q", descriptor = "(I)V")
	private void renderStationaryTexture() {
		if (this.textureUpdate) {
			OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
			OpenGL.glLoadIdentity();
			OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
			this.textureUpdate = false;
		}
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "()Z")
	@Override
	public boolean extendBuildArea() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "za", descriptor = "(IIIII)V")
	@Override
	public void za(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int radius, @OriginalArg(3) int colour, @OriginalArg(4) int blendMode) {
		if (radius < 0) {
			radius = -radius;
		}
		if (x + radius < this.clipLeft || this.clipRight < x - radius || this.clipTop > y + radius || this.clipBottom < y - radius) {
			return;
		}
		this.enter2dMode();
		this.setBlendMode(blendMode);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		@Pc(72) float centerX = (float) x + 0.35F;
		@Pc(77) float centerY = (float) y + 0.35F;
		@Pc(81) int diameter = radius << 1;
		if (this.fillCirclePointSize > (float) diameter) {
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glVertex2f(centerX + 1.0F, centerY + 1.0F);
			OpenGL.glVertex2f(centerX + 1.0F, -1.0F + centerY);
			OpenGL.glVertex2f(centerX - 1.0F, centerY - 1.0F);
			OpenGL.glVertex2f(centerX - 1.0F, centerY + 1.0F);
			OpenGL.glEnd();
		} else if (this.outlineCirclePointSize >= (float) diameter) {
			OpenGL.glEnable(OpenGLConstants.GL_POINT_SMOOTH);
			OpenGL.glPointSize((float) diameter);
			OpenGL.glBegin(OpenGLConstants.GL_POINTS);
			OpenGL.glVertex2f(centerX, centerY);
			OpenGL.glEnd();
			OpenGL.glDisable(OpenGLConstants.GL_POINT_SMOOTH);
		} else {
			OpenGL.glBegin(OpenGLConstants.GL_TRIANGLE_FAN);
			OpenGL.glVertex2f(centerX, centerY);
			@Pc(150) int angleStep = 262144 / (radius * 6);
			if (angleStep <= 64) {
				angleStep = 64;
			} else if (angleStep > 512) {
				angleStep = 512;
			}
			angleStep = Utils.floorPowerOfTwo(angleStep);
			OpenGL.glVertex2f((float) radius + centerX, centerY);
			for (@Pc(177) int i = 16384 - angleStep; i > 0; i -= angleStep) {
				OpenGL.glVertex2f((float) radius * OggStreamDecoder.COSINE[i] + centerX, (float) radius * OggStreamDecoder.SINE[i] + centerY);
			}
			OpenGL.glVertex2f(centerX + (float) radius, centerY);
			OpenGL.glEnd();
		}
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(I)V")
	@Override
	public void linkThreads(@OriginalArg(0) int threadId) {
		this.attach();
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "(B)V")
	private void toggleLighting() {
		if (this.lightingEnabled && !this.hideLights) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHTING);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHTING);
		}
	}

	@OriginalMember(owner = "client!qo", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		for (@Pc(6) Node node = this.heapManager.first(); node != null; node = this.heapManager.next()) {
			((GlMemoryPool) node).deallocate();
		}
		if (this.framebufferCompositor != null) {
			this.framebufferCompositor.shutdown();
		}
		this.dropSceneTarget(); // Not part of the original client: see beginScene.
		if (this.activeOpenglInstance != null) {
			this.detach();
			@Pc(40) Enumeration enumeration = this.canvasSurfaces.keys();
			while (enumeration.hasMoreElements()) {
				@Pc(46) Canvas canvas = (Canvas) enumeration.nextElement();
				@Pc(52) Long long2 = (Long) this.canvasSurfaces.get(canvas);
				this.activeOpenglInstance.releaseSurface(canvas, long2);
			}
			this.activeOpenglInstance.release();
			this.activeOpenglInstance = null;
		}
		if (this.colortableReset) {
			ColorUtil.releaseColorTables(true, false);
			this.colortableReset = false;
		}
	}

	@OriginalMember(owner = "client!qo", name = "q", descriptor = "()V")
	@Override
	public void renderLastFrame() {
		if (!this.s3Graphics || this.viewportWidth <= 0 || this.viewportHeight <= 0) {
			return;
		}
		if (this.scaledTarget != null) {
			// Not part of the original client: the scaled target keeps the last frame, so there is no
			// front buffer to copy back from, and the window's buffers cannot be chosen while it is bound.
			return;
		}
		@Pc(22) int savedClipLeft = this.clipLeft;
		@Pc(25) int savedClipRight = this.clipRight;
		@Pc(28) int savedClipTop = this.clipTop;
		@Pc(31) int savedClipBottom = this.clipBottom;
		this.la();
		OpenGL.glReadBuffer(OpenGLConstants.GL_FRONT);
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK);
		this.setOrthoMatrixMode();
		this.toggleFog(false);
		this.setLightingEnabled(false);
		this.toggleZbuffer(false);
		this.setDepthMask(false);
		this.setActiveTexture(null);
		this.renderTexture(-2);
		this.setColourOp(1);
		this.setBlendMode(0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadIdentity();
		OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		OpenGL.glRasterPos2i(0, 0);
		OpenGL.glCopyPixels(0, 0, this.viewportWidth, this.viewportHeight, OpenGLConstants.GL_COLOR);
		OpenGL.glFlush();
		OpenGL.glReadBuffer(OpenGLConstants.GL_BACK);
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK);
		this.KA(savedClipLeft, savedClipTop, savedClipRight, savedClipBottom);
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	@Override
	public EnvironmentSampler createCubeTexture(@OriginalArg(0) int materialAtPositiveX, @OriginalArg(1) int materialAtNegativeX, @OriginalArg(2) int materialAtPositiveY, @OriginalArg(3) int materialAtNegativeY, @OriginalArg(4) int materialAtPositiveZ, @OriginalArg(5) int materialAtNegativeZ) {
		return this.arbTextureCubeMap ? new OpenGLArbTextureCubeMapGenerator(this, materialAtPositiveX, materialAtNegativeX, materialAtPositiveY, materialAtNegativeY, materialAtPositiveZ, materialAtNegativeZ) : null;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(JZ)V")
	public synchronized void requestShaderProgramCleanup(@OriginalArg(0) long programHandle) {
		@Pc(13) Node node = new Node();
		node.key = programHandle;
		this.shaderProgramsAwaitingDeallocation.addLast(node);
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(ILclient!tv;)V")
	public void popTarget(@OriginalArg(1) GlRenderTarget target) {
		if (this.framebufferBlit) {
			this.popReadTarget(target);
			this.popDrawTarget(target);
		} else if (this.targetIndex >= 0 && this.targetStack[this.targetIndex] == target) {
			this.targetStack[this.targetIndex--] = null;
			target.unbind();
			if (this.targetIndex < 0) {
				this.readTarget = this.drawTarget = null;
			} else {
				this.readTarget = this.drawTarget = this.targetStack[this.targetIndex];
				this.readTarget.bind();
			}
		} else {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BLclient!ud;)V")
	public void setActiveTexture(@OriginalArg(1) GlTexture replacingCombiner) {
		@Pc(20) GlTexture current = this.openglTextures[this.glTexturesCursorPos];
		if (current != replacingCombiner) {
			if (replacingCombiner == null) {
				OpenGL.glDisable(current.target);
			} else {
				if (current == null) {
					OpenGL.glEnable(replacingCombiner.target);
				} else if (replacingCombiner.target != current.target) {
					OpenGL.glDisable(current.target);
					OpenGL.glEnable(replacingCombiner.target);
				}
				OpenGL.glBindTexture(replacingCombiner.target, replacingCombiner.getId());
			}
			this.openglTextures[this.glTexturesCursorPos] = replacingCombiner;
		}
		this.functionMask &= 0xFFFFFFFE;
	}

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "()V")
	@Override
	public void stopBloom() {
		if (this.rectangularTexture != null && this.rectangularTexture.isActive()) {
			this.framebufferCompositor.removeEffect(this.rectangularTexture);
			this.textureCache.reset();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIIIII)V")
	@Override
	public void drawLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colorArgb, @OriginalArg(6) int dashLength, @OriginalArg(7) int gapLength, @OriginalArg(8) int offset) {
		if (startX == endX && startY == endY) {
			return;
		}
		this.enter2dMode();
		this.setBlendMode(1);
		@Pc(29) float dx = (float) endX - (float) startX;
		@Pc(35) float dy = (float) endY - (float) startY;
		@Pc(48) float inverseLength = (float) (1.0D / Math.sqrt((double) (dy * dy + dx * dx)));
		@Pc(52) float dirX = dx * inverseLength;
		@Pc(56) float dirY = dy * inverseLength;
		OpenGL.glColor4ub((byte) (colorArgb >> 16), (byte) (colorArgb >> 8), (byte) colorArgb, (byte) (colorArgb >> 24));
		@Pc(77) int phase = offset % (dashLength + gapLength);
		@Pc(82) float dashX = (float) dashLength * dirX;
		@Pc(87) float dashY = dirY * (float) dashLength;
		@Pc(89) float startOffsetX = 0.0F;
		@Pc(91) float startOffsetY = 0.0F;
		@Pc(93) float segmentX = dashX;
		@Pc(95) float segmentY = dashY;
		if (phase <= dashLength) {
			segmentY = (float) (dashLength - phase) * dirY;
			segmentX = dirX * (float) (dashLength - phase);
		} else {
			startOffsetY = (float) (gapLength + dashLength - phase) * dirY;
			startOffsetX = (float) (gapLength + dashLength - phase) * dirX;
		}
		@Pc(141) float currentX = startOffsetX + (float) startX + 0.35F;
		@Pc(148) float currentY = (float) startY + startOffsetY + 0.35F;
		@Pc(153) float gapX = dirX * (float) gapLength;
		@Pc(158) float gapY = dirY * (float) gapLength;
		while (true) {
			if (endX <= startX) {
				if ((float) endX + 0.35F > currentX) {
					break;
				}
				if (currentX + segmentX < (float) endX) {
					segmentX = (float) endX - currentX;
				}
			} else {
				if (currentX > (float) endX + 0.35F) {
					break;
				}
				if (currentX + segmentX > (float) endX) {
					segmentX = (float) endX - currentX;
				}
			}
			if (startY >= endY) {
				if ((float) endY + 0.35F > currentY) {
					break;
				}
				if (segmentY + currentY < (float) endY) {
					segmentY = (float) endY - currentY;
				}
			} else {
				if ((float) endY + 0.35F < currentY) {
					break;
				}
				if (segmentY + currentY > (float) endY) {
					segmentY = (float) endY - currentY;
				}
			}
			OpenGL.glBegin(OpenGLConstants.GL_LINES);
			OpenGL.glVertex2f(currentX, currentY);
			OpenGL.glVertex2f(currentX + segmentX, segmentY + currentY);
			OpenGL.glEnd();
			currentY += segmentY + gapY;
			currentX += gapX + segmentX;
			segmentX = dashX;
			segmentY = dashY;
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void addCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		if (this.canvas == canvas) {
			throw new RuntimeException();
		} else if (!this.canvasSurfaces.containsKey(canvas)) {
			if (!canvas.isShowing()) {
				throw new RuntimeException();
			}
			try {
				@Pc(26) Class class2 = Class.forName("java.awt.Canvas");
				@Pc(36) Method method = class2.getMethod("setIgnoreRepaint", Boolean.TYPE);
				method.invoke(canvas, Boolean.TRUE);
			} catch (@Pc(48) Exception ex) {
			}
			@Pc(53) long surface = this.activeOpenglInstance.prepareSurface(canvas);
			if (surface == -1L) {
				throw new RuntimeException();
			}
			this.canvasSurfaces.put(canvas, Long.valueOf(surface));
		}
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(B)V")
	private void updateViewBounds() {
		this.viewX2 = this.clipRight - this.projectionCenterX;
		this.viewX1 = this.clipLeft - this.projectionCenterX;
		this.viewY1 = this.clipTop - this.projectionCenterY;
		this.viewY2 = this.clipBottom - this.projectionCenterY;
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "(I)Lclient!za;")
	@Override
	public MemoryPool createHeap(@OriginalArg(0) int size) {
		@Pc(8) GlMemoryPool glMemoryPool = new GlMemoryPool(size);
		this.heapManager.addLast(glMemoryPool);
		return glMemoryPool;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	@Override
	public Ground createGround(@OriginalArg(0) int width, @OriginalArg(1) int length, @OriginalArg(2) int[][] tileHeights, @OriginalArg(3) int[][] nextLevelHeights, @OriginalArg(5) int groundFlags, @OriginalArg(6) int featureFlags) {
		return new GlGround(this, groundFlags, featureFlags, width, length, tileHeights, nextLevelHeights, 512);
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "()Z")
	@Override
	public boolean hardShadow() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(ILclient!tv;)V")
	public void pushTarget(@OriginalArg(1) GlRenderTarget target) {
		if (this.framebufferBlit) {
			this.pushReadTarget(target);
			this.pushDrawTarget(target);
		} else if (this.targetIndex >= 3) {
			throw new RuntimeException();
		} else {
			if (this.targetIndex >= 0) {
				this.targetStack[this.targetIndex].unbind();
			}
			this.readTarget = this.drawTarget = this.targetStack[++this.targetIndex] = target;
			this.readTarget.bind();
		}
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "(B)I")
	private int accumulateCapabilities() {
		@Pc(5) int errors = 0;
		this.gpuCompanyString = OpenGL.glGetString(OpenGLConstants.GL_VENDOR).toLowerCase();
		this.gpuNameString = OpenGL.glGetString(OpenGLConstants.GL_RENDERER).toLowerCase();
		if (this.gpuCompanyString.indexOf("microsoft") != -1) {
			errors = 1;
		}
		if (this.gpuCompanyString.indexOf("brian paul") != -1 || this.gpuCompanyString.indexOf("mesa") != -1) {
			errors |= 0x1;
		}
		@Pc(44) String string = OpenGL.glGetString(OpenGLConstants.GL_VERSION);
		@Pc(52) String[] strings = Utils.split(' ', string.replace('.', ' '));
		if (strings.length >= 2) {
			try {
				@Pc(62) int major = Utils.parseInt(strings[0]);
				@Pc(68) int minor = Utils.parseInt(strings[1]);
				this.openglVersion = major * 10 + minor;
			} catch (@Pc(78) NumberFormatException ex) {
				errors |= 0x4;
			}
		} else {
			errors |= 0x4;
		}
		if (this.openglVersion < 12) {
			errors |= 0x2;
		}
		if (!this.activeOpenglInstance.a("GL_ARB_multitexture")) {
			errors |= 0x8;
		}
		if (!this.activeOpenglInstance.a("GL_ARB_texture_env_combine")) {
			errors |= 0x20;
		}
		@Pc(124) int[] values = new int[1];
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_UNITS, values, 0);
		this.maxTextureUnits = values[0];
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_COORDS, values, 0);
		this.maxTextureCoordinates = values[0];
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_IMAGE_UNITS, values, 0);
		this.maxTextureImageUnits = values[0];
		if (this.maxTextureUnits < 2 || this.maxTextureCoordinates < 2 || this.maxTextureImageUnits < 2) {
			errors |= 0x10;
		}
		this.bigEndianess = Stream.c();
		this.pbuffersAvailable = this.activeOpenglInstance.arePbuffersAvailable();
		this.vertexbufferSupported = this.activeOpenglInstance.a("GL_ARB_vertex_buffer_object");
		this.arbMultisample = this.activeOpenglInstance.a("GL_ARB_multisample");
		this.arbVertexProgram = this.activeOpenglInstance.a("GL_ARB_vertex_program");
		this.activeOpenglInstance.a("GL_ARB_fragment_program");
		this.arbVertexShader = this.activeOpenglInstance.a("GL_ARB_vertex_shader");
		this.arbFragmentShader = this.activeOpenglInstance.a("GL_ARB_fragment_shader");
		this.texture3dSupported = this.activeOpenglInstance.a("GL_EXT_texture3D");
		this.arbRectangleTexturesSupported = this.activeOpenglInstance.a("GL_ARB_texture_rectangle");
		this.arbTextureCubeMap = this.activeOpenglInstance.a("GL_ARB_texture_cube_map");
		this.arbTextureFloat = this.activeOpenglInstance.a("GL_ARB_texture_float");
		this.framebufferObjectSupported = false;
		this.extFramebufferObject = this.activeOpenglInstance.a("GL_EXT_framebuffer_object");
		this.framebufferBlit = this.activeOpenglInstance.a("GL_EXT_framebuffer_blit");
		this.framebufferMultisample = this.activeOpenglInstance.a("GL_EXT_framebuffer_multisample");
		this.advancedFramebuffers = this.framebufferMultisample & this.framebufferBlit;
		OpenGL.glGetFloatv(OpenGLConstants.GL_POINT_SIZE_RANGE, OpenGlToolkit.BUFFER_4F, 0);
		this.fillCirclePointSize = OpenGlToolkit.BUFFER_4F[0];
		this.outlineCirclePointSize = OpenGlToolkit.BUFFER_4F[1];
		return errors == 0 ? 0 : errors;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	@Override
	public void drawTexturedLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY, @OriginalArg(9) int dashLength, @OriginalArg(10) int gapLength, @OriginalArg(11) int offset) {
		if (endX == startX && startY == endY) {
			return;
		}
		@Pc(19) GlClippingMask mask = (GlClippingMask) clippingMask;
		@Pc(22) OpenGl2DImageTexture surface = mask.surface;
		this.setupSpriteRendering();
		this.setActiveTexture(mask.surface);
		this.setBlendMode(1);
		this.loadTextureCombineMode(7681, 8448);
		this.setTexture(34167, 768, 0);
		@Pc(52) float texScaleS = surface.s / (float) surface.singleWidth;
		@Pc(59) float texScaleT = surface.t / (float) surface.singleLength;
		@Pc(66) float dx = (float) -startX + (float) endX;
		@Pc(72) float dy = (float) endY - (float) startY;
		@Pc(85) float inverseLength = (float) (1.0D / Math.sqrt((double) (dx * dx + dy * dy)));
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		@Pc(104) float dirX = dx * inverseLength;
		@Pc(108) float dirY = dy * inverseLength;
		@Pc(114) int phase = offset % (gapLength + dashLength);
		@Pc(119) float dashX = (float) dashLength * dirX;
		@Pc(124) float dashY = dirY * (float) dashLength;
		@Pc(126) float startOffsetX = 0.0F;
		@Pc(128) float startOffsetY = 0.0F;
		@Pc(130) float segmentX = dashX;
		@Pc(132) float segmentY = dashY;
		if (phase <= dashLength) {
			segmentX = dirX * (float) (dashLength - phase);
			segmentY = dirY * (float) (dashLength - phase);
		} else {
			startOffsetY = (float) (dashLength + gapLength - phase) * dirY;
			startOffsetX = (float) (dashLength + gapLength - phase) * dirX;
		}
		@Pc(181) float currentX = startOffsetX + (float) startX + 0.35F;
		@Pc(188) float currentY = (float) startY + startOffsetY + 0.35F;
		@Pc(193) float gapX = (float) gapLength * dirX;
		@Pc(198) float gapY = (float) gapLength * dirY;
		while (true) {
			if (endX > startX) {
				if (currentX > (float) endX + 0.35F) {
					break;
				}
				if ((float) endX < currentX + segmentX) {
					segmentX = (float) endX - currentX;
				}
			} else {
				if ((float) endX + 0.35F > currentX) {
					break;
				}
				if ((float) endX > currentX + segmentX) {
					segmentX = (float) endX - currentX;
				}
			}
			if (endY <= startY) {
				if ((float) endY + 0.35F > currentY) {
					break;
				}
				if ((float) endY > currentY + segmentY) {
					segmentY = (float) endY - currentY;
				}
			} else {
				if (currentY > (float) endY + 0.35F) {
					break;
				}
				if ((float) endY < segmentY + currentY) {
					segmentY = (float) endY - currentY;
				}
			}
			OpenGL.glBegin(OpenGLConstants.GL_LINES);
			OpenGL.glTexCoord2f(texScaleS * ((float) -maskX + currentX), texScaleT * ((float) -maskY + currentY));
			OpenGL.glVertex2f(currentX, currentY);
			OpenGL.glTexCoord2f((currentX + segmentX - (float) maskX) * texScaleS, ((float) -maskY + currentY + segmentY) * texScaleT);
			OpenGL.glVertex2f(segmentX + currentX, currentY + segmentY);
			OpenGL.glEnd();
			currentX += segmentX + gapX;
			currentY += segmentY + gapY;
			segmentY = dashY;
			segmentX = dashX;
		}
		this.setTexture(5890, 768, 0);
	}

	@OriginalMember(owner = "client!qo", name = "X", descriptor = "(I)V")
	@Override
	public void X(@OriginalArg(0) int scale) {
		this.shadowScaleShift = 0;
		while (scale > 1) {
			this.shadowScaleShift++;
			scale >>= 0x1;
		}
		this.shadowScale = 0x1 << this.shadowScaleShift;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	@Override
	public ComponentSurface createMask(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] rowOffsets, @OriginalArg(3) int[] rowWidths) {
		return GlClippingMask.create(width, rowOffsets, rowWidths, this, height);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([I)V")
	@Override
	public void accumulateViewportDimensions(@OriginalArg(0) int[] dimensions) {
		dimensions[0] = this.viewportWidth;
		dimensions[1] = this.viewportHeight;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!za;)V")
	@Override
	public void setHeap(@OriginalArg(0) MemoryPool memoryPool) {
		this.nativeHeap = ((GlMemoryPool) memoryPool).heap;
		if (this.screenQuadArrayBuffer != null) {
			return;
		}
		@Pc(16) GlBitBuffer gpuPacket = new GlBitBuffer(80);
		if (this.bigEndianess) {
			gpuPacket.writeFloatBigEndian(-1.0F);
			gpuPacket.writeFloatBigEndian(-1.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(-1.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(-1.0F);
			gpuPacket.writeFloatBigEndian(1.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
			gpuPacket.writeFloatBigEndian(0.0F);
		} else {
			gpuPacket.writeFloatLittleEndian(-1.0F);
			gpuPacket.writeFloatLittleEndian(-1.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(-1.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(-1.0F);
			gpuPacket.writeFloatLittleEndian(1.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
			gpuPacket.writeFloatLittleEndian(0.0F);
		}
		this.screenQuadArrayBuffer = this.createArrayBuffer(false, 20, gpuPacket.buffer, gpuPacket.currentPosition);
		this.quadVertexBuffer = new OpenGlArrayDrawBuffer(this.screenQuadArrayBuffer, 5126, 3, 0);
		this.quadTexCoordBuffer = new OpenGlArrayDrawBuffer(this.screenQuadArrayBuffer, 5126, 2, 12);
		this.particleRenderer.initialize(this);
	}

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "(ILclient!aa;II)V")
	@Override
	public void A(@OriginalArg(0) int colour, @OriginalArg(1) ComponentSurface clippingMask, @OriginalArg(2) int maskX, @OriginalArg(3) int maskY) {
		@Pc(6) GlClippingMask mask = (GlClippingMask) clippingMask;
		@Pc(9) OpenGl2DImageTexture surface = mask.surface;
		this.setupSpriteRendering();
		this.setActiveTexture(mask.surface);
		this.setBlendMode(1);
		this.loadTextureCombineMode(7681, 8448);
		this.setTexture(34167, 768, 0);
		@Pc(39) float texScaleS = surface.s / (float) surface.singleWidth;
		@Pc(46) float texScaleT = surface.t / (float) surface.singleLength;
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glTexCoord2f(texScaleS * (float) (this.clipLeft - maskX), (float) (this.clipTop - maskY) * texScaleT);
		OpenGL.glVertex2i(this.clipLeft, this.clipTop);
		OpenGL.glTexCoord2f(texScaleS * (float) (this.clipLeft - maskX), texScaleT * (float) (this.clipBottom - maskY));
		OpenGL.glVertex2i(this.clipLeft, this.clipBottom);
		OpenGL.glTexCoord2f((float) (this.clipRight - maskX) * texScaleS, (float) (this.clipBottom - maskY) * texScaleT);
		OpenGL.glVertex2i(this.clipRight, this.clipBottom);
		OpenGL.glTexCoord2f(texScaleS * (float) (this.clipRight - maskX), texScaleT * (float) (this.clipTop - maskY));
		OpenGL.glVertex2i(this.clipRight, this.clipTop);
		OpenGL.glEnd();
		this.setTexture(5890, 768, 0);
	}

	@OriginalMember(owner = "client!qo", name = "t", descriptor = "()Z")
	@Override
	public boolean replaceCanvasOnDisplayUpdate() {
		return false;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(II)Lclient!vm;")
	@Override
	public DepthBuffer createDepthBuffer(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZIII)V")
	public void setTextureTransparency(@OriginalArg(1) int textureUnit, @OriginalArg(3) int alpha) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_SRC0_ALPHA, alpha);
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, textureUnit + OpenGLConstants.GL_OPERAND0_ALPHA, OpenGLConstants.GL_SRC_ALPHA);
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "(I)V")
	private void attach() {
		@Pc(13) int attempts = 0;
		while (!this.activeOpenglInstance.b()) {
			if (attempts++ > 5) {
				throw new RuntimeException("");
			}
			TimeUtils.sleep(1000L);
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public void strongLine(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int width) {
		OpenGL.glLineWidth((float) width);
		this.line(x1, y1, x2, y2, colour, 0);
		OpenGL.glLineWidth(1.0F);
	}

	@OriginalMember(owner = "client!qo", name = "ra", descriptor = "(IIII)V")
	@Override
	public void ra(@OriginalArg(0) int waterHeight, @OriginalArg(1) int fogColour, @OriginalArg(2) int depth, @OriginalArg(3) int intensity) {
		this.waterHeight = waterHeight;
		this.waterDepth = depth;
		this.waterFogColour = fogColour;
		this.stillWaterToggled = true;
		this.waterIntensity = intensity;
	}

	@OriginalMember(owner = "client!qo", name = "s", descriptor = "(I)V")
	private void updateDepthRange() {
		this.projectionMatrix[10] = this.projectionDepthScale;
		this.projectionMatrix[14] = this.projectionDepthOffset;
		this.farClipDistance = this.zFar;
		this.projectedFarClipDistance = (this.projectionMatrix[14] - (float) this.zFar) / this.projectionMatrix[10];
	}

	@OriginalMember(owner = "client!qo", name = "T", descriptor = "(IIII)V")
	@Override
	public void T(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom) {
		if (this.clipTop < top) {
			this.clipTop = top;
		}
		if (this.clipBottom > bottom) {
			this.clipBottom = bottom;
		}
		if (this.clipLeft < left) {
			this.clipLeft = left;
		}
		if (right < this.clipRight) {
			this.clipRight = right;
		}
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
		this.updateViewBounds();
		this.defineScissorBox();
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void resizeCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(5) long surface = 0L;
		if (canvas == null || this.canvas == canvas) {
			surface = this.canvasSurface;
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(22) Long long2 = (Long) this.canvasSurfaces.get(canvas);
			surface = long2;
		}
		if (surface == 0L) {
			throw new RuntimeException();
		}
		this.activeOpenglInstance.surfaceResized(surface);
		if (this.currentCanvas == canvas) {
			this.updateCanvasSize();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int[] pixels, @OriginalArg(2) int rowLength, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) boolean copyPixels) {
		return new SpriteGL(this, width, height, pixels, 0, rowLength);
	}

	@OriginalMember(owner = "client!qo", name = "k", descriptor = "(B)V")
	private void enter2dMode() {
		if (this.functionMask == 1) {
			return;
		}
		this.setScreenProjectionMode();
		this.toggleFog(false);
		this.setLightingEnabled(false);
		this.toggleZbuffer(false);
		this.setDepthMask(false);
		this.setActiveTexture(null);
		this.renderTexture(-2);
		this.setColourOp(1);
		this.functionMask = 1;
	}

	@OriginalMember(owner = "client!qo", name = "t", descriptor = "(I)V")
	private void updateSunDiffuse() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0F;
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorIntensity * this.sunColorRed;
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunColorIntensity;
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunColorIntensity;
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0);
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * -this.antiSunColorIntensity;
		OpenGlToolkit.BUFFER_4F[3] = 1.0F;
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * -this.antiSunColorIntensity;
		OpenGlToolkit.BUFFER_4F[0] = this.sunColorRed * -this.antiSunColorIntensity;
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.BUFFER_4F, 0);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(III)V")
	public synchronized void requestArbBufferCleanup(@OriginalArg(0) int bufferSize, @OriginalArg(2) int bufferHandle) {
		@Pc(8) IntNode intNode = new IntNode(bufferSize);
		intNode.key = bufferHandle;
		this.vboBuffersAwaitingDeallocation.addLast(intNode);
	}

	@OriginalMember(owner = "client!qo", name = "M", descriptor = "()I")
	@Override
	public int M() {
		@Pc(6) int count = this.modelCount;
		this.modelCount = 0;
		return count;
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(BI)I")
	public int getCombineModeEffectType(@OriginalArg(1) int mode) {
		if (mode == 1) {
			return 7681;
		} else if (mode == 0) {
			return 8448;
		} else if (mode == 2) {
			return 34165;
		} else if (mode == 3) {
			return 260;
		} else if (mode == 4) {
			return 34023;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Lclient!tv;I)V")
	public void pushReadTarget(@OriginalArg(0) GlRenderTarget target) {
		if (this.readTargetIndex >= 3) {
			throw new RuntimeException();
		}
		if (this.readTargetIndex >= 0) {
			this.readTargetStack[this.readTargetIndex].unbindRead();
		}
		this.readTarget = this.readTargetStack[++this.readTargetIndex] = target;
		this.readTarget.bindRead();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void outlineRect(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		@Pc(8) float left = (float) x + 0.35F;
		@Pc(13) float top = (float) y + 0.35F;
		@Pc(20) float right = left + (float) width - 1.0F;
		this.enter2dMode();
		@Pc(30) float bottom = top + (float) height - 1.0F;
		this.setBlendMode(mode);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		if (this.arbMultisample) {
			OpenGL.glDisable(32925);
		}
		OpenGL.glBegin(OpenGLConstants.GL_LINE_LOOP);
		OpenGL.glVertex2f(left, top);
		OpenGL.glVertex2f(left, bottom);
		OpenGL.glVertex2f(right, bottom);
		OpenGL.glVertex2f(right, top);
		OpenGL.glEnd();
		if (this.arbMultisample) {
			OpenGL.glEnable(32925);
		}
	}

	@OriginalMember(owner = "client!qo", name = "H", descriptor = "(III[I)V")
	@Override
	public void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] output) {
		@Pc(28) float depth = this.viewMatrix43.zD + (float) x * this.viewMatrix43.zA + this.viewMatrix43.zB * (float) y + this.viewMatrix43.zC * (float) z;
		if (depth == 0.0F) {
			output[0] = output[1] = output[2] = -1;
			return;
		}
		@Pc(77) int screenX = (int) ((this.viewMatrix43.xD + this.viewMatrix43.xC * (float) z + this.viewMatrix43.xB * (float) y + this.viewMatrix43.xA * (float) x) * (float) this.projectionScaleX / depth);
		@Pc(109) int screenY = (int) (((float) x * this.viewMatrix43.yA + this.viewMatrix43.yB * (float) y + this.viewMatrix43.yC * (float) z + this.viewMatrix43.yD) * (float) this.projectionScaleY / depth);
		output[0] = (int) ((float) screenX - this.viewX1);
		output[2] = (int) depth;
		output[1] = (int) ((float) screenY - this.viewY1);
	}

	@OriginalMember(owner = "client!qo", name = "j", descriptor = "()Lclient!bca;")
	@Override
	public Matrix createMatrix() {
		return new Matrix43();
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "()Z")
	@Override
	public boolean hasBloom() {
		if (this.rectangularTexture == null) {
			return false;
		}
		if (!this.rectangularTexture.isActive()) {
			if (!this.framebufferCompositor.addEffect(this.rectangularTexture)) {
				return false;
			}
			this.textureCache.reset();
		}
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "u", descriptor = "(I)V")
	private void updatePointLights() {
		@Pc(7) int lightIndex;
		for (lightIndex = 0; lightIndex < this.lastSceneLightCount; lightIndex++) {
			@Pc(14) PointLight pointLight = this.lightNodes[lightIndex];
			OpenGlToolkit.LIGHT_MATRIX[0] = pointLight.getX();
			@Pc(25) int glLight = lightIndex + 16386;
			OpenGlToolkit.LIGHT_MATRIX[1] = pointLight.getY();
			OpenGlToolkit.LIGHT_MATRIX[2] = pointLight.getZ();
			OpenGlToolkit.LIGHT_MATRIX[3] = 1.0F;
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_POSITION, OpenGlToolkit.LIGHT_MATRIX, 0);
			@Pc(52) int range = pointLight.getColour();
			@Pc(58) float intensityScale = pointLight.getIntensity() / 255.0F;
			OpenGlToolkit.LIGHT_MATRIX[2] = (float) (range & 0xFF) * intensityScale;
			OpenGlToolkit.LIGHT_MATRIX[0] = (float) (range >> 16 & 0xFF) * intensityScale;
			OpenGlToolkit.LIGHT_MATRIX[1] = (float) (range >> 8 & 0xFF) * intensityScale;
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_DIFFUSE, OpenGlToolkit.LIGHT_MATRIX, 0);
			OpenGL.glLightf(glLight, OpenGLConstants.GL_QUADRATIC_ATTENUATION, 1.0F / (float) (pointLight.getRadius() * pointLight.getRadius()));
			OpenGL.glEnable(glLight);
		}
		while (lightIndex < this.activeLightCount) {
			OpenGL.glDisable(lightIndex + OpenGLConstants.GL_LIGHT2);
			lightIndex++;
		}
		this.activeLightCount = this.lastSceneLightCount;
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(B)V")
	public void setupMatrixRendering() {
		if (this.functionMask == 8) {
			return;
		}
		this.setModelViewMatrixMode();
		this.toggleFog(true);
		this.toggleZbuffer(true);
		this.setDepthMask(true);
		this.setBlendMode(1);
		this.functionMask = 8;
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "(II)I")
	public int getDataTypeSize(@OriginalArg(0) int dataType) {
		if (dataType == 5121 || dataType == 5120) {
			return 1;
		} else if (dataType == 5123 || dataType == 5122) {
			return 2;
		} else if (dataType == 5125 || dataType == 5124 || dataType == 5126) {
			return 4;
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!mda;I)V")
	@Override
	public void renderOrtho(@OriginalArg(0) ParticleList particleList, @OriginalArg(1) int zoom) {
		this.particleRenderer.drawParticles(this, zoom, particleList);
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(Z)V")
	private void buildTables() {
		this.openglTextures = new GlTexture[this.maxTextureUnits];
		this.texture2d = new OpenGl2DTexture(this, 3553, 6408, 1, 1);
		new OpenGl2DTexture(this, 3553, 6408, 1, 1);
		new OpenGl2DTexture(this, 3553, 6408, 1, 1);
		this.copyTarget1 = new GlModel(this);
		this.copyTarget2 = new GlModel(this);
		this.copyTarget3 = new GlModel(this);
		this.copyTarget4 = new GlModel(this);
		this.copyTarget5 = new GlModel(this);
		this.copyBuffer1 = new GlModel(this);
		this.copyBuffer2 = new GlModel(this);
		this.copyBuffer3 = new GlModel(this);
		this.copyBuffer4 = new GlModel(this);
		this.copyBuffer5 = new GlModel(this);
		if (this.extFramebufferObject) {
			this.integratedGraphicsImageProducer = new GlFramebuffer(this);
			new GlFramebuffer(this);
		}
	}

	@OriginalMember(owner = "client!qo", name = "v", descriptor = "(I)V")
	private void updateAmbient() {
		OpenGlToolkit.BUFFER_4F[3] = 1.0F;
		OpenGlToolkit.BUFFER_4F[0] = this.sunBrightness * this.sunColorRed;
		OpenGlToolkit.BUFFER_4F[2] = this.sunColorBlue * this.sunBrightness;
		OpenGlToolkit.BUFFER_4F[1] = this.sunColorGreen * this.sunBrightness;
		OpenGL.glLightModelfv(OpenGLConstants.GL_LIGHT_MODEL_AMBIENT, OpenGlToolkit.BUFFER_4F, 0);
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "()Lclient!fia;")
	@Override
	public Renderer renderer() {
		@Pc(7) int vendor = -1;
		if (this.gpuCompanyString.indexOf("nvidia") != -1) {
			vendor = PciVendorId.NVIDIA;
		} else if (this.gpuCompanyString.indexOf("intel") != -1) {
			vendor = PciVendorId.INTEL;
		} else if (this.gpuCompanyString.indexOf("ati") != -1) {
			vendor = PciVendorId.AMD;
		}
		return new Renderer(vendor, "OpenGL", this.openglVersion, this.gpuNameString, 0L);
	}

	@OriginalMember(owner = "client!qo", name = "da", descriptor = "(III[I)V")
	@Override
	public void da(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] output) {
		@Pc(28) float depth = (float) y * this.viewMatrix43.zB + this.viewMatrix43.zA * (float) x + this.viewMatrix43.zC * (float) z + this.viewMatrix43.zD;
		if (depth < (float) this.zNear || depth > (float) this.zFar) {
			output[0] = output[1] = output[2] = -1;
			return;
		}
		@Pc(85) int screenX = (int) ((float) this.projectionScaleX * ((float) y * this.viewMatrix43.xB + this.viewMatrix43.xA * (float) x + this.viewMatrix43.xC * (float) z + this.viewMatrix43.xD) / depth);
		@Pc(117) int screenY = (int) ((this.viewMatrix43.yD + (float) x * this.viewMatrix43.yA + this.viewMatrix43.yB * (float) y + this.viewMatrix43.yC * (float) z) * (float) this.projectionScaleY / depth);
		if (this.viewX1 <= (float) screenX && (float) screenX <= this.viewX2 && this.viewY1 <= (float) screenY && this.viewY2 >= (float) screenY) {
			output[2] = (int) depth;
			output[0] = (int) ((float) screenX - this.viewX1);
			output[1] = (int) ((float) screenY - this.viewY1);
		} else {
			output[0] = output[1] = output[2] = -1;
		}
	}

	@OriginalMember(owner = "client!qo", name = "w", descriptor = "(I)V")
	public void pushMatrix() {
		OpenGL.glPushMatrix();
	}

	@OriginalMember(owner = "client!qo", name = "F", descriptor = "(II)V")
	@Override
	public void F(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY) {
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!is;)V")
	@Override
	public void swapSurface(@OriginalArg(0) OffscreenSurface surface) {
	}

	@OriginalMember(owner = "client!qo", name = "KA", descriptor = "(IIII)V")
	@Override
	public void KA(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom) {
		if (top < 0) {
			top = 0;
		}
		if (this.viewportWidth < right) {
			right = this.viewportWidth;
		}
		if (left < 0) {
			left = 0;
		}
		if (this.viewportHeight < bottom) {
			bottom = this.viewportHeight;
		}
		this.clipRight = right;
		this.clipBottom = bottom;
		this.clipTop = top;
		this.clipLeft = left;
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
		this.updateViewBounds();
		this.defineScissorBox();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(BF)V")
	public void setOrthoZoom(@OriginalArg(1) float zoom) {
		if (this.orthoZoom != zoom) {
			this.orthoZoom = zoom;
			if (this.depthClippingMode == 3) {
				this.loadOrthoProjection();
			}
		}
	}

	@OriginalMember(owner = "client!qo", name = "la", descriptor = "()V")
	@Override
	public void la() {
		this.clipTop = 0;
		this.clipBottom = this.viewportHeight;
		this.clipRight = this.viewportWidth;
		this.clipLeft = 0;
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST);
		this.updateViewBounds();
	}

	@OriginalMember(owner = "client!qo", name = "C", descriptor = "(Z)V")
	@Override
	public void C(@OriginalArg(0) boolean enabled) {
		this.depthBufferingEnabled = enabled;
		this.setDepthMask();
	}

	@OriginalMember(owner = "client!qo", name = "l", descriptor = "()V")
	@Override
	public void cacheReset() {
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(III)V")
	public void setViewportOffset(@OriginalArg(1) int offsetY, @OriginalArg(2) int offsetX) {
		this.viewportOffsetY = offsetY;
		this.viewportOffsetX = offsetX;
		this.updateViewport();
		this.defineScissorBox();
	}

	@OriginalMember(owner = "client!qo", name = "JA", descriptor = "(IIIIII)I")
	@Override
	public int JA(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2) {
		@Pc(7) int outcode = 0;
		@Pc(32) float depth1 = (float) z1 * this.viewMatrix43.zC + (float) x1 * this.viewMatrix43.zA + (float) y1 * this.viewMatrix43.zB + this.viewMatrix43.zD;
		if (depth1 < 1.0F) {
			depth1 = 1.0F;
		}
		@Pc(63) float depth2 = (float) z2 * this.viewMatrix43.zC + (float) y2 * this.viewMatrix43.zB + this.viewMatrix43.zA * (float) x2 + this.viewMatrix43.zD;
		if (depth2 < 1.0F) {
			depth2 = 1.0F;
		}
		if ((float) this.zNear > depth1 && (float) this.zNear > depth2) {
			outcode |= 0x10;
		} else if (depth1 > (float) this.zFar && depth2 > (float) this.zFar) {
			outcode |= 0x20;
		}
		@Pc(135) int screenX1 = (int) ((this.viewMatrix43.xC * (float) z1 + this.viewMatrix43.xB * (float) y1 + this.viewMatrix43.xA * (float) x1 + this.viewMatrix43.xD) * (float) this.projectionScaleX / depth1);
		@Pc(167) int screenX2 = (int) ((float) this.projectionScaleX * ((float) z2 * this.viewMatrix43.xC + this.viewMatrix43.xA * (float) x2 + this.viewMatrix43.xB * (float) y2 + this.viewMatrix43.xD) / depth2);
		if ((float) screenX1 < this.viewX1 && this.viewX1 > (float) screenX2) {
			outcode |= 0x1;
		} else if ((float) screenX1 > this.viewX2 && (float) screenX2 > this.viewX2) {
			outcode |= 0x2;
		}
		@Pc(233) int screenY1 = (int) ((float) this.projectionScaleY * (this.viewMatrix43.yC * (float) z1 + (float) y1 * this.viewMatrix43.yB + (float) x1 * this.viewMatrix43.yA + this.viewMatrix43.yD) / depth1);
		@Pc(265) int screenY2 = (int) ((this.viewMatrix43.yD + (float) x2 * this.viewMatrix43.yA + (float) y2 * this.viewMatrix43.yB + this.viewMatrix43.yC * (float) z2) * (float) this.projectionScaleY / depth2);
		if ((float) screenY1 < this.viewY1 && this.viewY1 > (float) screenY2) {
			outcode |= 0x4;
		} else if ((float) screenY1 > this.viewY2 && (float) screenY2 > this.viewY2) {
			outcode |= 0x8;
		}
		return outcode;
	}

	@OriginalMember(owner = "client!qo", name = "s", descriptor = "()Z")
	@Override
	public boolean utilisesNativeHeap() {
		return true;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIZ)Lclient!mi;")
	@Override
	public NativeSprite generateEmptySprite(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) boolean hasAlpha) {
		return new SpriteGL(this, width, height, hasAlpha);
	}

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(I)V")
	@Override
	public void method7281(@OriginalArg(0) int threadId) {
		this.detach();
	}

	@OriginalMember(owner = "client!qo", name = "x", descriptor = "(I)V")
	private void prepareForRendering() {
		this.renderTexture(-2);
		for (@Pc(14) int i = this.maxTextureUnits - 1; i >= 0; i--) {
			this.activateTexture(i);
			this.setActiveTexture(null);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_MODE, OpenGLConstants.GL_COMBINE);
		}
		this.loadTextureCombineMode(8448, 8448);
		this.setTexture(34168, 770, 2);
		this.renderStationaryTexture();
		this.activeBlendAlpha = 1;
		OpenGL.glEnable(OpenGLConstants.GL_BLEND);
		OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA);
		this.activeBlendType = 1;
		OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST);
		OpenGL.glAlphaFunc(OpenGLConstants.GL_GREATER, 0.0F);
		this.alphaTestEnabled = true;
		OpenGL.glColorMask(true, true, true, true);
		this.colormaskActive = true;
		this.toggleFog(true);
		this.setLightingEnabled(true);
		this.toggleZbuffer(true);
		this.setDepthMask(true);
		this.setOrthoMatrixMode();
		this.activeOpenglInstance.setSwapInterval(0);
		OpenGL.glShadeModel(OpenGLConstants.GL_SMOOTH);
		OpenGL.glClearDepth(1.0F);
		OpenGL.glDepthFunc(OpenGLConstants.GL_LEQUAL);
		OpenGL.glPolygonMode(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_FILL);
		OpenGL.glEnable(OpenGLConstants.GL_CULL_FACE);
		OpenGL.glCullFace(OpenGLConstants.GL_BACK);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		OpenGL.glColorMaterial(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_AMBIENT_AND_DIFFUSE);
		OpenGL.glEnable(OpenGLConstants.GL_COLOR_MATERIAL);
		@Pc(125) float[] zeroAmbient = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
		for (@Pc(127) int j = 0; j < 8; j++) {
			@Pc(133) int glLight = j + 16384;
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_AMBIENT, zeroAmbient, 0);
			OpenGL.glLightf(glLight, OpenGLConstants.GL_CONSTANT_ATTENUATION, 0.0F);
			OpenGL.glLightf(glLight, OpenGLConstants.GL_LINEAR_ATTENUATION, 0.0F);
		}
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT0);
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT1);
		OpenGL.glFogf(OpenGLConstants.GL_FOG_DENSITY, 0.95F);
		OpenGL.glFogi(OpenGLConstants.GL_FOG_MODE, OpenGLConstants.GL_LINEAR);
		OpenGL.glHint(OpenGLConstants.GL_FOG_HINT, OpenGLConstants.GL_FASTEST);
		this.sunColorRgb = this.fogColor = -1;
		this.la();
	}

	@OriginalMember(owner = "client!qo", name = "y", descriptor = "(I)V")
	public void setupSpriteRendering() {
		if (this.functionMask == 2) {
			return;
		}
		this.setScreenProjectionMode();
		this.toggleFog(false);
		this.setLightingEnabled(false);
		this.toggleZbuffer(false);
		this.setDepthMask(false);
		this.renderTexture(-2);
		this.functionMask = 2;
	}

	@OriginalMember(owner = "client!qo", name = "I", descriptor = "()I")
	@Override
	public int I() {
		@Pc(6) int count = this.polygonCount;
		this.polygonCount = 0;
		return count;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ILclient!ni;)V")
	public void setElementsBuffer(@OriginalArg(1) ElementsBufferGL elementsBuffer) {
		if (this.glBinding != elementsBuffer) {
			if (this.vertexbufferSupported) {
				OpenGL.glBindBufferARB(OpenGLConstants.GL_ELEMENT_ARRAY_BUFFER, elementsBuffer.getId());
			}
			this.glBinding = elementsBuffer;
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!nh;Lclient!nh;ILclient!nh;Lclient!nh;)V")
	public void toggleDrawingClientstates(@OriginalArg(0) OpenGlArrayDrawBuffer vertices, @OriginalArg(1) OpenGlArrayDrawBuffer colors, @OriginalArg(3) OpenGlArrayDrawBuffer textureCoordinates, @OriginalArg(4) OpenGlArrayDrawBuffer normals) {
		if (vertices == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_VERTEX_ARRAY);
		} else {
			this.setArrayBuffer(vertices.buffer);
			OpenGL.glVertexPointer(vertices.size, vertices.type, this.currentArrayBuffer.getStride(), this.currentArrayBuffer.getAddress() + (long) vertices.offset);
			OpenGL.glEnableClientState(OpenGLConstants.GL_VERTEX_ARRAY);
		}
		if (normals == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_NORMAL_ARRAY);
		} else {
			this.setArrayBuffer(normals.buffer);
			OpenGL.glNormalPointer(normals.type, this.currentArrayBuffer.getStride(), this.currentArrayBuffer.getAddress() + (long) normals.offset);
			OpenGL.glEnableClientState(OpenGLConstants.GL_NORMAL_ARRAY);
		}
		if (colors == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_COLOR_ARRAY);
		} else {
			this.setArrayBuffer(colors.buffer);
			OpenGL.glColorPointer(colors.size, colors.type, this.currentArrayBuffer.getStride(), this.currentArrayBuffer.getAddress() + (long) colors.offset);
			OpenGL.glEnableClientState(OpenGLConstants.GL_COLOR_ARRAY);
		}
		if (textureCoordinates == null) {
			OpenGL.glDisableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY);
		} else {
			this.setArrayBuffer(textureCoordinates.buffer);
			OpenGL.glTexCoordPointer(textureCoordinates.size, textureCoordinates.type, this.currentArrayBuffer.getStride(), this.currentArrayBuffer.getAddress() + (long) textureCoordinates.offset);
			OpenGL.glEnableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY);
		}
	}

	@OriginalMember(owner = "client!qo", name = "x", descriptor = "()I")
	@Override
	public int getMaxLights() {
		return 4;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	@Override
	public OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite surface, @OriginalArg(1) DepthBuffer buffer) {
		return null;
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "()Lclient!bca;")
	@Override
	public Matrix camera() {
		return this.viewMatrix43;
	}

	@OriginalMember(owner = "client!qo", name = "z", descriptor = "(I)V")
	private void setDepthMask() {
		OpenGL.glDepthMask(this.enableDepthMask && this.depthBufferingEnabled);
	}

	@OriginalMember(owner = "client!qo", name = "y", descriptor = "()Lclient!bca;")
	@Override
	public Matrix scratchMatrix() {
		return this.scratchMatrix;
	}

	@OriginalMember(owner = "client!qo", name = "i", descriptor = "()I")
	@Override
	public int i() {
		return this.zNear;
	}

	@OriginalMember(owner = "client!qo", name = "ya", descriptor = "()V")
	@Override
	public void ya() {
		this.setDepthMask(true);
		OpenGL.glClear(OpenGLConstants.GL_DEPTH_BUFFER_BIT);
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(Z)V")
	@Override
	public void method7250(@OriginalArg(0) boolean enabled) {
	}

	@OriginalMember(owner = "client!qo", name = "A", descriptor = "(I)V")
	public void setLightingSourceParams() {
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_POSITION, this.sunDirection, 0);
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_POSITION, this.lightingDirection, 0);
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "()Z")
	@Override
	public boolean stillwaterEffectLoaded() {
		return this.effectManager.isStillWaterSupported();
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "()Z")
	@Override
	public boolean bloom() {
		return this.rectangularTexture != null && this.rectangularTexture.isActive();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(I[Lclient!ab;)V")
	@Override
	public void copyLights(@OriginalArg(0) int count, @OriginalArg(1) PointLight[] pointLights) {
		for (@Pc(3) int i = 0; i < count; i++) {
			this.lightNodes[i] = pointLights[i];
		}
		this.lastSceneLightCount = count;
		if (this.depthClippingMode != 1) {
			this.updatePointLights();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Z)V")
	@Override
	public void setShrinkTextures(@OriginalArg(0) boolean shrink) {
	}

	@OriginalMember(owner = "client!qo", name = "B", descriptor = "(I)V")
	private void detach() {
		this.activeOpenglInstance.a();
	}

	@OriginalMember(owner = "client!qo", name = "pa", descriptor = "()V")
	@Override
	public void pa() {
		this.stillWaterToggled = false;
	}

	@OriginalMember(owner = "client!qo", name = "r", descriptor = "(IIIIIII)I")
	@Override
	public int r(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2, @OriginalArg(6) int projectionDepth) {
		@Pc(30) float depth1 = (float) z1 * this.viewMatrix43.zC + (float) y1 * this.viewMatrix43.zB + (float) x1 * this.viewMatrix43.zA + this.viewMatrix43.zD;
		@Pc(55) float depth2 = (float) z2 * this.viewMatrix43.zC + this.viewMatrix43.zA * (float) x2 + (float) y2 * this.viewMatrix43.zB + this.viewMatrix43.zD;
		@Pc(57) int outcode = 0;
		if (depth1 < (float) this.zNear && (float) this.zNear > depth2) {
			outcode |= 0x10;
		} else if ((float) this.zFar < depth1 && depth2 > (float) this.zFar) {
			outcode |= 0x20;
		}
		@Pc(124) int screenX1 = (int) ((float) this.projectionScaleX * (this.viewMatrix43.xC * (float) z1 + this.viewMatrix43.xB * (float) y1 + this.viewMatrix43.xA * (float) x1 + this.viewMatrix43.xD) / (float) projectionDepth);
		@Pc(157) int screenX2 = (int) ((float) this.projectionScaleX * (this.viewMatrix43.xD + this.viewMatrix43.xC * (float) z2 + (float) x2 * this.viewMatrix43.xA + this.viewMatrix43.xB * (float) y2) / (float) projectionDepth);
		if (this.viewX1 > (float) screenX1 && this.viewX1 > (float) screenX2) {
			outcode |= 0x1;
		} else if (this.viewX2 < (float) screenX1 && (float) screenX2 > this.viewX2) {
			outcode |= 0x2;
		}
		@Pc(224) int screenY1 = (int) ((this.viewMatrix43.yD + this.viewMatrix43.yC * (float) z1 + this.viewMatrix43.yA * (float) x1 + (float) y1 * this.viewMatrix43.yB) * (float) this.projectionScaleY / (float) projectionDepth);
		@Pc(257) int screenY2 = (int) ((float) this.projectionScaleY * ((float) z2 * this.viewMatrix43.yC + (float) y2 * this.viewMatrix43.yB + this.viewMatrix43.yA * (float) x2 + this.viewMatrix43.yD) / (float) projectionDepth);
		if (this.viewY1 > (float) screenY1 && (float) screenY2 < this.viewY1) {
			outcode |= 0x4;
		} else if ((float) screenY1 > this.viewY2 && this.viewY2 < (float) screenY2) {
			outcode |= 0x8;
		}
		return outcode;
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(II)I")
	public int getBytesPerPixel(@OriginalArg(0) int format) {
		if (format == 6406 || format == 6409) {
			return 1;
		} else if (format == 6410 || format == 34846 || format == 34844) {
			return 2;
		} else if (format == 6407) {
			return 3;
		} else if (format == 6408 || format == 34847) {
			return 4;
		} else if (format == 34843) {
			return 6;
		} else if (format == 34842) {
			return 8;
		} else if (format == 6402) {
			return 3;
		} else if (format == 6401) {
			return 1;
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(FFFB)V")
	private void renderAnimatedTexture(@OriginalArg(0) float offsetV, @OriginalArg(1) float offsetW, @OriginalArg(2) float offsetU) {
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		if (this.textureUpdate) {
			OpenGL.glLoadIdentity();
		}
		OpenGL.glTranslatef(offsetU, offsetV, offsetW);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		this.textureUpdate = true;
	}

	@OriginalMember(owner = "client!qo", name = "n", descriptor = "(II)V")
	public synchronized void requestFrameBufferCleanup(@OriginalArg(1) int bufferSize) {
		@Pc(8) IntNode intNode = new IntNode(bufferSize);
		this.frameBuffersAwaitingDeallocation.addLast(intNode);
	}

	@OriginalMember(owner = "client!qo", name = "v", descriptor = "()V")
	@Override
	public void compositeBloomEffects() {
		this.framebufferCompositor.compositeEffects();
	}

	@OriginalMember(owner = "client!qo", name = "P", descriptor = "(IIIII)V")
	@Override
	public void P(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int height, @OriginalArg(3) int colour, @OriginalArg(4) int blendMode) {
		this.enter2dMode();
		this.setBlendMode(blendMode);
		@Pc(15) float startX = (float) x + 0.35F;
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		@Pc(35) float startY = (float) y + 0.35F;
		OpenGL.glBegin(OpenGLConstants.GL_LINES);
		OpenGL.glVertex2f(startX, startY);
		OpenGL.glVertex2f(startX, (float) height + startY);
		OpenGL.glEnd();
	}

	@OriginalMember(owner = "client!qo", name = "E", descriptor = "()I")
	@Override
	public int E() {
		return this.renderBuffersSize + this.textureBuffersSize + this.vboBuffersSize;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	@Override
	public EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler environmentSampler, @OriginalArg(1) EnvironmentSampler environmentSampler2, @OriginalArg(2) float alpha, @OriginalArg(3) EnvironmentSampler environmentSampler3) {
		if (environmentSampler != null && environmentSampler2 != null && this.arbTextureCubeMap && this.extFramebufferObject) {
			@Pc(15) OpenGlCubeTextureGenerator result = null;
			@Pc(18) GlEnvironmentSampler glEnvironmentSampler = (GlEnvironmentSampler) environmentSampler;
			@Pc(21) GlEnvironmentSampler glEnvironmentSampler2 = (GlEnvironmentSampler) environmentSampler2;
			@Pc(25) GlCubeTexture glCubeTexture = glEnvironmentSampler.getCubeTexture();
			@Pc(29) GlCubeTexture glCubeTexture2 = glEnvironmentSampler2.getCubeTexture();
			if (glCubeTexture != null && glCubeTexture2 != null) {
				@Pc(44) int size = glCubeTexture2.size >= glCubeTexture.size ? glCubeTexture2.size : glCubeTexture.size;
				if (environmentSampler3 != environmentSampler && environmentSampler2 != environmentSampler3 && environmentSampler3 instanceof OpenGlCubeTextureGenerator) {
					@Pc(56) OpenGlCubeTextureGenerator previous = (OpenGlCubeTextureGenerator) environmentSampler3;
					if (size == previous.getSize()) {
						result = previous;
					}
				}
				if (result == null) {
					result = new OpenGlCubeTextureGenerator(this, size);
				}
				if (result.blendCubeTextures(glCubeTexture, alpha, glCubeTexture2)) {
					return result;
				}
			}
		}
		return alpha < 0.5F ? environmentSampler : environmentSampler2;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void setCanvas(@OriginalArg(0) Canvas canvas) {
		this.currentCanvas = null;
		this.currentSurface = 0L;
		if (canvas == null || canvas == this.canvas) {
			this.currentCanvas = this.canvas;
			this.currentSurface = this.canvasSurface;
		} else if (this.canvasSurfaces.containsKey(canvas)) {
			@Pc(36) Long long2 = (Long) this.canvasSurfaces.get(canvas);
			this.currentSurface = long2;
			this.currentCanvas = canvas;
		}
		if (this.currentCanvas == null || this.currentSurface == 0L) {
			throw new RuntimeException();
		}
		this.activeOpenglInstance.setSurface(this.currentSurface);
		this.updateCanvasSize();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZII)V")
	public void renderTexture(@OriginalArg(0) boolean hasLight, @OriginalArg(1) int materialId) {
		this.renderTexture(hasLight, materialId, true);
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(II)I")
	@Override
	public int compareFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		return maskB ^ maskA & maskB;
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Override
	public void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count, @OriginalArg(2) int x, @OriginalArg(3) int y) throws FlipException {
		this.flip(x, y);
	}

	@OriginalMember(owner = "client!qo", name = "C", descriptor = "(I)V")
	private void updateFog() {
		this.fogEndPosition = (float) (this.zFar - this.fogOffset) - this.fogMin;
		this.fogStartPosition = this.fogEndPosition - this.densityFactor * (float) this.fogDensity;
		if ((float) this.zNear > this.fogStartPosition) {
			this.fogStartPosition = this.zNear;
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_START, this.fogStartPosition);
		OpenGL.glFogf(OpenGLConstants.GL_FOG_END, this.fogEndPosition);
		OpenGlToolkit.BUFFER_4F[0] = (float) (this.fogColor & 0xFF0000) / 1.671168E7F;
		OpenGlToolkit.BUFFER_4F[1] = (float) (this.fogColor & 0xFF00) / 65280.0F;
		OpenGlToolkit.BUFFER_4F[2] = (float) (this.fogColor & 0xFF) / 255.0F;
		OpenGL.glFogfv(OpenGLConstants.GL_FOG_COLOR, OpenGlToolkit.BUFFER_4F, 0);
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(ZB)V")
	public void setHideLights(@OriginalArg(0) boolean enabled) {
		if (enabled != this.hideLights) {
			this.hideLights = enabled;
			this.toggleLighting();
		}
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(II)V")
	@Override
	public void flip(@OriginalArg(0) int x, @OriginalArg(1) int y) throws FlipException {
		try {
			if (this.scaledTarget != null) {
				// Not part of the original client: the frame was drawn into the scaled target at the
				// game's size; copy it to the window, scaled, before the buffers swap.
				OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST);
				if (this.scaledMultisample != null) {
					OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample.framebufferId());
					OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget.framebufferId());
					OpenGL.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, this.scaledWidth, this.scaledHeight, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST);
				}
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget.framebufferId());
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, 0);
				OpenGL.glBlitFramebufferEXT(0, 0, this.scaledWidth, this.scaledHeight, 0, 0, ClientFeatures.scale(this.scaledWidth), ClientFeatures.scale(this.scaledHeight),
					OpenGLConstants.GL_COLOR_BUFFER_BIT, ClientFeatures.wholeScale() ? OpenGLConstants.GL_NEAREST : OpenGLConstants.GL_LINEAR);
				if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
					OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
				}
				this.activeOpenglInstance.swapBuffers();
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer());
				return;
			}
			this.activeOpenglInstance.swapBuffers();
		} catch (@Pc(4) Exception ex) {
		}
	}

	// Not part of the original client: a copy or read from the frame cannot come from a multisampled
	// framebuffer, so while the frame is the multisampled scaled target, the part wanted is resolved
	// into the scaled target and read from there until endReadFrame; a pushed framebuffer reads as is.
	private boolean readingResolved;

	public void beginReadFrame(int x, int y, int width, int height) {
		if (this.scaledMultisample == null || this.readTargetIndex >= 0 || this.drawTargetIndex >= 0) {
			return;
		}
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample.framebufferId());
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledTarget.framebufferId());
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST);
		OpenGL.glBlitFramebufferEXT(x, y, x + width, y + height, x, y, x + width, y + height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST);
		if (this.clipLeft != 0 || this.clipTop != 0 || this.clipRight != this.viewportWidth || this.clipBottom != this.viewportHeight) {
			OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
		}
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.scaledMultisample.framebufferId());
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledTarget.framebufferId());
		this.readingResolved = true;
	}

	public void endReadFrame() {
		if (this.readingResolved) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.scaledMultisample.framebufferId());
			this.readingResolved = false;
		}
	}

	// Not part of the original client: the anti-aliasing pipeline (client.msaa, client.smaa,
	// client.renderScale). The 3D view of each frame is drawn into a framebuffer of its own, the
	// view's size, multisampled by the samples asked for, then resolved and drawn into the frame as
	// a textured quad, so that the interface drawn before and after it is never multisampled.
	private final int sceneSettingSamples;
	private boolean sceneAvailable;
	private int sceneMaxSamples;
	private GlSceneTarget sceneTarget;
	private boolean sceneActive;
	private int sceneX;
	private int sceneY;
	private int sceneWidth;
	private int sceneHeight;
	private int sceneSavedOffsetX;
	private int sceneSavedOffsetY;
	private double sceneScale = 1.0D;
	private GlSmaaFilter smaaFilter;

	// Whether the viewport and clip are to be scaled now: the scene framebuffer is the draw target and
	// is drawn larger than the view. Another framebuffer pushed over it keeps its own pixels.
	private boolean sceneScaling() {
		return this.sceneActive && this.sceneScale != 1.0D && this.sceneTarget != null && this.drawTarget == this.sceneTarget.target();
	}

	private boolean sceneAlphaToCoverage;

	// Alpha-to-coverage for cut-out textures (alphaBlendMode 1) while the multisampled 3D view is the
	// draw target, so leaves and fences get smooth edges; off everywhere else, and with client.a2c=false.
	private void setSceneAlphaToCoverage(boolean cutout) {
		boolean on = cutout && ClientFeatures.a2c && this.sceneActive && this.sceneTarget != null && this.sceneTarget.multisample != null && this.drawTarget == this.sceneTarget.multisample;
		if (on != this.sceneAlphaToCoverage) {
			if (on) {
				OpenGL.glEnable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE);
			} else {
				OpenGL.glDisable(OpenGLConstants.GL_SAMPLE_ALPHA_TO_COVERAGE);
			}
			this.sceneAlphaToCoverage = on;
		}
	}

	private int sceneScaled(int value) {
		return (int) Math.floor((double) value * this.sceneScale + 0.5D);
	}

	// Decides, once the extensions are known, whether the pipeline can run on this driver. Without
	// framebuffer objects, blit and multisample it cannot. The window was made without samples before
	// the extensions could be known, and a failed renderer is rebuilt from a setting that is safe mode
	// at startup, so the renderer stays OpenGL without anti-aliasing and says so.
	private void checkScenePipeline() {
		if (!ClientFeatures.AA_PIPELINE || ClientFeatures.aaPipelineUnavailable) {
			return;
		}
		if (!this.extFramebufferObject || !this.framebufferBlit || !this.framebufferMultisample) {
			System.err.println("client.msaa: this OpenGL driver has no framebuffer objects, blit or multisample; anti-aliasing is off");
			ClientFeatures.aaPipelineUnavailable = true;
			return;
		}
		@Pc(0) int[] max = new int[1];
		OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_SAMPLES, max, 0);
		this.sceneMaxSamples = max[0];
		this.sceneAvailable = true;
		System.out.println("client.msaa: the 3D view is drawn in its own framebuffer; up to " + this.sceneMaxSamples + " samples");
	}

	// The samples the view is drawn with now: the switch or the game's setting, within the driver's limit.
	private int sceneSamples() {
		int samples = ClientFeatures.sceneSamples(this.sceneSettingSamples);
		return samples > this.sceneMaxSamples ? this.sceneMaxSamples : samples;
	}

	// Starts the 3D view at the given place in the frame: the scene framebuffer is made or remade to
	// fit, pushed as the target, and the viewport moved so that the view lands at its origin, as the
	// bloom filter does for its layer. The target's size and the projection stay in the frame's pixels.
	public void beginScene(int x, int y, int width, int height) {
		if (!this.sceneAvailable || this.sceneActive || width <= 0 || height <= 0) {
			return;
		}
		int samples = this.sceneSamples();
		double scale = ClientFeatures.renderScale;
		int targetWidth = (int) Math.floor((double) width * scale + 0.5D);
		int targetHeight = (int) Math.floor((double) height * scale + 0.5D);
		if (this.sceneTarget == null || this.sceneTarget.width != targetWidth || this.sceneTarget.height != targetHeight || this.sceneTarget.samples != samples) {
			this.dropSceneTarget();
			GlSceneTarget target = new GlSceneTarget(this, targetWidth, targetHeight, samples, true);
			this.rebindTargets();
			if (!target.complete) {
				target.release();
				this.rebindTargets();
				System.err.println("client.msaa: the scene framebuffer is incomplete on this OpenGL driver; the 3D view is drawn in the frame");
				this.sceneAvailable = false;
				return;
			}
			this.sceneTarget = target;
			System.out.println("client.msaa: scene framebuffer " + targetWidth + "x" + targetHeight + " for a " + width + "x" + height + " view, " + samples + " samples");
		}
		this.sceneX = x;
		this.sceneY = y;
		this.sceneWidth = width;
		this.sceneHeight = height;
		this.sceneSavedOffsetX = this.viewportOffsetX;
		this.sceneSavedOffsetY = this.viewportOffsetY;
		this.sceneScale = (double) targetWidth / (double) width;
		this.sceneActive = true;
		this.pushTarget(this.sceneTarget.target());
		this.setViewportOffset(height + y - this.viewportHeight, -x);
	}

	// Ends the 3D view: the target is popped, the view resolved into its texture and drawn over its
	// place in the frame. The clip is not reset through la(), which would move the picking bounds.
	public void endScene() {
		if (!this.sceneActive) {
			return;
		}
		this.setSceneAlphaToCoverage(false);
		this.popTarget(this.sceneTarget.target());
		this.sceneActive = false;
		this.setViewportOffset(this.sceneSavedOffsetY, this.sceneSavedOffsetX);
		OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST);
		if (this.sceneTarget.multisample != null) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.sceneTarget.multisample.framebufferId());
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.sceneTarget.resolve.framebufferId());
			OpenGL.glBlitFramebufferEXT(0, 0, this.sceneTarget.width, this.sceneTarget.height, 0, 0, this.sceneTarget.width, this.sceneTarget.height, OpenGLConstants.GL_COLOR_BUFFER_BIT, OpenGLConstants.GL_NEAREST);
			this.rebindTargets();
		}
		this.setupSpriteRendering();
		this.setBlendMode(0);
		OpenGl2DTexture view = this.sceneTarget.colour;
		if (ClientFeatures.smaa) {
			if (this.smaaFilter == null) {
				this.smaaFilter = new GlSmaaFilter(this);
			}
			if (this.smaaFilter.available) {
				this.smaaFilter.apply(view, this.sceneTarget.width, this.sceneTarget.height);
				view = this.smaaFilter.output;
			}
		}
		this.setColourOp(1);
		this.activateTexture(0);
		this.setActiveTexture(view);
		OpenGL.glColor4ub((byte) -1, (byte) -1, (byte) -1, (byte) -1);
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		// Wound as the sprites are, since back faces stay culled in 2D.
		OpenGL.glTexCoord2f(0.0F, 1.0F);
		OpenGL.glVertex2i(this.sceneX, this.sceneY);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2i(this.sceneX, this.sceneY + this.sceneHeight);
		OpenGL.glTexCoord2f(1.0F, 0.0F);
		OpenGL.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY + this.sceneHeight);
		OpenGL.glTexCoord2f(1.0F, 1.0F);
		OpenGL.glVertex2i(this.sceneX + this.sceneWidth, this.sceneY);
		OpenGL.glEnd();
		this.setActiveTexture(null);
		OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
		this.defineScissorBox();
	}

	// Binds the read and draw framebuffers the target stacks say are current, after a bind of our own.
	private void rebindTargets() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.drawTargetIndex >= 0 ? ((GlFramebuffer) this.drawTargetStack[this.drawTargetIndex]).framebufferId() : this.defaultFramebuffer());
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.readTargetIndex >= 0 ? ((GlFramebuffer) this.readTargetStack[this.readTargetIndex]).framebufferId() : this.defaultFramebuffer());
	}

	// Lets go of the scene framebuffer; it is made again on the next frame if still wanted.
	public void dropSceneTarget() {
		if (this.smaaFilter != null) {
			this.smaaFilter.release();
		}
		if (this.sceneTarget != null) {
			this.sceneTarget.release();
			this.sceneTarget = null;
			if (this.activeOpenglInstance != null) {
				this.rebindTargets();
			}
		}
	}

	// Not part of the original client: the framebuffer that stands for the window, 0 unless the frame
	// is drawn into the scaled target.
	public int defaultFramebuffer() {
		if (this.scaledMultisample != null) {
			return this.scaledMultisample.framebufferId();
		}
		return this.scaledTarget == null ? 0 : this.scaledTarget.framebufferId();
	}

	// Not part of the original client: makes, resizes or drops the scaled target to match client.scale
	// and the game's size. Needs the framebuffer object and blit extensions; without them the scale is
	// given up, and the client lays the window out at the game's own size again.
	private void updateScaledTarget() {
		boolean wanted = ClientFeatures.scaled() && this.currentCanvas instanceof GameCanvas && this.width > 0 && this.height > 0;
		if (wanted && (!this.extFramebufferObject || !this.framebufferBlit)) {
			System.err.println("client.scale: this OpenGL driver has no framebuffer objects, so the game is drawn at its own size");
			ClientFeatures.scaleUnavailable = true;
			wanted = false;
		}
		if (!wanted) {
			this.dropScaledTarget();
			return;
		}
		if (this.scaledTarget != null && this.scaledWidth == this.width && this.scaledHeight == this.height) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.defaultFramebuffer());
			return;
		}
		this.dropScaledTarget();
		GlFramebuffer target = new GlFramebuffer(this);
		OpenGl2DTexture colour = new OpenGl2DTexture(this, OpenGLConstants.GL_TEXTURE_2D, OpenGLConstants.GL_RGBA, this.width, this.height);
		OpenGlRenderBuffer depth = new OpenGlRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height);
		target.bind();
		target.attachTexture(colour, 0);
		target.attachRenderbuffer(8, depth);
		target.drawBuffer(0);
		if (!target.isFramebufferComplete()) {
			target.clearAttachment(0);
			target.clearAttachment(8);
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0);
			System.err.println("client.scale: the scaled framebuffer is incomplete on this OpenGL driver, so the game is drawn at its own size");
			ClientFeatures.scaleUnavailable = true;
			return;
		}
		this.scaledTarget = target;
		this.scaledColour = colour;
		this.scaledDepth = depth;
		this.scaledWidth = this.width;
		this.scaledHeight = this.height;
		if (this.multiSamplesPreference > 1 && this.framebufferMultisample) {
			GlFramebuffer multisample = new GlFramebuffer(this);
			OpenGlRenderBuffer multisampleColour = new OpenGlRenderBuffer(this, OpenGLConstants.GL_RGBA, this.width, this.height, this.multiSamplesPreference);
			OpenGlRenderBuffer multisampleDepth = new OpenGlRenderBuffer(this, GL_DEPTH_COMPONENT, this.width, this.height, this.multiSamplesPreference);
			multisample.bind();
			multisample.attachRenderbuffer(0, multisampleColour);
			multisample.attachRenderbuffer(8, multisampleDepth);
			multisample.drawBuffer(0);
			if (multisample.isFramebufferComplete()) {
				this.scaledMultisample = multisample;
				this.scaledMultisampleColour = multisampleColour;
				this.scaledMultisampleDepth = multisampleDepth;
			} else {
				multisample.clearAttachment(0);
				multisample.clearAttachment(8);
				multisampleColour.dispose();
				multisampleDepth.dispose();
				OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, target.framebufferId());
				System.err.println("client.scale: no multisampled framebuffer on this OpenGL driver, so the scaled frame has no anti-aliasing");
			}
		}
	}

	// Not part of the original client: detaches and lets go of the scaled target; its GL objects are
	// deleted when their finalisers run, as the toolkit's other textures and renderbuffers are.
	private void dropScaledTarget() {
		if (this.scaledTarget == null) {
			return;
		}
		if (this.scaledMultisample != null) {
			OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledMultisample.framebufferId());
			this.scaledMultisample.clearAttachment(0);
			this.scaledMultisample.clearAttachment(8);
			this.scaledMultisampleColour.dispose();
			this.scaledMultisampleDepth.dispose();
			this.scaledMultisample = null;
			this.scaledMultisampleColour = null;
			this.scaledMultisampleDepth = null;
		}
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.scaledTarget.framebufferId());
		this.scaledTarget.clearAttachment(0);
		this.scaledTarget.clearAttachment(8);
		this.scaledColour.delete();
		this.scaledDepth.dispose();
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, 0);
		this.scaledTarget = null;
		this.scaledColour = null;
		this.scaledDepth = null;
	}

	private static final int GL_DEPTH_COMPONENT = 6402;

	@OriginalMember(owner = "client!qo", name = "f", descriptor = "(II)V")
	@Override
	public void f(@OriginalArg(0) int near, @OriginalArg(1) int far) {
		if (near == this.zNear && this.zFar == far) {
			return;
		}
		this.zNear = near;
		this.zFar = far;
		this.updateProjectionMatrix();
		this.updateFog();
		if (this.depthClippingMode == 3) {
			this.loadOrthoProjection();
			return;
		}
		if (this.depthClippingMode != 2) {
			return;
		}
		this.loadProjectionMatrix();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(IIII)V")
	@Override
	public void method7261(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		this.framebufferCompositor.prepareForRendering(width, x, height, y);
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(ILclient!tv;)V")
	public void popReadTarget(@OriginalArg(1) GlRenderTarget target) {
		if (this.readTargetIndex < 0 || target != this.readTargetStack[this.readTargetIndex]) {
			throw new RuntimeException();
		}
		this.readTargetStack[this.readTargetIndex--] = null;
		target.unbindRead();
		if (this.readTargetIndex < 0) {
			this.readTarget = null;
		} else {
			this.readTarget = this.readTargetStack[this.readTargetIndex];
			this.readTarget.bindRead();
		}
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(I)V")
	@Override
	public void allocateThreads(@OriginalArg(0) int threadCount) {
		if (threadCount != 1) {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!qo", name = "d", descriptor = "(II)Lclient!he;")
	@Override
	public Sprite method7268(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!qo", name = "D", descriptor = "(I)V")
	private void updateProjectionMatrix() {
		@Pc(6) float[] matrix = this.projectionMatrix;
		@Pc(18) float left = (float) (this.zNear * -this.projectionCenterX) / (float) this.projectionScaleX;
		@Pc(32) float right = (float) ((this.viewportWidth - this.projectionCenterX) * this.zNear) / (float) this.projectionScaleX;
		@Pc(49) float top = (float) (this.zNear * this.projectionCenterY) / (float) this.projectionScaleY;
		@Pc(64) float bottom = (float) ((this.projectionCenterY - this.viewportHeight) * this.zNear) / (float) this.projectionScaleY;
		if (right == left || bottom == top) {
			matrix[13] = 0.0F;
			matrix[15] = 1.0F;
			matrix[3] = 0.0F;
			matrix[11] = 0.0F;
			matrix[12] = 0.0F;
			matrix[4] = 0.0F;
			matrix[6] = 0.0F;
			matrix[8] = 0.0F;
			matrix[7] = 0.0F;
			matrix[1] = 0.0F;
			matrix[10] = 1.0F;
			matrix[14] = 0.0F;
			matrix[5] = 1.0F;
			matrix[0] = 1.0F;
			matrix[9] = 0.0F;
			matrix[2] = 0.0F;
		} else {
			@Pc(144) float twoNear = (float) this.zNear * 2.0F;
			matrix[1] = 0.0F;
			matrix[5] = twoNear / (top - bottom);
			matrix[9] = (top + bottom) / (-bottom + top);
			matrix[3] = 0.0F;
			matrix[4] = 0.0F;
			matrix[8] = (right + left) / (right - left);
			matrix[7] = 0.0F;
			matrix[14] = this.projectionDepthOffset = -((float) this.zFar * twoNear) / (float) (this.zFar - this.zNear);
			matrix[2] = 0.0F;
			matrix[11] = -1.0F;
			matrix[15] = 0.0F;
			matrix[10] = this.projectionDepthScale = (float) -(this.zFar + this.zNear) / (float) (this.zFar - this.zNear);
			matrix[12] = 0.0F;
			matrix[0] = twoNear / (right - left);
			matrix[13] = 0.0F;
			matrix[6] = 0.0F;
		}
		this.updateDepthRange();
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "()V")
	@Override
	public void finish() {
		OpenGL.glFinish();
	}

	@OriginalMember(owner = "client!qo", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	@Override
	public NativeSprite generateSprite(@OriginalArg(0) SpriteType indexedImage, @OriginalArg(1) boolean paletteSprite) {
		@Pc(12) int[] pixels = new int[indexedImage.width * indexedImage.height];
		@Pc(14) int srcIndex = 0;
		@Pc(16) int dstIndex = 0;
		@Pc(21) int y;
		@Pc(25) int x;
		if (indexedImage.alpha == null) {
			for (y = 0; y < indexedImage.height; y++) {
				for (x = 0; x < indexedImage.width; x++) {
					@Pc(83) int colour = indexedImage.pallete[indexedImage.pixels[srcIndex++] & 0xFF];
					pixels[dstIndex++] = colour == 0 ? 0 : colour | 0xFF000000;
				}
			}
		} else {
			for (y = 0; y < indexedImage.height; y++) {
				for (x = 0; x < indexedImage.width; x++) {
					pixels[dstIndex++] = indexedImage.alpha[srcIndex] << 24 | indexedImage.pallete[indexedImage.pixels[srcIndex] & 0xFF];
					srcIndex++;
				}
			}
		}
		@Pc(120) NativeSprite sprite = this.createNativeSprite(indexedImage.width, pixels, indexedImage.width, indexedImage.height);
		sprite.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY);
		return sprite;
	}

	@OriginalMember(owner = "client!qo", name = "b", descriptor = "(ZI)V")
	public void toggleFog(@OriginalArg(0) boolean enableFog) {
		if (enableFog != this.fogEnabled) {
			this.fogEnabled = enableFog;
			this.toggleFog();
			this.functionMask &= 0xFFFFFFE0;
		}
	}

	@OriginalMember(owner = "client!qo", name = "o", descriptor = "(II)V")
	public void setTextureEnvColour(@OriginalArg(0) int colour) {
		OpenGlToolkit.BUFFER_4F[2] = (float) (colour & 0xFF) / 255.0F;
		OpenGlToolkit.BUFFER_4F[3] = (float) (colour >>> 24) / 255.0F;
		OpenGlToolkit.BUFFER_4F[1] = (float) (colour & 0xFF00) / 65280.0F;
		OpenGlToolkit.BUFFER_4F[0] = (float) (colour & 0xFF0000) / 1.671168E7F;
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, OpenGlToolkit.BUFFER_4F, 0);
	}

	@OriginalMember(owner = "client!qo", name = "na", descriptor = "(IIII)[I")
	@Override
	public int[] na(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		@Pc(10) int[] pixels = new int[width * height];
		this.beginReadFrame(x, this.viewportHeight - y - height, width, height); // Not part of the original client: see beginReadFrame.
		for (@Pc(12) int i = 0; i < height; i++) {
			OpenGL.glReadPixelsi(x, this.viewportHeight - y - i, width, 1, OpenGLConstants.GL_BGRA, this.pixelType, pixels, width * i);
		}
		this.endReadFrame();
		return pixels;
	}

	@OriginalMember(owner = "client!qo", name = "e", descriptor = "(Z)V")
	private void updateViewport() {
		if (this.sceneScaling()) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			OpenGL.glViewport(this.sceneScaled(this.viewportOffsetX), this.sceneScaled(this.viewportOffsetY), this.sceneScaled(this.viewportWidth), this.sceneScaled(this.viewportHeight));
			return;
		}
		OpenGL.glViewport(this.viewportOffsetX, this.viewportOffsetY, this.viewportWidth, this.viewportHeight);
	}

	@OriginalMember(owner = "client!qo", name = "w", descriptor = "()Z")
	@Override
	public boolean supportsAntiAliasing() {
		return this.arbMultisample && (!this.bloom() || this.advancedFramebuffers);
	}

	@OriginalMember(owner = "client!qo", name = "c", descriptor = "(III)V")
	public synchronized void requestRenderBufferCleanup(@OriginalArg(0) int handle, @OriginalArg(1) int bufferSize) {
		@Pc(12) IntNode intNode = new IntNode(bufferSize);
		intNode.key = handle;
		this.renderBuffersAwaitingRemoval.addLast(intNode);
	}

	@OriginalMember(owner = "client!qo", name = "m", descriptor = "(B)V")
	private void defineScissorBox() {
		if (this.sceneScaling() && this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			// Not part of the original client: the scene framebuffer is the render scale larger.
			int x0 = this.sceneScaled(this.viewportOffsetX + this.clipLeft);
			int y0 = this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipBottom);
			OpenGL.glScissor(x0, y0, this.sceneScaled(this.viewportOffsetX + this.clipRight) - x0, this.sceneScaled(this.viewportOffsetY + this.viewportHeight - this.clipTop) - y0);
			return;
		}
		if (this.clipLeft <= this.clipRight && this.clipTop <= this.clipBottom) {
			OpenGL.glScissor(this.viewportOffsetX + this.clipLeft, this.viewportOffsetY + this.viewportHeight - this.clipBottom, this.clipRight - this.clipLeft, -this.clipTop + this.clipBottom);
		} else {
			OpenGL.glScissor(0, 0, 0, 0);
		}
	}

	@OriginalMember(owner = "client!qo", name = "z", descriptor = "()Z")
	@Override
	public boolean method7311() {
		return false;
	}
	@OriginalMember(owner = "client!jfa", name = "g", descriptor = "I")
	public static final int MAX_LIGHTS = 4;
	@OriginalMember(owner = "client!rl", name = "g", descriptor = "[I")
	public static final int[] deleteIdBuffer = new int[1000];
	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(IILjava/awt/Canvas;Lclient!d;)Lclient!ha;")
	public static Toolkit create(@OriginalArg(1) int multiSamples, @OriginalArg(2) Canvas canvas, @OriginalArg(3) MaterialProvider textureSource) {
		return new GlToolkit(canvas, textureSource, multiSamples);
	}
}
