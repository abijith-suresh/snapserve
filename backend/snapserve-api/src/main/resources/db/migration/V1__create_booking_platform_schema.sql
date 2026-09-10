CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX accounts_email_lower_key ON accounts (LOWER(email));

CREATE TABLE account_roles (
    account_id UUID NOT NULL REFERENCES accounts (id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'PROVIDER')),
    PRIMARY KEY (account_id, role)
);

CREATE TABLE customer_profiles (
    account_id UUID PRIMARY KEY REFERENCES accounts (id) ON DELETE CASCADE,
    display_name VARCHAR(120) NOT NULL,
    phone VARCHAR(32),
    address_line_1 VARCHAR(200),
    address_line_2 VARCHAR(200),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE provider_profiles (
    account_id UUID PRIMARY KEY REFERENCES accounts (id) ON DELETE CASCADE,
    display_name VARCHAR(120) NOT NULL,
    headline VARCHAR(160),
    bio TEXT,
    phone VARCHAR(32),
    service_area VARCHAR(200),
    years_experience INTEGER CHECK (years_experience IS NULL OR years_experience >= 0),
    timezone VARCHAR(64) NOT NULL DEFAULT 'UTC',
    profile_photo_url TEXT,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_offerings (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES provider_profiles (account_id) ON DELETE CASCADE,
    title VARCHAR(160) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    pricing_type VARCHAR(10) NOT NULL CHECK (pricing_type IN ('FIXED', 'HOURLY')),
    price NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    minimum_duration_minutes INTEGER CHECK (
        minimum_duration_minutes IS NULL OR minimum_duration_minutes > 0
    ),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_offerings_provider_title_key UNIQUE (provider_id, title)
);

CREATE INDEX service_offerings_category_idx ON service_offerings (category);
CREATE INDEX service_offerings_provider_active_idx ON service_offerings (provider_id, is_active);

CREATE TABLE provider_availability (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES provider_profiles (account_id) ON DELETE CASCADE,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    timezone VARCHAR(64) NOT NULL DEFAULT 'UTC',
    CONSTRAINT provider_availability_time_range_check CHECK (end_time > start_time),
    CONSTRAINT provider_availability_slot_key UNIQUE (provider_id, day_of_week, start_time, end_time)
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customer_profiles (account_id),
    provider_id UUID NOT NULL REFERENCES provider_profiles (account_id),
    service_offering_id UUID NOT NULL REFERENCES service_offerings (id),
    scheduled_start TIMESTAMPTZ NOT NULL,
    scheduled_end TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (
        status IN ('REQUESTED', 'CONFIRMED', 'DECLINED', 'CANCELLED', 'COMPLETED')
    ),
    agreed_price NUMERIC(12, 2) NOT NULL CHECK (agreed_price > 0),
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    service_address_line_1 VARCHAR(200) NOT NULL,
    service_address_line_2 VARCHAR(200),
    service_city VARCHAR(100) NOT NULL,
    service_postal_code VARCHAR(20),
    customer_note TEXT,
    provider_note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT bookings_time_range_check CHECK (scheduled_end > scheduled_start)
);

CREATE INDEX bookings_customer_schedule_idx ON bookings (customer_id, scheduled_start DESC);
CREATE INDEX bookings_provider_schedule_idx ON bookings (provider_id, scheduled_start DESC);
CREATE INDEX bookings_status_idx ON bookings (status);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL UNIQUE REFERENCES bookings (id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES customer_profiles (account_id),
    provider_id UUID NOT NULL REFERENCES provider_profiles (account_id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX reviews_provider_idx ON reviews (provider_id, created_at DESC);
