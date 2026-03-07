# Product Stock Microservices Platform (Lab 8 Part 1)

## Goal

Instrument the services so requests are exported to Zipkin and the stock-to-product synchronization
flow is visible in the Zipkin UI.

## Changes made

Tracing dependencies were added to the runtime services:

- `io.micrometer:micrometer-tracing-bridge-otel`
- `io.opentelemetry:opentelemetry-exporter-zipkin`

Services that use Feign also include:

- `io.github.openfeign:feign-micrometer:13.6`

Tracing configuration was added so each service:

- samples every request with `management.tracing.sampling.probability=1.0`
- exports spans to `http://localhost:9411/api/v2/spans`

Feign-based services also enable observation with:

```properties
spring.cloud.openfeign.micrometer.enabled=true
```

## Files changed

- `product-service/pom.xml`
- `product-service/src/main/resources/application.properties`
- `stock-service/pom.xml`
- `stock-service/src/main/resources/application.properties`

## Current trace scenario

The current code no longer performs a product-to-stock lookup during a product read. The cross-service
flow now happens when stock changes are pushed into the product read model.

Start the applications:

1. Start MongoDB on port `27017`
2. Start `config-server`
3. Start `product-service`
4. Start `stock-service`
5. Call `POST http://localhost:8901/products`

Request body:

```json
{"productNumber":1,"name":"Laptop","price":1499.99}
```

6. Call `PUT http://localhost:8900/stock/1`

Request body:

```json
{"productNumber":1,"quantity":40}
```

7. Call `GET http://localhost:8901/products/1`

Expected response:

```json
{"productNumber":1,"name":"Laptop","price":1499.99,"numberInStock":40}
```

## Verify in Zipkin

1. Open `http://localhost:9411/zipkin`
2. Click `Run Query`
3. Open a trace for the `PUT /stock/1` request

Expected trace flow:

- `stock-service`
- `product-service`

That trace shows `stock-service` handling the stock command and then calling the internal product
projection endpoint so the read model stays current.
