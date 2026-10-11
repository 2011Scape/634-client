package com.jagex.game.runetek5.graphics.dx;

import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.D3DTexture;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.CubeTextureSampler;
import jagdx.IDirect3DBaseTexture;
import jagdx.IDirect3DCubeTexture;
import jagdx.PixelBuffer;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!gm")
public final class D3DCubeMapTexture extends D3DTexture implements CubeTextureSampler {

	@OriginalMember(owner = "client!gm", name = "f", descriptor = "I")
	private final int size;

	@OriginalMember(owner = "client!gm", name = "g", descriptor = "Lclient!jagdx/IDirect3DCubeTexture;")
	private final IDirect3DCubeTexture texture;

	@OriginalMember(owner = "client!gm", name = "<init>", descriptor = "(Lclient!bfa;IZ[[I)V")
	public D3DCubeMapTexture(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) int size, @OriginalArg(2) boolean mipmapped, @OriginalArg(3) int[][] pixels) {
		super(d3DToolkit, GLFormat.HDR, GLFormat.RGBA, mipmapped && d3DToolkit.mipmappedCubeMapSupported, size * 6 * size);
		this.size = size;
		if (this.mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice.a(this.size, 0, 1024, 21, 1);
		} else {
			this.texture = this.toolkit.iDirect3DDevice.a(this.size, 1, 0, 21, 1);
		}
		@Pc(52) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		for (@Pc(54) int i = 0; i < 6; i++) {
			@Pc(73) int result = this.texture.LockRect(i, 0, 0, 0, this.size, this.size, 0, pixelBuffer);
			if (ue.a(result, false)) {
				@Pc(81) int rowPitch = pixelBuffer.getRowPitch();
				if (rowPitch == this.size * 4) {
					pixelBuffer.a((int[]) pixels[i], 0, 0, this.size * this.size);
				} else {
					for (@Pc(102) int j = 0; j < this.size; j++) {
						pixelBuffer.a(pixels[i], this.size * j, j * rowPitch, this.size);
					}
				}
				this.texture.UnlockRect(i, 0);
			}
		}
	}

	@OriginalMember(owner = "client!gm", name = "a", descriptor = "(B)V")
	@Override
	public void bindTexture() {
		this.toolkit.bindTexture(this);
	}

	@OriginalMember(owner = "client!gm", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	@Override
	public IDirect3DBaseTexture getTexture() {
		return this.texture;
	}

	@OriginalMember(owner = "client!gm", name = "a", descriptor = "(Lclient!ria;I)V")
	@Override
	public void setFilterMode(@OriginalArg(0) FilterMode filter) {
		super.setFilterMode(filter);
	}
}
