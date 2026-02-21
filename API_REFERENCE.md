# SportsFolio API Reference

## Base URL
```
http://localhost:8080
```

## Authentication
All endpoints (except `/auth/login` and `/auth/signup`) require JWT token in the Authorization header:
```
Authorization: Bearer <access_token>
```

---

## Authentication Endpoints

### 1. User Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user with credentials and receive JWT tokens

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "john_doe",
    "email": "john@example.com",
    "bio": "Sports enthusiast",
    "profilePicture": "https://...",
    "userRole": "USER",
    "createdAt": "2025-02-22T10:30:45"
  }
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid credentials
- `400 Bad Request` - Missing or invalid parameters

**Example cURL:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }'
```

---

### 2. User Registration
**Endpoint:** `POST /auth/signup`

**Description:** Create a new user account

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "SecurePassword123!",
  "emailId": "john@example.com"
}
```

**Validation Rules:**
- **Username:** 3-20 characters, alphanumeric and underscores only
- **Password:** Minimum 8 characters, must include uppercase, lowercase, number, and special character
- **Email:** Valid email format
- **Unique constraints:** Username and email must be unique in database

**Response (200 OK):**
```json
{
  "message": "User signed up successfully"
}
```

**Error Responses:**
- `409 Conflict` - Username or email already exists
- `400 Bad Request` - Invalid input data or validation failure

**Example cURL:**
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!",
    "emailId": "john@example.com"
  }'
```

---

### 3. Refresh Token
**Endpoint:** `POST /auth/refresh`

**Description:** Get a new access token using refresh token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { /* user object */ }
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or expired refresh token

**Example cURL:**
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

---

### 4. Logout
**Endpoint:** `POST /auth/logout`

**Description:** Logout user and invalidate token (PENDING IMPLEMENTATION)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "message": "Logged out successfully"
}
```

---

## User Endpoints

### 5. Get User Profile
**Endpoint:** `GET /api/users/{userId}`

**Description:** Retrieve user profile information

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `userId` (Long) - User ID

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "john_doe",
  "email": "john@example.com",
  "bio": "Professional athlete",
  "profilePicture": "https://...",
  "userRole": "USER",
  "createdAt": "2025-02-22T10:30:45",
  "updatedAt": "2025-02-22T15:45:30"
}
```

**Error Responses:**
- `404 Not Found` - User not found
- `401 Unauthorized` - Invalid or missing token

---

### 6. Update User Profile
**Endpoint:** `PUT /api/users/{userId}`

**Description:** Update user profile information (PENDING FULL IMPLEMENTATION)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `userId` (Long) - User ID (must match authenticated user)

**Request Body:**
```json
{
  "name": "john_doe_updated",
  "bio": "Updated bio",
  "profilePicture": "https://example.com/new-picture.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "john_doe_updated",
  "email": "john@example.com",
  "bio": "Updated bio",
  "profilePicture": "https://example.com/new-picture.jpg",
  "userRole": "USER",
  "updatedAt": "2025-02-22T16:00:00"
}
```

**Error Responses:**
- `403 Forbidden` - Cannot update other user's profile
- `404 Not Found` - User not found
- `400 Bad Request` - Invalid input

---

### 7. Follow User
**Endpoint:** `POST /api/users/{userId}/follow`

**Description:** Follow another user

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `userId` (Long) - User ID to follow

**Response (200 OK):**
```json
{
  "message": "Successfully followed user"
}
```

**Error Responses:**
- `404 Not Found` - User not found
- `400 Bad Request` - Cannot follow yourself
- `409 Conflict` - Already following user

---

### 8. Unfollow User
**Endpoint:** `DELETE /api/users/{userId}/follow`

**Description:** Unfollow a user

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `userId` (Long) - User ID to unfollow

**Response (200 OK):**
```json
{
  "message": "Successfully unfollowed user"
}
```

---

## Post Endpoints

### 9. Create Post
**Endpoint:** `POST /api/posts`

**Description:** Create a new post

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "Just finished an amazing training session!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "content": "Just finished an amazing training session!",
  "author": {
    "id": 1,
    "name": "john_doe",
    "email": "john@example.com",
    "profilePicture": "https://..."
  },
  "comments": [],
  "likes": 0,
  "createdAt": "2025-02-22T10:30:45"
}
```

**Error Responses:**
- `400 Bad Request` - Empty content
- `401 Unauthorized` - Invalid token

---

### 10. Get News Feed
**Endpoint:** `GET /api/posts`

**Description:** Get news feed (posts from followed users and own posts)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
- `page` (Integer, optional) - Page number (default: 0)
- `size` (Integer, optional) - Posts per page (default: 20)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "content": "Amazing tournament victory!",
    "author": {
      "id": 2,
      "name": "jane_smith",
      "profilePicture": "https://..."
    },
    "comments": [
      {
        "id": 1,
        "content": "Congrats!",
        "author": { "id": 1, "name": "john_doe" },
        "createdAt": "2025-02-22T11:00:00"
      }
    ],
    "likes": 5,
    "createdAt": "2025-02-22T09:00:00"
  }
]
```

---

### 11. Get Single Post
**Endpoint:** `GET /api/posts/{postId}`

**Description:** Get a specific post

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `postId` (Long) - Post ID

**Response (200 OK):**
```json
{
  "id": 1,
  "content": "Post content",
  "author": { /* author object */ },
  "comments": [],
  "likes": 3,
  "createdAt": "2025-02-22T10:30:45"
}
```

---

### 12. Get User's Posts
**Endpoint:** `GET /api/posts/user/{username}`

**Description:** Get all posts by a specific user

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `username` (String) - Username

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "content": "Post 1 content",
    "author": { /* author object */ },
    "comments": [],
    "likes": 2,
    "createdAt": "2025-02-22T10:30:45"
  },
  {
    "id": 2,
    "content": "Post 2 content",
    "author": { /* author object */ },
    "comments": [],
    "likes": 5,
    "createdAt": "2025-02-22T12:00:00"
  }
]
```

---

### 13. Delete Post
**Endpoint:** `DELETE /api/posts/{postId}`

**Description:** Delete a post (only owner or admin)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `postId` (Long) - Post ID

**Response (200 OK):**
```json
{
  "message": "Post deleted successfully"
}
```

**Error Responses:**
- `403 Forbidden` - Cannot delete other user's post
- `404 Not Found` - Post not found

---

## Comment Endpoints

### 14. Create Comment
**Endpoint:** `POST /api/comments`

**Description:** Add a comment to a post

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "postId": 1,
  "content": "Great achievement!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "content": "Great achievement!",
  "author": {
    "id": 1,
    "name": "john_doe",
    "profilePicture": "https://..."
  },
  "createdAt": "2025-02-22T10:35:00"
}
```

---

### 15. Get Post Comments
**Endpoint:** `GET /api/comments/{postId}`

**Description:** Get all comments on a post

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `postId` (Long) - Post ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "content": "Great post!",
    "author": { "id": 2, "name": "jane_smith" },
    "createdAt": "2025-02-22T10:35:00"
  }
]
```

---

### 16. Delete Comment
**Endpoint:** `DELETE /api/comments/{commentId}`

**Description:** Delete a comment (only owner or admin)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `commentId` (Long) - Comment ID

**Response (200 OK):**
```json
{
  "message": "Comment deleted successfully"
}
```

---

## Like Endpoints

### 17. Like Post
**Endpoint:** `POST /api/likes`

**Description:** Like a post

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "postId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "postId": 1,
  "userId": 1,
  "createdAt": "2025-02-22T10:40:00"
}
```

**Error Responses:**
- `409 Conflict` - Already liked this post
- `404 Not Found` - Post not found

---

### 18. Unlike Post
**Endpoint:** `DELETE /api/likes/{likeId}`

**Description:** Remove like from post

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `likeId` (Long) - Like ID

**Response (200 OK):**
```json
{
  "message": "Like removed successfully"
}
```

---

### 19. Get Post Likes
**Endpoint:** `GET /api/posts/{postId}/likes`

**Description:** Get count and list of users who liked a post

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `postId` (Long) - Post ID

**Response (200 OK):**
```json
{
  "count": 5,
  "likes": [
    {
      "id": 1,
      "user": { "id": 1, "name": "john_doe", "profilePicture": "https://..." },
      "createdAt": "2025-02-22T10:30:00"
    }
  ]
}
```

---

## Tournament Endpoints

### 20. Create Tournament
**Endpoint:** `POST /api/tournaments`

**Description:** Create a new tournament (PENDING FULL IMPLEMENTATION)

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Spring Tennis Championship",
  "type": "SINGLES",
  "sportId": 1,
  "startDate": "2025-04-01",
  "endDate": "2025-04-15"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Spring Tennis Championship",
  "type": "SINGLES",
  "organizer": { "id": 1, "name": "john_doe" },
  "sport": { "id": 1, "name": "Tennis" },
  "startDate": "2025-04-01",
  "endDate": "2025-04-15",
  "createdAt": "2025-02-22T10:50:00"
}
```

---

### 21. Get All Tournaments
**Endpoint:** `GET /api/tournaments`

**Description:** List all tournaments with optional filtering

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
- `sportId` (Long, optional) - Filter by sport
- `status` (String, optional) - Filter by status (UPCOMING, ONGOING, COMPLETED)
- `page` (Integer, optional) - Page number

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Spring Tennis Championship",
    "type": "SINGLES",
    "organizer": { "id": 1, "name": "john_doe" },
    "sport": { "id": 1, "name": "Tennis" },
    "startDate": "2025-04-01",
    "endDate": "2025-04-15",
    "participants": 32,
    "status": "UPCOMING",
    "createdAt": "2025-02-22T10:50:00"
  }
]
```

---

### 22. Get Tournament Details
**Endpoint:** `GET /api/tournaments/{tournamentId}`

**Description:** Get detailed information about a tournament

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `tournamentId` (Long) - Tournament ID

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Spring Tennis Championship",
  "type": "SINGLES",
  "organizer": { /* organizer details */ },
  "sport": { /* sport details */ },
  "startDate": "2025-04-01",
  "endDate": "2025-04-15",
  "participants": [
    { "id": 1, "name": "john_doe", "level": "ADVANCED" },
    { "id": 2, "name": "jane_smith", "level": "PROFESSIONAL" }
  ],
  "fixtures": [
    {
      "id": 1,
      "team1": { "id": 1, "name": "john_doe" },
      "team2": { "id": 2, "name": "jane_smith" },
      "matchDate": "2025-04-02T10:00:00",
      "gameStage": "GROUP_STAGE"
    }
  ],
  "status": "UPCOMING"
}
```

---

### 23. Join Tournament
**Endpoint:** `POST /api/tournaments/{tournamentId}/join`

**Description:** Join a tournament as participant

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `tournamentId` (Long) - Tournament ID

**Response (200 OK):**
```json
{
  "message": "Successfully joined tournament",
  "participantId": 1
}
```

**Error Responses:**
- `404 Not Found` - Tournament not found
- `409 Conflict` - Already participant
- `400 Bad Request` - Tournament registration closed

---

## Trainer Endpoints

### 24. Get All Trainers
**Endpoint:** `GET /api/trainers`

**Description:** List all trainers (PENDING FULL IMPLEMENTATION)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
- `specialization` (String, optional) - Filter by specialization
- `page` (Integer, optional) - Page number

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "user": {
      "id": 5,
      "name": "coach_mike",
      "email": "mike@example.com",
      "profilePicture": "https://..."
    },
    "specialization": "Tennis Coaching",
    "bio": "10 years of coaching experience",
    "rating": 4.8,
    "createdAt": "2025-02-22T08:00:00"
  }
]
```

---

### 25. Get Trainer Profile
**Endpoint:** `GET /api/trainers/{trainerId}`

**Description:** Get detailed trainer profile

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `trainerId` (Long) - Trainer ID

**Response (200 OK):**
```json
{
  "id": 1,
  "user": { /* user details */ },
  "specialization": "Tennis Coaching",
  "bio": "10 years of coaching experience",
  "rating": 4.8,
  "students": 45,
  "reviews": [
    {
      "id": 1,
      "rating": 5,
      "comment": "Great trainer!",
      "author": { "id": 1, "name": "john_doe" },
      "createdAt": "2025-02-22T09:00:00"
    }
  ]
}
```

---

## Sports Endpoints

### 26. Get All Sports
**Endpoint:** `GET /api/sports`

**Description:** Get list of all available sports

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Tennis",
    "description": "Individual racquet sport"
  },
  {
    "id": 2,
    "name": "Basketball",
    "description": "Team sport played with a ball"
  },
  {
    "id": 3,
    "name": "Cricket",
    "description": "Bat-and-ball sport"
  }
]
```

---

### 27. Add Sport to User Profile
**Endpoint:** `POST /api/user-sports`

**Description:** Add a sport to user's profile with skill level

**Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "sportId": 1,
  "level": "ADVANCED"
}
```

**Level Options:**
- `BEGINNER`
- `INTERMEDIATE`
- `ADVANCED`
- `PROFESSIONAL`

**Response (201 Created):**
```json
{
  "id": 1,
  "user": { "id": 1, "name": "john_doe" },
  "sport": { "id": 1, "name": "Tennis" },
  "level": "ADVANCED",
  "createdAt": "2025-02-22T11:00:00"
}
```

---

## Admin Endpoints

### 28. Get All Users
**Endpoint:** `GET /api/admin/users`

**Description:** Get list of all users (admin only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "john_doe",
    "email": "john@example.com",
    "userRole": "USER",
    "createdAt": "2025-02-22T10:00:00",
    "postCount": 5,
    "followerCount": 10
  }
]
```

---

### 29. Delete User
**Endpoint:** `DELETE /api/admin/users/{userId}`

**Description:** Delete a user account (admin only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
- `userId` (Long) - User ID

**Response (200 OK):**
```json
{
  "message": "User deleted successfully"
}
```

---

### 30. Get User Statistics
**Endpoint:** `GET /api/admin/stats`

**Description:** Get platform-wide statistics

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response (200 OK):**
```json
{
  "totalUsers": 150,
  "totalPosts": 2450,
  "totalTournaments": 25,
  "activeUsersToday": 45,
  "newUsersThisWeek": 12,
  "averagePostsPerUser": 16.3
}
```

---

## Error Handling

All error responses follow this format:
```json
{
  "timestamp": "2025-02-22T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message"
}
```

### HTTP Status Codes
- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid input or validation failure
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Authenticated but insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource or conflicting operation
- `500 Internal Server Error` - Server error

---

## Rate Limiting (Pending Implementation)
- 100 requests per minute per IP address
- 50 requests per minute for auth endpoints

---

## Pagination (Standard Format)
Endpoints supporting pagination accept:
```
GET /api/resource?page=0&size=20&sort=createdAt,desc
```

Response:
```json
{
  "content": [ /* items */ ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

---

**API Version:** 1.0  
**Last Updated:** February 22, 2025
