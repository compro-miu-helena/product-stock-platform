# Product Stock Microservices Platform

Spring Boot microservices project for a product catalog and stock inventory system with:

- `product-service` for product details
- `stock-service` and `stock-service-2` for inventory availability
- `api-gateway` for unified routing
- Consul-based service discovery and load balancing

This repository currently includes the work captured in:

- `README-part1.md` for Lab 8 Part 1 Zipkin tracing across gateway, product, and stock services
- `README-part2.md` for the circuit breaker on the product-to-stock remote call
- `README-part3.md` for Spring Cloud Contract producer and consumer modules
- `README-part4.md` for Spring Cloud Config Server with Git-backed configuration
