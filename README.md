# BajriX Marketplace

A simple marketplace where buyers compare seller offers and sellers manage only their own listings.

## Folders

- `backend` - Java Spring Boot REST API
- `frontend` - React + Vite user interface
- `docker-compose.yml` - PostgreSQL database
- `database/schema.sql` - table creation SQL for learning and manual setup
- `database/data.sql` - sample seller, product, and listing data
- `database/queries.sql` - simple PostgreSQL queries for learning and testing

## Run the project

1. Start PostgreSQL: `docker compose up -d`
2. Start the backend: `cd backend` then `mvn spring-boot:run`
3. Start the frontend: `cd frontend`, `npm install`, then `npm run dev`
4. Open `http://localhost:5173` (the backend runs on port `8081`)

The frontend uses seller `1` as the logged-in seller. Change the seller in the dropdown to test that each seller can only update its own listings.

Open `database/queries.sql` to see the same database operations written as plain SQL.

If PostgreSQL uses a password other than `postgres`, set `DB_PASSWORD` to that same password before starting Docker Compose and the backend. For example, in PowerShell run `$env:DB_PASSWORD = "your-local-password"` before `docker compose up -d`, then keep that PowerShell session (or set the same variable in the backend terminal) while starting Spring Boot. `DB_URL` and `DB_USERNAME` can also override the backend connection settings.

## Verify

- Backend integration tests: run `mvn test` from `backend`. Tests use an isolated in-memory H2 database and exercise buyer search/offer visibility, listing creation and updates, seller ownership, stop-selling, validation, and seller approval status changes.
- Frontend production build: run `npm run build` from `frontend`.

## Database

`docker compose up -d` starts PostgreSQL with database name `bajrix_marketplace`, username `postgres`, and password `postgres`.

When you run the backend, Spring Boot creates or updates the tables and inserts sample data into an empty database automatically. The schema setting is `update` so restarting the application does not drop existing marketplace data. For manual database practice, use `database/schema.sql`, then run `database/data.sql`, and finally use `database/queries.sql`.

## Main decisions

- A product is shared catalogue data. A seller listing belongs to one seller and one product.
- Only listings from `APPROVED` sellers, with `available=true` and stock greater than zero, are visible to buyers.
- Active listings cannot have a minimum order larger than their available stock; temporarily out-of-stock or explicitly unavailable listings may retain their MOQ.
- The seller approval screen can move sellers between `APPROVED`, `PENDING`, and `REJECTED`; it is a demonstration workflow and is not protected by real admin authentication.
- The database has a unique constraint on seller + product, so duplicate listings are not allowed.
- `@Version` on listings gives simple optimistic locking when two updates happen at the same time.
- This challenge uses `X-Seller-Id` instead of real authentication. The service checks the listing owner before every change.

## Not included

Checkout, payment, real login/role-based access control, image upload, and advanced discovery infrastructure are intentionally out of scope.

## Scaling later

Add PostgreSQL indexes, full-text search or Elasticsearch, Redis caching, object storage for images, proper authentication, and separate read/search services when product and listing counts grow.
