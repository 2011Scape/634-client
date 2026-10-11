package com.jagex.game.runetek5.sound.midi;

public final class MidiStatus {

	public static final int NOTE_ON = 0x90;

	public static final int NOTE_OFF = 0x80;

	public static final int CONTROLLER = 0xB0;

	public static final int PITCH = 0xE0;

	public static final int CHANNEL_AFTERTOUCH = 0xD0;

	public static final int NOTE_AFTERTOUCH = 0xA0;

	public static final int PROGRAM_CHANGE = 0xC0;

	private MidiStatus() {
		/* empty */
	}
}
