package jaclib.peer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jaclib/peer/hb")
public final class hb {

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "b", descriptor = "Lclient!jaclib/peer/PeerReference;")
	private PeerReference b;

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "c", descriptor = "Lclient!jaclib/peer/PeerReference;")
	private PeerReference c;

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "Ljava/lang/ref/ReferenceQueue;")
	public final ReferenceQueue a = new ReferenceQueue();

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(B)V")
	public void a(@OriginalArg(0) byte arg0) {
		this.a((int) 21492);
		while (this.b != null) {
			this.a(19236, this.b);
		}
		this.a((int) 21492);
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(Lclient!jaclib/peer/PeerReference;I)V")
	public void a(@OriginalArg(0) PeerReference peerReference, @OriginalArg(1) int arg1) {
		peerReference.a = this.b;
		peerReference.b = null;
		if (this.c == null) {
			this.c = peerReference;
		} else {
			this.b.b = peerReference;
		}
		this.b = peerReference;
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(I)V")
	private void a(@OriginalArg(0) int arg0) {
		while (true) {
			@Pc(3) Reference reference = this.a.poll();
			if (reference == null) {
				return;
			}
			@Pc(9) PeerReference peerReference = (PeerReference) reference;
			this.a(19236, peerReference);
		}
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "b", descriptor = "(B)V")
	public void b(@OriginalArg(0) byte arg0) {
		this.a(arg0 + 21574);
	}

	@OriginalMember(owner = "client!jaclib/peer/hb", name = "a", descriptor = "(ILclient!jaclib/peer/PeerReference;)V")
	private void a(@OriginalArg(0) int arg0, @OriginalArg(1) PeerReference peerReference) {
		peerReference.a(false);
		if (peerReference == this.b) {
			this.b = peerReference.a;
		}
		if (this.c == peerReference) {
			this.c = peerReference.b;
		}
		if (peerReference.b != null) {
			peerReference.b.a = peerReference.a;
		}
		if (peerReference.a != null) {
			peerReference.a.b = peerReference.b;
		}
	}
}
