# SportsFolio

A comprehensive sports portfolio platform built with Spring Boot, enabling athletes to showcase their achievements, connect with trainers, participate in tournaments, and engage with the sports community.

## Table of Contents
- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Modules](#modules)
- [Features Implemented](#features-implemented)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Getting Started](#getting-started)
- [Pending Features](#pending-features)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

SportsFolio is a modular, multi-module Java Spring Boot application designed to manage user authentication, authorization, domain logic, and data persistence for a sports portfolio platform. The application follows a microservices-inspired architecture with clear separation of concerns across multiple Maven modules.

**Target Users:**
- Athletes of all levels (amateur to professional)
- Trainers and coaches
- Tournament organizers
- Sports enthusiasts

**Core Value Propositions:**
- Build and showcase sports portfolios
- Connect with trainers and other athletes
- Participate in tournaments
- Endorse other athletes
- Track achievements and hall of fame status

---

## Technology Stack

### Backend Framework
- **Spring Boot 3.4.1** - Modern Spring ecosystem
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **Hibernate** - ORM framework

### Authentication & Security
- **JWT (JSON Web Tokens)** - Stateless authentication using JJWT 0.11.5
- **BCrypt** - Password hashing and encryption
- **Spring Security Filters** - Request authentication and authorization

### Database
- **PostgreSQL** - Production database
- **H2** - In-memory database for testing

### Build & Dependencies
- **Maven 3.x** - Build tool and dependency management
- **Java 21** - Language version
- **Lombok** - Reduce boilerplate code

### Validation & Utilities
- **Jakarta Validation API** - Data validation annotations
- **SLF4J with Logback** - Logging framework

---

## Project Architecture

### Modular Design
The application is organized into 5 interdependent Maven modules:

```
SportsFolio (Root)
├── api/              → REST API layer & Controllers
├── auth/             → Authentication & JWT utilities
├── commons/          → Shared utilities, exceptions, enums
├── database/         → Data access layer, repositories, DAOs
└── domain/           → Entity models & DTOs
```

### Design Patterns Used
1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **DTO Pattern** - Data transfer objects for API responses
4. **Global Exception Handler** - Centralized error handling
5. **Dependency Injection** - Spring's IoC container
6. **Builder Pattern** - JWT token generation

---

## Modules

### 1. **domain/** - Entity Models & DTOs
Contains all JPA entity classes and data transfer objects.

**Key Entities:**
- `Users` - Core user entity with profile information
- `UserSports` - Many-to-many relationship between users and sports
- `Post` - User posts/updates with comments and likes
- `Comment` - Comments on posts
- `PostLike` - Likes on posts
- `Endorsement` - Athlete endorsements
- `Tournament` - Tournament information
- `TournamentParticipant` - Tournament participation tracking
- `Trainer` - Trainer profiles
- `Fixtures` - Tournament fixtures
- `HallOfFame` - Hall of fame entries
- `Like` - General like entity
- `Sports` - Sports catalog

**Key DTOs:**
- `AuthRequest` - Login/signup request
- `AuthResponse` - Authentication response with tokens
- `UserDTO` - User profile data
- `PostDTO` - Post data
- `TokenRequest` - Token refresh request

### 2. **auth/** - Authentication & Security
Handles all authentication logic and JWT operations.

**Components:**
- `JwtUtil` - JWT token generation, validation, and claims extraction
- `CustomUserDetailsService` - User details loading for Spring Security
- `SecurityConfig` - Spring Security configuration
- `CorsConfig` - CORS policy configuration
- `JwtAuthenticationFilter` - JWT validation filter

**Features:**
- Generate access and refresh tokens
- Validate tokens and extract claims
- Handle token expiration
- CORS configuration for API access

### 3. **commons/** - Shared Utilities & Exceptions
Provides common functionality used across all modules.

**Exception Classes:**
- `DuplicateResourceException` - Resource already exists
- `UnauthorizedException` - Authentication failures
- `UserNotFoundException` - User not found
- `InvalidPasswordException` - Password validation failure
- `InvalidUsernameException` - Username validation failure
- `InvalidEmailIdException` - Email validation failure
- `ResourceNotFoundException` - General resource not found
- `ValidationException` - General validation errors

**Utilities:**
- `EmailValidator` - Email format validation
- `PasswordValidator` - Password strength validation
- `UsernameValidator` - Username format validation
- `Constants` - Application-wide constants

**Enums:**
- `UserRole` - User roles (USER, ADMIN, TRAINER)

**Global Exception Handler:**
- `GlobalExceptionHandler` - Centralized exception handling with `@ControllerAdvice`
- Returns consistent error responses with timestamp, status, error type, and message

### 4. **database/** - Data Access Layer
Manages database operations and integration tests.

**Repositories (Spring Data JPA):**
- `UserRepository`, `PostRepository`, `CommentRepository`
- `TournamentRepository`, `TrainerRepository`, `SportsRepository`
- `EndorsementRepository`, `PostLikeRepository`, etc.

**DAOs (Data Access Objects):**
- `UsersDAO` - User-specific database operations
- `EndorsementDAO` - Endorsement operations
- `TrainerDAO` - Trainer operations
- `TournamentParticipantDAO` - Tournament participation
- `HallOfFameDAO` - Hall of fame management

**Integration Tests:**
- Database initialization and cleanup
- Entity relationship testing
- Query validation

### 5. **api/** - REST API Layer
Exposes all REST endpoints for client interaction.

**Controllers:**
- `AuthController` - Login, signup, token refresh endpoints
- `UsersController` - User profile management
- `PostController` - Post CRUD operations and news feed
- `CommentController` - Comment management
- `LikeController` - Post and general likes
- `TournamentController` - Tournament management
- `TrainerController` - Trainer profiles
- `SportsController` - Sports catalog
- `AdminController` - Admin operations

**Services:**
- `AuthService` - Authentication business logic
- `UserService` - User management
- `PostService` - Post operations
- `CommentService` - Comment operations
- `LikeService` - Like operations
- Additional services for other domains

**Configuration:**
- `application.yml` - Application configuration
- Security, database, JWT, and logging settings

---

## Features Implemented

### 1. User Authentication & Authorization ✅
**Components:** `AuthController`, `AuthService`, `JwtUtil`, `SecurityConfig`

**Implemented:**
- ✅ User registration/signup with validation
- ✅ User login with credentials
- ✅ JWT token generation (access & refresh tokens)
- ✅ Token validation and claims extraction
- ✅ Token refresh mechanism
- ✅ Password hashing using BCrypt
- ✅ Custom user details loading
- ✅ Role-based access control (USER, ADMIN, TRAINER)
- ✅ Logout endpoint
- ✅ CORS configuration for cross-origin requests
- ✅ OTP Generation on signup
- ✅ Email verification via OTP
- ✅ OTP validation with 10-minute expiration
- ✅ OTP resend functionality
- ✅ Email verification required before login
- ✅ OTP-based password reset functionality

**How It Works:**
1. User provides username, password, and email in signup request
2. `AuthService.signup()` validates input and creates user with `emailVerified = false`
3. `OTPGenerator.generateOTP()` creates a 6-digit random code
4. OTP is saved to database with 10-minute expiration timestamp
5. `EmailService.sendOTPEmail()` sends OTP to user's email via SMTP
6. User receives email and provides OTP in verification request
7. `AuthService.verifyOTP()` validates OTP code and marks user as verified
8. `AuthController.login()` now checks `user.emailVerified` before issuing tokens
9. Only verified users can log in and receive JWT tokens
10. User can call `/auth/resend-otp` if they need a new OTP

**Password Reset Flow:**
1. User clicks "Forgot Password" and provides email
2. `AuthService.forgotPassword()` generates and sends OTP to email
3. User receives OTP and submits it along with new password
4. `AuthService.resetPasswordWithOTP()` validates:
   - OTP is valid and not expired
   - OTP hasn't been used before
   - New password meets strength requirements (8+ chars, upper, lower, digit, special char)
   - Password confirmation matches new password
5. User's password is updated and hashed with BCrypt
6. OTP is marked as verified (one-time use)
7. User can immediately login with new password

### 2. Input Validation & Error Handling ✅
**Components:** `GlobalExceptionHandler`, Validators, Custom Exceptions

**Implemented:**
- ✅ Email format validation (`@Email` annotation, `EmailValidator`)
- ✅ Password strength validation (`PasswordValidator`)
- ✅ Username format validation (`UsernameValidator`)
- ✅ Duplicate user/email detection
- ✅ Global exception handling with `@ControllerAdvice`
- ✅ Structured error responses with timestamp and status codes
- ✅ Custom exception types for different error scenarios
- ✅ HTTP status code mapping (400, 401, 403, 404, 409, 500)
- ✅ Comprehensive logging of errors

**How It Works:**
1. DTOs use validation annotations (`@Email`, `@NotBlank`, etc.)
2. Controllers use `@Valid` annotation on request parameters
3. Validation errors trigger `MethodArgumentNotValidException`
4. `GlobalExceptionHandler` catches all exceptions
5. Returns consistent JSON error response with status code

### 3. User Management ✅
**Components:** `UsersController`, `UserService`, `UserRepository`, `Users` entity

**Implemented:**
- ✅ User profile creation and persistence
- ✅ User profile retrieval
- ✅ User profile updates
- ✅ User follow/unfollow system (Many-to-Many relationship)
- ✅ User role management (USER, ADMIN, TRAINER)
- ✅ Profile picture and bio management
- ✅ Trainer profile flag
- ✅ Refresh token storage and management
- ✅ Automatic timestamp tracking (createdAt, updatedAt)

**How It Works:**
1. User entity has followers/following many-to-many relationship
2. Users can follow other athletes
3. Profile information is persisted in PostgreSQL
4. User role determines access level in system
5. Refresh tokens are stored for session management

### 4. Post & Social Features ✅
**Components:** `PostController`, `PostService`, `Post`, `Comment`, `PostLike` entities

**Implemented:**
- ✅ Create posts with content
- ✅ Post retrieval (single, user's posts, news feed)
- ✅ Comments on posts (One-to-Many relationship)
- ✅ Post likes/reactions
- ✅ Like count on comments
- ✅ Automatic timestamp management
- ✅ Cascade deletion (deleting post deletes comments/likes)

**How It Works:**
1. Authenticated users create posts
2. Posts are associated with author
3. Other users can comment on posts
4. Users can like posts and comments
5. News feed shows posts from followed users (planned)

### 5. Data Persistence & JPA Integration ✅
**Components:** `JPA Entities`, `Repositories`, `Database Module`

**Implemented:**
- ✅ JPA/Hibernate ORM mapping
- ✅ Entity relationships (One-to-Many, Many-to-Many, Many-to-One)
- ✅ LocalDateTime support for timestamps
- ✅ Cascade operations (PERSIST, REMOVE)
- ✅ Lazy loading optimization
- ✅ Unique constraints on username and email
- ✅ Foreign key relationships
- ✅ Automatic timestamp generation (`@CreationTimestamp`, `@PrePersist`, `@PreUpdate`)
- ✅ Spring Data JPA repositories for CRUD operations
- ✅ Custom DAO classes for complex queries
- ✅ PostgreSQL dialect configuration
- ✅ H2 database for testing

### 6. Tournament Management ✅
**Components:** `Tournament`, `TournamentParticipant`, `Fixtures`, `GameStage` enum

**Implemented:**
- ✅ Tournament creation and management
- ✅ Tournament participant tracking
- ✅ Match fixtures management
- ✅ Game stages (GROUP_STAGE, KNOCKOUT, FINALS, etc.)
- ✅ Tournament organizer assignment
- ✅ Sport-specific tournament configuration
- ✅ Tournament dates management

### 7. Endorsement System ✅
**Components:** `Endorsement` entity, `EndorsementDAO`, `EndorsementRepository`

**Implemented:**
- ✅ User endorsement of other athletes
- ✅ Endorsement badges and messages
- ✅ Endorsement timestamp tracking
- ✅ Many-to-One relationships (endorser and endorsed)

### 8. Sports & Trainer Management ✅
**Components:** `Sports`, `Trainer`, `UserSports` entities

**Implemented:**
- ✅ Sports catalog management
- ✅ User sports association (users can be skilled in multiple sports)
- ✅ User level in each sport (BEGINNER, INTERMEDIATE, ADVANCED, PROFESSIONAL)
- ✅ Trainer profiles
- ✅ Trainer association with users

### 9. Hall of Fame ✅
**Components:** `HallOfFame` entity, `HallOfFameDAO`

**Implemented:**
- ✅ Hall of fame entries for notable achievements
- ✅ Achievement tracking and recognition

### 10. Security & Logging ✅
**Components:** `JwtAuthenticationFilter`, `SecurityConfig`, SLF4J Logback

**Implemented:**
- ✅ JWT-based stateless authentication
- ✅ Spring Security integration
- ✅ Request filtering and validation
- ✅ Comprehensive logging of authentication events
- ✅ Error logging for debugging
- ✅ Password hashing with BCrypt

---

## Database Schema

### Key Entities & Relationships

```
Users (1) ──────→ (M) UserSports
  ├─ id (PK)
  ├─ name (UNIQUE)
  ├─ email (UNIQUE)
  ├─ passwordHash
  ├─ bio
  ├─ profilePicture
  ├─ isTrainer
  ├─ userRole
  ├─ refreshToken
  ├─ createdAt
  └─ updatedAt

Users (1) ──────→ (M) Post
  └─ Post
     ├─ id (PK)
     ├─ content
     ├─ author_id (FK)
     ├─ createdAt
     └─ updatedAt

Post (1) ──────→ (M) Comment
  └─ Comment
     ├─ id (PK)
     ├─ content
     ├─ post_id (FK)
     ├─ user_id (FK)
     └─ createdAt

Post (1) ──────→ (M) PostLike
  └─ PostLike
     ├─ id (PK)
     ├─ post_id (FK)
     ├─ user_id (FK)
     └─ createdAt

Users (M) ──────→ (M) Users (Followers/Following)
  └─ user_followers
     ├─ followed_id (FK)
     └─ follower_id (FK)

Users (1) ──────→ (M) Tournament
  └─ Tournament
     ├─ id (PK)
     ├─ name
     ├─ type
     ├─ organizer_id (FK)
     ├─ sports_id (FK)
     ├─ startDate
     ├─ endDate
     └─ createdAt

Tournament (1) ──────→ (M) TournamentParticipant
  └─ TournamentParticipant
     ├─ id (PK)
     ├─ tournament_id (FK)
     ├─ user_id (FK)
     └─ createdAt

Tournament (1) ──────→ (M) Fixtures
  └─ Fixtures
     ├─ id (PK)
     ├─ tournament_id (FK)
     ├─ team1_id (FK)
     ├─ team2_id (FK)
     ├─ matchDate
     └─ gameStage

Users (M) ──────→ (M) Sports
  └─ UserSports
     ├─ id (PK)
     ├─ user_id (FK)
     ├─ sport_id (FK)
     └─ level (BEGINNER, INTERMEDIATE, ADVANCED, PROFESSIONAL)

Users (M) ──────→ (M) Users (Endorsements)
  └─ Endorsement
     ├─ id (PK)
     ├─ endorser_id (FK)
     ├─ endorsed_id (FK)
     ├─ badge
     ├─ message
     └─ createdAt

Users (1) ──────→ (M) Trainer
  └─ Trainer
     ├─ id (PK)
     ├─ user_id (FK)
     ├─ specialization
     └─ bio

Users (1) ──────→ (M) HallOfFame
  └─ HallOfFame
     ├─ id (PK)
     ├─ user_id (FK)
     ├─ achievement
     └─ createdAt
```

---

## API Endpoints

### Authentication Endpoints
```
POST   /auth/login              → User login with credentials (requires email verification)
POST   /auth/signup             → User registration with OTP generation
POST   /auth/verify-otp         → Verify email via OTP code
POST   /auth/resend-otp         → Resend OTP to email
POST   /auth/forgot-password    → Request OTP for password reset
POST   /auth/reset-password     → Reset password using OTP
POST   /auth/refresh            → Refresh access token
POST   /auth/logout             → User logout
```

### User Endpoints
```
GET    /api/users/{id}          → Get user profile
PUT    /api/users/{id}          → Update user profile
GET    /api/users/{id}/posts    → Get user's posts
POST   /api/users/{id}/follow   → Follow a user
DELETE /api/users/{id}/follow   → Unfollow a user
```

### Post Endpoints
```
POST   /api/posts               → Create a post
GET    /api/posts               → Get news feed
GET    /api/posts/{postId}      → Get specific post
GET    /api/posts/user/{username} → Get user's posts
DELETE /api/posts/{postId}      → Delete post
```

### Comment Endpoints
```
POST   /api/comments            → Create comment
GET    /api/comments/{postId}   → Get post comments
DELETE /api/comments/{commentId} → Delete comment
```

### Like Endpoints
```
POST   /api/likes               → Like a post
DELETE /api/likes/{likeId}      → Unlike a post
GET    /api/posts/{postId}/likes → Get post likes
```

### Tournament Endpoints
```
POST   /api/tournaments         → Create tournament
GET    /api/tournaments         → Get all tournaments
GET    /api/tournaments/{id}    → Get tournament details
POST   /api/tournaments/{id}/join → Join tournament
```

### Trainer Endpoints
```
GET    /api/trainers            → Get all trainers
GET    /api/trainers/{id}       → Get trainer profile
```

### Sports Endpoints
```
GET    /api/sports              → Get all sports
POST   /api/user-sports         → Add sport to user profile
```

---

## Configuration

### Environment Variables
```
JWT_SECRET=your_secret_key_that_is_super_secret_and_long_enough_for_jwt_signing
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/sports_folio
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

### Application Properties (application.yml)
```yaml
jwt:
  secret: ${JWT_SECRET:default_secret}
  expiration: 18000000  # 5 hours in milliseconds

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/sports_folio
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        show_sql: true

logging:
  level:
    root: INFO
```

---

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production)
- Git

### Installation & Running

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/SportsFolio.git
   cd SportsFolio
   ```

2. **Configure environment variables**
   ```bash
   export JWT_SECRET="your_super_secret_key_here"
   export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/sports_folio"
   export SPRING_DATASOURCE_USERNAME="postgres"
   export SPRING_DATASOURCE_PASSWORD="postgres"
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   cd api
   ../mvnw spring-boot:run
   ```

5. **Access the API**
   - Base URL: `http://localhost:8080`
   - Test endpoint: `http://localhost:8080/auth/signup`

### Running Tests
```bash
./mvnw test
```

### Building Docker Image
```bash
./mvnw clean package
docker build -t sportsfolio:latest .
docker run -p 8080:8080 sportsfolio:latest
```

---

## Pending Features

### High Priority 🔴
1. **API Documentation**
   - Swagger/OpenAPI integration
   - Endpoint documentation
   - Request/response schemas
   - Authentication documentation

2. **Comprehensive Testing**
   - Unit tests for services
   - Integration tests for controllers
   - Database tests with TestContainers
   - Security tests for JWT validation
   - Edge cases and error scenarios

### Medium Priority 🟡
5. **Enhanced User Features**
   - User profile picture upload to cloud storage (S3/GCS)
   - User bio enrichment
   - User verification badges
   - User activity feed/timeline

6. **Advanced Search & Filtering**
   - Search users by name/sport
   - Filter tournaments by sport/date
   - Search posts by content
   - Advanced filtering with pagination

7. **Notifications System**
   - Follow notifications
   - Comment notifications
   - Like notifications
   - Tournament updates
   - WebSocket integration for real-time notifications

8. **Production-Ready Features**
   - Redis integration for session management and caching
   - JWT token blacklist using Redis
   - Rate limiting on endpoints
   - Database connection pooling optimization
   - Database migration scripts (Flyway)
   - Application monitoring with Micrometer/Prometheus

9. **Performance Optimization**
   - Pagination for list endpoints
   - Caching strategies (HTTP caching, Redis)
   - Database query optimization
   - Lazy loading verification
   - Load testing and optimization

### Low Priority 🟢
10. **DevOps & Deployment**
    - Docker containerization
    - Docker Compose for local development
    - Kubernetes deployment configuration
    - CI/CD pipeline (GitHub Actions/Jenkins)
    - Automated testing in CI/CD

11. **Frontend Integration**
    - Angular/React frontend
    - API client SDK
    - Frontend deployment configuration

12. **Analytics & Reporting**
    - User activity analytics
    - Tournament statistics
    - Achievement tracking
    - Usage dashboards

13. **Admin Features**
    - Admin dashboard
    - User management
    - Content moderation
    - Analytics and reporting
    - System configuration

14. **Mobile Support**
    - Mobile app development
    - Mobile API optimization
    - Push notifications

---

## Troubleshooting

### Build Issues
- **Lombok not working**: Enable annotation processing in IDE settings
- **Database connection errors**: Verify PostgreSQL is running and credentials are correct
- **JWT secret not configured**: Set `JWT_SECRET` environment variable

### Runtime Issues
- **401 Unauthorized**: Ensure JWT token is included in Authorization header
- **403 Forbidden**: Check user role and endpoint permissions
- **404 Not Found**: Verify resource ID and endpoint path
- **500 Internal Server Error**: Check logs for detailed error messages

---

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards
- Follow Google Java Style Guide
- Add unit tests for new features
- Update documentation
- Keep commits atomic and descriptive

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Contact

For questions or support, please open an issue on GitHub or contact the development team.

---

## Roadmap

### v0.1.0 (Current)
- ✅ User authentication with JWT
- ✅ User management
- ✅ Post and comments system
- ✅ Tournament management
- ✅ Trainer profiles
- ✅ Endorsement system
- ✅ OTP-based email verification
- ✅ OTP-based password reset flow

### v0.2.0 (Q2 2025)
- 🔄 API documentation with Swagger
- 🔄 Comprehensive testing

### v0.3.0 (Q3 2025)
- 🔄 Advanced search and filtering
- 🔄 Notifications system
- 🔄 Redis integration
- 🔄 Performance optimization

### v1.0.0 (Q4 2025)
- 🔄 Production deployment
- 🔄 Frontend integration
- 🔄 Mobile app support
- 🔄 Analytics dashboard

