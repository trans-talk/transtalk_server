# 🚀 Transtalk-Server

`transtalk-server` is the backend server for a real-time chat application, focusing on communication features.

It provides core functionalities such as user authentication and authorization, real-time message transmission, data management, and external service integration, ensuring a stable and scalable service.

<br>

## 🔗 Deployment Link

[Transtalk Live Demo](https://transtalk.vercel.app/login)

<br>

## 🧱 Tech Stack

### Back-end
<div align="start">
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">  
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">  
  <img src="https://img.shields.io/badge/Spring Data JPA-007396?style=for-the-badge&logo=hibernate&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  
  <img src="https://img.shields.io/badge/H2 Database-003B57?style=for-the-badge&logo=h2&logoColor=white">
  <br/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">  
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white">  
  <img src="https://img.shields.io/badge/DeepL-0077FF?style=for-the-badge&logo=deepl&logoColor=white">
</div>

<br>

## 📦 Package Structure

**java**

```java
transtalk-server/
├── src/main/java/com/wootech/transtalk
│   ├── client/                    # External API Client
│   ├── config/                    # Configuration
│   ├── controller/                # REST API Controller
│   ├── dto/                       # Data Transfer Object
│   ├── entity/                    # Entity
│   ├── interceptor/               # Interceptor
│   ├── repository/                # Repository
│   ├── exception/                 # Exception Handling
│   ├── service/                   # Business Logic
│   └── TranstalkApplication       # Main Class
├── src/main/resources/
│   ├── application.properties     # Configuration File
└── build.gradle
```

## ✨ Key Features

- **Real-time Chat System**: Real-time message sending and receiving using WebSocket and STOMP.
- **User Authentication and Authorization**: Secure access control using Spring Security and JWT-based token management.
- **User Management**: Account management functions such as user signup, login, and withdrawal.
- **Database Management**: Data storage and management via MySQL (initial JPA) and MongoDB (chat data migration).
- **DeepL API Integration**: Integration with AI-based features (e.g., translation services) (inference).
- **Automated Deployment**: Efficient deployment using Docker and GitHub Actions.
- **Refresh Token Management**: Issuance and reissuance of Refresh Tokens using Redis.

<br>

## 🧾 API Documentation

For more detailed API information, please refer to the Notion document below:

[**Transtalk API Documentation**](api.md)

<br>

## 🔗 ERD (Entity-Relationship Diagram)

The relationships between database tables can be viewed through the ERD below:

<img width="800" height="578" alt="Image" src="https://github.com/user-attachments/assets/8a9196a4-5766-4464-aa74-c270aacea7f8" />

### Redis Key Design

This section defines the key patterns and purposes for data stored in Redis.

| Key Type          | Key Pattern                                         | Description                                                                 | Value Type | Expiration Policy                   |
|---|---|---|---|---|
| Refresh Token     | `refresh:{userId}:{refreshTokenValue}`<br>(e.g., `refresh:user123:abcDEfGHIjkl...`) | Stores valid Refresh Tokens for Access Token re-issuance.                     | String     | Matches Refresh Token's validity (e.g., 2 weeks) |
| JWT Blacklist (JTI) | `bl:jti:{jtiValue}`<br>(e.g., `bl:jti:a1b2c3d4e5f6...`) | Stores JTI of invalidated Access Tokens to prevent reuse after logout or compromise. | String     | Matches Access Token's validity (e.g., 30 mins)  |

**Notes:**

*   **`{userId}`**: Actual user ID.
*   **`{refreshTokenValue}`**: The Refresh Token string.
*   **`{jtiValue}`**: The `jti` (JWT ID) claim value from the JWT.

<br>

## 💬 Development Conventions

### 🚀 Git Flow

> This project follows the Git Flow branching strategy.
> 
- Main
    - Production-ready code.
    - Updated only through merging release or hotfix branches.
    - No direct commits allowed.
- develop
    - Integration branch for ongoing development.
    - All completed features are merged here before release preparation.
- feature/
    - For implementing individual features or tasks.
    - Branched from: develop
    - Merged back into: develop via PR
    - Naming: feature/<feature-name>
- release/
    - For preparing a new production release.
    - Branched from: develop
    - Merged into: main and develop
    - Naming: release/<version>
- hotfix/
    - For urgent fixes on the production environment.
    - Branched from: develop
    - Merged into: develop
    - Naming: hotfix/<issue-name>

<br>

## 👥 Role Assignment

The main responsibilities for the Transtalk-Server project are as follows:

| Name        | Key Responsibilities                                                                                                                                                                                                                                                                           |
|---|---|
| TaeSeon Yoo | - **Chatroom & Participant Domains**: Designed and implemented core structures.<br>- **WebSockets**: Set up real-time communication.<br>- **Chat Messaging**: Implemented sending and storage using JPA.<br>- **Refactoring & Migration**: Led chat message refactoring and migrated from JPA to MongoDB.<br>- **Translation Integration**: Integrated external translation services using DeepL API.<br>- **CI/CD**: Automated deployment workflows with GitHub Actions. |
| HoSoo Lee   | - **Authentication & Authorization**: Implemented comprehensive authentication and authorization using Spring Security, JWT, Google Social Login, Redis blacklist, and Refresh Token mechanisms.<br>- **WebSocket Integration**: Configured JWT and Spring Security settings for secure real-time communication via WebSockets.<br>- **Chat Messaging (MongoDB)**: Implemented chat message sending, storage, and retrieval by configuring MongoDB.<br>- **Cloud Deployment**: Led application deployment utilizing AWS EC2, RDS, and MongoDB Cloud services. |