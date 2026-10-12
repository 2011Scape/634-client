/*
 * Not part of the original client: the WebGL 2 emulator and jaclib memory behind the Java OpenGL
 * toolkit (1) in a browser, from void-client's jsMain (milestone 5).
 */
package jaclib.memory

actual open class NativeBuffer : Buffer, Source {
    actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
    }

    actual fun a(arg0: Long, arg1: Int) {
    }

    var a: Long
        get() = TODO("Not yet implemented")
        set(value) {}
    var b: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    actual override fun getAddress(): Long {
        TODO("Not yet implemented")
    }

    actual override fun getSize(): Int {
        TODO("Not yet implemented")
    }

    fun get(arg0: Long, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int) {
    }

    fun put(arg0: Long, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int) {
    }
}