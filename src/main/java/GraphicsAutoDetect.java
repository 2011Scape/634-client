import com.jagex.game.runetek5.load.ModelTransformOriginY;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.ToolkitType;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;

public final class GraphicsAutoDetect {

	@OriginalMember(owner = "client!qfa", name = "m", descriptor = "(I)I")
	public static int calculateGraphics() {
		@Pc(12) boolean profileSoftware = false;
		@Pc(14) boolean profileOpengl = false;
		@Pc(16) boolean profileDirectx = false;
		if (GameShell.signLink.signed && !GameShell.signLink.microsoftJava) {
			profileSoftware = true;
			if (SystemInfo.SYSTEM_INFO.ram < 512 && SystemInfo.SYSTEM_INFO.ram != 0) {
				profileSoftware = false;
			}
			if (SystemInfo.operatingSystemLowercase.startsWith("win")) {
				profileDirectx = true;
				profileOpengl = true;
			} else {
				profileOpengl = true;
			}
		}
		if (GameShell.softwareMode) {
			profileSoftware = false;
		}
		if (GameShell.openglMode) {
			profileOpengl = false;
		}
		if (GameShell.directxMode) {
			profileDirectx = false;
		}
		if (!profileSoftware && !profileOpengl && !profileDirectx) {
			return SoftwareDetailAutoSelect.selectSoftwareDetailLevel();
		}
		@Pc(68) int performanceSoftware = -1;
		@Pc(70) int performanceOpengl = -1;
		@Pc(72) int performanceDirectx = -1;
		if (profileSoftware) {
			try {
				performanceSoftware = ToolkitProfiler.getProfilingPerformance(ToolkitType.SSE, 1000);
			} catch (@Pc(81) Exception ex) {
			}
		}
		if (profileDirectx) {
			try {
				performanceDirectx = ToolkitProfiler.getProfilingPerformance(ToolkitType.D3D, 1000);
				if (GamePreferences.uid.toolkit.getValue() == ToolkitType.D3D) {
					@Pc(102) Renderer renderer = ClientState.activeToolkit.renderer();
					@Pc(107) long driverVersion = renderer.driverVersion & 0xFFFFFFFFFFFFL;
					@Pc(110) int vendor = renderer.vendor;
					if (vendor == 4318) {
						profileOpengl &= driverVersion >= 64425238954L;
					} else if (vendor == 4098) {
						profileOpengl &= driverVersion >= 60129613779L;
					}
				}
			} catch (@Pc(146) Exception ex2) {
			}
		}
		if (profileOpengl) {
			try {
				performanceOpengl = ToolkitProfiler.getProfilingPerformance(ToolkitType.GL, 1000);
			} catch (@Pc(158) Exception ex3) {
			}
		}
		if (performanceSoftware == -1 && performanceOpengl == -1 && performanceDirectx == -1) {
			return SoftwareDetailAutoSelect.selectSoftwareDetailLevel();
		}
		performanceDirectx = (int) ((float) performanceDirectx * 1.1F);
		performanceOpengl = (int) ((float) performanceOpengl * 1.1F);
		if (performanceDirectx < performanceSoftware && performanceOpengl < performanceSoftware) {
			return SseDetailAutoSelect.selectSseDetailLevel(performanceSoftware);
		} else if (performanceDirectx <= performanceOpengl) {
			return Static690.method5092(ToolkitType.GL, performanceOpengl);
		} else {
			return Static690.method5092(3, performanceDirectx);
		}
	}
}
