package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.Loader;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;

@OriginalClass("client!up")
public final class LoadingRequirement {

	@OriginalMember(owner = "client!up", name = "i", descriptor = "Lclient!lu;")
	private Loader loader;

	@OriginalMember(owner = "client!up", name = "e", descriptor = "I")
	private int size = 1;

	@OriginalMember(owner = "client!up", name = "c", descriptor = "Lclient!kea;")
	private final LoadingRequirementType type;

	@OriginalMember(owner = "client!up", name = "<init>", descriptor = "(Lclient!kea;)V")
	public LoadingRequirement(@OriginalArg(0) LoadingRequirementType loadingRequirementType) {
		this.type = loadingRequirementType;
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(I)I")
	public int getSize() {
		return this.size;
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(Lclient!lu;Z)V")
	public void setLoader(@OriginalArg(0) Loader loader) {
		if (loader.getRequirementType() != this.type) {
			throw new IllegalArgumentException();
		}
		this.loader = loader;
	}

	@OriginalMember(owner = "client!up", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!up", name = "c", descriptor = "(I)Lclient!lu;")
	public Loader getLoader() {
		return this.loader;
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(II)V")
	public void setSize(@OriginalArg(1) int size) {
		this.size = size;
	}
}
