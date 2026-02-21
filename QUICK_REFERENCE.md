# SportsFolio Quick Reference Card

## Project Overview
**SportsFolio** is a Spring Boot-based sports portfolio platform where athletes showcase achievements, connect with trainers, and participate in tournaments.

---

## Project Structure
```
5 Maven Modules:
├── api/       → REST Controllers & Services
├── auth/      → JWT Security & Authentication  
├── commons/   → Shared Utilities & Exceptions
├── database/  → Repositories & Data Access
└── domain/    → Entity Models & DTOs
```

---

## Tech Stack at a Glance
| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.4.1 |
| **Authentication** | JWT (JJWT 0.11.5) |
| **Database** | PostgreSQL / H2 |
| **ORM** | JPA/Hibernate |
| **Build** | Maven |
| **Language** | Java 21 |
| **Security** | Spring Security + BCrypt |
| **Logging** | SLF4J + Logback |

---

## Core Features

### ✅ Implemented
- User registration & login with JWT tokens
- User profiles with follow system
- Post creation, comments, and likes
- Tournament management
- Trainer profiles
- Endorsement system
- Hall of fame tracking
- Global exception handling
- Email/username/password validation
- Role-based access control (USER, ADMIN, TRAINER)

### ⏳ Pending (High Priority)
- OTP generation and email verification
- Password reset flow
- Swagger/OpenAPI documentation
- Comprehensive testing suite

### ⏳ Pending (Medium Priority)
- Advanced search & filtering
- Real-time notifications
- Redis caching
- Rate limiting
- Profile picture upload

### ⏳ Pending (Low Priority)
- Docker/Kubernetes deployment
- Frontend integration
- Analytics dashboard
- Mobile app support

---

## Key Entities
```
Users
├─ id, name (unique), email (unique)
├─ passwordHash, bio, profilePicture
├─ userRole (USER, ADMIN, TRAINER)
├─ refreshToken, createdAt, updatedAt
└─ relationships:
   ├─ 1:M with Post (author)
   ├─ 1:M with UserSports
   ├─ M:M with Users (followers/following)
   ├─ 1:M with Tournament (organizer)
   └─ 1:M with Endorsement

Post
├─ id, content, author_id
├─ createdAt, updatedAt
└─ relationships:
   ├─ 1:M with Comment
   └─ 1:M with PostLike

Tournament
├─ id, name, type, organizer_id, sports_id
├─ startDate, endDate, createdAt
└─ relationships:
   ├─ 1:M with TournamentParticipant
   └─ 1:M with Fixtures
```

---

## Authentication Flow (JWT)

### Login
```
POST /auth/login
{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
↓
✓ Credentials valid
↓
AccessToken (5h) + RefreshToken
↓
Client stores both tokens
```

### Using Token
```
GET /api/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
↓
JwtAuthenticationFilter validates
↓
Request proceeds or 401 Unauthorized
```

### Refresh Token
```
POST /auth/refresh
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
↓
New AccessToken + RefreshToken
```

---

## API Endpoints Summary

### Authentication
- `POST /auth/login` - Login with credentials
- `POST /auth/signup` - Register new user
- `POST /auth/refresh` - Refresh access token
- `POST /auth/logout` - Logout (pending)

### Users
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update profile
- `POST /api/users/{id}/follow` - Follow user
- `DELETE /api/users/{id}/follow` - Unfollow

### Posts
- `POST /api/posts` - Create post
- `GET /api/posts` - Get news feed
- `GET /api/posts/{id}` - Get single post
- `GET /api/posts/user/{username}` - Get user posts
- `DELETE /api/posts/{id}` - Delete post

### Comments
- `POST /api/comments` - Add comment
- `GET /api/comments/{postId}` - Get comments
- `DELETE /api/comments/{id}` - Delete comment

### Likes
- `POST /api/likes` - Like post
- `DELETE /api/likes/{id}` - Unlike post
- `GET /api/posts/{id}/likes` - Get likes count

### Tournaments
- `POST /api/tournaments` - Create tournament
- `GET /api/tournaments` - List tournaments
- `GET /api/tournaments/{id}` - Get details
- `POST /api/tournaments/{id}/join` - Join tournament

### Trainers
- `GET /api/trainers` - List trainers
- `GET /api/trainers/{id}` - Get trainer profile

### Sports
- `GET /api/sports` - List sports
- `POST /api/user-sports` - Add sport to profile

### Admin
- `GET /api/admin/users` - Get all users
- `DELETE /api/admin/users/{id}` - Delete user
- `GET /api/admin/stats` - Get statistics

---

## Error Response Format
```json
{
  "timestamp": "2025-02-22T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message"
}
```

### HTTP Status Codes
| Code | Meaning | Examples |
|------|---------|----------|
| 200 | OK | Successful GET/PUT |
| 201 | Created | POST successful |
| 400 | Bad Request | Validation failed |
| 401 | Unauthorized | Invalid/missing token |
| 403 | Forbidden | No permission |
| 404 | Not Found | Resource missing |
| 409 | Conflict | Duplicate resource |
| 500 | Server Error | Unexpected error |

---

## Configuration

### Environment Variables
```bash
JWT_SECRET=your_secret_key_here
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/sports_folio
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

### Application Properties
```yaml
jwt:
  secret: ${JWT_SECRET:default}
  expiration: 18000000  # 5 hours in ms

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    root: INFO
```

---

## Common Tasks

### Build Project
```bash
./mvnw clean install
```

### Run Application
```bash
cd api
../mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw test
```

### Build Docker Image
```bash
./mvnw clean package
docker build -t sportsfolio:latest .
```

### Start PostgreSQL
```bash
docker run -d \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=sports_folio \
  -p 5433:5432 \
  postgres:15
```

---

## Exception Classes

### Custom Exceptions
| Exception | Status | When Thrown |
|-----------|--------|------------|
| `DuplicateResourceException` | 409 | User/email already exists |
| `UnauthorizedException` | 401 | Invalid credentials |
| `UserNotFoundException` | 404 | User not found |
| `InvalidPasswordException` | 400 | Password validation fails |
| `InvalidUsernameException` | 400 | Username validation fails |
| `InvalidEmailIdException` | 400 | Email validation fails |
| `ResourceNotFoundException` | 404 | General resource not found |

### Throwing Exceptions
```java
if (usersDAO.checkIfEmailExists(email)) {
    throw new DuplicateResourceException("Email already exists");
}

if (!passwordValidator.isValid(password)) {
    throw new InvalidPasswordException("Password does not meet requirements");
}

User user = userRepository.findById(id)
    .orElseThrow(() -> new UserNotFoundException("User not found"));
```

---

## Validation Annotations
```java
@Email(message = "Invalid email format")
private String email;

@NotBlank(message = "Username cannot be empty")
private String username;

@Size(min = 8, message = "Password must be at least 8 characters")
private String password;
```

---

## Database Migration Notes

### Creating New Tables
1. Create JPA entity with `@Entity` and `@Table`
2. Define columns with `@Column` annotations
3. Add relationships with `@OneToMany`, `@ManyToMany`, etc.
4. Set `spring.jpa.hibernate.ddl-auto=update`
5. Run application - tables auto-created

### Running Migrations
```sql
-- Clear database
DELETE FROM post_like;
DELETE FROM post;
DELETE FROM comment;
DELETE FROM users;

-- Or use clearDb.sql in database module
```

---

## Security Features

### Password Hashing
```java
new BCryptPasswordEncoder().encode(password)
```

### JWT Token
```java
Jwts.builder()
    .setSubject(username)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + validity))
    .signWith(getKey(), SignatureAlgorithm.HS256)
    .compact()
```

### Role-Based Access
```java
@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getAllUsers() {
    // Only ADMIN can access
}
```

---

## Useful CURL Commands

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"SecurePassword123!"}'
```

### Create Post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"content":"Amazing post!"}'
```

### Get News Feed
```bash
curl -X GET http://localhost:8080/api/posts \
  -H "Authorization: Bearer <token>"
```

### Follow User
```bash
curl -X POST http://localhost:8080/api/users/2/follow \
  -H "Authorization: Bearer <token>"
```

---

## Performance Tips

1. **Use pagination** for list endpoints
2. **Lazy load** relationships to reduce queries
3. **Cache frequently accessed data** with Redis
4. **Index** database columns for faster queries
5. **Use connection pooling** in production
6. **Monitor** with Prometheus metrics

---

## Debugging Tips

### Enable Debug Logging
```yaml
logging:
  level:
    com.tech: DEBUG
    org.springframework.security: DEBUG
```

### Check JWT Token
```javascript
// Decode JWT in browser console
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
const payload = JSON.parse(atob(token.split('.')[1]));
console.log(payload);
```

### Database Connection Issues
```bash
# Test PostgreSQL connection
psql -h localhost -p 5433 -U postgres -d sports_folio
```

---

## Links & Resources

- **Documentation**
  - README.md - Complete overview
  - ARCHITECTURE.md - System design
  - API_REFERENCE.md - All endpoints
  - CLASS_DIAGRAM.puml - Entity relationships

- **Tools**
  - Postman - API testing
  - PlantUML - UML diagrams
  - pgAdmin - Database management
  - Docker - Containerization

- **References**
  - Spring Boot Docs: https://spring.io/projects/spring-boot
  - JWT.io: https://jwt.io
  - Hibernate: https://hibernate.org
  - PostgreSQL: https://www.postgresql.org

---

## Common Issues & Solutions

### Issue: Port 8080 already in use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Issue: JWT Secret not configured
```bash
# Set environment variable
export JWT_SECRET="your_secret_key_here"
```

### Issue: Database connection refused
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Start PostgreSQL if needed
docker run -d -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres
```

### Issue: Tests failing
```bash
# Run tests with more details
./mvnw test -X

# Run specific test
./mvnw test -Dtest=AuthServiceTest
```

---

## Version Information
- **Document Version:** 1.0
- **Last Updated:** February 22, 2025
- **Valid for:** SportsFolio v0.0.1-SNAPSHOT

---

**Happy Coding! 🚀**
