package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor;
import com.jagex.game.runetek5.scene.Scene;

@OriginalClass("client!ga")
public abstract class Entity extends EntityNode {

	@OriginalMember(owner = "client!ga", name = "i", descriptor = "B")
	public byte virtualLevel;

	@OriginalMember(owner = "client!ga", name = "j", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!ga", name = "l", descriptor = "Lclient!ga;")
	public Entity nextEntity;

	@OriginalMember(owner = "client!ga", name = "m", descriptor = "I")
	public int screenY;

	@OriginalMember(owner = "client!ga", name = "n", descriptor = "B")
	public byte level;

	@OriginalMember(owner = "client!ga", name = "o", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!ga", name = "s", descriptor = "I")
	public int screenX;

	@OriginalMember(owner = "client!ga", name = "t", descriptor = "Z")
	public boolean particlesOnly;

	@OriginalMember(owner = "client!ga", name = "u", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!ga", name = "v", descriptor = "I")
	public int screenDepth;

	@OriginalMember(owner = "client!ga", name = "x", descriptor = "Z")
	public boolean markDirtyOnRender = false;

	@OriginalMember(owner = "client!ga", name = "<init>", descriptor = "()V")
	public Entity() {
	}

	@OriginalMember(owner = "client!ga", name = "g", descriptor = "(I)Z")
	public abstract boolean isStationary();

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(B)Z")
	public abstract boolean hasAlpha();

	@OriginalMember(owner = "client!ga", name = "h", descriptor = "(I)Z")
	public abstract boolean canRender();

	@OriginalMember(owner = "client!ga", name = "i", descriptor = "(I)I")
	public abstract int getMinY(@OriginalArg(0) int check);

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	public abstract void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX);

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	public abstract Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit);

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(B)Z")
	public abstract boolean isOccluded();

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "([Lclient!ab;I)I")
	public abstract int findLights(@OriginalArg(0) PointLight[] pointLights);

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	public abstract PickableEntity render(@OriginalArg(1) Toolkit toolkit);

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(Lclient!ha;I)V")
	public abstract void particleCycle(@OriginalArg(0) Toolkit toolkit);

	@OriginalMember(owner = "client!ga", name = "j", descriptor = "(I)Z")
	public abstract boolean hasTileShadow();

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "([Lclient!ab;III)I")
	public final int findTileLights(@OriginalArg(0) PointLight[] pointLights, @OriginalArg(1) int tileX, @OriginalArg(3) int tileZ) {
		@Pc(20) long lightUids = Scene.tileLightUids[this.level][tileX][tileZ];
		@Pc(22) long uidShift = 0L;
		@Pc(24) int lightCount = 0;
		@Pc(34) int local34;
		while (uidShift <= 48L) {
			int lightIndex = (int) (lightUids >> (int) uidShift & 0xFFFFL);
			if (lightIndex <= 0) {
				break;
			}
			pointLights[lightCount++] = Scene.aClass300Array1[lightIndex - 1].pointLight;
			uidShift += 16L;
		}
		for (int slot = lightCount; slot < 4; slot++) {
			pointLights[slot] = null;
		}
		return lightCount;
	}

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;III)Z")
	public abstract boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX);

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(Z)V")
	public abstract void removeTileShadow();

	@OriginalMember(owner = "client!ga", name = "l", descriptor = "(I)I")
	public int getPickSizeShift() {
		return 0;
	}

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(Z)I")
	public abstract int getModelMinY();
	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!ga;)V")
	public static void computeScreenPosition(@OriginalArg(0) Entity entity) {
		Scene.toolkit.H(entity.x, entity.y + (entity.getModelMinY() >> 1), entity.z, CS2Executor.projectionBuffer);
		entity.screenX = CS2Executor.projectionBuffer[0];
		entity.screenY = CS2Executor.projectionBuffer[1];
		entity.screenDepth = CS2Executor.projectionBuffer[2];
	}

	@OriginalMember(owner = "client!kd", name = "a", descriptor = "([Lclient!ga;II)V")
	public static void quicksortAbove(@OriginalArg(0) Entity[] entitys, @OriginalArg(1) int start, @OriginalArg(2) int end) {
		if (start >= end) {
			return;
		}
		@Pc(8) int mid = (start + end) / 2;
		@Pc(10) int storeIndex = start;
		@Pc(14) Entity entity = entitys[mid];
		entitys[mid] = entitys[end];
		entitys[end] = entity;
		@Pc(27) int screenDepth = entity.screenDepth;
		for (@Pc(29) int i = start; i < end; i++) {
			if (entitys[i].screenDepth > screenDepth + (i & 0x1)) {
				@Pc(44) Entity entity2 = entitys[i];
				entitys[i] = entitys[storeIndex];
				entitys[storeIndex++] = entity2;
			}
		}
		entitys[end] = entitys[storeIndex];
		entitys[storeIndex] = entity;
		quicksortAbove(entitys, start, storeIndex - 1);
		quicksortAbove(entitys, storeIndex + 1, end);
	}
}
