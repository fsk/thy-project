import React, { useCallback, useEffect, useState } from "react";
import PaginationBar from "../components/PaginationBar";
import { apiFetch, readApiJson } from "../api";
import { useAuth } from "../AuthContext";
import "./Pages.css";

const LOCATION_PICKER_SIZE = 500;
const ROUTE_PAGE_SIZE = 10;

export default function RoutesPage() {
  const { isAdmin } = useAuth();
  const [locations, setLocations] = useState([]);
  const [originId, setOriginId] = useState("");
  const [destId, setDestId] = useState("");
  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [routes, setRoutes] = useState([]);
  const [routePage, setRoutePage] = useState(0);
  const [routeTotalPages, setRouteTotalPages] = useState(0);
  const [routeTotalElements, setRouteTotalElements] = useState(0);
  const [hasQueried, setHasQueried] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [selected, setSelected] = useState(null);

  const loadLocations = useCallback(async () => {
    if (!isAdmin) return;
    const res = await apiFetch(`/api/locations?page=0&size=${LOCATION_PICKER_SIZE}`);
    const body = await readApiJson(res);
    if (res.ok && body?.data?.content?.length) {
      const list = body.data.content;
      setLocations(list);
      setOriginId(list[0].id);
      setDestId(list.length > 1 ? list[1].id : list[0].id);
    }
  }, [isAdmin]);

  useEffect(() => {
    loadLocations();
  }, [loadLocations]);

  async function fetchRoutes(pageIndex) {
    setLoading(true);
    setError("");
    setSelected(null);
    const q = [
      `originLocationId=${encodeURIComponent(originId)}`,
      `destinationLocationId=${encodeURIComponent(destId)}`,
      `date=${encodeURIComponent(date)}`,
      `page=${pageIndex}`,
      `size=${ROUTE_PAGE_SIZE}`,
    ].join("&");
    const res = await apiFetch(`/api/routes?${q}`);
    const body = await readApiJson(res);
    if (!res.ok) {
      setRoutes([]);
      setRouteTotalPages(0);
      setRouteTotalElements(0);
      setHasQueried(true);
      setError(body?.errorMessage || `Request failed (${res.status})`);
    } else {
      const d = body?.data;
      setRoutes(d?.content || []);
      setRouteTotalPages(d?.totalPages ?? 0);
      setRouteTotalElements(d?.totalElements ?? 0);
      setRoutePage(typeof d?.number === "number" ? d.number : pageIndex);
      setHasQueried(true);
    }
    setLoading(false);
  }

  async function search(e) {
    e.preventDefault();
    setError("");
    setSelected(null);
    if (!originId || !destId) {
      setError("Select origin and destination or enter UUID.");
      return;
    }
    await fetchRoutes(0);
  }

  return (
    <div className="page">
      <h2>Routes</h2>

      <form className="card form-grid" onSubmit={search} style={{ maxWidth: "100%" }}>
        {isAdmin && locations.length === 0 && (
          <p className="error-text" style={{ gridColumn: "1 / -1", margin: 0 }}>
            No locations yet. Please add locations from Locations page first.
          </p>
        )}
        {isAdmin && locations.length > 0 ? (
          <>
            <label>
              Origin
              <select value={originId} onChange={(e) => setOriginId(e.target.value)} required>
                {locations.map((l) => (
                  <option key={l.id} value={l.id}>
                    {l.locationCode} — {l.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Destination
              <select value={destId} onChange={(e) => setDestId(e.target.value)} required>
                {locations.map((l) => (
                  <option key={l.id} value={l.id}>
                    {l.locationCode} — {l.name}
                  </option>
                ))}
              </select>
            </label>
          </>
        ) : (
          <>
            <label style={{ gridColumn: "1 / -1" }}>
              Origin location UUID
              <input
                value={originId}
                onChange={(e) => setOriginId(e.target.value.trim())}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                required
                pattern="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
              />
            </label>
            <label style={{ gridColumn: "1 / -1" }}>
              Destination location UUID
              <input
                value={destId}
                onChange={(e) => setDestId(e.target.value.trim())}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                required
                pattern="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
              />
            </label>
          </>
        )}
        <label>
          Date
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
        </label>
        <div className="toolbar" style={{ gridColumn: "1 / -1" }}>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading || (isAdmin && locations.length === 0)}
          >
            {loading ? "Searching…" : "List routes"}
          </button>
        </div>
      </form>

      {error && <p className="error-text">{error}</p>}

      <table className="data-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Leg count</th>
            <th>Summary</th>
          </tr>
        </thead>
        <tbody>
          {routes.map((route, idx) => {
            const legs = route.legs || [];
            const summary = legs.map((l) => `${l.origin?.locationCode}→${l.destination?.locationCode}`).join(" | ");
            return (
              <tr
                key={`${routePage}-${idx}`}
                className={`clickable ${selected === route ? "selected" : ""}`}
                onClick={() => setSelected(route)}
              >
                <td>{routePage * ROUTE_PAGE_SIZE + idx + 1}</td>
                <td>{legs.length}</td>
                <td>{summary || "—"}</td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {hasQueried && !error && (
        <PaginationBar
          page={routePage}
          totalPages={routeTotalPages}
          totalElements={routeTotalElements}
          onPageChange={fetchRoutes}
          disabled={loading}
        />
      )}

      {routes.length === 0 && !loading && !error && !hasQueried && (
        <p className="muted">No results; run the query.</p>
      )}

      {selected && (
        <>
          <button type="button" className="panel-overlay" aria-label="Close panel" onClick={() => setSelected(null)} />
          <aside className="side-panel">
            <div className="side-panel-header">
              <h3>Route details</h3>
              <button type="button" className="btn" onClick={() => setSelected(null)}>
                Close
              </button>
            </div>
            {(selected.legs || []).map((leg, i) => (
              <div key={i} className="leg-block">
                <strong>
                  Leg {i + 1}: {leg.transportationType}
                </strong>
                <div>
                  {leg.origin?.name} ({leg.origin?.locationCode}) → {leg.destination?.name} (
                  {leg.destination?.locationCode})
                </div>
                <div style={{ marginTop: "0.35rem", color: "#64748b" }}>
                  Days: {[...(leg.operatingDays || [])].sort().join(", ")}
                </div>
              </div>
            ))}
          </aside>
        </>
      )}
    </div>
  );
}
