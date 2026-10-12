package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import lang.reflect.Constructor
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import awt.Component
import lang.Class
import lang.jClass
import lang.getDeclaredConstructor

@OriginalClass("client!g")
abstract class AbstractMouseManager @OriginalMember(owner = "client!g", name = "<init>", descriptor = "()V") constructor() {

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(I)Z")
	abstract fun isMiddleButtonDown(): Boolean

	@OriginalMember(owner = "client!g", name = "b", descriptor = "(I)V")
	abstract fun mainLogic()

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(B)Z")
	abstract fun isRightButtonDown(): Boolean

	@OriginalMember(owner = "client!g", name = "c", descriptor = "(I)V")
	abstract fun remove()

	@OriginalMember(owner = "client!g", name = "d", descriptor = "(I)Z")
	fun isAnyButtonDown(): Boolean {
		return this.isLeftButtonDown() || this.isMiddleButtonDown() || this.isRightButtonDown()
	}

	@OriginalMember(owner = "client!g", name = "b", descriptor = "(B)I")
	abstract fun getMouseY(): Int

	@OriginalMember(owner = "client!g", name = "e", descriptor = "(I)Z")
	abstract fun isLeftButtonDown(): Boolean

	@OriginalMember(owner = "client!g", name = "f", descriptor = "(I)Lclient!bu;")
	abstract fun nextSubmission(): MouseRecord?

	@OriginalMember(owner = "client!g", name = "a", descriptor = "(Z)I")
	abstract fun getMouseX(): Int

	companion object {
		@OriginalMember(owner = "client!jg", name = "a", descriptor = "(Ljava/awt/Component;IZ)Lclient!g;")
		@JvmStatic
		fun create(@OriginalArg(0) component: Component?): AbstractMouseManager {
			try {
				@Pc(24) val constructor: Constructor<*> = lang.forName("com.jagex.game.runetek5.client.input.mouse.MouseManager").getDeclaredConstructor(Component::class.jClass, lang.BOOLEAN_TYPE)
				return constructor.newInstance(component, (true)) as AbstractMouseManager
			} catch (@Pc(42) ex: Throwable) {
				return LegacyMouseManager(component, true)
			}
		}
	}
}
