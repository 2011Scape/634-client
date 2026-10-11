package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.EntityOverhead;
import com.jagex.game.runetek5.entity.EntityNodeList;

public final class EntityOverheadAllocator {

	@OriginalMember(owner = "client!fj", name = "d", descriptor = "(I)Lclient!ql;")
	public static EntityOverhead allocate() {
		@Pc(8) EntityOverhead overhead = (EntityOverhead) EntityOverheadAllocator.aClass325_3.removeFirst();
		if (overhead == null) {
			return new EntityOverhead();
		} else {
			EntityOverheadAllocator.poolSize--;
			return overhead;
		}
	}
	@OriginalMember(owner = "client!jw", name = "e", descriptor = "Lclient!tca;")
	public static final EntityNodeList aClass325_3 = new EntityNodeList();
	@OriginalMember(owner = "client!sfa", name = "d", descriptor = "Lclient!tca;")
	public static final EntityNodeList aClass325_7 = new EntityNodeList();

	@OriginalMember(owner = "client!fia", name = "e", descriptor = "I")
	public static int poolSize = 0;
}
