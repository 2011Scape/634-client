/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGlRenderBuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.lru.CacheableNode

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.toolkit.companion.Binding
import kotlin.jvm.JvmField

class HdRenderBuffer : CacheableNode, Binding {

	private var target: Int = -1

	private var attachment: Int = -1

	@JvmField
	val width: Int

	private val glToolkit: HdToolkit?

	@JvmField
	val height: Int

	private val format: Int

	private var handle: Int = 0

	private val bufferSizeBytes: Int

	constructor(glToolkit: HdToolkit?, format: Int, width: Int, height: Int) {
		this.width = width
		this.glToolkit = glToolkit
		this.height = height
		this.format = format
		HdGl.glGenRenderbuffersEXT(1, HdRenderBuffer.handleBuffer, 0)
		this.handle = HdRenderBuffer.handleBuffer[0]
		HdGl.glBindRenderbufferEXT(36161, this.handle)
		HdGl.glRenderbufferStorageEXT(36161, this.format, this.width, this.height)
		this.bufferSizeBytes = this.height * this.width * this.glToolkit!!.getBytesPerPixel(this.format)
	}

	constructor(glToolkit: HdToolkit?, format: Int, width: Int, height: Int, samples: Int) {
		this.width = width
		this.format = format
		this.height = height
		this.glToolkit = glToolkit
		HdGl.glGenRenderbuffersEXT(1, HdRenderBuffer.handleBuffer, 0)
		this.handle = HdRenderBuffer.handleBuffer[0]
		HdGl.glBindRenderbufferEXT(36161, this.handle)
		HdGl.glRenderbufferStorageMultisampleEXT(36161, samples, this.format, this.width, this.height)
		this.bufferSizeBytes = this.height * this.width * this.glToolkit!!.getBytesPerPixel(this.format)
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.dispose()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	fun dispose() {
		if (this.handle > 0) {
			this.glToolkit!!.requestRenderBufferCleanup(this.handle, this.bufferSizeBytes)
			this.handle = 0
		}
	}

	fun attach(attachment: Int, target: Int) {
		HdGl.glFramebufferRenderbufferEXT(target, attachment, OpenGLConstants.GL_RENDERBUFFER, this.handle)
		this.target = target
		this.attachment = attachment
	}

	override fun detach() {
		HdGl.glFramebufferRenderbufferEXT(this.target, this.attachment, OpenGLConstants.GL_RENDERBUFFER, 0)
		this.target = -1
		this.attachment = -1
	}

	companion object {
		@JvmField
		val handleBuffer: IntArray = IntArray(1)
	}
}
