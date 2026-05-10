import React, { useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Boxes,
  CreditCard,
  LogIn,
  LogOut,
  PackagePlus,
  RefreshCw,
  Search,
  ShoppingCart,
  Trash2,
  UserPlus
} from "lucide-react";
import "./styles.css";

const TOKEN_KEY = "cartx_token";

function parseJwt(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
  } catch {
    return null;
  }
}

async function readResponse(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || "");
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState({ userId: "", items: [] });
  const [orders, setOrders] = useState([]);
  const [query, setQuery] = useState("");
  const [authMode, setAuthMode] = useState("login");
  const [authForm, setAuthForm] = useState({ email: "", password: "" });
  const [productForm, setProductForm] = useState({
    name: "",
    category: "",
    description: "",
    price: "",
    stockQuantity: "",
    imageUrls: ""
  });
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [checkout, setCheckout] = useState(null);

  const user = useMemo(() => parseJwt(token), [token]);
  const cartTotal = useMemo(() => {
    return (cart.items || []).reduce((total, item) => {
      const product = products.find((p) => p.id === item.productId);
      return total + (product?.price || 0) * item.quantity;
    }, 0);
  }, [cart.items, products]);

  const filteredProducts = useMemo(() => {
    const term = query.trim().toLowerCase();
    if (!term) return products;
    return products.filter((product) => {
      return [product.name, product.category, product.description]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(term));
    });
  }, [products, query]);

  function authHeaders() {
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  async function api(path, options = {}) {
    const response = await fetch(path, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...authHeaders(),
        ...(options.headers || {})
      }
    });
    const body = await readResponse(response);
    if (!response.ok) {
      throw new Error(typeof body === "string" ? body : body?.message || `Request failed (${response.status})`);
    }
    return body;
  }

  async function loadProducts() {
    const data = await api("/products");
    setProducts(Array.isArray(data) ? data : []);
  }

  async function loadCart() {
    if (!token) {
      setCart({ userId: "", items: [] });
      return;
    }
    const data = await api("/cart");
    setCart(data || { userId: "", items: [] });
  }

  async function loadOrders() {
    if (!token) {
      setOrders([]);
      return;
    }
    const data = await api("/orders/my");
    setOrders(Array.isArray(data) ? data : []);
  }

  async function refreshAll() {
    setLoading(true);
    setMessage("");
    try {
      await loadProducts();
      await loadCart();
      await loadOrders();
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refreshAll();
  }, [token]);

  async function submitAuth(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    try {
      const result = await api(`/auth/${authMode}`, {
        method: "POST",
        body: JSON.stringify(authForm)
      });

      if (authMode === "login") {
        localStorage.setItem(TOKEN_KEY, result);
        setToken(result);
        setMessage("Logged in successfully.");
      } else {
        setAuthMode("login");
        setMessage("Registered successfully. You can log in now.");
      }
      setAuthForm({ email: "", password: "" });
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY);
    setToken("");
    setCheckout(null);
    setMessage("Logged out.");
  }

  async function addToCart(product, quantity) {
    if (!token) {
      setMessage("Log in before adding items to your cart.");
      return;
    }
    setLoading(true);
    setMessage("");
    try {
      await api("/cart/items", {
        method: "POST",
        body: JSON.stringify({ productId: product.id, quantity })
      });
      await loadCart();
      setMessage(`${product.name} added to cart.`);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function createProduct(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    try {
      const payload = {
        ...productForm,
        price: Number(productForm.price),
        stockQuantity: Number(productForm.stockQuantity),
        imageUrls: productForm.imageUrls
          .split("\n")
          .map((url) => url.trim())
          .filter(Boolean)
      };
      await api("/products", {
        method: "POST",
        body: JSON.stringify(payload)
      });
      setProductForm({
        name: "",
        category: "",
        description: "",
        price: "",
        stockQuantity: "",
        imageUrls: ""
      });
      await loadProducts();
      setMessage("Product created.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function removeProduct(productId) {
    setLoading(true);
    setMessage("");
    try {
      await api(`/products/${productId}`, { method: "DELETE" });
      await loadProducts();
      await loadCart();
      setMessage("Product deleted.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function startCheckout() {
    if (!cart.items?.length) {
      setMessage("Your cart is empty.");
      return;
    }
    setLoading(true);
    setMessage("");
    try {
      const data = await api("/payments/checkout", { method: "POST" });
      setCheckout(data);
      setMessage("Checkout created. Confirm the mock payment to place the order.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function confirmPayment() {
    if (!checkout) return;
    setLoading(true);
    setMessage("");
    try {
      await api("/payments/confirm", {
        method: "POST",
        body: JSON.stringify({
          paymentId: checkout.razorpayOrderId,
          status: "SUCCESS",
          amount: checkout.amount
        })
      });
      await loadCart();
      await loadOrders();
      setCheckout(null);
      setMessage("Payment confirmed. The order service will receive the event from Kafka.");
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">CartX commerce</p>
          <h1>Products, carts, payments, and orders in one desk.</h1>
        </div>
        <div className="topbar-actions">
          <button className="icon-button" onClick={refreshAll} disabled={loading} title="Refresh data">
            <RefreshCw size={18} />
          </button>
          {token ? (
            <button className="secondary" onClick={logout}>
              <LogOut size={17} />
              Logout
            </button>
          ) : null}
        </div>
      </header>

      {message ? <div className="notice">{message}</div> : null}

      <section className="status-strip">
        <Status icon={<Boxes size={18} />} label="Products" value={products.length} />
        <Status icon={<ShoppingCart size={18} />} label="Cart items" value={cart.items?.length || 0} />
        <Status icon={<CreditCard size={18} />} label="Cart total" value={formatMoney(cartTotal)} />
        <Status icon={<LogIn size={18} />} label="Session" value={user?.sub || "Guest"} />
      </section>

      <div className="layout">
        <section className="catalog-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Catalog</p>
              <h2>Products</h2>
            </div>
            <label className="search">
              <Search size={17} />
              <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search products" />
            </label>
          </div>

          <div className="product-grid">
            {filteredProducts.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
                cartItem={cart.items?.find((item) => item.productId === product.id)}
                onAdd={addToCart}
                onDelete={removeProduct}
              />
            ))}
            {!filteredProducts.length ? <p className="empty">No products found.</p> : null}
          </div>
        </section>

        <aside className="side-panel">
          <AuthPanel
            token={token}
            user={user}
            authMode={authMode}
            setAuthMode={setAuthMode}
            authForm={authForm}
            setAuthForm={setAuthForm}
            onSubmit={submitAuth}
          />
          <CartPanel
            cart={cart}
            products={products}
            total={cartTotal}
            checkout={checkout}
            onCheckout={startCheckout}
            onConfirm={confirmPayment}
          />
          <OrdersPanel orders={orders} />
          <ProductEditor form={productForm} setForm={setProductForm} onSubmit={createProduct} />
        </aside>
      </div>
    </main>
  );
}

function Status({ icon, label, value }) {
  return (
    <div className="status-item">
      {icon}
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function ProductCard({ product, cartItem, onAdd, onDelete }) {
  const [quantity, setQuantity] = useState(1);
  const image = product.imageUrls?.[0];
  const outOfStock = product.status === "OUT_OF_STOCK" || product.stockQuantity <= 0;

  return (
    <article className="product-card">
      <div className="product-image">
        {image ? <img src={image} alt={product.name} /> : <Boxes size={34} />}
      </div>
      <div className="product-body">
        <div>
          <span className={outOfStock ? "badge danger" : "badge"}>{product.status || "IN_STOCK"}</span>
          <h3>{product.name || "Untitled product"}</h3>
          <p>{product.description || "No description yet."}</p>
        </div>
        <div className="product-meta">
          <span>{product.category || "General"}</span>
          <strong>{formatMoney(product.price)}</strong>
        </div>
        <div className="product-actions">
          <input
            type="number"
            min="1"
            max={Math.max(product.stockQuantity || 1, 1)}
            value={quantity}
            onChange={(event) => setQuantity(Math.max(1, Number(event.target.value)))}
            aria-label={`Quantity for ${product.name}`}
          />
          <button onClick={() => onAdd(product, quantity)} disabled={outOfStock}>
            <ShoppingCart size={17} />
            Add
          </button>
          <button className="icon-button danger-button" onClick={() => onDelete(product.id)} title="Delete product">
            <Trash2 size={17} />
          </button>
        </div>
        <small>{cartItem ? `${cartItem.quantity} in cart` : `${product.stockQuantity || 0} available`}</small>
      </div>
    </article>
  );
}

function AuthPanel({ token, user, authMode, setAuthMode, authForm, setAuthForm, onSubmit }) {
  return (
    <section className="panel">
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">Account</p>
          <h2>{token ? "Signed in" : "Sign in"}</h2>
        </div>
        {token ? <span className="badge">{user?.role || "ROLE_USER"}</span> : null}
      </div>

      {token ? (
        <p className="muted">{user?.sub}</p>
      ) : (
        <form onSubmit={onSubmit} className="stack">
          <div className="segmented">
            <button type="button" className={authMode === "login" ? "active" : ""} onClick={() => setAuthMode("login")}>
              <LogIn size={16} />
              Login
            </button>
            <button
              type="button"
              className={authMode === "register" ? "active" : ""}
              onClick={() => setAuthMode("register")}
            >
              <UserPlus size={16} />
              Register
            </button>
          </div>
          <input
            type="email"
            value={authForm.email}
            onChange={(event) => setAuthForm({ ...authForm, email: event.target.value })}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={authForm.password}
            onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })}
            placeholder="Password"
            required
          />
          <button type="submit">{authMode === "login" ? "Login" : "Register"}</button>
        </form>
      )}
    </section>
  );
}

function CartPanel({ cart, products, total, checkout, onCheckout, onConfirm }) {
  return (
    <section className="panel">
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">Basket</p>
          <h2>Cart</h2>
        </div>
        <strong>{formatMoney(total)}</strong>
      </div>
      <div className="stack">
        {cart.items?.map((item) => {
          const product = products.find((entry) => entry.id === item.productId);
          return (
            <div className="line-item" key={item.productId}>
              <span>{product?.name || item.productId}</span>
              <strong>x{item.quantity}</strong>
            </div>
          );
        })}
        {!cart.items?.length ? <p className="empty">Cart is empty.</p> : null}
        <button onClick={onCheckout} disabled={!cart.items?.length}>
          <CreditCard size={17} />
          Checkout
        </button>
        {checkout ? (
          <div className="checkout-box">
            <span>{checkout.razorpayOrderId}</span>
            <strong>{formatMoney((checkout.amount || 0) / 100)}</strong>
            <button onClick={onConfirm}>Confirm mock payment</button>
          </div>
        ) : null}
      </div>
    </section>
  );
}

function OrdersPanel({ orders }) {
  return (
    <section className="panel">
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">History</p>
          <h2>Orders</h2>
        </div>
        <strong>{orders.length}</strong>
      </div>
      <div className="stack">
        {orders.slice(0, 4).map((order) => (
          <div className="order-row" key={order.id}>
            <span>{order.id}</span>
            <strong>{formatMoney(order.totalAmount)}</strong>
          </div>
        ))}
        {!orders.length ? <p className="empty">No orders yet.</p> : null}
      </div>
    </section>
  );
}

function ProductEditor({ form, setForm, onSubmit }) {
  return (
    <section className="panel">
      <div className="section-heading compact">
        <div>
          <p className="eyebrow">Admin</p>
          <h2>Add product</h2>
        </div>
        <PackagePlus size={20} />
      </div>
      <form onSubmit={onSubmit} className="stack">
        <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="Name" required />
        <input
          value={form.category}
          onChange={(event) => setForm({ ...form, category: event.target.value })}
          placeholder="Category"
          required
        />
        <textarea
          value={form.description}
          onChange={(event) => setForm({ ...form, description: event.target.value })}
          placeholder="Description"
          rows="3"
        />
        <div className="form-grid">
          <input
            type="number"
            min="0"
            step="0.01"
            value={form.price}
            onChange={(event) => setForm({ ...form, price: event.target.value })}
            placeholder="Price"
            required
          />
          <input
            type="number"
            min="0"
            value={form.stockQuantity}
            onChange={(event) => setForm({ ...form, stockQuantity: event.target.value })}
            placeholder="Stock"
            required
          />
        </div>
        <textarea
          value={form.imageUrls}
          onChange={(event) => setForm({ ...form, imageUrls: event.target.value })}
          placeholder="Image URLs, one per line"
          rows="3"
        />
        <button type="submit">
          <PackagePlus size={17} />
          Create product
        </button>
      </form>
    </section>
  );
}

function formatMoney(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
  }).format(Number(value || 0));
}

createRoot(document.getElementById("root")).render(<App />);
