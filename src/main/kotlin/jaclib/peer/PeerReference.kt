package jaclib.peer

import java.lang.ref.WeakReference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jaclib/peer/PeerReference")
abstract class PeerReference @OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "<init>", descriptor = "(Lclient!jaclib/peer/Peer;Lclient!jaclib/peer/hb;)V") protected constructor(@OriginalArg(0) peer: Peer?, @OriginalArg(1) arg1: hb?) : WeakReference<Any?>(peer, arg1!!.a) {

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "a", descriptor = "Lclient!jaclib/peer/PeerReference;")
	@JvmField
	var a: PeerReference? = null

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "b", descriptor = "Lclient!jaclib/peer/PeerReference;")
	@JvmField
	var b: PeerReference? = null

	init {
		arg1!!.a(this, -22143)
	}

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "releasePeer", descriptor = "(J)J")
	protected abstract fun releasePeer(@OriginalArg(0) arg0: Long): Long

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "a", descriptor = "(B)Z")
	fun a(@OriginalArg(0) arg0: Byte): Boolean {
		return this.peer != 0L
	}

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "setPeer", descriptor = "(J)V")
	private fun setPeer(@OriginalArg(0) peer: Long) {
		this.a(false)
		this.peer = peer
	}

	@OriginalMember(owner = "client!jaclib/peer/PeerReference", name = "a", descriptor = "(Z)J")
	fun a(@OriginalArg(0) arg0: Boolean): Long {
		@Pc(11) val local11: Long
		if (this.peer == 0L) {
			local11 = 0L
		} else {
			local11 = this.releasePeer(this.peer)
			this.peer = 0L
		}
		return local11
	}
}
