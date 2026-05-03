import { useState } from "react";
import axios from "axios";
import debounce from "lodash/debounce";
import debounce from "lodash/debounce";

const SearchBar = ({ setProducts }) => {
  const [query, setQuery] = useState("");

  const fetchResults = debounce(async (value) => {
    try {
      const res = await axios.get("http://localhost:8080/api/products/search", {
        params: { q: value }
      });
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    }
  }, 500);

  const handleChange = (e) => {
    const value = e.target.value;
    setQuery(value);
    fetchResults(value);
  };

  return (
    <input
      type="text"
      placeholder="Search products..."
      value={query}
      onChange={handleChange}
      style={{ padding: "10px", width: "300px" }}
    />
  );
};

export default SearchBar;