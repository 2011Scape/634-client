package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.LoadingStage
import kotlin.jvm.JvmStatic

object LoadingStageAdvancer {

	@OriginalMember(owner = "client!hr", name = "c", descriptor = "(Z)I")
	@JvmStatic
	fun finalizeStartup(): Int {
		@Pc(7) val step: Int = LoadingStage.LOADING_STAGE!!.getStep()
		if (step < LoadingStage.LOADING_STAGES!!.size - 1) {
			LoadingStage.LOADING_STAGE = LoadingStage.LOADING_STAGES!![step + 1]
		}
		return 100
	}
}
