package com.jagex.game.runetek5.client.window;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!gp")
public final class WindowMode {

	@OriginalMember(owner = "client!gp", name = "b", descriptor = "Ljava/awt/DisplayMode;")
	private DisplayMode displayMode;

	@OriginalMember(owner = "client!gp", name = "a", descriptor = "Ljava/awt/GraphicsDevice;")
	private GraphicsDevice fullscreenDevice;

	@OriginalMember(owner = "client!gp", name = "<init>", descriptor = "()V")
	public WindowMode() throws Exception {
		@Pc(3) GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.fullscreenDevice = graphicsEnvironment.getDefaultScreenDevice();
		if (!this.fullscreenDevice.isFullScreenSupported()) {
			@Pc(15) GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
			for (@Pc(19) int i = 0; i < graphicsDevices.length; i++) {
				@Pc(27) GraphicsDevice graphicsDevice = graphicsDevices[i];
				if (graphicsDevice != null && graphicsDevice.isFullScreenSupported()) {
					this.fullscreenDevice = graphicsDevice;
					return;
				}
			}
			throw new Exception();
		}
	}

	@OriginalMember(owner = "client!gp", name = "enter", descriptor = "(Ljava/awt/Frame;IIII)V")
	private void enter(@OriginalArg(0) Frame frame, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int bitDepth, @OriginalArg(4) int targetRefreshRate) {
		this.displayMode = this.fullscreenDevice.getDisplayMode();
		if (this.displayMode == null) {
			throw new NullPointerException();
		}
		frame.setUndecorated(true);
		frame.enableInputMethods(false);
		this.setFullScreenWindow(frame, (byte) 51);
		if (targetRefreshRate == 0) {
			@Pc(29) int refreshRate = this.displayMode.getRefreshRate();
			@Pc(33) DisplayMode[] displayModes = this.fullscreenDevice.getDisplayModes();
			@Pc(35) boolean foundMatch = false;
			for (@Pc(37) int i = 0; i < displayModes.length; i++) {
				if (width == displayModes[i].getWidth() && height == displayModes[i].getHeight() && displayModes[i].getBitDepth() == bitDepth) {
					@Pc(68) int modeRefreshRate = displayModes[i].getRefreshRate();
					if (!foundMatch || Math.abs(modeRefreshRate - refreshRate) < Math.abs(targetRefreshRate - refreshRate)) {
						foundMatch = true;
						targetRefreshRate = modeRefreshRate;
					}
				}
			}
			if (!foundMatch) {
				targetRefreshRate = refreshRate;
			}
		}
		this.fullscreenDevice.setDisplayMode(new DisplayMode(width, height, bitDepth, targetRefreshRate));
	}

	@OriginalMember(owner = "client!gp", name = "listmodes", descriptor = "()[I")
	private int[] listmodes() {
		@Pc(3) DisplayMode[] displayModes = this.fullscreenDevice.getDisplayModes();
		@Pc(9) int[] modes = new int[displayModes.length << 2];
		for (@Pc(11) int i = 0; i < displayModes.length; i++) {
			modes[i << 2] = displayModes[i].getWidth();
			modes[(i << 2) + 1] = displayModes[i].getHeight();
			modes[(i << 2) + 2] = displayModes[i].getBitDepth();
			modes[(i << 2) + 3] = displayModes[i].getRefreshRate();
		}
		return modes;
	}

	@OriginalMember(owner = "client!gp", name = "exit", descriptor = "()V")
	private void exit() {
		if (this.displayMode != null) {
			this.fullscreenDevice.setDisplayMode(this.displayMode);
			if (!this.fullscreenDevice.getDisplayMode().equals(this.displayMode)) {
				throw new RuntimeException("Did not return to correct resolution!");
			}
			this.displayMode = null;
		}
		this.setFullScreenWindow(null, (byte) 104);
	}

	@OriginalMember(owner = "client!gp", name = "a", descriptor = "(Ljava/awt/Frame;B)V")
	private void setFullScreenWindow(@OriginalArg(0) Frame frame, @OriginalArg(1) byte check) {
		@Pc(1) boolean validCleared = false;
		if (check <= 47) {
			this.setFullScreenWindow(null, (byte) -25);
		}
		try {
			@Pc(14) Field field = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid");
			field.setAccessible(true);
			@Pc(24) boolean valid = (Boolean) field.get(this.fullscreenDevice);
			if (valid) {
				field.set(this.fullscreenDevice, Boolean.FALSE);
				validCleared = true;
			}
		} catch (@Pc(35) Throwable ex) {
		}
		try {
			this.fullscreenDevice.setFullScreenWindow(frame);
		} finally {
			if (validCleared) {
				try {
					@Pc(64) Field field2 = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid");
					field2.set(this.fullscreenDevice, Boolean.TRUE);
				} catch (@Pc(71) Throwable ex2) {
				}
			}
		}
	}
	@OriginalMember(owner = "client!lo", name = "o", descriptor = "I")
	public static int resizableScreenHeight;

	@OriginalMember(owner = "client!uu", name = "d", descriptor = "I")
	public static int resizableScreenWidth;
	@OriginalMember(owner = "client!ic", name = "o", descriptor = "Ljava/awt/Frame;")
	public static Frame fullScreenFrame;
	@OriginalMember(owner = "client!db", name = "d", descriptor = "(I)I")
	public static WindowModeType getWindowMode() {
		if (WindowMode.fullScreenFrame == null) {
			return WindowMode.resizableMode ? WindowModeType.RESIZABLE : WindowModeType.FIXED;
		} else {
			return WindowModeType.FULLSCREEN;
		}
	}

	@OriginalMember(owner = "client!efa", name = "c", descriptor = "I")
	public static int fullScreenWidth;

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(ZI)V")
	public static void interfaceResize(@OriginalArg(0) boolean fireResizeHooks) {
		InterfaceManager.updateInterfaceDimensions(GameShell.screenCanvasHeight, ClientState.BASE_WINDOW_ID, fireResizeHooks, GameShell.screenCanvasWidth);
	}
	@OriginalMember(owner = "client!kg", name = "k", descriptor = "Z")
	public static boolean needsResize = false;
	@OriginalMember(owner = "client!mfa", name = "o", descriptor = "I")
	public static int fullScreenHeight;
	@OriginalMember(owner = "client!tga", name = "I", descriptor = "I")
	public static final int UNCHANGED_WINDOW_HEIGHT = -1;

	@OriginalMember(owner = "client!cq", name = "k", descriptor = "I")
	public static final int UNCHANGED_WINDOW_WIDTH = -1;

	@OriginalMember(owner = "client!gb", name = "p", descriptor = "Z")
	public static boolean resizableMode = true;

	@OriginalMember(owner = "client!iha", name = "y", descriptor = "J")
	public static long nextDimensionUpdateCheck = 0L;
}
