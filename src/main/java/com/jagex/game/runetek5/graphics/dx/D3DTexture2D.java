package com.jagex.game.runetek5.graphics.dx;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.FilterMode;
import com.jagex.game.runetek5.PixelFormat;
import com.jagex.game.runetek5.D3DTexture;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.D3DEnumTranslator;
import com.jagex.game.runetek5.graphics.GpuTexture;
import jagdx.IDirect3DBaseTexture;
import jagdx.IDirect3DTexture;
import jagdx.PixelBuffer;
import jagdx.ue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!kt")
public final class D3DTexture2D extends D3DTexture implements GpuTexture {

	@OriginalMember(owner = "client!kt", name = "i", descriptor = "Z")
	public boolean repeatU;

	@OriginalMember(owner = "client!kt", name = "j", descriptor = "Z")
	public boolean repeatV;

	@OriginalMember(owner = "client!kt", name = "h", descriptor = "I")
	private final int width;

	@OriginalMember(owner = "client!kt", name = "f", descriptor = "I")
	private final int height;

	@OriginalMember(owner = "client!kt", name = "g", descriptor = "Lclient!jagdx/IDirect3DTexture;")
	private final IDirect3DTexture texture;

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;IIZ[III)V")
	public D3DTexture2D(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) boolean mipmapped, @OriginalArg(4) int[] pixels, @OriginalArg(5) int offset, @OriginalArg(6) int rowLength) {
		super(d3DToolkit, GLFormat.HDR, GLFormat.RGBA, mipmapped && d3DToolkit.mipmapSupported, height * width);
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.height = height;
			this.width = width;
		} else {
			this.width = Utils.nextPowerOfTwo(width);
			this.height = Utils.nextPowerOfTwo(height);
		}
		if (mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice.a(this.width, this.height, 0, 1024, 21, 1);
		} else {
			this.texture = this.toolkit.iDirect3DDevice.a(this.width, this.height, 1, 0, 21, 1);
		}
		@Pc(71) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(82) int result = this.texture.LockRect(0, 0, 0, width, height, 0, pixelBuffer);
		if (ue.a(result, false)) {
			if (rowLength == 0) {
				rowLength = width;
			}
			@Pc(95) int rowPitch = pixelBuffer.getRowPitch();
			if (rowPitch == width * 4 && width == rowLength) {
				pixelBuffer.a((int[]) pixels, offset, 0, width * height);
			} else {
				for (@Pc(114) int i = 0; i < height; i++) {
					pixelBuffer.a(pixels, i * rowLength + offset, rowPitch * i, width);
				}
			}
			this.texture.UnlockRect(0);
		}
	}

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;Lclient!baa;II)V")
	public D3DTexture2D(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) PixelFormat format, @OriginalArg(2) DataType type, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		super(d3DToolkit, format, type, false, height * width);
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.width = width;
			this.height = height;
		} else {
			this.width = Utils.nextPowerOfTwo(width);
			this.height = Utils.nextPowerOfTwo(height);
		}
		this.texture = this.toolkit.iDirect3DDevice.a(width, height, 1, 0, D3DEnumTranslator.toD3DFormat(this.type, this.format), 1);
	}

	@OriginalMember(owner = "client!kt", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;IIZ[BII)V")
	public D3DTexture2D(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) PixelFormat format, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean mipmapped, @OriginalArg(5) byte[] data, @OriginalArg(6) int offset, @OriginalArg(7) int rowLength) {
		super(d3DToolkit, format, GLFormat.RGBA, mipmapped && d3DToolkit.mipmapSupported, width * height);
		if (this.toolkit.nonPowerOfTwoSupported) {
			this.width = width;
			this.height = height;
		} else {
			this.width = Utils.nextPowerOfTwo(width);
			this.height = Utils.nextPowerOfTwo(height);
		}
		if (mipmapped) {
			this.texture = this.toolkit.iDirect3DDevice.a(this.width, this.height, 0, 1024, D3DEnumTranslator.toD3DFormat(GLFormat.RGBA, this.format), 1);
		} else {
			this.texture = this.toolkit.iDirect3DDevice.a(this.width, this.height, 1, 0, D3DEnumTranslator.toD3DFormat(GLFormat.RGBA, this.format), 1);
		}
		@Pc(79) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(90) int result = this.texture.LockRect(0, 0, 0, width, height, 0, pixelBuffer);
		if (ue.a(result, false)) {
			@Pc(105) int rowBytes = width * this.format.componentCount;
			@Pc(111) int srcRowBytes = width * this.format.componentCount;
			@Pc(114) int rowPitch = pixelBuffer.getRowPitch();
			if (rowPitch == rowBytes && srcRowBytes == rowBytes) {
				pixelBuffer.a((byte[]) data, offset, 0, rowBytes * height);
			} else {
				for (@Pc(130) int i = 0; i < height; i++) {
					pixelBuffer.a(data, i * srcRowBytes + offset, rowPitch * i, rowBytes);
				}
			}
			this.texture.UnlockRect(0);
		}
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(Lclient!ria;I)V")
	@Override
	public void setFilterMode(@OriginalArg(0) FilterMode filter) {
		super.setFilterMode(filter);
	}

	@OriginalMember(owner = "client!kt", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	@Override
	public IDirect3DBaseTexture getTexture() {
		return this.texture;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(Z)I")
	@Override
	public int getTextureWidth() {
		return this.width;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(I)I")
	@Override
	public int getTextureHeight() {
		return this.height;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(I[IIIIBI)V")
	@Override
	public void readPixels(@OriginalArg(1) int[] dest, @OriginalArg(2) int width, @OriginalArg(4) int height) {
		if (GLFormat.HDR != this.format || this.type != GLFormat.RGBA) {
			throw new RuntimeException();
		}
		@Pc(22) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(33) int result = this.texture.LockRect(0, 0, 0, width, height, 16, pixelBuffer);
		if (!ue.a(result, false)) {
			return;
		}
		@Pc(41) int rowPitch = pixelBuffer.getRowPitch();
		if (rowPitch == width * 4) {
			pixelBuffer.b(dest, 0, 0, dest.length);
		} else {
			for (@Pc(48) int i = 0; i < height; i++) {
				pixelBuffer.b(dest, width * i, i * rowPitch, width);
			}
		}
		this.texture.UnlockRect(0);
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(BF)F")
	@Override
	public float toTextureCoordT(@OriginalArg(1) float y) {
		return y / (float) this.height;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(II[IIIIII)V")
	@Override
	public void updateTextureRegion(@OriginalArg(0) int rowLength, @OriginalArg(2) int[] pixels, @OriginalArg(3) int x, @OriginalArg(5) int width, @OriginalArg(6) int y, @OriginalArg(7) int height) {
		if (this.format != GLFormat.HDR || this.type != GLFormat.RGBA) {
			throw new RuntimeException();
		}
		@Pc(16) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(27) int result = this.texture.LockRect(0, x, y, width, height, 0, pixelBuffer);
		if (!ue.a(result, false)) {
			return;
		}
		@Pc(35) int rowPitch = pixelBuffer.getRowPitch();
		if (width * 4 == rowPitch && rowLength == width) {
			pixelBuffer.a((int[]) pixels, 0, 0, height * width);
		} else {
			for (@Pc(53) int i = 0; i < height; i++) {
				pixelBuffer.a(pixels, rowLength * i, i * rowPitch, width);
			}
		}
		this.texture.UnlockRect(0);
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(FI)F")
	@Override
	public float toTextureCoordS(@OriginalArg(0) float x) {
		return x / (float) this.width;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(B)V")
	@Override
	public void bindTexture() {
		this.toolkit.bindTexture2D(this);
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	@Override
	public void upload(@OriginalArg(3) int rowLength, @OriginalArg(4) int height, @OriginalArg(5) int width, @OriginalArg(7) byte[] data, @OriginalArg(8) PixelFormat format) {
		if (this.format != format || this.type != GLFormat.RGBA) {
			throw new RuntimeException();
		}
		@Pc(20) PixelBuffer pixelBuffer = this.toolkit.pixelBuffer;
		@Pc(31) int result = this.texture.LockRect(0, 0, 0, width, height, 0, pixelBuffer);
		if (!ue.a(result, false)) {
			return;
		}
		@Pc(42) int srcRowBytes = rowLength * this.format.componentCount;
		@Pc(48) int rowBytes = width * this.format.componentCount;
		@Pc(51) int rowPitch = pixelBuffer.getRowPitch();
		if (rowBytes == rowPitch && srcRowBytes == rowBytes) {
			pixelBuffer.a((byte[]) data, 0, 0, rowBytes * height);
		} else {
			for (@Pc(72) int i = 0; i < height; i++) {
				pixelBuffer.a(data, i * srcRowBytes, i * rowPitch, rowBytes);
			}
		}
		this.texture.UnlockRect(0);
	}

	@OriginalMember(owner = "client!kt", name = "b", descriptor = "(I)Z")
	@Override
	public boolean supportsTextureRepeat() {
		return true;
	}

	@OriginalMember(owner = "client!kt", name = "a", descriptor = "(ZZI)V")
	@Override
	public void setWarp(@OriginalArg(0) boolean repeatU, @OriginalArg(1) boolean repeatV) {
		this.repeatV = repeatV;
		this.repeatU = repeatU;
	}
}
