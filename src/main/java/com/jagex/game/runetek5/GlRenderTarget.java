package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tv")
public interface GlRenderTarget {

	@OriginalMember(owner = "client!tv", name = "a", descriptor = "(B)V")
	public void unbindRead();

	@OriginalMember(owner = "client!tv", name = "a", descriptor = "(I)V")
	public void bind();

	@OriginalMember(owner = "client!tv", name = "b", descriptor = "(I)V")
	public void bindDraw();

	@OriginalMember(owner = "client!tv", name = "c", descriptor = "(I)V")
	public void unbind();

	@OriginalMember(owner = "client!tv", name = "d", descriptor = "(I)V")
	public void bindRead();

	@OriginalMember(owner = "client!tv", name = "e", descriptor = "(I)V")
	public void unbindDraw();
}
