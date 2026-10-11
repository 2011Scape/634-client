package com.jagex.game.runetek5;

import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import java.lang.reflect.Constructor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!g")
public abstract class AbstractMouseManager {

	@OriginalMember(owner = "client!g", name = "<init>", descriptor = "()V")
	public AbstractMouseManager() {
	}

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(I)Z")
	public abstract boolean isMiddleButtonDown();

	@OriginalMember(owner = "client!g", name = "b", descriptor = "(I)V")
	public abstract void mainLogic();

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(B)Z")
	public abstract boolean isRightButtonDown();

	@OriginalMember(owner = "client!g", name = "c", descriptor = "(I)V")
	public abstract void remove();

	@OriginalMember(owner = "client!g", name = "d", descriptor = "(I)Z")
	public final boolean isAnyButtonDown() {
		return this.isLeftButtonDown() || this.isMiddleButtonDown() || this.isRightButtonDown();
	}

	@OriginalMember(owner = "client!g", name = "b", descriptor = "(B)I")
	public abstract int getMouseY();

	@OriginalMember(owner = "client!g", name = "e", descriptor = "(I)Z")
	public abstract boolean isLeftButtonDown();

	@OriginalMember(owner = "client!g", name = "f", descriptor = "(I)Lclient!bu;")
	public abstract MouseRecord nextSubmission();

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(Z)I")
	public abstract int getMouseX();
	@OriginalMember(owner = "client!jg", name = "a", descriptor = "(Ljava/awt/Component;IZ)Lclient!g;")
	public static AbstractMouseManager create(@OriginalArg(0) java.awt.Component component) {
		try {
			@Pc(24) Constructor constructor = Class.forName("com.jagex.game.runetek5.client.input.mouse.MouseManager").getDeclaredConstructor(java.awt.Component.class, Boolean.TYPE);
			return (AbstractMouseManager) constructor.newInstance(component, Boolean.valueOf(true));
		} catch (@Pc(42) Throwable ex) {
			return new LegacyMouseManager(component, true);
		}
	}
}
