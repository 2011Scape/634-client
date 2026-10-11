package com.jagex.game.runetek5.sign;

import com.jagex.game.runetek5.client.features.WindowIcons;
import com.jagex.game.runetek5.DisplayModeDetails;
import com.jagex.game.runetek5.core.constants.FileStore;
import com.jagex.game.runetek5.core.constants.SignedResourceType;
import com.jagex.game.runetek5.cache.JFS32;
import com.jagex.game.runetek5.core.io.socket.ProxyAuthenticationException;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.graphics.ms.MicrosoftFullscreenAdapter;
import com.jagex.game.runetek5.graphics.ms.MicrosoftJavaMouseCallback;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.network.SocketManager;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.cache.FileOnDisk;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.CacheFileLocator;

@OriginalClass("client!wea")
public final class SignLink implements Runnable {

	@OriginalMember(owner = "client!wea", name = "d", descriptor = "Lclient!so;")
	public JFS32 cacheIndex255 = null;

	@OriginalMember(owner = "client!wea", name = "j", descriptor = "Lclient!so;")
	public JFS32 cacheDat = null;

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "Z")
	public boolean signed = false;

	@OriginalMember(owner = "client!wea", name = "w", descriptor = "Lclient!of;")
	private SignedResource last = null;

	@OriginalMember(owner = "client!wea", name = "z", descriptor = "Z")
	private boolean stopped = false;

	@OriginalMember(owner = "client!wea", name = "m", descriptor = "Lclient!so;")
	public JFS32 randomDat = null;

	@OriginalMember(owner = "client!wea", name = "s", descriptor = "Z")
	public boolean microsoftJava = false;

	@OriginalMember(owner = "client!wea", name = "v", descriptor = "Lclient!of;")
	private SignedResource current = null;

	@OriginalMember(owner = "client!wea", name = "x", descriptor = "Ljava/awt/EventQueue;")
	public EventQueue eventQueue;

	@OriginalMember(owner = "client!wea", name = "t", descriptor = "[Lclient!so;")
	public JFS32[] cacheIndex;

	@OriginalMember(owner = "client!wea", name = "l", descriptor = "Ljava/lang/Object;")
	private Object microsoftPcmDevice;

	@OriginalMember(owner = "client!wea", name = "r", descriptor = "Ljava/lang/Object;")
	private Object windowMode;

	@OriginalMember(owner = "client!wea", name = "A", descriptor = "Lclient!nt;")
	private MicrosoftFullscreenAdapter microsoftFullscreenAdapter;

	@OriginalMember(owner = "client!wea", name = "p", descriptor = "Ljava/lang/Object;")
	private Object cursorManager;

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "Lclient!lq;")
	private MicrosoftJavaMouseCallback mouseCallback;

	@OriginalMember(owner = "client!wea", name = "o", descriptor = "Ljava/lang/Thread;")
	private final Thread thread;

	@OriginalMember(owner = "client!wea", name = "<init>", descriptor = "(ILjava/lang/String;IZ)V")
	public SignLink(@OriginalArg(0) int cacheId, @OriginalArg(1) String string, @OriginalArg(2) int cacheIndexCount, @OriginalArg(3) boolean signed) throws Exception {
		SignLinkEnvironment.gameName = string;
		this.signed = signed;
		SystemInfo.javaVersion = "1.1";
		PlayerUpdate.javaVendor = "Unknown";
		SignLink.cacheId = cacheId;
		try {
			PlayerUpdate.javaVendor = System.getProperty("java.vendor");
			SystemInfo.javaVersion = System.getProperty("java.version");
		} catch (@Pc(44) Exception ex) {
		}
		if (PlayerUpdate.javaVendor.toLowerCase().indexOf("microsoft") != -1) {
			this.microsoftJava = true;
		}
		try {
			SignLinkEnvironment.operatingSystem = System.getProperty("os.name");
		} catch (@Pc(59) Exception ex2) {
			SignLinkEnvironment.operatingSystem = "Unknown";
		}
		SystemInfo.operatingSystemLowercase = SignLinkEnvironment.operatingSystem.toLowerCase();
		try {
			SystemInfo.OS_ARCH = System.getProperty("os.arch").toLowerCase();
		} catch (@Pc(70) Exception ex3) {
			SystemInfo.OS_ARCH = "";
		}
		try {
			SystemInfo.osVersion = System.getProperty("os.version").toLowerCase();
		} catch (@Pc(78) Exception ex4) {
			SystemInfo.osVersion = "";
		}
		try {
			SignLinkEnvironment.preferencesParentFile = System.getProperty("user.home");
			if (SignLinkEnvironment.preferencesParentFile != null) {
				SignLinkEnvironment.preferencesParentFile = SignLinkEnvironment.preferencesParentFile + "/";
			}
		} catch (@Pc(96) Exception ex5) {
		}
		if (SignLinkEnvironment.preferencesParentFile == null) {
			SignLinkEnvironment.preferencesParentFile = "~/";
		}
		try {
			this.eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		} catch (@Pc(106) Throwable ex6) {
		}
		if (!this.microsoftJava) {
			try {
				SignLinkEnvironment.setFocusTraversalKeysEnabledMethod = Class.forName("java.awt.Component").getDeclaredMethod("setFocusTraversalKeysEnabled", Boolean.TYPE);
			} catch (@Pc(122) Exception ex7) {
			}
			try {
				SignLinkEnvironment.setFocusCycleRootMethod = Class.forName("java.awt.Container").getDeclaredMethod("setFocusCycleRoot", Boolean.TYPE);
			} catch (@Pc(135) Exception ex8) {
			}
		}
		CacheFileLocator.initialize(SignLinkEnvironment.gameName, SignLink.cacheId);
		if (this.signed) {
			this.randomDat = new JFS32(CacheFileLocator.getCacheFile(SignLink.cacheId, "random.dat", null), "rw", 25L);
			this.cacheDat = new JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.dat2"), "rw", 209715200L);
			this.cacheIndex255 = new JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.idx255"), "rw", 1048576L);
			this.cacheIndex = new JFS32[cacheIndexCount];
			for (@Pc(181) int i = 0; i < cacheIndexCount; i++) {
				this.cacheIndex[i] = new JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.idx" + i), "rw", 1048576L);
			}
			if (this.microsoftJava) {
				try {
					this.microsoftPcmDevice = Class.forName("com.jagex.game.runetek5.DirectSoundPcmDevice").getDeclaredConstructor().newInstance();
				} catch (@Pc(218) Throwable ex9) {
				}
			}
			try {
				if (this.microsoftJava) {
					this.microsoftFullscreenAdapter = new MicrosoftFullscreenAdapter();
				} else {
					this.windowMode = Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getDeclaredConstructor().newInstance();
				}
			} catch (@Pc(235) Throwable ex10) {
			}
			try {
				if (this.microsoftJava) {
					this.mouseCallback = new MicrosoftJavaMouseCallback();
				} else {
					this.cursorManager = Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredConstructor().newInstance();
				}
			} catch (@Pc(251) Throwable ex11) {
			}
		}
		if (this.signed && !this.microsoftJava) {
			@Pc(261) ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
			for (@Pc(264) ThreadGroup threadGroup2 = threadGroup.getParent(); threadGroup2 != null; threadGroup2 = threadGroup2.getParent()) {
				threadGroup = threadGroup2;
			}
			@Pc(275) Thread[] threads = new Thread[1000];
			threadGroup.enumerate(threads);
			for (@Pc(281) int j = 0; j < threads.length; j++) {
				if (threads[j] != null && threads[j].getName().startsWith("AWT")) {
					threads[j].setPriority(1);
				}
			}
		}
		this.stopped = false;
		this.thread = new Thread(this);
		this.thread.setPriority(10);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(IIIIB)Lclient!of;")
	public SignedResource enterFullscreen(@OriginalArg(1) int height, @OriginalArg(2) int bitDepth, @OriginalArg(3) int width) {
		return this.enqueue(bitDepth << 16, SignedResourceType.FULLSCREEN_ENTER, height + (width << 16), null);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "([Ljava/lang/Class;ILjava/lang/Class;Ljava/lang/String;)Lclient!of;")
	public SignedResource getDeclaredMethod(@OriginalArg(0) Class[] parameterTypes, @OriginalArg(2) Class owner, @OriginalArg(3) String name) {
		return this.enqueue(0, SignedResourceType.METHOD, 0, new Object[] { owner, name, parameterTypes });
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(II)Lclient!of;")
	public SignedResource lookupHostname(@OriginalArg(1) int ipAddress) {
		return this.enqueue(0, SignedResourceType.HOSTNAME, ipAddress, null);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(BLjava/lang/String;Z)Lclient!of;")
	public SignedResource openCacheFile(@OriginalArg(1) String string) {
		return this.enqueue(0, SignedResourceType.OPEN_CACHE_FILE, 0, string);
	}

	@OriginalMember(owner = "client!wea", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (true) {
			@Pc(15) SignedResource signedResource;
			synchronized (this) {
				while (true) {
					if (this.stopped) {
						return;
					}
					if (this.current != null) {
						signedResource = this.current;
						this.current = this.current.next;
						if (this.current == null) {
							this.last = null;
						}
						break;
					}
					try {
						this.wait();
					} catch (@Pc(31) InterruptedException ex) {
					}
				}
			}
			try {
				@Pc(41) SignedResourceType kind = signedResource.kind;
				if (kind == SignedResourceType.SOCKET) {
					if (Utils.currentTimeMillis() < SignLink.timeout) {
						throw new IOException();
					}
					signedResource.result = new Socket(InetAddress.getByName((String) signedResource.objectData), signedResource.intData1);
				} else if (kind == SignedResourceType.SOCKET_PROXY) {
					if (Utils.currentTimeMillis() < SignLink.timeout) {
						throw new IOException();
					}
					try {
						signedResource.result = SocketManager.create(signedResource.intData1, (String) signedResource.objectData).open();
					} catch (@Pc(834) ProxyAuthenticationException ex2) {
						signedResource.result = ex2.getMessage();
						throw ex2;
					}
				} else if (kind == SignedResourceType.START_THREAD) {
					@Pc(59) Thread thread = new Thread((Runnable) signedResource.objectData);
					thread.setDaemon(true);
					thread.start();
					thread.setPriority(signedResource.intData1);
					signedResource.result = thread;
				} else if (kind == SignedResourceType.OPEN_STREAM) {
					if (SignLink.timeout > Utils.currentTimeMillis()) {
						throw new IOException();
					}
					signedResource.result = new DataInputStream(((URL) signedResource.objectData).openStream());
				} else {
					@Pc(403) Object[] objects;
					if (kind == SignedResourceType.METHOD) {
						objects = (Object[]) signedResource.objectData;
						if (this.signed && ((Class) objects[0]).getClassLoader() == null) {
							throw new SecurityException();
						}
						signedResource.result = ((Class) objects[0]).getDeclaredMethod((String) objects[1], (Class[]) objects[2]);
					} else if (kind == SignedResourceType.FIELD) {
						objects = (Object[]) signedResource.objectData;
						if (this.signed && ((Class) objects[0]).getClassLoader() == null) {
							throw new SecurityException();
						}
						signedResource.result = ((Class) objects[0]).getDeclaredField((String) objects[1]);
					} else if (kind == SignedResourceType.GET_CLIPBOARD_CONTENTS) {
						@Pc(740) Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						signedResource.result = clipboard.getContents(null);
					} else if (kind == SignedResourceType.SET_CLIPBOARD_CONTENTS) {
						@Pc(118) Transferable transferable = (Transferable) signedResource.objectData;
						@Pc(121) Clipboard clipboard2 = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard2.setContents(transferable, null);
					} else if (this.signed) {
						@Pc(180) String string;
						if (kind == SignedResourceType.HOSTNAME) {
							if (SignLink.timeout > Utils.currentTimeMillis()) {
								throw new IOException();
							}
							string = (signedResource.intData1 >> 24 & 0xFF) + "." + (signedResource.intData1 >> 16 & 0xFF) + "." + (signedResource.intData1 >> 8 & 0xFF) + "." + (signedResource.intData1 & 0xFF);
							signedResource.result = InetAddress.getByName(string).getHostName();
						} else if (kind == SignedResourceType.ADDRESS) {
							if (Utils.currentTimeMillis() < SignLink.timeout) {
								throw new IOException();
							}
							signedResource.result = InetAddress.getByName((String) signedResource.objectData).getAddress();
						} else if (kind == SignedResourceType.FULLSCREEN_MODES) {
							if (this.microsoftJava) {
								signedResource.result = this.microsoftFullscreenAdapter.listModes();
							} else {
								signedResource.result = Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("listmodes").invoke(this.windowMode);
							}
						} else if (kind == SignedResourceType.FULLSCREEN_ENTER) {
							// Not part of the original client: the full screen window is titled Void, not Jagex.
							@Pc(593) Frame frame = new Frame("Void Full Screen");
							// Not part of the original client: the full screen window carries Void's icon too (WindowIcons).
							WindowIcons.apply(frame);
							signedResource.result = frame;
							frame.setResizable(false);
							if (this.microsoftJava) {
								this.microsoftFullscreenAdapter.enter(signedResource.intData1 & 0xFFFF, signedResource.intData2 >> 16, signedResource.intData1 >>> 16, signedResource.intData2 & 0xFFFF, frame);
							} else {
								Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("enter", Frame.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(this.windowMode, frame, Integer.valueOf(signedResource.intData1 >>> 16), new Integer(signedResource.intData1 & 0xFFFF), Integer.valueOf(signedResource.intData2 >> 16), new Integer(signedResource.intData2 & 0xFFFF));
							}
						} else if (kind != SignedResourceType.FULLSCREEN_EXIT) {
							@Pc(254) JFS32 fileOnDisk;
							if (kind == SignedResourceType.OPEN_CACHE_FILE) {
								fileOnDisk = SignLink.openPreferencesFile((String) signedResource.objectData, SignLinkEnvironment.gameName, SignLink.cacheId);
								signedResource.result = fileOnDisk;
							} else if (kind == SignedResourceType.PREFERENCES) {
								fileOnDisk = SignLink.openPreferencesFile((String) signedResource.objectData, "", SignLink.cacheId);
								signedResource.result = fileOnDisk;
							} else if (this.signed && kind == SignedResourceType.MOVE_MOUSE) {
								@Pc(272) int intData1 = signedResource.intData1;
								@Pc(275) int type = signedResource.intData2;
								if (this.microsoftJava) {
									this.mouseCallback.movemouse(intData1, type);
								} else {
									Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("movemouse", Integer.TYPE, Integer.TYPE).invoke(this.cursorManager, Integer.valueOf(intData1), new Integer(type));
								}
							} else if (this.signed && kind == SignedResourceType.UPDATE_MOUSE_COMPONENT) {
								@Pc(338) boolean visible = signedResource.intData1 != 0;
								@Pc(342) java.awt.Component component = (java.awt.Component) signedResource.objectData;
								if (this.microsoftJava) {
									this.mouseCallback.setCursorVisible(visible, component);
								} else {
									Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("showcursor", java.awt.Component.class, Boolean.TYPE).invoke(this.cursorManager, component, Boolean.valueOf(visible));
								}
							} else if (!this.microsoftJava && kind == SignedResourceType.UPDATE_MOUSE_CURSOR) {
								objects = (Object[]) signedResource.objectData;
								Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("setcustomcursor", java.awt.Component.class, int[].class, Integer.TYPE, Integer.TYPE, Point.class).invoke(this.cursorManager, (java.awt.Component) objects[0], (int[]) objects[1], Integer.valueOf(signedResource.intData1), new Integer(signedResource.intData2), (Point) objects[2]);
							} else if (kind == SignedResourceType.OPEN_PAGE) {
								try {
									if (!SystemInfo.operatingSystemLowercase.startsWith("win")) {
										throw new Exception();
									}
									string = (String) signedResource.objectData;
									if (!string.startsWith("http://") && !string.startsWith("https://")) {
										throw new Exception();
									}
									@Pc(525) String string2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?&=,.%+-_#:/*";
									for (@Pc(527) int i = 0; string.length() > i; i++) {
										if (string2.indexOf(string.charAt(i)) == -1) {
											throw new Exception();
										}
									}
									Runtime.getRuntime().exec("cmd /c start \"j\" \"" + string + "\"");
									signedResource.result = null;
								} catch (@Pc(566) Exception ex3) {
									signedResource.result = ex3;
									throw ex3;
								}
							} else {
								throw new Exception("");
							}
						} else if (this.microsoftJava) {
							this.microsoftFullscreenAdapter.exit((Frame) signedResource.objectData);
						} else {
							Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("exit").invoke(this.windowMode);
						}
					} else {
						throw new Exception("");
					}
				}
				signedResource.status = SignedResourceStatus.SUCCESS;
			} catch (@Pc(869) ThreadDeath threadDeath) {
				throw threadDeath;
			} catch (@Pc(872) Throwable ex4) {
				signedResource.status = SignedResourceStatus.ERROR;
			}
			synchronized (signedResource) {
				signedResource.notify();
			}
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(B)V")
	public void shutdown() {
		synchronized (this) {
			this.stopped = true;
			this.notifyAll();
		}
		try {
			this.thread.join();
		} catch (@Pc(25) InterruptedException ex) {
		}
		if (this.cacheDat != null) {
			try {
				this.cacheDat.close();
			} catch (@Pc(34) IOException ex2) {
			}
		}
		if (this.cacheIndex255 != null) {
			try {
				this.cacheIndex255.close();
			} catch (@Pc(44) IOException ex3) {
			}
		}
		if (this.cacheIndex != null) {
			for (@Pc(50) int i = 0; i < this.cacheIndex.length; i++) {
				if (this.cacheIndex[i] != null) {
					try {
						this.cacheIndex[i].close();
					} catch (@Pc(64) IOException ex4) {
					}
				}
			}
		}
		if (this.randomDat != null) {
			try {
				this.randomDat.close();
			} catch (@Pc(80) IOException ex5) {
			}
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(ZILjava/lang/String;I)Lclient!of;")
	public SignedResource openSocket(@OriginalArg(0) boolean proxy, @OriginalArg(1) int intData1, @OriginalArg(2) String string) {
		return this.enqueue(0, proxy ? SignedResourceType.SOCKET_PROXY : SignedResourceType.SOCKET, intData1, string);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/Runnable;II)Lclient!of;")
	public SignedResource startThread(@OriginalArg(0) Runnable runnable, @OriginalArg(2) int priority) {
		return this.enqueue(0, SignedResourceType.START_THREAD, priority, runnable);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/net/URL;I)Lclient!of;")
	public SignedResource openStream(@OriginalArg(0) URL url) {
		return this.enqueue(0, SignedResourceType.OPEN_STREAM, 0, url);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(I[IILjava/awt/Point;Ljava/awt/Component;I)Lclient!of;")
	public SignedResource setCustomCursor(@OriginalArg(0) int height, @OriginalArg(1) int[] pixels, @OriginalArg(3) Point hotspot, @OriginalArg(4) java.awt.Component component, @OriginalArg(5) int width) {
		return this.enqueue(height, SignedResourceType.UPDATE_MOUSE_CURSOR, width, new Object[] { component, pixels, hotspot });
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(I)V")
	public void breakConnections() {
		SignLink.timeout = Utils.currentTimeMillis() + 5000L;
	}

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "(I)Lclient!of;")
	public SignedResource listFullscreenModes() {
		return this.enqueue(0, SignedResourceType.FULLSCREEN_MODES, 0, null);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "([BBLjava/io/File;)Z")
	public boolean writeFile(@OriginalArg(0) byte[] data, @OriginalArg(2) File file) {
		try {
			@Pc(4) FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(data, 0, data.length);
			fileOutputStream.close();
			return true;
		} catch (@Pc(20) IOException ex) {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(ILjava/lang/String;Ljava/lang/Class;)Lclient!of;")
	public SignedResource getDeclaredField(@OriginalArg(1) String name, @OriginalArg(2) Class owner) {
		return this.enqueue(0, SignedResourceType.FIELD, 0, new Object[] { owner, name });
	}

	@OriginalMember(owner = "client!wea", name = "c", descriptor = "(I)Ljava/lang/Object;")
	public Object getMicrosoftPcmDevice() {
		return this.microsoftPcmDevice;
	}

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "(Ljava/lang/String;I)Lclient!of;")
	public SignedResource openPage(@OriginalArg(0) String url) {
		return this.enqueue(0, SignedResourceType.OPEN_PAGE, 0, url);
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(IIIILjava/lang/Object;)Lclient!of;")
	private SignedResource enqueue(@OriginalArg(1) int intData2, @OriginalArg(2) SignedResourceType kind, @OriginalArg(3) int intData1, @OriginalArg(4) Object object) {
		@Pc(3) SignedResource signedResource = new SignedResource();
		signedResource.objectData = object;
		signedResource.intData2 = intData2;
		signedResource.kind = kind;
		signedResource.intData1 = intData1;
		synchronized (this) {
			if (this.last == null) {
				this.last = this.current = signedResource;
			} else {
				this.last.next = signedResource;
				this.last = signedResource;
			}
			this.notify();
			return signedResource;
		}
	}

	@OriginalMember(owner = "client!wea", name = "d", descriptor = "(I)Z")
	public boolean isFullscreenSupported() {
		if (!this.signed) {
			return false;
		} else if (this.microsoftJava) {
			return this.microsoftFullscreenAdapter != null;
		} else {
			return this.windowMode != null;
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(BLjava/awt/Frame;)Lclient!of;")
	public SignedResource exitFullscreen(@OriginalArg(1) Frame frame) {
		return this.enqueue(0, SignedResourceType.FULLSCREEN_EXIT, 0, frame);
	}

	@OriginalMember(owner = "client!hi", name = "a", descriptor = "(BLclient!wea;)[Lclient!b;")
	public static DisplayModeDetails[] getDisplayModes(@OriginalArg(1) SignLink signLink) {
		if (!signLink.isFullscreenSupported()) {
			return new DisplayModeDetails[0];
		}
		@Pc(24) SignedResource signedResource = signLink.listFullscreenModes();
		while (signedResource.status == SignedResourceStatus.IDLE) {
			TimeUtils.sleep(10L);
		}
		if (signedResource.status == SignedResourceStatus.ERROR) {
			return new DisplayModeDetails[0];
		}
		@Pc(45) int[] modeData = (int[]) signedResource.result;
		@Pc(51) DisplayModeDetails[] modes = new DisplayModeDetails[modeData.length >> 2];
		for (@Pc(53) int i = 0; i < modes.length; i++) {
			@Pc(59) DisplayModeDetails mode = new DisplayModeDetails();
			modes[i] = mode;
			mode.width = modeData[i << 2];
			mode.height = modeData[(i << 2) + 1];
			mode.bitDepth = modeData[(i << 2) + 2];
			mode.refreshRate = modeData[(i << 2) + 3];
		}
		return modes;
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;I)Lclient!so;")
	public static JFS32 openPreferencesFile(@OriginalArg(0) String suffix, @OriginalArg(2) String gameName, @OriginalArg(3) int cacheId) {
		@Pc(18) String string3;
		if (cacheId == FileStore.RC) {
			string3 = "jagex_" + gameName + "_preferences" + suffix + "_rc.dat";
		} else if (cacheId == FileStore.WIP) {
			string3 = "jagex_" + gameName + "_preferences" + suffix + "_wip.dat";
		} else {
			string3 = "jagex_" + gameName + "_preferences" + suffix + ".dat";
		}
		@Pc(91) String[] strings = new String[] { "c:/rscache/", "/rscache/", SignLinkEnvironment.preferencesParentFile, "c:/windows/", "c:/winnt/", "c:/", "/tmp/", "" };
		for (@Pc(93) int i = 0; i < strings.length; i++) {
			@Pc(98) String string4 = strings[i];
			if (string4.length() <= 0 || (new File(string4)).exists()) {
				try {
					return new JFS32(new File(string4, string3), "rw", 10000L);
				} catch (@Pc(124) Exception ex) {
				}
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!wea", name = "f", descriptor = "J")
	public static volatile long timeout = 0L;

	@OriginalMember(owner = "client!wea", name = "q", descriptor = "I")
	public static int cacheId;
	@OriginalMember(owner = "client!qt", name = "o", descriptor = "Lclient!wea;")
	public static SignLink signLink;
	@OriginalMember(owner = "client!sk", name = "d", descriptor = "Lclient!wea;")
	public static SignLink PACKET_3;
	@OriginalMember(owner = "client!dba", name = "a", descriptor = "(ILjava/io/File;)[B")
	public static byte[] readFile(@OriginalArg(1) File file) {
		return FileOnDisk.readFileBytes((int) file.length(), file);
	}
}
