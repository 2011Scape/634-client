import com.jagex.game.runetek5.scene.Scene;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.TypeLists;

public final class WorldMapMemoryRelease {

	@OriginalMember(owner = "client!ek", name = "b", descriptor = "(I)V")
	public static void resetSceneAndCaches() {
		Scene.destroy();
		for (@Pc(8) int i = 0; i < 4; i++) {
			MapRegion.aClass104Array3[i].reset();
		}
		ComponentMinimap.resetMinimap();
		TypeLists.resetAllCaches();
		Static683.method656();
		System.gc();
		ClientState.activeToolkit.ya();
	}
}
