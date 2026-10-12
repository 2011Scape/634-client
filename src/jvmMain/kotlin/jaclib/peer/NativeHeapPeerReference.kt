package jaclib.peer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/peer/NativeHeapPeerReference")
class NativeHeapPeerReference @OriginalMember(owner = "client!jaclib/peer/NativeHeapPeerReference", name = "<init>", descriptor = "(Lclient!jaclib/peer/uda;Lclient!jaclib/peer/hb;)V") constructor(@OriginalArg(0) peer: uda?, @OriginalArg(1) arg1: hb?) : PeerReference(peer, arg1) {

	@OriginalMember(owner = "client!jaclib/peer/NativeHeapPeerReference", name = "releasePeer", descriptor = "(J)J")
	protected external override fun releasePeer(@OriginalArg(0) arg0: Long): Long
}
