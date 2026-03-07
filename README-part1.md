# Product Stock Microservices Platform (Lab 8 Part 1)

## Goal
Instrument `api-gateway`, `product-service`, and `stock-service` so requests are exported to Zipkin and the end-to-end call chain is visible in the Zipkin UI.

## Changes made

Tracing dependencies were added to all three services:

- `io.micrometer:micrometer-tracing-bridge-otel`
- `io.opentelemetry:opentelemetry-exporter-zipkin`

`product-service` also includes:

- `io.github.openfeign:feign-micrometer:13.6`

Tracing configuration was added so each service:

- samples every request with `management.tracing.sampling.probability=1.0`
- exports spans to `http://localhost:9411/api/v2/spans`

`product-service` also enables Feign observation:

```properties
spring.cloud.openfeign.micrometer.enabled=true
```

## Files changed

- `api-gateway/pom.xml`
- `api-gateway/src/main/resources/application.yml`
- `product-service/pom.xml`
- `product-service/src/main/resources/application.properties`
- `stock-service/pom.xml`
- `stock-service/src/main/resources/application.properties`

## Run order

Zipkin is already running on port `9411`, so start the applications:

1. Start `stock-service`
2. Start `product-service`
3. Start `api-gateway`
4. Call `http://localhost:8080/product/1`

Expected sample response:

```json
{"productNumber":1,"name":"Laptop","numberOnStock":40}
```

## Verify in Zipkin

1. Open `http://localhost:9411/zipkin`
2. Click `Run Query`
3. Open a trace for the `GET /product/1` request

Expected trace flow:

- `api-gateway`
- `product-service`
- `stock-service`

The dependencies view should show the gateway calling `product-service`, and `product-service` calling `stock-service`.
