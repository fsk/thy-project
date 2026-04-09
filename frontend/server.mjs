import fs from "node:fs/promises";
import http from "node:http";
import path from "node:path";
import { fileURLToPath } from "node:url";

const distDir = path.join(path.dirname(fileURLToPath(import.meta.url)), "dist");
const port = Number(process.env.PORT) || 8080;

const MIME = {
  ".html": "text/html; charset=utf-8",
  ".js": "application/javascript",
  ".css": "text/css",
  ".json": "application/json",
  ".ico": "image/x-icon",
  ".png": "image/png",
  ".svg": "image/svg+xml",
  ".woff2": "font/woff2",
  ".webp": "image/webp",
  ".txt": "text/plain",
  ".map": "application/json",
};

function mimeFor(filePath) {
  return MIME[path.extname(filePath)] || "application/octet-stream";
}

async function readIfFile(filePath) {
  try {
    const st = await fs.stat(filePath);
    if (st.isDirectory()) {
      return readIfFile(path.join(filePath, "index.html"));
    }
    const data = await fs.readFile(filePath);
    return { filePath, data };
  } catch {
    return null;
  }
}

http
  .createServer(async (req, res) => {
    const pathname = new URL(req.url || "/", "http://127.0.0.1").pathname;
    if (pathname.includes("..")) {
      res.writeHead(400).end();
      return;
    }
    const rel = pathname === "/" ? "index.html" : pathname.slice(1);
    const candidate = path.join(distDir, rel);

    let out = await readIfFile(candidate);
    if (!out) {
      try {
        const data = await fs.readFile(path.join(distDir, "index.html"));
        out = { filePath: path.join(distDir, "index.html"), data };
      } catch {
        res
          .writeHead(500, { "Content-Type": "text/plain; charset=utf-8" })
          .end("dist/ missing — build step must run before start (npm run build).");
        return;
      }
    }
    res.writeHead(200, { "Content-Type": mimeFor(out.filePath) });
    res.end(out.data);
  })
  .listen(port, "0.0.0.0", () => {
    console.log(`static http://0.0.0.0:${port} -> ${distDir}`);
  });
