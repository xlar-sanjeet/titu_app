-- Run this in Supabase SQL Editor.
-- This sets up the minimum backend required for the current app flow:
-- 1. profiles table
-- 2. artist_profiles table
-- 3. artist-selfies storage bucket
-- 4. row level security policies

create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  role text not null check (role in ('artist', 'user', 'admin')),
  full_name text,
  phone text,
  email text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.profiles
  drop constraint if exists profiles_id_fkey;

create table if not exists public.artist_profiles (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null unique references public.profiles(id) on delete cascade,
  brand_name text,
  city text,
  state text,
  years_of_experience text,
  bio text,
  identity_verified boolean not null default false,
  live_selfie_url text,
  onboarding_status text not null default 'draft',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.artist_profiles
  add column if not exists categories jsonb not null default '[]'::jsonb,
  add column if not exists styles jsonb not null default '[]'::jsonb,
  add column if not exists price_range numeric,
  add column if not exists customization_enabled boolean not null default false,
  add column if not exists customization_time text,
  add column if not exists bulk_order_enabled boolean not null default false,
  add column if not exists bulk_capacity text,
  add column if not exists materials jsonb not null default '[]'::jsonb,
  add column if not exists delivery_city text,
  add column if not exists same_city_delivery boolean not null default false,
  add column if not exists delivery_speed text,
  add column if not exists portfolio_urls jsonb not null default '[]'::jsonb,
  add column if not exists story_bio text;

create table if not exists public.artist_live_services (
  id uuid primary key default gen_random_uuid(),
  artist_id uuid not null unique references public.profiles(id) on delete cascade,
  is_active boolean not null default false,
  mode text,
  service_types jsonb not null default '[]'::jsonb,
  available_cities jsonb not null default '[]'::jsonb,
  travel_radius_km numeric,
  charge_per_session numeric,
  available_dates jsonb not null default '[]'::jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists set_profiles_updated_at on public.profiles;
create trigger set_profiles_updated_at
before update on public.profiles
for each row
execute function public.set_updated_at();

drop trigger if exists set_artist_profiles_updated_at on public.artist_profiles;
create trigger set_artist_profiles_updated_at
before update on public.artist_profiles
for each row
execute function public.set_updated_at();

drop trigger if exists set_artist_live_services_updated_at on public.artist_live_services;
create trigger set_artist_live_services_updated_at
before update on public.artist_live_services
for each row
execute function public.set_updated_at();

alter table public.profiles enable row level security;
alter table public.artist_profiles enable row level security;
alter table public.artist_live_services enable row level security;

drop policy if exists "profiles_select_own" on public.profiles;
create policy "profiles_select_own"
on public.profiles
for select
to authenticated
using (id = auth.uid());

drop policy if exists "profiles_select_local_onboarding" on public.profiles;
create policy "profiles_select_local_onboarding"
on public.profiles
for select
to public
using (true);

drop policy if exists "profiles_insert_own" on public.profiles;
create policy "profiles_insert_own"
on public.profiles
for insert
to authenticated
with check (id = auth.uid());

drop policy if exists "profiles_insert_local_onboarding" on public.profiles;
create policy "profiles_insert_local_onboarding"
on public.profiles
for insert
to public
with check (true);

drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own"
on public.profiles
for update
to authenticated
using (id = auth.uid())
with check (id = auth.uid());

drop policy if exists "profiles_update_local_onboarding" on public.profiles;
create policy "profiles_update_local_onboarding"
on public.profiles
for update
to public
using (true)
with check (true);

drop policy if exists "artist_profiles_select_own" on public.artist_profiles;
create policy "artist_profiles_select_own"
on public.artist_profiles
for select
to authenticated
using (user_id = auth.uid());

drop policy if exists "artist_profiles_select_seeded_demo" on public.artist_profiles;
create policy "artist_profiles_select_seeded_demo"
on public.artist_profiles
for select
to public
using (
  user_id in (
    '11111111-1111-1111-1111-111111111111'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid
  )
);

drop policy if exists "artist_profiles_select_local_onboarding" on public.artist_profiles;
create policy "artist_profiles_select_local_onboarding"
on public.artist_profiles
for select
to public
using (true);

drop policy if exists "artist_profiles_insert_own" on public.artist_profiles;
create policy "artist_profiles_insert_own"
on public.artist_profiles
for insert
to authenticated
with check (user_id = auth.uid());

drop policy if exists "artist_profiles_insert_local_onboarding" on public.artist_profiles;
create policy "artist_profiles_insert_local_onboarding"
on public.artist_profiles
for insert
to public
with check (true);

drop policy if exists "artist_profiles_update_own" on public.artist_profiles;
create policy "artist_profiles_update_own"
on public.artist_profiles
for update
to authenticated
using (user_id = auth.uid())
with check (user_id = auth.uid());

drop policy if exists "artist_profiles_update_local_onboarding" on public.artist_profiles;
create policy "artist_profiles_update_local_onboarding"
on public.artist_profiles
for update
to public
using (true)
with check (true);

drop policy if exists "artist_live_services_select_own" on public.artist_live_services;
create policy "artist_live_services_select_own"
on public.artist_live_services
for select
to authenticated
using (artist_id = auth.uid());

drop policy if exists "artist_live_services_select_seeded_demo" on public.artist_live_services;
create policy "artist_live_services_select_seeded_demo"
on public.artist_live_services
for select
to public
using (
  artist_id in (
    '11111111-1111-1111-1111-111111111111'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid
  )
);

drop policy if exists "artist_live_services_select_local_onboarding" on public.artist_live_services;
create policy "artist_live_services_select_local_onboarding"
on public.artist_live_services
for select
to public
using (true);

drop policy if exists "artist_live_services_insert_own" on public.artist_live_services;
create policy "artist_live_services_insert_own"
on public.artist_live_services
for insert
to authenticated
with check (artist_id = auth.uid());

drop policy if exists "artist_live_services_insert_local_onboarding" on public.artist_live_services;
create policy "artist_live_services_insert_local_onboarding"
on public.artist_live_services
for insert
to public
with check (true);

drop policy if exists "artist_live_services_update_own" on public.artist_live_services;
create policy "artist_live_services_update_own"
on public.artist_live_services
for update
to authenticated
using (artist_id = auth.uid())
with check (artist_id = auth.uid());

drop policy if exists "artist_live_services_update_local_onboarding" on public.artist_live_services;
create policy "artist_live_services_update_local_onboarding"
on public.artist_live_services
for update
to public
using (true)
with check (true);

insert into storage.buckets (id, name, public)
values ('artist-selfies', 'artist-selfies', false)
on conflict (id) do nothing;

insert into storage.buckets (id, name, public)
values ('artist-works', 'artist-works', false)
on conflict (id) do nothing;

drop policy if exists "artist_selfies_insert_own" on storage.objects;
create policy "artist_selfies_insert_own"
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'artist-selfies'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_selfies_select_own" on storage.objects;
create policy "artist_selfies_select_own"
on storage.objects
for select
to authenticated
using (
  bucket_id = 'artist-selfies'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_selfies_update_own" on storage.objects;
create policy "artist_selfies_update_own"
on storage.objects
for update
to authenticated
using (
  bucket_id = 'artist-selfies'
  and split_part(name, '/', 1) = auth.uid()::text
)
with check (
  bucket_id = 'artist-selfies'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_selfies_delete_own" on storage.objects;
create policy "artist_selfies_delete_own"
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'artist-selfies'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_works_insert_own" on storage.objects;
create policy "artist_works_insert_own"
on storage.objects
for insert
to authenticated
with check (
  bucket_id = 'artist-works'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_works_select_own" on storage.objects;
create policy "artist_works_select_own"
on storage.objects
for select
to authenticated
using (
  bucket_id = 'artist-works'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_works_update_own" on storage.objects;
create policy "artist_works_update_own"
on storage.objects
for update
to authenticated
using (
  bucket_id = 'artist-works'
  and split_part(name, '/', 1) = auth.uid()::text
)
with check (
  bucket_id = 'artist-works'
  and split_part(name, '/', 1) = auth.uid()::text
);

drop policy if exists "artist_works_delete_own" on storage.objects;
create policy "artist_works_delete_own"
on storage.objects
for delete
to authenticated
using (
  bucket_id = 'artist-works'
  and split_part(name, '/', 1) = auth.uid()::text
);

notify pgrst, 'reload schema';
