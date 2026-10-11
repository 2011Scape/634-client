package com.jagex.game.runetek5.core.io;

import java.util.Hashtable;
import org.openrs2.deob.annotation.OriginalMember;

public final class FileCache {

	@OriginalMember(owner = "client!br", name = "a", descriptor = "I")
	public static int cacheId;

	@OriginalMember(owner = "client!br", name = "b", descriptor = "Ljava/util/Hashtable;")
	public static final Hashtable foundFiles = new Hashtable(16);

	@OriginalMember(owner = "client!br", name = "c", descriptor = "Ljava/lang/String;")
	public static String homeDirectory;

	@OriginalMember(owner = "client!br", name = "d", descriptor = "Z")
	public static boolean initialized = false;

	@OriginalMember(owner = "client!br", name = "e", descriptor = "Ljava/lang/String;")
	public static String game;
}
