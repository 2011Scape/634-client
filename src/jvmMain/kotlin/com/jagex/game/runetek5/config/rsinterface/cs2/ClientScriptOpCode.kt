package com.jagex.game.runetek5.config.rsinterface.cs2

object ClientScriptOpCode {

	const val PUSH_CONSTANT_INT: Int = 0

	const val PUSH_VAR: Int = 1

	const val POP_VAR: Int = 2

	const val PUSH_CONSTANT_STRING: Int = 3

	const val BRANCH: Int = 6

	const val BRANCH_NOT: Int = 7

	const val BRANCH_EQUALS: Int = 8

	const val BRANCH_LESS_THAN: Int = 9

	const val BRANCH_GREATER_THAN: Int = 10

	const val RETURN: Int = 21

	const val POP_VARBIT: Int = 25

	const val PUSH_VARBIT: Int = 27

	const val BRANCH_LESS_THAN_OR_EQUALS: Int = 31

	const val BRANCH_GREATER_THAN_OR_EQUALS: Int = 32

	const val PUSH_INT_LOCAL: Int = 33

	const val POP_INT_LOCAL: Int = 34

	const val PUSH_STRING_LOCAL: Int = 35

	const val POP_STRING_LOCAL: Int = 36

	const val JOIN_STRING: Int = 37

	const val POP_INT_DISCARD: Int = 38

	const val POP_STRING_DISCARD: Int = 39

	const val GOSUB_WITH_PARAMS: Int = 40

	const val PUSH_VARC: Int = 42

	const val POP_VARC: Int = 43

	const val DEFINE_ARRAY: Int = 44

	const val PUSH_ARRAY_INT: Int = 45

	const val POP_ARRAY_INT: Int = 46

	const val PUSH_VARCSTR: Int = 47

	const val POP_VARCSTR: Int = 48

	const val SWITCH: Int = 51

	const val BRANCH_IF_TRUE: Int = 86

	const val CC_FIND: Int = 200

	const val IF_FIND: Int = 201

	const val IF_SENDTOFRONT: Int = 202

	const val IF_SENDTOBACK: Int = 203

	const val BASEIDKIT: Int = 403

	const val BASECOLOUR: Int = 404

	const val SETGENDER: Int = 410

	const val SETOBJ: Int = 411

	const val CC_IF_SETSCROLLPOS: Int = 1100

	const val CC_IF_SETCOLOUR: Int = 1101

	const val CC_IF_SETFILL: Int = 1102

	const val CC_IF_SETTTRANS: Int = 1103

	const val CC_IF_SETLINEWID: Int = 1104

	const val CC_IF_SETGRAPHIC: Int = 1105

	const val CC_IF_SET2DANGLE: Int = 1106

	const val CC_IF_SETTILING: Int = 1107

	const val CC_IF_SETMODEL: Int = 1108

	const val CC_IF_SETMODELANGLE: Int = 1109

	const val CC_IF_SETMODELANIM: Int = 1110

	const val CC_IF_SETMODELORTHOG: Int = 1111

	const val CC_IF_SETTEXT: Int = 1112

	const val CC_IF_SETTEXTFONT: Int = 1113

	const val CC_IF_SETTEXTALIGN: Int = 1114

	const val CC_IF_SETTEXTSHADOW: Int = 1115

	const val CC_IF_SETOUTLINE: Int = 1116

	const val CC_IF_SETGRAPHICSHADOW: Int = 1117

	const val CC_IF_SETHFLIP: Int = 1118

	const val CC_IF_SETVFLIP: Int = 1119

	const val CC_IF_SETSCROLLSIZE: Int = 1120

	const val CC_IF_SETALPHA: Int = 1122

	const val CC_IF_SETMODELZOOM: Int = 1123

	const val CC_IF_SETLINEDIRECTION: Int = 1124

	const val CC_IF_SETMODELORIGIN: Int = 1125

	const val CC_IF_SETMAXLINES: Int = 1126

	const val CC_IF_SETPARAM_INT: Int = 1127

	const val CC_IF_SETPARAM_STRING: Int = 1128

	const val CC_IF_SETOP: Int = 1300

	const val CC_IF_SETDRAGGABLE: Int = 1301

	const val CC_IF_SETDRAGRENDERBEHAVIOUR: Int = 1302

	const val CC_IF_SETDRAGDEADZONE: Int = 1303

	const val CC_IF_SETDRAGDEADTIME: Int = 1304

	const val CC_IF_SETOPBASE: Int = 1305

	const val CC_IF_SETTARGETVERB: Int = 1306

	const val CC_IF_CLEAROPS: Int = 1307

	const val CC_IF_SETTARGETCURSORS: Int = 1308

	const val CC_IF_SETOPCURSOR: Int = 1309

	const val CC_IF_SETPAUSETEXT: Int = 1310

	const val CC_IF_SETTARGETOPCURSOR: Int = 1311

	const val CC_IF_SETOPCHAR: Int = 1312

	const val CC_IF_SETOPKEY: Int = 1313

	const val CC_IF_SETMOUSEOVERCURSOR: Int = 1314

	const val CC_IF_CLEARSCRIPTHOOKS: Int = 1499

	const val CC_IF_SETONCLICK: Int = 1400

	const val CC_IF_SETONHOLD: Int = 1401

	const val CC_IF_SETONRELEASE: Int = 1402

	const val CC_IF_SETONMOUSEOVER: Int = 1403

	const val CC_IF_SETONMOUSELEAVE: Int = 1404

	const val CC_IF_SETONDRAG: Int = 1405

	const val CC_IF_SETONTARGETLEAVE: Int = 1406

	const val CC_IF_SETONVARTRANSMIT: Int = 1407

	const val CC_IF_SETONTIMER: Int = 1408

	const val CC_IF_SETONOP: Int = 1409

	const val CC_IF_SETONDRAGCOMPLETE: Int = 1410

	const val CC_IF_SETONCLICKREPEAT: Int = 1411

	const val CC_IF_SETONMOUSEREPEAT: Int = 1412

	const val CC_IF_SETONINVTRANSMIT: Int = 1414

	const val CC_IF_SETONSTATTRANSMIT: Int = 1415

	const val CC_IF_SETONTARGETENTER: Int = 1416

	const val CC_IF_SETONSCROLLWHEEL: Int = 1417

	const val CC_IF_SETONCHATTRANSMIT: Int = 1418

	const val CC_IF_SETONKEY: Int = 1419

	const val CC_IF_SETONFRIENDTRANSMIT: Int = 1420

	const val CC_IF_SETONCLANTRANSMIT: Int = 1421

	const val CC_IF_SETONMISCTRANSMIT: Int = 1422

	const val CC_IF_SETONDIALOGABORT: Int = 1423

	const val CC_IF_SETONSUBCHANGE: Int = 1424

	const val CC_IF_SETONSTOCKTRANSMIT: Int = 1425

	const val CC_IF_SETONCAMFINISHED: Int = 1426

	const val CC_IF_SETONRESIZE: Int = 1427

	const val CC_IF_SETONVARCTRANSMIT: Int = 1428

	const val CC_IF_SETONVARCSTRTRANSMIT: Int = 1429

	const val CC_IF_SETONOPT: Int = 1430

	const val CC_GETX: Int = 1500

	const val CC_GETY: Int = 1501

	const val CC_GETWIDTH: Int = 1502

	const val CC_GETHEIGHT: Int = 1503

	const val CC_GETHIDE: Int = 1504

	const val CC_GETLAYER: Int = 1505

	const val CC_GETPARENTLAYER: Int = 1506

	const val CC_GETSCROLLX: Int = 1600

	const val CC_GETSCROLLY: Int = 1601

	const val CC_GETTEXT: Int = 1602

	const val CC_GETSCROLLWIDTH: Int = 1603

	const val CC_GETSCROLLHEIGHT: Int = 1604

	const val CC_GETMODELZOOM: Int = 1605

	const val CC_GETMODELANGLE_X: Int = 1606

	const val CC_GETMODELANGLE_Z: Int = 1607

	const val CC_GETMODELANGLE_Y: Int = 1608

	const val CC_GETTRANS: Int = 1609

	const val CC_GETMODELXOF: Int = 1610

	const val CC_GETMODELYOF: Int = 1611

	const val CC_GETGRAPHIC: Int = 1612

	const val CC_PARAM: Int = 1613

	const val CC_GET2DANGLE: Int = 1614

	const val CC_GETMODEL: Int = 2614

	const val CC_GETINVOBJECT: Int = 1700

	const val CC_GETINVCOUNT: Int = 1701

	const val CC_GETID: Int = 1702

	const val CC_GETTARGETMASK: Int = 1800

	const val CC_GETOP: Int = 1801

	const val CC_GETOPBASE: Int = 1802

	const val CC_IF_CALLONRESIZE: Int = 1927

	const val IF_GETX: Int = 2500

	const val IF_GETY: Int = 2501

	const val IF_GETWIDTH: Int = 2502

	const val IF_GETHEIGHT: Int = 2503

	const val IF_GETHIDE: Int = 2504

	const val IF_GETLAYER: Int = 2505

	const val IF_GETSCROLLX: Int = 2600

	const val IF_GETSCROLLY: Int = 2601

	const val IF_GETTEXT: Int = 2602

	const val IF_GETSCROLLWIDTH: Int = 2603

	const val IF_GETSCROLLHEIGHT: Int = 2604

	const val IF_GETMODELZOOM: Int = 2605

	const val IF_GETMODELANGLE_X: Int = 2606

	const val IF_GETMODELANGLE_Z: Int = 2607

	const val IF_GETMODELANGLE_Y: Int = 2608

	const val IF_GETTRANS: Int = 2609

	const val IF_GETMODELXOF: Int = 2610

	const val IF_GETMODELYOF: Int = 2611

	const val IF_GETGRAPHIC: Int = 2612

	const val IF_GET2DANGLE: Int = 2613

	const val IF_GETMODEL: Int = 2614

	const val IF_GETINVOBJECT: Int = 2700

	const val IF_GETINVCOUNT: Int = 2701

	const val IF_HASSUB: Int = 2702

	const val IF_GETNEXTSUBID: Int = 2703

	const val IF_HASSUBMODAL: Int = 2704

	const val IF_HASSUBOVERLAY: Int = 2705

	const val IF_GETTARGETMASK: Int = 2800

	const val IF_GETOP: Int = 2801

	const val IF_GETOPBASE: Int = 2802

	const val MES: Int = 3100

	const val IF_CLOSE: Int = 3103

	const val RESUME_COUNTDIALOG: Int = 3104

	const val RESUME_NAMEDIALOG: Int = 3105

	const val RESUME_STRINGDIALOG: Int = 3106

	const val OPPLAYER: Int = 3107

	const val IF_DRAGPICKUP: Int = 3108

	const val CC_DRAGPICKUP: Int = 3109

	const val RESUME_OBJDIALOG: Int = 3110

	const val IF_OPENSUBCLIENT: Int = 3111

	const val IF_CLOSESUBCLIENT: Int = 3112

	const val OPPLAYERT: Int = 3113

	const val MES_TYPED: Int = 3114

	const val SETUP_MESSAGEBOX: Int = 3115

	const val SOUND_SYNTH: Int = 3200

	const val SOUND_SONG: Int = 3201

	const val SOUND_JINGLE: Int = 3202

	const val SOUND_SYTHN_VOLUME: Int = 3203

	const val SOUND_SONG_VOLUME: Int = 3204

	const val SOUND_JINGLE_VOLUME: Int = 3205

	const val SOUND_VORBIS_VOLUME: Int = 3206

	const val SOUND_SPEECH_VOLUME: Int = 3207

	const val SOUND_SYNTH_RATE: Int = 3208

	const val SOUND_VORBIS_RATE: Int = 3209

	const val CLIENTCLOCK: Int = 3300

	const val INV_GETOBJ: Int = 3301

	const val INV_GETNUM: Int = 3302

	const val INV_TOTAL: Int = 3303

	const val INV_SIZE: Int = 3304

	const val STAT: Int = 3305

	const val STAT_BASE: Int = 3306

	const val STAT_VISIBLE_XP: Int = 3307

	const val COORD: Int = 3308

	const val COORDX: Int = 3309

	const val COORDY: Int = 3310

	const val COORDZ: Int = 3311

	const val MAP_MEMBERS: Int = 3312

	const val INVOTHER_GETOBJ: Int = 3313

	const val INVOTHER_GETNUM: Int = 3314

	const val INVOTHER_TOTAL: Int = 3315

	const val STAFFMODLEVEL: Int = 3316

	const val REBOOTTIMER: Int = 3317

	const val MAP_WORLD: Int = 3318

	const val RUNENERGY_VISIBLE: Int = 3321

	const val RUNWEIGHT_VISIBLE: Int = 3322

	const val PLAYERMOD: Int = 3323

	const val PLAYERMODLEVEL: Int = 3324

	const val PLAYERMEMBER: Int = 3325

	const val COMLEVEL_ACTIVE: Int = 3326

	const val GENDER: Int = 3327

	const val MAP_QUICKCHAT: Int = 3329

	const val INV_FREESPACE: Int = 3330

	const val INV_TOTALPARAM: Int = 3331

	const val INV_TOTALPARAM_STACK: Int = 3332

	const val MAP_LANG: Int = 3335

	const val MOVE_COORD: Int = 3336

	const val AFFILIATE: Int = 3337

	const val PROFILE_CPU: Int = 3338

	const val PLAYERDEMO: Int = 3339

	const val APPLET_HASFOCUS: Int = 3340

	const val FROMBILLING: Int = 3341

	const val GET_MOUSEX: Int = 3342

	const val GET_MOUSEY: Int = 3343

	const val GET_ACTIVE_MINIMENU_ENTRY: Int = 3344

	const val GET_SECOND_MINIMENU_ENTRY: Int = 3345

	const val GET_MINIMENU_LENGTH: Int = 3346

	const val GET_CURRENTCURSOR: Int = 3347

	const val GET_SELFYANGLE: Int = 3349

	const val GET_MOUSEBUTTONS: Int = 3351

	const val ENUM_STRING: Int = 3400

	const val ENUM: Int = 3408

	const val ENUM_HASOUTPUT: Int = 3409

	const val ENUM_HASOUTPUT_STRING: Int = 3410

	const val ENUM_GETOUTPUT_COUNT: Int = 3411

	const val FRIEND_COUNT: Int = 3600

	const val FRIEND_GETNAME: Int = 3601

	const val FRIEND_GETWORLD: Int = 3602

	const val FRIEND_GETRANK: Int = 3603

	const val FRIEND_SETRANK: Int = 3604

	const val FRIEND_ADD: Int = 3605

	const val FRIEND_DEL: Int = 3606

	const val IGNORE_ADD: Int = 3607

	const val IGNORE_DEL: Int = 3608

	const val FRIEND_TEST: Int = 3609

	const val FRIEND_GETWORLDNAME: Int = 3610

	const val CLAN_GETCHATDISPLAYNAME: Int = 3611

	const val CLAN_GETCHATCOUNT: Int = 3612

	const val CLAN_GETCHATUSERNAME: Int = 3613

	const val CLAN_GETCHATUSERWORLD: Int = 3614

	const val CLAN_GETCHATUSERRANK: Int = 3615

	const val CLAN_GETCHATMINKICK: Int = 3616

	const val CLAN_KICKUSER: Int = 3617

	const val CLAN_GETCHATRANK: Int = 3618

	const val CLAN_JOINCHAT: Int = 3619

	const val CLAN_LEAVECHAT: Int = 3620

	const val IGNORE_COUNT: Int = 3621

	const val IGNORE_GETNAME: Int = 3622

	const val IGNORE_TEXT: Int = 3623

	const val CLAN_ISSELF: Int = 3624

	const val CLAN_GETCHATOWNERNAME: Int = 3625

	const val CLAN_GETCHATUSERWORLDNAME: Int = 3626

	const val FRIEND_SAME_GAME: Int = 3627

	const val FRIEND_GETSLOTFROMNAME: Int = 3628

	const val PLAYERCOUNTRY: Int = 3629

	const val IGNORE_ADD_TEMP: Int = 3630

	const val IGNORE_IS_TEMP: Int = 3631

	const val CLAN_GETCHATUSERNAME_UNFILTERED: Int = 3632

	const val IGNORE_GETNAME_UNFILTERED: Int = 3633

	const val STOCKMARKET_GETOFFERTYPE: Int = 3903

	const val STOCKMARKET_GETOFFERITEM: Int = 3904

	const val STOCKMARKET_GETOFFERPRICE: Int = 3905

	const val STOCKMARKET_GETOFFERCOUNT: Int = 3906

	const val STOCKMARKET_GETOFFERCOMPLETEDCOUNT: Int = 3907

	const val STOCKMARKET_GETOFFERCOMPLETEDGOLD: Int = 3908

	const val STOCKMARKET_ISOFFEREMPTY: Int = 3910

	const val STOCKMARKET_ISOFFERSTABLE: Int = 3911

	const val STOCKMARKET_ISOFFERFINISHED: Int = 3912

	const val STOCKMARKET_ISOFFERADDING: Int = 3913

	const val ADD: Int = 4000

	const val SUB: Int = 4001

	const val MULTIPLY: Int = 4002

	const val DIVIDE: Int = 4003

	const val RANDOM: Int = 4004

	const val RANDOMINC: Int = 4005

	const val INTERPOLATE: Int = 4006

	const val ADDPERCENT: Int = 4007

	const val SETBIT: Int = 4008

	const val CLEARBIT: Int = 4009

	const val TESTBIT: Int = 4010

	const val MODULO: Int = 4011

	const val POW: Int = 4012

	const val INVPOW: Int = 4013

	const val AND: Int = 4014

	const val OR: Int = 4015

	const val MIN: Int = 4016

	const val MAX: Int = 4017

	const val SCALE: Int = 4018

	const val RANDOM_SOUND_PITCH: Int = 4019

	const val APPEND_NUM: Int = 4100

	const val APPEND: Int = 4101

	const val APPEND_SIGNNUM: Int = 4102

	const val LOWERCASE: Int = 4103

	const val FROMDATE: Int = 4104

	const val TEXT_GENDER: Int = 4105

	const val TOSTRING: Int = 4106

	const val COMPARE: Int = 4107

	const val PARAHEIGHT: Int = 4108

	const val ESCAPE: Int = 4111

	const val APPEND_CHAR: Int = 4112

	const val CHAR_ISPRINTABLE: Int = 4113

	const val CHAR_ISALPHANUMERIC: Int = 4114

	const val CHAR_ISALPHA: Int = 4115

	const val CHAR_ISNUMERIC: Int = 4116

	const val STRING_LENGTH: Int = 4117

	const val SUBSTRING: Int = 4118

	const val REMOVETAGS: Int = 4119

	const val STRING_INDEXOF_CHAR: Int = 4120

	const val STRING_INDEXOF_STRING: Int = 4121

	const val CHAR_TOLOWERCASE: Int = 4122

	const val CHAR_TOUPPERCASE: Int = 4123

	const val TOSTRING_LOCALISED: Int = 4124

	const val STRINGWIDTH: Int = 4125

	const val OC_NAME: Int = 4200

	const val OC_OP: Int = 4201

	const val OC_IOP: Int = 4202

	const val OC_COST: Int = 4203

	const val OC_STACKABLE: Int = 4204

	const val OC_CERT: Int = 4205

	const val OC_UNCERT: Int = 4206

	const val OC_MEMBERS: Int = 4207

	const val OC_PARAM: Int = 4208

	const val OC_ICURSOR: Int = 4209

	const val OC_FIND: Int = 4210

	const val OC_FINDNEXT: Int = 4211

	const val OC_FINDRESTART: Int = 4212

	const val OC_MULTISTACKSIZE: Int = 4213

	const val NC_PARAM: Int = 4300

	const val BAS_GETANIM_READY: Int = 4600

	const val CHAT_GETFILTER_PUBLIC: Int = 5000

	const val CHAT_SETFILTER: Int = 5001

	const val CHAT_SENDABUSEREPORT: Int = 5002

	const val CHAT_GETHISTORY_BYTYPEANDLINE: Int = 5003

	const val CHAT_GETHISTORY_BYUID: Int = 5004

	const val CHAT_GETFILTER_PRIVATE: Int = 5005

	const val CHAT_SETMODE: Int = 5006

	const val CHAT_SENDPUBLIC: Int = 5008

	const val CHAT_SENDPRIVATE: Int = 5009

	const val CHAT_GETHISTORYNAME: Int = 5010

	const val CHAT_GETHISTORYCLAN: Int = 5011

	const val CHAT_GETHISTORYPHRASE: Int = 5012

	const val CHAT_PLAYERNAME: Int = 5015

	const val CHATCAT_GETDESC: Int = 5050

	const val CHATCAT_GETSUBCATCOUNT: Int = 5051

	const val CHATCAT_GETSUBCAT: Int = 5052

	const val CHATCAT_GETPHRASECOUNT: Int = 5053

	const val CHATCAT_GETPHRASE: Int = 5054

	const val CHATPHRASE_GETTEXT: Int = 5055

	const val CHATPHRASE_GETAUTORESPONSECOUNT: Int = 5056

	const val CHATPHRASE_GETAUTORESPONSE: Int = 5057

	const val ACTIVECHATPHRASE_PREPARE: Int = 5058

	const val ACTIVECHATPHRASE_SENDPUBLIC: Int = 5059

	const val ACTIVECHATPHRASE_SENDPRIVATE: Int = 5060

	const val ACTIVECHATPHRASE_SENDCLAN: Int = 5061

	const val CHATCAT_GETSUBCATSHORTCUT: Int = 5062

	const val CHATCAT_GETPHRASESHORTCUT: Int = 5063

	const val CHATCAT_FINDSUBCATBYSHORTCUT: Int = 5064

	const val CHATCAT_FINDPHRASEBYSHORTCUT: Int = 5065

	const val CHATPHRASE_GETDYNAMICCOMMANDCOUNT: Int = 5066

	const val CHATPHRASE_GETDYNAMICCOMMAND: Int = 5067

	const val ACTIVECHATPHRASE_SETDYNAMICINT: Int = 5068

	const val ACTIVECHATPHRASE_SETDYNAMICOBJ: Int = 5069

	const val CHATPHRASE_GETDYNAMICCOMMANDPARAM_ENUM: Int = 5070

	const val CHATPHRASE_FIND: Int = 5071

	const val CHATPHRASE_FINDNEXT: Int = 5072

	const val CHATPHRASE_FINDRESTART: Int = 5073

	const val KEYHELD_ALT: Int = 5100

	const val KEYHELD_CTRL: Int = 5101

	const val KEYHELD_SHIFT: Int = 5102

	const val WORLDMAP_SETZOOM: Int = 5200

	const val WORLDMAP_GETZOOM: Int = 5201

	const val WORLDMAP_SETMAP: Int = 5205

	const val WORLDMAP_GETMAP: Int = 5206

	const val ORLDMAP_GETMAPNAME: Int = 5207

	const val WORLDMAP_GETSIZE: Int = 5208

	const val WORLDMAP_GETDISPLAYPOSITION: Int = 5209

	const val WORLDMAP_GETCONFIGORIGIN: Int = 5210

	const val WORLDMAP_GETCONFIGBOUNDS: Int = 5211

	const val WORLDMAP_LISTELEMENT_START: Int = 5212

	const val WORLDMAP_LISTELEMENT_NEXT: Int = 5213

	const val WORLDMAP_JUMPTOSOURCECOORD: Int = 5214

	const val WORLDMAP_COORDINMAP: Int = 5215

	const val WORLDMAP_GETCONFIGZOOM: Int = 5218

	const val WORLDMAP_ISLOADED: Int = 5220

	const val WORLDMAP_JUMPTODISPLAYCOORD: Int = 5221

	const val WORLDMAP_GETSOURCEPOSITION: Int = 5222

	const val WORLDMAP_SETMAP_COORD: Int = 5223

	const val WORLDMAP_GETDISPLAYCOORD: Int = 5224

	const val WORLDMAP_GETSOURCECOORD: Int = 5225

	const val WORLDMAP_FLASHELEMENT: Int = 5226

	const val WORLDMAP_SETMAP_COORD_OVERRIDE: Int = 5227

	const val WORLDMAP_DISABLEELEMENTS: Int = 5228

	const val WORLDMAP_GETDISABLEELEMENTS: Int = 5229

	const val WORLDMAP_FLASHELEMENTCATEGORY: Int = 5230

	const val WORLDMAP_DISABLEELEMENTCATEGORY: Int = 5231

	const val WORLDMAP_GETDISABLEELEMENTCATEGORY: Int = 5232

	const val WORLDMAP_DISABLEELEMENT: Int = 5233

	const val WORLDMAP_GETDISABLEELEMENT: Int = 5234

	const val WORLDMAP_GETCURRENTMAP: Int = 5235

	const val WORLDMAP_FINDNEARESTELEMENT: Int = 5236

	const val WORLDMAP_CLOSEMAP: Int = 5237

	const val FULLSCREEN_ENTER: Int = 5300

	const val FULLSCREEN_EXIT: Int = 5301

	const val FULLSCREEN_MODECOUNT: Int = 5302

	const val FULLSCREEN_GETMODE: Int = 5303

	const val FULLSCREEN_LASTMODE: Int = 5305

	const val GETWINDOWMODE: Int = 5306

	const val SETWINDOWMODE: Int = 5307

	const val GETDEFAULTWINDOWMODE: Int = 5308

	const val SETDEFAULTWINDOWMODE: Int = 5309

	const val OPENURL: Int = 5400

	const val SETCLIENTPALETTE: Int = 5401

	const val SPLINE_NEW: Int = 5405

	const val SPLINE_ADDPOINT: Int = 5406

	const val SPLINE_LENGTH: Int = 5407

	const val QUIT: Int = 5411

	const val LASTLOGIN: Int = 5419

	const val OPENURL_NOLOGIN: Int = 5421

	const val WRITECONSOLE: Int = 5423

	const val FORMATMINIMENU: Int = 5424

	const val DEFAULTMINIMENU: Int = 5425

	const val SETDEFAULTCURSORS: Int = 5426

	const val SETHARDCODEDOPCURSORS: Int = 5427

	const val MINIMENUOPEN: Int = 5428

	const val DOCHEAT: Int = 5429

	const val NOTIFY_ACCOUNTCREATED: Int = 5430

	const val NOTIFY_ACCOUNTCREATESTARTED: Int = 5431

	const val GETCLIPBOARD: Int = 5432

	const val SETSUBMENUMINLENGTH: Int = 5433

	const val CAM_MOVETO: Int = 5500

	const val CAM_MOVEALONG: Int = 5502

	const val CAM_RESET: Int = 5503

	const val CAM_FORCEANGLE: Int = 5504

	const val CAM_GETANGLE_XA: Int = 5505

	const val CAM_GETANGLE_YA: Int = 5506

	const val CAM_INC_Y: Int = 5507

	const val CAM_DEC_Y: Int = 5508

	const val CAM_INC_X: Int = 5509

	const val CAM_DEC_X: Int = 5510

	const val CAM_SMOOTH_RESET: Int = 5512

	const val CAM_FOLLOWCOORD: Int = 5511

	const val CAM_REMOVEROOF: Int = 5517

	const val LOGIN_REQUEST: Int = 5600

	const val LOGIN_CONTINUE: Int = 5601

	const val LOGIN_RESETREPLY: Int = 5602

	const val CREATE_AVAILABLEREQUEST: Int = 5604

	const val CREATE_CONNECTREQUEST: Int = 5605

	const val LOGIN_REPLY: Int = 5607

	const val LOGIN_HOPTIME: Int = 5608

	const val CREATE_REPLY: Int = 5609

	const val LOGIN_DISALLOWRESULT: Int = 5611

	const val LOBBY_ENTERGAME: Int = 5612

	const val LOBBY_ENTERGAME_REPLY: Int = 5613

	const val LOBBY_ENTERLOBBY: Int = 5615

	const val LOBBY_LEAVELOBBY: Int = 5616

	const val LOBBY_ENTERLOBBYREPLY: Int = 5617

	const val USERFLOW_FLAGS: Int = 5624

	const val CREATE_UNDER13: Int = 5625

	const val CREATE_SETUNDER13: Int = 5626

	const val DETAIL_BRIGHTNESS: Int = 6001

	const val DETAIL_REMOVEROOFS_OPTION: Int = 6003

	const val DETAIL_GROUNDDECOR_ON: Int = 6005

	const val DETAIL_IDLEANIMS_MANY: Int = 6007

	const val DETAIL_FLICKERING_ON: Int = 6008

	const val DETAIL_SPOTSHADOWS_ON: Int = 6010

	const val DETAIL_HARDSHADOWS: Int = 6011

	const val DETAIL_LIGHTDETAIL_HIGH: Int = 6012

	const val DETAIL_WATERDETAIL_HIGH: Int = 6014

	const val DETAIL_FOG_ON: Int = 6015

	const val DETAIL_ANTIALIASING_QUALITY: Int = 6016

	const val DETAIL_STEREO: Int = 6017

	const val DETAIL_SOUNDVOL: Int = 6018

	const val DETAIL_MUSICVOL: Int = 6019

	const val DETAIL_BGSOUNDVOL: Int = 6020

	const val DETAIL_REMOVEROOFS_OPTION_OVERRIDE: Int = 6021

	const val DETAIL_PARTICLES: Int = 6023

	const val DETAIL_ANTIALIASING_DEFAULT: Int = 6024

	const val DETAIL_BUILDAREA: Int = 6025

	const val DETAIL_BLOOM: Int = 6027

	const val DETAIL_CUSTOMCURSORS: Int = 6028

	const val DETAIL_IDLEANIMS: Int = 6029

	const val DETAIL_GROUNDBLOUNDING: Int = 6030

	const val DETAIL_TOOLKIT: Int = 6031

	const val DETAIL_TOOLKIT_DEFAULT: Int = 6032

	const val DETAIL_CPUUSAGE: Int = 6033

	const val DETAIL_TEXTURING: Int = 6034

	const val DETAIL_ANIMDETAIL: Int = 6035

	const val DETAIL_MAXSCREENSIZE: Int = 6036

	const val DETAIL_SPEECHVOL: Int = 6037

	const val DETAIL_LOGINVOL: Int = 6038

	const val DETAIL_LOADINGSCREENTYPE: Int = 6039

	const val DETAIL_ORTHOGRAPHIC: Int = 6040

	const val DETAILGET_BRIGHTNESS: Int = 6101

	const val DETAILGET_ANIMDETAIL: Int = 6102

	const val DETAILGET_REMOVEROOFS_OPTIONS: Int = 6103

	const val DETAILGET_GROUNDDECOR_ON: Int = 6105

	const val DETAILGET_IDLEANIMS_MANY: Int = 6107

	const val DETAILGET_FLICKERING_ON: Int = 6108

	const val DETAILGET_SPOTSHADOWS: Int = 6110

	const val DETAILGET_HARDSHADOWS: Int = 6111

	const val DETAILGET_LIGHTDETAIL_HIGH: Int = 6112

	const val DETAILGET_WATERDETAIL_HIGH: Int = 6114

	const val DETAILGET_FOG_ON: Int = 6115

	const val DETAILGET_ANTIALIASING_QUALITY: Int = 6116

	const val DETAILGET_STEREO: Int = 6117

	const val DETAILGET_SOUNDVOL: Int = 6118

	const val DETAILGET_MUSICVOL: Int = 6119

	const val DETAILGET_BGSOUNDVOL: Int = 6120

	const val DETAILGET_PARTICLES: Int = 6123

	const val DETAILGET_ANTIALIASING: Int = 6124

	const val DETAILGET_BUILDAREA: Int = 6125

	const val DETAILGET_BLOOM: Int = 6127

	const val DETAILGET_CUSTOMCURSORS: Int = 6128

	const val DETAILGET_IDLEANIMS: Int = 6129

	const val DETAILGET_GROUNDBLENDING: Int = 6130

	const val DETAILGET_TOOLKIT: Int = 6131

	const val DETAILGET_TOOLKIT_DEFAULT: Int = 6132

	const val DETAILGET_CPUUSAGE: Int = 6135

	const val DETAILGET_TEXTURING: Int = 6136

	const val DETAILGET_PERFORMANCE_METRIC: Int = 6138

	const val DETAILGET_MAXSCREENSIZE: Int = 6139

	const val DETAILGET_SPEECHVOL: Int = 6142

	const val DETAILGET_LOGINVOL: Int = 6143

	const val DETAILGET_SAFEMODE: Int = 6144

	const val DETAILGET_LOADINGSCREENTYPE: Int = 6145

	const val DETAILGET_ORTHOGRAPHIC: Int = 6146

	const val DETAILGET_CANCHOOSESAFEMODE: Int = 6147

	const val DETAILGET_CHOSESAFEMODE: Int = 6148

	const val VIEWPORT_SETFOV: Int = 6200

	const val VIEWPORT_SETZOOM: Int = 6201

	const val VIEWPORT_CLAMPFOV: Int = 6202

	const val VIEWPORT_GETEFFECTIVESIZE: Int = 6203

	const val VIEWPORT_GETZOOM: Int = 6204

	const val VIEWPORT_GETFOV: Int = 6205

	const val DATE_MINUTES: Int = 6300

	const val DATE_RUNEDAY: Int = 6301

	const val DATE_RUNEDAY_FROMDATE: Int = 6302

	const val DATE_YEAR: Int = 6303

	const val DATE_ISLEAPYEAR: Int = 6304

	const val WORLDLIST_FETCH: Int = 6500

	const val WORLDLIST_START: Int = 6501

	const val WORLDLIST_NEXT: Int = 6502

	const val WORLDLIST_SWITCH: Int = 6503

	const val WORLDLIST_SPECIFIC: Int = 6506

	const val WORLDLIST_SORT: Int = 6507

	const val WORLDLIST_AUTOWORLD: Int = 6508

	const val WORLDLIST_PINGWORLDS: Int = 6509

	const val WORLDLIST_SPECIFIC_THISWORLD: Int = 6510

	const val IF_DEBUG_GETOPENIFCOUNT: Int = 6700

	const val IF_GETTOP: Int = 6701

	const val IF_DEBUG_GETNAME: Int = 6702

	const val IF_DEBUG_GETCOMCOUNT: Int = 6703

	const val IF_DEBUG_GETCOMNAME: Int = 6704

	const val IF_DEBUG_GETSERVERTRIGGERS: Int = 6705

	const val IF_DEBUG_BUTTON1: Int = 6707

	const val IF_DEBUG_BUTTON2: Int = 6708

	const val IF_DEBUG_BUTTON3: Int = 6709

	const val IF_DEBUG_BUTTON4: Int = 6710

	const val IF_DEBUG_BUTTON5: Int = 6711

	const val IF_DEBUG_BUTTON6: Int = 6712

	const val IF_DEBUG_BUTTON7: Int = 6713

	const val IF_DEBUG_BUTTON8: Int = 6714

	const val IF_DEBUG_BUTTON9: Int = 6715

	const val IF_DEBUG_BUTTON10: Int = 6716

	const val IF_DEBUG_TARGET: Int = 6717

	const val MEC_TEXT: Int = 6800

	const val MEC_SPRITE: Int = 6801

	const val MEC_TEXTSIZE: Int = 6802

	const val MEC_CATEGORY: Int = 6803

	const val MEC_PARAM: Int = 6804

	const val USERDETAIL_QUICKCHAT: Int = 6900

	const val USERDETAIL_LOBBY_MEMBERSHIP: Int = 6901

	const val USERDETAIL_LOBBY_RECOVERYDAY: Int = 6902

	const val USERDETAIL_LOBBY_UNREADMESSAGES: Int = 6903

	const val USERDETAIL_LOBBY_LASTLOGINDAY: Int = 6904

	const val USERDETAIL_LOBBY_LASTLOGINADDRESS: Int = 6905

	const val USERDETAIL_LOBBY_EMAILSTATUS: Int = 6906

	const val USERDETAIL_LOBBY_CCEXPIRY: Int = 6907

	const val USERDETAIL_LOBBY_GRACEEXPIRY: Int = 6908

	const val USERDETAIL_LOBBY_DOBREQUESTED: Int = 6909

	const val UDETAIL_DOB: Int = 6910

	const val USERDETAIL_LOBBY_MEMBERSSTATS: Int = 6911

	const val USERDETAIL_LOBBY_PLAYAGE: Int = 6912

	const val AUTOSETUP_DOSETUP: Int = 7000

	const val AUTOSETUP_SETHIGH: Int = 7001

	const val AUTOSETUP_SETMEDIUM: Int = 7002

	const val AUTOSETUP_SETLOW: Int = 7003

	const val AUTOSETUP_SETMIN: Int = 7004

	const val AUTOSETUP_SETCUSTOM: Int = 7005

	const val AUTOSETUP_BLACKFLAGLAST: Int = 7006

	const val AUTOSETUP_GETLEVEL: Int = 7007

	const val VIDEO_ADVERT_PLAY: Int = 7100

	const val VIDEO_ADVERT_FORCE_REMOVE: Int = 7101

	const val VIDEO_ADVERT_ALLOW_SKIP: Int = 7102

	const val DETAILCANMOD_GROUNDDECOR: Int = 7201

	const val DETAILCANMOD_SPOTSHADOWS: Int = 7202

	const val DETAILCANMOD_HARDSHADOWS: Int = 7203

	const val DETAILCANMOD_WATERDETAIL: Int = 7204

	const val DETAILCANMOD_ANTIALIASING: Int = 7205

	const val DETAILCANMOD_PARTICLES: Int = 7206

	const val DETAILCANMOD_BUILDAREA: Int = 7207

	const val DETAILCANMOD_BLOOM: Int = 7208

	const val DETAILCANMOD_GROUNDBLENDING: Int = 7209

	const val DETAILCANMOD_TEXTURING: Int = 7210

	const val DETAILCANMOD_MAXSCREENSIZE: Int = 7211

	const val DETAILCANMOD_FOG: Int = 7212

	const val DETAILCANMOD_ORTHOGRAPHIC: Int = 7213

	const val DETAILCANMOD_TOOLKIT_DEFAULT: Int = 7214

	const val DETAILCANSET_GROUNDDECOR: Int = 7301

	const val DETAILCANSET_SPOTSHADOWS: Int = 7302

	const val DETAILCANSET_HARDSHADOWS: Int = 7303

	const val DETAILCANSET_WATERDETAIL: Int = 7304

	const val DETAILCANSET_ANTIALIASING: Int = 7305

	const val DETAILCANSET_PARTICLES: Int = 7306

	const val DETAILCANSET_BUILDAREA: Int = 7307

	const val DETAILCANSET_BLOOM: Int = 7308

	const val DETAILCANSET_GROUNDBLENDING: Int = 7309

	const val DETAILCANSET_TEXTURING: Int = 7310

	const val DETAILCANSET_MAXSCREENSIZE: Int = 7311

	const val DETAILCANSET_FOG: Int = 7312

	const val DETAILCANSET_ORTHOGRAPHIC: Int = 7313

	const val DETAILCANSET_TOOLKIT_DEFAULT: Int = 7314
}
