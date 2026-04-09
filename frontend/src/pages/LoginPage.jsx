import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./Pages.css";

export default function LoginPage() {
  const { user, login, loading } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  if (!loading && user) {
    return <Navigate to="/routes" replace />;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    const result = await login(username.trim(), password);
    if (!result.ok) {
      setError(result.message || "Login failed.");
    }
  }

  return (
    <div className="page-center">
      <form className="card login-card" onSubmit={handleSubmit}>
        <h2>Login</h2>
        <p className="muted">Same as Basic Auth in Backend (e.g. admin / agency)</p>
        <label>
          Username
          <input
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        {error && <p className="error-text">{error}</p>}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? "Checking…" : "Login"}
        </button>
      </form>
    </div>
  );
}
