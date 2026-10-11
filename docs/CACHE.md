# Cache and JS5

How the client finds a file: first in its disk cache, otherwise from the server over JS5. This guide
also covers where to add a new archive or asset.

## Where it lives

Where its state lives: `Resource` holds the archive providers, the disk files (`cacheFileData`,
`cacheFileIndices`, `referenceTableDecompressor`) and the remote workers (`remoteFileWorkersByIndex`,
`remoteFileRequesterJs5`). The JS5 session is `RequestManager.requestManager`.

| Part | Class | What it does |
|---|---|---|
| Archives the client uses | `config/Resource` | One `Js5ResourceProvider` per archive (`Resource.OBJ`, `Resource.MODEL`, `Resource.ENUM`, ...) |
| Opening an archive | `Resource.createProvider(directMemoryMode, archiveId, loadType)` | Wires a disk store, a remote worker and a provider together |
| Reading files | `js5/Js5ResourceProvider` | `getFile(groupId, fileId)`, `getFile(groupName, fileName)`, `isFileReady`, `getGroupId`, `getGroupCapacity`, `fullyLoaded`. It unpacks a group into its files (`unpackGroup`), decrypting with XTEA keys when given |
| An archive's index | `js5/Js5MasterIndex` | Group and file ids, name hashes, CRCs, Whirlpool digests and versions, decoded from the reference table |
| Fetching groups | `js5/request/remote/RemoteFileWorker` (a `FileWorker`) | `request` checks the disk store first, then asks the server. It verifies the CRC, the Whirlpool digest and the version, and keeps the store up to date |
| The disk store | `cache/Decompressor` (one per archive) over `cache/FileOnDisk` / `cache/JFS32` | 520-byte sectors in `main_file_cache.dat2` (`Resource.cacheFileData`) and 6-byte entries in each `.idx` (`Resource.cacheFileIndices`); `Resource.referenceTableDecompressor` is idx255 |
| Disk requests | `js5/request/local/LocalRequester` | `createNormalRequest` (read), `createStoreRequest` (write), `createPriorityRequest` (urgent read), on its own thread |
| Server requests | `js5/Js5WorkerThread` (`Resource.remoteFileRequesterJs5`) | `request` queues urgent and prefetch requests and reads the responses |
| The JS5 session | `js5/request/RequestManager` (`RequestManager.requestManager`) | `initialize` fetches and checks the master index (RSA and Whirlpool). `getWorker` makes each archive's worker. `pulse` runs every worker once per client cycle (`client.java`) |
| Checks | `js5/Whirlpool`, `cache/compression/GzipDecompressor`, `Js5Compression` | Digests and container decompression |

## How a file is loaded

1. A caller asks the archive's provider for a file, for example `Resource.OBJ.getFile(group, file)`. If the group is not in memory yet, the provider returns `null`. The caller tries again on a later cycle, which is why loaders poll `isFileReady` or `fullyLoaded`.
2. The provider asks its `RemoteFileWorker` for the group. The worker queues a read on the `LocalRequester`.
3. If the group is on disk and its CRC, digest and version match the index, it is used.
4. Otherwise the worker requests the group from the server through the `Js5WorkerThread`, and writes the answer back to disk with a store request.
5. The provider unpacks the group into its files; its `loadType` decides how long it keeps them.

## Adding an archive or asset

- **A new file in an existing archive.** Read it with `getFile` on the archive's `Resource` provider. Poll until it is ready, the way the config type lists and loaders do.
- **A new archive.**
  1. Open it with `createProvider` beside the others where `Resource` is filled in.
  2. Keep the provider in a `Resource` field.
  3. Add it to the loading steps if the client must wait for it.

  The server must serve the archive, and the master index must list it.
- Start the client (`./gradlew runClient`) and log in on each toolkit. Logging in loads the archives the game uses from a fresh client cycle, through the same path.
