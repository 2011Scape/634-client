package jaclib.peer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/peer/Peer")
abstract class Peer @OriginalMember(owner = "client!jaclib/peer/Peer", name = "<init>", descriptor = "()V") protected constructor() {

	@OriginalMember(owner = "client!jaclib/peer/Peer", name = "reference", descriptor = "Lclient!jaclib/peer/PeerReference;")
	@JvmField
	protected var reference: PeerReference? = null

	@OriginalMember(owner = "client!jaclib/peer/Peer", name = "a", descriptor = "(Z)Z")
	fun a(@OriginalArg(0) arg0: Boolean): Boolean {
		return this.reference!!.a((-67).toByte())
	}

	@OriginalMember(owner = "client!jaclib/peer/Peer", name = "a", descriptor = "()J")
	open fun a(): Long {
		return this.reference!!.a(false)
	}

	companion object {

		init {
			init(PeerReference::class.java)
		}

		@OriginalMember(owner = "client!jaclib/peer/Peer", name = "init", descriptor = "(Ljava/lang/Class;)V")
		@JvmStatic private external fun init(@OriginalArg(0) arg0: Class<*>?)
	}
}
