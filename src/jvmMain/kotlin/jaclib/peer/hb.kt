package jaclib.peer

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jaclib/peer/hb")
class hb {

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "b", descriptor = "Lclient!jaclib/peer/PeerReference;")
	private var b: PeerReference? = null

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "c", descriptor = "Lclient!jaclib/peer/PeerReference;")
	private var c: PeerReference? = null

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "Ljava/lang/ref/ReferenceQueue;")
	@JvmField
	val a: ReferenceQueue<Any?> = ReferenceQueue<Any?>()

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(B)V")
	fun a(@OriginalArg(0) arg0: Byte) {
		this.a(21492)
		while (this.b != null) {
			this.a(19236, this.b)
		}
		this.a(21492)
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(Lclient!jaclib/peer/PeerReference;I)V")
	fun a(@OriginalArg(0) peerReference: PeerReference?, @OriginalArg(1) arg1: Int) {
		peerReference!!.a = this.b
		peerReference.b = null
		if (this.c == null) {
			this.c = peerReference
		} else {
			this.b!!.b = peerReference
		}
		this.b = peerReference
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(I)V")
	private fun a(@OriginalArg(0) arg0: Int) {
		while (true) {
			@Pc(3) val reference: Reference<*>? = this.a.poll()
			if (reference == null) {
				return
			}
			@Pc(9) val peerReference: PeerReference = reference as PeerReference
			this.a(19236, peerReference)
		}
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "b", descriptor = "(B)V")
	fun b(@OriginalArg(0) arg0: Byte) {
		this.a(arg0 + 21574)
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(ILclient!jaclib/peer/PeerReference;)V")
	private fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) peerReference: PeerReference?) {
		peerReference!!.a(false)
		if (peerReference === this.b) {
			this.b = peerReference.a
		}
		if (this.c === peerReference) {
			this.c = peerReference.b
		}
		if (peerReference.b != null) {
			peerReference.b!!.a = peerReference.a
		}
		if (peerReference.a != null) {
			peerReference.a!!.b = peerReference.b
		}
	}
}
