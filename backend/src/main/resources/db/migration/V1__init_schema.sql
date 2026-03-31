CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE UNIQUE INDEX uk_users_username ON users (username);

CREATE TABLE locations (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    location_code VARCHAR(10) NOT NULL
);

CREATE UNIQUE INDEX uk_locations_location_code ON locations (location_code);

CREATE TABLE transportations (
    id UUID PRIMARY KEY,
    origin_location_id UUID NOT NULL,
    destination_location_id UUID NOT NULL,
    transportation_type VARCHAR(30) NOT NULL,
    CONSTRAINT fk_transportations_origin_location
        FOREIGN KEY (origin_location_id) REFERENCES locations (id),
    CONSTRAINT fk_transportations_destination_location
        FOREIGN KEY (destination_location_id) REFERENCES locations (id)
);

CREATE TABLE transportation_operating_days (
    transportation_id UUID NOT NULL,
    operating_day VARCHAR(20) NOT NULL,
    CONSTRAINT fk_transportation_operating_days_transportation
        FOREIGN KEY (transportation_id) REFERENCES transportations (id)
);
