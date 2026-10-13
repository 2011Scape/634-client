package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.material.texture.ColorImageCacheEntry
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sign.SignLink
import java.io.File
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import java.lang.reflect.Field
import java.util.Enumeration
import java.util.Hashtable
import java.util.Vector

@OriginalClass("client!bc")
actual class NativeLibraryOwner {

	@OriginalMember(owner = "client!bc", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	actual companion object {
		@OriginalMember(owner = "client!gba", name = "a", descriptor = "(Ljava/lang/Class;BLjava/lang/String;)Z")
		@JvmStatic
		actual fun loadLibrary(@OriginalArg(0) class2: Class<*>?, @OriginalArg(2) string: String?): Boolean {
			// Not part of the original client: jaggl runs on LWJGL (jaggl.OpenGL), so there is no
			// libjaggl to load. jaclib's memory classes are pure JVM code too; libjaclib is still loaded
			// where the cache has one, because libhw3d links against it, but toolkits 0 and 1 do not
			// need it (there is none for arm64).
			if (string == "jaggl") {
				return true
			}
			if (string == "jaclib") {
				NativeLibraryOwner.loadNativeLibrary(class2, string)
				return true
			}
			return NativeLibraryOwner.loadNativeLibrary(class2, string)
		}

		// Not part of the original client: the original loadLibrary, which loadLibrary above wraps.
		private fun loadNativeLibrary(class2: Class<*>?, string: String?): Boolean {
			@Pc(8) val class3: Class<*>? = NativeLibraryOwner.nativeLibraryOwners.get(string) as Class<*>?
			if (class3 != null) {
				return class3.getClassLoader() === class2!!.getClassLoader()
			}
			@Pc(21) var file: File? = null
			if (file == null) {
				file = NativeLibraryOwner.nativeLibraryFiles.get(string) as File?
			}
			if (file != null) {
				try {
					file = File(file!!.getCanonicalPath())
					@Pc(44) val class4: Class<*> = Class.forName("java.lang.Runtime")
					@Pc(47) val class5: Class<*> = Class.forName("java.lang.reflect.AccessibleObject")
					@Pc(57) val method: Method = class5.getDeclaredMethod("setAccessible", java.lang.Boolean.TYPE)
					@Pc(73) val method2: Method = class4.getDeclaredMethod("load0", Class.forName("java.lang.Class"), Class.forName("java.lang.String"))
					method.invoke(method2, java.lang.Boolean.TRUE)
					method2.invoke(Runtime.getRuntime(), class2, file.getPath())
					method.invoke(method2, java.lang.Boolean.FALSE)
					NativeLibraryOwner.nativeLibraryOwners.put(string, class2)
					return true
				} catch (@Pc(116) ex: NoSuchMethodException) {
					System.load(file!!.getPath())
					NativeLibraryOwner.nativeLibraryOwners.put(string, NativeLibraryOwner::class.java)
					return true
				} catch (@Pc(134) ex2: Throwable) {
				}
			}
			return false
		}

		@OriginalMember(owner = "client!qu", name = "a", descriptor = "(ILjava/lang/String;)Z")
		@JvmStatic
		actual fun loadLibrary(@OriginalArg(1) string: String?): Boolean {
			return NativeLibraryOwner.loadLibrary(NativeLibraryOwner::class.java, string)
		}

		@OriginalMember(owner = "client!vn", name = "a", descriptor = "(BLjava/lang/String;)Z")
		@JvmStatic
		actual fun isLibraryLoaded(@OriginalArg(1) string: String?): Boolean {
			return NativeLibraryOwner.nativeLibraryOwners.containsKey(string)
		}

		@OriginalMember(owner = "client!hw", name = "a", descriptor = "(Lclient!in;Lclient!wea;B)V")
		@JvmStatic
		actual fun init(@OriginalArg(0) nativesResource: Js5ResourceProvider?, @OriginalArg(1) signLink: SignLink?) {
			NativeLibraryOwner.nativeLibraryPathPrefix = ""
			NativeLibraryOwner.signLink = signLink
			Resource.aClass157_219 = nativesResource
			if (ColorImageCacheEntry.osName.startsWith("win")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "windows/"
			} else if (ColorImageCacheEntry.osName.startsWith("linux")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "linux/"
			} else if (ColorImageCacheEntry.osName.startsWith("mac")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "macos/"
			}
			if (NativeLibraryOwner.signLink!!.microsoftJava) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "msjava/"
			} else if (ColorImageCacheEntry.osArch.startsWith("amd64") || ColorImageCacheEntry.osArch.startsWith("x86_64")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "x86_64/"
			} else if (ColorImageCacheEntry.osArch.startsWith("i386") || ColorImageCacheEntry.osArch.startsWith("i486") || ColorImageCacheEntry.osArch.startsWith("i586") || ColorImageCacheEntry.osArch.startsWith("x86")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "x86/"
			} else if (ColorImageCacheEntry.osArch.startsWith("ppc")) {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "ppc/"
			} else {
				NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "universal/"
			}
		}
		@OriginalMember(owner = "client!ke", name = "me", descriptor = "Ljava/util/Hashtable;")
		@JvmField
		actual var nativeLibraryOwners: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()

		@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")
		@JvmStatic
		actual fun getNativeLibraryFileName(@OriginalArg(0) string: String?): String? {
			if (ColorImageCacheEntry.osName.startsWith("win")) {
				return string + ".dll"
			} else if (ColorImageCacheEntry.osName.startsWith("linux")) {
				return "lib" + string + ".so"
			} else if (ColorImageCacheEntry.osName.startsWith("mac")) {
				return "lib" + string + ".dylib"
			} else {
				return null
			}
		}

		@OriginalMember(owner = "client!km", name = "a", descriptor = "(Ljava/lang/String;I)Z")
		@JvmStatic
		actual fun hasNativeLibraryFile(@OriginalArg(0) string: String?): Boolean {
			return NativeLibraryOwner.nativeLibraryFiles.containsKey(string)
		}

		@OriginalMember(owner = "client!rga", name = "K", descriptor = "Lclient!wea;")
		@JvmField
		actual var signLink: SignLink? = null

		@OriginalMember(owner = "client!uba", name = "q", descriptor = "Ljava/lang/String;")
		@JvmField
		actual var nativeLibraryPathPrefix: String? = null

		@OriginalMember(owner = "client!pg", name = "a", descriptor = "(Ljava/io/File;ILjava/lang/String;)V")
		@JvmStatic
		actual fun registerNativeLibraryFile(@OriginalArg(0) file: File?, @OriginalArg(2) string: String?) {
			NativeLibraryOwner.nativeLibraryFiles.put(string, file)
		}

		@OriginalMember(owner = "client!si", name = "a", descriptor = "(Z)Z")
		@JvmStatic
		actual fun unloadNativeLibraries(): Boolean {
			@Pc(9) val hashtable: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()
			@Pc(12) var enumeration: Enumeration<Any?> = NativeLibraryOwner.nativeLibraryOwners.keys()
			while (enumeration.hasMoreElements()) {
				@Pc(17) val `object`: Any? = enumeration.nextElement()
				hashtable.put(`object`, NativeLibraryOwner.nativeLibraryOwners.get(`object`))
			}
			try {
				@Pc(30) val class2: Class<*> = Class.forName("java.lang.reflect.AccessibleObject")
				@Pc(33) val class3: Class<*> = Class.forName("java.lang.ClassLoader")
				@Pc(37) val field: Field = class3.getDeclaredField("nativeLibraries")
				@Pc(52) val method: Method = class2.getDeclaredMethod("setAccessible", java.lang.Boolean.TYPE)
				method.invoke(field, java.lang.Boolean.TRUE)
				try {
					enumeration = NativeLibraryOwner.nativeLibraryOwners.keys()
					while (enumeration.hasMoreElements()) {
						@Pc(71) val string: String? = enumeration.nextElement() as String?
						try {
							@Pc(76) val file: File? = NativeLibraryOwner.nativeLibraryFiles.get(string) as File?
							@Pc(81) val class4: Class<*>? = NativeLibraryOwner.nativeLibraryOwners.get(string) as Class<*>?
							@Pc(87) val vector: Vector<*>? = field.get(class4!!.getClassLoader()) as Vector<*>?
							@Pc(89) var i: Int = 0
							while (vector!!.size > i) {
								try {
									@Pc(95) val object2: Any? = vector.elementAt(i)
									@Pc(100) val field2: Field = object2!!.javaClass.getDeclaredField("name")
									method.invoke(field2, java.lang.Boolean.TRUE)
									try {
										@Pc(115) val string2: String? = field2.get(object2) as String?
										if (string2 != null && string2.equals(file!!.getCanonicalPath(), ignoreCase = true)) {
											@Pc(127) val field3: Field = object2.javaClass.getDeclaredField("handle")
											@Pc(134) val method2: Method = object2.javaClass.getDeclaredMethod("finalize")
											method.invoke(field3, java.lang.Boolean.TRUE)
											method.invoke(method2, java.lang.Boolean.TRUE)
											try {
												method2.invoke(object2)
												field3.set(object2, Integer.valueOf(0))
												hashtable.remove(string)
											} catch (@Pc(173) ex: Throwable) {
											}
											method.invoke(method2, java.lang.Boolean.FALSE)
											method.invoke(field3, java.lang.Boolean.FALSE)
										}
									} catch (@Pc(195) ex2: Throwable) {
									}
									method.invoke(field2, java.lang.Boolean.FALSE)
								} catch (@Pc(207) ex3: Throwable) {
								}
								i++
							}
						} catch (@Pc(219) ex4: Throwable) {
						}
					}
				} catch (@Pc(225) ex5: Throwable) {
				}
				method.invoke(field, java.lang.Boolean.FALSE)
			} catch (@Pc(237) ex6: Throwable) {
			}
			NativeLibraryOwner.nativeLibraryOwners = hashtable
			return NativeLibraryOwner.nativeLibraryOwners.isEmpty()
		}

		@OriginalMember(owner = "client!vb", name = "a", descriptor = "Ljava/util/Hashtable;")
		@JvmField
		actual val nativeLibraryFiles: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()
	}
}
