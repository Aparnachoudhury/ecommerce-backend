<img width="1920" height="1080" alt="Screenshot (1508)" src="https://github.com/user-attachments/assets/4817193a-9286-4512-9140-6ceab5643083" />
<img width="1920" height="1080" alt="Screenshot (1509)" src="https://github.com/user-attachments/assets/142adc6b-c6f5-492a-b1c7-c17975d7e0a5" />
<img width="1920" height="1080" alt="Screenshot (1510)" src="https://github.com/user-attachments/assets/c0a0a178-e5de-4981-8287-078e8818b720" />
<img width="1920" height="1080" alt="Screenshot (1513)" src="https://github.com/user-attachments/assets/82239535-4387-4976-9a8d-fc355323b511" />
<img width="1920" height="1080" alt="Screenshot (1514)" src="https://github.com/user-attachments/assets/51b08ab1-acba-4a7f-b17d-6bedf171c5d1" />
<img width="1920" height="1080" alt="Screenshot (1515)" src="https://github.com/user-attachments/assets/040b36d8-1d03-4dcc-b38f-e4b403675653" />
<img width="1920" height="1080" alt="Screenshot (1508)" src="https://github.com/user-attachments/assets/9fc8bcba-3e52-431f-85ed-3410504a9bda" />
# 🛒 EcomVeda — Full Stack Ecommerce System

> A production-inspired ecommerce platform built from scratch to understand how real-world systems like Amazon work — including authentication, multi-vendor onboarding, Redis-powered cart, real-time order tracking, and payment integration.

---

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven&logoColor=white)

---

## 📌 Why I Built This

Most developers use ready-made platforms for ecommerce. I wanted to understand what happens under the hood — how JWT authentication works at the filter level, how Redis makes cart operations 10x faster than a database, how multi-vendor approval flows are designed, and how transactional order placement prevents data inconsistency.

This project is my answer to those questions.

---

## 🚀 Tech Stack

### Backend
- Java 17<img width="1920" height="1080" alt="Screenshot (1512)" src="https://github.com/user-attachments/assets/f8a314ce-60da-489d-a633-6bef9b4961a8" />
<img width="1920" height="1080" alt="Screenshot (1511)" src="https://github.com/user-attachments/assets/07d75c2c-dad5-45a5-8847-1e1480158e59" />

- Spring Boot
- Spring Security + JWT
- PostgreSQL
- Redis (Cart & Wishlist)
- Flyway (DB migrations)

### Frontend
- React (Vite)
- Axios API integration

### Payment
- Razorpay Integration
- 
---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based access control (ADMIN, VENDOR, CUSTOMER)
- Secure password hashing
- Custom JWT filter

### 🏪 Vendor System
- Vendor onboarding with approval flow
- KYC status: PENDING → APPROVED / REJECTED
- Vendors can manage products after approval

### 🛍️ Product & Inventory
- Product management
- Category support
- Inventory tracking

### 🛒 Cart (Redis-Based)
- Ultra-fast cart using Redis
- Guest cart support
- Auto-expiry with TTL
- Cart merge on login

### ❤️ Wishlist
- Stored in Redis (Set)
- O(1) operations

### 📦 Orders
- Create order from cart
- Order lifecycle: PENDING_PAYMENT → PAID → PROCESSING → SHIPPED → DELIVERED

- Cancel support

### 💳 Payment Integration
- Razorpay payment gateway
- Payment verification via backend
- Order status auto-updated to **PAID**

### 🔄 Real-Time Updates
- WebSocket-based order tracking

---


## 📡 API Highlights

### Auth

POST /api/auth/login
POST /api/auth/register


### Products

GET /api/products


### Cart

POST /api/cart/add
GET /api/cart


### Orders

POST /api/orders/create


### Payment

POST /api/payment/verify


---

## ⚡ Performance Highlights

| Feature | Benefit |
|--------|--------|
| Redis Cart | Sub-millisecond operations |
| JWT Auth | Stateless & scalable |
| Flyway | Reliable DB migrations |
| Transactional Orders | Prevents data inconsistency |

---

## 🗂️ Project Structure


ecommerce/
│
├── src/ # Spring Boot backend
├── logicveda-frontend/ # React frontend
├── docker-compose.yml # (Future use)
├── Dockerfile # (Future use)


---

## ⚙️ Local Setup

### Prerequisites
- Java 17+
- PostgreSQL
- Redis
- Node.js

---

### Backend Setup

```bash
git clone https://github.com/Aparnachoudhury/ecommerce-backend.git
cd ecommerce-backend

Update application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password

---

Run backend:

mvn spring-boot:run

Frontend Setup:
cd logicveda-frontend
npm install
npm run dev
Redis
redis-server

---

📊 Current Status

Module	Status
Authentication	✅ Complete
Vendor System	✅ Complete
Cart (Redis)	✅ Complete
Orders	✅ Complete
Payment (Razorpay)	✅ Complete
WebSocket	✅ Complete
Docker	🚧 Planned
CI/CD	🚧 Planned
Cloud Deploy	🚧 Planned

---

🚀 Future Enhancements
Docker containerization
Kubernetes deployment
CI/CD with GitHub Actions
AWS / GCP deployment
Email notifications
Microservices architecture
🎥 Demo

👉 (Add your demo video link here)


💡 Key Learnings:
Deep understanding of Spring Security filter chain
Redis data structures for real-time systems
Transaction handling in order systems
Payment gateway integration flow
Full-stack system design

👩‍💻 Author
Aparna Choudhury

Building real systems to understand how the web works 🚀

⭐ If you like this project, consider giving it a star!
