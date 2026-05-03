import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../lib/axios";
import useAuthStore from "../store/authStore";

const Login = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await api.post("/auth/login", {
        email,
        password,
      });

      // 🔍 DEBUG (see actual backend response)
      console.log("LOGIN RESPONSE:", res.data);

      // ✅ SAFE TOKEN HANDLING (supports any backend format)
      const token = res.data.token || res.data.accessToken;

      if (!token) {
        throw new Error("Token not found in response");
      }

      // ✅ SAFE USER DATA HANDLING
      const user = {
        email: res.data.user?.email || email,
        role: res.data.user?.role || "CUSTOMER",
      };

      // ✅ STORE IN ZUSTAND
      setAuth(token, user);

      alert("Login successful ✅");

      // ✅ REDIRECT
      navigate("/dashboard");

    } catch (error: any) {
      console.error("LOGIN ERROR:", error);

      const message =
        error.response?.data?.message ||
        error.response?.data ||
        error.message ||
        "Login failed";

      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        padding: "50px",
        display: "flex",
        justifyContent: "center",
      }}
    >
      <form
        onSubmit={handleLogin}
        style={{
          width: "300px",
          border: "1px solid #ddd",
          padding: "20px",
          borderRadius: "10px",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
        }}
      >
        <h2 style={{ textAlign: "center" }}>Login</h2>

        <input
          type="email"
          placeholder="Enter email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          style={{
            width: "100%",
            padding: "10px",
            marginBottom: "10px",
            borderRadius: "6px",
            border: "1px solid #ccc",
          }}
        />

        <input
          type="password"
          placeholder="Enter password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          style={{
            width: "100%",
            padding: "10px",
            marginBottom: "15px",
            borderRadius: "6px",
            border: "1px solid #ccc",
          }}
        />

        <button
          type="submit"
          disabled={loading}
          style={{
            width: "100%",
            padding: "10px",
            backgroundColor: "#000",
            color: "#fff",
            border: "none",
            borderRadius: "6px",
            cursor: "pointer",
          }}
        >
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
};

export default Login;