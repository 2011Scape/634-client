package jaclib.peer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/peer/IUnknown")
open class IUnknown @OriginalMember(owner = "client!jaclib/peer/IUnknown", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V") constructor(@OriginalArg(0) arg0: hb?) : Peer() {

	init {
		this.reference = IUnknownReference(this, arg0)
	}

	@OriginalMember(owner = "client!jaclib/peer/IUnknown", name = "AddRef", descriptor = "()J")
	private external fun AddRef(): Long

	@OriginalMember(owner = "client!jaclib/peer/IUnknown", name = "a", descriptor = "(I)J")
	fun a(@OriginalArg(0) arg0: Int): Long {
		return super.a()
	}
}
