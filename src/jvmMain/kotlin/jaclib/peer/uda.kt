package jaclib.peer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/peer/uda")
actual open class uda @OriginalMember(owner = "client!jaclib/peer/uda", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V") actual constructor(@OriginalArg(0) arg0: hb?) : Peer() {

	init {
		this.reference = NativeHeapPeerReference(this, arg0)
	}
}
