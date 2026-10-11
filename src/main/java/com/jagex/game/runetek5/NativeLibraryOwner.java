package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.material.texture.ColorImageCacheEntry;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.sign.SignLink;
import java.io.File;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

@OriginalClass("client!bc")
public final class NativeLibraryOwner {

	@OriginalMember(owner = "client!bc", name = "<init>", descriptor = "()V")
	private NativeLibraryOwner() {
	}
	@OriginalMember(owner = "client!gba", name = "a", descriptor = "(Ljava/lang/Class;BLjava/lang/String;)Z")
	public static boolean loadLibrary(@OriginalArg(0) Class class2, @OriginalArg(2) String string) {
		@Pc(8) Class class3 = (Class) NativeLibraryOwner.nativeLibraryOwners.get(string);
		if (class3 != null) {
			return class3.getClassLoader() == class2.getClassLoader();
		}
		@Pc(21) File file = null;
		if (file == null) {
			file = (File) NativeLibraryOwner.nativeLibraryFiles.get(string);
		}
		if (file != null) {
			try {
				file = new File(file.getCanonicalPath());
				@Pc(44) Class class4 = Class.forName("java.lang.Runtime");
				@Pc(47) Class class5 = Class.forName("java.lang.reflect.AccessibleObject");
				@Pc(57) Method method = class5.getDeclaredMethod("setAccessible", Boolean.TYPE);
				@Pc(73) Method method2 = class4.getDeclaredMethod("load0", Class.forName("java.lang.Class"), Class.forName("java.lang.String"));
				method.invoke(method2, Boolean.TRUE);
				method2.invoke(Runtime.getRuntime(), class2, file.getPath());
				method.invoke(method2, Boolean.FALSE);
				NativeLibraryOwner.nativeLibraryOwners.put(string, class2);
				return true;
			} catch (@Pc(116) NoSuchMethodException ex) {
				System.load(file.getPath());
				NativeLibraryOwner.nativeLibraryOwners.put(string, NativeLibraryOwner.class);
				return true;
			} catch (@Pc(134) Throwable ex2) {
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!qu", name = "a", descriptor = "(ILjava/lang/String;)Z")
	public static boolean loadLibrary(@OriginalArg(1) String string) {
		return NativeLibraryOwner.loadLibrary(NativeLibraryOwner.class, string);
	}

	@OriginalMember(owner = "client!vn", name = "a", descriptor = "(BLjava/lang/String;)Z")
	public static boolean isLibraryLoaded(@OriginalArg(1) String string) {
		return NativeLibraryOwner.nativeLibraryOwners.containsKey(string);
	}

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(Lclient!in;Lclient!wea;B)V")
	public static void init(@OriginalArg(0) Js5ResourceProvider nativesResource, @OriginalArg(1) SignLink signLink) {
		NativeLibraryOwner.nativeLibraryPathPrefix = "";
		NativeLibraryOwner.signLink = signLink;
		Resource.aClass157_219 = nativesResource;
		if (ColorImageCacheEntry.osName.startsWith("win")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "windows/";
		} else if (ColorImageCacheEntry.osName.startsWith("linux")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "linux/";
		} else if (ColorImageCacheEntry.osName.startsWith("mac")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "macos/";
		}
		if (NativeLibraryOwner.signLink.microsoftJava) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "msjava/";
		} else if (ColorImageCacheEntry.osArch.startsWith("amd64") || ColorImageCacheEntry.osArch.startsWith("x86_64")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "x86_64/";
		} else if (ColorImageCacheEntry.osArch.startsWith("i386") || ColorImageCacheEntry.osArch.startsWith("i486") || ColorImageCacheEntry.osArch.startsWith("i586") || ColorImageCacheEntry.osArch.startsWith("x86")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "x86/";
		} else if (ColorImageCacheEntry.osArch.startsWith("ppc")) {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "ppc/";
		} else {
			NativeLibraryOwner.nativeLibraryPathPrefix = NativeLibraryOwner.nativeLibraryPathPrefix + "universal/";
		}
	}
	@OriginalMember(owner = "client!ke", name = "me", descriptor = "Ljava/util/Hashtable;")
	public static Hashtable nativeLibraryOwners = new Hashtable();

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")
	public static String getNativeLibraryFileName(@OriginalArg(0) String string) {
		if (ColorImageCacheEntry.osName.startsWith("win")) {
			return string + ".dll";
		} else if (ColorImageCacheEntry.osName.startsWith("linux")) {
			return "lib" + string + ".so";
		} else if (ColorImageCacheEntry.osName.startsWith("mac")) {
			return "lib" + string + ".dylib";
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(Ljava/lang/String;I)Z")
	public static boolean hasNativeLibraryFile(@OriginalArg(0) String string) {
		return NativeLibraryOwner.nativeLibraryFiles.containsKey(string);
	}

	@OriginalMember(owner = "client!rga", name = "K", descriptor = "Lclient!wea;")
	public static SignLink signLink;

	@OriginalMember(owner = "client!uba", name = "q", descriptor = "Ljava/lang/String;")
	public static String nativeLibraryPathPrefix;

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(Ljava/io/File;ILjava/lang/String;)V")
	public static void registerNativeLibraryFile(@OriginalArg(0) File file, @OriginalArg(2) String string) {
		NativeLibraryOwner.nativeLibraryFiles.put(string, file);
	}

	@OriginalMember(owner = "client!si", name = "a", descriptor = "(Z)Z")
	public static boolean unloadNativeLibraries() {
		@Pc(9) Hashtable hashtable = new Hashtable();
		@Pc(12) Enumeration enumeration = NativeLibraryOwner.nativeLibraryOwners.keys();
		while (enumeration.hasMoreElements()) {
			@Pc(17) Object object = enumeration.nextElement();
			hashtable.put(object, NativeLibraryOwner.nativeLibraryOwners.get(object));
		}
		try {
			@Pc(30) Class class2 = Class.forName("java.lang.reflect.AccessibleObject");
			@Pc(33) Class class3 = Class.forName("java.lang.ClassLoader");
			@Pc(37) Field field = class3.getDeclaredField("nativeLibraries");
			@Pc(52) Method method = class2.getDeclaredMethod("setAccessible", Boolean.TYPE);
			method.invoke(field, Boolean.TRUE);
			try {
				enumeration = NativeLibraryOwner.nativeLibraryOwners.keys();
				while (enumeration.hasMoreElements()) {
					@Pc(71) String string = (String) enumeration.nextElement();
					try {
						@Pc(76) File file = (File) NativeLibraryOwner.nativeLibraryFiles.get(string);
						@Pc(81) Class class4 = (Class) NativeLibraryOwner.nativeLibraryOwners.get(string);
						@Pc(87) Vector vector = (Vector) field.get(class4.getClassLoader());
						for (@Pc(89) int i = 0; vector.size() > i; i++) {
							try {
								@Pc(95) Object object2 = vector.elementAt(i);
								@Pc(100) Field field2 = object2.getClass().getDeclaredField("name");
								method.invoke(field2, Boolean.TRUE);
								try {
									@Pc(115) String string2 = (String) field2.get(object2);
									if (string2 != null && string2.equalsIgnoreCase(file.getCanonicalPath())) {
										@Pc(127) Field field3 = object2.getClass().getDeclaredField("handle");
										@Pc(134) Method method2 = object2.getClass().getDeclaredMethod("finalize");
										method.invoke(field3, Boolean.TRUE);
										method.invoke(method2, Boolean.TRUE);
										try {
											method2.invoke(object2);
											field3.set(object2, Integer.valueOf(0));
											hashtable.remove(string);
										} catch (@Pc(173) Throwable ex) {
										}
										method.invoke(method2, Boolean.FALSE);
										method.invoke(field3, Boolean.FALSE);
									}
								} catch (@Pc(195) Throwable ex2) {
								}
								method.invoke(field2, Boolean.FALSE);
							} catch (@Pc(207) Throwable ex3) {
							}
						}
					} catch (@Pc(219) Throwable ex4) {
					}
				}
			} catch (@Pc(225) Throwable ex5) {
			}
			method.invoke(field, Boolean.FALSE);
		} catch (@Pc(237) Throwable ex6) {
		}
		NativeLibraryOwner.nativeLibraryOwners = hashtable;
		return NativeLibraryOwner.nativeLibraryOwners.isEmpty();
	}

	@OriginalMember(owner = "client!vb", name = "a", descriptor = "Ljava/util/Hashtable;")
	public static final Hashtable nativeLibraryFiles = new Hashtable();
}
