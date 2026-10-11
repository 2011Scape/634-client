package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!fia")
public final class Renderer {

	@OriginalMember(owner = "client!fia", name = "b", descriptor = "J")
	public final long driverVersion;

	@OriginalMember(owner = "client!fia", name = "f", descriptor = "I")
	public final int version;

	@OriginalMember(owner = "client!fia", name = "g", descriptor = "I")
	public final int vendor;

	@OriginalMember(owner = "client!fia", name = "c", descriptor = "Ljava/lang/String;")
	public final String device;

	@OriginalMember(owner = "client!fia", name = "a", descriptor = "Ljava/lang/String;")
	public final String name;

	@OriginalMember(owner = "client!fia", name = "<init>", descriptor = "(ILjava/lang/String;ILjava/lang/String;J)V")
	public Renderer(@OriginalArg(0) int vendor, @OriginalArg(1) String string, @OriginalArg(2) int version, @OriginalArg(3) String string2, @OriginalArg(4) long driverVersion) {
		this.driverVersion = driverVersion;
		this.version = version;
		this.vendor = vendor;
		this.device = string2;
		this.name = string;
	}
}
