package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import java.awt.Canvas;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.NativeLibraryOwner;

public final class D3DToolkitFactory {

	@OriginalMember(owner = "client!mb", name = "a", descriptor = "(IILclient!d;Lclient!in;Ljava/awt/Canvas;)Lclient!ha;")
	public static Toolkit createD3DToolkit(@OriginalArg(0) int multiSamples, @OriginalArg(2) MaterialProvider textureSource, @OriginalArg(3) Js5ResourceProvider shaderResource, @OriginalArg(4) Canvas canvas) {
		try {
			if (!D3DToolkitFactory.loadHw3dLibraries()) {
				throw new RuntimeException("");
			} else if (NativeLibraryOwner.loadLibrary("jagdx")) {
				@Pc(26) Class class2 = Class.forName("com.jagex.game.runetek5.toolkit.D3DToolkit");
				@Pc(52) Method method = class2.getDeclaredMethod("createToolkit", Class.forName("java.awt.Canvas"), Class.forName("com.jagex.game.runetek5.config.material.MaterialProvider"), Class.forName("com.jagex.game.runetek5.js5.Js5ResourceProvider"), Class.forName("java.lang.Integer"));
				return (Toolkit) method.invoke(null, canvas, textureSource, shaderResource, Integer.valueOf(multiSamples));
			} else {
				throw new RuntimeException("");
			}
		} catch (@Pc(82) Throwable ex) {
			throw new RuntimeException("");
		}
	}
	@OriginalMember(owner = "client!gs", name = "a", descriptor = "(I)Z")
	public static boolean loadHw3dLibraries() {
		return NativeLibraryOwner.loadLibrary("jaclib") ? NativeLibraryOwner.loadLibrary("hw3d") : false;
	}
}
