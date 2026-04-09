import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE || "";

export function getStoredCredentials() {
  const raw = sessionStorage.getItem("thy_auth");
  if (!raw) return null;
  const o = JSON.parse(raw);
  if (o && typeof o.username === "string" && typeof o.password === "string") {
    return o;
  }
  return null;
}

export function setStoredCredentials(username, password) {
  sessionStorage.setItem("thy_auth", JSON.stringify({ username, password }));
}

export function clearStoredCredentials() {
  sessionStorage.removeItem("thy_auth");
}

export const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  const creds = getStoredCredentials();
  if (creds) {
    config.headers.Authorization = `Basic ${btoa(`${creds.username}:${creds.password}`)}`;
  }
  return config;
});
