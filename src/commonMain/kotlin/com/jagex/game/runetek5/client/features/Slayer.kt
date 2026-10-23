package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Counter
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Slayer, on Void's slayer task. The task and its kills left
 * come from the server's slayer varps (394, the count; 395, the task), as RuneLite now takes them: an
 * infobox shows them (gone after "Infobox expiry" minutes without a kill), the enchanted gem and slayer
 * helmets carry the count, the task's monsters are highlighted (hull, tile or outline: the outline of the
 * npc's box, the client keeping no npc model outlines), and a monster weak to an item shows that item once
 * low enough to finish with it (Void's rules: a gargoyle under 90 life points to a rock hammer, a rockslug
 * under 60 to salt, a zygomite under 80 to fungicide, a lizard under a quarter to an ice cooler).
 *
 * A task's monsters are the npcs of its Void category; the tasks and categories are Void's. Superior foes
 * are OSRS-only, and the !task chat command needs RuneLite's own server. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object Slayer : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("slayer", "Slayer", "1", PluginApi.VERSION, "Shows your slayer task and kills left, highlights its monsters and tells you when to finish one with its item.", "Pixel")

	const val COUNT_VARP: Int = 394
	const val TASK_VARP: Int = 395

	/** Void's slayer tasks (the slayer_target varp's values): value, name and the npcs of its category. */
	const val TASKS: String = "1:Monkeys:132.1456.1457.1458.1459.1460.1461.1465.1466.1467|2:Goblins:444.445.489.745.1822.1823.1824.1825.2069.2070.2071.2072.2073.2074.2075.2076.2275.3264.3265.3266.3267.3663.4261.4262.4263.4264.4265.4266.4267.4268.4269.4270.4271.4272.4273.4274.4275.4276.4407.4408.4409.4410.4411.4412.4479.4480.4481.4482.4483.4484.4485.4486.4487.4488.4489.4491.4492.4499.5800.5801.5874.5875.5879.6261.6263.6265.6279.6280.6281.6282.6283.9683.12353.12354.12355.12356.12357.12358.12359.12360.12361|3:Rats:47.86.88.224.446.950.978.2032.2033.2091.3662.3707.4395.4396.4415.4920.4921.4922.4923.4924.4926.4927.4929.4936.4937.4940.4941.4944.4945.6088.6089.6090.8828.8829.10480.10481.10482.10483.10484.10485.10486.12348.12349.12351|4:Spiders:58.59.60.61.62.63.64.134.977.1004.1221.1473.1478.2034.2035.4400.4401.6376.6377|5:Birds:41.46.48.131.138.139.951.1017.1018.1401.1403.1475.1476.1550.1692.1751.1752.1754.1755.1756.1996.2252.2312.2313.2314.2315.2693.2694.3375.3476.4570.4571.4971.4972.6108.6109.6110.6111.6112.6113.6114.6115.6116.6378.6382.7031.7032.7033.7034.7035.7036.9696|6:Cows:81.397.1691.1767.2310.3309.12362.12363.12364.12365.12366|7:Scorpions:107.108.109.144.271.493.1477.1693.4402.4403.4893|8:Bats:78.412.3711.4345|9:Wolves:95.96.97.141.143.839.1198.1330.1558.1951.1952.1953.1954.1955.1956.4413.4414.6046.6047.6050.6051.6052|10:Zombies:73.74.75.76.77.505.507.1465.1466.1467.1524.1525.1691.1692.1826.1961.1962.1963.1964.2015.2016.2017.2018.2019.2044.2045.2046.2047.2048.2049.2051.2052.2053.2054.2055.2060.2837.2838.2839.2840.2841.2842.2843.2844.2845.2846.2847.2848.3622.4392.4393.4394.5293.5294.5295.5296.5297.5298.5300.5303.5304.5305.5306.5307.5308.5309.5310.5311.5312.5313.5314.5315.5316.5318.5319.5320.5321.5322.5323.5324.5325.5326.5327.5328.5329.5330.5331.5357.5358.5375.5376.5377.5378.5379.5380.5393.5394.5395.5396.5397.5398.5399.5400.5401.5402.5403.5404.5405.5406.5407.5408.5409.5410.5627.5628.5629.5631.5633.5634.5635.5637.5638.5639.5640.5641.5642.5644.5646.5647.5650.5656.5658.5659.5661.5665.5678.5679.5680.5681.5682.5683.5684.5685.5686.5687.5688.5689.5690.5691.5692.5693.5694.5695.5696.5697.5698.5699.5700.5701.5702.5703.5704.5705.5706.5707.5708.5709.5710.5711.5712.5713.5714.5715.5716.5717.5718.5719.5720.5721.5722.5723.5724.5725.5726.5727.5728.5729.5730.5731.5732.5733.5734.5735.5736.5737.5738.5739.5740.5741.5742.5743.5744.5745.5746.5747.6088.6089.6090.6099.6100.6761.6762.6763.8149.8150.8151.8152|11:Skeletons:90.91.92.93.94.459.502.503.504.508.936.937.938.1471.1538.1539.1973.2036.2037.2050.2056.2057.3291.3697.3698.3699.3700.3701.3702.3703.3704.3706.3851.4384.4385.4386.5332.5333.5334.5335.5336.5337.5338.5339.5340.5341.5353.5354.5355.5356.5359.5365.5366.5367.5368.5381.5384.5385.5386.5387.5388.5389.5390.5391.5392.5422.6092.6093.6103.6104.6105.6106.6107.6768.7921|12:Ghosts:79.103.104.491.509.655.1052.1053.1537.1540.1549.1698.4387.4388.5342.5343.5344.5345.5346.5347.5348.5349.5350.5351.5352.5369.5370.5371.6097.7994.8125.9453|13:Bears:105.106.1195.1196.1197.1326.1327|14:Hill giants:117.4291.4292.4689.4690.4691.4692.4693.6078.6079.6080.6081|15:Ice giants:111.3072.4685.4686.4687|16:Fire giants:110.1582.1583.1584.1585.1586|17:Moss giants:112.1587.1588.4688.4706|18:Trolls:1095.1096.1097.1098.1099.1100.1101.1102.1103.1104.1105.1106.1107.1108.1109.1110.1111.1112.1115.1116.1117.1118.1119.1120.1121.1122.1123.1124.1125.1128.1129.1130.1131.1132.1133.1134.1138.1555.1560.1561.1562.1563.1564.1565.1566.1936.1937.1938.1939.1940.1941.1942.4947.5472.5473.5474.5475.5476.5521.5522.5523.5525.5526.5527.5528.8377.8378.8379.8380.8381.8382.8383.8384.8385.8386.8387.8388.8389.8390.8391.8392|19:Ice warriors:125.1913.3073.5452.5453.5454.5455|20:Ogres:114.115.374.852.853.854.855.856.858.859.862.870.873.874.875.2044.2045.2051.2052.2053.2054.2055.2801.3419.3451.7078.7079.7080.7081.7082.8658.9184.9185|21:Hobgoblins:122.123.2685.2686.2687.2688.3583|22:Dogs:98.99.1593.1594.1976.1994.7711|23:Ghouls:1218|24:Green dragons:742.941.4677.4678.4679.4680.5362|25:Blue dragons:52.55.4665.4666.4681.4682.4683.4684|26:Red dragons:53.4669.4670.4671.4672|27:Black dragons:54.3376.4673.4674.4675.4676|28:Lesser demons:82.4694.4695.4696.4697.6101|29:Greater demons:83.4698.4699.4700.4701.8352.8353.8354|30:Black demons:84.4702.4703.4704.4705|31:Hellhounds:49.269.1575.6210|32:Shadow warriors:158|33:Werewolves:6006.6026.6027.6028.6029.6030.6031.6032.6033.6034.6035.6036.6037.6038.6039.6040.6041.6042.6043.6044.6045.6212.6213|34:Vampyres:681.1223.1914.3520.3521.3522.3523.3524.3525.3531.3532.3533.3537.3538.4461.4462.4774.4775.4805.4807.4813.4814.4815.4819.4825.4826.4827.4828.4829.4830.4831.4832.4833.4834.4835.4836.6214.9356|35:Dagannoth:1338.1339.1340.1341.1342.1343.2454.2455.2456.2880.2881.2882.2883.2887.2888.9092.9097.9098.9103.9128.9129.9132.9133|36:Turoth:1623.1626.1627.1628.1629.1630.7800|37:Cave crawlers:1600.1601.1602.1603.7787|38:Banshees:1612.7786|39:Crawling hands:1648.1649.1650.1651.1652.1653.1654.1655.1656.1657.7640.7641|40:Infernal mage:1643.1644.1645.1646.1647|41:Aberrant spectre:1604.1605.1606.1607.7801.7802.7803.7804|42:Abyssal demon:1615.4230|43:Basilisks:1616.1617|44:Cockatrice:1620|45:Kurask:1608.1609.7797.7805|46:Gargoyles:1610|47:Pyrefiends:1633.1634.1635.1636.6216|48:Bloodvelds:1618.1619.6215.7642.7643|49:Dust devils:1624|50:Jellies:1637.1638.1639.1640.1641.1642|51:Rockslugs:1631.1632|52:Nechryael:1613|53:Kalphites:1153.1154.1155.1157|54:Earth warrior:124|55:Otherworldly beings:126|56:Elves:1183.1184.2359.2360.2361.2362|57:Dwarves:118.119.120.206.1795.1796.1797.2130.2131.2132.2133.2134.2135.2136.3219.3220.3221.3268.3269.3270.3271.3272.3273.3274.3275.3276.3277.3278.3279.4257.4258.4259.4260.8772.8773.8774.8776.8777.8779.8781.8782.8783.8784.8813|58:Bronze dragons:1590|59:Iron dragons:1591|60:Steel dragons:1592|61:Wall beasts:2058|62:Cave slimes:1831|63:Cave bugs:1832.5750|64:Shades:1240.1243.1244.1245.1246.1247.1248.1249.3615.8645|65:Crocodiles:1993|66:Dark beasts:2783|67:Mogres:114|68:Lizards:2803.2804.2805.2806.2807.2808|69:Fever spiders:2850|70:Harpie bug swarms:3153|71:Sea snakes:3939.3940|72:Skeletal wyverns:3068.3069.3070.3071|73:Killerwatts:3202|74:Mutated zygomites:3344.3345|75:Icefiends:3406.7714.7715.7716.9066|76:Minotaurs:4404.4406|77:Flesh crawlers:4389.4390.4391|78:Catablepons:4397.4398.4399|79:Ankou:4381.4382.4383|80:Cave horrors:4355|81:Jungle horrors:4352|82:Gorak:4418.6218|83:Suqahs:4527.4528.4529.4530.4531.4532.4533|84:Brine rats:3707|85:Scabarites:1969.2001.4500.5250.5251.5252.5253.5254.6770.6771.6773.6774.6777.6778.6780.7500|86:Terror dogs:5417.5418|87:Molanisks:5751|88:Waterfiends:5361|89:Spiritual warriors:|90:Spiritual rangers:|91:Spiritual mages:|92:Warped tortoises:|93:Warped creatures:6285.6286.6287.6288.6289.6291.6292.6294.6295.6296|94:Mithril dragons:5363|95:Aquanites:9172|101:Tzhaar:2591.2592.2594.2595.2596.2597.2598.2599.2600.2601.2602.2603.2604.2605.2606.2607.2608.2609.2610.2611.2612.2613.2614.2615.2616|102:Volcanic creatures:|103:Jungle strykewyrm:9466|104:Desert strykewyrm:9464|105:Ice strykewyrm:9462|106:Living rock creatures:8832.8833.8834|108:Cyclopes:116"

	/** Void's monsters with a weakness: npc, its life points, the life points it can be finished at, the item. */
	const val WEAKNESS: String = "1610,1050,90,4162;1631,270,60,4161;1632,270,60,4161;2803,400,100,6696;2807,150,37,6696;2808,150,37,6696;3344,650,80,7421;3345,750,80,7421;3346,650,80,7421;3347,750,80,7421;6369,200,50,6696"

	/** The enchanted gem: the infobox's picture, and an item that carries the count. */
	const val ENCHANTED_GEM: Int = 4155

	/** RuneLite's SlayerOverlay items, the ones 634 has: the enchanted gem and the slayer helmets. */
	@JvmField
	val SLAYER_ITEMS: IntArray = intArrayOf(ENCHANTED_GEM, 13263, 14636, 14637, 15492, 15496, 15497)

	class Task(@JvmField val value: Int, @JvmField val name: String, @JvmField val npcs: IntArray)

	class Weakness(@JvmField val npc: Int, @JvmField val lifePoints: Int, @JvmField val threshold: Int, @JvmField val itemId: Int)

	@JvmField var infobox: Boolean = true
	@JvmField var itemoverlay: Boolean = true
	@JvmField var statTimeout: Int = 5
	@JvmField var highlightHull: Boolean = false
	@JvmField var highlightTile: Boolean = false
	@JvmField var highlightOutline: Boolean = false
	@JvmField var targetColor: Int = RED
	@JvmField var weaknessPrompt: Boolean = true

	/** The task now (varp 395), null for none, and its kills left (varp 394). */
	@JvmField var task: Task? = null
	@JvmField var amount: Int = 0

	/** When the task or its count last changed, from [clock]: the infobox goes after statTimeout. */
	@JvmField var lastActivity: Long = 0L

	/** For the smoke test: the monsters the last frame highlighted, and the counts drawn on items. */
	@JvmField var targetsDrawn: Int = 0
	@JvmField var countsDrawn: Int = 0

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private val tasks: HashMap<Int, Task> = HashMap()
	private val weaknesses: HashMap<Int, Weakness> = HashMap()

	/** RuneLite's TaskCounter: the kills left, the task in its tooltip. */
	class TaskCounter(count: Int) : Counter("Slayer task", count) {
		init {
			itemId = ENCHANTED_GEM
		}

		override fun tooltip(): String = (task?.name ?: "") + "</br>" + count + " left"

		override fun render(): Boolean = infobox && amount > 0 && clock() - lastActivity < statTimeout * MINUTE_MILLIS
	}

	@JvmField var counter: TaskCounter? = null

	private var context: PluginContext? = null

	private val xs: IntArray = IntArray(16)
	private val ys: IntArray = IntArray(16)
	private val tileXs: IntArray = IntArray(4)
	private val tileYs: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)

	/** RuneLite's target highlights (NpcOverlayService) and TargetWeaknessOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> context?.client()?.let { drawTargets(g, view, it) } }

	/** RuneLite's SlayerOverlay: the count on the slayer items, over the interfaces. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { drawItemCounts(event.graphics, it) } }

	private val varpListener: EventListener<VarpChanged> = EventListener { event ->
		if (event.id == COUNT_VARP || event.id == TASK_VARP) {
			context?.client()?.let { update(it) }
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		infobox = !infobox
		context?.config()?.setBoolean("infobox", infobox)
		context?.config()?.save()
		context?.client()?.consolePrint(if (infobox) "Slayer infobox on." else "Slayer infobox off.")
	}

	init {
		for (row in TASKS.split('|')) {
			val f: List<String> = row.split(':')
			val npcs: IntArray = if (f[2].isEmpty()) IntArray(0) else f[2].split('.').map { it.toInt() }.toIntArray()
			tasks[f[0].toInt()] = Task(f[0].toInt(), f[1], npcs)
		}
		for (row in WEAKNESS.split(';')) {
			val f: List<String> = row.split(',')
			weaknesses[f[0].toInt()] = Weakness(f[0].toInt(), f[1].toInt(), f[2].toInt(), f[3].toInt())
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's SlayerConfig, in its order, but the superior foe notification and the !task command
		s.toggle("infobox", "Task infobox", true)
		s.toggle("itemoverlay", "Count on items", true)
		s.number("statTimeout", "Infobox expiry (minutes)", 5, 1, 60, 1)
		s.toggle("highlightHull", "Highlight hull", false)
		s.toggle("highlightTile", "Highlight tile", false)
		s.toggle("highlightOutline", "Highlight outline", false)
		s.colour("targetColor", "Target color", RED, false)
		s.toggle("weaknessPrompt", "Show monster weakness", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("slayer", toggle)
		if (context.client().gameState() === GameState.LOGGED_IN) {
			update(context.client())
		}
	}

	override fun stop() {
		counter?.let { context?.removeInfoBox(it) }
		counter = null
		task = null
		amount = 0
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		infobox = config.getBoolean("infobox", true)
		itemoverlay = config.getBoolean("itemoverlay", true)
		statTimeout = config.getInt("statTimeout", 5)
		highlightHull = config.getBoolean("highlightHull", false)
		highlightTile = config.getBoolean("highlightTile", false)
		highlightOutline = config.getBoolean("highlightOutline", false)
		targetColor = config.getColour("targetColor", RED)
		weaknessPrompt = config.getBoolean("weaknessPrompt", true)
	}

	@JvmStatic
	fun task(value: Int): Task? = tasks[value]

	@JvmStatic
	fun weakness(npc: Int): Weakness? = weaknesses[npc]

	/** RuneLite's onVarbitChanged(SLAYER_COUNT, SLAYER_TARGET): the task and count from the server, the infobox kept in step. */
	@JvmStatic
	fun update(client: ClientApi) {
		val count: Int = client.varp(COUNT_VARP)
		val now: Task? = if (count > 0) tasks[client.varp(TASK_VARP)] else null
		if (now !== task || count != amount) {
			lastActivity = clock()
		}
		task = now
		amount = if (now == null) 0 else count
		val box: TaskCounter? = counter
		if (now == null) {
			box?.let { context?.removeInfoBox(it) }
			counter = null
		} else if (box == null) {
			val made: TaskCounter = TaskCounter(amount)
			counter = made
			context?.addInfoBox(made)
		} else {
			box.count = amount
		}
	}

	/** Whether [npc] is one of the task's monsters. */
	@JvmStatic
	fun isTarget(npc: Npc): Boolean {
		val current: Task = task ?: return false
		return npc.typeId() in current.npcs
	}

	/** RuneLite's TargetWeaknessOverlay: whether a weak monster's health ([ratio] of [scale]) is low enough to finish it. */
	@JvmStatic
	fun finishable(weakness: Weakness, ratio: Int, scale: Int): Boolean {
		if (ratio < 0 || scale <= 0) {
			return false
		}
		val lifePoints: Int = weakness.lifePoints * ratio / scale
		return lifePoints < weakness.threshold
	}

	private fun drawTargets(g: Graphics, view: Viewport, client: ClientApi) {
		targetsDrawn = 0
		if (task == null || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val fill: Int = (FILL_ALPHA shl 24) or (targetColor and 0xFFFFFF)
		for (i in 0 until client.npcCount()) {
			val npc: Npc? = client.npc(i)
			if (npc != null && !npc.isDying() && isTarget(npc) && drawTarget(g, view, client, npc, fill)) {
				targetsDrawn++
			}
		}
	}

	/** One of the task's monsters highlighted; false when nothing of it is on the screen. */
	private fun drawTarget(g: Graphics, view: Viewport, client: ClientApi, npc: Npc, fill: Int): Boolean {
		var drawn: Boolean = false
		if (highlightHull || highlightOutline) {
			val count: Int = client.actorHull(npc, xs, ys)
			if (count >= 3) {
				for (c in 0 until count) {
					xs[c] += view.x
					ys[c] += view.y
				}
				if (highlightHull) {
					g.fillPolygon(xs, ys, count, fill)
				}
				g.polygon(xs, ys, count, targetColor)
				drawn = true
			}
		}
		if (highlightTile && TileOutline.draw(g, view, client, npc.tileX(), npc.tileY(), npc.level(), npc.sizeInTiles(), targetColor, fill, 2, tileXs, tileYs)) {
			drawn = true
		}
		val weakness: Weakness? = weaknesses[npc.typeId()]
		if (weakness != null && weaknessPrompt && finishable(weakness, npc.healthRatio(), npc.healthScale()) && client.projectActor(npc, npc.overheadHeight(), point)) {
			g.itemIcon(weakness.itemId, 1, view.x + point[0] - ICON_WIDTH / 2, view.y + point[1] - ICON_HEIGHT)
			drawn = true
		}
		return drawn
	}

	/** RuneLite's SlayerOverlay: the kills left on each slayer item shown. */
	private fun drawItemCounts(g: Graphics, client: ClientApi) {
		countsDrawn = 0
		if (!itemoverlay || task == null || amount <= 0 || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val text: String = amount.toString()
		ItemGrids.forEach(client) { itemId, _, x, y, _, height ->
			if (itemId in SLAYER_ITEMS) {
				g.text(x, y + height, text, WHITE, SHADOW)
				countsDrawn++
			}
		}
	}

	private const val MINUTE_MILLIS: Long = 60_000L
	private const val FILL_ALPHA: Int = 50
	private const val ICON_WIDTH: Int = 36
	private const val ICON_HEIGHT: Int = 32

	private const val WHITE: Int = -0x1
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val SHADOW: Int = 0xFF000000.toInt()
}
