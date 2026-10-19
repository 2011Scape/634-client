package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.TOOLKIT_CHANGED]. [toolkitId] as [ClientApi.toolkitId][com.jagex.game.runetek5.plugin.api.ClientApi.toolkitId]. */
class ToolkitChanged(@JvmField val toolkitId: Int)
