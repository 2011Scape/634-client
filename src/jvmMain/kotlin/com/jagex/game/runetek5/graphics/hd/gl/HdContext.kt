/*
 * Not part of the original client: 634HD (toolkit 6). What a jaggl.OpenGL instance is to the Java
 * OpenGL toolkit: the context and its surfaces, here a GL 3.3 core context (4.1 core on macOS) whose
 * calls go through HdGl. The emulator state (HdGl.state) belongs to the context and is made current
 * with it.
 */
package com.jagex.game.runetek5.graphics.hd.gl

import jaggl.GlContext
import java.awt.Canvas
import org.lwjgl.opengl.GL33C

class HdContext {

    private var context: GlContext? = null

    private var state: GlState? = null

    private var owner: Thread? = null

    /** The GL version and renderer, for the fallback message and the debug panel. */
    var description: String = ""
        private set

    fun setSwapInterval(interval: Int) {
        context?.setSwapInterval(interval)
    }

    fun surfaceResized(handle: Long) {
    }

    fun arePbuffersAvailable(): Boolean = false

    /** jaggl's a(): releases the context from this thread, if it holds it. */
    @Synchronized
    fun a(): Boolean {
        if (owner !== Thread.currentThread()) return false
        context?.detach()
        HdGl.b.remove(owner)
        owner = null
        return true
    }

    fun releaseSurface(canvas: Canvas?, handle: Long) {
        context?.releaseSurface(handle)
    }

    /**
     * jaggl's init: a core context on [canvas]. Throws a RuntimeException naming the reason when the
     * driver has no 3.3 core context or the emulator's shaders do not compile, which the toolkit
     * switch turns into the fallback (HdToolkit.create).
     */
    fun init(canvas: Canvas?, red: Int, green: Int, blue: Int, depth: Int, stencil: Int, samples: Int): Long {
        if (context != null) throw IllegalStateException("already initialised")
        val created: GlContext
        try {
            created = GlContext.create(canvas!!, red, green, blue, depth, stencil, samples, true)
        } catch (ex: Exception) {
            throw RuntimeException("no OpenGL 3.3 core context: " + ex.message, ex)
        } catch (ex: LinkageError) {
            throw RuntimeException("no OpenGL 3.3 core context: " + ex, ex)
        } catch (ex: java.awt.AWTError) {
            throw RuntimeException("no OpenGL 3.3 core context: " + ex, ex)
        }
        context = created
        if (!created.attach()) {
            release()
            throw RuntimeException("the OpenGL 3.3 core context cannot be made current")
        }
        try {
            description = GL33C.glGetString(GL33C.GL_VERSION) + " (" + GL33C.glGetString(GL33C.GL_RENDERER) + ")"
            val major = GL33C.glGetInteger(GL33C.GL_MAJOR_VERSION)
            val minor = GL33C.glGetInteger(GL33C.GL_MINOR_VERSION)
            if (major < 3 || major == 3 && minor < 3) throw RuntimeException("OpenGL " + major + "." + minor + " is below 3.3")
            val newState = HdGl.createState()
            HdGl.state = newState
            state = newState
            HdGl.resetWindowState(canvas.width, canvas.height)
        } catch (ex: RuntimeException) {
            created.detach()
            release()
            throw RuntimeException("634HD cannot start on " + description + ": " + ex.message, ex)
        } catch (ex: Error) {
            created.detach()
            release()
            throw RuntimeException("634HD cannot start on " + description + ": " + ex, ex)
        }
        created.detach()
        return created.mainHandle
    }

    /** jaggl's b(): makes the context current on this thread. */
    @Synchronized
    fun b(): Boolean {
        val gl = context ?: return false
        if (!gl.attach()) return false
        val thread = Thread.currentThread()
        val previous = HdGl.b.put(thread, this) as HdContext?
        if (previous != null && previous !== this) previous.owner = null
        owner = thread
        HdGl.state = state!!
        return true
    }

    fun swapBuffers() {
        context?.swapBuffers()
    }

    /** jaggl's a(String): whether the context offers [extension], as the emulator presents it. */
    fun a(extension: String?): Boolean = HdGl.hasExtension(extension)

    fun release() {
        val gl = context
        context = null
        state = null
        gl?.release()
    }

    fun prepareSurface(canvas: Canvas?): Long = context?.prepareSurface(canvas!!) ?: -1L

    fun setSurface(handle: Long): Boolean = context?.setSurface(handle) ?: false
}
