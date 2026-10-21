package com.jagex.game.runetek5.client.input.console

import awt.datatransfer.Clipboard
import io.FileOutputStream
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.tools.LocalizedText
import io.IOException
import util.Date
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.MainLogicManager
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object Console {
	@OriginalMember(owner = "client!uia", name = "J", descriptor = "Ljava/awt/datatransfer/Clipboard;")
	@JvmField
	var clipboard: Clipboard? = null

	@OriginalMember(owner = "client!fga", name = "a", descriptor = "Ljava/lang/String;")
	@JvmField
	var commandString: String? = ""

	@OriginalMember(owner = "client!dq", name = "p", descriptor = "J")
	@JvmField
	var pauseEndsAt: Long = 0L

	@OriginalMember(owner = "client!gb", name = "t", descriptor = "[Ljava/lang/String;")
	@JvmField
	var scriptArguments: Array<String?>? = null

	@OriginalMember(owner = "client!ufa", name = "f", descriptor = "I")
	@JvmField
	var historyIndex: Int = 0

	@OriginalMember(owner = "client!bn", name = "g", descriptor = "I")
	@JvmField
	var backgroundAlpha: Int = 0

	@OriginalMember(owner = "client!kc", name = "M", descriptor = "I")
	@JvmField
	var inputLineHeight: Int = 0

	@OriginalMember(owner = "client!ej", name = "g", descriptor = "Z")
	@JvmField
	var isOpen: Boolean = false

	@OriginalMember(owner = "client!baa", name = "k", descriptor = "I")
	@JvmField
	var outputLineHeight: Int = 0

	@OriginalMember(owner = "client!dp", name = "g", descriptor = "Ljava/io/FileOutputStream;")
	@JvmField
	var outputStream: FileOutputStream? = null

	@OriginalMember(owner = "client!sea", name = "u", descriptor = "I")
	@JvmField
	var commandLength: Int = 0

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "I")
	@JvmField
	var pausedAtCommandIndex: Int = -1

	@OriginalMember(owner = "client!an", name = "v", descriptor = "[Ljava/lang/String;")
	@JvmField
	var displayedOutput: Array<String?>? = null

	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun open() {
		if (!MainLogicManager.isPastStartup()) {
			return
		}
		if (Console.displayedOutput == null) {
			Console.initialize()
		}
		Console.backgroundAlpha = 0
		Console.isOpen = true
	}

	@OriginalMember(owner = "client!qh", name = "b", descriptor = "(Z)Z")
	@JvmStatic
	fun isOpen(): Boolean {
		return Console.isOpen
	}
	@OriginalMember(owner = "client!bk", name = "d", descriptor = "(B)V")
	@JvmStatic
	fun close() {
		Console.isOpen = false
		InterfaceRectangles.invalidateAllRectangles()
	}

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(Lclient!ha;B)V")
	@JvmStatic
	fun renderConsole(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(7) var offsetX: Int = 0
		@Pc(9) var offsetY: Int = 0
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX()
			offsetY = OrthoMode.getOrthoDrawOffsetY()
		}
		toolkit!!.KA(offsetX, offsetY, GameShell.screenCanvasWidth + offsetX, offsetY + 350)
		toolkit.aa(offsetX, offsetY, GameShell.screenCanvasWidth, 350, (Console.backgroundAlpha shl 24) or 0x332277, 1)
		OrthoMode.markOrthoTilesDirty(offsetX, GameShell.screenCanvasWidth + offsetX, offsetY, offsetY + 350)
		@Pc(58) val visibleLines: Int = 350 / Console.outputLineHeight
		@Pc(68) var local68: Int
		if (InterfaceComponentRenderer.consoleMessageCount > 0) {
			val trackHeight: Int = 342 - Console.outputLineHeight
			@Pc(79) val scrollbarHeight: Int = visibleLines * trackHeight / (visibleLines + InterfaceComponentRenderer.consoleMessageCount - 1)
			@Pc(81) var scrollbarY: Int = 4
			if (InterfaceComponentRenderer.consoleMessageCount > 1) {
				scrollbarY = (InterfaceComponentRenderer.consoleMessageCount - Console.scrollOffset - 1) * (trackHeight - scrollbarHeight) / (InterfaceComponentRenderer.consoleMessageCount - 1) + 4
			}
			toolkit.aa(GameShell.screenCanvasWidth + offsetX - 16, offsetY + scrollbarY, 12, scrollbarHeight, (Console.backgroundAlpha shl 24) or 0x332277, 2)
			@Pc(121) var i: Int = Console.scrollOffset
			while (visibleLines + Console.scrollOffset > i && i < InterfaceComponentRenderer.consoleMessageCount) {
				@Pc(130) val strings: Array<String?> = Utils.split('\b', Console.displayedOutput!![i]!!)
				@Pc(139) val columnWidth: Int = (GameShell.screenCanvasWidth - 16 - 8) / strings.size
				@Pc(141) var j: Int = 0
				while (j < strings.size) {
					@Pc(149) val columnX: Int = columnWidth * j + 8
					toolkit.KA(offsetX + columnX, offsetY, columnWidth + offsetX + columnX - 8, offsetY + 350)
					FontManager.aClass68_4!!.drawString(Console.formatCommand(strings[j]), -1, offsetY + 350 - (i - Console.scrollOffset) * Console.outputLineHeight - Console.inputLineHeight - FontManager.messageboxFont!!.paddingBottom - 2, offsetX + columnX, -16777216)
					j++
				}
				i++
			}
		}
		Renderers.FONT_RENDERER!!.renderRightAligned("Build: 634", offsetY + 350 - 20, -1, GameShell.screenCanvasWidth + offsetX + -25, -16777216)
		toolkit.KA(offsetX, offsetY, offsetX + GameShell.screenCanvasWidth, offsetY - -350)
		toolkit.horizontalLine(GameShell.screenCanvasWidth, offsetY + 350 - Console.inputLineHeight, -1, offsetX)
		MiniMenu.minimenuHeader!!.drawString("--> " + Console.formatCommand(Console.commandString), -1, offsetY + 350 - FontMetrics.defaultFontMetrics!!.paddingBottom - 1, offsetX + 10, -16777216)
		if (!GameShell.appletHasFocus) {
			return
		}
		var cursorColour: Int = -1
		if (ClientState.clientCycle % 30 > 15) {
			cursorColour = 16777215
		}
		toolkit.verticalLine(offsetX + FontMetrics.defaultFontMetrics!!.getWidthNoSprites("--> " + Console.formatCommand(Console.commandString).substring(0, Console.commandLength)) + 10, cursorColour, 12, offsetY + 350 - FontMetrics.defaultFontMetrics!!.paddingBottom - 11)
	}

	@OriginalMember(owner = "client!aa", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun adjustCommandString() {
		if (Console.historyIndex <= 0) {
			Console.commandString = ""
			return
		}
		@Pc(8) var commandCount: Int = 0
		@Pc(10) var i: Int = 0
		while (i < Console.displayedOutput!!.size) {
			if (Console.displayedOutput!![i]!!.indexOf("--> ") != -1) {
				commandCount++
				if (commandCount == Console.historyIndex) {
					Console.commandString = Console.displayedOutput!![i]!!.substring(Console.displayedOutput!![i]!!.indexOf(">") + 2)
					break
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!tm", name = "a", descriptor = "(BLjava/lang/String;)Ljava/lang/String;")
	@JvmStatic
	fun formatCommand(@OriginalArg(1) string: String?): String {
		var string: String? = string
		@Pc(12) var string2: String? = null
		@Pc(16) val arrowIndex: Int = string!!.indexOf("--> ")
		if (arrowIndex >= 0) {
			string2 = string.substring(0, arrowIndex + 4)
			string = string.substring(arrowIndex + 4)
		}
		if (string.startsWith("directlogin ")) {
			@Pc(44) val directLoginIndex: Int = string.indexOf(" ", "directlogin ".length)
			if (directLoginIndex >= 0) {
				@Pc(49) val end: Int = string.length
				string = string.substring(0, directLoginIndex) + " "
				@Pc(65) var i: Int = directLoginIndex + 1
				while (i < end) {
					string = string + "*"
					i++
				}
			}
		}
		return if (string2 == null) string else string2 + string
	}
	@OriginalMember(owner = "client!qba", name = "f", descriptor = "(I)V")
	@JvmStatic
	fun initialize() {
		Console.displayedOutput = arrayOfNulls<String>(500)
		Console.inputLineHeight = FontMetrics.defaultFontMetrics!!.paddingBottom + FontMetrics.defaultFontMetrics!!.paddingTop + 2
		Console.outputLineHeight = FontManager.messageboxFont!!.paddingTop + FontManager.messageboxFont!!.paddingBottom + 2
		@Pc(27) var i: Int = 0
		while (i < Console.displayedOutput!!.size) {
			Console.displayedOutput!![i] = ""
			i++
		}
		Console.printToConsole(LocalizedText.DEVELOPER_CONSOLE_INTRO.get(ClientState.language))
	}
	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun shutdown() {
		if (Console.outputStream != null) {
			try {
				Console.outputStream!!.close()
			} catch (@Pc(5) ex: IOException) {
			}
		}
		Console.outputStream = null
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(Ljava/lang/String;I)V")
	@JvmStatic
	fun printToConsole(@OriginalArg(0) string: String?) {
		if (com.jagex.game.runetek5.client.features.SmokeTest.ENABLED) {
			println("CONSOLE " + string) // Not part of the original client: the smoke test reads the console in its log
		}
		if (Console.displayedOutput == null) {
			Console.initialize()
		}
		TimeUtils.ACTIVE_TIMEZONE_CALENDER.setTime(Date(Utils.currentTimeMillis()))
		@Pc(20) val hour: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(11)
		@Pc(24) val minute: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(12)
		@Pc(28) val second: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(13)
		@Pc(62) val string2: String = (hour / 10).toString() + hour % 10 + ":" + minute / 10 + minute % 10 + ":" + second / 10 + second % 10
		@Pc(67) val strings: Array<String?> = Utils.split('\n', string!!)
		@Pc(69) var i: Int = 0
		while (i < strings.size) {
			@Pc(73) var line: Int = InterfaceComponentRenderer.consoleMessageCount
			while (line > 0) {
				Console.displayedOutput!![line] = Console.displayedOutput!![line - 1]
				line--
			}
			Console.displayedOutput!![0] = string2 + ": " + strings[i]
			if (Console.outputStream != null) {
				try {
					Console.outputStream!!.write(Utils.toCp1252Bytes(Console.displayedOutput!![0] + "\n"))
				} catch (@Pc(122) ex: IOException) {
				}
			}
			if (InterfaceComponentRenderer.consoleMessageCount < Console.displayedOutput!!.size - 1) {
				InterfaceComponentRenderer.consoleMessageCount++
				if (Console.scrollOffset > 0) {
					Console.scrollOffset++
				}
			}
			i++
		}
	}
	@OriginalMember(owner = "client!vha", name = "m", descriptor = "I")
	@JvmField
	var scrollOffset: Int = 0
}
