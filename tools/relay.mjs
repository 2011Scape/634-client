#!/usr/bin/env node
// A WebSocket-to-TCP relay for the browser client (docs/MULTIPLATFORM.md, milestone 4), in the
// place of websockify: each WebSocket connection to ws://localhost:<listen>/ (any path) gets its own
// TCP connection to the game server, and bytes go both ways unchanged. No dependencies.
//
//   node tools/relay.mjs [listenPort=8081] [serverHost=localhost] [serverPort=43594]

import { createServer } from 'node:http'
import { connect } from 'node:net'
import { createHash } from 'node:crypto'

const listenPort = Number(process.argv[2] ?? 8081)
const serverHost = process.argv[3] ?? 'localhost'
const serverPort = Number(process.argv[4] ?? 43594)
const GUID = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'

const server = createServer((req, res) => {
  res.writeHead(426, { 'Content-Type': 'text/plain' })
  res.end('WebSocket only\n')
})

server.on('upgrade', (req, socket) => {
  const key = req.headers['sec-websocket-key']
  if (!key || (req.headers.upgrade ?? '').toLowerCase() !== 'websocket') {
    socket.destroy()
    return
  }
  const accept = createHash('sha1').update(key + GUID).digest('base64')
  const protocol = req.headers['sec-websocket-protocol']
  const headers = [
    'HTTP/1.1 101 Switching Protocols',
    'Upgrade: websocket',
    'Connection: Upgrade',
    'Sec-WebSocket-Accept: ' + accept,
  ]
  // websockify clients ask for "binary"; answer with the first protocol offered
  if (protocol) headers.push('Sec-WebSocket-Protocol: ' + protocol.split(',')[0].trim())
  socket.write(headers.join('\r\n') + '\r\n\r\n')
  socket.setNoDelay(true)

  const upstream = connect(serverPort, serverHost)
  upstream.setNoDelay(true)
  const id = `${req.socket.remoteAddress}:${req.socket.remotePort}`
  console.log(`relay ${id} -> ${serverHost}:${serverPort}`)

  let toServer = 0
  let toClient = 0
  upstream.on('data', (data) => { toClient += data.length; socket.write(frame(0x2, data)) })
  upstream.on('close', () => { if (!socket.destroyed) socket.end(frame(0x8, Buffer.alloc(0))) })
  upstream.on('error', (err) => { console.log(`relay ${id}: server ${err.message}`); socket.destroy() })

  let buffer = Buffer.alloc(0)
  socket.on('data', (chunk) => {
    buffer = Buffer.concat([buffer, chunk])
    for (;;) {
      const parsed = parse(buffer)
      if (!parsed) break
      buffer = buffer.subarray(parsed.length)
      if (parsed.opcode === 0x8) { upstream.end(); socket.end(frame(0x8, Buffer.alloc(0))); return }
      if (parsed.opcode === 0x9) { socket.write(frame(0xA, parsed.payload)); continue }
      if (parsed.opcode === 0xA) continue
      toServer += parsed.payload.length
      upstream.write(parsed.payload)
    }
  })
  socket.on('close', () => { upstream.destroy(); console.log(`relay ${id} closed: ${toServer} bytes to the server, ${toClient} to the client`) })
  socket.on('error', () => upstream.destroy())
})

// One complete client frame at the start of buf, or null. Client frames are always masked.
function parse (buf) {
  if (buf.length < 2) return null
  const opcode = buf[0] & 0x0f
  let len = buf[1] & 0x7f
  let off = 2
  if (len === 126) { if (buf.length < 4) return null; len = buf.readUInt16BE(2); off = 4 }
  else if (len === 127) { if (buf.length < 10) return null; len = Number(buf.readBigUInt64BE(2)); off = 10 }
  const masked = (buf[1] & 0x80) !== 0
  const maskOff = off
  if (masked) off += 4
  if (buf.length < off + len) return null
  const payload = Buffer.from(buf.subarray(off, off + len))
  if (masked) for (let i = 0; i < len; i++) payload[i] ^= buf[maskOff + (i & 3)]
  return { opcode, payload, length: off + len }
}

// A final, unmasked server frame.
function frame (opcode, payload) {
  const len = payload.length
  let head
  if (len < 126) head = Buffer.from([0x80 | opcode, len])
  else if (len < 65536) { head = Buffer.alloc(4); head[0] = 0x80 | opcode; head[1] = 126; head.writeUInt16BE(len, 2) }
  else { head = Buffer.alloc(10); head[0] = 0x80 | opcode; head[1] = 127; head.writeBigUInt64BE(BigInt(len), 2) }
  return Buffer.concat([head, payload])
}

server.listen(listenPort, () => console.log(`relay listening on ws://localhost:${listenPort}, to ${serverHost}:${serverPort}`))
