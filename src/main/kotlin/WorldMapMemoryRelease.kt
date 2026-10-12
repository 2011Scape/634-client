import com.jagex.game.runetek5.scene.Scene
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.TypeLists

object WorldMapMemoryRelease {

	@OriginalMember(owner = "client!ek", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun resetSceneAndCaches() {
		Scene.destroy()
		@Pc(8) var i: Int = 0
		while (i < 4) {
			MapRegion.aClass104Array3[i]!!.reset()
			i++
		}
		ComponentMinimap.resetMinimap()
		TypeLists.resetAllCaches()
		Static683.method656()
		System.gc()
		ClientState.activeToolkit!!.ya()
	}
}
