package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.EntityOverhead
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object EntityOverheadAllocator {

	@OriginalMember(owner = "client!fj", name = "d", descriptor = "(I)Lclient!ql;")
	@JvmStatic
	fun allocate(): EntityOverhead {
		@Pc(8) val overhead: EntityOverhead? = EntityOverheadAllocator.aClass325_3.removeFirst() as EntityOverhead?
		if (overhead == null) {
			return EntityOverhead()
		} else {
			EntityOverheadAllocator.poolSize--
			return overhead
		}
	}

	@OriginalMember(owner = "client!jw", name = "e", descriptor = "Lclient!tca;")
	@JvmField
	val aClass325_3: EntityNodeList = EntityNodeList()
	@OriginalMember(owner = "client!sfa", name = "d", descriptor = "Lclient!tca;")
	@JvmField
	val aClass325_7: EntityNodeList = EntityNodeList()

	@OriginalMember(owner = "client!fia", name = "e", descriptor = "I")
	@JvmField
	var poolSize: Int = 0
}
