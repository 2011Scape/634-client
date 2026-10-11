package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.cache.node.Node;
import java.io.File;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ls")
public final class ColorImageCacheEntry extends Node {

	@OriginalMember(owner = "client!ls", name = "u", descriptor = "Ljava/lang/String;")
	public static final String osName;

	@OriginalMember(owner = "client!ls", name = "t", descriptor = "Ljava/lang/String;")
	public static final String osArch;

	@OriginalMember(owner = "client!ls", name = "p", descriptor = "I")
	public final int row;

	@OriginalMember(owner = "client!ls", name = "s", descriptor = "I")
	public final int index;

	static {
		@Pc(1) String property = "Unknown";
		try {
			property = System.getProperty("java.vendor").toLowerCase();
		} catch (@Pc(7) Exception ex) {
		}
		property.toLowerCase();
		property = "Unknown";
		try {
			property = System.getProperty("java.version").toLowerCase();
		} catch (@Pc(18) Exception ex) {
		}
		property.toLowerCase();
		property = "Unknown";
		try {
			property = System.getProperty("os.name").toLowerCase();
		} catch (@Pc(29) Exception ex) {
		}
		osName = property.toLowerCase();
		property = "Unknown";
		try {
			property = System.getProperty("os.arch").toLowerCase();
		} catch (@Pc(40) Exception ex) {
		}
		osArch = property.toLowerCase();
		property = "Unknown";
		try {
			property = System.getProperty("os.version").toLowerCase();
		} catch (@Pc(51) Exception ex) {
		}
		property.toLowerCase();
		property = "~/";
		try {
			property = System.getProperty("user.home").toLowerCase();
		} catch (@Pc(62) Exception ex) {
		}
		new File(property);
	}

	@OriginalMember(owner = "client!ls", name = "<init>", descriptor = "(II)V")
	public ColorImageCacheEntry(@OriginalArg(0) int row, @OriginalArg(1) int index) {
		this.row = row;
		this.index = index;
	}
}
