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
          <img
            src="/우리어플 아이콘.png"
            alt="Toss"
            className="h-16 w-16 cursor-pointer hover:opacity-80 transition-opacity"
            onClick={() => router.push("/")}
          />
        )}
        {title && <h1 className="text-xl font-semibold ml-2"></h1>}
      </div>
      {isAuthenticated && (
        <Button
          variant="ghost"
          size="icon"
          onClick={logout}
          className="ml-auto h-12 w-12"
        >
          <LogOut className="h-12 w-12" />
        </Button>
      )}
    </header>
  );
}
