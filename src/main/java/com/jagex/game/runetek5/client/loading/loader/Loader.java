package com.jagex.game.runetek5.client.loading.loader;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;

@OriginalClass("client!lu")
public interface Loader {

	@OriginalMember(owner = "client!lu", name = "a", descriptor = "(I)I")
	public int completePercentage();

	@OriginalMember(owner = "client!lu", name = "b", descriptor = "(I)Lclient!kea;")
	public LoadingRequirementType getRequirementType();
}
