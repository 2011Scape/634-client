import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object JavaToolkitFactory {

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(ZIILjava/awt/Canvas;Lclient!d;)Lclient!ha;")
	@JvmStatic
	fun createToolkit(@OriginalArg(1) screenHeight: Int, @OriginalArg(2) screenWidth: Int, @OriginalArg(3) canvas: Canvas?, @OriginalArg(4) textureSource: MaterialProvider?): Toolkit {
		return JavaToolkit(canvas, textureSource, screenWidth, screenHeight)
	}
}
