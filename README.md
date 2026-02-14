# 🛒 E-Commerce Backend Flow (User → Cart → Order → Inventory)

## 📌 Project Overview

This project is a production-level backend implementation of an e-commerce order flow.

It includes:

- User management
- Cart management
- Order placement
- Atomic stock validation
- Duplicate order prevention (Idempotency)
- Discount calculation
- Inventory management
- In-memory caching
- JPA auditing
- Proper layered architecture

The focus of this project is clean architecture and production-ready backend design.

---

## 🏗 Architecture Design

The project follows **domain-based layered architecture**:

com.krishnkant.inventorybackendflow
│
├── user
├── product
├── cart
├── order
├── inventory
├── discount
├── common
├── exception
├── config


Each module contains:
- entity
- repository
- service
- controller (where required)

---

## 🔄 High-Level Flow

1. User adds product to cart
2. User places order
3. System validates stock atomically
4. Discount is applied
5. Order is saved
6. Cart is marked as ORDERED
7. Inventory is updated

---

## 🧱 Core Features

### 1️⃣ Cart System

- One ACTIVE cart per user
- Multiple CartItems per cart
- Cart status: ACTIVE / ORDERED
- Stock is NOT validated during add-to-cart

---

### 2️⃣ Order System

- Order contains snapshot of product details
- Uses unique `orderReference` (Idempotency-Key)
- Prevents duplicate order creation
- Uses `@Transactional` for consistency

---

### 3️⃣ Atomic Stock Validation

To prevent race conditions, stock is deducted using an atomic database update:

```sql
UPDATE product
SET stock = stock - :quantity
WHERE id = :productId AND stock >= :quantity
If no row is updated → stock is insufficient.

This prevents overselling.

4️⃣ Idempotency Handling
Order API requires:

Idempotency-Key (Request Header)
If the same key is used again:

Existing order is returned

Duplicate order is not created

5️⃣ Discount Service
Discount logic is separated into DiscountService.

Current Rule:

10% discount if totalAmount > 5000

This keeps business logic modular.

6️⃣ Inventory Module
Provides:

Check stock API

Update stock API (admin)

Uses:

ConcurrentHashMap
for in-memory caching.

⚠ Future improvement: Can be replaced with Redis for distributed systems.

7️⃣ Auditing
All entities extend BaseEntity.

Fields:

createdAt

updatedAt

Enabled using:

@EnableJpaAuditing
8️⃣ Exception Handling
Custom exceptions implemented:

CartNotFoundException

StockNotAvailableException

DuplicateOrderException

EmptyCartException

Global exception handler returns structured error responses.

🔐 Transaction Management
OrderService uses @Transactional.

If any step fails:

Stock deduction

Order save

Cart update

Everything is rolled back.

🧠 Production Concepts Used
Domain-based modular structure

DTO instead of entity exposure

Atomic DB operations

Idempotency pattern

Transaction management

Enum-based state handling

Snapshot order item design

In-memory caching abstraction

Clean logging

Soft delete pattern

🚀 Future Enhancements
Replace in-memory cache with Redis

Add JWT authentication

Add pagination for order history

Add payment integration

Add distributed locking if needed

📌 Technologies Used
Java

Spring Boot

Spring Data JPA

MySQL

Lombok

ConcurrentHashMap (Caching)

📖 Conclusion
This project is designed with production-level thinking.

The goal was not only to make APIs work,
but to build a scalable, safe, and maintainable backend architecture.