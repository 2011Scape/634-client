/*
 * Not part of the original client: 634HD (toolkit 6), copied from FramebufferCompositor (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdFramebufferCompositor {

	private var isCompositionPending: Boolean = false

	private var hdrRenderBuffer: HdRenderBuffer? = null

	private var renderBuffer: HdRenderBuffer? = null

	private var activeTexture: Hd2DTexture? = null

	private var t: Int = 1

	private var vertexCoordinateY: Int = 0

	private var vertexCoordinateX: Int = 0

	private var width: Int = 1

	private val effectsList: Deque = Deque()

	private var isDepthBufferDirty: Boolean = true

	private var textures2d: Array<Hd2DTexture?>? = arrayOfNulls<Hd2DTexture>(2)

	private var pixelFormatSet: Boolean = true

	private var isFboAttachmentDirty: Boolean = true

	private var isFramebufferDirty: Boolean = true

	private var texturedFaceCount: Int = 0

	private var s: Int = -1

	private var loadTexture: Boolean = false

	private val activeToolkit: HdToolkit?

	private var resolveDisplay: HdFramebuffer? = null

	private var activeDisplay: HdFramebuffer? = null

	private var multisampleDisplay: HdFramebuffer? = null

	constructor(glToolkit: HdToolkit?) {
		this.activeToolkit = glToolkit
		if (this.activeToolkit!!.extFramebufferObject && this.activeToolkit.arbRectangleTexturesSupported) {
			this.resolveDisplay = HdFramebuffer(this.activeToolkit)
			this.activeDisplay = this.resolveDisplay
			if (this.activeToolkit.multiSamplesPreference > 1 && this.activeToolkit.framebufferBlit && this.activeToolkit.framebufferMultisample) {
				this.multisampleDisplay = HdFramebuffer(this.activeToolkit)
				this.activeDisplay = this.multisampleDisplay
				return
			}
		}
	}

	fun compositeEffects() {
		if (!this.isCompositionPending) {
			return
		}
		if (this.multisampleDisplay != null) {
			this.activeToolkit!!.popDrawTarget(this.multisampleDisplay)
			var blitMask: Short = 16384
			this.activeToolkit.pushDrawTarget(this.resolveDisplay)
			this.multisampleDisplay!!.readBuffer()
			this.resolveDisplay!!.drawBuffer(0)
			if (this.loadTexture) {
				blitMask = 16640
			}
			HdGl.glBlitFramebufferEXT(0, 0, this.width, this.t, 0, 0, this.width, this.t, blitMask.toInt(), OpenGLConstants.GL_NEAREST)
			this.activeToolkit.popReadTarget(this.multisampleDisplay)
			this.activeToolkit.pushReadTarget(this.resolveDisplay)
		}
		this.activeToolkit!!.setupSpriteRendering()
		this.activeToolkit.setBlendMode(0)
		this.activeToolkit.setColourOp(1)
		this.activeToolkit.la()
		var textureIndex: Int = 0
		var colorAttachmentSlot: Int = 1
		var glPostProcessFilter: HdPostProcessEffect?
		var glPostProcessFilter2: HdPostProcessEffect? = this.effectsList.first() as HdPostProcessEffect?
		while (glPostProcessFilter2 != null) {
			glPostProcessFilter = this.effectsList.next() as HdPostProcessEffect?
			val numTexturedFaces: Int = glPostProcessFilter2.getPassCount()
			var i: Int = 0
			while (i < numTexturedFaces) {
				glPostProcessFilter2.draw(this.activeTexture, this.textures2d!![textureIndex], i)
				if (glPostProcessFilter == null && i == numTexturedFaces - 1) {
					this.activeToolkit.popTarget(this.resolveDisplay)
					this.activeToolkit.setViewportOffset(0, 0)
					HdGl.glBegin(OpenGLConstants.GL_QUADS)
					HdGl.glTexCoord2f(0.0f, this.t.toFloat())
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 1.0f)
					HdGl.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY)
					HdGl.glTexCoord2f(0.0f, 0.0f)
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
					HdGl.glVertex2i(this.vertexCoordinateX, this.vertexCoordinateY + this.t)
					HdGl.glTexCoord2f(this.width.toFloat(), 0.0f)
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 0.0f)
					HdGl.glVertex2i(this.vertexCoordinateX + this.width, this.vertexCoordinateY + this.t)
					HdGl.glTexCoord2f(this.width.toFloat(), this.t.toFloat())
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 1.0f)
					HdGl.glVertex2i(this.width + this.vertexCoordinateX, this.vertexCoordinateY)
					HdGl.glEnd()
				} else {
					this.resolveDisplay!!.drawBuffer(colorAttachmentSlot)
					HdGl.glBegin(OpenGLConstants.GL_QUADS)
					HdGl.glTexCoord2f(0.0f, this.t.toFloat())
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 1.0f)
					HdGl.glVertex2i(0, 0)
					HdGl.glTexCoord2f(0.0f, 0.0f)
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
					HdGl.glVertex2i(0, this.t)
					HdGl.glTexCoord2f(this.width.toFloat(), 0.0f)
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 0.0f)
					HdGl.glVertex2i(this.width, this.t)
					HdGl.glTexCoord2f(this.width.toFloat(), this.t.toFloat())
					HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 1.0f, 1.0f)
					HdGl.glVertex2i(this.width, 0)
					HdGl.glEnd()
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

	fun addEffect(glPostProcessFilter: HdPostProcessEffect?): Boolean {
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

	fun prepareForRendering(width: Int, x: Int, height: Int, y: Int): Boolean {
		if (this.activeDisplay == null || this.effectsList.isEmpty()) {
			return false
		}
		if (width != this.width || height != this.t) {
			this.width = width
			this.t = height
			var node: Node? = this.effectsList.first()
			while (node !== this.effectsList.sentinel) {
				(node as HdPostProcessEffect).resize(this.t, this.width)
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
				this.renderBuffer = HdRenderBuffer(this.activeToolkit, 6402, this.width, this.t, this.activeToolkit!!.multiSamplesPreference)
			}
			if (this.loadTexture) {
				this.activeTexture = Hd2DTexture(this.activeToolkit, 34037, 6402, this.width, this.t)
			} else if (this.renderBuffer == null) {
				this.renderBuffer = HdRenderBuffer(this.activeToolkit, 6402, this.width, this.t)
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
				this.hdrRenderBuffer = HdRenderBuffer(this.activeToolkit, this.s, this.width, this.t, this.activeToolkit!!.multiSamplesPreference)
			}
			this.textures2d!![0] = Hd2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t)
			this.textures2d!![1] = if (this.texturedFaceCount > 1) Hd2DTexture(this.activeToolkit, 34037, this.s, this.width, this.t) else null
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

	fun removeEffect(glPostProcessFilter: HdPostProcessEffect?) {
		glPostProcessFilter!!.isActive = false
		glPostProcessFilter.destroy()
		glPostProcessFilter.unlink()
		this.recalculateFormat()
	}

	fun shutdown() {
		this.hdrRenderBuffer = null
		this.resolveDisplay = null
		this.multisampleDisplay = this.resolveDisplay
		this.activeDisplay = this.multisampleDisplay
		this.activeTexture = null
		this.renderBuffer = null
		this.textures2d = null
		if (!this.effectsList.isEmpty()) {
			var node: Node? = this.effectsList.first()
			while (node !== this.effectsList.sentinel) {
				(node as HdPostProcessEffect).destroy()
				node = node.next
			}
		}
		this.t = 1
		this.width = this.t
	}

	fun isDisplayAvailable(): Boolean {
		return this.activeDisplay != null
	}

	private fun recalculateFormat() {
		var loadTexture: Boolean = false
		var maxFormat: Int = 0
		var texturedFaceCount: Int = 0
		var local31: Int
		var glPostProcessFilter: HdPostProcessEffect? = this.effectsList.first() as HdPostProcessEffect?
		while (glPostProcessFilter != null) {
			val effectFormat: Int = glPostProcessFilter.getFormat()
			texturedFaceCount += glPostProcessFilter.getPassCount()
			if (effectFormat > maxFormat) {
				maxFormat = effectFormat
			}
			loadTexture = loadTexture or glPostProcessFilter.requiresDepth()
			glPostProcessFilter = this.effectsList.next() as HdPostProcessEffect?
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
		val currFaces: Int = if (this.texturedFaceCount > 2) 2 else this.texturedFaceCount
		val newFaces: Int = if (texturedFaceCount <= 2) texturedFaceCount else 2
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
