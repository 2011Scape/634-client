package com.jagex.game.runetek5.sound.midi

object MidiStatus {

	const val NOTE_ON: Int = 0x90

	const val NOTE_OFF: Int = 0x80

	const val CONTROLLER: Int = 0xB0

	const val PITCH: Int = 0xE0

	const val CHANNEL_AFTERTOUCH: Int = 0xD0

	const val NOTE_AFTERTOUCH: Int = 0xA0

	const val PROGRAM_CHANGE: Int = 0xC0
}
