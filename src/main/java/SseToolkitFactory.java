import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import java.awt.Canvas;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class SseToolkitFactory {

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "(IILclient!d;Ljava/awt/Canvas;I)Lclient!ha;")
	public static Toolkit createToolkit(@OriginalArg(1) int height, @OriginalArg(2) MaterialProvider textureSource, @OriginalArg(3) Canvas canvas, @OriginalArg(4) int width) {
		return new oa(canvas, textureSource, width, height);
	}
}
