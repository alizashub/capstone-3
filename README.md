# 🛒 EasyShop 

A Java Spring Boot REST API for an e-commerce application that supports browsing products, managing a shopping cart, checking out orders, and securely handling users and profiles.

This project focuses on **backend workflows** and secure, user-scoped data access.

---

## ✨ Features

### 🗂 Categories & Products
- View all product categories
- Browse products by category
- Search products using optional filters:
   - category
   - price range
   - subcategory
- Admin-only product and category management

---

### 🧺 Shopping Cart
- Each authenticated user has their own shopping cart
- Add products to the cart
- Update product quantities
- Automatically recalculates totals

---

### 📦 Orders & Checkout
- Converts shopping cart into an order
- Creates order line items for each product
- Stores product price at time of purchase

---

### 👤 Users & Profiles
- Secure authentication using Spring Security
- Profiles are separate from authentication data
- Authenticated users can view and update their own profile
- Passwords are never exposed in API responses

---

## 🧠 Project Structure

The backend follows a layered architecture:

Controller → DAO Interface → MySQL DAO → Database

```java

- **Controllers** handle HTTP requests and security
- **DAO Interfaces** define application behavior
- **MySQL DAOs** contain SQL and database logic
- **Models** represent application data only

This structure keeps responsibilities clearly separated and the codebase easy to maintain.

---

## 🌐 Example API Routes

GET /categories
GET /products
GET /products?cat=1&minPrice=10
GET /cart
POST /cart/products/{id}
PUT /cart/products/{id}
POST /orders
GET /profile
PUT /profile

```

## 🧩 Interesting Code Example

Add Code Picture--

### 🔐 Security Highlights
Authentication required for carts, checkout, and profiles

Admin role required for product and category management

User identity is derived from the authentication context

User IDs are never accepted directly from client requests



### 🛠 Tech Stack
Java 17

Spring Boot

Spring Security

JDBC (DAO pattern)

MySQL

Maven

