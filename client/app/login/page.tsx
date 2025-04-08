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
import { publicApi, authApi } from "@/lib/api";
import axios from "axios";
import { User, Lock, Mail, ArrowRight, UserPlus, RefreshCw, LogIn } from "lucide-react";

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
}

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
      const response: LoginResponse = await authApi.post("/api/v1/login", {
        email: formData.email,
        password: formData.password,
      });

      // 응답에서 토큰 추출 및 저장
      const { accessToken, refreshToken } = response;
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      toast({
        title: "로그인 성공! 🎉",
        description: "환영합니다! 메인 페이지로 이동합니다.",
        variant: "default",
        duration: 500,
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
          duration: 500,
        });
      } else {
        console.error(error);
        toast({
          title: "로그인 실패 😢",
          description: "알 수 없는 오류가 발생했습니다. 다시 시도해주세요.",
          variant: "destructive",
          duration: 500,
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Toaster />
      <Header title="로그인" showBackButton />
      <main className="flex-1 overflow-auto p-4 flex flex-col items-center justify-center">
        <div className="w-full max-w-md space-y-5">
          <div className="text-center mb-6">
            <div className="bg-blue-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-3">
              <User className="h-8 w-8 text-blue-600" />
            </div>
            <h1 className="text-2xl font-bold text-slate-800">로그인</h1>
            <p className="text-slate-500 mt-1">모임통장 서비스를 시작하세요</p>
          </div>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <form onSubmit={handleSubmit}>
              <CardContent className="p-6 space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="email" className="text-slate-700 font-medium block">
                    이메일
                  </Label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Mail className="h-5 w-5 text-slate-400" />
                    </div>
                    <Input
                      id="email"
                      name="email"
                      type="email"
                      placeholder="example@example.com"
                      required
                      value={formData.email}
                      onChange={handleInputChange}
                      className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                    />
                  </div>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="password" className="text-slate-700 font-medium block">
                    비밀번호
                  </Label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Lock className="h-5 w-5 text-slate-400" />
                    </div>
                    <Input
                      id="password"
                      name="password"
                      type="password"
                      required
                      value={formData.password}
                      onChange={handleInputChange}
                      className="pl-10 py-6 rounded-xl border-slate-200 focus:border-blue-400 focus:ring-1 focus:ring-blue-400"
                    />
                  </div>
                </div>
                
                <Button 
                  type="submit" 
                  className="w-full py-6 rounded-xl bg-blue-600 hover:bg-blue-700 transition-colors mt-3"
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <div className="flex items-center justify-center">
                      <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                      로그인 중...
                    </div>
                  ) : (
                    <div className="flex items-center justify-center">
                      <LogIn className="h-5 w-5 mr-2" />
                      로그인하기
                    </div>
                  )}
                </Button>
              </CardContent>
            </form>
          </Card>
          
          <div className="text-center">
            <Button
              variant="link"
              className="text-blue-600 hover:text-blue-800"
              onClick={() => router.push("/signup")}
            >
              <UserPlus className="h-4 w-4 mr-1" />
              계정이 없으신가요? 회원가입하기
            </Button>
          </div>
          
          <div className="bg-blue-50 rounded-xl p-4 border border-blue-100 mt-6">
            <div className="flex">
              <div className="bg-blue-100 p-2 rounded-full mr-3 flex-shrink-0">
                <User className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <h3 className="text-sm font-medium text-blue-800 mb-1">계정 안내</h3>
                <p className="text-xs text-blue-700">
                  로그인하시면 모임통장 서비스의 모든 기능을 이용하실 수 있습니다. 
                  계정이 없으시다면 회원가입을 통해 서비스를 시작하세요.
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
