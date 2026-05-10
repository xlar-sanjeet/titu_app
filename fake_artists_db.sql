-- 1. Create fake users in auth.users (Needed for RLS to work properly)
-- Note: You MUST run this as a Superuser or via the Supabase SQL Editor.
INSERT INTO auth.users (id, instance_id, email, aud, role, encrypted_password, email_confirmed_at)
VALUES 
  ('11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000000', 'picasso@titu.com', 'authenticated', 'authenticated', crypt('123456', gen_salt('bf')), now()),
  ('22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000000', 'vangogh@titu.com', 'authenticated', 'authenticated', crypt('123456', gen_salt('bf')), now())
ON CONFLICT (id) DO UPDATE SET
  instance_id = EXCLUDED.instance_id,
  email = EXCLUDED.email,
  encrypted_password = EXCLUDED.encrypted_password,
  email_confirmed_at = EXCLUDED.email_confirmed_at;

UPDATE auth.users
SET
  confirmation_token = '',
  recovery_token = '',
  email_change = '',
  email_change_token_new = '',
  raw_app_meta_data = jsonb_build_object('provider', 'email', 'providers', jsonb_build_array('email')),
  raw_user_meta_data = jsonb_build_object(
    'sub', id::text,
    'email', email,
    'email_verified', true,
    'phone_verified', false
  ),
  is_sso_user = false,
  is_anonymous = false
WHERE id in (
  '11111111-1111-1111-1111-111111111111',
  '22222222-2222-2222-2222-222222222222'
);

INSERT INTO auth.identities (
  id,
  provider_id,
  user_id,
  identity_data,
  provider,
  last_sign_in_at,
  created_at,
  updated_at
)
VALUES
  (
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    '{"sub": "11111111-1111-1111-1111-111111111111", "email": "picasso@titu.com", "email_verified": true, "phone_verified": false}'::jsonb,
    'email',
    now(),
    now(),
    now()
  ),
  (
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222222',
    '{"sub": "22222222-2222-2222-2222-222222222222", "email": "vangogh@titu.com", "email_verified": true, "phone_verified": false}'::jsonb,
    'email',
    now(),
    now(),
    now()
  )
ON CONFLICT (provider, provider_id) DO UPDATE SET
  identity_data = EXCLUDED.identity_data,
  updated_at = EXCLUDED.updated_at;

-- 2. Populate app profiles. artist_profiles.user_id references public.profiles(id).
INSERT INTO public.profiles (id, role, full_name, email)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'artist', 'Pablo Picasso', 'picasso@titu.com'),
  ('22222222-2222-2222-2222-222222222222', 'artist', 'Vincent Van Gogh', 'vangogh@titu.com')
ON CONFLICT (id) DO UPDATE SET
  role = EXCLUDED.role,
  full_name = EXCLUDED.full_name,
  email = EXCLUDED.email;

-- 3. Populate their Artist Profiles
INSERT INTO public.artist_profiles (
  user_id, 
  brand_name, 
  city, 
  categories, 
  styles, 
  onboarding_status
)
VALUES 
  (
    '11111111-1111-1111-1111-111111111111', 
    'Picasso Modern Art', 
    'Mumbai', 
    '["Painting", "Digital Art"]'::jsonb, 
    '["Modern", "Minimal"]'::jsonb, 
    'completed'
  ),
  (
    '22222222-2222-2222-2222-222222222222', 
    'Van Gogh Expressions', 
    'Delhi', 
    '["Portraits", "Sketches"]'::jsonb, 
    '["Traditional"]'::jsonb, 
    'completed'
  )
ON CONFLICT (user_id) DO UPDATE SET 
  brand_name = EXCLUDED.brand_name,
  city = EXCLUDED.city,
  categories = EXCLUDED.categories,
  styles = EXCLUDED.styles,
  onboarding_status = EXCLUDED.onboarding_status;

-- 4. Populate Live Services for the artist that has it.
DELETE FROM public.artist_live_services
WHERE artist_id = '11111111-1111-1111-1111-111111111111';

INSERT INTO public.artist_live_services (artist_id, is_active, mode, service_types)
VALUES 
  (
    '22222222-2222-2222-2222-222222222222', 
    true, 
    'Physical Live Art', 
    '["Birthday Portrait", "Corporate Event Mural"]'::jsonb
  )
ON CONFLICT (artist_id) DO UPDATE SET 
  is_active = EXCLUDED.is_active,
  mode = EXCLUDED.mode,
  service_types = EXCLUDED.service_types;

-- 5. Remove old draft/demo artist rows. New artists should be created by the app.
DELETE FROM auth.users
WHERE id = '33333333-3333-3333-3333-333333333333';
