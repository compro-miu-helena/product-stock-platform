# Product Stock Microservices Platform (Lab 8 Part 2)

## Goal

Change `ProductCommandService` to use event sourcing while keeping the same external behavior as Part 1.

## What changed

`stock-service` still behaves the same as before:

- `StockCommandService` stores stock documents in `stock-command-service`
- stock updates still synchronize `numberInStock` into the product read model

`product-service` now uses an event-sourced write model:

- `ProductCommandService` appends `ProductEvent` documents instead of overwriting a single product document
- events are stored in the `product-command-service` Mongo collection
- the current product state is rebuilt by replaying the event stream for a `productNumber`
- `ProductQueryService` still reads from `product-query-service`

The event types are:

- `CREATED`
- `UPDATED`
- `DELETED`

## Behavior that stayed the same

The public API did not change:

- `POST /products`
- `PUT /products/{productNumber}`
- `DELETE /products/{productNumber}`
- `GET /products`
- `GET /products/{productNumber}`
- `POST /stock`
- `PUT /stock/{productNumber}`
- `DELETE /stock/{productNumber}`
- `GET /stock/{productNumber}`

The query result also stays the same:

- `productNumber`
- `name`
- `price`
- `numberInStock`

When product data changes, the query projection is updated.
When stock data changes, the stock service still pushes the new quantity into the product projection.

## Event sourcing flow

For product writes, `ProductCommandService` now:

1. Loads all events for the `productNumber`
2. Rebuilds the current product state from those events
3. Validates whether the command is allowed
4. Appends a new event with the next sequence number
5. Updates the query projection so reads continue to work exactly as before

Deletes no longer remove command records from Mongo. Instead, a `DELETED` event is appended and the
read projection is removed.

## Mongo collections

- `product-command-service` stores product events
- `product-query-service` stores the current product read model
- `stock-command-service` stores stock documents

## Main files

- `product-service/src/main/java/lab/productservice/service/ProductCommandService.java`
- `product-service/src/main/java/lab/productservice/model/ProductEvent.java`
- `product-service/src/main/java/lab/productservice/model/ProductEventType.java`
- `product-service/src/main/java/lab/productservice/repository/ProductCommandRepository.java`
- `product-service/src/main/java/lab/productservice/service/ProductQueryService.java`
- `stock-service/src/main/java/lab/stockservice/service/StockCommandService.java`

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

8. Call `PUT http://localhost:8901/products/1001`

```json
{"productNumber":1001,"name":"4K Monitor","price":299.99}
```

9. Call `GET http://localhost:8901/products/1001`

Expected response:

```json
{"productNumber":1001,"name":"4K Monitor","price":299.99,"numberInStock":12}
```

10. Call `DELETE http://localhost:8901/products/1001`

11. Confirm that:

- `GET http://localhost:8901/products/1001` returns `404`
- the `product-command-service` collection still contains the event history for `1001`
- the last product event for `1001` is `DELETED`
