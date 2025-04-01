// context/AuthContext.tsx
"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
  useCallback,
} from "react";
import { useRouter, usePathname } from "next/navigation";
import {
  getAccessToken,
  setAccessToken,
  removeAccessToken,
  getRefreshToken,
  setRefreshToken,
  removeRefreshToken,
} from "@/lib/auth";

interface User {
  id: string;
  name: string;
  email: string;
  // 필요한 사용자 정보 추가
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  checkAuth: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  // 보호된 경로 목록
  const protectedPaths = [
    // "/group", "/dashboard", "/profile", "/settings"
    "/asdafsadfgagwerrg",
  ];

  // 현재 경로가 보호된 경로인지 확인
  const isProtectedPath = (path: string) => {
    return protectedPaths.some((protectedPath) =>
      path.startsWith(protectedPath)
    );
  };

  const checkAuth = useCallback(() => {
    const token = getAccessToken();

    if (token) {
      setIsAuthenticated(true);
    } else {
      setIsAuthenticated(false);
      setUser(null);
    }
  }, []);

  // 초기 로드 시 인증 상태 체크
  useEffect(() => {
    checkAuth();
    setLoading(false);
  }, [checkAuth]);

  // 라우트 보호
  useEffect(() => {
    const token = getAccessToken();

    if (isProtectedPath(pathname) && !token) {
      router.push("/login");
    } else if (
      (pathname === "/login" ||
        pathname === "/signup" ||
        pathname === "/forgot-password") &&
      token
    ) {
      router.push("/");
    }
  }, [pathname, router]);

  const login = async (email: string, password: string) => {
    try {
      // 로그인 API 호출
      const response = await fetch("/api/v1/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });
      if (response.ok) {
        const data = await response.json();

        // 토큰 저장
        setAccessToken(data.accessToken);
        setRefreshToken(data.refreshToken);

        // 인증 상태 체크
        checkAuth();

        // 홈 페이지로 리다이렉트
        router.push("/");
      } else {
        throw new Error("로그인 실패");
      }
    } catch (error) {
      console.error("로그인 오류:", error);
      throw error;
    }
  };

  const logout = () => {
    // 토큰 제거
    removeAccessToken();
    removeRefreshToken();

    // 사용자 데이터 제거
    localStorage.removeItem("user_data");

    // 상태 직접 업데이트
    setUser(null);
    setIsAuthenticated(false);

    // 로그인 페이지로 리다이렉트
    router.push("/login");
  };

  // API 요청 시 토큰을 헤더에 포함시키는 함수
  const getAuthHeaders = () => {
    const token = getAccessToken();
    return {
      "Content-Type": "application/json",
      Authorization: token ? `Bearer ${token}` : "",
    };
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    logout,
    checkAuth,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
