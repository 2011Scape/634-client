package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ifa")
public final class GlxShader {

	@OriginalMember(owner = "client!ifa", name = "d", descriptor = "J")
	public final long handle;

	@OriginalMember(owner = "client!ifa", name = "f", descriptor = "Lclient!bga;")
	private final GlxToolkit glxToolkit;

	@OriginalMember(owner = "client!ifa", name = "<init>", descriptor = "(Lclient!bga;JI)V")
	public GlxShader(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) long handle, @OriginalArg(2) int type) {
		this.handle = handle;
		this.glxToolkit = glxToolkit;
	}

	@OriginalMember(owner = "client!ifa", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glxToolkit.requestShaderDeletion(this.handle);
		super.finalize();
	}
	@OriginalMember(owner = "client!dea", name = "a", descriptor = "(Lclient!bga;[BII)Lclient!ifa;")
	public static GlxShader compile(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) byte[] source, @OriginalArg(3) int shaderType) {
		if (source == null || source.length == 0) {
			return null;
		}
		@Pc(16) long shaderId = OpenGL.glCreateShaderObjectARB(shaderType);
		OpenGL.glShaderSourceRawARB(shaderId, source);
		OpenGL.glCompileShaderARB(shaderId);
		OpenGL.glGetObjectParameterivARB(shaderId, OpenGLConstants.GL_COMPILE_STATUS, GlxShaderCompileStatus.SHADER_PARAMS, 0);
		if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
			if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
				System.out.println("Shader compile failed:");
			}
			OpenGL.glGetObjectParameterivARB(shaderId, OpenGLConstants.GL_INFO_LOG_LENGTH, GlxShaderCompileStatus.SHADER_PARAMS, 1);
			if (GlxShaderCompileStatus.SHADER_PARAMS[1] > 1) {
				@Pc(64) byte[] infoLog = new byte[GlxShaderCompileStatus.SHADER_PARAMS[1]];
				OpenGL.glGetInfoLogARB(shaderId, GlxShaderCompileStatus.SHADER_PARAMS[1], GlxShaderCompileStatus.SHADER_PARAMS, 0, infoLog, 0);
				System.out.println(new String(infoLog));
			}
			if (GlxShaderCompileStatus.SHADER_PARAMS[0] == 0) {
				OpenGL.glDeleteObjectARB(shaderId);
				return null;
			}
		}
		return new GlxShader(glxToolkit, shaderId, shaderType);
	}
}
