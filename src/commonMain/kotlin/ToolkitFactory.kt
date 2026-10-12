import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.statics.D3DToolkitFactory
import awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import kotlin.jvm.JvmStatic
import kotlin.jvm.Synchronized

object ToolkitFactory {

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIILclient!in;ILclient!d;Ljava/awt/Canvas;I)Lclient!ha;")
	@JvmStatic
	@Synchronized
	fun createToolkit(@OriginalArg(0) multiSamples: Int, @OriginalArg(1) height: Int, @OriginalArg(2) width: Int, @OriginalArg(3) shaders: Js5ResourceProvider?, @OriginalArg(5) textureSource: MaterialProvider?, @OriginalArg(6) canvas: Canvas?, @OriginalArg(7) toolkitType: Int): Toolkit? {
		if (toolkitType == ToolkitType.JAVA) {
			return JavaToolkitFactory.createToolkit(height, width, canvas, textureSource)
		} else if (toolkitType == ToolkitType.SSE) {
			return SseToolkitFactory.createToolkit(height, textureSource, canvas, width)
		} else if (toolkitType == ToolkitType.GL) {
			return GlToolkit.create(multiSamples, canvas, textureSource)
		} else if (toolkitType == ToolkitType.GLX) {
			return GlxToolkit.createToolkit(canvas, textureSource, shaders, multiSamples)
		} else if (toolkitType == ToolkitType.D3D) {
			return D3DToolkitFactory.createD3DToolkit(multiSamples, textureSource, shaders, canvas)
		} else {
			throw IllegalArgumentException("UM")
		}
	}
}
