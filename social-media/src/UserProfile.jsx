import axios from "axios";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import SearchUser from "./SearchUser.jsx";
import "./FeedPanel.css";


function UserProfile() {
    const { username } = useParams();
    const navigate = useNavigate();

    const [url, setUrl] = useState("");
    const [isFollowing, setIsFollowing] = useState(false);

    const [posts, setPosts] = useState([]);
    const [errorPosts, setErrorPosts] = useState("");

    const [search, setSearch] = useState("");
    const [filtredUsers, setFilteredUsers] = useState([]);

    const [loadingSearch, setLoadingSearch] = useState(false);

    const searchTimerRef = useRef(null);

    const [meUsername, setMeUsername] = useState("");

    useEffect(() => {
        axios.get("http://localhost:9090/me", { withCredentials: true })
            .then(res => {
                if (!res.data.success) navigate("/");
                else setMeUsername(res.data.username || "");
            })
            .catch(() => navigate("/"));
    }, [navigate]);

    useEffect(() => {
        if (!username) return;

        axios
            .get("http://localhost:9090/user-profile?username=" + username, {
                withCredentials: true,
            })
            .then((res) => {
                if (res.data.success) setUrl(res.data.url || "");
            })
            .catch(() => {});

        axios
            .get("http://localhost:9090/is-following?username=" + username, {
                withCredentials: true,
            })
            .then((res) => {
                if (res.data.success) setIsFollowing(!!res.data.bool);
            })
            .catch(() => {});
    }, [username]);

    useEffect(() => {
        if (!username) return;

        if (!isFollowing) {
            setPosts([]);
            setErrorPosts("Follow to see posts");
            return;
        }

        axios
            .get("http://localhost:9090/get-user-posts?username=" + username, {
                withCredentials: true,
            })
            .then((res) => {
                if (!res.data.success) {
                    setPosts([]);
                    setErrorPosts(String(res.data.errorCode || "Cannot load posts"));
                    return;
                }

                const basePosts = res.data.posts || [];

                const withDefaults = basePosts.map((p) => ({
                    ...p,
                    likesCount: 0,
                    dislikesCount: 0,
                    myReaction: 0,
                }));

                const requests = withDefaults.map((p) =>
                    axios
                        .get("http://localhost:9090/post-reaction?postId=" + p.id, {
                            withCredentials: true,
                        })
                        .then((r) => {
                            if (r.data && r.data.success) {
                                return {
                                    ...p,
                                    likesCount: r.data.likesCount ?? 0,
                                    dislikesCount: r.data.dislikesCount ?? 0,
                                    myReaction: r.data.myReaction ?? 0,
                                };
                            }
                            return p;
                        })
                        .catch(() => p)
                );

                Promise.all(requests).then((enriched) => {
                    setPosts(enriched);
                    setErrorPosts("");
                });
            })
            .catch(() => {
                setPosts([]);
                setErrorPosts("Failed to load posts");
            });
    }, [username, isFollowing]);

    const fetchFilteredUsers = (value) => {
        setLoadingSearch(true);
        axios.get("http://localhost:9090/get-filtred-users?f=" + value, { withCredentials: true })
            .then(res => {
                if (res.data && res.data.success) {
                    setFilteredUsers(res.data.users || []);
                } else {
                    setFilteredUsers([]);
                }
            })
            .catch(() => setFilteredUsers([]))
            .finally(() => setLoadingSearch(false));
    };

    const onSearchChange = (value) => {
        setSearch(value);
        const v = value.trim();

        if (searchTimerRef.current) {
            clearTimeout(searchTimerRef.current);
            searchTimerRef.current = null;
        }

        if (v.length < 3) {
            setFilteredUsers([]);
            setLoadingSearch(false);
            return;
        }

        setLoadingSearch(true);
        searchTimerRef.current = setTimeout(() => {
            fetchFilteredUsers(v);
        }, 250);
    };

    useEffect(() => {
        return () => {
            if (searchTimerRef.current) clearTimeout(searchTimerRef.current);
        };
    }, []);

    const reactToPost = (postId, reaction) => {
        axios
            .post(
                "http://localhost:9090/change-reaction?postId=" + postId + "&reaction=" + reaction,
                {},
                { withCredentials: true }
            )
            .then((res) => {
                if (!res.data.success) return;

                setPosts((prev) =>
                    prev.map((p) => {
                        if (p.id !== postId) return p;

                        const old = p.myReaction || 0;
                        const next = old === reaction ? 0 : reaction;

                        let likes = p.likesCount || 0;
                        let dislikes = p.dislikesCount || 0;

                        if (old === 1) likes--;
                        if (old === -1) dislikes--;

                        if (next === 1) likes++;
                        if (next === -1) dislikes++;

                        return {
                            ...p,
                            myReaction: next,
                            likesCount: likes,
                            dislikesCount: dislikes,
                        };
                    })
                );
            })
            .catch(() => {});
    };

    const follow = () => {
        axios
            .post("http://localhost:9090/follow?followed=" + username, {}, { withCredentials: true })
            .then((res) => {
                if (res.data.success) setIsFollowing(true);
            })
            .catch(() => {});
    };

    const unfollow = () => {
        axios
            .post("http://localhost:9090/unfollow?followed=" + username, {}, { withCredentials: true })
            .then((res) => {
                if (res.data.success) setIsFollowing(false);
            })
            .catch(() => {});
    };

    const defaultAvatar = "/default-avatar.jpg";
    const imgSrc = url && url.trim() !== "" ? url : defaultAvatar;

    const goToUser = (u) => {
        setSearch("");
        setFilteredUsers([]);
        navigate("/user/" + u.username);
    };

    return (
        <div className="dashboard">
            <div className="left">
                <div className="card">
                    <button onClick={() => navigate("/dashboard")}>Back</button>

                    <div style={{ marginTop: "16px", display: "flex", alignItems: "center", gap: "12px" }}>
                        <img src={imgSrc} width="80" height="80" style={{ borderRadius: "50%" }} alt="avatar" />
                        <h2>@{username}</h2>
                    </div>

                    <div style={{ marginTop: "16px" }}>
                        {isFollowing ? (
                            <button onClick={unfollow}>Unfollow</button>
                        ) : (
                            <button onClick={follow}>Follow</button>
                        )}
                    </div>
                </div>
            </div>

            <div className="center">
                <div className="card">
                    <h3 style={{ marginTop: 0 }}>Posts</h3>
                    {isFollowing && <div>{posts.length} Posts on Page</div>}

                    {!isFollowing ? (
                        <div style={{ opacity: 0.7 }}>{errorPosts || "Follow to see posts"}</div>
                    ) : posts.length === 0 ? (
                        <div style={{ opacity: 0.7 }}>No posts yet.</div>
                    ) : (
                        <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                            {posts.map((post) => (
                                <div key={post.id} className="postItem">
                                    <div className="postHeader">
                                        <div className="postUser">@{username}</div>

                                        <div className="reactions">
                                            <button
                                                type="button"
                                                className={"reactBtn " + (post.myReaction === 1 ? "reactLikeOn" : "")}
                                                onClick={() => reactToPost(post.id, 1)}
                                            >
                                                👍 {post.likesCount ?? 0}
                                            </button>

                                            <button
                                                type="button"
                                                className={"reactBtn " + (post.myReaction === -1 ? "reactDislikeOn" : "")}
                                                onClick={() => reactToPost(post.id, -1)}
                                            >
                                                👎 {post.dislikesCount ?? 0}
                                            </button>
                                        </div>
                                    </div>

                                    <div className="postContent">{post.content}</div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            <div className="right">
                <div className="card">
                    <SearchUser
                        filter={search}
                        onChange={onSearchChange}
                        loading={loadingSearch}
                    />

                    <div style={{ marginTop: "12px" }}>
                        {search.trim().length >= 3 &&
                            filtredUsers
                                .filter((u) => u.username !== meUsername && u.username !== username )
                                .map((u, index) => (
                                    <div
                                        key={u.username || index}
                                        style={{ display: "flex", alignItems: "center", gap: "8px", padding: "6px 0" }}
                                    >
                                        <img
                                            src={u.url && u.url.trim() !== "" ? u.url : "/default-avatar.jpg"}
                                            width="32"
                                            height="32"
                                            style={{ borderRadius: "50%" }}
                                            alt=""
                                        />

                                        <div
                                            onClick={() => goToUser(u)}
                                            style={{ cursor: "pointer", flexGrow: 1 }}
                                        >
                                            {u.username}
                                        </div>

                                        <span
                                            style={{
                                                fontSize: "12px",
                                                padding: "2px 6px",
                                                borderRadius: "6px",
                                                backgroundColor: u.follow ? "#e0f7e9" : "#f0f0f0",
                                                color: u.follow ? "#1b7f4b" : "#777",
                                            }}
                                        >
                                            {u.follow ? "Following" : "Not following"}
                                        </span>
                                    </div>
                                ))}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default UserProfile;