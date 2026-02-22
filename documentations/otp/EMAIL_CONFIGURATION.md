# Email Configuration Guide for OTP Feature

This guide will help you set up email sending for the OTP (One-Time Password) feature in SportsFolio.

## Table of Contents
- [Quick Start](#quick-start)
- [Gmail Configuration](#gmail-configuration)
- [Other Email Providers](#other-email-providers)
- [Environment Variables](#environment-variables)
- [Testing Email Configuration](#testing-email-configuration)
- [Troubleshooting](#troubleshooting)

---

## Quick Start

### 1. Set Environment Variables

For macOS/Linux (add to your `.zshrc`, `.bashrc`, or `.env` file):

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

For Windows (PowerShell):

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
```

### 2. Verify Configuration

The application will use these environment variables automatically. If not set, it will use default values from `application.yml`.

### 3. Run the Application

```bash
cd /Users/adarshverma013/Developer/SportsFolio
./mvnw spring-boot:run -pl api
```

---

## Gmail Configuration

### Prerequisites
- A Gmail account
- 2-Factor Authentication enabled on your Google Account

### Step-by-Step Setup

#### Step 1: Enable 2-Factor Authentication
1. Go to [myaccount.google.com](https://myaccount.google.com)
2. Click "Security" in the left sidebar
3. Under "How you sign in to Google", enable "2-Step Verification"
4. Follow the prompts to complete 2FA setup

#### Step 2: Generate App Password
1. Go to [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
2. Select "Mail" as the app
3. Select "Windows Computer", "Mac", or "Linux" (depending on your device)
4. Click "Generate"
5. Google will show a 16-character password
6. **Copy this password** - you'll use it as MAIL_PASSWORD

#### Step 3: Set Environment Variables

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=abcd1234efgh5678  # 16-character app password
```

#### Step 4: Verify Configuration

To test if email is working, run the application and try to signup:

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234",
    "emailId": "your-email@gmail.com"
  }'
```

Check your email inbox for the OTP.

---

## Other Email Providers

### Microsoft Outlook/Hotmail

**Configuration:**
```bash
export MAIL_HOST=smtp-mail.outlook.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@outlook.com
export MAIL_PASSWORD=your-password
```

**Note:** You may need to [allow less secure apps](https://account.live.com/cv) or use an app password.

### Yahoo Mail

**Configuration:**
```bash
export MAIL_HOST=smtp.mail.yahoo.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@yahoo.com
export MAIL_PASSWORD=your-app-password
```

**Setup:**
1. Go to [account.yahoo.com](https://account.yahoo.com)
2. Click "Account Security"
3. Generate an "App Password"
4. Use the app password for MAIL_PASSWORD

### SendGrid

**Configuration:**
```bash
export MAIL_HOST=smtp.sendgrid.net
export MAIL_PORT=587
export MAIL_USERNAME=apikey
export MAIL_PASSWORD=SG.your-sendgrid-api-key
```

**Setup:**
1. Create a SendGrid account at [sendgrid.com](https://sendgrid.com)
2. Generate an API key
3. Use "apikey" as the username and the API key as the password

### AWS SES (Simple Email Service)

**Configuration:**
```bash
export MAIL_HOST=email-smtp.us-east-1.amazonaws.com
export MAIL_PORT=587
export MAIL_USERNAME=your-smtp-username
export MAIL_PASSWORD=your-smtp-password
```

**Setup:**
1. Create an AWS SES account
2. Verify your email address or domain
3. Create SMTP credentials in SES console
4. Use the provided username and password

### Custom SMTP Server

**Configuration:**
```bash
export MAIL_HOST=your-smtp-server.com
export MAIL_PORT=587
export MAIL_USERNAME=your-username
export MAIL_PASSWORD=your-password
```

---

## Environment Variables

### application.yml Configuration

The application reads email configuration from environment variables and falls back to default values:

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

### Setting Environment Variables

#### macOS/Linux (Temporary)

For current session only:
```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

#### macOS/Linux (Permanent)

Add to `~/.zshrc` or `~/.bashrc`:

```bash
# Email Configuration for SportsFolio
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

Then reload your shell:
```bash
source ~/.zshrc  # For zsh
# or
source ~/.bashrc  # For bash
```

#### Windows (Temporary - PowerShell)

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
```

#### Windows (Permanent - Environment Variables)

1. Press `Win + X` and select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "User variables", click "New"
5. Add each variable:
   - Variable name: `MAIL_HOST`
   - Variable value: `smtp.gmail.com`
6. Click OK and restart your IDE/terminal

#### Using .env File (with Spring Boot)

Create a `.env` file in the project root:

```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

Note: You may need to install `spring-dotenv` dependency for this to work:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>3.0.0</version>
</dependency>
```

---

## Testing Email Configuration

### Test 1: Sign Up and Check Email

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234",
    "emailId": "your-test-email@gmail.com"
  }'
```

Expected response:
```json
"User signed up successfully. OTP sent to your email. Please verify your email to login."
```

Check your email inbox for the OTP.

### Test 2: Verify OTP

```bash
curl -X POST http://localhost:8080/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "otp": "123456"  # Replace with actual OTP from email
  }'
```

Expected response:
```json
"Email verified successfully. You can now login."
```

### Test 3: Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@1234"
  }'
```

Expected response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userDTO": {
    "id": 1,
    "name": "testuser",
    "email": "your-test-email@gmail.com",
    "emailVerified": true,
    "userRole": "USER"
  }
}
```

### Test 4: Resend OTP

```bash
curl -X POST http://localhost:8080/auth/resend-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com"
  }'
```

Expected response:
```json
"OTP resent to your email. Please verify within 10 minutes."
```

Check your email for the new OTP.

---

## Troubleshooting

### Email Not Sending

#### Issue: "Failed to connect to mail server"

**Causes:**
- MAIL_HOST is incorrect
- MAIL_PORT is incorrect
- Network/firewall blocking SMTP connection
- SMTP server is down

**Solutions:**
1. Verify MAIL_HOST and MAIL_PORT are correct for your email provider
2. Test connection with telnet:
   ```bash
   telnet smtp.gmail.com 587
   ```
3. Check firewall settings to allow SMTP port
4. Verify email provider's SMTP server status

#### Issue: "Authentication failed" or "Invalid credentials"

**Causes:**
- MAIL_USERNAME is incorrect
- MAIL_PASSWORD is incorrect
- Gmail App Password not generated correctly
- Account permissions not set

**Solutions:**
1. For Gmail:
   - Verify 2-Factor Authentication is enabled
   - Regenerate App Password
   - Use exactly the 16-character app password (no spaces)
2. For other providers:
   - Verify username/email is correct
   - Check if special characters need escaping
   - Verify account is enabled for SMTP access

#### Issue: "STARTTLS is required but not supported"

**Cause:** STARTTLS is not enabled on the SMTP server

**Solution:** 
- Ensure MAIL_PORT is 587 (TLS) not 465 (SSL)
- If using port 465, add this to application.yml:
  ```yaml
  spring:
    mail:
      properties:
        mail:
          smtp:
            socketFactory:
              port: 465
              class: javax.net.ssl.SSLSocketFactory
  ```

#### Issue: Application logs show no error but email not received

**Causes:**
- Email is going to spam folder
- Email address is incorrect
- Email provider's rate limiting

**Solutions:**
1. Check spam/junk folder
2. Verify email address in signup request
3. Wait a minute and try again
4. Check email provider's rate limiting policies

### Signature/SSL Certificate Issues

**Issue:** "javax.mail.MessagingException: Could not convert socket to TLS"

**Solutions:**
1. Update Java to the latest version
2. Ensure STARTTLS is properly configured
3. For Gmail, ensure port 587 is used (not 465)

### Emails Being Rejected

**Issue:** "550 5.1.1 The email account that you tried to reach does not exist"

**Causes:**
- Email address is blocked
- Email address doesn't exist
- Email provider's account issue

**Solutions:**
1. Verify the destination email address is correct
2. Check if the email address is properly formatted
3. Verify email provider account is active

### Testing Without Email Sending

For development/testing without actual email sending, you can use a mock email service:

1. Create a mock EmailService:
```java
@Service
@Profile("test")
public class MockEmailService extends EmailService {
    @Override
    public void sendOTPEmail(String email, String otp) {
        System.out.println("MOCK EMAIL: Sending OTP " + otp + " to " + email);
        // Log to console instead of sending email
    }
}
```

2. Run tests with test profile:
```bash
./mvnw test -Dspring.profiles.active=test
```

### Application Won't Start

**Issue:** Application crashes with mail configuration error on startup

**Solutions:**
1. Ensure all MAIL_* environment variables are set
2. Remove environment variables to use defaults
3. Check logs for specific error message
4. Verify application.yml syntax

---

## Security Best Practices

1. **Never commit credentials to version control**
   - Use environment variables or `.env` files (add to `.gitignore`)
   - Use secret management tools for production

2. **Use app-specific passwords**
   - Don't use your main email password
   - Use provider's app password feature
   - Rotate passwords regularly

3. **Enable 2-Factor Authentication**
   - Required for generating app passwords on Gmail
   - Adds extra security layer

4. **Use TLS/STARTTLS**
   - Ensure encryption is enabled (port 587)
   - Never send credentials over unencrypted connection

5. **Limit email sending rate**
   - Implement rate limiting to prevent abuse
   - Monitor for unusual email sending patterns

---

## Production Setup

For production deployment, use a dedicated email service:

1. **SendGrid** - Reliable, scalable, good pricing
2. **AWS SES** - Integration with AWS ecosystem
3. **Mailgun** - Developer-friendly, good documentation
4. **Sendmail** - Self-hosted solution
5. **Office 365** - Enterprise email service

Recommended production configuration:

```yaml
spring:
  mail:
    host: ${MAIL_HOST}  # Use environment variable
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 10000
          writetimeout: 10000
          socketFactory:
            fallback: false
```

---

## Additional Resources

- [Spring Boot Mail Configuration](https://spring.io/guides/gs/sending-email/)
- [Gmail SMTP Settings](https://support.google.com/a/answer/176600)
- [SendGrid Integration](https://sendgrid.com/docs/for-developers/sending-email/integrations/spring/)
- [AWS SES Configuration](https://docs.aws.amazon.com/ses/latest/dg/send-email-smtp.html)

