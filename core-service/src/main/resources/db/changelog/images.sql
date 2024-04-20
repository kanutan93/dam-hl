CREATE TABLE IF NOT EXISTS images
(
    id         SERIAL           NOT NULL,
    filename   VARCHAR(50)      NOT NULL,
    category   VARCHAR(50),
    category_match_result  NUMERIC,

    PRIMARY KEY (id)
);

CREATE INDEX category_idx ON images (category);