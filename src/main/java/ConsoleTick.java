import com.jagex.game.runetek5.UpdateZoneFullFollowsProt;
import com.jagex.game.runetek5.SceneCameraZ;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.features.ConsoleEditing;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;

public final class ConsoleTick {

	@OriginalMember(owner = "client!iea", name = "b", descriptor = "(I)V")
	public static void processConsoleInput() {
		if (Console.backgroundAlpha < 102) {
			Console.backgroundAlpha += 6;
		}
		@Pc(27) int pausedAtCommandIndex;
		if (Console.pausedAtCommandIndex != -1 && Utils.currentTimeMillis() > Console.pauseEndsAt) {
			for (pausedAtCommandIndex = Console.pausedAtCommandIndex; pausedAtCommandIndex < Console.scriptArguments.length; pausedAtCommandIndex++) {
				if (Console.scriptArguments[pausedAtCommandIndex].startsWith("pause")) {
					@Pc(46) int pauseSeconds = 5;
					try {
						pauseSeconds = Integer.parseInt(Console.scriptArguments[pausedAtCommandIndex].substring(6));
					} catch (@Pc(55) Exception ex) {
					}
					Console.printToConsole("Pausing for " + pauseSeconds + " seconds...");
					Console.pausedAtCommandIndex = pausedAtCommandIndex + 1;
					Console.pauseEndsAt = (long) (pauseSeconds * 1000) + Utils.currentTimeMillis();
					return;
				}
				Console.commandString = Console.scriptArguments[pausedAtCommandIndex];
				ConsoleCommandSubmit.executeCommand(false);
			}
			Console.pausedAtCommandIndex = -1;
		}
		if (client.mouseWheelDelta != 0) {
			Console.scrollOffset -= client.mouseWheelDelta * 5;
			if (InterfaceComponentRenderer.consoleMessageCount <= Console.scrollOffset) {
				Console.scrollOffset = InterfaceComponentRenderer.consoleMessageCount - 1;
			}
			client.mouseWheelDelta = 0;
			if (Console.scrollOffset < 0) {
				Console.scrollOffset = 0;
			}
		}
		for (pausedAtCommandIndex = 0; pausedAtCommandIndex < client.maximumHeldKeys; pausedAtCommandIndex++) {
			@Pc(129) KeyRecord keyLog = client.KEY_RECORDS[pausedAtCommandIndex];
			@Pc(133) int keyCode = keyLog.getKeyCode();
			@Pc(137) char keyChar = keyLog.getKeyChar();
			@Pc(141) int modifierFlags = keyLog.getModifierFlags();
			if (keyCode == 84) {
				ConsoleCommandSubmit.executeCommand(false);
			}
			if (ClientFeatures.CONSOLE && ConsoleEditing.handle(keyCode, modifierFlags)) {
				// Not part of the original client: Up and Down recall commands, Ctrl edits a word at a time.
				continue;
			}
			if (keyCode == 80) {
				ConsoleCommandSubmit.executeCommand(true);
			} else if (keyCode == 66 && (modifierFlags & 0x4) != 0) {
				if (Console.clipboard != null) {
					@Pc(165) String string = "";
					for (@Pc(170) int i = Console.displayedOutput.length - 1; i >= 0; i--) {
						if (Console.displayedOutput[i] != null && Console.displayedOutput[i].length() > 0) {
							string = string + Console.displayedOutput[i] + '\n';
						}
					}
					Console.clipboard.setContents(new StringSelection(string), null);
				}
			} else if (keyCode == 67 && (modifierFlags & 0x4) != 0) {
				if (Console.clipboard != null) {
					@Pc(439) Transferable transferable = Console.clipboard.getContents(null);
					if (transferable != null) {
						try {
							@Pc(446) String string2 = (String) transferable.getTransferData(DataFlavor.stringFlavor);
							if (string2 != null) {
								@Pc(453) String[] strings = Utils.split('\n', string2);
								ConsoleScriptRunner.runScript(strings);
							}
						} catch (@Pc(458) Exception ex2) {
						}
					}
				}
			} else if (keyCode == 85 && Console.commandLength > 0) {
				Console.commandString = Console.commandString.substring(0, Console.commandLength - 1) + Console.commandString.substring(Console.commandLength);
				Console.commandLength--;
			} else if (keyCode == 101 && Console.commandLength < Console.commandString.length()) {
				Console.commandString = Console.commandString.substring(0, Console.commandLength) + Console.commandString.substring(Console.commandLength + 1);
			} else if (keyCode == 96 && Console.commandLength > 0) {
				Console.commandLength--;
			} else if (keyCode == 97 && Console.commandLength < Console.commandString.length()) {
				Console.commandLength++;
			} else if (keyCode == 102) {
				Console.commandLength = 0;
			} else if (keyCode == 103) {
				Console.commandLength = Console.commandString.length();
			} else if (keyCode == 104 && Console.historyIndex < Console.displayedOutput.length) {
				Console.historyIndex++;
				Console.adjustCommandString();
				Console.commandLength = Console.commandString.length();
			} else if (keyCode == 105 && Console.historyIndex > 0) {
				Console.historyIndex--;
				Console.adjustCommandString();
				Console.commandLength = Console.commandString.length();
			} else if (Utils.isAlphanumeric(keyChar) || keyChar == '\\' || keyChar == '/' || keyChar == '.' || keyChar == ':' || keyChar == ',' || keyChar == ' ' || keyChar == '_' || keyChar == '-' || keyChar == '+' || keyChar == '[' || keyChar == ']') {
				Console.commandString = Console.commandString.substring(0, Console.commandLength) + client.KEY_RECORDS[pausedAtCommandIndex].getKeyChar() + Console.commandString.substring(Console.commandLength);
				Console.commandLength++;
			}
		}
		client.maximumHeldKeys = 0;
		KeyRecorder.keyCachePointerPoss = 0;
		InterfaceRectangles.invalidateAllRectangles();
	}
}
