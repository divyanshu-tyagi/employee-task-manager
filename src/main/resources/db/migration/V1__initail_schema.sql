CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE',
                       department VARCHAR(100),
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                       status VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
                       assigned_to BIGINT NOT NULL REFERENCES users(id),
                       assigned_by BIGINT NOT NULL REFERENCES users(id),
                       due_date TIMESTAMP,
                       completed_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE task_history (
                              id BIGSERIAL PRIMARY KEY,
                              task_id BIGINT NOT NULL REFERENCES tasks(id),
                              changed_by BIGINT NOT NULL REFERENCES users(id),
                              old_status VARCHAR(20),
                              new_status VARCHAR(20) NOT NULL,
                              comment TEXT,
                              changed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_task_history_task_id ON task_history(task_id);