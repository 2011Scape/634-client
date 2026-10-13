import com.jagex.game.runetek5.UpdateZoneFullFollowsProt
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.ConsoleEditing
import com.jagex.game.runetek5.client.input.console.Console
import kotlin.jvm.JvmStatic

object ConsoleCommandSubmit {

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(ZI)V")
	@JvmStatic
	fun executeCommand(@OriginalArg(0) tabComplete: Boolean) {
		if (Console.commandString!!.length == 0) {
			return
		}
		if (ClientFeatures.CONSOLE && tabComplete) {
			// Not part of the original client: Tab sends the line for completion and keeps it, so the server's
			// answer can replace it.
			ConsoleEditing.awaitingCompletion = true
			ClientCommandProcessor.executeCommand(Console.commandString, false, true)
			return
		}
		if (ClientFeatures.CONSOLE) {
			// Not part of the original client: Up and Down recall what the player typed (ConsoleEditing).
			ConsoleEditing.record(Console.commandString)
			ConsoleEditing.awaitingCompletion = false
		}
		Console.printToConsole("--> " + Console.commandString)
		ClientCommandProcessor.executeCommand(Console.commandString, false, tabComplete)
		Console.commandLength = 0
		Console.commandString = ""
		Console.historyIndex = 0
	}
}
