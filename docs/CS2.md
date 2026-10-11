# Interfaces and CS2

How the client runs client scripts (CS2), and where to change it. The first table says
where each part lives.

Where its state lives: the interpreter's in `CS2State` and `ScriptRunner`, the open interfaces and
pending hooks in `InterfaceManager`, the loaded components in `Interface`, and the queued tick and mouse
hooks in `TickHookRequests`.

## Where it lives

| Part | Class | What it holds |
|---|---|---|
| The interpreter | `CS2Interpreter` (root package) | `executeScript`, the two opcode handlers, and the entry points below |
| Interpreter state | `statics/CS2State` | `intStackPtr`, `stringStack`/`stringStackPtr`, `frames`/`framePtr`, `globalArrayLengths`, `activeComponent`/`activeComponent2`, `hookRequestCount` |
| Stacks and locals | `ScriptRunner` | `intStack`, `intLocals`, `stringVars` |
| A script | `cache/node/lru/ClientScript` | `operations`, `intOpValues`, `stringOpValues`, `switchMaps`, the four local and argument counts, `scriptName`, `triggerType` |
| Loading | `ClientScript.decodeScript` (decoder), `ClientScript.getClientScript` (cached load by id), `ClientScript.getTriggerScript` (trigger lookup) | |
| Call frames | `StackFrame` | `script`, `pc`, `intVars`, `stringVars` for one `GOSUB_WITH_PARAMS` |
| Opcode numbers | `config/rsinterface/cs2/ClientScriptOpCode` | One `int` constant per opcode |
| Hooks | `config/rsinterface/HookRequest` | `source`, `params` (script id and arguments), `mouseX`/`mouseY`, `clickedOption`, `targetComponent`, `typedKeyCode`, `optionName` |
| Interfaces | `config/rsinterface/Component`, `IFEvents`, `config/rsinterface/Interface`, `InterfaceManager` | A component's hooks (`onLoadScript`, `onTimer`, `onClick`, ...) and its settings bits; the loaded interfaces (`Interface.interfaces`); the open sub-interfaces and pending hooks (`InterfaceManager`) |

## How a script runs

There are three ways in, and each one ends in `executeScript(script, stackLimit)`:

- **Hooks.** `executeHookInner(hook)` calls `executeHookInternal(hook, 200000)`. That method:
  1. loads the script named by `hook.params[0]`;
  2. copies the remaining arguments into `ScriptRunner.intLocals` and `stringVars`;
  3. substitutes the `event_*` placeholders with values from the hook. The int placeholders are `0x80000001` to `0x80000009`: mouse x and y, source component and slot, clicked option, target component and slot, key code, key char. The string placeholder is `"event_opbase"`.

  Hooks come from:
  - the game tick (`GameTick.processGameTick`), which drains `TickHookRequests.TICK_HOOK_REQUESTS` (onTimer), then `TickHookRequests.MOUSE_HOOK_REQUESTS` (onRelease, onMouseLeave), then `InterfaceManager.PENDING_HOOK_REQUESTS` (onClick, onHold, onMouseOver and the other component hooks, and `CC_IF_CALLONRESIZE`);
  - interface events such as sub-interface change and close (`SubInterfaceHookRunner`), dragging (`ComponentDragTick`) and targeting (`InterfaceSelectionStart`, `Static657`).
- **Interface load.** `runInterfaceOnLoadScripts(interfaceId)` runs every component's `onLoadScript` with a limit of 2,000,000. Called when an interface opens or the base window changes.
- **Triggers.** `executeTrigger(triggerType, v1, v2)` finds a script with `ClientScript.getTriggerScript`. A script's `triggerType` is a `ClientTriggerType`. The world map's `MEC_MOUSE_*` triggers use this route.

`executeScript` runs a loop:
1. Resets the stack pointers.
2. Fetches `operations[instrPtr]`.
3. Dispatches the opcode:
   - **Below 100** (constants, vars, branches, locals, `GOSUB_WITH_PARAMS`, `RETURN`, arrays, `SWITCH`): handled inline.
   - **100 to 4999** (`cc_*`/`if_*` component ops, config lookups, chat): `executeOpBelow5000(opcode, hasIntOperand)`.
   - **5000 to 9999** (client ops: chat, friends, world map, camera, settings, sound, login and lobby): `executeOpFrom5000`.

`hasIntOperand` is `intOpValues[pc] == 1`. The component ops use it to pick between `activeComponent` and `activeComponent2` (`cc_*` vs `.cc_*`).

When a script runs more than `stackLimit` instructions, the loop throws `"slow"`. Any exception is caught. It is reported through `ExplicitRuntimeException.reportError` with the chain of calling scripts (`frames`) and printed to the console with `printToConsole`, and the script stops. The `Op:` line always prints `-1`, as it did in the 2010 client.

## Adding or changing an opcode

1. Add the constant to `ClientScriptOpCode` (or find it there). The number must match the scripts in the cache.
2. Handle it in the handler for its range. Ops pop their arguments from `ScriptRunner.intStack` with `intStack[--CS2State.intStackPtr]` (strings from `CS2State.stringStack[--stringStackPtr]`), and push results the same way.
3. Ops that change an interface component call `Component.redrawComponent` afterwards. If the server must hear about the change, they queue a delayed state change (the `delayedIf*Update` methods).
4. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. Logging in runs the base window's onLoad scripts and the game tick's hooks.

## Interfaces, briefly

An interface is a group of `Component`s. `Interface.getComponent(idHash)` finds one by `interfaceId << 16 | componentId`; `Interface.interfaces[interfaceId]` holds every component of a loaded interface. A component's kind is `Component.type`, a `ComponentType` (`CONTAINER`, `FIGURE`, `TEXT`, `SPRITE`, `MODEL`, `LINE`, ...). Open sub-interfaces are kept in `InterfaceManager.OPEN_INTERFACES` as `SubInterface`s, whose `openType` is a `SubInterfaceType` (`MODAL`, `OVERLAY`, `OVERLAY_CLIENT`):
- `SubInterfaceOpener.openSubInterface` opens one;
- `SubInterfaceCloser.closeSubInterface` closes one;
- `InterfaceManager.loadInterface` decodes a group from the cache.

A component's size and position are recomputed against its parent in `InterfaceManager.initSizes`, and redrawn with `Component.redrawComponent`.
