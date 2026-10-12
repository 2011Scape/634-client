package com.jagex.game.runetek5.client.window

import java.awt.DisplayMode
import java.awt.Frame
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.lang.reflect.Field
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!gp")
class WindowMode @OriginalMember(owner = "client!gp", name = "<init>", descriptor = "()V") @Throws(Exception::class) constructor() {

	@OriginalMember(owner = "client!gp", name = "b", descriptor = "Ljava/awt/DisplayMode;")
	private var displayMode: DisplayMode? = null

	@OriginalMember(owner = "client!gp", name = "a", descriptor = "Ljava/awt/GraphicsDevice;")
	private var fullscreenDevice: GraphicsDevice? = null

	init {
		run {
			@Pc(3) val graphicsEnvironment: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
			this.fullscreenDevice = graphicsEnvironment.getDefaultScreenDevice()
			if (!this.fullscreenDevice!!.isFullScreenSupported()) {
				@Pc(15) val graphicsDevices: Array<GraphicsDevice?> = graphicsEnvironment.getScreenDevices()
				@Pc(19) var i: Int = 0
				while (i < graphicsDevices.size) {
					@Pc(27) val graphicsDevice: GraphicsDevice? = graphicsDevices[i]
					if (graphicsDevice != null && graphicsDevice.isFullScreenSupported()) {
						this.fullscreenDevice = graphicsDevice
						return@run
					}
					i++
				}
				throw Exception()
			}
		}
	}

	@OriginalMember(owner = "client!gp", name = "enter", descriptor = "(Ljava/awt/Frame;IIII)V")
	private fun enter(@OriginalArg(0) frame: Frame?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) bitDepth: Int, @OriginalArg(4) targetRefreshRate: Int) {
		var targetRefreshRate: Int = targetRefreshRate
		this.displayMode = this.fullscreenDevice!!.getDisplayMode()
		if (this.displayMode == null) {
			throw NullPointerException()
		}
		frame!!.setUndecorated(true)
		frame.enableInputMethods(false)
		this.setFullScreenWindow(frame, 51.toByte())
		if (targetRefreshRate == 0) {
			@Pc(29) val refreshRate: Int = this.displayMode!!.getRefreshRate()
			@Pc(33) val displayModes: Array<DisplayMode> = this.fullscreenDevice!!.getDisplayModes()
			@Pc(35) var foundMatch: Boolean = false
			@Pc(37) var i: Int = 0
			while (i < displayModes.size) {
				if (width == displayModes[i].getWidth() && height == displayModes[i].getHeight() && displayModes[i].getBitDepth() == bitDepth) {
					@Pc(68) val modeRefreshRate: Int = displayModes[i].getRefreshRate()
					if (!foundMatch || Math.abs(modeRefreshRate - refreshRate) < Math.abs(targetRefreshRate - refreshRate)) {
						foundMatch = true
						targetRefreshRate = modeRefreshRate
					}
				}
				i++
			}
			if (!foundMatch) {
				targetRefreshRate = refreshRate
			}
		}
		this.fullscreenDevice!!.setDisplayMode(DisplayMode(width, height, bitDepth, targetRefreshRate))
	}

	@OriginalMember(owner = "client!gp", name = "listmodes", descriptor = "()[I")
	private fun listmodes(): IntArray? {
		@Pc(3) val displayModes: Array<DisplayMode> = this.fullscreenDevice!!.getDisplayModes()
		@Pc(9) val modes: IntArray = IntArray(displayModes.size shl 2)
		@Pc(11) var i: Int = 0
		while (i < displayModes.size) {
			modes[i shl 2] = displayModes[i].getWidth()
			modes[(i shl 2) + 1] = displayModes[i].getHeight()
			modes[(i shl 2) + 2] = displayModes[i].getBitDepth()
			modes[(i shl 2) + 3] = displayModes[i].getRefreshRate()
			i++
		}
		return modes
	}

	@OriginalMember(owner = "client!gp", name = "exit", descriptor = "()V")
	private fun exit() {
		if (this.displayMode != null) {
			this.fullscreenDevice!!.setDisplayMode(this.displayMode)
			if (!this.fullscreenDevice!!.getDisplayMode().equals(this.displayMode)) {
				throw RuntimeException("Did not return to correct resolution!")
			}
			this.displayMode = null
		}
		this.setFullScreenWindow(null, 104.toByte())
	}

	@OriginalMember(owner = "client!gp", name = "a", descriptor = "(Ljava/awt/Frame;B)V")
	private fun setFullScreenWindow(@OriginalArg(0) frame: Frame?, @OriginalArg(1) check: Byte) {
		@Pc(1) var validCleared: Boolean = false
		if (check <= 47) {
			this.setFullScreenWindow(null, (-25).toByte())
		}
		try {
			@Pc(14) val field: Field = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid")
			field.setAccessible(true)
			@Pc(24) val valid: Boolean = field.get(this.fullscreenDevice) as Boolean
			if (valid) {
				field.set(this.fullscreenDevice, java.lang.Boolean.FALSE)
				validCleared = true
			}
		} catch (@Pc(35) ex: Throwable) {
		}
		try {
			this.fullscreenDevice!!.setFullScreenWindow(frame)
		} finally {
			if (validCleared) {
				try {
					@Pc(64) val field2: Field = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid")
					field2.set(this.fullscreenDevice, java.lang.Boolean.TRUE)
				} catch (@Pc(71) ex2: Throwable) {
				}
			}
		}
	}

	companion object {
		@OriginalMember(owner = "client!lo", name = "o", descriptor = "I")
		@JvmField
		var resizableScreenHeight: Int = 0

		@OriginalMember(owner = "client!uu", name = "d", descriptor = "I")
		@JvmField
		var resizableScreenWidth: Int = 0
		@OriginalMember(owner = "client!ic", name = "o", descriptor = "Ljava/awt/Frame;")
		@JvmField
		var fullScreenFrame: Frame? = null
		@OriginalMember(owner = "client!db", name = "d", descriptor = "(I)I")
		@JvmStatic
		fun getWindowMode(): WindowModeType? {
			if (WindowMode.fullScreenFrame == null) {
				return if (WindowMode.resizableMode) WindowModeType.RESIZABLE else WindowModeType.FIXED
			} else {
				return WindowModeType.FULLSCREEN
			}
		}

		@OriginalMember(owner = "client!efa", name = "c", descriptor = "I")
		@JvmField
		var fullScreenWidth: Int = 0

		@OriginalMember(owner = "client!su", name = "a", descriptor = "(ZI)V")
		@JvmStatic
		fun interfaceResize(@OriginalArg(0) fireResizeHooks: Boolean) {
			InterfaceManager.updateInterfaceDimensions(GameShell.screenCanvasHeight, ClientState.BASE_WINDOW_ID, fireResizeHooks, GameShell.screenCanvasWidth)
		}
		@OriginalMember(owner = "client!kg", name = "k", descriptor = "Z")
		@JvmField
		var needsResize: Boolean = false
		@OriginalMember(owner = "client!mfa", name = "o", descriptor = "I")
		@JvmField
		var fullScreenHeight: Int = 0
		@OriginalMember(owner = "client!tga", name = "I", descriptor = "I")
		const val UNCHANGED_WINDOW_HEIGHT: Int = -1

		@OriginalMember(owner = "client!cq", name = "k", descriptor = "I")
		const val UNCHANGED_WINDOW_WIDTH: Int = -1

		@OriginalMember(owner = "client!gb", name = "p", descriptor = "Z")
		@JvmField
		var resizableMode: Boolean = true

		@OriginalMember(owner = "client!iha", name = "y", descriptor = "J")
		@JvmField
		var nextDimensionUpdateCheck: Long = 0L
	}
}
