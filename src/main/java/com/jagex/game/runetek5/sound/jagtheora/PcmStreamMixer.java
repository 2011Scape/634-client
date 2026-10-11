package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.sound.jagtheora.PcmStream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sq")
public final class PcmStreamMixer extends PcmStream {

	@OriginalMember(owner = "client!sq", name = "s", descriptor = "Lclient!uh;")
	private final Deque subStreams = new Deque();

	@OriginalMember(owner = "client!sq", name = "t", descriptor = "Lclient!uh;")
	private final Deque scheduledEvents = new Deque();

	@OriginalMember(owner = "client!sq", name = "u", descriptor = "I")
	private int nextEventTime = -1;

	@OriginalMember(owner = "client!sq", name = "v", descriptor = "I")
	private int currentPosition = 0;

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!jba;)V")
	public synchronized void remove(@OriginalArg(0) PcmStream buss) {
		buss.unlink();
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(I)V")
	@Override
	public synchronized void skip(@OriginalArg(0) int length) {
		do {
			if (this.nextEventTime < 0) {
				this.skipAllStreams(length);
				return;
			}
			if (this.currentPosition + length < this.nextEventTime) {
				this.currentPosition += length;
				this.skipAllStreams(length);
				return;
			}
			@Pc(29) int count = this.nextEventTime - this.currentPosition;
			this.skipAllStreams(count);
			length -= count;
			this.currentPosition += count;
			this.updateEventTimings();
			@Pc(50) ScheduledAudioEvent task = (ScheduledAudioEvent) this.scheduledEvents.first();
			synchronized (task) {
				@Pc(58) int time = task.run(this);
				if (time < 0) {
					task.scheduledTime = 0;
					this.removeEvent(task);
				} else {
					task.scheduledTime = time;
					this.insertEvent(task.next, task);
				}
			}
		} while (length != 0);
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getFirstStream() {
		return (PcmStream) this.subStreams.first();
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!eh;Lclient!om;)V")
	private void insertEvent(@OriginalArg(0) Node node, @OriginalArg(1) ScheduledAudioEvent event) {
		while (node != this.scheduledEvents.sentinel && ((ScheduledAudioEvent) node).scheduledTime <= event.scheduledTime) {
			node = node.next;
		}
		Node.insertBefore(event, node);
		this.nextEventTime = ((ScheduledAudioEvent) this.scheduledEvents.sentinel.next).scheduledTime;
	}

	@OriginalMember(owner = "client!sq", name = "a", descriptor = "(Lclient!om;)V")
	private void removeEvent(@OriginalArg(0) ScheduledAudioEvent event) {
		event.unlink();
		event.onRemove();
		@Pc(9) Node node = this.scheduledEvents.sentinel.next;
		if (node == this.scheduledEvents.sentinel) {
			this.nextEventTime = -1;
		} else {
			this.nextEventTime = ((ScheduledAudioEvent) node).scheduledTime;
		}
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "(Lclient!jba;)V")
	public synchronized void addSubStream(@OriginalArg(0) PcmStream stream) {
		this.subStreams.addFirst(stream);
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "(I)V")
	private void skipAllStreams(@OriginalArg(0) int amount) {
		for (@Pc(5) PcmStream buss = (PcmStream) this.subStreams.first(); buss != null; buss = (PcmStream) this.subStreams.next()) {
			buss.skip(amount);
		}
	}

	@OriginalMember(owner = "client!sq", name = "e", descriptor = "()V")
	private void updateEventTimings() {
		if (this.currentPosition <= 0) {
			return;
		}
		for (@Pc(8) ScheduledAudioEvent task = (ScheduledAudioEvent) this.scheduledEvents.first(); task != null; task = (ScheduledAudioEvent) this.scheduledEvents.next()) {
			task.scheduledTime -= this.currentPosition;
		}
		this.nextEventTime -= this.currentPosition;
		this.currentPosition = 0;
	}

	@OriginalMember(owner = "client!sq", name = "c", descriptor = "([III)V")
	private void fillFromStreams(@OriginalArg(0) int[] buffer, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		for (@Pc(5) PcmStream buss = (PcmStream) this.subStreams.first(); buss != null; buss = (PcmStream) this.subStreams.next()) {
			buss.process(buffer, offset, length);
		}
	}

	@OriginalMember(owner = "client!sq", name = "f", descriptor = "()I")
	public synchronized int count() {
		return this.subStreams.size();
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "()Lclient!jba;")
	@Override
	public PcmStream getNextStream() {
		return (PcmStream) this.subStreams.next();
	}

	@OriginalMember(owner = "client!sq", name = "d", descriptor = "()I")
	@Override
	public int getActiveVoiceCount() {
		return 0;
	}

	@OriginalMember(owner = "client!sq", name = "b", descriptor = "([III)V")
	@Override
	public synchronized void fill(@OriginalArg(0) int[] mix, @OriginalArg(1) int offset, @OriginalArg(2) int length) {
		do {
			if (this.nextEventTime < 0) {
				this.fillFromStreams(mix, offset, length);
				return;
			}
			if (this.currentPosition + length < this.nextEventTime) {
				this.currentPosition += length;
				this.fillFromStreams(mix, offset, length);
				return;
			}
			@Pc(33) int count = this.nextEventTime - this.currentPosition;
			this.fillFromStreams(mix, offset, count);
			offset += count;
			length -= count;
			this.currentPosition += count;
			this.updateEventTimings();
			@Pc(60) ScheduledAudioEvent task = (ScheduledAudioEvent) this.scheduledEvents.first();
			synchronized (task) {
				@Pc(68) int time = task.run(this);
				if (time < 0) {
					task.scheduledTime = 0;
					this.removeEvent(task);
				} else {
					task.scheduledTime = time;
					this.insertEvent(task.next, task);
				}
			}
		} while (length != 0);
	}
}
