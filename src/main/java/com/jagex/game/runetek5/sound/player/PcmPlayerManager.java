package com.jagex.game.runetek5.sound.player;

import com.jagex.game.runetek5.PcmPlayerThread;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.sign.SignLink;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class PcmPlayerManager {
	@OriginalMember(owner = "client!hk", name = "l", descriptor = "I")
	public static int sampleRate;

	@OriginalMember(owner = "client!qe", name = "f", descriptor = "Lclient!tm;")
	public static PcmPlayerThread runnable;

	private PcmPlayerManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!vga", name = "b", descriptor = "Z")
	public static boolean stereo;
	@OriginalMember(owner = "client!gv", name = "a", descriptor = "(ZIZI)V")
	public static void init(@OriginalArg(0) boolean stereo) {
		PcmPlayerManager.threadPriority = 2;
		PcmPlayerManager.sampleRate = 22050;
		PcmPlayerManager.stereo = stereo;
	}

	@OriginalMember(owner = "client!u", name = "e", descriptor = "I")
	public static int threadPriority;

	@OriginalMember(owner = "client!qr", name = "a", descriptor = "(ILclient!wea;Ljava/awt/Component;II)Lclient!cba;")
	public static PcmPlayer createPcmPlayer(@OriginalArg(0) int sampleRate2, @OriginalArg(1) SignLink signLink, @OriginalArg(2) java.awt.Component component, @OriginalArg(3) int deviceIndex) {
		if (PcmPlayerManager.sampleRate == 0) {
			throw new IllegalStateException();
		}
		try {
			@Pc(28) PcmPlayer pcmPlayer = (PcmPlayer) Class.forName("com.jagex.game.runetek5.sound.player.MonoSterioPlayer").getDeclaredConstructor().newInstance();
			pcmPlayer.sampleRate2 = sampleRate2;
			pcmPlayer.bitpackedSoundData = new int[(PcmPlayerManager.stereo ? 2 : 1) * 256];
			pcmPlayer.init(component);
			pcmPlayer.bufferCapacity = (sampleRate2 & 0xFFFFFC00) + 1024;
			if (pcmPlayer.bufferCapacity > 16384) {
				pcmPlayer.bufferCapacity = 16384;
			}
			pcmPlayer.play(pcmPlayer.bufferCapacity);
			if (PcmPlayerManager.threadPriority > 0 && PcmPlayerManager.runnable == null) {
				PcmPlayerManager.runnable = new PcmPlayerThread();
				PcmPlayerManager.runnable.signLink = signLink;
				signLink.startThread(PcmPlayerManager.runnable, PcmPlayerManager.threadPriority);
			}
			if (PcmPlayerManager.runnable != null) {
				if (PcmPlayerManager.runnable.players[deviceIndex] != null) {
					throw new IllegalArgumentException();
				}
				PcmPlayerManager.runnable.players[deviceIndex] = pcmPlayer;
			}
			return pcmPlayer;
		} catch (@Pc(106) Throwable ex) {
			try {
				@Pc(112) SignLinkAudioChannel signLinkAudioChannel = new SignLinkAudioChannel(signLink, deviceIndex);
				signLinkAudioChannel.sampleRate2 = sampleRate2;
				signLinkAudioChannel.bitpackedSoundData = new int[(PcmPlayerManager.stereo ? 2 : 1) * 256];
				signLinkAudioChannel.init(component);
				signLinkAudioChannel.bufferCapacity = 16384;
				signLinkAudioChannel.play(signLinkAudioChannel.bufferCapacity);
				if (PcmPlayerManager.threadPriority > 0 && PcmPlayerManager.runnable == null) {
					PcmPlayerManager.runnable = new PcmPlayerThread();
					PcmPlayerManager.runnable.signLink = signLink;
					signLink.startThread(PcmPlayerManager.runnable, PcmPlayerManager.threadPriority);
				}
				if (PcmPlayerManager.runnable != null) {
					if (PcmPlayerManager.runnable.players[deviceIndex] != null) {
						throw new IllegalArgumentException();
					}
					PcmPlayerManager.runnable.players[deviceIndex] = signLinkAudioChannel;
				}
				return signLinkAudioChannel;
			} catch (@Pc(171) Throwable ex2) {
				return new PcmPlayer();
			}
		}
	}
}
