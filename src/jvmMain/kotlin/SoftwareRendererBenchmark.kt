import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ToolkitType
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

object SoftwareRendererBenchmark {

	@OriginalMember(owner = "client!vha", name = "a", descriptor = "(I)I")
	@JvmStatic
	fun benchmarkSoftwareRenderer(): Int {
		@Pc(7) var toolkit: Toolkit? = ClientState.activeToolkit
		@Pc(9) var createdToolkit: Boolean = false
		if (GamePreferences.uid!!.toolkit!!.getValue() != ToolkitType.JAVA) {
			@Pc(23) val canvas: Canvas = Canvas()
			canvas.setSize(100, 100)
			toolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, null, 0, canvas, null)
			createdToolkit = true
		}
		@Pc(42) val startTime: Long = Utils.currentTimeMillis()
		@Pc(44) var i: Int = 0
		while (i < 10000) {
			toolkit!!.drawTriangle()
			i++
		}
		@Pc(72) val elapsedTime: Int = (Utils.currentTimeMillis() - startTime).toInt()
		toolkit!!.fillRect(100, 0, 0, 100, -16777216)
		if (createdToolkit) {
			toolkit.free()
		}
		return elapsedTime
	}
}
