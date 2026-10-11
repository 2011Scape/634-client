package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.ProgramGL;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pl")
public final class GlArbProgram {

	@OriginalMember(owner = "client!pl", name = "b", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!pl", name = "d", descriptor = "I")
	public final int programId;

	@OriginalMember(owner = "client!pl", name = "<init>", descriptor = "(Lclient!qo;II)V")
	public GlArbProgram(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int target, @OriginalArg(2) int programId) {
		this.glToolkit = glToolkit;
		this.programId = programId;
	}

	@OriginalMember(owner = "client!pl", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glToolkit.requestArbProgramCleanup(this.programId);
		super.finalize();
	}
	@OriginalMember(owner = "client!iu", name = "a", descriptor = "(ILclient!qo;Ljava/lang/String;Z)Lclient!pl;")
	public static GlArbProgram create(@OriginalArg(1) GlToolkit glToolkit, @OriginalArg(2) String string) {
		@Pc(5) int programId = OpenGL.glGenProgramARB();
		OpenGL.glBindProgramARB(34336, programId);
		OpenGL.glProgramStringARB(34336, OpenGLConstants.GL_PROGRAM_FORMAT_ASCII_ARB, string);
		OpenGL.glGetIntegerv(OpenGLConstants.GL_PROGRAM_ERROR_POSITION_ARB, ProgramGL.HANDLE, 0);
		if (ProgramGL.HANDLE[0] == -1) {
			OpenGL.glBindProgramARB(34336, 0);
			return new GlArbProgram(glToolkit, 34336, programId);
		} else {
			OpenGL.glBindProgramARB(34336, 0);
			return null;
		}
	}
}
