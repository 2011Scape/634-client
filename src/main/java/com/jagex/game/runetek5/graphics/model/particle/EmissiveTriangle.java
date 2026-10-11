package com.jagex.game.runetek5.graphics.model.particle;

import com.jagex.game.runetek5.config.particle.ParticleProducerType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!nn")
public final class EmissiveTriangle {

	@OriginalMember(owner = "client!nn", name = "m", descriptor = "[I")
	public static final int[] BIT_MASKS = new int[32];

	@OriginalMember(owner = "client!nn", name = "b", descriptor = "I")
	public int transformedCY;

	@OriginalMember(owner = "client!nn", name = "d", descriptor = "I")
	public int verticeYB;

	@OriginalMember(owner = "client!nn", name = "h", descriptor = "I")
	public int verticeZB;

	@OriginalMember(owner = "client!nn", name = "j", descriptor = "I")
	public int verticeZC;

	@OriginalMember(owner = "client!nn", name = "k", descriptor = "I")
	public int verticeZA;

	@OriginalMember(owner = "client!nn", name = "l", descriptor = "I")
	public int verticeYC;

	@OriginalMember(owner = "client!nn", name = "p", descriptor = "Lclient!nn;")
	public EmissiveTriangle child;

	@OriginalMember(owner = "client!nn", name = "q", descriptor = "I")
	public int verticeYA;

	@OriginalMember(owner = "client!nn", name = "r", descriptor = "I")
	public int verticeXA;

	@OriginalMember(owner = "client!nn", name = "s", descriptor = "I")
	public int verticeXC;

	@OriginalMember(owner = "client!nn", name = "n", descriptor = "I")
	private final int id;

	@OriginalMember(owner = "client!nn", name = "c", descriptor = "B")
	public final byte priority;

	@OriginalMember(owner = "client!nn", name = "i", descriptor = "I")
	public final int faceA;

	@OriginalMember(owner = "client!nn", name = "t", descriptor = "I")
	public final int vertexC;

	@OriginalMember(owner = "client!nn", name = "e", descriptor = "I")
	public final int faceB;

	static {
		@Pc(4) int powerOfTwo = 2;
		for (@Pc(6) int index = 0; index < 32; index++) {
			BIT_MASKS[index] = powerOfTwo - 1;
			powerOfTwo += powerOfTwo;
		}
	}

	@OriginalMember(owner = "client!nn", name = "<init>", descriptor = "(IIIIB)V")
	public EmissiveTriangle(@OriginalArg(0) int producerId, @OriginalArg(1) int faceA, @OriginalArg(2) int faceB, @OriginalArg(3) int faceC, @OriginalArg(4) byte priority) {
		this.id = producerId;
		this.priority = priority;
		this.faceA = faceA;
		this.vertexC = faceC;
		this.faceB = faceB;
	}

	@OriginalMember(owner = "client!nn", name = "a", descriptor = "(IIII)Lclient!nn;")
	public EmissiveTriangle copyWithFaces(@OriginalArg(0) int faceB, @OriginalArg(2) int faceC, @OriginalArg(3) int faceA) {
		return new EmissiveTriangle(this.id, faceA, faceB, faceC, this.priority);
	}

	@OriginalMember(owner = "client!nn", name = "a", descriptor = "(B)Lclient!qb;")
	public ParticleProducerType getProducer() {
		return ParticleProducerType.get(this.id);
	}
}
