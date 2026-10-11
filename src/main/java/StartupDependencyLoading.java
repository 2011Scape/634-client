import com.jagex.game.runetek5.LoadingRequirement;
import com.jagex.game.runetek5.EmailStatus;
import com.jagex.game.runetek5.MusicTrackProt;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck;
import com.jagex.game.runetek5.client.loading.loader.Js5FileLoader;
import com.jagex.game.runetek5.client.loading.loader.Js5GroupLoader;
import com.jagex.game.runetek5.js5.dependency.CacheableResource;
import com.jagex.game.runetek5.js5.dependency.CacheableDLL;
import com.jagex.game.runetek5.client.ClientStartup;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.js5.dependency.CacheableDependencies;
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraManager;

public final class StartupDependencyLoading {

	@OriginalMember(owner = "client!pb", name = "a", descriptor = "(B)I")
	public static int loadDependencies() {
		if (ClientStartup.loadingStatusDependencies == 0) {
			CacheableDependencies.JACLIB.setLoader(new CacheableDLL("jaclib"));
			if (CacheableDependencies.JACLIB.getLoader().completePercentage() != 100) {
				return 1;
			}
			if (!((CacheableDLL) CacheableDependencies.JACLIB.getLoader()).isDone()) {
				ActiveClient.activeClient.loadJaclib();
			}
			ClientStartup.loadingStatusDependencies = 1;
		}
		@Pc(234) int local234;
		@Pc(253) int loadingPercentDependencies;
		@Pc(267) int local267;
		@Pc(273) int local273;
		if (ClientStartup.loadingStatusDependencies == 1) {
			ClientStartup.fullyCachedDependencies = CacheableDependencies.getAll();
			CacheableDependencies.DEFAULTS.setLoader(new CacheableResource(Resource.DEFAULTS));
			CacheableDependencies.JAGGL.setLoader(new CacheableDLL("jaggl"));
			CacheableDependencies.JAGDX.setLoader(new CacheableDLL("jagdx"));
			CacheableDependencies.JAGMISC.setLoader(new CacheableDLL("jagmisc"));
			CacheableDependencies.SW3D.setLoader(new CacheableDLL("sw3d"));
			CacheableDependencies.HW3D.setLoader(new CacheableDLL("hw3d"));
			CacheableDependencies.JAGTHEORA.setLoader(new CacheableDLL("jagtheora"));
			CacheableDependencies.SHADERS.setLoader(new CacheableResource(Resource.SHADER));
			CacheableDependencies.MATERIALS.setLoader(new CacheableResource(Resource.MATERIAL));
			CacheableDependencies.CONFIG.setLoader(new CacheableResource(Resource.CONFIG));
			CacheableDependencies.LOC.setLoader(new CacheableResource(Resource.LOC));
			CacheableDependencies.ENUM.setLoader(new CacheableResource(Resource.ENUM));
			CacheableDependencies.NPC.setLoader(new CacheableResource(Resource.NPC));
			CacheableDependencies.OBJ.setLoader(new CacheableResource(Resource.OBJ));
			CacheableDependencies.SEQ.setLoader(new CacheableResource(Resource.SEQ));
			CacheableDependencies.GFX.setLoader(new CacheableResource(Resource.GFX));
			CacheableDependencies.VARBIT.setLoader(new CacheableResource(Resource.VARBIT));
			CacheableDependencies.QC_MSG.setLoader(new CacheableResource(Resource.QC_MESSAGES));
			CacheableDependencies.QC_MENU.setLoader(new CacheableResource(Resource.QC_MENU));
			CacheableDependencies.PARTICLES.setLoader(new CacheableResource(Resource.PARTICLE));
			CacheableDependencies.BILLBOARDS.setLoader(new CacheableResource(Resource.BILLBOARD));
			CacheableDependencies.HUFFMAN.setLoader(new Js5FileLoader(Resource.HUFFMAN, "huffman"));
			CacheableDependencies.INTERFACES.setLoader(new CacheableResource(Resource.INTERFACE));
			CacheableDependencies.CS2.setLoader(new CacheableResource(Resource.CS2));
			CacheableDependencies.FONTS.setLoader(new CacheableResource(Resource.FONT_METRICS));
			CacheableDependencies.WORLD_MAP.setLoader(new Js5GroupLoader(Resource.WORLD_MAP, "details"));
			for (int dependencyIndex = 0; dependencyIndex < ClientStartup.fullyCachedDependencies.length; dependencyIndex++) {
				if (ClientStartup.fullyCachedDependencies[dependencyIndex].getLoader() == null) {
					throw new RuntimeException();
				}
			}
			loadingPercentDependencies = 0;
			@Pc(255) LoadingRequirement[] loadingRequirements = ClientStartup.fullyCachedDependencies;
			for (@Pc(257) int i = 0; i < loadingRequirements.length; i++) {
				@Pc(263) LoadingRequirement loadingRequirement = loadingRequirements[i];
				int requirementSize = loadingRequirement.getSize();
				int loaderPercent = loadingRequirement.getLoader().completePercentage();
				loadingPercentDependencies += loaderPercent * requirementSize / 100;
			}
			ClientStartup.loadingPercentDependencies = loadingPercentDependencies;
			ClientStartup.loadingStatusDependencies = 2;
		}
		if (ClientStartup.fullyCachedDependencies == null) {
			return 100;
		}
		int totalSize = 0;
		loadingPercentDependencies = 0;
		@Pc(300) boolean fullyCached = true;
		@Pc(307) LoadingRequirement[] loadingRequirements2 = ClientStartup.fullyCachedDependencies;
		for (@Pc(309) int j = 0; j < loadingRequirements2.length; j++) {
			@Pc(315) LoadingRequirement loadingRequirement2 = loadingRequirements2[j];
			int requirementSize = loadingRequirement2.getSize();
			@Pc(327) int completionPercent = loadingRequirement2.getLoader().completePercentage();
			totalSize += requirementSize;
			loadingPercentDependencies += completionPercent * requirementSize / 100;
			if (completionPercent < 100) {
				fullyCached = false;
			}
		}
		if (fullyCached) {
			if (!((CacheableDLL) CacheableDependencies.JAGMISC.getLoader()).isDone()) {
				ActiveClient.activeClient.loadJagmisc();
			}
			if (!((CacheableDLL) CacheableDependencies.JAGTHEORA.getLoader()).isDone()) {
				JagTheoraManager.jagtheoraLoaded = ActiveClient.activeClient.loadJagtheora();
			}
			ClientStartup.fullyCachedDependencies = null;
		}
		totalSize -= ClientStartup.loadingPercentDependencies;
		loadingPercentDependencies -= ClientStartup.loadingPercentDependencies;
		int percentComplete = totalSize <= 0 ? 100 : loadingPercentDependencies * 100 / totalSize;
		if (!fullyCached && percentComplete > 99) {
			percentComplete = 99;
		}
		return percentComplete;
	}
}
