# OTP-Based Password Reset - Quick Reference

## Overview
Simple OTP-based password reset flow. Users request an OTP via email, then reset their password using that OTP.

## Two New Endpoints

### 1. Request Password Reset OTP
```
POST /auth/forgot-password
Content-Type: application/json

Request:
{
    "email": "user@example.com"
}

Response (200 OK):
{
    "message": "OTP has been sent to your email. Please check your inbox.",
    "email": "user@example.com"
}
```

### 2. Reset Password with OTP
```
POST /auth/reset-password
Content-Type: application/json

Request:
{
    "email": "user@example.com",
    "otp": "123456",
    "newPassword": "NewPassword@123",
    "confirmPassword": "NewPassword@123"
}

Response (200 OK):
"Password reset successfully. You can now login with your new password."
```

## Flow Diagram

```
User clicks "Forgot Password"
         ↓
POST /auth/forgot-password (email)
         ↓
System validates user exists
         ↓
System generates 6-digit OTP
         ↓
OTP sent to user's email (valid for 10 minutes)
         ↓
User receives email with OTP
         ↓
User submits OTP + new password
         ↓
POST /auth/reset-password (email, otp, password, confirm)
         ↓
System validates OTP
         ↓
System validates password strength
         ↓
System updates password (BCrypt)
         ↓
User can login with new password ✓
```

## Request/Response Examples

### Example 1: Request OTP
```bash
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com"}'
```

**Response:**
```json
{
    "message": "OTP has been sent to your email. Please check your inbox.",
    "email": "user@example.com"
}
```

### Example 2: Reset Password with OTP
```bash
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "otp": "123456",
    "newPassword": "SecurePass@123",
    "confirmPassword": "SecurePass@123"
  }'
```

**Response:**
```
Password reset successfully. You can now login with your new password.
```

## Error Scenarios

| Error | HTTP Status | Message |
|-------|------------|---------|
| User not found | 401 | User not found with this email |
| Invalid OTP | 401 | Invalid OTP code |
| OTP expired | 401 | OTP has expired. Please request a new OTP |
| OTP already used | 401 | OTP has already been verified |
| Passwords don't match | 401 | Passwords do not match |
| Weak password | 400 | Password validation error |

## Password Requirements

New password must have:
- ✓ Minimum 8 characters
- ✓ At least one uppercase letter (A-Z)
- ✓ At least one lowercase letter (a-z)
- ✓ At least one digit (0-9)
- ✓ At least one special character (!@#$%^&*)

**Valid:** `SecurePass@123`, `MyPassword#99`  
**Invalid:** `password`, `Pass123`, `PASS!`

## Key Features

✅ **Reuses existing OTP infrastructure** - No database changes needed
✅ **6-digit OTP** - Generated and sent to email
✅ **10-minute validity** - Configurable via `otp.validity.minutes`
✅ **One-time use** - OTP marked verified after use
✅ **Password validation** - Strong password enforced
✅ **BCrypt hashing** - Secure password storage
✅ **Error handling** - Specific messages for each scenario
✅ **Comprehensive logging** - All operations logged

## Files Created

1. `domain/src/main/java/com/tech/dto/ResetPasswordWithOTPRequest.java`
   - Request DTO with email, otp, newPassword, confirmPassword

## Files Modified

1. `api/src/main/java/com/tech/service/AuthService.java`
   - Added `forgotPassword(String email)` method
   - Added `resetPasswordWithOTP(String email, String otp, String newPassword, String confirmPassword)` method

2. `api/src/main/java/com/tech/controller/AuthController.java`
   - Added `POST /auth/forgot-password` endpoint
   - Added `POST /auth/reset-password` endpoint
   - Added import for `ResetPasswordWithOTPRequest`

## Implementation Details

### forgotPassword() Method
- Validates user exists
- Validates email format
- Deletes any existing unverified OTP
- Generates and sends new OTP
- OTP valid for 10 minutes

### resetPasswordWithOTP() Method
- Validates passwords match
- Validates password strength (8 chars, upper, lower, digit, special)
- Finds and validates OTP
- Checks OTP hasn't expired
- Checks OTP hasn't been used
- Updates user password with BCrypt
- Marks OTP as verified

## Testing Commands

### 1. Request OTP
```bash
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

### 2. Check database for OTP
```sql
SELECT otp_code FROM otp WHERE email='test@example.com' AND is_verified=false ORDER BY created_at DESC LIMIT 1;
```

### 3. Reset password with OTP
```bash
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456",
    "newPassword": "NewPass@123",
    "confirmPassword": "NewPass@123"
  }'
```

### 4. Login with new password
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"NewPass@123"}'
```

## Security

✅ OTP is 6 digits (1 million combinations)
✅ OTP expires after 10 minutes
✅ OTP can only be used once
✅ Passwords are BCrypt hashed
✅ Password strength validated
✅ Email ownership verified
✅ All operations logged

## Related Endpoints

- `POST /auth/signup` - Register new account
- `POST /auth/login` - Login with username/password
- `POST /auth/verify-otp` - Verify email during signup
- `POST /auth/resend-otp` - Resend OTP for signup

## Build Status

✅ **BUILD SUCCESSFUL**
- All modules compiled successfully
- No compilation errors
- Ready for deployment

## Status

✅ IMPLEMENTATION COMPLETE
✅ TESTED AND WORKING
✅ PRODUCTION READY

---

**Date:** February 23, 2026
**Status:** Ready for Production
