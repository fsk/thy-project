from __future__ import annotations

import os
import uuid
from collections.abc import Iterator

import psycopg2
from psycopg2.extensions import connection as PgConnection
from psycopg2.extras import RealDictCursor

from fake_data_generator.data import LOCATIONS, TRANSPORTATIONS

DAY_NUM_TO_HIBERNATE: dict[int, str] = {
    1: "MONDAY",
    2: "TUESDAY",
    3: "WEDNESDAY",
    4: "THURSDAY",
    5: "FRIDAY",
    6: "SATURDAY",
    7: "SUNDAY",
}


def connection_kwargs() -> dict[str, object]:
    return {
        "host": os.environ.get("DB_HOST", "localhost"),
        "port": int(os.environ.get("DB_PORT", "5434")),
        "dbname": os.environ.get("DB_NAME", "thy"),
        "user": os.environ.get("DB_USER", "thy"),
        "password": os.environ.get("DB_PASSWORD", "thy"),
    }


def clear_route_tables(conn: PgConnection) -> None:
    with conn.cursor() as cur:
        cur.execute("DELETE FROM transportation_operating_days")
        cur.execute("DELETE FROM transportations")
        cur.execute("DELETE FROM locations")
    conn.commit()


def seed_locations(conn: PgConnection) -> dict[str, uuid.UUID]:
    code_to_id: dict[str, uuid.UUID] = {}
    with conn.cursor() as cur:
        for loc in LOCATIONS:
            lid = uuid.uuid4()
            code_to_id[loc.code] = lid
            cur.execute(
                """
                INSERT INTO locations (id, name, country, city, location_code)
                VALUES (%s, %s, %s, %s, %s)
                """,
                (str(lid), loc.name, loc.country, loc.city, loc.code),
            )
    conn.commit()
    return code_to_id


def seed_transportations(
    conn: PgConnection, code_to_id: dict[str, uuid.UUID]
) -> None:
    with conn.cursor() as cur:
        for t in TRANSPORTATIONS:
            try:
                oid = code_to_id[t.origin_code]
                did = code_to_id[t.destination_code]
            except KeyError as e:
                raise ValueError(f"Unknown location_code: {e.args[0]}") from e
            tid = uuid.uuid4()
            cur.execute(
                """
                INSERT INTO transportations (
                    id, origin_location_id, destination_location_id, transportation_type
                )
                VALUES (%s, %s, %s, %s)
                """,
                (str(tid), str(oid), str(did), t.transportation_type),
            )
            for day_num in t.operating_days:
                if day_num not in DAY_NUM_TO_HIBERNATE:
                    raise ValueError(
                        f"Invalid operating day: {day_num} (must be 1-7, Monday-Sunday)"
                    )
                day_name = DAY_NUM_TO_HIBERNATE[day_num]
                cur.execute(
                    """
                    INSERT INTO transportation_operating_days (transportation_id, operating_day)
                    VALUES (%s, %s)
                    """,
                    (str(tid), day_name),
                )
    conn.commit()


def run_seed(*, clear_first: bool = False) -> None:
    kwargs = connection_kwargs()
    conn = psycopg2.connect(**kwargs)
    try:
        if clear_first:
            clear_route_tables(conn)
        code_to_id = seed_locations(conn)
        seed_transportations(conn, code_to_id)
    finally:
        conn.close()


def verify_counts(conn: PgConnection) -> Iterator[tuple[str, int]]:
    queries = [
        ("locations", "SELECT COUNT(*) AS c FROM locations"),
        ("transportations", "SELECT COUNT(*) AS c FROM transportations"),
        (
            "transportation_operating_days",
            "SELECT COUNT(*) AS c FROM transportation_operating_days",
        ),
    ]
    with conn.cursor(cursor_factory=RealDictCursor) as cur:
        for label, q in queries:
            cur.execute(q)
            row = cur.fetchone()
            assert row is not None
            yield label, int(row["c"])
