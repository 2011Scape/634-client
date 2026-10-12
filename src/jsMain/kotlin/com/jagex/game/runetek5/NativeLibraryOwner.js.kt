/*
 * Not part of the original client: native library loading in a browser (milestones 4 and 5). jaclib
 * and jaggl are emulated in jsMain (the jaclib memory classes, and jaggl.OpenGL on WebGL 2), so they
 * count as loaded and the Java OpenGL toolkit (1) can start. The others are not there: the client
 * reads that as a game cache that lacks them, and keeps to the toolkits it can make.
 */
package com.jagex.game.runetek5

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sign.SignLink
import io.File
import lang.Class
import util.Hashtable

actual class NativeLibraryOwner {
	actual companion object {
		/* The libraries jsMain emulates. */
		private fun emulated(string: String?): Boolean = string == "jaclib" || string == "jaggl"

		actual fun loadLibrary(class2: Class<*>?, string: String?): Boolean = emulated(string)

		actual fun loadLibrary(string: String?): Boolean = emulated(string)

		actual fun isLibraryLoaded(string: String?): Boolean = emulated(string)

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
