import { useState, type FormEvent } from "react";
import { useAuth } from "../auth/AuthContext";
import type { ApiError, UserRole } from "../types";

type Mode = "login" | "register";

export default function SignInBar() {
  const { username, role, token, register, login, signOut } = useAuth();
  const [mode, setMode] = useState<Mode>("login");
  const [inputUsername, setInputUsername] = useState("");
  const [password, setPassword] = useState("");
  const [newAccountRole, setNewAccountRole] = useState<UserRole>("USER");
  const [error, setError] = useState<string | null>(null);
  const [showToken, setShowToken] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    try {
      if (mode === "login") {
        await login(inputUsername, password);
      } else {
        await register(inputUsername, password, newAccountRole);
      }
      setError(null);
      setPassword("");
    } catch (err) {
      setError((err as ApiError).message ?? "Something went wrong");
    }
  }

  if (token && username) {
    return (
      <div className="signin-bar">
        <span>
          Signed in as <strong>{username}</strong> ({role})
        </span>
        <button className="btn btn-small" onClick={() => setShowToken((v) => !v)}>
          {showToken ? "Hide token" : "Show token"}
        </button>
        <button className="btn btn-small" onClick={signOut}>
          Sign out
        </button>
        {showToken && <code className="token-preview">{token}</code>}
      </div>
    );
  }

  return (
    <form className="signin-bar" onSubmit={handleSubmit}>
      <div className="mode-toggle">
        <button type="button" className="btn btn-small" disabled={mode === "login"} onClick={() => setMode("login")}>
          Log in
        </button>
        <button
          type="button"
          className="btn btn-small"
          disabled={mode === "register"}
          onClick={() => setMode("register")}
        >
          Register
        </button>
      </div>

      <input
        className="input"
        placeholder="Username"
        value={inputUsername}
        onChange={(e) => setInputUsername(e.target.value)}
        required
      />
      <input
        className="input"
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />

      {mode === "register" && (
        <select
          className="input"
          value={newAccountRole}
          onChange={(e) => setNewAccountRole(e.target.value as UserRole)}
        >
          <option value="USER">USER (can book seats)</option>
          <option value="ADMIN">ADMIN (can manage events)</option>
        </select>
      )}

      <button type="submit" className="btn btn-primary btn-small">
        {mode === "login" ? "Log in" : "Create account"}
      </button>
      {error && <span className="message error">{error}</span>}
    </form>
  );
}
