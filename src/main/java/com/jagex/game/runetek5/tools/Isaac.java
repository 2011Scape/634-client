package com.jagex.game.runetek5.tools;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ku")
public final class Isaac {

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "[I")
	private int[] results;

	@OriginalMember(owner = "client!ku", name = "c", descriptor = "I")
	private int lastResult;

	@OriginalMember(owner = "client!ku", name = "d", descriptor = "I")
	private int count;

	@OriginalMember(owner = "client!ku", name = "e", descriptor = "I")
	private int accumulator;

	@OriginalMember(owner = "client!ku", name = "f", descriptor = "I")
	private int counter;

	@OriginalMember(owner = "client!ku", name = "h", descriptor = "[I")
	private int[] memory;

	@OriginalMember(owner = "client!ku", name = "<init>", descriptor = "()V")
	private Isaac() {
	}

	@OriginalMember(owner = "client!ku", name = "<init>", descriptor = "([I)V")
	public Isaac(@OriginalArg(0) int[] seed) {
		this.memory = new int[256];
		this.results = new int[256];
		for (@Pc(13) int i = 0; i < seed.length; i++) {
			this.results[i] = seed[i];
		}
		this.init();
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(I)V")
	private void init() {
		@Pc(8) int a = -1640531527;
		@Pc(10) int b = -1640531527;
		@Pc(12) int c = -1640531527;
		@Pc(14) int d = -1640531527;
		@Pc(16) int e = -1640531527;
		@Pc(18) int f = -1640531527;
		@Pc(20) int g = -1640531527;
		@Pc(21) int h = -1640531527;
		@Pc(23) int i;
		for (i = 0; i < 4; i++) {
			h ^= g << 11;
			e += h;
			g += f;
			g ^= f >>> 2;
			d += g;
			f += e;
			f ^= e << 8;
			e += d;
			c += f;
			e ^= d >>> 16;
			b += e;
			d += c;
			d ^= c << 10;
			c += b;
			a += d;
			c ^= b >>> 4;
			h += c;
			b += a;
			b ^= a << 8;
			g += b;
			a += h;
			a ^= h >>> 9;
			h += g;
			f += a;
		}
		for (i = 0; i < 256; i += 8) {
			f += this.results[i + 2];
			a += this.results[i + 7];
			e += this.results[i + 3];
			c += this.results[i + 5];
			b += this.results[i + 6];
			g += this.results[i + 1];
			d += this.results[i + 4];
			h += this.results[i];
			h ^= g << 11;
			g += f;
			e += h;
			g ^= f >>> 2;
			f += e;
			d += g;
			f ^= e << 8;
			c += f;
			e += d;
			e ^= d >>> 16;
			b += e;
			d += c;
			d ^= c << 10;
			a += d;
			c += b;
			c ^= b >>> 4;
			b += a;
			h += c;
			b ^= a << 8;
			a += h;
			g += b;
			a ^= h >>> 9;
			f += a;
			h += g;
			this.memory[i] = h;
			this.memory[i + 1] = g;
			this.memory[i + 2] = f;
			this.memory[i + 3] = e;
			this.memory[i + 4] = d;
			this.memory[i + 5] = c;
			this.memory[i + 6] = b;
			this.memory[i + 7] = a;
		}
		for (i = 0; i < 256; i += 8) {
			f += this.memory[i + 2];
			e += this.memory[i + 3];
			a += this.memory[i + 7];
			c += this.memory[i + 5];
			g += this.memory[i + 1];
			b += this.memory[i + 6];
			h += this.memory[i];
			d += this.memory[i + 4];
			h ^= g << 11;
			e += h;
			g += f;
			g ^= f >>> 2;
			d += g;
			f += e;
			f ^= e << 8;
			c += f;
			e += d;
			e ^= d >>> 16;
			b += e;
			d += c;
			d ^= c << 10;
			c += b;
			a += d;
			c ^= b >>> 4;
			b += a;
			h += c;
			b ^= a << 8;
			g += b;
			a += h;
			a ^= h >>> 9;
			f += a;
			h += g;
			this.memory[i] = h;
			this.memory[i + 1] = g;
			this.memory[i + 2] = f;
			this.memory[i + 3] = e;
			this.memory[i + 4] = d;
			this.memory[i + 5] = c;
			this.memory[i + 6] = b;
			this.memory[i + 7] = a;
		}
		this.isaac();
		this.count = 256;
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(B)I")
	public int peek() {
		if (this.count == 0) {
			this.isaac();
			this.count = 256;
		}
		return this.results[this.count - 1];
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(Z)V")
	private void isaac() {
		this.lastResult += ++this.counter;
		for (@Pc(19) int i = 0; i < 256; i++) {
			@Pc(26) int x = this.memory[i];
			if ((i & 0x2) == 0) {
				if ((i & 0x1) == 0) {
					this.accumulator ^= this.accumulator << 13;
				} else {
					this.accumulator ^= this.accumulator >>> 6;
				}
			} else if ((i & 0x1) == 0) {
				this.accumulator ^= this.accumulator << 2;
			} else {
				this.accumulator ^= this.accumulator >>> 16;
			}
			this.accumulator += this.memory[i + 128 & 0xFF];
			@Pc(116) int y;
			this.memory[i] = y = this.memory[x >> 2 & 0xFF] + this.accumulator + this.lastResult;
			this.results[i] = this.lastResult = x + this.memory[y >> 8 >> 2 & 0xFF];
		}
	}

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "(B)I")
	public int next() {
		if (this.count == 0) {
			this.isaac();
			this.count = 256;
		}
		return this.results[--this.count];
	}
}
