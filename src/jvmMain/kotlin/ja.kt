import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ja")
actual class ja : Matrix, SoftwareObject {

	@OriginalMember(owner = "client!ja", name = "nativeid", descriptor = "J")
	@JvmField
	actual var nativeid: Long = 0L

	@OriginalMember(owner = "client!ja", name = "<init>", descriptor = "()V")
	actual constructor() {
		this.la()
	}

	@OriginalMember(owner = "client!ja", name = "la", descriptor = "()V")
	private external fun la()

	@OriginalMember(owner = "client!ja", name = "za", descriptor = "(JI)V")
	private external fun za(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "AA", descriptor = "(JZ)V")
	private external fun AA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Boolean)

	@OriginalMember(owner = "client!ja", name = "VA", descriptor = "(JI)V")
	private external fun VA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "NA", descriptor = "(JI)V")
	private external fun NA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(III)V")
	actual override fun translate(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.a(this.nativeid, arg0, arg1, arg2)
	}

	@OriginalMember(owner = "client!ja", name = "l", descriptor = "(JJ)V")
	private external fun l(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long)

	@OriginalMember(owner = "client!ja", name = "b", descriptor = "()V")
	actual override fun makeIdentity() {
		this.u(this.nativeid)
	}

	@OriginalMember(owner = "client!ja", name = "w", descriptor = "(Z)V")
	actual override fun w(@OriginalArg(0) arg0: Boolean) {
		this.AA(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "c", descriptor = "(III[I)V")
	actual override fun projectDirection(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IntArray?) {
		this.XA(this.nativeid, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "([I)V")
	actual override fun project(@OriginalArg(0) arg0: IntArray?) {
		this.w(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "w", descriptor = "(J[I)V")
	private external fun w(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: IntArray?)

	@OriginalMember(owner = "client!ja", name = "g", descriptor = "(I)V")
	actual override fun rotateAxisZ(@OriginalArg(0) arg0: Int) {
		this.za(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "XA", descriptor = "(JIII[I)V")
	private external fun XA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?)

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(IIIIII)V")
	actual override fun createCamera(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.P(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!ja", name = "t", descriptor = "(JI)V")
	private external fun t(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "d", descriptor = "(I)V")
	actual override fun rotateAxisX(@OriginalArg(0) arg0: Int) {
		this.J(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "u", descriptor = "(J)V")
	private external fun u(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!ja", name = "b", descriptor = "(III[I)V")
	actual override fun method4652(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IntArray?) {
		this.b(this.nativeid, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!ja", name = "m", descriptor = "(JI)V")
	private external fun m(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "FA", descriptor = "(JIII)V")
	private external fun FA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!ja", name = "e", descriptor = "(I)V")
	actual override fun makeRotationZ(@OriginalArg(0) arg0: Int) {
		this.NA(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "va", descriptor = "(JIII[I)V")
	private external fun va(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?)

	@OriginalMember(owner = "client!ja", name = "J", descriptor = "(JI)V")
	private external fun J(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(Lclient!bca;)V")
	actual override fun apply(@OriginalArg(0) matrix: Matrix?) {
		this.l(this.nativeid, (matrix as ja).nativeid)
	}

	@OriginalMember(owner = "client!ja", name = "b", descriptor = "(JIII[I)V")
	private external fun b(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?)

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(III[I)V")
	actual override fun projectRelative(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IntArray?) {
		this.va(this.nativeid, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!ja", name = "P", descriptor = "(JIIIIII)V")
	private external fun P(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int)

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(JIII)V")
	private external fun a(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!ja", name = "finalize", descriptor = "()V")
	actual fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!ja", name = "b", descriptor = "(I)V")
	actual override fun rotateAxisY(@OriginalArg(0) arg0: Int) {
		this.m(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "()Lclient!bca;")
	actual override fun copy(): Matrix? {
		@Pc(3) val local3: ja = ja()
		local3.apply(this)
		return local3
	}

	@OriginalMember(owner = "client!ja", name = "a", descriptor = "(I)V")
	actual override fun rotate(@OriginalArg(0) arg0: Int) {
		this.t(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!ja", name = "b", descriptor = "(III)V")
	actual override fun applyTranslation(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.FA(this.nativeid, arg0, arg1, arg2)
	}

	@OriginalMember(owner = "client!ja", name = "c", descriptor = "(I)V")
	actual override fun makeRotationX(@OriginalArg(0) arg0: Int) {
		this.VA(this.nativeid, arg0)
	}
}

