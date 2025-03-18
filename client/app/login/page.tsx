"use client";

import type React from "react";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { toast } from "@/components/ui/use-toast";
import { Toaster } from "@/components/ui/toaster";
import axios from "axios";

export default function LoginPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await axios.post("/api/auth/login", {
        email: formData.email,
        password: formData.password,
      });

      // 응답에서 토큰 추출 및 저장
      const { accessToken, refreshToken } = response.data;
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      toast({
        title: "로그인 성공! 🎉",
        description: "환영합니다! 메인 페이지로 이동합니다.",
        variant: "default",
        duration: 1500,
      });

      setTimeout(() => {
        router.push("/");
      }, 1500);
    } catch (error) {
      // 폼 데이터 초기화
      setFormData({
        email: "",
        password: "",
      });

      if (axios.isAxiosError(error)) {
        toast({
          title: "로그인 실패 😢",
          description:
            error.response?.data?.message ||
            "이메일 또는 비밀번호가 올바르지 않습니다.",
          variant: "destructive",
          duration: 3000,
        });
      } else {
        toast({
          title: "로그인 실패 😢",
          description: "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
          duration: 3000,
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Toaster />
      <Header title="로그인" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <form onSubmit={handleSubmit}>
            <CardHeader>
              <CardTitle>로그인</CardTitle>
              <CardDescription>
                계정에 로그인하여 서비스를 이용하세요.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">이메일</Label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="example@example.com"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">비밀번호</Label>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  required
                  value={formData.password}
                  onChange={handleInputChange}
                />
              </div>
            </CardContent>
            <CardFooter className="flex-col space-y-2">
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "로그인 중..." : "로그인"}
              </Button>
              <Button
                variant="link"
                className="w-full"
                onClick={() => router.push("/signup")}
              >
                계정이 없으신가요? 회원가입
              </Button>
            </CardFooter>
          </form>
        </Card>
      </main>
    </div>
  );
}
