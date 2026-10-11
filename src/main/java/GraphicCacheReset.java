import com.jagex.game.runetek5.PlayerInfoBitDecoder;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.graphics.hint.HintTrail;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.entity.PlayerEntity;

public final class GraphicCacheReset {

	@OriginalMember(owner = "client!tf", name = "b", descriptor = "(I)V")
	public static void resetGraphicCaches() {
		@Pc(5) int featureMask = 0;
		if (GamePreferences.uid.lightDetail.getValue() == 1) {
			featureMask = 55;
		}
		if (GamePreferences.uid.textures.getValue() == 0) {
			featureMask |= 0x40;
		}
		Component.setPreferenceMask(featureMask);
		TypeLists.LOC.setFeatureMask(featureMask);
		TypeLists.OBJ.setFeatureMask(featureMask);
		TypeLists.NPC.setFeatureMask(featureMask);
		TypeLists.SPOT_ANIM.setFeatureMask(featureMask);
		PlayerEntity.setPlayerHintFeatureMask(featureMask);
		HintArrow.setHintPreferenceFlags(featureMask);
		HintTrail.setTrailPreferenceFlags(featureMask);
		ItemContainer.setFeatureMask(featureMask);
		MapRebuild.rebuildMap();
	}
}
