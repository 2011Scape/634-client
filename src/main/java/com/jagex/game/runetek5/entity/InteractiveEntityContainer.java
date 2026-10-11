package com.jagex.game.runetek5.entity;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oga")
public final class InteractiveEntityContainer {

	@OriginalMember(owner = "client!oga", name = "b", descriptor = "Lclient!oga;")
	public InteractiveEntityContainer child;

	@OriginalMember(owner = "client!oga", name = "d", descriptor = "Lclient!jb;")
	public AbstractInteractiveEntity interactiveObject;

	@OriginalMember(owner = "client!oga", name = "a", descriptor = "(B)V")
	public void release() {
		if (InteractiveEntityContainer.activeChildCount < 500) {
			this.interactiveObject = null;
			this.child = InteractiveEntityContainer.mainInteractiveWrapper;
			InteractiveEntityContainer.mainInteractiveWrapper = this;
			InteractiveEntityContainer.activeChildCount++;
		}
	}

	@OriginalMember(owner = "client!kg", name = "g", descriptor = "I")
	public static final int poolSize = 52;
	@OriginalMember(owner = "client!hfa", name = "b", descriptor = "Lclient!oga;")
	public static InteractiveEntityContainer mainInteractiveWrapper;
	@OriginalMember(owner = "client!qo", name = "xe", descriptor = "I")
	public static int activeChildCount = 0;
	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(Lclient!jb;I)Lclient!oga;")
	public static InteractiveEntityContainer obtain(@OriginalArg(0) AbstractInteractiveEntity positionEntity) {
		@Pc(12) InteractiveEntityContainer positionEntityNode;
		if (InteractiveEntityContainer.mainInteractiveWrapper == null) {
			positionEntityNode = new InteractiveEntityContainer();
		} else {
			positionEntityNode = InteractiveEntityContainer.mainInteractiveWrapper;
			InteractiveEntityContainer.mainInteractiveWrapper = InteractiveEntityContainer.mainInteractiveWrapper.child;
			positionEntityNode.child = null;
			InteractiveEntityContainer.activeChildCount--;
		}
		positionEntityNode.interactiveObject = positionEntity;
		return positionEntityNode;
	}
}
