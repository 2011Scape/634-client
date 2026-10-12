import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!t")
actual class t : Ground, SoftwareObject {

	@OriginalMember(owner = "client!t", name = "nativeid", descriptor = "J")
	@JvmField
	actual var nativeid: Long = 0L

	@OriginalMember(owner = "client!t", name = "k", descriptor = "Lclient!uh;")
	private val aClass341_63: Deque = Deque()

	@OriginalMember(owner = "client!t", name = "m", descriptor = "I")
	private var anInt9622: Int = -1

	@OriginalMember(owner = "client!t", name = "l", descriptor = "Lclient!oa;")
	private val anOa3: oa?

	@OriginalMember(owner = "client!t", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;II[[I[[IIII)V")
	actual constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) tileHeights: Array<IntArray?>?, @OriginalArg(5) arg5: Array<IntArray?>?, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int) : super(arg2, arg3, arg6, tileHeights) {
		this.anOa3 = arg0
		this.ga(this.anOa3, arg1, arg2, arg3, super.tileHeights, arg5, arg6, arg7, arg8)
	}

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(III[[ZZII)V")
	actual override fun renderFloor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Array<BooleanArray?>?, @OriginalArg(4) arg4: Boolean, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int) {
		this.anInt9622 = arg5
		@Pc(4) var local4: Int = 0
		@Pc(10) val local10: FloatArray = FloatArray(this.aClass341_63.size())
		@Pc(16) var pointLight: PointLight? = this.aClass341_63.first() as PointLight?
		while (pointLight != null) {
			local10[local4++] = pointLight.getIntensity()
			pointLight = this.aClass341_63.next() as PointLight?
		}
		this.q(local10)
		@Pc(37) var i: Int = 0
		while (i < arg2 + arg2) {
			@Pc(40) var j: Int = 0
			while (j < arg2 + arg2) {
				if (arg3!![i]!![j]) {
					@Pc(53) val local53: Int = arg0 + i - arg2
					@Pc(59) val local59: Int = arg1 + j - arg2
					if (local53 >= 0 && local53 < super.width && local59 >= 0 && local59 < super.length) {
						this.renderTile(local53, local59)
					}
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!t", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	actual external override fun wa(@OriginalArg(0) arg0: Shadow?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean)

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(II)V")
	actual override fun renderTile(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
		if (this.anInt9622 < 0) {
			this.anOa3!!.method6405()!!.method1(this, arg0, arg1)
		} else {
			this.anOa3!!.method6405()!!.method6(this, arg0, arg1, this.anInt9622)
		}
	}

	@OriginalMember(owner = "client!t", name = "w", descriptor = "(Z)V")
	actual external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	actual override fun addTile(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: IntArray?, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: IntArray?, @OriginalArg(10) arg10: IntArray?, @OriginalArg(11) arg11: IntArray?, @OriginalArg(12) arg12: IntArray?, @OriginalArg(13) arg13: Int, @OriginalArg(14) arg14: Int, @OriginalArg(15) arg15: Int) {
		@Pc(1) var local1: Boolean = false
		if (arg9 != null) {
			@Pc(7) var i: Int = 0
			while (i < arg9.size) {
				@Pc(12) val local12: Int = arg9[i]
				if (local12 != -1) {
					local1 = true
					break
				}
				i++
			}
		}
		@Pc(26) val length: Int = arg9!!.size
		@Pc(31) val local31: IntArray = IntArray(length * 3)
		@Pc(36) val local36: IntArray = IntArray(length * 3)
		@Pc(41) val local41: IntArray = IntArray(length * 3)
		@Pc(46) val local46: IntArray = IntArray(length * 3)
		@Pc(51) val local51: IntArray = IntArray(length * 3)
		@Pc(60) val local60: IntArray? = if (arg10 == null) null else IntArray(length * 3)
		@Pc(69) val local69: IntArray? = if (arg3 == null) null else IntArray(length * 3)
		@Pc(78) val local78: IntArray? = if (arg5 == null) null else IntArray(length * 3)
		@Pc(80) var local80: Int = 0
		@Pc(82) var j: Int = 0
		while (j < length) {
			@Pc(87) val local87: Int = arg6!![j]
			@Pc(91) val local91: Int = arg7!![j]
			@Pc(95) val local95: Int = arg8!![j]
			local31[local80] = arg2!![local87]
			local36[local80] = arg4!![local87]
			local41[local80] = arg9[j]
			local46[local80] = arg11!![j]
			local51[local80] = arg12!![j]
			if (arg10 != null) {
				local60!![local80] = arg10[j]
			}
			if (arg3 != null) {
				local69!![local80] = arg3[local87]
			}
			if (arg5 != null) {
				local78!![local80] = arg5[local87]
			}
			local80++
			local31[local80] = arg2!![local91]
			local36[local80] = arg4!![local91]
			local41[local80] = arg9[j]
			local46[local80] = arg11!![j]
			local51[local80] = arg12!![j]
			if (arg10 != null) {
				local60!![local80] = arg10[j]
			}
			if (arg3 != null) {
				local69!![local80] = arg3[local91]
			}
			if (arg5 != null) {
				local78!![local80] = arg5[local91]
			}
			local80++
			local31[local80] = arg2!![local95]
			local36[local80] = arg4!![local95]
			local41[local80] = arg9[j]
			local46[local80] = arg11!![j]
			local51[local80] = arg12!![j]
			if (arg10 != null) {
				local60!![local80] = arg10[j]
			}
			if (arg3 != null) {
				local69!![local80] = arg3[local95]
			}
			if (arg5 != null) {
				local78!![local80] = arg5[local95]
			}
			local80++
			j++
		}
		if (local1 || local60 != null) {
			this.U(arg0, arg1, local31, local69, local36, local78, local41, local60, local46, local51, arg13, arg14, arg15, false)
		}
	}

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(Lclient!ab;[I)V")
	actual override fun addLight(@OriginalArg(0) pointLight: PointLight?, @OriginalArg(1) arg1: IntArray?) {
		this.aClass341_63.addLast(pointLight)
		this.V(pointLight!!.hashCode(), pointLight.getX(), pointLight.getY(), pointLight.getZ(), pointLight.getRadius(), pointLight.getColour(), arg1)
	}

	@OriginalMember(owner = "client!t", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	actual external override fun CA(@OriginalArg(0) arg0: Shadow?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean)

	@OriginalMember(owner = "client!t", name = "q", descriptor = "([F)V")
	private external fun q(@OriginalArg(0) arg0: FloatArray?)

	@OriginalMember(owner = "client!t", name = "ka", descriptor = "(III)V")
	actual external override fun ka(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	actual override fun method8002(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int): Boolean {
		return true
	}

	@OriginalMember(owner = "client!t", name = "V", descriptor = "(IIIIII[I)V")
	private external fun V(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: IntArray?)

	@OriginalMember(owner = "client!t", name = "YA", descriptor = "()V")
	actual external override fun YA()

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(III[[ZZI)V")
	actual override fun renderTiles(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Array<BooleanArray?>?, @OriginalArg(4) arg4: Boolean, @OriginalArg(5) arg5: Int) {
		this.anInt9622 = -1
		@Pc(4) var local4: Int = 0
		@Pc(10) val local10: FloatArray = FloatArray(this.aClass341_63.size())
		@Pc(16) var pointLight: PointLight? = this.aClass341_63.first() as PointLight?
		while (pointLight != null) {
			local10[local4++] = pointLight.getIntensity()
			pointLight = this.aClass341_63.next() as PointLight?
		}
		this.q(local10)
		@Pc(37) var i: Int = 0
		while (i < arg2 + arg2) {
			@Pc(40) var j: Int = 0
			while (j < arg2 + arg2) {
				if (arg3!![i]!![j]) {
					@Pc(53) val local53: Int = arg0 + i - arg2
					@Pc(59) val local59: Int = arg1 + j - arg2
					if (local53 >= 0 && local53 < super.width && local59 >= 0 && local59 < super.length) {
						this.renderTile(local53, local59)
					}
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!t", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	actual external override fun fa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Shadow?): Shadow?

	@OriginalMember(owner = "client!t", name = "finalize", descriptor = "()V")
	actual fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!t", name = "ga", descriptor = "(Lclient!oa;Lclient!ya;II[[I[[IIII)V")
	private external fun ga(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Array<IntArray?>?, @OriginalArg(5) arg5: Array<IntArray?>?, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

	@OriginalMember(owner = "client!t", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	actual external override fun U(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: IntArray?, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: IntArray?, @OriginalArg(10) arg10: Int, @OriginalArg(11) arg11: Int, @OriginalArg(12) arg12: Int, @OriginalArg(13) arg13: Boolean)

	@OriginalMember(owner = "client!t", name = "a", descriptor = "(IIIIIII[[Z)V")
	actual override fun drawMinimap(@OriginalArg(3) arg0: Int, @OriginalArg(4) arg1: Int, @OriginalArg(5) arg2: Int, @OriginalArg(6) arg3: Int, @OriginalArg(7) arg4: Array<BooleanArray?>?) {
		this.anOa3!!.method6405()!!.method7(this, arg0, arg1, arg2, arg3, arg4)
	}
}

