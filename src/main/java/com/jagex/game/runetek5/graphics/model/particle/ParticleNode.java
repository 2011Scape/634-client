package com.jagex.game.runetek5.graphics.model.particle;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.EntityNode;

@OriginalClass("client!taa")
public class ParticleNode extends EntityNode {

	@OriginalMember(owner = "client!taa", name = "j", descriptor = "Lclient!taa;")
	public ParticleNode prev2;

	@OriginalMember(owner = "client!taa", name = "k", descriptor = "Lclient!taa;")
	public ParticleNode next2;

	@OriginalMember(owner = "client!taa", name = "a", descriptor = "(I)V")
	public final void unlink2() {
		if (this.prev2 != null) {
			this.prev2.next2 = this.next2;
			this.next2.prev2 = this.prev2;
			this.next2 = null;
			this.prev2 = null;
		}
	}
}
