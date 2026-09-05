CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE desks (
    desk_id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE cards (
    card_id UUID PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    desk_id UUID NOT NULL,
    meet_chance DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_cards_desk
        FOREIGN KEY (desk_id)
        REFERENCES desks(desk_id)
        ON DELETE CASCADE
);

CREATE TABLE answers (
    answer_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    card_id UUID NOT NULL,
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_answers_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_answers_card
        FOREIGN KEY (card_id)
        REFERENCES cards(card_id)
        ON DELETE CASCADE
);