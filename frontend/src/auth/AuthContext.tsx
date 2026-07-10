import { createContext, useContext, useState, type ReactNode } from "react";
import { login as apiLogin, register as apiRegister } from "../api/client";
import type { UserRole } from "../types";

interface AuthState {
  username: string | null;
  role: UserRole | null;
  token: string | null;
  register: (username: string, password: string, role: UserRole) => Promise<void>;
  login: (username: string, password: string) => Promise<void>;
  signOut: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState<string | null>(() => localStorage.getItem("auth_username"));
  const [role, setRole] = useState<UserRole | null>(() => localStorage.getItem("auth_role") as UserRole | null);
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("auth_token"));

  function persist(response: { token: string; username: string; role: UserRole }) {
    setUsername(response.username);
    setRole(response.role);
    setToken(response.token);
    localStorage.setItem("auth_username", response.username);
    localStorage.setItem("auth_role", response.role);
    localStorage.setItem("auth_token", response.token);
  }

  async function register(inputUsername: string, password: string, inputRole: UserRole) {
    const response = await apiRegister(inputUsername, password, inputRole);
    persist(response);
  }

  async function login(inputUsername: string, password: string) {
    const response = await apiLogin(inputUsername, password);
    persist(response);
  }

  function signOut() {
    setUsername(null);
    setRole(null);
    setToken(null);
    localStorage.removeItem("auth_username");
    localStorage.removeItem("auth_role");
    localStorage.removeItem("auth_token");
  }

  return (
    <AuthContext.Provider value={{ username, role, token, register, login, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthState {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside an AuthProvider");
  }
  return context;
}
