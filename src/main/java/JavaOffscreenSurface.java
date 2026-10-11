import com.jagex.game.runetek5.JavaDepthBuffer;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!it")
public final class JavaOffscreenSurface implements OffscreenSurface {

	@OriginalMember(owner = "client!it", name = "k", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!it", name = "g", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!it", name = "d", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!it", name = "m", descriptor = "[I")
	public final int[] raster;

	@OriginalMember(owner = "client!it", name = "f", descriptor = "Lclient!ru;")
	private JavaDepthBuffer depth;

	@OriginalMember(owner = "client!it", name = "a", descriptor = "[F")
	public float[] depthBuffer;

	@OriginalMember(owner = "client!it", name = "<init>", descriptor = "(Lclient!ao;Lclient!mi;Lclient!ru;)V")
	public JavaOffscreenSurface(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) NativeSprite sprite, @OriginalArg(2) JavaDepthBuffer javaDepthBuffer) {
		this.toolkit = javaToolkit;
		if (sprite instanceof OpaqueSpriteSM) {
			@Pc(34) OpaqueSpriteSM javaRgbSprite = (OpaqueSpriteSM) sprite;
			this.raster = javaRgbSprite.pixels;
			this.height = javaRgbSprite.imageHeight;
			this.width = javaRgbSprite.imageWidth;
		} else if (sprite instanceof TransparentSpriteSM) {
			@Pc(17) TransparentSpriteSM javaArgbSprite = (TransparentSpriteSM) sprite;
			this.width = javaArgbSprite.imageWidth;
			this.height = javaArgbSprite.imageHeight;
			this.raster = javaArgbSprite.pixels;
		} else {
			throw new RuntimeException();
		}
		if (javaDepthBuffer != null) {
			this.depth = javaDepthBuffer;
			if (this.depth.height != this.width || this.height != this.depth.width) {
				throw new RuntimeException();
			}
			this.depthBuffer = this.depth.depths;
		}
	}

	@OriginalMember(owner = "client!it", name = "a", descriptor = "(IIIIIIZZ)V")
	@Override
	public void copyToSurface(@OriginalArg(0) int srcX, @OriginalArg(1) int srcY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int dstX, @OriginalArg(5) int dstY) {
		JavaOffscreenSurface.copyRasterRegion(this.toolkit.canvasSurface.raster, srcY, srcX, dstY, this.toolkit.mainDepthBuffer, this.raster, width, height, this.toolkit.canvasSurface.width, this.width, dstX, this.depthBuffer);
	}

	@OriginalMember(owner = "client!it", name = "b", descriptor = "(IIIIIIZZ)V")
	@Override
	public void copyFromSurface(@OriginalArg(0) int srcX, @OriginalArg(1) int srcY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(6) boolean copyRaster) {
		JavaOffscreenSurface.copyRasterRegion(this.raster, srcY, srcX, 0, this.depthBuffer, copyRaster ? this.toolkit.canvasSurface.raster : null, width, height, this.width, this.toolkit.canvasSurface.width, 0, this.toolkit.mainDepthBuffer);
	}
	@OriginalMember(owner = "client!rq", name = "a", descriptor = "([IIII[F[IIIIIII[F)V")
	public static void copyRasterRegion(@OriginalArg(0) int[] raster, @OriginalArg(1) int srcY, @OriginalArg(2) int srcX, @OriginalArg(3) int dstY, @OriginalArg(4) float[] dstDepth, @OriginalArg(5) int[] srcRaster, @OriginalArg(6) int copyWidth, @OriginalArg(8) int copyHeight, @OriginalArg(9) int width, @OriginalArg(10) int srcWidth, @OriginalArg(11) int dstX, @OriginalArg(12) float[] srcDepth) {
		@Pc(17) int srcOffset = srcY * srcWidth + srcX;
		@Pc(23) int dstOffset = dstX + width * dstY;
		@Pc(28) int srcStep = srcWidth - copyWidth;
		@Pc(33) int dstStep = width - copyWidth;
		@Pc(37) int row;
		@Pc(44) int rowEnd;
		if (srcRaster == null) {
			for (row = 0; row < copyHeight; row++) {
				rowEnd = srcOffset + copyWidth;
				while (rowEnd > srcOffset) {
					dstDepth[dstOffset++] = srcDepth[srcOffset++];
				}
				srcOffset += srcStep;
				dstOffset += dstStep;
			}
		} else if (srcDepth == null) {
			for (row = 0; row < copyHeight; row++) {
				rowEnd = copyWidth + srcOffset;
				while (rowEnd > srcOffset) {
					raster[dstOffset++] = srcRaster[srcOffset++];
				}
				srcOffset += srcStep;
				dstOffset += dstStep;
			}
		} else {
			for (row = 0; row < copyHeight; row++) {
				rowEnd = srcOffset + copyWidth;
				while (rowEnd > srcOffset) {
					raster[dstOffset] = srcRaster[srcOffset];
					dstDepth[dstOffset++] = srcDepth[srcOffset++];
				}
				srcOffset += srcStep;
				dstOffset += dstStep;
			}
		}
	}
}
