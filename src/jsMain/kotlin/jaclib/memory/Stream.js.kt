/*
 * Not part of the original client: the browser side of Stream (milestone 5). The JVM class's logic;
 * floatToRawIntBits and the byte-order test use typed arrays.
 */
package jaclib.memory

actual class Stream private actual constructor(arg0: Int) {

	private var a: Int = 0

	private var b: Buffer? = null

	private var c: Int = 0

	private var d: Int = 0

	private val e: ByteArray = ByteArray(arg0)

	actual constructor() : this(4096)

	actual constructor(buffer: Buffer?) : this(buffer, 0, buffer!!.getSize())

	actual constructor(buffer: Buffer?, arg1: Int, arg2: Int) : this(if (buffer!!.getSize() >= 4096) 4096 else buffer.getSize()) {
		this.a(buffer, arg1, arg2)
	}

	actual fun a(arg0: Int) {
		if (this.c + 1 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
	}

	actual fun a(arg0: Float) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		val local12: Int = floatToRawIntBits(arg0)
		this.e[this.c++] = (local12 shr 24).toByte()
		this.e[this.c++] = (local12 shr 16).toByte()
		this.e[this.c++] = (local12 shr 8).toByte()
		this.e[this.c++] = local12.toByte()
	}

	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		if (this.c + 3 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = arg1.toByte()
		this.e[this.c++] = arg2.toByte()
		this.e[this.c++] = arg3.toByte()
	}

	actual fun a(buffer: Buffer?) {
		this.a(buffer, 0, buffer!!.getSize())
	}

	actual fun b(arg0: Int) {
		if (this.c + 3 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = (arg0 shr 16).toByte()
		this.e[this.c++] = (arg0 shr 24).toByte()
	}

	actual fun a() {
		if (this.c <= 0) {
			return
		}
		if (this.a < this.c + this.d) {
			throw RuntimeException()
		}
		this.b!!.a(this.e, 0, this.d, this.c)
		this.d += this.c
		this.c = 0
	}

	private fun a(buffer: Buffer?, arg1: Int, arg2: Int) {
		this.a()
		this.a = arg1 + arg2
		this.d = arg1
		this.b = buffer
		if (this.a > buffer!!.getSize()) {
			throw RuntimeException()
		}
	}

	actual fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		this.e[this.c++] = arg2.toByte()
		this.e[this.c++] = arg1.toByte()
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = arg3.toByte()
	}

	actual fun c(arg0: Int) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		this.e[this.c++] = (arg0 shr 16).toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 24).toByte()
	}

	actual fun b(): Int {
		return this.d + this.c
	}

	actual fun d(arg0: Int) {
		if (this.e.size <= this.c + 1) {
			this.a()
		}
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = arg0.toByte()
	}

	actual fun e(arg0: Int) {
		this.a()
		this.d = arg0
	}

	actual fun b(arg0: Float) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		val local12: Int = floatToRawIntBits(arg0)
		this.e[this.c++] = local12.toByte()
		this.e[this.c++] = (local12 shr 8).toByte()
		this.e[this.c++] = (local12 shr 16).toByte()
		this.e[this.c++] = (local12 shr 24).toByte()
	}

	actual fun f(arg0: Int) {
		if (this.c >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
	}

actual companion object {
		private val scratchBuffer = org.khronos.webgl.ArrayBuffer(4)
		private val scratchFloat32 = org.khronos.webgl.Float32Array(scratchBuffer)
		private val scratchInt32 = org.khronos.webgl.Int32Array(scratchBuffer)

		actual fun floatToRawIntBits(arg0: Float): Int {
			scratchFloat32.asDynamic()[0] = arg0
			return scratchInt32.asDynamic()[0] as Int
		}

		fun getLSB(arg0: Int): Byte {
			return arg0.toByte()
		}

		actual fun c(): Boolean {
			val buffer = org.khronos.webgl.ArrayBuffer(4)
			val u32 = org.khronos.webgl.Uint32Array(buffer)
			val u8 = org.khronos.webgl.Uint8Array(buffer)
			u32.asDynamic()[0] = 1
			val lowestByte: Int = u8.asDynamic()[0]
			return lowestByte != 1
		}
	}

}
