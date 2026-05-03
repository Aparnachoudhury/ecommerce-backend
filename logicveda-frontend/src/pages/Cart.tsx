import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "../lib/axios";
import Navbar from "../components/Navbar";

type CartItem = {
  id: number;
  quantity: number;
  variant: {
    id: number;
    price: number;
    productName: string;
  };
};

export default function Cart() {
  const queryClient = useQueryClient();

  // ✅ Fetch cart
  const { data, isLoading } = useQuery({
    queryKey: ["cart"],
    queryFn: async () => {
      const res = await api.get("/cart");
      return res.data;
    },
  });

  // ✅ Remove item
  const removeItem = useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/cart/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
  });

  if (isLoading) return <h2>Loading cart...</h2>;

  return (
    <>
      <Navbar />

      <div style={{ padding: "20px" }}>
        <h2>Your Cart</h2>

        {data?.length === 0 && <p>Cart is empty</p>}

        {data?.map((item: CartItem) => (
          <div
            key={item.id}
            style={{
              border: "1px solid #ddd",
              padding: "15px",
              marginBottom: "10px",
              borderRadius: "8px",
            }}
          >
            <h3>{item.variant.productName}</h3>
            <p>Price: ₹{item.variant.price}</p>
            <p>Quantity: {item.quantity}</p>

            <button
              onClick={() => removeItem.mutate(item.id)}
              style={{
                backgroundColor: "red",
                color: "white",
                padding: "5px 10px",
                border: "none",
                borderRadius: "5px",
              }}
            >
              Remove
            </button>
          </div>
        ))}
      </div>
    </>
  );
}