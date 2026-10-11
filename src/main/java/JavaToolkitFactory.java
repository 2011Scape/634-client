import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import java.awt.Canvas;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class JavaToolkitFactory {

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(ZIILjava/awt/Canvas;Lclient!d;)Lclient!ha;")
	public static Toolkit createToolkit(@OriginalArg(1) int screenHeight, @OriginalArg(2) int screenWidth, @OriginalArg(3) Canvas canvas, @OriginalArg(4) MaterialProvider textureSource) {
		return new JavaToolkit(canvas, textureSource, screenWidth, screenHeight);
	}
}
