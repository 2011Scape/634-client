package com.jagex.game.runetek5.graphics

import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.PostProcessEffect

@OriginalClass("client!hh")
class FramebufferCompositor {

	@OriginalMember(owner = "client!hh", name = "t", descriptor = "Z")
	private var isCompositionPending: Boolean = false

	@OriginalMember(owner = "client!hh", name = "A", descriptor = "Lclient!kw;")
	private var hdrRenderBuffer: OpenGlRenderBuffer? = null

	@OriginalMember(owner = "client!hh", name = "C", descriptor = "Lclient!kw;")
	private var renderBuffer: OpenGlRenderBuffer? = null

	@OriginalMember(owner = "client!hh", name = "F", descriptor = "Lclient!tp;")
	private var activeTexture: OpenGl2DTexture? = null

	@OriginalMember(owner = "client!hh", name = "f", descriptor = "I")
	private var t: Int = 1

	@OriginalMember(owner = "client!hh", name = "g", descriptor = "I")
	private var vertexCoordinateY: Int = 0

	@OriginalMember(owner = "client!hh", name = "r", descriptor = "I")
	private var vertexCoordinateX: Int = 0

	@OriginalMember(owner = "client!hh", name = "q", descriptor = "I")
	private var width: Int = 1

	@OriginalMember(owner = "client!hh", name = "w", descriptor = "Lclient!uh;")
	private val effectsList: Deque = Deque()

	@OriginalMember(owner = "client!hh", name = "x", descriptor = "Z")
	private var isDepthBufferDirty: Boolean = true

	@OriginalMember(owner = "client!hh", name = "z", descriptor = "[Lclient!tp;")
	private var textures2d: Array<OpenGl2DTexture?>? = arrayOfNulls<OpenGl2DTexture>(2)

	@OriginalMember(owner = "client!hh", name = "B", descriptor = "Z")
	private var pixelFormatSet: Boolean = true

	@OriginalMember(owner = "client!hh", name = "y", descriptor = "Z")
	private var isFboAttachmentDirty: Boolean = true

	@OriginalMember(owner = "client!hh", name = "D", descriptor = "Z")
	private var isFramebufferDirty: Boolean = true

	@OriginalMember(owner = "client!hh", name = "I", descriptor = "I")
	private var texturedFaceCount: Int = 0

	@OriginalMember(owner = "client!hh", name = "H", descriptor = "I")
	private var s: Int = -1

	@OriginalMember(owner = "client!hh", name = "J", descriptor = "Z")
	private var loadTexture: Boolean = false

	@OriginalMember(owner = "client!hh", name = "u", descriptor = "Lclient!qo;")
	private val activeToolkit: GlToolkit?

	@OriginalMember(owner = "client!hh", name = "l", descriptor = "Lclient!rg;")
	private var resolveDisplay: GlFramebuffer? = null

	@OriginalMember(owner = "client!hh", name = "o", descriptor = "Lclient!rg;")
	private var activeDisplay: GlFramebuffer? = null

	@OriginalMember(owner = "client!hh", name = "p", descriptor = "Lclient!rg;")
	private var multisampleDisplay: GlFramebuffer? = null

	@OriginalMember(owner = "client!hh", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		this.activeToolkit = glToolkit
		if (this.activeToolkit!!.extFramebufferObject && this.activeToolkit.arbRectangleTexturesSupported) {
			this.resolveDisplay = GlFramebuffer(this.activeToolkit)
			this.activeDisplay = this.resolveDisplay
			if (this.activeToolkit.multiSamplesPreference > 1 && this.activeToolkit.framebufferBlit && this.activeToolkit.framebufferMultisample) {
				this.multisampleDisplay = GlFramebuffer(this.activeToolkit)
				this.activeDisplay = this.multisampleDisplay
				return
			}
		}
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(I)V")
	fun compositeEffects() {
		if (!this.isCompositionPending) {
			return
		}
		if (this.multisampleDisplay != null) {
			this.activeToolkit!!.popDrawTarget(this.multisampleDisplay)
			@Pc(20) var blitMask: Short = 16384
			this.activeToolkit.pushDrawTarget(this.resolveDisplay)
			this.multisampleDisplay!!.readBuffer()
			this.resolveDisplay!!.drawBuffer(0)
			if (this.loadTexture) {
				blitMask = 16640
			}
			OpenGL.glBlitFramebufferEXT(0, 0, this.width, this.t, 0, 0, this.width, this.t, blitMask.toInt(), OpenGLConstants.GL_NEAREST)
			this.activeToolkit.popReadTarget(this.multisampleDisplay)
			this.activeToolkit.pushReadTarget(this.resolveDisplay)
		}
		this.activeToolkit!!.setupSpriteRendering()
		this.activeToolkit.setBlendMode(0)
		this.activeToolkit.setColourOp(1)
		this.activeToolkit.la()
		@Pc(97) var textureIndex: Int = 0
		@Pc(99) var colorAttachmentSlot: Int = 1
		@Pc(113) var glPostProcessFilter: PostProcessEffect?
		@Pc(105) var glPostProcessFilter2: PostProcessEffect? = this.effectsList.first() as PostProcessEffect?
		while (glPostProcessFilter2 != null) {
			glPostProcessFilter = this.effectsList.next() as PostProcessEffect?
			@Pc(117) val numTexturedFaces: Int = glPostProcessFilter2.getPassCount()
			@Pc(119) var i: Int = 0
			while (i < numTexturedFaces) {
				glPostProcessFilter2.draw(this.activeTexture, this.textures2d!![textureIndex], i)
				if (glPostProcessFilter == null && i == numTexturedFaces - 1) {
					this.activeToolkit.popTarget(this.resolveDisplay)
					this.activeToolkit.setViewportOffset(0, 0)
					OpenGL.glBegin(OpenGLConstants.GL_QUADS)
					OpenGL.glTexCoord2f(0.0f, this.t.toFloat())
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 1.0f)
					OpenGL.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY)
					OpenGL.glTexCoord2f(0.0f, 0.0f)
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
					OpenGL.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY + this.t)
					OpenGL.glTexCoord2f(this.width.toFloat(), 0.0f)
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 0.0f)
					OpenGL.glVertex2i(this.vertexCoordinateX + this.width, this.vertexCoordinateY + this.t)
					OpenGL.glTexCoord2f(this.width.toFloat(), this.t.toFloat())
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 1.0f)
					OpenGL.glVertex2i(this.width + this.vertexCoordinateX, this.vertexCoordinateY)
					OpenGL.glEnd()
				} else {
					this.resolveDisplay!!.drawBuffer(colorAttachmentSlot)
					OpenGL.glBegin(OpenGLConstants.GL_QUADS)
					OpenGL.glTexCoord2f(0.0f, this.t.toFloat())
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 1.0f)
					OpenGL.glVertex2i(0, 0)
					OpenGL.glTexCoord2f(0.0f, 0.0f)
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
					OpenGL.glVertex2i(0, this.t)
					OpenGL.glTexCoord2f(this.width.toFloat(), 0.0f)
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 0.0f)
					OpenGL.glVertex2i(this.width, this.t)
					OpenGL.glTexCoord2f(this.width.toFloat(), this.t.toFloat())
					OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 1.0f)
					OpenGL.glVertex2i(this.width, 0)
					OpenGL.glEnd()
				}
				glPostProcessFilter2.endPass(i)
				textureIndex = (textureIndex + 1) and 0x1
				colorAttachmentSlot = (colorAttachmentSlot + 1) and 0x1
				i++
			}
			glPostProcessFilter2 = glPostProcessFilter
		}
		this.isCompositionPending = false
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Lclient!fba;Z)Z")
	fun addEffect(@OriginalArg(0) glPostProcessFilter: PostProcessEffect?): Boolean {
		if (this.activeDisplay != null) {
			if (glPostProcessFilter!!.isLoaded() || glPostProcessFilter.createShaders()) {
				this.effectsList.addLast(glPostProcessFilter)
				this.recalculateFormat()
				if (this.rebuildFramebuffers()) {
					if (this.width != -1 && this.t != -1) {
						glPostProcessFilter.resize(this.t, this.width)
					}
					glPostProcessFilter.isActive = true
					return true
				}
			}
			this.removeEffect(glPostProcessFilter)
		}
		return false
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(IIIII)Z")
	fun prepareForRendering(@OriginalArg(0) width: Int, @OriginalArg(1) x: Int, @OriginalArg(2) height: Int, @OriginalArg(3) y: Int): Boolean {
		if (this.activeDisplay == null || this.effectsList.isEmpty()) {
			return false
		}
		if (width != this.width || height != this.t) {
			this.width = width
			this.t = height
			@Pc(45) var node: Node? = this.effectsList.first()
			while (node !== this.effectsList.sentinel) {
				(node as PostProcessEffect).resize(this.t, this.width)
				node = node.next
			}
			this.pixelFormatSet = true
			this.isDepthBufferDirty = true
			this.isFboAttachmentDirty = true
		}
		if (!this.rebuildFramebuffers()) {
			return false
		}
		this.isCompositionPending = true
		this.vertexCoordinateY = y
		this.vertexCoordinateX = x
		this.activeToolkit!!.pushTarget(this.activeDisplay)
		this.activeDisplay!!.drawBuffer(0)
		this.activeToolkit.setViewportOffset(this.t + this.vertexCoordinateY - this.activeToolkit.viewportHeight, -this.vertexCoordinateX)
		return true
	}

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(Z)Z")
	private fun rebuildFramebuffers(): Boolean {
		if (this.isDepthBufferDirty) {
			if (this.renderBuffer != null) {
				this.renderBuffer!!.dispose()
				this.renderBuffer = null
			}
			if (this.activeTexture != null) {
				this.activeTexture!!.delete()
				this.activeTexture = null
			}
			if (this.multisampleDisplay != null) {
				this.renderBuffer = OpenGlRenderBuffer(this.activeToolkit, 6402, this.width, this.t, this.activeToolkit!!.multiSamplesPreference)
			}
			if (this.loadTexture) {
				this.activeTexture = OpenGl2DTexture(this.activeToolkit, 34037, 6402, this.width, this.t)
			} else if (this.renderBuffer == null) {
				this.renderBuffer = OpenGlRenderBuffer(this.activeToolkit, 6402, this.width, this.t)
			}
			this.isDepthBufferDirty = false
			this.isFboAttachmentDirty = true
			this.isFramebufferDirty = true
		}
		if (this.pixelFormatSet) {
			if (this.hdrRenderBuffer != null) {
				this.hdrRenderBuffer!!.dispose()
				this.hdrRenderBuffer = null
			}
			if (this.textures2d!![0] != null) {
				this.textures2d!![0]!!.delete()
				this.textures2d!![0] = null
			}
			if (this.textures2d!![1] != null) {
				this.textures2d!![1]!!.delete()
				this.textures2d!![1] = null
			}
			if (this.multisampleDisplay != null) {
				this.hdrRenderBuffer = OpenGlRenderBuffer(this.activeToolkit, this.s, this.width, this.t, this.activeToolkit!!.multiSamplesPreference)
			}
			this.textures2d!![0] = OpenGl2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t)
			this.textures2d!![1] = if (this.texturedFaceCount > 1) OpenGl2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t) else null
			this.isFboAttachmentDirty = true
			this.isFramebufferDirty = true
			this.pixelFormatSet = false
		}
		if (this.isFboAttachmentDirty) {
			if (this.multisampleDisplay == null) {
				this.activeToolkit!!.pushTarget(this.resolveDisplay)
				this.resolveDisplay!!.clearAttachment(0)
				this.resolveDisplay!!.clearAttachment(1)
				this.resolveDisplay!!.clearAttachment(8)
				this.resolveDisplay!!.attachTexture(this.textures2d!![0], 0)
				if (this.texturedFaceCount > 1) {
					this.resolveDisplay!!.attachTexture(this.textures2d!![1], 1)
				}
				if (this.loadTexture) {
					this.resolveDisplay!!.attachTexture(this.activeTexture, 8)
				} else {
					this.resolveDisplay!!.attachRenderbuffer(8, this.renderBuffer)
				}
				this.activeToolkit.popTarget(this.resolveDisplay)
			} else {
				this.activeToolkit!!.pushTarget(this.resolveDisplay)
				this.resolveDisplay!!.clearAttachment(0)
				this.resolveDisplay!!.clearAttachment(1)
				this.resolveDisplay!!.clearAttachment(8)
				this.resolveDisplay!!.attachTexture(this.textures2d!![0], 0)
				if (this.texturedFaceCount > 1) {
					this.resolveDisplay!!.attachTexture(this.textures2d!![1], 1)
				}
				if (this.loadTexture) {
					this.resolveDisplay!!.attachTexture(this.activeTexture, 8)
				}
				this.activeToolkit.popTarget(this.resolveDisplay)
				this.activeToolkit.pushTarget(this.multisampleDisplay)
				this.multisampleDisplay!!.clearAttachment(0)
				this.multisampleDisplay!!.clearAttachment(8)
				this.multisampleDisplay!!.attachRenderbuffer(0, this.hdrRenderBuffer)
				this.multisampleDisplay!!.attachRenderbuffer(8, this.renderBuffer)
				this.activeToolkit.popTarget(this.multisampleDisplay)
			}
			this.isFboAttachmentDirty = false
			this.isFramebufferDirty = true
		}
		if (this.isFramebufferDirty) {
			this.activeToolkit!!.pushTarget(this.activeDisplay)
			this.isFramebufferDirty = !this.activeDisplay!!.isFramebufferComplete()
			this.activeToolkit.popTarget(this.activeDisplay)
		}
		return !this.isFramebufferDirty
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(Lclient!fba;Z)V")
	fun removeEffect(@OriginalArg(0) glPostProcessFilter: PostProcessEffect?) {
		glPostProcessFilter!!.isActive = false
		glPostProcessFilter.destroy()
		glPostProcessFilter.unlink()
		this.recalculateFormat()
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(B)V")
	fun shutdown() {
		this.hdrRenderBuffer = null
		this.resolveDisplay = null
		this.multisampleDisplay = this.resolveDisplay
		this.activeDisplay = this.multisampleDisplay
		this.activeTexture = null
		this.renderBuffer = null
		this.textures2d = null
		if (!this.effectsList.isEmpty()) {
			@Pc(44) var node: Node? = this.effectsList.first()
			while (node !== this.effectsList.sentinel) {
				(node as PostProcessEffect).destroy()
				node = node.next
			}
		}
		this.t = 1
		this.width = this.t
	}

	@OriginalMember(owner = "client!hh", name = "b", descriptor = "(I)Z")
	fun isDisplayAvailable(): Boolean {
		return this.activeDisplay != null
	}

	@OriginalMember(owner = "client!hh", name = "c", descriptor = "(B)V")
	private fun recalculateFormat() {
		@Pc(7) var loadTexture: Boolean = false
		@Pc(9) var maxFormat: Int = 0
		@Pc(11) var texturedFaceCount: Int = 0
		@Pc(31) var local31: Int
		@Pc(17) var glPostProcessFilter: PostProcessEffect? = this.effectsList.first() as PostProcessEffect?
		while (glPostProcessFilter != null) {
			val effectFormat: Int = glPostProcessFilter.getFormat()
			texturedFaceCount += glPostProcessFilter.getPassCount()
			if (effectFormat > maxFormat) {
				maxFormat = effectFormat
			}
			loadTexture = loadTexture or glPostProcessFilter.requiresDepth()
			glPostProcessFilter = this.effectsList.next() as PostProcessEffect?
		}
		val internalFormat: Int
		if (maxFormat == 2) {
			internalFormat = 34836
		} else if (maxFormat == 1) {
			internalFormat = 34842
		} else {
			internalFormat = 6408
		}
		if (this.s != internalFormat) {
			this.pixelFormatSet = true
			this.s = internalFormat
		}
		@Pc(99) val currFaces: Int = if (this.texturedFaceCount > 2) 2 else this.texturedFaceCount
		@Pc(106) val newFaces: Int = if (texturedFaceCount <= 2) texturedFaceCount else 2
		this.texturedFaceCount = texturedFaceCount
		if (loadTexture != this.loadTexture) {
			this.loadTexture = loadTexture
			this.isDepthBufferDirty = true
		}
		if (newFaces != currFaces) {
			this.pixelFormatSet = true
			this.isFboAttachmentDirty = this.pixelFormatSet
		}
	}
}
