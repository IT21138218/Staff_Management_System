import { createContext, useContext, useState, useCallback } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

function loadStoredUser() {
  try {
    const raw = localStorage.getItem('sms_user');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function persistSession(data) {
  const loggedInUser = {
    username: data.username,
    email: data.email,
    role: data.role,
    employeeId: data.employeeId,
    fullName: data.fullName,
    photoUrl: data.photoUrl || null,
  };
  localStorage.setItem('sms_token', data.token);
  localStorage.setItem('sms_user', JSON.stringify(loggedInUser));
  return loggedInUser;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser);

  const login = useCallback(async (username, password) => {
    const data = await authApi.login(username, password);
    const loggedInUser = persistSession(data);
    setUser(loggedInUser);
    return loggedInUser;
  }, []);

  const register = useCallback(async (form) => {
    const data = await authApi.register(form);
    const loggedInUser = persistSession(data);
    setUser(loggedInUser);
    return loggedInUser;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('sms_token');
    localStorage.removeItem('sms_user');
    setUser(null);
  }, []);

  const updatePhotoUrl = useCallback((photoUrl) => {
    setUser((prev) => {
      if (!prev) return prev;
      const next = { ...prev, photoUrl };
      localStorage.setItem('sms_user', JSON.stringify(next));
      return next;
    });
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, register, logout, updatePhotoUrl, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}
