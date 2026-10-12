package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sound.player.PcmPlayer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField
import lang.Runnable

@OriginalClass("client!tm")
class PcmPlayerThread : Runnable {

	@OriginalMember(owner = "client!tm", name = "k", descriptor = "Lclient!wea;")
	@JvmField
	var signLink: SignLink? = null

	@OriginalMember(owner = "client!tm", name = "a", descriptor = "[Lclient!cba;")
	@JvmField
	val players: Array<PcmPlayer?> = arrayOfNulls<PcmPlayer>(2)

	@OriginalMember(owner = "client!tm", name = "d", descriptor = "Z")
	@Volatile
	@JvmField
	var stopping: Boolean = false

	@OriginalMember(owner = "client!tm", name = "f", descriptor = "Z")
	@Volatile
	@JvmField
	var running: Boolean = false

	@OriginalMember(owner = "client!tm", name = "run", descriptor = "()V")
	override fun run() {
		this.running = true
		try {
			while (!this.stopping) {
				@Pc(12) var i: Int = 0
				while (i < 2) {
					@Pc(19) val pcmPlayer: PcmPlayer? = this.players[i]
					if (pcmPlayer != null) {
						pcmPlayer.cycle()
					}
					i++
				}
				TimeUtils.sleep(10L)
				GameShell.performDummyActionEvent(null, this.signLink)
			}
		} catch (@Pc(41) ex: Exception) {
			ExplicitRuntimeException.reportError(null, ex)
		} finally {
			@Pc(51) val `object`: Any? = null
			this.running = false
		}
	}
}
