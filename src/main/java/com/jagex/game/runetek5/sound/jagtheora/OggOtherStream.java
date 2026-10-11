package com.jagex.game.runetek5.sound.jagtheora;

import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggStreamState;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!pk")
public final class OggOtherStream extends OggStreamDecoder {

	@OriginalMember(owner = "client!pk", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	public OggOtherStream(@OriginalArg(0) OggStreamState oggStreamState) {
		super(oggStreamState);
	}

	@OriginalMember(owner = "client!pk", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	@Override
	public void decode(@OriginalArg(1) OggPacket oggPacket) {
	}

	@OriginalMember(owner = "client!pk", name = "a", descriptor = "(B)V")
	@Override
	public void stop() {
	}
}
