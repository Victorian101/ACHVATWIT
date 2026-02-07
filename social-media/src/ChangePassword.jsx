import {useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import "./ChangePassword.css";

function ChangePassword(){
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword , setConfirmNewPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const changePassword = () => {
        if (newPassword !== confirmNewPassword) {
            setError('Password not match');
            setConfirmNewPassword('');
            setNewPassword('');
            setOldPassword('');
            return
        }
        if(newPassword.length < 3){
            setError('Password must be at least 3 characters');
            setConfirmNewPassword('');
            setNewPassword('');
            setOldPassword('');
            return;
        }
        axios.post('http://localhost:9090/change-password', {oldPassword: oldPassword, newPassword: newPassword, confirmNewPassword: confirmNewPassword}, {withCredentials: true})
            .then(res => {
                if(res.data.success){
                    navigate("/");
                }else{
                    setError('Change Password Failed');
                    setConfirmNewPassword('');
                    setNewPassword('');
                    setOldPassword('');
                }
            }).catch(err => {
                console.log(err)
            setError('Change Password Failed');
        });
    }
    return (
        <div className="authPage">
            <div className="authCard">
                <h1 className="authTitle">Change password</h1>
                <p className="authSubtitle">Choose a new password for your account.</p>

                <label className="authLabel">Old password</label>
                <input
                    className="authInput"
                    value={oldPassword}
                    onChange={(e) => { setOldPassword(e.target.value); if (error) setError(""); }}
                    type="password"
                    placeholder="Old password"
                />

                <label className="authLabel">New password</label>
                <input
                    className="authInput"
                    value={newPassword}
                    onChange={(e) => { setNewPassword(e.target.value); if (error) setError(""); }}
                    type="password"
                    placeholder="New password"
                />

                <label className="authLabel">Confirm new password</label>
                <input
                    className="authInput"
                    value={confirmNewPassword}
                    onChange={(e) => { setConfirmNewPassword(e.target.value); if (error) setError(""); }}
                    type="password"
                    placeholder="Confirm new password"
                />

                {error && <div className="authError">{error}</div>}

                <button
                    className="authPrimaryBtn"
                    onClick={changePassword}
                    type="button"
                    disabled={newPassword === "" || oldPassword === "" || confirmNewPassword === ""}
                >
                    Change password
                </button>

                <button className="authSecondaryBtn" onClick={() => navigate("/dashboard")} type="button">
                    Back
                </button>
            </div>
        </div>
    )
}
export default ChangePassword;