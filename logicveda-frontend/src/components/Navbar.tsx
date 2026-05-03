import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../store/authStore";

const Navbar = () => {
  const navigate = useNavigate();
  const logout = useAuthStore((state) => state.logout);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "15px 30px",
        background: "#222",
        color: "white",
      }}
       >
            <h2>Ecommerce</h2>

            <div style={{ display: "flex", gap: "20px" }}>
              <Link to="/dashboard" style={{ color: "white" }}>Dashboard</Link>
              <Link to="/products" style={{ color: "white" }}>Products</Link>
              <button onClick={handleLogout}>Logout</button>
            </div>
          </div>
        );
      };

      export default Navbar;