package com.college.controllers;


import com.college.responses.*;
import com.college.utils.ChangePassword;
import com.college.utils.DbUtils;
import com.college.utils.PostDto;
import com.college.utils.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.college.requests.UrlRequest;

import java.util.List;
import java.util.StringTokenizer;

import static com.college.utils.Errors.*;

@RestController
public class GeneralController {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();


    @Autowired
    private DbUtils dbUtils;

    @PostConstruct
    public void init() {
    }

    @PostMapping("/register")
    public BasicResponse register(@RequestBody User user) {
        if (user == null) {
            return new BasicResponse(false, ERROR_MISSING_USER);
        }
        if (user.getPassword().length() < 3) return new BasicResponse(false, ERROR_INVALID_PASSWORD);
        if (user.getUsername().length() < 3) return new BasicResponse(false, ERROR_INVALID_USERNAME);
        if (dbUtils.usernameTaken(user.getUsername())) {
            return new BasicResponse(false, ERROR_USERNAME_TAKEN);
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
            dbUtils.register(user);
            return new BasicResponse(true, null);
        }
    }

    @PostMapping("/login")
    public BasicResponse login(@RequestBody User user, HttpSession session) {
        if (user == null) {
            return new BasicResponse(false, ERROR_MISSING_USER);
        }
        User temp = dbUtils.getUserByUsername(user.getUsername());
        if (temp == null) return new BasicResponse(false, ERROR_MISSING_USER);
        boolean ok = encoder.matches(user.getPassword(), temp.getPassword());
        if(!ok) return new BasicResponse(false, ERROR_WRONG_USER_PARAMS);
        session.setAttribute("username", user.getUsername());
        return new BasicResponse(true, null);

    }

    @GetMapping("/me")
    public BasicResponse me(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        }
        String url = dbUtils.getProfileUrl(username);
        return new MeResponse(true, null, username, url);
    }

    @PostMapping("/logout")
    public BasicResponse logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new BasicResponse(true, null);
    }


    @PostMapping("/update-url")
    public BasicResponse updateUrl(@RequestBody UrlRequest req, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        }

        if (req == null || req.getUrl() == null || req.getUrl().trim().isEmpty()) {
            return new BasicResponse(false, ERROR_MISSING_URL);
        }

        dbUtils.updateProfileUrl(username, req.getUrl().trim());
        return new BasicResponse(true, null);
    }

    @PostMapping("/delete-url")
    public BasicResponse deleteUrl(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        } else {
            dbUtils.deleteProfileUrl(username);
            return new BasicResponse(true, null);
        }
    }

    @GetMapping("/get-all-users")
    public BasicResponse getAllUsers() {
        return new UsersResponse(true, null, dbUtils.getAllUsers());
    }

    @GetMapping("/get-filtred-users")
    public BasicResponse getFilteredUsers(HttpSession session, @RequestParam String f) {
        String me = (String) session.getAttribute("username");
        if (me == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        if (f == null) return new BasicResponse(false, ERROR_INVALID_SEARCH);
        if (f.trim().length() < 3)
            return new SearchUsersResponse(true, null, List.of());
        if (f == null) {
            return new BasicResponse(false, ERROR_MISSING_FILTER_STRING);
        } else {
            return new SearchUsersResponse(true, null, dbUtils.getFilteredUsersWithFollow(me, f));
        }
    }

    @PostMapping("/follow")
    public BasicResponse follow(HttpSession session, @RequestParam String followed) {
        String follower = (String) session.getAttribute("username");
        if (follower == null || followed == null) {
            return new BasicResponse(false, ERROR_USER_DONT_EXIST);
        }
        followed = followed.trim();
        if (followed.equals(follower)) {
            return new BasicResponse(false, ERROR_FOLLOW_THEMSELF);
        }
        if (dbUtils.isFollow(follower, followed)) {
            return new BasicResponse(false, ERROR_FOLLOWING_ALREADY);
        }
        dbUtils.follow(follower, followed);
        return new BasicResponse(true, null);

    }

    @PostMapping("/unfollow")
    public BasicResponse unfollow(HttpSession session, @RequestParam String followed) {
        String follower = (String) session.getAttribute("username");
        if (follower == null || followed == null) {
            return new BasicResponse(false, ERROR_USER_DONT_EXIST);
        }
        followed = followed.trim();
        if (followed.equals(follower)) return new BasicResponse(false, ERROR_FOLLOW_THEMSELF);
        if (!dbUtils.isFollow(follower, followed)) return new BasicResponse(false, ERROR_USER_DONT_FOLLOW);
        dbUtils.unfollow(follower, followed);
        return new BasicResponse(true, null);
    }

    @GetMapping("/is-following")
    public BasicResponse isFollow(HttpSession session, @RequestParam String username) {
        String following = (String) session.getAttribute("username");
        username = username.trim();
        if (following.equals(username)) return new BasicResponse(false, ERROR_FOLLOW_THEMSELF);
        boolean follow = dbUtils.isFollow(following, username);
        return new BooleanResponce(true, null, follow);
    }

    @GetMapping("/user-profile")
    public BasicResponse getUserProfile(@RequestParam String username, HttpSession session) {
        String me = (String) session.getAttribute("username");
        if (me == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);

        if (username == null || username.trim().isEmpty()) {
            return new BasicResponse(false, ERROR_MISSING_USER);
        }

        username = username.trim();
        if (!dbUtils.usernameTaken(username)) {
            return new BasicResponse(false, ERROR_USER_DONT_EXIST);
        }

        String url = dbUtils.getProfileUrl(username);
        return new MeResponse(true, null, username, url);
    }

    @PostMapping("/add-post")
    public BasicResponse addPost(HttpSession session, @RequestParam String content) {
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        if (content == null) return new BasicResponse(false, ERROR_POST);
        content = content.trim();
        if (content.isEmpty() || content.length() > 500) return new BasicResponse(false, ERROR_POST);
        dbUtils.addPost(content, username);
        return new PostResponce(true, null, content);

    }

    @GetMapping("/get-posts")
    public BasicResponse getPosts(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        return new FeedResponce(true, null, dbUtils.getMyPosts(username));
    }

    @GetMapping("/feed")
    public BasicResponse getFeed(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        return new FeedResponce(true, null, dbUtils.getFeed(username));
    }

    @GetMapping("/get-user-posts")
    public BasicResponse getUserPosts(HttpSession session, @RequestParam String username) {
        String me = (String) session.getAttribute("username");
        if (me == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        if (username == null) return new BasicResponse(false, ERROR_MISSING_USER);
        username = username.trim();
        if (username.isEmpty()) return new BasicResponse(false, ERROR_USER_DONT_EXIST);
        if (!dbUtils.usernameTaken(username)) return new BasicResponse(false, ERROR_USER_DONT_EXIST);
        if (!dbUtils.isFollow(me, username)) {
            return new BasicResponse(false, ERROR_USER_DONT_FOLLOW);
        }
        return new FeedResponce(true, null, dbUtils.getMyPosts(username));
    }

    @PostMapping("/delete-post")
    public BasicResponse deletePost(HttpSession session, @RequestParam int id) {
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        boolean deleted = dbUtils.deletePost(username, id);
        if (!deleted) return new BasicResponse(false, ERROR_POST_NOT_FOUND_OR_NOT_YOURS);
        return new BasicResponse(true, null);
    }

    @GetMapping("/following")
    public BasicResponse followedByMe(HttpSession session) {
        String me = (String) session.getAttribute("username");
        if (me == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        List<User> result = dbUtils.followingByMe(me);
        return new UsersResponse(true, null, result);
    }

    @GetMapping("/followers")
    public BasicResponse followersOfMe(HttpSession session) {
        String me = (String) session.getAttribute("username");
        if (me == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        List<User> result = dbUtils.followersOfMe(me);
        return new UsersResponse(true, null, result);
    }

    @PostMapping("/change-password")
    public BasicResponse changePassword(HttpSession session, @RequestBody ChangePassword changePassword) {
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);

        if (changePassword == null) return new BasicResponse(false, ERROR_MISSING_PASSWORD);

        String oldPass = changePassword.getOldPassword();
        String newPass = changePassword.getNewPassword();
        String confirmNewPassword = changePassword.getConfirmNewPassword();
        if (oldPass == null || newPass == null || confirmNewPassword == null)
            return new BasicResponse(false, ERROR_MISSING_PASSWORD);

        oldPass = oldPass.trim();
        newPass = newPass.trim();
        confirmNewPassword = confirmNewPassword.trim();

        if (!newPass.equals(confirmNewPassword)) return new BasicResponse(false, ERROR_PASSWORD_DONT_MATCH);

        if (newPass.length() < 3) return new BasicResponse(false, ERROR_INVALID_PASSWORD);

        User user = dbUtils.getUserByUsername(username);
        if (user == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);

        boolean oldEqualNow = encoder.matches(oldPass, user.getPassword());
        if(!oldEqualNow)  return new BasicResponse(false, ERROR_OLD_PASSWORD_WRONG);

        // לא לאפשר אותה סיסמה כמו הנוכחית
        boolean newEqualNow = encoder.matches(newPass, user.getPassword());
        if(newEqualNow) return new BasicResponse(false, ERROR_INVALID_PASSWORD);

        String password = encoder.encode(newPass);

        boolean changed = dbUtils.changePassword(username, password);
        if (!changed) return new BasicResponse(false, ERROR_CHANGE_PASSWORD);

        return new BasicResponse(true, null);
    }


    @GetMapping("/post-reaction")
    public BasicResponse postReaction(HttpSession session, @RequestParam int postId) {
        PostDto post = dbUtils.getPostById(postId);
        if (post == null) return new BasicResponse(false, ERROR_POST);
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        return new PostReactionResponse(true, null, postId, dbUtils.countReaction(postId, 1), dbUtils.countReaction(postId, -1), dbUtils.getUserReaction(postId, username));
    }

    @PostMapping("/change-reaction")
    public BasicResponse changeReaction(HttpSession session, @RequestParam int postId, @RequestParam int reaction) {
        if (reaction != 1 && reaction != -1) return new BasicResponse(false, ERROR_REACTION);
        String username = (String) session.getAttribute("username");
        if (username == null) return new BasicResponse(false, ERROR_USER_NOT_SIGNED_UP);
        PostDto post = dbUtils.getPostById(postId);
        if (post == null) return new BasicResponse(false, ERROR_POST);
        dbUtils.setPostReaction(username, postId, reaction);
        return new BasicResponse(true, null);
    }
}
