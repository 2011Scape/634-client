package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ee")
public interface PcmDevice {

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IB)V")
	public void close(@OriginalArg(0) int index);

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(BI)I")
	public int position(@OriginalArg(1) int index);

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(I[I)V")
	public void write(@OriginalArg(0) int index, @OriginalArg(1) int[] samples);

	@OriginalMember(owner = "client!ee", name = "b", descriptor = "(BI)V")
	public void discardBuffer(@OriginalArg(1) int index);

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IZLjava/awt/Component;I)V")
	public void init(@OriginalArg(0) int sampleRate, @OriginalArg(1) boolean stereo, @OriginalArg(2) java.awt.Component component) throws Exception;

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IIB)V")
	public void open(@OriginalArg(0) int bufferSize, @OriginalArg(1) int index) throws Exception;
}
