package com.jagex.game.runetek5.graphics.ms

import com.ms.awt.WComponentPeer
import com.ms.com.IUnknown
import com.ms.directX.DDSurfaceDesc
import com.ms.directX.DirectDraw
import com.ms.directX.IEnumModesCallback
import com.ms.win32.User32
import java.awt.Frame
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!nt")
actual class MicrosoftFullscreenAdapter : IEnumModesCallback {

	@OriginalMember(owner = "client!nt", name = "b", descriptor = "Lcom/ms/directX/DirectDraw;")
	private val draw: DirectDraw = DirectDraw()

	@OriginalMember(owner = "client!nt", name = "<init>", descriptor = "()V")
	actual constructor() {
		this.draw.initialize(null)
	}

	@OriginalMember(owner = "client!nt", name = "callbackEnumModes", descriptor = "(Lcom/ms/directX/DDSurfaceDesc;Lcom/ms/com/IUnknown;)V")
	private fun callbackEnumModes(@OriginalArg(0) ddSurfaceDesc: DDSurfaceDesc?, @OriginalArg(1) iUnknown: IUnknown?) {
		if (MicrosoftFullscreenAdapter.data == null) {
			MicrosoftFullscreenAdapter.pos += 4
		} else {
			MicrosoftFullscreenAdapter.data!![MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc!!.width
			MicrosoftFullscreenAdapter.data!![MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.height
			MicrosoftFullscreenAdapter.data!![MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.rgbBitCount
			MicrosoftFullscreenAdapter.data!![MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.refreshRate
		}
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(Z)[I")
	actual fun listModes(): IntArray? {
		this.draw.enumDisplayModes(0, null, null, this)
		MicrosoftFullscreenAdapter.data = IntArray(MicrosoftFullscreenAdapter.pos)
		MicrosoftFullscreenAdapter.pos = 0
		this.draw.enumDisplayModes(0, null, null, this)
		@Pc(25) val modes: IntArray? = MicrosoftFullscreenAdapter.data
		MicrosoftFullscreenAdapter.data = null
		MicrosoftFullscreenAdapter.pos = 0
		return modes
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(IIIIILjava/awt/Frame;)V")
	actual fun enter(@OriginalArg(0) height: Int, @OriginalArg(1) bits: Int, @OriginalArg(2) width: Int, @OriginalArg(4) refreshRate: Int, @OriginalArg(5) frame: Frame?) {
		frame!!.setVisible(true)
		// Not part of the original client, which called arg4.getPeer() directly; current JDKs have no such method.
		var getPeer: Method? = null
		var local6: WComponentPeer? = null
		try {
			getPeer = Frame::class.java.getDeclaredMethod("getPeer")
			getPeer!!.setAccessible(true)
			local6 = getPeer.invoke(frame) as WComponentPeer?
		} catch (ex: NoSuchMethodException) {
			throw RuntimeException(ex)
		} catch (ex: IllegalAccessException) {
			throw RuntimeException(ex)
		} catch (ex: InvocationTargetException) {
			throw RuntimeException(ex)
		}
		@Pc(9) val hwnd: Int = local6!!.getHwnd()
		User32.SetWindowLong(hwnd, -16, Int.MIN_VALUE)
		User32.SetWindowLong(hwnd, -20, 8)
		this.draw.setCooperativeLevel(frame, 17)
		this.draw.setDisplayMode(width, height, bits, refreshRate, 0)
		frame.setBounds(0, 0, width, height)
		frame.toFront()
		frame.requestFocus()
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(Ljava/awt/Frame;I)V")
	actual fun exit(@OriginalArg(0) frame: Frame?) {
		this.draw.restoreDisplayMode()
		this.draw.setCooperativeLevel(frame, 8)
	}

	companion object {
		@OriginalMember(owner = "client!nt", name = "a", descriptor = "[I")
		@JvmField
		var data: IntArray? = null

		@OriginalMember(owner = "client!nt", name = "c", descriptor = "I")
		@JvmField
		var pos: Int = 0
	}
}
