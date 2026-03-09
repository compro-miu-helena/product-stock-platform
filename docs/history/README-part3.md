# Product Stock Microservices Platform (Lab 8 Part 3)

## Goal

Add Spring Cloud Contract producer and consumer examples:

- `evenoddservice` publishes contracts and generated stubs
- `mathservice` consumes those stubs in integration tests

## Producer: `evenoddservice`

This module implements three contract-backed endpoints:

- `GET /validate?number1=2&number2=2` returns `Even`
- `GET /validate?number1=1&number2=1` returns `Odd`
- `GET /add?value1=2&value2=5` returns `7`

Key files:

- `evenoddservice/src/test/resources/contracts/*.groovy`
- `evenoddservice/src/test/java/lab/evenoddservice/BaseTestClass.java`
- `evenoddservice/src/main/java/lab/evenoddservice/EvenOddController.java`
- `evenoddservice/src/main/java/lab/evenoddservice/CalculatorController.java`

The final `validate` contract uses two request parameters, and the controller returns `Even` only when both are even.

## Consumer: `mathservice`

`mathservice` exposes:

- `GET /calculate?number=<n>`

Internally it calls the producer contract endpoint as:

- `/validate?number1=<n>&number2=<n>`

That keeps the consumer API unchanged while matching the updated producer stubs.

Key files:

- `mathservice/src/main/java/lab/mathservice/MathController.java`
- `mathservice/src/main/java/lab/mathservice/EvenOddClient.java`
- `mathservice/src/test/java/lab/mathservice/MathControllerIntegrationTest.java`

## How to verify

1. Run `mvn install` inside `evenoddservice`
2. Confirm generated tests pass and the stubs are installed locally
3. Run `mvn test` inside `mathservice`
4. Confirm the consumer tests pass using the local producer stubs

Expected consumer behavior:

- `/calculate?number=2` returns `Even`
- `/calculate?number=1` returns `Odd`
