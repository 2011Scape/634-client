package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder;
import com.jagex.game.runetek5.buffer.Packet;
import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggStreamState;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ts")
public final class OggKateDecoder extends OggStreamDecoder {

	@OriginalMember(owner = "client!ts", name = "x", descriptor = "F")
	private float endTime;

	@OriginalMember(owner = "client!ts", name = "A", descriptor = "Ljava/lang/String;")
	private String languageAbbreviation;

	@OriginalMember(owner = "client!ts", name = "D", descriptor = "I")
	private int granuleRateNumber;

	@OriginalMember(owner = "client!ts", name = "E", descriptor = "I")
	private int granuleRateDenominator;

	@OriginalMember(owner = "client!ts", name = "G", descriptor = "Ljava/lang/String;")
	private String category;

	@OriginalMember(owner = "client!ts", name = "H", descriptor = "Ljava/lang/String;")
	private String subtitleText;

	@OriginalMember(owner = "client!ts", name = "L", descriptor = "F")
	private float startTime;

	@OriginalMember(owner = "client!ts", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	public OggKateDecoder(@OriginalArg(0) OggStreamState oggStreamState) {
		super(oggStreamState);
	}

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(B)V")
	@Override
	public void stop() {
	}

	@OriginalMember(owner = "client!ts", name = "b", descriptor = "(B)F")
	public float getStartTime() {
		return this.startTime;
	}

	@OriginalMember(owner = "client!ts", name = "e", descriptor = "(B)F")
	public float getEndTime() {
		return this.endTime;
	}

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	@Override
	public void decode(@OriginalArg(1) OggPacket oggPacket) {
		if (super.packetCount > 0 && !"SUB".equals(this.category)) {
			return;
		}
		@Pc(21) Packet packet = new Packet(oggPacket.getData());
		@Pc(25) int packetType = packet.readUByte();
		if (super.packetCount <= 8) {
			if ((packetType | 0x80) == 0) {
				throw new IllegalStateException();
			}
			if (super.packetCount == 0) {
				packet.currentPosition += 23;
				this.granuleRateDenominator = packet.readUIntLittle();
				this.granuleRateNumber = packet.readUIntLittle();
				if (this.granuleRateDenominator == 0 || this.granuleRateNumber == 0) {
					throw new IllegalStateException();
				}
				@Pc(77) Packet packet2 = new Packet(16);
				packet.readBytes(0, 16, packet2.buffer);
				this.languageAbbreviation = packet2.readString();
				packet2.currentPosition = 0;
				packet.readBytes(0, 16, packet2.buffer);
				this.category = packet2.readString();
			}
			return;
		}
		if (packetType == 0) {
			@Pc(112) long startGranulePos = packet.readULongLittle();
			@Pc(116) long duration = packet.readULongLittle();
			@Pc(120) long backlink = packet.readULongLittle();
			if (startGranulePos < 0L || duration < 0L || backlink < 0L || backlink > startGranulePos) {
				throw new IllegalStateException();
			}
			this.startTime = (float) ((long) this.granuleRateNumber * startGranulePos) / (float) this.granuleRateDenominator;
			this.endTime = (float) ((startGranulePos + duration) * (long) this.granuleRateNumber) / (float) this.granuleRateDenominator;
			@Pc(177) int textLength = packet.readUIntLittle();
			if (textLength < 0 || packet.buffer.length - packet.currentPosition < textLength) {
				throw new IllegalStateException();
			}
			this.subtitleText = Utils.decodeUtf8(packet.buffer, packet.currentPosition, textLength);
		}
		if ((packetType | 0x80) != 0) {
			return;
		}
	}

	@OriginalMember(owner = "client!ts", name = "b", descriptor = "(I)Ljava/lang/String;")
	public String getLanguage() {
		return this.languageAbbreviation;
	}

	@OriginalMember(owner = "client!ts", name = "d", descriptor = "(I)Ljava/lang/String;")
	public String getSubtitle() {
		return this.subtitleText;
	}
}
