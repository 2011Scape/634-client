package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!oga")
class InteractiveEntityContainer {

	@OriginalMember(owner = "client!oga", name = "b", descriptor = "Lclient!oga;")
	@JvmField
	var child: InteractiveEntityContainer? = null

	@OriginalMember(owner = "client!oga", name = "d", descriptor = "Lclient!jb;")
	@JvmField
	var interactiveObject: AbstractInteractiveEntity? = null

	@OriginalMember(owner = "client!oga", name = "a", descriptor = "(B)V")
	fun release() {
		if (InteractiveEntityContainer.activeChildCount < 500) {
			this.interactiveObject = null
			this.child = InteractiveEntityContainer.mainInteractiveWrapper
			InteractiveEntityContainer.mainInteractiveWrapper = this
			InteractiveEntityContainer.activeChildCount++
		}
	}

	companion object {
		@OriginalMember(owner = "client!kg", name = "g", descriptor = "I")
		const val poolSize: Int = 52

		@OriginalMember(owner = "client!hfa", name = "b", descriptor = "Lclient!oga;")
		@JvmField
		var mainInteractiveWrapper: InteractiveEntityContainer? = null

		@OriginalMember(owner = "client!qo", name = "xe", descriptor = "I")
		@JvmField
		var activeChildCount: Int = 0

		@OriginalMember(owner = "client!gha", name = "a", descriptor = "(Lclient!jb;I)Lclient!oga;")
		@JvmStatic
		fun obtain(@OriginalArg(0) positionEntity: AbstractInteractiveEntity?): InteractiveEntityContainer {
			@Pc(12) val positionEntityNode: InteractiveEntityContainer
			if (InteractiveEntityContainer.mainInteractiveWrapper == null) {
				positionEntityNode = InteractiveEntityContainer()
			} else {
				positionEntityNode = InteractiveEntityContainer.mainInteractiveWrapper!!
				InteractiveEntityContainer.mainInteractiveWrapper = InteractiveEntityContainer.mainInteractiveWrapper!!.child
				positionEntityNode.child = null
				InteractiveEntityContainer.activeChildCount--
			}
			positionEntityNode.interactiveObject = positionEntity
			return positionEntityNode
		}
	}
}
