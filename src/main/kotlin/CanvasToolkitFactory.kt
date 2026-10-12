import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import java.awt.Canvas
import java.awt.Dimension
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object CanvasToolkitFactory {

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(ZILclient!d;ILjava/awt/Canvas;Lclient!in;)Lclient!ha;")
	@JvmStatic
	fun create(@OriginalArg(1) toolkitType: Int, @OriginalArg(2) textureSource: MaterialProvider?, @OriginalArg(3) multiSamples: Int, @OriginalArg(4) canvas: Canvas?, @OriginalArg(5) shaderResource: Js5ResourceProvider?): Toolkit? {
		@Pc(10) var width: Int = 0
		@Pc(12) var height: Int = 0
		if (canvas != null) {
			@Pc(17) val dimension: Dimension = canvas.getSize()
			height = dimension.height
			width = dimension.width
		}
		return ToolkitFactory.createToolkit(multiSamples, height, width, shaderResource, textureSource, canvas, toolkitType)
	}
}
