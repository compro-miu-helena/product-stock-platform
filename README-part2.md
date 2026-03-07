# Product Stock Microservices Platform (Lab 8 Part 2)

## Goal

Implement Mongo-backed command and query services for products and stock, and keep the product read
model in sync when either product data or stock data changes.

## Changes made

`product-service` now contains:

- `ProductCommandService` for add, update, and delete product operations
- `ProductQueryService` for read operations
- `ProductCommand` documents stored in the `product-command-service` collection
- `ProductView` documents stored in the `product-query-service` collection

`stock-service` now contains:

- `StockCommandService` for add, update, delete, and read stock operations
- `Stock` documents stored in the `stock-command-service` collection

The public controllers are:

- `POST /products`
- `PUT /products/{productNumber}`
- `DELETE /products/{productNumber}`
- `GET /products`
- `GET /products/{productNumber}`
- `POST /stock`
- `PUT /stock/{productNumber}`
- `DELETE /stock/{productNumber}`
- `GET /stock/{productNumber}`

## Synchronization strategy

The query side returned by `ProductQueryService` contains:

- `productNumber`
- `name`
- `price`
- `numberInStock`

That is maintained in two ways:

1. `ProductCommandService` updates the product query collection whenever product data is added or changed.
2. `StockCommandService` calls `PUT /internal/products/{productNumber}/stock` on `product-service`
   whenever stock changes.

This keeps product reads current even though product writes and stock writes live in different
services and different Mongo collections.

## Mongo collections

- `product-command-service`
- `product-query-service`
- `stock-command-service`

## Main files

- `product-service/src/main/java/lab/productservice/service/ProductCommandService.java`
- `product-service/src/main/java/lab/productservice/service/ProductQueryService.java`
- `product-service/src/main/java/lab/productservice/controller/ProductCommandController.java`
- `product-service/src/main/java/lab/productservice/controller/ProductQueryController.java`
- `product-service/src/main/java/lab/productservice/controller/ProductProjectionSyncController.java`
- `stock-service/src/main/java/lab/stockservice/service/StockCommandService.java`
- `stock-service/src/main/java/lab/stockservice/StockController.java`

## Manual verification

1. Start MongoDB on port `27017`
2. Start `config-server`
3. Start `product-service`
4. Start `stock-service`
5. Call `POST http://localhost:8901/products`

Request body:

```json
{"productNumber":1001,"name":"Monitor","price":249.99}
```

6. Call `PUT http://localhost:8900/stock/1001`

Request body:

```json
{"productNumber":1001,"quantity":12}
```

7. Call `GET http://localhost:8901/products/1001`

Expected response:

```json
{"productNumber":1001,"name":"Monitor","price":249.99,"numberInStock":12}
```

8. Update the product with `PUT http://localhost:8901/products/1001`

```json
{"productNumber":1001,"name":"4K Monitor","price":299.99}
```

9. Call `GET http://localhost:8901/products/1001` again

Expected response:

```json
{"productNumber":1001,"name":"4K Monitor","price":299.99,"numberInStock":12}
```
