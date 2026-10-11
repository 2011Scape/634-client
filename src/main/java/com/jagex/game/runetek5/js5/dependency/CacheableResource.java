package com.jagex.game.runetek5.js5.dependency;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;
import com.jagex.game.runetek5.client.loading.loader.Loader;

@OriginalClass("client!w")
public final class CacheableResource implements Loader {

	@OriginalMember(owner = "client!w", name = "h", descriptor = "Lclient!in;")
	private final Js5ResourceProvider resourceProvider;

	@OriginalMember(owner = "client!w", name = "<init>", descriptor = "(Lclient!in;)V")
	public CacheableResource(@OriginalArg(0) Js5ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	@OriginalMember(owner = "client!w", name = "a", descriptor = "(I)I")
	@Override
	public int completePercentage() {
		return this.resourceProvider.fullyLoaded() ? 100 : this.resourceProvider.getCompletePercentage();
	}

	@OriginalMember(owner = "client!w", name = "b", descriptor = "(I)Lclient!kea;")
	@Override
	public LoadingRequirementType getRequirementType() {
		return LoadingRequirementType.JS5;
	}
}
