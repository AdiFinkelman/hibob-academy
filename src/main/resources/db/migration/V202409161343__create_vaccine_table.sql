CREATE TABLE vaccine
(
    id BIGSERIAL PRIMARY KEY,
    name varchar(64) NOT NULL
);

CREATE TABLE vaccineToPet
(
    vaccine_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    vaccination_date DATE NOT NULL
);