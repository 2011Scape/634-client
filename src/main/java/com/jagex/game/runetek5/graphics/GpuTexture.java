package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.PixelFormat;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;

@OriginalClass("client!wfa")
public interface GpuTexture extends TextureSampler {

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(I[IIIIBI)V")
	public void readPixels(@OriginalArg(1) int[] dest, @OriginalArg(2) int width, @OriginalArg(4) int height);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(ZZI)V")
	public void setWarp(@OriginalArg(0) boolean repeatS, @OriginalArg(1) boolean repeatT);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(FI)F")
	public float toTextureCoordS(@OriginalArg(0) float x);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(II[IIIIII)V")
	public void updateTextureRegion(@OriginalArg(0) int rowLength, @OriginalArg(2) int[] pixels, @OriginalArg(3) int x, @OriginalArg(5) int width, @OriginalArg(6) int y, @OriginalArg(7) int height);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(Z)I")
	public int getTextureWidth();

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	public void upload(@OriginalArg(3) int rowLength, @OriginalArg(4) int height, @OriginalArg(5) int width, @OriginalArg(7) byte[] data, @OriginalArg(8) PixelFormat format);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(BF)F")
	public float toTextureCoordT(@OriginalArg(1) float y);

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(I)I")
	public int getTextureHeight();

	@OriginalMember(owner = "client!wfa", name = "b", descriptor = "(I)Z")
	public boolean supportsTextureRepeat();
}
