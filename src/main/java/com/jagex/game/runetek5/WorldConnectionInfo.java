package com.jagex.game.runetek5;

import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!pc")
public final class WorldConnectionInfo {

	@OriginalMember(owner = "client!pc", name = "f", descriptor = "I")
	public int worldId;

	@OriginalMember(owner = "client!pc", name = "j", descriptor = "Ljava/lang/String;")
	public String host;

	@OriginalMember(owner = "client!pc", name = "b", descriptor = "Z")
	private boolean useProxy = false;

	@OriginalMember(owner = "client!pc", name = "i", descriptor = "Z")
	private boolean usePrimaryPort = true;

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "I")
	public int primaryPort = 443;

	@OriginalMember(owner = "client!pc", name = "k", descriptor = "I")
	public int secondaryPort = 43594;

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(I)V")
	public void cycleConnectionMode() {
		if (!this.usePrimaryPort) {
			this.useProxy = true;
			this.usePrimaryPort = true;
		} else if (this.useProxy) {
			this.useProxy = false;
		} else {
			this.usePrimaryPort = false;
		}
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(Lclient!wea;B)Lclient!of;")
	public SignedResource openSocket(@OriginalArg(0) SignLink signLink) {
		return signLink.openSocket(this.useProxy, this.usePrimaryPort ? this.primaryPort : this.secondaryPort, this.host);
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(BLclient!pc;)Z")
	public boolean isSameServer(@OriginalArg(1) WorldConnectionInfo other) {
		if (other == null) {
			return false;
		} else {
			return this.worldId == other.worldId && this.host.equals(other.host);
		}
	}
}
