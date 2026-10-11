import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import java.awt.Canvas;
import java.awt.Dimension;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class CanvasToolkitFactory {

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(ZILclient!d;ILjava/awt/Canvas;Lclient!in;)Lclient!ha;")
	public static Toolkit create(@OriginalArg(1) int toolkitType, @OriginalArg(2) MaterialProvider textureSource, @OriginalArg(3) int multiSamples, @OriginalArg(4) Canvas canvas, @OriginalArg(5) Js5ResourceProvider shaderResource) {
		@Pc(10) int width = 0;
		@Pc(12) int height = 0;
		if (canvas != null) {
			@Pc(17) Dimension dimension = canvas.getSize();
			height = dimension.height;
			width = dimension.width;
		}
		return ToolkitFactory.createToolkit(multiSamples, height, width, shaderResource, textureSource, canvas, toolkitType);
	}
}
