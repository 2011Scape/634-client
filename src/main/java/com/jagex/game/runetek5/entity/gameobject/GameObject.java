package com.jagex.game.runetek5.entity.gameobject;

import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!se")
public interface GameObject {

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(I)Z")
	public boolean hardShadow();

	@OriginalMember(owner = "client!se", name = "b", descriptor = "(I)I")
	public int getShape();

	@OriginalMember(owner = "client!se", name = "c", descriptor = "(I)V")
	public void upload();

	@OriginalMember(owner = "client!se", name = "d", descriptor = "(I)I")
	public int getRotation();

	@OriginalMember(owner = "client!se", name = "e", descriptor = "(I)I")
	public int getId();

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(Lclient!ha;I)V")
	public void removeShadow(@OriginalArg(0) Toolkit toolkit);

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(ILclient!ha;)V")
	public void addShadow(@OriginalArg(1) Toolkit toolkit);
}
