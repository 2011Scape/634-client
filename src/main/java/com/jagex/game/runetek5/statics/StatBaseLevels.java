package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.Loading;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.particle.ParticleType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class StatBaseLevels {

	@OriginalMember(owner = "client!uca", name = "b", descriptor = "[I")
	public static final int[] statBaseLevels = new int[25];
	@OriginalMember(owner = "client!sc", name = "i", descriptor = "[I")
	public static final int[] SKILL_MAX_LEVEL = new int[] { 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 120 };
	@OriginalMember(owner = "client!sv", name = "S", descriptor = "[I")
	public static final int[] SKILL_XP_FOR_LEVEL = new int[120];

	static {
		@Pc(10) int sum = 0;
		for (@Pc(12) int level = 0; level < 120; level++) {
			@Pc(17) int nextLevel = level + 1;
			@Pc(30) int offset = (int) (Math.pow(2.0D, (double) nextLevel / 7.0D) * 300.0D + (double) nextLevel);
			sum += offset;
			StatBaseLevels.SKILL_XP_FOR_LEVEL[level] = sum / 4;
		}
	}
}
