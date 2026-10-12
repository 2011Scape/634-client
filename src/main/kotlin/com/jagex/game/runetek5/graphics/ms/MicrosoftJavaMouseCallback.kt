package com.jagex.game.runetek5.graphics.ms

import com.ms.awt.WComponentPeer
import com.ms.dll.Callback
import com.ms.dll.Root
import com.ms.win32.User32
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!lq")
class MicrosoftJavaMouseCallback : Callback() {

	@OriginalMember(owner = "client!lq", name = "b", descriptor = "I")
	@Volatile
	private var windowHandle: Int = 0

	@OriginalMember(owner = "client!lq", name = "c", descriptor = "I")
	private var windowLong: Int = 0

	@OriginalMember(owner = "client!lq", name = "d", descriptor = "I")
	@Volatile
	private var nIndex: Int = 0

	@OriginalMember(owner = "client!lq", name = "e", descriptor = "Z")
	private var loaded: Boolean = false

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "Z")
	@Volatile
	private var delete: Boolean = true

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "(IBI)V")
	fun movemouse(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int) {
		User32.SetCursorPos(x, y)
	}

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "(ZILjava/awt/Component;)V")
	fun setCursorVisible(@OriginalArg(0) delete: Boolean, @OriginalArg(2) component: java.awt.Component?) {
		// Not part of the original client, which called arg1.getPeer() directly; current JDKs have no such method.
		var getPeer: Method? = null
		var local3: WComponentPeer? = null
		try {
			getPeer = java.awt.Component::class.java.getDeclaredMethod("getPeer")
			getPeer!!.setAccessible(true)
			local3 = getPeer.invoke(component) as WComponentPeer?
		} catch (ex: NoSuchMethodException) {
			throw RuntimeException(ex)
		} catch (ex: IllegalAccessException) {
			throw RuntimeException(ex)
		} catch (ex: InvocationTargetException) {
			throw RuntimeException(ex)
		}
		@Pc(6) val hwnd: Int = local3!!.getTopHwnd()
		if (this.windowHandle == hwnd && delete == this.delete) {
			return
		}
		if (!this.loaded) {
			this.windowLong = User32.LoadCursor(0, 32512)
			Root.alloc(this)
			this.loaded = true
		}
		if (hwnd != this.windowHandle) {
			if (this.windowHandle != 0) {
				this.delete = true
				User32.SendMessage(hwnd, 101024, 0, 0)
				synchronized(this) {
					User32.SetWindowLong(this.windowHandle, -4, this.nIndex)
				}
			}
			synchronized(this) {
				this.windowHandle = hwnd
				this.nIndex = User32.SetWindowLong(this.windowHandle, -4, this)
			}
		}
		this.delete = delete
		User32.SendMessage(hwnd, 101024, 0, 0)
	}

	@OriginalMember(owner = "client!lq", name = "callback", descriptor = "(IIII)I")
	@Synchronized
	private fun callback(@OriginalArg(0) hwnd: Int, @OriginalArg(1) msg: Int, @OriginalArg(2) wParam: Int, @OriginalArg(3) lParam: Int): Int {
		@Pc(7) var local7: Int
		if (hwnd != this.windowHandle) {
			val windowProc: Int = User32.GetWindowLong(hwnd, -4)
			return User32.CallWindowProc(windowProc, hwnd, msg, wParam, lParam)
		}
		if (msg == 32) {
			val hitTest: Int = lParam and 0xFFFF
			if (hitTest == 1) {
				User32.SetCursor(if (this.delete) this.windowLong else 0)
				return 0
			}
		}
		if (msg == 101024) {
			User32.SetCursor(if (this.delete) this.windowLong else 0)
			return 0
		}
		if (msg == 1) {
			this.windowHandle = 0
			this.delete = true
		}
		return User32.CallWindowProc(this.nIndex, hwnd, msg, wParam, lParam)
	}
}
