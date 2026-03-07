# Product Stock Microservices Platform (Lab 8 Part 2)

## Goal
Add a circuit breaker around the remote call from `product-service` to `stock-service` so `product-service` can return a fallback value when the stock endpoint is unavailable.

## Changes made

`product-service` now includes:

- `org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j`

The Feign call is no longer made directly from the controller. It now flows through:

- `ProductController`
- `ProductCatalogService`
- `StockLookupService`

`StockLookupService` wraps the remote call through Spring Cloud CircuitBreaker:

```java
circuitBreakerFactory.create("stockService")
        .run(() -> stockClient.getStock(productNumber), throwable -> -1);
```

If `stock-service` is unavailable, the fallback returns `-1` for `numberOnStock`.

## Circuit breaker configuration

The breaker is configured in `product-service/src/main/resources/application.properties` to open quickly for demo/testing:

```properties
resilience4j.circuitbreaker.instances.stockService.sliding-window-size=2
resilience4j.circuitbreaker.instances.stockService.minimum-number-of-calls=2
resilience4j.circuitbreaker.instances.stockService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.stockService.wait-duration-in-open-state=30s
```

## Test added

Automated verification was added in:

- `product-service/src/test/java/lab/productservice/service/ProductCatalogServiceCircuitBreakerTest.java`

The test forces the Feign client to fail, verifies that:

- the fallback response returns `numberOnStock = -1`
- after two failures the breaker opens
- the third call is short-circuited and does not call the Feign client again

## Manual verification

1. Start `product-service`
2. Leave `stock-service` stopped, or stop it after startup
3. Call `http://localhost:8901/product/1`

Expected fallback response:

```json
{"productNumber":1,"name":"Laptop","numberOnStock":-1}
```

After repeated failed requests, the circuit opens and `product-service` stops attempting the remote stock call until the open-state wait period expires.
