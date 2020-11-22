-- Add FK from problem.poster -> user_t.username
ALTER TABLE problem
    ADD CONSTRAINT problem_poster_fk FOREIGN KEY (poster)
        REFERENCES user_t (username);


ALTER TABLE comment
    ADD CONSTRAINT comment_commenter_fk FOREIGN KEY (commenter)
        REFERENCES user_t (username);

Alter Table comment
    ADD CONSTRAINT comment_problem_fk FOREIGN KEY (problem)
        REFERENCES problem (id)
        ON DELETE CASCADE;

ALTER TABLE test_case
    ADD CONSTRAINT test_case_problem_fk FOREIGN KEY (problem)
        REFERENCES problem (id)
        ON DELETE CASCADE;

ALTER TABLE parameter
    ADD CONSTRAINT parameter_problem_fk FOREIGN KEY (problem)
        REFERENCES problem (id)
        ON DELETE CASCADE;

ALTER TABLE solution
    ADD CONSTRAINT solution_problem_fk FOREIGN KEY (problem)
        REFERENCES problem (id)
        ON DELETE CASCADE;

ALTER TABLE solution
    ADD CONSTRAINT solution_solver_fk FOREIGN KEY (solver)
        REFERENCES user_t (username);

ALTER TABLE auth
    ADD CONSTRAINT auth_username_fk FOREIGN KEY (username)
        REFERENCES user_t (username);