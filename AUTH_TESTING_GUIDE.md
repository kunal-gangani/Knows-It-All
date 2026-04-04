# 🔐 Authentication End-to-End Testing Guide

**Objective**: Verify that login/register flow works from Android emulator → Spring Boot backend → JWT token storage

---

## Prerequisites

### 1. Spring Boot Backend Running

**Start your backend server** (if you have one running locally):

```bash
# In your backend directory
./mvnw spring-boot:run
# OR
gradle bootRun
```

**Expected**: Server runs on `http://localhost:8080`

### 2. Android Emulator Started

In Android Studio:
```
Device Manager → Select emulator → Start
```

**Default emulator IP** for backend access: `http://10.0.2.2:8080/api/v1/`
- `10.0.2.2` = Host machine's localhost (special alias in Android emulator)
- `8080` = Backend port
- `/api/v1/` = API version prefix

---

## Step 1: Verify Backend Endpoints Exist

### Test API Connectivity

**Using curl (from your machine):**

```bash
# Test backend is running
curl -X GET http://localhost:8080/api/v1/health

# Should return 200 OK or similar health check response
```

### Required Endpoints (Backend Must Have)

```
POST   /api/v1/users/register    - User registration
POST   /api/v1/users/login       - User authentication
GET    /api/v1/users/{uid}       - Get user profile
```

---

## Step 2: Build and Deploy App to Emulator

### Build Debug APK

```bash
cd d:\Projects\KnowItAll

# Build debug APK
./gradlew assembleDebug

# Expected: BUILD SUCCESSFUL
```

### Deploy to Emulator

```bash
# Install on running emulator
./gradlew installDebug

# Expected: App appears on emulator home screen
```

### Verify Installation

```bash
# List installed packages
adb shell pm list packages | grep know_it_all

# Should show: com.example.know_it_all
```

---

## Step 3: Test Registration Flow

### Open App on Emulator

1. Open Android Emulator
2. Find and tap **KnowItAll** app
3. You should see the **Login Screen**

### Perform Registration

**On the Login screen**, tap: **"Don't have an account? Register"**

**On the Register screen**, enter:

| Field | Value |
|-------|-------|
| Full Name | `Test User` |
| Email | `test@knowitall.com` |
| Password | `TestPass123` |
| Confirm Password | `TestPass123` |

### Expected Behavior

✅ **Form Validation:**
- Name must be 2+ chars
- Email must be valid format
- Password must be 6+ chars
- Passwords must match

✅ **On Submit:**
- Loading spinner appears
- API call: `POST /api/v1/users/register`
- Request body:
  ```json
  {
    "name": "Test User",
    "email": "test@knowitall.com",
    "password": "TestPass123"
  }
  ```

✅ **On Success:**
- JWT token received in response
- Token stored in SharedPreferences (`KnowItAllPrefs`)
- Navigation → **Radar Screen**
- User sees map/skill list

✅ **On Error:**
- Error message displayed below button
- Examples: "User already exists", "Invalid email", etc.

---

## Step 4: Verify Token Storage

### Check Token is Saved Locally

**From Android Studio Terminal:**

```adb
# Access emulator shell
adb shell

# Navigate to app preferences
run-as com.example.know_it_all
cat /data/data/com.example.know_it_all/shared_prefs/KnowItAllPrefs.xml

# Should output XML like:
# <string name="jwt_token">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</string>
```

**Token Format:**
- JWT token = three parts separated by `.`
- Example: `header.payload.signature`

---

## Step 5: Test Login Flow

### Return to Login Screen

If you're on Radar screen, use device back button to logout and return to Login.

Or update AuthViewModel to add explicit logout button:

```kotlin
// Quick test: Add to any screen temporarily
Button(onClick = { authViewModel.logout() }) {
    Text("Logout")
}
```

### Perform Login

**On Login screen**, enter:

| Field | Value |
|-------|-------|
| Email | `test@knowitall.com` |
| Password | `TestPass123` |

### Expected Behavior

✅ **Form Validation:**
- Email format validation
- Password 6+ chars validation

✅ **On Submit:**
- Loading spinner appears
- API call: `POST /api/v1/users/login`
- Request body:
  ```json
  {
    "email": "test@knowitall.com",
    "password": "TestPass123"
  }
  ```

✅ **On Success:**
- JWT token received
- Token stored (same as registration)
- Navigation → **Radar Screen**

✅ **On Error:**
- Error message displayed: "Login failed: Invalid credentials" etc.

---

## Step 6: Verify Token in API Requests

### Monitor Network Requests

In Android Studio, use **Network Inspector** to see actual requests:

```
View → Tool Windows → Profiler → Network
```

### Check Token Headers

After successful login, perform any API call (e.g., get nearby users):

**Expected Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Verify in RetrofitClient:**

```kotlin
// From RetrofitClient.kt
private val httpClient: OkHttpClient
    get() {
        val clientBuilder = OkHttpClient.Builder()
            // Add interceptor that adds Authorization header
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer ${sessionManager.getToken()}")
                    .build()
                chain.proceed(newRequest)
            }
        // ...
    }
```

---

## Step 7: Test Error Scenarios

### Scenario 1: Invalid Email on Register

**Input:**
- Full Name: `Test`
- Email: `not-an-email`
- Password: `TestPass123`
- Confirm: `TestPass123`

**Expected:**
- ❌ Register button disabled
- ❌ Error message under email field: "Invalid email format"

---

### Scenario 2: Passwords Don't Match

**Input:**
- Full Name: `Test`
- Email: `test2@knowitall.com`
- Password: `TestPass123`
- Confirm: `DifferentPass`

**Expected:**
- ❌ Register button disabled
- ❌ Error under confirm field: "Passwords do not match"

---

### Scenario 3: Server Offline

**Stop backend server**, then try to login:

**Expected:**
- 🕐 Loading spinner shows briefly
- ❌ Error message: "Login failed: [connection error details]"
- Form remains on screen, user can retry

---

### Scenario 4: User Not Found

**Input (on Login):**
- Email: `nonexistent@example.com`
- Password: `TestPass123`

**Expected (from backend):**
- 🕐 Loading spinner
- ❌ Error message: "Login failed: User not found" (or similar backend message)

---

### Scenario 5: Wrong Password

**Input (on Login):**
- Email: `test@knowitall.com` (registered)
- Password: `WrongPassword123`

**Expected:**
- 🕐 Loading spinner
- ❌ Error message: "Login failed: Invalid credentials" (or similar)

---

## Step 8: End-to-End Flow Checklist

### Complete Flow Test

Perform this sequence and check all items:

- [ ] 1. App starts → Shows **Login Screen**
- [ ] 2. Click "Register" → Shows **Register Screen**
- [ ] 3. Fill in all fields (valid format)
- [ ] 4. Click Register button
- [ ] 5. Loading spinner appears
- [ ] 6. Backend receives request (check backend logs)
- [ ] 7. JWT token returned in response
- [ ] 8. Token stored locally
- [ ] 9. Navigation to **Radar Screen** succeeds
- [ ] 10. No errors on Radar screen
- [ ] 11. Logout (or close app and reopen)
- [ ] 12. App shows **Login Screen** (not remembered) OR shows **Radar** (session remembered)
- [ ] 13. If session remembered, Radar loads without login
- [ ] 14. Go back to Login
- [ ] 15. Click "Login" and enter credentials
- [ ] 16. Loading spinner appears
- [ ] 17. Backend receives login request
- [ ] 18. JWT token returned
- [ ] 19. Token updated locally
- [ ] 20. Navigation to **Radar Screen** succeeds

---

## Troubleshooting

### Issue: "Connection refused" error

**Problem:** Backend not running or wrong port

**Solution:**
1. Check backend is running: `http://localhost:8080`
2. Verify port in `RetrofitClient.kt`: `BASE_URL = "http://10.0.2.2:8080/api/v1/"`
3. Restart emulator and backend

---

### Issue: Form validation errors won't clear

**Problem:** Validation state stuck

**Solution:**
1. Clear app data: `adb shell pm clear com.example.know_it_all`
2. Rebuild and redeploy: `./gradlew installDebug`

---

### Issue: Token not stored in SharedPreferences

**Problem:** SessionManager not being called

**Solution:**
1. Verify `AuthViewModel.kt` calls `sessionManager.saveToken(token)`
2. Check `SessionManager.kt` has `MODE_PRIVATE` for shared prefs
3. Add debug logging:
   ```kotlin
   fun saveToken(token: String) {
       Log.d("SessionManager", "Saving token: $token")
       prefs.edit().putString(KEY_TOKEN, token).apply()
   }
   ```

---

### Issue: Can't see backend logs

**Problem:** Can't debug API calls

**Solution:**
1. Check `RetrofitClient.kt` has logging interceptor enabled for DEBUG builds
2. In Android Studio, use **Logcat** to see network logs:
   ```
   View → Tool Windows → Logcat
   Filter: "OkHttp" or "Retrofit"
   ```

---

## Success Criteria

✅ **All of the following must pass:**

1. ✅ Registration: Valid form → User created → Token stored → Navigates to Radar
2. ✅ Login: Valid credentials → Token stored → Navigates to Radar
3. ✅ Validation: Invalid email/password shows error messages
4. ✅ Error Handling: Server errors show user-friendly messages
5. ✅ Token Usage: Subsequent API calls include JWT in Authorization header
6. ✅ Session: App remembers login after restart (or re-prompts as designed)

---

## Next Steps (After Auth Verified)

Once auth flow is 100% working:

1. ✅ **Step 4**: Implement Skill Radar Screen
2. ✅ **Step 5**: Implement Trade Center Screen
3. ✅ **Step 6**: Implement The Vault Screen
4. ✅ **Step 7**: Implement Profile Screen

---

## Quick Reference: API Responses

### Successful Registration Response

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "userId": "user123"
  },
  "error": null
}
```

### Successful Login Response

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "user123"
  },
  "error": null
}
```

### Error Response

```json
{
  "success": false,
  "data": null,
  "error": "Invalid email or password"
}
```

---

**Test Duration**: ~15-20 minutes for complete end-to-end verification

Last Updated: April 2026
