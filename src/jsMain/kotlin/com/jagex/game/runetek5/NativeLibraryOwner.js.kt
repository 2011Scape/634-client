/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package com.jagex.game.runetek5

import NativeUnavailableException
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sign.SignLink
import io.File
import lang.Class
import nativeUnavailable
import util.Hashtable

actual class NativeLibraryOwner {
	actual companion object {
		actual fun loadLibrary(class2: Class<*>?, string: String?): Boolean {
			throw NativeUnavailableException("loadLibrary")
		}
		actual fun loadLibrary(string: String?): Boolean {
			throw NativeUnavailableException("loadLibrary")
		}
		actual fun isLibraryLoaded(string: String?): Boolean {
			throw NativeUnavailableException("isLibraryLoaded")
		}
		actual fun init(nativesResource: Js5ResourceProvider?, signLink: SignLink?) {
			throw NativeUnavailableException("init")
		}
		actual var nativeLibraryOwners: Hashtable<Any?, Any?>
			get() = throw NativeUnavailableException("nativeLibraryOwners")
			set(value) {
			}
		actual fun getNativeLibraryFileName(string: String?): String? {
			throw NativeUnavailableException("getNativeLibraryFileName")
		}
		actual fun hasNativeLibraryFile(string: String?): Boolean {
			throw NativeUnavailableException("hasNativeLibraryFile")
		}
		actual var signLink: SignLink? = null
		actual var nativeLibraryPathPrefix: String? = null
		actual fun registerNativeLibraryFile(file: File?, string: String?) {
			throw NativeUnavailableException("registerNativeLibraryFile")
		}
		actual fun unloadNativeLibraries(): Boolean {
			throw NativeUnavailableException("unloadNativeLibraries")
		}
		actual val nativeLibraryFiles: Hashtable<Any?, Any?>
			get() = throw NativeUnavailableException("nativeLibraryFiles")
	}
}
