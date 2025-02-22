-- Users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    age INTEGER,
    gender VARCHAR(50),
    introduction TEXT,
    profile_image_url VARCHAR(255),
    open_chat_url VARCHAR(255),
    oauth_id VARCHAR(255) NOT NULL,
    oauth_provider VARCHAR(50) NOT NULL,
    refresh_token VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Dailies 테이블
CREATE TABLE dailies (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    image_name VARCHAR(200) NOT NULL,
    image_content_type VARCHAR(80) NOT NULL,
    image_content BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tags 테이블
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    daily_id BIGINT,
    tag_name VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 인덱스 생성
CREATE INDEX idx_oauth_id ON users(oauth_id);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_refresh_token ON users(refresh_token);
