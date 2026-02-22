# OTP Email Verification Feature

## Overview

This document describes the OTP (One-Time Password) email verification feature that has been implemented for the SportsFolio application. This feature ensures that users verify their email addresses before they can log in to the platform.

## Features

### 1. **OTP Generation on Signup**
- When a user signs up with a valid username, password, and email, a 6-digit OTP is automatically generated
- The OTP is valid for 10 minutes from generation
- The OTP is stored in the database with expiration timestamp

### 2. **Email Delivery**
- OTP is sent to the user's registered email address via SMTP
- Email contains a clear message with the OTP code
- Email also includes security notice that user should not share the OTP

### 3. **OTP Verification**
- User can verify their OTP within 10 minutes of signup
- Once verified, the user's email is marked as verified in the system
- Only verified users can log in to the system

### 4. **OTP Resend**
- Users can request a new OTP if they didn't receive it or if the original OTP expired
- Previous unverified OTP is deleted when resending
- New OTP is generated and sent to the user's email

### 5. **Login Protection**
- Users cannot log in if their email is not verified
- Login endpoint checks email verification status
- Clear error message if email is not verified

## API Endpoints

### POST /auth/signup
**Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass@123",
  "emailId": "john@example.com"
}
```

**Response (201 Created):**
```json
"User signed up successfully. OTP sent to your email. Please verify your email to login."
```

**Flow:**
1. User registration data is validated
2. User is created with `emailVerified` set to `false`
3. OTP is generated (6-digit code)
4. OTP is saved to database with 10-minute expiration
5. Email is sent to user's email address with OTP

---

### POST /auth/verify-otp
**Request:**
```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**
```json
"Email verified successfully. You can now login."
```

**Possible Errors:**
- `401 Unauthorized` - "User not found with this email"
- `401 Unauthorized` - "Invalid OTP code"
- `401 Unauthorized` - "OTP has expired. Please request a new OTP"
- `401 Unauthorized` - "OTP has already been verified"

**Flow:**
1. User's email is verified in database
2. OTP code is validated against stored OTP
3. OTP expiration is checked
4. OTP verification status is checked
5. Both OTP and User records are marked as verified
6. User can now log in

---

### POST /auth/resend-otp
**Request:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
"OTP resent to your email. Please verify within 10 minutes."
```

**Possible Errors:**
- `401 Unauthorized` - "User not found with this email"

**Flow:**
1. User's email is verified in database
2. Any existing unverified OTP for the email is deleted
3. New OTP is generated
4. New OTP is saved to database
5. Email is sent with new OTP

---

### POST /auth/login
**Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass@123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userDTO": {
    "id": 1,
    "name": "john_doe",
    "email": "john@example.com",
    "userRole": "USER",
    "emailVerified": true
  }
}
```

**Possible Errors:**
- `401 Unauthorized` - "Invalid username or password"
- `401 Unauthorized` - "Email not verified. Please verify your email before logging in"

**Modified Flow:**
1. Username and password are authenticated
2. **NEW:** Email verification status is checked
3. If email not verified, error is thrown
4. Tokens are generated only for verified users

## Database Schema

### OTP Table
```sql
CREATE TABLE otp (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_otp_email ON otp(email);
CREATE INDEX idx_otp_code ON otp(otp_code);
```

### Users Table (Modified)
```sql
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT false;
```

### OTP Entity
- **id** (Long): Primary key, auto-generated
- **email** (String): User's email address
- **otpCode** (String): 6-digit OTP code
- **isVerified** (Boolean): Whether OTP has been verified
- **createdAt** (LocalDateTime): Timestamp when OTP was created
- **expiresAt** (LocalDateTime): Timestamp when OTP expires (10 minutes after creation)

### OTP Methods
- `isExpired()`: Returns true if current time is after expiresAt
- `isValid()`: Returns true if not expired and not verified

## Implementation Details

### OTP Generation (OTPGenerator.java)
```java
public static String generateOTP() {
    int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
    return String.format("%0" + OTP_LENGTH + "d", otp);
}
```
- Uses `SecureRandom` for cryptographic randomness
- Generates 6-digit OTP code
- Returns OTP as zero-padded string

### Email Service (EmailService.java)
- Uses Spring Mail for SMTP integration
- Configurable email host, port, username, password via environment variables
- Builds professional email template with OTP code
- Includes security disclaimer in email body

### AuthService Updates
**Key Methods:**
1. `signup(AuthRequest)` - Creates user and generates OTP
2. `verifyOTP(String email, String otp)` - Verifies OTP and marks email as verified
3. `resendOTP(String email)` - Generates and sends new OTP

## Configuration

### Environment Variables
```bash
# Email SMTP Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### application.yml
```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:your-email@gmail.com}
    password: ${MAIL_PASSWORD:your-app-password}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

### Gmail Configuration (if using Gmail)
1. Enable 2-Factor Authentication on your Google Account
2. Generate an App Password from Google Account Security settings
3. Use the App Password as `MAIL_PASSWORD` (not your regular Gmail password)

## User Journey

### Signup and Email Verification Flow

```
User Request
    ↓
POST /auth/signup
    ↓
Validate Input (username, password, email)
    ↓
Check for Duplicates
    ↓
Create User (emailVerified = false)
    ↓
Generate 6-digit OTP (valid for 10 min)
    ↓
Save OTP to Database
    ↓
Send Email with OTP
    ↓
Return 201 Created - User Signup Successful
    ↓
User Receives Email with OTP
    ↓
User Submits OTP
    ↓
POST /auth/verify-otp
    ↓
Validate OTP Code & Expiration
    ↓
Mark User.emailVerified = true
    ↓
Mark OTP.isVerified = true
    ↓
Return 200 OK - Email Verified
    ↓
User Can Now Login
    ↓
POST /auth/login
    ↓
Authenticate Credentials
    ↓
Check User.emailVerified == true
    ↓
Generate JWT Tokens
    ↓
Return 200 OK with Access Token
```

## Error Handling

### OTP-Related Exceptions
- **User Not Found**: Thrown when email doesn't match any registered user
- **Invalid OTP Code**: Thrown when OTP code doesn't match stored OTP
- **OTP Expired**: Thrown when OTP is older than 10 minutes
- **OTP Already Verified**: Thrown when attempting to verify already verified OTP
- **Email Not Verified**: Thrown when user attempts to login without email verification
- **Failed to Send Email**: Thrown when SMTP connection fails or email delivery fails

## Security Considerations

1. **OTP Generation**: Uses `SecureRandom` for cryptographic randomness
2. **OTP Storage**: OTPs are stored in database (not in memory/session)
3. **OTP Expiration**: OTPs expire after 10 minutes
4. **Single Use**: OTPs can only be used once
5. **Email Verification**: Required before login
6. **Password Hashing**: User passwords are hashed with BCrypt
7. **JWT Tokens**: Only issued to verified users

## Database Queries

### Find Latest Unverified OTP
```sql
SELECT o FROM OTP o 
WHERE o.email = ? AND o.isVerified = false 
ORDER BY o.createdAt DESC
```

### Find OTP by Email and Code
```sql
SELECT o FROM OTP o 
WHERE o.email = ? AND o.otpCode = ? AND o.isVerified = false
```

### Find User by Email
```sql
SELECT u FROM Users u WHERE u.email = ?
```

## Testing the Feature

### Manual Testing Steps

#### 1. Signup
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234",
    "emailId": "test@example.com"
  }'
```

#### 2. Check Email for OTP
- Check your email inbox for the OTP code

#### 3. Verify OTP
```bash
curl -X POST http://localhost:8080/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

#### 4. Attempt Login Before Verification
```bash
# This will fail with "Email not verified" error
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234"
  }'
```

#### 5. Login After Verification
```bash
# This will succeed if OTP was verified
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234"
  }'
```

#### 6. Resend OTP
```bash
curl -X POST http://localhost:8080/auth/resend-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'
```

## Files Modified/Created

### New Files
1. **domain/src/main/java/com/tech/entities/OTP.java** - OTP entity
2. **domain/src/main/java/com/tech/dto/OTPVerificationRequest.java** - DTO for OTP verification
3. **domain/src/main/java/com/tech/dto/OTPResendRequest.java** - DTO for OTP resend
4. **database/src/main/java/com/tech/repository/OTPRepository.java** - OTP repository
5. **database/src/main/java/com/tech/dao/OTPDAO.java** - OTP data access object
6. **commons/src/main/java/com/tech/commons/util/OTPGenerator.java** - OTP generation utility
7. **api/src/main/java/com/tech/service/EmailService.java** - Email sending service

### Modified Files
1. **domain/src/main/java/com/tech/entities/Users.java** - Added emailVerified field
2. **api/src/main/java/com/tech/service/AuthService.java** - Added OTP generation and verification logic
3. **api/src/main/java/com/tech/controller/AuthController.java** - Added OTP verification and resend endpoints, email verification check in login
4. **database/src/main/java/com/tech/dao/UsersDAO.java** - Added findByUserEmail method
5. **database/src/main/java/com/tech/repository/UsersRepository.java** - Added findByEmail method
6. **api/pom.xml** - Added spring-boot-starter-mail dependency
7. **api/src/main/resources/application.yml** - Added email configuration

## Future Enhancements

1. **Rate Limiting**: Limit OTP generation requests per email to prevent abuse
2. **OTP Delivery Methods**: Support SMS OTP in addition to email
3. **OTP Code Length**: Make OTP length configurable
4. **OTP Expiration Time**: Make OTP validity period configurable
5. **OTP Retry Limit**: Limit number of OTP verification attempts
6. **OTP Log**: Keep audit log of OTP generation and verification
7. **Email Templates**: Use HTML email templates with styling
8. **Localization**: Support OTP emails in multiple languages
9. **Webhook Integration**: Send OTP delivery notifications to admin
10. **Analytics**: Track OTP verification success/failure rates

## Troubleshooting

### Email Not Sending
- **Check SMTP Configuration**: Verify MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD environment variables
- **Check Network Connection**: Ensure SMTP server is reachable
- **Enable Less Secure Apps**: For Gmail, ensure "Less Secure Apps" is enabled or use App Password
- **Check Logs**: Look for JavaMailSender exceptions in logs

### OTP Not Matching
- **Copy Paste Carefully**: Ensure entire 6-digit code is copied
- **Check Expiration**: OTP expires after 10 minutes
- **Check Email**: Verify correct OTP from email (may have multiple if resent)

### User Cannot Login
- **Email Verification**: Ensure email is verified before attempting login
- **Credentials**: Verify correct username and password
- **Check Database**: Verify user.email_verified = true in database

### Tests Failing
- **Mail Configuration**: May need mock email service for unit tests
- **H2 Database**: Ensure H2 database is configured for tests
- **Timezone Issues**: Ensure server and database timezone is set correctly for OTP expiration

