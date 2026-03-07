# Product Stock Microservices Platform (Lab 8 Part 4)

## Goal

Create a Spring Cloud Config Server and externalize the runtime configuration for:

- `api-gateway`
- `product-service`
- `stock-service`

## What changed

Added a new module:

- `config-server`

Added a Git-backed config repository folder:

- `config-repo/api-gateway.yml`
- `config-repo/product-service.properties`
- `config-repo/stock-service.properties`

These files now hold the settings that were previously embedded in the three applications, including:

- service ports
- Consul registration settings
- gateway routes
- Zipkin tracing settings
- product-service circuit breaker settings

The three client applications now keep only:

- `spring.application.name`
- `spring.config.import=optional:configserver:http://localhost:8888`

## GitHub location

The config server defaults to your GitHub repo:

```properties
spring.cloud.config.server.git.uri=https://github.com/compro-miu-helena/CS590.git
spring.cloud.config.server.git.search-paths=product-stock-microservices-platform/config-repo
```

For local verification, the same values can be overridden with environment variables such as `CONFIG_GIT_URI`, `CONFIG_LABEL`, and `CONFIG_SEARCH_PATHS`.

## How I verified it

1. Build `config-server`, `stock-service`, `product-service`, and `api-gateway`
2. Start `config-server` against the local Git checkout
3. Start `stock-service`
4. Start `product-service`
5. Start `api-gateway`
6. Call `http://localhost:8080/product/1`

Expected response:

```json
{"productNumber":1,"name":"Laptop","numberOnStock":40}
```

That confirms the services still run correctly while loading their configuration through the config server.
