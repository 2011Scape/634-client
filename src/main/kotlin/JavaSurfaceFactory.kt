import com.jagex.game.runetek5.cache.node.JavaSurface
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object JavaSurfaceFactory {

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(IILjava/awt/Canvas;I)Lclient!pi;")
	@JvmStatic
	fun createSurface(@OriginalArg(1) height: Int, @OriginalArg(2) canvas: Canvas?, @OriginalArg(3) width: Int): JavaSurface {
		try {
			@Pc(11) val class2: Class<*> = Class.forName("JavaDefaultSurface")
			@Pc(15) val javaSurface: JavaSurface = class2.getDeclaredConstructor().newInstance() as JavaSurface
			javaSurface.initialize(canvas, width, height)
			return javaSurface
		} catch (@Pc(24) ex: Throwable) {
			@Pc(28) val javaFallbackSurface: JavaFallbackSurface = JavaFallbackSurface()
			javaFallbackSurface.initialize(canvas, width, height)
			return javaFallbackSurface
		}
	}
}
