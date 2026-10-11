package com.jagex.game.runetek5.sound.player;

import com.jagex.game.runetek5.PcmDevice;
import com.jagex.game.runetek5.sound.player.PcmPlayer;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.sign.SignLink;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.SignLinkPcmDevice;

@OriginalClass("client!qn")
public final class SignLinkAudioChannel extends PcmPlayer {

	@OriginalMember(owner = "client!qn", name = "F", descriptor = "I")
	private final int deviceIndex;

	@OriginalMember(owner = "client!qn", name = "<init>", descriptor = "(Lclient!wea;I)V")
	public SignLinkAudioChannel(@OriginalArg(0) SignLink signLink, @OriginalArg(1) int deviceIndex) {
		SignLinkPcmDevice.pcmDevice = (PcmDevice) signLink.getMicrosoftPcmDevice();
		this.deviceIndex = deviceIndex;
	}

	@OriginalMember(owner = "client!qn", name = "b", descriptor = "()V")
	@Override
	public void open() {
		SignLinkPcmDevice.pcmDevice.discardBuffer(this.deviceIndex);
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "()I")
	@Override
	public int dataLeft() {
		return SignLinkPcmDevice.pcmDevice.position(this.deviceIndex);
	}

	@OriginalMember(owner = "client!qn", name = "c", descriptor = "()V")
	@Override
	public void close() {
		SignLinkPcmDevice.pcmDevice.close(this.deviceIndex);
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "(Ljava/awt/Component;)V")
	@Override
	public void init(@OriginalArg(0) java.awt.Component component) throws Exception {
		SignLinkPcmDevice.pcmDevice.init(PcmPlayerManager.sampleRate, PcmPlayerManager.stereo, component);
	}

	@OriginalMember(owner = "client!qn", name = "d", descriptor = "()V")
	@Override
	public void writeToBuffer() {
		SignLinkPcmDevice.pcmDevice.write(this.deviceIndex, super.bitpackedSoundData);
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "(I)V")
	@Override
	public void play(@OriginalArg(0) int bufferCapacity) throws Exception {
		if (bufferCapacity > 32768) {
			throw new IllegalArgumentException();
		}
		SignLinkPcmDevice.pcmDevice.open(bufferCapacity, this.deviceIndex);
	}
}
