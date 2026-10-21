package com.jagex.game.runetek5.plugin

import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO

/*
 * Not part of the original client: the JVM side of showDesktopNotification. On Linux the desktop's
 * own notifications through notify-send (libnotify), as RuneLite sends them: a tray balloon is an
 * X11 window of Java's own that a Wayland desktop rarely shows, and the KDE and GNOME trays no
 * longer take the XEmbed icons Java makes. Elsewhere, and on a Linux without notify-send, a balloon
 * from one TrayIcon in the system tray (java.awt.SystemTray), made on the first call and removed
 * when the JVM exits. The icon is Void's (icons/icon-*.png, as WindowIcons gives the window), or a
 * blank one when the resource is missing, since a TrayIcon refuses to be made without an image.
 */
private object LinuxNotify {

	private const val ICON_SIZE: Int = 64

	private const val APP_NAME: String = "Void"

	/** Whether notify-send is worth trying: Linux, and not yet found missing. */
	private var available: Boolean = System.getProperty("os.name", "").lowercase().contains("linux")

	private var iconTried: Boolean = false

	/** The icon written out of the jar for notify-send to read, or null when that failed. */
	private var iconPath: String? = null

	/**
	 * Starts notify-send and does not wait for it: the game thread is not held for a D-Bus round
	 * trip, and a daemon that then refuses is a notification lost, nothing more. False, and no more
	 * tries, when the program is not there.
	 */
	@Suppress("SwallowedException") // the exception is "no notify-send": remembered, not reported
	fun show(title: String, text: String): Boolean {
		if (!available) {
			return false
		}
		val command: ArrayList<String> = ArrayList()
		command.add("notify-send")
		command.add("--app-name=" + APP_NAME)
		val icon: String? = icon()
		if (icon != null) {
			command.add("--icon=" + icon)
		}
		command.add("--")
		command.add(title)
		command.add(text)
		try {
			val builder: ProcessBuilder = ProcessBuilder(command)
			builder.redirectOutput(ProcessBuilder.Redirect.DISCARD)
			builder.redirectError(ProcessBuilder.Redirect.DISCARD)
			builder.start()
			return true
		} catch (error: IOException) {
			available = false
			return false
		}
	}

	@Suppress("TooGenericExceptionCaught", "SwallowedException") // no icon file means a notification without one
	private fun icon(): String? {
		if (iconTried) {
			return iconPath
		}
		iconTried = true
		val stream: InputStream = LinuxNotify::class.java.getResourceAsStream("/icons/icon-" + ICON_SIZE + ".png") ?: return null
		try {
			val file: File = File.createTempFile("void-notify-", ".png")
			file.deleteOnExit()
			Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
			iconPath = file.getAbsolutePath()
		} catch (error: Exception) {
			iconPath = null
		} finally {
			stream.close()
		}
		return iconPath
	}
}

private object DesktopTray {

	private const val ICON_SIZE: Int = 16

	/** Whether the tray was tried: a tray that refused the icon once is not asked again. */
	private var tried: Boolean = false

	private var icon: TrayIcon? = null

	@Suppress("TooGenericExceptionCaught", "SwallowedException") // a tray that cannot be used means "no notification", nothing more
	fun show(title: String, text: String): Boolean {
		val icon: TrayIcon = icon() ?: return false
		try {
			icon.displayMessage(title, text, TrayIcon.MessageType.NONE)
			return true
		} catch (error: Exception) {
			return false
		}
	}

	@Suppress("TooGenericExceptionCaught", "SwallowedException")
	private fun icon(): TrayIcon? {
		if (tried) {
			return icon
		}
		tried = true
		try {
			if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) {
				return null
			}
			val tray: SystemTray = SystemTray.getSystemTray()
			val made: TrayIcon = TrayIcon(image(), "Void")
			made.setImageAutoSize(true)
			tray.add(made)
			Runtime.getRuntime().addShutdownHook(
				Thread {
					try {
						tray.remove(made)
					} catch (error: Exception) {
						/* the tray is going down with the JVM anyway */
					}
				},
			)
			icon = made
			return made
		} catch (error: Exception) {
			return null
		}
	}

	@Suppress("TooGenericExceptionCaught", "SwallowedException")
	private fun image(): Image {
		val resource: URL? = DesktopTray::class.java.getResource("/icons/icon-" + ICON_SIZE + ".png")
		if (resource != null) {
			try {
				val read: Image? = ImageIO.read(resource)
				if (read != null) {
					return read
				}
			} catch (error: Exception) {
				/* fall through to the blank icon */
			}
		}
		return BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB)
	}
}

actual fun showDesktopNotification(title: String, text: String): Boolean {
	if (LinuxNotify.show(title, text)) {
		return true
	}
	return DesktopTray.show(title, text)
}
