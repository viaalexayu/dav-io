--USERS TABLE
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     username VARCHAR(100) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     PRIMARY KEY (id)
);

--TIME SLOTS TABLE
CREATE TABLE IF NOT EXISTS time_slots (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    admin_id     BIGINT NOT NULL,
    slot_date    DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY no_duplicate (admin_id, slot_date, start_time),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

--APPOINTMENTS TABLE
CREATE TABLE IF NOT EXISTS appointments (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    slot_id    BIGINT NOT NULL UNIQUE,
    status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes      TEXT,
    booked_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (slot_id) REFERENCES time_slots(id)
);

--NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS notifications (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    user_id        BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL,
    message        TEXT NOT NULL,
    is_read        BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);


--SAMPLE DATA
INSERT INTO users (username, password, role) VALUES
                                                 ('adminvia', '$2a$10$DP6KlPxyt.77EnzBnq.kT.H4RUYP5MbTDsGPkQUnq3jaxYyVfMfZi', 'ROLE_ADMIN'),
                                                 ('adminolga', '$2a$10$WvXk0ZbleiELSOiAE3gy6exPirvPTQ3wlcVKUDKXmyrWHgI6VRLqC', 'ROLE_ADMIN'),
                                                 ('userdeven', '$2a$10$Boqyq0jj2pGTUWRlzsqOeeqOFUjRul/dkYKyD54rbRyZ1LZspBHAW', 'ROLE_USER'),
                                                 ('userisaac', '$2a$10$KXHVVi.bAkLUvEZ1MqkME.sefD2bzwZCi0ujhySqr3PfPMbitOJOC', 'ROLE_USER'),
                                                 ('adminaliana', '$2a$10$4Swb311STVHCb2/rrhQ1/.0bb7LYGiTfK2IxmcEIyP39cL4VWFC3u', 'ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE username = username;

