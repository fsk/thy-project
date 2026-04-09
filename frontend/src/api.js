/**
 * Backend ApiResponse: { success, data, resultMessage, errorMessage }
 * Development: leave empty to use Vite proxy (/api -> localhost:8080).
 * Production: set VITE_API_BASE=https://your-backend.run.app in .env
 */
const API_BASE = (import.meta.env.VITE_API_BASE || "").replace(/\/$/, "");

export function apiUrl(path) {
  const p = path.startsWith("/") ? path : `/${path}`;
  return `${API_BASE}${p}`;
}

function basicHeader(username, password) {
  const token = btoa(`${username}:${password}`);
  return `Basic ${token}`;
}

export function getStoredCredentials() {
  try {
    const raw = sessionStorage.getItem("thy_auth");
    if (!raw) return null;
    const o = JSON.parse(raw);
    if (o && typeof o.username === "string" && typeof o.password === "string") {
      return o;
    }
  } catch {
    /* ignore */
  }
  return null;
}

export function setStoredCredentials(username, password) {
  sessionStorage.setItem("thy_auth", JSON.stringify({ username, password }));
}

export function clearStoredCredentials() {
  sessionStorage.removeItem("thy_auth");
}

/**
 * fetch wrapper: Basic Auth + JSON
 */
export async function apiFetch(path, options = {}) {
  const creds = getStoredCredentials();
  const headers = {
    Accept: "application/json",
    ...options.headers,
  };
  if (creds) {
    headers.Authorization = basicHeader(creds.username, creds.password);
  }
  if (options.body && typeof options.body === "object" && !(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    options = { ...options, body: JSON.stringify(options.body) };
  }
  const res = await fetch(apiUrl(path), { ...options, headers });
  return res;
}

export async function readApiJson(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return { parseError: true, raw: text };
  }
}
