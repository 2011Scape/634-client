package com.jagex.game.runetek5.toolkit;

import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.core.constants.PciVendorId;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.sprite.Sprite;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.statics.ZoneLocRotation;
import jaclib.memory.Stream;
import jaggl.MapBuffer;
import jaggl.OpenGL;
import java.awt.Canvas;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.VertexDeclarationGL;
import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.OpenGlArbArrayBuffer;
import com.jagex.game.runetek5.OpenGlArbElementArrayBuffer;
import com.jagex.game.runetek5.TexCoordGenMode;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.VertexAttribute;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.GlTransparentWaterPass;
import com.jagex.game.runetek5.GlUnderwaterPass;
import com.jagex.game.runetek5.GlslEnvironmentMappedWaterPass;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.CubeTextureSampler;
import com.jagex.game.runetek5.RenderPass;
import com.jagex.game.runetek5.ChatLineCount;
import com.jagex.game.runetek5.graphics.texture.sampler.GlCubeMapTexture;
import com.jagex.game.runetek5.graphics.texture.sampler.GlRectangleTexture;
import com.jagex.game.runetek5.graphics.texture.sampler.GlTexture2D;
import com.jagex.game.runetek5.graphics.texture.sampler.GlTexture3D;
import com.jagex.game.runetek5.graphics.texture.TextureState;
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode;
import com.jagex.game.runetek5.toolkit.companion.PrimitiveDrawMode;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import com.jagex.game.runetek5.statics.D3DToolkitFactory;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.TextureCombineFunction;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!bga")
public final class GlxToolkit extends AbstractNativeToolkit {

	@OriginalMember(owner = "client!bga", name = "Di", descriptor = "J")
	private long lastGcTime;

	@OriginalMember(owner = "client!bga", name = "Fi", descriptor = "Z")
	private boolean enableVertexColor;

	@OriginalMember(owner = "client!bga", name = "Gi", descriptor = "I")
	private int textureAllocationPosition;

	@OriginalMember(owner = "client!bga", name = "Hi", descriptor = "Z")
	private boolean enableVertexArray;

	@OriginalMember(owner = "client!bga", name = "Ii", descriptor = "Z")
	private boolean modelMatrixPushed;

	@OriginalMember(owner = "client!bga", name = "Ji", descriptor = "Z")
	private boolean enableVertexNormal;

	@OriginalMember(owner = "client!bga", name = "Li", descriptor = "Z")
	private boolean viewMatrixLoaded;

	@OriginalMember(owner = "client!bga", name = "Sh", descriptor = "Lclient!uh;")
	private final Deque displayListsToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "wi", descriptor = "Lclient!uh;")
	private final Deque textureBuffersToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "yi", descriptor = "Lclient!uh;")
	private final Deque texturesToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "zi", descriptor = "Lclient!uh;")
	private final Deque frameBuffersToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "Ai", descriptor = "Lclient!uh;")
	private final Deque renderBuffersToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "Bi", descriptor = "Lclient!uh;")
	private final Deque programsToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "Ci", descriptor = "Lclient!uh;")
	private final Deque shadersToDelete = new Deque();

	@OriginalMember(owner = "client!bga", name = "Ei", descriptor = "[Lclient!bba;")
	private final OpenGlArbArrayBuffer[] activeArrayBuffers = new OpenGlArbArrayBuffer[16];

	@OriginalMember(owner = "client!bga", name = "Ki", descriptor = "Lclient!jaggl/MapBuffer;")
	public final MapBuffer elementArrayMapBuffer = new MapBuffer();

	@OriginalMember(owner = "client!bga", name = "Mi", descriptor = "Lclient!jaggl/MapBuffer;")
	public final MapBuffer arrayMapBuffer = new MapBuffer();

	@OriginalMember(owner = "client!bga", name = "Wi", descriptor = "I")
	private final int polygonCount = 0;

	@OriginalMember(owner = "client!bga", name = "Fh", descriptor = "Lclient!jaggl/OpenGL;")
	private OpenGL openGL;

	@OriginalMember(owner = "client!bga", name = "Oi", descriptor = "Ljava/lang/String;")
	private final String gpuVendor;

	@OriginalMember(owner = "client!bga", name = "Ni", descriptor = "Ljava/lang/String;")
	private final String gpuManufactorer;

	@OriginalMember(owner = "client!bga", name = "Vi", descriptor = "I")
	private final int combinedOpenglVersion;

	@OriginalMember(owner = "client!bga", name = "Ri", descriptor = "Z")
	public boolean GLARBVertexBufferObject;

	@OriginalMember(owner = "client!bga", name = "Qi", descriptor = "Z")
	private boolean GLARBTextureRectangle;

	@OriginalMember(owner = "client!bga", name = "Xi", descriptor = "Z")
	private final boolean GLARBTextureNonPowerOfTwo;

	@OriginalMember(owner = "client!bga", name = "Si", descriptor = "Z")
	public final boolean GLARBVertexShader;

	@OriginalMember(owner = "client!bga", name = "Ui", descriptor = "Z")
	public final boolean GLARBVertexProgram;

	@OriginalMember(owner = "client!bga", name = "Ti", descriptor = "Z")
	public final boolean GLARBFragmentShader;

	@OriginalMember(owner = "client!bga", name = "Yi", descriptor = "[I")
	public final int[] textureSlots;

	@OriginalMember(owner = "client!bga", name = "Pi", descriptor = "I")
	public final int pixelType;

	@OriginalMember(owner = "client!bga", name = "<init>", descriptor = "(Lclient!jaggl/OpenGL;Ljava/awt/Canvas;JLclient!d;Lclient!in;I)V")
	public GlxToolkit(@OriginalArg(0) OpenGL openGL, @OriginalArg(1) Canvas canvas, @OriginalArg(2) long contextHandle, @OriginalArg(3) MaterialProvider textureSource, @OriginalArg(4) Js5ResourceProvider shaders, @OriginalArg(5) int multiSampleCount) {
		super(canvas, Long.valueOf(contextHandle), textureSource, shaders, multiSampleCount, 1);
		try {
			this.openGL = openGL;
			this.openGL.b();
			this.gpuVendor = OpenGL.glGetString(OpenGLConstants.GL_VENDOR).toLowerCase();
			this.gpuManufactorer = OpenGL.glGetString(OpenGLConstants.GL_RENDERER).toLowerCase();
			if (this.gpuVendor.indexOf("microsoft") != -1 || this.gpuVendor.indexOf("brian paul") != -1 || this.gpuVendor.indexOf("mesa") != -1) {
				throw new RuntimeException("");
			}
			@Pc(108) String string = OpenGL.glGetString(OpenGLConstants.GL_VERSION);
			@Pc(116) String[] strings = Utils.split(' ', string.replace('.', ' '));
			if (strings.length < 2) {
				throw new RuntimeException("");
			}
			@Pc(137) int local137;
			try {
				@Pc(131) int majorVersion = Utils.parseInt(strings[0]);
				int minorVersion = Utils.parseInt(strings[1]);
				this.combinedOpenglVersion = majorVersion * 10 + minorVersion;
			} catch (@Pc(147) NumberFormatException ex) {
				throw new RuntimeException("");
			}
			if (this.combinedOpenglVersion < 12) {
				throw new RuntimeException("");
			} else if (!this.openGL.a("GL_ARB_multitexture")) {
				throw new RuntimeException("");
			} else if (this.openGL.a("GL_ARB_texture_env_combine")) {
				@Pc(184) int[] maxTextureUnits = new int[1];
				OpenGL.glGetIntegerv(OpenGLConstants.GL_MAX_TEXTURE_UNITS, maxTextureUnits, 0);
				super.maxConcurrentTextures = maxTextureUnits[0];
				if (super.maxConcurrentTextures < 2) {
					throw new RuntimeException("");
				}
				super.maxActiveLights = 8;
				this.GLARBVertexBufferObject = this.openGL.a("GL_ARB_vertex_buffer_object");
				super.GLARBMultisample = this.openGL.a("GL_ARB_multisample");
				this.GLARBTextureRectangle = this.openGL.a("GL_ARB_texture_rectangle");
				super.cubeMapSupported = this.openGL.a("GL_ARB_texture_cube_map");
				this.GLARBTextureNonPowerOfTwo = this.openGL.a("GL_ARB_texture_non_power_of_two");
				super.SUPPORTS_TEXTURE3D = this.openGL.a("GL_EXT_texture3D");
				this.GLARBVertexShader = this.openGL.a("GL_ARB_vertex_shader");
				this.GLARBVertexProgram = this.openGL.a("GL_ARB_vertex_program");
				this.GLARBFragmentShader = this.openGL.a("GL_ARB_fragment_shader");
				this.openGL.a("GL_ARB_fragment_program");
				this.textureSlots = new int[super.maxConcurrentTextures];
				this.pixelType = Stream.c() ? 33639 : 5121;
				if (this.gpuManufactorer.indexOf("radeon") != -1) {
					int radeonModel = 0;
					@Pc(286) boolean isHd = false;
					@Pc(288) boolean isXSeries = false;
					@Pc(297) String[] strings2 = Utils.split(' ', this.gpuManufactorer.replace('/', ' '));
					for (@Pc(299) int i = 0; i < strings2.length; i++) {
						@Pc(305) String string2 = strings2[i];
						try {
							if (string2.length() > 0) {
								if (string2.charAt(0) == 'x' && string2.length() >= 3 && Utils.isValidInteger(string2.substring(1, 3))) {
									isXSeries = true;
									string2 = string2.substring(1);
								}
								if (string2.equals("hd")) {
									isHd = true;
								} else {
									if (string2.startsWith("hd")) {
										isHd = true;
										string2 = string2.substring(2);
									}
									if (string2.length() >= 4 && Utils.isValidInteger(string2.substring(0, 4))) {
										radeonModel = Utils.parseInt(string2.substring(0, 4));
										break;
									}
								}
							}
						} catch (@Pc(373) Exception ex2) {
						}
					}
					if (!isXSeries && !isHd) {
						if (radeonModel >= 7000 && radeonModel <= 7999) {
							this.GLARBVertexBufferObject = false;
						}
						if (radeonModel >= 7000 && radeonModel <= 9250) {
							super.SUPPORTS_TEXTURE3D = false;
						}
					}
					this.GLARBTextureRectangle &= this.openGL.a("GL_ARB_half_float_pixel");
				}
				this.gpuVendor.indexOf("intel");
				if (this.GLARBVertexBufferObject) {
					try {
						@Pc(427) int[] bufferIds = new int[1];
						OpenGL.glGenBuffersARB(1, bufferIds, 0);
					} catch (@Pc(433) Throwable ex3) {
						throw new RuntimeException("");
					}
				}
			} else {
				throw new RuntimeException("");
			}
		} catch (@Pc(439) Throwable ex4) {
			ex4.printStackTrace();
			this.free();
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(I)V")
	@Override
	public synchronized void tick(@OriginalArg(0) int time) {
		@Pc(7) int count = 0;
		@Pc(11) int cycle = time & Integer.MAX_VALUE;
		@Pc(19) IntNode intNode;
		while (!this.textureBuffersToDelete.isEmpty()) {
			intNode = (IntNode) this.textureBuffersToDelete.removeFirst();
			GlxToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			super.textureBuffersSizeOccupied -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteBuffersARB(count, GlxToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteBuffersARB(count, GlxToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.texturesToDelete.isEmpty()) {
			intNode = (IntNode) this.texturesToDelete.removeFirst();
			GlxToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			super.texturesSizeOccupied -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteTextures(count, GlxToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteTextures(count, GlxToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.frameBuffersToDelete.isEmpty()) {
			intNode = (IntNode) this.frameBuffersToDelete.removeFirst();
			GlxToolkit.deleteIdBuffer[count++] = intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteFramebuffersEXT(count, GlxToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteFramebuffersEXT(count, GlxToolkit.deleteIdBuffer, 0);
			count = 0;
		}
		while (!this.renderBuffersToDelete.isEmpty()) {
			intNode = (IntNode) this.renderBuffersToDelete.removeFirst();
			GlxToolkit.deleteIdBuffer[count++] = (int) intNode.key;
			super.renderBufferSizeOccupied -= intNode.value;
			if (count == 1000) {
				OpenGL.glDeleteRenderbuffersEXT(count, GlxToolkit.deleteIdBuffer, 0);
				count = 0;
			}
		}
		if (count > 0) {
			OpenGL.glDeleteRenderbuffersEXT(count, GlxToolkit.deleteIdBuffer, 0);
		}
		while (!this.displayListsToDelete.isEmpty()) {
			intNode = (IntNode) this.displayListsToDelete.removeFirst();
			OpenGL.glDeleteLists((int) intNode.key, intNode.value);
		}
		@Pc(214) Node node;
		while (!this.programsToDelete.isEmpty()) {
			node = this.programsToDelete.removeFirst();
			OpenGL.glDeleteProgramARB((int) node.key);
		}
		while (!this.shadersToDelete.isEmpty()) {
			node = this.shadersToDelete.removeFirst();
			OpenGL.glDeleteObjectARB(node.key);
		}
		while (!this.displayListsToDelete.isEmpty()) {
			intNode = (IntNode) this.displayListsToDelete.removeFirst();
			OpenGL.glDeleteLists((int) intNode.key, intNode.value);
		}
		if (this.E() > 100663296 && this.lastGcTime + TimeUtils.MILLISECONDS_PER_MINUTE < Utils.currentTimeMillis()) {
			System.gc();
			this.lastGcTime = Utils.currentTimeMillis();
		}
		super.tick(cycle);
	}

	@OriginalMember(owner = "client!bga", name = "j", descriptor = "(B)V")
	@Override
	public void setDepthRenderState() {
		OpenGL.glDepthMask(super.depthBufferingEnabled && super.depthMaskEnabled);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	@Override
	public VertexDeclaration createVertexDeclaration(@OriginalArg(1) VertexDeclarationComponent[] vertexDeclarationElements) {
		return new VertexDeclarationGL(vertexDeclarationElements);
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(Z)V")
	@Override
	public void toggleSun() {
		if (super.sunEnabled) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHT0);
			OpenGL.glEnable(OpenGLConstants.GL_LIGHT1);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHT0);
			OpenGL.glDisable(OpenGLConstants.GL_LIGHT1);
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	@Override
	public void setSurface(@OriginalArg(0) Object object, @OriginalArg(2) Canvas canvas) {
		@Pc(10) Long long2 = (Long) object;
		if (!this.openGL.setSurface(long2)) {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!bga", name = "t", descriptor = "(B)V")
	@Override
	public void uploadModelMatrix() {
		this.loadModelViewMatrix();
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	@Override
	public void setTextureColourSource(@OriginalArg(0) boolean useAlpha, @OriginalArg(2) int sourceIndex, @OriginalArg(3) TextureCombineSource state, @OriginalArg(4) boolean invert) {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_SRC0_RGB, GlxToolkit.toGlCombineSource(state));
		if (useAlpha) {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_OPERAND0_RGB, invert ? OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA : OpenGLConstants.GL_SRC_ALPHA);
		} else {
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, sourceIndex + OpenGLConstants.GL_OPERAND0_RGB, invert ? OpenGLConstants.GL_ONE_MINUS_SRC_COLOR : OpenGLConstants.GL_SRC_COLOR);
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	@Override
	public boolean supportsTextureFormat(@OriginalArg(0) DataType type, @OriginalArg(1) PixelFormat format) {
		return true;
	}

	@OriginalMember(owner = "client!bga", name = "c", descriptor = "(Z)V")
	@Override
	public void uploadProjectionMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadMatrixf(super.activeProjectionSource, 0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!bga", name = "ya", descriptor = "()V")
	@Override
	public void ya() {
		this.setDepthMaskEnabled(true);
		OpenGL.glClear(OpenGLConstants.GL_DEPTH_BUFFER_BIT);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(JI)V")
	public synchronized void requestShaderDeletion(@OriginalArg(0) long pointer) {
		@Pc(7) Node node = new Node();
		node.key = pointer;
		this.shadersToDelete.addLast(node);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(II)Lclient!vm;")
	@Override
	public DepthBuffer createDepthBuffer(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!bga", name = "K", descriptor = "(I)V")
	@Override
	public void setTextureAlphaStage() {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, TextureCombineFunction.toGlCombineFunction(super.textureAlphaCombineModes[super.currentTextureSlot]));
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	@Override
	public Texture3DSampler create3DTexture(@OriginalArg(2) PixelFormat pixelFormat, @OriginalArg(5) byte[] data) {
		return new GlTexture3D(this, pixelFormat, 128, 128, 16, data);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IBI)V")
	public synchronized void requestTextureDeletion(@OriginalArg(0) int value, @OriginalArg(2) int pointer) {
		@Pc(8) IntNode intNode = new IntNode(value);
		intNode.key = pointer;
		this.texturesToDelete.addLast(intNode);
	}

	@OriginalMember(owner = "client!bga", name = "r", descriptor = "(B)V")
	@Override
	public void enableLights() {
	}

	@OriginalMember(owner = "client!bga", name = "H", descriptor = "(I)V")
	@Override
	public void setFogColorAndIntensity() {
		GlxToolkit.scratchFloat4[0] = super.baseLightColorRed * super.intensity;
		GlxToolkit.scratchFloat4[2] = super.intensity * super.baseLightColorBlue;
		GlxToolkit.scratchFloat4[1] = super.baseLightColorGreen * super.intensity;
		GlxToolkit.scratchFloat4[3] = 1.0F;
		OpenGL.glLightModelfv(OpenGLConstants.GL_LIGHT_MODEL_AMBIENT, GlxToolkit.scratchFloat4, 0);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTextureSampler(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(4) byte[] pixels, @OriginalArg(5) PixelFormat pixelFormat, @OriginalArg(7) boolean mipmapping) {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new GlTexture2D(this, pixelFormat, width, height, mipmapping, pixels, 0, 0);
		} else if (this.GLARBTextureRectangle) {
			return new GlRectangleTexture(this, pixelFormat, width, height, pixels, 0, 0);
		} else {
			@Pc(51) GlTexture2D glTexture2D = new GlTexture2D(this, pixelFormat, GLFormat.RGBA, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height));
			glTexture2D.upload(0, height, width, pixels, pixelFormat);
			return glTexture2D;
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(BI)V")
	@Override
	public void setTexCoordIndex(@OriginalArg(1) int index) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!bf;I)V")
	@Override
	public void setTexCoordGeneration(@OriginalArg(0) TexCoordGenMode mode) {
		if (mode == TexCoordGenMode.aClass31_3) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S);
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T);
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R);
			return;
		}
		@Pc(13) int genMode = GlxToolkit.toGlTexGenMode(mode);
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T);
		OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, genMode);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(BZ)V")
	@Override
	public void toggleMultisampling(@OriginalArg(1) boolean enabled) {
		if (enabled) {
			OpenGL.glEnable(32925);
		} else {
			OpenGL.glDisable(32925);
		}
	}

	@OriginalMember(owner = "client!bga", name = "M", descriptor = "(I)V")
	@Override
	public void setTextureMatrix() {
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		if (TextureState.INITIALIZE == super.textureStates[super.currentTextureSlot]) {
			OpenGL.glLoadIdentity();
		} else {
			OpenGL.glLoadMatrixf(super.textureMatrices[super.currentTextureSlot].copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0);
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILclient!tc;I)V")
	@Override
	public void setStreamSource(@OriginalArg(1) NativeStream vertexBuffer, @OriginalArg(2) int streamIndex) {
		this.activeArrayBuffers[streamIndex] = (OpenGlArbArrayBuffer) vertexBuffer;
	}

	@OriginalMember(owner = "client!bga", name = "F", descriptor = "(I)V")
	@Override
	public void setTextureColorStage() {
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, TextureCombineFunction.toGlCombineFunction(super.textureRgbCombineModes[super.currentTextureSlot]));
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(II)Lclient!he;")
	@Override
	public Sprite method7268(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!bga", name = "d", descriptor = "(B)V")
	@Override
	public void unbindCurrentTexture() {
		@Pc(9) int target = this.textureSlots[super.currentTextureSlot];
		if (target != 0) {
			this.textureSlots[super.currentTextureSlot] = 0;
			OpenGL.glBindTexture(target, 0);
			OpenGL.glDisable(target);
		}
	}

	@OriginalMember(owner = "client!bga", name = "q", descriptor = "(B)V")
	@Override
	public void setupGraphics() {
		for (@Pc(10) int i = super.maxConcurrentTextures - 1; i >= 0; i--) {
			OpenGL.glActiveTexture(i + OpenGLConstants.GL_TEXTURE0);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_MODE, OpenGLConstants.GL_COMBINE);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_RGB, OpenGLConstants.GL_MODULATE);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_RGB, OpenGLConstants.GL_CONSTANT);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_COMBINE_ALPHA, OpenGLConstants.GL_MODULATE);
			OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_ALPHA, OpenGLConstants.GL_CONSTANT);
		}
		OpenGL.glTexEnvi(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_SRC2_ALPHA, OpenGLConstants.GL_PREVIOUS);
		OpenGL.glShadeModel(OpenGLConstants.GL_SMOOTH);
		OpenGL.glClearDepth(1.0F);
		OpenGL.glDepthFunc(OpenGLConstants.GL_LEQUAL);
		OpenGL.glPolygonMode(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_FILL);
		OpenGL.glEnable(OpenGLConstants.GL_CULL_FACE);
		OpenGL.glCullFace(OpenGLConstants.GL_BACK);
		OpenGL.glAlphaFunc(OpenGLConstants.GL_GREATER, 0.0F);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		OpenGL.glColorMaterial(OpenGLConstants.GL_FRONT, OpenGLConstants.GL_AMBIENT_AND_DIFFUSE);
		OpenGL.glEnable(OpenGLConstants.GL_COLOR_MATERIAL);
		@Pc(74) float[] black = new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
		for (@Pc(76) int j = 0; j < 8; j++) {
			@Pc(88) int light = j + 16384;
			OpenGL.glLightfv(light, OpenGLConstants.GL_AMBIENT, black, 0);
			OpenGL.glLightf(light, OpenGLConstants.GL_CONSTANT_ATTENUATION, 0.0F);
			OpenGL.glLightf(light, OpenGLConstants.GL_LINEAR_ATTENUATION, 0.0F);
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_DENSITY, 0.95F);
		OpenGL.glFogi(OpenGLConstants.GL_FOG_MODE, OpenGLConstants.GL_LINEAR);
		OpenGL.glHint(OpenGLConstants.GL_FOG_HINT, OpenGLConstants.GL_FASTEST);
		this.openGL.setSwapInterval(0);
		super.setupGraphics();
	}

	@OriginalMember(owner = "client!bga", name = "g", descriptor = "(B)V")
	@Override
	public void toggleFog() {
		if (super.fogSupported && super.fogEnabled && super.fogDistance >= 0) {
			OpenGL.glEnable(OpenGLConstants.GL_FOG);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_FOG);
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	@Override
	public OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite surface, @OriginalArg(1) DepthBuffer buffer) {
		return null;
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	@Override
	public Object createSurface(@OriginalArg(1) Canvas canvas) {
		@Pc(14) long handle = this.openGL.prepareSurface(canvas);
		if (handle == -1L) {
			throw new RuntimeException();
		}
		return Long.valueOf(handle);
	}

	@OriginalMember(owner = "client!bga", name = "c", descriptor = "()Lclient!fia;")
	@Override
	public Renderer renderer() {
		@Pc(7) int vendor = -1;
		if (this.gpuVendor.indexOf("nvidia") != -1) {
			vendor = PciVendorId.NVIDIA;
		} else if (this.gpuVendor.indexOf("intel") != -1) {
			vendor = PciVendorId.INTEL;
		} else if (this.gpuVendor.indexOf("ati") != -1) {
			vendor = PciVendorId.AMD;
		}
		return new Renderer(vendor, "OpenGL", this.combinedOpenglVersion, this.gpuManufactorer, 0L);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	@Override
	public EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler environmentSampler, @OriginalArg(1) EnvironmentSampler environmentSampler2, @OriginalArg(2) float alpha, @OriginalArg(3) EnvironmentSampler environmentSampler3) {
		return alpha < 0.5F ? environmentSampler : environmentSampler2;
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(III)V")
	public synchronized void requestTextureBufferDeletion(@OriginalArg(0) int value, @OriginalArg(1) int pointer) {
		@Pc(14) IntNode intNode = new IntNode(value);
		intNode.key = pointer;
		this.textureBuffersToDelete.addLast(intNode);
	}

	@OriginalMember(owner = "client!bga", name = "h", descriptor = "(Z)V")
	@Override
	public void setSunParameters() {
		GlxToolkit.scratchFloat4[2] = super.baseLightColorBlue * super.sunColorIntensity;
		GlxToolkit.scratchFloat4[3] = 1.0F;
		GlxToolkit.scratchFloat4[0] = super.baseLightColorRed * super.sunColorIntensity;
		GlxToolkit.scratchFloat4[1] = super.baseLightColorGreen * super.sunColorIntensity;
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0);
		GlxToolkit.scratchFloat4[0] = super.baseLightColorRed * -super.antiSunColorIntensity;
		GlxToolkit.scratchFloat4[2] = -super.antiSunColorIntensity * super.baseLightColorBlue;
		GlxToolkit.scratchFloat4[3] = 1.0F;
		GlxToolkit.scratchFloat4[1] = -super.antiSunColorIntensity * super.baseLightColorGreen;
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0);
	}

	@OriginalMember(owner = "client!bga", name = "U", descriptor = "(I)V")
	private void loadModelViewMatrix() {
		if (this.modelMatrixPushed) {
			OpenGL.glPopMatrix();
		}
		if (super.matrixMode.isSceneMode()) {
			if (!this.viewMatrixLoaded) {
				OpenGL.glLoadMatrixf(super.flippedViewMatrix.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0);
				this.viewMatrixLoaded = true;
				this.setLightPosition();
				this.updateSceneLights();
			}
			if (super.isModelMatrixIdentity) {
				this.modelMatrixPushed = false;
			} else {
				OpenGL.glPushMatrix();
				OpenGL.glMultMatrixf(super.modelMatrix43.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0);
				this.modelMatrixPushed = true;
			}
		} else if (super.isModelMatrixIdentity) {
			OpenGL.glLoadIdentity();
			this.modelMatrixPushed = false;
		} else {
			OpenGL.glLoadMatrixf(super.modelMatrix43.copyColumnMajorInto(GlxMatrixScratch.MATRIX_BUFFER), 0);
			this.modelMatrixPushed = false;
		}
	}

	@OriginalMember(owner = "client!bga", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		super.stop();
		if (this.openGL != null) {
			this.openGL.a();
			this.openGL.release();
			this.openGL = null;
		}
	}

	@OriginalMember(owner = "client!bga", name = "o", descriptor = "(I)V")
	@Override
	public void configureFog() {
		super.fogEnd = super.camFar - super.fogOffset;
		super.fogStart = (float) -super.fogDistance + super.fogEnd;
		if (super.fogStart < (float) super.camNear) {
			super.fogStart = super.camNear;
		}
		OpenGL.glFogf(OpenGLConstants.GL_FOG_START, super.fogStart);
		OpenGL.glFogf(OpenGLConstants.GL_FOG_END, super.fogEnd);
		GlxToolkit.scratchFloat4[2] = (float) (super.fogColorRgb & 0xFF) / 255.0F;
		GlxToolkit.scratchFloat4[0] = (float) (super.fogColorRgb & 0xFF0000) / 1.671168E7F;
		GlxToolkit.scratchFloat4[1] = (float) (super.fogColorRgb & 0xFF00) / 65280.0F;
		OpenGL.glFogfv(OpenGLConstants.GL_FOG_COLOR, GlxToolkit.scratchFloat4, 0);
	}

	@OriginalMember(owner = "client!bga", name = "o", descriptor = "()Z")
	@Override
	public boolean hasBloom() {
		return false;
	}

	@OriginalMember(owner = "client!bga", name = "u", descriptor = "(I)V")
	@Override
	public void toggleLights() {
		if (super.lightingEnabled && !super.forceLightsDisabled) {
			OpenGL.glEnable(OpenGLConstants.GL_LIGHTING);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_LIGHTING);
		}
	}

	@OriginalMember(owner = "client!bga", name = "G", descriptor = "(I)V")
	@Override
	public void toggleScissorTest() {
		if (super.boundsCalculated) {
			OpenGL.glEnable(OpenGLConstants.GL_SCISSOR_TEST);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_SCISSOR_TEST);
		}
	}

	@OriginalMember(owner = "client!bga", name = "D", descriptor = "(B)V")
	@Override
	public void setViewport() {
		OpenGL.glViewport(super.viewportOffsetX, super.viewportOffsetY, super.surfaceWidth, super.surfaceHeight);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IIII)V")
	@Override
	public void method7261(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	@Override
	public void drawIndexedPrimitive(@OriginalArg(0) PrimitiveType drawMode, @OriginalArg(1) int primitiveCount, @OriginalArg(2) GpuIndexBuffer gpuIndexBuffer, @OriginalArg(3) int minIndex, @OriginalArg(4) int startIndex, @OriginalArg(5) int vertexCount) {
		@Pc(23) byte glMode;
		@Pc(21) int indexCount;
		if (PrimitiveDrawMode.LINES == drawMode) {
			glMode = 1;
			indexCount = primitiveCount * 2;
		} else if (drawMode == PrimitiveType.aClass134_5) {
			indexCount = primitiveCount + 1;
			glMode = 3;
		} else if (drawMode == PrimitiveType.aClass134_6) {
			glMode = 4;
			indexCount = primitiveCount * 3;
		} else if (PrimitiveType.aClass134_3 == drawMode) {
			glMode = 6;
			indexCount = primitiveCount + 2;
		} else if (drawMode == PrimitiveType.aClass134_4) {
			indexCount = primitiveCount + 2;
			glMode = 5;
		} else {
			indexCount = primitiveCount;
			glMode = 0;
		}
		@Pc(74) DataType indexType = gpuIndexBuffer.getDataType();
		@Pc(77) OpenGlArbElementArrayBuffer glIndexBuffer = (OpenGlArbElementArrayBuffer) gpuIndexBuffer;
		glIndexBuffer.bind();
		OpenGL.glDrawElements(glMode, indexCount, DataType.getGlType(indexType), glIndexBuffer.getBufferAddress() + (long) (startIndex * indexType.bytesPerEntry));
	}

	@OriginalMember(owner = "client!bga", name = "e", descriptor = "(B)V")
	@Override
	public void updateSceneLights() {
		this.resetModelMatrix();
		@Pc(18) int lightIndex;
		for (lightIndex = 0; super.lastSceneLightCount > lightIndex; lightIndex++) {
			@Pc(25) PointLight pointLight = super.lightSources[lightIndex];
			@Pc(29) int range = pointLight.getColour();
			@Pc(33) int glLight = lightIndex + 16386;
			@Pc(39) float intensityScale = pointLight.getIntensity() / 255.0F;
			GlxToolkit.scratchFloat4[0] = pointLight.getX();
			GlxToolkit.scratchFloat4[1] = pointLight.getY();
			GlxToolkit.scratchFloat4[2] = pointLight.getZ();
			GlxToolkit.scratchFloat4[3] = 1.0F;
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_POSITION, GlxToolkit.scratchFloat4, 0);
			GlxToolkit.scratchFloat4[2] = (float) (range & 0xFF) * intensityScale;
			GlxToolkit.scratchFloat4[1] = intensityScale * (float) (range >> 8 & 0xFF);
			GlxToolkit.scratchFloat4[0] = (float) (range >> 16 & 0xFF) * intensityScale;
			GlxToolkit.scratchFloat4[3] = 1.0F;
			OpenGL.glLightfv(glLight, OpenGLConstants.GL_DIFFUSE, GlxToolkit.scratchFloat4, 0);
			OpenGL.glLightf(glLight, OpenGLConstants.GL_QUADRATIC_ATTENUATION, 1.0F / (float) (pointLight.getRadius() * pointLight.getRadius()));
			OpenGL.glEnable(glLight);
		}
		while (super.currentSceneLightCount > lightIndex) {
			OpenGL.glDisable(lightIndex + OpenGLConstants.GL_LIGHT2);
			lightIndex++;
		}
		super.updateSceneLights();
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	@Override
	public void resizeSurface(@OriginalArg(1) Canvas canvas, @OriginalArg(2) Object object) {
		@Pc(12) Long long2 = (Long) object;
		this.openGL.surfaceResized(long2);
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "()V")
	@Override
	public void finish() {
		OpenGL.glFinish();
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	@Override
	public boolean supports3DTextureFormat(@OriginalArg(1) PixelFormat format, @OriginalArg(2) DataType type) {
		return true;
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IILclient!hj;Z)V")
	@Override
	public void drawPrimitive(@OriginalArg(0) int primitiveCount, @OriginalArg(1) int first, @OriginalArg(2) PrimitiveType drawMode) {
		@Pc(22) byte glMode;
		@Pc(26) int vertexCount;
		if (drawMode == PrimitiveDrawMode.LINES) {
			vertexCount = primitiveCount * 2;
			glMode = 1;
		} else if (PrimitiveType.aClass134_5 == drawMode) {
			vertexCount = primitiveCount + 1;
			glMode = 3;
		} else if (PrimitiveType.aClass134_6 == drawMode) {
			glMode = 4;
			vertexCount = primitiveCount * 3;
		} else if (drawMode == PrimitiveType.aClass134_3) {
			glMode = 6;
			vertexCount = primitiveCount + 2;
		} else if (PrimitiveType.aClass134_4 == drawMode) {
			vertexCount = primitiveCount + 2;
			glMode = 5;
		} else {
			vertexCount = primitiveCount;
			glMode = 0;
		}
		OpenGL.glDrawArrays(glMode, first, vertexCount);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "([[IZII)Lclient!nj;")
	@Override
	public CubeTextureSampler generateNativeCubeTexture(@OriginalArg(0) int[][] pixels, @OriginalArg(1) boolean mipmapping, @OriginalArg(3) int size) {
		return new GlCubeMapTexture(this, size, mipmapping, pixels);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTexture(@OriginalArg(0) PixelFormat pixelFormat, @OriginalArg(1) int width, @OriginalArg(3) float[] pixels, @OriginalArg(4) boolean mipmapping, @OriginalArg(7) int height) {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new GlTexture2D(this, pixelFormat, width, height, mipmapping, pixels, 0, 0);
		} else if (this.GLARBTextureRectangle) {
			return new GlRectangleTexture(this, pixelFormat, width, height, pixels, 0, 0);
		} else {
			@Pc(47) GlTexture2D glTexture2D = new GlTexture2D(this, pixelFormat, DataType.aClass26_8, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height));
			glTexture2D.updateFloatData(pixels, pixelFormat, width, height);
			return glTexture2D;
		}
	}

	@OriginalMember(owner = "client!bga", name = "m", descriptor = "()Z")
	@Override
	public boolean bloom() {
		return false;
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IB)Lclient!fj;")
	@Override
	public RenderPass createRenderPass(@OriginalArg(0) int effect) {
		if (effect == 3) {
			return new GlUnderwaterPass(this, super.shaderResource);
		} else if (effect == 4) {
			return new GlTransparentWaterPass(this, super.shaderResource, super.waterTexture);
		} else if (effect == 8) {
			return new GlslEnvironmentMappedWaterPass(this, super.shaderResource, super.waterTexture);
		} else {
			return super.createRenderPass(effect);
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	@Override
	public GpuTexture createTextureSampler(@OriginalArg(0) int width, @OriginalArg(1) int[] pixels, @OriginalArg(2) boolean mipmapping, @OriginalArg(4) int offset, @OriginalArg(5) int rowLength, @OriginalArg(6) int height) {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new GlTexture2D(this, width, height, mipmapping, pixels, 0, rowLength);
		} else if (this.GLARBTextureRectangle) {
			return new GlRectangleTexture(this, width, height, pixels, 0, rowLength);
		} else {
			@Pc(49) GlTexture2D glTexture2D = new GlTexture2D(this, GLFormat.HDR, GLFormat.RGBA, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height));
			glTexture2D.updateTextureRegion(rowLength, pixels, 0, width, 0, height);
			return glTexture2D;
		}
	}

	@OriginalMember(owner = "client!bga", name = "F", descriptor = "(II)V")
	@Override
	public void F(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY) {
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(II)V")
	@Override
	public void flip(@OriginalArg(0) int x, @OriginalArg(1) int y) throws FlipException {
		this.openGL.swapBuffers();
	}

	@OriginalMember(owner = "client!bga", name = "q", descriptor = "()V")
	@Override
	public void renderLastFrame() {
		if (super.surfaceWidth <= 0 && super.surfaceHeight <= 0) {
			return;
		}
		@Pc(19) int clipLeft = super.cLeft;
		@Pc(22) int clipRight = super.cRight;
		@Pc(25) int clipTop = super.cTop;
		@Pc(28) int clipBottom = super.cBottom;
		this.la();
		OpenGL.glReadBuffer(OpenGLConstants.GL_FRONT);
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK);
		this.setOrthoMatrixMode();
		this.toggleFog(false);
		this.toggleLights(false);
		this.toggleDepthTest(false);
		this.setDepthMaskEnabled(false);
		this.setTextureSampler(null);
		this.setActiveMaterial(false, false, -2);
		this.setColourType(1);
		this.toggleBlending(0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadIdentity();
		OpenGL.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, -1.0D, 1.0D);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadIdentity();
		OpenGL.glRasterPos2i(0, 0);
		OpenGL.glCopyPixels(0, 0, super.surfaceWidth, super.surfaceHeight, OpenGLConstants.GL_COLOR);
		OpenGL.glFlush();
		OpenGL.glReadBuffer(OpenGLConstants.GL_BACK);
		OpenGL.glDrawBuffer(OpenGLConstants.GL_BACK);
		this.KA(clipLeft, clipTop, clipRight, clipBottom);
	}

	@OriginalMember(owner = "client!bga", name = "Q", descriptor = "(I)V")
	@Override
	public void toggleBlending() {
		if (super.enableBlending) {
			OpenGL.glEnable(OpenGLConstants.GL_BLEND);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_BLEND);
		}
	}

	@OriginalMember(owner = "client!bga", name = "l", descriptor = "(II)V")
	public synchronized void destroyProgram(@OriginalArg(1) int programHandle) {
		@Pc(7) Node node = new Node();
		node.key = programHandle;
		this.programsToDelete.addLast(node);
	}

	@OriginalMember(owner = "client!bga", name = "p", descriptor = "(I)V")
	@Override
	public void toggleDepthTest() {
		if (super.enableDepthTest) {
			OpenGL.glEnable(OpenGLConstants.GL_DEPTH_TEST);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_DEPTH_TEST);
		}
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	@Override
	public void setTextureAlphaStage(@OriginalArg(0) TextureCombineSource state, @OriginalArg(1) int sourceIndex) {
		OpenGL.glTexEnvi(8960, sourceIndex + 34184, GlxToolkit.toGlCombineSource(state));
		OpenGL.glTexEnvi(8960, sourceIndex + 34200, 770);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Override
	public void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count, @OriginalArg(2) int x, @OriginalArg(3) int y) throws FlipException {
		this.flip(x, y);
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(ILclient!np;)V")
	@Override
	public void setVertexDeclaration(@OriginalArg(1) VertexDeclaration vertexDeclaration) {
		@Pc(9) VertexDeclarationComponent[] vertexDeclarationElements = ((VertexDeclarationGL) vertexDeclaration).vertexDeclarationElements;
		@Pc(11) int texCoordUnit = 0;
		@Pc(13) boolean hasColour = false;
		@Pc(15) boolean hasNormal = false;
		@Pc(17) boolean hasPosition = false;
		for (@Pc(19) int i = 0; i < vertexDeclarationElements.length; i++) {
			@Pc(25) VertexDeclarationComponent vertexDeclarationElement = vertexDeclarationElements[i];
			@Pc(30) OpenGlArbArrayBuffer arrayBuffer = this.activeArrayBuffers[i];
			@Pc(32) int offset = 0;
			@Pc(36) int stride = arrayBuffer.getStride();
			@Pc(40) long address = arrayBuffer.getBufferAddress();
			arrayBuffer.bind();
			for (@Pc(45) int j = 0; vertexDeclarationElement.getSize() > j; j++) {
				@Pc(52) VertexAttribute action = vertexDeclarationElement.getAction(j);
				if (action == VertexDeclarationAction.POSITION) {
					OpenGL.glVertexPointer(3, OpenGLConstants.GL_FLOAT, stride, (long) offset + address);
					hasPosition = true;
				} else if (VertexDeclarationAction.NORMAL_POINTER_ACTION == action) {
					hasNormal = true;
					OpenGL.glNormalPointer(5126, stride, (long) offset + address);
				} else if (action == VertexDeclarationAction.COLOR) {
					OpenGL.glColorPointer(4, OpenGLConstants.GL_UNSIGNED_BYTE, stride, (long) offset + address);
					hasColour = true;
				} else if (VertexDeclarationAction.aClass69_4 == action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0);
					OpenGL.glTexCoordPointer(1, OpenGLConstants.GL_FLOAT, stride, (long) offset + address);
				} else if (VertexDeclarationAction.TEXCOORD == action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0);
					OpenGL.glTexCoordPointer(2, OpenGLConstants.GL_FLOAT, stride, (long) offset + address);
				} else if (VertexDeclarationAction.aClass69_6 == action) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0);
					OpenGL.glTexCoordPointer(3, OpenGLConstants.GL_FLOAT, stride, (long) offset + address);
				} else if (action == VertexDeclarationAction.aClass69_7) {
					OpenGL.glClientActiveTexture(texCoordUnit++ + OpenGLConstants.GL_TEXTURE0);
					OpenGL.glTexCoordPointer(4, OpenGLConstants.GL_FLOAT, stride, address + (long) offset);
				}
				offset += action.pointerOffsetStep;
			}
		}
		if (this.enableVertexArray != hasPosition) {
			if (hasPosition) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_VERTEX_ARRAY);
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_VERTEX_ARRAY);
			}
			this.enableVertexArray = hasPosition;
		}
		if (this.enableVertexNormal != hasNormal) {
			if (hasNormal) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_NORMAL_ARRAY);
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_NORMAL_ARRAY);
			}
			this.enableVertexNormal = hasNormal;
		}
		if (hasColour != this.enableVertexColor) {
			if (hasColour) {
				OpenGL.glEnableClientState(OpenGLConstants.GL_COLOR_ARRAY);
			} else {
				OpenGL.glDisableClientState(OpenGLConstants.GL_COLOR_ARRAY);
			}
			this.enableVertexColor = hasColour;
		}
		@Pc(258) int unit;
		if (this.textureAllocationPosition < texCoordUnit) {
			for (unit = this.textureAllocationPosition; unit < texCoordUnit; unit++) {
				OpenGL.glClientActiveTexture(unit + OpenGLConstants.GL_TEXTURE0);
				OpenGL.glEnableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY);
			}
			this.textureAllocationPosition = texCoordUnit;
		} else if (this.textureAllocationPosition > texCoordUnit) {
			for (unit = texCoordUnit; unit < this.textureAllocationPosition; unit++) {
				OpenGL.glClientActiveTexture(unit + OpenGLConstants.GL_TEXTURE0);
				OpenGL.glDisableClientState(OpenGLConstants.GL_TEXTURE_COORD_ARRAY);
			}
			this.textureAllocationPosition = texCoordUnit;
			return;
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(IIIID)V")
	@Override
	public void b(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) double depthOffset) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IZ)Lclient!uq;")
	@Override
	public GpuIndexBuffer createIndexBuffer(@OriginalArg(1) boolean drawStream) {
		return new OpenGlArbElementArrayBuffer(this, DataType.aClass26_5, drawStream);
	}

	@OriginalMember(owner = "client!bga", name = "I", descriptor = "()I")
	@Override
	public int I() {
		return this.polygonCount;
	}

	@OriginalMember(owner = "client!bga", name = "j", descriptor = "(Z)V")
	@Override
	public void activateCurrentTexture() {
		OpenGL.glActiveTexture(super.currentTextureSlot + OpenGLConstants.GL_TEXTURE0);
	}

	@OriginalMember(owner = "client!bga", name = "z", descriptor = "(I)V")
	@Override
	public void uploadViewMatrix() {
		this.viewMatrixLoaded = false;
		this.loadModelViewMatrix();
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(FFF)V")
	@Override
	public void setBloomParams(@OriginalArg(0) float whitePoint, @OriginalArg(1) float bloom, @OriginalArg(2) float brightpass) {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	@Override
	public void destroySurface(@OriginalArg(0) Canvas canvas, @OriginalArg(2) Object object) {
		@Pc(6) Long long2 = (Long) object;
		this.openGL.releaseSurface(canvas, long2);
	}

	@OriginalMember(owner = "client!bga", name = "P", descriptor = "(I)V")
	@Override
	public void setTextureEnvironmentColor() {
		GlxToolkit.scratchFloat4[1] = (float) (super.envColor & 0xFF00) / 65280.0F;
		GlxToolkit.scratchFloat4[2] = (float) (super.envColor & 0xFF) / 255.0F;
		GlxToolkit.scratchFloat4[3] = (float) (super.envColor >>> 24) / 255.0F;
		GlxToolkit.scratchFloat4[0] = (float) (super.envColor & 0xFF0000) / 1.671168E7F;
		OpenGL.glTexEnvfv(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_TEXTURE_ENV_COLOR, GlxToolkit.scratchFloat4, 0);
	}

	@OriginalMember(owner = "client!bga", name = "x", descriptor = "(I)V")
	@Override
	public void setLightPosition() {
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT0, OpenGLConstants.GL_POSITION, super.sunPosition, 0);
		OpenGL.glLightfv(OpenGLConstants.GL_LIGHT1, OpenGLConstants.GL_POSITION, super.lightPosition, 0);
	}

	@OriginalMember(owner = "client!bga", name = "GA", descriptor = "(I)V")
	@Override
	public void GA(@OriginalArg(0) int colour) {
		OpenGL.glClearColor((float) (colour & 0xFF0000) / 1.671168E7F, (float) (colour & 0xFF00) / 65280.0F, (float) (colour & 0xFF) / 255.0F, (float) (colour >>> 24) / 255.0F);
		OpenGL.glClear(OpenGLConstants.GL_COLOR_BUFFER_BIT);
	}

	@OriginalMember(owner = "client!bga", name = "p", descriptor = "()V")
	@Override
	public void restoreSurface() {
	}

	@OriginalMember(owner = "client!bga", name = "E", descriptor = "(B)V")
	@Override
	public void toggleAlpha() {
		if (super.enableAlphaTest) {
			OpenGL.glEnable(OpenGLConstants.GL_ALPHA_TEST);
		} else {
			OpenGL.glDisable(OpenGLConstants.GL_ALPHA_TEST);
		}
	}

	@OriginalMember(owner = "client!bga", name = "A", descriptor = "()V")
	@Override
	public void stopBloom() {
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTexture2D(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(3) DataType pixelActionFormat, @OriginalArg(4) PixelFormat pixelFormat) {
		if (this.GLARBTextureNonPowerOfTwo || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new GlTexture2D(this, pixelFormat, pixelActionFormat, width, height);
		} else if (this.GLARBTextureRectangle) {
			return new GlRectangleTexture(this, pixelFormat, pixelActionFormat, width, height);
		} else {
			return new GlTexture2D(this, pixelFormat, pixelActionFormat, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height));
		}
	}

	@OriginalMember(owner = "client!bga", name = "v", descriptor = "()V")
	@Override
	public void compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!bga", name = "w", descriptor = "(I)V")
	@Override
	public void setScissorRect() {
		OpenGL.glScissor(super.viewportOffsetX + super.cLeft, -super.cBottom + (super.viewportOffsetY - -super.surfaceHeight), -super.cLeft + super.cRight, super.cBottom - super.cTop);
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(Z)V")
	@Override
	public void method7250(@OriginalArg(0) boolean enabled) {
	}

	@OriginalMember(owner = "client!bga", name = "S", descriptor = "(I)V")
	@Override
	public void blendDrawBuffer() {
		if (BlendFuncMode.ALPHA_BLENDING == super.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_SRC_ALPHA, OpenGLConstants.GL_ONE_MINUS_SRC_ALPHA);
		} else if (BlendFuncMode.OPAQUE_BLENDING == super.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_ONE, OpenGLConstants.GL_ONE);
		} else if (BlendFuncMode.COLOR_BLENDING == super.blendFuncMode) {
			OpenGL.glBlendFunc(OpenGLConstants.GL_DST_COLOR, OpenGLConstants.GL_ONE);
		}
	}

	@OriginalMember(owner = "client!bga", name = "b", descriptor = "(ZI)Lclient!tc;")
	@Override
	public NativeStream createStream(@OriginalArg(0) boolean drawStream) {
		return new OpenGlArbArrayBuffer(this, drawStream);
	}

	@OriginalMember(owner = "client!bga", name = "i", descriptor = "(Z)F")
	@Override
	public float getPixelOffset() {
		return 0.0F;
	}

	@OriginalMember(owner = "client!bga", name = "na", descriptor = "(IIII)[I")
	@Override
	public int[] na(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		@Pc(10) int[] pixels = new int[height * width];
		for (@Pc(12) int i = 0; i < height; i++) {
			OpenGL.glReadPixelsi(x, super.surfaceHeight - i - y - 1, width, 1, OpenGLConstants.GL_BGRA, this.pixelType, pixels, width * i);
		}
		return pixels;
	}

	@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Lclient!is;)V")
	@Override
	public void swapSurface(@OriginalArg(0) OffscreenSurface surface) {
	}
	@OriginalMember(owner = "client!lt", name = "a", descriptor = "(Ljava/awt/Canvas;Lclient!d;Lclient!in;II)Lclient!ha;")
	public static Toolkit createToolkit(@OriginalArg(0) Canvas canvas, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) Js5ResourceProvider shaders, @OriginalArg(4) int multiSampleCount) {
		if (!D3DToolkitFactory.loadHw3dLibraries()) {
			throw new RuntimeException("");
		} else if (NativeLibraryOwner.loadLibrary("jaggl")) {
			@Pc(26) OpenGL openGL = new OpenGL();
			@Pc(36) long contextHandle = openGL.init(canvas, 8, 8, 8, 24, 0, multiSampleCount);
			if (contextHandle == 0L) {
				throw new RuntimeException("");
			}
			@Pc(60) GlxToolkit glxToolkit = new GlxToolkit(openGL, canvas, contextHandle, textureSource, shaders, multiSampleCount);
			glxToolkit.initializeTables();
			return glxToolkit;
		} else {
			throw new RuntimeException("");
		}
	}
	@OriginalMember(owner = "client!b", name = "a", descriptor = "(BLclient!kf;)I")
	public static int toGlCombineSource(@OriginalArg(1) TextureCombineSource source) {
		if (TextureCombineSource.TEXTURE == source) {
			return 5890;
		} else if (source == TextureCombineSource.PRIMARY_COLOR) {
			return 34167;
		} else if (TextureCombineSource.PREVIOUS == source) {
			return 34168;
		} else if (TextureCombineSource.CONSTANT_COLOR == source) {
			return 34166;
		} else {
			throw new IllegalArgumentException();
		}
	}
	@OriginalMember(owner = "client!mn", name = "i", descriptor = "[I")
	public static final int[] deleteIdBuffer = new int[1000];

	@OriginalMember(owner = "client!kg", name = "i", descriptor = "[F")
	public static final float[] scratchFloat4 = new float[4];
	@OriginalMember(owner = "client!jb", name = "a", descriptor = "(Lclient!bf;I)I")
	public static int toGlTexGenMode(@OriginalArg(0) TexCoordGenMode mode) {
		if (mode == TexCoordGenMode.aClass31_1) {
			return 9216;
		} else if (mode == TexCoordGenMode.aClass31_2) {
			return 34065;
		} else if (TexCoordGenMode.aClass31_4 == mode) {
			return 34066;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
