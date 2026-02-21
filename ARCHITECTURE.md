# SportsFolio Architecture Documentation

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLIENT APPLICATIONS                                 │
│                    (Web Browser, Mobile App, etc.)                           │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │ HTTP/REST
                             ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     API GATEWAY / LOAD BALANCER                              │
│                       (CORS Configuration)                                   │
└────────────────────────────┬────────────────────────────────────────────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                             ▼
┌──────────────────────────────┐  ┌──────────────────────────────┐
│      API MODULE (api/)        │  │  MONITORING & LOGGING        │
│  ┌──────────────────────────┐ │  │  ┌──────────────────────────┐ │
│  │  REST Controllers        │ │  │  │  SLF4J + Logback         │ │
│  │  - AuthController        │ │  │  │  - Error logs            │ │
│  │  - UsersController       │ │  │  │  - Access logs           │ │
│  │  - PostController        │ │  │  │  - Business events       │ │
│  │  - CommentController     │ │  │  └──────────────────────────┘ │
│  │  - LikeController        │ │  └──────────────────────────────┘
│  │  - TournamentController  │ │
│  │  - AdminController       │ │
│  └──────────────────────────┘ │
│              │                 │
│              ▼                 │
│  ┌──────────────────────────┐ │
│  │  Exception Handler       │ │
│  │  (GlobalExceptionHandler)│ │
│  └──────────────────────────┘ │
└──────────────┬─────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER (Business Logic)                          │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  - AuthService        - PostService       - TrainerService          │   │
│  │  - UserService        - CommentService    - SportsService           │   │
│  │  - LikeService        - TournamentService                           │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└──────────────┬─────────────────────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                   SECURITY & AUTHENTICATION LAYER                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  JwtUtil              SecurityConfig         JwtAuthenticationFilter  │   │
│  │  - Token generation   - Spring Security      - Token validation      │   │
│  │  - Token validation   - Role-based access   - Request filtering      │   │
│  │  - Claims extraction  - CORS config          - Principal injection   │   │
│  │  - Token expiration   - Password encoding    - Exception handling    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  CustomUserDetailsService                                            │   │
│  │  - Loads user details from database                                 │   │
│  │  - Provides credentials for authentication                          │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└──────────────┬─────────────────────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                  DATA ACCESS LAYER (Persistence)                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  Spring Data JPA Repositories      DAO Classes                       │   │
│  │  - UserRepository                  - UsersDAO                        │   │
│  │  - PostRepository                  - EndorsementDAO                  │   │
│  │  - CommentRepository               - TrainerDAO                      │   │
│  │  - TournamentRepository            - HallOfFameDAO                   │   │
│  │  - SportsRepository                - TournamentParticipantDAO        │   │
│  │  - PostLikeRepository              etc.                              │   │
│  │  - EndorsementRepository                                             │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                   │                                           │
│                                   ▼                                           │
│                          ┌──────────────────┐                                │
│                          │  Hibernate ORM   │                                │
│                          │ (Entity Mapping) │                                │
│                          └──────────────────┘                                │
└──────────────┬─────────────────────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DATABASE LAYER                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  PostgreSQL (Production) / H2 (Testing)                              │   │
│  │                                                                       │   │
│  │  Tables: users, post, comment, post_like, tournament,                │   │
│  │          tournament_participant, fixtures, endorsement,              │   │
│  │          trainer, sports, user_sports, hall_of_fame, etc.            │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Module Architecture

### 1. **api/** - API & Controller Layer
**Responsibility:** Handle HTTP requests and orchestrate responses

```
api/
├── App.java                    → Spring Boot entry point
├── controller/
│   ├── AuthController.java     → Auth endpoints (/auth/*)
│   ├── UsersController.java    → User endpoints (/api/users/*)
│   ├── PostController.java     → Post endpoints (/api/posts/*)
│   ├── CommentController.java  → Comment endpoints (/api/comments/*)
│   ├── LikeController.java     → Like endpoints (/api/likes/*)
│   ├── TournamentController.java → Tournament endpoints (/api/tournaments/*)
│   ├── TrainerController.java  → Trainer endpoints (/api/trainers/*)
│   ├── SportsController.java   → Sports endpoints (/api/sports/*)
│   └── AdminController.java    → Admin endpoints (/api/admin/*)
├── service/
│   ├── AuthService.java        → Auth business logic
│   ├── UserService.java        → User business logic
│   ├── PostService.java        → Post business logic
│   ├── CommentService.java     → Comment business logic
│   ├── LikeService.java        → Like business logic
│   └── impl/
│       └── UserServiceImpl.java → User service implementation
├── exception/
│   └── (Custom exception handlers)
├── resources/
│   └── application.yml         → Application configuration
└── pom.xml
```

**Key Responsibilities:**
- Receive HTTP requests from clients
- Validate input using DTOs with annotations
- Call service layer for business logic
- Handle authentication and authorization
- Return structured responses
- Log important events

### 2. **auth/** - Authentication & Security
**Responsibility:** Manage JWT tokens, user authentication, and security configuration

```
auth/
├── config/
│   ├── SecurityConfig.java     → Spring Security setup
│   └── CorsConfig.java         → CORS policy configuration
├── dto/
│   ├── AuthRequest.java        → Login/signup request
│   ├── AuthResponse.java       → Auth response with tokens
│   ├── TokenRequest.java       → Token refresh request
│   └── UserDTO.java            → User data transfer object
├── filter/
│   └── JwtAuthenticationFilter.java → JWT validation filter
├── service/
│   └── CustomUserDetailsService.java → User details provider
├── util/
│   └── JwtUtil.java            → JWT token operations
└── pom.xml
```

**Key Responsibilities:**
- Generate and validate JWT tokens
- Implement authentication filters
- Define security rules and permissions
- Handle user details loading
- Manage password encoding
- Configure CORS policies

**JWT Flow:**
```
Login Request
    ↓
[Validate Credentials] → Spring Security's AuthenticationManager
    ↓
Valid Credentials ✓
    ↓
[Generate Tokens] → JwtUtil.generateToken()
    ├─ Access Token (5 hours validity)
    └─ Refresh Token (longer validity)
    ↓
[Return Response] → AuthResponse with tokens
    ↓
Client stores tokens
    ↓
Subsequent Requests
    ↓
[Include Token] → Authorization: Bearer <token>
    ↓
[Validate Token] → JwtAuthenticationFilter
    ├─ Extract username from token
    ├─ Verify signature
    ├─ Check expiration
    └─ Load user details
    ↓
Valid Token ✓ → Allow request
Invalid/Expired ✗ → 401 Unauthorized
```

### 3. **commons/** - Shared Utilities & Common Code
**Responsibility:** Provide reusable code, exception handling, and constants

```
commons/
├── constants/
│   └── Constants.java          → Application constants
├── enums/
│   └── UserRole.java           → User roles (USER, ADMIN, TRAINER)
├── exception/
│   ├── GlobalExceptionHandler.java → Centralized error handling
│   ├── DuplicateResourceException.java
│   ├── UnauthorizedException.java
│   ├── UserNotFoundException.java
│   ├── InvalidPasswordException.java
│   ├── InvalidUsernameException.java
│   ├── InvalidEmailIdException.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
├── util/
│   ├── EmailValidator.java     → Email format validation
│   ├── PasswordValidator.java  → Password strength validation
│   └── UsernameValidator.java  → Username format validation
└── pom.xml
```

**Key Responsibilities:**
- Define custom exceptions with clear semantics
- Centralize exception handling and response formatting
- Provide validation utilities
- Store application-wide constants
- Define enums for type-safe values

**Exception Handling Flow:**
```
Request → Controller → Service
    ↓
[Business Logic]
    ↓
Exception Thrown (e.g., UserNotFoundException)
    ↓
[GlobalExceptionHandler catches it]
    ↓
[Build structured response]
{
  "timestamp": "2025-02-22T10:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 123"
}
    ↓
Return to Client with 404 status
```

### 4. **domain/** - Entity Models & Data Transfer Objects
**Responsibility:** Define business domain models and API DTOs

```
domain/
├── entities/
│   ├── Users.java              → User profile entity
│   ├── UserSports.java         → User-Sport association
│   ├── Post.java               → User posts
│   ├── Comment.java            → Post comments
│   ├── PostLike.java           → Post likes
│   ├── Like.java               → Generic likes
│   ├── Endorsement.java        → User endorsements
│   ├── Tournament.java         → Tournament info
│   ├── TournamentParticipant.java → Participant tracking
│   ├── Fixtures.java           → Match fixtures
│   ├── Trainer.java            → Trainer profiles
│   ├── Sports.java             → Sports catalog
│   └── HallOfFame.java         → Hall of fame entries
├── enums/
│   ├── Level.java              → Skill levels
│   └── GameStage.java          → Tournament stages
└── pom.xml
```

**Key Responsibilities:**
- Map business domain to relational database
- Define entity relationships (1-to-many, many-to-many)
- Implement automatic timestamp management
- Provide data transfer objects for API responses
- Define validation constraints

**Entity Relationships:**
```
Users (Parent Entity)
├─ 1:M → UserSports (junction table with Sports)
├─ 1:M → Post (author)
├─ 1:M → Comment (commenter)
├─ 1:M → PostLike (liker)
├─ 1:M → Tournament (organizer)
├─ 1:M → TournamentParticipant
├─ M:M → Users (followers/following)
├─ 1:M → Endorsement (endorser/endorsed)
├─ 1:M → Trainer (trainer profile)
└─ 1:M → HallOfFame (achievements)

Post (Content Entity)
├─ 1:M → Comment
└─ 1:M → PostLike

Tournament (Event Entity)
├─ 1:M → TournamentParticipant
└─ 1:M → Fixtures
```

### 5. **database/** - Data Access Layer
**Responsibility:** Manage database operations and persistence

```
database/
├── src/
│   ├── main/
│   │   └── java/com/tech/
│   │       ├── dao/
│   │       │   ├── UsersDAO.java
│   │       │   ├── EndorsementDAO.java
│   │       │   ├── TrainerDAO.java
│   │       │   ├── TournamentParticipantDAO.java
│   │       │   └── HallOfFameDAO.java
│   │       └── repository/
│   │           ├── UserRepository.java
│   │           ├── PostRepository.java
│   │           ├── CommentRepository.java
│   │           ├── PostLikeRepository.java
│   │           ├── TournamentRepository.java
│   │           ├── SportsRepository.java
│   │           ├── EndorsementRepository.java
│   │           └── TrainerRepository.java
│   └── test/
│       ├── java/ (Integration tests)
│       └── resources/
│           ├── application-test.yml
│           └── sql/
│               ├── clearDb.sql
│               └── initDb.sql
├── pom.xml
└── target/ (Compiled classes and built JAR)
```

**Key Responsibilities:**
- Provide Spring Data JPA repositories for CRUD operations
- Implement custom DAOs for complex queries
- Manage database schema through Hibernate
- Execute integration tests
- Handle database transactions
- Optimize queries with proper indexing

**Repository Pattern:**
```
Spring Data JPA Repository
├─ Automatically implements common CRUD methods
│  ├─ save(entity)
│  ├─ findById(id)
│  ├─ findAll()
│  ├─ delete(entity)
│  └─ deleteById(id)
└─ Custom query methods
   ├─ findByEmail(email)
   ├─ findByUsername(username)
   ├─ existsByEmail(email)
   └─ Custom @Query methods
```

## Data Flow Examples

### User Authentication Flow
```
1. Client sends POST /auth/login with credentials
   └─ AuthRequest {username, password}

2. AuthController.login() receives request
   └─ Validates input DTOs

3. AuthController calls authenticationManager.authenticate()
   └─ Spring Security validates credentials

4. If valid, AuthController calls jwtUtil.generateToken()
   └─ Creates signed JWT tokens

5. AuthController calls userService.updateRefreshToken()
   └─ Persists refresh token in database

6. AuthController returns AuthResponse
   └─ {accessToken, refreshToken, userDTO}

7. Client stores tokens locally

8. For subsequent requests, client includes:
   └─ Authorization: Bearer <accessToken>

9. JwtAuthenticationFilter intercepts request
   └─ Extracts token from header
   └─ Validates signature and expiration
   └─ Loads user details

10. If valid, request proceeds to controller
    └─ @AuthenticationPrincipal UserDetails available
    
11. If invalid, returns 401 Unauthorized
```

### Post Creation Flow
```
1. Client sends POST /api/posts with content
   └─ Authorization: Bearer <token>

2. JwtAuthenticationFilter validates token
   └─ Sets SecurityContext with user details

3. PostController.createPost() receives request
   └─ @AuthenticationPrincipal provides username

4. PostController calls postService.createPost()
   └─ Service validates input
   └─ Creates Post entity
   └─ Associates author

5. PostService calls postRepository.save()
   └─ Hibernate persists to database
   └─ @CreationTimestamp auto-sets timestamp
   └─ Generates ID (IDENTITY strategy)

6. PostService converts entity to PostDTO
   └─ Includes author, comments, likes count

7. PostController returns ResponseEntity<PostDTO>
   └─ Client receives JSON response
```

### Error Handling Flow
```
1. Request reaches controller

2. Business logic throws custom exception
   └─ Example: DuplicateResourceException("Email already exists")

3. GlobalExceptionHandler catches exception
   └─ @ExceptionHandler identifies exception type
   └─ Maps to appropriate HTTP status (409 Conflict)

4. buildResponse() creates error object
   └─ {
       "timestamp": "2025-02-22T10:30:45",
       "status": 409,
       "error": "Conflict",
       "message": "Email already exists"
     }

5. Logs error with SLF4J
   └─ logger.error("Email already exists: {}", exception)

6. Returns ResponseEntity with error JSON and status code

7. Client receives structured error response
```

## Authentication Security Model

### JWT Token Structure
```
Header.Payload.Signature

Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "iat": 1677145200,      (issued at)
  "exp": 1677161200       (expiration time)
}

Signature: HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  SECRET_KEY
)
```

### Security Features
1. **Secret Key Storage:** Configured via environment variable
2. **Token Signing:** HMAC-SHA256 algorithm
3. **Token Expiration:** 5 hours for access tokens
4. **Refresh Mechanism:** Separate refresh tokens for renewal
5. **Stateless:** No session storage required
6. **CORS:** Configured to allow cross-origin requests
7. **Password Hashing:** BCrypt encryption
8. **Role-Based Access:** USER, ADMIN, TRAINER roles

## Scalability Considerations

### Current State
- Single-instance deployment
- In-memory token blacklist
- File-based logging

### Future Improvements
1. **Database:** Connection pooling and read replicas
2. **Caching:** Redis for session and token management
3. **API Gateway:** Load balancing and rate limiting
4. **Message Queue:** Async operations (emails, notifications)
5. **Microservices:** Separate services for different domains
6. **Database Migration:** Flyway for schema versioning
7. **Monitoring:** Prometheus metrics and ELK stack
8. **Storage:** Cloud storage for user uploads (S3/GCS)

## Environment Configuration

### Development
```yaml
spring.datasource.url: jdbc:postgresql://localhost:5433/sports_folio
spring.jpa.hibernate.ddl-auto: update
logging.level.root: INFO
jwt.expiration: 18000000
```

### Testing
```yaml
spring.datasource.url: jdbc:h2:mem:testdb
spring.datasource.driver-class-name: org.h2.Driver
spring.jpa.database-platform: org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto: none
```

### Production
```yaml
spring.datasource.url: jdbc:postgresql://<prod-host>:<port>/<db>
spring.datasource.username: ${DB_USER}
spring.datasource.password: ${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto: validate
logging.level.root: WARN
jwt.secret: ${JWT_SECRET}
```

## Testing Strategy

### Unit Tests
- Service layer logic
- Validator functions
- Utility classes

### Integration Tests
- Database operations
- End-to-end API flows
- Authentication flows
- Error handling

### Security Tests
- JWT validation
- Authorization checks
- Role-based access control

## Deployment Architecture

```
┌──────────────────┐
│   Developer      │
│    Machine       │
└────────┬─────────┘
         │ git push
         ▼
┌──────────────────┐
│  GitHub          │
│  Repository      │
└────────┬─────────┘
         │ webhook
         ▼
┌──────────────────┐
│   CI/CD Pipeline │
│  (GitHub Actions)│
│  ├─ Compile      │
│  ├─ Test         │
│  ├─ Build JAR    │
│  └─ Deploy       │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────────┐
│  Production Environment           │
│  ┌──────────────────────────────┐ │
│  │  Docker Container            │ │
│  │  ├─ Spring Boot App          │ │
│  │  └─ Port 8080                │ │
│  └──────────────────────────────┘ │
│  ┌──────────────────────────────┐ │
│  │  PostgreSQL Database         │ │
│  │  └─ Port 5432                │ │
│  └──────────────────────────────┘ │
└──────────────────────────────────┘
         │
         ▼
┌──────────────────┐
│   Load Balancer  │
│   (HTTPS/TLS)    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Client Apps     │
│  (Web, Mobile)   │
└──────────────────┘
```

---

**Document Version:** 1.0  
**Last Updated:** February 22, 2025  
**Maintainer:** SportsFolio Development Team
