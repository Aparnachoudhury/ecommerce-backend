import { useNavigate } from "react-router-dom";
import useAuthStore from "../store/authStore";
import { useEffect, useState } from "react";
import axios from "axios";

const Dashboard = () => {
  const user = useAuthStore((state) => state.user);
  const token = useAuthStore((state) => state.token);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();

  const [products, setProducts] = useState<any[]>([]);
  const [orders, setOrders] = useState<any[]>([]);
  const [name, setName] = useState("");

  const config = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const goToAdmin = () => {
    navigate("/admin");
  };

  // ✅ Fetch products
  useEffect(() => {
    if (!token) return;

    axios
      .get("http://localhost:8080/api/products", config)
      .then((res) => setProducts(res.data))
      .catch((err) => console.error("Products error:", err));
  }, [token]);

  // ✅ Fetch orders
  useEffect(() => {
    if (!token) return;

    axios
      .get("http://localhost:8080/api/orders/my", config)
      .then((res) => setOrders(res.data))
      .catch((err) => console.error("Orders error:", err));
  }, [token]);

  // ✅ Add product
  const addProduct = async () => {
    if (!name) return;

    try {
      const res = await axios.post(
        "http://localhost:8080/api/products",
        { name, price: 100 },
        config
      );

      setProducts([...products, res.data]);
      setName("");
    } catch (err) {
      console.error("Add product error:", err);
    }
  };

  // ✅ Delete product
  const deleteProduct = async (id: number) => {
    try {
      await axios.delete(
        `http://localhost:8080/api/products/${id}`,
        config
      );

      setProducts(products.filter((p) => p.id !== id));
    } catch (err) {
      console.error("Delete error:", err);
    }
  };

  // 💳 ✅ FIXED PAYMENT FUNCTION
  const handlePayment = async (orderId: number) => {
    try {
      // 1️⃣ Create Razorpay order
      const res = await axios.post(
        "http://localhost:8080/api/orders/payment/create?amount=500",
        {},
        config
      );

      const razorpayOrderId = res.data;

      // 2️⃣ Razorpay options
      const options = {
        key: "rzp_test_SkaianNXPEWPHq",
        amount: 50000,
        currency: "INR",
        name: "Aparna Store",
        description: "Order Payment",
        order_id: razorpayOrderId,

        // ✅ IMPORTANT HANDLER
        handler: async function (response: any) {
          console.log("Payment response:", response);

          try {
            // 3️⃣ Verify payment
            await axios.post(
              "http://localhost:8080/api/payment/verify",
              {
                razorpayOrderId: response.razorpay_order_id,
                razorpayPaymentId: response.razorpay_payment_id,
                razorpaySignature: response.razorpay_signature,
                orderId: orderId,
              },
              config
            );

            alert("Payment successful!");
            navigate("/orders");   // 👈 ADD THIS

            // 4️⃣ Refresh orders
            const updated = await axios.get(
              "http://localhost:8080/api/orders/my",
              config
            );
            setOrders(updated.data);

          } catch (err) {
            console.error("Verification error:", err);
            alert("❌ Payment verification failed");
          }
        },

        theme: {
          color: "#3399cc",
        },
      };

      const rzp = new (window as any).Razorpay(options);
      rzp.open();

    } catch (err) {
      console.error("Payment error:", err);
      alert("❌ Payment failed");
    }
  };

  return (
    <div style={{ padding: "50px" }}>
      <h1>Welcome {user?.name || "User"} 👋</h1>

      <button onClick={handleLogout}>Logout</button>

      <button
        onClick={goToAdmin}
        style={{ marginLeft: "10px", background: "black", color: "white" }}
      >
        Admin Panel
      </button>

      <hr />

      <h2>Vendor Dashboard</h2>

      <input
        placeholder="Product name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <button onClick={addProduct}>Add Product</button>

      <h3>Products</h3>
      <ul>
        {products.map((p) => (
          <li key={p.id}>
            {p.name}
            <button onClick={() => deleteProduct(p.id)}>Delete</button>
          </li>
        ))}
      </ul>

      <h3>Orders</h3>
      <ul>
        {orders.map((o) => (
          <li key={o.id}>
            Order #{o.id} - {o.status}

            {o.status === "PENDING_PAYMENT" && (
              <button
                onClick={() => handlePayment(o.id)}
                style={{ marginLeft: "10px" }}
              >
                Pay
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Dashboard;