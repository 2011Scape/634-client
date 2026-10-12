package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import lang.Synchronized
import withLock

@OriginalClass("client!sq")
class PcmStreamMixer : PcmStream() {

	@OriginalMember(owner = "client!sq", name = "s", descriptor = "Lclient!uh;")
	private val subStreams: Deque = Deque()

	@OriginalMember(owner = "client!sq", name = "t", descriptor = "Lclient!uh;")
	private val scheduledEvents: Deque = Deque()

	@OriginalMember(owner = "client!sq", name = "u", descriptor = "I")
	private var nextEventTime: Int = -1

	@OriginalMember(owner = "client!sq", name = "v", descriptor = "I")
	private var currentPosition: Int = 0

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!jba;)V")
	@Synchronized
	fun remove(@OriginalArg(0) buss: PcmStream?) {
		buss!!.unlink()
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(I)V")
	@Synchronized
	override fun skip(@OriginalArg(0) length: Int) {
		var length: Int = length
		do {
			if (this.nextEventTime < 0) {
				this.skipAllStreams(length)
				return
			}
			if (this.currentPosition + length < this.nextEventTime) {
				this.currentPosition += length
				this.skipAllStreams(length)
				return
			}
			@Pc(29) val count: Int = this.nextEventTime - this.currentPosition
			this.skipAllStreams(count)
			length -= count
			this.currentPosition += count
			this.updateEventTimings()
			@Pc(50) val task: ScheduledAudioEvent? = this.scheduledEvents.first() as ScheduledAudioEvent?
			withLock(task!!) {
				@Pc(58) val time: Int = task.run(this)
				if (time < 0) {
					task.scheduledTime = 0
					this.removeEvent(task)
				} else {
					task.scheduledTime = time
					this.insertEvent(task.next, task)
				}
			}
		} while (length != 0)
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "()Lclient!jba;")
	override fun getFirstStream(): PcmStream? {
		return this.subStreams.first() as PcmStream?
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!eh;Lclient!om;)V")
	private fun insertEvent(@OriginalArg(0) node: Node?, @OriginalArg(1) event: ScheduledAudioEvent?) {
		var node: Node? = node
		while (node !== this.scheduledEvents.sentinel && (node as ScheduledAudioEvent).scheduledTime <= event!!.scheduledTime) {
			node = node.next
		}
		Node.insertBefore(event, node)
		this.nextEventTime = (this.scheduledEvents.sentinel.next as ScheduledAudioEvent).scheduledTime
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!om;)V")
	private fun removeEvent(@OriginalArg(0) event: ScheduledAudioEvent?) {
		event!!.unlink()
		event.onRemove()
		@Pc(9) val node: Node? = this.scheduledEvents.sentinel.next
		if (node === this.scheduledEvents.sentinel) {
			this.nextEventTime = -1
		} else {
			this.nextEventTime = (node as ScheduledAudioEvent).scheduledTime
		}
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "(Lclient!jba;)V")
	@Synchronized
	fun addSubStream(@OriginalArg(0) stream: PcmStream?) {
		this.subStreams.addFirst(stream)
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "(I)V")
	private fun skipAllStreams(@OriginalArg(0) amount: Int) {
		@Pc(5) var buss: PcmStream? = this.subStreams.first() as PcmStream?
		while (buss != null) {
			buss.skip(amount)
			buss = this.subStreams.next() as PcmStream?
		}
	}

	@OriginalMember(owner = "client!sq", name = "e", descriptor = "()V")
	private fun updateEventTimings() {
		if (this.currentPosition <= 0) {
			return
		}
		@Pc(8) var task: ScheduledAudioEvent? = this.scheduledEvents.first() as ScheduledAudioEvent?
		while (task != null) {
			task.scheduledTime -= this.currentPosition
			task = this.scheduledEvents.next() as ScheduledAudioEvent?
		}
		this.nextEventTime -= this.currentPosition
		this.currentPosition = 0
	}

	@OriginalMember(owner = "client!sq", name = "c", descriptor = "([III)V")
	private fun fillFromStreams(@OriginalArg(0) buffer: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		@Pc(5) var buss: PcmStream? = this.subStreams.first() as PcmStream?
		while (buss != null) {
			buss.process(buffer, offset, length)
			buss = this.subStreams.next() as PcmStream?
		}
	}

	@OriginalMember(owner = "client!sq", name = "f", descriptor = "()I")
	@Synchronized
	fun count(): Int {
		return this.subStreams.size()
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "()Lclient!jba;")
	override fun getNextStream(): PcmStream? {
		return this.subStreams.next() as PcmStream?
	}

	@OriginalMember(owner = "client!sq", name = "d", descriptor = "()I")
	override fun getActiveVoiceCount(): Int {
		return 0
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "([III)V")
	@Synchronized
	override fun fill(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		var offset: Int = offset
		var length: Int = length
		do {
			if (this.nextEventTime < 0) {
				this.fillFromStreams(mix, offset, length)
				return
			}
			if (this.currentPosition + length < this.nextEventTime) {
				this.currentPosition += length
				this.fillFromStreams(mix, offset, length)
				return
			}
			@Pc(33) val count: Int = this.nextEventTime - this.currentPosition
			this.fillFromStreams(mix, offset, count)
			offset += count
			length -= count
			this.currentPosition += count
			this.updateEventTimings()
			@Pc(60) val task: ScheduledAudioEvent? = this.scheduledEvents.first() as ScheduledAudioEvent?
			withLock(task!!) {
				@Pc(68) val time: Int = task.run(this)
				if (time < 0) {
					task.scheduledTime = 0
					this.removeEvent(task)
				} else {
					task.scheduledTime = time
					this.insertEvent(task.next, task)
				}
			}
		} while (length != 0)
	}
}
