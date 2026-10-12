package com.jagex.game.runetek5.sound.player

import com.jagex.game.runetek5.PcmPlayerThread
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.sign.SignLink
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import awt.Component
import lang.Class
import lang.getDeclaredConstructor

object PcmPlayerManager {
	@OriginalMember(owner = "client!hk", name = "l", descriptor = "I")
	@JvmField
	var sampleRate: Int = 0

	@OriginalMember(owner = "client!qe", name = "f", descriptor = "Lclient!tm;")
	@JvmField
	var runnable: PcmPlayerThread? = null

	@OriginalMember(owner = "client!vga", name = "b", descriptor = "Z")
	@JvmField
	var stereo: Boolean = false
	@OriginalMember(owner = "client!gv", name = "a", descriptor = "(ZIZI)V")
	@JvmStatic
	fun init(@OriginalArg(0) stereo: Boolean) {
		PcmPlayerManager.threadPriority = 2
		PcmPlayerManager.sampleRate = 22050
		PcmPlayerManager.stereo = stereo
	}

	@OriginalMember(owner = "client!u", name = "e", descriptor = "I")
	@JvmField
	var threadPriority: Int = 0

	@OriginalMember(owner = "client!qr", name = "a", descriptor = "(ILclient!wea;Ljava/awt/Component;II)Lclient!cba;")
	@JvmStatic
	fun createPcmPlayer(@OriginalArg(0) sampleRate2: Int, @OriginalArg(1) signLink: SignLink?, @OriginalArg(2) component: Component?, @OriginalArg(3) deviceIndex: Int): PcmPlayer {
		if (PcmPlayerManager.sampleRate == 0) {
			throw IllegalStateException()
		}
		try {
			@Pc(28) val pcmPlayer: PcmPlayer = lang.forName("com.jagex.game.runetek5.sound.player.MonoSterioPlayer").getDeclaredConstructor().newInstance() as PcmPlayer
			pcmPlayer.sampleRate2 = sampleRate2
			pcmPlayer.bitpackedSoundData = IntArray((if (PcmPlayerManager.stereo) 2 else 1) * 256)
			pcmPlayer.init(component)
			pcmPlayer.bufferCapacity = (sampleRate2 and 0xFFFFFC00.toInt()) + 1024
			if (pcmPlayer.bufferCapacity > 16384) {
				pcmPlayer.bufferCapacity = 16384
			}
			pcmPlayer.play(pcmPlayer.bufferCapacity)
			if (PcmPlayerManager.threadPriority > 0 && PcmPlayerManager.runnable == null) {
				PcmPlayerManager.runnable = PcmPlayerThread()
				PcmPlayerManager.runnable!!.signLink = signLink
				signLink!!.startThread(PcmPlayerManager.runnable, PcmPlayerManager.threadPriority)
			}
			if (PcmPlayerManager.runnable != null) {
				if (PcmPlayerManager.runnable!!.players[deviceIndex] != null) {
					throw IllegalArgumentException()
				}
				PcmPlayerManager.runnable!!.players[deviceIndex] = pcmPlayer
			}
			return pcmPlayer
		} catch (@Pc(106) ex: Throwable) {
			try {
				@Pc(112) val signLinkAudioChannel: SignLinkAudioChannel = SignLinkAudioChannel(signLink, deviceIndex)
				signLinkAudioChannel.sampleRate2 = sampleRate2
				signLinkAudioChannel.bitpackedSoundData = IntArray((if (PcmPlayerManager.stereo) 2 else 1) * 256)
				signLinkAudioChannel.init(component)
				signLinkAudioChannel.bufferCapacity = 16384
				signLinkAudioChannel.play(signLinkAudioChannel.bufferCapacity)
				if (PcmPlayerManager.threadPriority > 0 && PcmPlayerManager.runnable == null) {
					PcmPlayerManager.runnable = PcmPlayerThread()
					PcmPlayerManager.runnable!!.signLink = signLink
					signLink!!.startThread(PcmPlayerManager.runnable, PcmPlayerManager.threadPriority)
				}
				if (PcmPlayerManager.runnable != null) {
					if (PcmPlayerManager.runnable!!.players[deviceIndex] != null) {
						throw IllegalArgumentException()
					}
					PcmPlayerManager.runnable!!.players[deviceIndex] = signLinkAudioChannel
				}
				return signLinkAudioChannel
			} catch (@Pc(171) ex2: Throwable) {
				return PcmPlayer()
			}
		}
	}
}
