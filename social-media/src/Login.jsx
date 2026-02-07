import {useState} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import "./Login.css";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const login = () => {
        try {
            axios.post('http://localhost:9090/login', {username, password}, {withCredentials: true})
                .then(res => {
                    if (res.data.success) {
                        navigate('/dashboard');
                    } else {
                        setUsername('');
                        setPassword('');
                        setError('Failed to login');
                    }
                })
        } catch (err) {
            console.log("login error:", err);
            console.log("status:", err?.response?.status);
            console.log("data:", err?.response?.data);
            setError('Network / server error');
        }

    }
    return (
        <div className="authPage">
            <div className="authCard">
                <h1 className="authTitle">Login</h1>
                <p className="authSubtitle">Sign in to ACHVATWIT</p>

                <div className="field">
                    <div className="fieldLabel">Username</div>
                    <input
                        className="authInput"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Username"
                    />
                </div>

                <div className="field">
                    <div className="fieldLabel">Password</div>
                    <input
                        className="authInput"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                    />
                </div>

                <div className="actions">
                    <button
                        className="btnPrimary"
                        onClick={login}
                        disabled={username.length === 0 || password.length === 0}
                        type="button"
                    >
                        Login
                    </button>

                    <button
                        className="btnGhost"
                        onClick={() => navigate("/register")}
                        type="button"
                    >
                        Register
                    </button>
                </div>

                {error && <div className="errorBox">{error}</div>}
            </div>
        </div>
    )
}

export default Login