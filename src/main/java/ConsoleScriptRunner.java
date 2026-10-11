import com.jagex.game.runetek5.UpdateZoneFullFollowsProt;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class ConsoleScriptRunner {

	@OriginalMember(owner = "client!nk", name = "a", descriptor = "(I[Ljava/lang/String;)V")
	public static void runScript(@OriginalArg(1) String[] strings) {
		if (strings.length <= 1) {
			Console.commandString = Console.commandString + strings[0];
			Console.commandLength += strings[0].length();
			return;
		}
		for (@Pc(14) int i = 0; i < strings.length; i++) {
			if (strings[i].startsWith("pause")) {
				@Pc(24) int waitDuration = 5;
				try {
					waitDuration = Integer.parseInt(strings[i].substring(6));
				} catch (@Pc(33) Exception ex) {
				}
				Console.printToConsole("Pausing for " + waitDuration + " seconds...");
				Console.scriptArguments = strings;
				Console.pausedAtCommandIndex = i + 1;
				Console.pauseEndsAt = (long) (waitDuration * 1000) + Utils.currentTimeMillis();
				return;
			}
			Console.commandString = strings[i];
			ConsoleCommandSubmit.executeCommand(false);
		}
	}
}
