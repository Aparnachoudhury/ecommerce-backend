# 🛒 Ecommerce Backend (Spring Boot)

## 📌 Overview

This is a backend project built using **Spring Boot** and **PostgreSQL**.  
It is part of my learning journey to build a full-stack ecommerce system.

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

### 🔐 Day 2
* User, Vendor, and Role-based system
* Role enum (CUSTOMER, VENDOR, ADMIN, SUPPORT)
* One-to-One relationship (Vendor ↔ User)
* Spring Security configuration
* Password encryption using Argon2
* Custom UserDetailsService (database-based authentication)
* Role hierarchy (ADMIN > VENDOR > CUSTOMER)

---

## 🔜 Upcoming (Day 3)

* User authentication (Signup & Login APIs)
* JWT Token generation
* JWT Authentication filter
* Secure REST APIs

---

## 📂 Project Structure

* `entity` → Database models
* `repository` → Database access layer
* `service` → Business logic
* `security` → Security configuration

---

## 👩‍💻 Author

**Aparna Choudhury**
