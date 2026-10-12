/*
 * Not part of the original client: native library loading in a browser (milestone 4), which has
 * none to load. The client reads this as a JVM whose game cache lacks the libraries: none loads,
 * none is present, unloading succeeds, and it keeps to the Java toolkits.
 */
package com.jagex.game.runetek5

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sign.SignLink
import io.File
import lang.Class
import util.Hashtable

actual class NativeLibraryOwner {
	actual companion object {
		actual fun loadLibrary(class2: Class<*>?, string: String?): Boolean = false

		actual fun loadLibrary(string: String?): Boolean = false

		actual fun isLibraryLoaded(string: String?): Boolean = false

		actual fun init(nativesResource: Js5ResourceProvider?, signLink: SignLink?) {
			this.signLink = signLink
		}

		actual var nativeLibraryOwners: Hashtable<Any?, Any?> = Hashtable()

		actual fun getNativeLibraryFileName(string: String?): String? = null

		actual fun hasNativeLibraryFile(string: String?): Boolean = false

		actual var signLink: SignLink? = null

		actual var nativeLibraryPathPrefix: String? = null

		actual fun registerNativeLibraryFile(file: File?, string: String?) {
		}

		actual fun unloadNativeLibraries(): Boolean = true

		actual val nativeLibraryFiles: Hashtable<Any?, Any?> = Hashtable()
	}
}
