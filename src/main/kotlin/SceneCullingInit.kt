import com.jagex.game.runetek5.scene.culling.CullingCluster
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.client.ClientState

object SceneCullingInit {

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun initializeCulling() {
		Culling.tileDrawCycles = Array<Array<IntArray?>?>(Scene.length) { Array<IntArray?>(Scene.sceneWidth + 1) { IntArray(Camera.sceneLength + 1) } }
		Culling.dynamicCullingClusterCount = 0
		Culling.occluderMinCameraDistance = Culling.fullTileDimension
		Culling.tileCullingCluster = arrayOfNulls<CullingCluster>(2000)
		Culling.fullTileDimensionY = Culling.fullTileDimension
		Culling.tileCullingClustersBuilt = false
		Culling.aClass82Array3 = arrayOfNulls<CullingCluster>(500)
		Culling.tileCullingClusterCount = 0
		Culling.groundCullingClusterCount = 0
		Culling.aClass82Array4 = arrayOfNulls<CullingCluster>(1000)
		Culling.activeOccluderCount = 0
		// Not part of the original client: room for the occluders of a longer draw distance.
		Culling.members = arrayOfNulls<CullingCluster>(java.lang.Math.max(500, (500.0 * ClientFeatures.RENDER_DISTANCE).toInt()))
		if (Scene.toolkit is oa) {
			Culling.cullingEnabled = false
		} else {
			Culling.cullingEnabled = true
		}
	}
}
