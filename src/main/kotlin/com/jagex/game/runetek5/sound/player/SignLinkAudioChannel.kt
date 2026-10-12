package com.jagex.game.runetek5.sound.player

import com.jagex.game.runetek5.PcmDevice
import com.jagex.game.runetek5.sound.player.PcmPlayer
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.sign.SignLink
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.SignLinkPcmDevice

@OriginalClass("client!qn")
class SignLinkAudioChannel : PcmPlayer {

	@OriginalMember(owner = "client!qn", name = "F", descriptor = "I")
	private val deviceIndex: Int

	@OriginalMember(owner = "client!qn", name = "<init>", descriptor = "(Lclient!wea;I)V")
	constructor(@OriginalArg(0) signLink: SignLink?, @OriginalArg(1) deviceIndex: Int) : super() {
		SignLinkPcmDevice.pcmDevice = signLink!!.getMicrosoftPcmDevice() as PcmDevice?
		this.deviceIndex = deviceIndex
	}

	@OriginalMember(owner = "client!qn", name = "b", descriptor = "()V")
	override fun open() {
		SignLinkPcmDevice.pcmDevice.discardBuffer(this.deviceIndex)
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "()I")
	override fun dataLeft(): Int {
		return SignLinkPcmDevice.pcmDevice.position(this.deviceIndex)
	}

	@OriginalMember(owner = "client!qn", name = "c", descriptor = "()V")
	override fun close() {
		SignLinkPcmDevice.pcmDevice.close(this.deviceIndex)
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "(Ljava/awt/Component;)V")
	@Throws(Exception::class)
	override fun init(@OriginalArg(0) component: java.awt.Component?) {
		SignLinkPcmDevice.pcmDevice.init(PcmPlayerManager.sampleRate, PcmPlayerManager.stereo, component)
	}

	@OriginalMember(owner = "client!qn", name = "d", descriptor = "()V")
	override fun writeToBuffer() {
		SignLinkPcmDevice.pcmDevice.write(this.deviceIndex, super.bitpackedSoundData)
	}

	@OriginalMember(owner = "client!qn", name = "a", descriptor = "(I)V")
	@Throws(Exception::class)
	override fun play(@OriginalArg(0) bufferCapacity: Int) {
		if (bufferCapacity > 32768) {
			throw IllegalArgumentException()
		}
		SignLinkPcmDevice.pcmDevice.open(bufferCapacity, this.deviceIndex)
	}
}
