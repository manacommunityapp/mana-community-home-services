-- ==============================================================================
-- MANA COMMUNITY — HOME SERVICES / COMMUNITY HELP MODULE
-- PostgreSQL Database Schema & DDL
-- ==============================================================================

-- 1. Service Categories
CREATE TABLE IF NOT EXISTS home_service_category (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(100),
    supports_recurring BOOLEAN NOT NULL DEFAULT TRUE,
    supports_monthly BOOLEAN NOT NULL DEFAULT TRUE,
    supports_daily BOOLEAN NOT NULL DEFAULT TRUE,
    supports_hourly BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsc_active_order ON home_service_category (active, display_order);

-- 2. Home Service Workers
CREATE TABLE IF NOT EXISTS home_service_worker (
    id VARCHAR(64) PRIMARY KEY,
    community_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64),
    worker_type VARCHAR(30) NOT NULL DEFAULT 'COMMUNITY_WORKER', -- EXTERNAL, COMMUNITY_WORKER, MULTI_FLAT, VENDOR_EMPLOYEE
    display_name VARCHAR(150) NOT NULL,
    phone_masked VARCHAR(20),
    experience_years INT NOT NULL DEFAULT 0,
    languages TEXT[] NOT NULL DEFAULT '{}',
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED, SUSPENDED, EXPIRED
    mobile_verified BOOLEAN NOT NULL DEFAULT FALSE,
    community_verified BOOLEAN NOT NULL DEFAULT FALSE,
    security_verified BOOLEAN NOT NULL DEFAULT FALSE,
    rating NUMERIC(3, 2) NOT NULL DEFAULT 5.00,
    review_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED, ON_LEAVE
    serving_since DATE NOT NULL DEFAULT CURRENT_DATE,
    bio TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsw_community ON home_service_worker (community_id, status, verification_status);
CREATE INDEX IF NOT EXISTS idx_hsw_rating ON home_service_worker (rating DESC);

-- 3. Worker Skills (Many-to-Many)
CREATE TABLE IF NOT EXISTS home_service_worker_skill (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    category_id VARCHAR(64) NOT NULL REFERENCES home_service_category(id) ON DELETE RESTRICT,
    experience_years INT NOT NULL DEFAULT 1,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_worker_category UNIQUE (worker_id, category_id)
);

CREATE INDEX IF NOT EXISTS idx_hsws_category ON home_service_worker_skill (category_id, active);

-- 4. Worker Availability Schedules
CREATE TABLE IF NOT EXISTS home_service_worker_availability (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    day_of_week VARCHAR(15) NOT NULL, -- MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_bookings INT NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_avail_time CHECK (start_time < end_time)
);

CREATE INDEX IF NOT EXISTS idx_hswa_worker ON home_service_worker_availability (worker_id, day_of_week, active);

-- 5. Worker Flat Assignments (Serves multiple flats)
CREATE TABLE IF NOT EXISTS home_service_worker_flat_assignment (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    flat_id VARCHAR(64) NOT NULL,
    tower VARCHAR(50),
    flat_number VARCHAR(50) NOT NULL,
    assigned_from DATE NOT NULL DEFAULT CURRENT_DATE,
    assigned_to DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, ENDED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hswfa_worker ON home_service_worker_flat_assignment (worker_id, status);
CREATE INDEX IF NOT EXISTS idx_hswfa_flat ON home_service_worker_flat_assignment (flat_id, status);

-- 6. Service Packages
CREATE TABLE IF NOT EXISTS home_service_package (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) REFERENCES home_service_worker(id) ON DELETE CASCADE,
    category_id VARCHAR(64) NOT NULL REFERENCES home_service_category(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    included_tasks TEXT[] NOT NULL DEFAULT '{}',
    pricing_model VARCHAR(30) NOT NULL, -- FIXED_MONTHLY, PER_DAY, PER_VISIT, PER_HOUR, PER_TASK, FIXED_PACKAGE
    price NUMERIC(10, 2) NOT NULL,
    duration_minutes INT,
    frequency VARCHAR(30) NOT NULL DEFAULT 'MONTHLY', -- ONE_TIME, DAILY, WEEKLY, MONTHLY
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsp_worker ON home_service_package (worker_id, active);
CREATE INDEX IF NOT EXISTS idx_hsp_category ON home_service_package (category_id, active);

-- 7. Service Bookings
CREATE TABLE IF NOT EXISTS home_service_booking (
    id VARCHAR(64) PRIMARY KEY,
    community_id VARCHAR(64) NOT NULL,
    flat_id VARCHAR(64) NOT NULL,
    tower VARCHAR(50),
    flat_number VARCHAR(50) NOT NULL,
    resident_user_id VARCHAR(64) NOT NULL,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE RESTRICT,
    category_id VARCHAR(64) NOT NULL REFERENCES home_service_category(id) ON DELETE RESTRICT,
    package_id VARCHAR(64) REFERENCES home_service_package(id) ON DELETE SET NULL,
    booking_type VARCHAR(30) NOT NULL, -- ONE_TIME, DAILY, WEEKLY, MONTHLY
    pricing_model VARCHAR(30) NOT NULL, -- FIXED_MONTHLY, PER_DAY, PER_VISIT, PER_HOUR, PER_TASK, FIXED_PACKAGE
    start_date DATE NOT NULL,
    end_date DATE,
    recurring_days VARCHAR(15)[] NOT NULL DEFAULT '{}', -- ['MONDAY', 'TUESDAY', ...]
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED', 
    -- DRAFT, REQUESTED, ACCEPTED, REJECTED, CONFIRMED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED_BY_RESIDENT, CANCELLED_BY_WORKER, NO_SHOW, PAUSED, EXPIRED
    rejection_reason TEXT,
    cancellation_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsb_resident ON home_service_booking (resident_user_id, status);
CREATE INDEX IF NOT EXISTS idx_hsb_worker ON home_service_booking (worker_id, status);
CREATE INDEX IF NOT EXISTS idx_hsb_community ON home_service_booking (community_id, status);

-- 8. Service Schedules (Individual day slots)
CREATE TABLE IF NOT EXISTS home_service_schedule (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES home_service_booking(id) ON DELETE CASCADE,
    service_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, IN_PROGRESS, COMPLETED, MISSED, CANCELLED, RESCHEDULED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_booking_schedule_slot UNIQUE (booking_id, service_date)
);

CREATE INDEX IF NOT EXISTS idx_hss_date ON home_service_schedule (service_date, status);

-- 9. Service Attendance
CREATE TABLE IF NOT EXISTS home_service_attendance (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES home_service_booking(id) ON DELETE CASCADE,
    schedule_id VARCHAR(64) REFERENCES home_service_schedule(id) ON DELETE SET NULL,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE RESTRICT,
    resident_user_id VARCHAR(64) NOT NULL,
    service_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL, -- COMPLETED, ABSENT, LEAVE, HOLIDAY, CANCELLED
    check_in_time TIMESTAMP WITH TIME ZONE,
    check_out_time TIMESTAMP WITH TIME ZONE,
    marked_by VARCHAR(64) NOT NULL, -- RESIDENT, WORKER, SYSTEM, ADMIN
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_booking_attendance_date UNIQUE (booking_id, service_date)
);

CREATE INDEX IF NOT EXISTS idx_hsa_worker_date ON home_service_attendance (worker_id, service_date);
CREATE INDEX IF NOT EXISTS idx_hsa_booking ON home_service_attendance (booking_id, service_date);

-- 10. Service Payments
CREATE TABLE IF NOT EXISTS home_service_payment (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES home_service_booking(id) ON DELETE RESTRICT,
    billing_month VARCHAR(7), -- YYYY-MM
    amount NUMERIC(10, 2) NOT NULL,
    payment_model VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    transaction_reference VARCHAR(100),
    payment_method VARCHAR(50), -- UPI, RESIDENT_WALLET, CARD, CASH, GATEWAY
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsp_booking_status ON home_service_payment (booking_id, payment_status);

-- 11. Service Reviews (Two-way)
CREATE TABLE IF NOT EXISTS home_service_review (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES home_service_booking(id) ON DELETE CASCADE,
    reviewer_user_id VARCHAR(64) NOT NULL,
    reviewer_role VARCHAR(20) NOT NULL DEFAULT 'RESIDENT', -- RESIDENT, WORKER
    reviewee_id VARCHAR(64) NOT NULL,
    rating NUMERIC(2, 1) NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    work_quality INT CHECK (work_quality BETWEEN 1 AND 5),
    punctuality INT CHECK (punctuality BETWEEN 1 AND 5),
    behaviour INT CHECK (behaviour BETWEEN 1 AND 5),
    reliability INT CHECK (reliability BETWEEN 1 AND 5),
    comment TEXT,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED', -- PUBLISHED, HIDDEN, REPORTED, MODERATED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hsr_reviewee ON home_service_review (reviewee_id, status);

-- 12. Requirements & Bidding
CREATE TABLE IF NOT EXISTS home_service_request (
    id VARCHAR(64) PRIMARY KEY,
    community_id VARCHAR(64) NOT NULL,
    flat_id VARCHAR(64) NOT NULL,
    tower VARCHAR(50),
    created_by VARCHAR(64) NOT NULL,
    category_id VARCHAR(64) NOT NULL REFERENCES home_service_category(id) ON DELETE RESTRICT,
    frequency VARCHAR(30) NOT NULL, -- ONE_TIME, DAILY, WEEKLY, MONTHLY
    preferred_days VARCHAR(15)[] NOT NULL DEFAULT '{}',
    start_date DATE NOT NULL,
    preferred_start_time TIME,
    preferred_end_time TIME,
    budget_min NUMERIC(10, 2),
    budget_max NUMERIC(10, 2),
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, ASSIGNED, CLOSED, CANCELLED, EXPIRED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS home_service_request_response (
    id VARCHAR(64) PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL REFERENCES home_service_request(id) ON DELETE CASCADE,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    proposed_price NUMERIC(10, 2) NOT NULL,
    message TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, ACCEPTED, REJECTED, WITHDRAWN
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_request_worker_bid UNIQUE (request_id, worker_id)
);

CREATE INDEX IF NOT EXISTS idx_hsreq_community ON home_service_request (community_id, status);

-- 13. Reports & Complaints
CREATE TABLE IF NOT EXISTS home_service_report (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) REFERENCES home_service_booking(id) ON DELETE SET NULL,
    reported_by VARCHAR(64) NOT NULL,
    reported_against VARCHAR(64) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, UNDER_REVIEW, RESOLVED, DISMISSED
    resolution TEXT,
    resolved_by VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_hsrep_status ON home_service_report (status);

-- 14. Worker Leaves
CREATE TABLE IF NOT EXISTS home_service_worker_leave (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    leave_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'APPROVED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_worker_leave_date UNIQUE (worker_id, leave_date)
);

-- 15. Security Gate Passes / Worker Entry Records
CREATE TABLE IF NOT EXISTS home_service_gate_pass (
    id VARCHAR(64) PRIMARY KEY,
    worker_id VARCHAR(64) NOT NULL REFERENCES home_service_worker(id) ON DELETE CASCADE,
    qr_token_hash VARCHAR(128) NOT NULL UNIQUE,
    valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_entry_at TIMESTAMP WITH TIME ZONE,
    last_exit_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
