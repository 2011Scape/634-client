package com.jagex.game.runetek5.core.datastruct;

import com.jagex.game.runetek5.core.util.Arrays;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!bo")
public final class StringList {

	@OriginalMember(owner = "client!bo", name = "f", descriptor = "Z")
	private boolean exponential = false;

	@OriginalMember(owner = "client!bo", name = "j", descriptor = "I")
	private int last = -1;

	@OriginalMember(owner = "client!bo", name = "m", descriptor = "[Ljava/lang/String;")
	private String[] elements = new String[0];

	@OriginalMember(owner = "client!bo", name = "k", descriptor = "I")
	private final int growthFactor;

	@OriginalMember(owner = "client!bo", name = "<init>", descriptor = "(IZ)V")
	public StringList(@OriginalArg(0) int growthFactor, @OriginalArg(1) boolean exponential) {
		this.growthFactor = growthFactor;
		this.exponential = exponential;
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(II)I")
	private int nextSize(@OriginalArg(0) int count) {
		@Pc(17) int currentCount = this.elements.length;
		while (currentCount <= count) {
			if (!this.exponential) {
				currentCount += this.growthFactor;
			} else if (currentCount == 0) {
				currentCount = 1;
			} else {
				currentCount *= this.growthFactor;
			}
		}
		return currentCount;
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(I)[Ljava/lang/String;")
	public String[] toArray() {
		@Pc(17) String[] array = new String[this.last + 1];
		Arrays.copy(this.elements, 0, array, 0, this.last + 1);
		return array;
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(Ljava/lang/String;II)V")
	private void set(@OriginalArg(0) String string, @OriginalArg(2) int last) {
		if (this.last < last) {
			this.last = last;
		}
		if (last >= this.elements.length) {
			this.expand(last);
		}
		this.elements[last] = string;
	}

	@OriginalMember(owner = "client!bo", name = "b", descriptor = "(II)V")
	private void expand(@OriginalArg(0) int count) {
		@Pc(17) String[] strings = new String[this.nextSize(count)];
		Arrays.copy(this.elements, 0, strings, 0, this.elements.length);
		this.elements = strings;
	}

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(ILjava/lang/String;)V")
	public void add(@OriginalArg(1) String string) {
		this.set(string, this.last + 1);
	}

	@OriginalMember(owner = "client!bo", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		@Pc(9) StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[");
		for (@Pc(15) int i = 0; i < this.last; i++) {
			if (i != 0) {
				stringBuffer.append(", ");
			}
			stringBuffer.append(this.elements[i]);
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}
}
