import axios from "axios";
import {useNavigate} from "react-router-dom";
import {useState, useEffect} from "react";
import Profile from "./Profile.jsx";
import SearchUser from "./SearchUser.jsx";
import "./Dashboard.css";
import FeedPanel from "./FeedPanel.jsx";

function Dashboard() {
    const [username, setUsername] = useState("");
    const [url, setUrl] = useState("");
    const [newUrl, setNewUrl] = useState("");

    const [search, setSearch] = useState("");
    const [filtredUsers, setFilteredUsers] = useState([]);
    const [searchLoading, setSearchLoading] = useState(false);

    const [listOfFollowers, setListOfFollowers] = useState([]);
    const [listOfFollowing, setListOfFollowing] = useState([]);
    const [whatList, setWhatList] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        axios.get("http://localhost:9090/followers", {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    setListOfFollowers(res.data.users || []);
                } else {
                    setListOfFollowers([]);
                }
            })
            .catch(() => setListOfFollowers([]));

        axios.get("http://localhost:9090/following", {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    setListOfFollowing(res.data.users || []);
                } else {
                    setListOfFollowing([]);
                }
            })
            .catch(() => setListOfFollowing([]));
    }, []);

    useEffect(() => {
        axios.get('http://localhost:9090/me', {withCredentials: true})
            .then(res => {
                console.log("ME DATA:", res.data);
                if (res.data && res.data.success) {
                    setUsername(res.data.username);
                    setUrl(res.data.url);
                    setNewUrl('');
                } else {
                    navigate('/');
                }
            })
            .catch(() => {
                navigate('/');
            });
    }, [navigate]);

    const logout = () => {
        axios.post('http://localhost:9090/logout', {}, {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    navigate('/');
                }
            })
    }

    const saveProfile = () => {
        axios.post('http://localhost:9090/update-url', {url: newUrl}, {withCredentials: true})
            .then(res => {
                console.log("SAVING URL:", newUrl);
                if (res.data.success) {
                    setUrl(newUrl);
                } else {
                    console.log('Save faild', res.data);
                }
            })
            .catch(err => console.log(err));
    }

    const deleteProfile = () => {
        axios.post('http://localhost:9090/delete-url', {}, {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    setUrl('');
                    setNewUrl('');
                }
            })
            .catch(err => console.log(err));
    }

    const filteredUsersF = (f) => {
        const q = (f || "").trim();

        if (q.length < 3) {
            setFilteredUsers([]);
            setSearchLoading(false);
            return;
        }

        setSearchLoading(true);

        axios.get("http://localhost:9090/get-filtred-users?f=" + encodeURIComponent(q), {withCredentials: true})
            .then(res => {
                if (res.data.success) {
                    setFilteredUsers(res.data.users || []);
                } else {
                    setFilteredUsers([]);
                }
            })
            .catch(() => setFilteredUsers([]))
            .finally(() => setSearchLoading(false));
    };

    useEffect(() => {
        const q = search.trim();

        // לנקות תוצאות כשפחות מ-3
        if (q.length < 3) {
            setFilteredUsers([]);
            setSearchLoading(false);
            return;
        }

        // Debounce כדי לא “להפציץ” שרת ולא לקפוץ
        const t = setTimeout(() => {
            filteredUsersF(q);
        }, 300);

        return () => clearTimeout(t);
    }, [search]);

    const goToUser = (u) => {
        setSearch("");
        setFilteredUsers([]);
        navigate("/user/" + u.username);
    };

    return (
        <div className="dashboard">

            <div className="left">
                <div className="card">
                    <Profile
                        username={username}
                        url={url}
                        onChange={setNewUrl}
                        onClick={saveProfile}
                        onClickDelete={deleteProfile}
                        followers={listOfFollowers}
                        following={listOfFollowing}
                        whatList={whatList}
                        setWhatList={setWhatList}
                        onUserClick={(u) => navigate("/user/" + u.username)}
                    />
                    <button onClick={() => navigate('/change-password')}>
                        Change Password
                    </button>
                    <button onClick={logout}>
                        Logout
                    </button>
                </div>
            </div>

            <div className="center">
                <div className="card">
                    <FeedPanel/>
                </div>
            </div>

            <div className="right">
                <div className="card">
                    <SearchUser
                        filter={search}
                        onChange={setSearch}
                        loading={searchLoading}
                    />

                    <div className="searchResults">
                        {search.trim().length >= 3 && !searchLoading && filtredUsers.length === 0 && (
                            <div className="searchEmpty">No users found</div>
                        )}

                        {search.trim().length >= 3 && filtredUsers
                            .filter(u => u.username !== username)
                            .map((u, index) => (
                                <div key={u.username || index} className="userRow">
                                    <img
                                        className="userAvatar"
                                        src={u.url && u.url.trim() !== "" ? u.url : "/default-avatar.jpg"}
                                        width="32"
                                        height="32"
                                        alt=""
                                    />

                                    <div
                                        className="userName"
                                        onClick={() => goToUser(u)}
                                        title="Open profile"
                                    >
                                        {u.username}
                                    </div>

                                    <span className={u.follow ? "pill pillOn" : "pill pillOff"}>
                    {u.follow ? "Following" : "Not following"}
                </span>
                                </div>
                            ))}
                    </div>

                </div>
            </div>


        </div>)
}


export default Dashboard;


