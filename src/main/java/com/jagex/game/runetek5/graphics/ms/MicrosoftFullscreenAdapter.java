package com.jagex.game.runetek5.graphics.ms;

import com.ms.awt.WComponentPeer;
import com.ms.com.IUnknown;
import com.ms.com._Guid;
import com.ms.directX.DDSurfaceDesc;
import com.ms.directX.DirectDraw;
import com.ms.directX.IEnumModesCallback;
import com.ms.win32.User32;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!nt")
public final class MicrosoftFullscreenAdapter implements IEnumModesCallback {

	@OriginalMember(owner = "client!nt", name = "b", descriptor = "Lcom/ms/directX/DirectDraw;")
	private final DirectDraw draw = new DirectDraw();

	@OriginalMember(owner = "client!nt", name = "<init>", descriptor = "()V")
	public MicrosoftFullscreenAdapter() {
		this.draw.initialize(null);
	}

	@OriginalMember(owner = "client!nt", name = "callbackEnumModes", descriptor = "(Lcom/ms/directX/DDSurfaceDesc;Lcom/ms/com/IUnknown;)V")
	private void callbackEnumModes(@OriginalArg(0) DDSurfaceDesc ddSurfaceDesc, @OriginalArg(1) IUnknown iUnknown) {
		if (MicrosoftFullscreenAdapter.data == null) {
			MicrosoftFullscreenAdapter.pos += 4;
		} else {
			MicrosoftFullscreenAdapter.data[MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.width;
			MicrosoftFullscreenAdapter.data[MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.height;
			MicrosoftFullscreenAdapter.data[MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.rgbBitCount;
			MicrosoftFullscreenAdapter.data[MicrosoftFullscreenAdapter.pos++] = ddSurfaceDesc.refreshRate;
		}
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(Z)[I")
	public int[] listModes() {
		this.draw.enumDisplayModes(0, null, null, this);
		MicrosoftFullscreenAdapter.data = new int[MicrosoftFullscreenAdapter.pos];
		MicrosoftFullscreenAdapter.pos = 0;
		this.draw.enumDisplayModes(0, null, null, this);
		@Pc(25) int[] modes = MicrosoftFullscreenAdapter.data;
		MicrosoftFullscreenAdapter.data = null;
		MicrosoftFullscreenAdapter.pos = 0;
		return modes;
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(IIIIILjava/awt/Frame;)V")
	public void enter(@OriginalArg(0) int height, @OriginalArg(1) int bits, @OriginalArg(2) int width, @OriginalArg(4) int refreshRate, @OriginalArg(5) Frame frame) {
		frame.setVisible(true);
		// Not part of the original client, which called arg4.getPeer() directly; current JDKs have no such method.
		Method getPeer;
		WComponentPeer local6;
		try {
			getPeer = Frame.class.getDeclaredMethod("getPeer");
			getPeer.setAccessible(true);
			local6 = (WComponentPeer) getPeer.invoke(frame);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
		@Pc(9) int hwnd = local6.getHwnd();
		User32.SetWindowLong(hwnd, -16, Integer.MIN_VALUE);
		User32.SetWindowLong(hwnd, -20, 8);
		this.draw.setCooperativeLevel(frame, 17);
		this.draw.setDisplayMode(width, height, bits, refreshRate, 0);
		frame.setBounds(0, 0, width, height);
		frame.toFront();
		frame.requestFocus();
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "(Ljava/awt/Frame;I)V")
	public void exit(@OriginalArg(0) Frame frame) {
		this.draw.restoreDisplayMode();
		this.draw.setCooperativeLevel(frame, 8);
	}

	@OriginalMember(owner = "client!nt", name = "a", descriptor = "[I")
	public static int[] data;

	@OriginalMember(owner = "client!nt", name = "c", descriptor = "I")
	public static int pos;
}
