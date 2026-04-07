# 🛒 Ecommerce Backend (Spring Boot)

## 📌 Overview

This is a backend project built using **Spring Boot** and **PostgreSQL**.
It is part of my learning journey to build a full-stack ecommerce system with secure authentication and scalable architecture.

---

## 🚀 Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* Spring Security
* PostgreSQL
* Maven

---

## ✅ Features

### 🟢 Day 1

* Project setup using Spring Boot
* PostgreSQL database integration
* User entity creation
* Automatic table generation using JPA

---

### 🔐 Day 2

* User, Vendor, and Role-based system
* Role enum (CUSTOMER, VENDOR, ADMIN, SUPPORT)
* One-to-One relationship (Vendor ↔ User)
* Spring Security configuration
* Password encryption using Argon2
* Custom UserDetailsService (database-based authentication)
* Role hierarchy (ADMIN > VENDOR > CUSTOMER)

---

### 🚀 Day 3

* User Authentication APIs (Signup & Login)
* JWT Token generation & validation
* JWT Authentication filter
* Refresh Token mechanism
* Email verification token system
* Rate limiting filter for API protection
* Secure REST APIs with Spring Security

---

## 📂 Project Structure

* `entity` → Database models
* `repository` → Database access layer
* `service` → Business logic
* `controller` → REST APIs
* `security` → JWT & security configuration
* `dto` → Request & response objects

---

## 🔐 Security Highlights

* JWT-based authentication
* Refresh token support
* Password encryption (Argon2)
* Role-based authorization
* API rate limiting

---

## 🔜 Upcoming (Day 4)

* Product management APIs
* Cart & order system
* Payment integration
* API documentation (Swagger)

---

## 👩‍💻 Author

**Aparna Choudhury**
Backend Developer | Spring Boot Enthusiast 🚀
