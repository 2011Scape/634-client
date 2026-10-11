package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.companion.Binding;

@OriginalClass("client!kw")
public final class OpenGlRenderBuffer extends CacheableNode implements Binding {

	@OriginalMember(owner = "client!kw", name = "x", descriptor = "I")
	private int target = -1;

	@OriginalMember(owner = "client!kw", name = "y", descriptor = "I")
	private int attachment = -1;

	@OriginalMember(owner = "client!kw", name = "H", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!kw", name = "z", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!kw", name = "A", descriptor = "I")
	public final int height;

	@OriginalMember(owner = "client!kw", name = "C", descriptor = "I")
	private final int format;

	@OriginalMember(owner = "client!kw", name = "E", descriptor = "I")
	private int handle;

	@OriginalMember(owner = "client!kw", name = "G", descriptor = "I")
	private final int bufferSizeBytes;

	@OriginalMember(owner = "client!kw", name = "<init>", descriptor = "(Lclient!qo;III)V")
	public OpenGlRenderBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int format, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		this.width = width;
		this.glToolkit = glToolkit;
		this.height = height;
		this.format = format;
		OpenGL.glGenRenderbuffersEXT(1, OpenGlRenderBuffer.handleBuffer, 0);
		this.handle = OpenGlRenderBuffer.handleBuffer[0];
		OpenGL.glBindRenderbufferEXT(36161, this.handle);
		OpenGL.glRenderbufferStorageEXT(36161, this.format, this.width, this.height);
		this.bufferSizeBytes = this.height * this.width * this.glToolkit.getBytesPerPixel(this.format);
	}

	@OriginalMember(owner = "client!kw", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	public OpenGlRenderBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int format, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int samples) {
		this.width = width;
		this.format = format;
		this.height = height;
		this.glToolkit = glToolkit;
		OpenGL.glGenRenderbuffersEXT(1, OpenGlRenderBuffer.handleBuffer, 0);
		this.handle = OpenGlRenderBuffer.handleBuffer[0];
		OpenGL.glBindRenderbufferEXT(36161, this.handle);
		OpenGL.glRenderbufferStorageMultisampleEXT(36161, samples, this.format, this.width, this.height);
		this.bufferSizeBytes = this.height * this.width * this.glToolkit.getBytesPerPixel(this.format);
	}

	@OriginalMember(owner = "client!kw", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() throws Throwable {
		this.dispose();
		super.finalize();
	}

	@OriginalMember(owner = "client!kw", name = "d", descriptor = "(I)V")
	public void dispose() {
		if (this.handle > 0) {
			this.glToolkit.requestRenderBufferCleanup(this.handle, this.bufferSizeBytes);
			this.handle = 0;
		}
	}

	@OriginalMember(owner = "client!kw", name = "b", descriptor = "(III)V")
	public void attach(@OriginalArg(1) int attachment, @OriginalArg(2) int target) {
		OpenGL.glFramebufferRenderbufferEXT(target, attachment, OpenGLConstants.GL_RENDERBUFFER, this.handle);
		this.target = target;
		this.attachment = attachment;
	}

	@OriginalMember(owner = "client!kw", name = "a", descriptor = "(I)V")
	@Override
	public void detach() {
		OpenGL.glFramebufferRenderbufferEXT(this.target, this.attachment, OpenGLConstants.GL_RENDERBUFFER, 0);
		this.target = -1;
		this.attachment = -1;
	}
	@OriginalMember(owner = "client!cfa", name = "I", descriptor = "[I")
	public static final int[] handleBuffer = new int[1];
}
