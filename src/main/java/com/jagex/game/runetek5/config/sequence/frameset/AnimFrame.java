package com.jagex.game.runetek5.config.sequence.frameset;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!gk")
public final class AnimFrame {

	@OriginalMember(owner = "client!gk", name = "f", descriptor = "Z")
	public boolean modifiesColor = false;

	@OriginalMember(owner = "client!gk", name = "d", descriptor = "Z")
	public boolean modifiesBillboard = false;

	@OriginalMember(owner = "client!gk", name = "i", descriptor = "Lclient!raa;")
	public AnimBase base = null;

	@OriginalMember(owner = "client!gk", name = "n", descriptor = "Z")
	public boolean modifiesAlpha = false;

	@OriginalMember(owner = "client!gk", name = "a", descriptor = "I")
	public int numTransformations = 0;

	@OriginalMember(owner = "client!gk", name = "e", descriptor = "[S")
	public short[] baseIndices;

	@OriginalMember(owner = "client!gk", name = "o", descriptor = "[S")
	public short[] transformationX;

	@OriginalMember(owner = "client!gk", name = "l", descriptor = "[S")
	public short[] transformationY;

	@OriginalMember(owner = "client!gk", name = "j", descriptor = "[S")
	public short[] transformationZ;

	@OriginalMember(owner = "client!gk", name = "c", descriptor = "[S")
	public short[] labels;

	@OriginalMember(owner = "client!gk", name = "g", descriptor = "[B")
	public byte[] tweeningProperties;

	@OriginalMember(owner = "client!gk", name = "<init>", descriptor = "([BLclient!raa;)V")
	public AnimFrame(@OriginalArg(0) byte[] data, @OriginalArg(1) AnimBase animBase) {
		this.base = animBase;
		try {
			@Pc(24) Packet packet = new Packet(data);
			@Pc(29) Packet packet2 = new Packet(data);
			packet.readUByte();
			packet.currentPosition += 2;
			@Pc(43) int count = packet.readUByte();
			@Pc(45) int numTransformations = 0;
			@Pc(47) int last = -1;
			@Pc(49) int lastUsed = -1;
			packet2.currentPosition = packet.currentPosition + count;
			@Pc(64) int local64;
			for (@Pc(57) int i = 0; i < count; i++) {
				int transformType = this.base.transformationType[i];
				if (transformType == 0) {
					last = i;
				}
				@Pc(72) int attribute = packet.readUByte();
				if (attribute > 0) {
					if (transformType == 0) {
						lastUsed = i;
					}
					AnimFrame.INDICES[numTransformations] = (short) i;
					@Pc(85) short defaultValue = 0;
					if (transformType == 3 || transformType == 10) {
						defaultValue = 128;
					}
					if ((attribute & 0x1) == 0) {
						AnimFrame.TRANSFORM_X[numTransformations] = defaultValue;
					} else {
						AnimFrame.TRANSFORM_X[numTransformations] = (short) packet2.readSmart();
					}
					if ((attribute & 0x2) == 0) {
						AnimFrame.TRANSFORM_Y[numTransformations] = defaultValue;
					} else {
						AnimFrame.TRANSFORM_Y[numTransformations] = (short) packet2.readSmart();
					}
					if ((attribute & 0x4) == 0) {
						AnimFrame.TRANSFORM_Z[numTransformations] = defaultValue;
					} else {
						AnimFrame.TRANSFORM_Z[numTransformations] = (short) packet2.readSmart();
					}
					AnimFrame.FLAGS[numTransformations] = (byte) (attribute >>> 3 & 0x3);
					if (transformType == 2 || transformType == 9) {
						AnimFrame.TRANSFORM_X[numTransformations] = (short) (AnimFrame.TRANSFORM_X[numTransformations] << 2 & 0x3FFF);
						AnimFrame.TRANSFORM_Y[numTransformations] = (short) (AnimFrame.TRANSFORM_Y[numTransformations] << 2 & 0x3FFF);
						AnimFrame.TRANSFORM_Z[numTransformations] = (short) (AnimFrame.TRANSFORM_Z[numTransformations] << 2 & 0x3FFF);
					}
					AnimFrame.SKIPPED[numTransformations] = -1;
					if (transformType == 1 || transformType == 2 || transformType == 3) {
						if (last > lastUsed) {
							AnimFrame.SKIPPED[numTransformations] = (short) last;
							lastUsed = last;
						}
					} else if (transformType == 5) {
						this.modifiesAlpha = true;
					} else if (transformType == 7) {
						this.modifiesColor = true;
					} else if (transformType == 9 || transformType == 10 || transformType == 8) {
						this.modifiesBillboard = true;
					}
					numTransformations++;
				}
			}
			if (packet2.currentPosition != data.length) {
				throw new RuntimeException();
			}
			this.numTransformations = numTransformations;
			this.baseIndices = new short[numTransformations];
			this.transformationX = new short[numTransformations];
			this.transformationY = new short[numTransformations];
			this.transformationZ = new short[numTransformations];
			this.labels = new short[numTransformations];
			this.tweeningProperties = new byte[numTransformations];
			for (int transformIndex = 0; transformIndex < numTransformations; transformIndex++) {
				this.baseIndices[transformIndex] = AnimFrame.INDICES[transformIndex];
				this.transformationX[transformIndex] = AnimFrame.TRANSFORM_X[transformIndex];
				this.transformationY[transformIndex] = AnimFrame.TRANSFORM_Y[transformIndex];
				this.transformationZ[transformIndex] = AnimFrame.TRANSFORM_Z[transformIndex];
				this.labels[transformIndex] = AnimFrame.SKIPPED[transformIndex];
				this.tweeningProperties[transformIndex] = AnimFrame.FLAGS[transformIndex];
			}
		} catch (@Pc(331) Exception ex) {
			this.numTransformations = 0;
			this.modifiesAlpha = false;
			this.modifiesColor = false;
		}
	}

	@OriginalMember(owner = "client!gk", name = "b", descriptor = "[S")
	public static final short[] TRANSFORM_X = new short[500];

	@OriginalMember(owner = "client!gk", name = "h", descriptor = "[S")
	public static final short[] TRANSFORM_Y = new short[500];

	@OriginalMember(owner = "client!gk", name = "k", descriptor = "[B")
	public static final byte[] FLAGS = new byte[500];

	@OriginalMember(owner = "client!gk", name = "m", descriptor = "[S")
	public static final short[] SKIPPED = new short[500];

	@OriginalMember(owner = "client!gk", name = "p", descriptor = "[S")
	public static final short[] INDICES = new short[500];

	@OriginalMember(owner = "client!gk", name = "q", descriptor = "[S")
	public static final short[] TRANSFORM_Z = new short[500];
}
