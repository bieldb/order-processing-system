# Phase 1 — Java Core and Domain Modeling

## 1. Overview

Phase 1 was the foundation of the Order Processing System.

The main objective was not to build a complete application yet, but to understand and implement the core of the system using plain Java. Before introducing Spring Boot, databases, REST APIs, or other frameworks, the focus was on creating a well-defined domain and making sure that the main business rules worked correctly.

At this stage, the application runs entirely in memory. Orders are stored using Java collections, and the behavior of customers, products, order items, and orders is handled by the domain classes themselves.

The idea behind this phase was simple:

> Before worrying about frameworks and infrastructure, make sure the business domain itself makes sense.

---

# 2. What Was Built

During this phase, the main domain objects were created:

* `Customer`
* `Product`
* `OrderItem`
* `Order`
* `OrderStatus`

An `OrderService` was also created to handle order creation and retrieval.

The project also includes custom exceptions and a set of tests covering the main business scenarios.

The resulting structure is approximately:

```text
domain/
├── Customer
├── Product
├── Order
├── OrderItem
└── OrderStatus

service/
└── OrderService

exception/
├── InsufficientStockException
├── InvalidOrderStatusException
└── OrderNotFoundException

tests/
├── Customer-related scenarios
├── ProductTest
├── OrderItemTest
├── OrderTest
└── OrderServiceTest
```

---

# 3. Domain Modeling

One of the main goals of Phase 1 was to model the problem using objects that represent real concepts from an order processing system.

Instead of putting everything inside one large class, responsibilities were divided between the domain objects.

The basic relationship is:

```text
Customer
   │
   │ places
   ▼
 Order
   │
   │ contains
   ▼
OrderItem
   │
   │ references
   ▼
Product
```

This structure makes the domain easier to understand and gives each class a clear responsibility.

---

# 4. Customer

The `Customer` class represents the person who owns an order.

It currently contains:

```text
id
name
email
```

The ID is represented using `UUID`.

The class is intentionally simple because there are currently no complex business rules associated with customers in Phase 1.

---

# 5. Product and Inventory

The `Product` class represents an item that can be purchased.

It contains:

```text
id
name
price
stock
```

One important decision in this phase was to avoid allowing the stock to be modified arbitrarily.

Instead of exposing a method such as:

```java
product.setStock(5);
```

the domain provides operations that describe what is actually happening:

```java
product.increaseStock(quantity);
product.decreaseStock(quantity);
```

This makes the intention clearer and gives the `Product` object control over its own rules.

For example, when decreasing stock, the product checks whether the requested quantity is actually available.

If the requested quantity is greater than the current stock, an `InsufficientStockException` is thrown.

This means the following situation is prevented:

```text
Stock = 10
Request = 15

10 - 15 = -5
```

The domain does not allow the product to reach an invalid state.

The tests cover increasing stock, decreasing stock, reaching zero stock, invalid quantities, and insufficient stock.

---

# 6. OrderItem

An `OrderItem` represents a product inside an order.

It contains:

```text
product
quantity
unitPrice
```

There is an important detail here.

When the `OrderItem` is created, the current product price is copied into `unitPrice`.

For example:

```text
Product price: $20.00

OrderItem:
quantity = 3
unitPrice = $20.00
```

The subtotal is then:

```text
20.00 × 3 = 60.00
```

This value is calculated by the `OrderItem` itself.

The project uses `BigDecimal` for monetary values instead of floating-point types, avoiding the typical precision problems associated with floating-point arithmetic.

The class also validates its input.

A product cannot be null, and the quantity must be greater than zero.

---

# 7. Order

The `Order` class is the central object of the domain.

An order contains:

```text
id
customer
items
status
createdAt
```

When a new order is created, it automatically starts with:

```text
status = CREATED
```

and its creation time is recorded.

The order also starts with an empty list of items.

Items are added through:

```java
order.addItem(item);
```

rather than directly manipulating the internal list.

The order also calculates its total based on all of its items.

Conceptually:

```text
Order
 ├── Item 1 → $20.00
 ├── Item 2 → $15.00
 └── Item 3 → $10.00

Total → $45.00
```

The calculation is implemented using Java Streams and `BigDecimal`.

---

# 8. Order Lifecycle

One of the most important parts of Phase 1 was implementing the order lifecycle.

An order has four possible states:

```text
CREATED
PROCESSING
COMPLETED
CANCELLED
```

These states are not arbitrary. There are rules defining which transitions are allowed.

The valid flow is:

```text
CREATED
   │
   ├── process() ──> PROCESSING
   │                    │
   │                    ├── complete() ──> COMPLETED
   │                    │
   │                    └── cancel() ────> CANCELLED
   │
   └── cancel() ───────> CANCELLED
```

For example:

```java
order.process();
```

changes the order from:

```text
CREATED → PROCESSING
```

Then:

```java
order.complete();
```

changes it from:

```text
PROCESSING → COMPLETED
```

The domain also prevents invalid transitions.

For example:

```text
COMPLETED → PROCESSING
COMPLETED → CANCELLED
CANCELLED → PROCESSING
```

are not allowed.

When an invalid transition is attempted, an `InvalidOrderStatusException` is thrown.

This is important because the rule belongs to the order itself. Any part of the application that has access to an `Order` should not be able to put it into an invalid state.

---

# 9. Encapsulation

Encapsulation was one of the main concepts practiced during this phase.

The goal was to avoid treating domain objects as simple containers of data.

For example, instead of having external code directly manipulate the order status, the `Order` provides operations that represent valid business actions:

```java
order.process();
order.complete();
order.cancel();
```

The same idea is applied to inventory:

```java
product.increaseStock(quantity);
product.decreaseStock(quantity);
```

This approach makes the code easier to reason about because the object is responsible for protecting its own state.

Another example is the order's item collection.

The internal list is not returned directly. A copy is returned instead:

```java
return List.copyOf(items);
```

This prevents external code from modifying the internal collection without going through the `Order`.

---

# 10. Exception Handling

Specific exceptions were created for business problems.

The current exceptions are:

```text
InsufficientStockException
InvalidOrderStatusException
OrderNotFoundException
```

For example, an insufficient stock situation is represented by:

```java
throw new InsufficientStockException("Insufficient stock");
```

instead of using a generic exception for everything.

This makes the code more expressive and will become even more useful in later phases when these exceptions are eventually translated into HTTP responses.

---

# 11. In-Memory Storage

Since Phase 1 does not use a database, orders are stored in memory.

The `OrderService` uses:

```java
Map<UUID, Order>
```

implemented with:

```java
HashMap
```

The reason for using a `HashMap` is that the system frequently needs to find an order by its ID.

A lookup such as:

```java
orders.get(orderId);
```

has an average time complexity of:

```text
O(1)
```

This was an opportunity to connect the domain implementation with another important topic from this phase: choosing data structures based on how the application needs to access its data.

---

# 12. OrderService

The `OrderService` is responsible for operations involving the collection of orders.

The main operations implemented are:

```java
createOrder(...)
findOrderById(...)
findOrdersByCustomer(...)
```

Creating an order consists of creating the `Order`, adding its items, storing it in the `HashMap`, and returning it.

Finding an order by ID uses the `HashMap`.

Finding orders belonging to a specific customer is different.

The current implementation goes through the stored orders and filters them based on the customer's ID.

Therefore, if there are `N` orders, this operation has a time complexity of approximately:

```text
O(N)
```

This is acceptable for the current in-memory implementation and, more importantly, provided a practical example of how the choice of data structure affects performance.

---

# 13. Testing

Testing was also part of Phase 1.

The tests were intentionally implemented using plain Java rather than a testing framework.

The current tests are executed through `main()` methods and contain custom assertion methods.

The main test classes cover:

### OrderItem

* Correct subtotal calculation
* Zero quantity
* Negative quantity
* Null product

### Order

* Initial `CREATED` status
* `CREATED → PROCESSING`
* `PROCESSING → COMPLETED`
* `CREATED → CANCELLED`
* `PROCESSING → CANCELLED`
* Invalid cancellation
* Invalid processing

### Product

* Increasing stock
* Invalid stock increase
* Decreasing stock
* Reaching zero stock
* Invalid stock decrease
* Insufficient stock

### OrderService

* Creating an order
* Finding an order by ID
* Finding orders by customer

The tests focus on behavior rather than simply checking getters and setters.

---

# 14. What I Learned in Phase 1

The main lesson from this phase was that backend development is not only about making code execute.

A large part of backend development is deciding:

* where a rule should live;
* who should be responsible for changing a state;
* which data structure is appropriate;
* how invalid states should be prevented;
* how objects should interact;
* and how the behavior should be tested.

For example, the rule:

> "An order can only be completed when it is processing"

could technically be implemented anywhere.

However, placing this rule inside `Order.complete()` makes the domain itself responsible for maintaining its state.

That decision makes the system safer and easier to maintain.

---

# 15. Main Concepts Practiced

Throughout Phase 1, the project provided practical experience with:

### Java

* Classes and objects
* Constructors
* Encapsulation
* Enums
* Exceptions
* Generics
* Streams
* `UUID`
* `BigDecimal`
* `LocalDateTime`

### Collections

* `List`
* `ArrayList`
* `Map`
* `HashMap`

### Object-Oriented Design

* Domain modeling
* Composition
* Encapsulation
* Responsibility separation
* State management

### Algorithms

* Hash-based lookup
* Linear search
* Stream filtering
* Time complexity
* Data structure selection

### Testing

* Positive scenarios
* Negative scenarios
* Exception testing
* Behavioral testing
* Custom assertions

---

# 16. Current Architecture

At the end of Phase 1, the application can be represented as:

```text
                 ┌──────────────┐
                 │   Customer   │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │    Order     │
                 │              │
                 │   status     │
                 │   createdAt  │
                 │   items      │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │  OrderItem   │
                 │              │
                 │   quantity   │
                 │   unitPrice  │
                 └──────┬───────┘
                        │
                        ▼
                 ┌──────────────┐
                 │   Product    │
                 │              │
                 │    price     │
                 │    stock     │
                 └──────────────┘

                        ▲
                        │
                 ┌──────┴───────┐
                 │ OrderService  │
                 │               │
                 │   HashMap     │
                 └───────────────┘
```

The current implementation is intentionally simple. There is no HTTP layer and no persistence outside application memory.

---

# 17. Why Phase 1 Comes First

It would have been possible to start the project directly with Spring Boot, JPA, and PostgreSQL.

However, doing that would make it easier to confuse framework features with actual backend concepts.

For example:

```java
JpaRepository
```

can provide database operations automatically, but understanding why a `Map` provides fast lookup is a different skill.

Likewise, Spring can manage dependencies, but understanding why objects should have clear responsibilities is independent of Spring.

Phase 1 therefore establishes the foundation that the next phases will build upon.

---

# 18. Next Step

With the core domain implemented, the project is ready to move beyond an in-memory Java application.

The next phase will introduce:

```text
Spring Boot
      ↓
REST API
      ↓
DTOs
      ↓
Services
      ↓
Spring Data JPA
      ↓
Hibernate
      ↓
PostgreSQL
```

The important part is that the business domain created during Phase 1 will not simply be thrown away.

Instead, the goal of Phase 2 is to place this domain inside a real backend application with HTTP communication and persistent storage.

---

# Phase 1 Status

**Completed.**

The first phase established the core domain and business rules of the Order Processing System using Java 21 and in-memory data structures.

The project is now ready to evolve from a Java domain application into a persistent backend service.
