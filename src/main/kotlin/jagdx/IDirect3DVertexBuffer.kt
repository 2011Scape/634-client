package jagdx

import jaclib.memory.Source
import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DVertexBuffer")
class IDirect3DVertexBuffer : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "b", descriptor = "I")
	@JvmField
	var b: Int = 0

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "_Update", descriptor = "(JIII)Z")
	private external fun _Update(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int): Boolean

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIII)Z")
	fun a(@OriginalArg(0) source: Source?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int): Boolean {
		if (source == null || arg3 > source.getSize() + arg1) {
			throw fda("")
		} else if (arg3 > arg2 + this.b) {
			throw fda("")
		} else {
			return this._Update(arg1.toLong() + source.getAddress(), arg2, arg3, arg4)
		}
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "Unlock", descriptor = "()I")
	external fun Unlock(): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "a", descriptor = "()J")
	override fun a(): Long {
		this.b = 0
		return super.a()
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DVertexBuffer", name = "Lock", descriptor = "(IIILclient!jagdx/GeometryBuffer;)I")
	external fun Lock(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: GeometryBuffer?): Int
}
