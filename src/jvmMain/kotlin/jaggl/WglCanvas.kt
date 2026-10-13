package jaggl

import java.awt.AWTException
import java.awt.Canvas
import org.lwjgl.opengl.WGL
import org.lwjgl.opengl.WGLARBMultisample
import org.lwjgl.opengl.WGLARBPixelFormat
import org.lwjgl.opengl.awt.GLData
import org.lwjgl.opengl.awt.PlatformGLCanvas
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.jawt.JAWT
import org.lwjgl.system.jawt.JAWTFunctions
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo
import org.lwjgl.system.windows.GDI32
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR
import org.lwjgl.system.windows.User32

/**
 * Not part of the original client: a WGL context on an AWT canvas, in place of lwjgl3-awt's
 * PlatformWin32GLCanvas, for two reasons.
 *
 * - Under Wine (11.19, 32-bit client) the first wglMakeCurrent of a new context fails
 *   (ERROR_PROC_NOT_FOUND) and the second succeeds. lwjgl3-awt makes the context current once
 *   inside create and gives up, so the client fell back to software; makeCurrent here tries twice.
 * - It keeps its own device context from GetDC(hwnd) for the canvas's life, as GLFW and SDL do,
 *   so neither makeCurrent nor swapBuffers needs JAWT's lock. The canvas's window is read from JAWT
 *   once.
 *
 * A window's pixel format can be set only once, so a canvas that has one (a toolkit made again on
 * the same canvas) keeps it. With samples asked for, the format comes from wglChoosePixelFormatARB.
 *
 * The device context is the window's own, so it is used without JAWT's lock: lock and unlock do
 * nothing.
 */
internal class WglCanvas : PlatformGLCanvas {

    private var hwnd: Long = 0L
    private var hdc: Long = 0L

    override fun create(canvas: Canvas, data: GLData, effective: GLData): Long {
        if (hdc != 0L) throw AWTException("already created")
        hwnd = windowOf(canvas)
        hdc = User32.GetDC(hwnd)
        if (hdc == 0L) throw AWTException("GetDC failed")
        try {
            MemoryStack.stackPush().use { stack ->
                val error = stack.mallocInt(1)
                var format = GDI32.GetPixelFormat(error, hdc)
                if (format == 0) {
                    format = choosePixelFormat(data)
                    val pfd = PIXELFORMATDESCRIPTOR.calloc(stack)
                    if (GDI32.DescribePixelFormat(error, hdc, format, pfd) == 0 || !GDI32.SetPixelFormat(error, hdc, format, pfd)) {
                        throw AWTException("SetPixelFormat failed (error " + error.get(0) + ")")
                    }
                }
                val context = WGL.wglCreateContext(error, hdc)
                if (context == 0L) throw AWTException("wglCreateContext failed (error " + error.get(0) + ")")
                return context
            }
        } catch (ex: AWTException) {
            dispose()
            throw ex
        }
    }

    private fun choosePixelFormat(data: GLData): Int {
        if (data.samples > 0) {
            val format = choosePixelFormatARB(data)
            if (format != 0) return format
            // no multisampled format (or no WGL_ARB_pixel_format): a plain one, as libjaggl fell back
        }
        MemoryStack.stackPush().use { stack ->
            var flags = GDI32.PFD_DRAW_TO_WINDOW or GDI32.PFD_SUPPORT_OPENGL
            if (data.doubleBuffer) flags = flags or GDI32.PFD_DOUBLEBUFFER
            val pfd = PIXELFORMATDESCRIPTOR.calloc(stack)
                .nSize(PIXELFORMATDESCRIPTOR.SIZEOF.toShort())
                .nVersion(1.toShort())
                .dwFlags(flags)
                .iPixelType(GDI32.PFD_TYPE_RGBA)
                .cColorBits((data.redSize + data.greenSize + data.blueSize).toByte())
                .cRedBits(data.redSize.toByte())
                .cGreenBits(data.greenSize.toByte())
                .cBlueBits(data.blueSize.toByte())
                .cAlphaBits(data.alphaSize.toByte())
                .cDepthBits(data.depthSize.toByte())
                .cStencilBits(data.stencilSize.toByte())
                .dwLayerMask(GDI32.PFD_MAIN_PLANE.toInt())
            val error = stack.mallocInt(1)
            val format = GDI32.ChoosePixelFormat(error, hdc, pfd)
            if (format == 0) throw AWTException("ChoosePixelFormat failed (error " + error.get(0) + ")")
            return format
        }
    }

    /** 0 if the driver has no such format or no WGL_ARB_pixel_format. */
    private fun choosePixelFormatARB(data: GLData): Int {
        val attributes = intArrayOf(
            WGLARBPixelFormat.WGL_DRAW_TO_WINDOW_ARB, 1,
            WGLARBPixelFormat.WGL_SUPPORT_OPENGL_ARB, 1,
            WGLARBPixelFormat.WGL_DOUBLE_BUFFER_ARB, if (data.doubleBuffer) 1 else 0,
            WGLARBPixelFormat.WGL_PIXEL_TYPE_ARB, WGLARBPixelFormat.WGL_TYPE_RGBA_ARB,
            WGLARBPixelFormat.WGL_ACCELERATION_ARB, WGLARBPixelFormat.WGL_FULL_ACCELERATION_ARB,
            WGLARBPixelFormat.WGL_RED_BITS_ARB, data.redSize,
            WGLARBPixelFormat.WGL_GREEN_BITS_ARB, data.greenSize,
            WGLARBPixelFormat.WGL_BLUE_BITS_ARB, data.blueSize,
            WGLARBPixelFormat.WGL_DEPTH_BITS_ARB, data.depthSize,
            WGLARBPixelFormat.WGL_STENCIL_BITS_ARB, data.stencilSize,
            WGLARBMultisample.WGL_SAMPLE_BUFFERS_ARB, 1,
            WGLARBMultisample.WGL_SAMPLES_ARB, data.samples,
            0,
        )
        val format = IntArray(1)
        val count = IntArray(1)
        return try {
            // LWJGL finds the function through a context of its own when none is current
            if (WGLARBPixelFormat.wglChoosePixelFormatARB(hdc, attributes, null, format, count) && count[0] > 0) format[0] else 0
        } catch (_: Exception) {
            0
        }
    }

    override fun deleteContext(context: Long): Boolean = WGL.wglDeleteContext(null, context)

    /** GetLastError after the last makeCurrent that failed, for the exception GlContext throws. */
    var lastError: Int = 0
        private set

    override fun makeCurrent(context: Long): Boolean {
        MemoryStack.stackPush().use { stack ->
            val error = stack.mallocInt(1)
            if (context == 0L) return WGL.wglMakeCurrent(error, 0L, 0L)
            // twice: Wine fails the first make-current of a new context (see the class comment)
            if (WGL.wglMakeCurrent(error, hdc, context) || WGL.wglMakeCurrent(error, hdc, context)) return true
            lastError = error.get(0)
            return false
        }
    }

    override fun isCurrent(context: Long): Boolean = WGL.wglGetCurrentContext(null) == context

    override fun swapBuffers(): Boolean = GDI32.SwapBuffers(null, hdc)

    override fun delayBeforeSwapNV(seconds: Float): Boolean = false

    override fun lock() {}

    override fun unlock() {}

    override fun dispose() {
        if (hdc != 0L) User32.ReleaseDC(hwnd, hdc)
        hdc = 0L
        hwnd = 0L
    }

    companion object {

        private val awt: JAWT by lazy {
            val awt = JAWT.calloc()
            awt.version(JAWTFunctions.JAWT_VERSION_1_4)
            if (!JAWTFunctions.JAWT_GetAWT(awt)) throw IllegalStateException("JAWT_GetAWT failed")
            awt
        }

        /** The canvas's native window, through JAWT. */
        private fun windowOf(canvas: Canvas): Long {
            val ds = JAWTFunctions.JAWT_GetDrawingSurface(canvas, awt.GetDrawingSurface())
                ?: throw AWTException("no JAWT drawing surface (is the canvas displayable?)")
            try {
                val lock = JAWTFunctions.JAWT_DrawingSurface_Lock(ds, ds.Lock())
                if ((lock and JAWTFunctions.JAWT_LOCK_ERROR) != 0) throw AWTException("JAWT lock failed")
                try {
                    val dsi = JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo(ds, ds.GetDrawingSurfaceInfo())
                        ?: throw AWTException("no JAWT drawing surface info")
                    try {
                        val hwnd = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo()).hwnd()
                        if (hwnd == 0L) throw AWTException("JAWT gave no window")
                        return hwnd
                    } finally {
                        JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo())
                    }
                } finally {
                    JAWTFunctions.JAWT_DrawingSurface_Unlock(ds, ds.Unlock())
                }
            } finally {
                JAWTFunctions.JAWT_FreeDrawingSurface(ds, awt.FreeDrawingSurface())
            }
        }
    }
}
