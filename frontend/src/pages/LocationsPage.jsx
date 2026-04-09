import axios from "axios";
import React, { useCallback, useEffect, useState } from "react";
import PaginationBar from "../components/PaginationBar";
import { api } from "../api";
import "./Pages.css";

const emptyForm = { name: "", country: "", city: "", locationCode: "" };
const PAGE_SIZE = 10;

export default function LocationsPage() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/api/locations", { params: { page, size: PAGE_SIZE } });
      const body = res.data;
      const d = body?.data;
      setItems(d?.content || []);
      setTotalPages(d?.totalPages ?? 0);
      setTotalElements(d?.totalElements ?? 0);
    } catch (e) {
      const body = axios.isAxiosError(e) ? e.response?.data : undefined;
      const status = axios.isAxiosError(e) ? e.response?.status : "?";
      setError(body?.errorMessage || `List not found (${status})`);
      setItems([]);
    }
    setLoading(false);
  }, [page]);

  useEffect(() => {
    load();
  }, [load]);

  function startEdit(row) {
    setEditingId(row.id);
    setForm({
      name: row.name,
      country: row.country,
      city: row.city,
      locationCode: row.locationCode,
    });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(emptyForm);
  }

  async function handleCreate(e) {
    e.preventDefault();
    try {
      await api.post("/api/locations", form);
      setForm(emptyForm);
      if (page === 0) load();
      else setPage(0);
    } catch (err) {
      const body = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(body?.errorMessage || body?.message || `Error ${status}`);
    }
  }

  async function handleUpdate(e) {
    e.preventDefault();
    if (!editingId) return;
    try {
      await api.put(`/api/locations/${editingId}`, form);
      cancelEdit();
      load();
    } catch (err) {
      const body = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(body?.errorMessage || `Error ${status}`);
    }
  }

  async function handleDelete(id) {
    if (!confirm("Are you sure you want to delete this location?")) return;
    try {
      await api.delete(`/api/locations/${id}`);
      if (items.length === 1 && page > 0) {
        setPage((p) => p - 1);
      } else {
        load();
      }
    } catch (err) {
      const body = axios.isAxiosError(err) ? err.response?.data : undefined;
      const status = axios.isAxiosError(err) ? err.response?.status : "?";
      alert(body?.errorMessage || `Error ${status}`);
    }
  }

  return (
    <div className="page">
      <h2>Locations</h2>

      <form className="card form-grid" onSubmit={editingId ? handleUpdate : handleCreate}>
        <h3 style={{ margin: 0, gridColumn: "1 / -1" }}>{editingId ? "Edit" : "New location"}</h3>
        <label>
          Name
          <input
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
            maxLength={150}
          />
        </label>
        <label>
          Country
          <input
            value={form.country}
            onChange={(e) => setForm({ ...form, country: e.target.value })}
            required
            maxLength={100}
          />
        </label>
        <label>
          City
          <input
            value={form.city}
            onChange={(e) => setForm({ ...form, city: e.target.value })}
            required
            maxLength={100}
          />
        </label>
        <label>
          Code
          <input
            value={form.locationCode}
            onChange={(e) => setForm({ ...form, locationCode: e.target.value })}
            required
            maxLength={10}
          />
        </label>
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

      {loading && <p>Loading…</p>}
      {error && <p className="error-text">{error}</p>}

      {!loading && !error && (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th>City</th>
                <th>Country</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {items.map((row) => (
                <tr key={row.id}>
                  <td>{row.locationCode}</td>
                  <td>{row.name}</td>
                  <td>{row.city}</td>
                  <td>{row.country}</td>
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
