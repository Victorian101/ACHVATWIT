CREATE TABLE IF NOT EXISTS users
(
    username          VARCHAR(64)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(1024) DEFAULT NULL,
    PRIMARY KEY (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS posts
(
    id       INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(64)  NOT NULL,
    content  VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_posts_username (username),
    CONSTRAINT fk_posts_user
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS follows
(
    follower_username VARCHAR(64) NOT NULL,
    followed_username VARCHAR(64) NOT NULL,
    PRIMARY KEY (follower_username, followed_username),
    KEY idx_follows_followed (followed_username),
    CONSTRAINT fk_follows_follower
        FOREIGN KEY (follower_username) REFERENCES users (username)
            ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed
        FOREIGN KEY (followed_username) REFERENCES users (username)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS post_reactions
(
    post_id  INT         NOT NULL,
    username VARCHAR(64) NOT NULL,
    reaction TINYINT     NOT NULL,
    PRIMARY KEY (post_id, username),
    KEY idx_post_reactions_username (username),
    CONSTRAINT fk_post_reactions_post
        FOREIGN KEY (post_id) REFERENCES posts (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_post_reactions_user
        FOREIGN KEY (username) REFERENCES users (username)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;