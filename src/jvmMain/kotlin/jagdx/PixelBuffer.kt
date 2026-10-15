// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

package jagdx

import jaclib.memory.Buffer
import jaclib.peer.hb
import jaclib.peer.uda
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/PixelBuffer")
actual class PixelBuffer : uda, Buffer {

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.init()
	}

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "getRowPitch", descriptor = "()I")
	actual external fun getRowPitch(): Int

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "getub", descriptor = "([BIII)V")
	private external fun getub(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "a", descriptor = "([BIII)V")
	actual override fun a(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if ((arg2 < 0) or (arg0 == null) or (arg1 < 0) or (arg3 + arg1 > arg0!!.size) || this.getSize() < arg3 + arg2) {
			throw fda()
		}
		this.putub(arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "putub", descriptor = "([BIII)V")
	private external fun putub(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "a", descriptor = "([IIII)V")
	actual fun a(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if ((arg2 < 0) or (arg1 + arg3 > arg0!!.size) or (arg1 < 0) or (arg0 == null) || arg3 * 4 + arg2 > this.getSize()) {
			throw fda()
		}
		this.puti(arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "init", descriptor = "()V")
	private external fun init()

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "getAddress", descriptor = "()J")
	actual override fun getAddress(): Long {
		return 0L
	}

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "getSize", descriptor = "()I")
	actual external override fun getSize(): Int

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "b", descriptor = "([IIII)V")
	actual fun b(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if ((arg1 < 0) or (arg0 == null) or (arg3 + arg1 > arg0!!.size) or (arg2 < 0) || this.getSize() < arg3 * 4 + arg2) {
			throw fda()
		}
		this.geti(arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "puti", descriptor = "([IIII)V")
	private external fun puti(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "geti", descriptor = "([IIII)V")
	private external fun geti(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!jagdx/PixelBuffer", name = "getSlicePitch", descriptor = "()I")
	actual external fun getSlicePitch(): Int
}
