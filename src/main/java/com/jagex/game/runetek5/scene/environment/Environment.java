package com.jagex.game.runetek5.scene.environment;

import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GamePreferences;

@OriginalClass("client!bp")
public final class Environment {

	@OriginalMember(owner = "client!bp", name = "h", descriptor = "F")
	public float brightpass = 0.25F;

	@OriginalMember(owner = "client!bp", name = "k", descriptor = "F")
	public float bloom = 1.0F;

	@OriginalMember(owner = "client!bp", name = "q", descriptor = "F")
	public float whitePoint = 1.0F;

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "I")
	public final int sunPositionZ;

	@OriginalMember(owner = "client!bp", name = "b", descriptor = "F")
	public final float sunBacklight;

	@OriginalMember(owner = "client!bp", name = "g", descriptor = "Lclient!wh;")
	public final EnvironmentSampler sunCubeTexture;

	@OriginalMember(owner = "client!bp", name = "l", descriptor = "I")
	public final int fogDepth;

	@OriginalMember(owner = "client!bp", name = "o", descriptor = "I")
	public final int fogColour;

	@OriginalMember(owner = "client!bp", name = "n", descriptor = "I")
	public final int sunPositionY;

	@OriginalMember(owner = "client!bp", name = "d", descriptor = "I")
	public final int sunPositionX;

	@OriginalMember(owner = "client!bp", name = "j", descriptor = "F")
	public final float sunLight;

	@OriginalMember(owner = "client!bp", name = "f", descriptor = "I")
	public final int sunColour;

	@OriginalMember(owner = "client!bp", name = "m", descriptor = "F")
	public final float sunAmbient;

	@OriginalMember(owner = "client!bp", name = "<init>", descriptor = "()V")
	public Environment() {
		this.sunPositionZ = -50;
		this.sunBacklight = 1.2F;
		this.sunCubeTexture = EnvironmentManager.aClass78_4;
		this.fogDepth = 0;
		this.fogColour = EnvironmentManager.defaultFogColour;
		this.sunPositionY = -60;
		this.sunPositionX = -50;
		this.sunLight = 0.69921875F;
		this.sunColour = EnvironmentManager.defaultSunColour;
		this.sunAmbient = 1.1523438F;
	}

	@OriginalMember(owner = "client!bp", name = "<init>", descriptor = "(Lclient!cea;)V")
	public Environment(@OriginalArg(0) Packet packet) {
		@Pc(16) int flags = packet.readUByte();
		if (GamePreferences.uid.lightDetail.getValue() == 1 && EnvironmentManager.toolkit.getMaxLights() > 0) {
			if ((flags & 0x1) == 0) {
				this.sunColour = EnvironmentManager.defaultSunColour;
			} else {
				this.sunColour = packet.readInt();
			}
			if ((flags & 0x2) == 0) {
				this.sunAmbient = 1.1523438F;
			} else {
				this.sunAmbient = (float) packet.readUShort() / 256.0F;
			}
			if ((flags & 0x4) == 0) {
				this.sunLight = 0.69921875F;
			} else {
				this.sunLight = (float) packet.readUShort() / 256.0F;
			}
			if ((flags & 0x8) == 0) {
				this.sunBacklight = 1.2F;
			} else {
				this.sunBacklight = (float) packet.readUShort() / 256.0F;
			}
		} else {
			if ((flags & 0x1) != 0) {
				packet.readInt();
			}
			if ((flags & 0x2) != 0) {
				packet.readUShort();
			}
			if ((flags & 0x4) != 0) {
				packet.readUShort();
			}
			if ((flags & 0x8) != 0) {
				packet.readUShort();
			}
			this.sunColour = EnvironmentManager.defaultSunColour;
			this.sunAmbient = 1.1523438F;
			this.sunLight = 0.69921875F;
			this.sunBacklight = 1.2F;
		}
		if ((flags & 0x10) == 0) {
			this.sunPositionY = -60;
			this.sunPositionZ = -50;
			this.sunPositionX = -50;
		} else {
			this.sunPositionX = packet.readShort();
			this.sunPositionY = packet.readShort();
			this.sunPositionZ = packet.readShort();
		}
		if ((flags & 0x20) == 0) {
			this.fogColour = EnvironmentManager.defaultFogColour;
		} else {
			this.fogColour = packet.readInt();
		}
		if ((flags & 0x40) == 0) {
			this.fogDepth = 0;
		} else {
			this.fogDepth = packet.readUShort();
		}
		if ((flags & 0x80) == 0) {
			this.sunCubeTexture = EnvironmentManager.aClass78_4;
		} else {
			@Pc(231) int materialAtPositiveX = packet.readUShort();
			@Pc(235) int materialAtNegativeX = packet.readUShort();
			@Pc(239) int materialAtPositiveY = packet.readUShort();
			@Pc(243) int materialAtNegativeY = packet.readUShort();
			@Pc(247) int materialAtPositiveZ = packet.readUShort();
			@Pc(251) int materialAtNegativeZ = packet.readUShort();
			this.sunCubeTexture = EnvironmentManager.getCubeTexture(materialAtPositiveZ, materialAtNegativeY, materialAtPositiveX, materialAtNegativeZ, materialAtNegativeX, materialAtPositiveY);
		}
	}

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(ILclient!cea;)V")
	public void decodeHdr(@OriginalArg(1) Packet packet) {
		this.bloom = (float) (packet.readUByte() * 8) / 255.0F;
		this.brightpass = (float) (packet.readUByte() * 8) / 255.0F;
		this.whitePoint = (float) (packet.readUByte() * 8) / 255.0F;
	}

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(Lclient!bp;B)Z")
	public boolean isEqual(@OriginalArg(0) Environment environment) {
		return environment.sunColour == this.sunColour && this.sunAmbient == environment.sunAmbient && environment.sunLight == this.sunLight && this.sunBacklight == environment.sunBacklight && environment.brightpass == this.brightpass && environment.bloom == this.bloom && environment.whitePoint == this.whitePoint && this.fogColour == environment.fogColour && this.fogDepth == environment.fogDepth && environment.sunCubeTexture == this.sunCubeTexture;
	}
}
