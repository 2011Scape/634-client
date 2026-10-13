#!/usr/bin/env node
// Serves the browser client for development and its smoke test (README: "Running the browser client"), in the
// place of `python3 -m http.server`. No dependencies.
//
//   node tools/serve.mjs [port=8080] [bundleDir=build/kotlin-webpack/js/developmentExecutable]
//                        [referenceDir=~/634-smoke-ref/browser] [shotDir=build/smoke-browser]
//
// - GET /...              files from bundleDir (the page, the bundle, the SMAA files)
// - GET /smoke-ref/<png>  the browser smoke test's reference frames, from referenceDir
// - PUT /smoke-shots/<png> a frame the browser smoke test saved, written to shotDir
// Responses are never cached, so a rebuilt bundle is picked up on the next reload.
// It listens on 127.0.0.1 only; HOST=0.0.0.0 serves other machines too (the bundle and the references).

import { createServer } from 'node:http'
import { createReadStream, mkdirSync, statSync, writeFileSync } from 'node:fs'
import { homedir } from 'node:os'
import { basename, extname, join, normalize, resolve, sep } from 'node:path'

const port = Number(process.argv[2] ?? 8080)
const host = process.env.HOST ?? '127.0.0.1'
const MAX_SHOT = 16 * 1024 * 1024 // bytes; a smoke frame is well under 1 MB
const bundleDir = resolve(process.argv[3] ?? 'build/kotlin-webpack/js/developmentExecutable')
const referenceDir = resolve((process.argv[4] ?? '~/634-smoke-ref/browser').replace(/^~/, homedir()))
const shotDir = resolve(process.argv[5] ?? 'build/smoke-browser')
const log = (line) => console.log(new Date().toISOString().slice(11, 19) + ' ' + line)

const types = {
  '.html': 'text/html', '.js': 'text/javascript', '.map': 'application/json', '.png': 'image/png',
  '.ico': 'image/x-icon', '.hlsl': 'text/plain', '.bin': 'application/octet-stream', '.txt': 'text/plain',
}

// A path inside root, or null for one that would leave it (or a malformed escape).
function inside (root, urlPath) {
  let decoded
  try { decoded = decodeURIComponent(urlPath) } catch { return null }
  const file = normalize(join(root, decoded))
  return file === root || file.startsWith(root + sep) ? file : null
}

createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost')
  res.setHeader('Cache-Control', 'no-store')
  if (req.method === 'PUT' && url.pathname.startsWith('/smoke-shots/')) {
    const name = basename(url.pathname)
    if (extname(name) !== '.png') { res.writeHead(400); res.end(); return }
    const chunks = []
    let size = 0
    req.on('data', (chunk) => {
      size += chunk.length
      if (size > MAX_SHOT) { res.writeHead(413); res.end(); req.destroy(); return }
      chunks.push(chunk)
    })
    req.on('end', () => {
      if (size > MAX_SHOT) return
      mkdirSync(shotDir, { recursive: true })
      writeFileSync(join(shotDir, name), Buffer.concat(chunks))
      log(`saved ${join(shotDir, name)}`)
      res.writeHead(204); res.end()
    })
    return
  }
  if (req.method !== 'GET' && req.method !== 'HEAD') { res.writeHead(405); res.end(); return }
  let file = url.pathname.startsWith('/smoke-ref/')
    ? inside(referenceDir, url.pathname.slice('/smoke-ref/'.length))
    : inside(bundleDir, url.pathname === '/' ? 'index.html' : url.pathname)
  try {
    if (file === null || !statSync(file).isFile()) throw new Error()
  } catch {
    res.writeHead(404); res.end(); return
  }
  res.writeHead(200, { 'Content-Type': types[extname(file)] ?? 'application/octet-stream' })
  if (req.method === 'HEAD') { res.end(); return }
  createReadStream(file).pipe(res)
}).listen(port, host, () => log(`serving ${bundleDir} on http://${host}:${port}, references from ${referenceDir}, frames to ${shotDir}`))
