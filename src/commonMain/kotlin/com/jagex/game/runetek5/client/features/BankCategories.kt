package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField

/**
 * Not part of the original client: Bank Organizer's categories (RuneLite's ItemCategories), the
 * patterns and hidden exclusions remade for this cache's item names: the OSRS-only ones dropped, the
 * strays they caught in 2011 names excluded, and 2011 items added. Lowercase; a name matches a pattern
 * it contains (Tools & misc: as a whole word, BankOrganizerMatcher). One function a list keeps each
 * method under the class-size check.
 */
object BankCategories {

	const val COUNT: Int = 10

	const val RANGE: Int = 3

	const val TOOLS: Int = 7

	/** By category id (index 0 unused): the name in the settings. */
	@JvmField
	val NAMES: Array<String> = arrayOf("", "Melee", "Magic", "Range", "Skilling", "Potions", "Slayer loot", "Tools & misc", "Food", "Farming", "Skilling outfits")

	/** By category id: on at first (RuneLite's defaults: Melee and Farming). */
	@JvmField
	val DEFAULT_ACTIVE: BooleanArray = booleanArrayOf(false, true, false, false, false, false, false, false, false, true, false)

	/** By category id: RuneLite's default colours. */
	@JvmField
	val DEFAULT_COLOURS: IntArray = intArrayOf(0, 0xFFFF0000.toInt(), 0xFF0000FF.toInt(), 0xFF13FF00.toInt(), 0xFF00FFFF.toInt(), 0xFF9370DB.toInt(), 0xFFCFA60B.toInt(), 0xFFFF7B7B.toInt(), 0xFF800080.toInt(), 0xFF146411.toInt(), 0xFF964B00.toInt())

	private val PATTERNS: Array<Array<String>> = arrayOf(arrayOf(), melee(), magic(), range(), skilling(), potions(), slayerLoot(), tools(), food(), farming(), outfits())

	private val EXCLUSIONS: Array<Array<String>> = arrayOf(arrayOf(), meleeExclusions(), magicExclusions(), rangeExclusions(), skillingExclusions(), potionsExclusions(), slayerLootExclusions(), toolsExclusions(), foodExclusions(), farmingExclusions(), outfitsExclusions())

	/** Category [id]'s patterns. */
	fun patterns(id: Int): Array<String> = PATTERNS[id]

	/** Category [id]'s hidden exclusions: names containing one are never in it. */
	fun exclusions(id: Int): Array<String> = EXCLUSIONS[id]

	private fun melee(): Array<String> = arrayOf(
			"defender", "full helm", "med helm", "medium helm", "heraldic helm", "helm (h", "helm (class",
			"sacred clay helm", "kiteshield", "sq shield", "square shield", "shield (h", "spikeshield", "plateskirt",
			"platelegs", "chainbody", "platebody", "chainskirt", "armour set", "melee set", "initiate", "proselyte",
			"pros'yte", "rock-shell", "obsidian cape", "torva", "statius", "vesta's", "bandos chestplate",
			"bandos tassets", "bandos boots", "dragonfire shield", "warrior helm", "berserker", "neitiznot", "dharok's",
			"torag's", "verac's", "guthan's", "void melee helm", "dwarven helmet", "fighter hat", "fighter torso",
			"runner hat", "granite helm", "granite body", "granite legs", "granite shield", "granite maul",
			"granite mace", "climbing boots", "bronze boots", "iron boots", "steel boots", "black boots", "white boots",
			"mithril boots", "adamant boots", "rune boots", "dragon boots", "gauntlets", "novite", "bathus", "marmaros",
			"kratonite", "fractite", "zephyrium", "argonite", "katagon", "gorgonite", "promethium", "primal", "mace",
			"tzhaar-ket-em", "tzhaar-ket-om", "warhammer", "keris", "scimitar", "longsword", "battleaxe", "greataxe",
			" claws", "halberd", "2h sword", "two-handed sword", "godsword", "saradomin sword", "abyssal whip",
			"toktz-xil-ek", "toktz-xil-ak", "toktz-ket-xil", "silverlight", "darklight", "excalibur", "balmung",
			"fremennik blade", "brine sabre", "cutlass", "wolfbane", "rod of ivandis", "gadderhammer", "bone club",
			"ogre club", "dagger", "sword", "spear", "hasta", "rapier", "maul", "flail", "fire cape", "warrior ring",
			"amulet of strength", "strength amulet", "amulet of accuracy", "crystal shield", "book of war",
			"barrelchest anchor", "eye of the warrior",
		)

	private fun meleeExclusions(): Array<String> = arrayOf(
			"ornament kit", "armadyl chainskirt", "defender lvl", "farseer kiteshield", "eagle-eye kiteshield",
			"skeletal armour set", "splitbark armour set", "spined armour set", "leather armour set", "chaos gauntlets",
			"cooking gauntlets", "goldsmith gauntlets", "family gauntlets", "splitbark gauntlets", "klank's gauntlets",
			" arrows", "pickaxe", "hatchet", "pouch", "void knight mace", "kebbit claws", "long, sharp claws",
			"salty claws hat", "swordfish", "sword fragment", "sword pommel", "godsword shard", "godsword blade",
			"silverlight key", "guthix balance", "defender horn", "rock-shell chunk", "rock-shell shard",
			"rock-shell splinter", "ring of kinship", "arrowtips", " bar", " ore", "gorak claws", "mounted sword",
			"flail dust",
		)

	private fun magic(): Array<String> = arrayOf(
			"air rune", "water rune", "earth rune", "fire rune", "mind rune", "body rune", "cosmic rune", "chaos rune",
			"astral rune", "nature rune", "law rune", "death rune", "blood rune", "soul rune", "mist rune", "dust rune",
			"mud rune", "smoke rune", "steam rune", "lava rune", "elemental rune", "catalytic rune", "staff", "wand",
			"sceptre", "toktz-mej-tal", "void knight mace", "void mage helm", "surgebox", "blastbox", "wizard hat",
			"wizard robe", "wizard boots", "skeletal", "enchanted hat", "enchanted robe", "enchanted top",
			"enchanted set", "ghostly", "mystic", "moonclan", "lunar", "splitbark", "dagon'hai", "infinity", "farseer",
			"ahrim's", "virtus", "zuriel's", "third-age mage", "third-age robe", "third-age amulet", "battle robe",
			"battle hood", "combat robe", "combat hood", "druidic mage", "salve hood", "salve robe", "salve gloves",
			"salve shoes", "wildercress ", "blightleaf ", "roseblood ", "bryll ", "duskweed ", "soulbell ", "ectohood",
			"ectorobe", "ectogloves", "ectoshoes", "runic hood", "runic robe", "runic gloves", "runic shoes",
			"spiritbloom ", "celestial", "zamorak cape", "saradomin cape", "guthix cape", "amulet of magic",
			"arcane blast", "arcane pulse", "arcane stream", "tome of frost", "mages' book", "arcane spirit shield",
			"spectral spirit shield", "talisman", " tiara", "runecrafter", "small pouch", "medium pouch", "large pouch",
			"giant pouch", "rune essence", "pure essence", "teleport", "t'port", "ectophial", "grand seed pod",
			"plain of mud sphere", "camulet", "amulet of glory", "games necklace", "skills necklace", "ring of duelling",
			"combat bracelet", "seers' ring", "sacred clay robe", "sacred clay hat", "blue robe", "cream robe",
			"green robe", "pink robe", "turquoise robe", "healer hat", "fremennik hat", "pink hat", "cream hat",
			"green hat", "blue hat", "turquoise hat", "omni-tiara", "runecrafting gloves", "brimhaven tablet",
			"pollnivneach tablet", "rellekka tablet", "rimmington tablet", "taverley tablet", "trollheim tablet",
			"yanille tablet", "ring of kinship", "ring of slaying", "digsite pendant", "goblin village sphere",
			"dorgesh-kaan sphere", "enchanted lyre", "eye of the mage",
		)

	private fun magicExclusions(): Array<String> = arrayOf(
			"blank ", "astral rune shards", "ground astral rune", "unfinished astral rune", "ground mud runes",
			"noose wand", "wyvern", "lunar bar", "lunar ore", "prophecy tablet", "dagon'hai history", "tiara mould",
			"urist's talisman", "hatchet", "flagstaff", "broken staff", "rand skeletal", "mystic jewel",
			"moonclan manual", "lunar globe", "celestial globe", " cloth", "hunters' talisman", "monkey talisman",
			"teleport spell", "teleport trap", "a special tiara",
		)

	private fun range(): Array<String> = arrayOf(
			"leather cowl", "leather body", "leather chaps", "leather boots", "leather vambraces", "leather armour set",
			"hardleather body", "studded body", "studded chap", "frog-leather", "snakeskin", "coif", "spined",
			"ranger hat", "archer helm", "robin hood hat", "karil's", "vambraces", "chaps", "d'hide", "dragonhide body",
			"dragonhide chaps", "dragonhide set", "guthix dragonhide", "saradomin dragonhide", "zamorak dragonhide",
			"guthix body", "saradomin body", "zamorak body", "armadyl body", "bandos body", "ancient body",
			"armadyl helmet", "armadyl chestplate", "armadyl chainskirt", "pernix", "morrigan's", "sagittarian",
			"void ranger helm", "third-age range", "sacred clay body", "eagle-eye kiteshield", "penance skirt",
			"ranger boots", "ranging cape", "ava's", "archers' ring", "amulet of ranging", "book of law", "bow",
			"seercull", "dorgeshuun c'bow", "hand cannon", "sling", "arrow", "bolts", "bolt rack", "dart", " brutal",
			"bronze knife", "iron knife", "steel knife", "black knife", "mithril knife", "adamant knife", "rune knife",
			"performance knife", "thrownaxe", "throwing axe", "javelin", "jav'n", "chinchompa", "toktz-xil-ul",
			"armadyl dragonhide", "bandos dragonhide", "ancient dragonhide", "eye of the ranger",
		)

	private fun rangeExclusions(): Array<String> = arrayOf(
			"bowl", "rainbow", "bow string", "crossbow string", "bow-sword", "unstrung", "(u)", " (unf)", "arrowtips",
			"arrow shaft", "headless arrow", "broad arrow heads", "barrows - ahrim", "barrows - dharok",
			"barrows - guthan", "barrows - torag", "barrows - verac", "barrows icon", "dart tip",
			"unfinished broad bolts", "pouch", "ballista", "longbow sight", "arrowheads", "broken arrow", "dartboard",
		)

	private fun skilling(): Array<String> = arrayOf(
			"bronze bar", "iron bar", "steel bar", "mithril bar", "adamant bar", "rune bar", "gold bar", "silver bar",
			"blurite bar", "lunar bar", "copper ore", "tin ore", "iron ore", "silver ore", "gold ore", "mithril ore",
			"adamantite ore", "runite ore", "lunar ore", "elemental ore", "coal", "rune essence", "pure essence",
			"uncut", "opal", "jade", "red topaz", "sapphire", "emerald", "ruby", "diamond", "dragonstone", "onyx",
			"oyster pearl", "seaweed", "soda ash", "bucket of sand", "molten glass", "clay", "limestone", "marble block",
			"gold leaf", "granite (", "sandstone (", "plank", "logs", "battlestaff", "unpowered orb", "air orb",
			"water orb", "earth orb", "fire orb", "flax", "bow string", "magic string", "jute fibre", "sinew", "wool",
			"silk", "bolt of cloth", "(u)", "unstrung comp bow", "unfinished broad bolts", "arrowtips", "arrow heads",
			"bolttips", "headless arrow", "bolt tips", "dart tip", "arrow shaft", "feather", "cowhide", "dragonhide",
			"dragon leather", "leather", "snake hide", "yak-hide", "suqah hide", "unicorn hide", "kebbit fur",
			"graahk fur", "kyatt fur", "larupia fur", "weasel fur", "desert devil fur", "spirit shard", "gold charm",
			"stardust", "blurite ore", "bolts (unf)", "crimson charm", "blue charm", "green charm", "dagannoth hide",
		)

	private fun skillingExclusions(): Array<String> = arrayOf(
			"sapphire amulet", "sapphire bracelet", "sapphire necklace", "sapphire ring", "enchant sapphire",
			"emerald amulet", "emerald bracelet", "emerald necklace", "emerald ring", "emerald lens", "enchant emerald",
			"enchanted emerald", "sickle emerald", "ruby amulet", "ruby bracelet", "ruby necklace", "ruby ring",
			"ruby key", "enchant ruby", "diamond amulet", "diamond bracelet", "diamond necklace", "diamond ring",
			"enchant diamond", "diamond root", "blood diamond", "ice diamond", "shadow diamond", "smoke diamond",
			"dragonstone ammy", "dragonstone ring", "dragonstone amulet", "dragonstone bracelet", "dragonstone necklace",
			"onyx amulet", "onyx bracelet", "onyx necklace", "onyx ring", "enchant onyx", "jade vine", "potion (unf)",
			"guthix balance", "ring (unfired)", "sacred clay", "volatile clay", "bracelet of clay", "feather headdress",
			"feathered journal", "magic gold feather", "barrel of coal-tar", "charcoal", " set", "ore powder",
			"opal bolts", "jade bolts", "topaz bolts", "sapphire bolts", "emerald bolts", "ruby bolts", "diamond bolts",
			"dragonstone bolts", "onyx bolts", "pearl bolts", "machete", "sapphire glacialis", "ruby harvest",
			"emerald lantern", "sapphire lantern", "edible seaweed", "seaweed net", "seaweed sandwich", "clay ring",
			" battlestaff", "toadflax", "silk hood", "metal feather", " body", " boots", " chaps", " coif", " vambraces",
			" gloves", " cowl", " book", " armour", "guthix dragonhide", "saradomin dragonhide", "zamorak dragonhide",
			"charm slice", "spare construct", "demon statuette", "chalice", "diamond key", "bauble", "in a box",
			"ground seaweed", "clay attack stone", "clay deposit", "clay fireplace", "clay mould", "limestone altar",
			"woolly", "featherfoil", "ancient dragonhide", "armadyl dragonhide", "bandos dragonhide", "torn bag",
			"bucket of coal", "coal bag",
		)

	private fun potions(): Array<String> = arrayOf(
			"potion", " mix (", "supermix", "super ", "saradomin brew", "zamorak brew", "guthix rest", "guthix balance",
			"antifire", "antipoison", "antidote", "sanfew serum", "relicym's balm", "grimy ", "clean ", "guam",
			"marrentill", "tarromin", "harralander", "ranarr", "toadflax", " irit", "irit potion", "avantoe", "kwuarm",
			"snapdragon", "cadantine", "lantadyme", "dwarf weed", "torstol", "spirit weed", "wergali", "vial",
			"eye of newt", "limpwurt root", "red spiders' eggs", "white berries", "snape grass", "mort myre fungus",
			"nail beast nails", "yew roots", "magic roots", "wine of zamorak", "cactus spine", "potato cactus",
			"jangerberries", "cave nightshade", "poison ivy berries", "unicorn horn", "chocolate bar", "chocolate dust",
			"goat horn dust", "desert goat horn", "kebbit teeth", "blue dragon scale", "dragon scale dust",
			"crushed nest", "bird's nest", "cockatrice egg", "toad's legs", "coconut milk", "ashes", "swamp tar",
			"pestle and mortar", "ruby harvest", "sapphire glacialis", "black warlock", "overload", "extreme ",
			"magic essence", "star flower", "gorak claw",
		)

	private fun potionsExclusions(): Array<String> = arrayOf(
			" seed", "grimy key", "iban's ashes", "ashes scroll", "super kebab", "super truth serum", "gas vial",
			"vial of stench", "dream vial", "enchanted vial", "vial of liquid", "super fishing explosive",
			"super large egg", "clean cog", "clean necklace", "clean pipe", "clean pistons", "bowl of milk", "in a box",
			" gloves", "glaze", "moonshine", "fish vial", "exploding vial", "waking sleep vial", "blue vial",
			"green vial", "violet vial", "yellow vial", "spicy toad",
		)

	private fun slayerLoot(): Array<String> = arrayOf(
			"bones", "half of a key", "crystal key", "gold charm", "rune full helm", "rune platebody", "rune platelegs",
			"rune plateskirt", "rune chainbody", "rune sq shield", "rune kiteshield", "rune med helm", "rune halberd",
			"rune spear", "rune warhammer", "rune battleaxe", "rune 2h sword", "rune longsword", "rune scimitar",
			"rune boots", "rune arrowtips", "dragon halberd", "dragon dagger", "dragon longsword", "dragon scimitar",
			"dragon spear", "dragon 2h", "dragon plateskirt", "dragon platelegs", "dragon chainbody", "dragon med helm",
			"dragon dart tip", "dragon arrowtips", "granite maul", "granite shield", "abyssal whip", "dark bow",
			"steam battlestaff", "brine sabre", "zamorakian spear", "black dragonhide", "blue dragonhide",
			"green dragonhide", "red dragonhide", "hilt", "godsword shard", "shield left half", "shield right half",
			"draconic visage", "arcane sigil", "divine sigil", "elysian sigil", "spectral sigil", "spirit shield",
			"holy elixir", "onyx bolt", " impling jar", "scroll box", "ornament kit", "abyssal head", "basilisk head",
			"cockatrice head", "kurask head", "kbd heads", "kq head", "long bone", "crawling hand", "ancient effigy",
			"crimson charm", "blue charm", "green charm", "dagannoth hide",
		)

	private fun slayerLootExclusions(): Array<String> = arrayOf(
			"bones to bananas", "bonesack", "sheep bones", "marinated j' bones", "pasty jogre bones", "burnt ",
			"corrupt ", "c. dragon", "(g)", "(t)", "(h1)", "(h2)", "(h3)", "(h4)", "(h5)", "(charged)", "(e)",
			"dragonhide set", "bones to peaches", "charm slice", "ground ", "animals' bones", "chewed bones",
			"human bones", "mangled bones", "strongbones",
		)

	private fun tools(): Array<String> = arrayOf(
			"hatchet", "pickaxe", "inferno adze", "knife", "chisel", "hammer", "saw", "tinderbox", "needle", "thread",
			"shears", "glassblowing pipe", "mould", "nails", "rope", "lockpick", "rock pick", "pestle and mortar",
			"fishing rod", "fishing bait", "feather", "harpoon", "fishing net", "pot", "crayfish cage",
			"karambwan vessel", "rake", "seed dibber", "spade", "trowel", "secateurs", "watering can", "compost",
			"supercompost", "plant cure", "empty sack", "butterfly net", "box trap", "bird snare", "rabbit snare",
			"teasing stick", "noose wand", "butterfly jar", "impling jar", "bucket", "bowl", "jug of water", "specimen",
			"talisman", "small pouch", "medium pouch", "large pouch", "giant pouch", "dye", "candle", "lantern", "torch",
			"machete", "blackjack", "spice", "ball of wool", "goldsmith gauntlets", "ice gloves", "swamp tar",
			"swamp paste", "holy symbol", "holy wrench", "vampire dust", "brown apron", "olive oil", "sacred oil",
			"prayer book", "ghostspeak amulet", "silver sickle", "ring of charos", "sled", "ectophial", "rogue kit",
			"bonecrusher", "tarn's diary", "tackle box", "gem bag", "jar generator", "crystal chime", "barbarian rod",
			"coal bag",
		)

	private fun toolsExclusions(): Array<String> = arrayOf(
			"bronze knife", "iron knife", "steel knife", "black knife", "mithril knife", "adamant knife", "rune knife",
			"performance knife", "torag's hammers", "nail beast nails", "bucket of sand", "metal feather",
			"feather headdress", "magic gold feather", " impling jar", "pot of tea", "pot lid", "unfired", "key mould",
			"camel mould", "conductor mould", "sigil mould", "gold bowl", "golden bowl", "stone bowl",
			"compost mound pouch", "generate compost scroll", "bellows", "jack lantern mask", "oil lantern frame",
			"airtight pot", "blessed pot", "golden pot", "pungent pot", "sealed pot", "silver pot", "smouldering pot",
			"pot of quicklime", "pot of weeds", " handle", " head", "spades (", "firepit with pot", "glaze",
			"bowl of black mess", "bowl of honey", "bowl of milk", "bowl of blue water", "bowl of red water",
			"half baked bowl", "half made bowl", "unfinished bowl", "herb bowl", "anniversary", "monkey talisman",
			"gnomebowl mould", "rabbit mould", "rod clay mould", "chain link mould", "meatcorn rope", "rope bell-pull",
		)

	private fun food(): Array<String> = arrayOf(
			"raw", "pie", "anchovies", "anchovy", "baked potato", "bass", "wine", "bread", "cake", "chilli potato",
			"chilli con carne", "cod", "cooked", "curry", "egg potato", "herring", "kebab", "lobster", "mackerel",
			"manta ray", "pizza", "monkfish", "mushroom potato", "pike", "potato with cheese", "potato with butter",
			"tuna potato", "premade", "roast", "salmon", "sardine", "sea turtle", "shark", "shrimp", "spicy", "sandwich",
			"stew", "strange fruit", "swordfish", "trout", "tuna", "cooking gauntlets", "rocktail", "cavefish",
			"crayfish", "karambwan", "cave eel", "slimy eel", "rainbow fish", "jubbly", "oomlie", "chocolate", "batta",
			"crunchies", "worm hole", "veg ball", "toads' legs", "fruit blast", "pineapple punch", "wizard blizzard",
			"short green guy", "drunk dragon", "choc saturday", "blurberry special", "cheese", "ugthanki", "snail meat",
			"spider on", "scrambled egg", "fried onions", "fried mushrooms", "mushroom & onion", "egg and tomato",
			"onion & tomato", "cooked sweetcorn", "chompy", "blue crab", "cave moray", "moray &", "dusk eel",
			"giant flatfish", "flatfish &", "heim crab", "red-eye", "salve eel", "short-fin", "snipper", "edicap potato",
			"gissel potato", "cave potato", "lava eel", "gnomebowl", "baguette", "biscuits", "sweets", "candy cane",
			"wrapped candy", "cup of tea", "pot of tea", "nettle tea", "doughnut", "eel sushi", "frogburger",
			"frogspawn gumbo", "coated frogs' legs", "giant frog legs", "field ration", "turkey", "crab meat", "fillets",
			"caviar", "pumpkin", "minced meat", "skewered", "burnt", "peach",
		)

	private fun foodExclusions(): Array<String> = arrayOf(
			"wine of zamorak", "half full wine jug", "code key", "decoder", "granite lobster pouch", "lobster pot",
			"spike", "shark tooth", "crayfish cage", "karambwan vessel", "chocolate dust", "cake tin",
			"cheese feast scroll", "ugthanki dung", "chompy bird hat", "seed", "big swordfish", "big shark", "big bass",
			"empty wine bottle", "curry leaf", "chocolatey", "drawers", "crawl", "scrawled", "rapier", "magpie",
			"papyrus", "pie dish", "pie recipe", "strawberr", "brawling", "piece", "harpie", "mounted", "gloves",
			"approx", "karambwan paste", "karambwanji", "poison karambwan", "poisoned", "batta tin", "gnomebowl mould",
			"turkey book", "sapling", "afro", "burnt bones", "burnt jogre bones", "burnt diary", "burnt page",
			"salve eel's", "bones to peaches",
		)

	private fun farming(): Array<String> = arrayOf(
			"seed", "plant pot", "watering can", "secateurs", "rake", "seed dibber", "spade", "gardening trowel",
			"plant cure", "compost", "amulet of farming", "empty sack", "basket", "strawberries", "strawberry", "banana",
			"apples", "cooking apple", "oranges", "curry leaf", "cabbage", "sweetcorn", "onion", "potato", "pineapple",
			"watermelon", "papaya fruit", "coconut", "calquat fruit", "mushroom", "grapes", "nightshade", "cactus spine",
			"hops", "snape grass", "barley", "jute fibre", "redberries", "cadava berries", "dwellberries",
			"jangerberries", "white berries", "poison ivy berries", "marigolds", "nasturtiums", "limpwurt root",
			"woad leaf", "roots", "acorn", "sapling", "seedling", "tomato", "juju farming", "kalferberries",
			"lergberries", "white lily", "sunflower", "scarecrow", "hay sack",
		)

	private fun farmingExclusions(): Array<String> = arrayOf(
			"crystal seed", "odd bird seed", "mithril seeds", "grand seed pod", "bone seeds", "defective spade",
			"compost mound pouch", "generate compost", "snake basket", "waste-paper basket", "cooked sweetcorn",
			"fried onions", "chopped onion", "onion & tomato", "& onion", "potato with", "chilli potato", "baked potato",
			"tuna potato", "egg potato", "mushroom potato", "pineapple chunks", "pineapple punch", "pineapple ring",
			"pineapple pizza", "p'apple", "tenti pineapple", "watermelon slice", "coconut milk", "coconut shell",
			"black mushroom", "fried mushrooms", "sliced mushrooms", "button mushroom", "barley malt",
			"bones to bananas", "sliced banana", "sliced red banana", "red banana", "green banana", "acorn missile",
			"rake handle", "rake head", "spade handle", "spade head", "spadeful", "approx", "cat basket",
			"cushioned basket", "basket of eggs", "banana stew", "burnt", "edicap potato", "gissel potato",
			"crab potato", "bouldabass potato", "moray potato", "eel potato", "flatfish potato", "red-eye potato",
			"snipper potato", "baked cave", "raw cave potato", "roast potatoes", "rotten potato", "consecration seed",
			"marker seeds", "strange potato", "chopped tomato", "spicy tomato", "egg and tomato", "& tomato",
			"rotten tomato", "bagged",
		)

	private fun outfits(): Array<String> = arrayOf(
			"lumberjack", "rogue", "agile top", "agile legs", "boots of lightness", "gloves of silence", "camo",
			"larupia", "kyatt", "graahk", "spotted cape", "spottier cape", "goldsmith gauntlets", "cooking gauntlets",
			"ice gloves", "mining helmet", "chef's hat", "white apron", "brown apron", "runecrafter",
			"runecrafting gloves", "builder's", "flame gloves",
		)

	private fun outfitsExclusions(): Array<String> = arrayOf(
			"rogue's purse", "fur", "pouch",
		)
}
