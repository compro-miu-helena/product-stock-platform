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
