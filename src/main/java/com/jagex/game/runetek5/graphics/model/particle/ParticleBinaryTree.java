package com.jagex.game.runetek5.graphics.model.particle;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!lh")
public final class ParticleBinaryTree {

	@OriginalMember(owner = "client!lh", name = "n", descriptor = "Lclient!taa;")
	private ParticleNode cursor;

	@OriginalMember(owner = "client!lh", name = "l", descriptor = "Lclient!taa;")
	public final ParticleNode current = new ParticleNode();

	@OriginalMember(owner = "client!lh", name = "<init>", descriptor = "()V")
	public ParticleBinaryTree() {
		this.current.prev2 = this.current;
		this.current.next2 = this.current;
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(I)I")
	public int size() {
		@Pc(7) int size = 0;
		@Pc(11) ParticleNode node = this.current.next2;
		while (this.current != node) {
			node = node.next2;
			size++;
		}
		return size;
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(Lclient!taa;Z)V")
	public void insert(@OriginalArg(0) ParticleNode node2) {
		if (node2.prev2 != null) {
			node2.unlink2();
		}
		node2.next2 = this.current;
		node2.prev2 = this.current.prev2;
		node2.prev2.next2 = node2;
		node2.next2.prev2 = node2;
	}

	@OriginalMember(owner = "client!lh", name = "b", descriptor = "(B)V")
	public void clear() {
		while (true) {
			@Pc(5) ParticleNode node = this.current.next2;
			if (node == this.current) {
				this.cursor = null;
				return;
			}
			node.unlink2();
		}
	}

	@OriginalMember(owner = "client!lh", name = "c", descriptor = "(B)Lclient!taa;")
	public ParticleNode next() {
		@Pc(6) ParticleNode node2 = this.cursor;
		if (node2 == this.current) {
			this.cursor = null;
			return null;
		} else {
			this.cursor = node2.next2;
			return node2;
		}
	}

	@OriginalMember(owner = "client!lh", name = "c", descriptor = "(I)Lclient!taa;")
	public ParticleNode first() {
		@Pc(12) ParticleNode node2 = this.current.next2;
		if (node2 == this.current) {
			this.cursor = null;
			return null;
		} else {
			this.cursor = node2.next2;
			return node2;
		}
	}
}
