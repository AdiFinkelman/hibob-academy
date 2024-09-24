CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    creation_time TIMESTAMP DEFAULT now(),
    is_anonymous BOOLEAN NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE response (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    feedback_id BIGINT NOT NULL,
    creation_time TIMESTAMP DEFAULT now()
);