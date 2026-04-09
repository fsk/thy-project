import React from "react";
import { BrowserRouter, Navigate, Outlet, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "./AuthContext";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import LocationsPage from "./pages/LocationsPage";
import RoutesPage from "./pages/RoutesPage";
import TransportationsPage from "./pages/TransportationsPage";

function RequireAuth() {
  const { user, loading } = useAuth();
  if (loading) {
    return (
      <div className="page-center">
        <p>Loading…</p>
      </div>
    );
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return <Outlet />;
}

function AdminOnly() {
  const { isAdmin } = useAuth();
  if (!isAdmin) {
    return <Navigate to="/routes" replace />;
  }
  return <Outlet />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<RequireAuth />}>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/routes" replace />} />
          <Route path="routes" element={<RoutesPage />} />

          <Route element={<AdminOnly />}>
            <Route path="locations" element={<LocationsPage />} />
            <Route path="transportations" element={<TransportationsPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/routes" replace />} />
    </Routes>
  );
}

export default function App() {
  const baseUrl = import.meta.env.BASE_URL;
  const basename =
    baseUrl === "/" || baseUrl === "" ? undefined : baseUrl.replace(/\/$/, "");

  return (
    <BrowserRouter basename={basename}>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
