package com.jagex.game.runetek5.client.input.console;

import java.awt.datatransfer.Clipboard;
import java.io.FileOutputStream;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.utils.Utils;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.tools.LocalizedText;
import java.io.IOException;
import java.util.Date;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.MainLogicManager;

public final class Console {
	@OriginalMember(owner = "client!uia", name = "J", descriptor = "Ljava/awt/datatransfer/Clipboard;")
	public static Clipboard clipboard;

	@OriginalMember(owner = "client!fga", name = "a", descriptor = "Ljava/lang/String;")
	public static String commandString = "";

	@OriginalMember(owner = "client!dq", name = "p", descriptor = "J")
	public static long pauseEndsAt;

	@OriginalMember(owner = "client!gb", name = "t", descriptor = "[Ljava/lang/String;")
	public static String[] scriptArguments;

	@OriginalMember(owner = "client!ufa", name = "f", descriptor = "I")
	public static int historyIndex = 0;

	@OriginalMember(owner = "client!bn", name = "g", descriptor = "I")
	public static int backgroundAlpha = 0;

	@OriginalMember(owner = "client!kc", name = "M", descriptor = "I")
	public static int inputLineHeight;

	@OriginalMember(owner = "client!ej", name = "g", descriptor = "Z")
	public static boolean isOpen = false;

	@OriginalMember(owner = "client!baa", name = "k", descriptor = "I")
	public static int outputLineHeight;

	@OriginalMember(owner = "client!dp", name = "g", descriptor = "Ljava/io/FileOutputStream;")
	public static FileOutputStream outputStream;

	@OriginalMember(owner = "client!sea", name = "u", descriptor = "I")
	public static int commandLength = 0;

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "I")
	public static int pausedAtCommandIndex = -1;

	@OriginalMember(owner = "client!an", name = "v", descriptor = "[Ljava/lang/String;")
	public static String[] displayedOutput;

	private Console() {
		/* empty */
	}
	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(I)V")
	public static void open() {
		if (!MainLogicManager.isPastStartup()) {
			return;
		}
		if (Console.displayedOutput == null) {
			Console.initialize();
		}
		Console.backgroundAlpha = 0;
		Console.isOpen = true;
	}

	@OriginalMember(owner = "client!qh", name = "b", descriptor = "(Z)Z")
	public static boolean isOpen() {
		return Console.isOpen;
	}
	@OriginalMember(owner = "client!bk", name = "d", descriptor = "(B)V")
	public static void close() {
		Console.isOpen = false;
		InterfaceRectangles.invalidateAllRectangles();
	}

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(Lclient!ha;B)V")
	public static void renderConsole(@OriginalArg(0) Toolkit toolkit) {
		@Pc(7) int offsetX = 0;
		@Pc(9) int offsetY = 0;
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX();
			offsetY = OrthoMode.getOrthoDrawOffsetY();
		}
		toolkit.KA(offsetX, offsetY, GameShell.screenCanvasWidth + offsetX, offsetY + 350);
		toolkit.aa(offsetX, offsetY, GameShell.screenCanvasWidth, 350, Console.backgroundAlpha << 24 | 0x332277, 1);
		OrthoMode.markOrthoTilesDirty(offsetX, GameShell.screenCanvasWidth + offsetX, offsetY, offsetY + 350);
		@Pc(58) int visibleLines = 350 / Console.outputLineHeight;
		@Pc(68) int local68;
		if (InterfaceComponentRenderer.consoleMessageCount > 0) {
			int trackHeight = 342 - Console.outputLineHeight;
			@Pc(79) int scrollbarHeight = visibleLines * trackHeight / (visibleLines + InterfaceComponentRenderer.consoleMessageCount - 1);
			@Pc(81) int scrollbarY = 4;
			if (InterfaceComponentRenderer.consoleMessageCount > 1) {
				scrollbarY = (InterfaceComponentRenderer.consoleMessageCount - Console.scrollOffset - 1) * (trackHeight - scrollbarHeight) / (InterfaceComponentRenderer.consoleMessageCount - 1) + 4;
			}
			toolkit.aa(GameShell.screenCanvasWidth + offsetX - 16, offsetY + scrollbarY, 12, scrollbarHeight, Console.backgroundAlpha << 24 | 0x332277, 2);
			for (@Pc(121) int i = Console.scrollOffset; visibleLines + Console.scrollOffset > i && i < InterfaceComponentRenderer.consoleMessageCount; i++) {
				@Pc(130) String[] strings = Utils.split('\b', Console.displayedOutput[i]);
				@Pc(139) int columnWidth = (GameShell.screenCanvasWidth - 16 - 8) / strings.length;
				for (@Pc(141) int j = 0; j < strings.length; j++) {
					@Pc(149) int columnX = columnWidth * j + 8;
					toolkit.KA(offsetX + columnX, offsetY, columnWidth + offsetX + columnX - 8, offsetY + 350);
					FontManager.aClass68_4.drawString(Console.formatCommand(strings[j]), -1, offsetY + 350 - (i - Console.scrollOffset) * Console.outputLineHeight - Console.inputLineHeight - FontManager.messageboxFont.paddingBottom - 2, offsetX + columnX, -16777216);
				}
			}
		}
		Renderers.FONT_RENDERER.renderRightAligned("Build: 634", offsetY + 350 - 20, -1, GameShell.screenCanvasWidth + offsetX + -25, -16777216);
		toolkit.KA(offsetX, offsetY, offsetX + GameShell.screenCanvasWidth, offsetY - -350);
		toolkit.horizontalLine(GameShell.screenCanvasWidth, offsetY + 350 - Console.inputLineHeight, -1, offsetX);
		MiniMenu.minimenuHeader.drawString("--> " + Console.formatCommand(Console.commandString), -1, offsetY + 350 - FontMetrics.defaultFontMetrics.paddingBottom - 1, offsetX + 10, -16777216);
		if (!GameShell.appletHasFocus) {
			return;
		}
		int cursorColour = -1;
		if (ClientState.clientCycle % 30 > 15) {
			cursorColour = 16777215;
		}
		toolkit.verticalLine(offsetX + FontMetrics.defaultFontMetrics.getWidthNoSprites("--> " + Console.formatCommand(Console.commandString).substring(0, Console.commandLength)) + 10, cursorColour, 12, offsetY + 350 - FontMetrics.defaultFontMetrics.paddingBottom - 11);
	}

	@OriginalMember(owner = "client!aa", name = "a", descriptor = "(I)V")
	public static void adjustCommandString() {
		if (Console.historyIndex <= 0) {
			Console.commandString = "";
			return;
		}
		@Pc(8) int commandCount = 0;
		for (@Pc(10) int i = 0; i < Console.displayedOutput.length; i++) {
			if (Console.displayedOutput[i].indexOf("--> ") != -1) {
				commandCount++;
				if (commandCount == Console.historyIndex) {
					Console.commandString = Console.displayedOutput[i].substring(Console.displayedOutput[i].indexOf(">") + 2);
					break;
				}
			}
		}
	}

	@OriginalMember(owner = "client!tm", name = "a", descriptor = "(BLjava/lang/String;)Ljava/lang/String;")
	public static String formatCommand(@OriginalArg(1) String string) {
		@Pc(12) String string2 = null;
		@Pc(16) int arrowIndex = string.indexOf("--> ");
		if (arrowIndex >= 0) {
			string2 = string.substring(0, arrowIndex + 4);
			string = string.substring(arrowIndex + 4);
		}
		if (string.startsWith("directlogin ")) {
			@Pc(44) int directLoginIndex = string.indexOf(" ", "directlogin ".length());
			if (directLoginIndex >= 0) {
				@Pc(49) int end = string.length();
				string = string.substring(0, directLoginIndex) + " ";
				for (@Pc(65) int i = directLoginIndex + 1; i < end; i++) {
					string = string + "*";
				}
			}
		}
		return string2 == null ? string : string2 + string;
	}
	@OriginalMember(owner = "client!qba", name = "f", descriptor = "(I)V")
	public static void initialize() {
		Console.displayedOutput = new String[500];
		Console.inputLineHeight = FontMetrics.defaultFontMetrics.paddingBottom + FontMetrics.defaultFontMetrics.paddingTop + 2;
		Console.outputLineHeight = FontManager.messageboxFont.paddingTop + FontManager.messageboxFont.paddingBottom + 2;
		for (@Pc(27) int i = 0; i < Console.displayedOutput.length; i++) {
			Console.displayedOutput[i] = "";
		}
		Console.printToConsole(LocalizedText.DEVELOPER_CONSOLE_INTRO.get(ClientState.language));
	}
	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(B)V")
	public static void shutdown() {
		if (Console.outputStream != null) {
			try {
				Console.outputStream.close();
			} catch (@Pc(5) IOException ex) {
			}
		}
		Console.outputStream = null;
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(Ljava/lang/String;I)V")
	public static void printToConsole(@OriginalArg(0) String string) {
		if (Console.displayedOutput == null) {
			Console.initialize();
		}
		TimeUtils.ACTIVE_TIMEZONE_CALENDER.setTime(new Date(Utils.currentTimeMillis()));
		@Pc(20) int hour = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(11);
		@Pc(24) int minute = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(12);
		@Pc(28) int second = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(13);
		@Pc(62) String string2 = Integer.toString(hour / 10) + hour % 10 + ":" + minute / 10 + minute % 10 + ":" + second / 10 + second % 10;
		@Pc(67) String[] strings = Utils.split('\n', string);
		for (@Pc(69) int i = 0; i < strings.length; i++) {
			for (@Pc(73) int line = InterfaceComponentRenderer.consoleMessageCount; line > 0; line--) {
				Console.displayedOutput[line] = Console.displayedOutput[line - 1];
			}
			Console.displayedOutput[0] = string2 + ": " + strings[i];
			if (Console.outputStream != null) {
				try {
					Console.outputStream.write(Utils.toCp1252Bytes(Console.displayedOutput[0] + "\n"));
				} catch (@Pc(122) IOException ex) {
				}
			}
			if (InterfaceComponentRenderer.consoleMessageCount < Console.displayedOutput.length - 1) {
				InterfaceComponentRenderer.consoleMessageCount++;
				if (Console.scrollOffset > 0) {
					Console.scrollOffset++;
				}
			}
		}
	}
	@OriginalMember(owner = "client!vha", name = "m", descriptor = "I")
	public static int scrollOffset = 0;
}
