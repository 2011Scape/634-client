package com.jagex.game.runetek5.plugin.api

/** Where the player is with a quest, from [ClientApi.questState]. Since API 13. */
object QuestState {
	/** Not started, or a quest the cache does not have. */
	const val NOT_STARTED: Int = 0

	/** Started and not finished. */
	const val IN_PROGRESS: Int = 1

	/** Finished. */
	const val FINISHED: Int = 2
}
