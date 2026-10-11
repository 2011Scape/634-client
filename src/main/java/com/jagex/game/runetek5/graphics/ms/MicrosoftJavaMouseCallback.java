package com.jagex.game.runetek5.graphics.ms;

import com.jagex.game.runetek5.config.rsinterface.Component;
import com.ms.awt.WComponentPeer;
import com.ms.dll.Callback;
import com.ms.dll.Root;
import com.ms.win32.User32;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!lq")
public final class MicrosoftJavaMouseCallback extends Callback {

	@OriginalMember(owner = "client!lq", name = "b", descriptor = "I")
	private volatile int windowHandle;

	@OriginalMember(owner = "client!lq", name = "c", descriptor = "I")
	private int windowLong;

	@OriginalMember(owner = "client!lq", name = "d", descriptor = "I")
	private volatile int nIndex;

	@OriginalMember(owner = "client!lq", name = "e", descriptor = "Z")
	private boolean loaded;

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "Z")
	private volatile boolean delete = true;

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "(IBI)V")
	public void movemouse(@OriginalArg(0) int x, @OriginalArg(2) int y) {
		User32.SetCursorPos(x, y);
	}

	@OriginalMember(owner = "client!lq", name = "a", descriptor = "(ZILjava/awt/Component;)V")
	public void setCursorVisible(@OriginalArg(0) boolean delete, @OriginalArg(2) java.awt.Component component) {
		// Not part of the original client, which called arg1.getPeer() directly; current JDKs have no such method.
		Method getPeer;
		WComponentPeer local3;
		try {
			getPeer = java.awt.Component.class.getDeclaredMethod("getPeer");
			getPeer.setAccessible(true);
			local3 = (WComponentPeer) getPeer.invoke(component);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
		@Pc(6) int hwnd = local3.getTopHwnd();
		if (this.windowHandle == hwnd && delete == this.delete) {
			return;
		}
		if (!this.loaded) {
			this.windowLong = User32.LoadCursor(0, 32512);
			Root.alloc(this);
			this.loaded = true;
		}
		if (hwnd != this.windowHandle) {
			if (this.windowHandle != 0) {
				this.delete = true;
				User32.SendMessage(hwnd, 101024, 0, 0);
				synchronized (this) {
					User32.SetWindowLong(this.windowHandle, -4, this.nIndex);
				}
			}
			synchronized (this) {
				this.windowHandle = hwnd;
				this.nIndex = User32.SetWindowLong(this.windowHandle, -4, this);
			}
		}
		this.delete = delete;
		User32.SendMessage(hwnd, 101024, 0, 0);
	}

	@OriginalMember(owner = "client!lq", name = "callback", descriptor = "(IIII)I")
	private synchronized int callback(@OriginalArg(0) int hwnd, @OriginalArg(1) int msg, @OriginalArg(2) int wParam, @OriginalArg(3) int lParam) {
		@Pc(7) int local7;
		if (hwnd != this.windowHandle) {
			int windowProc = User32.GetWindowLong(hwnd, -4);
			return User32.CallWindowProc(windowProc, hwnd, msg, wParam, lParam);
		}
		if (msg == 32) {
			int hitTest = lParam & 0xFFFF;
			if (hitTest == 1) {
				User32.SetCursor(this.delete ? this.windowLong : 0);
				return 0;
			}
		}
		if (msg == 101024) {
			User32.SetCursor(this.delete ? this.windowLong : 0);
			return 0;
		}
		if (msg == 1) {
			this.windowHandle = 0;
			this.delete = true;
		}
		return User32.CallWindowProc(this.nIndex, hwnd, msg, wParam, lParam);
	}
}
