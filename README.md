# Product Stock Microservices Platform

Spring Boot microservices project for a product catalog and stock inventory system.

## Current architecture

The repository currently contains:

- `product-service` for product writes and product reads
- `stock-service` and `stock-service-2` for stock writes and stock lookups
- `api-gateway` for service routing
- `config-server` and `config-repo` for externalized configuration
- Consul-based service discovery and load balancing
- MongoDB-backed command/query persistence

## Current APIs

`product-service` exposes:

- `POST /products`
- `PUT /products/{productNumber}`
- `DELETE /products/{productNumber}`
- `GET /products`
- `GET /products/{productNumber}`

`stock-service` exposes:

- `POST /stock`
- `PUT /stock/{productNumber}`
- `DELETE /stock/{productNumber}`
- `GET /stock/{productNumber}`

## Data model

The services use separate Mongo collections:

- `product-command-service`
- `product-query-service`
- `stock-command-service`

`ProductQueryService` returns:

- `productNumber`
- `name`
- `price`
- `numberInStock`

When product data changes, `product-service` updates its command collection and read projection.
When stock data changes, `stock-service` updates its own collection and synchronizes the
`numberInStock` field in the product read model through an internal product-service endpoint.

## Repository guide

This repository currently includes the work captured in:

- `README-part1.md` for tracing and the current stock-to-product sync flow
- `README-part2.md` for the Mongo-backed command/query implementation
- `README-part3.md` for Spring Cloud Contract producer and consumer modules
- `README-part4.md` for Spring Cloud Config Server with Git-backed configuration

## Kafka orders example

`product-service` now includes a Kafka-based `Order` example with:

- a producer that publishes 5 sample `Order` messages
- a topic configured with 3 partitions
- 3 listeners, each pinned to one partition
- log output that prints the offset and partition for each consumed message
- two producer modes: same key for every order, or a unique key per order using `orderNumber`

Run Kafka with:

```bash
docker compose up -d kafka
```

Then run `product-service`. It will publish the 5 sample orders on startup using the same key for every order, so all messages should land on the same partition.

To publish the same 5 orders again with the same key:

```bash
curl -X POST http://localhost:8901/orders/publish-same-key
```

To publish the 5 orders with unique keys (`orderNumber`) and observe distribution across partitions:

```bash
curl -X POST http://localhost:8901/orders/publish-unique-keys
```

## Kafka retry and DLT examples

`product-service` also includes two error-handling examples for `Order` consumers:

- `DefaultErrorHandler`: retries 2 times, then sends the failed message to a DLT
- `@RetryableTopic`: retries 2 times, then sends the failed message to a DLT

To trigger the `DefaultErrorHandler` flow:

```bash
curl -X POST http://localhost:8901/orders/publish-default-error-handler-failure
```

To trigger the `@RetryableTopic` flow:

```bash
curl -X POST http://localhost:8901/orders/publish-retryable-failure
```

If you start `product-service` without `config-server`, use port `8080` instead.
