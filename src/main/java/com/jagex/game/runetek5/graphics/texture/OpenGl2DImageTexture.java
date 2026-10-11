package com.jagex.game.runetek5.graphics.texture;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!eba")
public final class OpenGl2DImageTexture extends OpenGl2DTexture {

	@OriginalMember(owner = "client!eba", name = "I", descriptor = "Z")
	public final boolean tiled;

	@OriginalMember(owner = "client!eba", name = "F", descriptor = "F")
	public final float t;

	@OriginalMember(owner = "client!eba", name = "E", descriptor = "F")
	public final float s;

	@OriginalMember(owner = "client!eba", name = "H", descriptor = "I")
	public final int singleWidth;

	@OriginalMember(owner = "client!eba", name = "G", descriptor = "I")
	public final int singleLength;

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length) {
		super(glToolkit, target, internalFormat, width, length);
		if (super.target == 34037) {
			this.t = length;
			this.tiled = false;
			this.s = width;
		} else {
			this.tiled = true;
			this.s = this.t = 1.0F;
		}
		this.singleWidth = width;
		this.singleLength = length;
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIII)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int width, @OriginalArg(5) int length) {
		super(glToolkit, 3553, target, width, length);
		this.s = (float) x / (float) width;
		this.singleLength = y;
		this.singleWidth = x;
		this.t = (float) y / (float) length;
		this.tiled = false;
		this.setRepeat(false, false);
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIIZ)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int width, @OriginalArg(5) int length, @OriginalArg(6) boolean copyFromFramebuffer) {
		super(glToolkit, target, x, y, width, length);
		this.singleWidth = width;
		if (super.target == 34037) {
			this.tiled = false;
			this.s = width;
			this.t = length;
		} else {
			this.tiled = true;
			this.s = this.t = 1.0F;
		}
		this.singleLength = length;
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIIIZ)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int singleWidth, @OriginalArg(4) int singleLength, @OriginalArg(5) int width, @OriginalArg(6) int length, @OriginalArg(7) boolean copyFromFramebuffer) {
		super(glToolkit, 3553, x, y, width, length);
		this.singleWidth = singleWidth;
		this.t = (float) singleLength / (float) length;
		this.s = (float) singleWidth / (float) width;
		this.singleLength = singleLength;
		this.tiled = false;
		this.setRepeat(false, false);
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIII[BI)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int singleWidth, @OriginalArg(3) int singleLength, @OriginalArg(4) int width, @OriginalArg(5) int length, @OriginalArg(6) byte[] data, @OriginalArg(7) int format) {
		super(glToolkit, 3553, internalFormat, width, length);
		this.singleWidth = singleWidth;
		this.singleLength = singleLength;
		this.uploadTextureData(singleWidth, true, singleLength, format, data);
		this.t = (float) singleLength / (float) length;
		this.s = (float) singleWidth / (float) width;
		this.tiled = false;
		this.setRepeat(false, false);
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIZ[III)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int width, @OriginalArg(3) int length, @OriginalArg(4) boolean mipmapped, @OriginalArg(5) int[] pixels, @OriginalArg(6) int rowLength, @OriginalArg(7) int offset) {
		super(glToolkit, target, 6408, width, length, mipmapped, pixels, rowLength, offset, true);
		if (super.target == 34037) {
			this.s = width;
			this.tiled = false;
			this.t = length;
		} else {
			this.tiled = true;
			this.s = this.t = 1.0F;
		}
		this.singleLength = length;
		this.singleWidth = width;
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIII[I)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int imageWidth, @OriginalArg(2) int imageLength, @OriginalArg(3) int totalWidth, @OriginalArg(4) int totalLength, @OriginalArg(5) int[] pixels) {
		super(glToolkit, 3553, 6408, totalWidth, totalLength);
		this.singleWidth = imageWidth;
		this.singleLength = imageLength;
		this.updateTextureRegion(imageLength, pixels, imageWidth);
		this.s = (float) imageWidth / (float) totalWidth;
		this.t = (float) imageLength / (float) totalLength;
		this.tiled = false;
		this.setRepeat(false, false);
	}

	@OriginalMember(owner = "client!eba", name = "<init>", descriptor = "(Lclient!qo;IIIIZ[BI)V")
	public OpenGl2DImageTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) boolean mipmapped, @OriginalArg(6) byte[] pixels, @OriginalArg(7) int format) {
		super(glToolkit, target, internalFormat, width, length, mipmapped, pixels, format, true);
		this.singleWidth = width;
		if (super.target == 34037) {
			this.t = length;
			this.tiled = false;
			this.s = width;
		} else {
			this.tiled = true;
			this.s = this.t = 1.0F;
		}
		this.singleLength = length;
	}
	@OriginalMember(owner = "client!fr", name = "a", descriptor = "(Z[BIILclient!qo;III)Lclient!eba;")
	public static OpenGl2DImageTexture createSurface(@OriginalArg(1) byte[] data, @OriginalArg(3) int height, @OriginalArg(4) GlToolkit glToolkit, @OriginalArg(7) int width) {
		if (glToolkit.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new OpenGl2DImageTexture(glToolkit, 3553, 6406, width, height, false, data, 6406);
		} else if (glToolkit.arbRectangleTexturesSupported) {
			return new OpenGl2DImageTexture(glToolkit, 34037, 6406, width, height, false, data, 6406);
		} else {
			return new OpenGl2DImageTexture(glToolkit, 6406, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), data, 6406);
		}
	}
	@OriginalMember(owner = "client!la", name = "a", descriptor = "(Lclient!qo;IIIII)Lclient!eba;")
	public static OpenGl2DImageTexture createFromScreenCapture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int x, @OriginalArg(3) int width, @OriginalArg(4) int y, @OriginalArg(5) int height) {
		if (glToolkit.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new OpenGl2DImageTexture(glToolkit, 3553, x, y, width, height, true);
		} else if (glToolkit.arbRectangleTexturesSupported) {
			return new OpenGl2DImageTexture(glToolkit, 34037, x, y, width, height, true);
		} else {
			return new OpenGl2DImageTexture(glToolkit, x, y, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), true);
		}
	}
	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(ILclient!qo;III)Lclient!eba;")
	public static OpenGl2DImageTexture createEmpty(@OriginalArg(1) GlToolkit glToolkit, @OriginalArg(2) int internalFormat, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		if (glToolkit.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new OpenGl2DImageTexture(glToolkit, 3553, internalFormat, width, height);
		} else if (glToolkit.arbRectangleTexturesSupported) {
			return new OpenGl2DImageTexture(glToolkit, 34037, internalFormat, width, height);
		} else {
			return new OpenGl2DImageTexture(glToolkit, internalFormat, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height));
		}
	}

	@OriginalMember(owner = "client!wi", name = "a", descriptor = "([IIIIILclient!qo;ZB)Lclient!eba;")
	public static OpenGl2DImageTexture createFromPixels(@OriginalArg(0) int[] pixels, @OriginalArg(1) int pixelOffset, @OriginalArg(2) int rowLength, @OriginalArg(3) int height, @OriginalArg(4) int width, @OriginalArg(5) GlToolkit glToolkit) {
		if (glToolkit.framebufferObjectSupported || MipmapTextureSampler.isPowerOfTwo(width) && MipmapTextureSampler.isPowerOfTwo(height)) {
			return new OpenGl2DImageTexture(glToolkit, 3553, width, height, false, pixels, rowLength, 0);
		} else if (glToolkit.arbRectangleTexturesSupported) {
			return new OpenGl2DImageTexture(glToolkit, 34037, width, height, false, pixels, rowLength, 0);
		} else {
			return new OpenGl2DImageTexture(glToolkit, width, height, Utils.nextPowerOfTwo(width), Utils.nextPowerOfTwo(height), pixels);
		}
	}
}
