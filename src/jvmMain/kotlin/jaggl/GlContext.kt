package jaggl

import java.awt.AWTException
import java.awt.Canvas
import java.io.File
import org.lwjgl.opengl.CGL
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.opengl.GLXEXTSwapControl
import org.lwjgl.opengl.WGLEXTSwapControl
import org.lwjgl.opengl.awt.AWTGLCanvas
import org.lwjgl.opengl.awt.GLData
import org.lwjgl.opengl.awt.PlatformGLCanvas
import org.lwjgl.opengl.awt.PlatformLinuxGLCanvas
import org.lwjgl.opengl.awt.PlatformMacOSXGLCanvas
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform

/**
 * Not part of the original client: the GL context behind a [OpenGL], on LWJGL. libjaggl made its
 * context on the canvas's native window; lwjgl3-awt does the same through JAWT, with GLX on Linux
 * and an NSOpenGLView (a legacy 2.1 context) on macOS. On Windows it is WglCanvas, a WGL context
 * on the canvas's own device context, which Wine also accepts.
 *
 * One context draws to every surface: the canvas given to [create] and each canvas given to
 * [prepareSurface]. GLX and WGL can make a context current on any window with a compatible pixel
 * format, as libjaggl did, so a further surface only borrows its canvas's drawable. macOS ties a
 * context to its view, so there a further surface has its own context, sharing objects with the
 * first.
 *
 * The JAWT drawing surface is locked only around makeCurrent and swapBuffers. On X11 the lock is
 * AWT's global lock, so holding it for a frame would stall the event thread; lwjgl3-awt requires it
 * to be held while a context is made current (Linux) and while buffers swap (Windows).
 */
internal class GlContext private constructor(private val data: GLData, main: Surface) {

    class Surface(val canvas: Canvas, val platform: PlatformGLCanvas, val context: Long, val ownsContext: Boolean)

    private val surfaces = HashMap<Long, Surface>()
    private var nextHandle = 1L
    private var current: Surface = main
    private var owner: Thread? = null

    /** The context of the surface from [create]: what jaggl's `peer` holds. */
    val context: Long = main.context

    /** The handle of the surface from [create], which `OpenGL.init` returns. */
    val mainHandle: Long = add(main)

    lateinit var capabilities: GLCapabilities
        private set

    private fun add(surface: Surface): Long {
        val handle = nextHandle++
        surfaces[handle] = surface
        return handle
    }

    private fun bind(surface: Surface): Boolean {
        surface.platform.lock()
        try {
            if (!surface.platform.makeCurrent(surface.context)) return false
        } finally {
            surface.platform.unlock()
        }
        GL.setCapabilities(capabilities)
        current = surface
        return true
    }

    /** libjaggl's attachPeer: makes the context current on this thread, on the current surface. */
    @Synchronized
    fun attach(): Boolean {
        val thread = Thread.currentThread()
        if (owner != null && owner !== thread) return false
        return try {
            if (!bind(current)) return false
            owner = thread
            true
        } catch (_: AWTException) {
            false
        }
    }

    /** libjaggl's detachPeer. */
    @Synchronized
    fun detach() {
        if (owner !== Thread.currentThread()) return
        try {
            current.platform.lock()
            try {
                current.platform.makeCurrent(0L)
            } finally {
                current.platform.unlock()
            }
        } catch (_: AWTException) {
        }
        GL.setCapabilities(null)
        owner = null
    }

    @Synchronized
    fun setSurface(handle: Long): Boolean {
        val surface = surfaces[handle] ?: return false
        if (owner !== Thread.currentThread()) return false
        return try {
            bind(surface)
        } catch (_: AWTException) {
            false
        }
    }

    @Synchronized
    fun prepareSurface(canvas: Canvas): Long {
        return try {
            val platform = newPlatformCanvas()
            if (Platform.get() == Platform.MACOSX) {
                // Not the drawable of another view: a context of its own, sharing with the first.
                val shared = copy(data)
                shared.shareContext = ShareHandle(context)
                val surfaceContext = platform.create(canvas, shared, GLData())
                add(Surface(canvas, platform, surfaceContext, true))
            } else {
                // create() sets the window's pixel format and finds its drawable; its own context
                // is not needed.
                val throwaway = platform.create(canvas, data, GLData())
                platform.deleteContext(throwaway)
                add(Surface(canvas, platform, context, false))
            }
        } catch (ex: AWTException) {
            -1L
        }
    }

    @Synchronized
    fun releaseSurface(handle: Long) {
        val surface = surfaces[handle] ?: return
        if (handle == mainHandle) return
        if (surface === current) {
            val main = surfaces.getValue(mainHandle)
            if (owner === Thread.currentThread()) {
                // Stay current, as OpenGL.b() still records this thread as holding the context, but
                // on the canvas from init.
                val rebound = try {
                    bind(main)
                } catch (_: AWTException) {
                    false
                }
                if (!rebound) detach()
            }
            current = main
        }
        surfaces.remove(handle)
        dispose(surface)
    }

    @Synchronized
    fun swapBuffers() {
        val platform = current.platform
        try {
            platform.lock()
        } catch (_: AWTException) {
            return
        }
        try {
            platform.swapBuffers()
        } finally {
            platform.unlock()
        }
    }

    /** Best effort, as with libjaggl: a driver may ignore or not offer the swap interval. */
    @Synchronized
    fun setSwapInterval(interval: Int) {
        try {
            when (val platform = current.platform) {
                is PlatformLinuxGLCanvas -> GLXEXTSwapControl.glXSwapIntervalEXT(platform.display, platform.drawable, interval)
                is WglCanvas -> WGLEXTSwapControl.wglSwapIntervalEXT(interval)
                is PlatformMacOSXGLCanvas -> MemoryStack.stackPush().use { stack ->
                    CGL.CGLSetParameter(current.context, CGL.kCGLCPSwapInterval, stack.ints(interval))
                }
            }
        } catch (_: Exception) {
            // no swap control extension, or the driver refused
        } catch (_: LinkageError) {
        }
    }

    /**
     * libjaggl's release. Only the thread holding the context (or none) can delete it: GL cannot
     * delete a context current on another thread, so then it is left, with a message, rather than
     * deleted under that thread.
     */
    @Synchronized
    fun release() {
        val holder = owner
        if (holder != null && holder !== Thread.currentThread()) {
            System.err.println("jaggl: the OpenGL context is still current on " + holder.name + "; not deleted")
            return
        }
        detach()
        for (surface in surfaces.values) {
            if (surface.ownsContext) dispose(surface)
        }
        for (surface in surfaces.values) {
            if (!surface.ownsContext) dispose(surface)
        }
        surfaces.clear()
    }

    private fun dispose(surface: Surface) {
        try {
            if (surface.ownsContext) surface.platform.deleteContext(surface.context)
            surface.platform.dispose()
        } catch (_: Exception) {
            // the canvas or its window is already gone
        }
    }

    /** Carries a context handle into [GLData.shareContext], which takes an AWTGLCanvas. */
    private class ShareHandle(handle: Long) : AWTGLCanvas() {
        init {
            context = handle
        }

        override fun initGL() {}

        override fun paintGL() {}
    }

    companion object {

        init {
            // LWJGL unpacks its natives into the shared temporary directory by default; keep them in
            // the home directory, as the game cache is.
            if (System.getProperty("org.lwjgl.system.SharedLibraryExtractPath") == null) {
                Configuration.SHARED_LIBRARY_EXTRACT_PATH.set(
                    File(System.getProperty("user.home"), ".lwjgl/634-client").path,
                )
            }
            // In a Wayland session LWJGL loads EGL for GL; the contexts here are GLX (see
            // newPlatformCanvas), so it must load libGL's GLX instead.
            if (System.getProperty("org.lwjgl.opengl.contextAPI") == null) {
                Configuration.OPENGL_CONTEXT_API.set("native")
            }
        }

        /**
         * libjaggl's init: a context on [canvas] with the given pixel format. No version and no
         * profile are asked for, which gives the newest compatibility context on GLX and WGL (the
         * client needs immediate mode, the matrix stack and ARB programs) and the legacy 2.1 context
         * on macOS.
         */
        fun create(canvas: Canvas, red: Int, green: Int, blue: Int, depth: Int, stencil: Int, samples: Int): GlContext =
            create(canvas, red, green, blue, depth, stencil, samples, false)

        /**
         * With [core], a 3.3 core profile context instead (forward compatible; 4.1 core on macOS),
         * for 634HD (toolkit 6); the other toolkits never ask for one.
         */
        fun create(canvas: Canvas, red: Int, green: Int, blue: Int, depth: Int, stencil: Int, samples: Int, core: Boolean): GlContext {
            val data = GLData()
            if (core) {
                // macOS has 4.1 core and nothing between it and 2.1; client.test.hdGlVersion=4.1 asks
                // for the same elsewhere, to check 634HD as a Mac would run it
                val mac = System.getProperty("os.name", "").lowercase().contains("mac")
                val forced = System.getProperty("client.test.hdGlVersion")
                data.majorVersion = if (mac || forced == "4.1") 4 else 3
                data.minorVersion = if (mac || forced == "4.1") 1 else 3
                data.profile = GLData.Profile.CORE
                data.forwardCompatible = true
            }
            data.redSize = red
            data.greenSize = green
            data.blueSize = blue
            data.alphaSize = 0
            data.depthSize = depth
            data.stencilSize = stencil
            data.doubleBuffer = true
            if (samples > 0) {
                data.sampleBuffers = 1
                data.samples = samples
            }
            data.swapInterval = 0
            val platform = newPlatformCanvas()
            val context = try {
                platform.create(canvas, data, GLData())
            } catch (ex: AWTException) {
                // a GLX without a swap-control extension (Mesa's software renderer, some remote and
                // virtual displays) refuses a context that asks for a swap interval: ask without one
                if (ex.message?.contains("wap") != true) throw ex
                data.swapInterval = null
                platform.create(canvas, data, GLData())
            }
            val gl = GlContext(data, Surface(canvas, platform, context, true))
            try {
                platform.lock()
                try {
                    if (!platform.makeCurrent(context)) {
                        val detail = if (platform is WglCanvas) " (error " + platform.lastError + ")" else ""
                        throw AWTException("cannot make the context current" + detail)
                    }
                    gl.capabilities = GL.createCapabilities()
                    platform.makeCurrent(0L)
                } finally {
                    platform.unlock()
                }
            } catch (ex: Exception) {
                gl.dispose(gl.current)
                throw ex
            } catch (ex: LinkageError) {
                gl.dispose(gl.current)
                throw ex
            } finally {
                GL.setCapabilities(null)
            }
            return gl
        }

        private fun newPlatformCanvas(): PlatformGLCanvas = when (Platform.get()) {
            Platform.WINDOWS -> WglCanvas()
            Platform.MACOSX -> PlatformMacOSXGLCanvas()
            // GLX, as libjaggl used, also under Wayland (through XWayland, where AWT's windows are);
            // lwjgl3-awt would pick EGL there.
            else -> PlatformLinuxGLCanvas()
        }

        private fun copy(from: GLData): GLData {
            val to = GLData()
            to.redSize = from.redSize
            to.greenSize = from.greenSize
            to.blueSize = from.blueSize
            to.alphaSize = from.alphaSize
            to.depthSize = from.depthSize
            to.stencilSize = from.stencilSize
            to.doubleBuffer = from.doubleBuffer
            to.sampleBuffers = from.sampleBuffers
            to.samples = from.samples
            to.swapInterval = from.swapInterval
            to.majorVersion = from.majorVersion
            to.minorVersion = from.minorVersion
            to.profile = from.profile
            to.forwardCompatible = from.forwardCompatible
            return to
        }
    }
}
