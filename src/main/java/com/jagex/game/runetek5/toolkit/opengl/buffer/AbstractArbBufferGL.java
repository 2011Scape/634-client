package com.jagex.game.runetek5.toolkit.opengl.buffer;

import jaclib.memory.Buffer;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!bi")
public abstract class AbstractArbBufferGL {

	@OriginalMember(owner = "client!bi", name = "c", descriptor = "Z")
	private final boolean streamDraw;

	@OriginalMember(owner = "client!bi", name = "g", descriptor = "Lclient!qo;")
	public final GlToolkit toolkit;

	@OriginalMember(owner = "client!bi", name = "j", descriptor = "I")
	private final int bufferTarget;

	@OriginalMember(owner = "client!bi", name = "a", descriptor = "I")
	private int bufferSize;

	@OriginalMember(owner = "client!bi", name = "i", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!bi", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	public AbstractArbBufferGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int bufferTarget, @OriginalArg(2) byte[] data, @OriginalArg(3) int bufferSize, @OriginalArg(4) boolean streamDraw) {
		this.streamDraw = streamDraw;
		this.toolkit = glToolkit;
		this.bufferTarget = bufferTarget;
		this.bufferSize = bufferSize;
		OpenGL.glGenBuffersARB(1, AbstractArbBufferGL.HANDLE, 0);
		this.id = AbstractArbBufferGL.HANDLE[0];
		this.setAsActiveBuffer();
		OpenGL.glBufferDataARBub(bufferTarget, this.bufferSize, data, 0, this.streamDraw ? 35040 : 35044);
		this.toolkit.vboBuffersSize += this.bufferSize;
	}

	@OriginalMember(owner = "client!bi", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;IZ)V")
	public AbstractArbBufferGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int bufferTarget, @OriginalArg(2) Buffer buffer, @OriginalArg(3) int bufferSize, @OriginalArg(4) boolean streamDraw) {
		this.bufferTarget = bufferTarget;
		this.bufferSize = bufferSize;
		this.toolkit = glToolkit;
		this.streamDraw = streamDraw;
		OpenGL.glGenBuffersARB(1, AbstractArbBufferGL.HANDLE, 0);
		this.id = AbstractArbBufferGL.HANDLE[0];
		this.setAsActiveBuffer();
		OpenGL.glBufferDataARBa(bufferTarget, this.bufferSize, buffer.getAddress(), this.streamDraw ? 35040 : 35044);
		this.toolkit.vboBuffersSize += this.bufferSize;
	}

	@OriginalMember(owner = "client!bi", name = "a", descriptor = "([BII)V")
	public final void put(@OriginalArg(0) byte[] data, @OriginalArg(2) int bufferSize) {
		this.setAsActiveBuffer();
		if (this.bufferSize >= bufferSize) {
			OpenGL.glBufferSubDataARBub(this.bufferTarget, 0, bufferSize, data, 0);
		} else {
			OpenGL.glBufferDataARBub(this.bufferTarget, bufferSize, data, 0, this.streamDraw ? 35040 : 35044);
			this.toolkit.vboBuffersSize += bufferSize - this.bufferSize;
			this.bufferSize = bufferSize;
		}
	}

	@OriginalMember(owner = "client!bi", name = "finalize", descriptor = "()V")
	@Override
	public final void finalize() throws Throwable {
		this.toolkit.requestArbBufferCleanup(this.bufferSize, this.id);
		super.finalize();
	}

	@OriginalMember(owner = "client!bi", name = "d", descriptor = "(I)V")
	public abstract void setAsActiveBuffer();
	@OriginalMember(owner = "client!fc", name = "S", descriptor = "[I")
	public static final int[] HANDLE = new int[1];
}
