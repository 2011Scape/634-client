package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer
import com.jagex.game.runetek5.toolkit.companion.Binding
import com.jagex.game.runetek5.GlRenderTarget
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.client.GlColorAttachments
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!rg")
class GlFramebuffer : GlRenderTarget {

	@OriginalMember(owner = "client!rg", name = "s", descriptor = "I")
	private var width: Int = 0

	@OriginalMember(owner = "client!rg", name = "u", descriptor = "I")
	private var occupiedSlots: Int = 0

	@OriginalMember(owner = "client!rg", name = "w", descriptor = "I")
	private var height: Int = 0

	@OriginalMember(owner = "client!rg", name = "l", descriptor = "[Lclient!pca;")
	private val attachments: Array<Binding?> = arrayOfNulls<Binding>(9)

	@OriginalMember(owner = "client!rg", name = "i", descriptor = "I")
	private var bindFlags: Int = 0

	@OriginalMember(owner = "client!rg", name = "j", descriptor = "I")
	private var bindTarget: Int = -1

	@OriginalMember(owner = "client!rg", name = "t", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!rg", name = "B", descriptor = "I")
	private val handle: Int

	@OriginalMember(owner = "client!rg", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		if (!glToolkit!!.extFramebufferObject) {
			throw IllegalStateException("")
		}
		this.glToolkit = glToolkit
		OpenGL.glGenFramebuffersEXT(1, GlFramebuffer.GEN_FRAMEBUFFER_IDS, 0)
		this.handle = GlFramebuffer.GEN_FRAMEBUFFER_IDS[0]
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(B)V")
	override fun unbindRead() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFE.toInt()
		this.bindTarget = this.getBindTarget()
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IIILclient!q;)V")
	fun attachCubeFace(@OriginalArg(2) faceTarget: Int, @OriginalArg(3) glCubeTexture: GlCubeTexture?) {
		this.bindCubeFaceAttachment(glCubeTexture, faceTarget)
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(I)V")
	override fun bind() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x4
		this.bindTarget = this.getBindTarget()
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(II)V")
	fun clearAttachment(@OriginalArg(1) slot: Int) {
		if (this.attachments[slot] != null) {
			this.attachments[slot]!!.detach()
		}
		this.occupiedSlots = this.occupiedSlots and (0x1 shl slot).inv()
		this.attachments[slot] = null
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IBLclient!q;II)V")
	private fun bindCubeFaceAttachment(@OriginalArg(2) glCubeTexture: GlCubeTexture?, @OriginalArg(4) faceTarget: Int) {
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

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(IB)V")
	fun drawBuffer(@OriginalArg(0) slot: Int) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		OpenGL.glDrawBuffer(GlColorAttachments.COLOR_ATTACHMENTS[slot])
	}

	@OriginalMember(owner = "client!rg", name = "c", descriptor = "(I)V")
	override fun unbind() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFB.toInt()
		this.bindTarget = this.getBindTarget()
	}

	@OriginalMember(owner = "client!rg", name = "h", descriptor = "(I)I")
	private fun getBindTarget(): Int {
		if ((this.bindFlags and 0x4) != 0) {
			return 36160
		} else if ((this.bindFlags and 0x2) == 0) {
			return if ((this.bindFlags and 0x1) == 0) -1 else 36008
		} else {
			return 36009
		}
	}

	@OriginalMember(owner = "client!rg", name = "e", descriptor = "(I)V")
	override fun unbindDraw() {
		// Not part of the original client: the window's framebuffer is the scaled target with client.scale.
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.glToolkit!!.defaultFramebuffer())
		this.bindFlags = this.bindFlags and 0xFFFFFFFD.toInt()
		this.bindTarget = this.getBindTarget()
	}

	@OriginalMember(owner = "client!rg", name = "b", descriptor = "(II)V")
	fun readBuffer() {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		OpenGL.glReadBuffer(GlColorAttachments.COLOR_ATTACHMENTS[0])
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(ILclient!tp;II)V")
	private fun bindTextureAttachment(@OriginalArg(1) texture: OpenGl2DTexture?, @OriginalArg(2) slot: Int) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		@Pc(16) val slotBit: Int = 0x1 shl slot
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

	@OriginalMember(owner = "client!rg", name = "b", descriptor = "(I)V")
	override fun bindDraw() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_DRAW_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x2
		this.bindTarget = this.getBindTarget()
	}

	// Not part of the original client: the GL name of this framebuffer, for the scaled target.
	fun framebufferId(): Int {
		return this.handle
	}

	@OriginalMember(owner = "client!rg", name = "i", descriptor = "(I)Z")
	fun isFramebufferComplete(): Boolean {
		@Pc(7) val status: Int = OpenGL.glCheckFramebufferStatusEXT(this.bindTarget)
		return status == OpenGLConstants.GL_FRAMEBUFFER_COMPLETE
	}

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(ILclient!kw;I)V")
	fun attachRenderbuffer(@OriginalArg(0) slot: Int, @OriginalArg(1) renderbuffer: OpenGlRenderBuffer?) {
		if (this.bindTarget == -1) {
			throw RuntimeException()
		}
		@Pc(15) val slotBit: Int = 0x1 shl slot
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

	@OriginalMember(owner = "client!rg", name = "a", descriptor = "(Lclient!tp;II)V")
	fun attachTexture(@OriginalArg(0) texture: OpenGl2DTexture?, @OriginalArg(2) slot: Int) {
		this.bindTextureAttachment(texture, slot)
	}

	@OriginalMember(owner = "client!rg", name = "d", descriptor = "(I)V")
	override fun bindRead() {
		OpenGL.glBindFramebufferEXT(OpenGLConstants.GL_READ_FRAMEBUFFER, this.handle)
		this.bindFlags = this.bindFlags or 0x1
		this.bindTarget = this.getBindTarget()
	}

	@OriginalMember(owner = "client!rg", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.glToolkit!!.requestFrameBufferCleanup(this.handle)
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	companion object {
		@OriginalMember(owner = "client!mia", name = "e", descriptor = "[I")
		@JvmField
		val GEN_FRAMEBUFFER_IDS: IntArray = IntArray(16)
	}
}
