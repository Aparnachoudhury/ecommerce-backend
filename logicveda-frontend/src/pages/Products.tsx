import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import api from "../lib/axios";
import Navbar from "../components/Navbar";

// ✅ Types
type Variant = {
  id: number;
  price: number;
};

type Product = {
  id: number;
  name: string;
  description: string;
  basePrice: number;
  variants?: Variant[];
};

const Products = () => {
  const [search, setSearch] = useState("");

  // ✅ Fetch products (with JWT via axios)
  const { data, isLoading, error } = useQuery<Product[]>({
    queryKey: ["products"],
    queryFn: async () => {
      const res = await api.get("/products");
      return res.data;
    },
  });

  // ✅ Filter
  const filteredProducts = data?.filter((product) =>
    product.name.toLowerCase().includes(search.toLowerCase())
  );

  // ✅ Add to cart
  const addToCart = async (variantId: number) => {
    try {
      await api.post("/cart/add", {
        variantId,
        quantity: 1,
      });

      alert("Added to cart 🛒");
    } catch (err) {
      console.error(err);
      alert("Failed to add to cart");
    }
  };

  // ✅ Loading
  if (isLoading) return <h2>Loading products...</h2>;

  // ❌ Error
  if (error) return <h2>Failed to load products</h2>;

  return (
    <>
      <Navbar />

      <div style={{ padding: "20px" }}>
        <h2>Products</h2>

        {/* 🔍 Search */}
        <input
          type="text"
          placeholder="Search products..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{
            padding: "10px",
            width: "300px",
            marginBottom: "20px",
            borderRadius: "8px",
            border: "1px solid #ccc",
          }}
        />

        {/* 🛍️ Product Grid */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
            gap: "20px",
          }}
        >
          {filteredProducts?.map((product) => {
            const variant = product.variants?.[0];

            return (
              <div
                key={product.id}
                style={{
                  border: "1px solid #ddd",
                  padding: "20px",
                  borderRadius: "12px",
                  boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
                  transition: "0.3s",
                }}
              >
                <h3>{product.name}</h3>
                <p>{product.description}</p>

                {/* 💰 Price */}
                <p>
                  <strong>
                    ₹ {variant?.price ?? product.basePrice}
                  </strong>
                </p>

                {/* 🛒 Add to Cart */}
                {variant && (
                  <button
                    onClick={() => addToCart(variant.id)}
                    style={{
                      marginTop: "10px",
                      padding: "10px",
                      backgroundColor: "#000",
                      color: "#fff",
                      border: "none",
                      borderRadius: "6px",
                      cursor: "pointer",
                      width: "100%",
                    }}
                  >
                    Add to Cart
                  </button>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};

export default Products;