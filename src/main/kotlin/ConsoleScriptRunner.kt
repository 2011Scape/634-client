import com.jagex.game.runetek5.UpdateZoneFullFollowsProt
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

object ConsoleScriptRunner {

	@OriginalMember(owner = "client!nk", name = "a", descriptor = "(I[Ljava/lang/String;)V")
	@JvmStatic
	fun runScript(@OriginalArg(1) strings: Array<String?>) {
		if (strings.size <= 1) {
			Console.commandString = Console.commandString + strings[0]
			Console.commandLength += strings[0]!!.length
			return
		}
		@Pc(14) var i: Int = 0
		while (i < strings.size) {
			if (strings[i]!!.startsWith("pause")) {
				@Pc(24) var waitDuration: Int = 5
				try {
					waitDuration = Integer.parseInt(strings[i]!!.substring(6))
				} catch (@Pc(33) ex: Exception) {
				}
				Console.printToConsole("Pausing for " + waitDuration + " seconds...")
				Console.scriptArguments = strings
				Console.pausedAtCommandIndex = i + 1
				Console.pauseEndsAt = (waitDuration * 1000).toLong() + Utils.currentTimeMillis()
				return
			}
			Console.commandString = strings[i]
			ConsoleCommandSubmit.executeCommand(false)
			i++
		}
	}
}
