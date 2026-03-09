# Product Stock Microservices Platform

Spring Boot microservices project for product, stock, configuration, routing, and Kafka-based messaging exercises.

## Services

- `product-service`
- `stock-service`
- `stock-service-2`
- `api-gateway`
- `config-server`
- `config-repo`

The platform uses MongoDB for persistence, Consul for discovery/load balancing, and Kafka for the `Order` messaging labs.

## Current Docs

- [SEQUENCE-DIAGRAMS.md](SEQUENCE-DIAGRAMS.md) for Mermaid sequence diagrams
- [docs/history/README-part1.md](docs/history/README-part1.md) for tracing and stock-to-product synchronization
- [docs/history/README-part2.md](docs/history/README-part2.md) for the Mongo-backed command/query implementation
- [docs/history/README-part3.md](docs/history/README-part3.md) for the Spring Cloud Contract milestone
- [docs/history/README-part4.md](docs/history/README-part4.md) for Spring Cloud Config Server with Git-backed configuration

## Run Notes

- Start Kafka with `docker compose up -d kafka` for the Kafka exercises.
- Start `product-service` on `8901` when using `config-server`; otherwise use `8080`.
- `product-service` contains the Kafka `Order` examples for partitions, retries/DLT, batching, transactions, and integration tests.
