package com.jagex.game.runetek5;

import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!aq")
public final class VertexDeclarationGL extends VertexDeclaration {

	@OriginalMember(owner = "client!aq", name = "n", descriptor = "[Lclient!jl;")
	public final VertexDeclarationComponent[] vertexDeclarationElements;

	@OriginalMember(owner = "client!aq", name = "<init>", descriptor = "([Lclient!jl;)V")
	public VertexDeclarationGL(@OriginalArg(0) VertexDeclarationComponent[] vertexDeclarationElements) {
		this.vertexDeclarationElements = vertexDeclarationElements;
	}
}
