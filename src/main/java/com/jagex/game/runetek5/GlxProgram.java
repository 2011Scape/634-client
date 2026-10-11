package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import jaggl.OpenGL;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!lha")
public final class GlxProgram {

	@OriginalMember(owner = "client!lha", name = "c", descriptor = "[I")
	public static final int[] CRC_TABLE = new int[256];

	@OriginalMember(owner = "client!lha", name = "e", descriptor = "J")
	public final long handle;

	@OriginalMember(owner = "client!lha", name = "a", descriptor = "Lclient!bga;")
	private final GlxToolkit glxToolkit;

	static {
		for (@Pc(4) int i = 0; i < 256; i++) {
			@Pc(7) int crc = i;
			for (@Pc(9) int bit = 0; bit < 8; bit++) {
				if ((crc & 0x1) == 1) {
					crc = crc >>> 1 ^ 0xEDB88320;
				} else {
					crc >>>= 0x1;
				}
			}
			CRC_TABLE[i] = crc;
		}
	}

	@OriginalMember(owner = "client!lha", name = "<init>", descriptor = "(Lclient!bga;J[Lclient!ifa;)V")
	public GlxProgram(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) long handle, @OriginalArg(2) GlxShader[] shaders) {
		this.handle = handle;
		this.glxToolkit = glxToolkit;
	}

	@OriginalMember(owner = "client!lha", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.glxToolkit.requestShaderDeletion(this.handle);
		super.finalize();
	}
	@OriginalMember(owner = "client!da", name = "a", descriptor = "(Lclient!bga;[Lclient!ifa;I)Lclient!lha;")
	public static GlxProgram create(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) GlxShader[] shaders) {
		for (@Pc(12) int i = 0; i < shaders.length; i++) {
			if (shaders[i] == null || shaders[i].handle <= 0L) {
				return null;
			}
		}
		@Pc(36) long programHandle = OpenGL.glCreateProgramObjectARB();
		for (@Pc(38) int j = 0; j < shaders.length; j++) {
			OpenGL.glAttachObjectARB(programHandle, shaders[j].handle);
		}
		OpenGL.glLinkProgramARB(programHandle);
		OpenGL.glGetObjectParameterivARB(programHandle, 35714, GlxProgram.glStatusParams, 0);
		if (GlxProgram.glStatusParams[0] == 0) {
			if (GlxProgram.glStatusParams[0] == 0) {
				System.out.println("Shader linking failed:");
			}
			OpenGL.glGetObjectParameterivARB(programHandle, 35716, GlxProgram.glStatusParams, 1);
			if (GlxProgram.glStatusParams[1] > 1) {
				@Pc(94) byte[] infoLog = new byte[GlxProgram.glStatusParams[1]];
				OpenGL.glGetInfoLogARB(programHandle, GlxProgram.glStatusParams[1], GlxProgram.glStatusParams, 0, infoLog, 0);
				System.out.println(new String(infoLog));
			}
			if (GlxProgram.glStatusParams[0] == 0) {
				for (@Pc(115) int k = 0; k < shaders.length; k++) {
					OpenGL.glDetachObjectARB(programHandle, shaders[k].handle);
				}
				OpenGL.glDeleteObjectARB(programHandle);
				return null;
			}
		}
		return new GlxProgram(glxToolkit, programHandle, shaders);
	}
	@OriginalMember(owner = "client!ro", name = "n", descriptor = "[I")
	public static final int[] glStatusParams = new int[2];
}
