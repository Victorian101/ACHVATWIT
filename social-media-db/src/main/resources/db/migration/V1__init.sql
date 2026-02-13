CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    url VARCHAR(1024) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    content VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_posts_user_id (user_id),
    KEY idx_posts_username (username),
    CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS follows (
    id BIGINT NOT NULL AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_follows_pair (follower_id, followed_id),
    KEY idx_follows_follower (follower_id),
    KEY idx_follows_followed (followed_id),
    CONSTRAINT fk_follows_follower
        FOREIGN KEY (follower_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed
        FOREIGN KEY (followed_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_reactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction TINYINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_post_reaction_user (post_id, user_id),
    KEY idx_post_reactions_post (post_id),
    KEY idx_post_reactions_user (user_id),
    CONSTRAINT fk_post_reactions_post
        FOREIGN KEY (post_id) REFERENCES posts(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_post_reactions_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_reaction
        CHECK (reaction IN (-1, 0, 1))
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;