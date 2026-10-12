/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/com/jagex/game/runetek5/NativeLibraryOwner.kt), so that common code can use it (milestone 3).
 */
package com.jagex.game.runetek5

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sign.SignLink
import io.File
import lang.Class
import util.Hashtable


expect class NativeLibraryOwner {
	companion object {
		fun loadLibrary(class2: Class<*>?, string: String?): Boolean
		fun loadLibrary(string: String?): Boolean
		fun isLibraryLoaded(string: String?): Boolean
		fun init(nativesResource: Js5ResourceProvider?, signLink: SignLink?)
		var nativeLibraryOwners: Hashtable<Any?, Any?>
		fun getNativeLibraryFileName(string: String?): String?
		fun hasNativeLibraryFile(string: String?): Boolean
		var signLink: SignLink?
		var nativeLibraryPathPrefix: String?
		fun registerNativeLibraryFile(file: File?, string: String?)
		fun unloadNativeLibraries(): Boolean
		val nativeLibraryFiles: Hashtable<Any?, Any?>
	}
}
