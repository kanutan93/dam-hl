CREATE TABLE IF NOT EXISTS images
(
    id         SERIAL           NOT NULL,
    filename   VARCHAR(50)      NOT NULL,
    category   VARCHAR(50),
    category_match_result  FLOAT,

    PRIMARY KEY (id),
    UNIQUE  (email)
);