import ProfileImage from "./ProfileImage.jsx";
import {useState} from "react";
import "./Profile.css";

function Profile(props) {
    const username = props.username;
    const url = props.url;

    const [showHelp, setShowHelp] = useState(false);

    const followers = props.followers || [];
    const following = props.following || [];
    const whatList = props.whatList;
    const setWhatList = props.setWhatList;

    const toggleList = (type) => {
        setWhatList(whatList === type ? "" : type);
    };

    const listToShow = whatList === "followers" ? followers : following;


    return (
        <div className="profileWrap">
            <ProfileImage url={url} />

            <div className="profileName">@{username}</div>

            <input
                className="profileInput"
                type="text"
                placeholder="Change profile image URL"
                onChange={(e) => props.onChange(e.target.value)}
            />

            <div className="profileBtnRow">
                <button className="profileBtn" onClick={props.onClick}>
                    Save Profile
                </button>

                <button className="profileBtn" onClick={props.onClickDelete}>
                    Delete Image
                </button>
            </div>

            <div style={{display: "flex", gap: 12, marginTop: 10, fontSize: 14}}>
            <span
                style={{cursor: "pointer", textDecoration: "underline"}}
                onClick={() => toggleList("following")}
            >
                Following: <b>{following.length}</b>
            </span>

                <span
                    style={{cursor: "pointer", textDecoration: "underline"}}
                    onClick={() => toggleList("followers")}
                >
                Followers: <b>{followers.length}</b>
            </span>
            </div>

            {whatList !== "" && (
                <div style={{marginTop: 10, width: "100%"}}>
                    <div style={{fontWeight: 800, marginBottom: 6}}>
                        {whatList === "followers" ? "Followers" : "Following"}
                    </div>

                    {listToShow.length === 0 ? (
                        <div style={{opacity: 0.7}}>Empty</div>
                    ) : (
                        <div
                            style={{
                                display: "flex",
                                flexDirection: "column",
                                gap: 8,
                                maxHeight: 220,
                                overflowY: "auto",
                                paddingRight: 6
                            }}
                        >
                            {listToShow.map((u, idx) => (
                                <div
                                    key={u.username || idx}
                                    onClick={() => props.onUserClick?.(u)}
                                    style={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: 8,
                                        cursor: "pointer",
                                        padding: "6px 4px",
                                        borderRadius: 8
                                    }}
                                >
                                    <img
                                        src={u.url && u.url.trim() !== "" ? u.url : "/default-avatar.jpg"}
                                        width="28"
                                        height="28"
                                        style={{borderRadius: "50%"}}
                                        alt=""
                                    />
                                    <div>@{u.username}</div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}

            <div className="profileHelpWrap">
                <button
                    type="button"
                    className="profileHelpBtn"
                    onClick={() => setShowHelp(v => !v)}
                >
                    ? איך מעלים תמונת פרופיל
                </button>

                {showHelp && (
                    <div className="profileHelpCard" dir="rtl">
                        <div className="profileHelpTitle">
                            הוראות לתמונת פרופיל
                        </div>

                        <div className="profileHelpText">
                            1) העלה תמונה ל-GitHub<br/>
                            2) קח קישור raw<br/>
                            <div className="profileHelpCode" dir="ltr">
                                https://raw.githubusercontent....jpg
                            </div>
                            3) בלי refs/heads<br/>
                            4) הדבק באתר ורענן עמוד
                        </div>

                        <div className="profileHelpCloseRow">
                            <button
                                className="profileBtn"
                                type="button"
                                onClick={() => setShowHelp(false)}
                            >
                                סגור
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Profile;