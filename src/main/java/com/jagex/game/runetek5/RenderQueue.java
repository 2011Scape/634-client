package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.GroundRenderTask;

@OriginalClass("client!cia")
public final class RenderQueue {

	@OriginalMember(owner = "client!cia", name = "m", descriptor = "Lclient!waa;")
	private RenderWorker worker;

	@OriginalMember(owner = "client!cia", name = "n", descriptor = "I")
	private volatile int size;

	@OriginalMember(owner = "client!cia", name = "c", descriptor = "Lclient!tca;")
	private final EntityNodeList tasks = new EntityNodeList();

	@OriginalMember(owner = "client!cia", name = "j", descriptor = "Ljava/lang/String;")
	public final String name;

	@OriginalMember(owner = "client!cia", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	public RenderQueue(@OriginalArg(0) String string) {
		this.name = string;
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!ga;Z)V")
	public void addEntity(@OriginalArg(0) Entity entity) {
		entity.particlesOnly = false;
		@Pc(9) EntityNodeList linkedList = this.tasks;
		synchronized (this.tasks) {
			this.tasks.offer(entity);
			this.size++;
		}
		if (this.worker != null) {
			@Pc(34) RenderWorker renderWorker = this.worker;
			synchronized (this.worker) {
				this.worker.notify();
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!waa;B)V")
	public void setWorker(@OriginalArg(0) RenderWorker renderWorker) {
		this.worker = renderWorker;
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!vo;I)V")
	public void addGroundTask(@OriginalArg(0) GroundRenderTask groundRenderTask) {
		@Pc(9) EntityNodeList linkedList = this.tasks;
		synchronized (this.tasks) {
			this.tasks.offer(groundRenderTask);
			this.size++;
		}
		if (this.worker != null) {
			@Pc(38) RenderWorker renderWorker = this.worker;
			synchronized (this.worker) {
				this.worker.notify();
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(I)Z")
	public boolean isEmpty() {
		return this.size == 0;
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(ILclient!ga;)V")
	public void addParticleEntity(@OriginalArg(1) Entity entity) {
		entity.particlesOnly = true;
		@Pc(9) EntityNodeList linkedList = this.tasks;
		synchronized (this.tasks) {
			this.tasks.offer(entity);
			this.size++;
		}
		if (this.worker != null) {
			@Pc(40) RenderWorker renderWorker = this.worker;
			synchronized (this.worker) {
				this.worker.notify();
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "b", descriptor = "(I)Lclient!cm;")
	public EntityNode poll() {
		@Pc(8) EntityNodeList linkedList = this.tasks;
		synchronized (this.tasks) {
			@Pc(22) EntityNode node = this.tasks.getFirst();
			node.unlinkNode();
			this.size--;
			return node;
		}
	}
	@OriginalMember(owner = "client!dba", name = "x", descriptor = "[[Ljava/lang/String;")
	public static final String[][] QUEUE_NAMES_BY_CORE_COUNT = new String[][] { { "M1", "M2", "S1", "F" }, { "M1", "M2", "M3", "S1", "S2", "F" }, { "M1", "M2", "M3", "M4", "S1", "S2", "S3", "F" } };
}
