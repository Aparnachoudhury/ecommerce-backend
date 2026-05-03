import { useEffect, useState } from "react";
import axios from "axios";
import useAuthStore from "../store/authStore";

const AdminDashboard = () => {
  const token = useAuthStore((state) => state.token);
  const [orders, setOrders] = useState<any[]>([]);

  // ✅ Fetch all orders
  useEffect(() => {
    axios.get("http://localhost:8080/api/orders/admin/all", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((res) => {
      setOrders(res.data);
    });
  }, [token]);

  // ✅ Update order status
  const updateStatus = async (id: number, status: string) => {
    await axios.put(
      `http://localhost:8080/api/orders/${id}/status?status=${status}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    alert("Updated!");

    // refresh
    const res = await axios.get("http://localhost:8080/api/orders/admin/all", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    setOrders(res.data);
  };

  return (
    <div style={{ padding: "40px" }}>
      <h1>👑 Admin Dashboard</h1>

      <h2>All Orders</h2>

      <table border={1} cellPadding={10}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Status</th>
            <th>Update</th>
          </tr>
        </thead>

        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.status}</td>
              <td>
                <button onClick={() => updateStatus(o.id, "PAID")}>
                  Paid
                </button>
                <button onClick={() => updateStatus(o.id, "SHIPPED")}>
                  Ship
                </button>
                <button onClick={() => updateStatus(o.id, "DELIVERED")}>
                  Deliver
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminDashboard;