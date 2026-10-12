import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object SseToolkitFactory {

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "(IILclient!d;Ljava/awt/Canvas;I)Lclient!ha;")
	@JvmStatic
	fun createToolkit(@OriginalArg(1) height: Int, @OriginalArg(2) textureSource: MaterialProvider?, @OriginalArg(3) canvas: Canvas?, @OriginalArg(4) width: Int): Toolkit {
		return oa(canvas, textureSource, width, height)
	}
}
