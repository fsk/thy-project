import React, { useState } from "react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./Layout.css";

export default function Layout() {
  const { user, isAdmin, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  const linkClass = ({ isActive }) => (isActive ? "nav-link active" : "nav-link");

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="header-row">
          <button
            type="button"
            className="menu-toggle"
            aria-label="Open/Close Menu"
            onClick={() => setMenuOpen((v) => !v)}
          >
            ☰
          </button>
          <h1 className="app-title">THY Project</h1>
          <div className="header-user">
            <span className="user-name">{user?.username}</span>
            <button type="button" className="btn btn-ghost" onClick={logout}>
              Logout
            </button>
          </div>
        </div>
      </header>

      <div className="app-body">
        <aside className={`sidebar ${menuOpen ? "open" : ""}`}>
          <nav className="sidebar-nav" onClick={() => setMenuOpen(false)}>
            <NavLink to="/routes" className={linkClass}>
              Routes
            </NavLink>
            {isAdmin && (
              <>
                <NavLink to="/locations" className={linkClass}>
                  Locations
                </NavLink>
                <NavLink to="/transportations" className={linkClass}>
                  Transportations
                </NavLink>
              </>
            )}
          </nav>
        </aside>

        {menuOpen && (
          <button
            type="button"
            className="sidebar-backdrop"
            aria-label="Close Menu"
            onClick={() => setMenuOpen(false)}
          />
        )}

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
