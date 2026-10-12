/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaggl

import NativeUnavailableException
import awt.Canvas
import nativeUnavailable
import util.Hashtable

actual class OpenGL {
	actual constructor() {
		throw NativeUnavailableException("OpenGL")
	}
	actual fun setSwapInterval(arg0: Int) {
		throw NativeUnavailableException("setSwapInterval")
	}
	actual fun surfaceResized(arg0: Long) {
		throw NativeUnavailableException("surfaceResized")
	}
	actual fun arePbuffersAvailable(): Boolean {
		throw NativeUnavailableException("arePbuffersAvailable")
	}
	actual fun a(): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun releaseSurface(arg0: Canvas?, arg1: Long) {
		throw NativeUnavailableException("releaseSurface")
	}
	actual fun init(arg0: Canvas?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): Long {
		throw NativeUnavailableException("init")
	}
	actual fun b(): Boolean {
		throw NativeUnavailableException("b")
	}
	actual fun swapBuffers() {
		throw NativeUnavailableException("swapBuffers")
	}
	actual fun a(string: String?): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun release() {
		throw NativeUnavailableException("release")
	}
	actual fun prepareSurface(arg0: Canvas?): Long {
		throw NativeUnavailableException("prepareSurface")
	}
	actual fun setSurface(arg0: Long): Boolean {
		throw NativeUnavailableException("setSurface")
	}
	actual companion object {
		actual fun glTranslatef(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glTranslatef")
		}
		actual fun glBindRenderbufferEXT(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBindRenderbufferEXT")
		}
		actual fun glScalef(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glScalef")
		}
		actual fun glActiveTexture(arg0: Int) {
			throw NativeUnavailableException("glActiveTexture")
		}
		actual fun glProgramLocalParameter4fvARB(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
			throw NativeUnavailableException("glProgramLocalParameter4fvARB")
		}
		actual fun glGetObjectParameterivARB(arg0: Long, arg1: Int, arg2: IntArray?, arg3: Int) {
			throw NativeUnavailableException("glGetObjectParameterivARB")
		}
		actual fun glFramebufferTexture3DEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
			throw NativeUnavailableException("glFramebufferTexture3DEXT")
		}
		actual fun glDeleteLists(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glDeleteLists")
		}
		actual fun glUniform1iARB(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glUniform1iARB")
		}
		actual fun glFinish() {
			throw NativeUnavailableException("glFinish")
		}
		actual fun glDrawElements(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
			throw NativeUnavailableException("glDrawElements")
		}
		actual fun glReadBuffer(arg0: Int) {
			throw NativeUnavailableException("glReadBuffer")
		}
		actual fun glUseProgramObjectARB(arg0: Long) {
			throw NativeUnavailableException("glUseProgramObjectARB")
		}
		actual fun glTexGeni(arg0: Int, arg1: Int, arg2: Int) {
			throw NativeUnavailableException("glTexGeni")
		}
		actual fun glDeleteFramebuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glDeleteFramebuffersEXT")
		}
		actual fun glColorMask(arg0: Boolean, arg1: Boolean, arg2: Boolean, arg3: Boolean) {
			throw NativeUnavailableException("glColorMask")
		}
		actual fun glCullFace(arg0: Int) {
			throw NativeUnavailableException("glCullFace")
		}
		actual fun glLoadMatrixf(arg0: FloatArray?, arg1: Int) {
			throw NativeUnavailableException("glLoadMatrixf")
		}
		actual fun glRasterPos2i(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glRasterPos2i")
		}
		actual fun glBufferDataARBa(arg0: Int, arg1: Int, arg2: Long, arg3: Int) {
			throw NativeUnavailableException("glBufferDataARBa")
		}
		actual fun glClearColor(arg0: Float, arg1: Float, arg2: Float, arg3: Float) {
			throw NativeUnavailableException("glClearColor")
		}
		actual fun glRenderbufferStorageEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
			throw NativeUnavailableException("glRenderbufferStorageEXT")
		}
		actual fun glDeleteBuffersARB(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glDeleteBuffersARB")
		}
		actual fun glCopyTexSubImage3D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int) {
			throw NativeUnavailableException("glCopyTexSubImage3D")
		}
		actual fun glTexImage2Df(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: FloatArray?, arg9: Int) {
			throw NativeUnavailableException("glTexImage2Df")
		}
		actual fun glReadPixelsi(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: IntArray?, arg7: Int) {
			throw NativeUnavailableException("glReadPixelsi")
		}
		actual fun glTexCoordPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
			throw NativeUnavailableException("glTexCoordPointer")
		}
		actual fun glBindTexture(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBindTexture")
		}
		actual fun glLinkProgramARB(arg0: Long) {
			throw NativeUnavailableException("glLinkProgramARB")
		}
		actual fun glOrtho(arg0: Double, arg1: Double, arg2: Double, arg3: Double, arg4: Double, arg5: Double) {
			throw NativeUnavailableException("glOrtho")
		}
		actual fun glBufferSubDataARBa(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
			throw NativeUnavailableException("glBufferSubDataARBa")
		}
		actual fun glDeleteRenderbuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glDeleteRenderbuffersEXT")
		}
		actual fun glPixelStorei(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glPixelStorei")
		}
		actual fun glScissor(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
			throw NativeUnavailableException("glScissor")
		}
		actual fun glLineWidth(arg0: Float) {
			throw NativeUnavailableException("glLineWidth")
		}
		actual fun glGenerateMipmapEXT(arg0: Int) {
			throw NativeUnavailableException("glGenerateMipmapEXT")
		}
		actual fun glDeleteObjectARB(arg0: Long) {
			throw NativeUnavailableException("glDeleteObjectARB")
		}
		actual fun glRotatef(arg0: Float, arg1: Float, arg2: Float, arg3: Float) {
			throw NativeUnavailableException("glRotatef")
		}
		actual fun glMultiTexCoord2f(arg0: Int, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glMultiTexCoord2f")
		}
		actual fun glFramebufferRenderbufferEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
			throw NativeUnavailableException("glFramebufferRenderbufferEXT")
		}
		actual fun glTexSubImage2Di(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: IntArray?, arg9: Int) {
			throw NativeUnavailableException("glTexSubImage2Di")
		}
		actual fun glLightf(arg0: Int, arg1: Int, arg2: Float) {
			throw NativeUnavailableException("glLightf")
		}
		actual fun glTexGenfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
			throw NativeUnavailableException("glTexGenfv")
		}
		actual fun glTexCoord3i(arg0: Int, arg1: Int, arg2: Int) {
			throw NativeUnavailableException("glTexCoord3i")
		}
		actual fun glDrawArrays(arg0: Int, arg1: Int, arg2: Int) {
			throw NativeUnavailableException("glDrawArrays")
		}
		actual fun glCallList(arg0: Int) {
			throw NativeUnavailableException("glCallList")
		}
		actual fun glDeleteProgramARB(arg0: Int) {
			throw NativeUnavailableException("glDeleteProgramARB")
		}
		actual fun glBindFramebufferEXT(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBindFramebufferEXT")
		}
		actual fun glTexCoord3f(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glTexCoord3f")
		}
		actual fun glGenBuffersARB(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glGenBuffersARB")
		}
		actual fun glVertexPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
			throw NativeUnavailableException("glVertexPointer")
		}
		actual fun glGetInfoLogARB(arg0: Long, arg1: Int, arg2: IntArray?, arg3: Int, arg4: ByteArray?, arg5: Int) {
			throw NativeUnavailableException("glGetInfoLogARB")
		}
		actual fun glDepthMask(arg0: Boolean) {
			throw NativeUnavailableException("glDepthMask")
		}
		actual fun glEnd() {
			throw NativeUnavailableException("glEnd")
		}
		actual fun glEndList() {
			throw NativeUnavailableException("glEndList")
		}
		actual fun glTexImage3Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int, arg9: ByteArray?, arg10: Int) {
			throw NativeUnavailableException("glTexImage3Dub")
		}
		actual fun glTexImage1Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: ByteArray?, arg8: Int) {
			throw NativeUnavailableException("glTexImage1Dub")
		}
		actual fun glBlendFunc(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBlendFunc")
		}
		actual fun glClearDepth(arg0: Float) {
			throw NativeUnavailableException("glClearDepth")
		}
		actual fun glGetUniformLocationARB(arg0: Long, arg1: String?): Int {
			throw NativeUnavailableException("glGetUniformLocationARB")
		}
		actual fun glNewList(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glNewList")
		}
		actual fun glViewport(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
			throw NativeUnavailableException("glViewport")
		}
		actual fun glTexEnvfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
			throw NativeUnavailableException("glTexEnvfv")
		}
		actual fun glPolygonMode(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glPolygonMode")
		}
		actual fun glTexParameteri(arg0: Int, arg1: Int, arg2: Int) {
			throw NativeUnavailableException("glTexParameteri")
		}
		actual fun glPushMatrix() {
			throw NativeUnavailableException("glPushMatrix")
		}
		actual fun glPixelTransferf(arg0: Int, arg1: Float) {
			throw NativeUnavailableException("glPixelTransferf")
		}
		actual fun glUniform1fARB(arg0: Int, arg1: Float) {
			throw NativeUnavailableException("glUniform1fARB")
		}
		actual fun glBlitFramebufferEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: Int, arg9: Int) {
			throw NativeUnavailableException("glBlitFramebufferEXT")
		}
		actual fun glCopyTexSubImage2D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) {
			throw NativeUnavailableException("glCopyTexSubImage2D")
		}
		actual fun glCompileShaderARB(arg0: Long) {
			throw NativeUnavailableException("glCompileShaderARB")
		}
		actual fun glDetachObjectARB(arg0: Long, arg1: Long) {
			throw NativeUnavailableException("glDetachObjectARB")
		}
		actual fun glNormalPointer(arg0: Int, arg1: Int, arg2: Long) {
			throw NativeUnavailableException("glNormalPointer")
		}
		actual fun glUniform2fARB(arg0: Int, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glUniform2fARB")
		}
		actual fun glLightfv(arg0: Int, arg1: Int, arg2: FloatArray?, arg3: Int) {
			throw NativeUnavailableException("glLightfv")
		}
		actual fun glFogfv(arg0: Int, arg1: FloatArray?, arg2: Int) {
			throw NativeUnavailableException("glFogfv")
		}
		actual fun glGetFloatv(arg0: Int, arg1: FloatArray?, arg2: Int) {
			throw NativeUnavailableException("glGetFloatv")
		}
		actual fun glHint(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glHint")
		}
		actual fun glBufferDataARBub(arg0: Int, arg1: Int, arg2: ByteArray?, arg3: Int, arg4: Int) {
			throw NativeUnavailableException("glBufferDataARBub")
		}
		actual fun glTexSubImage2Df(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: FloatArray?, arg9: Int) {
			throw NativeUnavailableException("glTexSubImage2Df")
		}
		actual fun glClientActiveTexture(arg0: Int) {
			throw NativeUnavailableException("glClientActiveTexture")
		}
		actual fun glLightModelfv(arg0: Int, arg1: FloatArray?, arg2: Int) {
			throw NativeUnavailableException("glLightModelfv")
		}
		actual fun glMatrixMode(arg0: Int) {
			throw NativeUnavailableException("glMatrixMode")
		}
		actual fun glCopyTexImage2D(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) {
			throw NativeUnavailableException("glCopyTexImage2D")
		}
		actual fun glTexImage2Di(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: IntArray?, arg9: Int) {
			throw NativeUnavailableException("glTexImage2Di")
		}
		actual fun glUnmapBufferARB(arg0: Int): Boolean {
			throw NativeUnavailableException("glUnmapBufferARB")
		}
		actual fun glColor4ub(arg0: Byte, arg1: Byte, arg2: Byte, arg3: Byte) {
			throw NativeUnavailableException("glColor4ub")
		}
		actual fun glBufferSubDataARBub(arg0: Int, arg1: Int, arg2: Int, arg3: ByteArray?, arg4: Int) {
			throw NativeUnavailableException("glBufferSubDataARBub")
		}
		actual fun glMultMatrixf(arg0: FloatArray?, arg1: Int) {
			throw NativeUnavailableException("glMultMatrixf")
		}
		actual fun glFogi(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glFogi")
		}
		actual fun glVertex2i(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glVertex2i")
		}
		actual fun glDepthFunc(arg0: Int) {
			throw NativeUnavailableException("glDepthFunc")
		}
		actual fun glVertex3f(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glVertex3f")
		}
		actual fun glUniform4fARB(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float) {
			throw NativeUnavailableException("glUniform4fARB")
		}
		actual fun glTexEnvi(arg0: Int, arg1: Int, arg2: Int) {
			throw NativeUnavailableException("glTexEnvi")
		}
		actual fun glCreateProgramObjectARB(): Long {
			throw NativeUnavailableException("glCreateProgramObjectARB")
		}
		actual fun glNormal3f(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glNormal3f")
		}
		actual fun glGenFramebuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glGenFramebuffersEXT")
		}
		actual fun glUniform3fARB(arg0: Int, arg1: Float, arg2: Float, arg3: Float) {
			throw NativeUnavailableException("glUniform3fARB")
		}
		actual fun glCreateShaderObjectARB(arg0: Int): Long {
			throw NativeUnavailableException("glCreateShaderObjectARB")
		}
		actual fun glMapBufferARB(arg0: Int, arg1: Int): Long {
			throw NativeUnavailableException("glMapBufferARB")
		}
		actual fun glGetIntegerv(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glGetIntegerv")
		}
		actual fun glShadeModel(arg0: Int) {
			throw NativeUnavailableException("glShadeModel")
		}
		actual fun glProgramStringARB(arg0: Int, arg1: Int, arg2: String?) {
			throw NativeUnavailableException("glProgramStringARB")
		}
		actual fun glGenProgramARB(): Int {
			throw NativeUnavailableException("glGenProgramARB")
		}
		actual fun glPushAttrib(arg0: Int) {
			throw NativeUnavailableException("glPushAttrib")
		}
		actual fun glFlush() {
			throw NativeUnavailableException("glFlush")
		}
		actual fun glRenderbufferStorageMultisampleEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
			throw NativeUnavailableException("glRenderbufferStorageMultisampleEXT")
		}
		actual fun glEnable(arg0: Int) {
			throw NativeUnavailableException("glEnable")
		}
		actual fun glDisable(arg0: Int) {
			throw NativeUnavailableException("glDisable")
		}
		actual fun glGenTextures(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glGenTextures")
		}
		actual fun glFramebufferTexture2DEXT(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
			throw NativeUnavailableException("glFramebufferTexture2DEXT")
		}
		actual fun glBindBufferARB(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBindBufferARB")
		}
		actual fun glMultiTexCoord3i(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
			throw NativeUnavailableException("glMultiTexCoord3i")
		}
		actual fun glColorMaterial(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glColorMaterial")
		}
		actual fun glTexSubImage2Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: ByteArray?, arg9: Int) {
			throw NativeUnavailableException("glTexSubImage2Dub")
		}
		actual fun glShaderSourceARB(arg0: Long, arg1: String?) {
			throw NativeUnavailableException("glShaderSourceARB")
		}
		actual fun glGetTexImagei(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?, arg5: Int) {
			throw NativeUnavailableException("glGetTexImagei")
		}
		actual fun glGetString(arg0: Int): String? {
			throw NativeUnavailableException("glGetString")
		}
		actual fun glDisableClientState(arg0: Int) {
			throw NativeUnavailableException("glDisableClientState")
		}
		actual fun glClear(arg0: Int) {
			throw NativeUnavailableException("glClear")
		}
		actual fun glPointSize(arg0: Float) {
			throw NativeUnavailableException("glPointSize")
		}
		actual fun glCopyPixels(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
			throw NativeUnavailableException("glCopyPixels")
		}
		actual fun glTexCoord2f(arg0: Float, arg1: Float) {
			throw NativeUnavailableException("glTexCoord2f")
		}
		actual fun glBindProgramARB(arg0: Int, arg1: Int) {
			throw NativeUnavailableException("glBindProgramARB")
		}
		actual fun glGenRenderbuffersEXT(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glGenRenderbuffersEXT")
		}
		actual fun glPopAttrib() {
			throw NativeUnavailableException("glPopAttrib")
		}
		actual fun glVertex2f(arg0: Float, arg1: Float) {
			throw NativeUnavailableException("glVertex2f")
		}
		actual fun glLoadIdentity() {
			throw NativeUnavailableException("glLoadIdentity")
		}
		actual fun glGenLists(arg0: Int): Int {
			throw NativeUnavailableException("glGenLists")
		}
		actual fun glColorPointer(arg0: Int, arg1: Int, arg2: Int, arg3: Long) {
			throw NativeUnavailableException("glColorPointer")
		}
		actual fun glPopMatrix() {
			throw NativeUnavailableException("glPopMatrix")
		}
		actual fun glEnableClientState(arg0: Int) {
			throw NativeUnavailableException("glEnableClientState")
		}
		actual fun glShaderSourceRawARB(arg0: Long, arg1: ByteArray?) {
			throw NativeUnavailableException("glShaderSourceRawARB")
		}
		actual fun glColor3f(arg0: Float, arg1: Float, arg2: Float) {
			throw NativeUnavailableException("glColor3f")
		}
		actual fun glTexEnvf(arg0: Int, arg1: Int, arg2: Float) {
			throw NativeUnavailableException("glTexEnvf")
		}
		actual fun glProgramLocalParameter4fARB(arg0: Int, arg1: Int, arg2: Float, arg3: Float, arg4: Float, arg5: Float) {
			throw NativeUnavailableException("glProgramLocalParameter4fARB")
		}
		actual fun glAlphaFunc(arg0: Int, arg1: Float) {
			throw NativeUnavailableException("glAlphaFunc")
		}
		actual fun glDeleteTextures(arg0: Int, arg1: IntArray?, arg2: Int) {
			throw NativeUnavailableException("glDeleteTextures")
		}
		actual fun glDrawBuffer(arg0: Int) {
			throw NativeUnavailableException("glDrawBuffer")
		}
		actual fun glBegin(arg0: Int) {
			throw NativeUnavailableException("glBegin")
		}
		actual fun glFogf(arg0: Int, arg1: Float) {
			throw NativeUnavailableException("glFogf")
		}
		actual fun glAttachObjectARB(arg0: Long, arg1: Long) {
			throw NativeUnavailableException("glAttachObjectARB")
		}
		actual fun glCheckFramebufferStatusEXT(arg0: Int): Int {
			throw NativeUnavailableException("glCheckFramebufferStatusEXT")
		}
		actual fun glTexImage2Dub(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: ByteArray?, arg9: Int) {
			throw NativeUnavailableException("glTexImage2Dub")
		}
		actual fun glProgramRawARB(arg0: Int, arg1: Int, arg2: ByteArray?) {
			throw NativeUnavailableException("glProgramRawARB")
		}
	}
}
