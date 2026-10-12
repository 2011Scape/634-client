import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.preference.GamePreferences
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object Static690 {

	@OriginalMember(owner = "client!jw", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun method5090() {
		@Pc(15) var videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.first() as VorbisNode?
		while (videoType != null) {
			if (videoType.isReferencedThisCycle) {
				videoType.isReferencedThisCycle = false
			} else {
				VorbisStreamStop.stopVorbisStream(videoType.fileId)
			}
			videoType = VorbisNode.INDEX36_FILE_CACHE.next() as VorbisNode?
		}
	}

	@OriginalMember(owner = "client!jw", name = "a", descriptor = "(BII)I")
	@JvmStatic
	fun method5092(@OriginalArg(1) toolkitType: Int, @OriginalArg(2) performanceScore: Int): Int {
		@Pc(21) val detailLevel: Byte
		if (performanceScore > 20000) {
			HighDetailPreset.loadHighDetailPreferences()
			detailLevel = 4
		} else if (performanceScore > 10000) {
			detailLevel = 3
			MediumDetailPreset.loadMediumDetailPreferences()
		} else if (performanceScore <= 5000) {
			MinDetailPreset.loadMinDetailPreferences()
			detailLevel = 1
		} else {
			detailLevel = 2
			LowDetailPreset.loadLowDetailPreferences()
		}
		if (GamePreferences.uid!!.toolkit!!.getValue() != toolkitType) {
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, toolkitType)
			ToolkitSwitch.loadAssets(false, toolkitType)
		}
		GamePreferences.savePreferences()
		return detailLevel.toInt()
	}
}
