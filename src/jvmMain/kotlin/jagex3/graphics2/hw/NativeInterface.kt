package jagex3.graphics2.hw

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagex3/graphics2/hw/NativeInterface")
actual class NativeInterface @OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "<init>", descriptor = "(II)V") actual constructor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	init {
		this.init(arg0, arg1)
	}

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "copyLighting", descriptor = "([S[B[S[S[S[S[BII[SIIIJ)V")
	actual external fun copyLighting(@OriginalArg(0) arg0: ShortArray?, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: ShortArray?, @OriginalArg(3) arg3: ShortArray?, @OriginalArg(4) arg4: ShortArray?, @OriginalArg(5) arg5: ShortArray?, @OriginalArg(6) arg6: ByteArray?, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: ShortArray?, @OriginalArg(10) arg10: Int, @OriginalArg(11) arg11: Int, @OriginalArg(12) arg12: Int, @OriginalArg(13) arg13: Long)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "init", descriptor = "(II)V")
	private external fun init(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "copyNormals", descriptor = "([S[S[S[BFFIIIJ)V")
	actual external fun copyNormals(@OriginalArg(0) arg0: ShortArray?, @OriginalArg(1) arg1: ShortArray?, @OriginalArg(2) arg2: ShortArray?, @OriginalArg(3) arg3: ByteArray?, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Long)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "copyPositions", descriptor = "([I[I[I[SIIIJ)V")
	actual external fun copyPositions(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: ShortArray?, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Long)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "setSunColour", descriptor = "(FFFFF)V")
	actual external fun setSunColour(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "initTextureMetrics", descriptor = "(IBB)V")
	actual external fun initTextureMetrics(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "release", descriptor = "()V")
	actual external fun release()

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "setAmbient", descriptor = "(F)V")
	actual external fun setAmbient(@OriginalArg(0) arg0: Float)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "copyTexCoords", descriptor = "([F[FIIIJ)V")
	actual external fun copyTexCoords(@OriginalArg(0) arg0: FloatArray?, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Long)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "copyColours", descriptor = "([S[B[SI[SIIIJ)V")
	actual external fun copyColours(@OriginalArg(0) arg0: ShortArray?, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: ShortArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ShortArray?, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Long)

	@OriginalMember(owner = "client!jagex3/graphics2/hw/NativeInterface", name = "setSunDirection", descriptor = "(FFF)V")
	actual external fun setSunDirection(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)
}
