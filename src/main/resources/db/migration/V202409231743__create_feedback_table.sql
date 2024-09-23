CREATE TABLE feedback_configuration (
    feedback_id BIGSERIAL PRIMARY KEY,
    employee_id BIGSERIAL NOT NULL,
    company_id BIGSERIAL NOT NULL,
    department VARCHAR(50) NOT NULL,
    creation_date DATE DEFAULT CURRENT_DATE,
    is_anonymous BOOLEAN NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE feedback_comment (
    comment_id BIGSERIAL PRIMARY KEY,
    employee_id BIGSERIAL NOT NULL,
    text TEXT NOT NULL,
    feedback_id BIGSERIAL NOT NULL,
    creation_time TIMESTAMP DEFAULT now()
);