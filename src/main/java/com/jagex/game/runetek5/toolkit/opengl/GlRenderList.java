package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!jv")
public final class GlRenderList {

	@OriginalMember(owner = "client!jv", name = "f", descriptor = "I")
	private final int reservedRange;

	@OriginalMember(owner = "client!jv", name = "<init>", descriptor = "(Lclient!qo;I)V")
	public GlRenderList(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int range) {
		this.reservedRange = OpenGL.glGenLists(range);
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(I)V")
	public void endList() {
		OpenGL.glEndList();
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(CI)V")
	public void callList(@OriginalArg(0) char offset) {
		OpenGL.glCallList(this.reservedRange + offset);
	}

	@OriginalMember(owner = "client!jv", name = "a", descriptor = "(BI)V")
	public void newList(@OriginalArg(1) int offset) {
		OpenGL.glNewList(offset + this.reservedRange, OpenGLConstants.GL_COMPILE);
	}
}
