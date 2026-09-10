CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE topics (
    topic_id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE desks (
    desk_id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    topic_id UUID NOT NULL REFERENCES topics(topic_id) ON DELETE CASCADE
);

CREATE TABLE cards (
    card_id UUID PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    desk_id UUID NOT NULL REFERENCES desks(desk_id) ON DELETE CASCADE,
    meet_chance DECIMAL(10, 2) NOT NULL
);

CREATE TABLE answers (
    answer_id UUID PRIMARY KEY,

    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    card_id UUID NOT NULL REFERENCES cards(card_id) ON DELETE CASCADE,

    user_answer TEXT NOT NULL,
    ai_feedback TEXT NOT NULL,

    start_answer_time TIMESTAMP NOT NULL,
    submission_time TIMESTAMP NOT NULL,

    ai_processing_duration_ms BIGINT NOT NULL,

    score INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);