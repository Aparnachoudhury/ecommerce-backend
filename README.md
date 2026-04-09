# 🛒 Ecommerce Backend (Spring Boot)

## 🚀 Overview

This is a **real-world inspired ecommerce backend system** built using **Spring Boot** and **PostgreSQL**.

Instead of just using platforms like Amazon or Flipkart, I built my own backend to understand how such systems work internally — including authentication, vendor onboarding, and order management.

---

## 🧰 Tech Stack

* **Java**
* **Spring Boot**
* **Spring Security (JWT Authentication)**
* **PostgreSQL**
* **Flyway (Database Migrations)**
* **Maven**

---

## 🔐 Features

### ✅ Authentication & Security

* JWT-based authentication
* Secure password hashing using **Argon2**
* Stateless session management
* Custom JWT filter implementation

### 👥 Role-Based Access Control

* Roles: **ADMIN, VENDOR, CUSTOMER, SUPPORT**
* Endpoint-level protection
* Role hierarchy support

### 🏪 Vendor System

* Vendor onboarding flow
* KYC status:

  * `PENDING`
  * `APPROVED`
  * `REJECTED`
* Admin approval/rejection APIs
* Vendor approval queue system

### 🛍️ Ecommerce Core

* Product & Category management
* Product Variants (with JSON attributes support)
* Inventory management system

### 📦 Orders & Payments

* Order creation & tracking
* Order items structure
* Payment handling system

### 🗄️ Database & Migrations

* Version-controlled schema using **Flyway**
* Clean relational database design
* Multiple migration files (V1 → V5)

---

## 📡 API Endpoints

### 🔓 Public APIs

* `POST /api/auth/signup`
* `POST /api/auth/login`

### 🔒 Protected APIs (Require JWT)

* `GET /api/products`
* `POST /api/vendor/apply`
* `POST /api/admin/vendors/{id}/approve`
* `POST /api/admin/vendors/{id}/reject`

👉 Add header:

```id="h0h0yf"
Authorization: Bearer <your_token>
```

---

## ⚙️ Setup Instructions

### 1️⃣ Clone Repository

```bash id="b7i3hq"
git clone https://github.com/Aparnachoudhury/ecommerce-backend.git
cd ecommerce-backend
```

### 2️⃣ Configure Database

Update `application.properties`:

```properties id="v8z2b1"
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

---

### 3️⃣ Run Application

```bash id="6p2g7z"
mvn spring-boot:run
```

---

## 🧪 Testing (Postman)

### Signup

```http id="3c5x6l"
POST /api/auth/signup
```

### Login

```http id="7d8f9k"
POST /api/auth/login
```

👉 Copy JWT token and use in headers for protected APIs.

---

## 💡 Key Learnings

* Built JWT authentication from scratch
* Understood Spring Security filter chain deeply
* Designed scalable relational database schema
* Implemented multi-vendor ecommerce architecture
* Worked with Flyway migrations for production-ready DB versioning

---

## 🚀 Future Improvements

* Payment gateway integration (Stripe/Razorpay)
* Order tracking & notifications
* Docker containerization
* Microservices architecture
* Frontend integration (React)

---

## 👩‍💻 Author

**Aparna Choudhury**

---

## ⭐ If you like this project

Give it a ⭐ on GitHub!
