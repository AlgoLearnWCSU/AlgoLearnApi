ALTER TABLE problem
ADD FOREIGN KEY (poster) REFERENCES user_t(username);

