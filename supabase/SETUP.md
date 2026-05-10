**Supabase Setup**

I have prepared the SQL already in [artist_onboarding_v1.sql](/D:/titu_app/supabase/artist_onboarding_v1.sql).

You only need to do these steps in the Supabase dashboard:

1. Open your project.
2. Go to `SQL Editor`.
3. Create a new query.
4. Paste the full contents of [artist_onboarding_v1.sql](/D:/titu_app/supabase/artist_onboarding_v1.sql).
5. Click `Run`.

After that, do this:

1. Go to `Authentication`.
2. Open `Providers`.
3. Enable `Phone`.
4. Configure an SMS provider there.

Important:
- If phone auth is not enabled, `Send OTP` will fail.
- If an SMS provider is not configured, `Send OTP` will fail.

Then test this flow in the app:

1. Enter phone number.
2. Tap `Send OTP`.
3. Enter the OTP you receive.
4. Tap `Verify OTP`.
5. Upload live selfie.
6. Tap `Continue`.

If any step fails, send me the exact error message from the app and I’ll fix it.
