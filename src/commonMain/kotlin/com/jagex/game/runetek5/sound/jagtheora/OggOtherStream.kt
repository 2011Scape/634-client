package com.jagex.game.runetek5.sound.jagtheora

import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggStreamState
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pk")
class OggOtherStream : OggStreamDecoder {

	@OriginalMember(owner = "client!pk", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	constructor(@OriginalArg(0) oggStreamState: OggStreamState?) : super(oggStreamState)

	@OriginalMember(owner = "client!pk", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	override fun decode(@OriginalArg(1) oggPacket: OggPacket?) {
	}

	@OriginalMember(owner = "client!pk", name = "a", descriptor = "(B)V")
	override fun stop() {
	}
}
