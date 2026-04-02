from __future__ import annotations

import argparse
import sys

import psycopg2

from fake_data_generator.postgres_seed import connection_kwargs, run_seed, verify_counts


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        description="Case study data: locations + transportations seed."
    )
    p.add_argument(
        "--clear",
        action="store_true",
        help="First clear the locations/transportations/operating_days tables (users are not affected).",
    )
    p.add_argument(
        "--dry-run",
        action="store_true",
        help="Test the connection, print the table counts, but don't write anything.",
    )
    return p


def main() -> None:
    args = build_parser().parse_args()
    if args.dry_run:
        try:
            conn = psycopg2.connect(**connection_kwargs())
            try:
                print("Connection is successful:", connection_kwargs())
                for name, count in verify_counts(conn):
                    print(f"  {name}: {count}")
            finally:
                conn.close()
        except Exception as e:
            print("Connection error:", e, file=sys.stderr)
            sys.exit(1)
        return

    try:
        run_seed(clear_first=args.clear)
    except Exception as e:
        print("Seed error:", e, file=sys.stderr)
        sys.exit(1)

    conn = psycopg2.connect(**connection_kwargs())
    try:
        print("Seed is successful. Record counts:")
        for name, count in verify_counts(conn):
            print(f"  {name}: {count}")
    finally:
        conn.close()


if __name__ == "__main__":
    main()
