/*
 * Not part of the original client: the browser side of MapBuffer (milestone 5). The same logic as the
 * JVM class, over the WebGL emulator's glMapBufferARB.
 */
package jaggl

import jaclib.memory.NativeBuffer

actual class MapBuffer actual constructor() : NativeBuffer() {

	private var c: Int = 0

	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		if (this.c == 0) {
			throw RuntimeException()
		}
		super.a(arg0, arg1, arg2, arg3)
	}

	actual fun a(arg0: Int, arg1: Int, arg2: Int): Boolean {
		if (this.c != 0) {
			return false
		}
		val local8: Long = OpenGL.glMapBufferARB(arg0, arg2)
		if (local8 == 0L) {
			return false
		} else {
			this.a(local8, arg1)
			this.c = arg0
			return true
		}
	}

	actual fun a(): Boolean {
		return this.c != 0
	}

	actual fun b(): Boolean {
		var local1: Boolean = true
		if (this.c != 0) {
			local1 = OpenGL.glUnmapBufferARB(this.c)
			this.a(0L, 0)
			this.c = 0
		}
		return local1
	}
}
