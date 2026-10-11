package com.jagex.game.runetek5;

import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.sign.SignLink;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.PcmPlayer;
import com.jagex.game.runetek5.core.util.TimeUtils;

@OriginalClass("client!tm")
public final class PcmPlayerThread implements Runnable {

	@OriginalMember(owner = "client!tm", name = "k", descriptor = "Lclient!wea;")
	public SignLink signLink;

	@OriginalMember(owner = "client!tm", name = "a", descriptor = "[Lclient!cba;")
	public final PcmPlayer[] players = new PcmPlayer[2];

	@OriginalMember(owner = "client!tm", name = "d", descriptor = "Z")
	public volatile boolean stopping = false;

	@OriginalMember(owner = "client!tm", name = "f", descriptor = "Z")
	public volatile boolean running = false;

	@OriginalMember(owner = "client!tm", name = "run", descriptor = "()V")
	@Override
	public void run() {
		this.running = true;
		try {
			while (!this.stopping) {
				for (@Pc(12) int i = 0; i < 2; i++) {
					@Pc(19) PcmPlayer pcmPlayer = this.players[i];
					if (pcmPlayer != null) {
						pcmPlayer.cycle();
					}
				}
				TimeUtils.sleep(10L);
				GameShell.performDummyActionEvent(null, this.signLink);
			}
		} catch (@Pc(41) Exception ex) {
			ExplicitRuntimeException.reportError(null, ex);
		} finally {
			@Pc(51) Object object = null;
			this.running = false;
		}
	}
}
