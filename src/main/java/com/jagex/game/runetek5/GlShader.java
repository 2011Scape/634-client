package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!tba")
public final class GlShader {

	@OriginalMember(owner = "client!tba", name = "c", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!tba", name = "f", descriptor = "J")
	public final long handle;

	@OriginalMember(owner = "client!tba", name = "<init>", descriptor = "(Lclient!qo;JI)V")
	public GlShader(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) long handle, @OriginalArg(2) int type) {
		this.glToolkit = glToolkit;
		this.handle = handle;
	}

	@OriginalMember(owner = "client!tba", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glToolkit.requestShaderProgramCleanup(this.handle);
		super.finalize();
	}
	@OriginalMember(owner = "client!et", name = "a", descriptor = "(IILclient!qo;Ljava/lang/String;)Lclient!tba;")
	public static GlShader compile(@OriginalArg(0) int shaderType, @OriginalArg(2) GlToolkit glToolkit, @OriginalArg(3) String string) {
		@Pc(14) long shaderHandle = OpenGL.glCreateShaderObjectARB(shaderType);
		OpenGL.glShaderSourceARB(shaderHandle, string);
		OpenGL.glCompileShaderARB(shaderHandle);
		OpenGL.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_COMPILE_STATUS, GlShader.queryResults, 0);
		if (GlShader.queryResults[0] == 0) {
			if (GlShader.queryResults[0] == 0) {
				System.out.println("Shader compile failed:");
			}
			OpenGL.glGetObjectParameterivARB(shaderHandle, OpenGLConstants.GL_INFO_LOG_LENGTH, GlShader.queryResults, 1);
			if (GlShader.queryResults[1] > 1) {
				@Pc(55) byte[] infoLog = new byte[GlShader.queryResults[1]];
				OpenGL.glGetInfoLogARB(shaderHandle, GlShader.queryResults[1], GlShader.queryResults, 0, infoLog, 0);
				System.out.println(new String(infoLog));
			}
			if (GlShader.queryResults[0] == 0) {
				OpenGL.glDeleteObjectARB(shaderHandle);
				return null;
			}
		}
		return new GlShader(glToolkit, shaderHandle, shaderType);
	}
	@OriginalMember(owner = "client!lm", name = "F", descriptor = "[I")
	public static final int[] queryResults = new int[2];
}
