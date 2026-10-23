package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: one thing the Discord plugin can say the player is doing, after
 * RuneLite's DiscordGameEventType. A skill being trained gives the details line and the small image;
 * a place gives the state line; "In Game" and "In Menu" are what is left when nothing else is known.
 * [priority] orders the stack in [DiscordState]: the highest wins each line. [shouldClear] drops the
 * other clearable events when this one fires, [shouldTimeout] lets it expire after the activity
 * timeout, [shouldRestart] restarts its clock each time it fires, and [shouldBeCleared] is whether
 * another event's clear, or the timeout, may remove it.
 */
class DiscordGameEvent(
	@JvmField val name: String,
	@JvmField val state: String?,
	@JvmField val details: String?,
	@JvmField val imageKey: String?,
	@JvmField val priority: Int,
	@JvmField val shouldClear: Boolean,
	@JvmField val shouldTimeout: Boolean,
	@JvmField val shouldRestart: Boolean,
	@JvmField val shouldBeCleared: Boolean,
	@JvmField val areaType: DiscordAreaType?,
	@JvmField val regionIds: IntArray?,
) {
	override fun toString(): String {
		return name
	}
}

/**
 * The events: the two states, one per skill, and the places of the 634 world by map region
 * (`(x shr 6 shl 8) + (y shr 6)` of the player's tile, [com.jagex.game.runetek5.plugin.api.ClientApi.regionId]).
 * The places are RuneLite's table kept to the regions the 634 cache has a map for, under their 2011
 * names, with the 2011 content RuneLite's game never had (Daemonheim, Soul Wars, Mobilising Armies,
 * the Thieves' Guild, the Chaos Tunnels, the Living Rock Caverns, the Wilderness, the bosses of the
 * day); every region id names at most one place.
 */
object DiscordGameEvents {

	private const val STATE_PRIORITY: Int = -3

	private const val AREA_PRIORITY: Int = -2

	@JvmField
	val IN_MENU: DiscordGameEvent = DiscordGameEvent("IN_MENU", "In Menu", null, null, STATE_PRIORITY, true, true, true, false, null, null)

	@JvmField
	val IN_GAME: DiscordGameEvent = DiscordGameEvent("IN_GAME", "In Game", null, null, STATE_PRIORITY, true, false, false, false, null, null)

	/** The training event of each skill, by skill number. */
	@JvmField
	val TRAINING: Array<DiscordGameEvent> = Array(Skill.COUNT) { skill -> training(skill) }

	/** Every place, by category: one function each, since a single initialiser would pass the JVM's 8000-byte JIT limit (checkClasses). */
	@JvmField
	val AREAS: Array<DiscordGameEvent> = bosses() + cities() + dungeons() + minigames() + regions()

	private fun bosses(): Array<DiscordGameEvent> = arrayOf(
		area("Chaos Elemental", DiscordAreaType.BOSSES, 12861),
		area("Commander Zilyana", DiscordAreaType.BOSSES, 11602),
		area("Corporeal Beast", DiscordAreaType.BOSSES, 11842, 11844),
		area("Dagannoth Kings", DiscordAreaType.BOSSES, 11588, 11589),
		area("General Graardor", DiscordAreaType.BOSSES, 11347),
		area("Giant Mole", DiscordAreaType.BOSSES, 6992, 6993),
		area("K'ril Tsutsaroth", DiscordAreaType.BOSSES, 11603),
		area("Kalphite Queen", DiscordAreaType.BOSSES, 13972),
		area("King Black Dragon", DiscordAreaType.BOSSES, 9033),
		area("Kree'arra", DiscordAreaType.BOSSES, 11346),
		area("Nex", DiscordAreaType.BOSSES, 11601),
		area("Tormented Demons", DiscordAreaType.BOSSES, 10831),
	)

	private fun cities(): Array<DiscordGameEvent> = arrayOf(
		area("Al Kharid", DiscordAreaType.CITIES, 13105, 13106),
		area("Ardougne", DiscordAreaType.CITIES, 9779, 9780, 10035, 10036, 10291, 10292, 10547, 10548),
		area("Bandit Camp", DiscordAreaType.CITIES, 12590),
		area("Barbarian Outpost", DiscordAreaType.CITIES, 10039),
		area("Barbarian Village", DiscordAreaType.CITIES, 12341),
		area("Bedabin Camp", DiscordAreaType.CITIES, 12591),
		area("Brimhaven", DiscordAreaType.CITIES, 11057, 11058),
		area("Burgh de Rott", DiscordAreaType.CITIES, 13873, 13874, 14129, 14130),
		area("Burthorpe", DiscordAreaType.CITIES, 11319, 11575),
		area("Canifis", DiscordAreaType.CITIES, 13878),
		area("Catherby", DiscordAreaType.CITIES, 11061, 11317, 11318),
		area("Darkmeyer", DiscordAreaType.CITIES, 14388),
		area("Dorgesh-Kaan", DiscordAreaType.CITIES, 10834, 10835),
		area("Draynor", DiscordAreaType.CITIES, 12338, 12339),
		area("Edgeville", DiscordAreaType.CITIES, 12342),
		area("Entrana", DiscordAreaType.CITIES, 11060, 11316),
		area("Etceteria", DiscordAreaType.CITIES, 10300),
		area("Falador", DiscordAreaType.CITIES, 11572, 11827, 11828, 12084),
		area("Gu'Tanoth", DiscordAreaType.CITIES, 10031),
		area("Jatizso", DiscordAreaType.CITIES, 9531),
		area("Keldagrim", DiscordAreaType.CITIES, 11422, 11423, 11678, 11679),
		area("Lletya", DiscordAreaType.CITIES, 9265),
		area("Lumbridge", DiscordAreaType.CITIES, 12850),
		area("Lunar Isle", DiscordAreaType.CITIES, 8252, 8253, 8508, 8509),
		area("Meiyerditch", DiscordAreaType.CITIES, 14132, 14385, 14386, 14387),
		area("Miscellania", DiscordAreaType.CITIES, 10044),
		area("Mort'ton", DiscordAreaType.CITIES, 13875),
		area("Mos Le'Harmless", DiscordAreaType.CITIES, 14637, 14638, 14639, 14894, 14895, 15151, 15406, 15407),
		area("Mountain Camp", DiscordAreaType.CITIES, 11065),
		area("Nardah", DiscordAreaType.CITIES, 13613),
		area("Neitiznot", DiscordAreaType.CITIES, 9275),
		area("Oo'glog", DiscordAreaType.CITIES, 10028, 10284),
		area("Piscatoris", DiscordAreaType.CITIES, 9273),
		area("Pollnivneach", DiscordAreaType.CITIES, 13358),
		area("Port Khazard", DiscordAreaType.CITIES, 10545),
		area("Port Phasmatys", DiscordAreaType.CITIES, 14646),
		area("Port Sarim", DiscordAreaType.CITIES, 12081, 12082),
		area("Rellekka", DiscordAreaType.CITIES, 10297, 10553),
		area("Rimmington", DiscordAreaType.CITIES, 11570, 11826),
		area("Seers' Village", DiscordAreaType.CITIES, 10806),
		area("Shilo Village", DiscordAreaType.CITIES, 11310),
		area("Sophanem", DiscordAreaType.CITIES, 13099),
		area("Tai Bwo Wannai", DiscordAreaType.CITIES, 11055, 11056),
		area("Taverley", DiscordAreaType.CITIES, 11573, 11574),
		area("Tree Gnome Stronghold", DiscordAreaType.CITIES, 9525, 9526, 9781, 9782),
		area("Tree Gnome Village", DiscordAreaType.CITIES, 10033),
		area("Troll Stronghold", DiscordAreaType.CITIES, 11321, 11421, 11676),
		area("TzHaar City", DiscordAreaType.CITIES, 9808, 10064),
		area("Uzer", DiscordAreaType.CITIES, 13872),
		area("Uzer Oasis", DiscordAreaType.CITIES, 13871),
		area("Varrock", DiscordAreaType.CITIES, 12596, 12597, 12852, 12853, 12854, 13108, 13109, 13110),
		area("Void Knights' Outpost", DiscordAreaType.CITIES, 10537, 10793),
		area("Witchaven", DiscordAreaType.CITIES, 10803),
		area("Yanille", DiscordAreaType.CITIES, 10032, 10288),
		area("Zanaris", DiscordAreaType.CITIES, 9285, 9540, 9541, 9797),
	)

	private fun dungeons(): Array<DiscordGameEvent> = arrayOf(
		area("Abandoned Mine", DiscordAreaType.DUNGEONS, 10821, 10822, 10823, 11077, 11078, 11079, 13618, 13718),
		area("Ah Za Rhoon", DiscordAreaType.DUNGEONS, 11666),
		area("Ancient Cavern", DiscordAreaType.DUNGEONS, 6483, 6995),
		area("Ape Atoll Banana Plantation", DiscordAreaType.DUNGEONS, 10895),
		area("Ape Atoll Dungeon", DiscordAreaType.DUNGEONS, 10894, 11150),
		area("Ardougne Sewers", DiscordAreaType.DUNGEONS, 10134, 10136, 10391, 10647),
		area("Asgarnian Ice Caves", DiscordAreaType.DUNGEONS, 11925, 12181),
		area("Brimhaven Dungeon", DiscordAreaType.DUNGEONS, 10643, 10644, 10645, 10899, 10900, 10901),
		area("Brine Rat Cavern", DiscordAreaType.DUNGEONS, 10910),
		area("Champions' Challenge", DiscordAreaType.DUNGEONS, 12696),
		area("Chaos Druid Tower", DiscordAreaType.DUNGEONS, 10392),
		area("Chaos Tunnels", DiscordAreaType.DUNGEONS, 12629, 12630, 12885, 12886, 13141, 13142),
		area("Chasm of Tears", DiscordAreaType.DUNGEONS, 12948),
		area("Chinchompa Hunting Ground", DiscordAreaType.DUNGEONS, 10129),
		area("Clock Tower Basement", DiscordAreaType.DUNGEONS, 10390),
		area("Crandor Dungeon", DiscordAreaType.DUNGEONS, 11414),
		area("Digsite Dungeon", DiscordAreaType.DUNGEONS, 13464, 13465),
		area("Dorgesh-Kaan South Dungeon", DiscordAreaType.DUNGEONS, 10833),
		area("Dorgeshuun Mines", DiscordAreaType.DUNGEONS, 12950, 13206),
		area("Draynor Sewers", DiscordAreaType.DUNGEONS, 12438, 12439),
		area("Dungeon of Tolna", DiscordAreaType.DUNGEONS, 13209),
		area("Dwarven Mines", DiscordAreaType.DUNGEONS, 12183, 12184, 12185),
		area("Eagles' Peak Dungeon", DiscordAreaType.DUNGEONS, 8013),
		area("Ectofuntus", DiscordAreaType.DUNGEONS, 14746),
		area("Edgeville Dungeon", DiscordAreaType.DUNGEONS, 12441, 12442, 12443, 12698),
		area("Elemental Workshop", DiscordAreaType.DUNGEONS, 7760, 10906),
		area("Evil Chicken's Lair", DiscordAreaType.DUNGEONS, 9796),
		area("Experiment Cave", DiscordAreaType.DUNGEONS, 13979, 14235),
		area("Fremennik Slayer Dungeon", DiscordAreaType.DUNGEONS, 10907, 10908, 11164),
		area("Glarial's Tomb", DiscordAreaType.DUNGEONS, 10137),
		area("Goblin Cave", DiscordAreaType.DUNGEONS, 10393),
		area("Grand Tree Tunnels", DiscordAreaType.DUNGEONS, 9882),
		area("H.A.M. Hideout", DiscordAreaType.DUNGEONS, 12694),
		area("H.A.M. Store room", DiscordAreaType.DUNGEONS, 10321),
		area("Heroes' Guild Mine", DiscordAreaType.DUNGEONS, 11674),
		area("Jatizso Mines", DiscordAreaType.DUNGEONS, 9631),
		area("Jiggig Burial Tomb", DiscordAreaType.DUNGEONS, 9874, 9875),
		area("Jogre Dungeon", DiscordAreaType.DUNGEONS, 11412),
		area("KGP Headquarters", DiscordAreaType.DUNGEONS, 10658),
		area("Karamja Dungeon", DiscordAreaType.DUNGEONS, 11413),
		area("Legends' Guild Dungeon", DiscordAreaType.DUNGEONS, 10904),
		area("Living Rock Caverns", DiscordAreaType.DUNGEONS, 14671, 14672),
		area("Lumbridge Swamp Caves", DiscordAreaType.DUNGEONS, 12693, 12949),
		area("Lunar Isle Mine", DiscordAreaType.DUNGEONS, 9377),
		area("Meiyerditch Laboratories", DiscordAreaType.DUNGEONS, 14232, 14233, 14487, 14488),
		area("Meiyerditch Mine", DiscordAreaType.DUNGEONS, 9544),
		area("Miscellania Dungeon", DiscordAreaType.DUNGEONS, 10144, 10400),
		area("Mogre Camp", DiscordAreaType.DUNGEONS, 11924),
		area("Mos Le'Harmless Caves", DiscordAreaType.DUNGEONS, 14994, 14995, 15251),
		area("Mourner Tunnels", DiscordAreaType.DUNGEONS, 7752, 8008),
		area("Mouse Hole", DiscordAreaType.DUNGEONS, 9046),
		area("Myreque Hideout", DiscordAreaType.DUNGEONS, 13721, 13974, 13977, 13978),
		area("Observatory Dungeon", DiscordAreaType.DUNGEONS, 9362),
		area("Ogre Enclave", DiscordAreaType.DUNGEONS, 10387),
		area("Ourania Cave", DiscordAreaType.DUNGEONS, 12119),
		area("Rashiliyta's Tomb", DiscordAreaType.DUNGEONS, 11668),
		area("Saradomin Shrine (Paterdomus)", DiscordAreaType.DUNGEONS, 13722),
		area("Shade Catacombs", DiscordAreaType.DUNGEONS, 13975),
		area("Shadow Dungeon", DiscordAreaType.DUNGEONS, 10575),
		area("Smoke Dungeon", DiscordAreaType.DUNGEONS, 12946, 13202),
		area("Sophanem Dungeon", DiscordAreaType.DUNGEONS, 13200),
		area("Stronghold of Security", DiscordAreaType.DUNGEONS, 7505, 8017, 8530, 9297),
		area("Tarn's Lair", DiscordAreaType.DUNGEONS, 12615, 12616),
		area("Taverley Dungeon", DiscordAreaType.DUNGEONS, 11416, 11417, 11671, 11672, 11673, 11928, 11929),
		area("Temple of Ikov", DiscordAreaType.DUNGEONS, 10649, 10650, 10905),
		area("Temple of Light", DiscordAreaType.DUNGEONS, 7496),
		area("Temple of Marimbo", DiscordAreaType.DUNGEONS, 11151),
		area("Thieves' Guild", DiscordAreaType.DUNGEONS, 18522, 18524, 19034, 19036),
		area("Tomb of Bervirius", DiscordAreaType.DUNGEONS, 11154),
		area("Tower of Life Basement", DiscordAreaType.DUNGEONS, 12100),
		area("Tunnel of Chaos", DiscordAreaType.DUNGEONS, 12625),
		area("Underground Pass", DiscordAreaType.DUNGEONS, 9369, 9370),
		area("Varrock Sewers", DiscordAreaType.DUNGEONS, 12954, 13210),
		area("Viyeldi Caves", DiscordAreaType.DUNGEONS, 9545, 11153),
		area("Warriors' Guild Basement", DiscordAreaType.DUNGEONS, 11675),
		area("Water Ravine", DiscordAreaType.DUNGEONS, 13461),
		area("Waterbirth Dungeon", DiscordAreaType.DUNGEONS, 7492, 7748, 9886, 10142),
		area("Waterfall Dungeon", DiscordAreaType.DUNGEONS, 10394),
		area("Werewolf Agility Course", DiscordAreaType.DUNGEONS, 14234),
		area("West Ardougne Basement", DiscordAreaType.DUNGEONS, 10135),
		area("White Wolf Mountain Caves", DiscordAreaType.DUNGEONS, 11418, 11419),
		area("Witchhaven Shrine Dungeon", DiscordAreaType.DUNGEONS, 10903),
		area("Wizards' Tower Basement", DiscordAreaType.DUNGEONS, 12437),
		area("Yanille Agility Dungeon", DiscordAreaType.DUNGEONS, 10388),
	)

	private fun minigames(): Array<DiscordGameEvent> = arrayOf(
		area("Ardougne Rat Pits", DiscordAreaType.MINIGAMES, 10646),
		area("Barbarian Assault", DiscordAreaType.MINIGAMES, 7508, 7509, 10322),
		area("Barrows", DiscordAreaType.MINIGAMES, 14131, 14231),
		area("Blast Furnace", DiscordAreaType.MINIGAMES, 7757),
		area("Brimhaven Agility Arena", DiscordAreaType.MINIGAMES, 11157),
		area("Burthorpe Games Room", DiscordAreaType.MINIGAMES, 8781),
		area("Castle Wars", DiscordAreaType.MINIGAMES, 9520, 9620),
		area("Clan Wars", DiscordAreaType.MINIGAMES, 12621, 12623, 13131, 13133, 13135, 13386, 13387, 13641, 13642, 13643, 13645, 13647, 13899),
		area("Duel Arena", DiscordAreaType.MINIGAMES, 13362, 13363),
		area("Fishing Trawler", DiscordAreaType.MINIGAMES, 7499),
		area("Keldagrim Rat Pits", DiscordAreaType.MINIGAMES, 7753),
		area("Mage Training Arena", DiscordAreaType.MINIGAMES, 13462, 13463),
		area("Mobilising Armies", DiscordAreaType.MINIGAMES, 9516, 9772),
		area("Pest Control", DiscordAreaType.MINIGAMES, 10536),
		area("Port Sarim Rat Pits", DiscordAreaType.MINIGAMES, 11926),
		area("Pyramid Plunder", DiscordAreaType.MINIGAMES, 7749),
		area("Rogues' Den", DiscordAreaType.MINIGAMES, 11854, 11855, 12109, 12110, 12111),
		area("Sorceress's Garden", DiscordAreaType.MINIGAMES, 11605),
		area("Soul Wars", DiscordAreaType.MINIGAMES, 7217, 7218, 7473, 7474, 7729, 7730),
		area("Temple Trekking", DiscordAreaType.MINIGAMES, 8014, 8256, 8270, 8782, 9038, 9294, 9550, 9806),
		area("Trouble Brewing", DiscordAreaType.MINIGAMES, 15150),
		area("Tzhaar Fight Caves", DiscordAreaType.MINIGAMES, 9551),
		area("Tzhaar Fight Pits", DiscordAreaType.MINIGAMES, 9552),
		area("Varrock Rat Pits", DiscordAreaType.MINIGAMES, 11599),
	)

	private fun regions(): Array<DiscordGameEvent> = arrayOf(
		area("Abyssal Area", DiscordAreaType.REGIONS, 12108),
		area("Agility Pyramid", DiscordAreaType.REGIONS, 12105, 13356),
		area("Air Altar", DiscordAreaType.REGIONS, 11339),
		area("Al Kharid Mine", DiscordAreaType.REGIONS, 13107),
		area("Ape Atoll", DiscordAreaType.REGIONS, 10794, 10795, 11050),
		area("Arandar", DiscordAreaType.REGIONS, 9266, 9267, 9523),
		area("Asgarnia", DiscordAreaType.REGIONS, 11825, 11829, 11830, 12085, 12086),
		area("Battlefield", DiscordAreaType.REGIONS, 10034),
		area("Body Altar", DiscordAreaType.REGIONS, 10059),
		area("Chaos Altar", DiscordAreaType.REGIONS, 9035),
		area("Cosmic Altar", DiscordAreaType.REGIONS, 8523),
		area("Cosmic Entity's Plane", DiscordAreaType.REGIONS, 8267),
		area("Crafting Guild", DiscordAreaType.REGIONS, 11571),
		area("Crandor", DiscordAreaType.REGIONS, 11314, 11315),
		area("Crash Island", DiscordAreaType.REGIONS, 11562),
		area("Daemonheim", DiscordAreaType.REGIONS, 13624, 13625, 13626, 13627, 13881, 13882, 13883),
		area("Death Altar", DiscordAreaType.REGIONS, 8779),
		area("Death Plateau", DiscordAreaType.REGIONS, 11320),
		area("Desert Plateau", DiscordAreaType.REGIONS, 13361, 13617),
		area("Digsite", DiscordAreaType.REGIONS, 13365),
		area("Dragontooth Island", DiscordAreaType.REGIONS, 15159),
		area("Draynor Manor", DiscordAreaType.REGIONS, 12340),
		area("Drill Sergeant's Training Camp", DiscordAreaType.REGIONS, 12619),
		area("Eagles' Peak", DiscordAreaType.REGIONS, 9270),
		area("Earth Altar", DiscordAreaType.REGIONS, 10571),
		area("Enchanted Valley", DiscordAreaType.REGIONS, 12102),
		area("Evil Twin Crane Room", DiscordAreaType.REGIONS, 7504),
		area("Exam Centre", DiscordAreaType.REGIONS, 13364),
		area("Falador Farm", DiscordAreaType.REGIONS, 12083),
		area("Feldip Hills", DiscordAreaType.REGIONS, 9773, 9774, 10029, 10030, 10285, 10286, 10287, 10542, 10543),
		area("Fenkenstrain's Castle", DiscordAreaType.REGIONS, 14135),
		area("Fight Arena", DiscordAreaType.REGIONS, 10289),
		area("Fire Altar", DiscordAreaType.REGIONS, 10315),
		area("Fisher Realm", DiscordAreaType.REGIONS, 10569),
		area("Fishing Guild", DiscordAreaType.REGIONS, 10293),
		area("Fishing Platform", DiscordAreaType.REGIONS, 11059),
		area("Freaky Forester's Clearing", DiscordAreaType.REGIONS, 10314),
		area("Fremennik Isles", DiscordAreaType.REGIONS, 9276, 9532),
		area("Fremennik Province", DiscordAreaType.REGIONS, 10296, 10552, 10808, 10809, 10810, 10811, 11064),
		area("Frogland", DiscordAreaType.REGIONS, 9802),
		area("Ghorrock Dungeon", DiscordAreaType.REGIONS, 11681),
		area("God Wars Dungeon", DiscordAreaType.REGIONS, 11578),
		area("Gorak's Plane", DiscordAreaType.REGIONS, 12115),
		area("Grand Exchange", DiscordAreaType.REGIONS, 12598),
		area("Harmony Island", DiscordAreaType.REGIONS, 15148),
		area("Hazelmere's Island", DiscordAreaType.REGIONS, 10544),
		area("Ice Path", DiscordAreaType.REGIONS, 11322, 11323),
		area("Iceberg", DiscordAreaType.REGIONS, 10558, 10559),
		area("Isafdar", DiscordAreaType.REGIONS, 8497, 8753, 8754, 9009, 9010),
		area("Jiggig", DiscordAreaType.REGIONS, 9775),
		area("Kandarin", DiscordAreaType.REGIONS, 9263, 9264, 9268, 9269, 9519, 9524, 9527, 9776, 9783, 10037, 10290, 10294, 10546, 10551, 10805, 11062),
		area("Karamja", DiscordAreaType.REGIONS, 10801, 10802, 11054, 11311, 11312, 11313, 11566, 11567, 11568, 11569, 11822),
		area("Kharazi Jungle", DiscordAreaType.REGIONS, 11053, 11309, 11565, 11821),
		area("Kharidian Desert", DiscordAreaType.REGIONS, 12844, 12845, 12846, 12847, 12848, 13100, 13101, 13102, 13103, 13104, 13354, 13355, 13357, 13359, 13360, 13610, 13611, 13612, 13614, 13615, 13616),
		area("Killerwatt Plane", DiscordAreaType.REGIONS, 10577),
		area("Law Altar", DiscordAreaType.REGIONS, 9803),
		area("Legends' Guild", DiscordAreaType.REGIONS, 10804),
		area("Lighthouse", DiscordAreaType.REGIONS, 10040),
		area("Lumbridge Swamp", DiscordAreaType.REGIONS, 12593, 12849),
		// Void's start: new players begin on Tutorial Island (3094, 3107)
		area("Tutorial Island", DiscordAreaType.REGIONS, 12079, 12080, 12335, 12336),
		area("Marim", DiscordAreaType.REGIONS, 11051),
		area("McGrubor's Wood", DiscordAreaType.REGIONS, 10550),
		area("Mime's Stage", DiscordAreaType.REGIONS, 8010),
		area("Mind Altar", DiscordAreaType.REGIONS, 11083),
		area("Misthalin", DiscordAreaType.REGIONS, 12594, 12595, 12851),
		area("Morytania", DiscordAreaType.REGIONS, 13619, 13620, 13621, 13622, 13876, 13877, 13879, 14133, 14134, 14390, 14391, 14647),
		area("Mr. Mordaut's Classroom", DiscordAreaType.REGIONS, 7502),
		area("Mudskipper Point", DiscordAreaType.REGIONS, 11824),
		area("Mysterious Old Man's Maze", DiscordAreaType.REGIONS, 11591),
		area("Nature Altar", DiscordAreaType.REGIONS, 9547),
		area("Observatory", DiscordAreaType.REGIONS, 9777),
		area("Odd One Out", DiscordAreaType.REGIONS, 7754),
		area("Otto's Grotto", DiscordAreaType.REGIONS, 10038),
		area("Pirates' Cove", DiscordAreaType.REGIONS, 8763),
		area("Piscatoris Hunter Area", DiscordAreaType.REGIONS, 9016, 9271, 9272, 9528),
		area("Poison Waste", DiscordAreaType.REGIONS, 8752, 9008),
		area("Port Tyras", DiscordAreaType.REGIONS, 8496),
		area("Puro-Puro", DiscordAreaType.REGIONS, 10307),
		area("Quarry", DiscordAreaType.REGIONS, 12589),
		area("Ranging Guild", DiscordAreaType.REGIONS, 10549),
		area("Ratcatchers Mansion", DiscordAreaType.REGIONS, 11343),
		area("Rune Essence Mine", DiscordAreaType.REGIONS, 11595),
		area("Runecrafting Guild", DiscordAreaType.REGIONS, 6741),
		area("ScapeRune", DiscordAreaType.REGIONS, 7758, 8261, 10058),
		area("Ship Yard", DiscordAreaType.REGIONS, 11823),
		area("Silvarea", DiscordAreaType.REGIONS, 13366),
		area("Sinclair Mansion", DiscordAreaType.REGIONS, 10807),
		area("Slayer Tower", DiscordAreaType.REGIONS, 13623, 13723),
		area("Troll Arena", DiscordAreaType.REGIONS, 11576),
		area("Trollheim", DiscordAreaType.REGIONS, 11577),
		area("Trollweiss Mountain", DiscordAreaType.REGIONS, 11066, 11067, 11068),
		area("Water Altar", DiscordAreaType.REGIONS, 10827),
		area("Waterbirth Island", DiscordAreaType.REGIONS, 10042),
		area("Wilderness", DiscordAreaType.REGIONS, 11831, 11832, 11833, 11834, 11835, 11836, 11837, 12087, 12088, 12089, 12090, 12091, 12092, 12093, 12343, 12344, 12345, 12346, 12347, 12348, 12349, 12599, 12600, 12601, 12602, 12603, 12604, 12605, 12855, 12856, 12857, 12858, 12859, 12860, 13111, 13112, 13113, 13114, 13115, 13116, 13117, 13367, 13368, 13369, 13370, 13371, 13372, 13373),
		area("Wizards' Tower", DiscordAreaType.REGIONS, 12337),
	)

	private val byRegion: HashMap<Int, DiscordGameEvent> = HashMap()

	init {
		for (area in AREAS) {
			for (regionId in area.regionIds!!) {
				val taken: DiscordGameEvent? = byRegion.put(regionId, area)
				check(taken == null) { "region " + regionId + " is both " + taken + " and " + area }
			}
		}
	}

	/** The place with map region [regionId], or null when it is not one the table knows. */
	@JvmStatic
	fun fromRegion(regionId: Int): DiscordGameEvent? {
		return byRegion[regionId]
	}

	/** The training event of [skill], or null for a number that is not a skill. */
	@JvmStatic
	fun fromSkill(skill: Int): DiscordGameEvent? {
		return if (skill >= 0 && skill < Skill.COUNT) TRAINING[skill] else null
	}

	/** The number of places the table knows. */
	@JvmStatic
	fun regionCount(): Int {
		return byRegion.size
	}

	/** Constitution ranks below the other skills (every fight raises it), Slayer and Fishing above, as RuneLite ranks them. */
	private fun training(skill: Int): DiscordGameEvent {
		val priority: Int = when (skill) {
			Skill.CONSTITUTION -> -1
			Skill.SLAYER, Skill.FISHING -> 1
			else -> 0
		}
		val name: String = Skill.NAMES[skill]
		return DiscordGameEvent("TRAINING_" + name.uppercase(), null, "Training: " + name, "icon_" + name.lowercase(), priority, false, true, false, true, null, null)
	}

	private fun area(name: String, type: DiscordAreaType, vararg regionIds: Int): DiscordGameEvent {
		return DiscordGameEvent(name, name, null, null, AREA_PRIORITY, true, false, false, true, type, regionIds)
	}
}
