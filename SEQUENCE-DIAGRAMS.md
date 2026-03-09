# Sequence Diagrams
Assumptions used in the diagrams:

- `Config Server` reads external configuration from `Git`
- services register themselves in `Registry`
- `API Gateway` routes client traffic to backend services through service discovery
- tracing data is sent to `Zipkin`
- logs are shipped to `Logstash`, then indexed in `ElasticSearch`, and viewed in `Kibana`
- `Authorization Server` is available before client-facing traffic starts
- `Orders` calls `Products` through Feign + Load Balancer + Circuit Breaker

## Startup Sequence

This diagram shows the recommended startup order and the main calls each service performs while starting.

```mermaid
sequenceDiagram
    autonumber
    participant Git
    participant Config as Config Server
    participant Reg1 as Registry 1
    participant Reg2 as Registry 2
    participant Zipkin
    participant Logstash
    participant ES as ElasticSearch
    participant Kibana
    participant Auth as Authorization Server
    participant Gateway as API Gateway
    participant Shopping
    participant Products
    participant Customers
    participant Orders


    Config->>Git: Load external configuration repository
    Git-->>Config: Return config files

    Reg1->>Config: Fetch registry configuration
    Config-->>Reg1: Return config
    Reg1->>Reg2: Establish registry peer replication
    Reg2-->>Reg1: Replication acknowledged

    Reg2->>Config: Fetch registry configuration
    Config-->>Reg2: Return config
    Reg2->>Reg1: Establish registry peer replication
    Reg1-->>Reg2: Replication acknowledged

    ES->>Config: Fetch logging configuration
    Config-->>ES: Return config
    Kibana->>ES: Connect to search/index backend
    ES-->>Kibana: Connection ready

    Logstash->>Config: Fetch logging pipeline configuration
    Config-->>Logstash: Return config
    Logstash->>ES: Connect output pipeline
    ES-->>Logstash: Pipeline ready

    Zipkin->>Config: Fetch tracing configuration
    Config-->>Zipkin: Return config

    Auth->>Config: Fetch auth configuration
    Config-->>Auth: Return config
    Auth->>Reg1: Register instance
    Reg1-->>Auth: Registration OK

    Shopping->>Config: Fetch service configuration
    Config-->>Shopping: Return config
    Shopping->>Reg1: Register instance
    Reg1-->>Shopping: Registration OK
    Shopping->>Zipkin: Connect tracing exporter
    Zipkin-->>Shopping: Tracing ready
    Shopping->>Logstash: Ship startup logs
    Logstash->>ES: Index logs

    Products->>Config: Fetch service configuration
    Config-->>Products: Return config
    Products->>Reg1: Register instance
    Reg1-->>Products: Registration OK
    Products->>Zipkin: Connect tracing exporter
    Zipkin-->>Products: Tracing ready
    Products->>Logstash: Ship startup logs
    Logstash->>ES: Index logs

    Customers->>Config: Fetch service configuration
    Config-->>Customers: Return config
    Customers->>Reg1: Register instance
    Reg1-->>Customers: Registration OK
    Customers->>Zipkin: Connect tracing exporter
    Zipkin-->>Customers: Tracing ready
    Customers->>Logstash: Ship startup logs
    Logstash->>ES: Index logs

    Orders->>Config: Fetch service configuration
    Config-->>Orders: Return config
    Orders->>Reg1: Register instance
    Reg1-->>Orders: Registration OK
    Orders->>Zipkin: Connect tracing exporter
    Zipkin-->>Orders: Tracing ready
    Orders->>Logstash: Ship startup logs
    Logstash->>ES: Index logs

    Gateway->>Config: Fetch gateway configuration
    Config-->>Gateway: Return config
    Gateway->>Reg1: Discover available services/routes
    Reg1-->>Gateway: Return service registry data
    Gateway->>Auth: Load auth / token validation metadata
    Auth-->>Gateway: Return auth metadata
    Gateway->>Reg1: Register gateway instance
    Reg1-->>Gateway: Registration OK
    Gateway->>Zipkin: Connect tracing exporter
    Zipkin-->>Gateway: Tracing ready
    Gateway->>Logstash: Ship startup logs
    Logstash->>ES: Index logs
```

## placeOrder Scenario

This diagram shows the call flow when the client places an order and `Orders` updates stock in `Products`.

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Gateway as API Gateway
    participant Auth as Authorization Server
    participant Registry
    participant Orders
    participant Products
    participant Zipkin
    participant Logstash
    participant ES as ElasticSearch

    Client->>Gateway: POST /orders/placeOrder(...)
    Gateway->>Auth: Validate token / authorization
    Auth-->>Gateway: Token valid

    Gateway->>Registry: Resolve Orders service instance
    Registry-->>Gateway: Orders instance
    Gateway->>Orders: Forward placeOrder(...)

    Orders->>Zipkin: Start trace span
    Orders->>Logstash: Write request log
    Logstash->>ES: Index log entry

    Note over Orders,Products: Orders uses Feign + Load Balancer + Circuit Breaker
    Orders->>Registry: Resolve Products service instance
    Registry-->>Orders: Products instance
    Orders->>Products: Update available stock amount

    Products->>Zipkin: Start trace span
    Products->>Logstash: Write stock update log
    Logstash->>ES: Index log entry

    alt Stock update succeeds
        Products-->>Orders: Stock updated successfully
        Orders->>Orders: Persist order / mark successful
        Orders-->>Gateway: Order placed successfully
        Gateway-->>Client: 200/201 success response
    else Stock update fails
        Products-->>Orders: Error / timeout / insufficient stock
        Orders->>Orders: Circuit breaker / fallback / reject order
        Orders-->>Gateway: Order failed
        Gateway-->>Client: 4xx/5xx error response
    end

    Orders->>Zipkin: Finish trace span
    Products->>Zipkin: Finish trace span
```
