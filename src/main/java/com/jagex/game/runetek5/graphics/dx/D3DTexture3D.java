package com.jagex.game.runetek5.graphics.dx;

import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.D3DTexture;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.Texture3DSampler;
import com.jagex.game.runetek5.D3DEnumTranslator;
import jagdx.IDirect3DBaseTexture;
import jagdx.IDirect3DVolumeTexture;
import jagdx.PixelBuffer;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!nc")
public final class D3DTexture3D extends D3DTexture implements Texture3DSampler {

	@OriginalMember(owner = "client!nc", name = "h", descriptor = "I")
	private final int depth;

	@OriginalMember(owner = "client!nc", name = "g", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!nc", name = "i", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!nc", name = "f", descriptor = "Lclient!jagdx/IDirect3DVolumeTexture;")
	private final IDirect3DVolumeTexture texture;

	@OriginalMember(owner = "client!nc", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;III[B)V")
	public D3DTexture3D(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) PixelFormat format, @OriginalArg(2) int depth, @OriginalArg(3) int height, @OriginalArg(4) int width, @OriginalArg(5) byte[] data) {
		super(d3DToolkit, format, GLFormat.RGBA, false, width * height * depth);
		this.depth = depth;
		this.width = width;
		this.height = height;
		this.texture = this.toolkit.iDirect3DDevice.a(depth, height, width, 1, 0, D3DEnumTranslator.toD3DFormat(this.type, format), 1);
		@Pc(40) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(53) int result = this.texture.LockBox(0, 0, 0, 0, depth, height, width, 0, pixelBuffer);
		if (ue.a(result, false)) {
			@Pc(64) int rowBytes = this.format.componentCount * this.depth;
			@Pc(69) int sliceBytes = rowBytes * this.height;
			@Pc(72) int slicePitch = pixelBuffer.getSlicePitch();
			if (slicePitch == sliceBytes) {
				pixelBuffer.a((byte[]) data, 0, 0, this.width * rowBytes * this.height);
			} else {
				@Pc(91) int rowPitch = pixelBuffer.getRowPitch();
				@Pc(96) int slice;
				if (rowBytes == rowPitch) {
					for (slice = 0; slice < this.width; slice++) {
						pixelBuffer.a(data, sliceBytes * slice, slice * slicePitch, sliceBytes);
					}
				} else {
					for (slice = 0; slice < this.width; slice++) {
						for (@Pc(102) int i = 0; i < this.height; i++) {
							pixelBuffer.a(data, rowBytes * i + sliceBytes * slice, i * rowPitch + slice * slicePitch, rowBytes);
						}
					}
				}
			}
			this.texture.UnlockBox(0);
		}
	}

	@OriginalMember(owner = "client!nc", name = "a", descriptor = "(B)V")
	@Override
	public void bindTexture() {
		this.toolkit.bindTexture3D(this);
	}

	@OriginalMember(owner = "client!nc", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	@Override
	public IDirect3DBaseTexture getTexture() {
		return this.texture;
	}

	@OriginalMember(owner = "client!nc", name = "a", descriptor = "(Lclient!ria;I)V")
	@Override
	public void setFilterMode(@OriginalArg(0) FilterMode filter) {
		super.setFilterMode(filter);
	}
}
