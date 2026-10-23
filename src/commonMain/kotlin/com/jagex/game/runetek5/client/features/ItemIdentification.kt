package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Item Identification. Seeds, herbs, logs, ores, bars, gems,
 * potions, tablets, jewellery and the other kinds switched on carry a short name (or a longer one) at the
 * bottom left of their slot wherever the interfaces show them (backpack, bank, equipment), noted ones too.
 *
 * The names are RuneLite's (its ItemIdentification), kept for the items whose OSRS id is the same item on
 * 634 (Void's names agree): 304 of RuneLite's 545. The OSRS-only ones (coral, sap-treated logs, repair
 * kits, most yarn and cloth, the Zeah and Varlamore items, divine and other later potions) find nothing, so
 * their categories have nothing to show. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ItemIdentification : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("itemidentification", "Item Identification", "1", PluginApi.VERSION, "Labels seeds, herbs, ores, potions and other items with a short name on their slot.", "Pixel")

	/** RuneLite's ItemIdentification, the items 634 has: type, medium name, short name, the item ids. */
	const val ITEMS: String = "SEED_HERB:Guam:G:5291|SEED_HERB:Marren:M:5292|SEED_HERB:Tarro:TAR:5293|SEED_HERB:Harra:HAR:5294|SEED_HERB:Ranarr:R:5295|SEED_HERB:Toad:TOA:5296|SEED_HERB:Irit:I:5297|SEED_HERB:Avan:A:5298|SEED_HERB:Kwuarm:K:5299|SEED_HERB:Snap:S:5300|SEED_HERB:Cadan:C:5301|SEED_HERB:Lanta:L:5302|SEED_HERB:Dwarf:D:5303|SEED_HERB:Torstol:TOR:5304|SEED_BERRY:Redberry:RED:5101|SEED_BERRY:Cadava:CAD:5102|SEED_BERRY:Dwell:DWEL:5103|SEED_BERRY:Janger:JANG:5104|SEED_BERRY:White:WHIT:5105|SEED_BERRY:Ivy:POI:5106|SEED_SPECIAL:Mushroom:MUSH:5282|SEED_SPECIAL:Bella:BELL:5281|SEED_SPECIAL:Cactus:CAC:5280|SEED_TREE:Oak:OAK:5312|SEED_TREE:Willow:WIL:5313|SEED_TREE:Maple:MAP:5314|SEED_TREE:Yew:YEW:5315|SEED_TREE:Magic:MAG:5316|SEED_TREE:Spirit:SPI:5317|SEED_TREE:Calquat:CAL:5290|SEED_FRUIT_TREE:Apple:APP:5283|SEED_FRUIT_TREE:Banana:BAN:5284|SEED_FRUIT_TREE:Orange:ORA:5285|SEED_FRUIT_TREE:Curry:CUR:5286|SEED_FRUIT_TREE:Pine.A:PINE:5287|SEED_FRUIT_TREE:Papaya:PAPA:5288|SEED_FRUIT_TREE:Palm:PALM:5289|SEED_ALLOTMENT:Potato:POT:5318|SEED_ALLOTMENT:Onion:ONI:5319|SEED_ALLOTMENT:Cabbage:CAB:5324|SEED_ALLOTMENT:Tomato:TOM:5322|SEED_ALLOTMENT:S.Corn:CORN:5320|SEED_ALLOTMENT:S.Berry:STRA:5323|SEED_ALLOTMENT:Melon:MEL:5321|SEED_FLOWER:Marigo:MARI:5096|SEED_FLOWER:Rosemar:ROSE:5097|SEED_FLOWER:Nastur:NAS:5098|SEED_FLOWER:Woad:WOAD:5099|SEED_FLOWER:Limpwurt:LIMP:5100|HOPS_SEED:Barley:BAR:5305|HOPS_SEED:Hammer:HAMM:5307|HOPS_SEED:Asgar:ASG:5308|HOPS_SEED:Jute:JUTE:5306|HOPS_SEED:Yani:YAN:5309|HOPS_SEED:Krand:KRA:5310|HOPS_SEED:Wild.B:WILD:5311|SACK:Empty:EMP:5418|SACK:Cabbage:CAB:5460.5462.5464.5466.5468.5470.5472.5474.5476.5478|SACK:Onion:ONI:5440.5442.5444.5446.5448.5450.5452.5454.5456.5458|SACK:Potato:POT:5420.5422.5424.5426.5428.5430.5432.5434.5436.5438|HERB:Guam:G:249.199|HERB:Marren:M:251.201|HERB:Tarro:TAR:253.203|HERB:Harra:HAR:255.205|HERB:Ranarr:R:257.207|HERB:Toad:TOA:2998.3049|HERB:Irit:I:259.209|HERB:Avan:A:261.211|HERB:Kwuarm:K:263.213|HERB:Snap:S:3000.3051|HERB:Cadan:C:265.215|HERB:Lanta:L:2481.2485|HERB:Dwarf:D:267.217|HERB:Torstol:TOR:269.219|HERB:Ardrig:ARD:1528.1527|HERB:Rogue:ROG:1534.1533|HERB:Sito:SF:1530.1529|HERB:Snake:SW:1526.1525|HERB:Volenc:V:1532.1531|LOGS:Red:RED:7404|LOGS:Green:GRE:7405|LOGS:Blue:BLU:7406|LOGS:White:WHI:10328|LOGS:Purple:PUR:10329|LOGS:Scrapey:SCRAP:8934|LOGS:Log:LOG:1511|LOGS:Achey:ACH:2862|LOGS:Oak:OAK:1521|LOGS:Willow:WIL:1519|LOGS:Teak:TEAK:6333|LOGS:Maple:MAP:1517|LOGS:Mahog:MAH:6332|LOGS:Arctic:ARC:10810|LOGS:Yew:YEW:1515|LOGS:Magic:MAG:1513|LOGS_PYRE:Pyre:P:3438|LOGS_PYRE:Art P:AP:10808|LOGS_PYRE:Oak P:OAKP:3440|LOGS_PYRE:Wil P:WILP:3442|LOGS_PYRE:Teak P:TEAKP:6211|LOGS_PYRE:Map P:MAPP:3444|LOGS_PYRE:Mah P:MAHP:6213|LOGS_PYRE:Yew P:YEWP:3446|LOGS_PYRE:Mag P:MAGP:3448|PLANK:Plank:PLANK:960|PLANK:Oak:OAK:8778|PLANK:Teak:TEAK:8780|PLANK:Mahog:MAH:8782|YARN:Wool:WOOL:1759|CLOTH:Cloth:CLO:8790|SAPLING:Oak:OAK:5370.5358.5364|SAPLING:Willow:WIL:5371.5359.5365|SAPLING:Maple:MAP:5372.5360.5366|SAPLING:Yew:YEW:5373.5361.5367|SAPLING:Magic:MAG:5374.5362.5368|SAPLING:Spirit:SPI:5375.5363.5369|SAPLING:Apple:APP:5496.5480.5488|SAPLING:Banana:BAN:5497.5481.5489|SAPLING:Orange:ORA:5498.5482.5490|SAPLING:Curry:CUR:5499.5483.5491|SAPLING:Pine:PINE:5500.5484.5492|SAPLING:Papaya:PAP:5501.5485.5493|SAPLING:Palm:PALM:5502.5486.5494|SAPLING:Calquat:CALQ:5503.5487.5495|COMPOST:Compost:COM:6032|COMPOST:Sup Com:SCOM:6034|ORE:Copper:COP:436|ORE:Tin:TIN:438|ORE:Iron:IRO:440|ORE:Silver:SIL:442|ORE:Coal:COA:453|ORE:Gold:GOL:444|ORE:Mithril:MIT:447|ORE:Adaman:ADA:449|ORE:Runite:RUN:451|ORE:R.Ess:R.E.:1436|ORE:P.Ess:P.E.:7936|ORE:Blurite:BLU:668|ORE:Element:ELE:2892|ORE:Daeyalt:DAE:9632|ORE:Lunar:LUN:9076|BAR:Bronze:BRO:2349|BAR:Iron:IRO:2351|BAR:Silver:SIL:2355|BAR:Steel:STE:2353|BAR:Gold:GOL:2357|BAR:Mithril:MIT:2359|BAR:Adaman:ADA:2361|BAR:Runite:RUN:2363|BAR:Blurite:BLU:9467|BAR:Elemental:ELE:2893|BAR:Lunar:LUN:9077|GEM:Sapphir:S:1623.1607|GEM:Emerald:E:1621.1605|GEM:Ruby:R:1619.1603|GEM:Diamon:DI:1617.1601|GEM:Opal:OP:1625.1609|GEM:Jade:J:1627.1611|GEM:Topaz:T:1629.1613|GEM:Dragon:DR:1631.1615|GEM:Onyx:ON:6571.6573|GEM:Shadow:SHD:4673|GEM:Blood:BD:4670|GEM:Ice:ID:4671|GEM:Smoke:SMD:4672|POTION:Att:A:2428.121.123.125|POTION:Att:A:11431.11429|POTION:Str:S:113.115.117.119|POTION:Str:S:11441.11443|POTION:Def:D:2432.133.135.137|POTION:Def:D:11459.11457|POTION:Com:C:9739.9741.9743.9745|POTION:Com:C:11447.11445|POTION:Magic:M:3040.3042.3044.3046|POTION:Magic:M:11515.11513|POTION:Range:R:2444.169.171.173|POTION:Range:R:11511.11509|POTION:S.Att:S.A:2436.145.147.149|POTION:S.Att:S.A:11471.11469|POTION:S.Str:S.S:2440.157.159.161|POTION:S.Str:S.S:11487.11485|POTION:S.Def:S.D:2442.163.165.167|POTION:S.Def:S.D:11499.11497|POTION:Restore:Re:2430.127.129.131|POTION:Restore:Re:11451.11449|POTION:GuthBal:G.B.:7660.7662.7664.7666|POTION:S.Rest:S.Re:3024.3026.3028.3030|POTION:S.Rest:S.Re:11495.11493|POTION:Prayer:P:2434.139.141.143|POTION:Prayer:P:11467.11465|POTION:Energy:En:3008.3010.3012.3014|POTION:Energy:En:11455.11453|POTION:S.Energ:S.En:3016.3018.3020.3022|POTION:S.Energ:S.En:11483.11481|POTION:ZammyBr:Za:2450.189.191.193|POTION:ZammyBr:Z:11523.11521|POTION:SaraBr:Sa:6685.6687.6689.6691|POTION:AntiP:AP:2446.175.177.179|POTION:AntiP:AP:11435.11433|POTION:S.AntiP:S.AP:2448.181.183.185|POTION:S.AntiP:S.AP:11475.11473|POTION:Antid+:A+:5943.5945.5947.5949|POTION:Antid+:A+:11503.11501|POTION:Antid++:A++:5952.5954.5956.5958|POTION:Relicym:R.B:4842.4844.4846.4848|POTION:Relicym:R.B:11439.11437|POTION:Sanfew:Sf:10925.10927.10929.10931|POTION:Antif:Af:2452.2454.2456.2458|POTION:Antif:Af:11507.11505|POTION:Ser207:S7:3408.3410.3412.3414|POTION:Ser208:S8:3416.3417.3418.3419|POTION:SaOil:SO:3430.3432.3434.3436|POTION:Olive:Oli:3422.3424.3426.3428|POTION:Compost:COM:6470.6472.6474.6476|POTION:Agility:Ag:3032.3034.3036.3038|POTION:Agility:Ag:11463.11461|POTION:Fishing:Fi:2438.151.153.155|POTION:Fishing:Fi:11479.11477|POTION:Hunter:Hu:9998.10000.10002.10004|POTION:Hunter:Hu:11519.11517|POTION:MagEss:M.E:9021.9022.9023.9024|POTION:MagEss:M.E:11491.11489|POTION:Guam:G:91|POTION:Marren:M:93|POTION:Tarro:TAR:95|POTION:Harra:HAR:97|POTION:Ranarr:R:99|POTION:Toad:TOA:3002|POTION:Irit:I:101|POTION:Avan:A:103|POTION:Kwuarm:K:105|POTION:Snap:S:3004|POTION:Cadan:C:107|POTION:Lanta:L:2483|POTION:Dwarf:D:109|POTION:Torstol:TOR:111|BUTTERFLY_MOTH_JAR:B.War:BW:10014|BUTTERFLY_MOTH_JAR:Ruby.H:RH:10020|BUTTERFLY_MOTH_JAR:Saph.G:SG:10018|BUTTERFLY_MOTH_JAR:Snow.K:SK:10016|IMPLING_JAR:Baby:B:11238|IMPLING_JAR:Young:Y:11240|IMPLING_JAR:Gourmet:G:11242|IMPLING_JAR:Earth:EAR:11244|IMPLING_JAR:Essen:ESS:11246|IMPLING_JAR:Eclect:ECL:11248|IMPLING_JAR:Nature:NAT:11250|IMPLING_JAR:Magpie:M:11252|IMPLING_JAR:Ninja:NIN:11254|IMPLING_JAR:Dragon:D:11256|TABLET:Varro:VAR:8007|TABLET:Lumbr:LUM:8008|TABLET:Fala:FAL:8009|TABLET:Cammy:CAM:8010|TABLET:Ardoug:ARD:8011|TABLET:W.tow:WT:8012|TABLET:House:POH:8013|TABLET:E.Saph:E SO:8016|TABLET:E.Emer:E EJ:8017|TABLET:E.Ruby:E RT:8018|TABLET:E.Diam:E DIA:8019|TABLET:E.Dstn:E DS:8020|TABLET:E.Onyx:E ONX:8021|TABLET:T.grab:T.GRB:8022|TABLET:Peach:BtP:8015|TABLET:Banana:BtB:8014|JEWELLERY:Gold:GOL:1635.1654.1673.1692|JEWELLERY:Sapphir:S:1637.1656.1675.1694|JEWELLERY:Emerald:E:1639.1658.11076.1677.1696|JEWELLERY:Ruby:R:1641.1660.11085.1679.1698|JEWELLERY:Diamon:DI:1643.1662.11092.1681.1700|JEWELLERY:Dragon:DR:1645.1664.11115.1683.1702|JEWELLERY:Onyx:ON:6575.6577.11130.6579.6581|JEWELLERY_ENCHANTED:Recoil:REC:2550|JEWELLERY_ENCHANTED:Games:GAME:3867.3865.3863.3861.3859.3857.3855.3853|JEWELLERY_ENCHANTED:Clay:CLA:11074|JEWELLERY_ENCHANTED:Magic:MAG:1727|JEWELLERY_ENCHANTED:Duel:DU:2566.2564.2562.2560.2558.2556.2554.2552|JEWELLERY_ENCHANTED:Binding:BIND:5521|JEWELLERY_ENCHANTED:Castle:CAS:11083.11081.11079|JEWELLERY_ENCHANTED:Defence:DEF:1729|JEWELLERY_ENCHANTED:Nature:NAT:6040|JEWELLERY_ENCHANTED:Forging:FOR:2568|JEWELLERY_ENCHANTED:Digsite:DIG:11190.11191.11192.11193.11194|JEWELLERY_ENCHANTED:Inocul:INO:11088|JEWELLERY_ENCHANTED:Strengt:STR:1725|JEWELLERY_ENCHANTED:Life:LI:2570|JEWELLERY_ENCHANTED:Phoenix:PHO:11090|JEWELLERY_ENCHANTED:Abyss:ABY:11103.11101.11099.11097.11095|JEWELLERY_ENCHANTED:Power:POW:1731|JEWELLERY_ENCHANTED:Wealth:WE:2572|JEWELLERY_ENCHANTED:Skill:SK:11113.11111.11109.11107.11105|JEWELLERY_ENCHANTED:Combat:COM:11126.11124.11122.11120.11118|JEWELLERY_ENCHANTED:Glory:GLO:1704.1706.1708.1710.1712|JEWELLERY_ENCHANTED:Stone:STO:6583|JEWELLERY_ENCHANTED:Berserk:BER:11128|JEWELLERY_ENCHANTED:Regen:REG:11133|JEWELLERY_ENCHANTED:Fury:FU:6585|WINES:Water:Wat:1937|WINES:Unf.:U:1995|WINES:Wine:W:1993|WINES:Zammy:Z:245|WINES:Bad:Bad:1991|WINES:Half:HF:1989"

	/** RuneLite's ItemIdentification.Type, in its order, with the config key of each. */
	@JvmField
	val TYPES: Array<String> = arrayOf(
		"SEED_HERB", "SEED_BERRY", "SEED_ALLOTMENT", "SEED_SPECIAL", "SEED_TREE", "SEED_FRUIT_TREE", "SEED_FLOWER", "HOPS_SEED",
		"CORAL_FRAG", "SACK", "HERB", "LOGS", "LOGS_PYRE", "LOGS_SAP_TREATED", "PLANK", "REPAIR_KIT", "YARN", "CLOTH", "SAPLING",
		"COMPOST", "ORE", "BAR", "GEM", "POTION", "BUTTERFLY_MOTH_JAR", "IMPLING_JAR", "TABLET", "SCROLL", "JEWELLERY",
		"JEWELLERY_ENCHANTED", "WINES",
	)

	/** RuneLite's ItemIdentificationConfig category settings: key, label, default; in TYPES' order. */
	@JvmField
	val CATEGORY_KEYS: Array<String> = arrayOf(
		"showHerbSeeds", "showBerrySeeds", "showAllotmentSeeds", "showSpecialSeeds", "showTreeSeeds", "showFruitTreeSeeds", "showFlowerSeeds", "showHopSeeds",
		"showCoralFrags", "showSacks", "showHerbs", "showLogs", "showPyreLogs", "showSapTreatedLogs", "showPlanks", "showRepairKits", "showYarn", "showCloth", "showSaplings",
		"showComposts", "showOres", "showBars", "showGems", "showPotions", "showButterflyMothJars", "showImplingJars", "showTablets", "showTeleportScrolls", "showJewellery",
		"showEnchantedJewellery", "showWines",
	)

	@JvmField
	val CATEGORY_LABELS: Array<String> = arrayOf(
		"Seeds (herb)", "Seeds (berry)", "Seeds (allotment)", "Seeds (special)", "Seeds (tree)", "Seeds (fruit tree)", "Seeds (flower)", "Seeds (hops)",
		"Coral Frags", "Sacks", "Herbs", "Logs", "Logs (pyre)", "Logs (sap-treated)", "Planks", "Repair Kits", "Yarn", "Cloth", "Saplings",
		"Composts", "Ores", "Bars", "Gems", "Potions", "Butterfly & Moth jars", "Impling jars", "Tablets", "Teleport scrolls", "Jewellery (unenchanted)",
		"Jewellery (enchanted)", "Wines",
	)

	/** RuneLite's defaults: herb seeds and saplings on, the rest off. */
	@JvmField
	val CATEGORY_DEFAULTS: BooleanArray = BooleanArray(TYPES.size) { TYPES[it] == "SEED_HERB" || TYPES[it] == "SAPLING" }

	/** RuneLite's ItemIdentificationMode. */
	@JvmField
	val MODES: Array<String> = arrayOf("Short", "Medium")

	class Identification(@JvmField val type: Int, @JvmField val medium: String, @JvmField val short: String)

	@JvmField var medium: Boolean = false
	@JvmField var textColor: Int = WHITE
	@JvmField val shown: BooleanArray = CATEGORY_DEFAULTS.copyOf()

	/** For the smoke test: the labels the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	private val items: HashMap<Int, Identification> = HashMap()

	private var context: PluginContext? = null

	/** RuneLite's ItemIdentificationOverlay, over the interfaces. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { draw(event.graphics, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		medium = !medium
		context?.config()?.set("identificationType", MODES[if (medium) 1 else 0])
		context?.config()?.save()
		context?.client()?.consolePrint(if (medium) "Item identification: medium names." else "Item identification: short names.")
	}

	init {
		for (row in ITEMS.split('|')) {
			val f: List<String> = row.split(':')
			val identification: Identification = Identification(TYPES.indexOf(f[0]), f[1], f[2])
			for (id in f[3].split('.')) {
				items[id.toInt()] = identification
			}
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ItemIdentificationConfig, in its order
		s.choice("identificationType", "Identification type", MODES[0], MODES)
		s.colour("textColor", "Color", WHITE, false)
		for (i in TYPES.indices) {
			s.toggle(CATEGORY_KEYS[i], CATEGORY_LABELS[i], CATEGORY_DEFAULTS[i])
		}
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("itemidentification", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		medium = config.get("identificationType", MODES[0]) == MODES[1]
		textColor = config.getColour("textColor", WHITE)
		for (i in TYPES.indices) {
			shown[i] = config.getBoolean(CATEGORY_KEYS[i], CATEGORY_DEFAULTS[i])
		}
	}

	/** RuneLite's findItemIdentification: the item's, a noted item's from its unnoted one (ItemManager.canonicalize). */
	@JvmStatic
	fun identify(client: ClientApi, itemId: Int): Identification? {
		items[itemId]?.let { return it }
		val definition: ItemDefinition = client.item(itemId) ?: return null
		return if (definition.isNoted()) items[definition.linkedId()] else null
	}

	/** The label for an item, or null when its kind is switched off or it has none. */
	@JvmStatic
	fun label(client: ClientApi, itemId: Int): String? {
		val identification: Identification = identify(client, itemId) ?: return null
		if (identification.type < 0 || !shown[identification.type]) {
			return null
		}
		return if (medium) identification.medium else identification.short
	}

	/** RuneLite's renderText: the label at the slot's bottom left. */
	private fun draw(g: Graphics, client: ClientApi) {
		drawnLastFrame = 0
		if (!g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		ItemGrids.forEach(client) { itemId, _, x, y, _, height ->
			val text: String? = label(client, itemId)
			if (text != null) {
				g.text(x - 1, y + height - 1, text, textColor, SHADOW)
				drawnLastFrame++
			}
		}
	}

	private const val WHITE: Int = -0x1
	private const val SHADOW: Int = 0xFF000000.toInt()
}
