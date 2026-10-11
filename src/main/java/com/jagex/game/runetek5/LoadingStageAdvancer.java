package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.LoadingStage;

public final class LoadingStageAdvancer {

	@OriginalMember(owner = "client!hr", name = "c", descriptor = "(Z)I")
	public static int finalizeStartup() {
		@Pc(7) int step = LoadingStage.LOADING_STAGE.getStep();
		if (step < LoadingStage.LOADING_STAGES.length - 1) {
			LoadingStage.LOADING_STAGE = LoadingStage.LOADING_STAGES[step + 1];
		}
		return 100;
	}
}
