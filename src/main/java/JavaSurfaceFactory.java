import com.jagex.game.runetek5.cache.node.JavaSurface;
import java.awt.Canvas;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class JavaSurfaceFactory {

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(IILjava/awt/Canvas;I)Lclient!pi;")
	public static JavaSurface createSurface(@OriginalArg(1) int height, @OriginalArg(2) Canvas canvas, @OriginalArg(3) int width) {
		try {
			@Pc(11) Class class2 = Class.forName("JavaDefaultSurface");
			@Pc(15) JavaSurface javaSurface = (JavaSurface) class2.getDeclaredConstructor().newInstance();
			javaSurface.initialize(canvas, width, height);
			return javaSurface;
		} catch (@Pc(24) Throwable ex) {
			@Pc(28) JavaFallbackSurface javaFallbackSurface = new JavaFallbackSurface();
			javaFallbackSurface.initialize(canvas, width, height);
			return javaFallbackSurface;
		}
	}
}
