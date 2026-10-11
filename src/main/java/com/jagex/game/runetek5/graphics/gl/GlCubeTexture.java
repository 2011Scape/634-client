package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!q")
public final class GlCubeTexture extends GlTexture {

	@OriginalMember(owner = "client!q", name = "w", descriptor = "I")
	private int framebufferAttachment = -1;

	@OriginalMember(owner = "client!q", name = "x", descriptor = "I")
	private int framebufferTarget = -1;

	@OriginalMember(owner = "client!q", name = "z", descriptor = "I")
	public final int size;

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;II)V")
	public GlCubeTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int size) {
		super(glToolkit, 34067, internalFormat, size * size * 6, false);
		this.size = size;
		super.glToolkit.setActiveTexture(this);
		for (@Pc(28) int i = 0; i < 6; i++) {
			OpenGL.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, GlTexture.getBaseFormat(super.internalFormat), OpenGLConstants.GL_UNSIGNED_BYTE, null, 0);
		}
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;IIZ[[BI)V")
	public GlCubeTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int size, @OriginalArg(3) boolean mipmapped, @OriginalArg(4) byte[][] faces, @OriginalArg(5) int format) {
		super(glToolkit, 34067, internalFormat, size * size * 6, mipmapped);
		this.size = size;
		super.glToolkit.setActiveTexture(this);
		for (@Pc(28) int i = 0; i < 6; i++) {
			OpenGL.glTexImage2Dub(i + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, format, OpenGLConstants.GL_UNSIGNED_BYTE, faces[i], 0);
		}
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!q", name = "<init>", descriptor = "(Lclient!qo;IIZ[[I)V")
	public GlCubeTexture(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int internalFormat, @OriginalArg(2) int size, @OriginalArg(3) boolean mipmapped, @OriginalArg(4) int[][] faces) {
		super(glToolkit, 34067, internalFormat, size * 6 * size, mipmapped);
		this.size = size;
		super.glToolkit.setActiveTexture(this);
		@Pc(30) int face;
		if (mipmapped) {
			for (face = 0; face < 6; face++) {
				GlTexture.uploadMipmapped(face + 34069, super.internalFormat, size, super.glToolkit.pixelType, size, faces[face]);
			}
		} else {
			for (face = 0; face < 6; face++) {
				OpenGL.glTexImage2Di(face + OpenGLConstants.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, super.internalFormat, size, size, 0, OpenGLConstants.GL_BGRA, super.glToolkit.pixelType, faces[face], 0);
			}
		}
		this.setLinearFilter(true);
	}

	@OriginalMember(owner = "client!q", name = "a", descriptor = "(IIIII)V")
	public void attachToFramebuffer(@OriginalArg(0) int framebufferTarget, @OriginalArg(1) int faceTarget, @OriginalArg(2) int attachment) {
		OpenGL.glFramebufferTexture2DEXT(framebufferTarget, attachment, faceTarget, super.id, 0);
		this.framebufferTarget = framebufferTarget;
		this.framebufferAttachment = attachment;
	}

	@OriginalMember(owner = "client!q", name = "a", descriptor = "(I)V")
	@Override
	public void detach() {
		OpenGL.glFramebufferTexture2DEXT(this.framebufferTarget, this.framebufferAttachment, OpenGLConstants.GL_TEXTURE_2D, 0, 0);
		this.framebufferAttachment = -1;
		this.framebufferTarget = -1;
	}
}
