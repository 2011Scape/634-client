package jaclib.peer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/peer/IUnknownReference")
class IUnknownReference @OriginalMember(owner = "client!jaclib/peer/IUnknownReference", name = "<init>", descriptor = "(Lclient!jaclib/peer/IUnknown;Lclient!jaclib/peer/hb;)V") constructor(@OriginalArg(0) iUnknown: IUnknown?, @OriginalArg(1) arg1: hb?) : PeerReference(iUnknown, arg1) {

	@OriginalMember(owner = "client!jaclib/peer/IUnknownReference", name = "releasePeer", descriptor = "(J)J")
	protected external override fun releasePeer(@OriginalArg(0) arg0: Long): Long
}
