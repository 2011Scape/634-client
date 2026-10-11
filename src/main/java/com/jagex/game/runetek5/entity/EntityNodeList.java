package com.jagex.game.runetek5.entity;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!tca")
public final class EntityNodeList {

	@OriginalMember(owner = "client!tca", name = "k", descriptor = "Lclient!cm;")
	private EntityNode pointer;

	@OriginalMember(owner = "client!tca", name = "j", descriptor = "Lclient!cm;")
	private final EntityNode head = new EntityNode();

	@OriginalMember(owner = "client!tca", name = "<init>", descriptor = "()V")
	public EntityNodeList() {
		this.head.previous = this.head;
		this.head.next = this.head;
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(ILclient!cm;)V")
	public void offer(@OriginalArg(1) EntityNode node) {
		if (node.previous != null) {
			node.unlinkNode();
		}
		node.previous = this.head.previous;
		node.next = this.head;
		node.previous.next = node;
		node.next.previous = node;
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(I)Lclient!cm;")
	public EntityNode getLast() {
		@Pc(13) EntityNode node = this.head.previous;
		if (this.head == node) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.previous;
			return node;
		}
	}

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(B)Z")
	public boolean isEmpty() {
		return this.head == this.head.next;
	}

	@OriginalMember(owner = "client!tca", name = "b", descriptor = "(I)Lclient!cm;")
	public EntityNode getFirst() {
		@Pc(7) EntityNode node = this.head.next;
		if (node == this.head) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.next;
			return node;
		}
	}

	@OriginalMember(owner = "client!tca", name = "c", descriptor = "(I)I")
	public int size() {
		@Pc(7) int count = 0;
		for (@Pc(11) EntityNode node = this.head.next; node != this.head; node = node.next) {
			count++;
		}
		return count;
	}

	@OriginalMember(owner = "client!tca", name = "d", descriptor = "(I)Lclient!cm;")
	public EntityNode removeFirst() {
		@Pc(7) EntityNode node = this.head.next;
		if (node == this.head) {
			return null;
		} else {
			node.unlinkNode();
			return node;
		}
	}

	@OriginalMember(owner = "client!tca", name = "c", descriptor = "(B)V")
	public void clear() {
		while (true) {
			@Pc(13) EntityNode node = this.head.next;
			if (node == this.head) {
				this.pointer = null;
				return;
			}
			node.unlinkNode();
		}
	}

	@OriginalMember(owner = "client!tca", name = "d", descriptor = "(B)Lclient!cm;")
	public EntityNode getNext() {
		@Pc(6) EntityNode node = this.pointer;
		if (node == this.head) {
			this.pointer = null;
			return null;
		} else {
			this.pointer = node.next;
			return node;
		}
	}
}
