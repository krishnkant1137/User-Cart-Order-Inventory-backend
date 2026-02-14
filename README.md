📄 Project Documentation
User → Cart → Order → Inventory Backend Flow
1️⃣ Project Objective

The objective of this project is to build a production-level backend flow for an e-commerce system.

The system supports:

Add products to cart

Place order with stock validation

Prevent duplicate orders

Apply discount

Maintain inventory

Use caching

Use proper layering

Use JPA auditing

The focus is not only functionality but also clean architecture and production-ready design.

2️⃣ High-Level Architecture

The project follows domain-based layered architecture.

Each module is separated:

user

product

cart

order

inventory

discount

common

exception

config

Each module contains:

entity

repository

service

controller (where required)

This structure improves scalability and maintainability.

3️⃣ BaseEntity and Auditing

We created a BaseEntity class with:

createdAt

updatedAt

JPA Auditing is enabled using @EnableJpaAuditing.

All entities extend BaseEntity.

This ensures automatic tracking of record creation and update time.

Purpose:
To maintain audit history without writing manual timestamp logic.

4️⃣ Product Module

Product entity contains:

id

name

price

stock

active (soft delete support)

Stock is used during order placement.

Soft delete is used instead of physical delete to maintain data integrity.

5️⃣ User Module

User entity contains:

id

name

email (unique)

active flag

Currently authentication is not implemented.
UserId is passed in request for simplicity.

6️⃣ Cart Module

Cart flow is designed properly.

One user can have only one ACTIVE cart.

Cart contains:

user reference (ManyToOne)

status (ACTIVE / ORDERED)

list of CartItems

CartItem contains:

cart reference

product reference

quantity

Important Design Decisions:

We do not validate stock during add to cart.

Stock is validated only during order placement.

Cart is not deleted after order. It is marked as ORDERED.

Reason:
To preserve history and maintain clean business flow.

7️⃣ Order Module

Order module is the core of the system.

Order contains:

user reference

orderReference (idempotency key)

totalAmount

discountAmount

finalAmount

status

list of OrderItems

OrderItem stores snapshot data:

productId

productName

price

quantity

totalPrice

Reason:
Product price may change later.
Order should preserve original purchase data.

8️⃣ Idempotency Handling

Order API requires an Idempotency-Key in request header.

If same key is used again:

System returns already created order

Duplicate order is not created

This prevents accidental double order due to network retry.

9️⃣ Atomic Stock Deduction

To prevent race condition, stock is deducted using atomic update query:

UPDATE product
SET stock = stock - quantity
WHERE id = productId AND stock >= quantity


If no row is updated:

StockNotAvailableException is thrown.

This prevents overselling in concurrent environment.

🔟 Discount Logic

Discount is handled by separate DiscountService.

Currently:

10% discount applied if totalAmount > 5000

Reason:
Business logic should be separated from order logic.

1️⃣1️⃣ Transaction Management

OrderService is annotated with @Transactional.

If any step fails:

Stock deduction

Order save

Cart update

Everything is rolled back automatically.

This ensures consistency.

1️⃣2️⃣ Inventory Module

Inventory module provides:

Check stock API

Update stock API

Caching is implemented using ConcurrentHashMap.

Design decision:
Task required in-memory caching.

Future Improvement:
This can be replaced with Redis for distributed environment.

Caching layer is designed in a way that it can be replaced without changing business logic.

1️⃣3️⃣ Key Production Concepts Used

Domain-based architecture

DTO instead of entity exposure

Atomic DB operation

Idempotency handling

Transaction management

Enum for status handling

Soft delete pattern

Snapshot order item design

In-memory caching

Proper layering

1️⃣4️⃣ Future Enhancements

Replace in-memory cache with Redis

Add authentication using JWT

Add pagination in order history

Add payment integration

Add distributed locking if required

Conclusion

This project is designed with production-level thinking.

Focus was not only on making APIs work,
but on building a clean, scalable, and safe backend architecture.