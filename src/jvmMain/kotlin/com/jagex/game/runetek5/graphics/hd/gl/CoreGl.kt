/*
 * Not part of the original client: 634HD (toolkit 6). The GL 3.3 core calls the fixed-function
 * emulator (HdGl) makes, shaped like the WebGL 2 context the browser's jaggl emulator was written
 * against (src/jsMain/kotlin/jaggl), so the port keeps that emulator's logic line for line.
 * GL objects are wrapped so the emulator can keep identity checks and per-texture bookkeeping.
 */
package com.jagex.game.runetek5.graphics.hd.gl

import com.jagex.game.runetek5.client.features.RenderStats
import jaggl.GlScratch
import org.lwjgl.opengl.GL33C
import org.lwjgl.system.MemoryStack

class WebGLBuffer(@JvmField val id: Int)

class WebGLTexture(@JvmField val id: Int) {
    /** The internal format of level 0 as last allocated, 0 if unknown. */
    @JvmField var internalFormat: Int = 0
    @JvmField var width: Int = -1
    @JvmField var height: Int = -1
    /** GL_GENERATE_MIPMAP, which a core context lacks: level-0 uploads generate the chain. */
    @JvmField var generateMipmap: Boolean = false
}

class WebGLProgram(@JvmField val id: Int)

class WebGLShader(@JvmField val id: Int)

class WebGLFramebuffer(@JvmField val id: Int)

class WebGLRenderbuffer(@JvmField val id: Int)

/** A uniform location; null (or -1) is ignored, as WebGL ignores null. */
typealias WebGLUniformLocation = Int

/** The ported emulator keeps the browser's name for its context. */
typealias WebGL2RenderingContext = CoreGl

object CoreGl {
    const val ARRAY_BUFFER: Int = GL33C.GL_ARRAY_BUFFER
    const val ELEMENT_ARRAY_BUFFER: Int = GL33C.GL_ELEMENT_ARRAY_BUFFER
    const val TEXTURE_2D: Int = GL33C.GL_TEXTURE_2D
    const val TEXTURE_CUBE_MAP: Int = GL33C.GL_TEXTURE_CUBE_MAP
    const val TEXTURE0: Int = GL33C.GL_TEXTURE0
    const val FLOAT: Int = GL33C.GL_FLOAT
    const val UNSIGNED_BYTE: Int = GL33C.GL_UNSIGNED_BYTE
    const val UNSIGNED_INT: Int = GL33C.GL_UNSIGNED_INT
    const val STREAM_DRAW: Int = GL33C.GL_STREAM_DRAW
    const val STATIC_DRAW: Int = GL33C.GL_STATIC_DRAW
    const val TRIANGLES: Int = GL33C.GL_TRIANGLES
    const val TRIANGLE_STRIP: Int = GL33C.GL_TRIANGLE_STRIP
    const val TRIANGLE_FAN: Int = GL33C.GL_TRIANGLE_FAN
    const val VERTEX_SHADER: Int = GL33C.GL_VERTEX_SHADER
    const val FRAGMENT_SHADER: Int = GL33C.GL_FRAGMENT_SHADER
    const val LINK_STATUS: Int = GL33C.GL_LINK_STATUS
    const val COMPILE_STATUS: Int = GL33C.GL_COMPILE_STATUS
    const val MAX_TEXTURE_IMAGE_UNITS: Int = GL33C.GL_MAX_TEXTURE_IMAGE_UNITS
    const val ALIASED_POINT_SIZE_RANGE: Int = 0x846D
    const val RGBA8: Int = GL33C.GL_RGBA8
    const val RGB8: Int = GL33C.GL_RGB8
    const val R8: Int = GL33C.GL_R8
    const val RG8: Int = GL33C.GL_RG8
    const val RED: Int = GL33C.GL_RED
    const val RG: Int = GL33C.GL_RG
    const val DEPTH_COMPONENT16: Int = GL33C.GL_DEPTH_COMPONENT16
    const val DEPTH_COMPONENT24: Int = GL33C.GL_DEPTH_COMPONENT24
    const val DEPTH24_STENCIL8: Int = GL33C.GL_DEPTH24_STENCIL8
    const val GENERATE_MIPMAP_HINT: Int = 0x8192
    const val FRAGMENT_SHADER_DERIVATIVE_HINT: Int = GL33C.GL_FRAGMENT_SHADER_DERIVATIVE_HINT
    const val BACK: Int = GL33C.GL_BACK
    const val NONE: Int = GL33C.GL_NONE

    // ---- buffers ----

    fun createBuffer(): WebGLBuffer = WebGLBuffer(GL33C.glGenBuffers())
    fun bindBuffer(target: Int, buffer: WebGLBuffer?) = GL33C.glBindBuffer(target, buffer?.id ?: 0)
    fun bufferData(target: Int, size: Int, usage: Int) = GL33C.glBufferData(target, size.toLong(), usage)
    fun bufferDataAddress(target: Int, size: Int, address: Long, usage: Int) = GL33C.nglBufferData(target, size.toLong(), address, usage)
    fun bufferData(target: Int, data: FloatArray, count: Int, usage: Int) =
        GL33C.nglBufferData(target, count.toLong() * 4, GlScratch.floats(data, 0, count), usage)
    fun bufferData(target: Int, data: IntArray, count: Int, usage: Int) =
        GL33C.nglBufferData(target, count.toLong() * 4, GlScratch.ints(data, 0, count), usage)
    fun bufferData(target: Int, data: ByteArray, offset: Int, count: Int, usage: Int) =
        GL33C.nglBufferData(target, count.toLong(), GlScratch.bytes(data, offset, count), usage)
    fun bufferSubData(target: Int, offset: Int, data: FloatArray, count: Int) =
        GL33C.nglBufferSubData(target, offset.toLong(), count.toLong() * 4, GlScratch.floats(data, 0, count))
    fun bufferSubData(target: Int, offset: Int, data: ByteArray, dataOffset: Int, count: Int) =
        GL33C.nglBufferSubData(target, offset.toLong(), count.toLong(), GlScratch.bytes(data, dataOffset, count))
    fun bufferSubDataAddress(target: Int, offset: Int, size: Int, address: Long) =
        GL33C.nglBufferSubData(target, offset.toLong(), size.toLong(), address)
    fun deleteBuffer(buffer: WebGLBuffer?) {
        if (buffer != null) GL33C.glDeleteBuffers(buffer.id)
    }

    // ---- textures ----

    fun createTexture(): WebGLTexture = WebGLTexture(GL33C.glGenTextures())
    fun deleteTexture(texture: WebGLTexture?) {
        if (texture != null) GL33C.glDeleteTextures(texture.id)
    }
    fun bindTexture(target: Int, texture: WebGLTexture?) = GL33C.glBindTexture(target, texture?.id ?: 0)
    fun texParameteri(target: Int, pname: Int, param: Int) = GL33C.glTexParameteri(target, pname, param)
    fun texParameterf(target: Int, pname: Int, param: Float) = GL33C.glTexParameterf(target, pname, param)
    fun generateMipmap(target: Int) = GL33C.glGenerateMipmap(target)
    fun activeTexture(texture: Int) = GL33C.glActiveTexture(texture)

    /** [address] 0 allocates the level without data. */
    fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, address: Long) =
        GL33C.nglTexImage2D(target, level, internalformat, width, height, border, format, type, address)
    fun texImage3D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, depth: Int, border: Int, format: Int, type: Int, address: Long) =
        GL33C.nglTexImage3D(target, level, internalformat, width, height, depth, border, format, type, address)
    fun texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, type: Int, address: Long) =
        GL33C.nglTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, address)
    fun copyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int) =
        GL33C.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)
    fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int) =
        GL33C.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    fun copyTexSubImage3D(target: Int, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, x: Int, y: Int, width: Int, height: Int) =
        GL33C.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height)

    // ---- vertex state ----

    fun enableVertexAttribArray(index: Int) = GL33C.glEnableVertexAttribArray(index)
    fun disableVertexAttribArray(index: Int) = GL33C.glDisableVertexAttribArray(index)
    fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Long) =
        GL33C.glVertexAttribPointer(index, size, type, normalized, stride, offset)
    fun vertexAttrib3f(index: Int, x: Float, y: Float, z: Float) = GL33C.glVertexAttrib3f(index, x, y, z)
    fun vertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float) = GL33C.glVertexAttrib4f(index, x, y, z, w)
    fun drawArrays(mode: Int, first: Int, count: Int) {
        RenderStats.drawCalls++
        RenderStats.categoryDrawCalls[RenderStats.category]++
        if (mode == TRIANGLES) RenderStats.triangles += count / 3
        GL33C.glDrawArrays(mode, first, count)
    }

    fun drawElements(mode: Int, count: Int, type: Int, offset: Long) {
        RenderStats.drawCalls++
        RenderStats.categoryDrawCalls[RenderStats.category]++
        if (mode == TRIANGLES) RenderStats.triangles += count / 3
        GL33C.glDrawElements(mode, count, type, offset)
    }

    // ---- fixed state ----

    fun enable(cap: Int) = GL33C.glEnable(cap)
    fun disable(cap: Int) = GL33C.glDisable(cap)
    fun isEnabled(cap: Int): Boolean = GL33C.glIsEnabled(cap)
    fun hint(target: Int, mode: Int) = GL33C.glHint(target, mode)
    fun lineWidth(width: Float) {
        // a core context may refuse widths above 1 (forward compatible); the emulator widens lines itself
        GL33C.glLineWidth(1f)
    }
    fun blendFunc(sfactor: Int, dfactor: Int) = GL33C.glBlendFunc(sfactor, dfactor)
    fun depthFunc(func: Int) = GL33C.glDepthFunc(func)
    fun depthMask(flag: Boolean) = GL33C.glDepthMask(flag)
    fun cullFace(mode: Int) = GL33C.glCullFace(mode)
    fun colorMask(r: Boolean, g: Boolean, b: Boolean, a: Boolean) = GL33C.glColorMask(r, g, b, a)
    fun scissor(x: Int, y: Int, width: Int, height: Int) = GL33C.glScissor(x, y, width, height)
    fun stencilFunc(func: Int, ref: Int, mask: Int) = GL33C.glStencilFunc(func, ref, mask)
    fun stencilOp(fail: Int, zfail: Int, zpass: Int) = GL33C.glStencilOp(fail, zfail, zpass)
    fun viewport(x: Int, y: Int, width: Int, height: Int) = GL33C.glViewport(x, y, width, height)
    fun clearColor(r: Float, g: Float, b: Float, a: Float) = GL33C.glClearColor(r, g, b, a)
    fun clearDepth(depth: Float) = GL33C.glClearDepth(depth.toDouble())
    fun clear(mask: Int) = GL33C.glClear(mask)
    fun finish() = GL33C.glFinish()
    fun flush() = GL33C.glFlush()
    fun getError(): Int = GL33C.glGetError()
    fun getInteger(pname: Int): Int = GL33C.glGetInteger(pname)
    fun getIntegers(pname: Int, out: IntArray) = GL33C.glGetIntegerv(pname, out)
    fun getFloats(pname: Int, out: FloatArray) = GL33C.glGetFloatv(pname, out)
    fun getString(pname: Int): String? = GL33C.glGetString(pname)
    fun pixelStorei(pname: Int, param: Int) = GL33C.glPixelStorei(pname, param)
    fun readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, address: Long) =
        GL33C.nglReadPixels(x, y, width, height, format, type, address)

    // ---- framebuffers ----

    fun createFramebuffer(): WebGLFramebuffer = WebGLFramebuffer(GL33C.glGenFramebuffers())
    fun deleteFramebuffer(framebuffer: WebGLFramebuffer?) {
        if (framebuffer != null) GL33C.glDeleteFramebuffers(framebuffer.id)
    }
    fun bindFramebuffer(target: Int, framebuffer: WebGLFramebuffer?) = GL33C.glBindFramebuffer(target, framebuffer?.id ?: 0)
    fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: WebGLTexture?, level: Int) =
        GL33C.glFramebufferTexture2D(target, attachment, textarget, texture?.id ?: 0, level)
    fun framebufferTextureLayer(target: Int, attachment: Int, texture: WebGLTexture?, level: Int, layer: Int) =
        GL33C.glFramebufferTextureLayer(target, attachment, texture?.id ?: 0, level, layer)
    fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: WebGLRenderbuffer?) =
        GL33C.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer?.id ?: 0)
    fun checkFramebufferStatus(target: Int): Int = GL33C.glCheckFramebufferStatus(target)
    fun blitFramebuffer(srcX0: Int, srcY0: Int, srcX1: Int, srcY1: Int, dstX0: Int, dstY0: Int, dstX1: Int, dstY1: Int, mask: Int, filter: Int) =
        GL33C.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter)
    fun drawBuffer(mode: Int) = GL33C.glDrawBuffer(mode)
    fun drawBuffers(buffers: IntArray) = GL33C.glDrawBuffers(buffers)
    fun readBuffer(mode: Int) = GL33C.glReadBuffer(mode)

    fun createRenderbuffer(): WebGLRenderbuffer = WebGLRenderbuffer(GL33C.glGenRenderbuffers())
    fun deleteRenderbuffer(renderbuffer: WebGLRenderbuffer?) {
        if (renderbuffer != null) GL33C.glDeleteRenderbuffers(renderbuffer.id)
    }
    fun bindRenderbuffer(target: Int, renderbuffer: WebGLRenderbuffer?) = GL33C.glBindRenderbuffer(target, renderbuffer?.id ?: 0)
    fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) =
        GL33C.glRenderbufferStorage(target, internalformat, width, height)
    fun renderbufferStorageMultisample(target: Int, samples: Int, internalformat: Int, width: Int, height: Int) =
        GL33C.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)

    // ---- shaders ----

    fun createShader(type: Int): WebGLShader = WebGLShader(GL33C.glCreateShader(type))
    fun shaderSource(shader: WebGLShader, source: String) = GL33C.glShaderSource(shader.id, source)
    fun compileShader(shader: WebGLShader) = GL33C.glCompileShader(shader.id)
    fun deleteShader(shader: WebGLShader?) {
        if (shader != null) GL33C.glDeleteShader(shader.id)
    }
    fun shaderCompiled(shader: WebGLShader): Boolean = GL33C.glGetShaderi(shader.id, GL33C.GL_COMPILE_STATUS) != 0
    fun getShaderParameter(shader: WebGLShader, pname: Int): Int = GL33C.glGetShaderi(shader.id, pname)
    fun getShaderInfoLog(shader: WebGLShader): String = GL33C.glGetShaderInfoLog(shader.id)

    fun createProgram(): WebGLProgram = WebGLProgram(GL33C.glCreateProgram())
    fun attachShader(program: WebGLProgram, shader: WebGLShader) = GL33C.glAttachShader(program.id, shader.id)
    fun detachShader(program: WebGLProgram, shader: WebGLShader) = GL33C.glDetachShader(program.id, shader.id)
    fun linkProgram(program: WebGLProgram) = GL33C.glLinkProgram(program.id)
    fun useProgram(program: WebGLProgram?) = GL33C.glUseProgram(program?.id ?: 0)
    fun deleteProgram(program: WebGLProgram?) {
        if (program != null) GL33C.glDeleteProgram(program.id)
    }
    fun programLinked(program: WebGLProgram): Boolean = GL33C.glGetProgrami(program.id, GL33C.GL_LINK_STATUS) != 0
    fun getProgramParameter(program: WebGLProgram, pname: Int): Int = GL33C.glGetProgrami(program.id, pname)
    fun getProgramInfoLog(program: WebGLProgram): String = GL33C.glGetProgramInfoLog(program.id)
    fun getUniformLocation(program: WebGLProgram, name: String): WebGLUniformLocation = GL33C.glGetUniformLocation(program.id, name)
    fun bindAttribLocation(program: WebGLProgram, index: Int, name: String) = GL33C.glBindAttribLocation(program.id, index, name)
    fun bindFragDataLocation(program: WebGLProgram, color: Int, name: String) = GL33C.glBindFragDataLocation(program.id, color, name)

    fun uniform1i(location: WebGLUniformLocation?, x: Int) = GL33C.glUniform1i(location ?: -1, x)
    fun uniform3i(location: WebGLUniformLocation?, x: Int, y: Int, z: Int) = GL33C.glUniform3i(location ?: -1, x, y, z)
    fun uniform4i(location: WebGLUniformLocation?, x: Int, y: Int, z: Int, w: Int) = GL33C.glUniform4i(location ?: -1, x, y, z, w)
    fun uniform1f(location: WebGLUniformLocation?, x: Float) = GL33C.glUniform1f(location ?: -1, x)
    fun uniform2f(location: WebGLUniformLocation?, x: Float, y: Float) = GL33C.glUniform2f(location ?: -1, x, y)
    fun uniform3f(location: WebGLUniformLocation?, x: Float, y: Float, z: Float) = GL33C.glUniform3f(location ?: -1, x, y, z)
    fun uniform4f(location: WebGLUniformLocation?, x: Float, y: Float, z: Float, w: Float) = GL33C.glUniform4f(location ?: -1, x, y, z, w)
    fun uniform3fv(location: WebGLUniformLocation?, value: FloatArray) = GL33C.glUniform3f(location ?: -1, value[0], value[1], value[2])
    fun uniform4fv(location: WebGLUniformLocation?, value: FloatArray) = GL33C.glUniform4f(location ?: -1, value[0], value[1], value[2], value[3])
    /** The first [count] floats of [value] as vec4s. */
    fun uniform4fv(location: WebGLUniformLocation?, value: FloatArray, count: Int) =
        GL33C.nglUniform4fv(location ?: -1, count / 4, GlScratch.floats(value, 0, count))
    fun uniformMatrix2fv(location: WebGLUniformLocation?, transpose: Boolean, value: FloatArray) = GL33C.glUniformMatrix2fv(location ?: -1, transpose, value)
    fun uniformMatrix3fv(location: WebGLUniformLocation?, transpose: Boolean, value: FloatArray) = GL33C.glUniformMatrix3fv(location ?: -1, transpose, value)
    fun uniformMatrix4fv(location: WebGLUniformLocation?, transpose: Boolean, value: FloatArray) = GL33C.glUniformMatrix4fv(location ?: -1, transpose, value)

    // ---- vertex arrays ----

    fun createVertexArray(): Int = GL33C.glGenVertexArrays()
    fun bindVertexArray(vao: Int) = GL33C.glBindVertexArray(vao)

    /** Reads one int parameter into a fresh stack slot; for the rare state queries. */
    fun queryInt(pname: Int): Int = MemoryStack.stackPush().use { stack ->
        val out = stack.mallocInt(1)
        GL33C.glGetIntegerv(pname, out)
        out.get(0)
    }
}
