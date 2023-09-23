DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS request CASCADE;
DROP TABLE IF EXISTS events_compilations CASCADE;

CREATE TABLE categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50)                             NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (name),
    CONSTRAINT pk_category_id PRIMARY KEY (id)
);

CREATE TABLE users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(250)                            NOT NULL,
    email VARCHAR(254)                            NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    lat                DOUBLE PRECISION                        NOT NULL,
    lon                DOUBLE PRECISION                        NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  BIGINT                                  NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    create_date        TIMESTAMP WITHOUT TIME ZONE,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    state              VARCHAR(40)                             NOT NULL,
    confirmed_requests BIGINT                                  NOT NULL,
    views              BIGINT                                  NOT NULL,
    CONSTRAINT pk_event_id PRIMARY KEY (id),
    CONSTRAINT fk_cat_id_in_event FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_user_id_in_event FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title  VARCHAR(50)                             NOT NULL,
    pinned boolean                                 NOT NULL,
    CONSTRAINT pk_compilation_id PRIMARY KEY (id)
);

CREATE TABLE requests
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requester_id   BIGINT                                  NOT NULL,
    event_id       BIGINT                                  NOT NULL,
    create_date    TIMESTAMP WITHOUT TIME ZONE,
    request_status VARCHAR(40)                             NOT NULL,
    CONSTRAINT pk_request_id PRIMARY KEY (id),
    CONSTRAINT fk_requester_id_in_request FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_event_id_in_request FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE events_compilations
(
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_events_compilations PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_ec_to_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_ec_to_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT uq_events_in_compilation UNIQUE (event_id, compilation_id)
);




