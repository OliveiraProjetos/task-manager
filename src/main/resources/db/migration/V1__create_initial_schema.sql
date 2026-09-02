CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    status VARCHAR(255),
    priority VARCHAR(255),
    title VARCHAR(255),
    description VARCHAR(255),
    due_date DATE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    user_id UUID NOT NULL,

    CONSTRAINT fk_tasks_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);
