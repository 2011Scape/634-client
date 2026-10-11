package com.jagex.game.runetek5.js5.dependency;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;
import com.jagex.game.runetek5.client.loading.loader.Loader;
import com.jagex.game.runetek5.config.Resource;
import java.io.File;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.CacheFileLocator;

@OriginalClass("client!cf")
public final class CacheableDLL implements Loader {

	@OriginalMember(owner = "client!cf", name = "s", descriptor = "Z")
	private boolean doneLoading;

	@OriginalMember(owner = "client!cf", name = "b", descriptor = "Ljava/lang/String;")
	private final String nativeName;

	@OriginalMember(owner = "client!cf", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	public CacheableDLL(@OriginalArg(0) String string) {
		this.nativeName = string;
	}

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		@Pc(16) int percentage = CacheableDLL.installNativeLibrary(this.nativeName);
		if (percentage >= 0 && percentage <= 100) {
			return percentage;
		} else {
			this.doneLoading = true;
			return 100;
		}
	}

	@OriginalMember(owner = "client!cf", name = "b", descriptor = "(I)Lclient!kea;")
	@Override
	public LoadingRequirementType getRequirementType() {
		return LoadingRequirementType.NATIVE_LIBRARY;
	}

	@OriginalMember(owner = "client!cf", name = "c", descriptor = "(I)Z")
	public boolean isDone() {
		return this.doneLoading;
	}
	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Ljava/lang/String;I)I")
	public static int installNativeLibrary(@OriginalArg(0) String string) {
		if (!NativeLibraryOwner.signLink.signed) {
			return -1;
		} else if (NativeLibraryOwner.nativeLibraryFiles.containsKey(string)) {
			return 100;
		} else {
			@Pc(20) String string2 = NativeLibraryOwner.getNativeLibraryFileName(string);
			if (string2 == null) {
				return -1;
			}
			@Pc(33) String string3 = NativeLibraryOwner.nativeLibraryPathPrefix + string2;
			if (!Resource.aClass157_219.fileExists("", string3)) {
				return -1;
			} else if (Resource.aClass157_219.isGroupReady(string3)) {
				@Pc(67) byte[] data = Resource.aClass157_219.getFile(string3, "");
				@Pc(75) File file;
				try {
					file = CacheFileLocator.loadFromCacheDir(string2);
				} catch (@Pc(77) RuntimeException ex) {
					return -1;
				}
				if (data == null || file == null) {
					return -1;
				}
				@Pc(85) boolean identical = true;
				@Pc(91) byte[] existingData = SignLink.readFile(file);
				if (existingData != null && data.length == existingData.length) {
					for (@Pc(108) int i = 0; i < existingData.length; i++) {
						if (existingData[i] != data[i]) {
							identical = false;
							break;
						}
					}
				} else {
					identical = false;
				}
				try {
					if (!identical) {
						NativeLibraryOwner.signLink.writeFile(data, file);
					}
				} catch (@Pc(140) Throwable ex2) {
					return -1;
				}
				NativeLibraryOwner.registerNativeLibraryFile(file, string);
				return 100;
			} else {
				return Resource.aClass157_219.getGroupCompletePercentage(string3);
			}
		}
	}
}
