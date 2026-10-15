// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("UNCHECKED_CAST")

package com.jagex.game.runetek5.sign

import com.jagex.game.runetek5.client.features.WindowIcons
import com.jagex.game.runetek5.DisplayModeDetails
import com.jagex.game.runetek5.core.constants.FileStore
import com.jagex.game.runetek5.core.constants.SignedResourceType
import com.jagex.game.runetek5.cache.JFS32
import com.jagex.game.runetek5.core.io.socket.ProxyAuthenticationException
import com.jagex.game.runetek5.graphics.ms.MicrosoftFullscreenAdapter
import com.jagex.game.runetek5.graphics.ms.MicrosoftJavaMouseCallback
import awt.EventQueue
import awt.Frame
import awt.Point
import awt.Toolkit
import awt.datatransfer.Clipboard
import awt.datatransfer.Transferable
import io.DataInputStream
import io.File
import io.FileOutputStream
import io.IOException
import net.InetAddress
import net.Socket
import net.URL
import util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.network.SocketManager
import com.jagex.game.runetek5.cache.FileOnDisk
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.CacheFileLocator
import kotlin.jvm.JvmField
import awt.Component
import lang.Class
import lang.InterruptedException
import lang.Runnable
import lang.SecurityException
import lang.Thread
import lang.ThreadGroup
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmStatic
import lockNotify
import lockNotifyAll
import lockWait
import withLock
import lang.getClassLoader
import lang.getDeclaredMethod
import lang.getMethod
import lang.jClass
import util.lowercase
import lang.getDeclaredConstructor
import lang.ThreadDeath
import runLoop
import executeWorkerTasksInline

@OriginalClass("client!wea")
class SignLink : Runnable {

	@OriginalMember(owner = "client!wea", name = "d", descriptor = "Lclient!so;")
	@JvmField
	var cacheIndex255: JFS32? = null

	@OriginalMember(owner = "client!wea", name = "j", descriptor = "Lclient!so;")
	@JvmField
	var cacheDat: JFS32? = null

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "Z")
	@JvmField
	var signed: Boolean = false

	@OriginalMember(owner = "client!wea", name = "w", descriptor = "Lclient!of;")
	private var last: SignedResource? = null

	@OriginalMember(owner = "client!wea", name = "z", descriptor = "Z")
	private var stopped: Boolean = false

	@OriginalMember(owner = "client!wea", name = "m", descriptor = "Lclient!so;")
	@JvmField
	var randomDat: JFS32? = null

	@OriginalMember(owner = "client!wea", name = "s", descriptor = "Z")
	@JvmField
	var microsoftJava: Boolean = false

	@OriginalMember(owner = "client!wea", name = "v", descriptor = "Lclient!of;")
	private var current: SignedResource? = null

	@OriginalMember(owner = "client!wea", name = "x", descriptor = "Ljava/awt/EventQueue;")
	@JvmField
	var eventQueue: EventQueue? = null

	@OriginalMember(owner = "client!wea", name = "t", descriptor = "[Lclient!so;")
	@JvmField
	var cacheIndex: Array<JFS32?>? = null

	@OriginalMember(owner = "client!wea", name = "l", descriptor = "Ljava/lang/Object;")
	private var microsoftPcmDevice: Any? = null

	@OriginalMember(owner = "client!wea", name = "r", descriptor = "Ljava/lang/Object;")
	private var windowMode: Any? = null

	@OriginalMember(owner = "client!wea", name = "A", descriptor = "Lclient!nt;")
	private var microsoftFullscreenAdapter: MicrosoftFullscreenAdapter? = null

	@OriginalMember(owner = "client!wea", name = "p", descriptor = "Ljava/lang/Object;")
	private var cursorManager: Any? = null

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "Lclient!lq;")
	private var mouseCallback: MicrosoftJavaMouseCallback? = null

	@OriginalMember(owner = "client!wea", name = "o", descriptor = "Ljava/lang/Thread;")
	private val thread: Thread

	@OriginalMember(owner = "client!wea", name = "<init>", descriptor = "(ILjava/lang/String;IZ)V")
	@Throws(Exception::class)
	constructor(@OriginalArg(0) cacheId: Int, @OriginalArg(1) string: String?, @OriginalArg(2) cacheIndexCount: Int, @OriginalArg(3) signed: Boolean) {
		SignLinkEnvironment.gameName = string
		this.signed = signed
		SystemInfo.javaVersion = "1.1"
		PlayerUpdate.javaVendor = "Unknown"
		SignLink.cacheId = cacheId
		try {
			PlayerUpdate.javaVendor = lang.getProperty("java.vendor")
			SystemInfo.javaVersion = lang.getProperty("java.version")
		} catch (@Pc(44) ex: Exception) {
		}
		if (PlayerUpdate.javaVendor!!.lowercase(util.defaultLocale()).indexOf("microsoft") != -1) {
			this.microsoftJava = true
		}
		try {
			SignLinkEnvironment.operatingSystem = lang.getProperty("os.name")
		} catch (@Pc(59) ex2: Exception) {
			SignLinkEnvironment.operatingSystem = "Unknown"
		}
		SystemInfo.operatingSystemLowercase = SignLinkEnvironment.operatingSystem!!.lowercase(util.defaultLocale())
		try {
			SystemInfo.OS_ARCH = lang.getProperty("os.arch")!!.lowercase(util.defaultLocale())
		} catch (@Pc(70) ex3: Exception) {
			SystemInfo.OS_ARCH = ""
		}
		try {
			SystemInfo.osVersion = lang.getProperty("os.version")!!.lowercase(util.defaultLocale())
		} catch (@Pc(78) ex4: Exception) {
			SystemInfo.osVersion = ""
		}
		try {
			SignLinkEnvironment.preferencesParentFile = lang.getProperty("user.home")
			if (SignLinkEnvironment.preferencesParentFile != null) {
				SignLinkEnvironment.preferencesParentFile = SignLinkEnvironment.preferencesParentFile + "/"
			}
		} catch (@Pc(96) ex5: Exception) {
		}
		if (SignLinkEnvironment.preferencesParentFile == null) {
			SignLinkEnvironment.preferencesParentFile = "~/"
		}
		try {
			this.eventQueue = awt.getDefaultToolkit().getSystemEventQueue()
		} catch (@Pc(106) ex6: Throwable) {
		}
		if (!this.microsoftJava) {
			try {
				SignLinkEnvironment.setFocusTraversalKeysEnabledMethod = focusTraversalKeysEnabledMethod()
			} catch (@Pc(122) ex7: Exception) {
			}
			try {
				SignLinkEnvironment.setFocusCycleRootMethod = focusCycleRootMethod()
			} catch (@Pc(135) ex8: Exception) {
			}
		}
		CacheFileLocator.initialize(SignLinkEnvironment.gameName, SignLink.cacheId)
		if (this.signed) {
			this.randomDat = JFS32(CacheFileLocator.getCacheFile(SignLink.cacheId, "random.dat", null), "rw", 25L)
			this.cacheDat = JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.dat2"), "rw", 209715200L)
			this.cacheIndex255 = JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.idx255"), "rw", 1048576L)
			this.cacheIndex = arrayOfNulls<JFS32>(cacheIndexCount)
			@Pc(181) var i: Int = 0
			while (i < cacheIndexCount) {
				this.cacheIndex!![i] = JFS32(CacheFileLocator.loadFromCacheDir("main_file_cache.idx" + i), "rw", 1048576L)
				i++
			}
			if (this.microsoftJava) {
				try {
					this.microsoftPcmDevice = newInstanceOf("com.jagex.game.runetek5.DirectSoundPcmDevice")
				} catch (@Pc(218) ex9: Throwable) {
				}
			}
			try {
				if (this.microsoftJava) {
					this.microsoftFullscreenAdapter = MicrosoftFullscreenAdapter()
				} else {
					this.windowMode = newInstanceOf("com.jagex.game.runetek5.client.window.WindowMode")
				}
			} catch (@Pc(235) ex10: Throwable) {
			}
			try {
				if (this.microsoftJava) {
					this.mouseCallback = MicrosoftJavaMouseCallback()
				} else {
					this.cursorManager = newInstanceOf("com.jagex.game.runetek5.client.input.mouse.CursorManager")
				}
			} catch (@Pc(251) ex11: Throwable) {
			}
		}
		if (this.signed && !this.microsoftJava) {
			@Pc(261) var threadGroup: ThreadGroup = lang.currentThread().getThreadGroup()
			@Pc(264) var threadGroup2: ThreadGroup? = threadGroup.getParent()
			while (threadGroup2 != null) {
				threadGroup = threadGroup2
				threadGroup2 = threadGroup2.getParent()
			}
			@Pc(275) val threads: Array<Thread?> = arrayOfNulls<Thread>(1000)
			threadGroup.enumerate(threads)
			@Pc(281) var j: Int = 0
			while (j < threads.size) {
				if (threads[j] != null && threads[j]!!.getName().startsWith("AWT")) {
					threads[j]!!.setPriority(1)
				}
				j++
			}
		}
		this.stopped = false
		this.thread = Thread(this)
		this.thread.setPriority(10)
		this.thread.setDaemon(true)
		// Not part of the original client: where requests run inline there is no worker to start (PlatformLoop).
		if (!executeWorkerTasksInline) {
			this.thread.start()
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(IIIIB)Lclient!of;")
	fun enterFullscreen(@OriginalArg(1) height: Int, @OriginalArg(2) bitDepth: Int, @OriginalArg(3) width: Int): SignedResource {
		return this.enqueue(bitDepth shl 16, SignedResourceType.FULLSCREEN_ENTER, height + (width shl 16), null)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "([Ljava/lang/Class;ILjava/lang/Class;Ljava/lang/String;)Lclient!of;")
	fun getDeclaredMethod(@OriginalArg(0) parameterTypes: Array<Class<*>?>?, @OriginalArg(2) owner: Class<*>?, @OriginalArg(3) name: String?): SignedResource {
		return this.enqueue(0, SignedResourceType.METHOD, 0, arrayOf<Any?>(owner, name, parameterTypes))
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(II)Lclient!of;")
	fun lookupHostname(@OriginalArg(1) ipAddress: Int): SignedResource {
		return this.enqueue(0, SignedResourceType.HOSTNAME, ipAddress, null)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(BLjava/lang/String;Z)Lclient!of;")
	fun openCacheFile(@OriginalArg(1) string: String?): SignedResource {
		return this.enqueue(0, SignedResourceType.OPEN_CACHE_FILE, 0, string)
	}

	@OriginalMember(owner = "client!wea", name = "run", descriptor = "()V")
	override fun run() {
		// Not part of the original client: the loop goes through runLoop (PlatformLoop), the original
		// blocking loop on the JVM and an event-loop chain where a platform has no threads; runStep is
		// one turn of the original while.
		runLoop(
			prelude = { true },
			step = { this.runStep() },
			onError = { throw it },
			onFinally = { },
		)
	}

	/* Not part of the original client: one turn of run's loop (one request), false when the worker stops. */
	private fun runStep(): Boolean {
		@Pc(15) var signedResource: SignedResource
		withLock(this) {
			while (true) {
				if (this.stopped) {
					return false
				}
				if (this.current != null) {
					signedResource = this.current!!
					this.current = this.current!!.next
					if (this.current == null) {
						this.last = null
					}
					break
				}
				try {
					lockWait(this)
				} catch (@Pc(31) ex: InterruptedException) {
				}
			}
		}
		this.processRequest(signedResource)
		return true
	}

	/*
	 * Not part of the original client: the handling of one request, the original body of the
	 * worker loop after it takes the request from the queue. enqueue calls it directly where there
	 * is no worker thread (executeWorkerTasksInline).
	 */
	private fun processRequest(signedResource: SignedResource) {
		try {
			@Pc(41) val kind: SignedResourceType? = signedResource.kind
			if (kind === SignedResourceType.SOCKET) {
				if (Utils.currentTimeMillis() < SignLink.timeout) {
					throw IOException()
				}
				signedResource.result = Socket(net.getByName(signedResource.objectData as String?), signedResource.intData1)
			} else if (kind === SignedResourceType.SOCKET_PROXY) {
				if (Utils.currentTimeMillis() < SignLink.timeout) {
					throw IOException()
				}
				try {
					signedResource.result = SocketManager.create(signedResource.intData1, signedResource.objectData as String?).open()
				} catch (@Pc(834) ex2: ProxyAuthenticationException) {
					signedResource.result = ex2.message
					throw ex2
				}
			} else if (kind === SignedResourceType.START_THREAD) {
				@Pc(59) val thread: Thread = Thread(signedResource.objectData as Runnable?)
				thread.setDaemon(true)
				thread.start()
				thread.setPriority(signedResource.intData1)
				signedResource.result = thread
			} else if (kind === SignedResourceType.OPEN_STREAM) {
				if (SignLink.timeout > Utils.currentTimeMillis()) {
					throw IOException()
				}
				signedResource.result = DataInputStream((signedResource.objectData as URL?)!!.openStream())
			} else {
				@Pc(403) var objects: Array<Any?>?
				if (kind === SignedResourceType.METHOD) {
					objects = signedResource.objectData as Array<Any?>?
					if (this.signed && (objects!![0] as Class<*>?)!!.getClassLoader() == null) {
						throw SecurityException()
					}
					signedResource.result = (objects!![0] as Class<*>?)!!.getDeclaredMethod(objects[1] as String?, *((objects[2] as Array<Class<*>?>?) ?: arrayOfNulls<Class<*>>(0)))
				} else if (kind === SignedResourceType.FIELD) {
					objects = signedResource.objectData as Array<Any?>?
					if (this.signed && (objects!![0] as Class<*>?)!!.getClassLoader() == null) {
						throw SecurityException()
					}
					signedResource.result = (objects!![0] as Class<*>?)!!.getDeclaredField(objects[1] as String?)
				} else if (kind === SignedResourceType.GET_CLIPBOARD_CONTENTS) {
					@Pc(740) val clipboard: Clipboard = awt.getDefaultToolkit().getSystemClipboard()
					signedResource.result = clipboard.getContents(null)
				} else if (kind === SignedResourceType.SET_CLIPBOARD_CONTENTS) {
					@Pc(118) val transferable: Transferable? = signedResource.objectData as Transferable?
					@Pc(121) val clipboard2: Clipboard = awt.getDefaultToolkit().getSystemClipboard()
					clipboard2.setContents(transferable, null)
				} else if (this.signed) {
					@Pc(180) var string: String?
					if (kind === SignedResourceType.HOSTNAME) {
						if (SignLink.timeout > Utils.currentTimeMillis()) {
							throw IOException()
						}
						string = (signedResource.intData1 shr 24 and 0xFF).toString() + "." + (signedResource.intData1 shr 16 and 0xFF) + "." + (signedResource.intData1 shr 8 and 0xFF) + "." + (signedResource.intData1 and 0xFF)
						signedResource.result = net.getByName(string).getHostName()
					} else if (kind === SignedResourceType.ADDRESS) {
						if (Utils.currentTimeMillis() < SignLink.timeout) {
							throw IOException()
						}
						signedResource.result = net.getByName(signedResource.objectData as String?).getAddress()
					} else if (kind === SignedResourceType.FULLSCREEN_MODES) {
						if (this.microsoftJava) {
							signedResource.result = this.microsoftFullscreenAdapter!!.listModes()
						} else {
							signedResource.result = windowModeListModes(this.windowMode)
						}
					} else if (kind === SignedResourceType.FULLSCREEN_ENTER) {
						// Not part of the original client: the full screen window is titled Void, not Jagex.
						@Pc(593) val frame: Frame = Frame("Void Full Screen")
						// Not part of the original client: the full screen window carries Void's icon too (WindowIcons).
						WindowIcons.apply(frame)
						signedResource.result = frame
						frame.setResizable(false)
						if (this.microsoftJava) {
							this.microsoftFullscreenAdapter!!.enter(signedResource.intData1 and 0xFFFF, signedResource.intData2 shr 16, signedResource.intData1 ushr 16, signedResource.intData2 and 0xFFFF, frame)
						} else {
							windowModeEnter(this.windowMode, frame, signedResource.intData1 ushr 16, signedResource.intData1 and 0xFFFF, signedResource.intData2 shr 16, signedResource.intData2 and 0xFFFF)
						}
					} else if (kind !== SignedResourceType.FULLSCREEN_EXIT) {
						@Pc(254) var fileOnDisk: JFS32?
						if (kind === SignedResourceType.OPEN_CACHE_FILE) {
							fileOnDisk = SignLink.openPreferencesFile(signedResource.objectData as String?, SignLinkEnvironment.gameName, SignLink.cacheId)
							signedResource.result = fileOnDisk
						} else if (kind === SignedResourceType.PREFERENCES) {
							fileOnDisk = SignLink.openPreferencesFile(signedResource.objectData as String?, "", SignLink.cacheId)
							signedResource.result = fileOnDisk
						} else if (this.signed && kind === SignedResourceType.MOVE_MOUSE) {
							@Pc(272) val intData1: Int = signedResource.intData1
							@Pc(275) val type: Int = signedResource.intData2
							if (this.microsoftJava) {
								this.mouseCallback!!.movemouse(intData1, type)
							} else {
								cursorManagerMoveMouse(this.cursorManager, intData1, type)
							}
						} else if (this.signed && kind === SignedResourceType.UPDATE_MOUSE_COMPONENT) {
							@Pc(338) val visible: Boolean = signedResource.intData1 != 0
							@Pc(342) val component: Component? = signedResource.objectData as Component?
							if (this.microsoftJava) {
								this.mouseCallback!!.setCursorVisible(visible, component)
							} else {
								cursorManagerShowCursor(this.cursorManager, component, visible)
							}
						} else if (!this.microsoftJava && kind === SignedResourceType.UPDATE_MOUSE_CURSOR) {
							objects = signedResource.objectData as Array<Any?>?
							cursorManagerSetCustomCursor(this.cursorManager, objects!![0] as Component?, objects[1] as IntArray?, signedResource.intData1, signedResource.intData2, objects[2] as Point?)
						} else if (kind === SignedResourceType.OPEN_PAGE) {
							try {
								if (!SystemInfo.operatingSystemLowercase!!.startsWith("win")) {
									throw Exception()
								}
								string = signedResource.objectData as String?
								if (!string!!.startsWith("http://") && !string.startsWith("https://")) {
									throw Exception()
								}
								// Not part of the original client: no '%', which cmd expands (%USERNAME%) even
								// inside the quotes openUrlWithCmd puts the address in.
								@Pc(525) val string2: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?&=,.+-_#:/*"
								@Pc(527) var i: Int = 0
								while (string.length > i) {
									if (string2.indexOf(string[i]) == -1) {
										throw Exception()
									}
									i++
								}
								openUrlWithCmd(string)
								signedResource.result = null
							} catch (@Pc(566) ex3: Exception) {
								signedResource.result = ex3
								throw ex3
							}
						} else {
							throw Exception("")
						}
					} else if (this.microsoftJava) {
						this.microsoftFullscreenAdapter!!.exit(signedResource.objectData as Frame?)
					} else {
						windowModeExit(this.windowMode)
					}
				} else {
					throw Exception("")
				}
			}
			signedResource.status = SignedResourceStatus.SUCCESS
		} catch (@Pc(869) threadDeath: ThreadDeath) {
			throw threadDeath
		} catch (@Pc(872) ex4: Throwable) {
			signedResource.status = SignedResourceStatus.ERROR
		}
		withLock(signedResource) {
			lockNotify(signedResource)
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(B)V")
	fun shutdown() {
		withLock(this) {
			this.stopped = true
			lockNotifyAll(this)
		}
		try {
			this.thread.join()
		} catch (@Pc(25) ex: InterruptedException) {
		}
		if (this.cacheDat != null) {
			try {
				this.cacheDat!!.close()
			} catch (@Pc(34) ex2: IOException) {
			}
		}
		if (this.cacheIndex255 != null) {
			try {
				this.cacheIndex255!!.close()
			} catch (@Pc(44) ex3: IOException) {
			}
		}
		if (this.cacheIndex != null) {
			@Pc(50) var i: Int = 0
			while (i < this.cacheIndex!!.size) {
				if (this.cacheIndex!![i] != null) {
					try {
						this.cacheIndex!![i]!!.close()
					} catch (@Pc(64) ex4: IOException) {
					}
				}
				i++
			}
		}
		if (this.randomDat != null) {
			try {
				this.randomDat!!.close()
			} catch (@Pc(80) ex5: IOException) {
			}
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(ZILjava/lang/String;I)Lclient!of;")
	fun openSocket(@OriginalArg(0) proxy: Boolean, @OriginalArg(1) intData1: Int, @OriginalArg(2) string: String?): SignedResource {
		return this.enqueue(0, if (proxy) SignedResourceType.SOCKET_PROXY else SignedResourceType.SOCKET, intData1, string)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/Runnable;II)Lclient!of;")
	fun startThread(@OriginalArg(0) runnable: Runnable?, @OriginalArg(2) priority: Int): SignedResource {
		return this.enqueue(0, SignedResourceType.START_THREAD, priority, runnable)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/net/URL;I)Lclient!of;")
	fun openStream(@OriginalArg(0) url: URL?): SignedResource {
		return this.enqueue(0, SignedResourceType.OPEN_STREAM, 0, url)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(I[IILjava/awt/Point;Ljava/awt/Component;I)Lclient!of;")
	fun setCustomCursor(@OriginalArg(0) height: Int, @OriginalArg(1) pixels: IntArray?, @OriginalArg(3) hotspot: Point?, @OriginalArg(4) component: Component?, @OriginalArg(5) width: Int): SignedResource {
		return this.enqueue(height, SignedResourceType.UPDATE_MOUSE_CURSOR, width, arrayOf<Any?>(component, pixels, hotspot))
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(I)V")
	fun breakConnections() {
		SignLink.timeout = Utils.currentTimeMillis() + 5000L
	}

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "(I)Lclient!of;")
	fun listFullscreenModes(): SignedResource {
		return this.enqueue(0, SignedResourceType.FULLSCREEN_MODES, 0, null)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "([BBLjava/io/File;)Z")
	fun writeFile(@OriginalArg(0) data: ByteArray?, @OriginalArg(2) file: File?): Boolean {
		try {
			// Not part of the original client: written beside the file and renamed over it, so a native
			// library another client has loaded is replaced rather than rewritten under it.
			val temporary: File = File(file!!.getPath() + ".tmp")
			@Pc(4) val fileOutputStream: FileOutputStream = FileOutputStream(temporary)
			fileOutputStream.write(data, 0, data!!.size)
			fileOutputStream.close()
			if (!temporary.renameTo(file)) {
				file.delete()
				if (!temporary.renameTo(file)) {
					throw IOException()
				}
			}
			return true
		} catch (@Pc(20) ex: IOException) {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(ILjava/lang/String;Ljava/lang/Class;)Lclient!of;")
	fun getDeclaredField(@OriginalArg(1) name: String?, @OriginalArg(2) owner: Class<*>?): SignedResource {
		return this.enqueue(0, SignedResourceType.FIELD, 0, arrayOf<Any?>(owner, name))
	}

	@OriginalMember(owner = "client!wea", name = "c", descriptor = "(I)Ljava/lang/Object;")
	fun getMicrosoftPcmDevice(): Any? {
		return this.microsoftPcmDevice
	}

	@OriginalMember(owner = "client!wea", name = "b", descriptor = "(Ljava/lang/String;I)Lclient!of;")
	fun openPage(@OriginalArg(0) url: String?): SignedResource {
		return this.enqueue(0, SignedResourceType.OPEN_PAGE, 0, url)
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(IIIILjava/lang/Object;)Lclient!of;")
	private fun enqueue(@OriginalArg(1) intData2: Int, @OriginalArg(2) kind: SignedResourceType?, @OriginalArg(3) intData1: Int, @OriginalArg(4) `object`: Any?): SignedResource {
		@Pc(3) val signedResource: SignedResource = SignedResource()
		signedResource.objectData = `object`
		signedResource.intData2 = intData2
		signedResource.kind = kind
		signedResource.intData1 = intData1
		if (executeWorkerTasksInline) {
			// Not part of the original client: no worker thread, so the request runs here (PlatformLoop).
			this.processRequest(signedResource)
			return signedResource
		}
		withLock(this) {
			if (this.last == null) {
				this.current = signedResource
				this.last = signedResource
			} else {
				this.last!!.next = signedResource
				this.last = signedResource
			}
			lockNotify(this)
			return signedResource
		}
	}

	@OriginalMember(owner = "client!wea", name = "d", descriptor = "(I)Z")
	fun isFullscreenSupported(): Boolean {
		if (!this.signed) {
			return false
		} else if (this.microsoftJava) {
			return this.microsoftFullscreenAdapter != null
		} else {
			return this.windowMode != null
		}
	}

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(BLjava/awt/Frame;)Lclient!of;")
	fun exitFullscreen(@OriginalArg(1) frame: Frame?): SignedResource {
		return this.enqueue(0, SignedResourceType.FULLSCREEN_EXIT, 0, frame)
	}

	companion object {

		@OriginalMember(owner = "client!hi", name = "a", descriptor = "(BLclient!wea;)[Lclient!b;")
		@JvmStatic
		fun getDisplayModes(@OriginalArg(1) signLink: SignLink?): Array<DisplayModeDetails?> {
			if (!signLink!!.isFullscreenSupported()) {
				return arrayOfNulls<DisplayModeDetails>(0)
			}
			@Pc(24) val signedResource: SignedResource = signLink.listFullscreenModes()
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(10L)
			}
			if (signedResource.status == SignedResourceStatus.ERROR) {
				return arrayOfNulls<DisplayModeDetails>(0)
			}
			@Pc(45) val modeData: IntArray? = signedResource.result as IntArray?
			@Pc(51) val modes: Array<DisplayModeDetails?> = arrayOfNulls<DisplayModeDetails>(modeData!!.size shr 2)
			@Pc(53) var i: Int = 0
			while (i < modes.size) {
				@Pc(59) val mode: DisplayModeDetails = DisplayModeDetails()
				modes[i] = mode
				mode.width = modeData[i shl 2]
				mode.height = modeData[(i shl 2) + 1]
				mode.bitDepth = modeData[(i shl 2) + 2]
				mode.refreshRate = modeData[(i shl 2) + 3]
				i++
			}
			return modes
		}

		@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;I)Lclient!so;")
		@JvmStatic
		fun openPreferencesFile(@OriginalArg(0) suffix: String?, @OriginalArg(2) gameName: String?, @OriginalArg(3) cacheId: Int): JFS32? {
			@Pc(18) val string3: String
			if (cacheId == FileStore.RC) {
				string3 = "jagex_" + gameName + "_preferences" + suffix + "_rc.dat"
			} else if (cacheId == FileStore.WIP) {
				string3 = "jagex_" + gameName + "_preferences" + suffix + "_wip.dat"
			} else {
				string3 = "jagex_" + gameName + "_preferences" + suffix + ".dat"
			}
			@Pc(91) val strings: Array<String?> = arrayOf<String?>("c:/rscache/", "/rscache/", SignLinkEnvironment.preferencesParentFile, "c:/windows/", "c:/winnt/", "c:/", "/tmp/", "")
			@Pc(93) var i: Int = 0
			while (i < strings.size) {
				@Pc(98) val string4: String? = strings[i]
				if (string4!!.length <= 0 || File(string4).exists()) {
					try {
						return JFS32(File(string4, string3), "rw", 10000L)
					} catch (@Pc(124) ex: Exception) {
					}
				}
				i++
			}
			return null
		}

		@OriginalMember(owner = "client!wea", name = "f", descriptor = "J")
		@Volatile
		@JvmField
		var timeout: Long = 0L

		@OriginalMember(owner = "client!wea", name = "q", descriptor = "I")
		@JvmField
		var cacheId: Int = 0
		@OriginalMember(owner = "client!qt", name = "o", descriptor = "Lclient!wea;")
		@JvmField
		var signLink: SignLink? = null
		@OriginalMember(owner = "client!sk", name = "d", descriptor = "Lclient!wea;")
		@JvmField
		var PACKET_3: SignLink? = null
		@OriginalMember(owner = "client!dba", name = "a", descriptor = "(ILjava/io/File;)[B")
		@JvmStatic
		fun readFile(@OriginalArg(1) file: File?): ByteArray? {
			return FileOnDisk.readFileBytes(file!!.length().toInt(), file)
		}
	}
}
