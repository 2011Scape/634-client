package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.Node;
import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggStreamState;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!au")
public abstract class OggStreamDecoder extends Node {

	@OriginalMember(owner = "client!au", name = "q", descriptor = "[F")
	public static final float[] SINE = new float[16384];

	@OriginalMember(owner = "client!au", name = "s", descriptor = "[F")
	public static final float[] COSINE = new float[16384];

	@OriginalMember(owner = "client!au", name = "t", descriptor = "I")
	public int packetCount;

	@OriginalMember(owner = "client!au", name = "u", descriptor = "Lclient!jagtheora/ogg/OggStreamState;")
	public final OggStreamState streamState;

	static {
		@Pc(7) double radiansPerUnit = 3.834951969714103E-4D;
		for (@Pc(9) int angle = 0; angle < 16384; angle++) {
			SINE[angle] = (float) Math.sin(radiansPerUnit * (double) angle);
			COSINE[angle] = (float) Math.cos((double) angle * radiansPerUnit);
		}
	}

	@OriginalMember(owner = "client!au", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	public OggStreamDecoder(@OriginalArg(0) OggStreamState oggStreamState) {
		this.streamState = oggStreamState;
	}

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(B)V")
	public abstract void stop();

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(Lclient!jagtheora/ogg/OggPacket;I)V")
	public final void processPacket(@OriginalArg(0) OggPacket oggPacket) {
		this.decode(oggPacket);
		this.packetCount++;
	}

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	public abstract void decode(@OriginalArg(1) OggPacket packet);
}
