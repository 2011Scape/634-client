import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.statics.NativeBlitErrorTimer
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!p")
class p : Node, SoftwareObject {

	@OriginalMember(owner = "client!p", name = "nativeid", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!p", name = "r", descriptor = "Ljava/awt/Canvas;")
	@JvmField
	var canvas: Canvas? = null

	@OriginalMember(owner = "client!p", name = "p", descriptor = "I")
	@JvmField
	var anInt7778: Int = 0

	@OriginalMember(owner = "client!p", name = "s", descriptor = "I")
	@JvmField
	var anInt7779: Int = 0

	@OriginalMember(owner = "client!p", name = "<init>", descriptor = "(Lclient!oa;Ljava/awt/Canvas;II)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) canvas: Canvas?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		this.canvas = canvas
		this.anInt7778 = arg2
		this.anInt7779 = arg3
		this.sa(arg0, this.canvas, arg2, arg3)
	}

	@OriginalMember(owner = "client!p", name = "sa", descriptor = "(Lclient!oa;Ljava/awt/Canvas;II)V")
	private external fun sa(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Canvas?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!p", name = "w", descriptor = "(Z)V")
	external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!p", name = "H", descriptor = "(IIII)V")
	private external fun H(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!p", name = "oa", descriptor = "(Ljava/awt/Canvas;II)V")
	private external fun oa(@OriginalArg(0) arg0: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!p", name = "a", descriptor = "(Ljava/lang/Exception;)V")
	private fun method6663(@OriginalArg(0) exception: Exception?) {
		if (!NativeBlitErrorTimer.blitFailed) {
			NativeBlitErrorTimer.firstErrorTime = Utils.currentTimeMillis()
			NativeBlitErrorTimer.blitFailed = true
		} else if (Utils.currentTimeMillis() - NativeBlitErrorTimer.firstErrorTime < 30000L) {
			this.canvas!!.repaint()
		} else {
			throw RuntimeException(exception!!.message)
		}
	}

	@OriginalMember(owner = "client!p", name = "K", descriptor = "(IIIIII)V")
	private external fun K(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!p", name = "c", descriptor = "(II)V")
	fun method6664(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
		try {
			synchronized(this.canvas!!.treeLock) {
				@Pc(9) val dimension: Dimension = this.canvas!!.size
				this.H(arg0, arg1, dimension.width, dimension.height)
				NativeBlitErrorTimer.blitFailed = false
			}
		} catch (@Pc(26) ex: Exception) {
			this.method6663(ex)
		}
	}

	@OriginalMember(owner = "client!p", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	fun method6665(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		try {
			synchronized(this.canvas!!.treeLock) {
				@Pc(7) var i: Int = 0
				while (i < arg1) {
					@Pc(12) val rectangle: Rectangle = rectangles!![i]!!
					if (rectangle.width > 0 && rectangle.height > 0) {
						this.K(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arg2, arg3)
					}
					i++
				}
				NativeBlitErrorTimer.blitFailed = false
			}
		} catch (@Pc(43) ex: Exception) {
			this.method6663(ex)
		}
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	fun method6666(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.anInt7778 = arg1
		this.anInt7779 = arg2
		this.oa(canvas, arg1, arg2)
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "()V")
	fun method6667() {
		this.w(true)
		this.nativeid = 0L
		this.canvas = null
	}
}

