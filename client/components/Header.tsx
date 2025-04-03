"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft, LogOut } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/app/context/AuthContext";
import { useEffect } from "react";
import { getAccessToken } from "@/lib/auth";

interface HeaderProps {
  title?: string;
  showBackButton?: boolean;
}

export function Header({ title, showBackButton = false }: HeaderProps) {
  const router = useRouter();
  const { isAuthenticated, logout, checkAuth } = useAuth();

  useEffect(() => {
    // 페이지 로드 시 토큰 확인
    const token = getAccessToken();
    if (token) {
      checkAuth();
    }
  }, [checkAuth]);

  return (
    <header className="flex justify-between items-center p-4 bg-white border-b">
      <div className="flex items-center">
        {showBackButton && (
          <Button variant="ghost" onClick={() => router.back()}>
            <ArrowLeft className="h-5 w-5" />
          </Button>
        )}
        {!showBackButton && (
          <svg
            className="h-8 w-8 text-blue-500"
            fill="none"
            height="24"
            stroke="currentColor"
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        )}
        {title && <h1 className="text-xl font-semibold ml-2"></h1>}
      </div>
      <div className="absolute left-1/2 transform -translate-x-1/2">
        <h1
          className="text-2xl font-bold text-blue-500 cursor-pointer hover:text-blue-600 transition-colors"
          onClick={() => router.push("/")}
        >
          Toss
        </h1>
      </div>
      {isAuthenticated && (
        <Button
          variant="ghost"
          size="icon"
          onClick={logout}
          className="ml-auto"
        >
          <LogOut className="h-5 w-5" />
        </Button>
      )}
    </header>
  );
}
