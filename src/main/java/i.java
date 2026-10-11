import com.jagex.game.runetek5.graphics.model.billboard.BillboardType;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;
import com.jagex.game.runetek5.statics.NativeModelScratch;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList;

@OriginalClass("client!i")
public final class i extends Model implements SoftwareObject {

	@OriginalMember(owner = "client!i", name = "nativeid", descriptor = "J")
	public long nativeid;

	@OriginalMember(owner = "client!i", name = "o", descriptor = "Lclient!oa;")
	private final oa anOa2;

	@OriginalMember(owner = "client!i", name = "r", descriptor = "Lclient!ya;")
	private final ya aYa1;

	@OriginalMember(owner = "client!i", name = "q", descriptor = "[Lclient!nn;")
	public EmissiveTriangle[] aClass227Array2;

	@OriginalMember(owner = "client!i", name = "p", descriptor = "[Lclient!eca;")
	public EffectiveVertex[] aClass87Array2;

	@OriginalMember(owner = "client!i", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;Lclient!bh;IIII)V")
	public i(@OriginalArg(0) oa arg0, @OriginalArg(1) ya arg1, @OriginalArg(2) Mesh mesh, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) int arg6) {
		this.anOa2 = arg0;
		this.aYa1 = arg1;
		this.aClass227Array2 = mesh.particleEmitters;
		this.aClass87Array2 = mesh.particleEffectors;
		@Pc(24) int local24 = mesh.particleEmitters == null ? 0 : mesh.particleEmitters.length;
		@Pc(33) int local33 = mesh.particleEffectors == null ? 0 : mesh.particleEffectors.length;
		@Pc(35) int local35 = 0;
		@Pc(42) int[] local42 = new int[local24 * 3 + local33];
		for (@Pc(44) int i = 0; i < local24; i++) {
			local42[local35++] = this.aClass227Array2[i].faceA;
			local42[local35++] = this.aClass227Array2[i].faceB;
			local42[local35++] = this.aClass227Array2[i].vertexC;
		}
		for (@Pc(78) int j = 0; j < local33; j++) {
			local42[local35++] = this.aClass87Array2[j].appliesOnVertex;
		}
		@Pc(101) int local101 = mesh.billboards == null ? 0 : mesh.billboards.length;
		@Pc(106) int[] local106 = new int[local101 * 8];
		@Pc(108) int local108 = 0;
		for (@Pc(110) int k = 0; k < local101; k++) {
			@Pc(116) MeshBillboard meshBillboard = mesh.billboards[k];
			@Pc(121) BillboardType lightType = BillboardTypeList.list(meshBillboard.billboardId);
			local106[local108++] = meshBillboard.appliesOnFace;
			local106[local108++] = lightType.size2d;
			local106[local108++] = lightType.size3d;
			local106[local108++] = lightType.materialId;
			local106[local108++] = lightType.blendType;
			local106[local108++] = lightType.shape;
			local106[local108++] = lightType.hasUid ? -1 : 0;
		}
		for (@Pc(173) int m = 0; m < local101; m++) {
			@Pc(179) MeshBillboard meshBillboard2 = mesh.billboards[m];
			local106[local108++] = meshBillboard2.depth;
		}
		this.R(this.anOa2, this.aYa1, mesh.numVertices, mesh.maxDepth, mesh.verticesX, mesh.verticesY, mesh.verticesZ, mesh.vertexGroups, mesh.verticesSubmeshes, mesh.numFaces, mesh.facesA, mesh.facesB, mesh.facesC, mesh.faceRenderType, mesh.facesLayer, mesh.facesAlpha, mesh.facesTexture, mesh.facesColor, mesh.facesMaterial, mesh.faceGroups, mesh.priority, mesh.facesSubmeshes, mesh.numTextures, mesh.texturesMapping, mesh.textureFaceX, mesh.textureFaceY, mesh.textureFaceZ, mesh.texturesScaleX, mesh.texturesScaleY, mesh.texturesScaleZ, mesh.texturesRotation, mesh.texturesDirection, mesh.texturesSpeed, mesh.texturesUtrans, mesh.texturesVtrans, local42, local24, local33, arg3, arg4, arg5, arg6, local106);
	}

	@OriginalMember(owner = "client!i", name = "<init>", descriptor = "(Lclient!oa;)V")
	public i(@OriginalArg(0) oa arg0) {
		this.anOa2 = arg0;
		this.aYa1 = null;
		this.oa(arg0);
	}

	@OriginalMember(owner = "client!i", name = "C", descriptor = "(I)V")
	public native void C(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "k", descriptor = "(I)V")
	public native void k(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "w", descriptor = "(Z)V")
	public native void w(@OriginalArg(0) boolean arg0);

	@OriginalMember(owner = "client!i", name = "O", descriptor = "(III)V")
	public native void O(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2);

	@OriginalMember(owner = "client!i", name = "s", descriptor = "(I)V")
	public native void s(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "LA", descriptor = "(I)V")
	public native void LA(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "fa", descriptor = "()I")
	public native int fa();

	@OriginalMember(owner = "client!i", name = "NA", descriptor = "()Z")
	public native boolean NA();

	@OriginalMember(owner = "client!i", name = "F", descriptor = "()Z")
	public native boolean F();

	@OriginalMember(owner = "client!i", name = "ZA", descriptor = "(Lclient!i;Lclient!i;IZZ)V")
	public native void ZA(@OriginalArg(0) i arg0, @OriginalArg(1) i arg1, @OriginalArg(2) int arg2, @OriginalArg(3) boolean arg3, @OriginalArg(4) boolean arg4);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	@Override
	public void shareLight(@OriginalArg(0) Model model, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) boolean arg4) {
		this.anOa2.method6405().method3(this, model, arg1, arg2, arg3, arg4);
	}

	@OriginalMember(owner = "client!i", name = "aa", descriptor = "(SS)V")
	public native void aa(@OriginalArg(0) short arg0, @OriginalArg(1) short arg1);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	@Override
	public boolean pickedOrtho(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5) {
		return this.anOa2.method6405().method2(this, arg0, arg1, matrix, arg3, arg5);
	}

	@OriginalMember(owner = "client!i", name = "EA", descriptor = "()I")
	public native int EA();

	@OriginalMember(owner = "client!i", name = "d", descriptor = "()[Lclient!nn;")
	@Override
	public EmissiveTriangle[] getEmissiveTriangles() {
		return this.aClass227Array2;
	}

	@OriginalMember(owner = "client!i", name = "wa", descriptor = "()V")
	public native void wa();

	@OriginalMember(owner = "client!i", name = "P", descriptor = "(IIII)V")
	public native void P(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	@Override
	public void renderOrtho(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon pickingCylinder, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
		if (pickingCylinder == null) {
			this.anOa2.method6405().method8(this, matrix, null, arg2, arg3);
			return;
		}
		NativeModelScratch.pickingResult[5] = 0;
		this.anOa2.method6405().method8(this, matrix, NativeModelScratch.pickingResult, arg2, arg3);
		pickingCylinder.left = NativeModelScratch.pickingResult[0];
		pickingCylinder.bottom = NativeModelScratch.pickingResult[1];
		pickingCylinder.right = NativeModelScratch.pickingResult[2];
		pickingCylinder.top = NativeModelScratch.pickingResult[3];
		pickingCylinder.size = NativeModelScratch.pickingResult[4];
		pickingCylinder.positionCalculated = NativeModelScratch.pickingResult[5] != 0;
	}

	@OriginalMember(owner = "client!i", name = "v", descriptor = "()V")
	public native void v();

	@OriginalMember(owner = "client!i", name = "l", descriptor = "(JI[IIIIIZ)V")
	private native void l(@OriginalArg(0) long arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int[] arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) int arg6, @OriginalArg(7) boolean arg7);

	@OriginalMember(owner = "client!i", name = "c", descriptor = "()Z")
	@Override
	public boolean loadedTextures() {
		return true;
	}

	@OriginalMember(owner = "client!i", name = "e", descriptor = "()V")
	@Override
	public void unlock() {
		if (this.anOa2.anInt7473 > 1) {
			synchronized (this) {
				super.isLocked = false;
				this.notifyAll();
			}
		}
	}

	@OriginalMember(owner = "client!i", name = "VA", descriptor = "(I)V")
	public native void VA(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	@Override
	public boolean picked(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean arg3, @OriginalArg(4) int arg4) {
		return this.anOa2.method6405().method5(this, arg0, arg1, matrix, arg3);
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(BIZ)Lclient!ka;")
	@Override
	public Model copy(@OriginalArg(0) byte arg0, @OriginalArg(1) int arg1, @OriginalArg(2) boolean arg2) {
		return this.anOa2.method6405().method11(this, arg0, arg1, arg2);
	}

	@OriginalMember(owner = "client!i", name = "na", descriptor = "()I")
	public native int na();

	@OriginalMember(owner = "client!i", name = "HA", descriptor = "()I")
	public native int HA();

	@OriginalMember(owner = "client!i", name = "b", descriptor = "()V")
	@Override
	public void uploadToGpu() {
	}

	@OriginalMember(owner = "client!i", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	public native void p(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) Ground arg2, @OriginalArg(3) Ground arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) int arg6);

	@OriginalMember(owner = "client!i", name = "V", descriptor = "()I")
	public native int V();

	@OriginalMember(owner = "client!i", name = "da", descriptor = "()I")
	public native int da();

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(I[IIIIIZ)V")
	@Override
	public void applyTransformUnmasked(@OriginalArg(0) int arg0, @OriginalArg(1) int[] arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) boolean arg6) {
		this.l(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	@OriginalMember(owner = "client!i", name = "G", descriptor = "()I")
	public native int G();

	@OriginalMember(owner = "client!i", name = "ma", descriptor = "()I")
	public native int ma();

	@OriginalMember(owner = "client!i", name = "R", descriptor = "(Lclient!oa;Lclient!ya;II[I[I[I[I[SI[S[S[S[B[B[B[B[S[S[IB[SI[B[S[S[S[I[I[I[B[B[I[I[I[IIIIIII[I)V")
	private native void R(@OriginalArg(0) oa arg0, @OriginalArg(1) ya arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int[] arg4, @OriginalArg(5) int[] arg5, @OriginalArg(6) int[] arg6, @OriginalArg(7) int[] arg7, @OriginalArg(8) short[] arg8, @OriginalArg(9) int arg9, @OriginalArg(10) short[] arg10, @OriginalArg(11) short[] arg11, @OriginalArg(12) short[] arg12, @OriginalArg(13) byte[] arg13, @OriginalArg(14) byte[] arg14, @OriginalArg(15) byte[] arg15, @OriginalArg(16) byte[] arg16, @OriginalArg(17) short[] arg17, @OriginalArg(18) short[] arg18, @OriginalArg(19) int[] arg19, @OriginalArg(20) byte arg20, @OriginalArg(21) short[] arg21, @OriginalArg(22) int arg22, @OriginalArg(23) byte[] arg23, @OriginalArg(24) short[] arg24, @OriginalArg(25) short[] arg25, @OriginalArg(26) short[] arg26, @OriginalArg(27) int[] arg27, @OriginalArg(28) int[] arg28, @OriginalArg(29) int[] arg29, @OriginalArg(30) byte[] arg30, @OriginalArg(31) byte[] arg31, @OriginalArg(32) int[] arg32, @OriginalArg(33) int[] arg33, @OriginalArg(34) int[] arg34, @OriginalArg(35) int[] arg35, @OriginalArg(36) int arg36, @OriginalArg(37) int arg37, @OriginalArg(38) int arg38, @OriginalArg(39) int arg39, @OriginalArg(40) int arg40, @OriginalArg(41) int arg41, @OriginalArg(42) int[] arg42);

	@OriginalMember(owner = "client!i", name = "FA", descriptor = "(I)V")
	public native void FA(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IIII)V")
	@Override
	public void adjustColours(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
	}

	@OriginalMember(owner = "client!i", name = "ia", descriptor = "(SS)V")
	public native void ia(@OriginalArg(0) short arg0, @OriginalArg(1) short arg1);

	@OriginalMember(owner = "client!i", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this);
		}
	}

	@OriginalMember(owner = "client!i", name = "WA", descriptor = "()I")
	public native int WA();

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;IZ)V")
	@Override
	public void transform(@OriginalArg(0) Matrix matrix, @OriginalArg(1) int arg1, @OriginalArg(2) boolean arg2) {
		this.A(((ja) matrix).nativeid, arg1, arg2);
	}

	@OriginalMember(owner = "client!i", name = "f", descriptor = "()V")
	@Override
	public void lock() {
		if (this.anOa2.anInt7473 <= 1) {
			return;
		}
		synchronized (this) {
			while (super.isLocked) {
				try {
					this.wait();
				} catch (@Pc(13) InterruptedException ex) {
				}
			}
			super.isLocked = true;
		}
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	@Override
	public void render(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon pickingCylinder, @OriginalArg(2) int arg2) {
		if (pickingCylinder == null) {
			this.anOa2.method6405().method4(this, matrix, null, arg2);
			return;
		}
		NativeModelScratch.pickingResult[5] = 0;
		this.anOa2.method6405().method4(this, matrix, NativeModelScratch.pickingResult, arg2);
		pickingCylinder.left = NativeModelScratch.pickingResult[0];
		pickingCylinder.bottom = NativeModelScratch.pickingResult[1];
		pickingCylinder.right = NativeModelScratch.pickingResult[2];
		pickingCylinder.top = NativeModelScratch.pickingResult[3];
		pickingCylinder.size = NativeModelScratch.pickingResult[4];
		pickingCylinder.positionCalculated = NativeModelScratch.pickingResult[5] != 0;
	}

	@OriginalMember(owner = "client!i", name = "r", descriptor = "()Z")
	public native boolean r();

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void updateParticlePosition(@OriginalArg(0) Matrix matrix) {
		this.method4358(NativeModelScratch.effectVertices, matrix);
		@Pc(5) int local5 = 0;
		@Pc(10) int local10;
		if (this.aClass227Array2 != null) {
			for (local10 = 0; local10 < this.aClass227Array2.length; local10++) {
				@Pc(16) EmissiveTriangle modelParticleEmitter = this.aClass227Array2[local10];
				modelParticleEmitter.verticeZC = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeXC = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeYA = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeZA = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeXA = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeZB = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeYB = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.transformedCY = NativeModelScratch.effectVertices[local5++];
				modelParticleEmitter.verticeYC = NativeModelScratch.effectVertices[local5++];
			}
		}
		if (this.aClass87Array2 == null) {
			return;
		}
		for (local10 = 0; local10 < this.aClass87Array2.length; local10++) {
			@Pc(87) EffectiveVertex modelParticleEffector = this.aClass87Array2[local10];
			@Pc(89) EffectiveVertex modelParticleEffector2 = modelParticleEffector;
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child;
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = matrix.copy();
			} else {
				modelParticleEffector.matrix.apply(matrix);
			}
			modelParticleEffector2.verticeZ = NativeModelScratch.effectVertices[local5++];
			modelParticleEffector2.verticeX = NativeModelScratch.effectVertices[local5++];
			modelParticleEffector2.verticeY = NativeModelScratch.effectVertices[local5++];
		}
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(I)V")
	public native void a(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!i", name = "a", descriptor = "()[Lclient!eca;")
	@Override
	public EffectiveVertex[] getEffectiveVertices() {
		return this.aClass87Array2;
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "([ILclient!bca;)V")
	private void method4358(@OriginalArg(0) int[] arg0, @OriginalArg(1) Matrix matrix) {
		this.anOa2.method6405().method10(this, arg0, matrix);
	}

	@OriginalMember(owner = "client!i", name = "ua", descriptor = "()I")
	public native int ua();

	@OriginalMember(owner = "client!i", name = "H", descriptor = "(III)V")
	public native void H(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2);

	@OriginalMember(owner = "client!i", name = "A", descriptor = "(JIZ)V")
	private native void A(@OriginalArg(0) long arg0, @OriginalArg(1) int arg1, @OriginalArg(2) boolean arg2);

	@OriginalMember(owner = "client!i", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	public native Shadow ba(@OriginalArg(0) Shadow arg0);

	@OriginalMember(owner = "client!i", name = "oa", descriptor = "(Lclient!oa;)V")
	private native void oa(@OriginalArg(0) oa arg0);

	@OriginalMember(owner = "client!i", name = "RA", descriptor = "()I")
	public native int RA();

	@OriginalMember(owner = "client!i", name = "I", descriptor = "(I[IIIIZI[I)V")
	public native void I(@OriginalArg(0) int arg0, @OriginalArg(1) int[] arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5, @OriginalArg(6) int arg6, @OriginalArg(7) int[] arg7);
}
