package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler

@OriginalClass("client!wfa")
interface GpuTexture : TextureSampler {

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(I[IIIIBI)V")
	fun readPixels(@OriginalArg(1) dest: IntArray?, @OriginalArg(2) width: Int, @OriginalArg(4) height: Int)

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(ZZI)V")
	fun setWarp(@OriginalArg(0) repeatS: Boolean, @OriginalArg(1) repeatT: Boolean)

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(FI)F")
	fun toTextureCoordS(@OriginalArg(0) x: Float): Float

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(II[IIIIII)V")
	fun updateTextureRegion(@OriginalArg(0) rowLength: Int, @OriginalArg(2) pixels: IntArray?, @OriginalArg(3) x: Int, @OriginalArg(5) width: Int, @OriginalArg(6) y: Int, @OriginalArg(7) height: Int)

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(Z)I")
	fun getTextureWidth(): Int

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(IIBIIII[BLclient!wq;)V")
	fun upload(@OriginalArg(3) rowLength: Int, @OriginalArg(4) height: Int, @OriginalArg(5) width: Int, @OriginalArg(7) data: ByteArray?, @OriginalArg(8) format: PixelFormat?)

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(BF)F")
	fun toTextureCoordT(@OriginalArg(1) y: Float): Float

	@OriginalMember(owner = "client!wfa", name = "a", descriptor = "(I)I")
	fun getTextureHeight(): Int

	@OriginalMember(owner = "client!wfa", name = "b", descriptor = "(I)Z")
	fun supportsTextureRepeat(): Boolean
}
