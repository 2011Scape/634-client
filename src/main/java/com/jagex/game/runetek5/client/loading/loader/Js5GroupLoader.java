package com.jagex.game.runetek5.client.loading.loader;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;

@OriginalClass("client!nv")
public final class Js5GroupLoader implements Loader {

	@OriginalMember(owner = "client!nv", name = "b", descriptor = "Ljava/lang/String;")
	private final String group;

	@OriginalMember(owner = "client!nv", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider config;

	@OriginalMember(owner = "client!nv", name = "<init>", descriptor = "(Lclient!in;Ljava/lang/String;)V")
	public Js5GroupLoader(@OriginalArg(0) Js5ResourceProvider config, @OriginalArg(1) String string) {
		this.group = string;
		this.config = config;
	}

	@OriginalMember(owner = "client!nv", name = "b", descriptor = "(I)Lclient!kea;")
	@Override
	public LoadingRequirementType getRequirementType() {
		return LoadingRequirementType.JS5_GROUP;
	}

	@OriginalMember(owner = "client!nv", name = "a", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		return this.config.isGroupReady(this.group) ? 100 : this.config.getGroupCompletePercentage(this.group);
	}
}
