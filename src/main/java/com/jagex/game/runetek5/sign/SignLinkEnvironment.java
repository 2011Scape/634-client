package com.jagex.game.runetek5.sign;

import com.jagex.game.runetek5.core.constants.FileStore;
import com.jagex.game.runetek5.cache.JFS32;
import java.io.File;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class SignLinkEnvironment {

	@OriginalMember(owner = "client!wea", name = "h", descriptor = "Ljava/lang/reflect/Method;")
	public static Method setFocusTraversalKeysEnabledMethod;

	@OriginalMember(owner = "client!wea", name = "i", descriptor = "Ljava/lang/String;")
	public static String operatingSystem;

	@OriginalMember(owner = "client!wea", name = "k", descriptor = "Ljava/lang/reflect/Method;")
	public static Method setFocusCycleRootMethod;

	@OriginalMember(owner = "client!wea", name = "n", descriptor = "Ljava/lang/String;")
	public static String gameName;

	@OriginalMember(owner = "client!wea", name = "y", descriptor = "Ljava/lang/String;")
	public static String preferencesParentFile;

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/String;I)Lclient!so;")
	public static JFS32 openGamePreferencesFile(@OriginalArg(0) String string) {
		return SignLink.openPreferencesFile(string, gameName, SignLink.cacheId);
	}
}
