import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!a")
actual class a : SoftwareObject {

	@OriginalMember(owner = "client!a", name = "nativeid", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!a", name = "h", descriptor = "Ljava/lang/Runnable;")
	@JvmField
	actual var aRunnable1: Runnable? = null

	@OriginalMember(owner = "client!a", name = "k", descriptor = "Lclient!oa;")
	private val anOa1: oa?

	@OriginalMember(owner = "client!a", name = "b", descriptor = "Lclient!i;")
	private val anI2: i

	@OriginalMember(owner = "client!a", name = "g", descriptor = "Lclient!i;")
	private val anI7: i

	@OriginalMember(owner = "client!a", name = "d", descriptor = "Lclient!i;")
	private val anI4: i

	@OriginalMember(owner = "client!a", name = "l", descriptor = "Lclient!i;")
	private val anI10: i

	@OriginalMember(owner = "client!a", name = "a", descriptor = "Lclient!i;")
	private val anI1: i

	@OriginalMember(owner = "client!a", name = "f", descriptor = "Lclient!i;")
	private val anI6: i

	@OriginalMember(owner = "client!a", name = "c", descriptor = "Lclient!i;")
	private val anI3: i

	@OriginalMember(owner = "client!a", name = "e", descriptor = "Lclient!i;")
	private val anI5: i

	@OriginalMember(owner = "client!a", name = "i", descriptor = "Lclient!i;")
	private val anI8: i

	@OriginalMember(owner = "client!a", name = "j", descriptor = "Lclient!i;")
	private val anI9: i

	@OriginalMember(owner = "client!a", name = "<init>", descriptor = "(Lclient!oa;II)V")
	actual constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.anOa1 = arg0
		this.anI2 = i(this.anOa1)
		this.anI7 = i(this.anOa1)
		this.anI4 = i(this.anOa1)
		this.anI10 = i(this.anOa1)
		this.anI1 = i(this.anOa1)
		this.anI6 = i(this.anOa1)
		this.anI3 = i(this.anOa1)
		this.anI5 = i(this.anOa1)
		this.anI8 = i(this.anOa1)
		this.anI9 = i(this.anOa1)
		this.HA(this.nativeid, arg0, arg1, arg2)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!s;II)V")
	actual fun method1(@OriginalArg(0) ground: Ground?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.H(this.nativeid, (ground as t).nativeid, arg1, arg2)
	}

	@OriginalMember(owner = "client!a", name = "R", descriptor = "(JJIIJZ)Z")
	private external fun R(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Long, @OriginalArg(5) arg5: Boolean): Boolean

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;IILclient!bca;ZI)Z")
	actual fun method2(@OriginalArg(0) model: Model?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) matrix: Matrix?, @OriginalArg(4) arg4: Boolean, @OriginalArg(5) arg5: Int): Boolean {
		return this.n(this.nativeid, (model as i).nativeid, arg1, arg2, (matrix as ja).nativeid, arg4, arg5)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;Lclient!ka;IIIZ)V")
	actual fun method3(@OriginalArg(0) model: Model?, @OriginalArg(1) model2: Model?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		this.r(this.nativeid, (model as i).nativeid, (model2 as i).nativeid, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!a", name = "E", descriptor = "(JZ)V")
	private external fun E(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Boolean)

	@OriginalMember(owner = "client!a", name = "e", descriptor = "(JJ[IJ)V")
	private external fun e(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Long)

	@OriginalMember(owner = "client!a", name = "W", descriptor = "(J)V")
	private external fun W(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!a", name = "finalize", descriptor = "()V")
	actual fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;Lclient!bca;[II)V")
	actual fun method4(@OriginalArg(0) model: Model?, @OriginalArg(1) matrix: Matrix?, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int) {
		this.UA(this.nativeid, (model as i).nativeid, (matrix as ja).nativeid, arg2, arg3)
	}

	@OriginalMember(owner = "client!a", name = "n", descriptor = "(JJIIJZI)Z")
	private external fun n(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Long, @OriginalArg(5) arg5: Boolean, @OriginalArg(6) arg6: Int): Boolean

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;IILclient!bca;Z)Z")
	actual fun method5(@OriginalArg(0) model: Model?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) matrix: Matrix?, @OriginalArg(4) arg4: Boolean): Boolean {
		return this.R(this.nativeid, (model as i).nativeid, arg1, arg2, (matrix as ja).nativeid, arg4)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!s;III)V")
	actual fun method6(@OriginalArg(0) ground: Ground?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		this.Z(this.nativeid, (ground as t).nativeid, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!a", name = "w", descriptor = "(Z)V")
	actual override fun w(@OriginalArg(0) arg0: Boolean) {
		this.E(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!s;IIIIIII[[Z)V")
	actual fun method7(@OriginalArg(0) ground: Ground?, @OriginalArg(4) arg1: Int, @OriginalArg(5) arg2: Int, @OriginalArg(6) arg3: Int, @OriginalArg(7) arg4: Int, @OriginalArg(8) arg5: Array<BooleanArray?>?) {
		this.ta(this.nativeid, (ground as t).nativeid, 0, 0, 1024, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;Lclient!bca;[III)V")
	actual fun method8(@OriginalArg(0) model: Model?, @OriginalArg(1) matrix: Matrix?, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		this.f(this.nativeid, (model as i).nativeid, (matrix as ja).nativeid, arg2, arg3, arg4)
	}

	@OriginalMember(owner = "client!a", name = "na", descriptor = "(JLclient!ha;IIIIIII)V")
	private external fun na(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Toolkit?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

	@OriginalMember(owner = "client!a", name = "O", descriptor = "(JLclient!ha;[I[I[I[SI)V")
	private external fun O(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Toolkit?, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: ShortArray?, @OriginalArg(6) arg6: Int)

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ha;IIIIIII)V")
	actual fun method9(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int) {
		this.na(this.nativeid, toolkit, arg1, arg2, arg3, arg4, arg5, arg6, 0)
	}

	@OriginalMember(owner = "client!a", name = "f", descriptor = "(JJJ[III)V")
	private external fun f(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!a", name = "r", descriptor = "(JJJIIIZ)V")
	private external fun r(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Boolean)

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ka;[ILclient!bca;)V")
	actual fun method10(@OriginalArg(0) model: Model?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) matrix: Matrix?) {
		this.e(this.nativeid, (model as i).nativeid, arg1, (matrix as ja).nativeid)
	}

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!i;BIZ)Lclient!ka;")
	actual fun method11(@OriginalArg(0) arg0: i?, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Boolean): Model? {
		@Pc(8) val local8: i
		@Pc(5) val local5: i
		if (arg1.toInt() == 1) {
			local5 = this.anI6
			local8 = this.anI2
		} else if (arg1.toInt() == 2) {
			local5 = this.anI3
			local8 = this.anI7
		} else if (arg1.toInt() == 3) {
			local5 = this.anI5
			local8 = this.anI4
		} else if (arg1.toInt() == 4) {
			local5 = this.anI8
			local8 = this.anI10
		} else if (arg1.toInt() == 5) {
			local5 = this.anI9
			local8 = this.anI1
		} else {
			local5 = i(this.anOa1)
			local8 = local5
		}
		arg0!!.ZA(local8, local5, arg2, arg1.toInt() != 0, arg3)
		local8.aClass227Array2 = arg0.aClass227Array2
		local8.aClass87Array2 = arg0.aClass87Array2
		return local8
	}

	@OriginalMember(owner = "client!a", name = "ta", descriptor = "(JJIIIIIII[[Z)V")
	private external fun ta(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Array<BooleanArray?>?)

	@OriginalMember(owner = "client!a", name = "Z", descriptor = "(JJIII)V")
	private external fun Z(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!a", name = "a", descriptor = "()V")
	private fun method12() {
		this.W(this.nativeid)
	}

	@OriginalMember(owner = "client!a", name = "b", descriptor = "()V")
	actual fun method13() {
		this.aRunnable1 = Thread.currentThread()
		this.method16()
	}

	@OriginalMember(owner = "client!a", name = "M", descriptor = "(J)V")
	private external fun M(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!a", name = "H", descriptor = "(JJII)V")
	private external fun H(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!a", name = "UA", descriptor = "(JJJ[II)V")
	private external fun UA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!a", name = "a", descriptor = "(Lclient!ha;[I[I[I[SI)V")
	actual fun method14(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: ShortArray?, @OriginalArg(5) arg5: Int) {
		this.O(this.nativeid, toolkit, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!a", name = "HA", descriptor = "(JLclient!ha;II)V")
	private external fun HA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Toolkit?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!a", name = "c", descriptor = "()V")
	actual fun method15() {
		this.aRunnable1 = null
		this.method12()
	}

	@OriginalMember(owner = "client!a", name = "d", descriptor = "()V")
	private fun method16() {
		this.M(this.nativeid)
	}
}

