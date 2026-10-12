package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.companion.Binding

@OriginalClass("client!kw")
class OpenGlRenderBuffer : CacheableNode, Binding {

	@OriginalMember(owner = "client!kw", name = "x", descriptor = "I")
	private var target: Int = -1

	@OriginalMember(owner = "client!kw", name = "y", descriptor = "I")
	private var attachment: Int = -1

	@OriginalMember(owner = "client!kw", name = "H", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!kw", name = "z", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!kw", name = "A", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!kw", name = "C", descriptor = "I")
	private val format: Int

	@OriginalMember(owner = "client!kw", name = "E", descriptor = "I")
	private var handle: Int = 0

	@OriginalMember(owner = "client!kw", name = "G", descriptor = "I")
	private val bufferSizeBytes: Int

	@OriginalMember(owner = "client!kw", name = "<init>", descriptor = "(Lclient!qo;III)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) format: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
		this.width = width
		this.glToolkit = glToolkit
		this.height = height
		this.format = format
		OpenGL.glGenRenderbuffersEXT(1, OpenGlRenderBuffer.handleBuffer, 0)
		this.handle = OpenGlRenderBuffer.handleBuffer[0]
		OpenGL.glBindRenderbufferEXT(36161, this.handle)
		OpenGL.glRenderbufferStorageEXT(36161, this.format, this.width, this.height)
		this.bufferSizeBytes = this.height * this.width * this.glToolkit!!.getBytesPerPixel(this.format)
	}

	@OriginalMember(owner = "client!kw", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) format: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) samples: Int) {
		this.width = width
		this.format = format
		this.height = height
		this.glToolkit = glToolkit
		OpenGL.glGenRenderbuffersEXT(1, OpenGlRenderBuffer.handleBuffer, 0)
		this.handle = OpenGlRenderBuffer.handleBuffer[0]
		OpenGL.glBindRenderbufferEXT(36161, this.handle)
		OpenGL.glRenderbufferStorageMultisampleEXT(36161, samples, this.format, this.width, this.height)
		this.bufferSizeBytes = this.height * this.width * this.glToolkit!!.getBytesPerPixel(this.format)
	}

	@OriginalMember(owner = "client!kw", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.dispose()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	@OriginalMember(owner = "client!kw", name = "d", descriptor = "(I)V")
	fun dispose() {
		if (this.handle > 0) {
			this.glToolkit!!.requestRenderBufferCleanup(this.handle, this.bufferSizeBytes)
			this.handle = 0
		}
	}

	@OriginalMember(owner = "client!kw", name = "b", descriptor = "(III)V")
	fun attach(@OriginalArg(1) attachment: Int, @OriginalArg(2) target: Int) {
		OpenGL.glFramebufferRenderbufferEXT(target, attachment, OpenGLConstants.GL_RENDERBUFFER, this.handle)
		this.target = target
		this.attachment = attachment
	}

	@OriginalMember(owner = "client!kw", name = "a", descriptor = "(I)V")
	override fun detach() {
		OpenGL.glFramebufferRenderbufferEXT(this.target, this.attachment, OpenGLConstants.GL_RENDERBUFFER, 0)
		this.target = -1
		this.attachment = -1
	}

	companion object {
		@OriginalMember(owner = "client!cfa", name = "I", descriptor = "[I")
		@JvmField
		val handleBuffer: IntArray = IntArray(1)
	}
}
