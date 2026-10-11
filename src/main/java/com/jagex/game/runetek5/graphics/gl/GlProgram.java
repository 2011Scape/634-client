package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.GlShader;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.shader.OpenGLShaderManager;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!dw")
public final class GlProgram {

	@OriginalMember(owner = "client!dw", name = "a", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!dw", name = "f", descriptor = "J")
	public final long programHandle;

	@OriginalMember(owner = "client!dw", name = "<init>", descriptor = "(Lclient!qo;J[Lclient!tba;)V")
	public GlProgram(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) long programHandle, @OriginalArg(2) GlShader[] shaders) {
		this.glToolkit = glToolkit;
		this.programHandle = programHandle;
	}

	@OriginalMember(owner = "client!dw", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glToolkit.requestShaderProgramCleanup(this.programHandle);
		super.finalize();
	}
	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!qo;I[Lclient!tba;)Lclient!dw;")
	public static GlProgram link(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(2) GlShader[] shaders) {
		for (@Pc(7) int i = 0; i < shaders.length; i++) {
			if (shaders[i] == null || shaders[i].handle <= 0L) {
				return null;
			}
		}
		@Pc(33) long program = OpenGL.glCreateProgramObjectARB();
		for (@Pc(35) int j = 0; j < shaders.length; j++) {
			OpenGL.glAttachObjectARB(program, shaders[j].handle);
		}
		OpenGL.glLinkProgramARB(program);
		OpenGL.glGetObjectParameterivARB(program, 35714, OpenGLShaderManager.BUFFER, 0);
		if (OpenGLShaderManager.BUFFER[0] == 0) {
			if (OpenGLShaderManager.BUFFER[0] == 0) {
				System.out.println("Shader linking failed:");
			}
			OpenGL.glGetObjectParameterivARB(program, 35716, OpenGLShaderManager.BUFFER, 1);
			if (OpenGLShaderManager.BUFFER[1] > 1) {
				@Pc(88) byte[] infoLog = new byte[OpenGLShaderManager.BUFFER[1]];
				OpenGL.glGetInfoLogARB(program, OpenGLShaderManager.BUFFER[1], OpenGLShaderManager.BUFFER, 0, infoLog, 0);
				System.out.println(new String(infoLog));
			}
			if (OpenGLShaderManager.BUFFER[0] == 0) {
				for (@Pc(109) int k = 0; k < shaders.length; k++) {
					OpenGL.glDetachObjectARB(program, shaders[k].handle);
				}
				OpenGL.glDeleteObjectARB(program);
				return null;
			}
		}
		return new GlProgram(glToolkit, program, shaders);
	}
}
