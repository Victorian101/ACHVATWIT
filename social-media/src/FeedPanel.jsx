import {useEffect, useState} from "react";
import axios from "axios";
import "./FeedPanel.css";


function FeedPanel() {
    const [mode, setMode] = useState('feed');
    const [posts, setPosts] = useState([]);
    const [newPost, setNewPost] = useState('');

    const loadPosts = () => {
        const endPoint =
            mode === "feed"
                ? "http://localhost:9090/feed"
                : "http://localhost:9090/get-posts";

        axios.get(endPoint, {withCredentials: true})
            .then(res => {
                if (!res.data.success) {
                    setPosts([]);
                    return;
                }

                const basePosts = res.data.posts || [];

                const withDefaults = basePosts.map(p => ({
                    ...p,
                    likesCount: 0,
                    dislikesCount: 0,
                    myReaction: 0
                }));

                const requests = withDefaults.map(p =>
                    axios.get("http://localhost:9090/post-reaction?postId=" + p.id, {withCredentials: true})
                        .then(r => {
                            if (r.data && r.data.success) {
                                return {
                                    ...p,
                                    likesCount: r.data.likesCount ?? 0,
                                    dislikesCount: r.data.dislikesCount ?? 0,
                                    myReaction: r.data.myReaction ?? 0
                                };
                            }
                            return p;
                        })
                        .catch(() => p)
                );

                Promise.all(requests).then(enriched => {
                    setPosts(enriched);
                });
            })
            .catch(() => setPosts([]));
    };

    useEffect(() => {
        loadPosts();
    }, [mode]);

    const addPost = () => {
        const content = newPost.trim();
        if (content.length === 0 || content.length > 500) return
        axios.post('http://localhost:9090/add-post?content=' + content, {}, {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    setNewPost('');
                    loadPosts();
                }
            })
    }

    const removePost = (postId) => {
        axios.post('http://localhost:9090/delete-post?id=' + postId, {}, {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    loadPosts();
                }
            })
            .catch(err => console.log(err));
    }

    const reactToPost = (postId, reaction) => {
        axios.post(
            "http://localhost:9090/change-reaction?postId=" + postId + "&reaction=" + reaction,
            {},
            {withCredentials: true}
        ).then(res => {
            if (!res.data.success) return;

            setPosts(prev =>
                prev.map(p => {
                    if (p.id !== postId) return p;

                    const old = p.myReaction || 0;
                    let next = old;

                    if (old === reaction) next = 0;
                    else next = reaction;

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
                        dislikesCount: dislikes
                    };
                })
            );
        });
    };

    return (
        <div>
            <div
                style={{
                    fontWeight: 900,
                    fontSize: 18,
                    marginBottom: 8,
                    color: "#2b1350"
                }}
            >
                {mode === "feed" ? "📰 Feed" : "✍️ My Posts"}
            </div>

            <div
                style={{
                    fontSize: 13,
                    opacity: 0.7,
                    marginBottom: 12
                }}
            >
                {mode === "feed"
                    ? "Posts from people you follow"
                    : "Posts you have published"}
            </div>
            <div style={{display: "flex", gap: "8px", marginBottom: "12px"}}>
                <button onClick={() => setMode("feed")} disabled={mode === "feed"}>
                    Feed
                </button>

                <button onClick={() => setMode("mine")} disabled={mode === "mine"}>
                    My Posts
                </button>

                {
                    mode === "mine" &&
                    <>
                        <input value={newPost}
                               onChange={(e) => setNewPost(e.target.value)}
                               maxLength={500}
                               placeholder="Write a post..."
                               dir="auto"/>
                        <div style={{fontSize: "12px", opacity: 0.7}}>
                            {newPost.length}/500
                        </div>
                        <button type="button" onClick={addPost} disabled={newPost.trim().length === 0}>
                            Add Post
                        </button>
                    </>
                }
            </div>

            <div>
                {
                    mode === "mine" &&
                    <div>{posts.length} Posts On Page</div>
                }
            </div>

            <div style={{display: "flex", flexDirection: "column", gap: "10px"}}>
                {
                    posts.length === 0 ? (
                        <div style={{opacity: 0.7}}>
                            {mode === "mine" ? "No posts yet." : "No feed posts yet."}
                        </div>
                    ) : (
                        posts.map((post) => (
                            <div key={post.id} className="postItem">
                                {mode === "feed" ? (
                                    <div className="postHeader">
                                        <div className="postUser">@{post.username}</div>

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
                                ) : (
                                    <div className="postHeader">
                                        <div className="postUser"> My post</div>

                                        <div className="reactions">
                                            <span className="reactStat">👍 {post.likesCount ?? 0}</span>
                                            <span className="reactStat">👎 {post.dislikesCount ?? 0}</span>

                                            <button type="button" className="dangerBtn"
                                                    onClick={() => removePost(post.id)}>
                                                Delete
                                            </button>
                                        </div>
                                    </div>
                                )}
                                <div className="postContent" dir="auto">{post.content}</div>
                            </div>
                        ))
                    )
                }
            </div>
        </div>
    )
}

export default FeedPanel;