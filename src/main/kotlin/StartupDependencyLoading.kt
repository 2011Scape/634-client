import com.jagex.game.runetek5.LoadingRequirement
import com.jagex.game.runetek5.EmailStatus
import com.jagex.game.runetek5.MusicTrackProt
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck
import com.jagex.game.runetek5.client.loading.loader.Js5FileLoader
import com.jagex.game.runetek5.client.loading.loader.Js5GroupLoader
import com.jagex.game.runetek5.js5.dependency.CacheableResource
import com.jagex.game.runetek5.js5.dependency.CacheableDLL
import com.jagex.game.runetek5.client.ClientStartup
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.js5.dependency.CacheableDependencies
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraManager

object StartupDependencyLoading {

	@OriginalMember(owner = "client!pb", name = "a", descriptor = "(B)I")
	@JvmStatic
	fun loadDependencies(): Int {
		if (ClientStartup.loadingStatusDependencies == 0) {
			CacheableDependencies.JACLIB.setLoader(CacheableDLL("jaclib"))
			if (CacheableDependencies.JACLIB.getLoader()!!.completePercentage() != 100) {
				return 1
			}
			if (!(CacheableDependencies.JACLIB.getLoader() as CacheableDLL).isDone()) {
				ActiveClient.activeClient!!.loadJaclib()
			}
			ClientStartup.loadingStatusDependencies = 1
		}
		@Pc(234) var local234: Int
		@Pc(253) var loadingPercentDependencies: Int
		@Pc(267) var local267: Int
		@Pc(273) var local273: Int
		if (ClientStartup.loadingStatusDependencies == 1) {
			ClientStartup.fullyCachedDependencies = CacheableDependencies.getAll()
			CacheableDependencies.DEFAULTS.setLoader(CacheableResource(Resource.DEFAULTS))
			CacheableDependencies.JAGGL.setLoader(CacheableDLL("jaggl"))
			CacheableDependencies.JAGDX.setLoader(CacheableDLL("jagdx"))
			CacheableDependencies.JAGMISC.setLoader(CacheableDLL("jagmisc"))
			CacheableDependencies.SW3D.setLoader(CacheableDLL("sw3d"))
			CacheableDependencies.HW3D.setLoader(CacheableDLL("hw3d"))
			CacheableDependencies.JAGTHEORA.setLoader(CacheableDLL("jagtheora"))
			CacheableDependencies.SHADERS.setLoader(CacheableResource(Resource.SHADER))
			CacheableDependencies.MATERIALS.setLoader(CacheableResource(Resource.MATERIAL))
			CacheableDependencies.CONFIG.setLoader(CacheableResource(Resource.CONFIG))
			CacheableDependencies.LOC.setLoader(CacheableResource(Resource.LOC))
			CacheableDependencies.ENUM.setLoader(CacheableResource(Resource.ENUM))
			CacheableDependencies.NPC.setLoader(CacheableResource(Resource.NPC))
			CacheableDependencies.OBJ.setLoader(CacheableResource(Resource.OBJ))
			CacheableDependencies.SEQ.setLoader(CacheableResource(Resource.SEQ))
			CacheableDependencies.GFX.setLoader(CacheableResource(Resource.GFX))
			CacheableDependencies.VARBIT.setLoader(CacheableResource(Resource.VARBIT))
			CacheableDependencies.QC_MSG.setLoader(CacheableResource(Resource.QC_MESSAGES))
			CacheableDependencies.QC_MENU.setLoader(CacheableResource(Resource.QC_MENU))
			CacheableDependencies.PARTICLES.setLoader(CacheableResource(Resource.PARTICLE))
			CacheableDependencies.BILLBOARDS.setLoader(CacheableResource(Resource.BILLBOARD))
			CacheableDependencies.HUFFMAN.setLoader(Js5FileLoader(Resource.HUFFMAN, "huffman"))
			CacheableDependencies.INTERFACES.setLoader(CacheableResource(Resource.INTERFACE))
			CacheableDependencies.CS2.setLoader(CacheableResource(Resource.CS2))
			CacheableDependencies.FONTS.setLoader(CacheableResource(Resource.FONT_METRICS))
			CacheableDependencies.WORLD_MAP.setLoader(Js5GroupLoader(Resource.WORLD_MAP, "details"))
			var dependencyIndex: Int = 0
			while (dependencyIndex < ClientStartup.fullyCachedDependencies!!.size) {
				if (ClientStartup.fullyCachedDependencies!![dependencyIndex]!!.getLoader() == null) {
					throw RuntimeException()
				}
				dependencyIndex++
			}
			loadingPercentDependencies = 0
			@Pc(255) val loadingRequirements: Array<LoadingRequirement?> = ClientStartup.fullyCachedDependencies!!
			@Pc(257) var i: Int = 0
			while (i < loadingRequirements.size) {
				@Pc(263) val loadingRequirement: LoadingRequirement? = loadingRequirements[i]
				val requirementSize: Int = loadingRequirement!!.getSize()
				val loaderPercent: Int = loadingRequirement.getLoader()!!.completePercentage()
				loadingPercentDependencies += loaderPercent * requirementSize / 100
				i++
			}
			ClientStartup.loadingPercentDependencies = loadingPercentDependencies
			ClientStartup.loadingStatusDependencies = 2
		}
		if (ClientStartup.fullyCachedDependencies == null) {
			return 100
		}
		var totalSize: Int = 0
		loadingPercentDependencies = 0
		@Pc(300) var fullyCached: Boolean = true
		@Pc(307) val loadingRequirements2: Array<LoadingRequirement?> = ClientStartup.fullyCachedDependencies!!
		@Pc(309) var j: Int = 0
		while (j < loadingRequirements2.size) {
			@Pc(315) val loadingRequirement2: LoadingRequirement? = loadingRequirements2[j]
			val requirementSize: Int = loadingRequirement2!!.getSize()
			@Pc(327) val completionPercent: Int = loadingRequirement2.getLoader()!!.completePercentage()
			totalSize += requirementSize
			loadingPercentDependencies += completionPercent * requirementSize / 100
			if (completionPercent < 100) {
				fullyCached = false
			}
			j++
		}
		if (fullyCached) {
			if (!(CacheableDependencies.JAGMISC.getLoader() as CacheableDLL).isDone()) {
				ActiveClient.activeClient!!.loadJagmisc()
			}
			if (!(CacheableDependencies.JAGTHEORA.getLoader() as CacheableDLL).isDone()) {
				JagTheoraManager.jagtheoraLoaded = ActiveClient.activeClient!!.loadJagtheora()
			}
			ClientStartup.fullyCachedDependencies = null
		}
		totalSize -= ClientStartup.loadingPercentDependencies
		loadingPercentDependencies -= ClientStartup.loadingPercentDependencies
		var percentComplete: Int = if (totalSize <= 0) 100 else loadingPercentDependencies * 100 / totalSize
		if (!fullyCached && percentComplete > 99) {
			percentComplete = 99
		}
		return percentComplete
	}
}
