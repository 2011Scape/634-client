import com.jagex.game.runetek5.scene.culling.CullingCluster;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.client.ClientState;

public final class SceneCullingInit {

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(I)V")
	public static void initializeCulling() {
		Culling.tileDrawCycles = new int[Scene.length][Scene.sceneWidth + 1][Camera.sceneLength + 1];
		Culling.dynamicCullingClusterCount = 0;
		Culling.occluderMinCameraDistance = Culling.fullTileDimension;
		Culling.tileCullingCluster = new CullingCluster[2000];
		Culling.fullTileDimensionY = Culling.fullTileDimension;
		Culling.tileCullingClustersBuilt = false;
		Culling.aClass82Array3 = new CullingCluster[500];
		Culling.tileCullingClusterCount = 0;
		Culling.groundCullingClusterCount = 0;
		Culling.aClass82Array4 = new CullingCluster[1000];
		Culling.activeOccluderCount = 0;
		// Not part of the original client: room for the occluders of a longer draw distance.
		Culling.members = new CullingCluster[Math.max(500, (int) (500.0D * ClientFeatures.RENDER_DISTANCE))];
		if (Scene.toolkit instanceof oa) {
			Culling.cullingEnabled = false;
		} else {
			Culling.cullingEnabled = true;
		}
	}
}
