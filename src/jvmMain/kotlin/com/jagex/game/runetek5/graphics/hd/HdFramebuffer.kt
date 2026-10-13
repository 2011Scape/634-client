/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlFramebuffer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.toolkit.companion.Binding
import com.jagex.game.runetek5.GlRenderTarget
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.client.GlColorAttachments
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdFramebuffer : GlRenderTarget {

	private var width: Int = 0

	private var occupiedSlots: Int = 0

	private var height: Int = 0

	private val attachments: Array<Binding?> = arrayOfNulls<Binding>(9)

	private var bindFlags: Int = 0

	private var bindTarget: Int = -1

	private val glToolkit: HdToolkit?

	private val handle: Int

	constructor(glToolkit: HdToolkit?) {
		if (!glToolkit!!.extFramebufferObject) {
			throw IllegalStateException("")
		}
		this.glToolkit = glToolkit
		HdGl.glGenFramebuffersEXT(1, HdFramebuffer.GEN_FRAMEBUFFER_IDS, 0)
		this.handle = HdFramebuffer.GEN_FRAMEBUFFER_IDS[0]
	}

	override fun unbindRead() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFE.toInt()
		this.bindTarget = this.getBindTarget()
	}

	fun attachCubeFace(faceTarget: Int, glCubeTexture: HdCubeTexture?) {
		this.bindCubeFaceAttachment(glCubeTexture, faceTarget)
	}

	override fun bind() {
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x4
		this.bindTarget = this.getBindTarget()
	}

	fun clearAttachment(slot: Int) {
		if (this.attachments[slot] != null) {
			this.attachments[slot]!!.detach()
		}
		this.occupiedSlots = this.occupiedSlots and (0x1 shl slot).inv()
		this.attachments[slot] = null
	}

	private fun bindCubeFaceAttachment(glCubeTexture: HdCubeTexture?, faceTarget: Int) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		if ((this.occupiedSlots and 0xFFFFFFFE.toInt()) == 0) {
			this.width = glCubeTexture!!.size
			this.height = glCubeTexture.size
		} else if (glCubeTexture!!.size != this.width || this.height != glCubeTexture.size) {
			throw RuntimeException()
		}
		glCubeTexture.attachToFramebuffer(this.bindTarget, faceTarget, GlColorAttachments.COLOR_ATTACHMENTS[0])
		this.attachments[0] = glCubeTexture
		this.occupiedSlots = this.occupiedSlots or 0x1
	}

	fun drawBuffer(slot: Int) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		HdGl.glDrawBuffer(GlColorAttachments.COLOR_ATTACHMENTS[slot])
	}

	override fun unbind() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFB.toInt()
		this.bindTarget = this.getBindTarget()
	}

	private fun getBindTarget(): Int {
		if ((this.bindFlags and 0x4) != 0) {
			return 36160
		} else if ((this.bindFlags and 0x2) == 0) {
			return if ((this.bindFlags and 0x1) == 0) -1 else 36008
		} else {
			return 36009
		}
	}

	override fun unbindDraw() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFD.toInt()
		this.bindTarget = this.getBindTarget()
	}

	fun readBuffer() {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		HdGl.glReadBuffer(GlColorAttachments.COLOR_ATTACHMENTS[0])
	}

	private fun bindTextureAttachment(texture: Hd2DTexture?, slot: Int) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		val slotBit: Int = 0x1 shl slot
		if ((slotBit.inv() and this.occupiedSlots) == 0) {
			this.width = texture!!.width
			this.height = texture.length
		} else if (texture!!.width != this.width || texture.length != this.height) {
			throw RuntimeException()
		}
		texture.attachToFramebuffer(this.bindTarget, GlColorAttachments.COLOR_ATTACHMENTS[slot])
		this.attachments[slot] = texture
		this.occupiedSlots = this.occupiedSlots or slotBit
	}

	override fun bindDraw() {
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x2
		this.bindTarget = this.getBindTarget()
	}

	// Not part of the original client: the GL name of this framebuffer, for the scaled target.
	fun framebufferId(): Int {
		return this.handle
	}

	fun isFramebufferComplete(): Boolean {
		val status: Int = HdGl.glCheckFramebufferStatusEXT(this.bindTarget)
		return status == OpenGLConstants.GL_FRAMEBUFFER_COMPLETE
	}

	fun attachRenderbuffer(slot: Int, renderbuffer: HdRenderBuffer?) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		val slotBit: Int = 0x1 shl slot
		if ((this.occupiedSlots and slotBit.inv()) == 0) {
			this.height = renderbuffer!!.height
			this.width = renderbuffer.width
		} else if (this.width != renderbuffer!!.width || this.height != renderbuffer.height) {
			throw RuntimeException()
		}
		renderbuffer.attach(GlColorAttachments.COLOR_ATTACHMENTS[slot], this.bindTarget)
		this.attachments[slot] = renderbuffer
		this.occupiedSlots = this.occupiedSlots or slotBit
	}

	fun attachTexture(texture: Hd2DTexture?, slot: Int) {
		this.bindTextureAttachment(texture, slot)
	}

	override fun bindRead() {
		HdGl.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x1
		this.bindTarget = this.getBindTarget()
	}

	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestFrameBufferCleanup(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@JvmField
		val GEN_FRAMEBUFFER_IDS: IntArray = IntArray(16)
	}
}
