import axios from "axios";
import React, { createContext, useCallback, useContext, useEffect, useState } from "react";
import { api, clearStoredCredentials, getStoredCredentials, setStoredCredentials } from "./api";

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
    try {
      await api.get("/api/locations", { params: { page: 0, size: 1 } });
      setRole("admin");
    } catch (e) {
      if (axios.isAxiosError(e) && e.response?.status === 403) {
        setRole("agency");
      } else if (axios.isAxiosError(e) && e.response?.status === 401) {
        logout();
      } else {
        setRole(null);
      }
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
      try {
        await api.get("/api/locations", { params: { page: 0, size: 1 } });
        setUser({ username });
        setRole("admin");
        setLoading(false);
        return { ok: true };
      } catch (e) {
        if (axios.isAxiosError(e) && e.response?.status === 401) {
          logout();
          setLoading(false);
          return { ok: false, message: "Username or password is incorrect." };
        }
        if (axios.isAxiosError(e) && e.response?.status === 403) {
          setUser({ username });
          setRole("agency");
          setLoading(false);
          return { ok: true };
        }
        const status = axios.isAxiosError(e) ? e.response?.status : undefined;
        const body = axios.isAxiosError(e) ? e.response?.data : undefined;
        setLoading(false);
        return {
          ok: false,
          message: body?.errorMessage || (status ? `Server error (${status})` : "Request failed."),
        };
      }
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
