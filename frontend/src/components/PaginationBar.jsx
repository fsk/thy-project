import React from "react";
import "./PaginationBar.css";

/** page: 0-based index (Spring Data Page.number) */
export default function PaginationBar({ page, totalPages, totalElements, onPageChange, disabled }) {
  const safeTotalPages = Math.max(totalPages, 1);
  const atFirst = page <= 0;
  const atLast = totalPages <= 0 || page >= totalPages - 1;

  return (
    <div className="pagination-bar">
      <button type="button" className="btn" disabled={disabled || atFirst} onClick={() => onPageChange(page - 1)}>
        Previous
      </button>
      <span className="pagination-info">
        Page {page + 1} / {safeTotalPages}
        <span className="pagination-total"> ({totalElements} total)</span>
      </span>
      <button type="button" className="btn" disabled={disabled || atLast} onClick={() => onPageChange(page + 1)}>
        Next
      </button>
    </div>
  );
}
