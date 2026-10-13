package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.preference.GamePreferences
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client. When 634HD (toolkit 6) cannot start, ActiveToolkitInit falls back
 * to OpenGL, and from there (as the original does for any toolkit) to the Java toolkit, Safe Mode. This
 * keeps the reason, prints it at once, and tells the player once in game which toolkit they got.
 */
object HdFallback {

	/** Why 634HD did not start, or null. */
	@JvmField
	var reason: String? = null

	private var told: Boolean = true

	@JvmStatic
	fun failed(ex: Throwable) {
		var why: String = ex.message ?: ex.toString()
		if (why.length == 0) {
			why = ex.toString()
		}
		reason = why
		told = false
		lang.printlnErr("634HD could not start: " + why)
		Console.printToConsole("634HD could not start: " + why)
	}

	/** Called every logic tick: once in game, the message goes to the chat box. */
	@JvmStatic
	fun tick() {
		if (told || ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return
		}
		told = true
		val toolkit: Int = GamePreferences.uid!!.toolkit!!.getValue()
		val now: String = if (toolkit == ToolkitType.GL) "OpenGL" else if (toolkit == ToolkitType.JAVA) "Safe Mode" else "toolkit " + toolkit
		ChatLineWriter.writeUnfilterableMessage("634HD could not start, so the game is using " + now + ": " + reason)
	}
}
