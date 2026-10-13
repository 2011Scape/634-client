package jaggl

import java.awt.Canvas
import java.util.Hashtable
import org.lwjgl.opengl.ARBDrawBuffers
import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.ARBVertexBufferObject
import org.lwjgl.opengl.ARBVertexProgram
import org.lwjgl.opengl.EXTFramebufferBlit
import org.lwjgl.opengl.EXTFramebufferMultisample
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

/*
 * Not part of the original client: libjaggl (JNI) is replaced by LWJGL. Every function keeps the
 * original's name and signature and does what libjaggl's did, through lwjgl-opengl; the context and
 * its surfaces are a GlContext (lwjgl3-awt), the array-and-offset calls copy through GlScratch.
 * `peer` holds the GL context handle, as it held libjaggl's.
 */
@OriginalClass("client!jaggl/OpenGL")
actual class OpenGL actual constructor() {

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "a", descriptor = "Ljava/lang/Thread;")
	private var a: Thread? = null

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "c", descriptor = "Ljava/util/Hashtable;")
	private var c: Hashtable<Any?, Any?>? = null

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	// Not part of the original client: the LWJGL context (see the class comment).
	private var context: GlContext? = null

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "detachPeer", descriptor = "()V")
	private fun detachPeer() {
		this.context?.detach()
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setSwapInterval", descriptor = "(I)V")
	actual fun setSwapInterval(@OriginalArg(0) arg0: Int) {
		this.context?.setSwapInterval(arg0)
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "surfaceResized", descriptor = "(J)V")
	actual fun surfaceResized(@OriginalArg(0) arg0: Long) {
		// GLX, WGL and the macOS layer follow the canvas's size themselves.
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setPbuffer", descriptor = "(J)V")
	private fun setPbuffer(@OriginalArg(0) arg0: Long) {
		throw UnsupportedOperationException("no pbuffers (arePbuffersAvailable)")
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "arePbuffersAvailable", descriptor = "()Z")
	actual fun arePbuffersAvailable(): Boolean {
		// lwjgl3-awt makes window contexts only; the toolkits then draw offscreen into framebuffers.
		return false
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "a", descriptor = "()Z")
	@Synchronized
	actual fun a(): Boolean {
		if (this.a === Thread.currentThread()) {
			this.detachPeer()
			b.remove(this.a)
			this.a = null
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "releasePbuffer", descriptor = "(J)V")
	private fun releasePbuffer(@OriginalArg(0) arg0: Long) {
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "attachPeer", descriptor = "()Z")
	private fun attachPeer(): Boolean {
		return this.context?.attach() ?: false
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "releaseSurface", descriptor = "(Ljava/awt/Canvas;J)V")
	actual fun releaseSurface(@OriginalArg(0) arg0: Canvas?, @OriginalArg(1) arg1: Long) {
		this.context?.releaseSurface(arg1)
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "init", descriptor = "(Ljava/awt/Canvas;IIIIII)J")
	actual fun init(@OriginalArg(0) arg0: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int): Long {
		if (this.context != null) {
			throw IllegalStateException("already initialised")
		}
		val context: GlContext
		try {
			context = GlContext.create(arg0!!, arg1, arg2, arg3, arg4, arg5, arg6)
		} catch (ex: Exception) {
			// The toolkits fall back to the software toolkit on a RuntimeException (ActiveToolkitInit),
			// as when libjaggl failed to load.
			throw RuntimeException("OpenGL context: " + ex, ex)
		} catch (ex: LinkageError) {
			// LWJGL's natives missing for this platform (UnsatisfiedLinkError), or a class failing to
			// initialise; a VirtualMachineError still propagates.
			throw RuntimeException("OpenGL context: " + ex, ex)
		} catch (ex: java.awt.AWTError) {
			throw RuntimeException("OpenGL context: " + ex, ex)
		}
		this.context = context
		this.peer = context.context
		return context.mainHandle
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "b", descriptor = "()Z")
	@Synchronized
	actual fun b(): Boolean {
		@Pc(1) val thread: Thread = Thread.currentThread()
		if (!this.attachPeer()) {
			return false
		}
		@Pc(10) val openGL: OpenGL? = b.put(thread, this) as OpenGL?
		if (openGL != null) {
			openGL.a = null
		}
		this.a = thread
		return true
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "createPbuffer", descriptor = "(II)J")
	private fun createPbuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Long {
		return 0L
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "swapBuffers", descriptor = "()V")
	actual fun swapBuffers() {
		this.context?.swapBuffers()
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "a", descriptor = "(Ljava/lang/String;)Z")
	actual fun a(@OriginalArg(0) string: String?): Boolean {
		if (this.c == null) {
			this.c = Hashtable<Any?, Any?>()
			@Pc(10) val string2: String? = glGetString(7939)
			@Pc(12) var local12: Int = 0
			while (true) {
				@Pc(17) val local17: Int = string2!!.indexOf(' ', local12)
				if (local17 == -1) {
					// trim { it <= ' ' } is exactly java.lang.String.trim()
					@Pc(48) val string3: String = string2.substring(local12).trim { it <= ' ' }
					if (string3.length != 0) {
						this.c!!.put(string3, string3)
					}
					break
				}
				@Pc(26) val string4: String = string2.substring(local12, local17).trim { it <= ' ' }
				if (string4.length != 0) {
					this.c!!.put(string4, string4)
				}
				local12 = local17 + 1
			}
		}
		return this.c!!.containsKey(string)
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "release", descriptor = "()V")
	actual fun release() {
		val context: GlContext? = this.context
		this.context = null
		this.peer = 0L
		context?.release()
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "prepareSurface", descriptor = "(Ljava/awt/Canvas;)J")
	actual fun prepareSurface(@OriginalArg(0) arg0: Canvas?): Long {
		return this.context?.prepareSurface(arg0!!) ?: -1L
	}

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setSurface", descriptor = "(J)Z")
	actual fun setSurface(@OriginalArg(0) arg0: Long): Boolean {
		return this.context?.setSurface(arg0) ?: false
	}

	actual companion object {

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "b", descriptor = "Ljava/util/Hashtable;")
		private val b: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()

		// Not part of the original client: GLhandleARB is a jlong in jaggl and an unsigned int in LWJGL.
		private fun handle(handle: Int): Long {
			return handle.toLong() and 0xFFFFFFFFL
		}

		// Not part of the original client: one source string, its bytes as libjaggl passed them.
		private fun shaderSource(shader: Int, source: ByteArray) {
			val stack: MemoryStack = MemoryStack.stackPush()
			try {
				val sourceAddress: Long = GlScratch.bytes(source, 0)
				ARBShaderObjects.nglShaderSourceARB(shader, 1, MemoryUtil.memAddress(stack.pointers(sourceAddress)), MemoryUtil.memAddress(stack.ints(source.size)))
			} finally {
				stack.pop()
			}
		}

		// Not part of the original client: glProgramStringARB with the program's bytes.
		private fun programString(target: Int, format: Int, program: ByteArray) {
			ARBVertexProgram.nglProgramStringARB(target, format, program.size, GlScratch.bytes(program, 0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTranslatef", descriptor = "(FFF)V")
		@JvmStatic actual fun glTranslatef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glTranslatef(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindRenderbufferEXT", descriptor = "(II)V")
		@JvmStatic actual fun glBindRenderbufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			EXTFramebufferObject.glBindRenderbufferEXT(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glScalef", descriptor = "(FFF)V")
		@JvmStatic actual fun glScalef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glScalef(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glActiveTexture", descriptor = "(I)V")
		@JvmStatic actual fun glActiveTexture(@OriginalArg(0) arg0: Int) {
			GL13.glActiveTexture(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramLocalParameter4fvARB", descriptor = "(II[FI)V")
		@JvmStatic actual fun glProgramLocalParameter4fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int) {
			ARBVertexProgram.glProgramLocalParameter4fARB(arg0, arg1, arg2!![arg3], arg2[arg3 + 1], arg2[arg3 + 2], arg2[arg3 + 3])
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetObjectParameterivARB", descriptor = "(JI[II)V")
		@JvmStatic actual fun glGetObjectParameterivARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int) {
			ARBShaderObjects.nglGetObjectParameterivARB(arg0.toInt(), arg1, GlScratch.ints(arg2, arg3, 4))
			GlScratch.readInts(arg2, arg3, 4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferTexture3DEXT", descriptor = "(IIIIII)V")
		@JvmStatic actual fun glFramebufferTexture3DEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
			EXTFramebufferObject.glFramebufferTexture3DEXT(arg0, arg1, arg2, arg3, arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteLists", descriptor = "(II)V")
		@JvmStatic actual fun glDeleteLists(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glDeleteLists(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform1iARB", descriptor = "(II)V")
		@JvmStatic actual fun glUniform1iARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			ARBShaderObjects.glUniform1iARB(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFinish", descriptor = "()V")
		@JvmStatic actual fun glFinish() {
			GL11.glFinish()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawElements", descriptor = "(IIIJ)V")
		@JvmStatic actual fun glDrawElements(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long) {
			GL11.nglDrawElements(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadBuffer", descriptor = "(I)V")
		@JvmStatic actual fun glReadBuffer(@OriginalArg(0) arg0: Int) {
			GL11.glReadBuffer(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetError", descriptor = "()I")
		@JvmStatic private fun glGetError(): Int {
			return GL11.glGetError()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUseProgramObjectARB", descriptor = "(J)V")
		@JvmStatic actual fun glUseProgramObjectARB(@OriginalArg(0) arg0: Long) {
			ARBShaderObjects.glUseProgramObjectARB(arg0.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor4f", descriptor = "(FFFF)V")
		@JvmStatic private fun glColor4f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float) {
			GL11.glColor4f(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexGeni", descriptor = "(III)V")
		@JvmStatic actual fun glTexGeni(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glTexGeni(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteFramebuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual fun glDeleteFramebuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			EXTFramebufferObject.nglDeleteFramebuffersEXT(arg0, GlScratch.ints(arg1, arg2, arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorMask", descriptor = "(ZZZZ)V")
		@JvmStatic actual fun glColorMask(@OriginalArg(0) arg0: Boolean, @OriginalArg(1) arg1: Boolean, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: Boolean) {
			GL11.glColorMask(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCullFace", descriptor = "(I)V")
		@JvmStatic actual fun glCullFace(@OriginalArg(0) arg0: Int) {
			GL11.glCullFace(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLoadMatrixf", descriptor = "([FI)V")
		@JvmStatic actual fun glLoadMatrixf(@OriginalArg(0) arg0: FloatArray?, @OriginalArg(1) arg1: Int) {
			GL11.nglLoadMatrixf(GlScratch.floats(arg0, arg1, 16))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRasterPos2i", descriptor = "(II)V")
		@JvmStatic actual fun glRasterPos2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glRasterPos2i(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferDataARBa", descriptor = "(IIJI)V")
		@JvmStatic actual fun glBufferDataARBa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: Int) {
			ARBVertexBufferObject.nglBufferDataARB(arg0, arg1.toLong(), arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glStencilFunc", descriptor = "(III)V")
		@JvmStatic private fun glStencilFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glStencilFunc(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClearColor", descriptor = "(FFFF)V")
		@JvmStatic actual fun glClearColor(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float) {
			GL11.glClearColor(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRenderbufferStorageEXT", descriptor = "(IIII)V")
		@JvmStatic actual fun glRenderbufferStorageEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
			EXTFramebufferObject.glRenderbufferStorageEXT(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteBuffersARB", descriptor = "(I[II)V")
		@JvmStatic actual fun glDeleteBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			ARBVertexBufferObject.nglDeleteBuffersARB(arg0, GlScratch.ints(arg1, arg2, arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexSubImage3D", descriptor = "(IIIIIIIII)V")
		@JvmStatic actual fun glCopyTexSubImage3D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int) {
			GL12.glCopyTexSubImage3D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Df", descriptor = "(IIIIIIII[FI)V")
		@JvmStatic actual fun glTexImage2Df(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: FloatArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.floats(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadPixelsi", descriptor = "(IIIIII[II)V")
		@JvmStatic actual fun glReadPixelsi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: Int) {
			GL11.nglReadPixels(arg0, arg1, arg2, arg3, arg4, arg5, GlScratch.ints(arg6, arg7))
			GlScratch.readInts(arg6, arg7)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoordPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual fun glTexCoordPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long) {
			GL11.nglTexCoordPointer(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindTexture", descriptor = "(II)V")
		@JvmStatic actual fun glBindTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glBindTexture(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLinkProgramARB", descriptor = "(J)V")
		@JvmStatic actual fun glLinkProgramARB(@OriginalArg(0) arg0: Long) {
			ARBShaderObjects.glLinkProgramARB(arg0.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glOrtho", descriptor = "(DDDDDD)V")
		@JvmStatic actual fun glOrtho(@OriginalArg(0) arg0: Double, @OriginalArg(1) arg1: Double, @OriginalArg(2) arg2: Double, @OriginalArg(3) arg3: Double, @OriginalArg(4) arg4: Double, @OriginalArg(5) arg5: Double) {
			GL11.glOrtho(arg0, arg1, arg2, arg3, arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix3fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private fun glUniformMatrix3fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int) {
			ARBShaderObjects.nglUniformMatrix3fvARB(arg0, arg1, arg2, GlScratch.floats(arg3, arg4, arg1 * 9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix2fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private fun glUniformMatrix2fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int) {
			ARBShaderObjects.nglUniformMatrix2fvARB(arg0, arg1, arg2, GlScratch.floats(arg3, arg4, arg1 * 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferSubDataARBa", descriptor = "(IIIJ)V")
		@JvmStatic actual fun glBufferSubDataARBa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long) {
			ARBVertexBufferObject.nglBufferSubDataARB(arg0, arg1.toLong(), arg2.toLong(), arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteRenderbuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual fun glDeleteRenderbuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			EXTFramebufferObject.nglDeleteRenderbuffersEXT(arg0, GlScratch.ints(arg1, arg2, arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelStorei", descriptor = "(II)V")
		@JvmStatic actual fun glPixelStorei(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glPixelStorei(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glScissor", descriptor = "(IIII)V")
		@JvmStatic actual fun glScissor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
			GL11.glScissor(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord2i", descriptor = "(II)V")
		@JvmStatic private fun glTexCoord2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glTexCoord2i(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLineWidth", descriptor = "(F)V")
		@JvmStatic actual fun glLineWidth(@OriginalArg(0) arg0: Float) {
			GL11.glLineWidth(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenerateMipmapEXT", descriptor = "(I)V")
		@JvmStatic actual fun glGenerateMipmapEXT(@OriginalArg(0) arg0: Int) {
			EXTFramebufferObject.glGenerateMipmapEXT(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteObjectARB", descriptor = "(J)V")
		@JvmStatic actual fun glDeleteObjectARB(@OriginalArg(0) arg0: Long) {
			ARBShaderObjects.glDeleteObjectARB(arg0.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord2i", descriptor = "(III)V")
		@JvmStatic private fun glMultiTexCoord2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL13.glMultiTexCoord2i(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRotatef", descriptor = "(FFFF)V")
		@JvmStatic actual fun glRotatef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float) {
			GL11.glRotatef(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord2f", descriptor = "(IFF)V")
		@JvmStatic actual fun glMultiTexCoord2f(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL13.glMultiTexCoord2f(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferRenderbufferEXT", descriptor = "(IIII)V")
		@JvmStatic actual fun glFramebufferRenderbufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
			EXTFramebufferObject.glFramebufferRenderbufferEXT(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Di", descriptor = "(IIIIIIII[II)V")
		@JvmStatic actual fun glTexSubImage2Di(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexSubImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.ints(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightf", descriptor = "(IIF)V")
		@JvmStatic actual fun glLightf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float) {
			GL11.glLightf(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexGenfv", descriptor = "(II[FI)V")
		@JvmStatic actual fun glTexGenfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int) {
			GL11.nglTexGenfv(arg0, arg1, GlScratch.floats(arg2, arg3, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord3i", descriptor = "(III)V")
		@JvmStatic actual fun glTexCoord3i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glTexCoord3i(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawArrays", descriptor = "(III)V")
		@JvmStatic actual fun glDrawArrays(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glDrawArrays(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCallList", descriptor = "(I)V")
		@JvmStatic actual fun glCallList(@OriginalArg(0) arg0: Int) {
			GL11.glCallList(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteProgramARB", descriptor = "(I)V")
		@JvmStatic actual fun glDeleteProgramARB(@OriginalArg(0) arg0: Int) {
			ARBVertexProgram.nglDeleteProgramsARB(1, GlScratch.ints(intArrayOf(arg0), 0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindFramebufferEXT", descriptor = "(II)V")
		@JvmStatic actual fun glBindFramebufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			EXTFramebufferObject.glBindFramebufferEXT(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord3f", descriptor = "(FFF)V")
		@JvmStatic actual fun glTexCoord3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glTexCoord3f(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenBuffersARB", descriptor = "(I[II)V")
		@JvmStatic actual fun glGenBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			ARBVertexBufferObject.nglGenBuffersARB(arg0, GlScratch.ints(arg1, arg2, arg0))
			GlScratch.readInts(arg1, arg2, arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertexPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual fun glVertexPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long) {
			GL11.nglVertexPointer(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetInfoLogARB", descriptor = "(JI[II[BI)V")
		@JvmStatic actual fun glGetInfoLogARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int) {
			ARBShaderObjects.nglGetInfoLogARB(arg0.toInt(), arg1, GlScratch.space(4, 1), GlScratch.space(maxOf(arg1, 1), 0))
			GlScratch.readInts(arg2, arg3, 1, 1)
			GlScratch.readBytes(arg4, arg5, arg1, 0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadPixelsub", descriptor = "(IIIIII[BI)V")
		@JvmStatic private fun glReadPixelsub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: ByteArray?, @OriginalArg(7) arg7: Int) {
			GL11.nglReadPixels(arg0, arg1, arg2, arg3, arg4, arg5, GlScratch.bytes(arg6, arg7))
			GlScratch.readBytes(arg6, arg7)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDepthMask", descriptor = "(Z)V")
		@JvmStatic actual fun glDepthMask(@OriginalArg(0) arg0: Boolean) {
			GL11.glDepthMask(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnd", descriptor = "()V")
		@JvmStatic actual fun glEnd() {
			GL11.glEnd()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEndList", descriptor = "()V")
		@JvmStatic actual fun glEndList() {
			GL11.glEndList()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage3Dub", descriptor = "(IIIIIIIII[BI)V")
		@JvmStatic actual fun glTexImage3Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: ByteArray?, @OriginalArg(10) arg10: Int) {
			GL12.nglTexImage3D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, GlScratch.bytes(arg9, arg10))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage1Dub", descriptor = "(IIIIIII[BI)V")
		@JvmStatic actual fun glTexImage1Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: ByteArray?, @OriginalArg(8) arg8: Int) {
			GL11.nglTexImage1D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, GlScratch.bytes(arg7, arg8))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBlendFunc", descriptor = "(II)V")
		@JvmStatic actual fun glBlendFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glBlendFunc(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glStencilOp", descriptor = "(III)V")
		@JvmStatic private fun glStencilOp(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glStencilOp(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClearDepth", descriptor = "(F)V")
		@JvmStatic actual fun glClearDepth(@OriginalArg(0) arg0: Float) {
			GL11.glClearDepth(arg0.toDouble())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetUniformLocationARB", descriptor = "(JLjava/lang/String;)I")
		@JvmStatic actual fun glGetUniformLocationARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: String?): Int {
			return ARBShaderObjects.glGetUniformLocationARB(arg0.toInt(), arg1!!)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNewList", descriptor = "(II)V")
		@JvmStatic actual fun glNewList(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glNewList(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glViewport", descriptor = "(IIII)V")
		@JvmStatic actual fun glViewport(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
			GL11.glViewport(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvfv", descriptor = "(II[FI)V")
		@JvmStatic actual fun glTexEnvfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int) {
			GL11.nglTexEnvfv(arg0, arg1, GlScratch.floats(arg2, arg3, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPolygonMode", descriptor = "(II)V")
		@JvmStatic actual fun glPolygonMode(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glPolygonMode(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexParameteri", descriptor = "(III)V")
		@JvmStatic actual fun glTexParameteri(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glTexParameteri(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPushMatrix", descriptor = "()V")
		@JvmStatic actual fun glPushMatrix() {
			GL11.glPushMatrix()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelTransferf", descriptor = "(IF)V")
		@JvmStatic actual fun glPixelTransferf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float) {
			GL11.glPixelTransferf(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform1fARB", descriptor = "(IF)V")
		@JvmStatic actual fun glUniform1fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float) {
			ARBShaderObjects.glUniform1fARB(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBlitFramebufferEXT", descriptor = "(IIIIIIIIII)V")
		@JvmStatic actual fun glBlitFramebufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Int) {
			EXTFramebufferBlit.glBlitFramebufferEXT(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexSubImage2D", descriptor = "(IIIIIIII)V")
		@JvmStatic actual fun glCopyTexSubImage2D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int) {
			GL11.glCopyTexSubImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCompileShaderARB", descriptor = "(J)V")
		@JvmStatic actual fun glCompileShaderARB(@OriginalArg(0) arg0: Long) {
			ARBShaderObjects.glCompileShaderARB(arg0.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMaterialfv", descriptor = "(II[FI)V")
		@JvmStatic private fun glMaterialfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int) {
			GL11.nglMaterialfv(arg0, arg1, GlScratch.floats(arg2, arg3, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDetachObjectARB", descriptor = "(JJ)V")
		@JvmStatic actual fun glDetachObjectARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long) {
			ARBShaderObjects.glDetachObjectARB(arg0.toInt(), arg1.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNormalPointer", descriptor = "(IIJ)V")
		@JvmStatic actual fun glNormalPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Long) {
			GL11.nglNormalPointer(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform2fARB", descriptor = "(IFF)V")
		@JvmStatic actual fun glUniform2fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			ARBShaderObjects.glUniform2fARB(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightfv", descriptor = "(II[FI)V")
		@JvmStatic actual fun glLightfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int) {
			GL11.nglLightfv(arg0, arg1, GlScratch.floats(arg2, arg3, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogfv", descriptor = "(I[FI)V")
		@JvmStatic actual fun glFogfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglFogfv(arg0, GlScratch.floats(arg1, arg2, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetFloatv", descriptor = "(I[FI)V")
		@JvmStatic actual fun glGetFloatv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglGetFloatv(arg0, GlScratch.floats(arg1, arg2, 16))
			GlScratch.readFloats(arg1, arg2, 16)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glHint", descriptor = "(II)V")
		@JvmStatic actual fun glHint(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glHint(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferDataARBub", descriptor = "(II[BII)V")
		@JvmStatic actual fun glBufferDataARBub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: ByteArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
			ARBVertexBufferObject.nglBufferDataARB(arg0, arg1.toLong(), GlScratch.bytes(arg2, arg3, arg1, reserve = arg1), arg4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Df", descriptor = "(IIIIIIII[FI)V")
		@JvmStatic actual fun glTexSubImage2Df(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: FloatArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexSubImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.floats(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClientActiveTexture", descriptor = "(I)V")
		@JvmStatic actual fun glClientActiveTexture(@OriginalArg(0) arg0: Int) {
			GL13.glClientActiveTexture(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightModelfv", descriptor = "(I[FI)V")
		@JvmStatic actual fun glLightModelfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglLightModelfv(arg0, GlScratch.floats(arg1, arg2, 4))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexParameterf", descriptor = "(IIF)V")
		@JvmStatic private fun glTexParameterf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float) {
			GL11.glTexParameterf(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMatrixMode", descriptor = "(I)V")
		@JvmStatic actual fun glMatrixMode(@OriginalArg(0) arg0: Int) {
			GL11.glMatrixMode(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexImage2D", descriptor = "(IIIIIIII)V")
		@JvmStatic actual fun glCopyTexImage2D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int) {
			GL11.glCopyTexImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Di", descriptor = "(IIIIIIII[II)V")
		@JvmStatic actual fun glTexImage2Di(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.ints(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUnmapBufferARB", descriptor = "(I)Z")
		@JvmStatic actual fun glUnmapBufferARB(@OriginalArg(0) arg0: Int): Boolean {
			return ARBVertexBufferObject.glUnmapBufferARB(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor4ub", descriptor = "(BBBB)V")
		@JvmStatic actual fun glColor4ub(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte, @OriginalArg(3) arg3: Byte) {
			GL11.glColor4ub(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferSubDataARBub", descriptor = "(III[BI)V")
		@JvmStatic actual fun glBufferSubDataARBub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: ByteArray?, @OriginalArg(4) arg4: Int) {
			ARBVertexBufferObject.nglBufferSubDataARB(arg0, arg1.toLong(), arg2.toLong(), GlScratch.bytes(arg3, arg4, arg2, reserve = arg2))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultMatrixf", descriptor = "([FI)V")
		@JvmStatic actual fun glMultMatrixf(@OriginalArg(0) arg0: FloatArray?, @OriginalArg(1) arg1: Int) {
			GL11.nglMultMatrixf(GlScratch.floats(arg0, arg1, 16))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogi", descriptor = "(II)V")
		@JvmStatic actual fun glFogi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glFogi(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex2i", descriptor = "(II)V")
		@JvmStatic actual fun glVertex2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glVertex2i(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDepthFunc", descriptor = "(I)V")
		@JvmStatic actual fun glDepthFunc(@OriginalArg(0) arg0: Int) {
			GL11.glDepthFunc(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawPixelsi", descriptor = "(IIII[II)V")
		@JvmStatic private fun glDrawPixelsi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: Int) {
			GL11.nglDrawPixels(arg0, arg1, arg2, arg3, GlScratch.ints(arg4, arg5))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawPixelsub", descriptor = "(IIII[BI)V")
		@JvmStatic private fun glDrawPixelsub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int) {
			GL11.nglDrawPixels(arg0, arg1, arg2, arg3, GlScratch.bytes(arg4, arg5))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex3f", descriptor = "(FFF)V")
		@JvmStatic actual fun glVertex3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glVertex3f(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform4fARB", descriptor = "(IFFFF)V")
		@JvmStatic actual fun glUniform4fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float) {
			ARBShaderObjects.glUniform4fARB(arg0, arg1, arg2, arg3, arg4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFrustum", descriptor = "(DDDDDD)V")
		@JvmStatic private fun glFrustum(@OriginalArg(0) arg0: Double, @OriginalArg(1) arg1: Double, @OriginalArg(2) arg2: Double, @OriginalArg(3) arg3: Double, @OriginalArg(4) arg4: Double, @OriginalArg(5) arg5: Double) {
			GL11.glFrustum(arg0, arg1, arg2, arg3, arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvi", descriptor = "(III)V")
		@JvmStatic actual fun glTexEnvi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
			GL11.glTexEnvi(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCreateProgramObjectARB", descriptor = "()J")
		@JvmStatic actual fun glCreateProgramObjectARB(): Long {
			return handle(ARBShaderObjects.glCreateProgramObjectARB())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNormal3f", descriptor = "(FFF)V")
		@JvmStatic actual fun glNormal3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glNormal3f(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetTexImageub", descriptor = "(IIII[BI)V")
		@JvmStatic private fun glGetTexImageub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int) {
			GL11.nglGetTexImage(arg0, arg1, arg2, arg3, GlScratch.bytes(arg4, arg5))
			GlScratch.readBytes(arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenFramebuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual fun glGenFramebuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			EXTFramebufferObject.nglGenFramebuffersEXT(arg0, GlScratch.ints(arg1, arg2, arg0))
			GlScratch.readInts(arg1, arg2, arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetProgramivARB", descriptor = "(II[II)V")
		@JvmStatic private fun glGetProgramivARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int) {
			ARBVertexProgram.nglGetProgramivARB(arg0, arg1, GlScratch.ints(arg2, arg3, 4))
			GlScratch.readInts(arg2, arg3, 4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform3fARB", descriptor = "(IFFF)V")
		@JvmStatic actual fun glUniform3fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float) {
			ARBShaderObjects.glUniform3fARB(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawBuffersARB", descriptor = "(I[II)V")
		@JvmStatic private fun glDrawBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			ARBDrawBuffers.nglDrawBuffersARB(arg0, GlScratch.ints(arg1, arg2, arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCreateShaderObjectARB", descriptor = "(I)J")
		@JvmStatic actual fun glCreateShaderObjectARB(@OriginalArg(0) arg0: Int): Long {
			return handle(ARBShaderObjects.glCreateShaderObjectARB(arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMapBufferARB", descriptor = "(II)J")
		@JvmStatic actual fun glMapBufferARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Long {
			return ARBVertexBufferObject.nglMapBufferARB(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor3ub", descriptor = "(BBB)V")
		@JvmStatic private fun glColor3ub(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte) {
			GL11.glColor3ub(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetIntegerv", descriptor = "(I[II)V")
		@JvmStatic actual fun glGetIntegerv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglGetIntegerv(arg0, GlScratch.ints(arg1, arg2, 16))
			GlScratch.readInts(arg1, arg2, 16)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShadeModel", descriptor = "(I)V")
		@JvmStatic actual fun glShadeModel(@OriginalArg(0) arg0: Int) {
			GL11.glShadeModel(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramStringARB", descriptor = "(IILjava/lang/String;)V")
		@JvmStatic actual fun glProgramStringARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: String?) {
			programString(arg0, arg1, arg2!!.toByteArray(Charsets.ISO_8859_1))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenProgramARB", descriptor = "()I")
		@JvmStatic actual fun glGenProgramARB(): Int {
			return ARBVertexProgram.glGenProgramsARB()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPushAttrib", descriptor = "(I)V")
		@JvmStatic actual fun glPushAttrib(@OriginalArg(0) arg0: Int) {
			GL11.glPushAttrib(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFlush", descriptor = "()V")
		@JvmStatic actual fun glFlush() {
			GL11.glFlush()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRenderbufferStorageMultisampleEXT", descriptor = "(IIIII)V")
		@JvmStatic actual fun glRenderbufferStorageMultisampleEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
			EXTFramebufferMultisample.glRenderbufferStorageMultisampleEXT(arg0, arg1, arg2, arg3, arg4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnable", descriptor = "(I)V")
		@JvmStatic actual fun glEnable(@OriginalArg(0) arg0: Int) {
			GL11.glEnable(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDisable", descriptor = "(I)V")
		@JvmStatic actual fun glDisable(@OriginalArg(0) arg0: Int) {
			GL11.glDisable(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenTextures", descriptor = "(I[II)V")
		@JvmStatic actual fun glGenTextures(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglGenTextures(arg0, GlScratch.ints(arg1, arg2, arg0))
			GlScratch.readInts(arg1, arg2, arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferTexture2DEXT", descriptor = "(IIIII)V")
		@JvmStatic actual fun glFramebufferTexture2DEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
			EXTFramebufferObject.glFramebufferTexture2DEXT(arg0, arg1, arg2, arg3, arg4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindBufferARB", descriptor = "(II)V")
		@JvmStatic actual fun glBindBufferARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			ARBVertexBufferObject.glBindBufferARB(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord3i", descriptor = "(IIII)V")
		@JvmStatic actual fun glMultiTexCoord3i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
			GL13.glMultiTexCoord3i(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorMaterial", descriptor = "(II)V")
		@JvmStatic actual fun glColorMaterial(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			GL11.glColorMaterial(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Dub", descriptor = "(IIIIIIII[BI)V")
		@JvmStatic actual fun glTexSubImage2Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: ByteArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexSubImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.bytes(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShaderSourceARB", descriptor = "(JLjava/lang/String;)V")
		@JvmStatic actual fun glShaderSourceARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: String?) {
			ARBShaderObjects.glShaderSourceARB(arg0.toInt(), arg1!!)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetTexImagei", descriptor = "(IIII[II)V")
		@JvmStatic actual fun glGetTexImagei(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: Int) {
			GL11.nglGetTexImage(arg0, arg1, arg2, arg3, GlScratch.ints(arg4, arg5))
			GlScratch.readInts(arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetString", descriptor = "(I)Ljava/lang/String;")
		@JvmStatic actual fun glGetString(@OriginalArg(0) arg0: Int): String? {
			return GL11.glGetString(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelZoom", descriptor = "(FF)V")
		@JvmStatic private fun glPixelZoom(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float) {
			GL11.glPixelZoom(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDisableClientState", descriptor = "(I)V")
		@JvmStatic actual fun glDisableClientState(@OriginalArg(0) arg0: Int) {
			GL11.glDisableClientState(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClear", descriptor = "(I)V")
		@JvmStatic actual fun glClear(@OriginalArg(0) arg0: Int) {
			GL11.glClear(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPointSize", descriptor = "(F)V")
		@JvmStatic actual fun glPointSize(@OriginalArg(0) arg0: Float) {
			GL11.glPointSize(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyPixels", descriptor = "(IIIII)V")
		@JvmStatic actual fun glCopyPixels(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
			GL11.glCopyPixels(arg0, arg1, arg2, arg3, arg4)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord2f", descriptor = "(FF)V")
		@JvmStatic actual fun glTexCoord2f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float) {
			GL11.glTexCoord2f(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindProgramARB", descriptor = "(II)V")
		@JvmStatic actual fun glBindProgramARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
			ARBVertexProgram.glBindProgramARB(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenRenderbuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual fun glGenRenderbuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			EXTFramebufferObject.nglGenRenderbuffersEXT(arg0, GlScratch.ints(arg1, arg2, arg0))
			GlScratch.readInts(arg1, arg2, arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPopAttrib", descriptor = "()V")
		@JvmStatic actual fun glPopAttrib() {
			GL11.glPopAttrib()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex2f", descriptor = "(FF)V")
		@JvmStatic actual fun glVertex2f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float) {
			GL11.glVertex2f(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLoadIdentity", descriptor = "()V")
		@JvmStatic actual fun glLoadIdentity() {
			GL11.glLoadIdentity()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenLists", descriptor = "(I)I")
		@JvmStatic actual fun glGenLists(@OriginalArg(0) arg0: Int): Int {
			return GL11.glGenLists(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual fun glColorPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long) {
			GL11.nglColorPointer(arg0, arg1, arg2, arg3)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPopMatrix", descriptor = "()V")
		@JvmStatic actual fun glPopMatrix() {
			GL11.glPopMatrix()
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnableClientState", descriptor = "(I)V")
		@JvmStatic actual fun glEnableClientState(@OriginalArg(0) arg0: Int) {
			GL11.glEnableClientState(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShaderSourceRawARB", descriptor = "(J[B)V")
		@JvmStatic actual fun glShaderSourceRawARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: ByteArray?) {
			shaderSource(arg0.toInt(), arg1!!)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor3f", descriptor = "(FFF)V")
		@JvmStatic actual fun glColor3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
			GL11.glColor3f(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvf", descriptor = "(IIF)V")
		@JvmStatic actual fun glTexEnvf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float) {
			GL11.glTexEnvf(arg0, arg1, arg2)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramLocalParameter4fARB", descriptor = "(IIFFFF)V")
		@JvmStatic actual fun glProgramLocalParameter4fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float) {
			ARBVertexProgram.glProgramLocalParameter4fARB(arg0, arg1, arg2, arg3, arg4, arg5)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glAlphaFunc", descriptor = "(IF)V")
		@JvmStatic actual fun glAlphaFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float) {
			GL11.glAlphaFunc(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteTextures", descriptor = "(I[II)V")
		@JvmStatic actual fun glDeleteTextures(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int) {
			GL11.nglDeleteTextures(arg0, GlScratch.ints(arg1, arg2, arg0))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawBuffer", descriptor = "(I)V")
		@JvmStatic actual fun glDrawBuffer(@OriginalArg(0) arg0: Int) {
			GL11.glDrawBuffer(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBegin", descriptor = "(I)V")
		@JvmStatic actual fun glBegin(@OriginalArg(0) arg0: Int) {
			GL11.glBegin(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogf", descriptor = "(IF)V")
		@JvmStatic actual fun glFogf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float) {
			GL11.glFogf(arg0, arg1)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glAttachObjectARB", descriptor = "(JJ)V")
		@JvmStatic actual fun glAttachObjectARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long) {
			ARBShaderObjects.glAttachObjectARB(arg0.toInt(), arg1.toInt())
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCheckFramebufferStatusEXT", descriptor = "(I)I")
		@JvmStatic actual fun glCheckFramebufferStatusEXT(@OriginalArg(0) arg0: Int): Int {
			return EXTFramebufferObject.glCheckFramebufferStatusEXT(arg0)
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Dub", descriptor = "(IIIIIIII[BI)V")
		@JvmStatic actual fun glTexImage2Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: ByteArray?, @OriginalArg(9) arg9: Int) {
			GL11.nglTexImage2D(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, GlScratch.bytes(arg8, arg9))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix4fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private fun glUniformMatrix4fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int) {
			ARBShaderObjects.nglUniformMatrix4fvARB(arg0, arg1, arg2, GlScratch.floats(arg3, arg4, arg1 * 16))
		}

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramRawARB", descriptor = "(II[B)V")
		@JvmStatic actual fun glProgramRawARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: ByteArray?) {
			programString(arg0, arg1, arg2!!)
		}
	}
}
