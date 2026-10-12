package com.jagex.game.runetek5

import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!aq")
class VertexDeclarationGL @OriginalMember(owner = "client!aq", name = "<init>", descriptor = "([Lclient!jl;)V") constructor(@OriginalArg(0) vertexDeclarationElements: Array<VertexDeclarationComponent?>?) : VertexDeclaration() {

	@OriginalMember(owner = "client!aq", name = "n", descriptor = "[Lclient!jl;")
	@JvmField
	val vertexDeclarationElements: Array<VertexDeclarationComponent?>?

	init {
		this.vertexDeclarationElements = vertexDeclarationElements
	}
}
