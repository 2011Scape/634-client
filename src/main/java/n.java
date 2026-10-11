import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!n")
public final class n extends AbstractFontRenderer implements SoftwareObject {

	@OriginalMember(owner = "client!n", name = "nativeid", descriptor = "J")
	private long nativeid;

	@OriginalMember(owner = "client!n", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;Lclient!oea;[Lclient!rh;[Lclient!mi;)V")
	public n(@OriginalArg(0) oa arg0, @OriginalArg(1) ya arg1, @OriginalArg(2) FontMetrics fontMetrics, @OriginalArg(3) SpriteType[] indexedImages, @OriginalArg(4) NativeSprite[] sprites) {
		super(arg0, fontMetrics);
		@Pc(7) byte[][] local7 = new byte[indexedImages.length][];
		@Pc(11) int[] local11 = new int[indexedImages.length];
		@Pc(15) int[] local15 = new int[indexedImages.length];
		@Pc(19) int[] local19 = new int[indexedImages.length];
		@Pc(23) int[] local23 = new int[indexedImages.length];
		for (@Pc(25) int i = 0; i < indexedImages.length; i++) {
			local7[i] = indexedImages[i].pixels;
			local11[i] = indexedImages[i].width;
			local15[i] = indexedImages[i].height;
			local19[i] = indexedImages[i].minX;
			local23[i] = indexedImages[i].minY;
		}
		this.S(arg0, arg1, local7, indexedImages[0].pallete, local11, local15, local19, local23);
	}

	@OriginalMember(owner = "client!n", name = "S", descriptor = "(Lclient!oa;Lclient!ya;[[B[I[I[I[I[I)V")
	private native void S(@OriginalArg(0) oa arg0, @OriginalArg(1) ya arg1, @OriginalArg(2) byte[][] arg2, @OriginalArg(3) int[] arg3, @OriginalArg(4) int[] arg4, @OriginalArg(5) int[] arg5, @OriginalArg(6) int[] arg6, @OriginalArg(7) int[] arg7);

	@OriginalMember(owner = "client!n", name = "w", descriptor = "(Z)V")
	public native void w(@OriginalArg(0) boolean arg0);

	@OriginalMember(owner = "client!n", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this);
		}
	}

	@OriginalMember(owner = "client!n", name = "fa", descriptor = "(CIIIZ)V")
	public native void fa(@OriginalArg(0) char arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) boolean arg4);

	@OriginalMember(owner = "client!n", name = "PA", descriptor = "(CIIIZLclient!aa;II)V")
	private native void PA(@OriginalArg(0) char arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) boolean arg4, @OriginalArg(5) ComponentSurface arg5, @OriginalArg(6) int arg6, @OriginalArg(7) int arg7);

	@OriginalMember(owner = "client!n", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	@Override
	public void renderSymbol(@OriginalArg(0) char arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) boolean arg4, @OriginalArg(5) ComponentSurface clippingMask, @OriginalArg(6) int arg6, @OriginalArg(7) int arg7) {
		this.PA(arg0, arg1, arg2, arg3, arg4, clippingMask, arg6, arg7);
	}
}
