# MySQL migrations

- `baseline/`: frozen current-product `B` baseline used only for new databases; never regenerate it after release.
- `history/`: immutable migrations released before the structured layout.
- `ddl/`: new versioned schema changes; do not include data changes.
- `dml/`: new versioned data changes; do not include schema changes.

Flyway orders every migration by version across these directories. Directory order is not execution order.
Never rename or edit a migration after it has been released.
