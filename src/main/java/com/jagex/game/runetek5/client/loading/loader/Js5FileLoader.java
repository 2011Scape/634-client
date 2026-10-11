package com.jagex.game.runetek5.client.loading.loader;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;

@OriginalClass("client!kk")
public final class Js5FileLoader implements Loader {

	@OriginalMember(owner = "client!kk", name = "n", descriptor = "Lclient!in;")
	private final Js5ResourceProvider config;

	@OriginalMember(owner = "client!kk", name = "g", descriptor = "Ljava/lang/String;")
	private final String fileName;

	@OriginalMember(owner = "client!kk", name = "<init>", descriptor = "(Lclient!in;Ljava/lang/String;)V")
	public Js5FileLoader(@OriginalArg(0) Js5ResourceProvider config, @OriginalArg(1) String string) {
		this.config = config;
		this.fileName = string;
	}

	@OriginalMember(owner = "client!kk", name = "a", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		return this.config.isFileReady(this.fileName) ? 100 : 0;
	}

	@OriginalMember(owner = "client!kk", name = "b", descriptor = "(I)Lclient!kea;")
	@Override
	public LoadingRequirementType getRequirementType() {
		return LoadingRequirementType.JS5_FILE;
	}
}
