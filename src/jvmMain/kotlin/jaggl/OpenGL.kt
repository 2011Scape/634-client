package jaggl

import java.awt.Canvas
import java.util.Hashtable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jaggl/OpenGL")
actual class OpenGL actual constructor() {

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "a", descriptor = "Ljava/lang/Thread;")
	private var a: Thread? = null

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "c", descriptor = "Ljava/util/Hashtable;")
	private var c: Hashtable<Any?, Any?>? = null

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "detachPeer", descriptor = "()V")
	private external fun detachPeer()

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setSwapInterval", descriptor = "(I)V")
	actual external fun setSwapInterval(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "surfaceResized", descriptor = "(J)V")
	actual external fun surfaceResized(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setPbuffer", descriptor = "(J)V")
	private external fun setPbuffer(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "arePbuffersAvailable", descriptor = "()Z")
	actual external fun arePbuffersAvailable(): Boolean

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
	private external fun releasePbuffer(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "attachPeer", descriptor = "()Z")
	private external fun attachPeer(): Boolean

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "releaseSurface", descriptor = "(Ljava/awt/Canvas;J)V")
	actual external fun releaseSurface(@OriginalArg(0) arg0: Canvas?, @OriginalArg(1) arg1: Long)

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "init", descriptor = "(Ljava/awt/Canvas;IIIIII)J")
	actual external fun init(@OriginalArg(0) arg0: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int): Long

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
	private external fun createPbuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Long

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "swapBuffers", descriptor = "()V")
	actual external fun swapBuffers()

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
	actual external fun release()

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "prepareSurface", descriptor = "(Ljava/awt/Canvas;)J")
	actual external fun prepareSurface(@OriginalArg(0) arg0: Canvas?): Long

	@OriginalMember(owner = "client!jaggl/OpenGL", name = "setSurface", descriptor = "(J)Z")
	actual external fun setSurface(@OriginalArg(0) arg0: Long): Boolean

	actual companion object {

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "b", descriptor = "Ljava/util/Hashtable;")
		private val b: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTranslatef", descriptor = "(FFF)V")
		@JvmStatic actual external fun glTranslatef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindRenderbufferEXT", descriptor = "(II)V")
		@JvmStatic actual external fun glBindRenderbufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glScalef", descriptor = "(FFF)V")
		@JvmStatic actual external fun glScalef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glActiveTexture", descriptor = "(I)V")
		@JvmStatic actual external fun glActiveTexture(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramLocalParameter4fvARB", descriptor = "(II[FI)V")
		@JvmStatic actual external fun glProgramLocalParameter4fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetObjectParameterivARB", descriptor = "(JI[II)V")
		@JvmStatic actual external fun glGetObjectParameterivARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferTexture3DEXT", descriptor = "(IIIIII)V")
		@JvmStatic actual external fun glFramebufferTexture3DEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteLists", descriptor = "(II)V")
		@JvmStatic actual external fun glDeleteLists(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform1iARB", descriptor = "(II)V")
		@JvmStatic actual external fun glUniform1iARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFinish", descriptor = "()V")
		@JvmStatic actual external fun glFinish()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawElements", descriptor = "(IIIJ)V")
		@JvmStatic actual external fun glDrawElements(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadBuffer", descriptor = "(I)V")
		@JvmStatic actual external fun glReadBuffer(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetError", descriptor = "()I")
		@JvmStatic private external fun glGetError(): Int

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUseProgramObjectARB", descriptor = "(J)V")
		@JvmStatic actual external fun glUseProgramObjectARB(@OriginalArg(0) arg0: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor4f", descriptor = "(FFFF)V")
		@JvmStatic private external fun glColor4f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexGeni", descriptor = "(III)V")
		@JvmStatic actual external fun glTexGeni(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteFramebuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual external fun glDeleteFramebuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorMask", descriptor = "(ZZZZ)V")
		@JvmStatic actual external fun glColorMask(@OriginalArg(0) arg0: Boolean, @OriginalArg(1) arg1: Boolean, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: Boolean)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCullFace", descriptor = "(I)V")
		@JvmStatic actual external fun glCullFace(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLoadMatrixf", descriptor = "([FI)V")
		@JvmStatic actual external fun glLoadMatrixf(@OriginalArg(0) arg0: FloatArray?, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRasterPos2i", descriptor = "(II)V")
		@JvmStatic actual external fun glRasterPos2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferDataARBa", descriptor = "(IIJI)V")
		@JvmStatic actual external fun glBufferDataARBa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glStencilFunc", descriptor = "(III)V")
		@JvmStatic private external fun glStencilFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClearColor", descriptor = "(FFFF)V")
		@JvmStatic actual external fun glClearColor(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRenderbufferStorageEXT", descriptor = "(IIII)V")
		@JvmStatic actual external fun glRenderbufferStorageEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteBuffersARB", descriptor = "(I[II)V")
		@JvmStatic actual external fun glDeleteBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexSubImage3D", descriptor = "(IIIIIIIII)V")
		@JvmStatic actual external fun glCopyTexSubImage3D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Df", descriptor = "(IIIIIIII[FI)V")
		@JvmStatic actual external fun glTexImage2Df(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: FloatArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadPixelsi", descriptor = "(IIIIII[II)V")
		@JvmStatic actual external fun glReadPixelsi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoordPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual external fun glTexCoordPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindTexture", descriptor = "(II)V")
		@JvmStatic actual external fun glBindTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLinkProgramARB", descriptor = "(J)V")
		@JvmStatic actual external fun glLinkProgramARB(@OriginalArg(0) arg0: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glOrtho", descriptor = "(DDDDDD)V")
		@JvmStatic actual external fun glOrtho(@OriginalArg(0) arg0: Double, @OriginalArg(1) arg1: Double, @OriginalArg(2) arg2: Double, @OriginalArg(3) arg3: Double, @OriginalArg(4) arg4: Double, @OriginalArg(5) arg5: Double)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix3fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private external fun glUniformMatrix3fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix2fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private external fun glUniformMatrix2fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferSubDataARBa", descriptor = "(IIIJ)V")
		@JvmStatic actual external fun glBufferSubDataARBa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteRenderbuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual external fun glDeleteRenderbuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelStorei", descriptor = "(II)V")
		@JvmStatic actual external fun glPixelStorei(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glScissor", descriptor = "(IIII)V")
		@JvmStatic actual external fun glScissor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord2i", descriptor = "(II)V")
		@JvmStatic private external fun glTexCoord2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLineWidth", descriptor = "(F)V")
		@JvmStatic actual external fun glLineWidth(@OriginalArg(0) arg0: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenerateMipmapEXT", descriptor = "(I)V")
		@JvmStatic actual external fun glGenerateMipmapEXT(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteObjectARB", descriptor = "(J)V")
		@JvmStatic actual external fun glDeleteObjectARB(@OriginalArg(0) arg0: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord2i", descriptor = "(III)V")
		@JvmStatic private external fun glMultiTexCoord2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRotatef", descriptor = "(FFFF)V")
		@JvmStatic actual external fun glRotatef(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord2f", descriptor = "(IFF)V")
		@JvmStatic actual external fun glMultiTexCoord2f(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferRenderbufferEXT", descriptor = "(IIII)V")
		@JvmStatic actual external fun glFramebufferRenderbufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Di", descriptor = "(IIIIIIII[II)V")
		@JvmStatic actual external fun glTexSubImage2Di(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightf", descriptor = "(IIF)V")
		@JvmStatic actual external fun glLightf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexGenfv", descriptor = "(II[FI)V")
		@JvmStatic actual external fun glTexGenfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord3i", descriptor = "(III)V")
		@JvmStatic actual external fun glTexCoord3i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawArrays", descriptor = "(III)V")
		@JvmStatic actual external fun glDrawArrays(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCallList", descriptor = "(I)V")
		@JvmStatic actual external fun glCallList(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteProgramARB", descriptor = "(I)V")
		@JvmStatic actual external fun glDeleteProgramARB(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindFramebufferEXT", descriptor = "(II)V")
		@JvmStatic actual external fun glBindFramebufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord3f", descriptor = "(FFF)V")
		@JvmStatic actual external fun glTexCoord3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenBuffersARB", descriptor = "(I[II)V")
		@JvmStatic actual external fun glGenBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertexPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual external fun glVertexPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetInfoLogARB", descriptor = "(JI[II[BI)V")
		@JvmStatic actual external fun glGetInfoLogARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glReadPixelsub", descriptor = "(IIIIII[BI)V")
		@JvmStatic private external fun glReadPixelsub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: ByteArray?, @OriginalArg(7) arg7: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDepthMask", descriptor = "(Z)V")
		@JvmStatic actual external fun glDepthMask(@OriginalArg(0) arg0: Boolean)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnd", descriptor = "()V")
		@JvmStatic actual external fun glEnd()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEndList", descriptor = "()V")
		@JvmStatic actual external fun glEndList()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage3Dub", descriptor = "(IIIIIIIII[BI)V")
		@JvmStatic actual external fun glTexImage3Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: ByteArray?, @OriginalArg(10) arg10: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage1Dub", descriptor = "(IIIIIII[BI)V")
		@JvmStatic actual external fun glTexImage1Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: ByteArray?, @OriginalArg(8) arg8: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBlendFunc", descriptor = "(II)V")
		@JvmStatic actual external fun glBlendFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glStencilOp", descriptor = "(III)V")
		@JvmStatic private external fun glStencilOp(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClearDepth", descriptor = "(F)V")
		@JvmStatic actual external fun glClearDepth(@OriginalArg(0) arg0: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetUniformLocationARB", descriptor = "(JLjava/lang/String;)I")
		@JvmStatic actual external fun glGetUniformLocationARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: String?): Int

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNewList", descriptor = "(II)V")
		@JvmStatic actual external fun glNewList(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glViewport", descriptor = "(IIII)V")
		@JvmStatic actual external fun glViewport(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvfv", descriptor = "(II[FI)V")
		@JvmStatic actual external fun glTexEnvfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPolygonMode", descriptor = "(II)V")
		@JvmStatic actual external fun glPolygonMode(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexParameteri", descriptor = "(III)V")
		@JvmStatic actual external fun glTexParameteri(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPushMatrix", descriptor = "()V")
		@JvmStatic actual external fun glPushMatrix()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelTransferf", descriptor = "(IF)V")
		@JvmStatic actual external fun glPixelTransferf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform1fARB", descriptor = "(IF)V")
		@JvmStatic actual external fun glUniform1fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBlitFramebufferEXT", descriptor = "(IIIIIIIIII)V")
		@JvmStatic actual external fun glBlitFramebufferEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexSubImage2D", descriptor = "(IIIIIIII)V")
		@JvmStatic actual external fun glCopyTexSubImage2D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCompileShaderARB", descriptor = "(J)V")
		@JvmStatic actual external fun glCompileShaderARB(@OriginalArg(0) arg0: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMaterialfv", descriptor = "(II[FI)V")
		@JvmStatic private external fun glMaterialfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDetachObjectARB", descriptor = "(JJ)V")
		@JvmStatic actual external fun glDetachObjectARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNormalPointer", descriptor = "(IIJ)V")
		@JvmStatic actual external fun glNormalPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform2fARB", descriptor = "(IFF)V")
		@JvmStatic actual external fun glUniform2fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightfv", descriptor = "(II[FI)V")
		@JvmStatic actual external fun glLightfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: FloatArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogfv", descriptor = "(I[FI)V")
		@JvmStatic actual external fun glFogfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetFloatv", descriptor = "(I[FI)V")
		@JvmStatic actual external fun glGetFloatv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glHint", descriptor = "(II)V")
		@JvmStatic actual external fun glHint(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferDataARBub", descriptor = "(II[BII)V")
		@JvmStatic actual external fun glBufferDataARBub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: ByteArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Df", descriptor = "(IIIIIIII[FI)V")
		@JvmStatic actual external fun glTexSubImage2Df(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: FloatArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClientActiveTexture", descriptor = "(I)V")
		@JvmStatic actual external fun glClientActiveTexture(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLightModelfv", descriptor = "(I[FI)V")
		@JvmStatic actual external fun glLightModelfv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexParameterf", descriptor = "(IIF)V")
		@JvmStatic private external fun glTexParameterf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMatrixMode", descriptor = "(I)V")
		@JvmStatic actual external fun glMatrixMode(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyTexImage2D", descriptor = "(IIIIIIII)V")
		@JvmStatic actual external fun glCopyTexImage2D(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Di", descriptor = "(IIIIIIII[II)V")
		@JvmStatic actual external fun glTexImage2Di(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: IntArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUnmapBufferARB", descriptor = "(I)Z")
		@JvmStatic actual external fun glUnmapBufferARB(@OriginalArg(0) arg0: Int): Boolean

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor4ub", descriptor = "(BBBB)V")
		@JvmStatic actual external fun glColor4ub(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte, @OriginalArg(3) arg3: Byte)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBufferSubDataARBub", descriptor = "(III[BI)V")
		@JvmStatic actual external fun glBufferSubDataARBub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: ByteArray?, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultMatrixf", descriptor = "([FI)V")
		@JvmStatic actual external fun glMultMatrixf(@OriginalArg(0) arg0: FloatArray?, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogi", descriptor = "(II)V")
		@JvmStatic actual external fun glFogi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex2i", descriptor = "(II)V")
		@JvmStatic actual external fun glVertex2i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDepthFunc", descriptor = "(I)V")
		@JvmStatic actual external fun glDepthFunc(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawPixelsi", descriptor = "(IIII[II)V")
		@JvmStatic private external fun glDrawPixelsi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawPixelsub", descriptor = "(IIII[BI)V")
		@JvmStatic private external fun glDrawPixelsub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex3f", descriptor = "(FFF)V")
		@JvmStatic actual external fun glVertex3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform4fARB", descriptor = "(IFFFF)V")
		@JvmStatic actual external fun glUniform4fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFrustum", descriptor = "(DDDDDD)V")
		@JvmStatic private external fun glFrustum(@OriginalArg(0) arg0: Double, @OriginalArg(1) arg1: Double, @OriginalArg(2) arg2: Double, @OriginalArg(3) arg3: Double, @OriginalArg(4) arg4: Double, @OriginalArg(5) arg5: Double)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvi", descriptor = "(III)V")
		@JvmStatic actual external fun glTexEnvi(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCreateProgramObjectARB", descriptor = "()J")
		@JvmStatic actual external fun glCreateProgramObjectARB(): Long

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glNormal3f", descriptor = "(FFF)V")
		@JvmStatic actual external fun glNormal3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetTexImageub", descriptor = "(IIII[BI)V")
		@JvmStatic private external fun glGetTexImageub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: ByteArray?, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenFramebuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual external fun glGenFramebuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetProgramivARB", descriptor = "(II[II)V")
		@JvmStatic private external fun glGetProgramivARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniform3fARB", descriptor = "(IFFF)V")
		@JvmStatic actual external fun glUniform3fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawBuffersARB", descriptor = "(I[II)V")
		@JvmStatic private external fun glDrawBuffersARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCreateShaderObjectARB", descriptor = "(I)J")
		@JvmStatic actual external fun glCreateShaderObjectARB(@OriginalArg(0) arg0: Int): Long

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMapBufferARB", descriptor = "(II)J")
		@JvmStatic actual external fun glMapBufferARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Long

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor3ub", descriptor = "(BBB)V")
		@JvmStatic private external fun glColor3ub(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetIntegerv", descriptor = "(I[II)V")
		@JvmStatic actual external fun glGetIntegerv(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShadeModel", descriptor = "(I)V")
		@JvmStatic actual external fun glShadeModel(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramStringARB", descriptor = "(IILjava/lang/String;)V")
		@JvmStatic actual external fun glProgramStringARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: String?)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenProgramARB", descriptor = "()I")
		@JvmStatic actual external fun glGenProgramARB(): Int

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPushAttrib", descriptor = "(I)V")
		@JvmStatic actual external fun glPushAttrib(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFlush", descriptor = "()V")
		@JvmStatic actual external fun glFlush()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glRenderbufferStorageMultisampleEXT", descriptor = "(IIIII)V")
		@JvmStatic actual external fun glRenderbufferStorageMultisampleEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnable", descriptor = "(I)V")
		@JvmStatic actual external fun glEnable(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDisable", descriptor = "(I)V")
		@JvmStatic actual external fun glDisable(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenTextures", descriptor = "(I[II)V")
		@JvmStatic actual external fun glGenTextures(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFramebufferTexture2DEXT", descriptor = "(IIIII)V")
		@JvmStatic actual external fun glFramebufferTexture2DEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindBufferARB", descriptor = "(II)V")
		@JvmStatic actual external fun glBindBufferARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glMultiTexCoord3i", descriptor = "(IIII)V")
		@JvmStatic actual external fun glMultiTexCoord3i(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorMaterial", descriptor = "(II)V")
		@JvmStatic actual external fun glColorMaterial(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexSubImage2Dub", descriptor = "(IIIIIIII[BI)V")
		@JvmStatic actual external fun glTexSubImage2Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: ByteArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShaderSourceARB", descriptor = "(JLjava/lang/String;)V")
		@JvmStatic actual external fun glShaderSourceARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: String?)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetTexImagei", descriptor = "(IIII[II)V")
		@JvmStatic actual external fun glGetTexImagei(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGetString", descriptor = "(I)Ljava/lang/String;")
		@JvmStatic actual external fun glGetString(@OriginalArg(0) arg0: Int): String?

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPixelZoom", descriptor = "(FF)V")
		@JvmStatic private external fun glPixelZoom(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDisableClientState", descriptor = "(I)V")
		@JvmStatic actual external fun glDisableClientState(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glClear", descriptor = "(I)V")
		@JvmStatic actual external fun glClear(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPointSize", descriptor = "(F)V")
		@JvmStatic actual external fun glPointSize(@OriginalArg(0) arg0: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCopyPixels", descriptor = "(IIIII)V")
		@JvmStatic actual external fun glCopyPixels(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexCoord2f", descriptor = "(FF)V")
		@JvmStatic actual external fun glTexCoord2f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBindProgramARB", descriptor = "(II)V")
		@JvmStatic actual external fun glBindProgramARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenRenderbuffersEXT", descriptor = "(I[II)V")
		@JvmStatic actual external fun glGenRenderbuffersEXT(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPopAttrib", descriptor = "()V")
		@JvmStatic actual external fun glPopAttrib()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glVertex2f", descriptor = "(FF)V")
		@JvmStatic actual external fun glVertex2f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glLoadIdentity", descriptor = "()V")
		@JvmStatic actual external fun glLoadIdentity()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glGenLists", descriptor = "(I)I")
		@JvmStatic actual external fun glGenLists(@OriginalArg(0) arg0: Int): Int

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColorPointer", descriptor = "(IIIJ)V")
		@JvmStatic actual external fun glColorPointer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glPopMatrix", descriptor = "()V")
		@JvmStatic actual external fun glPopMatrix()

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glEnableClientState", descriptor = "(I)V")
		@JvmStatic actual external fun glEnableClientState(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glShaderSourceRawARB", descriptor = "(J[B)V")
		@JvmStatic actual external fun glShaderSourceRawARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: ByteArray?)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glColor3f", descriptor = "(FFF)V")
		@JvmStatic actual external fun glColor3f(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexEnvf", descriptor = "(IIF)V")
		@JvmStatic actual external fun glTexEnvf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramLocalParameter4fARB", descriptor = "(IIFFFF)V")
		@JvmStatic actual external fun glProgramLocalParameter4fARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glAlphaFunc", descriptor = "(IF)V")
		@JvmStatic actual external fun glAlphaFunc(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDeleteTextures", descriptor = "(I[II)V")
		@JvmStatic actual external fun glDeleteTextures(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glDrawBuffer", descriptor = "(I)V")
		@JvmStatic actual external fun glDrawBuffer(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glBegin", descriptor = "(I)V")
		@JvmStatic actual external fun glBegin(@OriginalArg(0) arg0: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glFogf", descriptor = "(IF)V")
		@JvmStatic actual external fun glFogf(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glAttachObjectARB", descriptor = "(JJ)V")
		@JvmStatic actual external fun glAttachObjectARB(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glCheckFramebufferStatusEXT", descriptor = "(I)I")
		@JvmStatic actual external fun glCheckFramebufferStatusEXT(@OriginalArg(0) arg0: Int): Int

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glTexImage2Dub", descriptor = "(IIIIIIII[BI)V")
		@JvmStatic actual external fun glTexImage2Dub(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: ByteArray?, @OriginalArg(9) arg9: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glUniformMatrix4fvARB", descriptor = "(IIZ[FI)V")
		@JvmStatic private external fun glUniformMatrix4fvARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) arg3: FloatArray?, @OriginalArg(4) arg4: Int)

		@OriginalMember(owner = "client!jaggl/OpenGL", name = "glProgramRawARB", descriptor = "(II[B)V")
		@JvmStatic actual external fun glProgramRawARB(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: ByteArray?)
	}
}
