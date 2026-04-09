import React, { createContext, useCallback, useContext, useEffect, useState } from "react";
import {
  apiFetch,
  clearStoredCredentials,
  getStoredCredentials,
  readApiJson,
  setStoredCredentials,
} from "./api";

const AuthContext = createContext(null);

/**
 * Role detection: Admin GET /api/locations -> 200
 * Agency same request -> 403 (if authentication is valid)
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [role, setRole] = useState(null); // 'admin' | 'agency' | null
  const [loading, setLoading] = useState(true);

  const logout = useCallback(() => {
    clearStoredCredentials();
    setUser(null);
    setRole(null);
  }, []);

  const refreshRole = useCallback(async () => {
    const creds = getStoredCredentials();
    if (!creds) {
      setUser(null);
      setRole(null);
      setLoading(false);
      return;
    }
    setUser({ username: creds.username });
    const res = await apiFetch("/api/locations?page=0&size=1", { method: "GET" });
    if (res.status === 200) {
      setRole("admin");
    } else if (res.status === 403) {
      setRole("agency");
    } else if (res.status === 401) {
      logout();
    } else {
      setRole(null);
    }
    setLoading(false);
  }, [logout]);

  useEffect(() => {
    refreshRole();
  }, [refreshRole]);

  const login = useCallback(
    async (username, password) => {
      setStoredCredentials(username, password);
      setLoading(true);
      const res = await apiFetch("/api/locations?page=0&size=1", { method: "GET" });
      if (res.status === 401) {
        logout();
        setLoading(false);
        return { ok: false, message: "Username or password is incorrect." };
      }
      if (res.status === 200) {
        setUser({ username });
        setRole("admin");
        setLoading(false);
        return { ok: true };
      }
      // Credentials are valid, but /api/locations is forbidden -> agency role
      if (res.status === 403) {
        setUser({ username });
        setRole("agency");
        setLoading(false);
        return { ok: true };
      }
      const body = await readApiJson(res);
      setLoading(false);
      return { ok: false, message: body?.errorMessage || `Server error (${res.status})` };
    },
    [logout],
  );

  const value = {
    user,
    role,
    loading,
    isAdmin: role === "admin",
    isAgency: role === "agency",
    login,
    logout,
    refreshRole,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
