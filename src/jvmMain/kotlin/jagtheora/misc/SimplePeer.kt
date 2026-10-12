package jagtheora.misc

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/misc/SimplePeer")
actual abstract class SimplePeer actual constructor() {

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "clear", descriptor = "()V")
	protected abstract fun clear()

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "a", descriptor = "()V")
	actual fun a() {
		if (!this.b()) {
			this.clear()
		}
	}

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	actual fun finalize() {
		if (!this.b()) {
			this.a()
		}
		// super.finalize(): Object.finalize() is empty and cannot be called from Kotlin
	}

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "b", descriptor = "()Z")
	actual fun b(): Boolean {
		return this.peer == 0L
	}

	@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "setPeer", descriptor = "(J)V")
	private fun setPeer(@OriginalArg(0) peer: Long) {
		this.peer = peer
	}

	companion object {

		init {
			init()
		}

		@OriginalMember(owner = "client!jagtheora/misc/SimplePeer", name = "init", descriptor = "()V")
		@JvmStatic private external fun init()
	}
}
