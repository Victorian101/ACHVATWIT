function ProfileImage(props) {
    const url = props.url;

    const defaultAvatar = "/default-avatar.jpg";

    const imgSrc =
        url && url.trim() !== ""
            ? url
            : defaultAvatar;

    return (
        <img
            src={imgSrc}
            alt="profile"
            width="120"
        />
    );
}

export default ProfileImage;