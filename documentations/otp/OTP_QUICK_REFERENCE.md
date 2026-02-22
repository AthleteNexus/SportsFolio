# OTP & Email Verification - Quick Reference

## Feature Summary

✅ **OTP Generation & Email Verification** - Complete implementation for secure email-based signup

### What's New
- 6-digit OTP generation on signup
- Email delivery via SMTP
- 10-minute OTP expiration
- Email verification required before login
- OTP resend functionality

---

## Quick Start

### 1. Set Email Configuration

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password  # Gmail app password
```

For detailed setup, see [EMAIL_CONFIGURATION.md](EMAIL_CONFIGURATION.md)

### 2. Run Application

```bash
cd /Users/adarshverma013/Developer/SportsFolio
./mvnw spring-boot:run -pl api
```

### 3. Test the Flow

#### Signup
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234",
    "emailId": "your-email@gmail.com"
  }'
```

**Response:** User created, OTP sent to email

#### Verify Email
```bash
curl -X POST http://localhost:8080/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@gmail.com",
    "otp": "123456"  # Get from email
  }'
```

**Response:** Email verified successfully

#### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234"
  }'
```

**Response:** JWT tokens for authenticated user

---

## API Endpoints

### New Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/verify-otp` | Verify OTP and mark email as verified |
| POST | `/auth/resend-otp` | Resend OTP to email |

### Modified Endpoints

| Method | Endpoint | Change |
|--------|----------|--------|
| POST | `/auth/signup` | Now sends OTP via email |
| POST | `/auth/login` | Now requires email verification |

---

## Database Changes

### New Table: OTP
```sql
CREATE TABLE otp (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
```

### Modified Table: Users
```sql
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT false;
```

---

## New Files Created

### Entities
- `domain/src/main/java/com/tech/entities/OTP.java` - OTP entity

### DTOs
- `domain/src/main/java/com/tech/dto/OTPVerificationRequest.java` - OTP verification request
- `domain/src/main/java/com/tech/dto/OTPResendRequest.java` - OTP resend request

### Services
- `api/src/main/java/com/tech/service/EmailService.java` - Email sending service

### Data Access
- `database/src/main/java/com/tech/repository/OTPRepository.java` - OTP repository
- `database/src/main/java/com/tech/dao/OTPDAO.java` - OTP DAO

### Utilities
- `commons/src/main/java/com/tech/commons/util/OTPGenerator.java` - OTP generation utility

### Documentation
- `OTP_FEATURE_DOCUMENTATION.md` - Comprehensive OTP documentation
- `EMAIL_CONFIGURATION.md` - Email setup guide

---

## Modified Files

1. **domain/src/main/java/com/tech/entities/Users.java**
   - Added `emailVerified` field (Boolean)

2. **api/src/main/java/com/tech/service/AuthService.java**
   - Added `signup()` with OTP generation
   - Added `verifyOTP()` for email verification
   - Added `resendOTP()` for OTP resend
   - Added `generateAndSendOTP()` helper method

3. **api/src/main/java/com/tech/controller/AuthController.java**
   - Added `/auth/verify-otp` endpoint
   - Added `/auth/resend-otp` endpoint
   - Modified `/auth/login` to check email verification
   - Modified `/auth/signup` response

4. **database/src/main/java/com/tech/dao/UsersDAO.java**
   - Added `findByUserEmail()` method

5. **database/src/main/java/com/tech/repository/UsersRepository.java**
   - Added `findByEmail()` method

6. **api/pom.xml**
   - Added `spring-boot-starter-mail` dependency

7. **api/src/main/resources/application.yml**
   - Added email SMTP configuration

---

## User Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    USER SIGNUP FLOW                         │
└─────────────────────────────────────────────────────────────┘

1. User Signs Up
   POST /auth/signup
   ↓
2. Validate Input
   - Username, password, email validation
   - Check for duplicates
   ↓
3. Create User Account
   - emailVerified = false
   - Password hashed with BCrypt
   ↓
4. Generate 6-digit OTP
   - OTP valid for 10 minutes
   - Saved to database
   ↓
5. Send OTP via Email
   - SMTP email delivery
   - Professional email template
   ↓
6. Return 201 Created
   "User signed up successfully. OTP sent to your email."
   ↓
7. User Receives Email with OTP

┌─────────────────────────────────────────────────────────────┐
│              EMAIL VERIFICATION FLOW                        │
└─────────────────────────────────────────────────────────────┘

1. User Provides OTP
   POST /auth/verify-otp
   ↓
2. Validate Email
   - Check user exists
   ↓
3. Validate OTP
   - Check OTP code matches
   - Check OTP not expired
   - Check OTP not already verified
   ↓
4. Mark as Verified
   - OTP.isVerified = true
   - User.emailVerified = true
   ↓
5. Return 200 OK
   "Email verified successfully. You can now login."

┌─────────────────────────────────────────────────────────────┐
│                    LOGIN FLOW                               │
└─────────────────────────────────────────────────────────────┘

1. User Attempts Login
   POST /auth/login
   ↓
2. Authenticate Credentials
   - Check username/password
   ↓
3. Check Email Verification
   - User.emailVerified must be true
   ↓
4. Generate JWT Tokens
   - Access token (15 min)
   - Refresh token (24 hours)
   ↓
5. Return 200 OK with Tokens
   {
     "accessToken": "...",
     "refreshToken": "...",
     "userDTO": { ... }
   }
```

---

## Configuration

### Environment Variables (Required for Email)

```bash
MAIL_HOST=smtp.gmail.com           # SMTP server hostname
MAIL_PORT=587                       # SMTP port (587 for TLS, 465 for SSL)
MAIL_USERNAME=your-email@gmail.com # Sender email address
MAIL_PASSWORD=your-app-password    # Email provider app password
```

### For Gmail Users
1. Enable 2-Factor Authentication on Google Account
2. Generate App Password from Google Account Security settings
3. Use App Password as `MAIL_PASSWORD` (not regular Gmail password)

For other providers, see [EMAIL_CONFIGURATION.md](EMAIL_CONFIGURATION.md)

---

## Error Handling

### Signup Errors
- `400 Bad Request` - Invalid input (username, password, email format)
- `409 Conflict` - User/email already exists
- `500 Internal Server Error` - Email sending failed

### OTP Verification Errors
- `401 Unauthorized` - User not found
- `401 Unauthorized` - Invalid OTP code
- `401 Unauthorized` - OTP expired (request new OTP)
- `401 Unauthorized` - OTP already verified

### Login Errors
- `401 Unauthorized` - Invalid credentials
- `401 Unauthorized` - Email not verified (must verify first)

---

## Testing Checklist

- [ ] User can sign up with valid email
- [ ] OTP email is received within seconds
- [ ] User can verify email with correct OTP
- [ ] User cannot verify with expired OTP
- [ ] User cannot verify with invalid OTP
- [ ] User cannot login before email verification
- [ ] User can login after email verification
- [ ] User can resend OTP and use new OTP
- [ ] User receives professional email with OTP

---

## Security Features

✅ 6-digit cryptographically random OTP
✅ 10-minute OTP expiration
✅ Single-use OTP (cannot verify twice)
✅ Email verification required before login
✅ BCrypt password hashing
✅ SMTP over TLS/STARTTLS
✅ Secure error messages (no information leakage)

---

## Build & Compilation

The project compiles successfully with all changes:

```
✅ domain module
✅ database module
✅ commons module
✅ auth module
✅ api module
```

All modules build successfully with `./mvnw clean package -DskipTests`

---

## Next Steps

1. **Set Email Configuration** - See EMAIL_CONFIGURATION.md
2. **Run Application** - `./mvnw spring-boot:run -pl api`
3. **Test Signup** - Create a test user account
4. **Verify Email** - Check email for OTP and verify
5. **Login** - Log in with verified account

---

## Additional Resources

- [OTP_FEATURE_DOCUMENTATION.md](OTP_FEATURE_DOCUMENTATION.md) - Comprehensive documentation
- [EMAIL_CONFIGURATION.md](EMAIL_CONFIGURATION.md) - Email setup guide
- [README.md](../../README.md) - Main project documentation

---

## Support

For issues or questions:
1. Check [EMAIL_CONFIGURATION.md](EMAIL_CONFIGURATION.md) Troubleshooting section
2. Review [OTP_FEATURE_DOCUMENTATION.md](OTP_FEATURE_DOCUMENTATION.md)
3. Check application logs for detailed error messages
4. Verify email configuration environment variables

