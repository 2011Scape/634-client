package com.jagex.game.runetek5.toolkit.companion;

import com.jagex.game.runetek5.config.Identifiable;
import jaclib.memory.Buffer;
import jaclib.memory.Source;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tc")
public interface NativeStream extends Identifiable {

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	public boolean createFromSource(@OriginalArg(0) Source source, @OriginalArg(1) int stride, @OriginalArg(2) int size);

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(I)Z")
	public boolean unlock();

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	public Buffer lock();

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(IBI)Z")
	public boolean create(@OriginalArg(0) int size, @OriginalArg(2) int stride);

	@OriginalMember(owner = "client!tc", name = "b", descriptor = "(I)V")
	public void deallocate();
}
