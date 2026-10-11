# Network and packets

How the client reads server packets and sends its own, and where to add one. Each part is listed
with the class it lives in.

Where its state lives: the decoder's and the outgoing queue's state in `BufferedConnectionContext`, the
packet pool and the ISAAC cipher in `TcpPacket`, the socket in `Connection.connection`, the client
packets in `ClientProt`, the server packets in `ServerProt`, and the login state in `LoginManager`.

## Where it lives

| Part | Class | What it holds |
|---|---|---|
| The decoder | `PacketDecoder.decode` (root package) | Reads one packet and handles it: one `if` branch per `ServerProt` |
| The decode loop | `GuardedPacketDecode.decodePacketFromStream` | Calls `decode`, reports errors (`T2`), handles a lost connection |
| Decoder state | `network/BufferedConnectionContext` | `receivedBuffer`, `currentPacket`, `currentPacketSize`, `lastPacket`, `secondLastPacket`, `readPadding`, `idleReadPulses`. Two strays: `ThirdLastPacket.thirdLastPacket` and `OutboundByteCounter.bytesRead` |
| The connection | `Connection.connection` (`network/Connection`, `AsyncConnection`, `AsyncInputStream`, `AsyncOutputStream`) | The socket and its reader and writer threads |
| Encryption | `TcpPacket.isaac` (`tools/Isaac`) | The ISAAC cipher for outgoing opcodes, seeded at login |
| Server packets | `network/packet/ServerProt` | `getOpcode()` and `size` (`-1` = byte length, `-2` = short length). The constants are on the class, e.g. `ServerProt.VARP_LARGE`, `ServerProt.IF_SETITEM`; `ServerProt.serverProtValues()` lists them by opcode |
| Zone packets | `UpdateZonePacket`, `UpdateZonePacketDecoder.decodeUpdateZonePacket` | The sub-packets inside the `UPDATE_ZONE_*` and ground item and object packets. `UpdateZonePacket.updateZonePacketValues()` lists them by id |
| Client packets | `ClientPacket` (opcode and size), `network/packet/ClientProt` | One constant per packet, e.g. `ClientProt.OPLOC1`, `ClientProt.MOVE_GAMECLICK`, `ClientProt.RESUME_COUNTDIALOG` |
| Sending | `TcpPacket.createPacket(packet, isaac)`, `BufferedConnectionContext.queuePacket(packet)`, `BufferedConnectionContext.flush()` | `queuedPackets` holds the packets not yet written |
| Login | `Static701`, `LobbyManager`, `client/login/LoginManager`, `LoginProt` | The login and lobby handshakes. `LoginManager` holds the login state (`loginStep`, `loginUsername`, the responses) |

## How a server packet is read

The game tick (`GameTick.processGameTick`) calls `decodePacketFromStream` up to 100 times per cycle.

`decode` reads one packet:

1. It reads the opcode. It needs one byte, or two when `largeOpcode()` says so. The opcode looks up `currentPacket` in `serverProtValues()`.
2. It reads the payload size (`ClientFeatures.serverPacketSize` may override it for another server).
3. It reads the payload into `receivedBuffer` and shifts the packet history.
4. It runs the packet's branch.

Each branch reads its fields from `receivedBuffer` in the order the server writes them, acts, sets `currentPacket = null`, and returns `true`. A branch that returns `false` (`REGION`, `LOGOUT_FULL`) stops the loop for this cycle.

Errors are handled in three ways:
- **Unknown packet.** `decode` reports it as `T1` with the last three opcodes and logs out.
- **Exception.** `decodePacketFromStream` reports it as `T2` with the packet bytes and logs out.
- **IOException.** This means a lost connection. It goes to `ConnectionLostHandler.handleConnectionLost`, which reconnects in game or logs out from the lobby.

Most interface packets don't change a component directly. They queue a **pulse event** through a `priorityIf*Update` helper (for example `PulseEvent.priorityIfTextUpdate`, `PulseEvent.priorityIfcontentUpdate`), keyed by component hash and type in `PulseEventHandler.createPulseEvent`. The game tick then applies the queued events to the components. The same queue carries varc changes (`priorityClientvarcUpdate`, `priorityVarcstringUpdate`).

Zone packets (`UPDATE_ZONE_PARTIAL_ENCLOSED` and the ground item, object, projectile and sound packets) set a zone base (`UpdateZonePacket.updateZoneX`, `updateZoneY`, `UpdateZonePacketDecoder.updateZonePlane`) and hand an `UpdateZonePacket` to `decodeUpdateZonePacket`.

## How a client packet is sent

Clicking a location's first option (`MiniMenuAction.OPLOC1`) in `MiniMenuActionExecutor.processMenuClick`:

```java
TcpPacket packet = TcpPacket.createPacket(ClientProt.OPLOC1, TcpPacket.isaac);
packet.buffer.writeByteSubtract(KeyRecorder.keyRecorder.isPressed(82) ? 1 : 0);   // the payload, in the order the server reads it
packet.buffer.writeShortAddLittle(MapRegion.sceneBaseX + param0);
packet.buffer.writeShortLittle(param1 + MapRegion.sceneBaseY);
packet.buffer.writeShort(Integer.MAX_VALUE & (int) (entityUid >>> 32));
BufferedConnectionContext.queuePacket(packet);
```

`createPacket` writes the opcode, encrypted with ISAAC. `queuePacket` adds the packet to the queue, and `flush` writes the queue to the socket once per game tick (and right after login). Variable-size packets (size `-1`) start with a length byte. The CS2 op that sends `ClientProt.OPEN_URL` computes it up front (`Utils.getEncodedStringLength`). The one that sends `ClientProt.SEND_PRIVATE_MESSAGE` writes a placeholder, then the payload, then fills the length in with `buffer.writeIndex`.

The mini menu's option clicks in `MiniMenuAction` (`OPLOC1` to `OPLOC5`, `OPNPC1`, `OPOBJ3`, ...) each send the `ClientProt` packet of the same name from `processMenuClick`. To add a click, add its id to `MiniMenuAction` and a branch there that sends the packet.

## Adding a server packet

1. Declare the `ServerProt` with the opcode and size the server uses: `new ServerProt(opcode, size)`. Add it to the array `ServerProt.serverProtValues()` returns, at the index of its opcode (the array is indexed by opcode). An opcode the array does not cover is reported as invalid (`invo:`).
2. Add a branch to `decode`:

   ```java
   } else if (BufferedConnectionContext.currentPacket == ServerProt.MY_PACKET) {
       int value = BufferedConnectionContext.receivedBuffer.readUShort();
       // act
       BufferedConnectionContext.currentPacket = null;
       return true;
   }
   ```

   Read exactly `currentPacketSize` bytes. A short read misaligns every later packet.
3. If the packet changes an interface, queue a pulse event rather than changing the component, so it applies at the same point in the tick as the others.
4. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. Logging in exercises:
   - the decoder (region, varps, interfaces, inventories, player update);
   - the gfx step (`SPOT_ANIM_SPECIFIC`);
   - the chat step (`MESSAGE_PUBLIC`).

## Login, briefly

`Static701.method5595` drives the login steps (`LoginStep`) for the game and the lobby: it connects, exchanges the keys, sends the login block, and reads the reply.

The reply's fields land in the named statics the CS2 `USERDETAIL_*` ops read:
- `LoginManager.playerModLevel`
- `LoginManager.isMember`
- `LoginManager.playerDob`
- `LoginManager.lastLoginDay`
- and the others, mostly in `LoginManager`.

Each step changes `ClientState.mainLogicStep` through `setMainLogicStep`. The steps are listed in `MainLogicStep`.

## Example: experience drops

`client/features/XpDrops` reads an existing packet without changing how it is decoded: the
`UPDATE_STAT` branch of `PacketDecoder.decode` reads the level, skill id and experience, and calls
`XpDrops.statUpdated(skillId, client.statXp[skillId], xp)` before it stores the new experience. The
stat arrays (`client.statXp`, `client.statLevels`) are in the root `client`, out of reach of
packaged code, so the decoder passes the values. The drops are drawn after the scene in
`ClientState.drawGameArea`, with `Renderers.FONT_RENDERER`.
