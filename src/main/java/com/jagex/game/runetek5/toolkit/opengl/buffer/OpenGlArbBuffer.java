package com.jagex.game.runetek5.toolkit.opengl.buffer;

import jaclib.memory.Buffer;
import jaclib.memory.Source;
import jaclib.memory.heap.NativeHeapBuffer;
import jaggl.MapBuffer;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Identifiable;
import com.jagex.game.runetek5.toolkit.GlxToolkit;

@OriginalClass("client!vi")
public abstract class OpenGlArbBuffer implements Identifiable {

	@OriginalMember(owner = "client!vi", name = "e", descriptor = "I")
	private int initialCapacity;

	@OriginalMember(owner = "client!vi", name = "m", descriptor = "I")
	private int capacity;

	@OriginalMember(owner = "client!vi", name = "n", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private NativeHeapBuffer nativeheapBuffer;

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "I")
	private int mapState = 0;

	@OriginalMember(owner = "client!vi", name = "l", descriptor = "I")
	private int activeHandle = -1;

	@OriginalMember(owner = "client!vi", name = "y", descriptor = "Lclient!bga;")
	public final GlxToolkit nativeToolkit;

	@OriginalMember(owner = "client!vi", name = "u", descriptor = "I")
	private final int bufferTarget;

	@OriginalMember(owner = "client!vi", name = "p", descriptor = "Z")
	private final boolean drawStream;

	@OriginalMember(owner = "client!vi", name = "<init>", descriptor = "(Lclient!bga;IZ)V")
	public OpenGlArbBuffer(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) int bufferTarget, @OriginalArg(2) boolean drawStream) {
		this.nativeToolkit = glxToolkit;
		this.bufferTarget = bufferTarget;
		this.drawStream = drawStream;
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(ILclient!jaggl/MapBuffer;Z)Lclient!jaclib/memory/Buffer;")
	public final Buffer map(@OriginalArg(1) MapBuffer mapBuffer) {
		if (this.mapState == 0) {
			this.initialize();
			if (this.activeHandle <= 0) {
				this.mapState = 2;
				return this.nativeheapBuffer;
			}
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
			OpenGL.glBufferDataARBub(this.bufferTarget, this.initialCapacity, null, 0, this.drawStream ? 35040 : 35044);
			if (this.capacity <= this.nativeToolkit.stagingBuffer.b) {
				this.mapState = 1;
				return this.nativeToolkit.stagingBuffer;
			}
			if (!mapBuffer.a() && mapBuffer.a(this.bufferTarget, this.capacity, 35001)) {
				this.mapState = 2;
				return mapBuffer;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(ILclient!jaclib/memory/Source;I)Z")
	public final boolean createFromSource(@OriginalArg(1) Source source, @OriginalArg(2) int size) {
		if (size > this.initialCapacity) {
			this.initialize();
			if (this.activeHandle <= 0) {
				throw new RuntimeException("ARGH!");
			}
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
			OpenGL.glBufferDataARBa(this.bufferTarget, size, source.getAddress(), this.drawStream ? 35040 : 35044);
			this.nativeToolkit.textureBuffersSizeOccupied += size - this.capacity;
			this.initialCapacity = size;
		} else if (this.activeHandle > 0) {
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
			OpenGL.glBufferSubDataARBa(this.bufferTarget, 0, this.capacity, source.getAddress());
			this.nativeToolkit.textureBuffersSizeOccupied += size - this.capacity;
		} else {
			throw new RuntimeException("ARGH!");
		}
		this.capacity = size;
		return true;
	}

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "(I)V")
	public void deallocate() {
		if (this.activeHandle > 0) {
			this.nativeToolkit.requestTextureBufferDeletion(this.capacity, this.activeHandle);
			this.activeHandle = -1;
		}
	}

	@OriginalMember(owner = "client!vi", name = "c", descriptor = "(I)J")
	public final long getBufferAddress() {
		return this.activeHandle == 0 ? this.nativeheapBuffer.getAddress() : 0L;
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(Lclient!jaggl/MapBuffer;I)Z")
	public final boolean unmap(@OriginalArg(0) MapBuffer mapBuffer) {
		@Pc(5) boolean success = true;
		if (this.mapState != 0) {
			if (this.activeHandle > 0) {
				OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
				if (this.mapState == 1) {
					OpenGL.glBufferSubDataARBa(this.bufferTarget, 0, this.initialCapacity, this.nativeToolkit.stagingBuffer.getAddress());
				} else {
					success = mapBuffer.b();
				}
			}
			this.mapState = 0;
		}
		return success;
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(II)V")
	public void create(@OriginalArg(1) int size) {
		if (this.initialCapacity < size) {
			this.initialize();
			if (this.activeHandle <= 0) {
				this.nativeheapBuffer = this.nativeToolkit.generateNativeHeapBuffer(false, size);
			} else {
				OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
				OpenGL.glBufferDataARBub(this.bufferTarget, size, null, 0, this.drawStream ? 35040 : 35044);
				this.nativeToolkit.textureBuffersSizeOccupied += size - this.initialCapacity;
			}
			this.initialCapacity = size;
		}
		this.capacity = size;
	}

	@OriginalMember(owner = "client!vi", name = "e", descriptor = "(I)V")
	public final void bind() {
		if (this.nativeToolkit.GLARBVertexBufferObject) {
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
		}
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(Z)I")
	@Override
	public int getId() {
		return this.capacity;
	}

	@OriginalMember(owner = "client!vi", name = "finalize", descriptor = "()V")
	@Override
	public final void finalize() throws Throwable {
		this.deallocate();
		super.finalize();
	}

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "(B)V")
	private void initialize() {
		if (this.activeHandle >= 0) {
			return;
		}
		if (this.nativeToolkit.GLARBVertexBufferObject) {
			OpenGL.glGenBuffersARB(1, OpenGlArbBuffer.HANDLE_BUFFER, 0);
			this.activeHandle = OpenGlArbBuffer.HANDLE_BUFFER[0];
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle);
		} else {
			this.activeHandle = 0;
		}
	}
	@OriginalMember(owner = "client!ufa", name = "e", descriptor = "[I")
	public static final int[] HANDLE_BUFFER = new int[1];
}
