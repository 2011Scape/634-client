package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qr")
public final class GlxArbProgram {

	@OriginalMember(owner = "client!qr", name = "c", descriptor = "Lclient!bga;")
	private final GlxToolkit glxToolkit;

	@OriginalMember(owner = "client!qr", name = "b", descriptor = "I")
	public final int programId;

	@OriginalMember(owner = "client!qr", name = "<init>", descriptor = "(Lclient!bga;II)V")
	public GlxArbProgram(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int target, @OriginalArg(2) int programId) {
		this.glxToolkit = glxToolkit;
		this.programId = programId;
	}

	@OriginalMember(owner = "client!qr", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glxToolkit.destroyProgram(this.programId);
		super.finalize();
	}
	@OriginalMember(owner = "client!cu", name = "a", descriptor = "(I[BLclient!bga;I)Lclient!qr;")
	public static GlxArbProgram createVertexProgram(@OriginalArg(1) byte[] programData, @OriginalArg(2) GlxToolkit glxToolkit) {
		if (programData == null) {
			return null;
		}
		@Pc(9) int programId = OpenGL.glGenProgramARB();
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, programId);
		OpenGL.glProgramRawARB(34336, 34933, programData);
		OpenGL.glGetIntegerv(OpenGLConstants.GL_PROGRAM_ERROR_POSITION_ARB, GlxArbProgram.PROGRAM_ERROR_POSITION, 0);
		if (GlxArbProgram.PROGRAM_ERROR_POSITION[0] == -1) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
			return new GlxArbProgram(glxToolkit, 34336, programId);
		} else {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
			return null;
		}
	}
	@OriginalMember(owner = "client!mha", name = "q", descriptor = "[I")
	public static final int[] PROGRAM_ERROR_POSITION = new int[1];
}
