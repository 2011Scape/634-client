package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Actor
import kotlin.jvm.JvmField

/** [text] appeared over [actor]'s head: public chat, or an npc's or a script's line. */
class OverheadTextChanged(@JvmField val actor: Actor, @JvmField val text: String)
