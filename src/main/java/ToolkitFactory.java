import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.ToolkitType;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.statics.D3DToolkitFactory;
import java.awt.Canvas;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

public final class ToolkitFactory {

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIILclient!in;ILclient!d;Ljava/awt/Canvas;I)Lclient!ha;")
	public static synchronized Toolkit createToolkit(@OriginalArg(0) int multiSamples, @OriginalArg(1) int height, @OriginalArg(2) int width, @OriginalArg(3) Js5ResourceProvider shaders, @OriginalArg(5) MaterialProvider textureSource, @OriginalArg(6) Canvas canvas, @OriginalArg(7) int toolkitType) {
		if (toolkitType == ToolkitType.JAVA) {
			return JavaToolkitFactory.createToolkit(height, width, canvas, textureSource);
		} else if (toolkitType == ToolkitType.SSE) {
			return SseToolkitFactory.createToolkit(height, textureSource, canvas, width);
		} else if (toolkitType == ToolkitType.GL) {
			return GlToolkit.create(multiSamples, canvas, textureSource);
		} else if (toolkitType == ToolkitType.GLX) {
			return GlxToolkit.createToolkit(canvas, textureSource, shaders, multiSamples);
		} else if (toolkitType == ToolkitType.D3D) {
			return D3DToolkitFactory.createD3DToolkit(multiSamples, textureSource, shaders, canvas);
		} else {
			throw new IllegalArgumentException("UM");
		}
	}
}
