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