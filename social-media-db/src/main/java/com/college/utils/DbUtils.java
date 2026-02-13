package com.college.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;


@Component
public class DbUtils {
    @Autowired
    private DataSource dataSource;

    private Connection connection;

    @PostConstruct
    public void init() {
        try {
            this.connection = dataSource.getConnection();
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println("Failed to create db connection");
            e.printStackTrace();
        }
    }

    public void register(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?,?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean userExist(User user) {
        String sql = "SELECT username, password FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public boolean usernameTaken(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getProfileUrl(String username) {
        String sql = "SELECT profile_image_url FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateProfileUrl(String username, String url) {
        String sql = "UPDATE users SET profile_image_url = ? WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, url);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteProfileUrl(String username) {
        String sql = "UPDATE users SET profile_image_url = NULL WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, profile_image_url FROM users";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User(resultSet.getString(1), resultSet.getString(2));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<UserSearchDto> getFilteredUsersWithFollow(String me, String f) {
        if (me == null || f == null) return new ArrayList<>();

        String sql = """
                SELECT 
                    u.username,
                    u.profile_image_url,
                    CASE 
                        WHEN fol.follower_username IS NULL THEN false
                        ELSE true
                    END AS is_following
                FROM users u
                LEFT JOIN follows fol
                    ON fol.followed_username = u.username
                   AND fol.follower_username = ?
                WHERE u.username LIKE ?
                ORDER BY 
                    CASE WHEN u.username LIKE ? THEN 0 ELSE 1 END,
                    u.username
                """;

        List<UserSearchDto> result = new ArrayList<>();

        try {
            PreparedStatement ps = this.connection.prepareStatement(sql);
            ps.setString(1, me);
            ps.setString(2, "%" + f + "%");
            ps.setString(3, f + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserSearchDto dto = new UserSearchDto(
                        rs.getString("username"),
                        rs.getString("profile_image_url"),
                        rs.getBoolean("is_following")
                );
                result.add(dto);
            }

            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean isFollow(String follow, String followed) {
        String sql = "SELECT follower_username, followed_username FROM follows WHERE follower_username = ? AND followed_username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, follow);
            preparedStatement.setString(2, followed);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void follow(String follow, String followed) {
        if (!usernameTaken(followed)) return;
        if (!isFollow(follow, followed)) {
            String sql = "INSERT INTO follows(follower_username, followed_username) VALUES (?,?)";
            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
                preparedStatement.setString(1, follow);
                preparedStatement.setString(2, followed);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void unfollow(String follow, String followed) {
        if (!isFollow(follow, followed)) return;
        String sql = "DELETE FROM follows WHERE follower_username = ? AND followed_username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, follow);
            preparedStatement.setString(2, followed);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addPost(String content, String username) {
        String sql = "INSERT INTO posts (username, content) VALUES (?,?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<PostDto> getMyPosts(String username) {
        if (username == null) return new ArrayList<>();
        List<PostDto> myPosts = new ArrayList<>();
        String sql = "SELECT id, content FROM posts WHERE username = ? ORDER BY id DESC ";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PostDto post = new PostDto(resultSet.getInt("id"), username, resultSet.getString("content"));
                myPosts.add(post);
            }
            return myPosts;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<PostDto> getFeed(String username) {
        if (username == null) return new ArrayList<>();
        if (!haveFriend(username)) return new ArrayList<>();
        String sql = "SELECT p.id, p.username, p.content FROM posts p JOIN follows f ON f.followed_username = p.username WHERE f.follower_username = ? ORDER BY p.id DESC LIMIT 20";
        List<PostDto> friendPosts = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PostDto post = new PostDto(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("content"));
                friendPosts.add(post);
            }
            Collections.shuffle(friendPosts);
            return friendPosts;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean haveFriend(String username) {
        if (username == null) return false;
        String sql = "SELECT follower_username FROM follows WHERE follower_username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deletePost(String username, int id) {
        if (username == null) return false;
        String sql = "DELETE FROM posts WHERE username = ? AND id = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, id);
            int row = preparedStatement.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<User> followingByMe(String username) {
        if (username == null) return new ArrayList<>();
        String sql = "SELECT f.followed_username, u.profile_image_url FROM follows f JOIN users u ON f.followed_username = u.username WHERE f.follower_username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("followed_username"));
                user.setUrl(resultSet.getString("profile_image_url"));
                result.add(user);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public List<User> followersOfMe(String username) {
        if (username == null) return new ArrayList<>();
        String sql = "SELECT f.follower_username, u.profile_image_url FROM follows f JOIN users u ON f.follower_username = u.username WHERE f.followed_username = ?";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> result = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("follower_username"));
                user.setUrl(resultSet.getString("profile_image_url"));
                result.add(user);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public User getUserByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT username, password, profile_image_url FROM users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = new User();
            if (resultSet.next()) {
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setUrl(resultSet.getString("profile_image_url"));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean changePassword(String username, String newPassword) {
        if (newPassword == null) return false;
        if (username == null) return false;
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            int row = preparedStatement.executeUpdate();
            return row > 0 ;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public PostDto getPostById(int postId) {
        String sql = "SELECT id, username, content FROM posts WHERE id = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new PostDto(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("content"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public PostReaction getPostReactionByIdAndUsername(int postId, String username) {
        String sql = "SELECT post_id, username, reaction from post_reactions WHERE post_id = ? AND username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            preparedStatement.setString(2, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new PostReaction(resultSet.getInt("post_id"), resultSet.getString("username"), resultSet.getInt("reaction"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addReaction(String username, int postId, int reaction) {
        String sql = "INSERT INTO post_reactions (post_id, username, reaction) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            preparedStatement.setString(2, username);
            preparedStatement.setInt(3, reaction);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void changeReaction(String username, int postId, int reaction) {
        String sql = "UPDATE post_reactions SET reaction = ? WHERE username = ? AND post_id = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, reaction);
            preparedStatement.setString(2, username);
            preparedStatement.setInt(3, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteReaction(String username, int postId) {
        String sql = "DELETE FROM post_reactions WHERE username = ? AND post_id = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setPostReaction(String username, int postId, int reaction) {
        if (reaction != 1 && reaction != -1) return;
        PostReaction postReaction = getPostReactionByIdAndUsername(postId, username);
        if (postReaction == null) {
            addReaction(username, postId, reaction);
            return;
        }
        if (postReaction.getReaction() == reaction) {
            deleteReaction(username, postId);
            return;
        }
        changeReaction(username, postId, reaction);
    }

    public int countReaction(int postId, int reaction) {
        if (reaction != 1 && reaction != -1) return 0;

        String sql = "SELECT COUNT(*) AS cnt FROM post_reactions WHERE post_id = ? AND reaction = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            preparedStatement.setInt(2, reaction);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int getUserReaction(int postId, String username) {
        String sql = "SELECT reaction FROM post_reactions WHERE post_id = ? AND username = ?";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            preparedStatement.setString(2, username);
            ResultSet resultSet = preparedStatement.executeQuery();
           if(resultSet.next()){
               return resultSet.getInt("reaction");
           }else{
               return 0;
           }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
