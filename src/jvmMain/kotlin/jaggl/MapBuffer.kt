package jaggl

import jaclib.memory.NativeBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jaggl/MapBuffer")
actual class MapBuffer actual constructor() : NativeBuffer() {

	@OriginalMember(owner = "client!jaggl/MapBuffer", name = "c", descriptor = "I")
	private var c: Int = 0

	@OriginalMember(owner = "client!jaggl/MapBuffer", name = "a", descriptor = "([BIII)V")
	actual override fun a(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if (this.c == 0) {
			throw RuntimeException()
		}
		super.a(arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jaggl/MapBuffer", name = "a", descriptor = "(III)Z")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int): Boolean {
		if (this.c != 0) {
			return false
		}
		@Pc(8) val local8: Long = OpenGL.glMapBufferARB(arg0, arg2)
		if (local8 == 0L) {
			return false
		} else {
			this.a(local8, arg1)
			this.c = arg0
			return true
		}
	}

	@OriginalMember(owner = "client!jaggl/MapBuffer", name = "a", descriptor = "()Z")
	actual fun a(): Boolean {
		return this.c != 0
	}

	@OriginalMember(owner = "client!jaggl/MapBuffer", name = "b", descriptor = "()Z")
	actual fun b(): Boolean {
		@Pc(1) var local1: Boolean = true
		if (this.c != 0) {
			local1 = OpenGL.glUnmapBufferARB(this.c)
			this.a(0L, 0)
			this.c = 0
		}
		return local1
	}
}
