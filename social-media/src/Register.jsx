import {useState} from "react";
import {useNavigate,} from "react-router-dom";

import "./Register.css";

import axios from "axios";

function Register() {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [passwordConfirm, setPasswordConfirm] = useState('')
    const [error, setError] = useState('');

    const navigate = useNavigate();

    const register = () => {
        if (password === passwordConfirm) {
            axios.post('http://localhost:9090/register', {username, password})
                .then(res => {
                    if (res.data.success) {
                        navigate('/');
                    } else {
                        setUsername("");
                        setPassword("");
                        setPasswordConfirm("");
                        setError('Register failed');
                    }
                })
        } else {
            setUsername("");
            setPassword("");
            setPasswordConfirm("");
            setError('Passwords do not match');
        }
    }

    return (
        <div className="authPage">
            <div className="authCard">
                <h1 className="authTitle">Register</h1>
                <p className="authSubtitle">Create a new account</p>

                <div className="field">
                    <div className="fieldLabel">Username</div>
                    <input
                        className="authInput"
                        value={username}
                        type="text"
                        placeholder="Username"
                        onChange={e => setUsername(e.target.value)}
                    />
                </div>

                <div className="field">
                    <div className="fieldLabel">Password</div>
                    <input
                        className="authInput"
                        value={password}
                        type="password"
                        placeholder="Password"
                        onChange={e => setPassword(e.target.value)}
                    />
                </div>

                <div className="field">
                    <div className="fieldLabel">Confirm Password</div>
                    <input
                        className="authInput"
                        value={passwordConfirm}
                        type="password"
                        placeholder="Password Confirm"
                        onChange={e => setPasswordConfirm(e.target.value)}
                    />
                </div>

                <div className="actions">
                    <button
                        className="btnPrimary"
                        onClick={register}
                        disabled={username.length < 3 || password.length < 3 || passwordConfirm.length === 0}
                        type="button"
                    >
                        Register
                    </button>

                    <button
                        className="btnGhost"
                        onClick={() => navigate("/")}
                        type="button"
                    >
                        Back to Login
                    </button>
                </div>

                {error && <div className="errorBox">{error}</div>}
            </div>
        </div>
    )
}

export default Register