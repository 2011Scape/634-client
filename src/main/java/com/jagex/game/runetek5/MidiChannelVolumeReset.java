package com.jagex.game.runetek5;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;

public final class MidiChannelVolumeReset {

	@OriginalMember(owner = "client!cl", name = "g", descriptor = "(I)V")
	public static void resetChannelVolumes() {
		SongPlayer.setChannelVolume(255, -1);
	}
}
