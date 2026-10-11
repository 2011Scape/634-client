package com.jagex.game.runetek5.graphics.font;

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.TextureCombineFunction;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.FilterMode;

@OriginalClass("client!kr")
public final class NativeFont extends AbstractFontRenderer {

	@OriginalMember(owner = "client!kr", name = "x", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!kr", name = "v", descriptor = "Lclient!wfa;")
	private final GpuTexture glyphAtlasSampler;

	@OriginalMember(owner = "client!kr", name = "w", descriptor = "Z")
	private final boolean isAlphaOnlyTexture;

	@OriginalMember(owner = "client!kr", name = "y", descriptor = "Lclient!tc;")
	private final NativeStream glyphVertexStream;

	@OriginalMember(owner = "client!kr", name = "<init>", descriptor = "(Lclient!wga;Lclient!oea;[Lclient!rh;Z)V")
	public NativeFont(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) FontMetrics fontMetrics, @OriginalArg(2) SpriteType[] indexedImages, @OriginalArg(3) boolean isAlphaOnly) {
		super(nativeToolkit, fontMetrics);
		this.nativeToolkit = nativeToolkit;
		@Pc(8) int maxDimension = 0;
		for (@Pc(10) int i = 0; i < 256; i++) {
			@Pc(15) SpriteType indexedImage = indexedImages[i];
			if (indexedImage.height > maxDimension) {
				maxDimension = indexedImage.height;
			}
			if (indexedImage.width > maxDimension) {
				maxDimension = indexedImage.width;
			}
		}
		@Pc(37) int atlasSize = maxDimension * 16;
		@Pc(44) byte[] pixels = new byte[atlasSize * atlasSize];
		@Pc(54) int height;
		@Pc(63) int local63;
		@Pc(75) int local75;
		@Pc(77) int width;
		@Pc(85) int minX;
		for (@Pc(46) int j = 0; j < 256; j++) {
			@Pc(51) SpriteType indexedImage2 = indexedImages[j];
			height = indexedImage2.height;
			@Pc(57) int glyphWidth = indexedImage2.width;
			int cellX = j % 16 * maxDimension;
			@Pc(69) int cellY = j / 16 * maxDimension;
			int pixelIndex = cellY * atlasSize + cellX;
			width = 0;
			@Pc(83) byte[] maskBytes;
			@Pc(88) int x;
			if (indexedImage2.alpha == null) {
				maskBytes = indexedImage2.pixels;
				for (minX = 0; minX < height; minX++) {
					for (x = 0; x < glyphWidth; x++) {
						pixels[pixelIndex++] = (byte) (maskBytes[width++] == 0 ? 0 : -1);
					}
					pixelIndex += atlasSize - glyphWidth;
				}
			} else {
				maskBytes = indexedImage2.alpha;
				for (minX = 0; minX < height; minX++) {
					for (x = 0; x < glyphWidth; x++) {
						pixels[pixelIndex++] = maskBytes[width++];
					}
					pixelIndex += atlasSize - glyphWidth;
				}
			}
		}
		if (nativeToolkit.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
			this.glyphAtlasSampler = nativeToolkit.createTextureSampler(pixels, atlasSize, GLActionFormat.PIXEL_FORMAT, false, atlasSize);
		} else {
			@Pc(162) int[] argbPixels = new int[pixels.length];
			for (height = 0; height < pixels.length; height++) {
				argbPixels[height] = pixels[height] << 24;
			}
			this.glyphAtlasSampler = nativeToolkit.createTexture(atlasSize, atlasSize, false, argbPixels);
		}
		this.isAlphaOnlyTexture = true;
		this.glyphAtlasSampler.setFilterMode(FilterMode.aClass298_10);
		this.glyphVertexStream = nativeToolkit.createStream(false);
		this.glyphVertexStream.create(20480, 20);
		for (@Pc(366) int k = 0; k < 4; k++) {
			@Pc(373) Buffer buffer = this.glyphVertexStream.lock();
			if (buffer != null) {
				@Pc(381) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
				@Pc(391) float sScale = this.glyphAtlasSampler.toTextureCoordS((float) atlasSize) / (float) atlasSize;
				@Pc(401) float tScale = this.glyphAtlasSampler.toTextureCoordT((float) atlasSize) / (float) atlasSize;
				for (int glyphIndex = 0; glyphIndex < 256; glyphIndex++) {
					@Pc(408) SpriteType indexedImage3 = indexedImages[glyphIndex];
					int glyphHeight = indexedImage3.height;
					width = indexedImage3.width;
					@Pc(417) int minY = indexedImage3.minY;
					minX = indexedImage3.minX;
					@Pc(427) float cellX = (float) (glyphIndex % 16 * maxDimension);
					@Pc(434) float cellY = (float) (glyphIndex / 16 * maxDimension);
					@Pc(438) float s0 = cellX * sScale;
					@Pc(442) float t0 = cellY * tScale;
					@Pc(449) float s1 = (cellX + (float) width) * sScale;
					@Pc(456) float t1 = (cellY + (float) glyphHeight) * tScale;
					if (Stream.c()) {
						stream.a((float) minX);
						stream.a((float) minY);
						stream.a(0.0F);
						stream.a(s0);
						stream.a(t0);
						stream.a((float) minX);
						stream.a((float) (minY + glyphHeight));
						stream.a(0.0F);
						stream.a(s0);
						stream.a(t1);
						stream.a((float) (minX + width));
						stream.a((float) (minY + glyphHeight));
						stream.a(0.0F);
						stream.a(s1);
						stream.a(t1);
						stream.a((float) (minX + width));
						stream.a((float) minY);
						stream.a(0.0F);
						stream.a(s1);
						stream.a(t0);
					} else {
						stream.b((float) minX);
						stream.b((float) minY);
						stream.b(0.0F);
						stream.b(s0);
						stream.b(t0);
						stream.b((float) minX);
						stream.b((float) (minY + glyphHeight));
						stream.b(0.0F);
						stream.b(s0);
						stream.b(t1);
						stream.b((float) (minX + width));
						stream.b((float) (minY + glyphHeight));
						stream.b(0.0F);
						stream.b(s1);
						stream.b(t1);
						stream.b((float) (minX + width));
						stream.b((float) minY);
						stream.b(0.0F);
						stream.b(s1);
						stream.b(t0);
					}
				}
				stream.a();
				if (this.glyphVertexStream.unlock()) {
					return;
				}
			}
		}
	}

	@OriginalMember(owner = "client!kr", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	@Override
	public void renderSymbol(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow, @OriginalArg(5) ComponentSurface mask, @OriginalArg(6) int offsetX, @OriginalArg(7) int offsetY) {
	}

	@OriginalMember(owner = "client!kr", name = "fa", descriptor = "(CIIIZ)V")
	@Override
	public void fa(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow) {
		this.nativeToolkit.setupTexturedUiRenderState();
		this.nativeToolkit.setTextureSampler(this.glyphAtlasSampler);
		if (this.isAlphaOnlyTexture || shadow) {
			this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4);
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
			this.nativeToolkit.setColourValue(colour);
		} else {
			this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_4);
		}
		@Pc(43) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		modelMatrix.applyTranslation(x, y, 0);
		this.nativeToolkit.commitModelMatrix();
		this.nativeToolkit.setStreamSource(this.glyphVertexStream, 0);
		this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionTexCoordDeclaration);
		this.nativeToolkit.drawPrimitive(2, c * 4, PrimitiveType.aClass134_3);
		if (this.isAlphaOnlyTexture || shadow) {
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		}
	}
}
