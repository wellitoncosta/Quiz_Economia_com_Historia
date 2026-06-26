CREATE TABLE quizzes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(500) NULL,
    external_reference VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL,
    pass_percentage DECIMAL(5,2) NOT NULL,
    time_limit_minutes INT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_quizzes_external_reference UNIQUE (external_reference)
);

CREATE TABLE questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quiz_id BIGINT NOT NULL,
    statement VARCHAR(1000) NOT NULL,
    explanation VARCHAR(1000) NULL,
    position INT NOT NULL,
    points INT NOT NULL,
    active BIT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT uk_questions_quiz_position UNIQUE (quiz_id, position)
);

CREATE TABLE answer_options (
    id BIGINT NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    correct BIT NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_answer_options_question FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT uk_answer_options_question_position UNIQUE (question_id, position)
);

CREATE TABLE quiz_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quiz_id BIGINT NOT NULL,
    participant_id VARCHAR(120) NOT NULL,
    participant_name VARCHAR(160) NULL,
    status VARCHAR(20) NOT NULL,
    score INT NOT NULL,
    max_score INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    passed BIT NOT NULL,
    started_at DATETIME(6) NOT NULL,
    submitted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_quiz_attempts_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

CREATE TABLE attempt_answers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_id BIGINT NOT NULL,
    correct BIT NOT NULL,
    points_awarded INT NOT NULL,
    answered_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_attempt_answers_attempt FOREIGN KEY (attempt_id) REFERENCES quiz_attempts (id) ON DELETE CASCADE,
    CONSTRAINT fk_attempt_answers_question FOREIGN KEY (question_id) REFERENCES questions (id),
    CONSTRAINT fk_attempt_answers_option FOREIGN KEY (selected_option_id) REFERENCES answer_options (id),
    CONSTRAINT uk_attempt_answers_question UNIQUE (attempt_id, question_id)
);
