-- Add FK from problem.poster -> user_t.username
ALTER TABLE problem
ADD CONSTRAINT problem_poster_fk FOREIGN KEY (poster)
REFERENCES user_t(username);

