package com.jagex.game.runetek5;

import com.jagex.game.runetek5.statics.ZoneLocRotation;
import jagdx.D3DDISPLAYMODE;
import jagdx.D3DPRESENT_PARAMETERS;
import jagdx.IDirect3D;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode;
import com.jagex.game.runetek5.toolkit.companion.PrimitiveDrawMode;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

public final class D3DEnumTranslator {

	@OriginalMember(owner = "client!bfa", name = "Tg", descriptor = "[I")
	private static final int[] BACK_BUFFER_FORMATS = new int[] { 22, 23 };

	@OriginalMember(owner = "client!bfa", name = "ah", descriptor = "[F")
	public static final float[] MATRIX_BUFFER = new float[16];

	@OriginalMember(owner = "client!bfa", name = "mh", descriptor = "[I")
	public static final int[] DEPTH_STENCIL_FORMATS = new int[] { 77, 80 };

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!ria;B)I")
	public static int toD3DFilter(@OriginalArg(0) FilterMode filter) {
		if (SampleMode.LINEAR == filter) {
			return 2;
		} else if (FilterMode.aClass298_10 == filter) {
			return 1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!hj;B)I")
	public static int toD3DPrimitiveType(@OriginalArg(0) PrimitiveType primitiveType) {
		if (primitiveType == PrimitiveDrawMode.LINES) {
			return 2;
		} else if (PrimitiveType.aClass134_5 == primitiveType) {
			return 3;
		} else if (PrimitiveType.aClass134_8 == primitiveType) {
			return 1;
		} else if (primitiveType == PrimitiveType.aClass134_6) {
			return 4;
		} else if (PrimitiveType.aClass134_3 == primitiveType) {
			return 6;
		} else if (primitiveType == PrimitiveType.aClass134_4) {
			return 5;
		} else {
			throw new IllegalArgumentException("");
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!sia;)I")
	public static int toD3DTextureOp(@OriginalArg(1) TextureCombineFunction combineFunction) {
		if (TextureCombineFunction.aClass315_4 == combineFunction) {
			return 2;
		} else if (TextureCombineMode.MODULATE == combineFunction) {
			return 4;
		} else if (TextureCombineFunction.aClass315_5 == combineFunction) {
			return 26;
		} else if (combineFunction == TextureCombineFunction.aClass315_2) {
			return 7;
		} else if (combineFunction == TextureCombineFunction.aClass315_1) {
			return 10;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!baa;Lclient!wq;)I")
	public static int toD3DFormat(@OriginalArg(1) DataType type, @OriginalArg(2) PixelFormat format) {
		if (type == GLFormat.RGBA) {
			if (PixelFormat.aClass375_38 == format) {
				return 22;
			}
			if (GLFormat.HDR == format) {
				return 21;
			}
			if (GLActionFormat.PIXEL_FORMAT == format) {
				return 28;
			}
			if (PixelFormat.aClass375_44 == format) {
				return 50;
			}
			if (format == PixelFormat.aClass375_39) {
				return 51;
			}
			if (format == PixelFormat.aClass375_49) {
				return 77;
			}
		}
		throw new IllegalArgumentException("");
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!kf;I)I")
	public static int toD3DTextureArg(@OriginalArg(0) TextureCombineSource combineSource) {
		if (TextureCombineSource.TEXTURE == combineSource) {
			return 2;
		} else if (combineSource == TextureCombineSource.PRIMARY_COLOR) {
			return 0;
		} else if (TextureCombineSource.PREVIOUS == combineSource) {
			return 1;
		} else if (TextureCombineSource.CONSTANT_COLOR == combineSource) {
			return 3;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(ILclient!tr;)I")
	public static int toD3DTextureTransformFlags(@OriginalArg(1) TextureTransformMode transformMode) {
		if (transformMode == TextureTransformMode.aClass332_4) {
			return 1;
		} else if (transformMode == TextureTransformMode.aClass332_6) {
			return 2;
		} else if (transformMode == TextureTransformMode.aClass332_2) {
			return 3;
		} else if (TextureTransformMode.aClass332_1 == transformMode) {
			return 4;
		} else if (transformMode == TextureTransformMode.aClass332_5) {
			return 256;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!jagdx/D3DPRESENT_PARAMETERS;IILclient!jagdx/IDirect3D;II)Z")
	public static boolean selectPresentFormats(@OriginalArg(0) D3DPRESENT_PARAMETERS d3DPRESENT_PARAMETERS, @OriginalArg(2) int adapter, @OriginalArg(3) IDirect3D iDirect3D, @OriginalArg(4) int deviceType, @OriginalArg(5) int maxMultiSampleType) {
		@Pc(1) int depthStencilFormat = 0;
		@Pc(3) int backBufferFormat = 0;
		@Pc(5) int multiSampleType = 0;
		try {
			@Pc(9) D3DDISPLAYMODE d3DDISPLAYMODE = new D3DDISPLAYMODE();
			if (ue.a((byte) 97, iDirect3D.a(adapter, d3DDISPLAYMODE))) {
				return false;
			}
			label82: while (maxMultiSampleType >= 0) {
				if (maxMultiSampleType != 1) {
					multiSampleType = maxMultiSampleType;
					for (@Pc(33) int i = 0; i < BACK_BUFFER_FORMATS.length; i++) {
						if (iDirect3D.CheckDeviceType(adapter, deviceType, d3DDISPLAYMODE.Format, BACK_BUFFER_FORMATS[i], true) == 0 && iDirect3D.CheckDeviceFormat(adapter, deviceType, d3DDISPLAYMODE.Format, 1, 1, BACK_BUFFER_FORMATS[i]) == 0 && (maxMultiSampleType == 0 || iDirect3D.CheckDeviceMultiSampleType(adapter, deviceType, BACK_BUFFER_FORMATS[i], true, multiSampleType) == 0)) {
							for (@Pc(86) int j = 0; j < DEPTH_STENCIL_FORMATS.length; j++) {
								if (iDirect3D.CheckDeviceFormat(adapter, deviceType, d3DDISPLAYMODE.Format, 2, 1, DEPTH_STENCIL_FORMATS[j]) == 0 && iDirect3D.CheckDepthStencilMatch(adapter, deviceType, d3DDISPLAYMODE.Format, BACK_BUFFER_FORMATS[i], DEPTH_STENCIL_FORMATS[j]) == 0 && (maxMultiSampleType == 0 || iDirect3D.CheckDeviceMultiSampleType(adapter, deviceType, DEPTH_STENCIL_FORMATS[i], true, multiSampleType) == 0)) {
									depthStencilFormat = DEPTH_STENCIL_FORMATS[j];
									backBufferFormat = BACK_BUFFER_FORMATS[i];
									break label82;
								}
							}
						}
					}
				}
				maxMultiSampleType--;
			}
			if (maxMultiSampleType < 0 || backBufferFormat == 0 || depthStencilFormat == 0) {
				return false;
			}
			d3DPRESENT_PARAMETERS.MultiSampleType = multiSampleType;
			d3DPRESENT_PARAMETERS.AutoDepthStencilFormat = depthStencilFormat;
			d3DPRESENT_PARAMETERS.MultiSampleQuality = 0;
			d3DPRESENT_PARAMETERS.BackBufferFormat = backBufferFormat;
			return true;
		} catch (@Pc(185) Throwable ex) {
			return false;
		}
	}
}
