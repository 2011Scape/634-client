package com.jagex.game.runetek5.graphics.model.normal;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!fb")
public final class VertexNormal {

	@OriginalMember(owner = "client!fb", name = "a", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!fb", name = "b", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!fb", name = "c", descriptor = "I")
	public int magnitude;

	@OriginalMember(owner = "client!fb", name = "d", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!fb", name = "<init>", descriptor = "()V")
	public VertexNormal() {
	}

	@OriginalMember(owner = "client!fb", name = "<init>", descriptor = "(Lclient!fb;)V")
	public VertexNormal(@OriginalArg(0) VertexNormal vertexNormal) {
		this.x = vertexNormal.x;
		this.y = vertexNormal.y;
		this.z = vertexNormal.z;
		this.magnitude = vertexNormal.magnitude;
	}
}
