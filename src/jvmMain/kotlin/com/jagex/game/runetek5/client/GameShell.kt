package com.jagex.game.runetek5.client

import com.jagex.game.runetek5.client.features.WindowIcons
import com.jagex.game.runetek5.client.GameCanvas
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.PlayMusic
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.config.vartype.player.CanvasRevalidateCounter
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import com.jagex.game.runetek5.statics.PulseTimestampRing
import com.jagex.game.runetek5.statics.BrowserJavaScript
import jagex3.jagmisc.jagmisc
import java.applet.Applet
import java.applet.AppletContext
import java.awt.Color
import java.awt.Container
import java.awt.Frame
import java.awt.Graphics
import java.awt.Insets
import java.awt.LayoutManager
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.lang.reflect.Method
import java.net.URL
import java.util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.systeminfo.SystemInfo
import java.awt.Canvas
import com.jagex.game.runetek5.client.cycle.AbstractCycleManager
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.FileOnDisk
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!km")
abstract class GameShell : Applet(), Runnable, FocusListener, WindowListener {

	@OriginalMember(owner = "client!km", name = "q", descriptor = "Z")
	private var jagmiscLoaded: Boolean = false

	@OriginalMember(owner = "client!km", name = "A", descriptor = "Z")
	private var errorScreenRaised: Boolean = false

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(I)V")
	abstract fun mainquit()

	@OriginalMember(owner = "client!km", name = "stop", descriptor = "()V")
	final override fun stop() {
		if (GameShell.anApplet_Sub1_1 === this && !GameShell.isShutdown) {
			GameShell.killTime = Utils.currentTimeMillis() + 4000L
		}
	}

	@OriginalMember(owner = "client!km", name = "getParameter", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")
	final override fun getParameter(@OriginalArg(0) name: String?): String? {
		if (GameShell.engineFrame == null) {
			return if (GameShell.applet == null || GameShell.applet === this) super.getParameter(name) else GameShell.applet!!.getParameter(name)
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(B)Ljava/lang/String;")
	open fun getDebugString(): String? {
		return null
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ILjava/lang/String;)V")
	fun raiseErrorScreen(@OriginalArg(1) string: String?) {
		if (this.errorScreenRaised) {
			return
		}
		this.errorScreenRaised = true
		System.out.println("error_game_" + string)
		try {
			BrowserJavaScript.callJavaScript(GameShell.applet, "loggedout")
		} catch (@Pc(34) ex: Throwable) {
		}
		try {
			this.getAppletContext()!!.showDocument(URL(this.getCodeBase(), "error_game_" + string + ".ws"), "_top")
		} catch (@Pc(54) ex2: Exception) {
		}
	}

	@OriginalMember(owner = "client!km", name = "windowActivated", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowActivated(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(Z)Z")
	fun loadJagmisc(): Boolean {
		return NativeLibraryOwner.loadLibrary("jagmisc")
	}

	@OriginalMember(owner = "client!km", name = "focusLost", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	final override fun focusLost(@OriginalArg(0) event: FocusEvent?) {
		GameShell.hasFocus = false
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(I)V")
	private fun onPulseWrapper() {
		@Pc(8) val currentTime: Long = Utils.currentTimeMillis()
		@Pc(12) val lastTime: Long = PulseTimestampRing.PULSE_TIMES[PulseTimestampRing.pulseTimesIndex]
		PulseTimestampRing.PULSE_TIMES[PulseTimestampRing.pulseTimesIndex] = currentTime
		PulseTimestampRing.pulseTimesIndex = PulseTimestampRing.pulseTimesIndex + 1 and 0x1F
		@Pc(35) val hasPreviousPulse: Boolean
		if (lastTime == 0L || lastTime >= currentTime) {
			hasPreviousPulse = false
		} else {
			hasPreviousPulse = true
		}
		synchronized(this) {
			GameShell.appletHasFocus = GameShell.hasFocus
		}
		this.onPulse()
	}

	@OriginalMember(owner = "client!km", name = "update", descriptor = "(Ljava/awt/Graphics;)V")
	final override fun update(@OriginalArg(0) graphics: Graphics?) {
		this.paint(graphics)
	}

	@OriginalMember(owner = "client!km", name = "windowClosing", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowClosing(@OriginalArg(0) event: WindowEvent?) {
		this.destroy()
	}

	@OriginalMember(owner = "client!km", name = "getDocumentBase", descriptor = "()Ljava/net/URL;")
	final override fun getDocumentBase(): URL? {
		if (GameShell.engineFrame == null) {
			return if (GameShell.applet == null || GameShell.applet === this) super.getDocumentBase() else GameShell.applet!!.getDocumentBase()
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!km", name = "windowDeactivated", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowDeactivated(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(B)V")
	@Synchronized
	open fun addcanvas() {
		if (GameShell.gameCanvas != null) {
			GameShell.gameCanvas!!.removeFocusListener(this)
			GameShell.gameCanvas!!.getParent().setBackground(Color.black)
			GameShell.gameCanvas!!.getParent().remove(GameShell.gameCanvas)
		}
		@Pc(32) val topContainer: Container?
		if (WindowMode.fullScreenFrame != null) {
			topContainer = WindowMode.fullScreenFrame
		} else if (GameShell.engineFrame != null) {
			topContainer = GameShell.engineFrame
		} else if (GameShell.applet == null) {
			topContainer = GameShell.anApplet_Sub1_1
		} else {
			topContainer = GameShell.applet
		}
		topContainer!!.setLayout(null)
		GameShell.gameCanvas = GameCanvas(this)
		topContainer.add(GameShell.gameCanvas)
		GameShell.gameCanvas!!.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
		GameShell.gameCanvas!!.setVisible(true)
		if (topContainer === GameShell.engineFrame) {
			@Pc(69) val insets: Insets = GameShell.engineFrame!!.getInsets()
			// Not part of the original client: with client.scale, the game offsets are in game pixels.
				GameShell.gameCanvas!!.setLocation(insets.left + ClientFeatures.scale(MiniMenu.gameCanvasX), insets.top - -ClientFeatures.scale(GameShell.gameCanvasY))
		} else {
			GameShell.gameCanvas!!.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY))
		}
		GameShell.gameCanvas!!.addFocusListener(this)
		GameShell.gameCanvas!!.requestFocus()
		GameShell.hasFocus = true
		GameShell.appletHasFocus = true
		GameShell.fullRedrawRequested = true
		GameShell.fullWindowRepainted = false
		GameShell.canvasInitializationTime = Utils.currentTimeMillis()
	}

	@OriginalMember(owner = "client!km", name = "windowOpened", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowOpened(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "paint", descriptor = "(Ljava/awt/Graphics;)V")
	@Synchronized
	final override fun paint(@OriginalArg(0) graphics: Graphics?) {
		if (GameShell.anApplet_Sub1_1 !== this || GameShell.isShutdown) {
			return
		}
		GameShell.fullRedrawRequested = true
		if (GameShell.java5OrLater && Utils.currentTimeMillis() - GameShell.canvasInitializationTime > 1000L) {
			@Pc(26) val clipBounds: Rectangle? = graphics!!.getClipBounds()
			if (clipBounds == null || WindowMode.resizableScreenWidth <= clipBounds.width && clipBounds.height >= WindowMode.resizableScreenHeight) {
				GameShell.fullWindowRepainted = true
			}
		}
	}

	@OriginalMember(owner = "client!km", name = "c", descriptor = "(I)V")
	private fun mainRedrawWrapper() {
		@Pc(6) val currentTime: Long = Utils.currentTimeMillis()
		@Pc(10) val lastTime: Long = GameShell.MAIN_REDRAW_CACHE[GameShell.mainRedrawCachePos]
		GameShell.MAIN_REDRAW_CACHE[GameShell.mainRedrawCachePos] = currentTime
		if (lastTime != 0L && lastTime < currentTime) {
			@Pc(27) val timeDelta: Int = (currentTime - lastTime).toInt()
			Camera.fps = ((timeDelta shr 1) + 32000) / timeDelta
		}
		GameShell.mainRedrawCachePos = GameShell.mainRedrawCachePos + 1 and 0x1F
		if (CanvasRevalidateCounter.revalidateCounter++ > 50) {
			CanvasRevalidateCounter.revalidateCounter -= 50
			GameShell.fullRedrawRequested = true
			GameShell.gameCanvas!!.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
			GameShell.gameCanvas!!.setVisible(true)
			if (GameShell.engineFrame != null && WindowMode.fullScreenFrame == null) {
				@Pc(68) val insets: Insets = GameShell.engineFrame!!.getInsets()
				GameShell.gameCanvas!!.setLocation(insets.left + ClientFeatures.scale(MiniMenu.gameCanvasX), insets.top + ClientFeatures.scale(GameShell.gameCanvasY))
			} else {
				GameShell.gameCanvas!!.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY))
			}
		}
		this.mainRedraw()
	}

	@OriginalMember(owner = "client!km", name = "windowDeiconified", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowDeiconified(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "focusGained", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	final override fun focusGained(@OriginalArg(0) event: FocusEvent?) {
		GameShell.hasFocus = true
		GameShell.fullRedrawRequested = true
	}

	@OriginalMember(owner = "client!km", name = "windowClosed", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowClosed(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "d", descriptor = "(I)Z")
	fun isHostnameValid(): Boolean {
		@Pc(13) var string: String = this.getDocumentBase()!!.getHost().lowercase(Locale.getDefault())
		if (string == "jagex.com" || string.endsWith(".jagex.com")) {
			return true
		} else if (string == "runescape.com" || string.endsWith(".runescape.com")) {
			return true
		} else if (string == "stellardawn.com" || string.endsWith(".stellardawn.com")) {
			return true
		} else if (string.endsWith("127.0.0.1")) {
			return true
		} else {
			while (string.length > 0 && string[string.length - 1] >= '0' && string[string.length - 1] <= '9') {
				string = string.substring(0, string.length - 1)
			}
			if (string.endsWith("192.168.1.")) {
				return true
			} else {
				this.raiseErrorScreen("invalidhost")
				return false
			}
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ZZ)V")
	private fun prepareExit(@OriginalArg(0) clean: Boolean) {
		synchronized(this) {
			if (GameShell.isShutdown) {
				return
			}
			GameShell.isShutdown = true
		}
		System.out.println("Shutdown start - clean:" + clean)
		if (GameShell.applet != null) {
			GameShell.applet!!.destroy()
		}
		try {
			this.mainquit()
		} catch (@Pc(44) ex: Exception) {
		}
		if (this.jagmiscLoaded) {
			try {
				jagmisc.quit()
			} catch (@Pc(50) ex2: Throwable) {
			}
			this.jagmiscLoaded = false
		}
		SoftwareMemoryManager.markShutdown()
		NativeLibraryOwner.unloadNativeLibraries()
		if (GameShell.gameCanvas != null) {
			try {
				GameShell.gameCanvas!!.removeFocusListener(this)
				GameShell.gameCanvas!!.getParent().remove(GameShell.gameCanvas)
			} catch (@Pc(74) ex3: Exception) {
			}
		}
		if (GameShell.signLink != null) {
			try {
				GameShell.signLink!!.shutdown()
			} catch (@Pc(82) ex4: Exception) {
			}
		}
		this.cleanup()
		if (GameShell.engineFrame != null) {
			GameShell.engineFrame!!.setVisible(false)
			GameShell.engineFrame!!.dispose()
			GameShell.engineFrame = null
		}
		System.out.println("Shutdown complete - clean:" + clean)
	}

	@OriginalMember(owner = "client!km", name = "c", descriptor = "(B)V")
	abstract fun cleanup()

	@OriginalMember(owner = "client!km", name = "getAppletContext", descriptor = "()Ljava/applet/AppletContext;")
	final override fun getAppletContext(): AppletContext? {
		if (GameShell.engineFrame == null) {
			return if (GameShell.applet == null || GameShell.applet === this) super.getAppletContext() else GameShell.applet!!.getAppletContext()
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!km", name = "getCodeBase", descriptor = "()Ljava/net/URL;")
	final override fun getCodeBase(): URL? {
		if (GameShell.engineFrame == null) {
			return if (GameShell.applet == null || GameShell.applet === this) super.getCodeBase() else GameShell.applet!!.getCodeBase()
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!km", name = "init", descriptor = "()V")
	abstract override fun init()

	@OriginalMember(owner = "client!km", name = "destroy", descriptor = "()V")
	final override fun destroy() {
		if (GameShell.anApplet_Sub1_1 === this && !GameShell.isShutdown) {
			GameShell.killTime = Utils.currentTimeMillis()
			TimeUtils.sleep(5000L)
			SignLink.PACKET_3 = null
			this.prepareExit(false)
		}
	}

	@OriginalMember(owner = "client!km", name = "run", descriptor = "()V")
	final override fun run() {
		try {
			if (PlayerUpdate.javaVendor != null) {
				@Pc(10) val string: String = PlayerUpdate.javaVendor!!.lowercase(Locale.getDefault())
				if (string.indexOf("sun") != -1 || string.indexOf("apple") != -1) {
					@Pc(23) val string2: String? = SystemInfo.javaVersion
					if (string2!! == "1.1" || string2.startsWith("1.1.") || string2 == "1.2" || string2.startsWith("1.2.")) {
						this.raiseErrorScreen("wrongjava")
						return
					}
				} else if (string.indexOf("ibm") != -1 && (SystemInfo.javaVersion == null || SystemInfo.javaVersion == "1.4.2")) {
					this.raiseErrorScreen("wrongjava")
					return
				}
			}
			if (SystemInfo.javaVersion != null && SystemInfo.javaVersion!!.startsWith("1.")) {
				@Pc(71) var charIndex: Int = 2
				@Pc(73) var javaVersionNumber: Int = 0
				while (charIndex < SystemInfo.javaVersion!!.length) {
					@Pc(79) val versionChar: Char = SystemInfo.javaVersion!![charIndex]
					if (versionChar < '0' || versionChar > '9') {
						break
					}
					charIndex++
					javaVersionNumber = javaVersionNumber * 10 + versionChar.code - 48
				}
				if (javaVersionNumber >= 5) {
					GameShell.java5OrLater = true
				}
			}
			@Pc(114) var applet: Applet? = GameShell.anApplet_Sub1_1
			if (GameShell.applet != null) {
				applet = GameShell.applet
			}
			@Pc(120) val method: Method? = SignLinkEnvironment.setFocusCycleRootMethod
			if (method != null) {
				try {
					method.invoke(applet, java.lang.Boolean.TRUE)
				} catch (@Pc(134) ex: Throwable) {
				}
			}
			GameShell.detectMaxMemory()
			GameShell.detectAvailableProcessors()
			this.addcanvas()
			this.maininit()
			GameShell.cycleManager = AbstractCycleManager.create()
			while (GameShell.killTime == 0L || GameShell.killTime > Utils.currentTimeMillis()) {
				if (ClientFeatures.FPS > 0) {
					// Not part of the original client: client.fps paces the frames, and the game ticks
					// are counted from the clock at the client's own interval, so the game keeps its speed.
					GameShell.cycleManager!!.loopCycle(1000000000L / ClientFeatures.FPS.toLong())
					GameShell.loopsInCycle = ClientFeatures.ticksDue(GameShell.pulseIntervalNanos)
				} else {
					GameShell.loopsInCycle = GameShell.cycleManager!!.loopCycle(GameShell.pulseIntervalNanos)
				}
				@Pc(157) var i: Int = 0
				while (i < GameShell.loopsInCycle) {
					this.onPulseWrapper()
					i++
				}
				this.mainRedrawWrapper()
				GameShell.performDummyActionEvent(GameShell.gameCanvas, GameShell.signLink)
			}
		} catch (@Pc(188) threadDeath: ThreadDeath) {
			throw threadDeath
		} catch (@Pc(191) ex2: Throwable) {
			ExplicitRuntimeException.reportError(this.getDebugString(), ex2)
			this.raiseErrorScreen("crash")
		} finally {
			@Pc(207) val obj: Any? = null
			this.prepareExit(true)
		}
	}

	@OriginalMember(owner = "client!km", name = "e", descriptor = "(I)V")
	abstract fun maininit()

	@OriginalMember(owner = "client!km", name = "f", descriptor = "(I)V")
	abstract fun mainRedraw()

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(IIIIILjava/lang/String;I)V")
	fun startApplet(@OriginalArg(0) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) cacheId: Int, @OriginalArg(5) string: String?) {
		try {
			if (GameShell.anApplet_Sub1_1 == null) {
				MiniMenu.gameCanvasX = 0
				GameShell.screenCanvasHeight = height
				WindowMode.resizableScreenHeight = height
				GameShell.screenCanvasWidth = width
				WindowMode.resizableScreenWidth = width
				GameShell.gameCanvasY = 0
				SystemInfo.clientBuild = 634
				GameShell.anApplet_Sub1_1 = this
				ExplicitRuntimeException.applet = GameShell.applet
				GameShell.signLink = SignLink(cacheId, string, 37, GameShell.applet != null)
				SignLink.PACKET_3 = GameShell.signLink
				@Pc(64) val signedResource: SignedResource = GameShell.signLink!!.startThread(this, 1)
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(10L)
				}
			} else {
				GameShell.alreadyLoadedCount++
				if (GameShell.alreadyLoadedCount >= 3) {
					this.raiseErrorScreen("alreadyloaded")
				} else {
					this.getAppletContext()!!.showDocument(this.getDocumentBase(), "_self")
				}
			}
		} catch (@Pc(82) ex: Throwable) {
			ExplicitRuntimeException.reportError(null, ex)
			this.raiseErrorScreen("crash")
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(IIZIILjava/lang/String;II)V")
	fun startApplication(@OriginalArg(0) cacheId: Int, @OriginalArg(5) string: String?) {
		try {
			GameShell.screenCanvasWidth = 1024
			WindowMode.resizableScreenWidth = 1024
			GameShell.screenCanvasHeight = 768
			WindowMode.resizableScreenHeight = 768
			GameShell.anApplet_Sub1_1 = this
			GameShell.gameCanvasY = 0
			MiniMenu.gameCanvasX = 0
			SystemInfo.clientBuild = 634
			ExplicitRuntimeException.applet = null
			GameShell.engineFrame = Frame()
			// Not part of the original client: the window is titled Void, not Jagex.
			GameShell.engineFrame!!.setTitle("Void")
			// Not part of the original client: the window carries Void's icon (WindowIcons).
			WindowIcons.apply(GameShell.engineFrame)
			GameShell.engineFrame!!.setResizable(true)
			GameShell.engineFrame!!.addWindowListener(this)
			GameShell.engineFrame!!.setVisible(true)
			GameShell.engineFrame!!.toFront()
			@Pc(44) val insets: Insets = GameShell.engineFrame!!.getInsets()
			// Not part of the original client: with client.scale, the window opens that many times larger.
			GameShell.engineFrame!!.setSize(insets.right + insets.left + ClientFeatures.scale(WindowMode.resizableScreenWidth), insets.bottom + ClientFeatures.scale(WindowMode.resizableScreenHeight) + insets.top)
			GameShell.signLink = SignLink(cacheId, string, 37, true)
			SignLink.PACKET_3 = GameShell.signLink
			@Pc(78) val signedResource: SignedResource = GameShell.signLink!!.startThread(this, 1)
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(10L)
			}
		} catch (@Pc(88) ex: Exception) {
			ExplicitRuntimeException.reportError(null, ex)
		}
	}

	@OriginalMember(owner = "client!km", name = "windowIconified", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	final override fun windowIconified(@OriginalArg(0) event: WindowEvent?) {
	}

	@OriginalMember(owner = "client!km", name = "g", descriptor = "(I)Z")
	fun loadJaclib(): Boolean {
		return NativeLibraryOwner.loadLibrary("jaclib")
	}

	@OriginalMember(owner = "client!km", name = "start", descriptor = "()V")
	final override fun start() {
		if (GameShell.anApplet_Sub1_1 === this && !GameShell.isShutdown) {
			GameShell.killTime = 0L
		}
	}

	@OriginalMember(owner = "client!km", name = "d", descriptor = "(B)V")
	abstract fun onPulse()

	@OriginalMember(owner = "client!km", name = "i", descriptor = "(I)Z")
	fun loadJagtheora(): Boolean {
		return NativeLibraryOwner.loadLibrary("jagtheora")
	}

	companion object {

		@OriginalMember(owner = "client!km", name = "provideLoaderApplet", descriptor = "(Ljava/applet/Applet;)V")
		@JvmStatic
		private fun provideLoaderApplet(@OriginalArg(0) applet: Applet?) {
			GameShell.applet = applet
		}

		@OriginalMember(owner = "client!fu", name = "a", descriptor = "(BLjava/lang/Object;Lclient!wea;)V")
		@JvmStatic
		fun performDummyActionEvent(@OriginalArg(1) obj: Any?, @OriginalArg(2) signLink: SignLink?) {
			if (signLink!!.eventQueue == null) {
				return
			}
			@Pc(11) var i: Int = 0
			while (i < 50 && signLink.eventQueue!!.peekEvent() != null) {
				TimeUtils.sleep(1L)
				i++
			}
			try {
				if (obj != null) {
					signLink.eventQueue!!.postEvent(ActionEvent(obj, 1001, "dummy"))
				}
			} catch (@Pc(44) ex: Exception) {
			}
		}

		@OriginalMember(owner = "client!nl", name = "e", descriptor = "I")
		@JvmField
		var gameAreaWidth: Int = 0
		@OriginalMember(owner = "client!ol", name = "b", descriptor = "I")
		@JvmField
		var mainRedrawCachePos: Int = 0

		@OriginalMember(owner = "client!cq", name = "w", descriptor = "I")
		@JvmField
		var screenCanvasWidth: Int = 0

		@OriginalMember(owner = "client!sd", name = "c", descriptor = "Z")
		@JvmField
		var softwareMode: Boolean = false

		@OriginalMember(owner = "client!dg", name = "h", descriptor = "Z")
		@JvmField
		var openglMode: Boolean = false

		@OriginalMember(owner = "client!ab", name = "B", descriptor = "Z")
		@JvmField
		var directxMode: Boolean = false

		@OriginalMember(owner = "client!wr", name = "r", descriptor = "Ljava/awt/Canvas;")
		@JvmField
		var gameCanvas: Canvas? = null
		@OriginalMember(owner = "client!cea", name = "Fb", descriptor = "[J")
		@JvmField
		val MAIN_REDRAW_CACHE: LongArray = LongArray(32)
		@OriginalMember(owner = "client!gia", name = "u", descriptor = "Lclient!tb;")
		@JvmField
		var cycleManager: AbstractCycleManager? = null

		@OriginalMember(owner = "client!haa", name = "jb", descriptor = "I")
		@JvmField
		var loopsInCycle: Int = 0
		@OriginalMember(owner = "client!eg", name = "a", descriptor = "Z")
		@JvmField
		var lastSentFocus: Boolean = true
		@OriginalMember(owner = "client!lw", name = "a", descriptor = "(Z)V")
		@JvmStatic
		fun resetDrawCache() {
			GameShell.cycleManager!!.reset()
			@Pc(15) var i: Int = 0
			while (i < 32) {
				GameShell.MAIN_REDRAW_CACHE[i] = 0L
				i++
			}
			@Pc(27) var j: Int = 0
			while (j < 32) {
				PulseTimestampRing.PULSE_TIMES[j] = 0L
				j++
			}
			GameShell.loopsInCycle = 0
		}
		@OriginalMember(owner = "client!ep", name = "a", descriptor = "(Z)V")
		@JvmStatic
		fun detectAvailableProcessors() {
			try {
				@Pc(16) val method: Method? = Runtime::class.java.getMethod("availableProcessors")
				if (method != null) {
					try {
						@Pc(28) val runtime: Runtime = Runtime.getRuntime()
						@Pc(34) val integer: Int? = method.invoke(runtime, *arrayOfNulls<Any>(0)) as Int?
						GameShell.availableProcessors = integer!!
					} catch (@Pc(39) ex: Throwable) {
					}
				}
			} catch (@Pc(41) ex2: Exception) {
			}
		}

		@OriginalMember(owner = "client!jca", name = "k", descriptor = "Ljava/awt/Frame;")
		@JvmField
		var engineFrame: Frame? = null
		@OriginalMember(owner = "client!pf", name = "K", descriptor = "I")
		@JvmField
		var screenCanvasHeight: Int = 0

		@OriginalMember(owner = "client!sga", name = "f", descriptor = "I")
		@JvmField
		var maxMemory: Int = 64

		@OriginalMember(owner = "client!vf", name = "K", descriptor = "I")
		@JvmField
		var availableProcessors: Int = 1

		@OriginalMember(owner = "client!bo", name = "a", descriptor = "(IBLclient!cea;)V")
		@JvmStatic
		fun updateRandomDatUID192(@OriginalArg(0) startPosition: Int, @OriginalArg(2) packet: Packet?) {
			if (GameShell.aClass184_2 == null) {
				return
			}
			try {
				GameShell.aClass184_2!!.seek(0L)
				GameShell.aClass184_2!!.write(startPosition, 24, packet!!.buffer)
			} catch (@Pc(19) ex: Exception) {
			}
		}
		@OriginalMember(owner = "client!kj", name = "L", descriptor = "Lclient!km;")
		@JvmField
		var anApplet_Sub1_1: GameShell? = null

		@OriginalMember(owner = "client!bf", name = "a", descriptor = "J")
		@JvmField
		var killTime: Long = 0L
		@OriginalMember(owner = "client!nba", name = "b", descriptor = "(B)V")
		@JvmStatic
		fun detectMaxMemory() {
			if (GameShell.signLink!!.microsoftJava) {
				GameShell.maxMemory = 96
				return
			}
			try {
				@Pc(15) val method: Method? = Runtime::class.java.getMethod("maxMemory")
				if (method != null) {
					try {
						@Pc(19) val runtime: Runtime = Runtime.getRuntime()
						@Pc(25) val long2: Long? = method.invoke(runtime, *arrayOfNulls<Any>(0)) as Long?
						GameShell.maxMemory = (long2!! / 1048576L).toInt() + 1
					} catch (@Pc(35) ex: Throwable) {
					}
				}
			} catch (@Pc(37) ex2: Exception) {
			}
		}

		@OriginalMember(owner = "client!kk", name = "l", descriptor = "J")
		@JvmField
		var pulseIntervalNanos: Long = 20000000L

		@OriginalMember(owner = "client!dh", name = "Z", descriptor = "I")
		@JvmField
		var alreadyLoadedCount: Int = 0
		@OriginalMember(owner = "client!hs", name = "l", descriptor = "Z")
		@JvmField
		var isShutdown: Boolean = false

		@OriginalMember(owner = "client!ds", name = "b", descriptor = "I")
		@JvmField
		var gameCanvasY: Int = 0

		@OriginalMember(owner = "client!lt", name = "b", descriptor = "Ljava/applet/Applet;")
		@JvmField
		var applet: Applet? = null

		@OriginalMember(owner = "client!lia", name = "a", descriptor = "I")
		@JvmField
		var GAME_WIDTH: Int = 765

		@OriginalMember(owner = "client!gca", name = "c", descriptor = "Lclient!l;")
		@JvmField
		var aClass184_2: FileOnDisk? = null
		@OriginalMember(owner = "client!tb", name = "b", descriptor = "(B)J")
		@JvmStatic
		fun getCurrentCycleNanos(): Long {
			return GameShell.cycleManager!!.getCurrentCycle()
		}

		@OriginalMember(owner = "client!bk", name = "u", descriptor = "Z")
		@JvmField
		var java5OrLater: Boolean = false
		@OriginalMember(owner = "client!jm", name = "z", descriptor = "Lclient!wea;")
		@JvmField
		var signLink: SignLink? = null
		@OriginalMember(owner = "client!pq", name = "t", descriptor = "Z")
		@JvmField
		var appletHasFocus: Boolean = false
		@OriginalMember(owner = "client!av", name = "u", descriptor = "Ljava/lang/String;")
		@JvmField
		var siteSettings: String? = null

		@OriginalMember(owner = "client!iia", name = "a", descriptor = "(Ljava/lang/String;Z)V")
		@JvmStatic
		fun setSiteSettingsCookie(@OriginalArg(0) settings: String?) {
			GameShell.siteSettings = settings
			if (GameShell.applet == null) {
				return
			}
			try {
				@Pc(18) val string2: String? = GameShell.applet!!.getParameter("cookieprefix")
				@Pc(22) val string3: String? = GameShell.applet!!.getParameter("cookiehost")
				@Pc(37) var string4: String = string2 + "settings=" + settings + "; version=1; path=/; domain=" + string3
				if (settings!!.length == 0) {
					string4 = string4 + "; Expires=Thu, 01-Jan-1970 00:00:00 GMT; Max-Age=0"
				} else {
					string4 = string4 + "; Expires=" + TimeUtils.formatCookieDate(Utils.currentTimeMillis() + TimeUtils.MILLISECONDS_PER_THREE_YEARS) + "; Max-Age=" + 94608000L
				}
				BrowserJavaScript.evalJavaScript(GameShell.applet, "document.cookie=\"" + string4 + "\"")
			} catch (@Pc(88) ex: Throwable) {
			}
		}

		@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Ljava/lang/String;B)V")
		@JvmStatic
		fun exitWithError(@OriginalArg(0) string: String?) {
			System.exit(1)
		}

		@OriginalMember(owner = "client!pc", name = "n", descriptor = "Z")
		@JvmField
		var force64Mb: Boolean = false

		@OriginalMember(owner = "client!pda", name = "P", descriptor = "Z")
		@Volatile
		@JvmField
		var hasFocus: Boolean = true

		@OriginalMember(owner = "client!bm", name = "y", descriptor = "Ljava/lang/String;")
		@JvmField
		var additionalInfo: String? = null

		@OriginalMember(owner = "client!tda", name = "g", descriptor = "J")
		@JvmField
		var machineUid: Long = 0L

		@OriginalMember(owner = "client!tr", name = "f", descriptor = "I")
		@JvmField
		var worldFlags: Int = 0

		@OriginalMember(owner = "client!un", name = "I", descriptor = "Z")
		@JvmField
		var advertEnabled: Boolean = false

		@OriginalMember(owner = "client!ha", name = "w", descriptor = "I")
		@JvmField
		var country: Int = 0

		@OriginalMember(owner = "client!km", name = "P", descriptor = "I")
		@JvmField
		var anInt2389: Int = 0

		@OriginalMember(owner = "client!km", name = "Q", descriptor = "I")
		@JvmField
		var anInt2390: Int = 0

		@OriginalMember(owner = "client!km", name = "R", descriptor = "I")
		@JvmField
		var anInt2391: Int = 0

		@OriginalMember(owner = "client!km", name = "S", descriptor = "I")
		@JvmField
		var anInt2392: Int = 0

		@OriginalMember(owner = "client!km", name = "T", descriptor = "Z")
		@JvmField
		var aBoolean196: Boolean = false

		@OriginalMember(owner = "client!km", name = "U", descriptor = "Z")
		@JvmField
		var aBoolean197: Boolean = false

		@OriginalMember(owner = "client!km", name = "V", descriptor = "Z")
		@JvmField
		var aBoolean198: Boolean = false

		@OriginalMember(owner = "client!km", name = "W", descriptor = "Z")
		@JvmField
		var aBoolean199: Boolean = false

		@OriginalMember(owner = "client!km", name = "X", descriptor = "Z")
		@JvmField
		var aBoolean200: Boolean = false

		@OriginalMember(owner = "client!km", name = "Y", descriptor = "Z")
		@JvmField
		var aBoolean201: Boolean = false

		@OriginalMember(owner = "client!km", name = "Z", descriptor = "Z")
		@JvmField
		var aBoolean202: Boolean = false

		@OriginalMember(owner = "client!km", name = "ab", descriptor = "I")
		@JvmField
		var anInt2393: Int = 0

		@OriginalMember(owner = "client!km", name = "bb", descriptor = "Z")
		@JvmField
		var aBoolean203: Boolean = false

		@OriginalMember(owner = "client!km", name = "cb", descriptor = "I")
		@JvmField
		var anInt2394: Int = 0

		@OriginalMember(owner = "client!km", name = "db", descriptor = "Z")
		@JvmField
		var aBoolean204: Boolean = false

		@OriginalMember(owner = "client!km", name = "eb", descriptor = "I")
		@JvmField
		var anInt2395: Int = 0

		@OriginalMember(owner = "client!km", name = "fb", descriptor = "Z")
		@JvmField
		var aBoolean205: Boolean = false

		@OriginalMember(owner = "client!km", name = "gb", descriptor = "I")
		@JvmField
		var anInt2396: Int = 0

		@OriginalMember(owner = "client!ar", name = "C", descriptor = "J")
		@Volatile
		@JvmField
		var canvasInitializationTime: Long = 0L

		@OriginalMember(owner = "client!sa", name = "p", descriptor = "Z")
		@JvmField
		var objectTag: Boolean = false

		@OriginalMember(owner = "client!re", name = "y", descriptor = "I")
		@JvmField
		var AFFILIATE: Int = 0

		@OriginalMember(owner = "client!wc", name = "a", descriptor = "(IB)V")
		@JvmStatic
		fun setPulseRate(@OriginalArg(0) cyclesPerSecond: Int) {
			GameShell.pulseIntervalNanos = 1000000000L / cyclesPerSecond.toLong()
		}

		@OriginalMember(owner = "client!op", name = "c", descriptor = "Z")
		@JvmField
		var siteSettingsMember: Boolean = false

		@OriginalMember(owner = "client!gf", name = "b", descriptor = "Z")
		@JvmField
		var fromBilling: Boolean = false

		@OriginalMember(owner = "client!it", name = "h", descriptor = "Z")
		@Volatile
		@JvmField
		var fullRedrawRequested: Boolean = true

		@OriginalMember(owner = "client!nb", name = "d", descriptor = "Ljava/lang/String;")
		@JvmField
		var quitUrl: String? = null

		@OriginalMember(owner = "client!oc", name = "a", descriptor = "(B)V")
		@JvmStatic
		fun clearCanvasBackground() {
			if (WindowMode.fullScreenFrame != null) {
				return
			}
			@Pc(10) val leftGap: Int = MiniMenu.gameCanvasX
			@Pc(12) val topGap: Int = GameShell.gameCanvasY
			@Pc(27) val rightGap: Int = WindowMode.resizableScreenWidth - leftGap - GameShell.screenCanvasWidth
			@Pc(35) val bottomGap: Int = WindowMode.resizableScreenHeight - topGap - GameShell.screenCanvasHeight
			if (leftGap <= 0 && rightGap <= 0 && topGap <= 0 && bottomGap <= 0) {
				return
			}
			try {
				@Pc(61) val container: Container?
				if (GameShell.engineFrame != null) {
					container = GameShell.engineFrame
				} else if (GameShell.applet == null) {
					container = GameShell.anApplet_Sub1_1
				} else {
					container = GameShell.applet
				}
				@Pc(71) var left: Int = 0
				@Pc(73) var top: Int = 0
				if (container === GameShell.engineFrame) {
					@Pc(79) val insets: Insets = GameShell.engineFrame!!.getInsets()
					left = insets.left
					top = insets.top
				}
				@Pc(88) val graphics: Graphics? = container!!.getGraphics()
				graphics!!.setColor(Color.black)
				if (leftGap > 0) {
					graphics.fillRect(left, top, leftGap, WindowMode.resizableScreenHeight)
				}
				if (topGap > 0) {
					graphics.fillRect(left, top, WindowMode.resizableScreenWidth, topGap)
				}
				if (rightGap > 0) {
					graphics.fillRect(WindowMode.resizableScreenWidth + left - rightGap, top, rightGap, WindowMode.resizableScreenHeight)
				}
				if (bottomGap > 0) {
					graphics.fillRect(left, WindowMode.resizableScreenHeight + top - bottomGap, WindowMode.resizableScreenWidth, bottomGap)
					return
				}
			} catch (@Pc(137) ex: Exception) {
				return
			}
		}

		@OriginalMember(owner = "client!rea", name = "c", descriptor = "Z")
		@Volatile
		@JvmField
		var fullWindowRepainted: Boolean = false
	}
}
