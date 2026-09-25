# E-Commerce API

A REST API for managing e-commerce products and categories, built with Spring Boot and PostgreSQL.

## Stack

- Java 17
- Spring Boot 3.3 (Web, Data JPA, Validation)
- PostgreSQL

## Running locally

Set the following environment variables (or rely on the defaults, which point at a local PostgreSQL instance):

| Variable      | Default     |
|---------------|-------------|
| `DB_HOST`     | `localhost` |
| `DB_PORT`     | `5432`      |
| `DB_NAME`     | `ecommerce` |
| `DB_USERNAME` | `postgres`  |
| `DB_PASSWORD` | `postgres`  |
| `SERVER_PORT` | `8080`      |

Create the database, then run:

```bash
./mvnw spring-boot:run
```

Tables are created/updated automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

## API

### Categories — `/api/categories`

| Method | Path              | Description          |
|--------|-------------------|-----------------------|
| GET    | `/`               | List all categories  |
| GET    | `/{id}`           | Get a category by id |
| POST   | `/`               | Create a category    |
| PUT    | `/{id}`           | Update a category    |
| DELETE | `/{id}`           | Delete a category    |

Request body (`POST`/`PUT`):

```json
{
  "name": "Electronics",
  "description": "Phones, laptops and accessories"
}
```

### Products — `/api/products`

| Method | Path       | Description                                                                 |
|--------|------------|-------------------------------------------------------------------------------|
| GET    | `/`        | List products, paginated. Query params: `categoryId`, `name`, `page`, `size`, `sort` |
| GET    | `/{id}`    | Get a product by id                                                          |
| POST   | `/`        | Create a product                                                             |
| PUT    | `/{id}`    | Update a product                                                             |
| DELETE | `/{id}`    | Delete a product                                                             |

Request body (`POST`/`PUT`):

```json
{
  "name": "Wireless Mouse",
  "description": "2.4GHz wireless mouse",
  "price": 19.99,
  "stockQuantity": 100,
  "sku": "WM-1001",
  "categoryId": 1
}
```

## Error responses

Errors are returned as a consistent JSON body:

```json
{
  "timestamp": "2026-09-25T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 42",
  "path": "/api/products/42"
}
```
