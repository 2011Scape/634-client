package com.jagex.game.runetek5.js5.dependency

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType
import com.jagex.game.runetek5.client.loading.loader.Loader
import com.jagex.game.runetek5.config.Resource
import java.io.File
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.CacheFileLocator

@OriginalClass("client!cf")
class CacheableDLL : Loader {

	@OriginalMember(owner = "client!cf", name = "s", descriptor = "Z")
	private var doneLoading: Boolean = false

	@OriginalMember(owner = "client!cf", name = "b", descriptor = "Ljava/lang/String;")
	private val nativeName: String?

	@OriginalMember(owner = "client!cf", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) {
		this.nativeName = string
	}

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(I)I")
	override fun completePercentage(): Int {
		@Pc(16) val percentage: Int = CacheableDLL.installNativeLibrary(this.nativeName)
		if (percentage >= 0 && percentage <= 100) {
			return percentage
		} else {
			this.doneLoading = true
			return 100
		}
	}

	@OriginalMember(owner = "client!cf", name = "b", descriptor = "(I)Lclient!kea;")
	override fun getRequirementType(): LoadingRequirementType? {
		return LoadingRequirementType.NATIVE_LIBRARY
	}

	@OriginalMember(owner = "client!cf", name = "c", descriptor = "(I)Z")
	fun isDone(): Boolean {
		return this.doneLoading
	}

	companion object {
		@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Ljava/lang/String;I)I")
		@JvmStatic
		fun installNativeLibrary(@OriginalArg(0) string: String?): Int {
			if (!NativeLibraryOwner.signLink.signed) {
				return -1
			} else if (NativeLibraryOwner.nativeLibraryFiles.containsKey(string)) {
				return 100
			} else {
				@Pc(20) val string2: String? = NativeLibraryOwner.getNativeLibraryFileName(string)
				if (string2 == null) {
					return -1
				}
				@Pc(33) val string3: String = NativeLibraryOwner.nativeLibraryPathPrefix + string2
				if (!Resource.aClass157_219!!.fileExists("", string3)) {
					return -1
				} else if (Resource.aClass157_219!!.isGroupReady(string3)) {
					@Pc(67) val data: ByteArray? = Resource.aClass157_219!!.getFile(string3, "")
					@Pc(75) val file: File?
					try {
						file = CacheFileLocator.loadFromCacheDir(string2)
					} catch (@Pc(77) ex: RuntimeException) {
						return -1
					}
					if (data == null || file == null) {
						return -1
					}
					@Pc(85) var identical: Boolean = true
					@Pc(91) val existingData: ByteArray? = SignLink.readFile(file)
					if (existingData != null && data.size == existingData.size) {
						@Pc(108) var i: Int = 0
						while (i < existingData.size) {
							if (existingData[i] != data[i]) {
								identical = false
								break
							}
							i++
						}
					} else {
						identical = false
					}
					try {
						if (!identical) {
							NativeLibraryOwner.signLink.writeFile(data, file)
						}
					} catch (@Pc(140) ex2: Throwable) {
						return -1
					}
					NativeLibraryOwner.registerNativeLibraryFile(file, string)
					return 100
				} else {
					return Resource.aClass157_219!!.getGroupCompletePercentage(string3)
				}
			}
		}
	}
}
