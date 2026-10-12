/*
 * Not part of the original client: the browser side of the DirectSound output of Microsoft's JVM
 * (milestone 4). SignLink creates it by name only on that JVM, so in a browser it is never reached;
 * if it were, it throws, as a missing native library would.
 */
package com.jagex.game.runetek5

import NativeUnavailableException
import awt.Component

actual class DirectSoundPcmDevice : PcmDevice {
	actual constructor() : super() {
		throw NativeUnavailableException("DirectSoundPcmDevice")
	}
	actual override fun discardBuffer(index: Int) {
		throw NativeUnavailableException("discardBuffer")
	}
	actual override fun close(index: Int) {
		throw NativeUnavailableException("close")
	}
	actual override fun write(index: Int, samples: IntArray?) {
		throw NativeUnavailableException("write")
	}
	actual override fun position(index: Int): Int {
		throw NativeUnavailableException("position")
	}
	actual override fun init(sampleRate: Int, stereo: Boolean, component: Component?) {
		throw NativeUnavailableException("init")
	}
	actual override fun open(bufferSize: Int, index: Int) {
		throw NativeUnavailableException("open")
	}
}
