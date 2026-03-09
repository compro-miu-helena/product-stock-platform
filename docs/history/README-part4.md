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
- MongoDB connection settings
- Feign metrics settings for services that synchronize data across service boundaries

The three client applications now keep only:

- `spring.application.name`
- `spring.config.import=optional:configserver:http://localhost:8888`

## GitHub location

The config server defaults to your GitHub repo:

```properties
spring.cloud.config.server.git.uri=https://github.com/compro-miu-helena/CS590.git
spring.cloud.config.server.git.search-paths=product-stock-microservices-platform/config-repo
```

For local verification, the same values can be overridden with environment variables such as
`CONFIG_GIT_URI`, `CONFIG_LABEL`, and `CONFIG_SEARCH_PATHS`.

## How I verified it

1. Build `config-server`, `stock-service`, `product-service`, and `api-gateway`
2. Start `config-server` against the local Git checkout
3. Start MongoDB on port `27017`
4. Start `stock-service`
5. Start `product-service`
6. Call `POST http://localhost:8901/products`

```json
{"productNumber":1,"name":"Laptop","price":1499.99}
```

7. Call `PUT http://localhost:8900/stock/1`

```json
{"productNumber":1,"quantity":40}
```

8. Call `GET http://localhost:8901/products/1`

Expected response:

```json
{"productNumber":1,"name":"Laptop","price":1499.99,"numberInStock":40}
```

That confirms the services still run correctly while loading their configuration through the config
server, including the MongoDB settings used by the command and query services.
