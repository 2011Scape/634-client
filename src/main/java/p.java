import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;
import com.jagex.game.runetek5.statics.NativeBlitErrorTimer;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!p")
public final class p extends Node implements SoftwareObject {

	@OriginalMember(owner = "client!p", name = "nativeid", descriptor = "J")
	private long nativeid;

	@OriginalMember(owner = "client!p", name = "r", descriptor = "Ljava/awt/Canvas;")
	public Canvas canvas;

	@OriginalMember(owner = "client!p", name = "p", descriptor = "I")
	public int anInt7778;

	@OriginalMember(owner = "client!p", name = "s", descriptor = "I")
	public int anInt7779;

	@OriginalMember(owner = "client!p", name = "<init>", descriptor = "(Lclient!oa;Ljava/awt/Canvas;II)V")
	public p(@OriginalArg(0) oa arg0, @OriginalArg(1) Canvas canvas, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
		this.canvas = canvas;
		this.anInt7778 = arg2;
		this.anInt7779 = arg3;
		this.sa(arg0, this.canvas, arg2, arg3);
	}

	@OriginalMember(owner = "client!p", name = "sa", descriptor = "(Lclient!oa;Ljava/awt/Canvas;II)V")
	private native void sa(@OriginalArg(0) oa arg0, @OriginalArg(1) Canvas arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!p", name = "w", descriptor = "(Z)V")
	public native void w(@OriginalArg(0) boolean arg0);

	@OriginalMember(owner = "client!p", name = "H", descriptor = "(IIII)V")
	private native void H(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!p", name = "oa", descriptor = "(Ljava/awt/Canvas;II)V")
	private native void oa(@OriginalArg(0) Canvas arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2);

	@OriginalMember(owner = "client!p", name = "a", descriptor = "(Ljava/lang/Exception;)V")
	private void method6663(@OriginalArg(0) Exception exception) {
		if (!NativeBlitErrorTimer.blitFailed) {
			NativeBlitErrorTimer.firstErrorTime = Utils.currentTimeMillis();
			NativeBlitErrorTimer.blitFailed = true;
		} else if (Utils.currentTimeMillis() - NativeBlitErrorTimer.firstErrorTime < 30000L) {
			this.canvas.repaint();
		} else {
			throw new RuntimeException(exception.getMessage());
		}
	}

	@OriginalMember(owner = "client!p", name = "K", descriptor = "(IIIIII)V")
	private native void K(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5);

	@OriginalMember(owner = "client!p", name = "c", descriptor = "(II)V")
	public void method6664(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) {
		try {
			synchronized (this.canvas.getTreeLock()) {
				@Pc(9) Dimension dimension = this.canvas.getSize();
				this.H(arg0, arg1, dimension.width, dimension.height);
				NativeBlitErrorTimer.blitFailed = false;
			}
		} catch (@Pc(26) Exception ex) {
			this.method6663(ex);
		}
	}

	@OriginalMember(owner = "client!p", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this);
		}
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	public void method6665(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
		try {
			synchronized (this.canvas.getTreeLock()) {
				for (@Pc(7) int i = 0; i < arg1; i++) {
					@Pc(12) Rectangle rectangle = rectangles[i];
					if (rectangle.width > 0 && rectangle.height > 0) {
						this.K(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arg2, arg3);
					}
				}
				NativeBlitErrorTimer.blitFailed = false;
			}
		} catch (@Pc(43) Exception ex) {
			this.method6663(ex);
		}
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	public void method6666(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2) {
		this.anInt7778 = arg1;
		this.anInt7779 = arg2;
		this.oa(canvas, arg1, arg2);
	}

	@OriginalMember(owner = "client!p", name = "a", descriptor = "()V")
	public void method6667() {
		this.w(true);
		this.nativeid = 0L;
		this.canvas = null;
	}
}
