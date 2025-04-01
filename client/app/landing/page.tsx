// src/components/PrivateRoute.js
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

const PrivateRoute = () => {
  const { isAuthenticated } = useAuth();

  // 로그인이 되어있지 않으면 랜딩 페이지로 리다이렉트
  return isAuthenticated ? <Outlet /> : <Navigate to="/landing" replace />;
};

export default PrivateRoute;

// src/App.js
import { BrowserRouter, Routes, Route } from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute";
import LandingPage from "./pages/LandingPage";
import Dashboard from "./pages/Dashboard";
import Profile from "./pages/Profile";
import Settings from "./pages/Settings";
// 기타 필요한 컴포넌트 import

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 공개 경로 - 인증 필요 없음 */}
        <Route path="/landing" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* 보호된 경로 - 인증 필요 */}
        <Route element={<PrivateRoute />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
          {/* 기타 보호된 경로 */}
        </Route>

        {/* 존재하지 않는 경로 처리 */}
        <Route path="*" element={<Navigate to="/landing" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

// src/contexts/AuthContext.js
import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 여기서 토큰 검증, 사용자 정보 로드 등의 인증 체크를 수행
    const checkAuth = async () => {
      try {
        // 로컬 스토리지나 쿠키에서 토큰 확인
        const token = localStorage.getItem("auth_token");

        if (token) {
          // 토큰 유효성 검사 (예: API 호출)
          const response = await fetch("/api/verify-token", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          if (response.ok) {
            const userData = await response.json();
            setUser(userData);
            setIsAuthenticated(true);
          } else {
            // 토큰이 유효하지 않으면 로그아웃 처리
            localStorage.removeItem("auth_token");
            setIsAuthenticated(false);
            setUser(null);
          }
        } else {
          setIsAuthenticated(false);
          setUser(null);
        }
      } catch (error) {
        console.error("인증 확인 중 오류 발생:", error);
        setIsAuthenticated(false);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  // 로그인 함수
  const login = async (credentials) => {
    // 로그인 로직 구현
    // ...
    setIsAuthenticated(true);
  };

  // 로그아웃 함수
  const logout = () => {
    localStorage.removeItem("auth_token");
    setIsAuthenticated(false);
    setUser(null);
  };

  const value = {
    isAuthenticated,
    user,
    loading,
    login,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};

// src/index.js에 AuthProvider 추가
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { AuthProvider } from "./contexts/AuthContext";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>
);
