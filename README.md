# 🩸 HemoGlow
## Secure Blood Donation & Inventory Management System

HemoGlow is a full-stack web application designed to digitally manage blood donation processes, blood inventory tracking, and blood request handling in a secure and structured manner.

The system is built using Spring Boot (Backend), HTML/CSS/JavaScript (Frontend), and MySQL (Database).  
It focuses on real-world healthcare workflows with strong emphasis on security, reliability, and scalability.

---

## 🚀 Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- JWT Authentication
- REST APIs

### Frontend
- HTML
- CSS
- JavaScript

### Database
- MySQL

---

## 🎯 Project Objective

To replace inefficient manual blood bank record systems with a centralized, secure, and role-based digital platform that manages:

- Donor information  
- Blood inventory  
- Blood requests  
- Donation events  
- User profiles  

---

## 🩸 Core Features

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based access control (RBAC)
- Secure Spring Security configuration
- Protected REST endpoints
- Stateless authentication mechanism

### 👥 User Types

#### 1️⃣ Individual Donors
- Register & login
- Manage profile
- Track blood group
- View donation history

#### 2️⃣ Organizations
- Manage blood inventory
- Monitor stock levels
- Handle blood requests
- Organize donation events

### 🏥 Blood Donation Tracking
- Blood group-based tracking
- Donation history maintenance
- Structured donor database management

### 📦 Blood Inventory Monitoring
- Blood group-wise stock management
- Centralized inventory tracking
- Real-time stock updates
- Controlled inventory access

### 📄 Blood Request System
- Create blood requests
- Track request status
- Approve / reject requests
- Inventory-linked request validation

### 📅 Event Management
- Organize blood donation drives
- Manage event records
- Associate donors with events

---

## 🏗️ System Architecture

Frontend (HTML/CSS/JS)
        ↓
REST API Calls
        ↓
Spring Boot Backend
        ↓
Spring Security + JWT
        ↓
MySQL Database

---

## 📂 Backend Structure

hemo-glow/

- controller/        → REST Controllers  
- service/           → Business Logic Layer  
- repository/        → JPA Repository Interfaces  
- entity/            → Database Entities / Models  
- dto/               → Data Transfer Objects  
- config/            → Security & JWT Configuration  
- exception/         → Custom Exception Handling  
- resources/         → application.properties  

---

## ⚙️ Setup & Installation

1. Clone the repository:
   git clone https://github.com/priyanshu20051112/Hemoglow.git
2. Create MySQL database:
   CREATE DATABASE hemoglow;

3. Configure application.properties with your database credentials.

4. Run the application:
   mvn spring-boot:run

---

## 💡 What This Project Demonstrates

- Secure REST API development with Spring Boot
- JWT-based authentication & authorization
- Role-based backend architecture
- Database schema design using MySQL
- Full-stack frontend–backend integration
- Real-world healthcare workflow modeling

---

---

## 🤝 Contributing

HemoGlow is an open-source project, and contributions are welcome!

If you'd like to improve features, fix bugs, enhance security, optimize performance, or add new modules — feel free to contribute.

Let’s build something impactful together. 🩸
---
This project is open-source. 

## 👨‍💻 Author

Priyanshu Upadhyay  
