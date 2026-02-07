import {BrowserRouter, Route, Routes} from "react-router-dom";
import './App.css'
import Register from "./Register.jsx";
import Login from "./Login.jsx";
import Dashboard from "./Dashboard.jsx";
import UserProfile from "./UserProfile.jsx";
import ChangePassword from "./ChangePassword.jsx";
import Header from "./Header.jsx";

function App() {
  return (
      <div>
          <Header/>
        <BrowserRouter>
            <Routes>
                <Route path= "/" element={<Login />} />
                <Route path="register" element={<Register />} />
                <Route path="dashboard" element={<Dashboard />} />
                <Route path="user/:username" element={<UserProfile />} />
                <Route path="change-password" element={<ChangePassword />} />

            </Routes>
        </BrowserRouter>
      </div>
  )
}

export default App