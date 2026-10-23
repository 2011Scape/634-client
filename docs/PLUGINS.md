# Plugins

How to add to the client without changing it: a plugin is a jar in a folder, written against a
small, stable API, that reads the game, draws on it, adds menu entries and commands, and keeps its
own settings. The XP drops (`XpDrops`), the idle notifier (`IdleNotifier`), the menu swapper
(`MenuEntrySwapper`), the inventory value (`InventoryValue`), the name plates (`NamePlates`), the ground
tiles (`GroundTiles`), the npc indicators (`NpcIndicators`), the Discord presence (`DiscordPresence`),
the ground items (`GroundItems`), the loot tracker (`LootTracker`), the object markers
(`ObjectIndicators`), the XP tracker (`XpTracker`), the opponent information (`OpponentInfo`), the
boosts information (`Boosts`), the bank organizer (`BankOrganizer`), the status bars (`StatusBars`), the
timers (`TimersAndBuffs`), the tile indicators (`TileIndicators`), the XP globes (`XpGlobes`), the
Barrows Brothers (`BarrowsBrothers`), the attack styles (`AttackStyles`), the cannon (`Cannon`), the
anti drag (`AntiDrag`), the emojis (`Emojis`), the mouse tooltips (`MouseTooltips`), the key remapping
(`KeyRemapping`), the custom cursor (`CustomCursor`) and the item stats (`ItemStats`) are twenty-eight,
built in, so the client's own code shows the way.

## Contents

- [What a plugin can do](#what-a-plugin-can-do)
- [Installing plugins](#installing-plugins)
- [Managing plugins in the game](#managing-plugins-in-the-game)
- [The built-in plugins](#the-built-in-plugins)
- [Writing a plugin](#writing-a-plugin)
- [Cookbook: two plugins in a few dozen lines](#cookbook)
- [The API](#the-api)
- [Building](#building)
- [Trouble](#trouble)
- [How it works inside the client](#how-it-works-inside-the-client)
- [What comes next](#what-comes-next)

## What a plugin can do

A plugin follows the rule RuneLite's do: it **reads** the game and **shows** things; it does not play
the game for you.

- Read: the players, npcs and items on the ground in the scene, and what each actor is doing (its
  animation, graphic, target, facing, hit splats, overhead text and icons); skills, player variables
  (varps and varbits), inventories; the quests and where the player is with each (not started, in
  progress or finished, as the quest journal shows it); the interfaces, component by component (type, place on the
  screen, item, sprite, options, children) and which are open; the item, npc and location types
  from the cache (names, options, sizes, values); the locations and the movement flags of any tile;
  whether a player is a friend, in your friends chat or could attack you here; where any of it is
  on the screen; what an item fetches on the Grand Exchange (the guide prices the game's website
  publishes, fetched in the background) and from High Level Alchemy, and whether it can be traded;
  whether an item on the ground is another player's drop made public, and how high its pile lies;
  the shape of a location on the screen (its hull, its click box, its outline drawn) and what a
  location that changes with the player's variables is now.
- Draw: on the game view every frame (under the interfaces), or over the whole frame: lines, boxes,
  circles, polygons, text in the client's three fonts, the cache's sprites, the icons of items and
  pictures of its own (a PNG), within a clip of its own choosing; panels the client places in a corner
  of the game view and the player moves (RuneLite's overlays, with its panel, line and title
  components), infoboxes in the shared infobox panel, and tooltips by the mouse; and hide a component
  of an interface on this client (to draw its own in its place), or change how long an item must be
  held before it drags; rewrite a chat line or an overhead line before it shows, and add pictures of its
  own to the chat icons (`<img=N>`); set a component's text and the client variables (the chat box's
  typed line); remap the player's own keys before the client reads them (`KeyInput`); and set the
  mouse cursor, from a picture or an item's icon.
- Add: entries to the right-click menu of a tile, npc, player, ground item, location or interface
  component (an inventory slot included), with an action of its own, and entries that open a
  submenu of its own beside the menu; console commands (`::name`); lines in the chat box and the
  developer console.
- Edit: the right-click menu once it is built, before the left click is resolved: take an entry out,
  rename it, make it the left click or sink it below the others; and drop a click, a key or a turn of the wheel before the
  game acts on it (a hotkey of the plugin's own).
- Know: where the mouse is and what tile is under it, which keys and buttons are held, where the
  camera looks, where a tile falls on the minimap, the run energy, weight, world, region and
  membership, and the friends, ignore and friends chat lists; and play a sound effect.
- Work: at the next cycle or a later game tick, or on a thread of its own that hands its result
  back to the game thread (`invokeLater`), for a web request the game must not wait for.
- Hear: every client cycle and game tick, every stat update, variable change, chat line and inventory
  update, the menu being built and clicked, keys and mouse buttons, logins and logouts, the graphics
  mode changing; every npc, player and ground item appearing and leaving, every hit, animation,
  change of target and line of overhead text, every projectile and graphic; interfaces opening and
  closing, the root interface changing, the menu each time it is built, keys typed and released, and
  the mouse wheel.
- Keep: settings of its own, saved beside the plugins, which the plugin manager shows as switches,
  numbers, choices, text, colours (with the client's colour picker, which a plugin can also open
  itself) and keys.

It cannot send anything to the server, click or walk for you, or change the game's files. That is
the API's policy, not a sandbox: a plugin is code running in the client with everything the client
can do, so **only install plugins from people you trust**, as you would any program.

## Installing plugins

Put the plugin's jar in the plugins folder, `~/.634-client/plugins` (`C:\Users\<you>\.634-client\plugins`
on Windows), and start the client. The folder is made on the first start. Another folder:
`--plugins <dir>` on the client's command line (in the Void launcher: Settings, Developer, *Extra
client arguments*); `--no-plugins` loads none.

In the game, the developer console (the `` ` `` key) has:

| Command | What it does |
|---|---|
| `::plugins` | Lists every plugin: id, name, version, author, whether it is on, off or failed (and why), and its jar. |
| `::plugin <id> off` | Turns a plugin off, now and on later starts (kept in `~/.634-client/plugins.properties`). A plugin that is off runs nothing: its class is not even initialised until it is turned on. |
| `::plugin <id> on` | Turns it on again, or tries again one that failed. |

A plugin's own settings are in `~/.634-client/plugin-config/<id>.properties`.

## Managing plugins in the game

The Settings tab (the spanner) has a third button beside Graphics Settings and Audio Settings:
**Plugins**. It opens a window listing every plugin, built in and from the plugins folder, with
its icon (`Plugin-Icon`), name, version, author and where it came from, a tickbox that turns it on and off,
and **Settings** for a plugin that declared any; hovering a row shows the plugin's description
(`Plugin-Description` in a jar's manifest). A built-in's switch is its own "enabled" setting (so `::xpdrops` and the
window agree); a jar's starts and stops the plugin, as `::plugin <id> on|off` does. A failed plugin
shows why. The settings page shows each declared setting as a tickbox, a number typed
into a box (click it, type, Enter; Escape leaves it; a number outside the range is held to it), or
a choice from a list; every change is saved at once and the plugin hears of it (`SettingChanged`). `::pluginmanager` opens and closes the window from the console.

A plugin that throws is turned off on the spot, with a line in the console and the chat box
("Plugin X was disabled: ..."), and the game goes on. `::plugin <id> on` tries it again; the
stack trace is in the client's log (the launcher keeps it in `~/.void/logs`).

## The built-in plugins

Fifteen plugins come with the client, on the same API as the ones from the plugins folder, each with
its switch in the Plugins window: XP drops (`::xpdrops`), the idle notifier (`::idle`), the
menu entry swapper (`::menuswapper`), the inventory value (`::invvalue`), name plates (`::nameplates`), the
ground tiles, the npc indicators, the Discord presence (`::discord`), the ground items (`::grounditems`), the loot tracker (`::loot`), the object markers (`::objectmarkers`), the XP tracker (`::xp`), the opponent information (`::opponentinfo`) and the boosts information (`::boosts`); and **Overlays**, which has no switch: the settings of every plugin's panels, below.

**Moving the panels.** Every panel a plugin puts on the game view (the opponent's health, the XP
tracker's boxes, the boosts, the inventory value, the infoboxes, a plugin of your own's) can be moved,
as in RuneLite. Hold **Alt** (the Overlays drag hotkey) and each panel shows a yellow outline:

- drag a panel to put it anywhere on the game view; it stays there, measured from the nearest side,
  when the window is resized;
- drop it on one of the corner boxes that appear while dragging (top left, top centre, top right,
  bottom left, bottom right, above the chat box's right, the very top right) to stack it there with the
  others; the boxes can be dragged too (`::resetanchors` puts them back);
- drag a resizable panel's edge (the mouse shows the resize arrows) to make it wider or narrower; the
  infobox panel wraps to the width you give it;
- **Alt and right-click** a panel to put it back where it started.

While Alt is held a click on a panel is the panel's, never the game's. **Shift and right-click** a panel
for its own entries (**Configure** opens the plugin's settings) and **Overlay Origin**, which pins it to
a side or corner of the screen, or to the side panel, the chat box or the minimap. On an infobox,
**Detach** gives it (with the other boxes of the same kind) a panel of its own, **Flip** turns a panel
between across and down, and **Delete** puts a detached panel's boxes back; dropping one infobox panel
on another (it turns red) merges them. The places are kept in `plugin-config/overlay-layout.properties`,
under RuneLite's key names.

**Overlays** holds RuneLite's overlay settings: the font of the overlays drawn in the scene, of the
tooltips, of the panels and of the infoboxes (small, regular or bold), whether tooltips go above or under
the mouse, the infoboxes' size and outlined text, the panels' background colour, and the drag hotkey.

**Ground Tiles** marks tiles on the ground the way RuneLite's Ground Markers do. Hold Shift and
right-click a tile for **Mark tile**; a marked tile's Shift menu has **Unmark tile**, **Label tile**
(type the label, Enter keeps it, Escape leaves it as it was) and **Colour tile** (gives it the
colour chosen in the settings, when it has another). Settings: the tile colour, the fill opacity
(0 to 255), the border width, whether the tiles are drawn on the minimap, and whether the Shift
menu also has **Export ground tiles**, **Import ground tiles** and **Clear ground tiles**, which
`::groundtiles export|import|clear` do as well: Export puts the tiles of the map regions around you
on the clipboard as RuneLite's JSON (`[{"regionId":12850,"regionX":20,"regionY":37,"z":0,"color":"#FFFFFF00","label":"Home"}]`),
Import reads such a string from the clipboard (a RuneLite export imports as it is), and Clear
forgets the tiles around you. The tiles are kept in the plugin's settings file, one line a map
region in the same JSON. `::groundtiles` alone turns the plugin on and off.

**Menu Entry Swapper** chooses the left click and the Shift-click of a thing, after RuneLite's
Menu Entry Swapper. Hold Shift and right-click an npc, an object, a ground item or an item for
**Swap left click: <option>** and **Swap shift click: <option>** over each of its options (and
**Walk here** for the first three), and **Reset left click** / **Reset shift click** once one is
chosen; a chat line says what was set. A chosen click is kept in the settings by the thing (an npc
by its type, so it holds for every banker; an item in the backpack by the item, in another component
by the component and the item). Switches: whether the left or the Shift-click is customisable at
all, for npcs, objects and ground items; **Shift-click Walk here** for each of those kinds; and
**Remove dead npc menus**. Not here from RuneLite: its submenus (the swaps are entries of the Shift
menu) and its preset swaps, since every click is configurable here. A settings page taller than the
window scrolls with the mouse wheel.

**NPC Indicators** highlights npcs the way RuneLite's NPC Indicators do. Hold Shift and
right-click an npc for **Tag** (this one npc, until it goes) or **Tag-All** (every npc of that
name, kept in the settings); **Untag** and **Untag-All** undo them, and `::tag <name>` and
`::untag <name>` edit the list from the console. Settings: the highlight style (hull, tile, true
tile, south-west tile, south-west true tile, any of them together), the highlight colour, the fill
opacity and the border width; **NPCs to highlight**, the list itself, names separated by commas with
`*` as a wildcard (`TzHaar*`, `*impling`); names above the npcs and on the minimap; the menu
highlight (the npc's name, its level or both in the highlight colour in the right-click menu); ignore
dead npcs (no highlight once the health bar is empty) and a colour for a dead npc's menu entries;
and the respawn timer, which remembers where a tagged npc stood and how long it took to come back:
the first death counts up on its tile (dimmed) while the respawn is learnt, every death after it
counts down. Not here from RuneLite: the model outline (the hull is
its bounding box), per-npc colours and styles, and ignoring pets.

**XP Drops** draws XP drops the way Old School does, with RuneLite's XP Drop plugin on top. RuneLite's
plugin restyles the game's own drops, which 634 does not have, so the drops are drawn here and Old
School's XP drop setup is part of the settings: the position (Right, left of the minimap; Middle;
Left), the size (Smallest to Largest), the duration and the speed, the colour (White, Lilac, Cyan,
Jade, Lime, Yellow, Orange, Pink), the skills gained in one tick in one drop or one each (Group), an XP
counter (the total or the most recent skill) and a progress bar under it (the most recent skill or one
you choose). RuneLite's options: hide the skill icons, a standard colour of your own, and a colour for
a melee, ranged or magic drop while a prayer or curse of that kind is on (#1580AD by default), and the
fake XP drop delay, which shows the last drop again after that many ticks without one. Right-click the XP
button by the minimap for **Setup**, which opens these settings. Not quite Old School: its goals and its
drops for actions that give no experience (634 sends nothing for those).

**Discord** shows what you are doing in the Discord user panel, the way RuneLite's Discord plugin
does, under "Playing Void Client": the skill you are training ("Training: Fishing", with the
skill's icon), where you are (the state line: a boss, a city, a dungeon, a minigame or another
region, from the map region you stand in, under its 2011 name), "In Game" when the place is not
one it knows, "In Menu" on the login screen and in the lobby, and the elapsed time. Settings: the
elapsed time (since login, since the activity began, or hidden), the activity timeout (a skill
leaves the status that many minutes after its last experience drop; 5 by default), whether to show
a status on the main menu, and a switch for each kind of thing shown: skilling, bosses, cities,
dungeons, minigames and regions. `::discord` turns it on and off; `::discord status` says how the
connection is doing and what is shown. It talks to the Discord desktop app on this computer over
Discord's local RPC socket, so Discord must be running here; it connects again by itself when
Discord starts later, and shows nothing in the browser. Not here from RuneLite: its Discord party
(RuneLite removed that too) and the PvP and Deadman world states (no such worlds in 634). The
places are RuneLite's table kept to the regions of the 634 map, with the 2011 content it never
had (Daemonheim, Soul Wars, Mobilising Armies, the Thieves' Guild, the Chaos Tunnels, the Living
Rock Caverns, the Wilderness, Nex, Tormented Demons). The icons belong to the Discord application
"Void Client"; the config key `applicationId` in `plugin-config/discord.properties` points the plugin at
an application of your own, which needs the same art uploaded under the keys `default` and
`icon_<skill>` (`icon_attack` to `icon_dungeoneering`, lower case).

**Ground Items** is RuneLite's Ground Items, with every one of its settings. Each item on the ground
shows its name, its quantity and its value over its tile: the Grand Exchange guide price (from the
game's website, see `--prices`) and the High Level Alchemy value, either or both or neither. The label's
colour comes from the value tier the stack is above (low 20,000, medium 100,000, high 1,000,000, insane
10,000,000 by default, each with its own colour and price; a price of 0 turns the tier off), or from the
lists you keep: **Highlighted items** and **Hidden items**, names separated by commas, with `>N` or `<N`
for a quantity and `*` as a wildcard (`Coins>1000`, `Rune*`); an exact name beats a wildcard, and a
highlight beats a hide. Hold the hotkey (Alt) to see hidden items too, with two boxes after each name:
**−** hides it, **+** highlights it; clicking the name highlights it and right-clicking it hides it.
Tapping the hotkey twice quickly hides every label until the next press. The right-click menu shows a
highlighted item's colour on its name or option, a stack's quantity, can sink hidden items below the
rest and merge identical entries ("x 3"). Hold Shift and right-click an item for **Color**, a submenu
that gives the item a colour of its own (**Pick** opens the colour picker). Also: hide items under a
value (untradeables spared), show highlighted items only, outline the tiles, outlined text, a despawn
timer (a pie, seconds or ticks) for your own drops, your kills' loot and items you put on a table, an
ownership filter (only your drops and loot), lootbeams over highlighted or valuable piles (Light or
Modern), and a notification when a highlighted or valuable drop appears. Not quite RuneLite: 634 does
not say when an item will despawn, so the timers count as RuneLite did before the game told it (two
minutes for loot, three for a drop, ten on a table, thirty in an instance); and a lootbeam is drawn
over the scene rather than added to it as a model.

**Loot Tracker** is RuneLite's Loot Tracker. It keeps the loot of every kill (an npc's items that
appear on its tiles as it dies, by RuneLite's rules; another player's when you kill one), of
pickpocketing, the crystal chest, the thieving chests, the Barrows chest, implings caught barehanded
and their jars, caskets, birds' nests, and clue scrolls should the server give them; for these the loot
is what your backpack gained, and what fell on your tile when it was full. `::loot`, or **Open** on its
row of the plugin manager, opens its window: the view (grouped by source or kill by kill), whether
ignored items show, and collapse or expand everything; the total count and value with **Reset all**;
and a box a source, newest first, with its level or kill count, its value and its items. A box's title
has **Collapse**, **Hide loot** (ignore the source), **Reset** and **View details**, and an item has
**Toggle item** (ignore it); hovering one shows its average or its prices on the line under the totals.
Settings: ignored items and sources, the price type (Grand Exchange or High Alchemy) and whether to
show it, remembering the loot between sessions (per account, RuneLite's saved format, kept a year), a
chat line for each npc or player kill ("You've killed a Goblin for 1.2K loot.") and the value of a
Barrows chest. Not here from RuneLite: its loot sync with RuneLite's website, and the sources 634 or
the server does not have (raids, Wintertodt and the rest of the later game).

**Object Markers** is RuneLite's Object Markers. Hold Shift and right-click an object for **Mark
object**; a marked object's menu has **Unmark object** and three submenus: **Mark border color** and
**Mark fill color** (the colours already in use and RuneLite's five, or **Pick color** for the colour
picker; the fill also has **Reset**) and **Mark style** (**Hull**, **Outline**, **Clickbox**, **Tile**,
and **Reset** back to the settings). Settings: which styles a marker without its own uses, the marker
colour, a fill colour for new markers, the border width and the outline's feather. The markers are kept
by map region in RuneLite's JSON, so RuneLite's markers paste into the settings file
(`plugin-config/objectindicators.properties`, `region_<id>=[...]`). An object that changes with your
progress (a tree, a door, a farming patch) is drawn only while it is the one you marked, by name. Not
quite RuneLite: 634's click box for an object is the game's own picking area (rounded), not RuneLite's
box; and the hull and outline are worked out from the game's own picking of the model, sampled a few
pixels apart on a large object.

**XP Tracker** is RuneLite's XP Tracker. RuneLite shows it in a side panel, which this client does not
have, so the panel is a window (`::xp`, or **Open** on its row of the plugin manager) and RuneLite's
canvas boxes sit on the game view. The window has the total gained and per hour (right-click: **Reset
All**, **Reset All/hr**, **Pause All**, **Unpause All**) and a box for each skill trained this session:
its icon, four labels you choose (XP gained, XP left, XP/hr, actions left by default; also time to
level, actions/hr and actions done), and a progress bar to the next level in the skill's colour.
Right-click a box for **Collapse**, **Reset**, **Reset others**, **Reset/hr**, **Pause** and **Add to
canvas**; hovering one shows its experience against the goal on the totals' line. A skill on the canvas
is a small box at the left of the game view with its icon, two lines and the bar; the Stats tab's
skills have **Add to canvas** in their menus too. Settings: RuneLite's, including saving the tracking
between sessions (per account), hiding maxed skills, pausing at logout and after minutes without
experience, resetting the rates after minutes, the labels and the bar's label, and moving recently
trained skills to the top. Not here from RuneLite: its Wise Old Man link (an Old School website) and
skill-guide goals (634 has none), so the goal is always the next level, and the intermediate level
markers have nothing to mark.

**Opponent Information** is RuneLite's. The npc or player you last attacked, followed or traded with is
your opponent until you have dealt with nothing for five seconds; a panel at the top left of the game
view shows its name and health bar, as lifepoints out of its most, a percentage, or both (**Display
style**). An npc's most lifepoints come with the client (`NpcHitpoints`, taken from the Void server's npc
definitions; an npc not there shows a percentage), a player's from the website's hiscores (Constitution
times ten). The lifepoints are worked back from the bar as RuneLite does, so they can be a little out.
**Show opponents in menu** puts a `*` before an npc's name on its Attack entry while it is your opponent
or fights you. **Lookup players on interaction** compares a player you deal with to you: their combat
levels against yours, green where yours are higher and red where lower, in a panel at the bottom left.
Not here from RuneLite: the game's own boss health bar, which 634 does not have.

**Boosts Information** is RuneLite's. The combat skills (Attack, Strength, Defence, Ranged, Magic), and
with **Display boosts** set to Non Combat or Both the others, that are boosted above or drained below
their level show as infoboxes (the skill's icon and its level, or the boost with **Show relative boosts**),
in a panel ("Attack 112/99", **Display panel**) or in a compact list of small icons (**Compact display**),
green above the **Boost threshold**, yellow at or under it and red when drained; **Notify on boost
threshold** sends a desktop notification when a boost wears down to it. **Next buff change** and **Next
debuff change** (always, only while a stat is boosted or drained, or never) show the seconds until the
next boosted stat drops and the next drained one comes back: the server moves each stat one level toward
its own every minute, timed from the last change seen. RuneLite's Preserve prayer is not in 634; the
Berserker curse, which holds a boost one firing longer on the server, makes the next drop 69 seconds
after the last instead of 60. Constitution, Prayer and Summoning are points, not boosts, and are left out
as RuneLite leaves out Hitpoints and Prayer; Dungeoneering is one of the other skills.

**Bank Organizer** is the plugin hub's (by Ideonomy). While the bank is open, every item in a category
that is on gets a box over its slot in the category's colour, split into one segment a category when it
falls in several: Melee, Magic, Range, Skilling, Potions, Slayer loot, Tools & misc, Food, Farming and
Skilling outfits, each with its own switch, colour and **excluded items** (comma-separated words; an item
whose name contains one is left out). A category matches words in item names; the lists were remade for
2011 items (the plugin's are Old School's). Melee and Farming are on at first, as in RuneLite. **Show
boxes** hides them all. **Bank cleanup** adds a last segment to quest items: white (**Keep colour**) while a
quest that needs the item is not finished, black (**Discard colour**) once every quest that needs it is.
The rules were remade for 2011 quests; items with a use after their quests (rewards, teleports, items other
content needs) are not marked. A quest is finished when the game's own quest journal says so (the quest's
progress variables against the cache's quest list, `ClientApi.questState`), so a quest the server does not
run stays not started and its items stay Keep. Not here from the plugin: its Sailing category and the
achievement diary conditions of its Bank Cleanup.

**Status Bars** is RuneLite's. A bar on each side of the side panel shows lifepoints, prayer, run energy,
special attack or summoning points (**Left bar** and **Right bar**), filled from the bottom in RuneLite's
colours: lifepoints green while poisoned and orange while diseased, prayer brighter while a prayer or curse
is on, run energy brown while resting. **Show icons** puts the orb's icon on top (it follows the orb: a
poisoned heart), **Show counters** the value, and **Show restores** what the food or potion under the mouse
in the backpack would restore (lifepoints from the Void server's food, prayer, super restore, energy,
Saradomin brew, Guthix rest, recover special and summoning potions by their 2011 effects). **Hide after
combat delay** hides the bars that many game ticks after you last dealt with something you can attack (0:
always shown); **Bar width** applies on the resizable screen. On the fixed screen the bars stand on the
stone columns either side of the tabs, as RuneLite's; the resizable side panel reaches the screen's right
edge, so there both bars stand left of its box, as tall as it, 4 pixels apart (RuneLite's resizable box layout). Not here from RuneLite:
Warmth (Summoning takes its place), venom, stamina and parasites.

**Timers and Buffs** is RuneLite's, for what 634 has: antifire and super antifire, overload, antipoison,
the juju potions, the cannon, magic imbue, charge, vengeance and vengeance active, teleblock and freezes
(the ice spells, bind, snare and entangle), each an infobox with the potion's or the spell's icon counting
down, each with its own switch. The Void server sends only the poison variable (an antipoison's protection
and its time left), so the rest start from its chat lines ("You drink the antifire (4)."), the player's
animations and graphics and the "Taste vengeance!" overhead, and are timed in the client: they go at logout
(the server starts its own again without a word), and teleblock, freezes, overload and vengeance active go
on death, as RuneLite's. A freeze lasts as RuneLite's (half under Protect from Magic or Deflect Magic) and
goes once you move; Charge counts only when cast with nothing targeted (the god spells share its
animation). Not here from RuneLite: Old School's potions, items and spells (stamina, divine potions,
imbued heart, Arceuus spells, raids supplies and so on).

**Tile Indicators** is RuneLite's. The tile you are walking to (the minimap's flag), the tile under the
mouse and your true tile (where the server has you, ahead of your model while you walk), each with its
own switch, border colour, fill colour and border width, in RuneLite's defaults: the destination on, grey,
the other two off. Not here from RuneLite: its cutscene check (634 has no such flag).

**XP Globes** is RuneLite's. A skill that gains experience gets a globe at the top centre of the game view:
its icon on a disc, ringed by its progress to the next level in the skill's colour (or one of your own).
Hovering one darkens it, shows the percentage and a tooltip with its level, current experience, actions
left, experience left, experience an hour and the time to the level, each line its own setting; the rates
are the XP Tracker's while it is on, else the plugin's own. Up to five globes, in skill order, each gone
after **Duration of orbs** seconds; **Hide maxed skills** (99, Dungeoneering 120), **Show virtual level**,
the colours, the arc's width and the globes' size are settings. Shift and right-click the globes for
Configure and Flip (down instead of across).

**Barrows Brothers** is RuneLite's, for the Barrows as the Void server runs it. On the hills each brother's
mound is lettered on the minimap (cyan, red once he is slain; both colours are settings). In the crypts a
panel at the top left takes the place of the game's "Brothers slain" and "Creatures slain" lines: each
brother with a tick or a cross, and the chest's reward potential. Void rolls the chest's runes on the sum of
the combat levels killed in the run (at most 1012) and never sends it, so the plugin adds up each kill's
level itself and keeps the sum between sessions; it is exact for a run played on this client. The
potential is green from the blood runes' roll to the bolt racks', white under the death runes', else
yellow, as in RuneLite. The tunnels' prayer drain (every 18 seconds on Void) gets a timer, set again by
each drain it sees; the puzzle door's answer is outlined in green; and the chest's loot, as the backpack
gains it (634 has no reward window), is priced in the chat at Grand Exchange prices. Each part has its
switch.

**Attack Styles** is RuneLite's, off until switched on. The attack style in use shows above the chat
box: always, or only while it trains a skill you asked to be warned about (Attack, Strength, Defence,
Ranged or Magic), when it is red. A change to a warned style is said in the chat and, with **Warning
notification**, as a desktop notification; **Remove warned styles** hides those styles' buttons in the
combat tab, and **Hide auto retaliate** its button. The style comes from the server's style index and
the worn weapon's type (the cache's param 686, with the styles each type has on Void); autocasting is
Casting, or Defensive Casting. Not here from RuneLite: hiding the staff's autocast buttons (634 keeps
them in the spellbook, not the combat tab).

**Cannon** is RuneLite's, for the dwarf multicannon. The cannonballs left show over your cannon (green
over 15, orange over 5, else red) and, with **Show cannonball infobox**, in an infobox; you get a
notification when it runs out, and once when it falls to **Low warning threshold**. **Show double hit
spots** marks the tiles where it hits twice, and while you carry all four parts some common cannon spots
are marked (RuneLite's, where the monsters are on Void). Void never sends the ammo, so the plugin counts
it: what "You load the cannon with N cannon balls." adds, less each cannonball it sees leave the cannon;
the cannon is found beside you as you set it up. After a new login the count of a cannon still standing
is not known. Not here from RuneLite: granite cannonballs (not on Void).

**Anti Drag** is RuneLite's, off until switched on. An item in the backpack, the bank, the bank's
backpack or the deposit box only starts to drag once the mouse has been held on it for **Drag delay**
client cycles (30, a game tick's worth; the game's own is 5, or 10 in the bank's backpack): only while
Shift is held with **On shift only**, else always but not while Control is held with **Disable on
control pressed**. The game's delay comes back when the key is let go or the window loses focus.

**Emojis** is RuneLite's, off until switched on. In public chat, private chat and the friends chat, and
over players' heads, a word that is one of RuneLite's 67 emoticons (`:)`, `^_^`, `<3`, `:D` ...) is
shown as the emoji, a Twemoji picture added to the chat icons. Only you see them: the line sent to the
server is unchanged. Not here from RuneLite: its `:name:` shortcodes (they need its downloaded set of
every emoji), and the browser build, which has no pictures for them.

**Mouse Tooltips** is RuneLite's (its Mouse Highlighting plugin). What a left click will do, the menu's
top entry ("Attack Goblin (level: 2)"), follows the mouse as a tooltip; Walk here, Cancel and Continue
are left out, and **Interface tooltips**, **Chatbox tooltips** and **Disable spellbook tooltips** decide
the interfaces' entries. Not here from RuneLite: its check for the game's own tooltip (634 draws its
interface tooltips with scripts, so both can show at once).

**Key Remapping** is RuneLite's, off until switched on. The chat reads "Press Enter to Chat..." and
takes no typing until Enter (or `/` or `:`) is pressed, so keys are free: W, A, S and D (or keys of your
choosing) turn the camera as the arrow keys do, and with **Remap F-keys** 1 to 0, - and = stand for F1
to F12, and a key of your choosing for Escape; a key can stand for Space in a dialogue (to continue) and
for Control. Enter sends the line and locks the chat again, as do Escape (which also drops what was
typed) and Backspace on an empty line. In 634, Enter on an empty line opens Quick Chat; with the plugin
on, that Enter unlocks or locks the chat instead.

**Custom Cursor** is RuneLite's, off until switched on. The mouse cursor over the game is one of
RuneLite's pictures (RS3 gold or silver, a dragon dagger, a poisoned one, a trout, a dragon scimitar),
the icon of the weapon you wear, or a picture of your own: `cursor.png` in the `.634-client` folder. Not
here from RuneLite: the browser build, which carries neither the pictures nor your file.

**Item Stats** is RuneLite's. Hovering a food or a potion in the backpack, the worn equipment or (with
**Show stats in bank**) the bank shows what it would do to each stat now: the change, with **Show
theoretical** what it could do, and with **Show absolute** the level after, coloured by whether it is
better, capped, unchanged or worse (five colours of your own); a potion that lasts shows its duration.
Hovering equipment shows its bonuses against what you wear in its slot (a two-handed weapon against your
weapon and shield), and any item its weight. The numbers are the Void server's: food restores lifepoints
(a range shows its least and most), potions boost, restore and drain as the server does, and the slots,
two-handedness and weights are its own (the 634 cache has none of them). Not here from RuneLite: its
Grand Exchange item panel, which builds interface components the plugin API cannot.

## Writing a plugin

Start from [`plugin-template/`](../plugin-template): copy the folder, and change the manifest in
`build.gradle.kts` and the class in `src/main/kotlin`. In short, a plugin is:

1. A class with a public constructor without arguments that implements `Plugin`:

   ```kotlin
   class MyPlugin : Plugin {
       override fun start(context: PluginContext) {
           context.addOverlay { g, view -> g.text(view.x + 8, view.y + 20, "Hello", 0xFFFFFF00.toInt(), 0xFF000000.toInt()) }
           context.registerCommand("hello") { _, args -> context.client().chatMessage("Hello, " + args.joinToString(" ")) }
           context.events().subscribe(Events.STAT_CHANGED) { event -> /* ... */ }
       }
   }
   ```

   Java works just as well: the API is interfaces and plain classes, and `stop()` has a default.

2. A jar whose manifest names it:

   | Attribute | |
   |---|---|
   | `Plugin-Class` | The class, with its package. Needed. |
   | `Plugin-Id` | 2 to 32 of `a-z`, `0-9` and `-`, starting with a letter: the name in commands and files. Needed. |
   | `Plugin-Name` | The name the player sees. |
   | `Plugin-Version` | The plugin's version, any text. |
   | `Plugin-Api-Version` | The `PluginApi.VERSION` it was written against (1 if missing). The client refuses a plugin that needs a newer API than it has. |
   | `Plugin-Author` | Who wrote it: shown in the Plugins window and by `::plugins`. |
   | `Plugin-Description` | What it does: the Plugins window shows it when a row is hovered. |
   | `Plugin-Icon` | The name of a PNG entry in the jar (`icon.png`, `images/icon.png`): the plugin's icon in the Plugins window, shown at 25x25 (any size up to 64x64 is scaled). A missing or unreadable one is noted on stderr and the plugin loads without it. |

   One plugin a jar, and the jar is all of it: one that carries the client's classes (`com/jagex/...`)
   or the Kotlin standard library (`kotlin/...`) is refused, since the client has them, and so is one
   whose manifest has a `Class-Path` or `Multi-Release: true`, which would load classes from outside
   that check. The check is on the jar's entry names; it is a guard against a shaded build, not a sandbox.

### What happens when

- `start(context)` runs on the game thread once the cache's types, the interfaces and the player's
  variables exist, before the login screen. The toolkit's fonts may not exist yet
  (`Graphics.fontReady()`); the first `ToolkitChanged` event says when they do, and so does every
  change of graphics mode, after which every font and sprite is new.
- Every callback runs on the game thread: listeners, overlays, commands, menu actions. Keep the
  per-frame ones quick, and the "2010 idiom" of the client's own features suits them: plain arrays
  and loops, no allocation in a loop that runs every frame.
- The event objects of the events that happen every frame or cycle (`ClientTick`, `GameTick`,
  `ViewportDraw`, `FrameDraw`, `KeyPressed`, `KeyTyped`, `KeyReleased`, `MouseClicked`, `MouseWheel`,
  `MenuEntryAdded`, `MenuOptionClicked`, `MenuBuilt`, `ConsoleCommand`) are reused: read them in the
  listener, keep nothing. The actors, items and components the other events carry are the client's
  own objects, alive while they are in the scene.
- Another thread may call one thing only: `context.invokeLater(task)`. Everything else is for the
  game thread.
- `stop()` runs when the player turns the plugin off or the client quits. Everything the plugin
  registered through its context is removed for it afterwards, and the context registers nothing
  more: a listener or thread the plugin forgot cannot bring it back.
- A throw anywhere disables the plugin ([above](#installing-plugins)).

### The pieces

| Through the context | |
|---|---|
| `client()` | `ClientApi`: the game's state, world-to-screen projection, chat and console output, `addMenuEntry`; `isFocused()`, `notify(text)`, a notification on the desktop (notify-send on Linux, a balloon from the system tray elsewhere, a browser notification), and `requestFocus()`, the window to the front, since API 8. |
| `events()` | `subscribe(Events.X, listener)` and `unsubscribe`. |
| `config()` | `PluginConfig`: `get`/`set` (and `Int`/`Boolean`), `save()`. |
| `addOverlay(overlay)` | Draws on the game view every frame, after the scene and the client's own overlays, before the interfaces. The minimap, the orbs and the chat box cover what is under them; the XP drops on the right start left of the minimap (in the resizable modes) for that reason. |
| `registerCommand(name, handler)` | `::name` in the console, after the client's own commands have had their turn; a plugin's command is not sent to the server, and Tab (completion) never runs it. False when the name is taken or the client's own. |
| `registerMenuAction(action)` | A click type to give `client().addMenuEntry(label, clickType)` from a `MenuEntryAdded` listener. The action gets the target kind, the scene tile (an npc's or player's south-west tile) and the id (npc index, player index, item id, location id). |
| `log(message)` | Standard error, with the plugin id. |
| `invokeLater(task)` | Runs the task on the game thread at the next client cycle; the one call allowed from another thread. |
| `schedule(ticks, task)` | Runs the task on the game thread that many game ticks (600 ms) later. `GameTick` itself is a client cycle of 20 ms while logged in, 30 to a tick of the server. |
| `startThread(name, task)` | Runs the task on a thread of its own, for a web request or a file the game must not wait for; it reads nothing and hands its result to `invokeLater`. |
| `settings()` | `SettingsSchema`: `toggle(key, label, default)`, `number(key, label, default, min, max, step)`, `choice(key, label, default, options)`, `text(key, label, default, maxLength)`, declared in `start`. The plugin manager shows them, writes the value into `config()` under the key and saves; the plugin reads it back on `SettingChanged`. |

`Graphics` (in overlays and the draw events) draws lines, rectangles, circles, polygons, text in
the client's three fonts (`font(Font.BOLD)`), the cache's sprites (`sprite(id, x, y)`) and the
icons of items (`itemIcon(id, amount, x, y)`, 36 by 32, as an inventory shows them), in screen
pixels, with ARGB colours whose alpha blends, within a `clip` of its own if it wants one; the clip
and the font go back to the client's after each overlay. World to screen: `ClientApi.projectTile`,
`projectPoint` (scene units, 512 a tile) and `projectActor` write the view-relative point into an
`IntArray(2)` and say whether it is on screen, and `tilePolygon` gives a tile's four corners for
`polygon` and `fillPolygon` (view-relative too: add the viewport's `x` and `y` before drawing, as the recipes do);
`projectMinimap` places a tile on the minimap, in screen coordinates already. Text's `y` is its
baseline, and `fontHeight()` is the font's line height: a panel's first baseline sits one of them
below its top.

## Cookbook

Two plugins of the kind players ask for, each a few dozen lines on the public API, in the template
([`plugin-template/src/main/kotlin/recipes`](../plugin-template/src/main/kotlin/recipes));
`./gradlew -p plugin-template recipeJars` builds one jar each in `plugin-template/build/libs/recipes`,
ready for the plugins folder. What each one leans on:

| Plugin | Lines | The API it uses |
|---|---|---|
| `AttackTimer`, attack and idle timers under the player, timed by the equipped weapon | 96 | `itemContainer(94)` slot 3 is the weapon and `item(id).intParam(14, 4)` its speed in ticks, read again on `ItemContainerChanged`; `AnimationChanged` for the local player starts the cooldown, unless `HitsplatApplied` on the player in the same cycle while `interacting()` is null marks it as the block animation; `GameTick` counts it and the idle ticks; `projectActor` places the bar; `playSound` and `chatMessage` warn. |
| `ItemTags`, a colour on an item from its right-click menu, drawn around it in the backpack and the bank | 59 | `MenuEntryAdded` for `MenuTarget.WIDGET` with `addMenuEntry`; a `MenuAction` whose id is the item; `widget(149, 0)` and `widget(762, 93)`, drawn at `FRAME_DRAW` (over the interfaces), their `child(i)`, `itemId()`, `screenX()`, `drawRect`. |

The shape they share: subscribe in `start`, keep state in fields and `config()`, draw in one
overlay, and allocate outside the per-frame paths (the `IntArray`s for projection are fields).

### Overlays and infoboxes

An `Overlay` draws where it likes, every frame: for things in the scene (a tile, an npc's outline).
For a box of text or numbers, extend **`PanelOverlay`** instead (API 12), RuneLite's `Overlay` with a
position: the client places it in its corner (`position`, by default `TOP_LEFT`), under the panels of
that corner with a higher `priority`, or where the player dragged it, and calls `render(g, view)` with
the drawing moved so that the panel's top-left corner is (0, 0); `render` returns the size it drew, or
null for nothing this frame. Give it a `name` that stays the same between versions: its place is kept
under the plugin's id and that name. `resizable` panels read `preferredWidth`; `addMenuEntry` adds a
Shift-right-click entry; `backgroundColour` is the one the player chose.

**`OverlayPanel`** is a `PanelOverlay` that is a `PanelComponent`, as RuneLite's: add this frame's
lines in `render` and return `super.render(g, view)`.

```kotlin
class SpecPanel(private val client: ClientApi) : OverlayPanel("SpecPanel") {
	override fun render(g: Graphics, view: Viewport): Size? {
		panelComponent.addTitle("Special attack", 0xFFFF981F.toInt())
		panelComponent.addLine("Energy", -1, (client.varp(300) / 10).toString() + "%", -1)
		return super.render(g, view)
	}
}
// in start: context.addOverlay(SpecPanel(context.client()))
```

An **`InfoBox`** is a small square in the infobox panel, shared by every plugin: give it a cache
`spriteId` or an `image` (`ClientApi.loadImage` reads a PNG from the plugin's jar), and override
`text()`, `textColour()` and, to hide it for now, `render()`. `Counter` shows a number, `Timer` a
countdown that removes itself, and `LoopTimer` (API 17) a countdown that starts again each period, with
`reset()` to start it again from now. Add one with `context.addInfoBox(box)`; boxes with the same `name` move
together when the player detaches one. `ClientApi.addTooltip(text)` while drawing shows a tooltip by
the mouse that frame.

## The API

The API is the package `com.jagex.game.runetek5.plugin.api`, published with every client release as
`runetek5-plugin-api-<version>.jar` with a `-sources.jar` beside it; its KDoc is the reference. The jar
is also inside the client jar, which is what runs the plugin.

`PluginApi.VERSION` is the API's version, and goes up with every release that adds to the API (its
KDoc lists what each version brought). Nothing is removed or changed in a way that breaks an older
plugin: one built against version N runs on every client whose API is N or newer, and a client
refuses a plugin that needs a newer API than its own, with a message to update the client, rather
than failing inside it when the plugin reaches for what is not there.

## Building

The template's `build.gradle.kts` is the whole of it:

- `compileOnly(files(apiJar))`, where the jar comes from a release or from `./gradlew releaseApi` in
  the client repository (`build/release`); `-PapiJar=<path>` says where (absolute, or relative to
  the template folder: Gradle resolves it against the project, not against where you ran it).
- `compileOnly(kotlin("stdlib"))` and `kotlin.stdlib.default.dependency=false`: the client has the
  standard library, and a plugin that bundles one is refused. The plugin's Kotlin version must not
  be newer than the client's (2.2.20 at the time of writing).
- A plain `jar` task with the manifest attributes. No shading.

To try it from the client repository: `./gradlew runClient -Pclient.plugins=$PWD/plugin-template/build/libs`
(against a Void server on this computer), or the launcher's extra argument `--plugins <dir>`.
`--plugins` is kept across the client's own restart on Linux (it restarts itself once to set the
native library path).

Plugins are not loaded in the smoke test (`-Pclient.test=smoke`) unless `client.plugins` names a
folder, so the test sees the same client every time. `-Pclient.test.menuCheck=true` adds a step for
plugins that edit menus: it builds a banker's menu and runs the menu tick on it, then builds the
menu of the backpack's first item, clicks the first plugin entry on it and builds it again, and
prints what changed (`SMOKE menu check: ...`), with no mouse involved.

## Trouble

| | |
|---|---|
| `::plugins` says `failed: the manifest has no Plugin-Class` | The jar's manifest lacks the attributes; see the template's `tasks.jar`. |
| `failed: it needs plugin API version N` | The plugin was built against a newer client; update the client. |
| `failed: the jar carries kotlin/...` | The build bundled the standard library or the API; make both `compileOnly`. |
| `failed: fixture.X does not implement ...Plugin` | `Plugin-Class` names the wrong class. |
| `Plugin X was disabled: java.lang.NoSuchMethodError` | The plugin was compiled against another Kotlin version than the client's, or another API version. |
| Nothing draws | `Graphics.fontReady()` is false until the first `ToolkitChanged`; or the drawing is under the minimap or chat box. |
| `plugins.properties`, `plugin-config` not written | No home folder, or a smoke-test run (`client.test`). |

## How it works inside the client

For the client's own developers. The runtime is `com.jagex.game.runetek5.plugin` in `commonMain`,
so it is common code (no `java.*`; the browser build compiles it, though a browser loads only the
built-in plugins):

- `PluginRuntime` is what the game calls. Each hook in the original code (every one commented "Not
  part of the original client", as [FEATURES.md](FEATURES.md) asks) is one line: `drawViewport` from
  `ClientState.drawGameArea`, `statChanged` and `containerChanged` from `PacketDecoder`, `gameTick`
  and `varpChanged` from `GameTick`, `clientTick`, `keyPressed`, `mouseClicked`, `drawFrame` and
  `stop` from `client`, `stepChanged` from `MainLogicStepTransition`, `toolkitChanged` from
  `ToolkitRebuild`, `chatMessage` from `ChatLineWriter`, `consoleCommand` from
  `ClientCommandProcessor` (after the client's own commands, before the line goes to the server),
  `menuClicked` from `MiniMenuActionExecutor`, `menuTarget` from
  `ViewportMiniMenuPicking`, `MiniMenuBuilder` and `MiniMenu`, and `start` from `LoadingStageWork`
  at stage 15.
- `EventBusImpl` dispatches from arrays, allocating nothing; a listener that throws is reported to
  the runtime, which disables its plugin and removes everything it registered.
- `ClientApiImpl` is the façade over the client's statics; `PathingEntity`, `NPCEntity`,
  `PlayerEntity`, `ItemContainer` and `Component` implement the API's `Actor`, `Npc`, `Player`,
  `ItemContainer` and `Widget` themselves, so the API hands out the client's objects with no wrapping.
- `ToolkitGraphics` draws through the active toolkit and `Renderers.FONT_RENDERER`.
- `OverlayLayout` is RuneLite's OverlayManager and OverlayRenderer for the `PanelOverlay`s: it lays them
  out after the plain overlays in `drawViewport` (each corner a `SnapCorner`), runs the drag key's
  managing mode from `client` (`cycle` before the mouse's presses, `mousePressed` before the plugins'
  `MouseClicked`, so a press it takes never reaches `MouseManager.MOUSE_MOVEMENTS`), and adds the Shift
  menu from `MiniMenuInputTick` with the click type `MiniMenuAction.OVERLAY_MENU`. `InfoBoxes` is the
  infobox manager (each group one `PanelOverlay`), `Tooltips` draws at `drawFrame`. The HUD a corner
  measures from is the view less the resizable screen's minimap frame and tabs.
- Plugins' menu entries take the click types `MiniMenuAction.PLUGIN_FIRST` to `PLUGIN_LAST`; the
  target kind rides in the high half of the entry's `param0`, the tile in `param1` and the low half.
- `expect fun discoverPlugins()` is the platform seam: the JVM's actual (`plugin/loader`) reads the
  jars in the plugins folder, one `URLClassLoader` each with the client as parent; the browser's
  returns nothing. `BuiltinPlugins` lists the plugins compiled in, which every platform runs.
- The API is the Gradle subproject `api/`, a Kotlin Multiplatform library with no dependencies that
  the client's `commonMain` depends on, so API code cannot reach into the client. `releaseApi`
  copies its jar beside the release jar.

A new extra for the client should be a built-in plugin on this API where the API suffices, as
`XpDrops` is; where it does not, extend the API (additively, raising `PluginApi.VERSION`) rather
than adding another one-off hook.

## What comes next

Not built yet, in the order it is likely to come: a Plugins page in Graphics Options to turn plugins
on and off and edit their settings; a plugin hub in the Void launcher (a signed list of plugins to
install and update, verified by SHA-256); plugins in the browser client; dependencies between
plugins and reloading one without a restart.
