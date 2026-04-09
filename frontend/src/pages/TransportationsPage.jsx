import axios from "axios";
import React, { useCallback, useEffect, useRef, useState } from "react";
import PaginationBar from "../components/PaginationBar";
import { api } from "../api";
import "./Pages.css";

const TYPES = ["FLIGHT", "BUS", "SUBWAY", "UBER"];
const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
const LOCATION_PICKER_SIZE = 500;
const PAGE_SIZE = 10;

function emptyForm(locations) {
  const first = locations[0]?.id || "";
  return {
    originLocationId: first,
    destinationLocationId: first,
    transportationType: "FLIGHT",
    operatingDays: new Set(["MONDAY"]),
  };
}

export default function TransportationsPage() {
  const [locations, setLocations] = useState([]);
  const [locLoading, setLocLoading] = useState(true);
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [form, setForm] = useState(() => emptyForm([]));
  const [editingId, setEditingId] = useState(null);
  const editingIdRef = useRef(null);

  const loadLocationOptions = useCallback(async () => {
    setLocLoading(true);
    try {
      const res = await api.get("/api/locations", { params: { page: 0, size: LOCATION_PICKER_SIZE } });
      const body = res.data;
      setLocations(body?.data?.content || []);
      setError("");
    } catch (e) {
      const body = axios.isAxiosError(e) ? e.response?.data : undefined;
      setError(body?.errorMessage || "Locations not found");
      setLocations([]);
    }
    setLocLoading(false);
  }, []);

  const loadTransportations = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/api/transportations", { params: { page, size: PAGE_SIZE } });
      const trBody = res.data;
      const d = trBody?.data;
      setItems(d?.content || []);
      setTotalPages(d?.totalPages ?? 0);
      setTotalElements(d?.totalElements ?? 0);
    } catch (e) {
      const trBody = axios.isAxiosError(e) ? e.response?.data : undefined;
      setError(trBody?.errorMessage || "Transportations not found");
      setItems([]);
    }
    setLoading(false);
  }, [page]);

  useEffect(() => {
    loadLocationOptions();
  }, [loadLocationOptions]);

  useEffect(() => {
    if (!locLoading && locations.length > 0) {
      loadTransportations();
    }
  }, [locLoading, locations.length, page, loadTransportations]);

  useEffect(() => {
    if (locations.length && !editingIdRef.current) {
      setForm(emptyForm(locations));
    }
  }, [locations]);

  function startEdit(row) {
    editingIdRef.current = row.id;
    setEditingId(row.id);
    setForm({
      originLocationId: row.origin.id,
      destinationLocationId: row.destination.id,
      transportationType: row.transportationType,
      operatingDays: new Set(row.operatingDays || []),
    });
  }

  function cancelEdit() {
    editingIdRef.current = null;
    setEditingId(null);
    setForm(emptyForm(locations));
  }

  function toggleDay(day) {
    setForm((f) => {
      const next = new Set(f.operatingDays);
      if (next.has(day)) next.delete(day);
      else next.add(day);
      return { ...f, operatingDays: next };
    });
  }

  function bodyFromForm() {
    if (form.operatingDays.size === 0) {
      alert("Select at least one operating day.");
      return null;
    }
    return {
      originLocationId: form.originLocationId,
      destinationLocationId: form.destinationLocationId,
      transportationType: form.transportationType,
      operatingDays: Array.from(form.operatingDays),
    };
  }

  async function handleCreate(e) {
    e.preventDefault();
    const body = bodyFromForm();
    if (!body) return;
    try {
      await api.post("/api/transportations", body);
      setForm(emptyForm(locations));
      if (page === 0) loadTransportations();
      else setPage(0);
    } catch (err) {
      const j = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(j?.errorMessage || `Error ${status}`);
    }
  }

  async function handleUpdate(e) {
    e.preventDefault();
    const body = bodyFromForm();
    if (!body || !editingId) return;
    try {
      await api.put(`/api/transportations/${editingId}`, body);
      editingIdRef.current = null;
      setEditingId(null);
      await loadTransportations();
    } catch (err) {
      const j = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(j?.errorMessage || `Error ${status}`);
    }
  }

  async function handleDelete(id) {
    if (!confirm("Are you sure you want to delete this transportation?")) return;
    try {
      await api.delete(`/api/transportations/${id}`);
      if (items.length === 1 && page > 0) {
        setPage((p) => p - 1);
      } else {
        loadTransportations();
      }
    } catch (err) {
      const j = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(j?.errorMessage || `Error ${status}`);
    }
  }

  const locOptions = locations.map((l) => (
    <option key={l.id} value={l.id}>
      {l.locationCode} — {l.name}
    </option>
  ));

  const showTable = !locLoading && !loading && !error;

  return (
    <div className="page">
      <h2>Transportations</h2>

      {locLoading && <p>Loading locations…</p>}

      {!locLoading && locations.length === 0 ? (
        <p className="error-text">Please add at least one location first.</p>
      ) : (
        !locLoading && (
          <form className="card form-grid" onSubmit={editingId ? handleUpdate : handleCreate}>
            <h3 style={{ margin: 0, gridColumn: "1 / -1" }}>{editingId ? "Edit" : "New transportation"}</h3>
            <label>
              Origin location
              <select
                value={form.originLocationId}
                onChange={(e) => setForm({ ...form, originLocationId: e.target.value })}
                required
              >
                {locOptions}
              </select>
            </label>
            <label>
              Destination location
              <select
                value={form.destinationLocationId}
                onChange={(e) => setForm({ ...form, destinationLocationId: e.target.value })}
                required
              >
                {locOptions}
              </select>
            </label>
            <label>
              Type
              <select
                value={form.transportationType}
                onChange={(e) => setForm({ ...form, transportationType: e.target.value })}
              >
                {TYPES.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </label>
            <div style={{ gridColumn: "1 / -1" }}>
              <span style={{ fontSize: "0.85rem", color: "#475569" }}>Operating days</span>
              <div className="days-row" style={{ marginTop: "0.35rem" }}>
                {DAYS.map((d) => (
                  <label key={d}>
                    <input
                      type="checkbox"
                      checked={form.operatingDays.has(d)}
                      onChange={() => toggleDay(d)}
                    />
                    {d.slice(0, 3)}
                  </label>
                ))}
              </div>
            </div>
            <div className="toolbar" style={{ gridColumn: "1 / -1" }}>
              <button type="submit" className="btn btn-primary">
                {editingId ? "Save" : "Add"}
              </button>
              {editingId && (
                <button type="button" className="btn" onClick={cancelEdit}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        )
      )}

      {loading && !locLoading && <p>Loading…</p>}
      {error && <p className="error-text">{error}</p>}

      {showTable && (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Type</th>
                <th>Origin</th>
                <th>Destination</th>
                <th>Operating days</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {items.map((row) => (
                <tr key={row.id}>
                  <td>{row.transportationType}</td>
                  <td>{row.origin?.locationCode}</td>
                  <td>{row.destination?.locationCode}</td>
                  <td>{[...(row.operatingDays || [])].sort().join(", ")}</td>
                  <td>
                    <button type="button" className="btn" onClick={() => startEdit(row)}>
                      Edit
                    </button>{" "}
                    <button type="button" className="btn btn-danger" onClick={() => handleDelete(row.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <PaginationBar
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={setPage}
            disabled={loading}
          />
        </>
      )}
    </div>
  );
}
