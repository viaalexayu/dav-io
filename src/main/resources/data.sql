CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     username VARCHAR(100) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(50) NOT NULL,
                                     PRIMARY KEY (id)
);

INSERT INTO users (username, password, role) VALUES
                                                 ('adminvia', '$2a$10$DP6KlPxyt.77EnzBnq.kT.H4RUYP5MbTDsGPkQUnq3jaxYyVfMfZi', 'ROLE_ADMIN'),
                                                 ('adminolga', '$2a$10$WvXk0ZbleiELSOiAE3gy6exPirvPTQ3wlcVKUDKXmyrWHgI6VRLqC', 'ROLE_ADMIN'),
                                                 ('userdeven', '$2a$10$Boqyq0jj2pGTUWRlzsqOeeqOFUjRul/dkYKyD54rbRyZ1LZspBHAW', 'ROLE_USER'),
                                                 ('userisaac', '$2a$10$KXHVVi.bAkLUvEZ1MqkME.sefD2bzwZCi0ujhySqr3PfPMbitOJOC', 'ROLE_USER'),
                                                 ('adminaliana', '$2a$10$4Swb311STVHCb2/rrhQ1/.0bb7LYGiTfK2IxmcEIyP39cL4VWFC3u', 'ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE username = username;