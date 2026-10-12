import com.jagex.game.runetek5.load.ModelTransformOriginY
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState

object GraphicsAutoDetect {

	@OriginalMember(owner = "client!qfa", name = "m", descriptor = "(I)I")
	@JvmStatic
	fun calculateGraphics(): Int {
		@Pc(12) var profileSoftware: Boolean = false
		@Pc(14) var profileOpengl: Boolean = false
		@Pc(16) var profileDirectx: Boolean = false
		if (GameShell.signLink!!.signed && !GameShell.signLink!!.microsoftJava) {
			profileSoftware = true
			if (SystemInfo.SYSTEM_INFO!!.ram < 512 && SystemInfo.SYSTEM_INFO!!.ram != 0) {
				profileSoftware = false
			}
			if (SystemInfo.operatingSystemLowercase!!.startsWith("win")) {
				profileDirectx = true
				profileOpengl = true
			} else {
				profileOpengl = true
			}
		}
		if (GameShell.softwareMode) {
			profileSoftware = false
		}
		if (GameShell.openglMode) {
			profileOpengl = false
		}
		if (GameShell.directxMode) {
			profileDirectx = false
		}
		if (!profileSoftware && !profileOpengl && !profileDirectx) {
			return SoftwareDetailAutoSelect.selectSoftwareDetailLevel()
		}
		@Pc(68) var performanceSoftware: Int = -1
		@Pc(70) var performanceOpengl: Int = -1
		@Pc(72) var performanceDirectx: Int = -1
		if (profileSoftware) {
			try {
				performanceSoftware = ToolkitProfiler.getProfilingPerformance(ToolkitType.SSE, 1000)
			} catch (@Pc(81) ex: Exception) {
			}
		}
		if (profileDirectx) {
			try {
				performanceDirectx = ToolkitProfiler.getProfilingPerformance(ToolkitType.D3D, 1000)
				if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.D3D) {
					@Pc(102) val renderer: Renderer? = ClientState.activeToolkit!!.renderer()
					@Pc(107) val driverVersion: Long = renderer!!.driverVersion and 0xFFFFFFFFFFFFL
					@Pc(110) val vendor: Int = renderer.vendor
					if (vendor == 4318) {
						profileOpengl = profileOpengl and (driverVersion >= 64425238954L)
					} else if (vendor == 4098) {
						profileOpengl = profileOpengl and (driverVersion >= 60129613779L)
					}
				}
			} catch (@Pc(146) ex2: Exception) {
			}
		}
		if (profileOpengl) {
			try {
				performanceOpengl = ToolkitProfiler.getProfilingPerformance(ToolkitType.GL, 1000)
			} catch (@Pc(158) ex3: Exception) {
			}
		}
		if (performanceSoftware == -1 && performanceOpengl == -1 && performanceDirectx == -1) {
			return SoftwareDetailAutoSelect.selectSoftwareDetailLevel()
		}
		performanceDirectx = (performanceDirectx.toFloat() * 1.1f).toInt()
		performanceOpengl = (performanceOpengl.toFloat() * 1.1f).toInt()
		if (performanceDirectx < performanceSoftware && performanceOpengl < performanceSoftware) {
			return SseDetailAutoSelect.selectSseDetailLevel(performanceSoftware)
		} else if (performanceDirectx <= performanceOpengl) {
			return Static690.method5092(ToolkitType.GL, performanceOpengl)
		} else {
			return Static690.method5092(3, performanceDirectx)
		}
	}
}
