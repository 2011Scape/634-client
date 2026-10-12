import com.jagex.game.runetek5.UpdateZoneFullFollowsProt
import com.jagex.game.runetek5.SceneCameraZ
import awt.datatransfer.DataFlavor
import awt.datatransfer.StringSelection
import awt.datatransfer.Transferable
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.ConsoleEditing
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import kotlin.jvm.JvmStatic

object ConsoleTick {

	@OriginalMember(owner = "client!iea", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun processConsoleInput() {
		if (Console.backgroundAlpha < 102) {
			Console.backgroundAlpha += 6
		}
		@Pc(27) var pausedAtCommandIndex: Int
		if (Console.pausedAtCommandIndex != -1 && Utils.currentTimeMillis() > Console.pauseEndsAt) {
			pausedAtCommandIndex = Console.pausedAtCommandIndex
			while (pausedAtCommandIndex < Console.scriptArguments!!.size) {
				if (Console.scriptArguments!![pausedAtCommandIndex]!!.startsWith("pause")) {
					@Pc(46) var pauseSeconds: Int = 5
					try {
						pauseSeconds = lang.parseInt(Console.scriptArguments!![pausedAtCommandIndex]!!.substring(6))
					} catch (@Pc(55) ex: Exception) {
					}
					Console.printToConsole("Pausing for " + pauseSeconds + " seconds...")
					Console.pausedAtCommandIndex = pausedAtCommandIndex + 1
					Console.pauseEndsAt = (pauseSeconds * 1000).toLong() + Utils.currentTimeMillis()
					return
				}
				Console.commandString = Console.scriptArguments!![pausedAtCommandIndex]
				ConsoleCommandSubmit.executeCommand(false)
				pausedAtCommandIndex++
			}
			Console.pausedAtCommandIndex = -1
		}
		if (client.mouseWheelDelta != 0) {
			Console.scrollOffset -= client.mouseWheelDelta * 5
			if (InterfaceComponentRenderer.consoleMessageCount <= Console.scrollOffset) {
				Console.scrollOffset = InterfaceComponentRenderer.consoleMessageCount - 1
			}
			client.mouseWheelDelta = 0
			if (Console.scrollOffset < 0) {
				Console.scrollOffset = 0
			}
		}
		pausedAtCommandIndex = 0
		while (pausedAtCommandIndex < client.maximumHeldKeys) {
			@Pc(129) val keyLog: KeyRecord? = client.KEY_RECORDS!![pausedAtCommandIndex]
			@Pc(133) val keyCode: Int = keyLog!!.getKeyCode()
			@Pc(137) val keyChar: Char = keyLog.getKeyChar()
			@Pc(141) val modifierFlags: Int = keyLog.getModifierFlags()
			if (keyCode == 84) {
				ConsoleCommandSubmit.executeCommand(false)
			}
			if (ClientFeatures.CONSOLE && ConsoleEditing.handle(keyCode, modifierFlags)) {
				// Not part of the original client: Up and Down recall commands, Ctrl edits a word at a time.
				pausedAtCommandIndex++
				continue
			}
			if (keyCode == 80) {
				ConsoleCommandSubmit.executeCommand(true)
			} else if (keyCode == 66 && (modifierFlags and 0x4) != 0) {
				if (Console.clipboard != null) {
					@Pc(165) var string: String = ""
					@Pc(170) var i: Int = Console.displayedOutput!!.size - 1
					while (i >= 0) {
						if (Console.displayedOutput!![i] != null && Console.displayedOutput!![i]!!.length > 0) {
							string = string + Console.displayedOutput!![i] + '\n'
						}
						i--
					}
					Console.clipboard!!.setContents(StringSelection(string), null)
				}
			} else if (keyCode == 67 && (modifierFlags and 0x4) != 0) {
				if (Console.clipboard != null) {
					@Pc(439) val transferable: Transferable? = Console.clipboard!!.getContents(null)
					if (transferable != null) {
						try {
							@Pc(446) val string2: String? = transferable.getTransferData(awt.datatransfer.STRING_FLAVOR) as String?
							if (string2 != null) {
								@Pc(453) val strings: Array<String?>? = Utils.split('\n', string2)
								ConsoleScriptRunner.runScript(strings!!)
							}
						} catch (@Pc(458) ex2: Exception) {
						}
					}
				}
			} else if (keyCode == 85 && Console.commandLength > 0) {
				Console.commandString = Console.commandString!!.substring(0, Console.commandLength - 1) + Console.commandString!!.substring(Console.commandLength)
				Console.commandLength--
			} else if (keyCode == 101 && Console.commandLength < Console.commandString!!.length) {
				Console.commandString = Console.commandString!!.substring(0, Console.commandLength) + Console.commandString!!.substring(Console.commandLength + 1)
			} else if (keyCode == 96 && Console.commandLength > 0) {
				Console.commandLength--
			} else if (keyCode == 97 && Console.commandLength < Console.commandString!!.length) {
				Console.commandLength++
			} else if (keyCode == 102) {
				Console.commandLength = 0
			} else if (keyCode == 103) {
				Console.commandLength = Console.commandString!!.length
			} else if (keyCode == 104 && Console.historyIndex < Console.displayedOutput!!.size) {
				Console.historyIndex++
				Console.adjustCommandString()
				Console.commandLength = Console.commandString!!.length
			} else if (keyCode == 105 && Console.historyIndex > 0) {
				Console.historyIndex--
				Console.adjustCommandString()
				Console.commandLength = Console.commandString!!.length
			} else if (Utils.isAlphanumeric(keyChar) || keyChar == '\\' || keyChar == '/' || keyChar == '.' || keyChar == ':' || keyChar == ',' || keyChar == ' ' || keyChar == '_' || keyChar == '-' || keyChar == '+' || keyChar == '[' || keyChar == ']') {
				Console.commandString = Console.commandString!!.substring(0, Console.commandLength) + client.KEY_RECORDS!![pausedAtCommandIndex]!!.getKeyChar() + Console.commandString!!.substring(Console.commandLength)
				Console.commandLength++
			}
			pausedAtCommandIndex++
		}
		client.maximumHeldKeys = 0
		KeyRecorder.keyCachePointerPoss = 0
		InterfaceRectangles.invalidateAllRectangles()
	}
}
