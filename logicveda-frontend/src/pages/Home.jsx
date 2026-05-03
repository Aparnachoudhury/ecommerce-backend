import { useEffect, useState } from "react";
import axios from "axios";
import SearchBar from "../components/SearchBar";

const Home = () => {
  const [products, setProducts] = useState([]);

  // load all products initially
  useEffect(() => {
    axios.get("http://localhost:8080/api/products")
      .then(res => setProducts(res.data));
  }, []);

  return (
    <div>
      <h2>Products</h2>

      <SearchBar setProducts={setProducts} />

      <div>
        {products.map((p) => (
          <div key={p.id}>
            <h4>{p.name}</h4>
            <p>{p.description}</p>
            <p>₹ {p.basePrice}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Home;