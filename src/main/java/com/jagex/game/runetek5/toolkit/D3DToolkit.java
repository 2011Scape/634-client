package com.jagex.game.runetek5.toolkit;

import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.sprite.Sprite;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import com.jagex.game.runetek5.graphics.dx.D3DCubeMapTexture;
import com.jagex.game.runetek5.graphics.dx.D3DTexture2D;
import com.jagex.game.runetek5.graphics.dx.D3DTexture3D;
import com.jagex.game.runetek5.graphics.dx.DxIndexBuffer;
import com.jagex.game.runetek5.graphics.dx.DxVertexBuffer;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jaclib.peer.hb;
import jagdx.D3DADAPTER_IDENTIFIER;
import jagdx.D3DCAPS;
import jagdx.D3DDISPLAYMODE;
import jagdx.D3DLIGHT;
import jagdx.D3DPRESENT_PARAMETERS;
import jagdx.GeometryBuffer;
import jagdx.IDirect3D;
import jagdx.IDirect3DBaseTexture;
import jagdx.IDirect3DDevice;
import jagdx.IDirect3DEventQuery;
import jagdx.IDirect3DPixelShader;
import jagdx.IDirect3DSurface;
import jagdx.IDirect3DVertexShader;
import jagdx.PixelBuffer;
import jagdx.fda;
import jagdx.ue;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.D3DSwapChain;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.TexCoordGenMode;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.D3DEnvironmentMappedWaterPass;
import com.jagex.game.runetek5.D3DTexture;
import com.jagex.game.runetek5.D3DTransparentWaterPass;
import com.jagex.game.runetek5.D3DUnderwaterPass;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.CubeTextureSampler;
import com.jagex.game.runetek5.RenderPass;
import com.jagex.game.runetek5.ChatLineCount;
import com.jagex.game.runetek5.D3DEnumTranslator;
import com.jagex.game.runetek5.dxVertexLayout;
import com.jagex.game.runetek5.graphics.texture.TextureState;
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode;
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode;
import com.jagex.game.runetek5.TextureTransformMode;

@OriginalClass("client!bfa")
public final class D3DToolkit extends AbstractNativeToolkit {

	@OriginalMember(owner = "client!bfa", name = "Xg", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader iDirect3DVertexShader;

	@OriginalMember(owner = "client!bfa", name = "Og", descriptor = "I")
	private int failedFlipCount = 0;

	@OriginalMember(owner = "client!bfa", name = "eh", descriptor = "Z")
	private boolean lightsConfigured = false;

	@OriginalMember(owner = "client!bfa", name = "Rg", descriptor = "Lclient!jaclib/peer/hb;")
	public final hb peerTracker;

	@OriginalMember(owner = "client!bfa", name = "dh", descriptor = "Lclient!jagdx/D3DPRESENT_PARAMETERS;")
	private final D3DPRESENT_PARAMETERS d3DPRESENT_PARAMETERS;

	@OriginalMember(owner = "client!bfa", name = "kh", descriptor = "I")
	private final int deviceType;

	@OriginalMember(owner = "client!bfa", name = "Wg", descriptor = "Lclient!jagdx/IDirect3D;")
	private final IDirect3D iDirect3D;

	@OriginalMember(owner = "client!bfa", name = "ch", descriptor = "I")
	private final int adapter;

	@OriginalMember(owner = "client!bfa", name = "Qg", descriptor = "Lclient!je;")
	private D3DSwapChain currentSwapChain;

	@OriginalMember(owner = "client!bfa", name = "nh", descriptor = "Lclient!jagdx/IDirect3DDevice;")
	public final IDirect3DDevice iDirect3DDevice;

	@OriginalMember(owner = "client!bfa", name = "Ug", descriptor = "Lclient!jagdx/D3DCAPS;")
	public final D3DCAPS d3DCAPS;

	@OriginalMember(owner = "client!bfa", name = "Sg", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private final D3DLIGHT sunLight;

	@OriginalMember(owner = "client!bfa", name = "Zg", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private final D3DLIGHT antiSunLight;

	@OriginalMember(owner = "client!bfa", name = "lh", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private final D3DLIGHT scenePointLight;

	@OriginalMember(owner = "client!bfa", name = "gh", descriptor = "Lclient!jagdx/PixelBuffer;")
	public final PixelBuffer pixelBuffer;

	@OriginalMember(owner = "client!bfa", name = "oh", descriptor = "Lclient!jagdx/GeometryBuffer;")
	public final GeometryBuffer geometryBuffer;

	@OriginalMember(owner = "client!bfa", name = "bh", descriptor = "Z")
	public final boolean mipmapSupported;

	@OriginalMember(owner = "client!bfa", name = "Vg", descriptor = "Z")
	public final boolean nonPowerOfTwoSupported;

	@OriginalMember(owner = "client!bfa", name = "fh", descriptor = "Z")
	public final boolean mipmappedCubeMapSupported;

	@OriginalMember(owner = "client!bfa", name = "jh", descriptor = "[Z")
	private final boolean[] textureRepeatV;

	@OriginalMember(owner = "client!bfa", name = "hh", descriptor = "[Z")
	private final boolean[] textureMipmapped;

	@OriginalMember(owner = "client!bfa", name = "Ng", descriptor = "[Z")
	private final boolean[] textureBound;

	@OriginalMember(owner = "client!bfa", name = "Yg", descriptor = "[Lclient!ria;")
	private final FilterMode[] textureFilters;

	@OriginalMember(owner = "client!bfa", name = "ih", descriptor = "[I")
	private final int[] textureTransformFlags;

	@OriginalMember(owner = "client!bfa", name = "Pg", descriptor = "[Z")
	private final boolean[] textureRepeatU;

	@OriginalMember(owner = "client!bfa", name = "createToolkit", descriptor = "(Ljava/awt/Canvas;Lclient!d;Lclient!in;Ljava/lang/Integer;)Lclient!ha;")
	public static Toolkit createToolkit(@OriginalArg(0) Canvas canvas, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) Js5ResourceProvider shaderResource, @OriginalArg(3) Integer integer) {
		@Pc(1) D3DToolkit d3DToolkit = null;
		try {
			@Pc(3) byte adapter = 0;
			@Pc(5) byte deviceType = 1;
			@Pc(9) hb peerTracker = new hb();
			@Pc(13) IDirect3D iDirect3D = IDirect3D.a(-2147483616, peerTracker);
			@Pc(18) D3DCAPS d3DCAPS = iDirect3D.b(adapter, deviceType);
			if ((d3DCAPS.RasterCaps & 0x1000000) == 0) {
				throw new RuntimeException("");
			} else if (d3DCAPS.MaxSimultaneousTextures < 2) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.TextureOpCaps & 0x2) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.TextureOpCaps & 0x8) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.TextureOpCaps & 0x40) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.TextureOpCaps & 0x200) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.TextureOpCaps & 0x2000000) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.DestBlendCaps & 0x10 & d3DCAPS.SrcBlendCaps) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.DestBlendCaps & d3DCAPS.SrcBlendCaps & 0x20) == 0) {
				throw new RuntimeException("");
			} else if ((d3DCAPS.SrcBlendCaps & d3DCAPS.DestBlendCaps & 0x2) == 0) {
				throw new RuntimeException("");
			} else if (d3DCAPS.MaxActiveLights > 0 && d3DCAPS.MaxActiveLights < 2) {
				throw new RuntimeException("");
			} else if (d3DCAPS.MaxStreams >= 5) {
				@Pc(181) D3DPRESENT_PARAMETERS d3DPRESENT_PARAMETERS = new D3DPRESENT_PARAMETERS(canvas);
				if (!D3DEnumTranslator.selectPresentFormats(d3DPRESENT_PARAMETERS, adapter, iDirect3D, deviceType, integer)) {
					throw new RuntimeException("");
				}
				d3DPRESENT_PARAMETERS.PresentationInterval = Integer.MIN_VALUE;
				d3DPRESENT_PARAMETERS.EnableAutoDepthStencil = true;
				d3DPRESENT_PARAMETERS.Windowed = true;
				if (ClientFeatures.scaled()) {
					// Not part of the original client: with client.scale the back buffer is the game's
					// size, and Present stretches it over the larger window.
					Dimension gameSize = canvas.getSize();
					d3DPRESENT_PARAMETERS.BackBufferWidth = gameSize.width;
					d3DPRESENT_PARAMETERS.BackBufferHeight = gameSize.height;
				}
				@Pc(206) int behaviourFlags = 2;
				if ((d3DCAPS.DevCaps & 0x100000) != 0) {
					behaviourFlags |= 0x10;
				}
				@Pc(230) IDirect3DDevice iDirect3DDevice;
				try {
					iDirect3DDevice = iDirect3D.a(adapter, deviceType, canvas, behaviourFlags | 0x40, d3DPRESENT_PARAMETERS);
				} catch (@Pc(232) fda ex) {
					iDirect3DDevice = iDirect3D.a(adapter, deviceType, canvas, behaviourFlags | 0x20, d3DPRESENT_PARAMETERS);
				}
				@Pc(251) D3DSwapChain swapChain = new D3DSwapChain(iDirect3DDevice.b(0), iDirect3DDevice.c());
				d3DToolkit = new D3DToolkit(adapter, deviceType, canvas, peerTracker, iDirect3D, iDirect3DDevice, swapChain, d3DPRESENT_PARAMETERS, d3DCAPS, textureSource, shaderResource, integer);
				d3DToolkit.initializeTables();
				return d3DToolkit;
			} else {
				throw new RuntimeException("");
			}
		} catch (@Pc(274) RuntimeException ex) {
			if (d3DToolkit != null) {
				d3DToolkit.stop();
			}
			throw ex;
		}
	}

	@OriginalMember(owner = "client!bfa", name = "<init>", descriptor = "(IILjava/awt/Canvas;Lclient!jaclib/peer/hb;Lclient!jagdx/IDirect3D;Lclient!jagdx/IDirect3DDevice;Lclient!je;Lclient!jagdx/D3DPRESENT_PARAMETERS;Lclient!jagdx/D3DCAPS;Lclient!d;Lclient!in;I)V")
	public D3DToolkit(@OriginalArg(0) int adapter, @OriginalArg(1) int deviceType, @OriginalArg(2) Canvas canvas, @OriginalArg(3) hb peerTracker, @OriginalArg(4) IDirect3D iDirect3D, @OriginalArg(5) IDirect3DDevice iDirect3DDevice, @OriginalArg(6) D3DSwapChain swapChain, @OriginalArg(7) D3DPRESENT_PARAMETERS d3DPRESENT_PARAMETERS, @OriginalArg(8) D3DCAPS d3DCAPS, @OriginalArg(9) MaterialProvider textureSource, @OriginalArg(10) Js5ResourceProvider shaderResource, @OriginalArg(11) int multiSampleCount) {
		super(canvas, swapChain, textureSource, shaderResource, multiSampleCount, 0);
		try {
			this.peerTracker = peerTracker;
			this.d3DPRESENT_PARAMETERS = d3DPRESENT_PARAMETERS;
			this.deviceType = deviceType;
			this.iDirect3D = iDirect3D;
			this.adapter = adapter;
			this.currentSwapChain = swapChain;
			this.iDirect3DDevice = iDirect3DDevice;
			this.d3DCAPS = d3DCAPS;
			this.sunLight = new D3DLIGHT(this.peerTracker);
			this.antiSunLight = new D3DLIGHT(this.peerTracker);
			this.scenePointLight = new D3DLIGHT(this.peerTracker);
			this.pixelBuffer = new PixelBuffer(this.peerTracker);
			this.geometryBuffer = new GeometryBuffer(this.peerTracker);
			new GeometryBuffer(this.peerTracker);
			this.cubeMapSupported = (this.d3DCAPS.TextureCaps & 0x800) != 0;
			this.mipmapSupported = (this.d3DCAPS.TextureCaps & 0x4000) != 0;
			this.nonPowerOfTwoSupported = (this.d3DCAPS.TextureCaps & 0x2) == 0;
			this.mipmappedCubeMapSupported = (this.d3DCAPS.TextureCaps & 0x10000) != 0;
			this.maxActiveLights = this.d3DCAPS.MaxActiveLights <= 0 ? 8 : this.d3DCAPS.MaxActiveLights;
			this.SUPPORTS_TEXTURE3D = (this.d3DCAPS.TextureCaps & 0x2000) != 0;
			this.maxConcurrentTextures = this.d3DCAPS.MaxSimultaneousTextures;
			this.GLARBMultisample = this.multiSampleCount > 0 || this.iDirect3D.CheckDeviceMultiSampleType(this.adapter, this.deviceType, this.d3DPRESENT_PARAMETERS.BackBufferFormat, true, 2) == 0;
			this.textureRepeatV = new boolean[this.maxConcurrentTextures];
			this.textureMipmapped = new boolean[this.maxConcurrentTextures];
			this.textureBound = new boolean[this.maxConcurrentTextures];
			this.textureFilters = new FilterMode[this.maxConcurrentTextures];
			this.textureTransformFlags = new int[this.maxConcurrentTextures];
			this.textureRepeatU = new boolean[this.maxConcurrentTextures];
			this.iDirect3DDevice.BeginScene();
		} catch (@Pc(218) Throwable ex) {
			ex.printStackTrace();
			this.free();
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!bfa", name = "H", descriptor = "(I)V")
	@Override
	public void setFogColorAndIntensity() {
		this.sunLight.SetAmbient(this.baseLightColorRed * this.intensity, this.intensity * this.baseLightColorGreen, this.baseLightColorBlue * this.intensity, (float) 0);
		this.lightsConfigured = false;
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	@Override
	public void setTextureAlphaStage(@OriginalArg(0) TextureCombineSource state, @OriginalArg(1) int sourceIndex) {
		@Pc(19) byte argState;
		if (sourceIndex == 1) {
			argState = 6;
		} else if (sourceIndex == 2) {
			argState = 27;
		} else {
			argState = 5;
		}
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, argState, D3DEnumTranslator.toD3DTextureArg(state) | 0x0);
	}

	@OriginalMember(owner = "client!bfa", name = "na", descriptor = "(IIII)[I")
	@Override
	public int[] na(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		@Pc(1) int[] pixels = null;
		@Pc(6) IDirect3DSurface iDirect3DSurface = this.iDirect3DDevice.c(0);
		@Pc(16) IDirect3DSurface iDirect3DSurface2 = this.iDirect3DDevice.a(width, height, 21, 0, 0, true);
		if (ue.a(this.iDirect3DDevice.StretchRect(iDirect3DSurface, x, y, width, height, iDirect3DSurface2, 0, 0, width, height, 0), false) && ue.a(iDirect3DSurface2.LockRect(0, 0, width, height, 16, this.pixelBuffer), false)) {
			pixels = new int[height * width];
			@Pc(56) int rowPitch = this.pixelBuffer.getRowPitch();
			if (width * 4 == rowPitch) {
				this.pixelBuffer.b(pixels, 0, 0, width * height);
			} else {
				for (@Pc(67) int i = 0; i < height; i++) {
					this.pixelBuffer.b(pixels, i * width, i * rowPitch, width);
				}
			}
			iDirect3DSurface2.UnlockRect();
		}
		iDirect3DSurface.a(9275);
		iDirect3DSurface2.a(9275);
		return pixels;
	}

	@OriginalMember(owner = "client!bfa", name = "j", descriptor = "(Z)V")
	@Override
	public void activateCurrentTexture() {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!np;)V")
	@Override
	public void setVertexDeclaration(@OriginalArg(1) VertexDeclaration vertexDeclaration) {
		@Pc(2) dxVertexLayout layout = (dxVertexLayout) vertexDeclaration;
		this.iDirect3DDevice.SetVertexDeclaration(layout.vertexDeclaration);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!kt;)V")
	public void bindTexture2D(@OriginalArg(1) D3DTexture2D texture) {
		this.bindTexture(texture);
		if (this.textureRepeatU[this.currentTextureSlot] != texture.repeatU) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 1, texture.repeatU ? 1 : 3);
			this.textureRepeatU[this.currentTextureSlot] = texture.repeatU;
		}
		if (this.textureRepeatV[this.currentTextureSlot] != texture.repeatV) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 2, texture.repeatV ? 1 : 3);
			this.textureRepeatV[this.currentTextureSlot] = texture.repeatV;
		}
	}

	@OriginalMember(owner = "client!bfa", name = "j", descriptor = "(B)V")
	@Override
	public void setDepthRenderState() {
		this.iDirect3DDevice.a(14, this.depthBufferingEnabled && this.depthMaskEnabled);
	}

	@OriginalMember(owner = "client!bfa", name = "v", descriptor = "()V")
	@Override
	public void compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	@Override
	public Texture3DSampler create3DTexture(@OriginalArg(2) PixelFormat format, @OriginalArg(5) byte[] data) {
		return new D3DTexture3D(this, format, 128, 128, 16, data);
	}

	@OriginalMember(owner = "client!bfa", name = "g", descriptor = "(B)V")
	@Override
	public void toggleFog() {
		this.iDirect3DDevice.a(28, this.fogSupported && this.fogEnabled && this.fogDistance >= 0);
	}

	@OriginalMember(owner = "client!bfa", name = "t", descriptor = "(B)V")
	@Override
	public void uploadModelMatrix() {
		if (this.isModelMatrixIdentity) {
			D3DEnumTranslator.MATRIX_BUFFER[1] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[2] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[15] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[3] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[9] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[6] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[0] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[7] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[11] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[13] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[10] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[8] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[12] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[5] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[14] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[4] = 0.0F;
		} else {
			this.modelMatrix43.copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER);
		}
		this.iDirect3DDevice.SetTransform(256, D3DEnumTranslator.MATRIX_BUFFER);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(BZ)V")
	@Override
	public void toggleMultisampling(@OriginalArg(1) boolean enabled) {
		this.iDirect3DDevice.a(161, enabled);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	@Override
	public void setTextureColourSource(@OriginalArg(0) boolean useAlpha, @OriginalArg(2) int sourceIndex, @OriginalArg(3) TextureCombineSource state, @OriginalArg(4) boolean invert) {
		@Pc(12) byte argState;
		if (sourceIndex == 1) {
			argState = 3;
		} else if (sourceIndex == 2) {
			argState = 26;
		} else {
			argState = 2;
		}
		@Pc(20) int argModifiers = 0;
		if (useAlpha) {
			argModifiers = 32;
		}
		if (invert) {
			argModifiers |= 0x10;
		}
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, argState, argModifiers | D3DEnumTranslator.toD3DTextureArg(state));
	}

	@OriginalMember(owner = "client!bfa", name = "x", descriptor = "(I)V")
	@Override
	public void setLightPosition() {
		this.sunLight.SetDirection(-this.sunPosition[0], -this.sunPosition[1], -this.sunPosition[2]);
		this.antiSunLight.SetDirection(-this.lightPosition[0], -this.lightPosition[1], -this.lightPosition[2]);
		this.lightsConfigured = false;
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(II)Lclient!vm;")
	@Override
	public DepthBuffer createDepthBuffer(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(BI)V")
	@Override
	public void setTexCoordIndex(@OriginalArg(1) int texCoordIndex) {
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 11, texCoordIndex);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	@Override
	public void destroySurface(@OriginalArg(0) Canvas canvas, @OriginalArg(2) Object object) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(BLclient!cg;)V")
	public void bindTexture(@OriginalArg(1) D3DTexture texture) {
		this.iDirect3DDevice.SetTexture(this.currentTextureSlot, texture.getTexture());
		if (this.textureFilters[this.currentTextureSlot] != texture.filter) {
			@Pc(61) int filterMode = D3DEnumTranslator.toD3DFilter(texture.filter);
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 6, filterMode);
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 5, filterMode);
			this.textureFilters[this.currentTextureSlot] = texture.filter;
			if (this.textureMipmapped[this.currentTextureSlot] != texture.mipmapped) {
				this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 7, texture.mipmapped ? D3DEnumTranslator.toD3DFilter(texture.filter) : 0);
				this.textureMipmapped[this.currentTextureSlot] = texture.mipmapped;
			}
		} else if (this.textureMipmapped[this.currentTextureSlot] != texture.mipmapped) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 7, texture.mipmapped ? D3DEnumTranslator.toD3DFilter(texture.filter) : 0);
			this.textureMipmapped[this.currentTextureSlot] = texture.mipmapped;
		}
		if (!this.textureBound[this.currentTextureSlot]) {
			this.textureBound[this.currentTextureSlot] = true;
			this.setTextureAlphaStage();
			this.setTextureColorStage();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(II)Lclient!he;")
	@Override
	public Sprite method7268(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return null;
	}

	@OriginalMember(owner = "client!bfa", name = "c", descriptor = "(Z)V")
	@Override
	public void uploadProjectionMatrix() {
		this.iDirect3DDevice.SetTransform(3, this.activeProjectionSource);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!jagdx/IDirect3DVertexShader;B)V")
	public void setVertexShader(@OriginalArg(0) IDirect3DVertexShader vertexShader) {
		this.iDirect3DVertexShader = vertexShader;
		this.iDirect3DDevice.SetVertexShader(vertexShader);
		this.setTextureMatrix();
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	@Override
	public void drawIndexedPrimitive(@OriginalArg(0) PrimitiveType drawMode, @OriginalArg(1) int primitiveCount, @OriginalArg(2) GpuIndexBuffer indexBuffer, @OriginalArg(3) int minVertexIndex, @OriginalArg(4) int startIndex, @OriginalArg(5) int vertexCount) {
		this.iDirect3DDevice.SetIndices(((DxIndexBuffer) indexBuffer).indexBuffer);
		this.iDirect3DDevice.DrawIndexedPrimitive(D3DEnumTranslator.toD3DPrimitiveType(drawMode), 0, minVertexIndex, vertexCount, startIndex, primitiveCount);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	@Override
	public OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite surface, @OriginalArg(1) DepthBuffer buffer) {
		return null;
	}

	@OriginalMember(owner = "client!bfa", name = "p", descriptor = "()V")
	@Override
	public void restoreSurface() {
	}

	@OriginalMember(owner = "client!bfa", name = "w", descriptor = "(I)V")
	@Override
	public void setScissorRect() {
		this.iDirect3DDevice.SetScissorRect(this.viewportOffsetX + this.cLeft, this.viewportOffsetY - -this.cTop, this.cRight, this.cBottom);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Override
	public void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count, @OriginalArg(2) int x, @OriginalArg(3) int y) throws FlipException {
		this.flip(x, y);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	@Override
	public boolean supports3DTextureFormat(@OriginalArg(1) PixelFormat format, @OriginalArg(2) DataType type) {
		@Pc(3) D3DDISPLAYMODE d3DDISPLAYMODE = new D3DDISPLAYMODE();
		return ue.a(this.iDirect3D.a(this.adapter, d3DDISPLAYMODE), false) && ue.a(this.iDirect3D.CheckDeviceFormat(this.adapter, this.deviceType, d3DDISPLAYMODE.Format, 0, 4, D3DEnumTranslator.toD3DFormat(type, format)), false);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	@Override
	public void setSurface(@OriginalArg(0) Object object, @OriginalArg(2) Canvas canvas) {
		this.currentSwapChain = (D3DSwapChain) object;
	}

	@OriginalMember(owner = "client!bfa", name = "h", descriptor = "(Z)V")
	@Override
	public void setSunParameters() {
		@Pc(7) float sunIntensity = this.sunEnabled ? this.sunColorIntensity : 0.0F;
		@Pc(16) float antiSunIntensity = this.sunEnabled ? -this.antiSunColorIntensity : 0.0F;
		this.sunLight.SetDiffuse(sunIntensity * this.baseLightColorRed, this.baseLightColorGreen * sunIntensity, sunIntensity * this.baseLightColorBlue, 0.0F);
		this.antiSunLight.SetDiffuse(this.baseLightColorRed * antiSunIntensity, this.baseLightColorGreen * antiSunIntensity, this.baseLightColorBlue * antiSunIntensity, 0.0F);
		this.lightsConfigured = false;
	}

	@OriginalMember(owner = "client!bfa", name = "m", descriptor = "()Z")
	@Override
	public boolean bloom() {
		return false;
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "()V")
	@Override
	public void finish() {
		@Pc(3) IDirect3DEventQuery iDirect3DEventQuery = this.iDirect3DDevice.b();
		if (ue.a(iDirect3DEventQuery.Issue(), false)) {
			while (true) {
				@Pc(12) int status = iDirect3DEventQuery.IsSignaled();
				if (status != 1) {
					break;
				}
				Thread.yield();
			}
		}
		iDirect3DEventQuery.a(9275);
	}

	@OriginalMember(owner = "client!bfa", name = "F", descriptor = "(II)V")
	@Override
	public void F(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY) {
	}

	@OriginalMember(owner = "client!bfa", name = "P", descriptor = "(I)V")
	@Override
	public void setTextureEnvironmentColor() {
		this.iDirect3DDevice.SetRenderState(60, this.envColor);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	@Override
	public EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler environmentSampler, @OriginalArg(1) EnvironmentSampler environmentSampler2, @OriginalArg(2) float alpha, @OriginalArg(3) EnvironmentSampler environmentSampler3) {
		return alpha < 0.5F ? environmentSampler : environmentSampler2;
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(Z)V")
	@Override
	public void method7250(@OriginalArg(0) boolean enabled) {
	}

	@OriginalMember(owner = "client!bfa", name = "e", descriptor = "(B)V")
	@Override
	public void updateSceneLights() {
		@Pc(9) int lightIndex;
		for (lightIndex = 0; lightIndex < this.lastSceneLightCount; lightIndex++) {
			@Pc(22) PointLight pointLight = this.lightSources[lightIndex];
			@Pc(26) int deviceLightIndex = lightIndex + 2;
			@Pc(30) int range = pointLight.getColour();
			@Pc(36) float intensityScale = pointLight.getIntensity() / 255.0F;
			this.scenePointLight.SetPosition((float) pointLight.getX(), (float) pointLight.getY(), (float) pointLight.getZ());
			this.scenePointLight.SetDiffuse(intensityScale * (float) (range >> 16 & 0xFF), (float) (range >> 8 & 0xFF) * intensityScale, intensityScale * (float) (range & 0xFF), 0.0F);
			this.scenePointLight.SetAttenuation(0.0F, 0.0F, 1.0F / (float) (pointLight.getRadius() * pointLight.getRadius()));
			this.scenePointLight.SetRange((float) pointLight.getRadius());
			this.iDirect3DDevice.SetLight(deviceLightIndex, this.scenePointLight);
			this.iDirect3DDevice.LightEnable(deviceLightIndex, true);
		}
		while (this.currentSceneLightCount > lightIndex) {
			this.iDirect3DDevice.LightEnable(lightIndex + 2, false);
			lightIndex++;
		}
		super.updateSceneLights();
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(B)V")
	@Override
	public void unbindCurrentTexture() {
		if (this.textureBound[this.currentTextureSlot]) {
			this.textureBound[this.currentTextureSlot] = false;
			this.iDirect3DDevice.SetTexture(this.currentTextureSlot, null);
			this.setTextureAlphaStage();
			this.setTextureColorStage();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "z", descriptor = "(I)V")
	@Override
	public void uploadViewMatrix() {
		if (this.matrixMode.isSceneMode()) {
			this.flippedViewMatrix.copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER);
		} else {
			D3DEnumTranslator.MATRIX_BUFFER[1] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[7] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[12] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[0] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[5] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[13] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[2] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[11] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[9] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[10] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[14] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[8] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[4] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[15] = 1.0F;
			D3DEnumTranslator.MATRIX_BUFFER[6] = 0.0F;
			D3DEnumTranslator.MATRIX_BUFFER[3] = 0.0F;
		}
		this.iDirect3DDevice.SetTransform(2, D3DEnumTranslator.MATRIX_BUFFER);
	}

	@OriginalMember(owner = "client!bfa", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		this.peerTracker.a((byte) 13);
		super.stop();
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IZ)Lclient!uq;")
	@Override
	public GpuIndexBuffer createIndexBuffer(@OriginalArg(1) boolean dynamic) {
		return new DxIndexBuffer(this, DataType.aClass26_5, dynamic);
	}

	@OriginalMember(owner = "client!bfa", name = "GA", descriptor = "(I)V")
	@Override
	public void GA(@OriginalArg(0) int colour) {
		this.iDirect3DDevice.Clear(1, colour, 0.0F, 0);
	}

	@OriginalMember(owner = "client!bfa", name = "I", descriptor = "()I")
	@Override
	public int I() {
		return 0;
	}

	@OriginalMember(owner = "client!bfa", name = "o", descriptor = "(I)V")
	@Override
	public void configureFog() {
		this.fogEnd = this.camFar - this.fogOffset;
		this.fogStart = (float) -this.fogDistance + this.fogEnd;
		if (this.fogStart < (float) this.camNear) {
			this.fogStart = this.camNear;
		}
		this.iDirect3DDevice.a(36, this.fogStart);
		this.iDirect3DDevice.a(37, this.fogEnd);
		this.iDirect3DDevice.SetRenderState(34, this.fogColorRgb);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTexture(@OriginalArg(0) PixelFormat format, @OriginalArg(1) int width, @OriginalArg(3) float[] pixels, @OriginalArg(4) boolean mipmapped, @OriginalArg(7) int height) {
		return null;
	}

	@OriginalMember(owner = "client!bfa", name = "r", descriptor = "(B)V")
	@Override
	public void enableLights() {
		if (this.lightsConfigured) {
			return;
		}
		this.iDirect3DDevice.LightEnable(0, false);
		this.iDirect3DDevice.LightEnable(1, false);
		this.iDirect3DDevice.SetLight(0, this.sunLight);
		this.iDirect3DDevice.SetLight(1, this.antiSunLight);
		this.iDirect3DDevice.LightEnable(0, true);
		this.iDirect3DDevice.LightEnable(1, true);
		this.lightsConfigured = true;
	}

	@OriginalMember(owner = "client!bfa", name = "o", descriptor = "()Z")
	@Override
	public boolean hasBloom() {
		return false;
	}

	@OriginalMember(owner = "client!bfa", name = "D", descriptor = "(B)V")
	@Override
	public void setViewport() {
		this.iDirect3DDevice.SetViewport(this.viewportOffsetX, this.viewportOffsetY, this.surfaceWidth, this.surfaceHeight, 0.0F, 1.0F);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!nc;)V")
	public void bindTexture3D(@OriginalArg(1) D3DTexture3D texture) {
		this.bindTexture(texture);
		if (!this.textureRepeatU[this.currentTextureSlot]) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 1, 1);
			this.textureRepeatU[this.currentTextureSlot] = true;
		}
		if (!this.textureRepeatV[this.currentTextureSlot]) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 2, 1);
			this.textureRepeatV[this.currentTextureSlot] = true;
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IILclient!hj;Z)V")
	@Override
	public void drawPrimitive(@OriginalArg(0) int primitiveCount, @OriginalArg(1) int first, @OriginalArg(2) PrimitiveType drawMode) {
		this.iDirect3DDevice.DrawPrimitive(D3DEnumTranslator.toD3DPrimitiveType(drawMode), first, primitiveCount);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IIII)V")
	@Override
	public void method7261(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(Z)V")
	@Override
	public void toggleSun() {
		this.setSunParameters();
		this.enableLights();
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "()V")
	@Override
	public void renderLastFrame() {
	}

	@OriginalMember(owner = "client!bfa", name = "Q", descriptor = "(I)V")
	@Override
	public void toggleBlending() {
		this.iDirect3DDevice.a(27, this.enableBlending);
	}

	@OriginalMember(owner = "client!bfa", name = "p", descriptor = "(I)V")
	@Override
	public void toggleDepthTest() {
		this.iDirect3DDevice.a(7, this.enableDepthTest);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTextureSampler(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(4) byte[] pixels, @OriginalArg(5) PixelFormat format, @OriginalArg(7) boolean mipmapped) {
		return new D3DTexture2D(this, format, width, height, mipmapped, pixels, 0, 0);
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(I)V")
	@Override
	public synchronized void tick(@OriginalArg(0) int time) {
		this.peerTracker.b((byte) -82);
		super.tick(time);
	}

	@OriginalMember(owner = "client!bfa", name = "i", descriptor = "(Z)F")
	@Override
	public float getPixelOffset() {
		return -0.5F;
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	@Override
	public Object createSurface(@OriginalArg(1) Canvas canvas) {
		return null;
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "(Z)Z")
	private boolean resetDevice() {
		@Pc(3) int cooperativeLevel = this.iDirect3DDevice.TestCooperativeLevel();
		if (cooperativeLevel == 0 || cooperativeLevel == -2005530519) {
			@Pc(23) D3DSwapChain primarySwapChain = (D3DSwapChain) this.primarySurface;
			this.deallocate();
			primarySwapChain.release();
			this.d3DPRESENT_PARAMETERS.BackBufferHeight = 0;
			this.d3DPRESENT_PARAMETERS.BackBufferWidth = 0;
			if (ClientFeatures.scaled()) {
				// Not part of the original client: with client.scale the back buffer is the game's
				// size, and Present stretches it over the larger window.
				this.d3DPRESENT_PARAMETERS.BackBufferWidth = this.surfaceWidth;
				this.d3DPRESENT_PARAMETERS.BackBufferHeight = this.surfaceHeight;
			}
			if (D3DEnumTranslator.selectPresentFormats(this.d3DPRESENT_PARAMETERS, this.adapter, this.iDirect3D, this.deviceType, this.multiSampleCount)) {
				@Pc(56) int result = this.iDirect3DDevice.Reset(this.d3DPRESENT_PARAMETERS);
				if (ue.a(result, false)) {
					primarySwapChain.setSwapChain(this.iDirect3DDevice.b(0), this.iDirect3DDevice.c());
					this.initializeRenderers();
					this.setupGraphics();
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!bfa", name = "F", descriptor = "(I)V")
	@Override
	public void setTextureColorStage() {
		@Pc(15) int colourOp = this.textureBound[this.currentTextureSlot] ? D3DEnumTranslator.toD3DTextureOp(this.textureRgbCombineModes[this.currentTextureSlot]) : 1;
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 1, colourOp);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	@Override
	public GpuTexture createTextureSampler(@OriginalArg(0) int width, @OriginalArg(1) int[] pixels, @OriginalArg(2) boolean mipmapped, @OriginalArg(4) int offset, @OriginalArg(5) int rowLength, @OriginalArg(6) int height) {
		return new D3DTexture2D(this, width, height, mipmapped, pixels, 0, rowLength);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!is;)V")
	@Override
	public void swapSurface(@OriginalArg(0) OffscreenSurface surface) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "([[IZII)Lclient!nj;")
	@Override
	public CubeTextureSampler generateNativeCubeTexture(@OriginalArg(0) int[][] pixels, @OriginalArg(1) boolean mipmapped, @OriginalArg(3) int size) {
		return new D3DCubeMapTexture(this, size, mipmapped, pixels);
	}

	@OriginalMember(owner = "client!bfa", name = "E", descriptor = "(B)V")
	@Override
	public void toggleAlpha() {
		this.iDirect3DDevice.a(15, this.enableAlphaTest);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	@Override
	public VertexDeclaration createVertexDeclaration(@OriginalArg(1) VertexDeclarationComponent[] vertexDeclarationElements) {
		return new dxVertexLayout(this, vertexDeclarationElements);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!jagdx/IDirect3DPixelShader;)V")
	public void setPixelShader(@OriginalArg(1) IDirect3DPixelShader pixelShader) {
		this.iDirect3DDevice.SetPixelShader(pixelShader);
	}

	@OriginalMember(owner = "client!bfa", name = "A", descriptor = "()V")
	@Override
	public void stopBloom() {
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(ZI)Lclient!tc;")
	@Override
	public NativeStream createStream(@OriginalArg(0) boolean dynamic) {
		return new DxVertexBuffer(this, dynamic);
	}

	@OriginalMember(owner = "client!bfa", name = "G", descriptor = "(I)V")
	@Override
	public void toggleScissorTest() {
		this.iDirect3DDevice.a(174, this.boundsCalculated);
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(IIIID)V")
	@Override
	public void b(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) double depthOffset) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IB)Lclient!fj;")
	@Override
	public RenderPass createRenderPass(@OriginalArg(0) int effect) {
		if (effect == 3) {
			return new D3DUnderwaterPass(this, this.shaderResource);
		} else if (effect == 4) {
			return new D3DTransparentWaterPass(this, this.shaderResource, this.waterTexture);
		} else if (effect == 8) {
			return new D3DEnvironmentMappedWaterPass(this, this.shaderResource, this.waterTexture);
		} else {
			return super.createRenderPass(effect);
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(FFF)V")
	@Override
	public void setBloomParams(@OriginalArg(0) float whitePoint, @OriginalArg(1) float bloom, @OriginalArg(2) float brightpass) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!bf;I)V")
	@Override
	public void setTexCoordGeneration(@OriginalArg(0) TexCoordGenMode mode) {
		@Pc(1) int texCoordFlags = 0;
		if (TexCoordGenMode.aClass31_2 == mode) {
			texCoordFlags = 65536;
		} else if (TexCoordGenMode.aClass31_1 == mode) {
			texCoordFlags = 131072;
		} else if (mode == TexCoordGenMode.aClass31_4) {
			texCoordFlags = 196608;
		}
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 11, this.currentTextureSlot | texCoordFlags);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	@Override
	public boolean supportsTextureFormat(@OriginalArg(0) DataType type, @OriginalArg(1) PixelFormat format) {
		@Pc(3) D3DDISPLAYMODE d3DDISPLAYMODE = new D3DDISPLAYMODE();
		return ue.a(this.iDirect3D.a(this.adapter, d3DDISPLAYMODE), false) && ue.a(this.iDirect3D.CheckDeviceFormat(this.adapter, this.deviceType, d3DDISPLAYMODE.Format, 0, 3, D3DEnumTranslator.toD3DFormat(type, format)), false);
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "(B)V")
	@Override
	public void setupGraphics() {
		for (@Pc(1) int i = 0; i < this.maxConcurrentTextures; i++) {
			this.iDirect3DDevice.SetSamplerState(i, 7, 0);
			this.iDirect3DDevice.SetSamplerState(i, 6, 2);
			this.iDirect3DDevice.SetSamplerState(i, 5, 2);
			this.iDirect3DDevice.SetSamplerState(i, 1, 1);
			this.iDirect3DDevice.SetSamplerState(i, 2, 1);
			this.textureFilters[i] = SampleMode.LINEAR;
			this.textureRepeatU[i] = this.textureRepeatV[i] = true;
			this.textureMipmapped[i] = false;
			this.textureTransformFlags[i] = 0;
		}
		this.iDirect3DDevice.SetTextureStageState(0, 6, 1);
		this.iDirect3DDevice.SetRenderState(9, 2);
		this.iDirect3DDevice.SetRenderState(23, 4);
		this.iDirect3DDevice.SetRenderState(25, 5);
		this.iDirect3DDevice.SetRenderState(24, 0);
		this.iDirect3DDevice.SetRenderState(22, 2);
		this.iDirect3DDevice.SetRenderState(147, 1);
		this.iDirect3DDevice.SetRenderState(145, 1);
		this.iDirect3DDevice.a(38, 0.95F);
		this.iDirect3DDevice.SetRenderState(140, 3);
		this.sunLight.SetType(3);
		this.antiSunLight.SetType(3);
		this.scenePointLight.SetType(1);
		this.lightsConfigured = false;
		super.setupGraphics();
	}

	@OriginalMember(owner = "client!bfa", name = "c", descriptor = "()Lclient!fia;")
	@Override
	public Renderer renderer() {
		@Pc(6) D3DADAPTER_IDENTIFIER identifier = this.iDirect3D.a(this.adapter, 0);
		return new Renderer(identifier.VendorID, "Direct3D", 9, identifier.Description, identifier.DriverVersion);
	}

	@OriginalMember(owner = "client!bfa", name = "K", descriptor = "(I)V")
	@Override
	public void setTextureAlphaStage() {
		@Pc(19) int alphaOp = this.textureBound[this.currentTextureSlot] ? D3DEnumTranslator.toD3DTextureOp(this.textureAlphaCombineModes[this.currentTextureSlot]) : 1;
		this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 4, alphaOp);
	}

	@OriginalMember(owner = "client!bfa", name = "M", descriptor = "(I)V")
	@Override
	public void setTextureMatrix() {
		if (this.iDirect3DVertexShader != null || this.textureStates[this.currentTextureSlot] == TextureState.INITIALIZE) {
			this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 24, 0);
			this.textureTransformFlags[this.currentTextureSlot] = 0;
			return;
		}
		if (TextureTransformMode.aClass332_6 == this.textureStates[this.currentTextureSlot]) {
			this.iDirect3DDevice.SetTransform(this.currentTextureSlot + 16, this.textureMatrices[this.currentTextureSlot].copy2DColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER));
		} else {
			this.iDirect3DDevice.SetTransform(this.currentTextureSlot + 16, this.textureMatrices[this.currentTextureSlot].copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER));
		}
		@Pc(62) int transformFlags = D3DEnumTranslator.toD3DTextureTransformFlags(this.textureStates[this.currentTextureSlot]);
		if (transformFlags != this.textureTransformFlags[this.currentTextureSlot]) {
			this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 24, transformFlags);
			this.textureTransformFlags[this.currentTextureSlot] = transformFlags;
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	@Override
	public GpuTexture createNativeTexture2D(@OriginalArg(0) int height, @OriginalArg(2) int width, @OriginalArg(3) DataType type, @OriginalArg(4) PixelFormat format) {
		return new D3DTexture2D(this, format, type, width, height);
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	@Override
	public void resizeSurface(@OriginalArg(1) Canvas canvas, @OriginalArg(2) Object object) {
		if (canvas != this.lb) {
			return;
		}
		@Pc(7) Dimension dimension = canvas.getSize();
		if (dimension.width > 0 && dimension.height > 0) {
			this.iDirect3DDevice.EndScene();
			this.resetDevice();
			this.iDirect3DDevice.BeginScene();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "u", descriptor = "(I)V")
	@Override
	public void toggleLights() {
		this.iDirect3DDevice.a(137, this.lightingEnabled && !this.forceLightsDisabled);
	}

	@OriginalMember(owner = "client!bfa", name = "S", descriptor = "(I)V")
	@Override
	public void blendDrawBuffer() {
		if (BlendFuncMode.ALPHA_BLENDING == this.blendFuncMode) {
			this.iDirect3DDevice.SetRenderState(19, 5);
			this.iDirect3DDevice.SetRenderState(20, 6);
		} else if (this.blendFuncMode == BlendFuncMode.OPAQUE_BLENDING) {
			this.iDirect3DDevice.SetRenderState(19, 2);
			this.iDirect3DDevice.SetRenderState(20, 2);
		} else if (BlendFuncMode.COLOR_BLENDING == this.blendFuncMode) {
			this.iDirect3DDevice.SetRenderState(19, 9);
			this.iDirect3DDevice.SetRenderState(20, 2);
		}
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(II)V")
	@Override
	public void flip(@OriginalArg(0) int x, @OriginalArg(1) int y) throws FlipException {
		this.iDirect3DDevice.EndScene();
		if (this.currentSwapChain.isValid()) {
			this.failedFlipCount = 0;
			if (ue.a((byte) 97, this.currentSwapChain.present())) {
				this.resetDevice();
			}
		} else if (++this.failedFlipCount <= 50) {
			this.resetDevice();
		} else {
			throw new FlipException();
		}
		this.iDirect3DDevice.BeginScene();
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!tc;I)V")
	@Override
	public void setStreamSource(@OriginalArg(1) NativeStream vertexBuffer, @OriginalArg(2) int streamIndex) {
		@Pc(10) DxVertexBuffer dxVertexBuffer = (DxVertexBuffer) vertexBuffer;
		this.iDirect3DDevice.SetStreamSource(streamIndex, dxVertexBuffer.vertexBuffer, 0, dxVertexBuffer.getStride());
	}

	@OriginalMember(owner = "client!bfa", name = "ya", descriptor = "()V")
	@Override
	public void ya() {
		this.setDepthMaskEnabled(true);
		this.iDirect3DDevice.Clear(2, 0, 1.0F, 0);
	}
}
