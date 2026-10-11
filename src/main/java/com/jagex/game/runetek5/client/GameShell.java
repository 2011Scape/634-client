package com.jagex.game.runetek5.client;

import com.jagex.game.runetek5.client.features.WindowIcons;
import com.jagex.game.runetek5.client.GameCanvas;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.config.vartype.player.CanvasRevalidateCounter;
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import com.jagex.game.runetek5.statics.PulseTimestampRing;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import jagex3.jagmisc.jagmisc;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import java.awt.Canvas;
import com.jagex.game.runetek5.client.cycle.AbstractCycleManager;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.cache.FileOnDisk;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!km")
public abstract class GameShell extends Applet implements Runnable, FocusListener, WindowListener {

	@OriginalMember(owner = "client!km", name = "q", descriptor = "Z")
	private boolean jagmiscLoaded = false;

	@OriginalMember(owner = "client!km", name = "A", descriptor = "Z")
	private boolean errorScreenRaised = false;

	@OriginalMember(owner = "client!km", name = "provideLoaderApplet", descriptor = "(Ljava/applet/Applet;)V")
	private static void provideLoaderApplet(@OriginalArg(0) Applet applet) {
		GameShell.applet = applet;
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(I)V")
	public abstract void mainquit();

	@OriginalMember(owner = "client!km", name = "stop", descriptor = "()V")
	@Override
	public final void stop() {
		if (GameShell.anApplet_Sub1_1 == this && !GameShell.isShutdown) {
			GameShell.killTime = Utils.currentTimeMillis() + 4000L;
		}
	}

	@OriginalMember(owner = "client!km", name = "getParameter", descriptor = "(Ljava/lang/String;)Ljava/lang/String;")
	@Override
	public final String getParameter(@OriginalArg(0) String name) {
		if (GameShell.engineFrame == null) {
			return GameShell.applet == null || GameShell.applet == this ? super.getParameter(name) : GameShell.applet.getParameter(name);
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(B)Ljava/lang/String;")
	public String getDebugString() {
		return null;
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ILjava/lang/String;)V")
	public final void raiseErrorScreen(@OriginalArg(1) String string) {
		if (this.errorScreenRaised) {
			return;
		}
		this.errorScreenRaised = true;
		System.out.println("error_game_" + string);
		try {
			BrowserJavaScript.callJavaScript(GameShell.applet, "loggedout");
		} catch (@Pc(34) Throwable ex) {
		}
		try {
			this.getAppletContext().showDocument(new URL(this.getCodeBase(), "error_game_" + string + ".ws"), "_top");
		} catch (@Pc(54) Exception ex2) {
		}
	}

	@OriginalMember(owner = "client!km", name = "windowActivated", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowActivated(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(Z)Z")
	public final boolean loadJagmisc() {
		return NativeLibraryOwner.loadLibrary("jagmisc");
	}

	@OriginalMember(owner = "client!km", name = "focusLost", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	@Override
	public final void focusLost(@OriginalArg(0) FocusEvent event) {
		GameShell.hasFocus = false;
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(I)V")
	private void onPulseWrapper() {
		@Pc(8) long currentTime = Utils.currentTimeMillis();
		@Pc(12) long lastTime = PulseTimestampRing.PULSE_TIMES[PulseTimestampRing.pulseTimesIndex];
		PulseTimestampRing.PULSE_TIMES[PulseTimestampRing.pulseTimesIndex] = currentTime;
		PulseTimestampRing.pulseTimesIndex = PulseTimestampRing.pulseTimesIndex + 1 & 0x1F;
		@Pc(35) boolean hasPreviousPulse;
		if (lastTime == 0L || lastTime >= currentTime) {
			hasPreviousPulse = false;
		} else {
			hasPreviousPulse = true;
		}
		synchronized (this) {
			GameShell.appletHasFocus = GameShell.hasFocus;
		}
		this.onPulse();
	}

	@OriginalMember(owner = "client!km", name = "update", descriptor = "(Ljava/awt/Graphics;)V")
	@Override
	public final void update(@OriginalArg(0) Graphics graphics) {
		this.paint(graphics);
	}

	@OriginalMember(owner = "client!km", name = "windowClosing", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowClosing(@OriginalArg(0) WindowEvent event) {
		this.destroy();
	}

	@OriginalMember(owner = "client!km", name = "getDocumentBase", descriptor = "()Ljava/net/URL;")
	@Override
	public final URL getDocumentBase() {
		if (GameShell.engineFrame == null) {
			return GameShell.applet == null || GameShell.applet == this ? super.getDocumentBase() : GameShell.applet.getDocumentBase();
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!km", name = "windowDeactivated", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowDeactivated(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "b", descriptor = "(B)V")
	public synchronized void addcanvas() {
		if (GameShell.gameCanvas != null) {
			GameShell.gameCanvas.removeFocusListener(this);
			GameShell.gameCanvas.getParent().setBackground(Color.black);
			GameShell.gameCanvas.getParent().remove(GameShell.gameCanvas);
		}
		@Pc(32) Container topContainer;
		if (WindowMode.fullScreenFrame != null) {
			topContainer = WindowMode.fullScreenFrame;
		} else if (GameShell.engineFrame != null) {
			topContainer = GameShell.engineFrame;
		} else if (GameShell.applet == null) {
			topContainer = GameShell.anApplet_Sub1_1;
		} else {
			topContainer = GameShell.applet;
		}
		topContainer.setLayout(null);
		GameShell.gameCanvas = new GameCanvas(this);
		topContainer.add(GameShell.gameCanvas);
		GameShell.gameCanvas.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
		GameShell.gameCanvas.setVisible(true);
		if (topContainer == GameShell.engineFrame) {
			@Pc(69) Insets insets = GameShell.engineFrame.getInsets();
			// Not part of the original client: with client.scale, the game offsets are in game pixels.
				GameShell.gameCanvas.setLocation(insets.left + ClientFeatures.scale(MiniMenu.gameCanvasX), insets.top - -ClientFeatures.scale(GameShell.gameCanvasY));
		} else {
			GameShell.gameCanvas.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY));
		}
		GameShell.gameCanvas.addFocusListener(this);
		GameShell.gameCanvas.requestFocus();
		GameShell.hasFocus = true;
		GameShell.appletHasFocus = true;
		GameShell.fullRedrawRequested = true;
		GameShell.fullWindowRepainted = false;
		GameShell.canvasInitializationTime = Utils.currentTimeMillis();
	}

	@OriginalMember(owner = "client!km", name = "windowOpened", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowOpened(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "paint", descriptor = "(Ljava/awt/Graphics;)V")
	@Override
	public final synchronized void paint(@OriginalArg(0) Graphics graphics) {
		if (GameShell.anApplet_Sub1_1 != this || GameShell.isShutdown) {
			return;
		}
		GameShell.fullRedrawRequested = true;
		if (GameShell.java5OrLater && Utils.currentTimeMillis() - GameShell.canvasInitializationTime > 1000L) {
			@Pc(26) Rectangle clipBounds = graphics.getClipBounds();
			if (clipBounds == null || WindowMode.resizableScreenWidth <= clipBounds.width && clipBounds.height >= WindowMode.resizableScreenHeight) {
				GameShell.fullWindowRepainted = true;
			}
		}
	}

	@OriginalMember(owner = "client!km", name = "c", descriptor = "(I)V")
	private void mainRedrawWrapper() {
		@Pc(6) long currentTime = Utils.currentTimeMillis();
		@Pc(10) long lastTime = GameShell.MAIN_REDRAW_CACHE[GameShell.mainRedrawCachePos];
		GameShell.MAIN_REDRAW_CACHE[GameShell.mainRedrawCachePos] = currentTime;
		if (lastTime != 0L && lastTime < currentTime) {
			@Pc(27) int timeDelta = (int) (currentTime - lastTime);
			Camera.fps = ((timeDelta >> 1) + 32000) / timeDelta;
		}
		GameShell.mainRedrawCachePos = GameShell.mainRedrawCachePos + 1 & 0x1F;
		if (CanvasRevalidateCounter.revalidateCounter++ > 50) {
			CanvasRevalidateCounter.revalidateCounter -= 50;
			GameShell.fullRedrawRequested = true;
			GameShell.gameCanvas.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
			GameShell.gameCanvas.setVisible(true);
			if (GameShell.engineFrame != null && WindowMode.fullScreenFrame == null) {
				@Pc(68) Insets insets = GameShell.engineFrame.getInsets();
				GameShell.gameCanvas.setLocation(insets.left + ClientFeatures.scale(MiniMenu.gameCanvasX), insets.top + ClientFeatures.scale(GameShell.gameCanvasY));
			} else {
				GameShell.gameCanvas.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY));
			}
		}
		this.mainRedraw();
	}

	@OriginalMember(owner = "client!km", name = "windowDeiconified", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowDeiconified(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "focusGained", descriptor = "(Ljava/awt/event/FocusEvent;)V")
	@Override
	public final void focusGained(@OriginalArg(0) FocusEvent event) {
		GameShell.hasFocus = true;
		GameShell.fullRedrawRequested = true;
	}

	@OriginalMember(owner = "client!km", name = "windowClosed", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowClosed(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "d", descriptor = "(I)Z")
	public final boolean isHostnameValid() {
		@Pc(13) String string = this.getDocumentBase().getHost().toLowerCase();
		if (string.equals("jagex.com") || string.endsWith(".jagex.com")) {
			return true;
		} else if (string.equals("runescape.com") || string.endsWith(".runescape.com")) {
			return true;
		} else if (string.equals("stellardawn.com") || string.endsWith(".stellardawn.com")) {
			return true;
		} else if (string.endsWith("127.0.0.1")) {
			return true;
		} else {
			while (string.length() > 0 && string.charAt(string.length() - 1) >= '0' && string.charAt(string.length() - 1) <= '9') {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith("192.168.1.")) {
				return true;
			} else {
				this.raiseErrorScreen("invalidhost");
				return false;
			}
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ZZ)V")
	private void prepareExit(@OriginalArg(0) boolean clean) {
		synchronized (this) {
			if (GameShell.isShutdown) {
				return;
			}
			GameShell.isShutdown = true;
		}
		System.out.println("Shutdown start - clean:" + clean);
		if (GameShell.applet != null) {
			GameShell.applet.destroy();
		}
		try {
			this.mainquit();
		} catch (@Pc(44) Exception ex) {
		}
		if (this.jagmiscLoaded) {
			try {
				jagmisc.quit();
			} catch (@Pc(50) Throwable ex2) {
			}
			this.jagmiscLoaded = false;
		}
		SoftwareMemoryManager.markShutdown();
		NativeLibraryOwner.unloadNativeLibraries();
		if (GameShell.gameCanvas != null) {
			try {
				GameShell.gameCanvas.removeFocusListener(this);
				GameShell.gameCanvas.getParent().remove(GameShell.gameCanvas);
			} catch (@Pc(74) Exception ex3) {
			}
		}
		if (GameShell.signLink != null) {
			try {
				GameShell.signLink.shutdown();
			} catch (@Pc(82) Exception ex4) {
			}
		}
		this.cleanup();
		if (GameShell.engineFrame != null) {
			GameShell.engineFrame.setVisible(false);
			GameShell.engineFrame.dispose();
			GameShell.engineFrame = null;
		}
		System.out.println("Shutdown complete - clean:" + clean);
	}

	@OriginalMember(owner = "client!km", name = "c", descriptor = "(B)V")
	public abstract void cleanup();

	@OriginalMember(owner = "client!km", name = "getAppletContext", descriptor = "()Ljava/applet/AppletContext;")
	@Override
	public final AppletContext getAppletContext() {
		if (GameShell.engineFrame == null) {
			return GameShell.applet == null || GameShell.applet == this ? super.getAppletContext() : GameShell.applet.getAppletContext();
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!km", name = "getCodeBase", descriptor = "()Ljava/net/URL;")
	@Override
	public final URL getCodeBase() {
		if (GameShell.engineFrame == null) {
			return GameShell.applet == null || GameShell.applet == this ? super.getCodeBase() : GameShell.applet.getCodeBase();
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!km", name = "init", descriptor = "()V")
	public abstract void init();

	@OriginalMember(owner = "client!km", name = "destroy", descriptor = "()V")
	@Override
	public final void destroy() {
		if (GameShell.anApplet_Sub1_1 == this && !GameShell.isShutdown) {
			GameShell.killTime = Utils.currentTimeMillis();
			TimeUtils.sleep(5000L);
			SignLink.PACKET_3 = null;
			this.prepareExit(false);
		}
	}

	@OriginalMember(owner = "client!km", name = "run", descriptor = "()V")
	@Override
	public final void run() {
		try {
			if (PlayerUpdate.javaVendor != null) {
				@Pc(10) String string = PlayerUpdate.javaVendor.toLowerCase();
				if (string.indexOf("sun") != -1 || string.indexOf("apple") != -1) {
					@Pc(23) String string2 = SystemInfo.javaVersion;
					if (string2.equals("1.1") || string2.startsWith("1.1.") || string2.equals("1.2") || string2.startsWith("1.2.")) {
						this.raiseErrorScreen("wrongjava");
						return;
					}
				} else if (string.indexOf("ibm") != -1 && (SystemInfo.javaVersion == null || SystemInfo.javaVersion.equals("1.4.2"))) {
					this.raiseErrorScreen("wrongjava");
					return;
				}
			}
			if (SystemInfo.javaVersion != null && SystemInfo.javaVersion.startsWith("1.")) {
				@Pc(71) int charIndex = 2;
				@Pc(73) int javaVersionNumber = 0;
				while (charIndex < SystemInfo.javaVersion.length()) {
					@Pc(79) char versionChar = SystemInfo.javaVersion.charAt(charIndex);
					if (versionChar < '0' || versionChar > '9') {
						break;
					}
					charIndex++;
					javaVersionNumber = javaVersionNumber * 10 + versionChar - 48;
				}
				if (javaVersionNumber >= 5) {
					GameShell.java5OrLater = true;
				}
			}
			@Pc(114) Applet applet = GameShell.anApplet_Sub1_1;
			if (GameShell.applet != null) {
				applet = GameShell.applet;
			}
			@Pc(120) Method method = SignLinkEnvironment.setFocusCycleRootMethod;
			if (method != null) {
				try {
					method.invoke(applet, Boolean.TRUE);
				} catch (@Pc(134) Throwable ex) {
				}
			}
			GameShell.detectMaxMemory();
			GameShell.detectAvailableProcessors();
			this.addcanvas();
			this.maininit();
			GameShell.cycleManager = AbstractCycleManager.create();
			while (GameShell.killTime == 0L || GameShell.killTime > Utils.currentTimeMillis()) {
				if (ClientFeatures.FPS > 0) {
					// Not part of the original client: client.fps paces the frames, and the game ticks
					// are counted from the clock at the client's own interval, so the game keeps its speed.
					GameShell.cycleManager.loopCycle(1000000000L / (long) ClientFeatures.FPS);
					GameShell.loopsInCycle = ClientFeatures.ticksDue(GameShell.pulseIntervalNanos);
				} else {
					GameShell.loopsInCycle = GameShell.cycleManager.loopCycle(GameShell.pulseIntervalNanos);
				}
				for (@Pc(157) int i = 0; i < GameShell.loopsInCycle; i++) {
					this.onPulseWrapper();
				}
				this.mainRedrawWrapper();
				GameShell.performDummyActionEvent(GameShell.gameCanvas, GameShell.signLink);
			}
		} catch (@Pc(188) ThreadDeath threadDeath) {
			throw threadDeath;
		} catch (@Pc(191) Throwable ex2) {
			ExplicitRuntimeException.reportError(this.getDebugString(), ex2);
			this.raiseErrorScreen("crash");
		} finally {
			@Pc(207) Object object = null;
			this.prepareExit(true);
		}
	}

	@OriginalMember(owner = "client!km", name = "e", descriptor = "(I)V")
	public abstract void maininit();

	@OriginalMember(owner = "client!km", name = "f", descriptor = "(I)V")
	public abstract void mainRedraw();

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(IIIIILjava/lang/String;I)V")
	public final void startApplet(@OriginalArg(0) int width, @OriginalArg(2) int height, @OriginalArg(3) int cacheId, @OriginalArg(5) String string) {
		try {
			if (GameShell.anApplet_Sub1_1 == null) {
				MiniMenu.gameCanvasX = 0;
				GameShell.screenCanvasHeight = height;
				WindowMode.resizableScreenHeight = height;
				GameShell.screenCanvasWidth = width;
				WindowMode.resizableScreenWidth = width;
				GameShell.gameCanvasY = 0;
				SystemInfo.clientBuild = 634;
				GameShell.anApplet_Sub1_1 = this;
				ExplicitRuntimeException.applet = GameShell.applet;
				SignLink.PACKET_3 = GameShell.signLink = new SignLink(cacheId, string, 37, GameShell.applet != null);
				@Pc(64) SignedResource signedResource = GameShell.signLink.startThread(this, 1);
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(10L);
				}
			} else {
				GameShell.alreadyLoadedCount++;
				if (GameShell.alreadyLoadedCount >= 3) {
					this.raiseErrorScreen("alreadyloaded");
				} else {
					this.getAppletContext().showDocument(this.getDocumentBase(), "_self");
				}
			}
		} catch (@Pc(82) Throwable ex) {
			ExplicitRuntimeException.reportError(null, ex);
			this.raiseErrorScreen("crash");
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(IIZIILjava/lang/String;II)V")
	public final void startApplication(@OriginalArg(0) int cacheId, @OriginalArg(5) String string) {
		try {
			GameShell.screenCanvasWidth = 1024;
			WindowMode.resizableScreenWidth = 1024;
			GameShell.screenCanvasHeight = 768;
			WindowMode.resizableScreenHeight = 768;
			GameShell.anApplet_Sub1_1 = this;
			GameShell.gameCanvasY = 0;
			MiniMenu.gameCanvasX = 0;
			SystemInfo.clientBuild = 634;
			ExplicitRuntimeException.applet = null;
			GameShell.engineFrame = new Frame();
			// Not part of the original client: the window is titled Void, not Jagex.
			GameShell.engineFrame.setTitle("Void");
			// Not part of the original client: the window carries Void's icon (WindowIcons).
			WindowIcons.apply(GameShell.engineFrame);
			GameShell.engineFrame.setResizable(true);
			GameShell.engineFrame.addWindowListener(this);
			GameShell.engineFrame.setVisible(true);
			GameShell.engineFrame.toFront();
			@Pc(44) Insets insets = GameShell.engineFrame.getInsets();
			// Not part of the original client: with client.scale, the window opens that many times larger.
			GameShell.engineFrame.setSize(insets.right + insets.left + ClientFeatures.scale(WindowMode.resizableScreenWidth), insets.bottom + ClientFeatures.scale(WindowMode.resizableScreenHeight) + insets.top);
			SignLink.PACKET_3 = GameShell.signLink = new SignLink(cacheId, string, 37, true);
			@Pc(78) SignedResource signedResource = GameShell.signLink.startThread(this, 1);
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(10L);
			}
		} catch (@Pc(88) Exception ex) {
			ExplicitRuntimeException.reportError(null, ex);
		}
	}

	@OriginalMember(owner = "client!km", name = "windowIconified", descriptor = "(Ljava/awt/event/WindowEvent;)V")
	@Override
	public final void windowIconified(@OriginalArg(0) WindowEvent event) {
	}

	@OriginalMember(owner = "client!km", name = "g", descriptor = "(I)Z")
	public final boolean loadJaclib() {
		return NativeLibraryOwner.loadLibrary("jaclib");
	}

	@OriginalMember(owner = "client!km", name = "start", descriptor = "()V")
	@Override
	public final void start() {
		if (GameShell.anApplet_Sub1_1 == this && !GameShell.isShutdown) {
			GameShell.killTime = 0L;
		}
	}

	@OriginalMember(owner = "client!km", name = "d", descriptor = "(B)V")
	public abstract void onPulse();

	@OriginalMember(owner = "client!km", name = "i", descriptor = "(I)Z")
	public final boolean loadJagtheora() {
		return NativeLibraryOwner.loadLibrary("jagtheora");
	}

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(BLjava/lang/Object;Lclient!wea;)V")
	public static void performDummyActionEvent(@OriginalArg(1) Object object, @OriginalArg(2) SignLink signLink) {
		if (signLink.eventQueue == null) {
			return;
		}
		for (@Pc(11) int i = 0; i < 50 && signLink.eventQueue.peekEvent() != null; i++) {
			TimeUtils.sleep(1L);
		}
		try {
			if (object != null) {
				signLink.eventQueue.postEvent(new ActionEvent(object, 1001, "dummy"));
			}
		} catch (@Pc(44) Exception ex) {
		}
	}

	@OriginalMember(owner = "client!nl", name = "e", descriptor = "I")
	public static int gameAreaWidth = 0;
	@OriginalMember(owner = "client!ol", name = "b", descriptor = "I")
	public static int mainRedrawCachePos;

	@OriginalMember(owner = "client!cq", name = "w", descriptor = "I")
	public static int screenCanvasWidth;

	@OriginalMember(owner = "client!sd", name = "c", descriptor = "Z")
	public static boolean softwareMode = false;

	@OriginalMember(owner = "client!dg", name = "h", descriptor = "Z")
	public static boolean openglMode = false;

	@OriginalMember(owner = "client!ab", name = "B", descriptor = "Z")
	public static boolean directxMode = false;

	@OriginalMember(owner = "client!wr", name = "r", descriptor = "Ljava/awt/Canvas;")
	public static Canvas gameCanvas;
	@OriginalMember(owner = "client!cea", name = "Fb", descriptor = "[J")
	public static final long[] MAIN_REDRAW_CACHE = new long[32];
	@OriginalMember(owner = "client!gia", name = "u", descriptor = "Lclient!tb;")
	public static AbstractCycleManager cycleManager;

	@OriginalMember(owner = "client!haa", name = "jb", descriptor = "I")
	public static int loopsInCycle;
	@OriginalMember(owner = "client!eg", name = "a", descriptor = "Z")
	public static boolean lastSentFocus = true;
	@OriginalMember(owner = "client!lw", name = "a", descriptor = "(Z)V")
	public static void resetDrawCache() {
		GameShell.cycleManager.reset();
		for (@Pc(15) int i = 0; i < 32; i++) {
			GameShell.MAIN_REDRAW_CACHE[i] = 0L;
		}
		for (@Pc(27) int j = 0; j < 32; j++) {
			PulseTimestampRing.PULSE_TIMES[j] = 0L;
		}
		GameShell.loopsInCycle = 0;
	}
	@OriginalMember(owner = "client!ep", name = "a", descriptor = "(Z)V")
	public static void detectAvailableProcessors() {
		try {
			@Pc(16) Method method = Runtime.class.getMethod("availableProcessors");
			if (method != null) {
				try {
					@Pc(28) Runtime runtime = Runtime.getRuntime();
					@Pc(34) Integer integer = (Integer) method.invoke(runtime, (Object[]) null);
					GameShell.availableProcessors = integer;
				} catch (@Pc(39) Throwable ex) {
				}
			}
		} catch (@Pc(41) Exception ex2) {
		}
	}

	@OriginalMember(owner = "client!jca", name = "k", descriptor = "Ljava/awt/Frame;")
	public static Frame engineFrame;
	@OriginalMember(owner = "client!pf", name = "K", descriptor = "I")
	public static int screenCanvasHeight;

	@OriginalMember(owner = "client!sga", name = "f", descriptor = "I")
	public static int maxMemory = 64;

	@OriginalMember(owner = "client!vf", name = "K", descriptor = "I")
	public static int availableProcessors = 1;

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(IBLclient!cea;)V")
	public static void updateRandomDatUID192(@OriginalArg(0) int startPosition, @OriginalArg(2) Packet packet) {
		if (GameShell.aClass184_2 == null) {
			return;
		}
		try {
			GameShell.aClass184_2.seek(0L);
			GameShell.aClass184_2.write(startPosition, 24, packet.buffer);
		} catch (@Pc(19) Exception ex) {
		}
	}
	@OriginalMember(owner = "client!kj", name = "L", descriptor = "Lclient!km;")
	public static GameShell anApplet_Sub1_1 = null;

	@OriginalMember(owner = "client!bf", name = "a", descriptor = "J")
	public static long killTime = 0L;
	@OriginalMember(owner = "client!nba", name = "b", descriptor = "(B)V")
	public static void detectMaxMemory() {
		if (GameShell.signLink.microsoftJava) {
			GameShell.maxMemory = 96;
			return;
		}
		try {
			@Pc(15) Method method = Runtime.class.getMethod("maxMemory");
			if (method != null) {
				try {
					@Pc(19) Runtime runtime = Runtime.getRuntime();
					@Pc(25) Long long2 = (Long) method.invoke(runtime, (Object[]) null);
					GameShell.maxMemory = (int) (long2 / 1048576L) + 1;
				} catch (@Pc(35) Throwable ex) {
				}
			}
		} catch (@Pc(37) Exception ex2) {
		}
	}

	@OriginalMember(owner = "client!kk", name = "l", descriptor = "J")
	public static long pulseIntervalNanos = 20000000L;

	@OriginalMember(owner = "client!dh", name = "Z", descriptor = "I")
	public static int alreadyLoadedCount = 0;
	@OriginalMember(owner = "client!hs", name = "l", descriptor = "Z")
	public static boolean isShutdown = false;

	@OriginalMember(owner = "client!ds", name = "b", descriptor = "I")
	public static int gameCanvasY = 0;

	@OriginalMember(owner = "client!lt", name = "b", descriptor = "Ljava/applet/Applet;")
	public static Applet applet;

	@OriginalMember(owner = "client!lia", name = "a", descriptor = "I")
	public static int GAME_WIDTH = 765;

	@OriginalMember(owner = "client!gca", name = "c", descriptor = "Lclient!l;")
	public static FileOnDisk aClass184_2;
	@OriginalMember(owner = "client!tb", name = "b", descriptor = "(B)J")
	public static long getCurrentCycleNanos() {
		return GameShell.cycleManager.getCurrentCycle();
	}

	@OriginalMember(owner = "client!bk", name = "u", descriptor = "Z")
	public static boolean java5OrLater = false;
	@OriginalMember(owner = "client!jm", name = "z", descriptor = "Lclient!wea;")
	public static SignLink signLink;
	@OriginalMember(owner = "client!pq", name = "t", descriptor = "Z")
	public static boolean appletHasFocus;
	@OriginalMember(owner = "client!av", name = "u", descriptor = "Ljava/lang/String;")
	public static String siteSettings = null;

	@OriginalMember(owner = "client!iia", name = "a", descriptor = "(Ljava/lang/String;Z)V")
	public static void setSiteSettingsCookie(@OriginalArg(0) String settings) {
		GameShell.siteSettings = settings;
		if (GameShell.applet == null) {
			return;
		}
		try {
			@Pc(18) String string2 = GameShell.applet.getParameter("cookieprefix");
			@Pc(22) String string3 = GameShell.applet.getParameter("cookiehost");
			@Pc(37) String string4 = string2 + "settings=" + settings + "; version=1; path=/; domain=" + string3;
			if (settings.length() == 0) {
				string4 = string4 + "; Expires=Thu, 01-Jan-1970 00:00:00 GMT; Max-Age=0";
			} else {
				string4 = string4 + "; Expires=" + TimeUtils.formatCookieDate(Utils.currentTimeMillis() + TimeUtils.MILLISECONDS_PER_THREE_YEARS) + "; Max-Age=" + 94608000L;
			}
			BrowserJavaScript.evalJavaScript(GameShell.applet, "document.cookie=\"" + string4 + "\"");
		} catch (@Pc(88) Throwable ex) {
		}
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Ljava/lang/String;B)V")
	public static void exitWithError(@OriginalArg(0) String string) {
		System.exit(1);
	}

	@OriginalMember(owner = "client!pc", name = "n", descriptor = "Z")
	public static boolean force64Mb = false;

	@OriginalMember(owner = "client!pda", name = "P", descriptor = "Z")
	public static volatile boolean hasFocus = true;

	@OriginalMember(owner = "client!bm", name = "y", descriptor = "Ljava/lang/String;")
	public static String additionalInfo = null;

	@OriginalMember(owner = "client!tda", name = "g", descriptor = "J")
	public static long machineUid = 0L;

	@OriginalMember(owner = "client!tr", name = "f", descriptor = "I")
	public static int worldFlags = 0;

	@OriginalMember(owner = "client!un", name = "I", descriptor = "Z")
	public static boolean advertEnabled = false;

	@OriginalMember(owner = "client!ha", name = "w", descriptor = "I")
	public static int country;

	@OriginalMember(owner = "client!km", name = "P", descriptor = "I")
	public static int anInt2389;

	@OriginalMember(owner = "client!km", name = "Q", descriptor = "I")
	public static int anInt2390;

	@OriginalMember(owner = "client!km", name = "R", descriptor = "I")
	public static int anInt2391;

	@OriginalMember(owner = "client!km", name = "S", descriptor = "I")
	public static int anInt2392;

	@OriginalMember(owner = "client!km", name = "T", descriptor = "Z")
	public static boolean aBoolean196;

	@OriginalMember(owner = "client!km", name = "U", descriptor = "Z")
	public static boolean aBoolean197;

	@OriginalMember(owner = "client!km", name = "V", descriptor = "Z")
	public static boolean aBoolean198;

	@OriginalMember(owner = "client!km", name = "W", descriptor = "Z")
	public static boolean aBoolean199;

	@OriginalMember(owner = "client!km", name = "X", descriptor = "Z")
	public static boolean aBoolean200;

	@OriginalMember(owner = "client!km", name = "Y", descriptor = "Z")
	public static boolean aBoolean201;

	@OriginalMember(owner = "client!km", name = "Z", descriptor = "Z")
	public static boolean aBoolean202;

	@OriginalMember(owner = "client!km", name = "ab", descriptor = "I")
	public static int anInt2393;

	@OriginalMember(owner = "client!km", name = "bb", descriptor = "Z")
	public static boolean aBoolean203;

	@OriginalMember(owner = "client!km", name = "cb", descriptor = "I")
	public static int anInt2394;

	@OriginalMember(owner = "client!km", name = "db", descriptor = "Z")
	public static boolean aBoolean204;

	@OriginalMember(owner = "client!km", name = "eb", descriptor = "I")
	public static int anInt2395;

	@OriginalMember(owner = "client!km", name = "fb", descriptor = "Z")
	public static boolean aBoolean205;

	@OriginalMember(owner = "client!km", name = "gb", descriptor = "I")
	public static int anInt2396;

	@OriginalMember(owner = "client!ar", name = "C", descriptor = "J")
	public static volatile long canvasInitializationTime = 0L;

	@OriginalMember(owner = "client!sa", name = "p", descriptor = "Z")
	public static boolean objectTag = false;

	@OriginalMember(owner = "client!re", name = "y", descriptor = "I")
	public static int AFFILIATE = 0;

	@OriginalMember(owner = "client!wc", name = "a", descriptor = "(IB)V")
	public static void setPulseRate(@OriginalArg(0) int cyclesPerSecond) {
		GameShell.pulseIntervalNanos = 1000000000L / (long) cyclesPerSecond;
	}

	@OriginalMember(owner = "client!op", name = "c", descriptor = "Z")
	public static boolean siteSettingsMember = false;

	@OriginalMember(owner = "client!gf", name = "b", descriptor = "Z")
	public static boolean fromBilling = false;

	@OriginalMember(owner = "client!it", name = "h", descriptor = "Z")
	public static volatile boolean fullRedrawRequested = true;

	@OriginalMember(owner = "client!nb", name = "d", descriptor = "Ljava/lang/String;")
	public static String quitUrl;

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(B)V")
	public static void clearCanvasBackground() {
		if (WindowMode.fullScreenFrame != null) {
			return;
		}
		@Pc(10) int leftGap = MiniMenu.gameCanvasX;
		@Pc(12) int topGap = GameShell.gameCanvasY;
		@Pc(27) int rightGap = WindowMode.resizableScreenWidth - leftGap - GameShell.screenCanvasWidth;
		@Pc(35) int bottomGap = WindowMode.resizableScreenHeight - topGap - GameShell.screenCanvasHeight;
		if (leftGap <= 0 && rightGap <= 0 && topGap <= 0 && bottomGap <= 0) {
			return;
		}
		try {
			@Pc(61) Container container;
			if (GameShell.engineFrame != null) {
				container = GameShell.engineFrame;
			} else if (GameShell.applet == null) {
				container = GameShell.anApplet_Sub1_1;
			} else {
				container = GameShell.applet;
			}
			@Pc(71) int left = 0;
			@Pc(73) int top = 0;
			if (container == GameShell.engineFrame) {
				@Pc(79) Insets insets = GameShell.engineFrame.getInsets();
				left = insets.left;
				top = insets.top;
			}
			@Pc(88) Graphics graphics = container.getGraphics();
			graphics.setColor(Color.black);
			if (leftGap > 0) {
				graphics.fillRect(left, top, leftGap, WindowMode.resizableScreenHeight);
			}
			if (topGap > 0) {
				graphics.fillRect(left, top, WindowMode.resizableScreenWidth, topGap);
			}
			if (rightGap > 0) {
				graphics.fillRect(WindowMode.resizableScreenWidth + left - rightGap, top, rightGap, WindowMode.resizableScreenHeight);
			}
			if (bottomGap > 0) {
				graphics.fillRect(left, WindowMode.resizableScreenHeight + top - bottomGap, WindowMode.resizableScreenWidth, bottomGap);
				return;
			}
		} catch (@Pc(137) Exception ex) {
			return;
		}
	}

	@OriginalMember(owner = "client!rea", name = "c", descriptor = "Z")
	public static volatile boolean fullWindowRepainted = false;
}
