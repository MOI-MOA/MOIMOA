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
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  const checkAuth = useCallback(() => {
    const token = getAccessToken();
    setIsAuthenticated(!!token);

    // 공개 경로 (로그인, 회원가입)
    const publicPaths = ["/login", "/signup"];
    const isPublicPath = publicPaths.includes(pathname);

    // 이미 인증된 사용자가 로그인/회원가입 페이지에 접근하려면 메인 페이지로 리다이렉트
    if (token && isPublicPath) {
      router.push("/");
    }
  }, [pathname, router]);

  useEffect(() => {
    checkAuth();
    setLoading(false);
  }, [checkAuth]);

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

        // 사용자 정보 저장
        localStorage.setItem("user_data", JSON.stringify(data.user));

        // 사용자 정보 설정
        setUser(data.user);
        setIsAuthenticated(true);

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

    // 상태 업데이트
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
