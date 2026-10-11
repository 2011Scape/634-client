package com.jagex.game.runetek5.sound;

import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.cache.node.VorbisSound;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.config.midi.SoundEffect;
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream;

@OriginalClass("client!gt")
public final class Sound {

	@OriginalMember(owner = "client!gt", name = "e", descriptor = "Lclient!via;")
	public AreaSoundStream aClass2_Sub8_Sub5_1;

	@OriginalMember(owner = "client!gt", name = "l", descriptor = "Lclient!ck;")
	public SoundEffect aClass61_1;

	@OriginalMember(owner = "client!gt", name = "r", descriptor = "Lclient!ob;")
	public VariableRateSoundPacket aClass2_Sub29_Sub1_1;

	@OriginalMember(owner = "client!gt", name = "u", descriptor = "Lclient!hp;")
	public VorbisSound aClass2_Sub22_1;

	@OriginalMember(owner = "client!gt", name = "f", descriptor = "I")
	public final int loops;

	@OriginalMember(owner = "client!gt", name = "m", descriptor = "I")
	public final int volume;

	@OriginalMember(owner = "client!gt", name = "q", descriptor = "I")
	public int delay;

	@OriginalMember(owner = "client!gt", name = "g", descriptor = "I")
	public final int coord;

	@OriginalMember(owner = "client!gt", name = "n", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!gt", name = "i", descriptor = "B")
	public final byte type;

	@OriginalMember(owner = "client!gt", name = "b", descriptor = "I")
	public final int rate;

	@OriginalMember(owner = "client!gt", name = "a", descriptor = "Lclient!ga;")
	public final Entity entity;

	@OriginalMember(owner = "client!gt", name = "<init>", descriptor = "(BIIIIIILclient!ga;)V")
	public Sound(@OriginalArg(0) byte type, @OriginalArg(1) int id, @OriginalArg(2) int loops, @OriginalArg(3) int delay, @OriginalArg(4) int volume, @OriginalArg(5) int coord, @OriginalArg(6) int rate, @OriginalArg(7) Entity entity) {
		this.loops = loops;
		this.volume = volume;
		this.delay = delay;
		this.coord = coord;
		this.id = id;
		this.type = type;
		this.rate = rate;
		this.entity = entity;
	}

	@OriginalMember(owner = "client!gt", name = "c", descriptor = "(B)Z")
	public boolean isVorbis() {
		return this.type == SoundType.VORBIS || this.type == SoundType.VORBIS_VOICE_OVER;
	}
}
