package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import awt.Canvas
import lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredMethod

object D3DToolkitFactory {

	@OriginalMember(owner = "client!mb", name = "a", descriptor = "(IILclient!d;Lclient!in;Ljava/awt/Canvas;)Lclient!ha;")
	@JvmStatic
	fun createD3DToolkit(@OriginalArg(0) multiSamples: Int, @OriginalArg(2) textureSource: MaterialProvider?, @OriginalArg(3) shaderResource: Js5ResourceProvider?, @OriginalArg(4) canvas: Canvas?): Toolkit? {
		try {
			if (!D3DToolkitFactory.loadHw3dLibraries()) {
				throw RuntimeException("")
			} else if (NativeLibraryOwner.loadLibrary("jagdx")) {
				@Pc(26) val class2: Class<*> = lang.forName("com.jagex.game.runetek5.toolkit.D3DToolkit")
				@Pc(52) val method: Method = class2.getDeclaredMethod("createToolkit", lang.forName("java.awt.Canvas"), lang.forName("com.jagex.game.runetek5.config.material.MaterialProvider"), lang.forName("com.jagex.game.runetek5.js5.Js5ResourceProvider"), lang.forName("java.lang.Integer"))
				return method.invoke(null, canvas, textureSource, shaderResource, (multiSamples)) as Toolkit?
			} else {
				throw RuntimeException("")
			}
		} catch (@Pc(82) ex: Throwable) {
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!gs", name = "a", descriptor = "(I)Z")
	@JvmStatic
	fun loadHw3dLibraries(): Boolean {
		return if (NativeLibraryOwner.loadLibrary("jaclib")) NativeLibraryOwner.loadLibrary("hw3d") else false
	}
}
