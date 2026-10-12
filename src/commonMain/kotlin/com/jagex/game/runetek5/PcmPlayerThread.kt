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
import runLoop

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
		// Not part of the original client: the loop goes through runLoop (PlatformLoop), the original
		// blocking loop on the JVM and an event-loop chain where a platform has no threads; runStep is
		// one turn of the original while.
		runLoop(
			prelude = { true },
			step = { this.runStep() },
			onError = fun(@Pc(41) ex: Throwable) {
				if (ex !is Exception) {
					throw ex
				}
				ExplicitRuntimeException.reportError(null, ex)
			},
			onFinally = {
				@Pc(51) val `object`: Any? = null
				this.running = false
			},
		)
	}

	/* Not part of the original client: one turn of run's loop, false when the thread stops. */
	private fun runStep(): Boolean {
		if (this.stopping) {
			return false
		}
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
		return true
	}
}
